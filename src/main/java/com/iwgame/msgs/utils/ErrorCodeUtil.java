/**      
 * ErrorCodeUtil.java Create on 2013-12-4     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.lang.reflect.Field;

import android.content.Context;

import com.youban.msgs.R;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: ErrorCodeUtil
 * @Description: TODO(错误码的统一处理)
 * @author chuanglong
 * @date 2013-12-4 下午12:09:04
 * @Version 1.0
 * 
 */
public class ErrorCodeUtil {

	private static void handleErrorCode(Context context, Integer errorCode) {
		try {
			Field f = (Field) R.string.class.getDeclaredField("msgs_" + Math.abs(errorCode));
			int i = f.getInt(R.string.class);
			if(context != null)
			      ToastUtil.showToast(context, context.getResources().getString(i));
		} catch (Exception e) {
		        if(context != null)
			     ToastUtil.showToast(context, context.getString(R.string.ec_usnknown) + "errorcode:" + errorCode.intValue());
			//e.printStackTrace();
		}
	}
	
	private static void handleErrorCode(Context context, Integer errorCode,Object... formatArgs) {
		try {
			Field f = (Field) R.string.class.getDeclaredField("msgs_" + Math.abs(errorCode));
			int i = f.getInt(R.string.class);
			if(context != null)
			     ToastUtil.showToast(context, String.format(context.getResources().getString(i), formatArgs));
		} catch (Exception e) {
		    if(context != null) 
			     ToastUtil.showToast(context, context.getString(R.string.ec_usnknown) + "errorcode:" + errorCode.intValue());
			//e.printStackTrace();
		}
	}

	public static void handleErrorCode(Context context, Integer errorCode, String errorMsg) {
		if (errorMsg != null) {
			ToastUtil.showToast(context, errorMsg);
		} else {
			handleErrorCode(context, errorCode);
		}
	}
	
	public static void handleErrorCode(Context context,Integer errorCode, String errorMsg,Object... formatArgs){
	    if (errorMsg != null) {
		ToastUtil.showToast(context, errorMsg);
	} else {
		handleErrorCode(context, errorCode,formatArgs);
	}
	    
	}
}
