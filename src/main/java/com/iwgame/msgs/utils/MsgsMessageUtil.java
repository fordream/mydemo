/**      
* MsgsMessageUtil.java Create on 2013-8-8     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.utils;

import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;

/** 
 * @ClassName: MsgsMessageUtil 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2013-8-8 上午10:11:43 
 * @Version 1.0
 * 
 */
public class MsgsMessageUtil {

	public static ExtensionRegistryLite registry = ExtensionRegistryLite.newInstance();
	
	public static void registerExtension(final GeneratedMessageLite.GeneratedExtension<?, ?> extension) {
		registry.add(extension);
	}
	
}
