/**      
 * TopicDetailActivity.java Create on 2013-12-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.iwgame.msgs.common.BaseSlidingFragmentActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.GuideUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ToastUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: TopicDetailActivity
 * @Description: 帖子详情页
 * @author chuanglong
 * @date 2013-12-24 上午11:57:28
 * @Version 1.0
 * 
 */
public class TopicDetailActivity extends BaseSlidingFragmentActivity {

	/**
	 * 帖子id
	 */
	private long topicId;

	/**
	 * 贴吧id
	 */
	private long gid;
	/** 其他界面跳转到此界面的模式 **/
	private int mode = TopicDetailFragment.MODE_GENERAL;

	private int pmode = TopicReplyListAdapter.MODE_POSTBAR_UNSHOW;
	
	private String gamename;

	/**
	 * 回复我的的回复，默认为空，只有从回复我的界面过滤才有该数据
	 */
	Msgs.PostbarTopicReplyDetail replyMyReply = null;

	TopicDetailFragment topicDetailFragment;
	TopicDetailRightMenuFragment menuFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		GuideUtil.startGuide(this, GuideActivity.GUIDE_MODE_POST_SHARE);
		// 获得传入的参数
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				topicId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID);
				gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
				mode = tmpbundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE);
				pmode = tmpbundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POSTBAR_DETAIL_MODE);
				gamename = tmpbundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME);
				replyMyReply = (Msgs.PostbarTopicReplyDetail) tmpbundle.getSerializable(SystemConfig.BUNDLE_NAME_TOPICDETAIL_REPLYMYREPLY);
			}
		}

		setContentView(R.layout.content_frame);
		init();

		HashMap<String, String> ummap = new HashMap<String, String>();
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(SystemContext.getInstance().getExtUserVo().getUserid()));
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, SystemContext.getInstance().getExtUserVo().getUsername());
		ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(topicId));
		ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_ID, String.valueOf(gid));
		GameVo gvo = DaoFactory.getDaoFactory().getGameDao(this).getGameById(gid);
		if (gvo != null) {
			ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, gvo.getGamename() + "贴吧");
			ummap.put(UMConfig.MSGS_OPT_FROM_OBJ2_NAME, gvo.getGamename());
		}
		MobclickAgent.onEvent(this, UMConfig.MSGS_EVENT_GAME_POSTBAR_VIEW, ummap);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseSlidingFragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		//SystemContext.getInstance().setCurrentActivity(this);
	}
	
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void init() {
		menuFragment = new TopicDetailRightMenuFragment();

		topicDetailFragment = new TopicDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, topicId);
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, mode);
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POSTBAR_DETAIL_MODE, pmode);
		bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, gamename);
		bundle.putSerializable(SystemConfig.BUNDLE_NAME_TOPICDETAIL_REPLYMYREPLY, replyMyReply);

		topicDetailFragment.setArguments(bundle);

		getSlidingMenu().setMode(SlidingMenu.RIGHT);
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, topicDetailFragment).commit();

		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.common_more_menu);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, menuFragment).commit();

	}

	/**
	 * 提供给fragment使用 ,显示菜单
	 */
	public void showMenu() {
		getSlidingMenu().showMenu(true);
	}

	public void hideMenu() {
		getSlidingMenu().showContent();
	}

	/**
	 * 设置给菜单是否收藏
	 * 
	 * @param flag
	 */
	public void setIsFavAndRefreshMenu(boolean flag) {
		menuFragment.setIsFavAndRefreshMenu(flag);
	}

	/**
	 * 设置菜单当前的过滤条件
	 * 
	 * @param filter
	 */
	public void setFilterRefreshMenu(int filter) {
		menuFragment.setFilterRefreshMenu(filter);
	}

	public void actionMenu(String name) {
		hideMenu();
		if (name.equals(getString(R.string.postbar_topic_detail_right_menu_action_favorite))) {
			// 收藏帖子
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
			dialog.show();
			ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					setIsFavAndRefreshMenu(true);
					ToastUtil.showToast(TopicDetailActivity.this, getResources().getString(R.string.postbar_topic_detail_rightmenu_favorite_ok));
					UMUtil.sendEvent(TopicDetailActivity.this, UMConfig.MSGS_EVENT_BAR_COLLECT, null, null, String.valueOf(topicId), null, true);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					ErrorCodeUtil.handleErrorCode(TopicDetailActivity.this, result, null);
					UMUtil.sendEvent(TopicDetailActivity.this, UMConfig.MSGS_EVENT_BAR_COLLECT, null, null, String.valueOf(topicId), null, false);
				}

			};

			ProxyFactory.getInstance().getPostbarProxy()
					.actionTopic(callback, TopicDetailActivity.this, topicId, MsgsConstants.OP_ADD_TOPIC_FAVORITE, null);
		} else if (name.equals(getString(R.string.postbar_topic_detail_right_menu_action_cancelfavorite))) {
			// 取消收藏帖子
			final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
			dialog.show();
			ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					setIsFavAndRefreshMenu(false);
					ToastUtil
							.showToast(TopicDetailActivity.this, getResources().getString(R.string.postbar_topic_detail_rightmenu_cancelfavorite_ok));
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					ErrorCodeUtil.handleErrorCode(TopicDetailActivity.this, result, resultMsg);
				}

			};

			ProxyFactory.getInstance().getPostbarProxy()
					.actionTopic(callback, TopicDetailActivity.this, topicId, MsgsConstants.OP_CANCEL_TOPIC_FAVORITE, null);
		} else if (name.equals(getString(R.string.postbar_topic_detail_right_menu_action_share))) {
			// 分享
			topicDetailFragment.shareTopic();
		} else if (name.equals(getString(R.string.postbar_topic_detail_right_menu_action_report))) {
			// 举报
			topicDetailFragment.reportTopic();
		} else if (name.equals(getString(R.string.postbar_topic_detail_right_menu_page_page))) {
			// 翻页
			topicDetailFragment.selectPage();
		} else if (name.equals(getString(R.string.postbar_topic_detail_right_menu_filter_all))) {
			// 过滤显示全部
			topicDetailFragment.setFilter(0);
		} else if (name.equals(getString(R.string.postbar_topic_detail_right_menu_filter_onlyposter))) {
			// 只显示吧主
			topicDetailFragment.setFilter(MsgsConstants.POSTBAR_REPLY_FILTER_POSTER_ONLY);
		}
	}

	/**
	 * 当 SSO 授权 Activity 退出时，该函数被调用。
	 * 
	 * @see {@link Activity#onActivityResult}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// sina weibo SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
		// if (topicDetailFragment.getSinaSsoHandler() != null) {
		// topicDetailFragment.getSinaSsoHandler().authorizeCallBack(requestCode,
		// resultCode, data);
		// }
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
			if (topicDetailFragment.getSendMsgView() != null && topicDetailFragment.getSendMsgView().hideSmileyView())
				return true;
			if (topicDetailFragment.isBack())
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
