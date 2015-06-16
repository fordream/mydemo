/**      
 * GroupUserListActivity.java Create on 2014-9-10     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */ 

package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.List;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.chat.adapter.GroupChatUserAdapter;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.youban.msgs.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

/** 
 * @ClassName: GroupUserListActivity 
 * @Description: TODO(...) 
 * @author zhangjianchuan
 * @date 2014-9-10 下午1:56:52 
 * @Version 1.0
 * 
 */
public class GroupUserListActivity extends BaseActivity {

	private long grid;
	private PullToRefreshListView refreshlist;
	private ListView lv;
	private List<UserItemObj> list = new ArrayList<UserItemObj>();
	private GroupChatUserAdapter adapter;
	private boolean hasNext = true;
    private GroupUserRelVo currentRel;
    private boolean isPres;

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		grid = getIntent().getExtras().getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
		isPres = getIntent().getExtras().getBoolean(SystemConfig.SHAREDPREFERENCES_STOP_SAY_WORDS, false);
		init();
	}

	/**
	 * 获取列表数据(子类实现)
	 * 
	 * @param offset
	 * @param limit
	 */
	protected void getListData() {
		ProxyFactory.getInstance().getGroupProxy().getGroupContributePointTop(new ProxyCallBack<List<UserItemObj>>() {
			
			@Override
			public void onSuccess(List<UserItemObj> result) {
				if (result != null && result.size() > 0) {
					hasNext = true;
					list.addAll(result);
					adapter.notifyDataSetChanged();
				}else{
					hasNext = false;
				}
				new GetDataTask().execute();
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				new GetDataTask().execute();
			}
		}, GroupUserListActivity.this, grid,Integer.MAX_VALUE);
	}

	/**
	 * 初始化操作
	 */
	private void init() {
		setTitleTxt("公会成员列表");
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		View child = View.inflate(this, R.layout.group_user_list_layout, null);
		refreshlist = (PullToRefreshListView)child.findViewById(R.id.refreshList);
		getContentView().addView(child, params);
		setPullRefreshListView(refreshlist, Mode.DISABLED);
		//adapter 的初始化  groupchatUseradapter
		GroupDao dao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		GroupVo vo = dao.findGroupByGrid(grid);
		adapter = new GroupChatUserAdapter(this, list, currentRel,lv);
		lv.setAdapter(adapter);
        refreshList();
        setListener();
	}

	/**
	 * 给listview添加事件
	 */
	private void setListener() {
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(list.get(arg2-1).getUid() == SystemContext.getInstance().getExtUserVo().getUserid()) return;
				//跳转到详细界面
				Intent intent = new Intent(GroupUserListActivity.this,UserDetailInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, list.get(arg2-1).getUid());
				if(isPres){
					bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_STOP_SAY_WORDS, true);
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
				}
				bundle.putBoolean(SystemConfig.IF_NOT_SHOW_GROUP_CARD, true);
				bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GROUP_CARD, list.get(arg2-1).getRemark());
				intent.putExtras(bundle);
				GroupUserListActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * 清除列表数据,重新渲染列表
	 */
	protected void refreshList() {
		if (list != null && adapter != null) {
			hasNext = true;
			getListData();
		} else {
			return;
		}
	}
	
	/**
	 * 设置上拉刷新
	 * 和下拉刷新的一些相关属性
	 * @param pullRefreshListView
	 * @param mode
	 */
	public void setPullRefreshListView(PullToRefreshListView pullRefreshListView,Mode mode) {
		this.refreshlist = pullRefreshListView;
		lv = refreshlist.getRefreshableView();
		lv.setDivider(getResources().getDrawable(R.drawable.common_fengexian));
		if(mode == null)
			refreshlist.setMode(Mode.BOTH);
		else
			refreshlist.setMode(mode);
		final ILoadingLayout footerLabels = refreshlist.getLoadingLayoutProxy(false, true);
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		refreshlist.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				if (hasNext)
					getListData();
				else
					new GetDataTask().execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// Do work to refresh the list here.
				if (hasNext)
					getListData();
				else
					new GetDataTask().execute();
			}
		});
	}
	
	/**
	 * 定义了一个内部类，
	 * 当刷新完成的 时候要记得
	 * 取消旋转的dialog
	* @ClassName: GetDataTask 
	* @Description: TODO(...) 
	* @author zhangjianchuan
	* @date 2014-9-10 下午2:58:29 
	* @Version 1.0
	*
	 */
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			refreshlist.onRefreshComplete();
			super.onPostExecute(null);
		}
	}
}
