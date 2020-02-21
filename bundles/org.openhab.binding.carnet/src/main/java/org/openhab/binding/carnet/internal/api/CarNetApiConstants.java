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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link CarNetApiConstants} defines various Carnet API contants
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class CarNetApiConstants {

    public static final String CNAPI_BASE_URL_AUDI = "https://msg.audi.de/fs-car";
    public static final String CNAPI_BASE_URL_VW = "https://msg.volkswagen.de/fs-car";

    public static final String CNAPI_BRAND_AUDI = "Audi";
    public static final String CNAPI_BRAND_VW = "VW";
    public static final String CNAPI_BRAND_SKODE = "Skoda";

    // HTTP header attributes
    public static final String CNAPI_HEADER_TYPE = "Accept: application/json";
    public static final String CNAPI_HEADER_APP = "X-App-Name";
    public static final String CNAPI_HEADER_APP_VALUE = "eRemote";
    public static final String CNAPI_HEADER_VERS = "X-App-Version";
    public static final String CNAPI_HEADER_VERS_VALUE = "1.0.0";
    public static final String CNAPI_HEADER_USER_AGENT = "okhttp/2.3.0";
    public static final String CNAPI_AUTH_AUDI_VERS = "1";

    public static final String CNAPI_CONTENTT_FORM_URLENC = "application/x-www-form-urlencoded";
    public static final String CNAPI_ACCEPTT_JSON = "application/json";

    public static int CNAPI_TIMEOUT_MS = 30 * 1000;

    // URIs: {0}=brand, {1} = VIN
    public static final String CNAPI_URI_GET_TOKEN = "core/auth/v1/{0}/{1}/token";
    public static final String CNAPI_URI_VEHICLE_LIST = "usermanagement/users/v1/{0}/{1}/vehicles";
    public static final String CNAPI_URI_VEHICLE_DETAILS = "promoter/portfolio/v1/{0}/{1}/vehicle//{2}/carportdata";
    public static final String CNAPI_URI_VEHICLE_STATUS = "bs/vsr/v1/{0}/{1}/vehicles/{2}/status";
    public static final String CNAPI_URI_VEHICLE_POSITION = "bs/cf/v1/{0}/{1}/vehicles/{2}/position";
    public static final String CNAPI_URI_CHARGER_STATUS = "bs/batterycharge/v1/{0}/{1}/vehicles/{2}/charger";
    public static final String CNAPI_URI_DESTINATIONS = "destinationfeedservice/mydestinations/v1/{0}/{1}/vehicles/{2}/destinations";
    public static final String CNAPI_URI_CMD_HONK = "bs/rhf/v1/{0}/{1}/vehicles/{2}/honkAndFlash";
}
