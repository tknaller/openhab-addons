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
package org.openhab.binding.appletv.internal;

import static org.openhab.binding.appletv.internal.AppleTVBindingConstants.SUPPORTED_THING_TYPES_UIDS;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.appletv.internal.config.AppleTVBindingConfiguration;
import org.openhab.binding.appletv.internal.handler.AppleTVHandler;
import org.openhab.binding.appletv.internal.jpy.LibATVCallback;
import org.openhab.binding.appletv.internal.jpy.LibPyATV;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.annotations.NonNull;

/**
 * The {@link AppleTVHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
@Component(service = { ThingHandlerFactory.class, AppleTVHandlerFactory.class }, configurationPid = "binding.appletv")
public class AppleTVHandlerFactory extends BaseThingHandlerFactory implements LibATVCallback {
    private final Logger logger = LoggerFactory.getLogger(AppleTVHandlerFactory.class);

    private AppleTVBindingConfiguration bindingConfig = new AppleTVBindingConfiguration();

    private @Nullable LibPyATV pyATV = null;
    private String jsonDevices = "";
    private String lastDeviceId = "";

    /**
     * Activate the bundle: save properties
     *
     * @param componentContext
     * @param configProperties set of properties from cfg (use same names as in
     *            thing config)
     */
    @Activate
    public void AppleTVHandlerFactory(ComponentContext componentContext, Map<String, Object> configProperties) {
        logger.debug("Activate HandlerFactory");
        try {
            super.activate(componentContext);
            pyATV = new LibPyATV("");
            logger.debug("PyATV installation path: {}", pyATV.getLibPath());
        } catch (AppleTVException e) {

        }
    }

    public void setBindingConfig(AppleTVBindingConfiguration bindingConfig) {
        this.bindingConfig.update(bindingConfig);
        logger.info("Binding configuration refreshed");
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (AppleTVBindingConstants.THING_TYPE_APPLETV.equals(thingTypeUID)) {
            return new AppleTVHandler(thing, this);
        }
        return null;
    }

    /**
     * Remove handler of things.
     */
    @Override
    protected synchronized void removeHandler(@NonNull ThingHandler thingHandler) {
        if (thingHandler instanceof AppleTVHandler) {
        }
    }

    @SuppressWarnings("null")
    public void initPyATV(AppleTVHandler thingHandler) throws AppleTVException {
        pyATV.init(thingHandler);
    }

    @SuppressWarnings("null")
    public boolean sendCommands(String commands, @Nullable Object handler, String ipAddress, String loginId) {
        String[] args = new String[1];
        return pyATV.sendCommands(commands, handler, ipAddress, loginId, args);
    }

    @SuppressWarnings("null")
    public String scanDevices() {
        try {
            jsonDevices = "";
            pyATV.scanDevices(this);
            return jsonDevices;
        } catch (Exception e) {
            logger.info("Device scan failed!");
        }
        return "";
    }

    public boolean pairDevice(AppleTVHandler thingHandler, String remoteName, String pairingPIN)
            throws AppleTVException {
        Validate.notNull(pyATV);
        return pyATV.pairDevice(thingHandler, remoteName, pairingPIN);
    }

    @SuppressWarnings("null")
    String getLibPath() {
        return pyATV.getLibPath();

    }

    public String getLastDeviceId() {
        return lastDeviceId;
    }

    /**
     * Callback for PyATV module delivery the device list in JSON format
     *
     * @param json
     */
    @Override
    public void devicesDiscovered(String json) {
        logger.debug("Discovered devices: {}", json);
        jsonDevices = json;
    }

    @Override

    public void generatedDeviceId(String id) {
        lastDeviceId = id;
    }

    @Override
    public void info(String message) {
        logger.info("{}", message);
    }

    @Override
    public void debug(String message) {
        logger.debug("{}", message);
    }

    @Override
    public void statusEvent(String prop, String input) {
        logger.debug("Unexpected call to AppleTVHandlerFactory.statusEvent()");

    }

    @Override
    public void pairingResult(boolean result, String message) {
        logger.debug("Unexpected call to AppleTVHandlerFactory.pairingResult()");
    }
}
