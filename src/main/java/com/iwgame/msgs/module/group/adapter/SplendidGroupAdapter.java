/**      
 * GroupAdapter.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupVo;

/**
 * @ClassName: GroupAdapter
 * @Description: 推荐公会适配器
 * @author 
 * @date 2014-10-24 下午07:41:16
 * @Version 1.0
 * 
 */
public class SplendidGroupAdapter extends BaseAdapter {

	private static final String TAG = "SplendidGroupAdapter";
	
	private Context context;
	private LayoutInflater inflater;
	
	private ArrayList<Map<String, Object>> data;

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public SplendidGroupAdapter(Context context, ArrayList<Map<String, Object>> data) {
		//super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
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
		ViewHolder holder = new ViewHolder();;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.splendid_group_list_item, null);
			holder.gnameTxt = (TextView)convertView.findViewById(R.id.gnameTxt);//公会名称
			holder.groupId = (TextView)convertView.findViewById(R.id.group_id);//公会ID
			holder.avatarView = (ImageView) convertView.findViewById(R.id.icon);//公会头像
			holder.desc1 = (TextView) convertView.findViewById(R.id.desc1);//公会人数
			holder.desc2 = (TextView) convertView.findViewById(R.id.desc2);//会长
			holder.gameIcon = (ImageView) convertView.findViewById(R.id.gameIcon);//贴吧头像
			holder.grade = (TextView) convertView.findViewById(R.id.grade);//公会等级
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Map<String, Object> mapData = (HashMap<String, Object>) getItem(position);
		String title = null;
		if (mapData.containsKey("title")) {
			title = (String) mapData.get("title");
			holder.gnameTxt.setText(title);
		}
		// 显示公会头像
		String avatar = null;
		if (mapData.containsKey("pic")) {
			avatar = (String) mapData.get("pic");
			//公会头像
			ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_default_icon);
		}
		// 贴吧头像
		String gameIcon = null;
		if (mapData.containsKey("logo")) {
			gameIcon = (String) mapData.get("logo");
			//贴吧头像
			ImageViewUtil.showImage(holder.gameIcon, gameIcon, R.drawable.common_default_icon);
		}
		// 公会ID
		long gid;
		if (mapData.containsKey("grId")) {
			gid = Long.valueOf(mapData.get("grId").toString());;
		}
		// 公会等级
		if (mapData.containsKey("grade")) {
			holder.grade.setText("LV"+mapData.get("grade"));
		}else{
			holder.grade.setText("LV0");
		}
		
		// 公会人数
		if (mapData.containsKey("total") && mapData.containsKey("maxCount")) {
			holder.desc1.setText("公会人数：" + mapData.get("total")+"/"+mapData.get("maxCount"));
		}else{
			holder.desc1.setText("公会人数：" + "0/0");
		}
		// 公会会长
		if (mapData.containsKey("presidentName")) {
			holder.desc2.setText("会长：" + mapData.get("presidentName"));
		}else{
			holder.desc2.setText("会长：");
		}
		//公会显示ID
		if(mapData.containsKey("serial")){
			holder.groupId.setText(""+mapData.get("serial"));
		}
		return convertView;
	}

	static class ViewHolder {
		TextView gnameTxt;
		TextView groupId;
		ImageView avatarView;
		ImageView gameIcon;
		TextView desc1;
		TextView desc2;
		TextView grade;
	}

}
