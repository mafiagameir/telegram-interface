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

package co.mafiagame.telegraminterface;

import co.mafiagame.common.utils.MessageHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Esa Hekmatizadeh
 */
@Component
public class LangContainer {

    private Map<Long, MessageHolder.Lang> roomLangMap = new HashMap<>();

    public MessageHolder.Lang getLang(Long roomId) {
        if (Objects.isNull(roomId))
            return MessageHolder.Lang.EN;
        MessageHolder.Lang lang = roomLangMap.get(roomId);
        return Objects.isNull(lang) ? MessageHolder.Lang.EN : lang;
    }

    public void put(Long roomId, MessageHolder.Lang lang) {
        roomLangMap.put(roomId, lang);
    }

    public void remove(Long roomId) {
        roomLangMap.remove(roomId);
    }
}
