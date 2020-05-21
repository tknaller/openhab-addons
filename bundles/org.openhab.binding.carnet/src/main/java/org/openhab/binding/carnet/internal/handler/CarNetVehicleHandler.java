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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PointType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.builder.ChannelBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelKind;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.carnet.internal.CarNetDeviceListener;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.CarNetTextResources;
import org.openhab.binding.carnet.internal.CarNetVehicleInformation;
import org.openhab.binding.carnet.internal.api.CarNetApi;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiError;
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
 * @author Lorenzo Bernardi - Additional contribution
 *
 */
@NonNullByDefault
public class CarNetVehicleHandler extends BaseThingHandler implements CarNetDeviceListener {
    private final Logger logger = LoggerFactory.getLogger(CarNetVehicleHandler.class);
    private static final CarNetIdMapper idMap = new CarNetIdMapper();
    private String thingName = "";

    private final Map<String, Object> channelData = new HashMap<>();
    private final @Nullable CarNetApi api;
    private final @Nullable CarNetTextResources resources;
    private String vin = "";
    private @Nullable CarNetAccountHandler accountHandler;
    private @Nullable ScheduledFuture<?> pollingJob;

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

        // Register listener and wait for account being ONLINE
        Bridge bridge = getBridge();
        CarNetAccountHandler handler = null;
        if (bridge != null) {
            handler = (CarNetAccountHandler) bridge.getHandler();
        }
        Validate.notNull(handler);
        accountHandler = handler;
        accountHandler.registerListener(this);
        initializeThing();
        setupPollingJob(5);
    }

    @Override
    public void stateChanged(ThingStatus status, ThingStatusDetail detail, String message) {
        if (status != ThingStatus.ONLINE) {
            initializeThing();
        }
        updateStatus(status, detail, message);
    }

    /**
     * (re-)initialize the thing
     *
     * @return true=successful
     */
    @SuppressWarnings("null")
    boolean initializeThing() {

        channelData.clear(); // clear any cached channels
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
            updateState(CHANNEL_GROUP_GENERAL + "#" + CHANNEL_GENERAL_VIN, new StringType(vin));

            Map<String, CNIdMapEntry> channels = new HashMap<String, CNIdMapEntry>();

            logger.debug("{}: Get Vehicle Status", vin);
            CarNetVehicleStatus status = api.getVehicleStatus(vin);
            for (CNStatusData data : status.storedVehicleDataResponse.vehicleData.data) {
                for (CNStatusField field : data.fields) {
                    CNIdMapEntry definition = idMap.find(field.id);
                    if (definition != null) {
                        logger.info("{}: {}={}{} (channel {}#{})", vin, definition.symbolicName, gs(field.value),
                                gs(field.unit), definition.groupName, definition.channelName);
                        if (!definition.channelName.isEmpty()) {
                            if (!definition.channelName.startsWith(CHANNEL_GROUP_TIRES) || !field.value.contains("1")) {
                                if (!channels.containsKey(definition.id)) {
                                    channels.put(definition.id, definition);
                                }
                            }
                        }
                    } else {
                        logger.debug("{}: Unknown data field  {}.{}, value={} {}", vin, data.id, field.id, field.value,
                                field.unit);
                    }
                }
            }

            Collection<CNIdMapEntry> channelsCol = channels.values();
            createChannels(new ArrayList<CNIdMapEntry>(channelsCol));

            // CarNetDestinations destinations = api.getDestinations(vin);

            // CarNetHistory history = api.getHistory(vin);
        } catch (CarNetException e) {
            CarNetApiError res = e.getApiResult().getApiError();
            if (res.description.contains("disabled ")) {
                // Status service in the vehicle is disabled
                String message = "Status service is disabled, check data privacy settings in MMI: " + res;
                logger.debug("{}: {}", vin, message);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING, message);
            } else {
                successful = false;
            }
        }

        if (successful) {
            updateStatus(ThingStatus.ONLINE);
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
        if (command instanceof RefreshType) {
            return;
        }

        String channelId = channelUID.getIdWithoutGroup();
        try {
            switch (channelId) {
                case CHANNEL_GENERAL_UPDATE:
                    updateVehicleStatus();
                    updateState(channelUID.getId(), OnOffType.OFF);
                    break;
                case CHANNEL_GENERAL_LOCK:
                    api.lockDoor(vin, (OnOffType) command == OnOffType.ON);
                    break;
                default:
                    break;
            }
        } catch (CarNetException e) {
            CarNetApiError res = e.getApiResult().getApiError();
            logger.info("{}: Unable to process command {} for channel {}: {}", vin, command, channelId, res);
            if (!res.details.reason.isEmpty()) {
                logger.debug("{}: {} (user={})", vin, res.details.reason, res.details.user);
            }
            logger.trace("{}: {}", vin, e.toString(), e);
        }
    }

    @Override
    public void dispose() {
        cancelPollingJob();

        super.dispose();
    }

    @SuppressWarnings("null")
    private void createChannel(CNIdMapEntry channelDef) {
        Validate.notNull(resources);
        String channelId = channelDef.channelName;
        String label = resources.getText("channel-type.carnet." + channelId + ".label");
        String description = resources.getText("channel-type.carnet." + channelId + ".description");
        String groupId = channelDef.groupName;

        if (groupId.isEmpty()) {
            groupId = CHANNEL_GROUP_STATUS;
        }

        // ChannelGroupTypeUID groupTypeUID = new ChannelGroupTypeUID(BINDING_ID, groupId);
        ChannelTypeUID channelTypeUID = new ChannelTypeUID(BINDING_ID, channelId);

        if (label.contains(".label") || label.isEmpty() || channelDef.itemType.isEmpty()) {
            // throw new CarNetException(resources.getText("exception.channeldef-not-found", channelId));
            label = channelDef.symbolicName;
        }
        if (getThing().getChannel(groupId + "#" + channelId) == null) {
            // the channel does not exist yet, so let's add it
            logger.debug("Auto-creating channel '{}' ({})", channelId, getThing().getUID());

            ThingBuilder updatedThing = editThing();
            Channel channel = ChannelBuilder
                    .create(new ChannelUID(getThing().getUID(), groupId + "#" + channelId),
                            channelDef.itemType.isEmpty() ? ITEMT_NUMBER : channelDef.itemType)
                    .withType(channelTypeUID).withLabel(label).withDescription(description).withKind(ChannelKind.STATE)
                    .build();
            updatedThing.withChannel(channel);
            updateThing(updatedThing.build());
        }
    }

    @SuppressWarnings("null")
    private void createChannels(List<CNIdMapEntry> channels) {
        Validate.notNull(resources);

        ThingBuilder updatedThing = editThing();

        for (CNIdMapEntry channelDef : channels) {
            String channelId = channelDef.channelName;
            String label = resources.getText("channel-type.carnet." + channelId + ".label");
            String description = resources.getText("channel-type.carnet." + channelId + ".description");
            String groupId = channelDef.groupName;

            if (groupId.isEmpty()) {
                groupId = CHANNEL_GROUP_STATUS;
            }

            // ChannelGroupTypeUID groupTypeUID = new ChannelGroupTypeUID(BINDING_ID, groupId);
            ChannelTypeUID channelTypeUID = new ChannelTypeUID(BINDING_ID, channelId);

            if (label.contains(".label") || label.isEmpty() || channelDef.itemType.isEmpty()) {
                // throw new CarNetException(resources.getText("exception.channeldef-not-found", channelId));
                label = channelDef.symbolicName;
            }
            logger.trace("Checking if channel {}#{} exists", groupId, channelId);
            if (getThing().getChannel(groupId + "#" + channelId) == null) {
                // the channel does not exist yet, so let's add it
                logger.debug("Auto-creating channel '{}' ({})", channelId, getThing().getUID());

                Channel channel = ChannelBuilder
                        .create(new ChannelUID(getThing().getUID(), groupId + "#" + channelId),
                                channelDef.itemType.isEmpty() ? ITEMT_NUMBER : channelDef.itemType)
                        .withType(channelTypeUID).withLabel(label).withDescription(description)
                        .withKind(ChannelKind.STATE).build();
                updatedThing.withChannel(channel);
            }
        }

        updateThing(updatedThing.build());

    }

    private void updateVehicleStatus() {

        config = getConfigAs(CarNetVehicleConfiguration.class);
        Validate.notNull(api, "API not yet initialized");
        boolean successful = true;
        String error = "";

        Map<String, String> properties = getThing().getProperties();
        vin = gs(properties.get(PROPERTY_VIN)).toUpperCase();
        Validate.notEmpty(vin, "Unable to get VIN from thing properties!");

        // Try to query status information from vehicle
        try {
            logger.debug("{}: Get Vehicle Status", vin);
            CarNetVehicleStatus status = api.getVehicleStatus(vin);
            for (CNStatusData data : status.storedVehicleDataResponse.vehicleData.data) {
                for (CNStatusField field : data.fields) {
                    CNIdMapEntry map = idMap.find(field.id);
                    if (map != null) {
                        logger.info("Updating {}: {}={}{} (channel {}#{})", vin, map.symbolicName, gs(field.value),
                                gs(field.unit), gs(map.groupName), gs(map.channelName));
                        if (!map.channelName.isEmpty()) {
                            Channel channel = getThing().getChannel(map.groupName + "#" + map.channelName);
                            if (channel != null) {
                                logger.debug("Trying to update channel {} with value {}", channel.getUID(),
                                        gs(field.value));
                                switch (map.itemType) {
                                    case ITEMT_NUMBER:
                                        String val = "0";
                                        if (!gs(field.value).isEmpty()) {
                                            val = gs(field.value);
                                        }
                                        updateState(channel.getUID(), new DecimalType(val));
                                        break;
                                    case ITEMT_SWITCH:
                                        updateState(channel.getUID(), OnOffType.from(gs(field.value)));
                                        break;
                                    case ITEMT_STRING:
                                        updateState(channel.getUID(), new StringType(gs(field.value)));
                                        // break;
                                    default:
                                        logger.warn("Unknown type {}", map.itemType);
                                }
                            } else {
                                logger.debug("Channel {}#{} not found", map.groupName, map.channelName);
                            }
                        }
                    } else {
                        logger.debug("{}: Unknown data field  {}.{}, value={} {}", vin, data.id, field.id, field.value,
                                field.unit);
                    }
                }
            }

            // CarNetDestinations destinations = api.getDestinations(vin);

            // CarNetHistory history = api.getHistory(vin); accountHandler.registerListener(this);

        } catch (CarNetException e) {
            if (e.getMessage().toLowerCase().contains("disabled ")) {
                // Status service in the vehicle is disabled
                logger.debug("{}: Status service is disabled, check Data Privacy settings in MMI", vin);
            } else {
                successful = false;
            }
        }

        if (successful) {
            updateStatus(ThingStatus.ONLINE);
            // try {
            // } catch (CarNetException e) {
            // }
        }
        if (!successful) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, error);
        }
    }

    private void updateVehicleLocation() {
        logger.debug("{}: Get Vehicle Position", vin);
        CarNetVehiclePosition position;
        try {
            position = api.getVehiclePosition(vin);
            /*
             * DecimalType latitude = new DecimalType(
             * new Float(position.findCarResponse.carPosition.carCoordinate.latitude) / 1000000);
             * DecimalType longitude = new DecimalType(
             * new Float(position.findCarResponse.carPosition.carCoordinate.longitude) / 1000000);
             */

            PointType location = new PointType(new DecimalType(position.getLattitude()),
                    new DecimalType(position.getLongitude()));
            updateState(CHANNEL_GROUP_LOCATION + "#" + CHANNEL_LOCATTION_GEO, location);
            updateState(CHANNEL_GROUP_LOCATION + "#" + CHANNEL_LOCATTION_TIME,
                    new DateTimeType(position.getCarSentTime()));
            updateState(CHANNEL_GROUP_LOCATION + "#" + CHANNEL_LOCATTION_PARK,
                    new DateTimeType(position.getParkingTime()));
        } catch (CarNetException e) {
            logger.debug("Unable to update vehicle location", e);
        }
    }

    /**
     * Sets up a polling job (using the scheduler) with the given interval.
     *
     * @param initialWaitTime The delay before the first refresh. Maybe 0 to immediately
     *            initiate a refresh.
     */
    private void setupPollingJob(int initialWaitTime) {
        cancelPollingJob();
        logger.trace("Setting up polling job with fixed delay {} minutes, starting in {} seconds", 10, initialWaitTime);
        pollingJob = scheduler.scheduleWithFixedDelay(() -> {
            updateVehicleStatus();
            updateVehicleLocation();
        }, initialWaitTime, 10 * 60, TimeUnit.SECONDS);
    }

    /**
     * Cancels the polling job (if one was setup).
     */
    private void cancelPollingJob() {
        if (pollingJob != null) {
            pollingJob.cancel(false);
        }
    }

    private String gs(@Nullable String s) {
        return s != null ? s : "";
    }

    @Override
    public void informationUpdate(@Nullable List<CarNetVehicleInformation> vehicleList) {
    }
}
