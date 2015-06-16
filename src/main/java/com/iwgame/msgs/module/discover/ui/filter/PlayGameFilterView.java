/**      
 * PlayGameFilterView.java Create on 2015-5-26     
 *      
 * Copyright (c) 2015 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.discover.ui.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.discover.adapter.BaseRadioFilterAdapter;
import com.iwgame.msgs.module.discover.adapter.BaseRadioGreyBackgroundFilterAdapter;
import com.iwgame.msgs.module.discover.adapter.BaseRadioIconFilterAdapter;
import com.iwgame.msgs.module.discover.adapter.FilterAdapter;
import com.iwgame.msgs.module.discover.ui.filter.vo.FilterVo;
import com.iwgame.msgs.module.user.ui.ExtraGameVo;
import com.iwgame.msgs.proto.Msgs.GameKeysDetail;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameKeyVo;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/** 
* @ClassName: PlayGameFilterView 
* @Description:  陪玩筛选视图
* @author 彭赞 
* @date 2015-6-11 下午6:06:22 
* @Version 1.0 
*/
public class PlayGameFilterView extends FilterView  {
	//是否记住上次筛选的结果
	private boolean saveLastFlag = true;
	private final int GAME_LEVEL = 5;
	
	private TextView gameTxt;
	private TextView gameLevelTxt;
	private TextView intelligentTxt;
	private TextView sexTxt;

	private PullToRefreshListView gameRefreshParentList;
	private PullToRefreshListView gameRefreshChildList;
	private PullToRefreshListView gameLevelRefreshList;
	private PullToRefreshListView intelligentRefreshList;
	private PullToRefreshListView sexRefreshList;

	private BaseRadioIconFilterAdapter<FilterVo> gameServerParentAdpter;
	private BaseRadioGreyBackgroundFilterAdapter<FilterVo> gameServerChildAdpter;
	private BaseRadioFilterAdapter<FilterVo> gameLevelAdpter;
	private BaseRadioFilterAdapter<FilterVo> intelligentAdapter;
	private BaseRadioFilterAdapter<FilterVo> sexAdapter;

	private View gameServerView;
	private View gameLevelView;
	private View intelligentView;
	private View sexView;
	private TextView gameNullBg, gameLeverNullBg;
	private LinearLayout twoLayerRefreshLinearLayout;

	private final String[] playFilterText = { "游戏", "默认", "智能排序", "筛选" };
	private final String[] intelligentText = { "默认排序", "智能排序", "最新发布", "距离最近", "好评优先","价格最低", "价格最高" };
	private final String[] sexText = { "全部", "男", "女" };
	private String gameLevelString;
	private String gameName;
	
	// 筛选字段
	private long gid = 0;// 选中游戏id
	private long sid = 0;
	private int sorttype = 0;
	private Long keyid = null;
	private String keyval = null;
	private int sex = 0;
	
	private int parentPosition0 = 0;
	
	private final List<ExtraGameVo> gameExtraGameVoList = new ArrayList<ExtraGameVo>();
	private final Map<Long, List<GameKeyVo>> gameKeyVoMap = new ConcurrentHashMap<Long, List<GameKeyVo>>();
	

