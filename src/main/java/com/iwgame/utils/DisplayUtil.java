/**      
 * D.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * @ClassName: D
 * @Description: TODO(...)
 * @author william
 * @date 2013-9-3 下午2:51:09
 * @Version 1.0
 * 
 */
public class DisplayUtil {
	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return  (int)(pxValue / scale + 0.5f);
	}


	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		if (context != null) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f);
		} else {
			return 0;
		}
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static float px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return pxValue / fontScale;
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static float sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return spValue * fontScale;
	}

	/**
	 * 获取屏幕宽度(px)
	 * 
	 * @param act
	 * @return
	 */
	public static int widthPx(Activity act) {
		DisplayMetrics dm = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度(px)
	 * 
	 * @param act
	 * @return
	 */
	public static int heightPx(Activity act) {
		DisplayMetrics dm = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * 获取屏幕宽度(dp)
	 * 
	 * @param act
	 * @return
	 */
	public static float widthDip(Activity act) {
		DisplayMetrics dm = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return  dm.widthPixels / dm.density ;
	}

	/**
	 * 获取屏幕宽度(dp)
	 * 
	 * @param act
	 * @return
	 */
	public static float heightDip(Activity act) {
		DisplayMetrics dm = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return  dm.heightPixels / dm.density ;
	}
	
	/**
	 * 获得状态条的高度
	 * @param act
	 * @return
	 */
	public static int getStatusHeightPx(Activity act)
	{
		Rect rect = new Rect();
	    act.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
	   return  rect.top;
	}
	
	
	/**
	 * 通知字体大小获得字体的高度
	 * @param fontSize
	 * @return
	 */
	public static  int getFontHeight(float fontSize)  
	{  
	    Paint paint = new Paint();  
	    paint.setTextSize(fontSize);  
	    FontMetrics fm = paint.getFontMetrics();  
	    //return (int) Math.ceil(fm.descent - fm.top) + 2; 
	    return (int) Math.ceil(fm.descent - fm.ascent)  ;
	}  
	
}
