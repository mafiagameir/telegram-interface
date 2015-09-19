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

package co.mafiagame.telegraminterface;

import static co.mafiagame.common.Constants.CMD;

/**
 * @author Esa Hekmatizadeh
 */
@Deprecated
public class CommandsMap {
    public static String getFaCommand(String command) {
        return command;
        /*switch (command) {
            case CMD.VOTE:
                return CMD.FA.VOTE;
            case CMD.MAFIA_VOTE:
                return CMD.FA.MAFIA_VOTE;
            case CMD.DETECTOR_ASK:
                return CMD.FA.DETECTOR_ASK;
            case CMD.DOCTOR_HEAL:
                return CMD.FA.DOCTOR_HEAL;
            case CMD.REGISTER:
                return CMD.FA.REGISTER;
            case CMD.WHO_IS_PLAYING:
                return CMD.FA.WHO_IS_PLAYING;
            case CMD.START_ELECTION:
                return CMD.FA.START_ELECTION;
            case CMD.START_FINAL_ELECTION:
                return CMD.FA.START_FINAL_ELECTION;
            case CMD.CANCEL:
                return CMD.FA.CANCEL;
            case CMD.START_STASHED_GAME:
                return CMD.FA.START_STASHED_GAME;
            case CMD.HELP:
                return CMD.FA.HELP;
        }
        throw new IllegalArgumentException();*/
    }
}
