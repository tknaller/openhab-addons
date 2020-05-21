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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.DatatypeConverter;

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
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiToken;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetDestinations;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetHistory;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetRlUResponse;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetSecurityPinAuthInfo;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetSecurityPinAuthentication;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleDetails;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleList;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehiclePosition;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleStatus;
import org.openhab.binding.carnet.internal.config.CarNetAccountConfiguration;
import org.openhab.binding.carnet.internal.config.CarNetVehicleConfiguration;
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
    private class CombinedConfig {
        CarNetAccountConfiguration account = new CarNetAccountConfiguration();
        CarNetVehicleConfiguration vehicle = new CarNetVehicleConfiguration();
    }

    private class CarNetPendingRequest {
        public String vin = "";
        public String service = "";
        public String action = "";
        public String requestId = "";
        public Date creationTime = new Date();

        public CarNetPendingRequest(CarNetRlUResponse rsp, String action) {
            vin = rsp.rluActionResponse.vin;
            service = "rluActionResponse";
            this.action = action;
            requestId = rsp.rluActionResponse.requestId;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(CarNetApi.class);
    private final HttpClient httpClient;
    private final Gson gson = new Gson();
    private CombinedConfig config = new CombinedConfig();

    private CarNetToken apiToken = new CarNetToken();
    private CarNetToken idToken = new CarNetToken();
    private CarNetToken vwToken = new CarNetToken();
    private Map<String, CarNetToken> serviceTokens = new HashMap<>();
    private Map<String, CarNetPendingRequest> pendingRequest = new HashMap<>();

    public CarNetApi(@Nullable HttpClient httpClient) {
        logger.debug("Initializing CarNet API");
        Validate.notNull(httpClient);
        this.httpClient = httpClient;
    }

    public void setConfig(CarNetAccountConfiguration config) {
        logger.debug("Setting up CarNet API for brand {} ({}), user {}", config.brand, config.country, config.user);
        this.config.account = config;
    }

    public void setConfig(CarNetVehicleConfiguration config) {
        this.config.vehicle = config;
    }

    public void initialize() throws CarNetException {
        refreshTokens();
    }

    /**
     *
     * Request/refreh the different tokens
     * accessToken, which is required to access the API
     * idToken, which is required to request the securityToken and
     * securityToken, which is required to perform control functions
     *
     * The validity is checked and if token is not expired it will be reused.
     *
     * @throws CarNetException
     */
    public void refreshTokens() throws CarNetException {
        createAccessToken();
        try {
            createIdToken();
            createVwToken();
        } catch (CarNetException e) {
            // Ignore problems with the idToken or securityToken if the accessToken was requested successful
            logger.debug("Unable to create secondary token: {}", e.toString()); // "normal, no stack trace"
        }
    }

    private CarNetToken createAccessToken() throws CarNetException {
        if (!apiToken.isExpired()) {
            return apiToken;
        }

        logger.debug("Requesting new access token");
        Map<String, String> headers = new TreeMap<String, String>();
        Map<String, String> data = new TreeMap<>();
        data.put("grant_type", "password");
        data.put("username", config.account.user);
        data.put("password", config.account.password);
        String json = httpPost(CNAPI_URI_GET_TOKEN, headers, data, "", false);
        // process token
        CarNetApiToken token = gson.fromJson(json, CarNetApiToken.class);
        if ((token.accessToken == null) || token.accessToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        apiToken = new CarNetToken(token);
        return apiToken;
    }

    private CarNetToken createIdToken() throws CarNetException {
        if (!idToken.isExpired()) {
            return idToken;
        }

        // Get Audi idToken
        Map<String, String> headers = new TreeMap<>();
        Map<String, String> data = new TreeMap<>();
        data.put("client_id", "mmiconnect_android");
        data.put("scope",
                "openid profile email mbb offline_access mbbuserid myaudi selfservice:read selfservice:write");
        data.put("response_type", "token id_token");
        data.put("grant_type", "password");
        data.put("username", config.account.user);
        data.put("password", config.account.password);
        String json = httpPost(CNAPI_URL_GET_AUDI_TOKEN, headers, data, "", false);
        CarNetApiToken token = gson.fromJson(json, CarNetApiToken.class);
        if ((token.idToken == null) || token.idToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        idToken = new CarNetToken(token);
        return idToken;
    }

    private CarNetToken createVwToken() throws CarNetException {
        if (!vwToken.isExpired()) {
            return vwToken;
        }

        createIdToken();

        // "User-Agent": "okhttp/3.7.0",
        // "X-App-Version": "3.14.0",
        // "X-App-Name": "myAudi",
        // "X-Client-Id": "77869e21-e30a-4a92-b016-48ab7d3db1d8",
        // "Host": "mbboauth-1d.prd.ece.vwg-connect.com",
        Map<String, String> headers = new TreeMap<>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(CNAPI_HEADER_VERS, "3.14.0");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(CNAPI_HEADER_CLIENTID, "77869e21-e30a-4a92-b016-48ab7d3db1d8");
        headers.put(CNAPI_HEADER_HOST, "mbboauth-1d.prd.ece.vwg-connect.com");
        headers.put("Accept", "*/*");
        Map<String, String> data = new TreeMap<>();
        data.put("grant_type", "id_token");
        data.put("token", idToken.idToken);
        data.put("scope", "sc2:fal");

        String json = httpPost(CNAPI_URL_GET_SEC_TOKEN, headers, data, "", false);
        CarNetApiToken token = gson.fromJson(json, CarNetApiToken.class);
        if ((token.accessToken == null) || token.accessToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        vwToken = new CarNetToken(token);
        return vwToken;
    }

    private CarNetToken createServiceToken(String vin, String service, String action) throws CarNetException {
        String serviceId = getServiceId(vin, service);
        if (serviceTokens.containsKey(serviceId)) {
            CarNetToken serviceToken = serviceTokens.get(serviceId);
            if (!serviceToken.isExpired()) {
                return serviceToken;
            }
        }

        createVwToken();

        // "User-Agent": "okhttp/3.7.0",
        // "X-App-Version": "3.14.0",
        // "X-App-Name": "myAudi",
        // "Accept": "application/json",
        // "Authorization": "Bearer " + self.vwToken.get("access_token"),
        Map<String, String> headers = new TreeMap<>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(CNAPI_HEADER_VERS, "3.14.0");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(CNAPI_HEADER_AUTHORIZATION, "Bearer " + vwToken.accessToken);
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);

        String url = "https://mal-1a.prd.ece.vwg-connect.com/api/rolesrights/authorization/v2/vehicles/"
                + vin.toUpperCase() + "/services/" + service + "/operations/" + action + "/security-pin-auth-requested";

        // Build Hash: SHA512(SPIN+Challenge)
        String json = httpGet(url, headers, vin);
        CarNetSecurityPinAuthInfo authInfo = gson.fromJson(json, CarNetSecurityPinAuthInfo.class);
        String pinHash = sha512(config.vehicle.pin, authInfo.securityPinAuthInfo.securityPinTransmission.challenge)
                .toUpperCase();
        logger.debug("Authenticating SPIN, retires={}",
                authInfo.securityPinAuthInfo.securityPinTransmission.remainingTries);

        CarNetSecurityPinAuthentication pinAuth = new CarNetSecurityPinAuthentication();
        pinAuth.securityPinAuthentication.securityToken = authInfo.securityPinAuthInfo.securityToken;
        pinAuth.securityPinAuthentication.securityPin.challenge = authInfo.securityPinAuthInfo.securityPinTransmission.challenge;
        pinAuth.securityPinAuthentication.securityPin.securityPinHash = pinHash;
        String data = gson.toJson(pinAuth);
        json = httpPost(
                "https://mal-1a.prd.ece.vwg-connect.com/api/rolesrights/authorization/v2/security-pin-auth-completed",
                headers, data, "");
        CarNetToken serviceToken = new CarNetToken(gson.fromJson(json, CarNetApiToken.class));
        if ((serviceToken.securityToken == null) || serviceToken.securityToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        logger.debug("serviceToken granted successful!");
        if (!serviceTokens.containsKey(serviceId)) {
            serviceTokens.put(serviceId, serviceToken);
        } else {
            serviceTokens.replace(serviceId, serviceToken);
        }
        return serviceToken;
    }

    private String getServiceId(String vin, String service) {
        return vin + "." + service;
    }

    private String buildPostData(Map<String, String> dataMap, boolean json) {
        String data = "";
        for (Map.Entry<String, String> e : dataMap.entrySet()) {
            data = data + (data.isEmpty() ? "" : json ? ", " : "&");
            if (!json) {
                data = data + e.getKey() + "=" + e.getValue();
            } else {
                data = data + "\"" + e.getKey() + "\" : \"" + e.getValue() + "\"";
            }
        }
        return json ? "{ " + data + " }" : data;
    }

    public CarNetVehicleList getVehicles() throws CarNetException {
        Map<String, String> headers = fillAppHeaders();
        String json = httpGet(CNAPI_URI_VEHICLE_LIST, headers, "");
        CarNetVehicleList vehiceList = gson.fromJson(json, CarNetVehicleList.class);
        Validate.notNull(vehiceList, "Unable to get vehicle list!");
        return vehiceList;
    }

    public CarNetVehicleDetails getVehicleDetails(String vin) throws CarNetException {
        Map<String, String> headers = fillAppHeaders();
        String json = httpGet(CNAPI_URI_VEHICLE_DETAILS, headers, vin);
        CarNetVehicleDetails details = gson.fromJson(json, CarNetVehicleDetails.class);
        Validate.notNull(details, "Unable to get vehicle details!");
        return details;
    }

    public CarNetVehicleStatus getVehicleStatus(String vin) throws CarNetException {
        Map<String, String> headers = fillAppHeaders();
        String json = httpGet(CNAPI_URI_VEHICLE_STATUS, headers, vin);
        CarNetVehicleStatus status = gson.fromJson(json, CarNetVehicleStatus.class);
        Validate.notNull(status, "Unable to get vehicle details!");
        return status;
    }

    public CarNetVehiclePosition getVehiclePosition(String vin) throws CarNetException {
        Map<String, String> headers = fillAppHeaders();
        String json = httpGet(CNAPI_URI_VEHICLE_POSITION, headers, vin);
        CarNetVehiclePosition position = gson.fromJson(json, CarNetVehiclePosition.class);
        Validate.notNull(position, "Unable to get vehicle position!");
        return position;
    }

    public CarNetDestinations getDestinations(String vin) throws CarNetException {
        Map<String, String> headers = fillAppHeaders();
        String json = httpGet(CNAPI_URI_DESTINATIONS, headers, vin);
        CarNetDestinations destinations = gson.fromJson(json, CarNetDestinations.class);
        Validate.notNull(destinations, "Unable to get vehicle destinations!");
        return destinations;
    }

    public CarNetHistory getHistory(String vin) throws CarNetException {
        Map<String, String> headers = fillAppHeaders();
        String json = httpGet(CNAPI_URI_HISTORY, headers, vin);
        CarNetHistory history = gson.fromJson(json, CarNetHistory.class);
        Validate.notNull(history, "Unable to get vehicle history!");
        return history;
    }

    public void lockDoor(String vin, boolean lock) throws CarNetException {
        if (config.vehicle.pin.isEmpty()) {
            throw new CarNetException("No SPIN is confirgured, can't perform authentication");
        }

        String action = lock ? CNAPI_RLU_LOCK : CNAPI_RLU_UNLOCK;
        CarNetToken serviceToken = createServiceToken(vin, CNAPI_SERVICE_REMOTELOCK, action);
        Map<String, String> headers = fillAppHeaders("application/vnd.vwg.mbb.RemoteLockUnlock_v1_0_0+xml",
                serviceToken.securityToken);
        String data = "<?xml version=\"1.0\" encoding= \"UTF-8\" ?><rluAction xmlns=\"http://audi.de/connect/rlu\">"
                + "<action>" + (lock ? "lock" : "unlock") + "</action></rluAction>";
        String json = httpPost("https://msg.volkswagen.de/fs-car/bs/rlu/v1/{0}/{1}/vehicles/{2}/actions", headers, data,
                "");
        CarNetRlUResponse rsp = gson.fromJson(json, CarNetRlUResponse.class);
        logger.debug("{}: RLU request {} accepted, requestId={}", rsp.rluActionResponse.requestId, vin, action,
                rsp.rluActionResponse.vin);
        pendingRequest.put(rsp.rluActionResponse.requestId, new CarNetPendingRequest(rsp, action));
    }

    public boolean checkRluSuccessful(String vin, String requestId) {
        logger.debug("{}: Checking for RLU status, requestId=", vin, requestId);
        return checkRequestSuccessful(
                "https://msg.volkswagen.de/fs-car/bs/rlu/v1/{0}/{1}/vehicles/{2}/requests/" + requestId + "/status");
    }

    public boolean checkRequestSuccessful(String url) {
        /*
         * await self.check_request_succeeded(
         * checkUrl,
         * "lock vehicle" if lock else "unlock vehicle",
         * REQUEST_SUCCESSFUL,
         * REQUEST_FAILED,
         * "requestStatusResponse.status",
         * )
         */
        return true;
    }

    private Map<String, String> fillAppHeaders() throws CarNetException {
        createAccessToken();
        Validate.notNull(apiToken, "Token must not be null!");

        Map<String, String> headers = new TreeMap<String, String>();
        String auth = MessageFormat.format("{0} {1} {2}", apiToken.authType, CNAPI_AUTH_AUDI_VERS,
                apiToken.accessToken);
        headers.put(HttpHeader.USER_AGENT.toString(), CNAPI_HEADER_USER_AGENT);
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_EREMOTE);
        headers.put(CNAPI_HEADER_VERS, CNAPI_HEADER_VERS_VALUE);
        headers.put(HttpHeader.AUTHORIZATION.toString(), auth);
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);
        return headers;
    }

    private Map<String, String> fillAppHeaders(String contentType, String securityToken) throws CarNetException {
        createAccessToken();
        Validate.notNull(apiToken, "Token must not be null!");

        Map<String, String> headers = new TreeMap<String, String>();
        String auth = MessageFormat.format("{0} {1} {2}", apiToken.authType, CNAPI_AUTH_AUDI_VERS,
                apiToken.accessToken);
        headers.put(HttpHeader.USER_AGENT.toString(), CNAPI_HEADER_USER_AGENT);
        headers.put("Host", "msg.volkswagen.de");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(CNAPI_HEADER_VERS, CNAPI_HEADER_VERS_VALUE);
        headers.put(HttpHeader.CONTENT_TYPE.toString(), contentType);
        headers.put(HttpHeader.ACCEPT.toString(),
                "application/json, application/vnd.vwg.mbb.ChargerAction_v1_0_0+xml,application/vnd.volkswagenag.com-error-v1+xml,application/vnd.vwg.mbb.genericError_v1_0_2+xml, application/vnd.vwg.mbb.RemoteStandheizung_v2_0_0+xml, application/vnd.vwg.mbb.genericError_v1_0_2+xml,application/vnd.vwg.mbb.RemoteLockUnlock_v1_0_0+xml,*/*");
        headers.put("Accept-charset", "UTF-8");

        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + vwToken.accessToken);
        if (!securityToken.isEmpty()) {
            headers.put("x-mbbSecToken", securityToken);
        }
        return headers;
    }

    /**
     * Sends a HTTP GET request using the synchronous client
     *
     * @param path Path of the requested resource
     * @return response
     */
    @Nullable
    public String httpGet(String uri, Map<String, String> headers, String vin) throws CarNetException {
        return request(HttpMethod.GET, uri, "", headers, "", vin);
    }

    /**
     * Sends a HTTP POST request using the synchronous client
     *
     * @param path Path of the requested resource
     * @return response
     */
    @Nullable
    public String httpPost(String uri, String parms, Map<String, String> headers, String data, String vin)
            throws CarNetException {
        return request(HttpMethod.POST, uri, parms, headers, data, vin);
    }

    @Nullable
    public String httpPost(String uri, Map<String, String> headers, String data, String vin) throws CarNetException {
        return request(HttpMethod.POST, uri, "", headers, data, vin);
    }

    @Nullable
    public String httpPost(String uri, Map<String, String> headers, Map<String, String> data, String vin, boolean json)
            throws CarNetException {
        return request(HttpMethod.POST, uri, "", headers, buildPostData(data, json), vin);
    }

    @SuppressWarnings("null")
    @Nullable
    private String request(HttpMethod method, String uri, String parms, Map<String, String> headers,
            @Nullable String data, String pvin) throws CarNetException {
        Request request = null;
        String url = "";
        try {
            String vin = pvin.isEmpty() ? config.vehicle.vin : pvin;
            url = getBrandUrl(uri, parms, vin);
            CarNetApiResult apiResult = new CarNetApiResult(method.toString(), url);
            request = httpClient.newRequest(url).method(method).timeout(CNAPI_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            if (headers != null) {
                for (Map.Entry<String, String> h : headers.entrySet()) {
                    String key = h.getKey();
                    Validate.notNull(key);
                    String value = h.getValue();
                    if (key.equals(HttpHeader.USER_AGENT.toString())) {
                        request.header(key, value);
                        request.agent(value);
                    } else if (key.equals(HttpHeader.USER_AGENT.toString())) {
                        request.accept(value);
                    } else {
                        if ((value != null) && !value.isEmpty()) {
                            request.header(key, value);
                        }
                    }
                }
            }
            if ((data != null) && !data.isEmpty()) {
                StringContentProvider postData;
                if (request.getHeaders().contains(HttpHeader.CONTENT_TYPE)) {
                    postData = new StringContentProvider(data, StandardCharsets.UTF_8);
                } else {
                    boolean json = data.startsWith("{");
                    postData = new StringContentProvider(json ? CNAPI_ACCEPTT_JSON : CNAPI_CONTENTT_FORM_URLENC, data,
                            StandardCharsets.UTF_8);
                }
                request.content(postData);
                request.header("Content-Length", Long.toString(postData.getLength()));
            }

            // Do request and get response
            logger.debug("HTTP {} {}, data='{}', headers={}", method, url, data, request.getHeaders());
            ContentResponse contentResponse = request.send();
            apiResult = new CarNetApiResult(contentResponse);
            String response = contentResponse.getContentAsString().replaceAll("\t", "").replaceAll("\r\n", "").trim();

            // validate response, API errors are reported as Json
            logger.trace("HTTP Response: {}", response);
            if (response.contains("\"error\":")) {
                throw new CarNetException("Authentication failed", apiResult);
            }
            int code = contentResponse.getStatus();
            if ((code != HttpStatus.OK_200) && (code != HttpStatus.ACCEPTED_202)) {
                throw new CarNetException("API Call failed (HTTP" + code + ")", apiResult);
            }
            if (response.isEmpty()) {
                throw new CarNetException("Invalid result received from API, maybe URL problem", apiResult);
            }
            return response;
        } catch (ExecutionException | InterruptedException | TimeoutException | MalformedURLException e) {
            logger.debug("API call failed", e);
            throw new CarNetException("API call failed!", new CarNetApiResult(request, e));
        }
    }

    public @Nullable CarNetToken getToken() {
        return apiToken;
    }

    /**
     * Constructs an URL from the stored information, a specified path and a specified argument string
     *
     */
    private String getBrandUrl(String uriTemplate, String args, String vin) throws MalformedURLException {
        String path = MessageFormat.format(uriTemplate, config.account.brand, config.account.country, vin);
        if (!uriTemplate.contains("://")) { // no a full URL
            return getUrl(path.isEmpty() ? path : path + (!args.isEmpty() ? "?" + args : ""));
        } else {
            return path + (!args.isEmpty() ? "?" + args : "");
        }
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
        if (config.account.brand.equalsIgnoreCase(CNAPI_BRAND_AUDI)) {
            return CNAPI_BASE_URL_AUDI;
        }
        if (config.account.brand.equalsIgnoreCase(CNAPI_BRAND_VW)) {
            return CNAPI_BASE_URL_VW;
        }
        // if (config.brand.equalsIgnoreCase(CNAPI_BRAND_SKODA)) {
        // return CNAPI_BASE_URL_SKODA;
        // }
        throw new MalformedURLException("Unknown brand for base URL");
    }

    public static String sha512(String pin, String challenge) throws CarNetException {
        try {
            MessageDigest hash = MessageDigest.getInstance("SHA-512");
            byte[] pinBytes = DatatypeConverter.parseHexBinary(pin);
            byte[] challengeBytes = DatatypeConverter.parseHexBinary(challenge);
            ByteBuffer input = ByteBuffer.allocate(pinBytes.length + challengeBytes.length);
            input.put(pinBytes);
            input.put(challengeBytes);
            byte[] digest = hash.digest(input.array());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digest.length; ++i) {
                sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new CarNetException("sha512() failed", e);
        }
    }
}
