/**      
 * CallBackData.java Create on 2015-1-3     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostbarActionResult;
import com.iwgame.msgs.proto.Msgs.ServiceMsgResult;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.iwgame.xaction.proto.XAction.XActionResult;
import com.iwgame.xnode.utils.XTypeLiteUtil;

/**
 * @ClassName: CallBackData
 * @Description: 回调对象
 * @author 王卫
 * @date 2015-1-3 下午3:43:28
 * @Version 1.0
 * 
 */
public class CallBackData {
	/** 回调对象 **/
	private ServiceCallBack<XActionResult> serviceCallBack;
	/** 回调结果 **/
	private Object result;
	/** 是否执行会话失效的标识 **/
	public static boolean flag = true;

	/**
	 * 
	 */
	public CallBackData(ServiceCallBack<XActionResult> serviceCallBack, Object result) {
		this.serviceCallBack = serviceCallBack;
		this.result = result;
	}

	/**
	 * 执行回调结果
	 */
	public void execute() {
		if (serviceCallBack == null || result == null)
			return;

		if (result instanceof Integer) {
			serviceCallBack.onFailure((Integer) result, null);
		} else if (result instanceof XActionResult) {
			XActionResult ret = (XActionResult) result;
			if (ret.hasHeader() && ret.getHeader() != null) {
				Map<String, Object> params = XTypeLiteUtil.XPropertys2Map(ret.getHeader().getValuesList());
				String ts = String.valueOf(params.get("ts"));
				if (ts != null && !"null".equals(ts) && !"".equals(ts)) {
					SystemContext.getInstance().setCurrentTimeInMillis(Long.parseLong(ts));
				}
			}
			if (ret.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
				// 响应成功
				serviceCallBack.onSuccess(ret);
			} else if (ret.getRc() == XAccountInfo.ErrorCode.EC_SESSION_EXPIRED_VALUE) {
				if (flag) {
					flag = false;
					// 会话过期
					ProxyFactory.getInstance().getAccountProxy().logout(new ProxyCallBack<Integer>() {

						@Override
						public void onSuccess(Integer result) {
							jumpLoginView();
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							jumpLoginView();
						}
					}, SystemContext.getInstance().getContext(), SystemContext.getInstance().getToken());
				} else {
					// 响应成功
					serviceCallBack.onSuccess(ret);
				}
			} else {
				// 其他
				if (ret.hasExtension(Msgs.serviceMsgResult)) {
					ServiceMsgResult tmp = ret.getExtension(Msgs.serviceMsgResult);
					if (tmp != null && tmp.hasErrorMsg() && tmp.getErrorMsg() != null && !tmp.getErrorMsg().equals("null")
							&& !tmp.getErrorMsg().equals("")) {
						serviceCallBack.onFailure(ret.getRc(), tmp.getErrorMsg());
					} else {
						serviceCallBack.onFailure(ret.getRc(), null);
					}
				} else if (ret.hasExtension(Msgs.postbarActionResult)) {
					PostbarActionResult tmp = ret.getExtension(Msgs.postbarActionResult);
					if (tmp != null && tmp.hasMsg() && tmp.getMsg() != null && !tmp.getMsg().equals("null")
							&& !tmp.getMsg().equals("")) {
						serviceCallBack.onFailure(ret.getRc(), tmp.getMsg());
					} else {
						serviceCallBack.onFailure(ret.getRc(), null);
					}
				} else {
					serviceCallBack.onFailure(ret.getRc(), null);
				}
			}
		} else {
			return;
		}
	}

	public ServiceCallBack<XActionResult> getServiceCallBack() {
		return serviceCallBack;
	}

	public void setServiceCallBack(ServiceCallBack<XActionResult> serviceCallBack) {
		this.serviceCallBack = serviceCallBack;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * 
	 */
	private void jumpLoginView() {
		// 会话过期
		Intent intent = new Intent(SystemContext.getInstance().getContext(), MainFragmentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -5);
		bundle.putInt("Type", 1);
		intent.putExtras(bundle);
		SystemContext.getInstance().getContext().startActivity(intent);
	}
}
