/**      
* PayProxy.java Create on 2015-5-20     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.pay.adapter;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs.RechargeOrderResult;
import com.iwgame.msgs.proto.Msgs.UserYoubiDetail;
import com.iwgame.msgs.proto.Msgs.YoubiDetailInfo;
import com.iwgame.msgs.proto.Msgs.YoubiDetailList;
import com.iwgame.msgs.proto.Msgs.YoubiSchemeResult;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.xaction.proto.XAction.XActionResult;

/** 
 * @ClassName: PayProxy 
 * @Description: 支付数据代理接口 
 * @author 王卫
 * @date 2015-5-20 下午4:04:07 
 * @Version 1.0
 * 
 */
public interface PayProxy {
	
	/**
	 * 获取我的U币及相关数据
	 * 
	 * @param callback
	 * @param context
	 */
	public void getUb(ProxyCallBack<YoubiSchemeResult> callback, Context context, int type);

	/**
	 * 支付调用服务端接口
	 * 
	 * @param callback
	 * @param context
	 * @param faceValue
	 * @param channel
	 * @param phoneType
	 */
	public void pay(ProxyCallBack<RechargeOrderResult> callback, Context context, int faceValue, int channel, int phoneType);

	/**
	 * 查询游币明细
	 * @param callback
	 * @param context
	 * @param userAccount
	 */
	public void searchOrderDeatils(ProxyCallBack<PagerVo<UserYoubiDetail>> callback, Context context, int bussinessType, long offset, int limit);

	/**
	 * 游币明细
	 * @param callback
	 * @param context
	 * @param bussinessType
	 * @param orderNo
	 */
	public void searchOrderDeatil(ProxyCallBack<YoubiDetailInfo> callback, Context context, int bussinessType, long orderNo);
	
	/**
	 * 取消支付订单
	 * @param callback
	 * @param context
	 * @param order
	 */
	public void cannelOrder(ProxyCallBack<Integer> callback, Context context, long order, int type);
	

}
