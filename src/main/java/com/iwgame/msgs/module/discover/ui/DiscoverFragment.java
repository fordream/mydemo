/**      
 * DiscoverFragment.java Create on 2013-12-20     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheCallBack;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.discover.ui.filter.FilterView;
import com.iwgame.msgs.module.discover.ui.filter.GameFilterView;
import com.iwgame.msgs.module.discover.ui.filter.GroupFilterView;
import com.iwgame.msgs.module.discover.ui.filter.PlayGameFilterView;
import com.iwgame.msgs.module.discover.ui.filter.UserFilterView;
import com.iwgame.msgs.module.discover.ui.widget.GameSelectView;
import com.iwgame.msgs.module.discover.ui.widget.GroupSelectView;
import com.iwgame.msgs.module.discover.ui.widget.UserSelectView;
import com.iwgame.msgs.module.game.adapter.CommonGameAdapter;
import com.iwgame.msgs.module.game.adapter.DiscoverCommonGameAdapter;
import com.iwgame.msgs.module.game.adapter.ViewPagerAdapter;
import com.iwgame.msgs.module.group.adapter.DiscoverGroupAdapter2;
import com.iwgame.msgs.module.group.ui.AddGroupActivity;
import com.iwgame.msgs.module.group.ui.GroupDetailActivity;
import com.iwgame.msgs.module.play.adapter.DiscoverPlayListAdapter;
import com.iwgame.msgs.module.play.ui.CreateUserPlayActivity;
import com.iwgame.msgs.module.play.ui.PlayDetailInfoActivity;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.user.adapter.DiscoverUserAdapter;
import com.iwgame.msgs.module.user.ui.AddFriendActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.utils.DistanceUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SafeUtils;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: DiscoverFragment
 * @Description: 发现
 * @author 王卫
 * @date 2013-12-20 下午3:49:12
 * @Version 1.0
 * 
 */
@SuppressWarnings({ "unchecked", "deprecation","static-access" })
public class DiscoverFragment extends BaseFragment implements OnTouchListener, OnClickListener {
	private static final String TAG = "DiscoverFragment";
	public static int index = -1;
	private Button selectedBtn;
	private RelativeLayout searchBtn;
	private ImageView searchIcon;
	private ImageView creatPlayIcon;
	/************************* 选择过滤条件 ****************************/
	// 出现的时间
	private Integer selectTime = null;
	// 性别
	private Integer selectSex = null;
	// 贴吧类型
	private Integer selectGameType = null;
	// 贴吧平台
	private String selectGamePlatform = null;
	// 公会是否要验证
	private Boolean selectVerify = null;
	// 公会贴吧类型
	private String selectGames = null;
	// 用户服务器类型
	private String userSids = null;
	// 公会服务器类型
	private String groupSids = null;
	// 用户标签
	private TextView userTab;
	// 贴吧标签
	private TextView gameTab;
	// 公会标签
	private TextView groupTab;

	private LinearLayout contentView;

	// 当前要取消的触屏页面索引
	private int channelTouchPageIndex = -1;
	// 切换页面选项卡
	private LinearLayout tabContent = null;
	// 切换页面组件
	private ViewPager viewPager = null;
	// 页面集合列表
	private List<View> pageViews = null;
	// 显示内容列表
	private List<View> viewList = null;
	// 玩家内容
	private CommonListView playerView;
	// 贴吧内容
	private CommonListView gameView;
	// 陪玩内容
	private CommonListView playView;
	// 公会内容
	private CommonListView groupView;
	private View pview;
	private boolean isUserInit = true;
	private boolean isGameInit = true;
	private boolean isPlayInit = true;
	private boolean isGroupInit = true;

	private long uid;
	private Dialog dialog;
	private int mode;
	private DiscoverUserAdapter discoverAdapter;
	private boolean temp = true;

	private LinearLayout titleTabContent;
	private View userTitleTab;
	private View gameTitleTab;
	private View groupTitleTab;

	private TextView titleTxt;
	private GroupFilterView groupFilterView;
	private GameFilterView gameFilterView;
	private UserFilterView userFilterView;
	private PlayGameFilterView playGameFilterView;

