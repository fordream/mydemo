package com.iwgame.msgs.module.setting.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.JavaScriptAction1;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.youban.msgs.R;


/**
 * 活动专区的页面
 * 幸运大转盘
 * @author jczhang
 *
 */
public class ActivityDivisionActivity extends BaseSuperActivity implements JavaScriptAction1 {

	private WebView mwebView;
	
	
	/**
	 * 当activity一启动的时候
	 * 就会执行下面的这个方法 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_gamenews_content);
		init();
	}

	/**
	 * 下面的这个
	 * 方法 是做一些初始化操作的方法 
	 * 初始化界面
	 * java 与js交互的一些代码
	 */
	private void init() {
		CookieManager manager = CookieManager.getInstance();
		manager.removeAllCookie();
		mwebView = (WebView)findViewById(R.id.webView);
		mwebView.getSettings().setJavaScriptEnabled(true);
		mwebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		mwebView.setWebChromeClient(new WebChromeClient());
		mwebView.setWebViewClient(new mWebViewClient());
		mwebView.addJavascriptInterface(this, "AppContext");
		AppConfig appConfig = AdaptiveAppContext.getInstance().getAppConfig();
		if (appConfig != null) {
			mwebView.loadUrl(appConfig.getDivisionurl());
		}
	}
	
	
	
	
	/**
	 * 当js调 我这个方法 的时候
	 * 直接退出活动专区这个页面
	 */
	public void returnApp(){
		ActivityDivisionActivity.this.finish();
	}
	
	

	/**
	 * webview窗口
	* @ClassName: mWebViewClient 
	* @Description: 加载网页是耗时的，当加载成员的时候 在把一些相关的参数传过去 
	* @author Administrator
	* @date 2014-12-22 上午10:27:08 
	* @Version 1.0
	*
	*/
	private class mWebViewClient extends WebViewClient {
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			AppConfig appConfig = AdaptiveAppContext.getInstance().getAppConfig();
			if(appConfig != null){
				String appType = appConfig.getAppType();
				long uid = SystemContext.getInstance().getExtUserVo().getUserid();
				String token = SystemContext.getInstance().getToken();
				String deviceId = SystemContext.getInstance().getDeviceId();
				if(appType != null && !appType.isEmpty())
					mwebView.loadUrl("javascript:initWeb(\""+appType+"\","+ uid+",\""+token+"\",0,\""+deviceId+"\")");
			}
		}
	}
}
