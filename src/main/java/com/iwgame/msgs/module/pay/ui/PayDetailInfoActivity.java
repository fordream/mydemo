/**      
* PayDetailInfoActivity.java Create on 2015-5-25     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.pay.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.pay.adapter.PayListAdapter;
import com.iwgame.msgs.module.play.adapter.ReplyPlayListAdapter;
import com.iwgame.msgs.module.play.ui.PlayDetailInfoActivity;
import com.iwgame.msgs.module.user.ui.ReportActivity;
import com.iwgame.msgs.module.user.ui.SelectStopTimeActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs.PlayOrderInfo;
import com.iwgame.msgs.proto.Msgs.UserYoubiDetail;
import com.iwgame.msgs.proto.Msgs.YoubiDetailList;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.popwindow.MsgsPopTextItem;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow.OnClickPopItemListener;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/** 
 * @ClassName: PayDetailInfoActivity 
 * @Description: TODO(U币明细列表) 
 * @author xingjianlong
 * @date 2015-5-25 上午11:13:30 
 * @Version 1.0
 * 
 */
public class PayDetailInfoActivity extends BaseActivity implements OnClickListener{
	protected static final String TAG = "PayDetailInfoActivity";
	private LinearLayout view;
	private CommonListView listContent;
	private int type=0;
	private PayListAdapter adapter;
	private Button rightBtn;
	private CustomProgressDialog downloaddialog;
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView();
		init();
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (listContent != null && listContent.listData.size() <= 0) {
			listContent.onHeaderRefresh();
		} else if (listContent != null) {
			listContent.setRefreshMode(Mode.BOTH);
			listContent.adapter.notifyDataSetChanged();
			listContent.adapter.notifyDataSetInvalidated();
		}
	}
	private  void setView(){
		 downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
	}
	/**
	 * 
	 */
	private void init() {
			setTitleTxt("U币明细");
			view = (LinearLayout)findViewById(R.id.contentView);
			rightBtn =(Button)findViewById(R.id.rightbtn);
			rightBtn.setBackgroundResource(R.drawable.common_menu_more_nor);
			rightBtn.setVisibility(View.VISIBLE);
			rightBtn.setOnClickListener(this);
			 addView();
	}
	
	private void addView(){
		view.removeAllViews();
		listContent =null;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		listContent = new CommonListView(this, View.VISIBLE, true){
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				getData(PayDetailInfoActivity.this,type);
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
		view.addView(listContent, params);
		setNUllContent("当前还没有任何游币明细记录哦，加油吧！");
		listContent.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int realposition = (int) parent.getAdapter().getItemId(position);
				UserYoubiDetail object = (UserYoubiDetail) listContent.listData.get(position-1);
				if (realposition != -1 && object != null && object instanceof UserYoubiDetail) {
					Intent intent = new Intent(PayDetailInfoActivity.this, PayDetailPageInfoActivity.class);
					intent.putExtra("bid", object.getBusinessType());
					intent.putExtra("orn", object.getOrderNo());
					startActivity(intent);
				}
			}
		});
		adapter = new PayListAdapter(PayDetailInfoActivity.this, listContent.listData);
		listContent.setAdapter(adapter);
	}
	/**
	 * 获取游币明细数据
	 * @param context
	 * @param type
	 */
	public void getData(Context context ,int type){
		ProxyFactory.getInstance().getPayProxy().searchOrderDeatils(new ProxyCallBack<PagerVo<UserYoubiDetail>>() {
			
			@Override
			public void onSuccess(PagerVo<UserYoubiDetail> result) {
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
				}
				if (listContent.isRefresh) {
					listContent.clean();
					listContent.onHeaderRefreshComplete();
				} else {
					listContent.onFooterRefreshComplete();
				}
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					if (result.getItems().size() < listContent.limit)
						listContent.hasNext = false;
					listContent.listData.addAll(result.getItems());
					listContent.showListView();
				} else {
					LogUtil.d(TAG, "数据为空");
					if (listContent.listData.size() <= 0)
						listContent.showNullBgView();
				}
				if (listContent.listData.size() <= 0) {
					listContent.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					listContent.listData.clear();
				} else {
					listContent.setRefreshMode(Mode.BOTH);
				}
				listContent.adapter.notifyDataSetChanged();
				listContent.offset = listContent.listData.size();
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.d("pay", "接口"+result);
				downloaddialog.dismiss();
			}
		}, context, type, listContent.offset, listContent.limit);
	}
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if(v==rightBtn){
			onClickRightMenu(v);
		}
	}
	/**
	 * 当点击右上角的按钮的时候 会弹出一个popwindow
	 * 
	 * @param v
	 */
	private void onClickRightMenu(View v) {
		// rightMenu.setBackgroundResource(R.drawable.common_menu_more_pre);
		final MsgsPopTextItem item0 = new MsgsPopTextItem(this, "全部");
		final MsgsPopTextItem item1 = new MsgsPopTextItem(this, "提现");
		final MsgsPopTextItem item2 = new MsgsPopTextItem(this, "充值");
		final MsgsPopTextItem item3 = new MsgsPopTextItem(this, "收入");
		final MsgsPopTextItem item4 = new MsgsPopTextItem(this, "支付");
		List<View> items = new ArrayList<View>();
		items.add(item0);
		items.add(item1);
		items.add(item2);
		items.add(item3);
		items.add(item4);
		MsgsPopWindow pw = new MsgsPopWindow(this, items, v, 0, 0);
		pw.setOnClickPopItemListener(new OnClickPopItemListener() {

			@Override
			public void onClickPopItem(View v) {
				downloaddialog.show();
					if(v==item0){
						type=0;
						setNUllContent("当前还没有任何U币明细记录哦，加油吧！");
					}else if(v==item1){
						type=4;
						setNUllContent("当前还没有提现明细记录哦！");
					}else if(v==item2){
						type=1;
						setNUllContent("当前还没有充值明细记录哦！");
					}else if(v==item3){
						type=2;
						setNUllContent("当前还没有收入明细记录哦！");
					}else if(v==item4){
						type =3;
						setNUllContent("当前还没有支付明细记录哦！");
					}
					listContent.clean();
					LogUtil.d(TAG, type+"");
					listContent.onHeaderRefresh();
			}
		});
		pw.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// rightMenu.setBackgroundResource(R.drawable.common_menu_more_nor);
			}
		});
	}
	private void setNUllContent(String text){
		listContent.nullContent.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout v = (LinearLayout) View.inflate(PayDetailInfoActivity.this, R.layout.pay_no_any_info, null);
		TextView tv = (TextView)v.findViewById(R.id.no_pay_info_tip);
		tv.setText(text);
		listContent.nullContent.addView(v, params);
	}
}
