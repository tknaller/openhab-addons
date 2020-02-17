package org.openhab.binding.carnet.internal.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.type.DynamicStateDescriptionProvider;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.carnet.internal.api.CarNetApi;
import org.openhab.binding.carnet.internal.config.CarNetAccountConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class CarNetAccountHandler extends BaseBridgeHandler {
    private final Logger logger = LoggerFactory.getLogger(CarNetAccountHandler.class);
    /**
     * shared instance of HTTP client for asynchronous calls
     */
    private @Nullable final HttpClient httpClient;
    private @Nullable final DynamicStateDescriptionProvider stateDescriptionProvider;
    private @Nullable CarNetAccountConfiguration config;
    private @Nullable CarNetApi api;

    /**
     * keeps track of the {@link ChannelUID} for the 'apply_tamplate' {@link Channel}
     */
    // private final ChannelUID applyTemplateChannelUID;

    /**
     * Constructor
     *
     * @param bridge Bridge object representing a FRITZ!Box
     */
    public CarNetAccountHandler(Bridge bridge, @Nullable HttpClient httpClient,
            @Nullable DynamicStateDescriptionProvider stateDescriptionProvider) {
        super(bridge);
        this.httpClient = httpClient;
        this.stateDescriptionProvider = stateDescriptionProvider;
        // applyTemplateChannelUID = new ChannelUID(bridge.getUID(), CHANNEL_APPLY_TEMPLATE);
    }

    /**
     * Initializes the bridge.
     */
    @Override
    public void initialize() {
        config = getConfigAs(CarNetAccountConfiguration.class);

        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            if (initializeThing()) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

    }

    boolean initializeThing() {
        config = getConfigAs(CarNetAccountConfiguration.class);
        api = new CarNetApi(httpClient, config);
        return true;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        String channelId = channelUID.getIdWithoutGroup();
        logger.debug("Handle command '{}' for channel {}", command, channelId);
        if (command == RefreshType.REFRESH) {
            return;
        }

    }

    /**
     * Disposes the bridge.
     */
    @Override
    public void dispose() {
        logger.debug("Handler disposed.");
    }
}
