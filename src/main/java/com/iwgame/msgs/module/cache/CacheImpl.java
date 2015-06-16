/**      
 * CacheImpl.java Create on 2014-1-23     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.cache;

import java.io.IOException;
import java.util.List;

import android.content.Context;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: CacheImpl
 * @Description: 缓存数据存取实现类
 * @author 王卫
 * @date 2014-1-23 上午10:19:19
 * @Version 1.0
 * 
 */
public class CacheImpl implements Cache {

	private static byte[] lock = new byte[0];

	private static CacheImpl instance = null;

	private static final String cachePre = "cache-";

	protected static final String TAG = "CacheImpl";

	private CacheImpl() {

	}

	public static CacheImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new CacheImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.cache.Cache#saveData(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void saveData(int dataType, Object data) {
		try {
			FileUtils.writeFile(SystemContext.getInstance().getContext(), cachePre + SystemContext.getInstance().getExtUserVo().getUserid() + "-"
					+ dataType, data, Context.MODE_PRIVATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.cache.Cache#getData(java.lang.String)
	 */
	@Override
	public void getData(int dataType, CacheCallBack callBack) {
		switch (dataType) {
		case Cache.DATA_TYPE_CONTRANCT_USER_FRIEND:
			getContranctUser(1, 1, callBack);
			break;
		case Cache.DATA_TYPE_CONTRANCT_FANS:
			getContranctUser(2, 1, callBack);
			break;
		case Cache.DATA_TYPE_CONTRANCT_GROUP:
			syncGroupListData(callBack);
			break;
		case Cache.DATA_TYPE_DISCOVER_USER:
		case Cache.DATA_TYPE_DISCOVER_GAME:
		case Cache.DATA_TYPE_DISCOVER_PLAY:
		case Cache.DATA_TYPE_DISCOVER_GROUP:
		case Cache.DATA_TYPE_GAME_TOP:
		case Cache.DATA_TYPE_GAME_RECOMMEND:
		case Cache.DATA_TYPE_GAME_NEAR:
		case Cache.DATA_TYPE_TASK_POINTTASK:
			getCachePoolData(dataType, callBack);
			break;
		default:
			break;
		}
	}

	/************************* 通讯录缓存数据 *************************/
	private void getContranctUser(int mode, int sort, CacheCallBack callBack) {
		syncUserListData(mode, sort, callBack);
	}

	/**
	 * 获取用户通讯录数据（关注、粉丝）
	 * 
	 * @param mode
	 * @param sort
	 * @param callBack
	 */
	private void getUserContactData(final int mode, final int sort, final CacheCallBack callBack) {
		ProxyFactory.getInstance().getUserProxy().getContactUsers(new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> result) {
				callBack.onBack(result);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				callBack.onBack(null);
			}
		}, null, mode, 1, sort, 0, Integer.MAX_VALUE, null);
	}

	/**
	 * 获取我的公会
	 * 
	 * @param callBack
	 */
	private void getMyGroupData(final CacheCallBack callBack) {
		ProxyFactory.getInstance().getGroupProxy().getMyGroups(new ProxyCallBack<List<GroupVo>>() {

			@Override
			public void onSuccess(List<GroupVo> result) {
				callBack.onBack(result);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				callBack.onBack(null);
			}
		}, null);
	}

	/**
	 * 同步用户数据
	 * 
	 * @param mode
	 * @param sort
	 * @param callBack
	 */
	private void syncUserListData(final int mode, final int sort, final CacheCallBack callBack) {
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_REL_USER, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				Long afterSyncKey = SystemContext.getInstance().getUserRelSyncKey();
			    getUserContactData(mode, sort, callBack);
			}

			@Override
			public void onFailure(Integer result) {
				getUserContactData(mode, sort, callBack);
			}
		});
	}

	/**
	 * 同步我的公会数据
	 * 
	 * @param mode
	 * @param sort
	 * @param callBack
	 */
	private void syncGroupListData(final CacheCallBack callBack) {
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GROUP, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
					getMyGroupData(callBack);
			}

			@Override
			public void onFailure(Integer result) {
				LogUtil.e(TAG, "同步公会失败");
				getMyGroupData(callBack);
			}
		});
	}

	/************************* 发现缓存数据 *************************/
	/**
	 * 
	 * @param callBack
	 */
	private void getCachePoolData(int type, final CacheCallBack callBack) {
		try {
			if (SystemContext.getInstance().getExtUserVo() != null) {
				Object result = FileUtils.readFile(SystemContext.getInstance().getContext(), cachePre
						+ SystemContext.getInstance().getExtUserVo().getUserid() + "-" + type);
				callBack.onBack(result);
			} else {
				callBack.onBack(null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			callBack.onBack(null);
		}
	}
	
	
}
