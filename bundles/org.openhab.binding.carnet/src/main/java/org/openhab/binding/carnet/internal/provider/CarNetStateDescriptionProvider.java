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

package org.openhab.binding.carnet.internal.provider;

import static org.openhab.binding.carnet.internal.CarNetBindingConstants.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.eclipse.smarthome.core.thing.type.DynamicStateDescriptionProvider;
import org.eclipse.smarthome.core.types.StateDescription;
import org.eclipse.smarthome.core.types.StateDescriptionFragmentBuilder;
import org.eclipse.smarthome.core.types.StateOption;
import org.openhab.binding.carnet.internal.CarNetTextResources;
import org.openhab.binding.carnet.internal.provider.CarNetIChanneldMapper.ChannelIdMapEntry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link CarNetStateDescriptionProvider} class is a dynamic provider of state options while leaving other state
 * description fields as original.
 *
 * @author Markus Michels - Initial contribution
 */
@Component(service = { DynamicStateDescriptionProvider.class, CarNetStateDescriptionProvider.class })
@NonNullByDefault
public class CarNetStateDescriptionProvider implements DynamicStateDescriptionProvider {
    private final Map<ChannelUID, @Nullable List<StateOption>> channelOptionsMap = new ConcurrentHashMap<>();
    private final CarNetTextResources resources;
    private final CarNetIChanneldMapper channelIdMapper;

    @Activate
    public CarNetStateDescriptionProvider(@Reference CarNetTextResources resources,
            @Reference CarNetIChanneldMapper channelIdMapper) {
        this.resources = resources;
        this.channelIdMapper = channelIdMapper;
    }

    public void setStateOptions(ChannelUID channelUID, List<StateOption> options) {
        channelOptionsMap.put(channelUID, options);
    }

    @Override
    public @Nullable StateDescription getStateDescription(Channel channel, @Nullable StateDescription original,
            @Nullable Locale locale) {
        ChannelTypeUID ctu = channel.getChannelTypeUID();
        if (ctu == null) {
            return null;
        }
        String channelId = ctu.getId();
        StateDescriptionFragmentBuilder builder = buildStateDescriptor(channelId);
        if (builder == null) {
            builder = original == null ? StateDescriptionFragmentBuilder.create()
                    : StateDescriptionFragmentBuilder.create(original);
        }

        List<StateOption> options = channelOptionsMap.get(channel.getUID());
        if (options != null) {
            return builder.withOptions(options).build().toStateDescription();
        } else {
            return builder.build().toStateDescription();
        }
    }

    @Deactivate
    public void deactivate() {
        channelOptionsMap.clear();
    }

    private @Nullable StateDescriptionFragmentBuilder buildStateDescriptor(String channelId) {
        ChannelIdMapEntry channelDef = channelIdMapper.find(channelId);
        if (channelDef == null) {
            return null;
        }
        StateDescriptionFragmentBuilder state = StateDescriptionFragmentBuilder.create()
                .withReadOnly(channelDef.readOnly);
        String min = getChannelAttribute(channelId, "min");
        String max = getChannelAttribute(channelId, "max");
        String step = getChannelAttribute(channelId, "step");
        String pattern = getChannelAttribute(channelId, "pattern");
        if (pattern.isEmpty()) {
            switch (channelDef.itemType) {
                case ITEMT_SWITCH:
                    break;
                case ITEMT_STRING:
                    pattern = "%s";
                    break;
                case ITEMT_NUMBER:
                default:
                    if (channelDef.unit != null) {
                        pattern = "%f %unit%";
                    }
            }
        }
        if (!pattern.isEmpty()) {
            state = state.withPattern(pattern);
        }
        if (!min.isEmpty()) {
            state = state.withMinimum(new BigDecimal(Double.parseDouble(min)));
        }
        if (!max.isEmpty()) {
            state = state.withMaximum(new BigDecimal(Double.parseDouble(max)));
        }
        if (!min.isEmpty()) {
            state = state.withStep(new BigDecimal(Double.parseDouble(step)));
        }
        return state;
    }

    private String getChannelAttribute(String channelId, String attribute) {
        String key = "channel-type.carnet." + channelId + "." + attribute;
        String value = resources.getText(key);
        return !value.equals(key) ? value : "";
    }
}
