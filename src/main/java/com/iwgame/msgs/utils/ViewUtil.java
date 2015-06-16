/**      
 * ViewUtil.java Create on 2013-12-11     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.youban.msgs.R;

/**
 * @ClassName: ViewUtil
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-12-11 下午4:07:49
 * @Version 1.0
 * 
 */
public class ViewUtil {

	/**
	 * 没有数据，显示空背景
	 * 
	 * @param contentView
	 * @param resId
	 */
	public static void showNullBgView(Context context, LinearLayout view, Integer resId) {
		view.removeAllViews();
		final LinearLayout v = (LinearLayout) View.inflate(context, R.layout.common_null_data_bg, null);
		ImageView bgIcon = (ImageView) v.findViewById(R.id.bgIcon);
		if (resId != null)
			bgIcon.setBackgroundResource(resId);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view.addView(v, params);
	}

}
