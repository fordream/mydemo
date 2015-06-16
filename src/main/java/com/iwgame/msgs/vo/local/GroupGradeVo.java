/**      
* GroupGrade.java Create on 2014-8-19     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/** 
 * @ClassName: GroupGrade 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-8-19 下午9:13:11 
 * @Version 1.0
 * 
 */
public class GroupGradeVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5287668564709979366L;

	private long id;
	
	private int grade;//等级
	
	private int point;//所需积分
	
	private int members;//成员数
	
	private int userlimit;//成员贡献限额
	
	private int grouplimit;//公会获取限额
	
	private int status = -1;//数据状态 ,默认0 ，1:删除

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

	public int getMembers() {
		return members;
	}

	public void setMembers(int members) {
		this.members = members;
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

	public int getUserlimit() {
		return userlimit;
	}

	public void setUserlimit(int userlimit) {
		this.userlimit = userlimit;
	}

	public int getGrouplimit() {
		return grouplimit;
	}

	public void setGrouplimit(int grouplimit) {
		this.grouplimit = grouplimit;
	}
	
}
