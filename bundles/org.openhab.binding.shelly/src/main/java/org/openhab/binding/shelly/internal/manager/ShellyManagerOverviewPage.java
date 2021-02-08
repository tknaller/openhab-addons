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

import static org.openhab.binding.shelly.internal.ShellyBindingConstants.*;
import static org.openhab.binding.shelly.internal.manager.ShellyManagerConstants.*;
import static org.openhab.binding.shelly.internal.util.ShellyUtils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.openhab.binding.shelly.internal.api.ShellyApiException;
import org.openhab.binding.shelly.internal.handler.ShellyBaseHandler;
import org.openhab.binding.shelly.internal.manager.ShellyManagerPage.FwaList.FwalEntry;
import org.openhab.binding.shelly.internal.util.ShellyVersionDTO;
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
        logger.debug("Generating overview for {} devices", thingHandlers.size());
        firmwareListHtml = new HashMap<>(); // re-load list each time we generate an overview

        String html = "";
        Map<String, String> properties = new HashMap<>();
        properties.put("metaTag", "<meta http-equiv=\"refresh\" content=\"60\" />");
        properties.put("cssHeader", loadHTML(OVERVIEW_HEADER, properties));
        html = loadHTML(HEADER_HTML, properties);
        html += loadHTML(OVERVIEW_HTML, properties);

        String deviceHtml = "";
        TreeMap<String, ShellyBaseHandler> sortedMap = new TreeMap<>();
        for (Map.Entry<String, ShellyBaseHandler> handler : thingHandlers.entrySet()) { // sort by Device Name
            String deviceName = getDisplayName(handler.getValue().getThing().getProperties());
            sortedMap.put(deviceName, handler.getValue());
        }
        for (Map.Entry<String, ShellyBaseHandler> handler : sortedMap.entrySet()) {
            try {
                ShellyBaseHandler th = handler.getValue();
                String uid = getString(th.getThing().getUID().getAsString()); // handler.getKey();
                properties.clear();
                fillProperties(properties, uid, handler.getValue());
                properties.put("displayName", handler.getKey());
                String deviceType = getDeviceType(properties);
                if (!deviceType.equalsIgnoreCase("unknown")) { // pw-protected device
                    properties.put(ATTRIBUTE_FIRMWARE_SEL, fillFirmwareList(uid, deviceType));
                    properties.put(ATTRIBUTE_ACTION_LIST, fillActionList(th, uid));
                } else {
                    properties.put(ATTRIBUTE_FIRMWARE_SEL, "");
                    properties.put(ATTRIBUTE_ACTION_LIST, "");
                }
                html += loadHTML(OVERVIEW_DEVICE, properties);
            } catch (ShellyApiException e) {
                logger.debug("{}: Exception", LOG_PREFIX, e);
            }
        }

        properties.clear();
        properties.put("cssFooter", loadHTML(OVERVIEW_FOOTER, properties));
        html += deviceHtml + loadHTML(FOOTER_HTML, properties);
        return fillAttributes(html, properties);
    }

    private String fillFirmwareList(String uid, String deviceType) throws ShellyApiException {
        String key = uid + "_" + deviceType;
        if (firmwareListHtml.containsKey(key)) {
            return getString(firmwareListHtml.get(key));
        }

        String html = "\n\t\t\t\t<select name=\"fwList\" id=\"fwList\" onchange=\"location = this.options[this.selectedIndex].value;\">\n";
        html += "\t\t\t\t\t<option value=\"\" selected disabled hidden>update to</option>\n";

        String pVersion = "";
        String bVersion = "";
        String updateUrl = SHELLY_MGR_FWUPDATE_URI + "?uid=" + urlEncode(uid);
        try {
            // Get current prod + beta version from original firmware repo
            logger.debug("{}: Load firmware version list for device type {}", LOG_PREFIX, deviceType);
            String json = httpGet(FWREPO_PROD_URL); // return a strange JSON format so we are parsing this manually
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
            json = httpGet(FWREPO_ARCH_URL + "?type=" + deviceType);
            if (json.startsWith("[]")) {
                // no files available for this device type
                logger.debug("{}: No firmware files found for device type {}", LOG_PREFIX, deviceType);
            } else {
                // Create selection list
                json = "{" + json.replace("[{", "\"versions\":[{") + "}"; // make it an named array
                FwaList list = getFirmwareArchiveList(deviceType);
                ArrayList<FwalEntry> versions = list.versions;
                if (versions != null) {
                    html += "\t\t\t\t\t<option value=\"\" disabled>-- Archive:</option>\n";
                    for (int i = versions.size() - 1; i >= 0; i--) {
                        FwalEntry e = versions.get(i);
                        String version = getString(e.version);
                        ShellyVersionDTO v = new ShellyVersionDTO();
                        if (!version.equalsIgnoreCase(pVersion) && !version.equalsIgnoreCase(bVersion)
                                && (v.compare(version, SHELLY_API_MIN_FWCOIOT) >= 0) || version.contains("master")) {
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

        firmwareListHtml.put(key, html);
        return html;
    }

    private String fillActionList(ShellyBaseHandler handler, String uid) {
        ThingStatus status = handler.getThing().getStatus();
        if (status != ThingStatus.ONLINE) {
            return ""; // device not initialized, offline etc.
        }
        Map<String, String> actionList = ShellyManagerActionPage.getActions();
        String html = "\n\t\t\t\t<select name=\"actionList\" id=\"actionList\" onchange=\"location = '"
                + SHELLY_MGR_ACTION_URI + "?uid=" + urlEncode(uid)
                + "&action='+this.options[this.selectedIndex].value;\">\n";
        html += "\t\t\t\t\t<option value=\"\" selected disabled>select</option>\n";
        for (Map.Entry<String, String> a : actionList.entrySet()) {
            html += "\t\t\t\t\t<option value=\"" + a.getKey() + "\">" + a.getValue() + "</option>\n";
        }
        html += "\t\t\t\t</select>\n\t\t\t";
        return html;
    }

    private String getDisplayName(Map<String, String> properties) {
        String name = getString(properties.get(PROPERTY_DEV_NAME));
        if (name.isEmpty()) {
            name = getString(properties.get(PROPERTY_SERVICE_NAME));
        }
        return name;
    }
}