	public PlayGameFilterView(Context context, RefreshDataListener refreshDataListener, final int mode) {
		super(context, refreshDataListener, R.layout.discover_filter_playgame_view, mode);
		//初始化上次保存的筛选条件
		initLastData();
		//陪玩过滤数据查询
		setPlayFilterData();
		
		gameTxt = (TextView) findViewById(R.id.playgame_filter_gameTxt);
		gameLevelTxt = (TextView) findViewById(R.id.playgame_filter_gameLevelTxt);
		intelligentTxt = (TextView) findViewById(R.id.playgame_filter_intelligentTxt);
		sexTxt = (TextView) findViewById(R.id.playgame_filter_sexTxt);
		//显示上次筛选条件
		initTabText();
		
		// 初始化列表
		creatGameServerListView();
		createGameLevelListView();
		creatIntelligentListView();
		creatSexListView();
		
		views = new ArrayList<TextView>();
		views.add(gameTxt);
		views.add(gameLevelTxt);
		views.add(intelligentTxt);
		views.add(sexTxt);
		
		// 设置选项卡点击事件
		gameTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(0, gameServerView, gameTxt);
				setGamesData(getGameExtraGameVoList());
				setGameServerData(gid, getGameKeyVoMap());
				setGameLevelData(gid, getGameKeyVoMap());
			}
		});

		gameLevelTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(1, gameLevelView, gameLevelTxt);
				setGameLevelData(gid, getGameKeyVoMap());

			}
		});

		intelligentTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(2, intelligentView, intelligentTxt);
				intelligentAdapter.notifyDataSetChanged();
			}
		});

		sexTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addPopView(3, sexView, sexTxt);
				sexAdapter.notifyDataSetChanged();
			}
		});

	}

	private void initLastData() {
		gid = getSharePreGameId();
		sid = getSharePreGameServerId();
		sorttype = SystemContext.getInstance().getDiscoverPlayIntelligentSelected();
		keyid = getSharePreGameLeverKeyId();
		keyval = getSharePreGameLeverKeyValue();
		sex = SystemContext.getInstance().getDiscoverPlaySexSelected();
	}

	/**
	 * 
	* @Title: setTabText 
	* @Description:     默认加载上一次
	* @return void 
	* @throws
	 */
	private void initTabText() {
		gameTxt.setText(SystemContext.getInstance().getDiscoverPlayGameTabTextOne());
		gameLevelTxt.setText(SystemContext.getInstance().getDiscoverPlayGameTabTextTwo());
		int intelligentIndexTemp = SystemContext.getInstance().getDiscoverPlayIntelligentSelected();
		intelligentTxt.setText(intelligentText[intelligentIndexTemp]);
		int sexIndexTemp = SystemContext.getInstance().getDiscoverPlaySexSelected();
		sexTxt.setText(sexText[sexIndexTemp]);
	}

	/** 
	* @Title: resetTabText 
	* @Description:    恢复默认值
	* @return void 
	* @throws 
	*/
	private void resetTabText() {
		if (gameTxt != null) {
			gameTxt.setText("游戏");
		}
		if (gameLevelTxt != null) {
			gameLevelTxt.setText("默认");
		}
		if (intelligentTxt != null) {
			intelligentTxt.setText("默认排序");
		}
		if (sexTxt != null) {
			sexTxt.setText("筛选");
		}
	}

	/**
	 * 游戏服务器
	 */
	private void creatGameServerListView() {
		creatGamesPullToRefreshTwoListView();
		gameServerParentAdpter = new BaseRadioIconFilterAdapter<FilterVo>(getContext(), new ArrayList<FilterVo>());
		gameServerChildAdpter = new BaseRadioGreyBackgroundFilterAdapter<FilterVo>(getContext(), new ArrayList<FilterVo>());
		gameRefreshParentList.setAdapter(gameServerParentAdpter);
		gameRefreshChildList.setAdapter(gameServerChildAdpter);
		gameRefreshParentList.setMode(Mode.DISABLED);
		gameRefreshChildList.setMode(Mode.DISABLED);
		// 设置游戏列表数据
		setGamesData(getGameExtraGameVoList());
		//设置默认游戏服务器列表数据
		setGameServerData(gid, getGameKeyVoMap());
		
		gameRefreshParentList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				parentPosition0 = position;
				List<FilterVo> gameServerFilerVo = gameServerParentAdpter.data;
				FilterVo fvo = gameServerFilerVo.get(position);
				gameName = fvo.name;
				RefreshSelectedFilterVo(position, gameServerFilerVo,gameServerParentAdpter);
				setGameServerData(fvo.gid, getGameKeyVoMap());
				
			}
		});

		gameRefreshChildList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				List<FilterVo> gameServerFilerVo = gameServerChildAdpter.data;
				FilterVo fvo = gameServerFilerVo.get(position);
				if(gid!=fvo.gid || sid != fvo.sid){
					keyid = null;
					keyval = null;
					gameLevelTxt.setText(gameLevelString);
				}
				gid = fvo.gid;
				sid = fvo.sid;
				if(sorttype == 0){
					sorttype = 1;
					intelligentTxt.setText("智能排序");
					List<FilterVo> intelligentFilerVo = intelligentAdapter.data;
					intelligentFilerVo.get(0).selected = true;
				}
				RefreshSelectedFilterVo(position, gameServerFilerVo, gameServerChildAdpter);
				setGameServerText(position, gameServerChildAdpter);
				addPopView(0, gameServerView, gameTxt);
				gameServerParentAdpter.notifyDataSetChanged();
				gameServerChildAdpter.notifyDataSetChanged();
				if (mode == MODE_DISCOVER) {
					onRefreshData();
				}
				setGameLevelData(fvo.gid, getGameKeyVoMap());
			}
		});
	}


	/**
	 * 游戏段位
	 */
	private void createGameLevelListView() {
		gameLevelAdpter = new BaseRadioFilterAdapter<FilterVo>(getContext(),new ArrayList<FilterVo>());
		gameLevelView = creatPullToRefreshListView();
		gameLeverNullBg = (TextView) gameLevelView.findViewById(R.id.nullTxt);
		gameLeverNullBg.setText("你需要先选择游戏！");
		gameLevelRefreshList = (PullToRefreshListView) gameLevelView.findViewById(R.id.refreshList);
		gameLevelRefreshList.setAdapter(gameLevelAdpter);
		gameLevelRefreshList.setMode(Mode.DISABLED);
		setGameLevelData(gid, getGameKeyVoMap());
		gameLevelRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				List<FilterVo> gameServerFilerVo = gameLevelAdpter.data;
				FilterVo fvo = gameServerFilerVo.get(position);
				keyid = fvo.keyid;
				keyval = fvo.keyval;
				RefreshSelectedFilterVo(position, gameServerFilerVo, gameLevelAdpter);
				setGameLeverText(position, gameLevelAdpter);
				addPopView(1, gameLevelView, gameLevelTxt);
				gameLevelAdpter.notifyDataSetChanged();
				if (mode == MODE_DISCOVER) {
					onRefreshData();
				}
			}
		});

	}

	/**
	 * 智能筛选
	 */
	private void creatIntelligentListView() {
		intelligentView = creatPullToRefreshListView();
		intelligentRefreshList = (PullToRefreshListView) intelligentView.findViewById(R.id.refreshList);
		final List<FilterVo> intelligentFilterVoList = new ArrayList<FilterVo>();
		FilterVo intelligentFilterVo = new FilterVo(1, intelligentText[1], false);
		intelligentFilterVoList.add(intelligentFilterVo);
		intelligentFilterVo = new FilterVo(2, intelligentText[2], false);
		intelligentFilterVoList.add(intelligentFilterVo);
		intelligentFilterVo = new FilterVo(3, intelligentText[3], false);
		intelligentFilterVoList.add(intelligentFilterVo);
		intelligentFilterVo = new FilterVo(4, intelligentText[4], false);
		intelligentFilterVoList.add(intelligentFilterVo);
		intelligentFilterVo = new FilterVo(5, intelligentText[5], false);
		intelligentFilterVoList.add(intelligentFilterVo);
		intelligentFilterVo = new FilterVo(6, intelligentText[6], false);
		intelligentFilterVoList.add(intelligentFilterVo);
		int indexTemp = getIntillgentIndex();
		if(indexTemp != 0){
			intelligentFilterVoList.get(indexTemp-1).selected = true;
		}
		intelligentAdapter = new BaseRadioFilterAdapter<FilterVo>(
				getContext(), intelligentFilterVoList);
		intelligentRefreshList.setAdapter(intelligentAdapter);
		intelligentRefreshList.setMode(Mode.DISABLED);
		intelligentRefreshList.setOnItemClickListener(new OnItemClickListener() {

					@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				sorttype = position;
				intelligentTxt.setText(intelligentText[position]);
				position--;
				List<FilterVo> gameServerFilerVo = intelligentAdapter.data;
				RefreshSelectedFilterVo(position, gameServerFilerVo, intelligentAdapter);
				addPopView(2, intelligentView, intelligentTxt);
				intelligentAdapter.notifyDataSetChanged();
				if (mode == MODE_DISCOVER) {
					onRefreshData();
				}
			}
		});

	}

	/**
	 * 性别
	 */
	private void creatSexListView() {
		sexView = creatPullToRefreshListView();
		sexRefreshList = (PullToRefreshListView) sexView.findViewById(R.id.refreshList);
		final List<FilterVo> sexFilterVoList = new ArrayList<FilterVo>();
		FilterVo sexFilterVo = new FilterVo(0, sexText[0], false).setSexValue(0);//获取发现陪玩性别 index 0全部，1男，2女
		sexFilterVoList.add(sexFilterVo);
		sexFilterVo = new FilterVo(1, sexText[1], false).setSexValue(1);
		sexFilterVoList.add(sexFilterVo);
		sexFilterVo = new FilterVo(2, sexText[2], false).setSexValue(2);
		sexFilterVoList.add(sexFilterVo);
		sexFilterVoList.get(getSexIndex()).selected = true;
		sexAdapter = new BaseRadioFilterAdapter<FilterVo>(getContext(), sexFilterVoList);
		sexRefreshList.setAdapter(sexAdapter);
		sexRefreshList.setMode(Mode.DISABLED);
		sexRefreshList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position--;
				List<FilterVo> sexFilerVo = sexAdapter.data;
				sex = sexFilerVo.get(position).sex;
				RefreshSelectedFilterVo(position, sexFilerVo, sexAdapter);
				sexTxt.setText("筛选("+sexText[position]+")");
				addPopView(3, sexView, sexTxt);
				sexAdapter.notifyDataSetChanged();
				if (mode == MODE_DISCOVER) {
					onRefreshData();
				}
			}
		});
	}

	private void creatGamesPullToRefreshTwoListView() {
		gameServerView = View.inflate(getContext(), R.layout.discover_filter_content_list_two, null);
		gameNullBg = (TextView) gameServerView.findViewById(R.id.nullTxt);
		twoLayerRefreshLinearLayout = (LinearLayout) gameServerView.findViewById(R.id.twoLayerRefreshList);
		gameNullBg.setText("尚未找到游戏信息");
		gameRefreshParentList = (PullToRefreshListView) gameServerView.findViewById(R.id.parentRefreshList);
		gameRefreshChildList = (PullToRefreshListView) gameServerView.findViewById(R.id.childRefreshList);
	}

	/** 
	* @Title: setGamesData 
	* @Description: 设置游戏数据
	* @param voList    
	* @return void 
	* @throws 
	*/
	private void setGamesData(List<ExtraGameVo> voList) {
		gameServerParentAdpter.data.clear();
		if (voList != null && voList.size() > 0) {
			FilterVo afvo = new FilterVo(0, "全部", false);
			afvo.gid = 0;
			afvo.sid = 0;
			if(gid == 0 ){
				afvo.selected = true;
			}
			gameServerParentAdpter.data.add(afvo);
			int id = 0;
			final List<FilterVo> filtersVo = new ArrayList<FilterVo>();
			for (ExtraGameVo egVo : voList) {
				FilterVo fvo = new FilterVo(id, egVo.getgName(), false, egVo.getIcon(), true).setGidValue(egVo.getGid());
				if (egVo.getPriority() > 2) {
					if (egVo.getGid() == gid) {
						fvo.selected = true;
					}
					filtersVo.add(fvo);
				}
				id++;
			}
			gameServerParentAdpter.data.addAll(filtersVo);
			gameServerParentAdpter.notifyDataSetChanged();
		}
		if (gameServerParentAdpter.data.size() > 0) {
			twoLayerRefreshLinearLayout.setVisibility(View.VISIBLE);
			gameNullBg.setVisibility(View.GONE);
		} else {
			twoLayerRefreshLinearLayout.setVisibility(View.GONE);
			gameNullBg.setVisibility(View.VISIBLE);
		}
	}

	/** 
	* @Title: setGameServerData 
	* @Description: 		设置游戏服务器数据
	* @param clickGid
	* @param gameKeyVoMap    
	* @return void 
	* @throws 
	*/
	private void setGameServerData(long clickGid, Map<Long, List<GameKeyVo>> gameKeyVoMap) {
		if(gameServerParentAdpter.data.size()>0){
			gameServerChildAdpter.data.clear();
			//点击全部
			if(clickGid == 0){
				gameLevelString = "默认";
				FilterVo afvo = new FilterVo(0, "不限", false);
				afvo.sid = 0;
				afvo.gid = clickGid;
				if(gid == 0 && sid == 0 ){
					afvo.selected = true;
				}
				gameServerChildAdpter.data.add(afvo);
			}else{
				boolean temp = gid == clickGid;
				if (gameKeyVoMap != null && !gameKeyVoMap.isEmpty()) {
					if(gameKeyVoMap.containsKey(clickGid)){
						FilterVo afvo = new FilterVo(0, "不限", false);
						afvo.sid = 0;
						afvo.gid = clickGid;
						if(temp && sid == 0){
							afvo.selected = true;
						}
						gameServerChildAdpter.data.add(afvo);
						int id = 0;
						final List<FilterVo> filtersVo = new ArrayList<FilterVo>();
						boolean flag1 = false, flag2 = false;;
						for (GameKeyVo gkvo : gameKeyVoMap.get(clickGid)) {
							if (gkvo.getAttrType() == MsgsConstants.GAME_ROLE_KEY_SERVER) {
								for (GameKeysDetail gkd : gkvo.getList()) {
									FilterVo fvo = new FilterVo(id, gkd.getContent(), false);
									fvo.sid = gkd.getId();
									fvo.gid = clickGid;
									if(temp && sid == gkd.getId()){
										fvo.selected = true;
									}
									filtersVo.add(fvo);
									id++;
								}
								flag1 = true;
							}
							if (gkvo.getAttrType() == GAME_LEVEL) {
								gameLevelString = gkvo.getName();
								flag2 = true;
							}
							if(flag1 && flag2){
								break;//跳出循环
							}
							
						}
						gameServerChildAdpter.data.addAll(filtersVo);
					}
				}
			}
			gameServerChildAdpter.notifyDataSetChanged();
			if (gameServerChildAdpter.data.size() > 0) {
				twoLayerRefreshLinearLayout.setVisibility(View.VISIBLE);
				gameNullBg.setVisibility(View.GONE);
			} else {
				twoLayerRefreshLinearLayout.setVisibility(View.GONE);
				gameNullBg.setVisibility(View.VISIBLE);
			}
		}
	}

	/** 
	* @Title: setGameLevelData 
	* @Description: 		设置游戏段位i数据
	* @param gid
	* @param gameKeyVoMap    
	* @return void 
	* @throws 
	*/
	private void setGameLevelData(long gid, Map<Long, List<GameKeyVo>> gameKeyVoMap) {
		gameLevelAdpter.data.clear();
		if(gid == 0){
		}else {
			if (gameKeyVoMap != null && !gameKeyVoMap.isEmpty()) {
				if (gameKeyVoMap.containsKey(gid)) {
					int id = 0;
					final List<FilterVo> filtersVo = new ArrayList<FilterVo>();
					for (GameKeyVo gkvo : gameKeyVoMap.get(gid)) {
						if (gkvo.getAttrType() == GAME_LEVEL) {
							gameLevelString = gkvo.getName();
							for (GameKeysDetail gkd : gkvo.getList()) {
								FilterVo fvo = new FilterVo(id, gkd.getContent(), false);
								fvo.keyid = gkvo.getId();
								fvo.keyval = String.valueOf(gkd.getId());
								boolean keyidTemp = keyid !=null && gkvo.getId() == keyid.longValue();
								boolean keyvalueTemp = keyval !=null && String.valueOf(gkd.getId()).equals(keyval);
								if(keyidTemp && keyvalueTemp ){
									fvo.selected = true;
								}
								filtersVo.add(fvo);
								id++;
							}
							break;//找到段位后，跳出for循环
						}
					}
					gameLevelAdpter.data.addAll(filtersVo);
				}
			}
		}
		gameLevelAdpter.notifyDataSetChanged();
		gameLevelAdpter.notifyDataSetInvalidated();
		if (gameLevelAdpter.data.size() > 0) {
			gameLevelRefreshList.setVisibility(View.VISIBLE);
			gameLeverNullBg.setVisibility(View.GONE);
		} else {
			gameLevelRefreshList.setVisibility(View.GONE);
			gameLeverNullBg.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 刷新List选中列表
	 * 
	 * @param position
	 * @param gameServerFilerVo
	 * @param gameServerAdpter
	 */
	protected void RefreshSelectedFilterVo(int position,List<FilterVo> filerVoList, FilterAdapter<FilterVo> filterAdpter) {
		FilterVo fvo = filerVoList.get(position);
		if (!fvo.selected) {
			for (FilterVo filterVo : filerVoList) {
				filterVo.selected = false;
			}
			fvo.selected = true;
			filterAdpter.notifyDataSetChanged();
		}

	}
	
	/** 
	* @Title: setGameServerText 
	* @Description: 设置第一个筛选条件游戏的文本内容
	* @param position
	* @param filterAdapter    
	* @return void 
	* @throws 
	*/
	private void setGameServerText(int position, FilterAdapter<FilterVo> filterAdapter){
		String name = "";
		if (filterAdapter.data != null && filterAdapter.data.size() > 0) {
			for (FilterVo fvo : filterAdapter.data) {
				if (fvo.selected) {
					name = fvo.name;
				}
			}
		}
		//不限
		if(position == 0){
			if(parentPosition0==0){//全部,不限
				gameTxt.setText("游戏");
				gameLevelRefreshList.setVisibility(View.GONE);
				gameLeverNullBg.setVisibility(View.VISIBLE);
			}else{
				gameTxt.setText(gameName);
			}
		}else{
			gameTxt.setText(name);
		}
		
	}
	
	/** 
	* @Title: setGameLeverText 
	* @Description: 设置第二个筛选条件游戏等级的文本内容
	* @param position
	* @param filterAdapter    
	* @return void 
	* @throws 
	*/
	private void setGameLeverText(int position, FilterAdapter<FilterVo> filterAdapter){
		String name = "";
		if (filterAdapter.data != null && filterAdapter.data.size() > 0) {
			for (FilterVo fvo : filterAdapter.data) {
				if (fvo.selected) {
					name = fvo.name;
				}
			}
		}
		gameLevelTxt.setText(gameLevelString+"("+name+")");
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.iwgame.msgs.module.discover.ui.filter.FilterView#reset()
	 * 切换用户时候调用
	 */
	@Override
	public void reset() {
		LogUtil.info("lll reset()----------->");
		super.reset();
		//筛选重置
		resetField();
		gameTxt.setText(playFilterText[0]);
		gameNullBg.setVisibility(View.GONE);
		gameLevelTxt.setText(playFilterText[1]);
		intelligentTxt.setText("默认排序");
		sexTxt.setText(playFilterText[3]);
		// 初始化列表
		creatGameServerListView();
		createGameLevelListView();
		creatIntelligentListView();
		creatSexListView();
	}
	
	/** 
	* @Title: onDestroy 
	* @Description: 		    退出游戏
	* @return void 
	* @throws 
	*/
	public void onDestroy() {
		LogUtil.info("lll onDestroy()----------->");
		saveLastSelected();
	}
	
	
	/** 
	* @Title: saveLastSelected 
	* @Description: 		    保存数据
	* @return void 
	* @throws 
	*/
	private void saveLastSelected(){
		if (saveLastFlag) {
			// case one:
			SystemContext.getInstance().setDiscoverPlayGameIdSelected(gid);
			SystemContext.getInstance().setDiscoverPlayGameServerIdSelected(sid);
			// 保存TextView.text
			SystemContext.getInstance().setDiscoverPlayGameTabTextOne(gameTxt.getText().toString());
			// case two:
			SystemContext.getInstance().setDiscoverPlayGameLeverKeyIdSelected(String.valueOf(keyid));
			SystemContext.getInstance().setDiscoverPlayGameLeverKeyValueSelected(keyval);
			SystemContext.getInstance().setDiscoverPlayGameTabTextTwo(gameLevelTxt.getText().toString());
			// case three:
			SystemContext.getInstance().setDiscoverPlayIntelligentSelected(sorttype);
			// case four:
			SystemContext.getInstance().setDiscoverPlaySexSelected(sex);
		}
	}
	
	private void resetField() {
		gid = 0;
		sid = 0;
		sorttype = 0;
		keyid = null;
		keyval = null;
		sex = 0;
		parentPosition0 = 0;
	}
	
	private void resetLastSelected(){
		if(saveLastFlag){
			// case one:
			SystemContext.getInstance().setDiscoverPlayGameIdSelected(0);
			SystemContext.getInstance().setDiscoverPlayGameServerIdSelected(0);
			SystemContext.getInstance().setDiscoverPlayGameTabTextOne("游戏");
			// case two:
			SystemContext.getInstance().setDiscoverPlayGameLeverKeyIdSelected(null);
			SystemContext.getInstance().setDiscoverPlayGameLeverKeyValueSelected(null);
			SystemContext.getInstance().setDiscoverPlayGameTabTextTwo("默认");
			// case three:
			SystemContext.getInstance().setDiscoverPlayIntelligentSelected(0);
			// case four:
			SystemContext.getInstance().setDiscoverPlaySexSelected(0);
		}
	}
	
	
	
	
	/** 
	* @Title: getSharePreGameId 
	* @Description: 获取gid
	* @return    
	* @return Long 
	* @throws 
	*/
	private long getSharePreGameId() {
		return saveLastFlag ? SystemContext.getInstance().getDiscoverPlayGameIdSelected() : gid;
	}
	
	/** 
	* @Title: getSharePreGameServerId 
	* @Description: 获取sid
	* @return    
	* @return Long 
	* @throws 
	*/
	private long getSharePreGameServerId() {
		return  saveLastFlag ? SystemContext.getInstance().getDiscoverPlayGameServerIdSelected() : sid;
	}
	
	/** 
	* @Title: getSharePreGameLeverKeyId 
	* @Description: 获取keyid
	* @return    
	* @return Long 
	* @throws 
	*/
	private Long getSharePreGameLeverKeyId() {
		Long gameLeverId = null;
		try {
			gameLeverId = saveLastFlag ? Long.parseLong(SystemContext.getInstance().getDiscoverPlayGameLeverKeyIdSelected()) : keyid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameLeverId;
	}
	
	/** 
	* @Title: getSharePreGameLeverKeyValue 
	* @Description: 获取keyval
	* @return    
	* @return String 
	* @throws 
	*/
	private String getSharePreGameLeverKeyValue() {
		String gameLeverKeyValue = null;
		try {
			gameLeverKeyValue = saveLastFlag ? SystemContext.getInstance().getDiscoverPlayGameLeverKeyValueSelected() : keyval;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gameLeverKeyValue;
	}
	
	/** 
	* @Title: getIntillgentIndex 
	* @Description: 获取 intelligentIndex
	* @return    
	* @return int 
	* @throws 
	*/
	private int getIntillgentIndex(){
		return saveLastFlag ? SystemContext.getInstance().getDiscoverPlayIntelligentSelected() : sorttype;
	}
	
	
	/** 
	* @Title: sexIndex 
	* @Description: 获取sex
	* @return    
	* @return int 
	* @throws 
	*/
	private int getSexIndex(){
		return saveLastFlag ? SystemContext.getInstance().getDiscoverPlaySexSelected() : sex;
	}

	public Long getGid() {
		return gid;
	}

	public Long getSid() {
		return sid;
	}

	public int getSorttype() {
		return sorttype;
	}

	public Long getKeyid() {
		return keyid;
	}

	public String getKeyval() {
		return keyval;
	}

	public int getSex() {
		return sex;
	}

	public List<ExtraGameVo> getGameExtraGameVoList() {
		return gameExtraGameVoList;
	}

	public Map<Long, List<GameKeyVo>> getGameKeyVoMap() {
		return gameKeyVoMap;
	}

	/** 
	* @Title: setPlayFilterData 
	* @Description:     设置陪玩过滤内容
	* @return void 
	* @throws 
	*/
	public void setPlayFilterData(){
		
		ProxyFactory.getInstance().getUserProxy().getGameList(new ProxyCallBack<List<ExtraGameVo>>() {
			
			@Override
			public void onSuccess(List<ExtraGameVo> result) {
				LogUtil.info("lll setPlayFilterData Games 1111 onSuccess game List" );
				if(result!=null){
					gameExtraGameVoList.clear();
					gameKeyVoMap.clear();
					LogUtil.info("lll setPlayFilterData Games onSuccess result size = " + result.size() );
					boolean resetLastSelected = true;
					for(final ExtraGameVo egVo:result){
						if(egVo.getPriority()>2){
							gameExtraGameVoList.add(egVo);
							if (getSharePreGameId() != 0 && egVo.getGid() == getSharePreGameId()) {
								resetLastSelected = resetLastSelected && false;
							} 
							ProxyFactory.getInstance().getUserProxy().getGameKeyData(new ProxyCallBack<List<GameKeyVo>>() {
								
								@Override
								public void onSuccess(List<GameKeyVo> result) {
									gameKeyVoMap.put(egVo.getGid(), result);
									LogUtil.info("lll setPlayFilterData GameServices 2222 onSuccess gameKeyVoMap size =  " + gameKeyVoMap.size() );
								}
								
								@Override
								public void onFailure(Integer result, String resultMsg) {
									LogUtil.info("lll setPlayFilterData GameServices 2222  onFailure result = "+ result + ", resultMsg = " + resultMsg) ;
								}
								
							}, getContext(), egVo.getGid());
						}
					}
					LogUtil.info("lll setPlayFilterData Games size = " + gameExtraGameVoList.size() + ", resetLastSelected = " + resetLastSelected);
					if(resetLastSelected){
						resetLastSelected();
						resetField();
						resetTabText();
					}
				}
				if (mode == MODE_DISCOVER) {
					onRefreshData();
				}
			}
			
			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.info("lll setPlayFilterData Games 1111 onFailure result = "+ result + ", resultMsg = " + resultMsg) ;
				gameExtraGameVoList.clear();
				gameKeyVoMap.clear();
				if (mode == MODE_DISCOVER) {
					onRefreshData();
				}
				resetLastSelected();
			}
			
		}, getContext());
	}

}
