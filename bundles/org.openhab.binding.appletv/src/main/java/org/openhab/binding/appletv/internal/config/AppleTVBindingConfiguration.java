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
package org.openhab.binding.appletv.internal.config;

import static org.openhab.binding.appletv.internal.AppleTVBindingConstants.UPDATE_STATUS_INTERVAL;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * Contains the binding configuration and default values. The field names represent the configuration names,
 * do not rename them if you don't intend to break the configuration interface.
 *
 * @author Markus Michels - initial contribution
 */
@NonNullByDefault
public class AppleTVBindingConfiguration {
    public Integer updateInterval = UPDATE_STATUS_INTERVAL;
    public String libPath = "";
    public boolean autoDiscovery;

    public void update(AppleTVBindingConfiguration newConfiguration) {
        this.libPath = newConfiguration.libPath;
        this.updateInterval = newConfiguration.updateInterval;
        this.autoDiscovery = newConfiguration.autoDiscovery;
    }
}
