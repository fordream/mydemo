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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

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
import com.youban.msgs.R;


/**
 * @ClassName: ContactFriendActivity
 * @Description: 添加通讯录好友主界面
 * @author 王卫
 * @date 2014-4-23 下午3:33:12
 * @Version 1.0
 * 
 */
public class ContactFriendActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "ContactFriendActivity";
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
	// 通讯录数据（待关注、待邀请）
	private ContactObj contactObj = null;

	private SharedPreferences sp;
	private UserAdapter2 adapter;
	
	private CommonUserAdapter comAdapter;


	/**
	 * 如果是第一次进入
	 * 如果新 朋友没有数据
	 * 则跳转到待邀请界面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("isfirstenter", MODE_PRIVATE);
		sp.edit().putBoolean("isfirst", false).commit();
		initialize();
	}


	/**
	 * 每次执行onresume的生命周期方法时
	 * 都去请求联系人
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if(adapter != null) adapter.setFlag(true);
		if (SystemContext.getInstance().getExtUserVo() != null) {
			ProxyFactory.getInstance().getUserProxy().getContact(new ProxyCallBack<ContactObj>() {

				@Override
				public void onSuccess(ContactObj result) {
					contactObj = result;
					followView.refreshList();
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					LogUtil.e(TAG, "获取通讯录联系人失败，result＝" + result + ",resultMsg=" + resultMsg);
					if(!sp.getBoolean("isfirst",false)) viewPager.setCurrentItem(1);
					sp.edit().putBoolean("isfirst", true).commit();
				}
			}, this);
		}
	}

	/**
	 * 初始化当前
	 * 的用户界面
	 */
	private void initialize() {
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		// 设置TITLE
		setTitleTxt(getString(R.string.user_addfriend_contact));
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
					followTab.setSelected(true);
					inviteTab.setSelected(false);
					followView.refreshList();
					GuideUtil.startGuide(ContactFriendActivity.this, GuideActivity.GUIDE_MODE_CONTRACT_FOLLOW);
				} else if (arg0 == 1) {
					followTab.setSelected(false);
					inviteTab.setSelected(true);
					inviteView.refreshList();
					GuideUtil.startGuide(ContactFriendActivity.this, GuideActivity.GUIDE_MODE_CONTRACT_INVITE);
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
	 * 对新的朋友下面的控件
	 * 和待邀请下面的控件，进行初始化
	 * @return
	 */
	private List<View> getViews() {
		viewList = new ArrayList<View>();
		followView = new CommonListView(this, View.INVISIBLE) {

			@Override
			public void getListData(long offset, int limit) {
				if(ContactFriendActivity.this.adapter != null) ContactFriendActivity.this.adapter.setFlag(true);
				super.getListData(offset, limit);
				if(isRefresh)clean();
				getFollowData(followView);
			}
		};
		adapter = new UserAdapter2(this, followView.listData, R.layout.user_list_item_userfragment, new String[] { "nickname" },
				new int[] { R.id.nickname }, true, UserAdapter2.GAME_FOLLOW_TYPE_COMMON,true,followView.list);
		followView.setAdapter(adapter);
		inviteView = new CommonListView(this, View.INVISIBLE, true) {

			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				getInviteData(inviteView);
			}
		};
		comAdapter = new CommonUserAdapter(this, inviteView.listData, CommonUserAdapter.CONTACT_FRIEND);
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
			viewPager.setCurrentItem(0);
		} else if (v.getId() == R.id.inviteTab) {
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
				sp.edit().putBoolean("isfirst", true).commit();
			} else {
				if (view.listData.size() <= 0){
					if(sp.getBoolean("isfirst", false)){
						view.showNullBgView(R.drawable.common_no_seach_uers);
					}else{
						followTab.setSelected(false);
						inviteTab.setSelected(true);
						viewPager.setCurrentItem(1);
					}
				}
				LogUtil.d(TAG, "数据为空");
				sp.edit().putBoolean("isfirst", true).commit();
			}
		} else {
			if (view.listData.size() <= 0){
				if(sp.getBoolean("isfirst", false)){
					view.showNullBgView(R.drawable.common_no_seach_uers);
				}else{
					followTab.setSelected(false);
					inviteTab.setSelected(true);
					viewPager.setCurrentItem(1);
				}
			}
			LogUtil.d(TAG, "数据为空");
			sp.edit().putBoolean("isfirst", true).commit();
		}
	
		view.adapter.notifyDataSetChanged();
		if(viewPager.getCurrentItem() == 0)GuideUtil.startGuide(this, GuideActivity.GUIDE_MODE_CONTRACT_FOLLOW);
	}

	/**]
	 * 获取待邀请数据
	 * @param view
	 */
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
					view.showNullBgView(R.drawable.common_no_seach_uers);
				LogUtil.d(TAG, "数据为空");
			}
		} else {
			if (view.listData.size() <= 0)
				view.showNullBgView(R.drawable.common_no_seach_uers);
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
	 * 删除掉偏好设置里面的数据
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		sp.edit().remove("isfirst").commit();
		if(comAdapter != null && comAdapter.getDialog() != null) comAdapter.getDialog().dismiss();
	}
}
