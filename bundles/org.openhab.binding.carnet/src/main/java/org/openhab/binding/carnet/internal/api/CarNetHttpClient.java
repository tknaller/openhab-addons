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
package org.openhab.binding.carnet.internal.api;

import static org.openhab.binding.carnet.internal.api.CarNetApiConstants.UTF_8;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;

/**
 * The {@link CarNetHttpClient} implements http client functions
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class CarNetHttpClient {

    public static void fillHttpHeaders(Request request, Map<String, String> headers, String token) {
        if (!headers.isEmpty()) {
            for (Map.Entry<String, String> h : headers.entrySet()) {
                String key = h.getKey();
                String value = h.getValue();
                if (key.equals(HttpHeader.USER_AGENT.toString())) {
                    request.agent(value);
                } else if (key.equals(HttpHeader.ACCEPT.toString())) {
                    request.accept(value);
                } else {
                    if (!value.isEmpty()) {
                        request.header(key, value);
                    }
                }
            }
        }
    }

    public static String getUrlParm(String input, String parameter) {
        String pattern = "&" + parameter + "=";
        if (input.contains(pattern)) {
            String res = StringUtils.substringAfter(input, pattern);
            return res.contains("&") ? StringUtils.substringBefore(res, "&") : res;
        }
        return "";
    }

    public static long parseDate(String timestamp) {
        ZonedDateTime zdt = ZonedDateTime.parse(timestamp, DateTimeFormatter.RFC_1123_DATE_TIME);
        return zdt.toInstant().toEpochMilli() * 1000; // return ms
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, UTF_8);
        } catch (UnsupportedEncodingException e) {
            return s;
        }
    }

    public static String generateNonce() {
        String dateTimeString = Long.toString(new Date().getTime());
        byte[] nonceBytes = dateTimeString.getBytes();
        return Base64.getEncoder().encodeToString(nonceBytes);
    }
}
