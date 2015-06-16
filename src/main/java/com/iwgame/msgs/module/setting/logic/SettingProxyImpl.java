/**      
 * SettingProxyImpl.java Create on 2013-8-30     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Xml;

import com.baidu.location.BDLocation;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.AreaDao;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.remote.UserRemoteService;
import com.iwgame.msgs.module.setting.vo.ChannelGroupVo;
import com.iwgame.msgs.module.setting.vo.ChannelVo;
import com.iwgame.msgs.module.setting.vo.MessageSubjectRuleVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PageDataResult;
import com.iwgame.msgs.proto.Msgs.PageDataResult.PageData;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.AreaVo;
import com.iwgame.msgs.vo.local.PageDataVo;
import com.iwgame.msgs.vo.local.ResourceVo;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.HttpUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: SettingProxyImpl
 * @Description: 设置数据代理类
 * @author 王卫
 * @date 2013-8-30 上午10:44:15
 * @Version 1.0
 * 
 */
public class SettingProxyImpl implements SettingProxy {

	private static final String TAG = "SettingProxyImpl";
	private UserRemoteService userService = ServiceFactory.getInstance().getUserRemoteService();
	// 省映射集合
	private List<AreaVo> province;
	// 城市映射集合
	@SuppressLint("UseSparseArrays")
	private Map<Integer, List<AreaVo>> cityMap = new HashMap<Integer, List<AreaVo>>();

	private static byte[] lock = new byte[0];

	private static SettingProxyImpl instance = null;

	private SettingProxyImpl() {

	}

