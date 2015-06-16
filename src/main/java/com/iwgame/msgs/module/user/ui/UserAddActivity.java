/**      
o * UserAddActivity.java Create on 2014-4-15     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.ui;

import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.ShareSdkManager;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.group.ui.AddGroupActivity;
import com.iwgame.msgs.module.group.ui.CreatGroupActivity;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: UserAddActivity
 * @Description: 添加用户（公会）主界面
 * @author 王卫
 * @date 2014-4-15 下午4:55:10
 * @Version 1.0
 * 
 */
public class UserAddActivity extends BaseActivity implements OnClickListener {
	// 搜索类型 0：按用户编号,按昵称1：附近的人2：摇一摇3：通讯录4：微博账号5:用户id
	// 我是几个会长的个数
	private int presCount = 0;
	// 加入公会个数
	private int jgCount = 0;

	private LinearLayout addsearchfriendItem;
	private LinearLayout addContactfriendItem;
	private LinearLayout addwebofriendItem;
	private LinearLayout addweixinfriendItem;
	private LinearLayout addfatefriendItem;
	private LinearLayout joingroupItem;
	private LinearLayout creatgroupItem;
    private LinearLayout addQQFriendsItem;
	private final String TAG = "UserAddActivity";
    private LinearLayout addfateItemParent;
	private CustomProgressDialog dialog;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 初始化功能
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
		addsearchfriendItem.setEnabled(true);
		addContactfriendItem.setEnabled(true);
		addwebofriendItem.setEnabled(true);
		addweixinfriendItem.setEnabled(true);
		addfatefriendItem.setEnabled(true);
		joingroupItem.setEnabled(true);
		creatgroupItem.setEnabled(true);
		addQQFriendsItem.setEnabled(true);
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
		titleTxt.setText("添加");
		dialog = CustomProgressDialog.createDialog(this, true);
		View addFriendView = View.inflate(this, R.layout.user_add_view, null);
		addContentViewChild(addFriendView);
		addsearchfriendItem = (LinearLayout) findViewById(R.id.addsearchfriendItem);
		addsearchfriendItem.setOnClickListener(this);
		addContactfriendItem = (LinearLayout) findViewById(R.id.addContactfriendItem);
		addContactfriendItem.setOnClickListener(this);
		addwebofriendItem = (LinearLayout) findViewById(R.id.addwebofriendItem);
		addwebofriendItem.setOnClickListener(this);
		addweixinfriendItem = (LinearLayout) findViewById(R.id.addweixinfriendItem);
		addweixinfriendItem.setOnClickListener(this);
		addfatefriendItem = (LinearLayout) findViewById(R.id.addfatefriendItem);
		addfatefriendItem.setOnClickListener(this);
		addfateItemParent = (LinearLayout)findViewById(R.id.addfateitemparent);
		if(AdaptiveAppContext.getInstance().getAppConfig().isAddfriend_fate()){
			addfateItemParent.setVisibility(View.VISIBLE);
		}else{
			addfateItemParent.setVisibility(View.GONE);
		}
		joingroupItem = (LinearLayout) findViewById(R.id.joingroupItem);
		joingroupItem.setOnClickListener(this);
		creatgroupItem = (LinearLayout) findViewById(R.id.creatgroupItem);
		creatgroupItem.setOnClickListener(this);
		addQQFriendsItem = (LinearLayout)findViewById(R.id.addqqfriendItem);
		addQQFriendsItem.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.addsearchfriendItem:
			intent = new Intent(this, AddFriendActivity.class);
			startActivity(intent);
			addsearchfriendItem.setEnabled(false);
			break;
		case R.id.addContactfriendItem:
			intent = new Intent(this, ContactFriendActivity.class);
			startActivity(intent);
			addContactfriendItem.setEnabled(false);
			break;
		case R.id.addwebofriendItem:
				checkWeiboAuth();
				addwebofriendItem.setEnabled(false);
			break;
		case R.id.addweixinfriendItem:
				creatWeixinDialog();
			break;
		case R.id.addfatefriendItem:
			intent = new Intent(this, UserListActicity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TYPE, UserAdapter2.TYPE_FATE);
			bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_KEYWORD, null);
			intent.putExtras(bundle);
			startActivity(intent);
			addfatefriendItem.setEnabled(false);
			break;
		case R.id.joingroupItem:
			intent = new Intent(this, AddGroupActivity.class);
			startActivity(intent);
			joingroupItem.setEnabled(false);
			break;
		case R.id.creatgroupItem:
			if(SystemContext.getInstance().getIsGuest() != SystemConfig.LOGIN_TARGET_NORMAL){
				createBundPhoneDialog();
			}else{
				getGroupData();
			}
			creatgroupItem.setEnabled(false);
			break;
		case R.id.addqqfriendItem:
			//点击添加qq好友的时候，执行下面的方法
			addQQFriendsItem.setEnabled(false);
			addQQFriends();
			break;
		default:
			break;
		}
	}

	/**
	 * 添加qq好友方法
	 */
	private void addQQFriends() {
		//TODO
		ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
		if(vo == null) return;
		if (ShareSdkManager.getInstance().getAuthorizeStatus(this, QQ.NAME)) {
			ShareDate shareDate = new ShareDate();
			shareDate.setShareType(ShareUtil.SHARE_TYPE_INVITE);//类型为分享
			shareDate.setTargetType(ShareUtil.TYPE_USER);//类型为用户
			shareDate.setTargetId(vo.getUserid());//用户id(目标ID)
			shareDate.setTargerSerialId(vo.getSerial());//用户Serialid(目标SerialID)
			shareDate.setTargetName(vo.getUsername());//用户姓名（目标名称）
			shareDate.setImageUrl(ResUtil.getSmallRelUrl("i_youban"));
			shareDate.setTitle(getString(R.string.postbar_topic_share_add_qqfriend_title));
			
			ShareManager.getInstance().shareContent(UserAddActivity.this, shareDate, ShareUtil.MARKET_QQ, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_QQ, new ShareCallbackListener() {
				@Override
				public void doSuccess(String plamType) {
					addQQFriendsItem.setEnabled(true);
					if(QQ.NAME.equals(plamType)){
						//邀请QQ好友
						ShareTaskUtil.makeShareTask(UserAddActivity.this, TAG, SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.OT_QQ, MsgsConstants.OP_INVITE_FRIEND, plamType, null, this.shortUrl);
					}
				}

				@Override
				public void doFail() {
					addQQFriendsItem.setEnabled(true);
				}
			});
		}else {
			ShareManager.getInstance().authorize(this, 0, QQ.NAME, new ShareCallbackListener() {
				
				@Override
				public void doSuccess(String plamType) {
					if(QQ.NAME.equals(plamType)){
						ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
						if(vo == null) return;
						ShareDate shareDate = new ShareDate();
						shareDate.setShareType(ShareUtil.SHARE_TYPE_INVITE);//类型为分享
						shareDate.setTargetType(ShareUtil.TYPE_USER);//类型为用户
						shareDate.setTargetId(vo.getUserid());//用户id(目标ID)
						shareDate.setTargerSerialId(vo.getSerial());//用户Serialid(目标SerialID)
						shareDate.setTargetName(vo.getUsername());//用户姓名（目标名称）
						shareDate.setImageUrl(ResUtil.getSmallRelUrl("i_youban"));
						shareDate.setTitle(getString(R.string.postbar_topic_share_add_qqfriend_title));
						
						ShareManager.getInstance().shareContent(UserAddActivity.this, shareDate, ShareUtil.MARKET_QQ, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_QQ, new ShareCallbackListener() {
							@Override
							public void doSuccess(String plamType) {
								addQQFriendsItem.setEnabled(true);
								if(QQ.NAME.equals(plamType)){
									//邀请QQ好友
									ShareTaskUtil.makeShareTask(UserAddActivity.this, TAG, SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.OT_QQ, MsgsConstants.OP_INVITE_FRIEND, plamType, null, this.shortUrl);
								}
							}

							@Override
							public void doFail() {
								addQQFriendsItem.setEnabled(true);
							}
						});
					}
				}
				@Override
				public void doFail() {
				}
			});
		}

		
	}

	/**
	 * 绑定手机提示框
	 * @param actionName
	 */
	private void createBundPhoneDialog(){
		Intent intent = new Intent(UserAddActivity.this, BundPhoneActivity.class);
		startActivity(intent);
	}
	
	
	/**
	 * 
	 */
	private void creatWeixinDialog() {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setCanceledOnTouchOutside(true);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("添加微信好友");
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		content.removeAllViews();
		View view = View.inflate(this, R.layout.user_addfriend_weixin_dialog_content, null);
		content.addView(view, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		dialog.findViewById(R.id.bottom).setVisibility(View.GONE);
		view.findViewById(R.id.friend).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog != null)
					dialog.dismiss();
				ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
				if(vo == null) return;

				ShareDate shareDate = new ShareDate();
				shareDate.setShareType(ShareUtil.SHARE_TYPE_INVITE);//类型为分享
				shareDate.setTargetType(ShareUtil.TYPE_USER);//类型为用户
				shareDate.setTargetId(vo.getUserid());//用户id(目标ID)
				shareDate.setTargerSerialId(vo.getSerial());//用户serialid(目标serialID)
				shareDate.setTargetName(vo.getUsername());//用户姓名（目标名称）
				String avatar = vo.getAvatar();
				if(avatar != null && !avatar.isEmpty()){
					shareDate.setImageUrl(ResUtil.getSmallRelUrl(avatar));
				}
				
				ShareManager.getInstance().shareContent(UserAddActivity.this, shareDate, ShareUtil.MARKET_WEIXIN, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_WEIXIN, new ShareCallbackListener() {
					@Override
					public void doSuccess(String plamType) {
						if(Wechat.NAME.equals(plamType)){
							//邀请微信好友
							ShareTaskUtil.makeShareTask(UserAddActivity.this, TAG, SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.OT_WEIXIN, MsgsConstants.OP_INVITE_FRIEND, plamType, null, this.shortUrl);
						}
					}

					@Override
					public void doFail() {
					}
				});
			}
		});
		view.findViewById(R.id.friends).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog != null)
					dialog.dismiss();
				ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
				if(vo == null) return;

				ShareDate shareDate = new ShareDate();
				shareDate.setShareType(ShareUtil.SHARE_TYPE_INVITE);//类型为分享
				shareDate.setTargetType(ShareUtil.TYPE_USER);//类型为用户
				shareDate.setTargetId(vo.getUserid());//用户id(目标ID)
				shareDate.setTargerSerialId(vo.getSerial());//用户serialid(目标serialID)
				shareDate.setTargetName(vo.getUsername());//用户姓名（目标名称）
				String avatar = vo.getAvatar();
				if(avatar != null && !avatar.isEmpty()){
					shareDate.setImageUrl(ResUtil.getOriginalRelUrl(avatar));
				}
				
				ShareManager.getInstance().shareContent(UserAddActivity.this, shareDate, ShareUtil.MARKET_PENGYOUQUAN, shareDate.getTargetType(), ShareUtil.TYPE_TARGET_PENGYOUQUAN, new ShareCallbackListener() {
					@Override
					public void doSuccess(String plamType) {
						if(WechatMoments.NAME.equals(plamType)){
							//分享个人资料到朋友圈
							ShareTaskUtil.makeShareTask(UserAddActivity.this, TAG, SystemContext.getInstance().getExtUserVo().getUserid(), MsgsConstants.OT_USER, MsgsConstants.OP_RECORD_SHARE, plamType, null, this.shortUrl);
						}
					}

					@Override
					public void doFail() {
					}
				});
			}
		});
		dialog.show();
		UMUtil.sendEvent(this, UMConfig.MSGS_EVENT_USER_WEIXIN_CLICK, null, null, null, null, null);
	}
	

	/**
	 * 获取公会数据
	 */
	private void getGroupData() {
		presCount = 0;
		jgCount = 0;
		if(dialog != null)dialog.show();
		ProxyFactory.getInstance().getGroupProxy().getCreatGroupMax(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if(result != null && result > 0){
					GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
					List<GroupVo> list = groupDao.findAllMyGroups();
					presCount = result;
					if(list != null)
					jgCount = list.size() - result;
					addGroupAction();
				}else{
					addGroupAction();
				}
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				addGroupAction();
			}
		}, UserAddActivity.this, 1);
	}

	/**
	 * 创建公会行为判断
	 */
	private void addGroupAction() {
		int gcm = SystemContext.getInstance().getGCM();
		
		if (presCount < gcm) {
//			if (jgCount >= SystemContext.getInstance().getJGM()+gcm) {
//				ToastUtil.showToast(this, getString(R.string.group_join_group_over_count));
//				creatgroupItem.setEnabled(true);
//				if(dialog != null) dialog.dismiss();
//				return;
//			}
			GroupGradeDao groupGradeDao = DaoFactory.getDaoFactory().getGroupGradeDao(SystemContext.getInstance().getContext());
			GroupGradeVo vo = groupGradeDao.queryByGrade(1);
			if(vo != null && vo.getPoint() > 0){
				getPoint(vo.getPoint());
			}else{
				ProxyFactory.getInstance().getGroupProxy().getGroupGradePolicy(new ProxyCallBack<List<GroupGradeVo>>() {

					@Override
					public void onSuccess(List<GroupGradeVo> result) {
						for (Iterator iterator = result.iterator(); iterator.hasNext();) {
							GroupGradeVo groupGradeVo = (GroupGradeVo) iterator.next();
							if (groupGradeVo.getGrade() == 1) {
								getPoint(groupGradeVo.getPoint());
								break;
							}
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						creatgroupItem.setEnabled(true);
						if(dialog != null) dialog.dismiss();
					}
				}, this);
			}
		} else {
			String tip = null;
			int count =SystemContext.getInstance().getGCM();
			if(count==0){
				tip = getString(R.string.group_creat_count_max);
			}else{
			 tip = String.format(getString(R.string.group_creat_count_max2), SystemContext.getInstance().getGCM());
				}
			ToastUtil.showToast(this, tip);
			creatgroupItem.setEnabled(true);
			if(dialog != null) dialog.dismiss();
		}
	}

	
	
	/**
	 *创建公会的时候，
	 *判断当前用户的积分是否足够
	 */
	private void getPoint(final int point) {
		ProxyFactory.getInstance().getUserProxy().getUserPoint(new ProxyCallBack<List<ExtUserVo>>() {
			@Override
			public void onSuccess(List<ExtUserVo> result) {
				if(dialog != null) dialog.dismiss();
				if(result == null || result.size() <= 0){
					ToastUtil.showToast(UserAddActivity.this, getString(R.string.group_creat_point_notenough));
				}else{
					int upoint = result.get(0).getPoint();
					if (upoint >= point) {
						creatGroupPointDoalog(point);
					} else {
						ToastUtil.showToast(UserAddActivity.this, getString(R.string.group_creat_point_notenough));
					}
				}
				creatgroupItem.setEnabled(true);
			}
			@Override
			public void onFailure(Integer result, String resultMsg) {
				if(dialog != null) dialog.dismiss();
			}
		}, this, SystemContext.getInstance().getExtUserVo().getUserid()+"");
	}

	
	
	
	/**
	 * 创建公会消耗积分提醒
	 */
	private void creatGroupPointDoalog(int point) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("消耗积分提醒");
		final TextView txt = new TextView(this);
		txt.setTextColor(getResources().getColor(R.color.dialog_font_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(String.format("创建公会，需要耗费您%1$d积分哦！", point));
		content.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserAddActivity.this, CreatGroupActivity.class);
				startActivity(intent);
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

		try {
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 判断微博是否授权
	 */
	private void checkWeiboAuth() {
		if (ShareSdkManager.getInstance().getAuthorizeStatus(this, SinaWeibo.NAME)) {
			Intent intent = new Intent(UserAddActivity.this, WeiboFriendActivity.class);
			startActivity(intent);
		}else{
			ShareManager.getInstance().authorize(this, 0, SinaWeibo.NAME, new ShareCallbackListener() {
				
				@Override
				public void doSuccess(String plamType) {
					if(SinaWeibo.NAME.equals(plamType)){
						Intent intent = new Intent(UserAddActivity.this, WeiboFriendActivity.class);
						startActivity(intent);
					}
				}
				@Override
				public void doFail() {
				}
			});
		}
	}

}
