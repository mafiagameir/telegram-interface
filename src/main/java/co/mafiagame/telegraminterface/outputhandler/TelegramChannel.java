/*
 * Copyright (C) 2015 mafiagame.ir
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package co.mafiagame.telegraminterface.outputhandler;

import co.mafiagame.common.channel.InterfaceChannel;
import co.mafiagame.common.domain.result.ChannelType;
import co.mafiagame.common.domain.result.Message;
import co.mafiagame.common.domain.result.ResultMessage;
import co.mafiagame.common.utils.MessageHolder;
import co.mafiagame.exception.BotHasNotAccessException;
import co.mafiagame.exception.CouldNotSendMessageException;
import co.mafiagame.telegram.api.domain.TMessage;
import co.mafiagame.telegram.api.domain.TReplyKeyboardMarkup;
import co.mafiagame.telegraminterface.LangContainer;
import co.mafiagame.telegraminterface.RoomContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hekmatof
 */
@Component
public class TelegramChannel implements InterfaceChannel {
    private static final Logger logger = LoggerFactory.getLogger(TelegramChannel.class);
    @Value("${mafia.telegram.api.url}")
    private String telegramUrl;
    @Value("${mafia.telegram.token}")
    private String telegramToken;
    @Autowired
    private RoomContainer roomContainer;
    private String url;
    private final BlockingQueue<SendMessage> outQueue = new LinkedBlockingQueue<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static HttpClient client;
    @Autowired
    private LangContainer langContainer;

    @PostConstruct
    private void init() {
        client = HttpClients.createDefault();
        this.url = telegramUrl + telegramToken + "/sendMessage";
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SendMessage sendMessage = null;
                try {
                    if (!outQueue.isEmpty()) {
                        sendMessage = outQueue.take();
                        logger.info("deliver {}", sendMessage);
                        HttpPost post = new HttpPost(url);
                        post.setHeader("Content-Type", "application/json; charset=UTF-8");
                        StringEntity entity = new StringEntity(
                                objectMapper.writeValueAsString(sendMessage),
                                ContentType.create("application/json", "UTF-8"));
                        post.setEntity(entity);
                        HttpResponse response = client.execute(post);
                        if (response.getStatusLine().getStatusCode() != 200) {
                            if (IOUtils.toString(response.getEntity().getContent())
                                    .contains("PEER_ID_INVALID"))
                                throw new BotHasNotAccessException();
                            logger.error("could not deliver message: {}",
                                    response.getStatusLine());
                            post.releaseConnection();
                            throw new CouldNotSendMessageException();
                        }
                        post.releaseConnection();
                    }
                } catch (InterruptedException e) {
                    logger.error("error in reading outQueue", e);
                } catch (Exception e) {
                    logger.error("error sending message " + sendMessage, e);
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, TimeUnit.MINUTES.toMillis(1), TimeUnit.MILLISECONDS.toMillis(200));
    }

    @Override
    public void send(ResultMessage resultMessage) {
        if (resultMessage.getChannelType() == ChannelType.NONE)
            return;
        TelegramInterfaceContext ic = (TelegramInterfaceContext) resultMessage.getIc();
        for (Message msg : resultMessage.getMessages()) {
            sendMessage(msg, resultMessage.getChannelType(), ic);
        }
    }

    private void sendMessage(Message msg, ChannelType channelType, TelegramInterfaceContext ic) {
        SendMessage sendMessage = new SendMessage();
        if (channelType == ChannelType.GENERAL)
            sendMessage.setChatId(ic.getIntRoomId());
        if (channelType == ChannelType.USER_PRIVATE)
            sendMessage.setChatId(Long.valueOf(msg.getReceiverId()));
        String mentions = "";
        if (msg.getOptions().size() > 0) {
            TReplyKeyboardMarkup replyKeyboardMarkup = new TReplyKeyboardMarkup();
            if (msg.getToUsers() == null)
                replyKeyboardMarkup.setSelective(false);
            else
                mentions = String.join(" ", msg.getToUsers().stream()
                        .map(u -> "@" + u).collect(Collectors.toList()));
            replyKeyboardMarkup.addOptions(msg.getOptions());
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        String msgStr = mentions + "\n" + MessageHolder.get(msg.getMessageCode(),
                langContainer.getLang(ic.getIntRoomId(),ic.getUserId()), msg.getArgs());
        sendMessage.setText(msgStr);
        try {
            outQueue.put(sendMessage);
        } catch (InterruptedException e) {
            logger.error("could not put into outQueue: " + sendMessage, e);
        }
    }

    @Override
    public void gameOver(Set<String> usernames) {
        usernames.forEach(roomContainer::remove);
    }

    public static class SendMessageResult {
        private boolean ok;
        @JsonProperty("error_code")
        private Integer errorCode;
        private String description;
        private TMessage result;

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public TMessage getResult() {
            return result;
        }

        public void setResult(TMessage result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return "SendMessageResult{" +
                    "ok=" + ok +
                    ", errorCode=" + errorCode +
                    ", description='" + description + '\'' +
                    ", result=" + result +
                    '}';
        }
    }
}
