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

    public static class CarNetApiErrorMessage {
        /*
         * {
         * "error":"invalid_request",
         * "error_description": "Missing Username"
         * }
         */
        String error = "";
        @SerializedName("error_code")
        String code = "";
        @SerializedName("error_description")
        String description = "";
    }

    public static class CarNetApiToken {
        /*
         * {
         * "access_token": "64217P2jTNpckrCed7o8b5OJWAvTOGuWz8dperT4zY6KkDzREv67",
         * "token_type":"AudiAuth",
         * "expires_in":3600
         * }
         */

        @SerializedName("token_type")
        public String authType;
        @SerializedName("access_token")
        public String accessToken;
        @SerializedName("id_token")
        public String idToken;
        @SerializedName("refresh_token")
        public String refreshToken;

        @SerializedName("expires_in")
        public Integer validity;

        public String scope;
    }

    public static class CarNetVehicleList {
        /*
         * {"userVehicles":{"vehicle": ["WAUZZZF21LN046449"]}}
         */
        public static class CNVehicles {
            public ArrayList<String> vehicle;
        }

        public CNVehicles userVehicles;
    }

    public static class CarNetVehicleDetails {
        /*
         * {
         * "carportData":{"systemId":"msg","requestId":"MSG-ivwb2347-1582070213967-85252-ADE",
         * "brand":"Audi","country":"DE","vin":"WAUZZZF21LN046449","modelCode":"4A5BGA",
         * "modelName":"A6 Avant qTDI3.0 V6210 A8",
         * "modelYear":2020,"color":"LX7J","countryCode":"DE","engine":"DDV","mmi":"7UG","transmission":"SQN"}
         * }
         */
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
                    /*
                     * "id":"0x0101010001",
                     * "field": [
                     * {
                     * "id":"0x0101010001",
                     * "tsCarSentUtc":"2020-02-20T19:05:18Z",
                     * "tsCarSent":"2020-02-20T20:05:45",
                     * "tsCarCaptured":"2020-02-20T20:05:45","
                     * tsTssReceivedUtc":"2020-02-20T19:09:57Z",
                     * "milCarCaptured":3944,
                     * "milCarSent":3944,
                     * "value":"echo"
                     * }
                     * ]
                     */
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
        /*
         * {
         * "findCarResponse":
         * { "Position":{"timestampCarSent":"0002-11-28T00:00:00","timestampTssReceived":"2020-02-21T20:11:10Z",
         * "carCoordinate":{"latitude":49529343,"longitude":-1568820},
         * "timestampCarSentUTC":"2020-02-21T20:11:00Z","timestampCarCaptured":"0002-11-28T00:00:00"},
         * "parkingTimeUTC":"2020-02-21T20:08:52Z"}
         * }
         */
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

        public CNFindCarResponse findCarResponse;
    }

    public static class CarNetDestinations {
        public String destinations;
    }

    public static class CarNetHistory {
        public String destinations;
    }
}
