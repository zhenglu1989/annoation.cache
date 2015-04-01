package com.luffy.cache.annotation.support;

import org.apache.log4j.Logger;

import java.lang.reflect.Method;

/**
 * @author zhenglu
 * @since 15/2/12
 */
public class DefaultKeyProvider implements KeyProvider {
    private static final Logger logger = Logger.getLogger(DefaultKeyProvider.class);

    private CacheKeyMethodStore methodStore = new CacheKeyMethodStoreImpl();

    public void setMethodStore(final CacheKeyMethodStore methodStore) {
        this.methodStore = methodStore;
    }

    public CacheKeyMethodStore getMethodStore() {
        return this.methodStore;
    }

    @Override
    public String generateKey(Object keyObject) {
        if (keyObject == null) {
            logger.error("keyobject is not permit null");
        }
        try {

            final Method keyMethod = methodStore.getKeyMethod(keyObject.getClass());
            final String objectId = (String) keyMethod.invoke(keyObject, (Object[]) null);
            if (objectId == null || objectId.length() < 1) {
                throw new RuntimeException("Got an empty key value from " + keyMethod.getName());
            }

            return objectId;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