	private CustomProgressDialog loadDialog;
	private boolean showMainTable = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadDialog = CustomProgressDialog.createDialog(getActivity());
		loadDialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog不消失
		PTAG = TAG;
		LogUtil.i(TAG, "------------>DiscoverFragment::onCreate");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "------------>DiscoverFragment::onDestroy");
		playGameFilterView.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		if (dialog != null && SystemContext.getInstance().getExtUserVo() == null) {
			dialog.dismiss();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseListFragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (SystemContext.getInstance().isUnAuth()) {
			// 会话失效， 主动注销，token不存在/认证失败，都到登录页
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED, true);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
			if (getActivity() instanceof MainFragmentActivity) {
				((MainFragmentActivity) getActivity()).showOrDismissTabHost(true, null, null);
			}
			mode = AdaptiveAppContext.getInstance().getAppConfig().getMode();
			if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2) {
				gameTitleTab.setVisibility(View.GONE);
			}
			ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
			if (vo != null) {
				long newuid = vo.getUserid();
				if (newuid != uid) {
					playerView.clean();
					gameView.clean();
					playView.clean();
					groupView.clean();
					isUserInit = true;
					isGroupInit = true;
					isGameInit = true;
					isPlayInit = true;
					uid = newuid;
					if (viewPager != null)
						viewPager.setCurrentItem(0);
					if (groupFilterView != null)
						groupFilterView.reset();
					if (gameFilterView != null)
						gameFilterView.reset();
					if (userFilterView != null)
						userFilterView.reset();
					index = 1;
				}
				if (index != -1) {
					viewPager.setCurrentItem(index);
					index = -1;
				}
				int item = viewPager.getCurrentItem();
				setTitleTable(item);
			}
		}
		LogUtil.i(TAG, "------------>DiscoverFragment::onResume");
	}

	/*
	 * 在这个生命周期方法里面 取消dialog以防出现 在被踢下后 在次进入 在贴吧主页显示了一个对话框
	 */
	@Override
	public void onPause() {
		super.onPause();
		if (dialog != null)
			dialog.dismiss();
	}

	/**
	 * 数据改变
	 * 
	 * @param index
	 */
	private void changeData(int index) {
		if (index == 0) {
			if (playerView != null && playerView.listData.size() <= 0 && temp) {
				playerView.getListData(playerView.offset, playerView.limit);
			} else if (playerView != null) {
				playerView.setRefreshMode(Mode.BOTH);
				playerView.adapter.notifyDataSetChanged();
				playerView.adapter.notifyDataSetInvalidated();
			}
			searchIcon.setVisibility(View.VISIBLE);
			creatPlayIcon.setVisibility(View.GONE);
		} else if (index == 1) {
			if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 1) {
				if (playView != null && playView.listData.size() <= 0) {
					LogUtil.d(TAG, "-------->>searchDiscoverPlays changeData limit is " + playView.limit + ", offset is " + playView.offset);
					playView.getListData(playView.offset, playView.limit);
				} else if (playView != null) {
					playView.setRefreshMode(Mode.BOTH);
					playView.adapter.notifyDataSetChanged();
					playView.adapter.notifyDataSetInvalidated();
				}
				searchIcon.setVisibility(View.GONE);
				creatPlayIcon.setVisibility(View.VISIBLE);
			} else if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2) {
				selectGames = AdaptiveAppContext.getInstance().getAppConfig().getGameId() + "";
				if (groupView != null && groupView.listData.size() <= 0) {
					groupView.getListData(groupView.offset, groupView.limit);
				} else if (groupView != null) {
					groupView.setRefreshMode(Mode.BOTH);
					groupView.adapter.notifyDataSetChanged();
					groupView.adapter.notifyDataSetInvalidated();
				}
				searchIcon.setVisibility(View.VISIBLE);
				creatPlayIcon.setVisibility(View.GONE);
			}
		} else if (index == 2) {
			if (groupView.listData.size() == 0) {
				groupView.getListData(groupView.offset, groupView.limit);
			} else if (groupView != null) {
				groupView.setRefreshMode(Mode.BOTH);
				groupView.adapter.notifyDataSetChanged();
				groupView.adapter.notifyDataSetInvalidated();
			}
			searchIcon.setVisibility(View.VISIBLE);
			creatPlayIcon.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 * @param seclectIndex
	 */
	private void setTitleTable(int seclectIndex) {
		int userTitleBgRid = R.color.discover_title_tab_bg_nor_color;
		int gameTitleBgRid = R.color.discover_title_tab_bg_nor_color;
		int groupTitleBgRid = R.color.discover_title_tab_bg_nor_color;
		if (seclectIndex == 0) {
			userTitleBgRid = R.color.discover_title_tab_bg_pre_color;
			searchIcon.setVisibility(View.VISIBLE);
			creatPlayIcon.setVisibility(View.GONE);
		} else if (seclectIndex == 1) {
			if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2) {
				groupTitleBgRid = R.color.discover_title_tab_bg_pre_color;
				searchIcon.setVisibility(View.VISIBLE);
				creatPlayIcon.setVisibility(View.GONE);
			} else {
				gameTitleBgRid = R.color.discover_title_tab_bg_pre_color;
				searchIcon.setVisibility(View.GONE);
				creatPlayIcon.setVisibility(View.VISIBLE);
			}
		} else if (seclectIndex == 2) {
			groupTitleBgRid = R.color.discover_title_tab_bg_pre_color;
			searchIcon.setVisibility(View.VISIBLE);
			creatPlayIcon.setVisibility(View.GONE);
		}
		userTitleTab.setBackgroundColor(getResources().getColor(userTitleBgRid));
		gameTitleTab.setBackgroundColor(getResources().getColor(gameTitleBgRid));
		groupTitleTab.setBackgroundColor(getResources().getColor(groupTitleBgRid));
		// 设置右边的菜单功能

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.i(TAG, "------------>DiscoverFragment::onCreateView:getView()=" + getView());
		// 初始化数据
		selectTime = UserFilterView.getSelectTimeByShare();
		selectSex = UserFilterView.getSelectGenderByShare();
		selectGamePlatform = GameFilterView.getSelectGamePlatformByShare();
		selectGameType = GameFilterView.getSelectGameGtypeByShare();
		selectVerify = GroupFilterView.getSelectVerifyByShare();
		selectGames = SystemContext.getInstance().getDiscoverGame();
		userSids = SystemContext.getInstance().getDiscoverUserService();
		groupSids = SystemContext.getInstance().getDiscoverGroupService();

		// 添加发现到主界面
		View v = inflater.inflate(R.layout.discover_common_content, container, false);
		contentView = (LinearLayout) v.findViewById(R.id.contentView);
		//陪玩过滤数据查询
		if (playGameFilterView != null) {
			playGameFilterView.setPlayFilterData();
		}
		// 初始化界面
		initialize(v, contentView, inflater);
		LogUtil.i(TAG, "------------>DiscoverFragment::onCreateView");
		return v;
	}

	/**
	 * 
	 * @param v
	 * @param contentView
	 * @param inflater
	 */
	
	private void initialize(View v, LinearLayout contentView, LayoutInflater inflater) {
		v.findViewById(R.id.bottomHitView).setVisibility(View.VISIBLE);

		// 设置TITLE
		titleTxt = (TextView) v.findViewById(R.id.titleTxt);
		// 设置TITLE选项卡
		titleTabContent = (LinearLayout) v.findViewById(R.id.titleTabContent);
		userTitleTab = (View) v.findViewById(R.id.userTitleTab);
		gameTitleTab = (View) v.findViewById(R.id.gameTitleTab);
		groupTitleTab = (View) v.findViewById(R.id.groupTitleTab);

		// 添加右边功能按钮
		View view = View.inflate(getActivity(), R.layout.right_view, null);
		searchBtn = (RelativeLayout) view.findViewById(R.id.search_iv);
		searchIcon = (ImageView) view.findViewById(R.id.searchIcon);
		creatPlayIcon = (ImageView) view.findViewById(R.id.creatPlayIcon);
		selectedBtn = (Button) view.findViewById(R.id.select_iv);
		selectedBtn.setTextColor(AdaptiveAppContext.getInstance().getAppConfig().getDisplay_title_textcolor());
		searchIcon.setOnClickListener(this);
		creatPlayIcon.setOnClickListener(this);
		selectedBtn.setOnClickListener(this);
		LinearLayout rightView = (LinearLayout) v.findViewById(R.id.rightView);
		rightView.addView(view);

		// 设置主显示内容
		if (pview == null) {
			pview = View.inflate(getActivity(), R.layout.fragment_discove_main, null);
			tabContent = (LinearLayout) pview.findViewById(R.id.tabContent);
			viewPager = (ViewPager) pview.findViewById(R.id.viewPage);
			userTab = (TextView) pview.findViewById(R.id.userTab);
			userTab.setSelected(true);
			gameTab = (TextView) pview.findViewById(R.id.gameTab);
			gameTab.setSelected(false);
			if (AdaptiveAppContext.getInstance().getAppConfig().isDiscover_game()) {
				gameTab.setVisibility(View.VISIBLE);
				gameTitleTab.setVisibility(view.VISIBLE);
			} else {
				gameTab.setVisibility(view.GONE);
				gameTitleTab.setVisibility(view.GONE);
			}
			groupTab = (TextView) pview.findViewById(R.id.groupTab);
			groupTab.setSelected(false);
			userTab.setOnClickListener(this);
			gameTab.setOnClickListener(this);
			groupTab.setOnClickListener(this);
			pageViews = getViews();
			setNoDataHeight();
			viewPager.setOnTouchListener(this);
			viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					if (arg0 == 0) {
						userTab.setSelected(true);
						gameTab.setSelected(false);
						groupTab.setSelected(false);
						userTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_pre_color));
						gameTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_nor_color));
						groupTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_nor_color));
						searchIcon.setVisibility(View.VISIBLE);
						creatPlayIcon.setVisibility(View.GONE);
					} else if (arg0 == 1) {
						if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2) {
							userTab.setSelected(false);
							groupTab.setSelected(true);
							userTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_nor_color));
							groupTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_pre_color));
							searchIcon.setVisibility(View.VISIBLE);
							creatPlayIcon.setVisibility(View.GONE);
						} else if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 1) {
							userTab.setSelected(false);
							gameTab.setSelected(true);
							groupTab.setSelected(false);
							userTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_nor_color));
							gameTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_pre_color));
							groupTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_nor_color));
							searchIcon.setVisibility(View.GONE);
							creatPlayIcon.setVisibility(View.VISIBLE);
						}
					} else if (arg0 == 2) {
						userTab.setSelected(false);
						gameTab.setSelected(false);
						groupTab.setSelected(true);
						userTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_nor_color));
						gameTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_nor_color));
						groupTitleTab.setBackgroundColor(getResources().getColor(R.color.discover_title_tab_bg_pre_color));
						searchIcon.setVisibility(View.VISIBLE);
						creatPlayIcon.setVisibility(View.GONE);
					}
					setTitle();
					changeData(arg0);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
			ViewPagerAdapter adapter = new ViewPagerAdapter(pageViews);
			viewPager.setAdapter(adapter);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if (pview.getParent() != null)
			((LinearLayout) pview.getParent()).removeView(pview);
		contentView.addView(pview, params);
		setTitle();
	}

	/**
	 * 
	 */
	private void setTitle() {
		if (showMainTable) {
			titleTxt.setText("发现");
			titleTabContent.setVisibility(View.GONE);
		} else {
			if (userTab.isSelected()) {
				titleTxt.setText("玩家");
			} else if (gameTab.isSelected()) {
				titleTxt.setText("陪玩");
			} else if (groupTab.isSelected()) {
				titleTxt.setText("公会");
			}
			titleTabContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取里面的控件的高度
	 */
	private void setNoDataHeight() {
		playerView.post(new Runnable() {

			@Override
			public void run() {
				int height = playerView.getMeasuredHeight();
				((DiscoverUserAdapter) playerView.adapter).setNoDataHeight(height);
				 ((DiscoverPlayListAdapter)playView.adapter).setNoDataHeight(height);
				((DiscoverCommonGameAdapter) gameView.adapter).setNoDataHeight(height);
				((DiscoverGroupAdapter2) groupView.adapter).setNoDataHeight(height);
			}
		});
	}

	/**
	 * 获取多个view
	 * 
	 * @return
	 */
	private List<View> getViews() {
		int appmode = AdaptiveAppContext.getInstance().getAppConfig().getMode();
		viewList = new ArrayList<View>();
		playerView = new CommonListView(getActivity(), R.layout.common_filter_list, View.VISIBLE) {
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				temp = false;
				if (this.listData.size() <= 0)
					this.setLoadingUI();
				this.postDelayed(new Runnable() {
					public void run() {
						if (isUserInit) {
							isUserInit = false;
							setRefreshMode(Mode.DISABLED);
							ProxyFactory.getInstance().getCache().getData(Cache.DATA_TYPE_DISCOVER_USER, new CacheCallBack() {

								@Override
								public void onBack(Object result) {
									if (result != null) {
										playerView.listData.addAll((List<Map<String, Object>>) result);
										discoverAdapter.setFlag(true);
										playerView.adapter.notifyDataSetChanged();
										playerView.hasNext = false;
									}
									playerView.onHeaderRefreshComplete();
									onHeaderRefresh();
								}
							});
						} else {
							getLocation(playerView, TYPE_USER, isRefresh);
						}
					}
				}, 200);
			}
		};
		discoverAdapter = new DiscoverUserAdapter(getActivity(), playerView.listData, R.layout.user_list_item_userfragment,
				new String[] { "nickname" }, new int[] { R.id.nickname }, DiscoverUserAdapter.GAME_FOLLOW_TYPE_COMMON, playerView.list);
		discoverAdapter.setmShowDis(true);
		discoverAdapter.setIsshowcount(true);
		playerView.setAdapter(discoverAdapter);
		playerView.getmPullRefreshListView().setMoveEvent(new PullToRefreshBase.MoveEvent() {

			@Override
			public void onPullDown() {
				showMainTab(true, 300);
			}

			@Override
			public void onPullUp() {
				showMainTab(false, 300);
			}

		});
		// 添加列表点击功能
		playerView.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
		// 添加用户过滤内容
		userFilterView = new UserFilterView(getActivity(), new UserFilterView.RefreshDataListener() {

			@Override
			public void onRefreshData() {
				selectTime = UserFilterView.getSelectTimeByShare();
				selectSex = UserFilterView.getSelectGenderByShare();
				userSids = SystemContext.getInstance().getDiscoverUserService();
				playerView.refreshList();
				loadDialog.show();
			}

			@Override
			public void onRefreshGroup(Boolean verify, String sids) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRefreshUser(Integer gender, Integer time, String sids) {
				// TODO Auto-generated method stub

			}
		}, appmode != 2 ? View.VISIBLE : View.GONE, FilterView.MODE_DISCOVER);
		((LinearLayout) playerView.findViewById(R.id.filterContent)).addView(userFilterView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		viewList.add(playerView);
		gameView = new CommonListView(getActivity(), R.layout.common_filter_list, View.VISIBLE) {
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				if (this.listData.size() <= 0)
					this.setLoadingUI();
				this.postDelayed(new Runnable() {
					public void run() {
						if (isGameInit) {
							setRefreshMode(Mode.DISABLED);
							isGameInit = false;
							ProxyFactory.getInstance().getCache().getData(Cache.DATA_TYPE_DISCOVER_GAME, new CacheCallBack() {

								@Override
								public void onBack(Object result) {
									if (result != null) {
										gameView.listData.addAll((List<Map<String, Object>>) result);
										gameView.adapter.notifyDataSetChanged();
										gameView.hasNext = false;
									}
									if (gameView.listData.size() <= 0)
										gameView.showNullBgView(R.drawable.common_no_seach_postbar);
									gameView.onHeaderRefreshComplete();
									onHeaderRefresh();
								}
							});
						} else {
							getLocation(gameView, TYPE_GAME, isRefresh);
						}
					}
				}, 200);
			}
		};
		gameView.setAdapter(new DiscoverCommonGameAdapter(getActivity(), gameView.listData, R.layout.game_list_item, new String[] { "gamename",
				"distance" }, new int[] { R.id.gamename, R.id.rdesc }, 0, CommonGameAdapter.MODE_COMMON, CommonGameAdapter.DESC_MODE_0));
		gameView.getmPullRefreshListView().setMoveEvent(new PullToRefreshBase.MoveEvent() {

			@Override
			public void onPullDown() {
				showMainTab(true, 300);
			}

			@Override
			public void onPullUp() {
				showMainTab(false, 300);
			}

		});
		// 添加游戏过滤内容
		gameFilterView = new GameFilterView(getActivity(), new UserFilterView.RefreshDataListener() {

			@Override
			public void onRefreshData() {
				selectGamePlatform = GameFilterView.getSelectGamePlatformByShare();
				selectGameType = GameFilterView.getSelectGameGtypeByShare();
				gameView.refreshList();
				loadDialog.show();
			}

			@Override
			public void onRefreshGroup(Boolean verify, String sids) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRefreshUser(Integer gender, Integer time, String sids) {
				// TODO Auto-generated method stub

			}
		});
		((LinearLayout) gameView.findViewById(R.id.filterContent)).addView(gameFilterView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// 添加列表点击功能
		gameView.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				Object gid = map.get("gid");
				if (gid != null) {
					Intent intent = new Intent(parent.getContext(), GameTopicListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, Long.valueOf(gid.toString()));
					bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, String.valueOf(map.get("gamename")));
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					startActivity(intent);
				}
			}
		});
		playView = new CommonListView(getActivity(), R.layout.discover_play_filter_list, View.VISIBLE, true, false) {
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				if (this.listData.size() <= 0){
					this.setLoadingUI();
					if(loadDialog.isShowing()){
						loadDialog.dismiss();
					}
				}
				if(playRefresh){
					this.postDelayed(new Runnable() {
						public void run() {
							LogUtil.d(TAG, "-------->>searchDiscoverPlays get play listdata");
							if (isPlayInit) {
								setRefreshMode(Mode.DISABLED);
								isPlayInit = false;
								ProxyFactory.getInstance().getCache().getData(Cache.DATA_TYPE_DISCOVER_PLAY, new CacheCallBack() {
									
									@Override
									public void onBack(Object result) {
										if (result != null) {
											playView.listData.addAll((List<Object>) result);
											playView.adapter.notifyDataSetChanged();
											playView.hasNext = false;
										}
										playView.onHeaderRefreshComplete();
										onHeaderRefresh();
									}
								});
							} else {
								getLocation(playView, TYPE_PLAY, isRefresh);
							}
						}
					}, 200);
				}
			}
		};
		playView.setAdapter(new DiscoverPlayListAdapter(getActivity(), playView.listData));
		playView.getmPullRefreshListView().setMoveEvent(new PullToRefreshBase.MoveEvent() {

			@Override
			public void onPullDown() {
				showMainTab(true, 300);
			}

			@Override
			public void onPullUp() {
				showMainTab(false, 300);
			}

		});
		//添加陪玩过滤内容
		playGameFilterView = new PlayGameFilterView(getActivity(), new FilterView.RefreshDataListener() {

			@Override
			public void onRefreshData() {
				playView.playRefresh = true;
				playView.refreshList();
				loadDialog.show();
			}

			@Override
			public void onRefreshGroup(Boolean verify, String sids) {
				
			}

			@Override
			public void onRefreshUser(Integer gender, Integer time, String sids) {
				
			}
			
			
		}, FilterView.MODE_DISCOVER);
		((LinearLayout) playView.findViewById(R.id.filterContent)).addView(playGameFilterView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		
		// 添加列表点击功能
		playView.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int realposition = (int) parent.getAdapter().getItemId(position);
				Object object = playView.listData.get(realposition);
				if (realposition != -1 && object != null && object instanceof PlayInfo) {
					Intent intent = new Intent(parent.getContext(), PlayDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_PID, ((PlayInfo) object).getPlayid());
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, PlayDetailInfoActivity.MODE_1);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2)
			viewList.remove(playView);
		else
			viewList.add(playView);

		groupView = new CommonListView(getActivity(), R.layout.common_filter_list, View.VISIBLE) {
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				if (this.listData.size() <= 0)
					this.setLoadingUI();
				this.postDelayed(new Runnable() {
					public void run() {
						if (isGroupInit) {
							setRefreshMode(Mode.DISABLED);
							isGroupInit = false;
							ProxyFactory.getInstance().getCache().getData(Cache.DATA_TYPE_DISCOVER_GROUP, new CacheCallBack() {

								@Override
								public void onBack(Object result) {
									if (result != null) {
										groupView.listData.addAll((List<Map<String, Object>>) result);
										groupView.adapter.notifyDataSetChanged();
										groupView.hasNext = false;
									}
									if (groupView.listData.size() <= 0)
										groupView.showNullBgView(R.drawable.common_no_seach_group);
									groupView.onHeaderRefreshComplete();
									onHeaderRefresh();
								}
							});
						} else {
							getLocation(groupView, TYPE_GROUP, isRefresh);
						}
					}
				}, 200);
			}
		};
		groupView.setAdapter(new DiscoverGroupAdapter2(getActivity(), groupView.listData, R.layout.group_list_item_userfragment, new String[] {
				"name", "desc1", "desc2" }, new int[] { R.id.gnameTxt, R.id.desc1, R.id.desc2 }));
		groupView.getmPullRefreshListView().setMoveEvent(new PullToRefreshBase.MoveEvent() {

			@Override
			public void onPullDown() {
				showMainTab(true, 300);
			}

			@Override
			public void onPullUp() {
				showMainTab(false, 300);
			}

		});
		// 添加公会过滤内容
		groupFilterView = new GroupFilterView(getActivity(), new FilterView.RefreshDataListener() {

			@Override
			public void onRefreshData() {
				selectVerify = GroupFilterView.getSelectVerifyByShare();
				if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2) {
					selectGames = AdaptiveAppContext.getInstance().getAppConfig().getGameId() + "";
				} else {
					selectGames = SystemContext.getInstance().getDiscoverGame();
				}
				groupSids = SystemContext.getInstance().getDiscoverGroupService();
				groupView.refreshList();
				loadDialog.show();
			}

			@Override
			public void onRefreshGroup(Boolean verify, String sids) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRefreshUser(Integer gender, Integer time, String sids) {
				// TODO Auto-generated method stub

			}
		}, appmode != 2 ? View.VISIBLE : View.GONE, appmode != 2 ? View.VISIBLE : View.GONE, FilterView.MODE_DISCOVER);
		((LinearLayout) groupView.findViewById(R.id.filterContent)).addView(groupFilterView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// 添加列表点击功能
		groupView.list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				Long grid = (Long) map.get("grid");
				int rel;
				if (map.containsKey("relwithgroup")) {
					rel = (Integer) map.get("relwithgroup");
				} else {
					GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
					if (grid != null) {
						GroupVo groupVo = groupDao.findGroupByGrid(grid);
						if (groupVo == null)
							rel = 0;
						else
							rel = groupVo.getRelWithGroup();
					} else {
						return;
					}
				}
				if (rel == 0) {
					Intent intent = new Intent(parent.getContext(), GroupDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					// 群聊
					Intent intent = new Intent(getActivity(), GroupChatFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, (Long) map.get("grid"));
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					startActivity(intent);
				}
			}
		});
		viewList.add(groupView);

		return viewList;
	}

	//主选项卡是否动画显示或隐藏中
	private boolean isanimation = false;
	/**
	 * 主选项卡显示或隐藏
	 * @param show
	 * @param isanimation
	 */
	private void showMainTab(final boolean show, long duration) {
		if(isanimation || showMainTable == show)
			return;
		isanimation = true;
		int fy = 0;
		int ty = 0;
		final int dis = DisplayUtil.dip2px(getActivity(), 37);
		if (show) {
			fy = -dis;
		} else {
			ty = -dis;
		}
		TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, fy, ty);
		translateAnimation.setFillAfter(true);
		// 设置动画时间
		translateAnimation.setDuration(duration);
		translateAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				if(!show){
					setListViewMarginTop(0);
				}
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(show){
					setListViewMarginTop(dis);
					showMainTable = true;
				}else{
					showMainTable = false;
				}
				isanimation = false;
				setTitle();
			}
		});
		tabContent.setVisibility(View.GONE);
		setTabContentMargin(ty, dis);
		tabContent.setVisibility(View.VISIBLE);
		tabContent.startAnimation(translateAnimation);
	}
	
	public void setTabContentMargin(int top,int height){
		RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, height);
		layoutParam.setMargins(0, top, 0, 0);
		tabContent.setLayoutParams(layoutParam);
