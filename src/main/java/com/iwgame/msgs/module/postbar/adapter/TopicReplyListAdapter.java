/**      
 * TopicReplyListAdapter.java Create on 2013-12-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailFragment;
import com.iwgame.msgs.module.setting.ui.GoodsDetailActivity;
import com.iwgame.msgs.module.user.ui.SupportUserListActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostElementType;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyDetail;
import com.iwgame.msgs.proto.Msgs.QuotesReplyDetail;
import com.iwgame.msgs.proto.Msgs.TopicEstimateUsers;
import com.iwgame.msgs.proto.Msgs.TopicSetDetail;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.AppUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.MyTextView;
import com.iwgame.msgs.widget.URLSpanClickable;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: TopicReplyListAdapter
 * @Description: 帖子回复适配器
 * @author chuanglong
 * @date 2013-12-23 下午12:35:57
 * @Version 1.0
 * 
 */
public class TopicReplyListAdapter extends BaseAdapter {

	protected static final String TAG = "PostListAdapter";
	private Activity act;
	private Context context;
	private List<Object> data;
	private LayoutInflater inflater;
	private UserVo loginUserVo = null;

	private URLSpanClickable mURLSpanClickable;
	private ReplyButtonOnClickListener replyButtonOnClickListener;
	private TopicDetailActionListener topicDetailActionListener;

	private boolean detailIsDel = false;
	private boolean isVisControl = false;
	private long posterUid;

	final int TYPE_DEL = 1;
	final int TYPE_CTL = 2;

	// 布局类型有2中 ,TYPE_TOPIC为topic，TYPE_REPLY回复
	final int VIEW_TYPE = 2;
	final int TYPE_TOPIC = 0;
	final int TYPE_REPLY = 1;
	/**
	 * 回复的总数
	 */
	private int mTotalCount = 0;
	/** 是否关注楼主 **/
	private int followStaus = -1;
	/** 事件接口对象 **/
	private AdapterEventListener listener;

	private Map<PostbarTopicReplyDetail, Integer> likeMap;

	private Map<PostbarTopicReplyDetail, List<QuotesReplyDetail>> replyMap;

	/** 贴吧是否显示来自什么游戏模式 **/
	private int pmode = MODE_POSTBAR_UNSHOW;
	/** 默认普通不显示模式 **/
	public static int MODE_POSTBAR_UNSHOW = 0;
	/** 显示模式 **/
	public static int MODE_POSTBAR_SHOW = 1;
	/** 游戏名称 **/
	private String gamename;

	/** 事件接口 **/
	public interface AdapterEventListener {

		/** 赞 **/
		public void onLikeHander();

		/** 踩 **/
		public void onUnLikeHander();

		/** 评论 **/
		public void onCommentHander();

	}

	public TopicReplyListAdapter(Activity act, Context context, List<Object> data, URLSpanClickable clickable, AdapterEventListener listener) {
		this.act = act;
		this.context = context;
		this.data = data;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mURLSpanClickable = clickable;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
		this.listener = listener;
		likeMap = new HashMap<PostbarTopicReplyDetail, Integer>();
		replyMap = new HashMap<Msgs.PostbarTopicReplyDetail, List<QuotesReplyDetail>>();
	}

	/**
	 * 
	 * @param replyDetail
	 */
	public void addReply(QuotesReplyDetail replyDetail, int postion) {
		if (data.get(postion) instanceof PostbarTopicReplyDetail) {
			PostbarTopicReplyDetail detail = (PostbarTopicReplyDetail) data.get(postion);
			List<QuotesReplyDetail> details = replyMap.get(detail);
			details.add(replyDetail);
		}
	}

