/**      
 * DiscoverPlayListAdapter.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.play.ui.PlayDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs.PlayOrderInfo;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.youban.msgs.R;

/**
 * @ClassName: DiscoverPlayListAdapter
 * @Description: 报名陪玩列表适配器
 * @author 王卫
 * @date 2015-5-8 下午3:30:31
 * @Version 1.0
 * 
 */
public class ReplyPlayListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Object> data;

	/**
	 * 
	 */
	public ReplyPlayListAdapter(Context context, List<Object> data) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (data != null)
			return data.size();
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		if (data != null)
			return data.get(position);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.play_list_item, null);
			holder.age = (TextView) convertView.findViewById(R.id.age);
			holder.bottom = (View) convertView.findViewById(R.id.bottom);
			holder.creatTime = (TextView) convertView.findViewById(R.id.creatTime);
			holder.creatTimeTitle = (TextView) convertView.findViewById(R.id.creatTimeTitle);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.grade = (TextView) convertView.findViewById(R.id.grade);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.playIcon = (ImageView) convertView.findViewById(R.id.playIcon);
			holder.playInfoContent = (LinearLayout) convertView.findViewById(R.id.playInfoContent);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			holder.timeStatus = (RelativeLayout) convertView.findViewById(R.id.timeStatus);
			holder.ucount = (TextView) convertView.findViewById(R.id.ucount);
			holder.userInfo = (RelativeLayout) convertView.findViewById(R.id.userInfo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.timeStatus.setVisibility(View.VISIBLE);
		// 设置界面数据
		setItemDetail(holder, (PlayOrderInfo) getItem(position));
		return convertView;
	}

	/**
	 * 设置界面数据
	 * 
	 * @param holder
	 * @param playOrderInfo
	 */
	private void setItemDetail(ViewHolder holder, final PlayOrderInfo playOrderInfo) {
		holder.timeStatus.setVisibility(View.VISIBLE);
		holder.timeStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PlayDetailInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, playOrderInfo.getPlayid());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		// 复用清空之前数据
		holder.creatTimeTitle.setText("报名时间 ");
		holder.creatTime.setText("");
		holder.status.setText("");
		holder.playInfoContent.removeAllViews();

		holder.creatTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(playOrderInfo.getCreatetime())) + " >");
		// 设置状态
		int status = playOrderInfo.getStatus();
		int colorId = R.color.play_list_item_status_publish_color;
		String statusDesc = "待确认";
		if (status == MsgsConstants.PLAY_ORDER_STATUS_INIT) {// 待确认
			colorId = R.color.play_list_item_order_status_init_color;
			statusDesc = "待确认";
		} else if (status == MsgsConstants.PLAY_ORDER_STATUS_PAY) {// 待付款
			colorId = R.color.play_list_item_order_status_pay_color;
			statusDesc = "待付款";
		} else if (status == MsgsConstants.PLAY_ORDER_STATUS_APPEAL) {// 申诉中
			colorId = R.color.play_list_item_order_status_appeal_color;
			statusDesc = "申诉中";
		} else if (status == MsgsConstants.PLAY_ORDER_STATUS_EVAL) {// 待评价
			colorId = R.color.play_list_item_order_status_eval_color;
			statusDesc = "待评价";
		} else if (status == MsgsConstants.PLAY_ORDER_STATUS_CANCEL) {// 已取消
			colorId = R.color.play_list_item_order_status_cancel_color;
			statusDesc = "已取消";
		} else if (status == MsgsConstants.PLAY_ORDER_STATUS_END) {// 已结束
			colorId = R.color.play_list_item_order_status_end_color;
			statusDesc = "已结束";
		} else if (status == MsgsConstants.PLAY_ORDER_STATUS_TIMEOUT) {// 已过期
			colorId = R.color.play_list_item_order_status_tiemout_color;
			statusDesc = "已过期";
		}
		holder.status.setTextColor(context.getResources().getColor(colorId));
		holder.status.setText(statusDesc);
		// 设置图片
		new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(playOrderInfo.getResourceid()), 0, holder.playIcon, R.drawable.common_default_icon);
		// 其他详情设置
		View view = inflater.inflate(R.layout.play_list_item_detail_item, holder.playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("游戏");
		((TextView) view.findViewById(R.id.desc)).setText(playOrderInfo.getGamename());
		holder.playInfoContent.addView(view);
		view = inflater.inflate(R.layout.play_list_item_detail_item, holder.playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("服务器");
		((TextView) view.findViewById(R.id.desc)).setText(playOrderInfo.getServername());
		holder.playInfoContent.addView(view);
		view = inflater.inflate(R.layout.play_list_item_detail_item, holder.playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("角色名");
		((TextView) view.findViewById(R.id.desc)).setText(playOrderInfo.getRolename());
		holder.playInfoContent.addView(view);
		view = inflater.inflate(R.layout.play_list_item_detail_item, holder.playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("预约时间");
		((TextView) view.findViewById(R.id.desc)).setText(new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(playOrderInfo.getStarttime())));
		holder.playInfoContent.addView(view);
		view = inflater.inflate(R.layout.play_list_item_detail_item, holder.playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("陪玩时长");
		((TextView) view.findViewById(R.id.desc)).setText(playOrderInfo.getDuration() + "小时");
		holder.playInfoContent.addView(view);
		view = inflater.inflate(R.layout.play_list_item_detail_item, holder.playInfoContent, false);
		((TextView) view.findViewById(R.id.dname)).setText("总计费用");
		TextView costTxt = ((TextView) view.findViewById(R.id.desc));
		costTxt.setText((playOrderInfo.getDuration() * playOrderInfo.getCost()) + "U币");
		costTxt.setTextColor(context.getResources().getColor(R.color.play_list_item_detail_desc_color2));
		holder.playInfoContent.addView(view);
	}

	class ViewHolder {
		public RelativeLayout userInfo;
		public ImageView icon;
		public TextView nickname;
		public TextView age;
		public TextView grade;
		public TextView desc;

		public RelativeLayout timeStatus;
		public TextView creatTime;
		public TextView creatTimeTitle;
		public TextView status;

		public LinearLayout playInfoContent;
		public ImageView playIcon;
		public TextView ucount;

		public View bottom;
	}
}
