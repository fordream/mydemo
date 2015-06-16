/**      
 * GameFriendFragment.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import com.baidu.location.BDLocation;
import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.widget.PullToRefreshView;
import com.iwgame.msgs.widget.PullToRefreshView.OnFooterRefreshListener;
import com.iwgame.msgs.widget.PullToRefreshView.OnHeaderRefreshListener;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: GameFriendFragment
 * @Description: 贴吧-游伴
 * @author 王卫
 * @date 2013-9-3 下午05:01:17
 * @Version 1.0
 * 
 */
public class GameDetailAlikeFragment extends BaseFragment implements OnClickListener, OnHeaderRefreshListener, OnFooterRefreshListener {
	protected static final String TAG = "GameDetailAlikeFragment";
	// 系统推荐标签
	private RadioButton sysTab;
	// 共同好友标签
	private RadioButton comTab;
	// 附近的人标签
	private RadioButton nearTab;

	// 可刷新主容器
	protected PullToRefreshView mainPullRefreshView;
	// 列表
	protected ListView list;
	// 列表数据源
	protected List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
	// 数据源适配器
	protected UserAdapter2 adapter;
	// 当前页码
	protected long offset = Long.MAX_VALUE;
	// 每页大小(默认降序向下取)
	protected int limit = -PagerVo.LIMIT;
	// 是否有下一页
	protected boolean hasNext = true;
	// 类型[1系统的2他的3附近的]
	private int mode = 2;

	private LinearLayout listContent;
	private LinearLayout nullContent;

