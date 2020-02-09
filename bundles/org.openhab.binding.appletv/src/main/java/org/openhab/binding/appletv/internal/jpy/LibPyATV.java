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
package org.openhab.binding.appletv.internal.jpy;

import static org.openhab.binding.appletv.internal.AppleTVBindingConstants.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.jpy.PyLib;
import org.jpy.PyModule;
import org.jpy.PyObject;
import org.openhab.binding.appletv.internal.AppleTVException;
import org.openhab.binding.appletv.internal.AppleTVHandlerFactory;
import org.openhab.binding.appletv.internal.handler.AppleTVHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LibPyATV} wraps the PyATV library
 *
 * @author Markus Michels - Initial contribution
 */
@NonNullByDefault
public class LibPyATV {
    private final Logger logger = LoggerFactory.getLogger(LibPyATV.class);

    @SuppressWarnings("null")
    @NonNullByDefault
    public interface PyATVProxy {
        PyObject init(AppleTVHandler handler);

        PyObject exec(@Nullable Object handler, String[] arg);
    }

    private @Nullable Path libPath;
    private @Nullable PyATVProxy pyATV;
    private Semaphore accessMutex = new Semaphore(1);

    private boolean started = false;

    public LibPyATV() {

    }

    @SuppressWarnings({ "null", "unused" })
    public LibPyATV(String currentLibPath) throws AppleTVException {

        try {
            logger.debug("Initialize PyATV (current installation path: '{}')", currentLibPath);

            // if ((currentLibPath == null) || currentLibPath.isEmpty()) {
            libPath = Files.createTempDirectory("ohlib-");
            logger.info("Modules will be installed in '{}'", libPath.toString());
            // } else {
            // libPath = Paths.get(currentLibPath);
            // }

            String os = System.getProperty("os.name").toLowerCase();
            String platform = System.getProperty("os.arch").toLowerCase();
            logger.debug("Platform info: '{}', architecture: '{}'", os, platform);

            List<String> cleanedExtraPaths = new ArrayList<>();
            cleanedExtraPaths.add(libPath.toString());

            String jpyPath = libPath.toString();
            String jpyLib = "";
            System.setProperty("jpy.debug", "true");
            if (os.contains("linux") || os.contains("mac")) {
                System.setProperty("jpy.pythonExecutable", "/usr/bin/python3");
                System.setProperty("jpy.pythonPrefix", "/usr");
            } else {
                throw new AppleTVException(
                        "OS '" + os + "' not supported yet, please contact the author and provde platform information");
            }

            if (os.contains("mac")) {
                System.setProperty("jpy.pythonLib",
                        // "/usr/local/Cellar/python/3.6.5/Frameworks/Python.framework/Versions/3.6/lib/libpython3.6.dylib");
                        "/Library/Frameworks/Python.framework/Versions/3.6/lib/libpython3.6.dylib");
                jpyLib = "lib/jpy/lib.macosx-x86_64-3.6";
                System.setProperty("jpy.jpyLib", jpyPath + "/jpy.so");
                System.setProperty("jpy.jdlLib", jpyPath + "/jdl.so");
            } else if (os.contains("linux") && platform.contains("arm")) { // Raspberry
                // System.setProperty("jpy.jpyLib", "jpy.cpython-35m-arm-linux-gnueabihf.so");
                // System.setProperty("jpy.jdlLib", "jdl.cpython-35m-arm-linux-gnueabihf.so");
                jpyLib = "lib/jpy/lib.linux-armv7l-3.5";
                System.setProperty("jpy.pythonLib", "/usr/lib/arm-linux-gnueabihf/libpython3.5m.so");
                // jpyLib = "lib/jpy/lib.linux-armv7l-3.6";
                // System.setProperty("jpy.pythonLib", "/usr/local/lib/libpython3.6m.so");
                System.setProperty("jpy.jpyLib", jpyPath + "/jpy.so");
                System.setProperty("jpy.jdlLib", jpyPath + "/jdl.so");
            } else if (os.contains("linux") && (platform.contains("x86_64") || platform.contains("amd64"))) { // Synology
                jpyLib = "lib/jpy/lib.synology-x86-64-3.5";
                System.setProperty("jpy.jpyLib", jpyPath + "/jpy.so");
                System.setProperty("jpy.jdlLib", jpyPath + "/jdl.so");
                System.setProperty("jpy.pythonLib", "/usr/lib/x86_64-linux-gnu/libpython3.5m.so");
            } else {
                throw new AppleTVException(
                        "Architecture not supported yet, please contact the author and provde platform information");
            }
            // cleanedExtraPaths.add("/usr/local/lib/python3.6/dist-packages");
            // cleanedExtraPaths.add("/usr/local/lib/python3.6/site-packages");
            cleanedExtraPaths.add("/usr/local/lib/python3.5/dist-packages");
            cleanedExtraPaths.add("/usr/local/lib/python3.5/site-packages");
            cleanedExtraPaths.add("/usr/lib/python3/dist-packages");
            cleanedExtraPaths.add("/usr/lib/python3/site-packages");

            List<String> extraPaths = new ArrayList<>();
            extraPaths.add("classpath:lib/pyatv.zip"); // PyATV library
            extraPaths.add("classpath:lib/jpy-0.10.0-SNAPSHOT.jar");
            if (!jpyLib.isEmpty()) {
                logger.debug("jpyLib={}", jpyLib);
                extraPaths.add("classpath:" + jpyLib + "/jpy.so"); // python_runtime_module
                extraPaths.add("classpath:" + jpyLib + "/jdl.so"); // python_runtime_module
                extraPaths.add("classpath:" + jpyLib + "/jpyutil.py"); // python util
            }

            logger.debug("jpy.pythonExecutable: {}", System.getProperty("jpy.pythonExecutable"));
            logger.debug("jpy.pythonLib: {}", System.getProperty("jpy.pythonLib"));
            logger.debug("jpy.jpyLib: {}", System.getProperty("jpy.jpyLib"));
            logger.debug("jpy.jdlLib: {}", System.getProperty("jpy.jdlLib"));
            logger.debug("jpy.pythonPrefix: {}", System.getProperty("jpy.pythonPrefix"));

            // Runtime.getRuntime()
            // .addShutdownHook(new Thread(() -> FileSystemUtils.deleteRecursively(tempDirectory.toFile())));
            extraPaths.forEach(lib -> {
                if (lib.startsWith("classpath:")) {
                    try {
                        String finalLib = lib.replace("classpath:", "");
                        String targetName = finalLib.contains("/") ? StringUtils.substringAfterLast(lib, "/")
                                : finalLib;
                        Path target = Paths.get(libPath.toString(), targetName);
                        try (InputStream stream = AppleTVHandler.class.getClassLoader().getResourceAsStream(finalLib)) {
                            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
                        }
                        if (finalLib.endsWith(".zip")) {
                            ZipUtils.extract(target.toFile(), libPath.toFile());
                            String zipPath = libPath + "/" + targetName.replaceAll(".zip", "");
                            cleanedExtraPaths.add(zipPath);
                        }
                    } catch (IOException e) {
                        logger.error("Unable to install PyATV library: {} ({})", e.getMessage(), e.getClass());
                    }
                } else {
                    cleanedExtraPaths.add(lib);
                }
            });

            if (!PyLib.isPythonRunning()) {
                logger.debug("Starting Python");
                PyLib.startPython(cleanedExtraPaths.toArray(new String[] {}));
                started = true;
            }

            // Proxify the call to a python class
            logger.info("load PyATV");
            PyModule pyModule = PyModule.importModule("pyatv_api");
            PyObject pyObject = pyModule.call("PyATV");
            pyATV = pyObject.createProxy(PyATVProxy.class);
            if (pyATV == null) {
                throw new AppleTVException("Unable to initialize PyATV access");
            }
        } catch (IOException e) {
            logger.error("Unable to start Python (jpy): {} ({})", e.getMessage(), e.getClass());
        }
    }

