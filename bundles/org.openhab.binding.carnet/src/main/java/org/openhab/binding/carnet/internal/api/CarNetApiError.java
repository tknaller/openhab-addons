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

import com.google.gson.annotations.SerializedName;

/**
 * The {@link CarNetApi} implements the http based API access to CarNet
 *
 * @author Markus Michels - Initial contribution
 */
public class CarNetApiError {
    /*
     * {
     * "error":"invalid_request",
     * "error_description": "Missing Username"
     * }
     */
    public String error = "";
    public String code = "";
    public String description = "";
    public CNErrorMessage2Details details = new CNErrorMessage2Details();

    public CarNetApiError() {
    }

    public CarNetApiError(CNApiError1 format1) {
        error = getString(format1.error);
        code = getString(format1.code);
        description = getString(format1.description);
    }

    public CarNetApiError(CNApiError2 format2) {
        error = getString(format2.error.error);
        code = getString(format2.error.code);
        description = getString(format2.error.description);
        if (format2.error.details != null) {
            details = format2.error.details;
        }
    }

    public boolean isValid() {
        return !code.isEmpty() || !error.isEmpty() || !description.isEmpty();
    }

    public boolean isError() {
        return !code.isEmpty() || !error.isEmpty();
    }

    @Override
    public String toString() {
        return description + "(" + code + " " + error + ")";
    }

    private String getString(String s) {
        return s != null ? s : "";
    }

    public static class CNApiError1 {
        /*
         * {
         * "error":"invalid_request",
         * "error_description": "Missing Username"
         * }
         */
        public String error;
        @SerializedName("error_code")
        public String code;
        @SerializedName("error_description")
        public String description;
    }

    public static class CNApiError2 {
        /*
         * {"error":{"errorCode":"gw.error.validation","description":"Invalid Request"}}
         * "error": { "errorCode": "mbbc.rolesandrights.invalidSecurityPin", "description":
         * "The Security PIN is invalid.", "details": { "challenge": "", "user": "dYeJ7CoMzqV0obHyRZJSyzkb9d11",
         * "reason": "SECURITY_PIN_INVALID", "delay": "0" } }}
         */
        public class CNErrorMessage2 {
            public String error;
            @SerializedName("errorCode")
            public String code;
            @SerializedName("description")
            public String description;
            public CNErrorMessage2Details details;
        }

        public CNErrorMessage2 error;
    }

    public static class CNErrorMessage2Details {
        public String challenge = "";
        public String user = "";
        public String reason = "";
        public String delay = "";
    }
}
