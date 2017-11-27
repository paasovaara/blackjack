# Inmoov BlackJack

This is gonna be the coolest blackjack table ever, hosted by a robot.

- Arduino-folder contains code for the KnockSensor to detect "hit me" gesture.
- Java/leapmotion has code for detecting "stay" gesture.
- Java/blackjack has code for the blackjack engine which is the "brains"

Front end will be coded with Unity and the star of the show is the InMoov robot.

## Startup scripts:

Currently only tested on Ubuntu.

Recommend using ```rbenv``` to manage ruby versions.

Make sure dependencies are installed:

```
sudo apt install libusb-1.0-0-dev
gem isntall libusb
```

This command will automatically recognize our production devices and map them to the correct /dev/tty ports:

```
ruby run.rb
```
