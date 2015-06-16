/**      
 * PostListAdapter.java Create on 2013-12-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.module.game.object.GameData;
import com.iwgame.msgs.module.game.object.GameNoData;
import com.iwgame.msgs.module.game.ui.GameDetailAlikeAcitivity;
import com.iwgame.msgs.module.game.ui.GameDetailGroupActivity;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.postbar.ui.MainTopicListFragment;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailFragment;
import com.iwgame.msgs.module.postbar.ui.TopicListActivity;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.SupportUserListActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostElementType;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.proto.Msgs.TopicEstimateUsers;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.GameUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.MyTextView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: MainTopicListAdapter
 * @Description: 帖子列表适配器
 * @author 王卫
 * @date 2015-2-9 下午10:21:34
 * @Version 2.0
 * 
 */
public class MainTopicListAdapter extends BaseAdapter {

	protected static final String TAG = "PostListAdapter";
	private Context context;
	private List<Object> data;
	private LayoutInflater inflater;
	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	private List<View> radioBtnList = new ArrayList<View>();
	private List<Integer> pixel = new ArrayList<Integer>();
	private DisplayMetrics dm;
	private int nodataItemHeight;
	private int flag;// 当前是哪个标签
	private boolean isNeedRefreshTags = false;// 是否需要重新刷新绘制标签，默认为不需要，当发帖成功后需要重新定位到全部标签
	private UserVo loginUserVo = null;

	private int targettype = 0;

	// 布局类型有3中 ,TYPE_DETAIL为头部，TYPE_LIST为列表，TYPE_NODATA为无数据，
	// TYPE_LIST_DETAIL非置顶帖列表
	final int VIEW_TYPE = 4;
	final int TYPE_DETAIL = 0;
	final int TYPE_LIST = 1;
	final int TYPE_NODATA = 2;
	final int TYPE_LIST_DETAIL = 3;

	public static int ACTION_DEL = 1;

	private Map<Integer, List<UserVo>> likeUserMap;

	public interface ItemClickListener {
		/** 用户其他删除等行为 **/
		public void onClickAction(int position, int action);

		/** 分享贴吧 **/
		public void onClickShare();

		/** 分享帖子 **/
		public void onClickShareTipic();

		/** 点击标签按钮筛选刷新 **/
		public void flushByTag(int tageId, String tagName);

		public void followGameStatus(boolean followed);

	}

	private ItemClickListener itemClickListener = null;

	private int ordertype = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;

	private boolean editStatus;

	MainTopicListFragment fragment;

	GameData gameData;

	public MainTopicListAdapter(Context context, List<Object> data, int targettype, ItemClickListener listener) {
		this.context = context;
		this.data = data;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.targettype = targettype;
		itemClickListener = listener;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
		likeUserMap = new HashMap<Integer, List<UserVo>>();
	}

	/**
	 * 设置编辑状态， true 处于编辑中（删除按钮显示），false 处于一般状态 （删除按钮不显示）
	 * 
	 * @param status
	 */
	public void setEditStatus(boolean status) {
		editStatus = status;
	}

	/**
	 * 设置排序方式
	 * 
	 * @param order
	 */
	public void setOrderType(int order) {
		ordertype = order;
	}

