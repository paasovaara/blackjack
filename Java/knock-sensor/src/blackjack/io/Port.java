package blackjack.io;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

public class Port {
    private CommPortIdentifier portId;
    private SerialPort port;
    private OutputStreamWriter out;
    private InputStreamReader in;

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
            port.setSerialPortParams(9600, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            //BufferedInputStream in2 = new BufferedInputStream(new InputStreamReader(port.getInputStream()));
            in = new InputStreamReader(port.getInputStream());
            out = new OutputStreamWriter(port.getOutputStream());
            System.out.println("Port opened successfully " + comportUsed);

        } catch (Exception e) {
            System.out.println("Failed to open serial port " + comportUsed + ": Ex: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void write(String s) throws Exception {
        out.write(s);
        out.flush();
    }

    private void write(byte s) throws Exception {
        out.write(s);
        out.flush();
    }

    private void write(int s) throws Exception {
        out.write(s);
        out.flush();
    }

    public String readLine() throws Exception {
        /*while(in.ready()) {
            BufferedInputStream bi = new BufferedInputStream(port.getInputStream());

        }*/
        return "";
    }
     /*
    private String read() throws Exception {
        int n, i;
        char c;
        String answer = new String("");

        for (i = 0; i <>
        while (in.ready()) {
            n = in.read();
            if (n != -1) {
                c = (char) n;
                answer = answer + c;
                Thread.sleep(1);
            } else
                break;
        }
        delay(1);
    }


    return answer;
}*/



    private void delay(int a) {
        try {
            Thread.sleep(a);
        } catch (InterruptedException e) {
        }
    }



    public void close() throws Exception {
        try {
            port.close();
            System.out.println("port closed");
        } catch (Exception e) {
            System.out.println("Error: close port failed: " + e);
            e.printStackTrace();
        }
    }


}
