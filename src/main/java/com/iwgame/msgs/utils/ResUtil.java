/**      
 * ResUtil.java Create on 2013-11-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;

/**
 * @ClassName: ResUtil
 * @Description: TODO(资源工具类)
 * @author chuanglong
 * @date 2013-11-28 上午10:45:23
 * @Version 1.0
 * 
 */
public class ResUtil { 

	public static String getSmallRelUrl(String resid) {
	    if(resid !=null && !resid.equals("") && !resid.toLowerCase().equals("null"))
		return SystemContext.getInstance().getResIP() + "/" + SystemConfig.RESCOURCE_TYPE_SMALL + "/" + resid;
	    else
	       return null ;
	}

	public static String getMediumRelUrl(String resid) {
		if(resid !=null && !resid.equals("") && !resid.toLowerCase().equals("null"))
			return SystemContext.getInstance().getResIP() + "/" + SystemConfig.RESCOURCE_TYPE_MIDIUM + "/" + resid;
		else
			return null ;
	}

	public static String getOriginalRelUrl(String resid) {
	    if(resid !=null && !resid.equals("") && !resid.toLowerCase().equals("null"))
		return SystemContext.getInstance().getResIP() + "/" + resid;
	    else
	       return null ;
		
	}

}
