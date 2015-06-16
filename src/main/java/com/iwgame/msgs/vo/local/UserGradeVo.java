/**      
* UserGradeVo.java Create on 2014-8-19     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/** 
 * @ClassName: UserGradeVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-8-19 下午9:01:46 
 * @Version 1.0
 * 
 */
public class UserGradeVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4157310373184855667L;
	
	private long id;
	
	private int grade;//等级
	
	private int point;//所需积分
	
	private int joingroup;//可加入公会数量
	
	private int creategroup;//可创建公会数量
	
	private int followgame;//可关注游戏数
	
	private int sendpost;//可发帖数
	
	private int datelimit;//可发帖数
	
	private int status = -1;//数据状态 ,默认0 ，1:删除

	private String options;//升级条件
	
    private String multiple;//积分倍数
	
	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getMultiple() {
		return multiple;
	}

	public void setMultiple(String multiple) {
		this.multiple = multiple;
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

	public int getJoingroup() {
		return joingroup;
	}

	public void setJoingroup(int joingroup) {
		this.joingroup = joingroup;
	}

	public int getCreategroup() {
		return creategroup;
	}

	public void setCreategroup(int creategroup) {
		this.creategroup = creategroup;
	}

	public int getFollowgame() {
		return followgame;
	}

	public void setFollowgame(int followgame) {
		this.followgame = followgame;
	}

	public int getSendpost() {
		return sendpost;
	}

	public void setSendpost(int sendpost) {
		this.sendpost = sendpost;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDatelimit() {
		return datelimit;
	}

	public void setDatelimit(int datelimit) {
		this.datelimit = datelimit;
	}

}
