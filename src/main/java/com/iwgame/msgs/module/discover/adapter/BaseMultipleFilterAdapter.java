/**      
 * BaseMultipleFilterAdapter.java Create on 2015-4-17     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.module.discover.ui.filter.vo.FilterVo;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.youban.msgs.R;

/**
 * @ClassName: BaseMultipleFilterAdapter
 * @Description: TODO(...)
 * @author 王卫
 * @date 2015-4-17 下午3:07:11
 * @Version 1.0
 * 
 */
public class BaseMultipleFilterAdapter<T> extends FilterAdapter<T> {

	private FilterVo allfilter;

	/**
	 * @param context
	 * @param data
	 * @param listener
	 */
	public BaseMultipleFilterAdapter(Context context, List<T> data) {
		super(context, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.discover.adapter.FilterAdapter#getView(int,
	 * android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MultipleViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.discover_filter_multiple_item, null);
			holder = new MultipleViewHolder();
			holder.itemView = (LinearLayout) convertView.findViewById(R.id.itemView);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.tick = (ImageView) convertView.findViewById(R.id.tick);
			convertView.setTag(holder);
		} else {
			holder = (MultipleViewHolder) convertView.getTag();
		}
		// 设置值
		if (holder != null) {
			final FilterVo filter = (FilterVo) data.get(position);
			if (position == 0) {
				allfilter = filter;
			}
			
			holder.title.setText(filter.name);
			if (filter.showIcon) {
				holder.icon.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(filter.icon)) {
					ImageViewUtil.showImage(holder.icon, filter.icon, R.drawable.common_default_icon);
				}
			} else {
				holder.icon.setVisibility(View.GONE);
			}

			if (filter.selected) {
				holder.tick.setBackgroundResource(R.drawable.common_checkbox_pre);
			} else {
				holder.tick.setBackgroundResource(R.drawable.common_checkbox_nor);
			}
			holder.itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					filter.selected = !filter.selected;
					if (filter.selected) {
						holder.tick.setBackgroundResource(R.drawable.common_checkbox_pre);
					} else {
						holder.tick.setBackgroundResource(R.drawable.common_checkbox_nor);
					}
					clickItem(filter);
				}
			});
		}
		return convertView;
	}

	/**
	 * 
	 */
	private void clickItem(FilterVo filter) {
		if (filter.equals(allfilter)) {
			boolean selected = false;
			if (allfilter.selected) {
				selected = true;
			} else {
				selected = false;
			}
			int size = data.size();
			for (int i = 0; i < size; i++) {
				((FilterVo) data.get(i)).selected = selected;
			}
		} else {
			if (filter.selected) {
				boolean allselected = true;
				int size = data.size();
				for (int i = 0; i < size; i++) {
					FilterVo fvo = (FilterVo) data.get(i);
					if (!fvo.equals(allfilter) && !fvo.selected) {
						allselected = false;
						return;
					}
				}
				allfilter.selected = allselected;
			} else {
				allfilter.selected = false;
			}
		}
		this.notifyDataSetChanged();
	}

	protected class MultipleViewHolder {
		LinearLayout itemView;
		ImageView icon;
		TextView title;
		ImageView tick;
	}

}
