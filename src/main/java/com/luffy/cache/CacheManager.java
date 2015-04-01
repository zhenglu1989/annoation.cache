package com.luffy.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * cache控制器
 * @author zhenglu
 * @since 15/3/31
 */
public class CacheManager {
    private static Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private static volatile CacheService localMemService;

    private static volatile CacheService memCacheService;

    private CacheManager(){

    }
    private static synchronized void init(){
        try {
            localMemService = new LocalMemServiceImpl();
            Method method_init = localMemService.getClass().getDeclaredMethod("init");
            method_init.invoke(localMemService);
            memCacheService = new MemcachedServiceImpl();
        } catch (Exception e) {
           logger.error("init local mencache instance failure::" + e);
        }

    }
    public static CacheService getLocalMemInstanceService(){
        if(localMemService == null){
            init();
        }
        return localMemService;

    }
    public static CacheService getMemInstanceService(){
        if(memCacheService == null){
            init();
        }
        return memCacheService;

    }



}
