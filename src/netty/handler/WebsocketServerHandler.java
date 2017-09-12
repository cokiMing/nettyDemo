package netty.handler;

import common.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * Created by wuyiming on 2017/9/5.
 */
public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        //传统的HTTP接入
        if (msg instanceof FullHttpRequest){
            handleHttpRequest(channelHandlerContext,(FullHttpRequest)msg);
        }
        //webSocket接入
        else if (msg instanceof WebSocketFrame){
            handleWebSocketFrame(channelHandlerContext,(WebSocketFrame)msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext context, FullHttpRequest request) throws Exception{
        //如果HTTP解码失败，返回HTTP异常
        if (!request.getDecoderResult().isSuccess() || (!"websocket".equals(request.headers().get("Upgrade")))){
            sendHttpResponse(
                    context,
                    request,
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:"+ Constant.NIO_PORT+"/websocket",null,false);
        handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(context.channel());
        } else {
            handshaker.handshake(context.channel(),request);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws Exception{
        //判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame){
            handshaker.close(context.channel(),(CloseWebSocketFrame) frame.retain());
            return;
        }

        //判断是否是ping消息
        if (frame instanceof PingWebSocketFrame){
            context.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //仅支持文本消息
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName()));
        }

        //返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        context.channel().write(
                new TextWebSocketFrame(request + ", 欢迎使用Netty WebSocket服务，现在是" + new Date().toString())
        );
    }

    private static void sendHttpResponse(ChannelHandlerContext context,
                                         FullHttpRequest request,
                                         FullHttpResponse response){
        if (response.getStatus().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
        }

        //非Keep-Alive,关闭连接
        ChannelFuture future = context.channel().writeAndFlush(response);
        if (response.getStatus().code() != 200){
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
