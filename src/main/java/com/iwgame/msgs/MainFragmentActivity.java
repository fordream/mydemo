/**      
 * FrameActivity.java Create on 2013-8-5     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseSuperFragmentActivity;
import com.iwgame.msgs.common.CallBackData;
import com.iwgame.msgs.common.ImageCacheLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupGradeDao;
import com.iwgame.msgs.localdb.dao.UserGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.discover.ui.DiscoverFragment;
import com.iwgame.msgs.module.game.ui.GameFragment2;
import com.iwgame.msgs.module.game.ui.GameNewsFragment;
import com.iwgame.msgs.module.message.ui.MessageFragment2;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.module.setting.ui.PointMarketActivity;
import com.iwgame.msgs.module.setting.ui.SettingFragment;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.PointTaskDetailActivity;
import com.iwgame.msgs.module.user.ui.UserFragment;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.service.MessageService;
import com.iwgame.msgs.service.UpdateService;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.UpdataVersionUtil;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: FrameActivity
 * @Description: 主框架
 * @author 王卫
 * @date 2013-8-5 上午10:17:40
 * @Version 1.0
 * 
 */
public class MainFragmentActivity extends BaseSuperFragmentActivity implements IMainFragment {

	private static final String TAG = "MainFragmentActivity";
	// 定义FragmentTabHost对象
	private FragmentTabHost mTabHost;
	// TabHost 遮罩层
	private RelativeLayout tabhost_shade;
	// 定义一个布局
	private LayoutInflater layoutInflater;
	// 定义数组来存放Fragment界面
	@SuppressWarnings("rawtypes")
	private Class fragmentArray[] = new Class[5];

	// 定义数组来存放按钮图片
	private int mImageViewArray[] = { R.drawable.tab_main_message_selector, R.drawable.tab_main_discover_selector, R.drawable.tab_main_game_selector,
			R.drawable.tab_main_contrant_selector, R.drawable.tab_more_btn_selector };
	// Tab选项卡的文字
	private String mTextviewArray[] = { "msg", "discover", "game", "user", "setting" };

	private LinearLayout photoContentView;
	// 头像VIEW
	private ImageView mAvatarView;

	// 临时头像文件
	private File sdcardTempFile;

	// 未读动态消息显示VIEW
	private TextView unreadNewsView;
	// 未读动态消息显示VIEW
	private LinearLayout extView;

	private static Boolean isExit = false;
	private static Boolean hasTask = false;

	Dialog dialog = null;

	private static MainFragmentActivity instance;
	Timer tExit = new Timer();
	private ImageView settingTag;
	// 标记数据我上面的红点是不是由于任务或者商城有变化引起
	private boolean flag = false;
	private int width;
	private UserGradeDao userGradeDao;
	private GroupGradeDao groupGradeDao;
	private List<UserGradeVo> usergradevoList;
	private List<GroupGradeVo> groupgradevolList;

	private MyBroadCastReceiver receiver;

	public ImageView getSettingTag() {
		return settingTag;
	}

	public void setSettingTag(ImageView settingTag) {
		this.settingTag = settingTag;
	}

	private boolean temp = false;// 标记当前是不是游客

	public boolean isTemp() {
		return temp;
	}

	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public FragmentTabHost getmTabHost() {
		return mTabHost;
	}

	public void setmTabHost(FragmentTabHost mTabHost) {
		this.mTabHost = mTabHost;
	}

	public LinearLayout getExtView() {
		return extView;
	}

	public void setExtView(LinearLayout extView) {
		this.extView = extView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int updataMode = UpdataVersionUtil.getVersionUpdataMode(AppUtils.getLocalAppVersionCode(this), SystemConfig.C_VERSION_CODE,
				SystemConfig.UC_VERSION_CODE);
		if (!NetworkUtil.isNetworkAvailable(this) && UpdataVersionUtil.UPDATA_MODE_MUST == updataMode) {
			this.finish();
			return;
		}

		userGradeDao = DaoFactory.getDaoFactory().getUserGradeDao(SystemContext.getInstance().getContext());
		groupGradeDao = DaoFactory.getDaoFactory().getGroupGradeDao(SystemContext.getInstance().getContext());
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;// 宽度
		receiver = new MyBroadCastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SystemConfig.ACTION_RECEIVEMESSAGE_INTFO_BROADCAST);
		this.registerReceiver(receiver, filter);
		AdaptiveAppContext.getInstance().setAppConfig(null);
		instance = this;
		// 禁止默认的页面统计方式
		MobclickAgent.openActivityDurationTrack(false);
		// 调试模式true/正常模式false
		MobclickAgent.setDebugMode(false);
		// //定义发送间隔
		ShareSDK.initSDK(this);
		SystemContext.getInstance().setOpenMessage(false);
		SystemContext.getInstance().initCompleted = false;
		SystemContext.mainFragmentActivity = this;
		LogUtil.d(TAG, "----------主界面MainFragmentActivity::onCreate");

