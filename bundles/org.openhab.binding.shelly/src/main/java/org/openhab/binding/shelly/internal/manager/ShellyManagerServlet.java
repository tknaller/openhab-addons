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
package org.openhab.binding.shelly.internal.manager;

import static org.openhab.binding.shelly.internal.manager.ShellyManagerConstants.*;
import static org.openhab.binding.shelly.internal.util.ShellyUtils.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.io.net.http.HttpClientFactory;
import org.openhab.binding.shelly.internal.ShellyHandlerFactory;
import org.openhab.binding.shelly.internal.api.ShellyApiException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ShellyManagerServlet} implements the Shelly Manager - a simple device overview/management
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
@Component(service = HttpServlet.class, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ShellyManagerServlet extends HttpServlet {
    private static final long serialVersionUID = 1393403713585449126L;
    private final Logger logger = LoggerFactory.getLogger(ShellyManagerServlet.class);

    private static final String SERVLET_URI = SHELLY_MANAGER_URI;
    private final ShellyManager manager;
    private final String className;

    private final HttpService httpService;

    @Activate
    public ShellyManagerServlet(@Reference ConfigurationAdmin configurationAdmin, @Reference HttpService httpService,
            @Reference HttpClientFactory httpClientFactory, @Reference ShellyHandlerFactory handlerFactory,
            Map<String, Object> config) {
        className = substringAfterLast(getClass().toString(), ".");
        this.httpService = httpService;
        this.manager = new ShellyManager(configurationAdmin, httpClientFactory.getCommonHttpClient(),
                handlerFactory.getThingHandlers());
        try {
            httpService.registerServlet(SERVLET_URI, this, null, httpService.createDefaultHttpContext());
            logger.debug("{} started at '{}'", className, SERVLET_URI);
        } catch (NamespaceException | ServletException | IllegalArgumentException e) {
            logger.warn("Could not start {}", className, e);
        }
    }

    @Deactivate
    protected void deactivate() {
        httpService.unregister(SERVLET_URI);
        logger.debug("{} stopped", className);
    }

    @Override
    protected void service(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response)
            throws ServletException, IOException, IllegalArgumentException {
        if ((request == null) || (response == null)) {
            logger.debug("request or resp must not be null!");
            return;
        }

        PrintWriter out = response.getWriter();
        String path = getString(request.getRequestURI()).toLowerCase();
        String ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        try {
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            Map<String, String[]> parameters = request.getParameterMap();
            logger.info("{}: {} Request from {}:{}{}?{}", className, request.getProtocol(), ipAddress,
                    request.getRemotePort(), path, parameters.toString());
            if (!path.toLowerCase().startsWith(SERVLET_URI)) {
                logger.warn("{} received unknown request: path = {}", className, path);
                return;
            }

            // Make sure it's UTF-8 encoded
            String result = manager.generateContent(path, parameters);
            // result = new String(result.getBytes(StandardCharsets.ISO_8859_1), UTF_8);
            out.write(result);
        } catch (ShellyApiException e) {
            out.write("Exception:<br>" + e.toString());
        } catch (RuntimeException e) {
            logger.debug("{}: Exception uri={}, parameters={}", className, path, request.getParameterMap().toString(),
                    e);
        } finally {
            response.setContentType("text/html");
            response.setCharacterEncoding(UTF_8);
            out.close();
        }
    }
}
