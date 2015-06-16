/**      
* GroupNewsFragment.java Create on 2013-10-25     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.chat.ui;

import android.os.Bundle;
import android.view.View;

import com.iwgame.msgs.common.BaseFragment;
import com.umeng.analytics.MobclickAgent;

/** 
 * @ClassName: GroupNewsFragment 
 * @Description: TODO(...) 
 * @author Administrator
 * @date 2013-10-25 下午5:48:01 
 * @Version 1.0
 * 
 */
public class GroupNewsFragment extends BaseFragment {

	private static final String TAG = "GroupNewsFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		PTAG = TAG;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//MobclickAgent.onPageEnd(TAG);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//MobclickAgent.onPageStart(TAG);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}

}
