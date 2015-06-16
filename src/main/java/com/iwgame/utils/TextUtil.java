/**      
* TextUtil.java Create on 2013-9-29     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.utils;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

/** 
 * @ClassName: TextUtil 
 * @Description: TODO(文本的工具类) 
 * @author 吴禹青
 * @date 2013-9-29 下午2:07:54 
 * @Version 1.0
 * 
 */
public class TextUtil {
	
	/**
	 * 获得字符串中字符的高度
	 * @param paint 字符的Paint
	 * @return
	 */
	public static double getTextHeight(Paint paint)
	{
		double d = 0 ;
		if(paint != null)
		{
			FontMetrics fm = paint.getFontMetrics();
		
			d = Math.ceil(fm.descent - fm.ascent) ;
		}
		return d ;
	}
	
	/**
	 * 获得字符串的总宽度
	 * @param paint 字符的Paint
	 * @param str 字符串
	 * @return 总宽度
	 */
	public static float getTextWidth(Paint paint,String str)
	{
		float f = 0 ;
		if(paint != null && str != null)
		{
			f = paint.measureText(str);
		}
		return f ;
	}
	
	/**
	 * 获得字符串中字符的平均宽度
	 * @param paint
	 * @param str 字符串
	 * @return 单个字符的宽度
	 */
	public static float getTextAverageWidth(Paint paint,String str)
	{
		float f = 0 ;
		if(paint != null && str != null)
		{
			f = paint.measureText(str);
			f = f / str.length() ;
		}
		return f ;
	}

}
