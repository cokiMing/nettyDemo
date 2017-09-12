package gezbox.bean;

import gezbox.bean.proto.Message;
import gezbox.bean.proto.NormalMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class Decoder extends ByteToMessageDecoder {
    public Decoder() {
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() >= 12) {
            in.markReaderIndex();
            int len = in.readInt();
            if(in.readableBytes() < len) {
                in.resetReaderIndex();
            } else {
                System.out.println("长度" + len);
                int deviceType = in.readInt();
                int code = in.readInt();
                Message m = null;
                switch(code) {
                    case 3:
                    case 4:
                        int topicLen = in.readInt();
                        byte[] decoded = new byte[len];
                        in.readBytes(decoded);
                        m = new NormalMessage(ByteBuffer.wrap(decoded), len, topicLen, code, deviceType);
                    case 2:
                    default:
                        out.add(m);
                }
            }
        }
    }
}