	private long gid;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// 获得传入的参数
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, 0);
		}

		View v = inflater.inflate(R.layout.fragment_game_detail_alike, container, false);
		// 初始化界面
		initialize(v);
		PTAG = TAG;
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		changeTab(false, true, false, mode, false, false);
	}

	/**
	 * 初始化界面
	 */
	private void initialize(View v) {
		listContent = (LinearLayout) v.findViewById(R.id.listContent);
		nullContent = (LinearLayout) v.findViewById(R.id.nullContent);
		list = (ListView) v.findViewById(R.id.listView);
		// 设置LIST数据
		adapter = new UserAdapter2(GameDetailAlikeFragment.this.getActivity(), listData, R.layout.user_list_item_userfragment, new String[] { "nickname", "desc" },
				new int[] { R.id.nickname, R.id.desc }, true, UserAdapter2.GAME_FOLLOW_TYPE_COMMON, false, false,list);
		list.setAdapter(adapter);
		sysTab = (RadioButton) v.findViewById(R.id.sysTab);
		sysTab.setOnClickListener(this);
		comTab = (RadioButton) v.findViewById(R.id.comTab);
		comTab.setOnClickListener(this);
		nearTab = (RadioButton) v.findViewById(R.id.nearTab);
		nearTab.setOnClickListener(this);

		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				Object uid = map.get("uid");
				if (uid != null && !uid.equals(SystemContext.getInstance().getExtUserVo().getUserid())) {
					Intent intent = new Intent(parent.getContext(), UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sysTab) {
			// changeTab(true, false, false, 1, false, true);
		} else if (v.getId() == R.id.comTab) {
			changeTab(false, true, false, 2, false, false);
		} else if (v.getId() == R.id.nearTab) {
			ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {

				@Override
				public void onBack(BDLocation bdLocation) {
					changeTab(false, false, true, 3, true, true);
					if("0.000000,0.000000".equals(SystemContext.getInstance().getLocation()))
						ToastUtil.showToast(getActivity(), getString(R.string.com_haveno_location));
				}
			});
		}
	}

	/**
	 * 切换TAB加载数据
	 * 
	 * @param sysTabShow
	 * @param comTabShow
	 * @param nearTabShow
	 * @param mode
	 * @param showDis
	 */
	private void changeTab(boolean sysTabShow, boolean comTabShow, boolean nearTabShow, int mode, boolean showDis, boolean showFollow) {
		nullContent.setVisibility(View.GONE);
		listContent.setVisibility(View.VISIBLE);
		sysTab.setSelected(sysTabShow);
		comTab.setSelected(comTabShow);
		nearTab.setSelected(nearTabShow);
		this.mode = mode;
		adapter.setmShowDis(showDis);
		adapter.setmShowFollow(showFollow);
		hasNext = true;
		listData.clear();
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
		offset = Long.MAX_VALUE;
		getListData(mode, offset, limit);
	}

	/**
	 * 查找人的列表
	 * 
	 * @param mode
	 * @param showDis
	 *            [1系统推荐2共同好友3附近的人]
	 */
	private void getGameUser(final int mode, long offset, int limit) {
		Boolean isfate = null;
		Integer rel = null;
		Long gid = null;
		Boolean isrecommend = null;
		String resulttype = "3,4";
		Integer nearDistance = null;
		if (mode == 1) {
			isrecommend = true;
		} else if (mode == 2) {
			rel = 1;
			gid = this.gid;
		} else if (mode == 3) {
			gid = this.gid;
			nearDistance = SystemContext.getInstance().getNearDistance();
		}
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(getActivity());
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().searchUsersBysid(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					// 设置LIST数据
					addListData(result);
				} else {
					nullContent.setVisibility(View.VISIBLE);
					listContent.setVisibility(View.GONE);
					LogUtil.d(TAG, "数据为空");
				}
				dialog.dismiss();
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				nullContent.setVisibility(View.VISIBLE);
				listContent.setVisibility(View.GONE);
				LogUtil.e(TAG, "获取用户信息失败");
				dialog.dismiss();
				onFooterRefreshComplete();
			}
		}, getActivity(), gid, isfate, isrecommend, rel, resulttype, null, offset, limit, nearDistance, null, null, null,null,null);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(PagerVo<UserObject> pagerVo) {
		offset = pagerVo.getOffset();
		// 添加设置LIST数据
		listData.addAll(praseUserList(pagerVo.getItems()));
		if (pagerVo.getItems() != null) {
			if (pagerVo.getItems().size() < Math.abs(limit)) {
				hasNext = false;
			}
			if (pagerVo.getItems().size() > Math.abs(limit)) {
				list.setSelectionFromTop(list.getFirstVisiblePosition(), 0);
			}
		}
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseUserList(List<UserObject> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				UserObject vo = list.get(i);
				map.put("avatar", vo.getAvatar());
				map.put("nickname", vo.getNickname());
				map.put("news", vo.getMood());
				map.put("distance", DistanceUtil.covertDistance(vo.getDistance()));
				map.put("desc", "没有关注贴吧");
				map.put("sex", vo.getSex());
				map.put("age", vo.getAge());
				map.put("rel", vo.getRel());
				map.put("uid", vo.getUid());
				map.put("gids", vo.getGids());
				map.put("gameCount", vo.getGameCount());
				map.put("grade", vo.getGrade());
				tmplist.add(map);
			}
		}
		return tmplist;
	}

	/**
	 * 获取列表数据(子类实现)
	 * 
	 * @param offset
	 * @param limit
	 */
	private void getListData(int mode, long offset, int limit) {
		if(adapter != null)adapter.setFlag(true);
		getGameUser(mode, offset, limit);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.widget.PullToRefreshView.OnFooterRefreshListener#
	 * onFooterRefresh(com.iwgame.msgs.widget.PullToRefreshView)
	 */
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (hasNext) {
			getListData(mode, offset, limit);
		} else {
			onFooterDelayedRefreshComplete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.widget.PullToRefreshView.OnHeaderRefreshListener#
	 * onHeaderRefresh(com.iwgame.msgs.widget.PullToRefreshView)
	 */
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mainPullRefreshView.onHeaderRefreshComplete();
	}

	public void onFooterRefreshComplete() {
		mainPullRefreshView.onFooterRefreshComplete();
	}

	public void onHeaderRefreshComplete(CharSequence lastupdated) {
		mainPullRefreshView.onHeaderRefreshComplete(lastupdated);
	}

	public void onFooterDelayedRefreshComplete() {
		mainPullRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mainPullRefreshView.onFooterRefreshComplete();
			}
		}, 1000);
	}

	public void onHeaderDelayedRefreshComplete(final CharSequence lastupdated) {
		mainPullRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mainPullRefreshView.onHeaderRefreshComplete(lastupdated);
			}
		}, 1000);
	}

}
