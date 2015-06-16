/**      
 * SearchRemoteServiceImpl.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: SearchRemoteServiceImpl
 * @Description: 查询服务实现类
 * @author 王卫
 * @date 2013-9-24 下午07:12:40
 * @Version 1.0
 * 
 */
public class SearchRemoteServiceImpl implements SearchRemoteService {

	protected static final String TAG = "SearchRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static SearchRemoteServiceImpl instance = null;

	private SearchRemoteServiceImpl() {
	}

	public static SearchRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new SearchRemoteServiceImpl();
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
	 * com.iwgame.msgs.remote.SearchRemoteService#searchUser(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, java.lang.String, int)
	 */
	@Override
	public void searchUser(final ServiceCallBack<XActionResult> callback, Context context, String condition, String resulttype, long offset,
			int limit, Long uid) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		if (condition != null)
			params.put("condition", condition);
		params.put("resulttype", resulttype);
		params.put("offset", offset);
		params.put("limit", limit);
		if (uid != null)
			params.put("uid", uid);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_USER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.SearchRemoteService#searchUsers(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, java.lang.String, int,
	 * int, long, int, int)
	 */
	@Override
	public void searchUsers(final ServiceCallBack<XActionResult> callback, Context context, Boolean isfate, Integer rel, Long gid,
			Boolean isrecommend, String resulttype, String position, String uid, long offset, int limit, Integer nearDistance, Integer lastlogin,
			Integer sex, Boolean isfind,Integer source, String sid) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		if (position != null)
			params.put("position", position);
		if (isfate != null)
			params.put("isfate", isfate);
		if (rel != null)
			params.put("rel", rel);
		if (gid != null)
			params.put("gid", gid);
		if (isrecommend != null)
			params.put("isrecommend", isrecommend);
		params.put("resulttype", resulttype);
		if(source != null)
        params.put("source", source);
		if (uid != null)
			params.put("uid", uid);

		params.put("offset", offset);

		params.put("limit", limit);

		if (nearDistance != null)
			params.put("near", nearDistance);
		if (lastlogin != null)
			params.put("lastlogin", lastlogin);
		if (sex != null)
			params.put("sex", sex);
		if (isfind != null){
			params.put("isfind", isfind);
			params.put("isdistance", false);
		}
		if (sid != null)
			params.put("sid", sid);
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_USERS_NEW);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.SearchRemoteService#searchGame(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, java.lang.String, int,
	 * int, long, int)
	 */
	@Override
	public void searchGames(final ServiceCallBack<XActionResult> callback, Context context, Boolean isrecommend, Integer rel, Long uid,
			String resulttype, String position, Long gid, long offset, int limit, Integer uidtype, Integer near, Integer gtype, Boolean isfind,
			String platform,Integer source) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		if (position != null)
			params.put("position", position);
		if (isrecommend != null)
			params.put("isrecommend", isrecommend);
		if (rel != null)
			params.put("rel", rel);
		if (uid != null)
			params.put("uid", uid);

		params.put("resulttype", resulttype);

		if (gid != null)
			params.put("gid", gid);

		params.put("offset", offset);

		params.put("limit", limit);

		if (uidtype != null)
			params.put("uidtype", uidtype);

		if (near != null)
			params.put("near", near);
		if (gtype != null)
			params.put("gtype", gtype);
		if (isfind != null)
			params.put("isfind", isfind);
		if (platform != null)
			params.put("platform", platform);

		if(source != null)
			params.put("source", source);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_GAMES);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.SearchRemoteService#searchTop(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, int)
	 */
	@Override
	public void searchTop(final ServiceCallBack<XActionResult> callback, Context context, int type) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("type", type);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_TOP);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#searchDistance(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void searchDistance(final ServiceCallBack<XActionResult> callback, Context context, String postion, String ids) {
		if (callback == null)
			return;
		if (ids != null && postion != null) {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("postion", postion);

			params.put("ids", ids);
			RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_DISTANCE);
		} else {
			callback.onFailure(ErrorCode.EC_CLIENT_REMOTE_MESSAGE, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.SearchRemoteService#searchGroups(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.String,
	 * long, int, int, int)
	 */
	@Override
	public void searchGroups(ServiceCallBack<XActionResult> callback, Context context, String idname, Long uid, String gid, long offset, int limit,
			Integer resulttype, Integer near, Integer minCount, Integer maxCount, Boolean needValidate, Boolean isfind, String position, Integer source, String sid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (idname != null)
			params.put("idname", idname);
		if (uid != null)
			params.put("uid", uid);
		if (gid != null)
			params.put("gid", gid);
		params.put("offset", offset);
		params.put("limit", limit);
		if (resulttype != null)
			params.put("resulttype", resulttype);
		if (near != null)
			params.put("near", near);
		if (minCount != null)
			params.put("minCount", minCount);
		if (maxCount != null)
			params.put("maxCount", maxCount);
		if (needValidate != null)
			params.put("needValidate", needValidate);
		if (isfind != null)
			params.put("isfind", isfind);
		if (position != null)
			params.put("position", position);
		if (source != null)
			params.put("source", source);
		if(sid != null)
			params.put("sid", sid);
		params.put(SystemConfig.APPTYPE, SystemContext.APPTYPE);
		LogUtil.info("lll searchGroups = " +params );
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_GROUP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.SearchRemoteService#searchAppGroupUsers
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long,
	 * long)
	 */
	@Override
	public void searchAppGroupUsers(ServiceCallBack<XActionResult> callback, Context context, long maxUid, long grid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", maxUid);
		params.put("grid", grid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_CONTENT_GROUP_APPLY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.SearchRemoteService#searchRecommendGames
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context, int,
	 * int)
	 */
	@Override
	public void searchRecommendGames(ServiceCallBack<XActionResult> callback, Context context, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_RECOMMEND_GAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.SearchRemoteService#searchRecommendGroups
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, int, int)
	 */
	@Override
	public void searchRecommendGroups(ServiceCallBack<XActionResult> callback, Context context, String gids, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (gids != null)
			params.put("gids", gids);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_RECOMMEND_GROUP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.SearchRemoteService#searchRecommendUsers
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.String, int, int)
	 */
	@Override
	public void searchRecommendUsers(ServiceCallBack<XActionResult> callback, Context context, String grids, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (grids != null)
			params.put("grids", grids);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_RECOMMEND_USER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.SearchRemoteService#searchPostBarUsers(
	 * com.iwgame.msgs.common.ServiceCallBack, android.content.Context,
	 * java.lang.Long, long, int)
	 */
	@Override
	public void searchPostBarManager(ServiceCallBack<XActionResult> callback, Context context, long gid, long offset, int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gid", gid);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_POSTBAR_MASTER_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.SearchRemoteService#searchGameFriendFollowers
	 * (com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void searchGameFriendFollowers(ServiceCallBack<XActionResult> callback, Context context, String gids) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("gids", gids);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_GAME_FRIEND_COUNT);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.SearchRemoteService#getPoint(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, java.lang.String, int)
	 */
	@Override
	public void getPoint(ServiceCallBack<XActionResult> callback, Context context, String tids, int ttype) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tids", tids);
		params.put("ttype", ttype);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_POINT);
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.SearchRemoteService#getExperienceValue(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, java.lang.String, int)
	 */
	public void getExperienceValue(ServiceCallBack<XActionResult> callback,Context context,String tids , int ttype){
		if(callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tids", tids);
		params.put("ttype", ttype);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_POINT);
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.SearchRemoteService#syncPointOrTaskPolicy(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, int, long)
	 */
	@Override
	public void syncPointOrTaskPolicy(ServiceCallBack<XActionResult> callback, Context context, int type, long updatetime) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("updatetime", updatetime);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POINT_CONFIG);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.SearchRemoteService#searchUserContributeGroupPoint(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void searchUserContributeGroupPoint(ServiceCallBack<XActionResult> callback, Context context, String grids) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("grids", grids);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_CONTRIBUTION_POINT);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.SearchRemoteService#searchTopicNews(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, long, int)
	 */
	@Override
	public void searchTopicNews(ServiceCallBack<XActionResult> callback, Context context, long offset, int limit) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_POSTBAR_SEARCH_TOPIC_LIST_TOP);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.SearchRemoteService#searchSearch(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, int)
	 */
	@Override
	public void searchGameService(ServiceCallBack<XActionResult> callback, Context context){
		Map<String, Object> params = new HashMap<String, Object>();
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_SEARCH_USER_GAMESERVER);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.SearchRemoteService#searchGameBykeyword(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void searchGameBykeyword(ServiceCallBack<XActionResult> callback, Context context, String keyword, long offset, int limit){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("keyword", keyword);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.XACTION_NEEDAUTH_COMMAND|1617);
	}

}
