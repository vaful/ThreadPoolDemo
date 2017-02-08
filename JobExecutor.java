package Concurrencey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:04
 */
public class JobExecutor {
        private static final ExecutorService executor = ThreadPoolUtil.newCachedExecutor("light-job");
        private static Logger logger = LoggerFactory.getLogger(JobExecutor.class);

        public static void execute(final TimeoutJob timeoutJob) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        timeoutJob.run();
                    } catch (JobException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            });
        }

        public static JobResult executeWithResult(final TimeoutJob timeoutJob) {
            Future<JobResult> future = executor.submit(new Callable<JobResult>() {
                @Override
                public JobResult call() throws Exception {
                    timeoutJob.run();
                    return new JobResult(JobResult.RETURN_SUCCESS, "job run success");
                }
            });
            try {
                return future.get();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return new JobResult(JobResult.RETURN_FAIL, e.getMessage());
            }
        }

        public static <T> void executeInCurrentThread(CurrentThreadJob<T> currentThreadJob) throws JobException {
            if (JobPool.getInstance().add(currentThreadJob)) {
                try {
                    currentThreadJob.run();
                } finally {
                    JobPool.getInstance().remove(currentThreadJob);
                }
            } else {
                //not occur
                logger.warn("currentThread is executing the job, not occur");
            }
        }

        public static <X> Future<X> execute(Callable<X> callable) {
            return executor.submit(callable);
        }

}
