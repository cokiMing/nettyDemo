package gezbox.bean;

import gezbox.bean.proto.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class Encoder extends MessageToByteEncoder<Message> {
    public Encoder() {
    }

    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(message.toBytes());
    }
}
