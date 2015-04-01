package com.luffy.cache.annotation.support;

import com.luffy.cache.annotation.CacheKeyMethod;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhenglu
 * @since 15/2/11
 */
public class CacheKeyMethodStoreImpl implements CacheKeyMethodStore{

    public static final String DEFAULT_KEY_METHOD_NAME = "toString";

    private static final Logger logger = Logger.getLogger(CacheKeyMethodStoreImpl.class);

    private final Map<Class<?>,Method> map = new ConcurrentHashMap<Class<?>, Method>();



    @Override
    public Method getKeyMethod(Class<?> keyClass) throws NoSuchMethodException {
        final Method method = find(keyClass);
        if(method != null){
            return  method;
        }
        Method targetMethod = getMethodFromClass(keyClass);
        if (targetMethod == null) {
            // try to get from superclass
            Class<?> superKeyClass = keyClass.getSuperclass();
            if (superKeyClass != null) {
                targetMethod = getKeyMethod(superKeyClass);
            }

            if (targetMethod == null || DEFAULT_KEY_METHOD_NAME.equals(targetMethod.getName())) {
                targetMethod = keyClass.getMethod(DEFAULT_KEY_METHOD_NAME, (Class<?>[]) null);
            }
        }

        add(keyClass, targetMethod);

        return targetMethod;

    }

    private Method getMethodFromClass(final Class<?> keyClass ){
       Method targetMethod = null;
       final Method[] methods =  keyClass.getDeclaredMethods();
        for(Method method : methods){
            boolean isCacheKeyMethod = isCacheKeyMethod(method);
            if(isCacheKeyMethod && (targetMethod != null)){
                logger.error("Class  should have only one method annotated");
            }else if(isCacheKeyMethod){
                targetMethod = method;
            }

        }

        return targetMethod;
    }
    private boolean isCacheKeyMethod(final Method method){
        if(method != null && method.getAnnotation(CacheKeyMethod.class) != null){
            if(method.getParameterTypes().length > 0 ){
                logger.error("Method"+ method.toString()+"must have 0 arguments to be annotated");
            }
            if(!String.class.equals(method.getReturnType())){
                logger.error("Method"+ method.toString()+"must return String");
            }
            return  true;
        }
        return false;
    }



    private void add(Class<?> key,Method method){
        map.put(key,method);
    }

    private Method find(Class<?> key){
      return   map.get(key);
    }

}
