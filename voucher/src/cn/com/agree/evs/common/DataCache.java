package cn.com.agree.evs.common;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @subject 数据池
 * @author zhucl
 * @date 2018-06-26 09:20:43
 * @version v1.0 
 */
public class DataCache {
    public static ThreadLocal<Map<String, Object>> dataMap = new ThreadLocal<>();
    public static File pFontBlackFile;
    public static File pFontNormalFile;
    
    public static Object getValue(String key) {
    	Map<String, Object> tempMap = dataMap.get();
    	if (null == tempMap || tempMap.isEmpty()) {
			return null;
		}
    	return tempMap.get(key);
    }
    
    public static void putValue(String key, Object value) {
    	Map<String, Object> tempMap = dataMap.get();
    	if (null == tempMap) {
    		tempMap = new LinkedHashMap<>();
		}
    	tempMap.put(key, value);
    	dataMap.set(tempMap);
    }
}
