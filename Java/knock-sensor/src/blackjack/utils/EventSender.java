package blackjack.utils;

import java.net.*;

public class EventSender {
    private DatagramSocket m_socket;
    private InetAddress m_address;
    private int m_port;

    public static String BROADCAST_ADDRESS = "255.255.255.255";
    public static int DEFAULT_PORT = 4444;

    public void initialize(String host) throws UnknownHostException, SocketException {
        initialize(host, DEFAULT_PORT);
    }

    public void initialize(String host, int port) throws UnknownHostException, SocketException {
        m_address = InetAddress.getByName(host);
        m_socket = new DatagramSocket();
        m_port = port;
        System.out.println("EventSender initialization success to host " + host + ":" + port);

    }

    public void close() {
        if (m_socket != null) {
            m_socket.close();
            m_socket = null;
        }
    }

    public void sendMessage(byte[] msg) {
        try {
            System.out.println("Sending event " + new String(msg));
            DatagramPacket packet = new DatagramPacket(msg, msg.length, m_address, m_port);
            m_socket.send(packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
