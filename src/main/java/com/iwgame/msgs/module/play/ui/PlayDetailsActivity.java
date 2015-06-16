package com.iwgame.msgs.module.play.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.play.adapter.PlayDetailsRefreshListAdapter;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

public class PlayDetailsActivity extends BaseActivity implements OnClickListener {

	// 初始化页码
	private static long OFFSET_INIT = 0;
	// 初始化每页大小(默认降序向下取)
	private static int LIMIT_INIT = PagerVo.LIMIT;
	
	private View view;
	private PullToRefreshListView createPlayDetalisRefreshList;
	private PlayDetailsRefreshListAdapter playDetailsRefreshListAdapter;
	private CustomProgressDialog loadDialog;
	
	// 列表
	public ListView list;
	private final List<PlayInfo> listData = new ArrayList<PlayInfo>();
	
	private Long uid;
	
	
	// 当前页码
	public long offset = OFFSET_INIT;
	// 每页大小(默认降序向下取)
	public int limit = LIMIT_INIT;
	// 是否有下一页
	public boolean hasNext = true;
	// 是否重置刷新页面
	public boolean isRefresh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadDialog = CustomProgressDialog.createDialog(PlayDetailsActivity.this);
		loadDialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog不消失
		getData();
		initView();
	}

	private void getData() {
		uid = getIntent().getExtras().getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initView() {
		setTitleTxt("游戏陪玩");
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view = View.inflate(this, R.layout.play_details, null);
		contentView.addView(view, params);
		createPlayDetalisRefreshList = (PullToRefreshListView) view.findViewById(R.id.play_details_refreshlist);
		playDetailsRefreshListAdapter = new PlayDetailsRefreshListAdapter(getApplicationContext(), listData);
		createPlayDetalisRefreshList.setAdapter(playDetailsRefreshListAdapter);
		createPlayDetalisRefreshList.setMode(Mode.BOTH);
		
		createPlayDetalisRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(PlayDetailsActivity.this, PlayDetailInfoActivity.class);
				Bundle budle  = new Bundle();
				budle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, listData.get((int)id).getPlayid());
				budle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, PlayDetailInfoActivity.MODE_1);
				intent.putExtras(budle);
				startActivity(intent);
			}
		});
		
		
		final ILoadingLayout headerLabels = createPlayDetalisRefreshList.getLoadingLayoutProxy(true, false);
		final ILoadingLayout footerLabels = createPlayDetalisRefreshList.getLoadingLayoutProxy(false, true);
		headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		
		createPlayDetalisRefreshList.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
				refreshList(OFFSET_INIT, LIMIT_INIT);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				if (hasNext)
					getListData(offset, limit);
				else
					new GetDataTask().execute();
			}
			});
		loadDialog.show();
		refreshList(OFFSET_INIT, LIMIT_INIT);
		
	}
	
	


	private void refreshList(long offset, int limit) {
		if (listData != null && playDetailsRefreshListAdapter != null) {
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
		playDetailsRefreshListAdapter.notifyDataSetChanged();
		playDetailsRefreshListAdapter.notifyDataSetInvalidated();
	}
	
	private void getListData(long offset, int limit) {
		if(!NetworkUtil.isNetworkAvailable(getApplicationContext())){
			ToastUtil.showToast(getApplicationContext(), "当前网络已经断开！");
			return;
		}
		
		ProxyFactory.getInstance().getPlayProxy().searchCreatPlays(new ProxyCallBack<PagerVo<PlayInfo>>() {

					@Override
					public void onSuccess(PagerVo<PlayInfo> result) {
						LogUtil.info("lll onSuccess playInfoList size = " +result.getItems().size() );
						clean();
						listData.addAll(result.getItems());
						playDetailsRefreshListAdapter.notifyDataSetChanged();
						playDetailsRefreshListAdapter.notifyDataSetInvalidated();
						if(loadDialog.isShowing()){
							loadDialog.dismiss();
						}
						createPlayDetalisRefreshList.onRefreshComplete();
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						LogUtil.info("lll onFailure result  = " +result+ ", resultMsg = " + resultMsg);
						createPlayDetalisRefreshList.onRefreshComplete();
					}

				}, getApplicationContext(), uid, MsgsConstants.PLAY_STATUS_PUBLISH , offset, limit);
		
	}


	@Override
	public void onClick(View v) {

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
			createPlayDetalisRefreshList.onRefreshComplete();
			super.onPostExecute(null);
		}
	}

}
