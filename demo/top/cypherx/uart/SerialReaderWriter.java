package top.cypherx.uart;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class SerialReaderWriter {
    private SerialPort serialPort;

    public SerialReaderWriter(String portDescriptor, int baudRate) {
        serialPort = SerialPort.getCommPort(portDescriptor);
        serialPort.setBaudRate(baudRate);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING | SerialPort.TIMEOUT_READ_BLOCKING, 20, 0);
    }

    public boolean openPort() {
        if(serialPort.openPort()) {
            System.out.println("Port opened: " + serialPort.getSystemPortName());
            return true;
        } else {
            System.out.println("Unable to open port: " + serialPort.getSystemPortName());
            return false;
        }
    }

    public void closePort() {
        if(serialPort.closePort()) {
            System.out.println("Port closed: " + serialPort.getSystemPortName());
        } else {
            System.out.println("Unable to close port: " + serialPort.getSystemPortName());
        }
    }

    public int read(byte[] buffer) {
        return serialPort.readBytes(buffer, buffer.length);
    }

    public int write(byte[] buffer) {  
        return serialPort.writeBytes(buffer, buffer.length);
    }

    public int getReadTimeout() {
        return serialPort.getReadTimeout();
    }

    public int getWriteTimeout() {
        return serialPort.getWriteTimeout();
    }
}
