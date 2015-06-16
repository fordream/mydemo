/**      
 * SyncCallBack.java Create on 2013-12-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync;

/**
 * @ClassName: SyncCallBack
 * @Description: 同步回调接口
 * @author 王卫
 * @date 2013-12-19 上午11:47:54
 * @Version 1.0
 * 
 */
public interface SyncCallBack {

	/**
	 * 同步成功回调
	 * 
	 * @param result
	 */
	public void onSuccess(Object result);

	/**
	 * 失败
	 * 
	 * @param result
	 */
	public void onFailure(Integer result);

}
