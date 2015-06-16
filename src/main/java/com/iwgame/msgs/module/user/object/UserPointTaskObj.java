/**      
* UserPointTask.java Create on 2014-8-20     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.user.object;

import com.iwgame.msgs.vo.local.PointTaskVo;

/** 
 * @ClassName: UserPointTask 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-8-20 上午11:19:14 
 * @Version 1.0
 * 
 */
public class UserPointTaskObj {
	
	public static final int STATUS_UNCOMPLETE = 1;
	public static final int STATUS_UNFETCH = 2;
	public static final int STATUS_FETCHED = 3;
	
	private PointTaskVo pointTask;
	
	private int taskid;//任务id
	
	private int times;//完成动作次数
	
	private int status;//任务状态,1:未完成,2:完成未领取,3:已领取

	public PointTaskVo getPointTask() {
		return pointTask;
	}

	public void setPointTask(PointTaskVo pointTask) {
		this.pointTask = pointTask;
	}

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
