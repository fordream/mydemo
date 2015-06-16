/**      
 * NewsMainFragment.java Create on 2013-12-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */
package com.iwgame.msgs.module.message.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.BaseFragment;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.account.ui.login.LoginActivity;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.chat.ui.GroupChatListFragmentActivity;
import com.iwgame.msgs.module.chat.ui.GroupMassMsgFragmentActivity;
import com.iwgame.msgs.module.chat.ui.GroupMassMsgListFragmentActivity;
import com.iwgame.msgs.module.chat.ui.SplendidRecommendFragmentActivity;
import com.iwgame.msgs.module.chat.ui.SysOfficialChatFragmentActivity;
import com.iwgame.msgs.module.chat.ui.SystemChatFragmentActivity;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.module.message.adapter.MessageAdapter2;
import com.iwgame.msgs.module.message.adapter.MessageAdapter2.ItemClickListener2;
import com.iwgame.msgs.module.postbar.ui.TopicNotifyFragmentActivity;
import com.iwgame.msgs.module.setting.vo.ChannelGroupVo;
import com.iwgame.msgs.module.setting.vo.ChannelVo;
import com.iwgame.msgs.utils.DialogUtil;
import com.iwgame.msgs.utils.DialogUtil.OKCallBackListener;
import com.iwgame.msgs.utils.MessageUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.MyTagUtil;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.utils.LogUtil;
import com.iwgame.widget.slideitemlistview.SlideItemListView;
import com.iwgame.widget.slideitemlistview.SlideItemView;
import com.youban.msgs.R;

/**
 * 
 * @ClassName: MessageFragment
 * @Description: TODO(消息首页)
 * @author chuanglong
 * @date 2013-12-27 下午4:54:47
 * @Version 1.0
 * 
 */
public class MessageFragment2 extends BaseFragment implements OnItemClickListener {

	private static final String TAG = "MessageFragment";

	private LayoutInflater inflater;

	ListView listView_popwindow;

	SlideItemListView listView_content;
	List<MessageVo> newsdata = new ArrayList<MessageVo>(); // 对应列表上的数据
	// List<MessageVo> newsdata_all = new ArrayList<MessageVo>(); //
	// 用于保存全部的，这样后面过滤时就不需要再次查询计算，加快速度
	int newsdata_all_unreadcount = 0;// 全部的消息未读数
	MessageAdapter2 newsAdapter;
	View topCenterMenu = null;
	TextView news_menu_titlename = null;
	int currentSelectPopMenu = 0;
	long currentUserId = 0;// 临时使用，用于刷新重新登录的用户
	Activity activity = null;
	NewsRefreshDataReceiver newsRefreshDataReceiver;

	PopupWindow popWindow;
	boolean isGroupMassMsgList = false;
	boolean isGroupChatMsgList = false;
	/**
	 * 没有数据时的界面
	 */
	LinearLayout nullContent;
	/**
	 * 没有数据时显示的图片
	 */
	ImageView nullContentBgIcon;

	Handler handler = new Handler();

	/**
	 * 是否取消通知栏上的消息
	 */
	boolean isCannelNotification = true;

	/**
	 * 是否初始化了界面，默认没有，在createview的时候初始化完成改成true；目的用于计算未读数值
	 */
	boolean isInitFinish = false;
	/** 当前的内容view **/
	private View contentView = null;
	/** 当前用户id **/
	private long uid;

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

		// 获得传入的用户参数
		Bundle tmpbundle = this.getArguments();
		if (tmpbundle != null) {
			isGroupMassMsgList = tmpbundle.getBoolean(SystemConfig.BUNDLE_NAME_ISGROUPMASSMSGLIST, false);
			isGroupChatMsgList = tmpbundle.getBoolean(SystemConfig.BUNDLE_NAME_ISGROUPCHATMSGLIST, false);
		}

