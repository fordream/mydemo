/**      
* ClipboardUtil.java Create on 2013-9-29     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.utils;

import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Context;





/** 
 * @ClassName: ClipboardUtil 
 * @Description: TODO(android 剪贴板工具类) 
 * @author 吴禹青
 * @date 2013-9-29 上午11:55:13 
 * @Version 1.0
 * 
 */
public class ClipboardUtil {
	
	/**
	 * 复制字符串到剪贴板管理器
	 * @param context
	 * @param str
	 */
	@SuppressWarnings("deprecation")
	public static void setClipboard(Context context,CharSequence text)
	{
		if(context != null)
		{
			
			if (android.os.Build.VERSION.SDK_INT <11)
			{
				android.text.ClipboardManager cmb = (android.text.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setText(text);
			}
			else
			{
				android.content.ClipboardManager cmb = (android.content.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
				android.content.ClipData clipdata = android.content.ClipData.newPlainText("text label", text);
				cmb.setPrimaryClip(clipdata);
			}
		}
	}
	
	/**
	 * 从剪贴板管理器中获得内容
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static CharSequence getClipboard(Context context)
	{
		CharSequence text = null ;
		if(context != null)
		{
			
			if (android.os.Build.VERSION.SDK_INT <=android.os.Build.VERSION_CODES.HONEYCOMB)
			{
				
				android.text.ClipboardManager cmb = (android.text.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
				text = cmb.getText();
			}
			else
			{
				android.content.ClipboardManager cmb = (android.content.ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
				//无数据
				if(!cmb.hasPrimaryClip())
				{
					return text;
				}
				
				if(cmb.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
				{
					//文本信息
					android.content.ClipData clipdata = cmb.getPrimaryClip();
					Item  item = clipdata.getItemAt(0);
					if(item.getText() != null)
					{
						text = item.getText();
					}
				}
			}
		}
		return text ;
	}
}
