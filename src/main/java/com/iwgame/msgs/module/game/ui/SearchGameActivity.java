/**      
 * FollowSearchGameActivity.java Create on 2013-9-5     
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.game.adapter.CommonGameAdapter;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.utils.ViewUtil;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: FollowSearchGameActivity
 * @Description: 搜索贴吧添加关注
 * @author 王卫
 * @date 2013-9-5 下午04:27:43
 * @Version 1.0
 * 
 */
public class SearchGameActivity extends BaseListActivity {

	protected static final String TAG = "SearchGameActivity";
	private String keyword;
	private String gids = "";

	private LinearLayout listContent;

	/**
	 * 
	 */
	@Override
	protected void initialize() {
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			keyword = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_KEYWORD);
		}
		// 显示左边
		setLeftVisible(true);
		// 显示右边
		setRightVisible(false);
		titleTxt.setText("搜索结果");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (LinearLayout) View.inflate(this, R.layout.main_search_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		listContent = (LinearLayout) view.findViewById(R.id.listContent);
		// 获取列表
		setPullRefreshListView((PullToRefreshListView) findViewById(R.id.refreshList));
		CommonGameAdapter adapter = new CommonGameAdapter(SearchGameActivity.this, listData, R.layout.game_list_item, new String[] { "gamename" },
				new int[] { R.id.gamename }, 0, CommonGameAdapter.MODE_COMMON);
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

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
	 * @see com.iwgame.msgs.common.BaseListActivity2#getListData(long, int)
	 */
	@Override
	protected void getListData(long offset, int limit) {
		super.getListData(offset, limit);
		hasNext = false;
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getGameProxy().searchGameBykeyword(new ProxyCallBack<List<ExtGameVo>>() {

			@Override
			public void onSuccess(List<ExtGameVo> result) {
				if (result != null && result.size() > 0) {
					// 添加设置LIST数据
					addListData(result);
				} else {
					ViewUtil.showNullBgView(SearchGameActivity.this, listContent, R.drawable.common_no_seach_postbar);
					hasNext = false;
					LogUtil.d(TAG, "数据为空");
				}
				dialog.dismiss();
				onFooterRefreshComplete();
				getGameFollowCount();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ViewUtil.showNullBgView(SearchGameActivity.this, listContent, R.drawable.common_no_seach_postbar);
				dialog.dismiss();
				onFooterRefreshComplete();
			}
		}, this, keyword, 0, 100);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(List<ExtGameVo> result) {
		// 添加设置LIST数据
		listData.addAll(praseList(result));
		if (result != null && result.size() > Math.abs(mLimit)) {
			list.setSelectionFromTop(list.getFirstVisiblePosition(), 0);
		}
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}
	
	/**
	 * 批量获取贴吧关注数
	 */
	private void getGameFollowCount(){
		if(gids != null && !gids.isEmpty()){
			ProxyFactory.getInstance().getGameProxy().searchGameFollowCount(new ProxyCallBack<HashMap<Long,Long>>() {
				
				@Override
				public void onSuccess(HashMap<Long, Long> result) {
					if(result == null){
						return;
					}
					for(int i = 0 ; i < listData.size() ; i++){
						Map<String, Object> map = (Map<String, Object>) listData.get(i);
						long gid = (Long) map.get("gid");
						if(result.containsKey(gid) && result.get(gid) != 0){
							map.put("desc3", "有" + result.get(gid) + "个用户已关注");
						}
					}
					adapter.notifyDataSetChanged();
				}
				
				@Override
				public void onFailure(Integer result, String resultMsg) {
					
				}
			}, this, gids);
		}
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
			
			if(i != list.size() - 1){
				gids = gids + vo.getGameid() + ",";
			}else{
				gids = gids + vo.getGameid();
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
			map.put("desc3", "");
			tmplist.add(map);
		}
		return tmplist;
	}

}
