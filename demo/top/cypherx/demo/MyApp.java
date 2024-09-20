package top.cypherx.demo;

import top.cypherx.uart.SerialReaderWriter;
import com.fazecast.jSerialComm.SerialPort;

import java.nio.charset.StandardCharsets;
import java.util.Formatter;

public class MyApp {
	public static void main(String[] args) {
        // enumerate Serial Ports
        var ports = SerialPort.getCommPorts();
        System.out.println("Seial Port List:");
        for(var port : ports) {
            System.out.print("    " + port.getSystemPortName() + " ");
            System.out.println(port.getPortDescription());
        }
        System.out.println("----------");

        if(args.length < 1) {
            System.out.println("Need COM name");
            return;
        }

		String portName = args[0]; // Serial Port Name on windows looks like "COM3"
        int baudRate = 115200;

        var readerwriter = new SerialReaderWriter(portName, baudRate);
        if(readerwriter.openPort()) {
            System.out.printf("read timeout: %dms\n", readerwriter.getReadTimeout());
            System.out.printf("write timeout: %dms\n", readerwriter.getWriteTimeout());
            try {
            readerwriter.write("Hello Serial!".getBytes("utf-8"));
            } catch(Exception e) {

            }   
        } else {
            System.exit(-1);
        }

        // read serial by independent thread
        new Thread(() -> {
            while(true) {
                var buffer = new byte[100];
                int nread = readerwriter.read(buffer);
                if(nread > 0) {
                    String hexstr = bytesToHex(buffer, nread);
                    System.out.printf("read %d bytes: %s\n", nread, hexstr);
                    //System.out.println(new String(buffer, 0, nread, StandardCharsets.UTF_8));
                }
            }
        }).start();
    
        // don't quit
        while(true);
	}

    public static String bytesToHex(byte[] bytes, int n) {
        Formatter formatter = new Formatter();
        int index = 0;
        formatter.format("%s", "[ ");
        for(byte b : bytes){
            if(index++ >= n) {
                break;
            }
            if(index <= n - 1 ) {
                formatter.format("%02X ", b);
            } else {
                formatter.format("%02X", b);
            }    
        }
        formatter.format("%s", " ]");
        String hexString = formatter.toString();
        formatter.close();
        return hexString;
    }
}