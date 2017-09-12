package test;

import java.nio.ByteBuffer;

/**
 * Created by wuyiming on 2017/9/6.
 */
public class MyBuf {

    private ByteBuffer byteBuffer;

    public MyBuf(int capacity){
        byteBuffer = ByteBuffer.allocate(capacity);
    }

    public void write(byte[] bytes){
        int needSpace = bytes.length;
        if (needSpace > byteBuffer.remaining()){

        }
        byteBuffer.flip();
        byteBuffer.put(bytes);
    }
}
