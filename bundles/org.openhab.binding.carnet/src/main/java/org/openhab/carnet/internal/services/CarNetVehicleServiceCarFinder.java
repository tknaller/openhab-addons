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
package org.openhab.carnet.internal.services;

import static org.openhab.binding.carnet.internal.CarNetBindingConstants.*;
import static org.openhab.binding.carnet.internal.api.CarNetApiConstants.CNAPI_SERVICE_CARFINDER;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.PointType;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.api.CarNetApi;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehiclePosition;
import org.openhab.binding.carnet.internal.handler.CarNetVehicleHandler;
import org.openhab.binding.carnet.internal.provider.CarNetIChanneldMapper.ChannelIdMapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link CarNetVehicleServiceCarFinder} implements the carFinder service.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class CarNetVehicleServiceCarFinder extends CarNetVehicleBaseService {
    private final Logger logger = LoggerFactory.getLogger(CarNetVehicleServiceCarFinder.class);

    public CarNetVehicleServiceCarFinder(CarNetVehicleHandler thingHandler, CarNetApi api) {
        super(thingHandler, api);
        serviceId = CNAPI_SERVICE_CARFINDER;
    }

    @Override
    public boolean createChannels(Map<String, ChannelIdMapEntry> ch) throws CarNetException {
        return false;
    }

    @Override
    public boolean serviceUpdate() throws CarNetException {
        try {
            logger.debug("{}: Get Vehicle Position", thingId);
            updateLocation(api.getStoredPosition(), CHANNEL_STORED_POS);

            CarNetVehiclePosition position = updateLocation(api.getVehiclePosition(), CHANNEL_LOCATTION_GEO);
            String time = position.getCarSentTime();
            updateChannel(CHANNEL_GROUP_LOCATION, CHANNEL_LOCATTION_TIME, new DateTimeType(time));
            String parkingTime = position.getParkingTime();
            updateChannel(CHANNEL_GROUP_LOCATION, CHANNEL_LOCATTION_PARK,
                    parkingTime != null ? new DateTimeType(position.getParkingTime()) : UnDefType.NULL);
            return true;
        } catch (CarNetException e) {
            updateChannel(CHANNEL_GROUP_LOCATION, CHANNEL_STORED_POS, UnDefType.UNDEF);
            updateChannel(CHANNEL_GROUP_LOCATION, CHANNEL_LOCATTION_GEO, UnDefType.UNDEF);
            updateChannel(CHANNEL_GROUP_LOCATION, CHANNEL_LOCATTION_TIME, UnDefType.UNDEF);
            updateChannel(CHANNEL_GROUP_LOCATION, CHANNEL_LOCATTION_PARK, UnDefType.UNDEF);
            if (e.getApiResult().httpCode != HttpStatus.NO_CONTENT_204) { // Ignore No Content = Info not available
                throw e;
            }
        }
        return false;
    }

    private CarNetVehiclePosition updateLocation(CarNetVehiclePosition position, String channel) {
        PointType location = new PointType(new DecimalType(position.getLattitude()),
                new DecimalType(position.getLongitude()));
        updateChannel(CHANNEL_GROUP_LOCATION, channel, location);
        return position;
    }
}
