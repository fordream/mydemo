/**      
 * RecommendGameAdapter.java Create on 2013-9-5     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.module.user.object.UserNewsVo;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: RecommendGameAdapter
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-5 下午03:21:30
 * @Version 1.0
 * 
 */
public class RecommendGame2FriendAdapter extends SimpleAdapter {

	protected static final String TAG = "UserAdapter";

	public static final int GAME_FOLLOW_TYPE_NULL = 1;
	public static final int GAME_FOLLOW_TYPE_SELF = 2;
	public static final int GAME_FOLLOW_TYPE_COMMON = 3;

	// 是否显示关注按钮
	private boolean mShowFollow = true;
	// 获取用户关注贴吧的类型[1没有2他关注3共同关注]
	protected int mGameFollowType = GAME_FOLLOW_TYPE_NULL;

	private Map<Long, UserVo> uservo_cache = new HashMap<Long, UserVo>();
	private Map<Long, String> usernews_cache = new HashMap<Long, String>();
	private Map<Long, ViewHolder> viewholder_cache = new HashMap<Long, RecommendGame2FriendAdapter.ViewHolder>();
	private Map<Long, View>convertView_cache = new HashMap<Long, View>();
	private Map<Long, Boolean> imageview_cache = new HashMap<Long, Boolean>();
	private UserVo loginUserVo = null;
	private UserDao userDao;
	private boolean flag = true;//用来标记是否要加载数据
	private ListView listView;
	
	
	public boolean isFlag() {
		return flag;
	}



	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public RecommendGame2FriendAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,ListView listView) {
		super(context, data, resource, from, to);
		loginUserVo = SystemContext.getInstance().getExtUserVo();
		userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		this.listView = listView;
		addListener();
	}

	
	
	/**
	 * 设置监听器
	 * 监听listview的滑动
	 */
	private void addListener() {
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
					flag = true;
					int firstVisibleIndex = listView.getFirstVisiblePosition();
					int lastVisibleIndex = listView.getLastVisiblePosition();
					if(firstVisibleIndex > 0) firstVisibleIndex = firstVisibleIndex - 1;
					ViewHolder holder;
					Map<String, Object> map;
					long uid;
					View convertView;
					String avatar;
					for(int i = firstVisibleIndex; i < lastVisibleIndex; i++){
						try {
							map = (HashMap<String, Object>) getItem(i);
							uid = Long.valueOf(map.get("uid").toString()); 
							convertView = convertView_cache.get(uid);
							holder = (ViewHolder)viewholder_cache.get(uid);
							avatar = (String) map.get("avatar");
							if(!usernews_cache.containsKey(uid)){
								getUserNews(uid, holder);
							}
							if(!uservo_cache.containsKey(uid)){
								searchUserDetail(uid, convertView, holder);
							}
							ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_default_icon);
							imageview_cache.put(uid, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
					flag = false;
				}else if(scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					flag = false;
				}
			}
			
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		Object tag = convertView.getTag();
		final ViewHolder holder;
		if (tag == null) {
			holder = new ViewHolder();
			holder.avatarView = (ImageView) convertView.findViewById(R.id.icon);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.hotArea = (ImageView) convertView.findViewById(R.id.hotArea);
			holder.submitBtn = (ImageView) convertView.findViewById(R.id.submitBtn);
			holder.submitTxt = (TextView) convertView.findViewById(R.id.submitTxt);
			holder.newsTxt = (TextView) convertView.findViewById(R.id.newsTxt);
			holder.newtag = (ImageView) convertView.findViewById(R.id.newtag);
			holder.age = (TextView) convertView.findViewById(R.id.age);
			holder.grade = (TextView) convertView.findViewById(R.id.grade);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) tag;
		}
		final Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		holder.submitBtn.setVisibility(View.INVISIBLE);
		holder.hotArea.setVisibility(View.INVISIBLE);
		holder.hotArea.getBackground().setAlpha(0);
		holder.submitBtn.setEnabled(false);
		holder.hotArea.setEnabled(false);
		holder.submitBtn.setTag(String.valueOf(map.get("uid")));
		holder.submitTxt.setVisibility(View.INVISIBLE);
		holder.submitBtn.setBackgroundResource(R.drawable.have_recommend_btn_selector);
		holder.submitTxt.setTextColor(convertView.getContext().getResources().getColor(R.color.common_btn_text_color));
		holder.submitTxt.setText("推荐");
		// 获取是否已经推荐信息
		getRecommendInfo(holder.submitBtn, holder.hotArea, holder.submitTxt,map);
		// 获取用户
		final long uid = Long.valueOf(map.get("uid").toString());
		viewholder_cache.put(uid, holder);
		convertView_cache.put(uid, convertView);
		// 获取贴吧
		final long gid = Long.valueOf(map.get("gid").toString());
		holder.submitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				recommendGame(holder.submitBtn, holder.hotArea, holder.submitTxt, gid, uid, map);
			}
		});
		holder.hotArea.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				holder.hotArea.setEnabled(false);
				recommendGame(holder.submitBtn, holder.hotArea, holder.submitTxt, gid, uid, map);
			}
		});

		// 显示头像
		String avatar = (String) map.get("avatar");
		holder.avatarView.setImageResource(R.drawable.common_default_icon);
		if(flag || imageview_cache.containsKey(uid)){
			imageview_cache.put(uid, true);
			ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_default_icon);
		}
		// 查找用户关注贴吧
		mGameFollowType = GAME_FOLLOW_TYPE_SELF;
		// 不显示距离
		holder.desc.setVisibility(View.INVISIBLE);
		holder.newtag.setVisibility(View.GONE);
		holder.newsTxt.setText("");
		if (map.get("mood") != null) {
			String mood = String.valueOf(map.get("mood"));
			holder.newsTxt.setText(mood);
			if (!mood.isEmpty()) {
				holder.newtag.setVisibility(View.VISIBLE);
				holder.newtag.setBackgroundResource(R.drawable.common_qian);
			}
		}
		UserVo uvo = userDao.getUserByUserId(uid);
		if(uservo_cache.containsKey(uid)){
			UserVo vo = uservo_cache.get(uid);
			renderUI(vo, convertView, holder);
		}else if(uvo != null){
			uservo_cache.put(uid, uvo);
			renderUI(uvo, convertView, holder);
		}else if(flag){
			searchUserDetail(uid, convertView, holder);
		}
		
		if(usernews_cache.containsKey(uid)){
			String news = usernews_cache.get(uid);
			holder.newsTxt.setText(news);
			if (!news.isEmpty()) {
				holder.newtag.setVisibility(View.VISIBLE);
				holder.newtag.setBackgroundResource(R.drawable.common_dong);
			}
		}else if(flag){
			getUserNews(uid, holder);	
		}
		return convertView;
	}


	/**
	 * 获取用户的动态
	 * @param uid
	 * @param holder
	 */
	private void getUserNews(final long uid, final ViewHolder holder) {
		ProxyFactory.getInstance().getUserProxy().getTrend(new ProxyCallBack<List<UserNewsVo>>() {

			@Override
			public void onSuccess(List<UserNewsVo> result) {
				if (result != null && result.size() > 0) {
					UserNewsVo vo = result.get(0);
					String news = vo.getNews();
					if (news != null) {
						usernews_cache.put(uid, news);
						holder.newsTxt.setText(vo.getNews());
						if (!news.isEmpty()) {
							holder.newtag.setVisibility(View.VISIBLE);
							holder.newtag.setBackgroundResource(R.drawable.common_dong);
						}
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

			}
		}, SystemContext.getInstance().getContext(), uid + "");
	}

	/**
	 * 获取推荐信息
	 */
	private void getRecommendInfo(final ImageView view, final ImageView hotArea, final TextView submitTxt, Map<String, Object>map) {
		if((Boolean)map.get("isrecommend")){
			view.setVisibility(View.VISIBLE);
			hotArea.setVisibility(View.VISIBLE);
			submitTxt.setVisibility(View.VISIBLE);
			view.setEnabled(false);
			hotArea.setEnabled(false);
			submitTxt.setTextColor(view.getResources().getColor(R.color.common_btn_dis_text_color));
			submitTxt.setText("已推荐");
		}else{
			view.setVisibility(View.VISIBLE);
			hotArea.setVisibility(View.VISIBLE);
			submitTxt.setVisibility(View.VISIBLE);
			view.setEnabled(true);
			hotArea.setEnabled(true);
			submitTxt.setTextColor(view.getResources().getColor(R.color.common_btn_text_color));
			submitTxt.setText("推荐");
		}
	}

	/**
	 * 推荐贴吧给好友
	 * 
	 * @param submit
	 * @param gid
	 * @param uid
	 */
	private void recommendGame(final View submit, final View hotArea, final TextView submitTxt, final long gid, final long uid, final Map<String, Object>map) {
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					submit.setEnabled(false);
					hotArea.setEnabled(false);
					submitTxt.setTextColor(submit.getResources().getColor(R.color.common_btn_dis_text_color));
					submitTxt.setText("已推荐");
					map.put("isrecommend", true);
					ToastUtil.showToast(submit.getContext(), submit.getContext().getString(R.string.game_recommend_success));

					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(loginUserVo.getUserid()));
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, loginUserVo.getUsername());
					ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(uid));
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_ID, String.valueOf(gid));
					UserVo uvo = DaoFactory.getDaoFactory().getUserDao(submit.getContext()).getUserById(uid);
					if (uvo != null)
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, uvo.getUsername());
					GameVo gvo = DaoFactory.getDaoFactory().getGameDao(submit.getContext()).getGameById(gid);
					if (gvo != null)
						ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_NAME, gvo.getGamename());
					MobclickAgent.onEvent(submit.getContext(), UMConfig.MSGS_EVENT_GAME_RECOMMEND, ummap);
					break;
				default:
					submit.setEnabled(true);
					hotArea.setEnabled(true);
					ToastUtil.showToast(submit.getContext(), submit.getContext().getString(R.string.game_recommend_fail));
					break;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				submit.setEnabled(true);
				hotArea.setEnabled(true);
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE:
					ToastUtil.showToast(submit.getContext(), submit.getContext().getString(R.string.game_recommend_fail_count_over));
					break;
				default:
					ToastUtil.showToast(submit.getContext(), submit.getContext().getString(R.string.game_recommend_fail));
					break;
				}
			}
		}, submit.getContext(), gid, MsgsConstants.OT_GAME, MsgsConstants.OP_RECOMMAND, String.valueOf(uid), null,null);
	}

	/**
	 * @param uid
	 */
	private void searchUserDetail(final long uid, final View convertView, final ViewHolder holder) {
		final UserVo uvo = null; 
		ServiceFactory.getInstance().getSyncEntityService().syncEntity(uid, SyncEntityService.TYPE_USER, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				UserVo vo = null;
				vo = userDao.getUserByUserId(uid);
				uservo_cache.put(uid, vo);
				LogUtil.e(TAG, "无同步用户实体数据::uid=" + uid);
				renderUI(vo, convertView, holder);
			}

			@Override
			public void onFailure(Integer result) {
				renderUI(uvo, convertView, holder);
				LogUtil.e(TAG, "同步用户实体失败::uid=" + uid);
			}

		});
	}

	/**
	 * 渲染UI
	 * 
	 * @param vo
	 */
	private void renderUI(UserVo vo, View convertView, ViewHolder holder) {
		if (vo != null) {
			if(vo.getAvatar()!= null && flag){
				ImageViewUtil.showImage(holder.avatarView, vo.getAvatar(), R.drawable.common_default_icon);
			}
			if(vo.getUsername() != null){
				holder.nickname.setText(vo.getUsername());
			}else{
				holder.nickname.setText("");
			}

			if (vo.getAge() > 0) {
				holder.age.setText(AgeUtil.convertAgeByBirth(vo.getAge()) + "");
				holder.age.setVisibility(View.VISIBLE);

				if(vo.getSex() == 0){
					Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					holder.age.setCompoundDrawables(sexdraw, null, null, null);
					holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
					holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				}else if(vo.getSex() == 1){
					Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					holder.age.setCompoundDrawables(sexdraw, null, null, null);
					holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
					holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
				}
			} else {
				holder.age.setText("");
				holder.age.setCompoundDrawablePadding(0);
				if(vo.getSex() == 0){
					Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					holder.age.setCompoundDrawables(sexdraw, null, null, null);
					holder.age.setCompoundDrawablePadding(0);
					holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
					holder.age.setVisibility(View.VISIBLE);
				}else if(vo.getSex() == 1){
					holder.age.setVisibility(View.VISIBLE);
					Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
					sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
					holder.age.setCompoundDrawables(sexdraw, null, null, null);
					holder.age.setCompoundDrawablePadding(0);
					holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
				}else{
					holder.age.setVisibility(View.GONE);
				}
			}
			if(vo.getGrade() > 0){
				holder.grade.setVisibility(View.VISIBLE);
				holder.grade.setText("LV"+vo.getGrade());
			}else{
				holder.grade.setVisibility(View.GONE);
			}

			String mood = vo.getMood();
			if(mood != null){
				holder.newsTxt.setText(mood);
				if (!mood.isEmpty()) {
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_qian);
				}
			}
			if(usernews_cache.containsKey(vo.getUserid())){
				String news = usernews_cache.get(vo.getUserid());
				holder.newsTxt.setText(news);
				if (!news.isEmpty()) {
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_dong);
				}
			}
		} else {
			return;
		}
	}

	/**
	 * 获取贴吧ICON
	 * 
	 * @param view
	 * @param gid
	 */
	protected void showGameIcon(final ImageView view, final long gid) {
		final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		GameVo result = gameDao.getGameByGameId(gid);
		if (result != null && result.getGamelogo() != null) {
			ImageViewUtil.showImage(view, result.getGamelogo(), R.drawable.common_default_icon);
		} else {
			view.setImageResource(R.drawable.common_default_icon);
		}
	}

	static class ViewHolder {
		ImageView avatarView;
		TextView desc;
		ImageView sex;
		TextView nickname;
		TextView age;
		TextView newsTxt;
		ImageView newtag;
		ImageView submitBtn;
		ImageView hotArea;
		TextView submitTxt;
		LinearLayout rightView;
		TextView rdesc;
		TextView grade;
	}

}
