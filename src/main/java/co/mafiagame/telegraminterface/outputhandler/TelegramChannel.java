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
import co.mafiagame.telegram.api.domain.TMessage;
import co.mafiagame.telegram.api.domain.TReplyKeyboardMarkup;
import co.mafiagame.telegraminterface.RoomContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import co.mafiagame.telegraminterface.message.MessageHolder;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Set;
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

    @PostConstruct
    private void init() {
        this.url = telegramUrl + telegramToken + "/sendMessage";
    }

    @Override
    public void send(ResultMessage resultMessage) {
        if (resultMessage.getChannelType() == ChannelType.NONE)
            return;

        TelegramInterfaceContext ic = (TelegramInterfaceContext) resultMessage.getIc();
        for (Message msg : resultMessage.getMessages()) {
            try {
                sendMessage(msg, resultMessage.getChannelType(), ic);
            } catch (Exception e) {
                logger.error("error sending message {} to interfaceContext {}", msg, ic, e);
            }
        }

    }

    public SendMessageResult send(SendMessage message) {
        for (int i = 0; i < 2; i++) {
            try {
                return restTemplate.postForObject(url, message, SendMessageResult.class);
            } catch (Exception e) {
                logger.warn("can't send message " + url, e);
                logger.info("wait 1 second for next try...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e.getMessage(), e1);
                }
            }
        }
        throw new RuntimeException("can't send message after 2 try " + url);
    }

    public void sendMessage(Message msg, ChannelType channelType, TelegramInterfaceContext ic) throws Exception {
        Integer chatId = null;
        if (channelType == ChannelType.GENERAL)
            chatId = ic.getIntRoomId() == null ? Integer.valueOf(msg.getReceiverId()) : ic.getIntRoomId();
        if (channelType == ChannelType.USER_PRIVATE)
            chatId = Integer.valueOf(msg.getReceiverId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
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
        SendMessageResult sendMessageResult = send(sendMessage);
        if (!sendMessageResult.isOk())
            logger.error(
                    "telegram failed to send message {} to chatId {} with errorCode {}: {}",
                    msgStr, chatId, sendMessageResult.getErrorCode(), sendMessageResult.getDescription());
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
    }

}
