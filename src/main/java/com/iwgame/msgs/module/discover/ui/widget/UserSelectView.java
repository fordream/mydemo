/**      
 * UserSelectView.java Create on 2013-12-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.youban.msgs.R;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.widget.gridview.SelectGridView;

/**
 * @ClassName: UserSelectView
 * @Description: 用户选择界面
 * @author 王卫
 * @date 2013-12-26 下午4:14:44
 * @Version 1.0
 * 
 */
public class UserSelectView extends LinearLayout {

	private LayoutInflater mInflater;

	private LinearLayout sexSelect;

	private LinearLayout timeSelect;

	private SelectGridView sexGridView;

	private SelectGridView timeGridView;

	public static List<Map<String, Object>> timeData = getTimeData();
	public static List<Map<String, Object>> sexData = getSexData();

	/**
	 * 
	 * @param context
	 * @param sexSelectedIndex
	 * @param timeSelectedIndex
	 */
	public UserSelectView(Context context) {
		super(context);
		mInflater = LayoutInflater.from(getContext());
		View contentView = mInflater.inflate(R.layout.discover_user_dialog_content, this, false);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(contentView, params);
		sexSelect = (LinearLayout) contentView.findViewById(R.id.sexSelect);
		timeSelect = (LinearLayout) contentView.findViewById(R.id.timeSelect);

		initSexContent(sexSelect);
		initTimeContent(timeSelect);
	}

	public static List<Map<String, Object>> getTimeData() {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "1小时");
		map.put("id", 0);
		map.put("time", 60);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "1天");
		map.put("id", 1);
		map.put("time", 1440);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "3天");
		map.put("id", 2);
		map.put("time", 4320);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "7天");
		map.put("id", 3);
		map.put("time", 10080);
		items.add(map);
		return items;
	}

	public static List<Map<String, Object>> getSexData() {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "全部");
		map.put("id", 0);
		map.put("sex", -1);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "男");
		map.put("id", 1);
		map.put("sex", 0);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "女");
		map.put("id", 2);
		map.put("sex", 1);
		items.add(map);
		return items;
	}

	public static Integer getSelectTime(int index) {
		for (int i = 0; i < timeData.size(); i++) {
			if (index == (Integer) timeData.get(i).get("id")) {
				if ((Integer) timeData.get(i).get("time") == -1l)
					return null;
				else
					return (Integer) timeData.get(i).get("time");
			}
		}
		return null;
	}

	public static Integer getSelectSex(int index) {
		if (index == 0) {
			return null;
		} else {
			for (int i = 0; i < sexData.size(); i++) {
				if (index == (Integer) sexData.get(i).get("id")) {
					if ((Integer) sexData.get(i).get("sex") == -1l)
						return null;
					else
						return (Integer) sexData.get(i).get("sex");
				}
			}
			return null;
		}
	}

	/**
	 * 
	 */
	private void initTimeContent(LinearLayout timeSelect) {
		timeGridView = new SelectGridView(this.getContext(), timeData, 4, true, null, null);
		timeGridView.setSelection(SystemContext.getInstance().getDiscoverTime());
		timeSelect.addView(timeGridView);
	}

	/**
	 * 
	 */
	private void initSexContent(LinearLayout sexSelect) {
		sexGridView = new SelectGridView(this.getContext(), sexData, 3, true, null, null);
		sexGridView.setSelection(SystemContext.getInstance().getDiscoverUserSex());
		sexSelect.addView(sexGridView);
	}

	/**
	 * 保存选显卡信息
	 */
	public void saveSelectItems() {
		// 保存性别信息
		List<Map<String, Object>> sexSelectItems = sexGridView.getSelectedItems();
		if (sexSelectItems != null && sexSelectItems.size() > 0) {
			SystemContext.getInstance().setDiscoverUserSex((Integer) sexSelectItems.get(0).get("id"));
		}
		// 保存出现时间信息
		List<Map<String, Object>> timeSelectItems = timeGridView.getSelectedItems();
		if (timeSelectItems != null && timeSelectItems.size() > 0) {
			SystemContext.getInstance().setDiscoverTime((Integer) timeSelectItems.get(0).get("id"));
		}
	}

	public SelectGridView getTimeGridView() {
		return timeGridView;
	}

	public void setTimeGridView(SelectGridView timeGridView) {
		this.timeGridView = timeGridView;
	}

	public SelectGridView getSexGridView() {
		return sexGridView;
	}

	public void setSexGridView(SelectGridView sexGridView) {
		this.sexGridView = sexGridView;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getSelectSex() {
		int sex = (Integer) (sexGridView.getSelectedItems().get(0).get("sex"));
		if (sex == -1)
			return null;
		return sex;
	}

	/**
	 * 
	 * @return
	 */
	public Integer getSelectTime() {
		int time = (Integer) (timeGridView.getSelectedItems().get(0).get("time"));
		if (time == -1l)
			return null;
		return time;
	}
}
