/**      
 * RegisterInforActivity.java Create on 2013-8-1     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui.register;

import java.util.HashMap;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.LoadDataAcitvity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: SetDetailInfoActivity
 * @Description: 设置详细信息，提交注册
 * @author 王卫
 * @date 2013-8-1 下午03:50:32
 * @Version 1.0
 * 
 */
public class SetDetailInfoActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "RegisterSetDetailActivity";
	// 设置头像按钮
	private ImageView avatarView;
	// 头像右下角图像
	private ImageView avatar_add;
	// 手机号
	private String mobileNumber = null;
	// 验证码
	private String captcha = null;
	// 密码
	private String password = null;
	// 昵称
	private String nickname = null;
	// 性别
	private int sex = 0;
	// 头像数据
	private byte[] avatar = null;

	private Button confirmBtn;
	private Button setAvatarBtn;

	private TextView jumpView;
	
	private Uri imageUri;
	
	// 绑定类型（注册/游客绑定手机）
	private String bundType;
	
	String userAvatar;//游客头像资源ID
	
	// 倒计时次数
	private static final int escapeCount = 5;
	
	private Dialog statusErrordialog;

	private Bitmap photo = null;
	
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
		tv.setText("4/4");
		tv.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor());
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tv.setGravity(Gravity.CENTER);
		addRightView(tv);
		// 设置TITLE
		setTitleTxt(getString(R.string.title_set_detail_info_title));
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.account_register_set_detail, null), params);

		// 设置获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mobileNumber = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOBILENUM);
			captcha = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CAPTCHA);
			password = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PASSWORD);
			nickname = (String) bundle.get(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USERNAME);
			sex = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX);
			bundType = bundle.getString("BundPhoneType");
		}
		jumpView = (TextView) findViewById(R.id.jumpView);
		jumpView.setOnClickListener(this);
		confirmBtn = (Button) findViewById(R.id.confirmBtn);
		confirmBtn.setOnClickListener(this);
		setAvatarBtn = (Button) findViewById(R.id.setAvatarBtn);
		setAvatarBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PhotoUtil.showSelectDialog(SetDetailInfoActivity.this);
			}
		});
		// 设置头像按钮
		avatarView = (ImageView) findViewById(R.id.icon);
		avatar_add = (ImageView) findViewById(R.id.ccount_register_avatar_add);
		if(bundType != null && "BundPhone".equals(bundType)){//绑定手机
			pageName = "com.iwgame.msgs.module.account.ui.bundphone.SetDetailInfoActivity";
			avatar_add.setBackgroundResource(R.drawable.account_bund_avatar_add);
			setAvatarBtn.setVisibility(View.GONE);
			confirmBtn.setText("完成");
			confirmBtn.setVisibility(View.VISIBLE);
			ExtUserVo userVo = SystemContext.getInstance().getExtUserVo();
			if(userVo != null){
				userAvatar = userVo.getAvatar();
				if(userAvatar != null && !userAvatar.isEmpty() && !userAvatar.endsWith("_default")){
					String imageUrl = ResUtil.getOriginalRelUrl(userAvatar);
					new ImageLoader().loadRes(imageUrl, 0, avatarView, R.drawable.common_default_icon);
				}else{
					avatarView.setBackgroundResource(R.drawable.account_bundphone_avatar_bg_selector);
				}
			}else{
				avatarView.setBackgroundResource(R.drawable.account_bundphone_avatar_bg_selector);
			}
		}else{
			pageName = "com.iwgame.msgs.module.account.ui.register.SetDetailInfoActivity";
			avatar_add.setBackgroundResource(R.drawable.account_register_avatar_add);
			avatarView.setBackgroundResource(R.drawable.account_register_avatar_bg_selector);
			confirmBtn.setText("注册");
		}
		avatarView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PhotoUtil.showSelectDialog(SetDetailInfoActivity.this);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.confirmBtn) {
			if("BundPhone".equals(bundType)){//绑定手机
				
				if((userAvatar != null && !userAvatar.endsWith("_default")) || avatar != null){//默认头像
					if (NetworkUtil.isNetworkAvailable(this)) {
						bundPhone();
					}else {
						ToastUtil.showToast(this, getString(R.string.network_error));
					}
				}else{
					ToastUtil.showToast(this, getString(R.string.bundphone_notset_newavater));
				}
			}else{//注册
				if (avatar != null) {
					if (NetworkUtil.isNetworkAvailable(this)) {
						confirmBtn.setEnabled(false);
						jumpView.setEnabled(false);
						registerAccount(captcha, mobileNumber, password, nickname, sex, null, avatar);
					} else {
						ToastUtil.showToast(this, getString(R.string.network_error));
					}
				} else {
					ToastUtil.showToast(this, getString(R.string.register_notset_avater));
				}
			}
		} else if (v.getId() == R.id.jumpView) {
			if (NetworkUtil.isNetworkAvailable(this)) {
				confirmBtn.setEnabled(false);
				jumpView.setEnabled(false);
				registerAccount(captcha, mobileNumber, password, nickname, sex, null, avatar);
			} else {
				ToastUtil.showToast(this, getString(R.string.network_error));
			}
		}
	}

	/**
	 * 绑定手机
	 * @param password
	 */
	private void bundPhone(){
		confirmBtn.setEnabled(false);
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getAccountProxy().bundPhone(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					confirmBtn.setEnabled(false);
					SystemContext.getInstance().setGuestStatus(0);
					actionComplete();
					break;
				default:
					confirmBtn.setEnabled(true);
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_service_error));
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				confirmBtn.setEnabled(true);
				switch (result) {
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_BLANK_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_nickname_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_INVALID_VALUE: 
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_nickname_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_INVALID_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_account_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_BLANK_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_account_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_PASSWORD_BLANK_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_password_blank));
					break;
				default:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_service_error));
					break;
				}
				dialog.dismiss();
			}
		}, this, captcha, mobileNumber, password, nickname, sex, avatar);
	}
	
	/**
	 * 绑定成功，返回设置界面，会从空白页跳转
	 */
	private void actionComplete(){
		Intent intent = new Intent(this, BundPhoneActivity.class);   
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
		startActivity(intent); 
	}
	
	/**
	 * 提交注册用户详细信息
	 * 
	 * @param captcha
	 * @param accountName
	 * @param password
	 * @param nickname
	 * @param sex
	 * @param age
	 * @param avatar
	 */
	private void registerAccount(final String captcha, final String accountName, final String password, final String nickname, final int sex,
			final Integer age, final byte[] avatar) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getAccountProxy().registerAccount(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, accountName);
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, nickname);
					MobclickAgent.onEvent(SetDetailInfoActivity.this, UMConfig.MSGS_EVENT_USER_REGISTER, ummap);
					// 登录
					login(accountName, password);
					break;
				default:
					confirmBtn.setEnabled(true);
					jumpView.setEnabled(true);
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_service_error));
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				confirmBtn.setEnabled(true);
				jumpView.setEnabled(true);
				switch (result) {
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_BLANK_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_nickname_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_INVALID_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_nickname_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_INVALID_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_account_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_BLANK_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_account_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_PASSWORD_BLANK_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_register_password_blank));
					break;
				default:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_service_error));
					break;
				}
				LogUtil.e(TAG, "注册设置详细信息失败：" + result);
				dialog.dismiss();
			}
		}, this, captcha, accountName, password, nickname, sex, age, avatar, SystemContext.UMENG_CHANNEL);
	}

	/**
	 * 登录
	 * 
	 * @param account
	 * @param password
	 */
	private void login(final String account, final String password) {
		SystemContext.getInstance().setRecommendGameTag(true);
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					// 跳转到主界面
					Intent intent = new Intent(SetDetailInfoActivity.this, LoadDataAcitvity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 0);
					intent.putExtras(bundle);
					startActivity(intent);
					SetDetailInfoActivity.this.finish();
					break;
				default:
					jumpLoginAct();
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				switch (result) {
				case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_not_exist));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_INVALID_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_password_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_killed));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_locked));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_login_ip_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
					ToastUtil.showToast(SetDetailInfoActivity.this, getString(R.string.ec_account_info_uid_blank));
					break;
				case com.iwgame.msgs.common.ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR://session错误
					showSessionStatusErrorDialog();
					break;
				default:
					break;
				}
				dialog.dismiss();
				jumpLoginAct();
			}
		}, this, account, password);
	}

	/**
	 * 跳转到登录界面
	 */
	private void jumpLoginAct() {
		startActivity(new Intent(this, LoginActivity.class));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.d(TAG, "resultCode =" + resultCode + ";requestCode=" + requestCode);
//		if(photo != null && !photo.isRecycled()){
//			photo.recycle();
//			photo = null;
//			//System.gc();
//		}
		Bitmap tempBtm = null;
		byte[] photoByte = null;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:// 相机
				Display mDisplay = this.getWindowManager().getDefaultDisplay();
				int w = mDisplay.getWidth();
				int h = mDisplay.getHeight();
				int imagewidth = w > h ? h : w;
				imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
				PhotoUtil.doCropBigPhoto(this, imageUri, imageUri, 1, 1, imagewidth, imagewidth);
				return;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:// 相册
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				Display mDisplay2 = this.getWindowManager().getDefaultDisplay();
				int w2 = mDisplay2.getWidth();
				int h2 = mDisplay2.getHeight();
				int imagewidth2 = w2 > h2 ? h2 : w2;
				imageUri = Uri.parse("file://" + PhotoUtil.sdcardTempFilePath);
				PhotoUtil.doCropBigPhoto(this, originalUri, imageUri, 1, 1, imagewidth2, imagewidth2);
				return;
			case PhotoUtil.CROP_IMAGE_WITH_DATA:
				if (data != null && data.getParcelableExtra("data") != null) {
					try {
						tempBtm = photo;
						photo = data.getParcelableExtra("data");
					}catch(OutOfMemoryError e){
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
					if (photo != null) {
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 30);
					}
				} else {
					if (imageUri != null) {
						try {
							tempBtm = photo;
							photo = ImageUtil.decodeUri2Bitmap(this.getContentResolver(), imageUri);
						}catch(OutOfMemoryError e){
							e.printStackTrace();
						}catch (Exception e) {
							e.printStackTrace();
						}
						if (photo != null) {
							photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, 30);
						}
					}
				}
				break;
			}
			LogUtil.d(TAG, "photo =" + photo);
			if (photo != null) {
				avatarView.setImageBitmap(photo);
				avatar = photoByte;
				setAvatarBtn.setVisibility(View.GONE);
				confirmBtn.setVisibility(View.VISIBLE);
				if(tempBtm != null && !tempBtm.isRecycled()){
					tempBtm.recycle();
					tempBtm = null;
					//System.gc();
				}
			} else {
				if(tempBtm != null && !tempBtm.isRecycled()){
					tempBtm.recycle();
					tempBtm = null;
					//System.gc();
				}
				ToastUtil.showToast(this, getResources().getString(R.string.common_add_photo_error));
				LogUtil.e(TAG, "获得需要发送的图片异常"); 
				return;
			}
		} else {
			LogUtil.e(TAG, "选择发送的图片异常");
		}
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
					// 跳转到主界面
					Intent intent = new Intent(SetDetailInfoActivity.this, LoadDataAcitvity.class);
					Bundle bundle = new Bundle();
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 0);
					intent.putExtras(bundle);
					startActivity(intent);
					SetDetailInfoActivity.this.finish();
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
		}, SetDetailInfoActivity.this, mobileNumber, password);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(photo != null && !photo.isRecycled()){
			photo.recycle();
			photo = null;
			//System.gc();
		}
	}
}