    public void init(AppleTVHandler thingHandler) {
        boolean acquired = false;
        try {
            acquired = accessMutex.tryAcquire(PYATV_ACCESS_TIMEOUT, TimeUnit.SECONDS);
            Validate.notNull(pyATV);
            pyATV.init(thingHandler);

        } catch (RuntimeException | InterruptedException e) {
            logger.error("Unable to init PyATV: {} ({})", e.getMessage(), e.getClass());
        } finally {
            if (acquired) {
                accessMutex.release();
            }
        }
    }

    /**
     * Send one or more commands to the PyATV module.
     *
     * @param commands command sequence seperated by ' '
     * @param ipAddress IP address of the Apple-TV
     * @param loginId Login ID resulting from device pairing
     * @return true: successful, false: failed, e.g. exception in the PyATV module
     */
    @SuppressWarnings("null")
    public boolean sendCommands(String commands, @Nullable Object handler, String ipAddress, String loginId,
            @Nullable String cmdArgs[]) {

        boolean acquired = false;
        try {
            logger.trace("Sending command {} to ip {}, lid {}", commands, ipAddress, loginId);

            String[] args = new String[20];
            // PyObject res = plugIn.exec("--address 192.168.x.y --login_id 0xXXXXXXXXXXXXXXXX top_menu");
            int a = 0;
            // args[a++] = "--debug";
            // args[a++] = "--verbose";
            if (!ipAddress.isEmpty()) {
                args[a++] = "--address";
                args[a++] = ipAddress;
            }
            if (!loginId.isEmpty()) {
                args[a++] = "--login_id";
                args[a++] = loginId;
            }
            if (cmdArgs != null) {
                for (String s : cmdArgs) {
                    args[a++] = s;
                }
            }

            StringTokenizer tokenizer = new StringTokenizer(commands, " ");
            while (tokenizer.hasMoreElements()) {
                args[a++] = tokenizer.nextToken();
            }

            acquired = accessMutex.tryAcquire(PYATV_ACCESS_TIMEOUT, TimeUnit.SECONDS);
            return pyATV.exec(handler, args).getIntValue() == 0;
        } catch (Exception e) {
            logger.error("Exception on PyATV call: {} ({})", e.getMessage(), e.getClass());
            return false;
        } finally {
            if (acquired) {
                accessMutex.release();
            }
        }
    }

