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
package org.openhab.binding.connectedcar.internal.handler;

import static org.openhab.binding.connectedcar.internal.BindingConstants.*;

import java.time.ZoneId;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.connectedcar.internal.TextResources;
import org.openhab.binding.connectedcar.internal.api.ApiException;
import org.openhab.binding.connectedcar.internal.api.weconnect.WCServiceStatus;
import org.openhab.binding.connectedcar.internal.provider.CarChannelTypeProvider;
import org.openhab.binding.connectedcar.internal.provider.ChannelDefinitions;
import org.openhab.binding.connectedcar.internal.provider.ChannelDefinitions.ChannelIdMapEntry;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VehicleWeConnectHandler} implements the Vehicle Handler for WeConnect
 *
 * @author Markus Michels - Initial contribution
 *
 */
@NonNullByDefault
public class VehicleWeConnectHandler extends VehicleBaseHandler {
    private final Logger logger = LoggerFactory.getLogger(VehicleWeConnectHandler.class);

    public VehicleWeConnectHandler(Thing thing, TextResources resources, ZoneId zoneId, ChannelDefinitions idMapper,
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
        addService(new WCServiceStatus(this, api));
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
                case CHANNEL_CONTROL_CLIMATER:
                    action = switchOn ? "startClimater" : "stopClimater";
                    actionStatus = api.controlClimater(switchOn, "");
                    break;
                case CHANNEL_CONTROL_CHARGER:
                    action = switchOn ? "startCharging" : "stopCharging";
                    actionStatus = api.controlCharger(switchOn);
                    break;
                case CHANNEL_CLIMATER_TARGET_TEMP:
                    actionStatus = api.controlClimaterTemp(((DecimalType) command).doubleValue(), "electric");
                    break;
                case CHANNEL_CONTROL_MAXCURRENT:
                    int maxCurrent = ((DecimalType) command).intValue();
                    logger.info("{}: Setting max charging current to {}A", thingId, maxCurrent);
                    action = "controlMaxCurrent";
                    actionStatus = api.controlMaxCharge(maxCurrent);
                    break;
                case CHANNEL_CONTROL_TARGETCHG:
                    int maxLevel = ((DecimalType) command).intValue();
                    logger.info("{}: Setting target charge level to {}", thingId, maxLevel);
                    action = "controlTargetChgLevel";
                    actionStatus = api.controlTargetChgLevel(maxLevel);
                    break;
                case CHANNEL_CONTROL_WINHEAT:
                    action = switchOn ? "startWindowHeat" : "stopWindowHeat";
                    actionStatus = api.controlWindowHeating(switchOn);
                    break;

                /*
                 * case CHANNEL_CONTROL_LOCK:
                 * action = switchOn ? "lock" : "unlock";
                 * actionStatus = api.controlLock(switchOn);
                 * break;
                 * case CHANNEL_CONTROL_PREHEAT:
                 * action = switchOn ? "startPreHeat" : "stopPreHeat";
                 * actionStatus = api.controlPreHeating(switchOn, 30);
                 * break;
                 * case CHANNEL_CONTROL_VENT:
                 * action = switchOn ? "startVentilation" : "stopVentilation";
                 * actionStatus = api.controlVentilation(switchOn, getVentDuration());
                 * break;
                 * case CHANNEL_CONTROL_DURATION:
                 * DecimalType value = new DecimalType(((DecimalType) command).intValue());
                 * logger.debug("{}: Set ventilation/pre-heat duration to {}", thingId, value);
                 * cache.setValue(channelUID.getId(), value);
                 * break;
                 * case CHANNEL_CONTROL_FLASH:
                 * case CHANNEL_CONTROL_HONKFLASH:
                 * if (command == OnOffType.ON) {
                 * State point = cache.getValue(mkChannelId(CHANNEL_GROUP_LOCATION, CHANNEL_LOCATTION_GEO));
                 * if (point != UnDefType.NULL) {
                 * action = CHANNEL_CONTROL_FLASH == channelId ? "flash" : "honk";
                 * actionStatus = api.controlHonkFlash(CHANNEL_CONTROL_HONKFLASH.equals(channelId),
                 * (PointType) point, getHfDuration());
                 * } else {
                 * logger.warn("{}: Geo position is not available, can't execute command", thingId);
                 * }
                 * }
                 * break;
                 * case CHANNEL_CONTROL_HFDURATION:
                 * DecimalType hfd = new DecimalType(((DecimalType) command).intValue());
                 * logger.debug("{}: Set honk%flash duration to {}", thingId, hfd);
                 * cache.setValue(channelUID.getId(), hfd);
                 * break;
                 */ default:
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
