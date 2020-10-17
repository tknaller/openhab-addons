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

import static org.openhab.binding.carnet.internal.CarNetBindingConstants.API_REQUEST_TIMEOUT;
import static org.openhab.binding.carnet.internal.api.CarNetApiConstants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.UrlEncoded;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNApiToken;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNChargerInfo;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNChargerInfo.CarNetChargerStatus;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNClimater;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNClimater.CarNetClimaterStatus;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNEluActionHistory;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNEluActionHistory.CarNetRluHistory;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNOperationList;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNOperationList.CarNetOperationList;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNOperationList.CarNetOperationList.CarNetServiceInfo;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNPairingInfo;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNPairingInfo.CarNetPairingInfo;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNVehicleData;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNVehicleData.CarNetVehicleData;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetActionResponse;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetDestinations;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetHomeRegion;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetSecurityPinAuthInfo;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetSecurityPinAuthentication;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetServiceAvailability;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetTripData;
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
    public static final String UTF_8 = StandardCharsets.UTF_8.name();

    private class CombinedConfig {
        CarNetAccountConfiguration account = new CarNetAccountConfiguration();
        CarNetVehicleConfiguration vehicle = new CarNetVehicleConfiguration();
    }

    @NonNullByDefault
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

        public boolean isExpired() {
            Date currentTime = new Date();
            long diff = currentTime.getTime() - creationTime.getTime();
            return (diff / 1000) > API_REQUEST_TIMEOUT;
        }
    }

    private final Logger logger = LoggerFactory.getLogger(CarNetApi.class);
    private boolean initialzed = false;
    private final Gson gson = new Gson();
    private HttpClient httpClient = new HttpClient();
    private CombinedConfig config = new CombinedConfig();

    private CarNetToken brandToken = new CarNetToken();
    // private CarNetToken idToken = new CarNetToken();
    private CarNetToken vwToken = new CarNetToken();
    private CopyOnWriteArrayList<CarNetToken> securityTokens = new CopyOnWriteArrayList<CarNetToken>();
    private Map<String, CarNetPendingRequest> pendingRequest = new ConcurrentHashMap<>();
    private HttpFields lastHttpFields = new HttpFields();
    private boolean nextRedirect = false;

    // Brand specific data
    String urlCountry = "";
    String clientId = "";
    String xClientId = "";
    String authScope = "";
    String redirect = "";
    String xrequest = "";
    String responseType = "";
    String xappName = "";
    String xappVersion = "";

    public CarNetApi() {
    }

    public CarNetApi(@Nullable HttpClient httpClient) {
        logger.debug("Initializing CarNet API");
        Validate.notNull(httpClient);
        this.httpClient = httpClient;
        this.httpClient.setFollowRedirects(true);
    }

    public boolean isInitialized() {
        return initialzed;
    }

    public void setConfig(CarNetAccountConfiguration config) {
        logger.debug("Setting up CarNet API for brand {} ({}), user {}", config.brand, config.country, config.user);
        this.config.account = config;
        initBrandData();
    }

    public void setConfig(CarNetVehicleConfiguration config) {
        this.config.vehicle = config;
    }

    public void initialize() throws CarNetException {
        refreshTokens();
        initialzed = true;
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
    public boolean refreshTokens() throws CarNetException {
        try {
            refreshToken(config.account.brand, brandToken);
            refreshToken(CNAPI_BRAND_VW, vwToken);

            Iterator<CarNetToken> it = securityTokens.iterator();
            while (it.hasNext()) {
                CarNetToken stoken = it.next();
                if (!refreshToken(config.account.brand, stoken)) {
                    // Token invalid / refresh failed -> remove
                    securityTokens.remove(stoken);
                }
            }
        } catch (CarNetException e) {
            // Ignore problems with the idToken or securityToken if the accessToken was requested successful
            logger.debug("Unable to create secondary token: {}", e.toString()); // "normal, no stack trace"
        }
        return false;
    }

    private boolean refreshToken(String brand, CarNetToken token) throws CarNetException {
        if (!token.isValid()) {
            // token is still valid
            return false;
        }

        if (token.isExpired()) {
            logger.debug("{}: Refreshing Token {}", config.vehicle.vin, token);
            String url = "";
            String rtoken = "";
            Map<String, String> data = new TreeMap<>();

            if (isBrandAudi(brand)) {
                url = CNAPI_URL_AUDI_GET_TOKEN;
                // rtoken = idToken.refreshToken;
                rtoken = brandToken.refreshToken;
                url = CNAPI_URL_GET_SEC_TOKEN;
                data.put("client_id", clientId);
                data.put("grant_type", "refresh_token");
                data.put("response_type", "token id_token");
                data.put("refresh_token", rtoken);
            } else if (isBrandVW()) {
                url = CNAPI_URL_GET_SEC_TOKEN;
                rtoken = vwToken.refreshToken;
                data.put("grant_type", "refresh_token");
                data.put("scope", "scope=sc2%3Afa");
                data.put("token", rtoken);
            } else if (isBrandSkoda(brand)) {

            } else if (isBrandGo(brand)) {
                url = CNAPI_URL_GO_GET_TOKEN;
                data.put("client_id", clientId);
                data.put("scope", "openid+profile+address+email+phone");
                data.put("grant_type", "refresh_token");
                data.put("refresh_token", rtoken);
            } else {
                data.put("refresh_token=", rtoken);
                url = CNAPI_URL_DEF_GET_TOKEN;
                rtoken = brandToken.refreshToken; // not sure if that is the correct default one
            }

            try {
                Map<String, String> headers = fillRefreshHeaders();
                String json = httpPost(url, headers, data, "", false);
                return true;
            } catch (CarNetException e) {
                logger.debug("{}: Unable to refresh token {} - {}", config.vehicle.vin, token, e.getApiResult());
            }
        }

        // Invalidate token
        token.invalidate();
        return false;
    }

    private String createBrandToken() throws CarNetException {
        try {
            if (!brandToken.isExpired()) {
                return brandToken.accessToken;
            }

            if (!isInitialized()) {
                throw new CarNetException("API not completely initialized");
            }

            logger.debug("{}: Logging in, account={}", config.vehicle.vin, config.account.user);
            String url = CNAPI_OAUTH_AUTHORIZE_URL + "?response_type=code"
                    + "&client_id=09b6cbec-cd19-4589-82fd-363dfa8c24da%40apps_vw-dilab_com&redirect_uri=myaudi%3A%2F%2F%2F"
                    + "&scope=address%20profile%20badge%20birthdate%20birthplace%20nationalIdentifier%20nationality%20profession%20email%20vin%20phone%20nickname%20name%20picture%20mbb%20gallery%20openid"
                    + "&state=7f8260b5-682f-4db8-b171-50a5189a1c08&nonce=583b9af2-7799-4c72-9cb0-e6c0f42b87b3"
                    + "&prompt=login&ui_locales=de-DE%20de";
            Map<String, String> headers = new LinkedHashMap<>();
            headers.put("Accept", "application/json, text/plain, */*");
            headers.put("Content-Type", "application/json;charset=UTF-8");
            headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
            httpGet(url, headers);

            logger.debug("{}: OAuth: Get signin form", config.vehicle.vin);
            url = lastHttpFields.get("Location"); // Signin URL
            if (url.isEmpty()) {
                throw new CarNetException("Unable to get signin URL");
            }
            String html = httpGet(url, headers);
            String csrf = StringUtils.substringBetween(html, "name=\"_csrf\" value=\"", "\"/>");
            String relayState = StringUtils.substringBetween(html, "name=\"relayState\" value=\"", "\"/>");
            String hmac = StringUtils.substringBetween(html, "name=\"hmac\" value=\"", "\"/>");

            // Authenticate: Username
            logger.trace("{}: OAuth input: User", config.vehicle.vin);
            headers.clear();
            headers.put("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            headers.put(HttpHeader.USER_AGENT.toString(),
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
            headers.put("Referer",
                    "https://identity.vwgroup.io/signin-service/v1/signin/09b6cbec-cd19-4589-82fd-363dfa8c24da@apps_vw-dilab_com?relayState=1306273173f6e83fc92191ebf1b44c69cbaab41f");
            Map<String, String> data = new LinkedHashMap<>();
            data.put("_csrf", csrf);
            data.put("relayState", relayState);
            data.put("hmac", hmac);
            data.put("email", URLEncoder.encode(config.account.user, UTF_8));
            httpPost(CNAPI_OAUTH_IDENTIFIER_URL, headers, data, "", false);

            // Authenticate: Password
            logger.trace("{}: OAuth input: Password", config.vehicle.vin);
            url = CNAPI_OAUTH_BASE_URL + lastHttpFields.get("Location"); // Signin URL
            headers.clear();
            headers.put(HttpHeader.ACCEPT.toString(), "application/json, text/plain, */*");
            headers.put(HttpHeader.CONTENT_TYPE.toString(), "application/json;charset=UTF-8");
            headers.put(HttpHeader.USER_AGENT.toString(),
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
            html = httpGet(url, headers);
            csrf = StringUtils.substringBetween(html, "name=\"_csrf\" value=\"", "\"/>");
            relayState = StringUtils.substringBetween(html, "name=\"relayState\" value=\"", "\"/>");
            hmac = StringUtils.substringBetween(html, "name=\"hmac\" value=\"", "\"/>");

            headers.clear();
            headers.put("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            headers.put(HttpHeader.USER_AGENT.toString(),
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
            headers.put("Referer",
                    "https://identity.vwgroup.io/signin-service/v1/signin/09b6cbec-cd19-4589-82fd-363dfa8c24da@apps_vw-dilab_com?relayState=1306273173f6e83fc92191ebf1b44c69cbaab41f");
            data.clear();
            data.put("_csrf", csrf);
            data.put("relayState", relayState);
            data.put("hmac", hmac);
            data.put("email", URLEncoder.encode(config.account.user, UTF_8));
            data.put("password", URLEncoder.encode(config.account.password, UTF_8));
            httpPost(CNAPI_OAUTH_AUTHENTICATE_URL, headers, data, "", false);

            url = lastHttpFields.get("Location"); // Continue URL
            httpGet(url, headers);

            url = lastHttpFields.get("Location"); // Consent URL
            html = httpGet(url, headers);

            url = lastHttpFields.get("Location"); // Signin Callback URL
            html = httpGet(url, headers);

            url = lastHttpFields.get("Location"); // Signin Callback URL
            String authCode = StringUtils.substringAfter(url, "&code=");
            data.clear();
            data.put("client_id", "09b6cbec-cd19-4589-82fd-363dfa8c24da@apps_vw-dilab_com");
            data.put("grant_type", "authorization_code");
            data.put("code", authCode);
            data.put("redirect_uri", "myaudi:///");
            data.put("response_type", "token id_token");
            String json = httpPost(CNAPI_AUDI_TOKEN_URL, headers, data, "", false);
            logger.trace("{}: OAuth successful", config.vehicle.vin);

            // process token
            CNApiToken token = gson.fromJson(json, CNApiToken.class);
            if ((token.accessToken == null) || token.accessToken.isEmpty()) {
                throw new CarNetException("Authentication failed: Unable to get access token!");
            }
            brandToken = new CarNetToken(token);
            return brandToken.accessToken;
        } catch (UnsupportedEncodingException e) {
            throw new CarNetException("Login failed", e);
        }
    }

    private String createVwToken() throws CarNetException {
        if (!vwToken.isExpired()) {
            return vwToken.accessToken;
        }
        createBrandToken();

        // "User-Agent": "okhttp/3.7.0",
        // "X-App-Version": "3.14.0",
        // "X-App-Name": "myAudi",
        // "X-Client-Id": "77869e21-e30a-4a92-b016-48ab7d3db1d8",
        // "Host": "mbboauth-1d.prd.ece.vwg-connect.com",
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(CNAPI_HEADER_VERS, "3.14.0");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(CNAPI_HEADER_CLIENTID, "77869e21-e30a-4a92-b016-48ab7d3db1d8");
        headers.put(CNAPI_HEADER_HOST, "mbboauth-1d.prd.ece.vwg-connect.com");
        headers.put(HttpHeader.ACCEPT.toString(), "*/*");
        Map<String, String> data = new TreeMap<>();
        data.put("grant_type", "id_token");
        // data.put("token", idToken.idToken);
        data.put("token", brandToken.idToken);
        data.put("scope", "sc2:fal");

        String json = httpPost(CNAPI_URL_GET_SEC_TOKEN, headers, data, "", false);
        CNApiToken token = gson.fromJson(json, CNApiToken.class);
        if ((token.accessToken == null) || token.accessToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        vwToken = new CarNetToken(token);
        return vwToken.accessToken;
    }

    public @Nullable String getVehicleRights() throws CarNetException {
        try {
            String url = CNAPI_AUDIURL_OPERATIONS;
            Map<String, String> headers = fillActionHeaders("", createVwToken());
            String json = httpGet(url, headers);
            return json;
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public @Nullable String getHomeReguionUrl() throws CarNetException {
        try {
            if (!config.vehicle.homeRegionUrl.isEmpty()) {
                return config.vehicle.homeRegionUrl;
            }
            String url = CNAPI_VWURL_HOMEREGION.replace("{2}", config.vehicle.vin);
            Map<String, String> headers = fillActionHeaders("", createVwToken());
            String json = httpGet(url, headers);
            CarNetHomeRegion region = gson.fromJson(json, CarNetHomeRegion.class);
            config.vehicle.homeRegionUrl = StringUtils.substringBefore(region.homeRegion.baseUri.content, "/api")
                    + "/fs-car/";
            return config.vehicle.homeRegionUrl;
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {

        }
        return null;
    }

    private String createSecurityToken(String service, String action) throws CarNetException {
        if (config.vehicle.pin.isEmpty()) {
            throw new CarNetException("No SPIN is confirgured, can't perform authentication");
        }

        Iterator<CarNetToken> it = securityTokens.iterator();
        while (it.hasNext()) {
            CarNetToken stoken = it.next();
            if (stoken.service.equals(service) && stoken.isValid()) {
                return stoken.securityToken;
            }
        }

        String accessToken = createVwToken();

        // "User-Agent": "okhttp/3.7.0",
        // "X-App-Version": "3.14.0",
        // "X-App-Name": "myAudi",
        // "Accept": "application/json",
        // "Authorization": "Bearer " + self.vwToken.get("access_token"),
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(CNAPI_HEADER_VERS, "3.14.0");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);

        // Build Hash: SHA512(SPIN+Challenge)
        String url = "https://mal-1a.prd.ece.vwg-connect.com/api/rolesrights/authorization/v2/vehicles/"
                + config.vehicle.vin.toUpperCase() + "/services/" + service + "/operations/" + action
                + "/security-pin-auth-requested";
        String json = httpGet(url, headers, accessToken);
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
        CarNetToken securityToken = new CarNetToken(gson.fromJson(json, CNApiToken.class));
        if (securityToken.securityToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        logger.debug("securityToken granted successful!");
        synchronized (securityTokens) {
            securityToken.setService(service);
            if (!securityTokens.contains(securityToken)) {
                securityTokens.remove(securityToken);
            }
            securityTokens.add(securityToken);
        }
        return securityToken.securityToken;
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
        createBrandToken();
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
        return position;
    }

    public CarNetVehiclePosition getStoredPosition() throws CarNetException {
        String json = httpGet(CNAPI_VWURL_STORED_POS);
        CarNetVehiclePosition position = gson.fromJson(json, CarNetVehiclePosition.class);
        return position;
    }

    public @Nullable String getDestinations() throws CarNetException {
        try {
            String json = httpGet(CNAPI_URI_DESTINATIONS);
            CarNetDestinations destinations = null;
            if (!json.isEmpty()) {
                destinations = gson.fromJson(json, CarNetDestinations.class);
                if ((destinations != null) && (destinations.destinations != null)
                        && destinations.destinations.destination.size() > 0) {
                    return json;
                }
                return json;
            }
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public @Nullable String getHistory() throws CarNetException {
        try {
            String json = httpGet(CNAPI_URI_HISTORY);
            // String json = httpGet(CNAPI_URI_HISTORY, fillMmiHeaders());
            // CarNetHistory history = gson.fromJson(json, CarNetHistory.class);
            // Validate.notNull(history, "Unable to get vehicle history!");
            // return history;
            return json;
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public @Nullable CarNetClimaterStatus getClimaterStatus() throws CarNetException {
        try {
            // String json = httpGet(CNAPI_VWURL_CLIMATE_STATUS, fillMmiHeaders());
            String json = callApi(CNAPI_VWURL_CLIMATE_STATUS, "climaterStatus");
            if (json != null) {
                CNClimater cs = gson.fromJson(json, CNClimater.class);
                return cs.climater;
            }
        } catch (Exception e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        }
        return null;
    }

    public @Nullable String getClimaterTimer() throws CarNetException {
        try {
            String json = callApi(CNAPI_URI_CLIMATER_TIMER, "climaterTimer");
            return json;
        } catch (Exception e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        }
        return null;
    }

    public @Nullable CarNetChargerStatus getChargerStatus() throws CarNetException {
        String json = callApi(CNAPI_URI_CHARGER_STATUS, "chargerStatus");
        if (json != null) {
            CNChargerInfo ci = gson.fromJson(json, CNChargerInfo.class);
            return ci.charger;
        }
        return null;
    }

    public @Nullable CarNetTripData getTripData(String type) throws CarNetException {
        String json = "";
        try {
            String action = "list";
            // Map<String, String> headers = fillActionHeaders("", createVwToken());

            String url = CNAPI_VWURL_TRIP_DATA.replace("{3}", type).replace("{4}", action);
            json = httpGet(url, fillAppHeaders());
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {
        }

        if (json.isEmpty()) {
            json = loadJson("tripData" + type);
        }
        if (json != null) {
            return gson.fromJson(json, CarNetTripData.class);
        }
        return null;
    }

    public Map<String, String> fillAppHeaders() throws CarNetException {
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT.toString(), CNAPI_HEADER_USER_AGENT);
        headers.put(CNAPI_HEADER_APP, xappName);
        headers.put(CNAPI_HEADER_VERS, xappVersion);
        headers.put(HttpHeader.ACCEPT_CHARSET.toString(), StandardCharsets.UTF_8.toString());
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);
        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + createVwToken());
        headers.put("If-None-Match", "none");
        return headers;
    }

    public @Nullable String getPersonalData() throws CarNetException {
        if (/* isBrandAudi() || */ isBrandGo()) {
            return null; // not supported for Audi vehicles
        }

        /*
         * url: "https://customer-profile.apps.emea.vwapps.io/v1/customers/" + this.config.userid + "/personalData",
         * headers: {
         * "user-agent": "okhttp/3.7.0",
         * "X-App-version": this.xappversion,
         * "X-App-name": this.xappname,
         * authorization: "Bearer " + this.config.atoken,
         * accept: "application/json",
         * Host: "customer-profile.apps.emea.vwapps.io",
         * },
         */
        String json = "{}";
        try {
            String url = "https://customer-profile.apps.emea.vwapps.io/v1/customers/"
                    + UrlEncoded.encodeString(config.account.user) + "/personalData";
            // Map<String, String> headers = fillActionHeaders("", createBrandToken());
            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeader.USER_AGENT.toString(), CNAPI_HEADER_USER_AGENT);
            headers.put(CNAPI_HEADER_APP, xappName);
            headers.put(CNAPI_HEADER_VERS, xappVersion);
            headers.put(HttpHeader.AUTHORIZATION.toString(), createVwToken());
            // headers.put(HttpHeader.ACCEPT_CHARSET.toString(), StandardCharsets.UTF_8.toString());
            headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);
            headers.put(HttpHeader.HOST.toString(), "customer-profile.apps.emea.vwapps.io");
            json = httpGet(url, headers, createVwToken());
            return json;
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public @Nullable CarNetOperationList getOperationList() {
        try {
            // String url = CNAPI_VWURL_OPERATIONS + config.vehicle.vin;
            Map<String, String> headers = fillActionHeaders("", createVwToken());
            String json = httpGet(CNAPI_VWURL_OPERATIONS, headers);
            if (logger.isDebugEnabled()) {
                try {
                    logger.debug("Dave service list to {}/carnetServices.json", System.getProperty("user.dir"));
                    FileWriter myWriter = new FileWriter("carnetServices.json");
                    myWriter.write(json);
                    myWriter.close();
                } catch (IOException e) {
                }
            }

            CNOperationList operationList = gson.fromJson(json, CNOperationList.class);
            return operationList.operationList;
        } catch (CarNetException e) {

        }
        return null;
    }

    public CarNetServiceAvailability getServiceAvailability(CarNetOperationList operation) throws CarNetException {
        CarNetServiceAvailability serviceStatus = new CarNetServiceAvailability();
        for (CarNetServiceInfo si : operation.serviceInfo) {
            // Check service enabled status, maybe we need also to check serviceEol
            boolean enabled = si.serviceStatus.status.equalsIgnoreCase("Enabled")
                    && (!si.licenseRequired || si.cumulatedLicense.status.equalsIgnoreCase("ACTIVATED"));
            switch (si.serviceId) {
                case CNAPI_SERVICE_VEHICLE_STATUS:
                    serviceStatus.statusData = enabled;
                    break;
                case CNAPI_SERVICE_REMOTELOCK:
                    serviceStatus.rlu = enabled;
                case CNAPI_SERVICE_CLIMATER:
                    serviceStatus.clima = enabled;
                    break;
                case CNAPI_SERVICE_CHARGER:
                    serviceStatus.charger = enabled;
                    break;
                case CNAPI_SERVICE_CARFINDER:
                    serviceStatus.carFinder = enabled;
                    break;
                case CNAPI_SERVICE_DESTINATIONS:
                    serviceStatus.destinations = enabled;
                    break;
                case CNAPI_SERVICE_TRIPDATA:
                    serviceStatus.tripData = enabled;
                    break;
            }
        }
        return serviceStatus;
    }

    public @Nullable String getVehicleUsers() throws CarNetException {
        try {
            Map<String, String> headers = fillActionHeaders();
            String json = request(HttpMethod.GET, "usermanagement/users/v1/{0}/{1}/vehicles/{2}/", "", headers, "",
                    config.vehicle.vin, "");
            return json;
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public void lockDoor(boolean lock) throws CarNetException {
        final String action = lock ? CNAPI_RLU_LOCK : CNAPI_RLU_UNLOCK;
        Map<String, String> headers = fillActionHeaders("application/vnd.vwg.mbb.RemoteLockUnlock_v1_0_0+xml",
                createSecurityToken(CNAPI_SERVICE_REMOTELOCK, action));
        String data = "<?xml version=\"1.0\" encoding= \"UTF-8\" ?><rluAction xmlns=\"http://audi.de/connect/rlu\">"
                + "<action>" + (lock ? "lock" : "unlock") + "</action></rluAction>";
        String json = httpPost("https://msg.volkswagen.de/fs-car/bs/rlu/v1/{0}/{1}/vehicles/{2}/actions", headers, data,
                "");
        queuePendingAction(json, CNAPI_SERVICE_REMOTELOCK, action);
    }

    public void controlPreHeating(boolean start) throws CarNetException {
        final String action = start ? "startPreHeating" : "stopPreHeating";
        Map<String, String> headers = fillActionHeaders("application/vnd.vwg.mbb.ClimaterAction_v1_0_0+xml",
                createSecurityToken(CNAPI_SERVICE_PREHEATING, CNAPI_RHEATING_ACTION));
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<performAction xmlns=\"http://audi.de/connect/rs\"><quickstart><active>" + action
                + "</active></quickstart></performAction>";
        String json = httpPost(
                "https://msg.volkswagen.de/fs-car/bs/climatisation/v1/{0}/{1}/vehicles/{2}/climater/actions", headers,
                data, "");
        queuePendingAction(json, CNAPI_SERVICE_CLIMATISATION, action);
    }

    public void controlClimater(boolean start) throws CarNetException {
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

    public boolean checkRluSuccessful(String vin, String requestId) {
        logger.debug("{}: Checking for RLU status, requestId={}", vin, requestId);
        return checkRequestSuccessful(
                "https://msg.volkswagen.de/fs-car/bs/rlu/v1/{0}/{1}/vehicles/{2}/requests/" + requestId + "/status");
    }

    public @Nullable String getPois() {
        return callApi("\"{0}/{1}/vehicles/{2}/pois", "");
    }

    public @Nullable String getUserInfo() {
        return callApi("core/auth/v1/{0}/{1}/userInfo", "");
    }

    public @Nullable CarNetPairingInfo getPairingStatus() {
        String json = callApi(CNAPI_URI_GET_USERINFO, "");
        if (json != null) {
            CNPairingInfo pi = gson.fromJson(json, CNPairingInfo.class);
            return pi.pairingInfo;
        }
        return null;
    }

    public @Nullable CarNetVehicleData getVehicleManagementInfo() {
        String json = callApi(CNAPI_URI_VEHICLE_MANAGEMENT, "");
        if (json != null) {
            CNVehicleData vd = gson.fromJson(json, CNVehicleData.class);
            return vd.vehicleData;
        }
        return null;
    }

    public @Nullable CarNetRluHistory getRluActionHistory() {
        String json = callApi(CNAPI_URL_RLU_ACTIONS, "rluActionHistory");
        if (json != null) {
            CNEluActionHistory ah = gson.fromJson(json, CNEluActionHistory.class);
            return ah.actionsResponse;
        }
        return null;
    }

    public @Nullable String getMyDestinationsFeed(String userId) {
        return callApi("destinationfeedservice/mydestinations/v1/{0}/{1}/vehicles/{2}/users/{3}/destinations", "");
    }

    public @Nullable String getUserNews() {
        return callApi("https://msg.volkswagen.de/api/news/myfeeds/v1/vehicles/{2}/users/{3}/", "");
    }

    public @Nullable String getTripStats(String tripType) {
        String json = callApi("bs/tripstatistics/v1/{0}/{1}/vehicles/{2}/tripdata/" + tripType + "?newest", "");
        return json;
    }

    private @Nullable String callApi(String uri, String testData) {
        try {
            return httpGet(uri);
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {
        }
        return loadJson(testData);
    }

    private @Nullable String loadJson(String filename) {
        if (filename.isEmpty()) {
            return null;
        }
        try {
            StringBuffer result = new StringBuffer();
            String path = System.getProperty("user.dir") + "/userdata/";
            File myObj = new File(path + filename + ".json");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                result.append(line);
            }
            myReader.close();
            return result.toString();
        } catch (IOException e) {
        }
        return null;
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
        createBrandToken();
        // String auth = MessageFormat.format("{0} {2}", brandToken.authType, CNAPI_AUTH_AUDI_VERS,
        // brandToken.accessToken);
        String auth = MessageFormat.format("Bearer ", brandToken.accessToken);
        headers.put(HttpHeader.USER_AGENT.toString(), CNAPI_HEADER_USER_AGENT);
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_EREMOTE);
        headers.put(CNAPI_HEADER_VERS, CNAPI_HEADER_VERS_VALUE);
        headers.put(HttpHeader.AUTHORIZATION.toString(), auth);
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);
        // nextRedirect = true;
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

        // String bearer = brandToken.authType + " " + brandToken.authVersion + " " + createBrandToken();
        // headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + bearer);
        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + createBrandToken());

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
        headers.put(HttpHeader.ACCEPT_CHARSET.toString(), StandardCharsets.UTF_8.toString());
        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + token);
        // headers.put(HttpHeader.CONTENT_TYPE.toString(), CNAPI_CONTENTT_FORM_URLENC);
        headers.put("If-None-Match", "");
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
        if (!contentType.isEmpty()) {
            headers.put(HttpHeader.CONTENT_TYPE.toString(), contentType);
        }
        headers.put(HttpHeader.ACCEPT.toString(),
                "application/json, application/vnd.vwg.mbb.ChargerAction_v1_0_0+xml,application/vnd.volkswagenag.com-error-v1+xml,application/vnd.vwg.mbb.genericError_v1_0_2+xml,application/vnd.vwg.mbb.RemoteStandheizung_v2_0_0+xml,application/vnd.vwg.mbb.genericError_v1_0_2+xml,application/vnd.vwg.mbb.RemoteLockUnlock_v1_0_0+xml,application/vnd.vwg.mbb.operationList_v3_0_2+xml,application/vnd.vwg.mbb.genericError_v1_0_2+xml,*/*");
        headers.put(HttpHeader.ACCEPT_CHARSET.toString(), StandardCharsets.UTF_8.toString());

        headers.put(HttpHeader.AUTHORIZATION.toString(), "Bearer " + token);
        headers.put(HttpHeader.HOST.toString(), "msg.volkswagen.de");
        if (!securityToken.isEmpty()) {
            headers.put("x-mbbSecToken", securityToken);
        }
        return headers;
    }

    private Map<String, String> fillRefreshHeaders() {
        Map<String, String> headers = new TreeMap<String, String>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(CNAPI_HEADER_APP, xappName);
        headers.put(CNAPI_HEADER_VERS, xappVersion);
        headers.put(HttpHeader.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded");
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);
        headers.put("X-Client-Id", xClientId);
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
        // Map<String, String> headers = !uri.contains(".volkswagen.") ? fillBrandHeaders() : fillActionHeaders();
        Map<String, String> headers = fillActionHeaders();
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
            logger.debug("HTTP {} {}, data={}", request.getMethod(), request.getURI(), data);
            logger.trace("  Headers: {}", request.getHeaders());
            request.followRedirects(nextRedirect);
            nextRedirect = false;
            ContentResponse contentResponse = request.send();
            apiResult = new CarNetApiResult(contentResponse);
            int code = contentResponse.getStatus();
            String response = contentResponse.getContentAsString().replaceAll("\t", "").replaceAll("\r\n", "").trim();
            lastHttpFields = contentResponse.getHeaders();

            // validate response, API errors are reported as Json
            logger.trace("HTTP Response: {}", response);
            logger.trace("  Headers: {}", lastHttpFields);
            if (response.contains("\"error\":")) {
                throw new CarNetException("Authentication failed", apiResult);
            }
            if ((code != HttpStatus.OK_200) && (code != HttpStatus.ACCEPTED_202) && (code != HttpStatus.FOUND_302)
                    && (code != HttpStatus.SEE_OTHER_303)) {
                throw new CarNetException("API Call failed (HTTP" + code + ")", apiResult);
            }
            if (response.isEmpty() && (code != HttpStatus.FOUND_302) && (code != HttpStatus.SEE_OTHER_303)) {
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
        String path = MessageFormat.format(uriTemplate, config.account.brand, config.account.country, vin,
                config.vehicle.userId);
        if (!uriTemplate.contains("://")) { // not a full URL
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
        String base = getBaseUrl();
        return base.endsWith("/") && path.startsWith("/") ? base + path : base + "/" + path;
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

    private void initBrandData() {
        // Initialize your adapter here

        if (isBrandAudi()) {
            urlCountry = "DE";
            clientId = "mmiconnect_android";
            xClientId = "77869e21-e30a-4a92-b016-48ab7d3db1d8";
            authScope = "openid profile email mbb offline_access mbbuserid myaudi selfservice:read selfservice:write";
            responseType = "token id_token";
            xappVersion = "3.14.0";
            xappName = "myAudi";
        } else if (isBrandVW()) {
            urlCountry = "DE";
            clientId = "9496332b-ea03-4091-a224-8c746b885068%40apps_vw-dilab_com";
            xClientId = "38761134-34d0-41f3-9a73-c4be88d7d337";
            authScope = "openid%20profile%20mbb%20email%20cars%20birthdate%20badge%20address%20vin";
            redirect = "carnet%3A%2F%2Fidentity-kit%2Flogin";
            xrequest = "de.volkswagen.carnet.eu.eremote";
            responseType = "id_token%20token%20code";
            xappName = "eRemote";
            xappVersion = "5.1.2";
        } else if (isBrandSkoda()) {
            urlCountry = "CZ";
            clientId = "7f045eee-7003-4379-9968-9355ed2adb06%40apps_vw-dilab_com";
            xClientId = "28cd30c6-dee7-4529-a0e6-b1e07ff90b79";
            authScope = "openid%20profile%20phone%20address%20cars%20email%20birthdate%20badge%20dealers%20driversLicense%20mbb";
            redirect = "skodaconnect%3A%2F%2Foidc.login%2F";
            xrequest = "cz.skodaauto.connect";
            responseType = "code%20id_token";
            xappVersion = "3.2.6";
            xappName = "cz.skodaauto.connect";
        } else if (isBrandGo()) {
            clientId = "ac42b0fa-3b11-48a0-a941-43a399e7ef84@apps_vw-dilab_com";
            xClientId = "";
            authScope = "openid%20profile%20address%20email%20phone";
            redirect = "vwconnect%3A%2F%2Fde.volkswagen.vwconnect%2Foauth2redirect%2Fidentitykit";
            responseType = "code";
        }
    }

    private boolean isBrandAudi(String brand) {
        return brand.equalsIgnoreCase(CNAPI_BRAND_AUDI);
    }

    private boolean isBrandVW(String brand) {
        return brand.equalsIgnoreCase(CNAPI_BRAND_VW);
    }

    private boolean isBrandSkoda(String brand) {
        return brand.equalsIgnoreCase(CNAPI_BRAND_SKODA);
    }

    private boolean isBrandGo(String brand) {
        return brand.equalsIgnoreCase(CNAPI_BRAND_GO);
    }

    private boolean isBrandAudi() {
        return isBrandAudi(config.account.brand);
    }

    private boolean isBrandVW() {
        return isBrandVW(config.account.brand);
    }

    private boolean isBrandSkoda() {
        return isBrandSkoda(config.account.brand);
    }

    private boolean isBrandGo() {
        return isBrandGo(config.account.brand);
    }
}
