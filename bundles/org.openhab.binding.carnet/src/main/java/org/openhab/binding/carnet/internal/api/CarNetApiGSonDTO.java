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

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link CarNetApiGSonDTO} defines helper classes for the Json to GSon mapping
 *
 * @author Markus Michels - Initial contribution
 */
public class CarNetApiGSonDTO {

    public static class CNApiToken {
        @SerializedName("token_type")
        public String authType;
        @SerializedName("access_token")
        public String accessToken;
        @SerializedName("id_token")
        public String idToken;
        @SerializedName("refresh_token")
        public String refreshToken;
        @SerializedName("securityToken")
        public String securityToken;

        @SerializedName("expires_in")
        public Integer validity;

        public String scope;
    }

    public static class CarNetSecurityPinAuthInfo {
        public static class CNSecurityPinAuthInfo {
            public class CNSecurityPinTransmission {
                public Integer hashProcedureVersion;
                public String challenge;
                public Integer remainingTries;
            }

            public String securityToken;
            public CNSecurityPinTransmission securityPinTransmission;
        }

        public CNSecurityPinAuthInfo securityPinAuthInfo;
    }

    public static class CarNetSecurityPinAuthentication {
        public static class CNSecuritxPinAuth {
            public class CNSecurityPin {
                public String challenge;
                public String securityPinHash;
            }

            public CNSecurityPin securityPin = new CNSecurityPin();
            public String securityToken = "";
        }

        public CNSecuritxPinAuth securityPinAuthentication = new CNSecuritxPinAuth();
    }

    public static class CNContentString {
        public String timestamp;
        public String content;
    }

    public static class CNContentInt {
        public String timestamp;
        public Integer content;
    }

    public static class CNContentDouble {
        public String timestamp;
        public Double content;
    }

    public static class CNContentBool {
        public String timestamp;
        public Boolean content;
    }

    public static class CarNetHomeRegion {
        public class CNHomeRegion {
            public class CNBaseUri {
                public String systemId;
                public String content;
            }

            CNBaseUri baseUri;
        }

        public CNHomeRegion homeRegion;
    }

    public static class CNPairingInfo {
        public class CarNetPairingInfo {
            public String pairingStatus;
            public String xmlns;
            public String userId;
            public String pairingCode;
            public String vin;
        }

        CarNetPairingInfo pairingInfo;
    }

    public static class CNVehicleData {
        public class CarNetVehicleData {
            public class CNVehicleDeviceList {
                public class CNVecileDevice {
                    public class CNEmbeddedSIM {
                        public class CNSimIdentification {
                            public String type;
                            public String content;
                        }

                        CNSimIdentification identification;
                    }

                    public String deviceType;
                    public String deviceId;
                    public String ecuGeneration;
                    public CNEmbeddedSIM embeddedSim;
                    public String imei;
                    public String mno;
                }

                public ArrayList<CNVecileDevice> vehicleDevice;
            }

            public String systemId;
            public String requestId;
            public String brand;
            public String country;
            public String vin;
            public Boolean isConnect;
            public Boolean isConnectSorglosReady;
            public CNVehicleDeviceList vehicleDevices;
        }

        CarNetVehicleData vehicleData;
    }

    public static class CarNetVehicleList {
        public static class CNVehicles {
            public ArrayList<String> vehicle;
        }

        public CNVehicles userVehicles;
    }

    public static class CarNetVehicleDetails {
        public static class CNVehicleDetails {
            public String systemId;
            public String requestId;
            public String brand;
            public String country;
            public String vin;
            public String modelCode;
            public String modelName;
            public String modelYear;
            public String color;
            public String countryCode;
            public String engine;
            public String mmi;
            public String transmission;
        }

        public CNVehicleDetails carportData;
    }

    public static class CarNetVehicleStatus {

        public static class CNStoredVehicleDataResponse {
            public static class CNVehicleData {
                public static class CNStatusData {
                    public static class CNStatusField {
                        public String id;
                        public String tsCarSentUtc;
                        public String tsCarSent;
                        public String tsCarCaptured;
                        public String tsTssReceivedUtc;
                        public Integer milCarCaptured;
                        public Integer milCarSent;
                        public String value;
                        public String unit;
                    }

