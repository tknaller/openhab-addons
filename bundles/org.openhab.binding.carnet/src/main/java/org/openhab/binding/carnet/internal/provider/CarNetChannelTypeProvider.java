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
package org.openhab.binding.carnet.internal.provider;

import static org.openhab.binding.carnet.internal.CarNetBindingConstants.BINDING_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.carnet.internal.CarNetTextResources;
import org.openhab.binding.carnet.internal.api.CarNetChannelIdMapper;
import org.openhab.binding.carnet.internal.api.CarNetChannelIdMapper.ChannelIdMapEntry;
import org.openhab.core.thing.type.ChannelDefinition;
import org.openhab.core.thing.type.ChannelDefinitionBuilder;
import org.openhab.core.thing.type.ChannelGroupType;
import org.openhab.core.thing.type.ChannelGroupTypeBuilder;
import org.openhab.core.thing.type.ChannelGroupTypeProvider;
import org.openhab.core.thing.type.ChannelGroupTypeUID;
import org.openhab.core.thing.type.ChannelType;
import org.openhab.core.thing.type.ChannelTypeBuilder;
import org.openhab.core.thing.type.ChannelTypeProvider;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends the ChannelTypeProvider for user defined channel and channel group types.
 *
 * @author Markus Eckhardt - Initial contribution
 */
@NonNullByDefault
@Component(service = { ChannelTypeProvider.class, CarNetChannelTypeProvider.class })
public class CarNetChannelTypeProvider implements ChannelTypeProvider, ChannelGroupTypeProvider {
    private final Logger logger = LoggerFactory.getLogger(CarNetChannelTypeProvider.class);
    private final CarNetChannelIdMapper channelIdMapper;
    private final CarNetTextResources resources;

    private static List<ChannelType> channelTypes = new CopyOnWriteArrayList<ChannelType>(); // cache accross all things
    private static List<ChannelGroupType> channelGroupTypes = new CopyOnWriteArrayList<ChannelGroupType>();

    @Activate
    public CarNetChannelTypeProvider(@Reference CarNetTextResources resources,
            @Reference CarNetChannelIdMapper channelIdMapper) {
        this.channelIdMapper = channelIdMapper;
        this.resources = resources;
    }

    @Override
    public @Nullable ChannelGroupType getChannelGroupType(ChannelGroupTypeUID channelGroupTypeUID,
            @Nullable Locale locale) {
        return null; // return getChannelGroupType(channelGroupTypeUID.getAsString());
    }

    @Override
    public Collection<ChannelGroupType> getChannelGroupTypes(@Nullable Locale locale) {
        return channelGroupTypes;
    }

    public ChannelGroupType addChannelGroupType(String group) {
        ChannelGroupType groupType = getChannelGroupType(group);
        if (groupType != null) {
            return groupType;
        }

        ChannelGroupTypeUID channelGroupTypeUID = new ChannelGroupTypeUID(BINDING_ID, group);
        String label = CarNetChannelIdMapper.getGroupAttribute(resources, group, "label");
        char index = group.charAt(group.length() - 1);
        if (Character.isDigit(index)) {
            label = label + "[" + index + "]"; // add group index to label
        }

        // Generate channel definition for all channels belonging to the requested group
        List<ChannelDefinition> channelDefinitions = new ArrayList<>();
        for (Map.Entry<String, ChannelIdMapEntry> e : channelIdMapper.getDefinitions().entrySet()) {
            ChannelIdMapEntry channelDef = e.getValue();
            if (!channelDef.channelName.isEmpty() && channelDef.groupName.equalsIgnoreCase(group)) {
                ChannelTypeUID uid = new ChannelTypeUID(BINDING_ID, channelDef.channelName);
                ChannelDefinition cd = new ChannelDefinitionBuilder(channelDef.channelName, uid)
                        .withLabel(channelDef.getLabel()).withDescription(channelDef.getDescription()).build();
                channelDefinitions.add(cd);
            }
        }

        logger.trace("Adding ChannelGroupType with id={}, UID={}", group, channelGroupTypeUID.getAsString());
        // groupType = ChannelGroupTypeBuilder.instance(channelGroupTypeUID, label)
        // .withDescription(CarNetChannelIdMapper.getGroupAttribute(resources, group, "description"))
        // .withChannelDefinitions(channelDefinitions).build();
        groupType = ChannelGroupTypeBuilder.instance(channelGroupTypeUID, label)
                .withDescription(CarNetChannelIdMapper.getGroupAttribute(resources, group, "description")).build();
        channelGroupTypes.add(groupType);
        return groupType;
    }

    public @Nullable ChannelGroupType getChannelGroupType(String group) {
        /*
         * for (ChannelGroupType gt : channelGroupTypes) {
         * if (gt.getUID().getId().equals(group)) {
         * return gt;
         * }
         * }
         */
        return null;
    }

    @Override
    public Collection<ChannelType> getChannelTypes(@Nullable Locale locale) {
        return channelTypes;
    }

    @Override
    public @Nullable ChannelType getChannelType(ChannelTypeUID channelTypeUID, @Nullable Locale locale) {
        for (ChannelType c : channelTypes) {
            if (c.getUID().getAsString().startsWith(channelTypeUID.getAsString())) {
                return c;
            }
        }

        return addChannelType(channelTypeUID);
    }

    public @Nullable ChannelType addChannelType(ChannelTypeUID channelTypeUID) {
        ChannelType ct = getChannelType(channelTypeUID);
        if (ct != null) {
            return ct;
        }

        String channelId = channelTypeUID.getId();
        ChannelIdMapEntry channelDef = channelIdMapper.find(channelId);
        if (channelDef == null) {
            return null;
        }

        StateDescriptionFragmentBuilder desc = CarNetStateDescriptionProvider.buildStateDescriptor(resources,
                channelIdMapper, channelId);
        if (desc != null) {
            logger.trace("Adding ChannelType with id={}, UID={}", channelId, channelTypeUID.toString());
            ct = ChannelTypeBuilder.state(channelTypeUID, channelDef.getLabel(), channelDef.itemType)
                    .withDescription(channelDef.getDescription()).isAdvanced(channelDef.advanced)
                    .withStateDescriptionFragment(desc.build()).build();
            channelTypes.add(ct);
        }
        return ct;
    }

    public @Nullable ChannelType getChannelType(ChannelTypeUID channelTypeUID) {
        for (ChannelType ct : channelTypes) {
            if (ct.getUID().getAsString().startsWith(channelTypeUID.getAsString())) {
                return ct;
            }
        }
        return null;
    }
}
