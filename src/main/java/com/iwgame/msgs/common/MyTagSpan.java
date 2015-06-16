/**      
 * MyTagSpan.java Create on 2014-1-9     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.text.style.ClickableSpan;
import android.view.View;

import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: MyTagSpan
 * @Description: TODO(自己定义标签span)
 * @author chuanglong
 * @date 2014-1-9 上午10:56:29
 * @Version 1.0
 * 
 */
public class MyTagSpan extends ClickableSpan {
	private String TAG = "MyTagSpan";
	private String url;
	private MyTagClickListener myTagClickListener = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.text.style.ClickableSpan#onClick(android.view.View)
	 */
	@Override
	public void onClick(View paramView) {
		if (FastClickLimitUtil.isFastClick())
			return;
		if (myTagClickListener != null) {
			myTagClickListener.onClick(url);
		} else {
			LogUtil.w(TAG, "tagclicklistener is null ,so click no response");
		}
	}

	public MyTagSpan(String url) {
		this.url = url;

	}

	public void setMyTagClickListener(MyTagClickListener listener) {
		myTagClickListener = listener;
	}

	public interface MyTagClickListener {
		public void onClick(String param);
	}

}
