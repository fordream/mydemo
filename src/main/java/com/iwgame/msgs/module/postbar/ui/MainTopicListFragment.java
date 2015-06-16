/**      
 * PostBarFragment.java Create on 2013-12-20     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase.MySrcollListenerTwo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.NoDataListener;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.game.object.GameData;
import com.iwgame.msgs.module.game.object.GameExtDataVo;
import com.iwgame.msgs.module.game.object.GameNoData;
import com.iwgame.msgs.module.postbar.adapter.ChildItem;
import com.iwgame.msgs.module.postbar.adapter.MainTopicListAdapter;
import com.iwgame.msgs.module.postbar.adapter.MainTopicListAdapter.ItemClickListener;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.DialogUtil.OKCallBackListener;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.MenuMoreImageView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: TopicListFragment
 * @Description: 贴吧主题列表
 * @author chuanglong
 * @date 2013-12-20 下午3:20:31
 * @Version 1.0
 * 
 */
public class MainTopicListFragment extends BaseFragment implements OnItemClickListener, OnItemLongClickListener, OnRefreshListener2<ListView> {
	private static final String TAG = "PostBarFragment";
	private LayoutInflater inflater;
	LinearLayout postbar_topic_search;

	/**
	 * 没有数据时的界面
	 */
	LinearLayout nullContent;
	/**
	 * 没有数据时显示的图片
	 */
	ImageView nullContentBgIcon;

	PullToRefreshListView pullToRefreshListView;
	public int listViewHeight;
	public int firstItemHeight;

	private PopupWindow popupWindow;// 更多的排序方式
	private boolean isHasScroll = false;

	/**
	 * 选项卡组件对象
	 */

	RadioButton postbar_seq_newcomment;

	List<Object> listdata = new ArrayList<Object>();
	MainTopicListAdapter listAdapter;

	private int targetType = SystemConfig.GETTOPICLIST_TARGETTYPE_GAME;
	private long targetId = 0;
	// 是否是吧主
	private boolean isMaster = false;

	private boolean isNetPro = false;

	String title;
	/**
	 * 当前显示内容的排序条件
	 */
	int order = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;
	/**
	 * 当前显示的内容的tagid
	 */
	int tagid = -1;
	String tagName;
	/**
	 * 当前显示内容的过滤条件
	 */
	int filter = 0;

	private String pageFrom;

	long uid;
	// 没有数据时的通知
	NoDataListener noDataListener;

	public ImageView rightMenu;
	private LinearLayout right;

	private LinearLayout extendView;
	private ImageView shareTopicMenu;
	private ImageView sendPostBtn;
	// 筛选标题（全部、最新、最热）
	private TextView allTxt;
	private TextView newTxt;
	private TextView hotTxt;

	GameData gameData;

	GameVo gameVo;
	/**
	 * 贴吧扩展信息
	 */
	GameExtDataVo gameExtDataVo;
	/**
	 * 是否关注
	 */
	boolean isfollow;

	public List<TopicTagVo> tags = null;

	private HashMap<String, Integer> loadFlag = new HashMap<String, Integer>();

	public static int STATUS_LOAD_ING = 1;
	public static int STATUS_LOAD_OK = 0;
	public static int STATUS_LOAD_ERR = 2;

	/**
	 * 是否第一次加载
	 */
	boolean isfirstLoad = false;

	/**
	 * 是否需要显示加载进度条
	 */
	boolean isshowdialog = false;

	/**
	 * 是否已经获取过扩展信息，防止网络异常后重新点击，增加访问数
	 */
	boolean isGetExtData = false;

	/**
	 * 发布按钮是否可以点击开关，用于防止快速点击
	 */
	boolean isClickable_publish = true;

