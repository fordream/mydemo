/**      
* NotifyLister.java Create on 2013-10-17     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.baidu;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;

/** 
 * @ClassName: NotifyLister 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2013-10-17 下午04:46:04 
 * @Version 1.0
 * 
 */
public class NotifyLister extends BDNotifyListener {
/* (non-Javadoc)
 * @see com.baidu.location.BDNotifyListener#onNotify(com.baidu.location.BDLocation, float)
 */
@Override
public void onNotify(BDLocation arg0, float arg1) {
	// TODO Auto-generated method stub
	super.onNotify(arg0, arg1);
}
}
