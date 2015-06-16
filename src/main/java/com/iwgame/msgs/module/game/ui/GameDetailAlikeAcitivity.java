/**      
 * GameDetailAlikeAcitivity.java Create on 2014-5-21     
 *      
 * Copyright (c) 2014 by GreenShore Network
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
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.discover.ui.filter.FilterView;
import com.iwgame.msgs.module.discover.ui.filter.UserFilterView;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GameDetailAlikeAcitivity
 * @Description: 游伴
 * @author 王卫
 * @date 2014-5-21 上午10:28:38
 * @Version 1.0
 * 
 */

public class GameDetailAlikeAcitivity extends BaseActivity {

	protected static final String TAG = "GameDetailAlikeAcitivity";
	// 附近的人内容
	private CommonListView nearView;
	private long gid;
	private UserAdapter2 adapter;

	// 出现的时间
	private Integer selectTime = null;
	// 性别
	private Integer selectSex = null;
	// 用户服务器类型
	private String userSids = null;
	
	private CustomProgressDialog loadDialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadDialog = CustomProgressDialog.createDialog(this);
		loadDialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog不消失
		// 获得传入的参数
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			gid = b.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, 0);
		}
		// 显示左边
		setLeftVisible(true);
		titleTxt.setText("游伴");
		nearView = new CommonListView(this, R.layout.common_filter_list, View.VISIBLE, 0, PagerVo.LIMIT) {

			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				GameDetailAlikeAcitivity.this.adapter.setFlag(true);
				getGameUser(1, offset, limit, this);
			}

		};
		setListItemClikEvent(nearView.list);
		adapter = new UserAdapter2(this, nearView.listData, R.layout.user_list_item_userfragment, new String[] { "nickname", "desc" }, new int[] {
				R.id.nickname, R.id.rdesc }, true, UserAdapter2.GAME_FOLLOW_TYPE_COMMON, true, false, nearView.list);
		nearView.setAdapter(adapter);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(nearView, params);
		nearView.refreshList(0, 20);
		// 添加用户过滤内容
		GameVo gameVo = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext()).getGameById(gid);
		((LinearLayout) nearView.findViewById(R.id.filterContent)).addView(new UserFilterView(this, new UserFilterView.RefreshDataListener() {

			@Override
			public void onRefreshData() {

			}

			@Override
			public void onRefreshGroup(Boolean verify, String sids) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRefreshUser(Integer gender, Integer time, String sids) {
				selectTime = time;
				selectSex = gender;
				userSids = sids;
				nearView.refreshList();
				loadDialog.show();
				
			}
		}, (gameVo != null && gameVo.getGtype() == 0) ? View.GONE : View.VISIBLE, FilterView.MODE_COMMON), new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	protected void onResume() {
		super.onResume();
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 查找人的列表
	 * 
	 * @param mode
	 * @param showDis
	 *            [1系统推荐2共同好友3附近的人]
	 */
	private void getGameUser(Integer source, long offset, int limit, final CommonListView view) {
		Boolean isfate = null;
		Integer rel = null;
		Boolean isrecommend = null;
		String resulttype = "3,4";
		Integer nearDistance = null;
		if (nearView.listData.size() <= 0) {
			nearView.setLoadingUI();
			nearView.setRefreshMode(Mode.DISABLED);
		}
		ProxyFactory.getInstance().getUserProxy().searchUsersBysid(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				if(loadDialog.isShowing())
					loadDialog.dismiss();
				nearView.setRefreshMode(Mode.BOTH);
				if (view.isRefresh) {
					view.onHeaderRefreshComplete();
					view.clean();
				} else {
					view.onFooterRefreshComplete();
				}
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					// 设置LIST数据
					view.offset = result.getOffset();
					view.listData.addAll(praseUserList(result.getItems()));
					if (view.listData.size() > Math.abs(view.limit))
						view.list.setSelectionFromTop(view.list.getFirstVisiblePosition(), 0);
					view.adapter.notifyDataSetChanged();
					view.adapter.notifyDataSetInvalidated();
				} else {
					LogUtil.d(TAG, "数据为空");
				}
				view.showListView();
				if (view.listData.size() <= 0)
					view.showNullBgView(R.drawable.common_no_seach_uers);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if(loadDialog.isShowing())
					loadDialog.dismiss();
				nearView.setRefreshMode(Mode.BOTH);
				LogUtil.e(TAG, "获取用户信息失败");
				if (view.listData.size() <= 0)
					view.showNullBgView(R.drawable.common_no_seach_uers);
				view.onFooterRefreshComplete();
			}
		}, this, gid, isfate, isrecommend, rel, resulttype, null, offset, limit, nearDistance, selectTime, selectSex, null, userSids, source);
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
}
