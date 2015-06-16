/**      
 * GroupChatFragment.java Create on 2013-10-25     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.chat.ui;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iwgame.audio.AudioPlayer;
import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.ImageSendBrowerActivity;
import com.iwgame.msgs.common.LocationCallBack;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.MyTagSpan;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.SendMsgCallBack;
import com.iwgame.msgs.common.StrictModeWrapper;
import com.iwgame.msgs.common.imageloader.ImageLoader;
import com.iwgame.msgs.common.imageloader.PauseOnScrollListener;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.config.UMConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.ui.register.BundPhoneActivity;
import com.iwgame.msgs.module.chat.adapter.ChatMessageAdapter;
import com.iwgame.msgs.module.chat.adapter.SplendidMessageAdapter;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.MyTagUtil;
import com.iwgame.msgs.utils.PhotoUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.utils.SessionUtil;
import com.iwgame.msgs.utils.UMUtil;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.widget.MonitorSizeChangeLinearLayout;
import com.iwgame.msgs.widget.MonitorSizeChangeLinearLayout.SizeChangedListener;
import com.iwgame.msgs.widget.SendMsgView;
import com.iwgame.msgs.widget.SendMsgView.ActionSmileyButtonListener;
import com.iwgame.msgs.widget.SendMsgView.AudioRecorderStatusListener;
import com.iwgame.msgs.widget.URLSpanClickable;
import com.iwgame.sdk.xaction.XActionCommandClient;
import com.iwgame.utils.AudioUtils;
import com.iwgame.utils.ClipboardUtil;
import com.iwgame.utils.FileUtils;
import com.iwgame.utils.HttpUtil;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.ToastUtil;
import com.youban.msgs.R;

/**
 * @ClassName: GroupChatFragment
 * @Description: 聊天的内容fragment
 * @author 王卫
 * @date 2013-10-25 下午5:48:46
 * @Version 2.0
 * 
 */
public class ChatFragment extends BaseFragment implements OnItemClickListener, OnItemLongClickListener, OnRefreshListener<ListView> {

	private static final String TAG = "ChatFragment";
	LayoutInflater inflater = null;
	String toDomain;
	long toId;
	String category = MsgsConstants.MCC_CHAT;
	String channelType = "";
	// 是否允许发消息，true允许，false 不允许
	boolean isSendMsg = false;
	// 是否是发送数据中，解决发图片回来onResume执行一次，发送成功又加一次的问题
	boolean isSending = false;

	private List<MessageVo> chatMessageData = new ArrayList<MessageVo>();
	private List<Map<String, Object>> SplendidmessageData = new ArrayList<Map<String, Object>>();

	private ChatMessageAdapter chatMessageAdapter;
	private SplendidMessageAdapter splendidMessageAdapter;

	ChatMsgReceiver chatMsgReceiver;
	// 发送组件
	SendMsgView chat_content_bottom;

	Intent registerReceiverIntent = null;

	/**
	 * 是否是会长群发消息页面
	 */
	boolean isGroupManageSend = false;

	/**
	 * 定义父页面List所在的页面类型
	 */
	int pageType = 1;

	/**
	 * 群发消息的公共id
	 */
	long groupManageToGroupId = 0;

	private Handler handler = new Handler();

	public SendMsgView getSendMsgView() {
		return chat_content_bottom;
	}

	/**
	 * 中间内容的view
	 */
	MonitorSizeChangeLinearLayout centerContentView;

	/**
	 * 中间内容的view
	 * 
	 * @return the centerContentView
	 */
	public LinearLayout getCenterContentView() {
		return centerContentView;
	}

	PullToRefreshListView pullToRefreshListView;

	private URLSpanClickable mURLSpanClickable;

	/**
	 * 是否在加载数据的过程中，默认false不在；true在加载数据中
	 */
	boolean isLoadDataing = false;

	Queue<MessageVo> queue = new ConcurrentLinkedQueue<MessageVo>();

	// 定义该页面是否可见 ,false不可见；true ，可以见
	boolean isUIVISIBLE = false;

	private long uid = SystemContext.getInstance().getExtUserVo().getUserid();
	//删除本地历史消息
	private boolean deletedMsg = false;

