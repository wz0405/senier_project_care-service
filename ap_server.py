# Thread loop is Server ----> Rasp ----> Arduino
# main loop is Arduino ----> Rasp ---->  Server

import paho.mqtt.client as mqtt
import random

import serial
import time

import threading

import sys

temper = 0
is_onResend = 0
is_onSend = 0
on_message_check = 0
dataStr = "none"
preDataStr = "none"


# --- Lora function
def sendData(data="none"):
    eui = "001c3e"

    cmd = "AT+DATA=" + eui + ":" + data

    ser.write(cmd.encode())
    ser.write("\n".encode())
    ser.flush()


# --- multi thread definition
# That is Server ----> Rasp ----> Arduino

emer = -1
prevEmer = -1

lon = -1.00000000
prevLon = -1.00000000

lat = -1.00000000
prevLat = -1.00000000

# step is integer
# temper is float
step = -1
temper = -1.0


def main_thread(agrx, agry):
    global on_message_check, dataStr
    global is_onResend, is_onSend

    while True:
        global emer, prevEmer
        global lat, prevLat
        global lon, prevLon

        str = ser.readline()
        print str,

        result = str.find(STR_FAIL)
        result_suc = str.find("SUCCESS")
        result_rec = str.find("RECV:")  # number of char is 5

        # result_rec == -1 means that RECV: doesn't appear
        # result_rec != -1 means that RECV: appear
        if result_rec != -1:
            recvStr = str.split("#")
            # print "::split start::"
            # print recvStr

            # print recvStr[0]

            # print recvStr[1]
            # print recvStr[2]
            # print recvStr[3]
            # print recvStr[4]

            lat = recvStr[1]
            lon = recvStr[2]

            emer = recvStr[3]

            step = recvStr[4]

            temper = recvStr[5]

            # When emer is 1, transmit
            if emer != prevEmer and emer == '1':
                (result_pub, m_id) = mqttc.publish("/data/emer", emer)
            # the lon is not changed!

            mqttc.publish("/data/lat", lat)
            mqttc.publish("/data/lon", lon)
            mqttc.publish("/data/step", step)
            mqttc.publish("/data/temper", temper)

            prevEmer = emer

            # sendData(temper)

        # on_message_check means there is mqtt message
        if (on_message_check == 1):
            if (is_onResend == 0 and is_onSend == 0):
                is_onSend = 1
                sendData(dataStr)
                on_message_check = 0

        # result == -1 means that FAIL doesn't appear
        # result != -1 means that FAIL appear
        if (result != -1):
            # print ("fail find"),
            # print "fail find",
            is_onResend = 1
            sendData(dataStr)

        # result != -1 means that SUCCESS appear
        if (result_suc != -1):
            # is there is_onResend's Trace
            if (is_onResend == 1):
                is_onSend = 0
                is_onResend = 0
            else:
                is_onSend = 0


# --- mqtt function

def getMsg():
    msg = str(random.randrange(20, 36))
    return msg


def on_connect(client, userdata, falgs, rc):
    print("Connected with result code " + str(rc))
    client.subscribe("/data/warn")
    client.subscribe("/data/emerServer")


def on_publish(client, userdata, mid):
    msg_id = mid
    print("message published")


def on_message(client, userdata, message):
    global on_message_check, dataStr
    dataStr = str(message.payload)
    data = int(dataStr)
    if message.topic == "/data/warn":

        print("Received message :" + dataStr + " on topic: "
              + message.topic + " with Qos" + str(message.qos))

        if data == 1:
            dataStr = "WARN_ON"
        elif data == 0:
            dataStr = "WARN_OFF"
        on_message_check = 1
    elif message.topic == "/data/emerServer":
        # dataStr = str(message.payload)
        # data = int(dataStr)
        print("Received message :" + dataStr + " on topic: "
              + message.topic + " with Qos" + str(message.qos))

        if data == 1:
            dataStr = "EMER_ON"
        elif data == 0:
            dataStr = "EMER_OFF"
        on_message_check = 1


# --- Constant
# ----- Lora Setting
STR_FAIL = "(FAIL)"

mqttc = mqtt.Client()
mqttc.on_connect = on_connect
mqttc.on_publish = on_publish
mqttc.on_message = on_message

mqttc.username_pw_set("zqgmlwjg", "uQietis53KPr")
mqttc.connect("m12.cloudmqtt.com", 13288)
mqttc.loop_start()

# Setting Serial info
# timeout means how long wait in ser.readline
ser = serial.Serial('/dev/ttyUSB0', 115200, timeout=1)

# Show configuration
cmd = "AT+SHOW"
ser.write(cmd.encode())
ser.write("\n".encode())
ser.flush()

sendData("WARN_ON")

# multi thread ecute
th = threading.Thread(target=main_thread, args=(1, 2))
th.start()

# That is Arduino ----> Rasp ----> Server
# a few second in excution


try:
    while True:
        t1 = 0
        time.sleep(1)
        # temper = getMsg()
        # (result, m_id) = mqttc.publish("data/temper", temper)
        # sendData(temper)


except KeyboardInterrupt:
    print("Finished!")
    mqttc.loop_stop()
    mqttc.disconnect()

# --- --- ---

