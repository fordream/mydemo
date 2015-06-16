/**      
 * AddFriendActivity.java Create on 2013-8-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseListActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.utils.ViewUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: AddFriendActivity
 * @Description: 添加好友
 * @author 王卫
 * @date 2013-8-26 下午03:26:19
 * @Version 1.0
 * 
 */
public class AddFriendActivity extends BaseListActivity implements OnClickListener {
	// 搜索类型 0：按用户编号,按昵称1：附近的人2：摇一摇3：通讯录4：微博账号5:用户id
	private int type;
	// 获取文本输入框
	private EditText searchTxt;

	private LinearLayout listContent;
    private InputMethodManager manager;
    private ImageView ivLoading;
    private UserAdapter2 adapter;
    
	/**
	 * 初始化
	 */
	@Override
	protected void initialize() {
		manager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示左边
		setLeftVisible(true);
		// 隐藏右边
		setRightVisible(false);
		// 设置TITLE
		titleTxt.setText("添加好友");

		// 设置默认选择[添加好友]
		setAddFriendContent();
	}

	/**
	 * 设置添加好友内容界面
	 */
	private void setAddFriendContent() {
		// 获取内容界面
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		contentView.removeAllViews();
		View view = View.inflate(this, R.layout.user_add_friend, null);
		ivLoading = (ImageView)view.findViewById(R.id.ivLoading);
		AnimationDrawable animationDrawable = (AnimationDrawable)ivLoading.getBackground();
		animationDrawable.start();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		getContentView().addView(view, params);
		// 获取输入框
		searchTxt = (EditText) findViewById(R.id.searchTxt);
		// 注册点击监听事件
		(findViewById(R.id.searchBtn)).setOnClickListener(this);
		// 设置清除按钮
		Button cleanBtn = (Button) findViewById(R.id.cleanBtn);
		// 设置文本输入框文本变化监听
		EditTextUtil.ChangeCleanTextButtonVisible(searchTxt, cleanBtn);
		cleanBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchTxt.setText("");
			}
		});
		setPullRefreshListView((PullToRefreshListView) findViewById(R.id.refreshList));
		// 初始化列表
		listContent = (LinearLayout) findViewById(R.id.listContent);
		adapter = new UserAdapter2(this, listData, R.layout.user_list_item_userfragment, new String[] { "nickname" }, new int[] { R.id.nickname }, true, false,list);
		// 设置列表适配器
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
				}
			}
		});
	}

	
	/**
	 * 当点击返回按钮的时候
	 * 则关闭当前 的界面
	 */
	protected void back() {
		manager.hideSoftInputFromWindow(searchTxt.getWindowToken(), 0);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				AddFriendActivity.this.finish();
			}
		}, 200);
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListActivity#getListData(long, int)
	 */
	@Override
	protected void getListData(long offset, int limit) {
		super.getListData(offset, limit);
		if(adapter != null) adapter.setFlag(true);
		getRecommondUser();
	}

	/**
	 * 获取推荐用户
	 */
	private void getRecommondUser() {
		if(listData != null && listData.size() <= 0)ivLoading.setVisibility(View.VISIBLE);
		ProxyFactory.getInstance().getUserProxy().getRecommondUser(new ProxyCallBack<List<UserObject>>() {

			@Override
			public void onSuccess(List<UserObject> result) {
				ivLoading.setVisibility(View.GONE);
				onFooterRefreshComplete();
				if (result != null && result.size() > 0) {
					// 设置LIST数据
					listData.addAll(praseUserList(result));
					hasNext = false;
					adapter.notifyDataSetChanged();
					adapter.notifyDataSetChanged();
					adapter.notifyDataSetInvalidated();
				}
				if (listData.size() <= 0)
					ViewUtil.showNullBgView(AddFriendActivity.this, listContent, R.drawable.no_relative_user);

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ivLoading.setVisibility(View.GONE);
				if (listData.size() <= 0)
					ViewUtil.showNullBgView(AddFriendActivity.this, listContent, R.drawable.no_relative_user);
			}
		}, this);
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
				map.put("desc", "");
				map.put("sex", vo.getSex());
				map.put("age", vo.getAge());
				map.put("uid", vo.getUid());
				map.put("rel", vo.getRel());
				map.put("weibo", vo.getWeibo());
				map.put("weiboName", vo.getWeiboName());
				map.put("mobile", vo.getMobile());
				map.put("mobileName", vo.getContactName());
				map.put("userType", vo.getUsertype());
				if (vo.getUsertype() == UserObject.USER_SOURCE_TYPE_CONTACT) {
					map.put("desc", "通讯录好友:" + vo.getContactName());
				} else if (vo.getUsertype() == UserObject.USER_SOURCE_TYPE_WEIBO) {
					if (vo.getWeiboName() != null && !vo.getWeiboName().isEmpty())
						map.put("desc", "新浪微博好友:" + vo.getWeiboName());
					else
						map.put("desc", "新浪微博好友");
				}
				map.put("grade", vo.getGrade());
				tmplist.add(map);
			}
		}
		return tmplist;
	}

	/**
	 * 搜索用户
	 * 
	 * @param type
	 *            (搜索类型 0：按用户编号,按昵称1：附近的人2：摇一摇3：通讯录4：微博账号5:用户id)
	 * @param keyWord
	 *            (用户昵称或ID)
	 */
	private void searchUser(int type, String keyWord) {
		Intent intent = new Intent(this, UserListActicity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TYPE, type);
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_KEYWORD, keyWord);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		manager.hideSoftInputFromWindow(searchTxt.getWindowToken(), 0);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String txt = null;
		if (v.getId() == R.id.searchBtn) {
			type = UserAdapter2.TYPE_NULL;
			txt = searchTxt.getText().toString();
		}
		if (type == UserAdapter2.TYPE_NULL && txt.isEmpty()) {
			ToastUtil.showToast(AddFriendActivity.this, getString(R.string.user_search_verify_fail));
			return;
		} else {
			searchUser(type, txt);
		}
	}

}
