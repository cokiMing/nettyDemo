package nettyStackProtocol.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import nettyStackProtocol.pojo.NettyMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by wuyiming on 2017/9/5.
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    CustomMarshallingEncoder encoder;

    public NettyMessageEncoder() throws IOException{
        this.encoder = new CustomMarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext context, NettyMessage message, List<Object> list) throws Exception {
        if (message == null || message.getHeader() == null){
            throw new Exception("The encode message is null");
        }
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(message.getHeader().getCrcCode());
        buf.writeInt(message.getHeader().getLength());
        buf.writeLong(message.getHeader().getSessionId());
        buf.writeByte(message.getHeader().getType());
        buf.writeByte(message.getHeader().getPriority());
        buf.writeInt(message.getHeader().getAttachment().size());
        String key;
        byte[] keyArray;
        Object value;
        for (Map.Entry<String,Object> param : message.getHeader().getAttachment().entrySet()){
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            buf.writeInt(keyArray.length);
            buf.writeBytes(keyArray);
            value = param.getValue();
            encoder.encode(value,buf);
        }

        key = null;
        keyArray = null;
        value = null;
        if (message.getBody() != null){
            encoder.encode(message.getBody(), buf);
        } else {
            buf.writeInt(0);
            buf.setInt(4,buf.readableBytes());
        }
    }
}
