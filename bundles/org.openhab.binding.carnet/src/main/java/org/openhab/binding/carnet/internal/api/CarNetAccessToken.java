package org.openhab.binding.carnet.internal.api;

import java.text.MessageFormat;

import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiToken;

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
}
