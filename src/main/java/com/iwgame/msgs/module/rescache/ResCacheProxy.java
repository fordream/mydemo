/**      
 * ResCacheProxy.java Create on 2013-9-14     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.rescache;

import android.content.Context;
import android.net.Uri;

import com.iwgame.msgs.common.ProxyCallBack;

/**
 * @ClassName: ResCacheProxy
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-14 下午12:03:48
 * @Version 1.0
 * 
 */
public interface ResCacheProxy {

	public static final int TYPE_ORRGINAL = 1;
	public static final int TYPE_MIDIUM = 2;
	public static final int TYPE_SMALL = 3;
	public static final int TYPE_BIG = 4;

	/**
	 * 获取资源
	 * 
	 * @param callback
	 * @param context
	 * @param resid
	 * @param type
	 */
	public void getRes(ProxyCallBack<Uri> callback, Context context, String resid, int type);

	/**
	 * 获取资源
	 * 
	 * @param callback
	 * @param context
	 * @param resid
	 * @param type
	 * @param isnet
	 */
	public void getRes(ProxyCallBack<Uri> callback, Context context, String resid, int type, boolean isnet);

}
