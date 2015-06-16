/**      
 * FilterView.java Create on 2015-4-19     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui.filter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youban.msgs.R;

/**
 * @ClassName: FilterView
 * @Description: TODO(...)
 * @author 王卫
 * @date 2015-4-19 下午3:19:01
 * @Version 1.0
 * 
 */
public class FilterView extends LinearLayout {

	public static final int MODE_DISCOVER = 0;
	public static final int MODE_COMMON = 1;
	public int mode = MODE_DISCOVER;

	protected RefreshDataListener listener;

	public LinearLayout filterView;
	private LinearLayout filterListContent;
	public List<TextView> views;

	@SuppressWarnings("unused")
	private LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

	// 弹出筛选框index
	protected int popIndex = -1;
	private boolean popStatus = false;

	@SuppressLint("ClickableViewAccessibility")
	public FilterView(Context context, RefreshDataListener listener, int viewId, int mode) {
		super(context);
		this.listener = listener;
		this.mode = mode;
		View view = View.inflate(context, viewId, null);
		addView(view, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		filterView = (LinearLayout) view.findViewById(R.id.filterView);
		filterListContent = (LinearLayout) findViewById(R.id.filterListContent);
		filterView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean hasChild = false;
				if (filterListContent.getChildCount() > 0)
					hasChild = true;
				addPopView(popIndex, null, null);
				return hasChild;
			}
		});
	}

	/**
	 * 
	 * @param index
	 * @param view
	 */
	protected void addPopView(int index, View view, TextView v) {
		for (int i = 0; i < views.size(); i++) {
			setTxtDrawable(views.get(i), R.drawable.score_down);
			views.get(i).setBackgroundColor(getContext().getResources().getColor(R.color.discover_filter_tab_bg_text));
		}
		if (popIndex == index) {
			filterListContent.removeAllViews();
			filterView.setBackgroundColor(getContext().getResources().getColor(R.color.discover_filter_content_bg_color1));
			if (popStatus) {
				popStatus = false;
			} else {
				if (view != null) {
					ViewGroup parent = (ViewGroup) view.getParent();
					if (parent != null)
						parent.removeAllViews();
					filterListContent.addView(view);
					filterView.setBackgroundColor(getContext().getResources().getColor(R.color.discover_filter_content_bg_color2));
					popStatus = true;
					if (v != null) {
						setTxtDrawable(v, R.drawable.score_up);
						v.setBackgroundColor(getContext().getResources().getColor(R.color.discover_filter_tab_bg_pre_text));
					}
				}
			}
		} else {
			filterListContent.removeAllViews();
			filterView.setBackgroundColor(getContext().getResources().getColor(R.color.discover_filter_content_bg_color1));
			if (view != null) {
				ViewGroup parent = (ViewGroup) view.getParent();
				if (parent != null)
					parent.removeAllViews();
				filterView.setBackgroundColor(getContext().getResources().getColor(R.color.discover_filter_content_bg_color2));
				filterListContent.addView(view);
				popStatus = true;
				if (v != null) {
					setTxtDrawable(v, R.drawable.score_up);
					v.setBackgroundColor(getContext().getResources().getColor(R.color.discover_filter_tab_bg_pre_text));
				}
			}
		}
		popIndex = index;
	}

	/**
	 * 
	 * @param tv
	 * @param res
	 */
	private void setTxtDrawable(TextView tv, int res) {
		Drawable sexdraw = getContext().getResources().getDrawable(res);
		sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
		tv.setCompoundDrawables(null, null, sexdraw, null);
	}

	/**
	 * 
	 * @return
	 */
	protected View creatPullToRefreshListView() {
		return View.inflate(getContext(), R.layout.discover_filter_content_radio_list, null);
	}

	/**
	 * 
	 */
	protected void onRefreshData() {
		listener.onRefreshData();
	}

	/**
	 * 
	 * @return
	 */
	public LinearLayout getFilterListContent() {
		return filterListContent;
	}

	/**
	 * 
	 */
	public void reset() {
		if (views != null && filterListContent != null && filterView != null) {
			for (int i = 0; i < views.size(); i++) {
				setTxtDrawable(views.get(i), R.drawable.score_down);
				views.get(i).setBackgroundColor(getContext().getResources().getColor(R.color.point_detail_bg));
			}
			filterListContent.removeAllViews();
			filterView.setBackgroundColor(getContext().getResources().getColor(R.color.discover_filter_content_bg_color1));
		}
	}
	
	public interface RefreshDataListener {

		void onRefreshData();

		void onRefreshGroup(Boolean verify, String sids);

		void onRefreshUser(Integer gender, Integer time, String sids);
	}

}
