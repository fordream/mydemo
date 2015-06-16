/**      
 * PlayRemoteServiceImpl.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PlayEvalRequest;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.PlayOrderAppeal;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: PlayRemoteServiceImpl
 * @Description: 陪玩数据服务接口实现
 * @author 王卫
 * @date 2015-5-8 下午3:19:07
 * @Version 1.0
 * 
 */
public class PlayRemoteServiceImpl implements PlayRemoteService {

	protected static final String TAG = "PlayRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static PlayRemoteServiceImpl instance = null;

	private PlayRemoteServiceImpl() {
	}

	public static PlayRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new PlayRemoteServiceImpl();
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
	 * com.iwgame.msgs.module.remote.PlayRemoteService#searchCreatPlays(com.
	 * iwgame.msgs.common.ServiceCallBack, android.content.Context, long, int)
	 */
	@Override
	public void searchCreatPlays(ServiceCallBack<XActionResult> callback, Context context, Long uid, Integer status, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if(uid != null){
			params.put("uid", uid);
		}
		if(status !=null){
			params.put("status", status);
		}
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_MY_PLAY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#searchDiscoverPlays(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, long, int)
	 */
	@Override
	public void searchDiscoverPlays(ServiceCallBack<XActionResult> callback, Context context, long gid, long sid, int sorttype, Long keyid, 
			String keyval, Integer sex, String resulttype, String position, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if(gid!=0){
			params.put("gid", gid);
		}
		if(sid!=0){
			params.put("sid", sid);
		}
		params.put("sorttype", sorttype);
		if(keyid!=null){
			params.put("keyid", keyid.longValue());
		}
		if(keyval!=null){
			params.put("keyval", keyval);
		}
		// 获取发现陪玩性别 index 0全部，1男，2女
		if (sex != 0) {// 全部不传参
			if (sex == 1) {
				params.put("sex", 0);// 男传参0
			}
			if (sex == 2) {
				params.put("sex", 1);// 女传参1
			}
		}
		params.put("resulttype", resulttype);
		params.put("position", position);
		params.put("offset", offset);
		params.put("limit", limit);
		LogUtil.info("lll searchDiscoverPlays gid = " + gid + ", sid = " + sid + ", sorttype = " + sorttype + ", keyid = " + keyid + ", keyval = " + keyval + ", sex = " + params.get("sex")
				+ ", position = " + position);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_PLAY);
	}

	/**
	 * 创建、编辑陪玩
	 */
	@Override
	public void addPlayData(ServiceCallBack<XActionResult> callback, Context context, Long playid, byte[] image, PlayInfo info) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (playid > 0) {
			params.put("playid", playid);
		}
		if (image != null) {
			params.put("img", image);
		}
		RemoteUtils.tcpRequest(callback, context, params, Msgs.playInfoRequest, info, MsgsConstants.CMD_PLAY_ADD);
	}

	/**
	 * 查询陪玩详情
	 */
	@Override
	public void searchPlayInfo(ServiceCallBack<XActionResult> callback, Context context, Long playid, String resulttype) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("playid", playid);
		params.put("resulttype", resulttype);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_PLAY_DETAIL);
	}

	@Override
	public void getGameStarLeve(ServiceCallBack<XActionResult> callback, Context context, Long gid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", gid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_PLAY_STAR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#changePlayStatus(com.
	 * iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.Long, int)
	 */
	@Override
	public void changePlayStatus(ServiceCallBack<XActionResult> callback, Context context, Long pid, int type) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("playid", pid);
		params.put("type", type);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_PLAY_STATUS_MANAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#playEnrollOrder(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long, long, long,
	 * long, long, java.lang.Integer)
	 */
	@Override
	public void playEnrollOrder(ServiceCallBack<XActionResult> callback, Context context, long playid, long roleid, long gid, long sid,
			long starttime, Integer duration) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("playid", playid);
		params.put("roleid", roleid);
		params.put("gid", gid);
		params.put("sid", sid);
		params.put("starttime", starttime);
		params.put("duration", duration);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_PLAY_ORDER_ADD);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#searchApplyPlays(com.
	 * iwgame.msgs.common.ServiceCallBack, android.content.Context, long, int)
	 */
	@Override
	public void searchApplyPlays(ServiceCallBack<XActionResult> callback, Context context, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_APPLY_PLAY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#searchApplyOrder(com.
	 * iwgame.msgs.common.ServiceCallBack, android.content.Context, int, long,
	 * int)
	 */
	@Override
	public void searchApplyOrder(ServiceCallBack<XActionResult> callback, Context context, long pid, String status, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("playid", pid);
		params.put("status", status);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_PLAY_ORDER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#getOrderDetail(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void getOrderDetail(ServiceCallBack<XActionResult> callback, Context context, long oid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderid", oid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_ORDER_DETAIL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#acceptOrder(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void acceptOrder(ServiceCallBack<XActionResult> callback, Context context, long oid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderid", oid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_PLAY_ORDER_ACCEPT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#payOrder(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void payOrder(ServiceCallBack<XActionResult> callback, Context context, long oid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderid", oid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_PLAY_ORDER_PAY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#payOrder(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context,
	 * com.iwgame.msgs.proto.Msgs.PlayOrderAppeal)
	 */
	@Override
	public void appealOrder(ServiceCallBack<XActionResult> callback, Context context, PlayOrderAppeal playOrderAppeal) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		RemoteUtils.tcpRequest(callback, context, params, Msgs.playOrderAppealRequest, playOrderAppeal, MsgsConstants.CMD_PLAY_ORDER_APPEAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#cancelOrder(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void cancelOrder(ServiceCallBack<XActionResult> callback, Context context, long oid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderid", oid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_PLAY_ORDER_CANCEL);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.PlayRemoteService#getPlayComments(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long, long, int)
	 */
	@Override
	public void getPlayComments(ServiceCallBack<XActionResult> callback,
			Context context, long pid, long offset, int limit) {
		if(callback==null)
			return;
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("playid", pid);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_PLAY_EVAL);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.PlayRemoteService#sendPlayRely(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long, java.lang.String)
	 */
	@Override
	public void sendPlayRely(ServiceCallBack<XActionResult> callback,
			Context context, long id, String content) {
		if(callback==null)
			return;
		Map<String ,Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("content", content);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_PLAY_ORDER_REPLY);
	}
	@Override
	public void playEvalOrder(ServiceCallBack<XActionResult> callback, Context context, PlayEvalRequest playeval) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
	
		RemoteUtils.tcpRequest(callback, context, params,Msgs.playEvalRequest,playeval, MsgsConstants.CMD_PLAY_ORDER_EVAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.PlayRemoteService#searchAppealInfo(com.
	 * iwgame.msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void searchAppealInfo(ServiceCallBack<XActionResult> callback, Context context, long oid) {
		// TODO Auto-generated method stub
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orderid", oid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_ORDER_APPEAL);
	}

}
