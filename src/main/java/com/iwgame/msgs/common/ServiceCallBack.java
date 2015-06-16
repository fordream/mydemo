/**      
 * ActivityCallBack.java Create on 2013-9-6     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

/**
 * @ClassName: ProxyCallBack
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-6 上午11:54:41
 * @Version 1.0
 * 
 */
public interface ServiceCallBack<T> {

	/**
	 * 成功
	 * 
	 * @return
	 */
	public void onSuccess(T result);

	/**
	 * 失败
	 * 
	 * @param result
	 */
	public void onFailure(Integer result,String resultMsg);

}
