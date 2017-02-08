package Concurrencey;

import com.google.common.collect.HashMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:13
 */
public abstract class TimeoutPool<T extends TimeoutJob> {
    protected boolean start = false;
    protected ReentrantLock lock;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //bind t to currentThread,may be remove by timeout thread,ThreadLocal cannot be used
    protected HashMultimap<T,Thread> threadMap = HashMultimap.create();

    public boolean add(T t) {
        lock.lock();
        try {
            return threadMap.put(t, Thread.currentThread());
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(T t) {
        lock.lock();
        try {
            return threadMap.remove(t, Thread.currentThread());
        } finally {
            lock.unlock();
        }
    }
    public void start() {
        start(defaults);
    }

    public synchronized void start(DoTimeout doTimeout) {
        if (!start) {
            start = true;
            lock = new ReentrantLock();
            final Thread thread = new Thread(doTimeout, "timeout-pool-thread-" + getClass().getSimpleName());
            thread.start();
            ThreadPoolUtil.registerShutdown(thread);
        }
    }
    protected static interface DoTimeout extends Runnable{
    }

    protected DoTimeout defaults = new DoTimeout() {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.debug(e.getMessage(),e);
                }
                final HashMultimap<TimeoutJob,Thread> timeoutJobs = HashMultimap.create();
                lock.lock();
                try {
                    Iterator<Map.Entry<T, Thread>> iterator = threadMap.entries().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<T, Thread> entry = iterator.next();
                        T job = entry.getKey();
                        if (job.checkTimeout()) {
                            iterator.remove();
                            timeoutJobs.put(job, entry.getValue());
                        }
                    }
                } finally {
                    lock.unlock();
                }
                if (!executor.isShutdown()) {
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            for (Map.Entry<TimeoutJob, Thread> entry : timeoutJobs.entries()) {
                                try {
                                    entry.getKey().about(entry.getValue());
                                } catch (Exception e) {
                                    logger.error(e.getMessage(), e);
                                }
                            }
                        }
                    });
                } else {
                    for (Map.Entry<TimeoutJob, Thread> entry : timeoutJobs.entries()) {
                        try {
                            entry.getKey().about(entry.getValue());
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    };
    protected ExecutorService executor = ThreadPoolUtil.newFixedExecutor(2, "job-clean");
}
