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
package org.openhab.binding.connectedcar.internal.api.fordpass;

import static org.openhab.binding.connectedcar.internal.CarUtils.fromJson;
import static org.openhab.binding.connectedcar.internal.api.ApiDataTypesDTO.API_BRAND_FORD;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.connectedcar.internal.api.ApiEventListener;
import org.openhab.binding.connectedcar.internal.api.ApiException;
import org.openhab.binding.connectedcar.internal.api.ApiHttpClient;
import org.openhab.binding.connectedcar.internal.api.ApiHttpMap;
import org.openhab.binding.connectedcar.internal.api.ApiToken;
import org.openhab.binding.connectedcar.internal.api.ApiToken.OAuthToken;
import org.openhab.binding.connectedcar.internal.api.BrandApiProperties;
import org.openhab.binding.connectedcar.internal.api.TokenManager;
import org.openhab.binding.connectedcar.internal.api.TokenOAuthFlow;
import org.openhab.binding.connectedcar.internal.config.CombinedConfig;

/**
 * {@link BrandApiFord} provides the brand specific functions of the API
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class BrandFordPass extends FordPassApi {
    static BrandApiProperties properties = new BrandApiProperties();
    static {
        properties.brand = API_BRAND_FORD;
        properties.xcountry = "US";
        /*
         * "North America & Canada": "71A3AD0A-CF46-4CCF-B473-FC7FE5BC4592",
         * "UK&Europe": "1E8C7794-FF5F-49BC-9596-A1E0C86C5B19",
         * "Australia": "5C80A6BB-CF0D-4A30-BDBF-FC804B5C1A98",
         */
        properties.xClientId = "71A3AD0A-CF46-4CCF-B473-FC7FE5BC4592";
        properties.clientId = "9fb503e0-715b-47e8-adfd-ad4b7770f73b";
        properties.userAgent = "fordpass-ap/93 CFNetwork/1197 Darwin/20.0.0";
        properties.apiDefaultUrl = "https://usapi.cv.ford.com/api";
        properties.loginUrl = "https://sso.ci.ford.com/oidc/endpoint/default/token";
        properties.tokenUrl = "https://api.mps.ford.com/api/oauth2/v1/token";
        properties.tokenRefreshUrl = "https://api.mps.ford.com/api/oauth2/v1/refresh";
    }

    public BrandFordPass(ApiHttpClient httpClient, TokenManager tokenManager,
            @Nullable ApiEventListener eventListener) {
        super(httpClient, tokenManager, eventListener);
    }

    @Override
    public BrandApiProperties getProperties() {
        return properties;
    }

    @Override
    public String getLoginUrl() {
        return properties.loginUrl;
    }

    @Override
    public ApiToken login(String loginUrl, TokenOAuthFlow oauth) throws ApiException {
        // Step 1: get access code (returns access_token, but this is in fact the auth code
        oauth.init(createDefaultParameters()) //
                .data("client_id", config.api.clientId).data("grant_type", "password")
                .data("username", config.account.user).data("password", config.account.password);
        String json = oauth.post(loginUrl, false).response;
        OAuthToken token = fromJson(gson, json, OAuthToken.class).normalize();
        oauth.accessToken = token.accessToken;
        return new ApiToken(token);
    }

    @Override
    public ApiToken grantAccess(TokenOAuthFlow oauth) throws ApiException {
        // Step 2: get api+refresh token
        oauth.header("Application-Id", config.api.xClientId) //
                .clearData().data("code", oauth.accessToken);
        String json = oauth.put(config.api.tokenUrl, true).response;
        return new ApiToken(fromJson(gson, json, OAuthToken.class).normalize());
    }

    @Override
    public ApiToken refreshToken(CombinedConfig config, ApiToken apiToken) throws ApiException {
        ApiHttpMap params = super.createApiParameters() //
                .data("refresh_token", apiToken.getRefreshToken());
        String json = http.put(config.api.tokenRefreshUrl, params.getHeaders(), //
                params.getRequestData(true)).response;
        OAuthToken newToken = fromJson(gson, json, OAuthToken.class).normalize();
        return apiToken.updateToken(newToken);
    }
}
