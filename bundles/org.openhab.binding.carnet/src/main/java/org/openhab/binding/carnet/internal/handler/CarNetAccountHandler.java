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
package org.openhab.binding.carnet.internal.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.carnet.internal.CarNetDeviceListener;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.CarNetTextResources;
import org.openhab.binding.carnet.internal.CarNetVehicleInformation;
import org.openhab.binding.carnet.internal.api.CarNetApi;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleDetails;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleList;
import org.openhab.binding.carnet.internal.api.CarNetHttpClient;
import org.openhab.binding.carnet.internal.api.CarNetTokenManager;
import org.openhab.binding.carnet.internal.config.CarNetAccountConfiguration;
import org.openhab.binding.carnet.internal.config.CarNetCombinedConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CarNetAccountHandler} implements access to the myAudi account API. It is implemented as a brdige device
 * and also dispatches events to the vehicle things.
 *
 * @author Markus Michels - Initial contribution
 * @author Lorenzo Bernardi - Additional contribution
 *
 */
@NonNullByDefault
public class CarNetAccountHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(CarNetAccountHandler.class);
    private final CarNetCombinedConfig config = new CarNetCombinedConfig();
    private final CarNetTokenManager tokenManager;
    private final CarNetApi api;
    private final CarNetHttpClient http;

    private List<CarNetVehicleInformation> vehicleList = new ArrayList<>();
    private List<CarNetDeviceListener> vehicleInformationListeners = Collections
            .synchronizedList(new ArrayList<CarNetDeviceListener>());
    private @Nullable ScheduledFuture<?> refreshJob;

    /**
     * keeps track of the {@link ChannelUID} for the 'apply_tamplate' {@link Channel}
     */
    // private final ChannelUID applyTemplateChannelUID;

    /**
     * Constructor
     *
     * @param bridge Bridge object representing a FRITZ!Box
     */
    public CarNetAccountHandler(Bridge bridge, @Nullable CarNetTextResources resources,
            CarNetTokenManager tokenManager) {
        super(bridge);
        this.tokenManager = tokenManager;

        // Each instance has it's own http client. Audi requires weaked SSL attributes, other may not
        HttpClient httpClient = new HttpClient();
        try {
            SslContextFactory ssl = new SslContextFactory();
            // ssl.setIncludeCipherSuites("^TLS_RSA_.*$");
            String[] excludedCiphersWithoutTlsRsaExclusion = Arrays.stream(ssl.getExcludeCipherSuites())
                    .filter(cipher -> !cipher.equals("^TLS_RSA_.*$")).toArray(String[]::new);
            ssl.setExcludeCipherSuites(excludedCiphersWithoutTlsRsaExclusion);
            httpClient = new HttpClient(ssl);
            httpClient.start();
            logger.debug("{}", httpClient.dump());
        } catch (Exception e) {
            logger.warn("Unable to start HttpClient!");
        }

        this.http = new CarNetHttpClient(httpClient);
        this.api = new CarNetApi(http, tokenManager);

        // Generate a unique Id for all tokens of the new Account thing, but also of all depending Vehicle things. This
        // allows sharing the tokens across all things associated with the account.
        config.tokenSetId = tokenManager.generateTokenSetId();
        tokenManager.setHttpClient(config.tokenSetId, http);
    }

    /**
     * Initializes the bridge.
     */
    @Override
    public void initialize() {
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            try {
                initializeThing();
            } catch (CarNetException e) {
                stateChanged(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.toString());
            }
        });
    }

    public boolean initializeThing() throws CarNetException {
        Map<String, String> properties = new TreeMap<String, String>();

        config.account = getConfigAs(CarNetAccountConfiguration.class);
        api.setConfig(config);
        api.initialize();
        refreshProperties(properties);

        CarNetVehicleList vehices = api.getVehicles();
        vehicleList = new ArrayList<CarNetVehicleInformation>();
        for (String vin : vehices.userVehicles.vehicle) {
            CarNetVehicleDetails details = api.getVehicleDetails(vin);
            CarNetVehicleInformation vehicle = new CarNetVehicleInformation(details);
            vehicleList.add(vehicle);
        }
        informVehicleInformationListeners(vehicleList);

        setupRefreshJob(5);
        stateChanged(ThingStatus.ONLINE, ThingStatusDetail.NONE, "");
        return true;
    }

    private void refreshToken() {
        logger.debug("Validating/refreshing tokens");
        try {
            api.refreshTokens();
        } catch (CarNetException e) {
            logger.debug("Unable to refresh tokens", e);
        }
    }

    public CarNetHttpClient getHttpClient() {
        // Account and Vehicle Handlers are sharing the same httpClient
        return http;
    }

    public CarNetCombinedConfig getCombinedConfig() {
        return config;
    }

    /**
     * Called by vehicle handler to register callback
     *
     * @param listener Listener interface provided by Vehicle Handler
     */
    public void registerListener(CarNetDeviceListener listener) {
        vehicleInformationListeners.add(listener);
    }

    /**
     * Called by vehicle handler to unregister callback
     *
     * @param listener Listener interface provided by Vehicle Handler
     */
    public void unregisterListener(CarNetDeviceListener listener) {
        vehicleInformationListeners.remove(listener);
    }

    /**
     * Forward discovery information to all listeners (Vehicle Handlers)
     *
     * @param vehicleInformationList
     */
    private void informVehicleInformationListeners(@Nullable List<CarNetVehicleInformation> vehicleInformationList) {
        this.vehicleInformationListeners.forEach(discovery -> discovery.informationUpdate(vehicleInformationList));
    }

    /**
     * Notify all listeners about status changes
     *
     * @param status New status
     * @param detail Status details
     * @param message Message
     */
    void stateChanged(ThingStatus status, ThingStatusDetail detail, String message) {
        updateStatus(status, detail, message);
        this.vehicleInformationListeners.forEach(discovery -> discovery.stateChanged(status, detail, message));
    }

    /**
     * Thing configuration was updated
     */
    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
    }

    /**
     * Empty handleCommand for Account Thing
     */
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String channelId = channelUID.getIdWithoutGroup();
        logger.debug("Handle command '{}' for channel {}", command, channelId);
        if (command == RefreshType.REFRESH) {
            return;
        }
    }

    /**
     * Sets up a polling job (using the scheduler) with the given interval.
     *
     * @param initialWaitTime The delay before the first refresh. Maybe 0 to immediately
     *            initiate a refresh.
     */
    private void setupRefreshJob(int initialWaitTime) {
        cancelRefreshJob();
        logger.trace("Setting up token refresh job, checking every 5 minutes");
        refreshJob = scheduler.scheduleWithFixedDelay(() -> refreshToken(), initialWaitTime,
                60/* API_TOKEN_REFRESH_INTERVAL_SEC */, TimeUnit.SECONDS);
    }

    /**
     * Cancels the polling job (if one was setup).
     */
    private void cancelRefreshJob() {
        if (refreshJob != null) {
            refreshJob.cancel(false);
        }
    }

    /**
     * Add one property to the Thing Properties
     *
     * @param key Name of the property
     * @param value Value of the property
     */
    public void updateProperties(String key, String value) {
        Map<String, String> property = new TreeMap<String, String>();
        property.put(key, value);
        updateProperties(property);
    }

    public void refreshProperties(Map<String, String> newProperties) {
        Map<String, String> thingProperties = editProperties();
        for (Map.Entry<String, String> prop : newProperties.entrySet()) {
            if (thingProperties.containsKey(prop.getKey())) {
                thingProperties.replace(prop.getKey(), prop.getValue());
            } else {
                thingProperties.put(prop.getKey(), prop.getValue());
            }
        }
        updateProperties(thingProperties);
        logger.trace("Properties updated");
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        super.childHandlerInitialized(childHandler, childThing);
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        super.childHandlerDisposed(childHandler, childThing);
    }

    /**
     * Disposes the bridge.
     */
    @Override
    public void dispose() {
        logger.debug("Handler disposed.");
        cancelRefreshJob();
    }
}
