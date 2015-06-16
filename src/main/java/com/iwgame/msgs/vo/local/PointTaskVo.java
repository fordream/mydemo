/**      
* PointTaskVo.java Create on 2014-8-19     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

import com.iwgame.msgs.proto.Msgs.PostContent;

/** 
 * @ClassName: PointTaskVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-8-19 下午8:54:04 
 * @Version 1.0
 * 
 */
public class PointTaskVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7710433948682689700L;
	
	private long id;
	
	//任务id
	private int taskid;
	//任务名称
	private String taskname;
	//任务描述
	private String taskdesc;
	//积分值
	private int point;
	//任务类型 ,1:新手任务;2,每日任务
	private int type;
	//动作次
	private int times;
	//数据状态 ,默认0 ，1:删除
	private int status = -1;
	//点击未完成跳转的页面的id
	private int toid;
	
	//任务详情
	private String detail;
	
	//经验值
	private int exp;
	//经验值奖励次数
	private int exptimes;
	//任务和多图
	private String detailsBytes;
	//任务所属游戏id集合
	private String gids;
	
	private PostContent postContent;
	
	public PostContent getPostContent() {
		return postContent;
	}
	public void setPostContent(PostContent postContent) {
		this.postContent = postContent;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public int getToid() {
		return toid;
	}
	public void setToid(int toid) {
		this.toid = toid;
	}
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}
	public String getTaskdesc() {
		return taskdesc;
	}
	public void setTaskdesc(String taskdesc) {
		this.taskdesc = taskdesc;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the exp
	 */
	public int getExp() {
		return exp;
	}
	/**
	 * @param exp the exp to set
	 */
	public void setExp(int exp) {
		this.exp = exp;
	}
	/**
	 * @return the exptimes
	 */
	public int getExptimes() {
		return exptimes;
	}
	/**
	 * @param exptimes the exptimes to set
	 */
	public void setExptimes(int exptimes) {
		this.exptimes = exptimes;
	}
	/**
	 * @return the detailsBytes
	 */
	public String getDetailsBytes() {
		return detailsBytes;
	}
	/**
	 * @param detailsBytes the detailsBytes to set
	 */
	public void setDetailsBytes(String detailsBytes) {
		this.detailsBytes = detailsBytes;
	}
	/**
	 * @return the gids
	 */
	public String getGids() {
		return gids;
	}
	/**
	 * @param gids the gids to set
	 */
	public void setGids(String gids) {
		this.gids = gids;
	}
	
}
