/**      
 * PlayProxy.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.logic;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.IdResult;
import com.iwgame.msgs.proto.Msgs.PlayApplyOrderInfo;
import com.iwgame.msgs.proto.Msgs.PlayEvalList;
import com.iwgame.msgs.proto.Msgs.PlayEvalRequest;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.PlayOrderAppeal;
import com.iwgame.msgs.proto.Msgs.PlayOrderInfo;
import com.iwgame.msgs.proto.Msgs.PlayStar;
import com.iwgame.msgs.vo.local.PagerVo;

/**
 * @ClassName: PlayProxy
 * @Description: 陪玩数据代理类接口
 * @author 王卫
 * @date 2015-5-8 下午3:22:45
 * @Version 1.0
 * 
 */
public interface PlayProxy {

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param offet
	 * @param limit
	 */
	public void searchCreatPlays(ProxyCallBack<PagerVo<PlayInfo>> callback, Context context, Long uid, Integer status, long offset, int limit);

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param resulttype
	 * @param offet
	 * @param limit
	 */
	public void searchDiscoverPlays(ProxyCallBack<PagerVo<PlayInfo>> callback, Context context, long gid, long sid, int sorttype, 
			Long keyid, String keyval, Integer sex, String resulttype, String position, long offset, int limit);

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param playid
	 * @param image
	 * @param info
	 */
	public void createPlayInfo(ProxyCallBack<Integer> callback, Context context, Long playid, byte[] image, PlayInfo info);

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param playid
	 * @param resulttype
	 */
	public void searchPlayDetailInfo(ProxyCallBack<PlayInfo> callback, Context context, Long playid, String resulttype);

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 */
	public void getGameStarLeve(ProxyCallBack<PlayStar> callback, Context context, Long gid);

	/**
	 * 改变陪玩
	 * 
	 * @param callback
	 * @param context
	 * @param pid
	 * @param type
	 */
	public void changePlayStatus(ProxyCallBack<Integer> callback, Context context, Long pid, int type);

	/**
	 * 陪玩下单
	 * 
	 * @param callback
	 * @param context
	 * @param pid
	 * @param rid
	 * @param gid
	 * @param sid
	 * @param time
	 * @param duration
	 */
	public void createPlayEnroll(ProxyCallBack<IdResult> callback, Context context, long pid, long rid, long gid, long sid, long time,
			Integer duration);

	/**
	 * 查找我报名的陪玩
	 * 
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void searchApplyPlays(ProxyCallBack<PagerVo<PlayOrderInfo>> callback, Context context, long offset, int limit);

	/**
	 * 报名管理查询订单
	 * 
	 * @param callback
	 * @param context
	 * @param status
	 * @param offset
	 * @param limit
	 */
	public void searchApplyOrder(ProxyCallBack<PagerVo<PlayApplyOrderInfo>> callback, Context context, long pid, String status, long offset, int limit);

	/**
	 * 订单详情
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void getOrderDetail(ProxyCallBack<PlayOrderInfo> callback, Context context, long oid);

	/**
	 * 确认陪玩
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void acceptOrder(ProxyCallBack<Integer> callback, Context context, long oid);

	/**
	 * 确认付款
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void payOrder(ProxyCallBack<Integer> callback, Context context, long oid);

	/**
	 * 申诉
	 * 
	 * @param callback
	 * @param context
	 * @param playOrderAppeal
	 */
	public void appealOrder(ProxyCallBack<Integer> callback, Context context, Msgs.PlayOrderAppeal playOrderAppeal);

	/**
	 * 取消报名
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void cancelOrder(ProxyCallBack<Integer> callback, Context context, long oid);
	/**
	 * 获取陪玩的评论和回复内容
	 * @param callback
	 * @param context
	 * @param pid
	 * @param offset
	 * @param limit
	 */
	public void getPlayComments(ProxyCallBack<PlayEvalList> callback,Context context,long pid,long offset, int limit);
	/**
	 * 发送评价的评论
	 * @param callback
	 * @param context
	 * @param id
	 * @param content
	 */
	public void sendPlayReply(ProxyCallBack<Integer>callback,Context context,long id,String content);

	/**
	 * 提交评价
	 * 
	 * @param callback
	 * @param context
	 * @param playeval
	 *       (评价信息类)
	 * 
	 */
	public void playEvalOrder(ProxyCallBack<Integer> callback,Context context, PlayEvalRequest playeval);

	/**
	 * 获取申诉数据
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void seacrhAppealInfo(ProxyCallBack<PlayOrderAppeal> callback, Context context, long oid);
}
