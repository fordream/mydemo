/**      
 * SyncListTransformServiceImpl.java Create on 2014-1-26     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.AsyncResponseHandler;
import com.iwgame.msgs.common.MyAsyncTask;

/**
 * @ClassName: AsyncSyncListServiceImpl
 * @Description: TODO(...)
 * @author chuanglong
 * @date 2014-1-26 上午9:04:46
 * @Version 1.0
 * 
 */
public class AsyncSyncListServiceImpl implements SyncListService {

	private static byte[] lock = new byte[0];

	private static AsyncSyncListServiceImpl instance = null;

	private AsyncSyncListServiceImpl() {
	}

	public static AsyncSyncListServiceImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new AsyncSyncListServiceImpl();
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
	public void syncList(final int syncType, final Long id, final SyncCallBack callback) {
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
				handler.setSuccess(result);
			}

			@Override
			public void onFailure(Integer result) {
				handler.setFailure(result,null);
			}

		};
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				SyncListServiceImpl.getInstance().syncList(syncType, id, newCallback);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.sync.SyncListService#syncList(int,
	 * com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void syncList(final int syncType, final SyncCallBack callback) {
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
				handler.setSuccess(result);
			}

			@Override
			public void onFailure(Integer result) {
				handler.setFailure(result,null);
			}

		};
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				SyncListServiceImpl.getInstance().syncList(syncType, newCallback);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});

	}
	
	/**
	 * 同步公会的成员
	 */
	@Override
	public void syncList(final int syncType,final Long id,final Long utime,final SyncCallBack callback) {
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
				handler.setSuccess(result);
			}

			@Override
			public void onFailure(Integer result) {
				handler.setFailure(result,null);
			}

		};
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				SyncListServiceImpl.getInstance().syncList(syncType, id, utime, callback);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});

	
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.sync.SyncListService#syncList(int, long, com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void syncListByUtime(final int syncType, final Long utime, final SyncCallBack callBack) {
		final AsyncResponseHandler<Object> handler = new AsyncResponseHandler<Object>() {

			@Override
			public void onSuccess(Object content) {
				if (callBack != null)
					callBack.onSuccess(content);
			}

			@Override
			public void onFailure(Integer content,String resultMsg) {
				if (callBack != null)
					callBack.onFailure(content);
			}
		};

		final SyncCallBack newCallback = new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				handler.setSuccess(result);
			}

			@Override
			public void onFailure(Integer result) {
				handler.setFailure(result,null);
			}

		};
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				SyncListServiceImpl.getInstance().syncListByUtime(syncType, utime, newCallback);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});
	}
}
