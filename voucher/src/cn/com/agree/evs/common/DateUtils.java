package cn.com.agree.evs.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	public static final String allPattern = "yyyyMMddHHmmssSSS";
	
	public static String getDateByPattern(String pattern) {
		return new SimpleDateFormat(pattern).format(new Date());
	}
}
