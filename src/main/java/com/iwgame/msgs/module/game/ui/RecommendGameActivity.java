/**      
 * FollowGameActivity.java Create on 2013-9-5     
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.game.adapter.CommonGameAdapter;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.utils.ViewUtil;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: FollowGameActivity
 * @Description: 添加系统推荐贴吧关注
 * @author 王卫
 * @date 2013-9-5 下午03:37:09
 * @Version 1.0
 * 
 */
public class RecommendGameActivity extends BaseListActivity implements OnClickListener {
	protected static final String TAG = "RecommendGameActivity";
	// 菜单按钮
	private Button rightBtn;
	private TextView searchBtn;
	// 搜索框
	private EditText searchTxt;

	private LinearLayout listContent;
	
	private boolean isFirstLoad = false;
	
	private LinearLayout.LayoutParams params;

	private PullToRefreshListView pullToRefreshListView;
	/**
	 * 初始化
	 */
	@Override
	protected void initialize() {
		onresumeNeedRefresh = false;
		
		isFirstLoad = true;
		mLimit = PagerVo.LIMIT;
		offsetMode = 0;
		// 显示左边
		setLeftVisible(true);
		// 显示右边
		setRightVisible(false);
		titleTxt.setText("添加贴吧");
		// 添加右边功能按钮
		rightBtn = new Button(this);
		rightBtn.setBackgroundResource(R.drawable.common_tab_btn_distance);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(rightBtn);
		rightBtn.setOnClickListener(this);
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View content = (LinearLayout) View.inflate(this, R.layout.main_search_list, null);
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(content, params);
		// 设置搜索按钮功能
		searchBtn = (TextView) contentView.findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(this);

		// 搜索框
		searchTxt = (EditText) contentView.findViewById(R.id.searchTxt);
		searchTxt.setHint("请输入贴吧关键字");
		// 设置清除按钮
		Button cleanBtn = (Button) findViewById(R.id.cleanBtn);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(searchTxt, cleanBtn);
		cleanBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchTxt.setText("");
			}
		});
		// 获取TITLE
		// UI上部分
		LinearLayout top = (LinearLayout) content.findViewById(R.id.top);
		top.setVisibility(View.VISIBLE);
		TextView listTitle = (TextView) content.findViewById(R.id.listTitle);
		listTitle.setText("推荐的贴吧");
		// 设置列表功能
		listContent = (LinearLayout) content.findViewById(R.id.listContent);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		setPullRefreshListView(pullToRefreshListView);
		// 添加列表点击功能
		setListItemClikEvent(list);

		setAdapter(new CommonGameAdapter(RecommendGameActivity.this, listData, R.layout.game_list_item, new String[] { "gamename" },
				new int[] { R.id.gamename }, 0, CommonGameAdapter.MODE_COMMON));
		super.initialize();

		new Thread(new Runnable() {

			@Override
			public void run() {
				ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GAME, new SyncCallBack() {

					@Override
					public void onSuccess(Object result) {
					}

					@Override
					public void onFailure(Integer result) {
					}
				});
			}
		}).start();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		searchBtn.setClickable(true);
	}
	
	/**
	 * 添加列表点击功能
	 * 
	 * @param list
	 */
	private void setListItemClikEvent(ListView list) {
		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Utils.hideSoftInput(RecommendGameActivity.this, searchTxt);
				
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				Object gid = map.get("gid");
				if (gid != null) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == rightBtn.getId()) {
			ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {

				@Override
				public void onBack(BDLocation bdLocation) {
					if("0.000000,0.000000".equals(SystemContext.getInstance().getLocation()))
						ToastUtil.showToast(RecommendGameActivity.this, getString(R.string.com_haveno_location));
					Intent intent = new Intent(RecommendGameActivity.this, NearGameActivity.class);
					startActivity(intent);
				}
			});
		} else if (v.getId() == R.id.searchBtn) {
			// 点击搜索按钮校验搜索框内容
			String keyword = searchTxt.getText().toString();
			if (keyword != null && !keyword.isEmpty()) {
				searchBtn.setClickable(false);
				Intent intent = new Intent(this, SearchGameActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_KEYWORD, keyword);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				ToastUtil.showToast(this, getString(R.string.game_search_fail));
			}
		}
	}

	
	/**
	 * 显示加载的loading
	 */
	public void setLoadingUI() {
		listContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		view.setBackgroundColor(getResources().getColor(R.color.list_first_bg));
		listContent.addView(view, params);
	}
	
	
	/**
	 * 获取系统贴吧
	 */
	@Override
	protected void getListData(final long offset, int limit) {
		super.getListData(offset, limit);
		if(isFirstLoad){
			setLoadingUI();
		}
		ProxyFactory.getInstance().getGameProxy().getConditionGame(new ProxyCallBack<PagerVo<ExtGameVo>>() {

			@Override
			public void onSuccess(PagerVo<ExtGameVo> result) {
				if (isFirstLoad) {
					isFirstLoad = false;
					listContent.removeAllViews();
					listContent.addView(pullToRefreshListView, params);
				}
				
				if (offset == 0 || offset >= Integer.MAX_VALUE)
					clean();
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					// 添加设置LIST数据
					addListData(result);
				} else {
					if (listData == null || listData.size() == 0)
						ViewUtil.showNullBgView(RecommendGameActivity.this, listContent, null);
					hasNext = false;
					LogUtil.d(TAG, "数据为空");
				}
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (isFirstLoad) {
					isFirstLoad = false;
					listContent.removeAllViews();
					listContent.addView(pullToRefreshListView,params);
				}
				if (listData == null || listData.size() == 0)
					ViewUtil.showNullBgView(RecommendGameActivity.this, listContent, null);
				LogUtil.d(TAG, "获取贴吧列表数据为空");
				onFooterRefreshComplete();
			}
		}, this, true, null, null, "2", null, offset, limit, null, null, null, null, null,null);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(PagerVo<ExtGameVo> pageVo) {
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
		mOffset = mOffset + Math.abs(mLimit);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();

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
			map.put("gamename", vo.getGamename());
			map.put("follow", vo.getFollow());
			map.put("gid", vo.getGameid());
			if (vo.getUserCount() > 0) {
				map.put("desc3", "有" + vo.getUserCount() + "个用户已关注");
			} else {
				map.put("desc3", "");
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
			tmplist.add(map);
		}
		return tmplist;
	}

}
