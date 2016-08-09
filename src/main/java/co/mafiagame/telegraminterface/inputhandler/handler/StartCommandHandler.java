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

import co.mafiagame.common.Constants;
import co.mafiagame.common.domain.result.Message;
import co.mafiagame.common.domain.result.ResultMessage;
import co.mafiagame.telegraminterface.RoomContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Esa Hekmatizadeh
 */
@Component
public class StartCommandHandler extends TelegramCommandHandler {
    @Autowired
    private RoomContainer roomContainer;

    @Override
    protected String getCommandString() {
        return Constants.CMD.START_STASHED_GAME;
    }

    @Override
    public void execute(TelegramInterfaceContext ic, String[] args) {
        roomContainer.put(ic.getUserName(), ic.getIntRoomId());
        if (args.length < 4) {
            interfaceChannel.send(
                    new ResultMessage(new Message("welcome.message")
                            .setReceiverId(ic.getUserId()),
                            ic.getSenderType(), ic));
            validateUsername(ic);
        } else {
            if (!validateUsername(ic))
                return;
            gameApi.startStashedGame(ic, Integer.valueOf(args[0]), Integer.valueOf(args[1]),
                    Integer.valueOf(args[2]), Integer.valueOf(args[3]));
        }
    }
}
