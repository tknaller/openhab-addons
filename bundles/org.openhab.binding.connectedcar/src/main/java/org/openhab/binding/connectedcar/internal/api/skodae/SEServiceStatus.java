/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
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
package org.openhab.binding.connectedcar.internal.api.skodae;

import static org.openhab.binding.connectedcar.internal.BindingConstants.*;
import static org.openhab.binding.connectedcar.internal.api.ApiDataTypesDTO.API_SERVICE_VEHICLE_STATUS_REPORT;
import static org.openhab.binding.connectedcar.internal.util.Helpers.*;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.connectedcar.internal.api.ApiBase;
import org.openhab.binding.connectedcar.internal.api.ApiBaseService;
import org.openhab.binding.connectedcar.internal.api.ApiException;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleSettings.SEChargerSettings;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleSettings.SEClimaterSettings;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData.SEVehicleStatus.SEChargerStatus;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData.SEVehicleStatus.SEClimaterStatus;
import org.openhab.binding.connectedcar.internal.api.skodae.SEApiJsonDTO.SEVehicleStatusData.SEVehicleStatus.SEClimaterStatus.SEHeatingStatus;
import org.openhab.binding.connectedcar.internal.handler.ThingBaseHandler;
import org.openhab.binding.connectedcar.internal.provider.ChannelDefinitions.ChannelIdMapEntry;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SEServiceStatus} implements the Status Service for Skoda Enyak.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class SEServiceStatus extends ApiBaseService {
    private final Logger logger = LoggerFactory.getLogger(SEServiceStatus.class);

    public SEServiceStatus(ThingBaseHandler thingHandler, ApiBase api) {
        super(API_SERVICE_VEHICLE_STATUS_REPORT, thingHandler, api);
    }

    @Override
    public boolean createChannels(Map<String, ChannelIdMapEntry> channels) throws ApiException {
        addChannels(channels, true, CHANNEL_CONTROL_CHARGER, CHANNEL_CHARGER_MAXCURRENT, CHANNEL_CHARGER_CHG_STATE,
                CHANNEL_CHARGER_MODE, CHANNEL_CONTROL_TARGETCHG, CHANNEL_CHARGER_CHGLVL, CHANNEL_CHARGER_REMAINING,
                CHANNEL_CHARGER_RATE, CHANNEL_CHARGER_POWER, CHANNEL_RANGE_TOTAL, CHANNEL_CHARGER_PLUG_STATE,
                CHANNEL_CHARGER_LOCK_STATE, CHANNEL_CLIMATER_GEN_STATE, CHANNEL_CLIMATER_REMAINING,
                CHANNEL_CONTROL_CLIMATER, CHANNEL_CONTROL_TARGET_TEMP, CHANNEL_CONTROL_WINHEAT);
        return true;
    }

    @Override
    public boolean serviceUpdate() throws ApiException {
        // Try to query status information from vehicle
        logger.debug("{}: Get Vehicle Status", thingId);
        boolean updated = false;

        SEVehicleStatusData status = api.getVehicleStatus().seStatus;
        if (status != null) {
            updated |= updateRangeStatus(status);
            updated |= updateChargingStatus(status);
            updated |= updateClimatisationStatus(status);
            updated |= updateWindowHeatStatus(status);
        }
        return updated;
    }

    private boolean updateRangeStatus(SEVehicleStatusData data) {
        boolean updated = false;
        String group = CHANNEL_GROUP_RANGE;
        SEChargerStatus s = data.status.charger;
        if (s != null) {
            updated |= updateChannel(group, CHANNEL_RANGE_TOTAL,
                    toQuantityType(getLong(s.battery.cruisingRangeElectricInMeters) / 1000.0, 1, KILOMETRE));
        }
        return updated;
    }

    private boolean updateChargingStatus(SEVehicleStatusData data) {
        boolean updated = false;
        String group = CHANNEL_GROUP_CHARGER;
        if (data.status.charger != null) {
            SEChargerStatus s = data.status.charger;
            if (s.charging != null) {
                String state = getString(s.charging.state);
                updated |= updateChannel(CHANNEL_GROUP_CONTROL, CHANNEL_CONTROL_CHARGER,
                        "charging".equalsIgnoreCase(state) ? OnOffType.ON : OnOffType.OFF);
                updated |= updateChannel(group, CHANNEL_CHARGER_CHG_STATE, getStringType(state));
                updated |= updateChannel(group, CHANNEL_CHARGER_MODE, getStringType(s.charging.chargeMode));
                updated |= updateChannel(group, CHANNEL_CHARGER_REMAINING,
                        toQuantityType(getLong(s.charging.remainingToCompleteInSeconds) / 60.0, 0, Units.MINUTE));
                updated |= updateChannel(group, CHANNEL_CHARGER_POWER,
                        toQuantityType(getDouble(s.charging.chargingPowerInWatts), 0, Units.WATT));
                updated |= updateChannel(group, CHANNEL_CHARGER_RATE,
                        getDecimal(s.charging.chargingRateInKilometersPerHour));
            }

            if (s.battery != null) {
                updated |= updateChannel(group, CHANNEL_CHARGER_CHGLVL,
                        toQuantityType(getInteger(s.battery.stateOfChargeInPercent), 0, PERCENT));
            }
            if (s.plug != null) {
                updated |= updateChannel(group, CHANNEL_CHARGER_LOCK_STATE, getOnOffType(getString(s.plug.lockState)));
                updated |= updateChannel(group, CHANNEL_CHARGER_PLUG_STATE, getStringType(s.plug.connectionState));
            }
        }

        if (data.settings.charger != null) {
            SEChargerSettings s = data.settings.charger;
            updated |= updateChannel(group, CHANNEL_CONTROL_TARGETCHG,
                    toQuantityType(getInteger(s.targetStateOfChargeInPercent), 0, PERCENT));
            updated |= updateChannel(CHANNEL_GROUP_CHARGER, CHANNEL_CHARGER_MAXCURRENT,
                    getStringType(s.maxChargeCurrentAc));
        }

        return updated;
    }

    private boolean updateClimatisationStatus(SEVehicleStatusData data) {
        boolean updated = false;
        String group = CHANNEL_GROUP_CLIMATER;

        SEClimaterStatus status = data.status.climatisation;
        if (status != null) {
            updated |= updateChannel(group, CHANNEL_CLIMATER_GEN_STATE, getOnOffType(status.state));
            updated |= updateChannel(group, CHANNEL_CLIMATER_REMAINING,
                    toQuantityType(getInteger(status.remainingTimeToReachTargetTemperatureInSeconds), 0, Units.SECOND));
        }

        SEClimaterSettings settings = data.settings.climater;
        if (settings != null) {
            Double tempC = Units.KELVIN.getConverterTo(SIUnits.CELSIUS).convert(settings.targetTemperatureInKelvin)
                    .doubleValue();
            updated |= updateChannel(group, CHANNEL_CONTROL_TARGET_TEMP, toQuantityType(tempC, 1, SIUnits.CELSIUS));
        }
        return updated;
    }

    private boolean updateWindowHeatStatus(SEVehicleStatusData data) {
        boolean updated = false;
        SEClimaterStatus status = data.status.climatisation;
        if (status != null) {
            // show only aggregated status
            boolean on = false;
            for (SEHeatingStatus s : status.windowsHeatingStatuses) {
                on |= getOnOffType(s.state) == OnOffType.ON;
            }
            updated |= updateChannel(CHANNEL_GROUP_CONTROL, CHANNEL_CONTROL_WINHEAT, getOnOff(on));
        }
        return updated;
    }
}
