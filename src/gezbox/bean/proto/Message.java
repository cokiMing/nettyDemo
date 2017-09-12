package gezbox.bean.proto;

import gezbox.bean.AM;
import gezbox.bean.pojo.APMesg;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;

/**
 * Created by wuyiming on 2017/9/7.
 */
public abstract class Message implements AM {
    protected ByteBuffer buffer;
    private int len;
    private int code;
    private int deviceType;
    public static final int AUTH_MESSAGE = 1;
    public static final int POS_MESSAGE = 2;
    public static final int OTHER_MESSAGE = 3;
    public static final int FENG_MESSAGE = 4;
    public static final int DEVICETYPE_ANDROID = 1;
    public static final int DEVICETYPE_IOS = 2;
    public static final int DEVICETYPE_SERVER = 3;

    public Message(ByteBuffer buffer, int len, int code, int deviceType) {
        this.buffer = buffer;
        this.len = len;
        this.code = code;
        this.deviceType = deviceType;
    }

    public Message(AM a) {
        if(a != null) {
            this.setLen(a.getLen());
            this.setCode(a.getCode());
            this.setDeviceType(a.getDeviceType());
        }

    }

    public int getLen() {
        return this.len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public Message(byte[] buf) {
        this.buffer = ByteBuffer.wrap(buf);
    }

    public abstract APMesg toPojo();

    public byte[] toBytes() {
        return this.toBuffer().array();
    }

    public ByteBuffer toBuffer() {
        ByteBuffer res = ByteBuffer.allocate(this.len + 12);
        res.putInt(this.len);
        res.putInt(this.getDeviceType());
        res.putInt(this.getCode());
        res.put(this.buffer.array());
        res.flip();
        return res;
    }
}