/**      
 * MessageDialogManager.java Create on 2015-4-2     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.message.ui;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: MessageDialogManager
 * @Description: 消息对话框管理类
 * @author 王卫
 * @date 2015-4-2 上午11:09:53
 * @Version 1.0
 * 
 */
public class MessageDialogManager {

	private static final String TAG = "MessageDialogManager";

	private static byte[] lock = new byte[0];

	private static MessageDialogManager instance = null;
	
	// 消息队列
	private static Queue<MessageVo> messageQueue = new ConcurrentLinkedQueue<MessageVo>();
	
	private static boolean flag = true;

	private MessageDialogManager() {
		startUpConsumeThread(messageQueue);
	}

	public static MessageDialogManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new MessageDialogManager();
				return instance;
			}
		} else {
			return instance;
		}
	}
	
	/**
	 * 
	 * @param vo
	 * @return
	 */
	public boolean offer(MessageVo vo){
		LogUtil.d(TAG, "MessageDialogManager::offer:"+vo.toString());
		return messageQueue.offer(vo);
	}
	
	/**
	 * 启动处理callback线程
	 */
	private static void startUpConsumeThread(final Queue<MessageVo> queue) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (flag) {
					if (!queue.isEmpty()) {
						final Activity currentActivity = SystemContext.getInstance().getCurrentActivity();
						if (currentActivity != null) {
							final MessageVo vo = queue.poll();
							LogUtil.d(TAG, "MessageDialogManager::startUpConsumeThread:vo is"+vo.toString());
							currentActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_REWARD_LOGIN))) {
										new LoginRewardDialog(currentActivity, R.style.SampleTheme_Light, vo).show();
									} else if (vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_REWARD_POINT_EXP))) {
										new RewardDialog(currentActivity, R.style.SampleTheme_Light, vo).show();
									} else if (vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_USER_GRADE_UP))) {
										new UpgradeDialog(currentActivity, R.style.SampleTheme_Light, vo).show();
									}
								}
							});
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

}
