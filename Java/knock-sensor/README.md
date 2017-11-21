# README Serial port reader app

This app opens a serial port and reads messages that end with \n character. After it reads one it will forward the message as UDP message to configured host, with configured prefix.

Requirements: 

- MUST: 
  - Java JDK (8)
- Optional: 
  - IntelliJ
  - Ant

Project structure:

- /src contains code
- (*) /rxtx-2.2 must contain dependencies (read Setup)
- /out contains the compiled files
- (*) /dist contains the final jar built with Ant
- (*) *.properties is the config files

Items marked with * are required in prod.

## Setup

Download RxTx library from http://fizzed.com/oss/rxtx-for-java and extract it to folder rxtx-2.2 (if using some other platform than Windows, that exists already)

## Building

ant
(or use build.bat)

## Running

1. Configure the config.properties file that will be given as input argument to the application
  1.1 There are two pre-configured files knock.properties and rfid.properties. 
  1.2 Make sure the serial port you're about to use exists and is not reserved by any other process.
2. run.bat
  2.1 Required input arguments are java library path (for rxtx native libraries) and the configuration-file-name. 


