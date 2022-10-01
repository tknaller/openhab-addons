/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
package org.openhab.binding.connectedcar.internal.api.skodae;

import static org.openhab.binding.connectedcar.internal.BindingConstants.CONTENT_TYPE_JSON;
import static org.openhab.binding.connectedcar.internal.api.ApiDataTypesDTO.*;
import static org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.*;
import static org.openhab.binding.connectedcar.internal.util.Helpers.fromJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.measure.IncommensurableException;
import javax.ws.rs.core.HttpHeaders;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.http.HttpHeader;
import org.openhab.binding.connectedcar.internal.api.ApiDataTypesDTO.VehicleDetails;
import org.openhab.binding.connectedcar.internal.api.ApiDataTypesDTO.VehicleStatus;
import org.openhab.binding.connectedcar.internal.api.ApiEventListener;
import org.openhab.binding.connectedcar.internal.api.ApiException;
import org.openhab.binding.connectedcar.internal.api.ApiHttpClient;
import org.openhab.binding.connectedcar.internal.api.ApiHttpMap;
import org.openhab.binding.connectedcar.internal.api.ApiWithOAuth;
import org.openhab.binding.connectedcar.internal.api.BrandAuthenticator;
import org.openhab.binding.connectedcar.internal.api.IdentityManager;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleList;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleList.SEVehicle;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleSettings.SEChargerSettings;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleSettings.SEChargerSettingsRequest;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleSettings.SEClimaZoneSettingsRequest;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleSettings.SEClimaterSettings;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData.SEVehicleStatus.SEChargerStatus;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData.SEVehicleStatus.SEClimaterStatus;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData.SEVehicleStatus.SEParkingPositionStatus;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData.SEVehicleStatus.SEVehicleStatusV2;
import org.openhab.binding.connectedcar.internal.handler.ThingHandlerInterface;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SkodaEApi} implements the Skoda-E API calls
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class SkodaEApi extends ApiWithOAuth implements BrandAuthenticator {
    private final Logger logger = LoggerFactory.getLogger(SkodaEApi.class);
    private Map<String, SEVehicle> vehicleData = new HashMap<>();

    public SkodaEApi(ThingHandlerInterface handler, ApiHttpClient httpClient, IdentityManager tokenManager,
            @Nullable ApiEventListener eventListener) {
        super(handler, httpClient, tokenManager, eventListener);
    }

    @Override
    public ArrayList<String> getVehicles() throws ApiException {
        ApiHttpMap params = crerateParameters();
        String json = callApi("", config.api.apiDefaultUrl + "/v2/garage/vehicles", params.getHeaders(),
                "getVehicleList", String.class);
        json = "{ \"data\":" + json + "}";
        SEVehicleList apiList = fromJson(gson, json, SEVehicleList.class);
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
        SEVehicleStatusData s = new SEVehicleStatusData();
        s.settings.charger = getChargerSettings();
        s.status.charger = getChargerStatus();
        s.settings.climater = getClimaterSettings();
        s.status.climatisation = getClimaterStatus();
        s.status.vehicleStatus = getVehicleStatusV2();
        s.status.parkingPosition = getParkingPosition();
        return new VehicleStatus(s);
    }

    public SEChargerStatus getChargerStatus() throws ApiException {
        return getValues(SESERVICE_CHARGING, SEENDPOINT_STATUS, SEChargerStatus.class);
    }

    public SEChargerSettings getChargerSettings() throws ApiException {
        return getValues(SESERVICE_CHARGING, SEENDPOINT_SETTINGS, SEChargerSettings.class);
    }

    public SEClimaterStatus getClimaterStatus() throws ApiException {
        return getValues(SESERVICE_CLIMATISATION, SEENDPOINT_STATUS, SEClimaterStatus.class);
    }

    public SEClimaterSettings getClimaterSettings() throws ApiException {
        return getValues(SESERVICE_CLIMATISATION, SEENDPOINT_SETTINGS, SEClimaterSettings.class);
    }

    public SEParkingPositionStatus getParkingPosition() throws ApiException {
        return getValues2(SESERVICE_POSITIONVEHICLES, SEENDPOINT_PARKINGPOSITION, SEParkingPositionStatus.class);
    }

    public SEVehicleStatusV2 getVehicleStatusV2() throws ApiException {
        return getValuesV2(SESERVICE_VEHICLESTATUS, "", SEVehicleStatusV2.class);
    }

    @Override
    public String refreshVehicleStatus() {
        // For now it's unclear if there is an API call to request a status update from the vehicle
        return API_REQUEST_SUCCESSFUL;
    }

    private <T> T getValues(String service, String type, Class<T> classOfT) throws ApiException {
        ApiHttpMap params = crerateParameters();
        String json = callApi("", "v1/" + service + "/{2}/" + type, params.getHeaders(),
                "getValues_" + service + "." + type, String.class);
        return fromJson(gson, json, classOfT);
    }

    private <T> T getValues2(String service, String type, Class<T> classOfT) throws ApiException {
        ApiHttpMap params = crerateParameters2();
        String json = callApi("", "v1/" + service + "/{2}/" + type, params.getHeaders(),
                "getValues_" + service + "." + type, String.class);
        return fromJson(gson, json, classOfT);
    }

    private <T> T getValuesV2(String service, String type, Class<T> classOfT) throws ApiException {
        ApiHttpMap params = crerateParameters();
        String json = callApi("", "v2/" + service + "/{2}/" + type, params.getHeaders(),
                "getValues_" + service + "." + type, String.class);
        return fromJson(gson, json, classOfT);
    }

    @Override
    public String controlClimater(boolean start, String heaterSource) throws ApiException {
        String action = (start ? "Start" : "Stop");
        var body = "{\"type\":\"" + action + "\"}";
        return sendAction(SESERVICE_CLIMATISATION, "", body);
    }

    @Override
    public String controlWindowHeating(boolean start) throws ApiException {
        SEClimaZoneSettingsRequest request = new SEClimaZoneSettingsRequest();
        request.airConditioningSettings = getClimaterSettings();
        request.airConditioningSettings.windowHeatingEnabled = start;
        request.type = "UpdateSettings";
        String payload = gson.toJson(request);
        return sendSettings(SESERVICE_CLIMATISATION, payload);
    }

    @Override
    public String controlClimaterTemp(double tempC, String heaterSource) throws ApiException {
        try {
            SEClimaZoneSettingsRequest request = new SEClimaZoneSettingsRequest();
            request.airConditioningSettings = getClimaterSettings();
            Double tempK = SIUnits.CELSIUS.getConverterToAny(Units.KELVIN).convert(tempC);
            request.airConditioningSettings.targetTemperatureInKelvin = tempK;
            request.type = "UpdateSettings";
            String payload = gson.toJson(request);
            return sendSettings(SESERVICE_CLIMATISATION, payload);
        } catch (IncommensurableException e) {
            throw new ApiException("Unable to convert temperature", e);
        }
    }

    @Override
    public String controlCharger(boolean start) throws ApiException {
        String action = (start ? "Start" : "Stop");
        var body = "{\"type\":\"" + action + "\"}";
        return sendAction(SESERVICE_CHARGING, "", body);
    }

    @Override
    public String controlMaxCharge(int maxCurrent) throws ApiException {
        SEChargerSettings settings = getChargerSettings();
        // status.chargingSettings.maxChargeCurrentAC = "" + maxCurrent;
        String payload = gson.toJson(settings);
        return sendSettings(SESERVICE_CHARGING, payload);
    }

    @Override
    public String controlTargetChgLevel(int targetLevel) throws ApiException {
        SEChargerSettingsRequest request = new SEChargerSettingsRequest();
        request.chargingSettings = getChargerSettings();
        request.chargingSettings.targetStateOfChargeInPercent = targetLevel;
        request.type = "UpdateSettings";
        String payload = gson.toJson(request);
        return sendSettings(SESERVICE_CHARGING, payload);
    }

    private String sendAction(String service, String action, String body) throws ApiException {
        ApiHttpMap headers = crerateParameters().header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON);
        String json = http.post("v1/" + service + "/operation-requests?vin={2}", headers.getHeaders(), body).response;
        return API_REQUEST_STARTED;
    }

    private String sendSettings(String service, String body) throws ApiException {
        ApiHttpMap headers = crerateParameters().header(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON);
        String json = http.post("v1/" + service + "/operation-requests?vin={2}", headers.getHeaders(), body).response;
        return API_REQUEST_STARTED;
    }

    private ApiHttpMap crerateParameters() throws ApiException {
        /*
         * accept: "application/json",
         * "content-type": "application/json;charset=utf-8",
         * "user-agent": "OneConnect/000000023 CFNetwork/978.0.7 Darwin/18.7.0",
         * "accept-language": "de-de",
         * authorization: "Bearer " + this.config.atoken,
         */
        return new ApiHttpMap().header(HttpHeader.USER_AGENT, config.api.userAgent)
                .header(HttpHeaders.ACCEPT, CONTENT_TYPE_JSON).header(HttpHeader.ACCEPT_LANGUAGE, "de-de")
                .header(HttpHeader.HOST, "api.connect.skoda-auto.cz")
                .header(HttpHeader.AUTHORIZATION, "Bearer " + createAccessToken());
    }

    private ApiHttpMap crerateParameters2() throws ApiException {
        /*
         * accept: "application/json",
         * "content-type": "application/json;charset=utf-8",
         * "user-agent": "OneConnect/000000023 CFNetwork/978.0.7 Darwin/18.7.0",
         * "accept-language": "de-de",
         * authorization: "Bearer " + this.config.atoken,
         */
        return new ApiHttpMap().header(HttpHeader.USER_AGENT, config.prevoiusConfig.api.userAgent)
                .header(HttpHeaders.ACCEPT, CONTENT_TYPE_JSON).header(HttpHeader.ACCEPT_LANGUAGE, "de-de")
                .header(HttpHeader.HOST, "api.connect.skoda-auto.cz")
                .header(HttpHeader.AUTHORIZATION, "Bearer " + createAccessToken2());
    }
}
