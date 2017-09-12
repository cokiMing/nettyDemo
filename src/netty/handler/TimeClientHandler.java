package netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by wuyiming on 2017/9/1.
 */
public class TimeClientHandler extends ChannelHandlerAdapter {
    private final byte[] req;

    public TimeClientHandler() {
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    /**
     * 当发生异常时，该方法会被调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //释放资源
        System.err.println("Unexpected exception from downstream :" + cause.getMessage());
        ctx.close();
    }

    /**
     * 当客户端与服务端TCP链路建立成功之后，Netty的NIO线程会调用该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //调用ChannelHandlerContext的writeAndFlush将消息发送给服务端
        ByteBuf message = null;
        for (int i =0; i < 100;i++){
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    /**
     * 当服务端返回应答信息时，该方法将会被调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ByteBuf buf = (ByteBuf)msg;
//        byte[] req = new byte[buf.readableBytes()];
//        buf.readBytes(req);
//        String body = "";
//        try{
//            body = new String(req,"UTF-8");
//        } catch (Exception e){
//            e.printStackTrace();
//        }
        String body = (String)msg;
        System.out.println("Now is : "+body);
    }
}
