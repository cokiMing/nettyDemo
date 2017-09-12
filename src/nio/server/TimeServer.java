package nio.server;

import aio.serverHandler.AsyncTimeServerHandler;
import common.Constant;

/**
 * Created by wuyiming on 2017/9/1.
 */
public class TimeServer {

    public static void main(String[] args){
        int port = Constant.NIO_PORT;
        if (args != null && args.length != 0){
            try{
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException nfe){

            }
        }

//        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        AsyncTimeServerHandler timeServer = new AsyncTimeServerHandler(port);
        new Thread(timeServer,"SERVER-001").start();
    }
}
