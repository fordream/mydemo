/**      
 * SearchUserResultActicity.java Create on 2013-8-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.RelationGameDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.postbar.ui.ApplyMasterActivity;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.SelectorUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: SearchUserResultActicity
 * @Description: 搜索用户界面
 * @author 王卫
 * @date 2013-8-26 下午04:24:55
 * @Version 1.0
 * 
 */
public class UserListActicity extends BaseListActivity implements OnClickListener {

	protected static final String TAG = "UserListActicity";
	// 类型
	private int mType;
	// 搜索关键字
	private String mKeyWord;
	// 性别
	private int mSex;

	private Long mUid = null;

	private Long mGid = null;

	private ImageView rightMenu;

	private UserAdapter2 adapter;

	private LinearLayout nullcontent;

	private PullToRefreshListView pullToRefreshListView;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity#init()
	 */
	@Override
	protected void initialize() {
		super.initialize();
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mType = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TYPE);
			mKeyWord = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_KEYWORD);
			mSex = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX);
			Long uid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
			Long gid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			if (uid != null && uid > 0)
				mUid = uid;
			if (gid != null && gid > 0)
				mGid = gid;
			if (mType == UserAdapter2.TYPE_NULL) {
				titleTxt.setText("搜索结果");
			} else if (mType == UserAdapter2.TYPE_FATE) {
				titleTxt.setText("缘分好友");
				// 添加右边功能按钮
				setRightVisible(true);
				rightMenu = new ImageView(this);
				rightMenu.setBackgroundResource(R.drawable.commom_menu_again);
				LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
				rightView.addView(rightMenu);
				rightMenu.setOnClickListener(this);
			} else if (mType == UserAdapter2.TYPE_USER_FOLLW) {
				if (mSex == 0)
					titleTxt.setText("他的关注");
				else if(mSex == 1)
					titleTxt.setText("她的关注");
				else
					titleTxt.setText("Ta的关注");
			} else if (mType == UserAdapter2.TYPE_USER_FANS) {
				if (mSex == 0)
					titleTxt.setText("他的粉丝");
				else if(mSex == 1)
					titleTxt.setText("她的粉丝");
				else
					titleTxt.setText("Ta的粉丝");
			} else if (mType == UserAdapter2.TYPE_RECOMMEND) {
				setMode(Mode.DISABLED);
				titleTxt.setText("推荐用户");
				setRightVisible(true);
				rightMenu = new ImageView(this);
				rightMenu.setBackgroundResource(R.drawable.common_tab_btn_follow);
				LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
				rightView.addView(rightMenu, params);
				rightMenu.setOnClickListener(this);
				mLimit = 20;
				offsetMode = 0;
			} else if (mType == UserAdapter2.TYPE_POSTBAR_MANAGER) {
				setMode(Mode.DISABLED);
				// 吧主列表
				titleTxt.setText("吧主");
				setRightVisible(true);
				rightMenu = new ImageView(this);
				rightMenu.setBackgroundResource(R.drawable.common_tab_btn_apply);
				LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
				rightView.addView(rightMenu, params);
				rightMenu.setOnClickListener(this);
			}
		}
		// 添加内容视图
		View view = View.inflate(this, R.layout.common_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(view, params);
		nullcontent = (LinearLayout)view.findViewById(R.id.nullContent);
		// 初始化列表
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		if(mType == UserAdapter2.TYPE_FATE || mType == UserAdapter2.TYPE_RECOMMEND){
			setPullRefreshListView(pullToRefreshListView, Mode.DISABLED);
		}else{
			setPullRefreshListView(pullToRefreshListView);
		}
		// 设置列表适配器
		Boolean isSearchDetail = false;
		int mode = UserAdapter2.GAME_FOLLOW_TYPE_COMMON;
		if(mType == UserAdapter2.TYPE_POSTBAR_MANAGER)
			isSearchDetail = true;
		if(mType == UserAdapter2.TYPE_RECOMMEND){
			isSearchDetail = false;
			mode = UserAdapter2.GAME_FOLLOW_TYPE_SELF;
		}
		if(mType == UserAdapter2.TYPE_FATE)
			isSearchDetail = false;
		adapter = new UserAdapter2(UserListActicity.this, listData, R.layout.user_list_item_userfragment, new String[] { "nickname" },
				new int[] { R.id.nickname }, mode, isSearchDetail, mType,list);
		if(mType == UserAdapter2.TYPE_RECOMMEND){
			adapter.setmShowDis(false);
			adapter.setmIsSearchDetail(false);
		}else if(mType == UserAdapter2.TYPE_FATE){
			adapter.setmShowDis(true);
			adapter.setmIsSearchDetail(false);
		}else if(mType == UserAdapter2.TYPE_POSTBAR_MANAGER){
			adapter.setmShowDis(true);
			adapter.setmIsSearchDetail(true);
		}else{
			adapter.setmShowDis(true);
			adapter.setmIsSearchDetail(false);
		}
		setAdapter(adapter);

		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				Object uid = map.get("uid");
				if (uid != null && !uid.equals(SystemContext.getInstance().getExtUserVo().getUserid())) {
					Intent intent = new Intent(parent.getContext(), UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
					intent.putExtras(bundle);
					startActivity(intent);
				}else{
					ToastUtil.showToast(UserListActicity.this, UserListActicity.this.getString(R.string.check_information));
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		onresumeNeedRefresh = false;
		if(adapter != null){
			 adapter.setFlag(true);
			 adapter.notifyDataSetChanged();
		}
		if(mType == UserAdapter2.TYPE_POSTBAR_MANAGER && rightMenu != null){
			rightMenu.setClickable(true);
		}
	}

	/**
	 * 搜素用户
	 * 
	 * @param listView
	 */
	@Override
	protected void getListData(long offset, int limit) {
		super.getListData(offset, limit);
		if(adapter != null) adapter.setFlag(true);
		if (mType == UserAdapter2.TYPE_NULL) {
			searchUser(offset, limit);
		} else if (mType == UserAdapter2.TYPE_RECOMMEND) {
			searchRecommendUser();
		} else if (mType == UserAdapter2.TYPE_POSTBAR_MANAGER) {
			searchPostBarManager(mGid, 0, 0);
		} else {
			searchConditionUser(offset, limit);
		}
	}


	/**
	 * 无类型搜索用户
	 * 
	 * @param offset
	 * @param limit
	 */
	private void searchUser(long offset, int limit) {
		if(listData.size() <= 0){
			setMode(Mode.DISABLED);
			showLoadingUI();
		}else{
			setMode(Mode.PULL_UP_TO_REFRESH);
		}
		ProxyFactory.getInstance().getUserProxy().searchUser(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				showListView();
				setMode(Mode.PULL_UP_TO_REFRESH);
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					// 设置LIST数据
					addListData(result);
				} else {
					LogUtil.d(TAG, "搜索用户数据为空");
				}
				if (listData.size() <= 0)
					showNullBgView(R.drawable.common_no_seach_uers);
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				setMode(Mode.PULL_UP_TO_REFRESH);
				LogUtil.e(TAG, "获取列表数据失败");
				onFooterRefreshComplete();
			}
		}, this, mKeyWord, "3,4,5", offset, limit, null);
	}

	/**
	 * 获取推荐用户
	 */
	private void searchRecommendUser() {
		// 获取关注的贴吧id
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getInstance().getContext());
		List<GroupVo> list = groupDao.findAllGroups();
		String groups = getGroupIds(list);
		if (groups == null) {
			groups = SystemContext.getInstance().getRecommendGroups();
		}
		if (groups != null) {
			searchRecommendUser(groups);
		} else {
			showNullBgView(R.drawable.common_no_seach_uers);
			onFooterRefreshComplete();
		}
	}

	/**
	 * 搜索吧主
	 * 
	 * @param gid
	 * @param offset
	 * @param limit
	 */
	private void searchPostBarManager(long gid, long offset, int limit) {
		if(listData.size() <= 0) showLoadingUI();
		ProxyFactory.getInstance().getUserProxy().searchPostBarManager(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				showListView();
				if (result != null) {
					// 添加设置LIST数据
					addListData(result);
				} else {
					hasNext = false;
					LogUtil.d(TAG, "数据为空");
				}
				if (listData.size() <= 0)
					showNullBgView(R.drawable.common_no_seach_bazhu);
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				showListView();
				if (listData.size() <= 0)
					showNullBgView(R.drawable.common_no_seach_bazhu);
				LogUtil.e(TAG, "查询用户失败" + result);
				onFooterRefreshComplete();
			}
		}, this, gid, offset, limit);
	}

	/**
	 * 搜索条件用户
	 * 
	 * @param offset
	 * @param limit
	 */
	private void searchConditionUser(long offset, int limit) {
		if(listData.size() <= 0){
			setMode(Mode.DISABLED);
			showLoadingUI();
		}else{
			setMode(Mode.PULL_UP_TO_REFRESH);
		}
		Boolean isfate = null;
		Integer rel = null;
		Long gid = null;
		Boolean isrecommend = null;
		Integer nearDistance = null;
		String resulttype = "3,4,5";
		Integer source = null;
		if (mType == UserAdapter2.TYPE_FATE) {
			isfate = true;
			source = 3;
		} else if (mType == UserAdapter2.TYPE_USER_FOLLW) {
			rel = 1;
			source = 3;
		} else if (mType == UserAdapter2.TYPE_USER_FANS) {
			rel = 3;
			source = 3;
		}
		ProxyFactory.getInstance().getUserProxy().searchUsers(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				if(rightMenu != null)
					rightMenu.setEnabled(true);
				showListView();
				if(mType == UserAdapter2.TYPE_FATE) setMode(Mode.DISABLED);
				else setMode(Mode.PULL_UP_TO_REFRESH);
				if (result != null) {
					// 添加设置LIST数据
					addListData(result);
				} else {
					hasNext = false;
					LogUtil.d(TAG, "数据为空");
				}
				if (listData.size() <= 0)
					showNullBgView(R.drawable.common_no_seach_uers);
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if(rightMenu != null)
					rightMenu.setEnabled(true);
				setMode(Mode.PULL_UP_TO_REFRESH);
				if (listData.size() <= 0)
					showNullBgView(R.drawable.common_no_seach_uers);
				onFooterRefreshComplete();
				LogUtil.e(TAG, "查询用户失败" + result);
			}
		}, this, gid, isfate, isrecommend, rel, resulttype, mUid+"", offset, limit, nearDistance, null, null, null,source);
	}

	/**
	 * 搜素推荐用户
	 * 
	 * @param grids
	 */
	private void searchRecommendUser(String grids) {
		if(listData.size() <= 0)showLoadingUI();
		ProxyFactory.getInstance().getUserProxy().searchRecommendUsers(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				showListView();
				if (result != null && result.getItems() != null) {
					// 添加设置LIST数据
					addListData(result);
				} else {
					LogUtil.d(TAG, "数据为空");
				}
				if (listData.size() <= 0)
					showNullBgView(R.drawable.common_no_seach_uers);
				onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (listData.size() <= 0)
					showNullBgView(R.drawable.common_no_seach_uers);
				LogUtil.e(TAG, "获取推荐用户失败" + result);
				onFooterRefreshComplete();
			}
		}, this, grids, mOffset, mLimit);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(PagerVo<UserObject> result) {
		if (mType == UserAdapter2.TYPE_FATE || mType == UserAdapter2.TYPE_USER_FOLLW || mType == UserAdapter2.TYPE_USER_FANS || mType == UserAdapter2.TYPE_NULL)
			mOffset = result.getOffset();
		else
			mOffset = mOffset + Math.abs(mLimit);
		// 添加设置LIST数据
		listData.addAll(praseUserList(result.getItems()));
		if (result.getItems() != null) {
			if (result.getItems().size() < Math.abs(mLimit)) {
				hasNext = false;
			}
			if (result.getItems().size() > Math.abs(mLimit)) {
				list.setSelectionFromTop(list.getFirstVisiblePosition(), 0);
			}
		}
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 当没有数据的时候
	 * 显示loadingUI
	 */
	private void showLoadingUI(){
		showNullListView();		
		nullcontent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		nullcontent.addView(view, params);
	}

	/**
	 * 显示空背景
	 */
	private void showNullListView(){
		nullcontent.setVisibility(View.VISIBLE);
		pullToRefreshListView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示pulltorefreshListview
	 */
	private void showListView(){
		nullcontent.setVisibility(View.GONE);
		pullToRefreshListView.setVisibility(View.VISIBLE);
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseUserList(List<UserObject> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				UserObject vo = list.get(i);
				map.put("avatar", vo.getAvatar());
				map.put("nickname", vo.getNickname());
				map.put("news", vo.getMood());
				map.put("desc", "没有关注贴吧");
				map.put("sex", vo.getSex());
				map.put("age", vo.getAge());
				map.put("uid", vo.getUid());
				map.put("rel", vo.getRel());
				map.put("gids", vo.getGids());
				map.put("gameCount", vo.getGameCount());
				map.put("sortId", vo.getSortId());
				map.put("isChecked", false);
				map.put("grade", vo.getGrade());
				if(mType != UserAdapter2.TYPE_RECOMMEND){
					map.put("new", vo.getNews());
					String dis = DistanceUtil.covertDistance(vo.getDistance());
					if("".equals(dis)){
						map.put("distance", SafeUtils.getDate2MyStr2(vo.getLastLogin()));
					}else{
						map.put("distance", DistanceUtil.covertDistance(vo.getDistance()) + " | " + SafeUtils.getDate2MyStr2(vo.getLastLogin()));
					}
				}
				tmplist.add(map);
			}
		}
		return tmplist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == rightMenu.getId()) {
			if (mType == UserAdapter2.TYPE_FATE) {// 换一批
				rightMenu.setEnabled(false);
				refreshList(mOffset);
			} else if (mType == UserAdapter2.TYPE_RECOMMEND) {// 提交推荐用户
				praseFollowUser(listData);
			} else if (mType == UserAdapter2.TYPE_POSTBAR_MANAGER) {// 申请吧主
				rightMenu.setClickable(false);
					RelationGameDao relGameDao = DaoFactory.getDaoFactory().getRelationGameDao(this);
					RelationGameVo rgVo = relGameDao.getRelationGameByGameId(mGid);
					boolean isapp = true;
					if (rgVo != null) {
						if (rgVo.getIsbarmanager() == 1) {
							isapp = false;
						} else {
							isapp = true;
						}
					}
					if (isapp) {
						ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

							@Override
							public void onSuccess(Integer result) {
								if(result == 0){
									Intent intent = new Intent(UserListActicity.this, ApplyMasterActivity.class);
									Bundle bundle = new Bundle();
									bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, mGid);
									intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
									startActivity(intent);
								} else if(result == 1){
									rightMenu.setClickable(true);
									ToastUtil.showToast(UserListActicity.this, "你已申请过该贴吧吧主！");
								} else {
									rightMenu.setClickable(true);
									ToastUtil.showToast(UserListActicity.this, "获取已申请吧主次数失败，请稍后再试！");
								}
							}

							@Override
							public void onFailure(Integer result,String resultMsg) {
								ToastUtil.showToast(UserListActicity.this, "获取已申请吧主次数失败，请稍后再试！");
								rightMenu.setClickable(true);
							}
						};

						ProxyFactory.getInstance().getPostbarProxy().getMasterApplyCount(callback, this, mGid);
					} else {
						rightMenu.setClickable(true);
						ToastUtil.showToast(this, getResources().getString(R.string.user_list_bar_ismanager));
					}
				}
		}
	}

	/**
	 * 绑定手机提示框
	 * @param actionName
	 */
	private void createBundPhoneDialog(){
		Intent intent = new Intent(UserListActicity.this, BundPhoneActivity.class);
		startActivity(intent);
	}

	/**
	 * 
	 * @return
	 */
	private void praseFollowUser(List<Map<String, Object>> tmplist) {
		if (tmplist != null) {
			boolean hasCheck = false;
			for (int i = 0; i < tmplist.size(); i++) {
				Object isChecked = tmplist.get(i).get("isChecked");
				if (isChecked != null && (Boolean) isChecked) {
					hasCheck = true;
					followUser((Long) tmplist.get(i).get("uid"));
				}
			}
			if (!hasCheck) {
				ToastUtil.showToast(this, "请选择关注用户");
			} else {
				finish();
			}
		} else {
			ToastUtil.showToast(this, "请选择关注用户");
		}
	}

	/**
	 * 关注贴吧
	 * 
	 * @param gid
	 */
	private void followUser(long uid) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().addFollowUser(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "关注失败");
			}
		}, this, uid, false);
	}

	/**
	 * 获取公会IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getGroupIds(List<GroupVo> list) {
		if (list != null) {
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				strBuf.append(list.get(i).getGrid());
				strBuf.append(",");
			}
			if (strBuf.length() > 0)
				return strBuf.substring(0, strBuf.length() - 1);
			return null;
		} else {
			return null;
		}
	}

}
