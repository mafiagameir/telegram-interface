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

import co.mafiagame.telegram.api.domain.TUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author hekmatof
 */
@RestController
public class UpdateController {
    private static final Logger logger = LoggerFactory.getLogger(UpdateController.class);
    @Autowired
    private CommandHandler commandHandler;
    @Value("${mafia.telegram.token}")
    private String telegramToken;

    @RequestMapping(value = "/{token}/update", method = RequestMethod.POST)
    private void getUpdate(@PathVariable String token, @RequestBody TUpdate update) {
        logger.info("receive: {}", update);
        if (!validate(token)) {
            logger.warn("Suspicious connection with token {} and update {}", token, update);
            return;
        }
        commandHandler.handle(update);

    }

    private boolean validate(String token) {
        return telegramToken.equalsIgnoreCase(token);
    }
}
