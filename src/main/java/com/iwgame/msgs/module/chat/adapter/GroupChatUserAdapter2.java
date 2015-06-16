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

import android.content.Context;
import android.content.Intent;
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
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.chat.ui.GroupUsersFragment;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.SelectStopTimeActivity;
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
public class GroupChatUserAdapter2 extends BaseAdapter {
	private static final String TAG = "GroupChatUserAdapter";
	Context context = null;
	List<GroupUserRelVo> data = null;
	private LayoutInflater inflater;
	int currentRel = 0;
	long uid;
	HashMap<String, Integer> topMap = new HashMap<String, Integer>();
	GroupUserRelDao groupUserRelDao;
	private long groupId;
	private ListView listView;
	private boolean flag = true;//写了一个布尔变量用来标记 是否要加载数据
	private Map<Long, ViewHolder> viewholder_cache = new HashMap<Long, GroupChatUserAdapter2.ViewHolder>();
	private Map<Long, Boolean> image_cache = new HashMap<Long, Boolean>();//用来标记当前用户的头像是否已经加载过，

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * 构造方法
	 * @param context
	 * @param data
	 * @param currentRel
	 */
	public GroupChatUserAdapter2(Context context, List<GroupUserRelVo> data, int currentRel,long groupId,ListView listView) {
		this.context = context;
		this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.data = data;
		this.currentRel = currentRel;
		uid = SystemContext.getInstance().getExtUserVo().getUserid();
		groupUserRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
		this.groupId = groupId;
		this.listView = listView;
		addListener();
		rank();
	}



