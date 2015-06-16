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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.iwgame.msgs.common.ClearCacheListener;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.vo.local.GameVo;
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
public class DiscoverUserAdapter extends SimpleAdapter implements SectionIndexer,ClearCacheListener {

	protected static final String TAG = "UserAdapter";
	public static final int GAME_FOLLOW_TYPE_COMMON = 3;

	public static final int TYPE_NULL = 0;
	// 是否显示关注按钮
	private boolean mShowFollow = true;
	// 是否显示距离
	private boolean mShowDis = true;
	// 获取用户关注贴吧的类型[1没有2他关注3共同关注]
	protected int mGameFollowType;

	private boolean mShowNichen = false;

	// 距离右边距
	private int mDisRightMaginPX;
	// 信息缓存
	private Map<Long, Object> viewholder_cache = new HashMap<Long, Object>();
	private Map<Long, Boolean> avatar_cache = new HashMap<Long, Boolean>();
	private UserVo userVo;

	//这个布尔值 用于在发现页面原玩家的适配器，显示用户共同关注了多少贴吧，用gamecount字段来显示
	private boolean isshowcount = false;
	private int VIEW_TYPE = 2;
	final private int TYPE_DETAIL = 0;
	final private int TYPE_NODATA = 1;
	private int noDataHeight;//用于在发现页面当没有获取到数据的时候，显示默认图片的高度
	private ListView listView;
	private boolean flag = true;//第一次进入的时候要加载前面的几条数据，当滑动过后，就不去加载了

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public int getNoDataHeight() {
		return noDataHeight;
	}

