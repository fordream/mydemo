/**      
 * ContentRemoteService.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: ContentRemoteService
 * @Description: 内容服务
 * @author 王卫
 * @date 2013-9-24 下午05:05:50
 * @Version 1.0
 * 
 */
public interface ContentRemoteService {

	/**
	 * 同步内容列表
	 * 
	 * @param callback
	 * @param context
	 * @param type
	 * @param utime
	 */
	public void syncContent(ServiceCallBack<XActionResult> callback, Context context, int type, long utime);

	/**
	 * 获取内容详细信息
	 * 
	 * @param callback
	 * @param context
	 * @param id
	 * @param type
	 * @param utime
	 */
	public void getDetailContent(ServiceCallBack<XActionResult> callback, Context context, ContentDetailParams params, int type, Long uid);

	/**
	 * 获取内容的扩展数据
	 * 
	 * @param callback
	 * @param context
	 * @param ids
	 * @param type
	 */
	public void getExtContent(ServiceCallBack<XActionResult> callback, Context context, String ids, int type);

	/**
	 * 获取内容列表数据
	 * 
	 * @param callback
	 * @param context
	 * @param ownerId
	 * @param otype
	 * @param type
	 */
	public void getContentList(ServiceCallBack<XActionResult> callback, Context context, long ownerId, int otype, int type, int offset, int limit);

	/**
	 * 同步公会用户组成员
	 * 
	 * @param callback
	 * @param context
	 * @param grid
	 * @param utime
	 */
	public void syncGroupUser(ServiceCallBack<XActionResult> callback, Context context, Long grid, Long utime);

	/**
	 * 保存日志信息（错误日志）
	 * 
	 * @param callback
	 * @param context
	 * @param content
	 * @param imsi
	 * @param sverion
	 * @param btype
	 * @param pmodel
	 * @param mac
	 * @param position
	 */
	public void saveLog(ServiceCallBack<XActionResult> callback, Context context, String content, byte[] data, String imsi, String sverion, String btype,
			String pmodel, String mac, String position);
}
