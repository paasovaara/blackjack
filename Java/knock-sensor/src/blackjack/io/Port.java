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

    public void open(String comPortToUse, int baudRate, int stopBits) throws Exception {
        try {
            m_baudRate = baudRate;

            Enumeration portList;
            /*String defaultPort;

            String osname = System.getProperty("os.name", "").toLowerCase();
            if (osname.startsWith("windows")) {
                defaultPort = "COM1";
            } else if (osname.startsWith("linux")) {
                defaultPort = "/dev/ttyS0";
            } else if (osname.startsWith("mac")) {
                defaultPort = "????";
            } else {
                System.out.println("Sorry, your operating system is not supported");
            }*/

            portList = CommPortIdentifier.getPortIdentifiers();
            while (portList.hasMoreElements()) {
                CommPortIdentifier p = (CommPortIdentifier) portList.nextElement();
                if (p.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    System.out.println("System contains com port " + p.getName());
                    if (p.getName().equals(comPortToUse)) {
                        System.out.println("Found port: " + comPortToUse);
                        portId = p;
                        break;
                    }
                }
            }
            if (portId != null) {
                this.port = (SerialPort) this.portId.open(comPortToUse, 2000);
                //TODO properly
                int stopBitConfig = stopBits == 1 ? SerialPort.STOPBITS_1 : SerialPort.STOPBITS_2;

                port.setSerialPortParams(m_baudRate, SerialPort.DATABITS_8,
                        stopBitConfig, SerialPort.PARITY_NONE);
                in = port.getInputStream();
                out = new OutputStreamWriter(port.getOutputStream());
                System.out.println("Port opened successfully " + comPortToUse);
            }
            else {
                throw new RuntimeException("Failed to find com port " + comPortToUse);
            }

        } catch (Exception e) {
            System.out.println("Failed to open serial port " + comPortToUse + ": Ex: " + e.getMessage());
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
            else if (readByte != -1 && readByte != 0x0D) { // Let's ignore CR, and just use LF as line change
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
