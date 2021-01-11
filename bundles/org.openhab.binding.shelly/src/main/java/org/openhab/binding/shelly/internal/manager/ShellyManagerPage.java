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

import static org.eclipse.smarthome.core.thing.Thing.*;
import static org.openhab.binding.shelly.internal.ShellyBindingConstants.*;
import static org.openhab.binding.shelly.internal.manager.ShellyManagerConstants.*;
import static org.openhab.binding.shelly.internal.util.ShellyUtils.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.shelly.internal.api.ShellyApiException;
import org.openhab.binding.shelly.internal.api.ShellyApiResult;
import org.openhab.binding.shelly.internal.api.ShellyHttpApi;
import org.openhab.binding.shelly.internal.config.ShellyBindingConfiguration;
import org.openhab.binding.shelly.internal.config.ShellyThingConfiguration;
import org.openhab.binding.shelly.internal.handler.ShellyBaseHandler;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * {@link ShellyManagerFwUpdatePage} implements the Shelly Manager's page template
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class ShellyManagerPage {
    private static final String HTTP_HEADER_AUTH = "Authorization";
    private static final String HTTP_AUTH_TYPE_BASIC = "Basic";

    private final Logger logger = LoggerFactory.getLogger(ShellyManagerPage.class);
    private final ConfigurationAdmin configurationAdmin;
    protected final Map<String, ShellyBaseHandler> thingHandlers;
    protected final HttpClient httpClient;

    protected final Map<String, String> htmlTemplates = new HashMap<>();
    protected Map<String, String> firmwareListHtml = new HashMap<>();
    protected final Gson gson = new Gson();

    public static class FwaList {
        public static class FwalEntry {
            // {"version":"v1.5.10","file":"SHSW-1.zip"}
            public @Nullable String version;
            public @Nullable String file;
        }

        public @Nullable ArrayList<FwalEntry> versions;
    }

    public static class FwRepoEntry {
        public @Nullable String url; // prod
        public @Nullable String version;

        public @Nullable String beta_url; // beta version if avilable
        public @Nullable String beta_ver;
    }

    public ShellyManagerPage(ConfigurationAdmin configurationAdmin, HttpClient httpClient,
            Map<String, ShellyBaseHandler> thingHandlers) {
        this.thingHandlers = thingHandlers;
        this.httpClient = httpClient;
        this.configurationAdmin = configurationAdmin;
    }

    public String generateContent(Map<String, String[]> parameters) throws ShellyApiException {
        return "";
    }

    protected String getUrlParm(Map<String, String[]> parameters, String param) {
        String[] p = parameters.get(param);
        String value = "";
        if (p != null) {
            value = getString(p[0]);
        }
        return value;
    }

    protected String loadHTML(String template, Map<String, String> properties) throws ShellyApiException {
        if (htmlTemplates.containsKey(template)) {
            return getString(htmlTemplates.get(template));
        }

        String file = TEMPLATE_PATH + template;
        logger.info("Read HTML from {}", file);
        ClassLoader cl = ShellyBaseHandler.class.getClassLoader();
        if (cl != null) {
            try (InputStream inputStream = cl.getResourceAsStream(file)) {
                if (inputStream != null) {
                    String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8.toString());
                    htmlTemplates.put(template, html);
                    html = fillAttributes(html, properties);
                    return html;
                }
            } catch (IOException e) {
            }
        }
        throw new ShellyApiException("Unable to read " + file + " from bundle resources!");
    }

    protected String loadHTML(String template) throws ShellyApiException {
        return loadHTML(template, new HashMap<>());
    }

    protected String fillPage(String template, Map<String, String> properties) {
        return fillAttributes(template, properties);
    }

    protected String fillPage(String template, String uid, ShellyBaseHandler th, Map<String, String> addProperties)
            throws ShellyApiException {
        Map<String, String> properties = new HashMap<>();
        properties.put("metaTag", ""); // default empty, might be overwritten
        properties.putAll(addProperties);
        return fillAttributes(template, fillProperties(properties, uid, th));
    }

    protected Map<String, String> fillProperties(Map<String, String> properties, String uid, ShellyBaseHandler th) {
        properties.putAll(th.getThing().getProperties());
        properties.putAll(th.getStatsProp());

        Thing thing = th.getThing();
        properties.put("thingName", getString(thing.getLabel()));
        properties.put("thingStatus", getString(thing.getStatus().toString()));
        properties.put("thingStatusDetail", getString(thing.getStatusInfo().getStatusDetail().toString()));
        properties.put("thingStatusDescr", getString(thing.getStatusInfo().getDescription()));
        ShellyThingConfiguration config = thing.getConfiguration().as(ShellyThingConfiguration.class);
        for (Map.Entry<String, Object> p : thing.getConfiguration().getProperties().entrySet()) {
            String key = p.getKey();
            String value = p.getValue().toString();
            properties.put(key, value);
        }

        String deviceName = getString(properties.get(PROPERTY_DEV_NAME));
        if (deviceName.isEmpty()) {
            // If the Shelly doesn't provide a device name (not configured) we use the service name
            properties.put(PROPERTY_DEV_NAME, getString(properties.get(PROPERTY_SERVICE_NAME)));
        }

        if (config.userId.isEmpty()) {
            // Get defauls from Binding Config
            try {
                ShellyBindingConfiguration bindingConfig = new ShellyBindingConfiguration();
                Configuration serviceConfig = configurationAdmin.getConfiguration("binding." + BINDING_ID);
                bindingConfig.updateFromProperties(serviceConfig.getProperties());
                properties.put("userId", bindingConfig.defaultUserId);
                properties.put("password", bindingConfig.defaultPassword);
            } catch (IOException e) {
            }
        }

        addAttribute(properties, th, CHANNEL_GROUP_DEV_STATUS, CHANNEL_DEVST_RSSI);
        addAttribute(properties, th, CHANNEL_GROUP_DEV_STATUS, CHANNEL_DEVST_UPTIME);
        addAttribute(properties, th, CHANNEL_GROUP_DEV_STATUS, CHANNEL_DEVST_HEARTBEAT);
        addAttribute(properties, th, CHANNEL_GROUP_DEV_STATUS, CHANNEL_DEVST_ITEMP);
        addAttribute(properties, th, CHANNEL_GROUP_DEV_STATUS, CHANNEL_DEVST_WAKEUP);
        addAttribute(properties, th, CHANNEL_GROUP_DEV_STATUS, CHANNEL_DEVST_CHARGER);
        addAttribute(properties, th, CHANNEL_GROUP_DEV_STATUS, CHANNEL_DEVST_UPDATE);

        return properties;
    }

    private void addAttribute(Map<String, String> properties, ShellyBaseHandler thingHandler, String group,
            String attribute) {
        State state = thingHandler.getChannelValue(CHANNEL_GROUP_DEV_STATUS, attribute);
        if (state != UnDefType.NULL) {
            String value = state.toString();
            properties.put(attribute, value);
        }
    }

    protected String fillAttributes(String template, Map<String, String> properties) {
        if ((properties.size() == 0) || !template.contains("${")) {
            // no replacement necessary
            return template;
        }

        // fill in the attributes
        String html = template;
        while (html.indexOf("${") >= 0) {
            String attribute = substringBetween(html, "${", "}");
            String before = substringBefore(html, "${");
            String after = substringAfter(html, "}");
            String value = "";
            if (properties.containsKey(attribute)) {
                value = getValue(properties, attribute);
            }
            html = before + value + after;
        }
        return html;
    }

    protected String getValue(Map<String, String> properties, String attribute) {
        String value = getString(properties.get(attribute));
        if (!value.isEmpty()) {
            switch (attribute) {
                case PROPERTY_FIRMWARE_VERSION:
                    value = substringBefore(value, "-");
                    break;
                case PROPERTY_UPDATE_AVAILABLE:
                    value = value.replace(OnOffType.ON.toString(), "yes");
                    value = value.replace(OnOffType.OFF.toString(), "no");
                    break;
            }
        }
        return value;
    }

    public FwaList getFirmwareList(String deviceType) throws ShellyApiException {
        FwaList list;
        String json = "";
        try {
            if (!deviceType.isEmpty()) {
                json = httpGet(FWLISTARCH_URL + "?type=" + deviceType);
            }
        } catch (ShellyApiException e) {
            logger.debug("{}: Unable to get firmware list for device type {}: {}", LOG_PREFIX, deviceType,
                    e.toString());
        }
        if (json.isEmpty() || json.startsWith("[]")) {
            // no files available for this device type
            logger.info("{}: No firmware files found for device type {}", LOG_PREFIX, deviceType);
            list = new FwaList();
            list.versions = new ArrayList<FwaList.FwalEntry>();
        } else {
            // Create selection list
            json = "{" + json.replace("[{", "\"versions\":[{") + "}"; // make it an named array
            list = fromJson(gson, json, FwaList.class);
        }
        return list;
    }

    protected String httpGet(String url) throws ShellyApiException {
        ShellyApiResult apiResult = new ShellyApiResult();

        try {
            Request request = httpClient.newRequest(url).method(HttpMethod.GET).timeout(SHELLY_API_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS);
            request.header(HttpHeader.ACCEPT, ShellyHttpApi.CONTENT_TYPE_JSON);
            logger.info("{}: HTTP GET {}", LOG_PREFIX, url);
            ContentResponse contentResponse = request.send();
            apiResult = new ShellyApiResult(contentResponse);
            String response = contentResponse.getContentAsString().replace("\t", "").replace("\r\n", "").trim();
            logger.info("{}: HTTP Response {}: {}", LOG_PREFIX, contentResponse.getStatus(), response);

            // validate response, API errors are reported as Json
            if (contentResponse.getStatus() != HttpStatus.OK_200) {
                throw new ShellyApiException(apiResult);
            }
            return response;
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            throw new ShellyApiException("HTTP GET failed", e);
        }
    }

    protected String getDeviceType(Map<String, String> properties) {
        return getString(properties.get(PROPERTY_MODEL_ID));
    }

    protected String getDeviceIp(Map<String, String> properties) {
        return getString(properties.get("deviceIp"));
    }

    protected String getDeviceName(Map<String, String> properties) {
        return getString(properties.get(PROPERTY_DEV_NAME));
    }

    protected ShellyThingConfiguration getThingConfig(ShellyBaseHandler th, Map<String, String> properties) {
        Thing thing = th.getThing();
        ShellyThingConfiguration config = thing.getConfiguration().as(ShellyThingConfiguration.class);
        if (config.userId.isEmpty()) {
            config.userId = getString(properties.get("userId"));
            config.password = getString(properties.get("password"));
        }
        return config;
    }
}
