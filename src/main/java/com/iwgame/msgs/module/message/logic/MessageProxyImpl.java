/**      
 * MessageProxyImpl.java Create on 2013-9-24     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.message.logic;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.protobuf.ByteString;
import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.config.SystemConfig;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.MessageDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.message.ui.MessageDialogManager;
import com.iwgame.msgs.module.setting.vo.MessageSubjectRuleVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ChatMessageResponse;
import com.iwgame.msgs.proto.Msgs.ForwardInfo;
import com.iwgame.msgs.proto.Msgs.MessageContent;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.OID;
import com.iwgame.msgs.proto.Msgs.SyncMessage;
import com.iwgame.msgs.proto.Msgs.SyncMessageNotification;
import com.iwgame.msgs.proto.Msgs.SyncMessageResponse;
import com.iwgame.msgs.proto.Msgs.UserSubscribeChannelList;
import com.iwgame.msgs.service.msgbox.MessageCode;
import com.iwgame.msgs.utils.BuildVoUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.MyTagUtil;
import com.iwgame.msgs.utils.OIDUtil;
import com.iwgame.msgs.utils.SessionUtil;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.sdk.xaction.XActionSessionStatusListener.XActionSessionStatus;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: MessageProxyImpl
 * @Description: 消息逻辑处理类
 * @author 王卫
 * @date 2013-9-24 下午6:03:23
 * @Version 1.0
 * 
 */
public class MessageProxyImpl implements MessageProxy {

	private static String TAG = "MessageProxyImpl";
	private static byte[] lock = new byte[0];

	private static MessageProxyImpl instance = null;

	/**
	 * 移除正在同步数据的标识
	 * 
	 * @param key
	 */
	private void removeSyncingChannel(Context context, String key) {

		SystemContext.getInstance().getChannelSyncing().remove(key);
		// 丢弃过再次补发一次同步数据
		if (SystemContext.getInstance().getChannelDiscard().containsKey(key)) {
			LogUtil.debug(String.format("通道[%s]补发一次同步数据", key));
			SystemContext.getInstance().getChannelDiscard().remove(key);
			syncMessage(context, key, true);

		} else {
			LogUtil.debug(String.format("通道[%s]同步数据结束", key));
		}
	}

	private void addSyncingChannel(Context context, String key) {
		if (!SystemContext.getInstance().getChannelSyncing().containsKey(key)) {
			SystemContext.getInstance().getChannelSyncing().put(key, 1);
			LogUtil.debug(String.format("通道[%s]开始同步数据", key));
			// 发出同步
			syncMessage(context, key, false);
		} else {
			if (!SystemContext.getInstance().getChannelDiscard().containsKey(key))
				SystemContext.getInstance().getChannelDiscard().put(key, 1);
			LogUtil.debug(String.format("通道[%s]已经在同步数据中", key));
		}
	}

	private MessageProxyImpl() {

	}

