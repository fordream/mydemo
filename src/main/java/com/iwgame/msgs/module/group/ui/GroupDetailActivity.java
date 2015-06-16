/**      
 * GroupDetailActivity.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.UserActionDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheCallBack;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.group.adapter.GroupdDetailUserAdapter;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.module.postbar.ui.GameTopicListActivity;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.GroupUserListActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.ErrorCode;
import com.iwgame.msgs.utils.AppUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.UserActionVo;
import com.iwgame.msgs.widget.MyGridView;
import com.iwgame.msgs.widget.ShareImageView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.AppUtils;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;


/**
 * @ClassName: GroupDetailActivity
 * @Description: 公会详细资料
 * @author 王卫
 * @date 2013-10-24 下午07:44:52
 * @Version 1.0
 * 
 */
public class GroupDetailActivity extends BaseActivity implements OnItemClickListener, OnClickListener {
	private static final String TAG = "GroupDetailActivity";
	// 公会ID
	private long grid;
	// 关系
	private int rel;
	// 功能按钮
	private LinearLayout functionBtn;
	// 功能按钮
	private TextView functionTxt;
	// 详细信息
	private GroupVo groupVo;
	// 头像
	private ImageView avatar;
	// 公会名称
	private TextView gname;
	// 描述
	private TextView groupDesc;
	// 公告
	private TextView notice;
	// 会长
	private TextView pres;
	// 编辑公会资料条目
	private LinearLayout editItem;
	// 会长条目
	private LinearLayout presItem;
	// ID
	private TextView id;
	// 成员个数
	private TextView gcount;
	// 成员个数连接标示符
	private ImageView gcountLink;
	// 成员个数条目
	private LinearLayout gcountItem;
	// 贴吧标签
	private TextView gameTag;
	// 贴吧标签图标
	private ImageView gameIcon;
	// 贴吧条目
	private LinearLayout gameTagItem;
	// 创建日期
	private TextView ctime;
	// 贴吧
	private GameVo gameVo;
	private LinearLayout userView;
	private List<UserItemObj> userData = null;
	private int pageNo = 0;
	private int end = 0;
	private TextView moreBtn;
	private TextView grade;
	private LinearLayout gradeItem;
	//是不是由聊天窗口打开
	private boolean isFromChatActivityOpen = false;
	private LinearLayout groupCardItem;// 公会名片的item
	private TextView groupCard;// 显示公会名片的textview
	private GroupUserRelVo currentRel;
	private ImageView rightImg;
	private LayoutInflater inflater;//用于分享
	private CustomProgressDialog customProgressDialog;
	private GroupDao dao;
	private GameDao gameDao;
	
	
	/**
	 * 界面刚启动的时候，
	 * 执行下面的这个生命周期方法
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			grid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
			isFromChatActivityOpen = bundle.getBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ISFROMCHATACTIVITYOPEN, false);
		}
		// 显示左边
		setLeftVisible(true);
		setRightVisible(true);
		titleTxt.setText("公会资料");
		rightImg = new ShareImageView(this);
		
		rightImg.setOnClickListener(this);
		addRightView(rightImg);
		// 设置内容UI
		customProgressDialog = CustomProgressDialog.createDialog(this, true);
	    dao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
	    gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		LinearLayout contentView = (LinearLayout) findViewById(R.id.contentView);
		customProgressDialog.show();
		contentView.removeAllViews();
		RelativeLayout detailView = (RelativeLayout) View.inflate(this, R.layout.group_detail_info, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(detailView, params);
		groupCardItem = (LinearLayout) contentView.findViewById(R.id.groupcardItem);
		groupCard = (TextView) contentView.findViewById(R.id.groupcard);
		// 设置UI元素
		avatar = (ImageView) contentView.findViewById(R.id.icon);
		groupDesc = (TextView) contentView.findViewById(R.id.groupDesc);
		gname = (TextView) contentView.findViewById(R.id.gname);
		notice = (TextView) contentView.findViewById(R.id.notice);
		grade = (TextView) contentView.findViewById(R.id.grade);
		gradeItem = (LinearLayout) contentView.findViewById(R.id.gradeItem);
		gradeItem.setOnClickListener(this);
		// 可选项
		pres = (TextView) contentView.findViewById(R.id.pres);
		presItem = (LinearLayout) contentView.findViewById(R.id.presItem);
		presItem.setOnClickListener(this);
		editItem = (LinearLayout) contentView.findViewById(R.id.editItem);
		editItem.setOnClickListener(this);
		id = (TextView) contentView.findViewById(R.id.id);
		gcount = (TextView) contentView.findViewById(R.id.gcount);
		gcountLink = (ImageView) contentView.findViewById(R.id.gcountLink);
		gcountItem = (LinearLayout) contentView.findViewById(R.id.gcountItem);
		gcountItem.setOnClickListener(this);
		gameTag = (TextView) contentView.findViewById(R.id.gameTag);
		gameIcon = (ImageView) contentView.findViewById(R.id.gameIcon);
		gameTagItem = (LinearLayout) contentView.findViewById(R.id.gameTagItem);
		gameTagItem.setOnClickListener(this);
		ctime = (TextView) contentView.findViewById(R.id.ctime);
		//底部功能按钮
		functionBtn = (LinearLayout) findViewById(R.id.functionBtn);
		functionTxt = (TextView) findViewById(R.id.functionTxt);
		functionBtn.setOnClickListener(this);
		// 公会成员
		userView = (LinearLayout) findViewById(R.id.userView);
		moreBtn = (TextView) findViewById(R.id.moreBtn);
		moreBtn.setOnClickListener(this);
		groupVo = dao.findGroupByGrid(grid);
		if(groupVo != null) rel = groupVo.getRelWithGroup();
		else rel = 0;
		if (rel != 0){
			if (rel == GroupUserRelVo.REL_ADMIN || rel == GroupUserRelVo.REL_NORMALADMIN || rel == GroupUserRelVo.REL_USER) {
				groupCardItem.setVisibility(View.VISIBLE);
				//如果我在公会里面，就要去获取自己是否有公会名片
				getMyGroupCard();
			} else {
				groupCardItem.setVisibility(View.GONE);
			}
		} else {
			groupCardItem.setVisibility(View.GONE);
		}
		groupVo = dao.findGroupByGrid(grid);
		if(groupVo != null) rel = groupVo.getRelWithGroup();
		else rel = 0;
		if (rel == 0) {// 没有关系
			gcountLink.setVisibility(View.INVISIBLE);
			gcountItem.setOnClickListener(null);
			gcountItem.setClickable(false);
			Drawable sexdraw = getResources().getDrawable(R.drawable.group_detail_chat_menu_selector);
			sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
			functionTxt.setCompoundDrawables(sexdraw, null, null, null);
			functionTxt.setText("加入公会");
			editItem.setVisibility(View.INVISIBLE);
		} else if (rel == 1) {// 普通会员
			functionTxt.setText("进入聊天室");
			editItem.setVisibility(View.INVISIBLE);
			Drawable sexdraw = getResources().getDrawable(R.drawable.group_detail_join_group_selector);
			sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
			functionTxt.setCompoundDrawables(sexdraw, null, null, null);
		} else if (rel == 2) {// 管理员
			functionTxt.setText("进入聊天室");
			editItem.setVisibility(View.INVISIBLE);
			Drawable sexdraw = getResources().getDrawable(R.drawable.group_detail_join_group_selector);
			sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
			functionTxt.setCompoundDrawables(sexdraw, null, null, null);
		} else if (rel == 3) {// 会长
			functionTxt.setText("进入聊天室");
			editItem.setVisibility(View.VISIBLE);
			Drawable sexdraw = getResources().getDrawable(R.drawable.group_detail_join_group_selector);
			sexdraw.setBounds(0, 0, sexdraw.getMinimumWidth(), sexdraw.getMinimumHeight());
			functionTxt.setCompoundDrawables(sexdraw, null, null, null);
		}
		getGroupUserData();
		setListener();
	}

	
	/**
	 * 添加右边菜单
	 * @param v
	 */
	protected void addRightView(View v){
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.width = DisplayUtil.dip2px(this, 57);
		params.height = DisplayUtil.dip2px(this, 46);
		params.setMargins(0, 0, DisplayUtil.dip2px(this, 9), 0);
		rightView.setLayoutParams(params);
		rightView.addView(v, params);
	}
	
	
	
