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
        String error;
        @SerializedName("error_description")
        String text;
    }

    public static class CarNetApiToken {
        /*
         * {
         * "access_token": "64217P2jTNpckrCed7o8b5OJWAvTOGuWz8dperT4zY6KkDzREv67",
         * "token_type":"AudiAuth",
         * "expires_in":3600
         * }
         */

        @SerializedName("access_token")
        public String accesToken;
        @SerializedName("token_type")
        public String authType;
        @SerializedName("expires_in")
        public Integer validity;
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
}
