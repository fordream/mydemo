/**      
 * ContentRemoteServiceImpl.java Create on 2013-9-24     
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
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: ContentRemoteServiceImpl
 * @Description: 内容服务实现类
 * @author 王卫
 * @date 2013-9-24 下午07:04:22
 * @Version 1.0
 * 
 */
public class ContentRemoteServiceImpl implements ContentRemoteService {

	protected static final String TAG = "ContentRemoteServiceImpl";

	private static byte[] lock = new byte[0];

	private static ContentRemoteServiceImpl instance = null;

	private ContentRemoteServiceImpl() {
	}

	public static ContentRemoteServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new ContentRemoteServiceImpl();
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
	 * com.iwgame.msgs.remote.ContentRemoteService#syncContent(com.iwgame.msgs
	 * .common.ServiceCallBack, android.content.Context, int, long)
	 */
	@Override
	public void syncContent(final ServiceCallBack<XActionResult> callback, Context context, int type, long utime) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		params.put("utime", utime);

		// RemoteUtils.tcpRequest(callback, context, params,
		// MsgsConstants.CMD_CONTENT_SYNC);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.ContentRemoteService#getDetailContent(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, int, int, long)
	 */
	@Override
	public void getDetailContent(final ServiceCallBack<XActionResult> callback, Context context, ContentDetailParams cdp, int type, Long uid) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		if(uid != null)
			params.put("uid", uid);
		RemoteUtils.tcpRequest(callback, context, params,Msgs.ids, cdp, MsgsConstants.CMD_CONTENT_DETAIL_NEW);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.ContentRemoteService#getExtContent(com.iwgame.
	 * msgs.common.ServiceCallBack, android.content.Context, java.lang.String,
	 * int)
	 */
	@Override
	public void getExtContent(final ServiceCallBack<XActionResult> callback, Context context, String ids, int type) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		if (ids != null)
			params.put("ids", ids);
		params.put("type", type);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_CONTENT_EXT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.remote.ContentRemoteService#getContentList(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long, int, int)
	 */
	@Override
	public void getContentList(final ServiceCallBack<XActionResult> callback, Context context, long ownerId, int otype, int type, int offset,
			int limit) {
		if (callback == null)
			return;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ownerId", ownerId);
		params.put("otype", otype);
		params.put("type", type);
		params.put("offset", offset);
		params.put("limit", limit);
		RemoteUtils.tcpRequest(callback, context, params, MsgsConstants.CMD_CONTENT_LIST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.UserRemoteService#syncGroupUser(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, long, long)
	 */
	@Override
	public void syncGroupUser(ServiceCallBack<XActionResult> callback, Context context, Long grid, Long utime) {
		if (callback == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		if (grid != null)
			params.put("id", grid);
		if (utime != null)
			params.put("utime", utime);

		// RemoteUtils.tcpRequest(callback, context, params,
		// MsgsConstants.CMD_CONTENT_SYNC_GROUP_MEMBERS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.remote.ContentRemoteService#saveLog(com.iwgame
	 * .msgs.common.ServiceCallBack, android.content.Context, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void saveLog(ServiceCallBack<XActionResult> callback, Context context, String content, byte[] data, String imsi, String sverion, String btype,
			String pmodel, String mac, String position) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (content != null)
			params.put("content", content);
		if (data != null)
			params.put("data", data);
		if (imsi != null)
			params.put("imsi", imsi);
		if (sverion != null)
			params.put("systemVersion", sverion);
		if (btype != null)
			params.put("networkType", btype);
		if (pmodel != null)
			params.put("phoneModel", pmodel);
		if (mac != null)
			params.put("mac", mac);
		if (position != null)
			params.put("position", position);
		LogUtil.d(TAG, "---->>content="+content+", data="+data);
		RemoteUtils.httpRequest(callback, context, params, MsgsConstants.CMD_USER_CLIENT_CRASH_LOG);
	}

}
