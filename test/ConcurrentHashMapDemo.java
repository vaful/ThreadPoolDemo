package test;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用来统计文本中单词出现的次数，把单词出现的次数记录到一个Map中
 * 因为ConcurrentHashMap经常的使用场景是复合操作，经常是get和set一起完成，所以ConcurrentHashMap的使用场景并不是很多，
 * 并且用锁来控制。
 * ConcurrentHashMap保证了当只执行put的操作时，线程安全而已。
 * <p>
 * Created with IntelliJ IDEA.
 * User: WuYifei
 * Date: 2017/7/18
 * Time: 18:08
 */
public class ConcurrentHashMapDemo {
    public ConcurrentHashMap<String, Long> hashMap = new ConcurrentHashMap<String, Long>();

//    @unsafe
    public Long increase(String word) {
        HashMap map = new HashMap();
        Long oldValue = hashMap.get(word);
        Long newValue = oldValue == null ? 1 : oldValue + 1;
        hashMap.put(word, newValue);
        return newValue;
    }

    public Long increaseDemo(String word) {
        HashMap map = new HashMap();
        Long oldValue = hashMap.get(word);
        while(hashMap.get(word) == null) {

        }
        Long newValue = oldValue == null ? 1 : oldValue + 1;
        hashMap.put(word, newValue);
        return newValue;
    }
}
