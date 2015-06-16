/**      
 * XActionAsyncResponseHandler.java Create on 2013-10-22     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import android.os.Handler;
import android.os.Message;

import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: XActionAsyncResponseHandler
 * @Description: TODO(...)
 * @author Administrator
 * @date 2013-10-22 下午3:30:10
 * @Version 1.0
 * 
 */
public abstract class XActionAsyncResponseHandler extends Handler {
	// CustomProgressDialog dialog = null;

	// public XActionAsyncResponseHandler(CustomProgressDialog dialog) {
	// this.dialog = dialog;
	// }

	public final static int WHAT_SUCCESS = 1;
	public final static int WHAT_FAILURE = 2;

	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case WHAT_SUCCESS:
			onSuccess((XActionResult) msg.obj);
			// try {
			// if (this.dialog != null)
			// dialog.dismiss();
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			break;

		case WHAT_FAILURE:
			// try {
			// if (this.dialog != null)
			// dialog.dismiss();
			// } catch (Exception e) {
			// // TODO: handle exception
			// }
			onFailure((Integer) msg.obj);

			break;
		default:
			break;
		}
	}

	/**
	 * 成功
	 * 
	 * @return
	 */
	public abstract void onSuccess(XActionResult response);

	/**
	 * 失败
	 * 
	 * @param result
	 */
	public abstract void onFailure(Integer response);

}
