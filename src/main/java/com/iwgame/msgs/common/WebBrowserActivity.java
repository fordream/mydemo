/**      
 * WebBrowerActivity.java Create on 2014-7-24     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.speech.RecognizerResultsIntent;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: WebBrowerActivity
 * @Description: TODO(网页浏览器)
 * @author chuanglong
 * @date 2014-7-24 上午10:00:31
 * @Version 1.0
 * 
 */
public class WebBrowserActivity extends BaseActivity {

    private WebView webView;
    private String url;
    // private BrowserSettings mSettings;
    private ContentResolver mResolver;
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	// 设置显示top左边
	setLeftVisible(true);
	// 设置显示top右边
	setRightVisible(true);
	// 右边增加加载进度
	progressbar = new ProgressBar(this);
	LayoutParams params2 = new LayoutParams(DisplayUtil.dip2px(this, 20), DisplayUtil.dip2px(this, 20));
	progressbar.setLayoutParams(params2);
	LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
	rightView.addView(progressbar);

	// 设置TOP的中间布局容器
	LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
	titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);

	webView = new WebView(this);
	// 设置中间内容
	LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	contentView.addView(webView, params);

	webView.getSettings().setJavaScriptEnabled(true);// 设置使用够执行JS脚本
	webView.getSettings().setBuiltInZoomControls(true);// 设置使支持缩放
	// webView.getSettings().setDefaultFontSize(5);
	webView.setWebViewClient(new WebViewClient() {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO Auto-generated method stub
		view.loadUrl(url);// 使用当前WebView处理跳转
		return true;// true表示此事件在此处被处理，不需要再广播
	    }

	    @Override
	    // 转向错误时的处理
	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		// TODO Auto-generated method stub
		ToastUtil.showToast(WebBrowserActivity.this, "异常：" + description);
	    }

	    /*
	     * (non-Javadoc)
	     * 
	     * @see
	     * android.webkit.WebViewClient#onPageFinished(android.webkit.WebView
	     * , java.lang.String)
	     */
	    @Override
	    public void onPageFinished(WebView view, String url) {
		// TODO Auto-generated method stub
		super.onPageFinished(view, url);
		progressbar.setVisibility(View.GONE);

	    }

	    /*
	     * (non-Javadoc)
	     * 
	     * @see
	     * android.webkit.WebViewClient#onPageStarted(android.webkit.WebView
	     * , java.lang.String, android.graphics.Bitmap)
	     */
	    @Override
	    public void onPageStarted(WebView view, String url, Bitmap favicon) {
		// TODO Auto-generated method stub
		super.onPageStarted(view, url, favicon);
		progressbar.setVisibility(View.VISIBLE);
	    }

	});

	webView.setWebChromeClient(new WebChromeClient() {

	    /*
	     * (non-Javadoc)
	     * 
	     * @see
	     * android.webkit.WebChromeClient#onProgressChanged(android.webkit
	     * .WebView, int)
	     */
	    @Override
	    public void onProgressChanged(WebView view, int newProgress) {
		// TODO Auto-generated method stub
		super.onProgressChanged(view, newProgress);
		// 进度
	    }

	    /*
	     * (non-Javadoc)
	     * 
	     * @see
	     * android.webkit.WebChromeClient#onReceivedTitle(android.webkit
	     * .WebView, java.lang.String)
	     */
	    @Override
	    public void onReceivedTitle(WebView view, String title) {
		// TODO Auto-generated method stub
		super.onReceivedTitle(view, title);
		titleTxt.setText(title);
	    }

	});

	// 获取上一个页面的传值
	final Intent intent = getIntent();
	UrlData urlData = getUrlDataFromIntent(intent);

	if (urlData.isEmpty() || urlData.mUrl.isEmpty()) {
	    webView.loadUrl("http://default");
	} else {
	    webView.loadUrl(urlData.mUrl);
	}

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	// 默认点回退键，会退出Activity，需监听按键操作，使回退在WebView内发生
	if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	    webView.goBack();
	    return true;
	}
	return super.onKeyDown(keyCode, event);
    }

    private UrlData getUrlDataFromIntent(Intent intent) {
	String url = "";
	Map<String, String> headers = null;
	if (intent != null) {
	    final String action = intent.getAction();
	    if (Intent.ACTION_VIEW.equals(action)) {
		url = smartUrlFilter(intent.getData());
		if (url != null && url.startsWith("http")) {
		    final Bundle pairs = intent.getBundleExtra(Browser.EXTRA_HEADERS);
		    if (pairs != null && !pairs.isEmpty()) {
			Iterator<String> iter = pairs.keySet().iterator();
			headers = new HashMap<String, String>();
			while (iter.hasNext()) {
			    String key = iter.next();
			    headers.put(key, pairs.getString(key));
			}
		    }
		}
	    }
	}
	return new UrlData(url, headers, intent);
    }

    private String smartUrlFilter(Uri inUri) {
	if (inUri != null) {
	    return smartUrlFilter(inUri.toString());
	}
	return null;
    }

    protected static final Pattern ACCEPTED_URI_SCHEMA = Pattern.compile("(?i)" + // switch
										  // on
										  // case
										  // insensitive
										  // matching
	    "(" + // begin group for schema
	    "(?:http|https|file):\\/\\/" + "|(?:inline|data|about|javascript):" + ")" + "(.*)");
    // Google search
    final static String QuickSearch_G = "http://www.google.com/m?q=%s";

    final static String QUERY_PLACE_HOLDER = "%s";

    /**
     * Attempts to determine whether user input is a URL or search terms.
     * Anything with a space is passed to search.
     * 
     * Converts to lowercase any mistakenly uppercased schema (i.e., "Http://"
     * converts to "http://"
     * 
     * @return Original or modified URL
     * 
     */
    String smartUrlFilter(String url) {

	String inUrl = url.trim();
	boolean hasSpace = inUrl.indexOf(' ') != -1;

	Matcher matcher = ACCEPTED_URI_SCHEMA.matcher(inUrl);
	if (matcher.matches()) {
	    // force scheme to lowercase
	    String scheme = matcher.group(1);
	    String lcScheme = scheme.toLowerCase();
	    if (!lcScheme.equals(scheme)) {
		inUrl = lcScheme + matcher.group(2);
	    }
	    if (hasSpace) {
		inUrl = inUrl.replace(" ", "%20");
	    }
	    return inUrl;
	}
	if (!hasSpace) {
	    if (Patterns.WEB_URL.matcher(inUrl).matches()) {
		return URLUtil.guessUrl(inUrl);
	    }
	}

	// FIXME: Is this the correct place to add to searches?
	// what if someone else calls this function?

	Browser.addSearchUrl(mResolver, inUrl);
	return URLUtil.composeSearchUrl(inUrl, QuickSearch_G, QUERY_PLACE_HOLDER);
    }

    static class UrlData {
	final String mUrl;
	final Map<String, String> mHeaders;
	final Intent mVoiceIntent;

	UrlData(String url) {
	    this.mUrl = url;
	    this.mHeaders = null;
	    this.mVoiceIntent = null;
	}

	UrlData(String url, Map<String, String> headers, Intent intent) {
	    this.mUrl = url;
	    this.mHeaders = headers;
	    if (RecognizerResultsIntent.ACTION_VOICE_SEARCH_RESULTS.equals(intent.getAction())) {
		this.mVoiceIntent = intent;
	    } else {
		this.mVoiceIntent = null;
	    }
	}

	boolean isEmpty() {
	    return mVoiceIntent == null && (mUrl == null || mUrl.length() == 0);
	}

    };

}
