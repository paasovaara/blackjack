package blackjack.io;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

public class Port {
    private CommPortIdentifier portId;
    private SerialPort port;
    private OutputStreamWriter out;
    private InputStream in;

    public final int DEFAULT_BAUD_RATE = 2400;
    private int m_baudRate = DEFAULT_BAUD_RATE;

    private boolean m_reading = false;

    public void open(String comportUsed) throws Exception {
        try {
            Enumeration portList;
            String defaultPort;

            String osname = System.getProperty("os.name", "").toLowerCase();
            if (osname.startsWith("windows")) {
                defaultPort = "COM1";
            } else if (osname.startsWith("linux")) {
                defaultPort = "/dev/ttyS0";
            } else if (osname.startsWith("mac")) {
                defaultPort = "????";
            } else {
                System.out.println("Sorry, your operating system is not supported");
            }

            portList = CommPortIdentifier.getPortIdentifiers();
            while (portList.hasMoreElements()) {
                portId = (CommPortIdentifier) portList.nextElement();
                if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    if (portId.getName().equals(comportUsed)) {
                        System.out.println("Found port: " + comportUsed);
                        break;
                    }
                }
            }

            this.port = (SerialPort) this.portId.open(comportUsed, 2000);
            port.setSerialPortParams(m_baudRate, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            in = port.getInputStream();
            out = new OutputStreamWriter(port.getOutputStream());
            System.out.println("Port opened successfully " + comportUsed);

        } catch (Exception e) {
            System.out.println("Failed to open serial port " + comportUsed + ": Ex: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void write(String s) throws Exception {
        out.write(s);
        out.flush();
    }

    public String readLine() throws Exception {
        if (m_reading) {
            throw new Exception("Port already reading!");
        }
        m_reading = true;

        StringBuffer buffer = new StringBuffer();
        while(m_reading) {
            int readByte = in.read();
            if (readByte == 0x0A) {
                break;
            }
            else if (readByte != -1) {
                buffer.append((char)readByte);
            }
            else {
                float sleepMs = 1000.0f / (float)m_baudRate;
                delay(Math.round(sleepMs));
            }
        }
        m_reading = false;
        return buffer.toString();
    }

    private void delay(int a) {
        try {
            Thread.sleep(a);
        } catch (InterruptedException e) {
        }
    }

    public void close() throws Exception {
        try {
            m_reading = false;
            port.close();
            System.out.println("port closed");
        } catch (Exception e) {
            System.out.println("Error: close port failed: " + e);
            e.printStackTrace();
        }
    }


}
