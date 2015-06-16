/**      
* MessageRemoteServiceImpl.java Create on 2013-10-24     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.remote;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ChatMessageRequest;
import com.iwgame.msgs.proto.Msgs.ForwardInfo;
import com.iwgame.msgs.proto.Msgs.MessageContent;
import com.iwgame.msgs.proto.Msgs.OID;
import com.iwgame.msgs.proto.Msgs.SyncAckRequest;
import com.iwgame.msgs.proto.Msgs.SyncMessageRequest;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.xaction.proto.XAction.XActionResult;

/** 
 * @ClassName: MessageRemoteServiceImpl 
 * @Description: TODO(消息的service接口实现类) 
 * @author Administrator
 * @date 2013-10-24 下午5:07:22 
 * @Version 1.0
 * 
 */
public class MessageRemoteServiceImpl implements MessageRemoteService {
	
	protected static final String TAG = "MessageRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static MessageRemoteServiceImpl instance = null;

	private MessageRemoteServiceImpl() {
	}

	public static MessageRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new MessageRemoteServiceImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}


	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.MessageRemoteService#getUserSubscribeChannel(com.iwgame.msgs.xaction.XActionCallback)
	 */
	@Override
	public void getUserSubscribeChannel(ServiceCallBack<XActionResult> callback,Context context) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_MSG_GET_CHANNEL_LIST);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.MessageRemoteService#syncMessage(com.iwgame.msgs.xaction.XActionCallback, java.lang.String, int, long, boolean, boolean)
	 */
	@Override
	public void syncMessage(ServiceCallBack<XActionResult> callback,Context context,String mCode,
			boolean needMessageSummary) {
		// TODO Auto-generated method stub
		SyncMessageRequest.Builder obj = SyncMessageRequest.newBuilder();
		obj.setMcode(mCode);
		obj.setNeedMessageSummary(needMessageSummary);
		SyncMessageRequest request = obj.build();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);
		RemoteUtils.tcpRequest(callback, null, params, Msgs.syncRequest,request,  MsgsConstants.CMD_MSG_SYNC_MESSAGE);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.MessageRemoteService#sendSyncAck(com.iwgame.msgs.xaction.XActionCallback, int, long)
	 */
	@Override
	public void sendSyncAck(final ServiceCallBack<XActionResult> callback,Context context, String mCode, long syncKey) {
		// TODO Auto-generated method stub
		SyncAckRequest.Builder obj = SyncAckRequest.newBuilder();
		obj.setMcode(mCode);
	    obj.setSyncKey(syncKey);
	    SyncAckRequest request = obj.build();
	    Map<String, Object> params = new HashMap<String, Object>();
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);
	    RemoteUtils.tcpRequest(callback, context, params, Msgs.syncAckRequest, request, MsgsConstants.CMD_MSG_SYNC_MESSAGE_ACK);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.MessageRemoteService#sendChatMessage(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, com.iwgame.msgs.proto.Msgs.OID, com.iwgame.msgs.proto.Msgs.OID, com.iwgame.msgs.module.remote.MessageContent, java.lang.String, java.lang.String)
	 */
	@Override
	public void sendChatMessage(
			ServiceCallBack<XActionResult> callback, Context context,
			OID from, OID to,String channelType, MessageContent content, String summary,
			String position,ForwardInfo forwardInfo) {
		if(callback == null)
			return;		
		ChatMessageRequest.Builder obj = ChatMessageRequest.newBuilder();
		obj.setFrom(from);
		obj.setTo(to);
		obj.setChannelType(channelType);
		obj.setContent(content);
		
		if(summary != null)
		{
			obj.setSummary(summary);
		}
		obj.setPosition(position);
		
		if(forwardInfo != null){
		    obj.setForwardInfo(forwardInfo);
		}
		
		ChatMessageRequest request = obj.build();

		RemoteUtils.tcpRequest(callback, context, null, Msgs.chatRequest, request, MsgsConstants.CMD_MSG_POST_MESSAGE);
		
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.MessageRemoteService#getChatHistoryMessage(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, java.lang.String, long, long)
	 */
	@Override
	public void getChatHistoryMessage(ServiceCallBack<XActionResult> callback, Context context, long fromId,String fromDomain,long toId,String toDomain,String channelType,String category,long index,long limit,long minIndex) {
	    // TODO Auto-generated method stub
	    if(callback == null)
		return;		
	    Map<String, Object> params = new HashMap<String, Object>();
		params.put("channelType", channelType);
		params.put("fromId", fromId);
		params.put("fromDomain", fromDomain);
		params.put("toId", toId);
		params.put("toDomain", toDomain);
		params.put("category", category);
		params.put("index", index);
		params.put("limit", (int)limit);
		params.put("minIndex", minIndex);
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_MSG_HISTORY_MESSAGE);

	    
	}

}
