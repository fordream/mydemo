/**      
 * ListActivity.java Create on 2013-10-15     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.utils.ViewUtil;
import com.iwgame.msgs.vo.local.PagerVo;

/**
 * @ClassName: ListActivity
 * @Description: 列表基类
 * @author 王卫
 * @date 2013-10-15 下午02:25:21
 * @Version 1.0
 * 
 */
public class BaseListActivity extends BaseActivity {

	private static final String TAG = "BaseListActivity";
	// 可刷新主容器 
	private PullToRefreshListView mPullRefreshListView;
	// 列表
	protected ListView list;
	// 列表数据源
	protected List listData = new ArrayList<Map<String, Object>>();
	// 数据源适配器
	protected BaseAdapter adapter;
	// 当前页码
	protected long mOffset = Long.MAX_VALUE;
	// 每页大小(默认降序向下取)
	protected int mLimit = -PagerVo.LIMIT;
	// 是否有下一页
	protected boolean hasNext = true;
	// 1最大值从Long.MAX_VALUE开始，0从0开始,2通过传过来的值设置
	protected int offsetMode = 1;
	
	protected boolean onresumeNeedRefresh = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		initialize();
		if(!onresumeNeedRefresh){
			refreshList();
		}
	}

	/**
	 * 初始化 子类设置待列表内容的UI
	 */
	protected void initialize() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(onresumeNeedRefresh){
			refreshList();
		}
	}

	/**
	 * 清除列表数据,重新渲染列表
	 */
	protected void refreshList() {
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
	 * 清除列表数据,重新渲染列表
	 */
	protected void refreshList(long offset) {
		if (listData != null && adapter != null) {
			mOffset = offset;
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
	 * 清除所有数据
	 */
	public void clean() {
		listData.clear();
		if(adapter instanceof ClearCacheListener){
			((ClearCacheListener) adapter).clearCache();
		}
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 设置列表适配器
	 * 
	 * @param adapter
	 */
	protected void setAdapter(SimpleAdapter adapter) {
		if (adapter != null) {
			this.adapter = adapter;
			list.setAdapter(adapter);
		} else {
			return;
		}
	}

	/**
	 * 设置列表和适配器
	 * 
	 * @param adapter
	 */
	protected void setListAndAdapter(ListView list, BaseAdapter adapter) {
		this.list = list;
		this.adapter = adapter;
		if (list != null && adapter != null) {
			list.setAdapter(adapter);
		} else {
			return;
		}
	}

	/**
	 * 获取列表数据(子类实现)
	 * 
	 * @param offset
	 * @param limit
	 */
	protected void getListData(long offset, int limit) {
	}

	/**
	 * 没有数据，显示空背景
	 * 
	 * @param contentView
	 * @param resId
	 */
	protected void showNullBgView(Integer resId) {
		if (listData.size() <= 0) {
			ViewUtil.showNullBgView(this, contentView, resId);
		} else {
			return;
		}
	}

	public void onFooterRefreshComplete() {
		mPullRefreshListView.onRefreshComplete();
	}

	public void onHeaderRefreshComplete(CharSequence lastupdated) {
		mPullRefreshListView.onRefreshComplete();
	}

	public PullToRefreshListView getPullRefreshListView() {
		return mPullRefreshListView;
	}
	
	public void setPullRefreshListView(PullToRefreshListView pullRefreshListView, Mode mode) {
		this.mPullRefreshListView = pullRefreshListView;
		list = mPullRefreshListView.getRefreshableView();
		if(mode == null)
			mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		else
			mPullRefreshListView.setMode(mode);

		final ILoadingLayout footerLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// Do work to refresh the list here.
				if (hasNext)
					getListData(mOffset, mLimit);
				else
					new GetDataTask().execute();
			}
		});
	}

	public void setPullRefreshListView(PullToRefreshListView pullRefreshListView) {
		setPullRefreshListView(pullRefreshListView, null);
	}

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
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(null);
		}
	}

	
	public void setMode(Mode mode){
		if(mPullRefreshListView != null)
		mPullRefreshListView.setMode(mode);
	}
	
	
}
