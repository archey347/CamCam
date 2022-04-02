import sys
import getopt
import json

class Observation:
	def __init__(self, location, stations) -> None:
		self.location = location;
		self.stations = stations;

def printUsage():
	print("scanner.py -l <location> -n <network>")

def scan():
	fileContents = ""

	with open("data.txt", 'r') as f:
		fileContents = f.readlines();

	return fileContents[2:]

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

		

targetSsid = "eduroam"
if __name__ == "__main__":

	try:
		opts, args = getopt.getopt(sys.argv[1:], "hl:n:", ["location=", "network="])
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

	scanResult = scan();
	stations = getStations(scanResult)

	if (len(stations) > 0):
		observation = Observation(location, stations)
	