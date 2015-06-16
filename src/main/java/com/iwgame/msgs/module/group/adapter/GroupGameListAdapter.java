/**      
 * GroupGameListAdapter.java Create on 2013-10-29     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.game.adapter.GameAdapter;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;

/**
 * @ClassName: GroupGameListAdapter
 * @Description: 根据贴吧查找公会的贴吧列表适配器
 * @author 王卫
 * @date 2013-10-29 下午02:33:49
 * @Version 1.0
 * 
 */
public class GroupGameListAdapter extends GameAdapter {

	protected static final String TAG = "GroupGameListAdapter";

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public GroupGameListAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
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
		final ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder.desc == null) {
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
		}
		// 根据贴吧获取公会数
		Map<String, Object> mapData = (HashMap<String, Object>) getItem(position);
		final long gid = (Long) mapData.get("gid");
		holder.desc.setTag(String.valueOf(gid));
		if (NetworkUtil.isNetworkAvailable(convertView.getContext())) {
			getGroupCount(holder.desc, gid);
		} else {
			holder.desc.setText("没有相关公会");
		}
		return convertView;
	}

	/**
	 * 显示描述（贴吧相关公会个数）
	 * 
	 * @param tv
	 * @param gid
	 */
	private void getGroupCount(final TextView tv, final long gid) {
		ProxyFactory.getInstance().getGroupProxy().searchGroups(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (tv.getTag().equals(String.valueOf(gid))) {
					if (result > 0)
						tv.setText(result + "个相关公会");
					else {
						tv.setText("没有相关公会");
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (tv.getTag().equals(String.valueOf(gid))) {
					tv.setText("没有相关公会");
				}
				LogUtil.e(TAG, "根据贴吧搜索公会个数失败");
			}
		}, tv.getContext(), String.valueOf(gid), 0, Integer.MAX_VALUE, null);
	}
}