                    public String id;
                    @SerializedName("field")
                    public ArrayList<CNStatusField> fields;
                }

                public ArrayList<CNStatusData> data;
            }

            public String vin;
            public CNVehicleData vehicleData;
        }

        @SerializedName("StoredVehicleDataResponse")
        public CNStoredVehicleDataResponse storedVehicleDataResponse;
    }

    public static class CarNetVehiclePosition {
        public static class CNFindCarResponse {
            public static class CNPosition {
                public static class CNCarCoordinates {
                    public Integer latitude;
                    public Integer longitude;
                }

                public CNCarCoordinates carCoordinate;
                public String timestampCarSent;
                public String timestampTssReceived;
            }

            @SerializedName("Position")
            public CNPosition carPosition;
            public String parkingTimeUTC;
        }

        private CNFindCarResponse findCarResponse;

        public double getLattitude() {
            return findCarResponse.carPosition.carCoordinate.latitude / 1000000.0;
        }

        public double getLongitude() {
            return findCarResponse.carPosition.carCoordinate.longitude / 1000000.0;
        }

        public String getCarSentTime() {
            return findCarResponse.carPosition.timestampTssReceived;
        }

        public String getParkingTime() {
            return findCarResponse.parkingTimeUTC;
        }
    }

    public static class CarNetHistory {
        public String destinations;
    }

    public static class CarNetActionResponse {
        public class CNRluActionResponse {
            String requestId;
            String vin;
        }

        CNRluActionResponse response;

        CNRluActionResponse rluActionResponse;
    }

    public static class CNEluActionHistory {
        public class CarNetRluHistory {
            public class CarNetRluLockActionList {
                public class CarNetRluLockAction {
                    public class CNRluLockStatus {
                        public class CNRluLockEntry {
                            public Boolean valid;
                            public Boolean locked;
                            public Boolean open;
                            public Boolean safe; // maybe empty
                        }

                        CNRluLockEntry driverDoor;
                        CNRluLockEntry coDriverDoor;
                        CNRluLockEntry driverRearDoor;
                        CNRluLockEntry coDriverRearDoor;
                        CNRluLockEntry frontLid;
                        CNRluLockEntry boot;
                        CNRluLockEntry flap;
                    }

                    public String lock;
                    public String timestamp;
                    public String channel;
                    public String rluResult;
                    CNRluLockStatus lockStatus;
                }

                public ArrayList<CarNetRluLockAction> action;

            }

            public String vin;
            public String steeringWheelSide;
            public String doorModel;
            public CarNetRluLockActionList actions;
        }

        public CarNetRluHistory actionsResponse;
    }

    public static class CNOperationList {
        public class CarNetOperationList {
            public String vin;
            public String channelClient;
            public String userId;
            public String role;
            public String securityLevel;
            public String status;

            public class CarNetServiceInfo {
                public class CNServiceStatus {
                    public String status;
                }

                public class CNServiceOperation {
                    public String id;
                    public String version;
                    public String permission;
                    public String requiredRole;
                    public String requiredSecurityLevel;
                }

                public class CNServiceUrl {
                    public String content;
                }

                public class CNComulativeLicense {
                    public String status;
                }

                public String serviceId;
                public String serviceType;
                public CNServiceStatus serviceStatus;
                public Boolean licenseRequired;
                public CNComulativeLicense cumulatedLicense;
                public Boolean primaryUserRequired;
                public String serviceEol;
                public Boolean rolesAndRightsRequired;
                public CNServiceUrl invocationUrl;
                public ArrayList<CNServiceOperation> operation;
            }

            public ArrayList<CarNetServiceInfo> serviceInfo;
        }

        public CarNetOperationList operationList;
    }

    public static class CarNetServiceAvailability {
        public boolean statusData = true;
        public boolean rlu = true;
        public boolean clima = true;
        public boolean charger = true;
        public boolean carFinder = true;
        public boolean tripData = true;
        public boolean destinations = true;
    }

