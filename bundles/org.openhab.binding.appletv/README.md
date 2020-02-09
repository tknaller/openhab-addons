# Apple-TVBinding (org.openhab.binding.appletv)

This openHAB 2 Binding implements control for the Apple-TV devices. This includes sending remote keys (control the Apple-TV from openHAB). An upcoming version will also process the status updates to provide information about the media being played.

Author: Markus Michels (markus7017)
Check  https://community.openhab.org/t/binding-for-apple-tv for more information, questions and contributing ideas. Any comment is welcome!

Release: alpha2, check master branch for stable release

---

Known issues:

* Binding configuration and device pairing needs to be implemented
* Support for Synology NAS (amd64) is not verified
* Not yet very well tested with more than one Apple-TV (should work, may cause timing issues)
* The binding copies the embedded pyatv modules to a temp folder on each startup, this will be solved with upcoming binding configuration. 
* The Python module contains some output to stdout/debug, this needs to be optimzed

Please note:
This is a beta release, it has bugs, requires manual install etc. Questions, feedback and contributions are welcome (e.g. improving this documentation).

Looking for contribution: If you are familar with HTML and CSS you are welcome to contribute a nice HABpanel widget. ;-)

---

## Supported Devices, Platforms

Devices

* Apple-TV 4 - fully supported, verified by community
* Apple-TV 3 - latest firmware - fully supported, development environment
* Apple-TV 2 - no information, won't expect to work

Platforms

* macOS - dev environment is Mojave, but should also work with Sierra and High Sierra
* Raspberyy with OpenHabian - default test environment
* Ubuntu 18.04 - verified by the community
* Synology NAS - supported running a virual environment, native support not yet verified
* Win32 - only on request and with support of the user, because I know there are relevant differences in running Python on Win32 -> contact author
* others currently not supported - contact author

## Supported Things

|Thing  |Type                          |
|-------|------------------------------|
|device |Represents an Apple-TV device |

# Discovery

The binding supports auto discovery of Apple-TVs on the local network. Once the Apple-TV (using atvremote) the binding could discover the device. Integration of pairing functionality is planned for an upcoming release.
For now you need to pair your device first (using atvremote cli) before performing auto discovery in Paper UI.

* make sure all required packages have been installed
* run "atvremote scan" - this will show you the ip address of your Apple TV device
* run "atvremote pair -r openHAB -  this will initialte the pairing process
* On your Appe TV screen go to Settings->General->Remotes
* you should see the openHAB remote - if not you need to restart the Apple TV
* select the "openHAB" entry - the Apple TV requests a pairing code
* enter "1234" - the process should be completed without an error
* you could terminate the "atvremote pair" command with [Return]
* you should see the login id, which will be required for the thing configuration 

Proceed with Thing Configuration below.

## Binding installation

For now the bindinng is not available on the Eclipse Smart Home Market Place nor part of the openHAB distribution so you need to install it manually.

As described the binding integrates the Phyton-based PyATV project so you need to install Python 3.6.5 and the required modules:

* Platform software packages:
sudo apt-get update
sudo apt-get install python3.6 python3-pip libpython3.6 python3-jpy
sudo apt-get install avahi-utils
Python 3.6 for macOS can be found here: https://www.python.org/downloads/mac-osx/

* Python modules:
sudo python3.6 -m pip install pyatv zeroconf sh
On macOS use Homebrew to install the Python 3.6(!) and additional modules.
Make sure those modules go into the Python 3.6 folders. if you have multiple versions installed (by using the pip3.5/pip3.6 command).

* Verification
You should verify the installation before installing/configuring the binding:
atvremote --address <ip address>  --login_id <login id from pairing>  top_menu
should work without error messages and move the focus on the Apple-TV to the top menu.

* The binding itself

Copy the binding jar to openHAB's addons folder, add the thing in Paper UI (see below) and restart openHAB.

## Binding Configuration

There are no textual configuration files.

## Thing Configuration

Before adding the thing make sure that all pre-requisites are met and all modules have been installed (see above).

You could use Paper UI to run auto discvery from the Inbox or to add a thing manually

* Go to Configuration->Things and click on '+'
* Select Apple TV Binding
* fill in the device's ip address and login id as discovered through the pairing process
* Once you save the configuration the thing should become online

## Channels

|Group      | Channel   |Type                                                                              |
|-----------|-----------|----------------------------------------------------------------------------------|
|control    | remoteKey |Send a key or key sequence to the Apple-TV, see below for valid keys              |
|playStatus | playMode  |Current play mode: No Media/Idle/Loading/Playing/Paused/Fast Forward/Fast Backward|
|           | mediaType |Media type being played: None/Music/Video/TV/Unknown                              |
|           | title     |Title of current media.                                                           |
|           | artist    |Artist - only for Media Music                                                     |
|           | album     |Album - only for Media Music                                                      |
|           | genre     |Genre - only for Media Music                                                      |
|           | position  |Position within the media. While playing the position gets updated in intervals.  |
|           |           |The position could be changed, send the following format to the channel.          |
|           |           |+<n>: Move forward, e.g. +10 moves 10sec forward;  +5:00 moves 5min forward.      |
|           |           |-<n>: Move reverse, e.g. -10 moves 10sec backward; +7:00 moves 7min backward.     |
|           |           |<n>%: Move relativ of the total duration (0%=start...100%=end).                   |
|           | totalTime |Total time/duration of the media currently plaing. Note: could be 00:00:00!       |
|           | shuffle   |Music Shuffle Mode - True: shuffeling, False: no shuffeling                       |
|           | repeat    |Music Repeat  Mode - Off: no repeat, Track: repeat track, All: repeat playlis     |

## Keys

The following keys could ne semd with channel remoteKey

 - up - Press key up
 - down - Press key down
 - left - Press key left
 - right - Press key right
 - select - Press key select
 - play - Press key play
 - pause - Press key play
 - next - Press key next
 - previous - Press key previous
 - stop - Press key stop
 - top_menu - Go to main menu (long press menu)
 - menu - Press key menu

You could also send a key sequence, e.g. "top_menu up up left left select"

There are special keys, which will be mapped into a key sequence:

- movies - go to the Movies selection 
- tvshows - go to the TV Show selection
- music - go to the Music selection

Those keys will be mapped to a sequence of keys to navigate within the Apple-TV's menu. The sequences send for each of them can be configured in the thing settings if the defaults don't work for your setup. This depends on your personal menu layout. The sequence must match the order of the menu items on the main menu.

## Full Example

Note: PaperUI is recommended, if you want to use text files make sure to replace the thing id from you channel definition 

* .things

* .items

String Atv_Remote "ATV [%s]" {channel="appletv:device:34fc39d8:control#remoteKey"}

* .sitemap

Switch item=Atv_Remote mappings=[up = "^" ]
Switch item=Atv_Remote mappings=[left = "<", select = "Sel", right = ">" ]
Switch item=Atv_Remote mappings=[menu = "Menu", down = "  v   ", play = "Play" ]
Switch item=Atv_Remote mappings=[previous='Prev', pause='Pause', next='Next']

* .rules

// wakeup the Apple-TV
sendCommand(Atv_Remote, "top_menu")

## Notes

It integrates the PyATV Python library, which implements the protocol layer. The binding includes also platform specific stuff (jpy Java/Phyton bridge). All modules are included in the binding package and the binding tries to auto-select them. An upcoming version will allow to overwrite this auto-detection in case something went wrong or you have specific installation requirements.

Thanks postlund for his great work in contributing the PyATV library (https://github.com/postlund/pyatv) and the jpy team (https://github.com/bcdev/jpy).
