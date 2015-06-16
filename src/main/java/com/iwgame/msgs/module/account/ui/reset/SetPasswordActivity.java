/**      
 * LookUpPWSetPasswordActivity.java Create on 2013-8-12     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.reset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.iwgame.msgs.MainFragmentActivity;
import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;

/**
 * @ClassName: LookUpPWSetPasswordActivity
 * @Description: 找寻密码重新设置密码
 * @author 王卫
 * @date 2013-8-12 上午10:51:46
 * @Version 1.0
 * 
 */
public class SetPasswordActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener {

	protected static final String TAG = "LookUpPWSetPasswordActivity";
	// 密码文本框
	private EditText passwordTxt;
	// 二次密码确认框
	private EditText password2Txt;
	// 密码清除按钮
	private Button cleanPswdBtn;
	// 清除二次密码验证按钮
	private Button cleanPswd2Btn;
	// 设置密码按钮
	private Button commitBtn;
	// 电话号码
	private String mobileNum;
	// 验证码
	private String captcha;
	// 密码
	private String password;
	// 二次密码
	private String confimpassword;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		// 设置TITLE
		setTitleTxt(getString(R.string.title_reset_password_activity));
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.account_findpws_setpassword, null), params);
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mobileNum = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM);
			captcha = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CAPTCHA);
		}
		// 设置密码文本框
		passwordTxt = (EditText) findViewById(R.id.act_lookup_pw_passwordTxt);
		passwordTxt.setOnFocusChangeListener(this);
		// 设置二次密码确认框
		password2Txt = (EditText) findViewById(R.id.act_lookup_pw_confimPasswordTxt);
		password2Txt.setOnFocusChangeListener(this);
		// 设置密码清除按钮
		cleanPswdBtn = (Button) findViewById(R.id.act_lookup_pw_cleanpwBtn);
		cleanPswdBtn.setOnClickListener(this);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(passwordTxt, cleanPswdBtn);
		// 设置清除二次密码验证按钮
		cleanPswd2Btn = (Button) findViewById(R.id.act_lookup_pw_cleanConfimpwBtn);
		cleanPswd2Btn.setOnClickListener(this);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(password2Txt, cleanPswd2Btn);
		// 设置提交密码按钮
		commitBtn = (Button) findViewById(R.id.act_lookup_pw_setPasswordBtn);
		commitBtn.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		commitBtn.setClickable(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.act_lookup_pw_setPasswordBtn) {
			password = passwordTxt.getText().toString();
			confimpassword = password2Txt.getText().toString();
			if (password != null && !password.isEmpty() && confimpassword != null && !confimpassword.isEmpty()) {
				if (!password.matches("[0-9]+")) {
					if (password.equals(confimpassword)) {
						if (password.length() >= SystemConfig.PASSWORD_MIN_LENGTH && password.length() <= SystemConfig.PASSWORD_MAX_LENGTH
								&& confimpassword.length() >= SystemConfig.PASSWORD_MIN_LENGTH
								&& confimpassword.length() <= SystemConfig.PASSWORD_MAX_LENGTH) {
							if (NetworkUtil.isNetworkAvailable(this)) {
								commitBtn.setClickable(false);
								resetPassword(mobileNum, password, captcha);
							} else {
								ToastUtil.showToast(this, getString(R.string.network_error));
							}
						} else if(password.length() < SystemConfig.PASSWORD_MIN_LENGTH){
							ToastUtil.showToast(this, "密码长度不能少于6位哦");
						} else{
							ToastUtil.showToast(this, "密码长度不能多于16位哦");
						}
					} else {
						ToastUtil.showToast(this, "前后密码输入不一致，请重新输入！");
					}
				} else {
					ToastUtil.showToast(this, "密码不能为纯数字哦，需要为字母或数字字母的组合哦！");
				}
			} else {
				ToastUtil.showToast(this, "您未输入密码或确认密码");
			}
		} else if (v.getId() == R.id.act_lookup_pw_cleanpwBtn) {
			passwordTxt.setText("");
			password2Txt.setText("");
		} else if (v.getId() == R.id.act_lookup_pw_cleanConfimpwBtn) {
			password2Txt.setText("");
		}
	}

	private void resetPassword(final String account, final String password, final String captcha) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getAccountProxy().resetPassword(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					login(account, password);
					break;
				
				default:
					commitBtn.setClickable(true);
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_service_error));
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				commitBtn.setClickable(true);
				dialog.dismiss();
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					login(account, password);
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_RESET_PASS_BLANK_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_password_reset_pass_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_RESET_ACCOUNT_BLANK_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_password_reset_account_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_RESET_ACCOUNT_NOTFOUND_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_password_account_notfound));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_RESET_FAIELD_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_password_reset_faield));
					break;
				default:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_service_error));
					break;
				}
				LogUtil.e(TAG, "重置密码请求失败：" + result);
			}
		}, this, account, password, captcha);
	}

	/**
	 * 密码重置
	 * 
	 * @param account
	 * @param password
	 */
	private void login(final String account, final String password) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					Intent intent = new Intent(SetPasswordActivity.this, MainFragmentActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
					intent.putExtras(bundle);
					startActivity(intent);
					SetPasswordActivity.this.finish();
					break;
				
				default:
					commitBtn.setClickable(true);
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				commitBtn.setClickable(true);
				LogUtil.e(TAG, "登录请求失败" + result);
				switch (result) {
				case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_not_exist));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_INVALID_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_password_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_killed));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_locked));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_login_ip_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
					ToastUtil.showToast(SetPasswordActivity.this, getString(R.string.ec_account_info_uid_blank));
					break;
				default:
					break;
				}
				dialog.dismiss();
			}
		}, this, account, password);
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
		if (v == passwordTxt) {
			if (hasFocus) {
				if (passwordTxt.getText().length() > 0) {
					cleanPswdBtn.setVisibility(View.VISIBLE);
				}
			} else {
				cleanPswdBtn.setVisibility(View.INVISIBLE);
			}
		} else if (v == password2Txt) {
			if (hasFocus) {
				if (password2Txt.getText().length() > 0) {
					cleanPswd2Btn.setVisibility(View.VISIBLE);
				}
			} else {
				cleanPswd2Btn.setVisibility(View.INVISIBLE);
			}
		}
	}

}
