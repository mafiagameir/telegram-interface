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
import co.mafiagame.telegraminterface.RoomContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
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
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${mafia.telegram.api.url}")
    private String telegramUrl;
    @Value("${mafia.telegram.token}")
    private String telegramToken;
    @Autowired
    private RoomContainer roomContainer;
    private String url;
    private final BlockingQueue<SendMessage> outQueue = new LinkedBlockingQueue<>();

    @PostConstruct
    private void init() {
        this.url = telegramUrl + telegramToken + "/sendMessage";
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return !response.getStatusCode().equals(HttpStatus.OK)
                        && IOUtils.toString(response.getBody()).contains("PEER_ID_INVALID");
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                throw new BotHasNotAccessException();
                //logger.error(response.getStatusCode().toString() + ":" + response.getStatusText() + ":" + response.getBody());
                //throw new CouldNotSendMessageException();
            }
        });
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                SendMessage sendMessage = null;
                try {
                    if (!outQueue.isEmpty()) {
                        sendMessage = outQueue.take();
                        logger.info("deliver {}", sendMessage);
                        Map<String, Object> params = sendMessage.toMap();
                        logger.info("deliver param {}", params);
                        ResponseEntity<SendMessageResult> sendMessageResultResponseEntity =
                                restTemplate.postForEntity(url, sendMessage, SendMessageResult.class, params);
                        logger.info("result:{}", sendMessageResultResponseEntity);
                        SendMessageResult sendMessageResult = sendMessageResultResponseEntity.getBody();
                        if (!sendMessageResult.isOk()) {
                            logger.error("could not send message: {}", sendMessageResult);
                            throw new CouldNotSendMessageException();
                        }
                    }
                } catch (RestClientException e) {
                    logger.error("error in sending message: " + e.getMessage(), e);
                } catch (InterruptedException e) {
                    logger.error("error in reading outQueue", e);
                } catch (Exception e) {
                    logger.error("error sending message " + sendMessage, e);
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, TimeUnit.MINUTES.toMillis(1), TimeUnit.SECONDS.toMillis(1));
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

    public void sendMessage(Message msg, ChannelType channelType, TelegramInterfaceContext ic) {
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
                mentions = String.join(" ", msg.getToUsers().stream().map(u -> "@" + u).collect(Collectors.toList()));
            replyKeyboardMarkup.addOptions(msg.getOptions());
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        String msgStr = mentions + "\n" + MessageHolder.get(msg.getMessageCode(), msg.getArgs());
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
