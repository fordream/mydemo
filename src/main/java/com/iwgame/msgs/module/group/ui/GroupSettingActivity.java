/**      
 * GroupSettingActivity.java Create on 2013-10-29     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import java.util.List;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;
import com.youban.msgs.R;

/**
 * @ClassName: GroupSettingActivity
 * @Description: 公会设置界面
 * @author 王卫
 * @date 2013-10-29 下午03:38:03
 * @Version 1.0
 * 
 */
public class GroupSettingActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "GroupSettingActivity";
	private long grid;
	private int rel;
	private GroupVo groupVo;
	private Button msgOffOn;
	private Button verifyOffOn;
	private LinearLayout msgArea;
	private LinearLayout verifyArea;
	private boolean isshowmsgsremind;//标记是否显示消息提醒栏目 
	private LinearLayout nullContent;//loading动画的父类
	private LinearLayout contentParent;//显示消息提醒的父类

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			grid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
			isshowmsgsremind = bundle.getBoolean("isshowmsgsremind");
			rel = bundle.getInt("rel");
		}
		init();
	}


	/**
	 * 刚进去 的时候还是
	 * 把标题等信息
	 * 显示出来
	 */
	private void init() {
		// 显示左边
		setLeftVisible(true);
		// 影藏右边
		setRightVisible(false);
		if(isshowmsgsremind)
			titleTxt.setText("消息提醒");
		else
			titleTxt.setText("加入验证");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View detailView = (LinearLayout) View.inflate(this, R.layout.group_setting, null);
		contentView.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(detailView, params);
		nullContent = (LinearLayout)detailView.findViewById(R.id.loading_parent);
		contentParent = (LinearLayout)detailView.findViewById(R.id.content_parent);
		msgArea = (LinearLayout) detailView.findViewById(R.id.msgArea);
		verifyArea = (LinearLayout) detailView.findViewById(R.id.verifyArea);
		msgOffOn = (Button) detailView.findViewById(R.id.msgOffOn);
		verifyOffOn = (Button) detailView.findViewById(R.id.verifyOffOn);
		msgOffOn.setOnClickListener(this);
		verifyOffOn.setOnClickListener(this);
		if (rel == 0) {// 没有关系
			finish();
		} else if (rel == 1) {// 普通会员
			verifyArea.setVisibility(View.GONE);
		} else if (rel == 2) {// 管理员
			verifyArea.setVisibility(View.GONE);
		} else if (rel == 3) {// 会长
			if(isshowmsgsremind){
				msgArea.setVisibility(View.VISIBLE);
				verifyArea.setVisibility(View.GONE);
			}else{
				msgArea.setVisibility(View.GONE);
				verifyArea.setVisibility(View.VISIBLE);
			}
		}
		contentParent.setVisibility(View.GONE);
		nullContent.setVisibility(View.VISIBLE);
		if(SystemContext.getInstance().getExtUserVo() != null)
			getMessageTip();
	}


	/**
	 * 加载的动画
	 */
	private void setLoadingUI(){
		nullContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		LinearLayout parent = (LinearLayout)view.findViewById(R.id.parent);
		parent.setBackgroundResource(R.color.set_first_bg);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		nullContent.addView(view, params);
	}


	/**
	 * 
	 */
	private void getGroupDetail() {
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		GroupVo gvo = groupDao.findGroupByGrid(grid);
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(grid);
		cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
		p.addParam(cdp.build());
		// 获取公会详情
		ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(new ProxyCallBack<List<GroupVo>>() {

			@Override
			public void onSuccess(List<GroupVo> result) {
				if(result != null && result.size() > 0){
					groupVo = result.get(0);
					initialize();
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "获取公会信息失败");
				finish();
			}
		}, this, p.build(), 5, null);
	}

	/**
	 * 获取消息提醒
	 */
	private void getMessageTip() {
		setLoadingUI();
		ProxyFactory.getInstance().getUserProxy().getMessageTip(new ProxyCallBack<XAction.XActionResult>() {

			@Override
			public void onSuccess(XActionResult result) {
				getGroupDetail();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				getGroupDetail();
			}
		}, this);
	}

	/**
	 * 初始化
	 */
	private void initialize() {
		nullContent.setVisibility(View.GONE);
		contentParent.setVisibility(View.VISIBLE);
		if (groupVo != null) {
			// 设置验证请求状态
			if (groupVo.getNeedValidate() == 0) {
				verifyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
			} else if (groupVo.getNeedValidate() == 1) {
				verifyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
			}
			// 设置消息提醒状态
			if (groupVo.getMsgoffon() == 0) {
				msgOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
			} else if (groupVo.getMsgoffon() == 1) {
				msgOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
			}
		} else {
			verifyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
			msgOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.msgOffOn) {// 公会消息提醒
			GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(this);
			if (groupVo.getMsgoffon() == 0) {
				groupVo.setMsgoffon(1);
				groupDao.updateOrInsertById(groupVo);
				msgOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
				setMessageTip(grid, 4, 1);
			} else if (groupVo.getMsgoffon() == 1) {
				groupVo.setMsgoffon(0);
				groupDao.updateOrInsertById(groupVo);
				msgOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
				setMessageTip(grid, 4, 0);
			}
		} else if (v.getId() == R.id.verifyOffOn) {// 验证
			if (groupVo.getNeedValidate() == 0) {
				groupVo.setNeedValidate(1);
				modifyVierfy(true);
			} else {
				groupVo.setNeedValidate(0);
				modifyVierfy(false);
			}
		}
	}

	/**
	 * 
	 * @param tid
	 * @param type
	 * @param status
	 */
	private void setMessageTip(Long tid, int type, int status) {
		ProxyFactory.getInstance().getUserProxy().setMessageTip(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}
		}, this, tid, type, status);
	}


	/**
	 * 修改是否需要验证
	 */
	private void modifyVierfy(final boolean isverify) {
		ProxyFactory.getInstance().getGroupProxy().modifyGroupSettingInvaliDate(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				GroupDao dao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getInstance().getContext());
				if (isverify) {
					groupVo.setNeedValidate(1);
					dao.updateOrInsertById(groupVo);
					verifyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
				} else {
					groupVo.setNeedValidate(0);
					dao.updateOrInsertById(groupVo);
					verifyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "修改加入公会是否需要验证失败");
			}
		}, this, null, null, null, null, null, isverify, grid);
	}
}
