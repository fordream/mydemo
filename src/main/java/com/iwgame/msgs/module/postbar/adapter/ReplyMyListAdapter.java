/**      
 * ReplyMyListAdapter.java Create on 2013-12-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Activity;
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
import android.widget.TextView;

import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostElementType;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyDetail;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.MyTextView;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.youban.msgs.R;

/**
 * @ClassName: ReplyMyListAdapter
 * @Description: TODO(回复我的适配器)
 * @author chuanglong
 * @date 2013-12-27 下午2:11:26
 * @Version 1.0
 * 
 */
public class ReplyMyListAdapter extends BaseAdapter {

	protected static final String TAG = "PostListAdapter";
	private Context context;
	private List<Msgs.PostbarTopicReplyDetail> data;
	private LayoutInflater inflater;

	private  Map<Long, String> game_names = new ConcurrentHashMap<Long, String>();
	private  Map<Long, String> postbar_topic_titles = new ConcurrentHashMap<Long, String>();
	private  Map<Long, String> postbar_reply_contents = new ConcurrentHashMap<Long, String>();

	private UserVo loginUserVo = null;

	public ReplyMyListAdapter(Context context, List<Msgs.PostbarTopicReplyDetail> data) {
		this.context = context;
		this.data = data;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		game_names.clear();
		postbar_topic_titles.clear();
		postbar_reply_contents.clear();
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
			convertView = this.inflater.inflate(R.layout.postbar_replymy_list_item, null);
			tmpViewHolder.postbar_replymy_posteravatar = (ImageView) convertView.findViewById(R.id.icon);
			tmpViewHolder.postbar_replymy_posternickname = (TextView) convertView.findViewById(R.id.postbar_replymy_posternickname);
			tmpViewHolder.postbar_replymy_from_info = (TextView) convertView.findViewById(R.id.postbar_replymy_from_info);
			tmpViewHolder.postbar_replymy_date = (TextView) convertView.findViewById(R.id.postbar_replymy_date);
			tmpViewHolder.postbar_replymy_mycontent = (TextView) convertView.findViewById(R.id.postbar_replymy_mycontent);
			tmpViewHolder.postbar_replymy_replycontent = (TextView) convertView.findViewById(R.id.postbar_replymy_replycontent);
			tmpViewHolder.postbar_replymy_image = (ImageView) convertView.findViewById(R.id.postbar_replymy_image);
			tmpViewHolder.postbar_replymy_list_item_postcontent = (LinearLayout) convertView.findViewById(R.id.postbar_replymy_list_item_postcontent);//回复的布局
			convertView.setTag(tmpViewHolder);
		} else {
			tmpViewHolder = (ViewHolder) convertView.getTag();
		}
		final ViewHolder viewHolder = tmpViewHolder;

		final PostbarTopicReplyDetail detail = data.get(position);
		new ImageLoader().loadRes(ResUtil.getSmallRelUrl(detail.getPosterAvatar()), ImageLoader.RADIUS_DEFAULT_PX6, viewHolder.postbar_replymy_posteravatar, R.drawable.common_user_icon_default);
		viewHolder.postbar_replymy_posternickname.setText(detail.getPosterNickname());
		viewHolder.postbar_replymy_date.setText(SafeUtils.getDate2MyStr2(detail.getCreateTime()));

		//更改如下
		if (detail.hasPostContent() && detail.getPostContent().getElementsCount() > 0) {
			// 有内容数据
			viewHolder.postbar_replymy_list_item_postcontent.setVisibility(View.VISIBLE);
			setPostContent(viewHolder.postbar_replymy_list_item_postcontent, detail.getRid(), MsgsConstants.OT_TOPICREPLY, detail.getPostContent().getElementsList(),false);
		} else {
			// 没有内容数据
			viewHolder.postbar_replymy_list_item_postcontent.setVisibility(View.GONE);
		}

