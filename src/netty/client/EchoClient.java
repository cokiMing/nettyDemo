package netty.client;

import common.Constant;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import netty.decoder.MsgpackDecoder;
import netty.encoder.MsgpackEncoder;
import netty.handler.EchoClientHandler;

/**
 * Created by wuyiming on 2017/9/4.
 */
public class EchoClient {

    public void connect(int port,String host) throws Exception {
        //配置客户端线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
                            socketChannel.pipeline().addLast(new StringDecoder());
//                            socketChannel.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
//                            socketChannel.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            //发起异步连接操作
            ChannelFuture future = bootstrap.connect(host,port).sync();
            //等到客户端链路关闭
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        EchoClient echoClient = new EchoClient();
        echoClient.connect(Constant.NIO_PORT,Constant.HOST);
    }
}
