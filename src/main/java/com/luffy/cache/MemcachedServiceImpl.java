package com.luffy.cache;


import com.luffy.util.StringUtil;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.GetFuture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class MemcachedServiceImpl implements CacheService, InitializingBean,DisposableBean{
	 private static Logger logger = Logger.getLogger(MemcachedServiceImpl.class);
	 private String keyPrefix = "";
	 private String host = "";
	 private String port = "";
	 private String username = "";
	 private String password = "";
	 private long   opTimeout = 3000l;
	 private int   timeoutExceptionThreshold = 3000;
     private boolean auth = false;
     private boolean inited = false;
     private boolean enable = true;
     private MemcachedClient  client ;

	@Override
	public void delete(String key) {
		if(StringUtil.isEmpty(key) || !enable){
			return;
		}
		client.delete(buildKey(key));
	}


    public Object get(String key){
        if(StringUtil.isEmpty(key) || !enable){
            return "";
        }
        GetFuture<Object> result = client.asyncGet(buildKey(key));
        if(result!=null){
            try {
                return result.get();
            } catch (InterruptedException e) {
                logger.error("Memcached asyncGet exception:", e);
            } catch (ExecutionException e) {
                logger.error("Memcached asyncGet exception:", e);
            }
        }
        return "";
    }


    public boolean set(String key, Serializable value){
        if(StringUtil.isEmpty(key) || value==null ||  !enable){
            return false;
        }
        client.set(buildKey(key),0,value);
        return true;
    }


    public boolean set(String key, Serializable value, int keepSec){
        if(StringUtil.isEmpty(key) || value==null || keepSec<0 || !enable){
            return false;
        }
        client.set(buildKey(key), keepSec, value);
        return true;
    }
	
	private String buildKey(String key){
		return keyPrefix + ":" +key;
	}
	
	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public void destroy() throws Exception {
		if(client==null){
			return;
		}
		client.shutdown();
		logger.info("Memcached Client shutdown...");
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		if (!inited) {
			try {
				ConnectionFactoryBuilder cfBuidler = new ConnectionFactoryBuilder();
				cfBuidler.setProtocol(Protocol.BINARY);
				cfBuidler.setOpTimeout(opTimeout);
				cfBuidler.setTimeoutExceptionThreshold(timeoutExceptionThreshold);
				if (auth) {
					cfBuidler.setAuthDescriptor(new AuthDescriptor(
							new String[] { "PLAIN" }, new PlainCallbackHandler(
									username, password)));
				}
				client = new MemcachedClient(cfBuidler.build(),AddrUtil.getAddresses(host + ":" + port));
				inited = true;
				logger.info("Memcached Client inited...");
			} catch (IOException e) {
				logger.error("Memcached client initial exception:", e);
				e.printStackTrace();
			}
		}
	}
}
