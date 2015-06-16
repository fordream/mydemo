/**      
 * AccountRemoteServiceImpl.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.SessionUtil;
import com.iwgame.msgs.utils.XActionCmds;
import com.iwgame.sdk.xaction.XActionSessionStatusListener.XActionSessionStatus;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: AccountRemoteServiceImpl
 * @Description: 账号服务接口实现类
 * @author 王卫
 * @date 2013-9-24 下午05:22:17
 * @Version 1.0
 * 
 */
public class AccountRemoteServiceImpl implements AccountRemoteService {

	protected static final String TAG = "AccountRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static AccountRemoteServiceImpl instance = null;

	private AccountRemoteServiceImpl() {
	}

	public static AccountRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new AccountRemoteServiceImpl();
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
	 * com.iwgame.msgs.module.account.service.AccountRemoteService#getCaptcha
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, int)
	 */
	@Override
	public void getCaptcha(final ServiceCallBack<XActionResult> callback, Context context, String mobileNum, int mode) {
		if (callback == null)
			return;

		if (mobileNum != null) {

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mobileNumber", mobileNum);
			params.put("mode", mode);
			RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_ACCOUNT_POST_CAPTCHA);
		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.account.service.AccountRemoteService#validateCaptcha
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void validateCaptcha(final ServiceCallBack<XActionResult> callback, Context context, String mobileNumber, String captcha) {
		if (callback == null)
			return;

		if (mobileNumber != null && captcha != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("mobileNumber", mobileNumber);
			params.put("captcha", captcha);
			RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_ACCOUNT_VERIFY_CAPTCHA);
		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.account.service.AccountRemoteService#resetPassword
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void resetPassword(final ServiceCallBack<XActionResult> callback, Context context, String account, String password, String captcha) {
		if (callback == null)
			return;

		if (account != null && password != null && captcha != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("account", account);
			params.put("password", password);
			params.put("captcha", captcha);
			RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_ACCOUNT_RESET_PASSWD);
		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.account.service.AccountRemoteService#login(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void login(final ServiceCallBack<XActionResult> callback, Context context, String account, String password, String deviceId) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("password", password);
		params.put("deviceId", deviceId);
		params.put("apptype", SystemContext.APPTYPE);
		RemoteUtils.httpRequest(new ServiceCallBack<XActionResult>() {

			@Override
			public void onSuccess(final XActionResult result) {
				// 为了适配通讯层，登陆成功不立即返回，等状态为opened时再返回给上层
				if (XActionSessionStatus.SESSION_OPENED.equals(SessionUtil.status)) {
					callback.onSuccess(result);
				} else if (XActionSessionStatus.SESSION_UNAUTHENTICATED.equals(SessionUtil.status)) {
					callback.onFailure(ErrorCode.EC_XACTION_ERROR, null);
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							boolean flag = true;
							while (flag) {
								try {
									LogUtil.d(TAG, "--监听状态");
									if (XActionSessionStatus.SESSION_OPENED.equals(SessionUtil.status)) {
										flag = false;
										LogUtil.d(TAG, "--监听到状态成功");
										callback.onSuccess(result);
									} else if (XActionSessionStatus.SESSION_UNAUTHENTICATED.equals(SessionUtil.status)) {
										flag = false;
										LogUtil.d(TAG, "--监听到状态认证失败");
										callback.onFailure(ErrorCode.EC_XACTION_ERROR, null);
									}
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}).start();
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				callback.onFailure(result, resultMsg);
			}
		}, context, params, XActionCmds.CMD_ACCOUNT_LOGIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.account.service.AccountRemoteService#logout()
	 */
	@Override
	public void logout(final ServiceCallBack<XActionResult> callback, Context context, String token) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("apptype", SystemContext.APPTYPE);
		RemoteUtils.httpRequest(callback, context, params, XActionCmds.CMD_ACCOUNT_LOGOUT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.AccountRemoteService#modifyPassword(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void modifyPassword(final ServiceCallBack<XActionResult> callback, Context context, String opwd, String npwd) {
		if (callback == null)
			return;

		if (opwd != null && npwd != null) {
			// 构建消息
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("oldpwd", opwd);
			params.put("newpwd", npwd);

			RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_ACCOUNT_MODIFY_PASSWD);
		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.AccountRemoteService#validateAccount(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context)
	 */
	@Override
	public void validateAccount(ServiceCallBack<XActionResult> callback, Context context) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_ACCOUNT_VERIFY_ACCOUNT);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.AccountRemoteService#login(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void login(final ServiceCallBack<XActionResult> callback, Context context,
			String account, String deviceId, int authtype,
			String apitype, String openId) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("account", account);
		params.put("deviceId", deviceId);
		params.put("authtype", authtype);
		params.put("apitype", apitype);
		params.put("openid", openId);
		params.put("apptype", SystemContext.APPTYPE);
		RemoteUtils.httpRequest(new ServiceCallBack<XActionResult>() {
			
			@Override
			public void onSuccess(final XActionResult result) {
				// 为了适配通讯层，登陆成功不立即返回，等状态为opened时再返回给上层
				if (XActionSessionStatus.SESSION_OPENED.equals(SessionUtil.status)) {
					callback.onSuccess(result);
				} else if (XActionSessionStatus.SESSION_UNAUTHENTICATED.equals(SessionUtil.status)) {
					callback.onFailure(ErrorCode.EC_XACTION_ERROR, null);
				} else {
					new Thread(new Runnable() {

						@Override
						public void run() {
							boolean flag = true;
							while (flag) {
								try {
									LogUtil.d(TAG, "--监听状态");
									if (XActionSessionStatus.SESSION_OPENED.equals(SessionUtil.status)) {
										flag = false;
										LogUtil.d(TAG, "--监听到状态成功");
										callback.onSuccess(result);
									} else if (XActionSessionStatus.SESSION_UNAUTHENTICATED.equals(SessionUtil.status)) {
										flag = false;
										LogUtil.d(TAG, "--监听到状态认证失败");
										callback.onFailure(ErrorCode.EC_XACTION_ERROR, null);
									}
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}).start();
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				callback.onFailure(result, resultMsg);
			}

		}, context, params, XActionCmds.CMD_ACCOUNT_LOGIN);
		
	}
}
