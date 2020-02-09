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
package org.openhab.binding.appletv.internal.jpy;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link LibATVCallback} defines the callback interface every class needs to implement receiving those callbacks.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public interface LibATVCallback {
    /**
     * This function will be called from the PyATV module to display an info message
     *
     * @param message
     */
    public void info(String message);

    /**
     * This function will be called from the PyATV module to display a debug message
     *
     * @param message
     */
    public void debug(String message);

    /**
     * This function will be called from the PyATV module when device discovery is completed
     *
     * @param json Discovered devices in JSON format
     */
    public void devicesDiscovered(String json);

    /**
     * This function will be called from the PyATV to pass the unique device id
     *
     * @param id unique device id
     */
    public void generatedDeviceId(String id);

    /**
     * PyATV posts an status update
     *
     * @param prop key
     * @param input value
     */
    public void statusEvent(String prop, String input);

    /**
     * PyATV callback to report pairing result
     *
     * @param result true: pairing was successful, false: pairing failed
     * @param message an additional information
     */
    public void pairingResult(boolean result, String message);
}
