/**      
 * GameSelectView2.java Create on 2014-4-4     
 *      
 * Copyright (c) 2014 by GreenShore Network
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
 * @ClassName: GameSelectView
 * @Description: 发现贴吧选择界面
 * @author 王卫
 * @date 2014-4-4 上午10:58:17
 * @Version 1.0
 * 
 */
public class GameSelectView extends LinearLayout {

	private LayoutInflater mInflater;

	private LinearLayout platformSelect;

	private LinearLayout netSelect;

	private SelectGridView platformGridView;

	private SelectGridView netGridView;

	public static List<Map<String, Object>> platformData = getPlatformTypeData();
	public static List<Map<String, Object>> netData = getNetTypeData();

	/**
	 * @param context
	 */
	public GameSelectView(Context context) {
		super(context);
		mInflater = LayoutInflater.from(getContext());
		View contentView = mInflater.inflate(R.layout.discover_game_dialog_content2, this, false);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(contentView, params);
		platformSelect = (LinearLayout) contentView.findViewById(R.id.platformSelect);
		netSelect = (LinearLayout) contentView.findViewById(R.id.netSelect);

		initPlatformContent(platformSelect);
		initNetContent(netSelect);
	}

	/**
	 * 初始化贴吧联网类型
	 * 
	 * @param platformSelect
	 */
	private void initPlatformContent(LinearLayout platformSelect) {
		platformGridView = new SelectGridView(this.getContext(), platformData, 3, true, null, null);
		platformGridView.setSelection(SystemContext.getInstance().getDiscoverGamePlatform());
		platformSelect.addView(platformGridView);
	}

	/**
	 * 初始化贴吧联网类型
	 * 
	 * @param netSelect
	 */
	private void initNetContent(LinearLayout netSelect) {
		netGridView = new SelectGridView(this.getContext(), netData, 3, true, null, null);
		netGridView.setSelection(SystemContext.getInstance().getDiscoverGameType());
		netSelect.addView(netGridView);
	}

	public static final String PLATFORM_ALL = "-1";
	public static final String PLATFORM_PF1 = "pf1";
	public static final String PLATFORM_PF2 = "pf2";
	public static final String PLATFORM_PF3 = "pf3";
	public static final String PLATFORM_PF4 = "pf4";
	public static final String PLATFORM_PF5 = "pf5";
	private static List<Map<String, Object>> getPlatformTypeData() {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "查看全部");
		map.put("id", 0);
		map.put("platform", "-1");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "PC端游");
		map.put("id", 1);
		map.put("platform", "pf1");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "网页游戏");
		map.put("id", 2);
		map.put("platform", "pf2");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "手机游戏");
		map.put("id", 3);
		map.put("platform", "pf3");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "电视游戏");
		map.put("id", 4);
		map.put("platform", "pf4");
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "掌机游戏");
		map.put("id", 5);
		map.put("platform", "pf5");
		items.add(map);
		return items;
	}

	public static final int NETTYPE_ALL = -1;
	public static final int NETTYPE_NONET = 0;
	public static final int NETTYPE_NET = 1;
	private static List<Map<String, Object>> getNetTypeData() {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "不限");
		map.put("id", 0);
		map.put("gtype", -1);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "单机游戏");
		map.put("id", 1);
		map.put("gtype", 0);
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, "联网游戏");
		map.put("id", 2);
		map.put("gtype", 1);
		items.add(map);
		return items;
	}

	/**
	 * 保存选择条目
	 */
	public void saveSelectItems() {
		// 保存贴吧平台选择条目
		List<Map<String, Object>> platformSelectItems = platformGridView.getSelectedItems();
		if (platformSelectItems != null && platformSelectItems.size() > 0)
			SystemContext.getInstance().setDiscoverGamePlatform((Integer) platformSelectItems.get(0).get("id"));
		else
			SystemContext.getInstance().setDiscoverGamePlatform(0);
		// 保存贴吧网络类型选择条目
		List<Map<String, Object>> netSelectItems = netGridView.getSelectedItems();
		if (netSelectItems != null && netSelectItems.size() > 0)
			SystemContext.getInstance().setDiscoverGameType((Integer) netSelectItems.get(0).get("id"));
		else
			SystemContext.getInstance().setDiscoverGameType(0);
	}

	/**
	 * 
	 * @return
	 */
	public Integer getSelectGameType() {
		List<Map<String, Object>> netSelectItems = netGridView.getSelectedItems();
		if (netSelectItems != null && netSelectItems.size() > 0) {
			int gtype = (Integer) netSelectItems.get(0).get("gtype");
			if (gtype == -1)
				return null;
			else
				return gtype;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public String getSelectGamePlatform() {
		List<Map<String, Object>> platformSelectItems = platformGridView.getSelectedItems();
		if (platformSelectItems != null && platformSelectItems.size() > 0) {
			String platform = (String) platformSelectItems.get(0).get("platform");
			if (platform == "-1")
				return null;
			else
				return platform;
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static String getSelectGamePlatformByShare() {
		int pfIndex = SystemContext.getInstance().getDiscoverGamePlatform();
		switch (pfIndex) {
		case 0:
			return null;
		case 1:
			return "pf1";
		case 2:
			return "pf2";
		case 3:
			return "pf3";
		case 4:
			return "pf4";
		case 5:
			return "pf5";
		default:
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static Integer getSelectGameGtypeByShare() {
		int gtIndex = SystemContext.getInstance().getDiscoverGameType();
		switch (gtIndex) {
		case 0:
			return null;
		case 1:
			return 0;
		case 2:
			return 1;
		default:
			return null;
		}
	}

}
