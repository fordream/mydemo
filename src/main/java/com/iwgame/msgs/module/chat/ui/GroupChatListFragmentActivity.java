/**      
* GroupChatListFragmentActivity.java Create on 2014-2-8     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.chat.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.message.ui.MessageFragment2;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/** 
 * @ClassName: GroupChatListFragmentActivity 
 * @Description: TODO(有消息的公会列表) 
 * @author chuanglong
 * @date 2014-2-8 下午4:59:57 
 * @Version 1.0
 * 
 */
public class GroupChatListFragmentActivity extends BaseFragmentActivity {
    private final String TAG = "GroupMassMsgListFragmentActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	init();
//	//更新公会聊天室未读消息数为0
//	SystemContext.getInstance().setGroupChatUnReadCount(0);
    }

    /**
     * 初始化
     */
    private void init() {

	View top = findViewById(R.id.top);
	top.setVisibility(View.GONE);
	MessageFragment2 messageFragment = new MessageFragment2();
	Bundle bundle = new Bundle();
	bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISGROUPCHATMSGLIST,true);
	messageFragment.setArguments(bundle);
	FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
	fragmentTransaction.replace(R.id.contentView, messageFragment);
	// fragmentTransaction.addToBackStack(null);
	fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
    }

    @Override
    protected void onRestart() {
	// TODO Auto-generated method stub
	super.onRestart();
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
