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
import static org.openhab.binding.carnet.internal.CarNetUtils.substringBefore;
import static org.openhab.binding.carnet.internal.api.CarNetApiConstants.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.util.UrlEncoded;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNChargerInfo;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNChargerInfo.CarNetChargerStatus;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNClimater;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNClimater.CarNetClimaterStatus;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNDestinations;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNDestinations.CarNetDestinationList;
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
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetHomeRegion;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetOidcConfig;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetServiceAvailability;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetTripData;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleDetails;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleList;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehiclePosition;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleStatus;
import org.openhab.binding.carnet.internal.config.CarNetCombinedConfig;
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
    private CarNetHttpClient http = new CarNetHttpClient();

    private CarNetTokenManager tokenManager = new CarNetTokenManager();

    private Map<String, CarNetPendingRequest> pendingRequest = new ConcurrentHashMap<>();

    public CarNetApi() {
    }

    public CarNetApi(CarNetHttpClient httpClient, CarNetTokenManager tokenManager) {
        logger.debug("Initializing CarNet API");
        this.http = httpClient;
        this.tokenManager = tokenManager;
    }

    public void setConfig(CarNetCombinedConfig config) {
        logger.debug("Setting up CarNet API for brand {} ({}), user {}", config.account.brand, config.account.country,
                config.account.user);
        this.config = config;
        http.setConfig(this.config);
        initBrandData();
    }

    public void setConfig(CarNetVehicleConfiguration config) {
        this.config.vehicle = config;
        http.setConfig(this.config);
    }

    public void initialize() throws CarNetException {
        http.setConfig(this.config);
        config.oidcConfig = getOidcConfig();
        tokenManager.refreshTokens(config);
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
                // "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
                "application/json");
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

    public String getVehicleHealthReport() throws CarNetException {
        String json = callApi("bs/vhs/v2/vehicle/{2}", "healthReport");
        return json;
    }

    public String getRecommendedMaintenance() throws CarNetException {
        String json = callActionApi(
                "myaudi/recommended-maintenance-tasks/v1/vehicles/{2}/recommended-maintenance-tasks",
                "recommendedMaintenanceTasks");
        return json;
    }

    public String getServiceBook() throws CarNetException {
        try {
            return http.get("https://mal-1a.prd.ece.vwg-connect.com/myaudi/service-book/v1/vehicles/{2}/service-book",
                    CarNetHttpClient.fillActionHeaders(new HashMap<>(), "application/json;charset=UTF-8",
                            createVwToken(), ""));
        } catch (CarNetException e) {
            CarNetApiResult res = e.getApiResult();
            logger.debug("{}: API call {} failed: HTTP {}, {}", config.vehicle.vin, "getServiceBook", res.httpCode,
                    e.toString());
            String json = loadJson("getServiceBook");
            if (json == null) {
                throw e;
            }
            return json;
        }
        /*
         * String json = callActionApi(
         * "https://mal-1a.prd.ece.vwg-connect.com/myaudi/service-book/v1/vehicles/{2}/service-book",
         * "getServiceBook");
         * return json;
         */
    }

    public CarNetVehiclePosition getStoredPosition() throws CarNetException {
        String json = callApi(CNAPI_VWURL_STORED_POS, "getStoredPosition");
        CarNetVehiclePosition position = gson.fromJson(json, CarNetVehiclePosition.class);
        return position;
    }

    public CarNetDestinationList getDestinations() throws CarNetException {
        String json = callApi(CNAPI_URI_DESTINATIONS, "getDestinations");
        if (json.equals("{\"destinations\":null}")) {
            // This services returns an empty list rather than http 403 when access is not allowed
            // in this case try to load test data
            String test = loadJson("getDestinations");
            if (test != null) {
                json = test;
            }
        }
        CNDestinations dest = gson.fromJson(json, CNDestinations.class);
        if (dest.destinations != null) {
            return dest.destinations;
        }

        CarNetDestinationList empty = new CarNetDestinationList();
        empty.destination = new ArrayList<>();
        return empty; // return empty list
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
        if (isBrandAudi() || isBrandGo()) {
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

    public @Nullable CarNetOperationList getOperationList() throws CarNetException {
        String json = http.get(CNAPI_VWURL_OPERATIONS, fillAppHeaders());
        if (logger.isDebugEnabled()) {
            try {
                logger.debug("Save service list to {}/carnetServices.json", System.getProperty("user.dir"));
                FileWriter myWriter = new FileWriter("carnetServices.json");
                myWriter.write(json);
                myWriter.close();
            } catch (IOException e) {
            }
        }

        CNOperationList operationList = gson.fromJson(json, CNOperationList.class);
        return operationList.operationList;
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
        // return callApi("{0}/{1}/vehicles/{2}/pois", "");
        return callApi("https://msg.audi.de/audi/b2c/poinav/v1/vehicles/{2}/pois", "getPois");
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

    private String callActionApi(String uri, String function) throws CarNetException {
        try {
            return http.get(uri, fillActionHeaders());
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

    private Map<String, String> fillActionHeaders(String contentType, String securityToken) throws CarNetException {
        return CarNetHttpClient.fillActionHeaders(new HashMap<>(), contentType, createVwToken(), securityToken);
    }

    private Map<String, String> fillActionHeaders() throws CarNetException {
        return fillActionHeaders("", "");
    }

    public Map<String, String> fillAppHeaders() throws CarNetException {
        return http.fillAppHeaders(new HashMap<>(), createVwToken());
    }

    private String createVwToken() throws CarNetException {
        return tokenManager.createVwToken(config);
    }

    private String createSecurityToken(String service, String action) throws CarNetException {
        return tokenManager.createSecurityToken(config, service, action);
    }

    public boolean refreshTokens() throws CarNetException {
        return tokenManager.refreshTokens(config);
    }
}
