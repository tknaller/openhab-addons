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

import java.util.Collections;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link AppleTVBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class AppleTVBindingConstants {

    private static final String BINDING_ID = "appletv";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_APPLETV = new ThingTypeUID(BINDING_ID, "device");
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_APPLETV);

    // Thing properties
    public static final String PROPERTY_ID = "deviceId";
    public static final String PROPERTY_IP = "ipAddress";
    public static final String PROPERTY_LOGIN_ID = "loginId";

    // List of all Channel ids
    public static final String CHGROUP_CONTROL = "control";
    public static final String CHGROUP_STATUS = "playStatus";
    public static final String CHGROUP_MEDIA = "mediaInformation";

    public static final String CHANNEL_REMOTE_KEY = "remoteKey";

    public static final String CHAN_GROUP_PLAYSTATUS = "playStatus";
    public static final String CHANNEL_PLAY_MODE = "playMode";
    public static final String CHANNEL_MEDIA_TYPE = "mediaType";
    public static final String CHANNEL_TITLE = "title";
    public static final String CHANNEL_ALBUM = "album";
    public static final String CHANNEL_ARTIST = "artist";
    public static final String CHANNEL_GENRE = "genre";
    public static final String CHANNEL_ARTWORK_URL = "artworkUrl";
    public static final String CHANNEL_POSITION = "position";
    public static final String CHANNEL_TOTAL_TIME = "totalTime";
    public static final String CHANNEL_TOTAL_PERCENT = "totalPercent";
    public static final String CHANNEL_REPEAT_STATE = "repeat";
    public static final String CHANNEL_SHUFFLE = "shuffle";

    // Special keys, will be mapped to the configured key sequences
    public static final String KEY_MOVIES = "movies";
    public static final String KEY_MUSIC = "music";
    public static final String KEY_TVSHOWS = "tvshows";

    // PyATV status events
    public static final String PLAYSTATUS_STATE = "play_state";
    public static final String PLAYSTATUS_MEDIA_TYPE = "media_type";
    public static final String PLAYSTATUS_TITLE = "title";
    public static final String PLAYSTATUS_ALBUM = "album";
    public static final String PLAYSTATUS_ARTIST = "artist";
    public static final String PLAYSTATUS_GENRE = "genre";
    public static final String PLAYSTATUS_ARTWORK_URL = "artwork_url";
    public static final String PLAYSTATUS_POSITION = "position";
    public static final String PLAYSTATUS_TTIME = "total_time";
    public static final String PLAYSTATUS_TPERCENT = "total_percent";
    public static final String PLAYSTATUS_REPEAT = "repeat";
    public static final String PLAYSTATUS_SHUFFLE = "shuffle";
    public static final String EVENT_PAIRING_GUID = "pairing_guid";

    // Media types
    public static final String MEDIA_TYPE_UNKNOWN = "Unknown";
    public static final String MEDIA_TYPE_VIDEO = "Video";
    public static final String MEDIA_TYPE_MUSIC = "Music";
    public static final String MEDIA_TYPE_TV = "TV";
    public static final String MEDIA_TYPE_OTHER = "Unsupported";

    // Play state
    public static final String PLAY_STATE_IDLE = "Idle";
    public static final String PLAY_STATE_NO_MEDIA = "No media";
    public static final String PLAY_STATE_LOADING = "Loading";
    public static final String PLAY_STATE_PAUSED = "Paused";
    public static final String PLAY_STATE_PLAYING = "Playing";
    public static final String PLAY_STATE_FAST_FORWARD = "Fast forward";
    public static final String PLAY_STATE_FAST_BACKWARD = "Fast backward";
    public static final String PLAY_STATE_OTHER = "Unsupported";

    // Repeat state
    public static final String REPEAT_STATE_OFF = "Off";
    public static final String REPEAT_STATE_TRACK = "Track";
    public static final String REPEAT_STATE_ALL = "All";

    // Shuffle mode
    public static final String SHUFFLE_STATE_ON = "True";
    public static final String SHUFFLE_STATE_OFF = "False";

    // atvremote commands
    public static final String COMMAND_SCAN = "scan";
    public static final String COMMAND_AUTH = "auth";
    public static final String COMMAND_PAIR = "pair";
    public static final String COMMAND_DEVICE_ID = "device_id";
    public static final String COMMAND_PLAYING = "playing";
    public static final String COMMAND_ARTWORK = "artwork";
    public static final String COMMAND_ARTWORK_URL = "artwork_url";
    public static final String COMMAND_SET_POSITION = "set_position";
    public static final String COMMAND_SET_REPEAT = "set_repeat";
    public static final String COMMAND_SET_SHUFFLE = "set_shuffle";
    public static final String COMMAND_HASH = "hash";

    public static final int UPDATE_STATUS_INTERVAL = 3; // check for updates every x sec
    public static final int UPDATE_SKIP_COUNT = 5; // update every x triggers or when a key was pressed
    public static final int PYATV_ACCESS_TIMEOUT = 10; // timeout for claiming the pyatv library access
}
