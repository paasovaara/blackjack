# Sensor production setup for Linux

These scripts can be used to launch all java-processes for running the knock-sensors (via Arduinos) and the RFID readers + 1 leap motion. The scripts will automatically map the serial ports and feed them to the applications.

Leap motion has annoying limitation: only one device supported per PC.

Using these scripts is optional, you can just launch everything manually.


## Prerequisite:

Modify [fix_device_ports.rb-script](fix_device_ports.rb) and map the serial numbers for all connected HW to this script. You can use list_devices.rb script to find them out after connecting all devices.

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
