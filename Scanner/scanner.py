#!/usr/bin/python3
import iw
import sys
import time
import json
import requests

# scanner.py <net interface> <location> <n> <sleep>

if len(sys.argv) != 6:
    print("Usage: scanner.py <net interface> <location> <n> <sleep> <ssid>")
    sys.exit(1)

net = sys.argv[1]
location = sys.argv[2]
n = int(sys.argv[3])
sleep = int(sys.argv[4])
ssid = sys.argv[5]

observations = {}

def upload(data):
    url = "http://127.0.0.1/camcam/?key=JayDoesn'tKnowWhatATomTomIs"
    myobj = {'data': data}

    x = requests.post(url, data = myobj)

    print(x.text)

def sendObservation(observation):
	#data = json.dumps({
    #		"location": observation.location,
	#	"stations": observation.stations
	#})
	
	#print(data, end="\n\n")
	upload(observation)

for i in range(0, n):
    print("Scanning...", i, end="\r")
    networks = iw.get_interfaces(interface=net)

    observation = {
        "location": location,
        "stations": []
    }

    for station in networks:
        if station["Name"] == ssid:

            observation["stations"].append([
                station["Address"], 
                station["Signal Level"]
            ])

 

    sendObservation(observation)


    time.sleep(sleep)






