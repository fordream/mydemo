/**      
 * TopicNewsAdapter.java Create on 2015-3-31     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.module.game.ui.RecommendGameActivity;
import com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailFragment;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostElementType;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.proto.Msgs.TopicEstimateUsers;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.MyTextView;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: TopicNewsAdapter
 * @Description: 贴吧动态
 * @author 王卫
 * @date 2015-3-31 下午3:53:32
 * @Version 1.0
 * 
 */
public class TopicNewsAdapter extends BaseAdapter {

	private final int VIEW_TYPE_COUNT = 2;
	private final int TYPE_FOLLW_GAME = 0;
	private final int TYPE_TOPIC = 1;

	protected static final String TAG = "TopicNewsAdapter";
	private Context context;
	private List<Object> data;
	private LayoutInflater inflater;
	private UserVo loginUserVo = null;

	private Map<Integer, List<UserVo>> likeUserMap;
	// 关注的贴吧
	private List<GameVo> followGameData;
	private boolean isTableLayoutInit = false;
	/** 关注贴吧的最大Index **/
	private HashMap<String, Long> unReadMap = new HashMap<String, Long>();
	private Map<Long, GameVo> gameVo_cache = new HashMap<Long, GameVo>();
	private List<ViewHolder> viewHolders = new ArrayList<ViewHolder>();
	private int followGameCount = 0;
//	private int[] hasLoad = { 0, 0, 0 };
	
	private View fconvertView;
	private FollowGameViewHolder fholder = new FollowGameViewHolder();
	
	private OnTopicNewsListener mOnTopicNewsListener;

