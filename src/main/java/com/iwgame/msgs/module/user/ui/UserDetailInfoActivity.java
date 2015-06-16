/**      
 * UserDetailInfoActivity.java Create on 2013-8-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.BaseSuperActivity;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.module.game.adapter.CommonGameAdapter;
import com.iwgame.msgs.module.game.ui.GameListActivity;
import com.iwgame.msgs.module.group.adapter.GroupAdapter2;
import com.iwgame.msgs.module.group.ui.GroupListActivity;
import com.iwgame.msgs.module.play.ui.PlayDetailsActivity;
import com.iwgame.msgs.module.postbar.ui.UserTopicListActivity;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.GameRole;
import com.iwgame.msgs.proto.Msgs.GameServerEntry;
import com.iwgame.msgs.proto.Msgs.PlayInfo;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.ErrorCode;
import com.iwgame.msgs.proto.Msgs.UserRoleData.RoleAttr;
import com.iwgame.msgs.utils.AgeUtil;
import com.iwgame.msgs.utils.BuildVoUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.ImageViewUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.utils.UserUtil;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.CircleImageView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.popwindow.MsgsPopTextItem;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow.OnClickPopItemListener;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * 用户详细信息界面
 * 
 * @ClassName: UserDetailInfoActivity
 * @Description: TODO(...)
 * @author zhangjianchuan
 * @date 2014-10-11 上午9:07:50
 * @Version 1.0
 * 
 */
public class UserDetailInfoActivity extends BaseSuperActivity implements OnClickListener {

	private static final String TAG = "UserDetailInfoActivity";
	// 用户ID
	private long uid;
	private ExtUserVo extUserVo;
	// 设置菜单按钮
	private Button rightMenu;
	// 是否由聊天窗口打开
	private boolean isFromChatActivityOpen = false;
	// 弹出框
	private Dialog dialog;
	private EditText updateRemark;
	private TextView usedName;
	private Button cancelBtn;
	private Button commitBtn;
	private boolean flag;
	// 用户详情界面的控件
	private ImageView avatar;// 用户的头像
	private TextView nickName;// 用户昵称
	private TextView age;// 用户年龄
	private TextView city;// 用户的城市
	private TextView signName;// 签名
	private TextView userId;// 用户的id
	private RelativeLayout postItem;// 帖子的item
	private RelativeLayout gameItem;// 贴吧的item
	private RelativeLayout groupItem;// 公会的item
	private RelativeLayout followItem;// 关注的item
	private RelativeLayout fansItem;// 粉丝的item
	private ImageView addPhoteBtn;// 添加照片的button
	private TextView postNum;
	private TextView gameNum;
	private TextView groupNum;
	private TextView followNum;
	private TextView fansNum;
	private TextView gradeNum;
	private TextView pointNum;
	private TextView gameTime;
	private TextView loveGame;
	private TextView myJob;
	public HorizontalScrollView horizontalScrollView;
	public LinearLayout photoContent;// 显示相册的父容器
	private int photoCount; // 相册数
	private LinearLayout blacklistBottom;
	private LinearLayout follow;
	private LinearLayout talk;
	private LinearLayout commonBottom;
	private TextView followImg;
	private LinearLayout bottom;
	public TextView tip;// 相册的提示
	private boolean flagStopSay;
	private long groupId;
	private String groupCard;
	private LinearLayout groupCardItem;
	private TextView showMyGroupCard;
	private boolean showGroupcard;
	private LayoutInflater inflater;
	private Button shareBtn;
	private Dialog dialog1;
	private Dialog dialog2;
	private Dialog dialog3;
	private boolean isfromFollowList;// 标记是不是从通讯录的关注页面点击跳转过来的，如果是要显示备注 如果不是
										// 不显示备注
	private View fengexian;
	private ImageView groupcardFenge;// 公会名片下面的分割线

	private List<UserRoleVo> list = new ArrayList<UserRoleVo>();
	private LinearLayout otherGameRole;
	private LinearLayout gameLayout;
	private ImageView roleLine;
	private TextView moreGame;
	private LinearLayout otherPlay;
	private LinearLayout otherPlayContent;
	private ImageView playLine;
	private LinearLayout playItem;
	private LinearLayout roleItem;
	private static final int PLAY_STATUS =2;
	public View getFengexian() {
		return fengexian;
	}

	public void setFengexian(View fengexian) {
		this.fengexian = fengexian;
	}

