/**      
* SysOfficialChatFragmentActivity.java Create on 2014-4-11     
*      
* Copyright (c) 2014 by GreenShore Network
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
 * @ClassName: SysOfficialChatFragmentActivity 
 * @Description: TODO(系统公告页面) 
 * @author chuanglong
 * @date 2014-4-11 上午11:21:33 
 * @Version 1.0
 * 
 */
public class SysOfficialChatFragmentActivity extends BaseFragmentActivity {
    private final String TAG = "SysOfficialChatFragmentActivity";
    long toId ;
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
		toId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOSYSID);
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
	titleTxt.setText(R.string.news_sysadmin_name);

	chatFragment = new ChatFragment();

	Bundle bundle = new Bundle();
	// 聊天需要的参数
	bundle = new Bundle();
	bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISSENDMSG, false);// 允许发消息
	bundle.putLong(SystemConfig.BUNDLE_NAME_TOID, toId);
	bundle.putString(SystemConfig.BUNDLE_NAME_TODOMAIN, MsgsConstants.DOMAIN_PLATFORM);
	bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, MsgsConstants.MCC_OFFICIAL);
	bundle.putString(SystemConfig.BUNDLE_NAME_CHANNELTYPE, MsgsConstants.MC_PUB);
	bundle.putInt(SystemConfig.BUNDLE_NAME_PAGETYPE,ChatMessageAdapter.PAGE_TYPE_SYSTEM);
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

}
