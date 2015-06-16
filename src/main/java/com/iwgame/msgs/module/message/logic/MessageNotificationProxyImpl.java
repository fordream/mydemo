/**      
 * MessageNotificationProxyImpl.java Create on 2014-3-25     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.message.logic;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;

import com.iwgame.msgs.MessageTransitActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.chat.ui.GroupChatFragmentActivity;
import com.iwgame.msgs.module.chat.ui.GroupMassMsgFragmentActivity;
import com.iwgame.msgs.module.chat.ui.SplendidRecommendFragmentActivity;
import com.iwgame.msgs.module.chat.ui.SysOfficialChatFragmentActivity;
import com.iwgame.msgs.module.chat.ui.SystemChatFragmentActivity;
import com.iwgame.msgs.module.chat.ui.UserChatFragmentActivity;
import com.iwgame.msgs.module.message.ui.MessageFragment2;
import com.iwgame.msgs.module.postbar.ui.ReplyMyActivity;
import com.iwgame.msgs.module.postbar.ui.TopicNotifyFragmentActivity;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.ErrorCodeUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.MyTagUtil;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.LogUtil;
import com.youban.msgs.R;

/**
 * @ClassName: MessageNotificationProxyImpl
 * @Description: 消息通知代理实现类
 * @author chuanglong
 * @date 2014-3-25 下午2:27:49
 * @Version 1.0
 * 
 */
public class MessageNotificationProxyImpl implements MessageNotificationProxy {
	private static String TAG = "MessageNotificationProxyImpl";
	private static byte[] lock = new byte[0];

	private static MessageNotificationProxyImpl instance = null;

