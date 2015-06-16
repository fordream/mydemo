/**      
 * MessageProxy.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.message.logic;

import java.util.List;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.proto.Msgs.SyncMessageNotification;
import com.iwgame.msgs.vo.local.MessageVo;

/**
 * @ClassName: MessageProxy
 * @Description: TODO(消息处理类)
 * @author 吴禹青
 * @date 2013-9-24 下午5:58:39
 * @Version 1.0
 * 
 */
public interface MessageProxy {

	/**
	 * 发出获得用户通道的请求
	 * 
	 * @param context
	 */
	public void getUserSubscribeChannel(Context context);

	/**
	 * 处理消息通知的结果
	 * 
	 * @param notification
	 */
	public void handleSyncMessageNotification(Context context, SyncMessageNotification notification);

	/**
	 * 发送消息
	 * 
	 * @param callback
	 * @param context
	 * @param vo
	 * @return
	 */
	public void sendMessage(ProxyCallBack<MessageVo> callback, Context context, MessageVo vo);
	
	/**
	 * 发送消息，添加是否设置apptype
	 * 
	 * @param callback
	 * @param context
	 * @param vo
	 * @param setApptype
	 * @return
	 */
	public void sendMessage(ProxyCallBack<MessageVo> callback, Context context, MessageVo vo, boolean setApptype);

	/**
	 * 发送的消息先保存到本地
	 * 
	 * @param callback
	 * @param context
	 * @param vo
	 */
	public MessageVo sendMessageSaveToLocal(Context context, MessageVo vo);

	// /**
	// * 通过channelType，subjectid，subjectDomain，category更新已读状态
	// * @param channelType
	// * @param subjectid
	// * @param subjectDomain
	// * @param category
	// * @param readStatus
	// * @return
	// */
	// public int updateReadStatus(String channelType,long subjectid,String
	// subjectDomain,String category,int readStatus);

	/**
	 * 通过channelType，subjectid，subjectDomain，category删除记录
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public int delMessage(String channelType, long subjectid, String subjectDomain, String category);

	/**
	 * 通过id删除一条记录
	 * 
	 * @param id
	 * @return
	 */
	public int delMessageById(int id);

	/**
	 * 通过id更新消息的内容
	 * 
	 * @param id
	 * @param contentType
	 * @param content
	 * @return
	 */
	public int updateMessageContentById(int id, int contentType, String content);

	/**
	 * 按照msgindex，通过channelType，subjectid，subjectDomain，category获得记录
	 * （按照msgindex从大到小的排序)
	 * 
	 * @param callback
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @param msgindex
	 * @param limit
	 * @param isGetDataFromServer
	 * @return
	 */
	public void getMessage(ProxyCallBack<List<MessageVo>> callback, String channelType, long subjectid, String subjectDomain, String category,
			long msgindex, int limit, boolean isGetDataFromServer, long minIndex);
	
	/**
	 * 
	 * @param callback
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @param msgindex
	 * @param limit
	 * @param isGetDataFromServer
	 * @param minIndex
	 * @param estimateop
	 */
	public void getMessage(ProxyCallBack<List<MessageVo>> callback, String channelType, long subjectid, String subjectDomain, String category,
			long msgindex, int limit, boolean isGetDataFromServer, long minIndex, int estimateop);

	/**
	 * 按照msgindex，通过channelType，subjectid，subjectDomain，category获得记录
	 * （按照msgindex从大到小的排序)
	 * 
	 * @return
	 */
	public List<MessageVo> getSubjectLastMessage();

	/**
	 * 按照msgindex倒序排列，通过channelType，subjectid，subjectDomain，category做为条件，
	 * fromid和fromsubject分组统计获得最后一条消息 （按照msgindex从大到小的排序)（主要用于公会最后一次的发言）
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public List<MessageVo> getFromLastMessage(String channelType, long subjectid, String subjectDomain, String category);

	/**
	 * 公会会长群发消息
	 * 
	 * @param callback
	 * @param context
	 * @param groupid
	 * @param content
	 * @param resource
	 * @param position
	 */
	public void sendGroupMassMessage(ProxyCallBack<Integer> callback, Context context, long groupid, String content, byte[] resource,
			int resourceType, String position);

	/**
	 * 通过channelType，subjectid，subjectDomain，category 获得最大的msgindex
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	public long getMessageMaxIndex(String channelType, long subjectid, String subjectDomain, String category);
	
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
	 * @param content
	 * @return
	 */
	public int delByChannelTypeAndCategory(String channelType, String category, String content);
	
	/**
	 * 删除本地历史消息
	 * @param channelType
	 * @param category
	 * @param subjectid
	 * @param subjectDomain
	 */
	public void delLocalHistoryMessage(String channelType, long subjectid, String subjectDomain, String category);

}
