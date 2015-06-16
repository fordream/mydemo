/**      
* MessageDao.java Create on 2013-11-12     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.vo.local.MessageVo;

/** 
 * @ClassName: MessageDao 
 * @Description: 消息dao的接口 
 * @author chuanglong
 * @date 2013-11-12 下午7:35:14 
 * @Version 1.0
 * 
 */
public interface MessageDao {
	/**
	 * 插入一条消息
	 * @param vo
	 * @return
	 */
	public MessageVo insert(MessageVo vo);
	/**
	 * 通过id 更新消息，用于发送后更新,更新消息的状态（删除消息）
	 * @param id
	 * @param msgid
	 * @param createTime
	 * @param contentType
	 * @param content  为null或空值时将不更新
	 * @param status  为发送成功或发送失败
	 * @return
	 */
	public int updateById(int id ,long msgid ,long msgIndex,long createTime,int contentType,String content,int status);
	
	/**
	 * 通过id更新消息的内容，用于文本中快捷操作后的更新
	 * @param id
	 * @param contentType
	 * @param content
	 * @return
	 */
	public int updateMessageContentById(int id , int contentType,String content);

	
	/**
	 * 通过msgid获得消息
	 * @param msgid
	 * @return
	 */
	public MessageVo getMessageByMsgId(long msgid);
	
	/**
	 * 通过channelType，subjectid，subjectDomain，category删除记录
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public int delMessage(String channelType,long subjectid,String subjectDomain,String category);
	
	/**
	 * 按照msgindex，通过channelType，subjectid，subjectDomain，category获得记录 （按照msgindex从大到小的排序)
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @param msgindex
	 * @param limit  ,limit目前只有负数
	 * @return
	 */
	public List<MessageVo> getMessage(String channelType,long subjectid,String subjectDomain,String category,long msgindex,int limit,long minIndex);

	/**
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @param msgindex
	 * @param limit
	 * @param minIndex
	 * @param estimatetype
	 * @param estimateop
	 * @return
	 */
	public List<MessageVo> getMessage(String channelType,long subjectid,String subjectDomain,String category,long msgindex,int limit,long minIndex, int estimateop);
	
	/**
	 *   按照msgindex倒序排列，通过channelType，subjectid，subjectDomain，category做为条件，fromid和fromsubject分组统计获得最后一条消息 （按照msgindex从大到小的排序)（主要用于公会最后一次的发言）
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	 public List<MessageVo> getFromLastMessage(String channelType,long subjectid,String subjectDomain,String category);
	 
	/**
	 * 按照消息的时间排序，通过channelType，subjectid，subjectDomain，category统计最后一条消息
	 */
	public List<MessageVo> getSubjectLastMessage();
	
	
	public MessageVo getLastMessage(String channelType,long subjectid,String subjectDomain,String category);
	
	/**
	 * 根据类别和类型获取未删除的消息
	 * @param channelType
	 * @param category
	 * @return
	 */
	public List<MessageVo> getMessageByChannelTypeAndCategory(String channelType, String category);
	
	/**
	 * 根据类别和类型获设置一类消息为删除状态
	 * @param channelType
	 * @param category
	 * @return
	 */
	public int delByChannelTypeAndCategory(String channelType, String category, String content);
	/**
	 * 批量插入多条消息
	 * @param vo
	 * @return
	 */
	public int insertMessages(List<MessageVo> messages);

	/**
	 * 根据消息通道信息删除消息
	 * @param channelType
	 * @param category
	 * @param subjectid
	 * @param subjectDomain
	 * @return
	 */
	public int delMessageByPolicy(String channelType, String category, long subjectid, String subjectDomain);
}
