package com.iwgame.msgs.module.user.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.iwgame.msgs.module.user.object.UserPointDetailObj;
import com.iwgame.msgs.utils.SimpleDateFormateUtil;
import com.youban.msgs.R;


public class UserPointDetailAdapter extends BaseAdapter {

	List<UserPointDetailObj> list = null;
	LayoutInflater inflater;
	Context context;
	String type;
	/**
	 * 构造方法
	 * 主要是做了一些初始化操作
	 * @param context
	 * @param list
	 */
	public UserPointDetailAdapter(Context context,List<UserPointDetailObj>list,String type){
		if(list == null){
			this.list = new ArrayList<UserPointDetailObj>();
		}else{
			this.list = list;
		}
		this.context = context;
		this.type = type;
		inflater = LayoutInflater.from(context);
	}
	
	
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	
	/**
	 * 这里是获取到显示到listview上的每一个itemView
	 * 在这个方法里面可以做一些优化操作
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.personal_integral_item_view, null);
			holder = new ViewHolder();
			holder.optime_date = (TextView)convertView.findViewById(R.id.personal_integral_time);
			holder.optime_time = (TextView)convertView.findViewById(R.id.personal_integral_time_second);
			holder.aname = (TextView)convertView.findViewById(R.id.personal_integral_action);
			holder.point = (TextView)convertView.findViewById(R.id.personal_integral_detail);
			holder.totalPoint = (TextView)convertView.findViewById(R.id.personal_integral_total);
			holder.image = (ImageView)convertView.findViewById(R.id.personal_integral_detail_picture);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		UserPointDetailObj entity = list.get(position);
		AlarmManager mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		mAlarmManager.setTimeZone("GMT+08:00");
		holder.optime_date.setText(SimpleDateFormateUtil.switchToDate(entity.getOptime()*1000l));
		holder.optime_time.setText(SimpleDateFormateUtil.switchToTime(entity.getOptime()*1000l));
		holder.aname.setText(entity.getAname());
		if(type.equals("experience")){
			holder.image.setBackgroundResource(R.drawable.setting_main_jingyanzhi_label);
		}
		if(entity.getPoint() >= 0){
			holder.point.setText("+"+entity.getPoint()+"");
		}else{
			holder.point.setText(entity.getPoint()+"");
		}
		
		holder.totalPoint.setText("总："+entity.getTotalpoint()+"");
		return convertView;
	}

	
	
	/**
	 * 定义一个内部类
	 * 句柄类
	 * 用于控件的重复利用
	 * 可以达到优化的目的
	 * @author Administrator
	 *
	 */
	static class ViewHolder{
		TextView optime_date;
		TextView optime_time;
		TextView aname;
		TextView point;
		TextView totalPoint;
		ImageView image;
	}
}
