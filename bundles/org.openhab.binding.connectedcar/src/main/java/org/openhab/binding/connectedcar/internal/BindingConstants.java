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
package org.openhab.binding.connectedcar.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Length;
import javax.measure.quantity.Power;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Time;
import javax.measure.quantity.Volume;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.library.unit.MetricPrefix;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link BindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class BindingConstants {

    public static final String BINDING_ID = "connectedcar";

    // List of all Thing Type UIDs
    public static final String THING_MYAUDI = "myaudi";
    public static final String THING_VOLKSWAGEN = "volkswagen";
    public static final String THING_VWID = "vwid";
    public static final String THING_VWGO = "wcgo";
    public static final String THING_VWWC = "wecharge";
    public static final String THING_SEAT = "seat";
    public static final String THING_SKODA = "skoda";
    public static final String THING_ENYAK = "enyak";

    public static final ThingTypeUID THING_TYPE_MYAUDI = new ThingTypeUID(BINDING_ID, THING_MYAUDI);
    public static final ThingTypeUID THING_TYPE_VW = new ThingTypeUID(BINDING_ID, THING_VOLKSWAGEN);
    public static final ThingTypeUID THING_TYPE_VWID = new ThingTypeUID(BINDING_ID, THING_VWID);
    public static final ThingTypeUID THING_TYPE_VWGO = new ThingTypeUID(BINDING_ID, THING_VWGO);
    public static final ThingTypeUID THING_TYPE_SKODA = new ThingTypeUID(BINDING_ID, THING_SKODA);
    public static final ThingTypeUID THING_TYPE_SEAT = new ThingTypeUID(BINDING_ID, THING_SEAT);
    public static final ThingTypeUID THING_TYPE_ENYAK = new ThingTypeUID(BINDING_ID, THING_ENYAK);

    public static final ThingTypeUID THING_TYPE_CNVEHICLE = new ThingTypeUID(BINDING_ID, "cnvehicle");
    public static final ThingTypeUID THING_TYPE_IDVEHICLE = new ThingTypeUID(BINDING_ID, "idvehicle");
    public static final ThingTypeUID THING_TYPE_GOPLUG = new ThingTypeUID(BINDING_ID, "goplug");
    public static final ThingTypeUID THING_TYPE_WCWALLBOX = new ThingTypeUID(BINDING_ID, "wcbox");
    public static final ThingTypeUID THING_TYPE_ENYAKVEHICLE = new ThingTypeUID(BINDING_ID, "enyakvehicle");
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.unmodifiableSet(Stream
            .of(THING_TYPE_MYAUDI, THING_TYPE_VW, THING_TYPE_VWID, THING_TYPE_VWGO, THING_TYPE_SKODA, THING_TYPE_ENYAK,
                    THING_TYPE_SEAT, THING_TYPE_CNVEHICLE, THING_TYPE_IDVEHICLE, THING_TYPE_ENYAKVEHICLE)
            .collect(Collectors.toSet()));

    // List of all ChannelGroups and Channels
    public static final String CHANNEL_GROUP_GENERAL = "general";
    public static final String CHANNEL_GENERAL_UPDATED = "lastUpdate";
    public static final String CHANNEL_GENERAL_LOCKED = "vehicleLocked";
    public static final String CHANNEL_GENERAL_MAINTREQ = "maintenanceRequired";
    public static final String CHANNEL_GENERAL_WINCLOSED = "windowsClosed";
    public static final String CHANNEL_GENERAL_TIRESOK = "tiresOk";
    public static final String CHANNEL_GENERAL_ACTION = "lastAction";
    public static final String CHANNEL_GENERAL_ACTION_STATUS = "lastActionStatus";
    public static final String CHANNEL_GENERAL_ACTION_PENDING = "lastActionPending";
    public static final String CHANNEL_GENERAL_RATELIM = "rateLimit";
    public static final String CHANNEL_GENERAL_TIMEINCAR = "timeInCar";

    // Group status
    public static final String CHANNEL_GROUP_STATUS = "status";
    public static final String CHANNEL_STATUS_PBRAKE = "parkingBrake";
    public static final String CHANNEL_STATUS_LIGHTS = "vehicleLights";
    public static final String CHANNEL_STATUS_ERROR = "error";

    // Group control
    public static final String CHANNEL_GROUP_CONTROL = "control";
    public static final String CHANNEL_CONTROL_UPDATE = "update";
    public static final String CHANNEL_CONTROL_LOCK = "lock";
    public static final String CHANNEL_CONTROL_CHARGER = "charge";
    public static final String CHANNEL_CONTROL_MAXCURRENT = "maxCurrent";
    public static final String CHANNEL_CONTROL_TARGETCHG = "targetChgLvl";
    public static final String CHANNEL_CONTROL_CLIMATER = "climater";
    public static final String CHANNEL_CONTROL_HEATSOURCE = "heaterSource";
    public static final String CHANNEL_CONTROL_WINHEAT = "windowHeat";
    public static final String CHANNEL_CONTROL_PREHEAT = "preHeater";
    public static final String CHANNEL_CONTROL_DURATION = "duration";
    public static final String CHANNEL_CONTROL_VENT = "ventilation";
    public static final String CHANNEL_CONTROL_FLASH = "flash";
    public static final String CHANNEL_CONTROL_HONKFLASH = "honkFlash";
    public static final String CHANNEL_CONTROL_HFDURATION = "hfDuration";

    public static final String CHANNEL_GROUP_LOCATION = "location";
    public static final String CHANNEL_LOCATTION_GEO = "locationPosition";
    public static final String CHANNEL_LOCATTION_ADDRESS = "locationAddress";
    public static final String CHANNEL_LOCATTION_TIME = "locationLastUpdate";
    public static final String CHANNEL_PARK_LOCATION = "parkingPosition";
    public static final String CHANNEL_PARK_ADDRESS = "parkingAddress";
    public static final String CHANNEL_PARK_TIME = "parkingTime";
    public static final String CHANNEL_CAR_MOVING = "carMoving";

    // Group range
    public static final String CHANNEL_GROUP_RANGE = "range";
    public static final String CHANNEL_RANGE_TOTAL = "totalRange";
    public static final String CHANNEL_RANGE_PRANGE = "primaryRange";
    public static final String CHANNEL_RANGE_PFUELTYPE = "primaryFuelType";
    public static final String CHANNEL_RANGE_SRANGE = "secondaryRange";
    public static final String CHANNEL_RANGE_SFUELTYPE = "secondaryFuelType";

    // Group Climarter
    public static final String CHANNEL_GROUP_CLIMATER = "climater";
    public static final String CHANNEL_CLIMATER_TARGET_TEMP = "targetTemperature";
    public static final String CHANNEL_CLIMATER_HEAT_SOURCE = "heaterSource";
    public static final String CHANNEL_CLIMATER_GEN_STATE = "climatisationState";
    public static final String CHANNEL_CLIMATER_FL_STATE = "frontLeft";
    public static final String CHANNEL_CLIMATER_FR_STATE = "frontRight";
    public static final String CHANNEL_CLIMATER_RL_STATE = "rearLeft";
    public static final String CHANNEL_CLIMATER_RR_STATE = "rearRight";
    public static final String CHANNEL_CLIMATER_MIRROR_HEAT = "mirrorHeat";
    public static final String CHANNEL_CLIMATER_REMAINING = "remainingClimatisation";

    // Group charger
    public static final String CHANNEL_GROUP_CHARGER = "charger";
    public static final String CHANNEL_CHARGER_STATUS = "chargingStatus";
    public static final String CHANNEL_CHARGER_ERROR = "errorCode";
    public static final String CHANNEL_CHARGER_PWR_STATE = "powerState";
    public static final String CHANNEL_CHARGER_FLOW = "energyFlow";
    public static final String CHANNEL_CHARGER_CHG_STATE = "chargingState";
    public static final String CHANNEL_CHARGER_MODE = "chargingMode";
    public static final String CHANNEL_CHARGER_CHGLVL = "chargingLevel";
    public static final String CHANNEL_CHARGER_BAT_STATE = "batteryState";
    public static final String CHANNEL_CHARGER_PLUG_STATE = "plugState";
    public static final String CHANNEL_CHARGER_LOCK_STATE = "lockState";
    public static final String CHANNEL_CHARGER_REMAINING = "remainingChargingTime";
    public static final String CHANNEL_CHARGER_POWER = "chargingPower";
    public static final String CHANNEL_CHARGER_RATE = "chargingRate";

    public static final String CHANNEL_GROUP_TRIP_PRE = "trip";
    public static final String CHANNEL_TRIP_SHORT = "Short";
    public static final String CHANNEL_TRIP_LONG = "Long";
    public static final String CHANNEL_GROUP_STRIP = CHANNEL_GROUP_TRIP_PRE + CHANNEL_TRIP_SHORT;
    public static final String CHANNEL_GROUP_LTRIP = CHANNEL_GROUP_TRIP_PRE + CHANNEL_TRIP_LONG;
    public static final String CHANNEL_TRIP_TIME = "timestamp";
    public static final String CHANNEL_TRIP_TRAVELTIME = "traveltime";
    public static final String CHANNEL_TRIP_AVG_ELCON = "avgElectricConsumption";
    public static final String CHANNEL_TRIP_AVG_FUELCON = "avgFuelConsumption";
    public static final String CHANNEL_TRIP_AVG_SPEED = "avgSpeed";
    public static final String CHANNEL_TRIP_MILAGE = "mileage";
    public static final String CHANNEL_TRIP_START_MIL = "startMileage";
    public static final String CHANNEL_TRIP_OVR_MILAGE = "overallMileage";

    public static final String CHANNEL_GROUP_DEST_PRE = "destination";
    public static final String CHANNEL_DEST_NAME = "destinationName";
    public static final String CHANNEL_DEST_POI = "destinationPoi";
    public static final String CHANNEL_DEST_GEO = "destinationLocation";
    public static final String CHANNEL_DEST_STREET = "destinatinStreet";
    public static final String CHANNEL_DEST_CITY = "destinationCity";
    public static final String CHANNEL_DEST_ZIP = "destinationZip";
    public static final String CHANNEL_DEST_COUNTY = "destinationCountry";
    public static final String CHANNEL_DEST_SOURCE = "destinationSource";

    public static final String CHANNEL_GROUP_SPEEDALERT = "speedAlerts";
    public static final String CHANNEL_SPEEDALERT_TYPE = "speedAlertType";
    public static final String CHANNEL_SPEEDALERT_DESCR = "speedAlertDescr";
    public static final String CHANNEL_SPEEDALERT_TIME = "speedAlertTime";
    public static final String CHANNEL_SPEEDALERT_LIMIT = "speedAlertLimit";

    public static final String CHANNEL_GROUP_GEOFENCE = "geoFenceAlerts";
    public static final String CHANNEL_GEOFENCE_TYPE = "geoFenceAlertType";
    public static final String CHANNEL_GEOFENCE_DESCR = "geoFenceAlertDescr";
    public static final String CHANNEL_GEOFENCE_TIME = "geoFenceAlertTime";

    // Other channels group in here
    public static final String CHANNEL_GROUP_MAINT = "maintenance";
    public static final String CHANNEL_GROUP_WINDOWS = "windows";
    public static final String CHANNEL_GROUP_DOORS = "doors";
    public static final String CHANNEL_GROUP_TIRES = "tires";
    public static final String CHANNEL_GROUP_PICTURES = "pictures";
    public static final String CHANNEL_PICTURES_IMG_PREFIX = "imageUrl";

    public static final String CHANNEL_GROUP_RLUHIST = "rluHistory";
    public static final String CHANNEL_RLUHIST_TS = "rluTimestamp";
    public static final String CHANNEL_RLUHIST_OP = "rluOperation";
    public static final String CHANNEL_RLUHIST_RES = "rluResult";

    public static final String PROPERTY_VIN = "vin";
    public static final String PROPERTY_BRAND = "brand";
    public static final String PROPERTY_MODEL = "model";
    public static final String PROPERTY_COLOR = "color";
    public static final String PROPERTY_MMI = "mmi";
    public static final String PROPERTY_ENGINE = "engine";
    public static final String PROPERTY_TRANS = "transmission";

    public static final String ITEMT_STRING = "String";
    public static final String ITEMT_NUMBER = "Number";
    public static final String ITEMT_PERCENT = "Number:Dimensionless";
    public static final String ITEMT_SWITCH = "Switch";
    public static final String ITEMT_CONTACT = "Contact";
    public static final String ITEMT_LOCATION = "Location";
    public static final String ITEMT_TIME = "Number:Time";
    public static final String ITEMT_DATETIME = "DateTime";
    public static final String ITEMT_TEMP = "Number:Temperature";
    public static final String ITEMT_DISTANCE = "Number:Length";
    public static final String ITEMT_SPEED = "Number:Speed";
    public static final String ITEMT_VOLUME = "Number:Volume";
    public static final String ITEMT_POWER = "Number:Power";
    public static final String ITEMT_ENERGY = "Number:Energy";
    public static final String ITEMT_VOLT = "Number:ElectricPotential";
    public static final String ITEMT_AMP = "Number:ElectricCurrent";

    public static final String CONTENT_TYPE_FORM_URLENC = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final int POLL_INTERVAL_SEC = 3; // poll cycle evey 3sec
    public static final int API_TOKEN_REFRESH_INTERVAL_SEC = 5 * 60; // interval to check for valid token
    public static final int API_REQUEST_TIMEOUT_SEC = 120 + 5;
    public static final int API_REQUEST_CHECK_INT = 15 / POLL_INTERVAL_SEC; // interval for checking pending requests
    public static final int API_TIMEOUT_MS = 60 * 1000;
    public static final int DEFAULT_TOKEN_VALIDITY_SEC = 3600;

    public static int VENT_DEFAULT_DURATION_MIN = 30;
    public static int HF_DEFAULT_DURATION_SEC = 10;

    public static final Unit<Length> KILOMETRE = MetricPrefix.KILO(SIUnits.METRE);
    public static final Unit<Power> KWATT = MetricPrefix.KILO(Units.WATT);
    public static final Unit<Time> QDAYS = Units.DAY;
    public static final Unit<Time> QMINUTES = Units.MINUTE;
    public static final Unit<Dimensionless> PERCENT = Units.PERCENT;
    public static final Unit<Temperature> DKELVIN = MetricPrefix.DECI(Units.KELVIN);
    public static final Unit<Volume> DLITRE = MetricPrefix.DECI(Units.LITRE);
}
