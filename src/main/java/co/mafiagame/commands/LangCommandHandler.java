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
import co.mafiagame.common.domain.result.Message;
import co.mafiagame.common.domain.result.ResultMessage;
import co.mafiagame.common.utils.MessageHolder;
import co.mafiagame.persistence.api.PersistenceApi;
import co.mafiagame.telegram.api.domain.TChat;
import co.mafiagame.telegraminterface.LangContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Esa Hekmatizadeh
 */
@Component
public class LangCommandHandler extends TelegramCommandHandler {
    @Autowired
    private LangContainer langContainer;
    @Autowired
    private PersistenceApi persistenceApi;

    @Override
    protected String getCommandString() {
        return Constants.CMD.LANG;
    }

    @Override
    public void execute(TelegramInterfaceContext ic, TChat user, String[] args) {
        setLang(args, ic);
        validateUsername(ic);
    }

    private void setLang(String[] args, TelegramInterfaceContext ic) {
        if (args.length < 1) {
            interfaceChannel.send(new ResultMessage(
                    new Message("language.command.need.parameter")
                            .setReceiverId(ic.getUserId()),
                    ic.getSenderType(), ic));
        } else {
            MessageHolder.Lang lang = MessageHolder.Lang.valueOf(args[0].toUpperCase());
            if (Objects.isNull(lang))
                interfaceChannel.send(new ResultMessage(
                        new Message("language.command.need.parameter")
                                .setReceiverId(ic.getUserId()),
                        ic.getSenderType(), ic));
            else {
                langContainer.put(ic.getIntRoomId(), lang);
                ic.setLang(lang);
                persistenceApi.setLang(ic.getUserId(), lang);
                interfaceChannel.send(new ResultMessage(
                        new Message("language.changed")
                                .setReceiverId(ic.getUserId())
                                .setArgs(lang.lang()),
                        ic.getSenderType(), ic));
            }
        }
    }
}
