# CamCam
(A play on TomTom)

An app that uses the signal strength of nearby WiFi base stations to find your location on a campus.

## App
Built with Android Studio, it allows you to see a map of the University of Bath campus and tell you the building (and potentially even the room) you're standing in.

It gets a list of available nearby stations and passes them into a TensorFlow model packaged with the app, which classifies your location.

## Server
A PHP server servicing database requests. This is how the neural net gets its training data from the network scanner.

## Neural Net
Built with TensorFlow in python, it uses a labelled data set collected from the scanning app to train a location classifier.
It's then exported as a tflite model for better performance on device.

## Scanner
Originally a python script run on Linux, it used the output from `wpa_cli` and `iwlist` to determine nearby signals and their signal strength. It then uploaded these to the server to be retrieved by the TensorFlow model.
It was later incorporated into the main app, and adapted to use WifiManager on android to get the network information.
