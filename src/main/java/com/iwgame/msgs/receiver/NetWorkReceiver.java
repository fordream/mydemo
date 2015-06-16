/**      
 * BroadCastDemo.java Create on 2014-1-13     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.iwgame.msgs.utils.SessionUtil;
import com.iwgame.sdk.xaction.EnvStatusType;
import com.iwgame.utils.NetworkUtil;

/**
 * @ClassName: BroadCastDemo
 * @Description: 网络状态监听广播接收
 * @author 王卫
 * @date 2014-1-13 下午4:47:57
 * @Version 1.0
 * 
 */
public class NetWorkReceiver extends BroadcastReceiver {
	protected static final String TAG = "NetWorkReceiver";
	public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	private Context mContext = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		if (ACTION.equals(intent.getAction())) {
			// 获取手机的连接服务管理器，这里是连接管理器类
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			State wifiState = null;
			State mobileState = null;
			if (wifiInfo != null) {
				wifiState = wifiInfo.getState();
			}
			if (mobileInfo != null) {
				mobileState = mobileInfo.getState();
			}
			if (wifiState != null && (wifiState == State.CONNECTED || wifiState == State.CONNECTING)) {
			    SessionUtil.onEnvStatusChanged(EnvStatusType.ENV_STATUS_WIFI);
			} else if (mobileState != null && (mobileState == State.CONNECTED || mobileState == State.CONNECTING)) {
			    SessionUtil.onEnvStatusChanged(EnvStatusType.ENV_STATUS_MOBILE_3G);
			} else {
				if (!NetworkUtil.isConnect(context)) {
				    SessionUtil.onEnvStatusChanged(EnvStatusType.ENV_STATUS_NONETWORK);
				} else {
					SessionUtil.onEnvStatusChanged(EnvStatusType.ENV_STATUS_WIFI);
				}
			}
		}

	}

}
