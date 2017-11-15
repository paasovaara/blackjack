package blackjack.io;

import blackjack.utils.UDPServer;

public class SensorListener extends UDPServer.PacketListener {
    String m_regex = ".*";
    Object m_mutex = new Object();
    String m_msg;

    public SensorListener(String regex) {
        m_regex = regex;// + "\\{" + playerId + "\\}";
    }

    @Override
    public String regex() {
        return m_regex;
    }

    @Override
    public void packetArrived(String payload) {
        synchronized (m_mutex) {
            //System.out.println("DEBUG Sensor listener received " + payload);
            m_msg = payload;
            m_mutex.notify();
        }
        //System.out.println("DEBUG packer arrived ending, msg = " + m_msg);

    }

    public String blockUntilMessage(long timeout) {
        //System.out.println("DEBUG Block until message");

        String msg = null;
        synchronized (m_mutex) {
            //System.out.println("DEBUG Clearing message and starting to wait");

            m_msg = null;
            try {
                m_mutex.wait(timeout);
            }
            catch (InterruptedException e) {}
            //System.out.println("DEBUG wait ended, message " + m_msg);

            msg = m_msg;
        }
        return msg;
    }
}
