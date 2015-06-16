/**      
 * BlacklistActivity.java Create on 2013-11-12     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.setting.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.utils.ViewUtil;
import com.iwgame.msgs.vo.local.UserVo;
import com.youban.msgs.R;

/**
 * @ClassName: BlacklistActivity
 * @Description: 黑名单列表
 * @author 王卫
 * @date 2013-11-12 下午1:49:05
 * @Version 1.0
 * 
 */
public class BlacklistActivity extends BaseListActivity {

	private UserAdapter2 adapter;
	private LinearLayout nullcontent;
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
		// 设置TITLE
		titleTxt.setText("黑名单");
		// 设置内容UI
		View view = (LinearLayout) View.inflate(this, R.layout.user_search_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(view, params);
		// 设置列表
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		nullcontent = (LinearLayout)findViewById(R.id.nullContent);
		setPullRefreshListView(pullToRefreshListView);
//		adapter = new UserAdapter2(BlacklistActivity.this, listData, R.layout.user_list_item,
//				new String[] { "nickname", "desc", "sex" }, new int[] { R.id.nickname, R.id.desc, R.id.sex }, false, true,list);
		
		adapter = new UserAdapter2(BlacklistActivity.this, listData, R.layout.user_list_item,
				new String[] { "nickname"}, new int[] { R.id.nickname}, UserAdapter2.GAME_FOLLOW_TYPE_COMMON, false, UserAdapter2.TYPE_CONTRACT, list);
		adapter.setmShowDis(false);
		adapter.setmShowFollow(false);
		setListAndAdapter(list, adapter);
		// 添加列表点击功能
		setListItemClikEvent(list);
	}


	
	/**
	 * 添加列表点击功能
	 * 
	 * @param list
	 */
	protected void setListItemClikEvent(ListView list) {
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
					bundle.putBoolean("isshowlahei", false);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity2#getListData(long, int)
	 */
	@Override
	protected void getListData(long offset, int limit) {
		super.getListData(offset, limit);
		if(adapter != null)adapter.setFlag(true);
		hasNext = false;
		final LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		if(listData.size() <= 0){
			showLoadingUI();
			pullToRefreshListView.setMode(Mode.DISABLED);
		}
		ProxyFactory.getInstance().getUserProxy().getContactUsers(new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> result) {
				showListView();
				pullToRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
				if (result != null && result.size() > 0) {
					// 添加设置LIST数据
					addListData(result);
				} else {
					ViewUtil.showNullBgView(BlacklistActivity.this, contentView, R.drawable.no_relative_user);
				}
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				showNullBgView(null);
				onFooterRefreshComplete();
			}
		}, this, 1, 2, 1, 0, Integer.MAX_VALUE, null);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(List<UserVo> list) {
		// 添加设置LIST数据
		listData.addAll(praseUserList(list));
		adapter.notifyDataSetChanged();
	}

	/**
	 * 当没有数据的时候
	 * 显示loadingUI
	 */
	private void showLoadingUI(){
		showNullListView();		
		nullcontent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		nullcontent.addView(view, params);
	}
	
	/**
	 * 显示空背景
	 */
	private void showNullListView(){
		nullcontent.setVisibility(View.VISIBLE);
		pullToRefreshListView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示pulltorefreshListview
	 */
	private void showListView(){
		nullcontent.setVisibility(View.GONE);
		pullToRefreshListView.setVisibility(View.VISIBLE);
	}
	
	
	
	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseUserList(List<UserVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			UserVo vo = list.get(i);
			map.put("avatar", vo.getAvatar());
			map.put("nickname", vo.getUsername());
			map.put("news", vo.getMood());
			map.put("desc", vo.getMood());
			if (vo.getSex() == 0) {
				map.put("sex", vo.getSex());
			} else {
				map.put("sex",vo.getSex());
			}
			map.put("age", vo.getAge());
			map.put("uid", vo.getUserid());
			map.put("serial", vo.getSerial());
			map.put("rel", vo.getRelPositive());
			map.put("grade", vo.getGrade());
			tmplist.add(map);
		}
		return tmplist;
	}

}
