package org.openhab.binding.carnet.internal.api;

import java.util.HashMap;
import java.util.Map;

public class CarNetIdMapper {
    public static class CNIdMapEntry {
        public String symbolicName;
        public String channelName;
        public String value = "";
        public String unit = "";
    }

    private Map<String, CNIdMapEntry> map = new HashMap<String, CNIdMapEntry>();

    public CarNetIdMapper() {
        add("MAINTENANCE_INTERVAL_DISTANCE_TO_OIL_CHANGE", "0x0203010001", "distanceOilChange");
        add("MAINTENANCE_INTERVAL_TIME_TO_OIL_CHANGE", "0x0203010002", "");
        add("MAINTENANCE_INTERVAL_DISTANCE_TO_INSPECTION", "0x0203010003", "distanceToInspection");
        add("MAINTENANCE_INTERVAL_TIME_TO_INSPECTION", "0x0203010004");
        add("WARNING_OIL_CHANGE", "0x0203010005", "warningOilChange");
        add("MAINTENANCE_INTERVAL_ALARM_INSPECTION", "0x0203010006");
        add("MAINTENANCE_INTERVAL_MONTHLY_MILEAGE", "0x0203010007");
        add("MAINTENANCE_INTERVAL_AD_BLUE_RANGE", "0x02040C0001", "distanceAdBlue");
        add("OIL_LEVEL_AMOUNT_IN_LITERS", "0x0204040001");
        add("OIL_LEVEL_MINIMUM_WARNING", "0x0204040002");
        add("OIL_LEVEL_DIPSTICK_PERCENTAGE", "0x0204040003", "percentageOil");
        add("LIGHT_STATUS", "0x0301010001", "statusParkingLight");
        add("TOTAL_RANGE", "0x0301030005", "totalRange");
        add("FUEL_LEVEL_IN_PERCENTAGE", "0x030103000A", "percentageFuel");
        add("15CNG_LEVEL_IN_PERCENTAGE", "0x030103000D");
        add("LOCK_STATE_LEFT_FRONT_DOOR", "0x0301040001", "doorFrontLeftLock");
        add("OPEN_STATE_LEFT_FRONT_DOOR", "0x0301040002", "doorFrontLeftOpen");
        add("SAFETY_STATE_LEFT_FRONT_DOOR", "0x0301040003", "doorFrontLeftSafety");
        add("LOCK_STATE_LEFT_REAR_DOOR", "0x0301040004", "doorRearLeftLock");
        add("OPEN_STATE_LEFT_REAR_DOOR", "0x0301040005", "doorRearLeftOpen");
        add("SAFETY_STATE_LEFT_REAR_DOOR", "0x0301040006", "doorRearLeftSaftey");
        add("LOCK_STATE_RIGHT_FRONT_DOOR", "0x0301040007", "doorFrontRightLock");
        add("OPEN_STATE_RIGHT_FRONT_DOOR", "0x0301040008", "doorFrontRightOpen");
        add("SAFETY_STATE_RIGHT_FRONT_DOOR", "0x0301040009", "doorFrontRightSafety");
        add("LOCK_STATE_RIGHT_REAR_DOOR", "0x030104000A", "doorRearRightLock");
        add("OPEN_STATE_RIGHT_REAR_DOOR", "0x030104000B", "doorRearRightOpen");
        add("SAFETY_STATE_RIGHT_REAR_DOOR", "0x030104000C", "doorRearRightSafety");
        add("LOCK_STATE_TRUNK_LID", "0x030104000D", "trunkLidLock");
        add("OPEN_STATE_TRUNK_LID", "0x030104000E", "trunkLidOpen");
        add("SAFETY_STATE_TRUNK_LID", "0x030104000F", "trunkLidSafety");
        add("LOCK_STATE_HOOD", "0x0301040010", "hoodLock");
        add("OPEN_STATE_HOOD", "0x0301040011", "hoodOpen");
        add("SAFETY_STATE_HOOD", "0x0301040012", "hoodSafety");
        add("STATE_LEFT_FRONT_WINDOW", "0x0301050001", "leftFrontWindowState");
        add("POSITION_LEFT_FRONT_WINDOW", "0x0301050002", "leftFrontWindowPos");
        add("STATE_LEFT_REAR_WINDOW", "0x0301050003", "leftRearWindowState");
        add("POSITION_LEFT_REAR_WINDOW", "0x0301050004", "leftRearWindowPos");
        add("STATE_RIGHT_FRONT_WINDOW", "0x0301050005", "rightFrontWindowState");
        add("POSITION_RIGHT_FRONT_WINDOW", "0x0301050006", "rightFrontWindowPos");
        add("STATE_RIGHT_REAR_WINDOW", "0x0301050007", "rightRearWindowState");
        add("POSITION_RIGHT_REAR_WINDOW", "0x0301050008", "rightRearWindowState");
        add("STATE_CONVERTIBLE_TOP", "0x0301050009");
        add("POSITION_CONVERTIBLE_TOP", "0x030105000A");
        add("STATE_SUN_ROOF_MOTOR_COVER", "0x030105000B");
        add("POSITION_SUN_ROOF_MOTOR_COVER", "0x030105000C");
        add("STATE_SUN_ROOF_REAR_MOTOR_COVER_3", "0x030105000D");
        add("POSITION_SUN_ROOF_REAR_MOTOR_COVER_3", "0x030105000E");
        add("STATE_SERVICE_FLAP", "0x030105000F");
        add("POSITION_SERVICE_FLAP", "0x0301050010");
        add("STATE_SPOILER", "0x0301050011");
        add("POSITION_SPOILER", "0x0301050012");
        add("UTC_TIME_STATUS", "0x0101010001");
        add("KILOMETER_STATUS", "0x0101010002");
        add("PRIMARY_RANGE", "0x0301030006");
        add("PRIMARY_DRIVE", "0x0301030007");
        add("SECONDARY_RANGE", "0x0301030008");
        add("SECONDARY_DRIVE", "0x0301030009");
        add("STATE_OF_CHARGE", "0x0301030002");
        add("TEMPERATURE_OUTSIDE", "0x0301020001", "tempOutside");
        add("PARKING_BRAKE", "0x0301030001");
        add("TYRE_PRESSURE_LEFT_FRONT_CURRENT_VALUE", "0x0301060001", "tirePresFrontLeft");
        add("TYRE_PRESSURE_LEFT_FRONT_DESIRED_VALUE", "0x0301060002");
        add("TYRE_PRESSURE_LEFT_REAR_CURRENT_VALUE", "0x0301060003", "tirePresRearLeft");
        add("TYRE_PRESSURE_LEFT_REAR_DESIRED_VALUE", "0x0301060004");
        add("TYRE_PRESSURE_RIGHT_FRONT_CURRENT_VALUE", "0x0301060005", "tirePresFrontRight");
        add("TYRE_PRESSURE_RIGHT_FRONT_DESIRED_VALUE", "0x0301060006");
        add("TYRE_PRESSURE_RIGHT_REAR_CURRENT_VALUE", "0x0301060007", "tirePresRearRight");
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

    private void add(String name, String id, String channelName) {
        CNIdMapEntry entry = new CNIdMapEntry();
        entry.symbolicName = name;
        entry.channelName = channelName;
        map.put(id, entry);
    }

    private void add(String sensor, String id) {
        add(sensor, id, "");
    }

}
