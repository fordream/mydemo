/**      
* DistanceUtil.java Create on 2013-10-15     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.utils;

/** 
 * @ClassName: DistanceUtil 
 * @Description: 距离计算辅助类
 * @author nst
 * @date 2013-10-15 下午6:59:08 
 * @Version 1.0
 * 
 */
public class DistanceUtil {
	
	private static double EARTH_RADIUS = 6378.137; // 地球半径
	
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static int getDistance(double lng1,double lat1, double lng2,double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000.0;
		return new Double(s*1000).intValue();
	}

}