	/**
	 * 改成true
	 */
	private final int WHAT_CHANGECLICKABLE_PUBLISH_TRUE = 1;

	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case WHAT_CHANGECLICKABLE_PUBLISH_TRUE:
				isClickable_publish = true;
				break;
			}
		}
	};

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
			targetType = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETTYPE, 0);
			targetId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TARGETID, 0);
			Serializable data = tmpbundle.getSerializable(SystemConfig.BUNDLE_NAME_TOPIC_TAGS);
			if (tmpbundle.containsKey("From")) {
				pageFrom = tmpbundle.getString("From");
			}
			if (data != null)
				tags = (List<TopicTagVo>) data;
			tagid = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGID, 0);
		}
		isfirstLoad = true;

		if (pageFrom != null && pageFrom.equals("GameRegiestRecommendActivity")) {
			order = MsgsConstants.POSTBAR_ORDER_REPLY_NUMS;// 最热帖
		} else {
			order = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;// 最新回复
		}
		// 添加动态主界面
		View v = inflater.inflate(R.layout.topiclist_main, container, false);
		// 初始化界面
		init(v);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictModeWrapper.init(this.getActivity());
		super.onCreate(savedInstanceState);
		PTAG = TAG;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		getGameInfo(targetId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void viewInited() {
		listViewHeight = pullToRefreshListView.getHeight();
		firstItemHeight = DisplayUtil.dip2px(getActivity(), (float) 131.5);
	}

	/**
	 * 初始化
	 * 
	 * @param v
	 */
	private void init(View v) {
		// 初始化头部分
		initHeader(v);

		// 设置中间内容的布局文件
		LinearLayout contentView = (LinearLayout) v.findViewById(R.id.contentView);
		contentView.removeAllViews();
		final RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.postbar_main_topic_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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
		pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(this);
		pullToRefreshListView.post(new Runnable() {

			@Override
			public void run() {
				viewInited();
			}
		});
		pullToRefreshListView.setMyScrollListenerT(new MySrcollListenerTwo() {

			@Override
			public void actionMyScrollEvent(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// System.out.println("<<<<---->>>> firstVisibleItem = " +
				// firstVisibleItem + " visibleItemCount = " + visibleItemCount
				// + " totalItemCount = " + totalItemCount);
				int lastVisibleItem = firstVisibleItem + visibleItemCount;

				if (totalItemCount >= SystemConfig.PAGE_SIZE + 3) {
					if (totalItemCount == SystemConfig.PAGE_SIZE + 3 || lastVisibleItem == totalItemCount - SystemConfig.PAGE_SIZE) {// 可见区域最后一个为小组数据的第一个，加载下一页
						// System.out.println("<<<<---->>>> 满足条件，进行判断……");
						if (loadFlag.containsKey(totalItemCount + "") && loadFlag.get(totalItemCount + "") != STATUS_LOAD_ERR) {// 加载中或正在加载
							// System.out.println("<<<<---->>>> 正在加载或已经加载过……");
						} else {
							loadFlag.put(totalItemCount + "", STATUS_LOAD_ING);
							onPullUpToRefresh(pullToRefreshListView);
							// loadData();
							// System.out.println("<<<<---->>>> 开始加载……");
						}
					}
				}
			}
		});

		if (pageFrom != null && pageFrom.equals("GameRegiestRecommendActivity")) {// 最热帖
			//
		} else {// 最新回复
			//
		}
		// 发帖
		sendPostBtn = (ImageView) view.findViewById(R.id.sendPostBtn);
		sendPostBtn.setEnabled(false);
		sendPostBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (FastClickLimitUtil.isFastClick()) {
					return;
				}

				if (getActivity() instanceof GameTopicListActivity)
					((GameTopicListActivity) getActivity()).hideMenu();
				publishTopicButtonClick();
			}
		});

		listdata = new ArrayList<Object>();
		gameData = new GameData();
		gameData.setGid(targetId);
		listdata.add(gameData);

		ItemClickListener itemClickListener = new ItemClickListener() {
			@Override
			public void onClickAction(final int position, int action) {
				if (action == MainTopicListAdapter.ACTION_DEL) {
					final TextView txt = new TextView(getActivity());
					txt.setPadding(0, getActivity().getResources().getDimensionPixelSize(R.dimen.global_page_paddingtop), getActivity()
							.getResources().getDimensionPixelSize(R.dimen.global_page_paddingright), getActivity().getResources()
							.getDimensionPixelSize(R.dimen.global_page_paddingbottom));

					txt.setTextColor(getActivity().getResources().getColor(R.color.dialog_content_text_color));

					txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.text_medium));
					txt.setText("你确定真的要删除收藏吗？");
					OKCallBackListener listener = new OKCallBackListener() {

						@Override
						public void execute() {
							ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

								@Override
								public void onSuccess(Integer result) {
									listdata.remove(position);
									listAdapter.notifyDataSetChanged();
									if (listdata.size() == 0) {
										isNetPro = false;
										loadDataAfter();
									}
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
								}
							};

							ProxyFactory
									.getInstance()
									.getPostbarProxy()
									.actionTopic(callback, getActivity(), ((Msgs.PostbarTopicDetail) listdata.get(position)).getId(),
											MsgsConstants.OP_CANCEL_TOPIC_FAVORITE, null);
						}

						@Override
						public void cannel() {
						}
					};
					DialogUtil.showDialog(getActivity(), "提示", txt, listener);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.iwgame.msgs.module.postbar.adapter.MainTopicListAdapter.
			 * ItemClickListener#onClickShare()
			 */
			@Override
			public void onClickShare() {
				shareTopic();
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.iwgame.msgs.module.postbar.adapter.MainTopicListAdapter.
			 * ItemClickListener#onClickShareTipic()
			 */
			@Override
			public void onClickShareTipic() {
				// TODO Auto-generated method stub
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.iwgame.msgs.module.postbar.adapter.MainTopicListAdapter.
			 * ItemClickListener#flushByTag(long)
			 */
			@Override
			public void flushByTag(int tagId, String tagName) {
				loadFlag.clear();
				loadDataClear(tagId, tagName);
				if ((GameTopicListActivity) getActivity() != null)
					((GameTopicListActivity) getActivity()).setMenuSelectTag(tagName, tagId);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.iwgame.msgs.module.postbar.adapter.MainTopicListAdapter.
			 * ItemClickListener#followGameStatus(boolean)
			 */
			@Override
			public void followGameStatus(boolean followed) {
				if ((GameTopicListActivity) getActivity() != null)
					((GameTopicListActivity) getActivity()).setFollowCallMenu(followed);
			}

		};
		listAdapter = new MainTopicListAdapter(pullToRefreshListView.getRefreshableView().getContext(), listdata, targetType, itemClickListener);
		pullToRefreshListView.setAdapter(listAdapter);
		listAdapter.setOrderType(order);
		loadData();
	}

	/**
	 * 初始化头部布局
	 * 
	 * @param v
	 */
	private void initHeader(View v) {
		// 显示top左边菜单
		LinearLayout left = (LinearLayout) v.findViewById(R.id.left);
		left.setVisibility(View.VISIBLE);
		// 设置返回功能
		Button backBtn = (Button) v.findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((GameTopicListActivity) getActivity()).pageFrom != null
							&& ((GameTopicListActivity) getActivity()).pageFrom.equals("GameRegiestRecommendActivity")) {
						jumpMainView();
					} else {
						getActivity().finish();
					}
				}
			});
		}
		// 设置TOP的中间布局容器
		LinearLayout topcenter = (LinearLayout) v.findViewById(R.id.center);
		allTxt = (TextView) topcenter.findViewById(R.id.allTxt);
		newTxt = (TextView) topcenter.findViewById(R.id.newTxt);
		hotTxt = (TextView) topcenter.findViewById(R.id.hotTxt);
		allTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (FastClickLimitUtil.isFastClick())
					return;
				changeTitleTabView(false, true, true, R.color.topiclist_order_txt_pre, R.color.topiclist_order_txt_nor,
						R.color.topiclist_order_txt_nor, R.drawable.topiclist_left_title_order_pre_shap,
						R.drawable.topiclist_middle_title_order_shap, R.drawable.topiclist_right_title_order_shap);
				// 全部
				order = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;
				listAdapter.setOrderType(order);
				loadFlag.clear();
				loadDataClear(tagid, tagName);
			}
		});
		newTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (FastClickLimitUtil.isFastClick())
					return;
				changeTitleTabView(true, false, true, R.color.topiclist_order_txt_nor, R.color.topiclist_order_txt_pre,
						R.color.topiclist_order_txt_nor, R.drawable.topiclist_left_title_order_shap,
						R.drawable.topiclist_middle_title_order_pre_shap, R.drawable.topiclist_right_title_order_shap);
				// 最新回复
				order = MsgsConstants.POSTBAR_ORDER_CREATE_TIME;
				listAdapter.setOrderType(order);
				loadFlag.clear();
				loadDataClear(tagid, tagName);
			}
		});
		hotTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (FastClickLimitUtil.isFastClick())
					return;
				changeTitleTabView(true, true, false, R.color.topiclist_order_txt_nor, R.color.topiclist_order_txt_nor,
						R.color.topiclist_order_txt_pre, R.drawable.topiclist_left_title_order_shap, R.drawable.topiclist_middle_title_order_shap,
						R.drawable.topiclist_right_title_order_pre_shap);
				// 最热回复
				order = MsgsConstants.POSTBAR_ORDER_REPLY_NUMS;
				listAdapter.setOrderType(order);
				loadFlag.clear();
				loadDataClear(tagid, tagName);
			}
		});
		if (order == MsgsConstants.POSTBAR_ORDER_REPLY_NUMS) {
			changeTitleTabView(true, true, false, R.color.topiclist_order_txt_nor, R.color.topiclist_order_txt_nor, R.color.topiclist_order_txt_pre,
					R.drawable.topiclist_left_title_order_shap, R.drawable.topiclist_middle_title_order_shap,
					R.drawable.topiclist_right_title_order_pre_shap);
		} else if (order == MsgsConstants.POSTBAR_ORDER_CREATE_TIME) {
			changeTitleTabView(true, false, true, R.color.topiclist_order_txt_nor, R.color.topiclist_order_txt_pre, R.color.topiclist_order_txt_nor,
					R.drawable.topiclist_left_title_order_shap, R.drawable.topiclist_middle_title_order_pre_shap,
					R.drawable.topiclist_right_title_order_shap);
		} else if (order == MsgsConstants.POSTBAR_ORDER_REPLY_TIME) {
			changeTitleTabView(false, true, true, R.color.topiclist_order_txt_pre, R.color.topiclist_order_txt_nor, R.color.topiclist_order_txt_nor,
					R.drawable.topiclist_left_title_order_pre_shap, R.drawable.topiclist_middle_title_order_shap,
					R.drawable.topiclist_right_title_order_shap);
		}

		// 添加top右边发帖的按钮
		v.findViewById(R.id.extendView_left).setVisibility(View.GONE);
		v.findViewById(R.id.extendView_right).setVisibility(View.GONE);

		// 设置top右边功能按钮
		LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		rightMenu = new MenuMoreImageView(this.getActivity());
		LinearLayout rightView = (LinearLayout) v.findViewById(R.id.rightView);
		right = (LinearLayout) v.findViewById(R.id.right);
		rightView.addView(rightMenu, layoutParams);
		right.setVisibility(View.VISIBLE);
		rightView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (getActivity() instanceof GameTopicListActivity)
					((GameTopicListActivity) getActivity()).showMenu();
			}
		});

	}

	/**
	 * 
	 * @param allClickable
	 * @param newClickable
	 * @param hostClickable
	 * @param allTextColor
	 * @param newTextColor
	 * @param hostTextColor
	 * @param allbgRes
	 * @param newbgRes
	 * @param hostbgRes
	 */
	private void changeTitleTabView(boolean allClickable, boolean newClickable, boolean hostClickable, int allTextColor, int newTextColor,
			int hostTextColor, int allbgRes, int newbgRes, int hostbgRes) {
		allTxt.setClickable(allClickable);
		newTxt.setClickable(newClickable);
		hotTxt.setClickable(hostClickable);
		allTxt.setTextColor(getResources().getColor(allTextColor));
		newTxt.setTextColor(getResources().getColor(newTextColor));
		hotTxt.setTextColor(getResources().getColor(hostTextColor));
		allTxt.setBackgroundResource(allbgRes);
		newTxt.setBackgroundResource(newbgRes);
		hotTxt.setBackgroundResource(hostbgRes);
	}

	/**
	 * 分享贴吧
	 */
	public void shareTopic() {
		if (FastClickLimitUtil.isFastClick())
			return;
		if (gameVo == null) {
			ToastUtil.showToast(getActivity(), "信息未加载成功,请稍后再试");
			return;
		}
		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);// 类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_GAME);// 类型为帖子
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_GAME_DETIAL);// 内部分享类型
		shareDate.setTargetId(gameVo.getGameid());// 贴吧id(目标ID)
		shareDate.setTargetName(gameVo.getGamename());// 贴吧名称（目标名称）
		// shareDate.setSite("游伴");//QQ空间分享时使用（暂固定写法）
		// shareDate.setSiteUrl("http://www.51uban.com");//QQ空间分享时使用（暂固定写法）
		// 设置当前贴吧筛选标签
		if (tagid != SystemConfig.POSTBAR_TOPIC_TAG_ALL) {// 如果标签不为0，则说明当前标签不为全部和精华
			shareDate.setTagId(tagid);
		} else {// 全部或精华
			if (filter == MsgsConstants.POSTBAR_TOPIC_FILTER_ESSENCE) {// 加精（精华）
				shareDate.setTagId(SystemConfig.POSTBAR_TOPIC_TAG_ESSENCE);
			} else {// 全部
				shareDate.setTagId(SystemConfig.POSTBAR_TOPIC_TAG_ALL);
			}
		}

		// 获得图片地址
		String tmpImageSrc = "";
		if (gameVo.getGamelogo() != null && !gameVo.getGamelogo().isEmpty()) {
			tmpImageSrc = gameVo.getGamelogo();
		}
		if (!tmpImageSrc.isEmpty()) {
			shareDate.setImageUrl(ResUtil.getSmallRelUrl(tmpImageSrc));
			shareDate.setImagePath(tmpImageSrc);
		}

		ShareCallbackListener listener = new ShareCallbackListener() {

			@Override
			public void doSuccess(String plamType) {
				// 分享贴吧信息
				ShareTaskUtil.makeShareTask(getActivity(), TAG, gameVo.getGameid(), MsgsConstants.OT_GAME, MsgsConstants.OP_RECORD_SHARE, plamType,
						null, this.shortUrl);
			}

			@Override
			public void doFail() {
			}
		};
		ShareManager.getInstance().share(getActivity(), inflater, gameVo, shareDate, listener);
	}

	/**
	 * 根据排序名称获取ID
	 * 
	 * @param orderName
	 */
	private void getOrderTypeByName(String orderName) {
		if (orderName.equals(getString(R.string.postbar_topic_right_menu_order_reply_time))) {
			order = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;
		} else if (orderName.equals(getString(R.string.postbar_topic_right_menu_order_create_time))) {
			order = MsgsConstants.POSTBAR_ORDER_CREATE_TIME;
		} else if (orderName.equals(getString(R.string.postbar_topic_right_menu_order_hottest))) {
			order = MsgsConstants.POSTBAR_ORDER_REPLY_NUMS;// 最热帖
		} else {
			order = MsgsConstants.POSTBAR_ORDER_REPLY_TIME;
		}
		listAdapter.setOrderType(order);
	}

	/**
	 * 跳转到主界面
	 */
	private void jumpMainView() {
		LogUtil.d(TAG, "--------->LoadDataAcitvity::jumpMainView:跳转到主界面");
		Intent intent = new Intent(getActivity(), MainFragmentActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, -2);
		intent.putExtras(bundle);
		startActivity(intent);
		getActivity().finish();
	}

	/**
	 * 绑定手机提示框
	 * 
	 * @param actionName
	 */
	private void createBundPhoneDialog() {
		Intent intent = new Intent(getActivity(), BundPhoneActivity.class);
		startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		int realposition = (int) parent.getAdapter().getItemId(position);
		Object object = listdata.get(realposition);
		if (realposition != -1 && realposition != 0 && object instanceof Msgs.PostbarTopicDetail) {
			Intent intent = null;
			Bundle bundle = null;
			intent = new Intent(this.getActivity(), TopicDetailActivity.class);
			bundle = new Bundle();
			bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, ((Msgs.PostbarTopicDetail) object).getId());
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, ((Msgs.PostbarTopicDetail) object).getGameid());
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			startActivity(intent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android
	 * .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		int realposition = (int) parent.getAdapter().getItemId(position);
		Object object = listdata.get(realposition);
		if (realposition > 0 && object instanceof Msgs.PostbarTopicDetail) {
			PostbarTopicDetail detail = (PostbarTopicDetail) object;
			UserVo userVo = SystemContext.getInstance().getExtUserVo();
			// 弹出管理窗口
			if (this.isMaster) {
				showActionDialog(detail, 0);
				return true;
			} else if (userVo != null && detail.getPosterUid() == userVo.getUserid()) {
				showActionDialog(detail, 1);
				return true;
			}
		}
		return false;
	}

	/**
	 * 显示操作dialog
	 * 
	 * @param detail
	 */
	private void showActionDialog(final Msgs.PostbarTopicDetail detail, final int mode) {
		final Dialog dialog = new Dialog(this.getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setCanceledOnTouchOutside(true);// 点击其他区域dialog消失
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(R.string.postbar_master_action_dialog_title);

		LinearLayout bottom = (LinearLayout) dialog.findViewById(R.id.bottom);
		bottom.setVisibility(View.GONE);

		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		content.removeAllViews();

		LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.postbar_topic_action_list, null);
		ListView listView = (ListView) view.findViewById(R.id.listView);

		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), getActionListdata(detail, mode), R.layout.postbar_topic_action_list_item,
				new String[] { "name" }, new int[] { R.id.postbar_topic_action_list_item_name });
		listView.setAdapter(adapter);

		content.addView(view);
		dialog.show();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialog.dismiss();
				if (mode == 0) {
					if (position == 2) {// 删除
						// 弹出对话框
						createInputActionReasonDialog(position, detail, "删除", "确定删除");
					} else {// 非删除
						actionTopic(position, detail, null);
					}
				} else if (mode == 1) {
					// 弹出对话框
					delTopicDialog(2, detail);
				}
			}
		});

	}

	/**
	 * 添加删除理由
	 * 
	 * @param actionPosition
	 * @param detail
	 * @param sTitle
	 * @param sCommitBtn
	 */
	private void createInputActionReasonDialog(final int actionPosition, final Msgs.PostbarTopicDetail detail, String sTitle, String sCommitBtn) {
		final Dialog dialog = new Dialog(getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_card);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("请输入" + sTitle + "理由");
		final TextView edit_word_num = (TextView) dialog.findViewById(R.id.edit_word_num);
		final EditText edit_group_card = (EditText) dialog.findViewById(R.id.edit_group_card);
		InputFilterUtil.lengthFilter(getActivity(), edit_group_card, 40, "删除理由上限为20个字哦！");
		edit_group_card.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				String source = edit_group_card.getText().toString();
				int sourceLen = StringUtil.getCharacterNum(source.toString());
				if (sourceLen > 0) {
					double length = Math.ceil(sourceLen / 2.0);
					edit_word_num.setText((int) length + "/20");
				} else {
					edit_word_num.setText("0/20");
				}
			}
		});
		Button act_login_cleanAccountBtn = (Button) dialog.findViewById(R.id.act_login_cleanAccountBtn);
		act_login_cleanAccountBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				edit_group_card.setText("");
			}
		});
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setText(sCommitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				actionTopic(actionPosition, detail, edit_group_card.getText().toString().trim());
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

	/**
	 * 
	 * @param actionPosition
	 * @param detail
	 */
	private void delTopicDialog(final int actionPosition, final Msgs.PostbarTopicDetail detail) {
		final Dialog dialog = new Dialog(getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("删除帖子");
		final TextView txt = new TextView(getActivity());
		txt.setTextColor(getResources().getColor(R.color.darkgray));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText("是否真的要删除该贴？");
		content.setPadding(DisplayUtil.dip2px(getActivity(), 10), 10, DisplayUtil.dip2px(getActivity(), 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				actionTopic(actionPosition, detail, null);
				dialog.dismiss();
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

	/**
	 * 吧主操作帖子的操作列表（// 精华， 置顶 ， 删除， 公告）
	 * 
	 * @param detail
	 * @param mode
	 *            0默认全部 1只有删除功能
	 * @return
	 */
	private List<Map<String, String>> getActionListdata(Msgs.PostbarTopicDetail detail, int mode) {

		List<Map<String, String>> actionList = new ArrayList<Map<String, String>>();
		Map<String, String> nameMap = new HashMap<String, String>();
		if (mode == 0) {
			// 精华
			if (detail.getIsEssence()) {
				nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_delessence));
			} else {
				nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_setessence));
			}
			actionList.add(nameMap);
			// 置顶
			nameMap = new HashMap<String, String>();
			if (detail.getIsTop()) {
				nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_deltop));
			} else {
				nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_settop));
			}
			actionList.add(nameMap);
			// 删除
			nameMap = new HashMap<String, String>();
			nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_deltopic));
			actionList.add(nameMap);
			// 锁帖
			nameMap = new HashMap<String, String>();
			if (detail.getIsLock()) {
				nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_unlocktopic));
			} else {
				nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_locktopic));
			}
			actionList.add(nameMap);
			// 公告
			nameMap = new HashMap<String, String>();
			if (detail.getIsNotice()) {
				nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_delnotice));
			} else {
				nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_setnotice));
			}
			actionList.add(nameMap);
		} else {
			// 删除
			nameMap = new HashMap<String, String>();
			nameMap.put("name", getResources().getString(R.string.postbar_master_action_dialog_deltopic));
			actionList.add(nameMap);
		}
		return actionList;

	}

	/**
	 * 吧主操作帖子
	 * 
	 * @param actionPosition
	 *            操作的
	 * @param detail
	 */
	private void actionTopic(final int actionPosition, final Msgs.PostbarTopicDetail detail, String actionReason) {

		LogUtil.d(TAG, "onItemLongClick:" + detail.toString());
		int op = 0;
		switch (actionPosition) {
		case 0:// 精华
			if (detail.getIsEssence()) {
				// 取消精华
				op = MsgsConstants.OP_CANCEL_TOPIC_ESSENCE;
			} else {

				op = MsgsConstants.OP_SET_TOPIC_ESSENCE;
			}
			break;
		case 1:// 置顶
			if (detail.getIsTop()) {
				// 取消置顶
				op = MsgsConstants.OP_CANCEL_TOPIC_TOP;
			} else {
				op = MsgsConstants.OP_SET_TOPIC_TOP;
			}
			break;
		case 2:// 删除
			op = MsgsConstants.OP_DEL_TOPIC;
			break;
		case 3:// 锁帖
			if (detail.getIsLock()) {
				// 解锁
				op = MsgsConstants.OP_UNLOCK_TOPIC;
			} else {
				op = MsgsConstants.OP_LOCK_TOPIC;
			}
			break;
		case 4:// 公告
			if (detail.getIsNotice()) {
				// 取消置顶
				op = MsgsConstants.OP_CANCEL_TOPIC_NOTICE;
			} else {
				op = MsgsConstants.OP_SET_TOPIC_NOTICE;
			}
			break;
		}

		if (op != 0) {
			ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					Msgs.PostbarTopicDetail tmp = null;
					switch (actionPosition) {
					case 0:// 精华
						tmp = detail.toBuilder().setIsEssence(!detail.getIsEssence()).build();
						Collections.replaceAll(listdata, detail, tmp);
						listAdapter.notifyDataSetChanged();
						break;
					case 1:// 置顶
						isshowdialog = true;
						reloadData();
						break;
					case 2:// 删除
						listdata.remove(detail);
						listAdapter.notifyDataSetChanged();
						break;
					case 3:// 锁帖
						tmp = detail.toBuilder().setIsLock(!detail.getIsLock()).build();
						Collections.replaceAll(listdata, detail, tmp);
						listAdapter.notifyDataSetChanged();
						break;
					case 4:// 公告
						tmp = detail.toBuilder().setIsNotice(!detail.getIsNotice()).build();
						Collections.replaceAll(listdata, detail, tmp);
						listAdapter.notifyDataSetChanged();
						break;
					}
					ToastUtil.showToast(getActivity(), "操作成功");
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
				}

			};
			ProxyFactory.getInstance().getPostbarProxy().actionTopic(callback, this.getActivity(), detail.getId(), op, actionReason);
		}
	}

	// 是否加载数据中
	private boolean isLoadDataing = false;

	/**
	 * 加载数据
	 */
	private void loadData() {
		if (isLoadDataing) {
			// 数据加载中
			pullToRefreshListView.onRefreshComplete();
			return;
		} else {
			isLoadDataing = true;
		}

		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this.getActivity());
		if (isfirstLoad || isshowdialog)
			dialog.show();
		ProxyCallBack<Map<String, Object>> callback = new ProxyCallBack<Map<String, Object>>() {

			@Override
			public void onSuccess(final Map<String, Object> result) {
				sendPostBtn.setEnabled(true);
				if (isfirstLoad || isshowdialog) {
					dialog.dismiss();
					isfirstLoad = false;
					isshowdialog = false;
				}

				String key = listdata.size() + 2 + "";
				loadFlag.put(key, STATUS_LOAD_OK);

				int tagid = result.containsKey("tagid") ? (Integer) result.get("tagid") : 0;
				int filter = result.containsKey("filter") ? (Integer) result.get("filter") : 0;

				if (MainTopicListFragment.this.tagid == tagid && MainTopicListFragment.this.filter == filter) {
					List<PostbarTopicDetail> tempList = result.containsKey("list") ? (List<PostbarTopicDetail>) result.get("list")
							: new ArrayList<PostbarTopicDetail>();
					if (getActivity() != null) {
						if (tempList != null && tempList.size() > 0) {
							for (int i = 0; i < tempList.size(); i++) {
								listdata.add(tempList.get(i));
							}
							listAdapter.notifyDataSetChanged();
						}
						// 本次没有加载到数据，表示数据已经加载完
						if (listdata.size() > 1 && tempList.size() == 0) {
							ToastUtil.showToast(getActivity(), getString(R.string.global_data_load_ok));
						}
						isNetPro = false;

						loadDataAfter();
					}
				}

				pullToRefreshListView.onRefreshComplete();
				isLoadDataing = false;
			}

			@Override
			public void onFailure(final Integer result, final String resultMsg) {
				sendPostBtn.setEnabled(true);
				if (getActivity() != null) {
					if (isfirstLoad || isshowdialog) {
						dialog.dismiss();
						isfirstLoad = false;
						isshowdialog = false;
					}

					if (resultMsg != null && resultMsg.equals(getCurrentTag() + "")) {
						ErrorCodeUtil.handleErrorCode(getActivity(), result, null);
						isNetPro = true;
						loadDataAfter();
					}

				}

				String key = listdata.size() + 2 + "";
				loadFlag.put(key, STATUS_LOAD_OK);
				pullToRefreshListView.onRefreshComplete();
				isLoadDataing = false;

			}

		};
		ProxyFactory
				.getInstance()
				.getPostbarProxy()
				.getTopicListMap(callback, getActivity(), targetId, null, order, tagid, filter, 0, listdata.size() > 0 ? listdata.size() - 1 : 0,
						SystemConfig.PAGE_SIZE, null);

	}

	/**
	 * 加载数据，成功后再清空原数据
	 */
	private void loadDataClear() {
		loadDataClear(-1, null);
	}

	/**
	 * 加载数据，成功后再清空原数据
	 */
	public void loadDataClear(ChildItem item) {
		if (item != null) {
			this.filter = item.essence;
			loadDataClear(item.tagId, item.itemName);
		}
	}

	/**
	 * 加载数据，成功后再清空原数据
	 * 
	 * @param tagId
	 */
	private void loadDataClear(int tagId, String tagName) {
		this.tagid = tagId;
		this.tagName = tagName;
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this.getActivity());
		if (isfirstLoad || isshowdialog)
			dialog.show();
		ProxyCallBack<Map<String, Object>> callback = new ProxyCallBack<Map<String, Object>>() {

			@Override
			public void onSuccess(final Map<String, Object> result) {
				sendPostBtn.setEnabled(true);
				if (isfirstLoad || isshowdialog) {
					dialog.dismiss();
					isfirstLoad = false;
					isshowdialog = false;
				}
				if (result == null) {
					return;
				}

				int tagid = result.containsKey("tagid") ? (Integer) result.get("tagid") : 0;
				int filter = result.containsKey("filter") ? (Integer) result.get("filter") : 0;

				listdata.clear();
				listAdapter.clearLikeUserMap();
				listdata.add(gameData);
				List<PostbarTopicDetail> tempList = result.containsKey("list") ? (List<PostbarTopicDetail>) result.get("list")
						: new ArrayList<PostbarTopicDetail>();
				if (getActivity() != null) {
					if (tempList != null && tempList.size() > 0) {
						for (int i = 0; i < tempList.size(); i++) {
							listdata.add(tempList.get(i));
						}
						listAdapter.notifyDataSetChanged();
					}

					// 本次没有加载到数据，表示数据已经加载完
					if (listdata.size() > 1 && tempList.size() == 0) {
						ToastUtil.showToast(getActivity(), getString(R.string.global_data_load_ok));
					}

					isNetPro = false;
					loadDataAfter();
				}

				pullToRefreshListView.onRefreshComplete();
			}

			@Override
			public void onFailure(final Integer result, final String resultMsg) {
				sendPostBtn.setEnabled(true);
				if (getActivity() != null) {
					if (isfirstLoad || isshowdialog) {
						dialog.dismiss();
						isfirstLoad = false;
						isshowdialog = false;
					}
					if (resultMsg != null && resultMsg.equals(getCurrentTag() + "")) {
						ErrorCodeUtil.handleErrorCode(getActivity(), result, null);
						isNetPro = true;
						listdata.clear();
						listAdapter.clearLikeUserMap();
						listdata.add(gameData);
						loadDataAfter();
					}
				}
				pullToRefreshListView.onRefreshComplete();

			}

		};
		ProxyFactory
				.getInstance()
				.getPostbarProxy()
				.getTopicListMap(callback, getActivity(), targetId, null, order, tagId == -1 ? tagid : tagId, filter, 0, 0, SystemConfig.PAGE_SIZE,
						tagName);

	}

	/**
	 * 加载数据之后的操作，或没有数据之后调用
	 */
	private void loadDataAfter() {
		if (listdata.size() == 1) {
			final GameNoData gameNoData = new GameNoData();
			gameNoData.setTagid(tagid);
			gameNoData.setFilter(filter);
			// 设置无数据时的界面
			if (isNetPro) {// 如果是网络问题
				gameNoData.setNodateType(0);
			} else {
				gameNoData.setNodateType(1);
			}

			if (listAdapter.getNodataItemHeight() == 0) {
				if (listViewHeight - firstItemHeight > 0) {
					listAdapter.setNodataItemHeight(listViewHeight - firstItemHeight);
				} else {
					pullToRefreshListView.post(new Runnable() {

						@Override
						public void run() {
							viewInited();
							listAdapter.setNodataItemHeight(listViewHeight - firstItemHeight);
							listdata.add(gameNoData);
							listAdapter.notifyDataSetChanged();
						}
					});
					return;
				}
			}
			listdata.add(gameNoData);
			listAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 外部请求重新刷新数据
	 */
	private void reloadData() {
		loadFlag.clear();
		loadDataClear();
	}

	/**
	 * 操作重新加载数据
	 * 
	 * @param order
	 * @param tag
	 */
	public void actionReloadData(int order, int tag, int filter) {
		// 清空数据，重新加载
		this.order = order;
		listAdapter.setOrderType(order);
		this.tagid = tag;
		this.filter = filter;
		listdata.clear();
		listAdapter.clearLikeUserMap();
		loadFlag.clear();
		loadData();
	}

	/**
	 * 操作重新加载数据
	 * 
	 * @param order
	 * @param tag
	 */
	public void actionReloadData(int tag, int filter) {
		// 清空数据，重新加载
		this.tagid = tag;
		this.filter = filter;
		isHasScroll = false;
		loadFlag.clear();
		loadDataClear();
	}

	/**
	 * 操作重新加载数据
	 * 
	 * @param order
	 * @param tag
	 */
	public void actionReloadData() {
		getGameInfo(targetId);
		isHasScroll = false;
		loadFlag.clear();
		loadDataClear();
	}

	/**
	 * 外部设置item的编辑状态，true 处于编辑状态中，false，处于一般状态中(主要用于收藏显示删除按钮）
	 * 
	 * @param status
	 */
	public void setItemEditStatus(boolean status) {
		listAdapter.setEditStatus(status);
		listAdapter.notifyDataSetChanged();
	}

	public void setNoDataListener(NoDataListener listener) {
		noDataListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.d(TAG, "resultCode =" + resultCode + ";requestCode=" + requestCode);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SystemConfig.TOPICLIST_OPENPUBLISHTOPIC_REQUESTCODE) {
				// 全部
				order = MsgsConstants.POSTBAR_ORDER_CREATE_TIME;
				filter = 0;
				tagid = SystemConfig.POSTBAR_TOPIC_TAG_ALL;
				listAdapter.setOrderType(order);
				allTxt.setClickable(false);
				newTxt.setClickable(true);
				hotTxt.setClickable(true);
				allTxt.setTextColor(getResources().getColor(R.color.topiclist_order_txt_pre));
				newTxt.setTextColor(getResources().getColor(R.color.topiclist_order_txt_nor));
				hotTxt.setTextColor(getResources().getColor(R.color.topiclist_order_txt_nor));
				allTxt.setBackgroundResource(R.drawable.topiclist_left_title_order_pre_shap);
				newTxt.setBackgroundResource(R.drawable.topiclist_middle_title_order_shap);
				hotTxt.setBackgroundResource(R.drawable.topiclist_right_title_order_shap);
				((GameTopicListActivity) getActivity()).resetSelectMenu();
				reloadData();
			}
		}
	}

	/**
	 * 点击发布帖子的按钮
	 */
	private void publishTopicButtonClick() {
		sendPostBtn.setEnabled(false);
		// 判断是否关注过该贴吧
		ProxyCallBack<RelationGameVo> callback = new ProxyCallBack<RelationGameVo>() {
			@Override
			public void onSuccess(RelationGameVo result) {
				if (result != null && result.getRelation() == 1) {
					// 关注过该贴吧
					publishTopic();
				} else {
					long gameid = AdaptiveAppContext.getInstance().getAppConfig().getGameId();
					if (gameid != 0 && gameid == targetId) {// 自动关注攻略贴吧
						ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

							@Override
							public void onSuccess(Integer result) {
								publishTopic();
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								sendPostBtn.setEnabled(true);
							}
						}, getActivity(), gameid, MsgsConstants.OT_GAME, MsgsConstants.OP_FOLLOW, null, null, SystemContext.APPTYPE);
					} else {
						sendPostBtn.setEnabled(true);
						// 未关注该贴吧
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (getActivity() != null) {
									ToastUtil.showToast(getActivity(), getString(R.string.postbar_publish_topic_info));
								}
							}
						});
					}
				}

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				sendPostBtn.setEnabled(true);
				ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
			}
		};
		ProxyFactory.getInstance().getGameProxy().getRelGameInfoForLocal(callback, getActivity(), targetId);

	}

	private void publishTopic() {
		// 获得信息，判断是否可以发送帖子
		ProxyCallBack<Map<Integer, Integer>> callback = new ProxyCallBack<Map<Integer, Integer>>() {

			@Override
			public void onSuccess(Map<Integer, Integer> result) {
				sendPostBtn.setEnabled(true);
				if (getActivity() != null) {
					// 用查询到的限制值和总限制值比较
					int tmp = result.get(MsgsConstants.LimitedOP_POST_TOPIC);
					if (tmp >= SystemContext.getInstance().getPTM()) {
						// 超过每日发帖数
						ErrorCodeUtil.handleErrorCode(getActivity(), ErrorCode.EC_CLIENT_PUBLISHTOPIC_OVER_COUNT, null, SystemContext.getInstance()
								.getPTM());
					} else {
						if (System.currentTimeMillis() - SystemContext.getInstance().getLastTopicPublishTime() < SystemContext.getInstance().getPIT() * 1000) {
							// 隔的时间太短
							ErrorCodeUtil.handleErrorCode(getActivity(), Msgs.ErrorCode.EC_MSGS_POSTBAR_POST_OUT_OF_LIMIT_VALUE, null);
						} else {
							SystemContext.getInstance().setLastTopicPublishTime(0);
							// 发布
							Intent intent = null;
							Bundle bundle = null;
							intent = new Intent(getActivity(), PublishTopicActivity.class);
							bundle = new Bundle();
							bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, targetId);
							bundle.putInt(SystemConfig.BUNDLE_NAME_TOPIC_TAGID, tagid);
							intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
							startActivityForResult(intent, SystemConfig.TOPICLIST_OPENPUBLISHTOPIC_REQUESTCODE);
						}
					}
				}

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				sendPostBtn.setEnabled(true);
				if (getActivity() != null) {
					ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
				}
			}

		};
		ProxyFactory.getInstance().getPostbarProxy().getLimitedOPCount(callback, getActivity(), MsgsConstants.LimitedOP_POST_TOPIC);
	}

	/**
	 * 获得贴吧信息
	 * 
	 * @param gid
	 */
	private void getGameInfo(final long gid) {
		ProxyCallBack<GameVo> callback = new ProxyCallBack<GameVo>() {
			@Override
			public void onSuccess(GameVo result) {
				if (result != null) {
					gameVo = result;
					gameData.setGetGameInfo(true);
					gameData.setGid(targetId);// 设置贴吧ID
					gameData.setGamename(result.getGamename());// 设置贴吧名称
					if (result.getGamelogo() != null && !result.getGamelogo().isEmpty()) {// 贴吧LOGO
						gameData.setGamelogo(ResUtil.getSmallRelUrl(result.getGamelogo()));
					}
				} else {
					LogUtil.e(TAG, "获取贴吧信息失败");
				}
				getGameExtData(gid);

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				getGameExtData(gid);
			}

		};
		ProxyFactory.getInstance().getGameProxy().getGameInfo(callback, getActivity(), gid);
	}

	/**
	 * 获得贴吧的扩展信息
	 * 
	 * @param gid
	 */
	private void getGameExtData(final long gid) {
		ProxyCallBack<GameExtDataVo> callback = new ProxyCallBack<GameExtDataVo>() {
			@Override
			public void onSuccess(GameExtDataVo result) {
				if (result != null) {
					isGetExtData = true;
					if (result.isBarMaster()) {
						isMaster = true;
					}
					gameData.setGetExtraData(true);
					gameData.setPraise(result.getPraise());// 赞的次数
					gameData.setCriticize(result.getCriticize());// 踩得次数
					gameData.setIspraise(result.getIspraise());// 是否赞过
					gameData.setIscriticize(result.getIscriticize());// 是否踩过
					gameData.setGroupCount(result.getGroupCount());// 公会数
					gameData.setFollowCount(result.getFollowCount());// 游伴数
					if (listAdapter != null) {
						listAdapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "获取扩展数据失败");
				isGetExtData = false;
				if (listAdapter != null) {
					listAdapter.notifyDataSetChanged();
				}
			}
		};
		ProxyFactory.getInstance().getGameProxy().getGameExtData(callback, getActivity(), gid, 1);
	}

	/**
	 * 设置帖吧的maxIndex 为read的最大index
	 * 
	 * @param context
	 * @param gid
	 */
	private void setReadMaxIndex(Context context, final long gid) {
		ProxyFactory.getInstance().getGameProxy().getGamePostbarMaxIndex(new ProxyCallBack<List<ExtGameVo>>() {

			@Override
			public void onSuccess(List<ExtGameVo> result) {
				if (result != null && result.size() != 0) {
					for (int j = 0; j < result.size(); j++) {
						ExtGameVo gvo = result.get(j);
						long tmPGid = gvo.getGameid();
						if (tmPGid == gid) {
							SystemContext.getInstance().setUserSharedPreferencesPostbarReadMaxIndex(gid, gvo.getPostbarMaxIndex());
						}
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
			}

		}, null, gid + "");

	}

	public void setTags(List<TopicTagVo> tags) {
		this.tags = tags;
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
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(DateUtil.getFormatRefreshDate());

		loadFlag.clear();
		loadDataClear(tagid, tagName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2
	 * #onPullUpToRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		// Update the LastUpdatedLabel
		// refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		loadData();

	}

	/**
	 * 获取当前标签
	 * 
	 * @return
	 */
	private int getCurrentTag() {
		int tag = 0;
		if (tagid != SystemConfig.POSTBAR_TOPIC_TAG_ALL) {// 如果标签不为0，则说明当前标签不为全部和精华
			tag = tagid;
		} else {// 全部或精华
			if (filter == MsgsConstants.POSTBAR_TOPIC_FILTER_ESSENCE) {// 加精（精华）
				tag = -1;
			} else {// 全部
				tag = 0;
			}
		}
		return tag;
	}

	public void setFollow(boolean followed) {
		if (gameData != null) {
			if (followed)
				gameData.setRelation(1);
			else
				gameData.setRelation(0);

			listAdapter.notifyDataSetChanged();
		}
	}

	public boolean getFollow() {
		if (gameData != null && gameData.getRelation() == 1) {
			return true;
		} else {
			return false;
		}
	}
}
