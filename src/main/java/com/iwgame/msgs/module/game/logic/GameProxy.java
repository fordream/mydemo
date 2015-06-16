/**      
 * GameProxy.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.logic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.module.game.object.GameExtDataObj;
import com.iwgame.msgs.module.game.object.GameExtDataVo;
import com.iwgame.msgs.module.game.object.GameTopItemObj;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: GameProxy
 * @Description: 贴吧服务数据代理接口
 * @author 王卫
 * @date 2013-9-3 上午11:59:09
 * @Version 1.0
 * 
 */
public interface GameProxy {
    
       /**
        * 从本地获得关注的贴吧 
        * @param context
        * @return
        */
       public void getFollowGamesForLocal(ProxyCallBack<List<GameVo>> callback ,Context context);
       
       /**
        * 从本地获得关注的贴吧 信息
        * @param callback
        * @param context
        * @param gid
        */
       public void getRelGameInfoForLocal(ProxyCallBack<RelationGameVo> callback, Context context, long gid);
       

	/**
	 * 获取关系贴吧
	 * @param callback
	 * @param context
	 * @param isNotGetLocal 是否不从本地获得数据，true ，不从本地获得数据，false从本地获得数据 
	 */
	public void getFollowGames(ProxyCallBack<List<GameVo>> callback, Context context, boolean isNotGetLocal);

	/**
	 * 获取关系贴吧
	 * @param callback
	 * @param context
	 * @param isNotGetLocal
	 * @param utime
	 */
	public void getFollowGamesByUtime(ProxyCallBack<List<GameVo>> callback, Context context, boolean isNotGetLocal, Long utime);

	/**
	 * 获取贴吧
	 * 
	 * @param callback
	 * @param gid
	 */
	public void getGameInfo(ProxyCallBack<GameVo> callback, Context context, long gid);
	
	/**
	 * 获取多个贴吧
	 * 
	 * @param callback
	 * @param gid
	 */
	public void getGamesInfo(ProxyCallBack<List<GameVo>> callback, Context context, List<Long> gids, boolean isNotFromLocal);

	/**
	 * 获取贴吧赞踩
	 * 
	 * @param callback
	 * @param gid
	 */
	public void getGameLikeInfo(ProxyCallBack<GameExtDataObj> callback, Context context, long gid);

	/**
	 * 查找关注的贴吧信息
	 * 
	 * @param callback
	 * @param gid
	 */
	public void getRelGameInfo(ProxyCallBack<RelationGameVo> callback, Context context, long gid);

	/**
	 * 查找贴吧
	 * 
	 * @param callback
	 * @param context
	 * @param keyword
	 */
	public void searchGame(ProxyCallBack<List<ExtGameVo>> callback, Context context, String keyword);

	/**
	 * 查找贴吧的关注数
	 * @param callback
	 * @param context
	 * @param ids
	 */
	public void searchGameFollowCount(ProxyCallBack<HashMap<Long,Long>> callback, Context context, String ids);

	
	/**
	 * 获取贴吧
	 * 
	 * @param callback
	 * @param context
	 * @param isrecommend
	 * @param rel
	 * @param uid
	 * @param resulttype
	 * @param gid
	 * @param offset
	 * @param limit
	 * @param uidtype
	 * @param near
	 * @param gtype
	 * @param isfind
	 * @param platform
	 */
	public void getConditionGame(ProxyCallBack<PagerVo<ExtGameVo>> callback, Context context, Boolean isrecommend, Integer rel, Long uid,
			String resulttype, Long gid, long offset, int limit, Integer uidtype, Integer near, Integer gtype, Boolean isfind, String platform,Integer source);

	/**
	 * 搜索贴吧包（本地）
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 */
	public void searchGamePackage(ProxyCallBack<List<GamePackageVo>> callback, Context context, long gid);

	/**
	 * 贴吧排行榜
	 * 
	 * @param callback
	 * @param context
	 * @param type
	 */
	public void searchGameTop(ProxyCallBack<List<GameTopItemObj>> callback, Context context, int type);

	/**
	 * 搜索所有贴吧
	 * 
	 * @param callback
	 * @param context
	 */
	public void searchAllGame(ProxyCallBack<List<GameVo>> callback, Context context);

	/**
	 * 搜素推荐贴吧
	 * 
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void searchRecommendGames(ProxyCallBack<PagerVo<ExtGameVo>> callback, Context context, long offset, int limit);

	/**
	 * 获取贴吧贴吧最大Index
	 * 
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit	
	 */
	public void getGamePostbarMaxIndex(ProxyCallBack<List<ExtGameVo>> callback, Context context, String gids);

	/**
	 * 获取贴吧的扩展数据
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 */
	public void getGameExtData(ProxyCallBack<GameExtDataVo> callback, Context context, long gid, int type);

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param gids
	 */
	public void getGameFriendFollowers(ProxyCallBack<Map<Long, ExtGameVo>> callback, Context context, String gids);
	
	/**
	 * 获取游戏服务器
	 * @param callback
	 * @param context
	 * @param type
	 */
	public void getGameService(ProxyCallBack<Msgs.UserGameServerResult> callback, Context context);
	
	/**
	 * 根据关键字查询贴吧游戏
	 * @param callback
	 * @param context
	 * @param keyword
	 */
	public void searchGameBykeyword(ProxyCallBack<List<ExtGameVo>> callback, Context context, String keyword, long offset, int limit);

}