	/**
	 * 添加一个监听器
	 * 当滑动停止的时候，
	 * 才添加当前可见的头像信息
	 */
	private void addListener() {
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == SCROLL_STATE_IDLE){
					int firstvisibleIndex = listView.getFirstVisiblePosition();
					int lastvisibleIndex = listView.getLastVisiblePosition();
					if(firstvisibleIndex > 0) firstvisibleIndex = firstvisibleIndex - 1;
					ViewHolder viewHolder;
					GroupUserRelVo userItemObj;
					for(int i = firstvisibleIndex; i < lastvisibleIndex; i ++){
						try {
							userItemObj = (GroupUserRelVo)data.get(i);
							viewHolder = viewholder_cache.get(userItemObj.getUid());
							if(!image_cache.containsKey(userItemObj.getUid())){
								new ImageLoader().loadRes(ResUtil.getSmallRelUrl(userItemObj.getVo().getAvatar()), ImageLoader.RADIUS_DEFAULT_PX6, viewHolder.avatar,
										R.drawable.common_user_icon_default);
								image_cache.put(userItemObj.getUid(), true);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else if(scrollState == SCROLL_STATE_FLING){
					flag = false;
				}else if(scrollState == SCROLL_STATE_TOUCH_SCROLL){
					flag = false;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
	}



	/**
	 * 对公会里面的成员进行排名
	 */
	private void rank() {
		List<GroupUserRelVo> userList = groupUserRelDao.findUsersByGrid(groupId,"");
		topMap.clear();
		if(userList == null) return;
		int size = userList.size();
		GroupUserRelVo vo;
		//循环将排名保存到map中
		for(int i = 0; i < size; i ++){
			vo = userList.get(i);
			if(vo.getCpoint() > 0){
				topMap.put(""+vo.getUid(), i+1);
			}else{
				break;
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		rank();
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
		// TODO Auto-generated method stub
		final GroupUserRelVo userItemObj = (GroupUserRelVo)data.get(position);
		ViewHolder tmpViewHolder = null;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.group_chat_user_list_item2, null);
			tmpViewHolder = new ViewHolder();
			tmpViewHolder.avatar = (ImageView) convertView.findViewById(R.id.icon);
			tmpViewHolder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			tmpViewHolder.sex_age = (TextView) convertView.findViewById(R.id.sex_age);
			tmpViewHolder.date = (TextView) convertView.findViewById(R.id.date);
			tmpViewHolder.date2 = (TextView) convertView.findViewById(R.id.date2);
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

		LogUtil.debug(String.format("----getView,[%d][convertView = %s]", position, convertView));
		if (userItemObj != null) {
			viewholder_cache.put(userItemObj.getUid(), viewHolder);
			viewHolder.avatar.setImageResource(R.drawable.common_user_icon_default);
			viewHolder.avatar.setTag(R.id.imageview_tag_current_display_uri, "drawable://" + R.drawable.common_user_icon_default);
			viewHolder.avatar.setTag(R.id.imageview_tag_current_wait_display_uri, "drawable://" + R.drawable.common_user_icon_default);
			if(flag || image_cache.containsKey(userItemObj.getUid()))
				new ImageLoader().loadRes(ResUtil.getSmallRelUrl(userItemObj.getVo().getAvatar()), ImageLoader.RADIUS_DEFAULT_PX6, viewHolder.avatar,
						R.drawable.common_user_icon_default);

			if(userItemObj.getRemark() != null && !"".equals(userItemObj.getRemark())){
				viewHolder.nickname.setText(userItemObj.getRemark());
			}else{
				viewHolder.nickname.setText(userItemObj.getVo().getUsername());
			}

			if (userItemObj.getVo().getAge() > 0) {
				viewHolder.sex_age.setText(AgeUtil.convertAgeByBirth(userItemObj.getVo().getAge()) + "");
				viewHolder.sex_age.setVisibility(View.VISIBLE);
				if(userItemObj.getVo().getSex() == 0){
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_man_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					viewHolder.sex_age.setCompoundDrawables(sexdraw, null, null, null);
					viewHolder.sex_age.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
					viewHolder.sex_age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				}else if(userItemObj.getVo().getSex() == 1){
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_woman_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					viewHolder.sex_age.setCompoundDrawables(sexdraw, null, null, null);
					viewHolder.sex_age.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
					viewHolder.sex_age.setBackgroundResource(R.drawable.common_item_jh_shap);
				}
			} else {
				viewHolder.sex_age.setText("");
				viewHolder.sex_age.setCompoundDrawablePadding(0);
				if(userItemObj.getVo().getSex() == 0){
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_man_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					viewHolder.sex_age.setCompoundDrawables(sexdraw, null, null, null);
					viewHolder.sex_age.setCompoundDrawablePadding(0);
					viewHolder.sex_age.setBackgroundResource(R.drawable.common_item_jh2_shap);
					viewHolder.sex_age.setVisibility(View.VISIBLE);
				}else if(userItemObj.getVo().getSex() == 1){
					viewHolder.sex_age.setVisibility(View.VISIBLE);
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_woman_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					viewHolder.sex_age.setCompoundDrawables(sexdraw, null, null, null);
					viewHolder.sex_age.setCompoundDrawablePadding(0);
					viewHolder.sex_age.setBackgroundResource(R.drawable.common_item_jh_shap);
				}else{
					viewHolder.sex_age.setVisibility(View.GONE);
				}
			}
			if(userItemObj.getVo().getGrade() == 0){
				viewHolder.user_vip.setVisibility(View.GONE);
			}else{
				viewHolder.user_vip.setText(context.getString(R.string.groupchat_users_uservip, userItemObj.getVo().getGrade()));
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
			if (userItemObj.getCpoint() != 0) {
				viewHolder.contribute_point_chart.setVisibility(View.VISIBLE);
				if(topMap.containsKey(""+userItemObj.getUid())){
					viewHolder.contribute_point_chart.setText(context.getString(R.string.groupchat_users_contribute_point_chart, topMap.get(""+userItemObj.getUid())));
				}else{
					viewHolder.contribute_point_chart.setVisibility(View.INVISIBLE);
				}
			} else {
				viewHolder.contribute_point_chart.setVisibility(View.INVISIBLE);
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

		// 操作显示
		if ( currentRel == GroupUserRelVo.REL_ADMIN) {
			// 自己是会长,不能够操作自己
			if (userItemObj.getUid() == uid) {
				// 会长自己不显示操作
				viewHolder.action.setVisibility(View.GONE);
			} else {
				// 其他的都要可以操作
				viewHolder.action.setVisibility(View.VISIBLE);
			}
		} else if (currentRel == GroupUserRelVo.REL_NORMALADMIN) {
			// 自己是普通管理员，不能够操作自己和会长和其他管理员
			if(userItemObj.getUid() == uid){
				viewHolder.action.setVisibility(View.GONE);
			}else{
				viewHolder.action.setVisibility(View.VISIBLE);
			}
		} else {
			// 自己是普通会员，都不可以操作
			viewHolder.action.setVisibility(View.GONE);
		}
		// 操作
		viewHolder.action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 弹出删除操作
				showActionPopWindow(v, userItemObj);
			}
		});
		return convertView;
	}

	static class ViewHolder {
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

	private void showActionPopWindow(View view, final GroupUserRelVo vo) {

		int xoffInPixels = view.getWidth() / 2;
		final MsgsPopWindow msgsPopWindow = new MsgsPopWindow(context, view, xoffInPixels, 0);
		List<View> items = new ArrayList<View>();
		if (currentRel == GroupUserRelVo.REL_ADMIN) {
			// 自己是会长,根据条件判断对方是管理员还是非管理员
			if (vo.getRel() == GroupUserRelVo.REL_NORMALADMIN) {
				// 对方是管理员，显示取消管理员
				final MsgsPopTextItem item = new MsgsPopTextItem(context, context.getString(R.string.groupchat_users_action_canceladmin));
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
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
				msgsPopWindow.dismiss();
				delGroupUser(vo);
			}
		});
		if(currentRel == GroupUserRelVo.REL_ADMIN)
			items.add(item);
		else if(vo.getRel() != GroupUserRelVo.REL_ADMIN && vo.getRel() != GroupUserRelVo.REL_NORMALADMIN)
			items.add(item);

		//显示禁言
		MsgsPopTextItem item1 = new MsgsPopTextItem(context, "禁言");
		item1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//执行禁言的方法
				Intent intent = new Intent(context,SelectStopTimeActivity.class);
				intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, GroupUsersFragment.getInstance().getGroupId());
				intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, vo.getUid()+"");
				context.startActivity(intent);
				msgsPopWindow.dismiss();
			}
		});
		items.add(item1);
		msgsPopWindow.setContentItems(items);

	}

