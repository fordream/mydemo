package com.iwgame.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.iwgame.msgs.context.SystemContext;

public class SafeUtils {

    public static String getString(String str) {
	if (str == null)
	    return "";
	else
	    return str;
    }

    public static String getString(Object obj) {
	if (obj == null)
	    return "";
	else
	    return obj.toString();
    }

    public static java.util.Date getCurrentTime() {
//	Calendar ca = Calendar.getInstance();
//	java.util.Date tmpnow = new java.util.Date(ca.getTimeInMillis());
//	return tmpnow;
	java.util.Date tmpnow = new java.util.Date(SystemContext.getInstance().getCurrentTimeInMillis());
	return tmpnow;
    }

    public static final String getFormatDate(Date date, String format) {
	String tmpstr = "";
	if (format == null)
	    format = "yyyy-MM-dd HH:mm:ss";
	DateFormat format1 = new SimpleDateFormat(format);
	tmpstr = format1.format(date);
	return tmpstr;
    }

    public static final String getFormatDate(long date, String format) {
	java.util.Date tmpdate = new java.util.Date(date);
	return getFormatDate(tmpdate, format);
    }

    public static final String getCurrentTimeStr(String format) {
	Date tmpdate = getCurrentTime();
	return getFormatDate(tmpdate, format);
    }

    /**
     * 把时间转成只显示当天显示小时和分，其他显示月和日
     * 
     * @param date
     * @return
     */
    public static final String getDate2MyStr(long date) {
	String ret = "";

	if (getCurrentTimeStr("yyyy-MM-dd").equals(getFormatDate(date, "yyyy-MM-dd"))) {
	    ret = getFormatDate(date, "HH:mm");
	} else {
	    ret = getFormatDate(date, "MM-dd");
	}
	return ret;
    }

    /**
     * 把时间显示成，秒前，分钟前，小时前，天前，年月日（一年以上的）
     * 
     * @param date
     * @return
     */
    public static final String getDate2MyStr2(long date) {
//	Calendar ca = Calendar.getInstance();
//	long tmpnow = ca.getTimeInMillis();
	long tmpnow =  SystemContext.getInstance().getCurrentTimeInMillis();
	long diff = tmpnow - date;
	String ret = "";
	if (diff < 60 * 1000l) {
		ret = "刚刚";
	} else if (diff < 60 * 60 * 1000l) {
	    ret = diff / 1000 / 60 + "分钟前";
	} else if (diff < 60 * 60 * 1000 * 24l) {
	    ret = diff / 1000 / 60 / 60 + "小时前";
	} else if (diff < 60 * 60 * 1000 * 24 * 7l) {
	    ret = diff / 1000 / 60 / 60 / 24 + "天前";
	} else {
		ret = "7天前";
	    //ret = getFormatDate(date, "yyyy-MM-dd");
	}

	return ret;
    }

    /**
     * 把距离米转成字符串显示
     * 
     * @param distance
     * @return
     */
    public static final String getDistance2Str(int distance) {
	String ret = "";
	if (distance < 100) {
	    ret = distance + "m";
	} else {
	    ret = (double) distance / 1000 + "km";
	}
	return ret;

    }
    
    /**
     * 把时长转成时分秒的格式，（例如 0h
     * @param duration 毫秒
     * @param isSavems 转换后秒里面是否有小数位保存毫秒数
     * @return
     */
    public static final String getDuration2Str(long duration,boolean isSavems)
    {
	int h = 0 ;
        int m = 0 ;
        int s = 0 ;
        int ms = 0 ;
//	 h =(int) (duration /60 /60 /1000) ;
//	 m =(int) ((duration - h *3600000) /60/1000);
//	 s =(int) (( duration- h*3600000 - m*60000) /1000);
//	 ms =(int) (duration- h*3600000- m*60000 - s *1000 ) ;
	String tmp = "" ;
//	if(h>0)
//	    tmp += h +"h";
//	if(m>0 ||(m == 0 && h >0))
//	    tmp += m +"\'";
//	if(isSavems && ms > 0)
//	{
//	    tmp += s+"."+ms+"\"";
//	}
//	else
//	{
//	    if(s>0 ||(s==0 && !tmp.equals("")))
//		tmp += (ms > 0 ? s+1 : s) +"\"";
//	}
	s =(int)(duration / 1000);
	ms =(int)( duration - s *1000 );
	if(ms > 0) 
	    s= s + 1 ;
	if(s>60) s = 60 ;
	tmp = s+ "\"" ;
	return tmp ;
    }
}
