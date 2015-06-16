/**      
 * GameSocietyFragment.java Create on 2013-9-3     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.discover.ui.filter.FilterView;
import com.iwgame.msgs.module.discover.ui.filter.GroupFilterView;
import com.iwgame.msgs.module.group.adapter.GroupAdapter2;
import com.iwgame.msgs.module.group.ui.CreatGroupActivity;
import com.iwgame.msgs.module.group.ui.GroupDetailActivity;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GameSocietyFragment
 * @Description: 贴吧-公会
 * @author 王卫
 * @date 2013-9-3 下午05:02:46
 * @Version 1.0
 * 
 */
public class GameDetailGroupActivity extends BaseActivity implements OnClickListener {

	protected static final String TAG = "GameDetailGroupFragment";

	// 贴吧ID
	private long gid;

	private String gname;

	// 可刷新主容器
	private PullToRefreshListView mPullRefreshListView;
	// 列表
	protected ListView list;
	// 列表数据源
	protected List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
	// 数据源适配器
	protected SimpleAdapter adapter;
	// 当前页码
	protected long offset = 0;
	// 每页大小(默认降序向下取)
	protected int limit = 10;
	// 是否有下一页
	protected boolean hasNext = true;

	private LinearLayout listContent;
	// 设置菜单按钮
	private Button rightMenu;

	// 我是几个会长的个数
	private int presCount = 0;

	private GroupDao groupDao;

	private GroupVo groupVo;

	private LinearLayout nullContent;

	// 公会是否要验证
	private Boolean selectVerify = null;
	// 公会服务器类型
	private String groupSids = null;

