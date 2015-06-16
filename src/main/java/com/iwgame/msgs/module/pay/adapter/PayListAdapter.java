/**      
* PayListAdapter.java Create on 2015-5-25     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.pay.adapter;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.module.pay.ui.util.DoubleUtil;
import com.iwgame.msgs.proto.Msgs.UserYoubiDetail;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** 
 * @ClassName: PayListAdapter 
 * @Description: TODO(...) 
 * @author xingjianlong
 * @date 2015-5-25 下午1:41:32 
 * @Version 1.0
 * 
 */
public class PayListAdapter extends BaseAdapter {

	private Context context;
	private List<UserYoubiDetail> list;
	private LayoutInflater inflater;
	private static final int TRADE_RECHARGE = 1;//充值
	private static final int  TRADE_PLAY_PAY=3;//陪玩支付
	private static final int TRADE_PLAY_INCOME=2;//陪玩收入
	private static final int TRADE_PLAY_WITHDRAW =4;//陪玩提现
	private static final int  TRADE_OOFICIAL =5;//官方
	private static final int  OFFICIA_SERVER =6;//官方
	private static final int TRADE_DOING =1;
	private static final int TRADE_CANCLE = 2;
	private static final int   TRADE_FAIL =3;
	private static final int   TRADE_COMEPLTE = 4;
	public PayListAdapter(Context context,List<UserYoubiDetail> list){
		this.context = context;
		this.list = list;
		inflater =LayoutInflater.from(context);
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup root) {
		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.pay_info_list_item, null);
			holder.image =(ImageView)convertView.findViewById(R.id.pay_image);
			holder.paytitle=(TextView)convertView.findViewById(R.id.pay_item_title);
			holder.paytime=(TextView)convertView.findViewById(R.id.pay_time);
			holder.paymoney=(TextView)convertView.findViewById(R.id.pay_money);
			holder.paystatus=(TextView)convertView.findViewById(R.id.pay_status);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		UserYoubiDetail youbi = list.get(position);
		setDetailData(holder,youbi);
		return convertView;
	}
	/**
	 * @param holder
	 * @param youbi
	 */
	private void setDetailData(ViewHolder holder, UserYoubiDetail youbi) {
		String text = null;
		ImageViewUtil.showImage(holder.image, youbi.getUserIcon(), R.drawable.postbar_thumbimg_default);
		if(youbi.getBusinessType()==1){
			if(youbi.getChannel()==1){
				text ="支付宝充值"+DoubleUtil.getDoubleFormat(youbi.getAmountRmb())+"元";
			}else if(youbi.getChannel()==2){
				text="微信充值"+DoubleUtil.getDoubleFormat(youbi.getAmountRmb())+"元";
			}
		}else if(youbi.getBusinessType()==2){
			text="陪玩收入";
		}else if(youbi.getBusinessType()==3){
			text="支付陪玩";
		}else if(youbi.getBusinessType()==4){
			text="提现"+DoubleUtil.getDoubleFormat(youbi.getAmountRmb())+"元";
		}else if(youbi.getBusinessType()==5){
			if(youbi.getAmountYoubi()>0){
				text="系统赠送";
			}else{
				text="系统扣除";
			}
		}else if(youbi.getBusinessType()==6){
			text="支付陪玩技术服务费";
		}
		holder.paytitle.setText(text);
		LogUtil.d("pay", youbi.getCreateTime()+"");
		holder.paytime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(youbi.getCreateTime())));
		holder.paymoney.setText(DoubleUtil.getDoubleFormat(youbi.getAmountYoubi())+"U币");
		getTradeText(youbi,holder.paystatus);
	}
	
	private void getTradeText(UserYoubiDetail youbi,TextView text){
		switch(youbi.getBusinessType()){
		case TRADE_RECHARGE:
		getStatusText("充值",youbi,text);
			break;
		case TRADE_PLAY_INCOME:
		case TRADE_PLAY_PAY:
			getStatusText("交易",youbi,text);
			break;
		case TRADE_PLAY_WITHDRAW:
			getStatusText("提现",youbi,text);
			break;
		case TRADE_OOFICIAL:
			getStatusText("交易",youbi,text);
			break;
		case OFFICIA_SERVER:
			getStatusText("交易",youbi,text);
		}
	}
	
	public void getStatusText(String s,UserYoubiDetail youbi,TextView tv){
		String text = null;
		switch(youbi.getTradeStatus()){
		case TRADE_DOING:
			tv.setTextColor(context.getResources().getColor(R.color.pay_main_top_bg));
			text="进行中";
			break;
		case TRADE_CANCLE:
				text="取消";
				tv.setTextColor(context.getResources().getColor(R.color.pay_detail_status_color));
			break;
		case TRADE_COMEPLTE:
			if(youbi.getBusinessType()!=6){
			text ="成功";
			}else{
			s="";
			text="自动扣取";
			}
			tv.setTextColor(context.getResources().getColor(R.color.pay_detail_status_color));
			break;
		case TRADE_FAIL:
			if(s.equals("提现")){
				text="失败(有退款)";
			}else if(s.equals("交易")){
				text="交易关闭(有退款)";
			}
			else{
			text="失败";
			}
			tv.setTextColor(context.getResources().getColor(R.color.pay_detail_status_color));
			break;
			default:
				tv.setTextColor(context.getResources().getColor(R.color.pay_main_top_bg));
				text="进行中";
				break;
		}
		
		tv.setText(s+text);
	}
	class ViewHolder{
		ImageView image;
		TextView  paytitle;
		TextView paytime;
		TextView paymoney;
		TextView paystatus;
	}
	
}
