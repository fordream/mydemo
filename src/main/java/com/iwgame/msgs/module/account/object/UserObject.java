/**      
 * UserVo.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.account.object;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: UserVo
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-24 下午10:38:13
 * @Version 1.0
 * 
 */
public class UserObject implements Serializable {

	public static final int USER_SOURCE_TYPE_CONTACT = 1;
	public static final int USER_SOURCE_TYPE_WEIBO = 2;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4066141216133457098L;

	private long uid;

	private String nickname;

	private String avatar;

	private int sex;

	private int age = -1;

	private int rel;

	private List<Long> gids;

	private int gameCount;
	// 距离
	private int distance;
	// 排序ID（用于推荐用户）
	private long sortId;
	// 心情
	private String mood;
	// 最后登陆时间
	private long lastLogin;
	// 位置信息
	private String position;
	// 通讯录名称
	private String contactName;
	// 微博名称
	private String weiboName;
	// 手机号码
	private String mobile;
	// 微博ID
	private String weibo;
	// 用户类型1：通讯录用户2：微博用户
	private int usertype;
	//等级
	private int grade;
	
	//共同关注贴吧数
	private int count;

	/**
	 * 用户的动态
	 */
	private String news;
	
	public String getNews() {
		return news;
	}

	public void setNews(String news) {
		this.news = news;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getSortId() {
		return sortId;
	}

	public void setSortId(long sortId) {
		this.sortId = sortId;
	}

	/**
	 * @return the uid
	 */
	public long getUid() {
		return uid;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(long uid) {
		this.uid = uid;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the avatar
	 */
	public String getAvatar() {
		return avatar;
	}

	/**
	 * @param avatar
	 *            the avatar to set
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	/**
	 * @return the sex
	 */
	public int getSex() {
		return sex;
	}

	/**
	 * @param sex
	 *            the sex to set
	 */
	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the gids
	 */
	public List<Long> getGids() {
		return gids;
	}

	/**
	 * @param gids
	 *            the gids to set
	 */
	public void setGids(List<Long> gids) {
		this.gids = gids;
	}

	/**
	 * @return the gameCount
	 */
	public int getGameCount() {
		return gameCount;
	}

	/**
	 * @param gameCount
	 *            the gameCount to set
	 */
	public void setGameCount(int gameCount) {
		this.gameCount = gameCount;
	}

	/**
	 * @return the rel
	 */
	public int getRel() {
		return rel;
	}

	/**
	 * @param rel
	 *            the rel to set
	 */
	public void setRel(int rel) {
		this.rel = rel;
	}

	/**
	 * @return the distance
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * @param distance
	 *            the distance to set
	 */
	public void setDistance(int distance) {
		this.distance = distance;
	}

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(long lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getWeiboName() {
		return weiboName;
	}

	public void setWeiboName(String weiboName) {
		this.weiboName = weiboName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public int getUsertype() {
		return usertype;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

}
