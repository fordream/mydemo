/**      
 * GroupUserAdapter.java Create on 2013-10-28     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import com.iwgame.msgs.MainFragmentActivity;
import com.youban.msgs.R;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.object.UserNewsVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: GroupUserAdapter
 * @Description: 公会用户列表适配器
 * @author 王卫
 * @date 2013-10-28 上午10:55:50
 * @Version 1.0
 * 
 */
public class GroupUserAdapter extends BaseAdapter {

	private static final String TAG = "GroupUserAdapter";
	public static int MODE_APPLY = 1; // 批准成员列表
	public static int MODE_DELED = 2; // 删除成员列表
	public static int MODE_SETTING = 3; // 管理员设置列表
	public static int MODE_INVITE = 4; // 邀请成员列表
	public static int MODE_TRANSFER = 5; // 转让公会列表
	public static int MODE_USERLIST = 6; // 公会成员列表

	// 适配器用于那个公会用户列表
	private int mode;
	// 公会ID
	private long grid;

	private LayoutInflater inflater;
	// 列表类型
	private final int TYPE_TAG = 0;
	private final int TYPE_USER = 1;

	private List<Map<String, Object>> data;
	private UserVo userVo;
	private Map<Long, UserVo> uservo_cache = new HashMap<Long, UserVo>();
	private Map<Long, String> mood_cache = new HashMap<Long, String>();
	private Map<Long,UserVo> sync_cache = new HashMap<Long, UserVo>();
	private Map<Long, ViewHolder>viewHolder_cache = new HashMap<Long, GroupUserAdapter.ViewHolder>();
	private Map<Long, Boolean> avatar_cache = new HashMap<Long, Boolean>();//用来检测头像是否已经加载过
	private ListView listView;
	private boolean flag = true;//用来标记滑动的时候 是否要加载数据
	private Context context;
    private UserDao userDao;

    
	public boolean isFlag() {
		return flag;
	}


	public void setFlag(boolean flag) {
		this.flag = flag;
	}
    
