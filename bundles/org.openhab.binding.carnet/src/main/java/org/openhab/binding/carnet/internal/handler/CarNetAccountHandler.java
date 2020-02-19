package org.openhab.binding.carnet.internal.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.type.DynamicStateDescriptionProvider;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.carnet.internal.CarNetDeviceListener;
import org.openhab.binding.carnet.internal.CarNetException;
import org.openhab.binding.carnet.internal.CarNetVehicleInformation;
import org.openhab.binding.carnet.internal.api.CarNetApi;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetApiToken;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleDetails;
import org.openhab.binding.carnet.internal.api.CarNetApiGSonDTO.CarNetVehicleList;
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
    private @Nullable List<CarNetVehicleInformation> vehicleList;
    private List<CarNetDeviceListener> vehicleInformationListeners = Collections
            .synchronizedList(new ArrayList<CarNetDeviceListener>());

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
            try {
                initializeThing();
                updateStatus(ThingStatus.ONLINE);
            } catch (CarNetException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.toString());
            }
        });

    }

    @SuppressWarnings("null")
    public boolean initializeThing() throws CarNetException {
        Map<String, String> properties = new TreeMap<String, String>();

        config = getConfigAs(CarNetAccountConfiguration.class);
        api = new CarNetApi(httpClient, config);
        Validate.notNull(api, "Unable to create API instance");
        api.initialize();
        @SuppressWarnings("null")
        CarNetApiToken token = api.getToken();
        if (token == null) {
            throw new CarNetException("Unable to get access token!");
        }
        refreshProperties(properties);

        CarNetVehicleList vehices = api.getVehicles();
        vehicleList = new ArrayList<CarNetVehicleInformation>();
        for (String vin : vehices.userVehicles.vehicle) {
            CarNetVehicleDetails details = api.getVehicleDetails(vin);
            CarNetVehicleInformation vehicle = new CarNetVehicleInformation(details);
            vehicleList.add(vehicle);
        }
        informVehicleInformationListeners(vehicleList);
        return true;
    }

    public void registerListener(CarNetDeviceListener listener) {
        vehicleInformationListeners.add(listener);
    }

    public void unregisterListener(CarNetDeviceListener listener) {
        vehicleInformationListeners.remove(listener);
    }

    private void informVehicleInformationListeners(@Nullable List<CarNetVehicleInformation> vehicleInformationList) {
        this.vehicleInformationListeners.forEach(discovery -> discovery.informationUpdate(vehicleInformationList));
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
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
     * Add one property to the Thing Properties
     *
     * @param key Name of the property
     * @param value Value of the property
     */
    public void updateProperties(String key, String value) {
        Map<String, String> property = new TreeMap<String, String>();
        property.put(key, value);
        updateProperties(property);
    }

    public void refreshProperties(Map<String, String> newProperties) {
        Map<String, String> thingProperties = editProperties();
        for (Map.Entry<String, String> prop : newProperties.entrySet()) {
            if (thingProperties.containsKey(prop.getKey())) {
                thingProperties.replace(prop.getKey(), prop.getValue());
            } else {
                thingProperties.put(prop.getKey(), prop.getValue());
            }
        }
        updateProperties(thingProperties);
        logger.trace("Properties updated");
    }

    @Override
    public void childHandlerInitialized(ThingHandler childHandler, Thing childThing) {
        super.childHandlerInitialized(childHandler, childThing);
    }

    @Override
    public void childHandlerDisposed(ThingHandler childHandler, Thing childThing) {
        super.childHandlerDisposed(childHandler, childThing);
    }

    /**
     * Disposes the bridge.
     */
    @Override
    public void dispose() {
        logger.debug("Handler disposed.");
    }
}
