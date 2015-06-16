/**      
 * PlayManageBaseListFragment.java Create on 2015-5-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.play.adapter.PlayManageListAdapter;
import com.iwgame.msgs.proto.Msgs.PlayApplyOrderInfo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: PlayManageBaseListFragment
 * @Description: 陪玩报名管理列表基类
 * @author 王卫
 * @date 2015-5-15 下午4:37:21
 * @Version 1.0
 * 
 */
public class PlayManageBaseListFragment extends BaseFragment {

	protected static final String TAG = "PlayManageBaseListFragment";
	private CommonListView listContentView;
	private PlayManageListAdapter adapter;
	private CustomProgressDialog loadDialog;

	private String orderStatus;
	private int index;
	private long pid;
	private boolean isinit = true;
	// 加载中
	private boolean loading = false;
	// 陪玩状态和玩家封停状态
	private int pStatus;
	private int uStatus;
	private String gameName;

	/**
	 * 设置状态
	 * 
	 * @param orderStatus
	 * @param pid
	 */
	public void setData(String orderStatus, long pid, int index, int pStatus, int uStatus, String gameName) {
		this.orderStatus = orderStatus;
		this.pid = pid;
		this.index = index;
		this.pStatus = pStatus;
		this.uStatus = uStatus;
		this.gameName = gameName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (loadDialog == null)
			loadDialog = CustomProgressDialog.createDialog(getActivity());
		return init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseFragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (listContentView != null && listContentView.listData.size() <= 0 && !loading) {
			listContentView.onHeaderRefresh();
		} else if (listContentView != null) {
			listContentView.setRefreshMode(Mode.BOTH);
			listContentView.adapter.notifyDataSetChanged();
			listContentView.adapter.notifyDataSetInvalidated();
		}
	}

	/**
	 * 
	 */
	private View init() {
		if (listContentView == null) {
			listContentView = new CommonListView(getActivity(), View.VISIBLE, true) {
				@Override
				public void getListData(long offset, int limit) {
					super.getListData(offset, limit);
					getData();
				}

			};
			// 添加列表点击功能
			listContentView.list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					int realposition = (int) parent.getAdapter().getItemId(position);
					Object object = listContentView.listData.get(realposition);
					if (realposition != -1 && object != null && object instanceof PlayApplyOrderInfo) {
						Intent intent = new Intent(parent.getContext(), PlayOrderDetailActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_OID, ((PlayApplyOrderInfo) object).getOrderid());
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			});
			adapter = new PlayManageListAdapter(getActivity(), listContentView.listData, index, pStatus, uStatus, gameName);
			listContentView.setAdapter(adapter);
		}
		ViewGroup parent = (ViewGroup) listContentView.getParent();
		if (parent != null)
			parent.removeAllViews();
		return listContentView;
	}

	/**
	 * 
	 */
	private void getData() {
		if (loading)
			return;
		loading = true;
		if (isinit) {
			loadDialog.show();
			isinit = false;
		}
		ProxyFactory.getInstance().getPlayProxy().searchApplyOrder(new ProxyCallBack<PagerVo<PlayApplyOrderInfo>>() {

			@Override
			public void onSuccess(PagerVo<PlayApplyOrderInfo> result) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (listContentView.isRefresh) {
					listContentView.clean();
					listContentView.onHeaderRefreshComplete();
				} else {
					listContentView.onFooterRefreshComplete();
				}
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					if (result.getItems().size() < listContentView.limit)
						listContentView.hasNext = false;
					listContentView.listData.addAll(result.getItems());
					listContentView.showListView();
				} else {
					LogUtil.d(TAG, "数据为空");
					if (listContentView.listData.size() <= 0)
						listContentView.showNullBgView();
					// 显示TAB标签总数
					FragmentActivity factivity = getActivity();
					if (factivity != null)
						((PlayManageActivity) factivity).setTextView(0, index);
				}
				if (listContentView.listData.size() <= 0) {
					listContentView.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					listContentView.listData.clear();
					// 显示TAB标签总数
					FragmentActivity factivity = getActivity();
					if (factivity != null)
						((PlayManageActivity) factivity).setTextView(0, index);
				} else {
					listContentView.setRefreshMode(Mode.BOTH);
				}
				listContentView.adapter.notifyDataSetChanged();
				listContentView.offset = listContentView.listData.size();
				loading = false;
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (listContentView.isRefresh) {
					listContentView.onHeaderRefreshComplete();
				} else {
					listContentView.onFooterRefreshComplete();
				}
				if (listContentView.listData.size() <= 0) {
					listContentView.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					listContentView.listData.clear();
					LogUtil.d(TAG, "数据为空");
					if (listContentView.listData.size() <= 0)
						listContentView.showNullBgView();
					// 显示TAB标签总数
					FragmentActivity factivity = getActivity();
					if (factivity != null)
						((PlayManageActivity) factivity).setTextView(0, index);
				} else {
					listContentView.setRefreshMode(Mode.BOTH);
					listContentView.showListView();
				}
				listContentView.adapter.notifyDataSetChanged();
				loading = false;
			}
		}, getActivity(), pid, orderStatus, listContentView.offset, listContentView.limit);
	}

}
