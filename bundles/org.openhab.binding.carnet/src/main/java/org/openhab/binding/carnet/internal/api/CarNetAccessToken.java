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

import static org.openhab.binding.carnet.internal.CarNetBindingConstants.API_TOKEN_REFRESH_TRESHOLD_SEC;

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiToken;

/**
 * The {@link CarNetAccessToken} store the API token information.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class CarNetAccessToken {
    protected String accessToken = "";
    protected String idToken = "";
    protected String refreshToken = "";
    protected String authType = "";
    protected Integer authVersion = 1;
    protected Integer validity = -1;
    private Date creationTime = new Date();

    public CarNetAccessToken() {

    }

    public CarNetAccessToken(CarNetApiToken token) {
        accessToken = token.accessToken != null ? token.accessToken : "";
        idToken = token.idToken != null ? token.idToken : "";
        refreshToken = token.refreshToken != null ? token.refreshToken : "";
        authType = token.authType;
        validity = token.validity - API_TOKEN_REFRESH_TRESHOLD_SEC;
        creationTime = new Date();
    }

    public String getHttpHeader() {
        return MessageFormat.format("Authorization: {0} {1} {2}", authType, authVersion, accessToken);
    }

    /**
     * Check if access token is still valid
     *
     * @return false: token invalid or expired
     */
    public Boolean isExpired() {
        if (!isValid()) {
            return true;
        }
        Date currentTime = new Date();
        long diff = currentTime.getTime() - creationTime.getTime();
        return (diff / 1000) > validity;
    }

    public boolean isValid() {
        return (!accessToken.isEmpty() || !idToken.isEmpty()) && (validity != -1);
    }
}
