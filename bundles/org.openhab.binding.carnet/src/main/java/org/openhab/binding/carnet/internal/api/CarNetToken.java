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

import java.text.MessageFormat;
import java.util.Date;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNApiToken;

/**
 * The {@link CarNetToken} store the API token information.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class CarNetToken {
    protected String accessToken = "";
    protected String idToken = "";
    protected String securityToken = "";
    protected String refreshToken = "";
    protected String authType = "";
    protected int authVersion = 1;
    protected int validity = -1;
    protected String service = "";
    private Date creationTime = new Date();

    public CarNetToken() {

    }

    public CarNetToken(CNApiToken token) {
        accessToken = token.accessToken != null ? token.accessToken : "";
        idToken = token.idToken != null ? token.idToken : "";
        securityToken = token.securityToken != null ? token.securityToken : "";
        refreshToken = token.refreshToken != null ? token.refreshToken : "";
        authType = token.authType;
        if (token.validity != null) {
            int treshhold = token.validity - new Double(token.validity * 0.9).intValue();
            validity = token.validity - treshhold;
            validity = 30;
        }
        creationTime = new Date();
        if (!isValid()) {
            invalidate();
        }
    }

    public void setService(String service) {
        this.service = service;
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
        return (!accessToken.isEmpty() || !idToken.isEmpty() || !securityToken.isEmpty()) && (validity != -1);
    }

    public void invalidate() {
        validity = -1;
    }

    @Override
    public String toString() {
        String token = !securityToken.isEmpty() ? securityToken
                : !idToken.isEmpty() ? idToken : !accessToken.isEmpty() ? accessToken : "NULL";
        return token + creationTime + ", V=" + validity;
    }
}
