/**      
 * UMUtil.java Create on 2014-6-19     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.util.HashMap;

import android.content.Context;

import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: UMUtil
 * @Description: TODO(...)
 * @author 王卫
 * @date 2014-6-19 下午6:29:28
 * @Version 1.0
 * 
 */
public class UMUtil {

	public static void sendEvent(Context context, String event, String fromId, String fromName, String toId, String toName, Boolean success) {
		try {
			if (context != null && event != null) {
				HashMap<String, String> ummap = new HashMap<String, String>();
				ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
				if (vo != null) {
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(vo.getUserid()));
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, vo.getUsername());
				}
				if (fromId != null)
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_ID, fromId);
				if (fromName != null)
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_NAME, fromName);
				if (toId != null)
					ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, toId);
				if (toName != null)
					ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, toName);
				if (success != null) {
					if (success)
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, "1");
					else
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, "0");
				}
				MobclickAgent.onEvent(context, event, ummap);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
