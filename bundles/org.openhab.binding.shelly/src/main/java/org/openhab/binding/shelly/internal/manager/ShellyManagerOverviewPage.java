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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.shelly.internal.api.ShellyApiException;
import org.openhab.binding.shelly.internal.handler.ShellyBaseHandler;
import org.openhab.binding.shelly.internal.manager.ShellyManagerPage.FwaList.FwalEntry;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ShellyManagerFwUpdatePage} implements the Shelly Manager's device overview page
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class ShellyManagerOverviewPage extends ShellyManagerPage {
    private final Logger logger = LoggerFactory.getLogger(ShellyManagerOverviewPage.class);

    public ShellyManagerOverviewPage(ConfigurationAdmin configurationAdmin, HttpClient httpClient,
            Map<String, ShellyBaseHandler> thingHandlers) {
        super(configurationAdmin, httpClient, thingHandlers);
    }

    @Override
    public String generateContent(Map<String, String[]> parameters) throws ShellyApiException {
        logger.info("Generating overview for {} devices", thingHandlers.size());
        firmwareListHtml = new HashMap<>(); // re-load list each time we generate an overview

        Map<String, String> properties = new HashMap<>();
        properties.put("metaTag", "<meta http-equiv=\"refresh\" content=\"60\" />");

        String html = loadHTML(HEADER_HTML) + loadHTML(OVERVIEW_HTML);
        String deviceHtml = "";
        for (Map.Entry<String, ShellyBaseHandler> thing : thingHandlers.entrySet()) {
            try {
                String uid = thing.getKey();
                ShellyBaseHandler th = thing.getValue();

                logger.info("Generating info for UID {}", uid);
                fillProperties(properties, uid, th);
                String deviceType = getDeviceType(properties);
                if (!deviceType.equalsIgnoreCase("unknown")) { // pw-protected device
                    properties.put(ATTRIBUTE_FIRMWARE_SEL, fillFirmwareList(uid, deviceType));
                    properties.put(ATTRIBUTE_ACTION_LIST, fillActionList(uid));
                }
                html += fillPage(loadHTML(DEVICE_HTML), properties);
            } catch (ShellyApiException e) {
                logger.info("{}: Exception", LOG_PREFIX, e);
            }
        }
        html += deviceHtml + loadHTML(FOOTER_HTML);
        return fillAttributes(html, properties);
    }

    private String fillFirmwareList(String uid, String deviceType) throws ShellyApiException {
        if (firmwareListHtml.containsKey(deviceType)) {
            return getString(firmwareListHtml.get(deviceType));
        }

        String html = "\n\t\t\t\t<select name=\"fwList\" id=\"fwList\" onchange=\"location = this.options[this.selectedIndex].value;\">\n";
        html += "\t\t\t\t\t<option value=\"\" selected disabled hidden>update to</option>\n";

        String pVersion = "";
        String bVersion = "";
        String updateUrl = SHELLY_MGR_FWUPDATE_URI + "?uid=" + urlEncode(uid);
        try {
            // Get current prod + beta version from original firmware repo
            logger.debug("{}: Load firmware version list for device type {}", LOG_PREFIX, deviceType);
            String json = httpGet(FWREPO_URL); // return a strange JSON format so we are parsing this manually
            String entry = substringBetween(json, "\"" + deviceType + "\":{", "}");
            if (!entry.isEmpty()) {
                entry = "{" + entry + "}";
                FwRepoEntry fw = fromJson(gson, entry, FwRepoEntry.class);

                /*
                 * Example:
                 * "SHPLG-1":{
                 * "url":"http:\/\/repo.shelly.cloud\/firmware\/SHPLG-1.zip",
                 * "version":"20201228-092318\/v1.9.3@ad2bb4e3",
                 * "beta_url":"http:\/\/repo.shelly.cloud\/firmware\/rc\/SHPLG-1.zip",
                 * "beta_ver":"20201223-093703\/v1.9.3-rc5@3f583801"
                 * },
                 */
                pVersion = substringBetween(getString(fw.version), "/", "@");
                if (!pVersion.isEmpty()) {
                    html += "\t\t\t\t\t<option value=\"" + updateUrl + "&version=" + FWPROD + "\">Release " + pVersion
                            + "</option>\n";
                }
                bVersion = substringBetween(getString(fw.beta_ver), "/", "@");
                if (!bVersion.isEmpty()) {
                    html += "\t\t\t\t\t<option value=\"" + updateUrl + "&version=" + FWBETA + "\">Beta " + bVersion
                            + "</option>\n";
                }
            }

            // Add those from Shelly Firmware Archive
            json = httpGet(FWLISTARCH_URL + "?type=" + deviceType);
            if (json.startsWith("[]")) {
                // no files available for this device type
                logger.info("{}: No firmware files found for device type {}", LOG_PREFIX, deviceType);
            } else {
                // Create selection list
                json = "{" + json.replace("[{", "\"versions\":[{") + "}"; // make it an named array
                FwaList list = getFirmwareList(deviceType);
                ArrayList<FwalEntry> versions = list.versions;
                if (versions != null) {
                    html += "\t\t\t\t\t<option value=\"\" disabled hidden>- Archive -</option>\n";
                    for (int i = versions.size() - 1; i >= 0; i--) {
                        FwalEntry e = versions.get(i);
                        String version = getString(e.version);
                        if (!version.equalsIgnoreCase(pVersion) && !version.equalsIgnoreCase(bVersion)) {
                            html += "\t\t\t\t\t<option value=\"" + updateUrl + "&version=" + version + "\">" + version
                                    + "</option>\n";
                        }
                    }
                    html += "\t\t\t\t</select>\n\t\t\t";
                }
            }
        } catch (ShellyApiException e) {
            logger.debug("{}: Unable to retrieve firmware list: {}", LOG_PREFIX, e.toString());
        }

        firmwareListHtml.put(deviceType, html);
        return html;
    }

    private String fillActionList(String uid) {
        Map<String, String> actionList = ShellyManagerActionPage.getActions();
        String html = "\n\t\t\t\t<select name=\"actionList\" id=\"actionList\" onchange=\"location = '"
                + SHELLY_MGR_ACTION_URI + "?uid=" + urlEncode(uid)
                + "&action='+this.options[this.selectedIndex].value;\">\n";
        html += "\t\t\t\t\t<option value=\"\" selected disabled hidden>select</option>\n";
        for (Map.Entry<String, String> a : actionList.entrySet()) {
            html += "\t\t\t\t\t<option value=\"" + a.getKey() + "\">" + a.getValue() + "</option>\n";
        }
        html += "\t\t\t</select>\n\t\t\t";
        return html;
    }
}
