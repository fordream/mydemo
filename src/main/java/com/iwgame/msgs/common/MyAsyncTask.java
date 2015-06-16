/**      
 * MyAsyncTask.java Create on 2013-7-31     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.os.AsyncTask;

/**
 * @ClassName: MyAsyncTask
 * @Description: 异步请求类
 * @author 王卫
 * @param <T>
 * @date 2013-7-31 下午04:34:02
 * @Version 1.0
 * 
 */
public class MyAsyncTask<T> extends AsyncTask<Object, Integer, Object> {

	private AsyncCallBack asy;

	private ProxyCallBack<T> mCallback;

	private ProxyCallBack<T> proxyCallback;

	private AsyncResponseHandler<T> handler = new AsyncResponseHandler<T>() {

		@Override
		public void onSuccess(T content) {
			if (mCallback != null)
				mCallback.onSuccess(content);
		}

		@Override
		public void onFailure(Integer content, String resultMsg) {
			if (mCallback != null)
				mCallback.onFailure(content, resultMsg);
		}
	};

	public MyAsyncTask() {
		setCallBack(null);
	}

	public MyAsyncTask(ProxyCallBack<T> callback) {
		setCallBack(callback);
	}

	private void setCallBack(ProxyCallBack<T> callback) {
		mCallback = callback;
		proxyCallback = new ProxyCallBack<T>() {

			@Override
			public void onSuccess(T result) {
				handler.setSuccess(result);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				handler.setFailure(result, resultMsg);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Object doInBackground(Object... params) {
		asy = (AsyncCallBack) params[0];
		return asy.execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		if (asy != null) {
			asy.onHandle(result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	public ProxyCallBack<T> getProxyCallBack() {
		return proxyCallback;
	}

	public void setProxyCallBack(ProxyCallBack<T> asyncCallback) {
		this.proxyCallback = asyncCallback;
	}

}
