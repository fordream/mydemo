/**      
 * ApplyListFragment.java Create on 2015-5-11     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.play.adapter.ReplyPlayListAdapter;
import com.iwgame.msgs.proto.Msgs.PlayApplyOrderInfo;
import com.iwgame.msgs.proto.Msgs.PlayOrderInfo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ApplyListFragment
 * @Description: 我报名的陪玩列表
 * @author 王卫
 * @date 2015-5-11 下午3:24:43
 * @Version 1.0
 * 
 */
public class ApplyListFragment extends BaseFragment {

	protected static final String TAG = "ApplyListFragment";
	private CommonListView listContentView;
	private ReplyPlayListAdapter adapter;
	private CustomProgressDialog loadDialog;
	private boolean isInit = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		if (listContentView != null && listContentView.listData.size() <= 0) {
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
		listContentView = new CommonListView(getActivity(), View.VISIBLE, true) {
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				if(isInit){
					loadDialog.show();
					isInit = false;
				}
				getData();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.widget.listview.CommonListView#showNullBgView()
			 */
			@Override
			public void showNullBgView() {
				listContent.setVisibility(View.GONE);
				nullContent.setVisibility(View.VISIBLE);
			}
		};
		listContentView.nullContent.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout v = (LinearLayout) View.inflate(getActivity(), R.layout.play_apply_list_apply_play_bgview, null);
		listContentView.nullContent.addView(v, params);
		v.findViewById(R.id.applyPlay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到发现陪玩界面
				Intent intent = new Intent(getActivity(), MainFragmentActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_INDEX, 1);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		// 添加列表点击功能
		listContentView.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int realposition = (int) parent.getAdapter().getItemId(position);
				Object object = listContentView.listData.get(realposition);
				if (realposition != -1 && object != null && object instanceof PlayOrderInfo) {
					Intent intent = new Intent(parent.getContext(), PlayOrderDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_OID, ((PlayOrderInfo) object).getId());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		adapter = new ReplyPlayListAdapter(getActivity(), listContentView.listData);
		listContentView.setAdapter(adapter);
		return listContentView;
	}

	/**
	 * 获取数据
	 */
	private void getData() {
		ProxyFactory.getInstance().getPlayProxy().searchApplyPlays(new ProxyCallBack<PagerVo<PlayOrderInfo>>() {

			@Override
			public void onSuccess(PagerVo<PlayOrderInfo> result) {
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
				}
				if (listContentView.listData.size() <= 0) {
					listContentView.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					listContentView.listData.clear();
				} else {
					listContentView.setRefreshMode(Mode.BOTH);
				}
				listContentView.adapter.notifyDataSetChanged();
				listContentView.offset = listContentView.listData.size();
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
				} else {
					listContentView.setRefreshMode(Mode.BOTH);
					listContentView.showListView();
				}
				listContentView.adapter.notifyDataSetChanged();
			}
		}, getActivity(), listContentView.offset, listContentView.limit);
	}

}
