/**      
* DatePickerUtil.java Create on 2013-8-12     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

/** 
 * @ClassName: DatePickerUtil 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2013-8-12 下午06:57:20 
 * @Version 1.0
 * 
 */
public class DatePickerUtil {

	public static DatePicker findDatePicker(ViewGroup group){
		if(group != null){
			for(int i=0, j=group.getChildCount();i<j;i++){
				View child = group.getChildAt(i);
				if(child instanceof DatePicker){
					return (DatePicker)child;
				}else if(child instanceof ViewGroup){
					DatePicker result = findDatePicker((ViewGroup)child);
					if(result != null){
						return result;
					}
				}
			}
		}
		return null;
	}
	
}
