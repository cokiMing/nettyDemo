package nio.server;

import common.Constant;

/**
 * NIO 服务端序列
 * 1.打开ServerSocketChannel，用于监听客户端的连接，它是所有客户端连接的父管道
 * 2.绑定监听端口，设置连接模式为非阻塞式
 * 3.创建Reactor线程，创建多路复用器并启动线程
 * 4.将ServerSocketChannel注册到Reactor线程的多路复用器Selector上，监听ACCEPT事件
 * 5.多路复用器在线程run方法的无线循环体内轮询准备就绪的key
 * 6.多路复用器监听到有新的客户端接入，处理新的请求，完成TCP三次握手，建立物理链路
 * 7.设置客户端链路为非阻塞式
 * 8.将新接入的客户端连接注册到Reactor线程的多路复用器上，监听读写操作，读取客户端发送的网络消息
 * 9.异步读取客户端请求消息到缓冲区
 * 10.堆ByteBuffer进行编解码，如果有半包消息指针reset，继续读取后续报文，将解码成功的消息封装成task,
 *    投递到业务线程池中，进行业务逻辑编排
 * 11.将POJO对象encode成ByteBuffer，调用SocketChannel的异步write接口，将消息异步发送给客户端
 *
 * Created by wuyiming on 2017/8/31.
 */
public class NIOServer {
}
