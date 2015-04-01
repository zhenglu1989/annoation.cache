package com.luffy.cache.aspect;

import com.souche.cybertron.cache.CacheService;
import com.souche.cybertron.cache.annotation.CacheContext;
import com.souche.cybertron.cache.annotation.InvalidCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

/**
 * 失效缓存aspect
 * @author zhenglu
 * @since 15/2/11
 */
@Aspect
public class InvalidCacheAspect extends BaseAspect{

    @Around("@annotation(com.souche.annotation.InvalidCache)")
    public Object invalidCache(final ProceedingJoinPoint pjpParam) throws Throwable{


        String cacheKey = "";
        Object result = null;
        try{
                 result =   pjpParam.proceed();
                Method method = getMethodToCache(pjpParam);
                InvalidCache cacheData =  method.getAnnotation(InvalidCache.class);
                cacheKey +=  extKey(cacheData.prefix(),method,pjpParam);
                CacheService cacheService =  CacheContext.getCacheMap(cacheData.cacheType());
                cacheService.del(cacheKey);

        }catch (Exception e){
            getLogger().error("Caching on method"+pjpParam.toString() + " and key "+ cacheKey +" aborted due to an error",e);

        }
        return result;


    }
    @AfterThrowing(pointcut = "@annotation(com.souche.annotation.InvalidCache)",throwing="ex")
    public void doRecoveryActions( Exception ex) {
        getLogger().error("InvalidCacheAspect throws error:"+ ex);
    }

}
