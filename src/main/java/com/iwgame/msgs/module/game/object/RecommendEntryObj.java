/**      
* RecommendEntryVo.java Create on 2013-10-8     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.game.object;

/** 
 * @ClassName: RecommendEntryVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2013-10-8 下午08:04:26 
 * @Version 1.0
 * 
 */
public class RecommendEntryObj {

	private long id;
	
	private boolean isRecommend;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the isRecommend
	 */
	public boolean isRecommend() {
		return isRecommend;
	}

	/**
	 * @param isRecommend the isRecommend to set
	 */
	public void setRecommend(boolean isRecommend) {
		this.isRecommend = isRecommend;
	}
	
}
