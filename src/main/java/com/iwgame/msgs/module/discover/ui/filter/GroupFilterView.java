/**      
 * GroupFilterView.java Create on 2015-4-16     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.discover.adapter.BaseMultipleFilterAdapter;
import com.iwgame.msgs.module.discover.adapter.BaseRadioFilterAdapter;
import com.iwgame.msgs.module.discover.ui.filter.vo.FilterVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.vo.local.GameVo;
import com.youban.msgs.R;

/**
 * @ClassName: GroupFilterView
 * @Description: 公会过滤视图
 * @author 王卫
 * @date 2015-4-16 下午5:49:54
 * @Version 1.0
 * 
 */
public class GroupFilterView extends FilterView {

	private TextView verifyTxt;
	private TextView gameNunTxt;
	private TextView gameTxt;
	private TextView serviceNunTxt;
	private TextView serviceTxt;

	private PullToRefreshListView verifyRefreshList;
	private PullToRefreshListView gameRefreshList;
	private PullToRefreshListView serviceRefreshList;
	private View verifyView;
	private View gameView;
	private View seviceView;
	private TextView serviceNullBg;
	private LinearLayout serviceBottom;
	private TextView gameNullBg;
	private LinearLayout gameBottom;

	private List<GameVo> gameVoList;
	private BaseMultipleFilterAdapter<FilterVo> gameAdapter;

	private BaseMultipleFilterAdapter<FilterVo> serviceAdapter;

	private Boolean verify = null;
	
	private String selectServiceIds;
	
	private static int verifySelectedIndex;
	private String gamesSelectedId;
	private String servicesSelectedId;

