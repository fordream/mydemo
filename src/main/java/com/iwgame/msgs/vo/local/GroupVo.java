/**      
 * GroupVo.java Create on 2013-10-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/**
 * @ClassName: GroupVo
 * @Description: 公会表
 * @author 王卫
 * @date 2013-10-23 下午05:24:50
 * @Version 1.0
 * 
 */
public class GroupVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6507780027387310388L;
	// 本地数据库ID
	private long id;
	// 公会ID
	private long grid;
	// 公会名称
	private String name;
	// 公会头像
	private String avatar;
	// 编号
	private long serial;
	// 会长id
	private long presidentId;
	// 贴吧id
	private long gid;
	// 公告
	private String notice;
	// 简介
	private String undesc;
	// 创建日期
	private long creatTime;
	// 最后一次更新时间
	private long utime;
	// 公会关系最后关系或用户信息更新时间
	private long ureltime;
	// 批准用户清除的最大用户ID
	private long cleanMaxUid;
	// 公会总人数
	private int total;
	// 会长名称
	private String presidentName;
	// 是否需要验证（0不需要1需要）
	private int needValidate = -1;
	// 排序ID（用户推荐公会，不存数据库）
	private long sortId;
	// 网络开关
	private int netoffon = 0;
	// 消息开关
	private int msgoffon = 1;
	//附近总人数
	private int nearTotal;
	//公会最大容纳数
	private int maxcount;
	//服务器id
	private long sid;
	
	
	public int getMaxcount() {
		return maxcount;
	}

	public void setMaxcount(int maxcount) {
		this.maxcount = maxcount;
	}

	//====================== 扩展属性 ================
	private String gameIcon;
	//等级
	private int grade;
	//积分
	private int point;

	private int relWithGroup;
	
	
	public int getRelWithGroup() {
		return relWithGroup;
	}

	public void setRelWithGroup(int relWithGroup) {
		this.relWithGroup = relWithGroup;
	}

	public long getSortId() {
		return sortId;
	}

	public void setSortId(long sortId) {
		this.sortId = sortId;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the serial
	 */
	public long getSerial() {
		return serial;
	}

	/**
	 * @param serial
	 *            the serial to set
	 */
	public void setSerial(long serial) {
		this.serial = serial;
	}

	/**
	 * @return the presidentId
	 */
	public long getPresidentId() {
		return presidentId;
	}

	/**
	 * @param presidentId
	 *            the presidentId to set
	 */
	public void setPresidentId(long presidentId) {
		this.presidentId = presidentId;
	}

	/**
	 * @return the gid
	 */
	public long getGid() {
		return gid;
	}

	/**
	 * @param gid
	 *            the gid to set
	 */
	public void setGid(long gid) {
		this.gid = gid;
	}

	/**
	 * @return the notice
	 */
	public String getNotice() {
		return notice;
	}

	/**
	 * @param notice
	 *            the notice to set
	 */
	public void setNotice(String notice) {
		this.notice = notice;
	}

	/**
	 * @return the undesc
	 */
	public String getUndesc() {
		return undesc;
	}

	/**
	 * @param undesc
	 *            the undesc to set
	 */
	public void setUndesc(String undesc) {
		this.undesc = undesc;
	}

	/**
	 * @return the creatTime
	 */
	public long getCreatTime() {
		return creatTime;
	}

	/**
	 * @param creatTime
	 *            the creatTime to set
	 */
	public void setCreatTime(long creatTime) {
		this.creatTime = creatTime;
	}

	/**
	 * @return the utime
	 */
	public long getUtime() {
		return utime;
	}

	/**
	 * @param utime
	 *            the utime to set
	 */
	public void setUtime(long utime) {
		this.utime = utime;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the ureltime
	 */
	public long getUreltime() {
		return ureltime;
	}

	/**
	 * @param ureltime
	 *            the ureltime to set
	 */
	public void setUreltime(long ureltime) {
		this.ureltime = ureltime;
	}

	/**
	 * @return the cleanMaxUid
	 */
	public long getCleanMaxUid() {
		return cleanMaxUid;
	}

	/**
	 * @param cleanMaxUid
	 *            the cleanMaxUid to set
	 */
	public void setCleanMaxUid(long cleanMaxUid) {
		this.cleanMaxUid = cleanMaxUid;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * @return the presidentName
	 */
	public String getPresidentName() {
		return presidentName;
	}

	/**
	 * @param presidentName
	 *            the presidentName to set
	 */
	public void setPresidentName(String presidentName) {
		this.presidentName = presidentName;
	}

	/**
	 * @return the needValidate
	 */
	public int getNeedValidate() {
		return needValidate;
	}

	/**
	 * @param needValidate
	 *            the needValidate to set
	 */
	public void setNeedValidate(int needValidate) {
		this.needValidate = needValidate;
	}

	public int getNetoffon() {
		return netoffon;
	}

	public void setNetoffon(int netoffon) {
		this.netoffon = netoffon;
	}

	public int getMsgoffon() {
		return msgoffon;
	}

	public void setMsgoffon(int msgoffon) {
		this.msgoffon = msgoffon;
	}

	public int getNearTotal() {
		return nearTotal;
	}

	public void setNearTotal(int nearTotal) {
		this.nearTotal = nearTotal;
	}

	public String getGameIcon() {
		return gameIcon;
	}

	public void setGameIcon(String gameIcon) {
		this.gameIcon = gameIcon;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public long getSid() {
		return sid;
	}

	public void setSid(long sid) {
		this.sid = sid;
	}

}
