package com.luffy.cache.aspect;


import com.luffy.cache.CacheService;
import com.luffy.cache.annotation.CacheContext;
import com.luffy.cache.annotation.CacheData;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 缓存切面
 * @author zhenglu
 * @since 15/2/9
 */

@Aspect
public class CacheAspect extends BaseAspect{

    @Around("@annotation(com.souche.annotation.CacheData)")
    public Object doCache(final ProceedingJoinPoint pjpParam) throws Throwable{
        String cacheKey = "";
        try{
            Method method = getMethodToCache(pjpParam);
            CacheData cacheData =  method.getAnnotation(CacheData.class);
             cacheKey = method.getDeclaringClass().getName() +"_"+ method.getName()+"_";
            cacheKey +=  extKey(cacheData.prefix(),method,pjpParam);
            CacheService cacheService =  CacheContext.getCacheMap(cacheData.cacheType());
            Object value = null;
            value = cacheService.get(cacheKey);
            if(value == null) {
                value = pjpParam.proceed();
                if(value != null){
                    cacheService.set(cacheKey,(Serializable)value,cacheData.interval());
                }
            }
            return value;

        }catch (Exception e){
            getLogger().error("Caching on method"+pjpParam.toString() + " and key "+ cacheKey +" aborted due to an error",e);
            return  pjpParam.proceed();
        }



    }

}
