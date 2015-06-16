/**      
 * BaseFragment.java Create on 2013-12-12     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: BaseFragment
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-12-12 下午4:12:41
 * @Version 1.0
 * 
 */
public class BaseFragment extends Fragment {
	protected String PTAG="";
//	/**
//	 * 显示LOADING
//	 */
//	protected void showLoading() {
//		DialogManager.getInstance().show(getActivity());
//	}
//
//	/**
//	 * 不显示LOADING
//	 */
//	protected void dismissLoading() {
//		DialogManager.getInstance().dismiss();
//	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(getActivity() != null && getActivity() instanceof FragmentActivity){
			if(!"".equals(PTAG)){
				MobclickAgent.onPageStart(PTAG);
			}
		}
		if (SystemContext.getInstance().isUnAuth()) {
			// 清除全局配置
			SystemContext.getInstance().cleanContext();
			// 会话失效， 主动注销，token不存在/认证失败，都到登录页
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED, true);
			intent.putExtras(bundle);
			startActivity(intent);
			return;
		}
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(getActivity() != null && getActivity() instanceof FragmentActivity){
			MobclickAgent.onPageEnd(PTAG);
		}
	}

}
