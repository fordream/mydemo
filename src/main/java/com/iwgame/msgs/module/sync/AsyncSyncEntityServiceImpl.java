/**      
 * SyncEntityTransformService.java Create on 2014-1-26     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync;

import java.util.List;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.AsyncResponseHandler;
import com.iwgame.msgs.common.MyAsyncTask;

/**
 * @ClassName: AsyncSyncEntityServiceImpl
 * @Description: TODO(...)
 * @author chuanglong
 * @date 2014-1-26 上午9:03:46
 * @Version 1.0
 * 
 */
public class AsyncSyncEntityServiceImpl implements SyncEntityService {

	private static byte[] lock = new byte[0];

	private static AsyncSyncEntityServiceImpl instance = null;

	private AsyncSyncEntityServiceImpl() {
	}

	public static AsyncSyncEntityServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new AsyncSyncEntityServiceImpl();
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
	public void syncEntity(final long id, final int syncType, final SyncCallBack callback) {
		// TODO Auto-generated method stub
		final AsyncResponseHandler<Object> handler = new AsyncResponseHandler<Object>() {

			@Override
			public void onSuccess(Object content) {
				if (callback != null)
					callback.onSuccess(content);
			}

			@Override
			public void onFailure(Integer content,String resultMsg) {
				if (callback != null)
					callback.onFailure(content);
			}
		};

		final SyncCallBack newCallback = new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				handler.setSuccess(result);
			}

			@Override
			public void onFailure(Integer result) {
				// TODO Auto-generated method stub
				handler.setFailure(result,null);
			}

		};
		new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// TODO Auto-generated method stub
				SyncEntityServiceImpl.getInstance().syncEntity(id, syncType, newCallback);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});

	}

	@Override
	public void syncEntity(final List<Long> ids, final int syncType, final Long uid,
			final SyncCallBack callback) {
		final AsyncResponseHandler<Object> handler = new AsyncResponseHandler<Object>() {

			@Override
			public void onSuccess(Object content) {
				if (callback != null)
					callback.onSuccess(content);
			}

			@Override
			public void onFailure(Integer content,String resultMsg) {
				if (callback != null)
					callback.onFailure(content);
			}
		};

		final SyncCallBack newCallback = new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				handler.setSuccess(result);
			}

			@Override
			public void onFailure(Integer result) {
				// TODO Auto-generated method stub
				handler.setFailure(result,null);
			}

		};
		new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// TODO Auto-generated method stub
				SyncEntityServiceImpl.getInstance().syncEntity(ids, syncType, uid, newCallback);
				return null;
			}

			@Override
			public void onHandle(Void result) {
			}
		});
	}

}
