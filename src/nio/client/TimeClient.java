package nio.client;

import aio.clientHandler.AsyncTimeClientHandler;
import common.Constant;
import nio.handler.TimeClientHandler;

/**
 * Created by wuyiming on 2017/9/1.
 */
public class TimeClient {

    public static void main(String args[]){

//        TimeClientHandler timeClientHandler = new TimeClientHandler(Constant.HOST,Constant.NIO_PORT);
        AsyncTimeClientHandler timeClientHandler = new AsyncTimeClientHandler(Constant.HOST,Constant.NIO_PORT);

        new Thread(timeClientHandler,"client-001").start();
    }
}
