package test;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/7/17
 * Time: 11:39
 */
public class Test {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            Thread t = new MyThread();
            t.start();
        }
    }
}

class Sync {
    public void test() {
        synchronized (Test.class) {
            System.out.println(Thread.currentThread().getName() + "线程开始。。。");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "线程结束。。。");
        }
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        super.run();
        Sync sync = new Sync();
        sync.test();
    }
}