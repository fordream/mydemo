/**      
 * GameListActivity.java Create on 2013-10-16     
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
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.youban.msgs.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.game.adapter.CommonGameAdapter;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.utils.AppUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: GameListActivity
 * @Description: 贴吧列表
 * @author 王卫
 * @date 2013-10-16 上午11:59:03
 * @Version 1.0
 * 
 */
public class GameListActivity extends BaseListActivity {
	protected static final String TAG = "GameListActivity";
	// 用于列表类型
	private int mode;
	// 用户ID
	private Long uid;

	private ImageView rightMenu;

	private boolean isFirstLoad = false;

	private LinearLayout nullContent;
	
	private PullToRefreshListView pullToRefreshListView;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity#init()
	 */
	@Override
	protected void initialize() {
		super.initialize();
		isFirstLoad = true;

		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (LinearLayout) View.inflate(this, R.layout.main_search_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		// UI上部分
		LinearLayout top = (LinearLayout) view.findViewById(R.id.top);
		nullContent = (LinearLayout)view.findViewById(R.id.null_content);
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			// 窗口标题
			String title = bundle.getString(TITLE);
			if (title != null)
				titleTxt.setText(title);
			// 是否显示左边
			boolean isShowLeft = bundle.getBoolean(IS_SHOW_LEFT);
			setLeftVisible(isShowLeft);
			// 是否显示右边
			boolean isShowRight = bundle.getBoolean(IS_SHOW_RIGHT);
			setRightVisible(isShowRight);
			// 是否显示TOP搜索框内容
			boolean isShowTop = bundle.getBoolean(VISIBLE_TOP_MODE);
			if (isShowTop)
				top.setVisibility(View.VISIBLE);
			else
				top.setVisibility(View.GONE);
			// 列表显示类型
			mode = bundle.getInt(MODE);
			// 获取用户ID
			uid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
			// 添加右边菜单按钮功能
			if (mode == CommonGameAdapter.MODE_RECOMMEND) {
				rightMenu = new ImageView(this);
				rightMenu.setBackgroundResource(R.drawable.common_tab_btn_follow);
				LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
				rightView.addView(rightMenu);
			}
		}
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		setPullRefreshListView(pullToRefreshListView);
		setListAndAdapter(list, new CommonGameAdapter(GameListActivity.this, listData,
				R.layout.game_list_item, new String[] { "gamename", "distance" }, new int[] { R.id.gamename, R.id.rdesc }, 0, mode));
		// 添加列表点击功能
		if (mode != CommonGameAdapter.MODE_RECOMMEND)
			setListItemClikEvent(list);
	}


	@Override
	protected void refreshList() {
		offsetMode = 0;
		mLimit = 10;
		if (listData != null && adapter != null) {
			if (offsetMode == 1)
				mOffset = Long.MAX_VALUE;
			else if (offsetMode == 0)
				mOffset = 0;
			hasNext = true;
			listData.clear();
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
			getListData(mOffset, mLimit);
		} else {
			return;
		}
	}

	/**
	 * 显示加载动画
	 */
	private void setLoadingUI(){
		showNullContent();
		nullContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		view.setBackgroundColor(getResources().getColor(R.color.list_first_bg));
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		nullContent.addView(view, params);
	}

