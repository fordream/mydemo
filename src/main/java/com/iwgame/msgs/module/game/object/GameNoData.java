/**      
* GameExtDataVo.java Create on 2014-4-23     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.game.object;

import java.util.List;

import com.iwgame.msgs.vo.local.TopicTagVo;

/** 
 * @ClassName: GameExtDataVo 
 * @Description: TODO(贴吧的扩展数据) 
 * @author chuanglong
 * @date 2014-4-23 上午10:03:48 
 * @Version 1.0
 * 
 */
public class GameNoData {
	// 导致无数据类型 0 网络，1没有
	private int nodateType;
	// 当前帖子类型
	private int tagid;
	// 筛选条件，区别全部和精华帖
	private int filter;
	public int getNodateType() {
		return nodateType;
	}
	public void setNodateType(int nodateType) {
		this.nodateType = nodateType;
	}
	public int getTagid() {
		return tagid;
	}
	public void setTagid(int tagid) {
		this.tagid = tagid;
	}
	public int getFilter() {
		return filter;
	}
	public void setFilter(int filter) {
		this.filter = filter;
	}
}
