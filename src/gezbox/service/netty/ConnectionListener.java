package gezbox.service.netty;

import gezbox.util.MyLog;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class ConnectionListener implements ChannelFutureListener {
    private MyLog log;

    public ConnectionListener() {
    }

    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if(!channelFuture.isSuccess()) {
            log.debug("Reconnect");
            NettyConnectClient.reConnect();
        }

    }
}
