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

import static org.openhab.binding.carnet.internal.api.CarNetApiConstants.*;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiErrorMessage;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiToken;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleDetails;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleList;
import org.openhab.binding.carnet.internal.config.CarNetAccountConfiguration;
import org.openhab.binding.carnet.internal.handler.CarNetVehicleHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * The {@link CarNetApi} implements the http based API access to CarNet
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class CarNetApi {
    private final Logger logger = LoggerFactory.getLogger(CarNetVehicleHandler.class);
    private final @Nullable HttpClient httpClient;
    private final Gson gson = new Gson();
    private CarNetAccountConfiguration config = new CarNetAccountConfiguration();

    private @Nullable CarNetApiToken token = null;

    @SuppressWarnings("null")
    public CarNetApi(@Nullable HttpClient httpClient, @Nullable CarNetAccountConfiguration config) {
        logger.debug("Initializing CarNet API, brand={}", config.brand);
        this.config = config;
        Validate.notNull(httpClient);
        this.httpClient = httpClient;
    }

    public void initialize() throws CarNetException {
        createToken();
    }

    @SuppressWarnings("null")
    protected @Nullable CarNetApiToken createToken() throws CarNetException {

        Map<String, String> headers = new TreeMap<String, String>();
        headers.put(HttpHeader.CONTENT_TYPE.toString(), CNAPI_CONTENTT_FORM_URLENC);
        String data = "grant_type=password&username=" + config.user + "&password=" + config.password;
        String json = httpPost(CNAPI_URI_GET_TOKEN, null, headers, data, "");
        // process token
        token = gson.fromJson(json, CarNetApiToken.class);
        if ((token.accesToken == null) || token.accesToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        return token;
    }

    @SuppressWarnings("null")
    public CarNetVehicleList getVehicles() throws CarNetException {
        Map<String, String> headers = fillAppHeaders();
        String json = httpGet(CNAPI_URI_VEHICLE_LIST, null, headers, "");
        CarNetVehicleList vehiceList = gson.fromJson(json, CarNetVehicleList.class);
        Validate.notNull(vehiceList, "Unable to get vehicle list!");
        return vehiceList;
    }

    public CarNetVehicleDetails getVehicleDetails(String vin) throws CarNetException {
        Map<String, String> headers = fillAppHeaders();
        String json = httpGet(CNAPI_URI_VEHICLE_DETAILS, null, headers, vin);
        CarNetVehicleDetails details = gson.fromJson(json, CarNetVehicleDetails.class);
        Validate.notNull(details, "Unable to get vehicle details!");
        return details;
    }

    private Map<String, String> fillAppHeaders() {
        Map<String, String> headers = new TreeMap<String, String>();
        Validate.notNull(token);
        String auth = MessageFormat.format("{0} {1} {2}", token.authType, CNAPI_AUTH_AUDI_VERS, token.accesToken);
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_VALUE);
        headers.put(CNAPI_HEADER_VERS, CNAPI_HEADER_VERS_VALUE);
        headers.put(HttpHeader.USER_AGENT.toString(), CNAPI_HEADER_USER_AGENT);
        headers.put(HttpHeader.AUTHORIZATION.toString(), auth);
        return headers;
    }

    /**
     * Sends a HTTP GET request using the synchronous client
     *
     * @param path Path of the requested resource
     * @return response
     */
    @Nullable
    public String httpGet(String uri, @Nullable String parms, @Nullable Map<String, String> headers, String vin)
            throws CarNetException {
        return request(HttpMethod.GET, uri, parms, headers, null, vin);
    }

    /**
     * Sends a HTTP GET request using the synchronous client
     *
     * @param path Path of the requested resource
     * @return response
     */
    @Nullable
    public String httpPost(String uri, @Nullable String parms, @Nullable Map<String, String> headers,
            @Nullable String data, String vin) throws CarNetException {
        return request(HttpMethod.POST, uri, parms, headers, data, vin);
    }

    @SuppressWarnings("null")
    @Nullable
    private String request(HttpMethod method, String uri, @Nullable String parms, @Nullable Map<String, String> headers,
            @Nullable String data, String vin) throws CarNetException {
        Request request = null;
        String url = "";
        try {
            url = getBrandUrl(uri, parms, vin);
            CarNetApiResult apiResult = new CarNetApiResult(method.toString(), url);
            request = httpClient.newRequest(url).method(method).timeout(CNAPI_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (headers != null) {
                for (Map.Entry<String, String> h : headers.entrySet()) {
                    Validate.notNull(h.getKey());
                    String value = h.getValue();
                    if ((value != null) && !value.isEmpty()) {
                        request.header(h.getKey(), h.getValue());
                    }
                }
            }
            request.header(HttpHeader.ACCEPT, CNAPI_ACCEPTT_JSON);
            if (data != null) {
                request.content(new StringContentProvider(data, StandardCharsets.UTF_8));
            }

            // Do request and get response
            logger.trace("HTTP {} {}, parms={}, data={}, headers={}", method.toString(), url, gs(parms), gs(headers),
                    gs(data));
            ContentResponse contentResponse = request.send();
            apiResult = new CarNetApiResult(contentResponse);
            String response = contentResponse.getContentAsString().replaceAll("\t", "").replaceAll("\r\n", "").trim();
            Validate.notNull(response);

            // validate response, API errors are reported as Json
            logger.trace("HTTP Response: {}", response);
            if (response.contains("\"error\":")) {
                CarNetApiErrorMessage error = gson.fromJson(response, CarNetApiErrorMessage.class);
                throw new CarNetException("Authentication failed (" + error.error + "): " + error.text);
            }
            if (contentResponse.getStatus() != HttpStatus.OK_200) {
                throw new CarNetException("API Call failed", apiResult);
            }
            if (response.isEmpty()) {
                throw new CarNetException("Invalid result received from API, maybe URL problem", apiResult);
            }
            return response;
        } catch (ExecutionException | InterruptedException | TimeoutException | MalformedURLException e) {
            CarNetApiResult apiResult = new CarNetApiResult(request, e);
            throw new CarNetException("API call failed!", apiResult, e);
        }
    }

    public @Nullable CarNetApiToken getToken() {
        return token;
    }

    /**
     * Constructs an URL from the stored information, a specified path and a specified argument string
     *
     */

    @SuppressWarnings("null")
    private String getBrandUrl(String uriTemplate, @Nullable String args, String vin) throws MalformedURLException {
        String path = MessageFormat.format(uriTemplate, config.brand, config.country, vin);
        return getUrl(path.isEmpty() ? path : path + (args != null ? "?" + args : ""));
    }

    /**
     * Constructs an URL from the stored information and a specified path
     *
     * @param path Path to include in URL
     * @return URL
     */
    private String getUrl(String path) throws MalformedURLException {
        return getBaseUrl() + "/" + path;
    }

    private String getBaseUrl() throws MalformedURLException {
        if (config.brand.equalsIgnoreCase(CNAPI_BRAND_AUDI)) {
            return CNAPI_BASE_URL_AUDI;
        }
        if (config.brand.equalsIgnoreCase(CNAPI_BRAND_VW)) {
            return CNAPI_BASE_URL_VW;
        }
        // if (config.brand.equalsIgnoreCase(CNAPI_BRAND_SKODA)) {
        // return CNAPI_BASE_URL_SKODA;
        // }
        throw new MalformedURLException("Unknown brand for base URL");
    }

    private String gs(@Nullable String s) {
        return s != null ? s : "";
    }

    private String gs(@Nullable Map<String, String> map) {
        return map != null ? map.toString() : "";
    }
}
