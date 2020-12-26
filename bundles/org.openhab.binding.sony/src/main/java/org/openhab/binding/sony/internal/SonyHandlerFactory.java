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
package org.openhab.binding.sony.internal;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.openhab.binding.sony.internal.dial.DialConstants;
import org.openhab.binding.sony.internal.dial.DialHandler;
import org.openhab.binding.sony.internal.ircc.IrccConstants;
import org.openhab.binding.sony.internal.ircc.IrccHandler;
import org.openhab.binding.sony.internal.providers.SonyDefinitionProvider;
import org.openhab.binding.sony.internal.providers.SonyDynamicStateProvider;
import org.openhab.binding.sony.internal.scalarweb.ScalarWebHandler;
import org.openhab.binding.sony.internal.simpleip.SimpleIpConstants;
import org.openhab.binding.sony.internal.simpleip.SimpleIpHandler;
import org.openhab.core.io.net.http.WebSocketFactory;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.openhab.core.transform.TransformationHelper;
import org.openhab.core.transform.TransformationService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link SonyHandlerFactory} is responsible for creating all things sony!
 *
 * @author Tim Roberts - Initial contribution
 */
@NonNullByDefault
@Component(immediate = true, service = ThingHandlerFactory.class, configurationPid = "sony.things")
public class SonyHandlerFactory extends BaseThingHandlerFactory {
    /** websocket client used for scalar operations */
    private final WebSocketClient webSocketClient;

    /** The sony thing type provider */
    private final SonyDefinitionProvider sonyDefinitionProvider;

    /** The sony thing type provider */
    private final SonyDynamicStateProvider sonyDynamicStateProvider;

    /** The OSGI properties for the things */
    private final Map<String, String> osgiProperties;

    /**
     * Constructs the handler factory
     * 
     * @param webSocketFactory a non-null websocket factory
     * @param sonyDefinitionProvider a non-null sony definition provider
     * @param sonyDynamicStateProvider a non-null sony dynamic state provider
     * @param osgiProperties a non-null, possibly empty list of OSGI properties
     */
    @Activate
    public SonyHandlerFactory(final @Reference WebSocketFactory webSocketFactory,
            final @Reference SonyDefinitionProvider sonyDefinitionProvider,
            final @Reference SonyDynamicStateProvider sonyDynamicStateProvider,
            final Map<String, String> osgiProperties) {
        Objects.requireNonNull(webSocketFactory, "webSocketFactory cannot be null");
        Objects.requireNonNull(sonyDefinitionProvider, "sonyDefinitionProvider cannot be null");
        Objects.requireNonNull(sonyDynamicStateProvider, "sonyDynamicStateProvider cannot be null");
        Objects.requireNonNull(osgiProperties, "osgiProperties cannot be null");

        this.webSocketClient = webSocketFactory.getCommonWebSocketClient();
        this.sonyDefinitionProvider = sonyDefinitionProvider;
        this.sonyDynamicStateProvider = sonyDynamicStateProvider;
        this.osgiProperties = osgiProperties;
    }

    @Override
    public boolean supportsThingType(final ThingTypeUID thingTypeUID) {
        Objects.requireNonNull(thingTypeUID, "thingTypeUID cannot be null");
        return StringUtils.equalsIgnoreCase(SonyBindingConstants.BINDING_ID, thingTypeUID.getBindingId());
    }

    @Override
    protected @Nullable ThingHandler createHandler(final Thing thing) {
        Objects.requireNonNull(thing, "thing cannot be null");

        final ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (thingTypeUID.equals(SimpleIpConstants.THING_TYPE_SIMPLEIP)) {
            final TransformationService transformationService = TransformationHelper
                    .getTransformationService(getBundleContext(), "MAP");
            return new SimpleIpHandler(thing, transformationService);
        } else if (thingTypeUID.equals(IrccConstants.THING_TYPE_IRCC)) {
            final TransformationService transformationService = TransformationHelper
                    .getTransformationService(getBundleContext(), "MAP");
            return new IrccHandler(thing, transformationService);
        } else if (thingTypeUID.equals(DialConstants.THING_TYPE_DIAL)) {
            return new DialHandler(thing);
        } else if (thingTypeUID.getId().startsWith(SonyBindingConstants.SCALAR_THING_TYPE_PREFIX)) {
            final TransformationService transformationService = TransformationHelper
                    .getTransformationService(getBundleContext(), "MAP");

            return new ScalarWebHandler(thing, transformationService, webSocketClient, sonyDefinitionProvider,
                    sonyDynamicStateProvider, osgiProperties);
        }

        return null;
    }
}
