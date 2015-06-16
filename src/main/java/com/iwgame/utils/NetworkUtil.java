/**      
 * NetworkUtil.java Create on 2013-5-29     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

/**
 * @ClassName: NetworkUtil
 * @Description: TODO(...)
 * @author william
 * @date 2013-5-29 下午1:53:43
 * @Version 1.0
 * 
 */
public class NetworkUtil {

	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.v("error", e.toString());
		}
		return false;
	}

	public static int getAPNType(Context context) {
		int netType = -1;
		if (context != null) {
			ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo == null) {
				return netType;
			}
			int nType = networkInfo.getType();
			if (nType == ConnectivityManager.TYPE_MOBILE) {
				Log.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is " + networkInfo.getExtraInfo());
				if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
					netType = 3;
				} else {
					netType = 2;
				}
			} else if (nType == ConnectivityManager.TYPE_WIFI) {
				netType = 1;
			}
		}
		return netType;
	}

	public static boolean getNetWorkStatus(Context context) {
		boolean success = false;
		if (context != null) {
			// 获得网络连接服务
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
			if(connManager != null){
				// State state = connManager.getActiveNetworkInfo().getState();
				NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if(networkInfo != null){
					State state = networkInfo.getState(); // 获取网络连接状态
					if (State.CONNECTING.equals(state)) { // 判断是否正在使用WIFI网络
						success = true;
					}
				}
				networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				if(networkInfo != null){
					State state = networkInfo.getState(); // 获取网络连接状态
					if (!State.CONNECTING.equals(state)) { // 判断是否正在使用WIFI网络
						success = true;
					}
				}
			}
		}
		return success;
	}

	public static int NETTYPE_NONET = 0;
	public static int NETTYPE_WIFI = 1;
	public static int NETTYPE_CMWAP = 2;
	public static int NETTYPE_CMNET = 3;

	/*
	 * Returns whether the network is available,当前网络是否存在
	 * 
	 * @return true is Available; false is not available
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context != null) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null) {
				LogUtil.error("couldn't get connectivity manager");
				return false;
			} else {
				NetworkInfo[] info = cm.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							return true;
						}
					}
				}
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public static int getNetworkType(Context context) {

		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (extraInfo != null && !extraInfo.equals("")) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;

				} else {

					netType = NETTYPE_CMWAP;

				}

			}

		} else if (nType == ConnectivityManager.TYPE_WIFI) {

			netType = NETTYPE_WIFI;

		}

		return netType;

	}

	public static String getNetworkTypeString(Context context) {
		int netType = getNetworkType(context);
		String str = "NONET";
		switch (netType) {
		case 0:
			str = "NONET";
			break;
		case 1:
			str = "WIFI";
			break;

		case 2:
			str = "WAP";
			break;

		case 3:
			str = "NET";
			break;
		}
		return str;
	}

	/*
	 * 获得本地ip地址
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			LogUtil.error(ex.getMessage());
		}
		return null;
	}

}
