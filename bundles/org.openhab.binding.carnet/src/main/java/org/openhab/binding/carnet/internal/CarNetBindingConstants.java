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
package org.openhab.binding.carnet.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link CarNetBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class CarNetBindingConstants {

    public static final String BINDING_ID = "carnet";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_ACCOUNT = new ThingTypeUID(BINDING_ID, "account");
    public static final ThingTypeUID THING_TYPE_VEHICLE = new ThingTypeUID(BINDING_ID, "vehicle");
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections
            .unmodifiableSet(Stream.of(THING_TYPE_ACCOUNT, THING_TYPE_VEHICLE).collect(Collectors.toSet()));

    // List of all ChannelGroups and Channels
    public static final String CHANNEL_GROUP_GENERAL = "general";
    public static final String CHANNEL_GENERAL_VIN = "vin";

    public static final String CHANNEL_GROUP_CONTROL = "control";
    public static final String CHANNEL_CONTROL_LOCK = "lock";
    public static final String CHANNEL_CONTROL_CLIMA = "clima";
    public static final String CHANNEL_CONTROL_WINHEAT = "windowsHeat";
    public static final String CHANNEL_CONTROL_PREHEAT = "preHeater";
    public static final String CHANNEL_CONTROL_CHARGER = "charger";
    public static final String CHANNEL_CONTROL_UPDATE = "update";

    public static final String CHANNEL_GROUP_LOCATION = "location";
    public static final String CHANNEL_LOCATTION_GEO = "position";
    public static final String CHANNEL_LOCATTION_TIME = "lastUpdate";
    public static final String CHANNEL_LOCATTION_PARK = "parkingTime";

    // Other channels group in here
    public static final String CHANNEL_GROUP_STATUS = "status";
    public static final String CHANNEL_GROUP_RANGE = "range";
    public static final String CHANNEL_GROUP_MAINT = "maintenance";
    public static final String CHANNEL_GROUP_WINDOWS = "windows";
    public static final String CHANNEL_GROUP_DOORS = "doors";
    public static final String CHANNEL_GROUP_TIRES = "tires";

    public static final String PROPERTY_VIN = "vin";
    public static final String PROPERTY_BRAND = "brand";
    public static final String PROPERTY_MODEL = "model";
    public static final String PROPERTY_COLOR = "color";
    public static final String PROPERTY_MMI = "mmi";
    public static final String PROPERTY_ENGINE = "engine";
    public static final String PROPERTY_TRANS = "transmission";

    public static final String ITEMT_STRING = "String";
    public static final String ITEMT_NUMBER = "Number";
    public static final String ITEMT_SWITCH = "Switch";
    public static final String ITEMT_LOCATION = "Location";
    public static final String ITEMT_TEMP = "Number:Temperature";
    public static final String ITEMT_DISTANCE = "Number:Length";
    public static final String ITEMT_SPEED = "Number:Speed";
    public static final String ITEMT_VOLUME = "Number:Volume";
    public static final String ITEMT_TIME = "Number:Time";
    public static final String ITEMT_PERCENT = "Number:Dimensionless";

    public static final int API_TOKEN_REFRESH_INTERVAL_SEC = 5 * 60; // interval to check for valid token
    public static final int API_REQUEST_TIMEOUT = 60;

    public static final String API_STATUS_MSG_PREFIX = "api-status";
    public static final String API_STATUS_CLASS_SECURUTY = "VSR.security";
    public static final String API_STATUS_GW_ERROR = "gw.error";
}
