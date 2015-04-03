package cache;



import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;

/**
 * 测试工具类，负责编写一个测试统计工作的类
 * <p>使用方法
 *  new TestWorker(){
 *      @Override
 *      public void exe(){
 *          Assert.notNull(dao.getByID(1))
 *      }
 *  }.printTakeTime(1000);
 * </p>
 * @author zhenglu
 * @since 15/3/31
 */
public abstract  class TestWorker {

    private static final Logger logger = Logger.getLogger(TestWorker.class);


    /**
     * 子类需要实现的执行方法
     */
    public abstract  void exe();

    public void printTakeTime(int times){
        if(times < 0 ){
            times = 1;
        }
        long start = System.currentTimeMillis();
        int realtimes = 0;
        for(int i=0;i<times;i++){
            if(Thread.currentThread().isInterrupted()){
                logger.info(".........妹的，interrupt 啦......");
                break;
            }
           long pertime = System.currentTimeMillis();
            exe();
            realtimes++;
        }
        long takeTime = System.currentTimeMillis() - start;
        System.out.println("take ms ::" + takeTime);
        System.out.println("per time take ms ::" + (takeTime/(realtimes*1.0)));

    }

    public void printTakeTimeMutil(int times){
        CompletionService<Object> completionService = new ExecutorCompletionService<Object>(Executors.newFixedThreadPool(50));
        if(times < 0 ){
            times = 1;
        }
        long start = System.currentTimeMillis();
        final AtomicInteger realTime = new AtomicInteger();
        for(int i = 0 ; i<times;i++){
            if(Thread.currentThread().isInterrupted()){
                logger.info(".....妹的，interrupt 啦.......");
            }
           completionService.submit(new Callable<Object>() {
               @Override
               public Object call() throws Exception {

                   realTime.incrementAndGet();
                   exe();
                   return new Object();
               }
           });
        }
       for(int j=0;j<times;j++){
           try {
               completionService.take().get();
           } catch (Exception e) {
               e.printStackTrace();
           }
       }

      long tk = System.currentTimeMillis() - start;
       System.out.println("take ms :: "+ tk);
       System.out.println("per time take ms :: "+ (tk/realTime.get()*1.0));

    }

}