	/**
	 * 
	 */
	public TopicNewsAdapter(Context context, List<Object> data, OnTopicNewsListener listener) {
		this.context = context;
		this.data = data;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		loginUserVo = SystemContext.getInstance().getExtUserVo();
		likeUserMap = new HashMap<Integer, List<UserVo>>();
		mOnTopicNewsListener = listener;
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
		Object obj = data.get(position);
		if (obj instanceof List) {
			return TYPE_FOLLW_GAME;
		} else if (obj instanceof PostbarTopicDetail) {
			return TYPE_TOPIC;
		} else {
			return TYPE_FOLLW_GAME;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}
	
	/**
	 * 清除数据
	 */
	public void cleanData(){
		if(unReadMap != null)
			unReadMap.clear();
	}
	
	/**
	 * 清除赞踩数据
	 */
	public void cleanPraiseData(){
		if(likeUserMap != null)
			likeUserMap.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ListDetailViewHolder holder = null;
		if (convertView == null) {
			switch (type) {
			case TYPE_FOLLW_GAME:
				if(fconvertView == null)
					fconvertView = (ViewGroup) inflater.inflate(R.layout.fragment_game_content_header, null, false);
				convertView = fconvertView;
				TableLayout table = (TableLayout) convertView.findViewById(R.id.table);
				fholder.table = table;
				convertView.setTag(fholder);
				break;
			case TYPE_TOPIC:
				holder = new ListDetailViewHolder();
				convertView = this.inflater.inflate(R.layout.postbar_topic_list_detail_item, null);
				holder.tmp_fg_1 = (View) convertView.findViewById(R.id.tmp_fg_1);
				holder.gamename = (TextView) convertView.findViewById(R.id.gamename);
				holder.icon = (ImageView) convertView.findViewById(R.id.icon);
				holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holder.master = (ImageView) convertView.findViewById(R.id.master);
				holder.age = (TextView) convertView.findViewById(R.id.age);
				holder.userDesc = (TextView) convertView.findViewById(R.id.userDesc);
				holder.titleContent = (LinearLayout) convertView.findViewById(R.id.titleContent);
				holder.postbar_topic_list_item_islock = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_islock);
				holder.postbar_topic_list_item_istop = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_istop);
				holder.postbar_topic_list_item_isgift = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isgift);
				holder.postbar_topic_list_item_isnotice = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isnotice);
				holder.postbar_topic_list_item_isessence = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isessence);
				holder.postbar_topic_list_item_isji = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isji);
				holder.postbar_topic_list_item_ishot = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_ishot);
				holder.postbar_topic_list_item_isnews = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_isnews);
				holder.postbar_topic_list_item_ishavepic = (ImageView) convertView.findViewById(R.id.postbar_topic_list_item_ishavepic);
				holder.postbar_topic_list_item_title_gap = (View) convertView.findViewById(R.id.postbar_topic_list_item_title_gap);
				holder.postbarTitle = (TextView) convertView.findViewById(R.id.postbarTitle);
				holder.postbarContentTxt = (MyTextView) convertView.findViewById(R.id.postbarContentTxt);
				holder.images = (LinearLayout) convertView.findViewById(R.id.images);
				holder.imageFirst = (ImageView) convertView.findViewById(R.id.imageFirst);
				holder.imageSecond = (ImageView) convertView.findViewById(R.id.imageSecond);
				holder.imageThird = (ImageView) convertView.findViewById(R.id.imageThird);
				holder.tagTxt = (TextView) convertView.findViewById(R.id.tagTxt);
				holder.likeTxt = (TextView) convertView.findViewById(R.id.likeTxt);
				holder.chatTxt = (TextView) convertView.findViewById(R.id.chatTxt);
				holder.replyTxt = (TextView) convertView.findViewById(R.id.replyTxt);
				holder.shareTxt = (TextView) convertView.findViewById(R.id.shareTxt);

				holder.firstReplyContent = (LinearLayout) convertView.findViewById(R.id.firstReplyContent);
				holder.firstReplyUserName = (TextView) convertView.findViewById(R.id.firstReplyUserName);
				holder.firstReplyTxt = (TextView) convertView.findViewById(R.id.firstReplyTxt);
				holder.secondReplyContent = (LinearLayout) convertView.findViewById(R.id.secondReplyContent);
				holder.secondReplyUserName = (TextView) convertView.findViewById(R.id.secondReplyUserName);
				holder.secondReplyTxt = (TextView) convertView.findViewById(R.id.firstReplyTxt);
				holder.allCommentTxt = (TextView) convertView.findViewById(R.id.allCommentTxt);
				holder.imageNum = (TextView) convertView.findViewById(R.id.imageNum);
				convertView.setTag(holder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_FOLLW_GAME:
				fholder = (FollowGameViewHolder)convertView.getTag();
				break;
			case TYPE_TOPIC:
				holder = (ListDetailViewHolder) convertView.getTag();
				break;
			}
		}
		if (type == TYPE_FOLLW_GAME) {
			followGameData = (List<GameVo>) data.get(position);
			setFollowGamesTableLayout(convertView, fholder, followGameData, true);
		} else if (type == TYPE_TOPIC) {
			setTopicDetail(convertView, holder, (PostbarTopicDetail) data.get(position), position);
		}
		return convertView;
	}
		
	private void setTopicDetail(View convertView, ListDetailViewHolder holder, final PostbarTopicDetail detail, int position){
		//设置吧主不显示
		holder.master.setVisibility(View.INVISIBLE);
		//设置贴吧标签显示及功能
		holder.gamename.setVisibility(View.VISIBLE);
		holder.gamename.setText(detail.getPostbarName()+"吧");
		if(position == 1)
			holder.tmp_fg_1.setVisibility(View.GONE);
		else
			holder.tmp_fg_1.setVisibility(View.VISIBLE);
		// 标签显示规则， 有置顶的，显示2个,后面的显示顺序 礼包，公告，精华，集合，热，新 //其他，全部显示
		boolean isShowfullTag = false;
		// 是否显示了tag的图片
		boolean isShowtagImage = false;
		// 是否显示了图片的image
		boolean isShowImageImage = false;

		// 下面需要换算，显示那些tag
		// 是否锁帖
		if (detail.getIsLock()) {
			holder.postbar_topic_list_item_islock.setVisibility(View.VISIBLE);
			isShowtagImage = true;
		} else {
			holder.postbar_topic_list_item_islock.setVisibility(View.GONE);
		}
		// 是否置顶
		if (detail.getIsTop()) {
			holder.postbar_topic_list_item_istop.setVisibility(View.VISIBLE);
			isShowtagImage = true;
		} else {
			holder.postbar_topic_list_item_istop.setVisibility(View.GONE);
		}
		// 是否公告
		if (detail.getIsNotice() && !isShowfullTag) {
			holder.postbar_topic_list_item_isnotice.setVisibility(View.VISIBLE);
			isShowtagImage = true;
			if (detail.getIsTop())
				isShowfullTag = true;
		} else {
			holder.postbar_topic_list_item_isnotice.setVisibility(View.GONE);
		}

		// 是否礼包（暂时未加判断）
		// 是否热帖
		if (detail.getIsHot() && !isShowfullTag) {
			isShowtagImage = true;
			holder.postbar_topic_list_item_ishot.setVisibility(View.VISIBLE);
			if (detail.getIsTop())
				isShowfullTag = true;
		} else {
			holder.postbar_topic_list_item_ishot.setVisibility(View.GONE);
		}
		// 是否精华
		if (detail.getIsEssence() && !isShowfullTag) {
			isShowtagImage = true;
			holder.postbar_topic_list_item_isessence.setVisibility(View.VISIBLE);
			if (detail.getIsTop())
				isShowfullTag = true;
		} else {
			holder.postbar_topic_list_item_isessence.setVisibility(View.GONE);
		}
		// 是否集合贴
		if (detail.getIsTopicSet() && !isShowfullTag) {
			isShowtagImage = true;
			holder.postbar_topic_list_item_isji.setVisibility(View.VISIBLE);
			if (detail.getIsTop())
				isShowfullTag = true;
		} else {
			holder.postbar_topic_list_item_isji.setVisibility(View.GONE);
		}
		// 是否新帖
		if ((detail.getCreateTime() + SystemContext.getInstance().getPTID() > SystemContext.getInstance().getCurrentTimeInMillis())
				&& !isShowfullTag) {
			isShowtagImage = true;
			holder.postbar_topic_list_item_isnews.setVisibility(View.VISIBLE);
			if (detail.getIsTop())
				isShowfullTag = true;
		} else {
			holder.postbar_topic_list_item_isnews.setVisibility(View.GONE);
		}

		// 1.是否有图片 2.显示内容文字，如果由内容显示第一条目的内容
		holder.postbarContentTxt.setVisibility(View.GONE);
		holder.postbarContentTxt.setText("");
		holder.images.setVisibility(View.GONE);
		holder.imageNum.setVisibility(View.GONE);
		holder.imageFirst.setVisibility(View.INVISIBLE);
		holder.imageSecond.setVisibility(View.INVISIBLE);
		holder.imageThird.setVisibility(View.INVISIBLE);
		if (detail.hasPostContent() && detail.getPostContent().getElementsCount() > 0) {
			List<Msgs.PostElement> data = detail.getPostContent().getElementsList();
			final Msgs.PostElement firstPE = data.get(0);
			String ctxt = firstPE.getText();
			if (ctxt != null && !"".equals(ctxt)) {
				holder.postbarContentTxt.setVisibility(View.VISIBLE);
				holder.postbarContentTxt.setText(SmileyUtil.ReplaceSmiley(context, ctxt,
						context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width), context.getResources()
								.getDimensionPixelSize(R.dimen.global_string_smiley_heigth)));
			} else {
				holder.postbarContentTxt.setVisibility(View.GONE);
				holder.postbarContentTxt.setText("");
			}
			int count = 0;
			List<String> images = new ArrayList<String>();
			for (int i = 0; i < data.size(); i++) {
				final Msgs.PostElement element = data.get(i);
				if (element.getType() == PostElementType.PE_IMAGE_ID_REF && element.getHeight() != 30 && element.getWidth() != 30) {
					isShowImageImage = true;
					images.add(ResUtil.getOriginalRelUrl(element.getResourceId()));
					count++;
					holder.images.setVisibility(View.VISIBLE);
					if (count == 1) {
						holder.imageFirst.setVisibility(View.VISIBLE);
						ImageViewUtil.showImage(holder.imageFirst, element.getResourceId(), R.drawable.postbar_thumbimg_default);
					}
					if (count == 2) {
						holder.imageSecond.setVisibility(View.VISIBLE);
						ImageViewUtil.showImage(holder.imageSecond, element.getResourceId(), R.drawable.postbar_thumbimg_default);
					}
					if (count == 3) {
						holder.imageThird.setVisibility(View.VISIBLE);
						ImageViewUtil.showImage(holder.imageThird, element.getResourceId(), R.drawable.postbar_thumbimg_default);
					}
				}
			}
			if (images.size() > 3) {
				holder.imageNum.setVisibility(View.VISIBLE);
				holder.imageNum.setText(images.size() + "");
			}
			String[] tempt = images.toArray(new String[images.size()]);
			browerImages(holder.imageFirst, tempt, 0);
			browerImages(holder.imageSecond, tempt, 1);
			browerImages(holder.imageThird, tempt, 2);
		}

		if (isShowImageImage) {
			holder.postbar_topic_list_item_ishavepic.setVisibility(View.VISIBLE);
		} else {
			holder.postbar_topic_list_item_ishavepic.setVisibility(View.GONE);
		}
		// 标题
		if (!isShowtagImage && !isShowImageImage)
			holder.postbar_topic_list_item_title_gap.setVisibility(View.GONE);
		else
			holder.postbar_topic_list_item_title_gap.setVisibility(View.VISIBLE);
		holder.postbarTitle.setText(detail.getTitle());

		// 发布者
		holder.nickname.setText(detail.getPosterNickname());

		// 发布时间
		String tmploc = DistanceUtil.covertDistance(detail.getPosition());

		String desc = "";
		String time = SafeUtils.getDate2MyStr2(detail.getCreateTime());
		if (!tmploc.isEmpty()) {
			desc = time + " | " + tmploc;
		} else {
			desc = time;
		}
		holder.userDesc.setText(desc);

		// 发布者头像
		ImageViewUtil.showImage(holder.icon, detail.getPosterAvatar(), R.drawable.common_user_icon_default);
		// 设置头像单击
		holder.icon.setOnClickListener(new OnClickListener() {

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
		setSexAddAge(holder, convertView, detail.getPosterAge(), detail.getPosterSex());
		// tag id为7表示其他写死判断
		final long tageId = detail.getPostTagId();
		holder.tagTxt.setVisibility(View.GONE);
		
		String tagtxt = "";
		if (tageId == 7) {
			tagtxt = "其他(" + detail.getTagName() + detail.getTagTopicNums() + ")";
		} else {
			tagtxt = detail.getTagName() + "(" + detail.getTagTopicNums() + ")";
		}
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
			likeOrUnLike(holder, detail, likeUserMap.get(position));
		}
		if (detail.getHasPraise() > 0) {
			if (likeUserMap.get(position).size() > eusers.size()) {
				holder.likeTxt.setText("已赞" + "(" + (detail.getPraiseNums() + 1) + ")");
				holder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_link_txt));
				setLikeTxtIcon(holder.likeTxt, R.drawable.game_like_pre2);
			} else {
				holder.likeTxt.setText("已赞" + "(" + detail.getPraiseNums() + ")");
				holder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_link_txt));
				setLikeTxtIcon(holder.likeTxt, R.drawable.game_like_pre2);
			}
		} else {
			if (likeUserMap.get(position).size() > eusers.size()) {
				holder.likeTxt.setText("已赞" + "(" + (detail.getPraiseNums() + 1) + ")");
				holder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_link_txt));
				setLikeTxtIcon(holder.likeTxt, R.drawable.game_like_pre2);
			} else {
				holder.likeTxt.setText("赞" + (detail.getPraiseNums() > 0 ? "(" + detail.getPraiseNums() + ")" : ""));
				holder.likeTxt.setTextColor(context.getResources().getColor(R.color.topiclist_item_btn_txt));
				setLikeTxtIcon(holder.likeTxt, R.drawable.game_like_nor);
			}
		}
		// 私聊天
		if (SystemContext.getInstance().getExtUserVo().getUserid() == detail.getPosterUid()) {
			holder.chatTxt.setVisibility(View.GONE);
		} else {
			holder.chatTxt.setVisibility(View.VISIBLE);
		}
		chat(holder.chatTxt, detail.getPosterUid());

		holder.replyTxt.setText("评论" + (detail.getComments() > 0 ? "(" + detail.getComments() + ")" : ""));
		holder.replyTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, TopicDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, detail.getId());
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, detail.getGameid());
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, TopicDetailFragment.MODE_COMMENT);
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POSTBAR_DETAIL_MODE, TopicReplyListAdapter.MODE_POSTBAR_SHOW);
				bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, detail.getPostbarName());
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				context.startActivity(intent);
			}
		});
	}
	
	/**
	 * 关注贴吧的布局 循环绘制，每行绘制四个
	 */
	private void setFollowGamesTableLayout(View convertView, FollowGameViewHolder holder, List<GameVo> followGameData, boolean isNotFromLocal) {
		if (followGameData == null)
			return;
		int dataSize = followGameData.size();
		int numColumns = 4;
		int numRows = dataSize % numColumns == 0 ? dataSize / numColumns : (dataSize / numColumns + 1);
		int i = 0;
		if (dataSize != unReadMap.size() || followGameDataHasChange(followGameData) || !isTableLayoutInit) {// 数量不等，关注有变化，第一次进来
			holder.table.removeAllViews();
			viewHolders.clear();
			followGameCount = 0;
			for (i = 0; i < numRows; i++) {
				View rowView = this.inflater.inflate(R.layout.game_followgame_row, null);
				View item1 = rowView.findViewById(R.id.game_followgame_row_item_1);
				View item2 = rowView.findViewById(R.id.game_followgame_row_item_2);
				View item3 = rowView.findViewById(R.id.game_followgame_row_item_3);
				View item4 = rowView.findViewById(R.id.game_followgame_row_item_4);
				for (int j = i * numColumns; j < (i + 1) * numColumns && j < dataSize; j++) {
					final GameVo gameVo = followGameData.get(j);
					if (j % numColumns == 0) {
						addViewHolder(item1, gameVo.getGameid());
					} else if (j % numColumns == 1) {
						addViewHolder(item2, gameVo.getGameid());
					} else if (j % numColumns == 2) {
						addViewHolder(item3, gameVo.getGameid());
					} else if (j % numColumns == 3) {
						addViewHolder(item4, gameVo.getGameid());
					}
				}
				holder.table.addView(rowView);
			}
			setFollowGameItemsData(followGameData, isNotFromLocal);

			if (isNotFromLocal)
				isTableLayoutInit = true;
		} else {
			reShowTopicLogo();
			getTopicUnReadCounts(followGameData, true);
		}
		if (isNotFromLocal)
			getTopicUnReadCounts(followGameData, false);
	}
	
	/**
	 * 获取某个游戏的未读数
	 * 
	 * @param gid
	 * @param holder
	 */
	private void getTopicUnReadCount(String gid, final ViewHolder holder) {
		ProxyFactory.getInstance().getGameProxy().getGamePostbarMaxIndex(new ProxyCallBack<List<ExtGameVo>>() {

			@Override
			public void onSuccess(List<ExtGameVo> result) {
				if (result != null && result.size() != 0) {
					ExtGameVo gvo = result.get(0);
					if (gvo != null) {
						long gid = gvo.getGameid();
						long unreadCount = gvo.getPostbarMaxIndex();
						unReadMap.put(gid + "", unreadCount);
						if (unreadCount < 0) {
							unreadCount = 0;
						}
						setTopicUnReadCount(holder.topiccount, unreadCount);
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}
		}, null, gid);
	}
	
	/**
	 * 请求所有关注帖吧的最大index isNeedShowUnreadCount //判断是否需要显示未读数
	 * 
	 * @return
	 */
	private void getTopicUnReadCounts(List<GameVo> followGameData, final boolean isNeedShowUnreadCount) {
		String gids = "";
		for (int i = 0; i < followGameData.size(); i++) {
			long gid = followGameData.get(i).getGameid();
			if (i != followGameData.size() - 1) {
				gids = gids + gid + ",";
			} else {
				gids = gids + gid;
			}
		}

		ProxyFactory.getInstance().getGameProxy().getGamePostbarMaxIndex(new ProxyCallBack<List<ExtGameVo>>() {

			@Override
			public void onSuccess(List<ExtGameVo> result) {
				unReadMap.clear();
				if (result != null && result.size() != 0) {
					for (int j = 0; j < result.size(); j++) {
						ExtGameVo gvo = result.get(j);
						long gid = gvo.getGameid();
						long postbarMaxIndex = gvo.getPostbarMaxIndex();
						unReadMap.put(gid + "", postbarMaxIndex);
					}
				}
				// 判断是否需要显示未读数
				if (isNeedShowUnreadCount) {
					showUnReadIndex();
				}
				mOnTopicNewsListener.unreadLoadCompleted();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
//				loading_layout.setVisibility(View.GONE);
			}
		}, null, gids);
	}
	
	/**
	 * 显示未读数
	 */
	private void showUnReadIndex() {
		if (unReadMap.size() == 0) {
			return;
		}
		for (int i = 0; i < viewHolders.size(); i++) {
			ViewHolder holder = viewHolders.get(i);
			if (holder != null && holder.name != null && holder.id != null) {
				String gameId = holder.id.getText().toString();
				if (unReadMap.containsKey(gameId)) {
					long unreadCount = unReadMap.get(gameId);
					if (unreadCount < 0) {
						unreadCount = 0;
					}
					setTopicUnReadCount(holder.topiccount, unreadCount);
				} else {
					getTopicUnReadCount(gameId, holder);
				}
			}
		}
	}
	
	/**
	 * 设置帖子未读数
	 * 
	 * @param tv
	 * @param gid
	 */
	private void setTopicUnReadCount(final TextView tv, final long unReadCount) {
		if (unReadCount <= 0) {
			tv.setVisibility(View.INVISIBLE);
		} else if (unReadCount < 100) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(unReadCount + "");
		} else {
			tv.setVisibility(View.VISIBLE);
			tv.setText("...");
		}
	}
	
	/**
	 * 切换页面时重新渲染logo，防止第一次没显示logo，以后都不显示
	 */
	private void reShowTopicLogo() {
		for (int i = 0; i < viewHolders.size(); i++) {
			ViewHolder holder = viewHolders.get(i);
			if (holder != null && holder.icon != null && holder.id != null) {
				String gameId = holder.id.getText().toString();
				if (Long.valueOf(gameId) == -1) {
					holder.icon.setTag("drawable://" + R.drawable.game_add_btn_nor);
					holder.icon.setImageResource(R.drawable.game_add_btn_nor);

					holder.name.setVisibility(View.GONE);
					holder.topiccount.setVisibility(View.GONE);
				} else {
					if (gameVo_cache.containsKey(Long.valueOf(gameId))) {
						GameVo gameVo = gameVo_cache.get(Long.valueOf(gameId));
						String gameLogo = gameVo.getGamelogo();
						if (gameLogo != null && !gameLogo.isEmpty()) {
							ImageViewUtil.showImage(holder.icon, gameLogo, R.drawable.common_default_icon);
							// ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(gameLogo),
							// holder.icon, R.drawable.common_default_icon,
							// R.drawable.common_default_icon,
							// R.drawable.common_default_icon, null, true);
						} else {
							getAndShowGameLogo(Long.valueOf(gameId), holder);
						}
					} else {
						getAndShowGameLogo(Long.valueOf(gameId), holder);
					}
				}
			}
		}
	}
	
	/**
	 * 获取并显示贴吧logo，当缓存中没有时执行该方法
	 * 
	 * @param gameId
	 * @param holder
	 */
	private void getAndShowGameLogo(final long gameId, final ViewHolder holder) {
		ProxyCallBack<GameVo> callback = new ProxyCallBack<GameVo>() {

			@Override
			public void onSuccess(GameVo result) {
				if (result == null) {
					holder.icon.setTag("drawable://" + R.drawable.common_default_icon);
					holder.icon.setImageResource(R.drawable.common_default_icon);
				} else {
					gameVo_cache.put(result.getGameid(), result);
					ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(gameVo_cache.get(gameId).getGamelogo()), holder.icon,
							R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				holder.icon.setTag("drawable://" + R.drawable.common_default_icon);
				holder.icon.setImageResource(R.drawable.common_default_icon);
				// 出现错误
				ToastUtil.showToast(context, "获得贴吧[gid:" + gameId + "]异常：" + result);
			}
		};
		ProxyFactory.getInstance().getGameProxy().getGameInfo(callback, context, gameId);
	}
	
	/**
	 * 设置我关注贴吧的内容
	 * 
	 * @param dataSize
	 */
	private void setFollowGameItemsData(List<GameVo> followGameData, final boolean isNotFromLocal) {
		int dataSize = followGameData.size();
		int requestCount = 20;
		int requestNums = dataSize % requestCount == 0 ? dataSize / requestCount : (dataSize / requestCount + 1);
		for (int i = 0; i < requestNums; i++) {// 每一批次请求
			// 单次需要获取的数据，数据来源可能从缓存，也可能时服务端请求的数据
			final Map<Long, GameVo> gameVo_request = new HashMap<Long, GameVo>();
			final ArrayList<Long> requestGids = new ArrayList<Long>();

			// 循环判断，是否缓存中已存在
			for (int j = i * requestCount; j < (i + 1) * requestCount && j < dataSize; j++) {// 单个贴吧进行过滤
				final GameVo gameVo = followGameData.get(j);
				long gameId = gameVo.getGameid();
				if (!gameVo_cache.containsKey(gameId)) {// 缓存里没有，需请求数据
					if (gameId == -1) {// 添加贴吧按钮
						gameVo_request.put(gameId, gameVo);
					} else {
						requestGids.add(gameId);
					}
				} else {// 缓存里有，直接拿过来
					gameVo_request.put(gameId, gameVo_cache.get(gameId));
				}
			}

			if (gameVo_request.size() != 0) {
				showGameDetail(gameVo_request, isNotFromLocal);
			}

			// 请求数据并进行UI绘制
			if (requestGids.size() != 0) {// 需要请求数据
				final Map<Long, GameVo> gameVoExits = new HashMap<Long, GameVo>();
				ProxyCallBack<List<GameVo>> callback = new ProxyCallBack<List<GameVo>>() {

					@Override
					public void onSuccess(List<GameVo> result) {
						if (result != null) {// 成功
							for (int k = 0; k < result.size(); k++) {
								GameVo vo = result.get(k);
								if (vo != null) {
									long gameId = vo.getGameid();
									gameVo_cache.put(gameId, vo);
									gameVoExits.put(gameId, vo);
								}
							}
						} else {// 失败
							for (int m = 0; m < requestGids.size(); m++) {
								long id = requestGids.get(m);
								GameVo gameVo = new GameVo();
								gameVo.setGameid(id);
								gameVoExits.put(id, gameVo);
							}
						}
						showGameDetail(gameVoExits, isNotFromLocal);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						for (int m = 0; m < requestGids.size(); m++) {
							long id = requestGids.get(m);
							GameVo gameVo = new GameVo();
							gameVo.setGameid(id);
							gameVoExits.put(id, gameVo);
						}
						showGameDetail(gameVoExits, isNotFromLocal);
					}
				};
				ProxyFactory.getInstance().getGameProxy().getGamesInfo(callback, context, requestGids, isNotFromLocal);
			}
		}
	}
	
	/**
	 * 设置详细信息
	 * 
	 * @param gameVo_request
	 * @param isNotFromLocal
	 */
	private void showGameDetail(Map<Long, GameVo> gameVo_request, final boolean isNotFromLocal) {
		for (int i = 0; i < viewHolders.size(); i++) {
			final ViewHolder viewHolder = viewHolders.get(i);
			if (viewHolder != null && viewHolder.id != null) {
				// String gameName = viewHolder.name.getText().toString();
				// if(gameName != null && !gameName.isEmpty()){
				// continue;
				// }

				long gameId = Long.valueOf(viewHolder.id.getText().toString());
				if (gameId == -1) {
					viewHolder.icon.setTag("drawable://" + R.drawable.game_add_btn_nor);
					viewHolder.icon.setImageResource(R.drawable.game_add_btn_nor);

					viewHolder.name.setVisibility(View.GONE);
					viewHolder.topiccount.setVisibility(View.GONE);
				} else {
					if (gameVo_request.containsKey(gameId)) {
						GameVo gameVo = gameVo_request.get(gameId);

						if (gameVo.getGamename() == null || gameVo.getGamelogo() == null) {
							GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(context);
							GameVo vo = gameDao.getGameByGameId(gameId);
							if (vo != null) {
								gameVo = vo;
							}
						}

						if (gameVo.getGamelogo() != null && gameVo.getGamename() != null) {
							gameVo_cache.put(gameId, gameVo);
							viewHolder.name.setVisibility(View.VISIBLE);
							viewHolder.name.setText(gameVo.getGamename());
							ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(gameVo.getGamelogo()), viewHolder.icon,
									R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
							if (isNotFromLocal)
								showTopicUnRead();
						} else {
							final GameVo tempVo = gameVo;
							ProxyCallBack<GameVo> callback = new ProxyCallBack<GameVo>() {

								@Override
								public void onSuccess(GameVo result2) {
									if (result2 != null) {
										gameVo_cache.put(result2.getGameid(), result2);

										viewHolder.name.setVisibility(View.VISIBLE);
										if (result2.getGamename() != null) {
											viewHolder.name.setText(result2.getGamename());
										} else if (tempVo.getGamename() != null) {
											viewHolder.name.setText(tempVo.getGamename());
										} else {
											viewHolder.name.setText("未能够获得贴吧名");
										}

										if (result2.getGamelogo() != null) {
											ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result2.getGamelogo()), viewHolder.icon,
													R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon,
													null, true);
										} else {
											viewHolder.icon.setTag("drawable://" + R.drawable.common_default_icon);
											viewHolder.icon.setImageResource(R.drawable.common_default_icon);
										}
									} else {
										viewHolder.name.setVisibility(View.VISIBLE);
										if (tempVo.getGamename() != null) {
											viewHolder.name.setText(tempVo.getGamename());
										} else {
											viewHolder.name.setText("未能够获得贴吧名");
										}
										viewHolder.icon.setTag("drawable://" + R.drawable.common_default_icon);
										viewHolder.icon.setImageResource(R.drawable.common_default_icon);
									}

									if (isNotFromLocal)
										showTopicUnRead();
								}

								@Override
								public void onFailure(Integer result2, String resultMsg) {
									viewHolder.name.setVisibility(View.VISIBLE);
									if (tempVo.getGamename() != null) {
										viewHolder.name.setText(tempVo.getGamename());
									} else {
										viewHolder.name.setText("未能够获得贴吧名");
									}
									viewHolder.icon.setTag("drawable://" + R.drawable.common_default_icon);
									viewHolder.icon.setImageResource(R.drawable.common_default_icon);
									if (isNotFromLocal)
										showTopicUnRead();
								}

							};
							ProxyFactory.getInstance().getGameProxy().getGameInfo(callback, context, gameVo.getGameid());
						}
					}
				}
			}
		}
	}
	
	/**
	 * 当全部贴吧信息加载完成后显示贴吧未读数
	 */
	private void showTopicUnRead() {
		followGameCount++;
		if (followGameCount == followGameData.size()) {
			if (unReadMap.size() != 0) {
				showUnReadIndex();
			} else {
				getTopicUnReadCounts(followGameData, true);
			}
		}
	}
	
	/**
	 * 获取每个ITEM的ViewHolder
	 * 
	 * @param view
	 * @param gameId
	 * @return
	 */
	private void addViewHolder(View view, final long gameId) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.icon = (RoundedImageView) view.findViewById(R.id.gamelogo);
		viewHolder.name = (TextView) view.findViewById(R.id.gamename);
		viewHolder.topiccount = (TextView) view.findViewById(R.id.game_topiccount);
		viewHolder.id = (TextView) view.findViewById(R.id.game_id);

		viewHolder.topiccount.setVisibility(View.INVISIBLE);
		viewHolder.id.setText(gameId + "");
		viewHolders.add(viewHolder);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				if (gameId == -1) {
					// 打开增加贴吧的页面
					Intent intent = new Intent(context, RecommendGameActivity.class);
					context.startActivity(intent);
				} else if (gameId > 0) {
					Intent intent = new Intent(context, GameTopicListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gameId);
					bundle.putString("From", TAG);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					context.startActivity(intent);
				}
			}
		});
	}
	
	/*
	 * 判断我关注的帖子又没有产生变化
	 */
	private boolean followGameDataHasChange(List<GameVo> followGameData) {
		for (int i = 0; i < followGameData.size(); i++) {
			GameVo gameVo = followGameData.get(i);
			if (gameVo != null) {
				String gameId = gameVo.getGameid() + "";
				if (!unReadMap.containsKey(gameId)) {
					return true;
				}
			}
		}
		return false;
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
							tv.setTextColor(context.getResources().getColor(R.color.topiclist_item_link_txt));
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

	class FollowGameViewHolder {
		TableLayout table;
	}
	
	class ListDetailViewHolder {
		View tmp_fg_1;
		TextView gamename;
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
		LinearLayout firstReplyContent;
		TextView firstReplyUserName;
		TextView firstReplyTxt;
		LinearLayout secondReplyContent;
		TextView secondReplyUserName;
		TextView secondReplyTxt;
		TextView allCommentTxt;
		TextView imageNum;
	}
	
	private class ViewHolder {
		public TextView id;
		public RoundedImageView icon;
		public TextView topiccount;
		public TextView name;
	}
	
	public interface OnTopicNewsListener{
		
		void unreadLoadCompleted();
		
	}

}
