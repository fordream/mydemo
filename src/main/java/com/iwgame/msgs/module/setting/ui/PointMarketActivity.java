package com.iwgame.msgs.module.setting.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.game.adapter.ViewPagerAdapter;
import com.iwgame.msgs.module.setting.vo.Goods;
import com.iwgame.msgs.module.setting.vo.GoodsTab;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.ViewPagerItemView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.youban.msgs.R;

/**
 * 积分商城页面
 * @author jczhang
 *
 */
public class PointMarketActivity extends BaseSuperActivity implements OnClickListener{

	private Button leftBtn;
	private LinearLayout rightView;
	private TextView curPointNum;
    private ViewPager pager;
	private HorizontalScrollView myHorizontalScrollView;
    private List<GoodsTab> goodsTabList = new ArrayList<GoodsTab>();
    private List<View>pagerViewList = new ArrayList<View>();
	private List<View> radioBtnList = new ArrayList<View>();
    private LinearLayout tabContent;
    private int flag = 0;//标记tabcontent哪一个被选中了
    private DisplayMetrics dm;
    private List<Integer> pixel = new ArrayList<Integer>();
    private boolean temp = true;//标记pixel数组里面是否已经有值了
    private CustomProgressDialog customProgressDialog;
    private MyBroadCast myBroadCast;
    private int index = 0;
    private int lastIndex;

