/**      
 * HelpAboutFragment.java Create on 2013-11-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.utils.AppUtils;
import com.youban.msgs.R;

/**
 * @ClassName: HelpAboutFragment
 * @Description: 关于我们
 * @author 王卫
 * @date 2013-11-27 上午11:30:01
 * @Version 1.0
 * 
 */
public class HelpAboutFragment extends BaseFragment {

	private static final String TAG = "HelpAboutFragment";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.setting_help_about_info, container, false);
		// 初始化界面
		initialize(v);
		PTAG = TAG;
		return v;
	}

	/**
	 * 初始化界面
	 */
	private void initialize(View v) {
		TextView vesrion = (TextView) v.findViewById(R.id.version);
		String versionDesc = "";
		int startUp = AdaptiveAppContext.getInstance().getAppConfig().getStartup();
		if (startUp == 1) {// 游伴
			versionDesc = "版本号:" + AppUtils.getLocalAppVersionName(getActivity());
		}else{// 攻略
			versionDesc = "版本号:" + AppUtils.getLocalAppVersionName(getActivity())+"("+"内核"+SystemContext.COREDESC+")";
		}
		vesrion.setText(versionDesc);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		//MobclickAgent.onPageStart(TAG);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		//MobclickAgent.onPageEnd(TAG);
	}

}
