/**      
 * TimeUtil.java Create on 2013-5-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: (Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateUtils;

import com.iwgame.msgs.context.SystemContext;

/**
 * @ClassName: TimeUtil
 * @Description: TODO(...)
 * @author william
 * @date 2013-5-23 上午11:31:55
 * @Version 1.0
 * 
 */
public class DateUtil {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String Date2String(Date date) {
		return sdf.format(date);
	}

	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");

	public static String Date2String2(Date date) {
		return sdf2.format(date);
	}

	public static String Date2String3(Date date) {
		return sdf3.format(date);
	}

	/**
	 * 获取格式化时间
	 * 
	 * @return
	 */
	public static String getCurrentTime(long time) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(time);
		String str = formatter.format(curDate);
		return str;
	}

	public static long getNowDayTime() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		long daytime = 0;
		try {
			daytime = format.parse(format.format(new Date())).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return daytime;
	}

	public static boolean isToday(long time) {
		long ls = time - getNowDayTime();
		if (ls > 0)
			return true;
		else
			return false;
	}
	
	public static String getFormatRefreshDate(){
		return "最后更新："+DateUtils.formatDateTime(SystemContext.getInstance().getContext(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
	}
	
	/**
	 * 获取当前时间和
	 * @param date
	 * @return
	 */
	public static long getLaveDay(long date){
		return (System.currentTimeMillis() - date)/(1000*60*60*24);
	}

}
