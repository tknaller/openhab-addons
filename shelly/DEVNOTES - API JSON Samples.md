# REST API JSON Sample Data

## Shelly1

### Shelly1: /settings
{"device":{"type":"SHSW-1","mac":"CC50E325A73E","hostname":"shelly1-25A73E","num_outputs":1},"wifi_ap":{"enabled":false,"ssid":"shelly1-25A73E","key":""},"wifi_sta":{"enabled":true,"ssid":"ShellyIoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},"mqtt": {"enable":false,"server":"192.168.6.7:1883","user":"openhab","id":"shelly1-25A73E","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},"coiot": {"update_period":15},"sntp":{"server":"time.google.com","enabled":true},"login":{"enabled":false,"unprotected":false,"username":"openhab","password":"bluebird"},"pin_code":"","name":"","fw":"20200320-123430/v1.6.2@514044b4","factory_reset_from_switch":true,"discoverable":true,"build_info":{"build_id":"20200320-123430/v1.6.2@514044b4","build_timestamp":"2020-03-20T12:34:30Z","build_version":"1.0"},"cloud":{"enabled":false,"connected":false},"timezone":"Europe/Berlin","lat":50.110901,"lng":8.682130,"tzautodetect":true,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":true,"time":"07:29","unixtime":1592897395,"hwinfo":{"hw_revision":"prod-2018-08", "batch_id":2},"mode" :"relay","longpush_time":800,"relays":[{"name":null,"ison":false,"has_timer":false,"default_state":"off","btn_type":"edge","btn_reverse":1,"auto_on":0.00,"auto_off":0.00,"power":0.00,"btn_on_url":null,"btn_off_url":null,"out_on_url":null,"out_off_url":null,"longpush_url":null,"shortpush_url":null,"schedule":false,"schedule_rules":[]}],"ext_sensors":{},"ext_temperature":[],"ext_humidity":[]}


### Shelly1: /status/relay/0

```
{
	"wifi_sta":{	"connected":true,"ssid":"iot-wlan","ip":"10.0.0.1","rssi":-70},
	"cloud":{	"enabled":false, "connected":false},
	"mqtt":{	"connected":true}, "time":"15:36","serial":1,"has_update":false,"mac":"XXXXXXXXXXXX", "relays" :[
		{	"ison":false,"has_timer":false}],
	"meters":[
		{	"power":0.00,"is_valid":"true"}
	],
	"update":{	"status":"idle","has_update":false,"new_version":"20190711-084053/v1.5.0-hotfix4@3b4f7414","old_version":"20190711-084053/v1.5.0-hotfix4@3b4f7414"},
	"ram_total":51104,"ram_free":40356,"fs_size":233681,"fs_free":175951,"uptime":3455
}
```

### COAP Device Description Shelly1 (fw 1.6)

```
{
	"blk": [
		{	"I": 0, "D": "Relay0"},
		{	"I": 1, "D": "Sensors"} ],
	"sen": [
		{	"I": 111, "T": "P", "D": "Power","R": "0/3500","L": 0},
		{	"I": 112,"T": "S","D": "Switch","R": "0/1","L": 0},
		{	"I": 113,"T": "tC","D": "Temperature C","R": "-40/300","L": 0},
		{	"I": 114,"T": "tF","D": "Temperature F","R": "-40/300","L": 0},
		{	"I": 115,"T": "S","D": "Overtemp","R": "0/1","L": 0},
		{	"I": 118,"T": "S","D": "Input","R": "0/1/2","L": 0},
		{	"I": 211,"T": "S","D": "Energy counter 0 [W-min]","L": 0},
		{	"I": 212,"T": "S","D": "Energy counter 1 [W-min]","L": 0},
		{	"I": 213,"T": "S","D": "Energy counter 2 [W-min]", "L": 0},
		{	"I": 214,"T": "S","D": "Energy counter total [W-min]", "L": 0}
	]
}

```


## Shelly 1PM

### Shelly 1PM, FW 1.5.6 - /settings

```
{	"device":	{	"type": "SHSW-PM", "mac": "XXXXXXXX", "hostname": "shelly1pm-XXXXXX", "num_outputs": 1, "num_meters": 1},
	"wifi_ap":   { "enabled": false, "ssid": "shelly1pm-XXXXXX", "key": ""},
	"wifi_sta":  {	"enabled": true, "ssid": "iot-wlan", "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null},
	"wifi_sta1": { "enabled": false, "ssid": null, "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null},
	"mqtt":	  {	"enable": false, "server": "10.0.0.2:1883", "user": "", "id": "shelly1pm-XXXXXX","reconnect_timeout_max": 60.0,"reconnect_timeout_min": 2.0,"clean_session": true,"keep_alive": 60,"max_qos": 0,"retain": false,"update_period": 30},
	"sntp":	  {	"server": "time.google.com"},
	"login":	 {	"enabled": false,"unprotected": false,"username": "admin","password": "admin"}, "pin_code": "RPSR$p", "name": "",
	"fw": "20191127-095910/v1.5.6@0d769d69",
	"build_info": {	"build_id": "20191127-095910/v1.5.6@0d769d69","build_timestamp": "2019-11-27T09:59:10Z","build_version": "1.0"},
	"cloud": {	"enabled": true,"connected": true}, "timezone": "Europe/Berlin","lat": 50.252491,"lng": 5.779092,"tzautodetect": false,"time": "12:41",
	"hwinfo": {	"hw_revision": "prod-190329","batch_id": 1},

	"max_power": 3500, "mode": "relay", "relays": [
		{	"name": null, "ison": false, "has_timer": false, "default_state": "off", "btn_type": "edge", "btn_reverse": 0, "auto_on": 0.0, "auto_off": 0.0,
			"btn_on_url": null, "btn_off_url": null, "out_on_url": "http://10.0.0.20:8080/shelly/event/shelly1pm-e5bed0/relay/0?type=out_on",
			"out_off_url": "http://10.0.0.2:8080/shelly/event/shelly1pm-e5bed0/relay/0?type=out_off", "longpush_url": null, "shortpush_url": null, 
			"schedule": false, "schedule_rules": [], "max_power": 3500
		}],
	"meters": [
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117676,
			"counters": [
				0.0,0.0,0.0
			], "total": 2262 }
	]
}
```

### Shelly 1PM with Addon, FW 1.6.1 /settings

```
{"device":{"type":"SHSW-PM","mac":"8CAAB505FCF4","hostname":"shelly1pm-8CAAB505FCF4","num_outputs":1,"num_meters":1},"wifi_ap":{"enabled":false,"ssid":"shelly1pm-8CAAB505FCF4","key":""},"wifi_sta":{"enabled":true,"ssid":"TurtlePineHouse","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},"wifi_sta1":{"enabled":false,"ssid":"TurtlePineHouse2","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shelly1pm-8CAAB505FCF4","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},"coiot": {"update_period":15},"sntp":{"server":"192.168.6.137","enabled":true},"login":{"enabled":true,"unprotected":false,"username":"openhab","password":"bluebird"},"pin_code":"","name":"","fw":"20200312-124223/v1.6.1@112262fd","factory_reset_from_switch":true,"discoverable":true,"build_info":{"build_id":"20200312-124223/v1.6.1@112262fd","build_timestamp":"2020-03-12T12:42:23Z","build_version":"1.0"},"cloud":{"enabled":false,"connected":false},"timezone":"Europe/Berlin","lat":500.000000,"lng":500.000000,"tzautodetect":false,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":false,"time":"14:38","unixtime":1597588687,"hwinfo":{"hw_revision":"prod-191219", "batch_id":1},"max_power":3500,"power_correction":1.00,"mode" :"relay","longpush_time":800,"relays":[{"name":null,"ison":true,"has_timer":false,"default_state":"last","btn_type":"toggle","btn_reverse":0,"auto_on":0.00,"auto_off":0.00,"btn_on_url":null,"btn_off_url":null,"out_on_url":null,"out_off_url":null,"longpush_url":null,"shortpush_url":null,"schedule":false,"schedule_rules":[],"max_power":3500}],"meters":[{"power":224.53,"is_valid":true,"timestamp":1597588687,"counters":[0.000, 0.000, 0.000],"total":0}],"ext_sensors":{"temperature_unit":"C"},"ext_temperature":[{"overtemp_threshold_tC":4.0,"overtemp_threshold_tF":39.2,"undertemp_threshold_tC":30.0,"undertemp_threshold_tF":86.0,"overtemp_act":"disabled","undertemp_act":"disabled"},{"overtemp_threshold_tC":0.0,"overtemp_threshold_tF":32.0,"undertemp_threshold_tC":0.0,"undertemp_threshold_tF":32.0,"overtemp_act":"disabled","undertemp_act":"disabled"}],"ext_humidity":[]}
```

```
"ext_temperature":[
	{"overtemp_threshold_tC":4.0,"overtemp_threshold_tF":39.2,"undertemp_threshold_tC":30.0,"undertemp_threshold_tF":86.0,"overtemp_act":"disabled","undertemp_act":"disabled"},
	{"overtemp_threshold_tC":0.0,"overtemp_threshold_tF":32.0,"undertemp_threshold_tC":0.0,"undertemp_threshold_tF":32.0,"overtemp_act":"disabled","undertemp_act":"disabled"}
]
```

## Shelly 1PM /status

```
{
	"wifi_sta": {	"connected": true, "ssid": "iot-wlan", "ip": "10.0.0.2", "rssi": -49 },
	"cloud": {	"enabled": true, "connected": true },
	"mqtt": {	"connected": false },
	"time": "12:41", "serial": 23, "has_update": false, "mac": "XXXXXXXXXXXX",
	"relays": [
		{	"ison": false, "has_timer": false, "overpower": false }],
	"meters": [
		{	"power": 0.0, "is_valid": true, "timestamp": 1575117691,
			"counters": [0.0,0.0,0.0],
			"total": 2262 }
	],
	"inputs": [
		{"input": 0 }
	],
	"ext_temperature": {}, "temperature": 45.51, "overtemperature": false,
	"tmp": {"tC": 45.51, "tF": 113.93, "is_valid": "true" },
	"update": {"status": "idle", "has_update": false, "new_version": "20191127-095910/v1.5.6@0d769d69", "old_version": "20191127-095910/v1.5.6@0d769d69" },
	"ram_total": 50704, "ram_free": 35428, "fs_size": 233681, "fs_free": 172437, "uptime": 237010
}
```

### 1PM Coap FW 1.6

```
{
	"blk": [
		{	"I": 0,"D": "Relay0"},
		{	"I": 1,"D": "Sensors"} ],
	"sen": [
		{	"I": 111,"T": "P","D": "Power","R": "0/3500","L": 0},
		{	"I": 112,"T": "S","D": "State","R": "0/1","L": 0},
		{	"I": 113,"T": "T","D": "Temperature C","R": "-40/300","L": 0},
		{	"I": 114,"T": "T","D": "Temperature F","R": "-40/300","L": 0},
		{	"I": 115,"T": "S","D": "Overtemp","R": "0/1","L": 0},
		{	"I": 118,"T": "S","D": "Input","R": "0(off)/1(on)/2(longpush)","L": 0},
		{	"I": 211,"T": "S","D": "Energy counter 0 [W-min]","L": 0},
		{	"I": 212,"T": "S","D": "Energy counter 1 [W-min]","L": 0},
		{	"I": 213,"T": "S","D": "Energy counter 2 [W-min]","L": 0},
		{	"I": 214,"T": "S","D": "Energy counter total [W-min]","L": 0		}
	]
}
```

with 2 external temp sensors
```
{"blk":[
	{"I":0,"D":"Relay0"},
	{"I":1,"D":"Sensors"}
],
"sen":[
	{"I":111,"T":"P","D":"Power","R":"0/3500","L":0},
	{"I":112,"T":"S","D":"State","R":"0/1","L":0},
	{"I":113,"T":"T","D":"Temperature C","R":"-40/300","L":0},{
	"I":114,"T":"T","D":"Temperature F","R":"-40/300","L":0},
	{"I":115,"T":"S","D":"Overtemp","R":"0/1","L":0},
	{"I":118,"T":"S","D":"Input","R":"0(off)/1(on)/2(longpush)","L":0},
	{"I":211,"T":"S","D":"Energy counter 0 [W-min]","L":0},
	{"I":212,"T":"S","D":"Energy counter 1 [W-min]","L":0},
	{"I":213,"T":"S","D":"Energy counter 2 [W-min]","L":0},
	{"I":214,"T":"S","D":"Energy counter total [W-min]","L":0},
	{"I":119,"T":"T","D":"External temperature C","R":"-55/125","L":1},
	{"I":129,"T":"T","D":"External temperature C","R":"-55/125","L":1}
	]
}
```

## Shelly2
 
### /settings

```
{
	"device" : {
	   "type":"SHSW-21","mac":"XXXXXXXXXXXX","hostname":"shellyswitch-XXXXXX",
	   "num_outputs":2,"num_meters":1,"num_rollers":1
	 },
	"build_info_ap":{	"enabled":false,"ssid":"shellyswitch-XXXXXX","key":"},
	"wifi_sta":{	"enabled":true,"ssid":"iot-wlan","ipv4_method":"static","ip":"10.0.0.10","gw":"10.0.0.1","mask":"255.0.0.0","dns":"10.0.0.1"},
	"wifi_sta1":{	"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {	"enable":true,"server":"10.0.0.7:1883","user":"admin","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,
		   "clean_session":true,"keep_alive":60,"will_topic":"shellies/shellyswitch-XXXXXX/online","will_message":"false", max_qos":0,"retain":true,"update_period":30},
	"sntp": {	"server":"time.google.com"},
	"login":{	"enabled":true,"unprotected":false,"username":"xxx","password":"xxx"},"pin_code":"",
	"coiot_execute_enable":true,"name":"", "fw":"20190531-075812/v1.5.0-hotfix2@022ec015",
	"build_info":{	"build_id":"20190531-075812/v1.5.0-hotfix2@022ec015","build_timestamp":"2019-05-31T07:58:12Z","build_version":"1.0"},
	"cloud":{	"enabled":false,"connected":false}, "timezone":"Europe/Berlin","lat":45.864700,"lng":7.625460,"tzautodetect":true,"time":"23:02",
	"hwinfo":{	"hw_revision":"prod-2018-10c", "batch_id":5}, "mode":"relay","max_power":1840,
	"relays": [
		{	"name":null,"ison":false,"has_timer":false,"overpower":falsem "default_state":"last","btn_type":"edge", "btn_reverse":0,"auto_on":0.00,"auto_off":0.00,"btn_on_url":null,"btn_off_url":null,"out_on_url":null,"out_off_url":null,"schedule":false,"schedule_rules":[]},
		{	"name":null,"ison":false,"has_timer":false,"overpower":false,
			"default_state":"off","btn_type":"edge","btn_reverse":0,"auto_on":0.00,"auto_off":0.00,"btn_on_url":null,"btn_off_url":null,"out_on_url":null,"out_off_url":null,"schedule":false,"schedule_rules":[]}], "rollers":[{		"maxtime":20.00,"maxtime_open":20.00,"maxtime_close":20.00,
			"default_state":"stop","swap":false,"swap_inputs":false,"input_mode":"openclose",	"button_type":"toggle","btn_reverse":0,	"state":"stop",	"power":0.00,	"is_valid":true,"safety_switch":false,	"schedule":false,"schedule_rules":[],	"obstacle_mode":"disabled","obstacle_action":"stop","obstacle_power":200,"obstacle_delay":1,"safety_mode":"while_opening","safety_action":"stop",	"safety_allowed_on_trigger":"none","off_power":2,	"positioning":true}],
	"meters":[{	"power":0.00,"is_valid":true,"timestamp":1562713329,"counters":[0.000, 0.000, 0.000],"total":19111}]
	}
```

### Shelly 2 /settings/relay/0

```
{
	"name":null, "ison":false,"has_timer":false, "overpower":false,
			"default_state":"last", "btn_type":"edge","btn_reverse":0, "auto_on":0.00,"auto_off":0.00, "btn_on_url":null, "btn_off_url":null, "out_on_url":null, "out_off_url":null, "schedule":false, "schedule_rules":[]
}
```

### Shelly 2: /status/relay/0

```
{
	"wifi_sta":{	"connected":true,"ssid":"iot-wlan","ip":"10.0.0.1","rssi":-70},
	"cloud":{	"enabled":false, "connected":false},
	"mqtt":{	"connected":true}, "time":"15:36","serial":1,"has_update":false,"mac":"XXXXXXXXXXXX","serial":1, "has_update":true,"mac":"XXXXXXXXXXXX",
	"relays": [
		{	"ison":false,"has_timer":false,"overpower":false,"is_valid":true},
		{	"ison":false,"has_timer":false,"overpower":false,"is_valid":true}
	], "rollers":[
		{	"state":"stop","power":0.00,"is_valid":true,"safety_switch":false,"stop_reason":"normal","last_direction":"open","current_pos":101,"calibrating":false,"positioning":true}],
	"meters":[
		{	"power":0.00,"is_valid":true,"timestamp":1563292829,"counters":[0.000, 0.000, 0.000],"total":35473}
	],
	"update":{	"status":"pending","has_update":true,"new_version":"20190711-084105/v1.5.0-hotfix4@3b4f7414","old_version":"20190531-075812/v1.5.0-hotfix2@022ec015"},
	"ram_total":50264,"ram_free":37548,"fs_size":233681,"fs_free":155620,"uptime":1352018
}
```


### Shelly2: /status/device

```
{
	"enabled":true, "ssid":"iot-wlan", "ipv4_method":"static","ip":"10.0.0.10","gw":"10.0.0.1","mask":"255.0.0.0","dns":"10.0.0.1"

{
	"wifi_sta":{	"connected":true,"ssid":"iot-wlan","ip":"10.0.0.1","rssi":-69},
	"cloud":{	"enabled":false,"connected":false},
	"mqtt":{	"connected":true}, "time":"00:17","serial":1,"has_update":false,"mac":"XXXXXXXXXXXX",
	"relays": [
		{	"ison":false,"has_timer":false,"overpower":false,"is_valid":true
	 },{	"ison":false,"has_timer":false,"overpower":false,"is_valid":true}
	], "rollers":[
		{	"state":"stop","power":0.00,"is_valid":true,"safety_switch":false,"stop_reason":"normal","last_direction":"stop","current_pos":101,"calibrating":false,"positioning":true}],
	"meters":[{		"power":0.00,	"is_valid":true,	"timestamp":1562717876,	"counters":[0.000, 0.000, 0.000],	"total":19111}
	],
	"update":{
		"status":"idle", "has_update":false, "new_version":"20190531-075812/v1.5.0-hotfix2@022ec015", "old_version":"20190531-075812/v1.5.0-hotfix2@022ec015" },
	"ram_total":50264,"ram_free":37884,"fs_size":233681,"fs_free":156373,"uptime":777069
}
```

## Shelly 2.5

### Shelly 2.5-Relay: /settings

```
{	"device": {	"type": "SHSW-25", "mac": "XXXXXXXXXXXX", "hostname": "shellyswitch25-XXXXXX", "num_outputs": 2, "num_meters": 2, "num_rollers": 1 },
	"wifi_ap": {	"enabled": false, "ssid": "shellyswitch25-XXXXXX", "key": "" },
	"wifi_sta": {	"enabled": true, "ssid": "iot-wlan", "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"wifi_sta1": {	"enabled": false, "ssid": null, "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"mqtt": {	"enable": false, "server": "10.0.0.2:1883", "user": "", "id": "shellyswitch25-XXXXXX", "reconnect_timeout_max": 60.0, "reconnect_timeout_min": 2.0, "clean_session": true, "keep_alive": 60, "max_qos": 0, "retain": false, "update_period": 30 },
	"sntp": {	"server": "time.google.com" },
	"login": {	"enabled": false, "unprotected": false, "username": "admin", "password": "admin" },
	"pin_code": "Y}e!BK", "name": "",
	"fw": "20191127-095444/v1.5.6@0d769d69",
	"build_info": {	"build_id": "20191127-095444/v1.5.6@0d769d69", "build_timestamp": "2019-11-27T09:54:44Z", "build_version": "1.0" },
	"cloud": {	"enabled": true, "connected": true },
	"timezone": "Europe/Berlin", "lat": 50.252491, "lng": 5.779092, "tzautodetect": false, "time": "12:42",
	"hwinfo": {	"hw_revision": "prod-2019-03", "batch_id": 1 },
	"mode": "relay", "max_power": 1840, "relays": [
		{	"name": null, "ison": false, "has_timer": false,"overpower": false,
			"default_state": "off","btn_type": "edge","btn_reverse": 0,"auto_on": 0.0,"auto_off": 0.0,"max_power": 0,"btn_on_url": null,"btn_off_url": null,"out_on_url": "http://10.0.0.2:8080/shelly/event/shellyswitch25-0043e0/relay/0?type=out_on","out_off_url": "http://10.0.0.2:8080/shelly/event/shellyswitch25-0043e0/relay/0?type=out_off","longpush_url": null,"shortpush_url": null,"schedule": false,"schedule_rules": []},
		{	"name": null,"ison": false,"has_timer": false,"overpower": false,
			"default_state": "off","btn_type": "edge","btn_reverse": 0,"auto_on": 0.0,"auto_off": 0.0,"max_power": 0,"btn_on_url": null,"btn_off_url": null,"out_on_url": "http://10.0.0.2:8080/shelly/event/shellyswitch25-0043e0/relay/1?type=out_on","out_off_url": "http://10.0.0.2:8080/shelly/event/shellyswitch25-0043e0/relay/1?type=out_off","longpush_url": null,"shortpush_url": null,"schedule": false,"schedule_rules": []}
	], "rollers": [
		{	"maxtime": 20.0,"maxtime_open": 20.0,"maxtime_close": 20.0,
			"default_state": "stop","swap": false,"swap_inputs": false,"input_mode": "openclose","button_type": "toggle","btn_reverse": 0,"state": "stop","power": 0.0,"is_valid": true,"safety_switch": false,"roller_open_url": null,"roller_close_url": null,"roller_stop_url": null,"schedule": false,"schedule_rules": [],"obstacle_mode": "disabled","obstacle_action": "stop","obstacle_power": 200,"obstacle_delay": 1,"safety_mode": "while_opening","safety_action": "stop","safety_allowed_on_trigger": "none","off_power": 2,"positioning": true}],
	"meters": [
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117725,"counters": [	0.0,	0.0,	0.0],"total": 47},
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117725,"counters": [	0.0,	0.0,	0.0],"total": 10215}
	]
}
```

### Shelly 2.5 Relay Mode: /status

```
{
	"wifi_sta": {	"connected": true, "ssid": "iot_wlan", "ip": "10.0.0.2", "rssi": -48 },
	"cloud": {	"enabled": true, "connected": true },
	"mqtt": {	"connected": false },
	"time": "12:42", "serial": 83, "has_update": false, "mac": "XXXXXXXXXXXX", "relays": [
		{	"ison": false,"has_timer": false,"overpower": false,"overtemperature": false,"is_valid": true},
		{	"ison": false,"has_timer": false,"overpower": false,"overtemperature": false,"is_valid": true}
	], "rollers": [
		{	"state": "stop","power": 0.0,"is_valid": true,"safety_switch": false,"overtemperature": false,"stop_reason": "normal","last_direction": "stop","current_pos": 101,"calibrating": false,"positioning": true}],
	"meters": [
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117739,"counters": [	0.0,	0.0,	0.0],"total": 47},
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117739,"counters": [	0.0,	0.0,	0.0],"total": 10215}
	], "inputs": [
		{	"input": 0},
		{	"input": 0}
	], "temperature": 61.76, "overtemperature": false, "tmp": {	"tC": 61.76, "tF": 143.16, "is_valid": "true" },
	"update": {	"status": "idle", "has_update": false, "new_version": "20191127-095444/v1.5.6@0d769d69", "old_version": "20191127-095444/v1.5.6@0d769d69" },
	"ram_total": 49568, "ram_free": 34720, "fs_size": 233681, "fs_free": 156624, "voltage": 237.72, "uptime": 237037
}
```

### Shelly 2.5 Generic Coap Status 

```
{
	"G":[
		[0,112,0],
		[0,122,0],
		[0,111,0.000000]]
}
```

### Shelly 2.5 Coap Description FW 1.6


```
{
	"blk": [
		{	"I": 0,"D": "Relay0"},
		{	"I": 1,"D": "Relay1"},
		{	"I": 2,"D": "Device"} ],
	"sen": [
		{	"I": 112,"T": "S","D": "State","R": "0/1","L": 0},
		{	"I": 122,"T": "S","D": "State","R": "0/1","L": 1},
		{	"I": 111,"T": "P","D": "Power","R": "0/2300","L": 0},
		{	"I": 121,"T": "P","D": "Power","R": "0/2300","L": 1},
		{	"I": 118,"T": "S","D": "Input","R": "0(off)/1(on)/2(longpush)","L": 0},
		{	"I": 128,"T": "S","D": "Input","R": "0(off)/1(on)/2(longpush)","L": 1},
		{	"I": 115,"T": "T","D": "Temperature C","R": "-40/300","L": 2},
		{	"I": 116,"T": "T","D": "Temperature F","R": "-40/300","L": 2},
		{	"I": 117,"T": "S","D": "Overtemp","R": "0/1","L": 2},
		{	"I": 211,"T": "S","D": "Energy counter 0 [W-min]","L": 0},
		{	"I": 212,"T": "S","D": "Energy counter 1 [W-min]","L": 0},
		{	"I": 213,"T": "S","D": "Energy counter 2 [W-min]","L": 0},
		{	"I": 214,"T": "S","D": "Energy counter total [W-min]","L": 0},
		{	"I": 221,"T": "S","D": "Energy counter 0 [W-min]","L": 1},
		{	"I": 222,"T": "S","D": "Energy counter 1 [W-min]","L": 1},
		{	"I": 223,"T": "S","D": "Energy counter 2 [W-min]","L": 1},
		{	"I": 224,"T": "S","D": "Energy counter total [W-min]","L": 1}
	]
}
```

### SHelly 2.5  Roller Mode: /settings

```
{	"device": {	"type": "SHSW-25", "mac": "XXXXXXXXXXXX", "hostname": "shellyswitch25-XXXXXX", "num_outputs": 2, "num_meters": 2, "num_rollers": 1 },
	"wifi_ap": {	"enabled": false, "ssid": "shellyswitch25-XXXXXX", "key": "" },
	"wifi_sta": {	"enabled": true, "ssid": "iot-wlan", "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"wifi_sta1": {	"enabled": false, "ssid": null, "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"mqtt": {	"enable": false, "server": "10.0.0.2:1883", "user": "", "id": "shellyswitch25-XXXXXX", "reconnect_timeout_max": 60.0, "reconnect_timeout_min": 2.0, "clean_session": true, "keep_alive": 60, "max_qos": 0, "retain": false, "update_period": 30 },
	"sntp": {	"server": "time.google.com" },
	"login": {	"enabled": false, "unprotected": false, "username": "admin", "password": "admin" },
	"pin_code": "!pw5e5", "name": "",
	"fw": "20191127-095444/v1.5.6@0d769d69",
	"build_info": {	"build_id": "20191127-095444/v1.5.6@0d769d69", "build_timestamp": "2019-11-27T09:54:44Z", "build_version": "1.0" },
	"cloud": {	"enabled": true, "connected": true },
	"timezone": "Europe/Berlin", "lat": 50.252491, "lng": 5.779092, "tzautodetect": false, "time": "12:42",
	"hwinfo": {	"hw_revision": "prod-2019-03", "batch_id": 1 },
	"mode": "roller", "max_power": 1840, "relays": [
		{	"name": null,"ison": false,"has_timer": false,"overpower": false,
			"default_state": "off","btn_type": "toggle","btn_reverse": 0,"auto_on": 0.0,"auto_off": 0.0,"max_power": 0,"btn_on_url": null,"btn_off_url": null,"out_on_url": null,"out_off_url": null,"longpush_url": null,"shortpush_url": null,"schedule": false,"schedule_rules": []},
		{	"name": null,"ison": false,"has_timer": false,"overpower": false,
			"default_state": "off","btn_type": "toggle","btn_reverse": 0,"auto_on": 0.0,"auto_off": 0.0,"max_power": 0,"btn_on_url": null,"btn_off_url": null,"out_on_url": null,"out_off_url": null,"longpush_url": null,"shortpush_url": null,"schedule": false,"schedule_rules": []}
	], "rollers": [
		{	"maxtime": 20.0,"maxtime_open": 23.0,"maxtime_close": 22.0,
			"default_state": "stop","swap": false,"swap_inputs": false,"input_mode": "openclose","button_type": "momentary","btn_reverse": 0,"state": "stop","power": 0.0,"is_valid": true,"safety_switch": false,"roller_open_url": "http://10.0.0.2:8080/shelly/event/shellyswitch25-XXXXXX/roller/0?type=roller_open","roller_close_url": "http://10.0.0.2:8080/shelly/event/shellyswitch25-XXXXXX/roller/0?type=roller_close","roller_stop_url": "http://10.0.0.2:8080/shelly/event/shellyswitch25-XXXXXX/roller/0?type=roller_stop","schedule": false,"schedule_rules": [	"0700-0123456-10%",	"0000bss-0123456-open"],"obstacle_mode": "disabled","obstacle_action": "stop","obstacle_power": 200,"obstacle_delay": 1,"safety_mode": "while_opening","safety_action": "stop","safety_allowed_on_trigger": "none","off_power": 2,"positioning": true}],
	"meters": [
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117752,"counters": [	0.0,	0.0,	0.0],"total": 44},
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117752,"counters": [	0.0,	0.0,	0.0],"total": 44}
	]
}
```

### Shelly 2.5 Roller Mode: /status

```
{
	"wifi_sta": {	"connected": true, "ssid": "iot_wlan", "ip": "10.0.0.1", "rssi": -54 },
	"cloud": {	"enabled": true, "connected": true },
	"mqtt": {	"connected": false },
	"time": "12:42", "serial": 16, "has_update": false, "mac": "XXXXXXXXXXXX", "relays": [
		{	"ison": false,"has_timer": false,"overpower": false,"overtemperature": false,"is_valid": true},
		{	"ison": false,"has_timer": false,"overpower": false,"overtemperature": false,"is_valid": true}
	], "rollers": [
		{	"state": "stop","power": 0.0,"is_valid": true,"safety_switch": false,"overtemperature": false,"stop_reason": "normal","last_direction": "open","current_pos": 100,"calibrating": false,"positioning": true}],
	"meters": [
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117766,"counters": [	0.0,	0.0,	0.0],"total": 44},
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117766,"counters": [	0.0,	0.0,	0.0],"total": 44}
	], "inputs": [
		{	"input": 0},
		{	"input": 0}
	], "temperature": 63.62, "overtemperature": false, "tmp": {	"tC": 63.62, "tF": 146.52, "is_valid": "true" },
	"update": {	"status": "idle", "has_update": false, "new_version": "20191127-095444/v1.5.6@0d769d69", "old_version": "20191127-095444/v1.5.6@0d769d69" },
	"ram_total": 49568, "ram_free": 34712, "fs_size": 233681, "fs_free": 156122, "voltage": 231.83, "uptime": 237263
}
```



## Shelly Plug-S

###Shelly Plug-S: /settings

```
{	"device": {	"type": "SHPLG-S", "mac": "XXXXXXXXXXXX", "hostname": "shellyplug-s-XXXX", "num_outputs": 1, "num_meters": 1 },
	"wifi_ap": {	"enabled": false, "ssid": "shellyplug-s-XXXX", "key": "" },
	"wifi_sta": {	"enabled": true, "ssid": "iot-wlan", "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"wifi_sta1": {	"enabled": false, "ssid": null, "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"mqtt": {	"enable": false, "server": "10.0.0.2:1883", "user": "", "id": "shellyplug-s-XXXXXX", "reconnect_timeout_max": 60.0, "reconnect_timeout_min": 2.0, "clean_session": true, "keep_alive": 60, "max_qos": 0, "retain": false, "update_period": 30 },
	"sntp": {	"server": "time.google.com" },
	"login": {	"enabled": false, "unprotected": false, "username": "admin", "password": "admin" },
	"pin_code": "adIL-{	", "name": "",
	"fw": "20191127-095857/v1.5.6@0d769d69",
	"build_info": {	"build_id": "20191127-095857/v1.5.6@0d769d69", "build_timestamp": "2019-11-27T09:58:57Z", "build_version": "1.0" },
	"cloud": {	"enabled": true, "connected": true },
	"timezone": "Europe/Berlin", "lat": 50.252491, "lng": 5.779092, "tzautodetect": false, "time": "12:43",
	"hwinfo": {	"hw_revision": "prod-190516", "batch_id": 1 },
	"max_power": 2500, "led_status_disable": true, "led_power_disable": false, "relays": [
		{	"ison": false,"has_timer": false,"overpower": false,
			"default_state": "off","auto_on": 0.0,"auto_off": 0.0,"btn_on_url": null,"out_on_url": "http://10.0.0.1:8080/shelly/event/shellyplug-s-041b29/relay/0?type=out_on","out_off_url": "http://10.0.0.1:8080/shelly/event/shellyplug-s-041b29/relay/0?type=out_off","schedule": true,"schedule_rules": [	"0700-0123456-off"],"max_power": 2500}],
	"meters": [
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117787,"counters": [	0.0,	0.0,	0.0],"total": 0}
	]
}
```

### Shelly Plug-S: /status

```
{
	"wifi_sta": {	"connected": true, "ssid": "iot_wlan", "ip": "10.0.0.2", "rssi": -52 },
	"cloud": {	"enabled": true, "connected": true },
	"mqtt": {	"connected": false },
	"time": "12:43", "serial": 1, "has_update": false, "mac": "XXXXXXXXXXXX", "relays": [
		{	"ison": false,"has_timer": false,"overpower": false}],
	"meters": [
		{	"power": 0.0,"is_valid": true,"timestamp": 1575117798,"counters": [	0.0,	0.0,	0.0],"total": 0}
	], "temperature": 30.71, "overtemperature": false, "tmp": {	"tC": 30.71, "tF": 87.28, "is_valid": "true" },
	"update": {	"status": "idle", "has_update": false, "new_version": "20191127-095857/v1.5.6@0d769d69", "old_version": "20191127-095857/v1.5.6@0d769d69" },
	"ram_total": 50784, "ram_free": 37312, "fs_size": 233681, "fs_free": 173441, "uptime": 237171
}
```

### Shelly Plug-S Coap Description FW 1.6

```
{
	"blk": [
		{	"I": 0,"D": "Relay0"} ],
	"sen": [
		{	"I": 111,"T": "P","D": "Power","R": "0/2500","L": 0},
		{	"I": 112,"T": "S","D": "State","R": "0/1","L": 0},
		{	"I": 113,"T": "T","D": "Temperature C","R": "-40/300","L": 0},
		{	"I": 114,"T": "T","D": "Temperature F","R": "-40/300","L": 0},
		{	"I": 115,"T": "S","D": "Overtemp","R": "0/1","L": 0},
		{	"I": 211,"T": "S","D": "Energy counter 0 [W-min]","L": 0},
		{	"I": 212,"T": "S","D": "Energy counter 1 [W-min]","L": 0},
		{	"I": 213,"T": "S","D": "Energy counter 2 [W-min]","L": 0},
		{	"I": 214,"T": "S","D": "Energy counter total [W-min]","L": 0}
	]
} 
```

## Shelly 4 Pro

### 4 Pro /settings
```
{	"device":{	"type":"SHSW-44","mac":"90E202CC7D7A","hostname":"shelly4pro-CC7D7A","num_outputs":4, "num_meters":4, "num_rollers":0},
	"wifi_ap":{	"enabled":false,"ssid":"shelly4pro-CC7D7A","key":""},
	"wifi_sta":{	"enabled":true,"ssid":"ShellyIoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{	"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {	"enable":false,"server":"192.168.33.3:1883","user":"","id":"shelly4pro-CC7D7A","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {	"update_period":15},
	"sntp":{	"server":"time.google.com","enabled":true},
	"login":{	"enabled":false,"unprotected":false,"username":"admin","password":"admin"}, "pin_code":"","name":"","fw":"20200306-150856/v1.6.0-rc6@43056d58","discoverable":true,
	"build_info":{	"build_id":"20200306-150856/v1.6.0-rc6@43056d58","build_timestamp":"2020-03-06T15:08:56Z","build_version":"1.0"},
	"cloud":{	"enabled":false,"connected":false}, "timezone":"Europe/Berlin","lat":49.864700,"lng":8.625460,"tzautodetect":true,
	"tz_utc_offset":3600,"tz_dst":false,"tz_dst_auto":true,"time":"22:55","unixtime":1583621752,
	"hwinfo":{	"hw_revision":"prod-2018-12","batch_id":6},"lat":49.864700,"lng":8.625460,
	"relays": [
		{	"name":null,"ison":false,"has_timer":false,"overpower":false,
			"default_state":"off","btn_type":"toggle","auto_on":0.00,"auto_off":0.00,"max_power":2300,"schedule":false,"schedule_rules":[]},
		{	"name":null,"ison":false,"has_timer":false,"overpower":false,
			"default_state":"off","btn_type":"toggle","auto_on":0.00,"auto_off":0.00,"max_power":2300,"schedule":false,"schedule_rules":[]},
		{	"name":null,"ison":false,"has_timer":false,"overpower":false,
			"default_state":"off","btn_type":"toggle","auto_on":0.00,"auto_off":0.00,"max_power":2300,"schedule":false,"schedule_rules":[]},
		{	"name":null,"ison":false,"has_timer":false,"overpower":false,
			"default_state":"off","btn_type":"toggle","auto_on":0.00,"auto_off":0.00,"max_power":2300,"schedule":false,"schedule_rules":[]}],
	"meters":[
		{	"power":0.0,"is_valid":true,"timestamp":1583621752,"counters":[0.000, 0.000, 0.000],"total":0},
		{	"power":0.0,"is_valid":true,"timestamp":1583621752,"counters":[0.000, 0.000, 0.000],"total":0},
		{	"power":0.0,"is_valid":true,"timestamp":1583621752,"counters":[0.000, 0.000, 0.000],"total":0},
		{	"power":0.0,"is_valid":true,"timestamp":1583621752,"counters":[0.000, 0.000, 0.000],"total":0}
	]
}
```

### 4 Pro /status

```
{
	"wifi_sta":{	"connected":true,"ssid":"ShellyIoT","ip":"192.168.6.86","rssi":-56},
	"cloud":{	"enabled":false,"connected":false},
	"mqtt":{	"connected":false},"time":"23:01","unixtime":1583622115,"serial":1,"has_update":true,"mac":"90E202CC7D7A",
	"relays": [
		{	"ison":false,"has_timer":false,"timer_remaining":0,"overpower":false,"is_valid":true},
		{	"ison":false,"has_timer":false,"timer_remaining":0,"overpower":false,"is_valid":true},
		{	"ison":false,"has_timer":false,"timer_remaining":0,"overpower":false,"is_valid":true},
		{	"ison":false,"has_timer":false,"timer_remaining":0,"overpower":false,"is_valid":true}],
	"meters":[
		{	"power":0.0,"is_valid":true,"timestamp":1583622115,"counters":[0.000, 0.000, 0.000],"total":0},
		{	"power":0.0,"is_valid":true,"timestamp":1583622115,"counters":[0.000, 0.000, 0.000],"total":0},
		{	"power":0.0,"is_valid":true,"timestamp":1583622115,"counters":[0.000, 0.000, 0.000],"total":0},
		{	"power":0.0,"is_valid":true,"timestamp":1583622115,"counters":[0.000, 0.000, 0.000],"total":0}
	],
	"update":{	"status":"pending","has_update":true,"new_version":"20191216-090307/v1.5.7@c30657ba","old_version":"20200306-150856/v1.6.0-rc6@43056d58"},
	"ram_total":35896,"ram_free":7396,"fs_size":83081,"fs_free":12048,"voltage":232.44,"uptime":19719
}
```

#### Shelly Pro CoAP Descrition

```
{
	"blk":[
		{	"I":0,"D":"Relay0"},
		{	"I":1,"D":"Relay1"},
		{	"I":2,"D":"Relay2"},
		{	"I":3,"D":"Relay3"} ],
	"sen":[
		{	"I":111,"T":"W","D":"Power","R":"0/2650","L":0},
		{	"I":112,"T":"Switch","D":"State","R":"0/1","L":0},
		{	"I":121,"T":"W","D":"Power","R":"0/2650","L":1},
		{	"I":122,"T":"Switch","D":"State","R":"0/1","L":1},
		{	"I":131,"T":"W","D":"Power","R":"0/2650","L":2},
		{	"I":132,"T":"Switch","D":"State","R":"0/1","L":2},
		{	"I":141,"T":"W","D":"Power","R":"0/2650","L":3},
		{	"I":142,"T":"Switch","D":"State","R":"0/1","L":3},
		{	"I":118,"T":"S","D":"Input","R":"0/1","L":0},
		{	"I":128,"T":"S","D":"Input","R":"0/1","L":1},
		{	"I":138,"T":"S","D":"Input","R":"0/1","L":2},
		{	"I":148,"T":"S","D":"Input","R":"0/1","L":3},
		{	"I":211,"T":"S","D":"E cnt 0 [W-min]","L":0},
		{	"I":212,"T":"S","D":"E cnt 1 [W-min]","L":0},
		{	"I":213,"T":"S","D":"E cnt 2 [W-min]","L":0},
		{	"I":214,"T":"S","D":"E cnt total [W-min]","L":0},
		{	"I":221,"T":"S","D":"E cnt 0 [W-min]","L":1},
		{	"I":222,"T":"S","D":"E cnt 1 [W-min]","L":1},
		{	"I":223,"T":"S","D":"E cnt 2 [W-min]","L":1},
		{	"I":224,"T":"S","D":"E cnt total [W-min]","L":1},
		{	"I":231,"T":"S","D":"E cnt 0 [W-min]","L":2},
		{	"I":232,"T":"S","D":"E cnt 1 [W-min]","L":2},
		{	"I":233,"T":"S","D":"E cnt 2 [W-min]","L":2},
		{	"I":234,"T":"S","D":"E cnt total [W-min]","L":2},
		{	"I":241,"T":"S","D":"E cnt 0 [W-min]","L":3},
		{	"I":242,"T":"S","D":"E cnt 1 [W-min]","L":3},
		{	"I":243,"T":"S","D":"E cnt 2 [W-min]","L":3},
		{	"I":244,"T":"S","D":"E cnt total [W-min]","L":3}
	]
}
```


## Shelly EM

### Shelly EM /settings

```
{	"device":{	"type":"SHEM","mac":"XXXXXXXXXXXX","hostname":"shellyem-XXXXXX", "num_outputs":1, "num_meters": 0, "num_emeters":2
	 },
	"wifi_ap":{	"enabled":false,"ssid":"shellyem-XXXXXX","key":""},"wifi_sta":{	"enabled":true,"ssid":"iot_wlan","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},"wifi_sta1":{	"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {	"enable":false,"server":"10.0.0.1:1883","user":"","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"will_topic":"shellies/shellyem-XXXXXX/online","will_message":"false","max_qos":0,"retain":false,"update_period":30},
	"sntp": {	"server":"time.google.com"},"login":{	"enabled":false,"unprotected":false,"username":"admin","password":"admin"},"pin_code":"BZ1Xg+",
	"coiot_execute_enable":false,"name":"","fw":"20190821-095337/v1.5.2@4148d2b7",
	"build_info":{	"build_id":"20190821-095337/v1.5.2@4148d2b7","build_timestamp":"2019-08-21T09:53:37Z","build_version":"1.0"},
	"cloud":{	"enabled":true,"connected":true},"timezone":"Europe/Berlin","lat":45.775398,"lng":9.181760,"tzautodetect":true,"time":"22:50","hwinfo":{	"hw_revision":"prod-2019-06", "batch_id":0},"max_power":0,
	"relays": [
	 	{	"name":null,"ison":true,"has_timer":false,"overpower":false,
			"default_state":"last","auto_on":0.00,"auto_off":0.00,"max_power":0,
			"out_on_url":"http://10.0.10.254:8080/shelly/event/shellyem-b9f355/relay/0?type=out_on",
			"out_off_url":"http://10.0.10.254:8080/shelly/event/shellyem-b9f355/relay/0?type=out_off","schedule":false,
			"schedule_rules":[] } ],
	"emeters":[{	"ctraf_type":120},{	"ctraf_type":120}]
}
```

### Shelly EM /status

```
{
	"wifi_sta":{	"connected":true,"ssid":"iot-wlan","ip":"172.16.12.26","rssi":-54},
	"cloud": {		"enabled":true,"connected":true},
	"mqtt":{		"connected":false},"time":"22:56","serial":13416, "has_update":false,"mac":"XXXXXXXXXXXX",
	"relays": [{	"ison":true,"has_timer":false,"overpower":false,"is_valid":true}],
	"emeters":[
		{	"power":98.55,"reactive":-159.32,"voltage":239.38,"is_valid":true,"total":9188.8,"total_returned":16477.0},
		{	"power":0.00,"reactive":0.00,"voltage":239.38,"is_valid":true,"total":0.0,"total_returned":0.0}],
	"update":{	"status":"idle","has_update":false,"new_version":"20190821-095337/v1.5.2@4148d2b7","old_version":"20190821-095337/v1.5.2@4148d2b7"},
	"ram_total":49960,"ram_free":33784,"fs_size":233681,"fs_free":169425,"uptime":174189
}
```

Shelly 3EM /settings

```
{	"device":{	"type":"SHEM-3","mac":"XXXXXXXXXXXX","hostname":"shellyem3-XXXXXX","num_outputs":1, "num_meters": 0, "num_emeters":3},
	"wifi_ap":{	"enabled":false,"ssid":"shellyem3-XXXXXX","key":""},
	"wifi_sta":{	"enabled":true,"ssid":"xxxxx","ipv4_method":"static","ip":"xxx.xxx.xxx.xxx","gw":"xxx.xxx.xxx.xxx","mask":"255.255.255.0","dns":null},
	"wifi_sta1":{	"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {	"enable":true,"server":"xxx.xxx.xxx.xxx:1883","user":"xxx","id":"shellyem3-DC4F2276467A","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {	"update_period":15},
	"sntp":{	"server":"time.google.com","enabled":true},
	"login":{	"enabled":true,"unprotected":false,"username":"xxxxx","password":"xxxxxxx"}, "pin_code":"","name":"", "fw":"20200205-183800/master@fa9ba943","discoverable":false,
	"build_info":{	"build_id":"20200205-183800/master@fa9ba943","build_timestamp":"2020-02-05T18:38:00Z","build_version":"1.0"},
	"cloud":{	"enabled":false,"connected":false},
	"timezone":"Europe/Amsterdam","lat":xx.xxxxxx,"lng":xx.xxxxxx,"tzautodetect":true,"tz_utc_offset":3600,"tz_dst":false,"tz_dst_auto":true,"time":"08:18",
	"hwinfo":{	"hw_revision":"prod-2020-1", "batch_id":1}, "max_power":120,
	"relays": [
		{	"name":null,"ison":false,"has_timer":false,"overpower":false,
			"default_state":"off","auto_on":0.00,"auto_off":0.00,"max_power":0,"out_on_url":null,"out_off_url":null,"schedule":false,"schedule_rules":[]}],
	"emeters":[
		{	"ctraf_type":50},
		{	"ctraf_type":120},
		{	"ctraf_type":120}]
	}
	"update":{	"status":"idle","has_update":false,"new_version":"20190821-095337/v1.5.2@4148d2b7","old_version":"20190821-095337/v1.5.2@4148d2b7"},
	"ram_total":49960,"ram_free":33784,"fs_size":233681,"fs_free":169425,"uptime":174189
}
```

### Shelly 3EM /status

```
{
	"wifi_sta":{	"connected":true,"ssid":"xxxxxx","ip":"xxx.xxx.xxx.xxx","rssi":-57},
	"cloud": {		"enabled":false,"connected":false},
	"mqtt":{		"connected":true},"time":"08:23","serial":1,"has_update":false,"mac":"DC4F2276467A",
	"relays": [
		{	"ison":false,"has_timer":false,"overpower":false,"is_valid":true}],
	"emeters":[
		{	"power":30.63,"pf":0.32,"current":0.40,"voltage":236.41,"is_valid":true,"total":26560.9,"total_returned":0.0},
		{	"power":316.27,"pf":0.69,"current":1.92,"voltage":237.27,"is_valid":true,"total":67514.3,"total_returned":0.0},
		{	"power":-56.44,"pf":-0.20,"current":1.21,"voltage":237.57,"is_valid":true,"total":21726.1,"total_returned":33183.2}
	],
	"fs_mounted":true, "update":{	"status":"idle","has_update":false,"new_version":"20200205-183800/master@fa9ba943","old_version":"20200205-183800/master@fa9ba943"},
	"ram_total":48704,"ram_free":30744,"fs_size":233681,"fs_free":162146,"uptime":651613
}
```

### Shelly EM3 CoAP Description

```
{
	“blk”:[
		{“I”:0,“D”:“Relay0”},
		{“I”:1,“D”:“Meter0”},
		{“I”:2,“D”:“Meter1”},
		{“I”:3,“D”:“Meter2”}
	],
	“sen”:[
		{“I”:112,“T”:“S”,“D”:“State”,“R”:“0/1”,“L”:0},
		{“I”:111,“T”:“P”,“D”:“Power”,“R”:“0/26400”,“L”:1},
		{“I”:114,“T”:“S”,“D”:“PF”,“R”:“0/1”,“L”:1},
		{“I”:115,“T”:“S”,“D”:“Current”,“R”:“0/120”,“L”:1},
		{“I”:116,“T”:“S”,“D”:“Voltage”,“R”:“0/265”,“L”:1},
		{“I”:121,“T”:“P”,“D”:“Power”,“R”:“0/26400”,“L”:2},
		{“I”:124,“T”:“S”,“D”:“PF”,“R”:“0/1”,“L”:2},
		{“I”:125,“T”:“S”,“D”:“Current”,“R”:“0/120”,“L”:2},
		{“I”:126,“T”:“S”,“D”:“Voltage”,“R”:“0/265”,“L”:2},
		{“I”:131,“T”:“P”,“D”:“Power”,“R”:“0/26400”,“L”:3},
		{“I”:134,“T”:“S”,“D”:“PF”,“R”:“0/1”,“L”:3},
		{“I”:135,“T”:“S”,“D”:“Current”,“R”:“0/120”,“L”:3},
		{“I”:136,“T”:“S”,“D”:“Voltage”,“R”:“0/265”,“L”:3}
	]
}
```

### Shelly Dimmer /settings

```
{	"device":{"type":"SHDM-1","mac":"CC50E3F3A0D7","hostname":"shellydimmer-F3A0D7","num_outputs":1,"num_meters":1},
	"wifi_ap":{"enabled":false,"ssid":"shellydimmer-F3A0D7","key":""},
	"wifi_sta":{"enabled":true,"ssid":"ShellyIoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellydimmer-F3A0D7","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15},
	"sntp":{"server":"time.google.com","enabled":true},
	"login":{"enabled":false,"unprotected":false,"username":"admin","password":"admin"},"pin_code":"","name":"",
	"fw":"20200309-104554/v1.6.0@43056d58","factory_reset_from_switch":true,"discoverable":true,
	build_info":{"build_id":"20200309-104554/v1.6.0@43056d58","build_timestamp":"2020-03-09T10:45:54Z","build_version":"1.0"},
	"cloud":{"enabled":false,"connected":false},
	"timezone":"Europe/Berlin","lat":50.110901,"lng":8.682130,"tzautodetect":true,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":true,"time":"09:06","unixtime":1587114385,"hwinfo":{"hw_revision":"dev-prototype","batch_id":0},
	"mode":"white","pulse_mode":0,
	"calibrated":false,
	"transition":1000,
	"fade_rate":3,
	"min_brightness":0,
	"lights":[
		{"name":"","ison":false,"default_state":"off","auto_on":0.00,"auto_off":0.00,"btn1_on_url":"","btn1_off_url":"","btn1_longpush_url":"","btn1_shortpush_url":"","btn2_on_url":"","btn2_off_url":"","btn2_longpush_url":"","btn2_shortpush_url":"","out_on_url":"","out_off_url":"","schedule":false,"schedule_rules":[],"btn_type":"one_button","btn_debounce":80,"swap_inputs":0}
	],
	"night_mode":{"enabled":0, "start_time":"00:00", "end_time":"00:00", "brightness":0}
}
```


### Shelly Dimmer /status

```
{
	"wifi_sta":{	"connected":true,"ssid":"iot-wlan","ip":"10.0.0.195","rssi":-75},
	"cloud":{	"enabled":true,"connected":true},
	"mqtt":{		"connected":false}, "time":"13:35","serial":9496,"has_update":false,"mac":"XXXXXXXXXXXX",
	"lights":[
		{	"ison":true,"mode":"white","brightness":100}],
	"meters":[
		{	"power":22.83, "is_valid":true, "timestamp":1568986518,"counters":[8.305, 3.153, 8.892],"total":782}],
	"inputs":[
		{	"input":0},
		{	"input":0}],
	"tmp":{	"tC":56.73,"tF":134.11, "is_valid":"true"}, "overtemperature":false, "loaderror":false, "overload":false
	"update":{	"status":"unknown","has_update":false,"new_version":"","old_version":"20190913-140549/master@d040e20e"},
	"ram_total":49888,"ram_free":38540,"fs_size":233681,"fs_free":149596,"uptime":561474
}
```

### Shelly Dimmer Coap Description FW 1.6

```
{
	"blk": [
		{	"I": 0,"D": "Dimmer"} ],
	"sen": [
		{	"I": 111,"T": "S","D": "Brightness","R": "0/100","L": 0},
		{	"I": 121, "T": "S", "D": "Output", "R": "0/1", "L": 0},
		{	"I": 131,"T": "S","D": "Input","R": "0/1","L": 0},
		{	"I": 141,"T": "S","D": "Input","R": "0/1","L": 0},
		{	"I": 211,"T": "S","D": "Energy counter 0 [W-min]","L": 0},
		{	"I": 212,"T": "S","D": "Energy counter 1 [W-min]","L": 0},
		{	"I": 213,"T": "S","D": "Energy counter 2 [W-min]", "L": 0},
		{	"I": 214,"T": "S","D": "Energy counter total [W-min]", "L": 0}
	]
}
```

## Shelly Dimmer2

### /shelly

```
{"type":"SHDM-2","mac":"XXXXXXXXXXXX","auth":false,"fw":"20200528-111028/1026-new-device-dimmer-l@98d7122e","longid":1,"num_outputs":1,"num_meters":1}
```

### /settings

```
{	"device":{"type":"SHDM-2","mac":"XXXXXXXXXXXX","hostname":"shellydimmer2-E0980695A794","num_outputs":1,"num_meters":1},
	"wifi_ap":{"enabled":false,"ssid":"shellydimmer2-XXXXXXXXXXXX","key":""},
	"wifi_sta":{"enabled":true,"ssid":"ShellyIoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellydimmer2-E0980695A794","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15},"sntp":{"server":"time.google.com","enabled":true},
	"login":{"enabled":false,"unprotected":false,"username":"admin"},"pin_code":"","name":"","fw":"20200528-111028/1026-new-device-dimmer-l@98d7122e","discoverable":true,"build_info":{"build_id":"20200528-111028/1026-new-device-dimmer-l@98d7122e","build_timestamp":"2020-05-28T11:10:28Z","build_version":"1.0"},"cloud":{"enabled":false,"connected":false},"timezone":"Europe/Berlin","lat":50.110901,"lng":8.682130,"tzautodetect":true,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":true,"time":"07:45","unixtime":1592984713,"hwinfo":{"hw_revision":"dev-prototype","batch_id":0},"mode":"white","pulse_mode":1,"calibrated":false,"transition":1000,"fade_rate":3,"min_brightness":0,"lights":[{"name":"","ison":false,"default_state":"off","auto_on":0.00,"auto_off":0.00,"btn1_on_url":"","btn1_off_url":"","btn1_longpush_url":"","btn1_shortpush_url":"","btn2_on_url":"","btn2_off_url":"","btn2_longpush_url":"","btn2_shortpush_url":"","out_on_url":"","out_off_url":"","schedule":false,"schedule_rules":[],"btn_type":"one_button","btn_debounce":80,"swap_inputs":0}],"night_mode":{"enabled":false, "start_time":"00:00", "end_time":"00:00", "brightness":0},"warm_up": {"enabled":0, "brightness":100, "time":50}}
```

### /status

```
{	"wifi_sta":{"connected":true,"ssid":"ShellyIoT","ip":"192.168.6.66","rssi":-52},"cloud":{"enabled":false,"connected":false},
	"mqtt":{"connected":false},"time":"07:45","unixtime":1592984739,"serial":1,"has_update":true,"mac":"XXXXXXXXXXXX",
	"lights":[
		{"ison":false,"has_timer":false,"timer_started":0,"timer_duration":0,"timer_remaining":0,"mode":"white","brightness":40}],
	"meters":[{"power":0.00, "is_valid":true, "timestamp":1592984739,"counters":[0.000, 0.000, 0.000],"total":0}],
	"inputs":[
		{"input":0,"event":"","event_cnt":0},
		{"input":0,"event":"","event_cnt":0}],
	"tmp":{"tC":42.86,"tF":109.14, "is_valid":true},
	"calib_progress":0,"calib_status":0,"wire_mode":1,"loaderror":0,"overtemperature":false,"overload":false,
	"update":{"status":"pending","has_update":true,"new_version":"20200615-133340/v1.7.2@e39e9c86","old_version":"20200528-111028/1026-new-device-dimmer-l@98d7122e"},"ram_total":47968,"ram_free":36880,"fs_size":233681,"fs_free":124747,"uptime":199}
```

### Dimmer 2 CoAP Description

```
{	"blk":[{"I":0,"D":"Dimmer"}],
	"sen":[
		{"I":111,"T":"S","D":"Brightness","R":"0/100","L":0},
		{"I":121,"T":"S","D":"State","R":"0/1","L":0},
		{"I":131,"T":"S","D":"Input","R":"0(off)/1(on)/2(longpush)","L":0},
		{"I":141,"T":"S","D":"Input","R":"0(off)/1(on)/2(longpush)","L":0},
		{"I":211,"T":"S","D":"Energy counter 0 [W-min]","L":0},
		{"I":212,"T":"S","D":"Energy counter 1 [W-min]","L":0},
		{"I":213,"T":"S","D":"Energy counter 2 [W-min]","L":0},
		{"I":214,"T":"S","D":"Energy counter total [W-min]","L":0},
		{"I":311,"T":"T","D":"Temperature C","R":"-40/300","L":0},
		{"I":312,"T":"T","D":"Temperature F","R":"-40/300","L":0},
		{"I":313,"T":"S","D":"Overtemp","R":"0/1","L":0}]
	}
```

## Shelly ix3

### /shelly

```
{"type":"SHIX3-1","mac":"XXXXXXXXXXXX","auth":true,"fw":"20200601-123434/v1.7.0@d7961837","longid":1,"num_inputs":3, "num_outputs":0}
```

### /settings

```
{	"device":{"type":"SHIX3-1","mac":"XXXXXXXXXXXX","hostname":"shellyix3-XXXXXXXXXXXX","num_inputs":3, "num_outputs":0},
	"wifi_ap":{"enabled":false,"ssid":"shellyix3-XXXXXXXXXXXX","key":""},
	"wifi_sta":{"enabled":true,"ssid":"ShellyIoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellyix3-A4CF12F47388","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15},"sntp":{"server":"time.google.com","enabled":true},"login":{"enabled":true,"unprotected":false,"username":"openhab"},"pin_code":"","name":"",
	"fw":"20200601-123434/v1.7.0@d7961837","factory_reset_from_switch":true,"discoverable":true,"build_info":{"build_id":"20200601-123434/v1.7.0@d7961837","build_timestamp":"2020-06-01T12:34:34Z","build_version":"1.0"},
	"cloud":{"enabled":false,"connected":false},
	"timezone":"Europe/Berlin","lat":50.110901,"lng":8.682130,"tzautodetect":true,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":true,"time":"22:21","unixtime":1592950863,"hwinfo":{"hw_revision":"prod-2020-04-21", "batch_id":1},
	"longpush_duration_ms":{"min":800, "max":3000},"multipush_time_between_pushes_ms":{"max":500},
	"inputs":[
		{"name":null,"btn_type":"momentary","btn_reverse":0,"btn_on_url":null,"btn_off_url":null,"shortpush_url":null,"longpush_url":null,"double_shortpush_url":null,"triple_shortpush_url":null,"shortpush_longpush_url":null,"longpush_shortpush_url":null},
		{"name":null,"btn_type":"momentary","btn_reverse":0,"btn_on_url":null,"btn_off_url":null,"shortpush_url":null,"longpush_url":null,"double_shortpush_url":null,"triple_shortpush_url":null,"shortpush_longpush_url":null,"longpush_shortpush_url":null},
		{"name":null,"btn_type":"momentary","btn_reverse":0,"btn_on_url":null,"btn_off_url":null,"shortpush_url":null,"longpush_url":null,"double_shortpush_url":null,"triple_shortpush_url":null,"shortpush_longpush_url":null,"longpush_shortpush_url":null}
	]
}
```

### /status

```
{	"wifi_sta":{"connected":true,"ssid":"ShellyIoT","ip":"192.168.1.1","rssi":-47},"cloud":{"enabled":false,"connected":false},"mqtt":{"connected":false},"time":"22:26","unixtime":1592951206,"serial":1,"has_update":false,"mac":"A4CF12F47388",
	"inputs":[
		{"input":0,"event":"","event_cnt":0},
		{"input":0,"event":"","event_cnt":0},
		{"input":0,"event":"","event_cnt":0}
	],
	"update":{"status":"idle","has_update":false,"new_version":"20200601-123434/v1.7.0@d7961837","old_version":"20200601-123434/v1.7.0@d7961837"},"ram_total":50296,"ram_free":38880,"ram_lwm":30152,"fs_size":233681,"fs_free":155369,"uptime":1749}
```

### CoAP Description

```
{	"blk":[{"I":0,"D":"Device"}],
	"sen":[
		{"I":118,"T":"S","D":"Input","R":"0/1","L":0},
		{"I":128,"T":"S","D":"Input","R":"0/1","L":0},
		{"I":138,"T":"S","D":"Input","R":"0/1","L":0},
		{"I":119,"T":"S","D":"Input event","R":"S/L/SS/SSS/SL/LS","L":0},
		{"I":129,"T":"S","D":"Input event","R":"S/L/SS/SSS/SL/LS","L":0},
		{"I":139,"T":"S","D":"Input event","R":"S/L/SS/SSS/SL/LS","L":0},
		{"I":120,"T":"S","D":"Input event counter","R":"U16","L":0},
		{"I":130,"T":"S","D":"Input event counter","R":"U16","L":0},
		{"I":140,"T":"S","D":"Input event counter","R":"U16","L":0}
	]
}
```

### CoAP Messahe

```
{"G":[[0,118,0],[0,128,0],[0,138,0],[0,119,""],[0,129,""],[0,139,""],[0,120,0],[0,130,0],[0,140,0]]}
```

## Shelly Bulb

Firmware Version 1.5.2

### Shelly Bulb /settings

```
{	"device":{	"type":"SHBLB-1","mac":"XXXXXXXXXXXX","hostname":"shellybulb-XXXXXX","num_outputs":1},
	"wifi_ap":{	"enabled":false,"ssid":"shellybulb-XXXXXX","key":""},
	"wifi_sta":{	"enabled":true,"ssid":iot-wlan","ipv4_method":"static","ip":"10.0.0.10,"gw":"10.0.0.1","mask":"255.0.0.0","dns":"10.0.0.1"},
	"wifi_sta1":{	"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {	"enable":false,"server":"10.0.0.10:1883","user":"","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive";60,"will_topic":"shellies/shellybulb-XXXXXX/online","will_message":"false","max_qos":0,"retain":false,"update_period":30},
	"sntp": {	"server":"time.google.com"},
	"login":{	"enabled":false,"unprotected":false,"username":"admin","password":"admin"}, "pin_code":"aXJ9eE","coiot_execute_enable":true,"name":"", "fw":"20190821-094813/v1.5.2@4148d2b7",
	"build_info": {	"build_id":"20190821-094813/v1.5.2@4148d2b7","build_timestamp":"2019-08-21T09:48:13Z","build_version":"1.0"},
	"cloud":{	"enabled":true,"connected":true},"timezone":"Europe/Berlin","lat":51.394344,"lng":8.571319,"tzautodetect":false,"time":"10:40",
	"hwinfo": {	"hw_revision":"prod-1.3","batch_id":1},	   
	"mode":"color",
	"lights":[
		 {	"ison":true,"red":255,"green":182,"blue":27,"white":0,"gain":100,"temp":3648,"brightness":86,"effect":0,
			"default_state":"on","auto_on":0.00,"auto_off":0.00,"power":0.00,"schedule":false,"schedule_rules":[]}]
}
```


### Shelly Bulb /status


```
{
	"wifi_sta":{	"connected":true,"ssid":"iot-wlank","ip":"10.0.0.10","rssi":-74},
	"cloud":{	"enabled":true,"connected":true},
	"mqtt":{	"connected":false},"time":"10:41","serial":133,"has_update":false,"mac":"XXXXXXXXXXXX",
	"lights":[
		{	"ison":true,"mode":"color","red":255,"green":182,"blue":27,"white":0,"gain":100,"temp":3648,"brightness":86,"effect":0}],
	"meters":[{	"power":0.00,"is_valid":"true"}],
	"update":{	"status":"idle","has_update":false,"new_version":"20190821-094813/v1.5.2@4148d2b7","old_version":"20190821-094813/v1.5.2@4148d2b7"},
	"ram_total":51032,"ram_free":37420,"fs_size":233681,"fs_free":171182,"uptime":3125051
}
```

### Shelly Bulb Coap Description - Color Mode

```
{   
	"blk":[{	"I":1,"D":"RGBW"}],
	"sen":[
		{	"I":111,"T":"Red","R":"0/255","L":0},
		{	"I":121,"T":"Green","R":"0/255","L":0},
		{	"I":131,"T":"Blue","R":"0/255","L":0},
		{	"I":141,"T":"White","R":"0/255","L":0},
		{	"I":151,"T":"Gain","R":"0/100","L":0},
		{	"I":161,"T":"Temp","R":"3000/6500","L":0},
		{	"I":171,"T":"Brightness","R":"0/100","L":0},
		{	"I":181,"T":"VSwitch","R":"0/1","L":0}
	 ],
	"act":[
		{	"I":211,"D":"RGBW","L":0,"P":[{	"I":2011,"T":"Red","R":"0/255"},
		{	"I":2021,"T":"Green","R":"0/255"},{	"I":2031,"T":"Blue","R":"0/255"},
		{	"I":2041,"T":"White","R":"0/255"},{	"I":2051,"T":"Gain","R":"0/100"},
		{	"I":2061,"T":"Temp","R":"3000/6500"},
		{	"I":2071,"T":"Brightness","R":"0/100"},
		{	"I":2081,"T":"VSwitch","R":"0/1"}]}
	 ]
}
```

### Shelly Bulb Coap Description - White Mode

```
{
	"blk":[{	"I":1,"D":"RGBW"}],
	"sen":[
		{	"I":111,"T":"Red","R":"0/255","L":0},
		{	"I":121,"T":"Green","R":"0/255","L":0},
		{	"I":131,"T":"Blue","R":"0/255","L":0},
		{	"I":141,"T":"White","R":"0/255","L":0},
		{	"I":151,"T":"Gain","R":"0/100","L":0},
		{	"I":161,"T":"Temp","R":"3000/6500","L":0},
		{	"I":171,"T":"Brightness","R":"0/100","L":0},
		{	"I":181,"T":"VSwitch","R":"0/1","L":0}
	], "act":[...]
 }
 ```
 
# Shelly Duo

## Duo /settings
```
{	"device": {	"type": "SHBDUO-1","mac": "BCDDC2663FCC","hostname": "ShellyBulbDuo-XXXXXX","num_outputs": 1},
	"wifi_ap": {	"enabled": false,"ssid": "ShellyBulbDuo-XXXXXX","key": ""},
	"wifi_sta": {	"enabled": true,"ssid": "ShellyIoT","ipv4_method": "dhcp","ip": null,"gw": null,"mask": null,"dns": null},
	"wifi_sta1": {	"enabled": false,"ssid": null,"ipv4_method": "dhcp","ip": null,"gw": null,"mask": null,"dns": null},
	"mqtt": {	"enable": false,"server": "192.168.x.x:1883","user": "","id": "ShellyBulbDuo-XXXXXX","reconnect_timeout_max": 60.0,"reconnect_timeout_min": 2.0,"clean_session": true,"keep_alive": 60,"max_qos": 0,"retain": false,"update_period": 30},
	"coiot": {	"update_period": 15},
	"sntp": {	"server": "time.google.com","enabled": true},
	"login": {	"enabled": false,"unprotected": false,"username": "admin","password": "admin"}, "pin_code": "Q*/lu&", "name": "",
	"fw": "20200224-150831/master@d594e05d", "discoverable": true,
	"build_info": {	"build_id": "20200224-150831/master@d594e05d","build_timestamp": "2020-02-24T15:08:31Z","build_version": "1.0"},
	"cloud": {	"enabled": true,"connected": true}, "timezone": "Europe/Berlin","lat": 51.252491,"lng": 6.779092,"tzautodetect": false,"tz_utc_offset": 3600,"tz_dst": false,"tz_dst_auto": true,"time": "21:58","unixtime": 1582581485,
	"hwinfo": {	"hw_revision": "prod-2019-12","batch_id": 0}, "mode": "white", "transition": 3000,
	"lights": [
		{	"ison": false,"brightness": 22,"white": 0,"temp": 2700,
			"default_state": "last","auto_on": 0.0,"auto_off": 0.0,"schedule": false,"out_on_url": "","out_off_url": "","schedule_rules": []}
	], "night_mode": {	"enabled": 0,"start_time": "00:00","end_time": "00:00","brightness": 0}
}

```

## Duo /status

```
{
	"wifi_sta": {	"connected": true, "ssid": "ShellyIoT","ip": "192.168.x.x","rssi": -64},
	"cloud": {	"enabled": true,"connected": true},
	"mqtt": {	"connected": false}, "time": "21:57","unixtime": 1582581479,"serial": 30,"has_update": true,"mac": "BCDDC2663FCC",
	"lights": [
		{	"ison": false,"has_timer": false,"timer_remaining": 0,"brightness": 22,"white": 0,"temp": 2700}],
	"meters": [
		{	"power": 0.0,"is_valid": "true"} ],
	"update": {	"status": "pending","has_update": true,"new_version": "20200129-155730/master@a18bfaec","old_version": "20200224-150831/master@d594e05d"},
	"ram_total": 50488,"ram_free": 39784,"fs_size": 233681,"fs_free": 164154,"uptime": 19134
}
```

## Duo Coap FW 1.6

```
{
	"blk": [
		{	"I": 0,"D": "Channel0"} ],
	"sen": [
		{	"I": 121,"T": "S","D": "State","R": "0/1","L": 0},
		{	"I": 111,"T": "S","D": "Brightness","R": "0/100","L": 0},
		{	"I": 131,"T": "S","D": "ColorTemperature","R": "2700/6500","L": 0},
		{	"I": 141,"T": "P","D": "Power","R": "0/9","L": 0},
		{	"I": 211,"T": "S","D": "Energy counter 0 [W-min]","L": 0},
		{	"I": 212,"T": "S","D": "Energy counter 1 [W-min]","L": 0},
		{	"I": 213,"T": "S","D": "Energy counter 2 [W-min]","L": 0},
		{	"I": 214,"T": "S","D": "Energy counter total [W-min]","L": 0}
	]
}
```

## Shelly Duo Color Bulb (G10)

### /shelly

{"type":"SHCB-1","mac":"XXXXXXXXXXXX","auth":true,"fw":"20201202-135344/v1.9.3-rc3@50c6ab57","longid":1,"num_outputs":1}

### /settings

```
{	"device":{"type":"SHCB-1","mac":"XXXXXXXXXXXX","hostname":"shellycolorbulb-XXXXXXXXXXXX","num_outputs":1},
	"wifi_ap":{"enabled":false,"ssid":"shellycolorbulb-XXXXXXXXXXXX","key":""},
	"wifi_sta":{"enabled":true,"ssid":"IoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellycolorbulb-8CAAB555DB8B","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15,"execute_enable":false},
	"sntp":{"server":"time.google.com","enabled":true},"login":{"enabled":true,"unprotected":false,"username":"openhab"},"pin_code":"",
	"name":"testbed-duo-rgbw","fw":"20201202-135344/v1.9.3-rc3@50c6ab57","discoverable":true,"build_info":{"build_id":"20201202-135344/v1.9.3-rc3@50c6ab57","build_timestamp":"2020-12-02T13:53:44Z","build_version":"1.0"},"cloud":{"enabled":false,"connected":false},
	"timezone":"Europe/Berlin","lat":49.865711,"lng":8.626040,"tzautodetect":true,"tz_utc_offset":3600,"tz_dst":false,"tz_dst_auto":true,"time":"21:41","unixtime":1608673290,
	"actions":{"active":false,"names":["out_on_url","out_off_url"]},"hwinfo": {"hw_revision":"prod-102020","batch_id":0},"mode":"color",
	"lights":[
		{	"ison":true,
			"red":255,"green":199,"blue":137,"white":0,"gain":90,"temp":4750,"brightness":100,"transition":1000,"effect":0,
			"default_state":"on","auto_on":0.00,"auto_off":0.00,"schedule":false,"schedule_rules":[]}
	],
	"night_mode":{"enabled":false, "start_time":"00:00", "end_time":"00:00", "brightness":0}
}
```

### /status

```
{	"wifi_sta":{"connected":true,"ssid":"TurtleIOB","ip":"192.168.6.177","rssi":-60}, "cloud":{"enabled":false,"connected":false},"mqtt":{"connected":false},
	"time":"21:44","unixtime":1608673494,"serial":12,"has_update":true,"mac":"XXXXXXXXXXXX","cfg_changed_cnt":3,
	"actions_stats":{"skipped":0},
	"lights":[
		{	"ison":true,"source":"http","has_timer":false,"timer_started":0,"timer_duration":0,"timer_remaining":0,
			"mode":"color","red":255,"green":199,"blue":137,"white":0,"gain":90,"temp":4750,"brightness":100,"effect":0}
	],
	"meters":[
		{"power":2.61,"is_valid":true,"timestamp":1608673494,"counters":[2.607, 2.607, 2.607],"total":34}],
		"update":{"status":"pending","has_update":true,"new_version":"20201124-090732/v1.9.0@57ac4ad8","old_version":"20201202-135344/v1.9.3-rc3@50c6ab57","beta_version":"20201202-135344/v1.9.3-rc3@50c6ab57"},"ram_total":50808,"ram_free":38368,"fs_size":233681,"fs_free":156122,
	"uptime":854
}
```

### CoAP Description

```
{
	"blk":[{"I":1,"D":"light_0"},{"I":2,"D":"device"}],
	"sen":[
		{"I":1101,"T":"S","D":"output","R":"0/1","L":1},
		{"I":5105,"T":"S","D":"red","R":"0/255","L":1},
		{"I":5106,"T":"S","D":"green","R":"0/255","L":1},
		{"I":5107,"T":"S","D":"blue","R":"0/255","L":1},
		{"I":5108,"T":"S","D":"white","R":"0/255","L":1},
		{"I":5102,"T":"S","D":"gain","R":"0/100","L":1},
		{"I":5101,"T":"S","D":"brightness","R":"0/100","L":1},
		{"I":5103,"T":"S","D":"colorTemp","U":"K","R":"3000/6500","L":1},
		{"I":9101,"T":"S","D":"mode","R":"color/white","L":1}
	]
}
```


### CoIoT status message

```
{"G":[
	[0,1101,1],
	[0,5105,140],
	[0,5106,255],
	[0,5107,238],
	[0,5108,0],
	[0,5102,90],
	[0,5101,100],
	[0,5103,4750],
	[0,9101,"color"],
	[0,4101,2.79],
	[0,4103,29]]
}
```

## Shelly Vintage

### Shelly Vintage /settings

{
	"device":{"type":"SHVIN-1","mac":"ECFABC6EE918","hostname":"ShellyVintage-6EE918","num_outputs":1},
	"wifi_ap":{"enabled":false,"ssid":"ShellyVintage-6EE918","key":""},"wifi_sta":{"enabled":true,"ssid":"IoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":true,"server":"mqtt.lan:1883","user":"fab_33","id":"ShellyVintage-6EE918","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15},
	"sntp":{"server":"time.google.com","enabled":true},"login":{"enabled":false,"unprotected":false,"username":"admin","password":"admin"},"pin_code":"",
	"name":"","fw":"20200309-103920/v1.6.0@43056d58","discoverable":true,"build_info":{"build_id":"20200309-103920/v1.6.0@43056d58","build_timestamp":"2020-03-09T10:39:20Z","build_version":"1.0"},
	"cloud":{"enabled":false,"connected":false},"timezone":"Europe/Paris","lat":46.580200,"lng":0.340380,"tzautodetect":true,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":true,"time":"14:28","unixtime":1587824908,"hwinfo":{"hw_revision":"prod-2019-12","batch_id":0},
	"mode":"white","transition":5000,
	"lights":[{"ison":true,"brightness":18,"default_state":"last","auto_on":0.00,"auto_off":0.00,"schedule":false,"out_on_url":"","out_off_url":"","schedule_rules":[]}]
	,"night_mode":{"enabled":false, "start_time":"00:00", "end_time":"00:00", "brightness":0}
}
and /settings : 
{
	"wifi_sta":{"connected":true,"ssid":"IoT","ip":"192.168.1.1","rssi":-45},
	"cloud":{"enabled":false,"connected":false},"mqtt":{"connected":true},
	"time":"14:26","unixtime":1587824789,"serial":1,"has_update":false,"mac":"ECFABC6EE918",
	"lights":[{"ison":true,"has_timer":false,"timer_remaining":0,"brightness":100}],
	"meters":[{"power":6.00,"is_valid":"true","timestamp":1587824789,"counters":[6.000, 6.000, 6.000],"total":84}],
	"update":{"status":"idle","has_update":false,"new_version":"20200309-103920/v1.6.0@43056d58","old_version":"20200309-103920/v1.6.0@43056d58"},
	"ram_total":50544,"ram_free":39680,"fs_size":233681,"fs_free":164154,"uptime":900
}

### Shelly Vintage CoAP description

{	"blk":[
		{"I":0,"D":"Channel0"}
	],
	"sen":[
		{"I":121,"T":"S","D":"State","R":"0/1","L":0},
		{"I":111,"T":"S","D":"Brightness","R":"0/100","L":0},
		{"I":141,"T":"P","D":"Power","R":"0/6","L":0},
		{"I":211,"T":"S","D":"Energy counter 0 [W-min]","L":0},
		{"I":212,"T":"S","D":"Energy counter 1 [W-min]","L":0},
		{"I":213,"T":"S","D":"Energy counter 2 [W-min]","L":0},
		{"I":214,"T":"S","D":"Energy counter total [W-min]","L":0}
	]
} 

## Shelly Vintage 

### /Shelly

```
{"type":"SHVIN-1","mac":"98F4ABD0FFE7","auth":false,"fw":"20191216-140157/???","num_outputs":1}
```

### /settings

```
{	"device":{"type":"SHVIN-1","mac":"XXXXXXXXXXXX","hostname":"ShellyVintage-XXXXXX","num_outputs":1},
	"wifi_ap":{"enabled":false,"ssid":"ShellyVintage-D0FFE7","key":""},
	"wifi_sta":{"enabled":true,"ssid":"TurtlePineHouse","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"ShellyVintage-D0FFE7","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"sntp": {"server":"time.google.com"},"login":{"enabled":false,"unprotected":false,"username":"admin","password":"admin"},
	"pin_code":"","name":"","fw":"20191216-140157/???","build_info":{"build_id":"20191216-140157/???","build_timestamp":"2019-12-16T14:01:57Z","build_version":"1.0"},"cloud":{"enabled":false,"connected":false},"timezone":"Europe/Berlin","lat":50.110901,"lng":8.682130,"tzautodetect":true,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":true,"time":"10:02","hwinfo":{"hw_revision":"prod-2019-12","batch_id":0},"mode":"white","transition":1000,"lights":[{"ison":false,"brightness":50,"default_state":"on","auto_on":0.00,"auto_off":0.00,"schedule":false,"schedule_rules":[]}],"night_mode":{"enabled":0, "start_time":"00:00", "end_time":"00:00", "brightness":0}}
```

### CoAP Description

```

```

### CoAP Message

```

```

## Shelly RGBW2 FW 1.5

### Shelly RGW2 /settiings in color mode

```
{	"device":{	"type":"SHRGBW2", "mac":"XXXXXXXXXXXX", "hostname":"shellyrgbw2-XXXXXX", "num_outputs":4 },
	"wifi_ap":{	"enabled":false, "ssid":"shellyrgbw2-XXXXXX", "key":""},
	"wifi_sta":{	"enabled":true,"ssid":"iot-wlan","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{	"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {	"enable":false,"server":"10.0.0.3:1883","user":"","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"will_topic":"","will_message":"","max_qos":0,"retain":false,"update_period":30},
	"sntp": {	"server":"time.google.com"}, "login":{	"enabled":false,"unprotected":false,"xxx":"xxx","password":"xxx"}, "pin_code":"PIpf!A",
	"coiot_execute_enable":false,"name":"", "fw":"20190711-084448/v1.5.0-hotfix4@3b4f7414",
	"build_info":{	"build_id":"20190711-084448/v1.5.0-hotfix4@3b4f7414","build_timestamp":"2019-07-11T08:44:48Z","build_version":"1.0"},
	"cloud":{	"enabled":true,"connected":true}, "timezone":"Europe/Berlin","lat":45.252491,"lng":7.779092,"tzautodetect":false,"time":"07:13",
	"hwinfo": {	"hw_revision":"prod-190410b","batch_id":1},
	"mode":"color","dcpower":1,
	lights":[
		{	"ison":false, "red":255,"green":0,"blue":0,"white":255, "gain":29, "effect":0, "default_state":"off", "auto_on":0.00,"auto_off":0.00, "schedule":false, "btn_type":"detached", "btn_reverse":0,"schedule_rules":[] } ]
}
```

### RGW2 /status color mode

```
{
	"wifi_sta":{	"connected":true,"ssid":"iot-wlan","ip":"10.0.0.100","rssi":-69},
	"cloud":{	"enabled":true,"connected":true},
	"mqtt":{	"connected":false},"time":"07:12", "serial":112, "has_update":false,"mac":"XXXXXXXXXXXX", "mode":"color", "input":0,
	"lights":[
		{	"ison":false,"mode":"color", "red":255,"green":0,"blue":0,"white":255, "gain":29,"effect":0, "power":0.00,"overpower":false}],
	"meters":[
		{	"power":0.00,"is_valid":true}
	],
	"update":{	"status":"idle","has_update":false,"new_version":"20190711-084448/v1.5.0-hotfix4@3b4f7414","old_version":"20190711-084448/v1.5.0-hotfix4@3b4f7414"},
	"ram_total":50448,"ram_free":35824,"fs_size":233681,"fs_free":162648,"uptime":30380
}
```
### RGBW2 Color Mode Coap Description FW 1.6

```
{
	"blk": [
		{	"I": 0,"D": "RGBW2"} ],
	"sen": [
		{	"I": 118,"T": "S","D": "Input","R": "0/1","L": 0},
		{	"I": 111,"T": "S","D": "Red","R": "0/255","L": 0},
		{	"I": 121,"T": "S","D": "Green","R": "0/255","L": 0},
		{	"I": 131,"T": "S","D": "Blue","R": "0/255","L": 0},
		{	"I": 141,"T": "S","D": "White","R": "0/255","L": 0},
		{	"I": 151,"T": "S","D": "Gain","R": "0/100","L": 0},
		{	"I": 161,"T": "S","D": "VSwitch","R": "0/1","L": 0},
		{	"I": 211,"T": "P","D": "Power","R": "0/288","L": 0}
	]
}
```

### RGBW2 /settings in white mode

```
{	"device":{	"type":"SHRGBW2","mac":"XXXXXXXXXXXX","hostname":"shellyrgbw2-XXXXXX","num_outputs":4},
	"wifi_ap":{	"enabled":false,"ssid":"shellyrgbw2-XXXXXX","key":""},
	"wifi_sta":{	"enabled":true,"ssid":"iot_wlan","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{	"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {	"enable":false,"server":"10.0.0.1:1883","user":"","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"will_topic":"","will_message":"","max_qos":0,"retain":false,"update_period":30},
	"sntp": {	"server":"time.google.com"}, "login":{	"enabled":false,"unprotected":false,"username":"xxx","password":"xxx"}, "pin_code":"PIpf!A",
	"coiot_execute_enable":false, "name":"", "fw":"20190711-084448/v1.5.0-hotfix4@3b4f7414",
	"build_info":{	"build_id":"20190711-084448/v1.5.0-hotfix4@3b4f7414","build_timestamp":"2019-07-11T08:44:48Z","build_version":"1.0"},
	"cloud":{	"enabled":true,"connected":true}, "timezone":"Europe/Berlin","lat":45.252491,"lng":7.779092,"tzautodetect":false,"time":"20:33",
	"hwinfo": {	"hw_revision":"prod-190410b","batch_id":1}, "mode":"white", "dcpower":1,
	"lights":[
		{	"ison":true,"brightness":50,
			"default_state":"on","auto_on":0.00,"auto_off":0.00,"schedule":false,"btn_type":"detached","btn_reverse":0,"schedule_rules":[]},
		{	"ison":false,"brightness":50,
			"default_state":"on","auto_on":0.00,"auto_off":0.00,"schedule":false,"schedule_rules":[]},
		{	"ison":false,"brightness":50,
			"default_state":"on","auto_on":0.00,"auto_off":0.00,"schedule":false,"schedule_rules":[]},
		{	"ison":false,"brightness":50,
			"default_state":"on","auto_on":0.00,"auto_off":0.00,"schedule":false,"schedule_rules":[] } ]
}
```

### RGBW2 set/statustings in white mode

```
{
	"wifi_sta":{	"connected":true,"ssid":"iot_wlan","ip":"10.0.0.100","rssi":-36},
	"cloud":{	"enabled":true,"connected":true},
	"mqtt":{	"connected":false},"time":"20:33","serial":42, "has_update":false,"mac":"XXXXXXXXXXXX","mode":"white","input":0,
	"lights":[
		{	"ison":true,"mode":"white","brightness":50,"power":1.20,"overpower":false},
		{	"ison":false,"mode":"white","brightness":50,"power":0.00,"overpower":false},
		{	"ison":false,"mode":"white","brightness":50,"power":0.00,"overpower":false},
		{	"ison":false,"mode":"white","brightness":50,"power":0.00,"overpower":false}],
	"meters":[
		{	"power":1.20,"is_valid":true},
		{	"power":0.00,"is_valid":true},
		{	"power":0.00,"is_valid":true},
		{	"power":0.00,"is_valid":true}
	],
	"update":{	"status":"idle","has_update":false,"new_version":"20190711-084448/v1.5.0-hotfix4@3b4f7414","old_version":"20190711-084448/v1.5.0-hotfix4@3b4f7414"},
	"ram_total":50448,"ram_free":35360,"fs_size":233681,"fs_free":162648, "uptime":7201
}
```

### RGBW2 White Mode Coap Descritpion FW 1.6

```
{
	"blk": [
		{	"I": 0,"D": "RGBW2"} ],
	"sen": [
		{	"I": 118,"T": "S","D": "Input","R": "0/1","L": 0},
		{	"I": 111,"T": "S","D": "Brightness_0","R": "0/100","L": 0},
		{	"I": 121,"T": "S","D": "Brightness_1","R": "0/100","L": 0},
		{	"I": 131,"T": "S","D": "Brightness_2","R": "0/100","L": 0},
		{	"I": 141,"T": "S","D": "Brightness_3","R": "0/100","L": 0},
		{	"I": 151,"T": "S","D": "VSwitch_0","R": "0/1","L": 0},
		{	"I": 161,"T": "S","D": "VSwitch_1","R": "0/1","L": 0},
		{	"I": 171,"T": "S","D": "VSwitch_2","R": "0/1","L": 0},
		{	"I": 181,"T": "S","D": "VSwitch_3","R": "0/1","L": 0},
		{	"I": 211,"T": "P","D": "Power_0","R": "0/288","L": 0},
		{	"I": 221,"T": "P","D": "Power_1","R": "0/288","L": 0},
		{	"I": 231,"T": "P","D": "Power_2","R": "0/288","L": 0},
		{	"I": 241,"T": "P","D": "Power_3","R": "0/288","L": 0}
	]
}
```


## Shelly Gas, FW 1.7.0 (SHGS-1)

### /shelly
```
{	"type":"SHGS-1","mac":"XXXXXXXXXXXX","auth":true,"fw":"20200601-123447/v1.7.0@d7961837","longid":1,"num_inputs":0, "num_outputs":0}
```

### /settings

```
{	"device":{"type":"SHGS-1", "mac":"50029178B51F", "hostname":"shellygas-XXXXXXXXXXXX",
	"num_inputs":0, "num_outputs":0},
	"wifi_ap":{"enabled":false,"ssid":"shellygas-XXXXXXXXXXXX","key":""},
	"wifi_sta":{"enabled":true,"ssid":"xxx", "ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt":{"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellygas-50029178B51F","reconnect_timeout_max":60.000000, "reconnect_timeout_min":2.000000, "clean_session":true, "keep_alive":60, "max_qos":0, "retain":false, "update_period":30},
	"coiot":{ "update_period":15},
	"sntp":{"server":"192.168.6.137","enabled":true},
	"login":{"enabled":true,"unprotected":false,"username":"openhab"},
	"pin_code":"","name":"","fw":"20200601-123447/v1.7.0@d7961837","discoverable":true,
	"build_info":{"build_id":"20200601-123447/v1.7.0@d7961837","build_timestamp":"2020-06-01T12:34:47Z","build_version":"1.0"},
	"cloud":{"enabled":false,"connected":false},
	"timezone":"Europe/Berlin","lat":500.000000,"lng":500.000000,"tzautodetect":true,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":true,"time":"","unixtime":0,
	"hwinfo":{"hw_revision":"prod-2020-04-09","batch_id":1},
	
	"set_volume":11,
	"alarm_off_url":null,
	"alarm_mild_url":null,
	"alarm_heavy_url":null
}
```

### /status

```
{	"wifi_sta":{"connected":true,"ssid":"IoT","ip":"192.168.1.1"","rssi":-87},
	"cloud":{"enabled":false,"connected":false},
	"mqtt":{"connected":false},
	"time":"","unixtime":0,"serial":1,"has_update":false,"mac":"XXXXXXXXXXXX",

	"gas_sensor":{"sensor_state":"normal","self_test_state":"not_completed","alarm_state":"none"},
	"concentration":{"ppm":0,"is_valid":true},

	"update":{"status":"idle","has_update":false,"new_version":"20200601-123447/v1.7.0@d7961837","old_version":"20200601-123447/v1.7.0@d7961837"},
	"ram_total":50528,"ram_free":39676,"ram_lwm":33440,"fs_size":233681,"fs_free":134787,"uptime":171646
}
```

### CoaAP Description

```
{	"blk":[{"I":0,"D":"Device"}],
	"sen":[
		{"I":118,"T":"S","D":"Sensor state","R":"unknown/warmup/normal/fault","L":0},
		{"I":119,"T":"S","D":"Alarm state","R":"unknown/none/mild/heavy/test","L":0},
		{"I":120,"T":"S","D":"Self-test state","R":"not_completed/completed/running/pending","L":0},
		{"I":122,"T":"S","D":"Concentration (ppm)","R":"0-65535","L":0}
	]
}

{"G":[[0,118,"normal"],[0,119,"none"],[0,120,"not_completed"],[0,122,0]]}
```

### CoAP Description 1.8
{
	"blk":[
		{"I":1,"D":"sensor_0"},
		{"I":2,"D":"valve_0"},
		{"I":3,"D":"device"}
	],
	"sen":[
		{"I":9103,"T":"EVC","D":"cfgChanged","R":"U16","L":3},
		{"I":3113,"T":"S","D":"sensorOp","R":["warmup/normal/fault","unknown"],"L":1},
		{"I":3114,"T":"S","D":"selfTest","R":"not_completed/completed/running/pending","L":1},
		{"I":6108,"T":"A","D":"gas","R":["none/mild/heavy/test","unknown"],"L":1},
		{"I":3107,"T":"C","D":"concentration","U":"ppm","R":["U16","-1"],"L":1},
		{"I":1105,"T":"S","D":"valve","R":["closed/opened/not_connected/failure/closing/opening/checking","unknown"],"L":2}
	]
}


## Shelly Button (SHBTN-1) FW 1.7

### Shelly Button /shelly
```
{"type":"SHBTN-1","mac":"XXXXXXXXXXXX","auth":true,"fw":"20200601-123213/v1.7.0@d7961837","longid":1,"sleep_mode":true}
```

### Shelly Button /settings

```
{	"device":{"type":"SHBTN-1","mac":"XXXXXXXXXXXX","hostname":"shellybutton1-XXXXXXXXXXXX","sleep_mode":true},
	"wifi_ap":{"enabled":false,"ssid":"shellybutton1-A4CF12F44E99","key":""},
	"wifi_sta":{"enabled":true,"ssid":"IoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellybutton1-A4CF12F44E99","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15},"sntp":{"server":"time.google.com","enabled":true},
	"login":{"enabled":true,"unprotected":false,"username":"openhab"},"pin_code":"",
	"name":"","fw":"20200601-123213/v1.7.0@d7961837","discoverable":true,"build_info":{"build_id":"20200601-123213/v1.7.0@d7961837","build_timestamp":"2020-06-01T12:32:13Z","build_version":"1.0"},
	"cloud":{"enabled":false,"connected":false},"timezone":"Europe/Berlin","lat":500.000000,"lng":500.000000,"tzautodetect":true,"tz_utc_offset":7200,"tz_dst":true,"tz_dst_auto":true,"time":"","unixtime":0,
	"hwinfo":{"hw_revision":"prod-2020-04","batch_id":1},
	"sleep_mode":{"period":12,"unit":"h"},
	"led_status_disable":false,
	"longpush_duration_ms":{"max":800},"multipush_time_between_pushes_ms":{"max":500},
	"remain_awake":0,
	"inputs":[
		{"shortpush_url":"","double_shortpush_url":"","triple_shortpush_url":"","longpush_url":""}
	]
}
```

### Shelly Button /status

```
{	"wifi_sta":{"connected":true,"ssid":"IoT","ip":"192.168.1.1","rssi":-63},
	"cloud":{"enabled":false,"connected":false},"mqtt":{"connected":false},
	"time":"","unixtime":0,"serial":1,"has_update":false,"mac":"XXXXXXXXXXXX",
	"inputs":[
		{"input":0,"event":"S","event_cnt":1}
	], 
	"is_valid":true,
	"bat":{"value":100,"voltage":4.06},
	"charger":true,
	"act_reasons":["ext_power"],
	"connect_retries":0,
	"update":{"status":"idle","has_update":false,"new_version":"20200601-123213/v1.7.0@d7961837","old_version":"20200601-123213/v1.7.0@d7961837"},
	"ram_total":50312,"ram_free":39496,"ram_lwm":34576,"fs_size":233681,"fs_free":162648,"uptime":308
}
```

### Shelly Button CoAP Description

```
{	"blk":[{"I":1, "D":"sensors"}],
	"sen":[
		{"I":77, "D":"battery", "T":"B", "R":"0/100", "L":1}
		{"I":118,"T":"S","D":"Input","R":"0","L":1},
		{"I":119,"T":"S","D":"Input event","R":"S/SS/SSS/L","L":1},
		{"I":120,"T":"S","D":"Input event counter","R":"U16","L":1}
	]
}
```

### Shelly Button CoAP Message

```
"G":[[0,77,100],[0,118,0],[0,119,"S"],[0,120,3]]}
```

## Shelly H&T FW 1.6

### H&T /settings

```
{	"device":{"type":"SHHT-1","mac":"84F3EBE01AD9","hostname":"shellyht-E01AD9",
			"sleep_mode":true},
	"wifi_ap":{"enabled":false,"ssid":"shellyht-E01AD9","key":""},
	"wifi_sta":{"enabled":true,"ssid":"ShellyIoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellyht-E01AD9","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15},
	"sntp":{"server":"time.google.com","enabled":true},"login":{"enabled":false,"unprotected":false,"username":"admin","password":"admin"},"pin_code":"wbnkw{",
	"name":"","fw":"20200306-151016/v1.6.0-rc6@43056d58","discoverable":true,
	"build_info":{"build_id":"20200306-151016/v1.6.0-rc6@43056d58","build_timestamp":"2020-03-06T15:10:16Z","build_version":"1.0"},
	"cloud":{"enabled":false,"connected":false},
	"timezone":"America/New_York","lat":500.000000,"lng":500.000000,"tzautodetect":true,"tz_utc_offset":0,"tz_dst":false,"tz_dst_auto":true,"time":"","unixtime":0,
	"sensors":{"temperature_threshold":1.0,"temperature_unit":"C","humidity_threshold":5.0},
	"sleep_mode":{"period":12,"unit":"h"},
	"report_url":"",
	"external_power":0,
	"temperature_offset":0.0,"humidity_offset":0.0
}
```

### H&T /status

```
{
	"wifi_sta":{"connected":true,"ssid":"ShellyIoT","ip":"192.168.6.88","rssi":-59},
	"cloud":{"enabled":false,"connected":false},
	"mqtt":{"connected":false},
	"time":"","unixtime":0,"serial":1,"has_update":false,"mac":"84F3EBE01AD9",
	"is_valid":true,
	"tmp":{"value":24.62,"units":"C","tC":24.62,"tF":76.32, "is_valid":true},
	"hum":{"value":39.0, "is_valid":true},"bat":{"value":97,"voltage":2.93},
	"act_reasons":["button"],"connect_retries":0,
	"update":{"status":"unknown","has_update":false,"new_version":"","old_version":"20200306-151016/v1.6.0-rc6@43056d58"},
	"ram_total":50432,"ram_free":40032,"fs_size":233681,"fs_free":156122,"uptime":122
}
```

## HT&T CoAP Descr

```
{	"blk":[
		{"I":1, "D":"sensors"}
	],
	"sen":[
		{"I":33, "D":"temperature", "T":"T", "R":"-40/125", "L":1},
		{"I":44, "D":"humidity", "T":"H", "R":"0/100", "L":1},
		{"I":77, "D":"battery", "T":"B", "R":"0/100", "L":1}]
	}
```

## Shelly Flood

### Shelly Flood /settings

```
{
	“device”:{“type”:“SHWT-1”,“mac”:“XXXXXXXX”,“hostname”:“shellyflood-XXXXXX”,“sleep_mode”:true},
	“wifi_ap”:{“enabled”:false,“ssid”:“shellyflood-XXXXXX”,“key”:""},
	“wifi_sta”:{“enabled”:true,“ssid”:“iot-wlan”,“ipv4_method”:“dhcp”,“ip”:null,“gw”:null,“mask”:null,“dns”:null},
	“wifi_sta1”:{“enabled”:false,“ssid”:null,“ipv4_method”:“dhcp”,“ip”:null,“gw”:null,“mask”:null,“dns”:null},
	“mqtt”: {“enable”:true,“server”:“XXXXXX”,“user”:“XXXXXX”,“reconnect_timeout_max”:60.000000,“reconnect_timeout_min”:2.000000,“clean_session”:true,“keep_alive”:60,“will_topic”:“shellies/shellyflood-XXXXXX/online”,“will_message”:“false”,“max_qos”:0,“retain”:false,“update_period”:30},
	“sntp”: {“server”:“time.google.com”},login”:{“enabled”:true,“unprotected”:false,“username”:“XXXXXXX”,“password”:“XXXXXX”},“pin_code”:"",“coiot_execute_enable”:false,
	“name”:"",“fw”:“20190821-095233/v1.5.2@4148d2b7”,“build_info”:{“build_id”:“20190821-095233/v1.5.2@4148d2b7”,“build_timestamp”:“2019-08-21T09:52:33Z”,“build_version”:“1.0”},
	“cloud”:{“enabled”:false,“connected”:false},“timezone”:“Europe/Berlin”,“lat”:50.110901,“lng”:8.682130,“tzautodetect”:true,“time”:“11:09”,
	“sensors”:{
		“temperature_threshold”:1.0,
		“temperature_unit”:“C”
 },
	“sleep_mode”:{“period”:24,“unit”:“h”},
	“report_url”:null, 
	“rain_sensor”:false
}
```

### Shelly Flood /status

```
{
	“wifi_sta”:{“connected”:true,“ssid”:“XXXXX”,“ip”:"XXXXXXXX“,“rssi”:-88},
	“cloud”:{“enabled”:false,“connected”:false},“mqtt”:{“connected”:true},
	“time”:“11:07”,“serial”:1,“has_update”:false,“mac”:“XXXXXXXXXXXX”,
	“is_valid”:true,
	“flood”:false,
	“tmp”:{“value”:21.62,“units”:“C”,“tC”:21.62,“tF”:70.93, “is_valid”:true},
	“bat”:{“value”:95,“voltage”:2.92},“act_reasons”:[“button”], 
	“rain_sensor”:false,
	“update”:{“status”:“idle”,“has_update”:false,“new_version”:“20190821-095233/v1.5.2@4148d2b7”,“old_version”:“20190821-095233/v1.5.2@4148d2b7”},
	“ram_total”:50592,“ram_free”:39632,“fs_size”:233681,“fs_free”:154365,“uptime”:22
}
```



# COAP API JSON

## Shelly Door / Window

### Shelly DW /settings

```
{	"device":{"type":"SHDW-1","mac":"98F4ABF3B8A1","hostname":"shellydw-F3B8A1","sleep_mode":true},
	"wifi_ap":{"enabled":false,"ssid":"shellydw-F3B8A1","key":""},
	"wifi_sta":{"enabled":true,"ssid":"Headquarter","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellydw-F3B8A1","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15},"sntp":{"server":"time.google.com","enabled":true},
	"login":{"enabled":false,"unprotected":false,"username":"admin"},"pin_code":"",
	"name":"","fw":"20200408-085810/v1.6.5@b075f0bf","discoverable":false,
	"build_info":{"build_id":"20200408-085810/v1.6.5@b075f0bf","build_timestamp":"2020-04-08T08:58:10Z","build_version":"1.0"},
	"cloud":{"enabled":true,"connected":true},"timezone":"Europe/Vienna","lat":500.000000,"lng":500.000000,"tzautodetect":true,"tz_utc_offset":0,"tz_dst":false,"tz_dst_auto":true,"time":"","unixtime":0,
	"dark_threshold":100,"twilight_threshold":300,
	"sleep_mode":{"period":6,"unit":"h"},
	"led_status_disable":false,
	"dark_url":"http://192.168.1.3:8080/shelly/event/shellydw-f3b8a1/sensordata",
	"twilight_url":"http://192.168.1.3:8080/shelly/event/shellydw-f3b8a1/sensordata",
	"close_url":null,"vibration_url":null,
	"tilt_enabled":false,
	"tilt_calibrated":true,"vibration_enabled":false,"reverse_open_close":false
}
```


### Shelly DW /status
output when door in OPEN state: 

```
{
	“wifi_sta”:{“connected”:true,“ssid”:“IoT”,“ip”:“10.1.1.1i”:-55}, “cloud”:{“enabled”:true,“connected”:true},
	“mqtt”:{“connected”:false},“time”:"",“unixtime”:0,“serial”:3,“has_update”:false,“mac”:“98F4ABF3BCEB”,
	“is_valid”:true,
	“lux”:{“value”:11, “illumination”: “dark”, “is_valid”:true},
	“accel”:{“tilt”:-1, “vibration”:0},
	“sensor”:{“state”:“open”, “is_valid”:true},
	“bat”:{“value”:100,“voltage”:5.96},
	“act_reasons”:[“button”],
	“update”:{“status”:“idle”,“has_update”:false,“new_version”:“20200408-085810/v1.6.5@b075f0bf”,“old_version”:“20200408-085810/v1.6.5@b075f0bf”},
	“ram_total”:50240,“ram_free”:38916,“fs_size”:233681,“fs_free”:154867,“uptime”:97
}
```

### Shelly DW CoAP Desc

```
{	“blk”:[
		{	“I”:1, “D”:“sensors”}
	],
	“sen”:[
		{“I”:66, “D”:“lux”, “T”:“L”, “R”:“0/100000”, “L”:1},
		{“I”:55, “D”:“State”, “T”:“S”, “R”:“0/1”, “L”:1},
		{“I”:77, “D”:“battery”, “T”:“B”, “R”:“0/100”, “L”:1},
		{“I”:88, “D”:“tilt”, “T”:“S”, “R”:“0/180”, “L”:1},
		{“I”:99, “D”:“vibration”, “T”:“S”, “R”:“0/1”, “L”:1}
	]
}
```

Output when in closed state:

```
	...
	“is_valid”:true,
	“lux”:{“value”:5, “illumination”: “dark”, “is_valid”:true},
	“sensor”:{“state”:“close”, “is_valid”:true},
	“bat”:{“value”:100,“voltage”:6.38},
	“act_reasons”:[“button”],
	...
```

status values
```
state: open, close
illumination:  dark, twilight, bright
```


## Shelly Door Window 2

### /shelly, FW 1.8 beta

{"type":"SHDW-2","mac":"XXXXXXXXXXXX","auth":false,"fw":"20200612-144015/master@a3eeb1a1+","longid":1,"sleep_mode":true}

### /settings, FW 1.8 beta

{	"device":{"type":"SHDW-2","mac":"XXXXXXXXXXXX","hostname":"shellydw2-XXXXXXXXXXXX","sleep_mode":true},
	"wifi_ap":{"enabled":false,"ssid":"shellydw2-XXXXXXXXXXXX","key":""},
	"wifi_sta":{"enabled":true,"ssid":"ShellyIoT","ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"wifi_sta1":{"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {"enable":false,"server":"192.168.33.3:1883","user":"","id":"shellydw2-A4CF12F42DE5","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"max_qos":0,"retain":false,"update_period":30},
	"coiot": {"update_period":15},"sntp":{"server":"time.google.com","enabled":true},
	"login":{"enabled":false,"unprotected":false,"username":"admin"},"pin_code":"","name":null,
	"fw":"20200612-144015/master@a3eeb1a1+",
	"discoverable":true,
	"build_info":{"build_id":"20200612-144015/master@a3eeb1a1+","build_timestamp":"2020-06-12T14:40:15Z","build_version":"1.0"},
	"cloud":{"enabled":false,"connected":false},
	"timezone":"Europe/Berlin","lat":500.000000,"lng":500.000000,"tzautodetect":true,"tz_utc_offset":0,"tz_dst":false,"tz_dst_auto":true,"time":"","unixtime":0,
	"dark_threshold":100,"twilight_threshold":300,"sleep_mode":{"period":6,"unit":"h"},
	"led_status_disable":true,
	"dark_url":null,"twilight_url":null,"close_url":null,"vibration_url":null,"tilt_enabled":false,"tilt_calibrated":false,
	"vibration_enabled":true,"reverse_open_close":false,
	"sensors":{"temperature_threshold":1.0,"temperature_unit":"C"},
	"temp_over_url":null, "temp_over_value":0.0, "temp_over_onetime":true,"temp_under_url":null, "temp_under_value":0.0, "temp_under_onetime":true,"temperature_offset":0.0
}

## Shelly DW 2 Coap Description, FW 1.8 Beta

{	"blk":[
		{"I":1,"D":"sensor_0"},
		{"I":2,"D":"device"}],
	"sen":[
		{"I":3108,"T":"S","D":"dwIsOpened","R":["0/1","-1"],"L":1},
		{"I":3109,"T":"S","D":"tilt","U":"deg","R":["0/180","-1"],"L":1},
		{"I":6110,"T":"A","D":"vibration","R":["0/1","-1"],"L":1},
		{"I":3106,"T":"L","D":"luminosity","U":"lux","R":["U32","-1"],"L":1},
		{"I":3110,"T":"S","D":"luminosityLevel","R":["dark/twilight/bright","unknown"],"L":1},
		{"I":3101,"T":"T","D":"extTemp","U":"C","R":["-55/125","999"],"L":1},
		{"I":3102,"T":"T","D":"extTemp","U":"F","R":["-67/257","999"],"L":1},
		{"I":3115,"T":"S","D":"sensorError","R":"0/1","L":1},
		{"I":3111,"T":"B","D":"battery","R":["0/100","-1"],"L":2},
		{"I":9102,"T":"EV","D":"wakeupEvent","R":["battery/button/periodic/poweron/sensor/alarm","unknown"],"L":2}
	]
}

### Shelly DW2 CoAP message, FW 1.8 beta

```
{"G":[[0,3108,1],[0,3109,-1],[0,6110,0],[0,3106,5],[0,3110,"dark"],,[0,3101,28.20],[0,3102,82.76],[0,3115,0],[0,3111,93],[0,9102,["poweron"]]]}
```

# Shelly Sense

### Shelly Sense: /settings

```
{	"device":{	"type":"SHSEN-1","mac":"XXXXXXXXXXXX","hostname":"shellysense-XXXXXX"},
	"wifi_ap":{	"enabled":false,"ssid":"shellysense-XXXXXX","key":""},
	"wifi_sta":{	"enabled":true,"ssid":"iot-wlan","ipv4_method":"static","ip":"10.0.0.2","gw":"10.0.0.1","mask":"255.255.255.0","dns":"10.0.0.1"},
	"wifi_sta1":{	"enabled":false,"ssid":null,"ipv4_method":"dhcp","ip":null,"gw":null,"mask":null,"dns":null},
	"mqtt": {	"enable":false,"server":"10.0.0.10:1883","user":"","reconnect_timeout_max":60.000000,"reconnect_timeout_min":2.000000,"clean_session":true,"keep_alive":60,"will_topic":"shellies/shellysense-XXXXXXX/online","will_message":"false","max_qos":0,"retain":false,"update_period":30},
	"Sntp": {	"server":"time.google.com"},
	"login":{	"enabled":false,"unprotected":false,"username":"admin","password":"admin"},"pin_code":"bS^Mtq",
	"coiot_execute_enable":true,"name":"","fw":"20190821-095017/v1.5.2@4148d2b7",
	"build_info": {	"build_id":"20190821-095017/v1.5.2@4148d2b7","build_timestamp":"2019-08-21T09:50:17Z","build_version":"1.0"},
	"cloud": {	"enabled":true,"connected":true},
	"timezone":"Europe/Berlin","lat":51.394344,"lng":8.571319,"tzautodetect":false,"time":"19:32",
	"light_sensor":"NOA1305",
	"schedule":false,"schedule_rules":[],
	"sensors":{	"motion_duration":20,"motion_led":false,"temperature_unit":"C"}
}
```

### Shelly Sense /status

```
{	"wifi_sta":{"connected":true,"ssid":"NickyGhz","ip":"10.0.0.1","rssi":-61},"
	cloud":{"enabled":true,"connected":true},"mqtt":{"connected":false},
	"time":"11:31","unixtime":1589455864,"serial":33,"has_update":false,"mac":"D436394B3FA4",
	"motion":false,"charger":false,
	"tmp":{"value":26.109139,"is_valid":true,"units":"C"},
	"hum":{"value":76.739057,"is_valid":true},
	"lux":{"value":40.259740,"is_valid":true},
	"bat":{"value":100},
	"update":{"status":"idle","has_update":false,"new_version":"20200309-104151/v1.6.0@43056d58","old_version":"20200309-104151/v1.6.0@43056d58"},
	"ram_total":48020,"ram_free":23956,"fs_size":83081,"fs_free":21837,"uptime":2928
}
```


### Shelly Sense /ir/list

```
[
	["1_231_pwr","tv(231) - Power"],["1_231_chdwn","tv(231) - Channel Down"],["1_231_chup","tv(231) - Channel Up"],["1_231_voldwn","tv(231) - Volume Down"],
	["1_231_volup","tv(231) - Volume Up"],["1_231_mute","tv(231) - Mute"],["1_231_menu","tv(231) - Menu"],["1_231_inp","tv(231) - Input"],["1_231_info","tv(231) - Info"],
	["1_231_left","tv(231) - Left"],["1_231_up","tv(231) - Up"],["1_231_right","tv(231) - Right"],["1_231_ok","tv(231) - OK"],["1_231_down","tv(231) - Down"],
	["1_231_back","tv(231) - Back"],["6_546_pwr","receiver(546) - Power"],["6_546_voldwn","receiver(546) - Volume Down"],["6_546_volup","receiver(546) - Volume Up"],
	["6_546_mute","receiver(546) - Mute"],["6_546_menu","receiver(546) - Menu"],["6_546_info","receiver(546) - Info"],["6_546_left","receiver(546) - Left"],
	["6_546_up","receiver(546) - Up"],["6_546_right","receiver(546) - Right"],["6_546_ok","receiver(546) - OK"],["6_546_down","receiver(546) - Down"],["6_546_back","receiver(546) - Back"]
]
```

###  Shelly Sense Coap Description

```
{
	"blk":[{	"I":1, "D":"sensors"}],
	"sen":[
		{	"I":11, "D":"motion", "T":"S", "R":"0/1", "L":1},
		{	"I":22, "D":"charger", "T":"S", "R":"0/1", "L":1},
		{	"I":33, "D":"temperature", "T":"T", "R":"-40/125", "L":1},
		{	"I":44, "D":"humidity", "T":"H", "R":"0/100", "L":1},
		{	"I":66, "D":"lux", "T":"L", "R":"0/1", "L":1},
		{	"I":77, "D":"battery", "T":"H", "R":"0/100", "L":1}
	 ]
}
```


# Shelly Plug FW 1.6

## Plug /settings

```
{	"device": {	"type": "SHPLG-1","mac": "000000000000","hostname": "shellyplug-000000","num_outputs": 1,"num_meters": 1},
	"wifi_ap": {	"enabled": false,"ssid": "shellyplug-000000","key": ""},
	"wifi_sta": {	"enabled": true,"ssid": "SSID","ipv4_method": "dhcp","ip": null,"gw": null,"mask": null,"dns": null},
	"wifi_sta1": {	"enabled": false,"ssid": "Gastzugang","ipv4_method": "dhcp","ip": null,"gw": null,"mask": null,"dns": null},
	"mqtt": {	"enable": false,"server": "192.168.33.3:1883","user": "","id": "shellyplug-000000","reconnect_timeout_max": 60,"reconnect_timeout_min": 2,"clean_session": true,"keep_alive": 60,"max_qos": 0,"retain": false,"update_period": 30},
	"coiot": {	"update_period": 15},
	"sntp": {	"server": "time.google.com","enabled": true},
	"login": {	"enabled": false,"unprotected": false,"username": "admin","password": "admin"}, "pin_code": "000000","name": "","fw": "20200228-153215/v1.6.0-rc4@a7a0b9f1", "discoverable": true,
	"build_info": {	"build_id": "20200228-153215/v1.6.0-rc4@a7a0b9f1","build_timestamp": "2020-02-28T15:32:15Z","build_version": "1.0"},
	"cloud": {	"enabled": true,"connected": true}, "timezone": "Europe/Berlin","lat": 0.000000,"lng": 0.000000,"tzautodetect": true,"tz_utc_offset": 3600,"tz_dst": false,"tz_dst_auto": true,"time": "19:14","unixtime": 1583262881,
	"hwinfo": {	"hw_revision": "dev-prototype","batch_id": 0}, "max_power": 3500, "relays": [
		{	"ison": true,"has_timer": false,"overpower": false,
			"default_state": "on","auto_on": 0,"auto_off": 0,"btn_on_url": null,"out_on_url": null,"out_off_url": null,"schedule": false,"schedule_rules": [],"max_power": 3500}],
	"meters": [
		{	"power": 1.66,"is_valid": true,"timestamp": 1583262881,"counters":[0,0,0],"total": 423973}
	]
}
```

## Plug /status

```
	{	"wifi_sta": { "connected": true, "ssid": "SSID", "ip": "192.168.0.100", "rssi": -63},
		"cloud": { "enabled": true, "connected": true},
		"mqtt": { "connected": false},
		"time": "19:15", "unixtime": 1583262930, "serial": 9206, "has_update": true, "mac": "000000000000",
		"relays": [
			{"ison": true, "has_timer": false, "timer_remaining": 0, "overpower": false}],
		"meters": [	{"power": 1.66, "is_valid": true, "timestamp": 1583262930,
					"counters": [0,0,0], "total": 423974}],
	"update": { "status": "pending", "has_update": true, "new_version": "20191216-090203/v1.5.7@c30657ba", "old_version": "20200228-153215/v1.6.0-rc4@a7a0b9f1"},
	"ram_total": 60840, "ram_free": 38880, "fs_size": 83081, "fs_free": 18323, "uptime": 301039
	}
```

## Plug Coap /cit/d FW 1.6

```
{
	"blk": [
		{	"I": 0,"D": "Relay0"} ],
	"sen": [
		{	"I": 111,"T": "P","D": "Power","R": "0/2500","L": 0},
		{	"I": 112,"T": "S","D": "State","R": "0/1","L": 0},
		{	"I": 113,"T": "T","D": "Temperature C","R": "-40/300","L": 0},
		{	"I": 114,"T": "T","D": "Temperature F","R": "-40/300","L": 0},
		{	"I": 115,"T": "S","D": "Overtemp","R": "0/1","L": 0},
		{	"I": 211,"T": "S","D": "Energy counter 0 [W-min]","L": 0},
		{	"I": 212,"T": "S","D": "Energy counter 1 [W-min]","L": 0},
		{	"I": 213,"T": "S","D": "Energy counter 2 [W-min]","L": 0},
		{	"I": 214,"T": "S","D": "Energy counter total [W-min]","L": 0}
	]
}
```

## Plug Coap /cit/s FW 1.6

```
{
	"G": [
		[0,111,0.0],
		[0,112,0],
		[0,113,33.57],
		[0,114,92.42],
		[0,115,0],
		[0,211,0.0],
		[0,212,0.0],
		[0,213,0.0],
		[0,214,390] ]
}
```

# Shelly Sense FW 1.6

## Sense /settings

```
{	"device": {	"type": "SHSEN-1", "mac": "000000000000", "hostname": "shellysense-000000" },
	"wifi_ap": {	"enabled": false, "ssid": "shellysense-000000", "key": "" },
	"wifi_sta": {	"enabled": true, "ssid": "SSID", "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"wifi_sta1": {	"enabled": false, "ssid": null, "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"mqtt": {	"enable": false, "server": "192.168.33.3:1883", "user": "", "id": "shellysense-000000", "reconnect_timeout_max": 60, "reconnect_timeout_min": 2, "clean_session": true, "keep_alive": 60, "max_qos": 0, "retain": false, "update_period": 30 },
	"coiot": {	"update_period": 15 },
	"sntp": {	"server": "time.google.com", "enabled": true },
	"login": {	"enabled": false, "unprotected": false, "username": "admin", "password": "admin" },
	"pin_code": "000000", "name": "",
	"fw": "20200228-153247/v1.6.0-rc4@a7a0b9f1", "discoverable": true,
	"build_info": {	"build_id": "20200228-153247/v1.6.0-rc4@a7a0b9f1", "build_timestamp": "2020-02-28T15:32:47Z", "build_version": "1.0" },
	"cloud": {	"enabled": true, "connected": true },
	"timezone": "Europe/Berlin", "lat": 0.0, "lng": 0.0, "tzautodetect": true, "tz_utc_offset": 3600, "tz_dst": false, "tz_dst_auto": true, "time": "19:10", "unixtime": 1583262620, "light_sensor": "NOA1305", "schedule": false, "schedule_rules": [], "sensors": {	"motion_duration": 60, "motion_led": false, "temperature_unit": "C"
	}
}
```

## Sense /status

```
{
	"wifi_sta": {	"connected": true, "ssid": "SSID", "ip": "192.168.0.100", "rssi": -68 },
	"cloud": {	"enabled": true, "connected": true },
	"mqtt": {	"connected": false },
	"time": "19:11", "unixtime": 1583262717, "serial": 30214, "has_update": true, "mac": "000000000000",
	"motion": true, "charger": false,
	"tmp": {	"value": 24.389915, "is_valid": true, "units": "C" },
	"hum": {	"value": 42.28691, "is_valid": true },
	"lux": {	"value": 0, "is_valid": true },
	"bat": {	"value": 99 },
	"update": {	"status": "pending", "has_update": true, "new_version": "20191216-090234/v1.5.7@c30657ba", "old_version": "20200228-153247/v1.6.0-rc4@a7a0b9f1" },
	"ram_total": 48020, "ram_free": 23804, "fs_size": 83081, "fs_free": 21335, "uptime": 300222
}
```

## Sense Coap /cit/d

```
{
	"blk": [
		{	"I": 1,"D": "Sensors"} ],
	"sen": [
		{	"I": 11,"D": "Motion","T": "M","R": "0/1","L": 1},
		{	"I": 22,"D": "Charger","T": "S","R": "0/1","L": 1},
		{	"I": 33,"D": "Temperature","T": "T","R": "-40/125","L": 1},
		{	"I": 44,"D": "Humidity","T": "H","R": "0/100","L": 1},
		{	"I": 66,"D": "Lux","T": "L","R": "0/1","L": 1},
		{	"I": 77,"D": "Battery","T": "B","R": "0/100","L": 1}
	]
}
```

## Sense Coap /cit/s

```
{
	"G": [
		[0,11,1],
		[0,22,0],
		[0,33,24.389915],
		[0,44,42.46324],
		[0,66,0.0],
		[0,77,99] ]
}
```

# Shelly Bulb FW 1.6

## Bulb /settings

```
{	"device": {	"type": "SHBLB-1", "mac": "000000000000", "hostname": "shellybulb-000000", "num_outputs": 1 },
	"wifi_ap": {	"enabled": false, "ssid": "shellybulb-000000", "key": "" },
	"wifi_sta": {	"enabled": true, "ssid": "SSID", "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"wifi_sta1": {	"enabled": false, "ssid": null, "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"mqtt": {	"enable": false, "server": "192.168.33.3:1883", "user": "", "id": "shellybulb-000000", "reconnect_timeout_max": 60, "reconnect_timeout_min": 2, "clean_session": true, "keep_alive": 60, "max_qos": 0, "retain": false, "update_period": 30 },
	"coiot": {	"update_period": 15 },
	"sntp": {	"server": "time.google.com", "enabled": true },
	"login": {	"enabled": false, "unprotected": false, "username": "admin", "password": "admin" },
	"pin_code": "000000", "name": "",
	"fw": "20200228-153004/v1.6.0-rc4@a7a0b9f1", "discoverable": true,
	"build_info": {	"build_id": "20200228-153004/v1.6.0-rc4@a7a0b9f1", "build_timestamp": "2020-02-28T15:30:04Z", "build_version": "1.0" },
	"cloud": {	"enabled": true, "connected": true },
	"timezone": "Europe/Berlin", "lat": 0.0, "lng": 0.0, "tzautodetect": true, "tz_utc_offset": 3600, "tz_dst": false, "tz_dst_auto": true, "time": "19:09", "unixtime": 1583262550,
	"hwinfo": {	"hw_revision": "prod-1.3", "batch_id": 1 },
	"mode": "color",
	"lights": [
		{	"ison": false,"red": 255,"green": 0,"blue": 0,"white": 0,"gain": 100,"temp": 4750,"brightness": 100,"effect": 0,
			"default_state": "off","auto_on": 0,"auto_off": 0,"power": 0,"schedule": false,"schedule_rules": []}
	]
}
```

## Bulb /status

```
{
	"wifi_sta": {	"connected": true, "ssid": "SSID", "ip": "192.168.0.100", "rssi": -84 },
	"cloud": {	"enabled": true, "connected": true },
	"mqtt": {	"connected": false },
	"time": "19:08", "unixtime": 1583262492, "serial": 4, "has_update": true, "mac": "000000000000",
	"lights": [
		{	"ison": false,"has_timer": false,"timer_remaining": 0,"mode": "color","red": 255,"green": 0,"blue": 0,"white": 0,"gain": 100,"temp": 4750,"brightness": 100,"effect": 0}],
	"meters": [
		{	"power": 0,"is_valid": "true"}
	],
	"update": {	"status": "pending", "has_update": true, "new_version": "20191216-090015/v1.5.7@c30657ba", "old_version": "20200228-153004/v1.6.0-rc4@a7a0b9f1" },
	"ram_total": 50816, "ram_free": 36956, "fs_size": 233681, "fs_free": 164907, "uptime": 264421
}
```

## Bulb Coap /cit/d

```
{
	"blk": [
		{	"I": 0,"D": "RGBW"} ],
	"sen": [
		{	"I": 111,"T": "S","D": "Red","R": "0/255","L": 0},
		{	"I": 121,"T": "S","D": "Green","R": "0/255","L": 0},
		{	"I": 131,"T": "S","D": "Blue","R": "0/255","L": 0},
		{	"I": 141,"T": "S","D": "White","R": "0/255","L": 0},
		{	"I": 151,"T": "S","D": "Gain","R": "0/100","L": 0},
		{	"I": 161,"T": "S","D": "Temp","R": "3000/6500","L": 0},
		{	"I": 171,"T": "S","D": "Brightness","R": "0/100","L": 0},
		{	"I": 181,"T": "S","D": "VSwitch","R": "0/1","L": 0}
	]
}
```

## Bulb Coap /cit/s

```
{
	"G": [
		[0,111,255],
		[0,121,0],
		[0,131,0],
		[0,141,0],
		[0,151,100],
		[0,161,4750],
		[0,171,100],
		[0,181,0] ]
}
```

# Shelly EM FW 1.6

## EM /settings

```
{	"device": {	"type": "SHEM", "mac": "000000000000", "hostname": "shellyem-000000", "num_outputs": 1, "num_meters": 0, "num_emeters": 2 },
	"wifi_ap": {	"enabled": false, "ssid": "shellyem-000000", "key": "" },
	"wifi_sta": {	"enabled": true, "ssid": "SSID", "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"wifi_sta1": {	"enabled": false, "ssid": null, "ipv4_method": "dhcp", "ip": null, "gw": null, "mask": null, "dns": null },
	"mqtt": {	"enable": false, "server": "192.168.33.3:1883", "user": "", "id": "shellyem-000000", "reconnect_timeout_max": 60, "reconnect_timeout_min": 2, "clean_session": true, "keep_alive": 60, "max_qos": 0, "retain": false, "update_period": 30 },
	"coiot": {	"update_period": 15 },
	"sntp": {	"server": "time.google.com", "enabled": true },
	"login": {	"enabled": false, "unprotected": false, "username": "admin", "password": "admin" },
	"pin_code": "000000", "name": "",
	"fw": "20200228-153702/v1.6.0-rc4@a7a0b9f1", "discoverable": true,
	"build_info": {	"build_id": "20200228-153702/v1.6.0-rc4@a7a0b9f1", "build_timestamp": "2020-02-28T15:37:02Z", "build_version": "1.0" },
	"cloud": {	"enabled": true, "connected": true },
	"timezone": "Europe/Berlin", "lat": 0.0, "lng": 0.0, "tzautodetect": true, "tz_utc_offset": 3600, "tz_dst": false, "tz_dst_auto": true, "time": "19:13", "unixtime": 1583262781,
	"dual_phases_mode": false,
	"hwinfo": {	"hw_revision": "prod-2019-06", "batch_id": 0 },
	"max_power": 0, "relays": [
		{	"name": null,"ison": false,"has_timer": false,"overpower": false,
			"default_state": "off","auto_on": 0,"auto_off": 0,"max_power": 0,"out_on_url": null,"out_off_url": null,"schedule": false,"schedule_rules": []}
	], "emeters": [
		{	"ctraf_type": 50},
		{	"ctraf_type": 50}
	]
}
```

## EM /status

```
{
	"wifi_sta": {	"connected": true, "ssid": "SSID", "ip": "192.168.0.100", "rssi": -72 },
	"cloud": {	"enabled": true, "connected": true },
	"mqtt": {	"connected": false },
	"time": "19:12", "unixtime": 1583262750, "serial": 719, "has_update": true, "mac": "000000000000", "relays": [
		{	"ison": false,"has_timer": false,"timer_remaining": 0,"overpower": false,"is_valid": true}
	], "emeters": [
		{	"power": 0,"reactive": 0,"voltage": 232.83,"is_valid": true,"total": 51365.1,"total_returned": 0},
		{	"power": 0,"reactive": 0,"voltage": 232.83,"is_valid": true,"total": 0,"total_returned": 0}
	],
	"update": {	"status": "pending", "has_update": true, "new_version": "20200206-083637/v1.5.10@e6a4205e", "old_version": "20200228-153702/v1.6.0-rc4@a7a0b9f1" },
	"ram_total": 49744, "ram_free": 33760, "fs_size": 233681, "fs_free": 161393, "uptime": 300106
}
```

## EM Coap /cit/d

```
{
	"blk": [
		{	"I": 0,"D": "Relay0"},
		{	"I": 1,"D": "Device"} ],
	"sen": [
		{	"I": 112,"T": "S","D": "State","R": "0/1","L": 0},
		{	"I": 111,"T": "W","D": "Power","R": "0/2300","L": 0},
		{	"I": 121,"T": "W","D": "Power","R": "0/2300","L": 1}
	]
}
```

## EM Coap /cit/s

```
{
	"G": [
		[0,112,0],
		[0,111,0.0],
		[0,121,0.0] ]
}
```

## Shelly UNI

### /shelly

```
{"type":"SHUNI-1","mac":"483FDA82BEDD","auth":false,"fw":"20201007-085802/v1.8.5@5fbcbaa3","longid":1,"num_outputs":2}
```

### /settings 

```
{
{
   "device":{ "type":"SHUNI-1", "mac":"483FDA82BEDD", "hostname":"shellyuni-483FDA82BEDD", "num_outputs":2 },
   "wifi_ap":{ "enabled":false, "ssid":"shellyuni-483FDA82BEDD", "key":"" },
   "wifi_sta":{ "enabled":true, "ssid":"TurtlePineHouse", "ipv4_method":"dhcp", "ip":null, "gw":null, "mask":null, "dns":null },
   "wifi_sta1":{ "enabled":false, "ssid":null, "ipv4_method":"dhcp", "ip":null, "gw":null, "mask":null, "dns":null },
   "mqtt":{ "enable":false, "server":"192.168.33.3:1883", "user":"", "id":"shellyuni-483FDA82BEDD", "reconnect_timeout_max":60.000000, "reconnect_timeout_min":2.000000, "clean_session":true, "keep_alive":60, "max_qos":0, "retain":false, "update_period":30 },
   "coiot":{ "update_period":15 },
   "sntp":{ "server":"time.google.com", "enabled":true },
   "login":{ "enabled":false, "unprotected":false, "username":"admin" }, "pin_code":"",
   "name":"shelly-uni-testbed", "fw":"20201120-112906/v1.9.0-rc3@0890b23e", "factory_reset_from_switch":true, "discoverable":true,
   "build_info":{ "build_id":"20201120-112906/v1.9.0-rc3@0890b23e", "build_timestamp":"2020-11-20T11:29:06Z", "build_version":"1.0" },
   "cloud":{ "enabled":false,"connected":false },
   "timezone":"Europe/Berlin", "lat":50.110901, "lng":8.682130, "tzautodetect":true, "tz_utc_offset":3600, "tz_dst":false, "tz_dst_auto":true, "time":"19:24", "unixtime":1605986669,
   "actions":{ "active":false, "names":[	"out_on_url","out_off_url","btn_on_url","btn_off_url","longpush_url","shortpush_url","out_on_url","out_off_url","btn_on_url","btn_off_url","longpush_url",
      			"shortpush_url","adc_over_url","adc_under_url","ext_temp_over_url","ext_temp_under_url","ext_temp_over_url","ext_temp_under_url","ext_temp_over_url",
      			"ext_temp_under_url","ext_temp_over_url","ext_temp_under_url","ext_temp_over_url","ext_temp_under_url","ext_hum_over_url","ext_hum_under_url"
      ]
    },
   "hwinfo":{ "hw_revision":"prod-202008", "batch_id":0},
   "mode":"relay",
   "longpush_time":800,
   "relays":[
      {	"name":null,"appliance_type":"General","ison":false,"has_timer":false,"default_state":"off","btn_type":"toggle","btn_reverse":0,"auto_on":0.00,"auto_off":0.00,
      	"schedule":false,"schedule_rules":[]},
      {"name":null,"appliance_type":"General","ison":false,"has_timer":false,"default_state":"off","btn_type":"toggle","btn_reverse":0,"auto_on":0.00,"auto_off":0.00, "schedule":false,"schedule_rules":[]}
   ],
   "adcs":[
      {	"range":30,"relay_actions":[
            { "over_threshold":0, "over_act":"disabled", "under_threshold":0, "under_act":"disabled"},
            { "over_threshold":0, "over_act":"disabled", "under_threshold":0, "under_act":"disabled"}
        ]
      }
   ],
   "ext_sensors":{ },
   "ext_temperature":{ },
   "ext_humidity":{
   }
}
```

### /status

```
{
	"wifi_sta":{"connected":true,"ssid":"openHAB","ip":"192.168.1.1","rssi":-57},
	"cloud":{"enabled":false,"connected":false},"mqtt":{"connected":false},"time":"19:37",
	"unixtime":1605987466,"serial":1,"has_update":true,"mac":"483FDA82BEDD",
	"cfg_changed_cnt":0,"actions_stats":{"skipped":0},
	"relays":[
		{"ison":false,"has_timer":false,"timer_started":0,"timer_duration":0,"timer_remaining":0,"source":"input"},
		{"ison":false,"has_timer":false,"timer_started":0,"timer_duration":0,"timer_remaining":0,"source":"input"}
	],
	"inputs":[
		{"input":0},
		{"input":0}
	],
	"adcs":[
		{"voltage":0.00}
	],
	"ext_sensors":{},
	"ext_temperature":{},
	"ext_humidity":{},
	"update":{"status":"pending","has_update":true,"new_version":"20200826-111450/1206-production-ShellyUNI@a80ee1fc+","old_version":"20201120-112906/v1.9.0-rc3@0890b23e"},
	"ram_total":49944,"ram_free":37244,"fs_size":233681,"fs_free":140811,"uptime":1055
}
```

### Coap Description

```
{
	"blk":[
		{"I":1,"D":"relay_0"},
		{"I":2,"D":"relay_1"},
		{"I":3,"D":"adc_0"},
		{"I":4,"D":"sensor_0"},
		{"I":5,"D":"sensor_1"},
		{"I":6,"D":"sensor_2"},
		{"I":7,"D":"sensor_3"},
		{"I":8,"D":"sensor_4"},
		{"I":9,"D":"device"}
	],
	"sen":[
		{"I":9103,"T":"EVC","D":"cfgChanged","R":"U16","L":9},
		{"I":1101,"T":"S","D":"output","R":"0/1","L":1},
		{"I":1201,"T":"S","D":"output","R":"0/1","L":2},
		{"I":2101,"T":"S","D":"input","R":"0/1","L":1},
		{"I":2102,"T":"EV","D":"inputEvent","R":["S/L",""],"L":1},
		{"I":2103,"T":"EVC","D":"inputEventCnt","R":"U16","L":1},
		{"I":2201,"T":"S","D":"input","R":"0/1","L":2},
		{"I":2202,"T":"EV","D":"inputEvent","R":["S/L",""],"L":2},
		{"I":2203,"T":"EVC","D":"inputEventCnt","R":"U16","L":2},
		{"I":3118,"T":"V","D":"adc","U":"V","R":["0/30","-1"],"L":3},
		{"I":3101,"T":"T","D":"extTemp","U":"C","R":["-55/125","999"],"L":4},
		{"I":3201,"T":"T","D":"extTemp","U":"C","R":["-55/125","999"],"L":5},
		{"I":3301,"T":"T","D":"extTemp","U":"C","R":["-55/125","999"],"L":6},
		{"I":3401,"T":"T","D":"extTemp","U":"C","R":["-55/125","999"],"L":7},
		{"I":3501,"T":"T","D":"extTemp","U":"C","R":["-55/125","999"],"L":8},{
		"I":3103,"T":"H","D":"humidity","R":["0/100","999"],"L":4}
	]
}
```

## Firmware Listing

URL:  https://api.shelly.cloud/files/firmware

```
{
	"isok":true, "data":{
		"SHPLG-1":{	"url":"http://api.shelly.cloud/firmware/SHPLG-1_build.zip","version":"20191216-090203/v1.5.7@c30657ba"},
		"SHPLG-S":{	"url":"http://api.shelly.cloud/firmware/SHPLG-S_build.zip","version":"20200206-083538/v1.5.10@e6a4205e"},
		"SHPLG2-1":{	"url":"http://api.shelly.cloud/firmware/SHPLG2-1_build.zip","version":"20200206-083551/v1.5.10@e6a4205e"},
		"SHSW-1":{	"url":"http://api.shelly.cloud/firmware/SHSW-1_build.zip","version":"20200206-083100/v1.5.10@e6a4205e"},
		"SHSW-21":{	"url":"http://api.shelly.cloud/firmware/SHSW-21_build.zip","version":"20191216-090122/v1.5.7@c30657ba"},
		"SHSW-25":{	"url":"http://api.shelly.cloud/firmware/SHSW-25_build.zip","version":"20200206-083126/v1.5.10@e6a4205e"},
		"SHSW-PM":{	"url":"http://api.shelly.cloud/firmware/SHSW-PM_build.zip","version":"20200206-083604/v1.5.10@e6a4205e"},
		"SHSW-22":{	"url":"http://api.shelly.cloud/firmware/SHSW-22_build.zip","version":"20191216-090340/v1.5.7@c30657ba"},
		"SHSW-44":{	"url":"http://api.shelly.cloud/firmware/SHSW-44_build.zip","version":"20191216-090307/v1.5.7@c30657ba"},
		"SHEM":{	"url":"http://api.shelly.cloud/firmware/SHEM_build.zip","version":"20200206-083637/v1.5.10@e6a4205e"},
		"SHEM-3":{	"url":"http://api.shelly.cloud/firmware/SHEM-3_build.zip","version":"20200205-183800/master@fa9ba943"},
		"SHSEN-1":{	"url":"http://api.shelly.cloud/firmware/SHSEN-1_build.zip","version":"20191216-090234/v1.5.7@c30657ba"},
		"SHSM-01":{	"url":"http://api.shelly.cloud/firmware/SHSM-01_build.zip","version":"20191216-090406/v1.5.7@c30657ba"},
		"SHHT-1":{	"url":"http://api.shelly.cloud/firmware/SHHT-1_build.zip","version":"20191216-090428/v1.5.7@c30657ba"},
		"SHWT-1":{	"url":"http://api.shelly.cloud/firmware/SHWT-1_build.zip","version":"20191216-090450/v1.5.7@c30657ba"},
		"SHDW-1":{	"url":"http://api.shelly.cloud/firmware/SHDW-1_build.zip","version":"20191216-090511/v1.5.7@c30657ba"},
		"SHCL-255":{	"url":"http://api.shelly.cloud/firmware/compatibility/SHCL-255/0.2.0-73a40eef.zip","version":"20180116-161402/v0.2.0@73a40eef"},
		"SHBLB-1":{	"url":"http://api.shelly.cloud/firmware/SHBLB-1_build.zip","version":"20191216-090015/v1.5.7@c30657ba"},
		"SHRGBW2":{	"url":"http://api.shelly.cloud/firmware/SHRGBW2_build.zip","version":"20191216-090536/v1.5.7@c30657ba"},
		"SHRGBWW-01":{	"url":"http://api.shelly.cloud/firmware/SHRGBWW-01_build.zip","version":"20191216-090043/v1.5.7@c30657ba"},
		"SH2LED-1":{	"url":"http://api.shelly.cloud/firmware/SH2LED-1_build.zip","version":"20191216-090056/v1.5.7@c30657ba"},
		"SHDM-1":{	"url":"http://api.shelly.cloud/firmware/SHDM-1_build.zip","version":"20200206-083625/v1.5.10@e6a4205e"},
		"SHBDUO-1":{	"url":"http://api.shelly.cloud/firmware/SHBDUO-1_build.zip","version":"20200129-155730/master@a18bfaec"}
	}
}
```

