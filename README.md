# annoation.cache
注解式缓存，基于本地缓存 和memcache实现

如何使用：配置==>

需要自己注册 MemcachedServiceImpl 

	<bean id="memcachedService" class="com.luffy.cache.MemcachedServiceImpl">
		<property name="host" value="127.0.0.1" />
		<property name="port" value="11211" />
		<property name="username" value="" />
		<property name="password" value="" />
		<property name="auth" value="false" />
		<property name="keyPrefix" value="key_" />
	</bean>
	注册需要使用的切面 
	  <bean id="cacheAspect" class="com.luffy.aspect.CacheAspect"/>
    <bean id="invalidCacheAspect" class="com.luffy.aspect.InvalidCacheAspect"/>
    <bean id= "baseAspect" class="com.luffy.aspect.BaseAspect"/>
    
使用==》获取：：

@CacheData(prefix = "website_user_info_" ,interval = 300 ,cacheType = CacheType.REMOTE)
 public User getById(@KeyParameter int id){
  return userDao.getById(id);
 }
 
 
 失效：：
 
 @InvalidCacheAspect(prefix = "website_user_info_")
 public int save(@KeyParameter User user){
    return userDao.save(user);
 }
 这边需要在对应的vo里添加key的注解
 
 比如
 
 这边需要实现返回String 的get方法
 
 
 public class User{
    private int id;
   
    @CacheKeyMethod
    public String getId(){
       return String.valueof(id);
   }
 
 }
 

	 

