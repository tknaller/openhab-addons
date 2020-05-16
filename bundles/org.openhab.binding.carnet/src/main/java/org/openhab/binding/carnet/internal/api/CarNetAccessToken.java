package org.openhab.binding.carnet.internal.api;

import java.text.MessageFormat;
import java.util.Date;

import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiToken;

public class CarNetAccessToken {
    protected String accessToken = "";
    protected String authType = "";
    protected Integer authVersion = 1;
    protected Integer validity = -1;
    private Date creationTime;

    public CarNetAccessToken() {

    }

    public CarNetAccessToken(CarNetApiToken token) {
        accessToken = token.accesToken;
        authType = token.authType;
        validity = token.validity;
        creationTime = new Date();
    }

    public String getHttpHeader() {
        return MessageFormat.format("Authorization: {0} {1} {2}", authType, authVersion, accessToken);
    }

    public Boolean isExpired() {
        if (creationTime == null) {
            return true;
        }

        Date currentTime = new Date();

        long diff = currentTime.getTime() - creationTime.getTime();

        return (diff / 1000) > validity;
    }
}
