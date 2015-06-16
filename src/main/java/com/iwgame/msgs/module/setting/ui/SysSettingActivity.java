/**      
 * SysSettingActivity.java Create on 2013-9-22     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;

import com.iwgame.msgs.BaseApplication;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.ShareSdkManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.UserMainActivity;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.service.MessageService;
import com.iwgame.msgs.utils.UpdataVersionUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: SysSettingActivity
 * @Description: 系统设置主界面
 * @author 王卫
 * @date 2013-9-22 下午05:13:22
 * @Version 1.0
 * 
 */
public class SysSettingActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "SysSettingActivity";
	// 声音开关
	private Button soundOffOn;
	// 账号
	private TextView account;
	private ImageView rightImg;
	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private LinearLayout bind_telephone_number;
	private LinearLayout modifypwd;
	private ImageView bindTelephoneTag;// 绑定手机号的提示
	private TextView logoutBtn;

	/** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能 */

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
	 * 在onresume方法里面要去显示 用户帐号的信息 有可能在跳转绑定后，回来就要 更新显示信息
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (SystemContext.getInstance().getIsGuest() == SystemConfig.LOGIN_TARGET_NORMAL) {
			// 表示正常用户
			modifypwd.setVisibility(View.VISIBLE);
			bind_telephone_number.setEnabled(false);
			if(SystemContext.getInstance().getMobile() == null){
				account.setText("" + SystemContext.getInstance().getAccount());
			}else{
				account.setText("" + SystemContext.getInstance().getMobile());
			}
			rightImg.setVisibility(View.GONE);
			bindTelephoneTag.setVisibility(View.GONE);
			if(logoutBtn != null)logoutBtn.setVisibility(View.VISIBLE);
		} else {
			// 表示的是游客
			modifypwd.setVisibility(View.GONE);
			bind_telephone_number.setEnabled(true);
			if(SystemContext.getInstance().getIsGuest() == SystemConfig.LOGIN_TARGET_QQ_WEIBO){
				account.setText("QQ登录(" + AdaptiveAppContext.getInstance().getAppConfig().getSettingString() + ")");
			}
			else if (SystemContext.getInstance().getIsGuest() == SystemConfig.LOGIN_TARGET_SINA_WEIBO){
				account.setText("新浪微博登录(" + AdaptiveAppContext.getInstance().getAppConfig().getSettingString() + ")");
			}else{
				account.setText(AdaptiveAppContext.getInstance().getAppConfig().getSettingString());
			}
			rightImg.setVisibility(View.VISIBLE);
			bindTelephoneTag.setVisibility(View.VISIBLE);
			if (AdaptiveAppContext.getInstance().getAppConfig().isSetting_logout()) {
				logoutBtn.setVisibility(View.VISIBLE);
			} else {
				logoutBtn.setVisibility(View.GONE);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
	}

	/**
	 * 初始化
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		titleTxt.setText("系统设置");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View view = (LinearLayout) View.inflate(this, R.layout.setting_sys_setting, null);
		contentView.addView(view, params);
		// 设置退出登录功能
		findViewById(R.id.logoutBtn).setOnClickListener(this);
		// 设置修改密码功能
		findViewById(R.id.pwItem).setOnClickListener(this);
		// 黑名单功能
		findViewById(R.id.backItem).setOnClickListener(this);
		// 新浪微博绑定和解绑功能
//		findViewById(R.id.weiboBindingItem).setOnClickListener(this);
		bind_telephone_number = (LinearLayout) contentView.findViewById(R.id.bind_telephone_number);
		logoutBtn = (TextView) view.findViewById(R.id.logoutBtn);
		bind_telephone_number.setEnabled(false);
		bind_telephone_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 当点击绑定手机这一栏的时候，跳转到绑定手机界面
				Intent intent = new Intent(SysSettingActivity.this, BundPhoneActivity.class);
				SysSettingActivity.this.startActivity(intent);
			}
		});
		bindTelephoneTag = (ImageView) findViewById(R.id.bind_telephone_tag);
		modifypwd = (LinearLayout) findViewById(R.id.pwArea);

		// 声音总开关
		soundOffOn = (Button) findViewById(R.id.soundOffOn);
		soundOffOn.setOnClickListener(this);
		boolean open = SystemContext.getInstance().getSoundOffOn();
		if (open) {
			soundOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
		} else {
			soundOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
		}
		// 消息提醒
		findViewById(R.id.msgTipItem).setOnClickListener(this);
		findViewById(R.id.cacheItem).setOnClickListener(this);
		findViewById(R.id.userBackItem).setOnClickListener(this);
		findViewById(R.id.updataItem).setOnClickListener(this);
		findViewById(R.id.aboutItem).setOnClickListener(this);
		account = (TextView) findViewById(R.id.account);
		rightImg = (ImageView) findViewById(R.id.unbind_pic);
		if(SystemContext.getInstance().getIsGuest() == SystemConfig.LOGIN_TARGET_QQ_WEIBO){
			account.setText("QQ登录");
		}
		else if (SystemContext.getInstance().getIsGuest() == SystemConfig.LOGIN_TARGET_SINA_WEIBO){
			account.setText("新浪微博登录");
		}else{
			if(SystemContext.getInstance().getMobile() == null){
				account.setText("" + SystemContext.getInstance().getAccount());
			}else{
				account.setText("" + SystemContext.getInstance().getMobile());
			}
		}
		
		final ImageView newTag = (ImageView) findViewById(R.id.newTag);
		int umode = UpdataVersionUtil.getVersionUpdataMode(AppUtils.getLocalAppVersionCode(this), SystemConfig.C_VERSION_CODE,
				SystemConfig.UC_VERSION_CODE);
		if (umode == UpdataVersionUtil.UPDATA_MODE_NO) {
			newTag.setVisibility(View.INVISIBLE);
		} else {
			newTag.setVisibility(View.VISIBLE);
		}

		if(MainFragmentActivity.getInstance().isFlag()){
			newTag.setVisibility(View.INVISIBLE);
		}else{
			newTag.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 如果没有绑定手机 则弹出绑定手机的对话框
	 */
	private void showBindDialog(final View v) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(this, R.layout.dialog_upgrade_group, null);
		TextView cueword = (TextView) view.findViewById(R.id.upgrade_group_success);
		Button cancelBtn = (Button) view.findViewById(R.id.cannelBtn);
		cancelBtn.setText("继续退出");
		Button commitBtn = (Button) view.findViewById(R.id.commitBtn);
		commitBtn.setText("立刻绑定");
		cueword.setText("您尚未绑定过手机，退出登录ID会有遗失风险，系统建议您立刻绑定手机！");
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				logout(SystemContext.getInstance().getToken(), false, v);
			}
		});
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SysSettingActivity.this, BundPhoneActivity.class);
				SysSettingActivity.this.startActivity(intent);
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				v.setEnabled(true);
			}
		});
		dialog.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.logoutBtn) {
			// 如果是正常用户执行第一个分支， 如果是游客执行第二个分支
			if (SystemContext.getInstance().getIsGuest() == SystemConfig.LOGIN_TARGET_NORMAL) {
				v.setEnabled(false);
				logout(SystemContext.getInstance().getToken(), true, v);
			} else if(SystemContext.getInstance().getIsGuest() == SystemConfig.LOGIN_TARGET_QQ_WEIBO){
				ShareManager.getInstance().authorize(this, 1, QQ.NAME, new ShareCallbackListener());
				logout(SystemContext.getInstance().getToken(), false, v);
			} else if(SystemContext.getInstance().getIsGuest() == SystemConfig.LOGIN_TARGET_SINA_WEIBO){
				ShareManager.getInstance().authorize(this, 1, SinaWeibo.NAME, new ShareCallbackListener());
				logout(SystemContext.getInstance().getToken(), false, v);
			} else {
				v.setEnabled(false);
				showBindDialog(v);
			}
		} else if (v.getId() == R.id.pwItem) {
			// 跳转到修改密码界面
			Intent intent = new Intent(this, ModifyPasswrodActivity.class);
			this.startActivity(intent);
		} else if (v.getId() == R.id.backItem) {
			// 黑名单
			Intent intent = new Intent(SysSettingActivity.this, BlacklistActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.aboutItem) {
			// 关于我们
			Intent intent = new Intent(SysSettingActivity.this, HelpFragmentActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.userBackItem) {
			// 关于我们
			Intent intent = new Intent(SysSettingActivity.this, feedbackActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.msgTipItem) {
			// 消息提醒
			Intent intent = new Intent(SysSettingActivity.this, MessageRemindActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.soundOffOn) {
			boolean open = SystemContext.getInstance().getSoundOffOn();
			if (open) {
				SystemContext.getInstance().setSoundOffOn(false);
				soundOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
			} else {
				SystemContext.getInstance().setSoundOffOn(true);
				soundOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
			}
		} else if (v.getId() == R.id.updataItem) {
			UpdataVersionUtil.checkUpdata(this, true, null);
		} else if (v.getId() == R.id.cacheItem) {
			cleanCache();
		} 
//		else if (v.getId() == R.id.weiboBindingItem) {
//			Intent intent = new Intent(SysSettingActivity.this,BindWeiBoActivity.class);
//			this.startActivity(intent);
//		}
	}

	/**
	 * 退出、注销账号(清除全局配置用户信息、停止相关服务)
	 */
	private void logout(String token, final boolean flag, final View v) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(SysSettingActivity.this);
		dialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog不消失
		dialog.show();
		// 调用服务端注销功能
		ProxyFactory.getInstance().getAccountProxy().logout(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				v.setEnabled(true);
				dialog.dismiss();
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					LogUtil.e(TAG, "退出登录成功");
					jumpLoginView(flag);
					break;
				default:
					LogUtil.e(TAG, "退出登录失败:" + result);
					jumpLoginView(flag);
					break;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "退出登录失败:" + result);
				dialog.dismiss();
				v.setEnabled(true);
				jumpLoginView(flag);
			}
		}, this, token);
	}

	private void jumpLoginView(boolean flag) {
		// 停止相关服务
		Intent intent = new Intent(SysSettingActivity.this, MessageService.class);
		SysSettingActivity.this.stopService(intent);
		// 清除全局配置
		SystemContext.getInstance().cleanContext();
		// 处理返回结果，成功返回后跳转到主界面
		SystemContext.getInstance().setPasssword(null);
		SettingFragment.getInstance().setV(null);
		Intent intent2;
		if (flag) {
			intent2 = new Intent(SysSettingActivity.this, LoginActivity.class);
			SysSettingActivity.this.startActivity(intent2);
			BaseApplication app = (BaseApplication) getApplication();
			app.pushActivity(SysSettingActivity.this);
		} else {
			intent2 = new Intent(SysSettingActivity.this, UserMainActivity.class);
			SysSettingActivity.this.startActivity(intent2);
			SysSettingActivity.this.finish();
		}
	}

	/**
	 * 确认清除缓存
	 */
	private void cleanCache() {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final TextView txt = new TextView(this);
		txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(SysSettingActivity.this.getString(R.string.setting_sys_clean_cache_content));
		content.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(SysSettingActivity.this.getString(R.string.setting_sys_clean_cache_title));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 清空聊天cache和图片
				ProxyFactory.getInstance().getSettingProxy().cleanCache();
				SystemContext.getInstance().setHasNewMessage(true);
				ToastUtil.showToast(SysSettingActivity.this, SysSettingActivity.this.getString(R.string.setting_sys_clean_cache_success));
				dialog.dismiss();
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

}
