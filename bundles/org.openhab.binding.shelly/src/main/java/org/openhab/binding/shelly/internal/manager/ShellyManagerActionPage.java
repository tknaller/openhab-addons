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
package org.openhab.binding.shelly.internal.manager;

import static org.openhab.binding.shelly.internal.manager.ShellyManagerConstants.*;
import static org.openhab.binding.shelly.internal.util.ShellyUtils.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.Thing;
import org.openhab.binding.shelly.internal.api.ShellyApiException;
import org.openhab.binding.shelly.internal.api.ShellyApiJsonDTO.ShellySettingsLogin;
import org.openhab.binding.shelly.internal.api.ShellyHttpApi;
import org.openhab.binding.shelly.internal.config.ShellyThingConfiguration;
import org.openhab.binding.shelly.internal.handler.ShellyBaseHandler;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ShellyManagerActionPage} implements the Shelly Manager's action page
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class ShellyManagerActionPage extends ShellyManagerPage {
    private final Logger logger = LoggerFactory.getLogger(ShellyManagerActionPage.class);

    public ShellyManagerActionPage(ConfigurationAdmin configurationAdmin, HttpClient httpClient,
            Map<String, ShellyBaseHandler> thingHandlers) {
        super(configurationAdmin, httpClient, thingHandlers);
    }

    @Override
    public String generateContent(Map<String, String[]> parameters) throws ShellyApiException {
        String action = getUrlParm(parameters, "action");
        String uid = getUrlParm(parameters, "uid");
        String update = getUrlParm(parameters, "update");
        if (uid.isEmpty() || action.isEmpty()) {
            return "Invalid URL parameters: " + parameters.toString();
        }

        String html = loadHTML(HEADER_HTML);
        ShellyBaseHandler th = thingHandlers.get(uid);
        if (th != null) {
            Thing thing = th.getThing();
            Map<String, String> properties = fillProperties(new HashMap<>(), uid, th);

            Map<String, String> actions = getActions();
            String actionUrl = SHELLY_MGR_OVERVIEW_URI;
            String actionButtonLabel = "Perform Action"; // Default
            String message = "";

            ShellyThingConfiguration config = getThingConfig(th, properties);
            ShellyHttpApi api = new ShellyHttpApi(uid, config, httpClient);

            switch (action) {
                case SHELLY_MGR_ACTION_RESTART:
                    api.deviceReboot();
                    message = "The device is restarting and reconnects to WiFi. It will take a moment until device status is refreshed in openHAB.";
                    actionButtonLabel = "Ok";
                    break;
                case SHELLY_MGR_ACTION_RESET:
                    if (!update.equalsIgnoreCase("yes")) {
                        message = "<p style=\"color:red;\">Attention: Performing this action will reset the device to factory defaults.<br/>"
                                + "All configuration data incl. WiFi settings get lost and device will return to Access Point mode (WiFi ${serviceName}).</p>";
                        actionUrl = buildActionUrl(uid, action);
                    } else {
                        message = "<p style=\"color:blue;\">Factorry reset was performed. Connect to WiFi network ${serviceName} and open http://192.168.33.1 to restart with device setup.</p>";
                        actionButtonLabel = "Ok";
                    }
                    break;
                case SHELLY_MGR_ACTION_PROTECT:
                    // Get device settings
                    if (config.userId.isEmpty() || config.password.isEmpty()) {
                        message = "<p style=\"color:red;\">To use this feature you need to set default credentials in the Shelly Binding settings.</p>";
                        break;
                    }

                    if (!update.equalsIgnoreCase("yes")) {
                        ShellySettingsLogin status = api.getLoginSettings();
                        message = "Device protection is currently " + (status.enabled ? "enabled" : "disabled<br/>");
                        message += "<p style=\"color:yellow;\">Device login will be set to user ${user} with password ${password}.</p>";
                        actionUrl = buildActionUrl(uid, action);
                    } else {
                        api.setLoginCredentials(config.userId, config.password);
                        message = "<p style=\"color:green;\">Device login was updated to user ${user} with password ${password}.</p>";
                        actionButtonLabel = "Ok";
                    }
                    break;
                default:
                    logger.warn("{}: Unknown action {} requested", LOG_PREFIX, action);
            }

            properties.put("action", getString(actions.get(action))); // get description for command
            properties.put("actionButtonLabel", actionButtonLabel);
            properties.put("actionUrl", actionUrl);
            properties.put("message", message);
            html += fillPage(loadHTML(ACTION_HTML), uid, th, properties);
        }
        html += loadHTML(FOOTER_HTML);
        return html;
    }

    public static Map<String, String> getActions() {
        Map<String, String> list = new LinkedHashMap<>();
        list.put(SHELLY_MGR_ACTION_RESTART, "Reboot Device");
        list.put(SHELLY_MGR_ACTION_PROTECT, "Protect Device");
        list.put(SHELLY_MGR_ACTION_RESET, "Factory Reset");
        return list;
    }

    private String buildActionUrl(String uid, String action) {
        return SHELLY_MGR_ACTION_URI + "?action=" + action + "&uid=" + urlEncode(uid) + "&update=yes";
    }
}
