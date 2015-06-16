/**      
 * GameTopicListActivity.java Create on 2014-4-16     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.BaseSlidingFragmentActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.game.ui.GameDetailInfoFragmentActivity;
import com.iwgame.msgs.module.game.ui.RecommendGame2FriendActivity;
import com.iwgame.msgs.module.postbar.adapter.ChildItem;
import com.iwgame.msgs.module.user.adapter.UserAdapter2;
import com.iwgame.msgs.module.user.ui.UserListActicity;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.youban.msgs.R;

/**
 * @ClassName: GameTopicListActivity
 * @Description: 贴吧的帖子首页
 * @author chuanglong
 * @date 2014-4-16 上午11:25:42
 * @Version 1.0
 * 
 */
public class GameTopicListActivity extends BaseSlidingFragmentActivity {

	private long gid;

	private MainTopicListFragment topicListFragment;
	private MainTopicListRightMenuFragment menuFragment;
	public String pageFrom;
	/**
	 * tags
	 */
	private List<TopicTagVo> tags;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 获得传入的参数
		Intent tmpintent = this.getIntent();
		if (tmpintent != null) {
			Bundle tmpbundle = tmpintent.getBundleExtra(SystemConfig.BUNDLEEXTRA_NAME);
			if (tmpbundle != null) {
				gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, 0);
				if (tmpbundle.containsKey("From")) {
					pageFrom = tmpbundle.getString("From");
				}
			}
		}

		setContentView(R.layout.content_frame);
		init();

	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.common.BaseSlidingFragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		SystemContext.getInstance().setCurrentActivity(this);
	}
	
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		SystemContext.getInstance().setCurrentActivity(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void init() {
		topicListFragment = new MainTopicListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETTYPE, SystemConfig.GETTOPICLIST_TARGETTYPE_GAME);
		bundle.putLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID, gid);
		bundle.putString("From", pageFrom);
		bundle.putSerializable(SystemConfig.BUNDLE_NAME_TOPIC_TAGS, (Serializable) tags);
		topicListFragment.setArguments(bundle);

		getSlidingMenu().setMode(SlidingMenu.RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, topicListFragment).commitAllowingStateLoss();// .commit();

		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.common_more_menu);

		getTopicTags();
	}

	public void getTopicTags() {
		// 获得tag
		ProxyCallBack<List<TopicTagVo>> callback = new ProxyCallBack<List<TopicTagVo>>() {

			@Override
			public void onSuccess(List<TopicTagVo> result) {
				tags = result;

				// if(menuFragment != null){
				// menuFragment.updateTags(getTagsName());
				// }

				List<TopicTagVo> tmptags = new ArrayList<TopicTagVo>();
				for (int i = 0; i < tags.size(); i++) {
					TopicTagVo tag = tags.get(i);
					// 对访问权限和标签id为1新闻&2攻略的过滤
					if ((tag.getAccess() & 0x02) == 0x02 && tag.getId() != 1 && tag.getId() != 2) {
						tmptags.add(tag);
					}
				}

				if (topicListFragment != null) {
					topicListFragment.setTags(tmptags);
				}
				menuFragment = new MainTopicListRightMenuFragment();
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
				menuFragment.setArguments(bundle);
				if (!GameTopicListActivity.this.isFinishing())
					getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, menuFragment).commitAllowingStateLoss();// .commit();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				menuFragment = new MainTopicListRightMenuFragment();
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
				menuFragment.setArguments(bundle);
				if (!GameTopicListActivity.this.isFinishing())
					getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, menuFragment).commitAllowingStateLoss();// .commit();
			}
		};
		ProxyFactory.getInstance().getPostbarProxy().getTopicTags(callback, gid);
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
	 * 通过点击菜单打开搜索界面
	 */
	public void openSearchUI() {
		hideMenu();
		Intent intent = new Intent(this, SearchTopicActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		startActivity(intent);
	}

	/**
	 * 通过点击菜单重新加载数据
	 * 
	 * @param order
	 * @param tag
	 */
	public void actionReloadData(int order, String tag) {
		hideMenu();
		int tagId = getTagIdByName(tag);
		if (tagId == SystemConfig.POSTBAR_TOPIC_TAG_ESSENCE) {
			topicListFragment.actionReloadData(order, SystemConfig.POSTBAR_TOPIC_TAG_ALL, MsgsConstants.POSTBAR_TOPIC_FILTER_ESSENCE);
		} else {
			topicListFragment.actionReloadData(order, tagId, 0);
		}

	}

	/**
	 * 
	 * @param order
	 * @param tag
	 */
	public void actionReloadDataByTag(ChildItem item) {
		hideMenu();
		topicListFragment.loadDataClear(item);
	}

	public void actionReloadData(int tagId) {
		if (tagId == SystemConfig.POSTBAR_TOPIC_TAG_ESSENCE) {
			topicListFragment.actionReloadData(SystemConfig.POSTBAR_TOPIC_TAG_ALL, MsgsConstants.POSTBAR_TOPIC_FILTER_ESSENCE);
		} else {
			topicListFragment.actionReloadData(tagId, 0);
		}

	}

	public void actionReloadData() {
		topicListFragment.actionReloadData();
	}

	public void setTags(List<TopicTagVo> tags) {
		topicListFragment.setTags(tags);
	}

	/**
	 * 通过点击菜单 操作more
	 * 
	 * @param itemname
	 */
	public void actionMore(String itemname) {
		hideMenu();
		// 点击more
		if (itemname.equals(getString(R.string.postbar_topic_right_menu_more_gamedetail))) {
			// 详情
			Intent intent = new Intent(this, GameDetailInfoFragmentActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			startActivity(intent);

		} else if (itemname.equals(getString(R.string.postbar_topic_right_menu_more_recommend))) {
			// 推荐
			Intent intent = new Intent(this, RecommendGame2FriendActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
			intent.putExtras(bundle);
			startActivity(intent);
		} else if (itemname.equals(getString(R.string.postbar_topic_right_menu_more_master))) {

			// 吧主
			Intent intent = new Intent(this, UserListActicity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TYPE, UserAdapter2.TYPE_POSTBAR_MANAGER);
			intent.putExtras(bundle);
			startActivity(intent);

		}
	}

	/**
	 * 绑定手机提示框
	 * 
	 * @param actionName
	 */
	private void createBundPhoneDialog() {
		Intent intent = new Intent(GameTopicListActivity.this, BundPhoneActivity.class);
		startActivity(intent);
	}

	/**
	 * 通过tag名称获得tagid
	 * 
	 * @param name
	 * @return
	 */
	private int getTagIdByName(String name) {
		if (name.equals(getString(R.string.postbar_topic_right_menu_tags_all))) {
			return SystemConfig.POSTBAR_TOPIC_TAG_ALL;
		} else if (name.equals(getString(R.string.postbar_topic_right_menu_tags_essence))) {
			return SystemConfig.POSTBAR_TOPIC_TAG_ESSENCE;
		}
		for (int i = 0; i < tags.size(); i++) {
			if (tags.get(i).getName().equals(name)) {

				return tags.get(i).getId();
			}
		}
		return 0;
	}

	/**
	 * 获得标签名称数组
	 */
	private String[] getTagsName() {
		String[] tmp = null;
		if (tags != null) {
			List<TopicTagVo> tmptags = new ArrayList<TopicTagVo>();
			for (int i = 0; i < tags.size(); i++) {
				if ((tags.get(i).getAccess() & 0x01) == 0x01) {
					tmptags.add(tags.get(i));
				}
			}
			tmp = new String[tmptags.size()];
			for (int i = 0; i < tmptags.size(); i++) {
				tmp[i] = tmptags.get(i).getName();
			}
		}

		return tmp;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (pageFrom != null && pageFrom.equals("GameRegiestRecommendActivity")) {
				jumpMainView();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	/**
	 * 跳转到主界面
	 */
	private void jumpMainView() {
		Intent intent = new Intent(this, MainFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
		intent.putExtras(bundle);
		startActivity(intent);
		this.finish();
	}

	public void resetSelectMenu() {
		if (menuFragment != null)
			menuFragment.resetSelectMenu();
	}

	public void setFollow(boolean followed) {
		hideMenu();
		if (topicListFragment != null)
			topicListFragment.setFollow(followed);
	}

	public boolean getFollow() {
		if (topicListFragment != null)
			return topicListFragment.getFollow();
		return false;
	}

	public void setFollowCallMenu(boolean followed) {
		if (menuFragment != null)
			menuFragment.setFollow(followed);
	}

	public void setMenuSelectTag(String tagName, int tagId) {
		if (menuFragment != null)
			menuFragment.setSelectTag(tagName, tagId);
	}

}
