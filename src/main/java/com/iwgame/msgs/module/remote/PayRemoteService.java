/**      
 * PayRemoteService.java Create on 2015-5-20     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: PayRemoteService
 * @Description: 支付相关服务接口
 * @author 王卫
 * @date 2015-5-20 下午4:06:12
 * @Version 1.0
 * 
 */
public interface PayRemoteService {

	/**
	 * 获取我的U币及相关数据
	 * 
	 * @param callback
	 * @param context
	 */
	public void getUb(ServiceCallBack<XActionResult> callback, Context context, int type);

	/**
	 * 支付调用服务端接口
	 * 
	 * @param callback
	 * @param context
	 * @param faceValue
	 * @param channel
	 * @param phoneType
	 */
	public void pay(ServiceCallBack<XActionResult> callback, Context context, int faceValue, int channel, int phoneType);

	/**
	 * 查询游币明细
	 * @param callback
	 * @param context
	 * @param userAccount
	 */
	public void searchOrderDeatils(ServiceCallBack<XActionResult> callback, Context context, int bussinessType, long offset, int limit);

	/**
	 * 游币明细
	 * @param callback
	 * @param context
	 * @param bussinessType
	 * @param orderNo
	 */
	public void searchOrderDeatil(ServiceCallBack<XActionResult> callback, Context context, int bussinessType, long orderNo);
	/**
	 * 获取申诉数据
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void searchAppealInfo(ServiceCallBack<XActionResult> callback,Context context,long oid);

	/**
	 * 取消订单
	 * @param callback
	 * @param context
	 * @param order
	 */
	public void cannelOrder(ServiceCallBack<XActionResult> callback, Context context, long order, int type);

}
