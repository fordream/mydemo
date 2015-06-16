/**      
 * UserItemObj.java Create on 2013-10-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.object;

import com.iwgame.msgs.vo.local.MessageVo;

/**
 * @ClassName: UserItemObj
 * @Description: 公会管理用户列表条目数据
 * @author 王卫
 * @date 2013-10-28 上午11:04:33
 * @Version 1.0
 * 
 */
public class UserItemObj {
	// 用户ID
	private long uid;
	// 昵称
	private String nickname;
	// 用户头像
	private String avatar;
	// 用户性别
	private int sex = -1;
	// 用户心情
	private String mood;
	// 用户是否批准过0未批准1已批准
	private int status;
	// 用户条目是否选中
	private boolean isChecked;
	// 是否是管理员
	private int rel;
	// 属于的公会
	private long grid;
	
	private int age;
	//积分值
	private long point;
	//排名
	private int top;
	//等级
	private int grade;
	//消息时间
	private long msgCreatTime;
	//活跃时间
	private long atime;

	// 用户在公会中的最后一次发言
	private MessageVo messageVo;

	private String remark;
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	/**
	 * @return the mood
	 */
	public String getMood() {
		return mood;
	}

	/**
	 * @param mood
	 *            the mood to set
	 */
	public void setMood(String mood) {
		this.mood = mood;
	}

	/**
	 * @return the isChecked
	 */
	public boolean isChecked() {
		return isChecked;
	}

	/**
	 * @param isChecked
	 *            the isChecked to set
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	/**
	 * @return the grid
	 */
	public long getGrid() {
		return grid;
	}

	/**
	 * @param grid
	 *            the grid to set
	 */
	public void setGrid(long grid) {
		this.grid = grid;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the messageVo
	 */
	public MessageVo getMessageVo() {
		return messageVo;
	}

	/**
	 * @param messageVo
	 *            the messageVo to set
	 */
	public void setMessageVo(MessageVo messageVo) {
		this.messageVo = messageVo;
		this.msgCreatTime = messageVo.getCreateTime();
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public UserItemObj() {

	}

	public UserItemObj(UserItemObj obj) {
		this.uid = obj.getUid();
		this.nickname = obj.getNickname();
		this.avatar = obj.getAvatar();
		this.sex = obj.getSex();
		this.mood = obj.getMood();
		this.isChecked = obj.isChecked();
		this.rel = obj.getRel();
		this.grid = obj.getGrid();
		this.messageVo = obj.getMessageVo();
		this.top = obj.getTop();
		this.point = obj.getPoint();
	}

	public long getPoint() {
		return point;
	}

	public void setPoint(long point) {
		this.point = point;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public long getMsgCreatTime() {
		return msgCreatTime;
	}

	public void setMsgCreatTime(long msgCreatTime) {
		this.msgCreatTime = msgCreatTime;
	}

	public long getAtime() {
		return atime;
	}

	public void setAtime(long atime) {
		this.atime = atime;
	}

}
