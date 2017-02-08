package Concurrencey;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/2/8
 * Time: 10:08
 */
public  abstract  class CurrentThreadJob<T> implements TimeoutJob {
    public abstract T getResult();

}
