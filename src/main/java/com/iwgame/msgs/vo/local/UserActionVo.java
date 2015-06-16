/**      
* UserActionVo.java Create on 2014-1-28     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/** 
 * @ClassName: UserActionVo 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-1-28 上午11:00:49 
 * @Version 1.0
 * 
 */
public class UserActionVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2021247506696104796L;
	
	private int id;
	
	private int entitytype;
	
	private long entityid;
	
	private int opttype;
	
	private String content;
	
	private int count;
	
	private long creattime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEntitytype() {
		return entitytype;
	}

	public void setEntitytype(int entitytype) {
		this.entitytype = entitytype;
	}

	public long getEntityid() {
		return entityid;
	}

	public void setEntityid(long entityid) {
		this.entityid = entityid;
	}

	public int getOpttype() {
		return opttype;
	}

	public void setOpttype(int opttype) {
		this.opttype = opttype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getCreattime() {
		return creattime;
	}

	public void setCreattime(long creattime) {
		this.creattime = creattime;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
