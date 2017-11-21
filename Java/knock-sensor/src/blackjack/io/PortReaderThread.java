package blackjack.io;

import blackjack.utils.Config;
import blackjack.utils.EventSender;

public class PortReaderThread extends Thread {

    Port m_port;
    private boolean m_running = false;
    EventSender m_sender = new EventSender();
    Config m_config;

    public void initialize(Config config) throws Exception {
        m_config = config;

        Port port = new Port();
        port.open(config.comPort, config.baudRate, config.stopBits);
        m_port = port;
        m_sender.initialize(config.host, config.port);

    }

    @Override
    public void run() {
        if (m_port == null) {
            System.out.println("Cannot start PortReaderThread, com port is not opened.");
            return;
        }
        if (m_running) {
            System.out.println("Cannot start PortReaderThread, already running.");
            return;
        }
        m_running = true;
        while(m_running) {
            try {
                String line = m_port.readLine();
                System.out.println("READ: " + line);
                if (m_running && !line.isEmpty()) {
                    String msg = m_config.messagePrefix + line;
                    m_sender.sendMessage(msg.getBytes());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                //Should we close(); ?
            }

        }
    }

    public void close() {
        m_running = false;
        try {
            m_sender.close();
            m_port.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
