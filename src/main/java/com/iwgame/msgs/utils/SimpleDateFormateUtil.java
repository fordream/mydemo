package com.iwgame.msgs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 这个类是将时间戳
 * 转换成日期
 * 以及转换成时间
 * @author Administrator
 *
 */
public class SimpleDateFormateUtil {

	
	
	/**
	 * 将时间戳转换成日期
	 * @return
	 */
	public static String switchToDate(long time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date(time));
		return date;
	}
	
	
	
	/**
	 * 将时间戳转换成时间
	 * @return
	 */
	public static String switchToTime(long miliseconds){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(new Date(miliseconds));
		return time;
	}



	public static String toDateNoYear(long l) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		String date = sdf.format(new Date(l));
		return date;
	}



	public static CharSequence toTimeNoscecond(long l) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String time = sdf.format(new Date(l));
		return time;
	}
}
