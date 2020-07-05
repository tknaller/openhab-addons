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
package org.openhab.binding.carnet.internal;

import static org.openhab.binding.carnet.internal.CarNetBindingConstants.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.eclipse.smarthome.io.net.http.HttpClientFactory;
import org.openhab.binding.carnet.internal.api.CarNetApi;
import org.openhab.binding.carnet.internal.discovery.CarNetDiscoveryService;
import org.openhab.binding.carnet.internal.handler.CarNetAccountHandler;
import org.openhab.binding.carnet.internal.handler.CarNetIChanneldMapper;
import org.openhab.binding.carnet.internal.handler.CarNetVehicleHandler;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CarNetHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.carnet", service = ThingHandlerFactory.class)
public class CarNetHandlerFactory extends BaseThingHandlerFactory {
    private final Logger logger = LoggerFactory.getLogger(CarNetHandlerFactory.class);
    private final CarNetTextResources resources;
    private final CarNetIChanneldMapper channelIdMapper;
    private final Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegistrations = new HashMap<>();

    /**
     * shared instance of HTTP client for asynchronous calls
     */
    private @Nullable HttpClient httpClient;
    private CarNetApi api = new CarNetApi();

    @Activate
    public CarNetHandlerFactory(@Reference CarNetTextResources resources, @Reference CarNetIChanneldMapper channelIdMapper,
            @Reference HttpClientFactory httpClientFactory) {
        this.resources = resources;
        this.channelIdMapper = channelIdMapper;
        try {
            // this.httpClient = httpClientFactory.getCommonHttpClient();
            SslContextFactory ssl = new SslContextFactory();
            // ssl.setIncludeCipherSuites("^TLS_RSA_.*$");
            String[] excludedCiphersWithoutTlsRsaExclusion = Arrays.stream(ssl.getExcludeCipherSuites())
                    .filter(cipher -> !cipher.equals("^TLS_RSA_.*$")).toArray(String[]::new);
            ssl.setExcludeCipherSuites(excludedCiphersWithoutTlsRsaExclusion);
            this.httpClient = new HttpClient(ssl);
            logger.debug("{}", httpClient.dump());
            if (this.httpClient != null) {
                this.httpClient.start();
            }
        } catch (Exception e) {
            logger.warn("Unable to start HttpClient!");
        }
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_ACCOUNT.equals(thingTypeUID)) {
            api = new CarNetApi(httpClient);
            CarNetAccountHandler handler = new CarNetAccountHandler((Bridge) thing, resources, api);
            registerDeviceDiscoveryService(handler);
            return handler;
        } else if (THING_TYPE_VEHICLE.equals(thingTypeUID)) {
            return new CarNetVehicleHandler(thing, api, resources, channelIdMapper);
        }

        return null;
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof CarNetAccountHandler) {
            unregisterDeviceDiscoveryService((CarNetAccountHandler) thingHandler);
        }
    }

    private synchronized void registerDeviceDiscoveryService(CarNetAccountHandler bridgeHandler) {
        CarNetDiscoveryService discoveryService = new CarNetDiscoveryService(bridgeHandler, bundleContext.getBundle());
        discoveryService.activate();
        this.discoveryServiceRegistrations.put(bridgeHandler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
    }

    private synchronized void unregisterDeviceDiscoveryService(CarNetAccountHandler bridgeHandler) {
        ServiceRegistration<?> serviceRegistration = this.discoveryServiceRegistrations
                .get(bridgeHandler.getThing().getUID());
        if (serviceRegistration != null) {
            CarNetDiscoveryService discoveryService = (CarNetDiscoveryService) bundleContext
                    .getService(serviceRegistration.getReference());
            if (discoveryService != null) {
                discoveryService.deactivate();
            }
            serviceRegistration.unregister();
            discoveryServiceRegistrations.remove(bridgeHandler.getThing().getUID());
        }
    }
}