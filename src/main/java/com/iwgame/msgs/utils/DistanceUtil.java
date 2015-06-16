/**      
 * LocationUtil.java Create on 2013-10-17     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import com.iwgame.msgs.context.SystemContext;

/**
 * @ClassName: LocationUtil
 * @Description: 距离工具类
 * @author 王卫
 * @date 2013-10-17 下午12:18:16
 * @Version 1.0
 * 
 */
public class DistanceUtil {

	/**
	 * 距离转换方式 1、小于1公里显示多少米 2、大于等于1公里小于100公里，显示小数点后两位数（四舍五入）
	 * 3、大于等于100公里，显示整数公里（四舍五入）
	 * 
	 * @param distance
	 * @return
	 */
	public static String covertDistance(int distance) {
		String dis = "";
		if (distance >= 0 && distance < 1000) {
			dis = String.valueOf(distance) + "m";
		} else if (distance >= 1000 && distance < 100000) {
			double d = Double.valueOf(distance) / 1000;
			dis = String.format("%.2f", d) + "km";
		} else if (distance >= 100000) {
			double d = Double.valueOf(distance) / 1000;
			dis = String.format("%.0f", d) + "km";
		}
		return dis;
	}

	/**
	 * 通过别人的位置和自己的位置计算距离，然后转换 1、小于1公里显示多少米 2、大于等于1公里小于100公里，显示小数点后两位数（四舍五入）
	 * 3、大于等于100公里，显示整数公里（四舍五入）
	 * 
	 * @param remotePosition
	 * @return
	 */

	public static String covertDistance(String remotePosition) {
		double ia = 4.9E-324D;
		double h3 = 4.9E-324D;
		String tmpret = "";
		if (remotePosition.equals("0.000000,0.000000")) {
			return "";
		}
		// 距离
		// 本人的经纬度
		double local_lng = 0;
		double local_lat = 0;
		String localPosition = SystemContext.getInstance().getLocation();
		if (localPosition != null && !localPosition.equals("0,0")) {
			String tmp[] = localPosition.split(",");
			if (tmp.length == 2) {
				local_lng = Double.parseDouble(tmp[0]);
				local_lat = Double.parseDouble(tmp[1]);
			}
		}
		if ((local_lng == 0 && local_lat == 0) || local_lng == ia || local_lat == ia) {
			// 自己位置定位不到,位置就不显示
			tmpret = "";
		} else {
			if (remotePosition != null && !remotePosition.equals("0,0")) {
				String tmp[] = remotePosition.split(",");
				if (tmp.length == 2) {
					double remote_lng = Double.parseDouble(tmp[0]);
					double remote_lat = Double.parseDouble(tmp[1]);
					if (local_lng == ia || local_lat == ia) {
						// 对方位置不对
						tmpret = "";
					} else {
						// 显示距离
						int dis = com.iwgame.utils.DistanceUtil.getDistance(local_lng, local_lat, remote_lng, remote_lat);
						if (dis >= 10000000) {
							tmpret = "";
						} else {
							tmpret = covertDistance(dis);
						}
					}
				}

			}
		}
		return tmpret;
	}

}