//		tabContent.invalidate();
	}
	
	public void setListViewMarginTop(int top){
        RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams( RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT); 
        layoutParam.setMargins(0, top, 0, 0);
        viewPager.setLayoutParams(layoutParam);
        viewPager.invalidate();
    }

	private static final int TYPE_USER = 1;
	private static final int TYPE_GAME = 2;
	private static final int TYPE_PLAY = 3;
	private static final int TYPE_GROUP = 4;

	private void getLocation(final CommonListView view, final int type, final boolean isrefresh) {
		ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {

			@Override
			public void onBack(BDLocation bdLocation) {
				if (type == TYPE_USER)
					getUserData(view, isrefresh);
				if (type == TYPE_GAME)
					getGameData(view, isrefresh);
				if (type == TYPE_PLAY)
					getPlayData(view, isrefresh);
				if (type == TYPE_GROUP)
					getGroupData(view, isrefresh);
				if ("0.000000,0.000000".equals(SystemContext.getInstance().getLocation())) {
					ToastUtil.showToast(getActivity(), getString(R.string.com_haveno_location));
				}
			}
		});
	}

	/**
	 * 获取用户数据
	 */
	private void getUserData(final CommonListView view, final boolean isrefresh) {
		Boolean isfate = null;
		Integer rel = null;
		Long gid = null;
		Boolean isrecommend = null;
		Integer near = SystemContext.getInstance().getNearDistance();
		String resulttype = "3,4,5";
		ProxyFactory.getInstance().getUserProxy().searchUsersBysid(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (view.isRefresh)
					view.clean();
				if (view.isRefresh) {
					view.onHeaderRefreshComplete();
				} else {
					view.onFooterRefreshComplete();
				}
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					view.offset = result.getOffset();
					if (result.getItems().size() < view.limit)
						view.hasNext = false;
					view.listData.addAll(praseUserList(result.getItems()));
				} else {
					LogUtil.d(TAG, "数据为空");
					view.showNullBgView(R.drawable.common_no_seach_uers);
				}
				if (view.listData.size() <= 0) {
					view.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					view.listData.clear();
					List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
					Map<String, Object> mp = new HashMap<String, Object>();
					tmplist.add(mp);
					view.listData.addAll(tmplist);
				} else {
					view.setRefreshMode(Mode.BOTH);
				}
				discoverAdapter.setFlag(true);
				view.adapter.notifyDataSetChanged();
				view.adapter.notifyDataSetInvalidated();
				view.postDelayed(new Runnable() {

					@Override
					public void run() {
						view.showListView();
					}
				}, 200);
				int firstvisPos = view.list.getFirstVisiblePosition();
				int lastVisPos = view.list.getLastVisiblePosition();
				if (result != null)
					view.list.setSelection(view.listData.size() - result.getItems().size() - (lastVisPos - firstvisPos) + 1);
				ProxyFactory.getInstance().getCache().saveData(Cache.DATA_TYPE_DISCOVER_USER, view.listData);
				temp = true;
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (view.listData.size() <= 0) {
					view.showNullBgView(R.drawable.common_no_seach_group);
					view.listData.clear();
					List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
					Map<String, Object> mp = new HashMap<String, Object>();
					tmplist.add(mp);
					view.listData.addAll(tmplist);
					view.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
				} else {
					view.setRefreshMode(Mode.BOTH);
					view.postDelayed(new Runnable() {

						@Override
						public void run() {
							view.showListView();
						}
					}, 200);
				}
				discoverAdapter.setFlag(true);
				view.adapter.notifyDataSetChanged();
				view.adapter.notifyDataSetInvalidated();
				if (view.isRefresh) {
					view.onHeaderRefreshComplete();
				} else {
					view.onFooterRefreshComplete();
				}
				LogUtil.e(TAG, "获取用户数据失败" + result);
				temp = true;
			}
		}, getActivity(), gid, isfate, isrecommend, rel, resulttype, null, view.offset, view.limit, near, selectTime, selectSex, true, userSids, 2);
	}

	/**
	 * 获取贴吧数据
	 */
	private void getGameData(final CommonListView view, final boolean isrefresh) {
		ProxyFactory
				.getInstance()
				.getGameProxy()
				.getConditionGame(new ProxyCallBack<PagerVo<ExtGameVo>>() {

					@Override
					public void onSuccess(PagerVo<ExtGameVo> result) {
						if (loadDialog.isShowing())
							loadDialog.dismiss();
						if (isrefresh && result != null && result.getItems().size() > 0)
							view.clean();
						view.setRefreshMode(Mode.BOTH);
						if (view.isRefresh) {
							view.onHeaderRefreshComplete();
							view.clean();
						} else {
							view.onFooterRefreshComplete();
						}
						if (result != null && result.getItems() != null && result.getItems().size() > 0) {
							view.offset = result.getOffset();
							view.listData.addAll(praseGameList(result.getItems()));
							if (view.listData.size() > Math.abs(view.limit))
								view.list.setSelectionFromTop(view.list.getFirstVisiblePosition(), 0);
							view.adapter.notifyDataSetChanged();
							view.adapter.notifyDataSetInvalidated();
						} else {
							LogUtil.d(TAG, "数据为空");
							view.showNullBgView(R.drawable.common_no_seach_postbar);
						}
						if (view.listData.size() <= 0) {
							view.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
							view.listData.clear();
							List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
							Map<String, Object> mp = new HashMap<String, Object>();
							tmplist.add(mp);
							view.listData.addAll(tmplist);
						} else {
							view.setRefreshMode(Mode.BOTH);
						}
						view.adapter.notifyDataSetChanged();
						view.adapter.notifyDataSetInvalidated();
						view.showListView();
						ProxyFactory.getInstance().getCache().saveData(Cache.DATA_TYPE_DISCOVER_GAME, view.listData);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if (loadDialog.isShowing())
							loadDialog.dismiss();
						if (view.listData.size() <= 0) {
							view.showNullBgView(R.drawable.common_no_seach_postbar);
							view.listData.clear();
							List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
							Map<String, Object> mp = new HashMap<String, Object>();
							tmplist.add(mp);
							view.listData.addAll(tmplist);
							view.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
						} else {
							view.setRefreshMode(Mode.BOTH);
							view.postDelayed(new Runnable() {

								@Override
								public void run() {
									view.showListView();
								}
							}, 200);
						}
						view.adapter.notifyDataSetChanged();
						view.adapter.notifyDataSetInvalidated();
						if (view.isRefresh) {
							view.onHeaderRefreshComplete();
						} else {
							view.onFooterRefreshComplete();
						}
						LogUtil.d(TAG, "获取贴吧列表数据为空");
					}
				}, getActivity(), null, null, null, "2", null, view.offset, view.limit, 1, SystemContext.getInstance().getNearDistance(),
						selectGameType, true, selectGamePlatform, null);

		if (selectGameType == null) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_NET_ALL, null, null, null, null, null);
		} else if (selectGameType == GameSelectView.NETTYPE_NET) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_NET, null, null, null, null, null);
		} else if (selectGameType == GameSelectView.NETTYPE_NONET) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_NET_NO, null, null, null, null, null);
		}

		if (selectGamePlatform == null) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_ALL, null, null, null, null, null);
		} else if (selectGamePlatform.equals(GameSelectView.PLATFORM_PF1)) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_PC_GAME, null, null, null, null, null);
		} else if (selectGamePlatform.equals(GameSelectView.PLATFORM_PF2)) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_PAGE_GAME, null, null, null, null, null);
		} else if (selectGamePlatform.equals(GameSelectView.PLATFORM_PF3)) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_MOBILE_GAME, null, null, null, null, null);
		} else if (selectGamePlatform.equals(GameSelectView.PLATFORM_PF4)) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_TV_GAME, null, null, null, null, null);
		} else if (selectGamePlatform.equals(GameSelectView.PLATFORM_PF4)) {
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_DISCOVER_BAR_CHOOSE_MI_GAME, null, null, null, null, null);
		}
	}
	
	private void getPlayData(final CommonListView view, final boolean isrefresh) {
		if (view == null)
			return;
		long gid = playGameFilterView.getGid();
		long sid = playGameFilterView.getSid();
		int sorttype = playGameFilterView.getSorttype();
		Long keyid = playGameFilterView.getKeyid();
		String keyval = playGameFilterView.getKeyval();
		int sex = playGameFilterView.getSex();
		String postion = SystemContext.getInstance().getLocation();
		ProxyFactory.getInstance().getPlayProxy().searchDiscoverPlays(new ProxyCallBack<PagerVo<PlayInfo>>() {

			@Override
			public void onSuccess(PagerVo<PlayInfo> result) {
				LogUtil.info("lll playData  onSuccess result = " + result.getItems().size()+ ", view.listData size = " + view.listData.size());
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (isrefresh && result != null && result.getItems().size() > 0)
					view.clean();
				view.setRefreshMode(Mode.BOTH);
				if (view.isRefresh) {
					view.onHeaderRefreshComplete();
					view.clean();
				} else {
					view.onFooterRefreshComplete();
				}
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					view.offset = result.getOffset();
					view.listData.addAll(result.getItems());
					if (view.listData.size() > Math.abs(view.limit))
						view.list.setSelectionFromTop(view.list.getFirstVisiblePosition(), 0);
					view.adapter.notifyDataSetChanged();
					view.adapter.notifyDataSetInvalidated();
				} else {
					LogUtil.d(TAG, "数据为空");
					if (view.listData.size() <= 0) {
//						view.showNullBgView(R.drawable.common_no_seach_play);
					}
				}
				if (view.listData.size() <= 0) {
					view.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					view.listData.clear();
					view.listData.add(new Object());
				} else {
					view.setRefreshMode(Mode.BOTH);
				}
				view.adapter.notifyDataSetChanged();
				view.adapter.notifyDataSetInvalidated();
				view.postDelayed(new Runnable() {

					@Override
					public void run() {
						view.showListView();
					}
				}, 200);
				ProxyFactory.getInstance().getCache().saveData(Cache.DATA_TYPE_DISCOVER_PLAY, view.listData);
				int firstvisPos = view.list.getFirstVisiblePosition();
				int lastVisPos = view.list.getLastVisiblePosition();
				if (result != null)
					view.list.setSelection(view.listData.size() - result.getItems().size() - (lastVisPos - firstvisPos) + 1);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.info("lll playData   onFailure result = " + result + ", resultMsg = " + resultMsg+", view.listData size = "  + view.listData.size());
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if(view.listData.size()<=0){
					view.listData.add(new Object());
				}
				view.setRefreshMode(Mode.BOTH);
				view.postDelayed(new Runnable() {

					@Override
					public void run() {
						view.showListView();
					}
				}, 200);
				view.adapter.notifyDataSetChanged();
				view.adapter.notifyDataSetInvalidated();
				if (view.isRefresh) {
					view.onHeaderRefreshComplete();
				} else {
					view.onFooterRefreshComplete();
				}
				LogUtil.e(TAG, "搜索陪玩数据失败");
			}
		}, getActivity(), gid, sid, sorttype, keyid, keyval, sex, "1,2,3,4", postion, view.offset, view.limit);
	}

	/**
	 * 获取公会数据
	 */
	private void getGroupData(final CommonListView view, final boolean isrefresh) {
		if (view == null) {
			return;
		}

		Integer minCount = null;
		Integer maxCount = null;
		ProxyFactory.getInstance().getGroupProxy().searchGroups(new ProxyCallBack<PagerVo<GroupVo>>() {

			@Override
			public void onSuccess(PagerVo<GroupVo> result) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (isrefresh && result != null && result.getItems().size() > 0)
					view.clean();
				view.setRefreshMode(Mode.BOTH);
				if (view.isRefresh) {
					view.onHeaderRefreshComplete();
					view.clean();
				} else {
					view.onFooterRefreshComplete();
				}
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					view.offset = result.getOffset();
					view.listData.addAll(praseGroupList(result.getItems()));
					if (view.listData.size() > Math.abs(view.limit))
						view.list.setSelectionFromTop(view.list.getFirstVisiblePosition(), 0);
					view.adapter.notifyDataSetChanged();
					view.adapter.notifyDataSetInvalidated();
				} else {
					LogUtil.d(TAG, "数据为空");
					view.showNullBgView(R.drawable.common_no_seach_group);
				}
				if (view.listData.size() <= 0) {
					view.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
					view.listData.clear();
					List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
					Map<String, Object> mp = new HashMap<String, Object>();
					tmplist.add(mp);
					view.listData.addAll(tmplist);
				} else {
					view.setRefreshMode(Mode.BOTH);
				}
				view.adapter.notifyDataSetChanged();
				view.adapter.notifyDataSetInvalidated();
				view.postDelayed(new Runnable() {

					@Override
					public void run() {
						view.showListView();
					}
				}, 200);
				ProxyFactory.getInstance().getCache().saveData(Cache.DATA_TYPE_DISCOVER_GROUP, view.listData);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (loadDialog.isShowing())
					loadDialog.dismiss();
				if (selectGames != null)
					view.listData.clear();// 如果通过游戏去筛选公会，查询不到数据，则把原来的数据清空
				if (view.listData.size() <= 0) {
					view.showNullBgView(R.drawable.common_no_seach_group);
					view.listData.clear();
					List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
					Map<String, Object> mp = new HashMap<String, Object>();
					tmplist.add(mp);
					view.listData.addAll(tmplist);
					view.setRefreshMode(Mode.PULL_DOWN_TO_REFRESH);
				} else {
					view.setRefreshMode(Mode.BOTH);
					if (view.listData.size() > 0) {
						view.postDelayed(new Runnable() {

							@Override
							public void run() {
								view.showListView();
							}
						}, 200);
					}
				}
				view.adapter.notifyDataSetChanged();
				view.adapter.notifyDataSetInvalidated();
				if (view.isRefresh) {
					view.onHeaderRefreshComplete();
				} else {
					view.onFooterRefreshComplete();
				}
				LogUtil.e(TAG, "搜索公会数据失败");
			}
		}, getActivity(), null, null, selectGames, view.limit, view.offset, SystemContext.getInstance().getNearDistance(), minCount, maxCount, selectVerify, true, null, groupSids);
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
				map.put("desc", "");
				map.put("sex", vo.getSex());
				map.put("age", vo.getAge());
				map.put("uid", vo.getUid());
				map.put("news", vo.getMood());
				map.put("rel", vo.getRel());
				map.put("gids", vo.getGids());
				map.put("gameCount", vo.getCount());
				map.put("sortId", vo.getSortId());
				map.put("isChecked", false);
				String dis = DistanceUtil.covertDistance(vo.getDistance());
				if ("".equals(dis)) {
					map.put("distance", SafeUtils.getDate2MyStr2(vo.getLastLogin()));
				} else {
					map.put("distance", dis + " | " + SafeUtils.getDate2MyStr2(vo.getLastLogin()));
				}
				map.put("grade", vo.getGrade());
				map.put("new", vo.getNews());
				tmplist.add(map);
			}

		}
		return tmplist;
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseGameList(List<ExtGameVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			ExtGameVo vo = list.get(i);
			map.put("logo", vo.getGamelogo());
			map.put("gamename", vo.getGamename());
			map.put("gid", vo.getGameid());
			map.put("sortId", vo.getSortId());
			map.put("desc", "总关注：" + Utils.convertAmount(vo.getFollowCount()));
			map.put("desc3", "今日访问：" + vo.getVisitCount());
			map.put("isChecked", false);
			tmplist.add(map);
		}
		return tmplist;
	}

	/**
	 * 解析列表数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseGroupList(List<GroupVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				GroupVo vo = list.get(i);
				map.put("serial", vo.getSerial());
				map.put("avatar", vo.getAvatar());
				map.put("name", vo.getName());
				map.put("grid", vo.getGrid());
				map.put("desc1", "附近人数：" + vo.getNearTotal() + " | 总人数：" + vo.getTotal() + "/" + vo.getMaxcount());
				map.put("presidentId", vo.getPresidentId());
				map.put("desc2", "会长：" + vo.getPresidentName());
				map.put("sortId", vo.getSortId());
				map.put("isChecked", false);
				map.put("gameIcon", vo.getGameIcon());
				map.put("grade", vo.getGrade());
				tmplist.add(map);
			}
		}
		return tmplist;
	}

	/**
	 * 设置筛选用户监听
	 */
	private void creatUserSelectedDialog() {
		dialog = new Dialog(getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				selectedBtn.setEnabled(true);
			}
		});
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("玩家筛选");

		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		content.removeAllViews();
		final UserSelectView usView = new UserSelectView(getActivity());
		content.addView(usView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				usView.saveSelectItems();
				selectSex = usView.getSelectSex();
				selectTime = usView.getSelectTime();
				userSids = SystemContext.getInstance().getDiscoverUserService();
				playerView.refreshList();
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

	/**
	 * 设置筛选贴吧监听
	 */
	private void creatGameSelectedDialog() {
		dialog = new Dialog(getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				selectedBtn.setEnabled(true);
			}
		});
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("贴吧筛选");

		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		content.removeAllViews();
		final GameSelectView gsView = new GameSelectView(getActivity());
		content.addView(gsView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				gsView.saveSelectItems();
				selectGameType = gsView.getSelectGameType();
				selectGamePlatform = gsView.getSelectGamePlatform();
				gameView.refreshList();
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

	/**
	 * 
	 */
	private void getFollowGames() {
		GameDao dao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		List<GameVo> gameVoList = dao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE);
		if (gameVoList != null && gameVoList.size() > 0) {
			creatGroupSelectedDialog(gameVoList);
		} else {
			ProxyFactory.getInstance().getGameProxy().getFollowGames(new ProxyCallBack<List<GameVo>>() {

				@Override
				public void onSuccess(List<GameVo> result) {
					creatGroupSelectedDialog(result);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					creatGroupSelectedDialog(null);
				}
			}, getActivity(), true);
		}
	}

	/**
	 * 设置筛选公会监听
	 */
	private void creatGroupSelectedDialog(List<GameVo> result) {
		dialog = new Dialog(getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				selectedBtn.setEnabled(true);
			}
		});
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("公会筛选");
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		content.removeAllViews();
		final GroupSelectView grView = new GroupSelectView(getActivity(), result, mode);
		content.addView(grView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				grView.saveSelectItems();
				if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 1) {
					selectGames = grView.getSelectGames();
				} else {
					selectGames = AdaptiveAppContext.getInstance().getAppConfig().getGameId() + "";
				}
				selectVerify = grView.getSelectVerify();
				groupSids = SystemContext.getInstance().getDiscoverGroupService();
				groupView.refreshList();
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

	/**
	 * onclick方法 当点击界面上面的按钮的时候 就执行下面的这个方法
	 */
	@Override
	public void onClick(View v) {
		if (v == searchIcon) {
			if (viewPager.getCurrentItem() == 0) {
				Intent intent = new Intent(getActivity(), AddFriendActivity.class);
				getActivity().startActivity(intent);
			} else if (viewPager.getCurrentItem() == 1) {
				if (mode == 2) {
					Intent intent = new Intent(getActivity(), AddGroupActivity.class);
					getActivity().startActivity(intent);
				}
			} else if (viewPager.getCurrentItem() == 2) {
				Intent intent = new Intent(getActivity(), AddGroupActivity.class);
				getActivity().startActivity(intent);
			}
		} else if (v == creatPlayIcon) {
			if (viewPager.getCurrentItem() == 1 && mode == 1) {// 创建陪玩
				Intent intent = new Intent(getActivity(), CreateUserPlayActivity.class);
				startActivity(intent);
			}
		} else if (v == selectedBtn) {
			selectedBtn.setEnabled(false);
			if (viewPager.getCurrentItem() == 0) {// 选中玩家按钮
				creatUserSelectedDialog();
			} else if (viewPager.getCurrentItem() == 1) {// 选中贴吧按钮
				if (mode == 2) {
					getFollowGames();
				} else if (mode == 1) {
					creatGameSelectedDialog();
				}
			} else if (viewPager.getCurrentItem() == 2) {// 选中公会按钮
				getFollowGames();
			}
		} else if (v.getId() == R.id.userTab) {
			userTab.setSelected(true);
			gameTab.setSelected(false);
			groupTab.setSelected(false);
			viewPager.setCurrentItem(0);
		} else if (v.getId() == R.id.gameTab) {
			userTab.setSelected(false);
			gameTab.setSelected(true);
			groupTab.setSelected(false);
			viewPager.setCurrentItem(1);
		} else if (v.getId() == R.id.groupTab) {
			userTab.setSelected(false);
			gameTab.setSelected(false);
			groupTab.setSelected(true);
			if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2) {
				viewPager.setCurrentItem(1);
			} else if (AdaptiveAppContext.getInstance().getAppConfig().getMode() == 1) {
				viewPager.setCurrentItem(2);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View,
	 * android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (channelTouchPageIndex == -1) {
			if (viewPager.getCurrentItem() == channelTouchPageIndex) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}
}
