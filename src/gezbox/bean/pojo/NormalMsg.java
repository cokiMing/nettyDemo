package gezbox.bean.pojo;

import gezbox.bean.AM;
import gezbox.bean.proto.NormalMessage;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class NormalMsg extends APMesg{
    private String topicName;
    private int topicNameLen;
    private String message;
    private int messageLen;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.setLen(this.getLen() + message.getBytes().length);
        this.messageLen = message.getBytes().length;
        this.message = message;
    }

    public NormalMsg() {
        this.setLen(16);
    }

    public NormalMsg(AM am) {
        super(am);
        if(am == null) {
            this.setLen(16);
        }

    }

    public String getTopicName() {
        return this.topicName;
    }

    public void setTopicName(String topicName) {
        this.setLen(this.getLen() + topicName.getBytes().length);
        this.topicNameLen = topicName.getBytes().length;
        this.topicName = topicName;
    }

    public int getTopicNameLen() {
        return this.topicNameLen;
    }

    public NormalMessage toMessage() {
        return new NormalMessage(this);
    }

    public int getMessageLen() {
        return this.messageLen;
    }
}
