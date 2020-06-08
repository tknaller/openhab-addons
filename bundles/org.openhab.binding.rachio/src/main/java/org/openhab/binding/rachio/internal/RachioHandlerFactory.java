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
package org.openhab.binding.rachio.internal;

import static org.openhab.binding.rachio.internal.RachioBindingConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.rachio.internal.api.json.RachioEventGsonDTO;
import org.openhab.binding.rachio.internal.handler.RachioBridgeHandler;
import org.openhab.binding.rachio.internal.handler.RachioDeviceHandler;
import org.openhab.binding.rachio.internal.handler.RachioZoneHandler;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link RachioHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
@Component(service = { ThingHandlerFactory.class,
        RachioHandlerFactory.class }, immediate = true, configurationPid = "binding." + BINDING_ID)
public class RachioHandlerFactory extends BaseThingHandlerFactory {

    public class RachioBridge {
        @Nullable
        RachioBridgeHandler cloudHandler;
        @Nullable
        ThingUID uid;
    }

    private final Logger logger = LoggerFactory.getLogger(RachioHandlerFactory.class);
    private final HashMap<String, RachioBridge> bridgeList;
    private final RachioConfiguration bindingConfig = new RachioConfiguration();

    /**
     * OSGi activation callback.
     *
     */
    @Activate
    public RachioHandlerFactory(ComponentContext componentContext,
            @Nullable Map<String, @Nullable Object> configProperties) {
        super.activate(componentContext);
        logger.debug("RachioHandlerFactory: Initialized Rachio Thing handler.");
        bridgeList = new HashMap<String, RachioBridge>();

        logger.debug("RachioBridge: Activate, configurarion:");
        bindingConfig.updateConfig(configProperties);
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        try {
            ThingTypeUID thingTypeUID = thing.getThingTypeUID();
            logger.trace("RachioHandlerFactory: Create thing handler for type {}", thingTypeUID.toString());
            if (SUPPORTED_BRIDGE_THING_TYPES_UIDS.contains(thingTypeUID)) {
                return createBridge((Bridge) thing);
            } else if (SUPPORTED_ZONE_THING_TYPES_UIDS.contains(thingTypeUID)) {
                return new RachioZoneHandler(thing);
            } else if (SUPPORTED_DEVICE_THING_TYPES_UIDS.contains(thingTypeUID)) {
                return new RachioDeviceHandler(thing);
            }
        } catch (RuntimeException e) {
            logger.debug("RachioHandlerFactory:Exception while creating Rachio Thing handler: {}", e.toString());
        }

        logger.debug("RachioHandlerFactory:: Unable to create thing handler!");
        return null;
    }

    @Override
    protected void removeHandler(final ThingHandler thingHandler) {
        logger.debug("Removing Rachio Cloud handler");
        if (thingHandler instanceof RachioBridgeHandler) {
            RachioBridgeHandler bridgeHandler = (RachioBridgeHandler) thingHandler;
            bridgeHandler.shutdown();
        }
        if (thingHandler instanceof RachioDeviceHandler) {
            RachioDeviceHandler deviceHandler = (RachioDeviceHandler) thingHandler;
            deviceHandler.shutdown();
        }
        if (thingHandler instanceof RachioZoneHandler) {
            RachioZoneHandler zoneHandler = (RachioZoneHandler) thingHandler;
            zoneHandler.shutdown();
        }
    }

    /**
     * Called from the webhook servlet. event.externalId is used to route the event to the corresponding bridge handler
     *
     * @param event
     */
    @SuppressWarnings("null")
    public boolean webHookEvent(String ipAddress, RachioEventGsonDTO event) {
        try {
            logger.debug("Rachio Cloud Event for device '{}' received", event.deviceId);

            // process event parameters
            for (HashMap.Entry<String, RachioBridge> be : bridgeList.entrySet()) {
                RachioBridge bridge = be.getValue();
                Validate.notNull(bridge);
                Validate.notNull(bridge.cloudHandler);
                logger.trace("Check for externalId: '{}' / '{}'", event.externalId,
                        bridge.cloudHandler.getExternalId());
                if (bridge.cloudHandler.getExternalId().equals(event.externalId)) {
                    return bridge.cloudHandler.webHookEvent(event);
                }
            }

            // invalid externalId, could be an indicator for unauthorized access
            logger.warn("Unauthorized webhook event (wrong externalId: '{}')", event.externalId);
            return false;
        } catch (RuntimeException e) {
            logger.debug("Unable to process event: {}", e.getMessage());
        }
        logger.debug("Unable to route event to bridge, externalId='{}', deviceId='{}'", event.externalId,
                event.deviceId);
        return false;
    }

    @Nullable
    @SuppressWarnings("null")
    private RachioBridgeHandler createBridge(Bridge bridgeThing) {
        try {
            RachioBridge bridge = new RachioBridge();
            bridge.uid = bridgeThing.getUID();
            bridge.cloudHandler = new RachioBridgeHandler(bridgeThing);
            bridge.cloudHandler.setConfiguration(bindingConfig);
            bridgeList.put(bridge.uid.toString(), bridge);

            Validate.notNull(bridge.cloudHandler);
            return bridge.cloudHandler;
        } catch (RuntimeException e) {
            logger.warn("RachioFactory: Unable to create bridge thing: {}: ", e.getMessage());
        }
        return null;
    }
}
