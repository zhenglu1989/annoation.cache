package com.luffy.cache.aspect;



import com.luffy.cache.annotation.KeyParameter;
import com.luffy.cache.annotation.support.DefaultKeyProvider;
import com.luffy.cache.annotation.support.KeyProvider;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 基础aspect
 * @author zhenglu
 * @since 15/2/11
 */
public class BaseAspect {

    private static final Logger logger = LoggerFactory.getLogger(BaseAspect.class);

    private KeyProvider defaultKeyProvider = new DefaultKeyProvider();

    public void setDefaultKeyProvider(final KeyProvider defaultKeyProvider) {
        this.defaultKeyProvider = defaultKeyProvider;
    }

    public KeyProvider getDefaultKeyProvider() {
        return this.defaultKeyProvider;
    }

    protected Method getMethodToCache(final JoinPoint jp) throws NoSuchMethodException {
        final Signature sig = jp.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new NoSuchMethodException("This annotation is only valid on a method.");
        }

        final MethodSignature msig = (MethodSignature) sig;
        final Object target = jp.getTarget();

        // cannot use msig.getMethod() because it can return the method where annotation was declared i.e. method in

        String name = msig.getName();
        Class<?>[] parameters = msig.getParameterTypes();

        Method method = target.getClass().getMethod(name, parameters);

        return method;
    }


    /**
     * Todo key生成规则：KeyParameter指定的参数，如果该参数对象中包含CacheKeyMethod注解的方法，则调用其方法，否则调用toString方法
     * 提取参数中的key
     * @param method
     * @return
     */
    protected String extKey(String prefix,Method method,JoinPoint joinPoint){
        String keyprefix = prefix;

        Annotation[][] args =  method.getParameterAnnotations();
        for(int i = 0;i<args.length;i++){
            for(int j = 0;j<args[i].length;j++){
                if(args[i][j] instanceof KeyParameter){
                    Object targ = joinPoint.getArgs()[i];
                   String keyMethod =  defaultKeyProvider.generateKey(targ);
                    if(keyMethod == null){
                        keyprefix += String.valueOf(targ) + "_";
                    }else{
                        keyprefix += keyMethod + "_";
                    }
                }
            }
        }
        return keyprefix;
    }

    protected Logger getLogger() {
        return logger;
    }

}
