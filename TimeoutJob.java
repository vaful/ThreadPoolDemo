package Concurrencey;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:05
 */
public interface TimeoutJob {
    /**
     * @return  -1 means not yet start
     */
    public long startTime();

    public void run() throws JobException;

    /**
     * @param executeThread : executeThread.interrupt()
     * @throws JobException
     */
    public void about(Thread executeThread) throws JobException;

    public boolean checkTimeout();
}
