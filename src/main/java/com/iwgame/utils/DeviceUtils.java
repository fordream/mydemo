package com.iwgame.utils;

import com.iwgame.msgs.module.DeviceUuidFactory;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/*
 * 终端设备相关方法
 */
public class DeviceUtils {
	/*
	 * 获得ismi
	 */
	public static String getISMI(Context context) {
		return ((TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
	}

	/*
	 * 获得ua
	 */
	public static String getUA(Context context) {
		WebView webview;
		webview = new WebView(context);
		webview.layout(0, 0, 0, 0);
		WebSettings settings = webview.getSettings();
		String ua = settings.getUserAgentString();
		return ua;
	}
	
	/**
	 * 获取设备ID
	 * @return
	 */
	public static String getDeviceId(Context context){
		DeviceUuidFactory deviceUuidFactory = new DeviceUuidFactory(context);
		return deviceUuidFactory.getDeviceUuid().toString();
	}

}
