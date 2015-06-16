/**      
 * NearUserAdapter.java Create on 2013-9-9     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.iwgame.msgs.common.ClearCacheListener;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.PinyinUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: NearUserAdapter
 * @Description: 用户列表适配器
 * @author 王卫
 * @date 2013-9-9 上午10:25:37
 * @Version 1.0
 * 
 */
public class UserAdapter2 extends SimpleAdapter implements SectionIndexer,ClearCacheListener {

	protected static final String TAG = "UserAdapter";
	public static final int GAME_FOLLOW_TYPE_NULL = 1;
	public static final int GAME_FOLLOW_TYPE_SELF = 2;
	public static final int GAME_FOLLOW_TYPE_COMMON = 3;
	// 类型0：按用户编号,按昵称1：附近的人2：缘分好友3：通讯录4：微博账号5：用户关注6：用户粉丝7：推荐用户8:吧主
	public static final int TYPE_NULL = 0;
	public static final int TYPE_NEAR = 1;
	public static final int TYPE_FATE = 2;
	public static final int TYPE_CONTRACT = 3;
	public static final int TYPE_WEIBO = 4;
	public static final int TYPE_USER_FOLLW = 5;
	public static final int TYPE_USER_FANS = 6;
	public static final int TYPE_RECOMMEND = 7;
	public static final int TYPE_POSTBAR_MANAGER = 8;
	public static final int TYPE_SUPPORT_USER = 9;

