/**      
 * AsyncResponseHandler.java Create on 2014-1-24     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @ClassName: AsyncResponseHandler
 * @Description: 异步结果处理到主线程中
 * @author chuanglong
 * @date 2014-1-24 下午12:14:56
 * @Version 1.0
 * 
 */
public abstract class AsyncResponseHandler<T> {
	final String TAG = "AsyncResponseHandler";

	private class EHandler extends Handler {
		public EHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_SUCCESS:
				onSuccess((T) msg.obj);
				break;

			case WHAT_FAILURE:
				onFailure((Integer) msg.arg1, (String) msg.obj);
				break;
			default:
				break;
			}
		}
	}

	public Handler mHandler;

	public AsyncResponseHandler() {
		Looper myLooper, mainLooper;
		myLooper = Looper.myLooper();
		mainLooper = Looper.getMainLooper();
		mHandler = new EHandler(mainLooper);
	}

	/**
	 * 成功
	 * 
	 * @return
	 */
	public abstract void onSuccess(T response);

	/**
	 * 失败
	 * 
	 * @param result
	 */
	public abstract void onFailure(Integer response, String msg);

	private final static int WHAT_SUCCESS = 1;
	private final static int WHAT_FAILURE = 2;

	public void setSuccess(T response) {
		android.os.Message msg = mHandler.obtainMessage();// android.os.Message.obtain();
		msg.what = WHAT_SUCCESS;
		msg.obj = response;
		mHandler.sendMessage(msg);
	}

	public void setFailure(Integer response, String responseMsg) {
		android.os.Message msg = mHandler.obtainMessage();// android.os.Message.obtain();
		msg.what = WHAT_FAILURE;
		msg.arg1 = response;
		msg.obj = responseMsg;
		mHandler.sendMessage(msg);
	}

}
