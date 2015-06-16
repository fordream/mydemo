/**      
 * UserServiceProxy.java Create on 2013-7-30     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.logic;

import java.util.List;
import java.util.Map;

import org.openudid.OpenUDID_manager;

import android.content.Context;
import android.util.Log;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.EnvManager;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.object.GuestObject;
import com.iwgame.msgs.module.remote.AccountRemoteService;
import com.iwgame.msgs.module.remote.UserRemoteService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.GuestRegisterResult;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.service.MessageService;
import com.iwgame.msgs.utils.BuildVoUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.SessionUtil;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.sdk.xaction.XActionSessionStatusListener.XActionSessionStatus;
import com.iwgame.utils.BitmapUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.iwgame.xaccount.proto.XAccountInfo.AccountSummary;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: AccountProxyImpl
 * @Description: 用户账号模块代理服务类（处理业务逻辑）
 * @author 王卫
 * @date 2013-7-30 下午06:09:48
 * @Version 1.0
 * 
 */
public class AccountProxyImpl implements AccountProxy {

	private static final String TAG = "AccountProxyImpl";

	private AccountRemoteService accountService = ServiceFactory.getInstance().getAccountRemoteService();
	private UserRemoteService userService = ServiceFactory.getInstance().getUserRemoteService();
	// 登陆请求是否返回过
	private boolean loginIsBack = false;
	
	private long userFullId;

	private static byte[] lock = new byte[0];

	private static AccountProxyImpl instance = null;

	private AccountProxyImpl() {

	}

