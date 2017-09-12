package nio.server;

import common.Constant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wuyiming on 2017/9/1.
 */
public class MultiplexerTimeServer implements Runnable{

    private Selector selector;

    private ServerSocketChannel serverChannel;

    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听端口
     * @param port 端口号
     */
    public MultiplexerTimeServer(int port){
        try{
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(Constant.HOST,port),1024);
            serverChannel.register(selector,SelectionKey.OP_ACCEPT);
            System.out.println("The time server started in port: "+ port);
        }catch (IOException ioe){
            ioe.printStackTrace();
            System.exit(1);
        }
    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop){
            try{
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey selectionKey;
                while (iterator.hasNext()){
                    selectionKey = iterator.next();
                    iterator.remove();
                    try{
                        handleInput(selectionKey);
                    } catch (Exception e){
                        if (selectionKey != null){
                            selectionKey.cancel();
                            if (selectionKey.channel() != null){
                                selectionKey.channel().close();
                            }
                        }
                    }
                }
            } catch (ClosedSelectorException cse){

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        //多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
        if (selector != null){
            try {
                selector.close();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 输入的处理
     * @param selectionKey
     * @throws IOException
     */
    protected void handleInput(SelectionKey selectionKey) throws IOException {
        //接入请求消息
        if (selectionKey.isValid()) {
            //说明是新客户端接入
            if (selectionKey.isAcceptable()){
                ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                SocketChannel socketChannel = channel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            }
            //说明有新的就绪的数据包需要读取
            if (selectionKey.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBuffer);
                /*
                readBytes > 0 : 读到了字节
                readBytes = -1: 链路已关闭
                readBytes = 0 : 没有读到字节
                */
                if (readBytes > 0) {
                    //切换为读模式
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order :" + body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                            new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                    doWrite(socketChannel, currentTime);
                } else if (readBytes < 0) {
                    selectionKey.cancel();
                    socketChannel.close();
                }
            }
        }
    }

    /**
     * 写操作
     * @param socketChannel
     * @param response
     * @throws IOException
     */
    private void doWrite(SocketChannel socketChannel,String response)throws IOException{
        if (response != null && response.trim().length() > 0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            //将写模式转化为读模式
            writeBuffer.flip();
            socketChannel.write(writeBuffer);
        }
    }
}
