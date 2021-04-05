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
package org.openhab.binding.dreamscreen.internal.handler;

import static org.openhab.binding.dreamscreen.internal.DreamScreenBindingConstants.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.thing.Channel;
import org.openhab.core.thing.ThingUID;
import org.openhab.core.thing.type.ChannelTypeUID;
import org.openhab.core.thing.type.DynamicStateDescriptionProvider;
import org.openhab.core.types.StateDescription;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.openhab.core.types.StateOption;

/**
 * The {@link DreamScreenInputDescriptionProvider} provides dynamic channel state descriptions.
 *
 * @author Bruce Brouwer - Initial contribution
 */
@NonNullByDefault
public class DreamScreenInputDescriptionProvider implements DynamicStateDescriptionProvider {

    private final ThingUID thingUID;
    private String inputName1 = "Input 1", inputName2 = "Input 2", inputName3 = "Input 3";

    public DreamScreenInputDescriptionProvider(ThingUID thingUID) {
        this.thingUID = thingUID;
    }

    public void setInputDescriptions(final String inputName1, final String inputName2, final String inputName3) {
        this.inputName1 = inputName1;
        this.inputName2 = inputName2;
        this.inputName3 = inputName3;
    }

    @Override
    public @Nullable StateDescription getStateDescription(Channel channel,
            @Nullable StateDescription originalStateDescription, @Nullable Locale locale) {
        ChannelTypeUID channelTypeUID = channel.getChannelTypeUID();
        if (channelTypeUID == null || !BINDING_ID.equals(channelTypeUID.getBindingId())) {
            return null;
        }
        if (originalStateDescription == null) {
            return null;
        }
        final List<StateOption> options = Arrays.asList( //
                new StateOption(INPUT_CHANNEL1, this.inputName1), new StateOption(INPUT_CHANNEL2, this.inputName2),
                new StateOption(INPUT_CHANNEL3, this.inputName3));
        return StateDescriptionFragmentBuilder.create(originalStateDescription).withMinimum(BigDecimal.ZERO)
                .withMaximum(BigDecimal.valueOf(2)).withStep(BigDecimal.ONE).withReadOnly(false).withOptions(options)
                .build().toStateDescription();
    }
}
