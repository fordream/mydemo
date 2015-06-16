/**      
 * GameFragment2.java Create on 2014-4-9     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)
 */
package com.iwgame.msgs.module.game.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.JavaScriptAction;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.vo.local.GameNewDetailVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.ScrollWebView;
import com.iwgame.msgs.widget.ScrollWebView.OnScrollChangedCallback;
import com.iwgame.msgs.widget.ShareButton;
import com.youban.msgs.R;

/**
 * 攻略资讯
 * @ClassName: GameNewsFragment 
 * @Description: 攻略资讯展示
 * @author yhxu
 * @date 2014-12-6 下午1:28:22 
 * @Version 1.0
 *
 */
public class GameNewsFragment extends BaseFragment implements OnClickListener, JavaScriptAction{
	protected static final String TAG = "GameNewsFragment";
	private LayoutInflater inflater;
	private long uid;
	private View v = null;
	private ScrollWebView mWebView;
	private TextView titleTxt;
	private LinearLayout left;
	private LinearLayout rightView;
	private LinearLayout topicLeftShowView;
	private LinearLayout detailLeftShowView;
	private int pageId;
	private String imageUrl = "";
	private String shareTitle = "";
	private String shareContent = "";
	private LinearLayout bottomHitView;
	private Animation inAnimation;
	private Animation outAnimation;
	private GameNewDetailVo detailVo;
	private boolean isNeedChangeTab = false;
	private boolean isHasSrcoll = false;
	private float y;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PTAG = TAG;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SystemContext.getInstance().isUnAuth()) {
			// 会话失效， 主动注销，token不存在/认证失败，都到登录页
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED, true);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;


		ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
		if (vo != null) {
			long newuid = vo.getUserid();
			if (newuid != uid) {
				uid = newuid;
				v = null;
			}
		}
		if(v == null){
			v = inflater.inflate(R.layout.common_content, container, false);
			// 初始化界面
			init(v);
		}
		ViewGroup parent = (ViewGroup) v.getParent();
		if(parent != null){
			parent.removeView(v);
		}
		return v;
	}

	/**
	 * 初始化
	 */
	private void init(View v) {
		bottomHitView = (LinearLayout) v.findViewById(R.id.bottomHitView);
		bottomHitView.setVisibility(View.VISIBLE);
		// top左边菜单
		left = (LinearLayout) v.findViewById(R.id.left);
		left.setVisibility(View.VISIBLE);
		//移除所有的view
		left.removeAllViews();
		topicLeftShowView = (LinearLayout) inflater.inflate(R.layout.game_topic_left_layout, null);
		ImageView imageView = (ImageView) topicLeftShowView.findViewById(R.id.game_topic_left_imageview);
		imageView.setImageResource(R.drawable.title_image_wow);

		TextView textView = (TextView) topicLeftShowView.findViewById(R.id.game_topic_left_textview);
		textView.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_top_desc_textcolor());
		textView.setTextSize(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textsize());
		String sDesc = AdaptiveAppContext.getInstance().getAppConfig().getDisplay_top_desc_text() == null 
				? "":AdaptiveAppContext.getInstance().getAppConfig().getDisplay_top_desc_text();
		textView.setText(sDesc);
		topicLeftShowView.setVisibility(View.VISIBLE);
		left.addView(topicLeftShowView);
		detailLeftShowView = (LinearLayout) inflater.inflate(R.layout.game_topic_left_back_layout, null);
		Button button = (Button) detailLeftShowView.findViewById(R.id.leftBtn);
		button.setOnClickListener(this);
		detailLeftShowView.setVisibility(View.GONE);
		left.addView(detailLeftShowView);

		// 设置title
		titleTxt = (TextView) v.findViewById(R.id.titleTxt);
		titleTxt.setVisibility(View.GONE);
		//titleTxt.setText(getResources().getString(R.string.game_title_info));

		// 添加top右边功能按钮
		Button shareBtn = new ShareButton(v.getContext());
		rightView = (LinearLayout) v.findViewById(R.id.rightView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		rightView.addView(shareBtn, params);
		rightView.setVisibility(View.INVISIBLE);
		shareBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//分享
				shareWebPage();
			}
		});

		// 设置中间内容的布局文件
		LinearLayout contentView = (LinearLayout) v.findViewById(R.id.contentView);
		contentView.removeAllViews();
		final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_gamenews_content, null);
		contentView.addView(view, params);
		mWebView = (ScrollWebView) view.findViewById(R.id.webView);
		mWebView.setBackgroundColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_webview_bgcolor());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setPluginState(PluginState.ON);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		//mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		mWebView.addJavascriptInterface(this, "AppContext");
		mWebView.setWebViewClient(new mWebViewClient());
		mWebView.setWebChromeClient(new WebChromeClient());
		
		mWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) {
				//按下
				case MotionEvent.ACTION_DOWN:
					y = event.getY();
					break;
				//滑动
				case MotionEvent.ACTION_MOVE:
					float temp = y;
					y = event.getY();
					if(y < temp){// 上拉
						if(isNeedChangeTab){
							((MainFragmentActivity)getActivity()).showOrDismissTabHost(false, null, null);
						}
					}else if(y > temp){//下拉
						if(isNeedChangeTab){
							((MainFragmentActivity)getActivity()).showOrDismissTabHost(true, null, null);
						}
					}
					break;
				//停止
				case MotionEvent.ACTION_UP:
					y = event.getY();
					break;
				default:
					break;
				}
				return false;
			}
		});
		mWebView.setOnScrollChangedCallback(new OnScrollChangedCallback() {

			@Override
			public void onScroll(int l, int t, int oldl, int oldt) {
			}
		});
		String sUrl = AdaptiveAppContext.getInstance().getAppConfig().getGame_doMain();
		if(sUrl != null && !sUrl.isEmpty()){
			mWebView.loadUrl(sUrl);
		}
		AppConfig appConfig = AdaptiveAppContext.getInstance().getAppConfig();
		if(appConfig != null){
			String appType = appConfig.getAppType();
			long gameId = appConfig.getGameId();
			if(appType != null && !appType.isEmpty() && gameId > 0)
				mWebView.loadUrl("javascript:initWeb(\""+appType+"\","+ gameId+",0)");
		}
	}
	
	android.os.Handler mHandler = new android.os.Handler() {

		@Override 

		public void handleMessage(Message msg) {  
			super.handleMessage(msg);  
			switch (msg.arg1) {
			case 1:
				String title = (String) msg.obj;
				int showType = msg.what;
				if(showType == 0){//首页显示模式
					topicLeftShowView.setVisibility(View.VISIBLE);
					detailLeftShowView.setVisibility(View.GONE);
				}else{
					if(title != null && !title.isEmpty()){ 
						titleTxt.setVisibility(View.VISIBLE);
						titleTxt.setText(title);
					}else{
						titleTxt.setVisibility(View.GONE);
					}
					topicLeftShowView.setVisibility(View.GONE);
					detailLeftShowView.setVisibility(View.VISIBLE);
				}
				bottomHitView.setVisibility(View.VISIBLE);
				isHasSrcoll = false;
				if(pageId <= 0){
					//当前情况下隐藏分享按钮
					rightView.setVisibility(View.INVISIBLE);
					bottomHitView.setVisibility(View.VISIBLE);
					isNeedChangeTab = false;
				}else{
					rightView.setVisibility(View.VISIBLE);
					bottomHitView.setVisibility(View.GONE);
					isNeedChangeTab = true;
				}
				break;
			case 2:
				break;
			default:
				break;
			} 
		}
	};

	private class mWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			isHasSrcoll = false;
			isNeedChangeTab = false;
			if(bottomHitView != null){
				bottomHitView.setVisibility(View.VISIBLE);
			}
			((MainFragmentActivity)getActivity()).showOrDismissTabHost(true, null, null);

			view.loadUrl(url);
			return true;
		}
	}

	/**
	 * 更改头部显示视图
	 */
	@Override
	public void changeTopView(String title, int showType, int pageId) {
		this.pageId = pageId;
		android.os.Message msg = android.os.Message.obtain();
		msg.arg1 = 1;
		msg.what = showType;
		msg.obj = title;
		mHandler.sendMessage(msg);
	}

	@Override
	public void setShareData(String imageUrl, String shareTitle,
			String shareContent) {
		this.imageUrl = imageUrl;
		this.shareTitle = shareTitle;
		this.shareContent = shareContent;
		if(detailVo == null){
			detailVo = new GameNewDetailVo();
		}
		detailVo.setIcon(imageUrl);
		detailVo.setTitle(shareTitle);
		detailVo.setContent(shareContent);
	}

	/**
	 * 打开攻略贴吧列表页
	 */
	@Override
	public void gotoTopicListPage() {
		Intent intent = new Intent(getActivity(), GameTopicListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, AdaptiveAppContext.getInstance().getAppConfig().getGameId());
		bundle.putString("From", TAG);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		startActivity(intent);
	}

	/**
	 * 打开帖子详情页面
	 */
	@Override
	public void gotoTopicDetailPage(long topicId) {
		Intent intent = null;
		Bundle bundle = null;
		intent = new Intent(this.getActivity(), TopicDetailActivity.class);
		bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, topicId);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		startActivity(intent);		
	}

	/**
	 *  隐藏或显示下方TabHost
	 *  isShow true显示false隐藏
	 */
	@Override
	public void showOrDismissTabHost(boolean isShow) {
		android.os.Message msg = android.os.Message.obtain();
		msg.arg1 = 2;
		msg.obj = isShow;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.leftBtn){
			if(mWebView.canGoBack()){
				if(titleTxt != null){
					titleTxt.setText("");
				}
				isNeedChangeTab = false;
				isHasSrcoll = false;
				if(bottomHitView != null){
					bottomHitView.setVisibility(View.VISIBLE);
				}
				((MainFragmentActivity)getActivity()).showOrDismissTabHost(true, null, null);
				//浏览器回退
				mWebView.goBack();
			}

		}
	}

	/**
	 * 分享贴吧
	 */
	private void shareWebPage(){
		if(FastClickLimitUtil.isFastClick())
			return ;
		if(shareTitle == null || shareContent == null){
			return ;
		}

		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);//类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_WEBPAGE);//类型为网页
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_GAMENEW_DETIAL);//内部分享类型
		shareDate.setTargetId(pageId);//页面id(目标ID)
		shareDate.setTargetName(shareTitle);//页面名称（目标名称）
		shareDate.setTitle(shareTitle);//标题
		shareDate.setText(shareContent);//内容

		if(imageUrl != null && !imageUrl.isEmpty()){
			shareDate.setImageUrl(imageUrl);
			shareDate.setImagePath(imageUrl);
		}
		ShareCallbackListener listener = new ShareCallbackListener() {

			@Override
			public void doSuccess(String plamType) {
				//分享贴吧信息
			}

			@Override
			public void doFail() {
			}
		};
		ShareManager.getInstance().share(getActivity(), inflater, detailVo, shareDate, listener );
	}


}
