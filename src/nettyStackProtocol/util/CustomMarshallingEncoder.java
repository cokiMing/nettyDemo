package nettyStackProtocol.util;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * Created by wuyiming on 2017/9/6.
 */
public class CustomMarshallingEncoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    Marshaller marshaller;

    public CustomMarshallingEncoder() throws IOException {
    }

    protected void encode(Object msg, ByteBuf out) throws Exception {

    }
}
