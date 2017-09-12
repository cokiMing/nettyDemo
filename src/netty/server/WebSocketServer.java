package netty.server;

import common.Constant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import netty.handler.WebsocketServerHandler;

/**
 * Created by wuyiming on 2017/9/5.
 */
public class WebSocketServer {

    public void run(int port) throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //将请求和应答消息编码或解码为HTTP消息
                            pipeline.addLast("http-codec",new HttpServerCodec());
                            //将HTTP消息的多个部分组合成一个完整的HTTP消息
                            pipeline.addLast("aggregator",new HttpObjectAggregator(65536));
                            //用于发送H5文件，主要用于支持浏览器和服务端进行WebSocket通信
                            pipeline.addLast("http-chunked",new ChunkedWriteHandler());
                            //自定义handler
                            pipeline.addLast("handler",new WebsocketServerHandler());
                        }
                    });
            Channel channel = bootstrap.bind(port).sync().channel();
            System.out.println("Web socket server started at port : " + port);
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        int port = Constant.NIO_PORT;
        new WebSocketServer().run(port);
    }
}
