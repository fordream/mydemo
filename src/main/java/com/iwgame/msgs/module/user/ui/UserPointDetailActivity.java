package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.user.adapter.UserPointDetailAdapter;
import com.iwgame.msgs.module.user.object.UserPointDetailObj;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.youban.msgs.R;

/**
 * 用户积分详情界面
 * 
 * @author Administrator
 * 
 */
public class UserPointDetailActivity extends BaseListActivity {

	private TextView curTotalPoint;//当前的总积分
	private LinearLayout noDetailData;//当没有获取到数据的情况下，显示的默认背景
	private LinearLayout pullListView;//当获取到数据的情况列表父容器
	private TextView curPointDetailed;
	private TextView curSevenDaysDetailed;
	private ImageView curPointImageView;
	
	
	/**
	 * 初始化
	 * 把布局添加到手机界面
	 */
	protected void initialize() {
		setLeftVisible(true);// 显示左边
		setRightVisible(false);// 影藏右边
		titleTxt.setText(getString(R.string.personal_integral_detail_title));// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (LinearLayout) View.inflate(this, R.layout.personal_point_experience_detail, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		curPointImageView =(ImageView)view.findViewById(R.id.cur_bg);
		curPointImageView.setImageResource(R.drawable.setting_point_bg);
		curPointDetailed =(TextView)view.findViewById(R.id.cur_personal_detailed);
		curPointDetailed.setText(R.string.cur_point);
		curSevenDaysDetailed =(TextView)view.findViewById(R.id.cur_seven_days_detailed);
		curSevenDaysDetailed.setText(R.string.cur_point_detailed);
		curTotalPoint = (TextView)view.findViewById(R.id.cur_personal_num);
		getPoint();//请求当前用户的积分
		noDetailData = (LinearLayout)view.findViewById(R.id.no_detail);
		pullListView = (LinearLayout)view.findViewById(R.id.listContent);
		pullListView.setVisibility(View.VISIBLE);
		// 获取列表
		setPullRefreshListView((PullToRefreshListView) view.findViewById(R.id.refreshList));
		// 添加列表点击功能
		listData = new ArrayList<UserPointDetailObj>();
		list.setDivider(null);
		setListAndAdapter(list, new UserPointDetailAdapter(UserPointDetailActivity.this, listData,"point"));// 添加列表点击功能
	}
	/**
	 * 获取用户的
	 * 积分信息
	 * 将积分数值显示到界面上
	 */
	private void getPoint() {
		int point = SystemContext.getInstance().getPoint();
		if(point > 0){//先从本地获取用户的积分 ，如果积分是大于0的则不用请求，如果是等于0的，则从服务端请求数据显示
			curTotalPoint.setText("" + point);
		}else{//从服务端请求当前用户的积分
			ProxyFactory.getInstance().getUserProxy().getUserPoint(new ProxyCallBack<List<ExtUserVo>>() {
				@Override
				public void onSuccess(List<ExtUserVo> result) {
					if(result != null && result.size() == 1){
						int point = result.get(0).getPoint();
						curTotalPoint.setText("" + point);
						SystemContext.getInstance().setPoint(point);
					}else{
						curTotalPoint.setText("" + 0);
						SystemContext.getInstance().setPoint(0);
					}
				}
				@Override
				public void onFailure(Integer result, String resultMsg) {
				}
			}, this, SystemContext.getInstance().getExtUserVo().getUserid()+"");
		}
	}

	/**
	 * 通过请求服务端
	 * 查询个人用户
	 * 的积分明细
	 * 显示在手机界面上
	 */
	@Override
	protected void getListData(long off, int lim) {
		super.getListData(off, lim);
		if(listData != null && listData.size() <= 0)setLoadingUI();
		//请求服务端的数据
		ProxyFactory.getInstance().getUserProxy().getUserHistroyPointDetail(new ProxyCallBack<List<UserPointDetailObj>>() {
			@Override
			public void onSuccess(List<UserPointDetailObj> result) {
				mOffset = mOffset+mLimit;
				if(result != null && result.size() > 0){
					hasNext = true;
					listData.addAll(result);
					adapter.notifyDataSetChanged();
				}else if(listData.size() <= 0){
					hasNext = false;
					noDetailData.setVisibility(View.VISIBLE);
					pullListView.setVisibility(View.GONE);
				}else{
					hasNext = false;
				}
				onFooterRefreshComplete();
				if(listData != null && listData.size() > 0){
					noDetailData.setVisibility(View.GONE);
					pullListView.setVisibility(View.VISIBLE);
				}else{
					noDetailData.setVisibility(View.VISIBLE);
					setWithNoData();
					pullListView.setVisibility(View.GONE);
				}
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				handleErrorCode(result, resultMsg);
				onFooterRefreshComplete();
				if(listData != null && listData.size() > 0){
					noDetailData.setVisibility(View.GONE);
					pullListView.setVisibility(View.VISIBLE);
				}else{
					noDetailData.setVisibility(View.VISIBLE);
					setWithNoData();
					pullListView.setVisibility(View.GONE);
				}
			}
		}, UserPointDetailActivity.this, off, lim);
	}

	/**
	 * 当第一次进去的时候，
	 * 没有数据的时候，
	 * 显示加载数据的方法
	 */
	private void setLoadingUI(){
		noDetailData.removeAllViews();
		noDetailData.setVisibility(View.VISIBLE);
		pullListView.setVisibility(View.GONE);
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		noDetailData.addView(view, params);
	}
	
	
	/**
	 * 没有查出数据的时候
	 * 显示默认数据
	 * 即友好 的图片提示
	 */
	private void setWithNoData(){
		noDetailData.removeAllViews();
		noDetailData.setVisibility(View.VISIBLE);
		pullListView.setVisibility(View.GONE);
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.no_point_detail, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		noDetailData.addView(view, params);
	
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
			mOffset = 0;
			mLimit = 20;
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
	 * 当请求数据失败了以后
	 * 返回来是执行onfailure方法时
	 * 在这个方法里面处理这个错误码
	 * @param i
	 * @param msg
	 */
	private void handleErrorCode(Integer i,String msg){
		ErrorCodeUtil.handleErrorCode(UserPointDetailActivity.this, i, msg);
	}
}
