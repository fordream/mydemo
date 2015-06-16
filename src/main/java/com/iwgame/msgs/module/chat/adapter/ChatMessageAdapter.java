/**      
 * ChatMessageAdapter.java Create on 2013-10-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.adapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iwgame.audio.AudioPlayer;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.MyTagSpan.MyTagClickListener;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.WebBrowserActivity;
import com.iwgame.msgs.common.imageloader.ImageLoader;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.ui.ChatFragment;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.module.game.adapter.CommonGameAdapter;
import com.iwgame.msgs.module.game.ui.GameListActivity;
import com.iwgame.msgs.module.group.adapter.GroupAdapter2;
import com.iwgame.msgs.module.group.ui.GroupDetailActivity;
import com.iwgame.msgs.module.group.ui.GroupGradePolicyActivity;
import com.iwgame.msgs.module.group.ui.GroupListActivity;
import com.iwgame.msgs.module.pay.ui.PayDetailInfoActivity;
import com.iwgame.msgs.module.play.ui.MainPlayListActivity;
import com.iwgame.msgs.module.play.ui.PlayAllCommentInfoActivity;
import com.iwgame.msgs.module.play.ui.PlayDetailInfoActivity;
import com.iwgame.msgs.module.play.ui.PlayManageActivity;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.module.setting.ui.GoodsDetailActivity;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.module.user.ui.PointTaskDetailActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.module.user.ui.UserGradePolicyActivity;
import com.iwgame.msgs.module.user.ui.UserListActicity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.utils.AppUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MessageUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.MyTagUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageExt;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.MyTextView;
import com.iwgame.msgs.widget.URLSpanClickable;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.AudioUtils;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.HttpUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ChatMessageAdapter
 * @Description: 聊天消息适配器
 * @author Administrator
 * @date 2013-10-28 下午1:59:48
 * @Version 1.0
 * 
 */
public class ChatMessageAdapter extends BaseAdapter {

	private final String TAG = "ChatMessageAdapter";
	private Context context;
	private LayoutInflater inflater;
	private List<MessageVo> data;
	private UserVo loginUserVo = null;
	GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
	/**
	 * 定义父页面List所在的页面类型
	 */
	/**
	 * 个人对聊页面
	 */
	public static int PAGE_TYPE_CHAT = 1;
	/**
	 * 公会聊天页面
	 */
	public static int PAGE_TYPE_MCHAT = 2;

	/**
	 * 系统统发送的消息（游伴小助手，系统公告）
	 */
	public static int PAGE_TYPE_SYSTEM = 3;
	/**
	 * 公会群发页面
	 */
	public static int PAGE_TYPE_GROUPMANAGEMASSSEND = 4;
	/**
	 * 展示公会群发消息页
	 */
	public static int PAGE_TYPE_GROUPMASSMSG = 5;

	private int pageType = 1;

	private URLSpanClickable mURLSpanClickable;
	// 布局类型有3中 ,TYPE_OUT 为自己发出，TYPE_IN_USER为用户发进来，为系统发进来TYPE_IN_SYSTEM
	final int VIEW_TYPE = 3;
	final int TYPE_OUT = 0;
	final int TYPE_IN_USER = 1;
	final int TYPE_IN_SYSTEM = 2;

	private Handler handler = new Handler();

	private String currentMediaPlayerDataSource = "";

