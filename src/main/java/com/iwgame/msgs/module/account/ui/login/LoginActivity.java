/**      
 * LoginActivity.java Create on 2013-7-29     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.login;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

import com.iwgame.msgs.BaseApplication;
import com.iwgame.msgs.LoadDataAcitvity;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.ShareSdkManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.UserMainActivity;
import com.iwgame.msgs.module.account.ui.register.SetAccountActivity;
import com.iwgame.msgs.module.account.ui.reset.SetMobileNumActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.service.MessageService;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.widget.CircleImageView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.BitmapUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.youban.msgs.R;

/**
 * @ClassName: LoginActivity
 * @Description: 登录界面
 * @author 王卫
 * @date 2013-7-30 下午5:05:49
 * @Version 1.0
 * 
 */
public class LoginActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener {

	private static final String TAG = "LoginActivity";

	// 账号文本框
	private EditText accountTxt;
	// 密码文本框
	private EditText pwTxt;
	// 清除账号按钮
	private Button cleanAccountBtn;
	// 清除密码按钮
	private Button cleanpwBtn;
	// 登录按钮
	private Button loginBtn;
	// 注册按钮
	private TextView registerBtn;
	// 忘记密码查找密码按钮
	private TextView lookupPasswordBtn;
	// QQ登录按钮
	private ImageView loginQqBtn;
	// weibo登录按钮
	private ImageView loginWeiboBtn;
	
	private CircleImageView user_circle_icon;

	private boolean isClickLoginBtn = false;

	private int errorCount = 0;

	private String currentCount;
	
	private Bitmap bitmap;

	// 倒计时次数
	private static final int escapeCount = 5;
	
	private Dialog statusErrordialog;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		errorCount = 0;
		loginBtn.setEnabled(true);
		loginQqBtn.setEnabled(true);
		loginWeiboBtn.setEnabled(true);
		registerBtn.setEnabled(true);
		lookupPasswordBtn.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseApplication app = (BaseApplication) getApplication();
		app.finishActivity();

