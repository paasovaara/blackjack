package blackjack.utils;

import behave.tools.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Based on this tutorial https://docs.oracle.com/javase/tutorial/networking/datagrams/examples/QuoteServerThread.java
 */
public class UDPServer extends Thread {

    private boolean m_running = false;

    public static int DEFAULT_PORT = 4445;

    private int m_port = DEFAULT_PORT;
    protected DatagramSocket m_socket = null;

    protected final LinkedList<String> m_queue = new LinkedList<>();
    EventHandlerThread m_eventLoop = new EventHandlerThread();

    protected final LinkedList<PacketListener> m_listeners = new LinkedList<>();

    public void initialize(int port) throws Exception {
        Log.info("Binding UDP server to port " + port);
        m_port = port;
        m_socket = new DatagramSocket(port);
    }

    public void startServer() {
        Log.info("Starting UDP server");
        m_running = true;
        m_eventLoop.start();
        this.start();
    }


    public static abstract class PacketListener {
        /*private Pattern p = null;
        public Pattern regexPattern() {
            if (p == null) {
                p = Pattern.compile(regex());
            }
            return p;
        }*/

        public Pattern regexPattern() {
            return Pattern.compile(regex());
        }

        public String regex() {
            return ".*";
        }
        public abstract void packetArrived(String payload);
    }

    public void addListener(PacketListener l) {
        synchronized (m_listeners) {
            m_listeners.add(l);
        }
    }

    public void run() {
        while(m_running) {
            try {
                byte[] buf = new byte[1024];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                m_socket.receive(packet);
                String msg = new String(buf);
                //System.out.println(msg);
                synchronized (m_queue) {
                    m_queue.add(msg);
                    m_queue.notify();
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    class EventHandlerThread extends Thread {
        public void run() {
            final LinkedList<String> localQueue = new LinkedList<>();

            while (m_running) {
                //System.out.println("Waiting for events");
                try {
                    synchronized (m_queue) {
                        m_queue.wait();
                        localQueue.addAll(m_queue);
                        m_queue.clear();
                    }
                } catch (InterruptedException ie) {
                }
                //System.out.println("Read " + localQueue.size() + " Events, processing them");
                synchronized (m_listeners) {
                    ListIterator<String> itr = localQueue.listIterator();
                    while(itr.hasNext()) {
                        String msg = itr.next();
                        for(PacketListener l: m_listeners) {
                            Matcher matcher = l.regexPattern().matcher(msg);
                            if (matcher.lookingAt()) {
                                l.packetArrived(msg);
                            }
                        }
                        itr.remove();
                    }
                }
            }
        }
    }

    public void close() {
        m_running = false;
        if(m_socket != null) {
            System.out.print("Closing socket");
            m_socket.close();
            System.out.print("Socket closed");
        }
        //To wake all threads for sure
        synchronized (m_queue) {
            m_queue.notifyAll();
        }
    }


    public static void main(String[] args) {
        try {
            Config c = Config.readFromFile("sensors.properties");

            UDPServer server = new UDPServer();
            server.initialize(c.port);

            server.addListener(new PacketListener() {
                @Override
                public String regex() {
                    return"^(stay|hit)";
                    //return "^(stay\\{0\\})";
                }
                @Override
                public void packetArrived(String payload) {
                    System.out.println("stay/hit from player!: " + payload);
                }
            });

            server.addListener(new PacketListener() {
                @Override
                public void packetArrived(String payload) {
                    System.out.println("ANY MESSAGE: " + payload);
                }
            });

            server.startServer();

            System.out.println("Press any key to quit...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}