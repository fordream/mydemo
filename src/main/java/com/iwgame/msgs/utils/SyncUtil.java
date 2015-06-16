/**      
 * SyncUtil.java Create on 2013-10-12     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: SyncUtil
 * @Description: 同步
 * @author 王卫
 * @date 2013-10-12 下午01:38:12
 * @Version 1.0
 * 
 */
public class SyncUtil {

	protected static final String TAG = "SyncUtil";

	public static void sysInitData(SyncCallBack callBack) {
		callBack.onSuccess(null);
		//sysGame(callBack);
	}

	private static void sysGame(final SyncCallBack callBack) {
		final long time = System.currentTimeMillis();
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GAME, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				LogUtil.d(TAG, "---------------->同步服务端贴吧数据时间="+(System.currentTimeMillis() - time));
				LogUtil.d(TAG, "-------->贴吧数据同步完成");
				sysMyGame(callBack);
			}

			@Override
			public void onFailure(Integer result) {
				LogUtil.d(TAG, "-------->贴吧数据同步失败");
				sysMyGame(callBack);
			}
		});
	}

	private static void sysMyGame(final SyncCallBack callBack) {
		final long time = System.currentTimeMillis();
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				LogUtil.d(TAG, "---------------->同步我的贴吧数据时间="+(System.currentTimeMillis() - time));
				LogUtil.d(TAG, "-------->我的贴吧数据同步完成");
				sysMyRelUser(callBack);
			}

			@Override
			public void onFailure(Integer result) {
				LogUtil.d(TAG, "-------->我的贴吧数据同步失败");
				sysMyRelUser(callBack);
			}
		});
	}

	private static void sysMyRelUser(final SyncCallBack callBack) {
		final long time = System.currentTimeMillis();
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_REL_USER, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				LogUtil.d(TAG, "---------------->同步我的好友数据时间="+(System.currentTimeMillis() - time));
				LogUtil.d(TAG, "-------->我的用户关系数据同步完成");
				sysMyGroup(callBack);
			}

			@Override
			public void onFailure(Integer result) {
				LogUtil.d(TAG, "-------->我的用户关系数据同步失败");
				sysMyGroup(callBack);
			}
		});
	}

	private static void sysMyGroup(final SyncCallBack callBack) {
		final long time = System.currentTimeMillis();
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GROUP, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				LogUtil.d(TAG, "---------------->同步我的公会数据时间="+(System.currentTimeMillis() - time));
				LogUtil.d(TAG, "-------->我的公会数据同步完成");
				callBack.onSuccess(result);
			}

			@Override
			public void onFailure(Integer result) {
				LogUtil.d(TAG, "-------->我的公会数据同步失败");
				callBack.onSuccess(result);
			}
		});
	}

}
