package gezbox.service.netty;

import gezbox.bean.Decoder;
import gezbox.bean.Encoder;
import gezbox.bean.pojo.NormalMsg;
import gezbox.bean.proto.Message;
import gezbox.bean.proto.NormalMessage;
import gezbox.util.Configuration;
import gezbox.util.HandlerThreadPool;
import gezbox.util.MyLog;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class NettyConnectClient {
    private static MyLog log;
    private static NettyConnectClient client;
    private static EventLoopGroup group;
    private static BlockingQueue<Message> queue = new LinkedBlockingDeque();
    private static Timer checkConnectionTimer;
    public static volatile long lastTime = System.currentTimeMillis();
    private static ChannelFuture fChannel = null;
    private static AtomicBoolean connflag = new AtomicBoolean(false);

    public NettyConnectClient() {
    }

    public synchronized void connect(int port, String host) throws Exception {
        group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            ((((b.group(group)).channel(NioSocketChannel.class)).option(ChannelOption.TCP_NODELAY, Boolean.valueOf(true))).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Integer.valueOf(10000))).handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelHandler[]{new Decoder()});
                    ch.pipeline().addLast(new ChannelHandler[]{new Encoder()});
                    ch.pipeline().addLast(new ChannelHandler[]{new ServerSideClientHandler(NettyConnectClient.connflag)});
                }
            });
            fChannel = b.connect(host, port).sync();
            connflag.set(fChannel.isSuccess());

            while(!fChannel.isSuccess() || !fChannel.channel().isActive()) {
                ;
            }

            lastTime = System.currentTimeMillis();
            log.info("连接服务器成功");
            this.activeSendTask(fChannel);
            fChannel.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            connflag.set(false);
        }
    }

    private void activeSendTask(ChannelFuture ctx) {
        if(connflag.get() && !queue.isEmpty() && ctx != null && ctx.channel().isActive()) {
            try {
                Message m = (Message)queue.take();
                lastTime = System.currentTimeMillis();
                ctx.channel().writeAndFlush(m);
                log.info("发了一条消息");
                if(!queue.isEmpty()) {
                    this.activeSendTask(ctx);
                }
            } catch (InterruptedException var3) {
                log.error("线程被打断", var3);
            }
        }
    }

    public static synchronized void connect() throws Exception {
        HandlerThreadPool.getPoolInstance().execute(new Runnable() {
            public void run() {
                try {
                    NettyConnectClient.log.debug("尝试连接，开启新线程");
                    String host = Configuration.getString("netty.host");
                    String port = Configuration.getString("netty.port");
                    if(NettyConnectClient.client == null) {
                        NettyConnectClient.client = new NettyConnectClient();
                        NettyConnectClient.client.connect(Integer.parseInt(port), host);
                    } else if(!NettyConnectClient.connflag.get()) {
                        NettyConnectClient.client.connect(Integer.parseInt(port), host);
                    }
                } catch (Exception var3) {
                    NettyConnectClient.connflag.set(false);
                    NettyConnectClient.log.error("connect error", var3);
                }

            }
        });
    }

    public static synchronized void reConnect() {
        if(checkConnectionTimer == null) {
            try {
                if(group != null) {
                    group.shutdownGracefully();
                }
            } catch (Exception var1) {
                log.error("关闭失败", var1);
            }

            checkConnectionTimer = new Timer();
            checkConnectionTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        if(NettyConnectClient.connflag.get()) {
                            NettyConnectClient.checkConnectionTimer.cancel();
                            NettyConnectClient.checkConnectionTimer = null;
                        } else {
                            NettyConnectClient.connect();
                        }
                    } catch (Exception var2) {
                        ;
                    }

                }
            }, 2000L, 3000L);
        }
    }

    public static boolean sendMessage(String topicName, String msg) throws Exception {
        return sendMessage(topicName, msg, Integer.valueOf(3));
    }

    public static boolean sendMessage(String topicName, String msg, Integer messageType) throws Exception {
        NormalMsg nmsg = new NormalMsg();
        nmsg.setCode(messageType.intValue());
        nmsg.setTopicName(topicName);
        nmsg.setMessage(msg);
        nmsg.setDeviceType(3);
        boolean sendFlag = queue.add(new NormalMessage(nmsg));
        if(client != null) {
            client.activeSendTask(fChannel);
        }

        if(!connflag.get()) {
            reConnect();
        }

        return sendFlag;
    }

    public static void main(String[] args) throws Exception {
        (new Timer()).schedule(new TimerTask() {
            public void run() {
                try {
                    for(int i = 0; i < 2; ++i) {
                        String msg = "{\"itemList\":[{\"amount\":4.7,\"typeName\":\"标准件\",\"orderNo\":\"000000139537\"}],\"userId\":\"wd-n7c7aQZRdivSvHK3WEgLw\",\"payChannel\":100}";
                        NettyConnectClient.sendMessage("wind.staff", msg);
                         Thread.sleep(2000L);
                    }
                } catch (Exception var3) {
                    NettyConnectClient.log.error("connect error", var3);
                }

            }
        }, 200L, 200L);
    }

    static {
        reConnect();
    }
}
