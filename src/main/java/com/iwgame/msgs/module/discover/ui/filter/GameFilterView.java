/**      
 * GameFilterView.java Create on 2015-4-16     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui.filter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.discover.adapter.BaseRadioFilterAdapter;
import com.iwgame.msgs.module.discover.ui.filter.vo.FilterVo;
import com.youban.msgs.R;

/**
 * @ClassName: GameFilterView
 * @Description: 游戏过滤视图
 * @author 王卫
 * @date 2015-4-16 下午5:48:19
 * @Version 1.0
 * 
 */
public class GameFilterView extends FilterView {

	private TextView platformTxt;
	private TextView netwokTxt;

	private PullToRefreshListView platformRefreshList;
	private PullToRefreshListView gtypeRefreshList;
	private View platformView;
	private View gtypeView;

	/**
	 * @param context
	 */
	public GameFilterView(Context context, RefreshDataListener listener) {
		super(context, listener, R.layout.discover_filter_game_view, MODE_DISCOVER);
		platformTxt = (TextView) findViewById(R.id.platformTxt);
		netwokTxt = (TextView) findViewById(R.id.netwokTxt);
		// 初始化列表
		creatplatformListView();
		creatNetworkListView();
		// 设置选项卡点击事件
		views = new ArrayList<TextView>();
		views.add(platformTxt);
		views.add(netwokTxt);
		platformTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(0, platformView, platformTxt);
			}
		});
		netwokTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(1, gtypeView, netwokTxt);
			}
		});
	}

	/**
	 * 平台
	 */
	private void creatplatformListView() {
		int index = SystemContext.getInstance().getDiscoverGamePlatform();
		if (index == 0) {
			platformTxt.setText("平台(全部)");
		} else if (index == 1) {
			platformTxt.setText("平台(PC端游)");
		} else if (index == 2) {
			platformTxt.setText("平台(网页游戏)");
		} else if (index == 3) {
			platformTxt.setText("平台(手机游戏)");
		} else if (index == 4) {
			platformTxt.setText("平台(电视游戏)");
		} else if (index == 5) {
			platformTxt.setText("平台(掌机游戏)");
		}
		platformView = creatPullToRefreshListView();
		platformRefreshList = (PullToRefreshListView) platformView.findViewById(R.id.refreshList);
		final List<FilterVo> platforms = new ArrayList<FilterVo>();
		FilterVo gf = new FilterVo(0, "全部", index == 0 ? true : false).setPlatformValue(null);
		platforms.add(gf);
		gf = new FilterVo(1, "PC端游", index == 1 ? true : false).setPlatformValue("pf1");
		platforms.add(gf);
		gf = new FilterVo(2, "网页游戏", index == 2 ? true : false).setPlatformValue("pf2");
		platforms.add(gf);
		gf = new FilterVo(3, "手机游戏", index == 3 ? true : false).setPlatformValue("pf3");
		platforms.add(gf);
		gf = new FilterVo(4, "电视游戏", index == 4 ? true : false).setPlatformValue("pf4");
		platforms.add(gf);
		gf = new FilterVo(5, "掌机游戏", index == 5 ? true : false).setPlatformValue("pf5");
		platforms.add(gf);
		final BaseRadioFilterAdapter<FilterVo> platformAdapter = new BaseRadioFilterAdapter<FilterVo>(getContext(), platforms);
		platformRefreshList.setAdapter(platformAdapter);
		platformRefreshList.setMode(Mode.DISABLED);
		platformRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				int size = platforms.size();
				for (int i = 0; i < size; i++) {
					FilterVo filter = platforms.get(i);
					if (i == position) {
						filter.selected = true;
					} else {
						filter.selected = false;
					}
				}
				if (position == 0) {
					platformTxt.setText("平台(全部)");
				} else if (position == 1) {
					platformTxt.setText("平台(PC端游)");
				} else if (position == 2) {
					platformTxt.setText("平台(网页游戏)");
				} else if (position == 3) {
					platformTxt.setText("平台(手机游戏)");
				} else if (position == 4) {
					platformTxt.setText("平台(电视游戏)");
				} else if (position == 5) {
					platformTxt.setText("平台(掌机游戏)");
				}
				platformAdapter.notifyDataSetChanged();
				addPopView(0, platformRefreshList, platformTxt);
				SystemContext.getInstance().setDiscoverGamePlatform(position);
				onRefreshData();
			}
		});
	}

	/**
	 * 网络类型
	 */
	private void creatNetworkListView() {
		int index = SystemContext.getInstance().getDiscoverGameType();
		if (index == 0) {
			netwokTxt.setText("联网(不限)");
		} else if (index == 1) {
			netwokTxt.setText("联网(单机游戏)");
		} else if (index == 2) {
			netwokTxt.setText("联网(联网游戏)");
		}
		gtypeView = creatPullToRefreshListView();
		gtypeRefreshList = (PullToRefreshListView) gtypeView.findViewById(R.id.refreshList);
		final List<FilterVo> gtypes = new ArrayList<FilterVo>();
		FilterVo gf = new FilterVo(0, "不限", index == 0 ? true : false).setGtypeValue(null);
		gtypes.add(gf);
		gf = new FilterVo(1, "单机游戏", index == 1 ? true : false).setGtypeValue(0);
		gtypes.add(gf);
		gf = new FilterVo(2, "联网游戏", index == 2 ? true : false).setGtypeValue(1);
		gtypes.add(gf);
		final BaseRadioFilterAdapter<FilterVo> gtypeAdapter = new BaseRadioFilterAdapter<FilterVo>(getContext(), gtypes);
		gtypeRefreshList.setAdapter(gtypeAdapter);
		gtypeRefreshList.setMode(Mode.DISABLED);
		gtypeRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				int size = gtypes.size();
				for (int i = 0; i < size; i++) {
					FilterVo filter = gtypes.get(i);
					if (i == position) {
						filter.selected = true;
					} else {
						filter.selected = false;
					}
				}
				if (position == 0) {
					netwokTxt.setText("联网(不限)");
				} else if (position == 1) {
					netwokTxt.setText("联网(单机游戏)");
				} else if (position == 2) {
					netwokTxt.setText("联网(联网游戏)");
				}
				gtypeAdapter.notifyDataSetChanged();
				addPopView(1, gtypeRefreshList, netwokTxt);
				SystemContext.getInstance().setDiscoverGameType(position);
				onRefreshData();
			}
		});
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.discover.ui.filter.FilterView#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		platformTxt.setText("平台(全部)");
		netwokTxt.setText("联网(不限)");
		// 初始化列表
		creatplatformListView();
		creatNetworkListView();
	}

}
