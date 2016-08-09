package co.mafiagame.telegraminterface.outputhandler.handler;

import co.mafiagame.common.domain.result.ChannelType;
import co.mafiagame.common.domain.result.Message;
import co.mafiagame.common.utils.MessageHolder;
import co.mafiagame.telegram.api.domain.TReplyKeyboardMarkup;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import co.mafiagame.telegraminterface.outputhandler.SendMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Esa Hekmatizadeh
 */
@Component
public class DefaultOutputMessageHandler extends OutputMessageHandler {

    @Override
    public void execute(Message msg, ChannelType channelType, TelegramInterfaceContext ic) {
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
                langContainer.getLang(ic.getIntRoomId(), ic.getUserId()), msg.getArgs());
        sendMessage.setText(msgStr);
        telegramChannel.queue(sendMessage);
    }

    @Override
    protected Collection<String> messages() {
        return new ArrayList<>();
    }
}
