# Shelly Binding (org.openhab.binding.shelly)

This Binding implements control for the Shelly series of devices.
This includes sending commands to the devices as well as reading the device status and sensor data.

Author: Markus Michels (markus7017)
Check  https://community.openhab.org/t/shelly-binding/ for more information, questions and contributing ideas. Any comments are welcome!

Also check section **Additional Information** at the end of the document.
This includes some general comments, information how to debug and request new features.

Please let me know if you have problems installing the new build or this doc can be improved.

---

DISCLAIMER: Please be ware, installing a SNAPSHOT or DEV build might impact the stability of your installation and cause backward compatibility issues. Do a full backup of your installation any time you do an update.

--- 

## General Notes

**openHAB 2.5.x**: Version 2.5.x of the distribution doesn't receive updates anymore. You can continue to use the Shelly binding, but you have to switch to the DEV build.

**openHAB 3.x**: OH 3.x is fully supported. 

Be ware that there are
- the release distro package, which includes a stable version of the binding
- the official SNAPSHOT release / milestone builds, which might include a newer binding version, but will never be as current as the DEV build
- the DEV build: Latest features and bug fixes (from the myfiles repo)

If you want to use the SNAPSHOT/DEV build you can **NOT** install it using the UI. 
Make sure that the release version is not installed: Check that Shelly is not installed, uninstall if so. 

There is no strict dependency on the OH base version, e.g. you could run binding 3.1-SNAPSNOT on top of openHAB 3.0.1.

Use device firmware version 1.9.2 or newer - you could use device UI or Shelly Manager to perform and update.

## Installing DEV build

Download the following files

**Californium 2.0 libraries**
You should skip the following 2 steps if you already have installed the Tradfi binding
- [californium-core-2.0.0.jar from myfiles repo](https://github.com/markus7017/myfiles/blob/master/shelly/californium-core-2.0.0.jar?raw=true)
- [element-connector-2.0.0.jar from myfiles repo](https://github.com/markus7017/myfiles/blob/master/shelly/element-connector-2.0.0.jar?raw=true)

**Shelly Binding**

- Download the jar from my myfiles repo on GitHub [2.5.13-SNAPSHOT](https://github.com/markus7017/myfiles/blob/master/shelly/org.openhab.binding.shelly-2.5.13-SNAPSHOT.jar?raw=true)
or [3.1-SNAPSHOT](https://github.com/markus7017/myfiles/blob/master/shelly/org.openhab.binding.shelly-3.1.0-SNAPSHOT.jar?raw=true) .

### Installation

### New installation
- Stop OH
- copy californium-core-2.0.0.jar to OH's addons folder
- copy element-connector-2.0.0.jar to OH's addons folder
- Start OH, wait until fully initialized(!)
- copy org.openhab.binding.shelly-xxx-SNAPSHOT.jar to OH's addons folder

If everything was install correct a "bundle:list" output show be similar to this:

```
246 │ Installed │  80 │ 2.0.0                  │ Californium (Cf) Core
247 │ Installed │  80 │ 2.0.0                  │ Californium (Cf) Element Connector
248 │ Installed │  80 │ 2.5.12.202012150845    │ openHAB Add-ons :: Bundles :: Shelly Binding
```
## Updating DEV version

Channel definitions are subject to change with any alpha or beta release. Please make sure to **delete all Shelly things before updating*** the binding and clean out the JSON DB:

- **remove all Shelly things** (UI and .things)
- delete the existing binding jar from the addons folder, wait until OH unloaded the binding (check the OH log)
- run "bundle:list | grep Shelly" and make sure that the binding is gone
- otherwise run "bundle:uninstall <bundle id as listed from above (1st column)>"
- stop OH ("openhab-cli stop"), wait until everything is stopped (could take some time)
- copy binding jar into addons (set correct permission)
- start oh service ("openhab-cli start")
- **re-discover things**
- the channel/item linkage should be restored automatically
  verify the linked channels, maybe the are new ones and in rare cases I rename channels for consistency.

If you hit a problem make sure to post a TRACE log (or send PM) so I could look into the details.

## Additional Notes

### Reporting a problem/bug

If you encounter a problem you could put the binding into DEBUG or TRACE mode.
Check the [community thread](https://community.openhab.org/t/shelly-binding/) first - maybe others already solved the problem:.

- open OH console (execute "openhab-cli console")
- set the bindings's log level: "log:set DEBUG org.openhab.binding.shelly" or "log:set TRACE org.openhab.binding.shelly" for even more details
- issue command or wait until problem occurs
- save openhab.log
- remove all stuff not related to the problem
- anomynize all specific data of your installation (e.g. credentials for password protected devices, SSID)

Create a new Issue here: https://github.com/openhab/openhab-addons/issues
Use [shelly] as prefix for the subject, add a specific title and exact description of the problem.
You might want to send me a PM to give me a heads-up that there is a new Issue, include the link

### Feature Request

Any comment or feature request is welcome. Post the idea to the community thread, all of us will benefit.
Create a new Issue here: https://github.com/openhab/openhab-addons/issues
Use [shelly] as prefix for the subject, add a specific title and exact description of the request.
You might want to send me a PM to give me a heads-up that there is a new Issue, include the link

## Other devices

Check the README.md for supported devices.
If you have one of those devices send me a PM and we could work on the implementation/testing.

## Supporting new devices

You could help to integrate and support new devices. In general the following information is a good start

- open a browser and issue the following urls
- http://&lt;device ip&gt;/settings
- http://&lt;device ip&gt;/status

once basic discovery is implemented the coapDiscription in the Thing properties is required

- enable CoIoT events within the thing configuration
- open the thing properties ([Show Properties])
- and copy&amp;paste the coapDescr property

Create a new Issue here: https://github.com/openhab/openhab-addons/issues
Use [shelly] as prefix for the subject, add a specific title and exact description of the request.
You might want to send me a PM to give me a heads-up that there is a new Issue, include the link

