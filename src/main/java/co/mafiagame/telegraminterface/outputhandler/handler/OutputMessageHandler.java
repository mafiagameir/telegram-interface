package co.mafiagame.telegraminterface.outputhandler.handler;

import co.mafiagame.common.domain.result.ChannelType;
import co.mafiagame.common.domain.result.Message;
import co.mafiagame.telegraminterface.LangContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import co.mafiagame.telegraminterface.outputhandler.TelegramChannel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * @author Esa Hekmatizadeh
 */
public abstract class OutputMessageHandler {
    @Autowired
    protected LangContainer langContainer;
    @Autowired
    protected TelegramChannel telegramChannel;

    @PostConstruct
    private void init() {
        messages().forEach((msgKey) -> telegramChannel.registerOutputHandler(msgKey, this));
    }

    public abstract void execute(Message msg, ChannelType channelType, TelegramInterfaceContext ic);

    protected abstract Collection<String> messages();
}
