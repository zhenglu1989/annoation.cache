package com.luffy.util;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CommonUtil {
    private static Random rand = new Random();
    private final static Logger logger = Logger.getLogger(CommonUtil.class);
    private static char CHARACTERS[] = { 
        '0', '1', '2', '3', '4',
        '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 
        'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O',
        'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z'};

    
    /**
     * 对obj里面的Integer、Long、Float、Double类型字段中，为null值的成员，置为0
     */
    public static void invokeDefaultValue(Object obj) {
        if(obj == null) {
            return;
        }
        
        Class<? extends Object> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        Class<? extends Object> superCls = cls.getSuperclass();
        Field[] superFields = superCls.getDeclaredFields();

        for(Field field : fields) {
            try {
                field.setAccessible(true);
                if(field.get(obj) != null) {
                    continue;
                }
                String setVariableName = getSetFunctionName(field.getName());
                if(field.getType() == Integer.class) {
                    invokeMethod(obj, setVariableName, 0);
                } else if(field.getType() == Long.class) {
                    invokeMethod(obj, setVariableName, 0L);
                } else if(field.getType() == Float.class) {
                    invokeMethod(obj, setVariableName, 0.0f);
                } else if(field.getType() == Double.class) {
                    invokeMethod(obj, setVariableName, 0.0d);
                }
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
        for(Field field : superFields) {
            try {
                field.setAccessible(true);
                if(field.get(obj) != null) {
                    continue;
                }
                String setVariableName = getSetFunctionName(field.getName());
                if(field.getType() == Integer.class) {
                    invokeMethod(obj, setVariableName, 0);
                } else if(field.getType() == Long.class) {
                    invokeMethod(obj, setVariableName, 0L);
                } else if(field.getType() == Float.class) {
                    invokeMethod(obj, setVariableName, 0.0f);
                } else if(field.getType() == Double.class) {
                    invokeMethod(obj, setVariableName, 0.0d);
                }
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
    }
    
    /**
     * 解释url中的参数
     */
    public static Map<String, String> parseParameter(String url) {
        
        Map<String, String> parameterMap = 
                new HashMap<String, String>();
        
        if(StringUtil.isEmpty(url)) {
            return parameterMap;
        }
        StringBuffer sb = new StringBuffer();
        // 根据?把url切开
        String tmp1[] = url.split("\\?", 2);
        sb.append(tmp1[0]);
        if(tmp1.length == 2) {
            String[] kvs = tmp1[1].split("&");
            for(String kv : kvs) {
                String[] tmp2 = kv.split("=", 2);
                if(tmp2.length != 2 || StringUtil.isEmpty(tmp2[1])) {
                    continue;
                }
                parameterMap.put(tmp2[0], tmp2[1]);
            }
        }
        
        return parameterMap;
    }

    /**
     * 把obj2里面不为null的字段，更新到toUpdateObj中
     */
    public static void updateObjectValue(Object toUpdateObj, Object obj2) {
        if(toUpdateObj == null || obj2 == null) {
            return;
        }
        
        Class<? extends Object> cls = toUpdateObj.getClass();
        while(cls.getSuperclass() != null) {
        	Method[] methods = cls.getDeclaredMethods();
        	for(Method method : methods) {
        		String methodName = method.getName();
        		if(methodName.startsWith("set")) {
        			String getFuncName = "g" + methodName.substring(1);
        			@SuppressWarnings("unchecked")
					Method getMethod = getMethod(obj2, getFuncName);
        			if(getMethod != null) {
        				try {
        					Object obj = getMethod.invoke(obj2);
        					if(obj != null) {
        						invokeMethod(toUpdateObj, methodName, obj);
        					}
        				} catch (Exception e) {
        				}
        			}
        		}
        	}
        	cls = cls.getSuperclass();
        }
    }
    
    public static Object getObjectField(Object obj, String field) {
        if(obj == null) {
            return null;
        }

        try {
            Class<? extends Object> cls = obj.getClass();
            while(cls != null && cls != Object.class) {
                Field[] fields = cls.getDeclaredFields();
                for(Field f : fields) {
                    f.setAccessible(true);
                    if(f.getName().equals(field)) {
                        return f.get(obj);
                    }
                }
                cls = cls.getSuperclass();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 生成object里面所有有效参数的string串
     */
    public static String ObjectToString(Object obj) {
        if(obj == null) {
            return null;
        }
        
        Set<String> nameSet = new HashSet<String>();
        
        StringBuffer querySB = new StringBuffer();
        Class<? extends Object> cls = obj.getClass();
        while(cls.getSuperclass() != null) {
        	Field[] fields = cls.getDeclaredFields();
            int count = 0;
        	Method[] methods = cls.getDeclaredMethods();
        	for(Method method : methods) {
        		String methodName = method.getName();
        		if(methodName.startsWith("get")) {
        			try {
        				Object value = method.invoke(obj);
        				String fieldName = methodName.replace("get", "");
        				if(StringUtil.isEmpty(fieldName)) {
        					continue;
        				}
        				
        				fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
        				
        				if(value != null) {
                            if(nameSet.contains(fieldName.toUpperCase())) {
                            	continue;
                            }
                            
                            nameSet.add(fieldName.toUpperCase());
                            
                            if(count++ > 0)  {
                                querySB.append("\t");
                            }

                            querySB.append(fieldName)
                                   .append(": ")
                                   .append(String.valueOf(value));
        				}
        			} catch (Exception e) {
        			}
        		}
        	}

        	for(Field field : fields) {
                try {
                    field.setAccessible(true);
                    if(Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    
                    if(nameSet.contains(field.getName().toUpperCase())) {
                    	continue;
                    }
                    
                    nameSet.add(field.getName().toUpperCase());
                    
                    if(count++ > 0)  {
                        querySB.append("\t");
                    }

                    querySB.append(field.getName())
                           .append(": ")
                           .append(String.valueOf(field.get(obj)));
                } catch (Exception e) {
                    logger.warn("", e);
                }
            }
            
            
            cls = cls.getSuperclass();
        }
        
        return querySB.toString();
    }
    
    
    /**
     * 生成查询串的唯一标识符
     */
    public static String genQueryKey(Object obj) {
        if(obj == null) {
            return null;
        }
        
        StringBuffer querySB = new StringBuffer();
        Class<? extends Object> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        int count = 0;
        for(Field field : fields) {
            try {
                field.setAccessible(true);
                if(field.get(obj) == null || field.getName().equals("serialVersionUID")) {
                    continue;
                }
                
                if(Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                
                if(count++ > 0)  {
                    querySB.append("|");
                }

                querySB.append(field.getName())
                       .append("_")
                       .append(String.valueOf(field.get(obj)));
            } catch (Exception e) {
                logger.warn("", e);
            }
        }
        
        return querySB.toString();
    }
    
    /**
     * invoke调用obj里面的一个函数
     */
    @SuppressWarnings("unchecked")
    public static Object invokeMethod(Object obj, String methodName,
            Object... params) {
        if (obj == null || methodName == null) {
            return null;
        }

        Class<?extends Object>[] parameterTypes = null;
        if (params != null && params.length > 0) {
            List<Class<?extends Object>> typeList = new ArrayList<Class<?extends Object>>();
            for (Object paramObj : params) {
            	if(paramObj==null){
            		continue;
            	}
                Class<? extends Object> c = paramObj.getClass();
                typeList.add(c);
            }
            
            parameterTypes = new Class[typeList.size()];
            typeList.toArray(parameterTypes);
        }

        try {
            Method method = null;
            if (parameterTypes == null) {
                method = obj.getClass().getDeclaredMethod(methodName,
                        parameterTypes);
            } else {
                method = getMethod(obj, methodName, parameterTypes);
            }
            if (method == null) {
                return null;
            }
            method.setAccessible(true);
            return method.invoke(obj, params);
        } catch (Exception e) {
        }
        return null;
    }
    
    private static Method getMethod(Object obj, String methodName,
            Class<?extends Object>... parameterTypes) {
        try {
        	Class<? extends Object> cls = obj.getClass();
        	while(cls.getSuperclass() != null) {
        		Method[] methods = cls.getDeclaredMethods();

        		String internedName = methodName.intern();

        		for (int i = 0; i < methods.length; i++) {
        			Method m = methods[i];
        			if (!m.getName().equals(internedName)) {
        				continue;
        			}
        			if (arrayContentsEq(parameterTypes, m.getParameterTypes())) {
        				return m;
        			}
        		}
        		cls = cls.getSuperclass();
        	}
        } catch (Exception e) {
        }
        return null;
    }
    
    /**
     * 生成n以内的随机数
     */
    public static int randomNum(int n) {
        return Math.abs(rand.nextInt(n) % n);
    }

    /**
     * 生成n以内的随机数
     */
    public static long randomNum(long n) {
        return Math.abs(rand.nextLong() % n);
    }

    private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null || a2 == null) {
            return false;
        }
        if (a1.length != a2.length) {
            return false;
        }

        return true;
    }

    protected static String getSetFunctionName(String variablNname){ 
        String strHead = variablNname.substring(0, 1); 
        String strTail = variablNname.substring(1, variablNname.length()); 

        String strRetval = "set" + strHead.toUpperCase() + strTail; 

        return strRetval; 
    }
    
    protected static String getGetFunctionName(String variablNname){ 
        String strHead = variablNname.substring(0, 1); 
        String strTail = variablNname.substring(1, variablNname.length()); 

        String strRetval = "get" + strHead.toUpperCase() + strTail; 

        return strRetval; 
    }
    
    /**
     * 得到int值
     */
    public static int getIntValue(Object obj) {
        try {
            if(obj == null) {
                return 0;
            }

            if(obj instanceof Integer) {
                return (Integer) obj;
            }

            if(obj instanceof Long) {
                return ((Long)obj).intValue();
            }

            if(obj instanceof String) {
                if(StringUtil.isEmpty((String) obj)) {
                    return 0;
                }
                return Integer.parseInt(StringUtil.trim((String) obj));
            }
        } catch (Exception e) {
//            logger.warn("", e);
        }
        return 0;
    }
    
    public static long getLongValue(Object obj) {
        try {
            if(obj == null) {
                return 0;
            }

            if(obj instanceof Integer) {
                return ((Integer) obj).longValue();
            }

            if(obj instanceof Long) {
                return ((Long)obj).longValue();
            }

            if(obj instanceof String) {
                return Long.parseLong((String) obj);
            }
        } catch (Exception e) {
            logger.warn("", e);
        }
        return 0;
    }
    
    public static float getFloatValue(Object obj) {
        try {
            if(obj == null) {
                return 0;
            }

            if(obj instanceof Integer) {
                return ((Integer) obj).floatValue();
            }

            if(obj instanceof Long) {
                return ((Long)obj).floatValue();
            }

            if(obj instanceof String) {
                return Float.parseFloat(((String) obj).replaceAll("\\s+", ""));
            }
            if(obj instanceof Float) {
                return ((Float)obj).floatValue();
            }
            if(obj instanceof Double) {
                return ((Double)obj).floatValue();
            }
        } catch (Exception e) {
            logger.warn("", e);
        }
        return 0;
    }
    
    /**
     * 生成长度为length的随机数
     */
    public static String genRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<length; i++) {
            int randomNum = randomNum(CHARACTERS.length);
            sb.append(CHARACTERS[randomNum]);
        }
        
        return sb.toString();
    }
    
    /**
     * 获取request的url
     */
    public static String getRequestUrl(HttpServletRequest request) {
    	return getRequestUrl(request, false);
    }

    public static String getRequestUrl(HttpServletRequest request, boolean fullParam) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(request.getRequestURI());
    	if(fullParam) {
    		int n = 0;
    		for(Object key : request.getParameterMap().keySet()) {
    			String k = key.toString().toLowerCase();
    			if(k.indexOf("password") != -1 || k.indexOf("pwd") != -1) {
    				continue;
    			}
    			if(n ++ == 0) {
    				sb.append("?");
    			} else {
    				sb.append("&");
    			}
    			String value = request.getParameter((String) key);
    			sb.append(key).append("=").append(value);
    		}
    	} else {
            String query = request.getQueryString();
            if(StringUtil.isNotEmpty(query)) {
            	sb.append("?").append(query);
            }
    	}

    	return sb.toString();
    }

    public static InputStream openResourceInputStream(String file) throws IOException {
        if(new File(file).exists()) {
            return new FileInputStream(file);
        }
        
        URL u = CommonUtil.class.getResource(file);
        if(u == null) {
        	u = CommonUtil.class.getResource("/" + file);
        	if(u == null) {
        		return null;
        	}
        }
        return u.openStream();
    }
    

    
    public static String getAbsoluteFilePath(String file) {
        if(new File(file).exists()) {
            return file;
        }
        
        URL u = CommonUtil.class.getResource(file);
        if(u == null) {
        	u = CommonUtil.class.getResource("/" + file);
        	if(u == null) {
        		return file;
        	}
        }
        return u.getPath();
    }
    public static String getAbsoluteFilePathStream(String file) {
        if(new File(file).exists()) {
            return file;
        }
        
        URL u = CommonUtil.class.getResource(file);
        if(u == null) {
            u = CommonUtil.class.getResource("/" + file);
            if(u == null) {
                return file;
            }
        }
        return u.getPath();
    }
    
    
    /**
     * 在字符串头部填充字符
     * @param str 要填充的字符串
     * @param num 长度
     * @param c 要填充的字符
     * @return
     */
    public static String fillStringHead(String str, int num, char c) {
        if(str == null) {
            return null;
        } 
        
        if(str.length() >= num) {
            return str;
        }
        
        StringBuffer sb = new StringBuffer();
        for(int i=str.length(); i<num; i++) {
            sb.append(c);
        }
        sb.append(str);
        return sb.toString();
    }
    
    /**
     * 获取str里面价格的文本
     * @param str
     * @return
     */
    public static String getNumberString(String str) {
        StringBuffer sb = new StringBuffer();
        if(StringUtil.isEmpty(str)) {
            return sb.toString();
        }
        
        boolean started = false;
        int dotNum = 0;
        for(int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if(c >= '0' && c <= '9') {
                started = true;
                sb.append(c);
            } else if(c == '.') {
                if(started) {
                    dotNum ++;
                    if(dotNum >= 2) {
                        break;
                    }
                    sb.append(c);
                }
            } else if(c == ',') {
                continue;
            } else if(started) {
                break;
            }
        }
        
        return sb.toString();
    }
    

    public static String getRemoteIpAddress(HttpServletRequest request) {
        String realIP = request.getHeader("X-Real-IP");
        if(StringUtil.isNotEmpty(realIP)) {
            return realIP;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 取指定精度的浮点数
     * @param f
     * @param scale
     * @return
     */
    public static float getFloatValueByPrecision(float f,int scale ){
    	
    	BigDecimal   b   =   new   BigDecimal(f);  
		float   f1   =   b.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();  
    	return f1;
    }
    
    /**
     * 把指定double类型的值除以denominator保留两位小数显示
     * @param mileage
     * @param denominator
     * @return
     */
    public static String getDoubleValueShow(double mileage,int denominator) {
		String show = "0.00";
		if(mileage!=0){
			show =new  java.text.DecimalFormat("0.00").format((mileage)/denominator);
		}
		
		return show;
	}

    public static Map<Object,Object> listToMap(List<? extends Object> list,String methodNameKey){
    	Map<Object,Object> map = new HashMap<Object, Object>();
    	for(Object ob : list){
    		Class<?> classType = ob.getClass();
    		try {
				Method method = classType.getMethod(methodNameKey, new Class[]{});
				map.put(method.invoke(ob,  new Object[]{}), ob);
			} catch (Exception e) {
				return null;
			}
    	}
    	return map;
    } 
    
    @SuppressWarnings("rawtypes")
	public static Integer getObjectSize(Object obj){
    	
    	if(obj==null){
    		return 0;
    	}
    	if(obj instanceof List){
    		return ((List)obj).size();
    	}
    	if(obj instanceof Map){
    		return ((Map)obj).size();
    	}
    	return 0;
    }
}
