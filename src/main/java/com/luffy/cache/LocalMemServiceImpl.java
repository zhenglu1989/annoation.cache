package com.luffy.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import com.luffy.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本地缓存的实现
 * @author zhenglu
 * @since 15/3/30
 */
public class LocalMemServiceImpl implements CacheService{

    private static Logger logger = LoggerFactory.getLogger(LocalMemServiceImpl.class);

   private int maxItemNum = 1200000;

   private Map<Long,CacheItem> cache;

   private Lock reentrantLock = new ReentrantLock();

   private ExecutorService executorService;

    public void init(){
        cache  = new ConcurrentHashMap<Long, CacheItem>();
        executorService = Executors.newScheduledThreadPool(1);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
              clearExpireCache();
            }
        },300 * 1000);
    }

   protected  void clearExpireCache(){
       reentrantLock.lock();
       try {
           Iterator<Map.Entry<Long,CacheItem>> iterator = cache.entrySet().iterator();
           while (iterator.hasNext()){
               Map.Entry<Long,CacheItem> entry = iterator.next();
               CacheItem item = entry.getValue();
               if(item.isExpire()){
                   iterator.remove();
               }
           }

       } catch (Exception e) {
           logger.error("clear local cache has error:", e);
       } finally {
           reentrantLock.unlock();
       }
   }
    public boolean isFull(){
        return cache.size() >= maxItemNum;
    }

    @Override
    public void delete(String key) {
        reentrantLock.lock();
        try {
            long hashkey = HashUtil.getFPHash(key);
            cache.remove(hashkey);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            reentrantLock.unlock();
        }

    }

    @Override
    public Object get(String key) {
        long hash = HashUtil.getFPHash(key);
        CacheItem item = cache.get(hash);
        if(item != null && !item.isExpire()){
            return item.getValue();
        }
        return null;
    }

    @Override
    public boolean set(String key, Serializable value) {
        return set(key,value,-1);
    }

    @Override
    public boolean set(String key, Serializable value, int keepSec) {
        reentrantLock.lock();
        if(isFull()){
            return false;
        }
        try {
            long keyhash = HashUtil.getFPHash(key);
            cache.put(keyhash,new CacheItem(value,keepSec));
            return true;

        } catch (Exception e) {
            logger.error("set local memcache has error ",e);
        } finally {
            reentrantLock.unlock();
        }
        return false;
    }
    class CacheItem{
        public Object value;

        public long expireTime = 0 ;

        CacheItem(Object value, int keepSec) {

            this.value = value;
            if(keepSec > 0){
                this.expireTime = System.currentTimeMillis() + keepSec * 1000;
            }
        }
        public boolean isExpire(){
            if(expireTime <= 0){
                return false;
            }
            return  expireTime < System.currentTimeMillis();

        }

        public Object getValue() {
            return value;
        }
    }


}
