/**      
 * UserProxyImpl.java Create on 2013-8-26     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.user.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.MessageDao;
import com.iwgame.msgs.localdb.dao.PointTaskDao;
import com.iwgame.msgs.localdb.dao.ResourceDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.localdb.dao.UserGradeDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.account.object.UserObject;
import com.iwgame.msgs.module.cache.Cache;
import com.iwgame.msgs.module.cache.CacheImpl;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.module.remote.CommonRemoteService;
import com.iwgame.msgs.module.remote.SearchRemoteService;
import com.iwgame.msgs.module.remote.UserRemoteService;
import com.iwgame.msgs.module.setting.vo.ChangeRecordsEntity;
import com.iwgame.msgs.module.setting.vo.Goods;
import com.iwgame.msgs.module.setting.vo.GoodsDetail;
import com.iwgame.msgs.module.setting.vo.GoodsTab;
import com.iwgame.msgs.module.setting.vo.OrderDetail;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.module.user.object.ContactObj;
import com.iwgame.msgs.module.user.object.UserNewsVo;
import com.iwgame.msgs.module.user.object.UserPointDetailObj;
import com.iwgame.msgs.module.user.object.UserPointTaskObj;
import com.iwgame.msgs.module.user.ui.ExtraGameVo;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ActivityInfo;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams.ContentDetailParam;
import com.iwgame.msgs.proto.Msgs.ContentDetailsResult;
import com.iwgame.msgs.proto.Msgs.ContentExtDataResult;
import com.iwgame.msgs.proto.Msgs.ContentExtDataResult.ContentExtData;
import com.iwgame.msgs.proto.Msgs.ContentExtDataResult.UserContentExtData;
import com.iwgame.msgs.proto.Msgs.GameKey;
import com.iwgame.msgs.proto.Msgs.GameRole;
import com.iwgame.msgs.proto.Msgs.GameRoleBind;
import com.iwgame.msgs.proto.Msgs.GameRoleBindResult;
import com.iwgame.msgs.proto.Msgs.GoodsCategory;
import com.iwgame.msgs.proto.Msgs.GoodsCategoryResult;
import com.iwgame.msgs.proto.Msgs.GoodsDetailResult;
import com.iwgame.msgs.proto.Msgs.GoodsResult;
import com.iwgame.msgs.proto.Msgs.UserYoubiDetail;
import com.iwgame.msgs.proto.Msgs.LimitedOPCountResult.LimitedOPCount;
import com.iwgame.msgs.proto.Msgs.MessageContentType;
import com.iwgame.msgs.proto.Msgs.OrderDetailResult;
import com.iwgame.msgs.proto.Msgs.PointConfigDataResult;
import com.iwgame.msgs.proto.Msgs.PointEntityResult;
import com.iwgame.msgs.proto.Msgs.PointEntityResult.PointEntity;
import com.iwgame.msgs.proto.Msgs.PointTaskConfig;
import com.iwgame.msgs.proto.Msgs.PointTaskDataResult;
import com.iwgame.msgs.proto.Msgs.PointTaskDataResult.PointTaskData;
import com.iwgame.msgs.proto.Msgs.RecommendResult;
import com.iwgame.msgs.proto.Msgs.RecommendResult.RecommendEntry;
import com.iwgame.msgs.proto.Msgs.TopicPraiseUsers;
import com.iwgame.msgs.proto.Msgs.TopicPraiseUsers.PraiseUserInfo;
import com.iwgame.msgs.proto.Msgs.TransDetail;
import com.iwgame.msgs.proto.Msgs.TransDetailsResult;
import com.iwgame.msgs.proto.Msgs.TransSuccessResult;
import com.iwgame.msgs.proto.Msgs.UploadContactsRequest;
import com.iwgame.msgs.proto.Msgs.UserAlbumResult;
import com.iwgame.msgs.proto.Msgs.UserAlbumResult.UserAlbum;
import com.iwgame.msgs.proto.Msgs.UserGradeConfig;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.proto.Msgs.UserNewsResult;
import com.iwgame.msgs.proto.Msgs.UserNewsResult.UserNews;
import com.iwgame.msgs.proto.Msgs.UserPointDetailsResult;
import com.iwgame.msgs.proto.Msgs.UserPointDetailsResult.UserPointDetail;
import com.iwgame.msgs.proto.Msgs.UserQueryInfo;
import com.iwgame.msgs.proto.Msgs.UserQueryResult;
import com.iwgame.msgs.proto.Msgs.UserQueryResult.UserQueryEntry;
import com.iwgame.msgs.proto.Msgs.UserRecommendResult;
import com.iwgame.msgs.proto.Msgs.UserRecommendResult.UserRecommendEntry;
import com.iwgame.msgs.proto.Msgs.UserRoleDetail;
import com.iwgame.msgs.proto.Msgs.UserShareInfoResult;
import com.iwgame.msgs.utils.BuildVoUtil;
import com.iwgame.msgs.utils.ContactUtil;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.utils.MyTagUtil;
import com.iwgame.msgs.utils.ResUtil;
import com.iwgame.msgs.vo.local.GameKeyVo;
import com.iwgame.msgs.vo.local.GameRoleServiceVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.ResourceVo;
import com.iwgame.msgs.vo.local.SupportUserVo;
import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.UserRoleEntity;
import com.iwgame.msgs.vo.local.UserRoleVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.utils.BitmapUtil;
import com.iwgame.utils.DateUtil;
import com.iwgame.utils.ImageUtil;
import com.iwgame.utils.LogUtil;
import com.iwgame.utils.NetworkUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: UserProxyImpl
 * @Description: 用户模块数据代理类
 * @author 王卫
 * @date 2013-8-26 上午11:37:38
 * @Version 1.0
 * 
 */
public class UserProxyImpl implements UserProxy {

	private static final String TAG = "UserProxyImpl";

	private UserRemoteService userService = ServiceFactory.getInstance().getUserRemoteService();
	private SearchRemoteService searchService = ServiceFactory.getInstance().getSearchRemoteService();
	private CommonRemoteService commonService = ServiceFactory.getInstance().getCommonRemoteService();
	private String keyWords = null;
	private static byte[] lock = new byte[0];

	private static UserProxyImpl instance = null;

	private UserProxyImpl() {

	}

