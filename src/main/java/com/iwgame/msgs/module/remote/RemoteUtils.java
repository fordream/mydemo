/**      
 * RemoteUtils.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite.GeneratedExtension;
import com.google.protobuf.MessageLite;
import com.iwgame.msgs.common.CallBackData;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ServiceMsgResult;
import com.iwgame.msgs.utils.SessionUtil;
import com.iwgame.msgs.utils.XActionCmds;
import com.iwgame.sdk.xaction.XActionCallback;
import com.iwgame.sdk.xaction.XActionCommandClient;
import com.iwgame.sdk.xaction.XActionSessionStatusListener.XActionSessionStatus;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.xaction.proto.XAction.XActionCommand.XActionCommandExtension;
import com.iwgame.xaction.proto.XAction.XActionResult;
import com.iwgame.xnode.utils.XTypeLiteUtil;

/**
 * @ClassName: RemoteUtils
 * @Description: remote 工具类，用于兼容以前的http 的请求，采用ServiceCallBack的方法
 * @author 王卫
 * @date 2013-10-24 上午11:46:18
 * @Version 1.0
 * 
 */
public class RemoteUtils {


	protected static final String TAG = "RemoteUtils";
	
	private static boolean flag = true;

	// 发送请求回调队列
	public static Queue<CallBackData> callBackQueue = new ConcurrentLinkedQueue<CallBackData>();

