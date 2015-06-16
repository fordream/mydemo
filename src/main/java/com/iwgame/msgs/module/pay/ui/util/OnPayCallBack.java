/**      
* OnPayCallBack.java Create on 2015-5-28     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.pay.ui.util;

/** 
 * @ClassName: OnPayCallBack 
 * @Description: 支付回调 
 * @author 王卫
 * @date 2015-5-28 下午6:32:19 
 * @Version 1.0
 * 
 */
public interface OnPayCallBack {

	void onSuccess();
	
	void onFailure(int type);
	
}
