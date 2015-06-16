package com.iwgame.msgs.module.setting.ui;

import java.util.ArrayList;
import java.util.List;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.setting.adapter.ChangeRecordsAdapter;
import com.iwgame.msgs.module.setting.vo.ChangeRecordsEntity;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.utils.DateUtil;
import com.youban.msgs.R;

/**
 * 兑换记录界面
 * @author jczhang
 *
 */
public class ChangeRecordsActivity extends BaseListActivity{

	private PullToRefreshListView pullToRefreshListView;
    private int PULL_UP = 1;//向上拉
    private int PULL_DOWN = 0;//下拉 
	private Dialog detailDialog;
	private RelativeLayout noGoods;
	
	/**
	 * 当程序一启动的时候
	 * 就执行下面的这个方法 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	/**
	 * 初始化操作
	 * 初始化里面的一些控件，
	 * 以及事件方法 
	 */
	private void init() {
		titleTxt.setText("兑换记录");
		setLeftVisible(true);
		setRightVisible(false);
		View view = (LinearLayout) View.inflate(this, R.layout.change_records_activity, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(view, params);
		noGoods = (RelativeLayout)view.findViewById(R.id.no_goods);
		// 获取列表
		pullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.refreshList);
		setPullRefreshListView((PullToRefreshListView) view.findViewById(R.id.refreshList),Mode.BOTH);
		// 添加列表点击功能
		listData = new ArrayList<ChangeRecordsEntity>();
		list.setDivider(null);
	    detailDialog = new Dialog(this,R.style.SampleTheme_Light);
		setListAndAdapter(list,new ChangeRecordsAdapter(this, listData, detailDialog));
	}

	/**
	 * 查询个人用户
	 * 的积分明细
	 * 显示在手机界面上
	 */
	protected void getListData(long off, final int lim,final int type) {
		ProxyFactory.getInstance().getUserProxy().getChangeRecords(new ProxyCallBack<List<ChangeRecordsEntity>>() {

			@Override
			public void onSuccess(List<ChangeRecordsEntity> result) {
				pullToRefreshListView.onRefreshComplete();
				if(type == PULL_UP){
					if(result == null){
						hasNext = false;
					}else{
						mOffset = mOffset + mLimit;
						listData.addAll(result);
						adapter.notifyDataSetChanged();
						adapter.notifyDataSetInvalidated();
						if(result != null && result.size() >= mLimit){
							hasNext = true;
						}else{
							hasNext = false;
						}
					}
				}else if(type == PULL_DOWN){
					if(result != null && result.size() > 0){
						mOffset = result.size();
						listData.clear();
						listData.addAll(result);
						hasNext = true;
						adapter.notifyDataSetChanged();
						adapter.notifyDataSetInvalidated();
					}
				}
				if(listData != null && listData.size() <= 0){
					noGoods.setVisibility(View.VISIBLE);
					pullToRefreshListView.setVisibility(View.GONE);
				}else{
					noGoods.setVisibility(View.GONE);
					pullToRefreshListView.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(ChangeRecordsActivity.this, result, resultMsg);
				pullToRefreshListView.onRefreshComplete();
			}
		}, this, (int)off, lim);
	}

	/**
	 * 重写了父类的方法
	 * 这样可以更改去分页查询的时候
	 * 每页查询多少条数据
	 * 从什么地方开始去查询
	 */
	@Override
	protected void refreshList() {
		if (listData != null && adapter != null) {
			if(listData.size() <= 0){
				mOffset = 0;
				mLimit = 10;
				hasNext = true;
				listData.clear();
				adapter.notifyDataSetChanged();
				adapter.notifyDataSetInvalidated();
				getListData(mOffset, mLimit,PULL_UP);
			}
		} else {
			return;
		}
	}

	/**
	 * 当请求数据失败了以后
	 * 返回来是执行onfailure方法时
	 * 在这个方法里面处理这个错误码
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i,String msg){
		ErrorCodeUtil.handleErrorCode(ChangeRecordsActivity.this, i, msg);
	}

	/**
	 * 该方法是
	 * 初始化上拉刷新
	 * 下拉刷新的控件
	 */
	public void setPullRefreshListView(PullToRefreshListView pullRefreshListView, Mode mode) {
		this.pullToRefreshListView = pullRefreshListView;
		list = this.pullToRefreshListView.getRefreshableView();
		if(mode == null)
			pullRefreshListView.setMode(Mode.BOTH);
		else
			pullRefreshListView.setMode(mode);

		final ILoadingLayout headerLabels = pullRefreshListView.getLoadingLayoutProxy(true, false);
		final ILoadingLayout footerLabels = pullRefreshListView.getLoadingLayoutProxy(false, true);
		headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		// Set a listener to be invoked when the list should be refreshed.
		pullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				getListData(0, mLimit, PULL_DOWN);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// Do work to refresh the list here.
				if (hasNext)
					getListData(mOffset, mLimit,PULL_UP);
				else
					new GetDataTask().execute();
			}
		});
	}
	
	/**
	 * 当数据刷新完的时候
	 * 要隐藏转的dialog
	 * @author jczhang
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
			pullToRefreshListView.onRefreshComplete();
			super.onPostExecute(null);
		}
	}
	
	/**
	 * 在onstop里面关闭掉对话框
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if(detailDialog != null) detailDialog.dismiss();
	}
}
