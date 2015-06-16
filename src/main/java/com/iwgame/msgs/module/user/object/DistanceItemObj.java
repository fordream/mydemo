/**      
* DistanceItemVo.java Create on 2013-10-16     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.user.object;

/** 
 * @ClassName: DistanceItemVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2013-10-16 下午05:26:50 
 * @Version 1.0
 * 
 */
public class DistanceItemObj {

	private long uid;
	
	private int destance;

	/**
	 * @return the uid
	 */
	public long getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(long uid) {
		this.uid = uid;
	}

	/**
	 * @return the destance
	 */
	public int getDestance() {
		return destance;
	}

	/**
	 * @param destance the destance to set
	 */
	public void setDestance(int destance) {
		this.destance = destance;
	}
	
}
