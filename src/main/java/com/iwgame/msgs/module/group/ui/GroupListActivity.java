/**      
 * GroupListActivity.java Create on 2013-10-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.group.adapter.GroupAdapter2;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs.ErrorCode;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.widget.listview.CommonListView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.SelectorUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GroupListActivity
 * @Description: 通用公会列表
 * @author 王卫
 * @date 2013-10-24 下午06:05:04
 * @Version 1.0
 * 
 */
public class GroupListActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = "GroupListActivity";
	// 贴吧ID
	private long gid;
	// 用户ID
	private long uid;
	// 性别
	private int sex;
	// 公会ID或关键字
	private String keyname;
	// 模式
	private int mode;
	// 容器
	private LinearLayout contentView;
	private ImageView rightMenu;
	// 当前加载的页数
	private int loadPageNo;
	private GroupVo groupVo;
	private GroupDao groupDao;
	private CommonListView nearView;
	private LayoutParams params;
	private GroupAdapter2 adapter;

	/**
	 * 当界面一启动的时候
	 * 就会执行下面的这个方法 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		nearView.refreshList();
	}
	
	
	
	/**
	 * activity的生命周期方法 
	 * 每次可见的时候都会执行下面这个方法 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		adapter.setFlag(true);
		nearView.adapter.notifyDataSetChanged();
		nearView.adapter.notifyDataSetInvalidated();
	}

	/**
	 * 初始化界面的功能 
	 */
	protected void initialize() {
		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			keyname = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_KEYWORD);
			gid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
			uid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UID);
			sex = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_SEX);
			mode = bundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE);
		}
		loadPageNo = 0;
		// 显示左边
		setLeftVisible(true);
		if (mode == GroupAdapter2.MODE_GAME_GROUP) {
			setRightVisible(false);
			titleTxt.setText("贴吧公会");
		} else if (mode == GroupAdapter2.MODE_GROUP_NAMEID) {
			setRightVisible(false);
			titleTxt.setText("搜索结果");
		} else if (mode == GroupAdapter2.MODE_RECOMMEND_GROUP) {
			titleTxt.setText("推荐公会");
			setRightVisible(true);
			rightMenu = new ImageView(this);
			rightMenu.setImageResource(R.drawable.common_tab_btn_in);
			LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
			rightView.addView(rightMenu, params);
			rightMenu.setOnClickListener(this);

		} else if (mode == GroupAdapter2.MODE_USER_GROUP) {
			setRightVisible(false);
			if (sex == 0)
				titleTxt.setText("他的公会");
			else if(sex == 1)
				titleTxt.setText("她的公会");
			else
				titleTxt.setText("Ta的公会");
		}else if(mode == GroupAdapter2.MODE_MY_GROUP){
			setRightVisible(false);
			titleTxt.setText("我的公会");
		}
		// 设置内容UI
		contentView = (LinearLayout) findViewById(R.id.contentView);
		nearView = new CommonListView(this, View.GONE){
			@Override
			public void getListData(long offset, int limit) {
				super.getListData(offset, limit);
				if(nearView.listData != null && nearView.listData.size() <= 0) nearView.setLoadingUI();
				GroupListActivity.this.adapter.setFlag(true);
				if (mode == GroupAdapter2.MODE_RECOMMEND_GROUP) {
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, new SyncCallBack() {

						@Override
						public void onSuccess(Object result) {
							final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
							List<GameVo> gvolist = gameDao.getGameListByRelation(1, 0, 1, Integer.MAX_VALUE);
							String games = getGameIds(gvolist);
							if (games != null) {
								searchRecommendGroup(games);
							} else {
								nearView.setRefreshMode(Mode.PULL_UP_TO_REFRESH);
								showNullBg();
							}
						}

						@Override
						public void onFailure(Integer result) {
							nearView.setRefreshMode(Mode.PULL_UP_TO_REFRESH);
							if(nearView.listData.size() <= 0) showNullBg();
							else
								nearView.showListView();
						}
					});
				} else {
					Integer source = null;
					if (mode == GroupAdapter2.MODE_GAME_GROUP) {
						source = 1;
					}
					ProxyFactory.getInstance().getGroupProxy().searchGroups(new ProxyCallBack<PagerVo<GroupVo>>() {

						@Override
						public void onSuccess(PagerVo<GroupVo> result) {
							nearView.setRefreshMode(Mode.PULL_UP_TO_REFRESH);
							nearView.showListView();
							if (nearView.offset == 0 || nearView.offset >= Integer.MAX_VALUE)
								clean();
							if (result != null && result.getItems() != null && result.getItems().size() > 0) {
								// 设置LIST数据
								addListData(result);
							} else {
								hasNext = false;
								showNullBg();
								LogUtil.d(TAG, "数据为空");
							}
							nearView.onFooterRefreshComplete();
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							nearView.setRefreshMode(Mode.PULL_UP_TO_REFRESH);
							showNullBg();
							LogUtil.e(TAG, "搜索公会数据失败");
							nearView.onFooterRefreshComplete();
						}
					}, GroupListActivity.this, keyname, uid == 0 ? null : uid, gid == 0 ? null : String.valueOf(gid), limit, offset, null, null, null, null, null, source, null);
				}
			}
		};
		nearView.limit = PagerVo.LIMIT;
		contentView.addView(nearView, params);
		if(mode == GroupAdapter2.MODE_RECOMMEND_GROUP){
			adapter = new GroupAdapter2(GroupListActivity.this, nearView.listData, R.layout.group_list_item_userfragment_rec,
					new String[] { "name", "desc1", "desc2" }, new int[] { R.id.gnameTxt, R.id.desc1, R.id.desc2 }, mode,nearView.list);
			nearView.setAdapter(adapter);
		}else{
			adapter = new GroupAdapter2(GroupListActivity.this, nearView.listData, R.layout.group_list_item_userfragment,
					new String[] { "name", "desc1", "desc2" }, new int[] { R.id.gnameTxt, R.id.desc1, R.id.desc2 }, mode,nearView.list);
			nearView.setAdapter(adapter);
		}
		// 添加列表点击功能
		setListItemClikEvent(nearView.list);
	}

	

	/**
	 * 当没有数据的时候
	 * 显示默认的背景图片
	 */
	private void showNullBg(){
		if(nearView.listData.size() <= 0){
			nearView.showNullBgView(R.drawable.common_no_seach_group);
		}else{
			nearView.showListView();
		}
	}
	
	/**
	 * 添加列表点击功能
	 * 
	 * @param list
	 */
	private void setListItemClikEvent(ListView list) {
		// 添加列表点击功能
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, Object> map = (HashMap<String, Object>) parent.getItemAtPosition(position);
				Long grid = (Long) map.get("grid");
				groupVo = groupDao.findGroupByGrid(grid);
				int rel;
				if(groupVo == null) rel = 0;
				else
					rel = groupVo.getRelWithGroup();
				if (rel == 0){
					Intent intent = new Intent(parent.getContext(), GroupDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					// 群聊
					Intent intent = new Intent(GroupListActivity.this, GroupChatFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, (Long) map.get("grid"));
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 搜素推荐公会(根据贴吧)
	 * 
	 * @param gids
	 */
	private void searchRecommendGroup(String gids) {
		ProxyFactory.getInstance().getGroupProxy().searchRecommendGroups(new ProxyCallBack<PagerVo<GroupVo>>() {

			@Override
			public void onSuccess(PagerVo<GroupVo> result) {
				nearView.setRefreshMode(Mode.PULL_UP_TO_REFRESH);
				nearView.showListView();
				if (nearView.offset == 0 || nearView.offset >= Integer.MAX_VALUE)
					nearView.clean();
				if (result != null && result.getItems() != null && result.getItems().size() > 0) {
					nearView.offset = result.getOffset();
					// 设置LIST数据
					addListData(result);
				} else {
					nearView.hasNext = false;
					LogUtil.d(TAG, "数据为空");
					showNullBg();
				}
				nearView.onFooterRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				nearView.setRefreshMode(Mode.PULL_UP_TO_REFRESH);
				showNullBg();
				LogUtil.e(TAG, "获取推荐公会失败");
				nearView.onFooterRefreshComplete();
			}
		}, this, gids, nearView.offset, Integer.MAX_VALUE);
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(PagerVo<GroupVo> pageVo) {
		loadPageNo++;
		// 添加设置LIST数据
		nearView.listData.addAll(praseGroupList(pageVo.getItems()));
		if (pageVo != null && pageVo.getItems() != null) {
			if (pageVo.getItems().size() < Math.abs(nearView.limit)) {
				nearView.hasNext = false;
			}
			if (pageVo.getItems().size() > Math.abs(nearView.limit)) {
				nearView.list.setSelectionFromTop(nearView.list.getFirstVisiblePosition(), 0);
			}
		}
		if (mode != GroupAdapter2.MODE_RECOMMEND_GROUP || mode != GroupAdapter2.MODE_GAME_GROUP) {
			nearView.offset = nearView.offset + Math.abs(nearView.limit);
		} else {
			if (nearView.listData != null && nearView.listData.size() > 0 && ((Map<String, Object>)nearView.listData.get(nearView.listData.size() - 1)).get("sortId") != null) {
				nearView.offset = (Long) ((Map<String, Object>)nearView.listData.get(nearView.listData.size() - 1)).get("sortId");
			} else {
				nearView.offset = pageVo.getOffset();
			}
		}
		nearView.adapter.notifyDataSetChanged();
		nearView.adapter.notifyDataSetInvalidated();
	}

	/**
	 * 解析列表数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseGroupList(List<GroupVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				GroupVo vo = list.get(i);
				map.put("avatar", vo.getAvatar());
				map.put("serial", vo.getSerial());				
				map.put("name", vo.getName());
				map.put("grid", vo.getGrid());
				map.put("desc1", "简介：" + vo.getUndesc());
				map.put("presidentId", vo.getPresidentId());
				int count = vo.getMaxcount();
				if(count <= 0){
					GroupGradeVo gradeVo = DaoFactory.getDaoFactory().getGroupGradeDao(SystemContext.getInstance().getContext()).queryByGrade(vo.getGrade());
					if(gradeVo != null)
						count = gradeVo.getMembers();
				}
				map.put("desc2", "人数：" + vo.getTotal()+"/"+count + " | 会长：" + vo.getPresidentName());
				map.put("sortId", vo.getSortId());
				if (!map.containsKey("isChecked")) {
					if (loadPageNo == 1 && (i == 0 || i == 1)) {
						map.put("isChecked", true);
					} else {
						map.put("isChecked", false);
					}
				}
				map.put("gameIcon", vo.getGameIcon());
				map.put("gid", vo.getGid());
				map.put("grade", vo.getGrade());
				tmplist.add(map);
			}
		}
		return tmplist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getId() == rightMenu.getId()) {// 提交推荐贴吧
			praseFollowGroup(nearView.listData);
		}
	}

	/**
	 * 
	 * @return
	 */
	private void praseFollowGroup(List<Map<String, Object>> tmplist) {
		if (tmplist != null) {
			boolean hasCheck = false;
			for (int i = 0; i < tmplist.size(); i++) {
				Object isChecked = tmplist.get(i).get("isChecked");
				if (isChecked != null && (Boolean) isChecked) {
					hasCheck = true;
					joinGroup((Long) tmplist.get(i).get("grid"));
				}
			}
			if (!hasCheck) {
				ToastUtil.showToast(this, "请选择关注公会");
			} else {
				String groups = getGroupIds(tmplist);
				SystemContext.getInstance().setRecommendGroups(groups);
				finish();
			}
		} else {
			ToastUtil.showToast(this, "请选择关注公会");
		}
	}

	/**
	 * 关注贴吧
	 * 
	 * @param gid
	 */
	private void joinGroup(final long grid) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
		dialog.show();
		String content = null;
		if(AdaptiveAppContext.getInstance().getAppConfig().getMode() == 1) content = "recommend";
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				switch (result) {
				case ErrorCode.EC_OK_VALUE:
					ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GROUP, null);
					break;

				default:
					break;
				}
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "加入公会失败");
				dialog.dismiss();
			}
		}, this, grid, MsgsConstants.OT_GROUP, MsgsConstants.OP_AGREE_JOIN_GROUP, content, null,null);
	}

	/**
	 * 获取贴吧IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getGameIds(List<GameVo> list) {
		if (list != null) {
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				strBuf.append(list.get(i).getGameid());
				strBuf.append(",");
			}
			if (strBuf.length() > 0)
				return strBuf.substring(0, strBuf.length() - 1);
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 获取公会IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getGroupIds(List<Map<String, Object>> idlist) {
		if (idlist != null) {
			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < idlist.size(); i++) {
				Object isChecked = idlist.get(i).get("isChecked");
				if (isChecked != null && (Boolean) isChecked) {
					strBuf.append(idlist.get(i).get("grid"));
					strBuf.append(",");
				}
			}
			if (strBuf.length() > 0)
				return strBuf.substring(0, strBuf.length() - 1);
		}
		return null;
	}

}
