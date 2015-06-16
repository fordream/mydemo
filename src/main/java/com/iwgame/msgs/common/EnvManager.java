/**      
 * EnvManager.java Create on 2014-1-16     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.utils.SessionUtil;

/**
 * @ClassName: EnvManager
 * @Description: 设备信息管理
 * @author 王卫
 * @date 2014-1-16 下午4:32:24
 * @Version 1.0
 * 
 */
public class EnvManager {

	/**
	 * 设备采集参数常量
	 */
	// 设备系统版本
	public static String D_SVERSION = "d_sversion";
	// 设备网络类型
	public static String D_NTYPE = "d_ntype";
	// 手机型号
	public static String D_MODEL = "d_model";
	// IMSI
	public static String D_IMSI = "d_imsi";
	// 设备的mac地址
	public static String D_MAC = "d_mac";
	// 位置信息
	public static String D_POSITION = "d_position";
	// ip地址信息
	public static String D_IP = "d_ip";

	private static byte[] lock = new byte[0];

	private static EnvManager instance = null;

	private TelephonyManager tm;

	private Map<String, String> envInfo = new HashMap<String, String>();

	private EnvManager() {
		Context context = SystemContext.getInstance().getContext();
		if (context != null) {
			// 系统版本号
			tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
			String sv = tm.getDeviceSoftwareVersion();
			if (sv != null) {
				envInfo.put(D_SVERSION, sv);
			}
			/*
			 * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS
			 * GPRS网络 1 NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3
			 * NETWORK_TYPE_HSDPA HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9
			 * NETWORK_TYPE_HSPA HSPA网络 10 NETWORK_TYPE_CDMA CDMA网络,IS95A 或
			 * IS95B. 4 NETWORK_TYPE_EVDO_0 EVDO网络, revision 0. 5
			 * NETWORK_TYPE_EVDO_A EVDO网络, revision A. 6 NETWORK_TYPE_1xRTT
			 * 1xRTT网络 7
			 */
			envInfo.put(D_NTYPE, String.valueOf(tm.getNetworkType()));
			String imsi = tm.getSubscriberId();
			if (imsi != null)
				envInfo.put(D_IMSI, imsi);
			String model = android.os.Build.MODEL; // 手机型号
			if (model != null)
				envInfo.put(D_MODEL, model);
			String mac = getLocalMacAddress();
			if (mac != null) {
				envInfo.put(D_MAC, mac);
			}
		}
	}

	public static EnvManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new EnvManager();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/**
	 * 获取设备信息
	 * 
	 * @return
	 */
	public Map<String, String> getEnvMap() {
		if (SessionUtil.getLongClient() != null && SessionUtil.getShortClient() != null) {
			envInfo.put(D_POSITION, SystemContext.getInstance().getLocation());
		}
		return envInfo;
	}

	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) SystemContext.getInstance().getContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

}
