/**      
 * BaseSuperFragmentActivity.java Create on 2015-3-30     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;

import com.iwgame.msgs.context.SystemContext;

/**
 * @ClassName: BaseSuperFragmentActivity
 * @Description: 基础超类FragmentActivity 处理系统消息通知等
 * @author 王卫
 * @date 2015-3-30 上午11:37:06
 * @Version 1.0
 * 
 */
public class BaseSuperFragmentActivity extends FragmentActivity {

	private Dialog mDialog;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		SystemContext.getInstance().setCurrentActivity(this);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		SystemContext.getInstance().setCurrentActivity(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if(mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
		}
		super.onDestroy();
	}
	
	public Dialog getDialog() {
		return mDialog;
	}

	public void setDialog(Dialog mDialog) {
		if(mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
			mDialog = null;
		}
		this.mDialog = mDialog;
	}

}