	/**
	 * 
	 */
	public GroupUserAdapter(Context context, List<Map<String, Object>> data, int mode, long grid,ListView listView) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mode = mode;
		this.grid = grid;
		this.data = data;
		this.listView = listView;
		userVo = SystemContext.getInstance().getExtUserVo();
		userDao = DaoFactory.getDaoFactory().getUserDao(context);
		this.context = context;
		addListener();
	}


	/**
	 * 添加监听器   当滑动停止的时候
	 * 在去加载数据
	 */
	private void addListener() {
		this.listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
					flag = false;
				}else if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					flag = false;
				}else if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					try {
						int firstvisibleIndex = listView.getFirstVisiblePosition();
						int lastvisibleIndex = listView.getLastVisiblePosition();
						if(firstvisibleIndex > 0) firstvisibleIndex = firstvisibleIndex - 1;
						long uid = 0;
						Map<String, Object> mapData;
						ViewHolder holder;
						for(int i = firstvisibleIndex; i < lastvisibleIndex; i ++){
							if(getItemViewType(i) == TYPE_TAG) continue;
							mapData = (HashMap<String, Object>) getItem(i);
							uid = (Long)mapData.get("uid");
							holder = viewHolder_cache.get(uid);
							if(mode == MODE_INVITE){
								if(!avatar_cache.containsKey(uid) && mapData.containsKey("avatar")){
									String avatar = (String) mapData.get("avatar");
									ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_default_icon);
									avatar_cache.put(uid, true);
								}
							}else if(!uservo_cache.containsKey(uid)){
								getUserInfo(uid, mapData, holder);
							}
							if(!mood_cache.containsKey(uid)){
								getUserNews(uid, holder);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		Map<String, Object> mapData = (HashMap<String, Object>) getItem(position);
		if (mapData.containsKey("tag"))
			return TYPE_TAG;
		else
			return TYPE_USER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map<String, Object> mapData = (HashMap<String, Object>) getItem(position);
		final int type = getItemViewType(position);
		ViewHolder holder = null;
		TagViewHolder tagholder = null;
		if (convertView == null) {
			switch (type) {
			case TYPE_TAG:
				convertView = this.inflater.inflate(R.layout.group_user_list_tag_item, null);
				tagholder = new TagViewHolder();
				tagholder.tagtxt = (TextView) convertView.findViewById(R.id.tagtxt);
				convertView.setTag(tagholder);
				break;
			case TYPE_USER:
				convertView = this.inflater.inflate(R.layout.group_user_list_item, null);
				holder = new ViewHolder();
				holder.avatarView = (ImageView) convertView.findViewById(R.id.icon);
				holder.desc = (TextView) convertView.findViewById(R.id.desc);
				holder.grade = (TextView) convertView.findViewById(R.id.grade);
				holder.functionView = (LinearLayout) convertView.findViewById(R.id.functionView);
				holder.functionView2 = (FrameLayout) convertView.findViewById(R.id.functionView2);
				holder.submitBtn = (ImageView) convertView.findViewById(R.id.submitBtn);
				holder.submitTxt = (TextView) convertView.findViewById(R.id.submitTxt);
				holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holder.sex = (ImageView) convertView.findViewById(R.id.sex);
				holder.age = (TextView) convertView.findViewById(R.id.age);
				holder.tag = (ImageView) convertView.findViewById(R.id.tag);
				holder.hotArea = (ImageView) convertView.findViewById(R.id.hotArea);
				holder.newtag = (ImageView)convertView.findViewById(R.id.newtag);
				holder.newsTxt = (TextView)convertView.findViewById(R.id.newsTxt);
				holder.hotArea.getBackground().setAlpha(0);
				convertView.setTag(holder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_TAG:
				tagholder = (TagViewHolder) convertView.getTag();
				break;
			case TYPE_USER:
				holder = (ViewHolder) convertView.getTag();
				break;
			}
		}
		switch (type) {
		case TYPE_TAG:
			tagholder.tagtxt.setText(String.valueOf(mapData.get("tag")));
			break;
		case TYPE_USER:
			// 根据用户昵称判断用户信息是否齐全，没有用户基本信息则加载用户信息
			holder.functionView.removeAllViews();
			holder.tag.setVisibility(View.INVISIBLE);
			long uid = (Long)mapData.get("uid");
			viewHolder_cache.put(uid, holder);
			if(mode == MODE_INVITE){//如果是邀请成员  直接添加checkbox就可以啦
				showDetailMsgs(holder, mapData);
				holder.functionView.setVisibility(View.GONE);
				holder.functionView2.setVisibility(View.GONE);
				renderInviteUI(convertView, mapData, holder.functionView, holder.hotArea, holder.functionView2, holder.submitBtn, holder.submitTxt);
			}else if(uservo_cache.containsKey(uid)){//如果数据已经同步下来了  那直接显示就可以啦
				showDetailMsgs(holder, mapData);
				initialize(mapData, holder.avatarView, holder.age, holder.tag, holder.functionView, holder.nickname, holder.desc, holder.hotArea, holder.functionView2, holder.submitBtn, holder.submitTxt, holder.grade);
			}else{//如果是第一次进来，则先去同步数据
				showDefaultMsgs(holder);
				if(flag)
					getUserInfo(uid, mapData, holder);
			}

			//下面的代码是用来控制是否显示动态的代码
			if(mode == MODE_INVITE){//如果是邀请成员，则直接从map里面读取news的数据
				if(mapData.containsKey("mood")){
					String mood = (String)mapData.get("mood");
					if(mood != null){
						holder.newsTxt.setText(mood);
						if (!mood.isEmpty()) {
							holder.newtag.setVisibility(View.VISIBLE);
							holder.newtag.setBackgroundResource(R.drawable.common_qian);
						}else{
							holder.newtag.setVisibility(View.INVISIBLE);
						}
					}
				}
			}
			
			if(mood_cache.containsKey(uid)){
				String news = mood_cache.get(uid);
				if (news != null && !"".equals(news)) {
					holder.newsTxt.setText(news);
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_dong);
				}else{
					holder.newtag.setVisibility(View.INVISIBLE);
					holder.newsTxt.setText("");
				}
			}else if(flag){
				getUserNews(uid, holder);
			}

			break;
		}
		return convertView;
	}


	/**
	 * 如果是去请求用户的信息
	 * 首先要显示默认的数据，
	 * 在去加载数据
	 */
	private void showDefaultMsgs(ViewHolder holder){
		holder.avatarView.setImageResource(R.drawable.common_default_icon);
		holder.nickname.setText("");
		holder.age.setVisibility(View.INVISIBLE);
		holder.newtag.setVisibility(View.INVISIBLE);
		holder.grade.setVisibility(View.INVISIBLE);
		holder.newsTxt.setText("");
	}

	/**
	 * 显示用户的
	 * 默认信息
	 */
	private void showDetailMsgs(ViewHolder holder,Map<String, Object> mapData){
		long uid = (Long)mapData.get("uid");
		holder.avatarView.setImageResource(R.drawable.common_default_icon);
		// 显示头像
		if (mapData.containsKey("avatar")) {
			String avatar = (String) mapData.get("avatar");
			if(flag || avatar_cache.containsKey(uid)){
				ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_default_icon);
				avatar_cache.put(uid, true);
			}
		}
		//显示年龄和性别
		setSexAndAge(mapData, holder.age);
		//显示昵称
		if(mode == MODE_INVITE){
			if(mapData.containsKey("remark") && mapData.get("remark") != null && !"".equals((String)mapData.get("remark"))){
				holder.nickname.setText((String)mapData.get("remark"));
			}else if (mapData.containsKey("nickname")) {
				holder.nickname.setText((String) mapData.get("nickname"));
			}
		}else if(mapData.containsKey("nickname")) {
			holder.nickname.setText((String) mapData.get("nickname"));
		}
		if (mapData.containsKey("grade")) {
			if((Integer)mapData.get("grade") != 0){
				holder.grade.setVisibility(View.VISIBLE);
				holder.grade.setText("LV"+mapData.get("grade"));
			}else{
				holder.grade.setVisibility(View.GONE);
			}
			holder.grade.setVisibility(View.VISIBLE);
		}else{
			holder.grade.setVisibility(View.GONE);
		}
	}


	/**
	 * 显示动态
	 * 或者心情
	 */
	private void showMood(final long uid,final ViewHolder holder) {
		if(sync_cache.containsKey(uid)){
			UserVo vo = sync_cache.get(uid);
			renderUI(vo, holder);
		}else{
			ServiceFactory.getInstance().getSyncEntityService().syncEntity(uid, SyncEntityService.TYPE_USER, new SyncCallBack() {
				@Override
				public void onSuccess(Object result) {
					UserVo vo = null;
					vo = userDao.getUserByUserId(uid);
					sync_cache.put(uid,vo);
					renderUI(vo,holder);
				}

				@Override
				public void onFailure(Integer result) {
					
				}

			});
		}
	}

	/**
	 * 渲染用户的动态或者心情
	 * @param uvo
	 * @param convertView
	 * @param holder
	 */
	protected void renderUI(UserVo vo,ViewHolder holder) {
		if (vo != null) {
			String mood = vo.getMood();
			if(mood != null){
				if (!mood.isEmpty()) {
					holder.newsTxt.setText(mood);
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_qian);
				}else{
					holder.newtag.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			return;
		}

	}


	/**
	 * 获取用户的动态
	 * @param userid
	 * @param holder
	 */
	private void getUserNews(final long userid, final ViewHolder holder) {
		ProxyFactory.getInstance().getUserProxy().searchUsers(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				if (result != null && result.getItems() != null && result.getItems().size() == 1) {
					UserObject uobj = result.getItems().get(0);
					String news = uobj.getNews();
					if (news != null) {
						holder.newsTxt.setText(news);
						if (!news.isEmpty()) {
							mood_cache.put(userid, news);
							holder.newtag.setVisibility(View.VISIBLE);
							holder.newtag.setBackgroundResource(R.drawable.common_dong);
						}else{
							holder.newtag.setVisibility(View.INVISIBLE);
							showMood(userid,holder);
						}
					}else{
						showMood(userid,holder);
					}
				}
			
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				showMood(userid, holder);
			}

		}, SystemContext.getInstance().getContext(), null, null, null, null,"3,4,5", userid+"", Long.MAX_VALUE, -4, null, null, null, null, 4);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param contect
	 * @param uid
	 */
	private void getUserInfo(final long uid, final Map<String, Object> mapData,final ViewHolder holder) {
		UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		UserVo vo = dao.getUserById(uid);
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(uid);
		cdp.setUtime(vo == null ? 0 : vo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> uservoresult) {
				if(uservoresult == null || uservoresult.size() <= 0) return;
				UserVo result = uservoresult.get(0);
				if (result != null) {
					uservo_cache.put(uid, result);
					mapData.put("nickname", result.getUsername());
					if (!mapData.containsKey("mood"))
						mapData.put("mood", result.getMood());
					if (!mapData.containsKey("sex"))
						mapData.put("sex", result.getSex());
					if (!mapData.containsKey("age"))
						mapData.put("age", result.getAge());
					if (!mapData.containsKey("avatar"))
						mapData.put("avatar", result.getAvatar());
					mapData.put("grade", result.getGrade());
					initialize(mapData, holder.avatarView, holder.age, holder.tag, holder.functionView, holder.nickname, holder.desc, holder.hotArea, holder.functionView2, holder.submitBtn, holder.submitTxt, holder.grade);
					showDetailMsgs(holder, mapData);
					ImageViewUtil.showImage(holder.avatarView, result.getAvatar(), R.drawable.common_default_icon);
					avatar_cache.put(uid, true);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "获取用户信息失败：" + result);
			}
		}, context, p.build(), 0, null);
	}

	/**
	 * 初始化
	 * 
	 * @param convertView
	 * @param mapData
	 */
	private void initialize(Map<String, Object> mapData, ImageView avatarView, TextView age, ImageView tag,
			LinearLayout functionView, TextView nickname, TextView desc, ImageView hotArea, FrameLayout functionView2, ImageView submitBtn, TextView submitTxt, TextView gradeTxt) {
		functionView.setVisibility(View.GONE);
		functionView2.setVisibility(View.GONE);
		if (mode == MODE_APPLY) {
			renderApplyUI(mapData, functionView, functionView2, submitBtn, submitTxt);
		} else if (mode == MODE_DELED) {
			renderDeleteUI(mapData, tag, functionView, hotArea, functionView2, submitBtn, submitTxt);
		} else if (mode == MODE_SETTING) {
			renderSettingUI(mapData, tag, functionView, hotArea, functionView2, submitBtn, submitTxt);
		}else if (mode == MODE_TRANSFER) {
			renderTransferUI(mapData, functionView, functionView2, submitBtn, submitTxt);
		} else if (mode == MODE_USERLIST) {
			renderUserListUI(mapData, tag, functionView2, submitBtn, submitTxt);
		}
	}

	/**
	 * 渲染批准用户UI
	 * 
	 * @param convertView
	 */
	private void renderApplyUI(final Map<String, Object> mapData, final LinearLayout functionView, final FrameLayout functionView2, final ImageView submitBtn, final TextView submitTxt) {
		if (mapData != null && mapData.get("status") != null) {
			Integer status = (Integer) mapData.get("status");
			// 设置批准或已批准的按钮
			functionView.setVisibility(View.GONE);
			functionView2.setVisibility(View.VISIBLE);
			if (status == 1) {
				submitTxt.setText("已批准");
				submitTxt.setTextColor(context.getResources().getColor(R.color.common_btn_dis_text_color));
				submitTxt.setEnabled(false);
				submitBtn.setEnabled(false);
			} else {
				submitTxt.setText("批准");
				submitTxt.setEnabled(true);
				submitBtn.setEnabled(true);
			}
			submitBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					final CustomProgressDialog dialog = CustomProgressDialog.createDialog(v.getContext());
					dialog.show();
					// 点击批准按钮
					ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

						@Override
						public void onSuccess(Integer result) {
							switch (result) {
							case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
								ToastUtil.showToast(context, context.getString(R.string.group_approve_success));
								submitTxt.setText("已批准");
								submitTxt.setTextColor(context.getResources().getColor(R.color.common_btn_dis_text_color));
								submitTxt.setEnabled(false);
								submitBtn.setEnabled(false);
								UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_GROUP_APPROVE, null, null, null, null, null);
								break;
							default:
								ToastUtil.showToast(context, "批准失败");
								break;
							}
							dialog.dismiss();
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							switch (result) {

							default:
								ToastUtil.showToast(context, "批准失败");
								break;
							}
							dialog.dismiss();
						}
					}, v.getContext(), grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_APPROVE_MEMBER, String.valueOf(mapData.get("uid")), null,null);
				}
			});
		} else {
			functionView.setVisibility(View.GONE);
			functionView2.setVisibility(View.GONE);
			return;
		}
	}

	/**
	 * 公会成员列表UI
	 * 
	 * @param convertView
	 * @param mapData
	 */
	private void renderUserListUI(final Map<String, Object> mapData, ImageView tag, FrameLayout functionView2, ImageView submitBtn, TextView submitTxt) {// 判断不是自己且不是会长
		int rel = 0;
		if (mapData.get("rel") != null)
			rel = (Integer) mapData.get("rel");
		tag.setVisibility(View.VISIBLE);
		if (rel == 2) {
			// 添加管理员标记
			tag.setImageResource(R.drawable.group_manager_tag);
		} else if (rel == 3) {
			// 添加会长标记
			tag.setImageResource(R.drawable.group_president_tag);
		} else {
			tag.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 渲染删除用户UI
	 * 
	 * @param convertView
	 */
	private void renderDeleteUI(final Map<String, Object> mapData, ImageView tag, LinearLayout functionView, ImageView hotArea, FrameLayout functionView2, ImageView submitBtn, TextView submitTxt) {// 判断不是自己且不是会长
		GroupUserRelVo rvo = ProxyFactory.getInstance().getGroupProxy().getRel(grid, userVo.getUserid());
		boolean ispre = false;
		if (rvo != null && rvo.getRel() == 3)
			ispre = true;
		int rel = 0;
		if (mapData.get("rel") != null)
			rel = (Integer) mapData.get("rel");
		tag.setVisibility(View.VISIBLE);
		if (rel == 2) {
			// 添加管理员标记
			tag.setImageResource(R.drawable.group_manager_tag);
		} else if (rel == 3) {
			// 添加会长标记
			tag.setImageResource(R.drawable.group_president_tag);
		} else {
			tag.setVisibility(View.INVISIBLE);
		}
		functionView.setVisibility(View.GONE);
		functionView2.setVisibility(View.GONE);
		// 不能删除会长和管理员
		if ((Long) mapData.get("uid") != userVo.getUserid()) {
			if (ispre) {
				addCheckBoxView(mapData, functionView, hotArea, functionView2);
			} else {
				if (rel != 2 && rel != 3) {
					addCheckBoxView(mapData, functionView, hotArea, functionView2);
				}
			}
		}
	}

	/**
	 * 渲染管理员设置UI
	 * 
	 * @param convertView
	 */
	private void renderSettingUI(final Map<String, Object> mapData, final ImageView tag, final LinearLayout functionView,
			final ImageView hotArea, final FrameLayout functionView2, final ImageView submitBtn, final TextView submitTxt) {
		if ((Long) mapData.get("uid") != userVo.getUserid()) {
			// 判断管理员
			if (mapData != null && mapData.get("rel") != null && (Integer) mapData.get("rel") == 2) {
				// 添加管理员标记
				tag.setVisibility(View.VISIBLE);
				tag.setImageResource(R.drawable.group_manager_tag);
				// 设置批准或已批准的按钮
				functionView.setVisibility(View.GONE);
				functionView2.setVisibility(View.VISIBLE);
				submitTxt.setText("取消");
				submitBtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(final View v) {
						// 取消管理员
						ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

							@Override
							public void onSuccess(Integer result) {
								switch (result) {
								case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
									ToastUtil.showToast(context,
											context.getString(R.string.group_manager_cannel_success));
									functionView.setVisibility(View.VISIBLE);
									functionView2.setVisibility(View.GONE);
									tag.setVisibility(View.INVISIBLE);
									mapData.put("rel", 1);
									addCheckBoxView(mapData, functionView, hotArea, functionView2);
									break;
								default:
									break;
								}
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
							}
						}, v.getContext(), grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_DEL_ADMIN, String.valueOf(mapData.get("uid")), null,null);
					}
				});
			} else {
				// 添加CHECKBOX
				addCheckBoxView(mapData, functionView, hotArea, functionView2);
			}
		} else {
			tag.setVisibility(View.VISIBLE);
			// 添加会长标记
			tag.setImageResource(R.drawable.group_president_tag);
			functionView.setVisibility(View.GONE);
			functionView2.setVisibility(View.GONE);
		}
	}

	/**
	 * 渲染邀请用户UI
	 * 
	 * @param convertView
	 */
	private void renderInviteUI(View convertView, final Map<String, Object> mapData, LinearLayout functionView, ImageView hotArea, FrameLayout functionView2, ImageView submitBtn, TextView submitTxt) {
		// 添加CHECKBOX
		addCheckBoxView(mapData, functionView, hotArea, functionView2);
	}

	/**
	 * 渲染转让用户UI
	 * 
	 * @param convertView
	 */
	private void renderTransferUI(final Map<String, Object> mapData, LinearLayout functionView, FrameLayout functionView2, ImageView submitBtn, TextView submitTxt) {
		if ((Long) mapData.get("uid") != userVo.getUserid()) {
			// 设置转让按钮
			functionView.setVisibility(View.GONE);
			functionView2.setVisibility(View.VISIBLE);
			submitTxt.setText("转让");
			submitBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					// 点击转让按钮
					transferGroupDialog(mapData);
				}
			});
		}
	}

	/**
	 * 添加复选框
	 * 
	 * @param convertView
	 * @param mapData
	 */
	private void addCheckBoxView(final Map<String, Object> mapData, LinearLayout functionView, ImageView hotArea, FrameLayout functionView2) {
		// 添加CHECKBOX
		functionView.setVisibility(View.VISIBLE);
		functionView2.setVisibility(View.GONE);
		functionView.removeAllViews();
		LinearLayout boxLayout = (LinearLayout) View.inflate(context, R.layout.common_checkbox, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		final CheckBox cbox = (CheckBox) boxLayout.findViewById(R.id.checkBox);
		functionView.addView(boxLayout, params);
		cbox.setFocusable(false);
		cbox.setChecked((Boolean) mapData.get("isChecked"));
		cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mapData != null) {
					mapData.put("isChecked", isChecked);
				}
			}
		});
		hotArea.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(cbox.isChecked()){
					cbox.setChecked(false);
				}else{
					cbox.setChecked(true);
				}
			}
		});
	}

	/**
	 * 转让公会
	 */
	private void transferGroupDialog(final Map<String, Object> mapData) {
		final Dialog dialog = new Dialog(context, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final TextView txt = new TextView(context);
		txt.setTextColor(context.getResources().getColor(R.color.darkgray));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(context.getString(R.string.group_transfer_group_tip));
		content.setPadding(DisplayUtil.dip2px(context, 10), 10, DisplayUtil.dip2px(context, 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(context.getString(R.string.global_dialog_tip_title));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击转让按钮
				ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						switch (result) {
						case Msgs.ErrorCode.EC_OK_VALUE:
							ToastUtil.showToast(context, context.getString(R.string.group_transfer_success));
							Intent intent = new Intent(context, MainFragmentActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							Bundle bundle = new Bundle();
							bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 3);
							intent.putExtras(bundle);
							((Activity) context).startActivity(intent);
							ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GROUP, null);
							UMUtil.sendEvent(context, UMConfig.MSGS_EVENT_GROUP_TRANSFER, String.valueOf(grid), null, String.valueOf(mapData.get("uid")), null, null);
							break;

						default:
							ToastUtil.showToast(context, context.getString(R.string.group_transfer_fail));
							break;
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						switch (result) {
						case Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE:
							ToastUtil.showToast(context,
									context.getString(R.string.ec_group_transfer_group_over_count));
							break;
						case Msgs.ErrorCode.EC_MSGS_NO_PERMISSION_VALUE:
							ToastUtil.showToast(context,
									context.getString(R.string.ec_group_transfer_group_no_permission));
							break;
						default:
							ToastUtil.showToast(context, context.getString(R.string.group_transfer_fail));
							break;
						}
					}
				}, v.getContext(), grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_TRANSFER_GROUP, String.valueOf(mapData.get("uid")), null,null);
				dialog.dismiss();
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	private void setSexAndAge(Map<String, Object> mapData, TextView age) {
		if (mapData.containsKey("age") && (Integer) mapData.get("age") > 0) {
			age.setText(AgeUtil.convertAgeByBirth((Integer) mapData.get("age")) + "");
			age.setVisibility(View.VISIBLE);
			if(mapData.containsKey("sex")){
				if((Integer)mapData.get("sex") == 0){
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_man_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					age.setCompoundDrawables(sexdraw, null, null, null);
					age.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
					age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				}else if((Integer)mapData.get("sex") == 1){
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_woman_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					age.setCompoundDrawables(sexdraw, null, null, null);
					age.setCompoundDrawablePadding(DisplayUtil.dip2px(context, 4));
					age.setBackgroundResource(R.drawable.common_item_jh_shap);
				}
			}else{
				age.setCompoundDrawablePadding(0);
				age.setBackgroundResource(R.drawable.common_item_jh2_shap);
			}
		} else {
			age.setText("");
			age.setCompoundDrawablePadding(0);
			if(mapData.containsKey("sex")){
				if((Integer)mapData.get("sex") == 0){
					age.setVisibility(View.VISIBLE);
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_man_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					age.setCompoundDrawables(sexdraw, null, null, null);
					age.setCompoundDrawablePadding(0);
					age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				}else if((Integer)mapData.get("sex") == 1){
					age.setVisibility(View.VISIBLE);
					Drawable sexdraw = context.getResources().getDrawable(R.drawable.user_woman_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					age.setCompoundDrawables(sexdraw, null, null, null);
					age.setCompoundDrawablePadding(0);
					age.setBackgroundResource(R.drawable.common_item_jh_shap);
				}
			}else{
				age.setCompoundDrawablePadding(0);
				age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				age.setVisibility(View.GONE);
			}
		}
		age.setVisibility(View.VISIBLE);
	}

	static class ViewHolder {
		LinearLayout functionView;
		FrameLayout functionView2;
		ImageView submitBtn;
		TextView submitTxt;
		ImageView tag;
		TextView nickname;
		TextView desc;
		TextView grade;
		ImageView sex;
		TextView age;
		ImageView avatarView;
		ImageView hotArea;
		TextView newsTxt;
		ImageView newtag;
	}

	static class TagViewHolder {
		TextView tagtxt;
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


	/**
	 * 清除缓存数据
	 */
	public void clean(){
		uservo_cache.clear();
		mood_cache.clear();
		sync_cache.clear();
		viewHolder_cache.clear();
		avatar_cache.clear();
	}

}
