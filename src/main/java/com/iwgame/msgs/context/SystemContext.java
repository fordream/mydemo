/**      
 * SysContext.java Create on 2013-7-30     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.openudid.OpenUDID_manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;

import com.baidu.location.BDLocation;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.module.setting.vo.ChannelVo;
import com.iwgame.msgs.module.setting.vo.MessageSubjectRuleVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.UserGameServerResult;
import com.iwgame.msgs.utils.UserUtil;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;

/**
 * @ClassName: SysContext
 * @Description: 系统上下文
 * @author 王卫
 * @date 2013-7-30 下午06:15:23
 * @Version 1.0
 * 
 */
@SuppressLint("DefaultLocale")
public class SystemContext {

	private static final String SYNC_KEY = "SYNC_KEY";

	private static SystemContext instance;

	/**
	 * 上下文业务数据
	 */
	private ExtUserVo extUserVo;
	// 版本
	private final int version = 0x0100;
	// 令牌
	private String token;
	// 设备ID
	private String deviceId;
	// 账号
	private String account;
	// 密码
	private String password;
	// 手机号
	private String mobile;
	// 当前登录账号状态 游客；有资料无手机；无资料有手机；正常用户
	private String accountType;
	// 第三方登录类型
	private String apiType;
	// 第三方UID
	private String openId;
	// 当前用户是否是游客（判断行为动作是否需要绑定手机）
	private int isGuest = -1;
	// 游客账号
	private String guestAccount;
	// 游客密码
	private String guestPassword;
	// 游客状态（判断登录页面账号是否显示）
	private int guestStatus = -1;
	// 游客是否更改过用户昵称
	private int isUpdateUserName = -1;// 1 已修改
	// 登录状态，判断APP是否已经有正常账号登录过或者绑定过手机，如果有则游客进入按钮不可用
	private int loginStatus = -1;
	// 上下文
	private Context context;
	// 全局SharedPreferences
	private SharedPreferences mSharedPreferences;
	// 用户的SharedPreferences
	private SharedPreferences mUserSharedPreferences;
	// 游客的SharedPreferences
	private SharedPreferences mGuestSharedPreferences;
	// 接受消息开关
	private boolean openMessage = true;
	// 是否初始化完成
	public boolean initCompleted = false;
	// 是否要推荐贴吧
	private boolean recommendGameTag = false;
	// 是否认证失败
	private boolean unAuth = false;
	// Session状态
	private int configLoadedStatus = ON_READY;
	// 未加载
	public static final int ON_READY = 0;
	// 已加载
	public static final int READY = 1;

	public static String GLOBAL_APPCONFIG_URL = "";
	public static String GLOBAL_CONFIG_URL = "";
	public static String UMENG_CHANNEL = "";
	public static String WXPAY_APPID = "";
	// app类型，游伴/攻略
	public static String APPTYPE = "";
	// app内核描述
	public static String COREDESC = "";
	// 用户等级策略集合
	public List<UserGradeVo> userGradeConfig;
	// 公会等级策略集合
	public List<GroupGradeVo> groupGradeConfig;
	// 积分任务策略集合
	public List<PointTaskVo> pointTaskConfig;

	public static MainFragmentActivity mainFragmentActivity = null;

	public static String SHARETAG = "";

	public static long topicId = -1;

	public static JSONObject guestPermission = null;

	public static HashMap<String, Boolean> guestPermissionMap = null;

	private String pushIdString = null;

	private boolean hasNewMessage = true;

	private boolean reloadTag = false;
	// 是否已经发送过被迫下线的通知
	private boolean isHasSendUnAuthInfo = false;
	// 通讯层是否已打开
	private boolean isNewOpened = false;

	// 是否从工程本地res下读取图片
	private boolean isLoadAppLaunchFromLocal = true;

	public long THREE_DAY_TIME = 259200000L;
	// 但前运行的页面
	private Activity currentActivity;
	// 游戏服务器
	private Msgs.UserGameServerResult gameServices;
	// 陪玩业务用户封停状态
	private int userPlayStatus;
	
	/**
	 * 
	 */
	private SystemContext() {
	}

	public static SystemContext getInstance() {
		synchronized (SYNC_KEY) {
			if (instance == null)
				instance = new SystemContext();
			return instance;
		}
	}

	public void cleanContext() {
		// SystemContext.getInstance().setExtUserVo(null);
		extUserVo = null;
		SystemContext.getInstance().setToken(null);

		SystemContext.getInstance().mSharedPreferences = null;
		SystemContext.getInstance().mUserSharedPreferences = null;
		SystemContext.getInstance().userSyncMode0Key = 0L;
		SystemContext.getInstance().userSyncMode1Key = 0L;
		SystemContext.getInstance().userSyncMode2Key = 0L;
		SystemContext.getInstance().gameSyncKey = 0L;
		SystemContext.getInstance().gamePackSyncKey = 0L;
		SystemContext.getInstance().relationGameSyncKey = 0L;
		// SystemContext.getInstance().unReadMessageCount = 0;

		SystemContext.getInstance().channelSyncing.clear();
		SystemContext.getInstance().channelDiscard.clear();

		// 数据库HELP实例置空
		MySQLiteOpenHelper.clean();
		setHasNewMessage(true);
		SystemContext.getInstance().setNewOpened(false);
		SystemContext.getInstance().setHasNewMessage(true);
		if (MainFragmentActivity.getInstance() != null)
			MainFragmentActivity.getInstance().updataNewsUnReadCount(0);
	}

	public void clearUserContext() {
		MySQLiteOpenHelper.clean();
		SystemContext.getInstance().setExtUserVo(null);
		SystemContext.getInstance().mSharedPreferences = null;
		SystemContext.getInstance().mUserSharedPreferences = null;
	}

	public void init(Context appContext) {
		this.context = appContext;
		getSharedPreferences();
		// this.mSharedPreferences =
		// mContext.getSharedPreferences(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL,
		// PreferenceActivity.MODE_PRIVATE);
		// 读当前有效的用户

		// 初始化用户的信息
	}

