/**      
 * MessageRemindActivity.java Create on 2013-11-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;
import com.youban.msgs.R;

/**
 * @ClassName: MessageRemindActivity
 * @Description: 公会消息提醒页面
 * @author 王卫
 * @date 2013-11-26 下午5:55:58
 * @Version 1.0
 * 
 */
public class MessageRemindActivity extends BaseActivity implements OnClickListener {

	private Button msgGlobalOffOn;
	private Button chatOffOn;
	private Button commentMyOffOn;
	private Button wonderfullOffOn;
	private LinearLayout branch;
	private ScrollView sv;
	private LinearLayout nullContent;
	private Button fateBtnOffOn;
	
	
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
	 * 获取消息
	 * 是否置为关闭还是开启状态
	 */
	private void getMessageTip(){
		setLoadingUI();
		ProxyFactory.getInstance().getUserProxy().getMessageTip(new ProxyCallBack<XAction.XActionResult>() {

			@Override
			public void onSuccess(XActionResult result) {
				setBtnOnOrOff();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				setBtnOnOrOff();
			}
		}, this);
	}
	
	/**
	 * 设置右边的按钮
	 * 是开着  还是关闭状态
	 */
	private void setBtnOnOrOff(){
		nullContent.setVisibility(View.GONE);
		boolean open = SystemContext.getInstance().getMsgGlobalOffOn();
		sv.setVisibility(View.VISIBLE);
		if (open) {
			msgGlobalOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
			branch.setVisibility(View.VISIBLE);
		} else {
			msgGlobalOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
			branch.setVisibility(View.INVISIBLE);
		}
		open = SystemContext.getInstance().getMsgChatOffOn();
		if (open) {
			chatOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
		} else {
			chatOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
		}
		open = SystemContext.getInstance().getCommentMyOffOn();
		if (open) {
			commentMyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
		} else {
			commentMyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
		}
		open = SystemContext.getInstance().getWonderfullOffOn();
		if (open) {
			wonderfullOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
		} else {
			wonderfullOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
		}
		open = SystemContext.getInstance().getFateRecommendOffOn();
		if(open){
			fateBtnOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
		}else{
			fateBtnOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
		}
	}
	
	
	/**
	 * 初始化
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		titleTxt.setText("消息提醒");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (LinearLayout) View.inflate(this, R.layout.setting_msgremind, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		sv = (ScrollView)findViewById(R.id.detailInfo);
		msgGlobalOffOn = (Button) findViewById(R.id.msgGlobalOffOn);
		nullContent = (LinearLayout)findViewById(R.id.nullContent);
		msgGlobalOffOn.setOnClickListener(this);
		chatOffOn = (Button) findViewById(R.id.chatOffOn);
		chatOffOn.setOnClickListener(this);
		commentMyOffOn = (Button) findViewById(R.id.commentMyOffOn);
		commentMyOffOn.setOnClickListener(this);
		wonderfullOffOn = (Button) findViewById(R.id.wonderful_recommend);
		wonderfullOffOn.setOnClickListener(this);
		fateBtnOffOn = (Button)findViewById(R.id.fate_friends);
		fateBtnOffOn.setOnClickListener(this);
		branch = (LinearLayout) findViewById(R.id.branch);
		sv.setVisibility(View.INVISIBLE);
		getMessageTip();
	}

	/**
	 * 加载的loading 方法 
	 */
	private void setLoadingUI(){
		nullContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		LinearLayout parent = (LinearLayout)view.findViewById(R.id.parent);
		parent.setBackgroundResource(R.color.list_first_bg);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		nullContent.addView(view, params);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.msgGlobalOffOn) {// 全局消息提醒
			boolean open = SystemContext.getInstance().getMsgGlobalOffOn();
			if (open) {
				SystemContext.getInstance().setMsgGlobalOffOn(false);
				msgGlobalOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
				branch.setVisibility(View.INVISIBLE);
				setMessageTip(null, 1, 0);
			} else {
				SystemContext.getInstance().setMsgGlobalOffOn(true);
				msgGlobalOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
				branch.setVisibility(View.VISIBLE);
				setMessageTip(null, 1, 1);
			}
		} else if (v.getId() == R.id.chatOffOn) {// 聊天
			boolean open = SystemContext.getInstance().getMsgChatOffOn();
			if (open) {
				SystemContext.getInstance().setMsgChatOffOn(false);
				chatOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
				setMessageTip(null, 2, 0);
			} else {
				SystemContext.getInstance().setMsgChatOffOn(true);
				chatOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
				setMessageTip(null, 2, 1);
			}
		}else if (v.getId() == R.id.commentMyOffOn) {// 评论我的
			boolean open = SystemContext.getInstance().getCommentMyOffOn();
			if (open) {
				SystemContext.getInstance().setCommentMyOffOn(false);
				commentMyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
				setMessageTip(null, 3, 0);
			} else {
				SystemContext.getInstance().setCommentMyOffOn(true);
				commentMyOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
				setMessageTip(null, 3, 1);
			}
		} else if (v.getId() == R.id.wonderful_recommend) {// 精彩推荐
			boolean open = SystemContext.getInstance().getWonderfullOffOn();
			if (open) {
				SystemContext.getInstance().setWonderfullOffOn(false);
				wonderfullOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
				setMessageTip(null, 5, 0);
			} else {
				SystemContext.getInstance().setWonderfullOffOn(true);
				wonderfullOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
				setMessageTip(null, 5, 1);
			}
		}else if(v.getId() == fateBtnOffOn.getId()){//缘分好友
			boolean open = SystemContext.getInstance().getFateRecommendOffOn();
			if(open){
				SystemContext.getInstance().setFateRecommendOffOn(false);
				fateBtnOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_normal);
				setMessageTip(null, 6, 0);
			}else{
				SystemContext.getInstance().setFateRecommendOffOn(true);
				fateBtnOffOn.setBackgroundResource(R.drawable.group_verify_checkbox_pressed);
				setMessageTip(null, 6, 1);
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
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub

			}
		}, this, tid, type, status);
	}

}
