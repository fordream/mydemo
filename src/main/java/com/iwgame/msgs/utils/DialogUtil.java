/**      
 * DialogUtil.java Create on 2013-11-2     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.youban.msgs.R;

/**
 * @ClassName: DialogUtil
 * @Description: TODO(...)
 * @author 吴禹青
 * @date 2013-11-2 上午11:34:19
 * @Version 1.0
 * 
 */
public class DialogUtil {
    public interface OKCallBackListener {
	public void execute();

	public void cannel();

    }

    public static Dialog showDialog(Context context, String title, View contentView, final OKCallBackListener listener) {
	return showDialog(context, R.layout.dialog, title, contentView, listener);
    }

    public static Dialog showDialog(Context context, int dialogLayoutResID, String title, View contentView, final OKCallBackListener listener) {

	//view.setPadding(20, 15, 20, 15);
	
	return showDialog(context, dialogLayoutResID, title, contentView,20,15,20,15, listener) ;
    }
    
    public static Dialog showDialog(Context context, int dialogLayoutResID, String title, View contentView,int contentPaddingLeft,int contentPaddingTop,int contentPaddingRight,int contentPaddingBottom, final OKCallBackListener listener) {

   	final Dialog dialog = new Dialog(context, R.style.SampleTheme_Light);
   	// Window window = dialog.getWindow();
   	// WindowManager.LayoutParams lp = window.getAttributes();
   	// // 设置透明度为0.5
   	// lp.alpha = 0.5f;
   	// window.setAttributes(lp);
   	dialog.setCanceledOnTouchOutside(true);
   	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
   	dialog.setContentView(dialogLayoutResID);
   	LinearLayout view = (LinearLayout) dialog.findViewById(R.id.content);
   	view.removeAllViews();
   	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
   	view.setPadding(contentPaddingLeft, contentPaddingTop, contentPaddingRight, contentPaddingBottom);
   	view.addView(contentView, params);

   	TextView titleView = (TextView) dialog.findViewById(R.id.title);
   	titleView.setText(title);
   	// 隐藏下面的有确定和取消按钮的布局
   	if (listener == null) {
   	    dialog.findViewById(R.id.bottom).setVisibility(View.GONE);
   	} else {
   	    // 添加确定按钮功能
   	    Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
   	    commitBtn.setOnClickListener(new View.OnClickListener() {

   		@Override
   		public void onClick(View v) {
   		    // 关闭弹出框，回调
   		    dialog.dismiss();
   		    if (listener != null) {
   			listener.execute();
   		    }
   		}
   	    });
   	    // 添加取消按钮功能
   	    Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
   	    cannelBtn.setOnClickListener(new View.OnClickListener() {

   		@Override
   		public void onClick(View v) {
   		    dialog.dismiss();
   		    if (listener != null) {
   			listener.cannel();
   		    }
   		}
   	    });
   	}

   	dialog.show();
   	return dialog ;
       }

}