	// 读系统用户配置文件
	public SharedPreferences getUserSharedPreferences() {
		if (this.mUserSharedPreferences == null) {
			long uid = getSharedPreferences().getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, 0);
			this.mUserSharedPreferences = context.getSharedPreferences(Long.toString(uid) + SystemConfig.SHAREDPREFERENCES_NAME_USER_SUFFIX,
					PreferenceActivity.MODE_PRIVATE);
		}
		return mUserSharedPreferences;
	}

	// 读系统全局配置文件
	public SharedPreferences getSharedPreferences() {
		if (this.mSharedPreferences == null) {
			this.mSharedPreferences = context.getSharedPreferences(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL, PreferenceActivity.MODE_PRIVATE);
		}
		return mSharedPreferences;
	}

	// 读系统全局配置文件
	public SharedPreferences getGuestSharedPreferences() {
		if (this.mGuestSharedPreferences == null) {
			this.mGuestSharedPreferences = context.getSharedPreferences(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_SUFFIX,
					PreferenceActivity.MODE_PRIVATE);
		}
		return mGuestSharedPreferences;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		if (token == null) {
			// 从本地配置文件中读取
			token = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TOKEN, null);
		}
		return token;
	}

	/**
	 * @param tokenString
	 *            the token to set
	 */
	public void setToken(String tokenString) {
		this.token = tokenString;
		if (tokenString != null) {
			// 设置本地全局文件(token值)
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TOKEN, tokenString).commit();
			setUnAuth(false);
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TOKEN).commit();
		}
	}

	/**
	 * @return the device id
	 */
	public String getDeviceId() {
		if (deviceId != null) {
			return deviceId;
		}

		String value = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_DEVICEID, null);
		if (value != null) {
			deviceId = value;
			return deviceId;
		}

		value = OpenUDID_manager.getOpenUDID();
		if (value != null) {
			deviceId = value;
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_DEVICEID, deviceId).commit();
			return deviceId;
		}

		return null;
	}

	/**
	 * @return the token
	 */
	public String getAccountType() {
		if (accountType != null) {
			return accountType;
		} else {
			// 从本地配置文件中读取
			return getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_ACCOUNT_TYPE, null);
		}
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
		if (accountType != null) {
			// 设置本地全局文件(token值)
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_ACCOUNT_TYPE, accountType).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_ACCOUNT_TYPE).commit();
		}
	}

	/**
	 * @return the token
	 */
	public int getIsGuest() {
		if (isGuest != -1) {
			return isGuest;
		} else {
			// 从本地配置文件中读取
			return getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_IS_GUEST, 0);
		}
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setIsGuest(int isGuest) {
		this.isGuest = isGuest;
		if (isGuest != -1) {
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_IS_GUEST, isGuest).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_IS_GUEST).commit();
		}
	}

	/**
	 * 设置账号
	 * 
	 * @param account
	 */
	public void setAccount(String account) {
		this.account = account;
		if (account != null) {
			// 设置本地全局文件(account值)
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ACCOUNT, account).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ACCOUNT).commit();
		}
	}

	/**
	 * 获取账号
	 * 
	 * @return the account
	 */
	public String getAccount() {
		if (account != null) {
			return account;
		} else {
			// 从本地配置文件中读取
			return getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ACCOUNT, null);
		}
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
		if (mobile != null) {
			// 设置本地全局文件(account值)
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM, mobile).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM).commit();
		}
	}

	public String getMobile() {
		if (mobile != null) {
			return mobile;
		} else {
			// 从本地配置文件中读取
			return getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM, null);
		}
	}

	/**
	 * 
	 * @Title: setApiType
	 * @Description: 设置第三方登录API类型
	 * @param @param apiType 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void setApiType(String apiType) {
		this.apiType = apiType;
		if (apiType != null) {
			// 设置本地全局文件 apiType
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_API_TYPE, apiType).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_API_TYPE).commit();
		}
	}

	/**
	 * 
	 * @Title: getApiType
	 * @Description: 获得第三方登录API类型
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public String getApiType() {
		if (apiType != null) {
			return apiType;
		} else {
			// 从本地配置文件中读取
			return getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_API_TYPE, null);
		}
	}

	/**
	 * 
	 * @Title: setOpenId
	 * @Description: 设置第三方Uid
	 * @param @param openId 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
		if (openId != null) {
			// 设置本地全局文件(openid)
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_OPEN_ID, openId).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_OPEN_ID).commit();
		}
	}

	/**
	 * 
	 * @Title: getOpenId
	 * @Description: 获得第三方登录后的UID
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public String getOpenId() {
		if (openId != null) {
			return openId;
		} else {
			// 从本地配置文件中读取
			return getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_OPEN_ID, null);
		}
	}

	/**
	 * 设置游客账号
	 * 
	 * @param guestAccount
	 */
	public void setGuestAccount(String guestAccount) {
		this.guestAccount = guestAccount;
		if (guestAccount != null) {
			// 设置本地全局文件(account值)
			getGuestSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_ACCOUNT, guestAccount).commit();
		} else {
			getGuestSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_ACCOUNT).commit();
		}
	}

	/**
	 * 获取游客账号
	 * 
	 * @return the guestAccount
	 */
	public String getGuestAccount() {
		if (guestAccount != null) {
			return guestAccount;
		} else {
			// 从本地配置文件中读取
			return getGuestSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_ACCOUNT, null);
		}
	}

	/**
	 * 设置密码
	 * 
	 * @param password
	 */
	public void setPasssword(String password) {
		this.password = password;
		if (password != null) {
			// 设置本地全局文件(password值)
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PASSWORD, password).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PASSWORD).commit();
		}
	}

	/**
	 * 获取密码
	 * 
	 * @return the password
	 */
	public String getPassword() {
		if (password != null) {
			return password;
		} else {
			// 从本地配置文件中读取
			return getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PASSWORD, null);
		}
	}

	/**
	 * 设置游客密码
	 * 
	 * @param guestPassword
	 */
	public void setGuestPasssword(String guestPassword) {
		this.guestPassword = guestPassword;
		if (guestPassword != null) {
			// 设置本地全局文件(password值)
			getGuestSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_PASSWORD, guestPassword).commit();
		} else {
			getGuestSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_PASSWORD).commit();
		}
	}

	/**
	 * 获取游客密码
	 * 
	 * @return the guestPassword
	 */
	public String getGuestPassword() {
		if (guestPassword != null) {
			return guestPassword;
		} else {
			// 从本地配置文件中读取
			return getGuestSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_PASSWORD, null);
		}
	}

	/**
	 * 设置游客绑定手机状态
	 * 
	 * @param guestStatus
	 */
	public void setGuestStatus(int guestStatus) {
		this.guestStatus = guestStatus;
		if (guestStatus != -1) {
			// 设置本地全局文件(password值)
			getGuestSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_STATUS, guestStatus).commit();
		} else {
			getGuestSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_STATUS).commit();
		}
	}

	/**
	 * 获取游客手机状态
	 * 
	 * @return the guestStatus
	 */
	public int getGuestStatus() {
		if (guestStatus != -1) {
			return guestStatus;
		} else {
			// 从本地配置文件中读取
			return getGuestSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GUEST_STATUS, -1);
		}
	}

	/**
	 * 设置账户登录状态
	 * 
	 * @param guestStatus
	 */
	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
		if (loginStatus != -1) {
			// 设置本地全局文件(password值)
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_LOGIN_STATUS, loginStatus).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_LOGIN_STATUS).commit();
		}
	}

	/**
	 * 获取账户登录状态
	 * 
	 * @return the guestStatus
	 */
	public int getLoginStatus() {
		if (loginStatus != -1) {
			return loginStatus;
		} else {
			// 从本地配置文件中读取
			return getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_LOGIN_STATUS, -1);
		}
	}

	/**
	 * 设置游客是否更改过用户昵称
	 * 
	 * @param guestStatus
	 */
	public void setIsUpdateUserName(int isUpdateUserName) {
		this.isUpdateUserName = isUpdateUserName;
		if (isUpdateUserName != -1) {
			// 设置本地全局文件(isUpdateUserName值)
			getGuestSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_IS_UPDATE_USERNAME, isUpdateUserName).commit();
		} else {
			getGuestSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_IS_UPDATE_USERNAME).commit();
		}
	}

	/**
	 * 获取游客是否更改过用户昵称
	 * 
	 * @return the guestStatus
	 */
	public int getIsUpdateUserName() {
		if (isUpdateUserName != -1) {
			return isUpdateUserName;
		} else {
			// 从本地配置文件中读取
			return getGuestSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_IS_UPDATE_USERNAME, -1);
		}
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	// 同步用户的key （mode = 0 ，关注）
	private long userSyncMode0Key = 0L;
	// 同步用户的key （mode = 1 ，粉丝）
	private long userSyncMode1Key = 0L;
	// 同步用户的key （mode = 1 ，粉丝）
	private long userSyncMode2Key = 0L;

	public long getUserSyncMode0Key() {
		if (userSyncMode0Key == 0)
			getUserSharedPreferences().getLong(SystemConfig.KEY_SYNCUSER_MODE_0, 0);
		return userSyncMode0Key;
	}

	public void setUserSyncMode0Key(long userSyncMode0Key) {
		this.userSyncMode0Key = userSyncMode0Key;
		getUserSharedPreferences().edit().putLong(SystemConfig.KEY_SYNCUSER_MODE_0, userSyncMode0Key).commit();
	}

	public long getUserSyncMode1Key() {
		if (userSyncMode1Key == 0)
			getUserSharedPreferences().getLong(SystemConfig.KEY_SYNCUSER_MODE_1, 0);
		return userSyncMode1Key;
	}

	public void setUserSyncMode1Key(long userSyncMode1Key) {
		this.userSyncMode1Key = userSyncMode1Key;
		getUserSharedPreferences().edit().putLong(SystemConfig.KEY_SYNCUSER_MODE_1, userSyncMode1Key).commit();
	}

	public long getUserSyncMode2Key() {
		if (userSyncMode2Key == 0)
			getUserSharedPreferences().getLong(SystemConfig.KEY_SYNCUSER_MODE_2, 0);
		return userSyncMode2Key;
	}

	public void setUserSyncMode2Key(long userSyncMode2Key) {
		this.userSyncMode2Key = userSyncMode2Key;
		getUserSharedPreferences().edit().putLong(SystemConfig.KEY_SYNCUSER_MODE_2, userSyncMode2Key).commit();
	}

	// 同步贴吧key
	private long gameSyncKey;
	// 同步贴吧包key
	private long gamePackSyncKey;
	// //同步关系贴吧key
	private long relationGameSyncKey;

	public long getGameSyncKey() {
		if (gameSyncKey == 0)
			getUserSharedPreferences().getLong(SystemConfig.KEY_SYNCGAME, 0);
		return gameSyncKey;
	}

	public void setGameSyncKey(long gameSyncKey) {
		this.gameSyncKey = gameSyncKey;
		getUserSharedPreferences().edit().putLong(SystemConfig.KEY_SYNCGAME, gameSyncKey).commit();
	}

	public long getGamePackSyncKey() {
		if (gamePackSyncKey == 0)
			getUserSharedPreferences().getLong(SystemConfig.KEY_SYNCGAMEPACK, 0);
		return gamePackSyncKey;
	}

	public void setGamePackSyncKey(long gamePackSyncKey) {
		this.gamePackSyncKey = gamePackSyncKey;
		getUserSharedPreferences().edit().putLong(SystemConfig.KEY_SYNCGAMEPACK, gamePackSyncKey).commit();
	}

	public long getRelationGameSyncKey() {
		if (relationGameSyncKey == 0)
			getUserSharedPreferences().getLong(SystemConfig.KEY_SYNCRELATIONGAME, 0);
		return relationGameSyncKey;
	}

	public void setRelationGameSyncKey(long relationGameSyncKey) {
		this.relationGameSyncKey = relationGameSyncKey;
		getUserSharedPreferences().edit().putLong(SystemConfig.KEY_SYNCRELATIONGAME, relationGameSyncKey).commit();
	}

	/**
	 * 获取用户关系同步KEY
	 * 
	 * @return
	 */
	public long getUserRelSyncKey() {
		return getUserSharedPreferences().getLong(SystemConfig.SYNC_KEY_USER_REL, 0);
	}

	/**
	 * 设置用户关系同步KEY
	 * 
	 * @param userRelSyncKey
	 */
	public void setUserRelSyncKey(long userRelSyncKey) {
		getUserSharedPreferences().edit().putLong(SystemConfig.SYNC_KEY_USER_REL, userRelSyncKey).commit();
	}

	/**
	 * 获取贴吧关系同步KEY
	 * 
	 * @return
	 */
	public long getGameRelSyncKey() {
		return getUserSharedPreferences().getLong(SystemConfig.SYNC_KEY_GAME_REL, 0);
	}

	/**
	 * 设置贴吧关系同步KEY
	 * 
	 * @param gameRelSyncKey
	 */
	public void setGameRelSyncKey(long gameRelSyncKey) {
		getUserSharedPreferences().edit().putLong(SystemConfig.SYNC_KEY_GAME_REL, gameRelSyncKey).commit();
	}

	/**
	 * 获取贴吧同步KEY
	 * 
	 * @return
	 */
	public long getGameContentSyncKey() {
		return getUserSharedPreferences().getLong(SystemConfig.SYNC_KEY_GAME, 0);
	}

	/**
	 * 设置贴吧同步KEY
	 * 
	 * @param gameRelSyncKey
	 */
	public void setGameContentSyncKey(long gameSyncKey) {
		getUserSharedPreferences().edit().putLong(SystemConfig.SYNC_KEY_GAME, gameSyncKey).commit();
	}

	/**
	 * 获取贴吧包同步KEY
	 * 
	 * @return
	 */
	public long getGamePackageContentSyncKey() {
		return getUserSharedPreferences().getLong(SystemConfig.SYNC_KEY_GAME_PACKATE, 0);
	}

	/**
	 * 设置贴吧包同步KEY
	 * 
	 * @param gamePackageSyncKey
	 */
	public void setGamePackageContentSyncKey(long gamePackageSyncKey) {
		getUserSharedPreferences().edit().putLong(SystemConfig.SYNC_KEY_GAME_PACKATE, gamePackageSyncKey).commit();
	}

	/**
	 * 
	 * @return
	 */
	public long getUserGradePolicySyncKey() {
		return getUserSharedPreferences().getLong(SystemConfig.SYNC_KEY_USER_GRADE_POLICY, 0);
	}

	/**
	 * 
	 * @param userGradeSyncKey
	 */
	public void setUserGradePolicySyncKey(long userGradeSyncKey) {
		getUserSharedPreferences().edit().putLong(SystemConfig.SYNC_KEY_USER_GRADE_POLICY, userGradeSyncKey).commit();
	}

	/**
	 * 
	 * @return
	 */
	public long getGroupGradePolicySyncKey() {
		return getUserSharedPreferences().getLong(SystemConfig.SYNC_KEY_GROUP_GRADE_POLICY, 0);
	}

	/**
	 * 
	 * @param groupGradeSyncKey
	 */
	public void setGroupGradePolicySyncKey(long groupGradeSyncKey) {
		getUserSharedPreferences().edit().putLong(SystemConfig.SYNC_KEY_GROUP_GRADE_POLICY, groupGradeSyncKey).commit();
	}

	/**
	 * 
	 * @return
	 */
	public long getPointTaskSyncKey() {
		return getUserSharedPreferences().getLong(SystemConfig.SYNC_KEY_POINT_TASK_DETAIL, 0);
	}

	/**
	 * 
	 * @param pointTaskDetailSyncKey
	 */
	public void setPointTaskSyncKey(long pointTaskDetailSyncKey) {
		getUserSharedPreferences().edit().putLong(SystemConfig.SYNC_KEY_POINT_TASK_DETAIL, pointTaskDetailSyncKey).commit();
	}

	/**
	 * 获取使用状态
	 * 
	 * @return
	 */
	public boolean getUsedStatus() {
		return getSharedPreferences().getBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USED, false);
	}

	/**
	 * 获取公会同步KEY
	 * 
	 * @return
	 */
	public long getGroupSyncKey() {
		return getUserSharedPreferences().getLong(SystemConfig.SYNC_KEY_GROUP, 0);
	}

	/**
	 * 设置公会同步KEY
	 * 
	 * @param gameRelSyncKey
	 */
	public void setGroupSyncKey(long groupSyncKey) {
		getUserSharedPreferences().edit().putLong(SystemConfig.SYNC_KEY_GROUP, groupSyncKey).commit();
	}

	/**
	 * @return the extUserVo
	 */
	public ExtUserVo getExtUserVo() {
		if (extUserVo == null) {
			ExtUserVo vo = null;
			long uid = getSharedPreferences().getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, 0);
			long serial = getSharedPreferences().getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SERIAL, 0);
			String avatar = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_AVATAR, null);
			int grade = getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRADE, 0);
			String mood = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOOD, null);
			int sex = getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX, 0);
			int age = getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_AGE, 0);
			String city = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CITY, null);
			String job = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_JOB, null);
			String username = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USERNAME, null);
			String desc = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_DESC, null);
			String gametime = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMETIME, null);
			String likegaemtype = getSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_LIKEGAMETYPE, null);
			int point = getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POINT, 0);
			int isGuest = getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_IS_GUEST, -1);
			if (uid > 0) {
				vo = new ExtUserVo();
				vo.setUserid(uid);
				vo.setSerial(serial);
				vo.setAvatar(avatar);
				vo.setGrade(grade);
				vo.setMood(mood);
				vo.setSex(sex);
				vo.setAge(age);
				vo.setCity(city);
				vo.setJob(job);
				vo.setUsername(username);
				vo.setDescription(desc);
				vo.setGameTime(gametime);
				vo.setLikeGameType(likegaemtype);
				vo.setPoint(point);
				vo.setIsGuest(isGuest);
			}
			extUserVo = vo;
		}
		return extUserVo;
	}

	/**
	 * @param extUserVo
	 *            the extUserVo to set
	 */
	public void setExtUserVo(ExtUserVo extUserVo) {
		if (extUserVo != null) {
			// 设置本地全局文件(用户相关基本信息值)
			getSharedPreferences().edit().putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, extUserVo.getUserid()).commit();
			getSharedPreferences().edit().putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SERIAL, extUserVo.getSerial()).commit();
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_AVATAR, extUserVo.getAvatar()).commit();
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRADE, extUserVo.getGrade()).commit();
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOOD, extUserVo.getMood()).commit();
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX, extUserVo.getSex()).commit();
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_AGE, extUserVo.getAge()).commit();
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CITY, extUserVo.getCity()).commit();
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_JOB, extUserVo.getJob()).commit();
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USERNAME, extUserVo.getUsername()).commit();
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_DESC, extUserVo.getDescription()).commit();
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMETIME, extUserVo.getGameTime()).commit();
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_LIKEGAMETYPE, extUserVo.getLikeGameType()).commit();
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_IS_GUEST, extUserVo.getIsGuest()).commit();
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POINT, extUserVo.getPoint()).commit();
			getSharedPreferences().edit().putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USED, true).commit();
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ALBUM_COUNT, extUserVo.getAlbumCount()).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SERIAL).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_AVATAR).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRADE).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOOD).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_AGE).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CITY).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_JOB).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USERNAME).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_DESC).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMETIME).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_LIKEGAMETYPE).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ISFOLLOW).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POINT).commit();
			// 还原默认值
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_IS_GUEST, -1).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_API_TYPE).commit();
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_OPEN_ID).commit();
		}
		this.extUserVo = extUserVo;
	}

	/**
	 * 设置头像
	 * 
	 * @param avatar
	 */
	public void setAvatar(String avatar) {
		if (extUserVo != null) {
			extUserVo.setAvatar(avatar);
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_AVATAR, extUserVo.getAvatar()).commit();
		}
	}

	/**
	 * 设置心情
	 * 
	 * @param mood
	 */
	public void setMood(String mood) {
		if (extUserVo != null) {
			extUserVo.setMood(mood);
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOOD, extUserVo.getMood()).commit();
		}
	}

	/**
	 * 设置性别
	 * 
	 * @param sex
	 */
	public void setSex(int sex) {
		if (extUserVo != null) {
			extUserVo.setSex(sex);
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX, extUserVo.getSex()).commit();
		}
	}

	/**
	 * 设置年份
	 * 
	 * @param age
	 */
	public void setAge(int age) {
		if (extUserVo != null) {
			extUserVo.setAge(age);
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_AGE, extUserVo.getAge()).commit();
		}
	}

	/**
	 * 设置城市
	 * 
	 * @param city
	 */
	public void setCity(String city) {
		if (extUserVo != null) {
			extUserVo.setCity(city);
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CITY, extUserVo.getCity()).commit();
		}
	}

	/**
	 * 设置职业
	 * 
	 * @param job
	 */
	public void setJob(String job) {
		if (extUserVo != null) {
			extUserVo.setJob(job);
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_JOB, extUserVo.getJob()).commit();
		}
	}

	/**
	 * 设置用户名
	 * 
	 * @param username
	 */
	public void setUsername(String username) {
		if (extUserVo != null) {
			extUserVo.setUsername(username);
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USERNAME, extUserVo.getUsername()).commit();
		}
	}

	/**
	 * 设置描述信息
	 * 
	 * @param desc
	 */
	public void setDesc(String desc) {
		if (extUserVo != null) {
			extUserVo.setDescription(desc);
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_DESC, extUserVo.getDescription()).commit();
		}
	}

	/**
	 * 设置玩贴吧时长信息
	 * 
	 * @param gameTime
	 */
	public void setGameTime(String gameTime) {
		if (extUserVo != null) {
			extUserVo.setGameTime(gameTime);
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMETIME, extUserVo.getGameTime()).commit();
		}
	}

	/**
	 * 设置钟情贴吧信息
	 * 
	 * @param likeGameType
	 */
	public void setLikeGameType(String likeGameType) {
		if (extUserVo != null) {
			extUserVo.setLikeGameType(likeGameType);
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_LIKEGAMETYPE, extUserVo.getLikeGameType()).commit();
		}
	}

	/**
	 * 设置等级
	 * 
	 * @param likeGameType
	 */
	public void setGrade(int grade) {
		if (extUserVo != null) {
			extUserVo.setGrade(grade);
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRADE, extUserVo.getGrade()).commit();
		}
	}

	/**
	 * 设置积分
	 * 
	 * @param likeGameType
	 */
	public void setPoint(int point) {
		if (extUserVo != null) {
			extUserVo.setPoint(point);
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POINT, extUserVo.getPoint()).commit();
		}
	}

	/**
	 * 获取用户的积分
	 * 
	 * @return
	 */
	public int getPoint() {
		return getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POINT, 0);
	}

	/**
	 * 设置经验值
	 * 
	 * @param likeGameType
	 */
	public void setExp(int point) {
		if (extUserVo != null) {
			extUserVo.setPoint(point);
			getSharedPreferences().edit().putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_EXP, extUserVo.getPoint()).commit();
		}
	}

	/**
	 * 获取用户的经验值
	 * 
	 * @return
	 */
	public int getExp() {
		return getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_EXP, 0);
	}

	/**
	 * 获取当前登录用户的相册数量
	 * 
	 * @return
	 */
	public int getAlbumCount() {
		return getSharedPreferences().getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ALBUM_COUNT, 0);
	}

	/**
	 * 获取消息提醒用户全局配置
	 * 
	 * @return
	 */
	public boolean getMsgGlobalOffOn() {
		return getUserSharedPreferences().getBoolean(SystemConfig.SYS_SETTING_MSG_GLOBAL_OFFON, true);
	}

	/**
	 * 设置消息提醒用户全局配置
	 * 
	 * @param open
	 */
	public void setMsgGlobalOffOn(boolean open) {
		getUserSharedPreferences().edit().putBoolean(SystemConfig.SYS_SETTING_MSG_GLOBAL_OFFON, open).commit();
	}

	/**
	 * 获取聊天提醒配置
	 * 
	 * @return
	 */
	public boolean getMsgChatOffOn() {
		return getUserSharedPreferences().getBoolean(SystemConfig.SYS_SETTING_MSG_CHAT_OFFON, true);
	}

	/**
	 * 设置聊天提醒配置
	 * 
	 * @param open
	 */
	public void setMsgChatOffOn(boolean open) {
		getUserSharedPreferences().edit().putBoolean(SystemConfig.SYS_SETTING_MSG_CHAT_OFFON, open).commit();
	}

	/**
	 * 在消息页面设置 精彩推荐
	 * 
	 * @param open
	 */
	public void setWonderfullOffOn(boolean open) {
		getUserSharedPreferences().edit().putBoolean(SystemConfig.SYS_SETTING_WONDERFUll_OFFON, open).commit();
	}

	/**
	 * 获取是否打开 精彩推荐的布尔变量值
	 * 
	 * @return
	 */
	public boolean getWonderfullOffOn() {
		return getUserSharedPreferences().getBoolean(SystemConfig.SYS_SETTING_WONDERFUll_OFFON, true);
	}

	/**
	 * 在消息提醒页面设置 精彩推荐是否关闭
	 */
	public void setFateRecommendOffOn(boolean open) {
		getUserSharedPreferences().edit().putBoolean(SystemConfig.SYS_SETTING_FATE_OFFON, open).commit();
	}

	/**
	 * 获取是否打开精彩 推荐里面的缘分好友的布尔值变量
	 * 
	 * @return
	 */
	public boolean getFateRecommendOffOn() {
		return getUserSharedPreferences().getBoolean(SystemConfig.SYS_SETTING_FATE_OFFON, true);
	}

	/**
	 * 获取贴吧提醒提醒配置
	 * 
	 * @return
	 */
	public boolean getGameNewsOffOn() {
		return getUserSharedPreferences().getBoolean(SystemConfig.SYS_SETTING_NEWS_GAME_OFFON, true);
	}

	/**
	 * 设置贴吧提醒提醒配置
	 * 
	 * @param open
	 */
	public void setGameNewsOffOn(boolean open) {
		getUserSharedPreferences().edit().putBoolean(SystemConfig.SYS_SETTING_NEWS_GAME_OFFON, open).commit();
	}

	/**
	 * 获取评论我的提醒配置
	 * 
	 * @return
	 */
	public boolean getCommentMyOffOn() {
		return getUserSharedPreferences().getBoolean(SystemConfig.SYS_SETTING_COMMENT_MY_OFFON, true);
	}

	/**
	 * 设置评论我的提醒配置
	 * 
	 * @param open
	 */
	public void setCommentMyOffOn(boolean open) {
		getUserSharedPreferences().edit().putBoolean(SystemConfig.SYS_SETTING_COMMENT_MY_OFFON, open).commit();
	}

	/**
	 * 获取好友动态提醒配置
	 * 
	 * @return
	 */
	public boolean getNewsFriendOffOn() {
		return getUserSharedPreferences().getBoolean(SystemConfig.SYS_SETTING_NEWS_FRIEND_OFFON, true);
	}

	/**
	 * 设置好友动态提醒配置
	 * 
	 * @param open
	 */
	public void setNewsFriendOffOn(boolean open) {
		getUserSharedPreferences().edit().putBoolean(SystemConfig.SYS_SETTING_NEWS_FRIEND_OFFON, open).commit();
	}

	/**
	 * 获取声音的总开关
	 * 
	 * @return
	 */
	public boolean getSoundOffOn() {
		return getSharedPreferences().getBoolean(SystemConfig.SYS_SETTING_SOUND_OFFON, true);
	}

	/**
	 * 设置声音的总开关
	 * 
	 * @param open
	 */
	public void setSoundOffOn(boolean open) {
		getSharedPreferences().edit().putBoolean(SystemConfig.SYS_SETTING_SOUND_OFFON, open).commit();
	}

	/**
	 * 获取全局HTTP地址配置
	 * 
	 * @return
	 */
	public String getHttpIP() {
		return getSharedPreferences().getString(SystemConfig.HTTP_IP, "0.0.0.0");
	}

	/**
	 * 设置全局HTTP地址配置
	 * 
	 * @param open
	 */
	public void setHttpIP(String ip) {
		getSharedPreferences().edit().putString(SystemConfig.HTTP_IP, ip).commit();
	}

	/**
	 * 获取全局HTTP地址配置
	 * 
	 * @return
	 */
	public int getHttpPort() {
		return getSharedPreferences().getInt(SystemConfig.HTTP_PORT, 8000);
	}

	/**
	 * 设置全局TCP地址配置
	 * 
	 * @param open
	 */
	public void setHttpPort(int port) {
		getSharedPreferences().edit().putInt(SystemConfig.HTTP_PORT, port).commit();
	}

	/**
	 * 获取全局登陆HTTP地址配置
	 * 
	 * @return
	 */
	public String getLoginHttpIP() {
		return getSharedPreferences().getString(SystemConfig.HTTP_LOGIN_IP, "0.0.0.0");
	}

	/**
	 * 设置全局登陆HTTP地址配置
	 * 
	 * @param open
	 */
	public void setLoginHttpIP(String ip) {
		getSharedPreferences().edit().putString(SystemConfig.HTTP_LOGIN_IP, ip).commit();
	}

	/**
	 * 获取全局登陆HTTP地址配置
	 * 
	 * @return
	 */
	public int getLoginHttpPort() {
		return getSharedPreferences().getInt(SystemConfig.HTTP_LOGIN_PORT, 8000);
	}

	/**
	 * 设置全局登陆HTTP地址配置
	 * 
	 * @param port
	 */
	public void setLoginHttpPort(int port) {
		getSharedPreferences().edit().putInt(SystemConfig.HTTP_LOGIN_PORT, port).commit();
	}

	/**
	 * 获取全局TCP地址配置
	 * 
	 * @return
	 */
	public String getTcpIP() {
		return getSharedPreferences().getString(SystemConfig.TCP_IP, "0.0.0.0");
	}

	/**
	 * 设置全局TCP地址配置
	 * 
	 * @param open
	 */
	public void setTcpIP(String ip) {
		getSharedPreferences().edit().putString(SystemConfig.TCP_IP, ip).commit();
	}

	/**
	 * 获取全局TCP地址配置
	 * 
	 * @return
	 */
	public int getTcpPort() {
		return getSharedPreferences().getInt(SystemConfig.TCP_PORT, 8000);
	}

	/**
	 * 设置全局TCP地址配置
	 * 
	 * @param open
	 */
	public void setTcpPort(int port) {
		getSharedPreferences().edit().putInt(SystemConfig.TCP_PORT, port).commit();
	}

	/**
	 * 设置全局资源地址配置
	 * 
	 * @param open
	 */
	public void setResIP(String ip) {
		getSharedPreferences().edit().putString(SystemConfig.RES_URL, ip).commit();
	}

	/**
	 * 获取全局资源地址配置
	 * 
	 * @return
	 */
	public String getResIP() {
		return getSharedPreferences().getString(SystemConfig.RES_URL, "0.0.0.0");
	}

	/**
	 * 设置模板地址到偏好设置中
	 * 
	 * @param templateDir
	 */
	public void setTemplateDirIp(String templateDir) {
		getSharedPreferences().edit().putString(SystemConfig.TEMPLATE_URL, templateDir).commit();
	}

	/**
	 * 获取模板地址的ip
	 */
	public String getTemplateDirIp() {
		return getSharedPreferences().getString(SystemConfig.TEMPLATE_URL, "0.0.0.0");
	}

	/**
	 * @return
	 */
	public int getAM() {
		return getSharedPreferences().getInt(SystemConfig.AM, 10);
	}

	/**
	 * @param am
	 */
	public void setAM(int am) {
		getSharedPreferences().edit().putInt(SystemConfig.AM, am).commit();
	}

	/**
	 * @return
	 */
	public int getMM() {
		return getSharedPreferences().getInt(SystemConfig.MM, 5);
	}

	/**
	 * @param mm
	 */
	public void setMM(int mm) {
		getSharedPreferences().edit().putInt(SystemConfig.MM, mm).commit();
	}

	/**
	 * @return
	 */
	public int getJGM() {
		if (extUserVo != null) {
			UserGradeVo vo = UserUtil.getUserGradeConfigByGrade(userGradeConfig, extUserVo.getGrade());
			return vo == null ? 5 : vo.getJoingroup();
		} else {
			return 5;
		}
	}

	/**
	 * @return
	 */
	public int getGCM() {
		if (extUserVo != null) {
			UserGradeVo vo = UserUtil.getUserGradeConfigByGrade(userGradeConfig, extUserVo.getGrade());
			return vo == null ? 1 : vo.getCreategroup();
		} else {
			return 1;
		}
	}

	/**
	 * @return
	 */
	public int getGAM() {
		return getSharedPreferences().getInt(SystemConfig.GAM, 3);
	}

	/**
	 * @param jam
	 */
	public void setGAM(int gam) {
		getSharedPreferences().edit().putInt(SystemConfig.GAM, gam).commit();
	}

	/**
	 * @return
	 */
	public int getPAM() {
		return getSharedPreferences().getInt(SystemConfig.PAM, 1);
	}

	/**
	 * @param jam
	 */
	public void setPAM(int pam) {
		getSharedPreferences().edit().putInt(SystemConfig.PAM, pam).commit();
	}

	/**
	 * @return
	 */
	public int getPIT() {
		return getSharedPreferences().getInt(SystemConfig.PIT, 15);
	}

	/**
	 * @param pit
	 */
	public void setPIT(int pit) {
		getSharedPreferences().edit().putInt(SystemConfig.PIT, pit).commit();
	}

	/**
	 * @return
	 */
	public int getGCRN() {
		return getSharedPreferences().getInt(SystemConfig.GCRN, 5);
	}

	/**
	 * @param gcrn
	 */
	public void setGCRN(int gcrn) {
		getSharedPreferences().edit().putInt(SystemConfig.GCRN, gcrn).commit();
	}

	/**
	 * @return
	 */
	public int getGCRG() {
		return getSharedPreferences().getInt(SystemConfig.GCRG, 10);
	}

	/**
	 * @param gcrg
	 */
	public void setGCRG(int gcrg) {
		getSharedPreferences().edit().putInt(SystemConfig.GCRG, gcrg).commit();
	}

	/**
	 * @return
	 */
	public int getGCRT() {
		return getSharedPreferences().getInt(SystemConfig.GCRT, 30000);
	}

	/**
	 * @param gcrt
	 */
	public void setGCRT(int gcrt) {
		getSharedPreferences().edit().putInt(SystemConfig.GCRT, gcrt).commit();
	}

	/**
	 * @return
	 */
	public int getPTID() {
		return getSharedPreferences().getInt(SystemConfig.PTID, 21600000);
	}

	/**
	 * @param ptid
	 */
	public void setPTID(int ptid) {
		getSharedPreferences().edit().putInt(SystemConfig.PTID, ptid).commit();
	}

	/**
	 * @return
	 */
	public int getMSGM() {
		return getSharedPreferences().getInt(SystemConfig.MSGM, 2000);
	}

	/**
	 * @param msgm
	 */
	public void setMSGM(int msgm) {
		getSharedPreferences().edit().putInt(SystemConfig.MSGM, msgm).commit();
	}

	/**
	 * 获取赞踩已经提示次数
	 * 
	 * @return
	 */
	public int getGamePraiseTipCount() {
		return getSharedPreferences().getInt(SystemConfig.GAME_PRAISE_TIP_COUNT, 0);
	}

	/**
	 * 设置赞踩已经提示次数
	 * 
	 * @param count
	 */
	public void setGamePraiseTipCount(int count) {
		getSharedPreferences().edit().putInt(SystemConfig.GAME_PRAISE_TIP_COUNT, count).commit();
	}

	/**
	 * 获取赞踩提示次数
	 * 
	 * @return
	 */
	public int getGamePraiseMaxTipCount() {
		return getSharedPreferences().getInt(SystemConfig.GAME_PRAISE_MAX_TIP_COUNT, 10);
	}

	/**
	 * 设置赞踩提示次数
	 * 
	 * @param count
	 */
	public void setGamePraiseMaxTipCount(int count) {
		getSharedPreferences().edit().putInt(SystemConfig.GAME_PRAISE_MAX_TIP_COUNT, count).commit();
	}

	/**
	 * 获取全局距离配置
	 * 
	 * @return
	 */
	public int getNearDistance() {
		return getSharedPreferences().getInt(SystemConfig.NEAR_DISTANCE, 2000000);
	}

	/**
	 * 设置全局TCP地址配置
	 * 
	 * @param open
	 */
	public void setNearDistance(int distance) {
		getSharedPreferences().edit().putInt(SystemConfig.NEAR_DISTANCE, distance).commit();
	}

	/**
	 * @return the mContext
	 */
	public Context getContext() {
		return context;
	}

	/******************************** 贴吧筛选 ********************************/
	/**
	 * 获取贴吧发现选项索引 0全部1单机2网游
	 * 
	 * @return
	 */
	public int getDiscoverGameType() {
		return getUserSharedPreferences().getInt("discover_gametype_selectindex", 0);
	}

	/**
	 * 获取贴吧发现选项索引
	 * 
	 * @param index
	 */
	public void setDiscoverGameType(int index) {
		getUserSharedPreferences().edit().putInt("discover_gametype_selectindex", index).commit();
	}

	/**
	 * 获取发现用户性别选项索引 0全部1男2女
	 * 
	 * @return
	 */
	public int getDiscoverUserSex() {
		return getUserSharedPreferences().getInt("discover_user_sex_selectindex", 0);
	}

	/**
	 * 设计发现用户性别选项索引
	 * 
	 * @param index
	 */
	public void setDiscoverUserSex(int index) {
		getUserSharedPreferences().edit().putInt("discover_user_sex_selectindex", index).commit();
	}

	/**
	 * 获取发现用户时间选项索引 0三天以内1 15分钟 2 60分钟 3 一天以内
	 * 
	 * @return
	 */
	public int getDiscoverTime() {
		return getUserSharedPreferences().getInt("discover_time_selectindex", 3);
	}

	/**
	 * 设计发现用户时间选项索引
	 * 
	 * @param index
	 */
	public void setDiscoverTime(int index) {
		getUserSharedPreferences().edit().putInt("discover_time_selectindex", index).commit();
	}

	/**
	 * 获取发现公会验证选项索引 0全部1无验证2有验证
	 * 
	 * @return
	 */
	public int getDiscoverVerify() {
		return getUserSharedPreferences().getInt("discover_verify_selectindex", 0);
	}

	/**
	 * 设计发现公会验证选项索引
	 * 
	 * @param index
	 */
	public void setDiscoverVerify(int index) {
		getUserSharedPreferences().edit().putInt("discover_verify_selectindex", index).commit();
	}

	/**
	 * 获取发现贴吧（贴吧ID用户“,”隔开）
	 * 
	 * @return
	 */
	public String getDiscoverGame() {
		return getUserSharedPreferences().getString("discover_game", null);
	}

	/**
	 * 设置发现贴吧
	 * 
	 * @param index
	 */
	public void setDiscoverGame(String gids) {
		getUserSharedPreferences().edit().putString("discover_game", gids).commit();
	}

	/**
	 * 获取发现永辉（服务器ID用户“,”隔开）
	 * 
	 * @return
	 */
	public String getDiscoverUserService() {
		UserGameServerResult ugsResult = SystemContext.getInstance().getGameServices();
		if (ugsResult != null && ugsResult.getEntryList() != null && ugsResult.getEntryList().size() > 0) {
			return getUserSharedPreferences().getString("discover_user_service", null);
		} else {
			setDiscoverGroupService(null);
			return null;
		}
	}

	/**
	 * 设置发现用户服务器
	 * 
	 * @param index
	 */
	public void setDiscoverUserService(String sids) {
		getUserSharedPreferences().edit().putString("discover_user_service", sids).commit();
	}

	/**
	 * 获取发现公会（服务器ID用户“,”隔开）
	 * 
	 * @return
	 */
	public String getDiscoverGroupService() {
		UserGameServerResult ugsResult = SystemContext.getInstance().getGameServices();
		if (ugsResult != null && ugsResult.getEntryList() != null && ugsResult.getEntryList().size() > 0) {
			return getUserSharedPreferences().getString("discover_group_service", null);
		} else {
			setDiscoverGroupService(null);
			return null;
		}
	}

	/**
	 * 设置发现公会服务器
	 * 
	 * @param index
	 */
	public void setDiscoverGroupService(String sids) {
		getUserSharedPreferences().edit().putString("discover_group_service", sids).commit();
	}

	/**
	 * 获取发现公会人数选项索引 0不限1 100以内2 200以内 3 200以上
	 * 
	 * @return
	 */
	public int getDiscoverUserCount() {
		return getUserSharedPreferences().getInt("discover_group_usercount_selectindex", 0);
	}

	/**
	 * 获取发现贴吧平台 0全部1PC端贴吧2网页贴吧3手机贴吧4电视贴吧5掌上贴吧
	 * 
	 * @return
	 */
	public void setDiscoverGamePlatform(int platform) {
		getUserSharedPreferences().edit().putInt("discover_game_platform_selectindex", platform).commit();
	}

	/**
	 * 获取发现贴吧平台 0全部1PC端贴吧2网页贴吧3手机贴吧4电视贴吧5掌上贴吧
	 * 
	 * @return
	 */
	public int getDiscoverGamePlatform() {
		return getUserSharedPreferences().getInt("discover_game_platform_selectindex", 0);
	}

	/**
	 * 设计发现公会人数选项索引
	 * 
	 * @param index
	 */
	public void setDiscoverUserCount(int index) {
		getUserSharedPreferences().edit().putInt("discover_group_usercount_selectindex", index).commit();
	}
	
	/**
	 * 存储发现陪玩所选游戏id
	 */
	public void setDiscoverPlayGameIdSelected(long gid){
		getUserSharedPreferences().edit().putLong("discover_play_platform_gid", gid).commit();
	}
	
	
	/**
	 * 获取发现陪玩所选游戏id
	 */
	public long getDiscoverPlayGameIdSelected(){
		return getUserSharedPreferences().getLong("discover_play_platform_gid", 0);
	}
	
	
	/**
	 * 存储发现陪玩所选游戏服务器id
	 */
	public void setDiscoverPlayGameServerIdSelected(long serverId){
		getUserSharedPreferences().edit().putLong("discover_play_platform_serverid", serverId).commit();
	}
	
	public long getDiscoverPlayGameServerIdSelected(){
		return getUserSharedPreferences().getLong("discover_play_platform_serverid", 0);
	}
	
	/**
	 * 存储发现陪玩tab1名称
	 */
	public void setDiscoverPlayGameTabTextOne(String textOne){
		getUserSharedPreferences().edit().putString("discover_play_platform_tab_one", textOne).commit();
	}
	
	public String getDiscoverPlayGameTabTextOne(){
		return getUserSharedPreferences().getString("discover_play_platform_tab_one", "游戏");
	}
	
	/**
	 * 存储发现陪玩tab2名称
	 */
	public void setDiscoverPlayGameTabTextTwo(String textTwo){
		getUserSharedPreferences().edit().putString("discover_play_platform_tab_two", textTwo).commit();
	}
	
	public String getDiscoverPlayGameTabTextTwo(){
		return getUserSharedPreferences().getString("discover_play_platform_tab_two", "默认");
	}
	
	/**
	 * 存储发现陪玩所选游戏段位keyid
	 */
	public void setDiscoverPlayGameLeverKeyIdSelected(String keyid){
		getUserSharedPreferences().edit().putString("discover_play_platform_keyid", keyid).commit();
	}
	
	/**
	 * 获取发现陪玩所选游戏段位keyid
	 */
	public String getDiscoverPlayGameLeverKeyIdSelected(){
		return getUserSharedPreferences().getString("discover_play_platform_keyid", null);
	}
	
	/**
	 * 存储发现陪玩所选游戏段位keyval
	 */
	public void setDiscoverPlayGameLeverKeyValueSelected(String keyval){
		getUserSharedPreferences().edit().putString("discover_play_platform_keyval", keyval).commit();
	}
	
	/**
	 * 获取发现陪玩所选游戏段位keyval
	 */
	public String getDiscoverPlayGameLeverKeyValueSelected(){
		return getUserSharedPreferences().getString("discover_play_platform_keyval", null);
	}
	
	
	/**
	 * 存储发现陪玩所选游戏排序 index
	 */
	public void setDiscoverPlayIntelligentSelected(int  index){
		getUserSharedPreferences().edit().putInt("discover_play_platform_intelligent", index).commit();
	}
	
	/**
	 * 获取发现陪玩所选游戏排序 index 0默认
	 */
	public int getDiscoverPlayIntelligentSelected(){
		return getUserSharedPreferences().getInt("discover_play_platform_intelligent",0);
	}
	
	/**
	 * 存储发现陪玩性别 index
	 */
	public void setDiscoverPlaySexSelected(int  index){
		getUserSharedPreferences().edit().putInt("discover_play_platform_sex", index).commit();
	}
	
	/**
	 * 获取发现陪玩性别 index 0全部，1男，2女
	 */
	public int getDiscoverPlaySexSelected(){
		return getUserSharedPreferences().getInt("discover_play_platform_sex", 0);
	}
	
	

	/**
	 * 引导数据存储共享文件
	 * 
	 */
	public boolean getGuideDiscover() {
		return getUserSharedPreferences().getBoolean("guideDiscover", false);
	}

	public void setGuideDiscover(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guideDiscover", isVisit).commit();
	}

	public boolean getGuidUserFollow() {
		return getUserSharedPreferences().getBoolean("guideUserFollow", false);
	}

	public void setGuidUserFollow(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guideUserFollow", isVisit).commit();
	}

	public boolean getGuidUserGroup() {
		return getUserSharedPreferences().getBoolean("guideUserGroup", false);
	}

	public void setGuidUserGroup(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guideUserGroup", isVisit).commit();
	}

	public boolean getGuidGame() {
		return getUserSharedPreferences().getBoolean("guideGame", false);
	}

	public void setGuidGame(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guideGame", isVisit).commit();
	}

	public boolean getGuidGameDetial() {
		return getUserSharedPreferences().getBoolean("guideGameDetail", false);
	}

	public void setGuidGameDetial(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guideGameDetail", isVisit).commit();
	}

	public boolean getGuidChatGroup() {
		return getUserSharedPreferences().getBoolean("guidChatGroup", false);
	}

	public void setGuidChatGroup(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidChatGroup", isVisit).commit();
	}

	public boolean getGuidFollowContact() {
		return getUserSharedPreferences().getBoolean("guidFollowContact", false);
	}

	public void setGuidFollowContact(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidFollowContact", isVisit).commit();
	}

	public boolean getGuidInivteContact() {
		return getUserSharedPreferences().getBoolean("guidInviteContact", false);
	}

	public void setGuidInivteContact(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidInviteContact", isVisit).commit();
	}

	public boolean getGuidMessage() {
		return getUserSharedPreferences().getBoolean("guidMessage", false);
	}

	public void setGuidMessage(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidMessage", isVisit).commit();
	}

	public boolean getGuidPost() {
		return getUserSharedPreferences().getBoolean("guidPost", false);
	}

	public void setGuidPost(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidPost", isVisit).commit();
	}

	public boolean getGuidPostShare() {
		return getUserSharedPreferences().getBoolean("guidPostShare", false);
	}

	public void setGuidPostShare(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidPostShare", isVisit).commit();
	}

	public boolean getGuidUserDetail() {
		return getUserSharedPreferences().getBoolean("guidUserDetail", false);
	}

	public void setGuidUserDetail(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidUserDetail", isVisit).commit();
	}

	public boolean getGuidWeiboFollow() {
		return getUserSharedPreferences().getBoolean("guidWeiboFollow", false);
	}

	public void setGuidWeiboFollow(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidWeiboFollow", isVisit).commit();
	}

	public boolean getGuidWeiboInivte() {
		return getUserSharedPreferences().getBoolean("guidWeiboInvite", false);
	}

	public void setGuidWeiboInivte(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidWeiboInvite", isVisit).commit();
	}

	public boolean getGuidGroupPoint() {
		return getUserSharedPreferences().getBoolean("guidGroupPoint", false);
	}

	public void setGuidGroupPoint(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidGroupPoint", isVisit).commit();
	}

	public boolean getGuidPointTask() {
		return getUserSharedPreferences().getBoolean("guidPointTask", false);
	}

	public void setGuidPointTask(boolean isVisit) {
		getUserSharedPreferences().edit().putBoolean("guidPointTask", isVisit).commit();
	}

	public String getWordsMD5() {
		return getSharedPreferences().getString("wordsMD5", null);
	}

	public void setWordsMD5(String md5) {
		getSharedPreferences().edit().putString("wordsMD5", md5).commit();
	}

	public String getNameWordsMD5() {
		return getSharedPreferences().getString("nameWordsMD5", null);
	}

	public void setNameWordsMD5(String namemd5) {
		getSharedPreferences().edit().putString("nameWordsMD5", namemd5).commit();
	}

	public String getRecommendGames() {
		return getUserSharedPreferences().getString("recommendGames", null);
	}

	public void setRecommendGames(String games) {
		getUserSharedPreferences().edit().putString("recommendGames", games).commit();
	}

	public String getRecommendGroups() {
		return getUserSharedPreferences().getString("recommendGroups", null);
	}

	public void setRecommendGroups(String groups) {
		getUserSharedPreferences().edit().putString("recommendGroups", groups).commit();
	}

	public void setRecommendGameTag(boolean needRecommendGame) {
		recommendGameTag = needRecommendGame;
	}

	public boolean getRecommendGameTag() {
		return recommendGameTag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SystemContext [extUserVo=" + extUserVo + ", version=" + version + ", token=" + token + ", account=" + account + ", context="
				+ context + ", mSharedPreferences=" + mSharedPreferences + ", mUserSharedPreferences=" + mUserSharedPreferences + ", openMessage="
				+ openMessage + ", userSyncMode0Key=" + userSyncMode0Key + ", userSyncMode1Key=" + userSyncMode1Key + ", userSyncMode2Key="
				+ userSyncMode2Key + ", gameSyncKey=" + gameSyncKey + ", gamePackSyncKey=" + gamePackSyncKey + ", relationGameSyncKey="
				+ relationGameSyncKey + ", bdLocation=" + bdLocation + ", currentTimeInMillis=" + currentTimeInMillis + ", lastTopicPublishTime="
				+ lastTopicPublishTime + ", isStopOverMain=" + isStopOverMain + "]";
	}

	/**
	 * 百度位置信息对象
	 */
	private BDLocation bdLocation = null;

	/**
	 * 设置百度位置信息
	 * 
	 * @param bdLocation
	 */
	public void setLocation(BDLocation bdLocation) {
		this.bdLocation = bdLocation;
	}

	public BDLocation getBDLocation() {
		return this.bdLocation;
	}

	/**
	 * 返回格式为“lon，lat”的地址位置信息
	 * 
	 * @return
	 */
	public String getLocation() {

		double local_lng = 0;
		double local_lat = 0;
		if (bdLocation != null) {
			if ((bdLocation.getLongitude() == 4.9E-324D || bdLocation.getLatitude() == 4.9E-324D)) {
				local_lng = 0;
				local_lat = 0;
			} else {
				local_lng = bdLocation.getLongitude();
				local_lat = bdLocation.getLatitude();
			}
		}
		String pos = String.format("%f,%f", local_lng, local_lat);
		return pos;
	}

	/**
	 * 返回Longitude
	 */
	public String getLocationLongitude() {
		double local_lng = 0;
		if (bdLocation != null) {
			if ((bdLocation.getLongitude() == 4.9E-324D || bdLocation.getLatitude() == 4.9E-324D)) {
				local_lng = 0;
			} else {
				local_lng = bdLocation.getLongitude();
			}
		}
		String pos = String.format("%f", local_lng);
		return pos;
	}

	/**
	 * Latitude
	 * 
	 * @return
	 */
	public String getLocationLatitude() {
		double local_lat = 0;
		if (bdLocation != null) {
			if ((bdLocation.getLongitude() == 4.9E-324D || bdLocation.getLatitude() == 4.9E-324D)) {
				local_lat = 0;
			} else {
				local_lat = bdLocation.getLatitude();
			}
		}
		String pos = String.format("%f", local_lat);
		return pos;

	}

	/**
	 * 当前时间，服务器返回的
	 */
	private long currentTimeInMillis = 0;

	public void setCurrentTimeInMillis(long date) {
		currentTimeInMillis = date;
	}

	public long getCurrentTimeInMillis() {
		if (currentTimeInMillis == 0) {
			Calendar ca = Calendar.getInstance();
			long tmpnow = ca.getTimeInMillis();
			return tmpnow;
		} else {
			return currentTimeInMillis;
		}
	}

	/**
	 * 发布最后一条帖子的时间
	 */
	private long lastTopicPublishTime = 0;

	/**
	 * @return the lastTopicPublishTime
	 */
	public long getLastTopicPublishTime() {
		return lastTopicPublishTime;
	}

	/**
	 * @param lastTopicPublishTime
	 *            the lastTopicPublishTime to set
	 */
	public void setLastTopicPublishTime(long lastTopicPublishTime) {
		this.lastTopicPublishTime = lastTopicPublishTime;
	}

	public boolean isOpenMessage() {
		return openMessage;
	}

	public void setOpenMessage(boolean openMessage) {
		this.openMessage = openMessage;
	}

	/**
	 * 是否停留在首页，默认false
	 */
	private boolean isStopOverMain = false;

	/**
	 * 获得是否停留在首页
	 * 
	 * @return the isStopOverMain
	 */
	public boolean isStopOverMain() {
		return isStopOverMain;
	}

	/**
	 * 设置是否停留在首页，默认false
	 * 
	 * @param isStopOverMain
	 *            the isStopOverMain to set
	 */
	public void setStopOverMain(boolean isStopOverMain) {
		this.isStopOverMain = isStopOverMain;
	}

	/**
	 * 获取上一次采集通讯录的时间
	 */
	public long getCollectCtInfoTime() {
		return getSharedPreferences().getLong(SystemConfig.COLLECT_CONTACT_INFO_KEY, 0);
	}

	/**
	 * 设置上一次采集通讯录的时间
	 */
	public void setCollectCtInfoTime(long time) {
		getSharedPreferences().edit().putLong(SystemConfig.COLLECT_CONTACT_INFO_KEY, time).commit();
	}

	/**
	 * 获取上一次采集通讯录的时间
	 */
	public long getLoginTime() {
		return getSharedPreferences().getLong(SystemConfig.LOGIN_TIME_KEY, 0);
	}

	/**
	 * 设置上一次采集通讯录的时间
	 */
	public void setLoginTime(long time) {
		getSharedPreferences().edit().putLong(SystemConfig.LOGIN_TIME_KEY, time).commit();
	}

	/**
	 * 获得用户配置文件中，公会公告是否显示，默认显示 true
	 * 
	 * @return
	 */
	public boolean getUserSharedPreferencesGroupAnnounceIsShow(long grid) {
		String key = "group_announce_isshow_" + String.valueOf(grid);
		return getUserSharedPreferences().getBoolean(key, true);
	}

	/**
	 * 设置用户配置文件中，公会公告是否显示
	 */
	public void setUserSharedPreferencesGroupAnnounceIsShow(long grid, boolean flag) {
		String key = "group_announce_isshow_" + String.valueOf(grid);
		getUserSharedPreferences().edit().putBoolean(key, flag).commit();
	}

	/**
	 * 获得用户配置文件中,公会的最后一次公告的内容
	 * 
	 * @param gid
	 * @return
	 */
	public String getUserSharedPreferencesLastGroupAnnounce(long grid) {
		String key = "group_announce_lastcontent_" + String.valueOf(grid);
		return getUserSharedPreferences().getString(key, "");
	}

	/**
	 * 设置用户配置文件中,公会的最后一次公告的内容
	 * 
	 * @param gid
	 * @param content
	 */
	public void setUserSharedPreferencesLastGroupAnnounce(long grid, String content) {
		String key = "group_announce_lastcontent_" + String.valueOf(grid);
		getUserSharedPreferences().edit().putString(key, content).commit();
	}

	/**
	 * @return
	 */
	public String getSharePersonBaseUrl() {
		return getSharedPreferences().getString("sharePersionBaseUrl", "http://192.168.10.180/shareperson/");
	}

	/**
	 * @param baseurl
	 */
	public void setSharePersonBaseUrl(String baseurl) {
		getSharedPreferences().edit().putString("sharePersionBaseUrl", baseurl).commit();
	}

	/**
	 * @return
	 */
	public String getSharePostBaseUrl() {
		return getSharedPreferences().getString("sharePostBaseUrl", "http://192.168.10.180/sharetieba/");
	}

	/**
	 * @param baseurl
	 */
	public void setSharePostBaseUrl(String baseurl) {
		getSharedPreferences().edit().putString("sharePostBaseUrl", baseurl).commit();
	}

	/**
	 * 频道展示规则文件的md5
	 * */
	private String ChannelConfigMD5;

	/**
	 * @return the channelConfigMD5
	 */
	public String getChannelConfigMD5() {
		return (ChannelConfigMD5 == null || ChannelConfigMD5.isEmpty()) ? getSharedPreferences().getString("ChannelConfigMD5", "") : ChannelConfigMD5;
	}

	/**
	 * @param channelConfigMD5
	 *            the channelConfigMD5 to set
	 */
	public void setChannelConfigMD5(String channelConfigMD5) {
		ChannelConfigMD5 = channelConfigMD5;
		getSharedPreferences().edit().putString("ChannelConfigMD5", ChannelConfigMD5).commit();

	}

	/**
	 * 判断展示规则文件的Url
	 */
	public String ChannelConfigUrl;

	/**
	 * @return the channelConfigUrl
	 */
	public String getChannelConfigUrl() {
		return (ChannelConfigUrl == null || ChannelConfigUrl.isEmpty()) ? getSharedPreferences().getString("ChannelConfigUrl", "") : ChannelConfigUrl;
	}

	/**
	 * @param channelConfigUrl
	 *            the channelConfigUrl to set
	 */
	public void setChannelConfigUrl(String channelConfigUrl) {
		ChannelConfigUrl = channelConfigUrl;
		getSharedPreferences().edit().putString("ChannelConfigUrl", ChannelConfigMD5).commit();

	}

	/**
	 * 频道展示规则对象
	 */
	private ChannelVo showChannelsRuleVo;

	/**
	 * @return the showChannelsRuleVo
	 */
	public ChannelVo getShowChannelsRuleVo() {
		return showChannelsRuleVo;
	}

	/**
	 * @param showChannelsRuleVo
	 *            the showChannelsRuleVo to set
	 */
	public void setShowChannelsRuleVo(ChannelVo showChannelsRuleVo) {
		this.showChannelsRuleVo = showChannelsRuleVo;
	}

	/**
	 * 消息的主体选择规则对象
	 */
	private List<MessageSubjectRuleVo> messageSubjectRuleList;

	/**
	 * @return the messageSubjectRuleList
	 */
	public List<MessageSubjectRuleVo> getMessageSubjectRuleList() {
		return messageSubjectRuleList;
	}

	/**
	 * @param messageSubjectRuleList
	 *            the messageSubjectRuleList to set
	 */
	public void setMessageSubjectRuleList(List<MessageSubjectRuleVo> messageSubjectRuleList) {
		this.messageSubjectRuleList = messageSubjectRuleList;
	}

	/**
	 * 频道显示规则
	 */
	private List<Object> channelsShowRule = null;

	/**
	 * @return the channelsShowRule
	 */
	public List<Object> getChannelsShowRule() {
		return channelsShowRule;
	}

	/**
	 * @param channelsShowRule
	 *            the channelsShowRule to set
	 */
	public void setChannelsShowRule(List<Object> channelsShowRule) {
		this.channelsShowRule = channelsShowRule;
	}

	/**
	 * @return
	 */
	public int getFGM() {
		if (extUserVo != null) {
			UserGradeVo vo = UserUtil.getUserGradeConfigByGrade(userGradeConfig, extUserVo.getGrade());
			return vo == null ? 25 : vo.getFollowgame();
		} else {
			return 25;
		}
	}

	/**
	 * @return 每日最大发帖数
	 */
	public int getPTM() {
		if (extUserVo != null) {
			UserGradeVo vo = UserUtil.getUserGradeConfigByGrade(userGradeConfig, extUserVo.getGrade());
			return vo == null ? 5 : vo.getSendpost();
		} else {
			return 5;
		}
	}

	public boolean isUnAuth() {
		return unAuth;
	}

	public void setUnAuth(boolean unAuth) {
		this.unAuth = unAuth;
	}

	public int getConfigLoadedStatus() {
		return configLoadedStatus;
	}

	public void setConfigLoadedStatus(int configLoadedStatus) {
		this.configLoadedStatus = configLoadedStatus;
	}

	/**
	 * 消息 正在同步数据的频道，key 为mcode
	 */
	private Map<String, Integer> channelSyncing = new ConcurrentHashMap<String, Integer>();

	/**
	 * 消息 丢弃过通知消息的频道
	 */
	private Map<String, Integer> channelDiscard = new ConcurrentHashMap<String, Integer>();

	/**
	 * 消息 正在同步数据的频道，key 为mcode
	 * 
	 * @return the channelSyncing
	 */
	public Map<String, Integer> getChannelSyncing() {
		return channelSyncing;
	}

	/**
	 * 消息 丢弃过通知消息的频道，key 为mcode
	 * 
	 * @return the channelDiscard
	 */
	public Map<String, Integer> getChannelDiscard() {
		return channelDiscard;
	}

	/**
	 * 获得某个关注的贴吧的贴吧已经读到的最大数index
	 * 
	 * @param gameId
	 * @return
	 */
	public long getUserSharedPreferencesPostbarReadMaxIndex(long gameId) {
		String key = "postbar_read_maxIndex_" + gameId;
		return getUserSharedPreferences().getLong(key, 0);
	}

	/**
	 * 设置某个关注的贴吧的贴吧已经读到的最大数
	 * 
	 * @param gameId
	 * @param index
	 */
	public void setUserSharedPreferencesPostbarReadMaxIndex(long gameId, long index) {
		String key = "postbar_read_maxIndex_" + gameId;
		getUserSharedPreferences().edit().putLong(key, index).commit();
	}

	/**
	 * 设置某个主体中上次读到的最大Index
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 */
	public void setSubjectLastUnReadMaxIndex(String channelType, long subjectid, String subjectDomain, String category, long msgindex) {
		String key = "message_unread_last_maxIndex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
		long oldvalue = getUserSharedPreferences().getLong(key, 0L);
		getUserSharedPreferences().edit().putLong(key, Math.max(msgindex, oldvalue)).commit();
	}

	/**
	 * 获得某个主体上次读到的最大Index
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public long getSubjectLastUnReadMaxIndex(String channelType, long subjectid, String subjectDomain, String category) {
		String key = "message_unread_last_maxIndex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
		return getUserSharedPreferences().getLong(key, -1L);
	}

	/**
	 * 获得某个主体未读的最大Index
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public long getSubjectUnReadMaxIndex(String channelType, long subjectid, String subjectDomain, String category) {
		String key = "message_unread_maxIndex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
		return getUserSharedPreferences().getLong(key, 0L);
	}

	/**
	 * 设置某个主体未读的最大Index
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public void setSubjectUnReadMaxIndex(String channelType, long subjectid, String subjectDomain, String category, long msgindex) {
		String key = "message_unread_maxIndex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
		long oldvalue = getUserSharedPreferences().getLong(key, 0L);
		getUserSharedPreferences().edit().putLong(key, Math.max(msgindex, oldvalue)).commit();

	}

	/**
	 * 清空某个主体的未读数
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 */
	public void cleanSubjectUnReadCount(String channelType, long subjectid, String subjectDomain, String category) {
		String key = "message_unread_last_maxIndex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
		String key1 = "message_unread_maxIndex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
		getUserSharedPreferences().edit().putLong(key, -1L).commit();
		getUserSharedPreferences().edit().putLong(key1, 0L).commit();
	}

	/**
	 * 清空一类主体的未读数
	 * 
	 * @param channelType
	 * @param subjectDomain
	 * @param category
	 */
	public void cleanSubjectUnReadCount(String channelType, String subjectDomain, String category) {
		String key_prefix = "message_unread_last_maxIndex_" + channelType + "_" + category + "_" + subjectDomain + "_";
		String key1_prefix = "message_unread_maxIndex_" + channelType + "_" + category + "_" + subjectDomain + "_";
		for (Map.Entry<String, ?> entry : getUserSharedPreferencesAll().entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(key_prefix) || key.startsWith(key1_prefix)) {
				removeUserSharedPreferences(key);
			}
		}
	}

	/**
	 * 获得某个主体能够读到的最小Index
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public long getSubjectcanReadMinIndex(String channelType, long subjectid, String subjectDomain, String category) {
		String key = "message_canread_minIndex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
		return getUserSharedPreferences().getLong(key, -1L);
	}

	/**
	 * 设置某个主体能够读到的最小Index
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @param minIndex
	 */
	public void setSubjectcanReadMinIndex(String channelType, long subjectid, String subjectDomain, String category, long minIndex) {
		String key = "message_canread_minIndex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
		getUserSharedPreferences().edit().putLong(key, minIndex).commit();
	}

	/**
	 * 保存本地最大的index
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @param lastSyncKey
	 */
	public void setSubjectLocalMaxIndex(String channelType, long subjectId, String subjectDomain, String category, long index) {
		String key = "message_local_maxindex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectId;
		long oldlocalmaxindex = getUserSharedPreferences().getLong(key, -1L);
		if (index > oldlocalmaxindex)
			getUserSharedPreferences().edit().putLong(key, index).commit();

	}

	/**
	 * 获得保存在本地最大的index
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public long getSubjectLocalMaxIndex(String channelType, long subjectId, String subjectDomain, String category) {
		String key = "message_local_maxindex_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectId;
		return getUserSharedPreferences().getLong(key, -1L);
	}

	public static final String HAS_UNREAD_MESSAGE_TYPE_PRAISE = "praise";
	public static final String HAS_UNREAD_MESSAGE_TYPE_REPLY = "reply";

	/**
	 * 设置某类消息是否有未读数
	 * 
	 * @param channelType
	 * @param subjectId
	 * @param subjectDomain
	 * @param category
	 * @param type
	 * @param has
	 */
	public void setHasUnreadMessage(String channelType, long subjectId, String subjectDomain, String category, String type, boolean has) {
		String key = "message_local_hasunread_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectId
				+ (type != null ? "_" + type : "");
		getUserSharedPreferences().edit().putBoolean(key, has).commit();
	}

	/**
	 * 获取某类消息是否有未读数
	 * 
	 * @param channelType
	 * @param subjectId
	 * @param subjectDomain
	 * @param category
	 * @param type
	 * @return
	 */
	public boolean getHasUnreadMessage(String channelType, long subjectId, String subjectDomain, String category, String type) {
		String key = "message_local_hasunread_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectId
				+ (type != null ? "_" + type : "");
		return getUserSharedPreferences().getBoolean(key, false);
	}

	/**
	 * 获得用户配置文件中使用的变量
	 * 
	 * @return
	 */
	private Map<String, ?> getUserSharedPreferencesAll() {
		return getUserSharedPreferences().getAll();
	}

	/**
	 * 移除用户配置文件中某个key
	 * 
	 * @param key
	 */
	private void removeUserSharedPreferences(String key) {
		getUserSharedPreferences().edit().remove(key).commit();
	}

	/**
	 * @return
	 */
	public boolean getIsCreatUninstallProcess() {
		return getSharedPreferences().getBoolean("isCreatUninstallPross", false);
	}

	/**
	 * @param baseurl
	 */
	public void setIsCreatUninstallProcess(boolean isinit) {
		getSharedPreferences().edit().putBoolean("isCreatUninstallPross", isinit).commit();
	}

	/**
	 * @return
	 */
	public int getFollowSendMessageTime(long uid) {
		return getUserSharedPreferences().getInt("followSendMessageTime_" + uid, 0);
	}

	/**
	 * @param time
	 */
	public void setFollowSendMessageTime(long uid, int time) {
		getUserSharedPreferences().edit().putInt("followSendMessageTime_" + uid, time).commit();
	}

	/**
	 * 获取用户权限配置
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String, Boolean> getGuestPermissionMap() {
		InputStream abpath = null;
		if (guestPermissionMap == null) {
			try {
				if (guestPermission == null) {
					abpath = getClass().getResourceAsStream("/assets/gp");
					String respString = new String(readStream(abpath));
					JSONObject object = new JSONObject(respString);
					guestPermission = object.getJSONObject("Unbound");
				}
				if (guestPermission.length() > 0) {
					guestPermissionMap = new HashMap<String, Boolean>();
					Iterator it = guestPermission.keys();
					while (it.hasNext()) {
						String key = (String) it.next();
						guestPermissionMap.put(key, guestPermission.getBoolean(key));
					}
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (abpath != null) {
					try {
						abpath.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return guestPermissionMap;
	}

	/**
	 * 设置已推送PUSHID集合
	 * 
	 * @param pushIdString
	 */
	public void setPushIdString(String pushIdString) {
		this.pushIdString = pushIdString;
		if (pushIdString != null) {
			getSharedPreferences().edit().putString(SystemConfig.SHAREDPREFERENCES_NAME_PUSHID_STRING, pushIdString).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_NAME_PUSHID_STRING).commit();
		}
	}

	/**
	 * 获取已推送PUSHID集合
	 * 
	 * @return the guestAccount
	 */
	public String getPushIdString() {
		if (pushIdString != null) {
			return pushIdString;
		} else {
			// 从本地配置文件中读取
			return getGuestSharedPreferences().getString(SystemConfig.SHAREDPREFERENCES_NAME_PUSHID_STRING, null);
		}
	}

	/**
	 * 读取配置文件
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	private byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	/**
	 * 判读是否需要绑定手机
	 * 
	 * @param action
	 * @return
	 */
	public boolean isNeedBundPhone(String action) {
		int isGuest = getIsGuest();
		if (isGuest == 0) {
			return false;
		}
		HashMap<String, Boolean> map = getGuestPermissionMap();
		if (map != null) {
			if (map.containsKey(action) && map.get(action)) {
				return true;
			}
		}
		return false;
	}

	public boolean isHasNewMessage() {
		return hasNewMessage;
	}

	public void setHasNewMessage(boolean hasNewMessage) {
		this.hasNewMessage = hasNewMessage;
	}

	public boolean isReloadTag() {
		return reloadTag;
	}

	public void setReloadTag(boolean reloadTag) {
		this.reloadTag = reloadTag;
	}

	public boolean isHasSendUnAuthInfo() {
		return isHasSendUnAuthInfo;
	}

	public void setHasSendUnAuthInfo(boolean isHasSendUnAuthInfo) {
		this.isHasSendUnAuthInfo = isHasSendUnAuthInfo;
	}

	public boolean isNewOpened() {
		return isNewOpened;
	}

	public void setNewOpened(boolean isNewOpened) {
		this.isNewOpened = isNewOpened;
	}

	public boolean isLoadAppLaunchFromLocal() {
		return isLoadAppLaunchFromLocal;
	}

	public void setLoadAppLaunchFromLocal(boolean isLoadAppLaunchFromLocal) {
		this.isLoadAppLaunchFromLocal = isLoadAppLaunchFromLocal;
	}

	/**
	 * 设置账号
	 * 
	 * @param account
	 */
	public void setLuncharBgLoadTime(Long updateTime) {
		if (updateTime != 0) {
			// 设置本地全局文件(account值)
			getSharedPreferences().edit().putLong(SystemConfig.SHAREDPREFERENCES_LUNCHAR_BG_LOAD_TIME, updateTime).commit();
		} else {
			getSharedPreferences().edit().remove(SystemConfig.SHAREDPREFERENCES_LUNCHAR_BG_LOAD_TIME).commit();
		}
	}

	/**
	 * 获取账号
	 * 
	 * @return the account
	 */
	public long getLuncharBgLoadTime() {
		// 从本地配置文件中读取
		return getSharedPreferences().getLong(SystemConfig.SHAREDPREFERENCES_LUNCHAR_BG_LOAD_TIME, 0);
	}

	/**
	 * 判断是否显示引导图
	 * 
	 * @return
	 */
	public boolean isnotShowGuide() {
		long curTime = System.currentTimeMillis();
		long registTime = SystemContext.getInstance().getExtUserVo().getCreateTime();
		if (curTime - registTime <= THREE_DAY_TIME)
			return true;
		else
			return false;

	}
	
	
	public Activity getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}

	public Msgs.UserGameServerResult getGameServices() {
		return gameServices;
	}

	public void setGameServices(Msgs.UserGameServerResult gameServices) {
		this.gameServices = gameServices;
	}
	
	public int getUserPlayStatus() {
		return userPlayStatus;
	}

	public void setUserPlayStatus(int userPlayStatus) {
		this.userPlayStatus = userPlayStatus;
	}


}
