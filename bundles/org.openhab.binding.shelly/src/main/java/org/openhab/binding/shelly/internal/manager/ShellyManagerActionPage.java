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
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
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

    public ShellyManagerActionPage(ConfigurationAdmin configurationAdmin, HttpClient httpClient, String localIp,
            int localPort, Map<String, ShellyBaseHandler> thingHandlers) {
        super(configurationAdmin, httpClient, localIp, localPort, thingHandlers);
    }

    @Override
    public ShellyMgrResponse generateContent(String path, Map<String, String[]> parameters) throws ShellyApiException {
        String action = getUrlParm(parameters, "action");
        String uid = getUrlParm(parameters, "uid");
        String update = getUrlParm(parameters, "update");
        if (uid.isEmpty() || action.isEmpty()) {
            return new ShellyMgrResponse("Invalid URL parameters: " + parameters.toString(),
                    HttpStatus.BAD_REQUEST_400);
        }

        Map<String, String> properties = new HashMap<>();
        properties.put("metaTag", "");
        properties.put("cssHeader", "");
        properties.put("cssFooter", "");
        String html = loadHTML(HEADER_HTML, properties);

        ShellyBaseHandler th = thingHandlers.get(uid);
        if (th != null) {
            fillProperties(properties, uid, th);

            Map<String, String> actions = getActions();
            String actionUrl = SHELLY_MGR_OVERVIEW_URI;
            String actionButtonLabel = "Perform Action"; // Default
            String serviceName = getValue(properties, "serviceName");
            String message = "";

            ShellyThingConfiguration config = getThingConfig(th, properties);
            ShellyHttpApi api = new ShellyHttpApi(uid, config, httpClient);

            switch (action) {
                case SHELLY_MGR_ACTION_RESTART:
                    if (update.equalsIgnoreCase("yes")) {
                        message = "The device is restarting and reconnects to WiFi. It will take a moment until device status is refreshed in openHAB.";
                        actionButtonLabel = "Ok";
                        new Thread(() -> { // schedule asynchronous reboot
                            try {
                                api.deviceReboot();
                            } catch (ShellyApiException e) {
                                // maybe the device restarts before returning the http response
                            }
                            setRestarted(th, uid); // refresh 20s after reboot
                        }).start();
                    } else {
                        message = "The device will restart and reconnects to WiFi.";
                        actionUrl = buildActionUrl(uid, action);
                    }
                    break;
                case SHELLY_MGR_ACTION_RESET:
                    if (!update.equalsIgnoreCase("yes")) {
                        message = "<p style=\"color:red;\">Attention: Performing this action will reset the device to factory defaults.<br/>"
                                + "All configuration data incl. WiFi settings get lost and device will return to Access Point mode (WiFi "
                                + serviceName + ").</p>";
                        actionUrl = buildActionUrl(uid, action);
                    } else {
                        message = "<p style=\"color:blue;\">Factorry reset was performed. Connect to WiFi network "
                                + serviceName + " and open http://192.168.33.1 to restart with device setup.</p>";
                        actionButtonLabel = "Ok";
                        new Thread(() -> { // schedule asynchronous reboot
                            try {
                                api.factoryReset();
                            } catch (ShellyApiException e) {
                                // maybe the device restarts before returning the http response
                            }
                            setRestarted(th, uid);
                        }).start();
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
                        message += "<p style=\"color:yellow;\">Device login will be set to user ${userId} with password ${password}.</p>";
                        actionUrl = buildActionUrl(uid, action);
                    } else {
                        api.setLoginCredentials(config.userId, config.password);
                        message = "<p style=\"color:green;\">Device login was updated to user ${userId} with password ${password}.</p>";
                        actionButtonLabel = "Ok";
                    }
                    break;
                case SHELLY_MGR_ACTION_RES_STATS:
                    th.resetStats();
                    actionButtonLabel = "Ok";
                    break;
                default:
                    logger.warn("{}: Unknown action {} requested", LOG_PREFIX, action);
            }

            properties.put("action", getString(actions.get(action))); // get description for command
            properties.put("actionButtonLabel", actionButtonLabel);
            properties.put("actionUrl", actionUrl);
            message = fillAttributes(message, properties);
            properties.put("message", message);
            html += loadHTML(ACTION_HTML, properties);
        }

        properties.clear();
        html += loadHTML(FOOTER_HTML, properties);
        return new ShellyMgrResponse(html, HttpStatus.OK_200);
    }

    public static Map<String, String> getActions() {
        Map<String, String> list = new LinkedHashMap<>();
        list.put(SHELLY_MGR_ACTION_RESTART, "Reboot Device");
        list.put(SHELLY_MGR_ACTION_PROTECT, "Protect Device");
        list.put(SHELLY_MGR_ACTION_RESET, "Factory Reset");
        list.put(SHELLY_MGR_ACTION_RES_STATS, "Reset Statistics");
        return list;
    }

    private String buildActionUrl(String uid, String action) {
        return SHELLY_MGR_ACTION_URI + "?action=" + action + "&uid=" + urlEncode(uid) + "&update=yes";
    }

    private void setRestarted(ShellyBaseHandler th, String uid) {
        th.setThingOffline(ThingStatusDetail.GONE, "offline.status-error-restarted");
        scheduleUpdate(th, uid + "_upgrade", 20); // wait 20s before refresh
    }
}
