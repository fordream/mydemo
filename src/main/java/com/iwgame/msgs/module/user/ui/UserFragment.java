/**      
 * ContractFragment.java Create on 2013-8-5     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.FollowMenuVo;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ClearCacheListener;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheCallBack;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.game.adapter.ViewPagerAdapter;
import com.iwgame.msgs.module.group.adapter.GroupAdapter2;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.module.user.ui.widget.FollowListView;
import com.iwgame.msgs.module.user.ui.widget.GroupListView;
import com.iwgame.msgs.module.user.ui.widget.UserGameSelect;
import com.iwgame.msgs.utils.GuideUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.AddButton;
import com.iwgame.msgs.widget.SideBar;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.popwindow.MsgsPopTextItem;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow.OnClickPopItemListener;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.PinyinUtil;
import com.youban.msgs.R;

/**
 * @ClassName: ContractFragment
 * @Description: 通讯录主界面
 * @author 王卫
 * @date 2013-8-5 下午05:16:21
 * @Version 1.0
 * 
 */
public class UserFragment extends BaseFragment implements OnTouchListener, OnClickListener {

	private static final String TAG = "UserFragment";

	public static int index = -1;
	// 内容
	private LinearLayout contentView;
	// 左边布局
	private LinearLayout leftMenu;
	// 右边布局
	private LinearLayout rightMenu;
	// 添加关注、公会按钮
	private Button addBtn;
	// 用户标签
	private TextView followTab;
	// 贴吧标签
	private TextView fansTab;
	// 公会标签
	private TextView groupTab;

	// 当前要取消的触屏页面索引
	private int channelTouchPageIndex = -1;
	// 切换页面组件
	private ViewPager viewPager = null;

	// 页面集合列表
	private List<View> pageViews = null;
	// 显示内容列表
	private List<View> viewList = null;
	// 玩家内容
	private FollowListView followView;
	// 贴吧内容
	private CommonListView fansView;
	// 公会内容
	private GroupListView groupView;

	private View pview;

	private long uid;

	private long tuid;

	private Dialog dialog;
	private List<UserVo> list = new ArrayList<UserVo>();

	MsgsPopTextItem item1;
	MsgsPopTextItem item2;
	MsgsPopTextItem item3;
	MsgsPopTextItem item4;
	GameVo vo = new GameVo();

	private UserAdapter2 uadapter;

	private UserAdapter2 adapter;

	private GroupAdapter2 groupAdapter;

	private Map<Long, String> dis;
	
