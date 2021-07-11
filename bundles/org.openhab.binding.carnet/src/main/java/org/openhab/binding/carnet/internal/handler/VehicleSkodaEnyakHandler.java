/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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

import java.time.ZoneId;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.carnet.internal.TextResources;
import org.openhab.binding.carnet.internal.api.ApiException;
import org.openhab.binding.carnet.internal.api.skodaenyak.SEServiceStatus;
import org.openhab.binding.carnet.internal.provider.CarChannelTypeProvider;
import org.openhab.binding.carnet.internal.provider.ChannelDefinitions;
import org.openhab.binding.carnet.internal.provider.ChannelDefinitions.ChannelIdMapEntry;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VehicleSkodaEnyakHandler} implements the Vehicle Handler for Skoda Enyak
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class VehicleSkodaEnyakHandler extends VehicleBaseHandler {
    private final Logger logger = LoggerFactory.getLogger(VehicleSkodaEnyakHandler.class);

    public VehicleSkodaEnyakHandler(Thing thing, TextResources resources, ZoneId zoneId, ChannelDefinitions idMapper,
            CarChannelTypeProvider channelTypeProvider) throws ApiException {
        super(thing, resources, zoneId, idMapper, channelTypeProvider);
    }

    @Override
    public boolean createBrandChannels(Map<String, ChannelIdMapEntry> channels) {
        return false;
    }

    /**
     * Register all available services
     */
    @Override
    public void registerServices() {
        services.clear();
        addService(new SEServiceStatus(this, api));
    }

    @Override
    public boolean handleBrandCommand(ChannelUID channelUID, Command command) throws ApiException {
        String channelId = channelUID.getIdWithoutGroup();
        boolean processed = true;
        String action = "";
        String actionStatus = "";
        boolean switchOn = (command instanceof OnOffType) && (OnOffType) command == OnOffType.ON;
        logger.debug("{}: Channel {} received command {}", thingId, channelId, command);
        try {
            switch (channelId) {
                default:
                    processed = false;
            }
        } catch (RuntimeException /* ApiException */ e) {
            if (command instanceof OnOffType) {
                updateState(channelUID.getId(), OnOffType.OFF);
            }
            throw e;
        }

        if (processed && !action.isEmpty()) {
            logger.debug("{}: Action {} submitted, initial status={}", thingId, action, actionStatus);
        }
        return processed;
    }
}
