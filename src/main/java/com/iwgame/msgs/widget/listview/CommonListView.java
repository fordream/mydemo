/**      
 * CommonListView.java Create on 2014-1-21     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget.listview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.ClearCacheListener;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: CommonListView
 * @Description: 通用列表界面
 * @author 王卫
 * @date 2014-1-21 下午6:39:29
 * @Version 1.0
 * 
 */
public class CommonListView extends LinearLayout {
	// 初始化页码
	private static long OFFSET_INIT = 0;
	// 初始化每页大小(默认降序向下取)
	private static int LIMIT_INIT = PagerVo.LIMIT;

	// 可刷新主容器
	private PullToRefreshListView mPullRefreshListView;

	public PullToRefreshListView getmPullRefreshListView() {
		return mPullRefreshListView;
	}

	public void setmPullRefreshListView(PullToRefreshListView mPullRefreshListView) {
		this.mPullRefreshListView = mPullRefreshListView;
	}

	// 列表
	public ListView list;
	// 列表数据源
	@SuppressWarnings("rawtypes")
	public List listData = new ArrayList<Map<String, Object>>();
	// 数据源适配器
	public BaseAdapter adapter;
	// 当前页码
	public long offset = OFFSET_INIT;
	// 每页大小(默认降序向下取)
	public int limit = LIMIT_INIT;
	// 是否有下一页
	public boolean hasNext = true;
	// 是否重置刷新页面
	public boolean isRefresh;
	//陪玩上一次刷新结果
	public boolean playRefresh = true;

	protected LinearLayout listContent;
	public LinearLayout nullContent;
	private ImageView bgIcon;
	private LinearLayout nullbgLayout;

	/**
	 * @param context
	 */
	public CommonListView(Context context, int paging) {
		super(context);
		setListView(null, paging);
	}

	/**
	 * @param context
	 */
	public CommonListView(Context context, Integer listId, int paging, long intiOffset, int initLimit) {
		super(context);
		OFFSET_INIT = intiOffset;
		LIMIT_INIT = initLimit;
		this.offset = OFFSET_INIT;
		this.limit = LIMIT_INIT;
		setListView(listId, paging);
	}

	/**
	 * @param context
	 */
	public CommonListView(Context context, Integer listId, int paging) {
		super(context);
		init(context, listId, paging, false);
	}

	/**
	 * 
	 * @param context
	 * @param listId
	 * @param paging
	 * @param isobject
	 */
	public CommonListView(Context context, Integer listId, int paging, boolean isobject, boolean playRefresh) {
		super(context);
		this.playRefresh = playRefresh;
		init(context, listId, paging, isobject);
	}

	/**
	 * @param context
	 */
	public CommonListView(Context context, int paging, boolean isobject) {
		super(context);
		init(context, null, paging, isobject);
	}
	
	private void init(Context context, Integer listId, int paging, boolean isobject){
		if (isobject)
			listData = new ArrayList<Object>();
		setListView(listId, paging);
	}

	/**
	 * 设置列表
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setListView(Integer listId, int paging) {
		if (listId == null)
			listId = R.layout.common_list;
		View view = View.inflate(getContext(), listId, null);
		android.view.ViewGroup.LayoutParams params = null;
		if (view instanceof FrameLayout) {
			params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		} else if (view instanceof LinearLayout) {
			params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		} else if (view instanceof RelativeLayout) {
			params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		this.addView(view, params);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		mPullRefreshListView.setMode(Mode.BOTH);
		final ILoadingLayout headerLabels = mPullRefreshListView.getLoadingLayoutProxy(true, false);
		final ILoadingLayout footerLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
		headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
				refreshList(OFFSET_INIT, LIMIT_INIT);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// Do work to refresh the list here.
				if (hasNext)
					getListData(offset, limit);
				else
					new GetDataTask().execute();
			}
		});

		list = mPullRefreshListView.getRefreshableView();
		if (paging == View.VISIBLE) {
			refreshList(OFFSET_INIT, LIMIT_INIT);
		} else {
			mPullRefreshListView.setMode(Mode.DISABLED);
		}
		listContent = (LinearLayout) view.findViewById(R.id.listContent);
		nullContent = (LinearLayout) view.findViewById(R.id.nullContent);
		bgIcon = (ImageView) view.findViewById(R.id.bgIcon);
		nullbgLayout = (LinearLayout) view.findViewById(R.id.bg_layout);
	}

	/**
	 * 清除列表数据,重新渲染列表
	 */
	public void refreshList() {
		if (listData != null && adapter != null) {
			offset = 0;
			hasNext = true;
			isRefresh = true;
			getListData(offset, limit);
		} else {
			return;
		}
	}

