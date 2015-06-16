/**      
 * WXPayManager.java Create on 2015-5-29     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.pay.ui.util;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.MD5;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * @ClassName: WXPayManager
 * @Description: 微信支付管理
 * @author 王卫
 * @date 2015-5-29 下午5:20:31
 * @Version 1.0
 * 
 */
public class WXPayManager {

	private static final String TAG = "WXPayManager";

	private IWXAPI msgApi;

	private Activity mActivity;

	/**
	 * 
	 */
	public WXPayManager(Activity activity) {
		mActivity = activity;
		msgApi = WXAPIFactory.createWXAPI(mActivity, null);
		msgApi.registerApp(SystemContext.WXPAY_APPID);
	}

	/**
	 * 发送支付请求
	 * 
	 * @param fee
	 */
	public void sendPayReq(String partnerId, String prepayid, String nonce, String timeStamp, String sign) {
		PayReq req = new PayReq();

		req.appId = SystemContext.WXPAY_APPID;
		req.partnerId = partnerId;
		req.prepayId = prepayid;
		req.packageValue = "Sign=WXPay";
		req.nonceStr = nonce;
		req.timeStamp = timeStamp;
		req.sign = sign;
		LogUtil.d(TAG, "--->>sendPayReq::partnerId=" + partnerId + ", prepayid=" + prepayid + ", nonce=" + nonce + ", timeStamp=" + timeStamp
				+ ", sign=" + sign);
		msgApi.registerApp(SystemContext.WXPAY_APPID);
		msgApi.sendReq(req);
	}

	/**
	 * 
	 * @param params
	 * @return
	 */
	private String genAppSign(PayReq req) {
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < signParams.size(); i++) {
			sb.append(signParams.get(i).getName());
			sb.append('=');
			sb.append(signParams.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append("key");

		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		LogUtil.d(TAG, "--->>genAppSign::appSign=" + appSign);
		return appSign;
	}

}
