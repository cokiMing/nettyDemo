package netty.serial;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by wuyiming on 2017/9/4.
 */
public class TestUserInfo {

    public static void main(String[] args) throws Exception{
        UserInfo userInfo = new UserInfo();
        userInfo.buildUserID(100).buildUserName("Welcome to Netty");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(userInfo);
        os.flush();
        os.close();
        byte[] b = bos.toByteArray();
        System.out.println("The jdk serializable length is : " + b.length);
        bos.close();
        System.out.println("---------------------------------------");
        System.out.println("The byteArray serializable length is : " + userInfo.codeC().length);
    }
}
