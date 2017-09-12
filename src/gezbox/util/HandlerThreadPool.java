package gezbox.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wuyiming on 2017/9/7.
 */
public class HandlerThreadPool {
    private static ExecutorService threadPool;

    public HandlerThreadPool() {
    }

    public static ExecutorService getPoolInstance() {
        if(threadPool == null) {
            threadPool = Executors.newCachedThreadPool();
        }

        return threadPool;
    }
}
