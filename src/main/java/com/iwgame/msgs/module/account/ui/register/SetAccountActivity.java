/**      
 * RegisterActivity.java Create on 2013-7-29     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.register;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.service.MessageService;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.youban.msgs.R;

/**
 * @ClassName: SetAccountActivity
 * @Description: 注册电话号码 获取验证码
 * @author 王卫
 * @date 2013-7-30 下午2:29:24
 * @Version 1.0
 * 
 */
public class SetAccountActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener {
	
	private static final String TAG = "SetAccountActivity";
	// 电话号码输入框
	private EditText accountTxt;
	// 确认按钮（下一步）
	private Button confirmBtn;
	// 清除号码安妮
	private Button cleanBtn;
	private TextView setaccount_hint;
	// 协议勾选复选框
	private CheckBox checkBox;
	// 电话号码
	private String mobileNum = null;
	
	private LinearLayout bundphone_prompt_layout;
	
	private RelativeLayout setaccount_login_layout;
	
	private Button setaccount_login_tv;
	
	// 允许输入的手机号码最大长度
	private static final int mobileNumLength = 11;
	
	private String bundType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获得传入的参数
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getExtras();
			if (tmpbundle != null) {
				bundType = tmpbundle.getString("BundPhoneType");
			}
		}
		initialize();
	}

	/**
	 * 初始化
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 显示右边
		setRightVisible(true);
		TextView tv = new TextView(this);
		tv.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tv.setGravity(Gravity.CENTER);
		addRightView(tv);
		tv.setText("1/4");
		
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.account_register_set_account, null), params);
		// 设置账号文本框
		accountTxt = (EditText) findViewById(R.id.act_reg_accountTxt);
		accountTxt.setOnFocusChangeListener(this);
		setaccount_hint = (TextView) findViewById(R.id.setaccount_hint);
		bundphone_prompt_layout = (LinearLayout) findViewById(R.id.bundphone_prompt_layout);
		setaccount_login_layout = (RelativeLayout) findViewById(R.id.setaccount_login_layout);
		setaccount_login_tv = (Button) findViewById(R.id.setaccount_login_tv);
		setaccount_login_tv.setOnClickListener(this);
		setaccount_login_tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); 
		setaccount_login_tv.getPaint().setAntiAlias(true);
		if(bundType != null && bundType.equals("Register")){
			pageName = "com.iwgame.msgs.module.account.ui.register.SetAccountActivity";
			// 设置TITLE(注册)
			setTitleTxt(getString(R.string.title_set_account_activity));
			setaccount_hint.setText(getString(R.string.title_set_account_hint));
			bundphone_prompt_layout.setVisibility(View.GONE);
		}else{
			pageName = "com.iwgame.msgs.module.account.ui.bundphone.SetAccountActivity";
			// 设置TITLE（绑定手机）
			setTitleTxt(getString(R.string.title_bund_phone_activity));
			setaccount_hint.setText(getString(R.string.title_bund_phone_hint));
			bundphone_prompt_layout.setVisibility(View.VISIBLE);
			if(AdaptiveAppContext.getInstance().getAppConfig().isBundingLoginBTIsShow()){
				setaccount_login_layout.setVisibility(View.VISIBLE);
			}
		}
		// 设置确认按钮
		confirmBtn = (Button) findViewById(R.id.act_reg_accountBtn);
		confirmBtn.setOnClickListener(this);
		// 设置清除按钮
		cleanBtn = (Button) findViewById(R.id.act_reg_cleanNumBtn);
		cleanBtn.setOnClickListener(this);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(accountTxt, cleanBtn);
		// 设置复选框
		checkBox = (CheckBox) findViewById(R.id.act_reg_checkBox);
		findViewById(R.id.sproto).setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// 提交手机号码(需要校验)
		if (v.getId() == R.id.act_reg_accountBtn) {
			if (accountTxt != null && !accountTxt.getText().toString().isEmpty()) {
				if (accountTxt.getText().toString().length() == mobileNumLength) {
					if (checkBox.isChecked()) {
						mobileNum = accountTxt.getText().toString();
						if (NetworkUtil.isNetworkAvailable(this)) {
							putMobileNumber(mobileNum);
						} else {
							ToastUtil.showToast(this, getString(R.string.network_error));
						}
					} else {
						ToastUtil.showToast(this, getString(R.string.reg_checkbox_verify_fail));
					}
				} else {
					ToastUtil.showToast(this, getString(R.string.num_verify_fail));
				}
			} else {
				ToastUtil.showToast(this, getString(R.string.num_verify_isnull));
			}
		} else if (v.getId() == R.id.act_reg_cleanNumBtn) {
			accountTxt.setText("");
		} else if (v.getId() == R.id.sproto) {// 服务条款
			jumpProtocolAct("服务条款", SystemConfig.PROTOCOL_SERVICE);
		} else if(v.getId() == R.id.setaccount_login_tv){
			logout(SystemContext.getInstance().getToken(), false);
		}
	}

	/**
	 * 进入服务条款和隐私政策协议页面
	 * @param title 标题
	 * @param url   链接
	 */
	private void jumpProtocolAct(String title, String url) {
		Intent intent = new Intent(SetAccountActivity.this, ProtocolAcitivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TITLE, title);
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_URL, url);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 异步发送手机号码请求获取验证码
	 * 
	 * @param mobileNum
	 * @return
	 */
	private void putMobileNumber(final String mobileNum) {
		confirmBtn.setEnabled(false);
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getAccountProxy().getCaptcha(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				confirmBtn.setEnabled(true);
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					startNewActivity();
					break;
				default:
					ToastUtil.showToast(SetAccountActivity.this, getString(R.string.ec_service_error));
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				confirmBtn.setEnabled(true);
				switch (result) {
				case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_INVALID_VALUE:
					ToastUtil.showToast(SetAccountActivity.this, getString(R.string.ec_captcha_phone_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_OVERCOUNT_VALUE:
					ToastUtil.showToast(SetAccountActivity.this, getString(R.string.ec_captcha_overcount));
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_REGISTED_VALUE://手机号码已注册
					if("Register".equals(bundType)){//注册时的提示
						ToastUtil.showToast(SetAccountActivity.this, getString(R.string.ec_captcha_phone_unregisted));
					}else{//游客绑定手机时的提示
						showPhoneAlreadyRegisterDialog();
					}
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_SMS_SERVICE_NOT_EXISTED_VALUE:
					ToastUtil.showToast(SetAccountActivity.this, getString(R.string.ec_captcha_sms_service_not_existed));
					startNewActivity();
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_SMS_SEND_ERROR_VALUE:
					ToastUtil.showToast(SetAccountActivity.this, getString(R.string.ec_captcha_sms_send_error));
					break;
				default:
					ToastUtil.showToast(SetAccountActivity.this, getString(R.string.ec_service_error));
					break;
				}
				dialog.dismiss();
				LogUtil.e(TAG, "注册账号失败：" + result);
			}
		}, this, mobileNum, 0);
	}

	/**
	 * 显示手机号码已经被注册的提示框
	 */
	private void showPhoneAlreadyRegisterDialog(){
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final TextView txt = new TextView(this);
		txt.setSingleLine(false);
		txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(getResources().getString(R.string.bundphone_ready_register_tip));
		txt.setGravity(Gravity.CENTER);
		content.setGravity(Gravity.CENTER);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		dialog.findViewById(R.id.title).setVisibility(View.GONE);
		dialog.findViewById(R.id.dialogLine).setVisibility(View.GONE);
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setText("确定");
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logout(SystemContext.getInstance().getToken(), false);
//				Intent intent = new Intent(SetAccountActivity.this, LoginActivity.class);
//				startActivity(intent);
//				SetAccountActivity.this.finish();
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}
	
	/**
	 * 退出、注销账号(清除全局配置用户信息、停止相关服务)
	 */
	private void logout(String token,boolean flag) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(SetAccountActivity.this);
		dialog.show();
		// 调用服务端注销功能
		ProxyFactory.getInstance().getAccountProxy().logout(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				dialog.dismiss();
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					LogUtil.e(TAG, "退出登录成功");
					jumpMainView();
					break;
				default:
					LogUtil.e(TAG, "退出登录失败:" + result);
					jumpMainView();
					break;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				dialog.dismiss();
				LogUtil.e(TAG, "退出登录失败:" + result);
				jumpMainView();
			}
		}, this, token);
	}
	
	/**
	 * 跳转至主界面然后跳转到登录界面
	 */
	private void jumpMainView() {
		// 处理返回结果，成功返回后跳转到主界面
		SystemContext.getInstance().setPasssword(null);
		// 停止相关服务
		Intent serviceIntent = new Intent(SetAccountActivity.this, MessageService.class);
		SetAccountActivity.this.stopService(serviceIntent);
		// 清除全局配置
		SystemContext.getInstance().cleanContext();
		
		Intent intent = new Intent(this, MainFragmentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -5);
		bundle.putInt("Type", 1);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	
	/**
	 * 打开新的活动窗口
	 */
	private void startNewActivity() {
		// 跳转到输入验证码和密码界面
		Intent intent = new Intent(SetAccountActivity.this, VerifyCaptchaActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM, mobileNum);
		if(bundType != null){
			bundle.putString("BundPhoneType", bundType);
		}
		intent.putExtras(bundle);
		startActivity(intent);
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
		if (v == accountTxt) {
			if (hasFocus) {
				if (accountTxt.getText().length() > 0) {
					cleanBtn.setVisibility(View.VISIBLE);
				}
			} else {
				cleanBtn.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	/**
	 * 返回结束流程
	 */
	private void actionComplete(){
		Intent intent = new Intent(this, BundPhoneActivity.class);   
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
		startActivity(intent); 
	}
	
	@Override   
    public boolean onKeyDown(int keyCode, KeyEvent event) { 
    	if(keyCode == KeyEvent.KEYCODE_BACK){ 
    		if(bundType != null && bundType.equals("Register")){
				finish();
			}else{
				actionComplete();
			}
    		return  true;
    	} 
    	return  super.onKeyDown(keyCode, event);     

    } 
}
