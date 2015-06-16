/**      
* GroupRelVo.java Create on 2013-10-23     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/** 
 * @ClassName: GroupRelVo 
 * @Description: 公会成员表
 * @author 王卫
 * @date 2013-10-23 下午05:49:46 
 * @Version 1.0
 * 
 */
public class GroupUserRelVo implements Serializable {

	public long getMsgCreatTime() {
		return msgCreatTime;
	}
	public void setMsgCreatTime(long msgCreatTime) {
		this.msgCreatTime = msgCreatTime;
	}
	public int getTop() {
		return top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	public MessageVo getMessageVo() {
		return messageVo;
	}
	public void setMessageVo(MessageVo messageVo) {
		this.messageVo = messageVo;
		this.msgCreatTime = messageVo.getCreateTime();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8599529138330709710L;
	
	/**
	 * 退出公会
	 */
	public static int REL_OUT = 0 ;
	/**
	 * 普通成员
	 */
	public static int REL_USER = 1 ;
	/**
	 * 管理员
	 */
	public static int REL_NORMALADMIN = 2 ; 
	
	/**
	 * 会长
	 */
	public static int  REL_ADMIN = 3;
	//本地ID
	private long id;
	//用户ID
	private long uid;
	//公会组ID
	private long grid;
	//关系
	private int rel;
	//贡献积分
	private long cpoint;
	//活跃时间
	private long atime;
	//用户与公会之间的关系
	private int status;
	//用户信息或者用户与公会关系修改的时间
	private long utime;
	//用户的详细信息
	private UserVo vo;
	
	private int top;
	
	//公会名片
	private String remark;
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	//消息时间
    private long msgCreatTime;
	
 // 用户在公会中的最后一次发言
 	private MessageVo messageVo;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getUtime() {
		return utime;
	}
	public void setUtime(long utime) {
		this.utime = utime;
	}
	public UserVo getVo() {
		return vo;
	}
	public void setVo(UserVo vo) {
		this.vo = vo;
	}
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
	 * @return the grid
	 */
	public long getGrid() {
		return grid;
	}
	/**
	 * @param grid the grid to set
	 */
	public void setGrid(long grid) {
		this.grid = grid;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the rel
	 */
	public int getRel() {
		return rel;
	}
	/**
	 * @param rel the rel to set
	 */
	public void setRel(int rel) {
		this.rel = rel;
	}
	public long getCpoint() {
		return cpoint;
	}
	public void setCpoint(long cpoint) {
		this.cpoint = cpoint;
	}
	public long getAtime() {
		return atime;
	}
	public void setAtime(long atime) {
		this.atime = atime;
	}

}
