/**      
 * ReceiverMessageBroadcastReceiver.java Create on 2014-3-25     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.receiver;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iwgame.msgs.MainFragmentActivity;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: ReceiverMessageBroadcastReceiver
 * @Description: 接受到消息的广播接收器
 * @author chuanglong
 * @date 2014-3-25 下午12:24:51
 * @Version 1.0
 * 
 */
public class ReceiverMessageBroadcastReceiver extends BroadcastReceiver {

	final static String TAG = ReceiverMessageBroadcastReceiver.class.getName();

	private static Map<String, Long> lastGroupReciverTimeMap = new HashMap<String, Long>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(final Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle bundle = getResultExtras(true);
		Bundle bundle2 = intent.getExtras();
		boolean isMessagePage = false;
		MainFragmentActivity th = MainFragmentActivity.getInstance();
		if(th != null && th.getmTabHost() != null && th.getmTabHost().getCurrentTab() == 0)
			isMessagePage = true;
		if (bundle != null && !isMessagePage) {
			// 是否有后续数据
			boolean isHaveFollowUpData = bundle.getBoolean(SystemConfig.BUNDLE_NAME_ISHAVEFOLLOWUPDATA, false);
			boolean isChatActivity = bundle.getBoolean(SystemConfig.BUNDLE_NAME_ISCHATACTIVITY_RECEIVE, false);
			// LogUtil.d("消息处理",
			// "消息的广播ReceiverMessageBroadcastReceiver；isChatActivity=" +
			// isChatActivity);
			// LogUtil.d("消息处理",
			// "消息的广播ReceiverMessageBroadcastReceiver；isHaveFollowUpData=" +
			// isHaveFollowUpData);
			// boolean isMainActivity =
			// bundle.getBoolean(SystemConfig.BUNDLE_NAME_ISMAINACTIVITY_RECEIVE,false);
			Serializable data = bundle2.getSerializable(SystemConfig.BUNDLE_NAME_MESSAGEVO);
			if (data != null) {
				final MessageVo vo = (MessageVo) data;
				if(!SystemContext.APPTYPE.equals(vo.getCurrnetApptype()) || vo.getNotNotify() == 1){
					return;
				}
				ExtUserVo uvo = SystemContext.getInstance().getExtUserVo();
				if (vo.getChannelType().equals(MsgsConstants.MC_CHAT)) {
					// 对聊
					// 未发送到对应的窗口，没有后续数据
					if (!isChatActivity && !isHaveFollowUpData) {
						if (SystemContext.getInstance().getMsgGlobalOffOn()
								&& SystemContext.getInstance().getMsgChatOffOn() && uvo != null && uvo.getUserid() != vo.getFromId()
								&& vo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
							// 发送通知到任务栏
							ProxyFactory.getInstance().getMessageNotificationProxy().sendMessageToNotificationOpenChat(context, vo);
						}
						// //增加未读数
					}
				} else if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && (vo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE) || vo.getCategory().equals(MsgsConstants.MCC_PLAY))) {
					// 游伴小助手
					if (!isChatActivity && !isHaveFollowUpData && SystemContext.getInstance().getMsgGlobalOffOn()) {
						// 发送到任务栏
						ProxyFactory.getInstance().getMessageNotificationProxy().sendMessageToNotificationOpenSystemChat(context, vo);
						// //增加未读数
					}

				} else if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && vo.getCategory().equals(MsgsConstants.MCC_POST)) {
					// 精彩推荐
					if (!isChatActivity && !isHaveFollowUpData && SystemContext.getInstance().getMsgGlobalOffOn()
							&& SystemContext.getInstance().getWonderfullOffOn()) {
						// 发送到任务栏
						ProxyFactory.getInstance().getMessageNotificationProxy().sendMessageToNotificationOpenSplendidChat(context, vo);
					}
					sendPushIdString(vo, context);
				}  else if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && vo.getCategory().equals(MsgsConstants.MCC_FATE)) {
					// 缘分好友
					if (!isChatActivity && !isHaveFollowUpData && SystemContext.getInstance().getMsgGlobalOffOn()
							&& SystemContext.getInstance().getFateRecommendOffOn()) {
						// 发送到任务栏
						ProxyFactory.getInstance().getMessageNotificationProxy().sendMessageToNotificationOpenSplendidChat(context, vo);
					}
					sendPushIdString(vo, context);
				} else if (vo.getChannelType().equals(MsgsConstants.MC_PUB) && vo.getCategory().equals(MsgsConstants.MCC_OFFICIAL)) {
					// 系统公告
					if (!isChatActivity && !isHaveFollowUpData) {
						// 发送到任务栏
						ProxyFactory.getInstance().getMessageNotificationProxy().sendMessageToNotificationOpenSysOfficialChat(context, vo);
						// //增加未读数
					}

				} else if (vo.getChannelType().equals(MsgsConstants.MC_MCHAT) && vo.getCategory().equals(MsgsConstants.MCC_CHAT)
						&& vo.getSubjectDomain().equals(MsgsConstants.DOMAIN_GROUP)  && SystemContext.getInstance().getMsgGlobalOffOn() && SystemContext.getInstance().getMsgChatOffOn()) {
					// 公会群聊消息
					// 去除后续有消息不通知的判断，2014.08.07
					// && !isHaveFollowUpData
					if (!isChatActivity) {
						String mtkey = getLastMessageTime(vo.getChannelType(), vo.getSubjectId(), vo.getSubjectDomain(), vo.getCategory());
						long lastTime = 0;
						if (lastGroupReciverTimeMap.containsKey(mtkey)) {
							lastTime = lastGroupReciverTimeMap.get(mtkey);
						}
						long nowTme = System.currentTimeMillis();
						if ((nowTme - lastTime) >= SystemContext.getInstance().getGCRT()) {
							// 公会聊天消息提醒的规则 5条提醒，后面每次隔10条提醒一次
							// 增加消息开关的判断
							// 获得公会信息
							ProxyCallBack<List<GroupVo>> callback2 = new ProxyCallBack<List<GroupVo>>() {

								@Override
								public void onSuccess(List<GroupVo> result) {
									if(result != null && result.size() > 0){
										GroupVo groupVo = result.get(0);
										if (groupVo != null && groupVo.getMsgoffon() == 1) {// 提醒消息
											// 发送到任务栏
											ProxyFactory.getInstance().getMessageNotificationProxy().sendMessageToNotificationOpenGroupChat(context, vo);
										}
									}
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									LogUtil.e(TAG, "获得公会信息异常：" + result);

								}
							};
							GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
							GroupVo gvo = groupDao.findGroupByGrid(vo.getSubjectId());
							ContentDetailParams.Builder p = ContentDetailParams.newBuilder();
							ContentDetailParam.Builder cdp = ContentDetailParam.newBuilder();
							cdp.setId(vo.getSubjectId());
							cdp.setUtime(gvo == null ? 0 : gvo.getUtime());
							p.addParam(cdp.build());
							ProxyFactory.getInstance().getGroupProxy().getGroupDetailInfo(callback2, null, p.build(), MsgsConstants.OT_GROUP, null);
						}
						lastGroupReciverTimeMap.put(mtkey, nowTme);
					}
				} else if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && vo.getCategory().equals(MsgsConstants.MCC_COMMENT)  && SystemContext.getInstance().getMsgGlobalOffOn()) {
					// 评论我的
					if (!isChatActivity && !isHaveFollowUpData) {
						if (SystemContext.getInstance().getMsgGlobalOffOn()
								&& SystemContext.getInstance().getCommentMyOffOn()) {
							// 发送到任务栏
							ProxyFactory.getInstance().getMessageNotificationProxy().sendMessageToNotificationOpenGyMy(context, vo);
						}
						// //增加未读数
						// SystemContext.getInstance().addSubjectUnReadCount(vo.getChannelType(),
						// vo.getSubjectId(), vo.getSubjectDomain(),
						// vo.getCategory());

					}
				} else if (vo.getChannelType().equals(MsgsConstants.MC_PUB) && vo.getCategory().equals(MsgsConstants.MCC_ANNOUNCE)
						&& vo.getSubjectDomain().equals(MsgsConstants.DOMAIN_GROUP) && SystemContext.getInstance().getMsgGlobalOffOn()) {
					// 会长群发消息
					if (!isChatActivity && !isHaveFollowUpData && SystemContext.getInstance().getMsgGlobalOffOn()) {
						// 发送到任务栏
						ProxyFactory.getInstance().getMessageNotificationProxy().sendMessageToNotificationOpenGroupMassMsg(context, vo);
						// //增加未读数
						// SystemContext.getInstance().addSubjectUnReadCount(vo.getChannelType(),
						// vo.getSubjectId(), vo.getSubjectDomain(),
						// vo.getCategory());

					}
				}
			}
		}
		setResultExtras(bundle);
	}

	/**
	 * 发送PUSH接受消息
	 * 
	 * @param vo
	 * @param context
	 */
	private void sendPushIdString(MessageVo vo, Context context) {
		if (vo != null) {
			String content = vo.getContent();
			if (content != null) {
				try {
					JSONObject o = new JSONObject(content);
					long pushid = Long.valueOf(o.get("pushId").toString());
					ProxyCallBack<Integer> callback = new ProxyCallBack<Integer>() {
						@Override
						public void onSuccess(Integer result) {
							if (result == Msgs.ErrorCode.EC_OK_VALUE) {
								LogUtil.i(TAG, "PUSH统计成功");
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							LogUtil.e(TAG, "PUSH统计异常" + result);
						}
					};
					ProxyFactory.getInstance().getUserProxy().SendPushCount(callback, context, pushid, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取某个消息类型的最后消息key
	 * 
	 * @param channelType
	 * @param subjectid
	 * @param subjectDomain
	 * @param category
	 * @return
	 */
	private String getLastMessageTime(String channelType, long subjectid, String subjectDomain, String category) {
		return "lastmessagetime_" + channelType + "_" + category + "_" + subjectDomain + "_" + subjectid;
	}

}
