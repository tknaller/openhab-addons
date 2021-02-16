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

import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * {@link ShellyManagerConstants} defines the constants for Shelly Manager
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class ShellyManagerConstants {
    public static final String LOG_PREFIX = "ShellyManager";
    public static final String UTF_8 = StandardCharsets.UTF_8.toString();

    public static final String SHELLY_MANAGER_URI = "/shelly/manager";
    public static final String SHELLY_MGR_OVERVIEW_URI = SHELLY_MANAGER_URI + "/ovierview";
    public static final String SHELLY_MGR_FWUPDATE_URI = SHELLY_MANAGER_URI + "/fwupdate";
    public static final String SHELLY_MGR_IMAGES_URI = SHELLY_MANAGER_URI + "/images";
    public static final String SHELLY_MGR_ACTION_URI = SHELLY_MANAGER_URI + "/action";
    public static final String SHELLY_MGR_OTA_URI = SHELLY_MANAGER_URI + "/ota";

    public static final String ACTION_RESTART = "restart";
    public static final String ACTION_PROTECT = "protect";
    public static final String ACTION_SETTZ = "settz";
    public static final String ACTION_SETNTP = "setntp";
    public static final String ACTION_SETCLOUD = "setcloud";
    public static final String ACTION_RES_STATS = "reset_stat";
    public static final String ACTION_RESET = "reset";
    public static final String ACTION_NONE = "-";

    public static final String IMAGE_PATH = "images/";
    public static final String TEMPLATE_PATH = "templates/";
    public static final String HEADER_HTML = "header.html";
    public static final String OVERVIEW_HTML = "overview.html";
    public static final String OVERVIEW_HEADER = "ov_header.html";
    public static final String OVERVIEW_DEVICE = "ov_device.html";
    public static final String OVERVIEW_FOOTER = "ov_footer.html";
    public static final String FWUPDATE_HTML = "fwupdate.html";
    public static final String ACTION_HTML = "action.html";
    public static final String FOOTER_HTML = "footer.html";

    public static final String ATTRIBUTE_METATAG = "metaTag";
    public static final String ATTRIBUTE_CSS_HEADER = "cssHeader";
    public static final String ATTRIBUTE_CSS_FOOTER = "cssFooter";
    public static final String ATTRIBUTE_URI = "uri";
    public static final String ATTRIBUTE_MESSAGE = "message";
    public static final String ATTRIBUTE_STATUS_ICON = "iconStatus";
    public static final String ATTRIBUTE_DISPLAY_NAME = "displayName";
    public static final String ATTRIBUTE_DEV_STATUS = "deviceStatus";
    public static final String ATTRIBUTE_FIRMWARE_SEL = "firmwareSelection";
    public static final String ATTRIBUTE_ACTION_LIST = "actionList";
    public static final String ATTRIBUTE_LAST_ALARM = "lastAlarmTs";

    public static final String URLPARM_UID = "uid";
    public static final String URLPARM_DEVTYPE = "deviceType";
    public static final String URLPARM_ACTION = "action";
    public static final String URLPARM_FILTER = "filter";
    public static final String URLPARM_TYPE = "type";
    public static final String URLPARM_VERSION = "version";
    public static final String URLPARM_UPDATE = "update";
    public static final String URLPARM_SOURCE = "source";
    public static final String URLPARM_URL = "url";

    public static final String ACTION_REFRESH = "refresh";

    public static final String FILTER_ONLINE = "online";
    public static final String FILTER_INACTIVE = "inactive";
    public static final String FILTER_ATTENTION = "attention";
    public static final String FILTER_UPDATE = "update";
    public static final String FILTER_UNPROTECTED = "unprotected";

    public static final String ICON_ONLINE = "online";
    public static final String ICON_OFFLINE = "offline";
    public static final String ICON_UNINITIALIZED = "uninitialized";
    public static final String ICON_CONFIG = "config";
    public static final String ICON_ATTENTION = "attention";

    public static final String FWUPDATE_SOURCE_LOCAL = "local";
    public static final String FWUPDATE_SOURCE_INTERNET = "internet";
    public static final String FWUPDATE_SOURCE_CUSTOM = "custom";

    public static final String FWPROD = "prod";
    public static final String FWBETA = "beta";

    public static final String FWREPO_PROD_URL = "https://api.shelly.cloud/files/firmware/";
    public static final String FWREPO_TEST_URL = "https://repo.shelly.cloud/files/firmware/";
    public static final String FWREPO_ARCH_URL = "http://archive.shelly-tools.de/archive.php";
    public static final String FWREPO_ARCFILE_URL = "http://archive.shelly-tools.de/version/";

    public static final int CACHE_TIMEOUT_DEF_MIN = 60; // Default timeout for cache entries
    public static final int CACHE_TIMEOUT_FW_MIN = 15; // Cache entries for the firmware list 15min
}
