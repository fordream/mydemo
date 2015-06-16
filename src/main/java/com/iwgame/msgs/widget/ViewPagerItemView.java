package com.iwgame.msgs.widget;

import java.util.ArrayList;
import java.util.List;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.setting.adapter.GoodsListAdapter;
import com.iwgame.msgs.module.setting.vo.Goods;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DateUtil;
import com.youban.msgs.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


/**
 * 把积分商城页面 的
 * 当前个人积分以下的部分
 * 自定义成为一个控件
 * 自定义控件 
 * @author jczhang
 *
 */
public class ViewPagerItemView extends LinearLayout implements OnClickListener{

	private TextView isfinish;
	private RelativeLayout activityStateItem;
	private TextView goodsCategory;//商品种类
	private RelativeLayout goodsCategoryItem;
	private PullToRefreshListView listview;
	private ImageView activityStatePic;
	private ImageView goodsCategoryPic;
	private View fengexian;
	// 初始化页码
	// 可刷新主容器
	private Context context;
	private ListView list;
	// 是否有下一页
	protected boolean hasNext = true;
	// 当前页码
	protected int mOffset = 0;
	// 每页大小(默认降序向下取)
	protected int mLimit = 10;
	public List<Goods>listData;
	public GoodsListAdapter adapter;
	private long categoryId;
	private CustomProgressDialog customProgressDialog;
	private int VITURL_GOODS_NUM = 0;//虚拟商品
	private int ENTITY_GOODS_NUM = 1;//实物商品
	private int TOTAL_GOODS_NUM = -1;//全部
	private int ACTIVITY_STATE_TOTAL = -1;//商品状态全部
	private int ACTIVITY_STATE_UNCHANGE = 0;//可兑换
	private int ACTIVITY_STATE_UNSTART = 2;//未开始
	private int ACTIVITY_STATE_CHANGE_FINISH = 3;//已兑换完
	private String goodsStatus = ACTIVITY_STATE_TOTAL+"";
	private int goodType = TOTAL_GOODS_NUM;
	private int PULL_UP = 0;//上拉刷新
	private int PULL_DOWN = 1;//下拉刷新
	private View view;
	private View goodsCategoryview;
	private TextView goodsTotal;//全部
	private TextView goodsJinxing;//进行中
	private TextView goodsWeiKaishi;//未开始
	private TextView goodsDuihuanWan;//兑换完
	private TextView goodsCategoryTotal;//商品种类全部
	private TextView goodsCategoryVitul;//虚拟商品
	private TextView goodsCategoryReal;//真实物品
	private RelativeLayout nogoodsParent;//如果没有获取到商品列表，则显示默认的图片
	
	
	//构造方法 
	public ViewPagerItemView(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * 有参构造方法 
	 * @param context
	 * @param flag
	 */
	public ViewPagerItemView(Context context,boolean flag,boolean loadData,long categoryId){
		super(context);
		this.view = View.inflate(context, R.layout.goods_list_pop, null);
		this.goodsCategoryview = View.inflate(context, R.layout.goods_category_pop, null);
		goodsTotal = (TextView)this.view.findViewById(R.id.goods_total);
		goodsJinxing = (TextView)this.view.findViewById(R.id.goods_jinxingzhong);
		goodsWeiKaishi = (TextView)this.view.findViewById(R.id.goods_weikaishi);
		goodsDuihuanWan = (TextView)this.view.findViewById(R.id.goods_duihuanwan);
		goodsCategoryTotal = (TextView)this.goodsCategoryview.findViewById(R.id.goods_total);
		goodsCategoryVitul = (TextView)this.goodsCategoryview.findViewById(R.id.goods_jinxingzhong);
		goodsCategoryReal = (TextView)this.goodsCategoryview.findViewById(R.id.goods_weikaishi);
		this.context = context;
		this.categoryId = categoryId;
		customProgressDialog = CustomProgressDialog.createDialog(context, true);
		setListView(context,flag,loadData);
	}

	/**
	 * 将view添加进去
	 * @param context
	 * @param flag
	 */
	private void setListView(Context context, boolean flag,boolean loadData) {
		View view = View.inflate(context, R.layout.point_market_bottom_view, null);
		init(view,flag);
		setPullRefreshListView(listview);
		listData = new ArrayList<Goods>();
		adapter = new GoodsListAdapter(context, listData);
		list.setAdapter(adapter);
		if(loadData){
			//如果为true,则加载数据，如果为false，则不需要加载数据
			refreshList();
		}
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(view,params);
	}

	public int getmOffset() {
		return mOffset;
	}

	public void setmOffset(int mOffset) {
		this.mOffset = mOffset;
	}

	/**
	 * 清除列表数据,重新渲染列表
	 */
	public void refreshList() {
		if (listData != null && adapter != null) {
			customProgressDialog.show();
			mOffset = 0;
			hasNext = true;
			listData.clear();
			adapter.notifyDataSetChanged();
			adapter.notifyDataSetInvalidated();
			getListData(mOffset, mLimit,PULL_UP);
		} else {
			return;
		}
	}

	/**
	 * 初始化界面的控件
	 * @param view
	 * @param flag
	 */
	private void init(View view,boolean flag) {
		isfinish = (TextView)view.findViewById(R.id.activity_state);
		activityStateItem = (RelativeLayout)view.findViewById(R.id.activity_state_item);
		activityStatePic = (ImageView)view.findViewById(R.id.is_finish_tag);
		goodsCategory = (TextView)view.findViewById(R.id.goods_category);
		goodsCategoryItem = (RelativeLayout)view.findViewById(R.id.goods_category_item);
		goodsCategoryPic = (ImageView)view.findViewById(R.id.is_goods_tag);
		fengexian = (View)view.findViewById(R.id.fengexian);
		listview = (PullToRefreshListView)view.findViewById(R.id.refreshList);
		nogoodsParent = (RelativeLayout)view.findViewById(R.id.no_goods);
		activityStateItem.setOnClickListener(this);
		goodsCategoryItem.setOnClickListener(this);
		if(flag){
			fengexian.setVisibility(View.VISIBLE);
			goodsCategoryItem.setVisibility(View.VISIBLE);
		}else{
			fengexian.setVisibility(View.GONE);
			goodsCategoryItem.setVisibility(View.GONE);
		}
	}

	public long getCategoryId() {
		return categoryId;
	}


	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}


