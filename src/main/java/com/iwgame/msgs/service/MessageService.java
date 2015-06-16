/**      
 * MessageService.java Create on 2013-10-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.service;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openudid.OpenUDID_manager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.logic.UserProxyImpl;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.SyncMessageNotification;
import com.iwgame.msgs.proto.Msgs.UploadContactsRequest;
import com.iwgame.msgs.proto.Msgs.UploadContactsRequest.ContactsEntry;
import com.iwgame.msgs.utils.ContactUtil;
import com.iwgame.msgs.utils.GameUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.SessionUtil;
import com.iwgame.msgs.utils.UploadLogUtil;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.sdk.xaction.EnvStatusType;
import com.iwgame.sdk.xaction.XActionNotificationListener;
import com.iwgame.sdk.xaction.XActionSession;
import com.iwgame.sdk.xaction.XActionSessionStatusListener;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.xaction.proto.XAction.XActionNotification;

/**
 * @ClassName: MessageService
 * @Description: 消息处理服务
 * @author 吴禹青
 * @date 2013-10-23 下午7:55:10
 * @Version 1.0
 * 
 */
public class MessageService extends Service {
	protected static final String TAG = "MessageService";

	public static boolean isRunning = false;

	// 连接成功后延时同步通道消息 3s
	private final int GETUSERSUBSCRIBECHANNEL_DELAYMILLIS = 3000;
	// 连接成功，同步通道请求
	private final int WHAT_GETUSERSUBSCRIBECHANNEL = 1;
	// 同步消息通知
	private final int WHAT_SYNCMESSAGENOTIFICATION = 2;
	// 通道变化通知
	private final int WHAT_CHANNELCHANGEDNOTIFICATION = 3;
	// 踢下线通知
	private final int WHAT_UNAUTHENTICATED = 4;

	private ActivityManager manager;

	private String topPname = "";

	private boolean isStop = true;