    /**
     * Calls the PyATV module to scan for devices.
     * Once successful the PyATV module will call devicesDiscovered() to pass the resulting device json
     *
     * @return Device list in JSON format
     */
    public String scanDevices(AppleTVHandlerFactory handlerFactory) {
        boolean acquired = false;
        try {
            logger.info("Scan for AppleTV devices");
            /*
             * String[] args = new String[10];
             * int a = 0;
             * args[a++] = "--debug";
             * args[a++] = "scan";
             * // the excec(scan) call will do a callback and fills jsonDevices before returning
             * acquired = accessMutex.tryAcquire(PYATV_ACCESS_TIMEOUT, TimeUnit.SECONDS);
             * if (pyATV.exec(handlerFactory, args).getIntValue() == 0) {
             * return "";
             * }
             */
            String args[] = new String[1];
            if (!sendCommands(COMMAND_SCAN, handlerFactory, "", "", args)) {
                logger.error("Scanning for Apple-TV devices failed!");
            }
        } catch (Exception e) {
            logger.error("Exception device scan: {} ({})", e.getMessage(), e.getClass());
        } finally {
            if (acquired) {
                accessMutex.release();
            }
        }
        return "";
    }

    /**
     * Pair openHAB remote with an Apple-TV device
     *
     * @param handler - AppleTVHandler instance
     * @param remoteName - Name of the remote, which is display in the Appe-TV settings to select the corect remote for
     *            pairing
     * @param pairingPIN - PIN code for pairing. Will be requested by OH and must be entered on the TV screen
     *
     * @return true: successful, false: failed
     *
     * @throws AppleTVException
     */
    public boolean pairDevice(AppleTVHandler handler, String remoteName, String pairingPIN) throws AppleTVException {
        String args[] = new String[4];
        int a = 0;
        if (!remoteName.isEmpty()) {
            args[a++] = "--remote-name";
            args[a++] = remoteName;
        }
        if (!pairingPIN.isEmpty()) {
            args[a++] = "--pin";
            args[a++] = pairingPIN;
        }
        return sendCommands(COMMAND_PAIR, handler, "", "", args);
    }

    public String getLibPath() {
        Validate.notNull(libPath);
        return libPath.toString();
    }

    void dispose() {
        if (started) {
            PyLib.Diag.setFlags(PyLib.Diag.F_OFF);
            PyLib.stopPython();
        }
    }
}
