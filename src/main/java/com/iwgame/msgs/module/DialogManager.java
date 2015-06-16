/**      
 * DialogManager.java Create on 2013-12-4     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module;

import com.iwgame.msgs.widget.picker.CustomProgressDialog;

/**
 * @ClassName: DialogManager
 * @Description: 加载loading对话框管理
 * @author 王卫
 * @date 2013-12-4 下午8:06:33
 * @Version 1.0
 * 
 */
public class DialogManager {

	private static final String TAG = "DialogManager";

	private static byte[] lock = new byte[0];

	private static DialogManager instance = null;

	private CustomProgressDialog dialog = null;

	private DialogManager() {

	}

	public static DialogManager getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new DialogManager();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/**
	 * 显示
	 * 
	 * @param context
	 */
//	public void show(Context context) {
//		try {
//			if (dialog == null) {
//				dialog = CustomProgressDialog.createDialog(context);
//			}
//			dialog.show();
//			ImageView imageView = (ImageView) dialog.findViewById(R.id.loadingImageView);
//			AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
//			animationDrawable.start();
//		} catch (Exception e) {
//			LogUtil.e(TAG, "消失进度对话框错误");
//		}
//	}

	/**
	 * 不显示
	 */
//	public void dismiss() {
//		try {
//			if (dialog != null) {
//				dialog.dismiss();
//			}
//		} catch (Exception e) {
//			LogUtil.e(TAG, "消失进度对话框错误");
//		}
//	}
}