	/**
	 * 设置回复的监听
	 * 
	 * @param listener
	 */
	public void setReplyButtonOnClickListener(ReplyButtonOnClickListener listener) {
		replyButtonOnClickListener = listener;
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

	@Override
	public int getItemViewType(int position) {
		if (data.get(position) instanceof Msgs.PostbarTopicReplyDetail) {
			return TYPE_REPLY;
		} else if (data.get(position) instanceof Msgs.PostbarTopicDetail) {
			return TYPE_TOPIC;
		} else {
			return TYPE_REPLY;
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
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (position >= data.size())
			return null;
		final int type = getItemViewType(position);
		ViewHolder tmpViewHolder = new ViewHolder();
		TopicViewHolder tmpTopicViewHolder = new TopicViewHolder();
		if (convertView == null) {
			switch (type) {
			case TYPE_TOPIC:// 详情（帖子楼主）
				convertView = this.inflater.inflate(R.layout.postbar_topicreply_list_item_topic, null);
				tmpTopicViewHolder.postbar_head = (RelativeLayout) convertView.findViewById(R.id.postbar_head);
				tmpTopicViewHolder.postbar_head_gamename = (TextView) convertView.findViewById(R.id.postbar_head_gamename);
				tmpTopicViewHolder.postbar_head_jumpbtn = (TextView) convertView.findViewById(R.id.postbar_head_jumpbtn);
				tmpTopicViewHolder.postbar_head_line = (View) convertView.findViewById(R.id.postbar_head_line);

				tmpTopicViewHolder.postbar_topicreply_list_header_topic_posteravatar = (ImageView) convertView.findViewById(R.id.icon);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_posternickname = (TextView) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_posternickname);
				tmpTopicViewHolder.age = (TextView) convertView.findViewById(R.id.age);
				tmpTopicViewHolder.followBtn = (Button) convertView.findViewById(R.id.followBtn);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_createtime = (TextView) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_createtime);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_loc = (TextView) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_loc);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_posterid = (TextView) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_posterid);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_postermaster = (ImageView) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_postermaster);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_title = (TextView) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_title);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_comments = (TextView) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_comments);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_share = (ImageView) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_share);
				tmpTopicViewHolder.postbar_jihe_tab_image = (ImageView) convertView.findViewById(R.id.postbar_jihe_tab_image);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_postcontent = (LinearLayout) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_postcontent);
				tmpTopicViewHolder.postbar_topicreply_list_header_topic_setcontent = (LinearLayout) convertView
						.findViewById(R.id.postbar_topicreply_list_header_topic_setcontent);
				tmpTopicViewHolder.tagTxt = (TextView) convertView.findViewById(R.id.tagTxt);
				tmpTopicViewHolder.likeTxt = (TextView) convertView.findViewById(R.id.likeTxt);
				tmpTopicViewHolder.chatTxt = (TextView) convertView.findViewById(R.id.chatTxt);
				tmpTopicViewHolder.replyTxt = (TextView) convertView.findViewById(R.id.replyTxt);
				tmpTopicViewHolder.shareTxt = (TextView) convertView.findViewById(R.id.shareTxt);
				tmpTopicViewHolder.likeUsersContent = (LinearLayout) convertView.findViewById(R.id.likeUsersContent);
				tmpTopicViewHolder.likeUser1 = (ImageView) convertView.findViewById(R.id.likeUser1);
				tmpTopicViewHolder.likeUser2 = (ImageView) convertView.findViewById(R.id.likeUser2);
				tmpTopicViewHolder.likeUser3 = (ImageView) convertView.findViewById(R.id.likeUser3);
				tmpTopicViewHolder.likeUser4 = (ImageView) convertView.findViewById(R.id.likeUser4);
				tmpTopicViewHolder.likeUser5 = (ImageView) convertView.findViewById(R.id.likeUser5);
				tmpTopicViewHolder.likeUser6 = (ImageView) convertView.findViewById(R.id.likeUser6);
				tmpTopicViewHolder.likeUser7 = (ImageView) convertView.findViewById(R.id.likeUser7);
				tmpTopicViewHolder.likeUser8 = (ImageView) convertView.findViewById(R.id.likeUser8);
				tmpTopicViewHolder.likeUser9 = (ImageView) convertView.findViewById(R.id.likeUser9);
				tmpTopicViewHolder.likeUserCountTxt = (TextView) convertView.findViewById(R.id.likeUserCountTxt);
				convertView.setTag(tmpTopicViewHolder);
				break;
			case TYPE_REPLY:// 回复
				convertView = this.inflater.inflate(R.layout.postbar_topicreply_list_item, null);
				tmpViewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
				tmpViewHolder.postbar_topicreply_list_item_posternickname = (TextView) convertView
						.findViewById(R.id.postbar_topicreply_list_item_posternickname);
				tmpViewHolder.age = (TextView) convertView.findViewById(R.id.age);
				tmpViewHolder.likeTxt = (TextView) convertView.findViewById(R.id.likeTxt);
				tmpViewHolder.postbar_topicreply_list_item_date = (TextView) convertView.findViewById(R.id.postbar_topicreply_list_item_date);
				tmpViewHolder.postbar_topicreply_list_item_level = (TextView) convertView.findViewById(R.id.postbar_topicreply_list_item_level);
				tmpViewHolder.postbar_topicreply_list_item_preply = (LinearLayout) convertView.findViewById(R.id.postbar_topicreply_list_item_preply);
				tmpViewHolder.postbar_topicreply_list_items = (LinearLayout) convertView.findViewById(R.id.postbar_topicreply_list_items);
				tmpViewHolder.postbar_topicreply_preply_action = (LinearLayout) convertView.findViewById(R.id.postbar_topicreply_preply_action);
				tmpViewHolder.allReplyTxt = (TextView) convertView.findViewById(R.id.allReplyTxt);
				tmpViewHolder.postbar_topicreply_list_item_posterid = (TextView) convertView.findViewById(R.id.postbar_topicreply_list_item_posterid);
				tmpViewHolder.postbar_topicreply_list_item_loc = (TextView) convertView.findViewById(R.id.postbar_topicreply_list_item_loc);
				tmpViewHolder.postbar_topicreply_list_item_ll_reply = (TextView) convertView.findViewById(R.id.postbar_topicreply_list_item_ll_reply);
				tmpViewHolder.postbar_topicreply_list_item_postermaster = (ImageView) convertView
						.findViewById(R.id.postbar_topicreply_list_item_postermaster);
				tmpViewHolder.postbar_topicreply_list_item_postcontent = (LinearLayout) convertView
						.findViewById(R.id.postbar_topicreply_list_item_postcontent);
				convertView.setTag(tmpViewHolder);
				break;

			}

		} else {
			switch (type) {
			case TYPE_TOPIC:// 详情（帖子楼主）
				tmpTopicViewHolder = (TopicViewHolder) convertView.getTag();
				break;
			case TYPE_REPLY:// 回复
				tmpViewHolder = (ViewHolder) convertView.getTag();
				ImageViewUtil.showImage(tmpViewHolder.icon, null, R.drawable.common_default_icon);
				break;
			}

		}
		final ViewHolder viewHolder = tmpViewHolder;
		final TopicViewHolder topicViewHolder = tmpTopicViewHolder;
		if (type == TYPE_TOPIC) {// 帖子楼主
			final PostbarTopicDetail topicDetail = (PostbarTopicDetail) data.get(position);
			posterUid = topicDetail.getPosterUid();
			isVisControl = topicDetail.getIsVisControl();
			setTopicDetail(topicViewHolder, topicDetail);
			return convertView;
		}

		final PostbarTopicReplyDetail detail = (PostbarTopicReplyDetail) data.get(position);
		if (!replyMap.containsKey(detail)) {
			List<QuotesReplyDetail> list = new ArrayList<Msgs.QuotesReplyDetail>();
			replyMap.put(detail, list);
			int size = detail.getQuotesReplyDetailList().size();
			for (int i = 0; i < size; i++) {
				list.add(detail.getQuotesReplyDetailList().get(i));
			}
		}

		// ImageLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(detail.getPosterAvatar()),
		// viewHolder.postbar_topicreply_list_item_posteravatar,
		// R.drawable.common_default_icon,
		// R.drawable.common_default_icon, R.drawable.common_default_icon, null,
		// true);

		ImageViewUtil.showImage(viewHolder.icon, detail.getPosterAvatar(), R.drawable.common_default_icon);
		viewHolder.icon.setOnClickListener(null);
		// 设置头像单击
		viewHolder.icon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				int realposition = (int) getItemId(position);
				PostbarTopicReplyDetail tempDetail = (PostbarTopicReplyDetail) data.get(realposition);
				if (tempDetail.getPosterUid() != loginUserVo.getUserid()) {
					// 打开对方资料
					Intent intent = new Intent(context, UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, tempDetail.getPosterUid());
					intent.putExtras(bundle);
					context.startActivity(intent);
				} else {
					ToastUtil.showToast(context, "查看自己的信息去我的页面");
				}
			}

		});

		// 设置年龄和性别
		setSexAddAge(viewHolder.age, detail.getAge(), detail.getSex());
		// 是否赞过
		if (!likeMap.containsKey(detail)) {
			likeMap.put(detail, detail.getHasPraise());
		}
		int like = likeMap.get(detail);
		if (like == 0) {
			setLikeTxtIcon(viewHolder.likeTxt, R.drawable.game_like_nor);
			viewHolder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_unlike_txt));
		} else {
			setLikeTxtIcon(viewHolder.likeTxt, R.drawable.game_like_pre2);
			viewHolder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_liked_txt));
		}
		int likeCount = detail.getPraiseNums();
		if (detail.getHasPraise() > like) {
			likeCount = likeCount - 1;
		} else if (detail.getHasPraise() < like) {
			likeCount = likeCount + 1;
		}
		viewHolder.likeTxt.setText(likeCount + "");
		likeOrUnLike(viewHolder.likeTxt, detail);

		viewHolder.postbar_topicreply_list_item_ll_reply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				if (replyButtonOnClickListener != null) {
					int realposition = (int) getItemId(position);
					PostbarTopicReplyDetail tempDetail = (PostbarTopicReplyDetail) data.get(realposition);

					if (tempDetail != null && tempDetail.getIsDel()) {
						ToastUtil.showToast(context, "该回复已被吧主删除，不能进行回复哦！");
						return;
					}
					if (tempDetail != null && isVisControl) {
						if (detail.getPosterUid() != loginUserVo.getUserid() && loginUserVo.getUserid() != posterUid) {
							ToastUtil.showToast(context, "内容已被隐藏，不能进行回复哦！");
						} else {
							ToastUtil.showToast(context, "不能进行回复哦！");
						}
						return;
					}

					replyButtonOnClickListener.onClick(position);
				}
			}
		});

		if (detail.getPosterIsMaster()) {
			viewHolder.postbar_topicreply_list_item_postermaster.setVisibility(View.VISIBLE);
		} else {
			viewHolder.postbar_topicreply_list_item_postermaster.setVisibility(View.GONE);
		}
		viewHolder.postbar_topicreply_list_item_posternickname.setText(detail.getPosterNickname());
		viewHolder.postbar_topicreply_list_item_date.setText(SafeUtils.getDate2MyStr2(detail.getCreateTime()));
		viewHolder.postbar_topicreply_list_item_level.setText(context.getString(R.string.postbar_info_floor, detail.getLevelnew()));
		String tmploc = DistanceUtil.covertDistance(detail.getPosition());
		if (!tmploc.isEmpty()) {
			tmpViewHolder.postbar_topicreply_list_item_loc.setText(context.getString(R.string.postbar_topic_detail_loc_info, tmploc));
		} else {
			tmpViewHolder.postbar_topicreply_list_item_loc.setText("");
		}
		tmpViewHolder.postbar_topicreply_list_item_posterid.setText(context.getString(R.string.postbar_topic_detail_poster_seviceid,
				detail.getPosterSerial()));

		if (detail.getIsDel()) {
			detailIsDel = true;
			showDelOrControlView(viewHolder, detail, TYPE_DEL);
		} else {
			detailIsDel = false;
			if (isVisControl && detail.getPosterUid() != loginUserVo.getUserid() && loginUserVo.getUserid() != posterUid) {
				showDelOrControlView(viewHolder, detail, TYPE_CTL);
			} else {
				if (detail.hasPostContent() && detail.getPostContent().getElementsCount() > 0) {
					// 有内容数据
					viewHolder.postbar_topicreply_list_item_postcontent.setVisibility(View.VISIBLE);
					setPostContent(viewHolder.postbar_topicreply_list_item_postcontent, detail.getRid(), MsgsConstants.OT_TOPICREPLY, detail
							.getPostContent().getElementsList(), false, 1);
				} else {
					// 没有内容数据
					viewHolder.postbar_topicreply_list_item_postcontent.setVisibility(View.GONE);
				}

				viewHolder.postbar_topicreply_list_items.removeAllViews();
				List<QuotesReplyDetail> tmpReplylist = null;
				if (replyMap.containsKey(detail))
					tmpReplylist = replyMap.get(detail);
				else
					tmpReplylist = detail.getQuotesReplyDetailList();
				final List<QuotesReplyDetail> replylist = tmpReplylist;
				if (replylist != null && replylist.size() > 0) {
					final int size = replylist.size();
					viewHolder.postbar_topicreply_list_item_preply.setVisibility(View.VISIBLE);
					if (size > 3) {
						viewHolder.postbar_topicreply_preply_action.setVisibility(View.VISIBLE);
						viewHolder.allReplyTxt.setText("查看全部回复" + size + "条");
						viewHolder.postbar_topicreply_preply_action.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								for (int i = 3; i < size; i++) {
									viewHolder.postbar_topicreply_list_items.addView(getPReplyItemView(replylist.get(i), position));
								}
								viewHolder.postbar_topicreply_preply_action.setVisibility(View.GONE);
								viewHolder.allReplyTxt.setText("查看全部回复");
							}
						});
					} else {
						viewHolder.postbar_topicreply_preply_action.setVisibility(View.GONE);
						viewHolder.allReplyTxt.setText("查看全部回复");
					}
					for (int i = 0; i < size; i++) {
						if (i > 2)
							break;
						viewHolder.postbar_topicreply_list_items.addView(getPReplyItemView(replylist.get(i), position));
					}
				} else {
					viewHolder.postbar_topicreply_list_item_preply.setVisibility(View.GONE);
				}
			}
		}

		return convertView;
	}

	/**
	 * 
	 * @param tv
	 * @param detail
	 * @param op
	 */
	private void likeOrUnLike(final TextView likeTxt, final Msgs.PostbarTopicReplyDetail detail) {
		final int likeCount = Integer.valueOf(likeTxt.getText().toString());
		likeTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int op = MsgsConstants.OP_PRAISE;
				int like = likeMap.get(detail);
				if (like == 1) {
					op = MsgsConstants.OP_PRAISE_CANCEL;
				}
				final int relOp = op;
				likeTxt.setClickable(false);
				ProxyFactory.getInstance().getPostbarProxy().likeOrUnLikeTopic(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						if (relOp == MsgsConstants.OP_PRAISE) {
							likeMap.put(detail, 1);
							setLikeTxtIcon(likeTxt, R.drawable.game_like_pre2);
							likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_liked_txt));
						} else if (relOp == MsgsConstants.OP_PRAISE_CANCEL) {
							likeMap.put(detail, 0);
							setLikeTxtIcon(likeTxt, R.drawable.game_like_nor);
							likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_unlike_txt));
						}
						int like = likeMap.get(detail);
						int likeCount = detail.getPraiseNums();
						if (detail.getHasPraise() > like) {
							likeCount = likeCount - 1;
						} else if (detail.getHasPraise() < like) {
							likeCount = likeCount + 1;
						}
						likeTxt.setText(likeCount + "");
						likeTxt.setClickable(true);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						switch (result) {
						case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_ALL_GUEST_DENY_VALUE:
							Intent intent = new Intent(context, BundPhoneActivity.class);
							context.startActivity(intent);
							break;
						default:
							ErrorCodeUtil.handleErrorCode(context, result, null);
							break;
						}
						likeTxt.setClickable(true);
					}
				}, context, detail.getRid(), detail.getTid(), detail.getTopicUid(), detail.getPosterUid(), 1, relOp);
			}
		});
	}

	private void showDelOrControlView(ViewHolder viewHolder, PostbarTopicReplyDetail detail, int type) {
		viewHolder.postbar_topicreply_list_item_preply.setVisibility(View.GONE);
		viewHolder.postbar_topicreply_list_item_postcontent.setVisibility(View.VISIBLE);
		// 构造一个内容(删除的信息）
		List<Msgs.PostElement> elements = new ArrayList<Msgs.PostElement>();
		Msgs.PostElement.Builder builder = Msgs.PostElement.newBuilder();
		builder.setType(PostElementType.PE_TEXT);
		if (type == TYPE_DEL) {
			builder.setText(context.getResources().getString(R.string.postbar_reply_isdel_content));
		} else {
			builder.setText(context.getResources().getString(R.string.postbar_reply_isctl_content));
		}

		elements.add(builder.build());
		setPostContent(viewHolder.postbar_topicreply_list_item_postcontent, detail.getRid(), MsgsConstants.OT_TOPICREPLY, elements, false, 2);
	}

	/**
	 * 获得父评论的view item
	 * 
	 * @param detail
	 * @param isShowDivider
	 *            ,true 显示，false 不显示
	 * @return
	 */
	private View getPReplyItemView(final QuotesReplyDetail detail, final int postion) {
		View view = this.inflater.inflate(R.layout.postbar_topicreply_preply_item, null);
		TextView postbar_topicreply_preply_item_posternickname = (TextView) view.findViewById(R.id.postbar_topicreply_preply_item_posternickname);
		MyTextView postbar_topicreply_preply_item_content = (MyTextView) view.findViewById(R.id.postbar_topicreply_preply_item_content);
		postbar_topicreply_preply_item_posternickname.setText(detail.getFromName());
		postbar_topicreply_preply_item_content.setText(SmileyUtil.ReplaceSmiley(context, "回复 " + detail.getToName() + ": "
				+ detail.getPostContent().getElementsList().get(0).getText(),
				context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width_level2), context.getResources()
						.getDimensionPixelSize(R.dimen.global_string_smiley_heigth_level2)));
		postbar_topicreply_preply_item_posternickname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (detail.getFromUid() != loginUserVo.getUserid()) {
					// 打开对方资料
					Intent intent = new Intent(context, UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, detail.getFromUid());
					intent.putExtras(bundle);
					context.startActivity(intent);
				} else {
					ToastUtil.showToast(context, "查看自己的信息去我的页面");
				}
			}
		});
		postbar_topicreply_preply_item_content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				replyButtonOnClickListener.onClickReply(detail, postion);
			}
		});
		return view;
	}

	class ViewHolder {
		ImageView icon;
		TextView postbar_topicreply_list_item_posternickname;
		TextView age;
		TextView likeTxt;
		TextView postbar_topicreply_list_item_date;
		TextView postbar_topicreply_list_item_level;
		LinearLayout postbar_topicreply_list_item_preply;
		LinearLayout postbar_topicreply_list_items;
		LinearLayout postbar_topicreply_preply_action;
		TextView allReplyTxt;
		TextView postbar_topicreply_list_item_posterid;
		TextView postbar_topicreply_list_item_loc;
		TextView postbar_topicreply_list_item_ll_reply;
		ImageView postbar_topicreply_list_item_postermaster;
		LinearLayout postbar_topicreply_list_item_postcontent;

	}

	// 点击回复的回调
	public interface ReplyButtonOnClickListener {

		public void onClick(int position);

		public void onClickReply(QuotesReplyDetail detail, int position);
	}

	class TopicViewHolder {
		RelativeLayout postbar_head;
		TextView postbar_head_gamename;
		TextView postbar_head_jumpbtn;
		View postbar_head_line;

		ImageView postbar_topicreply_list_header_topic_posteravatar;
		TextView postbar_topicreply_list_header_topic_posternickname;
		TextView age;
		Button followBtn;
		TextView postbar_topicreply_list_header_topic_createtime;
		TextView postbar_topicreply_list_header_topic_loc;
		TextView postbar_topicreply_list_header_topic_posterid;
		ImageView postbar_topicreply_list_header_topic_postermaster;

		TextView postbar_topicreply_list_header_topic_title;
		TextView postbar_topicreply_list_header_topic_comments;
		ImageView postbar_topicreply_list_header_topic_share;

		ImageView postbar_jihe_tab_image;

		LinearLayout postbar_topicreply_list_header_topic_postcontent;
		LinearLayout postbar_topicreply_list_header_topic_setcontent;

		TextView tagTxt;
		TextView likeTxt;
		TextView chatTxt;
		TextView replyTxt;
		TextView shareTxt;
		LinearLayout likeUsersContent;
		ImageView likeUser1;
		ImageView likeUser2;
		ImageView likeUser3;
		ImageView likeUser4;
		ImageView likeUser5;
		ImageView likeUser6;
		ImageView likeUser7;
		ImageView likeUser8;
		ImageView likeUser9;
		TextView likeUserCountTxt;
	}

	public void setTopicDetail(TopicViewHolder topicViewHolder, final PostbarTopicDetail detail) {
		if (pmode == MODE_POSTBAR_SHOW) {
			topicViewHolder.postbar_head.setVisibility(View.VISIBLE);
			topicViewHolder.postbar_head_line.setVisibility(View.VISIBLE);
			if (gamename != null)
				topicViewHolder.postbar_head_gamename.setText("来自" + gamename + "贴吧");
			topicViewHolder.postbar_head_jumpbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (act != null) {
						Intent intent = new Intent(context, GameTopicListActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, detail.getGameid());
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						context.startActivity(intent);
						act.finish();
					}
				}
			});
		} else {
			topicViewHolder.postbar_head.setVisibility(View.GONE);
			topicViewHolder.postbar_head_line.setVisibility(View.GONE);
		}

		topicViewHolder.postbar_topicreply_list_header_topic_title.setText(detail.getTitle());
		topicViewHolder.postbar_topicreply_list_header_topic_comments.setText(mTotalCount + "");
		// 分享
		topicViewHolder.shareTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isVisControl) {
					ToastUtil.showToast(context, "仅楼主层主可见帖子不能被分享哦！");
					return;
				}

				if (topicDetailActionListener != null)
					topicDetailActionListener.onClickShare();
			}
		});
		ImageViewUtil.showImage(topicViewHolder.postbar_topicreply_list_header_topic_posteravatar, detail.getPosterAvatar(),
				R.drawable.common_default_icon);

		// 头像单击
		topicViewHolder.postbar_topicreply_list_header_topic_posteravatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				if (detail.getPosterUid() != loginUserVo.getUserid()) {
					// 打开对方资料
					Intent intent = new Intent(context, UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, detail.getPosterUid());
					intent.putExtras(bundle);
					context.startActivity(intent);
				} else {
					ToastUtil.showToast(context, "查看自己的信息去我的页面");
				}
			}

		});

		// 获取关注信息
		UserVo userVo1 = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext()).getUserByUserId(detail.getPosterUid());
		if (userVo1 != null) {
			followStaus = userVo1.getRelPositive();
		} else {
			if (followStaus == -1)
				followStaus = detail.getHasFollowPoster();
		}
		if (followStaus == 0 && detail.getPosterUid() != loginUserVo.getUserid()) {
			topicViewHolder.followBtn.setBackgroundResource(R.drawable.common_follow_nor);
			topicViewHolder.followBtn.setVisibility(View.VISIBLE);
		} else {
			topicViewHolder.followBtn.setBackgroundResource(R.drawable.common_follow_pre);
			topicViewHolder.followBtn.setVisibility(View.GONE);
		}
		// 设置年龄和性别
		setSexAddAge(topicViewHolder.age, detail.getPosterAge(), detail.getPosterSex());
		// 关注
		followUser(topicViewHolder.followBtn, detail.getPosterUid());
		// 吧主
		if (detail.getPosterIsMaster()) {
			topicViewHolder.postbar_topicreply_list_header_topic_postermaster.setVisibility(View.VISIBLE);
		} else {
			topicViewHolder.postbar_topicreply_list_header_topic_postermaster.setVisibility(View.GONE);
		}

		if (detail.getIsTopicSet()) {
			topicViewHolder.postbar_jihe_tab_image.setVisibility(View.VISIBLE);
		} else {
			topicViewHolder.postbar_jihe_tab_image.setVisibility(View.GONE);
		}
		topicViewHolder.postbar_topicreply_list_header_topic_posternickname.setText(detail.getPosterNickname());
		topicViewHolder.postbar_topicreply_list_header_topic_createtime.setText(SafeUtils.getDate2MyStr2(detail.getCreateTime()));
		String tmploc = DistanceUtil.covertDistance(detail.getPosition());
		if (!tmploc.isEmpty()) {
			topicViewHolder.postbar_topicreply_list_header_topic_loc.setText(context.getResources().getString(R.string.postbar_topic_detail_loc_info,
					tmploc));
		}
		topicViewHolder.postbar_topicreply_list_header_topic_posterid.setText(context.getResources().getString(
				R.string.postbar_topic_detail_poster_seviceid, detail.getPosterSerial()));

		// 内容赋值
		topicViewHolder.postbar_topicreply_list_header_topic_postcontent.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				if (topicDetailActionListener != null)
					topicDetailActionListener.onLongClickContent();
				return true;
			}
		});
		topicViewHolder.postbar_topicreply_list_header_topic_postcontent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				if (topicDetailActionListener != null)
					topicDetailActionListener.onClickContent();
			}
		});
		if (detail.hasPostContent() && detail.getPostContent().getElementsCount() > 0) {
			// 有内容数据
			topicViewHolder.postbar_topicreply_list_header_topic_postcontent.setVisibility(View.VISIBLE);
			setPostContent(topicViewHolder.postbar_topicreply_list_header_topic_postcontent, detail.getId(), MsgsConstants.OT_TOPIC, detail
					.getPostContent().getElementsList(), false, 1);
			setJiContent(topicViewHolder.postbar_topicreply_list_header_topic_setcontent, detail);
		} else {
			// 没有内容数据
			topicViewHolder.postbar_topicreply_list_header_topic_postcontent.setVisibility(View.GONE);
			topicViewHolder.postbar_topicreply_list_header_topic_setcontent.setVisibility(View.GONE);
		}
		// 帖子标签
		// tag id为7表示其他写死判断
		final long tageId = detail.getPostTagId();
		String tagtxt = "";
		if (tageId == 7) {
			tagtxt = "其他(" + detail.getTagName() + detail.getTagTopicNums() + ")";
		} else {
			tagtxt = detail.getTagName() + "(" + detail.getTagTopicNums() + ")";
		}
		topicViewHolder.tagTxt.setText(tagtxt);
		// 赞
		if (detail.getHasPraise() > 0) {
			topicViewHolder.likeTxt.setText("已赞");
			topicViewHolder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_liked_txt));
			setLikeTxtIcon(topicViewHolder.likeTxt, R.drawable.game_like_pre3);
		} else {
			topicViewHolder.likeTxt.setText("赞");
			topicViewHolder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_unlike_txt));
			setLikeTxtIcon(topicViewHolder.likeTxt, R.drawable.game_like_nor);
		}
		List<UserVo> users = new ArrayList<UserVo>();
		List<TopicEstimateUsers> eusers = detail.getTopicEstimateUsersList();
		int length = eusers.size();
		for (int i = 0; i < length; i++) {
			UserVo uvo = new UserVo();
			TopicEstimateUsers euser = eusers.get(i);
			uvo.setUserid(euser.getUid());
			uvo.setAvatar(euser.getAvatar());
			users.add(uvo);
		}
		setLikeUserAvatar(topicViewHolder, users, detail, 0);
		likeOrUnLike(topicViewHolder, detail, users);
		chat(topicViewHolder.chatTxt, detail.getPosterUid());
		// 赞的用户页面
		topicViewHolder.likeUsersContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, SupportUserListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SUPPORT_USER_LIST_ACTIVITY_GAMEID, detail.getId());
				bundle.putInt(SystemConfig.TOTAL_SUPPORT_NUMBER, detail.getPraiseNums());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		// 评论
		topicViewHolder.replyTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onCommentHander();
			}
		});
	}

	/**
	 * 设置赞的用户头像
	 * 
	 * @param topicViewHolder
	 * @param userlist
	 */
	private void setLikeUserAvatar(TopicViewHolder topicViewHolder, List<UserVo> userlist, Msgs.PostbarTopicDetail detail, int addcount) {
		topicViewHolder.likeUser1.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser1.setImageResource(R.color.topiclist_item_btn_bg);
		topicViewHolder.likeUser2.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser2.setImageResource(R.color.topiclist_item_btn_bg);
		topicViewHolder.likeUser3.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser3.setImageResource(R.color.topiclist_item_btn_bg);
		topicViewHolder.likeUser4.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser4.setImageResource(R.color.topiclist_item_btn_bg);
		topicViewHolder.likeUser5.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser5.setImageResource(R.color.topiclist_item_btn_bg);
		topicViewHolder.likeUser6.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser6.setImageResource(R.color.topiclist_item_btn_bg);
		topicViewHolder.likeUser7.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser7.setImageResource(R.color.topiclist_item_btn_bg);
		topicViewHolder.likeUser8.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser8.setImageResource(R.color.topiclist_item_btn_bg);
		topicViewHolder.likeUser9.setVisibility(View.INVISIBLE);
		topicViewHolder.likeUser9.setImageResource(R.color.topiclist_item_btn_bg);
		if (userlist != null && userlist.size() > 0) {
			topicViewHolder.likeUsersContent.setVisibility(View.VISIBLE);
			int size = userlist.size();
			int pcount = detail.getPraiseNums();
			if (addcount > 0) {
				if (detail.getHasPraise() <= 0) {
					pcount += addcount;
				}
			} else if (addcount < 0) {
				if (detail.getHasPraise() > 0) {
					pcount += addcount;
				}
			} else {
				List<TopicEstimateUsers> eusers = detail.getTopicEstimateUsersList();
				int ucount = userlist.size();
				if (eusers != null) {
					int ecount = eusers.size();
					if (ecount < ucount)
						pcount++;
					else if (ecount > ucount)
						pcount--;
				} else {
					pcount++;
				}
			}
			if (pcount >= 9999) {
				topicViewHolder.likeUserCountTxt.setText("9999+");
			} else {
				topicViewHolder.likeUserCountTxt.setText(pcount + "");
			}
			for (int i = 0; i < size; i++) {
				if (i == 0) {
					topicViewHolder.likeUser1.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser1, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i == 1) {
					topicViewHolder.likeUser2.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser2, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i == 2) {
					topicViewHolder.likeUser3.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser3, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i == 3) {
					topicViewHolder.likeUser4.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser4, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i == 4) {
					topicViewHolder.likeUser5.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser5, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i == 5) {
					topicViewHolder.likeUser6.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser6, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i == 6) {
					topicViewHolder.likeUser7.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser7, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i == 7) {
					topicViewHolder.likeUser8.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser8, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i == 8) {
					topicViewHolder.likeUser9.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(topicViewHolder.likeUser9, userlist.get(i).getAvatar(), R.drawable.common_user_icon_default);
				}
				if (i > 8)
					break;
			}
		} else {
			topicViewHolder.likeUsersContent.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @param tv
	 * @param detail
	 * @param op
	 */
	private void likeOrUnLike(final TopicViewHolder holder, final Msgs.PostbarTopicDetail detail, final List<UserVo> users) {
		final TextView tv = holder.likeTxt;
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int op = MsgsConstants.OP_PRAISE;
				if ("赞".equals(tv.getText().toString())) {
					op = MsgsConstants.OP_PRAISE;
				} else {
					op = MsgsConstants.OP_PRAISE_CANCEL;
				}
				final int relOp = op;
				tv.setClickable(false);
				final UserVo muser = SystemContext.getInstance().getExtUserVo();
				ProxyFactory.getInstance().getPostbarProxy().likeOrUnLikeTopic(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						if (relOp == MsgsConstants.OP_PRAISE) {
							tv.setText("已赞");
							tv.setTextColor(context.getResources().getColor(R.color.topiclist_item_btn_bg));
							setLikeTxtIcon(tv, R.drawable.game_like_pre3);
							tv.setBackgroundResource(R.drawable.postbar_liked_btn_selector);
							if (muser != null) {
								UserVo uvo = new UserVo();
								uvo.setUserid(muser.getUserid());
								uvo.setAvatar(muser.getAvatar());
								users.add(0, uvo);
								setLikeUserAvatar(holder, users, detail, 1);
							}
							listener.onLikeHander();
						} else if (relOp == MsgsConstants.OP_PRAISE_CANCEL) {
							tv.setText("赞");
							tv.setTextColor(context.getResources().getColor(R.color.topiclist_item_user_nickname));
							setLikeTxtIcon(tv, R.drawable.game_like_nor);
							tv.setBackgroundResource(R.drawable.postbar_btn_selector);
							for (int i = 0; i < users.size(); i++) {
								UserVo user = users.get(i);
								if (user.getUserid() == muser.getUserid()) {
									users.remove(user);
								}
							}
							setLikeUserAvatar(holder, users, detail, -1);
							listener.onUnLikeHander();
						}
						tv.setClickable(true);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						switch (result) {
						case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_ALL_GUEST_DENY_VALUE:
							Intent intent = new Intent(context, BundPhoneActivity.class);
							context.startActivity(intent);
							break;
						default:
							ErrorCodeUtil.handleErrorCode(context, result, null);
							break;
						}
						tv.setClickable(true);
					}
				}, context, detail.getId(), detail.getId(), detail.getPosterUid(), detail.getPosterUid(), 0, relOp);
			}
		});
	}

	/**
	 * 
	 * @param tv
	 * @param uid
	 */
	private void chat(TextView tv, final long uid) {
		// 私聊天
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserChatFragmentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.BUNDLE_NAME_TOUSERID, uid);
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				context.startActivity(intent);
			}
		});
	}

	/**
	 * 
	 * @param followBtn
	 * @param uid
	 */
	private void followUser(final Button followBtn, final long uid) {
		// 关注
		followBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				followBtn.setClickable(false);
				if (followStaus == 1) {
					ProxyFactory.getInstance().getUserProxy().cannelFollowUser(new ProxyCallBack<Integer>() {

						@Override
						public void onSuccess(Integer result) {
							followBtn.setBackgroundResource(R.drawable.common_follow_nor);
							followStaus = 0;
							followBtn.setVisibility(View.VISIBLE);
							followBtn.setClickable(true);
							ToastUtil.showToast(context, "取消关注成功");
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							followBtn.setClickable(true);
							ToastUtil.showToast(context, "取消关注失败");
						}
					}, context, uid);
				} else {
					ProxyFactory.getInstance().getUserProxy().addFollowUser(new ProxyCallBack<Integer>() {

						@Override
						public void onSuccess(Integer result) {
							followBtn.setBackgroundResource(R.drawable.common_follow_pre);
							followStaus = 1;
							followBtn.setVisibility(View.GONE);
							followBtn.setClickable(true);
							ToastUtil.showToast(context, "关注成功");
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							followBtn.setClickable(true);
							ToastUtil.showToast(context, "关注失败");
						}

					}, context, uid, false);
				}
			}
		});
	}

	/**
	 * 
	 * @param likeTxt
	 * @param res
	 */
	private void setLikeTxtIcon(TextView likeTxt, int res) {
		Drawable sexdraw = context.getResources().getDrawable(res);
		sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
		likeTxt.setCompoundDrawables(sexdraw, null, null, null);
		likeTxt.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
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

	/**
	 * 设置内容
	 * 
	 * @param pview
	 * @param data
	 * @param isQuote
	 *            是否是引用，引用的字的样式不一样，比较小，
	 */
	private void setPostContent(LinearLayout pview, final long tId, final int tType, List<Msgs.PostElement> data, boolean isQuote, int mode) {
		pview.removeAllViews();
		final List<String> images = new ArrayList<String>();
		for (int i = 0; i < data.size(); i++) {
			final Msgs.PostElement element = data.get(i);
			// 判断类型
			if (element.getType() == PostElementType.PE_TEXT) {
				// 文本
				View view = this.inflater.inflate(R.layout.postbar_topicreply_list_item_textview, pview, false);
				pview.addView(view);
				MyTextView postbar_topicreply_list_postcontent_text = (MyTextView) view.findViewById(R.id.postbar_topicreply_list_postcontent_text);
				if (isQuote) {
					postbar_topicreply_list_postcontent_text.setTextColor(context.getResources().getColor(R.color.topic_head_item_content_color));
					postbar_topicreply_list_postcontent_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							context.getResources().getDimensionPixelSize(R.dimen.global_font_size5));
				} else {
					if (detailIsDel) {// 删除
						postbar_topicreply_list_postcontent_text.setTextColor(context.getResources().getColor(R.color.topic_reply_item_del_color));
					} else {
						if (mode == 1) {
							postbar_topicreply_list_postcontent_text.setTextColor(context.getResources().getColor(
									R.color.topic_head_item_content_color));
						} else {
							postbar_topicreply_list_postcontent_text.setTextColor(context.getResources().getColor(R.color.topiclist_order_bg_nor));
						}
					}
					postbar_topicreply_list_postcontent_text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
							context.getResources().getDimensionPixelSize(R.dimen.global_font_size2));
				}

				postbar_topicreply_list_postcontent_text.setText(SmileyUtil.ReplaceSmiley(context, element.getText(), context.getResources()
						.getDimensionPixelSize(R.dimen.global_string_smiley_width),
						context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth)));
				postbar_topicreply_list_postcontent_text.SetLinkClickIntercept(mURLSpanClickable);

			} else if (element.getType() == PostElementType.PE_IMAGE_ID_REF) {
				// 图片
				LinearLayout view = (LinearLayout) this.inflater.inflate(R.layout.postbar_topicreply_list_item_img, pview, false);
				if (element.getWidth() == 30 && element.getHeight() == 30) {
					view.setBackgroundDrawable(null);
					view.setGravity(Gravity.LEFT);
				}
				pview.addView(view);
				ImageView postbar_topicreply_list_postcontent_resource = (ImageView) view
						.findViewById(R.id.postbar_topicreply_list_postcontent_resource);

				new ImageLoader().loadRes(ResUtil.getOriginalRelUrl(element.getResourceId()), 0, postbar_topicreply_list_postcontent_resource,
						R.drawable.postbar_thumbimg_default);

				// ImageCacheLoader.getInstance().loadRes(ResUtil.getOriginalRelUrl(element.getResourceId()),
				// postbar_topicreply_list_postcontent_resource,
				// R.drawable.postbar_thumbimg_default,
				// R.drawable.postbar_thumbimg_default,
				// R.drawable.postbar_thumbimg_default, null, true);
				images.add(ResUtil.getOriginalRelUrl(element.getResourceId()));
				final int index = images.size() - 1;
				// 资源点击放大查看
				postbar_topicreply_list_postcontent_resource.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View paramView) {
						String[] tempt = images.toArray(new String[images.size()]);
						Intent intent = new Intent(context, ImageBrowerActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, index);
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, tId);
						bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, tempt);
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, tType);
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						context.startActivity(intent);
					}
				});
			}

		}

	}

	/**
	 * 设置集合贴内容
	 * 
	 * @param pview
	 * @param data
	 */
	private void setJiContent(LinearLayout pview, PostbarTopicDetail detail) {
		List<Msgs.PostElement> data = detail.getPostContent().getElementsList();
		boolean isHasTopicSet = false;
		pview.removeAllViews();
		for (int i = 0; i < data.size(); i++) {
			final Msgs.PostElement element = data.get(i);
			// 判断类型
			if (element.getType() == PostElementType.PE_TOPIC_SET_LINK || element.getType() == PostElementType.PE_GOOD_SET_LINK) {
				final TopicSetDetail setDetail = element.getTopicSetDetail();
				if (setDetail != null) {
					isHasTopicSet = true;
					View view = this.inflater.inflate(R.layout.postbar_topicreply_list_item_set, pview, false);
					pview.addView(view);
					TextView postbar_topicreply_list_item_set_name = (TextView) view.findViewById(R.id.postbar_topicreply_list_item_set_name);
					postbar_topicreply_list_item_set_name.setText(setDetail.getTopicSetTitle());
					LinearLayout postbar_topicreply_list_item_set_layout = (LinearLayout) view
							.findViewById(R.id.postbar_topicreply_list_item_set_layout);
					postbar_topicreply_list_item_set_layout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if (setDetail.getTid() != 0) {
								if (element.getType() == PostElementType.PE_TOPIC_SET_LINK) {
									showJiTopicDetail(setDetail.getTid());
								} else if (element.getType() == PostElementType.PE_GOOD_SET_LINK) {
									showGoodsDetail(setDetail.getTid());
								}
							}
						}
					});
				}
			}
		}
		if (!isHasTopicSet) {
			pview.setVisibility(View.GONE);
		}

	}

	/**
	 * 点击集合贴，进入帖子详情（先做帖子是否已被删除的提示）
	 * 
	 * @param topicId
	 */
	private void showJiTopicDetail(final long topicId) {
		if (FastClickLimitUtil.isFastClick())
			return;
		// 通过帖子id获得帖子详情，然后判断帖子是否被删除
		// 获得帖子详情
		ProxyCallBack<Msgs.PostbarTopicDetail> callback = new ProxyCallBack<Msgs.PostbarTopicDetail>() {

			@Override
			public void onSuccess(final PostbarTopicDetail result) {
				((TopicDetailActivity) context).runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (result != null) {
							if (result.getIsDel()) {
								ToastUtil.showToast(context, context.getResources().getString(R.string.postbaor_set_topic_del));
							} else {
								long gid = result.getGameid();
								if (gid != 0) {
									// 根据当前的应用配置判断是否要启动游伴
									AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
									if (appconfig != null && appconfig.isRecbarmsg() && gid != appconfig.getGameId()) {
										AppUtil.openGame(context, topicId, TopicDetailActivity.class.getName(),
												context.getResources().getString(R.string.postbar_show_topic_tip_for_youban_uninstall));
									} else {
										Intent intent = new Intent(context, TopicDetailActivity.class);
										Bundle bundle = new Bundle();
										bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, topicId);
										intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
										context.startActivity(intent);
									}
								}
							}
						} else {
							ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_POSTBAR_GETTOPICDETAILISNULL, null);
						}
					}
				});
			}

			@Override
			public void onFailure(final Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}

		};
		ProxyFactory.getInstance().getPostbarProxy().getTopicDetail(callback, context, topicId);

	}

	private void showGoodsDetail(final long goodsId) {
		if (FastClickLimitUtil.isFastClick())
			return;
		if (goodsId == 0) {
			return;
		}
		Intent intent = new Intent(context, GoodsDetailActivity.class);
		intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_ID, goodsId);
		intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_FLAG, true);
		context.startActivity(intent);
	}

	/**
	 * 帖子详情上的操作回调
	 * 
	 * @ClassName: TopicDetailActionListener
	 * @Description: 帖子详情上的操作回调
	 * @author chuanglong
	 * @date 2014-8-11 下午1:53:38
	 * @Version 1.0
	 * 
	 */
	public interface TopicDetailActionListener {
		// 内容的单击回调
		public void onClickContent();

		// 内容的长按回调
		public boolean onLongClickContent();

		// 单击分享
		public void onClickShare();
	}

	/**
	 * 设置帖子详情上的操作回调
	 * 
	 * @param listener
	 */
	public void setTopicDetailListener(TopicDetailActionListener listener) {
		topicDetailActionListener = listener;
	}

	/**
	 * 设置回复的总数
	 * 
	 * @param totalcount
	 */
	public void setReplyTotalCount(int totalCount) {
		mTotalCount = totalCount;
	}

	public int getPmode() {
		return pmode;
	}

	public void setPmode(int pmode) {
		this.pmode = pmode;
	}

	public String getGamename() {
		return gamename;
	}

	public void setGamename(String gamename) {
		this.gamename = gamename;
	}
}
