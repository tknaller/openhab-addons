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
package org.openhab.binding.connectedcar.internal.api.carnet;

import static org.openhab.binding.connectedcar.internal.api.carnet.CarNetApiConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.connectedcar.internal.api.ApiBrandProperties;
import org.openhab.binding.connectedcar.internal.api.ApiEventListener;
import org.openhab.binding.connectedcar.internal.api.ApiHttpClient;
import org.openhab.binding.connectedcar.internal.api.BrandAuthenticator;
import org.openhab.binding.connectedcar.internal.api.IdentityManager;

/**
 * {@link BrandCarNetSkoda} provides the Skoda specific functions of the API, portal URL us
 * https://www.skoda-connect.com
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class BrandCarNetSkoda extends CarNetApi implements BrandAuthenticator {
    public BrandCarNetSkoda(ApiHttpClient httpClient, IdentityManager tokenManager,
            @Nullable ApiEventListener eventListener) {
        super(httpClient, tokenManager, eventListener);
    }

    @Override
    public ApiBrandProperties getProperties() {
        return getSkodaProperties();
    }

    public static ApiBrandProperties getSkodaProperties() {
        ApiBrandProperties properties = new ApiBrandProperties();
        properties.brand = "VW"; // it's "VW", not "Skoda"
        properties.xcountry = "CZ";
        properties.apiDefaultUrl = "";
        properties.clientId = "7f045eee-7003-4379-9968-9355ed2adb06@apps_vw-dilab_com";
        properties.xClientId = "28cd30c6-dee7-4529-a0e6-b1e07ff90b79";
        properties.authScope = "openid profile mbb cars";
        properties.apiDefaultUrl = CNAPI_DEFAULT_API_URL;
        properties.tokenUrl = CNAPI_VW_TOKEN_URL;
        properties.tokenRefreshUrl = properties.tokenUrl;
        properties.redirect_uri = "skodaconnect://oidc.login/";
        properties.xrequest = "cz.skodaauto.connect";
        properties.responseType = "token id_token";
        properties.xappVersion = "3.2.6";
        properties.xappName = "cz.skodaauto.connect";
        return properties;
    }
}