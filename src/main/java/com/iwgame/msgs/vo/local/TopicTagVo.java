/**      
 * TopicTagVo.java Create on 2014-4-16     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.vo.local;

import java.io.Serializable;

/**
 * @ClassName: TopicTagVo
 * @Description: 帖子的标签
 * @author chuanglong
 * @date 2014-4-16 上午9:57:49
 * @Version 1.0
 * 
 */
public class TopicTagVo implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = -3177135764079604486L;
	/**
	 * id
	 */
	private int id;
	/**
	 * name
	 */
	private String name;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 定义tag用的范围
	 */
	private int access;

	/**
	 * @return the access
	 */
	public int getAccess() {
		return access;
	}

	/**
	 * @param access
	 *            the access to set
	 */
	public void setAccess(int access) {
		this.access = access;
	}

	/**
	 * 标签权重
	 */
	private int sort;

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	/**
	 * 是否默认标签
	 */
	private int tagDefault;

	public int getTagDefault() {
		return tagDefault;
	}

	public void setTagDefault(int tagDefault) {
		this.tagDefault = tagDefault;
	}

	/** 此标签帖子数 **/
	private int topicNums;

	public int getTopicNums() {
		return topicNums;
	}

	public void setTopicNums(int topicNums) {
		this.topicNums = topicNums;
	}

	// 最后更新时间
	private long utime;

	public long getUtime() {
		return utime;
	}

	public void setUtime(long utime) {
		this.utime = utime;
	}

}
