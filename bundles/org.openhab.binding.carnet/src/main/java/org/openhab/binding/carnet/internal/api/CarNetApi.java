/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.carnet.internal.api;

import static org.eclipse.jetty.http.HttpMethod.*;
import static org.openhab.binding.carnet.internal.api.CarNetApiConstants.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.openhab.binding.carnet.internal.config.CarNetAccountConfiguration;
import org.openhab.binding.carnet.internal.handler.CarNetVehicleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CarNetApi} implements the http based API access to CarNet
 *
 * @author Markus Michels - Initial contribution
 */
public class CarNetApi {
    private final Logger logger = LoggerFactory.getLogger(CarNetVehicleHandler.class);
    private final CarNetAccountConfiguration config;
    private final HttpClient httpClient;
    private String vin = "";

    public CarNetApi(HttpClient httpClient, CarNetAccountConfiguration config) {
        logger.debug("Initializing CarNet API, brand={}", config.brand);
        this.config = config;
        this.httpClient = httpClient;
    }

    protected String getToken() throws IOException {
        try {
            String fields = "grant_type=password&username" + config.user + "password=" + config.password;
            ContentResponse contentResponse = httpClient.newRequest(getBrandUrl("core/auth/v1/{0}/{1}/token"))
                    .timeout(CNAPI_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .content(new StringContentProvider(fields)).method(POST).send();
            String content = contentResponse.getContentAsString();
            logger.debug("Response complete: {}", content);

            /*
             * returns token as JSON
             * {
             * "access_token": "64217P2jTNpckrCed7o8b5OJWAvTOGuWz8dperT4zY6KkDzREv67",
             * "token_type":"AudiAuth",
             * "expires_in":3600
             * }
             */

            return content;
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new IOException(e);
        }
    }

    /**
     * Sends a HTTP GET request using the synchronous client
     *
     * @param path Path of the requested resource
     * @return response
     */
    @Nullable
    public String httpGet(String url) {
        try {
            ContentResponse contentResponse = httpClient.newRequest(url)
                    .timeout(CNAPI_TIMEOUT_MS, TimeUnit.MILLISECONDS).method(GET).send();
            String content = contentResponse.getContentAsString();
            logger.debug("Response complete: {}", content);
            return content;
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            logger.debug("Failed to GET url '{}': {}", url, e.getLocalizedMessage(), e);
            return null;
        }
    }

    /**
     * Sends a HTTP GET request using the synchronous client
     *
     * @param path Path of the requested resource
     * @return response
     */
    @Nullable
    public String httpPost(String url, String args) {
        try {
            ContentResponse contentResponse = httpClient.newRequest(url)
                    .timeout(CNAPI_TIMEOUT_MS, TimeUnit.MILLISECONDS).method(POST)
                    .content(new StringContentProvider(args)).send();
            String content = contentResponse.getContentAsString();
            logger.debug("Response complete: {}", content);
            return content;
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            logger.debug("Failed to POST url '{}' data {}: {}", url, e.getLocalizedMessage(), args, e);
            return null;
        }
    }

    /**
     * Constructs an URL from the stored information, a specified path and a specified argument string
     *
     * @param path Path to include in URL
     * @param args String of arguments, in standard HTTP format (arg1=value1&arg2=value2&...)
     * @return URL
     */
    public String getURL(String path, String args) throws IOException {
        return getUrl(path.isEmpty() ? path : path + "?" + args);
    }

    protected String getBrandUrl(String format) throws IOException {
        return getUrl(MessageFormat.format(format, config.brand, config.country, vin));
    }

    protected String getBrandUrl(String format, String args) throws IOException {
        String path = MessageFormat.format(format, config.brand, config.country, vin);
        return getUrl(path.isEmpty() ? path : path + "?" + args);
    }

    /**
     * Constructs an URL from the stored information and a specified path
     *
     * @param path Path to include in URL
     * @return URL
     */
    public String getUrl(String path) throws IOException {
        return getBaseUrl() + "/" + path;
    }

    public String getBaseUrl() throws IOException {
        if (config.brand.equalsIgnoreCase(CNAPI_BRAND_AUDI)) {
            return CNAPI_BASE_URL_AUDI;
        }
        if (config.brand.equalsIgnoreCase(CNAPI_BRAND_VW)) {
            return CNAPI_BASE_URL_VW;
        }
        // if (config.brand.equalsIgnoreCase(CNAPI_BRAND_SKODA)) {
        // return CNAPI_BASE_URL_SKODA;
        // }
        throw new IOException("Unknown brand for base URL");
    }
}
