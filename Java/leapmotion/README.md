# README Leap Motion reader app

This app reads no-more-cards gesture from leap motion and sends those as UDP events to configured host

Requirements: 

- MUST: 
  - Java JDK (8)
- Optional: 
  - IntelliJ
  - Ant

Project structure:

- /src contains code
- (*) /lib must contain dependencies (read Setup)
- /out contains the compiled files
- (*) /dist contains the final jar built with Ant
- (*) config.properties is the config files

Items marked with * are required in prod.

## Setup

https://developer.leapmotion.com/documentation/v2/java/devguide/Project_Setup.html

1. install Leap motion SDK 2.3.x to some folder (f.ex C:\Leap_Motion_SDK_Windows_2.3.1)
2. create symlink to the library folder
  2.1 open cmd as administrator
  2.2 go to your java project folder f.ex cd C:\projects\inmoov-blackjack\Java\leapmotion mklink 
  2.3 mklink /D lib <leapmotion-sdk-lib-dir> (f.ex mklink lib C:\Leap_Motion_SDK_Windows_2.3.1\LeapDeveloperKit_2.3.1+31549_win\LeapSDK\lib)
3. Make sure your java project sees the lib dir for compiling (IntelliJ > Project Structure > Libraries)
  3.1 Classes should contain <projectdir>\lib
  3.2 Nativa library locations should contain also <projectdir>\lib\x64 (or x86 on legacy systems)
4. Add the path for the native libraries to your runtime configuration(s)

After previous steps compile and run.

## Building

ant
(or use build.bat)

## Running

1. Attach leap motion to the PC (can only have one per PC!)

2. run.bat
(or java -jar -Djava.library.path=.\lib\x64 dist\leapmotion.jar)

## Developer notes

There's a utility class for running the UDP server for debugging. 
