package top.cypherx.demo;

import top.cypherx.uart.SerialReaderWriter;
import com.fazecast.jSerialComm.SerialPort;
import top.cypherx.tpl.Down;
import top.cypherx.tpl.Query;
import top.cypherx.tpl.Regaccept;
import top.cypherx.tpl.Requirereg;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.util.Formatter;
import java.io.Console;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.codec.binary.Hex;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import java.util.Scanner;

public class MyApp {
    private static Map<String, Object> templates;
    private static short seq = 0;
    private static final long gateway_token = 0x00007AC1;
    private static final int nonce =  0x132F;
    private static short next_seq() {
        short r = seq;
        if(++seq == 256) {
            seq = 0;
        }
        return r;
    }
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
        } else {
            System.exit(-1);
        }

        // read serial by independent thread
        new Thread(() -> {
            while(true) {
                var buffer = new byte[100];
                int nread = readerwriter.read(buffer);
                if(nread > 0) {
                    System.out.printf("\b\brecv: %s\n> ", bytesToHex(buffer, nread));
                }
            }
        }).start();
         
        templates = generateTemplates(new String[] {"down", "query", "regaccept", "requirereg"});
        
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.print("> ");
            String cmd = scanner.nextLine();
            var result = parseCommand(cmd);
            if(!result.getLeft()) {
                System.out.println("bad command");
                continue;
            }
            boolean has_crc = result.getMiddle();
            Object packet = result.getRight();
            byte[] frame_bytes = generate(packet, has_crc);
            try {
                readerwriter.write(frame_bytes);
                System.out.printf("sent: %s\n", bytesToHex(frame_bytes, frame_bytes.length));
            } catch(Exception e) {
                e.printStackTrace();
            }

        }
	}

    private static byte[] generate(Object frame, boolean has_crc) {
        Class<?> clazz = frame.getClass();
        if(clazz == Query.class) {
            Query query =  Query.class.cast(frame);
            byte[] frame_bytes = new byte[20];
            ByteBuffer bb =ByteBuffer.wrap(frame_bytes);
            bb.order(ByteOrder.BIG_ENDIAN);

            bb.put((byte)(query.getCmd() & 0xFF));
            bb.putInt((int) query.getDevid());
            bb.put((byte)(query.getFrameseq() & 0xFF));
            bb.putShort((short)query.getNonce());
            bb.putInt((int) query.getGwtoken());
            bb.putInt((int) query.getKey());
            bb.putShort((short)query.getQ());
            if(has_crc) {
                bb.putShort((short)query.getCrc());
            } else {
                bb.putShort((short)calculateCRC16(frame_bytes, 0, frame_bytes.length - 2));
            }
            
            return frame_bytes;

        } else if(clazz == Regaccept.class) {
            Regaccept regaccept =  Regaccept.class.cast(frame);
            byte[] frame_bytes = new byte[10];
            ByteBuffer bb =ByteBuffer.wrap(frame_bytes);
            bb.order(ByteOrder.BIG_ENDIAN);

            bb.put((byte)(regaccept.getCmd() & 0xFF));
            bb.putInt((int) regaccept.getDevid());
            bb.put((byte)(regaccept.getFrameseq() & 0xFF));
            bb.putShort((short)regaccept.getShortaddr());
            if(has_crc) {
                bb.putShort((short)regaccept.getCrc());
            } else {
                byte[] for_crc = Arrays.copyOf(frame_bytes, 8 + 4);
                ByteBuffer bb_for_gwtoken= ByteBuffer.wrap(for_crc, 8, 4);
                bb_for_gwtoken.order(ByteOrder.BIG_ENDIAN);
                bb_for_gwtoken.putInt((int)gateway_token);

                bb.putShort((short)calculateCRC16(for_crc, 0, for_crc.length));
            }
            return frame_bytes;
        } else if(clazz == Requirereg.class) {
            Requirereg requirereg =  Requirereg.class.cast(frame);
            byte[] frame_bytes = new byte[18];
            ByteBuffer bb =ByteBuffer.wrap(frame_bytes);
            bb.order(ByteOrder.BIG_ENDIAN);

            bb.put((byte)(requirereg.getCmd() & 0xFF));
            bb.putShort((short) requirereg.getShortaddr());
            bb.put((byte)(requirereg.getFrameseq() & 0xFF));
            bb.putShort((short)requirereg.getNonce());
            bb.putInt((int) requirereg.getGwtoken());
            bb.putInt((int) requirereg.getKey());
            bb.putShort((short)requirereg.getQ());
            if(has_crc) {
                bb.putShort((short)requirereg.getCrc());
            } else {
                byte[] for_crc = Arrays.copyOf(frame_bytes, 16 + 4);
                ByteBuffer bb_for_gwtoken= ByteBuffer.wrap(for_crc, 16, 4);
                bb_for_gwtoken.order(ByteOrder.BIG_ENDIAN);
                bb_for_gwtoken.putInt((int)gateway_token);
                
                bb.putShort((short)calculateCRC16(for_crc, 0, for_crc.length));
            }
            return frame_bytes;
        } else if (clazz == Down.class) {
            Down down =  Down.class.cast(frame);
            byte[] frame_bytes = new byte[7 + (int)down.getDatalen()];
            ByteBuffer bb =ByteBuffer.wrap(frame_bytes);
            bb.order(ByteOrder.BIG_ENDIAN);
            
            bb.put((byte)(down.getCmd() & 0xFF));
            bb.putShort((short) down.getShortaddr());
            bb.put((byte)(down.getFrameseq() & 0xFF));

            byte start = (byte) (down.getStart() != 0 ? 1 : 0);
            byte stop = (byte) (down.getStop() != 0 ? 1 : 0);
            byte tmp = (byte) ((start << 7) + (stop << 6) + ((int)down.getDatalen() & 0x3F));
            bb.put(tmp);

            bb.put(down.getData());

            if(has_crc) {
                bb.putShort((short)down.getCrc());
            } else {
                byte[] for_crc = Arrays.copyOf(frame_bytes, frame_bytes.length + 2);
                ByteBuffer bb_for_gwtoken= ByteBuffer.wrap(for_crc, frame_bytes.length - 2 , 4);
                bb_for_gwtoken.order(ByteOrder.BIG_ENDIAN);
                bb_for_gwtoken.putInt((int)gateway_token);
                
                bb.putShort((short)calculateCRC16(for_crc, 0, for_crc.length));
            }

            return frame_bytes;
        }
        return null;
    }

    public static Triple<Boolean, Boolean, Object> parseCommand(String cmd) {
        cmd = cmd.trim();
        String[] tokens = cmd.split("\\s+");
        if(tokens.length < 2) {
            return Triple.of(false, false, null);
        }
        if(!tokens[0].equals("send") || !templates.containsKey(tokens[1])) {
            return Triple.of(false, false, null);
        }

        Object tpl = templates.get(tokens[1]);
        Object tpl_copy = invoke_with_no_params(tpl, "copy");

        boolean has_seq = false;
        boolean has_crc = false;
        boolean has_gwtoken = false;
        boolean has_nonce = false;

        for(int i = 2; i < tokens.length; i++) {
            String key_val = tokens[i];
            try {
                String[] turple = key_val.split("=");
                if(!hasField(tpl_copy, turple[0])) {
                    return Triple.of(false, false, null);
                }

                if(turple[0].equals("frameseq")) {
                    has_seq = true;
                }

                if(turple[0].equals("crc")) {
                    has_crc = true;
                }

                if(turple[0].equals("gwtoken")) {
                    has_gwtoken = true;
                }

                if(turple[0].equals("nonce")) {
                    has_nonce = true;
                }

                if(!turple[0].equals("data")) {
                    setFieldValue(tpl_copy, turple[0], Long.parseLong(turple[1]));
                } else {
                    setFieldValue(tpl_copy, turple[0], Hex.decodeHex(turple[1].toCharArray()).clone());
                }
            } catch (Exception e) {
                return Triple.of(false, false, null);
            }
        }

        if(!has_seq) {
            setFieldValue(tpl_copy, "frameseq", next_seq());
        }

        Class<?> clazz = tpl_copy.getClass();

        if(!has_gwtoken) {
            if(clazz == Query.class || clazz == Requirereg.class) {
                setFieldValue(tpl_copy, "gwtoken", gateway_token);
            }
        }

        if(!has_nonce) {
            if(clazz == Query.class || clazz == Requirereg.class) {
                setFieldValue(tpl_copy, "nonce", nonce);
            }
        }

        if(clazz == Down.class) {
            Down obj =  Down.class.cast(tpl_copy);
            setFieldValue(tpl_copy, "datalen", obj.getData().length);
        }
  
        return Triple.of(true, has_crc, tpl_copy);
    }

    public static Map<String, Object> generateTemplates(String[] names) {
        Map<String, Object> map = new HashMap<>();
        var objectMapper = new ObjectMapper();
        for(var name: names) {
            try {
                var tpl =  objectMapper.readValue(new File(String.format("template/%s.json", name)), 
                                                Class.forName(String.format("top.cypherx.tpl.%s", capitalizeFirstLetter(name))));
                map.put(name, tpl);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return map;
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

    public static boolean hasField(Object obj, String fieldName) {
        Class<?> clazz = obj.getClass();
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field != null;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static void setFieldValue(Object obj, String fieldName, Object newValue) {
        try {
        Class<?> clazz = obj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, newValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static Object invoke_with_no_params(Object obj, String method_name) {
        try {
            Class<?> clazz = obj.getClass();
            java.lang.reflect.Method method = clazz.getMethod(method_name);
            return method.invoke(obj);
        } catch (NoSuchMethodException e) {
            System.out.println("Method not found: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateCRC16(byte[] data, int start, int length) {
        final int POLYNOMIAL = 0x18005; // x^16+x^15+x^2+1
        int crc = 0xFFFF; // initial value
        for(int i = start; i < start + length; i++) {
            byte b = data[i];

            crc ^= (b << 8);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) == 0x8000) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc <<= 1;
                }
                crc &= 0xFFFF;
            }
        }
        return crc;
    }
}