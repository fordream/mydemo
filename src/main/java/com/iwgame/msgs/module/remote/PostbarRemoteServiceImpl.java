/**      
 * PostbarRemoteServiceImpl.java Create on 2013-12-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentList;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: PostbarRemoteServiceImpl
 * @Description: TODO(贴吧remote service的实现类)
 * @author chuanglong
 * @date 2013-12-27 上午9:40:33
 * @Version 1.0
 * 
 */
public class PostbarRemoteServiceImpl implements PostbarRemoteService {

	protected static final String TAG = "PostbarRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static PostbarRemoteServiceImpl instance = null;

	private PostbarRemoteServiceImpl() {
	}

	public static PostbarRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new PostbarRemoteServiceImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getTopicList(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long,
	 * java.lang.String, int, long, long, int)
	 */
	@Override
	public void getTopicList(ServiceCallBack<XActionResult> callback, Context context, long gid, String title, int order, int tagid, int filter,
			long uid, long offset, int limit, String tagName) {
		// TODO Auto-generated method stub

		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", gid);
		if (title != null)
			params.put("keyword", title);
		params.put("order", order);
		params.put("tagId", (long) tagid);
		params.put("filter", filter);
		params.put("uid", uid);
		params.put("offset", offset);
		params.put("limit", limit);
		if(tagName != null)
			params.put("tagName", tagName); 
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_SEARCH_TOPIC_LIST);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getFavoriteTopicList
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long,
	 * long, int)
	 */
	@Override
	public void getFavoriteTopicList(ServiceCallBack<XActionResult> callback, Context context, long uid, long offset, int limit) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", uid);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_FAVORITE_TOPIC_LIST);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getTopicDetail(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void getTopicDetail(ServiceCallBack<XActionResult> callback, Context context, long topicId) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tid", topicId);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_TOPIC_DETAIL);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getTopicReplyList(
	 * com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long,
	 * int, long, int)
	 */
	@Override
	public void getTopicReplyList(ServiceCallBack<XActionResult> callback, Context context, long tid, int ttype, int filter, int offsettype,
			long offset, int limit) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tid", tid);
		params.put("ttype", ttype);
		params.put("filter", filter);
		params.put("offset", offset);
		params.put("limit", limit);
		params.put("offsetType", offsettype);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_TOPIC_REPLY_LIST_EXT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getTopicReplyDetail
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void getTopicReplyDetail(ServiceCallBack<XActionResult> callback, Context context, long replyid) {
		// TODO Auto-generated method stub

		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rid", replyid);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_TOPIC_REPLY_DETAIL);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#applyPostbarMaster
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long,
	 * java.lang.String, java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void applyPostbarMaster(ServiceCallBack<XActionResult> callback, Context context, long gid, String name, String idcardNo,
			String applyContent, byte[] idcardImage) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", gid);
		if (name != null)
			params.put("realName", name);
		if (idcardNo != null)
			params.put("idcardNo", idcardNo);
		if (idcardImage != null)
			params.put("idcardImage", idcardImage);
		if (applyContent != null)
			params.put("applyContent", applyContent);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_POSTBAR_MASTER_APPLY);
	}

	@Override
	public void getMasterApplyCount(ServiceCallBack<XActionResult> callback, Context context, long gid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", gid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_MASTER_ISAPPLY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getPostbarMaster(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void getPostbarMaster(ServiceCallBack<XActionResult> callback, Context context, long gid) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", gid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_POSTBAR_MASTER_LIST);

	}

	// /* (non-Javadoc)
	// * @see
	// com.iwgame.msgs.module.remote.PostbarRemoteService#getPostbarTopic(com.iwgame.msgs.common.ServiceCallBack,
	// android.content.Context, long)
	// */
	// @Override
	// public void getGamePostbarCount(ServiceCallBack<XActionResult> callback,
	// Context context, long gid) {
	// if (callback == null)
	// return;
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("gid", gid);
	// RemoteUtils.tcpRequest(callback, context, params,
	// MsgsConstants.CMD_POSTBAR_GET_ADDED_NUMBER);
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getTopicTags(com.iwgame
	 * .msgs.common.ServiceCallBack, long)
	 */
	@Override
	public void getTopicTags(ServiceCallBack<XActionResult> callback, long gid) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", gid);
		long updatetime = 0;
		params.put("updatetime", updatetime);
		RemoteUtils.tcpRequest(callback, null, params, MsgsConstants.CMD_POSTBAR_GET_TAGS);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getLimitedOPCount(
	 * com.iwgame.msgs.common.ServiceCallBack, android.content.Context, int)
	 */
	@Override
	public void getLimitedOPCount(ServiceCallBack<XActionResult> callback, Context context, int limitedop) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("limitedop", limitedop);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_LIMITED_OP_COUNT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#userAction(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long, int, int,
	 * java.lang.String, long, byte[], int, java.lang.String)
	 */
	@Override
	public void userAction(ServiceCallBack<XActionResult> callback, Context context, long tid, int ttype, int op, String content, long seq,
			byte[] resource, int resourceType, String pos) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("tid", tid);

		params.put("ttype", ttype);

		params.put("op", op);

		if (content != null)
			params.put("content", content);
		if (seq != 0)
			params.put("seq", seq);

		if (resource != null) {
			params.put("resource", resource);
			params.put("resourceType", resourceType);
		}

		if (pos != null)
			params.put("position", pos);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_ACTION);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#getGamePostbarMaxIndex
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void getGamePostbarMaxIndex(ServiceCallBack<XActionResult> callback, Context context, String gids) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		// params.put("gid", gid);

		params.put("gids", gids);
		params.put("model", 1);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTABAR_MAX_INDEX);
	}

	@Override
	public void getGameFollowCount(ServiceCallBack<XActionResult> callback, Context context, String gids) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("gids", gids);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_GAME_FOLLOW);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#likeOrUnLikeTopic(
	 * com.iwgame.msgs.common.ServiceCallBack, java.lang.Long, java.lang.Long,
	 * java.lang.Long, java.lang.Long, int, int)
	 */
	@Override
	public void likeOrUnLikeTopic(ServiceCallBack<XActionResult> callback, Context context, Long tid, Long topicId, Long topicUid, Long trUid,
			int type, int op) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (tid != null)
			params.put("id", tid);
		if (topicId != null)
			params.put("topicId", topicId);
		if (topicUid != null)
			params.put("topicUid", topicUid);
		if (trUid != null)
			params.put("trUid", trUid);
		params.put("type", type);
		params.put("op", op);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_ESTIMATE_ACTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PostbarRemoteService#publicTopic(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.Long,
	 * java.lang.Long, int, int, int, com.iwgame.msgs.proto.Msgs.ContentList,
	 * java.lang.String)
	 */
	@Override
	public void publicTopic(ServiceCallBack<XActionResult> callback, Context context, Long tid, Long gid, int op, int actiontype, int topictype,
			ContentList contentList, String position, int isSaveAlbum) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		if (tid != null)
			params.put("tid", tid);
		if (gid != null)
			params.put("gid", gid);
		params.put("op", op);
		params.put("actiontype", actiontype);
		params.put("topictype", topictype);
		params.put("isSaveAlbum", isSaveAlbum);
		if (position != null)
			params.put("position", position);

		RemoteUtils.tcpRequest(callback, context, params, Msgs.contentList, contentList, MsgsConstants.CMD_POSTBAR_ACTION_EXT);
	}

}