	public void clearLikeUserMap() {
		likeUserMap.clear();
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
		if (data.get(position) instanceof GameData) {
			return TYPE_DETAIL;
		} else if (data.get(position) instanceof PostbarTopicDetail) {
			PostbarTopicDetail ptd = (PostbarTopicDetail) data.get(position);
			if (ptd.getIsTop()) {
				return TYPE_LIST;
			} else {
				return TYPE_LIST_DETAIL;
			}
		} else if (data.get(position) instanceof GameNoData) {
			return TYPE_NODATA;
		} else {
			return TYPE_DETAIL;
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
		final int type = getItemViewType(position);
		HeaderGameDetailViewHolder tempdetailViewHolder = new HeaderGameDetailViewHolder();
		ViewHolder tmpViewHolder = new ViewHolder();
		NodateViewHolder tempNodateViewHolder = new NodateViewHolder();
		ListDetailViewHolder tempListDetailViewHolder = new ListDetailViewHolder();
		if (convertView == null) {
			switch (type) {
			case TYPE_DETAIL:
				convertView = this.inflater.inflate(R.layout.postbar_main_topic_list_header_gamedetail, null);
				tempdetailViewHolder.icon = (RoundedImageView) convertView.findViewById(R.id.icon);// 头像
				tempdetailViewHolder.gameName = (TextView) convertView.findViewById(R.id.gameName);// 名称
				tempdetailViewHolder.user_num = (TextView) convertView.findViewById(R.id.user_num);// 游伴数
				tempdetailViewHolder.user_num_view = (LinearLayout) convertView.findViewById(R.id.user_num_view);
				tempdetailViewHolder.group_num = (TextView) convertView.findViewById(R.id.group_num);// 公会数
				tempdetailViewHolder.group_num_view = (LinearLayout) convertView.findViewById(R.id.group_num_view);
				tempdetailViewHolder.news_num = (TextView) convertView.findViewById(R.id.news_num);// 新闻数
				tempdetailViewHolder.news_num_view = (LinearLayout) convertView.findViewById(R.id.news_num_view);
				tempdetailViewHolder.raiders_num = (TextView) convertView.findViewById(R.id.raiders_num);// 攻略数数
				tempdetailViewHolder.raiders_num_view = (LinearLayout) convertView.findViewById(R.id.raiders_num_view);
				tempdetailViewHolder.btn_follow = (Button) convertView.findViewById(R.id.btn_follow);// 关注按钮
				tempdetailViewHolder.shareBtn = (ImageView) convertView.findViewById(R.id.shareBtn);// share
				convertView.setTag(tempdetailViewHolder);
				break;
			case TYPE_LIST:
				convertView = this.inflater.inflate(R.layout.postbar_topic_list_item, null);
				tmpViewHolder.postbar_topic_list_item_isnotice = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isnotice);
				tmpViewHolder.postbar_topic_list_item_isessence = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isessence);
				tmpViewHolder.postbar_topic_list_item_istop = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_istop);
				tmpViewHolder.postbar_topic_list_item_ishot = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_ishot);
				tmpViewHolder.postbar_topic_list_item_isnews = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isnews);
				tmpViewHolder.postbar_topic_list_item_isji = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isji);
				tmpViewHolder.postbar_topic_list_item_islock = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_islock);
				tmpViewHolder.postbar_topic_list_item_title = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_title);
				tmpViewHolder.postbar_topic_list_item_nickname = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_nickname);
				tmpViewHolder.postbar_topic_list_item_date = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_date);
				tmpViewHolder.postbar_topic_list_item_ishavepic = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_ishavepic);
				tmpViewHolder.postbar_topic_list_item_comments = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_comments);
				tmpViewHolder.postbar_topic_list_item_visits = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_visits);
				tmpViewHolder.postbar_topic_list_item_del = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_del);
				tmpViewHolder.postbar_topic_list_item_loc = (TextView) convertView.findViewById(R.id.postbar_topic_list_item_loc);
				tmpViewHolder.postbar_topic_list_item_title_gap = (View) convertView.findViewById(R.id.postbar_topic_list_item_title_gap);

				tmpViewHolder.tmp_fg_1 = (View) convertView.findViewById(R.id.tmp_fg_1);
				tmpViewHolder.tmp_fg_2 = (View) convertView.findViewById(R.id.tmp_fg_2);
				tmpViewHolder.tmp_fg_3 = (View) convertView.findViewById(R.id.tmp_fg_3);
				tmpViewHolder.postbar_topic_list_item_showorhide_ll = (LinearLayout) convertView
						.findViewById(R.id.postbar_topic_list_item_showorhide_ll);
				tmpViewHolder.postbar_topic_list_item_master = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_master);
				convertView.setTag(tmpViewHolder);
				break;
			case TYPE_NODATA:
				convertView = this.inflater.inflate(R.layout.common_null_data_bg, null);
				tempNodateViewHolder.bg_layout = (LinearLayout) convertView.findViewById(R.id.bg_layout);
				tempNodateViewHolder.bgIcon = (ImageView) convertView.findViewById(R.id.bgIcon);
				convertView.setTag(tempNodateViewHolder);
				break;
			case TYPE_LIST_DETAIL:
				convertView = this.inflater.inflate(R.layout.postbar_topic_list_detail_item, null);
				tempListDetailViewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
				tempListDetailViewHolder.nickname = (TextView) convertView.findViewById(R.id.nickname);
				tempListDetailViewHolder.master = (ImageView) convertView.findViewById(R.id.master);
				tempListDetailViewHolder.age = (TextView) convertView.findViewById(R.id.age);
				tempListDetailViewHolder.userDesc = (TextView) convertView.findViewById(R.id.userDesc);
				tempListDetailViewHolder.titleContent = (LinearLayout) convertView.findViewById(R.id.titleContent);
				tempListDetailViewHolder.postbar_topic_list_item_islock = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_islock);
				tempListDetailViewHolder.postbar_topic_list_item_istop = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_istop);
				tempListDetailViewHolder.postbar_topic_list_item_isgift = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isgift);
				tempListDetailViewHolder.postbar_topic_list_item_isnotice = (ImageView) convertView
						.findViewById(R.id.postbar_topic_list_item_isnotice);
				tempListDetailViewHolder.postbar_topic_list_item_isessence = (ImageView) convertView
						.findViewById(R.id.postbar_topic_list_item_isessence);
				tempListDetailViewHolder.postbar_topic_list_item_isji = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isji);
				tempListDetailViewHolder.postbar_topic_list_item_ishot = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_ishot);
				tempListDetailViewHolder.postbar_topic_list_item_isnews = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isnews);
				tempListDetailViewHolder.postbar_topic_list_item_ishavepic = (ImageView) convertView
						.findViewById(R.id.postbar_topic_list_item_ishavepic);
				tempListDetailViewHolder.postbar_topic_list_item_title_gap = (View) convertView.findViewById(R.id.postbar_topic_list_item_title_gap);
				tempListDetailViewHolder.postbarTitle = (TextView) convertView.findViewById(R.id.postbarTitle);
				tempListDetailViewHolder.postbarContentTxt = (MyTextView) convertView.findViewById(R.id.postbarContentTxt);
				tempListDetailViewHolder.images = (LinearLayout) convertView.findViewById(R.id.images);
				tempListDetailViewHolder.imageFirst = (ImageView) convertView.findViewById(R.id.imageFirst);
				tempListDetailViewHolder.imageSecond = (ImageView) convertView.findViewById(R.id.imageSecond);
				tempListDetailViewHolder.imageThird = (ImageView) convertView.findViewById(R.id.imageThird);
				tempListDetailViewHolder.tagTxt = (TextView) convertView.findViewById(R.id.tagTxt);
				tempListDetailViewHolder.likeTxt = (TextView) convertView.findViewById(R.id.likeTxt);
				tempListDetailViewHolder.chatTxt = (TextView) convertView.findViewById(R.id.chatTxt);
				tempListDetailViewHolder.replyTxt = (TextView) convertView.findViewById(R.id.replyTxt);
				tempListDetailViewHolder.shareTxt = (TextView) convertView.findViewById(R.id.shareTxt);
				tempListDetailViewHolder.likeUsersContent = (LinearLayout) convertView.findViewById(R.id.likeUsersContent);
				tempListDetailViewHolder.likeUser1 = (ImageView) convertView.findViewById(R.id.likeUser1);
				tempListDetailViewHolder.likeUser2 = (ImageView) convertView.findViewById(R.id.likeUser2);
				tempListDetailViewHolder.likeUser3 = (ImageView) convertView.findViewById(R.id.likeUser3);
				tempListDetailViewHolder.likeUser4 = (ImageView) convertView.findViewById(R.id.likeUser4);
				tempListDetailViewHolder.likeUser5 = (ImageView) convertView.findViewById(R.id.likeUser5);
				tempListDetailViewHolder.likeUser6 = (ImageView) convertView.findViewById(R.id.likeUser6);
				tempListDetailViewHolder.likeUser7 = (ImageView) convertView.findViewById(R.id.likeUser7);
				tempListDetailViewHolder.likeUser8 = (ImageView) convertView.findViewById(R.id.likeUser8);
				tempListDetailViewHolder.likeUser9 = (ImageView) convertView.findViewById(R.id.likeUser9);
				tempListDetailViewHolder.likeUserCountTxt = (TextView) convertView.findViewById(R.id.likeUserCountTxt);

				tempListDetailViewHolder.firstReplyContent = (LinearLayout) convertView.findViewById(R.id.firstReplyContent);
				tempListDetailViewHolder.firstReplyUserName = (TextView) convertView.findViewById(R.id.firstReplyUserName);
				tempListDetailViewHolder.firstReplyTxt = (TextView) convertView.findViewById(R.id.firstReplyTxt);
				tempListDetailViewHolder.secondReplyContent = (LinearLayout) convertView.findViewById(R.id.secondReplyContent);
				tempListDetailViewHolder.secondReplyUserName = (TextView) convertView.findViewById(R.id.secondReplyUserName);
				tempListDetailViewHolder.secondReplyTxt = (TextView) convertView.findViewById(R.id.firstReplyTxt);
				tempListDetailViewHolder.allCommentTxt = (TextView) convertView.findViewById(R.id.allCommentTxt);
				tempListDetailViewHolder.imageNum = (TextView) convertView.findViewById(R.id.imageNum);
				convertView.setTag(tempListDetailViewHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_DETAIL:
				tempdetailViewHolder = (HeaderGameDetailViewHolder) convertView.getTag();
				break;
			case TYPE_LIST:
				tmpViewHolder = (ViewHolder) convertView.getTag();
				break;
			case TYPE_NODATA:
				tempNodateViewHolder = (NodateViewHolder) convertView.getTag();
				break;
			case TYPE_LIST_DETAIL:
				tempListDetailViewHolder = (ListDetailViewHolder) convertView.getTag();
				break;
			}

		}

		final HeaderGameDetailViewHolder detailViewHolder = tempdetailViewHolder;
		final ViewHolder viewHolder = tmpViewHolder;
		final NodateViewHolder nodateViewHolder = tempNodateViewHolder;
		final ListDetailViewHolder listDetailViewHolder = tempListDetailViewHolder;

		if (type == TYPE_DETAIL) {
			gameData = (GameData) data.get(position);
			if (gameData != null) {
				final long targetId = gameData.getGid();
				if (gameData.getGamename() != null)
					detailViewHolder.gameName.setText(gameData.getGamename());
				detailViewHolder.user_num.setText(gameData.getFollowCount() + "");
				detailViewHolder.group_num.setText(gameData.getGroupCount() + "");
				int mLikeCount = gameData.getIspraise();
				int mDislikeCount = gameData.getIscriticize();
				if (gameData.getGamelogo() != null && !gameData.getGamelogo().isEmpty()) {
					new ImageLoader().loadRes(gameData.getGamelogo(), 0, detailViewHolder.icon, R.drawable.common_default_icon);
				} else {
					new ImageLoader().loadRes("drawable://" + R.drawable.common_default_icon, 0, detailViewHolder.icon);
				}
				if (targetId > 0) {
					if (AdaptiveAppContext.getInstance().getAppConfig().getGameId() == 0) {
						getGameRel(detailViewHolder, gameData);
					} else {
						detailViewHolder.btn_follow.setVisibility(View.INVISIBLE);
					}
				}

				detailViewHolder.btn_follow.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (gameData != null && gameData.isGetGameInfo()) {
							if (gameData.getRelation() == 1) {
								followActionGame(detailViewHolder, gameData, targetId, MsgsConstants.OP_UNFOLLOW);
							} else {
								followActionGame(detailViewHolder, gameData, targetId, MsgsConstants.OP_FOLLOW);
							}
						} else {
							ToastUtil.showToast(context, "数据未加载完成，稍后再操作");
						}

					}
				});

				detailViewHolder.shareBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						itemClickListener.onClickShare();
					}
				});

				detailViewHolder.user_num_view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 游伴
						Intent intent = new Intent(context, GameDetailAlikeAcitivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, targetId);
						intent.putExtras(bundle);
						context.startActivity(intent);

					}
				});
				detailViewHolder.group_num_view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 公会
						if (gameData != null && gameData.isGetGameInfo()) {
							Intent intent = new Intent(context, GameDetailGroupActivity.class);
							Bundle bundle = new Bundle();
							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, targetId);
							bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, gameData.getGamename());
							intent.putExtras(bundle);
							context.startActivity(intent);
						}
					}
				});

			}

			ProxyFactory.getInstance().getPostbarProxy().getTopicTags(new ProxyCallBack<List<TopicTagVo>>() {

				@Override
				public void onSuccess(List<TopicTagVo> result) {
					for (int i = 0; i < result.size(); i++) {
						final TopicTagVo tgvo = result.get(i);
						if (tgvo.getId() == 1) {
							// 新闻
							detailViewHolder.news_num.setText(tgvo.getTopicNums() + "");
							detailViewHolder.news_num_view.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (FastClickLimitUtil.isFastClick()) {
										return;
									}
									if (tgvo.getTopicNums() > 0) {
										// itemClickListener.flushByTag(1,
										// "新闻");
										Intent intent = new Intent(context, TopicListActivity.class);
										Bundle bundle = new Bundle();
										bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, TopicListActivity.NEWS);
										bundle.putLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID, gameData.getGid());
										bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGID, 1);
										bundle.putString(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGNAME, "新闻");
										intent.putExtras(bundle);
										context.startActivity(intent);
									} else
										ToastUtil.showToast(context, "没有新闻相关的帖子");
								}
							});
						} else if (tgvo.getId() == 2) {
							// 攻略
							detailViewHolder.raiders_num.setText(tgvo.getTopicNums() + "");
							detailViewHolder.raiders_num_view.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (FastClickLimitUtil.isFastClick()) {
										return;
									}
									if (tgvo.getTopicNums() > 0) {
										// itemClickListener.flushByTag(2,
										// "攻略");
										Intent intent = new Intent(context, TopicListActivity.class);
										Bundle bundle = new Bundle();
										bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, TopicListActivity.RAIDES);
										bundle.putLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID, gameData.getGid());
										bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGID, 2);
										bundle.putString(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGNAME, "攻略");
										intent.putExtras(bundle);
										context.startActivity(intent);
									} else
										ToastUtil.showToast(context, "没有攻略相关的帖子");
								}
							});
						}
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					// TODO Auto-generated method stub
				}
			}, gameData.getGid());
		} else if (type == TYPE_LIST) {
			final Msgs.PostbarTopicDetail detail = (Msgs.PostbarTopicDetail) data.get(position);
			// 标签显示规则， 有置顶的，显示2个,后面的显示顺序 礼包，公告，精华，集合，热，新
			// 其他，全部显示

			// 是否显示了tag的图片
			boolean isShowtagImage = false;

			if (position == 1) {
				viewHolder.tmp_fg_1.setVisibility(View.VISIBLE);
				viewHolder.tmp_fg_2.setVisibility(View.VISIBLE);
				viewHolder.tmp_fg_3.setVisibility(View.VISIBLE);
			} else {
				viewHolder.tmp_fg_1.setVisibility(View.GONE);
				viewHolder.tmp_fg_2.setVisibility(View.GONE);
				viewHolder.tmp_fg_3.setVisibility(View.GONE);
			}
			// 下面需要换算，显示那些tag
			// 是否置顶
			if (detail.getIsTop()) {
				viewHolder.postbar_topic_list_item_showorhide_ll.setVisibility(View.GONE);
				viewHolder.postbar_topic_list_item_istop.setVisibility(View.VISIBLE);
				isShowtagImage = true;
			} else {
				viewHolder.postbar_topic_list_item_showorhide_ll.setVisibility(View.VISIBLE);
				viewHolder.postbar_topic_list_item_istop.setVisibility(View.GONE);
			}
			// 标题
			if (!isShowtagImage)
				viewHolder.postbar_topic_list_item_title_gap.setVisibility(View.GONE);
			else
				viewHolder.postbar_topic_list_item_title_gap.setVisibility(View.GONE);
			viewHolder.postbar_topic_list_item_title.setText(detail.getTitle());
			String remotePosition = null;
			// 发布者
			viewHolder.postbar_topic_list_item_nickname.setText(detail.getPosterNickname());
			// 发布时间
			viewHolder.postbar_topic_list_item_date.setText(SafeUtils.getDate2MyStr2(detail.getCreateTime()));
			// 发布者是否是吧主
			if (detail.getPosterIsMaster()) {
				viewHolder.postbar_topic_list_item_master.setImageResource(R.drawable.postbar_bazhu);
			} else {
				viewHolder.postbar_topic_list_item_master.setImageResource(R.drawable.game_icon_people);
			}
			remotePosition = detail.getPosition();

			String tmploc = DistanceUtil.covertDistance(remotePosition);
			if (!tmploc.isEmpty()) {
				tmpViewHolder.postbar_topic_list_item_loc.setText(" | " + tmploc);
			} else {
				tmpViewHolder.postbar_topic_list_item_loc.setText("");
			}

			// 评论数
			viewHolder.postbar_topic_list_item_comments.setText("" + detail.getComments());

			String visits = "/0";

			if (detail.getTopicVisits() > 99999) {
				visits = "10万以上";
			} else {
				visits = "/" + detail.getTopicVisits();
			}
			viewHolder.postbar_topic_list_item_visits.setText(visits);

			if (targettype != SystemConfig.GETTOPICLIST_TARGETTYPE_FAVORITE) {
				viewHolder.postbar_topic_list_item_del.setVisibility(View.GONE);
			} else {
				// 是否显示删除按钮
				if (editStatus) {
					viewHolder.postbar_topic_list_item_del.setVisibility(View.VISIBLE);
				} else {
					viewHolder.postbar_topic_list_item_del.setVisibility(View.GONE);
				}

			}

			viewHolder.postbar_topic_list_item_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					delFavorite(position);
				}
			});
		} else if (type == TYPE_NODATA) {
			final GameNoData gameNoData = (GameNoData) data.get(position);
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, nodataItemHeight);// (LayoutParams)
																																	// nodateViewHolder.bg_layout.getLayoutParams();
			nodateViewHolder.bg_layout.setLayoutParams(params);
			if (gameNoData.getNodateType() == 0) {// 网络原因
				nodateViewHolder.bgIcon.setBackgroundResource(R.drawable.topiclist_nonet_refresh_selector);
				nodateViewHolder.bgIcon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (gameNoData.getNodateType() == 0)
							((GameTopicListActivity) context).actionReloadData();
					}
				});
			} else {// 没有数据
				if (gameNoData.getTagid() == SystemConfig.POSTBAR_TOPIC_TAG_ALL) {
					if (gameNoData.getFilter() == 0) {// 全部
						nodateViewHolder.bgIcon.setBackgroundResource(R.drawable.common_no_postbar);
					} else {// 精华帖
						nodateViewHolder.bgIcon.setBackgroundResource(R.drawable.common_no_tab);
					}
				} else {
					nodateViewHolder.bgIcon.setBackgroundResource(R.drawable.common_no_tab);
				}
			}
		} else if (type == TYPE_LIST_DETAIL) {
			final Msgs.PostbarTopicDetail detail = (Msgs.PostbarTopicDetail) data.get(position);
			// 标签显示规则， 有置顶的，显示2个,后面的显示顺序 礼包，公告，精华，集合，热，新
			// 其他，全部显示

			boolean isShowfullTag = false;
			// 是否显示了tag的图片
			boolean isShowtagImage = false;
			// 是否显示了图片的image
			boolean isShowImageImage = false;

			// 下面需要换算，显示那些tag
			// 是否锁帖
			if (detail.getIsLock()) {
				listDetailViewHolder.postbar_topic_list_item_islock.setVisibility(View.VISIBLE);
				isShowtagImage = true;
			} else {
				listDetailViewHolder.postbar_topic_list_item_islock.setVisibility(View.GONE);
			}
			// 是否置顶
			if (detail.getIsTop()) {
				listDetailViewHolder.postbar_topic_list_item_istop.setVisibility(View.VISIBLE);
				isShowtagImage = true;
			} else {
				listDetailViewHolder.postbar_topic_list_item_istop.setVisibility(View.GONE);
			}
			// 是否公告
			if (detail.getIsNotice() && !isShowfullTag) {
				listDetailViewHolder.postbar_topic_list_item_isnotice.setVisibility(View.VISIBLE);
				isShowtagImage = true;
				if (detail.getIsTop())
					isShowfullTag = true;
			} else {
				listDetailViewHolder.postbar_topic_list_item_isnotice.setVisibility(View.GONE);
			}

			// 是否礼包（暂时未加判断）
			// 是否热帖
			if (detail.getIsHot() && !isShowfullTag) {
				isShowtagImage = true;
				listDetailViewHolder.postbar_topic_list_item_ishot.setVisibility(View.VISIBLE);
				if (detail.getIsTop())
					isShowfullTag = true;
			} else {
				listDetailViewHolder.postbar_topic_list_item_ishot.setVisibility(View.GONE);
			}
			// 是否精华
			if (detail.getIsEssence() && !isShowfullTag) {
				isShowtagImage = true;
				listDetailViewHolder.postbar_topic_list_item_isessence.setVisibility(View.VISIBLE);
				if (detail.getIsTop())
					isShowfullTag = true;
			} else {
				listDetailViewHolder.postbar_topic_list_item_isessence.setVisibility(View.GONE);
			}
			// 是否集合贴
			if (detail.getIsTopicSet() && !isShowfullTag) {
				isShowtagImage = true;
				listDetailViewHolder.postbar_topic_list_item_isji.setVisibility(View.VISIBLE);
				if (detail.getIsTop())
					isShowfullTag = true;
			} else {
				listDetailViewHolder.postbar_topic_list_item_isji.setVisibility(View.GONE);
			}
			// 是否新帖
			if ((detail.getCreateTime() + SystemContext.getInstance().getPTID() > SystemContext.getInstance().getCurrentTimeInMillis())
					&& !isShowfullTag) {
				isShowtagImage = true;
				listDetailViewHolder.postbar_topic_list_item_isnews.setVisibility(View.VISIBLE);
				if (detail.getIsTop())
					isShowfullTag = true;
			} else {
				listDetailViewHolder.postbar_topic_list_item_isnews.setVisibility(View.GONE);
			}

			// 1.是否有图片 2.显示内容文字，如果由内容显示第一条目的内容
			listDetailViewHolder.images.setVisibility(View.GONE);
			listDetailViewHolder.imageNum.setVisibility(View.GONE);
			listDetailViewHolder.imageFirst.setVisibility(View.INVISIBLE);
			listDetailViewHolder.imageSecond.setVisibility(View.INVISIBLE);
			listDetailViewHolder.imageThird.setVisibility(View.INVISIBLE);
			if (detail.hasPostContent() && detail.getPostContent().getElementsCount() > 0) {
				List<Msgs.PostElement> data = detail.getPostContent().getElementsList();
				final Msgs.PostElement firstPE = data.get(0);
				String ctxt = firstPE.getText();
				if (ctxt != null && !"".equals(ctxt)) {
					listDetailViewHolder.postbarContentTxt.setVisibility(View.VISIBLE);
					listDetailViewHolder.postbarContentTxt.setText(SmileyUtil.ReplaceSmiley(context, ctxt, context.getResources()
							.getDimensionPixelSize(R.dimen.global_string_smiley_width),
							context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth)));
				} else {
					listDetailViewHolder.postbarContentTxt.setVisibility(View.GONE);
					listDetailViewHolder.postbarContentTxt.setText("");
				}
				int count = 0;
				List<String> images = new ArrayList<String>();
				for (int i = 0; i < data.size(); i++) {
					final Msgs.PostElement element = data.get(i);
					if (element.getType() == PostElementType.PE_IMAGE_ID_REF && element.getHeight() != 30 && element.getWidth() != 30) {
						isShowImageImage = true;
						images.add(ResUtil.getOriginalRelUrl(element.getResourceId()));
						count++;
						listDetailViewHolder.images.setVisibility(View.VISIBLE);
						if (count == 1) {
							listDetailViewHolder.imageFirst.setVisibility(View.VISIBLE);
							ImageViewUtil.showImage(listDetailViewHolder.imageFirst, element.getResourceId(), R.drawable.postbar_thumbimg_default);
						}
						if (count == 2) {
							listDetailViewHolder.imageSecond.setVisibility(View.VISIBLE);
							ImageViewUtil.showImage(listDetailViewHolder.imageSecond, element.getResourceId(), R.drawable.postbar_thumbimg_default);
						}
						if (count == 3) {
							listDetailViewHolder.imageThird.setVisibility(View.VISIBLE);
							ImageViewUtil.showImage(listDetailViewHolder.imageThird, element.getResourceId(), R.drawable.postbar_thumbimg_default);
						}
					}
				}
				if (images.size() > 3) {
					listDetailViewHolder.imageNum.setVisibility(View.VISIBLE);
					listDetailViewHolder.imageNum.setText(images.size() + "");
				}
				String[] tempt = images.toArray(new String[images.size()]);
				browerImages(listDetailViewHolder.imageFirst, tempt, 0);
				browerImages(listDetailViewHolder.imageSecond, tempt, 1);
				browerImages(listDetailViewHolder.imageThird, tempt, 2);
			}

			if (isShowImageImage) {
				listDetailViewHolder.postbar_topic_list_item_ishavepic.setVisibility(View.VISIBLE);
			} else {
				listDetailViewHolder.postbar_topic_list_item_ishavepic.setVisibility(View.GONE);
			}
			// 标题
			if (!isShowtagImage && !isShowImageImage)
				listDetailViewHolder.postbar_topic_list_item_title_gap.setVisibility(View.GONE);
			else
				listDetailViewHolder.postbar_topic_list_item_title_gap.setVisibility(View.VISIBLE);
			listDetailViewHolder.postbarTitle.setText(detail.getTitle());

			// 发布者
			listDetailViewHolder.nickname.setText(detail.getPosterNickname());

			// 发布者是否是吧主
			if (detail.getPosterIsMaster()) {
				listDetailViewHolder.master.setVisibility(View.VISIBLE);
			} else {
				listDetailViewHolder.master.setVisibility(View.INVISIBLE);
			}
			// 发布时间
			String tmploc = DistanceUtil.covertDistance(detail.getPosition());

			String time = "";
			String desc = "";
			if (ordertype == MsgsConstants.POSTBAR_ORDER_REPLY_TIME) {
				time = SafeUtils.getDate2MyStr2(detail.getLastReplyTime());
				if (!tmploc.isEmpty()) {
					desc = tmploc + " | " + time;
				} else {
					desc = time;
				}
			} else {
				time = SafeUtils.getDate2MyStr2(detail.getCreateTime());
				if (!tmploc.isEmpty()) {
					desc = time + " | " + tmploc;
				} else {
					desc = time;
				}
			}
			listDetailViewHolder.userDesc.setText(desc);

			// 评论数
			// int comments = detail.getComments();
			// if (comments > 0) {
			// listDetailViewHolder.allCommentTxt.setVisibility(View.VISIBLE);
			// listDetailViewHolder.allCommentTxt.setText("查看全部评论" + comments +
			// "条");
			// } else {
			// listDetailViewHolder.allCommentTxt.setVisibility(View.GONE);
			// }
			// listDetailViewHolder.allCommentTxt.setOnClickListener(new
			// OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// Intent intent = new Intent(context, TopicDetailActivity.class);
			// Bundle bundle = new Bundle();
			// bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID,
			// detail.getId());
			// bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID,
			// detail.getGameid());
			// bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE,
			// TopicDetailFragment.MODE_GENERAL);
			// intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			// context.startActivity(intent);
			// }
			// });
			// 发布者头像
			ImageViewUtil.showImage(listDetailViewHolder.icon, detail.getPosterAvatar(), R.drawable.common_user_icon_default);
			// 设置头像单击
			listDetailViewHolder.icon.setOnClickListener(new OnClickListener() {

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
			// 设置年龄和性别
			setSexAddAge(listDetailViewHolder, convertView, detail.getPosterAge(), detail.getPosterSex());
			// tag id为7表示其他写死判断
			final long tageId = detail.getPostTagId();
			String tagtxt = "";
			if (tageId == 7) {
				tagtxt = "其他(" + detail.getTagName() + detail.getTagTopicNums() + ")";
			} else {
				tagtxt = detail.getTagName() + "(" + detail.getTagTopicNums() + ")";
			}
			listDetailViewHolder.tagTxt.setText(tagtxt);
			listDetailViewHolder.tagTxt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					itemClickListener.flushByTag(Integer.valueOf(String.valueOf(tageId)), detail.getTagName());
				}
			});
			// 设置评论
			// listDetailViewHolder.firstReplyContent.setVisibility(View.GONE);
			// listDetailViewHolder.secondReplyContent.setVisibility(View.GONE);
			// List<EstimateMostReply> replyList =
			// detail.getEstimateMostReplyList();
			// if (replyList != null && replyList.size() > 0) {
			// for (int i = 0; i < replyList.size(); i++) {
			// final EstimateMostReply reply = replyList.get(i);
			// String nickname = reply.getNickname();
			// String content =
			// reply.getPostContent().getElementsList().get(0).getText();
			// if (i == 0) {
			// listDetailViewHolder.firstReplyContent.setVisibility(View.VISIBLE);
			// listDetailViewHolder.firstReplyUserName.setText(nickname);
			// listDetailViewHolder.firstReplyTxt.setText(content);
			// chat(tempListDetailViewHolder.firstReplyUserName,
			// reply.getUid());
			// } else if (i == 1) {
			// listDetailViewHolder.secondReplyContent.setVisibility(View.VISIBLE);
			// listDetailViewHolder.secondReplyUserName.setText(nickname);
			// listDetailViewHolder.secondReplyTxt.setText(content);
			// chat(tempListDetailViewHolder.firstReplyUserName,
			// reply.getUid());
			// } else {
			// break;
			// }
			// }
			// }
			// 赞
			List<TopicEstimateUsers> eusers = detail.getTopicEstimateUsersList();
			if (eusers != null) {
				if (!likeUserMap.containsKey(position)) {
					List<UserVo> users = new ArrayList<UserVo>();
					int length = eusers.size();
					for (int i = 0; i < length; i++) {
						UserVo uvo = new UserVo();
						TopicEstimateUsers euser = eusers.get(i);
						uvo.setUserid(euser.getUid());
						uvo.setAvatar(euser.getAvatar());
						users.add(uvo);
					}
					likeUserMap.put(position, users);
				}
				likeOrUnLike(tempListDetailViewHolder, detail, likeUserMap.get(position));
			}
			if (detail.getHasPraise() > 0) {
				if (likeUserMap.get(position).size() > eusers.size()) {
					tempListDetailViewHolder.likeTxt.setText("已赞" + "(" + (detail.getPraiseNums() + 1) + ")");
					tempListDetailViewHolder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_btn_praised_txt));
					setLikeTxtIcon(tempListDetailViewHolder.likeTxt, R.drawable.game_like_pre2);
				} else {
					tempListDetailViewHolder.likeTxt.setText("已赞" + "(" + detail.getPraiseNums() + ")");
					tempListDetailViewHolder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_btn_praised_txt));
					setLikeTxtIcon(tempListDetailViewHolder.likeTxt, R.drawable.game_like_pre2);
				}
			} else {
				if (likeUserMap.get(position).size() > eusers.size()) {
					tempListDetailViewHolder.likeTxt.setText("已赞" + "(" + (detail.getPraiseNums() + 1) + ")");
					tempListDetailViewHolder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_btn_praised_txt));
					setLikeTxtIcon(tempListDetailViewHolder.likeTxt, R.drawable.game_like_pre2);
				} else {
					tempListDetailViewHolder.likeTxt.setText("赞" + (detail.getPraiseNums() > 0 ? "(" + detail.getPraiseNums() + ")" : ""));
					tempListDetailViewHolder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_btn_txt));
					setLikeTxtIcon(tempListDetailViewHolder.likeTxt, R.drawable.game_like_nor);
				}
			}
			// 私聊天
			if (SystemContext.getInstance().getExtUserVo().getUserid() == detail.getPosterUid()) {
				tempListDetailViewHolder.chatTxt.setVisibility(View.GONE);
			} else {
				tempListDetailViewHolder.chatTxt.setVisibility(View.VISIBLE);
			}
			chat(tempListDetailViewHolder.chatTxt, detail.getPosterUid());
			// 赞的用户页面
			listDetailViewHolder.likeUserCountTxt.setOnClickListener(new OnClickListener() {

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

			listDetailViewHolder.shareTxt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					shareTopic(detail);
				}
			});

			listDetailViewHolder.replyTxt.setText("评论" + (detail.getComments() > 0 ? "(" + detail.getComments() + ")" : ""));
			listDetailViewHolder.replyTxt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TopicDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, detail.getId());
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, detail.getGameid());
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, TopicDetailFragment.MODE_COMMENT);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					context.startActivity(intent);
				}
			});
		}
		return convertView;

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

	public void shareTopic(final Msgs.PostbarTopicDetail topicDetail) {
		if (topicDetail.getIsVisControl()) {
			ToastUtil.showToast(context, "仅楼主层主可见帖子不能被分享哦！");
			return;
		}

		if (FastClickLimitUtil.isFastClick())
			return;
		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);// 类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_POST);// 类型为帖子
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_TOPIC);// 内部分享类型
		shareDate.setTargetId(topicDetail.getId());// 帖子id(目标ID)
		shareDate.setTargetName(topicDetail.getTitle());// 帖子名称（目标名称）
		shareDate.setTempString(topicDetail.getGameid() + "");// 备用字段，传入贴吧名称（暂仅分享帖子时使用）
		// shareDate.setSite("游伴");//QQ空间分享时使用（暂固定写法）
		// shareDate.setSiteUrl("http://www.51uban.com");//QQ空间分享时使用（暂固定写法）
		// 获得图片地址
		String tmpImageSrc = "";
		if (topicDetail.hasPostContent() && topicDetail.getPostContent().getElementsCount() > 0) {
			for (int i = 0; i < topicDetail.getPostContent().getElementsCount(); i++) {
				if (topicDetail.getPostContent().getElements(i).getType() == PostElementType.PE_IMAGE_ID_REF) {
					tmpImageSrc = topicDetail.getPostContent().getElements(i).getResourceId();
					break;
				}
			}
		}
		if (!tmpImageSrc.isEmpty()) {
			shareDate.setImageUrl(ResUtil.getSmallRelUrl(tmpImageSrc));
			shareDate.setImagePath(tmpImageSrc);
		} else {
			shareDate.setImageUrl(ResUtil.getSmallRelUrl("i_youban"));
			shareDate.setImagePath("i_youban");
		}

		ShareCallbackListener listener = new ShareCallbackListener() {

			@Override
			public void doSuccess(String plamType) {
				// 分享帖子信息
				ShareTaskUtil.makeShareTask(context, TAG, topicDetail.getId(), MsgsConstants.OT_TOPIC, MsgsConstants.OP_RECORD_SHARE, plamType, null,
						this.shortUrl);
			}

			@Override
			public void doFail() {
			}

		};
		ShareManager.getInstance().share(context, inflater, topicDetail, shareDate, listener);
	}

	/**
	 * 
	 * @param tv
	 * @param detail
	 * @param op
	 */
	private void likeOrUnLike(final ListDetailViewHolder holder, final Msgs.PostbarTopicDetail detail, final List<UserVo> users) {
		final TextView tv = holder.likeTxt;
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int op = MsgsConstants.OP_PRAISE;
				if (tv.getText().toString().contains("已赞")) {
					op = MsgsConstants.OP_PRAISE_CANCEL;
				} else {
					op = MsgsConstants.OP_PRAISE;
				}
				final int relOp = op;
				tv.setClickable(false);
				final UserVo muser = SystemContext.getInstance().getExtUserVo();
				ProxyFactory.getInstance().getPostbarProxy().likeOrUnLikeTopic(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						if (relOp == MsgsConstants.OP_PRAISE) {
							int count = detail.getPraiseNums();
							if (detail.getHasPraise() == 0) {
								count++;
							}
							tv.setText("已赞" + (count > 0 ? "(" + count + ")" : ""));
							tv.setTextColor(context.getResources().getColor(R.color.topiclist_item_btn_praised_txt));
							setLikeTxtIcon(tv, R.drawable.game_like_pre2);
							if (muser != null) {
								UserVo uvo = new UserVo();
								uvo.setUserid(muser.getUserid());
								uvo.setAvatar(muser.getAvatar());
								users.add(0, uvo);
							}
						} else if (relOp == MsgsConstants.OP_PRAISE_CANCEL) {
							int count = detail.getPraiseNums();
							if (detail.getHasPraise() > 0) {
								count--;
							}
							tv.setText("赞" + (count > 0 ? "(" + count + ")" : ""));
							tv.setTextColor(context.getResources().getColor(R.color.topiclist_item_btn_txt));
							setLikeTxtIcon(tv, R.drawable.game_like_nor);
							for (int i = 0; i < users.size(); i++) {
								UserVo user = users.get(i);
								if (user.getUserid() == muser.getUserid()) {
									users.remove(user);
								}
							}
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
	 * @param imageView
	 * @param images
	 */
	private void browerImages(ImageView imageView, final String[] images, final int index) {
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ImageBrowerActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, index);
				bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, images);
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				context.startActivity(intent);
			}
		});
	}

	/**
	 * 设置赞的用户头像
	 * 
	 * @param listDetailViewHolder
	 * @param userlist
	 */
	private void setLikeUserAvatar(ListDetailViewHolder listDetailViewHolder, Msgs.PostbarTopicDetail detail, List<UserVo> userlist, int addcount) {
		listDetailViewHolder.likeUser1.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser1.setImageResource(R.color.topiclist_item_btn_bg);
		listDetailViewHolder.likeUser2.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser2.setImageResource(R.color.topiclist_item_btn_bg);
		listDetailViewHolder.likeUser3.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser3.setImageResource(R.color.topiclist_item_btn_bg);
		listDetailViewHolder.likeUser4.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser4.setImageResource(R.color.topiclist_item_btn_bg);
		listDetailViewHolder.likeUser5.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser5.setImageResource(R.color.topiclist_item_btn_bg);
		listDetailViewHolder.likeUser6.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser6.setImageResource(R.color.topiclist_item_btn_bg);
		listDetailViewHolder.likeUser7.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser7.setImageResource(R.color.topiclist_item_btn_bg);
		listDetailViewHolder.likeUser8.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser8.setImageResource(R.color.topiclist_item_btn_bg);
		listDetailViewHolder.likeUser9.setVisibility(View.INVISIBLE);
		listDetailViewHolder.likeUser9.setImageResource(R.color.topiclist_item_btn_bg);
		if (userlist != null && userlist.size() > 0) {
			listDetailViewHolder.likeUsersContent.setVisibility(View.VISIBLE);
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
				listDetailViewHolder.likeUserCountTxt.setText("9999+");
			} else {
				listDetailViewHolder.likeUserCountTxt.setText(pcount + "");
			}
			for (int i = 0; i < size; i++) {
				UserVo uvo = userlist.get(i);
				if (i == 0) {
					listDetailViewHolder.likeUser1.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser1, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser1, uvo);
				}
				if (i == 1) {
					listDetailViewHolder.likeUser2.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser2, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser2, uvo);
				}
				if (i == 2) {
					listDetailViewHolder.likeUser3.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser3, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser3, uvo);
				}
				if (i == 3) {
					listDetailViewHolder.likeUser4.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser4, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser4, uvo);
				}
				if (i == 4) {
					listDetailViewHolder.likeUser5.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser5, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser5, uvo);
				}
				if (i == 5) {
					listDetailViewHolder.likeUser6.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser6, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser6, uvo);
				}
				if (i == 6) {
					listDetailViewHolder.likeUser7.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser7, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser7, uvo);
				}
				if (i == 7) {
					listDetailViewHolder.likeUser8.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser8, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser8, uvo);
				}
				if (i == 8) {
					listDetailViewHolder.likeUser9.setVisibility(View.VISIBLE);
					ImageViewUtil.showImage(listDetailViewHolder.likeUser9, uvo.getAvatar(), R.drawable.common_user_icon_default);
					jumpUserDetailView(listDetailViewHolder.likeUser9, uvo);
				}
				if (i > 8)
					break;
			}
		} else {
			listDetailViewHolder.likeUsersContent.setVisibility(View.GONE);
		}
	}

	/**
	 * 打开对方资料
	 * 
	 * @param uvo
	 */
	private void jumpUserDetailView(ImageView view, final UserVo uvo) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (uvo.getUserid() != loginUserVo.getUserid()) {
					Intent intent = new Intent(context, UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uvo.getUserid());
					intent.putExtras(bundle);
					context.startActivity(intent);
				} else {
					ToastUtil.showToast(context, "查看自己的信息去我的页面");
				}
			}
		});
	}

	/**
	 * 设置用户的年龄和性别的方法
	 */
	private void setSexAddAge(ListDetailViewHolder holder, View convertView, int age, int sex) {
		if (age > 0) {
			holder.age.setText(AgeUtil.convertAgeByBirth(age) + "");
			holder.age.setVisibility(View.VISIBLE);

			if (sex == 0) {
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
			} else if (sex == 1) {
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
				holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
			} else {
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
			}
		} else {
			holder.age.setText("");
			holder.age.setCompoundDrawablePadding(0);
			if (sex == 0) {
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				holder.age.setVisibility(View.VISIBLE);
			} else if (sex == 1) {
				holder.age.setVisibility(View.VISIBLE);
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
			} else {
				holder.age.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 添加或取消关注
	 * 
	 * @param gids
	 * @param follow
	 */
	private void followActionGame(final HeaderGameDetailViewHolder detailViewHolder, final GameData gameData, final long gid, final int op) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (context != null) {
					switch (result) {
					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
						if (MsgsConstants.OP_FOLLOW == op) {
							gameData.setRelation(1);
							detailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_game_pre);
							detailViewHolder.btn_follow.setVisibility(View.GONE);
							ToastUtil.showToast(context, context.getString(R.string.game_add_follow_success));

							HashMap<String, String> ummap = new HashMap<String, String>();
							ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(loginUserVo.getUserid()));
							ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, loginUserVo.getUsername());
							ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(gid));
							GameVo gvo = DaoFactory.getDaoFactory().getGameDao(detailViewHolder.btn_follow.getContext()).getGameById(gid);
							if (gvo != null)
								ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, gvo.getGamename());
							MobclickAgent.onEvent(context, UMConfig.MSGS_EVENT_GAME_FOLLOW, ummap);

							// 刚关注贴吧时，需要获得贴吧最大index 设置成已经读到的最大数
							// setReadMaxIndex(context, gid);
							itemClickListener.followGameStatus(true);
						} else if (MsgsConstants.OP_UNFOLLOW == op) {
							gameData.setRelation(0);
							detailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_game_nor);
							ToastUtil.showToast(context, context.getString(R.string.game_cannel_follow_success));
							itemClickListener.followGameStatus(false);
						}
						ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, null);
						break;
					default:
						break;
					}
					detailViewHolder.btn_follow.setEnabled(true);
					dialog.dismiss();
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

				dialog.dismiss();
				if (context != null) {
					if (result == Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE) {
						int mfcount = SystemContext.getInstance().getFGM();
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_FOLLOWGAME_OVER_COUNT, null, mfcount);
						GameUtil.redressGameRel(mfcount);
					} else {
						ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
					}
				}
			}
		}, context, gid, MsgsConstants.OT_GAME, op, null, null, null);
	}

	/**
	 * 设置帖吧的maxIndex 为read的最大index
	 * 
	 * @param context
	 * @param gid
	 */
	private void setReadMaxIndex(Context context, final long gid) {
		ProxyFactory.getInstance().getGameProxy().getGamePostbarMaxIndex(new ProxyCallBack<List<ExtGameVo>>() {

			@Override
			public void onSuccess(List<ExtGameVo> result) {
				if (result != null && result.size() != 0) {
					for (int j = 0; j < result.size(); j++) {
						ExtGameVo gvo = result.get(j);
						long tmPGid = gvo.getGameid();
						if (tmPGid == gid) {
							SystemContext.getInstance().setUserSharedPreferencesPostbarReadMaxIndex(gid, gvo.getPostbarMaxIndex());
						}
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}

		}, null, gid + "");
	}

	/**
	 * 获取贴吧关注信息
	 * 
	 * @param gid
	 */
	private void getGameRel(final HeaderGameDetailViewHolder detailViewHolder, final GameData gameData) {
		ProxyFactory.getInstance().getGameProxy().getRelGameInfo(new ProxyCallBack<RelationGameVo>() {

			@Override
			public void onSuccess(RelationGameVo result) {
				if (context != null) {
					if (result != null) {
						if (result.getRelation() == 1) {
							gameData.setRelation(result.getRelation());
							detailViewHolder.btn_follow.setVisibility(View.GONE);
							detailViewHolder.btn_follow.setEnabled(true);
							detailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_game_pre);
							itemClickListener.followGameStatus(true);
						} else {
							detailViewHolder.btn_follow.setVisibility(View.VISIBLE);
							detailViewHolder.btn_follow.setEnabled(true);
							detailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_game_nor);
						}
					} else {
						detailViewHolder.btn_follow.setVisibility(View.VISIBLE);
						detailViewHolder.btn_follow.setEnabled(true);
						detailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_game_nor);
						detailViewHolder.btn_follow.setTextColor(context.getResources().getColor(R.color.global_color18));
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (context != null) {
					detailViewHolder.btn_follow.setEnabled(true);
					detailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_game_nor);
					detailViewHolder.btn_follow.setTextColor(context.getResources().getColor(R.color.global_color18));
				}
			}
		}, null, gameData.getGid());
	}

	/**
	 * 绑定手机提示框
	 * 
	 * @param actionName
	 */
	private void createBundPhoneDialog() {
		Intent intent = new Intent(context, BundPhoneActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 
	 * @ClassName: HeaderGameDetailViewHolder
	 * @Description: 头部贴吧详情布局
	 * @date 2014-11-17 下午6:30:06
	 * @Version 1.0
	 * 
	 */
	private class HeaderGameDetailViewHolder {
		// 游戏图标
		RoundedImageView icon;
		// 贴吧名
		TextView gameName;
		// 游伴数
		TextView user_num;
		// 公会数
		TextView group_num;
		// 攻略数
		TextView raiders_num;
		// 新闻数
		TextView news_num;
		// 关注按钮
		Button btn_follow;
		// share
		ImageView shareBtn;

		LinearLayout user_num_view;

		LinearLayout group_num_view;

		LinearLayout news_num_view;

		LinearLayout raiders_num_view;
	}

	class ViewHolder {
		ImageView postbar_topic_list_item_isnotice;
		ImageView postbar_topic_list_item_isessence;
		ImageView postbar_topic_list_item_istop;
		ImageView postbar_topic_list_item_ishot;
		ImageView postbar_topic_list_item_isnews;
		ImageView postbar_topic_list_item_isji;
		ImageView postbar_topic_list_item_islock;
		TextView postbar_topic_list_item_title;
		TextView postbar_topic_list_item_nickname;
		TextView postbar_topic_list_item_date;
		ImageView postbar_topic_list_item_ishavepic;
		TextView postbar_topic_list_item_comments;
		TextView postbar_topic_list_item_visits;
		ImageView postbar_topic_list_item_del;
		TextView postbar_topic_list_item_loc;
		View tmp_fg_1;// 根据情况需要隐藏
		View tmp_fg_2;// 根据情况需要隐藏
		View tmp_fg_3;// 根据情况需要隐藏
		LinearLayout postbar_topic_list_item_showorhide_ll;

		View postbar_topic_list_item_title_gap;// 标题前的gap

		ImageView postbar_topic_list_item_master;
	}

	class ListDetailViewHolder {
		ImageView icon;
		TextView nickname;
		ImageView master;
		TextView age;
		TextView userDesc;
		LinearLayout titleContent;
		ImageView postbar_topic_list_item_islock;
		ImageView postbar_topic_list_item_istop;
		ImageView postbar_topic_list_item_isgift;
		ImageView postbar_topic_list_item_isnotice;
		ImageView postbar_topic_list_item_isessence;
		ImageView postbar_topic_list_item_isji;
		ImageView postbar_topic_list_item_ishot;
		ImageView postbar_topic_list_item_isnews;
		ImageView postbar_topic_list_item_ishavepic;
		View postbar_topic_list_item_title_gap;
		TextView postbarTitle;
		MyTextView postbarContentTxt;
		LinearLayout images;
		ImageView imageFirst;
		ImageView imageSecond;
		ImageView imageThird;
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
		LinearLayout firstReplyContent;
		TextView firstReplyUserName;
		TextView firstReplyTxt;
		LinearLayout secondReplyContent;
		TextView secondReplyUserName;
		TextView secondReplyTxt;
		TextView allCommentTxt;
		TextView imageNum;
	}

	class NodateViewHolder {
		LinearLayout bg_layout;
		ImageView bgIcon;
	}

	private void delFavorite(int position) {
		if (itemClickListener != null) {
			itemClickListener.onClickAction(position, ACTION_DEL);
		}
	}

	/**
	 * 获取所有标签信息
	 * 
	 * @param detailViewHolder
	 * @param gameData
	 */
	public void getTopicTags(final HeaderGameDetailViewHolder detailViewHolder, final GameData gameData) {
		// 获得tag
		ProxyCallBack<List<TopicTagVo>> callback = new ProxyCallBack<List<TopicTagVo>>() {

			@Override
			public void onSuccess(List<TopicTagVo> result) {
				if (result != null) {
					List<TopicTagVo> tmptags = new ArrayList<TopicTagVo>();
					for (int i = 0; i < result.size(); i++) {
						if ((result.get(i).getAccess() & 0x02) == 0x02) {
							tmptags.add(result.get(i));
						}
					}
					((GameTopicListActivity) context).setTags(tmptags);

					List<TopicTagVo> temp = new ArrayList<TopicTagVo>();
					TopicTagVo tagAllVo = new TopicTagVo();
					tagAllVo.setName("全部");
					tagAllVo.setId(SystemConfig.POSTBAR_TOPIC_TAG_ALL);
					temp.add(tagAllVo);
					TopicTagVo tagEssenceVo = new TopicTagVo();
					tagEssenceVo.setName("精华");
					tagEssenceVo.setId(SystemConfig.POSTBAR_TOPIC_TAG_ESSENCE);
					temp.add(tagEssenceVo);
					for (int i = 0; i < result.size(); i++) {
						if ((result.get(i).getAccess() & 0x01) == 0x01) {
							temp.add(result.get(i));
						}
					}
					gameData.setTags(temp);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}
		};
		ProxyFactory.getInstance().getPostbarProxy().getTopicTags(callback, gameData.getGid());
	}

	/**
	 * 把顶部的标签的宽度添加到集合当中 去
	 */
	private void addWidthToPixel() {
		if (pixel != null && pixel.size() != 0)
			return;
		pixel.clear();
		for (int i = 0; i < radioBtnList.size(); i++) {
			if (i == 0) {
				pixel.add(0);
			} else {
				pixel.add(radioBtnList.get(i - 1).getMeasuredWidth() + pixel.get(i - 1));
			}
		}
	}

	/**
	 * 设置无数据提示框高度
	 * 
	 * @param nodataItemHeight
	 */
	public void setNodataItemHeight(int nodataItemHeight) {
		this.nodataItemHeight = nodataItemHeight;
	}

	public int getNodataItemHeight() {
		return nodataItemHeight;
	}

	/**
	 * 外部清空标签栏
	 */
	public void clearRadioBtnList() {
		radioBtnList.clear();
	}

	/**
	 * 设置是否需要重绘标签栏
	 * 
	 * @param isNeedRefreshTags
	 */
	public void sheIsNeedRefreshTags(boolean isNeedRefreshTags) {
		this.isNeedRefreshTags = isNeedRefreshTags;
	}

}
