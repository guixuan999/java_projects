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
            System.out.println("串口打开成功：" + serialPort.getSystemPortName());
            return true;
        } else {
            System.out.println("无法打开串口：" + serialPort.getSystemPortName());
            return false;
        }
    }

    public void closePort() {
        if(serialPort.closePort()) {
            System.out.println("串口关闭成功：" + serialPort.getSystemPortName());
        } else {
            System.out.println("无法关闭串口：" + serialPort.getSystemPortName());
        }
    }

    public int read(byte[] buffer) {
        return serialPort.readBytes(buffer, buffer.length);
    }

    public int write(byte[] buffer) {  
        return serialPort.writeBytes(buffer, buffer.length, 0);
    }

    public int getReadTimeout() {
        return serialPort.getReadTimeout();
    }

    public int getWriteTimeout() {
        return serialPort.getWriteTimeout();
    }
}
