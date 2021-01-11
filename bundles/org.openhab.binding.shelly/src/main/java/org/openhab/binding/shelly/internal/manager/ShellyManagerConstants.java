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

/**
 * {@link ShellyManagerConstants} defines the constants for Shelly Manager
 *
 * @author Markus Michels - Initial contribution
 */
public class ShellyManagerConstants {
    public static final String LOG_PREFIX = "ShellyManager";

    public static final String SHELLY_MANAGER_URI = "/shelly/manager";
    public static final String SHELLY_MGR_OVERVIEW_URI = SHELLY_MANAGER_URI + "/ovierview";
    public static final String SHELLY_MGR_FWUPDATE_URI = SHELLY_MANAGER_URI + "/fwupdate";
    public static final String SHELLY_MGR_ACTION_URI = SHELLY_MANAGER_URI + "/action";
    public static final String SHELLY_MGR_ACTION_RESTART = "restart";
    public static final String SHELLY_MGR_ACTION_RESET = "reset";
    public static final String SHELLY_MGR_ACTION_PROTECT = "protect";

    public static final String TEMPLATE_PATH = "templates/";
    public static final String HEADER_HTML = "header.html";
    public static final String OVERVIEW_HTML = "overview.html";
    public static final String DEVICE_HTML = "device.html";
    public static final String FWUPDATE_HTML = "fwupdate.html";
    public static final String ACTION_HTML = "action.html";
    public static final String FOOTER_HTML = "footer.html";
    public static final String ATTRIBUTE_FIRMWARE_SEL = "firmwareSelection";
    public static final String ATTRIBUTE_ACTION_LIST = "actionList";

    public static final String FWREPO_URL = "https://repo.shelly.cloud/files/firmware/";
    public static final String FWPROD = "prod";
    public static final String FWBETA = "beta";

    public static final String FWLISTARCH_URL = "http://archive.shelly-tools.de/archive.php";
    public static final String FWLISTARCH_FILE = "http://archive.shelly-tools.de/version/";
}
