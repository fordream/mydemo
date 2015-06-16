/**      
 * CityVo.java Create on 2013-9-16     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.vo.local;

import java.io.Serializable;


/**
 * @ClassName: CityVo
 * @Description: 所在地
 * @author 王卫
 * @date 2013-9-16 下午05:28:25
 * @Version 1.0
 * 
 */
public class AreaVo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2804998226531972143L;

	private int id;

	private String areaname;
	
	private int parentid;
	
	private int type;

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
	 * @return the areaname
	 */
	public String getAreaname() {
		return areaname;
	}

	/**
	 * @param areaname the areaname to set
	 */
	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	/**
	 * @return the parentid
	 */
	public int getParentid() {
		return parentid;
	}

	/**
	 * @param parentid the parentid to set
	 */
	public void setParentid(int parentid) {
		this.parentid = parentid;
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
