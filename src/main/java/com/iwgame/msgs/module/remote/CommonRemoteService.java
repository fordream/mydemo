/**      
 * CommonRemoteService.java Create on 2014-5-3     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.proto.Msgs.UploadContactsRequest;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: CommonRemoteService
 * @Description: 公共远程服务接口
 * @author 王卫
 * @date 2014-5-3 下午4:42:02
 * @Version 1.0
 * 
 */
public interface CommonRemoteService {

	/**
	 * 获取同步列表数据
	 * 
	 * @param callback
	 * @param context
	 * @param id
	 * @param utime
	 * @param mode
	 */
	public void getSyncListData(ServiceCallBack<XActionResult> callback, Context context, Long id, long utime, int mode);

	/**
	 * 分享贴吧接口
	 * @param callback
	 * @param context
	 * @param id(帖子ID、用户ID)
	 * @param type(0分享帖子1分享个人资料)
	 */
	public void share(ServiceCallBack<XActionResult> callback, Context context, long id, int type, int target);
	
	/**
	 * 分享资料接口
	 * @param callback
	 * @param context
	 * @param id(分享对象ID，例如：帖子ID、用户ID)
	 * @param type(分享的对象，例如：0分享帖子1分享个人资料)
	 * @param target（分享平台，例如：新浪微博，微信等）
	 * @param tagId（分享贴吧时的标筛选标签的ID）
	 */
	public void shareForShortUrl(ServiceCallBack<XActionResult> callback, Context context, long id, int type, int target, int tagId);

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param id 网页的页面id
	 * @param type 网页
	 * @param target 分享平台
	 */
	public void shareWebPageForShortUrl(ServiceCallBack<XActionResult> callback, Context context, long id, int type, int target);
	
	/**
	 * 同步公会成员信息列表
	 * @param callback
	 * @param context
	 * @param grids
	 */
	public void syncGroupMembersList(ServiceCallBack<XActionResult>callback,Context context,long grids,long offset,int limite,int orderType);
	
	/**
	 * 采集应用信息
	 * @param callback
	 * @param context
	 * @param app
	 */
	public void collectAppInfo(ServiceCallBack<XActionResult>callback, Context context, String app);
	
	/**
	 * 采集通讯录信息发送到服务端 
	 * @param callback
	 * @param context
	 * @param b
	 */
	public void collectContactInfo(ServiceCallBack<XActionResult>callback,Context context, UploadContactsRequest b);
	
	/**
	 * 获取app启动页面图片资源
	 * @param callback
	 * @param context
	 */
	public void loadAppLuncherBg(ServiceCallBack<XActionResult>callback,Context context, int pageId);
	
	/**
	 * 记录用户行为日志
	 * @param callback
	 * @param context
	 * @param op 行为op
	 * @param tid 对象id
	 * @param ttype 对象类型
	 * @param content 备注信息
	 */
	public void collectActionlLog(ServiceCallBack<XActionResult>callback,Context context, int op, Long tid, Integer ttype, String content);
}
