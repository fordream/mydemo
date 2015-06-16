/**      
* MyLocationListenner.java Create on 2014-1-26     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.common;

import com.baidu.location.BDLocation;

public interface LocationCallBack {
	
    public void onBack(BDLocation bdLocation);

}