	public void setNoDataHeight(int noDataHeight) {
		this.noDataHeight = noDataHeight;
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


	public DiscoverUserAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to, int gameFollowType,ListView listView) {
		super(context, data, resource, from, to);
		mGameFollowType = gameFollowType;
		userVo = SystemContext.getInstance().getExtUserVo();
		this.listView = listView;
		addListener();
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
	 * 获取到每一种数据的类型
	 */
	@Override
	public int getItemViewType(int position) {
		Map<String, Object> map = (HashMap<String, Object>) getItem(position);
		if(map.size() <= 0){
			return TYPE_NODATA;
		}else{
			return TYPE_DETAIL;
		}
	}


	/**
	 * 返回了适配器里面有几种类型
	 */
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int type = getItemViewType(position);
		ViewHolder holder1 = new ViewHolder();
		NodateViewHolder nodataHolder = new NodateViewHolder();
		if (convertView == null) {
			switch (type) {
			case TYPE_DETAIL:
				convertView = View.inflate(SystemContext.getInstance().getContext(), R.layout.user_list_item_userfragment, null);
				holder1.avatarView = (ImageView) convertView.findViewById(R.id.icon);
				holder1.desc = (TextView) convertView.findViewById(R.id.desc);
				holder1.sex = (ImageView) convertView.findViewById(R.id.sex);
				holder1.nickname = (TextView) convertView.findViewById(R.id.nickname);
				holder1.age = (TextView) convertView.findViewById(R.id.age);
				holder1.newsTxt = (TextView) convertView.findViewById(R.id.newsTxt);
				holder1.newtag = (ImageView) convertView.findViewById(R.id.newtag);
				holder1.submitBtn = (ImageView) convertView.findViewById(R.id.submitBtn);
				holder1.submitTxt = (TextView) convertView.findViewById(R.id.submitTxt);
				holder1.rightView = (LinearLayout) convertView.findViewById(R.id.rightView);
				holder1.rdesc = (TextView) convertView.findViewById(R.id.rdesc);
				holder1.grade = (TextView) convertView.findViewById(R.id.grade);
				convertView.setTag(holder1);
				break;

			case TYPE_NODATA:
				LinearLayout v = (LinearLayout)View.inflate(SystemContext.getInstance().getContext(), R.layout.no_data_view_discover, null);
				convertView = v;
				nodataHolder.bg_layout = v;
				nodataHolder.bgIcon = (ImageView)convertView.findViewById(R.id.bgIcon);
				convertView.setTag(nodataHolder);
				break;
			}
		} else {
			switch (type) {
			case TYPE_DETAIL:
				holder1 = (ViewHolder) convertView.getTag();
				break;

			case TYPE_NODATA:
				nodataHolder = (NodateViewHolder)convertView.getTag();
				break;
			}
		}

		final ViewHolder holder = holder1;
		final NodateViewHolder nodateViewHolder = nodataHolder;
		if(type == TYPE_DETAIL){
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (HashMap<String, Object>) getItem(position);
			// 获取用户
			final long uid = Long.valueOf(map.get("uid").toString());
			viewholder_cache.put(uid, holder);
			// 显示头像
			String avatar = (String) map.get("avatar");
			holder.avatarView.setImageResource(R.drawable.common_user_icon_default);
			if(flag || avatar_cache.containsKey(uid)){
				ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_user_icon_default);
				avatar_cache.put(uid, true);
			}
			// 查找用户关注贴吧
			holder.newtag.setVisibility(View.GONE);
			holder.newsTxt.setText("");
			holder.submitBtn.setVisibility(View.VISIBLE);
			holder.submitTxt.setVisibility(View.VISIBLE);
			if (map.containsKey("age") && (Integer) map.get("age") > 0) {
				holder.age.setText(AgeUtil.convertAgeByBirth((Integer) map.get("age")) + "");
				holder.age.setVisibility(View.VISIBLE);

				if(map.containsKey("sex")){
					if((Integer)map.get("sex") == 0){
						Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
						sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
						holder.age.setCompoundDrawables(sexdraw, null, null, null);
						holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
						holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
					}else if((Integer)map.get("sex") == 1){
						Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
						sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
						holder.age.setCompoundDrawables(sexdraw, null, null, null);
						holder.age.setCompoundDrawablePadding(DisplayUtil.dip2px(convertView.getContext(), 4));
						holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
					}
				}else{
					holder.age.setCompoundDrawablePadding(0);
					holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
				}
			} else {
				holder.age.setText("");
				holder.age.setCompoundDrawablePadding(0);
				if(map.containsKey("sex")){
					if((Integer)map.get("sex") == 0){
						Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_man_icon);
						sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
						holder.age.setCompoundDrawables(sexdraw, null, null, null);
						holder.age.setCompoundDrawablePadding(0);
						holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
					}else if((Integer)map.get("sex") == 1){
						Drawable sexdraw = convertView.getContext().getResources().getDrawable(R.drawable.user_woman_icon);
						sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
						holder.age.setCompoundDrawables(sexdraw, null, null, null);
						holder.age.setCompoundDrawablePadding(0);
						holder.age.setBackgroundResource(R.drawable.common_item_jh_shap);
					}else{
						holder.age.setCompoundDrawablePadding(0);
						holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
						holder.age.setVisibility(View.GONE);
					}
				}else{
					holder.age.setCompoundDrawablePadding(0);
					holder.age.setBackgroundResource(R.drawable.common_item_jh2_shap);
					holder.age.setVisibility(View.GONE);
				}
			}
			if(map.containsKey("new")){
				String news = (String) map.get("new");
				if (news != null && !"".equals(news)) {
					holder.newsTxt.setText(news);
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_dong);
				}else if (map.containsKey("news") && null != map.get("news")) {
					String mood = (String) map.get("news");
					holder.newsTxt.setText(mood);
					if (!mood.isEmpty()) {
						holder.newtag.setVisibility(View.VISIBLE);
						holder.newtag.setBackgroundResource(R.drawable.common_qian);
					}
				}
			}else if (map.containsKey("news") && null != map.get("news")) {
				String mood = (String) map.get("news");
				holder.newsTxt.setText(mood);
				if (!mood.isEmpty()) {
					holder.newtag.setVisibility(View.VISIBLE);
					holder.newtag.setBackgroundResource(R.drawable.common_qian);
				}
			}
			if(map.containsKey("nickname")){
				holder.nickname.setText((String)map.get("nickname"));
			}
			if (map.containsKey("grade") && (((Integer)map.get("grade"))>0)) {
				holder.grade.setVisibility(View.VISIBLE);
				holder.grade.setText("LV"+map.get("grade"));
			}else{
				holder.grade.setVisibility(View.GONE);
			}
			// 获取距离
			if (holder != null && holder.rdesc != null) {
				String distance = (String) map.get("distance");
				if (distance != null && !"".equals(distance) && mShowDis) {
					holder.rdesc.setVisibility(View.VISIBLE);
					holder.rdesc.setText(distance);
				} else {
					holder.rdesc.setVisibility(View.INVISIBLE);
				}
			}
			if (!mShowFollow) {
				holder.submitBtn.setVisibility(View.GONE);
				holder.submitTxt.setVisibility(View.GONE);
			}else{
				// 获取关注信息
				UserVo userVo1 = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext()).getUserByUserId(uid);
				if (userVo1 != null)
					map.put("rel", userVo1.getRelPositive());
				else
					map.put("rel", 0);
				final int rel = (Integer) map.get("rel");
				if (rel == 1) {
					holder.submitBtn.setEnabled(false);
				} else {
					holder.submitBtn.setEnabled(true);
				}
			}
			// 获取用户
			if (userVo != null && uid == userVo.getUserid()) {
				holder.submitBtn.setVisibility(View.GONE);
				holder.submitTxt.setVisibility(View.GONE);
			}
			holder.submitBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					holder.submitBtn.setEnabled(false);
					addFollow(holder.submitBtn, uid, position, holder.submitTxt);
				}
			});

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
			} else if (map.get("desc") != null) {
				holder.desc.setText((String) map.get("desc"));
			} else {
				if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
					holder.desc.setText("没有共同关注贴吧");
			}
		}else if(type == TYPE_NODATA){
			AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, noDataHeight);
			nodateViewHolder.bg_layout.setLayoutParams(params);
			nodateViewHolder.bgIcon.setBackgroundResource(R.drawable.common_no_seach_uers);
		}
		return convertView;
	}

	/**
	 * 添加监听器
	 * @param position
	 * @param convertView
	 * @param listView2
	 */
	private void addListener() {
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
					if(firstvisibleIndex > 0) firstvisibleIndex = firstvisibleIndex - 1;
					for(int i = firstvisibleIndex; i < lastvisibleIndex; i++){
						try {
							map = (HashMap<String, Object>) getItem(i);
							uid = Long.valueOf(map.get("uid").toString()); 
							holder = (ViewHolder)viewholder_cache.get(uid);
							avatar = (String) map.get("avatar");
							if(!avatar_cache.containsKey(uid)){
								ImageViewUtil.showImage(holder.avatarView, avatar, R.drawable.common_user_icon_default);
								avatar_cache.put(uid, true);
							}
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
	 * 关注
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


	private void createBundPhoneDialog(final View view){
		Intent intent = new Intent(view.getContext(), BundPhoneActivity.class);
		view.getContext().startActivity(intent);
	}


	/**
	 * 渲染条目信息
	 * 
	 * @param context
	 * @param uid
	 */
	protected void renderItem(final View view, final long uid, final ViewHolder holder, UserObject uobj) {
		if (uobj != null) {
			holder.rdesc.setVisibility(View.VISIBLE);
			holder.rightView.setPadding(0, 0, (int) DisplayUtil.sp2px(view.getContext(), mDisRightMaginPX), 0);
			String p = DistanceUtil.covertDistance(uobj.getPosition());
			String ltime = SafeUtils.getDate2MyStr2(uobj.getLastLogin());
			if ("".equals(p)) {
				holder.rdesc.setText(ltime);
			} else {
				holder.rdesc.setText(p + " | " + ltime);
			}
			if(mShowDis)holder.rdesc.setVisibility(View.VISIBLE);
			else holder.rdesc.setVisibility(View.INVISIBLE);
			// 获取关注的贴吧ID
			List<Long> list = uobj.getGids();
			if (list != null) {
				if (list.size() > 0) {
					if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
						holder.desc.setText("共同关注了" + list.size() + "个贴吧");
				} else {
					if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
						holder.desc.setText("没有共同关注贴吧");
				}
			}
		} else {
			if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
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
				if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
					desc.setText("共同关注了" + gids.size() + "个贴吧");
			} else {
				if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
					desc.setText("没有共同关注贴吧");
			}
		} else {
			if (mGameFollowType == GAME_FOLLOW_TYPE_COMMON)
				desc.setText("没有共同关注贴吧");
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
	 * 没有数据的时候的viewholder
	 * @author jczhang
	 *
	 */
	static class NodateViewHolder {
		LinearLayout bg_layout;
		ImageView bgIcon;
	}

	/**
	 * 清除缓存
	 */
	@Override
	public void clearCache() {
		viewholder_cache.clear();
		avatar_cache.clear();
	}

}
