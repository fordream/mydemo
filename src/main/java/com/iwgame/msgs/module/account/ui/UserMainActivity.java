/**      
 * UserMainActivity.java Create on 2013-8-15     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

import com.iwgame.msgs.LoadDataAcitvity;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.ShareSdkManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.object.GuestObject;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.account.ui.register.SetAccountActivity;
import com.iwgame.msgs.module.game.adapter.ViewPagerAdapter;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.xaccount.proto.XAccountInfo;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: UserMainActivity
 * @Description: 注册登录入口界面
 * @author 王卫
 * @date 2013-8-15 下午01:15:10
 * @Version 1.0
 * 
 */
public class UserMainActivity extends BaseSuperActivity implements OnClickListener {
	private static final String TAG = "UserMainActivity";

	// 切换页面组件
	private ViewPager viewPager = null;
	// 页面集合列表
	private List<View> pageViews = null;

	private LinearLayout dianView = null;
	
	private ImageView loadingImageView;
	// 点点引导集合列表
	private ArrayList<ImageView> imageViews = null;
	
	private boolean isRegister = false;
	// 登录按钮
//	private Button loginBtn;
	// 游客登录按钮
//	private Button registerBtn;
	// 帮助按钮
//	private Button helpBtn;
	//QQ登录按钮
	private ImageView loginQqBtn;
	//weibo登录按钮
	private ImageView loginWeiboBtn;
	//手机号登录按钮
	private ImageView loginPhoneBtn;
	//注册按钮
	private TextView loginRegisterBtn;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_main);
		// 初始化界面
		init();
	}

	@Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	MobclickAgent.onPageStart(TAG);
    	MobclickAgent.onResume(this);
    	int item = viewPager.getCurrentItem();
    	//发送友盟统计时间
		HashMap<String, String> ummap = new HashMap<String, String>();
		ummap.put(UMConfig.MSGS_USERMAIN_PAGEINDEX, String.valueOf(item+1));
		MobclickAgent.onEvent(UserMainActivity.this, UMConfig.MSGS_EVENT_USERMAIN, ummap);
		loginQqBtn.setClickable(true);
		loginWeiboBtn.setClickable(true);
		loginPhoneBtn.setClickable(true);
		loginRegisterBtn.setClickable(true);
		
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPageEnd(TAG);
    	MobclickAgent.onPause(this);
    }
	
	/**
	 * 初始化界面
	 */
	private void init() {
		// 设置QQ登录按钮
		loginQqBtn = (ImageView) findViewById(R.id.login_qqBtn);
		loginQqBtn.setOnClickListener(this);
		// 设置weibo登录按钮
		loginWeiboBtn = (ImageView) findViewById(R.id.login_weiboBtn);
		loginWeiboBtn.setOnClickListener(this);
		
		loginPhoneBtn = (ImageView) findViewById(R.id.login_phoneBtn);
		loginPhoneBtn.setOnClickListener(this);
		
		loginRegisterBtn = (TextView) findViewById(R.id.login_registerBtn);
		loginRegisterBtn.setOnClickListener(this);
		
		//加载进度动画
		loadingImageView = (ImageView) findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) loadingImageView.getBackground();
		animationDrawable.start();
		
		
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 显示点点引导
				drawPoint(arg0);
				//发送友盟统计时间
				HashMap<String, String> ummap = new HashMap<String, String>();
				ummap.put(UMConfig.MSGS_USERMAIN_PAGEINDEX, String.valueOf(arg0+1));
				MobclickAgent.onEvent(UserMainActivity.this, UMConfig.MSGS_EVENT_USERMAIN, ummap);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
		dianView = (LinearLayout) findViewById(R.id.dianView);

		pageViews = new ArrayList<View>();
		ImageView view = new ImageView(this);
		view.setImageDrawable(getResources().getDrawable(R.drawable.common_main_bg1));
		view.setScaleType(ScaleType.CENTER_CROP);
		pageViews.add(view);
		view = new ImageView(this);
		view.setImageDrawable(getResources().getDrawable(R.drawable.common_main_bg2));
		view.setScaleType(ScaleType.CENTER_CROP);
		pageViews.add(view);
		view = new ImageView(this);
		view.setImageDrawable(getResources().getDrawable(R.drawable.common_main_bg3));
		view.setScaleType(ScaleType.CENTER_CROP);
		pageViews.add(view);
		view = new ImageView(this);
		view.setImageDrawable(getResources().getDrawable(R.drawable.common_main_bg4));
		view.setScaleType(ScaleType.CENTER_CROP);
		pageViews.add(view);
		ViewPagerAdapter adapter = new ViewPagerAdapter(pageViews);
		viewPager.setAdapter(adapter);
		// 初始化点点
		initPoint();
	}

	/**
	 * 初始化点点引导
	 */
	private void initPoint() {
		imageViews = new ArrayList<ImageView>();
		ImageView imageView = null;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(5, 5));
			// if(unselected != null)
			// imageView.setImageBitmap(unselected);
			imageView.setImageResource(R.drawable.common_dian_unselect);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 18;
			layoutParams.rightMargin = 18;
			dianView.addView(imageView, layoutParams);
			if (i == 0) {
				// imageView.setImageBitmap(selected);
				imageView.setImageResource(R.drawable.common_dian_select);
			}
			imageViews.add(imageView);
		}
	}

	/**
	 * 绘制点点
	 * 
	 * @param index
	 */
	public void drawPoint(int index) {
		if (imageViews != null) {
			for (int i = 0; i < imageViews.size(); i++) {
				if (index == i) {
					imageViews.get(i).setImageResource(R.drawable.common_dian_select);
				} else {
					imageViews.get(i).setImageResource(R.drawable.common_dian_unselect);
				}
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
		// 处理点击登录按钮事件，跳转到登录界面
//		if (v.getId() == R.id.loginBtn) {
//			registerBtn.setClickable(false);
//			helpBtn.setClickable(false);
//			loginBtn.setClickable(false);
//			Intent intent = new Intent(this, LoginActivity.class);
//			startActivity(intent);
			//this.finish();
//		}
		// 处理点击游客进入按钮事件
//		else if (v.getId() == R.id.registerBtn) {
//			loginBtn.setClickable(false);
//			helpBtn.setClickable(false);
//			registerBtn.setClickable(false);
//			guestRegisterAccount();
//		}
		// 处理点击注册按钮事件，跳转到帮助界面
//		else if (v.getId() == R.id.helpbtn) {
//			loginQqBtn.setClickable(false);
//			loginWeiboBtn.setClickable(false);
//			loginPhoneBtn.setClickable(false);
//			loginRegisterBtn.setClickable(false);
//			Intent intent = new Intent(this, HelpFragmentActivity.class);
//			startActivity(intent);
//		}
		if(v.getId() == R.id.login_qqBtn){
			if(FastClickLimitUtil.isFastClick()){
				return;
			}
			loginQqBtn.setClickable(false);
			loginWeiboBtn.setClickable(false);
			loginPhoneBtn.setClickable(false);
			loginRegisterBtn.setClickable(false);
			if (ShareSdkManager.getInstance().getAuthorizeStatus(this, QQ.NAME)) {
				ShareManager.getInstance().authorize(this, 1, QQ.NAME, new ShareCallbackListener());
			}
			ShareManager.getInstance().authorize(this, 0, QQ.NAME, new ShareCallbackListener() {
				@Override
				public void doSuccess(String plamType) {
					if(QQ.NAME.equals(plamType)){
						String userId = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getUserId();
						String qqToken = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getToken();
						qqLogin(qqToken, userId);
					}
				}
				@Override
				public void doFail() {
				}
			});
			
		}else if(v.getId() == R.id.login_weiboBtn){
			if(FastClickLimitUtil.isFastClick()){
				return;
			}
			loginQqBtn.setClickable(false);
			loginWeiboBtn.setClickable(false);
			loginPhoneBtn.setClickable(false);
			loginRegisterBtn.setClickable(false);
			if (ShareSdkManager.getInstance().getAuthorizeStatus(this, SinaWeibo.NAME)) {
				ShareManager.getInstance().authorize(this, 1, SinaWeibo.NAME, new ShareCallbackListener() {});
			}
			ShareManager.getInstance().authorize(this, 0, SinaWeibo.NAME, new ShareCallbackListener() {
				@Override
				public void doSuccess(String plamType) {
					if(SinaWeibo.NAME.equals(plamType)){
						String userId = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getUserId();
						String weiboToken = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getToken();
						weiboLogin(weiboToken, userId);
					}
				}
				@Override
				public void doFail() {
				}
			});
			
		}else if(v.getId() == R.id.login_phoneBtn){
			loginQqBtn.setClickable(false);
			loginWeiboBtn.setClickable(false);
			loginPhoneBtn.setClickable(false);
			loginRegisterBtn.setClickable(false);
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}else if(v.getId() == R.id.login_registerBtn){
			if(FastClickLimitUtil.isFastClick()){
				return;
			}
			loginQqBtn.setClickable(false);
			loginWeiboBtn.setClickable(false);
			loginPhoneBtn.setClickable(false);
			loginRegisterBtn.setClickable(false);
			Intent intent = new Intent(UserMainActivity.this, SetAccountActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("BundPhoneType", "Register");
			intent.putExtras(bundle);
			startActivity(intent);
		}
		
		
	}

	/**
	 * 游客进入
	 */
	private void guestRegisterAccount(){
		String account = SystemContext.getInstance().getGuestAccount();
		String password =  SystemContext.getInstance().getGuestPassword();
		if (NetworkUtil.isNetworkAvailable(this)) {
			if(null != account && null != password){//已快速进入过
				//登录
				login(account, password);
			}else{//第一次进入
				//快速注册
				registerGuest();
			}
		} else {
			loginQqBtn.setClickable(true);
			loginWeiboBtn.setClickable(true);
			loginPhoneBtn.setClickable(true);
			loginRegisterBtn.setClickable(true);
			ToastUtil.showToast(this, getString(R.string.network_error));
		}
	}
	
	/**
	 * 游客注册
	 */
	private void registerGuest(){
//		registerBtn.setClickable(false);
		loadingImageView.setVisibility(View.VISIBLE);
		ProxyFactory.getInstance().getAccountProxy().registerGuestAccount(new ProxyCallBack<GuestObject>() {

			@Override
			public void onSuccess(GuestObject result) {
				if(result != null){
//					registerBtn.setClickable(false);
					String accountName = result.getAccount();
					String password = result.getPassword();
					SystemContext.getInstance().setGuestAccount(accountName);
					SystemContext.getInstance().setGuestPasssword(password);
					SystemContext.getInstance().setRecommendGameTag(true);
					
					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, accountName);
					MobclickAgent.onEvent(UserMainActivity.this, UMConfig.MSGS_EVENT_USER_REGISTER, ummap);
					
					// 登录
					login(accountName, password);
				}else{
					loginQqBtn.setClickable(true);
					loginWeiboBtn.setClickable(true);
					loginPhoneBtn.setClickable(true);
					loginRegisterBtn.setClickable(true);
					ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_not_success));
				}
				loadingImageView.setVisibility(View.GONE);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				switch (result) {
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_BLANK_VALUE:
					ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_nickname_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_INVALID_VALUE:
					ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_nickname_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_INVALID_VALUE:
					ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_account_invalid));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_BLANK_VALUE:
					ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_account_blank));
					break;
				case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_PASSWORD_BLANK_VALUE:
					ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_password_blank));
					break;
				case ErrorCode.EC_RESULT_ISNULL:
					ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_not_success));
					break;
				default:
					ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_service_error));
					break;
				}
				LogUtil.e(TAG, "注册设置详细信息失败：" + result);
				loadingImageView.setVisibility(View.GONE);
