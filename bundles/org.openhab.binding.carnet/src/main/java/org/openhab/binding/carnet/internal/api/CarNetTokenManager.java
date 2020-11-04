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

import static org.openhab.binding.carnet.internal.CarNetUtils.*;
import static org.openhab.binding.carnet.internal.api.CarNetApi.*;
import static org.openhab.binding.carnet.internal.api.CarNetApiConstants.*;
import static org.openhab.binding.carnet.internal.api.CarNetHttpClient.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.http.HttpHeader;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CNApiToken;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetSecurityPinAuthInfo;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetSecurityPinAuthentication;
import org.openhab.binding.carnet.internal.handler.CarNetCombinedConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * {@link CarNetTokenManager} implements token creation and refreshing.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
@Component(service = CarNetTokenManager.class)
public class CarNetTokenManager {
    private final Logger logger = LoggerFactory.getLogger(CarNetTokenManager.class);

    private final Gson gson = new Gson();

    // private CarNetCombinedConfig config = new CarNetCombinedConfig();
    private class TokenSet {
        private CarNetToken idToken = new CarNetToken();
        private CarNetToken brandToken = new CarNetToken();
        private CarNetToken vwToken = new CarNetToken();
    }

    private Map<String, TokenSet> accountTokens = new HashMap<>();

    private CarNetHttpClient http = new CarNetHttpClient();
    private CopyOnWriteArrayList<CarNetToken> securityTokens = new CopyOnWriteArrayList<CarNetToken>();

    @Activate
    public CarNetTokenManager() {
    }

    public void setHttpClient(String tokenSetId, CarNetHttpClient httpClient) {
        this.http = httpClient;
    }

    /**
     * Generate a unique Id for all tokens related to the Account thing, but also for all depending Vehicle things. This
     * allows sharing the tokens across all things associated with the account.
     *
     * @return unique group Id
     */
    public String generateTokenSetId() {
        String id = UUID.randomUUID().toString();
        createTokenSet(id);
        return id;
    }

