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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link AppleTVThingConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class AppleTVThingConfiguration {

    public String ipAddress = "";
    public String loginId = "";

    public boolean doPairing = true;
    public String remoteName = "";
    public String pairingPIN = "";

    public String authenticationPIN = "";

    public String keyMovie = "";
    public String keyTVShow = "";
    public String keyMusic = "";
}
