/**      
Ø * RegisterInforActivity.java Create on 2013-8-1     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.register;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.youban.msgs.R;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.receiver.CaptchaReceiver;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;

/**
 * @ClassName: VerifyCaptchaActivity
 * @Description: 验证码校验
 * @author 王卫
 * @date 2013-8-1 下午03:50:32
 * @Version 1.0
 * 
 */
public class VerifyCaptchaActivity extends BaseActivity implements OnClickListener, OnChronometerTickListener {

	private static final String TAG = "RegisterSetVcodeActivity";
	// 设置验证码文本框
	private EditText captchaTxt;
	// 电话号码提示显示文本框
	private TextView mobileTxt;
	// 发送验证码请求按钮
	private Button reqCaptchaBtn;
	// 计时器
	private Chronometer chronometer;
	// 验证验证码请求按钮
	private Button verifyCaptchaBtn;
	// 手机号
	private String mobileNum;
	// 验证码
	private String captcha;
	// 倒计时次数
	private static final int escapeCount = 60;
	// 验证码广播接收
	private CaptchaReceiver receiver;
	// 绑定类型（注册/游客绑定手机）
	private String bundType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		// 注册短信广播接收
		receiver = new CaptchaReceiver(this, captchaTxt);
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		this.registerReceiver(receiver, filter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		verifyCaptchaBtn.setClickable(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}
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
		tv.setText("2/4");
		addRightView(tv);
		// 设置TITLE
		setTitleTxt(getString(R.string.title_verify_captcha_activity));
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.account_register_set_captcha, null), params);
		// 设置设置验证码文本框
		captchaTxt = (EditText) findViewById(R.id.act_reg_vcodeTxt);
		// 设置电话号码提示显示文本框
		mobileTxt = (TextView) findViewById(R.id.act_reg_mobilenumTxt);
		// 设置发送验证码请求按钮
		reqCaptchaBtn = (Button) findViewById(R.id.act_reg_getvcodebtn);
		reqCaptchaBtn.setOnClickListener(this);
		// 设置定时器
		chronometer = (Chronometer) findViewById(R.id.act_reg_chronometer);
		chronometer.setOnChronometerTickListener(this);
		chronometer.setBase(SystemClock.elapsedRealtime());
		chronometer.start();
		// 设置验证验证码请求按钮
		verifyCaptchaBtn = (Button) findViewById(R.id.act_reg_vcodeBtn);
		verifyCaptchaBtn.setOnClickListener(this);
		// 设置验证验证码请求按钮
		reqCaptchaBtn.setEnabled(false);
		reqCaptchaBtn.setText("");
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		
		if (bundle != null) {
			mobileNum = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM);
			// 需要处理手机号码*号显示
			mobileTxt.setText("+86" + mobileNum.subSequence(0, 3) + "****" + mobileNum.subSequence(7, 11));
			bundType = bundle.getString("BundPhoneType");
			if(bundType != null && bundType.equals("Register")){
				pageName = "com.iwgame.msgs.module.account.ui.register.VerifyCaptchaActivity";
			}else{
				pageName = "com.iwgame.msgs.module.account.ui.bundphone.VerifyCaptchaActivity";
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// 点击验证验证码请求按钮,提交验证码（提交注册信息、需要做校验）
		if (v.getId() == R.id.act_reg_vcodeBtn) {
			captcha = captchaTxt.getText().toString();
			if (captcha != null && !captcha.isEmpty()) {
				verifyCaptchaBtn.setClickable(false);
				if (NetworkUtil.isNetworkAvailable(this)) {
					final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
					dialog.show();
					ProxyFactory.getInstance().getAccountProxy().validateCaptcha(new ProxyCallBack<Integer>() {

						@Override
						public void onSuccess(Integer result) {
							switch (result) {
							case Msgs.ErrorCode.EC_OK_VALUE:
								startNewActivity();
								break;
							default:
								verifyCaptchaBtn.setClickable(true);
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_verify_invalid));
								break;
							}
							dialog.dismiss();
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							LogUtil.e(TAG, "验证验证码失败：" + result);
							verifyCaptchaBtn.setClickable(true);
							switch (result) {
							case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_BLANK_VALUE:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_phone_blank));
								break;
							case XAccountInfo.ErrorCode.EC_CAPTCHA_CODE_BLANK_VALUE:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_code_blank));
								break;
							case XAccountInfo.ErrorCode.EC_CAPTCHA_TIMEOUT_VALUE:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_timeout));
								break;
							case XAccountInfo.ErrorCode.EC_CAPTCHA_OVERCOUNT_VALUE:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_overcount));
								break;
							case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_INVALID_VALUE:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_phone_invalid));
								break;
							case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_REGISTED_VALUE:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_phone_registed));
								break;
							case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_UNREGISTED_VALUE:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_phone_unregisted));
								break;
							case XAccountInfo.ErrorCode.EC_CAPTCHA_INVALID_VALUE:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_invalid));
								break;
							default:
								ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_verify_invalid));
								break;
							}
							dialog.dismiss();
						}
					}, this, mobileNum, captcha);
				} else {
					verifyCaptchaBtn.setClickable(true);
					ToastUtil.showToast(this, getString(R.string.network_error));
				}
			} else {
				ToastUtil.showToast(this, getString(R.string.vcode_verify_fail));
			}
		}
		// 点击获取验证验证码请求按钮
		else if (v.getId() == R.id.act_reg_getvcodebtn) {
			// 获取验证码
			if (NetworkUtil.isNetworkAvailable(this)) {
				reqCaptcha(mobileNum);
			} else {
				ToastUtil.showToast(this, getString(R.string.network_error));
			}
		}
	}

	/**
	 * 打开新的活动页面
	 */
	private void startNewActivity() {
		Intent intent = new Intent(VerifyCaptchaActivity.this, SetPasswordActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM, mobileNum);
		bundle.putString("captcha", captcha);
		if(bundType != null){
			bundle.putString("BundPhoneType", bundType);
		}
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 请求验证码
	 * 
	 * @param mobileNum
	 */
	private void reqCaptcha(final String mobileNum) {
		ProxyFactory.getInstance().getAccountProxy().getCaptcha(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					reqCaptchaBtn.setEnabled(false);
					reqCaptchaBtn.setText("");
					// 重置定时器
					chronometer.setBase(SystemClock.elapsedRealtime());
					chronometer.start();
					chronometer.setVisibility(View.VISIBLE);
					break;
				default:
					endChronometerTick();
					break;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				switch (result) {
				case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_INVALID_VALUE:
					ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_phone_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_OVERCOUNT_VALUE:
					ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_overcount));
					break;
				case XAccountInfo.ErrorCode.EC_CAPTCHA_PHONE_REGISTED_VALUE:
					ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_phone_unregisted));
					break;
				default:
					ToastUtil.showToast(VerifyCaptchaActivity.this, getString(R.string.ec_captcha_verify_invalid));
					break;
				}
				endChronometerTick();
				LogUtil.e(TAG, "注册账号失败：" + result);
			}
		}, this, mobileNum, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.Chronometer.OnChronometerTickListener#onChronometerTick
	 * (android.widget.Chronometer)
	 */
	@Override
	public void onChronometerTick(Chronometer chronometer) {
		int count = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
		if (count >= escapeCount) {
			endChronometerTick();
		} else {
			chronometer.setText((escapeCount - count) + "s");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#back()
	 */
	@Override
	protected void back() {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("提示");
		final TextView txt = new TextView(this);
		txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText("验证码短信可能略有延迟，确定取消并重新开始？");
		content.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				VerifyCaptchaActivity.this.finish();
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
	 * 定时器结束
	 */
	private void endChronometerTick() {
		chronometer.stop();
		reqCaptchaBtn.setEnabled(true);
		reqCaptchaBtn.setText("重新发送");
		chronometer.setVisibility(View.GONE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back();
		}
		return false;
	}
}
