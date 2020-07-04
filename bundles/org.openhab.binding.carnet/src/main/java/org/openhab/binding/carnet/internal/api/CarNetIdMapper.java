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

import static org.openhab.binding.carnet.internal.CarNetBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.measure.Unit;
import javax.measure.quantity.Length;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.unit.MetricPrefix;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;

/**
 * The {@link CarNetIdMapper} maps status value IDs from the API to channel definitions.
 *
 * @author Markus Michels - Initial contribution
 * @author Lorenzo Bernardi - Additional contribution
 *
 */
@NonNullByDefault
public class CarNetIdMapper {
    public static final Unit<Length> KILOMETRE = MetricPrefix.KILO(SIUnits.METRE);

    public static class CNIdMapEntry {
        public String id = "";
        public String symbolicName = "";
        public String channelName = "";
        public String itemType = "";
        public String groupName = "";
        public Optional<Unit<?>> unit = Optional.empty();
    }

    private Map<String, CNIdMapEntry> map = new HashMap<String, CNIdMapEntry>();

    public CarNetIdMapper() {
        // Status
        add("KILOMETER_STATUS", "0x0101010002", "kilometerStatus", ITEMT_DISTANCE, CHANNEL_GROUP_STATUS, KILOMETRE);
        add("TEMPERATURE_OUTSIDE", "0x0301020001", "tempOutside", ITEMT_TEMP);
        add("LIGHT_STATUS", "0x0301010001", "statusLight", ITEMT_SWITCH);
        add("PARKING_BRAKE", "0x0301030001", "parkingBrake", ITEMT_SWITCH);
        add("SAFETY_STATE_TRUNK_LID", "0x030104000F", "trunkLidState", ITEMT_NUMBER);
        add("POSITION_CONVERTIBLE_TOP", "0x030105000A", "positionConvertableTop", ITEMT_NUMBER);
        add("STATE_SUN_ROOF_MOTOR_COVER", "0x030105000B", "roofMotorCoverState", ITEMT_NUMBER);
        add("POSITION_SUN_ROOF_MOTOR_COVER", "0x030105000C", "roofMotorCoverPos", ITEMT_NUMBER);
        add("STATE_SUN_ROOF_REAR_MOTOR_COVER_3", "0x030105000D", "roofRearMotorCoverState", ITEMT_NUMBER);
        add("POSITION_SUN_ROOF_REAR_MOTOR_COVER_3", "0x030105000E", "roofRearMotorCoverPos", ITEMT_NUMBER);
        add("STATE_SPOILER", "0x0301050011", "spoilerState", ITEMT_NUMBER);
        add("POSITION_SPOILER", "0x0301050012", "spoilerPos", ITEMT_NUMBER);
        add("SAFETY_STATE_HOOD", "0x0301040012", "hoodState", ITEMT_NUMBER);
        add("STATE_SERVICE_FLAP", "0x030105000F", "serviceFlapState", ITEMT_SWITCH);
        add("POSITION_SERVICE_FLAP", "0x0301050010", "serviceFlapPos", ITEMT_NUMBER);

        // Gas
        add("FUEL_LEVEL_IN_PERCENTAGE", "0x030103000A", "fuelPercentage", ITEMT_PERCENT, CHANNEL_GROUP_RANGE,
                SmartHomeUnits.PERCENT);
        add("TOTAL_RANGE", "0x0301030005", "totalRange", ITEMT_DISTANCE, CHANNEL_GROUP_RANGE, KILOMETRE);
        add("PRIMARY_RANGE", "0x0301030006", "primaryRange", ITEMT_NUMBER, CHANNEL_GROUP_RANGE, KILOMETRE);
        add("PRIMARY_DRIVE", "0x0301030007", "primaryDrive", ITEMT_NUMBER, CHANNEL_GROUP_RANGE);
        add("SECONDARY_RANGE", "0x0301030008", "secondaryRange", ITEMT_NUMBER, CHANNEL_GROUP_RANGE, KILOMETRE);
        add("SECONDARY_DRIVE", "0x0301030009", "secondaryDrive", ITEMT_NUMBER, CHANNEL_GROUP_RANGE);
        add("15CNG_LEVEL_IN_PERCENTAGE", "0x030103000D", "gasPercentage", ITEMT_PERCENT, CHANNEL_GROUP_RANGE,
                SmartHomeUnits.PERCENT);
        add("STATE_OF_CHARGE", "0x0301030002", "chargingState", ITEMT_SWITCH, CHANNEL_GROUP_RANGE);

        // Maintenance
        add("MAINTINT_ALARM_INSPECTION", "0x0203010006", "alarmInspection", ITEMT_SWITCH, CHANNEL_GROUP_MAINT);
        add("MAINTINT_DIST_TO_INSPECTION", "0x0203010003", "distanceToInspection", ITEMT_DISTANCE, CHANNEL_GROUP_MAINT,
                KILOMETRE);
        add("MAINTINT_TIME_TO_INSPECTION", "0x0203010004", "timeToInspection", ITEMT_NUMBER, CHANNEL_GROUP_MAINT);
        add("WARNING_OIL_CHANGE", "0x0203010005", "oilWarning", ITEMT_SWITCH, CHANNEL_GROUP_MAINT);
        add("OIL_LEVEL_MINIMUM_WARNING", "0x0204040002", "oilWarningLevel", ITEMT_SWITCH, CHANNEL_GROUP_MAINT);
        add("OIL_LEVEL_DIPSTICK_PERCENTAGE", "0x0204040003", "oilPercentage", ITEMT_PERCENT, CHANNEL_GROUP_MAINT,
                SmartHomeUnits.PERCENT);
        add("OIL_LEVEL_AMOUNT_IN_LITERS", "0x0204040001", "oilAmount", ITEMT_VOLUME, CHANNEL_GROUP_MAINT,
                SmartHomeUnits.LITRE);
        add("MAINTINT_DISTANCE_TO_OIL_CHANGE", "0x0203010001", "distanceOilChange", ITEMT_DISTANCE, CHANNEL_GROUP_MAINT,
                KILOMETRE);
        add("MAINTINT_TIME_TO_OIL_CHANGE", "0x0203010002", "intervalOilChange", ITEMT_NUMBER, CHANNEL_GROUP_MAINT);
        add("MAINTENANCE_INTERVAL_AD_BLUE_RANGE", "0x02040C0001", "distanceAdBlue", ITEMT_DISTANCE, CHANNEL_GROUP_MAINT,
                KILOMETRE);
        add("MAINTINT_MONTHLY_MILEAGE", "0x0203010007", "monthlyMilage", ITEMT_NUMBER, CHANNEL_GROUP_MAINT);

        // Doors/trunk
        add("STATE_CONVERTABLE_TOP", "0x0301050009", "covertableTopState", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_TRUNK_LID", "0x030104000E", "trunkLidOpen", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_TRUNK_LID", "0x030104000D", "trunkLidLock", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_HOOD", "0x0301040011", "hoodOpen", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_HOOD", "0x0301040010", "hoodLock", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_LEFT_FRONT_DOOR", "0x0301040002", "doorFrontLeftOpen", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_LEFT_FRONT_DOOR", "0x0301040001", "doorFrontLeftLock", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("SAFETY_STATE_LEFT_FRONT_DOOR", "0x0301040003", "doorFrontLeftSafety", ITEMT_NUMBER, CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_RIGHT_FRONT_DOOR", "0x0301040008", "doorFrontRightOpen", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_RIGHT_FRONT_DOOR", "0x0301040007", "doorFrontRightLock", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("SAFETY_STATE_RIGHT_FRONT_DOOR", "0x0301040009", "doorFrontRightSafety", ITEMT_NUMBER, CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_LEFT_REAR_DOOR", "0x0301040005", "doorRearLeftOpen", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_LEFT_REAR_DOOR", "0x0301040004", "doorRearLeftLock", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("SAFETY_STATE_LEFT_REAR_DOOR", "0x0301040006", "doorRearLeftSafety", ITEMT_NUMBER, CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_RIGHT_REAR_DOOR", "0x030104000B", "doorRearRightOpen", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_RIGHT_REAR_DOOR", "0x030104000A", "doorRearRightLock", ITEMT_SWITCH, CHANNEL_GROUP_DOORS);
        add("SAFETY_STATE_RIGHT_REAR_DOOR", "0x030104000C", "doorRearRightSafety", ITEMT_NUMBER, CHANNEL_GROUP_DOORS);

        // Windows
        add("STATE_LEFT_FRONT_WINDOW", "0x0301050001", "windowFrontLeftState", ITEMT_SWITCH, CHANNEL_GROUP_WINDOWS);
        add("POSITION_LEFT_FRONT_WINDOW", "0x0301050002", "windowFrontLeftPos", ITEMT_NUMBER, CHANNEL_GROUP_WINDOWS);
        add("STATE_LEFT_REAR_WINDOW", "0x0301050003", "windowRearLeftState", ITEMT_SWITCH, CHANNEL_GROUP_WINDOWS);
        add("POSITION_LEFT_REAR_WINDOW", "0x0301050004", "windowRearLeftPos", ITEMT_NUMBER, CHANNEL_GROUP_WINDOWS);
        add("STATE_RIGHT_FRONT_WINDOW", "0x0301050005", "windowFrontRightState", ITEMT_SWITCH, CHANNEL_GROUP_WINDOWS);
        add("POSITION_RIGHT_FRONT_WINDOW", "0x0301050006", "windowFrontRightPos", ITEMT_NUMBER, CHANNEL_GROUP_WINDOWS);
        add("STATE_RIGHT_REAR_WINDOW", "0x0301050007", "windowRearRightState", ITEMT_SWITCH, CHANNEL_GROUP_WINDOWS);
        add("POSITION_RIGHT_REAR_WINDOW", "0x0301050008", "windowRearRightPos", ITEMT_NUMBER, CHANNEL_GROUP_WINDOWS);

        // Tires
        add("TIREPRESS_LEFT_FRONT_CURRENT", "0x0301060001", "tirePresFrontLeft", ITEMT_NUMBER, CHANNEL_GROUP_TIRES);
        add("TIREPRESS_LEFT_FRONT_DESIRED", "0x0301060002");
        add("TIREPRESS_LEFT_REAR_CURRENT", "0x0301060003", "tirePresRearLeft", ITEMT_NUMBER, CHANNEL_GROUP_TIRES);
        add("TIREPRESS_LEFT_REAR_DESIRED", "0x0301060004");
        add("TIREPRESS_RIGHT_FRONT_CURRENT", "0x0301060005", "tirePresFrontRight", ITEMT_NUMBER, CHANNEL_GROUP_TIRES);
        add("TIREPRESS_RIGHT_FRONT_DESIRED", "0x0301060006");
        add("TIREPRESS_RIGHT_REAR_CURRENT", "0x0301060007", "tirePresRearRight", ITEMT_NUMBER, CHANNEL_GROUP_TIRES);
        add("TIREPRESS_RIGHT_REAR_DESIRED", "0x0301060008");
        add("TIREPRESS_LEFT_FRONT_TIRE_DIFF", "0x030106000B");
        add("TIREPRESS_LEFT_REAR_TIRE_DIFF", "0x030106000C");
        add("TIREPRESS_RIGHT_FRONT_TIRE_DIFF", "0x030106000D");
        add("TIREPRESS_RIGHT_REAR_TIRE_DIFF", "0x030106000E");
        add("TIREPRESS_SPARE_TIRE_CURRENT", "0x0301060009");
        add("TIREPRESS_SPARE_TIRE_DESIRED", "0x030106000A");
        add("TIREPRESS_SPARE_TIRE_DIFF", "0x030106000F");

        // Misc
        add("UTC_TIME_STATUS", "0x0101010001");
    }

    public @Nullable CNIdMapEntry find(String id) {
        for (Map.Entry<String, CNIdMapEntry> e : map.entrySet()) {
            if (e.getKey().equalsIgnoreCase(id)) {
                return e.getValue();
            }
            CNIdMapEntry v = e.getValue();
            if (v.symbolicName.equalsIgnoreCase(id) || v.channelName.equalsIgnoreCase(id)) {
                return v;
            }
        }
        return null;
    }

    private void add(String name, String id, String channelName, String itemType, String groupName,
            @Nullable Unit<?> unit) {
        CNIdMapEntry entry = new CNIdMapEntry();
        entry.id = id;
        entry.symbolicName = name;
        entry.channelName = channelName;
        entry.itemType = itemType;
        entry.groupName = groupName;
        if (unit != null) {
            entry.unit = Optional.of(unit);
        }
        map.put(id, entry);
    }

    private void add(String name, String id, String channelName, String itemType, String groupName) {
        add(name, id, channelName, itemType, groupName, null);
    }

    private void add(String name, String id, String channelName, String itemType) {
        add(name, id, channelName, itemType, CHANNEL_GROUP_STATUS, null);
    }

    private void add(String name, String id) {
        add(name, id, "", "", CHANNEL_GROUP_STATUS, null);
    }
}
