/**      
 * NewsMainAdapter.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.message.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.setting.vo.ChannelGroupVo;
import com.iwgame.msgs.module.setting.vo.ChannelVo;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.MessageContent;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MessageUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.MyTagUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageExt;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.TextUtil;
import com.iwgame.widget.slideitemlistview.SlideItemView;
import com.iwgame.widget.slideitemlistview.SlideItemView.OnSlideListener;
import com.youban.msgs.R;

//import com.iwgame.msgs.common.ImageLoader;

/**
 * 
 * @ClassName: NewsMainAdapter
 * @Description: TODO(动态首页list的adapter )
 * @author 吴禹青
 * @date 2013-10-18 下午2:00:41
 * @Version 1.0
 * 
 */
public class MessageAdapter2 extends BaseAdapter {

	private static final String TAG = "NewsMainAdapter";
	private Context context;
	private LayoutInflater inflater;
	private List<MessageVo> data;

	public static int ACTION_DEL = 1;

	private SlideItemView mLastSlideItemViewWithStatusOn;

	private ExtUserVo loginUserVo;

	public interface ItemClickListener2 {
		public void onClickAction(int position, int action);
	}

	private ItemClickListener2 itemClickListener = null;

	private Map<String, Object> subject_cache = new HashMap<String, Object>();
	private GroupDao groupdao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
	private UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
	private GroupUserRelDao grelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());

	public MessageAdapter2(Context context, List<MessageVo> data, ItemClickListener2 itemClickListener2) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		itemClickListener = itemClickListener2;
		loginUserVo = SystemContext.getInstance().getExtUserVo();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (data != null && position < data.size()) {
			final MessageVo vo = data.get(position);
			ViewHolder tmp_viewHolder = null;

			// ViewHolder holder;
			SlideItemView slideItemView = (SlideItemView) convertView;
			if (slideItemView == null) {
				View itemView = this.inflater.inflate(R.layout.fragment_news_content_item2, null);

				slideItemView = new SlideItemView(context);
				convertView = slideItemView;

				slideItemView.setContentView(itemView);

				tmp_viewHolder = new ViewHolder();

				tmp_viewHolder.news_main_img = (ImageView) convertView.findViewById(R.id.icon);
				tmp_viewHolder.news_unreadcount = (TextView) convertView.findViewById(R.id.news_unreadcount);
				tmp_viewHolder.news_main_name = (TextView) convertView.findViewById(R.id.news_main_name);
				tmp_viewHolder.news_time = (TextView) convertView.findViewById(R.id.news_time);
				tmp_viewHolder.news_content = (TextView) convertView.findViewById(R.id.news_content);
				tmp_viewHolder.news_action_del = (View) convertView.findViewById(R.id.slideitem_item_holder);
				tmp_viewHolder.news_baseInfo = (View) convertView.findViewById(R.id.news_baseInfo);
				tmp_viewHolder.grade = (TextView) convertView.findViewById(R.id.grade);
				tmp_viewHolder.age = (TextView) convertView.findViewById(R.id.age);

				slideItemView.setOnSlideListener(new OnSlideListener() {

					@Override
					public void onSlide(View view, int status) {
						if (mLastSlideItemViewWithStatusOn != null && mLastSlideItemViewWithStatusOn != view) {
							mLastSlideItemViewWithStatusOn.shrink();
						}
						if (status == SLIDE_STATUS_ON || status == SLIDE_STATUS_START_SCROLL) {
							mLastSlideItemViewWithStatusOn = (SlideItemView) view;
						} else {
							mLastSlideItemViewWithStatusOn = null;
						}
					}
				});
				slideItemView.setTag(tmp_viewHolder);
			} else {
				tmp_viewHolder = (ViewHolder) slideItemView.getTag();
			}
			slideItemView.setmIndex(position);
			slideItemView.scrollToInit();

			final ViewHolder viewHolder = tmp_viewHolder;
			viewHolder.news_baseInfo.setVisibility(View.INVISIBLE);
			// tmp_viewHolder.front.setFocusable(false);
			// tmp_viewHolder.front.setPressed(false);

			LogUtil.d(TAG, String.format("----getView,[%d][convertView = %s][news_content = %s ][vo = %s]", position, convertView,
					viewHolder.news_content, vo.toString()));

			// 开始设置值
			viewHolder.news_time.setText(SafeUtils.getDate2MyStr(vo.getCreateTime()));
			// new ImageLoader().loadRes("drawable://" +
			// R.drawable.common_default_icon, 0, viewHolder.news_main_img,
			// R.drawable.common_default_icon);
			viewHolder.news_main_name.setText("");
			viewHolder.news_content.setText("");
			viewHolder.news_unreadcount.setText("");
			viewHolder.news_unreadcount.setVisibility(View.INVISIBLE);
			int unreadCount = 0;
			unreadCount = vo.getUnReadCount();
			ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_user_icon_default,
					R.drawable.common_user_icon_default, R.drawable.common_user_icon_default, null, true);
			// if(vo.getSubjectId() == -1)
			// {
			// //采用汇总
			// unreadCount =
			// SystemContext.getInstance().getUserSharedPreferencesSubjectUnreadMessageCount(vo.getChannelType(),
			// vo.getSubjectDomain(), vo.getCategory());
			// }
			// else
			// {
			// unreadCount =
			// SystemContext.getInstance().getSubjectUnReadCount(vo.getChannelType(),
			// vo.getSubjectId(), vo.getSubjectDomain(), vo.getCategory());
			// }
			// 设置为读数
			if (unreadCount > 0) {
				if (vo.getSubjectId() == -1 && vo.getChannelType().equals(MsgsConstants.MC_MCHAT)
						&& vo.getSubjectDomain().equals(MsgsConstants.DOMAIN_GROUP) && vo.getCategory().equals(MsgsConstants.MCC_CHAT)) {
					// 合并的公会
					viewHolder.news_unreadcount.setText("...");
					viewHolder.news_unreadcount.setVisibility(View.VISIBLE);
					viewHolder.news_unreadcount.setBackgroundResource(R.drawable.news_count_bg);
				} else {
					if (unreadCount > 99) {
						viewHolder.news_unreadcount.setText("99+");
						viewHolder.news_unreadcount.setBackgroundResource(R.drawable.news_count_bg);
					} else {
						viewHolder.news_unreadcount.setText(Integer.toString(unreadCount));
						viewHolder.news_unreadcount.setBackgroundResource(R.drawable.news_count_bg);
					}
					viewHolder.news_unreadcount.setVisibility(View.VISIBLE);
				}
			} else {
				viewHolder.news_unreadcount.setVisibility(View.INVISIBLE);
			}
			final Paint paint = new Paint();
			paint.setTextSize(viewHolder.news_content.getTextSize());
			paint.setTextScaleX(viewHolder.news_content.getTextScaleX());
			if (vo.getSummary() == null)
				vo.setSummary("");

			// 获得配置文件的规则，判断展示
			Object showRule = MessageUtil.getShowRuleByMessage(SystemContext.getInstance().getChannelsShowRule(), vo);

			if (vo.getChannelType().equals(MsgsConstants.MC_CHAT))// chat
			{
				viewHolder.news_baseInfo.setVisibility(View.VISIBLE);
				String summary = vo.getSummary();
				if (summary.contains(MyTagUtil.TAG_FOLLOW)) {
					summary = "";
				}
				// 对聊
				viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, summary, (int) TextUtil.getTextHeight(paint),
						(int) TextUtil.getTextHeight(paint)));

				if (subject_cache.containsKey(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getSubjectId()))) {
					UserVo result = (UserVo) subject_cache.get(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getSubjectId()));
					// new
					// ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()),
					// 0, viewHolder.news_main_img,
					// R.drawable.common_user_icon_default);
					ImageCacheLoader.getInstance()
							.loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), viewHolder.news_main_img, R.drawable.common_user_icon_default,
									R.drawable.common_user_icon_default, R.drawable.common_user_icon_default, null, true);
					viewHolder.news_main_name.setText(result.getUsername());
					setUserInfo(context, viewHolder, result);
				} else {
					ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_user_icon_default,
							R.drawable.common_user_icon_default, R.drawable.common_user_icon_default, null, true);
					// 获得用户信息
					ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

						@Override
						public void onSuccess(List<UserVo> uservoresult) {
							if (uservoresult == null || uservoresult.size() <= 0)
								return;
							UserVo result = uservoresult.get(0);
							// TODO Auto-generated method stub
							if (result != null) {
								ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), viewHolder.news_main_img,
										R.drawable.common_user_icon_default, R.drawable.common_user_icon_default,
										R.drawable.common_user_icon_default, null, true);
								// 数据库获取用户信息（备注）
								UserVo uvo = dao.getUserByUserId(result.getUserid());
								if (uvo != null) {
									String rname = uvo.getRemarksName();
									if (rname != null && !rname.isEmpty()) {
										viewHolder.news_main_name.setText(rname);
										result.setUsername(rname);
									} else {
										viewHolder.news_main_name.setText(result.getUsername());
									}
									setUserInfo(context, viewHolder, uvo);
								} else {
									viewHolder.news_main_name.setText(result.getUsername());
									setUserInfo(context, viewHolder, result);
								}
								subject_cache.put(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getSubjectId()), result);
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							// TODO Auto-generated method stub
							LogUtil.e(TAG, "获得用户信息异常：" + result);
							// ErrorCodeUtil.handleErrorCode(context, result);
							// new ImageLoader().loadRes("drawable://" +
							// R.drawable.common_user_icon_default, 0,
							// viewHolder.news_main_img,
							// R.drawable.common_user_icon_default);
							ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_user_icon_default,
									R.drawable.common_user_icon_default, R.drawable.common_user_icon_default, null, true);
							viewHolder.news_main_name.setText("");
						}

					};
					ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
					ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
					cdp.setId(vo.getSubjectId());
					UserVo uvo = dao.getUserById(vo.getSubjectId());
					cdp.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
					p.addParam(cdp.build());
					ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, context, p.build(), 0, null);
				}

			} else if (vo.getChannelType().equals(MsgsConstants.MC_PUB) && vo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)
					&& vo.getSubjectDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
				// 公会会长群发消息
				if (vo.getSubjectId() == -1) {
					// 合并的
					setNameAndIconByRule(showRule, viewHolder);

					if (subject_cache.containsKey(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()))) {
						GroupVo result = (GroupVo) subject_cache.get(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()));
						String tmp_content = context.getString(R.string.news_group_content_format, result.getName(), vo.getSummary());
						viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content, (int) TextUtil.getTextHeight(paint),
								(int) TextUtil.getTextHeight(paint)));

					} else {
						// 获得公会
						ProxyCallBack<List<GroupVo>> callback = new ProxyCallBack<List<GroupVo>>() {

							@Override
							public void onSuccess(List<GroupVo> groupvoresult) {
								// TODO Auto-generated method stub
								if (groupvoresult == null || groupvoresult.size() <= 0)
									return;
								GroupVo result = groupvoresult.get(0);
								if (result != null) {
									String tmp_content = context.getString(R.string.news_group_content_format, result.getName(), vo.getSummary());
									viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content,
											(int) TextUtil.getTextHeight(paint), (int) TextUtil.getTextHeight(paint)));
									subject_cache.put(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()), result);
								}
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								// TODO Auto-generated method stub
								LogUtil.e(TAG, "获得公会信息异常：" + result);
								ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
								viewHolder.news_content.setText("");
							}

						};
						ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
						ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
						cdp.setId(vo.getToId());
						GroupVo gvo = groupdao.findGroupByGrid(vo.getToId());
						cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
						p.addParam(cdp.build());
						ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback, context, p.build(), MsgsConstants.OT_GROUP, null);
					}
				} else {
					if (subject_cache.containsKey(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()))) {
						GroupVo result = (GroupVo) subject_cache.get(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()));
						viewHolder.news_main_name.setText(context.getString(R.string.news_group_name_format, result.getName()));
						String tmp_content = context.getString(R.string.news_group_content_format2, vo.getSummary());
						viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content, (int) TextUtil.getTextHeight(paint),
								(int) TextUtil.getTextHeight(paint)));
						ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), viewHolder.news_main_img,
								R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
					} else {
						ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_default_icon,
								R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
						// 获得公会
						ProxyCallBack<List<GroupVo>> callback = new ProxyCallBack<List<GroupVo>>() {

							@Override
							public void onSuccess(List<GroupVo> groupvoresult) {
								// TODO Auto-generated method stub
								if (groupvoresult == null || groupvoresult.size() <= 0)
									return;
								GroupVo result = groupvoresult.get(0);
								if (result != null) {
									viewHolder.news_main_name.setText(context.getString(R.string.news_group_name_format, result.getName()));
									String tmp_content = context.getString(R.string.news_group_content_format2, vo.getSummary());
									viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content,
											(int) TextUtil.getTextHeight(paint), (int) TextUtil.getTextHeight(paint)));
									ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), viewHolder.news_main_img,
											R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null,
											true);
									subject_cache.put(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()), result);
								} else {
									viewHolder.news_main_name.setText("");
									viewHolder.news_content.setText("");
									ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_default_icon,
											R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
								}
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								// TODO Auto-generated method stub
								LogUtil.e(TAG, "获得公会信息异常：" + result);
								ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
								viewHolder.news_main_name.setText("");
								viewHolder.news_content.setText("");
								ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_default_icon,
										R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
							}

						};
						ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
						ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
						cdp.setId(vo.getToId());
						GroupVo gvo = groupdao.findGroupByGrid(vo.getToId());
						cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
						p.addParam(cdp.build());
						ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback, context, p.build(), MsgsConstants.OT_GROUP, null);
					}
				}

			} else if (vo.getChannelType().equals(MsgsConstants.MC_MCHAT)) {
				// 公会聊天室消息
				if (vo.getSubjectId() == -1) {
					// 合并的
					setNameAndIconByRule(showRule, viewHolder);
					if (subject_cache.containsKey(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()))) {
						GroupVo result = (GroupVo) subject_cache.get(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()));
						String tmp_content = context.getString(R.string.news_groupchat_content_format, result.getName(), vo.getSummary());
						viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content, (int) TextUtil.getTextHeight(paint),
								(int) TextUtil.getTextHeight(paint)));
					} else {
						// 获得公会
						ProxyCallBack<List<GroupVo>> callback = new ProxyCallBack<List<GroupVo>>() {

							@Override
							public void onSuccess(List<GroupVo> groupvoresult) {
								// TODO Auto-generated method stub
								if (groupvoresult == null || groupvoresult.size() <= 0)
									return;
								GroupVo result = groupvoresult.get(0);
								if (result != null) {
									String tmp_content = context.getString(R.string.news_groupchat_content_format, result.getName(), vo.getSummary());
									viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content,
											(int) TextUtil.getTextHeight(paint), (int) TextUtil.getTextHeight(paint)));
									subject_cache.put(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()), result);
								}
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								// TODO Auto-generated method stub
								LogUtil.e(TAG, "获得公会信息异常：" + result);
								ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
								viewHolder.news_content.setText("");
							}

						};
						ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
						ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
						cdp.setId(vo.getToId());
						GroupVo gvo = groupdao.findGroupByGrid(vo.getToId());
						cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
						p.addParam(cdp.build());
						ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback, context, p.build(), MsgsConstants.OT_GROUP, null);
					}
				} else {

					if (subject_cache.containsKey(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()))) {
						GroupVo result = (GroupVo) subject_cache.get(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()));
						viewHolder.news_main_name.setText(context.getString(R.string.news_groupchat_name_format, result.getName()));
						// new
						// ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()),
						// 0, viewHolder.news_main_img,
						// R.drawable.common_default_icon);
						ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), viewHolder.news_main_img,
								R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
						if (vo.getFromDomain().equals(MsgsConstants.DOMAIN_PLATFORM)) {
							// 公会系统消息
							String tmp_content = "";
							MessageExt messageExt = MessageUtil.buildMessageExt(vo);
							if (messageExt != null && loginUserVo != null && messageExt.getContent() != null
									&& messageExt.getOp() == MsgsConstants.MESSAGE_OP_JOINGROUP_MSG
									&& messageExt.getContent().getUid() == loginUserVo.getUserid() && messageExt.getContent().getMsg() != null) {
								tmp_content = messageExt.getContent().getMsg();
							} else {
								tmp_content = vo.getSummary();
							}
							viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content, (int) TextUtil.getTextHeight(paint),
									(int) TextUtil.getTextHeight(paint)));
						} else if (vo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
							// 再去获得用户
							if (subject_cache.containsKey(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()))) {
								UserVo result2 = (UserVo) subject_cache.get(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()));
								String name = result2.getUsername();
								if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
									GroupUserRelVo grelvo = grelDao.findUsers(vo.getToId(), result2.getUserid());
									if (grelvo != null && grelvo.getRemark() != null && !grelvo.getRemark().isEmpty()) {
										name = grelvo.getRemark();
									}
								}
								String tmp_content = context.getString(R.string.news_groupchat_content_format2, name, vo.getSummary());
								viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content, (int) TextUtil.getTextHeight(paint),
										(int) TextUtil.getTextHeight(paint)));

							} else {
								ProxyCallBack<List<UserVo>> callback2 = new ProxyCallBack<List<UserVo>>() {
									@Override
									public void onSuccess(List<UserVo> uservoresult2) {
										// TODO Auto-generated method stub
										if (uservoresult2 == null || uservoresult2.size() <= 0)
											return;
										UserVo result2 = uservoresult2.get(0);
										if (result2 != null) {
											// 数据库获取用户信息（备注）
											UserVo uvo = dao.getUserByUserId(result2.getUserid());
											if (uvo != null) {
												String rname = uvo.getRemarksName();
												if (rname != null && !rname.isEmpty()) {
													result2.setUsername(rname);
												}
											}
											String name = result2.getUsername();
											if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
												GroupUserRelVo grelvo = grelDao.findUsers(vo.getToId(), result2.getUserid());
												if (grelvo != null && grelvo.getRemark() != null && !grelvo.getRemark().isEmpty()) {
													name = grelvo.getRemark();
												}
											}
											subject_cache.put(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()), result2);
											String tmp_content = context.getString(R.string.news_groupchat_content_format2, name, vo.getSummary());
											viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content,
													(int) TextUtil.getTextHeight(paint), (int) TextUtil.getTextHeight(paint)));
										} else {
											viewHolder.news_content.setText("");
										}
									}

									@Override
									public void onFailure(Integer result, String resultMsg) {
										// TODO Auto-generated method stub
										LogUtil.e(TAG, "获得用户信息异常：" + result);
										// ErrorCodeUtil.handleErrorCode(context,
										// result);
										viewHolder.news_content.setText("");
									}

								};
								ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
								ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
								cdp.setId(vo.getFromId());
								UserVo uvo = dao.getUserById(vo.getFromId());
								cdp.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
								p.addParam(cdp.build());
								ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback2, context, p.build(), 0, null);
							}
						}
					} else {
						ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_default_icon,
								R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
						// 获得公会
						ProxyCallBack<List<GroupVo>> callback = new ProxyCallBack<List<GroupVo>>() {

							@Override
							public void onSuccess(List<GroupVo> groupvoresult) {
								// TODO Auto-generated method stub
								if (groupvoresult == null || groupvoresult.size() <= 0)
									return;
								GroupVo result = groupvoresult.get(0);
								if (result != null) {
									subject_cache.put(MsgsConstants.DOMAIN_GROUP + String.valueOf(vo.getToId()), result);

									viewHolder.news_main_name.setText(context.getString(R.string.news_groupchat_name_format, result.getName()));
									ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), viewHolder.news_main_img,
											R.drawable.common_default_icon, R.drawable.common_default_icon, R.drawable.common_default_icon, null,
											true);
									if (vo.getFromDomain().equals(MsgsConstants.DOMAIN_PLATFORM)) {
										// 公会系统消息
										String tmp_content = "";
										MessageExt messageExt = MessageUtil.buildMessageExt(vo);
										if (messageExt != null && loginUserVo != null && messageExt.getContent() != null
												&& messageExt.getOp() == MsgsConstants.MESSAGE_OP_JOINGROUP_MSG
												&& messageExt.getContent().getUid() == loginUserVo.getUserid() && messageExt.getContent().getMsg() != null) {
											tmp_content = messageExt.getContent().getMsg();
										} else {
											tmp_content = vo.getSummary();
										}
										viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content,
												(int) TextUtil.getTextHeight(paint), (int) TextUtil.getTextHeight(paint)));
									} else if (vo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
										// 再去获得用户
										if (subject_cache.containsKey(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()))) {
											UserVo result2 = (UserVo) subject_cache.get(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()));
											String name = result2.getUsername();
											if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
												GroupUserRelVo grelvo = grelDao.findUsers(vo.getToId(), result2.getUserid());
												if (grelvo != null && grelvo.getRemark() != null && !grelvo.getRemark().isEmpty()) {
													name = grelvo.getRemark();
												}
											}
											String tmp_content = context.getString(R.string.news_groupchat_content_format2, name, vo.getSummary());
											viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content,
													(int) TextUtil.getTextHeight(paint), (int) TextUtil.getTextHeight(paint)));

										} else {
											ProxyCallBack<List<UserVo>> callback2 = new ProxyCallBack<List<UserVo>>() {
												@Override
												public void onSuccess(List<UserVo> uservoresult2) {
													if (uservoresult2 == null || uservoresult2.size() <= 0)
														return;
													UserVo result2 = uservoresult2.get(0);
													if (result2 != null) {

														// 数据库获取用户信息（备注）
														UserVo uvo = dao.getUserByUserId(result2.getUserid());
														if (uvo != null && uvo.getRemarksName() != null && !uvo.getRemarksName().isEmpty()) {
															result2.setUsername(uvo.getRemarksName());
														}
														String name = result2.getUsername();
														if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
															GroupUserRelVo grelvo = grelDao.findUsers(vo.getToId(), result2.getUserid());
															if (grelvo != null && grelvo.getRemark() != null && !grelvo.getRemark().isEmpty()) {
																name = grelvo.getRemark();
															}
														}
														subject_cache.put(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()), result2);
														String tmp_content = context.getString(R.string.news_groupchat_content_format2, name,
																vo.getSummary());
														viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content,
																(int) TextUtil.getTextHeight(paint), (int) TextUtil.getTextHeight(paint)));
													} else {
														viewHolder.news_content.setText("");
													}
												}

												@Override
												public void onFailure(Integer result, String resultMsg) {
													// TODO Auto-generated
													// method
													// stub
													LogUtil.e(TAG, "获得用户信息异常：" + result);
													// ErrorCodeUtil.handleErrorCode(context,
													// result);
													viewHolder.news_content.setText("");
												}

											};
											ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
											ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
											cdp.setId(vo.getFromId());
											UserVo uvo = dao.getUserById(vo.getFromId());
											cdp.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
											p.addParam(cdp.build());
											ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback2, context, p.build(), 0, null);
										}
									}

								} else {
									viewHolder.news_main_name.setText("");
									viewHolder.news_content.setText("");
									// new ImageLoader().loadRes("drawable://" +
									// R.drawable.common_default_icon, 0,
									// viewHolder.news_main_img,
									// R.drawable.common_default_icon);
									ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_default_icon,
											R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
								}
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								// TODO Auto-generated method stub
								LogUtil.e(TAG, "获得公会信息异常：" + result);
								ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
								viewHolder.news_main_name.setText("");
								viewHolder.news_content.setText("");
								// new ImageLoader().loadRes("drawable://" +
								// R.drawable.common_default_icon, 0,
								// viewHolder.news_main_img,
								// R.drawable.common_default_icon);
								ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_default_icon,
										R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
							}

						};
						ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
						ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
						cdp.setId(vo.getToId());
						GroupVo gvo = groupdao.findGroupByGrid(vo.getToId());
						cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
						p.addParam(cdp.build());
						ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback, context, p.build(), MsgsConstants.OT_GROUP, null);
					}
				}

			}

			else if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && vo.getCategory().equals(MsgsConstants.MCC_COMMENT)) {
				// 回复我的
				// new ImageLoader().loadRes("drawable://" +
				// R.drawable.news_icon_gymy, 0, viewHolder.news_main_img,
				// R.drawable.common_default_icon);
				//
				// viewHolder.news_main_name.setText(R.string.news_gymy_name);
				setNameAndIconByRule(showRule, viewHolder);

				if (subject_cache.containsKey(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()))) {
					UserVo result = (UserVo) subject_cache.get(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()));
					String tmp_content = context.getString(R.string.news_comment_content_format, result.getUsername(), vo.getSummary());
					viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content, (int) TextUtil.getTextHeight(paint),
							(int) TextUtil.getTextHeight(paint)));

				} else {
					// 获得用户信息
					ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

						@Override
						public void onSuccess(List<UserVo> uservoresult2) {
							if (uservoresult2 == null || uservoresult2.size() <= 0)
								return;
							UserVo result = uservoresult2.get(0);
							if (result != null) {
								// 数据库获取用户信息（备注）
								UserVo uvo = dao.getUserByUserId(result.getUserid());
								if (uvo != null) {
									String rname = uvo.getRemarksName();
									if (rname != null && !rname.isEmpty()) {
										result.setUsername(rname);
									}
								}
								String tmp_content = context.getString(R.string.news_comment_content_format, result.getUsername(), vo.getSummary());
								viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, tmp_content, (int) TextUtil.getTextHeight(paint),
										(int) TextUtil.getTextHeight(paint)));
								subject_cache.put(MsgsConstants.DOMAIN_USER + String.valueOf(vo.getFromId()), result);
							}

						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							// TODO Auto-generated method stub
							LogUtil.e(TAG, "获得用户信息异常：" + result);
							// ErrorCodeUtil.handleErrorCode(context, result);
							viewHolder.news_content.setText("");
						}

					};
					ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
					ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
					cdp.setId(vo.getFromId());
					UserVo uvo = dao.getUserById(vo.getFromId());
					cdp.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
					p.addParam(cdp.build());
					ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, context, p.build(), 0, null);
				}

			} else {
				setNameAndIconByRule(showRule, viewHolder);
				viewHolder.news_content.setText(SmileyUtil.ReplaceSmiley(context, MyTagUtil.getMessageShowContent(vo.getSummary()), (int) TextUtil.getTextHeight(paint),
						(int) TextUtil.getTextHeight(paint)));
			}

			// 设置item删除操作
			viewHolder.news_action_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (itemClickListener != null) {
						itemClickListener.onClickAction(position, ACTION_DEL);
					}
				}
			});
		}
		return convertView;
	}

	private void setUserInfo(Context context, ViewHolder holder, UserVo vo) {
		int age = vo.getAge();
		int sex = vo.getSex();
		int grade = vo.getGrade();
		if (age > 0) {
			holder.age.setText(AgeUtil.convertAgeByBirth((Integer) age) + "");
			holder.age.setVisibility(View.VISIBLE);
			if (sex >= 0) {
				if (sex == 0) {
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_man_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					holder.age.setCompoundDrawables(sexdraw, null, null, null);
					holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
					holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				} else if (sex == 1) {
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_woman_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					holder.age.setCompoundDrawables(sexdraw, null, null, null);
					holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
					holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
				}
			} else {
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
			}
		} else {
			holder.age.setText("");
			holder.age.setCompoundDrawablePadding(0);
			if (sex >= 0) {
				if (sex == 0) {
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_man_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					holder.age.setCompoundDrawables(sexdraw, null, null, null);
					holder.age.setCompoundDrawablePadding(0);
					holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				} else if (sex == 1) {
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_woman_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					holder.age.setCompoundDrawables(sexdraw, null, null, null);
					holder.age.setCompoundDrawablePadding(0);
					holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
				} else {
					holder.age.setCompoundDrawablePadding(0);
					holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
					holder.age.setVisibility(View.GONE);
				}
			} else {
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				holder.age.setVisibility(View.GONE);
			}
		}
		if (grade > 0) {
			holder.grade.setVisibility(View.VISIBLE);
			holder.grade.setText("LV" + grade);
		} else {
			holder.grade.setVisibility(View.GONE);
		}
	}

	/**
	 * 通过规则设置icon和name
	 * 
	 * @param showRule
	 * @param viewHolder
	 */
	private void setNameAndIconByRule(Object showRule, ViewHolder viewHolder) {
		String tmpName = "";
		String tmpIcon = "";
		if (showRule != null) {

			if (showRule instanceof ChannelVo) {
				ChannelVo tmp = (ChannelVo) showRule;
				tmpName = tmp.getName();
				tmpIcon = tmp.getIcon();
			} else if (showRule instanceof ChannelGroupVo) {
				ChannelGroupVo tmp = (ChannelGroupVo) showRule;
				tmpName = tmp.getName();
				tmpIcon = tmp.getIcon();
			}
		}
		// 名称
		if (tmpName != null && !tmpName.isEmpty()) {
			viewHolder.news_main_name.setText(tmpName);
		} else {
			viewHolder.news_main_name.setText("");
		}
		// icon
		if (tmpIcon != null && !tmpIcon.isEmpty()) {
			// new ImageLoader().loadRes(ResUtil.getSmallRelUrl(tmpIcon), 0,
			// viewHolder.news_main_img, R.drawable.common_default_icon);
			ImageCacheLoader.getInstance().loadRes(ResUtil.getSmallRelUrl(tmpIcon), viewHolder.news_main_img, R.drawable.common_default_icon,
					R.drawable.common_default_icon, R.drawable.common_default_icon, null, true);
		} else {
			// new ImageLoader().loadRes("drawable://" +
			// R.drawable.common_default_icon, 0, viewHolder.news_main_img,
			// R.drawable.common_default_icon);
			ImageCacheLoader.getInstance().loadRes(null, viewHolder.news_main_img, R.drawable.common_default_icon, R.drawable.common_default_icon,
					R.drawable.common_default_icon, null, true);
		}

	}

	class ViewHolder {
		ImageView news_main_img;
		TextView news_unreadcount;
		TextView news_main_name;
		TextView news_time;
		TextView news_content;
		View news_action_del;
		View news_baseInfo;
		TextView grade;
		TextView age;
	}

	public SlideItemView getmLastSlideItemViewWithStatusOn() {
		return mLastSlideItemViewWithStatusOn;
	}

	public void setmLastSlideItemViewWithStatusOn(SlideItemView mLastSlideItemViewWithStatusOn) {
		this.mLastSlideItemViewWithStatusOn = mLastSlideItemViewWithStatusOn;
	}

}
