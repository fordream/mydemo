package com.iwgame.msgs.vo.local;

import java.io.Serializable;

public class PageDataVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5138185245065217736L;
	
	/**
	 * 页面类型
	 */
	private int type;
	/**
	 * 图片更新时间
	 */
	private long updatetime;
	/**
	 * app类型
	 */
	private String apptype;
	/**
	 * 资源ID名称
	 */
	private String resourceid;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(long updatetime) {
		this.updatetime = updatetime;
	}
	public String getApptype() {
		return apptype;
	}
	public void setApptype(String apptype) {
		this.apptype = apptype;
	}
	public String getResourceid() {
		return resourceid;
	}
	public void setResourceid(String resourceid) {
		this.resourceid = resourceid;
	}
}
