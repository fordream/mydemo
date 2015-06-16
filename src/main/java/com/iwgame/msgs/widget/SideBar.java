/**      
 * SideBar.java Create on 2013-9-2     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.iwgame.utils.DisplayUtil;
import com.youban.msgs.R;

/**
 * @ClassName: SideBar
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-2 上午11:02:29
 * @Version 1.0
 * 
 */
public class SideBar extends View {

	private char[] l;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private TextView mDialogText;
	Bitmap mbitmap;
	private int type = 1;
	private int color = getContext().getResources().getColor(R.color.sidebar_color);

	/**
	 * @param context
	 */
	public SideBar(Context context) {
		super(context);
		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {

		l = new char[] { '!', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
				'Y', 'Z', '#' };
		// mbitmap = BitmapFactory.decodeResource(getResources(),
		// R.drawable.scroll_bar_search_icon);
	}

	public void setListView(ListView _list, BaseAdapter adapter) {
		list = _list;
		sectionIndexter = (SectionIndexer) adapter;

	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public boolean onTouchEvent(MotionEvent event) {

		super.onTouchEvent(event);
		try {

			int i = (int) event.getY();

			int idx = i / (getMeasuredHeight() / l.length);
			if (idx >= l.length) {
				idx = l.length - 1;
			} else if (idx < 0) {
				idx = 0;
			}
			if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
				// setBackgroundResource(R.drawable.common_search_bg_shap);
				if (mDialogText != null) {
					mDialogText.setVisibility(View.VISIBLE);
					if (idx == 0) {
						mDialogText.setText("Search");
						mDialogText.setTextSize(16);
					} else {
						mDialogText.setText(String.valueOf(l[idx]));
						mDialogText.setTextSize(34);
					}
				}
				int position = -1;
				if (sectionIndexter != null) {
					position = sectionIndexter.getPositionForSection(l[idx]);
				}
//				if (sectionIndexter == null) {
//					sectionIndexter = (SectionIndexer) list.getAdapter();
//				} else {
//					position = sectionIndexter.getPositionForSection(l[idx]);
//				}
				if (position == -1) {
					return true;
				}
				list.setSelection(position);
			} else {
				// setBackgroundResource(0);
				if (mDialogText != null)
					mDialogText.setVisibility(View.INVISIBLE);

			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				// setBackgroundDrawable(new ColorDrawable(0x00000000));
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setTextSize(DisplayUtil.sp2px(getContext(), 11));
		paint.setStyle(Style.FILL);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		if (l.length > 0) {
			float height = getMeasuredHeight() / l.length;
			for (int i = 0; i < l.length; i++) {
				// if (i == 0 && type != 2) {
				// canvas.drawBitmap(mbitmap, widthCenter - 7, (i + 1)
				// * height - height / 2, paint);
				// } else
				canvas.drawText(String.valueOf(l[i]), widthCenter, (i + 1) * height, paint);
			}
		}
		this.invalidate();
		super.onDraw(canvas);
	}
}
