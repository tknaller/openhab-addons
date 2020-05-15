package org.openhab.binding.carnet.internal.api;

import java.util.HashMap;
import java.util.Map;

import org.openhab.binding.carnet.internal.CarNetBindingConstants;

public class CarNetIdMapper {
    public static class CNIdMapEntry {
        public String symbolicName;
        public String channelName;
        public String itemType = "";
        public String groupName;
    }

    private Map<String, CNIdMapEntry> map = new HashMap<String, CNIdMapEntry>();

    public CarNetIdMapper() {
        // Misc
        add("UTC_TIME_STATUS", "0x0101010001");
        add("TEMPERATURE_OUTSIDE", "0x0301020001", "tempOutside", "Number");
        add("KILOMETER_STATUS", "0x0101010002", "kilometerStatus", "Number");
        add("STATE_OF_CHARGE", "0x0301030002", "chargingState", "Switch");

        add("FUEL_LEVEL_IN_PERCENTAGE", "0x030103000A", "gasPercentage", "Number");
        add("TOTAL_RANGE", "0x0301030005", "gasTotalRange", "Number");
        add("PRIMARY_RANGE", "0x0301030006");
        add("PRIMARY_DRIVE", "0x0301030007");
        add("SECONDARY_RANGE", "0x0301030008");
        add("SECONDARY_DRIVE", "0x0301030009");
        add("OIL_LEVEL_AMOUNT_IN_LITERS", "0x0204040001", "oilAmount", "Number");
        add("OIL_LEVEL_MINIMUM_WARNING", "0x0204040002", "oilWarning", "Switch");
        add("OIL_LEVEL_DIPSTICK_PERCENTAGE", "0x0204040003", "oilPercantage", "Number");
        add("WARNING_OIL_CHANGE", "0x0203010005", "oilWarning", "Switch");
        add("MAINTENANCE_INTERVAL_AD_BLUE_RANGE", "0x02040C0001", "distanceAdBlue", "Number");
        add("15CNG_LEVEL_IN_PERCENTAGE", "0x030103000D");

        add("LIGHT_STATUS", "0x0301010001", "statusLight", "Switch");
        add("PARKING_BRAKE", "0x0301030001", "parkingBrake", "Switch");
        add("SAFETY_STATE_TRUNK_LID", "0x030104000F");
        add("POSITION_CONVERTIBLE_TOP", "0x030105000A");
        add("STATE_SUN_ROOF_MOTOR_COVER", "0x030105000B");
        add("POSITION_SUN_ROOF_MOTOR_COVER", "0x030105000C");
        add("STATE_SUN_ROOF_REAR_MOTOR_COVER_3", "0x030105000D");
        add("POSITION_SUN_ROOF_REAR_MOTOR_COVER_3", "0x030105000E");
        add("STATE_SPOILER", "0x0301050011", "spoilerState", "Switch");
        add("POSITION_SPOILER", "0x0301050012");
        add("SAFETY_STATE_HOOD", "0x0301040012");
        add("STATE_SERVICE_FLAP", "0x030105000F");
        add("POSITION_SERVICE_FLAP", "0x0301050010");

        add("MAINTENANCE_INTERVAL_DISTANCE_TO_OIL_CHANGE", "0x0203010001", "distanceOilChange", "Number");
        add("MAINTENANCE_INTERVAL_TIME_TO_OIL_CHANGE", "0x0203010002");
        add("MAINTENANCE_INTERVAL_MONTHLY_MILEAGE", "0x0203010007");
        add("MAINTENANCE_INTERVAL_DISTANCE_TO_INSPECTION", "0x0203010003", "distanceToInspection", "Number");
        add("MAINTENANCE_INTERVAL_TIME_TO_INSPECTION", "0x0203010004");
        add("MAINTENANCE_INTERVAL_ALARM_INSPECTION", "0x0203010006");

        // Doors/trunk
        add("STATE_CONVERTIBLE_TOP", "0x0301050009", "covertableTopState", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_TRUNK_LID", "0x030104000E", "trunkLidOpen", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_TRUNK_LID", "0x030104000D", "trunkLidLock", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_HOOD", "0x0301040011", "hoodOpen", "Switch", CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_HOOD", "0x0301040010", "hoodLock", "Switch", CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("OPEN_STATE_LEFT_FRONT_DOOR", "0x0301040002", "doorFrontLeftOpen", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_LEFT_FRONT_DOOR", "0x0301040001", "doorFrontLeftLock", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("SAFETY_STATE_LEFT_FRONT_DOOR", "0x0301040003");
        add("OPEN_STATE_RIGHT_FRONT_DOOR", "0x0301040008", "doorFrontRightOpen", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_RIGHT_FRONT_DOOR", "0x0301040007", "doorFrontRightLock", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("SAFETY_STATE_RIGHT_FRONT_DOOR", "0x0301040009");
        add("OPEN_STATE_LEFT_REAR_DOOR", "0x0301040005", "doorRearLeftOpen", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_LEFT_REAR_DOOR", "0x0301040004", "doorRearLeftLock", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("SAFETY_STATE_LEFT_REAR_DOOR", "0x0301040006");
        add("OPEN_STATE_RIGHT_REAR_DOOR", "0x030104000B", "doorRearRightOpen", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("LOCK_STATE_RIGHT_REAR_DOOR", "0x030104000A", "doorRearRightLock", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_DOORS);
        add("SAFETY_STATE_RIGHT_REAR_DOOR", "0x030104000C");

        // Windows
        add("STATE_LEFT_FRONT_WINDOW", "0x0301050001", "windowFrontLeftState", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_WINDOWS);
        add("POSITION_LEFT_FRONT_WINDOW", "0x0301050002", "windowFrontLeftPos", "Number",
                CarNetBindingConstants.CHANNEL_GROUP_WINDOWS);
        add("STATE_LEFT_REAR_WINDOW", "0x0301050003", "windowRearLeftState", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_WINDOWS);
        add("POSITION_LEFT_REAR_WINDOW", "0x0301050004", "windowRearLeftPos", "Number",
                CarNetBindingConstants.CHANNEL_GROUP_WINDOWS);
        add("STATE_RIGHT_FRONT_WINDOW", "0x0301050005", "windowFrontRightState", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_WINDOWS);
        add("POSITION_RIGHT_FRONT_WINDOW", "0x0301050006", "windowFrontRightPos", "Number",
                CarNetBindingConstants.CHANNEL_GROUP_WINDOWS);
        add("STATE_RIGHT_REAR_WINDOW", "0x0301050007", "windowRearRightState", "Switch",
                CarNetBindingConstants.CHANNEL_GROUP_WINDOWS);
        add("POSITION_RIGHT_REAR_WINDOW", "0x0301050008", "windowRearRightPos", "Number",
                CarNetBindingConstants.CHANNEL_GROUP_WINDOWS);

        // Tyres
        add("TYRE_PRESSURE_LEFT_FRONT_CURRENT_VALUE", "0x0301060001", "tyrePresFrontLeft", "Number",
                CarNetBindingConstants.CHANNEL_GROUP_TYRES);
        add("TYRE_PRESSURE_LEFT_FRONT_DESIRED_VALUE", "0x0301060002");
        add("TYRE_PRESSURE_LEFT_REAR_CURRENT_VALUE", "0x0301060003", "tyrePresRearLeft", "Number",
                CarNetBindingConstants.CHANNEL_GROUP_TYRES);
        add("TYRE_PRESSURE_LEFT_REAR_DESIRED_VALUE", "0x0301060004");
        add("TYRE_PRESSURE_RIGHT_FRONT_CURRENT_VALUE", "0x0301060005", "tyrePresFrontRight", "Number",
                CarNetBindingConstants.CHANNEL_GROUP_TYRES);
        add("TYRE_PRESSURE_RIGHT_FRONT_DESIRED_VALUE", "0x0301060006");
        add("TYRE_PRESSURE_RIGHT_REAR_CURRENT_VALUE", "0x0301060007", "tyrePresRearRight", "Number",
                CarNetBindingConstants.CHANNEL_GROUP_TYRES);
        add("TYRE_PRESSURE_RIGHT_REAR_DESIRED_VALUE", "0x0301060008");
        add("TYRE_PRESSURE_SPARE_TYRE_CURRENT_VALUE", "0x0301060009");
        add("TYRE_PRESSURE_SPARE_TYRE_DESIRED_VALUE", "0x030106000A");
        add("TYRE_PRESSURE_LEFT_FRONT_TYRE_DIFFERENCE", "0x030106000B");
        add("TYRE_PRESSURE_LEFT_REAR_TYRE_DIFFERENCE", "0x030106000C");
        add("TYRE_PRESSURE_RIGHT_FRONT_TYRE_DIFFERENCE", "0x030106000D");
        add("TYRE_PRESSURE_RIGHT_REAR_TYRE_DIFFERENCE", "0x030106000E");
        add("TYRE_PRESSURE_SPARE_TYRE_DIFFERENCE", "0x030106000F");

    }

    public CNIdMapEntry find(String id) {
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

    private void add(String name, String id, String channelName, String itemType, String groupName) {
        CNIdMapEntry entry = new CNIdMapEntry();
        entry.symbolicName = name;
        entry.channelName = channelName;
        entry.itemType = itemType;
        entry.groupName = groupName;
        map.put(id, entry);
    }

    private void add(String name, String id, String channelName, String itemType) {
        add(name, id, channelName, itemType, CarNetBindingConstants.CHANNEL_GROUP_STATUS);
    }

    private void add(String name, String id) {
        add(name, id, "", "", CarNetBindingConstants.CHANNEL_GROUP_STATUS);
    }

}
