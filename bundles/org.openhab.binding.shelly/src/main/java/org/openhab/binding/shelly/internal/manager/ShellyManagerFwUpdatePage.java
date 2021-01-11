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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.Thing;
import org.openhab.binding.shelly.internal.api.ShellyApiException;
import org.openhab.binding.shelly.internal.api.ShellyApiJsonDTO.ShellySettingsUpdate;
import org.openhab.binding.shelly.internal.api.ShellyHttpApi;
import org.openhab.binding.shelly.internal.config.ShellyThingConfiguration;
import org.openhab.binding.shelly.internal.handler.ShellyBaseHandler;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * {@link ShellyManagerFwUpdatePage} implements the Shelly Manager's firmware update page
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class ShellyManagerFwUpdatePage extends ShellyManagerPage {
    public ShellyManagerFwUpdatePage(ConfigurationAdmin configurationAdmin, HttpClient httpClient,
            Map<String, ShellyBaseHandler> thingHandlers) {
        super(configurationAdmin, httpClient, thingHandlers);
    }

    @Override
    public String generateContent(Map<String, String[]> parameters) throws ShellyApiException {
        String uid = getUrlParm(parameters, "uid");
        String version = getUrlParm(parameters, "version");
        String update = getUrlParm(parameters, "update");
        if (uid.isEmpty() || version.isEmpty() || !thingHandlers.containsKey(uid)) {
            return "Invalid URL parameters";
        }

        String html = loadHTML(HEADER_HTML);
        ShellyBaseHandler th = thingHandlers.get(uid);
        if (th != null) {
            Map<String, String> properties = fillProperties(new HashMap<>(), uid, th);
            Thing thing = th.getThing();
            ShellyThingConfiguration config = getThingConfig(th, properties);
            String deviceType = getDeviceType(properties);
            String url = getFirmwareUrl(config.deviceIp, deviceType, version);
            properties.put("metaTag", "");
            properties.put("uid", uid);
            properties.put("version", version);
            properties.put("firmwareUrl", url);

            if (update.equalsIgnoreCase("yes")) {
                // do the update
                ShellyHttpApi api = new ShellyHttpApi(uid, config, httpClient);
                ShellySettingsUpdate result = api.firmwareUpdate(url);
                properties.put("updateStatus", result.status);
                String output = "<p/>Updating device ${deviceName} (${uid}) with version ${version}<br/>"
                        + "Firmware url for update: ${firmwareUrl}<p/>";
                if (result.status.equalsIgnoreCase("updating")) {
                    output += "Update was started, device returned status '{$updateStatus}'<br>";
                    output += "Wait 1 minute, then check device UI at <a href=http://${deviceIp}>${deviceIp}</a>, section Firmware.<p/>";
                } else {
                    output += "<p style=\"color:red;\">Device ${serviceName} did not accepted the update request, status=${updateStatus}<p/>";
                }
                html += fillPage(output, uid, th, properties);
            } else {
                properties.put("updateType", "Device connected to Internet");
                String message = "Do not power-off or restart device while updading the firmware!";
                message += "Make sure device is connected to the Internet.";
                properties.put("message", message);
                html += fillPage(loadHTML(FWUPDATE_HTML), uid, th, properties);
            }
        }
        html += loadHTML(FOOTER_HTML);
        return html;
    }

    protected String getFirmwareUrl(String deviceIp, String deviceType, String version) throws ShellyApiException {
        switch (version) {
            case FWPROD:
                return "update=true"; // update from regular url
            case FWBETA:
                return "beta=true"; // update from beta url
            default: // Update from firmware archive
                FwaList list = getFirmwareList(deviceType);
                ArrayList<FwaList.FwalEntry> versions = list.versions;
                if (versions != null) {
                    for (FwaList.FwalEntry e : versions) {
                        if (getString(e.version).equalsIgnoreCase(version)) {
                            return "url=" + FWLISTARCH_FILE + version + "/" + getString(e.file);
                        }
                    }
                }
        }
        return "";
    }
}
