/**      
 * MsgsPopTextItem.java Create on 2013-12-16     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget.popwindow;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youban.msgs.R;

/**
 * @ClassName: MsgsPopTextItem
 * @Description: 文本框条目组件
 * @author 王卫
 * @date 2013-12-16 上午10:26:28
 * @Version 1.0
 * 
 */
public class MsgsPopTextItem extends LinearLayout {

	private String textContent;
	
	
	//获取到里面显示的文字
	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	/**
	 * @param context
	 */
	public MsgsPopTextItem(Context context, String textContent) {
		super(context);
		this.textContent = textContent;
		LinearLayout ll = (LinearLayout) View.inflate(context, R.layout.common_popwindow_text_item, null);
		TextView tv = (TextView) ll.findViewById(R.id.textview);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tv.setText(textContent);
		addView(ll, params);
	}
	
	/**
	 * @param context
	 */
	public MsgsPopTextItem(Context context, String textContent,int textrescolor) {
		super(context);
		this.textContent = textContent;
		LinearLayout ll = (LinearLayout) View.inflate(context, R.layout.common_popwindow_text_item, null);
		TextView tv = (TextView) ll.findViewById(R.id.textview);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tv.setText(textContent);
		tv.setTextColor(textrescolor);
		addView(ll, params);
	}
	
	/**
	 * @param context
	 */
	public MsgsPopTextItem(Context context, int resId, String textContent) {
		super(context);
		this.textContent = textContent;
		LinearLayout ll = (LinearLayout) View.inflate(context, resId, null);
		TextView tv = (TextView) ll.findViewById(R.id.textview);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tv.setText(textContent);
		addView(ll, params);
	}
	
}
