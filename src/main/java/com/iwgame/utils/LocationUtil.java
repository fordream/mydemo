/**      
 * LocationUtil.java Create on 2013-9-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 * @ClassName: LocationUtil
 * @Description: 位置
 * @author 王卫
 * @date 2013-9-27 下午02:46:13
 * @Version 1.0
 * 
 */
public class LocationUtil {

	private static final String TAG = "LocationUtil";

	/**
	 * 获取本地位置对象
	 * 
	 * @param context
	 * @return
	 */
	public static Location getLocation(Context context) {
		if (context != null) {
			try {
				// 创建lcoationManager对象
				LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				// 创建一个criteria对象
				Criteria criteria = new Criteria();
				// 设置为最大精度
				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
				// 设置不需要获取海拔数据
				criteria.setAltitudeRequired(false);
				// 设置不需要获取方位信息
				criteria.setBearingRequired(false);
				// 设置允许产生资费
				criteria.setCostAllowed(true);
				// 要求低耗电
				criteria.setPowerRequirement(Criteria.POWER_LOW);
				// 为程序的通用性，动态选择location provider
				String provider = manager.getBestProvider(criteria, true);

				if (provider != null) {
					Location location = manager.getLastKnownLocation(provider);
					return location;
				} else {
					return null;
				}
			} catch (Exception e) {
				LogUtil.e(TAG, "获取为为空");
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 本地位置对象转换为字符串
	 * 
	 * @param location
	 * @return
	 */
	public static String covertLocation(Location location) {
		if (location != null) {
			// 经度
			double longitude = location.getLongitude();
			// 纬度
			double latitude = location.getLatitude();
			
			LogUtil.d(TAG, "-------->经度："+longitude+"------->维度："+latitude);
			return longitude + "," + latitude ;
		} else {
			return "0,0";
		}
	}
	
	
	/**
	 * 获得当前的纬度
	 * @param context
	 * @return
	 */
	public static double getLatitude(Context context)
	{
		if (context != null) 
		{
			 Location location = getLocation( context);
			 if(location != null)
			 {
				 return location.getLatitude();
			 }
		}
		return 0;
	}
	
	/**
	 * 获得当前的经度
	 * @param context
	 * @return
	 */
	public static double getLongitude(Context context)
	{
		if (context != null) 
		{
			 Location location = getLocation( context);
			 if(location != null)
			 {
				 return location.getLongitude();
			 }
		}
		return 0;
	}
	
	/**
	 * 获得当前的经纬度的字符串，格式为"经度,纬度"
	 * @param context
	 * @return
	 */
	public static String getStringLocation(Context context)
	{
		if (context != null) 
		{
			 Location location = getLocation( context);
			 if(location != null)
			 {
				 return location.getLongitude() + "," + location.getLatitude();
			 }
		}
		return "0,0";
	}
	

	

}
