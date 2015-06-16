/**      
* CaptchaReceiver.java Create on 2013-10-17     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.receiver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.EditText;

import com.youban.msgs.R;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;

/** 
 * @ClassName: CaptchaReceiver 
 * @Description: 短信验证码广播接收
 * @author 王卫
 * @date 2013-10-17 上午10:17:55 
 * @Version 1.0
 * 
 */
public class CaptchaReceiver extends BroadcastReceiver {

	private static final String TAG = "CaptchaReceiver";
	
	private EditText mCaptchaTxt;
	
	/**
	 * 
	 */
	public CaptchaReceiver(Context context, EditText captchaTxt) {
		ToastUtil.showToast(context, context.getString(R.string.captcha_sms_receiver_tip));
		this.mCaptchaTxt = captchaTxt;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtil.d(TAG, "------->收到短信");
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			Object[] pdus = (Object[])intent.getExtras().get("pdus");
			SmsMessage[] message = new SmsMessage[pdus.length];
			StringBuffer sb = new StringBuffer();
			LogUtil.d(TAG, "------->pdus长度="+pdus.length);
			for (int i = 0; i < pdus.length; i++) {
				//判断是否验证码
				message[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
				String content = message[i].getDisplayMessageBody();
				Pattern p = Pattern.compile("【游伴网络】");
				Matcher m = p.matcher(content);
				while (m.find()) {
					String exp = "】";
					int index = content.indexOf(exp);
					if(index != -1 && content.length() > index + 5){
						String str2 = content.substring(index+1, index+5);
						if(mCaptchaTxt != null && str2.length() == 4 && str2.matches("[0-9]+"))
							mCaptchaTxt.setText(str2);
					}
				}
				sb.append("------>接收到的短信来自：");
				sb.append(message[i].getDisplayOriginatingAddress());
				sb.append("------>内容："+message[i].getDisplayMessageBody());
			}
			LogUtil.d(TAG, sb.toString());
		}else{
			return;
		}
	}

}
