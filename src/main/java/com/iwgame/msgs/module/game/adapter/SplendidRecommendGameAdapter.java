/**      
 * RegiestRecommendGameAdapter.java Create on 2014-4-30     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;

/**
 * @ClassName: RegiestRecommendGameAdapter
 * @Description: 注册推荐贴吧适配器
 * @author 王卫
 * @date 2014-4-30 下午4:06:44
 * @Version 1.0
 * 
 */
public class SplendidRecommendGameAdapter extends BaseAdapter {

	private List<Map<String, Object>> mData;
	private final String TAG = "RegiestRecommendGameAdapter";
	private Context mContext;
	private LayoutInflater mInflater;

	/**
	 * 
	 */
	public SplendidRecommendGameAdapter(Context context, List<Map<String, Object>> data) {
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
			convertView = mInflater.inflate(R.layout.splendid_game_regiest_gridview_item, null);
			holder = new ViewHolder();
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			//holder.tag = (ImageView) convertView.findViewById(R.id.tag);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		renderUI(holder, (Map<String, Object>) getItem(position));
		return convertView;
	}

	/**
	 * 
	 * @param holder
	 * @param uvo
	 */
	private void renderUI(ViewHolder holder, Map<String, Object> gvo) {
		if(gvo.containsKey("pic")){
			// 显示头像
			ImageViewUtil.showImage(holder.icon, (String)gvo.get("pic"), R.drawable.common_default_icon);
		}
		
		
		if(gvo.containsKey("title")){
			if(((String)gvo.get("title")).length() > 7){
				holder.desc.setText(((String)gvo.get("title")).substring(0, 7)+ "…");
			}else{
				holder.desc.setText((String)gvo.get("title"));
			}
		}else{
			holder.desc.setText("");
		}
	}

	public List<Map<String, Object>> getData() {
		return mData;
	}

	static public class ViewHolder {
		public ImageView icon;
		public ImageView tag;
		public TextView desc;
	}

}