	/**
	 * 清除列表数据,重新渲染列表
	 */
	public void refreshList(long offset, int limit) {
		if (listData != null && adapter != null) {
			this.offset = offset;
			this.limit = limit;
			hasNext = true;
			isRefresh = true;
			getListData(offset, limit);
		} else {
			return;
		}
	}

	/**
	 * 清除所有数据
	 */
	public void clean() {
		isRefresh = false;
		listData.clear();
		if (adapter instanceof ClearCacheListener) {
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
	public void setAdapter(BaseAdapter adapter) {
		if (adapter != null) {
			this.adapter = adapter;
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
	public void getListData(long offset, int limit) {
		if(!NetworkUtil.isNetworkAvailable(getContext())){
			ToastUtil.showToast(getContext(), "当前网络已经断开！");
			return;
		}
		showListView();
	}

	public void onHeaderRefresh() {
		refreshList(OFFSET_INIT, LIMIT_INIT);
	}

	public void showNullBgView() {
		nullContent.setVisibility(View.VISIBLE);
		if (bgIcon != null)
			bgIcon.setBackgroundResource(R.drawable.common_no_seach_result);
		nullContent.removeAllViews();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if (nullbgLayout != null)
			nullContent.addView(nullbgLayout, params);
		listContent.setVisibility(View.GONE);
	}

	public void showNullBgView(Integer resid) {
		if (resid != null)
			bgIcon.setBackgroundResource(resid);
		nullContent.setVisibility(View.VISIBLE);
		nullContent.removeAllViews();
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if (nullbgLayout != null)
			nullContent.addView(nullbgLayout, params);
		listContent.setVisibility(View.GONE);
	}

	public void showListView() {
		nullContent.setVisibility(View.GONE);
		listContent.setVisibility(View.VISIBLE);
	}

	public void onFooterRefreshComplete() {
		mPullRefreshListView.onRefreshComplete();
	}

	public void onHeaderRefreshComplete() {
		mPullRefreshListView.onRefreshComplete();
	}

	public LinearLayout getNullContent() {
		return nullContent;
	}

	public void setNullContent(LinearLayout nullContent) {
		this.nullContent = nullContent;
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

	/**
	 * 设置刷新模式
	 * 
	 * @param mode
	 */
	public void setRefreshMode(Mode mode) {
		mPullRefreshListView.setMode(mode);
	}

	/**
	 * 显示加载的loading
	 */
	public void setLoadingUI() {
		showNullBgView();
		nullContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(getContext(), R.layout.frame_donghua, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		nullContent.addView(view, params);
	}

	/**
	 * 设置没有数据的时候显示的UI
	 */
	public void setNoDataView() {
		showNullBgView();
		nullContent.removeAllViews();
		final LinearLayout view = (LinearLayout) View.inflate(getContext(), R.layout.user_null_data_bg, null);
		((TextView) view.findViewById(R.id.desc)).setText("还木有粉丝哦，加油！");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		nullContent.addView(view, params);
	}

	/**
	 * 显示加载的符号
	 */
	public void setNoDataUI() {
		showNullBgView();
		nullContent.removeAllViews();
		final LinearLayout view = (LinearLayout) View.inflate(getContext(), R.layout.user_null_data_bg, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		nullContent.addView(view, params);
	}
}
