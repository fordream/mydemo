/**      
 * TopicDetailRightMenuFragment.java Create on 2014-4-18     
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
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.youban.msgs.R;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.module.postbar.adapter.ChildItem;
import com.iwgame.msgs.module.postbar.adapter.TopicRightMenuExpandableListAdapter;
import com.iwgame.msgs.utils.MsgsConstants;

/**
 * @ClassName: TopicDetailRightMenuFragment
 * @Description: TODO(帖子详情右边的菜单列表)
 * @author chuanglong
 * @date 2014-4-18 下午4:00:19
 * @Version 1.0
 * 
 */
public class TopicDetailRightMenuFragment extends BaseFragment {
	private static final String TAG = "TopicListRightMenuFragment";

	private LayoutInflater inflater;

	TopicRightMenuExpandableListAdapter expandableListAdapter;
	ExpandableListView expandableListView;
	List<String> listDataHeader;
	Map<String, List<ChildItem>> listDataChild;
	String[] tags;

	String selectFilter = "";
	boolean isFav = false;

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
		}
		View v = inflater.inflate(R.layout.postbar_topiclist_right_menu_main, container, false);
		// 初始化界面
		init(v);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		StrictModeWrapper.init(this.getActivity());
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	private void init(View view) {

		expandableListView = (ExpandableListView) view.findViewById(R.id.expdlv);
		expandableListView.setGroupIndicator(null);
		// preparing list data
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
				// TODO Auto-generated method stub
				return true;
			}
		});

		// 组展开响应
		expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// Toast.makeText(getApplicationContext(), "展开:" +
				// listDataHeader.get(groupPosition), Toast.LENGTH_LONG).show();
			}
		});

		// 组折叠响应
		expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// Toast.makeText(getApplicationContext(), "折叠:" +
				// listDataHeader.get(groupPosition), Toast.LENGTH_LONG).show();
			}
		});

		// 子元素单击响应
		expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				List<ChildItem> arr = listDataChild.get(listDataHeader.get(groupPosition));
				if (groupPosition == 0) {
					// 点击过滤
					if (!arr.get(childPosition).isSelect) {
						if (childPosition == 0) {
							arr.get(0).isSelect = true;
							arr.get(1).isSelect = false;

						} else if (childPosition == 1) {
							arr.get(0).isSelect = false;
							arr.get(1).isSelect = true;
						}
					}
				}
				((TopicDetailActivity) getActivity()).actionMenu(arr.get(childPosition).itemName);

				expandableListAdapter.notifyDataSetChanged();
				return true;
			}
		});

	}

	private void prepareListData() {

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<ChildItem>>();
		ChildItem childitem;
		// Adding header data
		listDataHeader.add(getString(R.string.postbar_topic_detail_right_menu_filter));
		listDataHeader.add(getString(R.string.postbar_topic_detail_right_menu_page));
		listDataHeader.add(getString(R.string.postbar_topic_detail_right_menu_action));

		// Adding child data
		// 过滤条件
		List<ChildItem> filter = new ArrayList<ChildItem>();
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_detail_right_menu_filter_all);
		childitem.isSelect = true;
		filter.add(childitem);
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_detail_right_menu_filter_onlyposter);
		childitem.isSelect = false;
		filter.add(childitem);

		// 翻页
		List<ChildItem> page = new ArrayList<ChildItem>();
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_detail_right_menu_page_page);
		childitem.isSelect = false;
		page.add(childitem);

		// 操作
		List<ChildItem> action = new ArrayList<ChildItem>();
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_detail_right_menu_action_favorite);
		childitem.isSelect = false;
		action.add(childitem);

		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_detail_right_menu_action_share);
		childitem.isSelect = false;
		action.add(childitem);
		childitem = new ChildItem();
		childitem.itemName = getString(R.string.postbar_topic_detail_right_menu_action_report);
		childitem.isSelect = false;
		action.add(childitem);

		listDataChild.put(listDataHeader.get(0), filter);
		listDataChild.put(listDataHeader.get(1), page);
		listDataChild.put(listDataHeader.get(2), action);
	}

	/**
	 * 设置收藏菜单
	 * 
	 * @param flag
	 */
	public void setIsFavAndRefreshMenu(boolean flag) {
		isFav = flag;
		// 重新设置收藏，取消收藏菜单
		if (!isFav) {
			listDataChild.get(listDataHeader.get(2)).get(0).itemName = getString(R.string.postbar_topic_detail_right_menu_action_favorite);
		} else {
			listDataChild.get(listDataHeader.get(2)).get(0).itemName = getString(R.string.postbar_topic_detail_right_menu_action_cancelfavorite);
		}
		expandableListAdapter.notifyDataSetChanged();
	}

	/**
	 * 设置过滤条件的选择
	 * 
	 * @param filter
	 */
	public void setFilterRefreshMenu(int filter) {
		if (filter == MsgsConstants.POSTBAR_REPLY_FILTER_POSTER_ONLY) {
			listDataChild.get(listDataHeader.get(0)).get(0).isSelect = false;
			listDataChild.get(listDataHeader.get(0)).get(1).isSelect = true;
		} else {
			listDataChild.get(listDataHeader.get(0)).get(0).isSelect = true;
			listDataChild.get(listDataHeader.get(0)).get(1).isSelect = false;
		}
		expandableListAdapter.notifyDataSetChanged();
	}

}
