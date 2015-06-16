/**      
 * TopicDetailFragment.java Create on 2014-4-18     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.postbar.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.google.protobuf.ByteString;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ImageBrowerActivity;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.SendMsgCallBack;
import com.iwgame.msgs.common.ShareCallbackListener;
import com.iwgame.msgs.common.ShareDate;
import com.iwgame.msgs.common.ShareManager;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.common.imageloader.ImageLoader;
import com.iwgame.msgs.common.imageloader.PauseOnScrollListener;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.game.object.GameExtDataVo;
import com.iwgame.msgs.module.postbar.adapter.PageAdapter;
import com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter;
import com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter.AdapterEventListener;
import com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter.ReplyButtonOnClickListener;
import com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter.TopicDetailActionListener;
import com.iwgame.msgs.module.postbar.object.ImageVo;
import com.iwgame.msgs.module.user.ui.ReportActivity;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetail;
import com.iwgame.msgs.proto.Msgs.ContentDetail.ContentType;
import com.iwgame.msgs.proto.Msgs.ContentList;
import com.iwgame.msgs.proto.Msgs.ErrorCode;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.PostContent;
import com.iwgame.msgs.proto.Msgs.PostElement;
import com.iwgame.msgs.proto.Msgs.PostElementType;
import com.iwgame.msgs.proto.Msgs.PostbarActionResult;
import com.iwgame.msgs.proto.Msgs.PostbarTopicDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyDetail;
import com.iwgame.msgs.proto.Msgs.PostbarTopicReplyListResult;
import com.iwgame.msgs.proto.Msgs.QuotesReplyDetail;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.DialogUtil.OKCallBackListener;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.FastClickLimitUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.ShareUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.utils.Utils;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.widget.MenuMoreImageView;
import com.iwgame.msgs.widget.ResizeLayout;
import com.iwgame.msgs.widget.SendMsgView;
import com.iwgame.msgs.widget.URLSpanClickable;
import com.iwgame.msgs.widget.picker.CustomProgressDialog;
import com.iwgame.utils.ClipboardUtil;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.InputFilterUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ShareTaskUtil;
import com.iwgame.utils.StringUtil;
import com.iwgame.utils.ToastUtil;
import com.iwgame.utils.imageselector.BitmapBucket;
import com.iwgame.utils.imageselector.BitmapCache;
import com.iwgame.utils.imageselector.BitmapCache.ImageCallBack;
import com.iwgame.utils.imageselector.ImageGridActivity;
import com.youban.msgs.R;

/**
 * @ClassName: TopicDetailFragment
 * @Description: 帖子详情页
 * @author chuanglong
 * @date 2014-4-18 下午3:59:29
 * @Version 1.0
 * 
 */
public class TopicDetailFragment extends BaseFragment implements OnItemClickListener, OnItemLongClickListener, OnRefreshListener2<ListView> {
	protected static final String TAG = "TopicDetailFragment";
	private static final int BIGGER = 1;
	private static final int SMALLER = 2;
	private static final int MSG_RESIZE = 1;

	private static final int HEIGHT_THREADHOLD = 30;

	LayoutInflater inflater;

	PullToRefreshListView pullToRefreshListView;
	TextView titleTxt;
	SendMsgView sendMsgView;
	/**
	 * 回复回复时的提示布局
	 */
	LinearLayout postbar_topicreply_bottom_tips;
	/**
	 * 回复回复的用户名的显示控件
	 */
	TextView postbar_topicreply_bottom_tips_username;

	private LinearLayout bottomView;
	private LinearLayout bottomContentView;
	private LinearLayout likeContent;
	private TextView likeTxt;
	private ImageView likeIcon;
	private LinearLayout commentContent;
	private TextView commentTxt;
	private ImageView commentIcon;

	/**
	 * 自定义内容中的url是否响应单击
	 */
	private URLSpanClickable mURLSpanClickable = new URLSpanClickable();

	/**
	 * 回复的适配器
	 */
	TopicReplyListAdapter listAdapter;

	/**
	 * 回复的图片数据
	 */
	private List<ImageVo> images;

	/**
	 * 回复的列表数据
	 */
	List<Object> listdata = new ArrayList<Object>();
	/**
	 * 帖子id
	 */
	long topicId;

	/**
	 * 游戏id
	 */
	long gid;

	String gameName;
	/**
	 * 帖子详情
	 */
	Msgs.PostbarTopicDetail topicDetail = null;

	// filter = 0 ,默认全部， =1 过滤只看楼主
	private int filter = 0;
	/**
	 * 是否是吧主
	 */
	boolean isMaster = false;

	/**
	 * 需要回复的父回复
	 */
	// Msgs.PostbarTopicReplyDetail needReplyPReply = null;
	Object needReplyPReply = null;
	int listReplyPosition;

	/**
	 * 回复我的的回复，默认为空，只有从回复我的界面过滤才有该数据
	 */
	Msgs.PostbarTopicReplyDetail replyMyReply = null;

	/**
	 * 是否需要定位到最后一条数据，用于发消息后的定位
	 */
	boolean isNeedLocateLast = false;

	/**
	 * 总回复数
	 */
	int totalcount = 0;
	/**
	 * 总楼层数
	 */
	int levelCount = 0;
	/**
	 * 楼主的总回复数
	 */
	int totalcount_landlord = 0;
	/**
	 * 当前第一页码
	 */
	int currentFirstPage = 1;
	/**
	 * cache的页面数据
	 */
	Map<Integer, List<Msgs.PostbarTopicReplyDetail>> cache_pagedata = new HashMap<Integer, List<Msgs.PostbarTopicReplyDetail>>();
	/**
	 * cache的楼主的页面数据
	 */
	Map<Integer, List<Msgs.PostbarTopicReplyDetail>> cache_pagedata_landlord = new HashMap<Integer, List<Msgs.PostbarTopicReplyDetail>>();

	/**
	 * 请求回来的数据加载类型
	 */
	int responseDataloadType_before = 1;
	int responseDataloadType_after = 2;
	int responseDataloadType_clear = 3;

	/**
	 * 分享的地址
	 */
	String shareUrl = "";

	// 是否是第一次加载
	private boolean isfirstload = false;

	private BitmapCache cache;

	long targetId = 0;
	String repContent = "";
	int targetType = 0;
	// 评论数
	private int commentCount;

	/** 其他界面跳转到此界面的模式 **/
	private int mode = MODE_GENERAL;
	/** 默认普通模式 **/
	public static int MODE_GENERAL = 0;
	/** 快速评论模式 **/
	public static int MODE_COMMENT = 1;

	private int pmode = TopicReplyListAdapter.MODE_POSTBAR_UNSHOW;

	private CustomProgressDialog dialog;

	private LinearLayout extendView;

	private ImageView shareTopicMenu;

	private String gamename;