    /**
	 * 当界面刚启动的时候
	 * 执行下面的这个方法 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.point_market_activity);
		//注册广播
		myBroadCast = new MyBroadCast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SystemConfig.UPDATE_GOODS_LIST_ACTION);
		registerReceiver(myBroadCast, filter);
		init();
	}

	/**
	 * 初始化界面
	 * 上的控件
	 */
	private void init() {
		customProgressDialog = customProgressDialog.createDialog(SystemContext.getInstance().getContext(), true);
		dm = new DisplayMetrics();getWindowManager().getDefaultDisplay().getMetrics(dm);
		leftBtn = (Button)findViewById(R.id.leftBtn);
		rightView = (LinearLayout)findViewById(R.id.rightView);
		TextView changeRecord = new TextView(this);
		changeRecord.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor());
		changeRecord.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		changeRecord.setText("兑换记录");
		rightView.addView(changeRecord);
		leftBtn.setOnClickListener(this);
		rightView.setOnClickListener(this);
		curPointNum = (TextView)findViewById(R.id.cur_point_num);
		tabContent = (LinearLayout)findViewById(R.id.tab_content);
		pager = (ViewPager)findViewById(R.id.viewPage);
		myHorizontalScrollView = (HorizontalScrollView)findViewById(R.id.point_market_tab_hori);
		setListener();
		getPointMarketTab();
	}

	
	/**
	 * 获取积分
	 * 商城顶部的
	 * 标签
	 */
	private void getPointMarketTab() {
		customProgressDialog.show();
		ProxyFactory.getInstance().getUserProxy().getPointTab(new ProxyCallBack<List<GoodsTab>>() {

			@Override
			public void onSuccess(List<GoodsTab> result) {
				customProgressDialog.dismiss();
				goodsTabList = result;
				if(result != null && result.size() > 0)
					addRadioBtnToGroup();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				customProgressDialog.dismiss();
				ErrorCodeUtil.handleErrorCode(PointMarketActivity.this, result, resultMsg);
			}
		}, this);
	}

	/**
	 * 添加radioBtn 
	 * 到radiogroup里面
	 */
	protected void addRadioBtnToGroup() {
		ViewPagerItemView viewPagerItemView;
		GoodsTab tab;
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		// 1,添加tab到顶部，2、把对应 的view 保存到集合中， 3、创建相应的viewpageritemview,4、初始显示第一个，5、设置点击的事件
		int size = goodsTabList.size();
		for(int i = 0; i < size; i++){
			//做上面的几步事情
			tab = goodsTabList.get(i);
			final View view = View.inflate(this, R.layout.tab_content_item, null);
			TextView textView = (TextView)view.findViewById(R.id.tab_content_desc);
			textView.setText(tab.getName());
			if(i == 0){
				view.setSelected(true);
			}else{
				view.setSelected(false);
			}
			tabContent.addView(view, params);
			radioBtnList.add(view);
			view.setOnClickListener(this);
			if(i == 0){
				viewPagerItemView = new ViewPagerItemView(this, true, true, goodsTabList.get(i).getId());
				pagerViewList.add(viewPagerItemView);
			}else{
				viewPagerItemView = new ViewPagerItemView(this, true, false, goodsTabList.get(i).getId());
				pagerViewList.add(viewPagerItemView);
			}
		}
		pager.setAdapter(new ViewPagerAdapter(pagerViewList));
		pager.setCurrentItem(0);
	}

	/**
	 * 界面中的控件
	 * 添加监听器
	 */
	private void setListener() {
		//viewpager滑动的时候会执行下面的这个方法 
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if(temp){
					addWidthToPixel();
					temp = false;
				}
				flag = position;
				if(((ViewPagerItemView)pagerViewList.get(position)).listData.size() > 0){
					
				}else{
					((ViewPagerItemView)pagerViewList.get(position)).refreshList();
				}
				pager.setCurrentItem(position);
				int lenght = radioBtnList.size();
				for(int i = 0; i < lenght; i++){
					if(i != flag){
						radioBtnList.get(i).setSelected(false);
					}else{
						radioBtnList.get(flag).setSelected(true);
					}
				}
			myHorizontalScrollView.smoothScrollTo(pixel.get(flag)+radioBtnList.get(flag).getMeasuredWidth()/2-dm.widthPixels/2, 0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int position) {
				
			}
		});
	}

	/**
	 * 把顶部的标签的
	 * 宽度添加到集合当中 去
	 */
	private void addWidthToPixel(){
		int length = radioBtnList.size();
		for(int i = 0; i < length; i ++){
			if(i == 0){
				pixel.add(0);
			}else{
				pixel.add(radioBtnList.get(i-1).getMeasuredWidth() + pixel.get(i-1));
			}
		}
	}
	
	
	/**
	 * 在这个方法里面
	 * 加载用户的积分还有多少 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getPoint();
	}


	/**
	 * 获取用户的
	 * 积分信息
	 * 将积分数值显示到界面上
	 */
	private void getPoint() {
		int point = SystemContext.getInstance().getPoint();
		if(point > 0){
			curPointNum.setText(""+point);
		}else{
			ProxyFactory.getInstance().getUserProxy().getUserPoint(new ProxyCallBack<List<ExtUserVo>>() {
				@Override
				public void onSuccess(List<ExtUserVo> result) {
					if(result != null && result.size() == 1){
						int point = result.get(0).getPoint();
						curPointNum.setText(""+point);
						SystemContext.getInstance().setPoint(point);
					}
				}
				@Override
				public void onFailure(Integer result, String resultMsg) {
				}
			}, this, SystemContext.getInstance().getExtUserVo().getUserid()+"");
		}
	}

	
	
	/**
	 * 当点击界面上的按钮的时候
	 * 执行下面的这个方法 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		if(v.getId() == leftBtn.getId()){
			//点击返回键的时候，退出当前的界面
			this.finish();
		}else if(v.getId() == rightView.getId()){
			//点击兑换记录的时候，跳转页面
			Intent intent = new Intent(this,ChangeRecordsActivity.class);
			this.startActivity(intent);
		}else{
			int length = radioBtnList.size();
			for(int i = 0; i < length; i++){
				if(temp){
					addWidthToPixel();
					temp = false;
				}
				if(v == radioBtnList.get(i)){
					flag = i;
					radioBtnList.get(i).setSelected(true);
					pager.setCurrentItem(i);
					for(int j = 0; j < radioBtnList.size(); j++){
						if(j != i){
							radioBtnList.get(j).setSelected(false);
						}else{
							radioBtnList.get(j).setSelected(true);
						}
					}
				myHorizontalScrollView.smoothScrollTo(pixel.get(flag)+radioBtnList.get(flag).getMeasuredWidth()/2-dm.widthPixels/2, 0);
				break;
				}
			}
		}
	}
	
	
	/**
	 * 广播类
	 * 这个广播的作用是 
	 * 如果 在自定义控件里面点击商品跳转
	 * 需要发送广播到其activity界面 在跳转到商品详情页面
	 * @author jczhang
	 *
	 */
	class MyBroadCast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(SystemConfig.UPDATE_GOODS_LIST_ACTION.equals(intent.getAction())){
				index = intent.getIntExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_INDEX, 0);
				long id = intent.getLongExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_ID, 0);
				Intent i = new Intent(PointMarketActivity.this,GoodsDetailActivity.class);
				i.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_ID, id);
				i.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_FLAG, true);
				lastIndex = flag;
				PointMarketActivity.this.startActivityForResult(i, 2);
			}
		}
	}
	

	/**
	 * 当调用 了startactivityforresult
	 * 后，会执行此方法 
	 * 其作用是，如果点击某一个商品到商品详情页面后，返回
	 * 如果其商品的状态发生了变化，这样就可以不通过去请求商品
	 *  直接在本地进行修改商品的状态
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(lastIndex != flag) return;
		Goods result = (Goods)data.getExtras().getSerializable("goods");
		List<Goods>list = new ArrayList<Goods>();
		list.addAll(((ViewPagerItemView)pagerViewList.get(flag)).listData.subList(0, index));
		if(result != null && result.getGoodsStatus() != 4){
			list.add(result);
		}
		list.addAll(((ViewPagerItemView)pagerViewList.get(flag)).listData.subList(index+1, ((ViewPagerItemView)pagerViewList.get(flag)).listData.size()));
		((ViewPagerItemView)pagerViewList.get(flag)).listData.clear();
		((ViewPagerItemView)pagerViewList.get(flag)).listData.addAll(list);
		((ViewPagerItemView)pagerViewList.get(flag)).adapter.notifyDataSetChanged();
		((ViewPagerItemView)pagerViewList.get(flag)).adapter.notifyDataSetInvalidated();
	}
	
	/**
	 * 在ondestroy方法里面
	 * 释放广播资源
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//释放广播资源
		unregisterReceiver(myBroadCast);
	}
}
