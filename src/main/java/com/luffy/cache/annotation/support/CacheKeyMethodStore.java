package com.luffy.cache.annotation.support;

import java.lang.reflect.Method;

/**
 * 储存一个object中key 被注释@CacheKeyMethod
 * @author zhenglu
 * @since 15/2/11
 */
public interface CacheKeyMethodStore {

    Method getKeyMethod(final Class<?> keyClass) throws NoSuchMethodException;
}
