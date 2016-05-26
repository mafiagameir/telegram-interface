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

import co.mafiagame.telegram.api.domain.TResult;
import co.mafiagame.telegram.api.domain.TUpdate;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author hekmatof
 */
@Component
public class UpdateController {
    private static final Logger logger = LoggerFactory.getLogger(UpdateController.class);
    @Autowired
    private CommandHandler commandHandler;
    @Value("${mafia.telegram.token}")
    private String telegramToken;
    @Value("${mafia.telegram.api.url}")
    private String telegramUrl;

    @PostConstruct
    public void init() {
        Thread thread = new Thread(() -> {
            try {
                long offset = 1;
                Thread.sleep(TimeUnit.MINUTES.toMillis(2));
                while (true) {
                    try {
                        RestTemplate restTemplate = new RestTemplate();
                        setErrorHandler(restTemplate);
                        //logger.debug("get request with offset {}", offset + 1);
                        TResult tResult = restTemplate.getForObject(
                                telegramUrl + telegramToken + "/getUpdates?offset=" + String.valueOf(offset + 1),
                                TResult.class);
                        for (TUpdate update : tResult.getResult()) {
                            if (offset < update.getId()) {
                                offset = update.getId();
                                if (Objects.nonNull(update.getMessage())) {
                                    logger.info("receive: {}", update);
                                    commandHandler.handle(update);
                                }
                                logger.info("offset set to {}", offset);
                            }
                        }
                        Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500));
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void setErrorHandler(RestTemplate restTemplate) {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return !clientHttpResponse.getStatusCode().equals(HttpStatus.OK);
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                logger.error("error calling telegram getUpdate\n code:{}\n{}",
                        clientHttpResponse.getStatusCode(),
                        IOUtils.toString(clientHttpResponse.getBody()));
            }
        });
    }

    @RequestMapping(value = "/{token}/update", method = RequestMethod.POST)
    @Deprecated
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
