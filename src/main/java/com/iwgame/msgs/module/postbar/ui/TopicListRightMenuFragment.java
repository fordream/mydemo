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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;

import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.module.postbar.adapter.ChildItem;
import com.iwgame.msgs.module.postbar.adapter.TopicRightMenuExpandableListAdapter;
import com.iwgame.msgs.utils.MsgsConstants;

/**
 * @ClassName: TopicListRightMenuFragment
 * @Description: 帖子列表右边的菜单
 * @author chuanglong
 * @date 2014-4-16 下午3:12:26
 * @Version 1.0
 * 
 */
public class TopicListRightMenuFragment extends BaseFragment {
	private static final String TAG = "TopicListRightMenuFragment";

	private LayoutInflater inflater;

	TopicRightMenuExpandableListAdapter expandableListAdapter;
	ExpandableListView expandableListView;
	List<String> listDataHeader;
	Map<String, List<ChildItem>> listDataChild;
	String[] tags;

	int selectOrder = 0;
	String selectTag = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		// 获得传入的参数
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			tags = tmpbundle.getStringArray(SystemConfig.BUNDLE_NAME_TOPIC_TAGS);
		}
		View v = inflater.inflate(R.layout.postbar_topiclist_right_menu_main, container, false);
		// 初始化界面
		init(v);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictModeWrapper.init(this.getActivity());
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
		//搜索菜单
		LinearLayout postbar_topiclist_right_menu_search = (LinearLayout)view.findViewById(R.id.postbar_topiclist_right_menu_search);
		postbar_topiclist_right_menu_search.setVisibility(View.VISIBLE);
		postbar_topiclist_right_menu_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getActivity() instanceof GameTopicListActivity)
					((GameTopicListActivity) getActivity()).openSearchUI();

			}
		});

		expandableListView = (ExpandableListView) view.findViewById(R.id.expdlv);
		expandableListView.setGroupIndicator(null);
		prepareListData();
		expandableListAdapter = new TopicRightMenuExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
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
					// 点击排序
					if (!arr.get(childPosition).isSelect) {
						if (childPosition == 0) {
							arr.get(0).isSelect = true;
							arr.get(1).isSelect = false;
							selectOrder = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;
						} else if (childPosition == 1) {
							arr.get(0).isSelect = false;
							arr.get(1).isSelect = true;
							selectOrder = MsgsConstants.POSTBAR_ORDER_CREATE_TIME;
						}
						if (getActivity() instanceof GameTopicListActivity)
							((GameTopicListActivity) getActivity()).actionReloadData(selectOrder, selectTag);
					}

				} else if (groupPosition == 1) {
					// 点击tag
					if (!arr.get(childPosition).isSelect) {
						for (int i = 0; i < arr.size(); i++) {
							if (i == childPosition) {
								arr.get(i).isSelect = true;
							} else {
								arr.get(i).isSelect = false;
							}
						}
						selectTag = arr.get(childPosition).itemName;
						if (getActivity() instanceof GameTopicListActivity)
							((GameTopicListActivity) getActivity()).actionReloadData(selectOrder, selectTag);
					}

				} else if (groupPosition == 2) {
					if (getActivity() instanceof GameTopicListActivity)
						((GameTopicListActivity) getActivity()).actionMore(arr.get(childPosition).itemName);
				}

				expandableListAdapter.notifyDataSetChanged();
				return true;
			}
		});

	}

	private void prepareListData() {
		selectOrder = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;
		selectTag = getString(R.string.postbar_topic_right_menu_tags_all);

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<ChildItem>>();
		ChildItem childitem;

		// Adding header data
		listDataHeader.add(getString(R.string.postbar_topic_right_menu_order));
		listDataHeader.add(getString(R.string.postbar_topic_right_menu_tags));
		listDataHeader.add(getString(R.string.postbar_topic_right_menu_more));

		// Adding child data
		// 排序
		List<ChildItem> order = new ArrayList<ChildItem>();
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_right_menu_order_reply_time);
		childitem.isSelect = true;
		order.add(childitem);
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_right_menu_order_create_time);
		childitem.isSelect = false;
		order.add(childitem);

		// 分类
		List<ChildItem> tmptags = new ArrayList<ChildItem>();
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_right_menu_tags_all);
		childitem.isSelect = true;
		tmptags.add(childitem);
		if (tags != null) {
			for (int i = 0; i < tags.length; i++) {
				childitem = new ChildItem();
				childitem.itemName = tags[i];
				childitem.isSelect = false;
				tmptags.add(childitem);
			}
		}
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_right_menu_tags_essence);
		childitem.isSelect = false;
		tmptags.add(childitem);
		// 更多more
		List<ChildItem> tmpmore = new ArrayList<ChildItem>();
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_right_menu_more_gamedetail);
		childitem.isSelect = false;
		tmpmore.add(childitem);
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_right_menu_more_recommend);
		childitem.isSelect = false;
		tmpmore.add(childitem);
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_right_menu_more_master);
		childitem.isSelect = false;
		tmpmore.add(childitem);

		listDataChild.put(listDataHeader.get(0), order);
		listDataChild.put(listDataHeader.get(1), tmptags);
		listDataChild.put(listDataHeader.get(2), tmpmore);
	}

	/**
	 * 更新tags
	 * @param tags
	 */
	public void updateTags(String[] tags)
	{
		this.tags = tags;
		if(isAdded()){
			List<ChildItem> tmptags = listDataChild.get(listDataHeader.get(1));
			tmptags.clear();
			//全部帖子
			ChildItem childitem = new ChildItem();
			childitem.itemName = getString(R.string.postbar_topic_right_menu_tags_all);
			childitem.isSelect = true;
			tmptags.add(childitem);

			if (tags != null) {
				for (int i = 0; i < tags.length; i++) {
					childitem = new ChildItem();
					childitem.itemName = tags[i];
					childitem.isSelect = false;
					tmptags.add(childitem);
				}
			}
			//精华
			childitem = new ChildItem();
			childitem.itemName = getString(R.string.postbar_topic_right_menu_tags_essence);
			childitem.isSelect = false;
			tmptags.add(childitem);

			expandableListAdapter.notifyDataSetChanged();
		}

	}

}
