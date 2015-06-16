/**      
* UserPointDetailObj.java Create on 2014-8-20     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.user.object;

/** 
 * @ClassName: UserPointDetailObj 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-8-20 上午11:50:05 
 * @Version 1.0
 * 
 */
public class UserPointDetailObj {

	 private long optime;//任务时间
	 
	 private String aname;//动作名称
	 
	 private int point;//积分
	 
	 private int totalpoint;//总积分

	public long getOptime() {
		return optime;
	}

	public void setOptime(long optime) {
		this.optime = optime;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getTotalpoint() {
		return totalpoint;
	}

	public void setTotalpoint(int totalpoint) {
		this.totalpoint = totalpoint;
	}
	
}
