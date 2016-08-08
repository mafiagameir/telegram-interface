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

package co.mafiagame.commands;

import co.mafiagame.common.Constants;
import co.mafiagame.telegram.api.domain.TChat;
import co.mafiagame.telegraminterface.RoomContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Esa Hekmatizadeh
 */
@Component
public class KillMeCommandHandler extends TelegramCommandHandler {
    @Autowired
    private RoomContainer roomContainer;

    @Override
    protected String getCommandString() {
        return Constants.CMD.KILL_ME;
    }

    @Override
    public void execute(TelegramInterfaceContext ic, TChat user, String[] args) {
        gameApi.whatIsMyRole(ic);
        roomContainer.remove(user.getUsername());
    }
}