	Callback callback = new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			return false;
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
		StrictModeWrapper.init(this.getActivity());
		dialog = CustomProgressDialog.createDialog(getActivity());
		this.inflater = inflater;
		// 获得传入的参数
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			topicId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOPICDETAIL_TOPICID);
			replyMyReply = (Msgs.PostbarTopicReplyDetail) tmpbundle.getSerializable(SystemConfig.BUNDLE_NAME_TOPICDETAIL_REPLYMYREPLY);
			mode = tmpbundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE);
			pmode = tmpbundle.getInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_POSTBAR_DETAIL_MODE);
			gamename = tmpbundle.getString(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GAMENAME);
		}
		// 添加动态主界面
		View v = inflater.inflate(R.layout.postbar_topic_detail, container, false);
		isfirstload = true;
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
		if (!isfirstload)
			loadDataNoAction();
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	class InputHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RESIZE:
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	private InputHandler mHandler = new InputHandler();

	/**
	 * 初始化
	 */
	private void init(View v) {
		ResizeLayout layout = (ResizeLayout) v.findViewById(R.id.root_layout);
		layout.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			public void OnResize(int w, int h, int oldw, int oldh) {
				int change = BIGGER;
				if (h < oldh) {
					change = SMALLER;
				}

				Message msg = new Message();
				msg.what = 1;
				msg.arg1 = change;
				mHandler.sendMessage(msg);
			}
		});

		// 显示top左边菜单
		LinearLayout left = (LinearLayout) v.findViewById(R.id.left);
		left.setVisibility(View.VISIBLE);
		// 设置返回功能
		Button backBtn = (Button) v.findViewById(R.id.leftBtn);
		if (backBtn != null) {
			backBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 隐藏输入法
					Utils.hideSoftInput(TopicDetailFragment.this.getActivity(), sendMsgView.getSendMsgEditText());
					isBack();
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
		shareTopicMenu = new ImageView(this.getActivity());
		shareTopicMenu.setImageDrawable(getResources().getDrawable(R.drawable.postbar_menu_share));
		LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		extendView.addView(shareTopicMenu, layoutParams);
		extendView.setVisibility(View.VISIBLE);
		extendView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 分享
				shareTopic();
			}
		});
		// 设置top右边功能按钮
		ImageView rightMenu = new MenuMoreImageView(this.getActivity());
		layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout rightView = (LinearLayout) v.findViewById(R.id.rightView);
		LinearLayout right = (LinearLayout) v.findViewById(R.id.right);
		rightView.addView(rightMenu, layoutParams);
		right.setVisibility(View.VISIBLE);
		rightView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getActivity() instanceof TopicDetailActivity) {
					Utils.hideSoftInput(TopicDetailFragment.this.getActivity(), sendMsgView.getSendMsgEditText());
					((TopicDetailActivity) getActivity()).showMenu();
				}
			}
		});
		final FrameLayout view = (FrameLayout) v.findViewById(R.id.postbar_topicreply_list);
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.refreshList);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setOnItemClickListener(this);

		pullToRefreshListView.getRefreshableView().setSelector(R.color.transparent);
		pullToRefreshListView.getRefreshableView().setOnItemClickListener(this);
		pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(this);

		final ILoadingLayout headerLabels = pullToRefreshListView.getLoadingLayoutProxy(true, false);
		final ILoadingLayout footerLabels = pullToRefreshListView.getLoadingLayoutProxy(false, true);
		headerLabels.setLastUpdatedLabel(DateUtil.getFormatRefreshDate());
		footerLabels.setPullLabel("加载更多");// 刚下拉时，显示的提示
		footerLabels.setRefreshingLabel("加载中...");// 刷新时
		footerLabels.setReleaseLabel("松开后加载");// 下来达到一定距离时，显示的提示

		postbar_topicreply_bottom_tips = (LinearLayout) view.findViewById(R.id.postbar_topicreply_bottom_tips);

		postbar_topicreply_bottom_tips_username = (TextView) view.findViewById(R.id.postbar_topicreply_bottom_tips_username);

		postbar_topicreply_bottom_tips_username.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setReplyPreply(null);
				postbar_topicreply_bottom_tips.setVisibility(View.GONE);
				if (sendMsgView.getSendMsgEditText().getHint().toString().length() > 0) {
					sendMsgView.setEditTextHint("请输入内容");
				}
				Utils.hideSoftInput(TopicDetailFragment.this.getActivity(), sendMsgView.getSendMsgEditText());
			}
		});
		ReplyButtonOnClickListener replyButtonOnClickListener = new ReplyButtonOnClickListener() {

			@Override
			public void onClick(int position) {
				PostbarTopicReplyDetail detail = (PostbarTopicReplyDetail) listdata.get(position);
				if (detail.getIsDel()) {
					ToastUtil.showToast(getActivity(), getString(R.string.postbar_reply_info_isdel_reply));
				} else {
					listReplyPosition = position;
					setReplyPreply(detail);
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter.
			 * ReplyButtonOnClickListener
			 * #onClickReply(com.iwgame.msgs.proto.Msgs.QuotesReplyDetail)
			 */
			@Override
			public void onClickReply(QuotesReplyDetail detail, int position) {
				listReplyPosition = position;
				setReplyPreply(detail);
			}

		};

		TopicDetailActionListener topicDetailActionListener = new TopicDetailActionListener() {

			@Override
			public void onClickContent() {
				Utils.hideSoftInput(getActivity(), sendMsgView.getSendMsgEditText());
			}

			@Override
			public boolean onLongClickContent() {
				if (mURLSpanClickable != null) {
					mURLSpanClickable.setIsClickUrlable(false);
				}
				showActionDialog(topicDetail);
				return false;
			}

			@Override
			public void onClickShare() {
				shareTopic();
			}

		};

		listAdapter = new TopicReplyListAdapter(getActivity(), pullToRefreshListView.getContext(), listdata, mURLSpanClickable,
				new AdapterEventListener() {

					@Override
					public void onUnLikeHander() {
						int pcount = topicDetail.getPraiseNums();
						if (topicDetail.getHasPraise() > 0) {
							pcount = pcount - 1;
						}
						if (pcount >= 9999) {
							likeTxt.setText("点赞(9999+)");
						} else {
							likeTxt.setText("点赞(" + pcount + ")");
						}
						likeTxt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.topiclist_item_link_txt));
						likeIcon.setBackgroundResource(R.drawable.game_like2);
					}

					@Override
					public void onLikeHander() {
						int pcount = topicDetail.getPraiseNums();
						if (topicDetail.getHasPraise() <= 0) {
							pcount = pcount + 1;
						}
						if (pcount >= 9999) {
							likeTxt.setText("已赞(9999+)");
						} else {
							likeTxt.setText("已赞(" + pcount + ")");
						}
						likeTxt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.topic_bottom_liked_color));
						likeIcon.setBackgroundResource(R.drawable.game_like3);
					}

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * com.iwgame.msgs.module.postbar.adapter.TopicReplyListAdapter
					 * . AdapterEventListener#onCommentHander()
					 */
					@Override
					public void onCommentHander() {
						bottomView.setVisibility(View.VISIBLE);
						bottomContentView.setVisibility(View.GONE);
						setReplyPreply(null);
						postbar_topicreply_bottom_tips.setVisibility(View.GONE);
						sendMsgView.setEditTextHint("请输入内容");
						sendMsgView.getSendMsgEditText().setText("");
						Utils.hideSoftInput(TopicDetailFragment.this.getActivity(), sendMsgView.getSendMsgEditText());
					}

				});
		listAdapter.setReplyButtonOnClickListener(replyButtonOnClickListener);
		listAdapter.setTopicDetailListener(topicDetailActionListener);
		listAdapter.setPmode(pmode);
		listAdapter.setGamename(gamename);
		pullToRefreshListView.setAdapter(listAdapter);
		// 设置滚动的时候图片是否加载
		pullToRefreshListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true) {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.common.imageloader.PauseOnScrollListener#onScroll
			 * (android.widget.AbsListView, int, int, int)
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.iwgame.msgs.common.imageloader.PauseOnScrollListener#
			 * onScrollStateChanged(android.widget.AbsListView, int)
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				super.onScrollStateChanged(view, scrollState);
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:// 触摸后滚动
					bottomView.setVisibility(View.GONE);
					bottomContentView.setVisibility(View.VISIBLE);
					setReplyPreply(null);
					postbar_topicreply_bottom_tips.setVisibility(View.GONE);
					Utils.hideSoftInput(TopicDetailFragment.this.getActivity(), sendMsgView.getSendMsgEditText());
					break;
				}
			}
		});

		// 设置底部发布框
		bottomView = (LinearLayout) v.findViewById(R.id.bottomView);
		bottomContentView = (LinearLayout) v.findViewById(R.id.bottomContentView);
		likeTxt = (TextView) v.findViewById(R.id.likeTxt);
		likeIcon = (ImageView) v.findViewById(R.id.likeIcon);
		likeContent = (LinearLayout) v.findViewById(R.id.likeContent);
		likeOrUnLike();
		commentTxt = (TextView) v.findViewById(R.id.commentTxt);
		commentIcon = (ImageView) v.findViewById(R.id.commentIcon);
		commentContent = (LinearLayout) v.findViewById(R.id.commentContent);
		commentContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bottomView.setVisibility(View.VISIBLE);
				bottomContentView.setVisibility(View.GONE);
				clearImages();
				images = new ArrayList<ImageVo>();
			}
		});
		sendMsgView = (SendMsgView) View.inflate(this.getActivity(), R.layout.public_send_msg_view, null);
		bottomView.addView(sendMsgView);
		// 设置发图片按钮不显示
		sendMsgView.setSendAudioButtonVisibility(View.GONE);
		SendMsgCallBack sendMsgCallBack = new SendMsgCallBack() {

			@Override
			public void send(final int msgtype, final String content, byte[] contentBytes, final int action) {
				if (msgtype == MessageContentType.TEXT_VALUE) {
					if (topicDetail != null) {
						if (topicDetail.getIsLock()) {
							ToastUtil.showToast(getActivity(),
									SystemContext.getInstance().getContext().getResources().getString(R.string.game_topic_lock));
						} else {
							if (!content.trim().isEmpty() || (images != null && images.size() > 0)) {
								replyTopic(content);
							} else {
								ToastUtil.showToast(TopicDetailFragment.this.getActivity(), "回复内容必须有文字或图片");
							}
						}
					}
				} else if (msgtype == MessageContentType.IMAGE_VALUE && action == SendMsgCallBack.ACTION_PHOTO) {
					// 相机
					if (images != null && images.size() >= BitmapBucket.max) {
						ToastUtil.showToast(getActivity(), "已上传" + BitmapBucket.max + "张相片，不能继续上传了哦！");
					} else {
						photoPath = PhotoUtil.sdcardFileRootPath + "msgs_" + System.currentTimeMillis() + "_uban.jpg";
						PhotoUtil.doTakePhoto(TopicDetailFragment.this, photoPath);
					}
				} else if (msgtype == MessageContentType.IMAGE_VALUE && action == SendMsgCallBack.ACTION_PICTURE) {
					Intent intent = new Intent(getActivity(), ImageGridActivity.class);
					intent.putExtra(ImageGridActivity.IMAGE_MAP, (Serializable) images);
					TopicDetailFragment.this.startActivityForResult(intent, ImageGridActivity.REQUESTCODE);
				} else if (msgtype == MessageContentType.VOICE_VALUE && action == SendMsgCallBack.ACTION_MICROPHONE) {
					// 声音
				}

			}

			@Override
			public void setAudioRecorderStatus(int status) {
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.common.SendMsgCallBack#setListViewLastIndexSelection
			 * ()
			 */
			@Override
			public void setListViewLastIndexSelection(int delaytime) {
				new Handler().postDelayed(new Runnable() {

					public void run() {
						if (pullToRefreshListView != null && needReplyPReply == null)
							pullToRefreshListView.getRefreshableView().setSelection(
									pullToRefreshListView.getRefreshableView().getAdapter().getCount() - 1);
					}

				}, delaytime);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.common.SendMsgCallBack#createBundPhoneDialog()
			 */
			@Override
			public void createDialog() {
				createBundPhoneDialog();
			}

		};
		sendMsgView.setSendMsgCallBack(sendMsgCallBack);

		// 获得数据
		// 获得帖子详情
		ProxyCallBack<Msgs.PostbarTopicDetail> callback3 = new ProxyCallBack<Msgs.PostbarTopicDetail>() {
			@Override
			public void onSuccess(final PostbarTopicDetail result) {
				if (result != null) {
					if (result.getIsDel()) {
						ToastUtil.showToast(getActivity(), getString(R.string.postbaor_replymy_topic_del));
						if (getActivity() != null) {
							getActivity().finish();
							System.gc();
						}
					}
					topicDetail = result;
					setTopicDetailTitle();
					gid = topicDetail.getGameid();
					if (gid != 0) {
						getTopicInfo();
					}

					totalcount = result.getComments();
					listAdapter.setReplyTotalCount(totalcount);
					if (getActivity() instanceof TopicDetailActivity) {
						((TopicDetailActivity) getActivity()).setIsFavAndRefreshMenu(result.getIsFavorite());
					}

					// 设置楼主到list 中去
					listdata.add(topicDetail);
					// 获得回复的列表 加载数据
					if (replyMyReply != null) {
						int tmp1 = replyMyReply.getLevel() / SystemConfig.PAGE_SIZE;
						int tmp2 = replyMyReply.getLevel() % SystemConfig.PAGE_SIZE;
						int page = (tmp2 == 0 ? tmp1 : (tmp1 + 1));
						loadDataByPage(page, SystemConfig.PAGE_SIZE, responseDataloadType_clear);
					} else {
						loadDataByPage(1, SystemConfig.PAGE_SIZE, responseDataloadType_clear);
					}
					// 赞
					int pcount = topicDetail.getPraiseNums();
					if (topicDetail.getHasPraise() > 0) {
						if (pcount >= 9999) {
							likeTxt.setText("已赞(9999+)");
						} else {
							likeTxt.setText("已赞(" + pcount + ")");
						}
						likeTxt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.topic_bottom_liked_color));
						likeIcon.setBackgroundResource(R.drawable.game_like3);
					} else {
						if (pcount >= 9999) {
							likeTxt.setText("点赞(9999+)");
						} else {
							likeTxt.setText("点赞(" + pcount + ")");
						}
						likeTxt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.topiclist_item_link_txt));
						likeIcon.setBackgroundResource(R.drawable.game_like2);
					}
					// 评论
					commentCount = topicDetail.getComments();
					if (commentCount >= 9999) {
						commentTxt.setText("评论(9999+)");
					} else {
						commentTxt.setText("评论(" + commentCount + ")");
					}
				} else {
					ToastUtil.showToast(getActivity(), "帖子加载失败,请稍后再试！");
					if (getActivity() != null) {
						getActivity().finish();
						System.gc();
					}
				}

			}

			@Override
			public void onFailure(final Integer result, final String resultMsg) {
				ToastUtil.showToast(getActivity(), "帖子加载失败,请稍后再试！");
				if (getActivity() != null) {
					getActivity().finish();
					System.gc();
				}
			}

		};
		ProxyFactory.getInstance().getPostbarProxy().getTopicDetail(callback3, this.getActivity(), topicId);
		if (mode == MODE_COMMENT) {
			bottomView.setVisibility(View.VISIBLE);
			bottomContentView.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 */
	private void getTopicDetail() {
		ProxyFactory.getInstance().getPostbarProxy().getTopicDetail(new ProxyCallBack<Msgs.PostbarTopicDetail>() {

			@Override
			public void onSuccess(PostbarTopicDetail result) {
				topicDetail = result;
				totalcount = result.getComments();
				if (listAdapter != null)
					listAdapter.setReplyTotalCount(totalcount);
				// 赞
				if (likeTxt != null && commentTxt != null) {
					int pcount = topicDetail.getPraiseNums();
					if (topicDetail.getHasPraise() > 0) {
						if (pcount >= 9999) {
							likeTxt.setText("已赞(9999+)");
						} else {
							likeTxt.setText("已赞(" + pcount + ")");
						}
						likeTxt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.topic_bottom_liked_color));
						likeIcon.setBackgroundResource(R.drawable.game_like3);
					} else {
						if (pcount >= 9999) {
							likeTxt.setText("点赞(9999+)");
						} else {
							likeTxt.setText("点赞(" + pcount + ")");
						}
						likeTxt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.topiclist_item_link_txt));
						likeIcon.setBackgroundResource(R.drawable.game_like2);
					}
					// 评论
					commentCount = topicDetail.getComments();
					if (commentCount >= 9999) {
						commentTxt.setText("评论(9999+)");
					} else {
						commentTxt.setText("评论(" + commentCount + ")");
					}
					if (listdata != null && listAdapter != null) {
						listdata.remove(0);
						listdata.add(0, topicDetail);
						listAdapter.notifyDataSetChanged();
					}
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub

			}

		}, this.getActivity(), topicId);
	}

	private void getTopicInfo() {
		// 获得游戏信息
		ProxyCallBack<GameVo> callback = new ProxyCallBack<GameVo>() {

			@Override
			public void onSuccess(GameVo result) {
				if (result != null) {
					// titleTxt.setText(result.getGamename() + "吧");
					gameName = result.getGamename();
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "获得贴吧信息异常");
			}
		};
		ProxyFactory.getInstance().getGameProxy().getGameInfo(callback, this.getActivity(), gid);

		// 获得是否是吧主

		ProxyCallBack<RelationGameVo> callback2 = new ProxyCallBack<RelationGameVo>() {

			@Override
			public void onSuccess(RelationGameVo result) {

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "获得用户与贴吧的关系异常");
			}
		};
		ProxyFactory.getInstance().getGameProxy().getRelGameInfo(callback2, this.getActivity(), gid);

		// 获得是否是吧主

		ProxyCallBack<GameExtDataVo> callback4 = new ProxyCallBack<GameExtDataVo>() {

			@Override
			public void onSuccess(GameExtDataVo result) {
				// 判断是否吧主
				if (result != null && result.isBarMaster()) {
					isMaster = true;
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				LogUtil.e(TAG, "获取吧主信息错误");
			}
		};
		ProxyFactory.getInstance().getGameProxy().getGameExtData(callback4, this.getActivity(), gid, 19);
	}

	/**
	 * 
	 * @param tv
	 * @param detail
	 * @param op
	 */
	private void likeOrUnLike() {
		likeContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (topicDetail == null)
					return;
				int op = MsgsConstants.OP_PRAISE;
				if (likeTxt.getText().toString().contains("点赞")) {
					op = MsgsConstants.OP_PRAISE;
				} else {
					op = MsgsConstants.OP_PRAISE_CANCEL;
				}
				final int relOp = op;
				likeContent.setClickable(false);
				final UserVo muser = SystemContext.getInstance().getExtUserVo();
				ProxyFactory.getInstance().getPostbarProxy().likeOrUnLikeTopic(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						if (relOp == MsgsConstants.OP_PRAISE) {
							int pcount = topicDetail.getPraiseNums();
							if (topicDetail.getHasPraise() <= 0) {
								pcount = pcount + 1;
							}
							if (pcount >= 9999) {
								likeTxt.setText("已赞(9999+)");
							} else {
								likeTxt.setText("已赞(" + pcount + ")");
							}
							likeTxt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.topic_bottom_liked_color));
							likeIcon.setBackgroundResource(R.drawable.game_like3);
						} else if (relOp == MsgsConstants.OP_PRAISE_CANCEL) {
							int pcount = topicDetail.getPraiseNums();
							if (topicDetail.getHasPraise() > 0) {
								pcount = pcount - 1;
							}
							if (pcount >= 9999) {
								likeTxt.setText("点赞(9999+)");
							} else {
								likeTxt.setText("点赞(" + pcount + ")");
							}
							likeTxt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.topiclist_item_link_txt));
							likeIcon.setBackgroundResource(R.drawable.game_like2);
						}
						ProxyFactory.getInstance().getPostbarProxy().getTopicDetail(new ProxyCallBack<Msgs.PostbarTopicDetail>() {

							@Override
							public void onSuccess(PostbarTopicDetail result) {
								if (result != null && !result.getIsDel() && listdata.size() > 0 && listdata.get(0) instanceof PostbarTopicDetail) {
									topicDetail = result;
									listdata.remove(0);
									listdata.add(0, topicDetail);
									listAdapter.notifyDataSetChanged();
									likeContent.setClickable(true);
								}
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								likeContent.setClickable(true);
							}
						}, getActivity(), topicId);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						switch (result) {
						case ErrorCode.EC_MSGS_ALL_GUEST_DENY_VALUE:
							Intent intent = new Intent(getActivity(), BundPhoneActivity.class);
							startActivity(intent);
							break;
						default:
							ErrorCodeUtil.handleErrorCode(getActivity(), result, null);
							break;
						}
						likeContent.setClickable(true);
					}
				}, getActivity(), topicDetail.getId(), topicDetail.getId(), topicDetail.getPosterUid(), topicDetail.getPosterUid(), 0, relOp);
			}
		});
	}

	private void setTopicDetailTitle() {
		if (topicDetail == null)
			return;
		if (topicDetail.getIsNotice()) {
			titleTxt.setText("公告帖");
			return;
		}
		if (topicDetail.getIsTop()) {
			titleTxt.setText("置顶帖");
			return;
		}
		if (topicDetail.getIsEssence()) {
			titleTxt.setText("精华帖");
			return;
		}
		if (topicDetail.getIsTopicSet()) {
			titleTxt.setText("集合帖");
			return;
		}
		if (topicDetail.getIsLock()) {
			titleTxt.setText("锁定帖");
			return;
		}
		if (topicDetail.getIsVisControl()) {
			titleTxt.setText("仅楼主层主可见帖");
			return;
		}
		titleTxt.setText("主题帖");
	}

	public void shareTopic() {
		if (topicDetail == null)
			return;
		if (topicDetail.getIsVisControl()) {
			ToastUtil.showToast(getActivity(), "仅楼主层主可见帖子不能被分享哦！");
			return;
		}

		if (FastClickLimitUtil.isFastClick())
			return;
		ShareDate shareDate = new ShareDate();
		shareDate.setShareType(ShareUtil.SHARE_TYPE_SHAER);// 类型为分享
		shareDate.setTargetType(ShareUtil.TYPE_POST);// 类型为帖子
		shareDate.setInTargetType(SystemConfig.CONTENT_TYPE_TOPIC);// 内部分享类型
		shareDate.setTargetId(topicDetail.getId());// 帖子id(目标ID)
		shareDate.setTargetName(topicDetail.getTitle());// 帖子名称（目标名称）
		shareDate.setTempString(gameName);// 备用字段，传入贴吧名称（暂仅分享帖子时使用）
		shareDate.setTitle(topicDetail.getTitle());
		// shareDate.setSite("游伴");//QQ空间分享时使用（暂固定写法）
		// shareDate.setSiteUrl("http://www.51uban.com");//QQ空间分享时使用（暂固定写法）
		// 获得图片地址
		String tmpImageSrc = "";
		if (topicDetail.hasPostContent() && topicDetail.getPostContent().getElementsCount() > 0) {
			for (int i = 0; i < topicDetail.getPostContent().getElementsCount(); i++) {
				if (topicDetail.getPostContent().getElements(i).getType() == PostElementType.PE_IMAGE_ID_REF) {
					tmpImageSrc = topicDetail.getPostContent().getElements(i).getResourceId();
					break;
				}
			}
		}
		if (!tmpImageSrc.isEmpty()) {
			shareDate.setImageUrl(ResUtil.getSmallRelUrl(tmpImageSrc));
			shareDate.setImagePath(tmpImageSrc);
		} else {
			shareDate.setImageUrl(ResUtil.getSmallRelUrl("i_youban"));
			shareDate.setImagePath("i_youban");
		}

		ShareCallbackListener listener = new ShareCallbackListener() {

			@Override
			public void doSuccess(String plamType) {
				// 分享帖子信息
				ShareTaskUtil.makeShareTask(getActivity(), TAG, topicDetail.getId(), MsgsConstants.OT_TOPIC, MsgsConstants.OP_RECORD_SHARE, plamType,
						null, this.shortUrl);
			}

			@Override
			public void doFail() {
			}

		};
		ShareManager.getInstance().share(getActivity(), inflater, topicDetail, shareDate, listener);
	}

	/**
	 * 创建游客绑定手机的提示框
	 * 
	 * @param actionName
	 */
	private void createBundPhoneDialog() {
		Intent intent = new Intent(getActivity(), BundPhoneActivity.class);
		startActivity(intent);
	}

	/**
	 * 回复帖子
	 * 
	 * @param content
	 */
	private void replyTopic(final String content) {
		pullToRefreshListView.setMode(Mode.DISABLED);
		dialog.show();
		sendMsgView.getSendButton().setClickable(false);
		// 发回复
		if (needReplyPReply == null) {
			// 对帖子回复
			targetId = topicId;
			targetType = MsgsConstants.OT_TOPIC;
		} else {
			// 对回复进行回复
			if (needReplyPReply instanceof Msgs.PostbarTopicReplyDetail) {
				targetId = ((PostbarTopicReplyDetail) needReplyPReply).getRid();
				repContent = ((PostbarTopicReplyDetail) needReplyPReply).getContent();
			} else {
				targetId = ((QuotesReplyDetail) needReplyPReply).getId();
				repContent = ((QuotesReplyDetail) needReplyPReply).getPostContent().getElementsList().get(0).getText();
			}
			targetType = MsgsConstants.OT_TOPICREPLY;
		}

		ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {

			@Override
			public void onBack(BDLocation bdLocation) {
				ContentList.Builder cb = ContentList.newBuilder();
				cb.setTitle("");
				ContentDetail.Builder cd = ContentDetail.newBuilder();
				cd.setType(ContentType.TEXT);
				cd.setText(content);
				cd.setSeq(1);
				cb.addContentDetail(cd);
				if (images != null) {
					for (int j = 0; j < images.size(); j++) {
						byte[] imageData = images.get(j).getData();
						if (imageData != null && imageData.length > 0) {
							cd = ContentDetail.newBuilder();
							cd.setType(ContentType.IMAGE);
							cd.setResourceId(ByteString.copyFrom(imageData));
							cd.setSeq(j + 1);
							cb.addContentDetail(cd);
						}
					}
				}
				ProxyFactory.getInstance().getPostbarProxy().publicTopic(
						new ProxyCallBack<PostbarActionResult>() {

							@Override
							public void onSuccess(PostbarActionResult result) {
								dialog.dismiss();
								sendMsgView.hideSendPicCount();
								sendMsgView.getSendButton().setClickable(true);
								if (result != null) {
									ToastUtil.showToast(getActivity(), "发送成功");
									if (needReplyPReply == null) {
										// 帖子
										UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_BAR_COMMENT_TOPIC, null, null,
												String.valueOf(topicDetail.getId()), topicDetail.getTitle(), true);
									} else {
										// 回复
										UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_BAR_COMMENT_REPLY, null, null, String.valueOf(targetId),
												repContent, true);
									}
									commentCount++;
									if (commentCount >= 9999) {
										commentTxt.setText("评论(9999+)");
									} else {
										commentTxt.setText("评论(" + commentCount + ")");
									}
									if (needReplyPReply == null) {
										isNeedLocateLast = true;
									} else {
										QuotesReplyDetail.Builder qrBuilder = QuotesReplyDetail.newBuilder();
										qrBuilder.setId(result.getResultId());
										PostContent.Builder pcBuilder = PostContent.newBuilder();
										PostElement.Builder eBuilder = PostElement.newBuilder();
										eBuilder.setType(PostElementType.PE_TEXT);
										eBuilder.setText(content);
										pcBuilder.addElements(eBuilder);
										qrBuilder.setPostContent(pcBuilder);
										if (needReplyPReply instanceof Msgs.PostbarTopicReplyDetail) {
											qrBuilder.setToName(((PostbarTopicReplyDetail) needReplyPReply).getPosterNickname());
										} else {
											qrBuilder.setToName(((QuotesReplyDetail) needReplyPReply).getFromName());
										}
										UserVo uvo = SystemContext.getInstance().getExtUserVo();
										if (uvo != null) {
											qrBuilder.setFromName(uvo.getUsername());
											qrBuilder.setFromUid(uvo.getUserid());
										}
										listAdapter.addReply(qrBuilder.build(), listReplyPosition);
										// listAdapter.notifyDataSetChanged();
									}
									// 清空数据
									setReplyPreply(null);
									postbar_topicreply_bottom_tips.setVisibility(View.GONE);
									sendMsgView.setEditTextHint("请输入内容");
									sendMsgView.setHideSendMsgSmileypanel();
									sendMsgView.getSendMsgEditText().setText("");
									// 隐藏输入法
									Utils.hideSoftInput(TopicDetailFragment.this.getActivity(), sendMsgView.getSendMsgEditText());

									// 清空图片
									clearImages();
									images = new ArrayList<ImageVo>();
									// 如果是只看楼主状态下，改成看全部
									if (filter == MsgsConstants.POSTBAR_REPLY_FILTER_POSTER_ONLY) {
										filter = 0;
										listdata.clear();
										listdata.add(topicDetail);
										// 改变数据
										if (getActivity() instanceof TopicDetailActivity)
											((TopicDetailActivity) getActivity()).setFilterRefreshMenu(0);
									}
									if (listdata.size() > 1) {
										if (((Msgs.PostbarTopicReplyDetail) listdata.get(listdata.size() - 1)).getLevel() + SystemConfig.PAGE_SIZE < totalcount) {
											// 清空，加载最后的楼层
											listdata.clear();
											listdata.add(topicDetail);
											loadDataByPage(getPageTotal(levelCount + 1, SystemConfig.PAGE_SIZE), SystemConfig.PAGE_SIZE,
													responseDataloadType_clear);
											pullToRefreshListView.setMode(Mode.BOTH);
										} else {
											// 加载所有未加载楼层
											loadDataAddLast();
										}
									} else {
										loadDataByPage(getPageTotal(levelCount + 1, SystemConfig.PAGE_SIZE), SystemConfig.PAGE_SIZE,
												responseDataloadType_clear);
										pullToRefreshListView.setMode(Mode.BOTH);
									}
								} else {
									ToastUtil.showToast(getActivity(), "发帖失败");
									if (needReplyPReply == null) {
										// 帖子
										UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_BAR_COMMENT_TOPIC, null, null,
												String.valueOf(topicDetail.getId()), topicDetail.getTitle(), false);
									} else {
										// 回复
										UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_BAR_COMMENT_REPLY, null, null, String.valueOf(targetId),
												repContent, false);
									}
									pullToRefreshListView.setMode(Mode.BOTH);
								}
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								dialog.dismiss();
								sendMsgView.getSendButton().setClickable(true);
								ErrorCodeUtil.handleErrorCode(TopicDetailFragment.this.getActivity(), result, resultMsg);
								if (needReplyPReply == null) {
									if (topicDetail != null) {
										// 帖子
										UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_BAR_COMMENT_TOPIC, null, null,
												String.valueOf(topicDetail.getId()), topicDetail.getTitle(), false);
									}
								} else {
									// 回复
									UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_BAR_COMMENT_REPLY, null, null, String.valueOf(targetId),
											repContent, false);
								}
								pullToRefreshListView.setMode(Mode.BOTH);
							}
						}, getActivity(), targetId, gid, MsgsConstants.OP_REPLY_TOPIC, targetType, (images != null && images.size() > 0) ? 1 : 0,
						cb.build(),
						SystemContext.getInstance().getLocation(), 0);
			}
		});
	}

	private void loadDataNoAction() {
		ProxyCallBack<PostbarTopicReplyListResult> callback = new ProxyCallBack<PostbarTopicReplyListResult>() {
			@Override
			public void onSuccess(final PostbarTopicReplyListResult result) {
			}

			@Override
			public void onFailure(final Integer result, final String resultMsg) {
			}

		};
		ProxyFactory.getInstance().getPostbarProxy()
				.getTopicReplyList(callback, getActivity(), topicId, MsgsConstants.OT_TOPIC, 0, MsgsConstants.POSTBAR_REPLAY_BY_PAGE, 1, 20);
	}

	/**
	 * 数据的按照页码加载
	 * 
	 * @param page
	 * @param pagesize
	 * @param responseDataloadType
	 */
	private void loadDataByPage(final int page, final int pagesize, final int responseDataloadType) {
		if (isfirstload)
			dialog.show();
		if (cache_pagedata.containsKey(page)) {
			// 代表有cache
			realloadData(responseDataloadType, page, cache_pagedata.get(page));
		} else {
			// 需要从网络加载数据
			ProxyCallBack<PostbarTopicReplyListResult> callback = new ProxyCallBack<PostbarTopicReplyListResult>() {
				@Override
				public void onSuccess(final PostbarTopicReplyListResult result) {
					if (getActivity() != null) {
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								checkFirstLoadCompleted();
								if (result != null) {
									// 刷新总数
									if (result.getTotalCount() != totalcount && filter == 0) {
										totalcount = result.getTotalCount();
										listAdapter.setReplyTotalCount(totalcount);
									}
									levelCount = result.getLevelCount();
									if (filter == MsgsConstants.POSTBAR_REPLY_FILTER_POSTER_ONLY) {
										totalcount_landlord = result.getTotalCount();
									}

									List<PostbarTopicReplyDetail> list = result.getEntryList();
									if (list.size() == pagesize) {
										if (filter == 0 && page != 1) {
											cache_pagedata.put(page, list);
										}
										if (filter == MsgsConstants.POSTBAR_REPLY_FILTER_POSTER_ONLY) {
											cache_pagedata_landlord.put(page, list);
										}
									}
									realloadData(responseDataloadType, page, list);
								}

								else {
									// 原因有可能只有帖子详情，所以还是要通知刷新
									listAdapter.notifyDataSetChanged();
								}
							}
						});
					}
				}

				@Override
				public void onFailure(final Integer result, final String resultMsg) {
					checkFirstLoadCompleted();
					ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);
					isNeedLocateLast = false;
					pullToRefreshListView.onRefreshComplete();
				}

			};
			ProxyFactory
					.getInstance()
					.getPostbarProxy()
					.getTopicReplyList(callback, getActivity(), topicId, MsgsConstants.OT_TOPIC, filter, MsgsConstants.POSTBAR_REPLAY_BY_PAGE, page,
							pagesize);

		}

	}

	/**
	 * 
	 */
	private void checkFirstLoadCompleted() {
		if (isfirstload) {
			dialog.dismiss();
			isfirstload = false;
			// 如果是快速评论模式设置输入框焦点、打开键盘
			if (mode == MODE_COMMENT) {
				sendMsgView.postDelayed(new Runnable() {

					@Override
					public void run() {
						sendMsgView.setEditTextFocus();
					}
				}, 1000);
			}
		}
	}

	/**
	 * 真正加载数据
	 * 
	 * @param responseDataloadType
	 * @param list
	 */
	private void realloadData(int responseDataloadType, int page, final List<PostbarTopicReplyDetail> list) {

		pullToRefreshListView.onRefreshComplete();
		if (list != null && list.size() > 0) {
			if (responseDataloadType == responseDataloadType_clear) {
				listdata.clear();
				listdata.add(topicDetail);
				currentFirstPage = page;
				for (int i = 0; i < list.size(); i++) {
					listdata.add(list.get(i));
				}
			} else if (responseDataloadType == responseDataloadType_before) {
				currentFirstPage = page;
				// 增加在前面
				for (int i = 0; i < list.size(); i++) {
					listdata.add(1, list.get(list.size() - 1 - i));
				}
			} else if (responseDataloadType == responseDataloadType_after) {
				// 增加在后面
				for (int i = 0; i < list.size(); i++) {
					listdata.add(list.get(i));
				}

			}
			listAdapter.notifyDataSetChanged();

			if (listdata.size() > 1 && responseDataloadType == responseDataloadType_clear) {
				pullToRefreshListView.getRefreshableView().postDelayed(new Runnable() {

					@Override
					public void run() {
						pullToRefreshListView.getRefreshableView().setSelection(1);
					}
				}, 100);

			}

			if (listdata.size() - list.size() > 0 && list.size() > 0 && responseDataloadType == responseDataloadType_before) {
				pullToRefreshListView.getRefreshableView().post(new Runnable() {

					@Override
					public void run() {
						pullToRefreshListView.getRefreshableView().setSelectionFromTop(
								pullToRefreshListView.getRefreshableView().getFirstVisiblePosition() + 1 + list.size(), 0);
					}
				});

			}

			// 定位到最后一条
			if (isNeedLocateLast)
				pullToRefreshListView.getRefreshableView().setSelection(pullToRefreshListView.getRefreshableView().getAdapter().getCount() - 1);
			// 定位到回复的条
			if (replyMyReply != null) {
				for (int i = 0; i < listdata.size(); i++) {
					if (listdata.get(i) instanceof Msgs.PostbarTopicReplyDetail) {
						if (((Msgs.PostbarTopicReplyDetail) listdata.get(i)).getRid() == replyMyReply.getRid()) {
							final int position = i;
							pullToRefreshListView.getRefreshableView().postDelayed(new Runnable() {

								@Override
								public void run() {
									pullToRefreshListView.getRefreshableView().setSelection(position);
								}
							}, 100);
						}
					}
				}
				replyMyReply = null;
			}
			isNeedLocateLast = false;

		}

	}

	/**
	 * 加载数据
	 * 
	 * @param offsettype
	 * @param offset
	 * @param limit
	 */
	private void loadDataAddLast() {
		if (isfirstload)
			dialog.show();
		ProxyCallBack<PostbarTopicReplyListResult> callback = new ProxyCallBack<PostbarTopicReplyListResult>() {

			@Override
			public void onSuccess(final PostbarTopicReplyListResult result) {
				if (isfirstload) {
					dialog.dismiss();
					isfirstload = false;
				}
				pullToRefreshListView.onRefreshComplete();

				if (result != null) {
					// 刷新总数
					if (result.getTotalCount() != totalcount && filter == 0) {
						totalcount = result.getTotalCount();
						listAdapter.setReplyTotalCount(totalcount);
					}
					// levelCount = result.getLevelCount();
					if (filter == MsgsConstants.POSTBAR_REPLY_FILTER_POSTER_ONLY) {
						totalcount_landlord = result.getTotalCount();
					}

					List<PostbarTopicReplyDetail> list = result.getEntryList();

					if (list != null && list.size() > 0) {

						// 增加在后面
						for (int i = 0; i < list.size(); i++) {
							listdata.add(list.get(i));
						}
					}
					listAdapter.notifyDataSetChanged();
					// 定位到最后一条
					if (isNeedLocateLast)
						pullToRefreshListView.getRefreshableView().setSelection(
								pullToRefreshListView.getRefreshableView().getAdapter().getCount() - 1);
					isNeedLocateLast = false;
				}
				pullToRefreshListView.setMode(Mode.BOTH);
			}

			@Override
			public void onFailure(final Integer result, final String resultMsg) {

				if (isfirstload) {
					dialog.dismiss();
					isfirstload = false;
				}
				ErrorCodeUtil.handleErrorCode(TopicDetailFragment.this.getActivity(), result, resultMsg);
				isNeedLocateLast = false;
				pullToRefreshListView.onRefreshComplete();
				pullToRefreshListView.setMode(Mode.BOTH);
			}

		};
		long tmp_rid = 0;
		if (listdata.size() > 1 || listdata.get(listdata.size() - 1) instanceof Msgs.PostbarTopicReplyDetail) {
			tmp_rid = ((Msgs.PostbarTopicReplyDetail) listdata.get(listdata.size() - 1)).getRid();
		}

		ProxyFactory
				.getInstance()
				.getPostbarProxy()
				.getTopicReplyList(callback, TopicDetailFragment.this.getActivity(), topicId, MsgsConstants.OT_TOPIC, filter,
						MsgsConstants.POSTBAR_REPLAY_BY_OFFSET, tmp_rid, SystemConfig.PAGE_SIZE);

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
		// 弹出窗口
		if (parent.equals(pullToRefreshListView.getRefreshableView())) {
			int realposition = (int) parent.getAdapter().getItemId(position);
			// realposition ==-1 代表在头上长按
			if (realposition != -1) {
				if (realposition == 0 && listdata.get(realposition) instanceof Msgs.PostbarTopicDetail) {
				} else {
					if (mURLSpanClickable != null) {
						mURLSpanClickable.setIsClickUrlable(false);
					}
					Msgs.PostbarTopicReplyDetail detail = (Msgs.PostbarTopicReplyDetail) listdata.get(realposition);
					if (detail.getIsDel()) {
						ToastUtil.showToast(getActivity(),
								SystemContext.getInstance().getContext().getResources().getString(R.string.postbar_reply_isdel_content));
					} else {
						showActionDialog(detail);
					}
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 帖子详情上显示的操作窗口
	 * 
	 * @param detail
	 */
	private void showActionDialog(final Msgs.PostbarTopicDetail detail) {
		final Dialog dialog = new Dialog(this.getActivity(), R.style.SampleTheme_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);

		TextView title = (TextView) dialog.findViewById(R.id.title);
		title.setText(R.string.postbar_master_action_dialog_title);

		LinearLayout bottom = (LinearLayout) dialog.findViewById(R.id.bottom);
		bottom.setVisibility(View.GONE);

		LinearLayout content = (LinearLayout) dialog.findViewById(R.id.content);
		content.removeAllViews();

		LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.postbar_topic_action_list, null);
		ListView listView = (ListView) view.findViewById(R.id.listView);

		List<Map<String, String>> actionList = new ArrayList<Map<String, String>>();
		Map<String, String> nameMap = new HashMap<String, String>();
		nameMap.put("name", SystemContext.getInstance().getContext().getResources().getString(R.string.postbar_reply_action_dialog_copy));
		actionList.add(nameMap);

		SimpleAdapter adapter = new SimpleAdapter(this.getActivity(), actionList, R.layout.postbar_topic_action_list_item, new String[] { "name" },
				new int[] { R.id.postbar_topic_action_list_item_name });
		listView.setAdapter(adapter);

		content.addView(view);
		dialog.show();
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialog.dismiss();
				if (position == 0) // 复制
				{
					String tmpContent = "";
					if (detail.hasPostContent() && detail.getPostContent().getElementsCount() > 0) {
						List<Msgs.PostElement> list = detail.getPostContent().getElementsList();
						for (int i = 0; i < list.size(); i++) {
							Msgs.PostElement tmpPostElement = list.get(i);
							if (tmpPostElement.getType() == PostElementType.PE_TEXT) {
								if (tmpContent.isEmpty()) {
									tmpContent = tmpPostElement.getText();
								} else {
									tmpContent += "\r\n" + tmpPostElement.getText();
								}
							}
						}
					}
					ClipboardUtil.setClipboard(TopicDetailFragment.this.getActivity(), tmpContent);
				}
			}
		});

	}

	/**
	 * 回复上显示的操作窗口
	 * 
	 * @param detail
	 */
	private void showActionDialog(final Msgs.PostbarTopicReplyDetail detail) {

		boolean isFirstItem = true;
		LinearLayout contentView = (LinearLayout) View.inflate(this.getActivity(), R.layout.common_dialog_action_manage, null);
		final Dialog dialog = DialogUtil.showDialog(getActivity(), R.layout.dialog, SystemContext.getInstance().getContext().getResources()
				.getString(R.string.postbar_master_action_dialog_title), contentView, 0, 0, 0, 0, null);
		View item;
		TextView text;
		// 复制回复
		item = View.inflate(this.getActivity(), R.layout.common_dialog_action_manage_item, null);
		contentView.addView(item);
		if (isFirstItem) {
			item.findViewById(R.id.common_dialog_action_item_divider).setVisibility(View.GONE);
			isFirstItem = false;
		}
		text = (TextView) item.findViewById(R.id.common_dialog_action_item_text);
		text.setText(R.string.postbar_reply_action_dialog_copy);
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				// 判断楼层已经删除，不能够复制
				if (detail.getIsDel()) {
					ToastUtil.showToast(getActivity(), getString(R.string.postbar_reply_info_isdel_copy));
				} else {
					String tmpContent = "";
					if (detail.hasPostContent() && detail.getPostContent().getElementsCount() > 0) {

						List<Msgs.PostElement> list = detail.getPostContent().getElementsList();
						for (int i = 0; i < list.size(); i++) {
							Msgs.PostElement tmpPostElement = list.get(i);
							if (tmpPostElement.getType() == PostElementType.PE_TEXT) {
								if (tmpContent.isEmpty()) {
									tmpContent = tmpPostElement.getText();
								} else {
									tmpContent += "\r\n" + tmpPostElement.getText();
								}
							}
						}
					}
					ClipboardUtil.setClipboard(TopicDetailFragment.this.getActivity(), tmpContent);

				}

			}
		});
		// 回复回复
		item = View.inflate(this.getActivity(), R.layout.common_dialog_action_manage_item, null);
		contentView.addView(item);
		if (isFirstItem) {
			item.findViewById(R.id.common_dialog_action_item_divider).setVisibility(View.GONE);
			isFirstItem = false;
		}
		text = (TextView) item.findViewById(R.id.common_dialog_action_item_text);
		text.setText(R.string.postbar_reply_action_dialog_reply);
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				// 回复
				if (detail.getIsDel()) {
					ToastUtil.showToast(getActivity(), getString(R.string.postbar_reply_info_isdel_reply));
				} else {
					setReplyPreply(detail);
					String nickName = "";
					if (needReplyPReply instanceof Msgs.PostbarTopicReplyDetail) {
						nickName = ((PostbarTopicReplyDetail) needReplyPReply).getPosterNickname();
					} else {
						nickName = ((QuotesReplyDetail) needReplyPReply).getFromName();
					}
					postbar_topicreply_bottom_tips.setVisibility(View.VISIBLE);
					postbar_topicreply_bottom_tips_username.setText(nickName);
					sendMsgView.setEditTextHint(getString(R.string.postbar_reply_sendedit_hint, nickName));
				}

			}
		});
		// 删除回复
		if (isMaster) {
			item = View.inflate(this.getActivity(), R.layout.common_dialog_action_manage_item, null);
			contentView.addView(item);
			if (isFirstItem) {
				item.findViewById(R.id.common_dialog_action_item_divider).setVisibility(View.GONE);
				isFirstItem = false;
			}
			text = (TextView) item.findViewById(R.id.common_dialog_action_item_text);
			text.setText(R.string.postbar_reply_action_dialog_del);
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					if (detail.getIsDel()) {
						ToastUtil.showToast(getActivity(), getString(R.string.postbar_reply_info_isdel_del));
					} else {
						createInputActionReasonDialog(detail, "删除", "确认删除");
					}

				}
			});
		}
	}

	/**
	 * 创建输入操作帖子理由的输出框
	 * 
	 * @param detail
	 * @param sTitle
	 * @param sCommitBtn
	 */
	private void createInputActionReasonDialog(final Msgs.PostbarTopicReplyDetail detail, String sTitle, String sCommitBtn) {
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
				delTopicReply(detail, edit_group_card.getText().toString().trim());
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
	 * 删除帖子回复
	 * 
	 * @param detail
	 */
	private void delTopicReply(final Msgs.PostbarTopicReplyDetail detail, String actionReason) {

		ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {
			@Override
			public void onSuccess(Integer result) {
				if (result == Msgs.ErrorCode.EC_OK_VALUE) {
					Msgs.PostbarTopicReplyDetail tmp = detail.toBuilder().setIsDel(true).build();
					Collections.replaceAll(listdata, detail, tmp);
					listAdapter.notifyDataSetChanged();
				} else {
					ErrorCodeUtil.handleErrorCode(TopicDetailFragment.this.getActivity(), result, null);
				}

			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				ErrorCodeUtil.handleErrorCode(TopicDetailFragment.this.getActivity(), result, resultMsg);
			}
		};
		ProxyFactory.getInstance().getPostbarProxy()
				.actionTopicReply(callback, this.getActivity(), detail.getRid(), MsgsConstants.OP_DEL_TOPIC, actionReason);
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
		if (parent.equals(pullToRefreshListView.getRefreshableView())) {
			Utils.hideSoftInput(getActivity(), sendMsgView.getSendMsgEditText());
		}
	}

	private String photoPath;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			byte[] photoByte = null;
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:
				// 先缩放
				try {
					Bitmap bitmap = ImageUtil.createImageThumbnail(photoPath, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					photoByte = ImageUtil.Bitmap2Bytes(bitmap, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
					if (bitmap != null && !bitmap.isRecycled()) {
						// 回收并且置为null
						bitmap.recycle();
						bitmap = null;
						// System.gc();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
				break;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:
				ContentResolver resolver = this.getActivity().getContentResolver();
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				try {
					Bitmap photo = ImageUtil.createImageThumbnail(resolver, originalUri, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					if (photo != null) {
						photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
						if (photo != null && !photo.isRecycled()) {
							// 回收并且置为null
							photo.recycle();
							photo = null;
							// System.gc();
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}

			case PhotoUtil.CROP_IMAGE_WITH_DATA:

				break;
			}
			if (photoByte != null) {
				addImageView(photoByte, photoPath);
			} else {
				ToastUtil.showToast(this.getActivity(),
						SystemContext.getInstance().getContext().getResources().getString(R.string.common_add_photo_error));
			}
		} else if (resultCode == ImageGridActivity.RESULTCODE) {
			cache = BitmapCache.getInstance();
			sendMsgView.sendmsg_ImageBrower.removeAllViews();
			clearImages();
			images = new ArrayList<ImageVo>();
			sendMsgView.hideSendPicCount();
			sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
			sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.GONE);
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("还没有上传图片哦");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
			List<String> resImageMap = (List<String>) data.getExtras().get(ImageGridActivity.IMAGE_MAP);
			int size = resImageMap.size();
			for (int i = 0; i < size; i++) {
				String path = resImageMap.get(i);
				addCacheImageView(path, path);
			}
		}

	}

	/**
	 * 
	 * @param btm
	 */
	private void addCacheImageView(final String imagePath, String thumbnailPath) {
		// 增加底部显示发送的图片
		final LinearLayout postbar_topicreply_replyimage_grid_item = (LinearLayout) View.inflate(getActivity(),
				R.layout.postbar_topicreply_replyimage_grid_item, null);
		sendMsgView.sendmsg_ImageBrower.addView(postbar_topicreply_replyimage_grid_item);
		final ImageView postbar_topicreply_replyimage_grid_item_image = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_image);
		final ImageView postbar_topicreply_replyimage_grid_item_del = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_del);
		final ImageVo imageVo = new ImageVo();
		cache.dispBitmap(postbar_topicreply_replyimage_grid_item_image, imagePath, thumbnailPath, new ImageCallBack() {

			@Override
			public void doImageLoad(ImageView iv, Bitmap bitmap, Object... params) {
				if (iv != null && bitmap != null) {
					iv.setImageBitmap(bitmap);
					try {
						Bitmap bp = ImageUtil.createImageThumbnail(imagePath, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
						final byte[] tphotoByte = ImageUtil.Bitmap2Bytes(bp, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
						if (bp != null && !bp.isRecycled()) {
							// 回收并且置为null
							bp.recycle();
							bp = null;
						}
						imageVo.setPath(imagePath);
						imageVo.setData(tphotoByte);
						images.add(imageVo);
						sendMsgView.showSendPicCount(images.size());
						sendMsgView.sendmsg_ImageBrower.setVisibility(View.VISIBLE);
						sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.VISIBLE);
						if (images.size() == BitmapBucket.max) {
							sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + BitmapBucket.max + "张图片！");
							sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color2));
						} else {
							sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size()) + "张哦!");
							sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
						}
						postbar_topicreply_replyimage_grid_item_del.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								images.remove(imageVo);
								sendMsgView.sendmsg_ImageBrower.removeView(postbar_topicreply_replyimage_grid_item);
								sendMsgView.showSendPicCount(images.size());
								sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size())
										+ "张哦!");
								sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
								if (images.size() == 0) {
									sendMsgView.hideSendPicCount();
									sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
									sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.GONE);
									sendMsgView.sendmsg_imageBrower_tipTxt.setText("还没有上传图片哦");
								}
							}
						});
						postbar_topicreply_replyimage_grid_item_image.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								jumpImageBrowerAct(imagePath);
							}
						});
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
					}
					postbar_topicreply_replyimage_grid_item_del.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							images.remove(imageVo);
							sendMsgView.sendmsg_ImageBrower.removeView(postbar_topicreply_replyimage_grid_item);
							sendMsgView.showSendPicCount(images.size());
							if (images.size() == 0) {
								sendMsgView.hideSendPicCount();
								sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
								sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.GONE);
								sendMsgView.sendmsg_imageBrower_tipTxt.setText("还没有上传图片哦");
							}
							sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size())
									+ "张哦!");
						}
					});
					postbar_topicreply_replyimage_grid_item_image.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							jumpImageBrowerAct(imagePath);
						}
					});
				} else {
					sendMsgView.sendmsg_ImageBrower.removeView(postbar_topicreply_replyimage_grid_item);
					Log.e(TAG, "你写了些什么！？bitmap怎么是null");
				}

			}
		});
		// 隐藏panel
		// sendMsgView.hidePanelAndSoftInput();
		// sendMsgView.showSendPicCount(1);
	}

	/**
	 * 
	 * @param btm
	 */
	private void addImageView(final byte[] photoByte, final String path) {
		if (images == null) {
			images = new ArrayList<ImageVo>();
		}
		if (images.size() == 0) {
			sendMsgView.sendmsg_ImageBrower.removeAllViews();
		}
		getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
		final ImageVo imageVo = new ImageVo(path, photoByte);
		images.add(imageVo);
		Bitmap btm = ImageUtil.Bytes2Bimap(photoByte);
		sendMsgView.showSendPicCount(images.size());
		// 增加底部显示发送的图片
		final LinearLayout postbar_topicreply_replyimage_grid_item = (LinearLayout) View.inflate(this.getActivity(),
				R.layout.postbar_topicreply_replyimage_grid_item, null);
		sendMsgView.sendmsg_ImageBrower.setVisibility(View.VISIBLE);
		sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.VISIBLE);
		if (images.size() == BitmapBucket.max) {
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + BitmapBucket.max + "张图片！");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color2));
		} else {
			sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size()) + "张哦!");
			sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
		}
		sendMsgView.sendmsg_ImageBrower.addView(postbar_topicreply_replyimage_grid_item);
		final ImageView postbar_topicreply_replyimage_grid_item_image = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_image);
		postbar_topicreply_replyimage_grid_item_image.setImageBitmap(btm);
		postbar_topicreply_replyimage_grid_item_image.setTag(new String(path));
		ImageView postbar_topicreply_replyimage_grid_item_del = (ImageView) postbar_topicreply_replyimage_grid_item
				.findViewById(R.id.postbar_topicreply_replyimage_grid_item_del);
		postbar_topicreply_replyimage_grid_item_del.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				images.remove(imageVo);
				sendMsgView.sendmsg_ImageBrower.removeView(postbar_topicreply_replyimage_grid_item);
				sendMsgView.showSendPicCount(images.size());
				if (images.size() == BitmapBucket.max) {
					sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + BitmapBucket.max + "张图片！");
					sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color2));
				} else {
					sendMsgView.sendmsg_imageBrower_tipTxt.setText("已经上传" + images.size() + "张, 还能选择" + (BitmapBucket.max - images.size()) + "张哦!");
					sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
				}
				if (images.size() == 0)
					sendMsgView.hideSendPicCount();
			}
		});
		postbar_topicreply_replyimage_grid_item_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jumpImageBrowerAct(String.valueOf(postbar_topicreply_replyimage_grid_item_image.getTag()));
			}
		});
		sendMsgView.sendmsg_ImageBrower.setVisibility(View.VISIBLE);
		sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.VISIBLE);
		// 隐藏panel
		// sendMsgView.hidePanelAndSoftInput();
		// sendMsgView.showSendPicCount(1);
	}

	/**
	 * 
	 * @param path
	 */
	private void jumpImageBrowerAct(String path) {
		if (images != null && images.size() > 0) {
			List<String> pathList = new ArrayList<String>();
			int index = 0;
			for (int i = 0; i < images.size(); i++) {
				String tpath = images.get(i).getPath();
				pathList.add(tpath);
				if (path.equals(tpath)) {
					index = i;
				}
			}
			Intent intent = new Intent(getActivity(), ImageBrowerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGE_INDEX, index);
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_MODE, 1);
			bundle.putStringArray(SystemConfig.BUNDLE_NAME_IMAGEBROWER_IMAGES, pathList.toArray(new String[pathList.size()]));
			intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
			getActivity().startActivity(intent);
		}
	}

	/**
	 * 设置数据的过滤类型
	 * 
	 * @param filter
	 */
	public void setFilter(int filter) {
		if (this.filter != filter) {
			this.filter = filter;
			// 清空数据
			listdata.clear();
			listdata.add(topicDetail);
			listAdapter.notifyDataSetChanged();
			isfirstload = true;
			loadDataByPage(1, SystemConfig.PAGE_SIZE, responseDataloadType_clear);
		}
	}

	/**
	 * 选择翻页
	 */
	public void selectPage() {
		if (listdata.size() == 1) {
			ToastUtil.showToast(this.getActivity(), "当前只有楼主，不需要翻页");
			return;
		}
		if (getPageTotal(levelCount, SystemConfig.PAGE_SIZE) == 1 && filter == 0) {
			// 全部
			ToastUtil.showToast(this.getActivity(), "当前只有1页数据，无需翻页");
			return;
		}

		if (getPageTotal(totalcount_landlord, SystemConfig.PAGE_SIZE) == 1 && filter == MsgsConstants.POSTBAR_REPLY_FILTER_POSTER_ONLY) {
			// 全部
			ToastUtil.showToast(this.getActivity(), "当前只有1页数据，无需翻页");
			return;
		}

		int firstVisiblePosition = pullToRefreshListView.getRefreshableView().getFirstVisiblePosition() - 1;

		// 当前处于界面上的第几页
		int tmp = 1;
		if (firstVisiblePosition != 0) {
			tmp = (firstVisiblePosition + 1 - 1) % SystemConfig.PAGE_SIZE == 0 ? (firstVisiblePosition + 1 - 1) / SystemConfig.PAGE_SIZE
					: (firstVisiblePosition + 1 - 1) / SystemConfig.PAGE_SIZE + 1;
		}

		// 计算当前页的页面
		final int currentPage = currentFirstPage + tmp - 1;

		// 弹出翻页操作窗口
		View popview = this.inflater.inflate(R.layout.postbar_topicreply_pagenum, null, true);
		final PopupWindow popWindow = new PopupWindow(popview, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		popWindow.setBackgroundDrawable(new ColorDrawable(0x7D000000));
		popWindow.setOutsideTouchable(true);
		popWindow.setTouchable(true);
		popWindow.setFocusable(true);
		popWindow.showAsDropDown(this.getView().findViewById(R.id.top), 0, 0);

		final PageViewHolder pageViewHolder = new PageViewHolder();
		pageViewHolder.postbar_topicreply_pages_gallery = (Gallery) popview.findViewById(R.id.postbar_topicreply_pages_gallery);
		pageViewHolder.postbar_topicreply_pages_current_page = (TextView) popview.findViewById(R.id.postbar_topicreply_pages_current_page);
		pageViewHolder.postbar_topicreply_current_pagenum = (TextView) popview.findViewById(R.id.postbar_topicreply_current_pagenum);
		pageViewHolder.cannel = (TextView) popview.findViewById(R.id.cannel);
		pageViewHolder.ok = (TextView) popview.findViewById(R.id.ok);

		final int pagetotal = getPageTotal(filter == MsgsConstants.POSTBAR_REPLY_FILTER_POSTER_ONLY ? totalcount_landlord : levelCount,
				SystemConfig.PAGE_SIZE);
		PageAdapter pageAdapter = new PageAdapter(this.getActivity(), pagetotal);
		pageViewHolder.postbar_topicreply_pages_gallery.setAdapter(pageAdapter);
		// 设置当前页

		pageViewHolder.postbar_topicreply_pages_gallery.setSelection(currentPage - 1);

		pageViewHolder.ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popWindow.dismiss();
				int selectPage = Integer.parseInt(pageViewHolder.postbar_topicreply_pages_current_page.getText().toString());
				if (selectPage != currentPage) {
					isfirstload = true;
					loadDataByPage(selectPage, SystemConfig.PAGE_SIZE, responseDataloadType_clear);
				}

			}
		});

		pageViewHolder.cannel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popWindow.dismiss();
			}
		});
		pageViewHolder.postbar_topicreply_pages_gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			// 当Gallery选中项发生改变时触发该方法
			@Override
			public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int position, long id) {
				pageViewHolder.postbar_topicreply_pages_current_page.setText((position + 1) + "");
				pageViewHolder.postbar_topicreply_current_pagenum.setText(String.format(SystemContext.getInstance().getContext().getResources()
						.getString(R.string.postbar_topic_detail_pagenum_currentpage), position + 1, pagetotal));
			}

			@Override
			public void onNothingSelected(AdapterView<?> paramAdapterView) {
			}
		});

	}

	private int getPageTotal(int total, int pageSize) {
		return total % pageSize == 0 ? (total / pageSize) : (total / pageSize) + 1;
	}

	public void reportTopic() {
		if (topicDetail == null) {
			ToastUtil.showToast(this.getActivity(), "数据还未加载完成，无法举报");

		} else if (topicDetail.getPosterUid() == SystemContext.getInstance().getExtUserVo().getUserid()) {
			ToastUtil.showToast(this.getActivity(), "自己的帖子不能举报哦！");
		} else {
			Intent intent = new Intent(this.getActivity(), ReportActivity.class);
			Bundle bundle = new Bundle();
			bundle.putLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TID, topicId);
			bundle.putInt(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_TTYPE, MsgsConstants.OT_TOPIC);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	/**
	 * 返回下面发送的组件对象
	 * 
	 * @return
	 */
	public SendMsgView getSendMsgView() {
		return sendMsgView;
	}

	/**
	 * 
	 * @ClassName: PageViewHolder
	 * @Description: TODO(翻页控件的viewHoder)
	 * @author chuanglong
	 * @date 2014-4-28 下午2:50:01
	 * @Version 1.0
	 * 
	 */
	class PageViewHolder {
		Gallery postbar_topicreply_pages_gallery;
		TextView postbar_topicreply_pages_current_page;
		TextView postbar_topicreply_current_pagenum;
		TextView cannel;
		TextView ok;
	}

	/**
	 * 是否返回
	 */
	public boolean isBack() {
		if ((sendMsgView.getSendMsgEditText().getText().toString() == null || sendMsgView.getSendMsgEditText().getText().toString().trim().equals(""))
				&& (images == null || images.size() == 0)) {
			if (getActivity() != null) {
				getActivity().finish();
				System.gc();
			}
			return true;
		}

		final TextView txt = new TextView(getActivity());
		txt.setPadding(0, SystemContext.getInstance().getContext().getResources().getDimensionPixelSize(R.dimen.global_page_paddingtop),
				SystemContext.getInstance().getContext().getResources().getDimensionPixelSize(R.dimen.global_page_paddingright), SystemContext
						.getInstance().getContext().getResources().getDimensionPixelSize(R.dimen.global_page_paddingbottom));

		txt.setTextColor(SystemContext.getInstance().getContext().getResources().getColor(R.color.dialog_content_text_color));

		txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, SystemContext.getInstance().getContext().getResources().getDimension(R.dimen.text_medium));
		txt.setText("确定要返回吗？");

		OKCallBackListener listener = new OKCallBackListener() {

			@Override
			public void execute() {
				if (getActivity() != null) {
					getActivity().finish();
					System.gc();
				}
			}

			@Override
			public void cannel() {
			}

		};
		DialogUtil.showDialog(getActivity(), "提示", txt, listener);
		return false;

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
		if (listdata.size() > 0) {
			if (currentFirstPage > 1) {
				loadDataByPage(currentFirstPage - 1, SystemConfig.PAGE_SIZE, responseDataloadType_before);
			} else {
				loadDataByPage(1, SystemConfig.PAGE_SIZE, responseDataloadType_clear);
			}
			getTopicDetail();
		} else {
			pullToRefreshListView.postDelayed(new Runnable() {

				@Override
				public void run() {
					pullToRefreshListView.onRefreshComplete();
				}
			}, 1000);
		}
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
		dialog.dismiss();
		if (listdata.size() >= 1) {
			if (listdata.size() % SystemConfig.PAGE_SIZE == 0) {
				// 代表前面的是整页
				loadDataByPage(currentFirstPage + (listdata.size() / SystemConfig.PAGE_SIZE) - 1 + 1, SystemConfig.PAGE_SIZE,
						responseDataloadType_after);
			} else {
				// 按照滑动来加载数据
				loadDataAddLast();
			}
		} else {
			pullToRefreshListView.postDelayed(new Runnable() {

				@Override
				public void run() {
					pullToRefreshListView.onRefreshComplete();
				}
			}, 1000);
		}

	}

	private void setReplyPreply(Object detail) {
		if (detail == null) {
			listReplyPosition = listdata.size() - 1;
			sendMsgView.sendmsg_addattachments.setEnabled(true);
			sendMsgView.sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_selector);
		} else {
			String nickName = "";
			if (detail instanceof Msgs.PostbarTopicReplyDetail) {
				nickName = ((PostbarTopicReplyDetail) detail).getPosterNickname();
			} else {
				nickName = ((QuotesReplyDetail) detail).getFromName();
			}
			sendMsgView.sendmsg_addattachments.setEnabled(false);
			sendMsgView.sendmsg_addattachments.setBackgroundResource(R.drawable.chat_msg_addattachments_pre2);
			postbar_topicreply_bottom_tips.setVisibility(View.VISIBLE);
			postbar_topicreply_bottom_tips_username.setText(nickName);
			sendMsgView.setEditTextHint(getString(R.string.postbar_reply_sendedit_hint, nickName));
			sendMsgView.setEditTextFocus2();
			bottomView.setVisibility(View.VISIBLE);
			bottomContentView.setVisibility(View.GONE);
		}
		sendMsgView.sendmsg_ImageBrower.removeAllViews();
		sendMsgView.hideSendPicCount();
		sendMsgView.sendmsg_ImageBrower.setVisibility(View.GONE);
		sendMsgView.sendmsg_imageBrower_tipTxt.setVisibility(View.GONE);
		sendMsgView.sendmsg_imageBrower_tipTxt.setText("还没有上传图片哦");
		sendMsgView.sendmsg_imageBrower_tipTxt.setTextColor(getResources().getColor(R.color.message_send_add_image_txt_color));
		needReplyPReply = detail;
		clearImages();
		images = new ArrayList<ImageVo>();
	}

	private void clearImages() {
		if (images != null) {
			images.clear();
			images = null;
		}
	}

}
