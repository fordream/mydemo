/**      
 * WeiboBindingActivity.java Create on 2014-4-25     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseActivity;

/**
 * @ClassName: WeiboBindingActivity
 * @Description: 微博绑定界面
 * @author 王卫
 * @date 2014-4-25 上午10:57:34
 * @Version 1.0
 * 
 */
public class WeiboAuthActivity extends BaseActivity implements OnClickListener {

	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	//private SsoHandler mSsoHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化功能
		initialize();
	}

	/**
	 * 初始化功能
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		// 设置TITLE
		titleTxt.setText("添加新浪微博好友");
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.user_weibo_auth, null), params);
		findViewById(R.id.bindingBtn).setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bindingBtn) {
//			mSsoHandler = WeiboManager.getInstance().auth(this, new WeiboManager.WeiboCallBack() {
//
//				/* (non-Javadoc)
//				 * @see com.iwgame.msgs.sina.weibo.WeiboManager.WeiboCallBack#onCallBackHandler(com.sina.weibo.sdk.auth.sso.SsoHandler)
//				 */
//				@Override
//				public void onCallBackHandler(SsoHandler handler) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onCallBack(Integer code, String resultMsg) {
//					switch (code) {
//					case WeiboManager.ERROR_CODE_OK:
//						WeiboAuthActivity.this.finish();
//						Intent intent = new Intent(WeiboAuthActivity.this, WeiboFriendActivity.class);
//						startActivity(intent);
//						break;
//					case WeiboManager.ERROR_CODE_FAILURE:
//						break;
//					default:
//						break;
//					}
//
//				}
//			});
		}
	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
//		if (mSsoHandler != null) {
//			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//		}
	}
}
