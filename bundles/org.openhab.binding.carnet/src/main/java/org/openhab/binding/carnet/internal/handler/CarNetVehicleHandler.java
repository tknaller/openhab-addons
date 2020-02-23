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

import static org.openhab.binding.carnet.internal.CarNetBindingConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelGroupTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.CarNetTextResources;
import org.openhab.binding.carnet.internal.api.CarNetApi;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetDestinations;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetHistory;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehiclePosition;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleStatus;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleStatus.CNStoredVehicleDataResponse.CNVehicleData.CNStatusData;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleStatus.CNStoredVehicleDataResponse.CNVehicleData.CNStatusData.CNStatusField;
import org.openhab.binding.carnet.internal.api.CarNetIdMapper;
import org.openhab.binding.carnet.internal.api.CarNetIdMapper.CNIdMapEntry;
import org.openhab.binding.carnet.internal.config.CarNetVehicleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link CarNetVehicleHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class CarNetVehicleHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(CarNetVehicleHandler.class);
    private String thingName = "";
    private Map<String, Object> channelData = new HashMap<>();
    private @Nullable final CarNetApi api;
    private @Nullable final CarNetTextResources resources;
    private String vin = "";
    private static final CarNetIdMapper idMap = new CarNetIdMapper();

    private @Nullable CarNetVehicleConfiguration config;

    public CarNetVehicleHandler(Thing thing, @Nullable CarNetApi api, @Nullable CarNetTextResources resources) {
        super(thing);
        this.api = api;
        this.resources = resources;
    }

    @Override
    public void initialize() {
        logger.debug("Initializing!");
        updateStatus(ThingStatus.UNKNOWN);

        // Start asynchronous thing initialization
        scheduler.execute(() -> {
            if (initializeThing()) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

    }

    /**
     * (re-)initialize the thing
     *
     * @return true=successful
     */
    @SuppressWarnings("null")
    boolean initializeThing() {

        channelData = new HashMap<>(); // clear any cached channels
        config = getConfigAs(CarNetVehicleConfiguration.class);
        Validate.notNull(api, "API not yet initialized");
        boolean successful = true;
        String error = "";

        Map<String, String> properties = getThing().getProperties();
        vin = properties.get(PROPERTY_VIN) != null ? properties.get(PROPERTY_VIN).toUpperCase() : "";
        Validate.notNull(vin, "Unable to get VIN from thing properties!");
        Validate.notEmpty(vin, "Unable to get VIN from thing properties!");

        // Try to query status information from vehicle
        try {
            logger.debug("{}: Get Vehicle Status", vin);
            CarNetVehicleStatus status = api.getVehicleStatus(vin);
            for (CNStatusData data : status.storedVehicleDataResponse.vehicleData.data) {
                for (CNStatusField field : data.fields) {
                    CNIdMapEntry map = idMap.find(field.id);
                    if (map != null) {
                        logger.info("{}: {}={}{} (channel {})", vin, map.symbolicName, gs(field.value), gs(field.unit),
                                gs(map.channelName));
                        if (!map.channelName.isEmpty()) {
                            if (!map.channelName.startsWith(CHANNEL_GROUP_TIRES) || !field.value.contains("1")) {
                                createChannel(map.channelName, map.itemType);
                            }
                        }
                    } else {
                        logger.debug("{}: Unknown data field  {}.{}, value={} {}", vin, data.id, field.id, field.value,
                                field.unit);
                    }
                }
            }

            logger.debug("{}: Get Vehicle Position", vin);
            CarNetVehiclePosition position = api.getVehiclePosition(vin);

            CarNetDestinations destinations = api.getDestinations(vin);

            CarNetHistory history = api.getHistory(vin);
        } catch (CarNetException e) {
            if (e.getMessage().toLowerCase().contains("disabled ")) {
                // Status service in the vehicle is disabled
                logger.debug("{}: Status service is disabled, check Data Privacy settings in MMI", vin);
            } else {
                successful = false;
            }
        }

        if (successful) {
            // try {
            // } catch (CarNetException e) {
            // }
        }
        if (!successful) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, error);
        }
        return successful;
    }

    /**
     * This routine is called every time the Thing configuration has been changed (e.g. PaperUI)
     */
    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
        logger.debug("{}: Thing config updated.", thingName);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        try {
            if (command instanceof RefreshType) {
                return;
            }

            switch (channelUID.getIdWithoutGroup()) {
                default:
                    break;
            }
        } catch (NullPointerException e) {
        } finally {
        }
    }

    private void createChannel(String channelId, String itemType) throws CarNetException {
        String label = resources.getText("channel-type.carnet." + channelId + ".label");
        String description = resources.getText("channel-type.carnet." + channelId + ".description");
        String groupId = resources.getText("channel-type.carnet." + channelId + ".group");
        if (groupId.endsWith(".group")) {
            groupId = "";
        }
        if (groupId.isEmpty()) {
            groupId = CHANNEL_GROUP_STATUS;
        }
        ChannelGroupTypeUID groupTypeUID = new ChannelGroupTypeUID(BINDING_ID, groupId);
        ChannelTypeUID channelTypeUID = new ChannelTypeUID(BINDING_ID, channelId);

        if (label.isEmpty() || itemType.isEmpty()) {
            throw new CarNetException(resources.getText("exception.channeldef-not-found", channelId));
        }
        if (getThing().getChannel(channelId) == null) {
            // the channel does not exist yet, so let's add it
            logger.debug("Auto-creating channel '{}' ({})", channelId, getThing().getUID());
            ThingBuilder updatedThing = editThing();
            Channel channel = ChannelBuilder.create(new ChannelUID(getThing().getUID(), channelId), itemType)
                    .withType(channelTypeUID).withLabel(label).withDescription(description).build();
            updatedThing.withChannel(channel);
            updateThing(updatedThing.build());

            /*
             * ThingHandlerCallback callback = getCallback();
             * if (callback != null) {
             * for (ChannelBuilder channelBuilder : callback.createChannelBuilders(
             * new ChannelGroupUID(getThing().getUID(), groupId), groupTypeUID)) {
             * Channel newChannel = channelBuilder.build(),
             * existingChannel = getThing().getChannel(newChannel.getUID().getId());
             * if (existingChannel != null) {
             * logger.trace("Thing '{}' already has an existing channel '{}'. Omit adding new channel '{}'.",
             * getThing().getUID(), existingChannel.getUID(), newChannel.getUID());
             * continue;
             * }
             * }
             * }
             */
        }
    }

    private String gs(@Nullable String s) {
        return s != null ? s : "";
    }
}
