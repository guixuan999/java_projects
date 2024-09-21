package top.cypherx.tpl;

public class Requirereg {
    private long cmd;
    private long shortaddr;
    private long frameseq;
    private long nonce;
    private long gwtoken;
    private long key;
    private long q;
    private long crc;

    public Requirereg copy() {
        Requirereg cloned = new Requirereg();
        cloned.cmd = cmd;
        cloned.shortaddr = shortaddr;
        cloned.frameseq = frameseq;
        cloned.nonce = nonce;
        cloned.gwtoken = gwtoken;
        cloned.key = key;
        cloned.q = q;
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

    public long getNonce() {
        return nonce;
    }
    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public long getGwtoken() {
        return gwtoken;
    }
    public void setGwtoken(long gwtoken) {
        this.gwtoken = gwtoken;
    }
    
    public long getKey() {
        return key;
    }
    public void setKey(long key) {
        this.key = key;
    }

    public long getQ() {
        return q;
    }
    public void setQ(long q) {
        this.q = q;
    }

    public long getCrc() {
        return crc;
    }
    public void setCrc(long crc) {
        this.crc = crc;
    }
}
