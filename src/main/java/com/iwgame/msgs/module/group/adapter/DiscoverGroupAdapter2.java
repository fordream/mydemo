/**      
 * GroupAdapter.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.youban.msgs.R;
import com.iwgame.msgs.common.ClearCacheListener;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.group.ui.GroupManageActivity;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.UserVo;

/**
 * @ClassName: GroupAdapter
 * @Description: 公会适配器
 * @author 王卫
 * @date 2013-10-24 下午07:41:16
 * @Version 1.0
 * 
 */
public class DiscoverGroupAdapter2 extends SimpleAdapter implements ClearCacheListener{

	private static final String TAG = "GroupAdapter";

	// 搜索贴吧公会
	public static int MODE_GAME_GROUP = 1;
	// 公会名称搜索公会
	public static int MODE_GROUP_NAMEID = 2;
	// 推荐公会
	public static int MODE_RECOMMEND_GROUP = 3;
	// 我的公会
	public static int MODE_MY_GROUP = 4;
	// 我的公会
	public static int MODE_USER_GROUP = 5;

	private int mode;
    private Map<Long, GameVo> gamevo_cache = new HashMap<Long, GameVo>();
    private Map<Long, GroupVo> groupVo_cache = new HashMap<Long, GroupVo>();
	UserVo userVo;
	private int VIEW_TYPE = 2;
  	final private int TYPE_DETAIL = 0;
	final private int TYPE_NODATA = 1;
  	private int noDataHeight;//用于在发现页面当没有获取到数据的时候，显示默认图片的高度
	
  	public int getNoDataHeight() {
		return noDataHeight;
	}