	/**
	 * 移出公会
	 * 
	 * @param vo
	 */
	private void delGroupUser(final GroupUserRelVo vo) {
		ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case Msgs.ErrorCode.EC_OK_VALUE:
					GroupChatUserAdapter2.this.data.remove(vo);
					GroupChatUserAdapter2.this.notifyDataSetChanged();
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
	private void setAdmin(final GroupUserRelVo vo) {
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
				if (result != null) {
					txt.setText(context.getString(R.string.groupchat_users_action_setadmin_content, result.getUsername()));
					DialogUtil.OKCallBackListener listener = new DialogUtil.OKCallBackListener() {

						@Override
						public void execute() {
							ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

								@Override
								public void onSuccess(Integer result) {
									switch (result) {
									case Msgs.ErrorCode.EC_OK_VALUE:
										vo.setRel(GroupUserRelVo.REL_NORMALADMIN);
										GroupChatUserAdapter2.this.notifyDataSetChanged();
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
				LogUtil.e(TAG, "获得用户信息异常：" + result);
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}

		};
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(vo.getUid());
		cdp.setUtime(vo.getUtime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, null,p.build(), 0, null);

	}

	/**
	 * 取消管理员
	 * 
	 * @param vo
	 */
	private void cancelAdmin(final GroupUserRelVo vo) {
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
										GroupChatUserAdapter2.this.notifyDataSetChanged();
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
				LogUtil.e(TAG, "获得用户信息异常：" + result);
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}

		};
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(vo.getUid());
		cdp.setUtime(vo.getUtime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, null, p.build(), 0, null);

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
