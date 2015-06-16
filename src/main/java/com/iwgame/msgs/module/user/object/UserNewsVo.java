/**      
* UserNewsVo.java Create on 2014-6-8     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.user.object;

/** 
 * @ClassName: UserNewsVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-6-8 下午5:00:22 
 * @Version 1.0
 * 
 */
public class UserNewsVo {
	
	private long uid;
	
	private String news;

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getNews() {
		return news;
	}

	public void setNews(String news) {
		this.news = news;
	}

}