	/**
	 * 显示列表数据
	 */
	private void showListView(){
		nullContent.setVisibility(View.GONE);
		pullToRefreshListView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 显示空背景
	 */
	private void showNullContent(){
		pullToRefreshListView.setVisibility(View.GONE);
		nullContent.setVisibility(View.VISIBLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity#getListData()
	 */
	@Override
	protected void getListData(long offset, int limit) {
		super.getListData(offset, limit);
		if (mode == CommonGameAdapter.MODE_RECOMMEND) {
			if(isFirstLoad){
				setLoadingUI();
			}
			ProxyFactory.getInstance().getGameProxy().searchRecommendGames(new ProxyCallBack<PagerVo<ExtGameVo>>() {

				@Override
				public void onSuccess(PagerVo<ExtGameVo> result) {
					showListView();
					if (isFirstLoad) {
						isFirstLoad = false;
					}
					if (result != null && result.getItems() != null && result.getItems().size() > 0) {
						mOffset = mOffset + result.getItems().size();
						// 设置LIST数据
						addListData(result);
					} else {
						hasNext = false;
						showNullBgView(R.drawable.common_no_seach_postbar);
						ToastUtil.showToast(GameListActivity.this, GameListActivity.this.getString(R.string.global_data_load_ok));
						LogUtil.d(TAG, "数据为空");
					}
					onFooterRefreshComplete();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					showListView();
					showNullBgView(R.drawable.common_no_seach_postbar);
					LogUtil.e(TAG, "获取推荐贴吧失败");
					if (isFirstLoad) {
						isFirstLoad = false;
					}
					ToastUtil.showToast(GameListActivity.this, GameListActivity.this.getString(R.string.global_data_load_ok));
					onFooterRefreshComplete();
				}
			}, this, offset, limit);
		} else {
			if(isFirstLoad){
				setLoadingUI();
			}
			ProxyFactory.getInstance().getGameProxy().getConditionGame(new ProxyCallBack<PagerVo<ExtGameVo>>() {

				@Override
				public void onSuccess(PagerVo<ExtGameVo> result) {
					showListView();
					if (result != null && result.getItems() != null && result.getItems().size() > 0) {
						// 添加设置LIST数据
						addListData(result);
					} else {
						showNullBgView(R.drawable.common_no_seach_postbar);
						hasNext = false;
						LogUtil.d(TAG, "数据为空");
					}
					if (isFirstLoad) {
						isFirstLoad = false;
					}
					onFooterRefreshComplete();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					showNullBgView(R.drawable.common_no_seach_postbar);
					LogUtil.d(TAG, "获取贴吧列表数据为空");
					showListView();
					if (isFirstLoad) {
						isFirstLoad = false;
					}
					onFooterRefreshComplete();
				}
			}, this, null, null, uid == 0 ? null : uid, "1", null, offset, limit, 1, null, null, null, null,1);
		}
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(PagerVo<ExtGameVo> pageVo) {
		mOffset = pageVo.getOffset();
		// 添加设置LIST数据
		listData.addAll(praseList(pageVo.getItems()));
		if (pageVo.getItems() != null) {
			if (pageVo.getItems().size() < Math.abs(mLimit)) {
				hasNext = false;
			}
			if (pageVo.getItems().size() > Math.abs(mLimit)) {
				list.setSelectionFromTop(list.getFirstVisiblePosition(), 0);
			}
		}
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 设置列表点击事件
	 * 
	 * @param list
	 */
	private void setListItemClikEvent(ListView list) {
		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				Object gid = map.get("gid");
				long gameId = Long.valueOf(gid.toString());
				AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
				if (appconfig != null && appconfig.isRecbarmsg() && gameId != appconfig.getGameId()) {
					AppUtil.openGame(GameListActivity.this, gameId, GameTopicListActivity.class.getName(), GameListActivity.this.getResources().getString(R.string.postbar_show_game_tip_for_youban_uninstall));
				} else if (gid != null) {
						Intent intent = new Intent(parent.getContext(), GameTopicListActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, Long.valueOf(gid.toString()));
						bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, String.valueOf(map.get("gamename")));
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
				}
			}
		});
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseList(List<ExtGameVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			ExtGameVo vo = list.get(i);
			map.put("logo", vo.getGamelogo());
			map.put("gamename", vo.getGamename());
			map.put("follow", vo.getFollow());
			map.put("gid", vo.getGameid());
			map.put("sortId", vo.getSortId());
			map.put("distance", DistanceUtil.covertDistance(vo.getDistance()));
			if (mode == CommonGameAdapter.MODE_SIMPLE) {
				// 用户关注的贴吧列表，模式为1
				if (vo.getUserCount() > 0)
					map.put("desc3", "有" + vo.getUserCount() + "个用户关注");
				else
					map.put("desc3", "没有用户关注");
			} else if (mode == CommonGameAdapter.MODE_COMMON) {
				// 附近的贴吧，模式为2
				if (vo.getNickname() != null && !vo.getNickname().isEmpty()) {
					map.put("desc3", "附近" + vo.getUserCount() + "个好友关注");
				} else {
					map.put("desc3", "附近没有好友关注");
				}
			}
			String type = "类型：";
			if (vo.getType() == null) {
				type += "未知";
			} else {
				type += vo.getType();
			}
			String publisher = "开发商：";
			if (vo.getPublisher() == null) {
				publisher += "未知";
			} else {
				publisher += vo.getPublisher();
			}
			map.put("desc", type + " | " + publisher);
			map.put("isChecked", true);
			tmplist.add(map);
		}
		return tmplist;
	}

}
