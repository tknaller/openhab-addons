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
package org.openhab.binding.carnet.internal.api;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link CarNetTokenManager} implements token creation and refreshing.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
@Component(service = CarNetTokenManager.class)
public class CarNetTokenManager {
    private final Logger logger = LoggerFactory.getLogger(CarNetTokenManager.class);

    private CarNetCombinedConfig config = new CarNetCombinedConfig();
    private String clientId = "";
    private CarNetToken idToken = new CarNetToken();
    private CarNetToken brandToken = new CarNetToken();
    private CarNetToken vwToken = new CarNetToken();

    @Activate
    public CarNetTokenManager() {
    }
}
