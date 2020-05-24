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
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetActionResponse;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiToken;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetDestinations;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetHistory;
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

        public CarNetPendingRequest(String service, String action, CarNetActionResponse rsp) {
            // normalize the resonse type
            if (rsp.rluActionResponse != null) {
                rsp.response = rsp.rluActionResponse;
            }
            vin = rsp.response.vin;
            this.service = service;
            this.action = action;
            requestId = rsp.response.requestId;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(CarNetApi.class);
    private boolean initialzed = false;
    private final Gson gson = new Gson();
    private HttpClient httpClient = new HttpClient();
    private CombinedConfig config = new CombinedConfig();

    private CarNetToken brandToken = new CarNetToken();
    private CarNetToken idToken = new CarNetToken();
    private CarNetToken vwToken = new CarNetToken();
    private Map<String, CarNetToken> securityTokens = new HashMap<>();
    private Map<String, CarNetPendingRequest> pendingRequest = new HashMap<>();

    public CarNetApi() {
    }

    public CarNetApi(@Nullable HttpClient httpClient) {
        logger.debug("Initializing CarNet API");
        Validate.notNull(httpClient);
        this.httpClient = httpClient;
    }

    public boolean isInitialized() {
        return initialzed;
    }

    public void setConfig(CarNetAccountConfiguration config) {
        logger.debug("Setting up CarNet API for brand {} ({}), user {}", config.brand, config.country, config.user);
        this.config.account = config;
        initialzed = true;
    }

    public void setConfig(CarNetVehicleConfiguration config) {
        this.config.vehicle = config;
    }

    public void initialize() throws CarNetException {
        initialzed = true;
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

    private String createAccessToken() throws CarNetException {
        if (!brandToken.isExpired()) {
            return brandToken.accessToken;
        }

        if (!isInitialized()) {
            throw new CarNetException("API not completely initialized");
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
        brandToken = new CarNetToken(token);
        return brandToken.accessToken;
    }

    private String createIdToken() throws CarNetException {
        if (!idToken.isExpired()) {
            return idToken.idToken;
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
        return idToken.idToken;
    }

    private String createVwToken() throws CarNetException {
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
        headers.put(HttpHeader.ACCEPT.toString(), "*/*");
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
        return vwToken.accessToken;
    }

    private String createsecurityToken(String service, String action) throws CarNetException {
        if (config.vehicle.pin.isEmpty()) {
            throw new CarNetException("No SPIN is confirgured, can't perform authentication");
        }

        String serviceId = getServiceId(config.vehicle.vin, service);
        if (securityTokens.containsKey(serviceId)) {
            CarNetToken securityToken = securityTokens.get(serviceId);
            if (!securityToken.isExpired()) {
                return securityToken.securityToken;
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
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);

        // Build Hash: SHA512(SPIN+Challenge)
        String url = "https://mal-1a.prd.ece.vwg-connect.com/api/rolesrights/authorization/v2/vehicles/"
                + config.vehicle.vin.toUpperCase() + "/services/" + service + "/operations/" + action
                + "/security-pin-auth-requested";
        String json = httpGet(url, headers, vwToken.accessToken);
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
                headers, data);
        CarNetToken securityToken = new CarNetToken(gson.fromJson(json, CarNetApiToken.class));
        if (securityToken.securityToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        logger.debug("securityToken granted successful!");
        if (!securityTokens.containsKey(serviceId)) {
            securityTokens.put(serviceId, securityToken);
        } else {
            securityTokens.replace(serviceId, securityToken);
        }
        return securityToken.securityToken;
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
        String json = httpGet(CNAPI_URI_VEHICLE_LIST);
        CarNetVehicleList vehiceList = gson.fromJson(json, CarNetVehicleList.class);
        Validate.notNull(vehiceList, "Unable to get vehicle list!");
        return vehiceList;
    }

    public CarNetVehicleDetails getVehicleDetails(String vin) throws CarNetException {
        String json = httpGet(CNAPI_URI_VEHICLE_DETAILS, vin);
        CarNetVehicleDetails details = gson.fromJson(json, CarNetVehicleDetails.class);
        Validate.notNull(details, "Unable to get vehicle details!");
        return details;
    }

    public CarNetVehicleStatus getVehicleStatus() throws CarNetException {
        String json = httpGet(CNAPI_URI_VEHICLE_STATUS);
        CarNetVehicleStatus status = gson.fromJson(json, CarNetVehicleStatus.class);
        Validate.notNull(status, "Unable to get vehicle details!");
        return status;
    }

    public CarNetVehiclePosition getVehiclePosition() throws CarNetException {
        String json = httpGet(CNAPI_URI_VEHICLE_POSITION);
        CarNetVehiclePosition position = gson.fromJson(json, CarNetVehiclePosition.class);
        Validate.notNull(position, "Unable to get vehicle position!");
        return position;
    }

    public CarNetDestinations getDestinations() throws CarNetException {
        String json = httpGet(CNAPI_URI_DESTINATIONS);
        CarNetDestinations destinations = gson.fromJson(json, CarNetDestinations.class);
        Validate.notNull(destinations, "Unable to get vehicle destinations!");
        return destinations;
    }

    public CarNetHistory getHistory() throws CarNetException {
        String json = httpGet(CNAPI_URI_HISTORY);
        CarNetHistory history = gson.fromJson(json, CarNetHistory.class);
        Validate.notNull(history, "Unable to get vehicle history!");
        return history;
    }

    public boolean getClimaStatus() throws CarNetException {
        // String json = httpGet(CNAPI_VWURL_CLIMATE_STATUS, fillMmiHeaders());
        String json = httpGet(CNAPI_VWURL_CLIMATE_STATUS);
        return true;
    }

    public boolean getStoredPosition() throws CarNetException {
        String json = httpGet(CNAPI_VWURL_STORED_POS);
        return true;
    }

    public boolean getTimer() throws CarNetException {
        String json = httpGet(CNAPI_VWURL_TIMER);
        return true;
    }

    public void lockDoor(boolean lock) throws CarNetException {
        final String action = lock ? CNAPI_RLU_LOCK : CNAPI_RLU_UNLOCK;
        if (config.vehicle.pin.isEmpty()) {
            throw new CarNetException("No SPIN is confirgured, can't perform authentication");
        }

        Map<String, String> headers = fillActionHeaders("application/vnd.vwg.mbb.RemoteLockUnlock_v1_0_0+xml",
                createsecurityToken(CNAPI_SERVICE_REMOTELOCK, action));
        String data = "<?xml version=\"1.0\" encoding= \"UTF-8\" ?><rluAction xmlns=\"http://audi.de/connect/rlu\">"
                + "<action>" + (lock ? "lock" : "unlock") + "</action></rluAction>";
        String json = httpPost("https://msg.volkswagen.de/fs-car/bs/rlu/v1/{0}/{1}/vehicles/{2}/actions", headers, data,
                "");
        queuePendingAction(json, CNAPI_SERVICE_REMOTELOCK, action);
    }

    public void climaControl(boolean start) throws CarNetException {
        final String action = start ? "start" : "stop";

        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        Map<String, String> headers = fillActionHeaders(
                "application/vnd.vwg.mbb.ClimaterAction_v1_0_0+xml;charset=utf-8", "");
        data = data + (start
                ? "<action><type>startClimatisation</type><settings><heaterSource>electric</heaterSource></settings></action>"
                : "<action><type>stopClimatisation</type></action>");
        String json = httpPost(
                "https://msg.volkswagen.de/fs-car/bs/climatisation/v1/{0}/{1}/vehicles/{2}/climater/actions", headers,
                data);
        queuePendingAction(json, CNAPI_SERVICE_CLIMATISATION, action);
    }

    public void controlWindowHeating(boolean start) throws CarNetException {
        final String action = start ? "startWindowHeating" : "stopWindowHeating";

        Map<String, String> headers = fillActionHeaders("application/vnd.vwg.mbb.ClimaterAction_v1_0_0+xml", "");
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><action><type>" + action + "</type></action>";
        String json = httpPost(
                "https://msg.volkswagen.de/fs-car/bs/climatisation/v1/{0}/{1}/vehicles/{2}/climater/actions", headers,
                data, "");
        queuePendingAction(json, CNAPI_SERVICE_CLIMATISATION, action);
    }

    public void controlPreHeating(boolean start) throws CarNetException {
        String action = start ? "startPreHeating" : "stopPreHeating";
        Map<String, String> headers = fillActionHeaders("application/vnd.vwg.mbb.ClimaterAction_v1_0_0+xml",
                createsecurityToken(CNAPI_SERVICE_PREHEATING, CNAPI_RHEATING_ACTION));
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<performAction xmlns=\"http://audi.de/connect/rs\"><quickstart><active>" + action
                + "</active></quickstart></performAction>";
        String json = httpPost(
                "https://msg.volkswagen.de/fs-car/bs/climatisation/v1/{0}/{1}/vehicles/{2}/climater/actions", headers,
                data, vwToken.accessToken);
        queuePendingAction(json, CNAPI_SERVICE_CLIMATISATION, action);
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

    private void queuePendingAction(String responseJson, String service, String action) {
        CarNetActionResponse in = gson.fromJson(responseJson, CarNetActionResponse.class);
        CarNetPendingRequest rsp = new CarNetPendingRequest(service, action, in);
        logger.debug("{}: Request for {}.{} accepted, requestId={}", rsp.vin, service, action, rsp.requestId);
        pendingRequest.put(rsp.requestId, rsp);

    }

    private Map<String, String> fillBrandHeaders() throws CarNetException {
        Map<String, String> headers = new TreeMap<String, String>();
        String auth = MessageFormat.format("{0} {1} {2}", brandToken.authType, CNAPI_AUTH_AUDI_VERS,
                createAccessToken());
        headers.put(HttpHeader.USER_AGENT.toString(), CNAPI_HEADER_USER_AGENT);
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_EREMOTE);
        headers.put(CNAPI_HEADER_VERS, CNAPI_HEADER_VERS_VALUE);
        headers.put(HttpHeader.AUTHORIZATION.toString(), auth);
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);
        return headers;
    }

    private Map<String, String> fillMmiHeaders() throws CarNetException {
        /*
         * 'Accept': 'application/json',
         * 'X-App-ID': 'de.audi.mmiapp',
         * 'X-App-Name': 'MMIconnect',
         * 'X-App-Version': '2.8.3',
         * 'X-Brand': 'audi',
         * 'X-Country-Id': 'DE',
         * 'X-Language-Id': 'de',
         * 'X-Platform': 'google',
         * 'User-Agent': 'okhttp/2.7.4',
         * 'ADRUM_1': 'isModule:true',
         * 'ADRUM': 'isAray:true'
         */
        Map<String, String> headers = new TreeMap<String, String>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/2.7.4");
        headers.put(CNAPI_HEADER_APP, "MMIconnect");
        headers.put("X-App-ID", "de.audi.mmiapp");
        headers.put(CNAPI_HEADER_VERS, "2.8.3");
        headers.put("X-Brand", config.account.brand.toLowerCase());
        headers.put("X-Country-Id", config.account.country);
        headers.put("X-Language-Id", config.account.country.toLowerCase());
        headers.put("X-Platform", "google");
        headers.put("ADRUM_1", "isModule:true");
        headers.put("ADRUM", "isAray:true");
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);

        // String bearer = brandToken.authType + " " + brandToken.authVersion + " " + createAccessToken();
        String bearer = createAccessToken();
        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + bearer);

        return headers;
    }

    private Map<String, String> fillActionHeaders() throws CarNetException {
        /*
         * "User-Agent": "okhttp/3.7.0",
         * "X-App-Version": "3.14.0",
         * "X-App-Name": "myAudi",
         * "X-Market": "de_DE",
         * "Accept": "application/json"
         */
        String token = createVwToken();
        Map<String, String> headers = new TreeMap<String, String>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(CNAPI_HEADER_VERS, "3.14.0");
        headers.put("X-Market", "de_DE");
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);
        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + token);
        return headers;
    }

    private Map<String, String> fillActionHeaders(String contentType, String securityToken) throws CarNetException {
        // "User-Agent": "okhttp/3.7.0",
        // "Host": "msg.volkswagen.de",
        // "X-App-Version": "3.14.0",
        // "X-App-Name": "myAudi",
        // "Authorization": "Bearer " + self.vwToken.get("access_token"),
        // "Accept-charset": "UTF-8",
        // "Content-Type": content_type,
        // "Accept": "application/json,
        // application/vnd.vwg.mbb.ChargerAction_v1_0_0+xml,application/vnd.volkswagenag.com-error-v1+xml,application/vnd.vwg.mbb.genericError_v1_0_2+xml,
        // application/vnd.vwg.mbb.RemoteStandheizung_v2_0_0+xml,
        // application/vnd.vwg.mbb.genericError_v1_0_2+xml,application/vnd.vwg.mbb.RemoteLockUnlock_v1_0_0+xml,*/*","
        String token = createVwToken();

        Map<String, String> headers = new TreeMap<String, String>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0)");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(CNAPI_HEADER_VERS, "3.14.0");
        headers.put(HttpHeader.CONTENT_TYPE.toString(), contentType);
        headers.put(HttpHeader.ACCEPT.toString(),
                "application/json, application/vnd.vwg.mbb.ChargerAction_v1_0_0+xml,application/vnd.volkswagenag.com-error-v1+xml,application/vnd.vwg.mbb.genericError_v1_0_2+xml, application/vnd.vwg.mbb.RemoteStandheizung_v2_0_0+xml, application/vnd.vwg.mbb.genericError_v1_0_2+xml,application/vnd.vwg.mbb.RemoteLockUnlock_v1_0_0+xml,*/*");
        headers.put(HttpHeader.ACCEPT_CHARSET.toString(), StandardCharsets.UTF_8.toString());

        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + token);
        headers.put(HttpHeader.HOST.toString(), "msg.volkswagen.de");
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
    public String httpGet(String uri, Map<String, String> headers, String token) throws CarNetException {
        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + token);
        return request(HttpMethod.GET, uri, "", headers, "", config.vehicle.vin, token);
    }

    public String httpGet(String uri, Map<String, String> headers) throws CarNetException {
        return request(HttpMethod.GET, uri, "", headers, "", config.vehicle.vin, "");
    }

    public String httpGet(String uri, String vin) throws CarNetException {
        Map<String, String> headers = !uri.contains(".volkswagen.") ? fillBrandHeaders() : fillActionHeaders();
        return request(HttpMethod.GET, uri, "", headers, "", vin, "");
    }

    public String httpGet(String uri) throws CarNetException {
        return httpGet(uri, "");
    }

    /**
     * Sends a HTTP POST request using the synchronous client
     *
     * @param path Path of the requested resource
     * @return response
     */
    public String httpPost(String uri, String parms, Map<String, String> headers, String data, String vin)
            throws CarNetException {
        return request(HttpMethod.POST, uri, parms, headers, data, vin, "");
    }

    public String httpPost(String uri, Map<String, String> headers, String data) throws CarNetException {
        return request(HttpMethod.POST, uri, "", headers, data, "", "");
    }

    public String httpPost(String uri, Map<String, String> headers, String data, String token) throws CarNetException {
        return request(HttpMethod.POST, uri, "", headers, data, "", token);
    }

    public String httpPost(String uri, Map<String, String> headers, Map<String, String> data, String vin, boolean json)
            throws CarNetException {
        return request(HttpMethod.POST, uri, "", headers, buildPostData(data, json), vin, "");
    }

    private String request(HttpMethod method, String uri, String parms, Map<String, String> headers, String data,
            String pvin, String token) throws CarNetException {
        Request request = null;
        String url = "";
        try {
            String vin = pvin.isEmpty() ? config.vehicle.vin : pvin;
            url = getBrandUrl(uri, parms, vin);
            CarNetApiResult apiResult = new CarNetApiResult(method.toString(), url);
            request = httpClient.newRequest(url).method(method).timeout(CNAPI_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            fillHttpHeaders(request, headers, token);
            fillPostData(request, data);

            // Do request and get response
            logger.debug("HTTP {} {}, data='{}', headers={}", request.getMethod(), request.getURI(), data,
                    request.getHeaders());
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

    private void fillHttpHeaders(Request request, Map<String, String> headers, String token) {
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> h : headers.entrySet()) {
                String key = h.getKey();
                String value = h.getValue();
                if (key.equals(HttpHeader.USER_AGENT.toString())) {
                    request.agent(value);
                } else if (key.equals(HttpHeader.ACCEPT.toString())) {
                    request.accept(value);
                } else {
                    if (!value.isEmpty()) {
                        request.header(key, value);
                    }
                }
            }
        }
    }

    private void fillPostData(Request request, String data) {
        if (!data.isEmpty()) {
            StringContentProvider postData;
            if (request.getHeaders().contains(HttpHeader.CONTENT_TYPE)) {
                String contentType = request.getHeaders().get(HttpHeader.CONTENT_TYPE);
                postData = new StringContentProvider(contentType, data, StandardCharsets.UTF_8);
            } else {
                boolean json = data.startsWith("{");
                postData = new StringContentProvider(json ? CNAPI_ACCEPTT_JSON : CNAPI_CONTENTT_FORM_URLENC, data,
                        StandardCharsets.UTF_8);
            }
            request.content(postData);
            request.header(HttpHeader.CONTENT_LENGTH, Long.toString(postData.getLength()));
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
