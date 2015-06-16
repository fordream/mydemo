/**      
 * PlayRemoteService.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PlayEvalRequest;
import com.iwgame.msgs.proto.Msgs.PlayOrderAppeal;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: PlayRemoteService
 * @Description: 陪玩数据服务接口
 * @author 王卫
 * @date 2015-5-8 下午3:17:57
 * @Version 1.0
 * 
 */
public interface PlayRemoteService {

	/**
	 * 查找创建陪玩
	 * 
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void searchCreatPlays(ServiceCallBack<XActionResult> callback, Context context, Long uid, Integer status, long offset, int limit);

	/**
	 * 发现查找陪玩
	 * 
	 * @param callback
	 * @param context
	 * @param resulttype
	 * @param offset
	 * @param limit
	 */
	public void searchDiscoverPlays(ServiceCallBack<XActionResult> callback, Context context, long gid, long sid, int sorttype, Long keyid, 
			String keyval, Integer sex, String resulttype, String position, long offset, int limit);

	/**
	 * 创建编辑陪玩信息
	 * 
	 * @param callback
	 * @param context
	 * @param playid
	 * @param image
	 * @param info
	 */
	public void addPlayData(ServiceCallBack<XActionResult> callback, Context context, Long playid, byte[] image, Msgs.PlayInfo info);

	/**
	 * 查询陪玩详情
	 * 
	 * @param callback
	 * @param context
	 * @param playid
	 * @param resulttype
	 */
	public void searchPlayInfo(ServiceCallBack<XActionResult> callback, Context context, Long playid, String resulttype);

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 */
	public void getGameStarLeve(ServiceCallBack<XActionResult> callback, Context context, Long gid);

	/**
	 * 改变陪玩状态
	 * 
	 * @param callback
	 * @param context
	 * @param pid
	 * @param type
	 */
	public void changePlayStatus(ServiceCallBack<XActionResult> callback, Context context, Long pid, int type);

	/**
	 * 报名下单
	 * 
	 * @param callback
	 * @param context
	 * @param playid
	 * @param roleid
	 * @param gid
	 * @param sid
	 * @param starttime
	 * @param duration
	 */
	public void playEnrollOrder(ServiceCallBack<XActionResult> callback, Context context, long playid, long roleid, long gid, long sid,
			long starttime, Integer duration);

	/**
	 * 查找我报名的陪玩
	 * 
	 * @param callback
	 * @param context
	 * @param offset
	 * @param limit
	 */
	public void searchApplyPlays(ServiceCallBack<XActionResult> callback, Context context, long offset, int limit);

	/**
	 * 报名管理查询订单
	 * 
	 * @param callback
	 * @param context
	 * @param status
	 * @param offset
	 * @param limit
	 */
	public void searchApplyOrder(ServiceCallBack<XActionResult> callback, Context context, long pid, String status, long offset, int limit);

	/**
	 * 获取订单详情
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void getOrderDetail(ServiceCallBack<XActionResult> callback, Context context, long oid);

	/**
	 * 确认陪玩
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void acceptOrder(ServiceCallBack<XActionResult> callback, Context context, long oid);

	/**
	 * 确认付款
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void payOrder(ServiceCallBack<XActionResult> callback, Context context, long oid);

	/**
	 * 申诉
	 * 
	 * @param callback
	 * @param context
	 * @param playOrderAppeal
	 */
	public void appealOrder(ServiceCallBack<XActionResult> callback, Context context, Msgs.PlayOrderAppeal playOrderAppeal);

	/**
	 * 取消陪玩
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void cancelOrder(ServiceCallBack<XActionResult> callback, Context context, long oid);
	/**
	 * 获取陪玩评论
	 * @param callback
	 * @param context
	 * @param pid
	 * @param offset
	 * @param limit
	 */
	public void getPlayComments(ServiceCallBack<XActionResult> callback,Context context,long pid,long offset, int limit);
	/**
	 * 发送陪玩回复
	 * @param callback
	 * @param context
	 * @param id
	 * @param content
	 */
	public void sendPlayRely(ServiceCallBack<XActionResult>callback,Context context,long id,String content);

	/**
	 * 提交评价
	 * 
	 * @param callback
	 * @param context
	 * @param playeval
	 *         (评价信息类)
	 * 
	 */
	public void playEvalOrder(ServiceCallBack<XActionResult> callback, Context context, PlayEvalRequest playeval);

	/**
	 * 获取申诉数据
	 * 
	 * @param callback
	 * @param context
	 * @param oid
	 */
	public void searchAppealInfo(ServiceCallBack<XActionResult> callback, Context context, long oid);
}
