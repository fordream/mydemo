/**      
 * GroupMassMsgActivity.java Create on 2013-11-01    
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.chat.adapter.ChatMessageAdapter;

/**
 * @ClassName: GroupChatFragment
 * @Description: TODO(会长群发消息)
 * @author Administrator
 * @date 2013-11-01 上午11:03:46
 * @Version 1.0
 * 
 */
public class GroupSendMassMsgActivity extends BaseFragmentActivity {
	private final String TAG = "GroupMassMsgActivity";
	long groupId;
	ChatFragment chatFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获得传入的用户参数
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				// 反序列化，在本地重建数据
				groupId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOGROUPID);
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

		// 设置显示top右边
		setRightVisible(false);
		// 设置TOP的中间布局容器
		LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
		titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);
		titleTxt.setText("群发消息");

		chatFragment = new ChatFragment();

		Bundle bundle = new Bundle();
		// 聊天需要的参数
		bundle = new Bundle();

		bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISSENDMSG, true);
		bundle.putInt(SystemConfig.BUNDLE_NAME_PAGETYPE, ChatMessageAdapter.PAGE_TYPE_GROUPMANAGEMASSSEND);
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, groupId);
		chatFragment.setArguments(bundle);
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.contentView, chatFragment);
		// fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 如果表情框的弹出了，那么就隐藏表情
			if (chatFragment.getSendMsgView().hideSmileyView())
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