	private UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
	private GroupUserRelDao grelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());

	/**
	 * 调用对应的回调
	 */
	private Object objectContextListener;

	private Map<Long, UserVo> user_cache = new ConcurrentHashMap<Long, UserVo>();
	private Map<Long, GroupVo> group_cache = new ConcurrentHashMap<Long, GroupVo>();
	private Map<Long, GameVo> game_cache = new ConcurrentHashMap<Long, GameVo>();
	private Map<Long, Msgs.PostbarTopicDetail> topic_cache = new ConcurrentHashMap<Long, Msgs.PostbarTopicDetail>();
	private List<UserVo> list = new ArrayList<UserVo>();
	/**
	 * 延时执行的Runnable，key 用vo，
	 */
	private HashMap<Object, Runnable> animStopRunnables = new HashMap<Object, Runnable>();

	public ChatMessageAdapter(Context context, List<MessageVo> data, URLSpanClickable clickable, int pageType) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.mURLSpanClickable = clickable;
		this.pageType = pageType;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
	}

	public void setOnItemLongClickListener(Object objectContextListener) {
		this.objectContextListener = objectContextListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		MessageVo vo = data.get(position);
		if (vo.getFromId() == loginUserVo.getUserid() && vo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
			if (vo.getContent() != null && vo.getContent().contains(MyTagUtil.TAG_FOLLOW)) {
				return TYPE_IN_SYSTEM;
			} else {
				return TYPE_OUT;
			}
		} else if (vo.getFromId() != loginUserVo.getUserid() && vo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
			if (vo.getContent() != null && vo.getContent().contains(MyTagUtil.TAG_FOLLOW)) {
				return TYPE_IN_SYSTEM;
			} else {
				return TYPE_IN_USER;
			}
		} else if (vo.getFromDomain().equals(MsgsConstants.DOMAIN_PLATFORM)) {
			if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) || vo.getChannelType().endsWith(MsgsConstants.MC_PUB)) {
				return TYPE_IN_USER;
			} else {
				return TYPE_IN_SYSTEM;
			}
		} else {
			return TYPE_IN_USER;
		}
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		final int type = getItemViewType(position);

		UserViewHolder tmp_userViewHolder = null;
		SystemViewHolder tmp_systemViewHolder = null;
		if (convertView == null) {
			// LogUtil.d(TAG,
			// String.format("----getView创建,[%d][convertView = %s]",position,convertView));
			switch (type) {
			case TYPE_OUT:
				convertView = this.inflater.inflate(R.layout.chatmsg_out_item, null);
				tmp_userViewHolder = new UserViewHolder();
				tmp_userViewHolder.item_chat_headimg = (ImageView) convertView.findViewById(R.id.icon);
				tmp_userViewHolder.tv_username = (TextView) convertView.findViewById(R.id.item_chat_username);
				tmp_userViewHolder.tv_date = (TextView) convertView.findViewById(R.id.item_chat_date);
				tmp_userViewHolder.tv_msg_text = (MyTextView) convertView.findViewById(R.id.item_chat_msg_text);
				tmp_userViewHolder.tv_msg_img = (ImageView) convertView.findViewById(R.id.item_chat_msg_img);
				tmp_userViewHolder.item_chat_msg_sendfail = (ImageView) convertView.findViewById(R.id.item_chat_msg_sendfail);
				tmp_userViewHolder.item_chat_msg_sending = (ProgressBar) convertView.findViewById(R.id.item_chat_msg_sending);
				tmp_userViewHolder.item_chat_msg_voice_ll = (LinearLayout) convertView.findViewById(R.id.item_chat_msg_voice_ll);
				tmp_userViewHolder.item_chat_msg_voice_duration = (TextView) convertView.findViewById(R.id.item_chat_msg_voice_duration);
				tmp_userViewHolder.item_chat_msg_voice_anim_tv = (TextView) convertView.findViewById(R.id.item_chat_msg_voice_anim_tv);
				tmp_userViewHolder.item_chat_msg_voice_anim = (TextView) convertView.findViewById(R.id.item_chat_msg_voice_anim);
				tmp_userViewHolder.item_chat_msg_share_ll = (LinearLayout) convertView.findViewById(R.id.item_chat_msg_share_ll);
				tmp_userViewHolder.item_chat_share_img = (ImageView) convertView.findViewById(R.id.item_chat_share_img);
				tmp_userViewHolder.item_chat_share_content = (TextView) convertView.findViewById(R.id.item_chat_share_content);
				tmp_userViewHolder.item_chat_share_bottom = (TextView) convertView.findViewById(R.id.item_chat_share_bottom);
				tmp_userViewHolder.item_chat_share_bottom_icon = (ImageView) convertView.findViewById(R.id.item_chat_share_bottom_icon);
				tmp_userViewHolder.item_chat_msg_voice_ll_downloading = (LinearLayout) convertView
						.findViewById(R.id.item_chat_msg_voice_ll_downloading);
				tmp_userViewHolder.item_chat_msg_voice_ll_downloaderr = (LinearLayout) convertView
						.findViewById(R.id.item_chat_msg_voice_ll_downloaderr);
				// 推荐游戏布局
				tmp_userViewHolder.item_chat_msg_recommend_ll = (LinearLayout) convertView.findViewById(R.id.item_chat_msg_recommend_ll);
				tmp_userViewHolder.item_chat_recommend_msg_text = (MyTextView) convertView.findViewById(R.id.item_chat_recommend_msg_text);
				tmp_userViewHolder.item_chat_recommend_img = (ImageView) convertView.findViewById(R.id.item_chat_recommend_img);
				tmp_userViewHolder.item_chat_recommend_ll_content = (MyTextView) convertView.findViewById(R.id.item_chat_recommend_ll_content);
				convertView.setTag(tmp_userViewHolder);
				break;
			case TYPE_IN_USER:
				convertView = this.inflater.inflate(R.layout.chatmsg_in_user_item, null);
				tmp_userViewHolder = new UserViewHolder();
				tmp_userViewHolder.item_chat_headimg = (ImageView) convertView.findViewById(R.id.icon);
				tmp_userViewHolder.tv_username = (TextView) convertView.findViewById(R.id.item_chat_username);
				tmp_userViewHolder.tv_date = (TextView) convertView.findViewById(R.id.item_chat_date);
				tmp_userViewHolder.tv_msg_text = (MyTextView) convertView.findViewById(R.id.item_chat_msg_text);
				tmp_userViewHolder.tv_msg_img = (ImageView) convertView.findViewById(R.id.item_chat_msg_img);
				tmp_userViewHolder.item_chat_msg_voice_ll = (LinearLayout) convertView.findViewById(R.id.item_chat_msg_voice_ll);
				tmp_userViewHolder.item_chat_msg_voice_duration = (TextView) convertView.findViewById(R.id.item_chat_msg_voice_duration);
				tmp_userViewHolder.item_chat_msg_voice_anim_tv = (TextView) convertView.findViewById(R.id.item_chat_msg_voice_anim_tv);
				tmp_userViewHolder.item_chat_msg_voice_anim = (TextView) convertView.findViewById(R.id.item_chat_msg_voice_anim);
				tmp_userViewHolder.item_chat_msg_share_ll = (LinearLayout) convertView.findViewById(R.id.item_chat_msg_share_ll);
				tmp_userViewHolder.item_chat_share_img = (ImageView) convertView.findViewById(R.id.item_chat_share_img);
				tmp_userViewHolder.item_chat_share_content = (TextView) convertView.findViewById(R.id.item_chat_share_content);
				tmp_userViewHolder.item_chat_share_bottom = (TextView) convertView.findViewById(R.id.item_chat_share_bottom);
				tmp_userViewHolder.item_chat_share_bottom_icon = (ImageView) convertView.findViewById(R.id.item_chat_share_bottom_icon);
				tmp_userViewHolder.item_chat_msg_voice_ll_downloading = (LinearLayout) convertView
						.findViewById(R.id.item_chat_msg_voice_ll_downloading);
				tmp_userViewHolder.item_chat_msg_voice_ll_downloaderr = (LinearLayout) convertView
						.findViewById(R.id.item_chat_msg_voice_ll_downloaderr);

				// 推荐游戏布局
				tmp_userViewHolder.item_chat_msg_recommend_ll = (LinearLayout) convertView.findViewById(R.id.item_chat_msg_recommend_ll);
				tmp_userViewHolder.item_chat_recommend_msg_text = (MyTextView) convertView.findViewById(R.id.item_chat_recommend_msg_text);
				tmp_userViewHolder.item_chat_recommend_img = (ImageView) convertView.findViewById(R.id.item_chat_recommend_img);
				tmp_userViewHolder.item_chat_recommend_ll_content = (MyTextView) convertView.findViewById(R.id.item_chat_recommend_ll_content);

				convertView.setTag(tmp_userViewHolder);

				break;
			case TYPE_IN_SYSTEM:
				convertView = this.inflater.inflate(R.layout.chatmsg_in_system_item, null);
				tmp_systemViewHolder = new SystemViewHolder();
				tmp_systemViewHolder.tv_date = (TextView) convertView.findViewById(R.id.item_chat_date);
				tmp_systemViewHolder.tv_msg_text = (MyTextView) convertView.findViewById(R.id.item_chat_msg_text);
				convertView.setTag(tmp_systemViewHolder);
				break;
			}
		} else {
			// LogUtil.d(TAG,
			// String.format("----getView复用,[%d][convertView = %s]",position,convertView));
			switch (type) {
			case TYPE_OUT:
			case TYPE_IN_USER:
				tmp_userViewHolder = (UserViewHolder) convertView.getTag();
				break;
			case TYPE_IN_SYSTEM:
				tmp_systemViewHolder = (SystemViewHolder) convertView.getTag();
				break;
			}
		}
		final MessageVo vo = data.get(position);

		// LogUtil.d(TAG,
		// String.format("----getView,[%d][convertView = %s][type = %d ][vo = %s]",
		// position, convertView, type,vo.toString()));
		// 判断时间是否需要显示,默认true，需要显示，false不需要显示
		boolean isShowTime = true;
		if (position > 0
				&& SafeUtils.getFormatDate(data.get(position - 1).getCreateTime(), "MM-dd HH:mm").equals(
						SafeUtils.getFormatDate(vo.getCreateTime(), "MM-dd HH:mm"))) {
			isShowTime = false;
		}

		final UserViewHolder userViewHolder = tmp_userViewHolder;
		SystemViewHolder systemViewHolder = tmp_systemViewHolder;
		if (type == TYPE_OUT) {
			if (tmp_userViewHolder.item_chat_msg_sendfail != null) {
				if (vo.getStatus() == MessageVo.STATUS_SENDERR) {
					tmp_userViewHolder.item_chat_msg_sendfail.setVisibility(View.VISIBLE);
				} else {
					tmp_userViewHolder.item_chat_msg_sendfail.setVisibility(View.GONE);
				}
			}
			if (tmp_userViewHolder.item_chat_msg_sending != null) {
				if (vo.getStatus() == MessageVo.STATUS_SENDING) {
					tmp_userViewHolder.item_chat_msg_sending.setVisibility(View.VISIBLE);
				} else {
					tmp_userViewHolder.item_chat_msg_sending.setVisibility(View.GONE);
				}

			}
		}

		if (type == TYPE_OUT || type == TYPE_IN_USER) {

			// 增加判断时间是否需要显示
			if (isShowTime) {
				userViewHolder.tv_date.setVisibility(View.VISIBLE);
				userViewHolder.tv_date.setText(SafeUtils.getFormatDate(vo.getCreateTime(), "MM-dd HH:mm"));
			} else {
				userViewHolder.tv_date.setVisibility(View.GONE);
			}

			if (MsgsConstants.DOMAIN_PLATFORM.equals(vo.getFromDomain())) {
				if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY)) {
					if (MsgsConstants.MCC_ACTIVITY.equals(vo.getCategory())) {
						ImageCacheLoader.getInstance().loadRes(null, userViewHolder.item_chat_headimg, R.drawable.news_icon_activity,
								R.drawable.news_icon_activity, R.drawable.news_icon_activity, null, true);
					} else if (MsgsConstants.MCC_PLAY.equals(vo.getCategory())) {
						ImageCacheLoader.getInstance().loadRes(null, userViewHolder.item_chat_headimg, R.drawable.news_icon_play,
								R.drawable.news_icon_play, R.drawable.news_icon_play, null, true);
					} else {
						ImageCacheLoader.getInstance().loadRes(null, userViewHolder.item_chat_headimg, R.drawable.news_icon_youban,
								R.drawable.news_icon_youban, R.drawable.news_icon_youban, null, true);
					}
					userViewHolder.tv_msg_text.setBackgroundResource(R.drawable.chat_msg_system_in_bg);
					userViewHolder.tv_username.setText(R.string.news_sys_name);
				} else {
					ImageCacheLoader.getInstance().loadRes(null, userViewHolder.item_chat_headimg, R.drawable.news_icon_admin,
							R.drawable.news_icon_admin, R.drawable.news_icon_admin, null, true);
					userViewHolder.tv_msg_text.setBackgroundResource(R.drawable.chat_msg_system_in_bg);
					userViewHolder.tv_username.setText(R.string.news_sysadmin_name);
				}
			} else {
				// 公会聊天，群发消息界面，显示发送的用户名称
				if (type == TYPE_IN_USER && (pageType == PAGE_TYPE_GROUPMASSMSG || pageType == PAGE_TYPE_MCHAT)) {
					userViewHolder.tv_username.setVisibility(View.VISIBLE);
				} else {
					userViewHolder.tv_username.setVisibility(View.GONE);
				}

				if (user_cache.containsKey(vo.getFromId())) {
					UserVo result = user_cache.get(vo.getFromId());
					// new
					// ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()),
					// ImageLoader.RADIUS_DEFAULT_PX6,
					// userViewHolder.item_chat_headimg,
					// R.drawable.common_user_icon_default);
					// ImageLoader.getInstance()
					// .loadRes(ResUtil.getSmallRelUrl(result.getAvatar()),
					// userViewHolder.item_chat_headimg,
					// R.drawable.common_user_icon_default,
					// R.drawable.common_user_icon_default,
					// R.drawable.common_user_icon_default,
					// null, true);
					ImageCacheLoader.getInstance()
							.loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), userViewHolder.item_chat_headimg,
									R.drawable.common_user_icon_default, R.drawable.common_user_icon_default, R.drawable.common_user_icon_default,
									null, true);

					String gRemarksName = result.getGremarksName();
					if (gRemarksName != null && !gRemarksName.isEmpty()) {
						userViewHolder.tv_username.setText(context.getResources().getString(R.string.chat_userchat_username, gRemarksName));
					} else {
						userViewHolder.tv_username.setText(context.getResources().getString(R.string.chat_userchat_username,
								result.getUsername() == null ? "" : result.getUsername()));
					}

					if (result.getSex() == MsgsConstants.SEX_MAN) {
						Drawable drawable = context.getResources().getDrawable(R.drawable.chat_sex_0);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
						userViewHolder.tv_username.setCompoundDrawables(drawable, null, null, null);
					} else if (result.getSex() == MsgsConstants.SEX_WOMAN) {
						Drawable drawable = context.getResources().getDrawable(R.drawable.chat_sex_1);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
						userViewHolder.tv_username.setCompoundDrawables(drawable, null, null, null);
					} else {
						userViewHolder.tv_username.setCompoundDrawables(null, null, null, null);
					}
				} else {
					// 获得用户信息
					ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

						@Override
						public void onSuccess(List<UserVo> uservoresult) {
							// TODO Auto-generated method stub
							if (uservoresult == null || uservoresult.size() <= 0)
								return;
							UserVo result = uservoresult.get(0);
							if (result != null) {
								ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), userViewHolder.item_chat_headimg,
										R.drawable.common_user_icon_default, R.drawable.common_user_icon_default,
										R.drawable.common_user_icon_default, null, true);

								userViewHolder.tv_username.setText(context.getResources().getString(R.string.chat_userchat_username,
										result.getUsername() == null ? "" : result.getUsername()));

								// 数据库获取用户信息（备注）
								UserVo uvo = dao.getUserByUserId(result.getUserid());
								GroupUserRelVo grelvo = null;
								if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
									grelvo = grelDao.findUsers(vo.getToId(), result.getUserid());
								}
								if (grelvo != null && grelvo.getRemark() != null && !grelvo.getRemark().isEmpty()) {
									userViewHolder.tv_username.setText(context.getResources().getString(R.string.chat_userchat_username,
											grelvo.getRemark()));
									result.setGremarksName(grelvo.getRemark());
									// result.setUsername(grelvo.getRemark());
								} else {
									if (uvo != null && uvo.getRemarksName() != null && !uvo.getRemarksName().isEmpty()) {
										userViewHolder.tv_username.setText(context.getResources().getString(R.string.chat_userchat_username,
												uvo.getRemarksName()));
										result.setUsername(uvo.getRemarksName());
									} else {
										userViewHolder.tv_username.setText(context.getResources().getString(R.string.chat_userchat_username,
												result.getUsername() == null ? "" : result.getUsername()));
									}
								}
								if (result.getSex() == MsgsConstants.SEX_MAN) {
									Drawable drawable = context.getResources().getDrawable(R.drawable.chat_sex_0);
									drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
									userViewHolder.tv_username.setCompoundDrawables(drawable, null, null, null);
								} else if (result.getSex() == MsgsConstants.SEX_WOMAN) {
									Drawable drawable = context.getResources().getDrawable(R.drawable.chat_sex_1);
									drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
									userViewHolder.tv_username.setCompoundDrawables(drawable, null, null, null);
								} else {
									userViewHolder.tv_username.setCompoundDrawables(null, null, null, null);
								}
								user_cache.put(result.getUserid(), result);

							} else {
								ImageCacheLoader.getInstance().loadRes(null, userViewHolder.item_chat_headimg, R.drawable.common_user_icon_default,
										R.drawable.common_user_icon_default, R.drawable.common_user_icon_default, null, true);

								userViewHolder.tv_username.setText("");
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							// TODO Auto-generated method stub
							LogUtil.e(TAG, "获得用户信息异常：" + result);
							ImageCacheLoader.getInstance().loadRes(null, userViewHolder.item_chat_headimg, R.drawable.common_user_icon_default,
									R.drawable.common_user_icon_default, R.drawable.common_user_icon_default, null, true);

							userViewHolder.tv_username.setText("");
							userViewHolder.tv_username.setCompoundDrawables(null, null, null, null);
						}

					};

					if (vo.getFromId() == loginUserVo.getUserid()) {
						list.clear();
						list.add(loginUserVo);
						callback.onSuccess(list);
					} else {
						final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
						final UserVo userVo = dao.getUserByUserId(vo.getFromId());
						if (userVo != null && userVo.getUsername() != null && !userVo.getUsername().isEmpty()) {
							list.clear();
							list.add(userVo);
							callback.onSuccess(list);
						} else {
							ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
							ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
							cdp.setId(vo.getFromId());
							cdp.setUtime(userVo == null ? 0 : userVo.getUpdatetime());
							p.addParam(cdp.build());
							ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, null, p.build(), 0, null, true);
						}
					}
				}
			}

			// 设置内容
			if (vo.getContentType() == MessageContentType.TEXT_VALUE) {
				// 判断是否访客账号，访客账号设置为不可点和修改提示文字
				if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && vo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)
						&& vo.getContent().contains(MyTagUtil.TAG_FORMAT_PREFIX_PREFIX + MsgsConstants.MESSAGE_OP_GUEST_BIND_CPN)
						&& SystemContext.getInstance().getIsGuest() != 1) {
					if (vo.getContent().contains(MyTagUtil.TAG_FORMAT_PREFIX_PREFIX + MsgsConstants.MESSAGE_OP_GUEST_BIND_CPN)) {
						// 更新消息
						String content = vo.getContent();
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_GUEST_BIND_CPN,
								context.getString(R.string.message_action_bundphone_update));
						vo.setContent(content);
						ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
					}
				}
				setContentTextValue(vo, userViewHolder);
			} else if (vo.getContentType() == MessageContentType.IMAGE_VALUE) {
				setContentImageValue(vo, userViewHolder);
			} else if (vo.getContentType() == MessageContentType.IMAGE_ID_REF_VALUE) {
				setContentImageIdRefValue(vo, userViewHolder);
			} else if (vo.getContentType() == MessageContentType.VOICE_VALUE) {
				setContentVoiceValue(vo, userViewHolder, position, type);
			} else if (vo.getContentType() == MessageContentType.NEWS_TEXT_VALUE) {
				setContentNewsTextValue(vo, userViewHolder);
			} else if (vo.getContentType() == MessageContentType.VOICE_ID_REF_VALUE) {
				setContentVoiceValue2(vo, userViewHolder, position, type);
			}
			// 增加头像单击事件
			if (vo.getFromId() != loginUserVo.getUserid() && vo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
				// 增加头像的单击事件
				userViewHolder.item_chat_headimg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// 打开对方资料
						Intent intent = new Intent(context, UserDetailInfoActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, vo.getFromId());
						// 判断是对聊还是其他
						if (vo.getChannelType().equals(MsgsConstants.MC_CHAT)) {
							bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ISFROMCHATACTIVITYOPEN, true);
						} else {
							bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ISFROMCHATACTIVITYOPEN, false);
							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, vo.getToId());
							if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
								GroupUserRelVo groupUserRelVo = DaoFactory.getDaoFactory().getGroupUserRelDao(context)
										.findUsers(vo.getToId(), loginUserVo.getUserid());
								if (groupUserRelVo != null) {
									if (groupUserRelVo.getRel() == GroupUserRelVo.REL_ADMIN
											|| groupUserRelVo.getRel() == GroupUserRelVo.REL_NORMALADMIN) {
										bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_STOP_SAY_WORDS, true);
										bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, vo.getToId());
									}
								}
								if (user_cache.containsKey(vo.getFromId())) {
									UserVo result = user_cache.get(vo.getFromId());
									bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GROUP_CARD, result.getGremarksName());
								}
								bundle.putBoolean(SystemConfig.IF_NOT_SHOW_GROUP_CARD, true);
							}
						}

						intent.putExtras(bundle);
						context.startActivity(intent);
					}
				});
			}

		} else if (type == TYPE_IN_SYSTEM) {
			// 增加判断时间是否需要显示
			if (isShowTime) {
				systemViewHolder.tv_date.setVisibility(View.VISIBLE);
				systemViewHolder.tv_date.setText(SafeUtils.getFormatDate(vo.getCreateTime(), "MM-dd HH:mm"));
			} else {
				systemViewHolder.tv_date.setVisibility(View.GONE);
			}
			// 设置内容
			if (vo.getContentType() == MessageContentType.TEXT_VALUE) {
				MyTagClickListener listener = new MyTagClickListener() {

					@Override
					public void onClick(String param) {
						// TODO Auto-generated method stub
						// 根据param 解析出action和参数
						LogUtil.d(TAG, param);
						tagAction(vo, param);
					}

				};

				String content = vo.getContent();
				if (vo.getContent() != null && content.contains(MyTagUtil.TAG_FOLLOW)) {
					// 关注消息
					if (vo.getFromId() == loginUserVo.getUserid())
						content = content.substring(0, content.indexOf(MyTagUtil.TAG_FOLLOW));
					else
						content = content.substring(content.indexOf(MyTagUtil.TAG_FOLLOW) + MyTagUtil.TAG_FOLLOW.length());
					systemViewHolder.tv_msg_text.setText(MyTagUtil.praseSexTagText(context, content,
							context.getResources().getDimensionPixelSize(R.dimen.global_string_sex_width), context.getResources()
									.getDimensionPixelSize(R.dimen.global_string_sex_heigth)));
				} else {
					MessageExt messageExt = MessageUtil.buildMessageExt(vo);
					if (messageExt != null && loginUserVo != null && messageExt.getContent() != null
							&& messageExt.getOp() == MsgsConstants.MESSAGE_OP_JOINGROUP_MSG
							&& messageExt.getContent().getUid() == loginUserVo.getUserid() && messageExt.getContent().getMsg() != null) {
						content = messageExt.getContent().getMsg();
					}
					systemViewHolder.tv_msg_text.setText(MyTagUtil.analyseMyTag(context, content,
							context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width), context.getResources()
									.getDimensionPixelSize(R.dimen.global_string_smiley_heigth), listener));
				}

				systemViewHolder.tv_msg_text.SetLinkClickIntercept(mURLSpanClickable);
				if (vo.getFromId() == MsgsConstants.SYSTEM_POINT_ID && vo.getFromDomain().equals(MsgsConstants.DOMAIN_PLATFORM)) {
					// 是积分的
					systemViewHolder.tv_msg_text.setBackgroundResource(R.drawable.chat_msg_system2_bg);
				} else if (vo.getFromId() == MsgsConstants.SYSTEM_GROUP_GRADEUP_ID && vo.getFromDomain().equals(MsgsConstants.DOMAIN_PLATFORM)) {
					// 升级提示
					systemViewHolder.tv_msg_text.setBackgroundResource(R.drawable.chat_msg_system3_bg);
				} else {
					systemViewHolder.tv_msg_text.setBackgroundResource(R.drawable.chat_msg_system_bg);
				}
			}
		}

		return convertView;
	}

	/**
	 * 设置用户的内容布局显示哪个
	 * 
	 * @param textFlag
	 * @param imgFlag
	 * @param voiceFlag
	 * @param voiceStatus
	 *            声音的状态 ，只有voiceFlag为
	 * @param newsFlag
	 */
	private void setViewShow(UserViewHolder userViewHolder, boolean textFlag, boolean imgFlag, boolean voiceFlag, int voiceStatus, boolean newsFlag,
			boolean recommendGame) {
		if (textFlag)
			userViewHolder.tv_msg_text.setVisibility(View.VISIBLE);
		else
			userViewHolder.tv_msg_text.setVisibility(View.GONE);
		if (recommendGame)
			userViewHolder.item_chat_msg_recommend_ll.setVisibility(View.VISIBLE);
		else
			userViewHolder.item_chat_msg_recommend_ll.setVisibility(View.GONE);
		if (imgFlag)
			userViewHolder.tv_msg_img.setVisibility(View.VISIBLE);
		else
			userViewHolder.tv_msg_img.setVisibility(View.GONE);
		if (voiceFlag) {
			if (voiceStatus == MessageVo.VOICESTATUS_OK) {
				userViewHolder.item_chat_msg_voice_ll.setVisibility(View.VISIBLE);
				userViewHolder.item_chat_msg_voice_ll_downloading.setVisibility(View.GONE);
				userViewHolder.item_chat_msg_voice_ll_downloaderr.setVisibility(View.GONE);
			} else if (voiceStatus == MessageVo.VOICESTATUS_DOWNLOADING) {
				userViewHolder.item_chat_msg_voice_ll.setVisibility(View.GONE);
				userViewHolder.item_chat_msg_voice_ll_downloading.setVisibility(View.VISIBLE);
				userViewHolder.item_chat_msg_voice_ll_downloaderr.setVisibility(View.GONE);
			} else if (voiceStatus == MessageVo.VOICESTATUS_DOWNLOADERR) {
				userViewHolder.item_chat_msg_voice_ll.setVisibility(View.GONE);
				userViewHolder.item_chat_msg_voice_ll_downloading.setVisibility(View.GONE);
				userViewHolder.item_chat_msg_voice_ll_downloaderr.setVisibility(View.VISIBLE);
			} else {
				userViewHolder.item_chat_msg_voice_ll.setVisibility(View.VISIBLE);
				userViewHolder.item_chat_msg_voice_ll_downloading.setVisibility(View.GONE);
				userViewHolder.item_chat_msg_voice_ll_downloaderr.setVisibility(View.GONE);
			}
		} else {
			userViewHolder.item_chat_msg_voice_ll.setVisibility(View.GONE);
			userViewHolder.item_chat_msg_voice_ll_downloading.setVisibility(View.GONE);
			userViewHolder.item_chat_msg_voice_ll_downloaderr.setVisibility(View.GONE);
		}
		userViewHolder.item_chat_msg_share_ll.setVisibility(View.GONE);
	}

	/**
	 * 设置内容（文本）
	 * 
	 * @param vo
	 * @param userViewHolder
	 */
	private void setContentTextValue(final MessageVo vo, final UserViewHolder userViewHolder) {
		MyTagClickListener listener = new MyTagClickListener() {

			@Override
			public void onClick(String param) {
				// TODO Auto-generated method stub
				// 根据param 解析出action和参数
				LogUtil.d(TAG, param);
				tagAction(vo, param);
			}

		};
		// 判断内容类型中是否有推荐游戏的连接，有显示推荐游戏布局，没有显示文本布局
		String cnt = vo.getContent();
		if (cnt != null && cnt.contains(MyTagUtil.TAG_FORMAT_PREFIX_PREFIX + String.valueOf(MsgsConstants.MESSAGE_OP_GAME_DETAIL))) {// 推荐游戏
			setViewShow(userViewHolder, false, false, false, 0, false, true);
			// 显示主文本内容
			userViewHolder.item_chat_recommend_msg_text.setText(MyTagUtil.getMessageShowContent(cnt));
			userViewHolder.item_chat_recommend_msg_text.SetLinkClickIntercept(mURLSpanClickable);
			// 显示连接文本
			MyTagClickListener llistener = new MyTagClickListener() {

				@Override
				public void onClick(String param) {
					// TODO Auto-generated method stub
					// 根据param 解析出action和参数
					LogUtil.d(TAG, param);
					tagAction(vo, param);
				}

			};
			userViewHolder.item_chat_recommend_ll_content.setText(MyTagUtil.analyseMyTag(context, MyTagUtil.getOptContent(cnt), context
					.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width),
					context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth), llistener));
			userViewHolder.item_chat_recommend_ll_content.SetLinkClickIntercept(mURLSpanClickable);
			// 加载游戏图标
			long gid = MyTagUtil.getOptRecommendGameId(cnt);
			if (gid > 0) {
				ProxyFactory.getInstance().getGameProxy().getGameInfo(new ProxyCallBack<GameVo>() {

					@Override
					public void onSuccess(GameVo result) {
						if (result != null) {
							ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result.getGamelogo()),
									userViewHolder.item_chat_recommend_img, R.drawable.common_default_icon, R.drawable.common_default_icon,
									R.drawable.common_default_icon, null, true);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						ImageCacheLoader.getInstance().loadRes(null, userViewHolder.item_chat_recommend_img, R.drawable.common_default_icon,
								R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
					}
				}, SystemContext.getInstance().getContext(), gid);
			}
		} else if (vo.getEstimateop() == MsgsConstants.MESSAGE_OP_JOINGROUP_MSG
				|| vo.getEstimateop() == MsgsConstants.MESSAGE_OP_JOIN_SERVERGROUP_MSG) {// 加入公会消息（包括加入角色服务器公会）
			setViewShow(userViewHolder, false, false, false, 0, true, false);
			userViewHolder.item_chat_msg_share_ll.setVisibility(View.VISIBLE);
			String ext = vo.getExt();
			String avatar;
			final long uid;
			if (ext != null) {
				try {
					JSONObject jsonObj;
					jsonObj = new JSONObject(ext);
					if (jsonObj != null) {
						Object pobj = jsonObj.get("p");
						if (pobj != null) {
							JSONObject pjson = new JSONObject(pobj.toString());
							avatar = pjson.get("avatar").toString();
							uid = Long.parseLong(pjson.get("uid").toString());
							ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(avatar), userViewHolder.item_chat_share_img,
									R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
							userViewHolder.item_chat_msg_share_ll.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									if (loginUserVo != null && loginUserVo.getUserid() == uid) {
										ToastUtil.showToast(context, context.getString(R.string.check_information));
									} else {
										Intent intent = new Intent(context, UserDetailInfoActivity.class);
										Bundle bundle = new Bundle();
										bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
										intent.putExtras(bundle);
										context.startActivity(intent);
									}
								}
							});
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			userViewHolder.item_chat_share_bottom_icon.setVisibility(View.VISIBLE);
			userViewHolder.item_chat_share_content.setText(vo.getContent());
			userViewHolder.item_chat_share_bottom.setText(context.getResources().getString(R.string.chat_role_desc_info));
		} else {
			setViewShow(userViewHolder, true, false, false, 0, false, false);
			// Paint paint = new Paint();
			// paint.setTextSize(userViewHolder.tv_msg_text.getTextSize());
			// paint.setTextScaleX(userViewHolder.tv_msg_text.getTextScaleX());
			// userViewHolder.tv_msg_text.setText(SmileyUtil.ReplaceSmiley(context,
			// vo.getContent(),(int)TextUtil.getTextHeight(paint),(int)TextUtil.getTextHeight(paint)));
			userViewHolder.tv_msg_text.setText(MyTagUtil.analyseMyTag(context, cnt,
					context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width),
					context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_heigth), listener));
			// userViewHolder.tv_msg_text.setText(MyTagUtil.analyseMyTag(context,
			// vo.getContent(),
			// DisplayUtil.getFontHeight(userViewHolder.tv_msg_text.getTextSize()),
			// DisplayUtil.getFontHeight(userViewHolder.tv_msg_text.getTextSize()),
			// listener));
			userViewHolder.tv_msg_text.SetLinkClickIntercept(mURLSpanClickable);
		}
	}

	/**
	 * 设置内容（图片）
	 * 
	 * @param vo
	 * @param userViewHolder
	 */
	private void setContentImageValue(final MessageVo vo, UserViewHolder userViewHolder) {
		if (pageType == PAGE_TYPE_GROUPMANAGEMASSSEND) {
			setViewShow(userViewHolder, true, false, false, 0, false, false);
			userViewHolder.tv_msg_text.setText(vo.getSummary());
		} else {

			// if(vo.getStatus() != MessageVo.STATUS_SENDERR)
			// {
			// setViewShow(userViewHolder, true, false, false, false);
			// userViewHolder.tv_msg_text.setText(vo.getSummary());
			// }
			// else
			// {
			setViewShow(userViewHolder, false, true, false, 0, false, false);
			String filename = String.format(SystemConfig.IMAGE_LOCAL_SEND_FILENAME_FORMAT, vo.getId());
			userViewHolder.tv_msg_img.setImageDrawable(null);
			userViewHolder.tv_msg_img.setTag(R.id.imageview_tag_current_display_uri, null);
			userViewHolder.tv_msg_img.setTag(R.id.imageview_tag_current_wait_display_uri, null);
			final String loaclimage = "file://" + filename;
			// new ImageLoader().loadRes(loaclimage, 0,
			// userViewHolder.tv_msg_img,R.drawable.common_default_icon, true);
			ImageLoader.getInstance().loadRes(loaclimage, userViewHolder.tv_msg_img, R.drawable.common_default_icon, R.drawable.common_default_icon,
					R.drawable.common_default_icon, null, true);
			// ImageCacheLoader.getInstance().loadRes(loaclimage,userViewHolder.tv_msg_img,
			// R.drawable.common_default_icon, R.drawable.common_default_icon,
			// R.drawable.common_default_icon, null, true);

			// 增加图片单击显示原始图片效果
			userViewHolder.tv_msg_img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String[] images = new String[1];
					images[0] = loaclimage;// ResUtil.getOriginalRelUrl(vo.getContent());
					Intent intent = new Intent(context, ImageBrowerActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, images);
					bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_ISLOADNET, true);
					String chatInfo = vo.getFromId() + "," + vo.getToId() + "," + vo.getFromDomain() + "," + vo.getToDomain() + ","
							+ vo.getCategory();
					bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CHATINFO, chatInfo);
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, vo.getMsgId());
					String ctype = vo.getChannelType();
					if (ctype.equals(MsgsConstants.MC_CHAT)) {
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_CHAT);
					} else if (ctype.equals(MsgsConstants.MC_MCHAT)) {
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_MCHAT);
					} else if (ctype.equals(MsgsConstants.MC_NOTIFY)) {
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_NOTIFY);
					} else if (ctype.equals(MsgsConstants.MC_PUB)) {
						bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_PUB);
					}
					if (loginUserVo != null && vo.getFromId() == loginUserVo.getUserid() && vo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)
							&& vo.getStatus() == MessageVo.STATUS_SENDERR) {
						bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
					}
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					context.startActivity(intent);
				}
			});

			// }

		}
	}

	/**
	 * 设置内容（new text，分享的帖子）
	 * 
	 * @param vo
	 * @param userViewHolder
	 */
	private void setContentNewsTextValue(final MessageVo vo, final UserViewHolder userViewHolder) {
		setViewShow(userViewHolder, false, false, false, 0, true, false);
		userViewHolder.item_chat_msg_share_ll.setVisibility(View.GONE);
		// 内容转json
		if (vo.getContent() != null) {
			String title = "";
			String desc = "";
			String contenttype = "";
			long tmptopicid = 0;
			long tmpgameid = 0;
			String pic = "";
			long tid = 0;
			String link = "";
			try {
				JSONObject json = new JSONObject(vo.getContent());

				JSONObject news = json.getJSONObject(MsgsConstants.JKEY_MESSAGE_NEWS);
				JSONArray articles = news.getJSONArray(MsgsConstants.JKEY_MESSAGE_NEWS_ARTICLES);
				if (articles.length() > 0) {
					JSONObject article = articles.getJSONObject(0);
					title = article.getString(MsgsConstants.JKEY_MESSAGE_NEWS_TITLE);
					desc = article.getString(MsgsConstants.JKEY_MESSAGE_NEWS_DESC);
					contenttype = article.getString(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT_TYPE);
					JSONObject content = article.getJSONObject(MsgsConstants.JKEY_MESSAGE_NEWS_CONTENT);
					if (contenttype.equals(MsgsConstants.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_POST)) {
						tmptopicid = content.getLong(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_TOPICID);
						tmpgameid = content.getLong(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GAMEID);
					} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GAME)) {
						tid = content.getLong(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GAMEID);
					} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GOODS)) {
						tid = content.getLong(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GOODSID);
					} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GROUP)) {
						tid = content.getLong(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_GRID);
					} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_USER)) {
						tid = content.getLong(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_UID);
					} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_WEBPAGE)) {
						tid = content.getLong(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_WEBPAGEID);
						link = content.getString(SystemConfig.JKEY_MESSAGE_NEWS_ARTICLES_ITEM_CONTENT_WEBPAGELINK);
					}
					pic = article.getString(MsgsConstants.JKEY_MESSAGE_NEWS_PIC);
				}

			} catch (JSONException ex) {

			}
			final long topicid = tmptopicid;
			final long gameid = tmpgameid;
			final long targetid = tid;
			final String targetlink = link;
			if (pic != null && !pic.isEmpty()) {
				userViewHolder.item_chat_share_img.setVisibility(View.VISIBLE);
				// new ImageLoader().loadRes(ResUtil.getSmallRelUrl(pic), 0,
				// userViewHolder.item_chat_share_img, true);
				// ImageLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(pic),
				// userViewHolder.item_chat_share_img,
				// R.drawable.common_default_icon,
				// R.drawable.common_default_icon,
				// R.drawable.common_default_icon, null, true);
				String url = null;
				if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_WEBPAGE)) {
					url = pic;
				} else {
					url = ResUtil.getSmallRelUrl(pic);
				}
				ImageCacheLoader.getInstance().loadRes(url, userViewHolder.item_chat_share_img, R.drawable.common_default_icon,
						R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
			} else {
				userViewHolder.item_chat_share_img.setVisibility(View.GONE);
			}
			userViewHolder.item_chat_msg_share_ll.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub

					if (objectContextListener instanceof ChatFragment) {
						((ChatFragment) objectContextListener).showActionDialog(vo);
						return true;
					}

					return false;
				}
			});
			if (contenttype.equals(MsgsConstants.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_POST)) {
				if (topicid > 0 && gameid > 0) {
					// 先获得贴吧信息写cache，再获得帖子信息写cache， 最后才是展示
					getGameInfo(userViewHolder, gameid, topicid, title);
					userViewHolder.item_chat_msg_share_ll.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 根据当前的应用配置判断是否要启动游伴
							AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
							if (appconfig != null && appconfig.isRecbarmsg() && gameid != appconfig.getGameId()) {
								AppUtil.openGame(context, topicid, TopicDetailActivity.class.getName(),
										context.getResources().getString(R.string.postbar_show_topic_tip_for_youban_uninstall));
							} else {
								Intent intent = new Intent(context, TopicDetailActivity.class);
								Bundle bundle = new Bundle();
								bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, topicid);
								bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gameid);
								intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
								context.startActivity(intent);
							}
						}
					});
				}
			} else {
				userViewHolder.item_chat_share_content.setText(title);
				if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GAME)) {
					userViewHolder.item_chat_msg_share_ll.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							openGameDetail(context, vo, targetid);
						}
					});
				} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GOODS)) {
					userViewHolder.item_chat_msg_share_ll.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(context, GoodsDetailActivity.class);
							intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_ID, targetid);
							intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GOODS_FLAG, true);
							context.startActivity(intent);
						}
					});
				} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_GROUP)) {
					userViewHolder.item_chat_msg_share_ll.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(context, GroupDetailActivity.class);
							Bundle bundle = new Bundle();
							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, targetid);
							bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ISFROMCHATACTIVITYOPEN, false);
							intent.putExtras(bundle);
							context.startActivity(intent);
						}
					});
				} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_USER)) {
					userViewHolder.item_chat_msg_share_ll.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							UserVo uvo = loginUserVo;
							if (uvo != null && uvo.getUserid() == targetid) {
								ToastUtil.showToast(context, context.getString(R.string.check_information));
							} else {
								Intent intent = new Intent(context, UserDetailInfoActivity.class);
								Bundle bundle = new Bundle();
								bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, targetid);
								intent.putExtras(bundle);
								context.startActivity(intent);
							}
						}
					});
				} else if (contenttype.equals(SystemConfig.JVALUE_MESSAGE_NEWS_CONTENT_TYPE_WEBPAGE)) {
					userViewHolder.item_chat_msg_share_ll.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(context, WebBrowserActivity.class);
							intent.setData(Uri.parse(targetlink));
							intent.setAction(Intent.ACTION_VIEW);
							context.startActivity(intent);
						}
					});
				}
			}
			userViewHolder.item_chat_msg_share_ll.setVisibility(View.VISIBLE);
			userViewHolder.item_chat_share_bottom_icon.setVisibility(View.GONE);
		}
	}

	/**
	 * 贴吧id
	 * 
	 * @param gameid
	 */
	private void getGameInfo(final UserViewHolder userViewHolder, final long gameid, final long topicid, final String topictitle) {
		if (game_cache.containsKey(gameid)) {
			setShareTopicContent(userViewHolder, gameid, topicid, topictitle);
		} else {
			// 获得贴吧信息
			ProxyCallBack<GameVo> callback = new ProxyCallBack<GameVo>() {

				@Override
				public void onSuccess(GameVo result) {
					// TODO Auto-generated method stub
					if (result != null) {
						game_cache.put(gameid, result);
					}
					setShareTopicContent(userViewHolder, gameid, topicid, topictitle);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					// TODO Auto-generated method stub
					setShareTopicContent(userViewHolder, gameid, topicid, topictitle);
					LogUtil.e(TAG, "获得贴吧信息异常");
				}
			};
			ProxyFactory.getInstance().getGameProxy().getGameInfo(callback, context, gameid);

		}
	}

	// /**
	// * 获得帖子信息
	// */
	// public void getTopicInfo(final UserViewHolder userViewHolder, final long
	// gameid, final long topicid) {
	// if (topic_cache.containsKey(topicid)) {
	// setShareTopicContent(userViewHolder, gameid, topicid);
	// } else {
	// ProxyCallBack<Msgs.PostbarTopicDetail> callback = new
	// ProxyCallBack<Msgs.PostbarTopicDetail>() {
	//
	// @Override
	// public void onSuccess(final PostbarTopicDetail result) {
	// // TODO Auto-generated method stub
	// if (result != null) {
	// topic_cache.put(topicid, result);
	// setShareTopicContent(userViewHolder, gameid, topicid);
	// } else {
	// setShareTopicContent(userViewHolder, gameid, topicid);
	// }
	// }
	//
	// @Override
	// public void onFailure(final Integer result, final String resultMsg) {
	// // TODO Auto-generated method stub
	// LogUtil.e(TAG, "获得帖子信息异常");
	// setShareTopicContent(userViewHolder, gameid, topicid);
	// }
	//
	// };
	// ProxyFactory.getInstance().getPostbarProxy().getTopicDetail(callback,
	// context, topicid);
	// }
	//
	// }

	/*
	 * 设置分享的帖子内容，这个时候cache 有贴吧信息和 帖子信息
	 */
	public void setShareTopicContent(UserViewHolder userViewHolder, long gameid, long topicid, String topictitle) {

		if (game_cache.containsKey(gameid) && game_cache.get(gameid) != null && !((GameVo) game_cache.get(gameid)).getGamelogo().isEmpty()) {
			// userViewHolder.item_chat_share_img.setVisibility(View.VISIBLE);
			GameVo gameVo = ((GameVo) game_cache.get(gameid));
			// new
			// ImageLoader().loadRes(ResUtil.getSmallRelUrl(gameVo.getGamelogo()),
			// 0, userViewHolder.item_chat_share_img, true);
			userViewHolder.item_chat_share_content.setText(context.getString(R.string.chat_share_topic_title_info, gameVo.getGamename(), topictitle));
		} else {
			// userViewHolder.item_chat_share_img.setVisibility(View.GONE);
			userViewHolder.item_chat_share_content.setText(context.getString(R.string.chat_share_topic_title_info, "", topictitle));
		}

	}

	/**
	 * 设置内容（图片id资源）
	 * 
	 * @param vo
	 * @param userViewHolder
	 */
	private void setContentImageIdRefValue(final MessageVo vo, final UserViewHolder userViewHolder) {
		setViewShow(userViewHolder, false, true, false, 0, false, false);

		// 增加公会消息判断，如果是公会的消息需要根据网络判断是否加载图片，//还需要判断网络类型
		if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP) && NetworkUtil.getAPNType(context) != NetworkUtil.NETTYPE_WIFI) {
			// 获得公会信息

			if (group_cache.containsKey(vo.getFromId())) {
				GroupVo result = group_cache.get(vo.getToId());
				if (result.getNetoffon() == 0)// false,能够访问图片
				{
					LogUtil.d(TAG, "--加载网络图片[" + ResUtil.getSmallRelUrl(vo.getContent()) + "]");
					// 加载网络图片
					showMessageContentImage(userViewHolder, vo, true);
				} else {
					// 不加载网络图片
					LogUtil.d(TAG, "--不加载网络图片[" + ResUtil.getSmallRelUrl(vo.getContent()) + "]");
					showMessageContentImage(userViewHolder, vo, false);
				}
			} else {

				ProxyCallBack<List<GroupVo>> callback2 = new ProxyCallBack<List<GroupVo>>() {

					@Override
					public void onSuccess(List<GroupVo> groupvoresult) {
						// TODO Auto-generated method stub
						if (groupvoresult == null || groupvoresult.size() <= 0)
							return;
						GroupVo result = groupvoresult.get(0);
						if (result != null) {
							group_cache.put(result.getGrid(), result);
							if (result.getNetoffon() == 0)// false,能够访问图片
							{
								LogUtil.d(TAG, "--加载网络图片[" + ResUtil.getSmallRelUrl(vo.getContent()) + "]");
								// 加载网络图片
								showMessageContentImage(userViewHolder, vo, true);
							} else {
								// 不加载网络图片
								LogUtil.d(TAG, "--不加载网络图片[" + ResUtil.getSmallRelUrl(vo.getContent()) + "]");
								showMessageContentImage(userViewHolder, vo, false);
							}
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						LogUtil.e(TAG, "获得公会信息异常：" + result);
						ImageCacheLoader.getInstance().loadRes(null, userViewHolder.tv_msg_img, R.drawable.ic_launcher, R.drawable.ic_launcher,
								R.drawable.ic_launcher, null, true);

					}

				};
				GroupVo groupVo = groupDao.findGroupByGrid(vo.getToId());
				ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
				ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
				cdp.setId(vo.getToId());
				cdp.setUtime(groupVo == null ? 0 : groupVo.getUtime());
				p.addParam(cdp.build());
				ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback2, null, p.build(), MsgsConstants.OT_GROUP, null);
			}
		} else {
			showMessageContentImage(userViewHolder, vo, true);
		}

		userViewHolder.tv_msg_img.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View paramView) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	/**
	 * 设置内容（声音）
	 * 
	 * @param vo
	 * @param userViewHolder
	 * @param position
	 * @param type
	 */
	private void setContentVoiceValue(final MessageVo vo, final UserViewHolder userViewHolder, final int position, final int type) {
		if (pageType == PAGE_TYPE_GROUPMANAGEMASSSEND) {
			setViewShow(userViewHolder, true, false, false, 0, false, false);
			userViewHolder.tv_msg_text.setText(vo.getSummary());
		} else {
			setViewShow(userViewHolder, false, false, true, MessageVo.VOICESTATUS_OK, false, false);
			// 声音播放的动画
			final AnimationDrawable animationDrawable = (AnimationDrawable) ((type == TYPE_OUT) ? userViewHolder.item_chat_msg_voice_anim
					.getCompoundDrawables()[2] : userViewHolder.item_chat_msg_voice_anim.getCompoundDrawables()[0]);

			final long delayMillis = (vo.getContent() != null ? Long.parseLong(vo.getContent()) : 0);
			final Runnable animStopRunnable = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					// LogUtil.d(TAG,
					// "执行动画停止:["+this+"]"+System.currentTimeMillis() );
					// 执行完了，不移除，不影响
					// animStopRunnables.remove(key)
					if (animationDrawable != null && animationDrawable.isRunning()) {
						animationDrawable.stop();
					}
					vo.setPlaying(false);
					userViewHolder.item_chat_msg_voice_anim.setVisibility(View.GONE);
					if (currentMediaPlayerDataSource.equals(String.format(SystemConfig.AUDIO_FILENAME_FORMAT, vo.getMsgId()))) {
						// LogUtil.d(TAG,
						// "animStopRunnable:run stopplaying:["+this+"]"+System.currentTimeMillis()
						// );
						AudioPlayer.getInstance().stopPlaying(context);
					}
				}
			};
			userViewHolder.item_chat_msg_voice_duration
					.setText(SafeUtils.getDuration2Str(
							vo.getContent() != null ? (Long.parseLong(vo.getContent()) > SystemConfig.AUDIO_RECORDER_MAXDURATION ? SystemConfig.AUDIO_RECORDER_MAXDURATION
									: Long.parseLong(vo.getContent()))
									: 0, false));

			if (vo.isPlaying()) {
				userViewHolder.item_chat_msg_voice_anim.setVisibility(View.VISIBLE);
				if (!vo.isWaitStopPlaying()) {
					animationDrawable.start();
					// LogUtil.d(TAG,
					// "动画需要继续执行:["+animStopRunnable+"]"+System.currentTimeMillis()
					// );
					if (animStopRunnables.get(vo) != null)
						handler.removeCallbacks(animStopRunnables.get(vo));
					animStopRunnables.put(vo, animStopRunnable);
					handler.postDelayed(animStopRunnable, delayMillis - AudioPlayer.getInstance().getCurrentPosition());
				} else {
					vo.setWaitStopPlaying(false);
					// LogUtil.d(TAG,
					// "动画不需要继续执行:["+animStopRunnable+"]"+System.currentTimeMillis()
					// );
					if (animStopRunnables.get(vo) != null)
						handler.removeCallbacks(animStopRunnables.get(vo));
					handler.post(animStopRunnable);
				}

			} else {
				userViewHolder.item_chat_msg_voice_anim.setVisibility(View.GONE);
			}
			// 单击
			userViewHolder.item_chat_msg_voice_anim_tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					userViewHolder.item_chat_msg_voice_anim.setVisibility(View.VISIBLE);

					animationDrawable.start();
					// LogUtil.d(TAG,
					// "启动动画:["+animStopRunnable+"]"+System.currentTimeMillis()
					// );
					if (animStopRunnables.get(vo) != null)
						handler.removeCallbacks(animStopRunnables.get(vo));
					animStopRunnables.put(vo, animStopRunnable);
					handler.postDelayed(animStopRunnable, delayMillis);
					vo.setPlaying(true);
					vo.setWaitStopPlaying(false);
					if (type == TYPE_OUT
							&& !(vo.getChannelType().equals(MsgsConstants.MC_PUB) && vo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)))

						AudioPlayer.getInstance().startPlaying(context, String.format(SystemConfig.AUDIO_LOCAL_SEND_FILENAME_FORMAT, vo.getId()));
					else
						AudioPlayer.getInstance().startPlaying(context, String.format(SystemConfig.AUDIO_FILENAME_FORMAT, vo.getMsgId()));

					currentMediaPlayerDataSource = String.format(SystemConfig.AUDIO_FILENAME_FORMAT, vo.getMsgId());
					// 判断其他的是否在播放
					// 如果其他的在播放，先停止，然后再启动该声音的动画
					stopPlayingAudio(position);
				}
			});

			userViewHolder.item_chat_msg_voice_anim.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View paramView) {
					// TODO Auto-generated method stub
					// 停止动画，停止播放
					// LogUtil.d(TAG,
					// "停止当前动画:["+animStopRunnable+"]"+System.currentTimeMillis()
					// );
					if (animStopRunnables.get(vo) != null)
						handler.removeCallbacks(animStopRunnables.get(vo));
					handler.post(animStopRunnable);
				}
			});
			userViewHolder.item_chat_msg_voice_anim_tv.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View paramView) {
					// TODO Auto-generated method stub
					return false;
				}
			});

		}

	}

	/**
	 * 设置是声音资源文件（要么去下载，要不下载中，要不下载异常)
	 * 
	 * @param vo
	 * @param userViewHolder
	 * @param position
	 * @param type
	 */
	private void setContentVoiceValue2(final MessageVo vo, final UserViewHolder userViewHolder, final int position, final int type) {
		if (vo.getVoiceStatus() == MessageVo.VOICESTATUS_DOWNLOADING) {
			// 在下载中
			setViewShow(userViewHolder, false, false, true, MessageVo.VOICESTATUS_DOWNLOADING, false, false);
		} else if (vo.getVoiceStatus() == MessageVo.VOICESTATUS_DOWNLOADERR) {
			// 下载异常的
			setViewShow(userViewHolder, false, false, true, MessageVo.VOICESTATUS_DOWNLOADERR, false, false);
		} else {
			// 设置成下载中，然后去下载
			setViewShow(userViewHolder, false, false, true, MessageVo.VOICESTATUS_DOWNLOADING, false, false);
			new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

				@Override
				public Void execute() {
					// TODO Auto-generated method stub
					// 去下载，保存文件，更新数据库，更新界面数据
					if (vo.getContentType() == MessageContentType.VOICE_ID_REF_VALUE) {
						try {
							String filename = String.format(SystemConfig.AUDIO_FILENAME_FORMAT, vo.getMsgId());
							FileUtils.writeSDFile(filename, HttpUtil.sendRequest(ResUtil.getOriginalRelUrl(vo.getContent()), null, null));
							vo.setContent(String.valueOf(AudioUtils.getAmrDuration(filename)));
							vo.setContentType(MessageContentType.VOICE_VALUE);
							vo.setVoiceStatus(MessageVo.VOICESTATUS_OK);
							// 更新数据库
							ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), vo.getContent());

						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							vo.setVoiceStatus(MessageVo.VOICESTATUS_DOWNLOADERR);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							vo.setVoiceStatus(MessageVo.VOICESTATUS_DOWNLOADERR);
							e.printStackTrace();
						}

					}

					return null;
				}

				@Override
				public void onHandle(Void result) {
					// TODO Auto-generated method stub
					// 通知刷新界面
					ChatMessageAdapter.this.notifyDataSetChanged();

				}

			});
		}

	}

	/**
	 * 设置图片
	 * 
	 * @param userViewHolder
	 * @param vo
	 * @param isLoadNetWork
	 */
	private void showMessageContentImage(UserViewHolder userViewHolder, final MessageVo vo, final boolean isLoadNetWork) {

		userViewHolder.tv_msg_img.setImageDrawable(null);
		userViewHolder.tv_msg_img.setTag(R.id.imageview_tag_current_display_uri, null);
		userViewHolder.tv_msg_img.setTag(R.id.imageview_tag_current_wait_display_uri, null);

		// new ImageLoader().loadRes(ResUtil.getSmallRelUrl(vo.getContent()), 0,
		// userViewHolder.tv_msg_img,R.drawable.common_default_icon,
		// isLoadNetWork);
		ImageLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(vo.getContent()), userViewHolder.tv_msg_img, R.drawable.common_default_icon,
				R.drawable.common_default_icon, R.drawable.common_default_icon, null, isLoadNetWork);

		// ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(vo.getContent()),userViewHolder.tv_msg_img,
		// R.drawable.common_default_icon, R.drawable.common_default_icon,
		// R.drawable.common_default_icon, null, true);

		// 增加图片单击显示原始图片效果
		userViewHolder.tv_msg_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 判断设置的条件是否允许在2g，3g下打开原图
				// 增加公会消息判断，如果是公会的消息需要根据网络判断是否加载图片，//还需要判断网络类型
				if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP) && NetworkUtil.getAPNType(context) != NetworkUtil.NETTYPE_WIFI) {
					// 获得公会信息
					ProxyCallBack<List<GroupVo>> callback2 = new ProxyCallBack<List<GroupVo>>() {

						@Override
						public void onSuccess(List<GroupVo> groupvoresult) {
							// TODO Auto-generated method stub
							if (groupvoresult == null || groupvoresult.size() <= 0)
								return;
							GroupVo result = groupvoresult.get(0);
							if (result != null) {
								if (result.getNetoffon() == 0)// false,能够访问图片
								{
									// 加载网络图片
									openContentImageOriginal(vo, true);
								} else {
									// 不加载网络图片
									openContentImageOriginal(vo, false);
								}
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							// TODO Auto-generated method stub
							LogUtil.e(TAG, "获得公会信息异常：" + result);
							openContentImageOriginal(vo, isLoadNetWork);
						}

					};
					GroupVo groupVo = groupDao.findGroupByGrid(vo.getToId());
					ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
					ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
					cdp.setId(vo.getToId());
					cdp.setUtime(groupVo == null ? 0 : groupVo.getUtime());
					p.addParam(cdp.build());
					ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback2, null, p.build(), MsgsConstants.OT_GROUP, null);

				} else {
					openContentImageOriginal(vo, true);
				}

			}
		});

	}

	/**
	 * 打开原图
	 * 
	 * @param vo
	 * @param isLoadNetWork
	 */
	private void openContentImageOriginal(final MessageVo vo, final boolean isLoadNetWork) {
		String[] images = new String[1];
		images[0] = ResUtil.getOriginalRelUrl(vo.getContent());
		Intent intent = new Intent(context, ImageBrowerActivity.class);
		Bundle bundle = new Bundle();
		if (loginUserVo != null && vo.getFromId() == loginUserVo.getUserid() && vo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
			bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES_ISSHOWREPORTMENU, false);
		}
		String chatInfo = vo.getFromId() + "," + vo.getToId() + "," + vo.getFromDomain() + "," + vo.getToDomain() + "," + vo.getCategory();
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CHATINFO, chatInfo);
		bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, images);
		bundle.putBoolean(SystemConfig.BUNDLE_NAME_IMAGEBROWER_ISLOADNET, isLoadNetWork);
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, vo.getMsgId());
		String ctype = vo.getChannelType();
		if (ctype.equals(MsgsConstants.MC_CHAT)) {
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_CHAT);
		} else if (ctype.equals(MsgsConstants.MC_MCHAT)) {
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_MCHAT);
		} else if (ctype.equals(MsgsConstants.MC_NOTIFY)) {
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_NOTIFY);
		} else if (ctype.equals(MsgsConstants.MC_PUB)) {
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_PUB);
		}
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		context.startActivity(intent);

	}

	class UserViewHolder {
		ImageView item_chat_headimg;
		TextView tv_username;
		TextView tv_date;
		MyTextView tv_msg_text;
		ImageView tv_msg_img;
		ImageView item_chat_msg_sendfail;
		LinearLayout item_chat_msg_voice_ll;
		TextView item_chat_msg_voice_duration;
		TextView item_chat_msg_voice_anim_tv;
		TextView item_chat_msg_voice_anim;
		LinearLayout item_chat_msg_share_ll;
		ImageView item_chat_share_img;
		TextView item_chat_share_content;
		TextView item_chat_share_bottom;
		ProgressBar item_chat_msg_sending;
		ImageView item_chat_share_bottom_icon;

		LinearLayout item_chat_msg_voice_ll_downloading;
		LinearLayout item_chat_msg_voice_ll_downloaderr;

		LinearLayout item_chat_msg_recommend_ll;
		MyTextView item_chat_recommend_msg_text;
		ImageView item_chat_recommend_img;
		MyTextView item_chat_recommend_ll_content;
	}

	class SystemViewHolder {
		TextView tv_date;
		MyTextView tv_msg_text;
	}

	/**
	 * 停止正在播放中的声音
	 */
	public void stopPlayingAudio() {
		stopPlayingAudio(-1);
	}

	/**
	 * 停止正在播放中的声音,排除position
	 * 
	 * @param excludePosition
	 */
	public void stopPlayingAudio(int excludePosition) {
		boolean isHasWaitStop = false;
		for (int i = 0; i < data.size(); i++) {
			if (i != excludePosition && data.get(i).isPlaying()) {
				if (animStopRunnables.get(data.get(i)) != null)
					handler.removeCallbacks(animStopRunnables.get(data.get(i)));
				data.get(i).setWaitStopPlaying(true);
				isHasWaitStop = true;
			}
		}
		if (isHasWaitStop)
			notifyDataSetChanged();
	}

	private void tagAction(MessageVo vo, String param) {
		if (param != null && !param.equals("")) {
			String[] tmp = new String[2];
			int index = param.indexOf("{");
			if (index != -1) {
				tmp[0] = param.substring(0, index);
				tmp[1] = param.substring(index);
			} else {
				tmp[0] = param;
				tmp[1] = "";
			}

			try {
				int action = Integer.parseInt(tmp[0]);
				JSONObject jsonparam = null;
				if (tmp[1] != null && !tmp[1].equals("")) {
					jsonparam = new JSONObject(tmp[1]);
				}

				switch (action) {
				case MsgsConstants.MESSAGE_OP_RECOMMEND_GAME:// 推荐贴吧
				{
					systemRecommendGame(context, vo);
					break;
				}
				case MsgsConstants.MESSAGE_OP_RECOMMEND_GROUP:// 推荐公会
				{
					systemRecommendGroup(context, vo);
					break;
				}
				case MsgsConstants.MESSAGE_OP_RECOMMEND_USER:// 推荐用户
				{
					systemRecommendUser(context, vo);

					break;
				}
				case MsgsConstants.MESSAGE_OP_APPROVE_MEMBER:// 会长快速批准会员加入公会
					// "grid":***;"uid":***;"seq":***
				{
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID)
							|| !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}

					long grid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
					long uid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
					long seq = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEQ);
					groupManagerApproveMember(context, vo, grid, uid, seq);
					break;
				}
				case MsgsConstants.MESSAGE_OP_IGNORE_USER:// 公会管理员忽略用户的申请加入公会
				{
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID)
							|| !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}

					long grid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
					long uid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
					long seq = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEQ);
					groupManagerIgnoreUser(context, vo, grid, uid, seq);
					break;
				}
				case MsgsConstants.MESSAGE_OP_AGREE_JOIN_GROUP:// 被邀请用户同意加入公会
				{
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long grid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
					long seq = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEQ);
					userAgreeJoinGroup(context, vo, grid, seq);
					break;
				}
				case MsgsConstants.MESSAGE_OP_REFUSE_JOIN_GROUP:// 被邀请用户拒绝加入公会
				{
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long grid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
					long seq = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEQ);
					userRefuseJoinGroup(context, vo, grid, seq);
					break;
				}
				case MsgsConstants.MESSAGE_OP_GAME_DETAIL:// 快捷打开贴吧详情
				{
					long gid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
					openGameDetail(context, vo, gid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PROMOTION_USER_FOLLOW:// 关注推广用户
				{
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long uid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
					followUser(context, vo, uid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PROMOTION_USER_IGNORE:// 忽略推广用户
				{
					ignoreFollowUser(context, vo);
					break;
				}
				case MsgsConstants.MESSAGE_OP_GROUP_GRADE_UP:// 公会升级
				{
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long grid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
					groupGradeUp(context, vo, grid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_USER_GRADE_UP:// 用户升级
				{
					userGradeUp(context, vo);
					break;
				}
				case MsgsConstants.MESSAGE_OP_GUEST_BIND_CPN:// 游客绑定手机消息
				{
					Intent intent = new Intent(context, BundPhoneActivity.class);
					context.startActivity(intent);
					break;
				}
				case MsgsConstants.MESSAGE_OP_DO_TASK:// 用户立刻做任务消息
				{
					Intent intent = new Intent(context, PointTaskDetailActivity.class);
					context.startActivity(intent);
					break;
				}
				case MsgsConstants.MESSAGE_OP_USER_INFO:// 查看个人资料
				{
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					Intent intent = new Intent(context, UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID));
					intent.putExtras(bundle);
					context.startActivity(intent);
					break;
				}
				case MsgsConstants.MESSAGE_OP_JOIN_SERVERGROUP_MSG:// 打开服务器公会
				{
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					Intent intent = new Intent(context, GroupChatFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID));
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					context.startActivity(intent);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_CREATE: {
					jumpMainPlayList(0);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_APPR_OK: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpPlayDetail(playid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_APPR_NO: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpPlayDetail(playid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_EVAL_STAR: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpPlayDetail(playid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_CREATE_ORDER: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpApplyAplayManage(playid, MsgsConstants.PLAY_ORDER_STATUS_INIT);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_CONFIRMED_OK: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpApplyAplayManage(playid, MsgsConstants.PLAY_ORDER_STATUS_PAY);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_REMIND_START: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)
							|| !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_STATUS)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					int status = jsonparam.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_STATUS);
					jumpApplyAplayManage(playid, status);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_NOT_CONFIRMED: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpApplyAplayManage(playid, MsgsConstants.PLAY_ORDER_STATUS_END);
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_PAY: {
					jumpUCoinDetails();
					break;
				}
				case MsgsConstants.MESSAGE_OP_PLAY_EVAL: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpPlayComments(playid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_ORDER_CREATE: {
					jumpMainPlayList(1);
					break;
				}
				case MsgsConstants.MESSAGE_OP_ORDER_CONFIRMED_OK: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpPlayDetail(playid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_ORDER_REMIND_START: {
					jumpMainPlayList(1);
					break;
				}
				case MsgsConstants.MESSAGE_OP_ORDER_AUTO_REFUND: {
					jumpUCoinDetails();
					break;
				}
				case MsgsConstants.MESSAGE_OP_ORDER_PAY: {
					jumpUCoinDetails();
					break;
				}
				case MsgsConstants.MESSAGE_OP_ORDER_AUTO_PAY: {
					jumpUCoinDetails();
					break;
				}
				case MsgsConstants.MESSAGE_OP_ORDER_APPEAL_OK: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpPlayDetail(playid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_ORDER_APPEAL_FAIL: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpPlayDetail(playid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_ADMIN_CLOSE_PLAY:
				case MsgsConstants.MESSAGE_OP_USER_PLAY_CLOSED: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long uid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
					jumpUserChat(uid);
					break;
				}
				case MsgsConstants.MESSAGE_OP_USER_PLAY_OPEN: {
					jumpMainPlayList(0);
					break;
				}
				case MsgsConstants.MESSAGE_OP_ADMIN_PUBLISH_PLAY: {
					if (jsonparam == null || !jsonparam.has(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID)) {
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
						break;
					}
					long playid = jsonparam.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PLAYID);
					jumpPlayDetail(playid);
					break;
				}
				default: {
					ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_UNDEFINED, null);
				}

				}
			} catch (Exception ex) {
				LogUtil.e(TAG, ex.getMessage());
			}
		}
	}

	/**
	 * 系统推荐贴吧
	 * 
	 * @param context
	 */
	private void systemRecommendGame(Context context, final MessageVo vo) {

		// 更新消息
		String content = vo.getContent();
		content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_RECOMMEND_GAME,
				context.getString(R.string.message_action_recommend_game_update));
		vo.setContent(content);
		ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
		ChatMessageAdapter.this.notifyDataSetChanged();

		// 跳转到推荐贴吧列表界面
		Intent intent = new Intent(context, GameListActivity.class);
		Bundle bundle = new Bundle();
		// 窗口标题
		bundle.putString(BaseActivity.TITLE, "推荐贴吧");
		// 是否显示左边
		bundle.putBoolean(BaseActivity.IS_SHOW_LEFT, true);
		// 是否显示右边
		bundle.putBoolean(BaseActivity.IS_SHOW_RIGHT, true);
		// 是否显示TOP搜索框内容
		bundle.putBoolean(BaseActivity.VISIBLE_TOP_MODE, false);
		// 列表显示类型
		bundle.putInt(BaseActivity.MODE, CommonGameAdapter.MODE_RECOMMEND);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 系统推荐公会
	 * 
	 * @param context
	 */
	private void systemRecommendGroup(Context context, final MessageVo vo) {
		// 更新消息
		String content = vo.getContent();
		content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_RECOMMEND_GROUP,
				context.getString(R.string.message_action_recommend_group_update));
		vo.setContent(content);
		ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
		ChatMessageAdapter.this.notifyDataSetChanged();
		// 跳转到推荐公会列表界面
		Intent intent = new Intent(context, GroupListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, GroupAdapter2.MODE_RECOMMEND_GROUP);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 系统推荐用户
	 * 
	 * @param context
	 */
	private void systemRecommendUser(Context context, final MessageVo vo) {
		// 更新消息
		String content = vo.getContent();
		content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_RECOMMEND_USER,
				context.getString(R.string.message_action_recommend_user_update));
		vo.setContent(content);
		ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
		ChatMessageAdapter.this.notifyDataSetChanged();
		// 跳转到推荐好友列表界面
		Intent intent = new Intent(context, UserListActicity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TYPE, UserAdapter2.TYPE_RECOMMEND);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 公会管理员快捷审批用户加入公会
	 * 
	 * @param context
	 * @param grid
	 * @param uid
	 */
	private void groupManagerApproveMember(final Context context, final MessageVo vo, final long grid, final long uid, long seq) {
		if (grid != 0 && uid != 0) {
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
			dialog.show();
			// 点击批准按钮
			ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					switch (result) {
					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
						ToastUtil.showToast(context, context.getString(R.string.message_action_approvemember_ok));

						UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_GROUP_APPROVE, String.valueOf(grid), null, String.valueOf(uid), null, true);

						String content = vo.getContent();
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_APPROVE_MEMBER,
								context.getString(R.string.message_action_approvemember_update));
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_IGNORE_USER, "");
						vo.setContent(content);
						ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
						ChatMessageAdapter.this.notifyDataSetChanged();

						break;
					default:
						UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_GROUP_APPROVE, String.valueOf(grid), null, String.valueOf(uid), null, false);
						ErrorCodeUtil.handleErrorCode(context, result, null);
						break;
					}
					dialog.dismiss();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_GROUP_APPROVE, String.valueOf(grid), null, String.valueOf(uid), null, false);
					switch (result) {

					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_GROUP_ALREADY_IN_VALUE:
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_GROUPMANAGER_APPROVE_MEMBER, null);
						break;
					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE:
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_GROUPMANAGER_APPROVE_MEMBER_OVER_COUNT, null);
						break;
					default:
						ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
						break;
					}
				}
			}, context, grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_APPROVE_MEMBER, String.valueOf(uid), null, seq, null);
		} else {
			// 参数错误
			ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
		}
	}

	// MESSAGE_OP_IGNORE_USER

	/**
	 * 公会管理员忽略用户的申请加入
	 * 
	 * @param context
	 */
	private void groupManagerIgnoreUser(final Context context, final MessageVo vo, final long grid, final long uid, long seq) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					// 更新消息
					String content = vo.getContent();
					content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_APPROVE_MEMBER, "");
					content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_IGNORE_USER,
							context.getString(R.string.message_action_ignore_user));
					vo.setContent(content);
					ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
					ChatMessageAdapter.this.notifyDataSetChanged();
					break;
				default:
					ErrorCodeUtil.handleErrorCode(context, result, "忽略失败");
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(context, result, "忽略失败");
				dialog.dismiss();
			}

		}, context, grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_IGNORE_GROUP_APPLY, String.valueOf(uid), null, null);
	}

	/**
	 * 用户同意加入公会
	 * 
	 * @param context
	 * @param grid
	 */
	private void userAgreeJoinGroup(final Context context, final MessageVo vo, long grid, long seq) {
		if (grid != 0) {

			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
			dialog.show();

			ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					switch (result) {
					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
						ToastUtil.showToast(context, context.getString(R.string.message_action_useragreejoingroup_ok));
						String content = vo.getContent();
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_AGREE_JOIN_GROUP,
								context.getString(R.string.message_action_useragreejoingroup_update));
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_REFUSE_JOIN_GROUP, "");
						vo.setContent(content);
						ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
						ChatMessageAdapter.this.notifyDataSetChanged();
						break;

					default:
						ErrorCodeUtil.handleErrorCode(context, result, null);
						break;
					}
					dialog.dismiss();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					switch (result) {

					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE:
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_USERAGREEJOINGROUP_OVER_COUNT, null);
						break;
					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_GROUP_ALREADY_IN_VALUE:
						ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_USERAGREEJOINGROUP_ALREADY_IN, null);
						break;
					default:
						ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
						break;
					}

				}
			}, context, grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_AGREE_JOIN_GROUP, null, null, seq, null);

		} else {
			// 参数错误
			ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
		}
	}

	/**
	 * 用户拒绝加入公会
	 * 
	 * @param context
	 * @param grid
	 */
	private void userRefuseJoinGroup(final Context context, final MessageVo vo, long grid, long seq) {
		if (grid != 0) {
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
			dialog.show();

			ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					switch (result) {
					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
						ToastUtil.showToast(context, context.getString(R.string.message_action_refusejoingroup_ok));
						String content = vo.getContent();
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_AGREE_JOIN_GROUP, "");
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_REFUSE_JOIN_GROUP,
								context.getString(R.string.message_action_refusejoingroup_update));
						vo.setContent(content);
						ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
						ChatMessageAdapter.this.notifyDataSetChanged();
						break;
					default:
						ErrorCodeUtil.handleErrorCode(context, result, null);
						break;
					}
					dialog.dismiss();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
				}
			}, context, grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_REFUSE_JOIN_GROUP, null, null, seq, null);

		} else {
			// 参数错误
			ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
		}
	}

	/**
	 * 打开贴吧详情页
	 * 
	 * @param context
	 * @param vo
	 * @param gid
	 */
	private void openGameDetail(Context context, MessageVo vo, long gid) {
		// 根据当前的应用配置判断是否要启动游伴
		AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
		if (appconfig != null && appconfig.isRecbarmsg() && gid != appconfig.getGameId()) {
			AppUtil.openGame(context, gid, GameTopicListActivity.class.getName(),
					context.getResources().getString(R.string.postbar_show_game_tip_for_youban_uninstall));
		} else {
			// 跳转到贴吧详情页界面
			Intent intent = new Intent(context, GameTopicListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			context.startActivity(intent);
		}
	}

	/**
	 * 忽略推广用户的关注
	 * 
	 * @param context
	 */
	private void ignoreFollowUser(Context context, MessageVo vo) {
		String content = vo.getContent();
		content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_PROMOTION_USER_FOLLOW, "");
		content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_PROMOTION_USER_IGNORE,
				context.getString(R.string.message_action_ignore_user));
		vo.setContent(content);
		ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
		ChatMessageAdapter.this.notifyDataSetChanged();
	}

	/**
	 * 消息中快捷关注某个用户
	 * 
	 * @param context
	 * @param vo
	 * @param uid
	 */
	private void followUser(final Context context, final MessageVo vo, long uid) {
		if (uid != 0) {

			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(context);
			dialog.show();

			ProxyFactory.getInstance().getUserProxy().addFollowUser(new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					if (result == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE) {

						String content = vo.getContent();
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_PROMOTION_USER_FOLLOW,
								context.getString(R.string.message_action_follow_user_update));
						content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_PROMOTION_USER_IGNORE, "");
						vo.setContent(content);
						ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
						ChatMessageAdapter.this.notifyDataSetChanged();

					} else {
						ToastUtil.showToast(context, "关注失败");
					}
					dialog.dismiss();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
				}
			}, context, uid, false);

		} else {
			// 参数错误
			ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENY_MESSAGE_ACTION_PARAMERROE, null);
		}
	}

	/**
	 * 快捷 公会升级
	 * 
	 * @param context
	 * @param vo
	 * @param grid
	 */
	private void groupGradeUp(final Context context, final MessageVo vo, final long grid) {
		Intent intent = new Intent(context, GroupGradePolicyActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 快捷用户升级
	 * 
	 * @param context
	 * @param vo
	 */
	private void userGradeUp(final Context context, final MessageVo vo) {
		Intent intent = new Intent(context, UserGradePolicyActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 我的陪玩-我创建的
	 */
	private void jumpMainPlayList(int index) {
		Intent intent = new Intent(context, MainPlayListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_INDEX, index);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 陪玩详情
	 * 
	 * @param playid
	 */
	private void jumpPlayDetail(long playid) {
		Intent intent = new Intent(context, PlayDetailInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, playid);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 跳转到用户聊天
	 * 
	 * @param uid
	 */
	private void jumpUserChat(long uid) {
		Intent intent = new Intent(context, UserChatFragmentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOUSERID, uid);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		context.startActivity(intent);
	}

	/**
	 * 报名管理-待确认 已确认
	 * 
	 * @param playid
	 * @param status
	 */
	private void jumpApplyAplayManage(final long playid, final int status) {
		ProxyFactory.getInstance().getPlayProxy().searchPlayDetailInfo(new ProxyCallBack<Msgs.PlayInfo>() {

			@Override
			public void onSuccess(PlayInfo result) {
				int pStatus = 0;
				int uStatus = 0;
				String gameName = null;
				if (result != null) {
					pStatus = result.getStatus();
					uStatus = result.getUserPlayStatus();
					gameName = result.getGamename();
				}
				jumpApplyAplayManage(playid, status, pStatus, uStatus, gameName);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				jumpApplyAplayManage(playid, status, 0, 0, null);
			}
		}, context, playid, "1");

	}

	/**
	 * 报名管理-待确认 已确认
	 * 
	 * @param playid
	 * @param status
	 */
	private void jumpApplyAplayManage(long playid, int status, int pStatus, int uStatus, String gameName) {
		Intent intent = new Intent(context, PlayManageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, playid);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_STATUS, status);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PSTATUS, pStatus);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_USTATUS, uStatus);
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, gameName);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * 我的U币-更多-U币明细
	 */
	private void jumpUCoinDetails() {
		Intent intent = new Intent(context, PayDetailInfoActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 我发布的陪玩详情-最新评论
	 */
	private void jumpPlayComments(long playid) {
		Intent intent = new Intent(context, PlayAllCommentInfoActivity.class);
		intent.putExtra("pid", playid);
		context.startActivity(intent);
	}

}