    public static class CarNetDestinations {
        public class CNDestinationAddress {
            public String addressType;
            public String city;
            public String country;
            public String street;
            public String zipCode;
        }

        public class CNDestinationGeo {
            public Double latitude;
            public Double longitude;
        }

        public class CNDestinationPOI {
            public String lastName;
            // "phoneData":[{"phoneType":"2"} ]}
        }

        public class CNDestination {
            public String destinationName = "";
            public Boolean immediateDestination;
            public String id;
            CNDestinationAddress address;
            public String destinationSource;
            CNDestinationGeo geoCoordinate;
            CNDestinationPOI POIContact;
            public String fetchStatus;
        }

        public class CNDestinationList {
            public ArrayList<CNDestination> destination;
        }

        CNDestinationList destinations;
    }

    public static class CarNetTripData {
        public class CarNetTripDataList {
            public class CarNetTripDataEntry {
                public String tripType;
                public String tripID;
                public Integer averageElectricEngineConsumption;
                public Double averageFuelConsumption;
                public Integer averageSpeed;
                public Integer mileage;
                public Integer startMileage;
                public Double traveltime;
                public String timestamp;
                public String reportReason;
                public Integer overallMileage;
            }

            public ArrayList<CarNetTripDataEntry> tripData;
        }

        public CarNetTripDataList tripDataList;
    }

    public static class CNChargerInfo {
        public class CarNetChargerStatus {
            public class CNChargerSettings {
                public class CNCargerModeSel {
                    public CNContentString modificationState;
                    public CNContentString modificationReason;
                    public CNContentString value;
                }

                public CNContentInt maxChargeCurrent;
                public CNCargerModeSel chargeModeSelection;
            }

            public class CNChargerStatus {
                public class CarNetChargerStatusData {
                    public CNContentString chargingMode;
                    public CNContentInt chargingStateErrorCode;
                    public CNContentString chargingReason;
                    public CNContentString externalPowerSupplyState;
                    public CNContentString energyFlow;
                    public CNContentString chargingState;

                }

                public class CNChargerRangeStatusData {
                    public CNContentString engineTypeFirstEngine;
                    public CNContentInt primaryEngineRange;
                    public CNContentInt hybridRange;
                    public CNContentString engineTypeSecondEngine;
                    public CNContentInt secondaryEngineRange;
                }

                public class CNChargerLedStatusData {
                    public CNContentString ledColor;
                    public CNContentString ledState;
                }

                public class CNBatteryStatusData {
                    public CNContentInt stateOfCharge;
                    public CNContentInt remainingChargingTime;
                    public CNContentString remainingChargingTimeTargetSOC;
                }

                public class CNPlugStatusData {
                    public CNContentString plugState;
                    public CNContentString lockState;
                }

                public CarNetChargerStatusData chargingStatusData;
                public CNChargerRangeStatusData cruisingRangeStatusData;
                public CNChargerLedStatusData ledStatusData;
                public CNBatteryStatusData batteryStatusData;
                public CNPlugStatusData plugStatusData;
            }

            public CNChargerSettings settings;
            public CNChargerStatus status;
        }

        CarNetChargerStatus charger;
    }

    public static class CarNetClimaterTimer {
        public class CNTimerProfileList {
            public class CNTimerProfile {
                public class CNTimerProfileEntry {
                    public String timestamp;
                    public String profileName;
                    public String profileID;
                    public Boolean operationCharging;
                    public Boolean operationClimatisation;
                    public String targetChargeLevel;
                    public Boolean nightRateActive;
                    public String nightRateTimeStart;
                    public String nightRateTimeEnd;
                    public String chargeMaxCurrent;
                    public String heaterSource;
                }

                public ArrayList<CNTimerProfileEntry> timerProfile;
            }

            CNTimerProfile timerProfileList;
        }

