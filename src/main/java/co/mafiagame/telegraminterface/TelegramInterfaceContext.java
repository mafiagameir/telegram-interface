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
import co.mafiagame.telegram.api.domain.TChat;

/**
 * @author hekmatof
 */
public class TelegramInterfaceContext implements InterfaceContext {
    private TChat from;
    private TChat chat;
    private Long roomId;
    private MessageHolder.Lang lang = MessageHolder.Lang.FA;

    public TelegramInterfaceContext() {
    }

    public TelegramInterfaceContext(Long roomId, TChat from, TChat chat, MessageHolder.Lang lang) {
        this.roomId = roomId;
        this.lang = lang;
        this.from = from;
        this.chat = chat;
        if (roomId == null)
            this.roomId = chat.getId();
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return String.valueOf(from.getId());
    }

    @Override
    public String getUserName() {
        return from.getUsername();
    }

    public Long getUserIdInt() {
        return from.getId();
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
        return chat.getChannelType();
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
                "from=" + from +
                ", chat=" + chat +
                ", roomId=" + roomId +
                ", lang=" + lang +
                '}';
    }
}
