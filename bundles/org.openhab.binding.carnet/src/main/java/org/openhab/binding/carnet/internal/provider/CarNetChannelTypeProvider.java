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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.type.ChannelGroupType;
import org.eclipse.smarthome.core.thing.type.ChannelGroupTypeUID;
import org.eclipse.smarthome.core.thing.type.ChannelType;
import org.eclipse.smarthome.core.thing.type.ChannelTypeBuilder;
import org.eclipse.smarthome.core.thing.type.ChannelTypeProvider;
import org.eclipse.smarthome.core.thing.type.ChannelTypeUID;
import org.openhab.binding.carnet.internal.CarNetTextResources;
import org.openhab.binding.carnet.internal.provider.CarNetIChanneldMapper.ChannelIdMapEntry;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Extends the ChannelTypeProvider for user defined channel and channel group types.
 *
 * @author Markus Eckhardt - Initial contribution
 */
@Component(service = { ChannelTypeProvider.class, CarNetChannelTypeProvider.class }, immediate = true)
public class CarNetChannelTypeProvider implements ChannelTypeProvider {
    private final CarNetTextResources resources;
    private final CarNetIChanneldMapper channelIdMapper;
    private List<ChannelType> channelTypes = new CopyOnWriteArrayList<ChannelType>();
    private List<ChannelGroupType> channelGroupTypes = new CopyOnWriteArrayList<ChannelGroupType>();

    @Activate
    public CarNetChannelTypeProvider(@Reference CarNetTextResources resources,
            @Reference CarNetIChanneldMapper channelIdMapper) {
        this.resources = resources;
        this.channelIdMapper = channelIdMapper;
    }

    @Override
    public Collection<ChannelType> getChannelTypes(Locale locale) {
        return channelTypes;
    }

    @Override
    public ChannelType getChannelType(ChannelTypeUID channelTypeUID, Locale locale) {
        String channelId = channelTypeUID.getId();
        ChannelIdMapEntry channelDef = channelIdMapper.find(channelId);
        if ((channelDef != null) && !channelDef.channelName.isEmpty()) {
            String category = channelDef.getChannelAttribute(resources, "category");
            String group = channelDef.getGroup();
            String label = channelDef.getLabel(resources);
            String description = channelDef.getDescription(resources);
            String attr = channelDef.getAdvanced(resources);
            boolean advanced = group.equals(CHANNEL_GROUP_STATUS) || group.equals(CHANNEL_GROUP_RANGE)
                    || group.equals(CHANNEL_GROUP_WINDOWS) || group.equals(CHANNEL_GROUP_DOORS)
                    || group.equals(CHANNEL_GROUP_TIRES);
            if (!attr.isEmpty()) {
                advanced = Boolean.valueOf(attr);
            }
            if (group.isEmpty() || label.isEmpty() || description.isEmpty() || channelDef.itemType.isEmpty()) {
                return null;
            }
            if (!category.isEmpty()) {
                return ChannelTypeBuilder.state(channelTypeUID, label, channelDef.itemType).withDescription(description)
                        .isAdvanced(advanced).withCategory(category).build();
            } else {
                return ChannelTypeBuilder.state(channelTypeUID, label, channelDef.itemType).withDescription(description)
                        .isAdvanced(advanced).build();
            }
        }
        return null;
    }

    @Override
    public ChannelGroupType getChannelGroupType(ChannelGroupTypeUID channelGroupTypeUID, Locale locale) {
        for (ChannelGroupType channelGroupType : channelGroupTypes) {
            if (channelGroupType.getUID().equals(channelGroupTypeUID)) {
                return channelGroupType;
            }
        }
        return null;
    }

    @Override
    public Collection<ChannelGroupType> getChannelGroupTypes(Locale locale) {
        return channelGroupTypes;
    }

    public void addChannelType(ChannelType type) {
        channelTypes.add(type);
    }

    public void removeChannelType(ChannelType type) {
        channelTypes.remove(type);
    }

    public void removeChannelTypesForThing(ThingUID uid) {
        List<ChannelType> removes = new ArrayList<ChannelType>();
        for (ChannelType c : channelTypes) {
            if (c.getUID().getAsString().startsWith(uid.getAsString())) {
                removes.add(c);
            }
        }
        channelTypes.removeAll(removes);
    }
}
