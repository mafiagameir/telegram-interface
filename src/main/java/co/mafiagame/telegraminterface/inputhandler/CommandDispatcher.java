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

package co.mafiagame.telegraminterface.inputhandler;

import co.mafiagame.commands.TelegramCommandHandler;
import co.mafiagame.common.Constants;
import co.mafiagame.engine.api.GameApi;
import co.mafiagame.telegram.api.domain.TUpdate;
import co.mafiagame.telegraminterface.LangContainer;
import co.mafiagame.telegraminterface.RoomContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * @author hekmatof
 */
@Component
public class CommandDispatcher {
    @Autowired
    private GameApi gameApi;
    @Autowired
    private RoomContainer roomContainer;
    @Autowired
    private LangContainer langContainer;

    private Map<String, TelegramCommandHandler> commandHandlers = new HashMap<>();

    public void registerCommandHandler(String command, TelegramCommandHandler handler) {
        commandHandlers.put(command, handler);
    }

    public void handle(TUpdate update) {
        Long roomId = roomContainer.getRoomId(update.getMessage().getFrom().getUsername());
        TelegramInterfaceContext ic = new TelegramInterfaceContext(
                roomId,
                update.getMessage().getFrom(),
                update.getMessage().getChat(),
                langContainer.getLang(roomId, String.valueOf(update.getMessage().getFrom().getId())));
        String msg = update.getMessage().getText();
        if (msg == null)
            return;
        try {
            if (msg.startsWith("/")) {
                String command = getCommand(msg);
                msg = msg.substring(command.length() + 1).trim();
                String[] args = msg.split(" ");
                commandHandlers.get(command).execute(ic, update.getMessage().getFrom(), args);
            }
        } catch (Exception e) {
            gameApi.commandNotFound(ic);
        }
    }

    private String getCommand(String message) throws NoSuchElementException {
        String pureMessage = message.substring(1).toLowerCase();
        return Stream.of(Constants.CMD.START_FINAL_ELECTION,
                Constants.CMD.START_ELECTION,
                Constants.CMD.REGISTER,
                Constants.CMD.VOTE,
                Constants.CMD.REGISTER,
                Constants.CMD.CANCEL,
                Constants.CMD.DETECTIVE_ASK,
                Constants.CMD.KILL_ME,
                Constants.CMD.DOCTOR_HEAL,
                Constants.CMD.MAFIA_VOTE,
                Constants.CMD.START_STASHED_GAME,
                Constants.CMD.WHO_IS_PLAYING,
                Constants.CMD.HELP,
                Constants.CMD.WHAT_IS_MY_ROLE,
                Constants.CMD.LANG
        ).filter(pureMessage::startsWith).findFirst().get();
    }
}
