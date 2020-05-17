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
    protected String authType = "";
    protected Integer authVersion = 1;
    protected Integer validity = -1;

    public CarNetAccessToken() {
    }

    public CarNetAccessToken(CarNetApiToken token) {
        accessToken = token.accesToken;
        authType = token.accesToken;
        validity = token.validity;
    }

    public String getHttpHeader() {
        return MessageFormat.format("Authorization: {0} {1} {2}", authType, authVersion, accessToken);
    }

    public boolean isValid() {
        // TO-DO: Check validity
        return !accessToken.isEmpty();
    }
}