	public static MessageProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new MessageProxyImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.MessageProxy#getUserSubscribeChannel(android
	 * .content.Context)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getUserSubscribeChannel(final Context context) {
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// 发出同步通道的请求
				ServiceCallBack<XActionResult> callback = new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub
						if (result != null && result.hasExtension(Msgs.subscribeChannelResponse)) {
							handleUserSubscribeChannelList(context, result.getExtension(Msgs.subscribeChannelResponse));
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						LogUtil.error("同步通道响应异常：" + result);
						if (SessionUtil.getLongClient() != null && XActionSessionStatus.SESSION_OPENED.equals(SessionUtil.status)) {
							// 重新发起
							ProxyFactory.getInstance().getMessageProxy().getUserSubscribeChannel(context);
							// ErrorCodeUtil.handleErrorCode(context,
							// result,resultMsg);
						}
					}
				};
				ServiceFactory.getInstance().getMessageRemoteService().getUserSubscribeChannel(callback, null);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.MessageProxy#handleSyncMessageNotification
	 * (android.content.Context,
	 * com.iwgame.msgs.proto.Msgs.SyncMessageNotification)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void handleSyncMessageNotification(final Context context, final SyncMessageNotification notification) {
		LogUtil.debug("--处理同步消息通知：apptype:" + SystemContext.APPTYPE + ", notification:" + notification.toString());
		if (notification == null)
			return;
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// 聊天消息同步通知syncNotification，增加判断当前消息通道是否正在同步数据， 如果正在同步，则该通知丢弃掉
				addSyncingChannel(context, notification.getMcode());
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.logic.MessageProxy#sendMessageSaveToLocal
	 * (android.content.Context, com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public MessageVo sendMessageSaveToLocal(Context context, MessageVo vo) {
		SystemContext.getInstance().setHasNewMessage(true);
		// 先入库
		MessageDao dao = DaoFactory.getDaoFactory().getMessageDao(context);
		return dao.insert(vo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.MessageProxy#sendMessage(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo)
	 */
	@Override
	public void sendMessage(final ProxyCallBack<MessageVo> callback, final Context context, MessageVo messageVo) {
		sendMessage(callback, context, messageVo, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.logic.MessageProxy#sendMessage(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context,
	 * com.iwgame.msgs.vo.local.MessageVo, boolean)
	 */
	@Override
	public void sendMessage(final ProxyCallBack<MessageVo> callback, final Context context, final MessageVo vo, final boolean setApptype) {
		final MyAsyncTask<MessageVo> asyncTask = new MyAsyncTask<MessageVo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final MessageDao dao = DaoFactory.getDaoFactory().getMessageDao(context);
				final int id = vo.getId();
				ServiceCallBack<XActionResult> serviceCallback = new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.chatResponse)) {
							ChatMessageResponse response = result.getExtension(Msgs.chatResponse);
							// 更新库
							long msgid = response.getMsgid();
							long createTime = response.getTimestamp();

							long msgindex = response.getMsgIndex();

							MessageContent content = response.getContent();
							String tmpContent = null;
							int tmpContentType = content.getContentType().getNumber();
							switch (content.getContentType().getNumber()) {
							case MessageContentType.TEXT_VALUE:
								break;
							case MessageContentType.HTML_TEXT_VALUE:
								break;
							case MessageContentType.IMAGE_VALUE:// 忽略
								break;
							case MessageContentType.VOICE_VALUE:

							case MessageContentType.MV_VALUE:// 忽略
								break;
							case MessageContentType.FILE_VALUE:// 忽略
								break;
							case MessageContentType.IMAGE_ID_REF_VALUE:
								tmpContent = content.getResourceId();
								break;
							case MessageContentType.VOICE_ID_REF_VALUE:
								tmpContentType = MessageContentType.VOICE_VALUE;
								tmpContent = vo.getContent();
								break;

							case MessageContentType.MV_ID_REF_VALUE:
								break;
							case MessageContentType.FILE_ID_REF_VALUE:
								break;
							case MessageContentType.CONTENT_ID_VALUE:// 忽略
								break;
							case MessageContentType.NEWS_TEXT_VALUE:
								break;
							}
							dao.updateById(id, msgid, msgindex, createTime, tmpContentType, tmpContent, MessageVo.STATUS_SENDSUCC);
							MessageVo mvo = dao.getMessageByMsgId(msgid);
							asyncTask.getProxyCallBack().onSuccess(mvo);
						} else if (result.getRc() == Msgs.ErrorCode.EC_MSGS_MSG_POST_NORIGHT_VALUE) {
							dao.updateById(id, 0, 0, 0, 0, null, MessageVo.STATUS_SENDERR);
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

						else {
							dao.updateById(id, 0, 0, 0, 0, null, MessageVo.STATUS_SENDERR);
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						LogUtil.error("发送消息响应异常：" + result);
						if (result == Msgs.ErrorCode.EC_MSGS_MSG_POST_NORIGHT_VALUE) {
							// dao.delMessageById(id);
							dao.updateById(id, 0, 0, 0, 0, null, MessageVo.STATUS_SENDERR);
							asyncTask.getProxyCallBack().onFailure(result, resultMsg);
						}

						else {
							dao.updateById(id, 0, 0, 0, 0, null, MessageVo.STATUS_SENDERR);
							asyncTask.getProxyCallBack().onFailure(result, resultMsg);
						}

					}
				};
				OID from = OIDUtil.buildOID(vo.getFromId(), vo.getFromDomain());
				OID to = OIDUtil.buildOID(vo.getToId(), vo.getToDomain());
				MessageContent.Builder contentBuilder = MessageContent.newBuilder();
				contentBuilder.setContentType(MessageContentType.valueOf(vo.getContentType()));
				if (setApptype)
					contentBuilder.setApptype(SystemContext.APPTYPE);
				switch (contentBuilder.getContentType().getNumber()) {
				case MessageContentType.TEXT_VALUE:
					contentBuilder.setText(vo.getContent());
					break;
				case MessageContentType.HTML_TEXT_VALUE:
					contentBuilder.setText(vo.getContent());
					break;
				case MessageContentType.IMAGE_VALUE:
					contentBuilder.setImage(ByteString.copyFrom(vo.getContentBytes()));
					break;
				case MessageContentType.VOICE_VALUE:
					contentBuilder.setVoice(ByteString.copyFrom(vo.getContentBytes()));
					break;
				case MessageContentType.MV_VALUE:
					contentBuilder.setMv(ByteString.copyFrom(vo.getContentBytes()));
					break;
				case MessageContentType.FILE_VALUE:
					contentBuilder.setFile(ByteString.copyFrom(vo.getContentBytes()));
					break;
				case MessageContentType.IMAGE_ID_REF_VALUE:
					contentBuilder.setResourceId(vo.getContent());
				case MessageContentType.VOICE_ID_REF_VALUE:
				case MessageContentType.MV_ID_REF_VALUE:
				case MessageContentType.FILE_ID_REF_VALUE:
					contentBuilder.setResourceId(vo.getContent());
					break;
				case MessageContentType.CONTENT_ID_VALUE:
					contentBuilder.setContentId(Integer.parseInt(vo.getContent()));
					break;
				case MessageContentType.NEWS_TEXT_VALUE:
					contentBuilder.setText(vo.getContent());
					break;
				}
				ForwardInfo forwardInfo = null;
				if (vo.getForwardId() > 0) {
					ForwardInfo.Builder forwardInfoBuilder = ForwardInfo.newBuilder();
					forwardInfoBuilder.setForwardId(vo.getForwardId());
					forwardInfoBuilder.setForwardType(vo.getForwardType());
					forwardInfo = forwardInfoBuilder.build();
				}

				ServiceFactory
						.getInstance()
						.getMessageRemoteService()
						.sendChatMessage(serviceCallback, context, from, to, vo.getChannelType(), contentBuilder.build(), vo.getSummary(),
								vo.getPosition(), forwardInfo);
				SystemContext.getInstance().setHasNewMessage(true);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.MessageProxy#delMessage(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	@Override
	public int delMessage(String channelType, long subjectid, String subjectDomain, String category) {
		return DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext())
				.delMessage(channelType, subjectid, subjectDomain, category);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.logic.MessageProxy#getMessage(com.iwgame
	 * .msgs.common.ProxyCallBack, java.lang.String, long, java.lang.String,
	 * java.lang.String, long, int, boolean, long, int, int)
	 */
	@Override
	public void getMessage(final ProxyCallBack<List<MessageVo>> callback, final String channelType, final long subjectid, final String subjectDomain,
			final String category, final long msgindex, final int limit, final boolean isGetDataFromServer, final long minIndex, final int estimateop) {
		final MyAsyncTask<List<MessageVo>> asyncTask = new MyAsyncTask<List<MessageVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// 首先从本地获得，本地不够就从网络补，直到补的足够了才返回到前端
				final List<MessageVo> list = DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext())
						.getMessage(channelType, subjectid, subjectDomain, category, msgindex, limit, minIndex, estimateop);
				if(MsgsConstants.MC_NOTIFY.equals(channelType) && MsgsConstants.DOMAIN_USER.equals(subjectDomain) && MsgsConstants.MCC_COMMENT.equals(category)){
					// 返回list
					asyncTask.getProxyCallBack().onSuccess(list);
					return null;
				}
				// 不显示的条数
				int invisibleCount = 0;
				// 断开的大的msgIndex
				long breakMaxMsgIndex = msgindex;
				// 断开的小的msgIndex
				long breakMinMsgIndex = 0;
				/**
				 * 开始产生断档的索引
				 */
				int startbreakIndex = -1;

				// 确认断开的数据有没有，记录其实删除的数据和不显示的数据条数
				for (int i = 0; i < list.size(); i++) {
					MessageVo tmpvo = list.get(i);

					// 判断是否断档
					if (breakMaxMsgIndex == Integer.MAX_VALUE) {
						breakMaxMsgIndex = tmpvo.getMsgIndex();
					} else {
						if (breakMaxMsgIndex == tmpvo.getMsgIndex() + 1 || breakMaxMsgIndex == tmpvo.getMsgIndex()) {
							breakMaxMsgIndex = tmpvo.getMsgIndex();
						} else {
							if (minIndex >= breakMaxMsgIndex - 1) {
								// 这个断档是正常的，不需要到服务器去拉数据
								// isGetDataFromServer = false ;
							} else {
								// 产生了断档
								startbreakIndex = i;
								breakMinMsgIndex = tmpvo.getMsgIndex();
								break;
							}
						}
					}

					// 判断是否需要显示
					if (tmpvo.getStatus() == MessageVo.STATUS_DEL) {
						invisibleCount++;
					}
				}
				// 判断是否需要到服务器上补数据
				if (!isGetDataFromServer) {
					// 不需要到服务端补数据
					// 判断是否有断档
					if (startbreakIndex != -1) {
						// 有断档，清空断档后，返回
						for (int i = list.size() - 1; i >= startbreakIndex; i--) {
							list.remove(i);
						}
						// 返回list
						asyncTask.getProxyCallBack().onSuccess(list);
					} else {
						// 没有断档
						if (list.size() >= -limit && list.size() - invisibleCount < -limit && list.get(list.size() - 1).getMsgIndex() != 1) {
							// 本地 返回的用于显示的条数不够，需要补数据 ；暂时先不考虑
							// 返回list
							asyncTask.getProxyCallBack().onSuccess(list);
						} else {
							// 不需要再次读本地的条数
							// 返回list
							asyncTask.getProxyCallBack().onSuccess(list);
						}
					}
				} else {
					// 需要到服务端补数据
					// 判断是否有断档
					if (startbreakIndex != -1 || list.size() == 0) {
						// 有断档，到服务器补数据
						if (list.size() > 0) {
							for (int i = list.size() - 1; i >= startbreakIndex; i--) {
								list.remove(i);
							}
						}

						int breaklimit = list.size() == 0 ? limit : limit + (startbreakIndex - invisibleCount);
						final int successLimit = limit - invisibleCount;
						ProxyCallBack<Integer> callback2 = new ProxyCallBack<Integer>() {

							@Override
							public void onSuccess(Integer result) {
								// TODO Auto-generated method stub
								// 补回来成功,重新得到数据
								List<MessageVo> newlist = DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext())
										.getMessage(channelType, subjectid, subjectDomain, category, msgindex, successLimit, minIndex);
								asyncTask.getProxyCallBack().onSuccess(newlist);
							}

							@Override
							public void onFailure(Integer result, String resultMsg) {
								// TODO Auto-generated method stub
								// 补数据失败,把有的数据返回过去，并且返回错误信息
								if (list.size() > 0) {
									asyncTask.getProxyCallBack().onSuccess(list);
								}
								asyncTask.getProxyCallBack().onFailure(result, "拉取数据异常，code：" + result + "，请重试");
							}
						};

						getChatHistoryMessageForServer(callback2, SystemContext.getInstance().getContext(), channelType, SystemContext.getInstance()
								.getExtUserVo().getUserid(), MsgsConstants.DOMAIN_USER, subjectid, subjectDomain, category, breakMaxMsgIndex,
								breaklimit, minIndex);

					} else {
						// 没有断档
						if (list.size() >= -limit && list.size() - invisibleCount < -limit && list.get(list.size() - 1).getMsgIndex() != 1) {
							// 本地 返回的用于显示的条数不够，需要补数据 ；暂时先不考虑
							// 返回list
							asyncTask.getProxyCallBack().onSuccess(list);
						} else {
							// 不需要再次读本地的条数
							// 返回list
							asyncTask.getProxyCallBack().onSuccess(list);
						}
					}

				}
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.MessageProxy#getMessage(java.lang.String,
	 * long, java.lang.String, java.lang.String, int, int, int)
	 */
	@Override
	public void getMessage(final ProxyCallBack<List<MessageVo>> callback, final String channelType, final long subjectid, final String subjectDomain,
			final String category, final long msgindex, final int limit, final boolean isGetDataFromServer, final long minIndex) {
		getMessage(callback, channelType, subjectid, subjectDomain, category, msgindex, limit, isGetDataFromServer, minIndex, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageProxy#getExtMessage()
	 */
	@Override
	public List<MessageVo> getSubjectLastMessage() {
		return DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext()).getSubjectLastMessage();
	}

	/**
	 * 处理通道同步结果
	 * 
	 * @param context
	 * @param response
	 */
	private void handleUserSubscribeChannelList(final Context context, UserSubscribeChannelList response) {
		// TODO Auto-generated method stub
		if (response == null)
			return;
		List<String> remoteList = response.getMcodesList();
		// 同步数据
		for (int i = 0; i < remoteList.size(); i++) {
			final MessageCode mcode = new MessageCode(remoteList.get(i));

			// 增加判断当前消息通道是否正在同步数据， 如果正在同步，则该通知丢弃掉
			addSyncingChannel(context, mcode.getMcode());
		}
	}

	/**
	 * 同步通道消息
	 * 
	 * @param context
	 * @param mcode
	 * @param isNeedIf
	 *            是否直接拉取数据，true：不判断，直接拉去[主要用于循环拉取数据]；false；需要根据公会的设置判断，是否拉数据[
	 *            主要用于第一次拉取数据]
	 */
	private void syncMessage(final Context context, final String mcodeStr, final boolean isNeedIf) {

		// 判断公会是否需要拉取信息
		final MessageCode mcode = new MessageCode(mcodeStr);
		// 非公会,或公会直接拉取的
		final ServiceCallBack<XActionResult> callback = new ServiceCallBack<XActionResult>() {

			@Override
			public void onSuccess(XActionResult result) {
				// TODO Auto-generated method stub
				if (result != null && result.hasExtension(Msgs.syncResponse))
					handleSyncMessageResponse(context, result.getExtension(Msgs.syncResponse), false);
				else
					removeSyncingChannel(context, mcodeStr);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.error("同步数据请求响应异常：" + result);
				// ErrorCodeUtil.handleErrorCode(context, result);
				// removeSyncingChannel(context,mcodeStr);
				// 重新发起(网络正常时）
				if (SessionUtil.getLongClient() != null
						&& XActionSessionStatus.SESSION_OPENED.equals(SessionUtil.status)
						&& (result == ErrorCode.EC_XACTION_ERROR || result == ErrorCode.EC_XACTION_NET_ERROR || result == ErrorCode.EC_XACTION_TIMEOUT_ERROR))
					syncMessage(context, mcodeStr, isNeedIf);
				else
					removeSyncingChannel(context, mcodeStr);

			}
		};

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ServiceFactory.getInstance().getMessageRemoteService().syncMessage(callback, context, mcodeStr, true);
	}

	/**
	 * 处理同步消息结果
	 * 
	 * @param context
	 * @param response
	 *            消息结果
	 * @param isHistory
	 *            是否是历史消息
	 */
	private synchronized void handleSyncMessageResponse(final Context context, SyncMessageResponse response, boolean isHistory) {

		LogUtil.d(TAG, "----处理同步的消息：apptype:" + SystemContext.APPTYPE + ", response:" + response.toString());
		UserVo uvo = SystemContext.getInstance().getExtUserVo();
		if (uvo == null)
			return;
		final String mCode = response.getMcode();
		// 获得通道类型
		String channelType = new MessageCode(mCode).getChannelType();
		// 通道同步数据到的数据的synckey
		long syncKey = response.getSyncKey();
		// 是否需要继续同步数据
		final boolean bool = response.getContinue();
		// 处理数据入库
		List<SyncMessage> messages = response.getMessageDatasList();

		// 上一次同步的index
		long lastIndex = response.getLastSyncKey();

		long subjectId = 0;
		String subjectDomain = "";
		String category = "";

		for (int i = 0; i < messages.size(); i++) {
			SyncMessage syncMessage = messages.get(i);
			// 接收到数据
			MessageVo vo = BuildVoUtil.SyncMessage2MessageVo(syncMessage, channelType, uvo.getUserid(), SystemContext.getInstance()
					.getMessageSubjectRuleList());
			// 入库(
			MessageDao dao = DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext());
			MessageVo tmpMessageVo = dao.getMessageByMsgId(vo.getMsgId());
			subjectId = vo.getSubjectId();
			subjectDomain = vo.getSubjectDomain();
			category = vo.getCategory();
			if (!isHistory && i == 0) {
				// 更新上一次的未读数
				if (SystemContext.getInstance().getSubjectLastUnReadMaxIndex(channelType, subjectId, subjectDomain, category) == -1) {
					SystemContext.getInstance().setSubjectLastUnReadMaxIndex(channelType, subjectId, subjectDomain, category, lastIndex);
				}
				// 判断是否需要更新能够读取历史 的最下值
				if (lastIndex > SystemContext.getInstance().getSubjectLocalMaxIndex(channelType, subjectId, subjectDomain, category)
						&& lastIndex > SystemContext.getInstance().getSubjectcanReadMinIndex(channelType, subjectId, subjectDomain, category)) {
					SystemContext.getInstance().setSubjectcanReadMinIndex(channelType, subjectId, subjectDomain, category, lastIndex);
				}
			}
			if (!isHistory && i == messages.size() - 1) {
				// 更新本地最大的index
				SystemContext.getInstance().setSubjectLocalMaxIndex(channelType, subjectId, subjectDomain, category, vo.getMsgIndex());
			}

			if (tmpMessageVo == null) {
				// 如果为info通知消息接受到直接设置为删除状态
				if (MsgsConstants.MCC_INFO.equals(vo.getCategory()) && MsgsConstants.MC_PUB.equals(vo.getChannelType())) {
					vo.setStatus(MessageVo.STATUS_DEL);
					handleReceiveInfoMessage(context, vo);
				} else if (vo.getFromId() == uvo.getUserid() && (vo.getEstimateop() == MsgsConstants.MESSAGE_OP_JOINGROUP_MSG || vo.getEstimateop() == MsgsConstants.MESSAGE_OP_JOIN_SERVERGROUP_MSG)) {// 加入公会消息（包括加入角色服务器公会）
					vo.setStatus(MessageVo.STATUS_DEL);
					// 更新已经读到的数值
					SystemContext.getInstance().setSubjectLastUnReadMaxIndex(channelType, vo.getToId(), vo.getToDomain(), category, vo.getMsgIndex());					
				}
				vo = dao.insert(vo);
				if (!isHistory) {
					if (vo.getStatus() != MessageVo.STATUS_DEL) {
						// 更新本次未读数
						SystemContext.getInstance().setSubjectUnReadMaxIndex(channelType, subjectId, subjectDomain, category, vo.getMsgIndex());
						// 发送到消息通知处理方法
						handleReceiveMessage(context, vo, (!bool && i == messages.size() - 1) ? false : true);
						// 设置赞和回复等消息是否有未读消息
						setHasUnreadMessage(vo);
					}
				}
			} else {
				String content = vo.getContent();
				if (vo.getStatus() != MessageVo.STATUS_DEL && content != null && content.contains(MyTagUtil.TAG_FOLLOW)) {
					// 更新本次已读数
					SystemContext.getInstance().setSubjectLastUnReadMaxIndex(channelType, subjectId, subjectDomain, category, vo.getMsgIndex());
					// 更新本次未读数
					SystemContext.getInstance().setSubjectUnReadMaxIndex(channelType, subjectId, subjectDomain, category, vo.getMsgIndex());
				}
			}
			SystemContext.getInstance().setHasNewMessage(true);

		}
		if (!isHistory) {
			if (syncKey != 0 && messages.size() > 0) {

				// 给出服务器ack响应
				ServiceCallBack<XActionResult> servicecallback = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub
						// 忽略，不做任何处理
						LogUtil.debug(String.format("---ack 响应成功，通道[%s],后续同步[%b]", mCode, bool));
						// 继续数据的同步
						if (bool) {
							// 发出消息同步请求

							syncMessage(context, mCode, true);
						} else {
							removeSyncingChannel(context, mCode);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						// 忽略，不做任何处理
						LogUtil.debug(String.format("---ack 响应失败，通道[%s],后续同步[%b]", mCode, bool));
						// 继续数据的同步
						if (bool) {
							// 发出消息同步请求
							try {
								Thread.sleep(80);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							syncMessage(context, mCode, true);
						} else {
							removeSyncingChannel(context, mCode);
						}
					}

				};
				ServiceFactory.getInstance().getMessageRemoteService().sendSyncAck(servicecallback, null, mCode, syncKey);

			} else {
				removeSyncingChannel(context, mCode);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.MessageProxy#delMessageById(int)
	 */
	@Override
	public int delMessageById(int id) {
		// TODO Auto-generated method stub
		MessageDao dao = DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext());
		// return dao.delMessageById(id);
		return dao.updateById(id, 0, 0, 0, 0, "", MessageVo.STATUS_DEL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.MessageProxy#updateMessageContentById(int,
	 * int, java.lang.String)
	 */
	@Override
	public int updateMessageContentById(int id, int contentType, String content) {
		// TODO Auto-generated method stub
		MessageDao dao = DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext());
		return dao.updateMessageContentById(id, contentType, content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.MessageProxy#getMessage(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MessageVo> getFromLastMessage(String channelType, long subjectid, String subjectDomain, String category) {
		// TODO Auto-generated method stub
		return DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext())
				.getFromLastMessage(channelType, subjectid, subjectDomain, category);
	}

	/**
	 * 处理接受到的消息怎样展示
	 * 
	 * @param context
	 * @param vo
	 * 
	 * @param bool
	 *            该数据后面是否还有同类型的数据
	 */
	private void handleReceiveMessage(Context context, MessageVo vo, boolean bool) {
		Bundle bundle = new Bundle();
		// 序列化
		bundle.putSerializable(SystemConfig.BUNDLE_NAME_MESSAGEVO, vo);
		bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISHAVEFOLLOWUPDATA, bool);
		bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISCHATACTIVITY_RECEIVE, false);
		bundle.putBoolean(SystemConfig.BUNDLE_NAME_ISMAINACTIVITY_RECEIVE, false);

		Intent intent = new Intent();
		intent.setAction(SystemConfig.ACTION_RECEIVEMESSAGE_BROADCAST);
		intent.putExtras(bundle);
		LogUtil.d(TAG, "----发送消息广播：apptype:" + SystemContext.APPTYPE + ", message:" + vo.toString());
		context.sendOrderedBroadcast(intent, null);
	}

	/**
	 * 设置某类消息是否有未读数
	 * 
	 * @param vo
	 */
	private void setHasUnreadMessage(MessageVo vo) {
		// 现在只有赞和回复我的需要此功能
		if (vo.getChannelType().equals(MsgsConstants.MC_NOTIFY) && vo.getCategory().equals(MsgsConstants.MCC_COMMENT)) {
			if (MsgsConstants.OP_PRAISE == vo.getEstimateop()) {
				SystemContext.getInstance().setHasUnreadMessage(vo.getChannelType(), vo.getSubjectId(), vo.getSubjectDomain(), vo.getCategory(),
						SystemContext.HAS_UNREAD_MESSAGE_TYPE_PRAISE, true);
			} else {
				SystemContext.getInstance().setHasUnreadMessage(vo.getChannelType(), vo.getSubjectId(), vo.getSubjectDomain(), vo.getCategory(),
						SystemContext.HAS_UNREAD_MESSAGE_TYPE_REPLY, true);
			}
		}
	}

	/**
	 * 发送有新商品或新任务通知
	 * 
	 * @param context
	 * @param vo
	 * @param bool
	 */
	private void handleReceiveInfoMessage(final Context context, final MessageVo vo) {
		LogUtil.d(TAG+"6", "MessageProxyImpl::handleReceiveInfoMessage:message is "+vo.toString());
		if (vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_TYPE_GOODS))
				|| vo.getContent().equals(String.valueOf(MsgsConstants.RESOURCE_APP_TYPE_TASK))) {
			// 发送有新商品或新任务通知
			Bundle bundle = new Bundle();
			bundle.putSerializable(SystemConfig.BUNDLE_NAME_MESSAGEVO, vo);
			Intent intent = new Intent();
			intent.setAction(SystemConfig.ACTION_RECEIVEMESSAGE_INTFO_BROADCAST);
			intent.putExtras(bundle);
			context.sendBroadcast(intent);
		} else {
			MessageDialogManager.getInstance().offer(vo);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see *
	 * com.iwgame.msgs.module.message.MessageProxy#sendGroupMassMessage(com.
	 * iwgame.msgs.common.ProxyCallBack, android.content.Context, long,
	 * java.lang.String, byte[], java.lang.String)
	 */
	@Override
	public void sendGroupMassMessage(final ProxyCallBack<Integer> callback, final Context context, final long groupid, final String content,
			final byte[] resource, final int resourceType, final String position) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallback = new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null) {
							if (result.getRc() == Msgs.ErrorCode.EC_OK_VALUE)
								asyncTask.getProxyCallBack().onSuccess(result.getRc());
							else
								asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}

				};
				long tid = groupid;
				int ttype = MsgsConstants.OT_GROUP;
				int op = MsgsConstants.OP_ADMIN_MASS_MSG;
				ServiceFactory.getInstance().getUserRemoteService()
						.userAction(serviceCallback, context, tid, ttype, op, content, 0, resource, resourceType, position, null);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	private void getChatHistoryMessageForServer(final ProxyCallBack<Integer> callback, final Context context, String channelType, long fromid,
			String fromDomain, long toid, String toDomain, String category, long msgindex, long limit, long minIndex) {
		// TODO Auto-generated method stub
		// 通过找入库规则，是否需要交换from和to主体
		List<MessageSubjectRuleVo> list = SystemContext.getInstance().getMessageSubjectRuleList();
		String result = MessageSubjectRuleVo.ITEM_RESULT_TO;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				MessageSubjectRuleVo tmp = list.get(i);
				if (tmp.getCategory().equals(category) && tmp.getChannelType().equals(channelType)) {
					result = tmp.getResult();
					break;
				}
			}
		}
		if (result.equals(MessageSubjectRuleVo.ITEM_RESULT_FROM)) {
			// 需要交换from 和to 主体
			long tmpfromid = fromid;
			String tmpfromDomain = fromDomain;
			fromid = toid;
			fromDomain = toDomain;
			toid = tmpfromid;
			toDomain = tmpfromDomain;
		}

		ServiceCallBack<XActionResult> callback2 = new ServiceCallBack<XActionResult>() {

			@Override
			public void onSuccess(XActionResult result) {
				// TODO Auto-generated method stub
				if (result != null && result.hasExtension(Msgs.syncResponse)) {
					handleSyncMessageResponse(context, result.getExtension(Msgs.syncResponse), true);
					callback.onSuccess(result.getRc());
				}
				// callback.onSuccess(Msgs.ErrorCode.EC_OK_VALUE);
			}

			@Override
			public void onFailure(Integer result, String resultMsg) {
				// TODO Auto-generated method stub
				LogUtil.error("获得历史数据请求响应异常：" + result);
				callback.onFailure(result, resultMsg);
			}
		};

		ServiceFactory.getInstance().getMessageRemoteService()
				.getChatHistoryMessage(callback2, context, fromid, fromDomain, toid, toDomain, channelType, category, msgindex, limit, minIndex);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.logic.MessageProxy#getMessageMaxIndex(
	 * java.lang.String, long, java.lang.String, java.lang.String)
	 */
	@Override
	public long getMessageMaxIndex(String channelType, long subjectid, String subjectDomain, String category) {
		// TODO Auto-generated method stub
		MessageVo vo = DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext())
				.getLastMessage(channelType, subjectid, subjectDomain, category);
		if (vo != null)
			return vo.getMsgIndex();
		else
			return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.logic.MessageProxy#
	 * getMessageByChannelTypeAndCategory(java.lang.String, java.lang.String)
	 */
	@Override
	public List<MessageVo> getMessageByChannelTypeAndCategory(String channelType, String category) {
		return DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext())
				.getMessageByChannelTypeAndCategory(channelType, category);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.message.logic.MessageProxy#
	 * setMessageDelByChannelTypeAndCategory(java.lang.String, java.lang.String)
	 */
	@Override
	public int delByChannelTypeAndCategory(String channelType, String category, String content) {
		return DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext())
				.delByChannelTypeAndCategory(channelType, category, content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.message.logic.MessageProxy#delLocalHistoryMessage
	 * (java.lang.String, java.lang.String, long, java.lang.String)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void delLocalHistoryMessage(final String channelType, final long subjectid, final String subjectDomain, final String category) {
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext())
						.delMessageByPolicy(channelType, category, subjectid, subjectDomain);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

}
