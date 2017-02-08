package Concurrencey;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:10
 */
public class SimpleThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    public SimpleThreadFactory(String prefix) {
        group = Thread.currentThread().getThreadGroup();
        namePrefix = "pool-"+prefix+"-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
    }
}
