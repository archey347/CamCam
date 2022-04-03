import numpy as np 
np.random.seed(42)
import tensorflow as tf
import pandas as pd


def hexTtoInt(number):
    number.replace("-", "")
    number.replace(":", "")

    return int(number, 16)


#tf.keras.backend.clear_session

import json
# GET THE DATA
with open('file.json') as json_file:
    data = json.load(json_file)

# Get Unique rooms and Mac ADDRESSES
locations = []
out_class = []
mac_addresses = [] 

for i in range(len(data)):
    out_class.append(data[i]["location"])
    if not data[i]['location'] in locations:
        #print(data[i]["location"])
        locations.append(data[i]["location"])

    for j in range(len(data[i]["stations"])):
        if not data[i]["stations"][j][0] in mac_addresses:
            mac_addresses.append(data[i]["stations"][j][0])

with open('MAC.json', "w") as outfile:
    json.dump(mac_addresses, outfile)



rows, cols = (len(data), len(mac_addresses))
observations=[]
for i in range(rows):
    col = []
    for j in range(cols):
        col.append(-100)
    observations.append(col)



print("Next bit\n")

for i in range(len(data)):
    for k in range(len(data[i]["stations"])):
        if data[i]["stations"][k][0] in mac_addresses:
            for j in range(len(mac_addresses)): 
                if mac_addresses[j] == data[i]["stations"][k][0]:
                    observations[i][j] = data[i]["stations"][k][1]
                    break

lbls =[]
for i in range(len(mac_addresses)):
    lbls.append(i)

obs = pd.DataFrame(observations, columns=lbls)

obs["location"] = out_class
 
X = obs.copy().drop(["location"], axis=1)
 
def fn (adresses):
    for i in range(len(adresses)):
        mapping = {adresses[i] : i}
    return mapping
fn(locations)

print(locations)

# DEFINE THE OUTPUT CLASS
Y = obs.copy()["location"]
Y = obs.copy()["location"].map({"LIBRARY 2":0, "LIBRARY 3":1, "LIBRARY 4": 2, "LIBRARY 5": 3, "BRENDON": 4, "1W 3":5,
                                "1W 2.101":6, "1W 2.102":7, "1W 2.103": 8, "1W 2.104": 9, "3WN 3.T": 10, "3WN 3.8":11,
                                "3WN 2.1":12, "LAKE":13, "PARADE": 14, "3 PARADE": 15, "-3 PARADE": 16, "1WN 3":17,
                                "3E":18,  "FRESH":19           }).values

# ENCODE OUTPUT CLASS
codes = {"LIBRARY 2":0, "LIBRARY 3":1, "LIBRARY 4": 2, "LIBRARY 5": 3, "BRENDON": 4, "1W 3":5,
                                "1W 2.101":6, "1W 2.102":7, "1W 2.103": 8, "1W 2.104": 9, "3WN 3.T": 10, "3WN 3.8":11,
                                "3WN 2.1":12, "LAKE":13, "PARADE": 14, "3 PARADE": 15, "-3 PARADE": 16, "1WN 3":17,
                                "3E":18,  "FRESH":19           }
obs["location"] = obs["location"].map(codes)
                                        

# SPLIT INTO TRAINIG AND TEST AND DEVELOPMENT
from sklearn.model_selection import train_test_split

X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, 
                                                    train_size=0.8, random_state=42)
X_train, X_dev, y_train, y_dev = train_test_split(X_train, y_train, 
                                                  test_size=0.2, train_size=0.8, 
                                                  random_state=42)
#print(X_train.shape())

# PREPROCESS THE DATA A BIT
from sklearn.impute import SimpleImputer 
from sklearn import preprocessing
print("lesgo")
imputer = SimpleImputer(strategy="median")
imputer.fit(X_train)
print("qui")

X_train = imputer.transform(X_train)
X_dev = imputer.transform(X_dev)
X_test = imputer.transform(X_test)

scaler = preprocessing.StandardScaler().fit(X_train)


X_train = scaler.transform(X_train)
X_dev = scaler.transform(X_dev)
X_test = scaler.transform(X_test)


data_input = tf.constant(X_train)
data_output = tf.constant(y_train)
print(y_train.shape,"\n\n")

# MODEL FROM ML 5
tf.keras.backend.clear_session

reg_model = tf.keras.models.Sequential([
    tf.keras.layers.Dense(60, activation="relu", input_shape=X_train.shape[1:]),
    tf.keras.layers.Dense(100, activation="relu"),
    tf.keras.layers.Dense(60, activation="relu"),
    tf.keras.layers.Dense(len(locations), activation="softmax")
])

reg_model.compile(loss="sparse_categorical_crossentropy", metrics=['accuracy'],
                  optimizer=tf.keras.optimizers.SGD(learning_rate=0.075))
history = reg_model.fit(X_train, y_train, epochs=50, validation_data=(X_dev, y_dev))
mse_test = reg_model.evaluate(X_test, y_test)

converter = tf.lite.TFLiteConverter.from_keras_model(reg_model)
tflite_model = converter.convert()

# Save the model.
with open('model.tflite', 'wb') as f:
  f.write(tflite_model)
"""
NOTES:
- look into number of hidden layers
- lokk into number of neurons per layer
- look into activation fnctions
- look into optimizer
- look into learning rate
"""

#print("Paul Coddio")