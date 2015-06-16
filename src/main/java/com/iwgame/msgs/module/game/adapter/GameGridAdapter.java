/**      
 * GameGridAdapter.java Create on 2014-3-31     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.youban.msgs.R;

/**
 * @ClassName: GameGridAdapter
 * @Description: 贴吧表格适配器
 * @author 王卫
 * @date 2014-3-31 下午5:46:54
 * @Version 1.0
 * 
 */
public class GameGridAdapter extends SimpleAdapter {

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public GameGridAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		Object tag = convertView.getTag();
		final ViewHolder holder;
		if (tag == null) {
			holder = new ViewHolder();
			holder.gameIcon = (ImageView) convertView.findViewById(R.id.gameIcon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) tag;
		}
		Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		return null;
	}

	static class ViewHolder {
		public ImageView gameIcon;
		public TextView postbarCount;
		public TextView gname;
	}

}
