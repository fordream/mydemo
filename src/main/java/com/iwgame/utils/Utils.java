package com.iwgame.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static String Date2String(Date date) {
		return sdf.format(date);
	}

}
