package gezbox.bean.pojo;

import gezbox.bean.AM;
import gezbox.bean.proto.Message;

/**
 * Created by wuyiming on 2017/9/7.
 */
public abstract class APMesg implements AM {
    private int len;
    private int deviceType;
    private int code;

    public APMesg() {
    }

    public APMesg(AM am) {
        if(am != null) {
            this.setLen(am.getLen());
            this.setCode(am.getCode());
            this.setDeviceType(am.getDeviceType());
        }
    }

    public int getLen() {
        return this.len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public abstract Message toMessage();
}
