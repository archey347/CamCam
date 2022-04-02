#!/usr/bin/python3
import os
import subprocess
import sys
import getopt
import json
import time
import iw_parse


def upload(data):
    url = "https://camcam.wwlrc.co.uk/?key=JayDoesn'tKnowWhatATomTomIs"
    myobj = {'data': data}

    x = requests.post(url, data = myobj)

    print(x.text)

def sendObservation(observation):
	data = json.dumps({
		"location": observation.location,
		"stations": observation.stations
	})
	
	print(data, end="\n\n")
	#upload(data)

class Observation:
	def __init__(self, location, stations) -> None:
		self.location = location
		self.stations = stations


def printUsage():
	print("scanner.py -l <location> -n <network>")

def scan():
	subprocess.check_output(["wpa_cli", "scan"])
	output = subprocess.check_output(["/usr/sbin/wpa_cli", "scan_result"]).decode(sys.stdout.encoding)

	output = output.split("\n")

	return output[2:]

def getStations(fileContents):
	stations = []
	for line in fileContents:
		print(line)
		line = line.split('\t')

		bssid = line[0]
		signalLevel = line[2]
		ssid = line[4]

		bssidInt = int(bssid.replace(":", ""), 16)

		if (ssid == targetSsid):
			station = [bssidInt, signalLevel]
			stations.append(station)

		return stations



targetSsid = "eduroam"
scanDuration = 10
location = "UNKNOWN"

if __name__ == "__main__":
	try:
		opts, args = getopt.getopt(sys.argv[1:], "hdl:n:", ["amount=", "location=", "network="])
	except getopt.GetoptError:
		printUsage()
		sys.exit(1)

	for opt, arg in opts:
		if (opt == "-h"):
			printUsage();
			sys.exit()
		elif (opt in ("-l", "--location")):
			location = arg
		elif (opt in ("-n", "--network")):
			targetSsid = arg
		elif (opt in ("-d", "--amount")):
			scanDuration = arg

	observations = []

	
	#end = time.time() + scanDuration
	#while (time.time() < end):

	for i in range(0, scanDuration):
		print("Scanning...", i, end="\r")
		
		scanResult = scan()
		if(len(scanResult) == 0):
			continue

		stations = getStations(scanResult)

		if (len(stations) > 0):
			observation = Observation(location, stations)


		time.sleep(1)

		sendObservation(observation)


		observations.append(observation)

	for observation in observations:
		sendObservation(observation)
	