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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iwgame.msgs.common.ClearCacheListener;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.group.ui.GroupManageActivity;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GroupAdapter
 * @Description: 公会适配器
 * @author 王卫
 * @date 2013-10-24 下午07:41:16
 * @Version 1.0
 * 
 */
public class GroupAdapter2 extends SimpleAdapter implements ClearCacheListener {

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
	UserVo userVo;

	private ListView listView;
	private boolean flag = true;// 用来标记刚进来的时候，是否执行加载数据的方法
	private Map<Long, Boolean> image_cache = new HashMap<Long, Boolean>();// 用来标记图片是否已经加载
																			// 了
	private Map<Long, ViewHolder> viewholder_cache = new HashMap<Long, GroupAdapter2.ViewHolder>();
	private Map<Long, View> convertView_cache = new HashMap<Long, View>();// 保存的是view的对象的集合
	private Map<Long, Boolean> entity_cache = new HashMap<Long, Boolean>();// 判断公会的实体是否已经同步

	private LayoutInflater mInflater;
	private int mResource;

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public GroupAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, ListView listView) {
		super(context, data, resource, from, to);
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		this.mResource = resource;
		addListener();
		this.mInflater = LayoutInflater.from(context);

	}

	/**
	 * 给listview添加了监听器
	 */
	private void addListener() {
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					flag = true;
					int firstvisibleIndex = listView.getFirstVisiblePosition();
					int lastvisibleIndex = listView.getLastVisiblePosition();
					if (firstvisibleIndex > 0)
						firstvisibleIndex = firstvisibleIndex - 1;
					ViewHolder holder;
					Map<String, Object> mapData;
					long gid;
					long grid;
					String gameIcon;
					String avatar;
					View convertView;
					try {
						for (int i = firstvisibleIndex; i < lastvisibleIndex; i++) {
							mapData = (HashMap<String, Object>) getItem(i);
							gid = (Long) mapData.get("gid");
							grid = (Long) mapData.get("grid");
							holder = viewholder_cache.get(grid);
							gameIcon = (String) mapData.get("gameIcon");
							avatar = (String) mapData.get("avatar");
							convertView = convertView_cache.get(grid);
							if (mode != MODE_MY_GROUP) {
								if (!gamevo_cache.containsKey(gid)) {
									showGameLabel(holder.gameIcon, gameIcon, gid);
								}
								ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_default_icon);
								image_cache.put(grid, true);
							} else if (mode == MODE_MY_GROUP && !entity_cache.containsKey(grid)) {
								syncGroupEntity(grid, mapData, holder, convertView);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
					flag = false;
				} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					flag = false;
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}
		});
	}

	public GroupAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int mode, ListView listView) {
		super(context, data, resource, from, to);
		this.mode = mode;
		this.listView = listView;
		userVo = SystemContext.getInstance().getExtUserVo();
		addListener();
		this.mResource = resource;
		this.mInflater = LayoutInflater.from(context);
	}

	HashMap<Integer, View> lmap = new HashMap<Integer, View>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// convertView = super.getView(position, convertView, parent);
		convertView = lmap.get(position);

		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(mResource, null);
			holder = new ViewHolder();
			holder.groupId = (TextView) convertView.findViewById(R.id.group_id);
			holder.avatarView = (ImageView) convertView.findViewById(R.id.icon);
			holder.desc1 = (TextView) convertView.findViewById(R.id.desc1);
			holder.desc2 = (TextView) convertView.findViewById(R.id.desc2);
			holder.labelView = (ImageView) convertView.findViewById(R.id.labelView);
			holder.gameIcon = (ImageView) convertView.findViewById(R.id.gameIcon);
			holder.manageTxt = (TextView) convertView.findViewById(R.id.manageTxt);
			holder.manageView = (ImageView) convertView.findViewById(R.id.manageView);
			holder.rightView = (LinearLayout) convertView.findViewById(R.id.rightViewparent);
			holder.grade = (TextView) convertView.findViewById(R.id.grade);
			holder.gName = (TextView) convertView.findViewById(R.id.gnameTxt);
			holder.tagIcon = (ImageView) convertView.findViewById(R.id.tagIcon);
			lmap.put(position, convertView);
			convertView.setTag(holder);
		} else {
			// convertView = lmap.get(position);
			Object tag = convertView.getTag();
			holder = (ViewHolder) tag;
		}

		Map<String, Object> mapData = (HashMap<String, Object>) getItem(position);
		final Long grid = (Long) mapData.get("grid");
		showGroupDetailMsgs(holder, mapData, convertView);
		// 当前模式为推荐公会
		if (mode == MODE_RECOMMEND_GROUP) {
			// 是否关注
			holder.manageView.setVisibility(View.GONE);
			holder.manageTxt.setVisibility(View.GONE);
			holder.labelView.setVisibility(View.GONE);
			holder.rightView.setVisibility(View.VISIBLE);
			addCheckBoxView(convertView, holder.rightView, mapData);
		}
		if (mode == MODE_MY_GROUP && flag) {
			syncGroupEntity(grid, mapData, holder, convertView);
		}
		return convertView;
	}

	/**
	 * 同步公会 的实体信息
	 */
	private void syncGroupEntity(final Long id, final Map<String, Object> mapData, final ViewHolder holder, final View convertView) {
		// 同步公会实体
		ServiceFactory.getInstance().getSyncEntityService().syncEntity(id, SyncEntityService.TYPE_GROUP, new SyncCallBack() {

			@Override
			public void onSuccess(Object result2) {
				GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
				GroupVo groupVo = null;
				groupVo = groupDao.findGroupByGrid(id);
				praseGroupList(groupVo, mapData);
				showGroupDetailMsgs(holder, mapData, convertView);
				entity_cache.put(id, true);
			}

			@Override
			public void onFailure(Integer result2) {

			}
		});
	}

	/**
	 * 解析列表数据
	 * 
	 * @param list
	 * @return
	 */
	private void praseGroupList(GroupVo vo, Map<String, Object> map) {
		map.put("avatar", vo.getAvatar());
		map.put("serial", vo.getSerial());
		map.put("name", vo.getName());
		map.put("grid", vo.getGrid());
		if (vo.getUndesc() == null) {
			map.put("desc1", "简介：" + "");
		} else {
			map.put("desc1", "简介：" + vo.getUndesc());
		}
		map.put("presidentId", vo.getPresidentId());
		int count = vo.getMaxcount();
		if (count <= 0) {
			GroupGradeDao dao = DaoFactory.getDaoFactory().getGroupGradeDao(SystemContext.getInstance().getContext());
			int grade = vo.getGrade();
			GroupGradeVo gvo = dao.queryByGrade(grade);
			if (gvo != null)
				count = gvo.getMembers();
			LogUtil.d(TAG, count + "" + vo.getName());
		}
		if (vo.getPresidentName() == null) {
			map.put("desc2", "人数：" + vo.getTotal() + "/" + count + " | 会长：" + "");
		} else {
			map.put("desc2", "人数：" + vo.getTotal() + "/" + count + " | 会长：" + vo.getPresidentName());
		}
		map.put("gameIcon", vo.getGameIcon());
		map.put("gid", vo.getGid());
		map.put("grade", vo.getGrade());
		map.put("relwithgroup", vo.getRelWithGroup());
		map.put("maxcount", count);
	}

	/**
	 * 显示公会的详细信息
	 * 
	 * @param holder
	 * @param mapData
	 */
	private void showGroupDetailMsgs(ViewHolder holder, Map<String, Object> mapData, View convertView) {
		// 判断是否是群主还是管理员
		final Long grid = (Long) mapData.get("grid");
		viewholder_cache.put(grid, holder);
		convertView_cache.put(grid, convertView);
		// 显示公会头像
		String avatar = null;
		if (mapData.containsKey("avatar")) {
			avatar = (String) mapData.get("avatar");
		}
		String gameIcon = null;
		if (mapData.containsKey("gameIcon")) {
			gameIcon = (String) mapData.get("gameIcon");
		}
		String gName = null;
		if (mapData.containsKey("name")) {
			gName = (String) mapData.get("name");
			if (gName == null) {
				holder.gName.setText("");
			} else {
				holder.gName.setText(gName + "");
			}
		} else {
			holder.gName.setText("");
		}
		// 设置公会名称颜色
		if (mapData.containsKey("sid") && ((Long) mapData.get("sid")) > 0) {
			holder.tagIcon.setVisibility(View.VISIBLE);
			holder.gName.setTextAppearance(SystemContext.getInstance().getContext(), R.style.user_service_group_name);
		} else {
			holder.tagIcon.setVisibility(View.GONE);
			holder.gName.setTextAppearance(SystemContext.getInstance().getContext(), R.style.text_item_first_grade_left);
		}
		Long gid = null;
		if (mapData.containsKey("gid")) {
			gid = (Long) mapData.get("gid");
		}
		if (mapData.containsKey("grade")) {
			holder.grade.setText("LV" + mapData.get("grade"));
		} else {
			holder.grade.setText("LV0");
		}
		holder.gameIcon.setImageResource(R.drawable.common_default_icon);
		holder.avatarView.setImageResource(R.drawable.common_default_icon);
		if (gamevo_cache.containsKey(gid)) {
			ImageViewUtil.showImage(holder.gameIcon, gamevo_cache.get(gid).getGamelogo(), R.drawable.common_default_icon);
		} else if (flag) {
			showGameLabel(holder.gameIcon, gameIcon, gid);
		}
		if (flag || image_cache.containsKey(grid)) {
			ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_default_icon);
			image_cache.put(grid, true);
		}
		if (mapData.containsKey("serial"))
			holder.groupId.setText("" + mapData.get("serial"));

		if (mapData.containsKey("desc2")) {
			holder.desc2.setText("" + mapData.get("desc2"));
		}

		if (mapData.containsKey("desc1")) {
			holder.desc1.setText("" + mapData.get("desc1"));
		}
		if (mapData.containsKey("relwithgroup")) {
			int rel = (Integer) mapData.get("relwithgroup");
			addLabelView(convertView, grid, holder.labelView, holder.manageView, holder.manageTxt, rel);
		} else {
			GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
			GroupVo groupVo = groupDao.findGroupByGrid(grid);
			if (groupVo == null)
				addLabelView(convertView, grid, holder.labelView, holder.manageView, holder.manageTxt, 0);
			else
				addLabelView(convertView, grid, holder.labelView, holder.manageView, holder.manageTxt, groupVo.getRelWithGroup());
		}
		// 如果当前去请求公会数据 没有请求到的情况，下次重新去请求
		if (mode == MODE_MY_GROUP && mapData.containsKey("maxcount")) {
			if ((Integer) mapData.get("maxcount") == 0 && entity_cache.containsKey(grid)) {
				entity_cache.remove(grid);
			}
		}
	}

	/**
	 * 
	 * @param view
	 * @param gid
	 */
	private void showGameLabel(final ImageView view, final String gameIcon, final Long gid) {
		if (gid != null) {
			ProxyFactory.getInstance().getGameProxy().getGameInfo(new ProxyCallBack<GameVo>() {

				@Override
				public void onSuccess(GameVo result) {
					if (result != null) {
						gamevo_cache.put(gid, result);
						if (flag)
							ImageViewUtil.showImage(view, result.getGamelogo(), R.drawable.common_default_icon);
					} else
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
	private void addLabelView(View convertView, long grid, ImageView labelView, ImageView manageView, TextView manageTxt, int rel) {
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
		ImageView tagIcon;
	}

	/**
	 * 清除缓存的方法
	 */
	@Override
	public void clearCache() {
		image_cache.clear();
		viewholder_cache.clear();
		convertView_cache.clear();
		entity_cache.clear();
	}

}
