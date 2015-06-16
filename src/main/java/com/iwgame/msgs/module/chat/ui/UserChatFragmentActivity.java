package com.iwgame.msgs.module.chat.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.chat.adapter.ChatMessageAdapter;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.ReportActivity;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.utils.UserActionUtil;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.MenuMoreButton;
import com.iwgame.msgs.widget.popwindow.MsgsPopTextItem;
import com.iwgame.msgs.widget.popwindow.MsgsPopWindow;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

public class UserChatFragmentActivity extends BaseFragmentActivity implements OnClickListener, OnItemClickListener {
	private final String TAG = "UserChatFragmentActivity";

	private long toUserId;

	Button moreBtn;
	// 弹出框
	PopupWindow popWindow;

	ChatFragment chatFragment;
	UserDao userdao;
	/**
	 * 右上角的弹出窗口是否打开
	 */
	private boolean isShowPopWindows = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取上一个页面的传值
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				toUserId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOUSERID);
			}
		}
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		userdao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		// 设置显示top左边
		setLeftVisible(true);

		// 设置显示top右边
		setRightVisible(true);
		// 设置top右边功能按钮
		moreBtn = new MenuMoreButton(this);
		//moreBtn.setBackgroundResource(R.drawable.common_menu_more_nor);
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(moreBtn, params);
		moreBtn.setOnClickListener(this);

		// 设置TOP的中间布局容器
		LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
		titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);
		chatFragment = new ChatFragment();

		Bundle bundle = new Bundle();
		// 聊天需要的参数
		bundle = new Bundle();

		bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISSENDMSG, true);// 允许发消息
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOID, toUserId);
		bundle.putString(SystemConfig.BUNDLE_NAME_TODOMAIN, MsgsConstants.DOMAIN_USER);
		bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, MsgsConstants.MCC_CHAT);
		bundle.putString(SystemConfig.BUNDLE_NAME_CHANNELTYPE, MsgsConstants.MC_CHAT);
		bundle.putInt(SystemConfig.BUNDLE_NAME_PAGETYPE, ChatMessageAdapter.PAGE_TYPE_CHAT);

		chatFragment.setArguments(bundle);
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.contentView, chatFragment);
		// fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		// 获得用户信息
		ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> uservoresult) {
				if(uservoresult != null && uservoresult.size() > 0){
					UserVo result = uservoresult.get(0);
					if (result != null) {
						UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
						//数据库获取用户信息（备注）
						UserVo uvo = dao.getUserByUserId(result.getUserid());
						if(uvo != null){
							String rname = uvo.getRemarksName();
							if(rname != null && !rname.isEmpty()){
								titleTxt.setText(rname);
								result.setUsername(rname);
							}else{
								titleTxt.setText(result.getUsername());
							}
						}else{
							titleTxt.setText(result.getUsername());
						}
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "获得用户信息异常：" + result);
				// ErrorCodeUtil.handleErrorCode(UserChatFragmentActivity.this,
				// result);
			}

		};
		UserVo userVo = userdao.getUserById(toUserId);
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(toUserId);
		cdp.setUtime(userVo == null ? 0 : userVo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, this, p.build(), 0, null);

		// 设置返回功能
		Button backBtn = (Button) findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Utils.hideSoftInput(UserChatFragmentActivity.this, UserChatFragmentActivity.this.chatFragment.getSendMsgView()
							.getSendMsgEditText());
					UserChatFragmentActivity.this.finish();
				}
			});
		}
		
		ProxyFactory.getInstance().getUserProxy().collectActionlLog(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				
			}
		}, this, UserActionUtil.USER_OPEN_CHAT, toUserId, MsgsConstants.OT_USER, null);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == moreBtn.getId() && !isShowPopWindows) {

			isShowPopWindows = true;
			//moreBtn.setBackgroundResource(R.drawable.common_menu_more_pre);
			int xoffInPixels = v.getWidth() / 2;
			final MsgsPopWindow msgsPopWindow = new MsgsPopWindow(this, v, xoffInPixels, 0);

			// 获得用户信息，通过用户信息判断是否能够加入黑名单
			ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

				@Override
				public void onSuccess(List<UserVo> uservoresult) {
					// TODO Auto-generated method stub
					if(uservoresult == null || uservoresult.size() <= 0) return;
					UserVo result = uservoresult.get(0);
					if (result != null && result.getRelPositive() == MsgsConstants.UR_CRL) {
						// ToastUtil.showToast(UserChatFragmentActivity.this,
						// getString(R.string.chat_userchat_has_adduserToblacklist)
						// );
						setPopwindowItems(msgsPopWindow, true);
					} else {
						setPopwindowItems(msgsPopWindow, false);
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					// TODO Auto-generated method stub
					LogUtil.e(TAG, "获得用户信息异常");
					ErrorCodeUtil.handleErrorCode(UserChatFragmentActivity.this, result, resultMsg);
					setPopwindowItems(msgsPopWindow, false);
				}
			};
			UserVo userVo = userdao.getUserById(toUserId);
			ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
			ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
			cdp.setId(toUserId);
			cdp.setUtime(userVo == null ? 0 : userVo.getUpdatetime());
			p.addParam(cdp.build());
			ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, UserChatFragmentActivity.this, p.build(), 0, null);
			msgsPopWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					//moreBtn.setBackgroundResource(R.drawable.common_menu_more_nor);
					isShowPopWindows = false;
				}
			});

		}

	}

	private void setPopwindowItems(final MsgsPopWindow msgsPopWindow, boolean isBlackUser) {
		List<View> items = new ArrayList<View>();
		// 查看对方信息
		MsgsPopTextItem item = new MsgsPopTextItem(this, getString(R.string.chat_userchat_menu_openuserdetail));
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				msgsPopWindow.dismiss();
				openUserDetail(toUserId);
			}
		});
		items.add(item);
		// 清空聊天记录
		item = new MsgsPopTextItem(this, getString(R.string.chat_userchat_menu_cleanchatLog));
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				msgsPopWindow.dismiss();
				cleanChatLog(toUserId);
			}
		});
		items.add(item);
		// 加入黑名单
		if (!isBlackUser) {
			item = new MsgsPopTextItem(this, getString(R.string.chat_userchat_menu_adduserToblacklist));
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					msgsPopWindow.dismiss();
					addUserToBlackList(toUserId);

				}
			});
			items.add(item);
		}

		// 举报该用户
		item = new MsgsPopTextItem(this, getString(R.string.chat_userchat_menu_report));
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				msgsPopWindow.dismiss();
				ReportUser(toUserId);
			}
		});
		items.add(item);

		msgsPopWindow.setContentItems(items);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
	}

	/**
	 * 打开用户的详情页
	 * 
	 * @param uid
	 */
	private void openUserDetail(long uid) {
		Intent intent = new Intent(this, UserDetailInfoActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID, uid);
		bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_ISFROMCHATACTIVITYOPEN, true);
		intent.putExtras(bundle);
		this.startActivity(intent);
	}

	/**
	 * 清空聊天记录
	 */
	private void cleanChatLog(long touid) {
		// 先获得最大的index写到系统中
		long maxIndex = ProxyFactory.getInstance().getMessageProxy()
				.getMessageMaxIndex(MsgsConstants.MC_CHAT, touid, MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_CHAT);
		long minIndex = SystemContext.getInstance().getSubjectcanReadMinIndex(MsgsConstants.MC_CHAT, touid, MsgsConstants.DOMAIN_USER,
				MsgsConstants.MCC_CHAT);
		if (maxIndex > minIndex)
			SystemContext.getInstance().setSubjectcanReadMinIndex(MsgsConstants.MC_CHAT, touid, MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_CHAT,
					maxIndex);
		// 清空聊天记录
		ProxyFactory.getInstance().getMessageProxy().delMessage(MsgsConstants.MC_CHAT, touid, MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_CHAT);
		chatFragment.refreshData();
	}

	/**
	 * 增加用户到黑名单
	 * 
	 * @param uid
	 */
	public void addUserToBlackList(final long uid) {
		ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {

				if (result == Msgs.ErrorCode.EC_OK_VALUE) {
					ToastUtil.showToast(UserChatFragmentActivity.this, "增加到黑名单成功");
					UMUtil.sendEvent(UserChatFragmentActivity.this, UMConfig.MSGS_EVENT_USER_BLACK, null, null, String.valueOf(uid), null, true);
					// 同步用户
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_REL_USER, null);
				} else {
					// ToastUtil.showToast(UserChatFragmentActivity.this,
					// "增加到黑名单失败：失败码：" + String.valueOf(result),1000);
					UMUtil.sendEvent(UserChatFragmentActivity.this, UMConfig.MSGS_EVENT_USER_BLACK, null, null, String.valueOf(uid), null, false);
					ErrorCodeUtil.handleErrorCode(UserChatFragmentActivity.this, result, null);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// ToastUtil.showToast(UserChatFragmentActivity.this,
				// "增加黑名单异常："+result,1000);
				UMUtil.sendEvent(UserChatFragmentActivity.this, UMConfig.MSGS_EVENT_USER_BLACK, null, null, String.valueOf(uid), null, false);
				ErrorCodeUtil.handleErrorCode(UserChatFragmentActivity.this, result, resultMsg);
			}
		};
		ProxyFactory.getInstance().getUserProxy().addToBlackList(callback, this, uid);
	}

	/**
	 * 举报用户
	 * 
	 * @param uid
	 */
	public void ReportUser(long uid) {
		Intent intent = new Intent(this, ReportActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, uid);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_USER);
		intent.putExtras(bundle);
		startActivity(intent);
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
			if (chatFragment.getSendMsgView().hideSmileyView())
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		//chatFragment.onActivityResult(arg0, resultCode, data);
	}

}
