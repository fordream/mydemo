/**      
 * SyncCallBack.java Create on 2013-12-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.cache;

/**
 * @ClassName: CacheCallBack
 * @Description: 缓存回调接口
 * @author 王卫
 * @date 2013-12-19 上午11:47:54
 * @Version 1.0
 * 
 */
public interface CacheCallBack {

	/**
	 * 数据变化回调到界面
	 * 
	 * @param result
	 */
	public void onBack(Object result);

}
