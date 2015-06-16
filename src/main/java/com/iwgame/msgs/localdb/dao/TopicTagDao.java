/**      
 * TopicTagDao.java Create on 2015-2-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.vo.local.TopicTagVo;

/**
 * @ClassName: TopicTagDao
 * @Description: 贴吧标签dao
 * @author 王卫
 * @date 2015-2-15 上午10:57:47
 * @Version 1.0
 * 
 */
public interface TopicTagDao {

	/**
	 * 根据标签名查找标签
	 * 
	 * @param tagName
	 * @return
	 */
	public TopicTagVo getTopicTagByTagName(String tagName);

	/**
	 * 
	 * @param topicTag
	 * @return
	 */
	public int insertTopicTag(TopicTagVo topicTag);
	
	/**
	 * 
	 * @param topicTag
	 * @return
	 */
	public int updateTopicTag(TopicTagVo topicTag);

	/**
	 * 插入或更新发帖标签
	 * 
	 * @param topicTag
	 * @return
	 */
	public int insertOrUpdate(TopicTagVo topicTag);

	/**
	 * 获取发帖标签
	 * 
	 * @param limit
	 * @return
	 */
	public List<TopicTagVo> getTopicTags(int limit);

}
