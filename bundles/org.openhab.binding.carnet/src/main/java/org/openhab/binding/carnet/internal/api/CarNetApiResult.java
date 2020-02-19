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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NonNullByDefault
public class CarNetApiResult {
    private final Logger logger = LoggerFactory.getLogger(CarNetApiResult.class);

    public String url = "";
    public String method = "";
    public Integer resultCode = 0;
    public String result = "";
    public String response = "";

    public CarNetApiResult() {
    }

    public CarNetApiResult(String url, String method) {
        this.method = method;
        this.url = url;
    }

    public CarNetApiResult(String url, String method, Integer responseCode, String response) {
        this.method = method;
        this.url = url;
        this.resultCode = responseCode;
        this.response = response;
    }

    @SuppressWarnings("null")
    public CarNetApiResult(@Nullable ContentResponse contentResponse) {
        if (contentResponse != null) {
            resultCode = contentResponse.getStatus();
            result = contentResponse.getReason();
            response = contentResponse.getContentAsString().trim();

            Request request = contentResponse.getRequest();
            if (request != null) {
                url = request.getURI().toString();
                method = request.getMethod();
            }
        }
    }

    public CarNetApiResult(@Nullable ContentResponse contentResponse, Throwable e) {
        response = e.toString();
        if (contentResponse != null) {
            resultCode = contentResponse.getStatus();
            result = contentResponse.getReason();

            Request request = contentResponse.getRequest();
            if (request != null) {
                url = request.getURI().toString();
                method = request.getMethod();
            }
        }
    }

    public CarNetApiResult(@Nullable Request request, Throwable e) {
        response = e.toString();
        if (request != null) {
            url = request.getURI().toString();
            method = request.getMethod();
        }
    }

    public CarNetApiResult get() {
        return this;
    }
}
