/**      
 * TopicPraiseListAdapter.java Create on 2015-3-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.MyTextView;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.youban.msgs.R;

/**
 * @ClassName: TopicPraiseListAdapter
 * @Description: 帖子赞的通知
 * @author 王卫
 * @date 2015-3-15 下午4:09:00
 * @Version 1.0
 * 
 */
public class TopicPraiseListAdapter extends BaseAdapter {

	protected static final String TAG = "PostListAdapter";
	private Context context;
	private List<MessageVo> data;
	private LayoutInflater inflater;

	private UserVo loginUserVo = null;

	public TopicPraiseListAdapter(Context context, List<MessageVo> data) {
		this.context = context;
		this.data = data;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		if (position < data.size())
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
		ViewHolder tmpViewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.topic_praise_list_item, null);
			tmpViewHolder.viewContent = (RelativeLayout) convertView.findViewById(R.id.viewContent);
			tmpViewHolder.topic_praise_posteravatar = (ImageView) convertView.findViewById(R.id.icon);
			tmpViewHolder.topic_praise_posternickname = (TextView) convertView.findViewById(R.id.topic_praise_posternickname);
			tmpViewHolder.topic_praise_from_info = (TextView) convertView.findViewById(R.id.topic_praise_from_info);
			tmpViewHolder.topic_praise_date = (TextView) convertView.findViewById(R.id.topic_praise_date);
			tmpViewHolder.topic_praise_mycontent = (TextView) convertView.findViewById(R.id.topic_praise_mycontent);
			tmpViewHolder.topic_praise_tip = (TextView) convertView.findViewById(R.id.topic_praise_tip);
			tmpViewHolder.topic_praise_list_item_postcontent = (LinearLayout) convertView.findViewById(R.id.topic_praise_list_item_postcontent);// 回复的布局
			convertView.setTag(tmpViewHolder);
		} else {
			tmpViewHolder = (ViewHolder) convertView.getTag();
		}
		final ViewHolder viewHolder = tmpViewHolder;
		MessageVo messageVo = data.get(position);
		LogUtil.d(TAG, messageVo.toString());
		setViewData(viewHolder, messageVo);
		return convertView;
	}

	/**
	 * 
	 * @param mvo
	 */
	private void setViewData(ViewHolder viewHolder, MessageVo mvo) {
		// 设置日期
		viewHolder.topic_praise_date.setText(SafeUtils.getDate2MyStr2(mvo.getCreateTime()));
		String ext = mvo.getExt();
		TopicPraise topicPraise = null;
		if (ext != null) {
			try {
				topicPraise = new TopicPraise();
				JSONObject jo = new JSONObject(ext);
				topicPraise.estimateUserName = jo.getString("estimateUserName");
				topicPraise.estimateUserAvatar = jo.getString("estimateUserAvatar");
				topicPraise.id = jo.getLong("id");
				topicPraise.gid = jo.getLong("gid");
				topicPraise.gamename = jo.getString("gamename");
				int type = jo.getInt("type");
				topicPraise.type = type;
				if (type == 0) {// 主贴
					topicPraise.title = jo.getString("title");
				} else {// 回复
					topicPraise.levelnew = jo.getInt("levelnew");
					List<TopicReplyContent> rcontent = new ArrayList<TopicReplyContent>();
					topicPraise.rcontent = rcontent;
					JSONArray contentJson = jo.getJSONArray("contentJson");
					if (contentJson != null) {
						for (int i = 0; i < contentJson.length(); i++) {
							TopicReplyContent rc = new TopicReplyContent();
							JSONObject ob = new JSONObject(contentJson.get(i).toString());
							rc.cont = ob.getString("cont");
							rc.type = ob.getInt("type");
							rcontent.add(rc);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (topicPraise != null) {
			final TopicPraise tmpTopicPraise = topicPraise;
			// 设置头像
			new ImageLoader().loadRes(ResUtil.getSmallRelUrl(topicPraise.estimateUserAvatar), ImageLoader.RADIUS_DEFAULT_PX6,
					viewHolder.topic_praise_posteravatar, R.drawable.common_user_icon_default);
			// 设置昵称
			viewHolder.topic_praise_posternickname.setText(topicPraise.estimateUserName);
			// 设置来自贴吧
			viewHolder.topic_praise_from_info.setText("来自" + topicPraise.gamename + "吧");
			// 设置赞的贴吧内容
			if (topicPraise.type == 0) {// 主贴
				viewHolder.topic_praise_tip.setText("赞了帖子");
				viewHolder.topic_praise_mycontent.setText(topicPraise.title);
				viewHolder.topic_praise_mycontent.setVisibility(View.VISIBLE);
				viewHolder.topic_praise_list_item_postcontent.setVisibility(View.GONE);
			} else {// 评论&回复
				viewHolder.topic_praise_tip.setText("赞了楼层");
				viewHolder.topic_praise_mycontent.setVisibility(View.GONE);
				viewHolder.topic_praise_list_item_postcontent.setVisibility(View.VISIBLE);
				setPostContent(viewHolder.topic_praise_list_item_postcontent, topicPraise.rcontent);
			}
		}
	}

	/**
	 * 设置内容
	 * 
	 * @param pview
	 * @param data
	 * @param isQuote
	 *            是否是引用，引用的字的样式不一样，比较小，
	 */
	private void setPostContent(LinearLayout pview, List<TopicReplyContent> rcontent) {
		pview.removeAllViews();
		for (int i = 0; i < rcontent.size(); i++) {
			final TopicReplyContent element = rcontent.get(i);
			// 判断类型
			if (element.type == 0) {
				// 文本
				View view = this.inflater.inflate(R.layout.postbar_replymy_list_item_textview, pview, false);
				pview.addView(view);
				MyTextView postbar_topicreply_list_postcontent_text = (MyTextView) view.findViewById(R.id.postbar_replymy_list_postcontent_text);
				postbar_topicreply_list_postcontent_text.setTextColor(context.getResources().getColor(R.color.reply_my_reply_text_color));
				postbar_topicreply_list_postcontent_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
						context.getResources().getDimensionPixelSize(R.dimen.global_font_size8));
				postbar_topicreply_list_postcontent_text.setText(SmileyUtil.ReplaceSmiley(context, element.cont, context.getResources()
						.getDimensionPixelSize(R.dimen.global_string_smiley_width),
						context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth)));
			} else if (element.type == 1) {
				// 图片
				View view = this.inflater.inflate(R.layout.postbar_replymy_list_item_img, pview, false);
				pview.addView(view);
				ImageView postbar_topicreply_list_postcontent_resource = (ImageView) view
						.findViewById(R.id.postbar_replymy_list_postcontent_resource);

				com.iwgame.msgs.common.imageloader.ImageLoader.getInstance().loadRes(ResUtil.getOriginalRelUrl(element.cont),
						postbar_topicreply_list_postcontent_resource, R.drawable.postbar_thumbimg_default, R.drawable.postbar_thumbimg_default,
						R.drawable.postbar_thumbimg_default, null, true);

				// 资源点击放大查看
				postbar_topicreply_list_postcontent_resource.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						String[] images = new String[1];
						images[0] = ResUtil.getOriginalRelUrl(element.cont);
						Intent intent = new Intent(context, ImageBrowerActivity.class);
						Bundle bundle = new Bundle();
						bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, images);
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						context.startActivity(intent);
					}
				});
			}

		}
	}

	class ViewHolder {
		RelativeLayout viewContent;
		ImageView topic_praise_posteravatar;
		TextView topic_praise_posternickname;
		TextView topic_praise_from_info;
		TextView topic_praise_date;
		TextView topic_praise_mycontent;
		TextView topic_praise_tip;
		LinearLayout topic_praise_list_item_postcontent;// 回复的列表
	}

	/**
	 * 赞贴内容
	 */
	class TopicPraise {
		// 赞的用户名称
		String estimateUserName;
		// 赞的用户头像
		String estimateUserAvatar;
		// 主帖id或者回复id
		Long id;
		// 标题（类型未主贴才有）
		String title;
		// 游戏id
		Long gid;
		// 游戏名称
		String gamename;
		// 0:主帖 1:回复
		int type;
		// 楼层
		int levelnew;
		List<TopicReplyContent> rcontent;
	}

	/**
	 * 回帖内容
	 */
	class TopicReplyContent {
		// 文本内容或资源id
		String cont;
		// 内容类型，文本内容或资源id[0:txt 1:image]
		int type;
	}

}
