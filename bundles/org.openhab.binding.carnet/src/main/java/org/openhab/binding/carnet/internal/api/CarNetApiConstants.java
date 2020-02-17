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
    public static final String CNAPI_HEADER_APP = "X-App-Name: eRemote";
    public static final String CNAPI_HEADER_APP_VERS = "X-App-Version: 1.0.0";
    public static final String CNAPI_HEADER_AGENT = "User-Agent: okhttp/2.3.0";
    public static final String CNAPI_HEADER_AUTH_AUDI = "Authorization: AudiAuth 1 {0}"; // {0} = Token

    public static final String CNAPI_CONTENT_FORM_URLENC = "application/x-www-form-urlencoded";

    public static int CNAPI_TIMEOUT_MS = 30 * 1000;

    // URIs: {0}=brand, {1} = VIN
    public static final String CNAPI_URI_GET_TOKEN = "core/auth/v1/{0}/DE/token";
    public static final String CNAPI_URI_VEHICLE_LIST = "usermanagement/users/v1/{0}/DE/vehicles";
    public static final String CNAPI_URI_VEHICLE_DETAILS = "promoter/portfolio/v1/{0}/DE/vehicle//{1}/carportdata";
    public static final String CNAPI_URI_VEHICLE_STATUS = "bs/vsr/v1/{0}/DE/vehicles/{1}/status";
    public static final String CNAPI_URI_VEHICLE_POSITION = "bs/cf/v1/{0}/DE/vehicles/{1}/position";
    public static final String CNAPI_URI_CHARGER_STATUS = "bs/batterycharge/v1/{0}/DE/vehicles/{1}/charger";
    public static final String CNAPI_URI_CMD_HONK = "bs/rhf/v1/{0}/DE/vehicles/{1}/honkAndFlash";

    public static final String CNID_MAINTENANCE_INTERVAL_DISTANCE_TO_OIL_CHANGE = "0x0203010001";
    public static final String CNID_MAINTENANCE_INTERVAL_TIME_TO_OIL_CHANGE = "0x0203010002";
    public static final String CNID_MAINTENANCE_INTERVAL_DISTANCE_TO_INSPECTION = "0x0203010003";
    public static final String CNID_MAINTENANCE_INTERVAL_TIME_TO_INSPECTION = "0x0203010004";
    public static final String CNID_WARNING_OIL_CHANGE = "0x0203010005";
    public static final String CNID_MAINTENANCE_INTERVAL_ALARM_INSPECTION = "0x0203010006";
    public static final String CNID_MAINTENANCE_INTERVAL_MONTHLY_MILEAGE = "0x0203010007";
    public static final String CNID_MAINTENANCE_INTERVAL_AD_BLUE_RANGE = "0x02040C0001";
    public static final String CNID_IL_LEVEL_AMOUNT_IN_LITERS = "0x0204040001";
    public static final String CNID_OIL_LEVEL_MINIMUM_WARNING = "0x0204040002";
    public static final String CNID_OIL_LEVEL_DIPSTICK_PERCENTAGE = "0x0204040003";
    public static final String CNID_LIGHT_STATUS = "0x0301010001";
    public static final String CNID_TOTAL_RANGE = "0x0301030005";
    public static final String CNID_FUEL_LEVEL_IN_PERCENTAGE = "0x030103000A";
    public static final String CNID_15CNG_LEVEL_IN_PERCENTAGE = "0x030103000D";
    public static final String CNID_LOCK_STATE_LEFT_FRONT_DOOR = "0x0301040001";
    public static final String CNID_OPEN_STATE_LEFT_FRONT_DOOR = "0x0301040002";
    public static final String CNID_SAFETY_STATE_LEFT_FRONT_DOOR = "0x0301040003";
    public static final String CNID_LOCK_STATE_LEFT_REAR_DOOR = "0x0301040004";
    public static final String CNID_OPEN_STATE_LEFT_REAR_DOOR = "0x0301040005";
    public static final String CNID_SAFETY_STATE_LEFT_REAR_DOOR = "0x0301040006";
    public static final String CNID_LOCK_STATE_RIGHT_FRONT_DOOR = "0x0301040007";
    public static final String CNID_OPEN_STATE_RIGHT_FRONT_DOOR = "0x0301040008";
    public static final String CNID_SAFETY_STATE_RIGHT_FRONT_DOOR = "0x0301040009";
    public static final String CNID_LOCK_STATE_RIGHT_REAR_DOOR = "0x030104000A";
    public static final String CNID_OPEN_STATE_RIGHT_REAR_DOOR = "0x030104000B";
    public static final String CNID_SAFETY_STATE_RIGHT_REAR_DOOR = "0x030104000C";
    public static final String CNID_LOCK_STATE_TRUNK_LID = "0x030104000D";
    public static final String CNID_OPEN_STATE_TRUNK_LID = "0x030104000E";
    public static final String CNID_SAFETY_STATE_TRUNK_LID = "0x030104000F";
    public static final String CNID_LOCK_STATE_HOOD = "0x0301040010";
    public static final String CNID_OPEN_STATE_HOOD = "0x0301040011";
    public static final String CNID_SAFETY_STATE_HOOD = "0x0301040012";
    public static final String CNID_STATE_LEFT_FRONT_WINDOW = "0x0301050001";
    public static final String CNID_POSITION_LEFT_FRONT_WINDOW = "0x0301050002";
    public static final String CNID_STATE_LEFT_REAR_WINDOW = "0x0301050003";
    public static final String CNID_POSITION_LEFT_REAR_WINDOW = "0x0301050004";
    public static final String CNID_STATE_RIGHT_FRONT_WINDOW = "0x0301050005";
    public static final String CNID_POSITION_RIGHT_FRONT_WINDOW = "0x0301050006";
    public static final String CNID_STATE_RIGHT_REAR_WINDOW = "0x0301050007";
    public static final String CNID_POSITION_RIGHT_REAR_WINDOW = "0x0301050008";
    public static final String CNID_STATE_CONVERTIBLE_TOP = "0x0301050009";
    public static final String CNID_POSITION_CONVERTIBLE_TOP = "0x030105000A";
    public static final String CNID_STATE_SUN_ROOF_MOTOR_COVER = "0x030105000B";
    public static final String CNID_POSITION_SUN_ROOF_MOTOR_COVER = "0x030105000C";
    public static final String CNID_STATE_SUN_ROOF_REAR_MOTOR_COVER_3 = "0x030105000D";
    public static final String CNID_POSITION_SUN_ROOF_REAR_MOTOR_COVER_3 = "0x030105000E";
    public static final String CNID_STATE_SERVICE_FLAP = "0x030105000F";
    public static final String CNID_POSITION_SERVICE_FLAP = "0x0301050010";
    public static final String CNID_STATE_SPOILER = "0x0301050011";
    public static final String CNID_POSITION_SPOILER = "0x0301050012";
    public static final String CNID_UTC_TIME_STATUS = "0x0101010001";
    public static final String CNID_KILOMETER_STATUS = "0x0101010002";
    public static final String CNID_PRIMARY_RANGE = "0x0301030006";
    public static final String CNID_PRIMARY_DRIVE = "0x0301030007";
    public static final String CNID_SECONDARY_RANGE = "0x0301030008";
    public static final String CNID_SECONDARY_DRIVE = "0x0301030009";
    public static final String CNID_STATE_OF_CHARGE = "0x0301030002";
    public static final String CNID_TEMPERATURE_OUTSIDE = "0x0301020001";
    public static final String CNID_PARKING_BRAKE = "0x0301030001";
    public static final String CNID_TYRE_PRESSURE_LEFT_FRONT_CURRENT_VALUE = "0x0301060001";
    public static final String CNID_TYRE_PRESSURE_LEFT_FRONT_DESIRED_VALUE = "0x0301060002";
    public static final String CNID_TYRE_PRESSURE_LEFT_REAR_CURRENT_VALUE = "0x0301060003";
    public static final String CNID_TYRE_PRESSURE_LEFT_REAR_DESIRED_VALUE = "0x0301060004";
    public static final String CNID_TYRE_PRESSURE_RIGHT_FRONT_CURRENT_VALUE = "0x0301060005";
    public static final String CNID_TYRE_PRESSURE_RIGHT_FRONT_DESIRED_VALUE = "0x0301060006";
    public static final String CNID_TYRE_PRESSURE_RIGHT_REAR_CURRENT_VALUE = "0x0301060007";
    public static final String CNID_TYRE_PRESSURE_RIGHT_REAR_DESIRED_VALUE = "0x0301060008";
    public static final String CNID_TYRE_PRESSURE_SPARE_TYRE_CURRENT_VALUE = "0x0301060009";
    public static final String CNID_TYRE_PRESSURE_SPARE_TYRE_DESIRED_VALUE = "0x030106000A";
    public static final String CNID_TYRE_PRESSURE_LEFT_FRONT_TYRE_DIFFERENCE = "0x030106000B";
    public static final String CNID_TYRE_PRESSURE_LEFT_REAR_TYRE_DIFFERENCE = "0x030106000C";
    public static final String CNID_TYRE_PRESSURE_RIGHT_FRONT_TYRE_DIFFERENCE = "0x030106000D";
    public static final String CNID_TYRE_PRESSURE_RIGHT_REAR_TYRE_DIFFERENCE = "0x030106000E";
    public static final String CNID_TYRE_PRESSURE_SPARE_TYRE_DIFFERENCE = "0x030106000F";
}
