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

import co.mafiagame.common.channel.InterfaceContext;
import co.mafiagame.common.domain.InterfaceType;
import co.mafiagame.common.domain.result.ChannelType;
import co.mafiagame.common.utils.MessageHolder;

/**
 * @author hekmatof
 */
public class TelegramInterfaceContext implements InterfaceContext {
    private Long roomId;
    private ChannelType senderType;
    private Long userId;
    private String userName;
    private MessageHolder.Lang lang = MessageHolder.Lang.EN;

    public TelegramInterfaceContext() {
    }

    public TelegramInterfaceContext(Long roomId, Long userId, String userName, ChannelType senderType, MessageHolder.Lang lang) {
        this.roomId = roomId;
        this.senderType = senderType;
        this.userId = userId;
        this.userName = userName;
        this.lang = lang;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return String.valueOf(userId);
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public Long getUserIdInt() {
        return userId;
    }

    public String getRoomId() {
        return String.valueOf(roomId);
    }

    public Long getIntRoomId() {
        return roomId;
    }

    @Override
    public InterfaceType interfaceType() {
        return InterfaceType.TELEGRAM;
    }

    @Override
    public ChannelType getSenderType() {
        return senderType;
    }

    public MessageHolder.Lang getLang() {
        return lang;
    }

    public void setLang(MessageHolder.Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TelegramInterfaceContext that = (TelegramInterfaceContext) o;

        return !(roomId != null ? !roomId.equals(that.roomId) : that.roomId != null);

    }

    @Override
    public int hashCode() {
        return roomId != null ? roomId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TelegramInterfaceContext{" +
                "roomId=" + roomId +
                ", senderType=" + senderType +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
