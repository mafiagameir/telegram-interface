/*
 *  Copyright (C) 2015 mafiagame.ir
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package co.mafiagame.telegraminterface.inputhandler.handler;

import co.mafiagame.common.channel.InterfaceChannel;
import co.mafiagame.common.domain.result.Message;
import co.mafiagame.common.domain.result.ResultMessage;
import co.mafiagame.engine.api.GameApi;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import co.mafiagame.telegraminterface.inputhandler.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author Esa Hekmatizadeh
 */
public abstract class TelegramCommandHandler {
    @Autowired
    protected CommandDispatcher commandDispatcher;
    @Autowired
    protected InterfaceChannel interfaceChannel;
    @Autowired
    protected GameApi gameApi;

    @PostConstruct
    protected final void init() {
        commandDispatcher.registerCommandHandler(getCommandString(), this);
    }

    protected abstract String getCommandString();

    public abstract void execute(TelegramInterfaceContext ic, String[] args);

    boolean validateUsername(TelegramInterfaceContext ic) {
        String username = ic.getUserName();
        if (username == null || username.equals("null")) {
            ic.setRoomId(ic.getUserIdInt());
            interfaceChannel.send(
                    new ResultMessage(
                            new Message("username.must.be.defined").setReceiverId(ic.getUserId()),
                            ic.getSenderType(), ic));
            return false;
        }
        return true;
    }
}