	public static UserProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new UserProxyImpl();
				return instance;
			}
		} else {
			return instance;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.user.logic.UserProxy#getContactUsers(int,
	 * int, java.lang.String, int, int)
	 */
	@Override
	public void getContactUsers(final ProxyCallBack<List<UserVo>> callback, Context context, final int type, final int relation, final int sort,
			final int pageNo, final int size, final String keyword) {
		LogUtil.d(TAG, "--------获取通讯录好友数据PROXY：type=" + type + ", relation=" + relation + ", keyword=" + keyword);
		UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		List<UserVo> result = null;
		if (keyword != null) {
			result = dao.getUsersByRelation(type, relation, 1, keyword);
		} else {
			result = dao.getUsersByRelation(type, relation, 1);
		}
		callback.onSuccess(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#searchUser(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, java.lang.Long,
	 * java.lang.Integer, java.lang.Integer, java.lang.String,
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void searchUsers(ProxyCallBack<PagerVo<com.iwgame.msgs.module.account.object.UserObject>> callback, Context context, Long gid,
			Boolean isfate, Boolean isrecommend, Integer rel, String resulttype, String uid, long offset, int limit, Integer nearDistance,
			Integer lastlogin, Integer sex, Boolean isfind, Integer source) {
		String position = SystemContext.getInstance().getLocation();

		searchUsersFromService(callback, context, gid, isfate, isrecommend, rel, resulttype, uid, offset, limit, nearDistance, position, lastlogin,
				sex, isfind, source, null);

	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.user.logic.UserProxy#searchUsersBysid(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.Long, java.lang.Boolean, java.lang.Boolean, java.lang.Integer, java.lang.String, java.lang.String, long, int, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Boolean, java.lang.Integer, java.lang.String)
	 */
	@Override
	public void searchUsersBysid(ProxyCallBack<PagerVo<UserObject>> callback, Context context, Long gid, Boolean isfate, Boolean isrecommend,
			Integer rel, String resulttype, String uid, long offset, int limit, Integer nearDistance, Integer lastlogin, Integer sex, Boolean isfind,
			String sid, Integer source) {
		String position = SystemContext.getInstance().getLocation();
		
		searchUsersFromService(callback, context, gid, isfate, isrecommend, rel, resulttype, uid, offset, limit, nearDistance, position, lastlogin,
				sex, isfind, source, sid);
	}

	/**
	 * 
	 * @param callback
	 * @param context
	 * @param gid
	 * @param isfate
	 * @param isrecommend
	 * @param rel
	 * @param resulttype
	 * @param uid
	 * @param pageNo
	 * @param pageSize
	 * @param isnear
	 * @param position
	 */
	private void searchUsersFromService(final ProxyCallBack<PagerVo<UserObject>> callback, final Context context, final Long gid,
			final Boolean isfate, final Boolean isrecommend, final Integer rel, final String resulttype, final String uid, final long offset,
			final int limit, final Integer nearDistance, final String position, final Integer lastlogin, final Integer sex, final Boolean isfind,
			final Integer source, final String sid) {
		final MyAsyncTask<PagerVo<UserObject>> asyncTask = new MyAsyncTask<PagerVo<UserObject>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				searchService.searchUsers(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						PagerVo<UserObject> pagerVo = null;
						if (result.hasExtension(Msgs.userQueryResult)) {
							UserQueryResult userQueryResult = result.getExtension(Msgs.userQueryResult);
							List<UserQueryEntry> list = userQueryResult.getEntryList();
							pagerVo = new PagerVo<UserObject>();

							if (list != null) {
								int listSize = list.size();
								List<UserObject> items = new ArrayList<UserObject>();
								for (int i = 0; i < listSize; i++) {
									UserQueryEntry entry = list.get(i);
									UserQueryInfo info = entry.getUserInfo();
									UserObject vo = new UserObject();
									vo.setAvatar(info.getAvatar());
									vo.setMood(info.getMood());
									vo.setUid(info.getUid());
									vo.setNickname(info.getNickname());
									vo.setSex(info.getSex());
									vo.setAge(info.getAge());
									vo.setGids(entry.getGidsList());
									vo.setGameCount(entry.getGameCount());
									vo.setRel(entry.getRel());
									if (entry.hasDistance())
										vo.setDistance(entry.getDistance());
									vo.setLastLogin(entry.getLastLogin());
									vo.setGrade(info.getGrade());
									vo.setCount(entry.getCount());
									vo.setPosition(entry.getPosition());
									vo.setNews(entry.getNews());
									items.add(vo);
								}
								pagerVo.setItems(items);
								pagerVo.setOffset(userQueryResult.getOffset());
								asyncTask.getProxyCallBack().onSuccess(pagerVo);
								return;
							}
						}
						asyncTask.getProxyCallBack().onSuccess(pagerVo);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, null, isfate, rel, gid, isrecommend, resulttype, position, uid, offset, limit, nearDistance, lastlogin, sex, isfind, source, sid);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#searchUser(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, java.lang.String,
	 * java.lang.Integer)
	 */
	@Override
	public void searchUser(final ProxyCallBack<PagerVo<UserObject>> callback, final Context context, final String condition, final String resulttype,
			final long offset, final int limit, final Long uid) {
		final MyAsyncTask<PagerVo<UserObject>> asyncTask = new MyAsyncTask<PagerVo<UserObject>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				searchService.searchUser(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.userQueryResult)) {
							UserQueryResult userQueryResult = result.getExtension(Msgs.userQueryResult);
							List<UserQueryEntry> list = userQueryResult.getEntryList();
							PagerVo<UserObject> pagerVo = new PagerVo<UserObject>();
							if (list != null) {
								int sizeList = list.size();
								List<UserObject> items = new ArrayList<UserObject>();
								for (int i = 0; i < sizeList; i++) {
									UserQueryEntry entry = list.get(i);
									UserQueryInfo info = entry.getUserInfo();
									UserObject vo = new UserObject();
									vo.setAvatar(info.getAvatar());
									vo.setUid(info.getUid());
									vo.setNickname(info.getNickname());
									vo.setMood(info.getMood());
									vo.setSex(info.getSex());
									vo.setAge(info.getAge());
									vo.setGids(entry.getGidsList());
									vo.setGameCount(entry.getGameCount());
									vo.setRel(entry.getRel());
									if (entry.hasDistance())
										vo.setDistance(entry.getDistance());
									vo.setLastLogin(entry.getLastLogin());
									vo.setPosition(entry.getPosition());
									vo.setGrade(info.getGrade());
									vo.setNews(entry.getNews());
									items.add(vo);
								}
								pagerVo.setItems(items);
								pagerVo.setOffset(userQueryResult.getOffset());
								asyncTask.getProxyCallBack().onSuccess(pagerVo);
							} else {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, null, condition, resulttype, offset, limit, uid);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#addFollowUser(com.iwgame.
	 * msgs.common.ProxyCallBack, android.content.Context, java.lang.String,
	 * long)
	 */
	@Override
	public void addFollowUser(final ProxyCallBack<Integer> callback, final Context context, final long uid, final boolean isSync) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.updateRelUser(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							final UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
							UserVo uvo = userDao.getUserByUserId(uid);
							if (uvo != null) {
								uvo.setRelPositive(1);
								userDao.insertOrUpdateByUserid(uvo);
								sendFollowMessage(uvo);
							} else {
								ContentDetailParams.Builder params = ContentDetailParams.newBuilder();
								ContentDetailParam.Builder cb = ContentDetailParam.newBuilder();
								cb.setId(uid);
								cb.setUtime(uvo == null ? 0 : uvo.getUpdatetime());
								params.addParam(cb.build());
								ProxyFactory.getInstance().getUserProxy().getUserDetailInfo(new ProxyCallBack<List<UserVo>>() {

									@Override
									public void onSuccess(List<UserVo> result) {
										if (result != null && result.size() > 0) {
											UserVo uvo = result.get(0);
											if (uvo != null) {
												uvo.setRelPositive(1);
												userDao.insertOrUpdateByUserid(uvo);
												sendFollowMessage(uvo);
											}
										}
									}

									@Override
									public void onFailure(Integer result, String resultMsg) {
									}
								}, context, params.build(), 0, null);
							}
							if (isSync) {
								ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_REL_USER, new SyncCallBack() {

									@Override
									public void onSuccess(Object res) {
										asyncTask.getProxyCallBack().onSuccess(result.getRc());
									}

									@Override
									public void onFailure(Integer res) {
										asyncTask.getProxyCallBack().onSuccess(result.getRc());
									}
								});
							} else {
								asyncTask.getProxyCallBack().onSuccess(result.getRc());
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, String.valueOf(uid), 1);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 关注成功后发送关注消息
	 * 
	 * @param uid
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void sendFollowMessage(final UserVo uvo) {
		if (uvo == null)
			return;
		int ltiem = Integer.valueOf(DateUtil.Date2String3(new Date())) - SystemContext.getInstance().getFollowSendMessageTime(uvo.getUserid());
		if (ltiem <= 0) {
			return;
		}
		new MyAsyncTask().execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// 发送消息
				MessageVo vo = new MessageVo();
				vo.setSource(MessageVo.SOURCE_CLIENT);
				vo.setChannelType(MsgsConstants.MC_CHAT);
				vo.setMsgId(0);
				vo.setFromId(SystemContext.getInstance().getExtUserVo().getUserid());
				vo.setFromDomain(MsgsConstants.DOMAIN_USER);
				vo.setToId(uvo.getUserid());
				vo.setToDomain(MsgsConstants.DOMAIN_USER);
				vo.setSubjectId(uvo.getUserid());
				vo.setSubjectDomain(MsgsConstants.DOMAIN_USER);
				vo.setCategory(MsgsConstants.MCC_CHAT);
				vo.setContentType(MessageContentType.TEXT_VALUE);
				String umane = "";
				int sex = -1;
				if (SystemContext.getInstance().getExtUserVo() != null) {
					umane = SystemContext.getInstance().getExtUserVo().getUsername();
					sex = SystemContext.getInstance().getExtUserVo().getSex();
				}
				StringBuffer sb = new StringBuffer();
				sb.append("你已经添加了");
				if (uvo.getSex() == 0 || uvo.getSex() == 1) {
					sb.append("[gender:");
					sb.append(String.valueOf(uvo.getSex()));
					sb.append("]");
				}
				sb.append(uvo.getUsername());
				sb.append(",现在可以开始聊天了");
				sb.append(MyTagUtil.TAG_FOLLOW);
				if (sex == 0 || sex == 1) {
					sb.append("[gender:");
					sb.append(String.valueOf(sex));
					sb.append("]");
				}
				sb.append(umane);
				sb.append("关注了你，现在可以开始聊天了");
				vo.setContent(sb.toString());
				vo.setSummary(sb.toString());
				vo.setCreateTime(System.currentTimeMillis());
				vo.setPosition(SystemContext.getInstance().getLocation());
				vo.setReadStatus(MessageVo.READSTATUS_READ);
				vo.setStatus(MessageVo.STATUS_SENDING);
				final MessageDao dao = DaoFactory.getDaoFactory().getMessageDao(SystemContext.getInstance().getContext());
				List<MessageVo> messages = dao.getSubjectLastMessage();
				if (messages != null && messages.size() > 0) {
					MessageVo maxvo = messages.get(0);
					vo.setMsgIndex(maxvo.getMsgIndex());
				}
				// 先入本地库中，更新界面上的内容为在发送中
				vo = ProxyFactory.getInstance().getMessageProxy().sendMessageSaveToLocal(SystemContext.getInstance().getContext(), vo);
				if (vo != null) {
					final MessageVo mvo = vo;
					// 发送到服务器
					ProxyFactory.getInstance().getMessageProxy().sendMessage(new ProxyCallBack<MessageVo>() {

						@Override
						public void onSuccess(MessageVo result) {
							LogUtil.i(TAG, "消息发送成功:" + result.toString());
							SystemContext.getInstance().setFollowSendMessageTime(uvo.getUserid(), Integer.valueOf(DateUtil.Date2String3(new Date())));
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							LogUtil.e(TAG, "消息发送失败:" + result);
							dao.updateById(mvo.getId(), 0, 0, 0, 0, null, MessageVo.STATUS_DEL);
						}
					}, SystemContext.getInstance().getContext(), vo, false);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#cannelUserFromFollowList(
	 * long)
	 */
	@Override
	public void cannelFollowUser(final ProxyCallBack<Integer> callback, final Context context, final long uid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.updateRelUser(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
						UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
						UserVo uvo = userDao.getUserByUserId(uid);
						if (uvo != null) {
							uvo.setRelPositive(0);
							userDao.insertOrUpdateByUserid(uvo);
						}
						ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_REL_USER, new SyncCallBack() {

							@Override
							public void onSuccess(Object res) {
								asyncTask.getProxyCallBack().onSuccess(result.getRc());
							}

							@Override
							public void onFailure(Integer result) {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
							}
						});
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, String.valueOf(uid), 0);
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
	 * @see com.iwgame.msgs.module.user.logic.UserProxy#addToBlackList(long)
	 */
	@Override
	public void addToBlackList(final ProxyCallBack<Integer> callback, final Context context, final long uid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.updateRelUser(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						ServiceFactory.getInstance().getSyncListService().syncList(SyncListService.TYPE_MY_REL_USER, new SyncCallBack() {

							@Override
							public void onSuccess(Object res) {
								asyncTask.getProxyCallBack().onSuccess(result.getRc());
							}

							@Override
							public void onFailure(Integer result) {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
							}
						});
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, String.valueOf(uid), MsgsConstants.UR_CRL);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#userAction(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, long, int, int,
	 * java.lang.String, byte[], java.lang.String)
	 */
	@Override
	public void userAction(final ProxyCallBack<Integer> callback, final Context context, final long tid, final int ttype, final int op,
			final String content, final byte[] resource, final String remark) {
		userAction(callback, context, tid, ttype, op, content, resource, 0, remark);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#userAction(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, long, int, int,
	 * java.lang.String, byte[], java.lang.String)
	 */
	@Override
	public void userAction(final ProxyCallBack<Integer> callback, final Context context, final long tid, final int ttype, final int op,
			final String content, final byte[] resource, long seq, String remark) {
		userAction(callback, context, tid, ttype, op, content, resource, MsgsConstants.RESOURCE_TYPE_IMAGE, seq, remark);
	}

	private void userAction(final ProxyCallBack<Integer> callback, final Context context, final long tid, final int ttype, final int op,
			final String content, final byte[] resource, final int resourceType, final long seq, final String remark) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String position = SystemContext.getInstance().getLocation();
				userService.userAction(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, tid, ttype, op, content, seq, resource, resourceType, position, remark);
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
	 * com.iwgame.msgs.module.setting.logic.SettingProxy#addAlbum(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, byte[])
	 */
	@Override
	public void addAlbum(final ProxyCallBack<ResourceVo> callback, final Context context, final byte[] image) {
		final MyAsyncTask<ResourceVo> asyncTask = new MyAsyncTask<ResourceVo>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.addAlbum(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.id)) {
							if (result.hasExtension(Msgs.id)) {
								Msgs.IdResult idr = result.getExtension(Msgs.id);
								ResourceVo vo = new ResourceVo();
								vo.setResourceId(idr.getResourceId());
								asyncTask.getProxyCallBack().onSuccess(vo);
							} else {
								asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, image);
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
	 * com.iwgame.msgs.module.setting.logic.SettingProxy#getUserAlbum(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long, long)
	 */
	@Override
	public void getUserAlbum(final ProxyCallBack<List<ResourceVo>> callback, final Context context, final long uid, final long utime) {
		final MyAsyncTask<List<ResourceVo>> asyncTask = new MyAsyncTask<List<ResourceVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
				final ResourceDao resourceDao = DaoFactory.getDaoFactory().getResourceDao(SystemContext.getInstance().getContext());

				if (NetworkUtil.isNetworkAvailable(context)) {
					// 发送服务端请求
					userService.getUserAlbum(new ServiceCallBack<XActionResult>() {

						@Override
						public void onSuccess(XActionResult result) {
							if (result != null && result.hasExtension(Msgs.userAlbumResult)) {
								UserAlbumResult uaresult = result.getExtension(Msgs.userAlbumResult);
								UserAlbum ua = uaresult.getAlbum();
								if (ua != null && ua.getResourceIdList() != null) {
									List<ResourceVo> rlist = new ArrayList<ResourceVo>();
									List<String> list = ua.getResourceIdList();
									int length = list.size();
									for (int i = 0; i < length; i++) {
										ResourceVo vo = new ResourceVo(uid, list.get(i), 0, ResourceVo.RES_TYPE_IMAGE);
										UserVo uvo = userDao.getUserByUserId(uid);
										if (uvo != null || uid == SystemContext.getInstance().getExtUserVo().getUserid()) {
											// 保存到数据库中
											resourceDao.insertOrUpdate(vo);
										}
										rlist.add(vo);
									}
									asyncTask.getProxyCallBack().onSuccess(rlist);
								} else {
									asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
								}
							} else {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							asyncTask.getProxyCallBack().onFailure(result, resultMsg);
						}
					}, null, uid, utime);
				} else {
					asyncTask.getProxyCallBack().onSuccess(resourceDao.findResourceByUserid(uid));
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#delUserAlbum(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void delUserAlbum(final ProxyCallBack<Integer> callback, final Context context, final String resourceId) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final ResourceDao resourceDao = DaoFactory.getDaoFactory().getResourceDao(SystemContext.getInstance().getContext());
				// 发送服务端请求
				userService.delUserAblum(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						// 成功删除本地资源库
						resourceDao.deleteByResId(resourceId);
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, resourceId);
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
	 * com.iwgame.msgs.module.game.logic.GameProxy#getRecommendedGameInfo(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context, long,
	 * java.lang.String)
	 */
	@Override
	public void getRecommendedGameInfo(final ProxyCallBack<Map<Long, Boolean>> callback, final Context context, final long gid, final String ids) {
		final MyAsyncTask<Map<Long, Boolean>> asyncTask = new MyAsyncTask<Map<Long, Boolean>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.searchRecommended(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.recommendResult)) {
							RecommendResult rresult = result.getExtension(Msgs.recommendResult);
							if (rresult != null && rresult.getEntrysList() != null) {
								Map<Long, Boolean> list = new HashMap<Long, Boolean>();
								List<RecommendEntry> rlist = rresult.getEntrysList();
								int size = rlist.size();
								for (int i = 0; i < size; i++) {
									RecommendEntry er = rlist.get(i);
									list.put(er.getId(), er.getIsRecommend());
								}
								asyncTask.getProxyCallBack().onSuccess(list);
							} else {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, null, gid, ids);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#setPosition(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void setPosition(final ProxyCallBack<Integer> callback, final Context context, final String position) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.setPosition(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, position);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	public void getUserDetailInfo(ProxyCallBack<List<UserVo>> callback, Context context, ContentDetailParams params, int type, Long uid) {
		getUserDetailInfo(callback, context, params, type, uid, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#getUserDetailInfo(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long, long, boolean)
	 */
	@Override
	public void getUserDetailInfo(final ProxyCallBack<List<UserVo>> callback, final Context context, final ContentDetailParams params,
			final int type, final Long uid, final boolean fromnet) {
		final List<UserVo> list = new ArrayList<UserVo>();
		UserVo vo = SystemContext.getInstance().getExtUserVo();
		final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		// 如果是只获取一个用户的信息，肯定会执行下面的这个分支
		if (params.getParamCount() == 1) {
			ContentDetailParam cdp = params.getParam(0);
			UserVo userVo = dao.getUserByUserId(cdp.getId());
			if (vo == null) {
				vo = new UserVo();
				list.clear();
				list.add(vo);
				callback.onSuccess(list);
				return;
			}
			if (cdp.getId() == vo.getUserid()) {
				list.clear();
				list.add(vo);
				callback.onSuccess(list);
				return;
			}
			if (userVo != null) {
				if (!fromnet) {
					list.clear();
					list.add(userVo);
					callback.onSuccess(list);
					return;
				}
			}
		}
		// 判断有没有网络
		if (NetworkUtil.isNetworkAvailable(SystemContext.getInstance().getContext())) {
			final MyAsyncTask<List<UserVo>> asyncTask = new MyAsyncTask<List<UserVo>>(callback);
			asyncTask.execute(new AsyncCallBack<Void>() {

				@Override
				public Void execute() {
					// 服务端获取数据
					userService.getUserInfo(new ServiceCallBack<XActionResult>() {

						@Override
						public void onSuccess(XActionResult result) {
							if (result.hasExtension(Msgs.contentDetailsResult)) {
								ContentDetailsResult uiresult = result.getExtension(Msgs.contentDetailsResult);
								if (uiresult != null) {
									List<UserInfoDetail> infoList = uiresult.getUserInfoDetailList();
									if (infoList == null || infoList.size() <= 0) {
										asyncTask.getProxyCallBack().onSuccess(list);
										return;
									}
									int size = infoList.size();
									UserInfoDetail detail;
									UserVo userVo;
									for (int i = 0; i < size; i++) {
										detail = infoList.get(i);
										if (detail != null && detail.getId() != 0) {
											ExtUserVo vo = BuildVoUtil.buildExtUserVo(detail);
											userVo = dao.getUserByUserId(detail.getId());
											if (userVo != null) {
												vo.setRelPositive(userVo.getRelPositive());
												vo.setRelInverse(userVo.getRelInverse());
												vo.setRemarksName(userVo.getRemarksName());
											} else {
												vo.setRelPositive(0);
												vo.setRelInverse(0);
											}
											dao.insertOrUpdateByUserid(vo);
											list.add(vo);
										}
									}
									asyncTask.getProxyCallBack().onSuccess(list);
								} else {
									LogUtil.e(TAG, "获取用户数据为空");
									asyncTask.getProxyCallBack().onSuccess(list);
								}
							} else {
								asyncTask.getProxyCallBack().onSuccess(list);
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							LogUtil.e(TAG, "网络获取用户信息失败" + result);
							asyncTask.getProxyCallBack().onSuccess(list);
						}
					}, context, params, type, uid);
					return null;
				}

				@Override
				public void onHandle(Void result) {
					// TODO Auto-generated method stub
				}

			});
		} else {
			callback.onSuccess(list);
		}
	}

	@Override
	public void getUserDetailInfoNoFromLocal(final ProxyCallBack<List<UserVo>> callback, final Context context, final ContentDetailParams params,
			final int type, final Long uid) {
		final List<UserVo> list = new ArrayList<UserVo>();
		final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		// 判断有没有网络
		if (NetworkUtil.isNetworkAvailable(SystemContext.getInstance().getContext())) {
			final MyAsyncTask<List<UserVo>> asyncTask = new MyAsyncTask<List<UserVo>>(callback);
			asyncTask.execute(new AsyncCallBack<Void>() {

				@Override
				public Void execute() {
					// 服务端获取数据
					userService.getUserInfo(new ServiceCallBack<XActionResult>() {

						@Override
						public void onSuccess(XActionResult result) {
							if (result.hasExtension(Msgs.contentDetailsResult)) {
								ContentDetailsResult uiresult = result.getExtension(Msgs.contentDetailsResult);
								if (uiresult != null) {
									List<UserInfoDetail> infoList = uiresult.getUserInfoDetailList();
									if (infoList == null || infoList.size() <= 0)
										return;
									int size = infoList.size();
									UserInfoDetail detail;
									UserVo userVo;
									for (int i = 0; i < size; i++) {
										detail = infoList.get(i);
										userVo = dao.getUserByUserId(detail.getId());
										if (detail != null && detail.getId() != 0) {
											ExtUserVo vo = BuildVoUtil.buildExtUserVo(detail);
											if (userVo != null) {
												vo.setRelPositive(userVo.getRelPositive());
												vo.setRelInverse(userVo.getRelInverse());
											} else {
												vo.setRelPositive(0);
												vo.setRelInverse(0);
											}
											SystemContext.getInstance().setIsGuest(detail.getIsGuest());
											SystemContext.getInstance().setLoginStatus(vo.getIsGuest());
											// 存储当前登录的用户信息
											SystemContext.getInstance().setExtUserVo(vo);
											dao.insertOrUpdateByUserid(vo);
											// 设置当前登录账户类型
											list.add(vo);
										} else {
											LogUtil.e(TAG, "获取用户数据为空");
											asyncTask.getProxyCallBack().onSuccess(list);
										}
									}
									asyncTask.getProxyCallBack().onSuccess(list);
								} else {
									LogUtil.e(TAG, "获取用户数据为空");
									asyncTask.getProxyCallBack().onSuccess(list);
								}
							} else {
								asyncTask.getProxyCallBack().onSuccess(list);
							}
						}

						@Override
						public void onFailure(Integer result, String resultMsg) {
							LogUtil.e(TAG, "网络获取用户信息失败" + result);
							asyncTask.getProxyCallBack().onSuccess(list);
						}
					}, context, params, type, uid);
					return null;
				}

				@Override
				public void onHandle(Void result) {
					// TODO Auto-generated method stub
				}

			});
		} else {
			callback.onSuccess(list);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#searchRecommendUsers(com.
	 * iwgame.msgs.common.ProxyCallBack, android.content.Context,
	 * java.lang.String, int, int)
	 */
	@Override
	public void searchRecommendUsers(final ProxyCallBack<PagerVo<UserObject>> callback, final Context context, final String grids, final long offset,
			final int limit) {
		final MyAsyncTask<PagerVo<UserObject>> asyncTask = new MyAsyncTask<PagerVo<UserObject>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				searchService.searchRecommendUsers(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.userRecommendResult)) {
							UserRecommendResult gr = result.getExtension(Msgs.userRecommendResult);
							List<UserRecommendEntry> entry = gr.getEntryList();
							if (entry != null && entry.size() > 0) {
								PagerVo<UserObject> pagetVo = new PagerVo<UserObject>();
								List<UserObject> items = new ArrayList<UserObject>();
								pagetVo.setOffset(entry.get(entry.size() - 1).getId());
								pagetVo.setItems(items);
								int entrySize = entry.size();
								for (int i = 0; i < entrySize; i++) {
									UserRecommendEntry e = entry.get(i);
									UserObject vo = new UserObject();
									vo.setSortId(e.getId());
									vo.setUid(e.getUid());
									vo.setNickname(e.getNickname());
									vo.setAvatar(e.getAvatar());
									vo.setSex(e.getSex());
									vo.setAge(e.getAge());
									vo.setGrade(e.getGrade());
									items.add(vo);
								}
								asyncTask.getProxyCallBack().onSuccess(pagetVo);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, grids, offset, limit);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#searchPostBarManager(com.
	 * iwgame.msgs.common.ProxyCallBack, android.content.Context, long, long,
	 * int)
	 */
	@Override
	public void searchPostBarManager(final ProxyCallBack<PagerVo<UserObject>> callback, final Context context, final long gid, final long offset,
			final int limit) {
		final MyAsyncTask<PagerVo<UserObject>> asyncTask = new MyAsyncTask<PagerVo<UserObject>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				searchService.searchPostBarManager(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.userQueryResult)) {
							UserQueryResult userQueryResult = result.getExtension(Msgs.userQueryResult);
							List<UserQueryEntry> list = userQueryResult.getEntryList();
							PagerVo<UserObject> pagerVo = new PagerVo<UserObject>();

							if (list != null) {
								List<UserObject> items = new ArrayList<UserObject>();
								int listSize = list.size();
								for (int i = 0; i < listSize; i++) {
									UserQueryEntry entry = list.get(i);
									UserQueryInfo info = entry.getUserInfo();
									UserObject vo = new UserObject();
									vo.setAvatar(info.getAvatar());
									vo.setUid(info.getUid());
									vo.setNickname(info.getNickname());
									vo.setSex(info.getSex());
									vo.setAge(info.getAge());
									vo.setMood(info.getMood());
									vo.setGrade(info.getGrade());
									items.add(vo);
								}
								pagerVo.setItems(items);
								asyncTask.getProxyCallBack().onSuccess(pagerVo);
							} else {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, gid, offset, limit);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#setMessageTip(com.iwgame.
	 * msgs.common.ProxyCallBack, android.content.Context, java.lang.Long, int,
	 * int)
	 */
	@Override
	public void setMessageTip(final ProxyCallBack<Integer> callback, final Context context, final Long tid, final int type, final int status) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.setMessageTip(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null)
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						else
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, tid, type, status);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#getMessageTip(com.iwgame.
	 * msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getMessageTip(final ProxyCallBack<XActionResult> callback, final Context context) {
		final MyAsyncTask<XActionResult> asyncTask = new MyAsyncTask<XActionResult>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
				userService.getMessageTip(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.msgNoticeSet)) {
							Msgs.MsgNoticeSet msgNoticeSet = result.getExtension(Msgs.msgNoticeSet);
							List<Msgs.MsgNoticeSet.MsgNoticeEntry> list = msgNoticeSet.getEntryList();
							if (list != null) {
								int length = list.size();
								for (int i = 0; i < length; i++) {
									Msgs.MsgNoticeSet.MsgNoticeEntry entry = list.get(i);
									if (entry.getType() == 1) {// 全局消息
										SystemContext.getInstance().setMsgGlobalOffOn(entry.getStatus() == 1 ? true : false);
									} else if (entry.getType() == 2) {// 个人聊天
										SystemContext.getInstance().setMsgChatOffOn(entry.getStatus() == 1 ? true : false);
									} else if (entry.getType() == 3) {// 关于我的
										SystemContext.getInstance().setCommentMyOffOn(entry.getStatus() == 1 ? true : false);
									} else if (entry.getType() == 4) {// 公会消息
										GroupVo groupVo = groupDao.findGroupByGrid(entry.getId());
										if (groupVo != null) {
											groupVo.setMsgoffon(entry.getStatus());
											groupDao.updateOrInsertById(groupVo);
										}
									} else if (entry.getType() == 5) {// 精彩推荐
										SystemContext.getInstance().setWonderfullOffOn(entry.getStatus() == 1 ? true : false);
									} else if (entry.getType() == 6) {// 表示的是缘分好友
										SystemContext.getInstance().setFateRecommendOffOn(entry.getStatus() == 1 ? true : false);
									}
								}
								asyncTask.getProxyCallBack().onSuccess(result);
							} else {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_REQUEST_ERROR, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#getContact(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getContact(final ProxyCallBack<ContactObj> callback, final Context context) {
		final MyAsyncTask<ContactObj> asyncTask = new MyAsyncTask<ContactObj>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final ContactObj contactObj = new ContactObj();
				// 查找我关注的好友
				final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
				final List<UserVo> followUsers = dao.getUsersByRelation(1, 1, 1);
				// 查找本地联系人
				List<Map<String, String>> contacts = ContactUtil.getPhoneContacts(context);
				final List<UserObject> list = new ArrayList<UserObject>();
				int contractsLength = contacts.size();
				for (int i = 0; i < contractsLength; i++) {
					Map<String, String> cnt = contacts.get(i);
					UserObject uobj = new UserObject();
					uobj.setContactName(cnt.get(Phone.DISPLAY_NAME));
					String num = cnt.get(Phone.NUMBER);
					if (num != null)
						num = num.replaceAll("\\+86|\\D", "");
					uobj.setMobile(num);
					if (uobj.getMobile() != null && !uobj.getMobile().equals(SystemContext.getInstance().getAccount())) {
						list.add(uobj);
					}
				}
				// 向服务端发送请求验证是否有游伴账号
				userService.verifiyContactUser(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XAction.XActionResult result) {
						List<UserObject> follows = new ArrayList<UserObject>();
						List<UserObject> intives = new ArrayList<UserObject>();
						contactObj.followObjects = follows;
						contactObj.inviteObjects = intives;
						if (result != null && result.hasExtension(Msgs.uidMapperResult)) {
							Msgs.UidMapperResult umresult = result.getExtension(Msgs.uidMapperResult);
							List<Msgs.UidMapperResult.UidMapperEntry> uidlist = umresult.getEntryList();
							int uidlistSize = uidlist.size();
							for (int j = 0; j < uidlistSize; j++) {
								Msgs.UidMapperResult.UidMapperEntry entry = uidlist.get(j);
								UserObject vo = new UserObject();
								vo.setUid(entry.getUid());
								vo.setMobile(entry.getCondition());
								follows.add(vo);
							}
							int listLength = list.size();
							for (int i = 0; i < listLength; i++) {
								UserObject userObject = list.get(i);
								boolean has = false;
								for (int j = 0; j < uidlist.size(); j++) {
									Msgs.UidMapperResult.UidMapperEntry entry = uidlist.get(j);
									if (userObject.getMobile().equals(entry.getCondition())) {
										has = true;
										break;
									}
								}
								if (!has) {
									intives.add(userObject);
								}
							}
						}
						asyncTask.getProxyCallBack().onSuccess(contactObj);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, getUserMobiles(list), 0);
				// 上传通讯录
				upContacts(new ProxyCallBack<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}
				}, contacts, context);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 上传通讯录
	 * 
	 * @param contacts
	 */
	private void upContacts(final ProxyCallBack<Integer> callback, final List<Map<String, String>> contacts, final Context context) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.upContact(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, contacts);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 获取用户手机号IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getUserMobiles(List<UserObject> list) {
		if (list != null) {
			StringBuffer strBuf = new StringBuffer();
			int listLength = list.size();
			for (int i = 0; i < listLength; i++) {
				UserObject uo = list.get(i);
				strBuf.append(uo.getMobile());
				strBuf.append(",");
			}
			if (strBuf.length() > 0)
				return strBuf.substring(0, strBuf.length() - 1);
			return null;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#getWeiboFriends(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getWeiboFriends(final ProxyCallBack<ContactObj> callback, final Context context) {
		final MyAsyncTask<ContactObj> asyncTask = new MyAsyncTask<ContactObj>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				// 查找我关注的好友
				final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());

				SinaWeibo weibo = new SinaWeibo(context);
				weibo.setPlatformActionListener(new PlatformActionListener() {
					public void onError(Platform weibo, int action, Throwable t) {
						t.printStackTrace();
						if (action == Platform.ACTION_GETTING_FRIEND_LIST) {
							// 在这里处理获取关注列表失败的代码
						}
					}

					public void onComplete(Platform weibo, int action, HashMap<String, Object> res) {
						if (action == Platform.ACTION_GETTING_FRIEND_LIST) {
							// 在这里处理获取关注列表成功的代码
							try {
								final List<UserObject> list = new ArrayList<UserObject>();

								ArrayList<HashMap<String, Object>> users = (ArrayList<HashMap<String, Object>>) res.get("users");
								for (int i = 0; i < users.size(); i++) {
									HashMap<String, Object> user = users.get(i);
									UserObject uobj = new UserObject();
									uobj.setWeibo(String.valueOf(user.get("id")));
									uobj.setWeiboName(String.valueOf(user.get("name")));
									list.add(uobj);
								}
								final ContactObj contactObj = new ContactObj();
								// 向服务端发送请求验证是否有游伴账号
								userService.verifiyContactUser(new ServiceCallBack<XAction.XActionResult>() {

									@Override
									public void onSuccess(XAction.XActionResult result) {
										List<UserObject> follows = new ArrayList<UserObject>();
										List<UserObject> intives = new ArrayList<UserObject>();
										contactObj.followObjects = follows;
										contactObj.inviteObjects = intives;
										if (result != null && result.hasExtension(Msgs.uidMapperResult)) {
											Msgs.UidMapperResult umresult = result.getExtension(Msgs.uidMapperResult);
											List<Msgs.UidMapperResult.UidMapperEntry> uidlist = umresult.getEntryList();
											for (int i = 0; i < list.size(); i++) {
												if (uidlist != null) {
													UserObject userObject = list.get(i);
													boolean has = false;
													for (int j = 0; j < uidlist.size(); j++) {
														Msgs.UidMapperResult.UidMapperEntry entry = uidlist.get(j);
														if (entry.getCondition().equals(userObject.getWeibo())) {
															userObject.setUid(entry.getUid());
															has = true;
															break;
														}
													}
													if (has) {
														follows.add(userObject);
														UserVo uvo = new UserVo();
														uvo.setUserid(userObject.getUid());
														uvo.setWeibo(userObject.getWeibo());
														uvo.setWeiboName(userObject.getWeiboName());
														dao.insertOrUpdateByUserid(uvo);
													} else {
														intives.add(userObject);
													}
												}
											}
										}
										// 删除我关注的用户
										final List<UserVo> followUsers = dao.getUsersByRelation(1, 1, 1);
										for (int i = follows.size() - 1; i >= 0; i--) {
											UserObject uobj = follows.get(i);
											for (int j = 0; j < followUsers.size(); j++) {
												UserVo uvo = followUsers.get(j);
												if (uobj.getUid() == uvo.getUserid()) {
													follows.remove(i);
												}
											}
										}
										asyncTask.getProxyCallBack().onSuccess(contactObj);
									}

									@Override
									public void onFailure(Integer result, String resultMsg) {
										asyncTask.getProxyCallBack().onFailure(result, null);
									}
								}, context, getWeiboUserIds(list), 1);

								userService.upWeibo(new ServiceCallBack<XAction.XActionResult>() {

									@Override
									public void onSuccess(XActionResult result) {
										// TODO Auto-generated method stub

									}

									@Override
									public void onFailure(Integer result, String resultMsg) {
										// TODO Auto-generated method stub

									}
								}, context, res.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					public void onCancel(Platform weibo, int action) {
						if (action == Platform.ACTION_GETTING_FRIEND_LIST) {
							// 在这里处理取消获取关注列表的代码
						}
					}
				});
				weibo.listFriend(50, 0, null); // 获取授权账号的列表则传递null
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 获取贴吧IDS
	 * 
	 * @param lists
	 * @return
	 */
	private String getWeiboUserIds(List<UserObject> list) {
		if (list != null) {
			StringBuffer strBuf = new StringBuffer();
			int listLength = list.size();
			for (int i = 0; i < listLength; i++) {
				UserObject uo = list.get(i);
				strBuf.append(uo.getWeibo());
				strBuf.append(",");
			}
			if (strBuf.length() > 0)
				return strBuf.substring(0, strBuf.length() - 1);
			return null;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#getRecommondUser(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getRecommondUser(ProxyCallBack<List<UserObject>> callback, Context context) {
		// 查找我关注的好友
		final UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		final List<UserVo> unfollowUsers = dao.getUsersByRelation(1, 0, 1);
		// 查找本地联系人
		List<Map<String, String>> contacts = ContactUtil.getPhoneContacts(context);
		final List<UserObject> list = new ArrayList<UserObject>();
		int unfollowUsersSize = unfollowUsers.size();
		for (int j = 0; j < unfollowUsersSize; j++) {
			UserVo uvo = unfollowUsers.get(j);
			if (uvo.getUserid() == SystemContext.getInstance().getExtUserVo().getUserid())
				continue;
			boolean contactUser = false;
			int contactsLength = contacts.size();
			for (int i = 0; i < contactsLength; i++) {
				Map<String, String> cnt = contacts.get(i);
				String number = cnt.get(Phone.NUMBER);
				if (number != null && uvo != null && uvo.getMobile() != null && uvo.getMobile().equals(number)) {
					UserObject uobj = new UserObject();
					uobj.setContactName(cnt.get(Phone.DISPLAY_NAME));
					uobj.setMobile(cnt.get(Phone.NUMBER));
					uobj.setUid(uvo.getUserid());
					uobj.setAvatar(uvo.getAvatar());
					uobj.setAge(uvo.getAge());
					uobj.setGrade(uvo.getGrade());
					uobj.setSex(uvo.getSex());
					uobj.setMood(uvo.getMood());
					uobj.setNickname(uvo.getUsername());
					uobj.setWeibo(uvo.getWeibo());
					uobj.setWeiboName(uvo.getWeiboName());
					uobj.setUsertype(UserObject.USER_SOURCE_TYPE_CONTACT);
					list.add(uobj);
					contactUser = true;
					break;
				}
			}
			if (!contactUser && uvo.getWeibo() != null && !uvo.getWeibo().isEmpty()) {
				UserObject uobj = new UserObject();
				uobj.setContactName(uvo.getMobileName());
				uobj.setMobile(uvo.getMobile());
				uobj.setUid(uvo.getUserid());
				uobj.setAvatar(uvo.getAvatar());
				uobj.setAge(uvo.getAge());
				uobj.setSex(uvo.getSex());
				uobj.setGrade(uvo.getGrade());
				uobj.setMood(uvo.getMood());
				uobj.setNickname(uvo.getUsername());
				uobj.setWeibo(uvo.getWeibo());
				uobj.setWeiboName(uvo.getWeiboName());
				uobj.setUsertype(UserObject.USER_SOURCE_TYPE_WEIBO);
				list.add(uobj);
			}
		}
		callback.onSuccess(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#sharePost(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void share(final ProxyCallBack<Integer> callback, final Context context, final long id, final int type, final int target) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getCommonRemoteService().share(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}

				}, context, id, type, target);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	public void shareForShortUrl(final ProxyCallBack<String> callback, final Context context, final long id, final int type, final int target,
			final int tagId) {
		final MyAsyncTask<String> asyncTask = new MyAsyncTask<String>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getCommonRemoteService().shareForShortUrl(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.userShareInfoResult)) {
							UserShareInfoResult shareInfoResult = result.getExtension(Msgs.userShareInfoResult);
							if (shareInfoResult != null) {
								String url = shareInfoResult.getShorturl();
								if (url != null && !url.isEmpty()) {
									asyncTask.getProxyCallBack().onSuccess(url);
								} else {
									asyncTask.getProxyCallBack().onSuccess(null);
								}
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}

				}, context, id, type, target, tagId);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	public void shareWebPageForShortUrl(final ProxyCallBack<String> callback, final Context context, final long id, final int type, final int target) {
		final MyAsyncTask<String> asyncTask = new MyAsyncTask<String>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getCommonRemoteService().shareWebPageForShortUrl(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.userShareInfoResult)) {
							UserShareInfoResult shareInfoResult = result.getExtension(Msgs.userShareInfoResult);
							if (shareInfoResult != null) {
								String url = shareInfoResult.getShorturl();
								if (url != null && !url.isEmpty()) {
									asyncTask.getProxyCallBack().onSuccess(url);
								} else {
									asyncTask.getProxyCallBack().onSuccess(null);
								}
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}

				}, context, id, type, target);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#getTrend(com.iwgame.msgs.
	 * common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void getTrend(final ProxyCallBack<List<UserNewsVo>> callback, final Context context, final String uids) {
		final MyAsyncTask<List<UserNewsVo>> asyncTask = new MyAsyncTask<List<UserNewsVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getUserRemoteService().getTrend(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.userNewsResult)) {
							UserNewsResult unResult = result.getExtension(Msgs.userNewsResult);
							if (userService != null) {
								List<UserNews> unList = unResult.getUserNewsList();
								if (unList != null && unList.size() > 0) {
									List<UserNewsVo> unvoList = new ArrayList<UserNewsVo>();
									int unListLength = unList.size();
									for (int i = 0; i < unListLength; i++) {
										UserNews news = unList.get(i);
										UserNewsVo vo = new UserNewsVo();
										vo.setUid(news.getUid());
										vo.setNews(news.getNews());
										unvoList.add(vo);
									}
									asyncTask.getProxyCallBack().onSuccess(unvoList);
								} else {
									asyncTask.getProxyCallBack().onSuccess(null);
								}
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, uids);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#getLimitedOPCount(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, int)
	 */
	@Override
	public void getLimitedOPCount(final ProxyCallBack<Map<Integer, Integer>> callback, final Context context, final int limitedop) {
		final MyAsyncTask<Map<Integer, Integer>> asyncTask = new MyAsyncTask<Map<Integer, Integer>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceCallBack<XActionResult> serviceCallBack = new ServiceCallBack<XActionResult>() {
					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							Map<Integer, Integer> map = new HashMap<Integer, Integer>();
							if (result.hasExtension(Msgs.limitedOPCountResult) && result.getExtension(Msgs.limitedOPCountResult) != null) {
								List<LimitedOPCount> list = result.getExtension(Msgs.limitedOPCountResult).getOpCountList();
								int listSize = list.size();
								for (int i = 0; i < listSize; i++) {
									LimitedOPCount item = list.get(i);
									map.put(item.getLop(), item.getCount());
								}
							}

							asyncTask.getProxyCallBack().onSuccess(map);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				};
				ServiceFactory.getInstance().getUserRemoteService().getLimitedOPCount(serviceCallBack, context, limitedop);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#upContact(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void upContacts(ProxyCallBack<Integer> callback, Context context) {
		// 查找本地联系人
		List<Map<String, String>> contacts = ContactUtil.getPhoneContacts(context);
		upContacts(callback, contacts, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#upWeiboFriends(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void upWeiboFriends(final ProxyCallBack<Integer> callback, final Context context) {
		// 获取当前已保存过的 Token
		// Oauth2AccessToken mAccessToken =
		// AccessTokenKeeper.readAccessToken(context);
		// if (mAccessToken != null && mAccessToken.isSessionValid()) {
		// // 获取微博好友
		// new
		// FriendshipsAPI(mAccessToken).bilateral(Long.valueOf(mAccessToken.getUid()),
		// 50, 1, new RequestListener() {
		//
		// @Override
		// public void onWeiboException(WeiboException arg0) {
		// callback.onFailure(-1, null);
		// }
		//
		// @Override
		// public void onComplete(String arg0) {
		// userService.upWeibo(new ServiceCallBack<XAction.XActionResult>() {
		//
		// @Override
		// public void onSuccess(XActionResult result) {
		// callback.onSuccess(result.getRc());
		// }
		//
		// @Override
		// public void onFailure(Integer result, String resultMsg) {
		// callback.onFailure(-1, null);
		// }
		// }, context, arg0);
		// }
		// });
		//
		// } else {
		// callback.onFailure(-1, null);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#fetchTaskPoint(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, int)
	 */
	@Override
	public void getUserExperience(final ProxyCallBack<List<ExtUserVo>> callback, final Context context, final String uids) {
		final MyAsyncTask<List<ExtUserVo>> asyncTask = new MyAsyncTask<List<ExtUserVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSearchRemoteService().getExperienceValue(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.pointEntityResult)) {
							PointEntityResult peResult = result.getExtension(Msgs.pointEntityResult);
							List<PointEntity> list = peResult.getPointEntityList();
							if (list != null && list.size() > 0) {
								List<ExtUserVo> rlist = new ArrayList<ExtUserVo>();
								int listSize = list.size();
								for (int i = 0; i < listSize; i++) {
									PointEntity entity = list.get(i);
									ExtUserVo vo = new ExtUserVo();
									vo.setUserid(entity.getTid());
									vo.setExp(entity.getExp());
									rlist.add(vo);
								}
								asyncTask.getProxyCallBack().onSuccess(rlist);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, uids, 0);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	public void getUserPoint(final ProxyCallBack<List<ExtUserVo>> callback, final Context context, final String uids) {
		final MyAsyncTask<List<ExtUserVo>> asyncTask = new MyAsyncTask<List<ExtUserVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSearchRemoteService().getPoint(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.pointEntityResult)) {
							PointEntityResult peResult = result.getExtension(Msgs.pointEntityResult);
							List<PointEntity> list = peResult.getPointEntityList();
							if (list != null && list.size() > 0) {
								List<ExtUserVo> rlist = new ArrayList<ExtUserVo>();
								int listSize = list.size();
								for (int i = 0; i < listSize; i++) {
									PointEntity entity = list.get(i);
									ExtUserVo vo = new ExtUserVo();
									vo.setUserid(entity.getTid());
									vo.setPoint(entity.getPoint());
									rlist.add(vo);
								}
								asyncTask.getProxyCallBack().onSuccess(rlist);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, uids, 0);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#getPointTask(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context, int)
	 */
	@Override
	public void getPointTask(final ProxyCallBack<List<UserPointTaskObj>> callback, final Context context, final int type, final int code) {
		final MyAsyncTask<List<UserPointTaskObj>> asyncTask = new MyAsyncTask<List<UserPointTaskObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final PointTaskDao pointTaskDao = DaoFactory.getDaoFactory().getPointTaskDao(SystemContext.getInstance().getContext());
				ServiceFactory.getInstance().getUserRemoteService().getPointTask(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.pointTaskDataResult)) {
							PointTaskDataResult ptdResult = result.getExtension(Msgs.pointTaskDataResult);
							List<PointTaskData> list = ptdResult.getPointTaskDataList();
							if (list != null && list.size() > 0) {
								List<UserPointTaskObj> tasklist = new ArrayList<UserPointTaskObj>();
								int listSize = list.size();
								for (int i = 0; i < listSize; i++) {
									
									PointTaskData ptData = list.get(i);
									
									PointTaskVo vo = pointTaskDao.queryByTaskId(ptData.getTaskid());
									if (vo == null || vo.getStatus() == 1) {
										continue;
									} else {
										UserPointTaskObj obj = new UserPointTaskObj();
										obj.setTaskid(ptData.getTaskid());
										obj.setTimes(ptData.getTimes());
										obj.setStatus(ptData.getStatus());
										obj.setPointTask(vo);
										tasklist.add(obj);
									
									}
								}
								asyncTask.getProxyCallBack().onSuccess(tasklist);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, type, code);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#getPointTaskDetail(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getPointTaskDetail(final ProxyCallBack<List<PointTaskVo>> callback, final Context context) {
		final MyAsyncTask<List<PointTaskVo>> asyncTask = new MyAsyncTask<List<PointTaskVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final PointTaskDao pointTaskDao = DaoFactory.getDaoFactory().getPointTaskDao(SystemContext.getInstance().getContext());
				long syncKey = SystemContext.getInstance().getPointTaskSyncKey();
				ServiceFactory.getInstance().getSearchRemoteService().syncPointOrTaskPolicy(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						PointConfigDataResult pcdResult = result.getExtension(Msgs.pointConfigDataResult);
						List<PointTaskConfig> list = pcdResult.getPointTaskConfigList();
						if (list != null && list.size() > 0) {
							int listSize = list.size();
							List<PointTaskVo> pointTasklist = new ArrayList<PointTaskVo>();
							Map<Integer,PointTaskVo> map = new HashMap<Integer,PointTaskVo>();
							for (int j = 0; j < listSize; j++) {
								PointTaskVo vo = new PointTaskVo();
								PointTaskConfig config = list.get(j);
								vo.setPoint(config.getPoint());
								vo.setStatus(config.getStatus());
								vo.setTaskdesc(config.getDesc());
								vo.setTaskid(config.getTaskid());
								vo.setTaskname(config.getTaskname());
								vo.setTimes(config.getTimes());
								vo.setType(config.getType());
								vo.setToid(config.getToid());
								vo.setDetail(config.getDetails());
								vo.setExp(config.getExp());
								vo.setExptimes(config.getExptimes());
								vo.setGids(config.getGids());
								vo.setDetailsBytes(config.getDetailsBytes().toString());
								vo.setPostContent(config.getDetailsContent());
								map.put(config.getTaskid(), vo);
								pointTasklist.add(vo);
								
							}
							CacheImpl.getInstance().saveData(Cache.DATA_TYPE_TASK_POINTTASK, map);
							pointTaskDao.insertOrUpdatePointTask(pointTasklist);
							SystemContext.getInstance().setPointTaskSyncKey(pcdResult.getUpdatetime());
							SystemContext.getInstance().pointTaskConfig = pointTaskDao.queryAll();
							asyncTask.getProxyCallBack().onSuccess(SystemContext.getInstance().pointTaskConfig);
						} else {
							if (SystemContext.getInstance().pointTaskConfig == null) {
								SystemContext.getInstance().pointTaskConfig = pointTaskDao.queryAll();
							}
							asyncTask.getProxyCallBack().onSuccess(SystemContext.getInstance().pointTaskConfig);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if (SystemContext.getInstance().pointTaskConfig == null) {
							SystemContext.getInstance().pointTaskConfig = pointTaskDao.queryAll();
						}
						callback.onSuccess(SystemContext.getInstance().pointTaskConfig);
					}
				}, context, 1, syncKey);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#getUserGradePolicy(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getUserGradePolicy(final ProxyCallBack<List<UserGradeVo>> callback, final Context context) {
		final MyAsyncTask<List<UserGradeVo>> asyncTask = new MyAsyncTask<List<UserGradeVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final UserGradeDao userGradeDao = DaoFactory.getDaoFactory().getUserGradeDao(SystemContext.getInstance().getContext());
				long syncKey = SystemContext.getInstance().getUserGradePolicySyncKey();
				ServiceFactory.getInstance().getSearchRemoteService().syncPointOrTaskPolicy(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						PointConfigDataResult pcdResult = result.getExtension(Msgs.pointConfigDataResult);
						List<UserGradeConfig> list = pcdResult.getUserGradeConfigList();
						if (list != null && list.size() > 0) {
							int listSize = list.size();
							for (int j = 0; j < listSize; j++) {
								UserGradeVo vo = new UserGradeVo();
								UserGradeConfig config = list.get(j);
								vo.setOptions(config.getOptions());
								vo.setMultiple(config.getMultiple());
								vo.setCreategroup(config.getCreategroup());
								vo.setFollowgame(config.getFollowgame());
								vo.setGrade(config.getGrade());
								vo.setJoingroup(config.getJoingroup());
								vo.setPoint(config.getPoint());
								vo.setSendpost(config.getPost());
								vo.setDatelimit(config.getEverydaylimit());
								vo.setStatus(config.getStatus());
								userGradeDao.insertOrUpdate(vo);
							}
							SystemContext.getInstance().setUserGradePolicySyncKey(pcdResult.getUpdatetime());
							SystemContext.getInstance().userGradeConfig = userGradeDao.queryAll();
							asyncTask.getProxyCallBack().onSuccess(SystemContext.getInstance().userGradeConfig);
						} else {
							if (SystemContext.getInstance().userGradeConfig == null) {
								SystemContext.getInstance().userGradeConfig = userGradeDao.queryAll();
							}
							asyncTask.getProxyCallBack().onSuccess(SystemContext.getInstance().userGradeConfig);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if (SystemContext.getInstance().userGradeConfig == null) {
							SystemContext.getInstance().userGradeConfig = userGradeDao.queryAll();
						}
						callback.onSuccess(SystemContext.getInstance().userGradeConfig);
					}
				}, context, 2, syncKey);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#userExperienceValueDetail
	 * (com.iwgame .msgs.common.ProxyCallBack, android.content.Context, long,
	 * int)
	 */
	@Override
	public void getUserHistroyExperienceValueDetail(final ProxyCallBack<List<UserPointDetailObj>> callback, final Context context, final long offset,
			final int limit) {
		final MyAsyncTask<List<UserPointDetailObj>> asyncTask = new MyAsyncTask<List<UserPointDetailObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getUserRemoteService().getUserHistoryExperienceDetail(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.userPointDetailsResult)) {
							UserPointDetailsResult updResult = result.getExtension(Msgs.userPointDetailsResult);
							List<UserPointDetail> list = updResult.getUserPointDetailList();
							if (list != null && list.size() > 0) {
								List<UserPointDetailObj> rlist = new ArrayList<UserPointDetailObj>();
								int listsize = list.size();
								for (int i = 0; i < listsize; i++) {
									UserPointDetail detail = list.get(i);
									UserPointDetailObj obj = new UserPointDetailObj();
									obj.setOptime(detail.getOptime());
									obj.setAname(detail.getAname());
									obj.setPoint(detail.getPoint());
									obj.setTotalpoint(detail.getTotalpoint());
									rlist.add(obj);
								}
								asyncTask.getProxyCallBack().onSuccess(rlist);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, offset, limit);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	public void getUserHistroyPointDetail(final ProxyCallBack<List<UserPointDetailObj>> callback, final Context context, final long offset,
			final int limit) {
		final MyAsyncTask<List<UserPointDetailObj>> asyncTask = new MyAsyncTask<List<UserPointDetailObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getUserRemoteService().getUserHistoryPointDetail(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.userPointDetailsResult)) {
							UserPointDetailsResult updResult = result.getExtension(Msgs.userPointDetailsResult);
							List<UserPointDetail> list = updResult.getUserPointDetailList();
							if (list != null && list.size() > 0) {
								List<UserPointDetailObj> rlist = new ArrayList<UserPointDetailObj>();
								int listsize = list.size();
								for (int i = 0; i < listsize; i++) {
									UserPointDetail detail = list.get(i);
									UserPointDetailObj obj = new UserPointDetailObj();
									obj.setOptime(detail.getOptime());
									obj.setAname(detail.getAname());
									obj.setPoint(detail.getPoint());
									obj.setTotalpoint(detail.getTotalpoint());
									rlist.add(obj);
								}
								asyncTask.getProxyCallBack().onSuccess(rlist);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, offset, limit);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#getPointTaskDetailById(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context, int)
	 */
	@Override
	public void getPointTaskDetailById(ProxyCallBack<PointTaskVo> callback, Context context, int tid) {
		final PointTaskDao pointTaskDao = DaoFactory.getDaoFactory().getPointTaskDao(SystemContext.getInstance().getContext());
		callback.onSuccess(pointTaskDao.queryByTaskId(tid));
	}

	/**
	 * 去获取用户的扩展数据， 关注数，公会数，等
	 */
	@Override
	public void getExtUserData(final ProxyCallBack<List<ExtUserVo>> callback, final Context context, final String ids, final int type) {
		final MyAsyncTask<List<ExtUserVo>> asyncTask = new MyAsyncTask<List<ExtUserVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getUserRemoteService().getExtUserData(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.contentExtDataResult)) {
							ContentExtDataResult contentExtResult = result.getExtension(Msgs.contentExtDataResult);
							if (contentExtResult != null && contentExtResult.getContentExtDataListList() != null
									&& contentExtResult.getContentExtDataListList().size() > 0) {
								ContentExtData data = contentExtResult.getContentExtDataListList().get(0);
								UserContentExtData userContentExtData = data.getUserExt();
								List<ExtUserVo> listext = new ArrayList<ExtUserVo>();
								if (userContentExtData != null) {
									ExtUserVo vo = new ExtUserVo();
									vo.setPostCount(userContentExtData.getPostcount());
									vo.setGroupCount(userContentExtData.getGroupcount());
									vo.setGameCount(userContentExtData.getGamecount());
									vo.setFansCount(userContentExtData.getFanscount());
									vo.setFollowCount(userContentExtData.getFollowcount());
									vo.setFavoritesCount(userContentExtData.getFavoritescount());
									listext.add(vo);
									asyncTask.getProxyCallBack().onSuccess(listext);
								} else {
									asyncTask.getProxyCallBack().onSuccess(null);
								}
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {

						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, ids, type);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 用户行为动作之 公会禁言
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void userAction(final Context context, final String position, final long tid, final int ttype, final int op, final String content,
			final String remark, final ProxyCallBack<Integer> callback) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.userAction(context, position, tid, ttype, op, content, remark, new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				});
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	@Override
	public void SendPushCount(final ProxyCallBack<Integer> callback, final Context context, final long pushid, final int action) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.SendPushCount(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, pushid, action);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/*
	 * 8 (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#getPointTab(com.iwgame.msgs
	 * .common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getPointTab(final ProxyCallBack<List<GoodsTab>> callback, final Context context) {
		final MyAsyncTask<List<GoodsTab>> asyncTask = new MyAsyncTask<List<GoodsTab>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.getPointTab(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.goodsCategoryResult)) {
							GoodsCategoryResult contentExtResult = result.getExtension(Msgs.goodsCategoryResult);
							if (contentExtResult != null) {
								List<GoodsCategory> goodslist = contentExtResult.getCategoryList();
								if (goodslist.size() > 0) {
									GoodsTab tab;
									GoodsCategory category;
									List<GoodsTab> list = new ArrayList<GoodsTab>();
									int goodsListSize = goodslist.size();
									for (int i = 0; i < goodsListSize; i++) {
										tab = new GoodsTab();
										category = goodslist.get(i);
										tab.setId(category.getId());
										tab.setName(category.getName());
										list.add(tab);
									}
									asyncTask.getProxyCallBack().onSuccess(list);
								} else {
									asyncTask.getProxyCallBack().onSuccess(null);
								}

							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 获取商品详细列表
	 */
	@Override
	public void getGoodsList(final ProxyCallBack<List<Goods>> callback, final Context context, final long categoryId, final String goodsStatus,
			final int goodType, final int offset, final int limit) {
		final MyAsyncTask<List<Goods>> asyncTask = new MyAsyncTask<List<Goods>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.getGoodsList(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {

						if (result.hasExtension(Msgs.goodsResult)) {
							GoodsResult goodsResult = result.getExtension(Msgs.goodsResult);
							List<com.iwgame.msgs.proto.Msgs.Goods> goodslList = goodsResult.getGoodsList();
							List<Goods> list = new ArrayList<Goods>();
							com.iwgame.msgs.proto.Msgs.Goods good;
							Goods goods;
							if (goodslList.size() > 0) {
								int goodsListLength = goodslList.size();
								for (int i = 0; i < goodsListLength; i++) {
									goods = new Goods();
									good = goodslList.get(i);
									goods.setId(good.getId());
									goods.setName(good.getName());
									goods.setType(good.getType());
									goods.setIcon(good.getIcon());
									goods.setDeliveryNum(good.getDeliveryType());
									goods.setRemainNum(good.getRemainNum());
									goods.setObtainNum(good.getObtainedNum());
									goods.setNeedPoint(good.getNeedPrice());
									goods.setNeedLevel(good.getNeedLevel());
									goods.setWeight(good.getWeight());
									goods.setShowTime(good.getShowTime());
									goods.setTransTime(good.getTransTime());
									goods.setTransTimes(good.getTransTimes());
									goods.setOffTime(good.getOffTime());
									goods.setDeliveryTemplateID(good.getDeliveryTemplateID());
									goods.setTransTemplateID(good.getTransTemplateID());
									goods.setGoodsStatus(good.getGoodsStatus());
									if (goods != null && goods.getGoodsStatus() != 4) {
										list.add(goods);
									}
								}
								asyncTask.getProxyCallBack().onSuccess(list);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						}

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, categoryId, goodsStatus, goodType, offset, limit);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 获取兑换记录
	 */
	@Override
	public void getChangeRecords(final ProxyCallBack<List<ChangeRecordsEntity>> callback, final Context context, final int offset, final int limit) {
		final MyAsyncTask<List<ChangeRecordsEntity>> asyncTask = new MyAsyncTask<List<ChangeRecordsEntity>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.getChangeRecords(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.transDetailResult)) {
							TransDetailsResult transDetailsResult = result.getExtension(Msgs.transDetailResult);
							if (transDetailsResult != null) {
								List<TransDetail> listTrans = transDetailsResult.getTransDetailList();
								List<ChangeRecordsEntity> changeRecordsList = new ArrayList<ChangeRecordsEntity>();
								TransDetail detail;
								ChangeRecordsEntity entity;
								if (listTrans != null && listTrans.size() > 0) {
									int listTransLength = listTrans.size();
									for (int i = 0; i < listTransLength; i++) {
										entity = new ChangeRecordsEntity();
										detail = listTrans.get(i);
										entity.setId(detail.getId());
										entity.setGoodsName(detail.getName());
										entity.setType(detail.getType());
										entity.setIcon(detail.getIcon());
										entity.setDeliveryType(detail.getDeliveryType());
										entity.setNeedPrice(detail.getNeedPrice());
										entity.setTransTime(detail.getTransTime());
										entity.setDeliveryTime(detail.getDeliveryTime());
										entity.setDeliveryDetail(detail.getDeliveryTemplateID());
										entity.setTransDetail(detail.getTransTemplateID());
										entity.setGoodsId(detail.getGoodsId());
										changeRecordsList.add(entity);
									}
									asyncTask.getProxyCallBack().onSuccess(changeRecordsList);
								} else {
									asyncTask.getProxyCallBack().onSuccess(null);
								}
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}

						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, offset, limit);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 获取商品的详情
	 */
	@Override
	public void getGoodsDetail(final ProxyCallBack<Goods> callback, final Context context, final long goodsId) {
		final MyAsyncTask<Goods> asyncTask = new MyAsyncTask<Goods>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.getGoodsDetail(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.goods)) {
							com.iwgame.msgs.proto.Msgs.Goods goodserver = result.getExtension(Msgs.goods);
							if (goodserver != null) {
								Goods goodsNative = new Goods();
								GoodsDetail goodsDetail;
								goodsNative.setId(goodserver.getId());
								goodsNative.setName(goodserver.getName());
								goodsNative.setType(goodserver.getType());
								goodsNative.setIcon(goodserver.getIcon());
								goodsNative.setDeliveryNum(goodserver.getDeliveryType());
								goodsNative.setRemainNum(goodserver.getRemainNum());
								goodsNative.setObtainNum(goodserver.getObtainedNum());
								goodsNative.setNeedPoint(goodserver.getNeedPrice());
								goodsNative.setNeedLevel(goodserver.getNeedLevel());
								goodsNative.setWeight(goodserver.getWeight());
								goodsNative.setTransTime(goodserver.getTransTime());
								goodsNative.setShowTime(goodserver.getShowTime());
								goodsNative.setTransTimes(goodserver.getTransTimes());
								goodsNative.setOffTime(goodserver.getOffTime());
								goodsNative.setDeliveryTemplateID(goodserver.getDeliveryTemplateID());
								goodsNative.setTransTemplateID(goodserver.getTransTemplateID());
								goodsNative.setGoodsStatus(goodserver.getGoodsStatus());
								GoodsDetailResult goodsDetailResult = goodserver.getGoodsDetails();
								List<GoodsDetail> goodsDetailsList = new ArrayList<GoodsDetail>();
								if (goodsDetailResult != null) {
									List<com.iwgame.msgs.proto.Msgs.GoodsDetail> list = goodsDetailResult.getGoodsDetailList();
									if (list != null && list.size() > 0) {
										int listLength = list.size();
										for (int i = 0; i < listLength; i++) {
											com.iwgame.msgs.proto.Msgs.GoodsDetail detail = list.get(i);
											goodsDetail = new GoodsDetail();
											goodsDetail.setId(detail.getId());
											goodsDetail.setType(detail.getDetailType());
											goodsDetail.setDetailItem(detail.getDetailItem());
											goodsDetailsList.add(goodsDetail);
										}
									}
								}
								goodsNative.setDetail(goodsDetailsList);
								asyncTask.getProxyCallBack().onSuccess(goodsNative);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, goodsId);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 获取订单详情
	 */
	@Override
	public void getOrderDetail(final ProxyCallBack<OrderDetail> callBack, final Context context, final long transId, final int deliveryType) {
		final MyAsyncTask<OrderDetail> asyncTask = new MyAsyncTask<OrderDetail>(callBack);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.getOrderDetail(new ServiceCallBack<XAction.XActionResult>() {
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.orderDetailResult)) {
							OrderDetailResult orderDetailResult = result.getExtension(Msgs.orderDetailResult);
							if (orderDetailResult != null) {
								OrderDetail detail = new OrderDetail();
								detail.setStatus(orderDetailResult.getStatus());
								detail.setOrderInfo(orderDetailResult.getOrderInfo());
								asyncTask.getProxyCallBack().onSuccess(detail);
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					};

					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					};
				}, context, transId, deliveryType);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 兑换商品接口
	 */
	@Override
	public void transGoods(final ProxyCallBack<String> callback, final Context context, final long goodsId, final String transInfo) {
		final MyAsyncTask<String> asyncTask = new MyAsyncTask<String>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.transGoods(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.transSuccessResult)) {
							TransSuccessResult transSuccessResult = result.getExtension(Msgs.transSuccessResult);
							if (transSuccessResult != null) {
								String msgs = transSuccessResult.getSuccessInfo();
								asyncTask.getProxyCallBack().onSuccess(msgs);
							} else {
								asyncTask.getProxyCallBack().onSuccess("");
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess("");
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, goodsId, transInfo);
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
	 * com.iwgame.msgs.module.user.logic.UserProxy#collectAppInfo(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void collectAppInfo(final ProxyCallBack<Integer> callback, final Context context, final String app) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				commonService.collectAppInfo(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null)
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, app);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 下载网络图片到本地并获取文件路径
	 */
	@Override
	public void getImageUrlToPath(ProxyCallBack<String> callback, final Context context, final String imageName) {
		final MyAsyncTask<String> asyncTask = new MyAsyncTask<String>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String url = ResUtil.getSmallRelUrl(imageName);
				if (url != null) {
					Bitmap bitmap = BitmapUtil.getBitmapFromURL(url);
					if (bitmap != null) {
						String imagePath = ImageUtil.saveBitmapToLocal(bitmap, "/msg_share_temp.jpg", null, 100);
						if (imagePath != null && !imagePath.isEmpty()) {
							File f = new File(imagePath);
							// 判断图片是否生成成功
							if (f != null && f.exists()) {
								asyncTask.getProxyCallBack().onSuccess(imagePath);
							} else {
								asyncTask.getProxyCallBack().onFailure(0, null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(0, null);
						}
						if (bitmap != null && !bitmap.isRecycled()) {
							// 回收并且置为null
							bitmap.recycle();
							bitmap = null;
						}
						// System.gc();
					} else {
						asyncTask.getProxyCallBack().onFailure(0, null);
					}
				} else {
					asyncTask.getProxyCallBack().onFailure(0, null);
				}
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 保存图片到data/data下
	 */
	@Override
	public void getImageUrlToData(ProxyCallBack<Integer> callback, final Context context, final String imageUrl, final String fileName) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				boolean result = BitmapUtil.saveUrlBitmapToData(context, imageUrl, fileName);
				if (result) {
					asyncTask.getProxyCallBack().onSuccess(com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE);
				} else {
					asyncTask.getProxyCallBack().onFailure(0, null);
				}
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 这个接口是用于在 邀请成员的时候 去通过keywords筛选用的
	 */
	@Override
	public void getFollowUsers(ProxyCallBack<List<UserItemObj>> callback, Context context, int type, int relation, int sort, int pageNo, int size,
			String keyword, long grid) {

		LogUtil.d(TAG, "--------获取通讯录好友数据PROXY：type=" + type + ", relation=" + relation + ", keyword=" + keyword);
		UserDao dao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		List<UserItemObj> result = null;
		if (keyword != null) {
			result = dao.getFollowUserByRelation(type, relation, 1, keyword, grid);
		}
		callback.onSuccess(result);

	}

	/**
	 * 通过游戏id 还有keywords 查询用户的详细信息
	 */
	@Override
	public void searchUsers(final ProxyCallBack<PagerVo<UserObject>> callback, final Context context, final Long gid, final Boolean isfate,
			final Boolean isrecommend, final Integer rel, final String resulttype, final String uid, final long offset, final int limit,
			final Integer nearDistance, final Integer lastlogin, final Integer sex, final Boolean isfind, final Integer source, final String keywords) {
		final MyAsyncTask<PagerVo<UserObject>> asyncTask = new MyAsyncTask<PagerVo<UserObject>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				keyWords = keywords;
				String position = SystemContext.getInstance().getLocation();
				searchService.searchUsers(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						PagerVo<UserObject> pagerVo = null;
						if (result.hasExtension(Msgs.userQueryResult)) {
							UserQueryResult userQueryResult = result.getExtension(Msgs.userQueryResult);
							List<UserQueryEntry> list = userQueryResult.getEntryList();
							pagerVo = new PagerVo<UserObject>();
							if (list != null) {
								int listSize = list.size();
								UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
								Map<Long, UserVo> map = userDao.getUsersByRel(1, 1, 1);
								List<UserObject> items = new ArrayList<UserObject>();
								for (int i = 0; i < listSize; i++) {
									UserQueryEntry entry = list.get(i);
									UserQueryInfo info = entry.getUserInfo();
									UserObject vo = new UserObject();
									if (map.containsKey(info.getUid())) {
										UserVo uservo = map.get(info.getUid());
										String remark = uservo.getRemarksName();
										if ((remark != null && remark.indexOf(keyWords) >= 0)
												|| ((remark == null || remark.trim().length() <= 0) && (info.getNickname() == null || info
														.getNickname().indexOf(keyWords) >= 0))) {
											vo.setAvatar(info.getAvatar());
											vo.setMood(info.getMood());
											vo.setUid(info.getUid());
											vo.setNickname(info.getNickname());
											vo.setSex(info.getSex());
											vo.setAge(info.getAge());
											vo.setGids(entry.getGidsList());
											vo.setGameCount(entry.getGameCount());
											vo.setRel(entry.getRel());
											if (entry.hasDistance())
												vo.setDistance(entry.getDistance());
											vo.setLastLogin(entry.getLastLogin());
											vo.setGrade(info.getGrade());
											items.add(vo);
										}
									} else if (info.getNickname() == null || info.getNickname().indexOf(keyWords) >= 0) {
										vo.setAvatar(info.getAvatar());
										vo.setMood(info.getMood());
										vo.setUid(info.getUid());
										vo.setNickname(info.getNickname());
										vo.setSex(info.getSex());
										vo.setAge(info.getAge());
										vo.setGids(entry.getGidsList());
										vo.setGameCount(entry.getGameCount());
										vo.setRel(entry.getRel());
										if (entry.hasDistance())
											vo.setDistance(entry.getDistance());
										vo.setLastLogin(entry.getLastLogin());
										vo.setGrade(info.getGrade());
										items.add(vo);
									}
								}
								pagerVo.setItems(items);
								pagerVo.setOffset(userQueryResult.getOffset());
								asyncTask.getProxyCallBack().onSuccess(pagerVo);
								return;
							}
						}
						asyncTask.getProxyCallBack().onSuccess(pagerVo);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, null, isfate, rel, gid, isrecommend, resulttype, position, uid, offset, limit, nearDistance, lastlogin, sex, isfind, source, null);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 采集通讯录信息 发送到服务端
	 */
	@Override
	public void collectContactInfo(final ProxyCallBack<Integer> callback, final Context context, final UploadContactsRequest b) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				commonService.collectContactInfo(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null)
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, b);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 获取我页面的活动专区的title
	 */
	@Override
	public void getActivityDivisionTitle(ProxyCallBack<String> callBack, final Context context) {
		final MyAsyncTask<String> asyncTask = new MyAsyncTask<String>(callBack);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.getActivityDivisionTitle(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.activityInfo)) {
							ActivityInfo info = result.getExtension(Msgs.activityInfo);
							if (info != null) {
								asyncTask.getProxyCallBack().onSuccess(info.getActivityAreaName());
							} else {
								asyncTask.getProxyCallBack().onSuccess("转盘比拼");
							}
						} else {
							asyncTask.getProxyCallBack().onSuccess("转盘比拼");
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}

		});
	}

	/**
	 * 获取赞列表的用户数据
	 */
	@Override
	public void getSupportUserData(final ProxyCallBack<List<SupportUserVo>> callBack, final Context context, final long offset, final int limit,
			final long gameid) {

		final MyAsyncTask<List<SupportUserVo>> asyncTask = new MyAsyncTask<List<SupportUserVo>>(callBack);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				userService.getSupportUserData(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						List<SupportUserVo> list = new ArrayList<SupportUserVo>();
						if (result != null && result.hasExtension(Msgs.topicPraiseUsers)) {
							TopicPraiseUsers topicPraiseUsers = result.getExtension(Msgs.topicPraiseUsers);
							if (topicPraiseUsers != null) {
								List<PraiseUserInfo> infoList = topicPraiseUsers.getPraiseUserInfoList();
								if (infoList != null && infoList.size() > 0) {
									PraiseUserInfo info;
									for (int i = 0; i < infoList.size(); i++) {
										info = infoList.get(i);
										SupportUserVo vo = new SupportUserVo();
										vo.setUid(info.getUid());
										vo.setAvatar(info.getAvatar());
										vo.setUserName(info.getNickname());
										vo.setSex(info.getSex());
										vo.setAge(info.getAge());
										vo.setGrade(info.getGrade());
										vo.setCommonCount(info.getSameGames());
										vo.setTime(info.getCreatetime());
										vo.setMood(info.getMood());
										vo.setNews(info.getNews());
										list.add(vo);
									}
								}
							}
						}
						asyncTask.getProxyCallBack().onSuccess(list);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, offset, limit, gameid);
				return null;
			}

			@Override
			public void onHandle(Void result) {
			}

		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.user.logic.UserProxy#collectActionlLog(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, int, java.lang.Long,
	 * java.lang.Integer, java.lang.String)
	 */
	@Override
	public void collectActionlLog(ProxyCallBack<Integer> callback, final Context context, final int op, final Long tid, final Integer ttype,
			final String content) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				commonService.collectActionlLog(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub

					}
				}, context, op, tid, ttype, content);
				return null;
			}

			@Override
			public void onHandle(Void result) {
			}

		});
	}
	
	/**
	 * 获取用户角色数据
	 */
	@Override
	public void getUserRoleInfo(final ProxyCallBack<List<UserRoleVo>> callback,
			final Context context, final ContentDetailParams params,final  int type,final Long uid,
		final	Long gid,final Long sid) {
		final List<UserRoleVo> list = new ArrayList<UserRoleVo>();
		// 判断有没有网络
				if (NetworkUtil.isNetworkAvailable(SystemContext.getInstance().getContext())) {
					final MyAsyncTask<List<UserRoleVo>> asyncTask = new MyAsyncTask<List<UserRoleVo>>(callback);
					asyncTask.execute(new AsyncCallBack<Void>() {

						@Override
						public Void execute() {
							// 服务端获取数据
							userService.getUserRoleInfo(new ServiceCallBack<XActionResult>() {

								@Override
								public void onSuccess(XActionResult result) {
									if (result.hasExtension(Msgs.contentDetailsResult)) {
										ContentDetailsResult uiresult = result.getExtension(Msgs.contentDetailsResult);
										if (uiresult != null) {
											List<UserRoleDetail> infoList = uiresult.getUserRoleDetailList();
											if (infoList == null || infoList.size() <= 0) {
												asyncTask.getProxyCallBack().onSuccess(list);
												return;
											}
											int size = infoList.size();
											UserRoleDetail detail;
											for (int i = 0; i < size; i++) {
												detail = infoList.get(i);
												if (detail != null && detail.getUid()!= 0) {
													UserRoleEntity voEntity = BuildVoUtil.buildUserRoleEntity(detail);
													if(voEntity!=null){
															BuildVoUtil.buildUserRoleVo(voEntity,list);
													}
												}
											}
											asyncTask.getProxyCallBack().onSuccess(list);
										} else {
											LogUtil.e(TAG, "获取用户数据为空");
											asyncTask.getProxyCallBack().onSuccess(list);
										}
									} else {
										asyncTask.getProxyCallBack().onSuccess(list);
									}
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									LogUtil.e(TAG, "网络获取用户信息失败" + result);
									asyncTask.getProxyCallBack().onSuccess(list);
								}
							}, context, params, type, uid,gid,sid);
							return null;
						}

						@Override
						public void onHandle(Void result) {
							// TODO Auto-generated method stub
						}

					});
					}else{
						callback.onSuccess(list);
					}
		}
	/**
	 * 获取角色属性值
	 */
	@Override
	public void getGameKeyData(final ProxyCallBack<List<GameKeyVo>> callback,
			final Context context, final Long gid) {
		final List<GameKeyVo> gameKeylist = new ArrayList<GameKeyVo>();
		
		if (NetworkUtil.isNetworkAvailable(SystemContext.getInstance().getContext())){
			final MyAsyncTask<List<GameKeyVo>> asyncTask = new MyAsyncTask<List<GameKeyVo>>(callback);
			asyncTask.execute(new AsyncCallBack<Void>() {

				@Override
				public Void execute() {
					// TODO Auto-generated method stub
					userService.getGameRoleValues(new ServiceCallBack<XActionResult>() {
						
						@Override
						public void onSuccess(XActionResult result) {
							if(result.hasExtension(Msgs.gameRole)){
								GameRole gameRole = result.getExtension(Msgs.gameRole);
								if(gameRole!=null){
									List<GameKey> gamelist = gameRole.getGameKeyListList();
									if (gamelist == null || gamelist.size() <= 0) {
										asyncTask.getProxyCallBack().onSuccess(gameKeylist);
										return;
									}else{
										int size = gamelist.size();
										GameKey key;
										for(int i = 0 ;i<size;i++){
											key = gamelist.get(i);
											GameKeyVo vo = new GameKeyVo();
											vo.setId(key.getId());
											vo.setGid(key.getGid());
											vo.setType(key.getKeyType());
											vo.setName(key.getKeyName());
											vo.setAttrType(key.getAttrType());
											vo.setList(key.getGameKeysDetailListList());
											gameKeylist.add(vo);
										}
										asyncTask.getProxyCallBack().onSuccess(gameKeylist);
									}
								}else{
									asyncTask.getProxyCallBack().onSuccess(gameKeylist);
								}
							}else{
								asyncTask.getProxyCallBack().onSuccess(gameKeylist);
							}
						}
						
						@Override
						public void onFailure(Integer result, String resultMsg) {
							
						}
					}, context, gid);
					return null;
				}

				@Override
				public void onHandle(Void result) {
					// TODO Auto-generated method stub
					
				}
			});
		}else{
			callback.onSuccess(gameKeylist);
		}
	}
	/**
	 * 
	 */
	@Override
	public void getGameServiceData(
		final 	ProxyCallBack<List<GameRoleServiceVo>> callback,final Context context,
		final	int type,final Long uptime) {
		final List<GameRoleServiceVo> servicelist = new ArrayList<GameRoleServiceVo>();
		if (NetworkUtil.isNetworkAvailable(SystemContext.getInstance().getContext())){
			final MyAsyncTask<List<GameRoleServiceVo>> asyncTask = new MyAsyncTask<List<GameRoleServiceVo>>(callback);
			asyncTask.execute(new AsyncCallBack<Void>() {

				@Override
				public Void execute() {
					// TODO Auto-generated method stub
					userService.getGameServiceList(new ServiceCallBack<XAction.XActionResult>() {
						
						@Override
						public void onSuccess(XActionResult result) {
							if(result.hasExtension(Msgs.relationResult)){
								List<Msgs.RelationResult.RelationEntry> list =result.getExtension(Msgs.relationResult).getEntryList();
								if(list!=null&&list.size()>0){
									for(int i =0;i<list.size();i++){
										Msgs.RelationResult.RelationEntry entity = list.get(i);
										GameRoleServiceVo vo = new GameRoleServiceVo();
										vo.setId(entity.getId());
										vo.setType(entity.getType());
										vo.setName(entity.getName());
										vo.setGameId(entity.getGameid());
										servicelist.add(vo);
											}
										}
									}
							asyncTask.getProxyCallBack().onSuccess(servicelist);
						}
						
						@Override
						public void onFailure(Integer result, String resultMsg) {
							asyncTask.getProxyCallBack().onSuccess(servicelist);
						}
					}, context, type,uptime);
					return null;
				}

				@Override
				public void onHandle(Void result) {
				}
			});
		}else{
			callback.onSuccess(servicelist);
		}
	}
	/**
	 * 获取游戏列表
	 */
	
	@Override
	public void getGameList(final ProxyCallBack<List<ExtraGameVo>> callback,
		final	Context context) {
		// TODO Auto-generated method stub
		final List<ExtraGameVo> list = new ArrayList<ExtraGameVo>();
		if (NetworkUtil.isNetworkAvailable(SystemContext.getInstance().getContext())){
			final MyAsyncTask<List<ExtraGameVo>> asyncTask = new MyAsyncTask<List<ExtraGameVo>>(callback);
			asyncTask.execute(new AsyncCallBack<Void>() {

				@Override
				public Void execute() {
					LogUtil.d("gamelist", "接口");
					userService.getGameList(new ServiceCallBack<XActionResult>() {
						
						@Override
						public void onSuccess(XActionResult result) {
							LogUtil.d("gamelist", "接口2");
							if(result.hasExtension(Msgs.gameRoleBindResult)){
								GameRoleBindResult gameBindResult = result.getExtension(Msgs.gameRoleBindResult);
								List<GameRoleBind> bindlist = gameBindResult.getGameRoleBindList();
								LogUtil.d("gamelist",bindlist.size()+"");
								if(bindlist!=null&&bindlist.size()>0){
									for(int i =0;i<bindlist.size();i++){
										ExtraGameVo vo = new ExtraGameVo();
										GameRoleBind gameBind = bindlist.get(i);
										vo.setGid(gameBind.getGid());
										vo.setgName(gameBind.getGameName());
										vo.setIcon(gameBind.getIcon());
										vo.setPriority(gameBind.getPriority());
										vo.setRecomment(gameBind.getIsRecommand());
										list.add(vo);
									}
								}
							}
							asyncTask.getProxyCallBack().onSuccess(list);
						}
						
						@Override
						public void onFailure(Integer result, String resultMsg) {
							asyncTask.getProxyCallBack().onSuccess(list);
						}
					}, context);
					return null;
				}

				@Override
				public void onHandle(Void result) {
					
				}
			});
		}else{
			callback.onSuccess(list);
		}
	}
	
	/**
	 * 刪除用戶角色
	 */
	@Override
	public void deleteGameRole(final ProxyCallBack<Integer> callback,
		final	Context context, final Long roleid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>(){

			@Override
			public Void execute() {
				userService.deleteUserRoleData(new ServiceCallBack<XActionResult>() {
					
					@Override
					public void onSuccess(XActionResult result) {
						// TODO Auto-generated method stub
						if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE) {
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
							// 获取游戏服务器列表
							ProxyFactory.getInstance().getGameProxy().getGameService(new ProxyCallBack<Msgs.UserGameServerResult>() {

								@Override
								public void onSuccess(Msgs.UserGameServerResult result) {
									SystemContext.getInstance().setGameServices(result);
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									// TODO Auto-generated method stub

								}
							}, context);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}
					
					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, roleid);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
				
			}});
	}
	/**
	 * 获取游戏反馈信息
	 */
	@Override
	public void UserGameAPPly(ProxyCallBack<Integer> callback, final Context context,
			final Long gid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>(){

			@Override
			public Void execute() {
				userService.getGameApplyInfo(new ServiceCallBack<XAction.XActionResult>() {
					
					@Override
					public void onSuccess(XActionResult result) {
						if(result!=null&&result.getRc()==Msgs.ErrorCode.EC_OK_VALUE){
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
						}else{
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}
					
					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, gid);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
				
			}});
	}
	/**
	 * 增加用户角色
	 */
	@Override
	public void addUserRoleData(ProxyCallBack<Integer> callback,
			final Context context,final Long gid ,final Long sid,final String rolename ,final Msgs.UserRoleData role) {
		// TODO Auto-generated method stub
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>(){

			@Override
			public Void execute() {
				userService.addUserRoleData(new ServiceCallBack<XActionResult>() {
					
					@Override
					public void onSuccess(XActionResult result) {
						if(result!=null&&result.getRc()==Msgs.ErrorCode.EC_OK_VALUE){
							asyncTask.getProxyCallBack().onSuccess(result.getRc());
							//获取游戏服务器列表
							ProxyFactory.getInstance().getGameProxy().getGameService(new ProxyCallBack<Msgs.UserGameServerResult>() {

								@Override
								public void onSuccess(Msgs.UserGameServerResult result) {
									SystemContext.getInstance().setGameServices(result);
								}

								@Override
								public void onFailure(Integer result, String resultMsg) {
									// TODO Auto-generated method stub

								}
							}, context);
						}else{
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}
					
					@Override
					public void onFailure(Integer result, String resultMsg) {
						// TODO Auto-generated method stub
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context,gid, sid,rolename, role);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
				
			}});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.user.logic.UserProxy#getFilterUserRoleData(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, long, long, long, long, int)
	 */
	@Override
	public void getFilterUserRoleData(ProxyCallBack<UserRoleDetail> callback,
			final Context context,final long gid, final long sid, final long roleid, final long offset,
		final	int limit) {
			final MyAsyncTask<UserRoleDetail> asyncTask = new MyAsyncTask<UserRoleDetail>(callback);
			asyncTask.execute(new AsyncCallBack<Void>(){

				@Override
				public Void execute() {
					userService.getFilterRoleData(new ServiceCallBack<XAction.XActionResult>() {
						
						@Override
						public void onSuccess(XActionResult result) {
							if (result != null && result.getRc() == Msgs.ErrorCode.EC_OK_VALUE && result.hasExtension(Msgs.roleListResult)) {
								asyncTask.getProxyCallBack().onSuccess(result.getExtension(Msgs.roleListResult));
							} else {
								asyncTask.getProxyCallBack().onSuccess(null);
							}
						}
						
						@Override
						public void onFailure(Integer result, String resultMsg) {
							
						}
					}, context, gid, sid, roleid, offset, limit);
					return null;
				}

				@Override
				public void onHandle(Void result) {
					
				}});
	}

	
	
	
}
