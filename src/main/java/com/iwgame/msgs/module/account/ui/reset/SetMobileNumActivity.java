/**      
 * RegisterActivity.java Create on 2013-7-29     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.reset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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
 * @ClassName: SetMobileNumActivity
 * @Description: 找回密码输入手机号码
 * @author 王卫
 * @date 2013-8-12 上午午10:12:24
 * @Version 1.0
 * 
 */
public class SetMobileNumActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "SetMobileNumActivity";
	// 电话号码输入框
	private EditText mobileNumTxt;
	// 确认按钮
	private Button confirmBtn;
	// 清除号码按钮
	private Button cleanBtn;
	// 电话号码值
	private String mobileNum;
	// 允许输入的手机号码最大长度
	private static final int mobileNumLength = 11;

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

	/**
	 * 初始化
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		// 设置TITLE
		setTitleTxt(getString(R.string.title_set_mobilenum_activity));
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.account_findpws_get_capach, null), params);
		// 设置电话号码输入框
		mobileNumTxt = (EditText) findViewById(R.id.act_lookuppw_mobileNumTxt);
		// 设置确认按钮
		confirmBtn = (Button) findViewById(R.id.act_lookuppw_nextstepBtn);
		confirmBtn.setOnClickListener(this);
		// 设置清除号码按钮
		cleanBtn = (Button) findViewById(R.id.act_lookuppw_cleanNumBtn);
		cleanBtn.setOnClickListener(this);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(mobileNumTxt, cleanBtn);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		confirmBtn.setClickable(true);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// 处理点击下一步事件，提交手机号码(需要校验)
		if (v.getId() == R.id.act_lookuppw_nextstepBtn) {
			mobileNum = mobileNumTxt.getText().toString();
			if (mobileNum != null && !mobileNum.isEmpty()) {
				if (mobileNum.length() == mobileNumLength) {
					if (NetworkUtil.isNetworkAvailable(this)) {
						confirmBtn.setClickable(false);
						putMobileNumber(mobileNum);
					} else {
						ToastUtil.showToast(this, getString(R.string.network_error));
					}
				} else {
					ToastUtil.showToast(this, getString(R.string.num_verify_fail));
				}
			} else {
				ToastUtil.showToast(this, getString(R.string.num_verify_isnull));
			}
		}
		// 处理点击清除电话号码事件
		else if (v.getId() == R.id.act_lookuppw_cleanNumBtn) {
			mobileNumTxt.setText("");
		}
	}

	/**
	 * 异步发送手机号码请求获取验证码
	 * 
	 * @param mobileNum
	 * @return
	 */
	private void putMobileNumber(final String mobileNum) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getAccountProxy().getCaptcha(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					startNewActivity();
					break;
				
				default:
					confirmBtn.setClickable(true);
					ToastUtil.showToast(SetMobileNumActivity.this, getString(R.string.ec_account_not_exist));
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				confirmBtn.setClickable(true);
				switch (result) {
				case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_INVALID_VALUE:
					ToastUtil.showToast(SetMobileNumActivity.this, getString(R.string.ec_captcha_phone_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_OVERCOUNT_VALUE:
					ToastUtil.showToast(SetMobileNumActivity.this, getString(R.string.ec_captcha_overcount));
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_REGISTED_VALUE:
					ToastUtil.showToast(SetMobileNumActivity.this, getString(R.string.ec_captcha_phone_unregisted));
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_SMS_SERVICE_NOT_EXISTED_VALUE:
					ToastUtil.showToast(SetMobileNumActivity.this, getString(R.string.ec_captcha_sms_service_not_existed));
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_SMS_SEND_ERROR_VALUE:
					ToastUtil.showToast(SetMobileNumActivity.this, getString(R.string.ec_captcha_sms_send_error));
					break;
				default:
					ToastUtil.showToast(SetMobileNumActivity.this, getString(R.string.ec_account_not_exist));
					break;
				}
				LogUtil.e(TAG, "获取验证码请求失败：" + result);
				dialog.dismiss();
			}
		}, this, mobileNum, 1);
	}

	/**
	 * 打开设置验证码窗口
	 */
	private void startNewActivity() {
		// 跳转到输入验证码和密码界面
		Intent intent = new Intent(SetMobileNumActivity.this, VerifyCaptchaActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM, mobileNum);
		intent.putExtras(bundle);
		startActivity(intent);
	}

}
