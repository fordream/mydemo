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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.adaptiveconfig.AdaptiveAppContext;
import com.iwgame.msgs.adaptiveconfig.AppConfig;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageLoader;
import com.iwgame.msgs.common.NoDataListener;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.game.object.GameExtDataVo;
import com.iwgame.msgs.module.game.ui.GameDetailAlikeAcitivity;
import com.iwgame.msgs.module.game.ui.GameDetailGroupActivity;
import com.iwgame.msgs.module.game.ui.GameTopicGLFragmentActivity;
import com.iwgame.msgs.module.game.ui.GameTopicNewsFragmentActivity;
import com.iwgame.msgs.module.guide.GuideActivity;
import com.iwgame.msgs.module.postbar.adapter.TopicListAdapter;
import com.iwgame.msgs.module.postbar.adapter.TopicListAdapter.ItemClickListener;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.utils.AppUtil;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.DialogUtil.OKCallBackListener;
import com.iwgame.msgs.utils.EditTextUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.GameUtil;
import com.iwgame.msgs.utils.GuideUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.TopicTagVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.msgs.widget.gridview.SelectGridView;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.msgs.widget.roundedimageview.RoundedImageView;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.DisplayUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.youban.msgs.R;

/**
 * @ClassName: TopicListFragment
 * @Description: 贴吧主题列表
 * @author chuanglong
 * @date 2013-12-20 下午3:20:31
 * @Version 1.0
 * 
 */
public class TopicListFragment extends BaseFragment implements OnItemClickListener, OnItemLongClickListener, OnRefreshListener2<ListView> {
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
	/**
	 * 选项卡组件对象
	 */

	RadioButton postbar_seq_newcomment;

	List<Msgs.PostbarTopicDetail> listdata = new ArrayList<Msgs.PostbarTopicDetail>();
	TopicListAdapter listAdapter;

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
	int tagid = 0;

	private String tagName;
	/**
	 * 当前显示内容的过滤条件
	 */
	int filter = 0;
	long uid;
	// 没有数据时的通知
	NoDataListener noDataListener;

	public ImageView rightMenu;
	private LinearLayout right;

	private LinearLayout extendView;
	private ImageView publishTopicMenu;

	/*
	 * 头部贴吧的相关信息
	 */
	HeaderGameDetailViewHolder headerGameDetailViewHolder = null;
	// 标题
	TextView titleTxt;

	GameVo gameVo;
	/**
	 * 贴吧扩展信息
	 */
	GameExtDataVo gameExtDataVo;
	/**
	 * 是否关注
	 */
	boolean isfollow;

