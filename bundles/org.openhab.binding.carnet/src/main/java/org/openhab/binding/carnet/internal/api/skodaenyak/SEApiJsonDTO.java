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

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * {@link SEApiJsonDTO} defines the Skoda Enyaq data formats
 *
 * @author Markus Michels - Initial contribution
 */
public class SEApiJsonDTO {
    public static final String SESERVICE_STATUS = "status";
    public static final String SESERVICE_CLIMATISATION = "air-conditioning";
    public static final String SESERVICE_CHARGING = "charging";

    public static final String SEENDPOINT_STATUS = "status";
    public static final String SEENDPOINT_SETTINGS = "settings";

    public static class SEVehicleList {
        public static class SEVehicle {
            public class SEVehicleSpec {
                public class SEBatterySpec {
                    @SerializedName("CapacityInKWh")
                    public Integer capacityInKWh;
                }

                @SerializedName("Title")
                public String title;
                @SerializedName("Brand")
                public String brand;
                @SerializedName("Model")
                public String model;
                @SerializedName("Battery")
                public SEBatterySpec battery;
            }

            public class SEConnectivities {

            }

            public class SECapabilities {

            }

            @SerializedName("ID")
            public String id;
            @SerializedName("VIN")
            public String vin;
            @SerializedName("LastUpdatedAt")
            public String lastUpdatedAt;
            @SerializedName("Specification")
            public SEVehicleSpec specification;
            @SerializedName("Connectivities")
            public SEConnectivities connectivities;
            @SerializedName("Capabilities")
            public SECapabilities capabilities;
        }

        public ArrayList<SEVehicle> data;
    }

    public static class SEVehicleStatusData {
        public static class SEVehicleStatus {
            public class SEChargerStatus {
                public class SEPlugStatus {
                    @SerializedName("ConnectionState")
                    public String connectionState;
                    @SerializedName("LockState")
                    public String lockState;
                }

                public class SEChargingStatus {
                    @SerializedName("State")
                    public String state;
                    @SerializedName("RemainingToCompleteInSeconds")
                    public Long remainingToCompleteInSeconds;
                    @SerializedName("ChargingPowerInWatts")
                    public Double chargingPowerInWatts;
                    @SerializedName("ChargingRateInKilometersPerHour")
                    public Double chargingRateInKilometersPerHour;
                    @SerializedName("chargingType")
                    public String ChargingType;
                    @SerializedName("chargeMode")
                    public String ChargeMode;
                }

                public class SEBatteryStatus {
                    @SerializedName("CruisingRangeElectricInMeters")
                    public Long CruisingRangeElectricInMeters;
                    @SerializedName("StateOfChargeInPercent")
                    public Integer StateOfChargeInPercent;
                }

                @SerializedName("Plug")
                public SEPlugStatus plug;
                @SerializedName("charging")
                public SEChargingStatus Charging;
                @SerializedName("battery")
                public SEBatteryStatus Battery;
            }
        }
    }

    public static class SEChargerSettins {
        // {
        // "autoUnlockPlugWhenCharged": "Permanent",
        // "maxChargeCurrentAc": "Maximum",
        // "targetStateOfChargeInPercent": 100
        // }
        public String autoUnlockPlugWhenCharged;
        public String maxChargeCurrentAc;
        public Integer targetStateOfChargeInPercent;
    }
}
