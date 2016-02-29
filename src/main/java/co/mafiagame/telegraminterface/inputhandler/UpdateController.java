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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private volatile int offset = 1;

    @PostConstruct
    public void init() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        HttpClient httpClient = new HttpClient();
        HttpMethod get = new GetMethod(telegramUrl + telegramToken + "/getUpdates");
        ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);
        executorService.setMaximumPoolSize(1);
        executorService.scheduleAtFixedRate(() -> {
            get.setQueryString(new NameValuePair[]{
                    new NameValuePair("offset", String.valueOf(offset)),
                    new NameValuePair("limit", "10")
            });
            try {
                int status = httpClient.executeMethod(get);
                if (status != 200)
                    logger.error("error calling telegram getUpdate\n code:{}\n{}", status, get.getResponseBodyAsString());
                TResult tResult = objectMapper.readValue(get.getResponseBodyAsStream(), TResult.class);
                for (TUpdate update : tResult.getResult()) {
                    logger.info("receive: {}", update);
                    commandHandler.handle(update);
                    offset = update.getId();
                }
            } catch (IOException e) {
                logger.error("error calling telegram getUpdate: {}", e.getMessage(), e);
            } finally {
                get.releaseConnection();
            }
        }, 0, 5, TimeUnit.SECONDS);
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