		if (detail.getPosterUid() != loginUserVo.getUserid()) {
			// 人头像单击
			viewHolder.postbar_replymy_posteravatar.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 打开对方资料
					Intent intent = new Intent(context, UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, detail.getPosterUid());
					intent.putExtras(bundle);
					context.startActivity(intent);

				}
			});
		}

		if(game_names.containsKey(detail.getGameid()))
		{
			viewHolder.postbar_replymy_from_info.setText(context.getString(R.string.postbar_replymy_from_info, game_names.get(detail.getGameid())));
		}
		else
		{
			viewHolder.postbar_replymy_from_info.setText("");
			// 获得贴吧信息
			ProxyCallBack<GameVo> callback = new ProxyCallBack<GameVo>() {

				@Override
				public void onSuccess(GameVo result) {
					if (result != null) {
						viewHolder.postbar_replymy_from_info.setText(context.getString(R.string.postbar_replymy_from_info, result.getGamename()));
						game_names.put(detail.getGameid(), result.getGamename());
					}
					else
					{
						viewHolder.postbar_replymy_from_info.setText("");
					}
				}

				@Override
				public void onFailure(Integer result,String resultMsg) {
					LogUtil.e(TAG, "获得贴吧信息异常：" + result);
					viewHolder.postbar_replymy_from_info.setText("");
				}

			};
			ProxyFactory.getInstance().getGameProxy().getGameInfo(callback, null, detail.getGameid());
		}

		if (detail.getQuoteRid() != 0) {
			// 对我的回复
			if(postbar_reply_contents.containsKey(detail.getQuoteRid()))
			{

				viewHolder.postbar_replymy_mycontent.setText(SmileyUtil.ReplaceSmiley(context,
						context.getResources().getString(R.string.postbar_replymy_mycontent_reply, postbar_reply_contents.get(detail.getQuoteRid())),
						context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width),
						context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth)));

			}
			else
			{
				// 获得回复的详情
				ProxyCallBack<PostbarTopicReplyDetail> callback2 = new ProxyCallBack<PostbarTopicReplyDetail>() {
					@Override
					public void onSuccess(final PostbarTopicReplyDetail result) {
						if (result != null) {
							String tmpContent = "" ;
							if(result.hasPostContent() && result.getPostContent().getElementsCount() > 0){
								List<Msgs.PostElement> elementList = result.getPostContent().getElementsList();
								Msgs.PostElement element = elementList.get(0);
								if(element != null){
									if (element.getType() == PostElementType.PE_TEXT) {
										tmpContent = element.getText();
									}else if(element.getType() == PostElementType.PE_IMAGE_ID_REF){
										tmpContent ="[图片]";
									}
								}
							}
							viewHolder.postbar_replymy_mycontent.setText(SmileyUtil.ReplaceSmiley(context,
									context.getResources().getString(R.string.postbar_replymy_mycontent_reply, tmpContent),
									context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width),
									context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth)));
							postbar_reply_contents.put((long)(detail.getQuoteRid()), tmpContent);
						} else {
							viewHolder.postbar_replymy_mycontent.setText("");
						}


					}

					@Override
					public void onFailure(Integer result,String resultMsg) {
						LogUtil.e(TAG, "获得我的回复异常：" + result);
						viewHolder.postbar_replymy_mycontent.setText("");
					}

				};
				ProxyFactory.getInstance().getPostbarProxy().getTopicReplyDetail(callback2, context, detail.getQuoteRid());
			}
		} else {
			// 对我的帖子
			if(postbar_topic_titles.containsKey(detail.getTid()))
			{
				viewHolder.postbar_replymy_mycontent.setText(context.getString(R.string.postbar_replymy_mycontent_topic, postbar_topic_titles.get(detail.getTid())));
			}
			else
			{
				// 获得帖子详情
				ProxyCallBack<PostbarTopicDetail> callback2 = new ProxyCallBack<PostbarTopicDetail>() {

					@Override
					public void onSuccess(final PostbarTopicDetail result) {
						((Activity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {
								if (result != null) {
									viewHolder.postbar_replymy_mycontent.setText(context.getString(R.string.postbar_replymy_mycontent_topic, result.getTitle()));
									postbar_topic_titles.put(detail.getTid(), result.getTitle());
								} else {
									viewHolder.postbar_replymy_mycontent.setText("");
								}
							}

						});

					}

					@Override
					public void onFailure(Integer result,String resultMsg) {
						LogUtil.e(TAG, "获得我的帖子异常：" + result);
						viewHolder.postbar_replymy_mycontent.setText("");
					}

				};
				ProxyFactory.getInstance().getPostbarProxy().getTopicDetail(callback2, context, detail.getTid());
			}
		}
		return convertView;
	}


	/**
	 * 设置内容
	 * 
	 * @param pview
	 * @param data
	 * @param isQuote 是否是引用，引用的字的样式不一样，比较小，
	 */
	private void setPostContent(LinearLayout pview, final long tId, final int tType, List<Msgs.PostElement> data,boolean isQuote) {
		pview.removeAllViews();
		for (int i = 0; i < data.size(); i++) {
			final Msgs.PostElement element = data.get(i);
			// 判断类型
			if (element.getType() == PostElementType.PE_TEXT) {
				// 文本
				View view = this.inflater.inflate(R.layout.postbar_replymy_list_item_textview, pview,false);
				pview.addView(view);
				MyTextView postbar_topicreply_list_postcontent_text = (MyTextView) view.findViewById(R.id.postbar_replymy_list_postcontent_text);
				postbar_topicreply_list_postcontent_text.setTextColor(context.getResources().getColor(R.color.reply_my_reply_text_color));
				postbar_topicreply_list_postcontent_text.setTextSize(TypedValue.COMPLEX_UNIT_PX , context.getResources().getDimensionPixelSize(R.dimen.global_font_size8));
				postbar_topicreply_list_postcontent_text.setText(SmileyUtil.ReplaceSmiley(context, element.getText(), context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width),
						context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth)));

				//postbar_topicreply_list_postcontent_text.SetLinkClickIntercept(mURLSpanClickable);


			} else if (element.getType() == PostElementType.PE_IMAGE_ID_REF) {
				// 图片
				View view = this.inflater.inflate(R.layout.postbar_replymy_list_item_img, pview,false);
				pview.addView(view);
				ImageView postbar_topicreply_list_postcontent_resource = (ImageView) view.findViewById(R.id.postbar_replymy_list_postcontent_resource);

				com.iwgame.msgs.common.imageloader.ImageLoader.getInstance().loadRes(ResUtil.getOriginalRelUrl(element.getResourceId()), postbar_topicreply_list_postcontent_resource, R.drawable.postbar_thumbimg_default,
						R.drawable.postbar_thumbimg_default, R.drawable.postbar_thumbimg_default, null, true);

				// 资源点击放大查看
				postbar_topicreply_list_postcontent_resource.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						// TODO Auto-generated method stub
						String[] images = new String[1];
						images[0] = ResUtil.getOriginalRelUrl(element.getResourceId());
						Intent intent = new Intent(context, ImageBrowerActivity.class);
						Bundle bundle = new Bundle();
						bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, images);
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, tId);
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, tType);
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						context.startActivity(intent);

					}
				});
			}

		}
	}

	class ViewHolder {
		ImageView postbar_replymy_posteravatar;
		TextView postbar_replymy_posternickname;
		TextView postbar_replymy_from_info;
		TextView postbar_replymy_date;
		TextView postbar_replymy_mycontent;
		TextView postbar_replymy_replycontent;
		ImageView postbar_replymy_image;
		LinearLayout postbar_replymy_list_item_postcontent;//回复的列表
	}

}
