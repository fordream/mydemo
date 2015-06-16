/**      
 * NewsMainFragment.java Create on 2013-12-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.youban.msgs.R;

/**
 * 自定义线型布局，
 * @ClassName: TitleLinearLayout 
 * @Description: 背景会根据App类型适配
 * @author yhxu
 * @date 2014-12-5 下午2:17:30 
 * @Version 1.0
 *
 */
public class TitleLinearLayout extends LinearLayout {
	public TitleLinearLayout(Context context) {
		this(context, null);
	}

	public TitleLinearLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setBackgroundResource(R.drawable.common_title_bg);
	}

}
