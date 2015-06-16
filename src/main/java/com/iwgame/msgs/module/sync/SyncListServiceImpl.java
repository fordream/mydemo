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

import android.content.Context;

import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.module.remote.RemoteUtils;
import com.iwgame.msgs.module.sync.store.SyncListStore;
import com.iwgame.msgs.module.sync.store.SyncListStoreImpl;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.RelationResult.RelationEntry;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameRoleServiceVo;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: SyncServiceImpl
 * @Description: 同步列表服务实现类
 * @author 王卫
 * @date 2013-12-19 上午11:30:25
 * @Version 1.0
 * 
 */
public class SyncListServiceImpl implements SyncListService {

	private static byte[] lock = new byte[0];

	private static SyncListServiceImpl instance = null;
	
	// 同步存储
	private SyncListStore syncStore = null;
	
	private Context context;
	private SyncListServiceImpl() {
	}

	public static SyncListServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new SyncListServiceImpl();
					instance.syncStore = new SyncListStoreImpl();
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
	 * @see com.iwgame.msgs.module.sync.SyncListService#syncList(int,
	 * java.lang.Long, com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void syncList(final int syncType, final Long id, final SyncCallBack callBack) {
		syncList(syncType, id, null, callBack);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.sync.SyncListService#syncList(int,
	 * com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void syncList(int syncType, SyncCallBack callBack) {
		syncList(syncType, null, null, callBack);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.sync.SyncListService#syncList(int, long,
	 * com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void syncListByUtime(int syncType, Long utime, SyncCallBack callBack) {
		syncList(syncType, null, utime, callBack);
	}

	public void syncList(final int syncType, final Long id, Long utime, final SyncCallBack callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(TYPE, syncType);
		if (utime != null) {
			params.put(UTIME, utime);
		} else {
			if (syncStore != null) {
				params.put(UTIME, syncStore.getSyncKey(syncType, id));
			}
		}
		if (id != null) {
			params.put(ID, id);
		}

		RemoteUtils.tcpRequest(new ServiceCallBack<XActionResult>() {

			@Override
			public void onSuccess(XActionResult result) {
				if (syncStore != null) {
					syncStore.process(id, syncType, result, callBack);
				} else {
					callBack.onFailure(syncType);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				callBack.onFailure(result);
			}
		}, null, params, MsgsConstants.CMD_USER_REL_SYNC);
	}
	
	/**
	 * 同步游戏服务器数据
	 * @param syncType
	 * @param utime
	 * @param callBack
	 */
	public void synRoleServiceList(final int syncType,final Long utime, final SyncCallBack callBack ){
		Map<String, Object> params = new HashMap<String, Object>();
		final List<GameRoleServiceVo> servicelist = new ArrayList<GameRoleServiceVo>();
		params.put(TYPE, syncType);
		if (utime != null) {
			params.put(UTIME, utime);
		}
		RemoteUtils.tcpRequest(new ServiceCallBack<XActionResult>() {

			@Override
			public void onSuccess(XActionResult result) {
				if(result!=null){
					List<Msgs.RelationResult.RelationEntry> list =result.getExtension(Msgs.relationResult).getEntryList();
					if(list!=null&&list.size()>0){
						for(int i =0;i<list.size();i++){
							Msgs.RelationResult.RelationEntry entity = list.get(i);
							GameRoleServiceVo vo = new GameRoleServiceVo();
							vo.setId(entity.getId());
							vo.setType(entity.getType());
							vo.setName(entity.getName());
							vo.setGameId(entity.getGameid());
							servicelist.add(vo);
						}
					}
				}
				callBack.onSuccess(servicelist);
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				callBack.onSuccess(servicelist);
			}
		}, null, params, MsgsConstants.CMD_USER_REL_SYNC);
	}
		
}