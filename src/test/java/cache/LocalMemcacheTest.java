package cache;

import com.luffy.cache.CacheService;
import com.luffy.cache.LocalMemServiceImpl;
import junit.framework.TestCase;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author zhenglu
 * @since 15/3/30
 */
public class LocalMemcacheTest extends TestCase {

    private static CacheService localCacheService;

    public void setUp() throws Exception {
        super.setUp();
        localCacheService = new LocalMemServiceImpl();
        Method method_init = localCacheService.getClass().getDeclaredMethod("init");
        method_init.invoke(localCacheService);

    }
   @Test
    public void testLocalCache(){
      String str = "hello world";
      localCacheService.set(str,3);

   }
    @Test
    public void testSetLocalCache(){
        new TestWorker(){
            int i = 1;
            @Override
            public void exe() {

                String str = "key_test"+String.valueOf(i);
                localCacheService.set(str,String.valueOf(i),3);
                System.out.println(localCacheService.get(str));
                i++;
            }

        }.printTakeTime(100);
    }
}
