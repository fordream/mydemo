package com.iwgame.msgs.module.chat.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iwgame.msgs.common.BaseFragmentActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.chat.adapter.ChatMessageAdapter;
import com.iwgame.msgs.module.chat.adapter.MorePopWindowAdapter;
import com.iwgame.msgs.module.chat.adapter.PopWindowAdapter;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.ui.UserDetailInfoActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;
/**
 *
 * @ClassName: GroupMassMsgFragmentActivity 
 * @Description: TODO( 某个公会群发消息展示) 
 * @author chuanglong
 * @date 2014-1-13 下午12:32:13 
 * @Version 1.0
 *
 */
public class GroupMassMsgFragmentActivity extends BaseFragmentActivity implements OnClickListener, OnItemClickListener {
	private final String TAG = "GroupMassMsgFragmentActivity";

	private long toGroupId;

	ImageButton moreBtn;
	// 弹出框
	PopupWindow popWindow;

	ChatFragment chatFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取上一个页面的传值
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				toGroupId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOGROUPID);
			}
		}
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置显示top左边
		setLeftVisible(true);

		// 设置显示top右边
		setRightVisible(false);
		// 设置top右边功能按钮
		moreBtn = new ImageButton(this);
		moreBtn.setBackgroundResource(R.drawable.common_empty);
		moreBtn.setImageResource(R.drawable.common_tab_btn_more2);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(moreBtn);
		moreBtn.setOnClickListener(this);

		// 设置TOP的中间布局容器
		LinearLayout topcenter = (LinearLayout) findViewById(R.id.center);
		titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);

		chatFragment = new ChatFragment();

		Bundle bundle = new Bundle();
		// 聊天需要的参数
		bundle = new Bundle();

		bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISSENDMSG, false);// 允许发消息
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOID, toGroupId);
		bundle.putString(SystemConfig.BUNDLE_NAME_TODOMAIN, MsgsConstants.DOMAIN_GROUP);
		bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, MsgsConstants.MCC_ANNOUNCE);
		bundle.putString(SystemConfig.BUNDLE_NAME_CHANNELTYPE, MsgsConstants.MC_PUB);
		bundle.putInt(SystemConfig.BUNDLE_NAME_PAGETYPE,ChatMessageAdapter.PAGE_TYPE_GROUPMASSMSG);
		chatFragment.setArguments(bundle);
		FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.contentView, chatFragment);
		// fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		// 获得公会
		ProxyCallBack<List<GroupVo>> callback = new ProxyCallBack<List<GroupVo>>() {

			@Override
			public void onSuccess(List<GroupVo> groupvoresult) {
				// TODO Auto-generated method stub
				if(groupvoresult == null || groupvoresult.size() <= 0) return;
				GroupVo result = groupvoresult.get(0);
				if (result != null) {
					titleTxt.setText(result.getName() + "通知");
				}
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "获得公会信息异常：" + result);
				ErrorCodeUtil.handleErrorCode(GroupMassMsgFragmentActivity.this, result,resultMsg);
			}

		};
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		GroupVo groupVo = groupDao.findGroupByGrid(toGroupId);
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(toGroupId);
		cdp.setUtime(groupVo == null ? 0 : groupVo.getUtime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback, this, p.build(), MsgsConstants.OT_GROUP, null);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
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
		if (v.getId() == moreBtn.getId()) {
			// 弹出更多菜单
			View popview = this.getLayoutInflater().inflate(R.layout.common_popwindow_menu, null, true);
			popWindow = new PopupWindow(popview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
			popWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

			popWindow.setOutsideTouchable(false);
			popWindow.setTouchable(true);
			popWindow.setFocusable(true);
			// 弹出位置
			// popWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, 0, 0);
			int xoffInPixels = moreBtn.getWidth() / 2;
			popWindow.showAsDropDown(v, -DisplayUtil.px2dip(this, xoffInPixels), 0);

			// popWindow.update();
			ListView listView_popwindow = (ListView) popview.findViewById(R.id.listView_popwindow);
			// 单击item
			listView_popwindow.setOnItemClickListener(this);
			// 按返回或菜单键隐藏菜单
			listView_popwindow.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_BACK:
						case KeyEvent.KEYCODE_MENU:
							hidePopupWindow();
							break;

						}
					}
					return false;
				}
			});

			MorePopWindowAdapter adapter = new MorePopWindowAdapter(this, getPopWindowMenuData());
			listView_popwindow.setAdapter(adapter);

		}

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
		if (parent.getId() == R.id.listView_popwindow) {
			// 点击弹出窗上的item
			hidePopupWindow();
			// switch(position)
			// {
			// case 0://查看对方信息
			// openUserDetail(toUserId);
			// break;
			// case 1://清空聊天记录
			// cleanChatLog(toUserId);
			// break;
			// case 2://加入黑名单
			// addUserToBlackList(toUserId);
			// break;
			// case 3://举报该用户(目前功能没有)
			// ToastUtil.showToast(GroupMassMsgFragmentActivity.this,
			// "举报该用户(目前功能没有)");
			// break;
			// default:
			//
			// break;
			// }

		}
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
		ProxyFactory.getInstance().getMessageProxy().delMessage(MsgsConstants.MC_CHAT, touid, MsgsConstants.DOMAIN_USER, MsgsConstants.MCC_CHAT);
		chatFragment.refreshData();
	}

	/**
	 * 增加用户到黑名单
	 * 
	 * @param uid
	 */
	public void addUserToBlackList(long uid) {
		ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (result == Msgs.ErrorCode.EC_OK_VALUE) {
					//ToastUtil.showToast(GroupMassMsgFragmentActivity.this, "增加到黑名单成功", 1000);
					ErrorCodeUtil.handleErrorCode(GroupMassMsgFragmentActivity.this, result,null);
					// 同步用户
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_REL_USER, null);
				} else {
					//ToastUtil.showToast(GroupMassMsgFragmentActivity.this, "增加到黑名单失败：失败码：" + String.valueOf(result), 1000);
					ErrorCodeUtil.handleErrorCode(GroupMassMsgFragmentActivity.this, result,null);
				}
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				//ToastUtil.showToast(GroupMassMsgFragmentActivity.this, "增加黑名单异常：" + result, 1000);
				ErrorCodeUtil.handleErrorCode(GroupMassMsgFragmentActivity.this, result,resultMsg);
			}
		};
		ProxyFactory.getInstance().getUserProxy().addToBlackList(callback, this, uid);
	}

	/*
	 * 更多弹出菜单数据
	 */
	private ArrayList<HashMap<String, String>> getPopWindowMenuData() {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> item = new HashMap<String, String>();
		item.put(PopWindowAdapter.KEY_TITLE, getString(R.string.chat_userchat_menu_openuserdetail));
		// item.put(PopWindowAdapter.KEY_RESID,
		// String.valueOf(R.drawable.news_popwindow_img_all));
		data.add(item);
		item = new HashMap<String, String>();
		item.put(PopWindowAdapter.KEY_TITLE, getString(R.string.chat_userchat_menu_cleanchatLog));
		// item.put(PopWindowAdapter.KEY_RESID,
		// String.valueOf(R.drawable.news_popwindow_img_game));
		data.add(item);
		item = new HashMap<String, String>();
		item.put(PopWindowAdapter.KEY_TITLE, getString(R.string.chat_userchat_menu_adduserToblacklist));
		// item.put(PopWindowAdapter.KEY_RESID,
		// String.valueOf(R.drawable.news_popwindow_img_user));
		data.add(item);
		item = new HashMap<String, String>();
		item.put(PopWindowAdapter.KEY_TITLE, getString(R.string.chat_userchat_menu_report));
		// item.put(PopWindowAdapter.KEY_RESID,
		// String.valueOf(R.drawable.news_popwindow_img_chat));
		data.add(item);

		return data;
	}

	private void hidePopupWindow() {
		if (popWindow != null) {
			popWindow.dismiss();
		}
	}

}
