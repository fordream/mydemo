/**      
 * GameFragment2.java Create on 2014-4-9     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.game.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.game.adapter.TopicNewsAdapter;
import com.iwgame.msgs.module.game.adapter.TopicNewsAdapter.OnTopicNewsListener;
import com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter;
import com.iwgame.msgs.module.postbar.ui.TopicDetailActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicListResult;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.msgs.widget.AddButton;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.NetworkUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GameFragment2
 * @Description: 贴吧主界面
 * @author chuanglong
 * @date 2014-4-9 下午2:06:40
 * @Version 1.0
 * 
 */
public class GameFragment2 extends BaseFragment implements OnItemClickListener, OnRefreshListener2<ListView> {

	protected static final String TAG = "GameFragment";
	
	private static GameFragment2 instance;
	
	private LayoutInflater inflater;
	private long uid;
	// 关注的贴吧
	List<GameVo> followGameData;
	// 关注贴吧的最大Index
	private Button addBtn;
	// 贴吧动态
	List<Object> topicNewsData = new ArrayList<Object>();
	TopicNewsAdapter toipcNewsAdapter;
	View v = null;

	PullToRefreshListView pullToRefreshListView;

	// 是否正在获得我的关注贴吧
	private boolean isGetMyFollowGames = false;
	// 动态是否加载过了
	private boolean inited = false;
	private RelativeLayout loading_layout;
	private boolean followGameLoaded = false;
	private boolean unreadLoaded = false;
	private boolean newsLoaded = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		instance = this;
		PTAG = TAG;
	}
	
	public static GameFragment2 getInstance() {
		return instance;
	}
	
	/**
	 * 定位到顶部
	 */
	public void goTop(){
		pullToRefreshListView.getRefreshableView().setSelection(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (SystemContext.getInstance().isUnAuth()) {
			// 会话失效， 主动注销，token不存在/认证失败，都到登录页
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED, true);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
			addBtn.setEnabled(true);// 添加按钮可用
			// 加载数据
			loadData();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

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
		ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
		if (vo != null) {
			long newuid = vo.getUserid();
			if (newuid != uid) {
				uid = newuid;
				v = null;
			}
		}
		if (v == null) {
			v = inflater.inflate(R.layout.common_content, container, false);
			// 初始化界面
			init(v);
		}
		ViewGroup parent = (ViewGroup) v.getParent();
		if (parent != null) {
			parent.removeView(v);
		}
		return v;
	}

	/**
	 * 初始化
	 */
	private void init(View v) {
		loading_layout = (RelativeLayout) v.findViewById(R.id.loading_layout);
		loading_layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return true;
			}
		});
		ImageView loadingImageView = (ImageView) v.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) loadingImageView.getBackground();
		animationDrawable.start();
		v.findViewById(R.id.bottomHitView).setVisibility(View.VISIBLE);
		// 隐藏top左边菜单
		LinearLayout left = (LinearLayout) v.findViewById(R.id.left);
		left.setVisibility(View.INVISIBLE);
		// 设置title
		TextView titleTxt = (TextView) v.findViewById(R.id.titleTxt);
		titleTxt.setText(getResources().getString(R.string.game_title_info));

		// 添加top右边功能按钮
		addBtn = new AddButton(v.getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout rightView = (LinearLayout) v.findViewById(R.id.rightView);
		rightView.addView(addBtn, params);
		addBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addBtn.setEnabled(false);// 设置为不可用，防止快速点击
				// 跳转到添加关注贴吧主界面
				Intent intent = new Intent(v.getContext(), RecommendGameActivity.class);
				startActivity(intent);
			}
		});

		// 设置中间内容的布局文件
		LinearLayout contentView = (LinearLayout) v.findViewById(R.id.contentView);
		contentView.removeAllViews();
		final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_game_content, null);
		contentView.addView(view, params);
		// 上拉加载跟过，下拉刷新组件（ListView）
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.refreshList);
		pullToRefreshListView.setMode(Mode.BOTH);
		final ILoadingLayout headerLabels = pullToRefreshListView.getLoadingLayoutProxy(true, false);
		final ILoadingLayout footerLabels = pullToRefreshListView.getLoadingLayoutProxy(false, true);
		headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setOnItemClickListener(this);

		toipcNewsAdapter = new TopicNewsAdapter(getActivity(), topicNewsData, new OnTopicNewsListener() {

			@Override
			public void unreadLoadCompleted() {
				unreadLoaded = true;
				if (followGameLoaded && unreadLoaded && newsLoaded)
					loading_layout.setVisibility(View.GONE);
			}
		});
		if (followGameData == null) {
			followGameData = new ArrayList<GameVo>();
			topicNewsData.add(followGameData);
		}
		pullToRefreshListView.setAdapter(toipcNewsAdapter);
		pullToRefreshListView.setOnItemClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		int realposition = (int) parent.getAdapter().getItemId(position);
		Object object = topicNewsData.get(realposition);
		if (realposition != -1 && realposition != 0 && object instanceof Msgs.PostbarTopicDetail) {
			Intent intent = null;
			Bundle bundle = null;
			intent = new Intent(this.getActivity(), TopicDetailActivity.class);
			bundle = new Bundle();
			bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, ((Msgs.PostbarTopicDetail) object).getId());
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, ((Msgs.PostbarTopicDetail) object).getGameid());
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POSTBAR_DETAIL_MODE, TopicReplyListAdapter.MODE_POSTBAR_SHOW);
			bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, ((Msgs.PostbarTopicDetail) object).getPostbarName());
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			startActivity(intent);
		}
	}

	/**
	 * 获得关注的贴吧
	 */
	private void loadData() {
		if (!inited && NetworkUtil.isNetworkAvailable(getActivity()))
			loading_layout.setVisibility(View.VISIBLE);
		getMyFollowGame();
	}

	/**
	 * 获取我关注的贴吧
	 * 
	 * @param isGetNews
	 *            是否需要获得贴吧排行
	 */
	private void getMyFollowGame() {
		final boolean reload;
		if (SystemContext.getInstance().isReloadTag()) {
			reload = true;
			toipcNewsAdapter.cleanData();
			followGameData.clear();
			toipcNewsAdapter.notifyDataSetChanged();
			SystemContext.getInstance().setReloadTag(false);
		} else {
			reload = false;
		}
		if (isGetMyFollowGames)
			return;
		isGetMyFollowGames = true;
		ProxyCallBack<List<GameVo>> callback = new ProxyCallBack<List<GameVo>>() {

			@Override
			public void onSuccess(List<GameVo> result) {
				followGameLoaded = true;
				if (followGameLoaded && unreadLoaded && newsLoaded)
					loading_layout.setVisibility(View.GONE);
				isGetMyFollowGames = false;
				int followSize = followGameData.size();
				int rsize = result.size();
				followGameData.clear();
				if (result != null && rsize > 0) {
					followGameData.addAll(result);
				} else {
					// 增加默认的增加贴吧的图片
					followGameData.add(getDefaultAddGame());
				}
				// 获得贴吧排行
				if (!inited || reload || followSize != rsize) {
					getTopicNews(true);
					inited = true;
				}
				toipcNewsAdapter.notifyDataSetChanged();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				followGameLoaded = true;
				if (followGameLoaded && unreadLoaded && newsLoaded)
					loading_layout.setVisibility(View.GONE);
				isGetMyFollowGames = false;
				if (followGameData.size() < 1) {
					// 增加默认的增加贴吧的图片
					followGameData.add(getDefaultAddGame());
				}
				// 获得贴吧排行
				if (!inited || reload) {
					getTopicNews(true);
					inited = true;
				}
				toipcNewsAdapter.notifyDataSetChanged();
			}
		};
		ProxyFactory.getInstance().getGameProxy().getFollowGames(callback, getActivity(), true);
	}

	/**
	 * 参数一个gid = -1 的伪贴吧对象
	 * 
	 * @return
	 */
	private GameVo getDefaultAddGame() {
		GameVo vo = new GameVo();
		vo.setGameid(-1);
		return vo;
	}

	/**
	 * 获取贴吧动态
	 */
	private void getTopicNews(final boolean isClear) {
		long offset = 0;
		if (!isClear)
			offset = topicNewsData.size() > 0 ? topicNewsData.size() - 1 : 0;
		ProxyFactory.getInstance().getPostbarProxy().searchTopicNews(new ProxyCallBack<Msgs.PostbarTopicListResult>() {

			@Override
			public void onSuccess(PostbarTopicListResult result) {
				newsLoaded = true;
				if (followGameLoaded && unreadLoaded && newsLoaded)
					loading_layout.setVisibility(View.GONE);
				if (isClear) {
					toipcNewsAdapter.cleanPraiseData();
					topicNewsData.clear();
					topicNewsData.add(followGameData);
				}
				if (result != null) {
					List<PostbarTopicDetail> pList = result.getEntryList();
					if (pList != null) {
						topicNewsData.addAll(pList);
					}
				}
				toipcNewsAdapter.notifyDataSetChanged();
				if(isClear)
					pullToRefreshListView.getRefreshableView().setSelection(0);
				pullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				newsLoaded = true;
				if (followGameLoaded && unreadLoaded && newsLoaded)
					loading_layout.setVisibility(View.GONE);
				if (isClear) {
					toipcNewsAdapter.cleanPraiseData();
					topicNewsData.clear();
					topicNewsData.add(followGameData);
					toipcNewsAdapter.notifyDataSetChanged();
				}
				pullToRefreshListView.onRefreshComplete();
			}
		}, getActivity(), offset, SystemConfig.PAGE_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2
	 * #onPullDownToRefresh
	 * (com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		getTopicNews(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2
	 * #onPullUpToRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		getTopicNews(false);
	}

}