	public static MessageNotificationProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new MessageNotificationProxyImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageNotificationProxy#
	 * sendMessageToNotificationOpenChat(android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessageToNotificationOpenChat(final Context context, final MessageVo vo) {
		// 获得用户信息
		ProxyCallBack<List<UserVo>> callback = new ProxyCallBack<List<UserVo>>() {

			@Override
			public void onSuccess(List<UserVo> uservoList) {
				if (uservoList == null || uservoList.size() <= 0)
					return;
				UserVo result = uservoList.get(0);
				// TODO Auto-generated method stub
				if (result != null) {
					Intent intent = new Intent(context, MessageTransitActivity.class);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS, UserChatFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOUSERID, vo.getSubjectId());
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					String tickerText = vo.getSummary();
					String title = "消息";

					String summary = vo.getSummary();
					String content = result.getUsername() + "：" + summary;
					// 对关注消息处理，按照［iwgame:follow］截取
					if (summary.contains(MyTagUtil.TAG_FOLLOW)) {
						UserVo uvo = SystemContext.getInstance().getExtUserVo();
						if (uvo != null && vo.getFromId() == uvo.getUserid()) {
							summary = summary.substring(0, summary.indexOf(MyTagUtil.TAG_FOLLOW));
							summary = summary.replaceAll(MyTagUtil.TAG_SEX_REG, "");
						} else {
							summary = summary.substring(summary.indexOf(MyTagUtil.TAG_FOLLOW) + 15);
							summary = summary.replaceAll(MyTagUtil.TAG_SEX_REG, "");
						}
						content = summary;
						tickerText = summary;
					}
					sendMessageToNotification(context, intent, tickerText, title, content);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "获得用户信息异常：" + result);
				ErrorCodeUtil.handleErrorCode(context, result, resultMsg);
			}
		};
		UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		UserVo uvo = userDao.getUserById(vo.getSubjectId());
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(vo.getSubjectId());
		cdp.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(callback, null, p.build(), 0, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageNotificationProxy#
	 * sendMessageToNotificationOpenSystemChat(android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessageToNotificationOpenSystemChat(Context context, MessageVo vo) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, MessageTransitActivity.class);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS, SystemChatFragmentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOUSERID, vo.getSubjectId());
		if (vo.getCategory().equals(MsgsConstants.MCC_PLAY)) {
			bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, MsgsConstants.MCC_PLAY);
			bundle.putInt(SystemConfig.BUNDLE_NAME_TITLE, R.string.news_play_name);
		}
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		String tickerText = vo.getSummary();
		String title = "系统消息";
		String content = "系统：" + vo.getSummary();
		sendMessageToNotification(context, intent, tickerText, title, content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageNotificationProxy#
	 * sendMessageToNotificationOpenSystemChat(android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessageToNotificationOpenSplendidChat(Context context, MessageVo vo) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, MessageTransitActivity.class);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS, SplendidRecommendFragmentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOSYSID, vo.getSubjectId());
		bundle.putString(SystemConfig.BUNDLE_NAME_CATEGORY, vo.getCategory());
		bundle.putString(SystemConfig.BUNDLE_NAME_CHANNELTYPE, vo.getChannelType());
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		String tickerText = vo.getSummary();
		String title = "系统消息";
		String content = "系统：" + vo.getSummary();
		sendMessageToNotification(context, intent, tickerText, title, content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageNotificationProxy#
	 * sendMessageToNotificationOpenGroupChat(android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessageToNotificationOpenGroupChat(final Context context, final MessageVo vo) {
		// TODO Auto-generated method stub
		// 获得公会信息
		ProxyCallBack<List<GroupVo>> callback = new ProxyCallBack<List<GroupVo>>() {

			@Override
			public void onSuccess(List<GroupVo> groupvoresult) {
				if (groupvoresult == null || groupvoresult.size() <= 0)
					return;
				// TODO Auto-generated method stub
				GroupVo result = groupvoresult.get(0);
				if (result != null) {
					Intent intent = new Intent(context, MessageTransitActivity.class);
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS, GroupChatFragmentActivity.class);
					Bundle bundle = new Bundle();
					bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, vo.getSubjectId());
					intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
					String tickerText = result.getName() + "聊天室有新消息了";
					String title = "消息";
					String content = result.getName() + "聊天室有新消息了";
					sendMessageToNotification(context, intent, tickerText, title, content);
				}
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.e(TAG, "获得公会信息异常：" + result);
			}

		};
		GroupDao groupdao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		GroupVo gvo = groupdao.findGroupByGrid(vo.getSubjectId());
		ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
		ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
		cdp.setId(vo.getSubjectId());
		cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
		p.addParam(cdp.build());
		ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback, context, p.build(), 5, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageNotificationProxy#
	 * sendMessageToNotificationOpenGyMy(android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessageToNotificationOpenGyMy(Context context, MessageVo vo) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, MessageTransitActivity.class);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS, TopicNotifyFragmentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(SystemConfig.BUNDLE_NAME_MY_TAB_SELECT, SystemConfig.MY_TAB_VALUE_COMMENTMY);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		String tickerText = vo.getSummary();
		String title = "评论或赞消息";
		String content = vo.getSummary();
		sendMessageToNotification(context, intent, tickerText, title, content);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageNotificationProxy#
	 * sendMessageToNotificationOpenGroupMassMsg(android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessageToNotificationOpenGroupMassMsg(Context context, MessageVo vo) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, MessageTransitActivity.class);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS, GroupMassMsgFragmentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOGROUPID, vo.getSubjectId());
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		String tickerText = vo.getSummary();
		String title = "会长群发消息";
		String content = vo.getSummary();
		sendMessageToNotification(context, intent, tickerText, title, content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageNotificationProxy#
	 * sendMessageToNotificationOpenMain(android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessageToNotificationOpenMain(Context context, MessageVo vo) {
		// TODO Auto-generated method stub

	}

	/**
	 * 通知消息到消息栏和下拉列表
	 * 
	 * @param intent
	 *            点击打开的intent
	 * @param tickerText
	 *            显示在消息栏上的
	 * @param title
	 *            显示下拉列表中，的标题
	 * @param content
	 *            显示下拉列表中的内容
	 */
	private void sendMessageToNotification(Context context, Intent intent, String tickerText, String title, String content) {
		if (intent != null) {
			LogUtil.d(TAG, "----消息通知到消息栏,消息：" + "tickerText=" + tickerText + ", title=" + title + ", content=" + content);
			SpannableString tickerSS = null;
			SpannableString contentSS = null;
			if (tickerText != null) {
				tickerSS = MyTagUtil.analyseMessageTag(context, tickerText,
						context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width), context.getResources()
								.getDimensionPixelSize(R.dimen.global_string_smiley_heigth));
			}
			if (content != null) {
				contentSS = MyTagUtil.analyseMessageTag(context, content,
						context.getResources().getDimensionPixelSize(R.dimen.global_string_smiley_width), context.getResources()
								.getDimensionPixelSize(R.dimen.global_string_smiley_heigth));
			}
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_launcher_nf;
			notification.tickerText = tickerSS;

			if (SystemContext.getInstance().getSoundOffOn()) {
				// true ,有声音
				notification.defaults = Notification.DEFAULT_SOUND;
				LogUtil.d(TAG, "----开启系统提示声音");
			} else {
				// false ,没有声音
				LogUtil.d(TAG, "----关闭系统提示声音");
			}
			notification.flags |= Notification.FLAG_AUTO_CANCEL;// 通知点击后，自动消失
			// notification.flags |= Notification.FLAG_NO_CLEAR; //
			// 点击“clear“，不清除该消息
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			// 系统默认布局
			notification.setLatestEventInfo(SystemContext.getInstance().getContext(), title, contentSS, contentIntent);
			// 发出通知
			NotificationManager nm = (NotificationManager) SystemContext.getInstance().getContext()
					.getSystemService(SystemContext.getInstance().getContext().NOTIFICATION_SERVICE);
			// Base notification id
			nm.notify(SystemConfig.NOTIFICATION_ID_BASE, notification);
		} else {
			LogUtil.d(TAG, "----通知消息到消息栏时：Intent 不可以为空");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageNotificationProxy#
	 * sendMessageToNotificationOpenSysOfficialChat(android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessageToNotificationOpenSysOfficialChat(Context context, MessageVo vo) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, MessageTransitActivity.class);
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME_CLASS, SysOfficialChatFragmentActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(SystemConfig.BUNDLE_NAME_TOSYSID, vo.getSubjectId());
		intent.putExtra(SystemConfig.BUNDLEEXTRA_NAME, bundle);
		String tickerText = vo.getSummary();
		String title = "系统消息";
		String content = "系统：" + vo.getSummary();
		sendMessageToNotification(context, intent, tickerText, title, content);
	}

}
