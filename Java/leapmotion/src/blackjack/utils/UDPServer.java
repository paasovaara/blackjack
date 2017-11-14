package blackjack.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Based on this tutorial https://docs.oracle.com/javase/tutorial/networking/datagrams/examples/QuoteServerThread.java
 */
public class UDPServer extends Thread {

    private boolean m_running = false;

    public static int DEFAULT_PORT = 4445;

    private int m_port = DEFAULT_PORT;
    protected DatagramSocket m_socket = null;
    protected BufferedReader m_in = null;

    public void initialize(int port) throws Exception {
        m_port = port;
        m_socket = new DatagramSocket(port);
    }

    public void run() {
        if (m_running)
            return;
        System.out.print("Starting event reading..");

        m_running = true;
        while(m_running) {
            try {
                byte[] buf = new byte[1024];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                m_socket.receive(packet);

                String msg = new String(buf);
                System.out.println(msg);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
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
    }


    public static void main(String[] args) {
        try {
            Config c = Config.readFromFile("config.properties");

            UDPServer server = new UDPServer();
            server.initialize(c.port);
            server.start();

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