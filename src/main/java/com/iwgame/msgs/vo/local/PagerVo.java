/**      
 * PagerVo.java Create on 2013-8-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.vo.local;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: PagerVo
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-8-28 下午03:15:52
 * @Version 1.0
 * 
 */
public class PagerVo<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5844320044088943199L;

	public static int LIMIT = 10;

	/****** 根据偏移量和获取个数获取分页数据 *******/
	// 偏移量
	private long offset = Long.MAX_VALUE;
	// 获取个数
	private int limit = LIMIT;

	private Boolean hanNext;

	private List<T> items;
	
	private int totalCount;
	
	public Boolean getHanNext() {
		return hanNext;
	}

	public void setHanNext(Boolean hanNext) {
		this.hanNext = hanNext;
	}

	/**
	 * @return the items
	 */
	public List<T> getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(List<T> items) {
		this.items = items;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
