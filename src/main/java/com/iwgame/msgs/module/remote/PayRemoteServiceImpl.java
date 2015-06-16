/**      
 * PayRemoteServiceImpl.java Create on 2015-5-20     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: PayRemoteServiceImpl
 * @Description: 支付相关服务接口
 * @author 王卫
 * @date 2015-5-20 下午4:06:55
 * @Version 1.0
 * 
 */
public class PayRemoteServiceImpl implements PayRemoteService {

	protected static final String TAG = "PayRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static PayRemoteServiceImpl instance = null;

	private PayRemoteServiceImpl() {
	}

	public static PayRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new PayRemoteServiceImpl();
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
	 * com.iwgame.msgs.module.remote.PayRemoteService#getUb(com.iwgame.msgs.
	 * common.ServiceCallBack, android.content.Context)
	 */
	@Override
	public void getUb(ServiceCallBack<XActionResult> callback, Context context, int type) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("phoneType", 1);
		params.put("type", type);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_YOUBI_CHOOSE_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PayRemoteService#pay(com.iwgame.msgs.common
	 * .ServiceCallBack, android.content.Context, int, int, int)
	 */
	@Override
	public void pay(ServiceCallBack<XActionResult> callback, Context context, int faceValue, int channel, int phoneType) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("faceValue", faceValue);
		params.put("channel", channel);
		params.put("phoneType", phoneType);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_PAY_REAHARGE_ORDER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PayRemoteService#searchOrderDeatil(com.
	 * iwgame.msgs.common.ServiceCallBack, android.content.Context, int, long)
	 */
	@Override
	public void searchOrderDeatil(ServiceCallBack<XActionResult> callback, Context context, int bussinessType, long orderNo) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessType", bussinessType);
		params.put("orderNo", orderNo);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_YOUBI_DETAIL_INFO);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PayRemoteService#searchOrderDeatils(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context, int, long,
	 * int)
	 */
	@Override
	public void searchOrderDeatils(ServiceCallBack<XActionResult> callback, Context context, int bussinessType, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessType", bussinessType);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_YOUBI_DETAIL_LIST);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.PayRemoteService#cannelOrder(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void cannelOrder(ServiceCallBack<XActionResult> callback, Context context, long order, int type) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderNo", order);
		params.put("type", type);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_RECHARGE_ORDER_CANCEL);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.PayRemoteService#searchAppealInfo(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void searchAppealInfo(ServiceCallBack<XActionResult> callback,
			Context context, long oid) {
		// TODO Auto-generated method stub
		if(callback==null) 
			return;
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("orderid", oid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_PLAY_ORDER_APPEAL);
	}

}
