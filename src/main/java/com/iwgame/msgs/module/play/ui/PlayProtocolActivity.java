package com.iwgame.msgs.module.play.ui;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.youban.msgs.R;

public class PlayProtocolActivity extends BaseActivity {
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
			title = bundle.getString("playprotocol");
			url = bundle.getString("playurl");
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
