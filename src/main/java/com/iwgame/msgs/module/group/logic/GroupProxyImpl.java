/**      
 * GroupProxyImpl.java Create on 2013-10-23     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.group.logic;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.iwgame.msgs.common.AsyncCallBack;
import com.iwgame.msgs.common.ErrorCode;
import com.iwgame.msgs.common.MyAsyncTask;
import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.common.ServiceCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupGradeDao;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.module.remote.CommonRemoteService;
import com.iwgame.msgs.module.remote.ContentRemoteService;
import com.iwgame.msgs.module.remote.SearchRemoteService;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailParams;
import com.iwgame.msgs.proto.Msgs.ContentDetailsResult;
import com.iwgame.msgs.proto.Msgs.GroupApplyQueryResult.GroupApplyQueryEntry;
import com.iwgame.msgs.proto.Msgs.GroupDetail;
import com.iwgame.msgs.proto.Msgs.GroupGradeConfig;
import com.iwgame.msgs.proto.Msgs.GroupMembersSyncResult;
import com.iwgame.msgs.proto.Msgs.IdResult;
import com.iwgame.msgs.proto.Msgs.GroupMembersSyncResult.GroupMemberEntry;
import com.iwgame.msgs.proto.Msgs.GroupQueryResult;
import com.iwgame.msgs.proto.Msgs.GroupRecommendResult;
import com.iwgame.msgs.proto.Msgs.GroupRecommendResult.GroupRecommendEntry;
import com.iwgame.msgs.proto.Msgs.IdResult;
import com.iwgame.msgs.proto.Msgs.LimitedOPCountResult;
import com.iwgame.msgs.proto.Msgs.LimitedOPCountResult.LimitedOPCount;
import com.iwgame.msgs.proto.Msgs.PointConfigDataResult;
import com.iwgame.msgs.proto.Msgs.PointEntityResult;
import com.iwgame.msgs.proto.Msgs.PointEntityResult.PointEntity;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.msgs.vo.local.PagerVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: GroupProxyImpl
 * @Description: 公会数据代理层实现类
 * @author 王卫
 * @date 2013-10-23 下午04:40:18
 * @Version 1.0
 * 
 */
public class GroupProxyImpl implements GroupProxy {

	protected static final String TAG = "GroupProxyImpl";

	private ContentRemoteService contentService = ServiceFactory.getInstance().getContentRemoteService();

	private SearchRemoteService searchService = ServiceFactory.getInstance().getSearchRemoteService();

	private CommonRemoteService commonService = ServiceFactory.getInstance().getCommonRemoteService();

	private static byte[] lock = new byte[0];

	private static GroupProxyImpl instance = null;
	// 批准成员的最大用户ID
	private Long cleanMaxUid;

