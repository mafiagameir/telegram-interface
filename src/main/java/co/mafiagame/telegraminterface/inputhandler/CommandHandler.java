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

import co.mafiagame.common.Constants;
import co.mafiagame.common.channel.InterfaceChannel;
import co.mafiagame.common.domain.result.Message;
import co.mafiagame.common.domain.result.ResultMessage;
import co.mafiagame.common.utils.MessageHolder;
import co.mafiagame.engine.api.GameApi;
import co.mafiagame.persistence.api.PersistenceApi;
import co.mafiagame.telegram.api.domain.TChat;
import co.mafiagame.telegram.api.domain.TUpdate;
import co.mafiagame.telegraminterface.LangContainer;
import co.mafiagame.telegraminterface.RoomContainer;
import co.mafiagame.telegraminterface.TelegramInterfaceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author hekmatof
 */
@Component
public class CommandHandler {
    @Autowired
    private GameApi gameApi;
    @Autowired
    private InterfaceChannel interfaceChannel;
    @Autowired
    private RoomContainer roomContainer;
    @Autowired
    private LangContainer langContainer;
    @Autowired
    private PersistenceApi persistenceApi;

    public void handle(TUpdate update) {
        Long roomId = roomContainer.getRoomId(update.getMessage().getFrom().getUsername());
        TelegramInterfaceContext ic = new TelegramInterfaceContext(
                roomId,
                update.getMessage().getFrom(),
                update.getMessage().getChat(),
                langContainer.getLang(roomId, String.valueOf(update.getMessage().getFrom().getId())));

        if (!validateUsername(update, ic))
            return;
        String msg = update.getMessage().getText();
        if (msg == null)
            return;
        try {
            if (msg.startsWith("/")) {
                String command = getCommand(msg);
                msg = msg.substring(command.length() + 1).trim();
                String[] args = msg.split(" ");
                handle(ic,
                        update.getMessage().getFrom(), command, args);
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
                Constants.CMD.DETECTOR_ASK,
                Constants.CMD.DOCTOR_HEAL,
                Constants.CMD.MAFIA_VOTE,
                Constants.CMD.START_STASHED_GAME,
                Constants.CMD.WHO_IS_PLAYING,
                Constants.CMD.HELP,
                Constants.CMD.WHAT_IS_MY_ROLE,
                Constants.CMD.KILL_ME,
                Constants.CMD.LANG
        ).filter(pureMessage::startsWith).findFirst().get();
    }

    private boolean validateUsername(TUpdate update, TelegramInterfaceContext ic) {
        String username = update.getMessage().getFrom().getUsername();
        if (username == null || username.equals("null")) {
            interfaceChannel.send(
                    new ResultMessage(
                            new Message("username.must.be.defined", String.valueOf(update.getMessage().getFrom().getId()), ""),
                            ic.getSenderType(), ic));
            return false;
        }
        return true;
    }

    private void handle(TelegramInterfaceContext ic, TChat user, String command, String[] args) {
        switch (command) {
            case Constants.CMD.START_STASHED_GAME:
                roomContainer.put(user.getUsername(), ic.getIntRoomId());
                if (args.length < 4) {
                    interfaceChannel.send(
                            new ResultMessage(new Message("welcome.message", ic.getUserId(), ic.getUserName()),
                                    ic.getSenderType(), ic));
                } else {
                    gameApi.startStashedGame(ic, Integer.valueOf(args[0]), Integer.valueOf(args[1]),
                            Integer.valueOf(args[2]), Integer.valueOf(args[3]));
                }
                break;
            case Constants.CMD.REGISTER:
                roomContainer.put(user.getUsername(), ic.getIntRoomId());
                gameApi.register(ic, user.getUsername(), user.getFirstName(), user.getLastName());
                break;
            case Constants.CMD.VOTE:
                gameApi.vote(ic, user.getUsername(), Arrays.asList(args));
                break;
            case Constants.CMD.START_ELECTION:
                gameApi.startElection(ic);
                break;
            case Constants.CMD.START_FINAL_ELECTION:
                gameApi.startFinalElection(ic);
                break;
            case Constants.CMD.WHO_IS_PLAYING:
                gameApi.whoIsPlaying(ic);
                break;
            case Constants.CMD.WHAT_IS_MY_ROLE:
                gameApi.whatIsMyRole(ic);
                break;
            case Constants.CMD.KILL_ME:
                gameApi.whatIsMyRole(ic);
                roomContainer.remove(user.getUsername());
                break;
            case Constants.CMD.MAFIA_VOTE:
                gameApi.mafiaKillVote(ic, user.getUsername(), args[0]);
                break;
            case Constants.CMD.DETECTOR_ASK:
                gameApi.detectorAsk(ic, user.getUsername(), args[0]);
                break;
            case Constants.CMD.DOCTOR_HEAL:
                gameApi.doctorHeal(ic, user.getUsername(), args[0]);
                break;
            case Constants.CMD.HELP:
                gameApi.help(ic);
                break;
            case Constants.CMD.CANCEL:
                gameApi.cancelGame(ic, user.getUsername());
                break;
            case Constants.CMD.LANG:
                if (args.length < 1) {
                    interfaceChannel.send(new ResultMessage(
                            new Message("language.command.need.parameter", ic.getUserId(), ic.getUserName()),
                            ic.getSenderType(), ic));
                } else {
                    MessageHolder.Lang lang = MessageHolder.Lang.valueOf(args[0].toUpperCase());
                    if (Objects.isNull(lang))
                        interfaceChannel.send(new ResultMessage(
                                new Message("language.command.need.parameter", ic.getUserId(), ic.getUserName()),
                                ic.getSenderType(), ic));
                    else {
                        langContainer.put(ic.getIntRoomId(), lang);
                        ic.setLang(lang);
                        persistenceApi.setLang(ic.getUserId(), lang);
                        interfaceChannel.send(new ResultMessage(
                                new Message("language.changed", ic.getUserId(), ic.getUserName(), lang.lang()),
                                ic.getSenderType(), ic));
                    }
                }
        }
    }
}
