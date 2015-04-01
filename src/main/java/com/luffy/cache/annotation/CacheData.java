package com.luffy.cache.annotation;


import com.luffy.cache.constant.CacheType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO 后期增加组（group）的概念
 * 注解缓存
 * @author zhenglu
 * @since 15/2/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheData{


    /**
     * 缓存失效时间 默认五分钟
     * 单位s
     */
    int interval() default 300;

    /**
     * 缓存类型 默认是memcache
     * @return
     */
    String cacheType() default CacheType.REMOTE;

    /**
     * 缓存key前缀
     * @return
     */

    String prefix() default "";


}
