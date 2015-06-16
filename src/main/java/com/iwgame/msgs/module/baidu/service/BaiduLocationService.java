/**      
 * BaiduLocationService.java Create on 2013-10-17     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.baidu.service;

import com.iwgame.msgs.common.LocationCallBack;

/**
 * @ClassName: BaiduLocationService
 * @Description: 百度位置服务接口
 * @author 王卫
 * @date 2013-10-17 下午02:56:59
 * @Version 1.0
 * 
 */
public interface BaiduLocationService {

	/**
	 * 打开位置服务
	 */
	public void start();

	/**
	 * 关闭位置服务
	 */
	public void stop();

	/**
	 * 初始化服务
	 * 
	 * @param listener
	 */
	public void init(LocationCallBack listener);

	/**
	 * 请求位置
	 * 
	 * @param callBack
	 */
	public void requestLocation(LocationCallBack callBack);

}
