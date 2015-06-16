/**      
 * AlbumVo.java Create on 2013-9-29     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/**
 * @ClassName: ResourceVo
 * @Description: 用户资源（图片）对象
 * @author 王卫
 * @date 2013-9-29 下午04:11:40
 * @Version 1.0
 * 
 */
public class ResourceVo implements Serializable {

	public static final int RES_TYPE_IMAGE = 0;
	/**
	 * 
	 */
	private static final long serialVersionUID = -6221179463440601987L;
	// ID
	private int id;
	// 用户ID
	private long userid;
	// 资源ID
	private String resourceId;
	//创建时间
	private long creattime;
	// 类型[0:图片...]
	private int type;
	
	public ResourceVo(){
		
	}
	
	/**
	 * 
	 */
	public ResourceVo(long userid, String resourceId, long creattime, int type) {
		this.userid = userid;
		this.resourceId = resourceId;
		this.creattime = creattime;
		this.type = type;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the uid
	 */
	public long getUserid() {
		return userid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUserid(long userid) {
		this.userid = userid;
	}

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the creattime
	 */
	public long getCreattime() {
		return creattime;
	}

	/**
	 * @param creattime the creattime to set
	 */
	public void setCreattime(long creattime) {
		this.creattime = creattime;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

}
