package top.cypherx.tpl;

public class Regaccept {
    private long cmd;
    private long devid;
    private long frameseq;
    private long shortaddr;
    private long crc;

    public Regaccept copy() {
        Regaccept cloned = new Regaccept();
        cloned.cmd = cmd;
        cloned.devid = devid;
        cloned.frameseq = frameseq;
        cloned.shortaddr = shortaddr;
        cloned.crc = crc;
        return cloned;
    }

    public long getCmd() {
        return cmd;
    }
    public void setCmd(long cmd) {
        this.cmd = cmd;
    }

    public long getDevid() {
        return devid;
    }
    public void setDevid(long devid) {
        this.devid = devid;
    }

    public long getFrameseq() {
        return frameseq;
    }
    public void setFrameseq(long frameseq) {
        this.frameseq = frameseq;
    }

    public long getShortaddr() {
        return shortaddr;
    }
    public void setShortaddr(long shortaddr) {
        this.shortaddr = shortaddr;
    }

    public long getCrc() {
        return crc;
    }
    public void setCrc(long crc) {
        this.crc = crc;
    }
}
