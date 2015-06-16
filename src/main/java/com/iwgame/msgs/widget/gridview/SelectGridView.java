/**      
 * RadioGridView.java Create on 2013-12-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.widget.gridview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.youban.msgs.R;
import com.iwgame.msgs.widget.gridview.adapter.SelectGridAdapter;

/**
 * @ClassName: RadioGridView
 * @Description: 支持单选的网格布局
 * @author 王卫
 * @date 2013-12-24 下午3:51:04
 * @Version 1.0
 * 
 */
public class SelectGridView extends LinearLayout {

	public static final String ITEM_NAME = "name";
	public static final String ITEM_CHECK = "check";
	public static final String ITEM_ALL = "all";
	public static final String ITEM_DISABLE = "disable";

	public static final String OVER_BG_WHITE = "white";
	public static final String OVER_BG_GRAY = "gray";

	private GridView gridView;

	private ImageView overBgView;

	private SelectGridAdapter adapter;

	private OnItemClickListener mItemClickListener;

	// 适配器数据（[注]：map里面必须包含描述name键值,check键值可选，多选情况下可支持某个项目是否为所有，添加all键值）
	private List<Map<String, Object>> mItems;
	// 选中的条目
	private List<Map<String, Object>> mSelectItems;

	private boolean mIsRadio = true;

	private int mNumColumns;

	/**
	 * 
	 * @param context
	 *            上下文
	 * @param items
	 *            数据
	 * @param numColumns
	 *            列数
	 * @param isRadio
	 *            是否单选
	 * @param overBgType
	 *            边框背景类型
	 * @param listener
	 *            条目点击回调监听
	 */
	public SelectGridView(Context context, List<Map<String, Object>> items, Integer numColumns, boolean isRadio, String overBgType,
			OnItemClickListener listener) {
		super(context);
		mItems = items;
		mIsRadio = isRadio;
		mNumColumns = numColumns;
		mItemClickListener = listener;
		addLastItem();
		LinearLayout ll = (LinearLayout) View.inflate(context, R.layout.widget_gridview, null);
		gridView = (GridView) ll.findViewById(R.id.gridView);
		overBgView = (ImageView) ll.findViewById(R.id.overBgView);
		if (overBgType != null) {
			if (overBgType == OVER_BG_GRAY) {
				overBgView.setBackgroundResource(R.drawable.common_btn_stroke_shap);
			} else {
				overBgView.setBackgroundResource(R.drawable.common_btn_stroke_shap);
			}
		} else {
			overBgView.setBackgroundResource(R.drawable.common_btn_stroke_shap);
		}
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		adapter = new SelectGridAdapter(context, mItems, R.layout.widget_gridview_item, new String[] { ITEM_NAME }, new int[] { R.id.textView },
				mIsRadio, items, numColumns);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new ItemClickListener());
		if (numColumns != null) {
			gridView.setNumColumns(numColumns);
		}
		addView(ll, params);
	}

	/**
	 * 添加空白条目
	 */
	private void addLastItem() {
		int lnum = mNumColumns - mItems.size() % mNumColumns;
		if (lnum < mNumColumns) {
			for (int i = 0; i < lnum; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(ITEM_DISABLE, true);
				mItems.add(map);
			}
		}
	}

	/**
	 * 获取选中的条目数据
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getSelectedItems() {
		List<Map<String, Object>> selectedItems = new ArrayList<Map<String, Object>>();
		if (mIsRadio) {
			int selection = adapter.getSelection();
			if (selection >= 0) {
				selectedItems.add(mItems.get(selection));
			}
		} else {
			for (int i = 0; i < mItems.size(); i++) {
				if (mItems.get(i).containsKey(ITEM_CHECK) && (Boolean) mItems.get(i).get(ITEM_CHECK)) {
					selectedItems.add(mItems.get(i));
				}
			}
		}
		return selectedItems;
	}

	/**
	 * 
	 * @param pos
	 * @return
	 */
	public Map<String, Object> getData(int pos) {
		if (mItems != null && mItems.size() > pos)
			return mItems.get(pos);
		return null;
	}

	public SelectGridAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(SelectGridAdapter adapter) {
		this.adapter = adapter;
	}

	public GridView getGridView() {
		return gridView;
	}

	public void setGridView(GridView gridView) {
		this.gridView = gridView;
	}

	public void setSelection(int position) {
		adapter.setSelection(position);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * @ClassName: ItemClickListener
	 * @Description: TODO(...)
	 * @author 王卫
	 * @date 2013-12-24 下午5:31:31
	 * @Version 1.0
	 * 
	 */
	public class ItemClickListener implements OnItemClickListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.widget.AdapterView.OnItemClickListener#onItemClick(android
		 * .widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (mItems.get(position).containsKey(ITEM_DISABLE) && (Boolean) mItems.get(position).get(ITEM_DISABLE)) {
				return;
			} else {
				if (mIsRadio) {
					adapter.setSelection(position);
				} else {
					// 设置当前条目状态
					if (mItems.get(position).containsKey(ITEM_CHECK)) {
						if (mItems.get(position).containsKey(ITEM_ALL) && (Boolean) mItems.get(position).get(ITEM_ALL)) {
							mItems.get(position).put(ITEM_CHECK, true);
						} else {
							if ((Boolean) mItems.get(position).get(ITEM_CHECK))
								mItems.get(position).put(ITEM_CHECK, false);
							else
								mItems.get(position).put(ITEM_CHECK, true);
						}
					} else {
						mItems.get(position).put(ITEM_CHECK, true);
					}
					// 处理点击ALL条目时所有条目状态
					if (mItems.get(position).containsKey(ITEM_ALL) && (Boolean) mItems.get(position).get(ITEM_ALL)) {
						if ((Boolean) mItems.get(position).get(ITEM_CHECK)) {
							for (int i = 0; i < mItems.size(); i++) {
								if (i != position) {
									mItems.get(i).put(ITEM_CHECK, false);
								}
							}
						}
					} else {
						if ((Boolean) mItems.get(position).get(ITEM_CHECK)) {
							for (int i = 0; i < mItems.size(); i++) {
								if (i != position) {
									Map<String, Object> item = mItems.get(i);
									if (item.containsKey(ITEM_ALL) && (Boolean) item.get(ITEM_ALL)) {
										item.put(ITEM_CHECK, false);
									}
								}
							}
						} else {
							boolean checkAll = false;
							for (int i = 0; i < mItems.size(); i++) {
								Map<String, Object> item = mItems.get(i);
								if (item.containsKey(ITEM_CHECK) && (Boolean) item.get(ITEM_CHECK)) {
									checkAll = true;
								}
							}
							if (!checkAll) {
								for (int i = 0; i < mItems.size(); i++) {
									if (i != position) {
										Map<String, Object> item = mItems.get(i);
										if (item.containsKey(ITEM_ALL) && (Boolean) item.get(ITEM_ALL)) {
											item.put(ITEM_CHECK, true);
										}
									}
								}
							}
						}
					}
				}
				adapter.notifyDataSetChanged();
			}
			if (mItemClickListener != null)
				mItemClickListener.onItemClick(parent, view, position, id);
		}
	}

}
