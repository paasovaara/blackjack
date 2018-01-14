package blackjack.io.sensors;

import blackjack.utils.UDPServer;

public class SensorListener extends UDPServer.PacketListener {
    String m_regex = ".*";
    Object m_mutex = new Object();
    String m_msg;

    public SensorListener(String regex) {
        m_regex = regex;
    }

    @Override
    public String regex() {
        return m_regex;
    }

    @Override
    public void packetArrived(String payload) {
        synchronized (m_mutex) {
            m_msg = payload;
            m_mutex.notify();
        }
    }

    public String blockUntilMessage(long timeout) {
        String msg = null;
        synchronized (m_mutex) {
            m_msg = null;
            try {
                m_mutex.wait(timeout);
            }
            catch (InterruptedException e) {}

            msg = m_msg;
        }
        return msg;
    }
}
