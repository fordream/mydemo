/**      
 * TopicListRightMenuFragment.java Create on 2014-4-16     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.postbar.adapter.ChildItem;
import com.iwgame.msgs.module.postbar.adapter.TopicRightMenuExpandableListAdapter;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.GameUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: TopicListRightMenuFragment
 * @Description: 帖子列表右边的菜单
 * @author chuanglong
 * @date 2014-4-16 下午3:12:26
 * @Version 1.0
 * 
 */
public class MainTopicListRightMenuFragment extends BaseFragment {
	private static final String TAG = "MainTopicListRightMenuFragment";

	TopicRightMenuExpandableListAdapter expandableListAdapter;
	ExpandableListView expandableListView;
	List<String> listDataHeader;
	Map<String, List<ChildItem>> listDataChild;
	String[] tags;

	private List<TopicTagVo> taglist;
	private long gid;

	private GameTopicListActivity gameTopicListActivity;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 获得传入的参数
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			gid = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
		}
		final View v = inflater.inflate(R.layout.postbar_topiclist_right_menu_main, container, false);
		ProxyFactory.getInstance().getPostbarProxy().getTopicTags(new ProxyCallBack<List<TopicTagVo>>() {

			@Override
			public void onSuccess(List<TopicTagVo> result) {
				taglist = result;
				// 初始化界面
				init(v);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// 初始化界面
				init(v);
			}
		}, gid);
		gameTopicListActivity = (GameTopicListActivity) getActivity();
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictModeWrapper.init(gameTopicListActivity);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void init(View view) {
		// 搜索菜单
		LinearLayout postbar_topiclist_right_menu_search = (LinearLayout) view.findViewById(R.id.postbar_topiclist_right_menu_search);
		postbar_topiclist_right_menu_search.setVisibility(View.VISIBLE);
		postbar_topiclist_right_menu_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (gameTopicListActivity != null)
					gameTopicListActivity.openSearchUI();
			}
		});

		TextView searchTxt = (TextView) postbar_topiclist_right_menu_search.findViewById(R.id.searchTxt);
		searchTxt.setText("在本贴吧搜索");

		expandableListView = (ExpandableListView) view.findViewById(R.id.expdlv);
		expandableListView.setGroupIndicator(null);
		prepareListData();
		expandableListAdapter = new TopicRightMenuExpandableListAdapter(gameTopicListActivity, listDataHeader, listDataChild);
		expandableListView.setAdapter(expandableListAdapter);
		// 全部默认展开
		for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
			expandableListView.expandGroup(i);
		}
		// 设置组点击，不响应
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});

		// 组展开响应
		expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
			}
		});

		// 组折叠响应
		expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
			}
		});

		// 子元素单击响应
		expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				List<ChildItem> arr = listDataChild.get(listDataHeader.get(groupPosition));
				if (groupPosition == 0) {
					// 点击tag
					if (!arr.get(childPosition).isSelect) {
						for (int i = 0; i < arr.size(); i++) {
							if (i == childPosition) {
								arr.get(i).isSelect = true;
								gameTopicListActivity.actionReloadDataByTag(arr.get(childPosition));
							} else {
								arr.get(i).isSelect = false;
							}
						}
					}

				} else if (groupPosition == 1) {
					ChildItem item = arr.get(childPosition);
					if (childPosition == 3) {
						if (item.itemName.equals("关注")) {
							// 关注
							followGame(gameTopicListActivity, MsgsConstants.OP_FOLLOW, item);
						} else {
							// 取消关注
							followGame(gameTopicListActivity, MsgsConstants.OP_UNFOLLOW, item);
						}
					} else {
						// 点击more
						gameTopicListActivity.actionMore(item.itemName);
					}
				}
				expandableListAdapter.notifyDataSetChanged();
				return true;
			}
		});
	}

	/**
	 * 准备列表数据
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<ChildItem>>();
		ChildItem childitem;

		listDataHeader.add(SystemContext.getInstance().getContext().getString(R.string.postbar_topic_right_menu_tags));
		listDataHeader.add(SystemContext.getInstance().getContext().getString(R.string.postbar_topic_right_menu_more));

		// 分类
		List<ChildItem> tmptags = new ArrayList<ChildItem>();
		childitem = new ChildItem();
		childitem.itemName = SystemContext.getInstance().getContext().getString(R.string.postbar_topic_right_menu_tags_all);
		childitem.isSelect = true;
		childitem.tagId = 0;
		tmptags.add(childitem);
		childitem = new ChildItem();
		childitem.itemName = SystemContext.getInstance().getContext().getString(R.string.postbar_topic_right_menu_tags_essence);
		childitem.isSelect = false;
		childitem.tagId = 0;
		childitem.essence = 1;
		tmptags.add(childitem);
		if (taglist != null) {
			int size = taglist.size();
			for (int i = 0; i < size; i++) {
				TopicTagVo tag = taglist.get(i);
				if ((tag.getAccess() & 0x01) == 0x01 && tag.getId() != 1 && tag.getId() != 2) {
					childitem = new ChildItem();
					childitem.itemName = tag.getName();
					childitem.isSelect = false;
					childitem.tagId = tag.getId();
					tmptags.add(childitem);
				}
			}
		}
		// 更多more
		List<ChildItem> tmpmore = new ArrayList<ChildItem>();
		childitem = new ChildItem();
		childitem.itemName = SystemContext.getInstance().getContext().getString(R.string.postbar_topic_right_menu_more_gamedetail);
		childitem.isSelect = false;
		tmpmore.add(childitem);
		childitem = new ChildItem();
		childitem.itemName = SystemContext.getInstance().getContext().getString(R.string.postbar_topic_right_menu_more_recommend);
		childitem.isSelect = false;
		tmpmore.add(childitem);
		childitem = new ChildItem();
		childitem.itemName = SystemContext.getInstance().getContext().getString(R.string.postbar_topic_right_menu_more_master);
		childitem.isSelect = false;
		tmpmore.add(childitem);
		childitem = new ChildItem();
		if (gameTopicListActivity != null) {
			boolean follow = gameTopicListActivity.getFollow();
			if (follow)
				childitem.itemName = "已关注";
			else
				childitem.itemName = "关注";
		}
		childitem.isSelect = false;
		if (AdaptiveAppContext.getInstance().getAppConfig().getGameId() == 0) {
			tmpmore.add(childitem);
		}
		

		listDataChild.put(listDataHeader.get(0), tmptags);
		listDataChild.put(listDataHeader.get(1), tmpmore);
	}

	public void resetSelectMenu() {
		List<ChildItem> arr = listDataChild.get(listDataHeader.get(0));
		for (int i = 0; i < arr.size(); i++) {
			if (i == 0) {
				arr.get(i).isSelect = true;
			} else {
				arr.get(i).isSelect = false;
			}
		}
		expandableListAdapter.notifyDataSetChanged();
	}

	public void setFollow(boolean followed) {
		if (listDataChild != null && listDataHeader != null && listDataHeader.get(1) != null) {
			List<ChildItem> arr = listDataChild.get(listDataHeader.get(1));
			ChildItem item = arr.get(3);
			if (followed)
				item.itemName = "已关注";
			else
				item.itemName = "关注";
			expandableListAdapter.notifyDataSetChanged();
		}
	}

	private void followGame(final Context context, final int op, final ChildItem item) {
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (context != null) {
					switch (result) {
					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
						if (MsgsConstants.OP_FOLLOW == op) {
							item.itemName = "已关注";
							if (gameTopicListActivity != null)
								gameTopicListActivity.setFollow(true);
							ToastUtil.showToast(context, "关注成功");
						} else if (MsgsConstants.OP_UNFOLLOW == op) {
							item.itemName = "关注";
							if (gameTopicListActivity != null)
								gameTopicListActivity.setFollow(false);
							ToastUtil.showToast(context, "取消关注成功");
						}
						expandableListAdapter.notifyDataSetChanged();
						ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, null);
						break;
					default:
						break;
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

			}
		}, context, gid, MsgsConstants.OT_GAME, op, null, null, null);
	}

	/**
	 * 
	 * @param tagName
	 * @param tagId
	 */
	public void setSelectTag(String tagName, int tagId) {
		if (listDataHeader != null && expandableListAdapter != null) {
			String h = listDataHeader.get(0);
			if (h != null) {
				List<ChildItem> childs = listDataChild.get(listDataHeader.get(0));
				if (childs != null) {
					int size = childs.size();
					for (int i = 0; i < size; i++) {
						ChildItem item = childs.get(i);
						if (item != null && item.tagId == tagId) {
							item.isSelect = true;
						} else {
							item.isSelect = false;
						}
					}
					expandableListAdapter.notifyDataSetChanged();
				}
			}
		}
	}
}
