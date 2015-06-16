/**      
* BootCompletedReceiver.java Create on 2013-11-11     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.iwgame.msgs.service.MessageService;
import com.iwgame.utils.LogUtil;

/** 
 * @ClassName: BootCompletedReceiver 
 * @Description: TODO(监控开机启动服务) 
 * @author 吴禹青
 * @date 2013-11-11 下午12:10:56 
 * @Version 1.0
 * 
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	    LogUtil.d("BootCompletedReceiver", intent.toString());
	   doBootCompleted( context);
		
	}

	/**
	 * 做开机动作，启动定时器启动服务
	 */
	private void doBootCompleted(Context context) {
		// TODO Auto-generated method stub
		//
		
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		long triggerAtTime = SystemClock.elapsedRealtime();
		//10s 一个周期，不停的发送广播
		long interval =10*1000;
		Intent intent = new Intent();
		intent.setClass(context, MessageService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, interval, pi);
		
	}

}