		// 取消状态栏上的通知
		NotificationManager nm = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		nm.cancel(SystemConfig.NOTIFICATION_ID_BASE);
		// 设置是否打开消息全局变量
		SystemContext.getInstance().setOpenMessage(false);
		// 停止相关服务
		Intent intent = new Intent(LoginActivity.this, MessageService.class);
		this.stopService(intent);
		// 清除全局配置
		SystemContext.getInstance().cleanContext();

		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			if ((boolean) bundle.getBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED)) {
				creatUnauthenticatedDialog("踢下线提醒", getString(R.string.global_login_other));
				SystemContext.getInstance().setUnAuth(false);
			} else if (bundle.getBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEALACCOUNT)) {
				String desc = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_DESC);
				if (desc != null) {
					creatUnauthenticatedDialog("封号提醒", desc);
				}
			}
		}
		// 显示左边
		setLeftVisible(AdaptiveAppContext.getInstance().getAppConfig().isLogin_backButtonIsShow());
		// 隐藏右边
		setRightVisible(false);
		// 设置TITLE
		setTitleTxt(getString(R.string.login_title));
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.account_login, null), params);
		
		user_circle_icon = (CircleImageView) findViewById(R.id.user_circle_icon);
		bitmap = BitmapUtil.getBitmapFromData(LoginActivity.this, SystemConfig.LOGIN_USER_ICON_NAME, "UTF-8");
		if(bitmap != null){
			user_circle_icon.setImageBitmap(bitmap);
		}else{
			user_circle_icon.setImageResource(R.drawable.ic_launcher);
		}
		
		
		// 设置密码输入框
		pwTxt = (EditText) findViewById(R.id.act_login_passwordTxt);
		pwTxt.setOnFocusChangeListener(this);
		String password = SystemContext.getInstance().getPassword();
		if (password != null && !password.isEmpty() && SystemContext.getInstance().getLoginStatus() == 0)
			pwTxt.setText(password);
		// 设置账号文本输入框
		accountTxt = (EditText) findViewById(R.id.act_login_accountTxt);
		accountTxt.setOnFocusChangeListener(this);
		String mobile = SystemContext.getInstance().getMobile();
		if (mobile != null && !mobile.isEmpty()) {
			if (SystemContext.getInstance().getIsGuest() != SystemConfig.LOGIN_TARGET_NORMAL ) {//非正式账号登录，不显示登录账号
				accountTxt.setText("");
			} else {
				accountTxt.setText(mobile);
			}
		}
		accountTxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				pwTxt.setText("");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		// 设置清除账号按钮
		cleanAccountBtn = (Button) findViewById(R.id.act_login_cleanAccountBtn);
		cleanAccountBtn.setOnClickListener(this);
		// 设置清除密码按钮
		cleanpwBtn = (Button) findViewById(R.id.act_login_cleanpwBtn);
		cleanpwBtn.setOnClickListener(this);
		// 设置登录按钮
		loginBtn = (Button) findViewById(R.id.act_loginBtn);
		loginBtn.setOnClickListener(this);
		// 设置注册按钮
		registerBtn = (TextView) findViewById(R.id.act_login_registerBtn);
		registerBtn.setOnClickListener(this);
		// 设置忘记密码按钮
		lookupPasswordBtn = (TextView) findViewById(R.id.act_login_lookpasswordBtn);
		lookupPasswordBtn.setOnClickListener(this);
		//QQ登录按钮
		loginQqBtn = (ImageView) findViewById(R.id.login_qqBtn);
		loginQqBtn.setOnClickListener(this);
		//weibo登录按钮
		loginWeiboBtn = (ImageView) findViewById(R.id.login_weiboBtn);
		loginWeiboBtn.setOnClickListener(this);
		
		// 设置文本输入框文本变化监听
		if (accountTxt.getText() != null && accountTxt.getText().length() > 0)
			cleanAccountBtn.setVisibility(View.VISIBLE);
		EditTextUtil.ChangeCleanTextButtonVisible(accountTxt, cleanAccountBtn);
		EditTextUtil.ChangeCleanTextButtonVisible(pwTxt, cleanpwBtn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// 登录
		if (v.getId() == R.id.act_loginBtn) {
			if(FastClickLimitUtil.isFastClick()){
				return;
			}
			String account = accountTxt.getText().toString();
			String password = pwTxt.getText().toString();
			// 校验
			if (NetworkUtil.isNetworkAvailable(this)) {
				if (account == null || account.isEmpty()) {
					ToastUtil.showToast(this, getString(R.string.login_verify_account_isnull));
				} else {
					if (account.length() < 11) {
						ToastUtil.showToast(this, getString(R.string.login_verify_account_verify_fail));
					} else {
						if (password == null || password.isEmpty()) {
							ToastUtil.showToast(this, getString(R.string.login_verify_password_isnull));
						} else {
							login(account, password);
						}
					}
				}
			} else {
				ToastUtil.showToast(this, getString(R.string.network_error));
			}
		}
		// 注册
		else if (v.getId() == R.id.act_login_registerBtn) {
			if(FastClickLimitUtil.isFastClick()){
				return;
			}
			registerBtn.setEnabled(false);
			Intent intent = new Intent(LoginActivity.this, SetAccountActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("BundPhoneType", "Register");
			intent.putExtras(bundle);
			startActivity(intent);
		}
		// 找回密码
		else if (v.getId() == R.id.act_login_lookpasswordBtn) {
			if(FastClickLimitUtil.isFastClick()){
				return;
			}
			lookupPasswordBtn.setEnabled(false);
			Intent intent = new Intent(LoginActivity.this, SetMobileNumActivity.class);
			startActivity(intent);
		}
		// 清除账号
		else if (v.getId() == R.id.act_login_cleanAccountBtn) {
			accountTxt.setText("");
			pwTxt.setText("");
		}
		// 清除密码
		else if (v.getId() == R.id.act_login_cleanpwBtn) {
			pwTxt.setText("");
		}
		// QQ登录
		else if(v.getId() == R.id.login_qqBtn){
			if(FastClickLimitUtil.isFastClick()){
				return;
			}
			loginBtn.setEnabled(false);
			loginQqBtn.setEnabled(false);
			loginWeiboBtn.setEnabled(false);
			if (ShareSdkManager.getInstance().getAuthorizeStatus(this, QQ.NAME)) {
				ShareManager.getInstance().authorize(this, 1, QQ.NAME, new ShareCallbackListener() {});
			}
			
			ShareManager.getInstance().authorize(this, 0, QQ.NAME, new ShareCallbackListener() {
				@Override
				public void doSuccess(String plamType) {
					if(QQ.NAME.equals(plamType)){
						String userId = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getUserId();
						String qqToken = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getToken();
						qqLogin(qqToken, userId);
					}
				}
				@Override
				public void doFail() {
				}
			});
			
		}
		// 微博登录
		else if (v.getId() == R.id.login_weiboBtn){
			if(FastClickLimitUtil.isFastClick()){
				return;
			}
			loginBtn.setEnabled(false);
			loginQqBtn.setEnabled(false);
			loginWeiboBtn.setEnabled(false);
			if (ShareSdkManager.getInstance().getAuthorizeStatus(this, SinaWeibo.NAME)) {
				ShareManager.getInstance().authorize(this, 1, SinaWeibo.NAME, new ShareCallbackListener() {});
			}
			ShareManager.getInstance().authorize(this, 0, SinaWeibo.NAME, new ShareCallbackListener() {
				@Override
				public void doSuccess(String plamType) {
					if(SinaWeibo.NAME.equals(plamType)){
						String userId = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getUserId();
						String weiboToken = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getToken();
						weiboLogin(weiboToken, userId);
					}
				}
				@Override
				public void doFail() {
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#back()
	 */
	@Override
	protected void back() {
		startActivity(new Intent(this, UserMainActivity.class));
		finish();
	}

	/**
	 * 登录
	 * 
	 * @param account
	 * @param password
	 */
	private void login(final String account, final String password) {
		isClickLoginBtn = true;
		loginBtn.setEnabled(false);
		loginQqBtn.setEnabled(false);
		loginWeiboBtn.setEnabled(false);
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this, false, false);
		dialog.show();
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (isClickLoginBtn) {
					switch (result) {
					case Msgs.ErrorCode.EC_OK_VALUE:
						loginBtn.setEnabled(false);
						loginQqBtn.setEnabled(false);
						loginWeiboBtn.setEnabled(false);
						Intent intent = new Intent(LoginActivity.this, LoadDataAcitvity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
						intent.putExtras(bundle);
						startActivity(intent);
						LoginActivity.this.finish();
						break;
					default:
						loginBtn.setEnabled(true);
						loginQqBtn.setEnabled(true);
						loginWeiboBtn.setEnabled(true);
						ToastUtil.showToast(LoginActivity.this, "登录失败，请检查网络，稍后再试");
						break;
					}
					dialog.dismiss();
				}
				isClickLoginBtn = false;
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (isClickLoginBtn) {
					LogUtil.e(TAG, "登录请求失败：code=" + result + ", msg=" + resultMsg);

					switch (result) {
					case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_not_exist), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_INVALID_VALUE:
						if (currentCount != null && currentCount.equals(account)) {
							errorCount++;
						} else {
							errorCount = 0;
							errorCount++;
						}
						currentCount = account;
						if (errorCount < 2) {
							ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_password_invalid), 1000);
						} else {
							ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_password_invalid2), 1000);
						}
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_killed), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_locked), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_login_ip_invalid), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_info_uid_blank), 1000);
						break;
					case com.iwgame.msgs.common.ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR://session错误
						showSessionStatusErrorDialog();
						break;
					default:
						if (resultMsg != null && !resultMsg.isEmpty())
							ErrorCodeUtil.handleErrorCode(LoginActivity.this, result, resultMsg);
						else
							ToastUtil.showToast(LoginActivity.this, "登录失败，稍后再试");
						break;
					}

					dialog.dismiss();
					loginBtn.setEnabled(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
				}
				isClickLoginBtn = false;
			}
		}, this, account, password);
	}
	
	/**
	 * 
	* @Title: qqLogin    
	* @Description: QQ登录
	* @param @param token
	* @param @param userId
	* @param @param apitype    设定文件    
	* @return void    返回类型    
	* @throws
	 */
	private void qqLogin(final String token, final String userId){
		isClickLoginBtn = true;
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				if (isClickLoginBtn) {
					switch (result) {
					case Msgs.ErrorCode.EC_OK_VALUE:
						Intent intent = new Intent(LoginActivity.this, LoadDataAcitvity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
						intent.putExtras(bundle);
						startActivity(intent);
						LoginActivity.this.finish();
						break;
					default:
						ToastUtil.showToast(LoginActivity.this, "登录失败，请检查网络，稍后再试");
						break;
					}
					loginBtn.setEnabled(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
				}
				isClickLoginBtn = false;
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (isClickLoginBtn) {
					LogUtil.e(TAG, "登录请求失败：code=" + result + ", msg=" + resultMsg);

					switch (result) {
					case XAccountInfo.ErrorCode.EC_ACCOUNT_BIND_ERROR_VALUE:
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_not_exist), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_killed), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_locked), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_login_ip_invalid), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_info_uid_blank), 1000);
						break;
					case com.iwgame.msgs.common.ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR://session错误
						//showSessionStatusErrorDialog();
						break;
					case XAccountInfo.ErrorCode.EC_API_SYSTEM_ERROR_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_system_error_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_HTTP_CONNECT_ERROR_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_http_connect_error_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCOUNT_NOT_EXISTED_VALUE:
						String userId = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getUserId();
						String icon = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getUserIcon();
						String nickname = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getUserName();
						String gender = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getUserGender();
						RegisterAccount(userId, 21, gender.equals("m")?0:1, icon, nickname);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_ILLEGAL_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_access_token_illegal_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_EXPIRED_VALUE:
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_REVOKED_VALUE:
						//ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_access_token_revoked_value), 1000);
						String userId1 = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getUserId();
						String qqToken1 = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getToken();
						qqLogin(qqToken1, userId1);
						break;
					case XAccountInfo.ErrorCode.EC_API_OPENID_NOT_EXPECTED_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_openid_not_expected_value), 1000);
						break;
					default:
						if (resultMsg != null && !resultMsg.isEmpty())
							ErrorCodeUtil.handleErrorCode(LoginActivity.this, result, resultMsg);
						else
							ToastUtil.showToast(LoginActivity.this, "登录失败，稍后再试");
						break;
					}
					loginBtn.setEnabled(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
				}
				isClickLoginBtn = false;
				
			}
		}, this, token, SystemConfig.LOGIN_THIRD_AUTH_TYPE, SystemConfig.LOGIN_QQ_WEIBO, userId);
	}
	
	/**
	 * 
	* @Title: weiboLogin    
	* @Description: 微博登录 
	* @param @param token
	* @param @param userId
	* @param @param apitype    设定文件    
	* @return void    返回类型    
	* @throws
	 */
	private void weiboLogin(final String token, final String userId){
		isClickLoginBtn = true;
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				if (isClickLoginBtn) {
					switch (result) {
					case Msgs.ErrorCode.EC_OK_VALUE:
						Intent intent = new Intent(LoginActivity.this, LoadDataAcitvity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
						intent.putExtras(bundle);
						startActivity(intent);
						LoginActivity.this.finish();
						break;
					default:
						
						ToastUtil.showToast(LoginActivity.this, "登录失败，请检查网络，稍后再试");
						break;
					}
					loginBtn.setEnabled(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
				}
				isClickLoginBtn = false;
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (isClickLoginBtn) {
					LogUtil.e(TAG, "登录请求失败：code=" + result + ", msg=" + resultMsg);

					switch (result) {
					
					case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_not_exist), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_killed), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_locked), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_login_ip_invalid), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_info_uid_blank), 1000);
						break;
					case com.iwgame.msgs.common.ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR://session错误
						//showSessionStatusErrorDialog();
						break;
					case XAccountInfo.ErrorCode.EC_API_SYSTEM_ERROR_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_system_error_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_HTTP_CONNECT_ERROR_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_http_connect_error_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCOUNT_NOT_EXISTED_VALUE:
						String userId = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getUserId();
						String icon = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getUserIcon();
						String nickname = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getUserName();
						String gender = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getUserGender();
						RegisterAccount(userId, 11, gender.equals("m")?0:1, icon, nickname);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_ILLEGAL_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_access_token_illegal_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_EXPIRED_VALUE:
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_REVOKED_VALUE:
						String userId1 = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getUserId();
						String weiboToken1 = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getToken();
						weiboLogin(weiboToken1, userId1);
						break;
					case XAccountInfo.ErrorCode.EC_API_OPENID_NOT_EXPECTED_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_openid_not_expected_value), 1000);
						break;
					default:
						if (resultMsg != null && !resultMsg.isEmpty())
							ErrorCodeUtil.handleErrorCode(LoginActivity.this, result, resultMsg);
						else
							ToastUtil.showToast(LoginActivity.this, "登录失败，稍后再试");
						break;
					}
					loginBtn.setEnabled(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
				}
				isClickLoginBtn = false;
				
			}
		}, this, token, SystemConfig.LOGIN_THIRD_AUTH_TYPE, SystemConfig.LOGIN_SINA_WEIBO, userId);
	}
	

	private void RegisterAccount(String openID, final int target, int sex, String avatar, String nickname){
		if (NetworkUtil.isNetworkAvailable(this)) {
			ProxyFactory.getInstance().getAccountProxy().registerAccount(new ProxyCallBack<Integer>() {
				
				@Override
				public void onSuccess(Integer result) {
					SystemContext.getInstance().setRecommendGameTag(true);
					if(target==11){
						String userId1 = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getUserId();
						String weiboToken1 = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, SinaWeibo.NAME).getDb().getToken();
						weiboLogin(weiboToken1, userId1);
					}else{
						String userId1 = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getUserId();
						String qqToken1 = ShareSdkManager.getInstance().getPlatform(LoginActivity.this, QQ.NAME).getDb().getToken();
						qqLogin(qqToken1, userId1);
					}
					
				}
				
				@Override
				public void onFailure(Integer result, String resultMsg) {
					switch (result) {
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_BLANK_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_register_nickname_blank));
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_INVALID_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_register_nickname_invalid));
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_INVALID_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_register_account_invalid));
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_BLANK_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_register_account_blank));
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_PASSWORD_BLANK_VALUE:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_register_password_blank));
						break;
					case ErrorCode.EC_RESULT_ISNULL:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_not_success));
						break;
					case ErrorCode.EC_CLIENT_THIRD_REGISTER_FAIL:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_account_not_success));
						break;
					default:
						ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_service_error));
						break;
					}
				}
			}, this, openID, target, sex, avatar, nickname, SystemContext.UMENG_CHANNEL);
		} else {
			loginBtn.setEnabled(true);
			loginQqBtn.setEnabled(true);
			loginWeiboBtn.setEnabled(true);
			ToastUtil.showToast(LoginActivity.this, "登录失败，请检查网络，稍后再试");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(statusErrordialog != null && statusErrordialog.isShowing()){
				return false;
			}
			if (AdaptiveAppContext.getInstance().getAppConfig().getStartup() == 1) {// 游伴流程
				startActivity(new Intent(this, UserMainActivity.class));
				finish();
			} else {// 攻略流程
				Intent intent = new Intent(this, MainFragmentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -1);
				intent.putExtras(bundle);
				this.startActivity(intent);
				this.finish();
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.View.OnFocusChangeListener#onFocusChange(android.view.View,
	 * boolean)
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v == pwTxt) {
			if (hasFocus) {
				if (pwTxt.getText().length() > 0) {
					cleanpwBtn.setVisibility(View.VISIBLE);
				}
			} else {
				cleanpwBtn.setVisibility(View.INVISIBLE);
			}
		} else if (v == accountTxt) {
			if (hasFocus) {
				if (accountTxt.getText().length() > 0) {
					cleanAccountBtn.setVisibility(View.VISIBLE);
				}
			} else {
				cleanAccountBtn.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	
	/**
	 * 显示登录失败重试对话框
	 */
	private void showSessionStatusErrorDialog() {
		//禁止系统返回键功能
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
		
		final ImageView loadingImageView = (ImageView) statusErrordialog.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) loadingImageView.getBackground();
		animationDrawable.start();
		
		final TextView login_error_tips = (TextView) statusErrordialog.findViewById(R.id.login_error_tips);
		final Chronometer login_error_reg_chronometer = (Chronometer) statusErrordialog.findViewById(R.id.login_error_reg_chronometer);
		//定时器
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
		String account = accountTxt.getText().toString().trim();
		String password = pwTxt.getText().toString().trim();
		loadingImageView.setVisibility(View.VISIBLE);
		ProxyFactory.getInstance().getAccountProxy().getUserInfoHasLogin(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
				loadingImageView.setVisibility(View.GONE);
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					statusErrordialog.dismiss();
					Intent intent = new Intent(LoginActivity.this, LoadDataAcitvity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
					intent.putExtras(bundle);
					startActivity(intent);
					LoginActivity.this.finish();
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
		}, LoginActivity.this, account, password);
	}
	
	
	/**
	 * 添加踢下线提醒对话框
	 */
	private void creatUnauthenticatedDialog(String head, String msg) {
		try {
			final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialog);
			dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
			LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
			TextView title = (TextView) dialog.findViewById(R.id.title);
			title.setText(head);
			LinearLayout bottom = (LinearLayout) dialog.findViewById(R.id.bottom);
			bottom.setVisibility(View.GONE);
			final TextView txt = new TextView(this);
			txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));
			txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			txt.setText(msg);
			content.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
			content.removeAllViews();
			content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			dialog.show();
		} catch (Exception e) {
			LogUtil.d(TAG, "login activity create dialog error" + e.toString());
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(statusErrordialog != null && statusErrordialog.isShowing()){
			statusErrordialog.dismiss();
		}
		if(bitmap != null && !bitmap.isRecycled()){ 
			// 回收并且置为null
			bitmap.recycle(); 
			bitmap = null; 
		} 
		//System.gc();
	}
	
	
}
