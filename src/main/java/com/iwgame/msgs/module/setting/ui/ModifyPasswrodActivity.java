/**      
 * ModifypswActivity.java Create on 2013-9-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.youban.msgs.R;

/**
 * @ClassName: ModifypswActivity
 * @Description: 修改密码界面
 * @author 王卫
 * @date 2013-9-27 上午11:47:59
 * @Version 1.0
 * 
 */
public class ModifyPasswrodActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "ModifyPasswrodActivity";
	// 保存按钮
	private ImageView saveImg;
	// 旧密码框
	private EditText opwdTxt;
	// 新密码框
	private EditText npwdTxt;
	// 重复新密码框
	private EditText npwdTxt2;
	// 旧密码清除按钮
	private Button cleanOpwdBtn;
	// 新密码清除按钮
	private Button cleanNpwdBtn;
	// 重复新密码清除按钮
	private Button cleanNpwdBtn2;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
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
		// 添加右边功能按钮
		setRightVisible(true);
		saveImg = new ImageView(this);
		saveImg.setBackgroundResource(R.drawable.setting_modify_password_save_btn);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(saveImg);
		saveImg.setOnClickListener(this);
		// 设置TITLE
		titleTxt.setText("修改密码");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View view = (LinearLayout) View.inflate(this, R.layout.setting_modify_password, null);
		contentView.addView(view, params);
		// 赋值界面元素
		opwdTxt = (EditText) findViewById(R.id.opwdTxt);
		npwdTxt = (EditText) findViewById(R.id.npwdTxt);
		npwdTxt2 = (EditText) findViewById(R.id.npwdTxt2);
		cleanOpwdBtn = (Button) findViewById(R.id.cleanOpwdBtn);
		cleanNpwdBtn = (Button) findViewById(R.id.cleanNpwdBtn);
		cleanNpwdBtn2 = (Button) findViewById(R.id.cleanNpwdBtn2);
		cleanOpwdBtn.setOnClickListener(this);
		cleanNpwdBtn.setOnClickListener(this);
		cleanNpwdBtn2.setOnClickListener(this);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(opwdTxt, cleanOpwdBtn);
		EditTextUtil.ChangeCleanTextButtonVisible(npwdTxt, cleanNpwdBtn);
		EditTextUtil.ChangeCleanTextButtonVisible(npwdTxt2, cleanNpwdBtn2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == cleanOpwdBtn.getId()) {
			opwdTxt.setText("");
		} else if (v.getId() == cleanNpwdBtn.getId()) {
			npwdTxt.setText("");
		} else if (v.getId() == cleanNpwdBtn2.getId()) {
			npwdTxt2.setText("");
		} else if (v.getId() == saveImg.getId()) {
			String opwd = opwdTxt.getText().toString();
			String npwd = npwdTxt.getText().toString();
			String npwd2 = npwdTxt2.getText().toString();
			if(!opwd.isEmpty()){
				if(opwd.trim().length() >= 6){
					if(opwd.trim().length() <= 16){
						if (!opwd.matches("[0-9]+")){
							if(opwd.indexOf(" ") < 0){
								if(!opwd.equals(npwd)){
									if(!npwd.isEmpty()){
										if(npwd.trim().length() >= 6){
											if(npwd.trim().length() <= 16){
												if(!npwd.matches("[0-9]+")){
													if(!npwd2.isEmpty()){

														if(npwd.equals(npwd2)){
															if(npwd.indexOf(" ") < 0){
																// 发送服务端
																if (NetworkUtil.isNetworkAvailable(this)) {
																	modifyPassword(opwd, npwd);
																} else {
																	ToastUtil.showToast(this, getString(R.string.network_error));
																}
															}else{
																ToastUtil.showToast(this, "密码输入不能带有空格哦!");
															}
														}else{
															ToastUtil.showToast(this, "新密码和重复新密码输入需要保持一致哦!");
														}
													}else{
														ToastUtil.showToast(this, "需要输入重复新密码哦!");
													}
												}else{
													ToastUtil.showToast(this, "密码需要为纯字母或字母数字组合哦!");
												}
											}else{
												ToastUtil.showToast(this, "密码长度最长只能输入16位哦!");
											}
										}else{
											ToastUtil.showToast(this, "密码长度需要6位以上哦!");
										}
									}else{
										ToastUtil.showToast(this, "需要输入新密码哦!");
									}
								}else{
									ToastUtil.showToast(this, "新密码不能和旧密码一致哦!");
								}
							}else{
								ToastUtil.showToast(this, "密码输入不能带有空格哦!");
							}
						}else{
							ToastUtil.showToast(this, "密码需要为纯字母或字母数字组合哦!");
						}
					}else{
						ToastUtil.showToast(this, "密码长度最长只能输入16位哦!");
					}
				}else{
					ToastUtil.showToast(this, "密码长度需要6位以上哦!");
				}
			}else {
				ToastUtil.showToast(this, "需要输入旧密码哦!");
			}
		}
	}

	/**
	 * 修改密码
	 * 
	 * @param opwd
	 * @param npwd
	 */
	private void modifyPassword(String opwd, String npwd) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getSettingProxy().modifypassword(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					ToastUtil.showToast(ModifyPasswrodActivity.this, getString(R.string.setting_modify_pwd_success));
					finish();
					break;
				default:
					ToastUtil.showToast(ModifyPasswrodActivity.this, "修改密码失败");
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				switch (result) {
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_MODIFY_ERROR_VALUE:
					ToastUtil.showToast(ModifyPasswrodActivity.this, "修改密码失败");
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_MODIFY_NEW_BLANK_VALUE:
					ToastUtil.showToast(ModifyPasswrodActivity.this, "新密码为空");
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_MODIFY_NEW_EQUAL_OLD_VALUE:
					ToastUtil.showToast(ModifyPasswrodActivity.this, "新密码和旧密码相等");
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_MODIFY_OLD_BLANK_VALUE:
					ToastUtil.showToast(ModifyPasswrodActivity.this, "旧密码为空");
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_MODIFY_OLD_ERROR_VALUE:
					ToastUtil.showToast(ModifyPasswrodActivity.this, "旧密码输入不对哦!");
					break;
				default:
					ToastUtil.showToast(ModifyPasswrodActivity.this, "修改密码失败");
					break;
				}
				LogUtil.e(TAG, "修改密码失败");
				dialog.dismiss();
			}
		}, this, opwd, npwd);
	}

	private Location init2() {
		// 创建lcoationManager对象
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 创建一个criteria对象
		Criteria criteria = new Criteria();
		// 设置为最大精度
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// 设置不需要获取海拔数据
		criteria.setAltitudeRequired(false);
		// 设置不需要获取方位信息
		criteria.setBearingRequired(false);
		// 设置允许产生资费
		criteria.setCostAllowed(true);
		// 要求低耗电
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// 为程序的通用性，动态选择location provider
		String provider = manager.getBestProvider(criteria, true);

		manager.getLastKnownLocation(provider);

		if (provider != null) {
			Log.i(TAG, "----------选择的provider:" + provider);
			Location location = manager.getLastKnownLocation(provider);
			return location;
		} else {
			return null;
		}
	}
}
