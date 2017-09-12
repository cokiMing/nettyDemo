package gezbox.service.netty;

import gezbox.bean.proto.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class ServerSideClientHandler extends SimpleChannelInboundHandler<Message> {
    private static final Logger logger = LoggerFactory.getLogger(ServerSideClientHandler.class);
    private AtomicBoolean reconnectFlag = null;

    public ServerSideClientHandler(AtomicBoolean reconnectFlag) {
        this.reconnectFlag = reconnectFlag;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        this.reconnectFlag.set(true);
        logger.info("连接成功");
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.reconnectFlag.set(false);
        NettyConnectClient.reConnect();
        super.channelInactive(ctx);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        this.reconnectFlag.set(false);
        NettyConnectClient.reConnect();
        logger.warn("Unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        logger.info("收到服务端返回：" + message.getCode());
    }
}
