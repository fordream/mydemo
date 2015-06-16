/**      
* PayDetailPageInfoActivity.java Create on 2015-5-26     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.pay.ui;

import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.logging.SimpleFormatter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.pay.ui.util.DoubleUtil;
import com.iwgame.msgs.proto.Msgs.CashoutDetailPB;
import com.iwgame.msgs.proto.Msgs.ConsumeDetailPB;
import com.iwgame.msgs.proto.Msgs.IncomeDetailPB;
import com.iwgame.msgs.proto.Msgs.OfficialDetailPB;
import com.iwgame.msgs.proto.Msgs.RechargeDetailPB;
import com.iwgame.msgs.proto.Msgs.YoubiDetailInfo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/** 
 * @ClassName: PayDetailPageInfoActivity 
 * @Description: 游币详细明细
 * @author xingjianlong
 * @date 2015-5-26 上午9:05:35 
 * @Version 1.0
 * 
 */
public class PayDetailPageInfoActivity extends BaseActivity {
	
	private LinearLayout view;
	private TextView payTitle;
	private TextView payUb;
	private TextView payExtra;
	private LinearLayout payContent;
	private ImageView payBtnImage;
	private TextView payBtnText;
	private int type;
	private long orN;
	private RechargeDetailPB recharge;
	private IncomeDetailPB income;
	private ConsumeDetailPB consume;
	private CashoutDetailPB cashout;
	private OfficialDetailPB offical;

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		init();
	}

	/**
	 * 
	 */
	private void getData() {
		type = getIntent().getIntExtra("bid", 0);
		orN=getIntent().getLongExtra("orn", 0);
	}

	/**
	 * 
	 */
	private void init() {
		setTitleTxt("详情明细");
		view =(LinearLayout)findViewById(R.id.contentView);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		View contentView = View.inflate(this, R.layout.pay_more_info, null);
		view.addView(contentView, params);
		payTitle=(TextView)contentView.findViewById(R.id.pay_detail_title);
		payUb=(TextView)contentView.findViewById(R.id.pay_detail_ub);
		payExtra=(TextView)contentView.findViewById(R.id.pay_other_money);
		payContent=(LinearLayout)contentView.findViewById(R.id.play_detail_content);
		payBtnImage=(ImageView)contentView.findViewById(R.id.pay_btn_image);
		payBtnText =(TextView)contentView.findViewById(R.id.pay_btn_text);
		view.setVisibility(View.GONE);
		getDetailInfo(this,type, orN);
		
	}
	/**
	 * 渲染界面
	 * @param yb
	 */
	public void readerLayout(YoubiDetailInfo yb){
		if(yb==null) return;
		recharge = yb.getRechargeDetailPB();
		income  =yb.getIncomeDetailPB();
		consume =yb.getConsumeDetailPB();
		cashout = yb.getCashoutDetailPB();
		offical = yb.getOfficialDetailPB();
		if(recharge.getOrderNo()!=0){
			rechargeContent(recharge);
		}else 
		if(income.getOrderNo()!=0){
			incomeContent(income);
		}
		 if(consume.getOrderNo()!=0){
			consumeContent(consume);
		}
		else if(cashout.getOrderNo()!=0){
			cashoutContent(cashout);
		}
		else if(offical.getOrderNo()!=0){
			officalContent(offical);
		}
	}
	
	private void officalContent(OfficialDetailPB official){
		payContent.removeAllViews();
		if(official.getYoubi()>0){
		payTitle.setText("系统赠送");
		}else{
			payTitle.setText("系统扣除");
		}
		if(official.getYoubi()!=0){
			if(official.getYoubi()>0){
			payUb.setText("+"+DoubleUtil.getDoubleFormat(official.getYoubi())+"U币");
			}else{
				payUb.setText(DoubleUtil.getDoubleFormat(official.getYoubi())+"U币");
			}
		}
		if(official.getCreateTime()!=0){
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(official.getCreateTime()));
			setContentView("交易时间", time);
		}
		if(official.getOrderNo()!=0){
			setContentView("交易号", official.getOrderNo()+"");
		}
		if(official.getTradeStatus()!=0){
			setBottomLayout("交易" ,5,official.getTradeStatus());
			
		}else{
			setBottomLayout("交易" ,5,1);
		}
	}
	/**
	 * 显示提现内容
	 * @param cashout
	 */
	private void cashoutContent(CashoutDetailPB cashout) {
		payContent.removeAllViews();
		payTitle.setText("提现");
		if(cashout.getYoubi()!=0){
			payUb.setText("-"+DoubleUtil.getDoubleFormat(cashout.getYoubi())+"U币");
		}
		if(cashout.getName()!=null){
			setContentView("持卡人姓名", cashout.getName());
		}
		if(cashout.getBank()!=null){
			setContentView("提现银行", cashout.getBank());
		}
		if(cashout.getBankDetail()!=null){
			setContentView("开户行", cashout.getBankDetail());
		}
		if(cashout.getCardNo()!=null){
			setContentView("提现银行卡号", cashout.getCardNo());
		}
		if(cashout.getCreateTime()!=0){
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(cashout.getCreateTime()));
			setContentView("交易时间", time);
		}
		if(cashout.getOrderNo()!=0){
			setContentView("交易号", cashout.getOrderNo()+"");
		}
		if(cashout.getTradeStatus()!=0){
			setBottomLayout("提现" ,4,cashout.getTradeStatus());
		}
	}

	/**
	 * 显示收入内容
	 * @param income
	 */
	private void incomeContent(IncomeDetailPB income) {
		payContent.removeAllViews();
		payTitle.setText("陪玩收入");
		if(income.getYoubi()!=0){
			payUb.setText("+"+DoubleUtil.getDoubleFormat(income.getYoubi())+"U币");
		}
		
		if(income.getFromName()!=null){
			setContentView("陪练人", income.getFromName());
		} 
		if(income.getFromSerial()!=0){
			setContentView("游伴ID", income.getFromSerial()+"");
		}
		if(income.getRemark()!=null){
			setContentView("陪玩类型", income.getRemark());
		}
		if(income.getCreateTime()!=0){
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(income.getCreateTime()));
			setContentView("交易时间", time);
		}
			if(income.getOrderNo()!=0){
				setContentView("交易号" ,income.getOrderNo()+"");
		}
			if(income.getTradeStatus()!=0){
			setBottomLayout("交易" ,1,income.getTradeStatus());
		}else{
			setBottomLayout("交易" ,5,1);
		}
	}

	/**
	 * 显示消费内容
	 * @param consume
	 */
	private void consumeContent(ConsumeDetailPB consume) {
		payContent.removeAllViews();
		payTitle.setText("支付陪玩");
		if(type==6){
			payTitle.setText("支付陪玩技术服务费");
		}
		if(consume.getYoubi()!=0){
			
				payUb.setText("-"+DoubleUtil.getDoubleFormat(consume.getYoubi())+"U币");
		
		}
		 if(consume.getToName()!=null){
			setContentView("陪玩人",consume.getToName());
		}
		 if(consume.getToSerial()!=0){
			setContentView("游伴ID", consume.getToSerial()+"");
		}
		 if(consume.getRemark()!=null){
				setContentView("陪玩类型", consume.getRemark());
		 }
		if(consume.getCreateTime()!=0){
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(consume.getCreateTime()));
			setContentView("交易时间", time);
		}
		 if(consume.getOrderNo()!=0){
			setContentView("交易号", consume.getOrderNo()+"");
		}
		if(consume.getTradeStatus()!=0){
			setBottomLayout("交易" ,type,consume.getTradeStatus());
		}else{
			setBottomLayout("交易" ,5,1);
		}
	}

	/**
	 * 显示充值内容
	 * @param recharge
	 */
	private void rechargeContent(RechargeDetailPB recharge) {
		payContent.removeAllViews();
		payTitle.setText("充值U币");
		if(recharge.getYoubi()!=0){
			payUb.setText("+"+DoubleUtil.getDoubleFormat(recharge.getYoubi())+"U币");
		}
		if(recharge.getGiveAdded()!=0){
			payExtra.setVisibility(View.VISIBLE);
			payExtra.setText("额外赠送"+DoubleUtil.getDoubleFormat(recharge.getGiveAdded())+"U币");
		} 
		if(recharge.getChannel()!=0){
			if(recharge.getChannel()==1){
				setContentView("充值方式", "支付宝充值");
			}else if(recharge.getChannel()==2){
				setContentView("充值方式", "微信充值");
			}
		}
		if(recharge.getRechargeType()!=0){
			if(recharge.getRechargeType()==2){
				setContentView("充值优惠", "首次充值");
			}else if(recharge.getRechargeType()==3){
				setContentView("充值优惠", "活动充值");
			}
		}
		if(recharge.getPrice()!=0){
			setContentView("价格",DoubleUtil.getDoubleFormat(recharge.getPrice())+"元");
		}
		if(recharge.getCreateTime()!=0){
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(recharge.getCreateTime()));
			setContentView("交易时间", time);
		}
		if(recharge.getOrderNo()!=0){
			setContentView("交易号",recharge.getOrderNo()+"");
		}
		if(recharge.getTradeStatus()!=0){
			setBottomLayout("充值", 3,recharge.getTradeStatus());
		}
	}
	/**
	 * 添加相应的布局
	 * @param title
	 * @param content
	 */
	private void setContentView(String title,String content){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		View view =View.inflate(PayDetailPageInfoActivity.this, R.layout.pay_detial_recharge, null);
		TextView titleItem = (TextView)view.findViewById(R.id.pay_detail_item_title);
		TextView contentItem =(TextView)view.findViewById(R.id.pay_detail_item_content);
		titleItem.setText(title);
		contentItem.setText(content);
		payContent.addView(view, params);
	}
	
	private void setBottomLayout(String name ,int type,int status){
		if(status==1){
			payBtnImage.setBackgroundResource(R.drawable.dingdan_jinxingzhong);
			payBtnText.setText(name+"进行中");
		}else if(status==2){
			payBtnImage.setBackgroundResource(R.drawable.dingdan_close);
			payBtnText.setText(name+"取消");
		}else if(status==3){
			payBtnImage.setBackgroundResource(R.drawable.dingdan_shibai);
			if(name.equals("提现")){
				payBtnText.setText(name+"失败(有退款)");
			}else if(type==2){
				payBtnText.setText(name+"关闭(有退款)");
			}
			else{
			payBtnText.setText(name+"失败");
			}
		}else if(status==4){
			payBtnImage.setBackgroundResource(R.drawable.dingdan_tick);
			if(type==6){
				payBtnText.setText("自动扣取");
			}else{
			payBtnText.setText(name+"成功");
			}
		}else {
			payBtnImage.setBackgroundResource(R.drawable.dingdan_shibai);
			payBtnText.setText("ERROR");
		}
	}
	
	private void getDetailInfo(Context context,int bussinessType, long orderNo){
		final CustomProgressDialog downloaddialog = CustomProgressDialog.createDialog(this, false);
		downloaddialog.show();
		ProxyFactory.getInstance().getPayProxy().searchOrderDeatil(new ProxyCallBack<YoubiDetailInfo>() {
			
			@Override
			public void onSuccess(YoubiDetailInfo result) {
				if(downloaddialog.isShowing()){
					downloaddialog.dismiss();
				}
				if(result!=null){
				readerLayout(result);
				}
				view.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				downloaddialog.dismiss();
				ToastUtil.showToast(PayDetailPageInfoActivity.this, "数据加载失败");
				view.setVisibility(View.VISIBLE);
			}
		}, context, bussinessType, orderNo);
	}
}
