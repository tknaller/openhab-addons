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
package org.openhab.binding.bluetooth.bluegiga.internal.command.attributeclient;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.bluetooth.bluegiga.internal.BlueGigaDeviceCommand;

/**
 * Class to implement the BlueGiga command <b>executeWrite</b>.
 * <p>
 * This command can be used to execute or cancel a previously queued prepare_write command on a
 * remote device
 * <p>
 * This class provides methods for processing BlueGiga API commands.
 * <p>
 * Note that this code is autogenerated. Manual changes may be overwritten.
 *
 * @author Chris Jackson - Initial contribution of Java code generator
 */
@NonNullByDefault
public class BlueGigaExecuteWriteCommand extends BlueGigaDeviceCommand {
    public static int COMMAND_CLASS = 0x04;
    public static int COMMAND_METHOD = 0x0A;

    /**
     * 1: commits queued writes, 0: cancels queued writes
     * <p>
     * BlueGiga API type is <i>uint8</i> - Java type is {@link int}
     */
    private int commit;

    /**
     * 1: commits queued writes, 0: cancels queued writes
     *
     * @param commit the commit to set as {@link int}
     */
    public void setCommit(int commit) {
        this.commit = commit;
    }

    @Override
    public int[] serialize() {
        // Serialize the header
        serializeHeader(COMMAND_CLASS, COMMAND_METHOD);

        // Serialize the fields
        serializeUInt8(connection);
        serializeUInt8(commit);

        return getPayload();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BlueGigaExecuteWriteCommand [connection=");
        builder.append(connection);
        builder.append(", commit=");
        builder.append(commit);
        builder.append(']');
        return builder.toString();
    }
}
