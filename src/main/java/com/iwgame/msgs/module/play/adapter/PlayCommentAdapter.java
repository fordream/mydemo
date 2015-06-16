/**      
* PlayCommentAdapter.java Create on 2015-5-29     
*      
* Copyright (c) 2015 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.module.play.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.play.ui.PlayDetailInfoActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs.PlayEvalInfo;
import com.iwgame.msgs.proto.Msgs.PlayEvalInfo.PlayEvalReply;
import com.iwgame.msgs.proto.Msgs.QuotesReplyDetail;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.MyTextView;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/** 
 * @ClassName: PlayCommentAdapter 
 * @Description: 陪玩评价适配器
 * @author xingjianlong
 * @date 2015-5-29 下午5:33:30 
 * @Version 1.0
 * 
 */
public class PlayCommentAdapter extends BaseAdapter {
		
		private Context context;
		private LayoutInflater inflater;
		private List<Object> list;
		private UserVo loginUserVo = null;
		private ReplyButtonOnClickListener replyButtonOnClickListener;
		private Map<Long, List<PlayEvalReply>> evalReply;
		private long uid;
		public PlayCommentAdapter(Context context,List<Object> list,long uid){
			this.context = context;
			this.list = list;
			this.uid = uid;
			evalReply = new HashMap<Long, List<PlayEvalReply>>();
			loginUserVo = SystemContext.getInstance().getExtUserVo();
			inflater = LayoutInflater.from(context);
		}
		/**
		 * 设置回复的监听
		 * 
		 * @param listener
		 */
		public void setReplyButtonOnClickListener(ReplyButtonOnClickListener listener) {
			replyButtonOnClickListener = listener;
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
	public Object getItem(int position) {
		return list.get(position);
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.play_detail_info_remark, null);
			holder.icon =(ImageView)convertView.findViewById(R.id.play_remark_icon);
			holder.username =(TextView)convertView.findViewById(R.id.play_remark_user_name);
			holder.star1=(ImageView)convertView.findViewById(R.id.play_user_remark_star1);
			holder.star2=(ImageView)convertView.findViewById(R.id.play_user_remark_star2);
			holder.star3=(ImageView)convertView.findViewById(R.id.play_user_remark_star3);
			holder.star4=(ImageView)convertView.findViewById(R.id.play_user_remark_star4);
			holder.star5=(ImageView)convertView.findViewById(R.id.play_user_remark_star5);
			holder.time=(TextView)convertView.findViewById(R.id.play_remark_time);
			holder.distance=(TextView)convertView.findViewById(R.id.play_remark_meter);
			holder.content=(MyTextView)convertView.findViewById(R.id.remark_content);
			holder.replyView=(LinearLayout)convertView.findViewById(R.id.user_remark_content);
			holder.reply=(TextView)convertView.findViewById(R.id.play_remark_reply);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		PlayEvalInfo info = (PlayEvalInfo) list.get(position);
		setViewData(info, holder,position);
		return convertView;
	}
	
