import os
import subprocess
import sys
import getopt
import json
import time

class Observation:
	def __init__(self, location, stations) -> None:
		self.location = location;
		self.stations = stations;

def printUsage():
	print("scanner.py -l <location> -n <network>")

def scan():
	os.system("wpa_cli scan")
	return subprocess.check_output("wpa_cli scan_result")

def getStations(fileContents):
	stations = []
	for line in fileContents:
		line = line.split('\t')

		bssid = line[0]
		signalLevel = line[2]
		ssid = line[4].replace("\n", "")

		bssidInt = int(bssid.replace(":", ""), 16)

		if (ssid == targetSsid):
			station = [bssidInt, signalLevel]
			stations.append(station)

		return stations

def sendObservation(observation):
	pass

targetSsid = "eduroam"
scanDuration = 5

if __name__ == "__main__":
	try:
		opts, args = getopt.getopt(sys.argv[1:], "hdl:n:", ["duration=", "location=", "network="])
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
		elif (opt in ("-d", "--duration")):
			scanDuration = arg

	
	end = time.time() + scanDuration
	while (time.time() < end):
		scanResult = scan()
		stations = getStations(scanResult)

	if (len(stations) > 0):
		observation = Observation(location, stations)

	sendObservation(observation)
	