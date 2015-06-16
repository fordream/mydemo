/**      
 * MsgsAsync.java Create on 2013-7-31     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;


/**
 * @ClassName: AsyncTaskCallBack
 * @Description: 异步操作回调接口类
 * @author 王卫
 * @date 2013-7-31 下午04:15:12
 * @Version 1.0
 * 
 */
public interface AsyncCallBack<T> {

	/**
	 * 执行异步操作
	 * 
	 * @return
	 */
	public T execute();

	/**
	 * 处理异步结果
	 * 
	 * @param result
	 */
	public void onHandle(T result);

}
