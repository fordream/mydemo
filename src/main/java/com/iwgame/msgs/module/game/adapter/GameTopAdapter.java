/**      
 * FollowGameAdapter.java Create on 2014-4-9     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.game.object.GameData;
import com.iwgame.msgs.module.game.object.GameTopItemObj;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.GameUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: FollowGameAdapter
 * @Description: TODO(贴吧排行榜适配器)
 * @author chuanglong
 * @date 2014-4-9 下午4:35:52
 * @Version 1.0
 * 
 */
public class GameTopAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	FollowGameListener followGameListener =null ;
	private UserVo loginUserVo = null;
	/**
	 * 数据
	 */
	List<GameTopItemObj> data;

	private Map<Long, GameVo> gameVo_cache = new HashMap<Long, GameVo>();

	public GameTopAdapter(Context context, List<GameTopItemObj> data) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return data.get(position);
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
		ViewHolder tmpViewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.game_topgame_list_item, null);
			tmpViewHolder.icon = (RoundedImageView) convertView.findViewById(R.id.icon);
			tmpViewHolder.topview  = (ImageView) convertView.findViewById(R.id.topview);
			tmpViewHolder.gamename  = (TextView) convertView.findViewById(R.id.gamename);
			tmpViewHolder.desc_info = (TextView) convertView.findViewById(R.id.desc_info);
			tmpViewHolder.desc_info2 = (TextView) convertView.findViewById(R.id.desc_info2);
			tmpViewHolder.btn_follow  = (Button) convertView.findViewById(R.id.btn_follow);
			tmpViewHolder.topic_title  = (TextView) convertView.findViewById(R.id.topic_title);
			tmpViewHolder.topic_title_fgx  = (View) convertView.findViewById(R.id.topic_title_fgx);
			tmpViewHolder.topic_ll = (LinearLayout) convertView.findViewById(R.id.topic_ll);
			convertView.setTag(tmpViewHolder);

		} else {
			tmpViewHolder = (ViewHolder) convertView.getTag();
		}
		// 给予初始值，防止列表复用时显示错乱
		final ViewHolder viewHolder = tmpViewHolder;
		viewHolder.icon.setImageResource(R.drawable.common_default_icon);
		
		final GameTopItemObj item = data.get(position);
		//设置图片和名称
		if(item.getGid() <= 0){
			//贴吧id有问题
			viewHolder.icon.setTag("drawable://" + R.drawable.common_default_icon);
			viewHolder.icon.setImageResource(R.drawable.common_default_icon);

			viewHolder.gamename.setText(""); 
		}else{
			if(item.getNickname() != null){
				viewHolder.gamename.setText(item.getNickname()); 
			}
			if(item.getGameLogo() != null){
				ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(item.getGameLogo()), viewHolder.icon, R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
			}
		}

		// 设置排行榜数值
		setTop(position + 1, viewHolder.topview);
		// 是否关注
		boolean follow = item.isFollow();
		if (follow) {
			viewHolder.btn_follow.setClickable(false);
			viewHolder.btn_follow.setEnabled(false);
		} else {
			viewHolder.btn_follow.setClickable(true);
			viewHolder.btn_follow.setEnabled(true);
			viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					viewHolder.btn_follow.setClickable(false);
						//viewHolder.btn_follow.setEnabled(false);
						//需要判断关注贴吧的个数，来确定是否需要发送到服务端
						followGame(viewHolder.btn_follow,item,MsgsConstants.OP_FOLLOW);
				}
			});

		}
		//设置赞踩数
		//	viewHolder.desc_info.setText(context.getResources().getString(R.string.game_desc_info, item.getPraise(),item.getCriticize()));
		//设置多少人关注了贴吧
		viewHolder.desc_info.setText(context.getResources().getString(R.string.game_desc_info, item.getFollowCount()));
		//设置总发帖数和今日发帖数
		viewHolder.desc_info2.setText(context.getResources().getString(R.string.game_desc_info2, item.getTopicCount(),item.getDailyTopicCount()));
		//设置帖子信息
		final PostbarTopicDetail hotTopic = item.getHotTopic();
		if(hotTopic != null&&hotTopic.getTitle()!=null && !hotTopic.getTitle().isEmpty()){
			tmpViewHolder.topic_ll.setVisibility(View.VISIBLE);
			viewHolder.topic_title.setVisibility(View.VISIBLE);
			viewHolder.topic_title_fgx.setVisibility(View.VISIBLE);
			viewHolder.topic_title.setText(hotTopic.getTitle());
			tmpViewHolder.topic_ll.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = null;
					Bundle bundle = null;
					intent = new Intent(context, TopicDetailActivity.class);
					bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, hotTopic.getId());
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, hotTopic.getGameid());
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					context.startActivity(intent);
				}
			});

		}
		else
		{
			tmpViewHolder.topic_ll.setVisibility(View.GONE);
			viewHolder.topic_title.setVisibility(View.GONE);
			viewHolder.topic_title_fgx.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	

	private class ViewHolder {

		RoundedImageView icon;
		ImageView topview ;
		TextView gamename ;
		TextView desc_info;
		TextView desc_info2;
		Button btn_follow ;
		TextView topic_title ;
		View topic_title_fgx ;
		LinearLayout topic_ll ;
	}



	/**
	 * 绑定手机提示框
	 * @param actionName
	 */
	private void createBundPhoneDialog(){
		Intent intent = new Intent(context, BundPhoneActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 添加或取消关注
	 * 
	 * @param v
	 * @param gid
	 * @param op
	 */
	private void followGame(final Button btn_follow ,final GameTopItemObj item,final int op) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(GameTopAdapter.this.context);
		dialog.show();
		ProxyCallBack<Integer> callBack = new ProxyCallBack<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					item.setFollow(true);
					//btn_follow.setEnabled(false);
					// 获得关注的贴吧
					if(followGameListener !=null)
						followGameListener.followGameAction();    
					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(loginUserVo.getUserid()));
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, loginUserVo.getUsername());
					ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(item.getGid()));
					if (item.getGameVo() != null)
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, item.getGameVo().getGamename());
					MobclickAgent.onEvent(GameTopAdapter.this.context, UMConfig.MSGS_EVENT_GAME_FOLLOW, ummap);
					break;
				default:
					btn_follow.setEnabled(true);
					break;
				}
				dialog.dismiss();
				//GameTopAdapter.this.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				dialog.dismiss();
				btn_follow.setEnabled(true);
				if(result==Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE){
					int mfcount = SystemContext.getInstance().getFGM();
					ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_FOLLOWGAME_OVER_COUNT, null,mfcount);
					GameUtil.redressGameRel(mfcount);
				}else{
					ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
				}
			}
		};
		ProxyFactory.getInstance().getUserProxy().userAction(callBack, SystemContext.getInstance().getContext(), item.getGid(), MsgsConstants.OT_GAME, op, null, null,null);
	}

	/**
	 * 设置关注贴吧listener
	 * @param listener
	 */
	public void setFollowGameListener(FollowGameListener listener){
		this.followGameListener = listener;
	}

	/**
	 * 设置排行榜标签
	 * 
	 * @param top
	 */
	private void setTop(int top, ImageView topView) {
		switch (top) {
		case 1:
			topView.setBackgroundResource(R.drawable.game_top_1);
			break;
		case 2:
			topView.setBackgroundResource(R.drawable.game_top_2);
			break;
		case 3:
			topView.setBackgroundResource(R.drawable.game_top_3);
			break;
		case 4:
			topView.setBackgroundResource(R.drawable.game_top_4);
			break;
		case 5:
			topView.setBackgroundResource(R.drawable.game_top_5);
			break;
		case 6:
			topView.setBackgroundResource(R.drawable.game_top_6);
			break;
		case 7:
			topView.setBackgroundResource(R.drawable.game_top_7);
			break;
		case 8:
			topView.setBackgroundResource(R.drawable.game_top_8);
			break;
		case 9:
			topView.setBackgroundResource(R.drawable.game_top_9);
			break;
		case 10:
			topView.setBackgroundResource(R.drawable.game_top_10);
			break;
		case 11:
			topView.setBackgroundResource(R.drawable.game_top_11);
			break;
		case 12:
			topView.setBackgroundResource(R.drawable.game_top_12);
			break;
		case 13:
			topView.setBackgroundResource(R.drawable.game_top_13);
			break;
		case 14:
			topView.setBackgroundResource(R.drawable.game_top_14);
			break;
		case 15:
			topView.setBackgroundResource(R.drawable.game_top_15);
			break;
		case 16:
			topView.setBackgroundResource(R.drawable.game_top_16);
			break;
		case 17:
			topView.setBackgroundResource(R.drawable.game_top_17);
			break;
		case 18:
			topView.setBackgroundResource(R.drawable.game_top_18);
			break;
		case 19:
			topView.setBackgroundResource(R.drawable.game_top_19);
			break;
		case 20:
			topView.setBackgroundResource(R.drawable.game_top_20);
			break;
		default:
			break;
		}
	}

	public interface FollowGameListener{
		public void followGameAction();
	}

}