	List<TopicTagVo> tags = null;
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
			if (data != null)
				tags = (List<TopicTagVo>) data;
			tagid = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGID, 0);
			tagName = tmpbundle.getString(SystemConfig.BUNDLE_NAME_GETTOPICLIST_TAGNAME);
		}
		isfirstLoad = true;

		// 添加动态主界面
		View v = inflater.inflate(R.layout.common_content, container, false);
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
		if ((targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME)) {
			// 获取贴吧扩展数据
			getGameInfo(targetId);
			getGameRel(targetId);
			// 更新帖子最大数为已经读到的最大数
			// setReadMaxIndex(getActivity(), targetId);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	/**
	 * 初始化
	 * 
	 * @param v
	 */
	private void init(View v) {
		// 初始化头部分
		initHeader(v);
		View topView = v.findViewById(R.id.top);
		// 设置中间内容的布局文件
		LinearLayout contentView = (LinearLayout) v.findViewById(R.id.contentView);
		contentView.removeAllViews();
		final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.postbar_topic_list, null);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);
		nullContent = (LinearLayout) view.findViewById(R.id.nullContent);
		nullContentBgIcon = (ImageView) nullContent.findViewById(R.id.bgIcon);
		nullContentBgIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isNetPro) {
					isshowdialog = true;
					loadData();
					if (getActivity() instanceof GameTopicListActivity) {
						if (!isGetExtData) {
							getGameInfo(targetId);
						}
						getGameRel(targetId);
						// 更新帖子最大数为已经读到的最大数
						// setReadMaxIndex(getActivity(), targetId);
						if (tags == null) {
							((GameTopicListActivity) getActivity()).getTopicTags();
						}
					}

				}
			}
		});

		postbar_topic_search = (LinearLayout) view.findViewById(R.id.postbar_topic_search);
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

		if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME) {
			topView.setVisibility(View.VISIBLE);
			postbar_topic_search.setVisibility(View.GONE);

			ViewGroup headerView = (ViewGroup) view.findViewById(R.id.postbar_topic_list_header_gamedetail);
			headerView.setVisibility(View.VISIBLE);
			headerGameDetailViewHolder = getHeaderGameDetailViewHolder(headerView);

			pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(this);

		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAMESEARCHTOPIC) {
			topView.setVisibility(View.GONE);
			postbar_topic_search.setVisibility(View.VISIBLE);

			pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(this);

			final EditText searchTxt = (EditText) postbar_topic_search.findViewById(R.id.searchTxt);
			TextView searchBtn = (TextView) postbar_topic_search.findViewById(R.id.searchBtn);
			searchBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (searchTxt.getText().toString().trim().equals("")) {
						ToastUtil.showToast(getActivity(), "查询内容不可以为空");
					} else {
						title = searchTxt.getText().toString().trim();
						searchTxt.clearFocus();
						Utils.hideSoftInput(getActivity(), searchTxt);
						// 清空数据
						listdata.clear();
						listAdapter.notifyDataSetChanged();
						// 重新搜索
						loadData();
					}

				}
			});

			// 设置清除按钮
			Button cleanBtn = (Button) view.findViewById(R.id.cleanBtn);
			cleanBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					searchTxt.setText("");
				}
			});
			// 设置文本输入框文本变化监听
			EditTextUtil.ChangeCleanTextButtonVisible(searchTxt, cleanBtn);

		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_USER) {
			topView.setVisibility(View.GONE);
			postbar_topic_search.setVisibility(View.GONE);

		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_FAVORITE) {
			topView.setVisibility(View.GONE);
			postbar_topic_search.setVisibility(View.GONE);
		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME_TAG) {
			topView.setVisibility(View.GONE);
			postbar_topic_search.setVisibility(View.GONE);
		} else {
			topView.setVisibility(View.GONE);
			postbar_topic_search.setVisibility(View.GONE);
		}

		listdata = new ArrayList<PostbarTopicDetail>();
		ItemClickListener itemClickListener = new ItemClickListener() {
			@Override
			public void onClickAction(final int position, int action) {
				if (action == TopicListAdapter.ACTION_DEL) {
					final TextView txt = new TextView(getActivity());
					txt.setPadding(0, getActivity().getResources().getDimensionPixelSize(R.dimen.global_page_paddingtop), getActivity()
							.getResources().getDimensionPixelSize(R.dimen.global_page_paddingright), getActivity().getResources()
							.getDimensionPixelSize(R.dimen.global_page_paddingbottom));

					txt.setTextColor(getActivity().getResources().getColor(R.color.dialog_content_text_color));

					txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.text_medium));
					txt.setText("确定删除该条收藏吗？");

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
									.actionTopic(callback, getActivity(), listdata.get(position).getId(), MsgsConstants.OP_CANCEL_TOPIC_FAVORITE,
											null);

						}

						@Override
						public void cannel() {
						}

					};
					DialogUtil.showDialog(getActivity(), "提示", txt, listener);
				}

			}
		};

		listAdapter = new TopicListAdapter(pullToRefreshListView.getRefreshableView().getContext(), listdata, targetType, itemClickListener);
		pullToRefreshListView.setAdapter(listAdapter);
		listAdapter.setOrderType(MsgsConstants.POSTBAR_ORDER_REPLY_TIME);
		// 不是搜索页面是加载初始化数据
		if (targetType != SystemConfig.GETTOPICLIST_TARGETTYPE_GAMESEARCHTOPIC) {
			loadData();
		}
	}

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
		titleTxt = (TextView) topcenter.findViewById(R.id.titleTxt);
		// 添加top右边发帖的按钮
		v.findViewById(R.id.extendView_left).setVisibility(View.INVISIBLE);
		extendView = (LinearLayout) v.findViewById(R.id.extendView_right);
		extendView.setVisibility(View.VISIBLE);
		publishTopicMenu = new ImageView(this.getActivity());
		publishTopicMenu.setBackgroundResource(R.drawable.news_addnews);

		extendView.addView(publishTopicMenu);
		extendView.setVisibility(View.VISIBLE);
		extendView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isClickable_publish)
					return;
				isClickable_publish = false;
				// 通知需要同步数据
				android.os.Message msg = android.os.Message.obtain();
				msg.what = WHAT_CHANGECLICKABLE_PUBLISH_TRUE;

				handler.sendMessageDelayed(msg, 500);
				if (getActivity() instanceof GameTopicListActivity)
					((GameTopicListActivity) getActivity()).hideMenu();
				publishTopicButtonClick();
			}
		});

		// 设置top右边功能按钮
		rightMenu = new ImageView(this.getActivity());
		rightMenu.setBackgroundResource(R.drawable.common_menu_more_nor);
		LinearLayout rightView = (LinearLayout) v.findViewById(R.id.rightView);
		right = (LinearLayout) v.findViewById(R.id.right);
		rightView.addView(rightMenu);
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

	private List<Map<String, Object>> getSelectGridViewData() {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, getString(R.string.postbar_action_publish));
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, getString(R.string.postbar_seq_newcomment));
		items.add(map);
		map = new HashMap<String, Object>();
		map.put(SelectGridView.ITEM_NAME, getString(R.string.postbar_seq_createtime));
		items.add(map);
		return items;
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
		long gid = listdata.get(realposition).getGameid();
		// 根据当前的应用配置判断是否要启动游伴
		AppConfig appconfig = AdaptiveAppContext.getInstance().getAppConfig();
		if (appconfig != null && appconfig.isRecbarmsg() && gid != appconfig.getGameId()) {
			AppUtil.openGame(getActivity(), listdata.get(realposition).getId(), TopicDetailActivity.class.getName(), getActivity().getResources()
					.getString(R.string.postbar_show_topic_tip_for_youban_uninstall));
		} else {
			Intent intent = null;
			Bundle bundle = null;
			intent = new Intent(this.getActivity(), TopicDetailActivity.class);
			bundle = new Bundle();
			bundle.putLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID, listdata.get(realposition).getId());
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, listdata.get(realposition).getGameid());
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
		PostbarTopicDetail detail = listdata.get(realposition);
		UserVo userVo = SystemContext.getInstance().getExtUserVo();
		// 弹出管理窗口
		if (this.isMaster) {
			showActionDialog(detail, 0);
			return true;
		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_USER && userVo != null && detail.getPosterUid() == userVo.getUserid()) {
			showActionDialog(detail, 1);
			return true;
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
	 * 创建删除理由弹出框
	 */
	private void createInputActionReasonDialog(final int actionPosition, final Msgs.PostbarTopicDetail detail, String sTitle, String sCommitBtn) {
		final Dialog dialog = new Dialog(getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_card);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText("请输入" + sTitle + "理由");
		final TextView edit_word_num = (TextView) dialog.findViewById(R.id.edit_word_num);
		final EditText edit_group_card = (EditText) dialog.findViewById(R.id.edit_group_card);
		InputFilterUtil.lengthFilter(getActivity(), edit_group_card, 40, "删除理由不能超过20个汉字哦！");
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

	//
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
		ProxyCallBack<List<PostbarTopicDetail>> callback = new ProxyCallBack<List<PostbarTopicDetail>>() {

			@Override
			public void onSuccess(final List<PostbarTopicDetail> result) {
				if (isfirstLoad || isshowdialog) {
					dialog.dismiss();
					isfirstLoad = false;
					isshowdialog = false;
				}

				if (getActivity() != null) {
					if (result != null && result.size() > 0) {
						for (int i = 0; i < result.size(); i++) {

							listdata.add(result.get(i));

						}
						listAdapter.notifyDataSetChanged();

					}
					// 本次没有加载到数据，表示数据已经加载完
					if (listdata.size() > 0 && result.size() == 0) {
						ToastUtil.showToast(getActivity(), getString(R.string.global_data_load_ok));
					}

					isNetPro = false;
					loadDataAfter();

					// 弹出引导页
					if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME) {
						GuideUtil.startGuide(getActivity(), GuideActivity.GUIDE_MODE_POST);
					}
				}
				pullToRefreshListView.onRefreshComplete();
				isLoadDataing = false;
			}

			@Override
			public void onFailure(final Integer result, final String resultMsg) {
				if (getActivity() != null) {
					if (isfirstLoad || isshowdialog) {
						dialog.dismiss();
						isfirstLoad = false;
						isshowdialog = false;
					}
					ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
					isNetPro = true;
					loadDataAfter();
				}
				pullToRefreshListView.onRefreshComplete();
				isLoadDataing = false;

			}

		};
		if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME || targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME_TAG) {
			ProxyFactory.getInstance().getPostbarProxy()
					.getTopicList(callback, getActivity(), targetId, null, order, tagid, filter, 0, listdata.size(), SystemConfig.PAGE_SIZE, null);
		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_USER) {
			ProxyFactory.getInstance().getPostbarProxy()
					.getTopicList(callback, getActivity(), 0, title, order, 0, 0, targetId, listdata.size(), SystemConfig.PAGE_SIZE, null);

		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAMESEARCHTOPIC) {
			ProxyFactory
					.getInstance()
					.getPostbarProxy()
					.getTopicList(callback, getActivity(), targetId, title, MsgsConstants.POSTBAR_ORDER_CREATE_TIME, 0, 0, 0, listdata.size(),
							SystemConfig.PAGE_SIZE, null);

		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_FAVORITE) {
			ProxyFactory
					.getInstance()
					.getPostbarProxy()
					.getFavoriteTopicList(callback, getActivity(), SystemContext.getInstance().getExtUserVo().getUserid(), listdata.size(),
							SystemConfig.PAGE_SIZE);
		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_NEWS || targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_RAIDERS) {
			ProxyFactory
					.getInstance()
					.getPostbarProxy()
					.getTopicList(callback, getActivity(), targetId, null, order, tagid == -1 ? tagid : tagid, filter, 0, listdata.size(),
							SystemConfig.PAGE_SIZE, tagName);
		}

	}

	//
	private void loadDataClear() {
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
		ProxyCallBack<List<PostbarTopicDetail>> callback = new ProxyCallBack<List<PostbarTopicDetail>>() {

			@Override
			public void onSuccess(final List<PostbarTopicDetail> result) {
				listdata.clear();
				if (isfirstLoad || isshowdialog) {
					dialog.dismiss();
					isfirstLoad = false;
					isshowdialog = false;
				}

				if (getActivity() != null) {
					if (result != null && result.size() > 0) {
						for (int i = 0; i < result.size(); i++) {

							listdata.add(result.get(i));

						}
						listAdapter.notifyDataSetChanged();

					}
					// 本次没有加载到数据，表示数据已经加载完
					if (listdata.size() > 0 && result.size() == 0) {
						ToastUtil.showToast(getActivity(), getString(R.string.global_data_load_ok));
					}

					isNetPro = false;
					loadDataAfter();

					// 弹出引导页
					if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME) {
						GuideUtil.startGuide(getActivity(), GuideActivity.GUIDE_MODE_POST);
					}
				}
				pullToRefreshListView.onRefreshComplete();
				isLoadDataing = false;
			}

			@Override
			public void onFailure(final Integer result, final String resultMsg) {
				if (getActivity() != null) {
					if (isfirstLoad || isshowdialog) {
						dialog.dismiss();
						isfirstLoad = false;
						isshowdialog = false;
					}
					ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
					isNetPro = true;
					listdata.clear();
					loadDataAfter();
				}
				pullToRefreshListView.onRefreshComplete();
				isLoadDataing = false;

			}

		};
		if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME || targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME_TAG) {
			ProxyFactory.getInstance().getPostbarProxy()
					.getTopicList(callback, getActivity(), targetId, null, order, tagid, filter, 0, 0, SystemConfig.PAGE_SIZE, null);
		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_USER) {
			ProxyFactory.getInstance().getPostbarProxy()
					.getTopicList(callback, getActivity(), 0, title, order, 0, 0, targetId, 0, SystemConfig.PAGE_SIZE, null);

		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAMESEARCHTOPIC) {
			ProxyFactory.getInstance().getPostbarProxy()
					.getTopicList(callback, getActivity(), targetId, title, order, 0, 0, 0, 0, SystemConfig.PAGE_SIZE, null);

		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_FAVORITE) {
			ProxyFactory.getInstance().getPostbarProxy()
					.getFavoriteTopicList(callback, getActivity(), SystemContext.getInstance().getExtUserVo().getUserid(), 0, SystemConfig.PAGE_SIZE);
		} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_NEWS || targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_RAIDERS) {
			ProxyFactory
					.getInstance()
					.getPostbarProxy()
					.getTopicList(callback, getActivity(), targetId, null, order, tagid == -1 ? tagid : tagid, filter, 0, 0, SystemConfig.PAGE_SIZE,
							tagName);
		}

	}

	/**
	 * 加载数据之后的操作，或没有数据之后调用
	 */
	private void loadDataAfter() {

		if (listdata.size() == 0) {

			if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME) {
				// 设置无数据时的界面
				nullContent.setVisibility(View.VISIBLE);
				if (isNetPro) {// 如果是网络问题
					nullContentBgIcon.setBackgroundResource(R.drawable.topiclist_nonet_refresh_selector);
				} else {
					nullContentBgIcon.setBackgroundResource(R.drawable.common_no_postbar);
				}

			} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_USER) {// 用户的帖子列表
				// 设置无数据时的界面
				nullContent.setVisibility(View.VISIBLE);

				if (isNetPro) {// 如果是网络问题
					nullContentBgIcon.setBackgroundResource(R.drawable.topiclist_nonet_refresh_selector);
				} else {
					nullContentBgIcon.setBackgroundResource(R.drawable.common_no_post);
				}

			} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAMESEARCHTOPIC) {// 搜索贴吧的帖子列表
				// 设置无数据时的界面
				nullContent.setVisibility(View.VISIBLE);
				if (isNetPro) {// 如果是网络问题
					nullContentBgIcon.setBackgroundResource(R.drawable.topiclist_nonet_refresh_selector);
				} else {
					nullContentBgIcon.setBackgroundResource(R.drawable.common_no_seach_result);
				}

			} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_FAVORITE) {// 我的收藏
				if (noDataListener != null)
					noDataListener.noDataNotify();
				// 设置无数据时的界面
				nullContent.setVisibility(View.VISIBLE);
				if (isNetPro) {// 如果是网络问题
					nullContentBgIcon.setBackgroundResource(R.drawable.topiclist_nonet_refresh_selector);
				} else {
					nullContentBgIcon.setBackgroundResource(R.drawable.common_no_collect);
				}
			} else if (targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME_TAG) {// 功率和新闻
				// 设置无数据时的界面
				nullContent.setVisibility(View.VISIBLE);
				if (isNetPro) {// 如果是网络问题
					nullContentBgIcon.setBackgroundResource(R.drawable.topiclist_nonet_refresh_selector);
				} else {
					nullContentBgIcon.setBackgroundResource(R.drawable.common_nothing_all);
				}
			}

		} else {
			nullContent.setVisibility(View.GONE);
		}

	}

	/**
	 * 外部请求重新刷新数据
	 */
	public void reloadData() {
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
		this.tagid = tag;
		this.filter = filter;
		listdata.clear();
		loadData();
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
				reloadData();
			}
		}
	}

	/**
	 * 点击发布帖子的按钮
	 */
	private void publishTopicButtonClick() {
		if (tags == null || tags.size() <= 0) {
			ToastUtil.showToast(getActivity(), "标签没有准备好,请稍后再试");
			return;
		}
		extendView.setClickable(false);
		// 判断是否关注过该贴吧
		ProxyCallBack<RelationGameVo> callback = new ProxyCallBack<RelationGameVo>() {
			@Override
			public void onSuccess(RelationGameVo result) {

				if (result != null && result.getRelation() == 1) {
					// 关注过该贴吧
					// 获得信息，判断是否可以发送帖子
					ProxyCallBack<Map<Integer, Integer>> callback = new ProxyCallBack<Map<Integer, Integer>>() {

						@Override
						public void onSuccess(Map<Integer, Integer> result) {
							if (getActivity() != null) {

								// 用查询到的限制值和总限制值比较
								int tmp = result.get(MsgsConstants.LimitedOP_POST_TOPIC);
								if (tmp >= SystemContext.getInstance().getPTM()) {
									// 超过每日发帖数
									ErrorCodeUtil.handleErrorCode(getActivity(), ErrorCode.EC_CLIENT_PUBLISHTOPIC_OVER_COUNT, null, SystemContext
											.getInstance().getPTM());
								} else {
									if (System.currentTimeMillis() - SystemContext.getInstance().getLastTopicPublishTime() < SystemContext
											.getInstance().getPIT() * 1000) {
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
										bundle.putSerializable(SystemConfig.BUNDLE_NAME_TOPIC_TAGS, (Serializable) tags);
										intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
										startActivityForResult(intent, SystemConfig.TOPICLIST_OPENPUBLISHTOPIC_REQUESTCODE);
									}
								}
								extendView.setClickable(true);
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							if (getActivity() != null) {
								ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
								extendView.setClickable(true);
							}
						}

					};
					ProxyFactory.getInstance().getPostbarProxy().getLimitedOPCount(callback, getActivity(), MsgsConstants.LimitedOP_POST_TOPIC);

				} else {
					// 未关注该贴吧
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (getActivity() != null) {

								ToastUtil.showToast(getActivity(), getString(R.string.postbar_publish_topic_info));
								extendView.setClickable(true);
							}
						}

					});

				}

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
				extendView.setClickable(true);
			}

		};
		ProxyFactory.getInstance().getGameProxy().getRelGameInfoForLocal(callback, getActivity(), targetId);

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
					if ((targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME)) {
						titleTxt.setText(result.getGamename());
						headerGameDetailViewHolder.gameName.setText(result.getGamename());
						headerGameDetailViewHolder.likeNum.setText(result.getLike() + "");
						headerGameDetailViewHolder.dislikeNum.setText(result.getDislike() + "");
						int mLikeCount = result.getMlike();
						int mDislikeCount = result.getMdislike();
						if (mLikeCount > 0) {
							headerGameDetailViewHolder.like.setBackgroundResource(R.drawable.game_like_pre);
						} else {
							headerGameDetailViewHolder.like.setBackgroundResource(R.drawable.game_like);
						}
						if (mDislikeCount > 0) {
							headerGameDetailViewHolder.dislike.setBackgroundResource(R.drawable.game_dislike_pre);
						} else {
							headerGameDetailViewHolder.dislike.setBackgroundResource(R.drawable.game_dislike);
						}
						if (result.getGamelogo() != null && !result.getGamelogo().isEmpty()) {
							new ImageLoader().loadRes(ResUtil.getSmallRelUrl(result.getGamelogo()), 0, headerGameDetailViewHolder.icon,
									R.drawable.common_default_icon);
						} else {
							new ImageLoader().loadRes("drawable://" + R.drawable.common_default_icon, 0, headerGameDetailViewHolder.icon);
						}
					}
				} else {
					LogUtil.e(TAG, "获取贴吧信息失败");
				}
				if ((targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME)) {
					// 获取贴吧赞和踩
					getGameExtData(gid);
				}

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if ((targetType == SystemConfig.GETTOPICLIST_TARGETTYPE_GAME)) {
					getGameExtData(gid);
				}

			}

		};
		ProxyFactory.getInstance().getGameProxy().getGameInfo(callback, getActivity(), gid);
	}

	/**
	 * 获取贴吧关注信息
	 * 
	 * @param gid
	 */
	private void getGameRel(long gid) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(getActivity());
		dialog.show();
		ProxyFactory.getInstance().getGameProxy().getRelGameInfo(new ProxyCallBack<RelationGameVo>() {

			@Override
			public void onSuccess(RelationGameVo result) {
				if (getActivity() != null) {
					if (result != null) {
						if (result.getRelation() == 1) {
							isfollow = true;
							headerGameDetailViewHolder.btn_follow.setEnabled(true);
							headerGameDetailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_pre);
						} else {
							isfollow = false;
							headerGameDetailViewHolder.btn_follow.setEnabled(true);
							headerGameDetailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_nor);
						}
					} else {
						isfollow = false;
						headerGameDetailViewHolder.btn_follow.setEnabled(true);
						headerGameDetailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_nor);
					}
					dialog.dismiss();
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (getActivity() != null) {
					dialog.dismiss();
					isfollow = false;
					headerGameDetailViewHolder.btn_follow.setEnabled(true);
					headerGameDetailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_nor);
				}
			}
		}, null, gid);
	}

	/**
	 * 添加或取消关注
	 * 
	 * @param gids
	 * @param follow
	 */
	private void followActionGame(final long gid, final int op) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(getActivity());
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (getActivity() != null) {
					switch (result) {
					case com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE:
						if (MsgsConstants.OP_FOLLOW == op) {
							isfollow = true;
							headerGameDetailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_pre);
							ToastUtil.showToast(getActivity(), getString(R.string.game_add_follow_success));

							HashMap<String, String> ummap = new HashMap<String, String>();
							ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(SystemContext.getInstance().getExtUserVo().getUserid()));
							ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, SystemContext.getInstance().getExtUserVo().getUsername());
							ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(gid));
							GameVo gvo = DaoFactory.getDaoFactory().getGameDao(headerGameDetailViewHolder.btn_follow.getContext()).getGameById(gid);
							if (gvo != null)
								ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, gvo.getGamename());
							MobclickAgent.onEvent(getActivity(), UMConfig.MSGS_EVENT_GAME_FOLLOW, ummap);

							// 刚关注贴吧时，需要获得贴吧最大index 设置成已经读到的最大数
							// setReadMaxIndex(getActivity(), gid);

						} else if (MsgsConstants.OP_UNFOLLOW == op) {
							isfollow = false;
							headerGameDetailViewHolder.btn_follow.setBackgroundResource(R.drawable.common_follow_nor);
							ToastUtil.showToast(getActivity(), getString(R.string.game_cannel_follow_success));
						}
						ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_GAME, null);
						break;
					default:
						break;
					}
					headerGameDetailViewHolder.btn_follow.setEnabled(true);
					dialog.dismiss();
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {

				dialog.dismiss();
				if (getActivity() != null) {
					if (result == Msgs.ErrorCode.EC_MSGS_OVER_COUNT_VALUE) {
						int mfcount = SystemContext.getInstance().getFGM();
						ErrorCodeUtil.handleErrorCode(getActivity(), ErrorCode.EC_CLIENT_FOLLOWGAME_OVER_COUNT, null, mfcount);
						GameUtil.redressGameRel(mfcount);
					} else {
						ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
					}
				}
			}
		}, getActivity(), gid, MsgsConstants.OT_GAME, op, null, null, null);
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
					gameExtDataVo = result;
					headerGameDetailViewHolder.likeNum.setText(result.getPraise() + "");
					headerGameDetailViewHolder.dislikeNum.setText(result.getCriticize() + "");
					if (result.getIspraise() > 0) {
						headerGameDetailViewHolder.like.setBackgroundResource(R.drawable.game_like_pre);
					} else {
						headerGameDetailViewHolder.like.setBackgroundResource(R.drawable.game_like);
					}
					if (result.getIscriticize() > 0) {
						headerGameDetailViewHolder.dislike.setBackgroundResource(R.drawable.game_dislike_pre);
					} else {
						headerGameDetailViewHolder.dislike.setBackgroundResource(R.drawable.game_dislike);
					}
					if (result.isBarMaster()) {
						isMaster = true;
					}
					// 更新db
					GameVo gvo = new GameVo();
					gvo.setGameid(gid);
					gvo.setLike(result.getPraise());
					gvo.setDislike(result.getCriticize());
					gvo.setMlike(result.getIspraise());
					gvo.setMdislike(result.getIspraise());
					DaoFactory.getDaoFactory().getGameDao(getActivity()).updateById(gvo);
					if (gameVo != null) {
						gameVo.setLike(result.getPraise());
						gameVo.setDislike(result.getCriticize());
						gameVo.setMlike(result.getIspraise());
						gameVo.setMdislike(result.getIspraise());
					}
					// 显示界面上其他扩展数据
					headerGameDetailViewHolder.user_num.setText(result.getFollowCount() + "");
					headerGameDetailViewHolder.group_num.setText(result.getGroupCount() + "");
					headerGameDetailViewHolder.news_num.setText(result.getNewsPostCount() + "");
					headerGameDetailViewHolder.gl_num.setText(result.getStrategyPostCount() + "");
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "获取扩展数据失败");
				isGetExtData = false;
			}
		};
		ProxyFactory.getInstance().getGameProxy().getGameExtData(callback, getActivity(), gid, 1);
	}

	/**
	 * 获得头上的贴吧信息
	 * 
	 * @param view
	 * @return
	 */
	private HeaderGameDetailViewHolder getHeaderGameDetailViewHolder(View view) {
		HeaderGameDetailViewHolder tmp = new HeaderGameDetailViewHolder();
		tmp.icon = (RoundedImageView) view.findViewById(R.id.icon);
		tmp.gameName = (TextView) view.findViewById(R.id.gameName);
		tmp.like = (Button) view.findViewById(R.id.like);
		tmp.likeNum = (TextView) view.findViewById(R.id.likeNum);
		tmp.dislike = (Button) view.findViewById(R.id.dislike);
		tmp.dislikeNum = (TextView) view.findViewById(R.id.dislikeNum);
		tmp.user_num = (TextView) view.findViewById(R.id.user_num);
		tmp.group_num = (TextView) view.findViewById(R.id.group_num);
		tmp.gl_num = (TextView) view.findViewById(R.id.gl_num);
		tmp.news_num = (TextView) view.findViewById(R.id.news_num);
		tmp.btn_follow = (Button) view.findViewById(R.id.btn_follow);
		tmp.ll_user_num = (RelativeLayout) view.findViewById(R.id.ll_user_num);
		tmp.ll_group_num = (RelativeLayout) view.findViewById(R.id.ll_group_num);
		tmp.ll_gl_num = (RelativeLayout) view.findViewById(R.id.ll_gl_num);
		tmp.ll_news_num = (RelativeLayout) view.findViewById(R.id.ll_news_num);

		tmp.dislike_ll = (RelativeLayout) view.findViewById(R.id.dislike_ll);
		tmp.like_ll = (RelativeLayout) view.findViewById(R.id.like_ll);

		tmp.like_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (FastClickLimitUtil.isFastClick())
					return;
				if (gameExtDataVo != null) {
					if (gameExtDataVo.getIspraise() > 0) {
						ToastUtil.showToast(getActivity(), getResources().getString(R.string.game_liked));
					} else if (gameExtDataVo.getIscriticize() > 0) {
						ToastUtil.showToast(getActivity(), getResources().getString(R.string.game_disliked));
					} else {
						tipPraise("赞", targetId, MsgsConstants.OP_PRAISE);
					}
				} else {
					ToastUtil.showToast(getActivity(), "数据未加载完成，稍后再操作");
				}
			}
		});

		tmp.dislike_ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (FastClickLimitUtil.isFastClick())
					return;
				if (gameExtDataVo != null) {
					if (gameExtDataVo.getIspraise() > 0) {
						ToastUtil.showToast(getActivity(), getResources().getString(R.string.game_liked));
					} else if (gameExtDataVo.getIscriticize() > 0) {
						ToastUtil.showToast(getActivity(), getResources().getString(R.string.game_disliked));
					} else {
						tipPraise("踩", targetId, MsgsConstants.OP_CRITICIZE);
					}
				} else {
					ToastUtil.showToast(getActivity(), "数据未加载完成，稍后再操作");
				}
			}
		});

		tmp.btn_follow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (gameVo != null) {
					if (isfollow) {
						followActionGame(targetId, MsgsConstants.OP_UNFOLLOW);
					} else {
						followActionGame(targetId, MsgsConstants.OP_FOLLOW);
					}
				} else {
					ToastUtil.showToast(getActivity(), "数据未加载完成，稍后再操作");
				}

			}
		});

		tmp.ll_user_num.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 游伴
				Intent intent = new Intent(getActivity(), GameDetailAlikeAcitivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, targetId);
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});
		tmp.ll_group_num.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 公会
				if (gameVo != null) {
					Intent intent = new Intent(getActivity(), GameDetailGroupActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, targetId);
					bundle.putString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME, gameVo.getGamename());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		tmp.ll_gl_num.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 攻略
				Intent intent = new Intent(getActivity(), GameTopicGLFragmentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, targetId);
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				startActivity(intent);
			}
		});
		tmp.ll_news_num.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 新闻
				Intent intent = new Intent(getActivity(), GameTopicNewsFragmentActivity.class);
				Bundle bundle = new Bundle();
				bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GID, targetId);
				intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
				startActivity(intent);
			}
		});

		return tmp;
	}

	/**
	 * 
	 * @param title
	 * @param op
	 */
	private void tipPraise(String title, long gid, Integer op) {
		headerGameDetailViewHolder.like.setEnabled(false);
		headerGameDetailViewHolder.dislike.setEnabled(false);
		if (SystemContext.getInstance().getGamePraiseTipCount() < SystemContext.getInstance().getGamePraiseMaxTipCount()) {
			creatOptDialog(title, gid, op);
			SystemContext.getInstance().setGamePraiseTipCount(SystemContext.getInstance().getGamePraiseTipCount() + 1);
		} else {
			actionGame(gid, op);
		}
	}

	/**
	 * 添加赞踩的确认框
	 */
	private void creatOptDialog(String titleTxt, final long gid, final int op) {
		final Dialog dialog = new Dialog(getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		dialog.setCanceledOnTouchOutside(false);
		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(titleTxt);
		final TextView txt = new TextView(getActivity());
		txt.setTextColor(getResources().getColor(R.color.dialog_content_text_color));
		txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txt.setText("您只能进行赞或踩操作一次，是否继续？");
		content.setPadding(DisplayUtil.dip2px(getActivity(), 10), 10, DisplayUtil.dip2px(getActivity(), 10), 10);
		content.removeAllViews();
		content.addView(txt, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		// 添加确定按钮功能
		Button commitBtn = (Button) dialog.findViewById(R.id.commitBtn);
		commitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				actionGame(gid, op);
				dialog.dismiss();
			}
		});
		// 添加取消按钮功能
		Button cannelBtn = (Button) dialog.findViewById(R.id.cannelBtn);
		cannelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				headerGameDetailViewHolder.like.setEnabled(true);
				headerGameDetailViewHolder.dislike.setEnabled(true);
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * 赞踩动作方法
	 * 
	 * @param gid
	 * @param op
	 */
	private void actionGame(final long gid, final int op) {
		final CustomProgressDialog dialog = CustomProgressDialog.createDialog(getActivity());
		dialog.show();
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				if (MsgsConstants.OP_PRAISE == op) {
					gameExtDataVo.setIspraise(1);
					headerGameDetailViewHolder.likeNum.setText(Integer.valueOf(headerGameDetailViewHolder.likeNum.getText().toString()) + 1 + "");
					headerGameDetailViewHolder.like.setBackgroundResource(R.drawable.game_like_pre);
					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(SystemContext.getInstance().getExtUserVo().getUserid()));
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, SystemContext.getInstance().getExtUserVo().getUsername());
					ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(gid));
					GameVo gvo = DaoFactory.getDaoFactory().getGameDao(getActivity()).getGameById(gid);
					if (gvo != null)
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, gvo.getGamename());
					MobclickAgent.onEvent(getActivity(), UMConfig.MSGS_EVENT_GAME_PRAISE, ummap);
				} else if (MsgsConstants.OP_CRITICIZE == op) {
					gameExtDataVo.setIscriticize(1);
					headerGameDetailViewHolder.dislikeNum.setText(Integer.valueOf(headerGameDetailViewHolder.dislikeNum.getText().toString()) + 1
							+ "");
					headerGameDetailViewHolder.dislike.setBackgroundResource(R.drawable.game_dislike_pre);
					HashMap<String, String> ummap = new HashMap<String, String>();
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_ID, String.valueOf(SystemContext.getInstance().getExtUserVo().getUserid()));
					ummap.put(UMConfig.MSGS_OPT_FROM_OBJ_NAME, SystemContext.getInstance().getExtUserVo().getUsername());
					ummap.put(UMConfig.MSGS_OPT_TO_OBJ_ID, String.valueOf(gid));
					GameVo gvo = DaoFactory.getDaoFactory().getGameDao(getActivity()).getGameById(gid);
					if (gvo != null)
						ummap.put(UMConfig.MSGS_OPT_TO_OBJ_NAME, gvo.getGamename());
					MobclickAgent.onEvent(getActivity(), UMConfig.MSGS_EVENT_GAME_CRITICIZE, ummap);
				}
				headerGameDetailViewHolder.like.setEnabled(true);
				headerGameDetailViewHolder.dislike.setEnabled(true);
				dialog.dismiss();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				headerGameDetailViewHolder.like.setEnabled(true);
				headerGameDetailViewHolder.dislike.setEnabled(true);
				dialog.dismiss();
				ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
			}
		}, getActivity(), gid, MsgsConstants.OT_GAME, op, null, null, null);
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

	private class HeaderGameDetailViewHolder {
		RoundedImageView icon;
		// 贴吧名
		TextView gameName;
		// 赞按钮
		Button like;
		// 赞数
		TextView likeNum;
		// 踩按钮
		Button dislike;
		// 踩数
		TextView dislikeNum;
		// 游伴数
		TextView user_num;
		// 公会数
		TextView group_num;
		// 公会信息
		// 攻略数
		TextView gl_num;
		// 新闻数
		TextView news_num;
		// 关注按钮
		Button btn_follow;

		// 游伴
		RelativeLayout ll_user_num;
		// 公会
		RelativeLayout ll_group_num;
		// 攻略
		RelativeLayout ll_gl_num;
		// 新闻
		RelativeLayout ll_news_num;
		// 踩布局
		RelativeLayout dislike_ll;
		// 赞布局
		RelativeLayout like_ll;

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
		refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		reloadData();
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
		loadData();
	}

}
