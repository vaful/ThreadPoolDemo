package Concurrencey;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:09
 */
public final class ThreadPoolUtil {
    private static final Map<String,ExecutorService> poolMonitor = new HashMap<String, ExecutorService>();
    private static final Object lock = new Object();
    private static final CleanThread cleanThread = new CleanThread();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(cleanThread));
    }

    private static class CleanThread implements Runnable {

        private List<Thread> threads = Collections.synchronizedList(new ArrayList<Thread>());

        private void addThread(Thread thread) {
            threads.add(thread);
        }
        @Override
        public void run() {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }
    }

    public static abstract class CleanThreadPool implements Runnable {

        protected ExecutorService executorService;

        public CleanThreadPool(ExecutorService executorService) {
            this.executorService = executorService;
        }

    }

    public static class DefaultCleanThreadPool extends CleanThreadPool {

        public DefaultCleanThreadPool(ExecutorService executorService) {
            super(executorService);
        }

        @Override
        public void run() {
            executorService.shutdownNow();
        }
    }

    public static ExecutorService newFixedExecutor(int nThread, String prefix) {
        ExecutorService executorService = Executors.newFixedThreadPool(nThread, new SimpleThreadFactory(prefix));
        Runtime.getRuntime().addShutdownHook(new Thread(new DefaultCleanThreadPool(executorService)));
        String info = String.format("ThreadPool name : %s, fixed thread : %d, default clean ", prefix, nThread);
        synchronized (lock) {
            poolMonitor.put(info, executorService);
        }
        return executorService;
    }

    public static ExecutorService newFixedExecutor(int nThread, String prefix, CleanThreadPool cleanThreadPool) {
        ExecutorService executorService = Executors.newFixedThreadPool(nThread, new SimpleThreadFactory(prefix));
        Runtime.getRuntime().addShutdownHook(new Thread(cleanThreadPool));
        String info = String.format("ThreadPool name : %s, fixed thread : %d, not default clean ", prefix, nThread);
        synchronized (lock) {
            poolMonitor.put(info, executorService);
        }
        return executorService;
    }

    public static ExecutorService newCachedExecutor(String prefix) {
        ExecutorService executorService = new ThreadPoolExecutor(20, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new SimpleThreadFactory(prefix));
        Runtime.getRuntime().addShutdownHook(new Thread(new DefaultCleanThreadPool(executorService)));
        String info = String.format("ThreadPool name : %s, cache thread , default clean ", prefix);
        synchronized (lock) {
            poolMonitor.put(info, executorService);
        }
        return executorService;
    }

    public static ExecutorService newCachedExecutor(String prefix, CleanThreadPool cleanThreadPool) {
        ExecutorService executorService = new ThreadPoolExecutor(20, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new SimpleThreadFactory(prefix));
        Runtime.getRuntime().addShutdownHook(new Thread(cleanThreadPool));
        String info = String.format("ThreadPool name : %s, cache thread , not default clean ", prefix);
        synchronized (lock) {
            poolMonitor.put(info, executorService);
        }
        return executorService;
    }

    public static ExecutorService newCachedExecutor(int corePoolSize, String prefix) {
        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new SimpleThreadFactory(prefix));
        Runtime.getRuntime().addShutdownHook(new Thread(new DefaultCleanThreadPool(executorService)));
        String info = String.format("ThreadPool name : %s, cache thread , default clean ", prefix);
        synchronized (lock) {
            poolMonitor.put(info, executorService);
        }
        return executorService;
    }

    public static ExecutorService newCachedExecutor(int corePoolSize, String prefix, CleanThreadPool cleanThreadPool) {
        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new SimpleThreadFactory(prefix));
        Runtime.getRuntime().addShutdownHook(new Thread(cleanThreadPool));
        String info = String.format("ThreadPool name : %s, cache thread , not default clean ", prefix);
        synchronized (lock) {
            poolMonitor.put(info, executorService);
        }
        return executorService;
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(int corePoolSize, int maximumPoolSize, String prefix) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new SimpleThreadFactory(prefix));
        Runtime.getRuntime().addShutdownHook(new Thread(new DefaultCleanThreadPool(poolExecutor)));
        String info = String.format("ThreadPool name : %s, pool thread %d core. %d max , default clean ", prefix, corePoolSize, maximumPoolSize);
        synchronized (lock) {
            poolMonitor.put(info, poolExecutor);
        }
        return poolExecutor;
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,TimeUnit unit,String prefix, CleanThreadPool cleanThreadPool) {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>(), new SimpleThreadFactory(prefix));
        Runtime.getRuntime().addShutdownHook(new Thread(cleanThreadPool));
        String info = String.format("ThreadPool name : %s, pool thread %d core. %d max. %d aliveTime. %s unit , default clean ", prefix, corePoolSize, maximumPoolSize, keepAliveTime,unit.name());
        synchronized (lock) {
            poolMonitor.put(info, poolExecutor);
        }
        return poolExecutor;
    }

    public static void registerShutdown(Thread thread) {
        cleanThread.addThread(thread);
    }

    public static Map<String, ExecutorService> monitorThreadPool() {
        synchronized (lock) {
            return new HashMap<String, ExecutorService>(poolMonitor);
        }
    }
}
