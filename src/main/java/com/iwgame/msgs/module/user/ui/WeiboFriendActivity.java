/**      
 * ContactFriendActivity.java Create on 2014-4-23     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.game.adapter.ViewPagerAdapter;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.user.adapter.CommonUserAdapter;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.module.user.object.ContactObj;
import com.iwgame.msgs.utils.GuideUtil;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;

/**
 * @ClassName: ContactFriendActivity
 * @Description: 添加微博好友主界面
 * @author 王卫
 * @date 2014-4-23 下午3:33:12
 * @Version 1.0
 * 
 */
public class WeiboFriendActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "WeiboFriendActivity";
	// 切换页面组件
	private ViewPager viewPager = null;
	// 页面集合列表
	private List<View> pageViews = null;
	// 显示内容列表
	private List<View> viewList = null;
	// 待关注内容
	private CommonListView followView;
	// 待邀请内容
	private CommonListView inviteView;
	// 待关注标签
	private TextView followTab;
	// 待邀请标签
	private TextView inviteTab;
	// 新浪微博数据（待关注、待邀请）
	private ContactObj contactObj = null;

	private UserAdapter2 adapter;
	 
	private CommonUserAdapter comAdapter;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(adapter != null) adapter.setFlag(true);
		if (SystemContext.getInstance().getExtUserVo() != null) {
			GuideUtil.startGuide(WeiboFriendActivity.this, GuideActivity.GUIDE_MODE_WEIBO_FOLLOW);
			if(NetworkUtil.isNetworkAvailable(this)){
				if(followView != null)
					followView.setLoadingUI();
				ProxyFactory.getInstance().getUserProxy().getWeiboFriends(new ProxyCallBack<ContactObj>() {

					@Override
					public void onSuccess(ContactObj result) {
						contactObj = result;
						followView.refreshList();
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						LogUtil.e(TAG, "获取微博互粉好友失败，result＝" + result + ",resultMsg=" + resultMsg);
						ToastUtil.showToast(WeiboFriendActivity.this, "获取微博好友失败");
						WeiboFriendActivity.this.finish();
					}
				}, this);
			}else{
				followView.refreshList();
			}
		}
	}

	/**
	 * 
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		// 设置TITLE
		setTitleTxt(getString(R.string.user_addfriend_weibo));
		// 设置内容UI
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(View.inflate(this, R.layout.user_common_add_friend, null), params);

		viewPager = (ViewPager) findViewById(R.id.viewPage);
		followTab = (TextView) findViewById(R.id.followTab);
		inviteTab = (TextView) findViewById(R.id.inviteTab);
		followTab.setSelected(true);
		inviteTab.setSelected(false);
		followTab.setOnClickListener(this);
		inviteTab.setOnClickListener(this);
		pageViews = getViews();
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == 0) {
					GuideUtil.startGuide(WeiboFriendActivity.this, GuideActivity.GUIDE_MODE_WEIBO_FOLLOW);
					followTab.setSelected(true);
					inviteTab.setSelected(false);
					followView.refreshList();
				} else if (arg0 == 1) {
					GuideUtil.startGuide(WeiboFriendActivity.this, GuideActivity.GUIDE_MODE_WEIBO_INVITE);
					followTab.setSelected(false);
					inviteTab.setSelected(true);
					inviteView.refreshList();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		ViewPagerAdapter adapter = new ViewPagerAdapter(pageViews);
		viewPager.setAdapter(adapter);
	}

	/**
	 * 获取多个view
	 * 
	 * @return
	 */
	private List<View> getViews() {
		viewList = new ArrayList<View>();
		followView = new CommonListView(this, View.INVISIBLE) {

			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				if(WeiboFriendActivity.this.adapter != null)WeiboFriendActivity.this.adapter.setFlag(true);
				if(isRefresh)clean();
				getFollowData(followView);
			}
		};
		adapter = new UserAdapter2(this, followView.listData, R.layout.user_list_item_userfragment, new String[] { "nickname" },
				new int[] { R.id.nickname }, true, UserAdapter2.GAME_FOLLOW_TYPE_COMMON, true,followView.list);
		followView.setAdapter(adapter);
		inviteView = new CommonListView(this, View.INVISIBLE, true) {

			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				getInviteData(inviteView);
			}
		};
		comAdapter = new CommonUserAdapter(this, inviteView.listData, CommonUserAdapter.WEIBO_FRIEND);
		inviteView.setAdapter(comAdapter);
		viewList.add(followView);
		viewList.add(inviteView);
		// 添加列表点击功能
		followView.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				Object uid = map.get("uid");
				if (uid != null && !uid.equals(SystemContext.getInstance().getExtUserVo().getUserid())) {
					Intent intent = new Intent(parent.getContext(), UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		return viewList;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.followTab) {
			followTab.setSelected(true);
			inviteTab.setSelected(false);
			viewPager.setCurrentItem(0);
		} else if (v.getId() == R.id.inviteTab) {
			followTab.setSelected(false);
			inviteTab.setSelected(true);
			viewPager.setCurrentItem(1);
		}
	}

	private void getFollowData(final CommonListView view) {
		view.hasNext = false;
		view.clean();
		if (contactObj != null && contactObj.followObjects != null) {
			if (contactObj.followObjects.size() > 0) {
				view.showListView();
				// 添加设置LIST数据
				view.listData.addAll(praseFollowUserList(contactObj.followObjects));
			} else {
				if (view.listData.size() <= 0)
					view.showNullBgView(R.drawable.no_relative_user);
				LogUtil.d(TAG, "数据为空");
			}
		} else {
			if (view.listData.size() <= 0)
				view.showNullBgView(R.drawable.no_relative_user);
			LogUtil.d(TAG, "数据为空");
		}
		view.adapter.notifyDataSetChanged();
	}

	private void getInviteData(final CommonListView view) {
		view.hasNext = false;
		view.clean();
		if (contactObj != null && contactObj.inviteObjects != null) {
			if (contactObj.inviteObjects.size() > 0) {
				view.showListView();
				// 添加设置LIST数据
				view.listData.addAll(contactObj.inviteObjects);
			} else {
				if (view.listData.size() <= 0)
					view.showNullBgView(R.drawable.no_relative_user);
				LogUtil.d(TAG, "数据为空");
			}
		} else {
			if (view.listData.size() <= 0)
				view.showNullBgView(R.drawable.no_relative_user);
			LogUtil.d(TAG, "数据为空");
		}
		view.adapter.notifyDataSetChanged();
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseFollowUserList(List<UserObject> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			UserObject vo = list.get(i);
			map.put("avatar", vo.getAvatar());
			map.put("nickname", vo.getNickname());
			map.put("desc", "没有关注贴吧");
			map.put("sex", vo.getSex());
			map.put("age", vo.getAge());
			map.put("uid", vo.getUid());
			map.put("rel", vo.getRel());
			map.put("news", vo.getMood());
			map.put("grade", vo.getGrade());
			tmplist.add(map);
		}
		return tmplist;
	}
	
	/**
	 * 在当前的activity关闭的时候
	 * 要避免窗体泄漏的问题
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(comAdapter != null && comAdapter.getDialog() != null) 
			comAdapter.getDialog().dismiss();
	}
}
