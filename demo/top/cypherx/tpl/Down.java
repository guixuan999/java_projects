package top.cypherx.tpl;

public class Down {
    private long cmd;
    private long shortaddr;
    private long frameseq;
    private long start;
    private long stop;
    private long datalen;
    private byte[] data;
    private long crc;

    public Down copy() {
        Down cloned = new Down();
        cloned.cmd = cmd;
        cloned.shortaddr = shortaddr;
        cloned.frameseq = frameseq;
        cloned.start = start;
        cloned.stop = stop;
        cloned.datalen = datalen;
        cloned.data = data.clone();
        cloned.crc = crc;
        return cloned;
    }

    public long getCmd() {
        return cmd;
    }
    public void setCmd(long cmd) {
        this.cmd = cmd;
    }

    public long getShortaddr() {
        return shortaddr;
    }
    public void setShortaddr(long shortaddr) {
        this.shortaddr = shortaddr;
    }

    public long getFrameseq() {
        return frameseq;
    }
    public void setFrameseq(long frameseq) {
        this.frameseq = frameseq;
    }

    public long getStart() {
        return start;
    }
    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }
    public void setStop(long stop) {
        this.stop = stop;
    }
    
    public long getDatalen() {
        return datalen;
    }
    public void setDatalen(long datalen) {
        this.datalen = datalen;
    }

    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }

    public long getCrc() {
        return crc;
    }
    public void setCrc(long crc) {
        this.crc = crc;
    }
}

