/**      
 * HelpUserFragment.java Create on 2013-11-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.config.SystemConfig;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: HelpUserFragment
 * @Description: 用户帮助
 * @author 王卫
 * @date 2013-11-27 上午11:30:47
 * @Version 1.0
 * 
 */
public class HelpUserFragment extends BaseFragment {

	private static final String TAG = "HelpUserFragment";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.common_webview, container, false);
		// 初始化界面
		initialize(v);
		PTAG = TAG;
		return v;
	}

	/**
	 * 初始化界面
	 */
	private void initialize(View v) {
		WebView webView = (WebView) v.findViewById(R.id.webView);
		webView.loadUrl(SystemConfig.PROTOCOL_HELP);
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
