/**      
 * GroupAdapter.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.youban.msgs.R;

/**
 * @ClassName: SplendidTopicAdapter
 * @Description: 推荐帖子适配器
 * @author 
 * @date 2014-10-24 下午07:41:16
 * @Version 1.0
 * 
 */
public class SplendidTopicAdapter extends SimpleAdapter {

	private static final String TAG = "SplendidTopicAdapter";

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public SplendidTopicAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		Object tag = convertView.getTag();
		final ViewHolder holder;
		if (tag == null) {
			holder = new ViewHolder();
			holder.splendid_topic_title = (TextView)convertView.findViewById(R.id.splendid_topic_title);//帖子详情
			holder.splendid_topic_icon = (ImageView) convertView.findViewById(R.id.splendid_topic_icon);//帖子头像
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) tag;
		}

		Map<String, Object> mapData = (HashMap<String, Object>) getItem(position);
		// 显示帖子头像
		String title = null;
		if (mapData.containsKey("title")) {
			title = (String) mapData.get("title");
			holder.splendid_topic_title.setText(title);
		}
		// 帖子头像
		String pic = null;
		if (mapData.containsKey("pic")) {
			pic = (String) mapData.get("pic");
			if(pic.equals("")){
				holder.splendid_topic_icon.setVisibility(View.INVISIBLE);
			}else{
				holder.splendid_topic_icon.setVisibility(View.VISIBLE);
				//帖子头像
				ImageViewUtil.showImage(holder.splendid_topic_icon, pic, R.drawable.common_default_icon);
			}
		}else{
			holder.splendid_topic_icon.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	static class ViewHolder {
		TextView splendid_topic_title;
		ImageView splendid_topic_icon;
	}

}
