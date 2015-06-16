/**      
 * RegisterInforActivity.java Create on 2013-8-1     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.register;


import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.youban.msgs.R;

/**
 * @ClassName: RegThirdStepActivity
 * @Description: 设置密码
 * @author 王卫
 * @date 2013-8-1 下午03:50:32
 * @Version 1.0
 * 
 */
public class SetPasswordActivity extends BaseActivity implements OnClickListener {

	protected static final String Tag = "RegisterSetPasswordActivity";
	// 密码文本框
	private EditText passwordTxt;
	// 昵称文本框
	private EditText nicknameTxt;
	// 清除密码按钮
	private Button cleanPswdBtn;
	// 确认提交，设置密码按钮
	private Button confirmBtn;
	// 清除昵称按钮
	private Button cleanNicknameBtn;
	// 男
	private ImageView manSex;
	// 女
	private ImageView womanSex;
	// 性别文本框
	private TextView sexTxt;
	// 性别
	private int sex = 0;
	// 手机号
	private String mobileNum = null;
	// 验证码
	private String captcha = null;
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
		tv.setText("3/4");
		addRightView(tv);
		// 设置TITLE
		setTitleTxt(getString(R.string.title_set_password_activity));
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.account_register_set_password, null), params);

		LinearLayout account_register_detail_layout = (LinearLayout) findViewById(R.id.account_register_detail_layout);
		// 确认提交，设置密码按钮
		confirmBtn = (Button) findViewById(R.id.confirmBtn);
		confirmBtn.setOnClickListener(this);
		// 设置男点选框
		manSex = (ImageView) findViewById(R.id.manSex);
		womanSex = (ImageView) findViewById(R.id.womanSex);
		manSex.setOnClickListener(this);
		womanSex.setOnClickListener(this);
		// 设置昵称文本框
		nicknameTxt = (EditText) findViewById(R.id.nickname);
		InputFilterUtil.lengthFilter(this, nicknameTxt, 16, getString(R.string.nickname_verify_more_fail));
		// 设置性别文本框
		sexTxt = (TextView) findViewById(R.id.sexView);
		// 设置获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mobileNum = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM);
			captcha = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CAPTCHA);
			bundType = bundle.getString("BundPhoneType");
			if(bundType != null && "BundPhone".equals(bundType)){//绑定手机
				pageName = "com.iwgame.msgs.module.account.ui.bundphone.SetPasswordActivity";
				ExtUserVo userVo = SystemContext.getInstance().getExtUserVo();
				if(userVo != null){
					//设置游客的性别，如果设置过了则显示
					int sex = userVo.getSex();
					String userName = userVo.getUsername();
					if(0 == sex){//男
						manSex.setBackgroundResource(R.drawable.common_sex_man_enable);
						sexTxt.setText("男");
					}else if(1 == sex){//女
						womanSex.setBackgroundResource(R.drawable.common_sex_woman_enable);
						sexTxt.setText("女");
					}
//					int isUpdateUserName = SystemContext.getInstance().getIsUpdateUserName();
//					if(isUpdateUserName == 1){//已修改
//						nicknameTxt.setText(userName);
//					}
					nicknameTxt.setText(userVo.getUsername());
				}
			}else{
				pageName = "com.iwgame.msgs.module.account.ui.register.SetPasswordActivity";
			}
		}
		// 密码文本框
		passwordTxt = (EditText) findViewById(R.id.pwdView);
		// 清除密码按钮
		cleanPswdBtn = (Button) findViewById(R.id.pwdCleanBtn);
		cleanPswdBtn.setOnClickListener(this);
		// 设置清除昵称按钮
		cleanNicknameBtn = (Button) findViewById(R.id.nicknameCleanBtn);
		cleanNicknameBtn.setOnClickListener(this);



		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(passwordTxt, cleanPswdBtn);
		EditTextUtil.ChangeCleanTextButtonVisible(nicknameTxt, cleanNicknameBtn);
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
		if (v.getId() == R.id.confirmBtn) {
			// 密码
			String password = passwordTxt.getText().toString();
			if (password != null && !password.isEmpty()) {
				if (!password.matches("[0-9]+")) {
					if (password.length() >= SystemConfig.PASSWORD_MIN_LENGTH && password.length() <= SystemConfig.PASSWORD_MAX_LENGTH) {
						String nickname = nicknameTxt.getText().toString();
						if (nickname == null || nickname.isEmpty()){
							ToastUtil.showToast(SetPasswordActivity.this, "还没有输入昵称哦！");
						}else if(nickname.trim().isEmpty()){
							ToastUtil.showToast(SetPasswordActivity.this, "昵称不能全为空格哦！");
						}else if(nickname.matches("[0-9]+")){
							ToastUtil.showToast(SetPasswordActivity.this, "纯数字不能作为昵称哦，请重新输入！");
						}else if(StringUtil.getCharacterNum(nickname) > 16){
							ToastUtil.showToast(SetPasswordActivity.this, "输入的昵称不能超过8个汉字或16个字符哦！");
						}else if(StringUtil.getCharacterNum(nickname) < 4){
							ToastUtil.showToast(SetPasswordActivity.this, "输入的昵称不能少于2个汉字或4个字符哦！");
						}else{
							if (!ServiceFactory.getInstance().getWordsManager().matchName(nickname)) {
								if (!sexTxt.getText().toString().isEmpty()) {
									if (sexTxt.getText().toString().equals("男")) {
										sex = 0;
									} else if (sexTxt.getText().toString().equals("女")) {
										sex = 1;
									}
									confirmBtn.setClickable(false);
									// 调整到完善资料页面（校验两次输入的密码一致）
									Intent intent = new Intent(SetPasswordActivity.this, SetDetailInfoActivity.class);
									Bundle bundle = new Bundle();
									bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM, mobileNum);
									bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CAPTCHA, captcha);
									bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PASSWORD, password);
									bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USERNAME, nickname);
									bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX, sex);
									bundle.putString("BundPhoneType", bundType);
									intent.putExtras(bundle);
									startActivity(intent);
								} else {
									ToastUtil.showToast(SetPasswordActivity.this, "性别不能为空");
								}
							} else {
								ToastUtil.showToast(this, getResources().getString(R.string.global_words_nikename_error));
							}
						}
					} else if(password.length() > SystemConfig.PASSWORD_MIN_LENGTH){
						ToastUtil.showToast(this, "密码长度不能多于16位哦");
					} else{
						ToastUtil.showToast(this, "密码长度不能少于6位哦");
					}
				} else {
					ToastUtil.showToast(this, "密码不能为纯数字哦，需要为字母或数字字母的组合哦！");
				}
			} else {
				ToastUtil.showToast(this, "您未输入密码");
			}
		} else if (v.getId() == R.id.pwdCleanBtn) {
			passwordTxt.setText("");
		} else if (v.getId() == R.id.nicknameCleanBtn) {
			nicknameTxt.setText("");
		} else if (v.getId() == R.id.manSex) {
			womanSex.setEnabled(true);
			manSex.setEnabled(false);
			sexTxt.setText("男");
			manSex.setBackgroundResource(R.drawable.common_sex_man_enable);
			womanSex.setBackgroundResource(R.drawable.common_sex_woman_disable);
		} else if (v.getId() == R.id.womanSex) {
			manSex.setEnabled(true);
			womanSex.setEnabled(false);
			sexTxt.setText("女");
			manSex.setBackgroundResource(R.drawable.common_sex_man_disable);
			womanSex.setBackgroundResource(R.drawable.common_sex_woman_enable);
		}
	}

}
