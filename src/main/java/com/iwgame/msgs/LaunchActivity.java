/**      
 * SysConfig.java Create on 2013-7-30     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.object.GuestObject;
import com.iwgame.msgs.module.account.ui.UserMainActivity;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.service.MessageService;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.SessionUtil;
import com.iwgame.msgs.utils.UpdataVersionUtil;
import com.iwgame.msgs.vo.local.PageDataVo;
import com.iwgame.sdk.xaction.XActionSessionStatusListener.XActionSessionStatus;
import com.iwgame.utils.BitmapUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * 
 * @ClassName: LaunchActivity
 * @Description: 应用启动界面
 * @author 王卫
 * @date 2013-7-30 下午06:27:11
 * @Version 1.0
 * 
 */
public class LaunchActivity extends BaseSuperActivity {

	protected static final String TAG = "LaunchActivity";
	private boolean isNeedExit = false;
	private ImageView launchImageView;
	
	private String accountName;
	private String password;
	public Bitmap luncher_bg = null;
	
	// 倒计时次数
	private static final int escapeCount = 5;
	
	private Dialog statusErrordialog;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_init_loading);
		launchImageView = (ImageView) findViewById(R.id.launch_image_view);
		luncher_bg = BitmapUtil.getBitmapFromData(this, SystemConfig.APP_LUNCHER_BG, "UTF-8");
		if(luncher_bg != null){
			launchImageView.setImageBitmap(luncher_bg); 
			SystemContext.getInstance().setLoadAppLaunchFromLocal(false);
		}else{
			SystemContext.getInstance().setLuncharBgLoadTime((long)0);
			launchImageView.setImageResource(R.drawable.common_luncher_bg);
			SystemContext.getInstance().setLoadAppLaunchFromLocal(true);
		}
		
		TextView load_tv_first_line = (TextView) findViewById(R.id.load_tv_first_line);
		TextView load_tv_second_line = (TextView) findViewById(R.id.load_tv_second_line);
		load_tv_first_line.setText(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_loading_text());
		load_tv_first_line.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_loading_textcolor());
		load_tv_second_line.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_loading_textcolor());
		// 初始化地理位置服务
		ServiceFactory.getInstance().getBaiduLocationService().init(new LocationCallBack() {

			@Override
			public void onBack(BDLocation bdLocation) {
				if (bdLocation != null)
					SystemContext.getInstance().setLocation(bdLocation);
				if (NetworkUtil.isNetworkAvailable(LaunchActivity.this)) {
					// 启动服务
					if (!MessageService.isRunning) {
						LogUtil.d(TAG,"--启动服务");
						startService(new Intent(LaunchActivity.this, MessageService.class));
						new Thread(new Runnable() {

							@Override
							public void run() {
								int count = 0;
								boolean flag = true;
								while (flag) {
									try {
										LogUtil.d(TAG,"--监听启动服务");
										if (SessionUtil.status == XActionSessionStatus.SESSION_OPENED || SessionUtil.status == XActionSessionStatus.SESSION_UNAUTHENTICATED || count >= 10) {
											flag = false;
											LogUtil.d(TAG,"--监听启动服务成功");
											loadConfig();
										} else {
											count++;
										}
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}).start();
					} else {
						loadConfig();
					}
				} else {
					init();
				}
			}

		});
		
	}

	/**
	 * 加载配置文件
	 */
	private void loadConfig() {
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (!AdaptiveAppContext.isHasGetAppConfigFromNet) {// 没有加载过
					// 读取远程配置文件
					ProxyFactory.getInstance().getSettingProxy().getAppConfig(new ProxyCallBack<Integer>() {

						@Override
						public void onSuccess(Integer result) {
							UpdataVersionUtil.checkMustUpdata(LaunchActivity.this, false, new UpdataVersionUtil.OnCallBackListener() {

								@Override
								public void execute() {
									init();
								}
							});
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							LogUtil.d(TAG,"--加载配置文件失败！");
							init();
						}
					});
				} else {// 已经加载成功
					UpdataVersionUtil.checkMustUpdata(LaunchActivity.this, false, new UpdataVersionUtil.OnCallBackListener() {

						@Override
						public void execute() {
							init();
						}
					});
				}
			}
		});
	}

	/**
	 * 加载启动背景图片
	 */
	private void loadAppLuncharBg(){
		ProxyFactory.getInstance().getSettingProxy().loadAppLuncherBg(new ProxyCallBack<PageDataVo>() {
			
			@Override
			public void onSuccess(PageDataVo result) {
				if(result != null){
					final long updateTime = result.getUpdatetime();
					String resourceid = result.getResourceid();
					String imageUrl = ResUtil.getOriginalRelUrl(resourceid);
					if(imageUrl != null && !imageUrl.isEmpty()){
						ProxyFactory.getInstance().getUserProxy().getImageUrlToData(new ProxyCallBack<Integer>() {
							
							@Override
							public void onSuccess(Integer result) {
								LogUtil.d(TAG, "图片保存成功");
								SystemContext.getInstance().setLuncharBgLoadTime(updateTime);
							}
							
							@Override
							public void onFailure(Integer result, String resultMsg) {
								LogUtil.d(TAG, "图片保存失败");
							}
						}, LaunchActivity.this, imageUrl, SystemConfig.APP_LUNCHER_BG);
					}
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				
			}
		}, LaunchActivity.this, SystemConfig.APP_LUNCHAR_PAGE_ID);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (SystemContext.getInstance().getExtUserVo() == null || SystemContext.getInstance().getToken() == null) {
			int startUp = AdaptiveAppContext.getInstance().getAppConfig().getStartup();//获取启动流程
			if (startUp == 1) {// 游伴流程
				startActivity(new Intent(LaunchActivity.this, UserMainActivity.class));
				LaunchActivity.this.finish();	
			} else {// 攻略流程
				if (SystemContext.getInstance().getLoginStatus() == 0) {// 已正式账号注册登录过
					Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
					startActivity(intent);
					LaunchActivity.this.finish();
				} else {// 还是游客身份
					guestRegisterAccount();
				}
			}
		} else {
			jumpMainView();
		}
		loadAppLuncharBg();
	}

	/**
	 * 游客进入
	 */
	private void guestRegisterAccount() {
		String account = SystemContext.getInstance().getGuestAccount();
		String password = SystemContext.getInstance().getGuestPassword();
		if (NetworkUtil.isNetworkAvailable(this)) {
			if (null != account && null != password) {// 已快速进入过，无需再调用注册接口
				// 登录
				login(account, password);
			} else {// 第一次进入 快速注册
				registerGuest();
			}
		} else {
			LogUtil.d(TAG,"--注册前网络不可用！");
			ToastUtil.showToast(this, getString(R.string.network_error));
			isNeedExit = true;
		}
	}


	/**
	 * 退出app
	 */
	private void exitApp() {
		Intent intent = new Intent(this, MainFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -1);
		intent.putExtras(bundle);
		this.startActivity(intent);
		this.finish();
	}

	/**
	 * 登录
	 * 
	 * @param account
	 * @param password
	 */
	private void login(final String account, final String password) {
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					Intent intent = new Intent(LaunchActivity.this, LoadDataAcitvity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
					intent.putExtras(bundle);
					startActivity(intent);
					LaunchActivity.this.finish();
					break;
				default:
					isNeedExit = true;
					ToastUtil.showToast(LaunchActivity.this, "登录失败，请检查网络，稍后再试");
					break;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "登录请求失败：code=" + result + ", msg=" + resultMsg);
				isNeedExit = true;
				switch (result) {
				case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_not_exist), 1000);
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_INVALID_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_password_invalid), 1000);
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_killed), 1000);
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_locked), 1000);
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_login_ip_invalid), 1000);
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_info_uid_blank), 1000);
					break;
				case com.iwgame.msgs.common.ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR://session错误处理
					showSessionStatusErrorDialog();
					break;
				default:
					if (resultMsg != null && !resultMsg.isEmpty())
						ErrorCodeUtil.handleErrorCode(LaunchActivity.this, result, resultMsg);
					else
						ToastUtil.showToast(LaunchActivity.this, "登录失败，请检查网络，稍后再试");
					break;
				}

			}
		}, this, account, password);
	}

	/**
	 * 游客注册
	 */
	private void registerGuest() {
		ProxyFactory.getInstance().getAccountProxy().registerGuestAccount(new ProxyCallBack<GuestObject>() {

			@Override
			public void onSuccess(GuestObject result) {
				if (result != null) {
					accountName = result.getAccount();
					password = result.getPassword();
					SystemContext.getInstance().setGuestAccount(accountName);
					SystemContext.getInstance().setGuestPasssword(password);
					SystemContext.getInstance().setRecommendGameTag(true);

					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, accountName);
					MobclickAgent.onEvent(LaunchActivity.this, UMConfig.MSGS_EVENT_USER_REGISTER, ummap);

					// 登录
					login(accountName, password);
				} else {
					isNeedExit = true;
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_not_success));
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				switch (result) {
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_BLANK_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_register_nickname_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_INVALID_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_register_nickname_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_INVALID_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_register_account_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_BLANK_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_register_account_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_PASSWORD_BLANK_VALUE:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_register_password_blank));
					break;
				case ErrorCode.EC_RESULT_ISNULL:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_account_not_success));
					break;
				default:
					ToastUtil.showToast(LaunchActivity.this, getString(R.string.ec_service_error));
					break;
				}
				isNeedExit = true;
				LogUtil.e(TAG, "注册设置详细信息失败：" + result);
			}
		}, this, SystemContext.UMENG_CHANNEL);
	}

	/**
	 * 跳转到主界面
	 */
	private void jumpMainView() {
		Intent intent = new Intent(LaunchActivity.this, MainFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
		intent.putExtras(bundle);
		startActivity(intent);
		LaunchActivity.this.finish();
	}
	
	/**
	 * 显示登录失败重试对话框
	 */
	private void showSessionStatusErrorDialog() {
		statusErrordialog = new Dialog(this, R.style.SampleTheme_Light){
			@Override
			public boolean onKeyDown(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				return super.onKeyDown(keyCode, event);
			}
			
		};
		statusErrordialog.setCanceledOnTouchOutside(false);
		statusErrordialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		statusErrordialog.setContentView(R.layout.login_error_dialog);
		statusErrordialog.findViewById(R.id.title).setVisibility(View.GONE);
		statusErrordialog.findViewById(R.id.dialogLine).setVisibility(View.GONE);
		
		// 添加确定按钮功能
		final Button commitBtn = (Button) statusErrordialog.findViewById(R.id.commitBtn);
		// 添加取消按钮功能
		final Button cannelBtn = (Button) statusErrordialog.findViewById(R.id.cannelBtn);
		
		/**
		 * 进度提示
		 */
		final ImageView loadingImageView = (ImageView) statusErrordialog.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) loadingImageView.getBackground();
		animationDrawable.start();
		
		final TextView login_error_tips = (TextView) statusErrordialog.findViewById(R.id.login_error_tips);
		final Chronometer login_error_reg_chronometer = (Chronometer) statusErrordialog.findViewById(R.id.login_error_reg_chronometer);
		login_error_reg_chronometer.setOnChronometerTickListener(new OnChronometerTickListener() {
			
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				int count = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
				if (count >= escapeCount) {
					login_error_reg_chronometer.stop();
					login_error_reg_chronometer.setVisibility(View.GONE);
					commitBtn.setVisibility(View.VISIBLE);
					commitBtn.setEnabled(true);
				} else {
					chronometer.setText((escapeCount - count) + "s");
				}
			}
		});

		commitBtn.setText("重试");
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				commitBtn.setEnabled(false);
				getUserInfoHasLogin(loadingImageView, login_error_tips, login_error_reg_chronometer, commitBtn);
			}
		});

		cannelBtn.setText("取消");
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(SystemContext.getInstance().getToken() != null){
					logout(SystemContext.getInstance().getToken(), loadingImageView);
				}
			}
		});

		statusErrordialog.show();
	}
	
	/**
	 * 退出登录
	 * @param token
	 * @param loadingImageView
	 */
	private void logout(String token, final ImageView loadingImageView) {
		loadingImageView.setVisibility(View.VISIBLE);
		// 调用服务端注销功能
		ProxyFactory.getInstance().getAccountProxy().logout(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					LogUtil.e(TAG, "退出登录成功");
					break;
				default:
					LogUtil.e(TAG, "退出登录失败:" + result);
					break;
				}
				loadingImageView.setVisibility(View.GONE);
				statusErrordialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "退出登录失败:" + result);
				loadingImageView.setVisibility(View.GONE);
				statusErrordialog.dismiss();
			}
		}, this, token);
	}

	/**
	 * 重试获取用户信息
	 * @param loadingImageView
	 * @param login_error_tips
	 * @param login_error_reg_chronometer
	 * @param commitBtn
	 */
	private void getUserInfoHasLogin(final ImageView loadingImageView, final TextView login_error_tips, final Chronometer login_error_reg_chronometer, final Button commitBtn){
		loadingImageView.setVisibility(View.VISIBLE);
		ProxyFactory.getInstance().getAccountProxy().getUserInfoHasLogin(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				loadingImageView.setVisibility(View.GONE);
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					statusErrordialog.dismiss();
					Intent intent = new Intent(LaunchActivity.this, LoadDataAcitvity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
					intent.putExtras(bundle);
					startActivity(intent);
					LaunchActivity.this.finish();
					break;
				default:
					commitBtn.setVisibility(View.GONE);
					login_error_reg_chronometer.setVisibility(View.VISIBLE);
					login_error_reg_chronometer.setBase(SystemClock.elapsedRealtime());
					login_error_reg_chronometer.start();
					break;
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				loadingImageView.setVisibility(View.GONE);
				commitBtn.setVisibility(View.GONE);
				login_error_reg_chronometer.setVisibility(View.VISIBLE);
				login_error_reg_chronometer.setBase(SystemClock.elapsedRealtime());
				login_error_reg_chronometer.start();
			}
		}, LaunchActivity.this, accountName, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(isNeedExit){
				exitApp();
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(luncher_bg != null && !luncher_bg.isRecycled()){ 
			// 回收并且置为null
			luncher_bg.recycle(); 
			luncher_bg = null; 
		} 
		//System.gc();
	}

}
