/**      
 * ProtocolAcitivity.java Create on 2013-11-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.register;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.config.SystemConfig;

/**
 * @ClassName: ProtocolAcitivity
 * @Description: 服务条款和隐私政策协议
 * @author 王卫
 * @date 2013-11-27 下午3:59:43
 * @Version 1.0
 * 
 */
public class ProtocolAcitivity extends BaseActivity {

	private WebView webView;

	private String url;

	private String title = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			url = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_URL);
			title = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TITLE);
		}
		if (url != null) {
			initialize();
		} else {
			finish();
		}
	}

	/**
	 * 初始化
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 添加右边功能按钮
		setRightVisible(false);
		// 设置TITLE
		setTitleTxt(title);
		// 设置内容UI
		getContentView().addView(View.inflate(this, R.layout.common_webview, null));

		webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl(url);
	}

}
