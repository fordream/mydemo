/**      
 * CustomProgressDialog.java Create on 2013-6-13     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget.picker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.youban.msgs.R;

/**
 * @ClassName: CustomProgressDialog
 * @Description: 加载进度
 * @author 王卫
 * @date 2013-6-13 下午3:15:25
 * @Version 1.0
 * 
 */
public class CustomProgressDialog extends Dialog {

	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;

	public CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static CustomProgressDialog createDialog(Context context) {
		if (context != null) {
			customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
			customProgressDialog.setContentView(R.layout.customprogressdialog);
			customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			customProgressDialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		}
		return customProgressDialog;
	}

	public static CustomProgressDialog createDialog(Context context, boolean canceldOnTiuchOutside, final boolean canceldOnBackKeyDown) {
		if (context != null) {
			customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog){
				@Override
				public boolean onKeyDown(int keyCode, KeyEvent event) {
					if(!canceldOnBackKeyDown){
						return true;
					}
					return super.onKeyDown(keyCode, event);
				}
			};
			customProgressDialog.setContentView(R.layout.customprogressdialog);
			customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			customProgressDialog.setCanceledOnTouchOutside(canceldOnTiuchOutside);
		}
		return customProgressDialog;
	}
	
	public static CustomProgressDialog createDialog(Context context, boolean canceldOnTiuchOutside) {
		if (context != null) {
			customProgressDialog = new CustomProgressDialog(context, R.style.CustomProgressDialog);
			customProgressDialog.setContentView(R.layout.customprogressdialog);
			customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
			customProgressDialog.setCanceledOnTouchOutside(canceldOnTiuchOutside);
		}
		return customProgressDialog;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (customProgressDialog == null) {
			return;
		}

		try {
			ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
			animationDrawable.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#show()
	 */
	@Override
	public void show() {
		try {
			super.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Dialog#dismiss()
	 */
	@Override
	public void dismiss() {
		try {
			super.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
