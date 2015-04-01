package com.luffy.cache.annotation.support;

/**
 * key的生成类
 * @author zhenglu
 * @since 15/2/12
 */
public interface KeyProvider {

    /**
     * 对于单个bean，生成key的实现
     * @param keyObject
     * @return
     */
     String generateKey(final Object keyObject);


}
