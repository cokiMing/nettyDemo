package gezbox.bean.proto;


import gezbox.bean.AM;
import gezbox.bean.pojo.NormalMsg;

import java.nio.ByteBuffer;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class NormalMessage extends Message {
    private int topicLen;

    public NormalMessage(ByteBuffer buffer, int len, int code, int deviceType) {
        super(buffer, len, code, deviceType);
    }

    public NormalMessage(ByteBuffer buffer, int len, int topicLen, int code, int deviceType) {
        super(buffer, len, code, deviceType);
        this.topicLen = topicLen;
    }

    public NormalMessage(AM am) {
        super(am);
        NormalMsg a = (NormalMsg)am;
        this.topicLen = a.getTopicNameLen();
        this.buffer = ByteBuffer.allocate(this.getLen() - 16);
        this.buffer.put(a.getTopicName().getBytes());
        this.buffer.put(a.getMessage().getBytes());
    }

    public NormalMsg toPojo() {
        NormalMsg res = new NormalMsg(this);
        byte[] topicNameByte = new byte[this.topicLen];
        this.buffer.get(topicNameByte);
        res.setTopicName(new String(topicNameByte));
        byte[] messageByte = new byte[this.getLen() - this.topicLen - 16];
        this.buffer.get(messageByte);
        res.setMessage(new String(messageByte));
        return res;
    }

    public ByteBuffer toBuffer() {
        ByteBuffer res = ByteBuffer.allocate(this.getLen());
        res.putInt(this.getLen());
        res.putInt(this.getDeviceType());
        res.putInt(this.getCode());
        res.putInt(this.topicLen);
        res.put(this.buffer.array());
        res.flip();
        return res;
    }
}
