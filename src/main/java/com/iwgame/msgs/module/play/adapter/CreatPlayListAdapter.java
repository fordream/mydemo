/**      
 * DiscoverPlayListAdapter.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.play.ui.PlayDetailInfoActivity;
import com.iwgame.msgs.module.play.util.PlayUtil;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.UserRoleData;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr.ValData;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.youban.msgs.R;

/**
 * @ClassName: DiscoverPlayListAdapter
 * @Description: 创建陪玩列表适配器
 * @author 王卫
 * @date 2015-5-8 下午3:30:31
 * @Version 1.0
 * 
 */
public class CreatPlayListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Object> data;

	/**
	 * 
	 */
	public CreatPlayListAdapter(Context context, List<Object> data) {
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

		// 设置界面数据
		setItemDetail(holder, (PlayInfo) getItem(position));
		return convertView;
	}

	/**
	 * 设置界面数据
	 * 
	 * @param holder
	 * @param playInfo
	 */
	private void setItemDetail(ViewHolder holder, final PlayInfo playInfo) {
		holder.timeStatus.setVisibility(View.VISIBLE);
		holder.timeStatus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, PlayDetailInfoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, playInfo.getPlayid());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		// 复用清空之前数据
		holder.creatTime.setText("");
		holder.status.setText("");
		holder.playInfoContent.removeAllViews();
		holder.creatTimeTitle.setText("创建时间 ");
		holder.creatTime.setText(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(playInfo.getCreatetime())) + " >");
		// 设置状态
		int status = playInfo.getStatus();
		int colorId = R.color.play_list_item_status_publish_color;
		String statusDesc = "已发布";
		int visible = View.GONE;
		if (SystemContext.getInstance().getUserPlayStatus() == MsgsConstants.USER_PLAY_STATUS_OPEN) {
			if (status == MsgsConstants.PLAY_STATUS_APPROVAL_NO) {// 未通过
				colorId = R.color.play_list_item_status_approvalno_color;
				statusDesc = "未通过";
				visible = View.VISIBLE;
			} else if (status == MsgsConstants.PLAY_STATUS_CLOSED) {// 已关闭
				colorId = R.color.play_list_item_status_close_color;
				statusDesc = "已关闭";
				visible = View.VISIBLE;
			} else if (status == MsgsConstants.PLAY_STATUS_DOWN) {// 已下架
				colorId = R.color.play_list_item_status_down_color;
				statusDesc = "已关闭";
				visible = View.VISIBLE;
			} else if (status == MsgsConstants.PLAY_STATUS_INIT) {// 审核中
				colorId = R.color.play_list_item_status_init_color;
				statusDesc = "审核中";
				visible = View.VISIBLE;
			} else if (status == MsgsConstants.PLAY_STATUS_PUBLISH) {// 已发布
				colorId = R.color.play_list_item_status_publish_color;
				statusDesc = "已发布";
				visible = View.GONE;
			}
		} else {
			colorId = R.color.play_list_item_status_down_color;
			statusDesc = "已封停";
			visible = View.VISIBLE;
		}
		holder.status.setTextColor(context.getResources().getColor(colorId));
		holder.status.setText(statusDesc);
		holder.status.setVisibility(visible);
		// 设置图片
		new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(playInfo.getResourceid()), 0, holder.playIcon, R.drawable.common_default_icon);
		// 其他详情设置
		PlayUtil.addAllPlayAttrView(context, inflater, holder.playInfoContent, playInfo);
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
