/**      
 * MessageRemoteService.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs.ForwardInfo;
import com.iwgame.msgs.proto.Msgs.MessageContent;
import com.iwgame.msgs.proto.Msgs.OID;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: MessageRemoteService
 * @Description: TODO(消息的service接口)
 * @author Administrator
 * @date 2013-10-24 下午5:06:09
 * @Version 1.0
 * 
 */
public interface MessageRemoteService {

	/**
	 * 获得通道列表
	 * 
	 * @param callback
	 */
	public void getUserSubscribeChannel(ServiceCallBack<XActionResult> callback, Context context);

	/**
	 * 同步消息
	 * 
	 * @param callback
	 * @param channelId
	 * @param needMessageSummary
	 */
	public void syncMessage(ServiceCallBack<XActionResult> callback, Context context, String mCode, boolean needMessageSummary);

	/**
	 * 发送ack响应
	 * 
	 * @param callback
	 * @param context
	 * @param channelId
	 * @param syncKey
	 */
	public void sendSyncAck(ServiceCallBack<XActionResult> callback, Context context, String mCode, long syncKey);

	/**
	 * 发送聊天消息
	 * 
	 * @param callback
	 * @param context
	 * @param from
	 * @param to
	 * @param content
	 * @param summary
	 * @param position
	 * @param forwardInfo
	 */
	public void sendChatMessage(ServiceCallBack<XActionResult> callback, Context context, OID from, OID to, String channelType,
			MessageContent content, String summary, String position,ForwardInfo forwardInfo);
	
	/**
	 * 获得历史消息
	 * @param callback
	 * @param context
	 * @param mCode
	 * @param startIndex
	 * @param endIndex
	 */
	public void getChatHistoryMessage(ServiceCallBack<XActionResult> callback, Context context,long fromId,String fromDomain,long toId,String toDomain,String channelType,String category,long index,long limit,long minIndex);

}
