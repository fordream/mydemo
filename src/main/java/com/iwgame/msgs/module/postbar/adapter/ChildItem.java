/**      
 * ChildItem.java Create on 2014-4-17     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

/**
 * @ClassName: ChildItem
 * @Description: TODO(...)
 * @author chuanglong
 * @date 2014-4-17 下午6:49:46
 * @Version 1.0
 * 
 */
public class ChildItem {
	/**
	 * 名字
	 */
	public String itemName;
	/**
	 * 是否选中
	 */
	public boolean isSelect;
	/**
	 * 扩展对象，用于外部临时保存
	 */
	public Object ext;
	/** 标签id **/
	public int tagId;
	/** 精华 **/
	public int essence;
}