	// 是否显示关注按钮
	private boolean mShowFollow = true;
	// 是否显示距离
	private boolean mShowDis = true;
	// 获取用户关注贴吧的类型[1没有2他关注3共同关注]
	protected int mGameFollowType = GAME_FOLLOW_TYPE_NULL;
	private boolean mIsSearchDetail = false;
	private boolean mShowNichen = false;
	private int mode;
	// 距离右边距
	private int mDisRightMaginPX;
	// 信息缓存
	private Map<Long, UserObject> cache = new HashMap<Long, UserObject>();
	private Map<Long, UserVo> uservo_cache = new HashMap<Long, UserVo>();
	private UserVo userVo;
	private ListView listView;//添加滑动事件
	private boolean flag = true;//表示第一次进来的时候要加载数据
	//这个布尔值 用于在发现页面原玩家的适配器，显示用户共同关注了多少贴吧，用gamecount字段来显示
	private boolean isshowcount = false;
	private Map<Long, Object> viewholder_cache = new HashMap<Long, Object>();
	private Map<Long, View>convertview_cache = new HashMap<Long, View>();
	private Map<Long, Boolean> avatar_cache = new HashMap<Long, Boolean>();//用来判定头像是否已经加载过
	private UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());;
	private Map<Long, String> dis;
	private Map<Long, String> temp;//这个只是中间的一个过度的变量

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isIsshowcount() {
		return isshowcount;
	}

	public void setIsshowcount(boolean isshowcount) {
		this.isshowcount = isshowcount;
	}

	public int getmDisRightMaginPX() {
		return mDisRightMaginPX;
	}

	public void setmDisRightMaginPX(int mDisRightMaginPX) {
		this.mDisRightMaginPX = mDisRightMaginPX;
	}

	public boolean ismShowNichen() {
		return mShowNichen;
	}

	public void setmShowNichen(boolean mShowNichen) {
		this.mShowNichen = mShowNichen;
	}

	public boolean ismIsSearchDetail() {
		return mIsSearchDetail;
	}

	public void setmIsSearchDetail(boolean mIsSearchDetail) {
		this.mIsSearchDetail = mIsSearchDetail;
	}

	public boolean ismShowDis() {
		return mShowDis;
	}

	public void setmShowDis(boolean mShowDis) {
		this.mShowDis = mShowDis;
	}

	public boolean ismShowFollow() {
		return mShowFollow;
	}

	public void setmShowFollow(boolean showFollow) {
		this.mShowFollow = showFollow;
	}



	/**
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 */
	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,ListView listView) {
		super(context, data, resource, from, to);
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener(context);
	}

	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, boolean showFollow,
			boolean isSearchDetail,ListView listView) {
		super(context, data, resource, from, to);
		mShowFollow = showFollow;
		mIsSearchDetail = isSearchDetail;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener(context);
	}

	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int gameFollowType,
			boolean isSearchDetail, int mode,ListView listView) {
		super(context, data, resource, from, to);
		mGameFollowType = gameFollowType;
		mIsSearchDetail = isSearchDetail;
		this.mode = mode;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener(context);
	}

	/**
	 * 这个适配器 的构造方法是专门用于赞列表界面的适配器 构造方法
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 * @param gameFollowType
	 * @param isSearchDetail
	 * @param mode
	 * @param listView
	 */
	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,
			int mode,ListView listView) {
		super(context, data, resource, from, to);
		this.mode = mode;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener(context);
	}

	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, boolean showFollow,
			int gameFollowType, boolean isSearchDetail,ListView listView) {
		super(context, data, resource, from, to);
		mShowFollow = showFollow;
		mGameFollowType = gameFollowType;
		mIsSearchDetail = isSearchDetail;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener(context);
	}

	/**
	 * 这个构造方法是专门用于
	 * 在通讯录页面的关注和粉丝列表
	 * @param context
	 * @param data
	 * @param resource
	 * @param from
	 * @param to
	 * @param showFollow
	 * @param gameFollowType
	 * @param isSearchDetail
	 * @param listView
	 * @param map
	 */
	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, boolean showFollow,
			int gameFollowType, boolean isSearchDetail,ListView listView,Map<Long, String> map) {
		super(context, data, resource, from, to);
		mShowFollow = showFollow;
		mGameFollowType = gameFollowType;
		mIsSearchDetail = isSearchDetail;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		this.dis = map;
		this.temp = map;
		addListener(context);
	}

	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, boolean showFollow,
			int gameFollowType, boolean isSearchDetail, int disRightMaginPX,ListView listView) {
		super(context, data, resource, from, to);
		mShowFollow = showFollow;
		mGameFollowType = gameFollowType;
		mIsSearchDetail = isSearchDetail;
		mDisRightMaginPX = disRightMaginPX;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener(context);
	}


	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, boolean showFollow,
			int gameFollowType, boolean isSearchDetail, int disRightMaginPX,boolean showNichen,ListView listView) {
		super(context, data, resource, from, to);
		mShowFollow = showFollow;
		mGameFollowType = gameFollowType;
		mIsSearchDetail = isSearchDetail;
		mDisRightMaginPX = disRightMaginPX;
		mShowNichen = showNichen;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener(context);
	}

	public UserAdapter2(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, boolean showFollow,
			int gameFollowType, boolean isSearchDetail, boolean showDis,ListView listView) {
		super(context, data, resource, from, to);
		mShowFollow = showFollow;
		mGameFollowType = gameFollowType;
		mIsSearchDetail = isSearchDetail;
		mShowDis = showDis;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener(context);
	}

	/**
	 * 执行这个方法 的时候
	 * 对uservo初始化
	 */
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		userVo = SystemContext.getInstance().getExtUserVo();
	}

	/**
	 * 返回了要显示到item上面的view
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = super.getView(position, convertView, parent);
		Object tag = convertView.getTag();
		final ViewHolder holder;
		if (tag == null) {
			holder = new ViewHolder();
			holder.avatarView = (ImageView) convertView.findViewById(R.id.icon);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.sex = (ImageView) convertView.findViewById(R.id.sex);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.age = (TextView) convertView.findViewById(R.id.age);
			holder.newsTxt = (TextView) convertView.findViewById(R.id.newsTxt);
			holder.newtag = (ImageView) convertView.findViewById(R.id.newtag);
			holder.submitBtn = (ImageView) convertView.findViewById(R.id.submitBtn);
			holder.submitTxt = (TextView) convertView.findViewById(R.id.submitTxt);
			holder.rightView = (LinearLayout) convertView.findViewById(R.id.rightView);
			holder.rdesc = (TextView) convertView.findViewById(R.id.rdesc);
			holder.grade = (TextView) convertView.findViewById(R.id.grade);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) tag;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		// 获取用户
		final long uid = Long.valueOf(map.get("uid").toString());
		viewholder_cache.put(uid, holder);
		convertview_cache.put(uid, convertView);
		// 显示头像
		String avatar = (String) map.get("avatar");
		holder.avatarView.setImageResource(R.drawable.common_user_icon_default);
		if(flag || avatar_cache.containsKey(uid)){
			ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_user_icon_default);
			avatar_cache.put(uid, true);
		}
		//如果是赞用户列表页面
		if(mode == TYPE_SUPPORT_USER){
			showSupportUser(holder,map,convertView);//显示赞用户列表
			//关注按钮的点击事件
			holder.submitBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					holder.submitBtn.setEnabled(false);
					addFollow(holder.submitBtn, uid, position, holder.submitTxt);
				}
			});
			return convertView;
		}

		// 查找用户关注贴吧
		holder.newtag.setVisibility(View.GONE);
		holder.newsTxt.setText("");
		holder.submitBtn.setVisibility(View.VISIBLE);
		holder.submitTxt.setVisibility(View.VISIBLE);

		//显示用户的年龄和性别 
		int age = 0;
		int sex = 2;
		if (map.containsKey("age")) {
			age = (Integer) map.get("age");
		}
		if(map.containsKey("sex")){
			sex = (Integer)map.get("sex");
		}
		setSexAddAge(holder, convertView, age, sex);


		//判断等级是否显示
		if (map.containsKey("grade") && (((Integer)map.get("grade"))>0)) {
			holder.grade.setVisibility(View.VISIBLE);
			holder.grade.setText("LV"+map.get("grade"));
		}else{
			holder.grade.setVisibility(View.GONE);
		}


		//判断是是不是推荐用户
		if (mode == TYPE_RECOMMEND) {// 设置推荐用户
			// 是否关注
			addCheckBoxView(convertView, holder.rightView, map);
		} else if (mode == TYPE_POSTBAR_MANAGER) {
			// 类型为吧主列表
			if (userVo != null && userVo.getUserid() != uid) {
				holder.submitBtn.setEnabled(true);
				holder.submitBtn.setBackgroundResource(R.drawable.common_btn_selector);
				holder.submitTxt.setTextColor(convertView.getResources().getColor(R.color.common_btn_text_color));
				holder.submitTxt.setText("对话");
				holder.submitBtn.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						chat(v, uid);
					}
				});
			} else {
				holder.submitBtn.setVisibility(View.GONE);
				holder.submitTxt.setVisibility(View.GONE);
			}
		} else {//不属于推荐用户和吧主列表

			//判断是否显示距离 和时间
			if (holder != null && holder.rdesc != null) {
				if (mShowDis && map.containsKey("distance")) {
					String distance = (String) map.get("distance");
					if(distance != null && !"".equals(distance)){
						holder.rdesc.setVisibility(View.VISIBLE);
						holder.rdesc.setText(distance);
					}else{
						holder.rdesc.setVisibility(View.INVISIBLE);
					}
				} else {
					holder.rdesc.setVisibility(View.INVISIBLE);
				}
			}

			//判断是否显示关注按钮
			if (!mShowFollow) {
				holder.submitBtn.setVisibility(View.GONE);
				holder.submitTxt.setVisibility(View.GONE);
			}else{
				// 获取关注信息，如果要显示关注按钮 ，先获取关注信息，在来判断显示与否
				UserVo userVo1 = userDao.getUserByUserId(uid);
				if (userVo1 != null)
					map.put("rel", userVo1.getRelPositive());
				else
					map.put("rel", 0);
				int rel = (Integer) map.get("rel");
				if (rel == 1) {
					holder.submitBtn.setEnabled(false);
				} else {
					holder.submitBtn.setEnabled(true);
				}
			}

			// 判断是否显示右边的按钮
			if (userVo != null && uid == userVo.getUserid()) {
				holder.submitBtn.setVisibility(View.GONE);
				holder.submitTxt.setVisibility(View.GONE);
			}

			//关注按钮的点击事件
			holder.submitBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					holder.submitBtn.setEnabled(false);
					addFollow(holder.submitBtn, uid, position, holder.submitTxt);
				}
			});
		}

		//接下来是另外一个分支，显示动态的相关代码
		if(map.containsKey("new")){
			//先从传过来的数据源里面判断
			String news = (String) map.get("new");
			if (news != null && !news.isEmpty()) {
				holder.newsTxt.setText(news);
				holder.newtag.setVisibility(View.VISIBLE);
				holder.newtag.setBackgroundResource(R.drawable.common_dong);
			}
		}else if(flag && !mIsSearchDetail){
			//从网络中取数据
			searchUser(convertView, uid, holder, map,position);
		}

		if (mIsSearchDetail) {
			UserVo uvo = userDao.getUserByUserId(uid);
			if(uvo != null){
				uservo_cache.put(uid, uvo);
				renderUI(uvo, convertView, holder, map);
			}else if(uservo_cache.containsKey(uid)){
				UserVo vo = uservo_cache.get(uid);
				if(uvo != null && uvo.getRemarksName() != null && !"".equals(uvo.getRemarksName())){
					vo.setRemarksName(uvo.getRemarksName());
					uservo_cache.put(uid, vo);
				}
				renderUI(vo, convertView, holder, map);
			}else if(flag){
				searchUserDetail(uid, convertView, holder,map, position);
			}
			if (cache.containsKey(uid)) {
				UserObject uobj = cache.get(uid);
				ImageViewUtil.showImage(holder.avatarView, uobj.getAvatar(), R.drawable.common_user_icon_default);
				renderItem(convertView, uid, holder, uobj, position);
			}else if(flag){
				searchUser(convertView, uid, holder, map, position);
			}
		} else {
			if (map.get("gids") != null && map.get("gameCount") != null) {
				if(isshowcount){
					int count = (Integer)map.get("gameCount");
					if(count > 0){
						holder.desc.setText("共同关注了" + count + "个贴吧");
					}else{
						holder.desc.setText("没有共同关注贴吧");
					}
				}else{
					renderGameLabelUI(convertView, (List<Long>) map.get("gids"), (Integer) map.get("gameCount"), holder.desc);
				}
			} else if (map.get("desc") != null && mode != TYPE_RECOMMEND) {
				holder.desc.setText((String) map.get("desc"));
			} else {
				if(mode == TYPE_RECOMMEND) holder.desc.setVisibility(View.INVISIBLE);
				else if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
					holder.desc.setText("没有关注贴吧");
				else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
					holder.desc.setText("没有共同关注贴吧");
			}
		}

		if(temp != null && temp.containsKey(uid) && mShowDis){
			String distance = temp.get(uid);
			if(distance != null && !"".equals(distance)){
				holder.rdesc.setVisibility(View.VISIBLE);
				holder.rdesc.setText(distance);
			}
		}
		return convertView;
	}

	/**
	 * 显示赞用户列表的数据
	 * 只有赞用户列表  才会执行这个方法 
	 * @param holder
	 * @param map
	 */
	private void showSupportUser(ViewHolder holder, Map<String, Object> map, View convertView) {
		String s = "";
		if(map.containsKey("nickname")){
			s = (String)map.get("nickname");
			holder.nickname.setText(s);
		}
		//显示用户的年龄和性别 
		int age = 0;
		int sex = 2;
		if (map.containsKey("age")) {
			age = (Integer) map.get("age");
		}
		if(map.containsKey("sex")){
			sex = (Integer)map.get("sex");
		}
		setSexAddAge(holder, convertView, age, sex);

		//判断等级是否显示
		if (map.containsKey("grade") && (((Integer)map.get("grade"))>0)) {
			holder.grade.setVisibility(View.VISIBLE);
			holder.grade.setText("LV"+map.get("grade"));
		}else{
			holder.grade.setVisibility(View.GONE);
		}
		//显示共同关注了多少款游戏
		if (map.containsKey("gamecount")) {
			int count = (Integer)map.get("gamecount");
			if(count > 0){
				holder.desc.setVisibility(View.VISIBLE);
				holder.desc.setText("共同关注了" + count + "款游戏");
			}else{
				holder.desc.setVisibility(View.INVISIBLE);
			}
		}
		//显示动态或者签名
		if(map.containsKey("new")){
			//先从传过来的数据源里面判断
			String news = (String) map.get("new");
			if (news != null && !news.isEmpty()) {
				holder.newsTxt.setVisibility(View.VISIBLE);
				holder.newtag.setVisibility(View.VISIBLE);
				holder.newsTxt.setText(news);
				holder.newtag.setVisibility(View.VISIBLE);
				holder.newtag.setBackgroundResource(R.drawable.common_dong);
			}else if(map.containsKey("news")){
				//先从传过来的数据源里面判断
		        news = (String) map.get("news");
				if (news != null && !news.isEmpty()) {
					holder.newsTxt.setVisibility(View.VISIBLE);
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newsTxt.setText(news);
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_qian);
				}else{
					holder.newsTxt.setVisibility(View.INVISIBLE);
					holder.newtag.setVisibility(View.INVISIBLE);
				}
			}
		}else if(map.containsKey("news")){
			    String news = (String) map.get("news");
				if (news != null && !news.isEmpty()) {
					holder.newsTxt.setVisibility(View.VISIBLE);
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newsTxt.setText(news);
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_qian);
				}else{
					holder.newsTxt.setVisibility(View.INVISIBLE);
					holder.newtag.setVisibility(View.INVISIBLE);
				}
			}
		
		//显示赞的时间
		if(map.containsKey("suptime")){
			String supportString = (String)map.get("suptime");
			holder.rdesc.setText(supportString);
		}
		final long uid = (Long)map.get("uid");
		UserVo userVo1 = userDao.getUserByUserId(uid);
		//是否显示关注按钮, 获取关注信息，如果要显示关注按钮 ，先获取关注信息，在来判断显示与否
		if(userVo1 != null){
			if(userVo != null && userVo1.getUserid() == userVo.getUserid()) {
				holder.submitBtn.setVisibility(View.INVISIBLE);
				holder.submitBtn.setEnabled(false);
			}else{
				holder.submitBtn.setVisibility(View.VISIBLE);
				if (userVo1.getRelPositive() == 1) {
					holder.submitBtn.setEnabled(false);
				} else {
					holder.submitBtn.setEnabled(true);
				}
			}
		}else{
			holder.submitBtn.setVisibility(View.VISIBLE);
			holder.submitBtn.setEnabled(true);
		}
	}

	/**
	 * 添加监听器，当滑动停止的时候
	 * 在去加载数据
	 * @param position
	 * @param convertView
	 * @param listView2
	 */
	private void addListener( final Context context) {
		listView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int state) {
				if(state == OnScrollListener.SCROLL_STATE_IDLE){
					int firstvisibleIndex = listView.getFirstVisiblePosition();
					int lastvisibleIndex = listView.getLastVisiblePosition();
					ViewHolder holder;
					Map<String, Object> map;
					long uid;
					String avatar;
					View convertView;
					if(firstvisibleIndex > 0) firstvisibleIndex = firstvisibleIndex - 1;
					for(int i = firstvisibleIndex; i < lastvisibleIndex; i++){
						try {
							map = (HashMap<String, Object>) getItem(i);
							uid = Long.valueOf(map.get("uid").toString()); 
							holder = (ViewHolder)viewholder_cache.get(uid);
							convertView = convertview_cache.get(uid);
							avatar = (String) map.get("avatar");
							ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_user_icon_default);
							avatar_cache.put(uid, true);
							if(mode == TYPE_SUPPORT_USER) continue;
							if(mIsSearchDetail && !uservo_cache.containsKey(uid))
								searchUserDetail(uid, convertView, holder, map,i);
							else
								searchUser(convertView, uid, holder, map, i);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else if(state == OnScrollListener.SCROLL_STATE_FLING){
					flag = false;
				}else if(state == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					flag = false;
				}
			}
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});
	}



	/**
	 * @param uid
	 */
	private void searchUserDetail(final long uid, final View convertView, final ViewHolder holder,final Map<String, Object>map,final int position) {
		ServiceFactory.getInstance().getSyncEntityService().syncEntity(uid, SyncEntityService.TYPE_USER, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				UserVo vo = null;
				vo = userDao.getUserByUserId(uid);
				uservo_cache.put(uid, vo);
				LogUtil.e(TAG, "无同步用户实体数据::uid=" + uid);
				renderUI(vo, convertView, holder, map);
				searchUser(convertView, uid, holder, map, position);
			}

			@Override
			public void onFailure(Integer result) {
				UserVo uvo = userDao.getUserByUserId(uid);
				renderUI(uvo, convertView, holder,map);
				searchUser(convertView, uid, holder, map, position);
				LogUtil.e(TAG, "同步用户实体失败::uid=" + uid);
			}
		});
	}

	/**
	 * 渲染UI
	 * 
	 * @param vo
	 */
	private void renderUI(UserVo vo, View convertView, ViewHolder holder,Map<String , Object>map) {
		if (vo != null) {
			if(mShowNichen){
				if(vo.getRemarksName() != null && !"".equals(vo.getRemarksName())){
					holder.nickname.setText(vo.getRemarksName());
				}else if(map.containsKey("remarkname") && !"".equals(map.get("remarkname"))){
					holder.nickname.setText((String)map.get("remarkname"));
				}else{
					holder.nickname.setText(vo.getUsername());
				}
			}else if (vo.getUsername() != null) {
				holder.nickname.setText(vo.getUsername());
			}
			setSexAddAge(holder, convertView, vo.getAge(), vo.getSex());
			if(vo.getGrade() > 0){
				holder.grade.setVisibility(View.VISIBLE);
				holder.grade.setText("LV"+vo.getGrade());
			}else{
				holder.grade.setVisibility(View.GONE);
			}
		} else {
			if(mShowNichen && map.containsKey("remarkname") && !"".equals(map.get("remarkname"))){
				holder.nickname.setText((String)map.get("remarkname"));
			}
		}
	}


	/**
	 * 设置用户的年龄和性别的方法 
	 */

	private void setSexAddAge(ViewHolder holder,View convertView,int age, int sex){
		if (age > 0) {
			holder.age.setText(AgeUtil.convertAgeByBirth(age) + "");
			holder.age.setVisibility(View.VISIBLE);

			if(sex == 0){
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
			}else if(sex == 1){
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
				holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
			}else{
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
			}
		} else {
			holder.age.setText("");
			holder.age.setCompoundDrawablePadding(0);
			if(sex == 0){
				Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
				sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
				holder.age.setCompoundDrawables(sexdraw, null, null, null);
				holder.age.setCompoundDrawablePadding(0);
				holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				holder.age.setVisibility(View.VISIBLE);
			}else if(sex == 1){
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
	}


	/**
	 * 如果当前的这个用户是我没有关注的
	 * 那么 当点击右边的按钮的时候
	 * 则执行下面的关注的方法
	 * 
	 * @param hotArea
	 * @param uid
	 */
	protected void addFollow(final View submit, final long uid, final int position, final TextView submitTxt) {
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(submit.getContext());
			dialog.show();
			ProxyFactory.getInstance().getUserProxy().addFollowUser(new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					if (result == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE) {
						Map<String, Object> map = (HashMap<String, Object>) getItem(position);
						map.put("rel", 1);
						submit.setEnabled(false);
						HashMap<String, String> ummap = new HashMap<String, String>();
						ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(userVo.getUserid()));
						ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, userVo.getUsername());
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(uid));
						UserVo uvo = DaoFactory.getDaoFactory().getUserDao(submit.getContext()).getUserById(uid);
						if (uvo != null)
							ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, uvo.getUsername());
						MobclickAgent.onEvent(submit.getContext(), UMConfig.MSGS_EVENT_USER_FOLLOW, ummap);
					} else {
						submit.setEnabled(true);
						LogUtil.e(TAG, "关注返回错误");
					}
					dialog.dismiss();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					dialog.dismiss();
					submit.setEnabled(true);
					ToastUtil.showToast(submit.getContext(), "关注失败");
				}
			}, submit.getContext(), uid, false);
		}


	/**
	 * 当点击添加关注的时候 ，
	 * 如果需要绑定手机号 
	 * 则跳转到绑定手机号的页面
	 * 如果不需要绑定手机号 
	 * 则执行关注的方法
	 * @param view
	 */
	private void createBundPhoneDialog(final View view){
		Intent intent = new Intent(view.getContext(), BundPhoneActivity.class);
		view.getContext().startActivity(intent);
	}



	/**
	 * 如果是吧主列表 
	 * 则右边的按钮是对话按钮 
	 * 当点击这个按钮的时候 
	 * 跳转到对话界面
	 * 
	 * @param uid
	 */
	private void chat(View v, long uid) {
		Intent intent = new Intent(v.getContext(), UserChatFragmentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOUSERID, uid);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		v.getContext().startActivity(intent);
	}

	/**
	 * 查询用户关注了那些贴吧
	 * 
	 * @param context
	 * @param uid
	 */
	protected void searchUser(final View view, final long uid, final ViewHolder holder,Map<String , Object> map,final int position) {
		ProxyFactory.getInstance().getUserProxy().searchUsers(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				if (result != null && result.getItems() != null && result.getItems().size() == 1) {
					UserObject uobj = result.getItems().get(0);
					cache.put(uobj.getUid(), uobj);
					ImageViewUtil.showImage(holder.avatarView, uobj.getAvatar(), R.drawable.common_user_icon_default);
					renderItem(view, uid, holder, uobj, position);
				} else {
					if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
						holder.desc.setText("没有关注贴吧");
					else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
						holder.desc.setText("没有共同关注贴吧");
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
					holder.desc.setText("没有关注贴吧");
				else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
					holder.desc.setText("没有共同关注贴吧");
			}

		}, SystemContext.getInstance().getContext(), null, null, null, null,"3,4,5", uid+"", Long.MAX_VALUE, -4, null, null, null, null, 4);
	}

	/**
	 * 渲染条目信息
	 * 
	 * @param context
	 * @param uid
	 */
	protected void renderItem(final View view, final long uid, final ViewHolder holder, UserObject uobj, int position) {
		if(position >= getCount())
			return;
		if(uobj != null){
			String news = uobj.getNews();
			if (news != null && !news.isEmpty()) {
				Map<String, Object> map = (HashMap<String, Object>) getItem(position);
				map.put("new", news);
				holder.newsTxt.setText(news);
				holder.newtag.setVisibility(View.VISIBLE);
				holder.newtag.setBackgroundResource(R.drawable.common_dong);
			}else{
				String mood = uobj.getMood();
				if (!mood.isEmpty()) {
					holder.newsTxt.setText(mood);
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_qian);
				}
			}
		}

		if (uobj != null) {
			holder.rdesc.setVisibility(View.VISIBLE);
			holder.rightView.setPadding(0, 0, (int) DisplayUtil.sp2px(view.getContext(), mDisRightMaginPX), 0);
			String p = DistanceUtil.covertDistance(uobj.getDistance());
			String ltime = SafeUtils.getDate2MyStr2(uobj.getLastLogin());
			String s = "";
			if ("".equals(p)) {
				holder.rdesc.setText(ltime);
				s = ltime;
			} else {
				holder.rdesc.setText(p + " | " + ltime);
				s = p + "|" + ltime;
			}
			if(mGameFollowType == GAME_FOLLOW_TYPE_COMMON && dis != null)
				dis.put(uid, s);
			if(mShowDis)holder.rdesc.setVisibility(View.VISIBLE);
			else holder.rdesc.setVisibility(View.INVISIBLE);
			// 获取关注的贴吧ID
			List<Long> list = uobj.getGids();
			if (list != null) {
				if (list.size() > 0) {
					if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
						holder.desc.setText("关注了" + list.size() + "个贴吧");
					else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
						holder.desc.setText("共同关注了" + list.size() + "个贴吧");
				} else {
					if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
						holder.desc.setText("没有关注贴吧");
					else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
						holder.desc.setText("没有共同关注贴吧");
				}
			}
		} else {
			if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
				holder.desc.setText("没有关注贴吧");
			else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
				holder.desc.setText("没有共同关注贴吧");
		}
	}

	/**
	 * 渲染界面贴吧相关元素
	 * 
	 * @param view
	 * @param condition
	 * @param gids
	 * @param gameCount
	 */
	protected void renderGameLabelUI(final View view, List<Long> gids, int gameCount, TextView desc) {
		if (gids != null) {
			if (gids.size() > 0) {
				if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
					desc.setText("关注了" + gids.size() + "个贴吧");
				else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
					desc.setText("共同关注了" + gids.size() + "个贴吧");
			} else {
				if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
					desc.setText("没有关注贴吧");
				else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
					desc.setText("没有共同关注贴吧");
			}
		} else {
			if (mGameFollowType == GAME_FOLLOW_TYPE_SELF)
				desc.setText("没有关注贴吧");
			else if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
				desc.setText("没有共同关注贴吧");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SectionIndexer#getSections()
	 */
	@Override
	public Object[] getSections() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SectionIndexer#getPositionForSection(int)
	 */
	@Override
	public int getPositionForSection(int section) {
		if (section == '!') {
			return 0;
		} else {
			for (int i = 0; i < getCount(); i++) {
				Map<String, Object> map = (HashMap<String, Object>) getItem(i);
				String nickname = (String) map.get("nickname");
				if (nickname != null) {
					String headChar = PinyinUtil.getPinYinHeadChar(nickname);
					char firstChar = headChar.toUpperCase().charAt(0);
					if (firstChar == section) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SectionIndexer#getSectionForPosition(int)
	 */
	@Override
	public int getSectionForPosition(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 添加复选框
	 * 
	 * @param convertView
	 * @param rightView
	 * @param mapData
	 */
	private void addCheckBoxView(View convertView, LinearLayout rightView, final Map<String, Object> mapData) {
		// 添加CHECKBOX
		rightView.removeAllViews();
		LinearLayout boxLayout = (LinearLayout) View.inflate(convertView.getContext(), R.layout.common_checkbox, null);
		final CheckBox cbox = (CheckBox) boxLayout.findViewById(R.id.checkBox);
		rightView.addView(boxLayout);
		if(mapData.containsKey("isChecked")){
			cbox.setChecked((Boolean)mapData.get("isChecked"));
		}else{
			cbox.setChecked(false);
		}
		cbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mapData != null) {
					mapData.put("isChecked", isChecked);
				}
			}
		});
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cbox != null) {
					if (cbox.isChecked()) {
						cbox.setChecked(false);
						mapData.put("isChecked", false);
					} else {
						cbox.setChecked(true);
						mapData.put("isChecked", true);
					}
				}
			}
		});
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
		TextView submitTxt;
		LinearLayout rightView;
		TextView rdesc;
		TextView grade;
	}

	/**
	 * 清除缓存
	 */
	@Override
	public void clearCache() {
		cache.clear();
		uservo_cache.clear();
		viewholder_cache.clear();
		convertview_cache.clear();
	}

}
