package com.luffy.cache.annotation;


import com.luffy.cache.CacheManager;
import com.luffy.cache.CacheService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * cache上下文
 * @author zhenglu
 * @since 15/2/9
 */
public class CacheContext {

    private static Map<String,CacheService> cacheMap = new ConcurrentHashMap<String, CacheService>();

    public static CacheService getCacheMap(String key) {
        if("local".equals(key)){
            return CacheManager.getLocalMemInstanceService();
        }else{
            return CacheManager.getMemInstanceService();
        }
    }

    public static void register(String key,CacheService cache) {
        cacheMap.put(key,cache);
    }
}