	public void setNoDataHeight(int noDataHeight) {
		this.noDataHeight = noDataHeight;
	}

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public DiscoverGroupAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		userVo = SystemContext.getInstance().getExtUserVo();
	}

	public DiscoverGroupAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int mode) {
		super(context, data, resource, from, to);
		this.mode = mode;
		userVo = SystemContext.getInstance().getExtUserVo();
	}


	/**
	 * 获取到每一种数据的类型
	 */
	@Override
	public int getItemViewType(int position) {
		Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		if(map.size() <= 0){
			return TYPE_NODATA;
		}else{
			return TYPE_DETAIL;
		}
	}
	
	
	/**
	 * 返回了适配器里面有几种类型
	 */
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE;
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
		int type = getItemViewType(position);
		ViewHolder holder1 = new ViewHolder();
		NodateViewHolder nodataHolder = new NodateViewHolder();
		if (convertView == null) {
			switch (type) {
			case TYPE_DETAIL:
				convertView = View.inflate(SystemContext.getInstance().getContext(), R.layout.group_list_item_userfragment, null);
				holder1.groupId = (TextView)convertView.findViewById(R.id.group_id);
				holder1.avatarView = (ImageView) convertView.findViewById(R.id.icon);
				holder1.desc1 = (TextView) convertView.findViewById(R.id.desc1);
				holder1.desc2 = (TextView) convertView.findViewById(R.id.desc2);
				holder1.labelView = (ImageView) convertView.findViewById(R.id.labelView);
				holder1.gameIcon = (ImageView) convertView.findViewById(R.id.gameIcon);
				holder1.manageTxt = (TextView) convertView.findViewById(R.id.manageTxt);
				holder1.manageView = (ImageView) convertView.findViewById(R.id.manageView);
				holder1.rightView = (LinearLayout) convertView.findViewById(R.id.rightViewparent);
				holder1.grade = (TextView) convertView.findViewById(R.id.grade);
				holder1.gName = (TextView)convertView.findViewById(R.id.gnameTxt);
				convertView.setTag(holder1);
				break;

			case TYPE_NODATA:
				LinearLayout v = (LinearLayout)View.inflate(SystemContext.getInstance().getContext(), R.layout.no_data_view_discover, null);
				convertView = v;
				nodataHolder.bg_layout = v;
				nodataHolder.bgIcon = (ImageView)convertView.findViewById(R.id.bgIcon);
				convertView.setTag(nodataHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_DETAIL:
				holder1 = (ViewHolder)convertView.getTag();
				break;

			case TYPE_NODATA:
				nodataHolder = (NodateViewHolder)convertView.getTag();
				break;
			}
		}
		final ViewHolder holder = holder1;
		final NodateViewHolder nodateViewHolder = nodataHolder;
		if(type == TYPE_DETAIL){
			Map<String, Object> mapData = (HashMap<String, Object>) getItem(position);
			// 显示公会头像
			String avatar = null;
			if (mapData.containsKey("avatar")) {
				avatar = (String) mapData.get("avatar");
			}
			String gameIcon = null;
			if (mapData.containsKey("gameIcon")) {
				gameIcon = (String) mapData.get("gameIcon");
			}
			
			if(mapData.containsKey("desc1")){
				holder1.desc1.setText((String)mapData.get("desc1"));
			}
			
			if(mapData.containsKey("desc2")){
				holder.desc2.setText((String)mapData.get("desc2"));
			}
			
			String gName = null;
			if(mapData.containsKey("name")){
				gName = (String)mapData.get("name");
				holder.gName.setText(gName);
			}
			Long gid = null;
			if (mapData.containsKey("gid")) {
				gid = (Long) mapData.get("gid");
			}
			if (mapData.containsKey("grade")) {
				holder.grade.setText("LV"+mapData.get("grade"));
			}else{
				holder.grade.setText("LV0");
			}
			if(gamevo_cache.containsKey(gid)){
				ImageViewUtil.showImage(holder.gameIcon,gamevo_cache.get(gid).getGamelogo(), R.drawable.common_default_icon);
			}else{
				showGameLabel(holder.gameIcon, gameIcon, gid);
			}
			ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_default_icon);
			// 判断是否是群主还是管理员
		    final Long grid = (Long) mapData.get("grid");
			if(mapData.containsKey("serial"))
			   holder.groupId.setText(""+mapData.get("serial"));
		
			if(mapData.containsKey("relwithgroup")){
				int rel = (Integer)mapData.get("relwithgroup");
				addLabelView(convertView, grid, holder.labelView, holder.manageView, holder.manageTxt,rel);
			}else{
			    GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
				GroupVo groupVo = groupDao.findGroupByGrid(grid);
				if(groupVo == null)
					addLabelView(convertView, grid, holder.labelView, holder.manageView, holder.manageTxt,0);
				else
					addLabelView(convertView, grid, holder.labelView, holder.manageView, holder.manageTxt,groupVo.getRelWithGroup());
			}
		}else if(type == TYPE_NODATA){
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, noDataHeight);
			nodateViewHolder.bg_layout.setLayoutParams(params);
			nodateViewHolder.bgIcon.setBackgroundResource(R.drawable.common_no_seach_group);
		}
		return convertView;
	}

	/**
	 * 
	 * @param view
	 * @param gid
	 */
	private void showGameLabel(final ImageView view, final String gameIcon,final Long gid) {
		if (gid != null) {
			ProxyFactory.getInstance().getGameProxy().getGameInfo(new ProxyCallBack<GameVo>() {

				@Override
				public void onSuccess(GameVo result) {
					if (result != null){
						gamevo_cache.put(gid, result);
						ImageViewUtil.showImage(view, result.getGamelogo(), R.drawable.common_default_icon);
					}
					else
						ImageViewUtil.showImage(view, gameIcon, R.drawable.common_default_icon);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					ImageViewUtil.showImage(view, gameIcon, R.drawable.common_default_icon);
				}
			}, view.getContext(), gid);
		} else {
			ImageViewUtil.showImage(view, gameIcon, R.drawable.common_default_icon);
		}
	}

	/**
	 * 添加身份标签
	 * 
	 * @param convertView
	 * @param grid
	 * @param uid
	 */
	private void addLabelView(View convertView, long grid, ImageView labelView, ImageView manageView, TextView manageTxt,int rel) {
		labelView.setVisibility(View.GONE);
		manageView.setVisibility(View.GONE);
		manageTxt.setVisibility(View.GONE);
		if (rel == 0) {// 没有关系
			labelView.setVisibility(View.GONE);
			manageView.setVisibility(View.GONE);
			manageTxt.setVisibility(View.GONE);
		} else if (rel == 1) {// 普通会员
			if (mode == MODE_MY_GROUP) {
				labelView.setVisibility(View.GONE);
				manageView.setVisibility(View.GONE);
				manageTxt.setVisibility(View.GONE);
			} else {
				labelView.setVisibility(View.VISIBLE);
				labelView.setBackgroundResource(R.drawable.group_join_tag);
				manageView.setVisibility(View.GONE);
				manageTxt.setVisibility(View.GONE);
			}
		} else if (rel == 2) {// 管理员
			labelView.setVisibility(View.VISIBLE);
			labelView.setBackgroundResource(R.drawable.group_manager_tag);
			manageView.setVisibility(View.VISIBLE);
			manageTxt.setVisibility(View.VISIBLE);
		} else if (rel == 3) {// 会长
			labelView.setVisibility(View.VISIBLE);
			labelView.setBackgroundResource(R.drawable.group_president_tag);
			manageView.setVisibility(View.VISIBLE);
			manageTxt.setVisibility(View.VISIBLE);
		}
		setManageEvent(manageView, grid, rel);
		setManageEvent(labelView, grid, rel);
	}


	/**
	 * 管理按钮功能
	 * 
	 * @param manageView
	 * @param grid
	 * @param rel
	 */
	private void setManageEvent(final ImageView manageView, final long grid, final int rel) {
		manageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(manageView.getContext(), GroupManageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_REL, rel);
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				manageView.getContext().startActivity(intent);
			}
		});
	}

	/**
	 * 添加复选框
	 * 
	 * @param convertView
	 * @param rightView
	 * @param mapData
	 */
	private void addCheckBoxView(View convertView, LinearLayout rightView, final Map<String, Object> mapData) {
		// 添加复选框
		rightView.removeAllViews();
		LinearLayout boxLayout = (LinearLayout) View.inflate(convertView.getContext(), R.layout.common_checkbox, null);
		final CheckBox cbox = (CheckBox) boxLayout.findViewById(R.id.checkBox);
		rightView.addView(boxLayout);
		cbox.setChecked((Boolean) mapData.get("isChecked"));
		cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mapData != null) {
					mapData.put("isChecked", isChecked);
				}
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cbox != null) {
					if (cbox.isChecked()) {
						cbox.setChecked(false);
						mapData.put("isChecked", false);
					} else {
						cbox.setChecked(true);
						mapData.put("isChecked", true);
					}
				}
			}
		});
	}

	static class ViewHolder {
		TextView groupId;
		ImageView avatarView;
		ImageView manageView;
		TextView manageTxt;
		ImageView labelView;
		ImageView gameIcon;
		LinearLayout rightView;
		TextView desc1;
		TextView desc2;
		TextView grade;
		TextView gName;
	}

	/**
	 * 没有数据的时候的viewholder
	 * @author jczhang
	 *
	 */
	static class NodateViewHolder {
		LinearLayout bg_layout;
		ImageView bgIcon;
	}
	
	/**
	 * 清除缓存的方法
	 */
	@Override
	public void clearCache() {
		gamevo_cache.clear();
		groupVo_cache.clear();
	}

}
