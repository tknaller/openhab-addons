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
package org.openhab.binding.carnet.internal.api.brand;

import static org.openhab.binding.carnet.internal.api.carnet.CarNetApiConstants.CNAPI_VW_TOKEN_URL;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.carnet.internal.api.ApiEventListener;
import org.openhab.binding.carnet.internal.api.ApiHttpClient;
import org.openhab.binding.carnet.internal.api.TokenManager;
import org.openhab.binding.carnet.internal.api.carnet.CarNetApi;

/**
 * {@link BrandSkodaEnyak} provides the Brand interface for Skoda Enyak
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class BrandSkodaEnyak extends CarNetApi {
    public BrandSkodaEnyak(ApiHttpClient httpClient, TokenManager tokenManager,
            @Nullable ApiEventListener eventListener) {
        super(httpClient, tokenManager, eventListener);
    }

    @Override
    public BrandApiProperties getProperties() {
        BrandApiProperties properties = new BrandApiProperties();
        properties.userAgent = "OneConnect/000000023 CFNetwork/978.0.7 Darwin/18.7.0";
        properties.apiDefaultUrl = "https://api.connect.skoda-auto.cz/api/";
        properties.brand = "skoda";
        properties.xcountry = "CZ";
        properties.clientId = "f9a2359a-b776-46d9-bd0c-db1904343117@apps_vw-dilab_com";
        properties.xClientId = "28cd30c6-dee7-4529-a0e6-b1e07ff90b79";
        properties.xrequest = "cz.skodaauto.connect";
        properties.redirect_uri = "skodaconnect://oidc.login/";
        properties.responseType = "code token id_token";
        properties.authScope = "openid profile mbb";
        properties.tokenUrl = CNAPI_VW_TOKEN_URL;
        properties.tokenRefreshUrl = "https://tokenrefreshservice.apps.emea.vwapps.io";
        properties.xappVersion = "3.2.6";
        properties.xappName = "cz.skodaauto.connect";
        return properties;
    }
}
