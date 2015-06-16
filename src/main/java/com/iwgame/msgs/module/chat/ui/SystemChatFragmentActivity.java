/**      
 * SystemChatFragmentActivity.java Create on 2013-11-21     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs.module.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.chat.adapter.ChatMessageAdapter;
import com.iwgame.msgs.utils.MsgsConstants;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * 
 * @ClassName: SystemChatFragmentActivity
 * @Description: 游伴小助手等聊天窗口
 * @author chuanglong
 * @date 2013-11-21 下午7:46:03
 * @Version 1.0
 * 
 */
public class SystemChatFragmentActivity extends BaseFragmentActivity {
	private final String TAG = "SystemChatFragmentActivity";

	private long toSysId;

	private String toDomain;

	private String category;

	private String channelType;

	private int pageType;

	private int titleId;

	ImageButton moreBtn;

	// 弹出框
	PopupWindow popWindow;

	ChatFragment chatFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取上一个页面的传值
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				toSysId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOSYSID);
				toDomain = tmpbundle.getString(SystemConfig.BUNDLE_NAME_TODOMAIN);
				category = tmpbundle.getString(SystemConfig.BUNDLE_NAME_CATEGORY);
				channelType = tmpbundle.getString(SystemConfig.BUNDLE_NAME_CHANNELTYPE);
				pageType = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_PAGETYPE);
				titleId = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_TITLE);
			}
		}
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置显示top左边
		setLeftVisible(true);
		// 设置TOP的中间布局容器
		LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
		titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);
		if (titleId > 0) {
			titleTxt.setText(titleId);
		} else {
			titleTxt.setText(R.string.news_sys_name);
		}
		chatFragment = new ChatFragment();
		Bundle bundle = new Bundle();
		// 聊天需要的参数
		bundle = new Bundle();
		bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISSENDMSG, false);// 允许发消息
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOID, toSysId);
		bundle.putString(SystemConfig.BUNDLE_NAME_TODOMAIN, toDomain == null ? MsgsConstants.DOMAIN_PLATFORM : toDomain);
		bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, category == null ? MsgsConstants.MCC_ANNOUNCE : category);
		bundle.putString(SystemConfig.BUNDLE_NAME_CHANNELTYPE, channelType == null ? MsgsConstants.MC_NOTIFY : channelType);
		bundle.putInt(SystemConfig.BUNDLE_NAME_PAGETYPE, pageType == 0 ? ChatMessageAdapter.PAGE_TYPE_SYSTEM : pageType);
		chatFragment.setArguments(bundle);
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.contentView, chatFragment);
		fragmentTransaction.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