	/**
	 * @param context
	 */
	public GroupFilterView(Context context, RefreshDataListener listener, int showGameTab, int showServiceTab, final int mode) {
		super(context, listener, R.layout.discover_filter_group_view, mode);
		verifyTxt = (TextView) findViewById(R.id.verifyTxt);
		findViewById(R.id.gameTab).setVisibility(showGameTab);
		findViewById(R.id.serviceTab).setVisibility(showServiceTab);
		gameNunTxt = (TextView) findViewById(R.id.gameNunTxt);
		gameTxt = (TextView) findViewById(R.id.gameTxt);
		serviceNunTxt = (TextView) findViewById(R.id.serviceNunTxt);
		serviceTxt = (TextView) findViewById(R.id.serviceTxt);
		//初始化上次保存记录
		initLastSelected();
		// 初始化列表
		creatVerifyListView();
		creatGameListView();
		creatServiceListView(false);
		// 设置选项卡点击事件
		views = new ArrayList<TextView>();
		views.add(verifyTxt);
		views.add(gameTxt);
		views.add(serviceTxt);
		verifyTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(0, verifyView, verifyTxt);
			}
		});
		gameTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popIndex != 1) {
					getFollowGames(true);
				} else {
					addPopView(1, gameView, gameTxt);
					gameAdapter.notifyDataSetChanged();
				}
			}
		});
		serviceTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mode == MODE_DISCOVER)
					creatServiceListView(true);
				else
					addPopView(2, seviceView, serviceTxt);
			}
		});
	}

	/**
	 * 验证
	 */
	private void creatVerifyListView() {
		int index = 0;
		if (mode == MODE_DISCOVER)
			index = SystemContext.getInstance().getDiscoverUserSex();
		if (index == 0) {
			verify = null;
			verifyTxt.setText("验证(全部)");
		} else if (index == 1) {
			verify = false;
			verifyTxt.setText("验证(无验证)");
		} else if (index == 2) {
			verifyTxt.setText("验证(有验证)");
			verify = true;
		} else {
			verifyTxt.setText("验证(全部)");
			verify = null;
		}
		verifyView = creatPullToRefreshListView();
		verifyRefreshList = (PullToRefreshListView) verifyView.findViewById(R.id.refreshList);
		final List<FilterVo> verifys = new ArrayList<FilterVo>();
		FilterVo gf = new FilterVo(0, "全部", index == 0 ? true : false).setVerifyValue(null);
		verifys.add(gf);
		gf = new FilterVo(1, "无验证", index == 1 ? true : false).setVerifyValue(false);
		verifys.add(gf);
		gf = new FilterVo(2, "有验证", index == 2 ? true : false).setVerifyValue(true);
		verifys.add(gf);
		final BaseRadioFilterAdapter<FilterVo> verfiyAdapter = new BaseRadioFilterAdapter<FilterVo>(getContext(), verifys);
		verifyRefreshList.setAdapter(verfiyAdapter);
		verifyRefreshList.setMode(Mode.DISABLED);
		verifyRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				int size = verifys.size();
				for (int i = 0; i < size; i++) {
					FilterVo filter = verifys.get(i);
					if (i == position) {
						filter.selected = true;
					} else {
						filter.selected = false;
					}
				}
				verfiyAdapter.notifyDataSetChanged();
				addPopView(0, verifyRefreshList, verifyTxt);
				if (position == 0) {
					verify = null;
					verifyTxt.setText("验证(全部)");
				} else if (position == 1) {
					verify = false;
					verifyTxt.setText("验证(无验证)");
				} else if (position == 2) {
					verifyTxt.setText("验证(有验证)");
					verify = true;
				}
				if (mode == MODE_DISCOVER) {
					verifySelectedIndex = position;
					saveLastSelected();
					onRefreshData();
				} else {
					listener.onRefreshGroup(verify, getSelectServiceIds());
				}
			}
		});
	}

	/**
	 * 游戏
	 */
	private void creatGameListView() {
		gameAdapter = new BaseMultipleFilterAdapter<FilterVo>(getContext(), new ArrayList<FilterVo>());
		gameRefreshList = creatGamePullToRefreshListView();
		gameRefreshList.setAdapter(gameAdapter);
		gameRefreshList.setMode(Mode.DISABLED);
		getFollowGames(false);
	}

	/**
	 * 服务器
	 */
	private void creatServiceListView(boolean isadd) {
		serviceAdapter = new BaseMultipleFilterAdapter<FilterVo>(getContext(), new ArrayList<FilterVo>());
		serviceRefreshList = creatServicePullToRefreshListView();
		serviceRefreshList.setAdapter(serviceAdapter);
		serviceRefreshList.setMode(Mode.DISABLED);
		setServiceData(SystemContext.getInstance().getGameServices(), isadd);
	}

	/**
	 * 选择游戏
	 * @return
	 */
	protected PullToRefreshListView creatGamePullToRefreshListView() {
		gameView = View.inflate(getContext(), R.layout.discover_filter_content_list, null);
		gameNullBg = (TextView) gameView.findViewById(R.id.nullTxt);
		gameNullBg.setText("尚未找到游戏信息");
		gameBottom = (LinearLayout) gameView.findViewById(R.id.bottom);
		gameBottom.setVisibility(View.VISIBLE);
		Button cannelBtn = (Button) gameView.findViewById(R.id.cannelBtn);
		Button commitBtn = (Button) gameView.findViewById(R.id.commitBtn);
		cannelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 重置
				if (gameAdapter.data != null) {
					int size = gameAdapter.data.size();
					for (int i = 0; i < size; i++) {
						FilterVo fvo = gameAdapter.data.get(i);
						fvo.selected = false;
					}
					gameAdapter.notifyDataSetChanged();
				}
			}
		});
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(1, gameView, gameTxt);
				gameAdapter.notifyDataSetChanged();
				if (mode == MODE_DISCOVER) {
					getSelectGameIds();
					onRefreshData();
				}
				setGameNun();
			}
		});
		return (PullToRefreshListView) gameView.findViewById(R.id.refreshList);
	}

	/**
	 * 选择服务器
	 * @return
	 */
	protected PullToRefreshListView creatServicePullToRefreshListView() {
		seviceView = View.inflate(getContext(), R.layout.discover_filter_content_list, null);
		serviceNullBg = (TextView) seviceView.findViewById(R.id.nullTxt);
		serviceNullBg.setText("尚未找到服务器信息");
		serviceBottom = (LinearLayout) seviceView.findViewById(R.id.bottom);
		serviceBottom.setVisibility(View.VISIBLE);
		Button cannelBtn = (Button) seviceView.findViewById(R.id.cannelBtn);
		Button commitBtn = (Button) seviceView.findViewById(R.id.commitBtn);
		cannelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 重置
				if (serviceAdapter.data != null) {
					int size = serviceAdapter.data.size();
					for (int i = 0; i < size; i++) {
						FilterVo fvo = serviceAdapter.data.get(i);
						fvo.selected = false;
					}
					serviceAdapter.notifyDataSetChanged();
					getSelectServiceIds();
				}
			}
		});
		commitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(2, seviceView, serviceTxt);
				if (mode == MODE_DISCOVER) {
					getSelectServiceIds();
					onRefreshData();
				} else {
					listener.onRefreshGroup(verify, getSelectServiceIds());
				}
				setServiveNun();
			}
		});
		return (PullToRefreshListView) seviceView.findViewById(R.id.refreshList);
	}

	/**
	 * 
	 * @param ids
	 * @return
	 */
	private Set<Long> getIds(String ids) {
		Set<Long> gs = new HashSet<Long>();
		if (ids != null) {
			String[] idsarray = ids.split(",");
			for (int i = 0; i < idsarray.length; i++) {
				gs.add(Long.valueOf(idsarray[i]));
			}
		}
		return gs;
	}

	/**
	 * 
	 * @param ids
	 * @return
	 */
	private Set<String> getServiceIds(String ids) {
		Set<String> gs = new HashSet<String>();
		if (ids != null) {
			String[] idsarray = ids.split(",");
			for (int i = 0; i < idsarray.length; i++) {
				gs.add(idsarray[i]);
			}
		}
		return gs;
	}

	/**
	 * 
	 */
	private void getFollowGames(final boolean isadd) {
		GameDao dao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		gameVoList = dao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE);
		if (gameVoList != null && gameVoList.size() > 0) {
			setGameData(gameVoList, isadd);
		} else {
			ProxyFactory.getInstance().getGameProxy().getFollowGames(new ProxyCallBack<List<GameVo>>() {

				@Override
				public void onSuccess(List<GameVo> result) {
					setGameData(result, isadd);
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					setGameData(null, isadd);
				}
			}, getContext(), true);
		}
	}

	private void setGameData(List<GameVo> games, boolean isadd) {
		gameAdapter.data.clear();
		if (games != null && games.size() > 0) {
			// 添加全部
			boolean isall = true;
			FilterVo afvo = new FilterVo(0, "全部", isall, null, false);
			gameAdapter.data.add(afvo);
			Set<Long> gameIds = getIds(mode == MODE_DISCOVER ? gamesSelectedId : null);
			List<FilterVo> filters = new ArrayList<FilterVo>();
			int size = games.size();
			for (int i = 0; i < size; i++) {
				GameVo gvo = games.get(i);
				boolean select = gameIds.contains(gvo.getGameid());
				FilterVo fvo = new FilterVo(i, gvo.getGamename(), select, gvo.getGamelogo(), true).setGidValue(gvo.getGameid());
				filters.add(fvo);
				if (!select)
					isall = false;
			}
			afvo.selected = isall;
			gameAdapter.data.addAll(filters);
			setGameNun();
		}
		gameAdapter.notifyDataSetChanged();
		if (isadd){
			addPopView(1, gameView, gameTxt);
			gameAdapter.notifyDataSetChanged();
		}
		if (gameAdapter.data.size() > 0) {
			gameBottom.setVisibility(View.VISIBLE);
			gameRefreshList.setVisibility(View.VISIBLE);
			gameNullBg.setVisibility(View.GONE);
		} else {
			gameBottom.setVisibility(View.GONE);
			gameRefreshList.setVisibility(View.GONE);
			gameNullBg.setVisibility(View.VISIBLE);
		}
	}

	private void setServiceData(Msgs.UserGameServerResult result, boolean isadd) {
		serviceAdapter.data.clear();
		if (result != null) {
			List<Msgs.GameServerEntry> server = result.getEntryList();
			if (server != null && server.size() > 0) {
				// 添加全部
				boolean isall = true;
				FilterVo afvo = new FilterVo(0, "全部", isall, null, false);
				serviceAdapter.data.add(afvo);
				Set<String> sids = getServiceIds(mode == MODE_DISCOVER ? servicesSelectedId : selectServiceIds);
				List<FilterVo> filters = new ArrayList<FilterVo>();
				int size = server.size();
				for (int i = 0; i < size; i++) {
					Msgs.GameServerEntry gvo = server.get(i);
					boolean select = sids.contains(gvo.getGid() + "-" + gvo.getId());
					FilterVo fvo = new FilterVo(i, gvo.getName(), select, null, false).setServiceKeyValue(gvo.getGid(), gvo.getId()).setSidValue(
							gvo.getId());
					filters.add(fvo);
					if (!select)
						isall = false;
				}
				afvo.selected = isall;
				serviceAdapter.data.addAll(filters);
				setServiveNun();
			}
		}
		serviceAdapter.notifyDataSetChanged();
		if (isadd)
			addPopView(2, seviceView, serviceTxt);
		if (serviceAdapter.data.size() > 0) {
			serviceBottom.setVisibility(View.VISIBLE);
			serviceRefreshList.setVisibility(View.VISIBLE);
			serviceNullBg.setVisibility(View.GONE);
		} else {
			serviceBottom.setVisibility(View.GONE);
			serviceRefreshList.setVisibility(View.GONE);
			serviceNullBg.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 */
	private void setServiveNun() {
		// 0一个都没选 －1全选
		int num = 0;
		String firstSname = "";
		if (serviceAdapter.data != null && serviceAdapter.data.size() > 0) {
			int size = serviceAdapter.data.size();
			for (int i = 0; i < size; i++) {
				FilterVo fvo = serviceAdapter.data.get(i);
				if (fvo.selected) {
					num++;
					firstSname = fvo.name;
				}
			}
			if (num == size) {
				num = -1;
			}
		}
		if (num == 0) {
			serviceNunTxt.setVisibility(View.GONE);
			serviceTxt.setText("只看同服");
		} else if (num == -1) {
			serviceNunTxt.setVisibility(View.GONE);
			serviceTxt.setText("只看同服(全部)");
		} else {
			if (num > 1) {
				serviceTxt.setText("只看同服");
				serviceNunTxt.setVisibility(View.VISIBLE);
			} else {
				serviceTxt.setText(firstSname);
				serviceNunTxt.setVisibility(View.GONE);
			}
			serviceNunTxt.setText(num + "");
			if (num < 100) {
				serviceNunTxt.setBackgroundResource(R.drawable.discover_filter_num_bg_shap);
			} else {
				serviceNunTxt.setBackgroundResource(R.drawable.discover_filter_num_bg_shap2);
			}
		}
	}

	/**
	 * 
	 */
	private void setGameNun() {
		// 0一个都没选 －1全选
		int num = 0;
		String firstSname = "";
		if (gameAdapter.data != null && gameAdapter.data.size() > 0) {
			int size = gameAdapter.data.size();
			for (int i = 0; i < size; i++) {
				FilterVo fvo = gameAdapter.data.get(i);
				if (fvo.selected) {
					num++;
					firstSname = fvo.name;
				}
			}
			if (num == size) {
				num = -1;
			}
		}
		if (num == 0) {
			gameNunTxt.setVisibility(View.GONE);
			gameTxt.setText("所属游戏");
		} else if (num == -1) {
			gameNunTxt.setVisibility(View.GONE);
			gameTxt.setText("所属游戏(全部)");
		} else {
			if (num > 1) {
				gameTxt.setText("所属游戏");
				gameNunTxt.setVisibility(View.VISIBLE);
			} else {
				gameTxt.setText(firstSname);
				gameNunTxt.setVisibility(View.GONE);
			}
			gameNunTxt.setText(num + "");
			if (num < 100) {
				gameNunTxt.setBackgroundResource(R.drawable.discover_filter_num_bg_shap);
			} else {
				gameNunTxt.setBackgroundResource(R.drawable.discover_filter_num_bg_shap2);
			}
		}
	}

	/**
	 * 设置贴吧IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getSelectGameIds() {
		if (gameAdapter.data != null) {
			StringBuffer strBuf = new StringBuffer();
			int size = gameAdapter.data.size();
			for (int i = 0; i < size; i++) {
				FilterVo fvo = gameAdapter.data.get(i);
				if (fvo.selected && fvo.gid > 0) {
					strBuf.append(fvo.gid);
					strBuf.append(",");
				}
			}
			if (strBuf.length() > 0) {
				String sg = strBuf.substring(0, strBuf.length() - 1);
				if (mode == MODE_DISCOVER)
					gamesSelectedId = sg;
					saveLastSelected();
				return sg;
			} else {
				if (mode == MODE_DISCOVER){
					gamesSelectedId = null;
					saveLastSelected();
				}
			}
		}
		return null;
	}

	/**
	 * 设置服务器IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getSelectServiceIds() {
		if (serviceAdapter.data != null) {
			StringBuffer strBuf = new StringBuffer();
			int size = serviceAdapter.data.size();
			for (int i = 0; i < size; i++) {
				FilterVo fvo = serviceAdapter.data.get(i);
				if (fvo.selected && !TextUtils.isEmpty(fvo.serviceKey)) {
					strBuf.append(fvo.serviceKey);
					strBuf.append(",");
				}
			}
			if (strBuf.length() > 0) {
				String sg = strBuf.substring(0, strBuf.length() - 1);
				selectServiceIds = sg;
				if (mode == MODE_DISCOVER)
					servicesSelectedId = sg;
					saveLastSelected();
				return sg;
			} else {
				selectServiceIds = null;
				if (mode == MODE_DISCOVER){
					servicesSelectedId = null;
					saveLastSelected();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public static Boolean getSelectVerifyByShare() {
		int verifyIndex = verifySelectedIndex;
		switch (verifyIndex) {
		case 0:
			return null;
		case 1:
			return false;
		case 2:
			return true;
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
		verify = null;
		verifyTxt.setText("验证(全部)");
		gameNunTxt.setVisibility(View.GONE);
		gameNunTxt.setText(0 + "");
		gameTxt.setText("所属游戏");
		serviceNunTxt.setVisibility(View.GONE);
		serviceNunTxt.setText(0 + "");
		serviceTxt.setText("只看同服");
		// 初始化列表
		creatVerifyListView();
		creatGameListView();
		creatServiceListView(false);
	}
	
	private void saveLastSelected(){
		//验证选项索引
		SystemContext.getInstance().setDiscoverVerify(verifySelectedIndex);
		//所选游戏
		SystemContext.getInstance().setDiscoverGame(gamesSelectedId);
		//所选服务器
		SystemContext.getInstance().setDiscoverGroupService(servicesSelectedId);
	}
	
	private void initLastSelected(){
		verifySelectedIndex = SystemContext.getInstance().getDiscoverVerify();
		gamesSelectedId =  SystemContext.getInstance().getDiscoverGame();
		servicesSelectedId = SystemContext.getInstance().getDiscoverGroupService();
	}

}