		// // 添加动态主界面
		// View v = inflater.inflate(R.layout.common_content, container, false);
		// // 初始化界面
		// init(v);
		// isInitFinish = true;
		// return v;
		LogUtil.d(TAG, "--->>MessageFragment2");
		// 判断当前用户有没有切换为新用户
		ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
		if (vo != null) {
			long newuid = vo.getUserid();
			if (newuid != uid) {
				LogUtil.d(TAG, "--->>MessageFragment2::newuid=" + newuid + ", uid=" + uid);
				if (newsdata != null && newsAdapter != null) {
					newsdata.clear();
					newsAdapter.notifyDataSetChanged();
				}
				uid = newuid;
				contentView = null;
			}
		}
		// 判断当前消息是否有变化
		if (SystemContext.getInstance().isHasNewMessage() || newsdata.size() == 0) {
			contentView = null;
		}

		if (contentView == null) {
			contentView = inflater.inflate(R.layout.common_content, container, false);
			// 初始化界面
			init(contentView);
			isInitFinish = true;
		}
		ViewGroup parent = (ViewGroup) contentView.getParent();
		if (parent != null) {
			parent.removeView(contentView);
		}
		return contentView;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		PTAG = TAG;
		if (activity != null) {
			// 注册receiver
			newsRefreshDataReceiver = new NewsRefreshDataReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(SystemConfig.ACTION_RECEIVEMESSAGE_BROADCAST);
			filter.setPriority(getResources().getInteger(R.integer.receiver_priority_messagebroadcast_mainactivity));
			activity.registerReceiver(newsRefreshDataReceiver, filter);
		}

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// MobclickAgent.onPageEnd(TAG);
		isCannelNotification = false;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// MobclickAgent.onPageStart(TAG);
		if (SystemContext.getInstance().isUnAuth()) {
			// 会话失效， 主动注销，token不存在/认证失败，都到登录页
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean(SystemConfig.SHAREDPREFERENCES_NAME_GLOBAL_UNAUTHENTICATED, true);
			intent.putExtras(bundle);
			startActivity(intent);
		} else {
			if (getActivity() instanceof MainFragmentActivity) {
				((MainFragmentActivity) getActivity()).showOrDismissTabHost(true, null, null);
			}

			isCannelNotification = true;

			ExtUserVo vo = SystemContext.getInstance().getExtUserVo();
			if (vo != null && this.currentUserId != vo.getUserid()) {
				currentSelectPopMenu = 0;
				currentUserId = vo.getUserid();
			}
			if (isGroupMassMsgList) {
				// 公会会长群发消息
				getGroupAnnounceAndRefresh();
			} else if (isGroupChatMsgList) {
				// 公会聊天室list消息
				getGroupChatAndRefresh();
			} else {
				if (SystemContext.getInstance().isHasNewMessage() || newsdata.size() == 0) {
					newsdata.clear();
					getAllDataAndRefresh();
					SystemContext.getInstance().setHasNewMessage(false);
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		if (activity != null && newsRefreshDataReceiver != null) {
			activity.unregisterReceiver(newsRefreshDataReceiver);
		}
		super.onDestroy();

	}

	private void init(View v) {
		if (getActivity() instanceof MainFragmentActivity) {
			v.findViewById(R.id.bottomHitView).setVisibility(View.VISIBLE);
			v.findViewById(R.id.bottomView).setVisibility(View.VISIBLE);
		}
		// 隐藏top 左边菜单
		LinearLayout left = (LinearLayout) v.findViewById(R.id.left);

		// 设置top右边功能按钮

		// 设置TOP的中间布局容器
		LinearLayout topcenter = (LinearLayout) v.findViewById(R.id.center);
		TextView titleTxt = (TextView) v.findViewById(R.id.titleTxt);

		if (isGroupMassMsgList || isGroupChatMsgList) {
			left.setVisibility(View.VISIBLE);
			// 设置返回功能
			Button backBtn = (Button) v.findViewById(R.id.leftBtn);
			if (backBtn != null) {
				backBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						MessageFragment2.this.getActivity().finish();
					}
				});
			}
			if (isGroupMassMsgList)
				titleTxt.setText(this.getString(R.string.message_title_groupmassmsg_info));
			if (isGroupChatMsgList)
				titleTxt.setText(this.getString(R.string.message_title_groupchatmsg_info));

		} else {
			left.setVisibility(View.INVISIBLE);
			titleTxt.setText(this.getString(R.string.message_title_info));
		}