	/**
	 * 启动处理callback线程
	 */
	public static void startUpConsumeThread(final Queue<CallBackData> queue) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (flag) {
					if (!queue.isEmpty()) {
						CallBackData callBackData = queue.poll();
						callBackData.execute();
						try {
							Thread.sleep(2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	public static void httpRequest(final ServiceCallBack<XActionResult> callback, Context context, Map<String, Object> params, int cmd) {
		if (!NetworkUtil.isConnect(SystemContext.getInstance().getContext()) && cmd != XActionCmds.CMD_ACCOUNT_LOGOUT) {
			callback.onFailure(ErrorCode.EC_NET_WORK_NOT_AVAILABLE, null);
			return;
		}

		final long t = System.currentTimeMillis();
		final XActionCallback actionCallback = new XActionCallback() {

			@Override
			public void onResult(XActionResult result) {
				LogUtil.debug("---------http map result actionCallback:" + this);
				LogUtil.debug("---------http result start-----------------");
				LogUtil.debug("consume time " + (System.currentTimeMillis() - t));
				LogUtil.debug(result.toString());
				LogUtil.debug("---------http result end-----------------");

				if (result.hasHeader() && result.getHeader() != null) {
					Map<String, Object> params = XTypeLiteUtil.XPropertys2Map(result.getHeader().getValuesList());
					String ts = String.valueOf(params.get("ts"));
					if (ts != null && !"null".equals(ts) && !"".equals(ts)) {
						SystemContext.getInstance().setCurrentTimeInMillis(Long.parseLong(ts));
						if (result.getCmd() == XActionCmds.CMD_ACCOUNT_LOGIN) {
							SystemContext.getInstance().setLoginTime(Long.parseLong(ts));
						}
					}
				}

				if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
					callback.onSuccess(result);
				} else {
					String msg = result.getContent();
					if (result.hasExtension(Msgs.serviceMsgResult)) {
						ServiceMsgResult tmp = result.getExtension(Msgs.serviceMsgResult);
						if (tmp != null && tmp.hasErrorMsg() && tmp.getErrorMsg() != null && !tmp.getErrorMsg().equals("null")
								&& !tmp.getErrorMsg().equals("")) {
							callback.onFailure(result.getRc(), tmp.getErrorMsg());
						} else {
							callback.onFailure(result.getRc(), msg);
						}
					} else {
						callback.onFailure(result.getRc(), msg);
					}
				}
			}

			@Override
			public void onError(int errorCode) {
				LogUtil.debug("---------http map error actionCallback:" + this);
				LogUtil.debug("---------http result start-----------------");
				LogUtil.debug("consume time " + (System.currentTimeMillis() - t));
				LogUtil.debug("http result is error ：" + errorCode);
				LogUtil.debug("---------http result end-----------------");
				if (errorCode != com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE)
					callback.onFailure(errorCode, null);
			}
		};

		XActionCommandClient client = SessionUtil.getShortClient();
		LogUtil.debug("---------http request start-----------------");
		LogUtil.debug("client is ：" + client);
		LogUtil.debug("cmd is ：" + cmd);
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				LogUtil.debug(entry.getKey().toString() + " = " + entry.getValue().toString());
			}
		}
		LogUtil.debug("---------http request end-----------------");
		if (client != null) {
			try {

				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LogUtil.debug("---------http map request actionCallback:" + actionCallback);
			if (cmd == XActionCmds.CMD_ACCOUNT_LOGIN) {
				// 登录的特殊处理
				String account = String.valueOf(params.get("account"));
				String password = String.valueOf(params.get("password"));
				String deviceId = String.valueOf(params.get("deviceId"));
				String appId = String.valueOf(params.get("apptype"));
				String openid = String.valueOf(params.get("openid"));
				int rcode;
				if(openid != null && !openid.equals("") && !openid.equals("null")){
					String apitype = String.valueOf(params.get("apitype"));
					int authtype = Integer.valueOf(String.valueOf(params.get("authtype")));
					rcode = SessionUtil.login(account, deviceId, appId, apitype, openid, authtype, actionCallback);
				}
				else{
					rcode = SessionUtil.login(account, password, deviceId, appId, actionCallback);
				}
				if(SessionUtil.authenticating == rcode || SessionUtil.authenticated == rcode || SessionUtil.opening == rcode || SessionUtil.opened == rcode){
					actionCallback.onError(ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR);
				}
			} else if (cmd == XActionCmds.CMD_ACCOUNT_LOGOUT) {
				// 退出的特殊处理
				int rcode = SessionUtil.logout(actionCallback);
				if (rcode != SessionUtil.opened && rcode != SessionUtil.authenticated && rcode != SessionUtil.unopen){
					actionCallback.onError(ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR);
				}
			} else {
				client.sendCommand(SessionUtil.createHttpCommandParameters(cmd, params), actionCallback);
			}
		} else {
			callback.onFailure(ErrorCode.EC_HTTP_CLIENT_ISNULL, null);
		}

	}

	public static void tcpRequest(final ServiceCallBack<XActionResult> callback, Context context, Map<String, Object> params, int cmd) {
		if (!NetworkUtil.isConnect(SystemContext.getInstance().getContext()) || !XActionSessionStatus.SESSION_OPENED.equals(SessionUtil.status)) {
			callback.onFailure(ErrorCode.EC_NET_WORK_NOT_AVAILABLE, null);
			return;
		}

		final long t = System.currentTimeMillis();
		XActionCallback actionCallback = new XActionCallback() {

			@Override
			public void onResult(XActionResult result) {
				LogUtil.debug("---------tcp map result actionCallback:" + this);
				LogUtil.debug("---------tcp map result start-----------------");
				LogUtil.debug("consume time " + (System.currentTimeMillis() - t));
				LogUtil.debug(result.toString());
				LogUtil.debug("---------tcp map result end-----------------");
				callBackQueue.offer(new CallBackData(callback, result));
			}

			@Override
			public void onError(int errorCode) {
				LogUtil.debug("---------tcp map error actionCallback:" + this);
				LogUtil.debug("---------tcp map error start-----------------");
				LogUtil.debug("consume time " + (System.currentTimeMillis() - t));
				LogUtil.debug("tcp result is error ：" + errorCode);
				LogUtil.debug("---------tcp map error end-----------------");
				callBackQueue.offer(new CallBackData(callback, errorCode));
			}
		};

		XActionCommandClient client = SessionUtil.getLongClient();
		LogUtil.debug("---------tcp map request start-----------------");
		LogUtil.debug("client is ：" + client);
		LogUtil.debug("cmd is ：" + cmd);
		if (params != null) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				LogUtil.debug(entry.getKey().toString() + " = " + (entry.getValue() == null ? "" : entry.getValue().toString()));
			}
		}
		LogUtil.debug("---------tcp map request end-----------------");
		if (client != null) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LogUtil.debug("---------tcp map request actionCallback:" + actionCallback);
			client.sendCommand(SessionUtil.createTcpCommandParameters(cmd, params), actionCallback);
		} else {

			callback.onFailure(ErrorCode.EC_TCP_CLIENT_ISNULL, null);
		}

	}

	public static <T1 extends MessageLite> void tcpRequest(final ServiceCallBack<XActionResult> callback, Context context,
			Map<String, Object> params, GeneratedExtension<XActionCommandExtension, T1> extension, T1 value, int cmd) {
		if (!NetworkUtil.isConnect(SystemContext.getInstance().getContext()) || !XActionSessionStatus.SESSION_OPENED.equals(SessionUtil.status)) {
			callback.onFailure(ErrorCode.EC_NET_WORK_NOT_AVAILABLE, null);
			return;
		}

		final long t = System.currentTimeMillis();
		XActionCallback actionCallback = new XActionCallback() {

			@Override
			public void onError(int arg0) {
				LogUtil.debug("---------tcp map error actionCallback:" + this);
				LogUtil.debug("---------tcp ext error start-----------------");
				LogUtil.debug("consume time " + (System.currentTimeMillis() - t));
				LogUtil.debug("tcp result is error ：" + arg0);
				LogUtil.debug("---------tcp ext error end-----------------");
				callBackQueue.offer(new CallBackData(callback, arg0));
			}

			@Override
			public void onResult(XActionResult result) {
				LogUtil.debug("---------tcp map result actionCallback:" + this);
				LogUtil.debug("---------tcp ext result start-----------------");
				LogUtil.debug("consume time " + (System.currentTimeMillis() - t));
				LogUtil.debug(result.toString());
				LogUtil.debug("---------tcp ext result end-----------------");
				callBackQueue.offer(new CallBackData(callback, result));
			}

		};

		XActionCommandClient client = SessionUtil.getLongClient();
		LogUtil.debug("---------tcp ext request start-----------------");
		LogUtil.debug("client is ：" + client);
		LogUtil.debug("cmd is ：" + cmd);
		if (value != null)
			LogUtil.debug("value: " + value.toString());

		LogUtil.debug("---------tcp ext request end-----------------");
		if (client != null) {
			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			LogUtil.debug("---------tcp map request actionCallback:" + actionCallback);
			client.sendCommand(SessionUtil.createCommandParameters(cmd, params, extension, value), actionCallback);
		} else {
			callback.onFailure(ErrorCode.EC_TCP_CLIENT_ISNULL, null);
		}
	}

}