//				registerBtn.setClickable(true);
//				loginBtn.setClickable(true);
//				helpBtn.setClickable(true);
				loginQqBtn.setClickable(true);
				loginWeiboBtn.setClickable(true);
				loginPhoneBtn.setClickable(true);
				loginRegisterBtn.setClickable(true);
			}
		}, this, SystemContext.UMENG_CHANNEL);
	}
	
	/**
	 * 登录
	 * 
	 * @param account
	 * @param password
	 */
	private void login(final String account, final String password) {
//		loginBtn.setEnabled(false);
//		final CustomProgressDialog dialog = CustomProgressDialog.createDialog2(this, 1);
//		dialog.show();
		loadingImageView.setVisibility(View.VISIBLE);
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
					switch (result) {
					case Msgs.ErrorCode.EC_OK_VALUE:
//						loginBtn.setClickable(false);
						Intent intent = new Intent(UserMainActivity.this, LoadDataAcitvity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
						intent.putExtras(bundle);
						startActivity(intent);
						break;
					default:
						loginQqBtn.setClickable(true);
						loginWeiboBtn.setClickable(true);
						loginPhoneBtn.setClickable(true);
						loginRegisterBtn.setClickable(true);

						ToastUtil.showToast(UserMainActivity.this, "登录失败，请检查网络，稍后再试");
						break;
					}
					loadingImageView.setVisibility(View.GONE);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
					LogUtil.e(TAG, "登录请求失败：code=" + result + ", msg=" + resultMsg);

					switch (result) {
					case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_not_exist), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_PASSWORD_INVALID_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_password_invalid), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_killed), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_locked), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_login_ip_invalid), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_info_uid_blank), 1000);
						break;
					default:
						if(resultMsg != null && !resultMsg.isEmpty())
							ErrorCodeUtil.handleErrorCode(UserMainActivity.this, result, resultMsg);
						else
							ToastUtil.showToast(UserMainActivity.this, "登录失败，请检查网络，稍后再试");
						break;
					}

					loadingImageView.setVisibility(View.GONE);
					loginQqBtn.setClickable(true);
					loginWeiboBtn.setClickable(true);
					loginPhoneBtn.setClickable(true);
					loginRegisterBtn.setClickable(true);
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
		if (NetworkUtil.isNetworkAvailable(this)) {
			if(!isRegister){
				loadingImageView.setVisibility(View.VISIBLE);
			}
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
					switch (result) {
					case Msgs.ErrorCode.EC_OK_VALUE:
						Intent intent = new Intent(UserMainActivity.this, LoadDataAcitvity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
						intent.putExtras(bundle);
						startActivity(intent);
						UserMainActivity.this.finish();
						break;
					default:
						
						ToastUtil.showToast(UserMainActivity.this, "登录失败，请检查网络，稍后再试");
						break;
					}
					loginPhoneBtn.setClickable(true);
					loginRegisterBtn.setClickable(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
					loadingImageView.setVisibility(View.GONE);
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
					LogUtil.e(TAG, "登录请求失败：code=" + result + ", msg=" + resultMsg);

					switch (result) {
					case XAccountInfo.ErrorCode.EC_ACCOUNT_BIND_ERROR_VALUE:
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_not_exist), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_killed), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_locked), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_login_ip_invalid), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_info_uid_blank), 1000);
						break;
					case com.iwgame.msgs.common.ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR://session错误
