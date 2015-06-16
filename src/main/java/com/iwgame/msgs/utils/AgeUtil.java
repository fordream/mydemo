/**      
* AgeUtil.java Create on 2014-5-6     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/** 
 * @ClassName: AgeUtil 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-5-6 上午10:43:46 
 * @Version 1.0
 * 
 */
public class AgeUtil {

	public static int NOT_SET_AGE = -1;
	/**
	 * 根据出生年份获取年龄
	 * @param birth
	 * @return
	 */
	public static int convertAgeByBirth(int birth){
		if(birth==0) return NOT_SET_AGE;
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get(Calendar.YEAR);// 得到年
		int age = year - birth;
		age = age < 0 ? 0 : age;
		return age;
	}
	
}
