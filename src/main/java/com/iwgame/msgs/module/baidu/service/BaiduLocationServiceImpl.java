/**      
 * BaiduLocationService.java Create on 2013-10-17     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.baidu.service;

import android.os.Vibrator;

import com.baidu.location.BDLocation;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.baidu.LocationListener;
import com.iwgame.msgs.module.baidu.NotifyLister;

/**
 * @ClassName: BaiduLocationService
 * @Description: 百度位置服务
 * @author 王卫
 * @date 2013-10-17 下午02:56:27
 * @Version 1.0
 * 
 */
public class BaiduLocationServiceImpl implements BaiduLocationService {

	public LocationClient mLocationClient;
	public LocationListener mLocationListener;
	public GeofenceClient mGeofenceClient;

	public NotifyLister mNotifyer = null;
	public Vibrator mVibrator01;
	public static String TAG = "BaseApplication";

	private long lastReqTime;
	// 获取地理位置时间间隔（5分钟）
	// private final int time = 300000;
	private final int delayTime = 60000;
	// 缓存LOCATION
	private BDLocation location;

	private boolean isinit = false;

	private boolean isnet = true;

	private boolean isStart = true;
	// 上一次请求时间
	private long oldRequestTime = System.currentTimeMillis();

	private static byte[] lock = new byte[0];

	private static BaiduLocationServiceImpl instance = null;

	private BaiduLocationServiceImpl() {
	}

	public static BaiduLocationServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new BaiduLocationServiceImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.baidu.service.BaiduLocationService#init(com.iwgame
	 * .msgs.common.MyLocationListenner)
	 */
	@Override
	public void init(LocationCallBack callBack) {
		if (!isinit) {
			// 位置服务
			mLocationClient = new LocationClient(SystemContext.getInstance().getContext());
			/**
			 * —————————————————————————————————————————————————————————————————
			 * — 这里的AK和应用签名包名绑定，如果使用在自己的工程中需要替换为自己申请的Key
			 * ————————————————————————
			 * ——————————————————————————————————————————
			 */
			mLocationClient.setAK("E2013dd90eeab2f00f1fbd37e4b7dbdf");
			mLocationListener = new LocationListener(callBack, this);
			mLocationClient.registerLocationListener(mLocationListener);
			setLocationOption();
			mLocationClient.start();
			oldRequestTime = System.currentTimeMillis();
			mLocationClient.requestLocation();
			// mGeofenceClient = new
			// GeofenceClient(SystemContext.getInstance().getContext());
			// 位置提醒相关代码
			// mNotifyer = new NotifyLister();
			// mNotifyer.SetNotifyLocation(40.047883,116.312564,3000,"gps");//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
			// mLocationClient.registerNotify(mNotifyer);
			isinit = true;
		} else {
			callBack.onBack(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.service.BaiduLocationService#close()
	 */
	@Override
	public void stop() {
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.service.BaiduLocationService#start()
	 */
	@Override
	public void start() {
		if (mLocationClient != null && !mLocationClient.isStarted())
			mLocationClient.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.baidu.service.BaiduLocationService#requestLocation
	 * ()
	 */
	@Override
	public void requestLocation(LocationCallBack callBack) {
		long l = System.currentTimeMillis() - oldRequestTime;
		if (l > delayTime) {
			if (mLocationListener != null && mLocationClient != null) {
				mLocationClient.start();
				mLocationListener.setmRequestLocationOnCallBack(callBack);
				oldRequestTime = System.currentTimeMillis();
				mLocationClient.requestLocation();
			} else {
				callBack.onBack(null);
			}
		} else {
			callBack.onBack(null);
		}
	}

	/**
	 * 设置相关参数
	 */
	private void setLocationOption() {
		if (mLocationClient != null) {
			LocationClientOption option = new LocationClientOption();
			option.setServiceName("com.baidu.location.service_v2.9");
			option.setAddrType("all");
			option.setPoiExtraInfo(true);
			option.setProdName("msgs");
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd0911");// 设置坐标类型
			option.setPoiExtraInfo(true);
			// option.setScanSpan(time);
			option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先
			option.setPoiNumber(3);
			option.disableCache(true);
			mLocationClient.setLocOption(option);
		}
	}

}
