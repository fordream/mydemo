package com.iwgame.msgs.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.GeneratedMessageLite.GeneratedExtension;
import com.google.protobuf.MessageLite;
import com.iwgame.msgs.common.EnvManager;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.remote.RemoteUtils;
import com.iwgame.sdk.xaction.EnvStatusType;
import com.iwgame.sdk.xaction.SessionParamKeyType;
import com.iwgame.sdk.xaction.XActionAuthStore;
import com.iwgame.sdk.xaction.XActionCallback;
import com.iwgame.sdk.xaction.XActionClientEnv;
import com.iwgame.sdk.xaction.XActionCommandClient;
import com.iwgame.sdk.xaction.XActionCommandClientProfile;
import com.iwgame.sdk.xaction.XActionCommandClientProfile.ProtocolType;
import com.iwgame.sdk.xaction.XActionCommandParameters;
import com.iwgame.sdk.xaction.XActionNotificationListener;
import com.iwgame.sdk.xaction.XActionSession;
import com.iwgame.sdk.xaction.XActionSessionConnectivityListener;
import com.iwgame.sdk.xaction.XActionSessionFactory;
import com.iwgame.sdk.xaction.XActionSessionProfile;
import com.iwgame.sdk.xaction.XActionSessionStatusListener;
import com.iwgame.sdk.xaction.XActionSessionStatusListener.XActionSessionStatus;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.xaction.proto.XAction.XActionCommand.XActionCommandExtension;
import com.iwgame.xnode.proto.XType.XProperty;
import com.iwgame.xnode.utils.XTypeLiteUtil;

public class SessionUtil {
	protected static final String TAG = "SessionUtil";

	public static XActionSessionStatus status;

	public static int reqFail = XActionSessionStatus.REQUEST_FAIL.value();
	public static int reqSuccess = XActionSessionStatus.REQUEST_SUCCESS.value();
	public static int authenticating = XActionSessionStatus.SESSION_AUTHENTICATING.value() | reqFail;
	public static int authenticated = XActionSessionStatus.SESSION_AUTHENTICATED.value() | reqFail;
	public static int opening = XActionSessionStatus.SESSION_OPENING.value() | reqFail;
	public static int opened = XActionSessionStatus.SESSION_OPENED.value() | reqFail;
	public static int unopen = XActionSessionStatus.SESSION_UNOPEN.value() | reqFail;

	static {
		System.loadLibrary("uninstall");
	}

	private static XActionSessionFactory sessionFactory = XActionSessionFactory.defaultFactory();
	private static XActionSession session;

	private static final String SESSION_ID = "uban.session";
	private static final String AUTH_CLIENT_ID = "uban.client.auth";
	private static final String TCP_CLIENT_ID = "uban.client.tcp";
	private static final String HTTP_CLIENT_ID = "uban.client.http";

