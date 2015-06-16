/**      
 * BaseRadioFilterAdapter.java Create on 2015-4-17     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iwgame.msgs.module.discover.ui.filter.vo.FilterVo;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.youban.msgs.R;

/**
 * @ClassName: BaseRadioFilterAdapter
 * @Description: TODO(...)
 * @author 王卫
 * @date 2015-4-17 下午2:27:49
 * @Version 1.0
 * 
 */
public class BaseRadioIconFilterAdapter<T> extends FilterAdapter<T> {

	/**
	 * 
	 */
	public BaseRadioIconFilterAdapter(Context context, List<T> data) {
		super(context, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.discover.adapter.FilterAdapter#getView(int,
	 * android.view.View, android.view.ViewGroup)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RadioViewHolder holder;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.discover_filter_radio_icon_item, null);
			holder = new RadioViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (RadioViewHolder) convertView.getTag();
		}
		// 设置值
		if (holder != null) {
			FilterVo filter = (FilterVo) data.get(position);
			holder.title.setText(filter.name);
			if(filter.showIcon){
				if (!TextUtils.isEmpty(filter.icon)) {
					ImageViewUtil.showImage(holder.icon, filter.icon, R.drawable.common_default_icon);
				}
			}else{
				holder.icon.setVisibility(View.GONE);
			}
			if (filter.selected) {
				convertView.setBackgroundResource(R.color.discover_filter_item_bg_text_pre);
			} else {
				convertView.setBackgroundResource(R.color.discover_filter_item_bg_text);
			}
		}
		return convertView;
	}

	public interface SumbitListener<T> {
		/** 单选条目回调 **/
		public void onRadio(T item);
	}

	protected class RadioViewHolder {
		ImageView icon;
		TextView title;
	}

}