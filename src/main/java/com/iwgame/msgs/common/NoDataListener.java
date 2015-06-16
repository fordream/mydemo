/**      
* NoDataListener.java Create on 2014-1-9     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common;

/** 
 * @ClassName: NoDataListener 
 * @Description: TODO(没有数据时向外的通知，这样外部能够根据他做相应的事情) 
 * @author chuanglong
 * @date 2014-1-9 下午5:23:54 
 * @Version 1.0
 * 
 */
public interface NoDataListener {
    public void noDataNotify();
}