	public static void initSession(final EnvStatusType netType, final XActionNotificationListener notificationListener,
			final XActionSessionStatusListener statusListener) {
		if (SystemContext.getInstance().getConfigLoadedStatus() == SystemContext.ON_READY) {
			// 请求服务端配置
			ProxyFactory.getInstance().getSettingProxy().getGlobalConfig(new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					SystemContext.getInstance().setConfigLoadedStatus(SystemContext.READY);
					if (SessionUtil.getSession() == null)
						createSession(netType, notificationListener, statusListener);
					else
						onEnvStatusChanged(netType);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					LogUtil.e(TAG, "请求全局配置数据失败");
					if (SessionUtil.getSession() != null)
						SessionUtil.onEnvStatusChanged(netType);
				}
			}, SystemContext.GLOBAL_CONFIG_URL);
		} else {
			if (SessionUtil.getSession() == null) {
				createSession(netType, notificationListener, statusListener);
			} else {
				SessionUtil.onEnvStatusChanged(netType);
			}
		}
	}

	/**
	 * 创建session
	 * 
	 * @param netType
	 * @param notificationListener
	 * @param statusListener
	 */
	private static void createSession(EnvStatusType netType, XActionNotificationListener notificationListener,
			XActionSessionStatusListener statusListener) {
		RemoteUtils.startUpConsumeThread(RemoteUtils.callBackQueue);
		XActionAuthStore xActionTokenStore = new XActionAuthStore() {
			@Override
			public String getToken() {
				// TODO Auto-generated method stub
				String token = SystemContext.getInstance().getToken();
				if (token == null)
					token = "";
				LogUtil.d(TAG, "AS: messageService token is " + token);
				return token;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.iwgame.sdk.xaction.XActionAuthStore#setToken(java.lang
			 * .String)
			 */
			@Override
			public void setToken(String token) {
				LogUtil.d(TAG, "setToken: " + token);
				SystemContext.getInstance().setToken(token);
			}

			@Override
			public String getDeviceId() {
				String deviceId = SystemContext.getInstance().getDeviceId();
				return deviceId;
			}

		};

		XActionClientEnv env = new XActionClientEnv() {

			@Override
			public Map<String, String> getEnvProperties() {
				return EnvManager.getInstance().getEnvMap();
			}
		};
		createSession(SystemContext.getInstance().getTcpIP(), SystemContext.getInstance().getTcpPort(), SystemContext.getInstance().getHttpIP(),
				SystemContext.getInstance().getHttpPort(), SystemContext.getInstance().getLoginHttpIP(), SystemContext.getInstance()
						.getLoginHttpPort(), xActionTokenStore, env, notificationListener, statusListener);
		if (session != null) {
			session.open();
		}
		LogUtil.d(TAG, "---------->SystemContext is ready");
	}

	public static XActionCommandClient getLongClient() {
		if (session == null) {
			LogUtil.i(TAG, "get long client session not created");
			return null;
		}
		return session.getCommandClient(TCP_CLIENT_ID);
	}

	public static XActionCommandClient getShortClient() {
		if (session == null) {
			LogUtil.i(TAG, "get short client session not created");
			return null;
		}
		return session.getCommandClient(HTTP_CLIENT_ID);
	}

	public static XActionCommandParameters createHttpCommandParameters(int cmd, Map<String, Object> propMap) {
		return XActionCommandParametersFactory.createParameters(XActionCommandParametersFactory.TYPE_HTTP, cmd, propMap);
	}

	public static XActionCommandParameters createTcpCommandParameters(int cmd, Map<String, Object> propMap) {
		return XActionCommandParametersFactory.createParameters(XActionCommandParametersFactory.TYPE_TCP, cmd, propMap);
	}

	public static <T extends MessageLite> XActionCommandParameters createCommandParameters(int cmd, Map<String, Object> propMap,
			GeneratedExtension<XActionCommandExtension, T> extension, T value) {
		XActionCommandParameters param = new XActionCommandParameters(cmd);
		if (propMap != null) {
			Set<String> propKeySet = propMap.keySet();
			if (propKeySet != null && propKeySet.size() > 0) {
				ArrayList<XProperty> properties = new ArrayList<XProperty>();
				for (String propKey : propKeySet) {
					properties.add(XTypeLiteUtil.buildXProperty(propKey, propMap.get(propKey)));
				}
				param.setXPropertyList(properties.toArray(new XProperty[] {}));
			}
		}
		if (extension != null && value != null) {
			try {
				param.setXActionCommandMessage(extension, value);
			} catch (IOException e) {
				LogUtil.e(TAG, "setXActionCommandMessage failed: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return param;
	}

	public static int login(String username, String password, String deviceId, String appId, final XActionCallback callback) {
		if (session != null) {
			Map<SessionParamKeyType, String> params = new HashMap<SessionParamKeyType, String>();
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_USERNAME, username);
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_APPID, appId);
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_DEVICEID, deviceId);
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_PASSWORD, password);
			return session.login(params, callback);
		} else {
			return ErrorCode.EC_XACTION_NET_ERROR;
		}
	}
	
	public static int login(String username, String deviceId, String appId, String apitype, String openid, int authtype, final XActionCallback callback) {
		if (session != null) {
			Map<SessionParamKeyType, String> params = new HashMap<SessionParamKeyType, String>();
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_USERNAME, username);
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_APPID, appId);
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_DEVICEID, deviceId);
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_APITYPE, apitype);
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_OPENID, openid);
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_PASSWORD, "");
			params.put(SessionParamKeyType.SESSION_PARAM_KEY_TYPE_AUTHTYPE, String.valueOf(authtype));
			return session.login(params, callback);
		} else {
			return ErrorCode.EC_XACTION_NET_ERROR;
		}
	}


	public static int logout(XActionCallback callback) {
		if (session != null) {
			return session.logout(callback);
		} else {
			return ErrorCode.EC_XACTION_NET_ERROR;
		}
	}

	public static void onEnvStatusChanged(EnvStatusType status) {
		if (session != null) {
			session.onEnvStatusChanged(status);
		}
	}

	private static native void initUninstall(String url, int sysversion);

	private static XActionSession createSession(String tcpIP, int tcpPort, String httpIP, int httpPort, String loginHttpIP, int loginHttpPort,
			XActionAuthStore authStore, XActionClientEnv env, XActionNotificationListener notificationListener,
			XActionSessionStatusListener statusListener) {
		XActionSessionProfile sessionProfile = new XActionSessionProfile(SESSION_ID);

		// HTTP Auth client
		String transAuthHttpIp = "http://" + loginHttpIP + ":" + loginHttpPort + "/xaction";
		XActionCommandClientProfile authClientProfile = new XActionCommandClientProfile(AUTH_CLIENT_ID, ProtocolType.PROTOCOL_TYPE_HTTP,
				transAuthHttpIp, loginHttpPort, null);
		sessionProfile.setAuthClientProfile(authClientProfile);

		// HTTP client
		String transHttpIp = "http://" + httpIP + ":" + httpPort + "/xaction";
		XActionCommandClientProfile httpProfile = new XActionCommandClientProfile(HTTP_CLIENT_ID, ProtocolType.PROTOCOL_TYPE_HTTP, transHttpIp,
				httpPort, null);
		sessionProfile.addClientProfile(httpProfile);

		// TCP client
		XActionCommandClientProfile tcpProfile = new XActionCommandClientProfile(TCP_CLIENT_ID, ProtocolType.PROTOCOL_TYPE_TCP, tcpIP, tcpPort, null);
		sessionProfile.addClientProfile(tcpProfile);

		// Auth Store
		sessionProfile.setAuthStore(authStore);

		session = sessionFactory.getOrCreateSession(sessionProfile);

		if (notificationListener != null) {
			session.addNotificationListener(notificationListener);
		}

		if (statusListener != null) {
			session.addSessionStatusListener(statusListener);
		}

		session.setConnectivityListener(new XActionSessionConnectivityListener() {

			@Override
			public ConnectivityType getConnectivityType() {
				return NetworkUtil.isConnect(SystemContext.getInstance().getContext()) ? ConnectivityType.CONN_TYPE_WIFI
						: ConnectivityType.CONN_TYPE_NO_NETWORK;
			}
		});

		return session;
	}

	static class XActionCommandParametersFactory {
		final static int TYPE_HTTP = 1;
		final static int TYPE_TCP = 2;

		private static XActionCommandParameters createParameters(int type, int cmd, Map<String, Object> propMap) {
			XActionCommandParameters param = null;
			switch (type) {
			case TYPE_HTTP:
				param = new XActionCommandParameters(cmd);
				break;

			case TYPE_TCP:
				param = new XActionCommandParameters(cmd);
				break;
			default:
				throw new IllegalArgumentException("invalid type: " + type);
			}

			if (propMap != null) {
				Set<String> propKeySet = propMap.keySet();
				if (propKeySet != null && propKeySet.size() > 0) {
					ArrayList<XProperty> properties = new ArrayList<XProperty>();
					for (String propKey : propKeySet) {
						properties.add(XTypeLiteUtil.buildXProperty(propKey, propMap.get(propKey)));
					}
					param.setXPropertyList(properties.toArray(new XProperty[] {}));
				}
			}

			return param;
		}
	}

	public static XActionSession getSession() {
		return session;
	}

	public static void setSession(XActionSession session) {
		SessionUtil.session = session;
	}

}
