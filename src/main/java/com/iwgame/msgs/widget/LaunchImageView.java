/**      
 * NewsMainFragment.java Create on 2013-12-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.utils.BitmapUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.SelectorUtil;
import com.youban.msgs.R;

/**
 * 自定义启动image
 * @ClassName: LaunchImageView 
 * @Description: 启动image
 * @author yhxu
 * @date 2014-12-5 下午2:17:30 
 * @Version 1.0
 *
 */
public class LaunchImageView extends ImageView {
	public Bitmap luncher_bg = null;
	
	public LaunchImageView(Context context) {
		this(context, null);
	}

	public LaunchImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LaunchImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		luncher_bg = BitmapUtil.getBitmapFromData(context, SystemConfig.APP_LUNCHER_BG, "UTF-8");
		if(luncher_bg != null){
			setImageBitmap(luncher_bg);
		}else{
			SystemContext.getInstance().setLuncharBgLoadTime((long)0);
			setImageResource(R.drawable.common_luncher_bg);
		}
	}
	
}
