package blackjack.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

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

    public void initialize(int port) throws Exception {
        m_port = port;
        m_socket = new DatagramSocket(port);
    }

    public void startServer() {
        m_running = true;
        m_eventLoop.start();
        this.start();
    }

    public void run() {
        while(m_running) {
            try {
                byte[] buf = new byte[1024];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                m_socket.receive(packet);
                String msg = new String(buf);
                System.out.println(msg);
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
                System.out.println("Waiting for events");
                try {
                    synchronized (m_queue) {
                        m_queue.wait();
                        localQueue.addAll(m_queue);
                        m_queue.clear();
                    }
                } catch (InterruptedException ie) {
                }
                System.out.println("Read " + localQueue.size() + " Events, processing them");
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
            Config c = Config.readFromFile("ui.properties");

            UDPServer server = new UDPServer();
            server.initialize(c.port);
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