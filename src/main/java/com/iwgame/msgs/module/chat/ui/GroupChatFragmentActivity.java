/**      
 * GroupChatFragmentActivity.java Create on 2013-10-25     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseSuperFragmentActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.account.ui.register.SetAccountActivity;
import com.iwgame.msgs.module.chat.adapter.ChatMessageAdapter;
import com.iwgame.msgs.module.chat.adapter.MyTabsAdapter;
import com.iwgame.msgs.module.chat.ui.ParentViewPager.MyTouchListener;
import com.iwgame.msgs.module.group.ui.ContributePointActivity;
import com.iwgame.msgs.module.group.ui.GroupDetailActivity;
import com.iwgame.msgs.module.group.ui.GroupManageActivity;
import com.iwgame.msgs.module.group.ui.GroupSettingActivity;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.ReportActivity;
import com.iwgame.msgs.utils.GuideUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.UserActionUtil;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.widget.MenuMoreButton;
import com.iwgame.msgs.widget.SendMsgView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.popwindow.MsgsPopTextItem;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow;
import com.iwgame.sdk.xaction.XActionUtils;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: GroupChatFragmentActivity
 * @Description: 公会聊天主界面
 * @author 王卫
 * @date 2013-10-25 下午5:17:14
 * @Version 1.0
 * 
 */
public class GroupChatFragmentActivity extends BaseSuperFragmentActivity implements OnClickListener {

	protected static final String TAG = "GroupChatFragmentActivity";
	private GroupUserRelDao groupUserRelDao;
	private long toGroupId;
	private int groupGrade;
	// top 右边text view
	private TextView topRightTextView;
	// 定义TabHost对象
	private TabHost mTabHost;
	private ParentViewPager mViewPager;
	private MyTabsAdapter mTabsAdapter;
	// 定义数组来存放Fragment界面
	private Class mFragmentArray[] = null;
	// Tab选项卡的文字
	private String mTabTextArray[] = null;
	private LayoutInflater layoutInflater;
	// top 中间标题
	private TextView titleTxt;
	// 公会公告view
	private RelativeLayout group_chat_main_announce;
	// 公会公告内容
	private TextView group_chat_main_announce_context;
	// 公告内容
	private String notice;
	// 屏幕高度
	private int displayHeight = 0;
	// centerContentView 在屏幕中的位置
	private int[] centerContentView_location = new int[2];
	private ChatFragment chatFragment;
	// 弹出框
	private PopupWindow popWindow;
	// 右上角的弹出窗口是否打开
	private boolean isShowPopWindows = false;
	private Button moreBtn;
	private GroupVo groupVo;

