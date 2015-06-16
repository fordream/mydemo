/**      
 * MessageDialog.java Create on 2015-3-26     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.message.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.BaseSuperFragmentActivity;
import com.iwgame.msgs.vo.local.MessageVo;

/**
 * @ClassName: MessageDialog
 * @Description: 消息弹出提示对话框
 * @author 王卫
 * @date 2015-3-26 下午6:26:09
 * @Version 1.0
 * 
 */
public class MessageDialog extends Dialog {

	protected MessageVo messageVo;

	/**
	 * @param context
	 */
	public MessageDialog(Context context) {
		super(context);
		init();
	}

	/**
	 * @param context
	 * @param theme
	 */
	public MessageDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	/**
	 * @param context
	 * @param theme
	 * @param messageVo
	 */
	public MessageDialog(Context context, int theme, MessageVo messageVo) {
		super(context, theme);
		if (context != null) {
			if (context instanceof BaseSuperActivity) {
				((BaseSuperActivity) context).setDialog(this);
			} else if (context instanceof BaseSuperFragmentActivity) {
				((BaseSuperFragmentActivity) context).setDialog(this);
			}
		}
		this.messageVo = messageVo;
		init();
	}

	/**
	 * @param context
	 * @param cancelable
	 * @param cancelListener
	 */
	public MessageDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init();
	}

	protected void init() {
		// 基础布局设置
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 点击其他区域dialog消失
		setCanceledOnTouchOutside(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#show()
	 */
	@Override
	public void show() {
		try {
			super.show();
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						Context context = getContext();
						if (context != null) {
							if (MessageDialog.this != null && MessageDialog.this.isShowing()) {
								MessageDialog.this.dismiss();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
