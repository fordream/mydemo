/**      
 * GroupChatUserAdapter.java Create on 2013-10-31     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.popwindow.MsgsPopTextItem;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow;
import com.iwgame.msgs.widget.smiley.SmileyUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.TextUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GroupChatUserAdapter
 * @Description: TODO(公会聊天室公会成员列表界面)
 * @author Administrator
 * @date 2013-10-31 下午7:12:29
 * @Version 1.0
 * 
 */
public class GroupChatUserAdapter extends BaseAdapter {
	private static final String TAG = "GroupChatUserAdapter";
	Context context = null;
	List<UserItemObj> data = null;
	private LayoutInflater inflater;
	GroupUserRelVo currentRel = null;
	UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
	private Map<Long, UserVo> user_cache = new ConcurrentHashMap<Long, UserVo>();
	private ListView listView;
	private boolean flag = true;//p这个布尔值用来判断当前是在滑动还是处于禁止的
	private Map<Long, Boolean> image_cache = new HashMap<Long, Boolean>();//用来判断头像是否已经加载过
	private Map<Long, ViewHolder> viewHolder_cache = new HashMap<Long, GroupChatUserAdapter.ViewHolder>();//用来保存viewholder的map
	

	/**
	 * 构造方法 
	 * @param context
	 * @param data
	 * @param currentRel
	 * @param listView
	 */
	public GroupChatUserAdapter(Context context, List<UserItemObj> data, GroupUserRelVo currentRel,ListView listView) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.currentRel = currentRel;
		this.listView = listView;
		addListener();
	}

	/**
	 * 当滑动停止 的时候 去加载数据
	 */
	private void addListener() {
		this.listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView paramAbsListView, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:
					flag = false;
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					flag = false;
					break;
				case OnScrollListener.SCROLL_STATE_IDLE:
					try {
						int firstvisibleIndex = listView.getFirstVisiblePosition();
						int lastvisibleIndex = listView.getLastVisiblePosition();
						if(firstvisibleIndex > 0) firstvisibleIndex = firstvisibleIndex - 1;
						UserItemObj userItemObj;
						ViewHolder holder;
						long uid = 0;
						for(int i = firstvisibleIndex; i < lastvisibleIndex; i ++){
							userItemObj = data.get(i);
							if(userItemObj == null) continue;
							uid = userItemObj.getUid();
							holder = viewHolder_cache.get(uid);
							if(!user_cache.containsKey(uid))
								getuserDetail(userItemObj, holder);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView paramAbsListView, int paramInt1,
					int paramInt2, int paramInt3) {

			}
		});
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final UserItemObj userItemObj = data.get(position);
		ViewHolder tmpViewHolder = null;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.group_chat_user_list_item3, null);
			tmpViewHolder = new ViewHolder();
			tmpViewHolder.avatar = (ImageView) convertView.findViewById(R.id.icon);
			tmpViewHolder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			tmpViewHolder.sex_age = (TextView) convertView.findViewById(R.id.sex_age);
			tmpViewHolder.date = (TextView) convertView.findViewById(R.id.date);
			tmpViewHolder.date.setVisibility(View.GONE);
			tmpViewHolder.date2 = (TextView) convertView.findViewById(R.id.date2);
			tmpViewHolder.date2.setVisibility(View.GONE);
			tmpViewHolder.message = (TextView) convertView.findViewById(R.id.message);
			tmpViewHolder.tag = (ImageView) convertView.findViewById(R.id.tag);
			tmpViewHolder.action = (ImageView) convertView.findViewById(R.id.action);
			tmpViewHolder.contribute_point_chart = (TextView) convertView.findViewById(R.id.contribute_point_chart);
			tmpViewHolder.user_vip = (TextView) convertView.findViewById(R.id.user_vip);
			convertView.setTag(tmpViewHolder);
		} else {
			tmpViewHolder = (ViewHolder) convertView.getTag();
		}
		final ViewHolder viewHolder = tmpViewHolder;
		showDefaultMsgs(viewHolder);
		LogUtil.debug(String.format("----getView,[%d][convertView = %s]", position, convertView));
		if (userItemObj != null) {
			viewHolder_cache.put(userItemObj.getUid(), viewHolder);
			if (user_cache.containsKey(userItemObj.getUid())) {
				long uid = userItemObj.getUid();
				UserVo result = user_cache.get(uid);
				if(flag || image_cache.containsKey(uid)){
					new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), ImageLoader.RADIUS_DEFAULT_PX6, viewHolder.avatar,
							R.drawable.common_user_icon_default);
					image_cache.put(uid, true);
				}
				if(userItemObj.getRemark() != null && !"".equals(userItemObj.getRemark())){
					viewHolder.nickname.setText(userItemObj.getRemark());
				}else{
					viewHolder.nickname.setText(result.getUsername());
				}
				if (result.getSex() == MsgsConstants.SEX_MAN) {
					viewHolder.sex_age.setBackgroundResource(R.drawable.common_item_jh2_shap);
					Drawable drawable = context.getResources().getDrawable(R.drawable.user_man_icon);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					viewHolder.sex_age.setCompoundDrawables(drawable, null, null, null);
				} else {
					viewHolder.sex_age.setBackgroundResource(R.drawable.common_item_jh_shap);
					Drawable drawable = context.getResources().getDrawable(R.drawable.user_woman_icon);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
					viewHolder.sex_age.setCompoundDrawables(drawable, null, null, null);
				}

				if(result.getAge() > 0 || result.getSex() == MsgsConstants.SEX_MAN || result.getSex() == MsgsConstants.SEX_WOMAN){
					viewHolder.sex_age.setVisibility(View.VISIBLE);
				}else{
					viewHolder.sex_age.setVisibility(View.GONE);
				}
				viewHolder.user_vip.setText(context.getString(R.string.groupchat_users_uservip, result.getGrade()));
			} else if(flag){
				getuserDetail(userItemObj, viewHolder);
			}

			viewHolder.date.setText(context.getString(R.string.groupchat_users_date2, SafeUtils.getDate2MyStr2(userItemObj.getAtime())));
			// 角色显示
			if (userItemObj.getRel() == GroupUserRelVo.REL_ADMIN) {
				viewHolder.tag.setVisibility(View.VISIBLE);
				viewHolder.tag.setImageResource(R.drawable.group_president_tag);
			} else if (userItemObj.getRel() == GroupUserRelVo.REL_NORMALADMIN) {
				viewHolder.tag.setVisibility(View.VISIBLE);
				viewHolder.tag.setImageResource(R.drawable.group_manager_tag);
			} else if (userItemObj.getRel() == GroupUserRelVo.REL_USER) {
				viewHolder.tag.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.tag.setVisibility(View.INVISIBLE);
			}
			// 贡献排名
			if (userItemObj.getPoint() != 0) {
				viewHolder.contribute_point_chart.setVisibility(View.VISIBLE);
				viewHolder.contribute_point_chart.setText(context.getString(R.string.groupchat_users_contribute_point_chart, userItemObj.getTop()));
			} else {
				viewHolder.contribute_point_chart.setVisibility(View.GONE);
			}
		} else {
			if (!viewHolder.avatar.getTag().equals(R.drawable.common_user_icon_default)) {
				viewHolder.avatar.setImageResource(R.drawable.common_user_icon_default);
				viewHolder.avatar.setTag(R.drawable.common_user_icon_default);
			}
			viewHolder.tag.setVisibility(View.INVISIBLE);
			viewHolder.nickname.setText("");

		}

		// 显示内容
		MessageVo messageVo = userItemObj.getMessageVo();
		if (messageVo != null) {
			viewHolder.date2.setText(context.getString(R.string.groupchat_users_date, SafeUtils.getDate2MyStr2(messageVo.getCreateTime())));
			Paint paint = new Paint();
			paint.setTextSize(viewHolder.message.getTextSize());
			paint.setTextScaleX(viewHolder.message.getTextScaleX());
			viewHolder.message.setText(SmileyUtil.ReplaceSmiley(context, messageVo.getSummary(), (int) TextUtil.getTextHeight(paint),
					(int) TextUtil.getTextHeight(paint)));
		} else {
			viewHolder.date2.setText("");
			viewHolder.message.setText("");
		}
		//右下角按钮的点击事件
		action(userItemObj, viewHolder);
		return convertView;
	}

	/**
	 * 显示默认的信息
	 */
	private void showDefaultMsgs(ViewHolder holder){
		holder.avatar.setImageResource(R.drawable.common_user_icon_default);
		holder.nickname.setText("");
	}
	
	
	
	/**
	 * 当点击右下角的按钮的时候 
	 * 执行下面的这 个方法 
	 * @param userItemObj
	 * @param viewHolder
	 */
	private void action(final UserItemObj userItemObj,ViewHolder viewHolder){
		// 操作显示
		if (currentRel != null && currentRel.getRel() == GroupUserRelVo.REL_ADMIN) {
			// 自己是会长,不能够操作自己
			if (userItemObj.getUid() == currentRel.getUid()) {
				// 会长自己不显示操作
				viewHolder.action.setVisibility(View.INVISIBLE);
			} else {
				// 其他的都要可以操作
				viewHolder.action.setVisibility(View.VISIBLE);
			}
		} else if (currentRel != null && currentRel.getRel() == GroupUserRelVo.REL_NORMALADMIN) {
			// 自己是普通管理员，不能够操作自己和会长和其他管理员
			if (userItemObj.getRel() == GroupUserRelVo.REL_ADMIN || userItemObj.getRel() == GroupUserRelVo.REL_NORMALADMIN
					|| userItemObj.getUid() == currentRel.getUid()) {
				viewHolder.action.setVisibility(View.INVISIBLE);
			} else {
				viewHolder.action.setVisibility(View.VISIBLE);
			}
		} else {
			// 自己是普通会员，都不可以操作
			viewHolder.action.setVisibility(View.INVISIBLE);
		}
		// 操作
		viewHolder.action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 弹出删除操作
				showActionPopWindow(v, userItemObj);
			}
		});
	}

	/**
	 * 获取用户的详细信息
	 * @param userItemObj
	 * @param viewHolder
	 */
	private void getuserDetail(final UserItemObj userItemObj,final ViewHolder viewHolder){

		// 获得用户信息
		ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {
			@Override
			public void onSuccess(List<UserVo> resultList) {
				if(resultList == null || resultList.size() <= 0) return;
				UserVo result = resultList.get(0);
				if (result != null) {
					// 设置头像
					new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getAvatar()), ImageLoader.RADIUS_DEFAULT_PX6, viewHolder.avatar,
							R.drawable.common_user_icon_default);
					image_cache.put(result.getUserid(), true);
					if(userItemObj.getRemark() != null && !"".equals(userItemObj.getRemark())){
						viewHolder.nickname.setText(userItemObj.getRemark());
					}else{
						viewHolder.nickname.setText(result.getUsername());
					}
					if (result.getSex() == MsgsConstants.SEX_MAN) {
						viewHolder.sex_age.setBackgroundResource(R.drawable.common_item_jh2_shap);
						Drawable drawable = context.getResources().getDrawable(R.drawable.user_man_icon);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
						viewHolder.sex_age.setCompoundDrawables(drawable, null, null, null);

					} else {
						viewHolder.sex_age.setBackgroundResource(R.drawable.common_item_jh_shap);
						Drawable drawable = context.getResources().getDrawable(R.drawable.user_woman_icon);
						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
						viewHolder.sex_age.setCompoundDrawables(drawable, null, null, null);
					}
					if (result.getAge() != 0) {
						viewHolder.sex_age.setText("" + AgeUtil.convertAgeByBirth(result.getAge()));
						viewHolder.sex_age.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
					} else {
						viewHolder.sex_age.setText("");
						viewHolder.sex_age.setCompoundDrawablePadding(0);
					}
					if(result.getAge() > 0 || result.getSex() == MsgsConstants.SEX_MAN || result.getSex() == MsgsConstants.SEX_WOMAN){
						viewHolder.sex_age.setVisibility(View.VISIBLE);
					}else{
						viewHolder.sex_age.setVisibility(View.GONE);
					}
					viewHolder.user_vip.setText(context.getString(R.string.groupchat_users_uservip, result.getGrade()));
					user_cache.put(userItemObj.getUid(), result);

				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "获得用户信息异常：" + result);
				new ImageLoader().loadRes("drawable://" + R.drawable.common_user_icon_default, 0, viewHolder.avatar,
						R.drawable.common_user_icon_default);
				viewHolder.nickname.setText("");
			}

		};
		UserVo vo = dao.getUserById(userItemObj.getUid());
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(userItemObj.getUid());
		cdp.setUtime(vo == null ? 0 : vo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, null, p.build(), 0, null);
	}




	class ViewHolder {
		ImageView avatar;
		TextView nickname;
		TextView sex_age;
		TextView date;
		TextView date2;
		TextView message;
		ImageView tag;
		ImageView action;
		TextView contribute_point_chart;
		TextView user_vip;
	}


	/**
	 * 弹出popwindow的代码
	 * @param view
	 * @param vo
	 */
	private void showActionPopWindow(View view, final UserItemObj vo) {

		int xoffInPixels = view.getWidth() / 2;
		final MsgsPopWindow msgsPopWindow = new MsgsPopWindow(context, view, xoffInPixels, 0);
		List<View> items = new ArrayList<View>();
		if (currentRel != null && currentRel.getRel() == GroupUserRelVo.REL_ADMIN) {
			// 自己是会长,根据条件判断对方是管理员还是非管理员
			if (vo.getRel() == GroupUserRelVo.REL_NORMALADMIN) {
				// 对方是管理员，显示取消管理员
				final MsgsPopTextItem item = new MsgsPopTextItem(context, context.getString(R.string.groupchat_users_action_canceladmin));
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						msgsPopWindow.dismiss();
						cancelAdmin(vo);
					}
				});
				items.add(item);
			} else {
				// 对方不是管理员，显示设为管理员
				MsgsPopTextItem item = new MsgsPopTextItem(context, context.getString(R.string.groupchat_users_action_setadmin));
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						msgsPopWindow.dismiss();
						setAdmin(vo);
					}
				});
				items.add(item);
			}
		}
		// 移出公会
		MsgsPopTextItem item = new MsgsPopTextItem(context, context.getString(R.string.groupchat_users_action_delgroupuser));
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				msgsPopWindow.dismiss();
				delGroupUser(vo);
			}
		});
		items.add(item);

		msgsPopWindow.setContentItems(items);

	}

	/**
	 * 移出公会
	 * 
	 * @param vo
	 */
	private void delGroupUser(final UserItemObj vo) {
		ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					GroupChatUserAdapter.this.data.remove(vo);
					GroupChatUserAdapter.this.notifyDataSetChanged();
					ToastUtil.showToast(context, "操作成功");
					// 同步公会成员
					syncGroupUser(vo.getGrid());
					break;

				default:
					ErrorCodeUtil.handleErrorCode(context, result, null);
					break;
				}

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}
		};
		ProxyFactory.getInstance().getUserProxy()
		.userAction(callback, context, vo.getGrid(), MsgsConstants.OT_GROUP, MsgsConstants.OP_DELETE_MEMBER, "" + vo.getUid(), null,null);
	}

	/**
	 * 设为管理员
	 * 
	 * @param vo
	 */
	private void setAdmin(final UserItemObj vo) {
		final TextView txt = new TextView(context);
		txt.setTextColor(context.getResources().getColor(R.color.dialog_font_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.text_large));
		txt.setPadding(context.getResources().getDimensionPixelSize(R.dimen.global_page_paddingleft),
				context.getResources().getDimensionPixelSize(R.dimen.global_page_paddingtop),
				context.getResources().getDimensionPixelSize(R.dimen.global_page_paddingright),
				context.getResources().getDimensionPixelSize(R.dimen.global_page_paddingbottom));

		// 获得用户信息
		ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> uservoresult) {
				// TODO Auto-generated method stub
				if(uservoresult == null || uservoresult.size() <= 0) return;
				UserVo result = uservoresult.get(0);
				if (result != null) {
					txt.setText(context.getString(R.string.groupchat_users_action_setadmin_content, result.getUsername()));

					// listener
					DialogUtil.OKCallBackListener listener = new DialogUtil.OKCallBackListener() {

						@Override
						public void execute() {
							// TODO Auto-generated method stub
							ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

								@Override
								public void onSuccess(Integer result) {
									switch (result) {
									case Msgs.ErrorCode.EC_OK_VALUE:
										vo.setRel(GroupUserRelVo.REL_NORMALADMIN);
										GroupChatUserAdapter.this.notifyDataSetChanged();
										ToastUtil.showToast(context, "操作成功");
										// 同步公会成员
										syncGroupUser(vo.getGrid());
										break;

									default:
										ErrorCodeUtil.handleErrorCode(context, result, null);
										break;
									}

								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
								}
							};
							ProxyFactory
							.getInstance()
							.getUserProxy()
							.userAction(callback, context, vo.getGrid(), MsgsConstants.OT_GROUP, MsgsConstants.OP_ADD_ADMIN,
									"" + vo.getUid(), null,null);

						}

						@Override
						public void cannel() {
							// TODO Auto-generated method stub

						}

					};
					DialogUtil.showDialog(context, context.getString(R.string.groupchat_users_action_setadmin), txt, listener);

				} else {
					LogUtil.e(TAG, "获得用户信息为null");
					ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_USER_NULL, null);
				}

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "获得用户信息异常：" + result);
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}

		};
		UserVo userVo = dao.getUserById(vo.getUid());
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(vo.getUid());
		cdp.setUtime(userVo == null ? 0 : userVo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, null, p.build(), 0, null);

	}

	/**
	 * 取消管理员
	 * 
	 * @param vo
	 */
	private void cancelAdmin(final UserItemObj vo) {
		final TextView txt = new TextView(context);
		txt.setTextColor(context.getResources().getColor(R.color.darkgray));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.text_large));
		txt.setPadding(context.getResources().getDimensionPixelSize(R.dimen.global_page_paddingleft),
				context.getResources().getDimensionPixelSize(R.dimen.global_page_paddingtop),
				context.getResources().getDimensionPixelSize(R.dimen.global_page_paddingright),
				context.getResources().getDimensionPixelSize(R.dimen.global_page_paddingbottom));

		// 获得用户信息
		ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> uservoresult) {
				if(uservoresult == null || uservoresult.size() <= 0) return;
				UserVo result = uservoresult.get(0);
				// TODO Auto-generated method stub
				if (result != null) {
					txt.setText(context.getString(R.string.groupchat_users_action_canceladmin_content, result.getUsername()));

					// listener
					DialogUtil.OKCallBackListener listener = new DialogUtil.OKCallBackListener() {

						@Override
						public void execute() {
							// TODO Auto-generated method stub
							ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

								@Override
								public void onSuccess(Integer result) {
									switch (result) {
									case Msgs.ErrorCode.EC_OK_VALUE:

										vo.setRel(GroupUserRelVo.REL_USER);
										GroupChatUserAdapter.this.notifyDataSetChanged();
										ToastUtil.showToast(context, "操作成功");
										// 同步公会成员
										syncGroupUser(vo.getGrid());
										break;

									default:
										ErrorCodeUtil.handleErrorCode(context, result, null);
										break;
									}

								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
								}
							};
							ProxyFactory
							.getInstance()
							.getUserProxy()
							.userAction(callback, context, vo.getGrid(), MsgsConstants.OT_GROUP, MsgsConstants.OP_DEL_ADMIN,
									"" + vo.getUid(), null,null);

						}

						@Override
						public void cannel() {
							// TODO Auto-generated method stub

						}

					};
					DialogUtil.showDialog(context, context.getString(R.string.groupchat_users_action_canceladmin), txt, listener);
				} else {
					LogUtil.e(TAG, "获得用户信息为null");
					ErrorCodeUtil.handleErrorCode(context, ErrorCode.EC_CLIENT_USER_NULL, null);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "获得用户信息异常：" + result);
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}
		};
		UserVo userVo = dao.getUserById(vo.getUid());
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(vo.getUid());
		cdp.setUtime(userVo == null ? 0 : userVo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, null,p.build(), 0, null);

	}

	/**
	 * 同步公会成员
	 * 
	 * @param groupId
	 */
	private void syncGroupUser(long groupId) {
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GROUP_USER, groupId, new SyncCallBack() {
			@Override
			public void onSuccess(Object result) {
			}

			@Override
			public void onFailure(Integer result) {
			}
		});
	}

}