	public String getGoodsStatus() {
		return goodsStatus;
	}


	public void setGoodsStatus(String goodsStatus) {
		this.goodsStatus = goodsStatus;
	}


	public int getGoodType() {
		return goodType;
	}


	public void setGoodType(int goodType) {
		this.goodType = goodType;
	}


	@Override
	public void onClick(View v) {

		if(v.getId() == activityStateItem.getId()){
			activityStatePic.setBackgroundResource(R.drawable.score_up);
			final PopupWindow window = new PopupWindow(view,v.getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
			goodsTotal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					window.dismiss();
					if(isfinish.getText().toString() != null && !"全部".equals(isfinish.getText().toString().trim())){
						goodsStatus = ACTIVITY_STATE_TOTAL+"";
						isfinish.setText("全部");
						refreshList();
					}
				}
			});
			goodsJinxing.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					window.dismiss();
					if(isfinish.getText().toString() != null && !"进行中".equals(isfinish.getText().toString().trim())){
						goodsStatus = ACTIVITY_STATE_UNCHANGE+","+1;
						isfinish.setText("进行中");
						refreshList();
					}
				}
			});
			goodsWeiKaishi.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					window.dismiss();
					if(isfinish.getText().toString() != null && !"未开始".equals(isfinish.getText().toString().trim())){
						goodsStatus = ACTIVITY_STATE_UNSTART+"";
						isfinish.setText("未开始");
						refreshList();
					}
				}
			});
			goodsDuihuanWan.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					window.dismiss();
					if(isfinish.getText().toString() != null && !"兑换完".equals(isfinish.getText().toString().trim())){
						goodsStatus = ACTIVITY_STATE_CHANGE_FINISH+"";
						isfinish.setText("兑换完");
						refreshList();
					}
				}
			});
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			window.setOutsideTouchable(true);
			window.setFocusable(true);
			window.setTouchable(true);
			window.showAsDropDown(v);
			window.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					activityStatePic.setBackgroundResource(R.drawable.score_down);
				}
			});
		}else if(v.getId() == goodsCategoryItem.getId()){
			goodsCategoryPic.setBackgroundResource(R.drawable.score_up);
			final PopupWindow window = new PopupWindow(goodsCategoryview,v.getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
			goodsCategoryTotal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View paramView) {
					window.dismiss();
					if(goodsCategory.getText().toString() != null && !"全部".equals(goodsCategory.getText().toString().trim())){
						goodType = TOTAL_GOODS_NUM;
						goodsCategory.setText("全部");
						refreshList();
					}
				}
			});
			goodsCategoryVitul.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View paramView) {

					window.dismiss();
					if(goodsCategory.getText().toString() != null && !"虚拟商品".equals(goodsCategory.getText().toString().trim())){
						goodType = VITURL_GOODS_NUM;
						goodsCategory.setText("虚拟商品");
						refreshList();
					}
				}
			});
			goodsCategoryReal.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View paramView) {
					window.dismiss();
					if(goodsCategory.getText().toString() != null && !"实物商品".equals(goodsCategory.getText().toString().trim())){
						goodType = ENTITY_GOODS_NUM;
						goodsCategory.setText("实物商品");
						refreshList();
					}
				}
			});
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			window.setOutsideTouchable(true);
			window.setFocusable(true);
			window.setTouchable(true);
			window.showAsDropDown(v);
			window.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					goodsCategoryPic.setBackgroundResource(R.drawable.score_down);
				}
			});
		}
	}

	public void setPullRefreshListView(PullToRefreshListView pullRefreshListView) {
		this.listview = pullRefreshListView;
		list = pullRefreshListView.getRefreshableView();
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.setAction(SystemConfig.UPDATE_GOODS_LIST_ACTION);
				intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_INDEX, arg2-1);
				intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_ID, listData.get(arg2-1).getId());
				context.sendBroadcast(intent);
			}
		});
        this.listview.setMode(Mode.BOTH);
		final ILoadingLayout headerLabels = listview.getLoadingLayoutProxy(true, false);
		final ILoadingLayout footerLabels = listview.getLoadingLayoutProxy(false, true);
		headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		// Set a listener to be invoked when the list should be refreshed.
		listview.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				    headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
					getListData(0, mLimit,PULL_DOWN);
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
	 * 获取列表数据(子类实现)
	 * 
	 * @param offset
	 * @param limit
	 */
	protected void getListData(final int offset, final int limit,final int type) {
		//去获取数据  获取数据后，要更新适配器
		ProxyFactory.getInstance().getUserProxy().getGoodsList(new ProxyCallBack<List<Goods>>() {
			
			@Override
			public void onSuccess(List<Goods> result) {
				customProgressDialog.dismiss();
				if(type == PULL_UP){
					if(result != null){
						if(result.size() < limit){
							hasNext = false;
						}
						mOffset = offset + limit;
						listData.addAll(result);
						adapter.notifyDataSetChanged();
						adapter.notifyDataSetInvalidated();
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
				listview.onRefreshComplete();
				if(listData == null || listData.size() <= 0){
					listview.setVisibility(View.GONE);
					nogoodsParent.setVisibility(View.VISIBLE);
				}else{
					listview.setVisibility(View.VISIBLE);
					nogoodsParent.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				customProgressDialog.dismiss();
				listview.onRefreshComplete();
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}
		}, context, categoryId, goodsStatus+"", goodType, offset, limit);
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
			listview.onRefreshComplete();
			super.onPostExecute(null);
		}
	}
}
