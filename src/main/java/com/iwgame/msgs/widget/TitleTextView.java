/**      
 * NewsMainFragment.java Create on 2013-12-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;

/**
 * 自定义TextView
 * @ClassName: TitleTextView 
 * @Description: 字体大小和颜色根据App类型自动适配
 * @author yhxu
 * @date 2014-12-5 下午2:17:30 
 * @Version 1.0
 *
 */
public class TitleTextView extends TextView {

	private int textColor;
	private float textSize;

	public TitleTextView(Context context) {
		this(context, null);
	}

	public TitleTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		textColor = AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor();
		textSize = AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textsize();
		setTextColor(textColor);
		setTextSize(textSize);
	}

}
