/**      
 * CommonRemoteServiceImpl.java Create on 2014-5-3     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.remote;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.proto.Msgs.UploadContactsRequest;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: CommonRemoteServiceImpl
 * @Description: TODO(...)
 * @author 王卫
 * @date 2014-5-3 下午4:45:05
 * @Version 1.0
 * 
 */
public class CommonRemoteServiceImpl implements CommonRemoteService {

	protected static final String TAG = "CommonRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static CommonRemoteServiceImpl instance = null;

	private CommonRemoteServiceImpl() {
	}

	public static CommonRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new CommonRemoteServiceImpl();
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
	 * com.iwgame.msgs.module.remote.CommonRemoteService#getSyncListData(com
	 * .iwgame.msgs.common.ServiceCallBack, android.content.Context, int, int)
	 */
	@Override
	public void getSyncListData(ServiceCallBack<XActionResult> callback, Context context, Long id, long utime, int type) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("utime", utime);
		if (id != null)
			params.put("id", id);
		params.put("type", type);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_REL_SYNC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.CommonRemoteService#sharePost(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long)
	 */
	@Override
	public void share(ServiceCallBack<XActionResult> callback, Context context, long id, int type, int target) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("type", type);
		params.put("target", target);
		params.put("source", 0);
	}

	/**
	 * 获取分享短链
	 */
	@Override
	public void shareForShortUrl(ServiceCallBack<XActionResult> callback, Context context, long id, int type, int target, int tagId) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("type", type);
		params.put("target", target);
		params.put("tagId", tagId);
		params.put("source", 0);

		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_USER_SHARE_INFO);//MsgsConstants.CMD_USER_SHARE);
	}
	
	/**
	 * 获取攻略网页分享短链
	 */
	public void shareWebPageForShortUrl(ServiceCallBack<XActionResult> callback, Context context, long id, int type, int target){
		if (callback == null)
			return;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("type", type);
		params.put("target", target);
		params.put("source", 0);
		params.put("apptype", SystemContext.APPTYPE);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_STRATEGY_USER_SHARE_INFO);//MsgsConstants.CMD_USER_SHARE);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.CommonRemoteService#collectAppInfo(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void collectAppInfo(ServiceCallBack<XActionResult> callback, Context context, String app) {
		if(callback == null) 
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("app", app);
		RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_USER_APP_STATISTIC);
	}

	/**
	 * 分页的获取
	 * 公会的成员列表
	 */
	@Override
	public void syncGroupMembersList(ServiceCallBack<XActionResult> callback,
			Context context, long grids, long offset, int limite, int orderType) {
		if(callback == null) return;
		Map<String, Object>params = new HashMap<String, Object>();
		params.put("grid", grids);
		params.put("offset", offset);
		params.put("limit", limite);
		params.put("orderType", orderType);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_CONTENT_SEARCH_GROUP_MEMBERS);
	}

	/**
	 * 采集了通讯录信息上传到服务端
	 */
	@Override
	public void collectContactInfo(ServiceCallBack<XActionResult> callback,
			Context context, UploadContactsRequest b) {
		if(callback == null) 
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uploadContactsRequest", b);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_UPLOAD_CONTACTS);
	}

	/**
	 * 获取app启动页面图片资源
	 */
	@Override
	public void loadAppLuncherBg(ServiceCallBack<XActionResult> callback,
			Context context, int pageId) {
		if(callback == null) 
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		long updateTime = SystemContext.getInstance().getLuncharBgLoadTime();
		params.put("updatetime", updateTime);
		params.put("type", pageId);
		params.put("apptype", SystemContext.APPTYPE);
		RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_GAME_STARTINGPAGE);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.remote.CommonRemoteService#collectActionlLog(com.iwgame.msgs.common.ServiceCallBack, android.content.Context, int, long, int, java.lang.String)
	 */
	@Override
	public void collectActionlLog(ServiceCallBack<XActionResult> callback, Context context, int op, Long tid, Integer ttype, String content) {
		if(callback == null) 
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("op", op);
		if(tid != null)
			params.put("tid", tid);
		if(ttype != null)
			params.put("ttype", ttype);
		if(content != null)
			params.put("content", content);
		RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_USER_ACTIONLOG);
	}

}
