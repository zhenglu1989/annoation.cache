package com.luffy.cache.annotation;


import com.luffy.cache.constant.CacheType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 失效cache
 * @author zhenglu
 * @since 15/2/11
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InvalidCache {

    /**
     * 缓存类型 默认是memchche
     * @return
     */
    String cacheType() default CacheType.REMOTE;
    /**
     * 缓存key前缀
     * @return
     */

    String prefix() default "";
}
