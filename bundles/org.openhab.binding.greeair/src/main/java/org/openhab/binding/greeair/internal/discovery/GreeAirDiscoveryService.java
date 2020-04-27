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

package org.openhab.binding.greeair.internal.discovery;

import static org.openhab.binding.greeair.internal.GreeAirBindingConstants.*;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.net.NetworkAddressService;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Device discovery creates a thing in the inbox for each vehicle
 * found in the data received from {@link CarNetAccountHandler}.
 *
 * @author Markus Michels - Initial Contribution
 *
 */
@NonNullByDefault
public class GreeAirDiscoveryService extends AbstractDiscoveryService {
    private static final int TIMEOUT = 10;
    private final Logger logger = LoggerFactory.getLogger(GreeAirDiscoveryService.class);
    private final String broadcastAddress;

    private GreeDeviceFinder deviceFinder = new GreeDeviceFinder();

    public GreeAirDiscoveryService(@Reference NetworkAddressService networkAddressService, Bundle bundle
    // ,@Nullable TranslationProvider i18nProvider, @Nullable LocaleProvider localeProvider
    ) {
        super(SUPPORTED_THING_TYPES_UIDS, TIMEOUT);
        String broadcastAddress = networkAddressService.getConfiguredBroadcastAddress();
        this.broadcastAddress = broadcastAddress != null ? broadcastAddress : "192.168.255.255";
    }

    public void activate() {
    }

    @Override
    protected void startBackgroundDiscovery() {
        logger.trace("Starting background scan");
        startScan();
    }

    @Override
    protected void stopBackgroundDiscovery() {
        logger.trace("Stopping background scan");
        stopScan();
    }

    @Override
    protected void startScan() {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            deviceFinder = new GreeDeviceFinder(broadcastAddress);
            deviceFinder.Scan(clientSocket);
            clientSocket.close();

            logger.debug("GreeAircon found {} Gree Devices during scanning", deviceFinder.getScannedDeviceCount());
            createResult(deviceFinder.getDevices());
        } catch (Exception e) {
            logger.debug("Discovery failed", e);
        }
    }

    public void createResult(HashMap<String, GreeAirDevice> deviceList) {
        for (Map.Entry<String, GreeAirDevice> d : deviceList.entrySet()) {
            GreeAirDevice device = d.getValue();
            logger.debug("Discovery for [{}]", device.getName());
            Map<String, Object> properties = new TreeMap<String, Object>();
            properties.put(Thing.PROPERTY_VENDOR, device.getVendor());
            properties.put(Thing.PROPERTY_MODEL_ID, device.getModel());
            properties.put(Thing.PROPERTY_MAC_ADDRESS, device.getId());
            properties.put(PROPERTY_IP, device.getAddress().toString());
            properties.put(PROPERTY_BROADCAST, broadcastAddress);
            ThingUID thingUID = new ThingUID(THING_TYPE_GREEAIRCON, device.getId());
            DiscoveryResult result = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                    .withRepresentationProperty(device.getId()).withLabel(device.getName()).build();
            thingDiscovered(result);
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        removeOlderResults(getTimestampOfLastScan());
    }
}
