/**      
 * SyncServiceImpl.java Create on 2013-12-19     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.module.remote.RemoteUtils;
import com.iwgame.msgs.module.sync.store.SyncEntityStoreImpl;
import com.iwgame.msgs.module.sync.store.SyncStore;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: SyncServiceImpl
 * @Description: 同步实体服务实现类
 * @author 王卫
 * @date 2013-12-19 上午11:30:25
 * @Version 1.0
 * 
 */
public class SyncEntityServiceImpl implements SyncEntityService {

	private static byte[] lock = new byte[0];

	private static SyncEntityServiceImpl instance = null;

	// 同步存储
	private SyncStore syncStore = null;

	private SyncEntityServiceImpl() {
	}

	public static SyncEntityServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new SyncEntityServiceImpl();
					instance.syncStore = new SyncEntityStoreImpl();
				}
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.sync.SyncEntityService#syncEntity(long, int,
	 * com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void syncEntity(final long id, final int syncType, final SyncCallBack callBack) {
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cb = ContentDetailParam.newBuilder();
		cb.setId(id);
		if (syncStore != null) {
			cb.setUtime(syncStore.getSyncKey(syncType, id));
		}
		p.addParam(cb.build());
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", syncType);
		RemoteUtils.tcpRequest(new ServiceCallBack<XActionResult>() {

			@Override
			public void onSuccess(XActionResult result) {
				if (syncStore != null) {
					List<Long> ids = new ArrayList<Long>();
					ids.add(id);
					syncStore.process(ids, syncType, result, callBack);
				} else {
					callBack.onFailure(syncType);
				}
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				callBack.onFailure(result);
			}
		}, null, params, Msgs.ids, p.build(), MsgsConstants.CMD_CONTENT_DETAIL_NEW);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.sync.SyncEntityService#syncEntity(List<Long>, int,
	 * com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void syncEntity(final List<Long> ids, final int syncType, Long uid, final SyncCallBack callBack) {
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		for(int i = 0 ; i < ids.size() ; i++){
			ContentDetailParam.Builder cb = ContentDetailParam.newBuilder();
			cb.setId(ids.get(i));
			if (syncStore != null) {
				cb.setUtime(syncStore.getSyncKey(syncType, ids.get(i)));
			}
			p.addParam(cb.build());
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", syncType);
		if(uid != null){
			params.put("uid", uid);
		}
		
		RemoteUtils.tcpRequest(new ServiceCallBack<XActionResult>() {

			@Override
			public void onSuccess(XActionResult result) {
				if (syncStore != null) {
					syncStore.process(ids, syncType, result, callBack);
				} else {
					callBack.onFailure(syncType);
				}
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				callBack.onFailure(result);
			}
		}, null, params, Msgs.ids, p.build(), MsgsConstants.CMD_CONTENT_DETAIL_NEW);
		
	}

}
