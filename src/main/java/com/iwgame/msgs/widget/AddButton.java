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
 * 自定义添加Button
 * @ClassName: BackButton 
 * @Description: 自定义添加的Button
 * @author yhxu
 * @date 2014-12-5 下午2:17:30 
 * @Version 1.0
 *
 */
public class AddButton extends Button {
	
	public AddButton(Context context) {
		this(context, null);
	}

	public AddButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AddButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setBackgroundResource(R.drawable.add_button_selector);
	}

}
