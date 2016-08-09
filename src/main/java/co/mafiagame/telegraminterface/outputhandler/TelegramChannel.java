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
import co.mafiagame.exception.CouldNotSendMessageException;
import co.mafiagame.telegram.api.domain.TMessage;
import co.mafiagame.telegraminterface.RoomContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import co.mafiagame.telegraminterface.outputhandler.handler.DefaultOutputMessageHandler;
import co.mafiagame.telegraminterface.outputhandler.handler.OutputMessageHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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
    private final Map<String, OutputMessageHandler> handlers = new TreeMap<>();
    @Autowired
    private DefaultOutputMessageHandler defaultOutputMessageHandler;


    public void registerOutputHandler(String msgKey, OutputMessageHandler handler) {
        handlers.put(msgKey, handler);
    }

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
                        deliverMessage(sendMessage);
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

    private void deliverMessage(SendMessage sendMessage) throws IOException {
        logger.info("deliver {}", sendMessage);
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json; charset=UTF-8");
        StringEntity entity = new StringEntity(
                objectMapper.writeValueAsString(sendMessage),
                ContentType.create("application/json", "UTF-8"));
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        SendMessageResult result = objectMapper.readValue(response.getEntity().getContent(),
                SendMessageResult.class);
        post.releaseConnection();
        if (!result.isOk()) {
            logger.error("could not deliver message: {} - {}",
                    result.getErrorCode(), result.getDescription());
            throw new CouldNotSendMessageException();
        }
    }

    @Override
    public void send(ResultMessage resultMessage) {
        if (resultMessage.getChannelType() == ChannelType.NONE)
            return;
        TelegramInterfaceContext ic = (TelegramInterfaceContext) resultMessage.getIc();
        for (Message msg : resultMessage.getMessages()) {
            OutputMessageHandler outputMessageHandler = handlers.get(msg.getMessageCode());
            if (Objects.isNull(outputMessageHandler))
                outputMessageHandler = defaultOutputMessageHandler;
            outputMessageHandler.execute(msg, resultMessage.getChannelType(), ic);

        }

//        sendMessage(msg, resultMessage.getChannelType(), ic);
    }

    private void sendMessage(Message msg, ChannelType channelType, TelegramInterfaceContext ic) {

    }

    @Override
    public void gameOver(Set<String> usernames) {
        usernames.forEach(roomContainer::remove);
    }

    public void queue(SendMessage sendMessage) {
        try {
            outQueue.put(sendMessage);
        } catch (InterruptedException e) {
            logger.error("could not put into outQueue: " + sendMessage, e);
        }
    }


    private static class SendMessageResult {
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
