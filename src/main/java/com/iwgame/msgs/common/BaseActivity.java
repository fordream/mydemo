/**      
 * BaseActicity.java Create on 2013-8-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: BaseActicity
 * @Description: 应用ACTIVITY基类（同一布局风格）
 * @author 王卫
 * @date 2013-8-26 下午05:14:40
 * @Version 1.0
 * 
 */
public class BaseActivity extends BaseSuperActivity{

	// 页面TITLE
	public static String TITLE = "title";
	// 是否显示左边
	public static String IS_SHOW_LEFT = "isShowLeft";
	// 是否显示右边
	public static String IS_SHOW_RIGHT = "isShowRight";
	// 是否显示TOP搜索框内容
	public static String VISIBLE_TOP_MODE = "isShowTop";
	// 用于列表类型
	public static String MODE = "mode";

	// 标题
	protected TextView titleTxt;

	protected LinearLayout contentView;
	
	public boolean isinit = true;
	
	protected LinearLayout rightView;
	
	private LinearLayout top_layout;
	
	protected String pageName = null;

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(null == pageName || pageName.isEmpty()){
			MobclickAgent.onPageStart(this.getClass().getName());
		}else{
			MobclickAgent.onPageStart(pageName);
		}
		
		MobclickAgent.onResume(this);
		if (SystemContext.getInstance().isUnAuth()) {
			// 清除全局配置
			SystemContext.getInstance().cleanContext();
			// 会话失效， 主动注销，token不存在/认证失败，都到登录页
			Intent intent = new Intent(this, LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED, true);
			intent.putExtras(bundle);
			startActivity(intent);
			return;
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		if(null == pageName || pageName.isEmpty()){
			MobclickAgent.onPageEnd(this.getClass().getName());
		}else{
			MobclickAgent.onPageEnd(pageName);
		}
		//MobclickAgent.onPageEnd(titleTxt.getText().toString());
		MobclickAgent.onPause(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!isinit)
			return;
		setContentView(R.layout.common_content);
		titleTxt = (TextView) findViewById(R.id.titleTxt);
		contentView = (LinearLayout) findViewById(R.id.contentView);
		rightView = (LinearLayout) findViewById(R.id.rightView);

		// 设置返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
	    backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				back();
			}
		});

	}

	/**
	 * 返回
	 */
	protected void back() {
		BaseActivity.this.finish();
	}

	/**
	 * 获取中间布局容器
	 * 
	 * @param visible
	 */
	protected LinearLayout getContentView() {
		return (LinearLayout) findViewById(R.id.contentView);
	}

	/**
	 * 设置标题
	 * 
	 * @param visible
	 */
	protected void setTitleTxt(String txt) {
		titleTxt.setText(txt);
	}

	/**
	 * 设置布局左边是否显示
	 * 
	 * @param visible
	 */
	protected void setLeftVisible(Boolean visible) {
		View view = findViewById(R.id.left);
		if (view != null) {
			if (visible) {
				// 显示左边菜单
				view.setVisibility(View.VISIBLE);
			} else {
				// 隐藏左边菜单
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 设置布局右边是否显示
	 * 
	 * @param visible
	 */
	protected void setRightVisible(Boolean visible) {
		View view = findViewById(R.id.right);
		if (view != null) {
			if (visible) {
				// 显示左边菜单
				view.setVisibility(View.VISIBLE);
			} else {
				// 隐藏左边菜单
				view.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 设置内容UI
	 * 
	 * @param v
	 */
	protected void addContentViewChild(View v) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// 设置内容UI
		contentView.addView(v, params);
	}
	
	/**
	 * 添加右边菜单
	 * @param v
	 */
	protected void addRightView(View v){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rightView.addView(v, params);
	}

}
