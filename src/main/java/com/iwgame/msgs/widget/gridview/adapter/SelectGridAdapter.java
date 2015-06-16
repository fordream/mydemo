/**      
 * RadioGridAdapter.java Create on 2013-12-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget.gridview.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.widget.gridview.SelectGridView;

/**
 * @ClassName: RadioGridAdapter
 * @Description: 选择组件适配器
 * @author 王卫
 * @date 2013-12-24 下午6:19:38
 * @Version 1.0
 * 
 */
public class SelectGridAdapter extends SimpleAdapter {

	private Context mContext;

	private boolean mIsRadio = true;

	private int selectedPosition = -1;

	private List<Map<String, Object>> mItems;

	private Integer mNumColumns;

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public SelectGridAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, boolean isRadio,
			List<Map<String, Object>> items, Integer numColumns) {
		super(context, data, resource, from, to);
		mContext = context;
		mIsRadio = isRadio;
		mItems = items;
		mNumColumns = numColumns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		if (convertView != null) {
			Map<String, Object> data = (Map<String, Object>) getItem(position);
			if (data.containsKey(SelectGridView.ITEM_DISABLE) && (Boolean) data.get(SelectGridView.ITEM_DISABLE)) {
				convertView.setBackgroundColor(mContext.getResources().getColor(R.color.global_color10));
			} else {

				if (mIsRadio) {
					if (position == selectedPosition) {
						setSelectItemColor(convertView, position);
					} else {
						setUnSelectItemColor(convertView, position);
					}
				} else {
					if (data.containsKey(SelectGridView.ITEM_CHECK) && (Boolean) data.get(SelectGridView.ITEM_CHECK)) {
						setSelectItemColor(convertView, position);
					} else {
						setUnSelectItemColor(convertView, position);
					}
				}
			}
		}
		return convertView;
	}

	/**
	 * 
	 * @param position
	 */
	public void setSelection(int position) {
		selectedPosition = position;
	}

	public int getSelection() {
		return selectedPosition;
	}

	/**
	 * 选中条目颜色
	 * 
	 * @param convertView
	 */
	private void setSelectItemColor(View convertView, int position) {
		convertView.setBackgroundColor(mContext.getResources().getColor(R.color.discover_select_dialog_select_textcolor));
		TextView tv = (TextView) convertView.findViewById(R.id.textView);
		tv.setTextColor(mContext.getResources().getColor(R.color.discover_select_dialog_textview_select_textcolor));
	}

	/**
	 * 未选中条目颜色
	 * 
	 * @param convertView
	 */
	private void setUnSelectItemColor(View convertView, int position) {
		convertView.setBackgroundColor(mContext.getResources().getColor(R.color.discover_select_dialog_unselect_textcolor));
		TextView tv = (TextView) convertView.findViewById(R.id.textView);
		tv.setTextColor(mContext.getResources().getColor(R.color.discover_select_dialog_textview_unselect_textcolor));
	}

}