	private class ChatMsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SystemConfig.ACTION_RECEIVEMESSAGE_BROADCAST)) {
				Bundle bundle = intent.getExtras();
				// 反序列化，在本地重建数据
				Serializable data = bundle.getSerializable(SystemConfig.BUNDLE_NAME_MESSAGEVO);
				if (data != null) {
					MessageVo vo = (MessageVo) data;
					LogUtil.d(TAG, "----收到消息数据:" + vo.toString());
					// 判断是否是自己的消息
					if (SystemContext.APPTYPE.equals(vo.getCurrnetApptype())
							&& (uid != vo.getFromId() || (uid == vo.getFromId() && !SystemContext.APPTYPE.equals(vo.getApptype())))
							&& toId == vo.getSubjectId() && toDomain.equals(vo.getSubjectDomain()) && channelType.equals(vo.getChannelType())
							&& category.equals(vo.getCategory())) {
						queue.offer(vo);
						bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISCHATACTIVITY_RECEIVE, true);
					}
				}
				LogUtil.d("消息处理", "消息的广播ChatMsgReceiver；isChatActivity=" + bundle.getBoolean(SystemConfig.BUNDLE_NAME_ISCHATACTIVITY_RECEIVE));
				setResultExtras(bundle);
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictModeWrapper.init(getActivity());
		super.onCreate(savedInstanceState);
		PTAG = TAG;
		// 获取一次位置信息
		ServiceFactory.getInstance().getBaiduLocationService().requestLocation(new LocationCallBack() {

			@Override
			public void onBack(BDLocation bdLocation) {
				ProxyFactory.getInstance().getUserProxy().setPosition(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
					}
				}, SystemContext.getInstance().getContext(), SystemContext.getInstance().getLocation());
			}
		});
	}

	@Override
	public void onDestroy() {
		if (!isGroupManageSend) {
			if (chatMsgReceiver != null && registerReceiverIntent != null) {

				getActivity().unregisterReceiver(chatMsgReceiver);
				registerReceiverIntent = null;
			}
		}

		super.onDestroy();

	}

	@Override
	public void onPause() {
		super.onPause();

		// 退出播放的声音
		if (chatMessageAdapter != null)
			chatMessageAdapter.stopPlayingAudio();
		if (chat_content_bottom.isAudioRecorder())
			chat_content_bottom.actionUp(true);
		AudioPlayer.getInstance().stopPlaying(getActivity());

	}

	@Override
	public void onResume() {
		super.onResume();
		isUIVISIBLE = true;

		if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
			getMessageVoData(chatMessageData);
			splendidMessageAdapter.notifyDataSetChanged();
		} else if (pageType == ChatMessageAdapter.PAGE_TYPE_SYSTEM) {
			// 游伴小助手, 判断是否有手机绑定消息，有的话判断绑定状态，修改文本显示
			int guest = SystemContext.getInstance().getIsGuest();
			if (guest != 1) {
				if (chatMessageData != null) {
					for (int i = 0; i < chatMessageData.size(); i++) {
						MessageVo vo = chatMessageData.get(i);
						// 判断是否访客账号，访客账号设置为不可点和修改提示文字
						if (vo != null && vo.getContentType() == MessageContentType.TEXT_VALUE && vo.getChannelType().equals(MsgsConstants.MC_NOTIFY)
								&& vo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)
								&& vo.getContent().contains(MyTagUtil.TAG_FORMAT_PREFIX_PREFIX + MsgsConstants.MESSAGE_OP_GUEST_BIND_CPN)) {
							// 更新消息
							String content = vo.getContent();
							content = MyTagUtil.replaceAction(content, MsgsConstants.MESSAGE_OP_GUEST_BIND_CPN,
									getActivity().getString(R.string.message_action_bundphone_update));
							vo.setContent(content);
							ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), content);
							chatMessageAdapter.notifyDataSetChanged();
							return;
						}
					}
				}
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		isUIVISIBLE = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.inflater = inflater;
		// 获得传入的用户参数
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			isSendMsg = tmpbundle.getBoolean(SystemConfig.BUNDLE_NAME_ISSENDMSG);
			toId = tmpbundle.getLong(SystemConfig.BUNDLE_NAME_TOID);
			toDomain = tmpbundle.getString(SystemConfig.BUNDLE_NAME_TODOMAIN);
			category = tmpbundle.getString(SystemConfig.BUNDLE_NAME_CATEGORY);
			channelType = tmpbundle.getString(SystemConfig.BUNDLE_NAME_CHANNELTYPE);
			groupManageToGroupId = tmpbundle.getLong(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_GRID);

			pageType = tmpbundle.getInt(SystemConfig.BUNDLE_NAME_PAGETYPE);
			if (pageType == ChatMessageAdapter.PAGE_TYPE_GROUPMANAGEMASSSEND) {
				isGroupManageSend = true;
			}
		}

		View v = inflater.inflate(R.layout.chat_content, container, false);
		init(v);
		if (!isGroupManageSend) {
			if (registerReceiverIntent == null) {
				// 注册receiver
				chatMsgReceiver = new ChatMsgReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(SystemConfig.ACTION_RECEIVEMESSAGE_BROADCAST);
				filter.setPriority(getResources().getInteger(R.integer.receiver_priority_messagebroadcast_chatactivity));
				registerReceiverIntent = getActivity().registerReceiver(chatMsgReceiver, filter);

			}
			// 开始加载数据
			loadData(Integer.MAX_VALUE, -SystemConfig.PAGE_SIZE, true, false);
			// 处理未读数
			long lastUnReadMaxIndex = SystemContext.getInstance().getSubjectLastUnReadMaxIndex(channelType, toId, toDomain, category);
			long unReadMaxIndex = SystemContext.getInstance().getSubjectUnReadMaxIndex(channelType, toId, toDomain, category);
			if (lastUnReadMaxIndex != -1 && unReadMaxIndex != 0 && unReadMaxIndex > lastUnReadMaxIndex) {
				// 更新已经读到的数值
				SystemContext.getInstance().setSubjectLastUnReadMaxIndex(channelType, toId, toDomain, category, unReadMaxIndex);
				// 取消状态栏上的通知
				NotificationManager nm = (NotificationManager) getActivity().getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				nm.cancel(SystemConfig.NOTIFICATION_ID_BASE);
			}
		}
		return v;
	}

	private void init(View v) {
		// 设置中间内容
		centerContentView = (MonitorSizeChangeLinearLayout) v.findViewById(R.id.contentView);

		View view = (LinearLayout) inflater.inflate(R.layout.chat_content_center, centerContentView, false);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		centerContentView.addView(view, params);
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.chat_msg_listview);
		if(isGroupManageSend)
			pullToRefreshListView.setMode(Mode.DISABLED);
		else
			pullToRefreshListView.setMode(Mode.PULL_FROM_START);
		pullToRefreshListView.setOnRefreshListener(this);

		// chat_msg_listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		mURLSpanClickable = new URLSpanClickable();

		if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {// 精彩推荐
			// 添加精彩推荐适配器
			splendidMessageAdapter = new SplendidMessageAdapter(view.getContext(), SplendidmessageData);
			pullToRefreshListView.setAdapter(splendidMessageAdapter);
		} else {
			chatMessageAdapter = new ChatMessageAdapter(view.getContext(), chatMessageData, mURLSpanClickable, pageType);
			pullToRefreshListView.setAdapter(chatMessageAdapter);
			chatMessageAdapter.setOnItemLongClickListener(this);
			pullToRefreshListView.setOnItemClickListener(this);
		}

		// registerForContextMenu(chat_msg_listview);
		if (!isGroupManageSend) {
			pullToRefreshListView.getRefreshableView().setOnItemLongClickListener(this);
		}

		centerContentView.setSizeChangedListener(new SizeChangedListener() {

			@Override
			public void onSizeChanged() {
				handler.post(new Runnable() {

					@Override
					public void run() {
						if (pullToRefreshListView.getRefreshableView().getAdapter().getCount() > 0) {
							pullToRefreshListView.getRefreshableView().setSelection(
									pullToRefreshListView.getRefreshableView().getAdapter().getCount() - 1);
						}
					}
				});

			}
		});
		pullToRefreshListView.getRefreshableView().setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
				chat_content_bottom.hidePanelAndSoftInput();
				// 设置可以滚动
				if (getActivity() instanceof GroupChatFragmentActivity) {
					((GroupChatFragmentActivity) ChatFragment.this.getActivity()).setViewPagerScrollable(true);
				}
				return false;
			}
		});

		// 设置滚动的时候图片是否加载
		pullToRefreshListView.getRefreshableView().setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));

		// 设置底部
		LinearLayout bottomView = (LinearLayout) v.findViewById(R.id.bottomView);
		chat_content_bottom = (SendMsgView) View.inflate(getActivity(), R.layout.public_send_msg_view, null);
		//chat_content_bottom = (SendMsgView) inflater.inflate(R.layout.public_send_msg_view, bottomView, false);
		bottomView.addView(chat_content_bottom);

		SendMsgCallBack callback = new SendMsgCallBack() {

			@Override
			public void send(int msgtype, String content, byte[] contentBytes, int action) {
				if (msgtype == MessageContentType.TEXT_VALUE) {
					if (!content.isEmpty() && !content.trim().isEmpty())
						sendMsg(msgtype, content, null);
					else
						ToastUtil.showToast(getActivity(), "发送内容不可以为空");
				} else if (msgtype == MessageContentType.IMAGE_VALUE && action == SendMsgCallBack.ACTION_PHOTO) {
					// 相机
					PhotoUtil.doTakePhoto(ChatFragment.this);
				} else if (msgtype == MessageContentType.IMAGE_VALUE && action == SendMsgCallBack.ACTION_PICTURE) {
					// 相册
					PhotoUtil.doPickPhotoFromGallery(ChatFragment.this);
				} else if (msgtype == MessageContentType.VOICE_VALUE && action == SendMsgCallBack.ACTION_MICROPHONE) {
					// 声音
					sendMsg(msgtype, null, contentBytes);
				}
			}

			@Override
			public void setAudioRecorderStatus(int status) {
				// 录音中停止在播放的声音
				if (status == SendMsgCallBack.AUDIORECORDER_ING) {
					chatMessageAdapter.stopPlayingAudio();
				}
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
						if (pullToRefreshListView != null)
							pullToRefreshListView.getRefreshableView().setSelection(
									pullToRefreshListView.getRefreshableView().getAdapter().getCount() - 1);
					}

				}, 700);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.iwgame.msgs.common.SendMsgCallBack#creatBundPhomeDialog()
			 */
			@Override
			public void createDialog() {
				createBundPhoneDialog();
			}

		};
		chat_content_bottom.setSendMsgCallBack(callback);
		if (isSendMsg) {
			chat_content_bottom.setVisibility(View.VISIBLE);
		} else {
			chat_content_bottom.setVisibility(View.GONE);
		}

		if (this.getActivity() instanceof GroupChatFragmentActivity) {
			chat_content_bottom.setActionSmileyButtonListener(new ActionSmileyButtonListener() {

				@Override
				public void setSmileyViewPagerHeight(int height) {
					((GroupChatFragmentActivity) ChatFragment.this.getActivity()).setChildViewPagerHeight(height);
				}
			});

			chat_content_bottom.setAudioRecorderStatusListener(new AudioRecorderStatusListener() {

				@Override
				public void setViewPagerScrollable(boolean able) {
					if (getActivity() != null) {
						((GroupChatFragmentActivity) getActivity()).setViewPagerScrollable(able);
					}
				}

			});

		}

		// 启动线程监控接受到的消息
		new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				while (true) {
					if (!isLoadDataing && !queue.isEmpty() && isUIVISIBLE) {
						// 处理队列中的消息 出队列中的数据
						final MessageVo vo = queue.poll();
						if (getActivity() != null) {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									/**
									 * 增加保护，由于有时返回的时候loaddata加载时db已经插入，可以得到，
									 * 而前面广播的又可以接受到，所以用了个保护
									 */
									boolean isadd = true;
									for (int i = 0; i < 4 && i < chatMessageData.size(); i++) {
										if (vo.getMsgId() == chatMessageData.get(chatMessageData.size() - 1 - i).getMsgId()) {
											isadd = false;
											break;
										}
									}
									if (isadd && vo.getStatus() != MessageVo.STATUS_DEL) {
										chatMessageData.add(vo);
										if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
											getMessageVoData(chatMessageData);
											splendidMessageAdapter.notifyDataSetChanged();
										} else {
											chatMessageAdapter.notifyDataSetChanged();
											int sp = pullToRefreshListView.getRefreshableView().getLastVisiblePosition();
											int count = pullToRefreshListView.getRefreshableView().getAdapter().getCount();
											if (sp >= count - 5) {
												pullToRefreshListView.getRefreshableView().setSelection(
														pullToRefreshListView.getRefreshableView().getAdapter().getCount() - 1);
											}
										}
										// 更新已经读到的数据
										SystemContext.getInstance().setSubjectLastUnReadMaxIndex(channelType, toId, toDomain, category,
												vo.getMsgIndex());
									}
								}
							});
						}
						try {
							// 休眠10 ms,处理下一条
							sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} else {
						// 休眠100 ms
						try {
							sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}.start();

	}

	/**
	 * content和contentBytes只有一个起效果，根据msgtype来判断
	 * 
	 * @param msgtype
	 * @param content
	 * @param contentBytes
	 */
	private void sendMsg(int msgtype, String content, byte[] contentBytes) {
		if (SessionUtil.getLongClient() != null) {
			if (isGroupManageSend) {
				GroupManagerSendMassMsg(msgtype, content, contentBytes);
			} else {
				// 获得msgindex
				long msgIndex = 1;
				for (int i = chatMessageData.size() - 1; i >= 0; i--) {
					MessageVo tmpvo = chatMessageData.get(i);
					if (tmpvo.getStatus() == MessageVo.STATUS_SENDERR || tmpvo.getStatus() == MessageVo.STATUS_SENDING) {
						msgIndex = tmpvo.getMsgIndex();
					} else {
						msgIndex = tmpvo.getMsgIndex() + 1;
					}
					break;
				}

				UserSendMsg(msgtype, content, contentBytes, msgIndex);
			}
		} else {
			ErrorCodeUtil.handleErrorCode(getActivity(), ErrorCode.EC_TCP_CLIENT_ISNULL, null);
		}
	}

	/**
	 * 会长群发消息
	 * 
	 * @param msgtype
	 * @param content
	 * @param contentBytes
	 */
	private void GroupManagerSendMassMsg(int msgtype, String content, byte[] contentBytes) {
		// 发送消息
		final MessageVo vo = new MessageVo();

		vo.setSource(MessageVo.SOURCE_CLIENT);
		vo.setChannelType(MsgsConstants.MC_PUB);

		vo.setMsgId(0);
		vo.setFromId(uid);
		vo.setFromDomain(MsgsConstants.DOMAIN_USER);
		vo.setToId(groupManageToGroupId);
		vo.setToDomain(MsgsConstants.DOMAIN_GROUP);
		vo.setSubjectId(groupManageToGroupId);
		vo.setSubjectDomain(MsgsConstants.DOMAIN_GROUP);

		vo.setCategory(MsgsConstants.MCC_ANNOUNCE);

		vo.setContentType(msgtype);
		int resourceType = 0;
		switch (msgtype) {
		case MessageContentType.TEXT_VALUE:
			content = ServiceFactory.getInstance().getWordsManager().replace(content);
			vo.setContent(content);
			vo.setSummary(content);
			break;

		case MessageContentType.IMAGE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[图片]");
			resourceType = MsgsConstants.RESOURCE_TYPE_IMAGE;
			break;
		case MessageContentType.VOICE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[声音]");
			resourceType = MsgsConstants.RESOURCE_TYPE_AUDIO;
			break;
		case MessageContentType.MV_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[视频]");
			resourceType = MsgsConstants.RESOURCE_TYPE_VIDEO;
			break;
		case MessageContentType.FILE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[文件]");
			break;
		}

		vo.setCreateTime(System.currentTimeMillis());
		vo.setPosition(SystemContext.getInstance().getLocation());
		vo.setReadStatus(MessageVo.READSTATUS_READ);
		vo.setStatus(MessageVo.STATUS_SENDSUCC);

		// 首先判断后端通讯是否正常，
		XActionCommandClient client = SessionUtil.getLongClient();
		if (client != null) {

			// 向远端发送消息
			ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {

				@Override
				public void onSuccess(Integer result) {
					chatMessageData.add(vo);
					chatMessageAdapter.notifyDataSetChanged();
					chat_content_bottom.getSendMsgEditText().setText("");
					UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_GROUP_MCHAT, null, null, String.valueOf(groupManageToGroupId), null, true);

				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					ErrorCodeUtil.handleErrorCode(ChatFragment.this.getActivity(), result, resultMsg);
					UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_GROUP_MCHAT, null, null, String.valueOf(groupManageToGroupId), null, false);

				}

			};
			// 发送群发消息
			ProxyFactory
					.getInstance()
					.getMessageProxy()
					.sendGroupMassMessage(callback, ChatFragment.this.getActivity(), groupManageToGroupId, content, contentBytes, resourceType,
							SystemContext.getInstance().getLocation());
		} else {
			// "系统通讯异常，请稍后再试"
			ErrorCodeUtil.handleErrorCode(ChatFragment.this.getActivity(), ErrorCode.EC_TCP_CLIENT_ISNULL, null);
		}
	}

	/**
	 * 发送消息给消息系统
	 * 
	 * @param msgtype
	 * @param content
	 * @param contentBytes
	 */
	private void UserSendMsg(int msgtype, String content, byte[] contentBytes, long msgIndex) {
		if (!isSendMsg) {
			ToastUtil.showToast(this.getActivity(), "该窗口不允许发送消息");
			return;
		}
		// 发送消息
		MessageVo vo = new MessageVo();

		vo.setSource(MessageVo.SOURCE_CLIENT);
		if (toDomain.equals(MsgsConstants.DOMAIN_USER)) {
			vo.setChannelType(MsgsConstants.MC_CHAT);

		} else if (toDomain.equals(MsgsConstants.DOMAIN_GROUP)) {
			vo.setChannelType(MsgsConstants.MC_MCHAT);
		}

		vo.setMsgId(0);
		vo.setFromId(uid);
		vo.setFromDomain(MsgsConstants.DOMAIN_USER);
		vo.setToId(toId);
		vo.setToDomain(toDomain);
		vo.setSubjectId(toId);
		vo.setSubjectDomain(toDomain);

		vo.setMsgIndex(msgIndex);

		vo.setCategory(MsgsConstants.MCC_CHAT);

		vo.setContentType(msgtype);
		switch (msgtype) {
		case MessageContentType.TEXT_VALUE:
			content = ServiceFactory.getInstance().getWordsManager().replace(content);
			vo.setContent(content);
			vo.setSummary(content);
			break;

		case MessageContentType.IMAGE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[图片]");
			break;
		case MessageContentType.VOICE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[声音]");
			break;
		case MessageContentType.MV_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[视频]");
			break;
		case MessageContentType.FILE_VALUE:
			vo.setContentBytes(contentBytes);
			vo.setSummary("[文件]");
			break;
		}

		vo.setCreateTime(System.currentTimeMillis());
		vo.setPosition(SystemContext.getInstance().getLocation());
		vo.setReadStatus(MessageVo.READSTATUS_READ);
		vo.setStatus(MessageVo.STATUS_SENDING);

		isSending = true;

		chat_content_bottom.getSendButton().setClickable(false);

		// 先入本地库中，更新界面上的内容为在发送中
		vo = ProxyFactory.getInstance().getMessageProxy().sendMessageSaveToLocal(this.getActivity(), vo);
		if (vo == null) {
			ToastUtil.showToast(getActivity(), "消息本地入库异常，请稍后重试");
			chat_content_bottom.getSendButton().setClickable(true);
		} else {
			boolean issend = true;// 是否发送，默认发送，当写声音文件失败后，将不发送
			if (vo.getContentType() == Msgs.MessageContentType.VOICE_VALUE) {
				try {
					String filename = String.format(SystemConfig.AUDIO_LOCAL_SEND_FILENAME_FORMAT, vo.getId());
					FileUtils.writeSDFile(filename, vo.getContentBytes());
					vo.setContent(String.valueOf(AudioUtils.getAmrDuration(filename)));
					ProxyFactory.getInstance().getMessageProxy().updateMessageContentById(vo.getId(), vo.getContentType(), vo.getContent());
				} catch (IOException e) {
					LogUtil.e(TAG, "声音保存成文件失败");
					ToastUtil.showToast(getActivity(), "声音保存成文件失败");
					e.printStackTrace();
					issend = false;
				}
			} else if (vo.getContentType() == Msgs.MessageContentType.IMAGE_VALUE) {
				try {
					String filename = String.format(SystemConfig.IMAGE_LOCAL_SEND_FILENAME_FORMAT, vo.getId());
					FileUtils.writeSDFile(filename, vo.getContentBytes());

				} catch (IOException e) {
					LogUtil.e(TAG, "图片保存成文件失败");
					ToastUtil.showToast(getActivity(), "图片保存成文件失败");
					e.printStackTrace();
					issend = false;
				}
			}

			if (issend) {

				chatMessageData.add(vo);
				if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
					getMessageVoData(chatMessageData);
					splendidMessageAdapter.notifyDataSetChanged();
				} else {
					chatMessageAdapter.notifyDataSetChanged();
				}

				pullToRefreshListView.getRefreshableView().setSelection(pullToRefreshListView.getRefreshableView().getAdapter().getCount() - 1);
				isSending = false;
				chat_content_bottom.getSendMsgEditText().setText("");
				chat_content_bottom.getSendButton().setClickable(true);
				// 发送到服务器
				UserSendMsgToServer(vo);

			} else {
				ProxyFactory.getInstance().getMessageProxy().delMessageById(vo.getId());
			}
		}

	}

	/**
	 * 发送消息到服务器端
	 * 
	 * @param vo
	 */
	private void UserSendMsgToServer(final MessageVo vo) {
		if (vo != null) {
			// 先更新到界面上，显示成正在发送，回来还是采用更新数据的形式来表现
			// 发送中
			ProxyCallBack<MessageVo> callback = new ProxyCallBack<MessageVo>() {

				@Override
				public void onSuccess(MessageVo result) {
					LogUtil.i(TAG, "消息发送成功:" + result.toString());
					// 更新列表中的数据为发送成功
					UpdateMessageData(result.getId(), result.getMsgId(), result.getCreateTime(), result.getContentType(), result.getContent(),
							result.getStatus(), result.getMsgIndex());
					// 更新已经读到的数据
					SystemContext.getInstance().setSubjectLastUnReadMaxIndex(result.getChannelType(), result.getToId(), result.getToDomain(),
							result.getCategory(), result.getMsgIndex());
					switch (vo.getContentType()) {
					case MessageContentType.TEXT_VALUE:
						if (vo.getToDomain().equals(MsgsConstants.DOMAIN_USER)) {
							// 发用户的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_CHAT_SEND_WORD, null, null, String.valueOf(vo.getToId()), null, true);
						} else if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
							// 发公会的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_MCHAT_SEND_WORD, null, null, String.valueOf(vo.getToId()), null, true);
						}
						break;

					case MessageContentType.IMAGE_VALUE:
					case MessageContentType.IMAGE_ID_REF_VALUE:
						if (vo.getToDomain().equals(MsgsConstants.DOMAIN_USER)) {
							// 发用户的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_CHAT_SEND_PHOTO, null, null, String.valueOf(vo.getToId()), null, true);
						} else if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
							// 发公会的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_MCHAT_SEND_PHOTO, null, null, String.valueOf(vo.getToId()), null,
									true);
						}
						break;
					case MessageContentType.VOICE_VALUE:
						if (vo.getToDomain().equals(MsgsConstants.DOMAIN_USER)) {
							// 发用户的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_CHAT_SEND_VOICE, null, null, String.valueOf(vo.getToId()), null, true);
						} else if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
							// 发公会的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_MCHAT_SEND_VOICE, null, null, String.valueOf(vo.getToId()), null,
									true);
						}
						break;
					}
					;
				}

				@Override
				public void onFailure(Integer result, String resultMsg) {
					LogUtil.e(TAG, "消息发送失败:" + result);
					// 发送失败后，需要把该消息的数据保存下来，用于重发，(主要是图片数据）
					if (vo.getContentType() == Msgs.MessageContentType.IMAGE_VALUE) {
						try {
							String filename = String.format(SystemConfig.IMAGE_LOCAL_SEND_FILENAME_FORMAT, vo.getId());
							FileUtils.writeSDFile(filename, vo.getContentBytes());
						} catch (IOException e) {
							LogUtil.e(TAG, "发送失败的图片保存成文件失败");
							e.printStackTrace();
						}
					}

					// 增加转换错误码
					if (result.intValue() == Msgs.ErrorCode.EC_MSGS_MSG_POST_NORIGHT_VALUE) {
						if (toDomain.equals(MsgsConstants.DOMAIN_GROUP)) {
							ErrorCodeUtil.handleErrorCode(ChatFragment.this.getActivity(), ErrorCode.EC_CLIENT_MCHAT_NORIGHT, null);
						} else if (toDomain.equals(MsgsConstants.DOMAIN_USER)) {
							ErrorCodeUtil.handleErrorCode(ChatFragment.this.getActivity(), ErrorCode.EC_CLIENT_CHAT_NORIGHT, null);
						} else {
							ErrorCodeUtil.handleErrorCode(ChatFragment.this.getActivity(), result, resultMsg);
						}

					} else {
						ErrorCodeUtil.handleErrorCode(ChatFragment.this.getActivity(), result, resultMsg);
					}
					// 更新列表中的数据为发送失败
					UpdateMessageData(vo.getId(), 0, 0, 0, null, MessageVo.STATUS_SENDERR, 0);
					switch (vo.getContentType()) {
					case MessageContentType.TEXT_VALUE:
						if (vo.getToDomain().equals(MsgsConstants.DOMAIN_USER)) {
							// 发用户的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_CHAT_SEND_WORD, null, null, String.valueOf(vo.getToId()), null, false);
						} else if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
							// 发公会的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_MCHAT_SEND_WORD, null, null, String.valueOf(vo.getToId()), null,
									false);
						}
						break;

					case MessageContentType.IMAGE_VALUE:
					case MessageContentType.IMAGE_ID_REF_VALUE:
						if (vo.getToDomain().equals(MsgsConstants.DOMAIN_USER)) {
							// 发用户的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_CHAT_SEND_PHOTO, null, null, String.valueOf(vo.getToId()), null,
									false);
						} else if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
							// 发公会的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_MCHAT_SEND_PHOTO, null, null, String.valueOf(vo.getToId()), null,
									false);
						}
						break;
					case MessageContentType.VOICE_VALUE:
						if (vo.getToDomain().equals(MsgsConstants.DOMAIN_USER)) {
							// 发用户的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_CHAT_SEND_VOICE, null, null, String.valueOf(vo.getToId()), null,
									false);
						} else if (vo.getToDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
							// 发公会的
							UMUtil.sendEvent(getActivity(), UMConfig.MSGS_EVENT_MCHAT_SEND_VOICE, null, null, String.valueOf(vo.getToId()), null,
									false);
						}
						break;
					}
					;

				}

			};
			ProxyFactory.getInstance().getMessageProxy().sendMessage(callback, this.getActivity(), vo);

		}
	}

	/**
	 * 更新展示中的列表数据
	 * 
	 * @param chatMessageData
	 * @param id
	 * @param msgid
	 * @param createTime
	 * @param contentType
	 * @param content
	 * @param status
	 */
	private void UpdateMessageData(long id, long msgId, long createTime, int contentType, String content, int status, long msgIndex) {
		if (status == MessageVo.STATUS_SENDSUCC) {
			if (chatMessageData != null) {
				for (int i = chatMessageData.size() - 1; i >= 0; i--) {
					MessageVo mvo = chatMessageData.get(i);
					if (mvo.getId() == id) {
						mvo.setMsgId(msgId);
						mvo.setCreateTime(createTime);
						mvo.setContentType(contentType);
						mvo.setContent(content);
						mvo.setStatus(status);
						mvo.setMsgIndex(msgIndex);
						if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
							getMessageVoData(chatMessageData);
							splendidMessageAdapter.notifyDataSetChanged();
						} else {
							chatMessageAdapter.notifyDataSetChanged();
						}
						break;
					}
				}
			}
		} else if (status == MessageVo.STATUS_SENDERR) {
			if (chatMessageData != null) {
				for (int i = chatMessageData.size() - 1; i >= 0; i--) {
					MessageVo mvo = chatMessageData.get(i);
					if (mvo.getId() == id) {
						mvo.setStatus(status);
						if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
							getMessageVoData(chatMessageData);
							splendidMessageAdapter.notifyDataSetChanged();
						} else {
							chatMessageAdapter.notifyDataSetChanged();
						}
						break;
					}
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * 
	 * android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android
	 * .widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent.equals(pullToRefreshListView.getRefreshableView())) {
			int realposition = (int) parent.getAdapter().getItemId(position);
			final MessageVo selectItemVo = chatMessageData.get(realposition);

			showActionDialog(selectItemVo);

			return true;
		}
		return false;
	}

	/**
	 * 操作菜单
	 * 
	 * @param selectItemVo
	 */
	public void showActionDialog(final MessageVo selectItemVo) {
		// 弹出复制删除窗口,增加转发功能
		boolean isFirstItem = true;
		LinearLayout contentView = (LinearLayout) View.inflate(this.getActivity(), R.layout.common_dialog_action_manage, null);
		final Dialog dialog = DialogUtil.showDialog(getActivity(), R.layout.dialog, "操作", contentView, 0, 0, 0, 0, null);
		View item;
		TextView text;
		if (selectItemVo.getContentType() == Msgs.MessageContentType.TEXT_VALUE) {
			if (isAllowForwardingAndCopy(selectItemVo)) {
				// 复制
				item = View.inflate(this.getActivity(), R.layout.common_dialog_action_manage_item, null);
				contentView.addView(item);
				if (isFirstItem) {
					item.findViewById(R.id.common_dialog_action_item_divider).setVisibility(View.GONE);
					isFirstItem = false;
				}
				text = (TextView) item.findViewById(R.id.common_dialog_action_item_text);
				text.setText(R.string.common_dialog_action_item_text_copy);
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						ClipboardUtil.setClipboard(ChatFragment.this.getActivity(), selectItemVo.getContent());
					}
				});
			}
		}
		// 如果是声音id
		if (selectItemVo.getContentType() == MessageContentType.VOICE_ID_REF_VALUE
				&& selectItemVo.getVoiceStatus() == MessageVo.VOICESTATUS_DOWNLOADERR) {
			// 重新接受
			item = View.inflate(this.getActivity(), R.layout.common_dialog_action_manage_item, null);
			contentView.addView(item);
			if (isFirstItem) {
				item.findViewById(R.id.common_dialog_action_item_divider).setVisibility(View.GONE);
				isFirstItem = false;
			}
			text = (TextView) item.findViewById(R.id.common_dialog_action_item_text);
			text.setText(R.string.common_dialog_action_item_repeatreceiver);
			item.setOnClickListener(new OnClickListener() {

				@SuppressWarnings("unchecked")
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					// 重收声音文件
					selectItemVo.setVoiceStatus(MessageVo.VOICESTATUS_DOWNLOADING);
					if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
						getMessageVoData(chatMessageData);
						splendidMessageAdapter.notifyDataSetChanged();
					} else {
						chatMessageAdapter.notifyDataSetChanged();
					}

					new MyAsyncTask(null).execute(new AsyncCallBack<Void>() {

						@Override
						public Void execute() {
							// 去下载，保存文件，更新数据库，更新界面数据
							if (selectItemVo.getContentType() == MessageContentType.VOICE_ID_REF_VALUE) {
								try {
									String filename = String.format(SystemConfig.AUDIO_FILENAME_FORMAT, selectItemVo.getMsgId());
									FileUtils.writeSDFile(filename,
											HttpUtil.sendRequest(ResUtil.getOriginalRelUrl(selectItemVo.getContent()), null, null));
									selectItemVo.setContent(String.valueOf(AudioUtils.getAmrDuration(filename)));
									selectItemVo.setContentType(MessageContentType.VOICE_VALUE);
									selectItemVo.setVoiceStatus(MessageVo.VOICESTATUS_OK);
									// 更新数据库
									ProxyFactory.getInstance().getMessageProxy()
											.updateMessageContentById(selectItemVo.getId(), selectItemVo.getContentType(), selectItemVo.getContent());

								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									selectItemVo.setVoiceStatus(MessageVo.VOICESTATUS_DOWNLOADERR);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									selectItemVo.setVoiceStatus(MessageVo.VOICESTATUS_DOWNLOADERR);
									e.printStackTrace();
								}

							}

							return null;
						}

						@Override
						public void onHandle(Void result) {
							// 通知刷新界面
							if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
								getMessageVoData(chatMessageData);
								splendidMessageAdapter.notifyDataSetChanged();
							} else {
								chatMessageAdapter.notifyDataSetChanged();
							}

						}

					});

				}
			});
		} else {
			// 转发功能
			if (isAllowForwardingAndCopy(selectItemVo)) {
				item = View.inflate(this.getActivity(), R.layout.common_dialog_action_manage_item, null);
				contentView.addView(item);
				if (isFirstItem) {
					item.findViewById(R.id.common_dialog_action_item_divider).setVisibility(View.GONE);
					isFirstItem = false;
				}
				text = (TextView) item.findViewById(R.id.common_dialog_action_item_text);
				text.setText(R.string.common_dialog_action_item_forwarding);
				item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						// 转发
						Intent intent = new Intent(ChatFragment.this.getActivity(), ForwardingShareContentFragmentActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt(SystemConfig.BUNDLE_NAME_CONTENT_ACTION_TYPE, SystemConfig.CONTENT_ACTION_TYPE_FORWARDING);
						bundle.putInt(SystemConfig.BUNDLE_NAME_CONTENT_TYPE, SystemConfig.CONTENT_TYPE_MESSAGEVO);
						bundle.putSerializable(SystemConfig.BUNDLE_NAME_CONTENT, selectItemVo);
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					}
				});
			}
		}

		// 重发
		if (selectItemVo.getStatus() == MessageVo.STATUS_SENDERR) {
			item = View.inflate(this.getActivity(), R.layout.common_dialog_action_manage_item, null);
			contentView.addView(item);
			if (isFirstItem) {
				item.findViewById(R.id.common_dialog_action_item_divider).setVisibility(View.GONE);
				isFirstItem = false;
			}
			text = (TextView) item.findViewById(R.id.common_dialog_action_item_text);
			text.setText(R.string.common_dialog_action_item_repeat);
			item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
					// 重发
					// 纠正选中的itemvo的content内容
					if (selectItemVo != null) {
						byte[] contentBytes = null;
						// 读数据文件写到contentBytes中，
						switch (selectItemVo.getContentType()) {
						case MessageContentType.IMAGE_VALUE:
							contentBytes = FileUtils.getSDFileData(String.format(SystemConfig.IMAGE_LOCAL_SEND_FILENAME_FORMAT, selectItemVo.getId()));
							break;
						case MessageContentType.VOICE_VALUE:
							contentBytes = FileUtils.getSDFileData(String.format(SystemConfig.AUDIO_LOCAL_SEND_FILENAME_FORMAT, selectItemVo.getId()));
							break;

						}
						if (contentBytes == null
								&& (selectItemVo.getContentType() == MessageContentType.IMAGE_VALUE || selectItemVo.getContentType() == MessageContentType.VOICE_VALUE)) {
							ToastUtil.showToast(getActivity(), "读取该消息重发数据时异常,无法发送，请重新发送");
						} else {
							if (selectItemVo.getContentType() == MessageContentType.IMAGE_VALUE
									|| selectItemVo.getContentType() == MessageContentType.VOICE_VALUE) {
								selectItemVo.setContentBytes(contentBytes);
							}
							UserSendMsgToServer(selectItemVo);
						}
					}
				}
			});
		}

		// 删除
		item = View.inflate(this.getActivity(), R.layout.common_dialog_action_manage_item, null);
		contentView.addView(item);
		if (isFirstItem) {
			item.findViewById(R.id.common_dialog_action_item_divider).setVisibility(View.GONE);
			isFirstItem = false;
		}
		text = (TextView) item.findViewById(R.id.common_dialog_action_item_text);
		text.setText(R.string.common_dialog_action_item_text_del);
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				// 删除文本
				ProxyFactory.getInstance().getMessageProxy().delMessageById(selectItemVo.getId());
				chatMessageData.remove(selectItemVo);
				if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
					getMessageVoData(chatMessageData);
					splendidMessageAdapter.notifyDataSetChanged();
				} else {
					chatMessageAdapter.notifyDataSetChanged();
				}
			}
		});

		if (mURLSpanClickable != null) {
			mURLSpanClickable.setIsClickUrlable(false);
		}
	}

	private byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		inStream.close();
		return outSteam.toByteArray();
	}

	/**
	 * 加载数据
	 * 
	 * @param index
	 *            读数据的id
	 * @param limit
	 *            条数 ，<0 往小的方向要数据，
	 * @param isUpdateReadStatus
	 *            是否更新读的状态，默认false ，true 更新，false 不更新
	 * @param isSetSelection
	 *            是否定位到最后一条，默认false ，true 为定位到，false为不定位到
	 * @param listener
	 *            加载完数据后的回调
	 */
	private void loadData(final long msgindex, int limit, final boolean isSetSelection, final boolean isGetDataFromServer) {
		LogUtil.i(TAG, "loaddata: index = " + msgindex + ":" + "limit=" + limit);
		isLoadDataing = true;
		final ProxyCallBack<List<MessageVo>> callback = new ProxyCallBack<List<MessageVo>>() {

			@Override
			public void onSuccess(List<MessageVo> result) {
				pullToRefreshListView.onRefreshComplete();
				if (getActivity() != null) {
					List<MessageVo> list = result;
					if (list != null && list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							// limit 肯定小于0
							MessageVo vo = list.get(i);
							if (vo.getStatus() != MessageVo.STATUS_DEL)
								chatMessageData.add(0, vo);
						}
						if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
							getMessageVoData(chatMessageData);
							splendidMessageAdapter.notifyDataSetChanged();
						} else {
							chatMessageAdapter.notifyDataSetChanged();
						}
					}

					if (isSetSelection) {
						pullToRefreshListView.getRefreshableView().setSelection(
								pullToRefreshListView.getRefreshableView().getAdapter().getCount() - 1);
					}
					if (list.size() > 0)
						pullToRefreshListView.getRefreshableView().setSelectionFromTop(
								pullToRefreshListView.getRefreshableView().getFirstVisiblePosition() + list.size(), 0);
				}
				isLoadDataing = false;
				delLocalHistoryMessage(channelType, toId, toDomain, category);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				if (getActivity() != null)
					ErrorCodeUtil.handleErrorCode(getActivity(), result, resultMsg);

				isLoadDataing = false;
				pullToRefreshListView.onRefreshComplete();
				delLocalHistoryMessage(channelType, toId, toDomain, category);
			}
		};
		long minIndex = SystemContext.getInstance().getSubjectcanReadMinIndex(channelType, toId, toDomain, category);
		ProxyFactory.getInstance().getMessageProxy()
				.getMessage(callback, channelType, toId, toDomain, category, msgindex, limit, isGetDataFromServer, minIndex);
	}
	
	/**
	 * 删除本地历史消息
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 */
	private void delLocalHistoryMessage(String channelType, long subjectid, String subjectDomain, String category){
		if(!deletedMsg){
			ProxyFactory.getInstance().getMessageProxy().delLocalHistoryMessage(channelType, subjectid, subjectDomain, category);
			deletedMsg = true;
		}
	}

	/**
	 * 拆解MessageVo信息，便于Adapter数据操作
	 * 
	 * @param messageVos
	 */
	private void getMessageVoData(List<MessageVo> messageVos) {
		SplendidmessageData.clear();
		for (int i = 0; i < messageVos.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			MessageVo vo = messageVos.get(i);
			ArrayList<Map<String, Object>> arrayList = null;
			String cardsType = null;
			try {
				JSONObject o = new JSONObject(vo.getContent());
				cardsType = o.getString("cardsType");
				long pushId = Long.valueOf(o.get("pushId").toString());
				map.put("cardsType", cardsType);// 推送类型
				map.put("pushId", pushId);// 推送ID

				arrayList = new ArrayList<Map<String, Object>>();
				JSONArray array = o.getJSONArray("cards");
				for (int j = 0; j < array.length(); j++) {
					Map<String, Object> tempmap = new HashMap<String, Object>();
					JSONObject temp = array.getJSONObject(j);
					Iterator it = temp.keys();
					while (it.hasNext()) {
						String key = (String) it.next();
						if (key.equals("content")) {
							JSONObject o2 = new JSONObject(temp.getString("content"));
							Iterator it2 = o2.keys();
							while (it2.hasNext()) {
								String key2 = (String) it2.next();
								tempmap.put(key2, o2.get(key2));
							}
						} else {
							tempmap.put(key, temp.get(key));
						}
					}
					arrayList.add(tempmap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			long createTime = vo.getCreateTime();
			map.put("createTime", createTime);// 创建时间
			if (arrayList != null && arrayList.size() > 0 && cardsType != null && !cardsType.isEmpty()) {
				map.put("arrayList", arrayList);
				SplendidmessageData.add(map);
			}
		}
	}

	public void refreshData() {
		chatMessageData.clear();
		if (pageType == SplendidMessageAdapter.PAGE_TYPE_SPLENDID_PUSH) {
			getMessageVoData(chatMessageData);
			splendidMessageAdapter.notifyDataSetChanged();
		} else {
			chatMessageAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.d(TAG, "resultCode =" + resultCode + ";requestCode=" + requestCode);
		byte[] photoByte = null;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PhotoUtil.CAMERA_WITH_DATA:
				// 先缩放
				try {
					Bitmap bitmap = ImageUtil.createImageThumbnail(PhotoUtil.sdcardTempFilePath, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
					photoByte = ImageUtil.Bitmap2Bytes(bitmap, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
					if(bitmap != null && !bitmap.isRecycled()){ 
						// 回收并且置为null
						bitmap.recycle(); 
						bitmap = null; 
					} 
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				break;
			case PhotoUtil.PHOTO_PICKED_WITH_DATA:
				ContentResolver resolver = this.getActivity().getContentResolver();
				if (data != null) {
					// 照片的原始资源地址
					Uri originalUri = data.getData();
					try {
						// 使用ContentProvider通过URI获取原始图片
						Bitmap photo = ImageUtil.createImageThumbnail(resolver, originalUri, -1, SystemConfig.BITMAP_MAX_RESOLUTION, true);
						if (photo != null) {
							photoByte = ImageUtil.Bitmap2Bytes(photo, CompressFormat.JPEG, SystemConfig.BITMAP_COMPRESS_QUALITY);
							if(photo != null && !photo.isRecycled()){ 
								// 回收并且置为null
								photo.recycle(); 
								photo = null; 
							} 
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			case PhotoUtil.CROP_IMAGE_WITH_DATA:
				break;
			}

			if (photoByte != null) {
				// 发送图片到服务器
				try {
					Intent intent = new Intent(this.getActivity(), ImageSendBrowerActivity.class);
					Bundle bundle = new Bundle();
					bundle.putByteArray(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CONTENT, photoByte);
					intent.putExtras(bundle);
					startActivityForResult(intent, SystemConfig.ACTIVITY_REQUESTCODE_DISTROY);
				} catch (Exception e) {
					LogUtil.e(TAG, "发送图片异常");
				}
			} else {
				if (data != null) {
					ToastUtil.showToast(this.getActivity(), getResources().getString(R.string.common_sendphoto_isnull));
				}
				LogUtil.w(TAG, "获得需要发送的图片异常");
				return;
			}
		} else if (resultCode == SystemConfig.ACTIVITY_RESULTCODE_DISTROY) {
			switch (requestCode) {
			case SystemConfig.ACTIVITY_REQUESTCODE_DISTROY:
				photoByte = data.getExtras().getByteArray(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_CONTENT);
				// 发送图片消息
				sendMsg(MessageContentType.IMAGE_VALUE, null, photoByte);
				break;
			}
		} else {
			LogUtil.w(TAG, "选择发送的图片异常");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int arg2, long arg3) {
		LogUtil.d(TAG, "parent.getId()" + ":" + R.id.chat_msg_listview);
		if (parent.equals(pullToRefreshListView.getRefreshableView())) {
			chat_content_bottom.hidePanelAndSoftInput();
			// 设置可以滚动
			if (getActivity() instanceof GroupChatFragmentActivity) {
				((GroupChatFragmentActivity) ChatFragment.this.getActivity()).setViewPagerScrollable(true);
			}
		}
	}

	/**
	 * 判断是否允许转发和复制
	 * 
	 * @return
	 */
	public boolean isAllowForwardingAndCopy(MessageVo vo) {
		if (vo == null)
			return false;
		if (vo.getContentType() == MessageContentType.TEXT_VALUE) {
			// 解析text内容
			SpannableString ss = MyTagUtil.analyseMyTag(getActivity(), vo.getContent(), 1, 1, null);
			MyTagSpan[] myTagSpans = ss.getSpans(0, ss.length(), MyTagSpan.class);
			// for(int i = 0 ; i<myTagSpans.length;i++){
			//
			// }
			if (myTagSpans.length > 0) {
				return false;
			}
		} else if (vo.getContentType() == MessageContentType.IMAGE_VALUE) {
		} else if (vo.getContentType() == MessageContentType.IMAGE_ID_REF_VALUE) {
		} else if (vo.getContentType() == MessageContentType.VOICE_VALUE) {
			return false;
		} else if (vo.getContentType() == MessageContentType.NEWS_TEXT_VALUE) {
		} else if (vo.getContentType() == MessageContentType.CARD_TEXT_VALUE) {
			return false;
		}
		return true;
	}

	/**
	 * 加载完数据后的回调
	 * 
	 * @ClassName: LoadDataFinishListener
	 * @Description: TODO(...)
	 * @author chuanglong
	 * @date 2014-5-12 下午3:06:59
	 * @Version 1.0
	 * 
	 */
	public interface LoadDataFinishListener {
		public void loadDataFinish();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener
	 * #onRefresh(com.handmark.pulltorefresh.library.PullToRefreshBase)
	 */
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		String label = DateUtils.formatDateTime(SystemContext.getInstance().getContext(), SystemContext.getInstance().getCurrentTimeInMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		if (chatMessageData.size() > 0) {

			MessageVo tmp = chatMessageData.get(0);
			if (tmp.getMsgIndex() == 1) {
				pullToRefreshListView.postDelayed(new Runnable() {

					@Override
					public void run() {
						pullToRefreshListView.onRefreshComplete();
					}
				}, 500);

			} else if (tmp.getMsgIndex() > 1 && tmp.getMsgIndex() < SystemConfig.PAGE_SIZE)
				loadData(tmp.getMsgIndex(), -(int) (tmp.getMsgIndex() - 1), false, true);
			else
				loadData(tmp.getMsgIndex(), -SystemConfig.PAGE_SIZE, false, true);
		} else {
			// 没有数据，先从网络上要20条数据
			loadData(Integer.MAX_VALUE, -SystemConfig.PAGE_SIZE, true, true);
		}
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
}
