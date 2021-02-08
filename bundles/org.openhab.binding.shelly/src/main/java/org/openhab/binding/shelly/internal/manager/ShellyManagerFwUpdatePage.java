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
import static org.openhab.binding.shelly.internal.util.ShellyUtils.getString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.openhab.binding.shelly.internal.api.ShellyApiException;
import org.openhab.binding.shelly.internal.api.ShellyApiJsonDTO.ShellySettingsUpdate;
import org.openhab.binding.shelly.internal.api.ShellyDeviceProfile;
import org.openhab.binding.shelly.internal.api.ShellyHttpApi;
import org.openhab.binding.shelly.internal.config.ShellyThingConfiguration;
import org.openhab.binding.shelly.internal.handler.ShellyBaseHandler;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ShellyManagerFwUpdatePage} implements the Shelly Manager's firmware update page
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class ShellyManagerFwUpdatePage extends ShellyManagerPage {
    protected final Logger logger = LoggerFactory.getLogger(ShellyManagerFwUpdatePage.class);

    public ShellyManagerFwUpdatePage(ConfigurationAdmin configurationAdmin, HttpClient httpClient,
            Map<String, ShellyBaseHandler> thingHandlers) {
        super(configurationAdmin, httpClient, thingHandlers);
    }

    @Override
    public String generateContent(Map<String, String[]> parameters) throws ShellyApiException {
        String uid = getUrlParm(parameters, "uid");
        String version = getUrlParm(parameters, "version");
        String update = getUrlParm(parameters, "update");
        String source = getUrlParm(parameters, "source");
        if (uid.isEmpty() || version.isEmpty() || !thingHandlers.containsKey(uid)) {
            return "Invalid URL parameters: " + parameters;
        }

        Map<String, String> properties = new HashMap<>();
        String html = loadHTML(HEADER_HTML, properties);
        ShellyBaseHandler th = thingHandlers.get(uid);
        if (th != null) {
            properties = fillProperties(new HashMap<>(), uid, th);
            ShellyThingConfiguration config = getThingConfig(th, properties);
            ShellyDeviceProfile profile = th.getProfile();
            String deviceType = getDeviceType(properties);
            String deviceName = getDeviceName(properties);
            String uri = getFirmwareUrl(config.deviceIp, deviceType, version);
            String url = "http://" + getDeviceIp(properties) + "/ota?" + uri;
            properties.put("uid", uid);
            properties.put("version", version);
            properties.put("firmwareUrl", url);

            if (update.equalsIgnoreCase("yes")) {
                // do the update
                th.setThingOffline(ThingStatusDetail.FIRMWARE_UPDATING, "offline.status-error-fwupgrade");
                String output = "<p/>Updating device ${deviceName} (${uid}) with version ${version}, source=" + source
                        + "<br/>" + "Firmware url for update: ${firmwareUrl}<p/>"
                        + "Wait 2 minutes, then check device UI at <a href=\"http://${deviceIp}\" title=\"${thingName}\" target=\"_blank\">${deviceIp}</a>, section Firmware.<p/>";
                html += fillAttributes(output, properties);

                new Thread(() -> { // schedule asynchronous reboot
                    try {
                        ShellyHttpApi api = new ShellyHttpApi(uid, config, httpClient);
                        ShellySettingsUpdate result = api.firmwareUpdate(uri);
                        String status = getString(result.status);
                        logger.info("{}: Firmware update initiated, device returned status {}", deviceName, status);

                        // Shelly Motion needs almost 2min for upgrade
                        scheduleUpdate(th, uid + "_check", profile.isMotion ? 110 : 30);
                    } catch (ShellyApiException e) {
                        // maybe the device restarts before returning the http response
                        logger.warn("{}: Firmware updated failed: {}", deviceName, e.toString());
                    }
                }).start();
            } else {
                String message = "Do not power-off or restart device while updading the firmware!<br/>";
                message += "Make sure device is connected to the Internet.";
                properties.put("message", message);
                html += loadHTML(FWUPDATE_HTML, properties);
            }
        }

        html += loadHTML(FOOTER_HTML, properties);
        return html;
    }

    protected String getFirmwareUrl(String deviceIp, String deviceType, String version) throws ShellyApiException {
        switch (version) {
            case FWPROD:
                return "update=true"; // update from regular url
            case FWBETA:
                return "beta=true"; // update from beta url
            default: // Update from firmware archive
                FwaList list = getFirmwareArchiveList(deviceType);
                ArrayList<FwaList.FwalEntry> versions = list.versions;
                if (versions != null) {
                    for (FwaList.FwalEntry e : versions) {
                        if (getString(e.version).equalsIgnoreCase(version)) {
                            return "url=" + FWREPO_ARCFILE_URL + version + "/" + getString(e.file);
                        }
                    }
                }
        }
        return "";
    }

    private void scheduleUpdate(ShellyBaseHandler th, String name, int delay) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                th.requestUpdates(1, true);
            }
        };
        Timer timer = new Timer(name);
        timer.schedule(task, delay * 1000);
    }
}
