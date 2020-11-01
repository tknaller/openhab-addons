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
import static org.openhab.binding.carnet.internal.CarNetUtils.*;
import static org.openhab.binding.carnet.internal.api.CarNetApiConstants.*;
import static org.openhab.binding.carnet.internal.api.CarNetHttpClient.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpHeader;
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
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNRoleRights;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNRoleRights.CarNetUserRoleRights;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNVehicleData;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNVehicleData.CarNetVehicleData;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetActionResponse;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetDestinations;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetHomeRegion;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetOidcConfig;
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
    private final Gson gson = new Gson();

    private boolean initialzed = false;
    private CarNetCombinedConfig config = new CarNetCombinedConfig();
    private CarNetHttpClient http;

    private CarNetToken idToken = new CarNetToken();
    private CarNetToken brandToken = new CarNetToken();
    private CarNetToken vwToken = new CarNetToken();
    private CopyOnWriteArrayList<CarNetToken> securityTokens = new CopyOnWriteArrayList<CarNetToken>();
    private Map<String, CarNetPendingRequest> pendingRequest = new ConcurrentHashMap<>();

    public CarNetApi() {
        this.http = new CarNetHttpClient();
    }

    public CarNetApi(HttpClient httpClient) {
        logger.debug("Initializing CarNet API");
        // httpClient.setFollowRedirects(true);
        this.http = new CarNetHttpClient(httpClient);
    }

    public void setConfig(CarNetAccountConfiguration config) {
        logger.debug("Setting up CarNet API for brand {} ({}), user {}", config.brand, config.country, config.user);
        this.config.account = config;
        http.setConfig(this.config);
        initBrandData();
    }

    public void setConfig(CarNetVehicleConfiguration config) {
        this.config.vehicle = config;
        http.setConfig(this.config);
    }

    public void initialize() throws CarNetException {
        config.oidcConfig = getOidcConfig();
        http.setConfig(this.config);
        refreshTokens();
        initialzed = true;
    }

    public boolean isInitialized() {
        return initialzed;
    }

    private CarNetOidcConfig getOidcConfig() throws CarNetException {
        // get OIDC confug
        String url = "https://app-api.live-my.audi.com/myaudiappidk/v1/openid-configuration";
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(HttpHeader.ACCEPT.toString(),
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        headers.put(HttpHeader.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded");
        String json = http.get(url, headers);
        config.oidcDate = http.getResponseDate();
        return gson.fromJson(json, CarNetOidcConfig.class);
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

    public CarNetUserRoleRights getRoleRights() throws CarNetException {
        String url = "/rolesrights/permissions/v1/{0}/{1}/vehicles/{2}/fetched-role";
        String json = http.get(url, fillAppHeaders());
        CNRoleRights r = gson.fromJson(json, CNRoleRights.class);
        return r.role;
    }

    public String getHomeReguionUrl() throws CarNetException {
        try {
            if (!config.vehicle.homeRegionUrl.isEmpty()) {
                return config.vehicle.homeRegionUrl;
            }
            String url = CNAPI_VWURL_HOMEREGION.replace("{2}", config.vehicle.vin);
            Map<String, String> headers = fillActionHeaders("", createVwToken());
            String json = http.get(url, headers);
            CarNetHomeRegion region = gson.fromJson(json, CarNetHomeRegion.class);
            config.vehicle.homeRegionUrl = substringBefore(region.homeRegion.baseUri.content, "/api") + "/fs-car/";
            return config.vehicle.homeRegionUrl;
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        }
        return "";
    }

    public CarNetVehicleList getVehicles() throws CarNetException {
        String json = http.get(CNAPI_URI_VEHICLE_LIST, fillAppHeaders());
        CarNetVehicleList vehiceList = gson.fromJson(json, CarNetVehicleList.class);
        return vehiceList;
    }

    public CarNetVehicleDetails getVehicleDetails(String vin) throws CarNetException {
        String json = http.get(CNAPI_URI_VEHICLE_DETAILS, vin, fillAppHeaders());
        CarNetVehicleDetails details = gson.fromJson(json, CarNetVehicleDetails.class);
        return details;
    }

    public CarNetVehicleStatus getVehicleStatus() throws CarNetException {
        String json = callApi(CNAPI_URI_VEHICLE_STATUS, "getVehicleStatus");
        CarNetVehicleStatus status = gson.fromJson(json, CarNetVehicleStatus.class);
        return status;
    }

    public CarNetVehiclePosition getVehiclePosition() throws CarNetException {
        String json = callApi(CNAPI_URI_VEHICLE_POSITION, "getVehiclePosition");
        CarNetVehiclePosition position = gson.fromJson(json, CarNetVehiclePosition.class);
        return position;
    }

    public CarNetVehiclePosition getStoredPosition() throws CarNetException {
        String json = callApi(CNAPI_VWURL_STORED_POS, "getStoredPosition");
        CarNetVehiclePosition position = gson.fromJson(json, CarNetVehiclePosition.class);
        return position;
    }

    public String getDestinations() throws CarNetException {
        String json = callApi(CNAPI_URI_DESTINATIONS, "getDestinations");
        CarNetDestinations destinations = gson.fromJson(json, CarNetDestinations.class);
        if ((destinations.destinations != null) && destinations.destinations.destination.size() > 0) {
            return json;
        }
        return json;
    }

    public String getHistory() throws CarNetException {
        String json = callApi(CNAPI_URI_HISTORY, "getHistory");
        return json;
    }

    public CarNetClimaterStatus getClimaterStatus() throws CarNetException {
        String json = callApi(CNAPI_VWURL_CLIMATE_STATUS, "climaterStatus");
        CNClimater cs = gson.fromJson(json, CNClimater.class);
        return cs.climater;
    }

    public String getClimaterTimer() throws CarNetException {
        String json = callApi(CNAPI_URI_CLIMATER_TIMER, "climaterTimer");
        return json;
    }

    public CarNetChargerStatus getChargerStatus() throws CarNetException {
        String json = callApi(CNAPI_URI_CHARGER_STATUS, "chargerStatus");
        CNChargerInfo ci = gson.fromJson(json, CNChargerInfo.class);
        return ci.charger;
    }

    public @Nullable CarNetTripData getTripData(String type) throws CarNetException {
        String json = "";
        try {
            String action = "list";
            String url = CNAPI_VWURL_TRIP_DATA.replace("{3}", type).replace("{4}", action);
            json = http.get(url, fillAppHeaders());
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
            Map<String, String> headers = new HashMap<>();
            headers.put(HttpHeader.USER_AGENT.toString(), CNAPI_HEADER_USER_AGENT);
            headers.put(CNAPI_HEADER_APP, config.xappName);
            headers.put(CNAPI_HEADER_VERS, config.xappVersion);
            headers.put(HttpHeader.AUTHORIZATION.toString(), createVwToken());
            // headers.put(HttpHeader.ACCEPT_CHARSET.toString(), StandardCharsets.UTF_8.toString());
            headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);
            headers.put(HttpHeader.HOST.toString(), "customer-profile.apps.emea.vwapps.io");
            json = http.get(url, headers, createVwToken());
            return json;
        } catch (CarNetException e) {
            logger.debug("{}: API call failed: {}", config.vehicle.vin, e.toString());
        } catch (Exception e) {

        }
        return null;
    }

    public @Nullable CarNetOperationList getOperationList() {
        try {
            String json = http.get(CNAPI_VWURL_OPERATIONS, fillAppHeaders());
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

    public @Nullable String getVehicleUsers() throws CarNetException {
        String json = callApi("https://msg.volkswagen.de/fs-car/bs//uic/v1/vin/{2}/users", "getVehicleUsers");
        return json;
    }

    public void lockDoor(boolean lock) throws CarNetException {
        final String action = lock ? CNAPI_RLU_LOCK : CNAPI_RLU_UNLOCK;
        Map<String, String> headers = fillActionHeaders("application/vnd.vwg.mbb.RemoteLockUnlock_v1_0_0+xml",
                createSecurityToken(CNAPI_SERVICE_REMOTELOCK, action));
        String data = "<?xml version=\"1.0\" encoding= \"UTF-8\" ?><rluAction xmlns=\"http://audi.de/connect/rlu\">"
                + "<action>" + (lock ? "lock" : "unlock") + "</action></rluAction>";
        String json = http.post(CNAPI_URL_RLU_ACTIONS, headers, data, "");
        queuePendingAction(json, CNAPI_SERVICE_REMOTELOCK, action);
    }

    public void controlPreHeating(boolean start) throws CarNetException {
        final String action = start ? "startPreHeating" : "stopPreHeating";
        Map<String, String> headers = fillActionHeaders("application/vnd.vwg.mbb.ClimaterAction_v1_0_0+xml",
                createSecurityToken(CNAPI_SERVICE_PREHEATING, CNAPI_RHEATING_ACTION));
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<performAction xmlns=\"http://audi.de/connect/rs\"><quickstart><active>" + action
                + "</active></quickstart></performAction>";
        String json = http.post(
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
        String json = http.post(
                "https://msg.volkswagen.de/fs-car/bs/climatisation/v1/{0}/{1}/vehicles/{2}/climater/actions", headers,
                data);
        queuePendingAction(json, CNAPI_SERVICE_CLIMATISATION, action);
    }

    public void controlWindowHeating(boolean start) throws CarNetException {
        final String action = start ? "startWindowHeating" : "stopWindowHeating";

        Map<String, String> headers = fillActionHeaders("application/vnd.vwg.mbb.ClimaterAction_v1_0_0+xml", "");
        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><action><type>" + action + "</type></action>";
        String json = http.post(
                "https://msg.volkswagen.de/fs-car/bs/climatisation/v1/{0}/{1}/vehicles/{2}/climater/actions", headers,
                data, "");
        queuePendingAction(json, CNAPI_SERVICE_CLIMATISATION, action);
    }

    public boolean checkRluSuccessful(String vin, String requestId) {
        logger.debug("{}: Checking for RLU status, requestId={}", vin, requestId);
        return checkRequestSuccessful(
                "https://msg.volkswagen.de/fs-car/bs/rlu/v1/{0}/{1}/vehicles/{2}/requests/" + requestId + "/status");
    }

    public String getPois() throws CarNetException {
        return callApi("{0}/{1}/vehicles/{2}/pois", "");
    }

    public String getUserInfo() throws CarNetException {
        return callApi("core/auth/v1/{0}/{1}/userInfo", "");
    }

    public CarNetPairingInfo getPairingStatus() throws CarNetException {
        String json = callApi(CNAPI_URI_GET_USERINFO, "");
        CNPairingInfo pi = gson.fromJson(json, CNPairingInfo.class);
        return pi.pairingInfo;
    }

    public CarNetVehicleData getVehicleManagementInfo() throws CarNetException {
        String json = callApi(CNAPI_URI_VEHICLE_MANAGEMENT, "");
        CNVehicleData vd = gson.fromJson(json, CNVehicleData.class);
        return vd.vehicleData;
    }

    public CarNetRluHistory getRluActionHistory() throws CarNetException {
        String json = callApi(CNAPI_URL_RLU_ACTIONS, "rluActionHistory");
        CNEluActionHistory ah = gson.fromJson(json, CNEluActionHistory.class);
        return ah.actionsResponse;
    }

    public String getMyDestinationsFeed(String userId) throws CarNetException {
        return callApi("destinationfeedservice/mydestinations/v1/{0}/{1}/vehicles/{2}/users/{3}/destinations", "");
    }

    public String getUserNews() throws CarNetException {
        // for now not working
        return callApi("https://msg.volkswagen.de/api/news/myfeeds/v1/vehicles/{2}/users/{3}/", "");
    }

    public String getTripStats(String tripType) throws CarNetException {
        String json = callApi("bs/tripstatistics/v1/{0}/{1}/vehicles/{2}/tripdata/" + tripType + "?newest", "");
        return json;
    }

    private String callApi(String uri, String function) throws CarNetException {
        try {
            return http.get(uri, fillAppHeaders());
        } catch (CarNetException e) {
            CarNetApiResult res = e.getApiResult();
            logger.debug("{}: API call {} failed: HTTP {}, {}", config.vehicle.vin, function, res.httpCode,
                    e.toString());
            String json = loadJson(function);
            if (json == null) {
                throw e;
            }
            return json;
        }
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

    private void initBrandData() {
        // Initialize your adapter here

        if (isBrandAudi()) {
            config.oidcConfigUrl = "https://app-api.live-my.audi.com/myaudiappidk/v1/openid-configuration";
            config.urlCountry = "DE";
            config.clientId = "09b6cbec-cd19-4589-82fd-363dfa8c24da@apps_vw-dilab_com";
            config.xClientId = "77869e21-e30a-4a92-b016-48ab7d3db1d8";
            // config.authScope = "openid myaudi mbb mbbuserid profile email offline_access selfservice:read";
            config.authScope = "address profile badge birthdate birthplace nationalIdentifier nationality profession email vin phone nickname name picture mbb gallery openid";
            // address%20profile%20badge%20birthdate%20birthplace%20nationalIdentifier%20nationality%20profession%20email%20vin%20phone%20nickname%20name%20picture%20mbb%20gallery%20openid"
            config.redirect_uri = "myaudi:///";
            config.responseType = "token id_token";
            config.xappVersion = "3.14.0";
            config.xappName = "myAudi";
        } else if (isBrandVW()) {
            config.urlCountry = "DE";
            config.clientId = "9496332b-ea03-4091-a224-8c746b885068%40apps_vw-dilab_com";
            // clientId = "c7c15e7f-135c-4bd3-9875-63838616509f@apps_vw-dilab_com"; // VW IOS App
            config.xClientId = "38761134-34d0-41f3-9a73-c4be88d7d337";
            config.authScope = "openid%20profile%20mbb%20email%20cars%20birthdate%20badge%20address%20vin";
            config.redirect_uri = "carnet://identity-kit/Flogin";
            config.xrequest = "de.volkswagen.carnet.eu.eremote";
            config.responseType = "id_token token code";
            config.xappName = "eRemote";
            config.xappVersion = "5.1.2";
        } else if (isBrandSkoda()) {
            config.urlCountry = "CZ";
            config.clientId = "7f045eee-7003-4379-9968-9355ed2adb06%40apps_vw-dilab_com";
            config.xClientId = "28cd30c6-dee7-4529-a0e6-b1e07ff90b79";
            config.authScope = "openid%20profile%20phone%20address%20cars%20email%20birthdate%20badge%20dealers%20driversLicense%20mbb";
            config.redirect_uri = "skodaconnect://oidc.login/";
            config.xrequest = "cz.skodaauto.connect";
            config.responseType = "code%20id_token";
            config.xappVersion = "3.2.6";
            config.xappName = "cz.skodaauto.connect";
        } else if (isBrandGo()) {
            config.clientId = "ac42b0fa-3b11-48a0-a941-43a399e7ef84@apps_vw-dilab_com";
            config.xClientId = "";
            config.authScope = "openid%20profile%20address%20email%20phone";
            config.redirect_uri = "vwconnect://de.volkswagen.vwconnect/oauth2redirect/identitykit";
            config.responseType = "code";
        }
        http.setConfig(config);
    }

    public static boolean isBrandAudi(String brand) {
        return brand.equalsIgnoreCase(CNAPI_BRAND_AUDI);
    }

    public static boolean isBrandVW(String brand) {
        return brand.equalsIgnoreCase(CNAPI_BRAND_VW);
    }

    public static boolean isBrandSkoda(String brand) {
        return brand.equalsIgnoreCase(CNAPI_BRAND_SKODA);
    }

    public static boolean isBrandGo(String brand) {
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

    public String createBrandToken() throws CarNetException {
        String url = "";
        try {
            if (!brandToken.isExpired()) {
                return brandToken.accessToken;
            }

            logger.debug("{}: Logging in, account={}", config.vehicle.vin, config.account.user);
            Map<String, String> headers = new LinkedHashMap<>();
            Map<String, String> data = new LinkedHashMap<>();
            headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
            headers.put(HttpHeader.ACCEPT.toString(),
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            headers.put(HttpHeader.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded");

            String urlClientId = urlEncode(config.clientId);
            logger.debug("{}: OAuth: Get signin form", config.vehicle.vin);
            String state = UUID.randomUUID().toString();
            String nonce = generateNonce();
            // url = CNAPI_OAUTH_AUTHORIZE_URL + "?response_type=code" + "&client_id=" + urlClientId
            url = CNAPI_OAUTH_AUTHORIZE_URL + "?response_type=" + urlEncode(config.responseType) + "&client_id="
                    + urlEncode(config.clientId) + "&redirect_uri=" + urlEncode(config.redirect_uri) + "&scope="
                    + urlEncode(config.authScope) + "&state=" + state + "&nonce=" + nonce
                    + "&prompt=login&ui_locales=de-DE%20de";
            http.get(url, headers);
            url = http.getRedirect(); // Signin URL
            // error=consent_required&error_description=
            if (url.isEmpty()) {
                throw new CarNetException("Unable to get signin URL");
            }
            if (url.contains("error=consent_require")) {
                String message = URLDecoder.decode(url);
                message = substringBefore(substringAfter(message, "&error_description="), "&");
                throw new CarNetException(
                        "Login failed, Consent missing. Login to Web App and give consent: " + message);
            }
            String html = http.get(url, headers);
            url = http.getRedirect(); // Signin URL
            String csrf = substringBetween(html, "name=\"_csrf\" value=\"", "\"/>");
            String relayState = substringBetween(html, "name=\"relayState\" value=\"", "\"/>");
            String hmac = substringBetween(html, "name=\"hmac\" value=\"", "\"/>");

            // Authenticate: Username
            logger.trace("{}: OAuth input: User", config.vehicle.vin);
            url = CNAPI_OAUTH_BASE_URL + "/signin-service/v1/" + config.clientId + "/login/identifier";
            data.put("_csrf", csrf);
            data.put("relayState", relayState);
            data.put("hmac", hmac);
            data.put("email", URLEncoder.encode(config.account.user, UTF_8));
            http.post(url, headers, data, false);

            // Authenticate: Password
            logger.trace("{}: OAuth input: Password", config.vehicle.vin);
            url = CNAPI_OAUTH_BASE_URL + http.getRedirect(); // Signin URL
            html = http.get(url, headers);
            csrf = substringBetween(html, "name=\"_csrf\" value=\"", "\"/>");
            relayState = substringBetween(html, "name=\"relayState\" value=\"", "\"/>");
            hmac = substringBetween(html, "name=\"hmac\" value=\"", "\"/>");

            logger.trace("{}: OAuth input: Authenticate", config.vehicle.vin);
            url = CNAPI_OAUTH_BASE_URL + "/signin-service/v1/" + config.clientId + "/login/authenticate";
            data.clear();
            data.put("email", URLEncoder.encode(config.account.user, UTF_8));
            data.put("password", URLEncoder.encode(config.account.password, UTF_8));
            data.put("_csrf", csrf);
            data.put("relayState", relayState);
            data.put("hmac", hmac);
            http.post(url, headers, data, false);
            url = http.getRedirect(); // Continue URL

            String userId = "";
            String authCode = "";
            String id_token = "";
            String accessToken = "";
            String expires_in = "";
            int count = 10;
            while (count-- > 0) {
                html = http.get(url, headers);
                url = http.getRedirect(); // Continue URL
                if (url.contains("&user_id=")) {
                    userId = getUrlParm(url, "user_id");
                }
                if (url.contains("&code=")) {
                    authCode = getUrlParm(url, "code");
                    break; // that's what we are looking for
                }
                if (url.contains("&id_token=")) {
                    id_token = getUrlParm(url, "id_token");
                }
                if (url.contains("&expires_in=")) {
                    expires_in = getUrlParm(url, "expires_in");
                }
                if (url.contains("&access_token=")) {
                    accessToken = getUrlParm(url, "access_token");
                    break; // that's what we are looking for
                }
            }

            CNApiToken token;
            String json = "";
            if (!id_token.isEmpty()) {
                logger.trace("{}: OAuth successful, idToken and accessToken retrieved", config.vehicle.vin);
                idToken = new CarNetToken(id_token, accessToken, "bearer", Integer.parseInt(expires_in, 10));
            } else {
                if (authCode.isEmpty()) {
                    logger.debug("{}: Unable to obtain authCode, last url={}, last response: {}", config.vehicle.vin,
                            url, html);
                    throw new CarNetException("Unable to complete OAuth, check credentials");
                }

                logger.trace("{}: OAuth successful, obtain ID token (auth code={})", config.vehicle.vin, authCode);
                headers.clear();
                headers.put(HttpHeader.ACCEPT.toString(), "application/json, text/plain, */*");
                headers.put(HttpHeader.CONTENT_TYPE.toString(), "application/json");
                headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");

                long tsC = parseDate(config.oidcDate);
                long n = System.currentTimeMillis();
                long ts1 = System.currentTimeMillis() - tsC;
                long ts2 = System.currentTimeMillis();
                long ts = ts1 + ts2;
                String s = ((Long) (ts / 100000)).toString();
                headers.put("X-QMAuth", "v1:934928ef:" + s);

                data.clear();
                data.put("client_id", config.clientId);
                data.put("grant_type", "authorization_code");
                data.put("code", authCode);
                data.put("redirect_uri", config.redirect_uri);
                data.put("response_type", "token id_token");
                json = http.post(CNAPI_AUDI_TOKEN_URL, headers, data, true);

                // process token
                token = gson.fromJson(json, CNApiToken.class);
                if ((token.accessToken == null) || token.accessToken.isEmpty()) {
                    throw new CarNetException("Authentication failed: Unable to get access token!");
                }

                idToken = new CarNetToken(token);
            }
            logger.debug("{}: OAuth successful", config.vehicle.vin);

            logger.debug("{}: Get Audi Token", config.vehicle.vin);
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            data.clear();
            data.put("config", "myaudi");
            data.put("grant_type", "id_token");
            data.put("stage", "live");
            data.put("token", idToken.accessToken);
            json = http.post("https://app-api.live-my.audi.com/azs/v1/token", headers, data, true);
            token = gson.fromJson(json, CNApiToken.class);

            brandToken = new CarNetToken(token);
            return brandToken.accessToken;
        } catch (CarNetException e) {
            logger.info("{}: Login failed: {}", config.vehicle.vin, e.toString());
        } catch (UnsupportedEncodingException e) {
            throw new CarNetException("Login failed", e);
        } catch (Exception e) {
            logger.info("{}: Login failed: {}", config.vehicle.vin, e.getMessage());
        }
        return "";
    }

    public String createVwToken() throws CarNetException {
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
        // https://github.com/TA2k/ioBroker.vw-connect/blob/master/main.js
        // VW: clientId = "9496332b-ea03-4091-a224-8c746b885068%40apps_vw-dilab_com";
        // VW: xclientId = "38761134-34d0-41f3-9a73-c4be88d7d337";
        // VW: xappversion = "5.1.2";
        // VW: xappname = "eRemote";
        headers.put(CNAPI_HEADER_HOST, "mbboauth-1d.prd.ece.vwg-connect.com");
        headers.put(HttpHeader.ACCEPT.toString(), "*/*");
        Map<String, String> data = new TreeMap<>();
        data.put("grant_type", "id_token");
        // data.put("token", idToken.idToken);
        data.put("token", idToken.idToken);
        data.put("scope", "sc2:fal");

        String json = http.post(CNAPI_URL_GET_SEC_TOKEN, headers, data, false);
        CNApiToken token = gson.fromJson(json, CNApiToken.class);
        if ((token.accessToken == null) || token.accessToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        vwToken = new CarNetToken(token);
        return vwToken.accessToken;
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
        String json = http.get(url, headers, accessToken);
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
        json = http.post(
                "https://mal-1a.prd.ece.vwg-connect.com/api/rolesrights/authorization/v2/security-pin-auth-completed",
                headers, data);
        CarNetToken securityToken = new CarNetToken(gson.fromJson(json, CNApiToken.class));
        if (securityToken.securityToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        logger.debug("securityToken granted successful!");
        synchronized (securityTokens) {
            securityToken.setService(service);
            if (securityTokens.contains(securityToken)) {
                securityTokens.remove(securityToken);
            }
            securityTokens.add(securityToken);
        }
        return securityToken.securityToken;
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

    public boolean refreshToken(String brand, CarNetToken token) throws CarNetException {
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
                data.put("client_id", config.clientId);
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
                data.put("client_id", config.clientId);
                data.put("scope", "openid+profile+address+email+phone");
                data.put("grant_type", "refresh_token");
                data.put("refresh_token", rtoken);
            } else {
                data.put("refresh_token=", rtoken);
                url = CNAPI_URL_DEF_GET_TOKEN;
                rtoken = brandToken.refreshToken; // not sure if that is the correct default one
            }

            try {
                String json = http.post(url, http.fillRefreshHeaders(), data, false);
                return true;
            } catch (CarNetException e) {
                logger.debug("{}: Unable to refresh token {} - {}", config.vehicle.vin, token, e.getApiResult());
            }
        }

        // Invalidate token
        token.invalidate();
        return false;
    }

    private Map<String, String> fillActionHeaders(String contentType, String securityToken) throws CarNetException {
        return CarNetHttpClient.fillActionHeaders(new HashMap<>(), contentType, createVwToken(), securityToken);
    }

    private Map<String, String> fillActionHeaders() throws CarNetException {
        return fillActionHeaders("", "");
    }

    public Map<String, String> fillAppHeaders() throws CarNetException {
        return http.fillAppHeaders(new HashMap<>(), createVwToken());
    }

    public Map<String, String> fillBrandHeaders() throws CarNetException {
        return http.fillBrandHeaders(new HashMap<>(), createBrandToken());
    }
}
