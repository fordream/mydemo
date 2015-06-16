package com.iwgame.msgs.module.setting.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.module.setting.vo.Goods;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.SimpleDateFormateUtil;
import com.youban.msgs.R;
import android.app.AlarmManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 积分商城的列表
 * @author jczhang
 *
 */
public class GoodsListAdapter extends BaseAdapter {

private Context context;
private List<Goods> list;

public GoodsListAdapter(Context context,List<Goods> list){
	this.context = context;
	if(list != null){
		this.list = list;
	}else{
		list = new ArrayList<Goods>();
	}
}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Goods getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return list.get(arg0).getId();
	}

	/**
	 * 最重要的一个方法 
	 * 获取到view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.change_goods_item_view, null);
			holder.icon = (ImageView)convertView.findViewById(R.id.change_record_icon);
			holder.goodsName = (TextView)convertView.findViewById(R.id.goods_name);
			holder.haveReceive = (TextView)convertView.findViewById(R.id.have_receive);
			holder.remainNum = (TextView)convertView.findViewById(R.id.surplus_num);
			holder.changeDate = (TextView)convertView.findViewById(R.id.change_date);
			holder.changeTime = (TextView)convertView.findViewById(R.id.change_time);
			holder.cosumePoint = (TextView)convertView.findViewById(R.id.cosume_point);
			holder.needGrade = (TextView)convertView.findViewById(R.id.need_grade);
			holder.activityState = (TextView)convertView.findViewById(R.id.activity_state);
			holder.cosumPointDesc = (TextView)convertView.findViewById(R.id.cosume_point_desc);
			holder.needGradeDesc = (TextView)convertView.findViewById(R.id.need_grade_desc);
			holder.goodsDescState = (TextView)convertView.findViewById(R.id.goods_desc_state);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		AlarmManager mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mAlarmManager.setTimeZone("GMT+08:00");
		Goods goods = list.get(position);
		ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(goods.getIcon()), holder.icon, R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
		holder.goodsName.setText(goods.getName()+"");
		holder.haveReceive.setText(""+goods.getObtainNum());
		holder.remainNum.setText(""+goods.getRemainNum());
		if(goods.getNeedPoint() <= 0){
			holder.cosumPointDesc.setVisibility(View.GONE);
			holder.cosumePoint.setText("免费");
		}else{
			holder.cosumePoint.setVisibility(View.VISIBLE);
			holder.cosumPointDesc.setVisibility(View.VISIBLE);
			holder.cosumePoint.setText(goods.getNeedPoint()+"");
		}
		if(goods.getNeedLevel() <= 0){
			holder.needGrade.setVisibility(View.INVISIBLE);
			holder.needGradeDesc.setVisibility(View.INVISIBLE);
		}else{
			holder.needGrade.setVisibility(View.VISIBLE);
			holder.needGradeDesc.setVisibility(View.VISIBLE);
			holder.needGrade.setText("LV"+goods.getNeedLevel());
		}
		if(goods.getGoodsStatus() == 3){
			holder.activityState.setText("兑换完");
			holder.goodsDescState.setText("兑换截止：");
			holder.changeDate.setText(SimpleDateFormateUtil.switchToDate(goods.getOffTime()));
			holder.changeTime.setText(SimpleDateFormateUtil.toTimeNoscecond(goods.getOffTime()));
			holder.activityState.setTextColor(context.getResources().getColor(R.color.market_pay_over_color));
		}else if(goods.getGoodsStatus() == 2){
			holder.activityState.setText("未开始");
			holder.goodsDescState.setText("开始兑换：");
			holder.changeDate.setText(SimpleDateFormateUtil.switchToDate(goods.getTransTime()));
			holder.changeTime.setText(SimpleDateFormateUtil.toTimeNoscecond(goods.getTransTime()));
			holder.activityState.setTextColor(context.getResources().getColor(R.color.global_color3));
		}else if(goods.getGoodsStatus() == 4){
			return null;
		}else if(goods.getGoodsStatus() == 0 || goods.getGoodsStatus() == 1){
			holder.activityState.setText("进行中");
			holder.goodsDescState.setText("兑换截止：");
			holder.changeDate.setText(SimpleDateFormateUtil.switchToDate(goods.getOffTime()));
			holder.changeTime.setText(SimpleDateFormateUtil.toTimeNoscecond(goods.getOffTime()));
			holder.activityState.setTextColor(context.getResources().getColor(R.color.activity_state_color_green));
		}
		return convertView;
	}
	
	/*
	 * 定义一个
	 * 句柄类
	 */
	static class ViewHolder{
		ImageView icon;
		TextView goodsName;
		TextView haveReceive;
		TextView remainNum;
		TextView changeDate;
		TextView changeTime;
		TextView cosumePoint;
		TextView needGrade;
		TextView activityState;
		TextView cosumPointDesc;
		TextView needGradeDesc;
		TextView goodsDescState;
	}
	
	
	
}