	public static AccountProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new AccountProxyImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/**
	 * 提交手机号获取验证码
	 * 
	 * @param callback
	 * @param context
	 * @param context
	 * @param mobileNumber
	 * @param mode
	 * @return
	 */
	@Override
	public void getCaptcha(final ProxyCallBack<Integer> callback, final Context context, final String mobileNum, final int mode) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				accountService.getCaptcha(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, mobileNum, mode);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/**
	 * 验证验证码
	 * 
	 * @param callback
	 * @param context
	 * @param context
	 * @param mobileNum
	 * @param captcha
	 * @return
	 */
	@Override
	public void validateCaptcha(final ProxyCallBack<Integer> callback, final Context context, final String mobileNum, final String captcha) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				accountService.validateCaptcha(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, mobileNum, captcha);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/**
	 * 注册账号 提交注册信息
	 * 
	 * @param callback
	 * @param context
	 * @param captcha
	 * @param accountName
	 * @param password
	 * @param nickname
	 * @param sex
	 * @param age
	 * @param avatar
	 * @return
	 */
	@Override
	public void registerAccount(final ProxyCallBack<Integer> callback, final Context context, final String captcha, final String accountName, final String password,
			final String nickname, final int sex, final Integer age, final byte[] avatar, final String origin) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.registAccount(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, captcha, accountName, password, nickname, sex, age, avatar, origin);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	@Override
	public void bundPhone(final ProxyCallBack<Integer> callback, final Context context,
			final String captcha, final String accountName, final String password, final String nickname, final int sex, final byte[] avatar) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				//改成了新接口去获取数据 ，要先拼一个pb结构
				final ContentDetailParams.Builder params = ContentDetailParams.newBuilder();
				ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
				ExtUserVo extUserVo = SystemContext.getInstance().getExtUserVo();
				cdp.setId(extUserVo.getUserid());
				cdp.setUtime(extUserVo.getUpdatetime());
				params.addParam(cdp.build());
				userService.bundPhone(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						if (result != null && result.getRc() == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE) {
							userService.getUserInfo(new ServiceCallBack<XActionResult>() {
								@Override
								public void onSuccess(XActionResult result) {
									if (result != null && result.hasExtension(Msgs.contentDetailsResult)) {
										Msgs.ContentDetailsResult contentResult = result.getExtension(Msgs.contentDetailsResult);
										List<UserInfoDetail> infoList = contentResult.getUserInfoDetailList();
										if(infoList == null || infoList.size() <= 0) return ;
										Msgs.UserInfoDetail detail = infoList.get(0);
										if (detail != null) {
											// 存储当前登录的用户信息
											SystemContext.getInstance().setAccount(accountName);
											SystemContext.getInstance().setIsGuest(detail.getIsGuest());
											SystemContext.getInstance().setGrade(detail.getGrade());
											SystemContext.getInstance().setPasssword(password);
											SystemContext.getInstance().setAvatar(detail.getAvatar());
											SystemContext.getInstance().setUsername(detail.getNickname());
											SystemContext.getInstance().setSex(detail.getSex());
											SystemContext.getInstance().setLoginStatus(detail.getIsGuest());
											SystemContext.getInstance().setJob(detail.getJob());
											SystemContext.getInstance().setAge(detail.getAge());
											SystemContext.getInstance().setPoint(detail.getPoint());
											SystemContext.getInstance().setMobile(detail.getPhoneNo());
											if(detail.getIsGuest() == 0){//正常用户
												String avater = detail.getAvatar();
												String imageUrl = ResUtil.getSmallRelUrl(avater);
												if(imageUrl != null && !imageUrl.isEmpty()){
													BitmapUtil.saveUrlBitmapToData(context, imageUrl, SystemConfig.LOGIN_USER_ICON_NAME);
												}
											}
											
											asyncTask.getProxyCallBack().onSuccess(result.getRc());
										} else {
											asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
										}
									} else {
										asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
									}
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									asyncTask.getProxyCallBack().onFailure(result, null);
								}
							}, context, params.build(), 0, null);

						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, captcha, accountName, password, nickname, sex, avatar);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/**
	 * 游客注册
	 */
	@Override
	public void registerGuestAccount(final ProxyCallBack<GuestObject> callback,
			final Context context, final String origin) {
		final MyAsyncTask<GuestObject> asyncTask = new MyAsyncTask<GuestObject>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.registGuestAccount(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE
								&& result.hasExtension(Msgs.guestRegisterResult)){
							GuestRegisterResult registerResult = result.getExtension(Msgs.guestRegisterResult);
							if(registerResult != null){
								GuestObject guest = new GuestObject();
								if(registerResult.getUsername() != null && registerResult.getPwd() != null){
									guest.setAccount(registerResult.getUsername());
									guest.setPassword(registerResult.getPwd());
									asyncTask.getProxyCallBack().onSuccess(guest);
								}else{
									asyncTask.getProxyCallBack().onSuccess(null);
								}

							}else{
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
							}
						}else{
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}
					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, origin);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/**
	 * 密码重置
	 * 
	 * @param callback
	 * @param context
	 * @param account
	 * @param password
	 * @param captcha
	 * @return
	 */
	@Override
	public void resetPassword(final ProxyCallBack<Integer> callback, final Context context, final String account, final String password, final String captcha) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				accountService.resetPassword(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, account, password, captcha);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/**
	 * 登录
	 * 
	 * @param callback
	 * @param context
	 * @param account
	 * @param password
	 * @return
	 */
	@Override
	public void login(final ProxyCallBack<Integer> callback, final Context context, final String account, final String password) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				SystemContext.getInstance().clearUserContext();
				userFullId = 0;
				String deviceId = OpenUDID_manager.getOpenUDID();
				LogUtil.d(TAG, "--------->获取设备ID：" + deviceId);
				loginIsBack = false;
				final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
				accountService.login(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						LogUtil.d(TAG, "--------->登陆返回结果："+result.getRc());
						if (!loginIsBack) {
							loginIsBack = true;
							if (result != null && result.getRc() == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE
									&& result.hasExtension(XAccountInfo.accountSummary)) { // 设置摘要信息到上下文
								final AccountSummary summary = result.getExtension(XAccountInfo.accountSummary);
								userFullId = summary.getFullId();
								// 设置账号
								SystemContext.getInstance().setAccount(account);
								if(userFullId > 0){
									getUserInfo(callback, context, dao, userFullId, account, password);
								}
							} else {
								asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
								pushErrorLog(String.format("--------->登陆(account:%1$s)返回结果为空", account));
							}
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if (!loginIsBack) {
							loginIsBack = true;
							asyncTask.getProxyCallBack().onFailure(result, resultMsg);
							pushErrorLog(String.format("--------->登陆(account:%1$s)失败,错误码:%2$d", account, result));
						}
					}
				}, context, account, password, deviceId);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}
	
	@Override
	public void getUserInfoHasLogin(ProxyCallBack<Integer> callback,
			Context context, String account, String password) {
		if(SessionUtil.getSession().getStatus() == XActionSessionStatus.SESSION_OPENED){//open状态直接打开
			if(userFullId > 0){
				final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
				getUserInfo(callback, context, dao, userFullId, account, password);
			}
		}else{
			SessionUtil.getSession().open();
			callback.onFailure(ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR, null);
		}
	}


	/**
	 * 获取用户信息
	 * @param callback
	 * @param context
	 * @param dao
	 * @param userFullId
	 * @param account
	 * @param password
	 */
	private void getUserInfo(final ProxyCallBack<Integer> callback, final Context context,final UserDao dao, final long userFullId, final String account, final String password){
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				//是否重新登录过
				SystemContext.getInstance().setReloadTag(true);
				// 存储当前登录的用户信息
				ExtUserVo vo = new ExtUserVo();
				vo.setUserid(userFullId);
				SystemContext.getInstance().setHasSendUnAuthInfo(false);
				userService.setPhoneType(new ServiceCallBack<XAction.XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
					}
				}, context);
				ContentDetailParams.Builder params = ContentDetailParams.newBuilder();
				ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
				cdp.setId(userFullId);
				cdp.setUtime(0);
				params.addParam(cdp.build());
				Log.i("rizhi", params.getParamCount()+"");
				// 为空则请求获取用户信息
				userService.getUserInfo(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.contentDetailsResult)) {
							Msgs.ContentDetailsResult contentResult = result.getExtension(Msgs.contentDetailsResult);
							List<UserInfoDetail> infoList = contentResult.getUserInfoDetailList();
							if(infoList == null || infoList.size() <= 0) return;
							Msgs.UserInfoDetail detail = contentResult.getUserInfoDetail(0);
							// 设置账号
							SystemContext.getInstance().setAccount(account);
							// 设置密码
							SystemContext.getInstance().setPasssword(password);
							// 设置手机号
							SystemContext.getInstance().setMobile(detail.getPhoneNo());
							
							// 获取用户详细信息并设置
							ExtUserVo vo = BuildVoUtil.buildExtUserVo(detail);
							if (vo != null) {
								//设置当前登录账户类型
								SystemContext.getInstance().setLoginStatus(vo.getIsGuest());
								SystemContext.getInstance().setIsGuest(vo.getIsGuest());
								if(vo.getIsGuest() == 0){//正常用户
									String avater = vo.getAvatar();
									String imageUrl = ResUtil.getSmallRelUrl(avater);
									if(imageUrl != null && !imageUrl.isEmpty()){
										BitmapUtil.saveUrlBitmapToData(context, imageUrl, SystemConfig.LOGIN_USER_ICON_NAME);
									}
								}
								// 存储当前登录的用户信息
								SystemContext.getInstance().setExtUserVo(vo);
								SystemContext.getInstance().setPoint(vo.getPoint());
								vo.setRelPositive(0);
								vo.setRelInverse(0);
								dao.insertOrUpdateByUserid(vo);
								asyncTask.getProxyCallBack().onSuccess(result.getRc());
								long gameid = AdaptiveAppContext.getInstance().getAppConfig().getGameId();
								if(gameid != 0){//自动关注攻略贴吧
									ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

										@Override
										public void onSuccess(Integer result) {
										}

										@Override
										public void onFailure(Integer result, String resultMsg) {
										}
									}, context, gameid, MsgsConstants.OT_GAME, MsgsConstants.OP_FOLLOW, null, null, SystemContext.APPTYPE);
								}
								LogUtil.d(TAG, "---->登录设置位置location=" + SystemContext.getInstance().getLocation());
								userService.setPosition(new ServiceCallBack<XActionResult>() {

									@Override
									public void onSuccess(XActionResult result) {
										LogUtil.d(TAG, "---->向服务端设置位置成功");
									}

									@Override
									public void onFailure(Integer result, String resultMsg) {
										LogUtil.d(TAG, "---->向服务端设置位置失败");
									}
								}, null, SystemContext.getInstance().getLocation());
								//获取用户等级策略
								ProxyFactory.getInstance().getUserProxy().getUserGradePolicy(new ProxyCallBack<List<UserGradeVo>>() {

									@Override
									public void onSuccess(List<UserGradeVo> result) {

									}

									@Override
									public void onFailure(Integer result, String resultMsg) {

									}
								}, context);
								//添加自动关注功能
								MessageService.getLocalInstallPackages();
							} else {
								//logout(callback, context);
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR, null);
								pushErrorLog(String.format("登陆获取用户(account:%1$s)信息为空", account));
							}
						} else {
							//logout(callback, context);
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR, null);
							pushErrorLog(String.format("登陆获取用户(account:%1$s)信息为空", account));
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						//callback.onFailure(result, null);
						asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR, null);
						pushErrorLog(String.format("登陆获取用户(account:%1$s)信息失败,错误码:%2$d", account, result));
					}
				}, context, params.build(), 0, null);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/**
	 * 提交错误日志
	 * 
	 * @param content
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void pushErrorLog(final String content) {
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String imsi = null;
				String systemVersion = null;
				String networkType = null;
				String phoneModel = null;
				String mac = null;
				String position = null;

				Map<String, String> emap = EnvManager.getInstance().getEnvMap();
				if (emap.containsKey(EnvManager.D_IMSI))
					imsi = emap.get(EnvManager.D_IMSI);
				if (emap.containsKey(EnvManager.D_SVERSION))
					systemVersion = emap.get(EnvManager.D_SVERSION);
				if (emap.containsKey(EnvManager.D_NTYPE))
					networkType = emap.get(EnvManager.D_NTYPE);
				if (emap.containsKey(EnvManager.D_MODEL))
					phoneModel = emap.get(EnvManager.D_MODEL);
				if (emap.containsKey(EnvManager.D_MAC))
					mac = emap.get(EnvManager.D_MAC);
				if (emap.containsKey(EnvManager.D_POSITION))
					position = emap.get(EnvManager.D_POSITION);

				ServiceFactory.getInstance().getContentRemoteService().saveLog(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}
				}, null, content, null, imsi, systemVersion, networkType, phoneModel, mac, position);
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
	 * com.iwgame.msgs.module.account.logic.AccountProxy#verifyAccount(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void verifyAccount(final ProxyCallBack<Integer> callback, final Context context) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// 判断网络,有网络直接发请求验证，没有网络的判断本地配置文件中的存储用户id，有跳转到成功，没有同样返回失败
				if (NetworkUtil.isNetworkAvailable(context)) {
					userService.verifyAccount(new ServiceCallBack<XActionResult>() {

						@Override
						public void onSuccess(final XActionResult result) {
							if (result != null) {
								asyncTask.getProxyCallBack().onSuccess(result.getRc());
							} else {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							asyncTask.getProxyCallBack().onFailure(result, null);
						}
					}, null);
				} else {
					long uid = SystemContext.getInstance().getSharedPreferences().getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, 0);
					if (uid > 0) {
						asyncTask.getProxyCallBack().onSuccess(com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE);
					} else {
						asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
					}
				}
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
	 * com.iwgame.msgs.module.account.logic.AccountProxy#logout(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void logout(final ProxyCallBack<Integer> callback, final Context context, final String token) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				accountService.logout(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						if (result != null)
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						else
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, token);
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
	 * com.iwgame.msgs.module.account.logic.AccountProxy#validateAccount(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void validateAccount(final ProxyCallBack<Integer> callback, final Context context) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				accountService.validateAccount(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.account.logic.AccountProxy#login(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public void login(final ProxyCallBack<Integer> callback, final Context context,
			final String account, final int authtype, final String apitype, final String openId) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
			SystemContext.getInstance().clearUserContext();
			userFullId = 0;
			String deviceId = OpenUDID_manager.getOpenUDID();
			LogUtil.d(TAG, "--------->获取设备ID：" + deviceId);
			loginIsBack = false;
			final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
			accountService.login(new ServiceCallBack<XActionResult>() {
	
				@Override
				public void onSuccess(final XActionResult result) {
					LogUtil.d(TAG, "--------->登陆返回结果："+result.getRc());
					if (!loginIsBack) {
						loginIsBack = true;
						if (result != null && result.getRc() == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE
								&& result.hasExtension(XAccountInfo.accountSummary)) { // 设置摘要信息到上下文
							final AccountSummary summary = result.getExtension(XAccountInfo.accountSummary);
							userFullId = summary.getFullId();
							// 设置账号
							SystemContext.getInstance().setAccount(account);
							SystemContext.getInstance().setApiType(apitype);
							SystemContext.getInstance().setOpenId(openId);
							if(userFullId > 0){
								getUserInfo(callback, context, dao, userFullId, openId, "");
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
							pushErrorLog(String.format("--------->登陆(account:%1$s)返回结果为空", account));
						}
					}
				}
	
				@Override
				public void onFailure(Integer result, String resultMsg) {
					if (!loginIsBack) {
						loginIsBack = true;
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
						pushErrorLog(String.format("--------->登陆(account:%1$s)失败,错误码:%2$d", account, result));
					}
				}
			}, context, account, deviceId, authtype, apitype, openId);
			return null;
			}
			
			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.account.logic.AccountProxy#registerAccount(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.String, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public void registerAccount(final ProxyCallBack<Integer> callback,
			final Context context, final String openID, final int target, final int sex, final String avatar,
			final String nickname, final String origin) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
			userService.registAccount(new ServiceCallBack<XActionResult>() {

			@Override
			public void onSuccess(final XActionResult result) {
				if (result != null) {
					callback.onSuccess(result.getRc());
				} else {
					callback.onFailure(ErrorCode.EC_RESULT_ISNULL, null);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				callback.onFailure(result, resultMsg);
			}
		}, context, openID, target, sex, avatar, nickname, origin);
			return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

}
