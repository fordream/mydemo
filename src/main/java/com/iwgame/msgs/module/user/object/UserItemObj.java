/**      
* UserItemVo.java Create on 2013-8-22     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.user.object;

/** 
 * @ClassName: UserItemVo 
 * @Description: 用户列表条目对象
 * @author 王卫
 * @date 2013-8-22 上午11:30:10 
 * @Version 1.0
 * 
 */
public class UserItemObj {
	//昵称
	private String nickName;
	//头像URL
	private String avatarUrl;
	//心情签名
	private String sign;
	//修改时间
	private long changeTime;
	//用户ID
	private long uid;
	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}
	/**
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	/**
	 * @return the avatarUrl
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}
	/**
	 * @param avatarUrl the avatarUrl to set
	 */
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}
	/**
	 * @param sign the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}
	/**
	 * @return the changeTime
	 */
	public long getChangeTime() {
		return changeTime;
	}
	/**
	 * @param changeTime the changeTime to set
	 */
	public void setChangeTime(long changeTime) {
		this.changeTime = changeTime;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	
}