	public static GroupProxyImpl getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null)
					instance = new GroupProxyImpl();
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
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#creatGroup(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.String,
	 * byte[], java.lang.Long, java.lang.String, java.lang.Boolean,
	 * java.lang.Long)
	 */
	@Override
	public void creatOrUpdataGroup(final ProxyCallBack<List<Object>> callback, final Context context, final String name, final byte[] avatar, final Long gid, final String desc,
			final String notice, final Boolean isvalidate, final Long grid) {
		final MyAsyncTask<List<Object>> asyncTask = new MyAsyncTask<List<Object>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getUserRemoteService().creatOrUpdataGroup(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						if (result != null && result.hasExtension(Msgs.id)) {
							List<Object>list = new ArrayList<Object>();
							list.add(result.getRc());
							IdResult idResult = result.getExtension(Msgs.id);
							if(idResult != null)
								list.add(idResult.getId());
							else
								list.add(0);
							asyncTask.getProxyCallBack().onSuccess(list);
						} else {
							asyncTask.getProxyCallBack().onFailure(result.getRc(), null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, name, avatar, gid, desc, notice, isvalidate, grid);
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
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#getMyGroups(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getMyGroups(ProxyCallBack<List<GroupVo>> callback, Context context) {
		// 查询本地数据库
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		List<GroupVo> list = groupDao.findAllMyGroups();
		if(list != null){
			for (GroupVo groupVo : list) {
				GameVo gameVo = gameDao.getGameByGameId(groupVo.getGid());
				if (gameVo != null && groupVo != null) {
					groupVo.setGameIcon(gameVo.getGamelogo());
					groupVo.setGid(gameVo.getGameid());
					LogUtil.d(TAG,"select  "+groupVo.getName());
				}
			}
		}else{
			list = new ArrayList<GroupVo>();
		}
		callback.onSuccess(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#searchGroups(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.String,
	 * java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Integer)
	 */
	@Override
	public void searchGroups(final ProxyCallBack<PagerVo<GroupVo>> callback, final Context context, final String idname, final Long uid, final String gid, final int limit,
			final long offset, final Integer near, final Integer minCount, final Integer maxCount, final Boolean needValidate, final Boolean isfind, final Integer source, final String sid) {
		final MyAsyncTask<PagerVo<GroupVo>> asyncTask = new MyAsyncTask<PagerVo<GroupVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				String position = SystemContext.getInstance().getLocation();
				final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getInstance().getContext());
				ServiceFactory.getInstance().getSearchRemoteService().searchGroups(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						List<GroupVo> list = null;
						if (result != null && result.hasExtension(Msgs.groupQueryResult)) {
							GroupQueryResult gResult = result.getExtension(Msgs.groupQueryResult);
							List<GroupDetail> gdlist = gResult.getEntryList();
							if (gdlist != null) {
								PagerVo<GroupVo> pagerVo = new PagerVo<GroupVo>();
								List<GroupVo> items = new ArrayList<GroupVo>();
								for (int i = 0; i < gdlist.size(); i++) {
									GroupDetail d = gdlist.get(i);
									GroupVo gvo = new GroupVo();
									gvo.setAvatar(d.getAvatar());
									gvo.setGid(d.getGid());
									gvo.setCreatTime(d.getCreatetime());
									gvo.setGrid(d.getGrid());
									gvo.setName(d.getName());
									gvo.setNotice(d.getNotice());
									gvo.setPresidentId(d.getPresidentId());
									gvo.setSerial(d.getSerial());
									gvo.setUndesc(d.getGroupDesc());
									gvo.setUtime(d.getUtime());
									gvo.setPresidentName(d.getPresidentName());
									gvo.setTotal(d.getTotal());
									gvo.setNeedValidate(d.getNeedValidate() ? 1 : 0);
									gvo.setNearTotal(d.getNearcount());
									gvo.setGrade(d.getGrade());
									gvo.setMaxcount(d.getMaxcount());
									gvo.setRelWithGroup(d.getRel());
									GameVo gameVo = gameDao.getGameByGameId(d.getGid());
									if (gameVo != null) {
										gvo.setGameIcon(gameVo.getGamelogo());
									}
									items.add(gvo);
								}
								pagerVo.setOffset(gResult.getOffset());
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
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, idname, uid, gid, offset, limit, 0, near, minCount, maxCount, needValidate, isfind, position, source, sid);
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
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#searchGroups(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.Long)
	 */
	@Override
	public void searchGroups(final ProxyCallBack<Integer> callback, final Context context, final String gid, final long offset, final int limit, final Integer source) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSearchRemoteService().searchGroups(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.getRc() == com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE) {
							int count = 0;
							if (result != null && result.hasExtension(Msgs.groupQueryResult)) {
								count = result.getExtension(Msgs.groupQueryResult).getTotal();
							} else {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
							}
							asyncTask.getProxyCallBack().onSuccess(count);
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, null, null, null, gid, offset, limit, 1, null, null, null, null, null, null, source, null);
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
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#getApplyUsers(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getApplyUsers(final ProxyCallBack<List<UserItemObj>> callback, final Context context, final long grid) {
		final MyAsyncTask<List<UserItemObj>> asyncTask = new MyAsyncTask<List<UserItemObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				GroupVo gvo = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getInstance().getContext()).findGroupByGrid(grid);
				long maxUid = 0;
				if (gvo != null) {
					maxUid = gvo.getCleanMaxUid();
				}
				ServiceFactory.getInstance().getSearchRemoteService().searchAppGroupUsers(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						List<UserItemObj> list = new ArrayList<UserItemObj>();
						if (result.hasExtension(Msgs.groupApplyQueryResult)) {
							List<GroupApplyQueryEntry> gdlist = result.getExtension(Msgs.groupApplyQueryResult).getEntryList();
							if (gdlist != null && gdlist.size() > 0) {
								for (int i = 0; i < gdlist.size(); i++) {
									GroupApplyQueryEntry entry = gdlist.get(i);
									if (i == 0) {
										// 赋值最大申请ID
										cleanMaxUid = entry.getId();
									}
									UserItemObj uobj = new UserItemObj();
									uobj.setUid(entry.getUid());
									uobj.setGrid(entry.getGrid());// 该用户申请加入的公会ID
									uobj.setMood(entry.getContent());
									uobj.setStatus(entry.getStatus());
									list.add(uobj);
								}
							} else {
								asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
							}
						} else {
							asyncTask.getProxyCallBack().onFailure(ErrorCode.EC_RESULT_ISNULL, null);
						}
						asyncTask.getProxyCallBack().onSuccess(list);
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, maxUid, grid);
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
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#getGroupUsers(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getGroupUsers(ProxyCallBack<List<UserItemObj>> callback, Context context, long grid) {
		List<GroupUserRelVo> list = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getInstance().getContext())
				.findUsersByGrid(grid);
		List<UserItemObj> userlist = null;
		if (list != null) {
			userlist = new ArrayList<UserItemObj>();
			for (int i = 0; i < list.size(); i++) {
				GroupUserRelVo vo = list.get(i);
				UserItemObj user = new UserItemObj();
				user.setGrid(vo.getGrid());
				user.setRel(vo.getRel());
				user.setUid(vo.getUid());
				user.setPoint(vo.getCpoint());
				userlist.add(user);
			}
		}
		callback.onSuccess(userlist);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#getInviteUsers(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getInviteUsers(ProxyCallBack<List<UserItemObj>> callback, Context context, long grid) {
		UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getInstance().getContext());
		GroupUserRelDao groupUserRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
		List<UserVo> list = userDao.findInviteUsers(grid);
		List<UserItemObj> userList = null;
		if (list != null) {
			GroupUserRelVo userRelVo;
			userList = new ArrayList<UserItemObj>();
			for (int i = 0; i < list.size(); i++) {
				UserVo vo = list.get(i);
				userRelVo = groupUserRelDao.findUsers(grid, vo.getUserid());
				if(userRelVo != null) continue;
				UserItemObj obj = new UserItemObj();
				obj.setUid(vo.getUserid());
				obj.setAvatar(vo.getAvatar());
				obj.setNickname(vo.getUsername());
				obj.setSex(vo.getSex());
				obj.setAge(vo.getAge());
				obj.setMood(vo.getMood());
				obj.setGrade(vo.getGrade());
				obj.setRemark(vo.getRemarksName());
				userList.add(obj);
			}
		}
		callback.onSuccess(userList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#cleanApproveUsers(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void cleanApproveUsers(ProxyCallBack<Integer> callback, Context context, long grid) {
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getInstance().getContext());
		GroupVo vo = groupDao.findGroupByGrid(grid);
		if (vo != null && cleanMaxUid != null) {
			vo.setCleanMaxUid(cleanMaxUid);
			groupDao.update(vo);
		}
		callback.onSuccess(com.iwgame.msgs.proto.Msgs.ErrorCode.EC_OK_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#getGroupDetailInfo(com
	 * .iwgame.msgs.common.ProxyCallBack, android.content.Context, long, int)
	 */
	@Override
	public void getGroupDetailInfo(final ProxyCallBack<List<GroupVo>> callback, final Context context, final ContentDetailParams params, final int type, final Long uid) {
		final List<GroupVo>list = new ArrayList<GroupVo>();
		final GroupDao dao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getInstance().getContext());
		if(params.getParamCount() == 1){
			
			final GroupVo groupVo = dao.findGroupByGrid(params.getParam(0).getId());
			if (groupVo != null && groupVo.getName() != null && !"".equals(groupVo.getName())) {
				list.add(groupVo);
				callback.onSuccess(list);
				return;
			}
		}
		// 服务端获取数据
		final MyAsyncTask<List<GroupVo>> asyncTask = new MyAsyncTask<List<GroupVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				contentService.getDetailContent(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result.hasExtension(Msgs.contentDetailsResult)) {
							ContentDetailsResult cdresult = result.getExtension(Msgs.contentDetailsResult);
							if(cdresult == null) return;
							List<GroupDetail> groupList = cdresult.getGroupDetailList();
							if(groupList == null || groupList.size() <= 0) return;
							int size = groupList.size();
							GroupDetail d;
							for(int i = 0; i < size; i ++){
								d = groupList.get(i);
								if (d != null) {
									GroupVo vo = new GroupVo();
									vo.setAvatar(d.getAvatar());
									vo.setCreatTime(d.getCreatetime());
									vo.setGid(d.getGid());
									vo.setGrid(d.getGrid());
									vo.setUndesc(d.getGroupDesc());
									vo.setName(d.getName());
									vo.setNotice(d.getNotice());
									vo.setPresidentId(d.getPresidentId());
									vo.setPresidentName(d.getPresidentName());
									vo.setTotal(d.getTotal());
									vo.setSerial(d.getSerial());
									vo.setUtime(d.getUtime());
									vo.setNeedValidate(d.getNeedValidate() ? 1 : 0);
									vo.setGrade(d.getGrade());
									vo.setMaxcount(d.getMaxcount());
									vo.setMsgoffon(-1);
									vo.setSid(d.getSid());
									dao.updateOrInsertById(vo);
									list.add(vo);
								} 
							}
							asyncTask.getProxyCallBack().onSuccess(list);
						} else {
							asyncTask.getProxyCallBack().onSuccess(list);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, params, type, uid);
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
	 * @see com.iwgame.msgs.module.group.ui.logic.GroupProxy#getRel(long, long)
	 */
	@Override
	public GroupUserRelVo getRel(long grid, long uid) {
		return DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext()).findUsers(grid, uid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#getGroupUsersByMessage
	 * (com.iwgame.msgs.common.ProxyCallBack, android.content.Context, long)
	 */
	@Override
	public void getGroupUsersByMessage(final ProxyCallBack<List<UserItemObj>> callback, Context context, long grid) {
		// 取消息
		final List<MessageVo> list_message = ProxyFactory.getInstance().getMessageProxy()
				.getFromLastMessage(MsgsConstants.MC_MCHAT, grid, MsgsConstants.DOMAIN_GROUP, MsgsConstants.MCC_CHAT);

		List<GroupUserRelVo> gurlist = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getInstance().getContext())
				.findUsersByGrid(grid, Integer.MAX_VALUE);
		List<UserItemObj> userlist = null;
		if (gurlist != null) {
			userlist = new ArrayList<UserItemObj>();
			for (int i = 0; i < gurlist.size(); i++) {
				GroupUserRelVo vo = gurlist.get(i);
				UserItemObj user = new UserItemObj();
				user.setGrid(vo.getGrid());
				user.setRel(vo.getRel());
				user.setUid(vo.getUid());
				user.setPoint(vo.getCpoint());
				user.setTop(i+1);
				user.setAtime(vo.getAtime());
				userlist.add(user);
			}
		}
		if (userlist != null) {
			List<UserItemObj> list = new ArrayList<UserItemObj>();

			// 先根据消息加人，然后再加剩下的人
			for (int i = 0; i < list_message.size(); i++) {
				MessageVo messageVo = list_message.get(i);
				if (messageVo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
					for (int j = 0; j < userlist.size(); j++) {
						UserItemObj userItemObj = userlist.get(j);
						if (userItemObj.getUid() == messageVo.getFromId()) {
							UserItemObj obj = new UserItemObj(userItemObj);
							obj.setMessageVo(messageVo);
							list.add(obj);
							userlist.remove(j);
							break;
						}
					}
				}
			}
			for (int i = 0; i < userlist.size(); i++) {
				UserItemObj userItemObj = userlist.get(i);
				list.add(userItemObj);
			}
			callback.onSuccess(list);

		} else {
			callback.onSuccess(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.group.ui.logic.GroupProxy#searchRecommendGroups
	 * (com.iwgame.msgs.common.ProxyCallBack, android.content.Context,
	 * java.lang.String, int, int)
	 */
	@Override
	public void searchRecommendGroups(final ProxyCallBack<PagerVo<GroupVo>> callback, final Context context, final String gids, final long offset, final int limit) {
		final MyAsyncTask<PagerVo<GroupVo>> asyncTask = new MyAsyncTask<PagerVo<GroupVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getInstance().getContext());
				searchService.searchRecommendGroups(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.groupRecommendResult)) {
							GroupRecommendResult gr = result.getExtension(Msgs.groupRecommendResult);
							List<GroupRecommendEntry> entry = gr.getEntryList();
							if (entry != null && entry.size() > 0) {
								PagerVo<GroupVo> pagetVo = new PagerVo<GroupVo>();
								List<GroupVo> items = new ArrayList<GroupVo>();
								pagetVo.setItems(items);
								pagetVo.setOffset(entry.get(entry.size() - 1).getId());
								for (int i = 0; i < entry.size(); i++) {
									GroupRecommendEntry e = entry.get(i);
									GroupVo vo = new GroupVo();
									vo.setSerial(e.getSerial());
									vo.setSortId(e.getId());
									vo.setGrid(e.getGrid());
									vo.setName(e.getGroupname());
									vo.setAvatar(e.getAvatar());
									vo.setPresidentName(e.getPresident());
									vo.setTotal(e.getCount());
									vo.setUndesc(e.getDesc());
									vo.setGid(e.getGid());
									vo.setGrade(e.getGrade());
									vo.setMaxcount(e.getMaxcount());
									try {
										GameVo gvo = gameDao.getGameByGameId(e.getGid());
										vo.setGameIcon(gvo.getGamelogo());
									} catch (Exception e2) {
										// TODO: handle exception
									}
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
				}, context, gids, offset, limit);
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
	 * com.iwgame.msgs.module.group.logic.GroupProxy#getGroupUsers(com.iwgame
	 * .msgs.common.ProxyCallBack, android.content.Context, java.lang.Long,
	 * long)
	 */
	@Override
	public void getGroupUsers(final ProxyCallBack<List<UserItemObj>> callback, final Context context, final Long grid, final long utime) {
		final MyAsyncTask<List<UserItemObj>> asyncTask = new MyAsyncTask<List<UserItemObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				commonService.getSyncListData(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XAction.XActionResult result) {
						if (result != null && result.hasExtension(Msgs.relationResult)) {
							Msgs.RelationResult relationResult = result.getExtension(Msgs.relationResult);
							List<Msgs.RelationResult.RelationEntry> list = relationResult.getEntryList();
							if (list != null && list.size() > 0) {
								List<UserItemObj> ulist = new ArrayList<UserItemObj>();
								for (int i = 0; i < list.size(); i++) {
									Msgs.RelationResult.RelationEntry entry = list.get(i);
									long uid = entry.getId();
									int type = entry.getType();
									int point = entry.getPoint();
									if (type != 0) {
										UserItemObj uiobj = new UserItemObj();
										uiobj.setGrid(grid);
										uiobj.setRel(type);
										uiobj.setUid(uid);
										uiobj.setPoint(point);
										uiobj.setRemark(entry.getRemark());
										ulist.add(uiobj);
									}
								}
								asyncTask.getProxyCallBack().onSuccess(ulist);
							}else{
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
				}, context, grid, 0, SyncListService.TYPE_GROUP_USER);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.group.logic.GroupProxy#getGroupContributePointTop(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.Long, int)
	 */
	@Override
	public void getGroupContributePointTop(ProxyCallBack<List<UserItemObj>> callback, Context context,final Long grid,final int limit) {

		final MyAsyncTask<List<UserItemObj>> asyncTask = new MyAsyncTask<List<UserItemObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				List<GroupUserRelVo> list = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getInstance().getContext())
						.findUsersByGrid(grid, limit);
				List<UserItemObj> userlist = null;
				if (list != null) {
					userlist = new ArrayList<UserItemObj>();
					for (int i = 0; i < list.size(); i++) {
						GroupUserRelVo vo = list.get(i);
						UserItemObj user = new UserItemObj();
						user.setGrid(vo.getGrid());
						user.setRel(vo.getRel());
						user.setUid(vo.getUid());
						user.setPoint(vo.getCpoint());
						user.setTop(i+1);
						user.setRemark(vo.getRemark());
						userlist.add(user);
					}
				}
				asyncTask.getProxyCallBack().onSuccess(userlist);
				return null;
			}

			@Override
			public void onHandle(Void result) {
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.group.logic.GroupProxy#getGroupGradePolicy(com.iwgame.msgs.common.ProxyCallBack, android.content.Context)
	 */
	@Override
	public void getGroupGradePolicy(final ProxyCallBack<List<GroupGradeVo>> callback, final Context context) {
		final MyAsyncTask<List<GroupGradeVo>> asyncTask = new MyAsyncTask<List<GroupGradeVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				final GroupGradeDao groupGradeDao = DaoFactory.getDaoFactory().getGroupGradeDao(SystemContext.getInstance().getContext());
				long syncKey = SystemContext.getInstance().getGroupGradePolicySyncKey();
				ServiceFactory.getInstance().getSearchRemoteService().syncPointOrTaskPolicy(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if(result.hasExtension(Msgs.pointConfigDataResult)){
							PointConfigDataResult pcdResult = result.getExtension(Msgs.pointConfigDataResult);
							List<GroupGradeConfig> list = pcdResult.getGroupGradeConfigList();
							if(list != null && list.size() > 0){
								for (int j = 0; j < list.size(); j++) {
									GroupGradeVo vo = new GroupGradeVo();
									GroupGradeConfig config = list.get(j);
									vo.setGrade(config.getGrade());
									vo.setMembers(config.getMembers());
									vo.setPoint(config.getPoint());
									vo.setGrouplimit(config.getGrouplimit());
									vo.setUserlimit(config.getUserlimit());
									vo.setStatus(config.getStatus());
									groupGradeDao.insertOrUpdate(vo);
								}
								SystemContext.getInstance().setGroupGradePolicySyncKey(pcdResult.getUpdatetime());
								SystemContext.getInstance().groupGradeConfig = groupGradeDao.queryAll();
								asyncTask.getProxyCallBack().onSuccess(SystemContext.getInstance().groupGradeConfig);
							}
						}else{
							if(SystemContext.getInstance().groupGradeConfig == null){
								SystemContext.getInstance().groupGradeConfig = groupGradeDao.queryAll();
							}
							asyncTask.getProxyCallBack().onSuccess(SystemContext.getInstance().groupGradeConfig);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						if(SystemContext.getInstance().groupGradeConfig == null){
							SystemContext.getInstance().groupGradeConfig = groupGradeDao.queryAll();
						}
						asyncTask.getProxyCallBack().onSuccess(SystemContext.getInstance().groupGradeConfig);	
					}
				}, context, 3, syncKey);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.group.logic.GroupProxy#getGroupPoint(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void getGroupPoint(final ProxyCallBack<List<GroupVo>> callback, final Context context, final String grids) {
		final MyAsyncTask<List<GroupVo>> asyncTask = new MyAsyncTask<List<GroupVo>>(callback);
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
								List<GroupVo> rlist = new ArrayList<GroupVo>();
								for (int i = 0; i < list.size(); i++) {
									PointEntity entity = list.get(i);
									GroupVo vo = new GroupVo();
									vo.setGrid(entity.getTid());
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
				}, context, grids, 5);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.module.group.logic.GroupProxy#getUserContributeGroupPoint(com.iwgame.msgs.common.ProxyCallBack, android.content.Context, java.lang.String)
	 */
	@Override
	public void getUserContributeGroupPoint(final ProxyCallBack<List<UserItemObj>> callback, final Context context, final String grids) {
		final MyAsyncTask<List<UserItemObj>> asyncTask = new MyAsyncTask<List<UserItemObj>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getSearchRemoteService().searchUserContributeGroupPoint(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if (result != null && result.hasExtension(Msgs.pointEntityResult)) {
							PointEntityResult peResult = result.getExtension(Msgs.pointEntityResult);
							List<PointEntity> list = peResult.getPointEntityList();
							if (list != null && list.size() > 0) {
								List<UserItemObj> rlist = new ArrayList<UserItemObj>();
								for (int i = 0; i < list.size(); i++) {
									PointEntity entity = list.get(i);
									UserItemObj vo = new UserItemObj();
									vo.setGrid(entity.getTid());
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
				}, context, grids);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}





	/**
	 * 分页获取
	 * 公会的成员
	 */
	@Override
	public void syncGroupMemberList(final ProxyCallBack<List<GroupUserRelVo>> callback,
			final Context context, final long grids, final long offset, final int limit, final int orderType) {
		final MyAsyncTask<List<GroupUserRelVo>> asyncTask = new MyAsyncTask<List<GroupUserRelVo>>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getCommonRemoteService().syncGroupMembersList(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if(result != null && result.hasExtension(Msgs.groupMembersSyncResult)){
							GroupMembersSyncResult groupMembersSyncResult = result.getExtension(Msgs.groupMembersSyncResult);
							List<GroupMemberEntry> list = groupMembersSyncResult.getEntryList();
							List<GroupUserRelVo> groupUserRelVos = new ArrayList<GroupUserRelVo>();
							GroupUserRelDao userRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
							// 取消息
							List<MessageVo> list_message = ProxyFactory.getInstance().getMessageProxy()
									.getFromLastMessage(MsgsConstants.MC_MCHAT, grids, MsgsConstants.DOMAIN_GROUP, MsgsConstants.MCC_CHAT);
							if(list == null || list.size() <= 0){
								asyncTask.getProxyCallBack().onSuccess(groupUserRelVos);
							}else{
								int listSize = list.size();
								GroupMemberEntry entry;
								GroupUserRelVo guvo;
								MessageVo messageVo;
								for(int i = 0; i < listSize; i++){
									//如果是在这个公会里面，将数据插入到数据库中去
									entry = list.get(i);
									guvo = new GroupUserRelVo();
									guvo.setUid(entry.getUid());
									guvo.setGrid(entry.getGrid());
									guvo.setUtime(entry.getUtime());
									guvo.setStatus(entry.getStatus());
									guvo.setCpoint(entry.getPoint());
									guvo.setRemark(entry.getRemark());//接口暂时没有返回
									guvo.setRel(entry.getStatus());
									guvo.setAtime(entry.getLastlogintime());
									UserVo vo = new UserVo();
									UserInfoDetail detail = entry.getUserDetail();
									vo.setUserid(detail.getId());
									vo.setJob(detail.getJob());
									vo.setAge(detail.getAge());
									vo.setMood(detail.getMood());
									vo.setSex(detail.getSex());
									vo.setCity(detail.getCity());
									vo.setUsername(detail.getNickname());
									vo.setSerial(detail.getSerial());
									vo.setAvatar(detail.getAvatar());
									vo.setGrade(detail.getGrade());
									vo.setDescription(detail.getDesc());
									vo.setGameTime(detail.getGametime());
									vo.setUpdatetime(detail.getUpdatetime());
									vo.setMobileName(detail.getPhoneNo());
									vo.setWeibo(detail.getMicroblog());
									vo.setPoint(detail.getPoint());
									vo.setWeiboName(detail.getMicroblogname());
									int size = list_message.size();
									for (int j = 0; j < size; j++) {
										messageVo = list_message.get(j);
										if (messageVo.getFromDomain().equals(MsgsConstants.DOMAIN_USER)) {
											if (entry.getUid() == messageVo.getFromId()) {
												guvo.setMessageVo(messageVo);
												list_message.remove(messageVo);
												break;
											}
										}
									}
									guvo.setVo(vo);
									groupUserRelVos.add(guvo);
								}
								userRelDao.insertOrUpdate(groupUserRelVos);
								asyncTask.getProxyCallBack().onSuccess(groupUserRelVos);
							}
						}else{
							asyncTask.getProxyCallBack().onSuccess(null);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, grids,offset,limit,orderType);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});

	}

	/**
	 * 加入验证的接口的方法 
	 */
	@Override
	public void modifyGroupSettingInvaliDate(
			final ProxyCallBack<Integer> callback, final Context context, final String name,
			final byte[] avatar, final Long gid, final String desc, final String notice,
			final Boolean isvalidate, final Long grid) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getUserRemoteService().creatOrUpdataGroup(new ServiceCallBack<XActionResult>() {

					@Override
					public void onSuccess(final XActionResult result) {
						asyncTask.getProxyCallBack().onSuccess(result.getRc());
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, null);
					}
				}, context, name, avatar, gid, desc, notice, isvalidate, grid);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	/**
	 * 获取创建公会的最大数目 
	 */
	@Override
	public void getCreatGroupMax(final ProxyCallBack<Integer> callback,
			final Context context, final int limitType) {
		final MyAsyncTask<Integer> asyncTask = new MyAsyncTask<Integer>(callback);
		asyncTask.execute(new AsyncCallBack<Void>() {

			@Override
			public Void execute() {
				ServiceFactory.getInstance().getUserRemoteService().getCreatGroupMax(new ServiceCallBack<XAction.XActionResult>() {

					@Override
					public void onSuccess(XActionResult result) {
						if(result != null && result.hasExtension(Msgs.limitedOPCountResult)){
							LimitedOPCountResult limitedOPCountResult = result.getExtension(Msgs.limitedOPCountResult);
							if(limitedOPCountResult != null){
								LimitedOPCount count = limitedOPCountResult.getOpCount(0);
								if(count != null){
									asyncTask.getProxyCallBack().onSuccess(count.getCount());
								}else{
									asyncTask.getProxyCallBack().onSuccess(0);
								}
							}else{
								asyncTask.getProxyCallBack().onSuccess(0);
							}
						}else{
							asyncTask.getProxyCallBack().onSuccess(0);
						}
					}

					@Override
					public void onFailure(Integer result, String resultMsg) {
						asyncTask.getProxyCallBack().onFailure(result, resultMsg);
					}
				}, context, limitType);
				return null;
			}

			@Override
			public void onHandle(Void result) {
				// TODO Auto-generated method stub
			}
			
		});
	}

}
