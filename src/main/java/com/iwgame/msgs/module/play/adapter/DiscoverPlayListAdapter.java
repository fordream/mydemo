/**      
 * DiscoverPlayListAdapter.java Create on 2015-5-8     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.play.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.play.util.PlayUtil;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: DiscoverPlayListAdapter
 * @Description: 发现陪玩列表适配器
 * @author 王卫
 * @date 2015-5-8 下午3:30:31
 * @Version 1.0
 * 
 */
public class DiscoverPlayListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<Object> data;
	private UserVo loginUserVo = null;
	private final int TYPE_DETAIL = 0;
	private final int TYPE_NODATA = 1;
	private final int VIEW_TYPE = 2;
	private int noDataHeight;//用于在发现页面当没有获取到数据的时候，显示默认图片的高度
//	public final static String NODATA = "nodata";

	
	
	public int getNoDataHeight() {
		return noDataHeight;
	}

	public void setNoDataHeight(int noDataHeight) {
		this.noDataHeight = noDataHeight;
	}
	
	/**
	 * 
	 */
	public DiscoverPlayListAdapter(Context context, List<Object> data) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
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

	@Override
	public int getItemViewType(int position) {
		if(getItem(position) instanceof PlayInfo){
			return TYPE_DETAIL;
		}else{
			return TYPE_NODATA;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewHolder holder1 = new ViewHolder();
		NodateViewHolder nodataHolder = new NodateViewHolder();
		if (convertView == null) {
			switch (type) {
			case TYPE_DETAIL:
				convertView = inflater.inflate(R.layout.play_list_item, null);
				holder1.age = (TextView) convertView.findViewById(R.id.age);
				holder1.bottom = (View) convertView.findViewById(R.id.bottom);
				holder1.creatTime = (TextView) convertView.findViewById(R.id.creatTime);
				holder1.creatTimeTitle = (TextView) convertView.findViewById(R.id.creatTimeTitle);
				holder1.desc = (TextView) convertView.findViewById(R.id.desc);
				holder1.grade = (TextView) convertView.findViewById(R.id.grade);
				holder1.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder1.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holder1.playIcon = (ImageView) convertView.findViewById(R.id.playIcon);
				holder1.playInfoContent = (LinearLayout) convertView.findViewById(R.id.playInfoContent);
				holder1.status = (TextView) convertView.findViewById(R.id.status);
				holder1.timeStatus = (RelativeLayout) convertView.findViewById(R.id.timeStatus);
				holder1.ucount = (TextView) convertView.findViewById(R.id.ucount);
				holder1.userInfo = (RelativeLayout) convertView.findViewById(R.id.userInfo);
				holder1.star_layout = (LinearLayout)convertView.findViewById(R.id.user_start_layout);
				holder1.star1 = (ImageView)convertView.findViewById(R.id.user_start_img1);
				holder1.star2 = (ImageView)convertView.findViewById(R.id.user_start_img2);
				holder1.star3 = (ImageView)convertView.findViewById(R.id.user_start_img3);
				holder1.star4 = (ImageView)convertView.findViewById(R.id.user_start_img4);
				holder1.star5 = (ImageView)convertView.findViewById(R.id.user_start_img5);
				holder1.pingjia = (TextView)convertView.findViewById(R.id.user_pingjia_haoping_textview);
				holder1.pingjia_percent = (TextView)convertView.findViewById(R.id.user_pingjia_percent_textview);
				convertView.setTag(holder1);
				break;
			case TYPE_NODATA:
				LinearLayout v = (LinearLayout)View.inflate(SystemContext.getInstance().getContext(), R.layout.no_data_view_discover, null);
				convertView = v;
				nodataHolder.bg_layout = v;
				nodataHolder.bgIcon = (ImageView)convertView.findViewById(R.id.bgIcon);
				convertView.setTag(nodataHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_DETAIL:
				holder1 = (ViewHolder) convertView.getTag();
				break;
			case TYPE_NODATA:
				nodataHolder = (NodateViewHolder)convertView.getTag();
				break;
			}
		}

		final ViewHolder holder = holder1;
		final NodateViewHolder nodateViewHolder = nodataHolder;
		switch (type) {
		case TYPE_DETAIL:
			// 设置界面数据
			Object itemData = getItem(position);
			if (itemData instanceof PlayInfo)
				setItemDetail(holder, (PlayInfo) itemData);
			break;
		case TYPE_NODATA:
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, noDataHeight);
			nodateViewHolder.bg_layout.setLayoutParams(params);
			nodateViewHolder.bgIcon.setBackgroundResource(R.drawable.common_no_seach_play);
			break;
		}
		return convertView;
	}

	/**
	 * 设置界面数据
	 * 
	 * @param holder
	 * @param playInfo
	 */
	private void setItemDetail(ViewHolder holder, PlayInfo playInfo) {
		holder.userInfo.setVisibility(View.VISIBLE);
		// 复用清空之前数据
		holder.playInfoContent.removeAllViews();
		holder.nickname.setText("");
		holder.grade.setText("");
		holder.desc.setText("");
		holder.ucount.setText("");
		// 用户信息
		final UserInfoDetail userInfo = playInfo.getUserInfo();
		if (userInfo != null) {
			ImageViewUtil.showImage(holder.icon, userInfo.getAvatar(), R.drawable.common_default_icon);
			// 设置年龄和性别
			setSexAddAge(holder.age, userInfo.getAge(), userInfo.getSex());
			holder.nickname.setText(userInfo.getNickname());
			holder.grade.setText("LV" + userInfo.getGrade());
			String desc = "";
			if (userInfo.getDistance() > 0)
				desc += DistanceUtil.covertDistance(userInfo.getDistance()) + " | ";
			desc += SafeUtils.getDate2MyStr2(userInfo.getLastLogin());
			holder.desc.setText(desc);
			// 设置头像单击
			holder.icon.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View paramView) {
					if (userInfo.getId() != loginUserVo.getUserid()) {
						// 打开对方资料
						Intent intent = new Intent(context, UserDetailInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, userInfo.getId());
						intent.putExtras(bundle);
						context.startActivity(intent);
					} else {
						ToastUtil.showToast(context, "查看自己的信息去我的页面");
					}
				}

			});
			//设置星星显示的个数
			int star = playInfo.getStar();
			if(star<=0){
				holder.star_layout.setVisibility(View.GONE);
			}else{
				holder.star_layout.setVisibility(View.VISIBLE);
				switch (star) {
				case 1:
					holder.star1.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star2.setBackgroundResource(R.drawable.peiwan_pingjia2);
					holder.star3.setBackgroundResource(R.drawable.peiwan_pingjia2);
					holder.star4.setBackgroundResource(R.drawable.peiwan_pingjia2);
					holder.star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
					break;
				case 2:
					holder.star1.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star2.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star3.setBackgroundResource(R.drawable.peiwan_pingjia2);
					holder.star4.setBackgroundResource(R.drawable.peiwan_pingjia2);
					holder.star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
					break;
				case 3:
					holder.star1.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star2.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star3.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star4.setBackgroundResource(R.drawable.peiwan_pingjia2);
					holder.star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
					break;
				case 4:
					holder.star1.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star2.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star3.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star4.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star5.setBackgroundResource(R.drawable.peiwan_pingjia2);
					break;
				default:
					holder.star1.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star2.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star3.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star4.setBackgroundResource(R.drawable.peiwan_pingjia);
					holder.star5.setBackgroundResource(R.drawable.peiwan_pingjia);
					break;
				}
			}
			if(playInfo.getPraise()<=0){
				holder.pingjia.setVisibility(View.GONE);
				holder.pingjia_percent.setVisibility(View.GONE);
			}else{
				holder.pingjia.setVisibility(View.VISIBLE);
				holder.pingjia_percent.setVisibility(View.VISIBLE);
				holder.pingjia_percent.setText(playInfo.getPraise()+"%");
			}
		}
		holder.ucount.setText("(" + playInfo.getJoinnum() + "人报名)");
		if (playInfo.getJoinnum() > 0)
			holder.ucount.setVisibility(View.VISIBLE);
		else
			holder.ucount.setVisibility(View.GONE);
		// 设置图片
		new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(playInfo.getResourceid()), 0, holder.playIcon, R.drawable.common_default_icon);
		// 其他详情设置
		PlayUtil.addAllPlayAttrView(context, inflater, holder.playInfoContent, playInfo);
	}

	/**
	 * 设置用户的年龄和性别的方法
	 */
	private void setSexAddAge(TextView ageTxt, int age, int sex) {
		if (age > 0) {
			ageTxt.setText(AgeUtil.convertAgeByBirth(age) + "");
			ageTxt.setVisibility(View.VISIBLE);

			if (sex == 0) {
				Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_man_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				ageTxt.setCompoundDrawables(sexdraw, null, null, null);
				ageTxt.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
				ageTxt.setBackgroundResource(R.drawable.common_item_jh2_shap);
			} else if (sex == 1) {
				Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_woman_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				ageTxt.setCompoundDrawables(sexdraw, null, null, null);
				ageTxt.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
				ageTxt.setBackgroundResource(R.drawable.common_item_jh_shap);
			} else {
				ageTxt.setCompoundDrawablePadding(0);
				ageTxt.setBackgroundResource(R.drawable.common_item_jh2_shap);
			}
		} else {
			ageTxt.setText("");
			ageTxt.setCompoundDrawablePadding(0);
			if (sex == 0) {
				Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_man_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				ageTxt.setCompoundDrawables(sexdraw, null, null, null);
				ageTxt.setCompoundDrawablePadding(0);
				ageTxt.setBackgroundResource(R.drawable.common_item_jh2_shap);
				ageTxt.setVisibility(View.VISIBLE);
			} else if (sex == 1) {
				ageTxt.setVisibility(View.VISIBLE);
				Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_woman_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				ageTxt.setCompoundDrawables(sexdraw, null, null, null);
				ageTxt.setCompoundDrawablePadding(0);
				ageTxt.setBackgroundResource(R.drawable.common_item_jh_shap);
			} else {
				ageTxt.setVisibility(View.GONE);
			}
		}
	}

	class ViewHolder {
		public RelativeLayout userInfo;
		public ImageView icon;
		public TextView nickname;
		public TextView age;
		public TextView grade;
		public TextView desc;
		
		public LinearLayout star_layout;
		public ImageView star1;
		public ImageView star2;
		public ImageView star3;
		public ImageView star4;
		public ImageView star5;
		public TextView pingjia;
		public TextView pingjia_percent;

		public RelativeLayout timeStatus;
		public TextView creatTime;
		public TextView creatTimeTitle;
		public TextView status;

		public LinearLayout playInfoContent;
		public ImageView playIcon;
		public TextView ucount;

		public View bottom;
	}
	
	/** 
	* @ClassName: NodateViewHolder 
	* @Description: TODO：没有数据的时候的viewholder
	* @author 彭赞 
	* @date 2015-6-4 下午5:22:39 
	* @Version 1.0 
	*/
	class NodateViewHolder {
		LinearLayout bg_layout;
		ImageView bgIcon;
	}
}