	private CustomProgressDialog loadDialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.common.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadDialog = CustomProgressDialog.createDialog(this);
		loadDialog.setCanceledOnTouchOutside(false);// 点击其他区域dialog不消失
		// 获取上一个页面的传值
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			gid = bundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID);
			gname = bundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME);
		}
		groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		// 设置内容UI
		getContentView().addView(View.inflate(this, R.layout.common_filter_list, null),
				new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		// 设置列表功能
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.refreshList);
		list = mPullRefreshListView.getRefreshableView();
		mPullRefreshListView.setMode(Mode.PULL_FROM_END);
		final ILoadingLayout footerLabels = mPullRefreshListView.getLoadingLayoutProxy(false, true);
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				if (hasNext)
					getListData(offset, limit);
				else
					new GetDataTask().execute();
			}
		});
		nullContent = (LinearLayout) findViewById(R.id.nullContent);
		listContent = (LinearLayout) findViewById(R.id.listContent);
		adapter = new GroupAdapter2(GameDetailGroupActivity.this, listData, R.layout.group_list_item_userfragment, new String[] { "name", "desc1",
				"desc2" }, new int[] { R.id.gnameTxt, R.id.desc1, R.id.desc2 }, list);
		list.setAdapter(adapter);
		// 添加列表点击功能
		setListItemClikEvent(list);
		// 添加公会过滤内容
		GameVo gameVo = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext()).getGameById(gid);
		((LinearLayout) findViewById(R.id.filterContent)).addView(new GroupFilterView(this, new FilterView.RefreshDataListener() {

			@Override
			public void onRefreshData() {

			}

			@Override
			public void onRefreshGroup(Boolean verify, String sids) {
				groupSids = sids;
				selectVerify = verify;
				refreshList();
				loadDialog.show();
			}

			@Override
			public void onRefreshUser(Integer gender, Integer time, String sids) {
				// TODO Auto-generated method stub

			}
		}, View.GONE, (gameVo != null && gameVo.getGtype() == 0) ? View.GONE : View.VISIBLE, FilterView.MODE_COMMON), new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		// 显示左边
		setLeftVisible(true);
		// 添加右边功能按钮
		setRightVisible(true);
		rightMenu = new Button(this);
		rightMenu.setBackgroundResource(R.drawable.group_creat_menu);
		LinearLayout rightView = (LinearLayout) findViewById(R.id.rightView);
		rightView.addView(rightMenu);
		rightMenu.setOnClickListener(this);
		// 设置TITLE
		setTitleTxt("公会");
		if (SystemContext.getInstance().getExtUserVo() != null)
			refreshList();
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();
			super.onPostExecute(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		((GroupAdapter2) adapter).setFlag(true);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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
				if (groupVo == null)
					rel = 0;
				else
					rel = groupVo.getRelWithGroup();
				if (rel == 0) {
					Intent intent = new Intent(parent.getContext(), GroupDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID, grid);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					// 群聊
					Intent intent = new Intent(GameDetailGroupActivity.this, GroupChatFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, (Long) map.get("grid"));
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 清除列表数据,重新渲染列表
	 */
	protected void refreshList() {
		if (list != null) {
			if (listData != null && adapter != null) {
				hasNext = true;
				offset = 0;
			}
			if (NetworkUtil.isNetworkAvailable(this)) {
				getListData(offset, limit);
			} else {
				ToastUtil.showToast(this, getString(R.string.network_error));
			}
		} else {
			return;
		}
	}

	/**
	 * 获取列表数据
	 */
	protected void getListData(final long offset, int limit) {
		if (listData != null && listData.size() <= 0)
			setLoadingUI();
		ProxyFactory
				.getInstance()
				.getGroupProxy()
				.searchGroups(new ProxyCallBack<PagerVo<GroupVo>>() {

					@Override
					public void onSuccess(PagerVo<GroupVo> result) {
						if (loadDialog.isShowing())
							loadDialog.dismiss();
						showListView();
						((GroupAdapter2) adapter).setFlag(true);
						if (result != null && result.getItems() != null && result.getItems().size() > 0) {
							if (offset == 0)
								listData.clear();
							// 设置LIST数据
							addListData(result);
						} else {
							LogUtil.d(TAG, "数据为空");
						}
						if (listData.size() <= 0) {
							addNullDataView(nullContent);
						} else {
							showListView();
						}
						onFooterRefreshComplete();
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if (loadDialog.isShowing())
							loadDialog.dismiss();
						if (offset == 0) {
							listData.clear();
							adapter.notifyDataSetChanged();
							adapter.notifyDataSetInvalidated();
						} else {
							showListView();
						}
						if (listData.size() <= 0) {
							addNullDataView(nullContent);
						} else {
							showListView();
						}
						onFooterRefreshComplete();
					}
				}, GameDetailGroupActivity.this, null, null, gid == 0 ? null : String.valueOf(gid), limit, offset, null, null, null, selectVerify,
						null, null, groupSids);
	}

	/**
	 * 第一次进入的时候，在顶部显示一个加载的loading 显示加载的loading
	 */
	public void setLoadingUI() {
		showNullBgView();
		nullContent.removeAllViews();
		LinearLayout view = (LinearLayout) View.inflate(this, R.layout.frame_donghua, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ImageView ivLoading = (ImageView) view.findViewById(R.id.loading_iv);
		AnimationDrawable animationDrawable = (AnimationDrawable) ivLoading.getBackground();
		animationDrawable.start();
		nullContent.addView(view, params);
	}

	/**
	 * 显示 的是空背景
	 */
	private void showNullBgView() {
		nullContent.setVisibility(View.VISIBLE);
		listContent.setVisibility(View.GONE);
	}

	/**
	 * 当加载数据回来的时候 显示的背景切换
	 */
	private void showListView() {
		nullContent.setVisibility(View.GONE);
		listContent.setVisibility(View.VISIBLE);
	}

	/**
	 * 
	 */
	private void addNullDataView(LinearLayout view) {
		view.removeAllViews();
		if (selectVerify == null && groupSids == null) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			LinearLayout v = (LinearLayout) View.inflate(this, R.layout.game_group_creat_view, null);
			view.addView(v, params);
			v.findViewById(R.id.creatGroup).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					getGroupData();
				}
			});
		} else {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			LinearLayout v = new LinearLayout(this);
			view.addView(v, params);
			ImageView iv = new ImageView(this);
			params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			iv.setBackgroundResource(R.drawable.common_no_seach_group);
			v.setGravity(Gravity.CENTER);
			v.addView(iv, params);
		}
		showNullBgView();
	}

	/**
	 * 添加列表数据
	 * 
	 * @param result
	 */
	private void addListData(PagerVo<GroupVo> pageVo) {
		// offset = pageVo.getOffset();
		// 添加设置LIST数据
		listData.addAll(praseList(pageVo.getItems()));
		if (pageVo.getItems() != null) {
			if (pageVo.getItems().size() < Math.abs(limit)) {
				hasNext = false;
			}
			if (pageVo.getItems().size() > 0) {
				list.setSelectionFromTop(list.getFirstVisiblePosition(), 0);
			}
		}
		offset = offset + Math.abs(limit);
		adapter.notifyDataSetChanged();
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 解析列表数据
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> praseList(List<GroupVo> list) {
		List<Map<String, Object>> tmplist = new ArrayList<Map<String, Object>>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				GroupVo vo = list.get(i);
				map.put("avatar", vo.getAvatar());
				map.put("serial", vo.getSerial());
				map.put("name", vo.getName());
				map.put("grid", vo.getGrid());
				map.put("gid", vo.getGid());
				map.put("desc1", "简介：" + vo.getUndesc());
				map.put("presidentId", vo.getPresidentId());
				GroupGradeVo gradeVo = DaoFactory.getDaoFactory().getGroupGradeDao(SystemContext.getInstance().getContext())
						.queryByGrade(vo.getGrade());
				map.put("desc2", "人数：" + vo.getTotal() + "/" + (gradeVo == null ? 0 : gradeVo.getMembers()) + " | 会长：" + vo.getPresidentName());
				map.put("gameIcon", vo.getGameIcon());
				map.put("grade", vo.getGrade());
				tmplist.add(map);
			}
		}
		return tmplist;
	}

	public void onFooterRefreshComplete() {
		mPullRefreshListView.onRefreshComplete();
	}

	public void onHeaderRefreshComplete(CharSequence lastupdated) {
		mPullRefreshListView.onRefreshComplete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v == rightMenu) {
			rightMenu.setEnabled(false);
			getGroupData();
		}
	}

	/**
	 * 获取公会数据
	 */
	private void getGroupData() {
		presCount = 0;
		List<GroupVo> list = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getInstance().getContext()).findAllGroups();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				GroupVo vo = list.get(i);
				if (((Long) vo.getPresidentId()).equals(SystemContext.getInstance().getExtUserVo().getUserid()))
					presCount++;
			}
		}

		addGroupAction();
	}

	/**
	 * 如果是游客的情况下 点击绑定按钮 跳转到绑定手机的页面 弹出绑定手机的对话框
	 * 
	 * @param actionName
	 */
	private void createBundPhoneDialog() {
		Intent intent = new Intent(GameDetailGroupActivity.this, BundPhoneActivity.class);
		GameDetailGroupActivity.this.startActivity(intent);
	}

	/**
	 * 创建公会行为判断
	 */
	private void addGroupAction() {
		int gcm = SystemContext.getInstance().getGCM();
		if (presCount < gcm) {
			ProxyFactory.getInstance().getGroupProxy().getGroupGradePolicy(new ProxyCallBack<List<GroupGradeVo>>() {

				@Override
				public void onSuccess(List<GroupGradeVo> result) {
					for (Iterator iterator = result.iterator(); iterator.hasNext();) {
						GroupGradeVo groupGradeVo = (GroupGradeVo) iterator.next();
						if (groupGradeVo.getGrade() == 1) {
							final int point = groupGradeVo.getPoint();
							ProxyFactory.getInstance().getUserProxy().getUserPoint(new ProxyCallBack<List<ExtUserVo>>() {

								@Override
								public void onSuccess(List<ExtUserVo> result) {
									if (result != null && result.size() == 1) {
										ExtUserVo vo = result.get(0);
										int upoint = vo.getPoint();
										if (upoint >= point) {
											creatGroupPointDoalog(point);
										} else {
											ToastUtil.showToast(GameDetailGroupActivity.this, getString(R.string.group_creat_point_notenough));
										}
									} else {
										ToastUtil.showToast(GameDetailGroupActivity.this, getString(R.string.group_creat_point_notenough));
									}
									rightMenu.setEnabled(true);
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									rightMenu.setEnabled(true);
								}
							}, GameDetailGroupActivity.this, SystemContext.getInstance().getExtUserVo().getUserid() + "");
							break;
						}
					}
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					rightMenu.setEnabled(true);
				}
			}, this);
		} else {
			String tip = String.format(getString(R.string.group_creat_count_max), SystemContext.getInstance().getGCM());
			ToastUtil.showToast(this, tip);
			rightMenu.setEnabled(true);
		}
	}

	/**
	 * 创建公会消耗积分提醒
	 */
	private void creatGroupPointDoalog(int point) {
		final Dialog dialog = new Dialog(this, R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("消耗积分提醒");
		final TextView txt = new TextView(this);
		txt.setTextColor(getResources().getColor(R.color.darkgray));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText(String.format("创建公会，需要耗费您%1$d积分哦！", point));
		content.setPadding(DisplayUtil.dip2px(this, 10), 10, DisplayUtil.dip2px(this, 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GameDetailGroupActivity.this, CreatGroupActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, gid);
				bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, gname);
				intent.putExtras(bundle);
				startActivity(intent);
				dialog.dismiss();
				GameDetailGroupActivity.this.finish();
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

}