		// 设置中间内容的布局文件
		LinearLayout contentView = (LinearLayout) v.findViewById(R.id.contentView);
		contentView.removeAllViews();
		final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.fragment_news_content2, null);

		nullContent = (LinearLayout) view.findViewById(R.id.nullContent);
		nullContentBgIcon = (ImageView) nullContent.findViewById(R.id.bgIcon);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		contentView.addView(view, params);

		// 获得中间listview
		listView_content = (SlideItemListView) view.findViewById(R.id.listView_content);

		ItemClickListener2 itemClickListener = new ItemClickListener2() {

			@Override
			public void onClickAction(final int position, int action) {
				// TODO Auto-generated method stub
				final TextView txt = new TextView(getActivity());
				txt.setPadding(0, getActivity().getResources().getDimensionPixelSize(R.dimen.global_page_paddingtop), getActivity().getResources()
						.getDimensionPixelSize(R.dimen.global_page_paddingright),
						getActivity().getResources().getDimensionPixelSize(R.dimen.global_page_paddingbottom));

				txt.setTextColor(getActivity().getResources().getColor(R.color.darkgray));

				txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.text_medium));
				txt.setText("确定要真的删除吗？");

				OKCallBackListener listener = new OKCallBackListener() {

					@Override
					public void execute() {
						// 删除
						MessageVo vo = newsdata.get(position);
						ProxyFactory.getInstance().getMessageProxy()
								.delMessage(vo.getChannelType(), vo.getSubjectId(), vo.getSubjectDomain(), vo.getCategory());
						// 删除item
						newsdata.remove(position);
						newsAdapter.notifyDataSetChanged();
						int unResdCount = Math.max(newsdata_all_unreadcount - vo.getUnReadCount(), 0);
						if (unResdCount != newsdata_all_unreadcount)
							SystemContext.getInstance().setHasNewMessage(true);
						newsdata_all_unreadcount = unResdCount;
						// 删除未读数
						if (vo.getSubjectId() == -1) {
							// 清空合并的
							SystemContext.getInstance().cleanSubjectUnReadCount(vo.getChannelType(), vo.getSubjectDomain(), vo.getCategory());
						} else {
							SystemContext.getInstance().cleanSubjectUnReadCount(vo.getChannelType(), vo.getSubjectId(), vo.getSubjectDomain(),
									vo.getCategory());
						}
						if (getActivity() instanceof MainFragmentActivity) {
							((MainFragmentActivity) getActivity()).updataNewsUnReadCount(newsdata_all_unreadcount);
						}
						loadDataAfter();
					}

					@Override
					public void cannel() {
						// TODO Auto-generated method stub
					}

				};
				DialogUtil.showDialog(getActivity(), "提示", txt, listener);

			}

		};
		newsAdapter = new MessageAdapter2(listView_content.getContext(), newsdata, itemClickListener);
		listView_content.setAdapter(newsAdapter);

		listView_content.setOnItemClickListener(this);
	}

	/**
	 * 获得全部消息并且刷新
	 */
	private void getAllDataAndRefresh() {
		ProxyCallBack<Map<String, Object>> callback = new ProxyCallBack<Map<String, Object>>() {

			@Override
			public void onSuccess(Map<String, Object> result) {
				// TODO Auto-generated method stub
				int unResdCount = Integer.valueOf(result.get("unreadCount_All").toString()).intValue();
				if (unResdCount != newsdata_all_unreadcount)
					SystemContext.getInstance().setHasNewMessage(true);
				newsdata_all_unreadcount = unResdCount;
				if (!isGroupMassMsgList && !isGroupChatMsgList) {
					if (activity != null) {
						// 刷新总未读数
						((MainFragmentActivity) activity).updataNewsUnReadCount(newsdata_all_unreadcount);
						if (isCannelNotification) {
							// 取消状态栏上的通知
							NotificationManager nm = (NotificationManager) activity.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
							nm.cancel(SystemConfig.NOTIFICATION_ID_BASE);
						}
					}
				}

				newsdata.clear();
				if (result.get("data") != null) {
					newsdata.addAll((List<MessageVo>) result.get("data"));
				}
				if (isInitFinish)
					newsAdapter.notifyDataSetChanged();
				loadDataAfter();
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// 此接口方法不会被调用
			}
		};
		getAllData(callback);
	}

	/**
	 * 
	 * @param callback
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void getAllData(final ProxyCallBack<Map<String, Object>> callback) {
		new MyAsyncTask().execute(new AsyncCallBack<Map<String, Object>>() {

			@Override
			public Map<String, Object> execute() {
				// TODO Auto-generated method stub
				Map<String, Object> ret = new HashMap<String, Object>();
				List<MessageVo> list = ProxyFactory.getInstance().getMessageProxy().getSubjectLastMessage();
				if (list != null) {
					MessageVo tmp_firstGroupAnnounceVo = null; // 第一条公会会长群发消息
					if (list != null && list.size() > 0) {
						// 过滤群聊的（公会，临时讨论）合并公会聊天室消息，合并公会会长群发消息，关于我的，，以评论我的为主，排序位置按照评论我的来排序，如果没有评论我的将不显示
						for (int i = list.size() - 1; i >= 0; i--) {
							MessageVo tmpvo = list.get(i);
							if (tmpvo.getChannelType().equals(MsgsConstants.MC_PUB) && tmpvo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)
									&& tmpvo.getSubjectDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
								// 公会会长群发消息
								tmp_firstGroupAnnounceVo = tmpvo;
							}
						}
						// 公会会长群发消息 合并
						if (tmp_firstGroupAnnounceVo != null) {
							MessageVo vo = new MessageVo(tmp_firstGroupAnnounceVo);
							vo.setSubjectId(-1);
							int tmp = list.indexOf(tmp_firstGroupAnnounceVo);
							list.remove(tmp);
							list.add(tmp, vo);
							// 获得该主体的未读数
							long lastUnReadMaxIndex2 = SystemContext.getInstance().getSubjectLastUnReadMaxIndex(
									tmp_firstGroupAnnounceVo.getChannelType(), tmp_firstGroupAnnounceVo.getSubjectId(),
									tmp_firstGroupAnnounceVo.getSubjectDomain(), tmp_firstGroupAnnounceVo.getCategory());
							long unReadMaxIndex2 = SystemContext.getInstance().getSubjectUnReadMaxIndex(tmp_firstGroupAnnounceVo.getChannelType(),
									tmp_firstGroupAnnounceVo.getSubjectId(), tmp_firstGroupAnnounceVo.getSubjectDomain(),
									tmp_firstGroupAnnounceVo.getCategory());
							int unReadCount2 = unReadMaxIndex2 - lastUnReadMaxIndex2 > 0 ? (int) (unReadMaxIndex2 - lastUnReadMaxIndex2) : 0;
							tmp_firstGroupAnnounceVo.setUnReadCount(unReadCount2);
							vo.setUnReadCount(vo.getUnReadCount() + unReadCount2);
							// 移除后面所有的公会会长群发消息
							for (int i = list.size() - 1; i > tmp; i--) {
								MessageVo tmpvo = list.get(i);
								if (tmpvo.getChannelType().equals(MsgsConstants.MC_PUB) && tmpvo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)
										&& tmpvo.getSubjectDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
									// 获得该主体的未读数
									long lastUnReadMaxIndex = SystemContext.getInstance().getSubjectLastUnReadMaxIndex(tmpvo.getChannelType(),
											tmpvo.getSubjectId(), tmpvo.getSubjectDomain(), tmpvo.getCategory());
									long unReadMaxIndex = SystemContext.getInstance().getSubjectUnReadMaxIndex(tmpvo.getChannelType(),
											tmpvo.getSubjectId(), tmpvo.getSubjectDomain(), tmpvo.getCategory());
									int unReadCount = unReadMaxIndex - lastUnReadMaxIndex > 0 ? (int) (unReadMaxIndex - lastUnReadMaxIndex) : 0;
									tmpvo.setUnReadCount(unReadCount);
									vo.setUnReadCount(vo.getUnReadCount() + unReadCount);
									list.remove(i);
								}
							}
						}
					}
					// 总未读数
					int unreadCount_All = 0;
					// 现在list已经按照最后一条消息情况进行排序了,开始按照情况作排序
					// 有未读对话排在前面，（系统，公会会长群发消息，关于我的,公会聊天室）有未读消息排中间，已读的排最后
					// List<MessageVo> newlist_up = new
					// ArrayList<MessageVo>();// 上
					// List<MessageVo> newlist_middle = new
					// ArrayList<MessageVo>();// 中
					// List<MessageVo> newlist_down = new
					// ArrayList<MessageVo>();// 下
					for (int i = 0; i < list.size(); i++) {
						MessageVo tmpvo = list.get(i);
						int unreadCount = 0;
						if (tmpvo.getSubjectId() == -1) {
							// 采用汇总
							unreadCount = tmpvo.getUnReadCount();
						} else {
							String content = tmpvo.getContent();
							UserVo uvo = SystemContext.getInstance().getExtUserVo();
							if (uvo != null && content != null && content.contains(MyTagUtil.TAG_FOLLOW)
									&& tmpvo.getSubjectDomain().equals(MsgsConstants.DOMAIN_USER)
									&& tmpvo.getChannelType().equals(MsgsConstants.MC_CHAT) && tmpvo.getCategory().equals(MsgsConstants.MCC_CHAT)
									&& tmpvo.getFromId() == uvo.getUserid()) {
								tmpvo.setUnReadCount(0);
							} else {
								// 获得该主体的未读数
								long lastUnReadMaxIndex = SystemContext.getInstance().getSubjectLastUnReadMaxIndex(tmpvo.getChannelType(),
										tmpvo.getSubjectId(), tmpvo.getSubjectDomain(), tmpvo.getCategory());
								long unReadMaxIndex = SystemContext.getInstance().getSubjectUnReadMaxIndex(tmpvo.getChannelType(),
										tmpvo.getSubjectId(), tmpvo.getSubjectDomain(), tmpvo.getCategory());
								int unReadCount = unReadMaxIndex - lastUnReadMaxIndex > 0 ? (int) (unReadMaxIndex - lastUnReadMaxIndex) : 0;
								tmpvo.setUnReadCount(unReadCount);
								unreadCount = tmpvo.getUnReadCount();
							}
						}
						// 未读排前面（对聊在前，其他在后），其他排后面
						unreadCount_All += unreadCount;
						// 设置排序权重
						if (unreadCount > 0) {
							if (tmpvo.getChannelType().equals(MsgsConstants.MC_PUB) && tmpvo.getCategory().equals(MsgsConstants.MCC_OFFICIAL)) {
								// 系统公告
								tmpvo.setSortWeights(20);
							} else if (tmpvo.getChannelType().equals(MsgsConstants.MC_PUB) && tmpvo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)) {
								// 公会公告
								tmpvo.setSortWeights(19);
							} else if ((tmpvo.getChannelType().equals(MsgsConstants.MC_CHAT) || tmpvo.getChannelType().equals(MsgsConstants.MC_MCHAT))
									&& tmpvo.getCategory().equals(MsgsConstants.MCC_CHAT)) {
								// 对聊、群聊
								tmpvo.setSortWeights(18);
							} else if (tmpvo.getChannelType().equals(MsgsConstants.MC_NOTIFY)
									&& tmpvo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)) {
								// 游伴小助手
								tmpvo.setSortWeights(17);
							} else if (tmpvo.getChannelType().equals(MsgsConstants.MC_NOTIFY)
									&& tmpvo.getCategory().equals(MsgsConstants.MCC_ACTIVITY)) {
								// 活动小秘
								tmpvo.setSortWeights(16);
							} else if (tmpvo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && tmpvo.getCategory().equals(MsgsConstants.MCC_PLAY)) {
								// 陪玩小助手
								tmpvo.setSortWeights(15);
							} else if (tmpvo.getChannelType().equals(MsgsConstants.MC_NOTIFY)
									&& tmpvo.getCategory().equals(MsgsConstants.MCC_COMMENT)) {
								// 回复我的
								tmpvo.setSortWeights(14);
							} else if (tmpvo.getChannelType().equals(MsgsConstants.MC_NOTIFY)
									&& (tmpvo.getCategory().equals(MsgsConstants.MCC_POST) || tmpvo.getCategory().equals(MsgsConstants.MCC_FATE))) {
								// 精彩推荐&缘分好友
								tmpvo.setSortWeights(13);
							} else {
								// 其他
								tmpvo.setSortWeights(1);
							}
						} else {
							// 未读
							tmpvo.setSortWeights(0);
						}
					}
					Collections.sort(list, new Comparator<MessageVo>() {

						@Override
						public int compare(MessageVo o1, MessageVo o2) {
							return ((Integer) o2.getSortWeights()).compareTo((Integer) o1.getSortWeights());
						}
					});
					ret.put("unreadCount_All", unreadCount_All);
					ret.put("data", list);
				} else {
					ret.put("unreadCount_All", 0);
					ret.put("data", null);
				}
				return ret;
			}

			@Override
			public void onHandle(Map<String, Object> result) {
				// TODO Auto-generated method stub
				callback.onSuccess(result);
			}
		});

	}

	public static void runUnReadCount(ProxyCallBack<Map<String, Object>> callback) {
		getAllData(callback);
	}

	/**
	 * 获得公会会长群发消息，并且刷新
	 */
	private void getGroupAnnounceAndRefresh() {
		new MyAsyncTask(null).execute(new AsyncCallBack<List<MessageVo>>() {
			@Override
			public List<MessageVo> execute() {
				// TODO Auto-generated method stub
				return ProxyFactory.getInstance().getMessageProxy().getSubjectLastMessage();
			}

			@Override
			public void onHandle(List<MessageVo> result) {
				// TODO Auto-generated method stub
				newsdata.clear();
				if (result != null) {
					for (int i = 0; i < result.size(); i++) {
						MessageVo tmpvo = result.get(i);
						if (tmpvo.getChannelType().equals(MsgsConstants.MC_PUB) && tmpvo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)
								&& tmpvo.getSubjectDomain().equals(MsgsConstants.DOMAIN_GROUP)) {
							// 获得该主体的未读数
							long lastUnReadMaxIndex = SystemContext.getInstance().getSubjectLastUnReadMaxIndex(tmpvo.getChannelType(),
									tmpvo.getSubjectId(), tmpvo.getSubjectDomain(), tmpvo.getCategory());
							long unReadMaxIndex = SystemContext.getInstance().getSubjectUnReadMaxIndex(tmpvo.getChannelType(), tmpvo.getSubjectId(),
									tmpvo.getSubjectDomain(), tmpvo.getCategory());
							int unReadCount = unReadMaxIndex - lastUnReadMaxIndex > 0 ? (int) (unReadMaxIndex - lastUnReadMaxIndex) : 0;
							tmpvo.setUnReadCount(unReadCount);
							newsdata.add(tmpvo);
						}
					}
				}
				newsAdapter.notifyDataSetChanged();
				loadDataAfter();
			}
		});
	}

	/**
	 * 获得公会聊天室消息，并且刷新
	 */
	private void getGroupChatAndRefresh() {
		new MyAsyncTask(null).execute(new AsyncCallBack<List<MessageVo>>() {
			@Override
			public List<MessageVo> execute() {
				// TODO Auto-generated method stub
				return ProxyFactory.getInstance().getMessageProxy().getSubjectLastMessage();
			}

			@Override
			public void onHandle(List<MessageVo> result) {
				// TODO Auto-generated method stub
				newsdata.clear();
				if (result != null) {
					for (int i = 0; i < result.size(); i++) {
						MessageVo tmpvo = result.get(i);
						if (tmpvo.getChannelType().equals(MsgsConstants.MC_MCHAT)) {
							// 获得该主体的未读数
							long lastUnReadMaxIndex = SystemContext.getInstance().getSubjectLastUnReadMaxIndex(tmpvo.getChannelType(),
									tmpvo.getSubjectId(), tmpvo.getSubjectDomain(), tmpvo.getCategory());
							long unReadMaxIndex = SystemContext.getInstance().getSubjectUnReadMaxIndex(tmpvo.getChannelType(), tmpvo.getSubjectId(),
									tmpvo.getSubjectDomain(), tmpvo.getCategory());
							int unReadCount = unReadMaxIndex - lastUnReadMaxIndex > 0 ? (int) (unReadMaxIndex - lastUnReadMaxIndex) : 0;
							tmpvo.setUnReadCount(unReadCount);
							newsdata.add(tmpvo);
						}
					}
				}
				newsAdapter.notifyDataSetChanged();
				loadDataAfter();
			}

		});

	}

	private class NewsRefreshDataReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (isInitFinish) {
				// 主动刷新
				if (isGroupMassMsgList) {
					// 公会会长群发消息
					getGroupAnnounceAndRefresh();
				} else if (isGroupChatMsgList) {
					// 公会聊天室list消息
					getGroupChatAndRefresh();
				} else {
					getAllDataAndRefresh();
				}
				// abortBroadcast();
			}
		}
	}

	/**
	 * 加载数据之后的操作
	 */
	private void loadDataAfter() {

		if (newsdata.size() == 0) {

			// 设置无数据时的界面
			nullContent.setVisibility(View.VISIBLE);
			nullContentBgIcon.setBackgroundResource(R.drawable.common_nomessage);

		} else {
			nullContent.setVisibility(View.GONE);
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onItemClick position=" + position);
		SystemContext.getInstance().setHasNewMessage(true);
		position = (int) parent.getAdapter().getItemId(position);
		// 单击item
		// 关闭所有打开的item
		// listView_content.closeOpenedItems();
		// 增加保护，由于在压力测试的情况下，点击到程序的响应时，数据已经更新，导致获得数据异常的问题 2014.08.14
		SlideItemView slideItemView = ((MessageAdapter2) parent.getAdapter()).getmLastSlideItemViewWithStatusOn();
		int index = -1;
		if (slideItemView != null)
			index = ((MessageAdapter2) parent.getAdapter()).getmLastSlideItemViewWithStatusOn().getmIndex();
		if (newsdata.size() - 1 < position)
			return;
		if (slideItemView != null && index == position)
			return;

		// 打开对于的item
		MessageVo vo = newsdata.get(position);
		Intent intent = null;
		Bundle bundle = null;

		// 获得规则
		// 获得配置文件的规则，判断展示
		Object showRule = MessageUtil.getShowRuleByMessage(SystemContext.getInstance().getChannelsShowRule(), vo);
		if (showRule instanceof ChannelVo) {
			ChannelVo tmp = (ChannelVo) showRule;
			if (tmp != null) {
				if (tmp.getPagetype().equals(ChannelVo.PAGETYPE_CHAT)) {
					// 对聊
					intent = new Intent(MessageFragment2.this.getActivity(), UserChatFragmentActivity.class);
					bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOUSERID, vo.getSubjectId());
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					startActivity(intent);
				} else if (tmp.getPagetype().equals(ChannelVo.PAGETYPE_PUB)) {
					// 公告
					if (tmp.getChannelType().equals(MsgsConstants.MC_NOTIFY) && tmp.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)) {
						// 游伴小助手
						intent = new Intent(MessageFragment2.this.getActivity(), SystemChatFragmentActivity.class);
						bundle = new Bundle();
						bundle.putLong(SystemConfig.BUNDLE_NAME_TOSYSID, vo.getSubjectId());
						bundle.putInt(SystemConfig.BUNDLE_NAME_TITLE, R.string.news_sys_name);
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					} else if (vo.getChannelType().equals(MsgsConstants.MC_PUB) && vo.getCategory().equals(MsgsConstants.MCC_OFFICIAL)) {
						// 系统公告
						intent = new Intent(MessageFragment2.this.getActivity(), SysOfficialChatFragmentActivity.class);
						bundle = new Bundle();
						bundle.putLong(SystemConfig.BUNDLE_NAME_TOSYSID, vo.getSubjectId());
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					} else if ((vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && vo.getCategory().equals(MsgsConstants.MCC_POST))
							|| (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && vo.getCategory().equals(MsgsConstants.MCC_FATE))) {
						// 精彩推荐
						intent = new Intent(MessageFragment2.this.getActivity(), SplendidRecommendFragmentActivity.class);
						bundle = new Bundle();
						bundle.putLong(SystemConfig.BUNDLE_NAME_TOSYSID, vo.getSubjectId());
						bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, vo.getCategory());
						bundle.putString(SystemConfig.BUNDLE_NAME_CHANNELTYPE, vo.getChannelType());
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					} else if (tmp.getChannelType().equals(MsgsConstants.MC_NOTIFY) && tmp.getCategory().equals(MsgsConstants.MCC_ACTIVITY)) {
						// 系统小秘
						intent = new Intent(MessageFragment2.this.getActivity(), SystemChatFragmentActivity.class);
						bundle = new Bundle();
						bundle.putLong(SystemConfig.BUNDLE_NAME_TOSYSID, vo.getSubjectId());
						bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, MsgsConstants.MCC_ACTIVITY);
						bundle.putInt(SystemConfig.BUNDLE_NAME_TITLE, R.string.news_activity_name);
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					} else if (tmp.getChannelType().equals(MsgsConstants.MC_NOTIFY) && tmp.getCategory().equals(MsgsConstants.MCC_PLAY)) {
						// 陪玩小助手
						intent = new Intent(MessageFragment2.this.getActivity(), SystemChatFragmentActivity.class);
						bundle = new Bundle();
						bundle.putLong(SystemConfig.BUNDLE_NAME_TOSYSID, vo.getSubjectId());
						bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, MsgsConstants.MCC_PLAY);
						bundle.putInt(SystemConfig.BUNDLE_NAME_TITLE, R.string.news_play_name);
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					}
				} else if (tmp.getPagetype().equals(ChannelVo.PAGETYPE_MCHAT)) {
					if (tmp.getChannelType().equals(MsgsConstants.MC_MCHAT)) {
						// 公会聊天室消息
						intent = new Intent(MessageFragment2.this.getActivity(), GroupChatFragmentActivity.class);
						bundle = new Bundle();
						bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, vo.getSubjectId());
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					} else if (tmp.getChannelType().equals(MsgsConstants.MC_PUB) && tmp.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)) {
						// 会长群发消息
						intent = new Intent(MessageFragment2.this.getActivity(), GroupMassMsgFragmentActivity.class);
						bundle = new Bundle();
						bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, vo.getSubjectId());
						intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
						startActivity(intent);
					}

				} else if (tmp.getPagetype().equals(ChannelVo.PAGETYPE_COMMENT)) {
					// 回复我的onItemClick positiononItemClick position
					intent = new Intent(MessageFragment2.this.getActivity(), TopicNotifyFragmentActivity.class);
					startActivity(intent);
				} else {
					LogUtil.d(TAG, "动态首页，单击item，该类型该版本暂时未支持:");
				}

			}
		} else if (showRule instanceof ChannelGroupVo) {
			ChannelGroupVo tmp = (ChannelGroupVo) showRule;
			if (tmp != null) {
				if (tmp.getPagetype().equals(ChannelVo.PAGETYPE_LIST)) {
					if (tmp.getType().equals(ChannelVo.TYPE_MCHAT)) {
						// 公会聊天室消息列表
						intent = new Intent(MessageFragment2.this.getActivity(), GroupChatListFragmentActivity.class);
						startActivity(intent);
					} else if (tmp.getType().equals(ChannelVo.TYPE_PUB)) {
						// 会长群发消息列表
						intent = new Intent(MessageFragment2.this.getActivity(), GroupMassMsgListFragmentActivity.class);
						startActivity(intent);
					} else {
						LogUtil.d(TAG, "动态首页，单击item，该类型该版本暂时未支持:");
					}
				} else {
					LogUtil.d(TAG, "动态首页，单击item，该类型该版本暂时未支持:");
				}

			}
		}
	}

}
