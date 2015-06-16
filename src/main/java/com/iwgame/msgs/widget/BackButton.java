/**      
 * NewsMainFragment.java Create on 2013-12-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.youban.msgs.R;

/**
 * 自定义返回Button
 * @ClassName: BackButton 
 * @Description: 自定义返回的Button
 * @author yhxu
 * @date 2014-12-5 下午2:17:30 
 * @Version 1.0
 *
 */
public class BackButton extends Button {
	
	public BackButton(Context context) {
		this(context, null);
	}

	public BackButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BackButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setBackgroundResource(R.drawable.back_button_selector);
	}

}
