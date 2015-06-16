/**      
 * RecommendGameActivity.java Create on 2013-9-4     
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
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.game.adapter.RecommendGame2FriendAdapter;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: RecommendGameActivity
 * @Description: 推荐贴吧给好友界面
 * @author 王卫
 * @date 2013-9-4 下午02:28:05
 * @Version 1.0
 * 
 */
public class RecommendGame2FriendActivity extends BaseListActivity {

	protected static final String TAG = "RecommendGame2FriendActivity";
	// 贴吧ID
	private long gid;
	PullToRefreshListView pullToRefreshListView;
	LinearLayout.LayoutParams params;
	private LinearLayout view;
	private RecommendGame2FriendAdapter adapter;
	
	
	/**
	 * 初始化
	 */
	@Override
	protected void initialize() {
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			gid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
		}
		// 显示左边
		setLeftVisible(true);
		// 影藏右边
		setRightVisible(false);
		titleTxt.setText("推荐贴吧");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		view = (LinearLayout) View.inflate(this, R.layout.game_user_list, null);
		params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		setPullRefreshListView(pullToRefreshListView);
	    adapter = new RecommendGame2FriendAdapter(this, listData, R.layout.user_list_item, new String[] { "nickname" },
				new int[] { R.id.nickname },list);
		setListAndAdapter(list, adapter);

		// 添加列表点击功能
		setListItemClikEvent(list);
	}


	/**
	 * 显示加载的loading
	 */
	public void setLoadingUI() {
		contentView.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		view.setBackgroundColor(getResources().getColor(R.color.list_first_bg));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		contentView.addView(view, params);
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
	 * 查找共同玩贴吧的人的列表
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity2#getListData(long, int)
	 */
	@Override
	protected void getListData(long offset, int limit) {
		// TODO Auto-generated method stub
		super.getListData(offset, limit);
		hasNext = false;
		getUserContactData(list, 1, 1, 1, 0, 10000, null);
	}

	/**
	 * 加载数据完成以后，
	 * 第二次不需要重新去加载数据了
	 */
	@Override
	protected void onResume() {
		super.onResume();
		onresumeNeedRefresh = false;
	}


	/**
	 * 获取用户通讯录数据
	 * 
	 * @param v
	 * @param mode
	 * @param sort
	 * @param keyword
	 */
	private void getUserContactData(final ListView v, int type, int relation, int sort, int pageNo, int size, String keyword) {
		if(listData != null && listData.size() <= 0) setLoadingUI();
		ProxyFactory.getInstance().getUserProxy().getContactUsers(new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> result) {
				if (result != null && result.size() > 0) {
					getRecommendInfo(result);
				} else {
					hasNext = false;
					showNullBgView(null);
					LogUtil.d(TAG, "数据为空");
				}
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				showNullBgView(null);
				onFooterRefreshComplete();
			}
		}, this, type, relation, sort, pageNo, size, keyword);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(List<UserVo> result,Map<Long, Boolean> map) {
		// 添加设置LIST数据
		listData.addAll(praseUserList(result,map));
		if (result != null && result.size() > Math.abs(mLimit)) {
			list.setSelectionFromTop(list.getFirstVisiblePosition(), 0);
		}
		adapter.setFlag(true);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseUserList(List<UserVo> list,Map<Long, Boolean>isRecommend) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			UserVo vo = list.get(i);
			map.put("avatar", vo.getAvatar());
			map.put("nickname", vo.getUsername());
			map.put("desc", "没有关注贴吧");
			map.put("sex", vo.getSex());
			map.put("mood", vo.getMood());
			map.put("age", vo.getAge());
			map.put("uid", vo.getUserid());
			map.put("gid", gid);
			map.put("grade", vo.getGrade());
			if(isRecommend.containsKey(vo.getUserid())){
				map.put("isrecommend", isRecommend.get(vo.getUserid()));
			}else{
				map.put("isrecommend", false);
			}
			tmplist.add(map);
		}
		return tmplist;
	}

	/**
	 * 获取是显示推荐  
	 * 还是显示已推荐
	 */
	private void getRecommendInfo(final List<UserVo>list){
		ProxyFactory.getInstance().getUserProxy().getRecommendedGameInfo(new ProxyCallBack<Map<Long, Boolean>>() {

			@Override
			public void onSuccess(Map<Long, Boolean> result) {
				if(listData.size() <= 0) {
					contentView.removeAllViews();
					contentView.addView(view, params);
				}
				addListData(list,result);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

			}
		}, null, gid, getIds(list));
	}

	/**
	 * 获取到所有用户的
	 * id的字符串
	 * @param list
	 * @return
	 */
	private String getIds(List<UserVo>list){
		UserVo vo;
		int size = list.size();
		StringBuilder strBuf = new StringBuilder();
		for(int i = 0; i < size; i ++){
			vo = list.get(i);
			strBuf.append(vo.getUserid()+"");
			strBuf.append(",");
		}
		if (strBuf.length() > 0)
			return strBuf.substring(0, strBuf.length() - 1);
		else
			return null;
	}
}