	public static SettingProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new SettingProxyImpl();
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
	 * com.iwgame.msgs.module.setting.logic.SettingProxy#modifyAccountInfo(java
	 * .lang.Object, int)
	 */
	@Override
	public void modifyAccountInfo(final ProxyCallBack<Integer> callback, final Context context, final Integer sex, final String mood, final Integer age, final String job,
			final byte[] avatarData, final String avatar, final String city, final String nickname, final String desc, final String gameTime, final String likeGameType, final String weiboUid,
			final String weiboName) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.updateUser(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, sex, mood, age, job, avatarData, avatar, city, nickname, desc, gameTime, likeGameType, weiboUid, weiboName);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.setting.logic.SettingProxy#modifyAccountAvatar
	 * (com.iwgame.msgs.common.ProxyCallBack, android.content.Context, byte[])
	 */
	@Override
	public void modifyAccountAvatar(final ProxyCallBack<ResourceVo> callback, final Context context, final byte[] avatarData) {
		final MyAsyncTask<ResourceVo> asyncTask = new MyAsyncTask<ResourceVo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.updateUser(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.id)) {
							Msgs.IdResult idr = result.getExtension(Msgs.id);
							ResourceVo vo = new ResourceVo();
							vo.setResourceId(idr.getResourceId());
							asyncTask.getProxyCallBack().onSuccess(vo);
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, null, null, null, null, avatarData, null, null, null, null, null, null, null, null);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.setting.logic.SettingProxy#findAreaByParentid(
	 * com.iwgame.msgs.common.ProxyCallBack, int)
	 */
	@Override
	public void findAreaByParentid(final ProxyCallBack<List<AreaVo>> callback, final int pid) {
		final AreaDao areaDao = DaoFactory.getDaoFactory().getAreaDao(SystemContext.getInstance().getContext());
		if (!cityMap.containsKey(pid)) {
			new MyAsyncTask().execute(new AsyncCallBack<List<AreaVo>>() {

				@Override
				public void onHandle(List<AreaVo> result) {
					cityMap.put(pid, result);
					callback.onSuccess(result);
				}

				@Override
				public List<AreaVo> execute() {
					// 获取关注贴吧列表
					return areaDao.findAreaByParentid(pid);
				}
			});
		} else {
			callback.onSuccess(cityMap.get(pid));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.setting.logic.SettingProxy#findAreaByType(com.
	 * iwgame.msgs.common.ProxyCallBack, int)
	 */
	@Override
	public void findAreaByType(final ProxyCallBack<List<AreaVo>> callBack, final String[] types) {
		final AreaDao areaDao = DaoFactory.getDaoFactory().getAreaDao(SystemContext.getInstance().getContext());
		if (province == null) {
			new MyAsyncTask(null).execute(new AsyncCallBack<List<AreaVo>>() {

				@Override
				public void onHandle(List<AreaVo> result) {
					province = result;
					callBack.onSuccess(result);
				}

				@Override
				public List<AreaVo> execute() {
					// 获取关注贴吧列表
					return areaDao.findAreaByType(types);
				}
			});
		} else {
			callBack.onSuccess(province);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.setting.logic.SettingProxy#modifypwd(com.iwgame
	 * .msgs.common.ProxyCallBack, java.lang.String, java.lang.String)
	 */
	@Override
	public void modifypassword(final ProxyCallBack<Integer> callback, final Context context, final String opwd, final String npwd) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getAccountRemoteService().modifyPassword(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, opwd, npwd);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.setting.logic.SettingProxy#getGlobalConfig(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void getGlobalConfig(final ProxyCallBack<Integer> callback, final String url) {
		LogUtil.d(TAG, "------->url:" + url);
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<String>() {

			@Override
			public String execute() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("v", AppUtils.getLocalAppVersionCode(SystemContext.getInstance().getContext()) + "");
					params.put("ch", SystemContext.UMENG_CHANNEL + "");
					BDLocation loc = SystemContext.getInstance().getBDLocation();
					if (loc != null) {
						params.put("c", loc.getCity() + "");
						params.put("lo", loc.getLongitude() + "");
						params.put("la", loc.getLatitude() + "");
					}
					return HttpUtil.get(url, params, null);
				} catch (Exception e) {
					LogUtil.e(TAG, "请求服务端全局配置失败：" + e.toString());
					asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
				}
				return null;
			}

			@Override
			public void onHandle(String result) {
				if (result != null) {
					LogUtil.d(TAG, "------->全局配置文件内容：" + result);
					// 解析服务地址配置
					try {
						JSONObject jo = new JSONObject(result);
						SystemContext.getInstance().setHttpIP((String) jo.get("hs"));
						SystemContext.getInstance().setHttpPort((Integer) jo.get("hp"));
						SystemContext.getInstance().setLoginHttpIP((String) jo.get("lhs"));
						SystemContext.getInstance().setLoginHttpPort((Integer) jo.get("lhp"));
						SystemContext.getInstance().setTcpIP((String) jo.get("ts"));
						SystemContext.getInstance().setTcpPort((Integer) jo.get("tp"));
						SystemContext.getInstance().setResIP((String) jo.get("rs"));
						SystemContext.getInstance().setTemplateDirIp((String) jo.get("templatedir"));
						// 解析版本配置
						SystemConfig.APP_CONFIG_URL = (String) jo.get("app");
						SystemConfig.GUEST_PERMISSION_URL = (String) jo.get("gp");
						SystemConfig.AGREEMENT_PATH = (String) jo.get("agreement");
						SystemConfig.HELP_PATH = (String) jo.get("help");
						if (!SystemConfig.AGREEMENT_PATH.isEmpty()) {
							SystemConfig.PROTOCOL_SERVICE = SystemConfig.AGREEMENT_PATH + "/agreement_" + SystemContext.APPTYPE + ".html";
							SystemConfig.GROUP_PROTOCOL_SERVICE = SystemConfig.AGREEMENT_PATH + "/guildagreement_" + SystemContext.APPTYPE + ".html";
							SystemConfig.PLAY_PROTOCOL_ADRRESS = SystemConfig.AGREEMENT_PATH + "/pwagreement_" + SystemContext.APPTYPE + ".html";
							SystemConfig.PAY_PROTOCOL_ADRRESS = SystemConfig.AGREEMENT_PATH + "/payagreement_" + SystemContext.APPTYPE + ".html";
						}
						if (!SystemConfig.HELP_PATH.isEmpty()) {
							String verson = AppUtils.getLocalAppVersionName(SystemContext.getInstance().getContext());
							int index = verson.lastIndexOf(".");
							String largerVerson = verson.substring(0, index);
							SystemConfig.PROTOCOL_HELP = SystemConfig.HELP_PATH + "/help_" + SystemContext.APPTYPE + "_" + largerVerson + ".html";
						}
						SystemConfig.DIVISION_URL = (String) jo.get("divisionurl");
						SystemConfig.GLOBAL_WORDS_MD5 = (String) ((JSONObject) jo.get("w")).get("md5");
						SystemConfig.GLOBAL_WORDS_URL = (String) ((JSONObject) jo.get("w")).get("word");
						SystemConfig.GLOBAL_NAMEWORDS_MD5 = (String) ((JSONObject) jo.get("w")).get("md54name");
						SystemConfig.GLOBAL_NAMEWORDS_URL = (String) ((JSONObject) jo.get("w")).get("word4name");
						SystemConfig.SERVCE_STATUS = (Integer) jo.get("status");

						SystemContext.getInstance().setSharePersonBaseUrl((String) ((JSONObject) jo.get("share")).get("person"));
						SystemContext.getInstance().setSharePostBaseUrl((String) ((JSONObject) jo.get("share")).get("post"));
						String newChannelsMD5 = jo.getJSONObject("channels").getString("md5");
						String newChannelsPath = jo.getJSONObject("channels").getString("channels");
						loadChannelsShowRule(newChannelsMD5, newChannelsPath);

					} catch (JSONException e) {
						e.printStackTrace();
						asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
					}
					loadWordsMD5(SystemConfig.GLOBAL_WORDS_MD5);
					loadNameWordsMD5(SystemConfig.GLOBAL_NAMEWORDS_MD5);
					loadGuestPermissionConfig(SystemConfig.GUEST_PERMISSION_URL);
					if (!AdaptiveAppContext.isHasGetAppConfigFromNet) {
						getAppConfig(null);
					}

					loadAppConfig(callback, SystemConfig.APP_CONFIG_URL);
				} else {
					asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
					SystemContext.getInstance().setMessageSubjectRuleList(getDefaultMessageSubjectRuleList(SystemContext.getInstance().getContext()));
				}
			}

		});

	}

	/**
	 * 加载关键字MD5码
	 * 
	 * @param wordMd5
	 */
	private void loadWordsMD5(final String wordMd5) {
		LogUtil.d(TAG, "------->请求敏感词MD5内容：" + wordMd5);
		if (wordMd5 != null) {
			// 解析服务地址配置
			String wordmd5 = SystemContext.getInstance().getWordsMD5();
			if (wordmd5 == null || !wordmd5.equals(wordMd5)) {
				loadWords(wordMd5, SystemConfig.GLOBAL_WORDS_URL, 0);
			}
		}
	}

	/**
	 * 加载关键字MD5码
	 * 
	 * @param wordMd5
	 */
	private void loadNameWordsMD5(final String wordMd5) {
		LogUtil.d(TAG, "------->请求名称敏感词MD5内容：" + wordMd5);
		if (wordMd5 != null) {
			// 解析服务地址配置
			String wordmd5 = SystemContext.getInstance().getNameWordsMD5();
			if (wordmd5 == null || !wordmd5.equals(wordMd5)) {
				loadWords(wordMd5, SystemConfig.GLOBAL_NAMEWORDS_URL, 1);
			}
		}
	}

	/**
	 * 加载关键字
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private void loadWords(final String wordsmd5, final String path, final int type) {
		new MyAsyncTask().execute(new AsyncCallBack<String>() {

			@Override
			public String execute() {
				try {
					return HttpUtil.get(path, null, null);
				} catch (Exception e) {
					LogUtil.e(TAG, "请求敏感词失败：" + e.getMessage());
				}
				return null;
			}

			@Override
			public void onHandle(String result) {
				if (result != null) {
					LogUtil.d(TAG, "------->请求敏感词内容：" + result);
					try {
						result = new String(Base64.decode(result, Base64.DEFAULT));
						LogUtil.d(TAG, "------->请求敏感词解码后的内容：" + result);
						if (type == 0) {
							// 保存文件
							FileUtils.writeFile(SystemContext.getInstance().getContext(), SystemConfig.MSGS_WORDS, result, Context.MODE_PRIVATE);
							SystemContext.getInstance().setWordsMD5(wordsmd5);
							// 设置到关键词管理类中
							ServiceFactory.getInstance().getWordsManager().setWords(result);
						} else if (type == 1) {
							// 保存文件
							FileUtils.writeFile(SystemContext.getInstance().getContext(), SystemConfig.MSGS_NAMEWORDS, result, Context.MODE_PRIVATE);
							SystemContext.getInstance().setNameWordsMD5(wordsmd5);
							// 设置到关键词管理类中
							ServiceFactory.getInstance().getWordsManager().setNameWords(result);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		});
	}

	/**
	 * 加载app相关阀值配置文件
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private void loadAppConfig(final ProxyCallBack<Integer> callback, final String path) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<String>() {

			@Override
			public String execute() {
				try {
					return HttpUtil.get(path, null, null);
				} catch (Exception e) {
					LogUtil.e(TAG, "请求配置失败：" + e.getMessage());
				}
				return null;
			}

			@Override
			public void onHandle(String result) {
				if (result != null) {
					LogUtil.d(TAG, "-------->请求配置内容：" + result);
					try {
						// 相关参数配置
						JSONObject jo = new JSONObject(result);
						SystemContext.getInstance().setAM((Integer) (((JSONObject) jo.get("p")).get("am")));
						SystemContext.getInstance().setMM((Integer) (((JSONObject) jo.get("p")).get("mm")));
						SystemContext.getInstance().setNearDistance((Integer) (((JSONObject) jo.get("p")).get("dis")));
						SystemContext.getInstance().setGamePraiseMaxTipCount((Integer) (((JSONObject) jo.get("p")).get("gptcm")));
						SystemContext.getInstance().setGAM((Integer) (((JSONObject) jo.get("p")).get("gam")));
						SystemContext.getInstance().setPAM((Integer) (((JSONObject) jo.get("p")).get("pam")));
						SystemContext.getInstance().setPIT((Integer) (((JSONObject) jo.get("p")).get("pit")));
						SystemContext.getInstance().setGCRN((Integer) (((JSONObject) jo.get("p")).get("gcrn")));
						SystemContext.getInstance().setGCRG((Integer) (((JSONObject) jo.get("p")).get("gcrg")));
						SystemContext.getInstance().setGCRT((Integer) (((JSONObject) jo.get("p")).get("gcrt")));
						SystemContext.getInstance().setPTID((Integer) (((JSONObject) jo.get("p")).get("ptid")));
						//SystemContext.getInstance().setMSGM((Integer) (((JSONObject) jo.get("p")).get("msgm")));

						JSONArray msr = jo.getJSONArray("msr");
						List<MessageSubjectRuleVo> msrList = new ArrayList<MessageSubjectRuleVo>();
						// 解析消息入库规则
						for (int i = 0; i < msr.length(); i++) {
							JSONObject msrItem = msr.getJSONObject(i);
							// {//对聊的规则
							// channelType:"chat",//消息系统中channeltype
							// category:"chat",//消息系统中categrory
							// result:"formorto" //主体用非本登录用户的另外一方
							// }
							MessageSubjectRuleVo msessageSubjectRuleVo = new MessageSubjectRuleVo();
							msessageSubjectRuleVo.setCategory(msrItem.getString("category"));
							msessageSubjectRuleVo.setChannelType(msrItem.getString("channelType"));
							msessageSubjectRuleVo.setResult(msrItem.getString("result"));
							msrList.add(msessageSubjectRuleVo);
						}
						SystemContext.getInstance().setMessageSubjectRuleList(msrList);
						asyncTask.getProxyCallBack().onSuccess(Msgs.ErrorCode.EC_OK_VALUE);
					} catch (Exception e) {
						e.printStackTrace();
						asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
						SystemContext.getInstance().setMessageSubjectRuleList(
								getDefaultMessageSubjectRuleList(SystemContext.getInstance().getContext()));
					}
				} else {
					asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);

					SystemContext.getInstance().setMessageSubjectRuleList(getDefaultMessageSubjectRuleList(SystemContext.getInstance().getContext()));

				}
			}

		});
	}

	@Override
	public void getAppConfig(final ProxyCallBack<Integer> callback) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<String>() {

			@Override
			public String execute() {
				try {
					return HttpUtil.get(SystemContext.GLOBAL_APPCONFIG_URL, null, null);
				} catch (Exception e) {
					LogUtil.e(TAG, "请求配置失败：" + e.getMessage());
					if (callback != null) {
						asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
					}
				}
				return null;
			}

			@Override
			public void onHandle(String result) {
				if (result != null) {
					LogUtil.d(TAG, "-------->请求配置内容：" + result);
					try {
						AppConfig appConfig = AdaptiveAppContext.getInstance().getAppConfig(result);
						if (appConfig != null) {
							AdaptiveAppContext.getInstance().setAppConfig(appConfig);
							AdaptiveAppContext.getInstance().setAppConfigString(result);
							AdaptiveAppContext.isHasGetAppConfigFromNet = true;
							if (callback != null) {
								asyncTask.getProxyCallBack().onSuccess(Msgs.ErrorCode.EC_OK_VALUE);
							}
						} else {
							if (callback != null) {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
							}
						}
					} catch (Exception e) {
						if (callback != null) {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
						}
						e.printStackTrace();
					}
				} else {
					if (callback != null) {
						asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
					}
				}
			}
		});
	}

	/**
	 * 加载游客权限配置
	 * 
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private void loadGuestPermissionConfig(final String path) {
		new MyAsyncTask().execute(new AsyncCallBack<String>() {

			@Override
			public String execute() {
				try {
					return HttpUtil.get(path, null, null);
				} catch (Exception e) {
					LogUtil.e(TAG, "请求配置失败：" + e.getMessage());
				}
				return null;
			}

			@Override
			public void onHandle(String result) {
				if (result != null) {
					LogUtil.d(TAG, "-------->请求配置内容：" + result);
					try {
						// 相关参数配置
						JSONObject guestPermission = new JSONObject(result).getJSONObject("Unbound");
						if (guestPermission.length() > 0) {
							SystemContext.guestPermissionMap = new HashMap<String, Boolean>();
							Iterator it = guestPermission.keys();
							while (it.hasNext()) {
								String key = (String) it.next();
								SystemContext.guestPermissionMap.put(key, guestPermission.getBoolean(key));
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		});
	}

	/**
	 * 获得默认消息入库规则
	 * 
	 * @return
	 */
	private List<MessageSubjectRuleVo> getDefaultMessageSubjectRuleList(Context context) {
		if (context != null) {
			try {
				InputStream is = context.getAssets().open("app");
				try {
					byte[] bytes = HttpUtil.readInputStream(is);
					if (bytes != null) {
						String str = new String(bytes);
						// 相关参数配置
						JSONObject jo = new JSONObject(str);
						JSONArray msr = jo.getJSONArray("msr");
						List<MessageSubjectRuleVo> msrList = new ArrayList<MessageSubjectRuleVo>();
						// 解析消息入库规则
						for (int i = 0; i < msr.length(); i++) {
							JSONObject msrItem = msr.getJSONObject(i);
							MessageSubjectRuleVo msessageSubjectRuleVo = new MessageSubjectRuleVo();
							msessageSubjectRuleVo.setCategory(msrItem.getString("category"));
							msessageSubjectRuleVo.setChannelType(msrItem.getString("channelType"));
							msessageSubjectRuleVo.setResult(msrItem.getString("result"));
							msrList.add(msessageSubjectRuleVo);
						}
						return msrList;
					} else {
						return null;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.setting.logic.SettingProxy#cleanCache()
	 */
	@Override
	public void cleanCache() {
		// TODO Auto-generated method stub
		// 清空聊天cache和图片
		ProxyFactory.getInstance().getMessageProxy().delMessage(MsgsConstants.MC_CHAT, -1, MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_CHAT);
		ProxyFactory.getInstance().getMessageProxy().delMessage(MsgsConstants.MC_MCHAT, -1, MsgsConstants.DOMAIN_GROUP, MsgsConstants.MCC_CHAT);
		// 清空所有的 相关SharedPreferences
		SystemContext.getInstance().cleanSubjectUnReadCount(MsgsConstants.MC_CHAT, MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_CHAT);
		SystemContext.getInstance().cleanSubjectUnReadCount(MsgsConstants.MC_MCHAT, MsgsConstants.DOMAIN_GROUP, MsgsConstants.MCC_CHAT);
		// 清空所有聊天记录
		// ProxyFactory.getInstance().getMessageProxy().delAllMessage();
		SharedPreferences share = SystemContext.getInstance().getUserSharedPreferences();
		if (share != null) {
			Map<String, ?> shareData = share.getAll();
			for (String key : shareData.keySet()) {
				if (key.contains("message_canread_minIndex_")) {
					share.edit().putLong(key, -1L).commit();
				}
			}
		}
	}

	/**
	 * 加载解析频道展示的规则
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void loadChannelsShowRule(final String md5, final String path) {
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String result = "";
				SystemContext.getInstance().setChannelConfigUrl(path);
				if (!SystemContext.getInstance().getChannelConfigMD5().equals(md5)) {

					try {

						result = HttpUtil.get(path, null, null);
					} catch (Exception e) {
						LogUtil.e(TAG, "获得频道的显示规则失败" + e.getMessage());
					}
					if (result != null && !result.isEmpty()) {
						// 更新本地缓存
						try {
							// 保存文件
							FileUtils.writeFile(SystemContext.getInstance().getContext(), SystemConfig.CHANNELSHOWRULE_FILENAME, result,
									Context.MODE_PRIVATE);
							// cache 文件保存成功，再写新的md5
							SystemContext.getInstance().setChannelConfigMD5(md5);

						} catch (IOException e) {
							e.printStackTrace();
							LogUtil.e(TAG, "保存从网络上获得的频道显示规则 异常：" + e.getMessage());
						}
					} else {
						LogUtil.e(TAG, "从网络上获得的频道显示规则异常，内容为空或不操作");
					}

				}
				if (result == null || result.isEmpty()) {
					// 从cache中获得
					try {
						Object obj = FileUtils.readFile(SystemContext.getInstance().getContext(), SystemConfig.CHANNELSHOWRULE_FILENAME);
						result = obj.toString();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						LogUtil.e(TAG, "从cache文件中获得频道显示规则时异常：" + e.getMessage());
					}
				}
				// 解析
				if (result != null && !result.isEmpty()) {
					SystemContext.getInstance().setChannelsShowRule(parseChannelsShowRule(result));
				} else {
					LogUtil.e(TAG, "解析频道显示规则时，原内容不存在或空");
				}

				return null;

			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 解析频道的显示规则
	 * 
	 * @param rule
	 */
	private List<Object> parseChannelsShowRule(String rule) {

		List<Object> channels = null;
		// channel 的父tag 1 为channels ，2为channelgroup
		int pTag = 1;
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new StringReader(rule));

			ChannelVo channel = null;
			ChannelGroupVo channelGroup = null;
			// 获取事件类型
			int eventType = parser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				// 文档开始
				case XmlPullParser.START_DOCUMENT:
					channels = new ArrayList<Object>();
					break;
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if ("channels".equals(tagName)) {

					} else if ("channel".equals(tagName)) {
						// <!--游伴小助手-->
						// <channel type= "c-pub"
						// name="游伴小助手"
						// icon=""
						// isUseChannel = "false"
						// channelType ="notify"
						// category="announce"
						// isAllowDel="true"
						// subjectid="0"
						// subjectdomian="p"
						// pagetype="page-pub"
						// >
						// <page />
						// </channel>

						channel = new ChannelVo();
						channel.setType(parser.getAttributeValue(null, "type"));
						channel.setName(parser.getAttributeValue(null, "name"));
						channel.setIcon(parser.getAttributeValue(null, "icon"));
						channel.setUseChannel(parser.getAttributeValue(null, "isUseChannel").equals("true") ? true : false);
						channel.setChannelType(parser.getAttributeValue(null, "channelType"));
						channel.setCategory(parser.getAttributeValue(null, "category"));
						channel.setAllowDel(parser.getAttributeValue(null, "isAllowDel").equals("true") ? true : false);
						String subjectid = parser.getAttributeValue(null, "subjectid");
						channel.setSubjectid(subjectid == null || subjectid.isEmpty() ? 0 : Long.parseLong(subjectid));
						channel.setSubjectdomain(parser.getAttributeValue(null, "subjectdomian"));
						channel.setPagetype(parser.getAttributeValue(null, "pagetype"));

					} else if ("page".equals(tagName)) {

					} else if ("channelGroup".equals(tagName)) {
						// <channelGroup type = "c-mchat"
						// name="公会聊天室消息"
						// icon = ""
						// isUseChannel = "false"
						// isAllowDel="true"
						// minExp = "5"
						// pagetype="page-list">
						pTag = 2;
						channelGroup = new ChannelGroupVo();
						channelGroup.setType(parser.getAttributeValue(null, "type"));
						channelGroup.setName(parser.getAttributeValue(null, "name"));
						channelGroup.setIcon(parser.getAttributeValue(null, "icon"));
						channelGroup.setUseChannel(parser.getAttributeValue(null, "isUseChannel").equals("true") ? true : false);
						channelGroup.setAllowDel(parser.getAttributeValue(null, "isAllowDel").equals("true") ? true : false);
						channelGroup.setMinExp(Integer.parseInt(parser.getAttributeValue(null, "minExp")));
						channelGroup.setPagetype(parser.getAttributeValue(null, "pagetype"));
					}
					break;
				case XmlPullParser.END_TAG:
					String endTagName = parser.getName();
					if ("channelGroup".equals(endTagName)) {
						channels.add(channelGroup);
						pTag = 1;
					} else if ("channel".equals(endTagName)) {
						if (pTag == 1) {
							channels.add(channel);
						} else if (pTag == 2) {
							channelGroup.setChannelVo(channel);
						}
					}
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return channels;
	}

	@Override
	public void loadAppLuncherBg(final ProxyCallBack<PageDataVo> callback, final Context context, final int pageId) {
		final MyAsyncTask<PageDataVo> asyncTask = new MyAsyncTask<PageDataVo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getCommonRemoteService().loadAppLuncherBg(new ServiceCallBack<XAction.XActionResult>() {
					
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.pageDataResult)) {
							PageDataResult ptr = result.getExtension(Msgs.pageDataResult);
							List<PageData> list = ptr.getPageDataList();
							if(list != null && list.size() > 0){
								PageData pageData = list.get(0);
								if(pageData != null){
									PageDataVo dataVo = new PageDataVo();
									dataVo.setApptype(pageData.getApptype());
									dataVo.setResourceid(pageData.getResourceid());
									dataVo.setType(pageData.getType());
									dataVo.setUpdatetime(pageData.getUpdatetime());
									asyncTask.getProxyCallBack().onSuccess(dataVo);
								}else{
									asyncTask.getProxyCallBack().onSuccess(null);
								}
							}else{
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}
					
					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, pageId);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

}
