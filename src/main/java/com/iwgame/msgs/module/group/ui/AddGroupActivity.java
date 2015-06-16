/**      
 * AddGroupActivity.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.youban.msgs.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.group.adapter.GroupAdapter2;
import com.iwgame.msgs.module.group.adapter.GroupGameListAdapter;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.utils.ViewUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: AddGroupActivity
 * @Description: 添加公会
 * @author 王卫
 * @date 2013-10-24 下午05:37:10
 * @Version 1.0
 * 
 */
public class AddGroupActivity extends BaseListActivity implements OnClickListener {
	private static final String TAG = "AddGroupActivity";
	// 搜索框
	private EditText searchTxt;

	private LinearLayout listContent;

	private List<GameVo> gamevoList = new ArrayList<GameVo>();
	private InputMethodManager manager;
	private LinearLayout nullContent;
	private PullToRefreshListView pullToRefreshListView;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity#initialize()
	 */
	@Override
	protected void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		titleTxt.setText("添加公会");
		// 设置内容UI
		manager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View content = (LinearLayout) View.inflate(this, R.layout.main_search_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		content.findViewById(R.id.top).setVisibility(View.VISIBLE);
		contentView.addView(content, params);
		// 设置搜索按钮功能
		contentView.findViewById(R.id.searchBtn).setOnClickListener(this);;
		// 搜索框
		searchTxt = (EditText) contentView.findViewById(R.id.searchTxt);
		searchTxt.setHint("请输入公会ID或名称");
		listContent = (LinearLayout) contentView.findViewById(R.id.listContent);
		nullContent = (LinearLayout)contentView.findViewById(R.id.null_content);
		// 获取TITLE
		TextView listTitle = (TextView) content.findViewById(R.id.listTitle);
		listTitle.setText("请从关注贴吧中选择公会");
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
		// 设置列表功能
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		setPullRefreshListView(pullToRefreshListView);
		GroupGameListAdapter adapter = new GroupGameListAdapter(AddGroupActivity.this, listData, R.layout.group_game_list_item,
				new String[] { "gamename" }, new int[] { R.id.gamename });
		setListAndAdapter(list, adapter);
		// 添加列表点击功能
		setListItemClikEvent(list);
	}

	/**
	 * 添加列表点击功能
	 * 
	 * @param list
	 */
	private void setListItemClikEvent(ListView list) {
		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				Object gid = map.get("gid");
				if (gid != null) {
					Intent intent = new Intent(parent.getContext(), GroupListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, Long.valueOf(gid.toString()));
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, GroupAdapter2.MODE_GAME_GROUP);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 当点击返回按钮的时候
	 * 把当前 的界面关闭掉即可
	 */
	protected void back() {
		manager.hideSoftInputFromWindow(searchTxt.getWindowToken(), 0);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				AddGroupActivity.this.finish();
			}
		}, 200);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity2#getListData(long, int)
	 */
	@Override
	protected void getListData(long offset, int limit) {
		super.getListData(offset, limit);
		hasNext = false;
		if(listData != null && listData.size() <= 0)setLoadingUI();
		ProxyFactory.getInstance().getGameProxy().getFollowGames(new ProxyCallBack<List<GameVo>>() {

			@Override
			public void onSuccess(List<GameVo> result) {
				showListview();
				if (result != null && result.size() > 0) {
					onresumeNeedRefresh = false;//如果获取到了数据后，返回的时候 不需要在去刷新数据
					// 添加设置LIST数据
					if(AdaptiveAppContext.getInstance().getAppConfig().getMode() == 1){
						addListData(result);
					}else if(AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2){
						gamevoList.clear();
						long gid = AdaptiveAppContext.getInstance().getAppConfig().getGameId();
						int size = result.size();
						GameVo vo;
						for(int i = 0; i < size; i ++){
							vo = result.get(i);
							if(gid == vo.getGameid()){
								gamevoList.add(vo);
								break;
							}
						}
						addListData(gamevoList);
					}
				} else {
					ViewUtil.showNullBgView(AddGroupActivity.this, listContent, null);
					hasNext = false;
					LogUtil.d(TAG, "数据为空");
				}
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				showListview();
				ViewUtil.showNullBgView(AddGroupActivity.this, listContent, null);
				LogUtil.e(TAG, "获取列表数据失败:" + result);
				onFooterRefreshComplete();
			}
		}, this, true);
	}

    /**
     * 显示加载的动画	
     */
	private void setLoadingUI() {
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
	 * 显示空背景
	 */
	private void showNullContent(){
		pullToRefreshListView.setVisibility(View.GONE);
		nullContent.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 当数据获取到的时候
	 * 要显示列表的内容
	 */
	private void showListview(){
		nullContent.setVisibility(View.GONE);
		pullToRefreshListView.setVisibility(View.VISIBLE);
	}
	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(List<GameVo> result) {
		// 添加设置LIST数据
		listData.addAll(praseList(result));
		if (result != null && result.size() > Math.abs(mLimit)) {
			list.setSelectionFromTop(list.getFirstVisiblePosition(), 0);
		}
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.searchBtn) {
			// 点击搜索按钮校验搜索框内容
			String keyword = searchTxt.getText().toString();
			if (keyword != null && !keyword.isEmpty()) {
				Intent intent = new Intent(this, GroupListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, GroupAdapter2.MODE_GROUP_NAMEID);
				bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_KEYWORD, keyword);
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				ToastUtil.showToast(this, getString(R.string.game_search_fail));
			}
		}
	}

	/**
	 * 解析数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseList(List<GameVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			GameVo vo = list.get(i);
			map.put("gid", vo.getGameid());
			map.put("avatar", vo.getGamelogo());
			map.put("gamename", vo.getGamename());
			tmplist.add(map);
		}
		return tmplist;
	}
}