	private NetWorkReceiver netWorkReceiver;

	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case WHAT_GETUSERSUBSCRIBECHANNEL:
				if (SystemContext.getInstance().isOpenMessage())
					ProxyFactory.getInstance().getMessageProxy().getUserSubscribeChannel(MessageService.this);
				// 采集本地安装的应用信息
				long saveTime = SystemContext.getInstance().getCollectCtInfoTime();
				long curTime = System.currentTimeMillis();
				if (curTime - saveTime >= SystemConfig.SEVEN_DAY_TIME) {
					SystemContext.getInstance().setCollectCtInfoTime(curTime);
					collectAppInfo();
					collectContactInfo();
					//getLocalInstallPackages();
				}
				// 上传通讯层crash日志文件
				UploadLogUtil.UploadAndDeleteDirectory(UploadLogUtil.LOGPATH);
				break;
			case WHAT_SYNCMESSAGENOTIFICATION:
				if (SystemContext.getInstance().isOpenMessage()) {
					SyncMessageNotification notification = (SyncMessageNotification) msg.obj;
					ProxyFactory.getInstance().getMessageProxy().handleSyncMessageNotification(MessageService.this, notification);
				}
				break;
			case WHAT_CHANNELCHANGEDNOTIFICATION:
				if (SystemContext.getInstance().isOpenMessage())
					ProxyFactory.getInstance().getMessageProxy().getUserSubscribeChannel(MessageService.this);
				break;
			case WHAT_UNAUTHENTICATED:
				if (SystemContext.getInstance().getExtUserVo() != null && SystemContext.getInstance().getToken() != null) {
					ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					RunningTaskInfo info = manager.getRunningTasks(1).get(0);
					String packageName = info.topActivity.getPackageName();
					if (!info.topActivity.getClassName().equals(LoginActivity.class.getName())) {
						String currentPackage = AppUtils.getLocalAppPackageInfo(MessageService.this).packageName;
						if (packageName.equals(currentPackage)) {
							if (SystemContext.getInstance().isHasSendUnAuthInfo()) {
								return;
							} else {
								SystemContext.getInstance().setHasSendUnAuthInfo(true);
							}
							// 会话失效， 主动注销，token不存在/认证失败，都到登录页
							jumpMainView();
						} else {
							SystemContext.getInstance().setUnAuth(true);
						}
					}
				}
				break;
			}
		}

	};

	/**
	 * 跳转至主界面然后跳转到登录界面
	 */
	private void jumpMainView() {
		Intent intent = new Intent(this, MainFragmentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -5);
		bundle.putInt("Type", 0);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 采集通讯录信息发送给服务端
	 */
	protected void collectContactInfo() {
		List<Map<String, String>> contacts = ContactUtil.getPhoneContacts(this);
		if (contacts == null || contacts.size() <= 0)
			return;
		UploadContactsRequest.Builder b = UploadContactsRequest.newBuilder();
		int i = 0;
		int size = contacts.size();
		Map<String, String> mp;
		for (i = 0; i < size; i++) {
			mp = contacts.get(i);
			ContactsEntry.Builder entry = ContactsEntry.newBuilder();
			entry.setName(mp.get(Phone.DISPLAY_NAME));
			entry.setPhoneNumber(mp.get(Phone.NUMBER));
			b.addEntry(entry.build());
		}
		ProxyFactory.getInstance().getUserProxy().collectContactInfo(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
			}
		}, this, b.build());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initOrNetStatusChanged(NetworkUtil.isConnect(this) ? EnvStatusType.ENV_STATUS_WIFI : EnvStatusType.ENV_STATUS_NONETWORK);
		isRunning = true;
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long triggerAtTime = SystemClock.elapsedRealtime();
		// 10s 一个周期，不停的发送广播
		long interval = 10 * 1000;
		Intent intent = new Intent();
		intent.setClass(this, MessageService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, interval, pi);
		LogUtil.debug("--------->>MessageService::onCreate");
		// 启动服务
		isStop = true;
		manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		new Thread(new UpdateRunnable()).start();

		netWorkReceiver = new NetWorkReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NetWorkReceiver.ACTION);
		registerReceiver(netWorkReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogUtil.debug("---------> destory");
		if (netWorkReceiver != null) {
			this.unregisterReceiver(netWorkReceiver);
			netWorkReceiver = null;
		}
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	/**
	 * 获取当前正在运行的应用
	 */
	private void getRunningProcess() {
		if (SystemContext.getInstance().getExtUserVo() != null) {
			LogUtil.debug("------->获取当前正在运行的应用");
			RunningTaskInfo info = manager.getRunningTasks(1).get(0);
			String packageName = info.topActivity.getPackageName();
			LogUtil.debug("------>packageName=" + packageName);
			if (topPname.isEmpty() || !topPname.equals(packageName)) {
				GamePackageVo gamePackage = GameUtil.getGamePackage(packageName);
				if (gamePackage != null) {
					if (!topPname.isEmpty()) {
						// 提交服务端退出当前开启的应用
						GamePackageVo oldGamePackage = GameUtil.getGamePackage(topPname);
						if (oldGamePackage != null) {
							GameUtil.userAction(oldGamePackage.getGameid(), MsgsConstants.OT_GAME, MsgsConstants.OP_APP_RUN);
							LogUtil.debug("------->当前退出运行的贴吧包名：" + topPname);
						}
					}
					// 赋值现在的顶层应用包名称
					topPname = packageName;
					long gid = gamePackage.getGameid();
					// 提交服务端现在开启的应用
					GameUtil.userAction(gid, MsgsConstants.OT_GAME, MsgsConstants.OP_APP_START);
					LogUtil.debug("------->当前开始运行的贴吧包名：" + packageName);
				}
			}
		}
	}

	public static void getLocalInstallPackages() {
		followCount = 0;
		if (SystemContext.getInstance().getExtUserVo() != null) {
			LogUtil.d(TAG, "关注...");
			PackageManager pm = SystemContext.getInstance().getContext().getPackageManager();
			List<ApplicationInfo> applist = pm.getInstalledApplications(0);
			if (applist != null) {
				for (ApplicationInfo info : applist) {
					if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
						GamePackageVo gamePackage = GameUtil.getGamePackage(info.packageName);
						if (gamePackage != null) {
							followGame(gamePackage.getGameid(), MsgsConstants.OP_FOLLOW);
						}
					}
				}
			}
		}
	}

	
	private static int followCount;
	private static int followedCount;
	/**
	 * 添加或取消关注
	 * 
	 * @param v
	 * @param gid
	 * @param op
	 */
	private static void followGame(long gid, int op) {
		followCount ++;
		UserProxyImpl.getInstance().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					LogUtil.d(TAG, "关注成功 result＝" + result);
					break;
				default:
					break;
				}
				followedCount ++;
				if(followCount <= followedCount)
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, null);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.d(TAG, "关注失败 result＝" + result + ", resultMsg=" + resultMsg);
				followedCount ++;
				if(followCount <= followedCount)
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, null);
			}
		}, SystemContext.getInstance().getContext(), gid, MsgsConstants.OT_GAME, op, null, null, null);
	}

	/**
	 * 采集应用信息
	 */
	private void collectAppInfo() {
		try {
			List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
			if (packages != null) {
				String deviceId = OpenUDID_manager.getOpenUDID();
				JSONArray array = new JSONArray();
				for (PackageInfo info : packages) {
					if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
						JSONObject obj = new JSONObject();
						obj.put("did", deviceId);
						obj.put("appp", info.packageName);
						obj.put("appn", info.applicationInfo.loadLabel(getPackageManager()).toString());
						obj.put("appv", info.versionName);
						obj.put("appc", info.versionCode);
						obj.put("name", AppUtils.getLocalAppVersionName(MessageService.this));
						obj.put("code", AppUtils.getLocalAppVersionCode(MessageService.this));
						array.put(obj);
					}
				}
				ProxyFactory.getInstance().getUserProxy().collectAppInfo(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
					}
				}, MessageService.this, array.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @ClassName: UpdateRunnable
	 * @Description: TODO(...)
	 * @author 王卫
	 * @date 2013-9-12 下午03:58:41
	 * @Version 1.0
	 * 
	 */
	@SuppressLint("HandlerLeak")
	class UpdateRunnable implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			while (isStop) {
				try {
					getRunningProcess();
					Thread.sleep(900000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * @ClassName: BroadCastDemo
	 * @Description: 网络状态监听广播接收
	 * @author 王卫
	 * @date 2014-1-13 下午4:47:57
	 * @Version 1.0
	 * 
	 */
	class NetWorkReceiver extends BroadcastReceiver {
		protected static final String TAG = "NetWorkReceiver";
		public static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

		private Context mContext = null;

		@Override
		public void onReceive(Context context, Intent intent) {
			mContext = context;
			if (ACTION.equals(intent.getAction())) {
				// 获取手机的连接服务管理器，这里是连接管理器类
				ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
				State wifiState = null;
				State mobileState = null;
				if (wifiInfo != null) {
					wifiState = wifiInfo.getState();
				}
				if (mobileInfo != null) {
					mobileState = mobileInfo.getState();
				}
				if (wifiState != null && (wifiState == State.CONNECTED || wifiState == State.CONNECTING)) {
					initOrNetStatusChanged(EnvStatusType.ENV_STATUS_WIFI);
				} else if (mobileState != null && (mobileState == State.CONNECTED || mobileState == State.CONNECTING)) {
					initOrNetStatusChanged(EnvStatusType.ENV_STATUS_MOBILE_3G);
				} else {
					if (!NetworkUtil.isConnect(context)) {
						initOrNetStatusChanged(EnvStatusType.ENV_STATUS_NONETWORK);
					} else {
						initOrNetStatusChanged(EnvStatusType.ENV_STATUS_WIFI);
					}
				}
			}

		}
	}

	private void initOrNetStatusChanged(final EnvStatusType netType) {
		if (EnvStatusType.ENV_STATUS_NONETWORK == netType) {
			if (SessionUtil.getSession() != null) {
				SessionUtil.onEnvStatusChanged(netType);
			} else {
				if (SystemContext.getInstance().getConfigLoadedStatus() == SystemContext.READY) {
					initSession(netType);
				}
			}
		} else {
			initSession(netType);
		}
	}

	private void initSession(final EnvStatusType netType) {
		new Thread() {
			@Override
			public void run() {
				XActionNotificationListener xActionNotificationListener = new XActionNotificationListener() {
					@Override
					public void onNotify(XActionNotification notification) {
						LogUtil.debug("--收到服务器数据的通知：apptype:" + SystemContext.APPTYPE + ", notification:" + notification.toString());
						if (notification != null && notification.getNotificationId() == MsgsConstants.NOTIFICATION_MSG_SYN_MESSAGE
								&& notification.getExtension().hasExtension(Msgs.syncNotification)) {
							// 通知需要同步数据
							android.os.Message msg = android.os.Message.obtain();
							msg.what = WHAT_SYNCMESSAGENOTIFICATION;
							msg.obj = notification.getExtension().getExtension(Msgs.syncNotification);
							handler.sendMessage(msg);
						} else if (notification != null && notification.getNotificationId() == MsgsConstants.NOTIFICATION_MSG_CHANNEL_CHANGED) {
							// 通知通道改变
							android.os.Message msg = android.os.Message.obtain();
							msg.what = WHAT_CHANNELCHANGEDNOTIFICATION;
							handler.sendMessage(msg);
						}

					}
				};
				XActionSessionStatusListener xActionSessionStatusListener = new XActionSessionStatusListener() {
					@Override
					public void onCallback(XActionSession session, XActionSessionStatus old, XActionSessionStatus current) {
						LogUtil.d(TAG, "StatusListener " + "old: " + old + ", current: " + current);
						SessionUtil.status = current;
						if (current == XActionSessionStatus.SESSION_OPENED) {
							// 发出同步通道的请求
							LogUtil.d(TAG, "--发出同步通道的请求" + ",status:" + session.getStatus() + ":longclient:" + SessionUtil.getLongClient());
							android.os.Message msg = android.os.Message.obtain();
							msg.what = WHAT_GETUSERSUBSCRIBECHANNEL;
							handler.sendMessageDelayed(msg, GETUSERSUBSCRIBECHANNEL_DELAYMILLIS);
						} else if (current == XActionSessionStatus.SESSION_UNAUTHENTICATED) {
							// 通知需要同步数据
							android.os.Message msg = android.os.Message.obtain();
							msg.what = WHAT_UNAUTHENTICATED;
							handler.sendMessage(msg);
						}
					}
				};
				SessionUtil.initSession(netType, xActionNotificationListener, xActionSessionStatusListener);
			}
		}.start();
	}

}