        public class CNTimerList {
            public class CNTimerEntryList {
                public class CNTimerEntry {
                    public String timestamp;
                    public String timerID;
                    public String timerProgrammedStatus;
                    public String timerFrequency;
                    public String departureDateTime;
                }

                public ArrayList<CNTimerEntry> timer;
            }

            CNTimerEntryList timerList;
        }

        public class CNStatusTimerList {
            public class CNStatusTimerEntry {
                CNContentString timerChargeScheduleStatus;
                CNContentString timerClimateScheduleStatus;
                CNContentString timerExpiredStatus;
                CNContentString instrumentClusterTime;
            }

            public ArrayList<CNStatusTimerEntry> timer;
        }

        public class CNTimerAndProfileList {
            public class CNZoneSettings {
                public class CNZoneSettingList {
                    public class CNZoneSettingEntry {
                        public class CNZoneValue {
                            Boolean isEnabled;
                            String position;
                        }

                        public String timestamp;
                        public CNZoneValue value;
                    }

                    public ArrayList<CNZoneSettingEntry> zoneSetting;
                }

                CNZoneSettingList zoneSettings;
            }

            public class CNClimateElementSettings {
                CNContentBool isClimatisationAtUnlock;
                CNContentBool isMirrorHeatingEnabled;
                CNZoneSettings zoneSettings;
            }

            public class CNBasicTimerSettings {
                public String timestamp;
                public String heaterSource;
                public String chargeMinLimit;
                CNClimateElementSettings climaterElementSettings;
            }

            public class CNStatusTimer {
                public String timerID;
            }

            CNTimerProfileList timerProfileList;
            CNTimerList timerList;
            CNBasicTimerSettings timerBasicSetting;
            CNStatusTimer status;
        }
    }

    public static class CNClimater {
        public class CarNetClimaterStatus {
            public class CNClimaterSettings {
                public class CNClimaterElementSettings {
                    public class CNClimaterZoneSettingsList {
                        public class CNClimaterZoneSetting {
                            public class CNClZoneSetValue {
                                public Boolean isEnabled;
                                public String position;
                            }

                            public String timestamp;
                            public CNClZoneSetValue value;
                        }

                        public CNContentBool isClimatisationAtUnlock;
                        public CNContentBool isMirrorHeatingEnabled;
                        public ArrayList<CNClimaterZoneSetting> zoneSetting;
                    }

                    CNClimaterElementSettings zoneSettings;
                }

                public CNContentDouble targetTemperature;
                public CNContentBool climatisationWithoutHVpower;
                public CNContentString heaterSource;
            }

            public class CNClimaterStatus {
                public class CarNetClimaterStatusData {
                    public class CarNetClimaterZoneState {
                        public class CNClimaterZonState {
                            public Boolean isActive;
                            public String position;
                        }

                        public String timestamp;
                        public CNClimaterZonState value;
                    }

                    public class CNClimaterElementState {
                        public class CarNetClimaterZoneStateList {
                            public ArrayList<CarNetClimaterZoneState> zoneState;
                        }

                        public CNContentBool isMirrorHeatingActive;
                        public CNContentBool extCondAvailableFL;
                        public CNContentBool extCondAvailableFR;
                        public CNContentBool extCondAvailableRL;
                        public CNContentBool extCondAvailableRR;
                        public CarNetClimaterZoneStateList zoneStates;
                    }

                    public CNContentString climatisationState;
                    public CNContentInt climatisationStateErrorCode;
                    public CNContentInt remainingClimatisationTime;
                    public CNContentString climatisationReason;
                    public CNClimaterElementState climatisationElementStates;
                }

                public class CNTemperatureStatusData {
                    public CNContentInt outdoorTemperature;
                }

                public class CNParkingClockStatusData {
                    CNContentString vehicleParkingClock;
                }

                public CarNetClimaterStatusData climatisationStatusData;
                public CNTemperatureStatusData temperatureStatusData;
                public CNParkingClockStatusData vehicleParkingClockStatusData;
            }

            public CNClimaterSettings settings;
            public CNClimaterStatus status;
        }

        CarNetClimaterStatus climater;
    }

}
