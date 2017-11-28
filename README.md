# Inmoov BlackJack

This is gonna be the coolest blackjack table ever, hosted by a robot.

- Arduino-folder contains code for the KnockSensor to detect "hit me" gesture.
- Java/leapmotion has code for detecting "stay" gesture.
- Java/blackjack has code for the blackjack engine which is the "brains"

Front end will be coded with Unity and the star of the show is the InMoov robot.

## Prerequisite:

Currently only tested on Ubuntu.

Recommend using ```rbenv``` to install & manage ruby versions. This is already done in production laptop.

Make sure dependencies are installed:

```
sudo apt install libusb-1.0-0-dev udev
gem install libusb
npm i -g concurrently
```

## START THE SERVICES:

```
# IMPORTANT: BEFORE RUNNING MAKE SURE EVERYTHING IS PLUGGED IN, INCLUDING
# knock-sensors, rfid readers and 1 leap motion

# RUN EACH STEP IN A SEPARATE TERMINAL - THERE SHOULD BE IN TOTAL 4 SESSIONS

# 1. Start leapmotion daemon
sudo leapd

# 2. Start leap motion app. This also automatically matches the knock sensors and rfid reader ports to the correct /dev/tty interfaces
cd ~/inmoov/blackjack && ./run_leap_motion

# 3. Start player 1 knock-sensor and rfid reader:
cd ~/inmoov/blackjack && ./run_player1

# 4. Start player 1 knock-sensor and rfid reader:
cd ~/inmoov/blackjack && ./run_player2
```
