/**      
 * MsgsPopWindow.java Create on 2013-12-15     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget.popwindow;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.youban.msgs.R;
import com.iwgame.msgs.module.chat.adapter.PopWindowAdapter;
import com.iwgame.utils.DisplayUtil;

/**
 * @ClassName: MsgsPopWindow
 * @Description: 弹出菜单组件
 * @author 王卫
 * @date 2013-12-13 下午2:32:37
 * @Version 1.0
 * 
 */
public class MsgsPopWindow extends PopupWindow implements OnClickListener, OnKeyListener {

	// 条目数组
	private List<View> mItems;
	// 点击条目监听
	private OnClickPopItemListener mOnClickPopItemListener;

	// context
	private Context context;

	// 相对显示的view
	private View anchor;

	// x偏移量
	private int xoff;

	// y偏移量
	private int yoff;

	WindowManager mWindowManager;

	/**
	 * 
	 * @param context
	 * @param items
	 * @param anchor
	 * @param xoff
	 * @param yoff
	 */
	public MsgsPopWindow(Context context, List<View> items, View anchor, int xoff, int yoff) {
		this(context, anchor, xoff, yoff, 0);
		this.mItems = items;
		setContentItems(items);
	}

	public MsgsPopWindow(Context context, List<View> items, View anchor, int xoff, int yoff, int bgResid) {
		this(context, anchor, xoff, yoff, bgResid);
		this.mItems = items;
		setContentItems(items);
	}

	/**
	 * 
	 * @param context
	 * @param anchor
	 * @param xoff
	 * @param yoff
	 */
	public MsgsPopWindow(Context context, View anchor, int xoff, int yoff) {
		this(context, anchor, xoff, yoff, 0);
	}

	/**
	 * 
	 * @param context
	 * @param anchor
	 * @param xoff
	 * @param yoff
	 * @param bgResid
	 */
	public MsgsPopWindow(Context context, View anchor, int xoff, int yoff, int bgResid) {

		super(View.inflate(context, R.layout.common_popwindow_menu_view, null), LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		this.context = context;
		this.anchor = anchor;
		this.xoff = xoff;
		this.yoff = yoff;

		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.setOutsideTouchable(false);
		this.setTouchable(true);
		this.setFocusable(true);
		this.showAsDropDown(anchor, xoff, yoff);
		this.getContentView().setOnKeyListener(this);
		if (bgResid > 0)
			this.getContentView().setBackgroundResource(bgResid);
	}


	/**
	 * 
	 * @param context
	 * @param anchor
	 * @param xoff
	 * @param yoff
	 * @param bgResid
	 */
	public MsgsPopWindow(Context context,int width, View anchor, int xoff, int yoff, int bgResid) {

		super(View.inflate(context, R.layout.common_popwindow_menu_view, null), width, LayoutParams.WRAP_CONTENT, true);

		this.context = context;
		this.anchor = anchor;
		this.xoff = xoff;
		this.yoff = yoff;

		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		this.setOutsideTouchable(false);
		this.setTouchable(true);
		this.setFocusable(true);
		this.showAsDropDown(anchor, xoff, yoff);
		this.getContentView().setOnKeyListener(this);
		if (bgResid > 0)
			this.getContentView().setBackgroundResource(bgResid);
	}
	
	/**
	 * 设置内容的items
	 * 
	 * @param list
	 */
	public void setContentItems(List<View> list) {
		LinearLayout contentView = ((LinearLayout) getContentView());
		contentView.removeAllViews();
		for (int i = 0; i < list.size(); i++) {
			View view = list.get(i);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			contentView.addView(view, params);
			if (i != list.size() - 1) {
				ImageView iv = new ImageView(context);
				iv.setBackgroundColor(context.getResources().getColor(R.color.common_menu_dialog_fengexian));
				LayoutParams divparams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
				contentView.addView(iv, divparams);
			}
		}

		int[] location = new int[2];
		anchor.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标
		int anchorHeight = anchor.getMeasuredHeight();
		int anchorWidth = anchor.getMeasuredWidth();
		int displayHeight = mWindowManager.getDefaultDisplay().getHeight();
		int displayWidth = mWindowManager.getDefaultDisplay().getWidth();
		this.getContentView().measure(this.getWidth(), this.getHeight());
		int pwheight = this.getContentView().getMeasuredHeight();
		int pwwidth = this.getContentView().getMeasuredWidth();

		int real_xoff = 0;
		int real_yoff = 0;
		if (location[1] + anchorHeight + pwheight + yoff > displayHeight) {

			real_yoff = -(pwheight + anchorHeight);

		} else {
			// this.update();
			real_yoff = yoff;
		}
		int padding = DisplayUtil.dip2px(context, 10);
		if (location[0] + xoff + pwwidth > displayWidth - padding) {
			// 右边出了限制
			real_xoff = displayWidth - padding - pwwidth - location[0];
		} else if (location[0] + xoff < padding) {
			// 左边出就边界限制
			real_xoff = padding - location[0];
		} else {
			real_xoff = xoff;
		}

		// if()
		// this.u
		this.update(anchor, real_xoff, real_yoff, -1, -1);
	}

	/**
	 * 
	 * @param onClickPopItemListener
	 */
	public void setOnClickPopItemListener(OnClickPopItemListener onClickPopItemListener) {
		this.mOnClickPopItemListener = onClickPopItemListener;
		LinearLayout contentView = ((LinearLayout) getContentView());
		for (int i = 0; i < contentView.getChildCount(); i++) {
			contentView.getChildAt(i).setOnClickListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnKeyListener#onKey(android.view.View, int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
			case KeyEvent.KEYCODE_MENU:
				MsgsPopWindow.this.dismiss();
				break;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		this.dismiss();
		if (mOnClickPopItemListener != null) {
			mOnClickPopItemListener.onClickPopItem(v);
		}
	}

	/**
	 * 
	 * @ClassName: OnClickPopItemListener
	 * @Description: 点击条目监听接口
	 * @author 王卫
	 * @date 2013-12-13 下午3:24:30
	 * @Version 1.0
	 * 
	 */
	public interface OnClickPopItemListener {
		public void onClickPopItem(View v);
	}

}
