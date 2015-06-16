/**      
 * MyLocationListenner.java Create on 2013-10-17     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.baidu;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.baidu.service.BaiduLocationService;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: MyLocationListenner
 * @Description: 百度定位监听类
 * @author 王卫
 * @date 2013-10-17 下午04:43:53
 * @Version 1.0
 * 
 */
public class LocationListener implements BDLocationListener {

	private static final String TAG = "LocationListener";

	private LocationCallBack mCallBack;
	
	private BaiduLocationService mService;

	public LocationListener(LocationCallBack callBack, BaiduLocationService service) {
		mCallBack = callBack;
		mService = service;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.baidu.location.BDLocationListener#onReceiveLocation(com.baidu.location
	 * .BDLocation)
	 */
	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null) {
			LogUtil.i(TAG, "-------->onReceiveLocation：：获取的位置信息为空");
			if(mCallBack != null){
				mCallBack.onBack(null);
				if(mService != null){
					mService.stop();
				}
			}
			return;
		}
		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation) {
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			/**
			 * 格式化显示地址信息
			 */
			// sb.append("\n省：");
			// sb.append(location.getProvince());
			// sb.append("\n市：");
			// sb.append(location.getCity());
			// sb.append("\n区/县：");
			// sb.append(location.getDistrict());
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
		}
		sb.append("\nisCellChangeFlag : ");
		sb.append(location.isCellChangeFlag());
		LogUtil.i(TAG, "-------->获取的位置信息onReceiveLocation：：" + sb.toString());
		if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeOffLineLocation
				|| location.getLocType() == BDLocation.TypeOffLineLocationFail || location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail
				|| location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeCacheLocation) {
			if (mCallBack != null && 4.9E-324D != location.getLatitude() && 4.9E-324D != location.getLongitude()) {
				SystemContext.getInstance().setLocation(location);
				mCallBack.onBack(location);
				if(mService != null){
					mService.stop();
				}
				return;
			} else {
				location = null;
			}
		} else {
			location = null;
		}
		if(mCallBack != null){
			mCallBack.onBack(location);
			if(mService != null){
				mService.stop();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.baidu.location.BDLocationListener#onReceivePoi(com.baidu.location
	 * .BDLocation)
	 */
	@Override
	public void onReceivePoi(BDLocation poiLocation) {
		if (poiLocation == null) {
			return;
		}
		StringBuffer sb = new StringBuffer(256);
		sb.append("Poi time : ");
		sb.append(poiLocation.getTime());
		sb.append("\nerror code : ");
		sb.append(poiLocation.getLocType());
		sb.append("\nlatitude : ");
		sb.append(poiLocation.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(poiLocation.getLongitude());
		sb.append("\nradius : ");
		sb.append(poiLocation.getRadius());
		if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb.append("\naddr : ");
			sb.append(poiLocation.getAddrStr());
		}
		if (poiLocation.hasPoi()) {
			sb.append("\nPoi:");
			sb.append(poiLocation.getPoi());
		} else {
			sb.append("noPoi information");
		}
	}

	public LocationCallBack getmRequestLocationOnCallBack() {
		return mCallBack;
	}

	public void setmRequestLocationOnCallBack(LocationCallBack mRequestLocationOnCallBack) {
		this.mCallBack = mRequestLocationOnCallBack;
	}

}
