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

import static org.openhab.binding.shelly.internal.ShellyBindingConstants.SHELLY_API_TIMEOUT_MS;
import static org.openhab.binding.shelly.internal.manager.ShellyManagerConstants.*;
import static org.openhab.binding.shelly.internal.util.ShellyUtils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
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

    public ShellyManagerFwUpdatePage(ConfigurationAdmin configurationAdmin, HttpClient httpClient, String localIp,
            int localPort, Map<String, ShellyBaseHandler> thingHandlers) {
        super(configurationAdmin, httpClient, localIp, localPort, thingHandlers);
    }

    @Override
    public ShellyMgrResponse generateContent(String path, Map<String, String[]> parameters) throws ShellyApiException {
        if (path.contains(SHELLY_MGR_OTA_URI)) {
            return loadFirmware(path, parameters);
        } else {
            return generatePage(path, parameters);
        }
    }

    public ShellyMgrResponse generatePage(String path, Map<String, String[]> parameters) throws ShellyApiException {
        String uid = getUrlParm(parameters, URLPARM_UID);
        String version = getUrlParm(parameters, URLPARM_VERSION);
        String update = getUrlParm(parameters, URLPARM_UPDATE);
        String source = getUrlParm(parameters, URLPARM_SOURCE);
        String url = getUrlParm(parameters, URLPARM_URL);
        if (uid.isEmpty() || version.isEmpty() || !thingHandlers.containsKey(uid)) {
            return new ShellyMgrResponse("Invalid URL parameters: " + parameters, HttpStatus.BAD_REQUEST_400);
        }

        Map<String, String> properties = new HashMap<>();
        String html = loadHTML(HEADER_HTML, properties);
        ShellyBaseHandler th = thingHandlers.get(uid);
        if (th != null) {
            properties = fillProperties(new HashMap<>(), uid, th);
            ShellyThingConfiguration config = getThingConfig(th, properties);
            ShellyDeviceProfile profile = th.getProfile();
            String deviceType = getDeviceType(properties);
            String uri = getFirmwareUrl(config.deviceIp, deviceType, version);
            if (source.equalsIgnoreCase(FWUPDATE_SOURCE_INTERNET)) {
                // If target
                // - contains "update=xx" then use -> ?update=true for release and ?update=false for beta
                // - otherwise qualify full url with ?url=xxxx
                url = "http://" + getDeviceIp(properties) + "/ota?"
                        + (uri.contains("update=") ? uri : URLPARM_URL + "=" + uri); // load directly from // Internet
            } else if (source.equalsIgnoreCase(FWUPDATE_SOURCE_LOCAL)) {
                // redirect to local server -> http://<oh-ip>:<oh-port>/shelly/manager/ota?deviceType=xxx&version=xxx
                url = "http://" + localIp + ":" + localPort + SHELLY_MGR_OTA_URI
                        + urlEncode("?" + URLPARM_DEVTYPE + "=" + deviceType + "&" + URLPARM_VERSION + "=" + version);
            } // else custom -> don't modify url
            properties.put("version", version);
            properties.put("firmwareUrl", uri);
            properties.put("updateUrl", url);
            properties.put("source", source);

            if (update.equalsIgnoreCase("yes")) {
                // do the update
                th.setThingOffline(ThingStatusDetail.FIRMWARE_UPDATING, "offline.status-error-fwupgrade");
                String output = "<p/>Updating device ${deviceName} (${uid}) with version ${version}, source=${source}"
                        + "<br/>" + "Update url for update: " + url + "<p/>"
                        + "Wait 1-2 minutes, then check device UI at <a href=\"http://${deviceIp}\" title=\"${thingName}\" target=\"_blank\">${deviceIp}</a>, section Firmware.<p/>";
                html += fillAttributes(output, properties);

                new Thread(() -> { // schedule asynchronous reboot
                    try {
                        ShellyHttpApi api = new ShellyHttpApi(uid, config, httpClient);
                        String updateUrl = uri;
                        if (source.equalsIgnoreCase(FWUPDATE_SOURCE_LOCAL)) {
                            updateUrl = "http://" + localIp + ":" + localPort + SHELLY_MGR_OTA_URI
                                    + urlEncode("?" + URLPARM_URL + "=" + updateUrl);
                        }
                        ShellySettingsUpdate result = api.firmwareUpdate(updateUrl);
                        String status = getString(result.status);
                        logger.info("{}: Firmware update initiated, device returned status {}", th.thingName, status);

                        // Shelly Motion needs almost 2min for upgrade
                        scheduleUpdate(th, uid + "_upgrade", profile.isMotion ? 110 : 30);
                    } catch (ShellyApiException e) {
                        // maybe the device restarts before returning the http response
                        logger.warn("{}: Firmware updated failed: {}", th.thingName, e.toString());
                    }
                }).start();
            } else {
                String message = "Do not power-off or restart device while updading the firmware!<p/>";
                properties.put("message", message);
                html += loadHTML(FWUPDATE_HTML, properties);
            }
        }

        html += loadHTML(FOOTER_HTML, properties);
        return new ShellyMgrResponse(html, HttpStatus.OK_200);
    }

    protected ShellyMgrResponse loadFirmware(String path, Map<String, String[]> parameters) throws ShellyApiException {
        String deviceType = getUrlParm(parameters, URLPARM_DEVTYPE);
        String version = getUrlParm(parameters, URLPARM_VERSION);
        String url = getUrlParm(parameters, URLPARM_URL);

        try {

            if (url.isEmpty()) {
                url = getFirmwareUrl("", deviceType, version);
                if (url.isEmpty()) {
                    throw new ShellyApiException(
                            "Unable to find firmware for device type " + deviceType + ", version " + version);
                }
                logger.debug("ShellyManager: Firmware URL for device type {}, version {} is {}", deviceType, version,
                        url);
            }

            logger.info("ShellyManager: Loading firmware from {}", url);
            // BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            // byte[] buf = new byte[in.available()];
            // in.read(buf);
            Request request = httpClient.newRequest(url).method(HttpMethod.GET).timeout(SHELLY_API_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS);
            ContentResponse contentResponse = request.send();
            HttpFields fields = contentResponse.getHeaders();
            Map<String, String> headers = new TreeMap<>();
            String etag = getString(headers.get("ETag"));
            headers.put("ETag", etag);
            headers.put("accept-ranges", getString(headers.get("accept-ranges")));
            byte[] data = contentResponse.getContent();
            logger.info("ShellyManager: Firmware successfully loaded, size={}, ETag={}", data.length);
            return new ShellyMgrResponse(data, HttpStatus.OK_200, contentResponse.getMediaType(), headers);
        } catch (ExecutionException | TimeoutException | InterruptedException | RuntimeException e) {
            logger.debug("{}: Unable to load firmware from {} (device type={}, version={})", url, deviceType, version,
                    e);
        }
        return new ShellyMgrResponse("Unable to load firmware from " + url, HttpStatus.NOT_FOUND_404);
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
                        String url = FWREPO_ARCFILE_URL + version + "/" + getString(e.file);
                        if (getString(e.version).equalsIgnoreCase(version)) {
                            return url;
                        }
                    }
                }
        }
        return "";
    }
}