	private void setViewData(final PlayEvalInfo info,ViewHolder holder,final int position){
		final UserInfoDetail user =info.getUserInfo();
		if(user!=null){
			new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(user.getAvatar()), 0, holder.icon, R.drawable.common_default_icon);
			
			holder.username.setText(user.getNickname().length()>1?user.getNickname().subSequence(0, 1)+"**":user.getNickname());
			switch(info.getStar()){
			case 0:
				holder.star1.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star2.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star3.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star4.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star5.setImageResource(R.drawable.peiwan_pingjia2);
				break;
			case 1:
				holder.star1.setImageResource(R.drawable.peiwan_pingjia);
				holder.star2.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star3.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star4.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star5.setImageResource(R.drawable.peiwan_pingjia2);
				
				break;
			case 2:
				holder.star1.setImageResource(R.drawable.peiwan_pingjia);
				holder.star2.setImageResource(R.drawable.peiwan_pingjia);
				holder.star3.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star4.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star5.setImageResource(R.drawable.peiwan_pingjia2);
				break;
			case 3:
				holder.star1.setImageResource(R.drawable.peiwan_pingjia);
				holder.star2.setImageResource(R.drawable.peiwan_pingjia);
				holder.star3.setImageResource(R.drawable.peiwan_pingjia);
				holder.star4.setImageResource(R.drawable.peiwan_pingjia2);
				holder.star5.setImageResource(R.drawable.peiwan_pingjia2);
				break;
			case 4:
				holder.star1.setImageResource(R.drawable.peiwan_pingjia);
				holder.star2.setImageResource(R.drawable.peiwan_pingjia);
				holder.star3.setImageResource(R.drawable.peiwan_pingjia);
				holder.star4.setImageResource(R.drawable.peiwan_pingjia);
				holder.star5.setImageResource(R.drawable.peiwan_pingjia2);
				break;
			case 5:
				holder.star1.setImageResource(R.drawable.peiwan_pingjia);
				holder.star2.setImageResource(R.drawable.peiwan_pingjia);
				holder.star3.setImageResource(R.drawable.peiwan_pingjia);
				holder.star4.setImageResource(R.drawable.peiwan_pingjia);
				holder.star5.setImageResource(R.drawable.peiwan_pingjia);
				break;
			}
			holder.time.setText(SafeUtils.getDate2MyStr2(info.getCreatetime()));
			holder.distance.setText(DistanceUtil.covertDistance(user.getDistance()));
			holder.content.setText(SmileyUtil.ReplaceSmiley(context, 
					info.getContent(),
					context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width_level2), context.getResources()
							.getDimensionPixelSize(R.dimen.global_string_smiley_heigth_level2)));
			if(uid==loginUserVo.getUserid()){
				holder.reply.setVisibility(View.VISIBLE);
			}else{
				holder.reply.setVisibility(View.GONE);
			}
			if(evalReply.containsKey(info.getId())){
				if(evalReply.get(info.getId()).size()>=1){
					holder.reply.setVisibility(View.GONE);
				}
				addRelyContent(holder.replyView, evalReply.get(info.getId()));
			}else{
				if(info.getEvalReplyList().size()>=1){
					holder.reply.setVisibility(View.GONE);
					addRelyContent(holder.replyView, info.getEvalReplyList());
					evalReply.put(info.getId(), info.getEvalReplyList());
				}
			}
			
//			holder.icon.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					if(info.getUserInfo().getId()!= loginUserVo.getUserid()){
//						// 打开对方资料
//						Intent intent = new Intent(context, UserDetailInfoActivity.class);
//						Bundle bundle = new Bundle();
//						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, info.getUserInfo().getId());
//						intent.putExtras(bundle);
//						context.startActivity(intent);
//					}else{
//						ToastUtil.showToast(context, "查看自己的信息去我的页面");
//					}
//				}
//			});
			holder.reply.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (replyButtonOnClickListener != null) {
						long id = info.getId();
						replyButtonOnClickListener.onClick(position,id,user,loginUserVo.getUsername());
					}
				}
			});
		}
	}
	private void addRelyContent(LinearLayout view,List<PlayEvalReply> replylist){
		if(replylist.size()==0||replylist==null){
			view.setVisibility(View.GONE);
			return;
		}
			view.setVisibility(View.VISIBLE);
			view.removeAllViews();
		for(int i =0;i<replylist.size();i++){
			PlayEvalReply re = replylist.get(i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			View reply = View.inflate(context, R.layout.play_reply_content_item, null);
			TextView  replyuser = (TextView)reply.findViewById(R.id.play_reply_user);
			MyTextView replycontent=(MyTextView)reply.findViewById(R.id.play_replay_content);
			replyuser.setText(re.getNickname());
//			replyuser.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					if(uid!= loginUserVo.getUserid()){
//						// 打开对方资料
//						Intent intent = new Intent(context, UserDetailInfoActivity.class);
//						Bundle bundle = new Bundle();
//						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
//						intent.putExtras(bundle);
//						context.startActivity(intent);
//					}else{
//						ToastUtil.showToast(context, "查看自己的信息去我的页面");
//					}
//					
//				}
//			});
			replycontent.setText(SmileyUtil.ReplaceSmiley(context, "回复 " + re.getTonickname() + ": "
					+ re.getContent(),
					context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width_level2), context.getResources()
							.getDimensionPixelSize(R.dimen.global_string_smiley_heigth_level2)));
			view.addView(reply, params);
		}
	}
	
	class ViewHolder{
		ImageView icon;
		TextView  username;
		ImageView star1;
		ImageView star2;
		ImageView star3;
		ImageView star4;
		ImageView star5;
		TextView   time;
		TextView   distance;
		MyTextView  content;
		TextView reply;
		LinearLayout replyView;
	}
	// 点击回复的回调
		public interface ReplyButtonOnClickListener {

			public void onClick(int position ,long id,UserInfoDetail info,String loginuser);

			public void onClickReply(QuotesReplyDetail detail, int position);
		}
		public Map<Long, List<PlayEvalReply>> getEvalReply() {
			return evalReply;
		}
		public void setEvalReply(Map<Long, List<PlayEvalReply>> evalReply) {
			this.evalReply = evalReply;
		}
}
