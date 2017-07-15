

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:12
 */
public final class JobPool extends TimeoutPool<TimeoutJob> {
    private JobPool() {
    }

    private static JobPool instance = null;

    public static JobPool getInstance() {
        if (instance == null) {
            synchronized (JobPool.class) {
                if (instance == null) {
                    JobPool jobPool = new JobPool();
                    jobPool.start();
                    instance = jobPool;
                }
            }
        }
        return instance;
    }
}
