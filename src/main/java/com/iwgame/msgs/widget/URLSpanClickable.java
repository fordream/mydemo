/**      
* URLSpanClickable.java Create on 2013-12-19     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.widget;

/** 
 * @ClassName: URLSpanClickable 
 * @Description: TODO(urlspan是否响应单击事件的对象) 
 * @author chuanglong
 * @date 2013-12-19 下午2:28:06 
 * @Version 1.0
 * 
 */

public  class  URLSpanClickable{
	
	boolean isClickUrlable = true ;
	public URLSpanClickable()
	{
	    
	}
	public boolean getIsClickUrlable()
	{
	    return isClickUrlable ;
	}
	public void setIsClickUrlable(boolean able){
	    isClickUrlable = able ;
	}
}