	/**
	 * 生命周期方法
	 * 每次都会执行
	 * 同步公会的实体
	 */
	@Override
	protected void onResume() {
		super.onResume();
		functionBtn.setEnabled(true);
		functionBtn.setClickable(true);
		if (SystemContext.getInstance().getExtUserVo() != null)
			initialize();
	}

	
	
	/**
	 * 获取自己的公会名片显示
	 */
	private void getMyGroupCard() {
		// 获取到我与公会之间的关系
		GroupUserRelVo groupUserRelVo = ProxyFactory.getInstance().getGroupProxy()
				.getRel(grid, SystemContext.getInstance().getExtUserVo().getUserid());
		if(groupUserRelVo != null){
			groupCard.setText(groupUserRelVo.getRemark());
		}else{
			ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GROUP_USER, grid, new SyncCallBack() {

				@Override
				public void onSuccess(Object result) {
					// 获取到我与公会之间的关系
					GroupUserRelVo groupUserRelVo = ProxyFactory.getInstance().getGroupProxy()
							.getRel(grid, SystemContext.getInstance().getExtUserVo().getUserid());
					if(groupUserRelVo != null)groupCard.setText(groupUserRelVo.getRemark());
				}

				@Override
				public void onFailure(Integer result) {
				}
			});
		}
	}

	/**
	 * 当点击我的群名片的时候 跳转到另外一个页面 编辑群名片
	 */
	private void setListener() {
		groupCardItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(GroupDetailActivity.this, EditGroupCardActivity.class);
				intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
				intent.putExtra(SystemConfig.GROUP_CARD_CONTENT_KEY, groupCard.getText().toString());
				GroupDetailActivity.this.startActivityForResult(intent, SystemConfig.SYSTEMCONFIG_GROUP_CARD_NUM);
			}
		});
	}

	/**
	 * 初始化
	 */
	private void initialize() {
		controBtnEnable(false);
		groupVo = dao.findGroupByGrid(grid);
		if(groupVo == null){
			GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
			GroupVo gvo = groupDao.findGroupByGrid(grid);
			// 获取公会详情
			ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
			ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
			cdp.setId(grid);
			cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
			p.addParam(cdp.build());
			ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(new ProxyCallBack<List<GroupVo>>() {

				@Override
				public void onSuccess(List<GroupVo> result) {
					if(result != null && result.size() > 0){
						groupVo = result.get(0);
						if (groupVo != null && groupVo.getGid() > 0)
							getGameInfo(groupVo, groupVo.getGid());
						else{
							controBtnEnable(false);
							customProgressDialog.dismiss();
						}
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					customProgressDialog.dismiss();
					controBtnEnable(false);
					LogUtil.e(TAG, "获取公会详情失败:" + result);
				}
			}, this, p.build(), 5, null);
		}else{
			getGameInfo(groupVo, groupVo.getGid());
		}
	}

	/**
	 * 获取公会里面的
	 * 用户的头像信息
	 */
	private void getGroupUserData() {
		pageNo = 0;
		ProxyCallBack<List<UserItemObj>> callback = new ProxyCallBack<List<UserItemObj>>() {
			@Override
			public void onSuccess(List<UserItemObj> result) {
				if (result != null && result.size() > 0) {
					gcount.setText(result.size() + "");
					userData = result;
					addNextPageData();
				}
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
			}
		};
		ProxyFactory.getInstance().getGroupProxy().getGroupUsers(callback, this, grid, 0);
	}

	/**
	 * 添加新的一页数据
	 */
	private void addNextPageData() {
		// 设置LIST数据
		MyGridView userGridView = new MyGridView(this);
		userGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		userGridView.setAdapter(new GroupdDetailUserAdapter(GroupDetailActivity.this, new ArrayList<UserItemObj>()));
		userGridView.setOnItemClickListener(GroupDetailActivity.this);
		userGridView.setNumColumns(4);
		GroupdDetailUserAdapter adapter = (GroupdDetailUserAdapter) userGridView.getAdapter();
		if (userData != null && end < userData.size()) {
			pageNo++;
			int start = (pageNo - 1) * 8;
			end = pageNo * 8;
			if (end >= userData.size()) {
				end = userData.size();
				moreBtn.setVisibility(View.GONE);
			} else {
				moreBtn.setVisibility(View.VISIBLE);
			}
			List<UserItemObj> pageData = userData.subList(start, end);
			adapter.getData().addAll(pageData);
			adapter.notifyDataSetChanged();
		} else {
			ToastUtil.showToast(this, "没有更多公会成员数据");
		}
		userView.addView(userGridView);
	}

	/**
	 * 获取贴吧信息
	 * 
	 * @param gid
	 */
	private void getGameInfo(final GroupVo grVo, long gid) {
		gameVo = gameDao.getGameByGameId(gid);
		if(gameVo == null){
			setUI(grVo, gameVo);	
		}else{
			ProxyFactory.getInstance().getGameProxy().getGameInfo(new ProxyCallBack<GameVo>() {

				@Override
				public void onSuccess(GameVo result) {
					gameVo = result;
					setUI(grVo, result);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					setUI(grVo, null);
					LogUtil.e(TAG, "获取公会详情失败:" + result);
				}
			}, this, gid);
		}
	}

	/**
	 * 根据我和公会
	 * 的关系设置UI元素
	 * @param rel
	 */
	private void setUI(GroupVo grVo, GameVo gVo) {
		if (grVo != null) {
			gname.setText(grVo.getName());
			groupDesc.setText(grVo.getUndesc());
			notice.setText(grVo.getNotice());
			id.setText(String.valueOf(grVo.getSerial()));
			pres.setText(grVo.getPresidentName());
			ctime.setText(DateUtil.Date2String2(new Date(grVo.getCreatTime())));
			grade.setText("LV" + grVo.getGrade());
			if (gVo != null && gVo.getGamename() != null) {
				gameTag.setText(gVo.getGamename());
			}
			if (gVo != null && gVo.getGamelogo() != null) {
				new ImageLoader().loadRes(ResUtil.getSmallRelUrl(gVo.getGamelogo()), 0, gameIcon, R.drawable.common_default_icon);
			}
			// 显示头像
			ImageViewUtil.showImage(avatar, grVo.getAvatar(), R.drawable.common_default_icon);
			contentView.setVisibility(View.VISIBLE);
			customProgressDialog.dismiss();
		} else {
			return;
		}
		controBtnEnable(true);
		customProgressDialog.dismiss();
	}

	/**
	 * 控制界面上面的
	 * 按钮是否可以点击
	 */
	private void controBtnEnable(boolean flag){
		if(flag){//当数据返回来的时候，设置成为可以点击的
			gradeItem.setClickable(true);
			functionBtn.setClickable(true);
			presItem.setClickable(true);
			gameTagItem.setClickable(true);
			gcountItem.setClickable(true);
			editItem.setClickable(true);
			moreBtn.setClickable(true);
			rightImg.setClickable(true);
		}else{
			gradeItem.setClickable(false);
			functionBtn.setClickable(false);
			presItem.setClickable(false);
			gameTagItem.setClickable(false);
			gcountItem.setClickable(false);
			editItem.setClickable(false);
			moreBtn.setClickable(false);
			rightImg.setClickable(false);
		}
	}
	
	
	/**
	 * onclick方法 
	 * 当点击界面上面的按钮的
	 * 时候会执行下面的这些方法 
	 */
	@Override
	public void onClick(final View v) {
		if (v.getId() == gradeItem.getId()) {
			Intent intent = new Intent(this, GroupGradePolicyActivity.class);
			intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
			this.startActivity(intent);
		} else if (v.getId() == R.id.functionBtn) {
			v.setEnabled(false);
			v.setClickable(false);
			// 功能按钮
			if (rel == 0) {
					ProxyFactory.getInstance().getCache().getData(Cache.DATA_TYPE_CONTRANCT_GROUP, new CacheCallBack() {

						@Override
						public void onBack(Object result) {
							List<GroupVo> list = (List<GroupVo>) result;
							joinGroupCheck(list, v);
						}
					});
			} else {
				// 跳转到聊天界面
				if (isFromChatActivityOpen == true) {
					// 从聊天窗口打开
					setResult(SystemConfig.ACTIVITY_RESULTCODE_DISTROY_CHATLIST);
					this.finish();
				} else {
					// 群聊
					Intent intent = new Intent(GroupDetailActivity.this, GroupChatFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, groupVo.getGrid());
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					startActivity(intent);
				}
			}
		} else if (v.getId() == R.id.presItem) {
			if (groupVo != null) {
				if (((Long) groupVo.getPresidentId()).equals(SystemContext.getInstance().getExtUserVo().getUserid())) {
					ToastUtil.showToast(this, getString(R.string.check_information));
				} else {
					Intent intent = new Intent(this, UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, (Long) groupVo.getPresidentId());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		} else if (v.getId() == R.id.gameTagItem) {
			AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
			if(gameVo == null) return;
			if (appconfig != null && appconfig.isRecbarmsg() && gameVo.getGameid() != appconfig.getGameId()) {
				AppUtil.openGame(this, gameVo.getGameid(), GameTopicListActivity.class.getName(), this.getResources().getString(R.string.postbar_show_game_tip_for_youban_uninstall));
			} else if (gameVo != null) {
					Intent intent = new Intent(this, GameTopicListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gameVo.getGameid());
					bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, gameVo.getGamename());
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					startActivity(intent);
				}
		} else if (v.getId() == R.id.gcountItem) {
			Intent intent = new Intent(GroupDetailActivity.this, GroupUserListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
			bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GNAME, gname.getText().toString());
			if (currentRel != null) {
				if (currentRel.getRel() == GroupUserRelVo.REL_ADMIN || currentRel.getRel() == GroupUserRelVo.REL_NORMALADMIN) {
					bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_STOP_SAY_WORDS, true);
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
				}
			}
			intent.putExtras(bundle);
			this.startActivity(intent);
		} else if (v.getId() == R.id.editItem) {
			if (rel == 3) {
				Intent intent = new Intent(this, EditDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		} else if (v.getId() == R.id.moreBtn) {
			addNextPageData();
		} else if (v == rightImg) {
			share();
		}
	}

	/**
	 * 加入公会时事先 判断
	 * 一方面是判断我当前加入的公会数
	 * 是否已经达到了上限
	 */
	private void joinGroupCheck(List<GroupVo> list, final View v) {
		if (groupVo != null) {
			if (groupVo.getNeedValidate() == 0) {
				functionBtn.setEnabled(false);
				functionBtn.setClickable(false);
				final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
				dialog.show();

				ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {

					@Override
					public void onBack(BDLocation bdLocation) {
						joinGroup(dialog);
					}
				});

			} else if (groupVo.getNeedValidate() == 1) {
				UserActionVo userAcionVo = DaoFactory.getDaoFactory().getUserActionDao(this)
						.findUserAction(grid, UserActionDao.ENTITY_TYPE_GROUP, UserActionDao.OP_TYPE_GROUP_APPLEY);
				int applyCount = 0;
				if (userAcionVo != null) {
					applyCount = userAcionVo.getCount();
				}
				int jam = SystemContext.getInstance().getGAM();
				if (applyCount < jam) {
					Intent intent = new Intent(GroupDetailActivity.this, GroupApplyActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
					intent.putExtras(bundle);
					GroupDetailActivity.this.startActivity(intent);
				} else {
					String tip = String.format(getString(R.string.group_apply_count_max), jam);
					ToastUtil.showToast(this, tip);
				}
				functionBtn.setEnabled(true);
				functionBtn.setClickable(true);
			} else {
				functionBtn.setEnabled(true);
				functionBtn.setClickable(true);
				LogUtil.w(TAG, "判断加入工会是否需要验证属性错误");
				return;
			}
		} else {
			functionBtn.setEnabled(true);
			functionBtn.setClickable(true);
		}
	}

	/**
	 * 当点击分享按钮的时候，
	 * 执行下面的这个方法
	 */
	private void share() {
		if (groupVo == null)
			return;
		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);// 类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_GROUP);// 类型为公会
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_GROUP_DETIAL);// 内部分享类型
		shareDate.setTargetId(groupVo.getGrid());// 公会id(目标ID)
		shareDate.setTargerSerialId(groupVo.getSerial());// 公会Serialid(目标SerialID)
		shareDate.setTargetName(groupVo.getName());// 公会名称（目标名称）
		// 获得图片地址
		shareDate.setImageUrl(ResUtil.getSmallRelUrl(groupVo.getAvatar()));
		shareDate.setImagePath(groupVo.getAvatar());
		ShareCallbackListener listener = new ShareCallbackListener() {

			@Override
			public void doSuccess(String plamType) {
				//分享公会资料
				ShareTaskUtil.makeShareTask(GroupDetailActivity.this, TAG, groupVo.getGrid(), MsgsConstants.OT_GROUP, MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
			}

			@Override
			public void doFail() {

			}
		};
		ShareManager.getInstance().share(this, inflater, groupVo, shareDate, listener);
	}

	/**
	 * 获取位置信息
	 *  然后在加入公会
	 */
	private void joinGroup(final CustomProgressDialog dialog) {
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					ToastUtil.showToast(GroupDetailActivity.this, getString(R.string.group_join_success));
					functionBtn.setEnabled(false);
					UMUtil.sendEvent(GroupDetailActivity.this, UMConfig.MSGS_EVENT_GROUP_ADD, null, null, grid + "", groupVo.getName(), null);
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GROUP, new SyncCallBack() {

						@Override
						public void onSuccess(Object result) {
							if(!getTopActivity().equals(GroupDetailActivity.class.getName())) return;
								// 群聊
								Intent intent = new Intent(GroupDetailActivity.this, GroupChatFragmentActivity.class);
								Bundle bundle = new Bundle();
								bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, groupVo.getGrid());
								intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
								startActivity(intent);
								GroupDetailActivity.this.finish();
						}

						@Override
						public void onFailure(Integer result) {
							functionBtn.setEnabled(true);
							functionBtn.setClickable(true);
							ErrorCodeUtil.handleErrorCode(GroupDetailActivity.this, result, null);
						}
					});
					break;

				default:
					functionBtn.setEnabled(true);
					functionBtn.setClickable(true);
					ErrorCodeUtil.handleErrorCode(GroupDetailActivity.this, result, null);
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				functionBtn.setEnabled(true);
				functionBtn.setClickable(true);
				LogUtil.e(TAG, "用户行为失败:" + result);
				switch (result) {
				case ErrorCode.EC_MSGS_GROUP_MEMBER_EXCEED_VALUE:
					ToastUtil.showToast(GroupDetailActivity.this, getString(R.string.group_join_group_members_over_count));
					break;
				case ErrorCode.EC_MSGS_OVER_COUNT_VALUE:
					ToastUtil.showToast(GroupDetailActivity.this, getString(R.string.group_join_group_over_count));
					break;
				case ErrorCode.EC_MSGS_GROUP_ALREADY_IN_VALUE:
					ToastUtil.showToast(GroupDetailActivity.this, getString(R.string.group_join_group_already_in));
					break;
				case ErrorCode.EC_MSGS_USER_ROLE_NO_VALUE:
					ToastUtil.showToast(GroupDetailActivity.this, getString(R.string.group_join_group_needrole));
					break;
				default:
					ErrorCodeUtil.handleErrorCode(GroupDetailActivity.this, result, resultMsg);
					break;
				}
				dialog.dismiss();
			}
		}, this, grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_AGREE_JOIN_GROUP, null, null, AppUtils.getLocalAppVersionCode(this)+"");
	}

	/**
	 * 获取到栈顶的activity
	 * 当加入公会成功的时候
	 * 如果 我 当前的页面 不是在公会资料页面
	 * 则不需要跳转到聊天室页面
	 * @return
	 */
	private String getTopActivity(){
		ActivityManager manager = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE) ;
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
		if(runningTaskInfos != null)
			return runningTaskInfos.get(0).topActivity.getClassName();
		else
			return null ;
	}

	
	
	/**
	 * 当点击底部的functionbtn按钮的时候
	 * 如果是游客的话  会跳转到绑定手机号的页面
	 * 弹出绑定手机的对话框
	 * 
	 * @param actionName
	 */
	private void createBundPhoneDialog() {
		Intent intent = new Intent(GroupDetailActivity.this, BundPhoneActivity.class);
		GroupDetailActivity.this.startActivity(intent);
	}


	/**
	 * 这个方法是在 调用了startactivityforresult
	 * 的时候返回来会执行下面的这个方法 
	 * 第二个是点击的了 修改公会名片成功后
	 * 返回来会执行的分支 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SystemConfig.ACTIVITY_REQUESTCODE_DISTROY:
			if (resultCode == SystemConfig.ACTIVITY_RESULTCODE_DISTROY) {
				GroupDetailActivity.this.setResult(SystemConfig.ACTIVITY_RESULTCODE_DISTROY);
				this.finish();
			}
			break;
		case SystemConfig.SYSTEMCONFIG_GROUP_CARD_NUM:
			if (resultCode == 20) {
				String groupCardContent = data.getStringExtra(SystemConfig.GROUP_CARD_CONTENT_KEY);
				groupCard.setText(groupCardContent);
			}
			break;
		}
	}

 
	/**
	 * 当点击用户的头像的时候
	 * 跳转到用户的详情页面
	 * 如果点击的是我自己的头像，则提示用户
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UserItemObj vo = ((GroupdDetailUserAdapter) parent.getAdapter()).getData().get(position);
		Long uid = vo.getUid();
		if (uid != null) {
			if (!uid.equals(SystemContext.getInstance().getExtUserVo().getUserid())) {
				if(functionBtn.isClickable()){
					Intent intent = new Intent(parent.getContext(), UserDetailInfoActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, Long.valueOf(uid.toString()));
					if (currentRel != null) {
						if (currentRel.getRel() == GroupUserRelVo.REL_ADMIN || currentRel.getRel() == GroupUserRelVo.REL_NORMALADMIN) {
							bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_STOP_SAY_WORDS, true);
						}
					}
					bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GROUP_CARD, userData.get(position).getRemark());
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
					GroupDao dao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
					GroupVo gvo = dao.findGroupByGrid(grid);
					if(gvo != null && gvo.getRelWithGroup() > 0)
						bundle.putBoolean(SystemConfig.IF_NOT_SHOW_GROUP_CARD, true);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			} else {
				ToastUtil.showToast(this, "查看自己信息，请进入我的资料页");
			}
		}
	}
}