	/**
	 * 在程序启动的时候就执行下面的这个方法
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "----->>跳转到聊天界面");
		GuideUtil.startGuide(this, GuideActivity.GUIDE_MODE_GROUP_CHAT);
		// 获取上一个页面的传值
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				toGroupId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOGROUPID);
				groupGrade = tmpbundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRADE, 1);
			}
		}
		// 获取屏幕宽高
		WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		displayHeight = windowManager.getDefaultDisplay().getHeight();
		setContentView(R.layout.common_content);
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);
		init(savedInstanceState);
		ProxyFactory.getInstance().getUserProxy().collectActionlLog(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				
			}
		}, this, UserActionUtil.GROUP_OPEN_CHAT, toGroupId, MsgsConstants.OT_GROUP, null);
	}

	/**
	 * 初始化
	 */
	private void init(Bundle savedInstanceState) {
		groupUserRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
		// 定义数组存放tab相关资源
		// 定义数组来存放Fragment界面
		mFragmentArray = new Class[] { ChatFragment.class, GroupUsersFragment.class };
		// // 定义数组来存放按钮图片
		// Tab选项卡的文字
		mTabTextArray = new String[] { getString(R.string.groupchat_tab_name_chat), getString(R.string.groupchat_tab_name_users) };
		// 设置Top左边返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					GroupChatFragmentActivity.this.finish();
				}
			});
		}
		// 设置top右边功能按钮
		moreBtn = new MenuMoreButton(this);
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(moreBtn, params);
		moreBtn.setOnClickListener(this);
		// 设置内容UI
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		View view = (FrameLayout) View.inflate(this, R.layout.group_chat_main, null);
		contentView.addView(view);
		// 实例化TabHost对象，得到TabHost
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mViewPager = (ParentViewPager) findViewById(R.id.pager);
		MyTouchListener myTouchListener = new MyTouchListener() {
			@Override
			public void onTouch() {
				// TODO Auto-generated method stub
				// 设置是否可以滚动(通过判断发送
				if (mViewPager.getCurrentItem() != 0)
					mViewPager.setScrollable(true);
				else
					mViewPager.setScrollable(getSendMsgViewIsBottom());
			}
		};
		mViewPager.setMyTouchListener(myTouchListener);
		mTabsAdapter = new MyTabsAdapter(this, mTabHost, mViewPager, topRightTextView);
		// 得到fragment的个数
		int count = mFragmentArray.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTabTextArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			Bundle bundle = null;
			bundle = new Bundle();
			if (i == 0)// 公会聊天
			{
				// 公会聊天需要的参数
				bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISSENDMSG, true);// 允许发消息
				bundle.putLong(SystemConfig.BUNDLE_NAME_TOID, toGroupId);
				bundle.putString(SystemConfig.BUNDLE_NAME_TODOMAIN, MsgsConstants.DOMAIN_GROUP);
				bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, MsgsConstants.MCC_CHAT);
				bundle.putString(SystemConfig.BUNDLE_NAME_CHANNELTYPE, MsgsConstants.MC_MCHAT);
				bundle.putInt(SystemConfig.BUNDLE_NAME_PAGETYPE, ChatMessageAdapter.PAGE_TYPE_MCHAT);

			} else {
				bundle.putLong(SystemConfig.BUNDLE_NAME_TOID, toGroupId);
			}
			mTabsAdapter.addTab(tabSpec, mFragmentArray[i], bundle);
		}

		if (savedInstanceState != null && savedInstanceState.getString("tab") != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		} else {// 设置聊天室为默认
			mTabHost.setCurrentTabByTag(mTabTextArray[0]);
		}

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 聊天室
				if (arg0 == 0) {
					boolean isshow = SystemContext.getInstance().getUserSharedPreferencesGroupAnnounceIsShow(toGroupId);
					String lastAnnounceContent = SystemContext.getInstance().getUserSharedPreferencesLastGroupAnnounce(toGroupId);

					if (!"".equals(notice) && (isshow || !lastAnnounceContent.equals(notice))) {
						// 需要显示公告
						group_chat_main_announce.setVisibility(View.VISIBLE);
						group_chat_main_announce_context.setText(notice);
					} else {
						group_chat_main_announce.setVisibility(View.GONE);
					}
				} else {
					group_chat_main_announce.setVisibility(View.GONE);
					/** 隐藏软键盘 **/
					View view = getWindow().peekDecorView();
					if (view != null) {
						InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
					}
				}
				mTabsAdapter.onPageSelected(arg0);
				// 如果跳转到了成员列表界面，则加载公会成员列表数据
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// 有三种状态（0，1，2）。arg0
				// ==1的时辰默示正在滑动，arg0==2的时辰默示滑动完毕了，arg0==0的时辰默示什么都没做。
				// 当页面开始滑动的时候，三种状态的变化顺序为（1，2，0）
				// 在滑动
				if (arg0 == 1) {
				} else if (arg0 == 0) {
					boolean isshow = SystemContext.getInstance().getUserSharedPreferencesGroupAnnounceIsShow(toGroupId);
					String lastAnnounceContent = SystemContext.getInstance().getUserSharedPreferencesLastGroupAnnounce(toGroupId);

					if (notice != null && !notice.equals("") && (isshow || !notice.equals(lastAnnounceContent)) && mViewPager.getCurrentItem() == 0) {
						// 需要显示公告
						group_chat_main_announce.setVisibility(View.VISIBLE);
						group_chat_main_announce_context.setText(notice);
					} else {
						group_chat_main_announce.setVisibility(View.GONE);
					}
				}

			}
		});

		// 设置Top中间标题
		titleTxt = (TextView) findViewById(R.id.titleTxt);

		group_chat_main_announce = (RelativeLayout) findViewById(R.id.group_chat_main_announce);
		group_chat_main_announce_context = (TextView) findViewById(R.id.group_chat_main_announce_context);
		ImageButton group_chat_main_announce_action_close = (ImageButton) findViewById(R.id.group_chat_main_announce_action_close);
		group_chat_main_announce_action_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SystemContext.getInstance().setUserSharedPreferencesGroupAnnounceIsShow(toGroupId, false);
				SystemContext.getInstance().setUserSharedPreferencesLastGroupAnnounce(toGroupId,
						group_chat_main_announce_context.getText().toString());
				group_chat_main_announce.setVisibility(View.GONE);
			}
		});

	}

	private boolean getSendMsgViewIsBottom() {
		chatFragment = (ChatFragment) mTabsAdapter.getItemByPosition(0);
		if (chatFragment != null && chatFragment.getSendMsgView() != null) {
			chatFragment.getSendMsgView().setActivity(this);
			// 由于centerContentView的高度是填充，设置成0dip了，所以SendMsgView获得的位置就和centerContentView的位置一样（相应于忽略了centerContentView），所以采用下列方式来实现获得位置
			int[] centerContentView_location = new int[2];
			LinearLayout centerContentView = chatFragment.getCenterContentView();
			centerContentView.getLocationOnScreen(centerContentView_location);
			int y1 = centerContentView_location[1];
			SendMsgView sendMsgView = chatFragment.getSendMsgView();
			if (y1 + centerContentView.getMeasuredHeight() + sendMsgView.getMeasuredHeight() < displayHeight - 10)
				return false;
			else
				return true;
		} else {
			return true;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		ServiceFactory.getInstance().getSyncEntityService().syncEntity(toGroupId, SyncEntityService.TYPE_GROUP, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {
				initData();
			}

			@Override
			public void onFailure(Integer result) {
				initData();
			}
		});
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		Log.e(TAG, "----->>跳转到聊天界面2");
		final GroupDao dao = DaoFactory.getDaoFactory().getGroupDao(this);
		groupVo = dao.findGroupByGrid(toGroupId);
		if (groupVo != null && groupVo.getName() != null && !"".equals(groupVo.getName())) {
			titleTxt.setText(GroupChatFragmentActivity.this.getString(R.string.groupchat_title_middle_name, groupVo.getName()));
			notice = groupVo.getNotice();

			boolean isshow = SystemContext.getInstance().getUserSharedPreferencesGroupAnnounceIsShow(toGroupId);
			String lastAnnounceContent = SystemContext.getInstance().getUserSharedPreferencesLastGroupAnnounce(toGroupId);
			if (!"".equals(notice) && (isshow || !lastAnnounceContent.equals(notice)) && mViewPager.getCurrentItem() == 0) {
				// 需要显示公告
				group_chat_main_announce.setVisibility(View.VISIBLE);
				group_chat_main_announce_context.setText(notice);
			} else {
				group_chat_main_announce.setVisibility(View.GONE);
			}
		}
		Log.e(TAG, "----->>跳转到聊天界面3");
	}

	/**
	 * 
	 * @param height
	 */
	public void setChildViewPagerHeight(int height) {
		mViewPager.setChildViewPagerHeight(height);
	}

	/**
	 * 设置是否可以滚动
	 * 
	 * @param able
	 */
	public void setViewPagerScrollable(boolean able) {
		mViewPager.setScrollable(able);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == moreBtn.getId() && !isShowPopWindows && groupVo != null) {
			isShowPopWindows = true;
			// moreBtn.setBackgroundResource(R.drawable.common_menu_more_pre);
			int xoffInPixels = v.getWidth() / 2;
			final MsgsPopWindow msgsPopWindow = new MsgsPopWindow(this, v, xoffInPixels, 0);
			setPopwindowItems(msgsPopWindow, groupVo);
			msgsPopWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					// moreBtn.setBackgroundResource(R.drawable.common_menu_more_nor);
					isShowPopWindows = false;
				}
			});

		}
	}

	private void setPopwindowItems(final MsgsPopWindow msgsPopWindow, GroupVo groupVo) {
		final int rel = groupVo.getRelWithGroup();
		List<View> items = new ArrayList<View>();
		// 公会资料
		MsgsPopTextItem item = new MsgsPopTextItem(this, getString(R.string.groupchat_menu_desc_info));
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				msgsPopWindow.dismiss();
				switchToRelActivity(rel, 1);
			}
		});
		items.add(item);
		// 贡献积分
		if (rel > 0 && groupVo.getSid() <= 0 ) {
			item = new MsgsPopTextItem(this, getString(R.string.groupchat_menu_point_info));
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					msgsPopWindow.dismiss();
					switchToRelActivity(rel, 2);
				}
			});
			items.add(item);
		}
		if (rel > 0) {
			// 设置
			item = new MsgsPopTextItem(this, getString(R.string.groupchat_menu_setting_info));
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					msgsPopWindow.dismiss();
					switchToRelActivity(rel, 3);

				}
			});
			items.add(item);
		}
		if (rel == GroupUserRelVo.REL_ADMIN) {
			// 加入验证
			item = new MsgsPopTextItem(this, getString(R.string.join_group_yanzheng));
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					msgsPopWindow.dismiss();
					switchToRelActivity(rel, 7);

				}
			});
			items.add(item);
		}
		if (rel == GroupUserRelVo.REL_ADMIN || rel == GroupUserRelVo.REL_NORMALADMIN) {// 会长或管理员
			// 公会管理 // 管理公会
			item = new MsgsPopTextItem(this, getString(R.string.groupchat_menu_manage_info));
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					msgsPopWindow.dismiss();
					switchToRelActivity(rel, 4);

				}
			});
			items.add(item);
		}
		if (rel == GroupUserRelVo.REL_NORMALADMIN || rel == GroupUserRelVo.REL_USER) {

			// 举报公会
			item = new MsgsPopTextItem(this, getString(R.string.groupchat_menu_report_info));
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					msgsPopWindow.dismiss();
					switchToRelActivity(rel, 5);
				}
			});
			items.add(item);
			// 退出公会
			item = new MsgsPopTextItem(this, getString(R.string.groupchat_menu_quit_info));
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					msgsPopWindow.dismiss();
					switchToRelActivity(rel, 6);

				}
			});
			items.add(item);
		}

		msgsPopWindow.setContentItems(items);
	}

	/**
	 * 点击相应的按钮，跳转到相应的界面
	 * 
	 * @param flag
	 */
	private void switchToRelActivity(int rel, int flag) {
		switch (flag) {
		case 1:
			Intent intent = new Intent(GroupChatFragmentActivity.this, GroupDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, toGroupId);
			bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ISFROMCHATACTIVITYOPEN, true);
			bundle.putInt("rel", rel);
			intent.putExtras(bundle);
			startActivityForResult(intent, SystemConfig.ACTIVITY_REQUESTCODE_DISTROY);
			break;
		case 2:
			Intent intent1 = new Intent(GroupChatFragmentActivity.this, ContributePointActivity.class);
			intent1.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, toGroupId);
			intent1.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRADE, groupGrade);
			if (rel == GroupUserRelVo.REL_ADMIN || rel == GroupUserRelVo.REL_NORMALADMIN) {
				intent1.putExtra(SystemConfig.IF_NOT_SHOW_GROUP_CARD, true);
			}
			GroupChatFragmentActivity.this.startActivity(intent1);
			break;
		case 3:
			Intent intent2 = new Intent(GroupChatFragmentActivity.this, GroupSettingActivity.class);
			Bundle bundle2 = new Bundle();
			bundle2.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, toGroupId);
			bundle2.putBoolean("isshowmsgsremind", true);// 显示消息提醒，不显示加入公会
			bundle2.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_REL, rel);
			intent2.putExtras(bundle2);
			startActivityForResult(intent2, SystemConfig.ACTIVITY_REQUESTCODE_DISTROY);
			break;
		case 4:
			Intent intent3 = new Intent(GroupChatFragmentActivity.this, GroupManageActivity.class);
			Bundle bundle3 = new Bundle();
			bundle3.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_REL, rel);
			bundle3.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, toGroupId);
			intent3.putExtras(bundle3);
			GroupChatFragmentActivity.this.startActivity(intent3);
			break;
		case 5:
			ReportGroup(toGroupId);
			break;
		case 6:
			quitGroup(toGroupId);
			break;
		case 7:
			Intent intent4 = new Intent(GroupChatFragmentActivity.this, GroupSettingActivity.class);
			Bundle bundle4 = new Bundle();
			bundle4.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, toGroupId);
			bundle4.putBoolean("isshowmsgsremind", false);// 显示加入公会验证，不显示消息提醒
			bundle4.putInt("rel", rel);
			intent4.putExtras(bundle4);
			startActivityForResult(intent4, SystemConfig.ACTIVITY_REQUESTCODE_DISTROY);
			break;
		}
	}

	/**
	 * 根据索引值获取界面
	 * 
	 * @param index
	 * @return
	 */
	private View getTabItemView(int index) {
		View view = layoutInflater.inflate(R.layout.common_sub_tab_item_view, null);

		// ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		// imageView.setImageResource(mTabImageArray[index]);

		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(mTabTextArray[index]);

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 如果表情框的弹出了，那么就隐藏表情
			if (chatFragment != null && chatFragment.getSendMsgView() != null && chatFragment.getSendMsgView().hideSmileyView())
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case SystemConfig.ACTIVITY_REQUESTCODE_DISTROY:
			if (resultCode == SystemConfig.ACTIVITY_RESULTCODE_DISTROY) {
				this.finish();
			} else if (resultCode == SystemConfig.ACTIVITY_RESULTCODE_DISTROY_USERLIST) {
				// 进入成员列表
				mTabHost.setCurrentTab(1);
			} else if (resultCode == SystemConfig.ACTIVITY_RESULTCODE_DISTROY_CHATLIST) {
				// 进入聊天列表
				mTabHost.setCurrentTab(0);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 举报公会
	 * 
	 * @param grid
	 */
	public void ReportGroup(long grid) {
		Intent intent = new Intent(this, ReportActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, grid);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_GROUP);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * 退出公会
	 */
	private void quitGroup(final long grid) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		final TextView txt = new TextView(this);
		txt.setTextColor(getResources().getColor(R.color.darkgray));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(this.getString(R.string.group_out_group_tip));
		content.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(this.getString(R.string.global_dialog_tip_title));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				final CustomProgressDialog dialog2 = CustomProgressDialog.createDialog(GroupChatFragmentActivity.this);
				dialog2.show();
				ProxyFactory.getInstance().getUserProxy().userAction(
						new ProxyCallBack<Integer>() {

							@Override
							public void onSuccess(Integer result) {
								switch (result) {
								case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
									ToastUtil.showToast(GroupChatFragmentActivity.this, getString(R.string.group_op_success));
									// 清理该公会的未读消息数
									SystemContext.getInstance().cleanSubjectUnReadCount(MsgsConstants.MC_MCHAT, grid, MsgsConstants.DOMAIN_GROUP,
											MsgsConstants.MC_CHAT);
									ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GROUP, new SyncCallBack() {

										@Override
										public void onSuccess(Object result) {
											GroupChatFragmentActivity.this.setResult(SystemConfig.ACTIVITY_RESULTCODE_DISTROY);
											GroupChatFragmentActivity.this.finish();
										}

										@Override
										public void onFailure(Integer result) {
											GroupChatFragmentActivity.this.setResult(SystemConfig.ACTIVITY_RESULTCODE_DISTROY);
											GroupChatFragmentActivity.this.finish();
										}
									});
									break;
								default:
									break;
								}
								dialog2.dismiss();
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								LogUtil.e(TAG, "用户行为失败：" + result);
								dialog2.dismiss();
							}
						}, GroupChatFragmentActivity.this, grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_OUT_GROUP,
						String.valueOf(SystemContext.getInstance().getExtUserVo().getUserid()), null, null);
				dialog2.dismiss();
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

}