	private View v;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PTAG = TAG;
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
	 * @see android.support.v4.app.Fragment#onResume()
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
			if(getActivity() instanceof MainFragmentActivity){
				((MainFragmentActivity)getActivity()).showOrDismissTabHost(true, null, null);
			}
			if(followView != null)followView.setFlag(true);
			ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
			if (vo != null) {
				long uid = vo.getUserid();
				if (uid != tuid) {
					if (viewPager != null){
						viewPager.setCurrentItem(AdaptiveAppContext.getInstance().getAppConfig().getUser_defaultSelect());
					}
					tuid = uid;
				}
				if(index != -1){
					viewPager.setCurrentItem(index);
					index = -1;
				}
				int item = viewPager.getCurrentItem();
				if (item == 0) {
					groupView.refreshList();
				} else if (item == 1) {
					GuideUtil.startGuide(getActivity(), GuideActivity.GUIDE_MODE_USER_FOLLOW);
					followView.refreshList();
				} else if (item == 2) {
					fansView.refreshList();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		if(dialog != null) dialog.dismiss();
		if(followView != null) followView.searchTxt.setText("");

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
		// 添加通讯录主界面
		if(v == null){
			v = inflater.inflate(R.layout.common_content, container, false);
		}
		ViewGroup parent = (ViewGroup) v.getParent();
		if(parent != null)parent.removeAllViews();
		initialize(v);// 初始化界面
		return v;
	}

	/**
	 * 初始化界面
	 */
	private void initialize(View v) {
		// 设置TITLE
		TextView titleTxt = (TextView) v.findViewById(R.id.titleTxt);
		titleTxt.setText("通讯录");
		v.findViewById(R.id.bottomHitView).setVisibility(View.VISIBLE);

		// 隐藏左边菜单
		leftMenu = (LinearLayout) v.findViewById(R.id.left);
		leftMenu.setVisibility(View.INVISIBLE);

		// 添加右边功能按钮
		addBtn = new AddButton(v.getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout rightView = (LinearLayout) v.findViewById(R.id.rightView);
		rightMenu = (LinearLayout) v.findViewById(R.id.right);
		rightMenu.setVisibility(View.VISIBLE);
		rightView.addView(addBtn, params);
		addBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), UserAddActivity.class);
				startActivity(intent);
			}
		});

		// 设置主显示内容
		contentView = (LinearLayout) v.findViewById(R.id.contentView);
		if (pview == null) {
			pview = View.inflate(getActivity(), R.layout.fragment_user_main, null);
			viewPager = (ViewPager) pview.findViewById(R.id.viewPage);
			followTab = (TextView) pview.findViewById(R.id.followTab);
			followTab.setSelected(false);
			fansTab = (TextView) pview.findViewById(R.id.fansTab);
			fansTab.setSelected(false);
			groupTab = (TextView) pview.findViewById(R.id.groupTab);
			groupTab.setSelected(true);
			followTab.setOnClickListener(this);
			fansTab.setOnClickListener(this);
			groupTab.setOnClickListener(this);
			pageViews = getViews();
			viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					if(GuideActivity.getInstance() != null && arg0 != 1) GuideActivity.getInstance().finish();
					InputMethodManager imm = (InputMethodManager) viewPager.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(viewPager.getWindowToken(), 0);
					if (arg0 == 0) {
						GuideUtil.startGuide(getActivity(), GuideActivity.GUIDE_MODE_USER_GROUP);
						followTab.setSelected(false);
						fansTab.setSelected(false);
						groupTab.setSelected(true);
						rightMenu.setVisibility(View.VISIBLE);
						groupView.refreshList();
					} else if (arg0 == 1) {
						GuideUtil.startGuide(getActivity(), GuideActivity.GUIDE_MODE_USER_FOLLOW);
						followTab.setSelected(true);
						fansTab.setSelected(false);
						groupTab.setSelected(false);
						rightMenu.setVisibility(View.VISIBLE);
						followView.refreshList();
					} else if (arg0 == 2) {
						followTab.setSelected(false);
						fansTab.setSelected(true);
						groupTab.setSelected(false);
						fansView.refreshList();
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub
					Log.i("index", ""+arg0);

				}
			});
			ViewPagerAdapter adapter = new ViewPagerAdapter(pageViews);
			viewPager.setAdapter(adapter);
		}
		if (pview.getParent() != null)
			((LinearLayout) pview.getParent()).removeView(pview);
		if(followView != null)followView.setFlag(false);
		contentView.addView(pview, params);
		ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
		if (vo != null) {
			long newuid = vo.getUserid();
			if (newuid != uid) {
				followView.clean();
				fansView.clean();
				groupView.clean();
				uid = newuid;
			}
		}
	}


	/**
	 * 获取多个view
	 * 
	 * @return
	 */
	private List<View> getViews() {
		dis = init();
		viewList = new ArrayList<View>();
		groupView = new GroupListView(getActivity()) {

			public void getListData(long offset, int limit) {
				if(this.listData.size() <= 0)this.setLoadingUI();
				groupAdapter.setFlag(true);
				this.postDelayed(new Runnable() {

					@Override
					public void run() {
						getCacheGroupData(groupView, Cache.DATA_TYPE_CONTRANCT_GROUP);
					}
				}, 300);
			}
			//重写了clean方法
			public void clean() {
				isRefresh = false;
				listData.clear();
				if (adapter instanceof ClearCacheListener) {
					((ClearCacheListener) adapter).clearCache();
				}
			}
		};
		groupAdapter = new GroupAdapter2(getActivity(), groupView.listData, R.layout.group_list_item_userfragment, new String[] { "name", "desc1", "desc2" },
				new int[] { R.id.gnameTxt, R.id.desc1, R.id.desc2 }, GroupAdapter2.MODE_MY_GROUP,groupView.list);
//		groupAdapter.setViewBinder(new ViewBinder() {
//			
//			@Override
//			public boolean setViewValue(View view, Object data, String textRepresentation) {
//				// TODO Auto-generated method stub
//				if( (view instanceof ImageView) & (data instanceof Bitmap) ) {  
//				            ImageView iv = (ImageView) view;  
//					            Bitmap bm = (Bitmap) data;  
//					             iv.setImageBitmap(bm);  
//					               return true;  
//					              }  
//					               return false; 
//
//			}
//		});
		// 设置列表数据
		groupView.setAdapter(groupAdapter);
		setGroupListItemClikEvent(groupView.list);
		viewList.add(groupView);
		followView = new FollowListView(getActivity()) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.module.discover.ui.CommonListView#getListData
			 * (long, int)
			 */
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				uadapter.setFlag(true);
				if(this.listData.size() <= 0)this.setLoadingUI();
				if(viewPager != null && viewPager.getCurrentItem() == 1){
					followView.findViewById(R.id.sideBar).setVisibility(View.VISIBLE);
					followView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
					this.postDelayed(new Runnable() {
						public void run() {
							if(viewPager != null && viewPager.getCurrentItem() == 1){
								followView.findViewById(R.id.sideBar).setVisibility(View.VISIBLE);
								followView.findViewById(R.id.divider).setVisibility(View.VISIBLE);
								if (followView.keyword == null) {
									if (followView.selectGameVo == null) {
										getCacheUserData(followView, Cache.DATA_TYPE_CONTRANCT_USER_FRIEND,1);
									} else {
										getFollowUserByGame(followView, followView.selectGameVo.getGameid());
									}
								} else {
									//TODO 要传一个gid 进去
									if(followView.selectGameVo == null){
										getUserData(followView, 1, 1, followView.keyword);
									}else{
										followView.listData.clear();
										followView.adapter.notifyDataSetChanged();
										followView.adapter.notifyDataSetInvalidated();
										getUserByGame(followView, followView.selectGameVo.getGameid(),followView.keyword);
									}
								}
							}
						}
					},300);
				}
			}
		};
		setListItemClikEvent(followView.list);
		uadapter = new UserAdapter2(getActivity(), followView.listData, R.layout.user_list_item_userfragment, new String[] { "nickname", "distance" },
				new int[] { R.id.nickname, R.id.rdesc }, false, UserAdapter2.GAME_FOLLOW_TYPE_COMMON, true,followView.list, dis);
		uadapter.setmShowNichen(true);
		uadapter.setmDisRightMaginPX(25);
		followView.setAdapter(uadapter);
		// 设置按字母定位
		setSideBar(followView, followView.list, uadapter);
		viewList.add(followView);
		followView.typeSelect.setOnClickListener(this);
		fansView = new CommonListView(getActivity(), View.GONE) {

			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				UserFragment.this.adapter.setFlag(true);
				if(this.listData.size() <= 0)this.setLoadingUI();
				this.postDelayed(new Runnable() {

					@Override
					public void run() {
						getCacheUserData(fansView, Cache.DATA_TYPE_CONTRANCT_FANS,2);
					}
				}, 300);
			}
		};
		LinearLayout ll = (LinearLayout) fansView.findViewById(R.id.nullContent);
		ll.removeAllViews();
		View v = View.inflate(getActivity(), R.layout.user_null_data_bg, null);
		((TextView) v.findViewById(R.id.desc)).setText("还木有粉丝哦，加油！");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ll.addView(v, params);
		setListItemClikEvent(fansView.list);
		adapter = new UserAdapter2(getActivity(), fansView.listData, R.layout.user_list_item_userfragment, new String[] { "nickname", "distance" },
				new int[] { R.id.nickname, R.id.rdesc }, false, UserAdapter2.GAME_FOLLOW_TYPE_COMMON, true,fansView.list, dis);
		
		fansView.setAdapter(adapter);
		viewList.add(fansView);
		return viewList;
	}




	/**
	 * 给hashmap进行初始化
	 */
	private Map<Long, String> init() {
		Map<Long, String> dis;//用来保存用户的距离和上次登录的时间 
		//第二保存到文件
		try {
			dis = (Map<Long, String>) FileUtils.readFile(SystemContext.getInstance().getContext(), SystemConfig.DISTANCE_AND_LASTLOGIN_FILENAME);
		} catch (IOException e) {
			dis = new HashMap<Long, String>();
		}
		return dis;
	}



	/**
	 * 通过所选择的游戏
	 * 还有所输入的文字筛选用户
	 * @param followView2
	 * @param gameid
	 * @param keyword
	 */
	protected void getUserByGame(final FollowListView v, long gameid,
			String keyword) {
		if(v.listData.size() <= 0)v.setLoadingUI();
		ProxyFactory.getInstance().getUserProxy().searchUsers(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				v.showListView();
				v.onHeaderRefreshComplete();
				v.hasNext = false;
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					v.showListView();
					LogUtil.d(TAG, "------通讯录好友长度=" + result.getItems().size());
					v.listData.clear();
					// 按用户昵称排序结果
					result.setItems(sortUserByNickname2(result.getItems()));
					// 添加设置LIST数据
					v.listData.addAll(praseUserListBygame(result.getItems()));
					v.adapter.notifyDataSetChanged();
				} else {
					v.listData.clear();
					if (v.listData.size() <= 0) {
						v.showNullBgView();
						v.getNullContent().removeAllViews();
						((FollowListView)v).setNoDataUI();
						v.findViewById(R.id.sideBar).setVisibility(View.INVISIBLE);
						v.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
					}
					LogUtil.d(TAG, "数据为空");
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				v.showListView();
				v.hasNext = false;
				v.clean();
				LogUtil.e(TAG, "--------获取通讯录好友：失败");
				if (v.listData.size() <= 0) {
					v.showNullBgView();
					v.getNullContent().removeAllViews();
					((FollowListView)v).setNoDataUI();
					v.findViewById(R.id.sideBar).setVisibility(View.INVISIBLE);
					v.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
				}
			}
		}, getActivity(), gameid, null, null, 1, "1", null, 0, -Integer.MAX_VALUE, null, null, null, null,1,keyword);

	}

	/**
	 * 点击根据贴吧筛选
	 * 
	 * @param v
	 */
	private void onClickUserSelect(View v) {
		List<FollowMenuVo> menuVoList = AdaptiveAppContext.getInstance().getAppConfig().getMenuVoList();
		int size = menuVoList.size();
		FollowMenuVo followMenuVo;
		if(menuVoList != null && size > 0){
			for(int i = 0; i < size; i ++){
				followMenuVo = menuVoList.get(i);
				if(followMenuVo.getGid() == 0){
					item1 = new MsgsPopTextItem(getActivity(), followMenuVo.getMenuName());
				}else if(followMenuVo.getGid() == -1){
					item3 = new MsgsPopTextItem(getActivity(), followMenuVo.getMenuName());
				}else if(followMenuVo.getGid() > 0){
					item4 = new MsgsPopTextItem(getActivity(), followMenuVo.getMenuName());
					vo.setGameid(followMenuVo.getGid());
				}
			}
		}
		item2 = new MsgsPopTextItem(getActivity(), "贴吧分组");
		List<View>items = new ArrayList<View>();
		int mode = AdaptiveAppContext.getInstance().getAppConfig().getMode();
		if(mode == 1){
			items.add(item1);
			items.add(item2);
		}else if(mode == 2){
			if(followView.gid == 0){
				items.add(item4);
			}else if(followView.gid == -1){
				items.add(item1);
				items.add(item4);
			}else if(followView.gid > 0){
				items.add(item1);
			}
		}
		MsgsPopWindow pw = new MsgsPopWindow(getActivity(), items, followView.typeSelect, -150, 0);
		pw.setOnClickPopItemListener(new OnClickPopItemListener() {

			@Override
			public void onClickPopItem(View v) {
				if (v == item1) {
					followView.typeTxt.setText(item1.getTextContent());
					followView.selectGameVo = null;
					followView.gid = 0;
					followView.refreshList();
				} else if (v == item2) {
					initGameContent();
				}else if(v == item3){
					followView.typeTxt.setText(item3.getTextContent());
					followView.selectGameVo = null;
					followView.gid = -1;
					followView.refreshList();
				}else if(v == item4){
					followView.typeTxt.setText(item4.getTextContent());
					followView.selectGameVo = vo;
					followView.gid = vo.getGameid();
					followView.refreshList();
				}
			}
		});
		pw.setOnDismissListener(new android.widget.PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				followView.typeSelect.setEnabled(true);
			}
		});
	}

	/**
	 * 
	 */
	private void initGameContent() {
		creatGameSelectedDialog(DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext())
				.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE));
	}

	/**
	 * 添加贴吧选择对话框
	 */
	private void creatGameSelectedDialog(List<GameVo> result) {
		dialog = new Dialog(getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.findViewById(R.id.dialogLine).setVisibility(View.GONE);
		dialog.findViewById(R.id.title).setVisibility(View.GONE);
		final UserGameSelect ugs = UserGameSelect.getInstance(this.getActivity(), result);
		final LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		content.removeAllViews();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout parent = (LinearLayout)ugs.getParent();
		if(parent != null) 
			parent.removeView(ugs);
		content.addView(ugs, params);

		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				GameVo gvo = ugs.getSelectGameVo();
				followView.selectGameVo = gvo;
				if (gvo != null) {
					followView.gid = gvo.getGameid();
					followView.typeTxt.setText(gvo.getGamename());
					followView.refreshList();
				}
				dialog.dismiss();
				content.removeView(ugs);
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				content.removeView(ugs);
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				followView.typeSelect.setEnabled(true);
				if(ugs.getRealIndex() >= 0){
					ugs.getSelectViewList().get(ugs.getRealIndex()).disSelect();
				}
				content.removeView(ugs);
			}
		});
		dialog.show();
	}

	/**
	 * 
	 * @param view
	 */
	private void getCacheUserData(final CommonListView view, final int type,final int flag) {
		ProxyFactory.getInstance().getCache().getData(type, new CacheCallBack() {

			@Override
			public void onBack(Object result) {
				view.onHeaderRefreshComplete();
				view.hasNext = false;
				if (result != null) {
					List<UserVo> list = (List<UserVo>) result;
					if (list.size() > 0) {
						view.listData.clear();
						LogUtil.d(TAG, "------>通讯录好友长度=" + list.size());
						// 按用户昵称排序结果
						result = sortUserByNickname(list);
						// 添加设置LIST数据
						view.listData.addAll(praseUserList(list));
						view.adapter.notifyDataSetChanged();
						view.showListView();
					} else {
						view.listData.clear();
						if (view.listData.size() <= 0) {
							if(flag == 1){
								((FollowListView)view).setNoDataUI();
							}else if(flag == 2){
								view.setNoDataView();
							}
							if (type == Cache.DATA_TYPE_CONTRANCT_USER_FRIEND) {
								view.findViewById(R.id.sideBar).setVisibility(View.INVISIBLE);
								view.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
							}
						}
						LogUtil.d(TAG, "数据为空");
					}
				} else {
					if (view.listData.size() <= 0) {
						if(flag == 1){
							((FollowListView)view).setNoDataUI();
						}else if(flag == 2){
							view.setNoDataView();
						}
						if (type == Cache.DATA_TYPE_CONTRANCT_USER_FRIEND) {
							view.findViewById(R.id.sideBar).setVisibility(View.INVISIBLE);
							view.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
						}
					}
					LogUtil.d(TAG, "数据为空");
				}
			}
		});
	}

	private void getCacheGroupData(final GroupListView view, int type) {
		if(view.listData.size() <= 0)view.setLoadingUI();
		ProxyFactory.getInstance().getCache().getData(type, new CacheCallBack() {

			@Override
			public void onBack(Object result) {
				groupView.onHeaderRefreshComplete();
				view.hasNext = false;
				view.listData.clear();
				if (result != null) {
					List<GroupVo> list = (List<GroupVo>) result;
					if (list.size() > 0) {
						groupView.listData.addAll(praseGroupList(list));
						groupView.adapter.notifyDataSetChanged();
						view.showListView();
					} else {
						if (groupView.listData.size() <= 0)
							groupView.setAddGroupUI();
						LogUtil.d(TAG, "数据为空");
					}
				} else {
					if (groupView.listData.size() <= 0)
						groupView.setAddGroupUI();
					LogUtil.d(TAG, "数据为空");
				}
			}
		});
	}

	/**
	 * 添加列表点击功能
	 * 
	 * @param list
	 */
	protected void setGroupListItemClikEvent(ListView list) {
		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				// 群聊
				Intent intent = new Intent(UserFragment.this.getActivity(), GroupChatFragmentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, (Long) map.get("grid"));
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRADE, (Integer)map.get("grade"));
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				startActivity(intent);
			}
		});
	}

	/**
	 * 解析列表数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseGroupList(List<GroupVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			GroupVo vo = list.get(i);
			map.put("avatar", vo.getAvatar());
			map.put("serial", vo.getSerial());
			map.put("name", vo.getName());
			map.put("grid", vo.getGrid());
			if(vo.getUndesc() == null){
				map.put("desc1", "简介：" + "");
			}else{
				map.put("desc1", "简介：" + vo.getUndesc());
			}
			map.put("presidentId", vo.getPresidentId());
			if(vo.getPresidentName() == null){
				map.put("desc2", "人数：" + vo.getTotal()+"/"+vo.getMaxcount() + " | 会长：" + "");
			}else{
				map.put("desc2", "人数：" + vo.getTotal()+"/"+vo.getMaxcount() + " | 会长：" + vo.getPresidentName());
			}
			map.put("gameIcon", vo.getGameIcon());
			map.put("gid", vo.getGid());
			map.put("grade", vo.getGrade());
			map.put("relwithgroup", vo.getRelWithGroup());
			map.put("maxcount", vo.getMaxcount());
			map.put("sid", vo.getSid());
			tmplist.add(map);
		}
		return tmplist;
	}

	/**
	 * 设置按字母定位
	 * 
	 * @param v
	 * @param lv
	 */
	private void setSideBar(LinearLayout v, ListView lv, SimpleAdapter adapter) {
		SideBar indexBar = (SideBar) v.findViewById(R.id.sideBar);
		if (indexBar != null) {
			// 设置SideBar的ListView内容实现点击a-z中任意一个进行定位
			indexBar.setListView(lv, adapter);
		}
	}

	/**
	 * 设置列表
	 * 
	 * @param view
	 * @param mode
	 * @param sort
	 * @param keyword
	 */
	private void getUserData(CommonListView view, int mode, int sort, String keyword) {
		// 添加列表点击功能
		setListItemClikEvent(view.list);
		// 设置LIST数据
		CustomProgressDialog dialog = null;
		if (mode == 2)
			dialog = CustomProgressDialog.createDialog(getActivity());
		getUserContactData(view, mode, sort, keyword, dialog);
	}

	/**
	 * 添加列表点击功能
	 * 
	 * @param list
	 */
	private void setListItemClikEvent(final ListView list) {
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
					if(list == followView.list){
						bundle.putBoolean(SystemConfig.IS_FROM_FOLLOWS_LISTVIEW, true);
						if(map.containsKey("remark"))
							bundle.putString(SystemConfig.USER_FOLLOW_REMARK_NAME,map.get("remark"));
					}
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 获取用户通讯录数据
	 * 
	 * @param v
	 * @param mode
	 * @param sort
	 * @param keyword
	 */
	private void getUserContactData(final CommonListView v, final int mode, final int sort, final String keyword, final CustomProgressDialog dialog) {
		LogUtil.d(TAG, "--------获取通讯录好友：mode=" + mode + ", keyord=" + keyword);
		final SideBar indexBar = (SideBar) v.findViewById(R.id.sideBar);
		final ImageView divider = (ImageView) v.findViewById(R.id.divider);
		indexBar.setVisibility(View.VISIBLE);
		divider.setVisibility(View.VISIBLE);
		ProxyFactory.getInstance().getUserProxy().getContactUsers(new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> result) {
				v.onHeaderRefreshComplete();
				v.hasNext = false;
				if (result != null && result.size() > 0) {
					v.showListView();
					LogUtil.d(TAG, "------通讯录好友长度=" + result.size());
					// 按用户昵称排序结果
					result = sortUserByNickname(result);
					list.clear();
					for(int i = 0; i < result.size(); i++){
						UserVo vo = result.get(i);
						String remarkname = vo.getRemarksName();
						String nickname = vo.getUsername();
						if(remarkname != null && !"".equals(remarkname)){
							if(remarkname.contains(followView.searchTxt.getText().toString().trim())){
								list.add(vo);
							}else{
								continue;
							}
						}else{
							if(nickname.contains(followView.searchTxt.getText().toString().trim())){
								list.add(vo);
							}
						}
					}
					v.listData.clear();
					// 添加设置LIST数据
					v.listData.addAll(praseUserList(list));
					v.adapter.notifyDataSetChanged();
				} else {
					v.listData.clear();
					if (v.listData.size() <= 0) {
						v.showNullBgView();
						v.getNullContent().removeAllViews();
						((FollowListView)v).setNoDataUI();
						indexBar.setVisibility(View.INVISIBLE);
						divider.setVisibility(View.INVISIBLE);
					}
					LogUtil.d(TAG, "数据为空");
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				v.onHeaderRefreshComplete();
				v.hasNext = false;
				v.clean();
				LogUtil.e(TAG, "--------获取通讯录好友：失败");
				if (v.listData.size() <= 0) {
					v.showNullBgView();
					v.getNullContent().removeAllViews();
					((FollowListView)v).setNoDataUI();
					indexBar.setVisibility(View.INVISIBLE);
					divider.setVisibility(View.INVISIBLE);
				}
				if (dialog != null)
					dialog.dismiss();
			}
		}, getActivity(), mode, 1, sort, 0, Integer.MAX_VALUE, keyword);
	}

	/**
	 * 
	 * @param gid
	 */
	private void getFollowUserByGame(final CommonListView v, Long gid) {
		ProxyFactory.getInstance().getUserProxy().searchUsers(new ProxyCallBack<PagerVo<UserObject>>() {

			@Override
			public void onSuccess(PagerVo<UserObject> result) {
				v.onHeaderRefreshComplete();
				v.hasNext = false;
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					v.showListView();
					LogUtil.d(TAG, "------通讯录好友长度=" + result.getItems().size());
					v.listData.clear();
					// 按用户昵称排序结果
					result.setItems(sortUserByNickname2(result.getItems()));
					// 添加设置LIST数据
					v.listData.addAll(praseUserListBygame(result.getItems()));
					v.adapter.notifyDataSetChanged();
				} else {
					v.listData.clear();
					if (v.listData.size() <= 0) {
						v.showNullBgView();
						v.getNullContent().removeAllViews();
						((FollowListView)v).setNoDataUI();
						v.findViewById(R.id.sideBar).setVisibility(View.INVISIBLE);
						v.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
					}
					LogUtil.d(TAG, "数据为空");
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				v.hasNext = false;
				v.clean();
				LogUtil.e(TAG, "--------获取通讯录好友：失败");
				if (v.listData.size() <= 0) {
					v.showNullBgView();
					v.getNullContent().removeAllViews();
					((FollowListView)v).setNoDataUI();
					v.findViewById(R.id.sideBar).setVisibility(View.INVISIBLE);
					v.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
				}
			}
		}, getActivity(), gid, null, null, 1, "1", null, 0, -Integer.MAX_VALUE, null, null, null, null,1);
	}

	/**
	 * 安装查找的用户昵称进行排序
	 * 
	 * @param list
	 * @return
	 */
	private List<UserVo> sortUserByNickname(List<UserVo> list) {
		try {
			Collections.sort(list, new Comparator<UserVo>() {

				@Override
				public int compare(UserVo o1, UserVo o2) {
					try {
						if (o1.getUsername() != null && o2.getUsername() != null) {
							String headChar = PinyinUtil.getPinYinHeadChar(o1.getUsername());
							char firstChar = headChar.toUpperCase().charAt(0);
							String headChar1 = PinyinUtil.getPinYinHeadChar(o2.getUsername());
							char firstChar1 = headChar1.toUpperCase().charAt(0);
							return String.valueOf(firstChar).compareTo(String.valueOf(firstChar1));
						} else {
							return 0;
						}
					} catch (Exception e) {
						LogUtil.e(TAG, e.getMessage());
						return 0;
					}
				}

			});
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage());
		}
		return list;
	}

	/**
	 * 安装查找的用户昵称进行排序
	 * 
	 * @param list
	 * @return
	 */
	private List<UserObject> sortUserByNickname2(List<UserObject> list) {
		try {
			Collections.sort(list, new Comparator<UserObject>() {

				@Override
				public int compare(UserObject o1, UserObject o2) {
					try {
						if (o1.getNickname() != null && o2.getNickname() != null) {
							String headChar = PinyinUtil.getPinYinHeadChar(o1.getNickname());
							char firstChar = headChar.toUpperCase().charAt(0);
							String headChar1 = PinyinUtil.getPinYinHeadChar(o2.getNickname());
							char firstChar1 = headChar1.toUpperCase().charAt(0);
							return String.valueOf(firstChar).compareTo(String.valueOf(firstChar1));
						} else {
							return 0;
						}
					} catch (Exception e) {
						LogUtil.e(TAG, e.getMessage());
						return 0;
					}
				}

			});
		} catch (Exception e) {
			LogUtil.e(TAG, e.getMessage());
		}
		return list;
	}

	/**
	 * 解析用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseUserList(List<UserVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			UserVo vo = list.get(i);
			map.put("avatar", vo.getAvatar());
			map.put("nickname", vo.getUsername());
			map.put("desc", "");
			map.put("sex", vo.getSex());
			map.put("age", vo.getAge());
			map.put("uid", vo.getUserid());
			map.put("serial", vo.getSerial());
			map.put("rel", vo.getRelPositive());
			map.put("news", vo.getMood());
			map.put("grade", vo.getGrade());
			map.put("remarkname", vo.getRemarksName());
			tmplist.add(map);
		}
		return tmplist;
	}

	/**
	 * 解析关注相同贴吧的用户数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseUserListBygame(List<UserObject> list) {
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
			map.put("serial", vo.getSortId());
			map.put("rel", vo.getRel());
			map.put("news", vo.getMood());
			map.put("grade", vo.getGrade());
			tmplist.add(map);
		}
		return tmplist;
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

	/**
	 * 在这个页面销毁的时候  
	 * 把适配器里面的用户的距离和时间保存到文件中
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		saveData();
	}


	/**
	 * 同步公会成员信息
	 */
	private void saveData(){
		//第二保存到文件
		try {
			FileUtils.delFile(SystemContext.getInstance().getContext(), SystemConfig.DISTANCE_AND_LASTLOGIN_FILENAME);
			FileUtils.writeFile(SystemContext.getInstance().getContext(), SystemConfig.DISTANCE_AND_LASTLOGIN_FILENAME, dis,this.getActivity().MODE_PRIVATE);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				FileUtils.writeFile(SystemContext.getInstance().getContext(), SystemConfig.DISTANCE_AND_LASTLOGIN_FILENAME, dis,this.getActivity().MODE_PRIVATE);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
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
			fansTab.setSelected(false);
			groupTab.setSelected(false);
			viewPager.setCurrentItem(1);
		} else if (v.getId() == R.id.fansTab) {
			followTab.setSelected(false);
			fansTab.setSelected(true);
			groupTab.setSelected(false);
			viewPager.setCurrentItem(2);
		} else if (v.getId() == R.id.groupTab) {
			followTab.setSelected(false);
			fansTab.setSelected(false);
			groupTab.setSelected(true);
			viewPager.setCurrentItem(0);
		} else if (v == followView.typeSelect) {
			followView.typeSelect.setEnabled(false);
			UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_USER_BARGROUP_CLICK, null, null, null, null, null);
			if (followView.gid == 0) {
				if(AdaptiveAppContext.getInstance().getAppConfig().getMode() == 1){
					//为1 的情况 是表示游伴
					initGameContent();
				}else if(AdaptiveAppContext.getInstance().getAppConfig().getMode() == 2){
					//如果为2，表示的是单攻略
					onClickUserSelect(followView.typeSelect);
				}
			} else {
				onClickUserSelect(followView.typeSelect);
			}
		}
	}
}
