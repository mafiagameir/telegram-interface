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

package co.mafiagame.management;

import co.mafiagame.engine.api.ManagementApi;
import co.mafiagame.engine.domain.Game;
import co.mafiagame.engine.domain.StashedGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

/**
 * @author hekmatof
 */
@Controller
@RequestMapping("/manage")
public class ManagementController {
    @Autowired
    private ManagementApi managementApi;

    @RequestMapping("/games")
    public ResponseEntity games() {
        return ResponseEntity.ok(managementApi.getGames().stream()
                .map(Game::toString).collect(Collectors.toList()));
    }

    @RequestMapping("/stashedgames")
    public ResponseEntity stashedGames() {
        return ResponseEntity.ok(managementApi.getStashedGames().stream()
                .map(StashedGame::toString).collect(Collectors.toList()));
    }
}
