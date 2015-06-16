/**      
 * CreatListFragment.java Create on 2015-5-11     
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
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.play.adapter.CreatPlayListAdapter;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: CreatListFragment
 * @Description: 我创建的陪玩列表
 * @author 王卫
 * @date 2015-5-11 下午3:24:00
 * @Version 1.0
 * 
 */
public class CreatListFragment extends BaseFragment {

	protected static final String TAG = "CreatListFragment";
	private CommonListView listContentView;
	private CreatPlayListAdapter adapter;
	private CustomProgressDialog loadDialog;
	private boolean isInit = true;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadDialog = CustomProgressDialog.createDialog(getActivity());
		loadDialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog不消失
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
		LinearLayout v = (LinearLayout) View.inflate(getActivity(), R.layout.play_creat_list_creat_play_bgview, null);
		listContentView.nullContent.addView(v, params);
		v.findViewById(R.id.creatPlay).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到创建陪玩
				Intent intent = new Intent(getActivity(), CreateUserPlayActivity.class);
				startActivity(intent);
			}
		});
		// 添加列表点击功能
		listContentView.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int realposition = (int) parent.getAdapter().getItemId(position);
				Object object = listContentView.listData.get(realposition);
				if (realposition != -1 && object != null && object instanceof PlayInfo) {
					Intent intent = new Intent(parent.getContext(), PlayManageActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, ((PlayInfo) object).getPlayid());
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PSTATUS, ((PlayInfo) object).getStatus());
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USTATUS, ((PlayInfo) object).getUserPlayStatus());
					bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, ((PlayInfo) object).getGamename());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		adapter = new CreatPlayListAdapter(getActivity(), listContentView.listData);
		listContentView.setAdapter(adapter);
		return listContentView;
	}

	/**
	 * 
	 */
	private void getData() {
		ProxyFactory.getInstance().getPlayProxy().searchCreatPlays(new ProxyCallBack<PagerVo<PlayInfo>>() {

			@Override
			public void onSuccess(PagerVo<PlayInfo> result) {
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
		}, getActivity(), null, null, listContentView.offset, listContentView.limit);
	}
}
