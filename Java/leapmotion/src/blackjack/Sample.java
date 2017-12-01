package blackjack;

/******************************************************************************\
* Copyright (C) 2012-2013 Leap Motion, Inc. All rights reserved.               *
* Leap Motion proprietary and confidential. Not for distribution.              *
* Use subject to the terms of the Leap Motion SDK Agreement available at       *
* https://developer.leapmotion.com/sdk_agreement, or another agreement         *
* between Leap Motion and you, your company or other organization.             *
\******************************************************************************/

import java.io.IOException;
import java.lang.Math;
import java.util.Timer;
import java.util.TimerTask;

import blackjack.utils.*;
import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;

class SampleListener extends Listener {
    EventSender m_sender = new EventSender();
    String m_payload = "stay{0}";
    private int m_frameCounter = 0;
    private long m_frameTimer = System.currentTimeMillis();

    public void onInit(Controller controller) {
        System.out.println("Initialized");
        DeviceList devices = controller.devices();
        System.out.println("Found devices: " + devices.count());

        for (Device dev: devices) {
            System.out.println(dev.serialNumber());
        }
        try {
            blackjack.utils.Config c = blackjack.utils.Config.readFromFile("config.properties");
            m_payload = c.payload;
            m_sender.initialize(c.host, c.port);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendEvent() {
        String msg = m_payload;
        m_sender.sendMessage(msg.getBytes());
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
        //controller.enableGesture(Gesture.Type.TYPE_SWIPE);
        //controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        //controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        //controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
    }

    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    public void onFrame(Controller controller) {
        // Get the most recent frame and report some basic information
        Frame frame = controller.frame();

        // debug("Frame id: " + frame.id()
        //                  + ", timestamp: " + frame.timestamp()
        //                  + ", hands: " + frame.hands().count()
        //                  + ", fingers: " + frame.fingers().count()
        //                  + ", tools: " + frame.tools().count()
        //                  + ", gestures " + frame.gestures().count());

        //Get hands
        /*if (frame.hands().count() > 0) {
            info("Hand present");
        }*/
        boolean notify = false;
        /*for(Hand hand : frame.hands()) {
            String handType = hand.isLeft() ? "Left hand" : "Right hand";
            debug("  " + handType + ", id: " + hand.id()
                    + ", palm position: " + hand.palmPosition());

            // Get the hand's normal vector and direction
            Vector normal = hand.palmNormal();
            Vector direction = hand.direction();

            double handNormalRoll = Math.toDegrees(normal.roll());
            if (Math.abs(handNormalRoll) <= 90) {
                notify = true;
            }
            else {
                debug("Hand in invalid roll: " + handNormalRoll);
            }
        }*/
        notify = !frame.hands().isEmpty();

        if (!notify) {
            notify = !frame.fingers().isEmpty();
            if (notify) {
                debug("Saw fingers, notifying: " + frame.fingers().count());
            }

        }

        if (notify) {
            info("Hand or fingers present");
            sendEvent();
        }

        m_frameCounter++;
    }
    /*
    private void maybePrintFps() {
        final int TIMER_INTERVAL_MS = 10000;
        m_frameCounter++;
        long now = System.currentTimeMillis();
        if (now - m_frameTimer >= TIMER_INTERVAL_MS) {
            float fps = ((float)m_frameCounter * 1000.0f) / (float)TIMER_INTERVAL_MS;
            info("FPS: " + fps);
            m_frameTimer = now;
            m_frameCounter = 0;
        }
    }*/

    public void printFps() {
        long now = System.currentTimeMillis();
        long elapsed = now - m_frameTimer;

        float fps = ((float)m_frameCounter * 1000.0f) / (float)elapsed;
        info("FPS: " + fps);
        m_frameTimer = now;
        m_frameCounter = 0;
    }

    private static boolean printDebug = false;
    static void debug(String message) {
        if (printDebug)
            System.out.println(message);
    }
    static void info(String message) {
        System.out.println(message);
    }

    public void close() {
        m_sender.close();
    }
}

class Sample {
    public static void main(String[] args) {
        // Create a sample listener and controller
        final SampleListener listener = new SampleListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
        controller.addListener(listener);

        final int TIMER_INTERVAL_MS = 10000;

        java.util.Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                listener.printFps();
            }
        }, TIMER_INTERVAL_MS, TIMER_INTERVAL_MS);
        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
        listener.close();
    }
}
