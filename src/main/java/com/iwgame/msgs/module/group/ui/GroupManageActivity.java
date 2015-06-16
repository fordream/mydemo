/**      
 * GroupManageActivity.java Create on 2013-10-25     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.chat.ui.GroupSendMassMsgActivity;
import com.iwgame.msgs.module.group.adapter.GroupUserAdapter;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;

/**
 * @ClassName: GroupManageActivity
 * @Description: 公会管理
 * @author 王卫
 * @date 2013-10-25 下午04:35:36
 * @Version 1.0
 * 
 */
public class GroupManageActivity extends BaseActivity implements OnClickListener {

	// 公会ID
	private long grid;
	// 关系
	private int rel;
	// 编辑公会资料
	private LinearLayout editItem;
	// 批准会员按钮
	private LinearLayout allowItem;
	private ImageView editLine;
	// 删除会员按钮
	private LinearLayout deleteItem;
	// 群发消息按钮
	private LinearLayout messageItem;
	// 添加管理员按钮
	private LinearLayout managerItem;
	// 邀请按钮
	private LinearLayout inviteItem;
	// 转让按钮
	private LinearLayout giveItem;

	private LinearLayout chatFunction;

	private LinearLayout group_upgradeLayout;
	
	private ImageView allowTag;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	/**
	 * 去请求是否有需要批准的成员
	 */
	@Override
	protected void onResume() {
		super.onResume();
		getApplyUsers(grid);
	}
	
	
	/**
	 * 初始化
	 */
	private void initialize() {
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			grid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
			rel = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_REL);
		}
		if (rel != 2 && rel != 3)
			finish();
		// 显示左边菜单
		setLeftVisible(true);
		// 影藏右边菜单
		setRightVisible(false);
		setTitleTxt("公会管理");
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		contentView.removeAllViews();
		RelativeLayout detailView = (RelativeLayout) View.inflate(this, R.layout.group_manage, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(detailView, params);
		// 获取并设置功能按钮
		editItem = (LinearLayout) findViewById(R.id.editItem);
		editLine = (ImageView) findViewById(R.id.editLine);
		allowItem = (LinearLayout) findViewById(R.id.allowItem);
		deleteItem = (LinearLayout) findViewById(R.id.deleteItem);
		messageItem = (LinearLayout) findViewById(R.id.messageItem);
		managerItem = (LinearLayout) findViewById(R.id.managerItem);
		inviteItem = (LinearLayout) findViewById(R.id.inviteItem);
		giveItem = (LinearLayout) findViewById(R.id.giveItem);
		allowTag = (ImageView)findViewById(R.id.allowtag);
		group_upgradeLayout = (LinearLayout)findViewById(R.id.group_upgrade_item);
		group_upgradeLayout.setOnClickListener(this);
		editItem.setOnClickListener(this);
		allowItem.setOnClickListener(this);
		deleteItem.setOnClickListener(this);
		messageItem.setOnClickListener(this);
		managerItem.setOnClickListener(this);
		inviteItem.setOnClickListener(this);
		giveItem.setOnClickListener(this);
		// 根据我和公会的关系设置权限
		LinearLayout maxLimitView = (LinearLayout) findViewById(R.id.maxLimitView);
		if (rel == 2) {// 管理员
			group_upgradeLayout.setVisibility(View.GONE);
			editItem.setVisibility(View.GONE);
			editLine.setVisibility(View.GONE);
			maxLimitView.setVisibility(View.GONE);
			deleteItem.setBackgroundResource(R.drawable.common_item_bottom_selector);
		} else if (rel == 3) {// 会长
			editItem.setVisibility(View.VISIBLE);
			editLine.setVisibility(View.VISIBLE);
			maxLimitView.setVisibility(View.VISIBLE);
			deleteItem.setBackgroundResource(R.drawable.common_item_middle_selector);
		}
		chatFunction = (LinearLayout) findViewById(R.id.chatFunction);
		chatFunction.setAlpha(204);
		chatFunction.setOnClickListener(this);
		getApplyUsers(grid);
	}

	
	
	/**
	 * 获取批准成员的数据的用户列表数据
	 * 
	 * @param grid
	 */
	private void getApplyUsers(long grid) {
		ProxyFactory.getInstance().getGroupProxy().getApplyUsers(new ProxyCallBack<List<UserItemObj>>() {

			@Override
			public void onSuccess(List<UserItemObj> result) {
				if(result != null && result.size() > 0 && allowTag != null){
					allowTag.setVisibility(View.VISIBLE);
				}else if(allowTag != null){
					allowTag.setVisibility(View.GONE);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if(allowTag != null)
				allowTag.setVisibility(View.GONE);
			}
		}, this, grid);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.editItem) {
			if (rel == 3) {// 会长
				Intent intent = new Intent(this, EditDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		} else if (v.getId() == R.id.allowItem) {
			startUserListActivity(GroupUserAdapter.MODE_APPLY, grid);
		} else if (v.getId() == R.id.deleteItem) {
			startUserListActivity(GroupUserAdapter.MODE_DELED, grid);
		} else if (v.getId() == R.id.messageItem) {
			Intent intent = new Intent(this, GroupSendMassMsgActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, grid);
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			startActivity(intent);
		} else if (v.getId() == R.id.managerItem) {
			startUserListActivity(GroupUserAdapter.MODE_SETTING, grid);
		} else if (v.getId() == R.id.inviteItem) {
			startUserListActivity(GroupUserAdapter.MODE_INVITE, grid);
		} else if (v.getId() == R.id.giveItem) {
			startUserListActivity(GroupUserAdapter.MODE_TRANSFER, grid);
		} else if (v.getId() == chatFunction.getId()) {
			intoChat();
		}else if(v.getId() == group_upgradeLayout.getId()){
			Intent intent = new Intent(this,GroupGradePolicyActivity.class);
			intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
			this.startActivity(intent);
		}
	}

	/**
	 * 跳转到详细列表页
	 * 
	 * @param mode
	 * @param grid
	 */
	private void startUserListActivity(int mode, long grid) {
		Intent intent = new Intent(this, GroupManageUserListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MOOD, mode);
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 
	 */
	private void intoChat() {
		// 群聊
		Intent intent = new Intent(GroupManageActivity.this, GroupChatFragmentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, grid);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		startActivity(intent);
	}

}
