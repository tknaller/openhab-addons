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
package org.openhab.binding.appletv.internal.handler;

import static org.openhab.binding.appletv.internal.AppleTVBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.appletv.internal.AppleTVException;
import org.openhab.binding.appletv.internal.AppleTVHandlerFactory;
import org.openhab.binding.appletv.internal.config.AppleTVThingConfiguration;
import org.openhab.binding.appletv.internal.jpy.LibATVCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AppleTVHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class AppleTVHandler extends BaseThingHandler implements LibATVCallback {

    private final Logger logger = LoggerFactory.getLogger(AppleTVHandler.class);

    private @Nullable AppleTVThingConfiguration config;
    private AppleTVHandlerFactory handlerFactory;
    private Map<String, Object> playStatus = new HashMap<>();
    private Map<String, String> channelMap = new HashMap<>();

    private @Nullable ScheduledFuture<?> statusJob;
    private int skipUpdate = 0;
    private int requestUpdates = 1;

    private Long position = 0l;
    private Long totalTime = 0l;

    public AppleTVHandler(Thing thing, AppleTVHandlerFactory handlerFactory) {
        super(thing);
        this.handlerFactory = handlerFactory;
        initChannelMap();
        initPlayStatus();
    }

    @SuppressWarnings("null")
    @Override
    public void initialize() {

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            logger.info("Initializing AppleTV");
            config = getConfigAs(AppleTVThingConfiguration.class);

            try {
                Configuration configuration = getConfig();
                // logger.info("PyATV Library installed in {}", config.libPath);
                // configuration.remove("libPath");
                // configuration.put("libPath", handlerFactory.getLibPath());
                updateConfiguration(configuration);
                logger.debug("Configuration updated.");

                // pass class instance for callbacks
                handlerFactory.initPyATV(this);
                updateStatus(ThingStatus.ONLINE);

                Validate.notNull(config);
                if (config.doPairing) {
                    try {
                        logger.info("Initiate pairing, RemoteName={}, PIN={}", config.remoteName, config.pairingPIN);
                        handlerFactory.pairDevice(this, config.remoteName, config.pairingPIN);
                    } catch (AppleTVException e) {
                        logger.info("FATAL: Pairing request failed!");
                    } finally {
                        configuration.remove("doPairing");
                        configuration.put("doPairing", "false");
                        this.updateConfiguration(configuration);
                    }
                }

                updatePlayStatus();
                logger.debug("Starting background status update");
                if (statusJob == null || statusJob.isCancelled()) {
                    statusJob = scheduler.scheduleWithFixedDelay(this::updatePlayStatus, 5, UPDATE_STATUS_INTERVAL,
                            TimeUnit.SECONDS);
                }

            } catch (AppleTVException e) {
                logger.error("Call to PyATV failed: {} ({})", e.getMessage(), e.getClass());
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Uneable to initialize thing: " + e.getMessage());
            }
        });
    }

    @Override
    public void handleConfigurationUpdate(Map<String, Object> configurationParameters) {
        super.handleConfigurationUpdate(configurationParameters);
        logger.info("Thing configuration updated");
    }

    @SuppressWarnings("null")
    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (getThing().getStatus() != ThingStatus.ONLINE) {
        }

        if (command instanceof RefreshType) {
            // TO-DO: handle data refresh
        } else {
            // command
            switch (channelUID.getIdWithoutGroup()) {
                case CHANNEL_REMOTE_KEY:
                    String keySequence = command.toString();
                    logger.info("Send key(s): {}", keySequence);
                    switch (keySequence) {
                        case KEY_MOVIES:
                            keySequence = config.keyMovie;
                            break;
                        case KEY_MUSIC:
                            keySequence = config.keyMusic;
                            break;
                        case KEY_TVSHOWS:
                            keySequence = config.keyTVShow;
                            break;
                    }
                    if (!keySequence.equals(command.toString())) {
                        logger.debug("Key '{}' mapped to key sequence '{}'", command.toString(), keySequence);
                    }
                    sendCommands(keySequence);
                    requestUpdates = UPDATE_SKIP_COUNT;
                    break;
                case CHANNEL_POSITION:
                    logger.info("Set position to {}", command.toString());
                    setPosition(command.toString());
                    break;
                case CHANNEL_TOTAL_PERCENT:
                    logger.info("Set position to {}%", command.toString());
                    setPosition(command.toString() + "%");
                    break;
                case CHANNEL_SHUFFLE:
                    logger.info("Set shuffle to {}", command.toString());
                    setShuffle(command.toString());
                    break;
                case CHANNEL_REPEAT_STATE:
                    logger.info("Set repeat to {}", command.toString());
                    setRepeat(command.toString());
                    break;
            }
            // request update on every set operation
            requestUpdates++;
        }
    }

    @SuppressWarnings("null")
    private boolean sendCommands(String commands) {
        return handlerFactory.sendCommands(commands, this, config.ipAddress, config.loginId);
    }

    /**
     * Device device status events
     *
     * @return
     */
    public void updatePlayStatus() {
        if ((requestUpdates > 0) || (skipUpdate++ % UPDATE_SKIP_COUNT == 0)) {
            logger.trace("Updating play status");
            // sendCommands("playing artwork_url");
            sendCommands(COMMAND_PLAYING);
            if (requestUpdates > 0) {
                --requestUpdates;
                logger.trace("{} more updates requested", requestUpdates);
            }
        } else {
            logger.trace("Update skipped {}/{}", (skipUpdate - 1) % UPDATE_SKIP_COUNT, UPDATE_SKIP_COUNT);
        }
    }

    /**
     * Call back for pyatv_api to pass status updates
     *
     * @param prop
     * @param value
     */
    @SuppressWarnings({ "null", "unused" })
    @Override
    public void statusEvent(String prop, String input) {
        String channel = channelMap.get(prop);
        if (channel != null) {
            logger.trace("PyATV.Update: {}={}", prop, input);
            String value = input;
            if (prop.equals(PLAYSTATUS_POSITION)) { // remember position for delta compution
                position = Long.parseLong(value);
                value = Sec2Time(value); // channel expects string
            }
            if (prop.equals(PLAYSTATUS_TTIME)) {
                totalTime = Long.parseLong(value);
                value = Sec2Time(value); // channel expects string
            }
            if (prop.equals(PLAYSTATUS_TPERCENT)) {
                float totalPercent = Float.parseFloat(value);
                value = String.format("%.0f%%", totalPercent * 100.0);
            }

            boolean updated = updateChannel(channel, prop, value);
            if (updated && prop.equals(PLAYSTATUS_MEDIA_TYPE) && !value.equals(MEDIA_TYPE_MUSIC)) {
                // reset album, artist, genre (only valid if Media == Music)
                logger.trace("Media type is != Music -> reset Album/Artist/Genre");
                updateChannel(CHANNEL_ALBUM, PLAYSTATUS_ALBUM, "");
                updateChannel(CHANNEL_ARTIST, PLAYSTATUS_ARTIST, "");
                updateChannel(CHANNEL_GENRE, PLAYSTATUS_GENRE, "");
            }
            if (updated && prop.equals(PLAYSTATUS_STATE)) {
                // if play mode changes request an update
                logger.trace("Playmode changed -> update");
                requestUpdates++;
            }
        } else {
            logger.debug("Event property '{}' with value '{}' for unknown channel", prop, input);
        }
    }

    @SuppressWarnings("null")
    private boolean updateChannel(String channel, String prop, String value) {
        Object current = playStatus.get(prop);
        if ((current != null) && !current.equals(value)) {
            logger.trace("Updating chanel {} with {}", channel, value);
            updateState(CHAN_GROUP_PLAYSTATUS + "#" + channel, new StringType(value));
            playStatus.replace(prop, value);
            return true;
        }
        return false;
    }

    /**
     * Compute new position, format:
     * hh:mmn:ss new offset, will be converted to sec
     * mm:ss short form, will be converted to sec (max position=59:59!)
     * <n> new position in sec
     * +<n> skip <n> sec forward
     * -<n> skip <n> sec backward
     * <n>% -> relative, based on total time
     *
     * New offset will be adjusted if > totalTime
     *
     * @param newPosition - new positon, could be absolute, relative or in %
     */
    boolean setPosition(String newPosition) {

        Long oldPosition = position;
        Long secPosition = -1l;
        if (StringUtils.isNumeric(newPosition)) {
            // specific position in seconds
            secPosition = Long.parseLong(newPosition);
        }
        if (newPosition.contains("+") || newPosition.contains("-")) {
            Long delta = time2Sec(newPosition);
            secPosition = position + delta; // do the math +/-
            if (secPosition < 0l) {
                secPosition = 0l; // adjust if before start
            }
            if ((totalTime != 0l) && secPosition > totalTime) {
                secPosition = totalTime; // adjust if behind end
            }
        }
        if (newPosition.contains("%")) {
            if (totalTime != 0l) {
                float percentage = Float.parseFloat(StringUtils.substringBefore(newPosition, "%"));
                secPosition = (long) ((float) totalTime * percentage / 100.0);
                logger.info("Set position to {}sec ({})", secPosition, newPosition);
            } else {
                logger.info("Can't set new position to {}, because total time is not given", newPosition);
            }
        } else {
            secPosition = time2Sec(newPosition);
        }

        if (secPosition == -1) {
            logger.info("WARNING: SetPosition with invalid format: {}", newPosition);
        }
        logger.debug("Old position={}s / {}, new position={}s / {}", oldPosition,
                String.format("%02d:%02d:%02d", secPosition / 3600, secPosition % 3600 / 60, secPosition % 3600 % 60),
                secPosition,
                String.format("%02d:%02d:%02d", secPosition / 3600, secPosition % 3600 / 60, secPosition % 3600 % 60));
        statusEvent(PLAYSTATUS_POSITION, secPosition.toString());
        requestUpdates++; // request refresh
        return sendCommands(COMMAND_SET_POSITION + "=" + Long.toString(secPosition));
    }

    boolean setShuffle(String newShuffle) {
        Integer shuffle = 0;
        if (StringUtils.isNumeric(newShuffle)) {
            shuffle = Integer.parseInt(newShuffle);
        } else {
            shuffle = newShuffle.toLowerCase().equals("true") ? 1 : 0;
        }
        return sendCommands(COMMAND_SET_SHUFFLE + "=" + shuffle.toString());
    }

    boolean setRepeat(String newRepeat) {
        return sendCommands(COMMAND_SET_REPEAT + "=" + newRepeat);
    }

    private String Sec2Time(String secStr) {
        long totalSecs = Long.parseLong(secStr);
        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private Long time2Sec(String time) {
        long hours = 0, minutes = 0, seconds = 0;
        if (time.contains(":")) {
            String[] units = time.split(":"); // will break the string up into an array
            if (units.length > 2) {
                // hh:mm:ss format
                hours = Integer.parseInt(units[0]);
                minutes = Integer.parseInt(units[1]);
                seconds = Integer.parseInt(units[2]);
            } else {
                // format mm:ss
                minutes = Integer.parseInt(units[0]);
                seconds = Integer.parseInt(units[1]);
            }
            return 3600 * hours + 60 * minutes + seconds;
        }
        return Long.parseLong(time);
    }

    @Override
    public void pairingResult(boolean result, String message) {
        logger.info("{}", message);
        if (!result) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, message);
        }
    }

    /**
     * This function will be called from the PyATV module to display an info message
     *
     * @param message
     */
    @Override
    public void info(String message) {
        logger.info("{}", message);
    }

    /**
     * This function will be called from the PyATV module to display a debug message
     *
     * @param message
     */
    @Override
    public void debug(String message) {
        logger.debug("{}", message);
    }

    @Override
    public void devicesDiscovered(String json) {
        logger.trace("Unexpected call to AppleTVHandler.devicesDiscovered()");
    }

    @Override
    public void generatedDeviceId(String id) {
        logger.trace("Unexpected call to AppleTVHandler.generatedDeviceId()");
    }

    private void initChannelMap() {
        channelMap.put(PLAYSTATUS_POSITION, CHANNEL_POSITION);
        channelMap.put(PLAYSTATUS_TTIME, CHANNEL_TOTAL_TIME);
        channelMap.put(PLAYSTATUS_TPERCENT, CHANNEL_TOTAL_PERCENT);
        channelMap.put(PLAYSTATUS_MEDIA_TYPE, CHANNEL_MEDIA_TYPE);
        channelMap.put(PLAYSTATUS_STATE, CHANNEL_PLAY_MODE);
        channelMap.put(PLAYSTATUS_REPEAT, CHANNEL_REPEAT_STATE);
        channelMap.put(PLAYSTATUS_SHUFFLE, CHANNEL_SHUFFLE);
        channelMap.put(PLAYSTATUS_ALBUM, CHANNEL_ALBUM);
        channelMap.put(PLAYSTATUS_ARTIST, CHANNEL_ARTIST);
        channelMap.put(PLAYSTATUS_GENRE, CHANNEL_GENRE);
        channelMap.put(PLAYSTATUS_TITLE, CHANNEL_TITLE);
        // channelMap.put(PLAYSTATUS_ARTWORK_URL, CHANNEL_ARTWORK_URL);
    }

    private void initPlayStatus() {
        position = 0l;
        totalTime = 0l;
        playStatus.put(PLAYSTATUS_POSITION, Sec2Time(position.toString()));
        playStatus.put(PLAYSTATUS_TTIME, Sec2Time(totalTime.toString()));
        playStatus.put(PLAYSTATUS_TPERCENT, "0%");
        playStatus.put(PLAYSTATUS_MEDIA_TYPE, MEDIA_TYPE_UNKNOWN);
        playStatus.put(PLAYSTATUS_STATE, PLAY_STATE_IDLE);
        playStatus.put(PLAYSTATUS_REPEAT, REPEAT_STATE_OFF);
        playStatus.put(PLAYSTATUS_SHUFFLE, SHUFFLE_STATE_OFF);
        playStatus.put(PLAYSTATUS_ALBUM, "");
        playStatus.put(PLAYSTATUS_ARTIST, "");
        playStatus.put(PLAYSTATUS_GENRE, "");
        playStatus.put(PLAYSTATUS_TITLE, "");
        playStatus.put(PLAYSTATUS_ARTWORK_URL, "");
    }

    @Override
    public void dispose() {
        if (statusJob != null) {
            statusJob.cancel(true);
        }
        super.dispose();
    }
}