		getAppConfig();
		initFragmentArray();
		// 初始化UI
		setContentView(R.layout.main_tab);
		initView();
		// 跳转到启动初始化界面
		Intent intent = new Intent(this, LaunchActivity.class);
		startActivity(intent);
		getNewVersion();
	}

	private void getAppConfig() {
		// 加载AppConfig配置
		AppConfig appConfig = null;
		AdaptiveAppContext.isHasGetAppConfigFromNet = false;
		String appConfigString = AdaptiveAppContext.getInstance().getAppConfigString();
		if (appConfigString != null) {
			appConfig = AdaptiveAppContext.getInstance().getAppConfig(appConfigString);
		}
		if (appConfig == null) {
			appConfig = AdaptiveAppContext.getInstance().getAppConfig();
		}
		if (appConfig != null) {
			AdaptiveAppContext.getInstance().setAppConfig(appConfig);
		}
	}

	/**
	 * 初始化五个切换的页面组合
	 */
	private void initFragmentArray() {
		fragmentArray[0] = MessageFragment2.class;
		fragmentArray[1] = DiscoverFragment.class;
		if (AdaptiveAppContext.getInstance().getAppConfig().getGame_model() == 1) {// 游伴原生模式
			fragmentArray[2] = GameFragment2.class;
		} else {// 攻略Html形式
			fragmentArray[2] = GameNewsFragment.class;
		}
		fragmentArray[3] = UserFragment.class;
		fragmentArray[4] = SettingFragment.class;
	}

	public static MainFragmentActivity getInstance() {
		return instance;
	}

	/**
	 * 判断是否要显示我 上面的tag
	 */
	private void isShowSettingTag() {
		if (SystemContext.getInstance().getIsGuest() == 0) {
			temp = false;
			if (!flag)
				settingTag.setVisibility(View.VISIBLE);
		} else {
			settingTag.setVisibility(View.VISIBLE);
			temp = true;
		}
	}

	/**
	 * 判断是否有新的版本
	 */
	private void getNewVersion() {
		ProxyFactory.getInstance().getSettingProxy().getGlobalConfig(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				int umode = UpdataVersionUtil.getVersionUpdataMode(AppUtils.getLocalAppVersionCode(MainFragmentActivity.this),
						SystemConfig.C_VERSION_CODE, SystemConfig.UC_VERSION_CODE);
				if (umode == UpdataVersionUtil.UPDATA_MODE_NO) {
					flag = true;
					if (SystemContext.getInstance().getIsGuest() == 0)
						settingTag.setVisibility(View.INVISIBLE);
					else if (SystemContext.getInstance().getIsGuest() == 1)
						settingTag.setVisibility(View.VISIBLE);
				} else {
					flag = false;
					settingTag.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "请求全局配置数据失败");
			}
		}, SystemContext.GLOBAL_CONFIG_URL);
	}

	/**
	 * 通过判断是否有商品消息 和任务消息来判断是否显示settingtag
	 * 
	 * @return
	 */
	private boolean showSettingTagByMsgs() {
		List<MessageVo> list = ProxyFactory.getInstance().getMessageProxy()
				.getMessageByChannelTypeAndCategory(MsgsConstants.MC_PUB, MsgsConstants.MCC_INFO);
		if (list == null)
			return false;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			MessageVo vo = list.get(i);
			if (vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_TYPE_GOODS))
					|| vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_TYPE_TASK)))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		isExit = false;
		hasTask = true;

		SystemContext.getInstance().setStopOverMain(true);
		MobclickAgent.onResume(this);
		// 更新为读数
		setMsgUnreadCount();
		isShowSettingTag();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SystemContext.getInstance().setStopOverMain(false);
		MobclickAgent.onPause(this);
		if (dialog != null) {
			dialog.dismiss();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if (receiver != null)
			this.unregisterReceiver(receiver);
		ImageCacheLoader.getInstance().saveDataToDb(getApplicationContext());
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.FragmentActivity#onNewIntent(android.content.Intent
	 * )
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		checkSettingTag();
		LogUtil.d(TAG, "---------->主界面MainFragmentActivity::onNewIntent");
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			int mode = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE);
			int index = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_INDEX);
			LogUtil.d(TAG, "---------->主界面MainFragmentActivity::mode=" + mode);
			if (mode == -1) {// 完全退出应用
				this.finish();
			} else if (mode == -2) {// 正常启动
				long time = System.currentTimeMillis();
				LogUtil.d(TAG, "---------------->页面切换数据时间开始=" + time);
				mTabHost.setCurrentTab(2);
				ProxyFactory.getInstance().getMessageProxy().getUserSubscribeChannel(MainFragmentActivity.this);
				SystemContext.getInstance().setOpenMessage(true);
				LogUtil.d(TAG, "---------->主界面MainFragmentActivity::初始化启动");
				SystemContext.getInstance().initCompleted = true;
				CallBackData.flag = true;
				LogUtil.d(TAG, "---------------->页面切换数据时间=" + (System.currentTimeMillis() - time));

				/**
				 * 同步我的公会
				 */
				ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GROUP, new SyncCallBack() {

					@Override
					public void onSuccess(Object result) {
					}

					@Override
					public void onFailure(Integer result) {
					}
				});
				ProxyFactory.getInstance().getUserProxy().upContacts(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}
				}, this);

				usergradevoList = userGradeDao.queryAll();
				if (!(usergradevoList != null && usergradevoList.size() > 0)) {
					// 获取用户等级策略
					ProxyFactory.getInstance().getUserProxy().getUserGradePolicy(new ProxyCallBack<List<UserGradeVo>>() {

						@Override
						public void onSuccess(List<UserGradeVo> result) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							// TODO Auto-generated method stub

						}
					}, this);
				}

				groupgradevolList = groupGradeDao.queryAll();
				if (!(groupgradevolList != null && groupgradevolList.size() > 0)) {
					ProxyFactory.getInstance().getGroupProxy().getGroupGradePolicy(new ProxyCallBack<List<GroupGradeVo>>() {
						@Override
						public void onSuccess(List<GroupGradeVo> result) {
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
						}
					}, this);
				}
				// 同步我关注的用户数据
				syncUserListData();
				// 检测更新
				int ntype = NetworkUtil.getNetworkType(SystemContext.getInstance().getContext());
				if (ntype == NetworkUtil.NETTYPE_WIFI) {// WIFI
					dialog = UpdataVersionUtil.checkUpdata(this, false, null);
				}
				ProxyFactory.getInstance().getUserProxy().getMessageTip(new ProxyCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
					}
				}, this);
				// 添加自动关注功能
				MessageService.getLocalInstallPackages();
				// 跳转到帖子详情界面
				if (ShareTransitActivity.tid != null && ShareTransitActivity.gid != null) {
					Intent tintent = new Intent(this, TopicDetailActivity.class);
					Bundle tbundle = new Bundle();
					tbundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, Long.valueOf(ShareTransitActivity.tid));
					tbundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, Long.valueOf(ShareTransitActivity.gid));
					tintent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, tbundle);
					startActivity(tintent);
					ShareTransitActivity.tid = null;
					ShareTransitActivity.gid = null;
				}
				//获取游戏服务器列表
				ProxyFactory.getInstance().getGameProxy().getGameService(new ProxyCallBack<Msgs.UserGameServerResult>() {

					@Override
					public void onSuccess(Msgs.UserGameServerResult result) {
						SystemContext.getInstance().setGameServices(result);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}
				}, MainFragmentActivity.this);
			} else if (mode == -3) {// 退出应用
				this.finish();
			} else if (mode == -4 || mode == 0) {
				Intent updateIntent = new Intent(this, UpdateService.class);
				Bundle bundle2 = new Bundle();
				bundle2.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -4);
				updateIntent.putExtras(bundle2);
				this.startService(updateIntent);
				if (SystemContext.getInstance().getExtUserVo() == null) {
					// 跳转到启动初始化界面
					startActivity(new Intent(this, LoginActivity.class));
				}
			} else if (mode == -5) {// 跳转至登录界面
				int type = bundle.getInt("Type");

				Intent intent3 = new Intent(MainFragmentActivity.this, LoginActivity.class);
				Bundle bundle3 = new Bundle();
				if (type == 0) {// 被迫下线
					bundle3.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED, true);
				} else if (type == 1) {// 会话失效
					bundle3.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_EXPIRED, true);
				}
				intent3.putExtras(bundle3);
				startActivity(intent3);
			} else {
				jumpFragment(mode, index);
			}
		}
	}

	/**
	 * 检测在游客进入后， 在绑定手机后进入 是否显示settingtag
	 */
	private void checkSettingTag() {
		if (flag && SystemContext.getInstance().getIsGuest() == 0 && settingTag != null)
			settingTag.setVisibility(View.INVISIBLE);
	}

	/**
	 * 同步用户关系数据
	 */
	private void syncUserListData() {
		// TODO Auto-generated method stub
		final Long beforeSyncKey = SystemContext.getInstance().getUserRelSyncKey();
		ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_REL_USER, new SyncCallBack() {

			@Override
			public void onSuccess(Object result) {

			}

			@Override
			public void onFailure(Integer result) {
			}
		});
	}

	/**
	 * 
	 * @param mode
	 * @param index
	 */
	private void jumpFragment(int mode, int index) {
		if (mode == 1) {
			DiscoverFragment.index = index;
		} else if (mode == 3) {
			UserFragment.index = index;
		}
		mTabHost.setCurrentTab(mode);
	}

	/**
	 * 
	 */
	private void validateAccount() {
		ProxyFactory.getInstance().getAccountProxy().validateAccount(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				LogUtil.d(TAG, "账号验证成功 result:" + result);
				ProxyFactory.getInstance().getMessageProxy().getUserSubscribeChannel(MainFragmentActivity.this);
				SystemContext.getInstance().setOpenMessage(true);
				LogUtil.d(TAG, "---------->主界面MainFragmentActivity::初始化启动");
				SystemContext.getInstance().initCompleted = true;
				mTabHost.setCurrentTab(2);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.d(TAG, "账号验证失败 result:" + result + ", resultMsg=" + resultMsg);
				Intent intent = new Intent(MainFragmentActivity.this, LoginActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEALACCOUNT, true);
				bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_DESC, resultMsg);
				intent.putExtras(bundle);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		}, this);
	}

	/**
	 * 更新新闻未读数信息
	 * 
	 * @param count
	 */
	@Override
	public void updataNewsUnReadCount(int count) {
		try {
			if (unreadNewsView != null) {
				if (count > 0) {
					unreadNewsView.setVisibility(View.VISIBLE);
					if (count < 100) {
						unreadNewsView.setBackgroundResource(R.drawable.news_count_bg2);
						unreadNewsView.setText(String.valueOf(count));
					} else {
						unreadNewsView.setBackgroundResource(R.drawable.news_count_bg2);
						unreadNewsView.setText(String.valueOf(99) + "+");
					}
				} else {
					unreadNewsView.setBackgroundResource(R.drawable.news_count_bg2);
					unreadNewsView.setVisibility(View.GONE);
					unreadNewsView.setText("0");
				}
			} else {
				return;
			}
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.main.ui.IMainFragment#getSDcardTempFile()
	 */
	@Override
	public File getSDcardTempFile() {
		if (sdcardTempFile == null) {
			sdcardTempFile = new File(Environment.getExternalStorageDirectory(), "msgs_tmp_pic" + ".png");
		}
		return sdcardTempFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.main.ui.IMainFragment#setPhotoContentView()
	 */
	@Override
	public void setPhotoContentView(LinearLayout v) {
		photoContentView = v;
	}

	/**
	 * 初始化
	 */
	private void initView() {
		// 实例化布局对象
		layoutInflater = LayoutInflater.from(this);

		extView = (LinearLayout) findViewById(R.id.extView);
		extView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				extView.removeAllViews();
				extView.setVisibility(View.GONE);
			}
		});
		tabhost_shade = (RelativeLayout) findViewById(R.id.tabhost_shade);
		tabhost_shade.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		// 实例化TabHost对象，得到TabHost
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		LayoutParams parap = mTabHost.getLayoutParams();
		int height = 55 + AdaptiveAppContext.getInstance().getAppConfig().getDisplay_height();
		parap.height = DisplayUtil.dip2px(this, height);
		mTabHost.setLayoutParams(parap);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				extView.setVisibility(View.GONE);
				extView.removeAllViews();
				if (tabId.equals(mTextviewArray[1])) {
					if (!SystemContext.getInstance().getGuideDiscover() && SystemContext.getInstance().isnotShowGuide()) {
						SystemContext.getInstance().setGuideDiscover(true);
						// 设置内容UI
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
						View v = View.inflate(MainFragmentActivity.this, R.layout.guide, null);
						((ImageView) v.findViewById(R.id.guideView)).setBackgroundResource(R.drawable.guide_mode_discover_group);
						extView.addView(v, params);
						extView.setVisibility(View.VISIBLE);
					}
				} else if (tabId.equals(mTextviewArray[2])) {
					if (!SystemContext.getInstance().getGuidGame() && SystemContext.getInstance().isnotShowGuide()) {
						if (AdaptiveAppContext.getInstance().getAppConfig().isGame_guide()) {
							SystemContext.getInstance().setGuidGame(true);
							// 设置内容UI
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							View v = View.inflate(MainFragmentActivity.this, R.layout.guide, null);
							((ImageView) v.findViewById(R.id.guideView)).setBackgroundResource(R.drawable.guide_mode_follow_game);
							extView.addView(v, params);
							extView.setVisibility(View.VISIBLE);
						}
					}
				} else if (tabId.equals(mTextviewArray[3])) {
					int mode = AdaptiveAppContext.getInstance().getAppConfig().getMode();
					if (!SystemContext.getInstance().getGuidUserGroup() && mode == 1 && SystemContext.getInstance().isnotShowGuide()) {
						SystemContext.getInstance().setGuidUserGroup(true);
						// 设置内容UI
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
						View v = View.inflate(MainFragmentActivity.this, R.layout.guide, null);
						((ImageView) v.findViewById(R.id.guideView)).setBackgroundResource(R.drawable.guide_mode_user_add);
						extView.addView(v, params);
						extView.setVisibility(View.VISIBLE);
					} else if (!SystemContext.getInstance().getGuidUserFollow() && mode == 2 && SystemContext.getInstance().isnotShowGuide()) {
						// 表示的是单攻略
						SystemContext.getInstance().setGuidUserFollow(true);
						// 设置内容UI
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
						View v = View.inflate(MainFragmentActivity.this, R.layout.guide, null);
						((ImageView) v.findViewById(R.id.guideView)).setBackgroundResource(R.drawable.guide_mode_user_follow);
						extView.addView(v, params);
						extView.setVisibility(View.VISIBLE);
					}
				} else if (tabId.equals(mTextviewArray[4])) {
					if (settingTag != null) {
						if (!flag || temp) {
							settingTag.setVisibility(View.VISIBLE);
						} else {
							settingTag.setVisibility(View.GONE);
						}
					}
				}
			}
		});
		// 得到fragment的个数
		int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			// 设置Tab按钮的背景
		}
	}

	private void setMsgUnreadCount() {
		MessageFragment2.runUnReadCount(new ProxyCallBack<Map<String, Object>>() {

			@Override
			public void onSuccess(Map<String, Object> result) {
				// TODO Auto-generated method stub
				// 刷新总未读数
				updataNewsUnReadCount(Integer.valueOf(result.get("unreadCount_All").toString()).intValue());
				// 取消状态栏上的通知
				NotificationManager nm = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				nm.cancel(SystemConfig.NOTIFICATION_ID_BASE);

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * 根据索引值获取界面
	 * 
	 * @param index
	 * @return
	 */
	private View getTabItemView(final int index) {
		View view = layoutInflater.inflate(R.layout.main_tab_item_view, null);
		if (index == 0) {
			unreadNewsView = (TextView) view.findViewById(R.id.unreadcount);
		} else if (index == 4) {
			settingTag = (ImageView) view.findViewById(R.id.setting_tag);
		}
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		LayoutParams para;
		para = imageView.getLayoutParams();
		para.width = LayoutParams.MATCH_PARENT;
		int height = 55 + AdaptiveAppContext.getInstance().getAppConfig().getDisplay_height();
		para.height = DisplayUtil.dip2px(this, height);
		imageView.setLayoutParams(para);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setImageResource(mImageViewArray[index]);
		imageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN && index == 2 && mTabHost.getCurrentTab() == 2) {
					GameFragment2 gameFragment = GameFragment2.getInstance();
					if (gameFragment != null) {
						gameFragment.goTop();
					}
				}
				return false;
			}
		});
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.IMainFragment#setAvatarView(android.widget.ImageView)
	 */
	@Override
	public void setAvatarView(ImageView avatarView) {
		mAvatarView = avatarView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onKeyDown(int,
	 * android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!isExit) {
				isExit = true;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				if (hasTask) {
					tExit.schedule(new TimerTask() {
						@Override
						public void run() {
							isExit = false; // 取消退出
							hasTask = true;
						}
					}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
					// tExit.schedule(task, 2000);
				}
			} else {
				finish();
			}
		}
		return false;
	}

	/**
	 * 当接收到广播 说有新任务的时候，则去同步一下任务
	 */
	private void syncTask() {
		// 首先去同步数据
		ProxyFactory.getInstance().getUserProxy().getPointTaskDetail(new ProxyCallBack<List<PointTaskVo>>() {
			@Override
			public void onSuccess(List<PointTaskVo> result) {
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

			}
		}, this);

	}

	/**
	 * 获取到栈顶的activity 当加入公会成功的时候 如果 我 当前的页面 不是在公会资料页面 则不需要跳转到聊天室页面
	 * 
	 * @return
	 */
	private String getTopActivity() {
		ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return null;
	}

	private native void initUninstall();

	/**
	 * 定义一个广播类来 控制显示我上面的小红点
	 * 
	 * @author jczhang
	 * 
	 */
	class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (SystemConfig.ACTION_RECEIVEMESSAGE_INTFO_BROADCAST.equals(intent.getAction())) {
				if (MainFragmentActivity.this.getmTabHost().getCurrentTab() != 4) {
					settingTag.setVisibility(View.VISIBLE);
				}
				Bundle bundle = intent.getExtras();
				MessageVo vo = (MessageVo) bundle.getSerializable(SystemConfig.BUNDLE_NAME_MESSAGEVO);
				SettingFragment instance = SettingFragment.getInstance();
				if (vo != null && vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_TYPE_GOODS))) {
					if (instance != null && instance.getPointMartketTag() != null) {
						if (!getTopActivity().equals(PointMarketActivity.class.getName())) {
							instance.getPointMartketTag().setVisibility(View.VISIBLE);
						} else {
							ProxyFactory
									.getInstance()
									.getMessageProxy()
									.delByChannelTypeAndCategory(MsgsConstants.MC_PUB, MsgsConstants.MCC_INFO,
											MsgsConstants.RESOURCE_APP_TYPE_GOODS + "");
						}
					}
				} else if (vo != null && vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_TYPE_TASK))) {
					syncTask();
					if (instance != null && instance.getTaskTag() != null) {
						if (!getTopActivity().equals(PointTaskDetailActivity.class.getName())) {
							instance.getTaskTag().setVisibility(View.VISIBLE);
							instance.getTaskTag().setBackgroundResource(R.drawable.common_tag_new);
						} else {
							ProxyFactory
									.getInstance()
									.getMessageProxy()
									.delByChannelTypeAndCategory(MsgsConstants.MC_PUB, MsgsConstants.MCC_INFO,
											MsgsConstants.RESOURCE_APP_TYPE_TASK + "");
						}
					}
				}
			}
		}
	}

	public void showOrDismissTabHost(boolean isShow, Animation inAnimation, Animation outAnimation) {
		if (isShow) {// 显示
			// if (inAnimation != null) {
			// mTabHost.startAnimation(inAnimation);
			// }
			mTabHost.setVisibility(View.VISIBLE);
			tabhost_shade.setVisibility(View.GONE);
		} else {// 隐藏
			// if (outAnimation != null) {
			// mTabHost.startAnimation(outAnimation);
			// }
			mTabHost.setVisibility(View.GONE);
		}
	}

	public void setShadeVisible() {
		tabhost_shade.setVisibility(View.VISIBLE);
	}
}
