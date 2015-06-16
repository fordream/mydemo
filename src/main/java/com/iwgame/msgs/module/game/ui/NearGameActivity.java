/**      
 * FollowNearGameActivity.java Create on 2013-9-5     
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

import com.youban.msgs.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.game.adapter.CommonGameAdapter;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: FollowNearGameActivity
 * @Description: 附近的贴吧
 * @author 王卫
 * @date 2013-9-5 下午04:24:08
 * @Version 1.0
 * 
 */
public class NearGameActivity extends BaseListActivity {
	protected static final String TAG = "NearGameActivity";

	/**
	 * 初始化
	 */
	protected void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 影藏右边
		setRightVisible(false);
		titleTxt.setText("附近贴吧");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (LinearLayout) View.inflate(this, R.layout.main_search_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		// 获取列表
		setPullRefreshListView((PullToRefreshListView) findViewById(R.id.refreshList));
		// 添加列表点击功能
		CommonGameAdapter adapter = new CommonGameAdapter(NearGameActivity.this, listData, R.layout.game_list_item, new String[] { "gamename",
				"distance" }, new int[] { R.id.gamename, R.id.rdesc }, 0, CommonGameAdapter.MODE_COMMON);
		setListAndAdapter(list, adapter);
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

	/**
	 * 查找附近的人玩的贴吧列表
	 */
	@Override
	protected void getListData(final long offset, int limit) {
		super.getListData(offset, limit);
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getGameProxy().getConditionGame(new ProxyCallBack<PagerVo<ExtGameVo>>() {

			@Override
			public void onSuccess(PagerVo<ExtGameVo> result) {
				if (offset == 0 || offset >= Integer.MAX_VALUE)
					clean();
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					// 添加设置LIST数据
					addListData(result);
				} else {
					showNullBgView(null);
					hasNext = false;
					LogUtil.d(TAG, "数据为空");
				}
				dialog.dismiss();
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				showNullBgView(null);
				LogUtil.e(TAG, "查找附近的贴吧失败");
				dialog.dismiss();
				onFooterRefreshComplete();
			}
		}, this, null, null, null, "2,4", null, offset, limit, null, SystemContext.getInstance().getNearDistance(), null, null, null,null);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(PagerVo<ExtGameVo> pageVo) {
		mOffset = pageVo.getOffset();
		// 添加设置LIST数据
		listData.addAll(praseGameList(pageVo.getItems()));
		if (pageVo.getItems() != null) {
			if (pageVo.getItems().size() < Math.abs(mLimit)) {
				hasNext = false;
			}
			if (pageVo.getItems().size() > Math.abs(mLimit)) {
				list.setSelectionFromTop(list.getFirstVisiblePosition(), 0);
			}
		}
		mOffset = Long.MAX_VALUE;
		if (listData != null && listData.size() > 0 && ((Map<String, Object>)listData.get(listData.size() - 1)).get("gid") != null) {
			mOffset = (Long) ((Map<String, Object>)listData.get(listData.size() - 1)).get("gid");
		}
		if (pageVo.getItems() != null) {
			if (pageVo.getItems().size() < Math.abs(mLimit)) {
				hasNext = false;
			}
			if (pageVo.getItems().size() > 0) {
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
	private List<Map<String, Object>> praseGameList(List<ExtGameVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			ExtGameVo vo = list.get(i);
			map.put("logo", vo.getGamelogo());
			map.put("gamename", vo.getGamename());
			map.put("follow", vo.getFollow());
			map.put("gid", vo.getGameid());
			map.put("distance", DistanceUtil.covertDistance(vo.getDistance()));
			if (vo.getNickname() != null && !vo.getNickname().isEmpty()) {
				map.put("desc3", "附近" + vo.getUserCount() + "个好友关注");
			} else {
				map.put("desc3", "附近没有好友关注");
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
