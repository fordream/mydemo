/**      
 * QueryRemoteService.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.List;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: SearchRemoteService
 * @Description: 查询服务
 * @author 王卫
 * @date 2013-9-24 下午05:07:37
 * @Version 1.0
 * 
 */
public interface SearchRemoteService {

	/**
	 * 精确查找用户信息
	 * 
	 * @param callback
	 * @param context
	 * @param condition
	 * @param resulttype
	 * @param offset
	 * @param limit
	 */
	public void searchUser(ServiceCallBack<XActionResult> callback, Context context, String condition, String resulttype, long offset, int limit,
			Long uid);

	/**
	 * 条件查找用户
	 * 
	 * @param callback
	 * @param context
	 * @param isfate
	 * @param rel
	 * @param gid
	 * @param isrecommend
	 * @param resulttype
	 * @param position
	 * @param uid
	 * @param offset
	 * @param limit
	 * @param isnear
	 * @param lastlogin
	 * @param sex
	 */
	public void searchUsers(ServiceCallBack<XActionResult> callback, Context context, Boolean isfate, Integer rel, Long gid, Boolean isrecommend,
			String resulttype, String position, String uid, long offset, int limit, Integer nearDistance, Integer lastlogin, Integer sex, Boolean isfind,Integer source, String sid);

	/**
	 * 查询贴吧信息
	 * 
	 * @param callback
	 * @param context
	 * @param isrecommend
	 * @param rel
	 * @param uid
	 * @param resulttype
	 * @param position
	 * @param gid
	 * @param offset
	 * @param limit
	 * @param near
	 * @param gtype
	 * @param platform
	 */
	public void searchGames(ServiceCallBack<XActionResult> callback, Context context, Boolean isrecommend, Integer rel, Long uid, String resulttype,
			String position, Long gid, long offset, int limit, Integer uidtype, Integer near, Integer gtype, Boolean isfind, String platform,Integer source);

	/**
	 * 获取排行榜
	 * 
	 * @param callback
	 * @param context
	 * @param type
	 *            [1:关注排行榜]
	 */
	public void searchTop(ServiceCallBack<XActionResult> callback, Context context, int type);

	/**
	 * 查找我与用户之间的距离
	 * 
	 * @param callback
	 * @param context
	 * @param postion
	 * @param ids
	 */
	public void searchDistance(ServiceCallBack<XActionResult> callback, Context context, String postion, String ids);

	/**
	 * 搜索公会
	 * 
	 * @param callback
	 * @param context
	 * @param idname
	 * @param uid
	 * @param gid
	 * @param offset
	 * @param limit
	 * @param resulttype
	 * @param near
	 * @param minCount
	 * @param maxCount
	 * @param needValidate
	 */
	public void searchGroups(ServiceCallBack<XActionResult> callback, Context context, String idname, Long uid, String gid, long offset, int limit,
			Integer resulttype, Integer near, Integer minCount, Integer maxCount, Boolean needValidate, Boolean isfind, String position,
			Integer source, String sid);

	/**
	 * 查询申请公会的用户
	 * 
	 * @param callback
	 * @param context
	 * @param maxUid
	 * @param grid
	 */
	public void searchAppGroupUsers(ServiceCallBack<XActionResult> callback, Context context, long maxUid, long grid);

	/**
	 * 查询推荐的贴吧
	 * 
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void searchRecommendGames(ServiceCallBack<XActionResult> callback, Context context, long offset, int limit);

	/**
	 * 查询推荐的公会
	 * 
	 * @param callback
	 * @param context
	 * @param gids
	 * @param offset
	 * @param limit
	 */
	public void searchRecommendGroups(ServiceCallBack<XActionResult> callback, Context context, String gids, long offset, int limit);

	/**
	 * 查询推荐的玩家
	 * 
	 * @param callback
	 * @param context
	 * @param grids
	 * @param offset
	 * @param limit
	 */
	public void searchRecommendUsers(ServiceCallBack<XActionResult> callback, Context context, String grids, long offset, int limit);

	/**
	 * 搜索贴吧吧主列表
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 * @param offset
	 * @param limit
	 */
	public void searchPostBarManager(ServiceCallBack<XActionResult> callback, Context context, long gid, long offset, int limit);

	/**
	 * 查找和我共同关注的贴吧的好友关注数
	 * 
	 * @param callback
	 * @param context
	 * @param gids
	 */
	public void searchGameFriendFollowers(ServiceCallBack<XActionResult> callback, Context context, String gids);

	/**
	 * 查询积分（用户、公会）
	 * @param callback
	 * @param context
	 * @param tids
	 * @param ttype
	 */
	public void getPoint(ServiceCallBack<XActionResult> callback, Context context, String tids, int ttype);
	/**
	 * 查询经验值
	 * @param callback
	 * @param context
	 * @param tids
	 * @param ttype
	 */
	public void getExperienceValue(ServiceCallBack<XActionResult> callback,Context context,String tids , int ttype);
	
	/**
	 * 同步用户公会积分和任务
	 * @param callback
	 * @param context
	 * @param type
	 * @param updatetime
	 */
	public void syncPointOrTaskPolicy(ServiceCallBack<XActionResult> callback, Context context, int type, long updatetime);
	
	/**
	 * 
	 * @param callback
	 * @param context
	 * @param grids
	 */
	public void searchUserContributeGroupPoint(ServiceCallBack<XActionResult> callback, Context context, String grids);
	
	/**
	 * 查找贴吧动态
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void searchTopicNews(ServiceCallBack<XActionResult> callback, Context context, long offset, int limit);
	
	/**
	 * 
	 * @param callback
	 * @param context
	 * @param type
	 */
	public void searchGameService(ServiceCallBack<XActionResult> callback, Context context);
	
	/**
	 * 
	 * @param callback
	 * @param context
	 * @param keyword
	 */
	public void searchGameBykeyword(ServiceCallBack<XActionResult> callback, Context context, String keyword, long offset, int limit);

}
