package blackjack.io;

public class PortReaderThread extends Thread {

    Port m_port;
    private boolean m_running = true;

    public void initialize(String comport) throws Exception {
        Port port = new Port();
        port.open(comport);
        m_port = port;
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    void close() {
        m_running = false;
        try {
            //Hopefully this will abort blocking read. TODO test
            m_port.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
