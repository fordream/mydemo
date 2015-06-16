/**      
 * SlideItemListView.java Create on 2014-9-10     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.widget.slideitemlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * @ClassName: SlideItemListView
 * @Description: TODO(具有item滑动的listview)
 * @author chuanglong
 * @date 2014-9-10 下午5:04:02
 * @Version 1.0
 * 
 */
public class SlideItemListView extends ListView {

	private static final String TAG = "SlideItemListView";

	private SlideItemView mFocusedItemView;

	public SlideItemListView(Context context) {
		super(context);
	}

	public SlideItemListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlideItemListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void shrinkListItem(int position) {
		View item = getChildAt(position);

		if (item != null) {
			try {
				((SlideItemView) item).shrink();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			int x = (int) event.getX();
			int y = (int) event.getY();
			int position = pointToPosition(x, y);
			Log.d(TAG, "postion=" + position);
			if (position != INVALID_POSITION) {
				View topItem = getChildAt(0);
				int topIndex = 0;
				if (topItem != null) {
					topIndex = ((SlideItemView) topItem).getmIndex();
				}
				position -= topIndex;
				View item = getChildAt(position);
				if (item != null) {
					try {
						mFocusedItemView = ((SlideItemView) item);
					} catch (ClassCastException e) {
						e.printStackTrace();
					}
				} else {
					mFocusedItemView = null;
				}
				Log.d(TAG, "FocusedItemView=" + mFocusedItemView);
			} else {
				mFocusedItemView = null;
			}
		}
		default:
			break;
		}

		if (mFocusedItemView != null) {
			mFocusedItemView.onRequireTouchEvent(event);
		}
		try {
			return super.onTouchEvent(event);
		} catch (Exception e) {
			return true;
		}
	}
}
