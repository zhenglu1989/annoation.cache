package com.luffy.cache;

import java.io.Serializable;
import java.util.List;

/**
 * 缓存服务
 * @author zhenglu
 *
 */
public interface CacheService {

	/**
	 * 删除缓存
	 * @param key
	 */
	public void delete(String key);


    /**
     * 根据key，获取缓存中的值
     */
    public Object get(String key);

    /**
     * 设置kv值到缓存中
     * @param key key
     * @param value value
     */
    public boolean set(String key, Serializable value);

    /**
     * 设置kv对到缓存中
     * @param key value值
     * @param value key值
     * @param keepSec 保留的秒数
     */
    public boolean set(String key, Serializable value, int keepSec);


}