    public String createBrandToken(CarNetCombinedConfig config) throws CarNetException {
        String url = "";
        try {
            TokenSet tokens = getTokenSet(config.tokenSetId);

            if (!tokens.brandToken.isExpired()) {
                return tokens.brandToken.accessToken;
            }

            logger.debug("{}: Logging in, account={}", config.vehicle.vin, config.account.user);
            Map<String, String> headers = new LinkedHashMap<>();
            Map<String, String> data = new LinkedHashMap<>();
            headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
            headers.put(HttpHeader.ACCEPT.toString(),
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            headers.put(HttpHeader.CONTENT_TYPE.toString(), "application/x-www-form-urlencoded");

            logger.debug("{}: OAuth: Get signin form", config.vehicle.vin);
            String state = UUID.randomUUID().toString();
            String nonce = generateNonce();
            url = CNAPI_OAUTH_AUTHORIZE_URL + "?response_type=" + urlEncode(config.responseType) + "&client_id="
                    + urlEncode(config.clientId) + "&redirect_uri=" + urlEncode(config.redirect_uri) + "&scope="
                    + urlEncode(config.authScope) + "&state=" + state + "&nonce=" + nonce
                    + "&prompt=login&ui_locales=de-DE%20de";
            http.get(url, headers);
            url = http.getRedirect(); // Signin URL
            // error=consent_required&error_description=
            if (url.isEmpty()) {
                throw new CarNetException("Unable to get signin URL");
            }
            if (url.contains("error=consent_require")) {
                String message = URLDecoder.decode(url);
                message = substringBefore(substringAfter(message, "&error_description="), "&");
                throw new CarNetException(
                        "Login failed, Consent missing. Login to Web App and give consent: " + message);
            }
            String html = http.get(url, headers);
            url = http.getRedirect(); // Signin URL
            String csrf = substringBetween(html, "name=\"_csrf\" value=\"", "\"/>");
            String relayState = substringBetween(html, "name=\"relayState\" value=\"", "\"/>");
            String hmac = substringBetween(html, "name=\"hmac\" value=\"", "\"/>");

            // Authenticate: Username
            logger.trace("{}: OAuth input: User", config.vehicle.vin);
            url = CNAPI_OAUTH_BASE_URL + "/signin-service/v1/" + config.clientId + "/login/identifier";
            data.put("_csrf", csrf);
            data.put("relayState", relayState);
            data.put("hmac", hmac);
            data.put("email", URLEncoder.encode(config.account.user, UTF_8));
            http.post(url, headers, data, false);

            // Authenticate: Password
            logger.trace("{}: OAuth input: Password", config.vehicle.vin);
            url = CNAPI_OAUTH_BASE_URL + http.getRedirect(); // Signin URL
            html = http.get(url, headers);
            csrf = substringBetween(html, "name=\"_csrf\" value=\"", "\"/>");
            relayState = substringBetween(html, "name=\"relayState\" value=\"", "\"/>");
            hmac = substringBetween(html, "name=\"hmac\" value=\"", "\"/>");

            logger.trace("{}: OAuth input: Authenticate", config.vehicle.vin);
            url = CNAPI_OAUTH_BASE_URL + "/signin-service/v1/" + config.clientId + "/login/authenticate";
            data.clear();
            data.put("email", URLEncoder.encode(config.account.user, UTF_8));
            data.put("password", URLEncoder.encode(config.account.password, UTF_8));
            data.put("_csrf", csrf);
            data.put("relayState", relayState);
            data.put("hmac", hmac);
            http.post(url, headers, data, false);
            url = http.getRedirect(); // Continue URL

            // String userId = "";
            String authCode = "";
            String id_token = "";
            String accessToken = "";
            String expires_in = "";
            int count = 10;
            while (count-- > 0) {
                html = http.get(url, headers);
                url = http.getRedirect(); // Continue URL
                // if (url.contains("&user_id=")) {
                // userId = getUrlParm(url, "user_id");
                // }
                if (url.contains("&code=")) {
                    authCode = getUrlParm(url, "code");
                    break; // that's what we are looking for
                }
                if (url.contains("&id_token=")) {
                    id_token = getUrlParm(url, "id_token");
                }
                if (url.contains("&expires_in=")) {
                    expires_in = getUrlParm(url, "expires_in");
                }
                if (url.contains("&access_token=")) {
                    accessToken = getUrlParm(url, "access_token");
                    break; // that's what we are looking for
                }
            }

            CNApiToken token;
            String json = "";
            if (!id_token.isEmpty()) {
                logger.trace("{}: OAuth successful, idToken and accessToken retrieved", config.vehicle.vin);
                tokens.idToken = new CarNetToken(id_token, accessToken, "bearer", Integer.parseInt(expires_in, 10));
            } else {
                if (authCode.isEmpty()) {
                    logger.debug("{}: Unable to obtain authCode, last url={}, last response: {}", config.vehicle.vin,
                            url, html);
                    throw new CarNetException("Unable to complete OAuth, check credentials");
                }

                logger.trace("{}: OAuth successful, obtain ID token (auth code={})", config.vehicle.vin, authCode);
                headers.clear();
                headers.put(HttpHeader.ACCEPT.toString(), "application/json, text/plain, */*");
                headers.put(HttpHeader.CONTENT_TYPE.toString(), "application/json");
                headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");

                long tsC = parseDate(config.oidcDate);
                // long n = System.currentTimeMillis();
                long ts1 = System.currentTimeMillis() - tsC;
                long ts2 = System.currentTimeMillis();
                long ts = ts1 + ts2;
                String s = ((Long) (ts / 100000)).toString();
                headers.put("X-QMAuth", "v1:934928ef:" + s);

                data.clear();
                data.put("client_id", config.clientId);
                data.put("grant_type", "authorization_code");
                data.put("code", authCode);
                data.put("redirect_uri", config.redirect_uri);
                data.put("response_type", "token id_token");
                json = http.post(CNAPI_AUDI_TOKEN_URL, headers, data, true);

                // process token
                token = gson.fromJson(json, CNApiToken.class);
                if ((token.accessToken == null) || token.accessToken.isEmpty()) {
                    throw new CarNetException("Authentication failed: Unable to get access token!");
                }

                tokens.idToken = new CarNetToken(token);
            }
            logger.debug("{}: OAuth successful", config.vehicle.vin);

            logger.debug("{}: Get Audi Token", config.vehicle.vin);
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            data.clear();
            data.put("config", "myaudi");
            data.put("grant_type", "id_token");
            data.put("stage", "live");
            data.put("token", tokens.idToken.accessToken);
            json = http.post("https://app-api.live-my.audi.com/azs/v1/token", headers, data, true);
            token = gson.fromJson(json, CNApiToken.class);
            if (token.validity < 0) {
                token.validity = tokens.idToken.validity;
            }
            tokens.brandToken = new CarNetToken(token);
            updateTokenSet(config.tokenSetId, tokens);

            return tokens.brandToken.accessToken;
        } catch (CarNetException e) {
            logger.info("{}: Login failed: {}", config.vehicle.vin, e.toString());
        } catch (UnsupportedEncodingException e) {
            throw new CarNetException("Login failed", e);
        } catch (Exception e) {
            logger.info("{}: Login failed: {}", config.vehicle.vin, e.getMessage());
        }
        return "";
    }

    public String createVwToken(CarNetCombinedConfig config) throws CarNetException {
        TokenSet tokens = getTokenSet(config.tokenSetId);

        if (!tokens.vwToken.isExpired()) {
            return tokens.vwToken.accessToken;
        }
        createBrandToken(config);

        // "User-Agent": "okhttp/3.7.0",
        // "X-App-Version": "3.14.0",
        // "X-App-Name": "myAudi",
        // "X-Client-Id": "77869e21-e30a-4a92-b016-48ab7d3db1d8",
        // "Host": "mbboauth-1d.prd.ece.vwg-connect.com",
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(CNAPI_HEADER_VERS, "3.14.0");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(CNAPI_HEADER_CLIENTID, "77869e21-e30a-4a92-b016-48ab7d3db1d8");
        // https://github.com/TA2k/ioBroker.vw-connect/blob/master/main.js
        // VW: clientId = "9496332b-ea03-4091-a224-8c746b885068%40apps_vw-dilab_com";
        // VW: xclientId = "38761134-34d0-41f3-9a73-c4be88d7d337";
        // VW: xappversion = "5.1.2";
        // VW: xappname = "eRemote";
        headers.put(CNAPI_HEADER_HOST, "mbboauth-1d.prd.ece.vwg-connect.com");
        headers.put(HttpHeader.ACCEPT.toString(), "*/*");
        Map<String, String> data = new TreeMap<>();
        data.put("grant_type", "id_token");
        // data.put("token", idToken.idToken);
        data.put("token", tokens.idToken.idToken);
        data.put("scope", "sc2:fal");

        String json = http.post(CNAPI_URL_GET_SEC_TOKEN, headers, data, false);
        CNApiToken token = gson.fromJson(json, CNApiToken.class);
        if ((token.accessToken == null) || token.accessToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        tokens.vwToken = new CarNetToken(token);
        updateTokenSet(config.tokenSetId, tokens);
        return tokens.vwToken.accessToken;
    }

    public String createSecurityToken(CarNetCombinedConfig config, String service, String action)
            throws CarNetException {
        if (config.vehicle.pin.isEmpty()) {
            throw new CarNetException("No SPIN is confirgured, can't perform authentication");
        }

        Iterator<CarNetToken> it = securityTokens.iterator();
        while (it.hasNext()) {
            CarNetToken stoken = it.next();
            if (stoken.service.equals(service) && stoken.isValid()) {
                return stoken.securityToken;
            }
        }

        String accessToken = createVwToken(config);

        // "User-Agent": "okhttp/3.7.0",
        // "X-App-Version": "3.14.0",
        // "X-App-Name": "myAudi",
        // "Accept": "application/json",
        // "Authorization": "Bearer " + self.vwToken.get("access_token"),
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeader.USER_AGENT.toString(), "okhttp/3.7.0");
        headers.put(CNAPI_HEADER_VERS, "3.14.0");
        headers.put(CNAPI_HEADER_APP, CNAPI_HEADER_APP_MYAUDI);
        headers.put(HttpHeader.ACCEPT.toString(), CNAPI_ACCEPTT_JSON);

        // Build Hash: SHA512(SPIN+Challenge)
        String url = "https://mal-1a.prd.ece.vwg-connect.com/api/rolesrights/authorization/v2/vehicles/"
                + config.vehicle.vin.toUpperCase() + "/services/" + service + "/operations/" + action
                + "/security-pin-auth-requested";
        String json = http.get(url, headers, accessToken);
        CarNetSecurityPinAuthInfo authInfo = gson.fromJson(json, CarNetSecurityPinAuthInfo.class);
        String pinHash = sha512(config.vehicle.pin, authInfo.securityPinAuthInfo.securityPinTransmission.challenge)
                .toUpperCase();
        logger.debug("Authenticating SPIN, retires={}",
                authInfo.securityPinAuthInfo.securityPinTransmission.remainingTries);

        CarNetSecurityPinAuthentication pinAuth = new CarNetSecurityPinAuthentication();
        pinAuth.securityPinAuthentication.securityToken = authInfo.securityPinAuthInfo.securityToken;
        pinAuth.securityPinAuthentication.securityPin.challenge = authInfo.securityPinAuthInfo.securityPinTransmission.challenge;
        pinAuth.securityPinAuthentication.securityPin.securityPinHash = pinHash;
        String data = gson.toJson(pinAuth);
        json = http.post(
                "https://mal-1a.prd.ece.vwg-connect.com/api/rolesrights/authorization/v2/security-pin-auth-completed",
                headers, data);
        CarNetToken securityToken = new CarNetToken(gson.fromJson(json, CNApiToken.class));
        if (securityToken.securityToken.isEmpty()) {
            throw new CarNetException("Authentication failed: Unable to get access token!");
        }
        logger.debug("securityToken granted successful!");
        synchronized (securityTokens) {
            securityToken.setService(service);
            if (securityTokens.contains(securityToken)) {
                securityTokens.remove(securityToken);
            }
            securityTokens.add(securityToken);
        }
        return securityToken.securityToken;
    }

    /**
     *
     * Request/refreh the different tokens
     * accessToken, which is required to access the API
     * idToken, which is required to request the securityToken and
     * securityToken, which is required to perform control functions
     *
     * The validity is checked and if token is not expired it will be reused.
     *
     * @throws CarNetException
     */
    public boolean refreshTokens(CarNetCombinedConfig config) throws CarNetException {
        try {
            TokenSet tokens = getTokenSet(config.tokenSetId);
            refreshToken(config, config.account.brand, tokens.brandToken);
            refreshToken(config, CNAPI_BRAND_VW, tokens.vwToken);

            Iterator<CarNetToken> it = securityTokens.iterator();
            while (it.hasNext()) {
                CarNetToken stoken = it.next();
                if (!refreshToken(config, config.account.brand, stoken)) {
                    // Token invalid / refresh failed -> remove
                    securityTokens.remove(stoken);
                }
            }
        } catch (CarNetException e) {
            // Ignore problems with the idToken or securityToken if the accessToken was requested successful
            logger.debug("Unable to create secondary token: {}", e.toString()); // "normal, no stack trace"
        } catch (IllegalArgumentException e) {
            logger.debug("Invalid token!");
        }
        return false;
    }

    public boolean refreshToken(CarNetCombinedConfig config, String brand, CarNetToken token) throws CarNetException {
        if (!token.isValid()) {
            // token is still valid
            return false;
        }

        TokenSet tokens = getTokenSet(config.tokenSetId);
        if (token.isExpired()) {
            logger.debug("{}: Refreshing Token {}", config.vehicle.vin, token);
            String url = "";
            String rtoken = "";
            Map<String, String> data = new TreeMap<>();

            if (isBrandAudi(brand)) {
                url = CNAPI_URL_AUDI_GET_TOKEN;
                // rtoken = idToken.refreshToken;
                rtoken = tokens.brandToken.refreshToken;
                url = CNAPI_URL_GET_SEC_TOKEN;
                data.put("client_id", config.clientId);
                data.put("grant_type", "refresh_token");
                data.put("response_type", "token id_token");
                data.put("refresh_token", rtoken);
            } else if (isBrandVW(brand)) {
                url = CNAPI_URL_GET_SEC_TOKEN;
                rtoken = tokens.vwToken.refreshToken;
                data.put("grant_type", "refresh_token");
                data.put("scope", "scope=sc2%3Afa");
                data.put("token", rtoken);
            } else if (isBrandSkoda(brand)) {

            } else if (isBrandGo(brand)) {
                url = CNAPI_URL_GO_GET_TOKEN;
                data.put("client_id", config.clientId);
                data.put("scope", "openid+profile+address+email+phone");
                data.put("grant_type", "refresh_token");
                data.put("refresh_token", rtoken);
            } else {
                data.put("refresh_token=", rtoken);
                url = CNAPI_URL_DEF_GET_TOKEN;
                rtoken = tokens.brandToken.refreshToken; // not sure if that is the correct default one
            }

            try {
                String json = http.post(url, http.fillRefreshHeaders(), data, false);
                return true;
            } catch (CarNetException e) {
                logger.debug("{}: Unable to refresh token {} - {}", config.vehicle.vin, token, e.getApiResult());
            }
        }

        // Invalidate token
        // token.invalidate();
        return false;
    }

    public boolean createTokenSet(String tokenSetId) {
        if (!accountTokens.containsKey(tokenSetId)) {
            accountTokens.put(tokenSetId, new TokenSet());
            return true;
        }
        return false;
    }

    TokenSet getTokenSet(String tokenSetId) {
        if (accountTokens.containsKey(tokenSetId)) {
            return accountTokens.get(tokenSetId);
        }
        throw new IllegalArgumentException("tokenSetId is invalid");
    }

    synchronized void updateTokenSet(String tokenSetId, TokenSet tokens) {
        if (accountTokens.containsKey(tokenSetId)) {
            accountTokens.remove(tokenSetId);
        }
        accountTokens.put(tokenSetId, tokens);
    }
}