//						showSessionStatusErrorDialog();
						break;
					case XAccountInfo.ErrorCode.EC_API_SYSTEM_ERROR_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_api_system_error_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_HTTP_CONNECT_ERROR_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_api_http_connect_error_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCOUNT_NOT_EXISTED_VALUE:
						String userId = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getUserId();
						String icon = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getUserIcon();
						String nickname = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getUserName();
						String gender = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getUserGender();
						RegisterAccount(userId, 21, gender.equals("m")?0:1, icon, nickname);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_ILLEGAL_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_api_access_token_illegal_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_EXPIRED_VALUE:
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_REVOKED_VALUE:
						//ToastUtil.showToast(LoginActivity.this, getString(R.string.ec_api_access_token_revoked_value), 1000);
						String userId1 = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getUserId();
						String qqToken1 = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getToken();
						qqLogin(qqToken1, userId1);
						break;
					case XAccountInfo.ErrorCode.EC_API_OPENID_NOT_EXPECTED_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_api_openid_not_expected_value), 1000);
						break;
					default:
						if (resultMsg != null && !resultMsg.isEmpty())
							ErrorCodeUtil.handleErrorCode(UserMainActivity.this, result, resultMsg);
						else
							ToastUtil.showToast(UserMainActivity.this, "登录失败，稍后再试");
						break;
					}
					loginPhoneBtn.setClickable(true);
					loginRegisterBtn.setClickable(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
					
					loadingImageView.setVisibility(View.GONE);
				}
			
		}, this, token, SystemConfig.LOGIN_THIRD_AUTH_TYPE, SystemConfig.LOGIN_QQ_WEIBO, userId);
	} else {
		loginPhoneBtn.setClickable(true);
		loginRegisterBtn.setClickable(true);
		loginQqBtn.setEnabled(true);
		loginWeiboBtn.setEnabled(true);
		ToastUtil.showToast(UserMainActivity.this, "登录失败，请检查网络，稍后再试");
	}
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
		if (NetworkUtil.isNetworkAvailable(this)) {
			if(!isRegister){
				loadingImageView.setVisibility(View.VISIBLE);
			}
		ProxyFactory.getInstance().getAccountProxy().login(new ProxyCallBack<Integer>() {
			
			@Override
			public void onSuccess(Integer result) {
					switch (result) {
					case Msgs.ErrorCode.EC_OK_VALUE:
						Intent intent = new Intent(UserMainActivity.this, LoadDataAcitvity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
						intent.putExtras(bundle);
						startActivity(intent);
						UserMainActivity.this.finish();
						break;
					default:
						ToastUtil.showToast(UserMainActivity.this, "登录失败，请检查网络，稍后再试");
						break;
					}
					loginPhoneBtn.setClickable(true);
					loginRegisterBtn.setClickable(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
					loadingImageView.setVisibility(View.GONE);
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
					LogUtil.e(TAG, "登录请求失败：code=" + result + ", msg=" + resultMsg);

					switch (result) {
					
					case XAccountInfo.ErrorCode.EC_ACCOUNT_NOT_EXIST_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_not_exist), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_KILLED_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_killed), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOCKED_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_locked), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_LOGIN_IP_INVALID_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_login_ip_invalid), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_INFO_UID_BLANK_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_info_uid_blank), 1000);
						break;
					case com.iwgame.msgs.common.ErrorCode.EC_CLIENT_LOGIN_SESSINT_STATUS_ERR://session错误
//						showSessionStatusErrorDialog();
						break;
					case XAccountInfo.ErrorCode.EC_API_SYSTEM_ERROR_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_api_system_error_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_HTTP_CONNECT_ERROR_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_api_http_connect_error_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCOUNT_NOT_EXISTED_VALUE:
						String userId = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getUserId();
						String icon = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getUserIcon();
						String nickname = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getUserName();
						String gender = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getUserGender();
						RegisterAccount(userId, 11, gender.equals("m")?0:1, icon, nickname);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_ILLEGAL_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_api_access_token_illegal_value), 1000);
						break;
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_EXPIRED_VALUE:
					case XAccountInfo.ErrorCode.EC_API_ACCESS_TOKEN_REVOKED_VALUE:
						String userId1 = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getUserId();
						String weiboToken1 = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getToken();
						weiboLogin(weiboToken1, userId1);
						break;
					case XAccountInfo.ErrorCode.EC_API_OPENID_NOT_EXPECTED_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_api_openid_not_expected_value), 1000);
						break;
					default:
						if (resultMsg != null && !resultMsg.isEmpty())
							ErrorCodeUtil.handleErrorCode(UserMainActivity.this, result, resultMsg);
						else
							ToastUtil.showToast(UserMainActivity.this, "登录失败，稍后再试");
						break;
					}
					loginPhoneBtn.setClickable(true);
					loginRegisterBtn.setClickable(true);
					loginQqBtn.setEnabled(true);
					loginWeiboBtn.setEnabled(true);
					loadingImageView.setVisibility(View.GONE);
			}
		}, this, token, SystemConfig.LOGIN_THIRD_AUTH_TYPE, SystemConfig.LOGIN_SINA_WEIBO, userId);
		} else {
			loginPhoneBtn.setClickable(true);
			loginRegisterBtn.setClickable(true);
			loginQqBtn.setEnabled(true);
			loginWeiboBtn.setEnabled(true);
			ToastUtil.showToast(UserMainActivity.this, "登录失败，请检查网络，稍后再试");
		}
	}
	

	private void RegisterAccount(String openID, final int target, int sex, String avatar, String nickname){
		if (NetworkUtil.isNetworkAvailable(this)) {
			isRegister = true;
			ProxyFactory.getInstance().getAccountProxy().registerAccount(new ProxyCallBack<Integer>() {
				
				@Override
				public void onSuccess(Integer result) {
					SystemContext.getInstance().setRecommendGameTag(true);
					if(target==11){
						String userId1 = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getUserId();
						String weiboToken1 = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, SinaWeibo.NAME).getDb().getToken();
						weiboLogin(weiboToken1, userId1);
					}else{
						String userId1 = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getUserId();
						String qqToken1 = ShareSdkManager.getInstance().getPlatform(UserMainActivity.this, QQ.NAME).getDb().getToken();
						qqLogin(qqToken1, userId1);
					}
					
				}
				
				@Override
				public void onFailure(Integer result, String resultMsg) {
					switch (result) {
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_BLANK_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_nickname_blank));
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_NICKNAME_INVALID_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_nickname_invalid));
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_INVALID_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_account_invalid));
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_ACCOUNT_BLANK_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_account_blank));
						break;
					case XAccountInfo.ErrorCode.EC_ACCOUNT_REGISTER_PASSWORD_BLANK_VALUE:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_register_password_blank));
						break;
					case ErrorCode.EC_RESULT_ISNULL:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_not_success));
						break;
					case ErrorCode.EC_CLIENT_THIRD_REGISTER_FAIL:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_account_not_success));
						break;
					default:
						ToastUtil.showToast(UserMainActivity.this, getString(R.string.ec_service_error));
						break;
					}
					isRegister = false;
				}
			}, this, openID, target, sex, avatar, nickname, SystemContext.UMENG_CHANNEL);
		} else {
			loginPhoneBtn.setClickable(true);
			loginRegisterBtn.setClickable(true);
			loginQqBtn.setEnabled(true);
			loginWeiboBtn.setEnabled(true);
			ToastUtil.showToast(UserMainActivity.this, "登录失败，请检查网络，稍后再试");
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
			// 处理返回结果，成功返回后跳转到主界面
			Intent intent = new Intent(this, MainFragmentActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -1);
			intent.putExtras(bundle);
			this.startActivity(intent);
			this.finish();
		}
		return false;
	}

}
