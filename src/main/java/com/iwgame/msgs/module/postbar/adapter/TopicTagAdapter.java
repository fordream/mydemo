/**      
 * RegiestRecommendGameAdapter.java Create on 2014-4-30     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iwgame.msgs.vo.local.TopicTagVo;
import com.youban.msgs.R;

/**
 * @ClassName: TopicTagAdapter
 * @Description: 贴吧标签适配器
 * @author 王卫
 * @date 2015-2-9 下午4:06:44
 * @Version 1.0
 * 
 */
public class TopicTagAdapter extends BaseAdapter {

	private List<TopicTagVo> mData;
	private Context mContext;
	private LayoutInflater mInflater;

	private int clickTemp = -1;

	// 标识选择的Item
	public void setSeclection(int position) {
		clickTemp = position;
	}

	/**
	 * 
	 */
	public TopicTagAdapter(Context context, List<TopicTagVo> data) {
		mData = data;
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (mData != null)
			return mData.size();
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		if (mData != null)
			return mData.get(position);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.topic_tag_gridview_item, null);
			holder = new ViewHolder();
			holder.tagTxt = (TextView) convertView.findViewById(R.id.tagTxt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		TopicTagVo vo = (TopicTagVo) getItem(position);
		holder.tagTxt.setText(vo.getName());
		// 点击改变选中listItem的背景色
		if (clickTemp == position) {
			holder.tagTxt.setBackgroundResource(R.drawable.publish_topic_tag_bg_pre_shap);
		} else {
			holder.tagTxt.setBackgroundResource(R.drawable.publish_topic_tag_edittxt_bg);
		}
		return convertView;
	}

	public List<TopicTagVo> getData() {
		return mData;
	}

	static public class ViewHolder {
		public TextView tagTxt;
	}

}
