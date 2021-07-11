/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.openhab.binding.carnet.internal.api.skodaenyak;

import static org.openhab.binding.carnet.internal.BindingConstants.*;
import static org.openhab.binding.carnet.internal.CarUtils.fromJson;
import static org.openhab.binding.carnet.internal.api.skodaenyak.SEApiJsonDTO.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.http.HttpHeader;
import org.openhab.binding.carnet.internal.api.ApiBase;
import org.openhab.binding.carnet.internal.api.ApiDataTypesDTO.VehicleDetails;
import org.openhab.binding.carnet.internal.api.ApiDataTypesDTO.VehicleStatus;
import org.openhab.binding.carnet.internal.api.ApiEventListener;
import org.openhab.binding.carnet.internal.api.ApiException;
import org.openhab.binding.carnet.internal.api.ApiHttpClient;
import org.openhab.binding.carnet.internal.api.ApiHttpMap;
import org.openhab.binding.carnet.internal.api.TokenManager;
import org.openhab.binding.carnet.internal.api.brand.BrandAuthenticator;
import org.openhab.binding.carnet.internal.api.skodaenyak.SEApiJsonDTO.SEVehicleList;
import org.openhab.binding.carnet.internal.api.skodaenyak.SEApiJsonDTO.SEVehicleList.SEVehicle;
import org.openhab.binding.carnet.internal.api.skodaenyak.SEApiJsonDTO.SEVehicleStatusData.SEVehicleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SkodaEnyaqApi} implements the Skoda Enyaq API calls
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class SkodaEnyaqApi extends ApiBase implements BrandAuthenticator {
    private final Logger logger = LoggerFactory.getLogger(SkodaEnyaqApi.class);
    private Map<String, SEVehicle> vehicleData = new HashMap<>();

    public SkodaEnyaqApi(ApiHttpClient httpClient, TokenManager tokenManager,
            @Nullable ApiEventListener eventListener) {
        super(httpClient, tokenManager, eventListener);
    }

    @Override
    public ArrayList<String> getVehicles() throws ApiException {
        ApiHttpMap params = crerateParameters();
        SEVehicleList apiList = callApi("", config.api.apiDefaultUrl + "/v2/garage/vehicles", params.getHeaders(),
                "getVehicleList", SEVehicleList.class);
        ArrayList<String> list = new ArrayList<String>();
        for (SEVehicle vehicle : apiList.data) {
            list.add(vehicle.vin);
            vehicleData.put(vehicle.vin, vehicle);
        }
        return list;
    }

    @Override
    public VehicleDetails getVehicleDetails(String vin) throws ApiException {
        if (vehicleData.containsKey(vin)) {
            SEVehicle vehicle = vehicleData.get(vin);
            return new VehicleDetails(config, vehicle);
        } else {
            throw new ApiException("Unknown VIN: " + vin);
        }
    }

    @Override
    public String getApiUrl() {
        return config.api.apiDefaultUrl;
    }

    @Override
    public String getHomeReguionUrl() {
        return getApiUrl();
    }

    @Override
    public VehicleStatus getVehicleStatus() throws ApiException {
        return new VehicleStatus(getStatus());
    }

    private SEVehicleStatus getStatus() throws ApiException {
        String acStatus = getValues(SESERVICE_CLIMATISATION, SEENDPOINT_STATUS, String.class);
        String acSettings = getValues(SESERVICE_CLIMATISATION, SEENDPOINT_SETTINGS, String.class);
        String chStatus = getValues(SESERVICE_CHARGING, SEENDPOINT_STATUS, String.class);
        String chSettgins = getValues(SESERVICE_CHARGING, SEENDPOINT_SETTINGS, String.class);

        ApiHttpMap params = crerateParameters();
        SEVehicleStatus status = callApi("", config.api.apiDefaultUrl + "/v1/vehicle-status/{2}", params.getHeaders(),
                "getVehicleStatus", SEVehicleStatus.class);
        return status;
    }

    @Override
    public String refreshVehicleStatus() {
        // For now it's unclear if there is an API call to request a status update from the vehicle
        return API_REQUEST_SUCCESSFUL;
    }

    private <T> T getValues(String service, String type, Class<T> classOfT) throws ApiException {
        ApiHttpMap params = crerateParameters();
        params.header("api-key", "ok")/* .header("If-None-Match", "ok") */;
        String json = callApi("", config.api.apiDefaultUrl + "/v1/" + service + "/{2}/" + type, params.getHeaders(),
                "getValues_" + service + "." + type, String.class);
        return fromJson(gson, json, classOfT);
    }

    @Override
    public String controlClimater(boolean start, String heaterSource) throws ApiException {
        String action = (start ? "start" : "stop");
        return sendAction(SESERVICE_CLIMATISATION, action, "");
    }

    @Override
    public String controlClimaterTemp(double tempC, String heaterSource) throws ApiException {
        // try {
        SEVehicleStatus status = getStatus();

        /*
         * Double tempK = SIUnits.CELSIUS.getConverterToAny(Units.KELVIN).convert(tempC);
         * status.climatisationSettings.targetTemperature_C = tempC;
         * status.climatisationSettings.targetTemperature_K = tempK;
         */
        String payload = gson.toJson(status);
        return sendSettings(SESERVICE_CLIMATISATION, payload);
        // } catch (IncommensurableException e) {
        // throw new ApiException("Unable to convert temperature", e);
        // }
    }

    @Override
    public String controlWindowHeating(boolean start) throws ApiException {
        SEVehicleStatus status = getStatus();
        // status.climatisationSettings.windowHeatingEnabled = start;
        String payload = gson.toJson(status);
        return sendSettings(SESERVICE_CLIMATISATION, payload);
    }

    @Override
    public String controlCharger(boolean start) throws ApiException {
        String action = (start ? "start" : "stop");
        return sendAction(SESERVICE_CHARGING, action, "");
    }

    @Override
    public String controlMaxCharge(int maxCurrent) throws ApiException {
        SEVehicleStatus status = getStatus();
        // status.chargingSettings.maxChargeCurrentAC = "" + maxCurrent;
        String payload = gson.toJson(status);
        return sendSettings(SESERVICE_CHARGING, payload);
    }

    @Override
    public String controlTargetChgLevel(int targetLevel) throws ApiException {
        SEVehicleStatus status = getStatus();
        // status.chargingSettings.targetSOC_pct = targetLevel;
        String payload = gson.toJson(status);
        return sendSettings(SESERVICE_CHARGING, payload);
    }

    private String sendAction(String service, String action, String body) throws ApiException {
        ApiHttpMap headers = crerateParameters();
        String json = http.post(config.api.apiDefaultUrl + "/vehicles/{2}/" + service + "/" + action,
                headers.getHeaders(), body).response;
        return API_REQUEST_STARTED;
    }

    private String sendSettings(String service, String body) throws ApiException {
        ApiHttpMap headers = crerateParameters();
        String json = http.put(config.api.apiDefaultUrl + "/vehicles/{2}/" + service + "/settings",
                headers.getHeaders(), body).response;
        return API_REQUEST_STARTED;
    }

    @Override
    public void checkPendingRequests() {
    }

    public String getRequestStatus(String requestId, String rstatus) throws ApiException {
        return API_REQUEST_SUCCESSFUL;
    }

    private ApiHttpMap crerateParameters() throws ApiException {
        /*
         * accept: "application/json",
         * "content-type": "application/json;charset=utf-8",
         * "user-agent": "OneConnect/000000023 CFNetwork/978.0.7 Darwin/18.7.0",
         * "accept-language": "de-de",
         * authorization: "Bearer " + this.config.atoken,
         */
        return new ApiHttpMap().header(HttpHeaders.ACCEPT, CONTENT_TYPE_JSON)
                .header(HttpHeader.USER_AGENT, config.api.userAgent).header(HttpHeader.ACCEPT_LANGUAGE, "de-de")
                .header(HttpHeader.AUTHORIZATION, "Bearer " + createAccessToken());
    }
}