	/**
	 * 首先要在oncreate方法里面 初始化界面 以及初始化控件的一些操作
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_user_detail_info);
		initialize();
		setView();
	}

	/**
	 * 初始化界面 将界面的一些控件初始化
	 */
	private void initialize() {
		inflater = LayoutInflater.from(this);
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		flag = bundle.getBoolean(SystemConfig.IS_FROM_FOLLOWS_LISTVIEW, false);
		showGroupcard = bundle.getBoolean(SystemConfig.IF_NOT_SHOW_GROUP_CARD);
		isfromFollowList = bundle.getBoolean(SystemConfig.IS_FROM_FOLLOWS_LISTVIEW, false);
		if (bundle != null) {
			uid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
			isFromChatActivityOpen = bundle.getBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ISFROMCHATACTIVITYOPEN, false);
			flagStopSay = bundle.getBoolean(SystemConfig.SHAREDPREFERENCES_STOP_SAY_WORDS, false);
			groupId = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);
			groupCard = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GROUP_CARD);
		}
		// 设置返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					back();
				}
			});
		}
		rightMenu = (Button) findViewById(R.id.rightMenu);
		shareBtn = (Button) findViewById(R.id.share);
		avatar = (ImageView) findViewById(R.id.icon);
		nickName = (TextView) findViewById(R.id.nickname);
		age = (TextView) findViewById(R.id.age);
		city = (TextView) findViewById(R.id.city);
		signName = (TextView) findViewById(R.id.mood);
		userId = (TextView) findViewById(R.id.serial);
		postItem = (RelativeLayout) findViewById(R.id.post_item);
		gameItem = (RelativeLayout) findViewById(R.id.game_item);
		groupItem = (RelativeLayout) findViewById(R.id.group_item);
		followItem = (RelativeLayout) findViewById(R.id.follow_item);
		fansItem = (RelativeLayout) findViewById(R.id.fans_item);
		addPhoteBtn = (ImageView) findViewById(R.id.addpotoBtn);
		postNum = (TextView) findViewById(R.id.post_num);
		gameNum = (TextView) findViewById(R.id.game_num);
		groupNum = (TextView) findViewById(R.id.group_num);
		followNum = (TextView) findViewById(R.id.follow_num);
		fansNum = (TextView) findViewById(R.id.fans_num);
		pointNum = (TextView) findViewById(R.id.point_num);
		gradeNum = (TextView) findViewById(R.id.grade_num);
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.detailInfo);
		photoContent = (LinearLayout) findViewById(R.id.photoContentView);
		gameTime = (TextView) findViewById(R.id.game_time);
		loveGame = (TextView) findViewById(R.id.love_game);
		myJob = (TextView) findViewById(R.id.my_job);
		blacklistBottom = (LinearLayout) findViewById(R.id.blacklistBottom);
		follow = (LinearLayout) findViewById(R.id.followBtn);
		talk = (LinearLayout) findViewById(R.id.talk);
		commonBottom = (LinearLayout) findViewById(R.id.commonBottom);
		followImg = (TextView) findViewById(R.id.followImg);
		bottom = (LinearLayout) findViewById(R.id.bottom);
		tip = (TextView) findViewById(R.id.tip);
		tip.setVisibility(View.GONE);
		fengexian = (View) findViewById(R.id.no_ablum_fengexian);
		groupCardItem = (LinearLayout) findViewById(R.id.my_group_card);
		showMyGroupCard = (TextView) findViewById(R.id.group_card);
		groupcardFenge = (ImageView) findViewById(R.id.group_card_fengexian);
		otherGameRole = (LinearLayout) findViewById(R.id.other_game_role);
		gameLayout = (LinearLayout) findViewById(R.id.other_user_role);
		roleItem =(LinearLayout)findViewById(R.id.other_role_item);
		roleLine = (ImageView) findViewById(R.id.role_Line);
		
		otherPlay =(LinearLayout)findViewById(R.id.user_play);
		otherPlayContent =(LinearLayout)findViewById(R.id.other_user_play);
		playLine=(ImageView)findViewById(R.id.play_Line);
		playItem=(LinearLayout)findViewById(R.id.user_play_item);
		
		if (showGroupcard) {
			groupCardItem.setVisibility(View.VISIBLE);
			groupcardFenge.setVisibility(View.VISIBLE);
			if (groupCard != null && !"".equals(groupCard)) {
				showMyGroupCard.setText(groupCard);
			} else {
				final GroupUserRelDao userRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(this);
				GroupUserRelVo vo = userRelDao.findUsers(groupId, uid);
				if (vo != null) {
					String remark = vo.getRemark();
					showMyGroupCard.setText(remark == null ? "" : remark);
				} else {
					// 同步公会成员
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_GROUP_USER, groupId, new SyncCallBack() {

						@Override
						public void onSuccess(Object result) {
							GroupUserRelVo vo = userRelDao.findUsers(groupId, uid);
							if (vo != null) {
								String remark = vo.getRemark();
								showMyGroupCard.setText(remark == null ? "" : remark);
							}
						}

						@Override
						public void onFailure(Integer result) {

						}
					});
				}
			}
		}
		// 关系条目添加点击事件监听
		blacklistBottom.setOnClickListener(this);
		follow.setOnClickListener(this);
		talk.setOnClickListener(this);
		postItem.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		gameItem.setOnClickListener(this);
		groupItem.setOnClickListener(this);
		followItem.setOnClickListener(this);
		fansItem.setOnClickListener(this);
		rightMenu.setOnClickListener(this);
		roleItem.setOnClickListener(this);
		playItem.setOnClickListener(this);
		rightMenu.setVisibility(View.VISIBLE);
		shareBtn.setVisibility(View.INVISIBLE);
		photoContent.setVisibility(View.VISIBLE);
		getPoint();
		UserUtil.showUserAlbum(this, this, UserDetailInfoActivity.this, uid, avatar, UserUtil.TYPE_USER, 1);
		if (SystemContext.getInstance().getExtUserVo() != null) {
			// 获取基本信息
			getUserDetailInfo(this, uid, false);
			if (setRoleItemVisible()) {
				getUserRoleDetailInfo(this, uid);
				getUserPlayInfo(this, uid);
			} else {
				otherGameRole.setVisibility(View.GONE);
				roleLine.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 设置角色模块是否显示
	 */
	public boolean setRoleItemVisible() {
		AppConfig config = AdaptiveAppContext.getInstance().getAppConfig();
		return config.isShowRoleList();
	}

	/**
	 * 当点击左上角的返回按钮的的时候 返回到上一个界面
	 */
	protected void back() {
		UserDetailInfoActivity.this.finish();
	}

	/**
	 * 获取用户的额外数据
	 * 
	 * @param userDetailInfoActivity
	 * @param uid2
	 * @param i
	 */
	private void getExtUserData(final Context context, long uid, int i) {
		ProxyFactory.getInstance().getUserProxy().getExtUserData(new ProxyCallBack<List<ExtUserVo>>() {

			@Override
			public void onSuccess(List<ExtUserVo> result) {

				if (result != null && result.size() > 0) {
					ExtUserVo vo = result.get(0);
					postNum.setText("" + vo.getPostCount());
					gameNum.setText("" + vo.getGameCount());
					groupNum.setText("" + vo.getGroupCount());
					fansNum.setText("" + vo.getFansCount());
					followNum.setText("" + vo.getFollowCount());
				} else {
					postNum.setText("0");
					gameNum.setText("0");
					groupNum.setText("0");
					fansNum.setText("0");
					followNum.setText("0");
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}
		}, context, uid + "", i);
	}
	/**
	 * 获取用户的陪玩数据
	 * @param context
	 * @param uid
	 */
	private void getUserPlayInfo(final Context context,final long uid){
		ProxyFactory.getInstance().getPlayProxy().searchCreatPlays(new ProxyCallBack<PagerVo<PlayInfo>>() {
			
			@Override
			public void onSuccess(PagerVo<PlayInfo> result) {
				if(result != null && result.getItems() != null && result.getItems().size() > 0){
					renderPlayData(result.getItems());
				}else{
					otherPlay.setVisibility(View.GONE);
					playLine.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				
			}
		}, context,uid,2, 0, 1);
	}
	private void renderPlayData(List<PlayInfo> list){
		if(list==null||list.size()<=0) return;
		otherPlayContent.removeAllViews();
		for(PlayInfo info : list){
			addPlayItem(info);
		}
	}
	private void addPlayItem(PlayInfo info){
		LinearLayout.LayoutParams params =new  LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		View view = View.inflate(UserDetailInfoActivity.this, R.layout.other_user_play_info,null);
		CircleImageView image = (CircleImageView)view.findViewById(R.id.play_icon);
		TextView playRemark =(TextView)view.findViewById(R.id.user_play_remark);
		TextView playServer =(TextView)view.findViewById(R.id.user_play_service);
		TextView moreServer =(TextView)view.findViewById(R.id.play_more_server);
		TextView playPay =(TextView)view.findViewById(R.id.user_play_pay);
		otherPlayContent.addView(view, params);
		if(info.getResourceid()!=null){
			ImageViewUtil.showImage(image, info.getResourceid(), R.drawable.postbar_thumbimg_default);
		}
		if(info.getRemark()!=null){
			playRemark.setText(info.getRemark());
		}
		if(info.getServername()!=null){
			playServer.setText(info.getServername());
		}
		if(playPay!=null){
			playPay.setText(info.getCost()+"U币");
		}
		if(info.getSids()!=null){
			if(!getMoreServerName(info).equals(""))
			moreServer.setText("("+getMoreServerName(info) + ")");
			moreServer.setTextColor(getResources().getColor(R.color.no_user_role_info));
		}
	}
	private String getMoreServerName(PlayInfo info){
		String name ="";
		if("0".equals(info.getSids())){
			name ="支持全服";
			return name;
		}
		List<GameServerEntry> list = new ArrayList<GameServerEntry>();
		list.addAll(info.getGameServerList());
		if(list==null||list.size()<=0) return name;
		for(int i = 0 ;i<list.size();i++){
			GameServerEntry entry = list.get(i);
			if(i==list.size()-1){
				name+=entry.getName();
			}else{
				name+=entry.getName()+",";
			}
		}
		return name;
	}
	/**
	 * 获得用户的角色信息
	 * 
	 * @param context
	 * @param uid
	 */
	public void getUserRoleDetailInfo(final Context context, final long uid) {
		UserDao userdao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		UserVo uvo = userdao.getUserById(uid);
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(uid);
		cdp.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserRoleInfo(new ProxyCallBack<List<UserRoleVo>>() {

			@Override
			public void onSuccess(List<UserRoleVo> result) {
				// TODO Auto-generated method stub
				if (result != null && result.size() > 0) {
					otherGameRole.setVisibility(View.VISIBLE);
					roleLine.setVisibility(View.VISIBLE);
					list.clear();
					list.addAll(result);
					addRoleView(list, uid);
				} else {
					roleLine.setVisibility(View.GONE);
					otherGameRole.setVisibility(View.GONE);
					return;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				otherGameRole.setVisibility(View.GONE);
				ToastUtil.showToast(context, getString(R.string.network_error));
			}
		}, context, p.build(), 23, null,(long)0,(long)0);
	}

	/**
	 * 设置用户角色数据
	 * 
	 * @param list
	 */
	private void addRoleView(List<UserRoleVo> list, final Long uid) {
		final boolean Temp = true;
		int size;
		if (list == null && list.size() <= 0)
			return;
		if (list.size() <= 3) {
			//moreGame.setVisibility(View.GONE);
			size = list.size();
		} else {
			size = 3;
			//moreGame.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < size; i++) {
			UserRoleVo vo = list.get(i);
			View view = LayoutInflater.from(UserDetailInfoActivity.this).inflate(R.layout.other_user_role_info, null);
			CircleImageView image = (CircleImageView) view.findViewById(R.id.game_icon);
			TextView roleName = (TextView) view.findViewById(R.id.user_role_name);
			TextView serviceName = (TextView) view.findViewById(R.id.user_role_service);
			TextView roleOtherInfo = (TextView) view.findViewById(R.id.user_role_grade);
			TextView gameRz = (TextView) view.findViewById(R.id.user_game_rz);
			StringBuilder builder = new StringBuilder();
			ImageViewUtil.showImage(image, vo.getAvatar(), R.drawable.postbar_thumbimg_default);
			List<RoleAttr> attrlist = vo.getList();
			for (RoleAttr attr : attrlist) {
				if (attr.getAttrtype() == MsgsConstants.GAME_ROLE_KEY_SERVER) {
					serviceName.setText(attr.getContent());
				} else if (attr.getAttrtype() == MsgsConstants.GAME_ROLE_KEY_USER) {
					roleName.setText(attr.getContent());
				} else {
					builder.append(attr.getContent() + " ");
				}
			}
			if (vo.getStatus() == 1) {
				gameRz.setText("已认证");
			} else if (vo.getStatus() == 0) {
				gameRz.setText("未认证");
			} else if (vo.getStatus() == 2) {
				gameRz.setText("未通过");
			} else if (vo.getStatus() == 3) {
				gameRz.setText("未验证");
			}
			roleOtherInfo.setText(builder.toString());
			gameLayout.addView(view);
		}
//		moreGame.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//				// TODO Auto-generated method stub
//				Intent intent = new Intent(UserDetailInfoActivity.this, UserRoleDetailActivity.class);
//				intent.putExtra("uid", uid);
//				intent.putExtra("other", Temp);
//				startActivity(intent);
//			}
//		});
	}

	/**
	 * 获取用户详细信息
	 * 
	 * @param context
	 * @param uid
	 * @param mode
	 */
	private void getUserDetailInfo(final Context context, final long uid, final boolean isOnResume) {
		if (uid > 0) {
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
			dialog.show();
			UserDao userdao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
			UserVo uvo = userdao.getUserById(uid);
			ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
			ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
			cdp.setId(uid);
			cdp.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
			p.addParam(cdp.build());
			ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(new ProxyCallBack<List<UserVo>>() {

				@Override
				public void onSuccess(List<UserVo> userVoresult) {
					if (userVoresult == null || userVoresult.size() <= 0)
						return;
					UserVo result = userVoresult.get(0);
					extUserVo = BuildVoUtil.buildExtUserVo(result);
					if (!isOnResume) {
						// 设置界面， 处理结果
						renderContent(extUserVo);
					} else {
						renderBottom(extUserVo);
					}
					if (extUserVo != null)
						gradeNum.setText("LV" + extUserVo.getGrade());
					dialog.dismiss();
					getExtUserData(UserDetailInfoActivity.this, uid, 0);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					ToastUtil.showToast(context, getString(R.string.network_error));
					dialog.dismiss();
					getExtUserData(UserDetailInfoActivity.this, uid, 0);
				}
			}, this, p.build(), 0, null);
		} else {
			if (!isOnResume) {
				renderContent(null);
				getExtUserData(this, uid, 0);
			} else {
				renderBottom(extUserVo);
				getExtUserData(this, uid, 0);
			}
		}
	}

	/**
	 * 修改底部按钮的而已 判断是显示黑名单的而已 还是显示关注的布局
	 * 
	 * @param vo
	 */
	private void renderBottom(ExtUserVo vo) {
		if (vo != null) {
			if (vo.getRelPositive() == 0) {
				shareBtn.setVisibility(View.VISIBLE);
				commonBottom.setVisibility(View.VISIBLE);
				blacklistBottom.setVisibility(View.GONE);
				Drawable draw = getResources().getDrawable(R.drawable.user_detail_add_follow_selector);
				draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
				followImg.setCompoundDrawables(draw, null, null, null);
				followImg.setText("添加关注");
			} else if (vo.getRelPositive() == 1) {
				shareBtn.setVisibility(View.VISIBLE);
				commonBottom.setVisibility(View.VISIBLE);
				blacklistBottom.setVisibility(View.GONE);
				Drawable draw;
				if (vo.getRelInverse() == 1) {
					draw = getResources().getDrawable(R.drawable.user_detail_follow_eachother_selector);
					draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
					followImg.setCompoundDrawables(draw, null, null, null);
					followImg.setText("互相关注");
				} else {
					draw = getResources().getDrawable(R.drawable.user_detail_have_follow_selector);
					draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
					followImg.setCompoundDrawables(draw, null, null, null);
					followImg.setText("已关注");
				}
			} else if (vo.getRelPositive() == 2) {
				commonBottom.setVisibility(View.GONE);
				blacklistBottom.setVisibility(View.VISIBLE);
				rightMenu.setVisibility(View.VISIBLE);
				shareBtn.setVisibility(View.INVISIBLE);
			}
			bottom.setVisibility(View.VISIBLE);
		} else {
			bottom.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 将用户信息渲染到 手机的界面上
	 * 
	 * @param vo
	 */
	private void renderContent(ExtUserVo vo) {
		// 获取上下文用户信息
		if (vo != null) {
			userId.setText(vo.getSerial() > 0 ? String.valueOf(vo.getSerial()) : "");
			if (flag) {
				if (vo.getRemarksName() != null && !"".equals(vo.getRemarksName())) {
					nickName.setText(vo.getRemarksName());
				} else {
					nickName.setText(vo.getUsername() == null ? "" : vo.getUsername());
				}
			} else {
				nickName.setText(vo.getUsername() == null ? "" : vo.getUsername());
			}
			if (vo.getMood() != null && !"".equals(vo.getMood())) {
				signName.setVisibility(View.VISIBLE);
				signName.setText(vo.getMood() == null ? "" : vo.getMood().trim());
			} else {
				signName.setVisibility(View.INVISIBLE);
			}
			if (vo.getSex() == 0) {
				age.setText("男" + (vo.getAge() > 0 ? " " + AgeUtil.convertAgeByBirth(vo.getAge()) + "" : ""));
			} else if (vo.getSex() == 1) {
				age.setText("女" + (vo.getAge() > 0 ? " " + AgeUtil.convertAgeByBirth(vo.getAge()) + "" : ""));
			} else {
				age.setText(vo.getAge() > 0 ? AgeUtil.convertAgeByBirth(vo.getAge()) + "" : "");
			}
			if (vo.getCity() == null || "".equals(vo.getCity())) {
				city.setVisibility(View.GONE);
			} else {
				city.setVisibility(View.VISIBLE);
				city.setText(vo.getCity() == null ? "" : vo.getCity());
			}
			myJob.setText(vo.getJob() == null ? "" : vo.getJob());
			gameTime.setText(vo.getGameTime() == null ? "" : vo.getGameTime());
			loveGame.setText(vo.getLikeGameType() == null ? "" : vo.getLikeGameType());
			// 获取头像图片
			if (vo.getAvatar() != null) {
				UserUtil.showAvatar(this, avatar, vo.getAvatar(), false, vo.getUserid(), null);
			}
			addPhoteBtn.setVisibility(View.GONE);
			if (vo.getRelPositive() == 0) {
				shareBtn.setVisibility(View.VISIBLE);
				commonBottom.setVisibility(View.VISIBLE);
				blacklistBottom.setVisibility(View.GONE);
				Drawable draw = getResources().getDrawable(R.drawable.user_detail_add_follow_selector);
				draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
				followImg.setCompoundDrawables(draw, null, null, null);
				followImg.setText("添加关注");
			} else if (vo.getRelPositive() == 1) {
				shareBtn.setVisibility(View.VISIBLE);
				commonBottom.setVisibility(View.VISIBLE);
				blacklistBottom.setVisibility(View.GONE);
				Drawable draw;
				if (vo.getRelInverse() == 1) {
					draw = getResources().getDrawable(R.drawable.user_detail_follow_eachother_selector);
					draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
					followImg.setCompoundDrawables(draw, null, null, null);
					followImg.setText("互相关注");
				} else {
					draw = getResources().getDrawable(R.drawable.user_detail_have_follow_selector);
					draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
					followImg.setCompoundDrawables(draw, null, null, null);
					followImg.setText("已关注");
				}
			} else if (vo.getRelPositive() == 2) {
				commonBottom.setVisibility(View.GONE);
				blacklistBottom.setVisibility(View.VISIBLE);
				rightMenu.setVisibility(View.VISIBLE);
				shareBtn.setVisibility(View.INVISIBLE);
			}
			bottom.setVisibility(View.VISIBLE);
		} else {
			ToastUtil.showToast(this, getString(R.string.user_get_user_detail_info_fail));
			// 返回上一个界面
			UserDetailInfoActivity.this.finish();
		}
	}

	/**
	 * 初始化一些dialog 当点击备注的时候 弹出来的dialog就是 在此初始化
	 */
	private void setView() {
		dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = View.inflate(this, R.layout.dialog_remarks, null);
		updateRemark = (EditText) view.findViewById(R.id.input_remarks_name);
		InputFilterUtil.lengthFilter(this, updateRemark, 16, getString(R.string.remarkname_verify_fail, 8, 16));
		usedName = (TextView) view.findViewById(R.id.used_name);
		cancelBtn = (Button) view.findViewById(R.id.cannelBtn);
		commitBtn = (Button) view.findViewById(R.id.commitBtn);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				updateRemark.setText("");
			}
		});
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				updateRemarks();
			}
		});
		dialog.setContentView(view);

	}

	/**
	 * 当点击备注所弹框 出来的 dialog的确定按钮的时候 这个时候，就会执行下面的这个方法 去修改当前用户的备注名
	 */
	protected void updateRemarks() {
		final String remarksName = updateRemark.getText().toString();
		if (remarksName == null || "".equals(remarksName)) {
			updateRemark.setText("");
			ToastUtil.showToast(this, "备注名称不能为空哦！");
		} else if (remarksName.trim().length() == 0) {
			updateRemark.setText("");
			ToastUtil.showToast(this, "请输入正确的备注名称");
		} else if (ServiceFactory.getInstance().getWordsManager().matchName(remarksName)) {
			ToastUtil.showToast(this, getResources().getString(R.string.global_words_error));
		} else if (remarksName != null && !remarksName.isEmpty() && remarksName.trim().length() > 0) {
			if (!ServiceFactory.getInstance().getWordsManager().match(remarksName)) {
				ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						nickName.setText(remarksName);
						updateRemark.setText("");
						// 把昵称插入到数据库里面去
						extUserVo.setRemarksName(remarksName);
						UserDao dao = DaoFactory.getDaoFactory().getUserDao(UserDetailInfoActivity.this);
						dao.insertOrUpdateByUserid(extUserVo);
						dialog.dismiss();
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						updateRemark.setText("");
						if (result == 500127) {
							ErrorCodeUtil.handleErrorCode(UserDetailInfoActivity.this, result, null);
						} else {
							ErrorCodeUtil.handleErrorCode(UserDetailInfoActivity.this, result, resultMsg);
						}
						dialog.dismiss();
					}
				}, this, uid, MsgsConstants.OT_USER, MsgsConstants.OP_USER_REMARK_NAME, remarksName.trim(), null, null);
			} else {
				updateRemark.setText("");
				ToastUtil.showToast(this, getResources().getString(R.string.global_words_error));
				dialog.dismiss();
			}
		} else {
			updateRemark.setText("");
			ToastUtil.showToast(this, "您输入的昵称不合法，不能少于4个字符且不能为纯数字。");
			dialog.dismiss();
		}

	}

	/**
	 * 获取用户的积分 将积分显示到手机界面上
	 * 
	 * @param userDetialView
	 */
	private void getPoint() {
		ProxyFactory.getInstance().getUserProxy().getUserPoint(new ProxyCallBack<List<ExtUserVo>>() {

			@Override
			public void onSuccess(List<ExtUserVo> result) {
				int point = 0;
				if (result != null && result.size() == 1) {
					ExtUserVo vo = result.get(0);
					point = vo.getPoint();
				}
				pointNum.setText(String.valueOf(point));
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

			}
		}, this, uid + "");
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (talk != null)
			talk.setEnabled(true);
	}

	/**
	 * 给界面中的 一些按钮 添加 点击事件
	 */
	@Override
	public void onClick(View v) {
		if (extUserVo != null) {
			if (v.getId() == follow.getId()) {
				if (FastClickLimitUtil.isFastClick()) 
					return;
				follow.setEnabled(false);
				follow.setClickable(false);
				// 点击的是关注按钮
				if (extUserVo.getRelPositive() == 1) {
					cannelRelUser(v, uid, 1);
				} else {
					addFollowUser(v, uid);
				}
			} else if (v.getId() == talk.getId()) {
				// 点击对话
				if (isFromChatActivityOpen == true) {
					// 从聊天窗口打开
					this.finish();
				} else {
					// 对聊
					Intent intent = new Intent(this, UserChatFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOUSERID, uid);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					startActivity(intent);
				}
			} else if (v.getId() == postItem.getId()) {
				Intent intent = new Intent(this, UserTopicListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				String title = "";
				if (extUserVo.getSex() == 0) {
					title = "他的帖子";
				} else if (extUserVo.getSex() == 1) {
					title = "她的帖子";
				} else {
					title = "Ta的帖子";
				}
				bundle.putString(BaseActivity.TITLE, title);
				startActivity(intent);
			} else if (v.getId() == gameItem.getId()) {
				Intent intent = new Intent(this, GameListActivity.class);
				Bundle bundle = new Bundle();
				// 窗口标题
				String title = "";
				if (extUserVo.getSex() == 0)
					title = "他的贴吧";
				else if (extUserVo.getSex() == 1)
					title = "她的贴吧";
				else
					title = "Ta的贴吧";
				bundle.putString(BaseActivity.TITLE, title);
				// 是否显示左边
				bundle.putBoolean(BaseActivity.IS_SHOW_LEFT, true);
				// 是否显示右边
				bundle.putBoolean(BaseActivity.IS_SHOW_RIGHT, false);
				// 是否显示TOP搜索框内容
				bundle.putBoolean(BaseActivity.VISIBLE_TOP_MODE, false);
				// 列表显示类型
				bundle.putInt(BaseActivity.MODE, CommonGameAdapter.MODE_SIMPLE);
				// 用户ID
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
				intent.putExtras(bundle);
				startActivity(intent);
			} else if (v.getId() == followItem.getId()) {
				Intent intent = new Intent(this, UserListActicity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TYPE, UserAdapter2.TYPE_USER_FOLLW);
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, extUserVo.getUserid());
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX, extUserVo.getSex());
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
				intent.putExtras(bundle);
				startActivity(intent);
			} else if (v.getId() == fansItem.getId()) {
				Intent intent = new Intent(this, UserListActicity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TYPE, UserAdapter2.TYPE_USER_FANS);
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, extUserVo.getUserid());
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX, extUserVo.getSex());
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
				intent.putExtras(bundle);
				startActivity(intent);
			} else if (v.getId() == groupItem.getId()) {
				Intent intent = new Intent(this, GroupListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, GroupAdapter2.MODE_USER_GROUP);
				bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX, extUserVo.getSex());
				intent.putExtras(bundle);
				startActivity(intent);
			} else if (v.getId() == blacklistBottom.getId()) {
				cannelBlacklist();
			} else if (v.getId() == rightMenu.getId()) {
				onClickRightMenu(v);
			} else if (v.getId() == shareBtn.getId()) {
				shareUserInfo();
			}else if(v.getId()==playItem.getId()){
				Intent intent = new Intent(UserDetailInfoActivity.this,PlayDetailsActivity.class);
					Bundle budle  = new Bundle();
					budle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
					intent.putExtras(budle);
					startActivity(intent);
			}else if(v.getId()==roleItem.getId()){
				Intent intent = new Intent(UserDetailInfoActivity.this, UserRoleDetailInfoActivity.class);
				intent.putExtra("uid", uid);
				intent.putExtra("other", true);
				startActivity(intent);
			}
		}
	}

	/**
	 * 分享用户的资料的
	 */
	private void shareUserInfo() {
		if (extUserVo == null)
			return;
		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);// 类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_USER);// 类型为类型为用户
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_USER_DETIAL);// 内部分享类型
		shareDate.setTargetId(extUserVo.getUserid());// 用户id(目标ID)
		shareDate.setTargerSerialId(extUserVo.getSerial());// 用户id(目标ID)
		shareDate.setTargetName(extUserVo.getUsername());// 用户名称（目标名称）
		if (SystemContext.getInstance().getExtUserVo().getUserid() == extUserVo.getUserid()) {
			shareDate.setDetailType(0);// 自己
		} else if (extUserVo.getSex() == 0) {
			shareDate.setDetailType(1);// 男
		} else if (extUserVo.getSex() == 1) {
			shareDate.setDetailType(2);// 女
		} else if (extUserVo.getSex() == 2) {
			shareDate.setDetailType(3);// 游客
		}
		// shareDate.setSite("游伴");//QQ空间分享时使用（暂固定写法）
		// shareDate.setSiteUrl("http://www.51uban.com");//QQ空间分享时使用（暂固定写法）
		// 获得图片地址
		shareDate.setImageUrl(ResUtil.getSmallRelUrl(extUserVo.getAvatar()));
		shareDate.setImagePath(extUserVo.getAvatar());
		ShareCallbackListener listener = new ShareCallbackListener() {
			@Override
			public void doSuccess(String plamType) {
				// 分享他人资料
				ShareTaskUtil.makeShareTask(UserDetailInfoActivity.this, TAG, extUserVo.getUserid(), MsgsConstants.OT_USER,
						MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
			}

			@Override
			public void doFail() {
			}
		};
		ShareManager.getInstance().share(this, inflater, extUserVo, shareDate, listener);
	}

	/**
	 * 取消关注
	 * 
	 * @param v
	 * @param uid
	 * @param type
	 */
	private void cannelRelUser(final View v, final long uid, final int type) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().cannelFollowUser(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				follow.setEnabled(true);
				follow.setClickable(true);
				dialog.dismiss();
				switch (result) {
				case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
					extUserVo.setRelPositive(0);
					Drawable draw = getResources().getDrawable(R.drawable.user_detail_add_follow_selector);
					draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
					followImg.setCompoundDrawables(draw, null, null, null);
					followImg.setText("添加关注");
					commonBottom.setVisibility(View.VISIBLE);
					blacklistBottom.setVisibility(View.GONE);
					rightMenu.setVisibility(View.VISIBLE);
					flag = false;
					UserDao dao = DaoFactory.getDaoFactory().getUserDao(UserDetailInfoActivity.this);
					extUserVo.setRemarksName("");
					dao.insertOrUpdateByUserid(extUserVo);
					// 取消关注后，用户名要显示昵称，不能在显示备注名了
					nickName.setText(extUserVo.getUsername());
					if (type == 1) {
						ToastUtil.showToast(v.getContext(), v.getContext().getString(R.string.user_cannel_follow_success));
					} else if (type == 2) {
						ToastUtil.showToast(v.getContext(), v.getContext().getString(R.string.user_cannel_black_success));
					}
					break;
				default:
					break;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				follow.setEnabled(true);
				follow.setClickable(true);
				dialog.dismiss();
			}
		}, this, uid);
	}

	/**
	 * 添加关注
	 * 
	 * @param v
	 * @param uid
	 */
	private void addFollowUser(final View v, final long uid) {

		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().addFollowUser(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				follow.setEnabled(true);
				follow.setClickable(true);
				switch (result) {
				case ErrorCode.EC_OK_VALUE:
					extUserVo.setRelPositive(1);
					Drawable draw;
					if (extUserVo.getRelInverse() == 1) {
						draw = getResources().getDrawable(R.drawable.user_detail_follow_eachother_selector);
						draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
						followImg.setCompoundDrawables(draw, null, null, null);
						followImg.setText("互相关注");
					} else {
						draw = getResources().getDrawable(R.drawable.user_detail_have_follow_selector);
						draw.setBounds(0, 0, draw.getMinimumWidth(), draw.getMinimumHeight());
						followImg.setCompoundDrawables(draw, null, null, null);
						followImg.setText("已关注");
					}
					flag = true;
					ToastUtil.showToast(v.getContext(), v.getContext().getString(R.string.user_add_follow_success));

					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(SystemContext.getInstance().getExtUserVo().getUserid()));
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, SystemContext.getInstance().getExtUserVo().getUsername());
					ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(uid));
					UserVo uvo = DaoFactory.getDaoFactory().getUserDao(UserDetailInfoActivity.this).getUserById(uid);
					if (uvo != null)
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, uvo.getUsername());
					MobclickAgent.onEvent(UserDetailInfoActivity.this, UMConfig.MSGS_EVENT_USER_FOLLOW, ummap);
					break;
				default:
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				follow.setEnabled(true);
				follow.setClickable(true);
				dialog.dismiss();
			}
		}, v.getContext(), uid, true);
	}

	/**
	 * 弹出绑定手机的对话框
	 * 
	 * @param actionName
	 */
	private void createBundPhoneDialog() {
		Intent intent = new Intent(UserDetailInfoActivity.this, BundPhoneActivity.class);
		UserDetailInfoActivity.this.startActivity(intent);
	}

	/**
	 * 移除黑名单 将之前拉黑的用户移除黑名单
	 */
	private void cannelBlacklist() {
		dialog2 = new Dialog(this, R.style.SampleTheme_Light);
		dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog2.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog2.findViewById(R.id.content);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("移除黑名单");
		final TextView txt = new TextView(this);
		txt.setTextColor(getResources().getColor(R.color.darkgray));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText("确定将" + extUserVo.getUsername() + "移除黑名单？");
		content.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog2.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cannelRelUser(v, uid, 2);
				dialog2.dismiss();
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog2.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog2.dismiss();
			}
		});

		dialog2.show();
	}

	/**
	 * 当点击右上角的按钮的时候 会弹出一个popwindow
	 * 
	 * @param v
	 */
	private void onClickRightMenu(View v) {
		// rightMenu.setBackgroundResource(R.drawable.common_menu_more_pre);
		final MsgsPopTextItem item0 = new MsgsPopTextItem(this, "备注");
		final MsgsPopTextItem item1 = new MsgsPopTextItem(this, "拉黑");
		final MsgsPopTextItem item2 = new MsgsPopTextItem(this, "举报");
		final MsgsPopTextItem item3 = new MsgsPopTextItem(this, "禁言");
		List<View> items = new ArrayList<View>();
		if (flagStopSay) {
			items.add(item3);
		}
		if (isfromFollowList && flag) {
			items.add(item0);
		}
		UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		UserVo uvo = dao.getUserByUserId(uid);
		if (uvo != null && uvo.getRelPositive() != 2)
			items.add(item1);
		items.add(item2);
		MsgsPopWindow pw = new MsgsPopWindow(this, items, v, 0, 0);
		pw.setOnClickPopItemListener(new OnClickPopItemListener() {

			@Override
			public void onClickPopItem(View v) {
				if (v == item1) {
					addUserToBlackListDialog(uid);
				} else if (v == item2) {
					Intent intent = new Intent(UserDetailInfoActivity.this, ReportActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, uid);
					bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_USER);
					intent.putExtras(bundle);
					startActivity(intent);
				} else if (v == item0) {
					updateRemarkName();
				} else if (v == item3) {
					Intent intent = new Intent(UserDetailInfoActivity.this, SelectStopTimeActivity.class);
					intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, groupId);
					intent.putExtra(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid + "");
					UserDetailInfoActivity.this.startActivity(intent);
				}
			}
		});
		pw.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// rightMenu.setBackgroundResource(R.drawable.common_menu_more_nor);
			}
		});
	}

	/**
	 * 当点击备注按钮的时候 就会执行下面的这个方法 弹对对话框 修改备注名
	 */
	protected void updateRemarkName() {
		if (extUserVo != null) {
			usedName.setText(extUserVo.getUsername() + "");
		} else {
			usedName.setText("");
		}
		dialog.show();
	}

	/**
	 * 当点击拉黑的按钮的 时候，首先 会弹出一个对话框 判断是否要拉黑 在点击确定按钮的时候，才是拉黑用户
	 * 
	 * @param uid
	 */
	private void addUserToBlackListDialog(final long uid) {
		dialog3 = new Dialog(this, R.style.SampleTheme_Light);
		dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog3.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog3.findViewById(R.id.content);
		TextView title = (TextView) dialog3.findViewById(R.id.title);
		title.setText("加入黑名单");
		final TextView txt = new TextView(this);
		txt.setTextColor(getResources().getColor(R.color.dialog_font_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText("确定将" + extUserVo.getUsername() + "加入黑名单？");
		content.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog3.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addUserToBlackList(uid);
				dialog3.dismiss();
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog3.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog3.dismiss();
			}
		});

		dialog3.show();
	}

	/**
	 * onstop方法里面 取消对话框
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (dialog != null)
			dialog.dismiss();
		if (dialog1 != null)
			dialog1.dismiss();
		if (dialog2 != null)
			dialog2.dismiss();
		if (dialog3 != null)
			dialog3.dismiss();
	}

	/**
	 * 下面的这个方法 是真正的将 用户给拉黑掉
	 * 
	 * @param uid
	 */
	private void addUserToBlackList(final long uid) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().addToBlackList(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (result == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE) {
					ToastUtil.showToast(UserDetailInfoActivity.this, "增加到黑名单成功");
					dialog.dismiss();
					flag = false;
					UserDao dao = DaoFactory.getDaoFactory().getUserDao(UserDetailInfoActivity.this);
					extUserVo.setRemarksName("");
					extUserVo.setRelPositive(2);
					dao.insertOrUpdateByUserid(extUserVo);
					UserDetailInfoActivity.this.finish();
				} else {
					ToastUtil.showToast(UserDetailInfoActivity.this, "增加到黑名单失败");
					dialog.dismiss();
				}
				UMUtil.sendEvent(UserDetailInfoActivity.this, UMConfig.MSGS_EVENT_USER_BLACK, null, null, uid + "", null, null);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ToastUtil.showToast(UserDetailInfoActivity.this, "增加到黑名单失败");
				dialog.dismiss();
			}
		}, this, uid);
	}

	/**
	 * 给photocount设置 get和set方法
	 * 
	 * @return
	 */
	public int getPhotoCount() {
		return photoCount;
	}

	public void setPhotoCount(int photoCount) {
		this.photoCount = photoCount;
	}
}
