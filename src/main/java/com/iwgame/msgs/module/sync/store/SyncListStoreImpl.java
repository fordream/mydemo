/**      
 * SyncStoreImpl.java Create on 2013-12-25     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.module.sync.store;

import java.util.ArrayList;
import java.util.List;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.localdb.dao.RelationGameDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.module.ServiceFactory;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.module.sync.SyncListService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.RelationResult;
import com.iwgame.msgs.utils.MsgsConstants;
import com.iwgame.msgs.vo.local.GameRoleServiceVo;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.RelationGameVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: SyncStoreImpl
 * @Description: 同步列表存储实现
 * @author 王卫
 * @date 2013-12-25 下午5:28:40
 * @Version 1.0
 * 
 */
public class SyncListStoreImpl implements SyncListStore {

	private static final String TAG = "SyncListStoreImpl";
	private boolean syncGamePackageSuccess = true;
	private boolean syncMyGroupSuccess = true;

	/**
	 * 
	 */
	public SyncListStoreImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.sync.store.SyncStore#process(com.iwgame.xaction
	 * .proto.XAction.XActionResult, com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void process(Long id, int syncType, XActionResult result, SyncCallBack callBack) {
		if (result != null) {
			if (result.hasExtension(Msgs.relationResult)) {
				Msgs.RelationResult relationResult = result.getExtension(Msgs.relationResult);
				switch (syncType) {
				case SyncListService.TYPE_GAME:
					saveGameList(relationResult, syncType, callBack);
					break;
				case SyncListService.TYPE_GAME_PACKAGE:
					syncGamePackageSuccess = true;
					saveGamePackageList(id, relationResult, callBack);
					break;
				case SyncListService.TYPE_GROUP_USER:
					saveGroupUser(id, relationResult, callBack);
					break;
				case SyncListService.TYPE_MY_GAME:
					saveMyGame(relationResult, callBack);
					break;
				case SyncListService.TYPE_MY_GROUP:
					syncMyGroupSuccess = true;
					saveMyGroup(relationResult, SyncListService.TYPE_MY_GROUP, callBack);
					break;
				case SyncListService.TYPE_MY_REL_USER:
					saveMyRelUser(relationResult, callBack);
					break;
				case SyncListService.TYPE_GAME_SERVICE:
					saveMyRoleService(relationResult,callBack);
					break;
				default:
					callBack.onSuccess(syncType);
					break;
				}
			} else {
				callBack.onSuccess(syncType);
			}
		} else {
			callBack.onFailure(syncType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.sync.store.SyncStore#getSyncKey(int, long)
	 */
	@Override
	public long getSyncKey(int syncType, Long id) {
		switch (syncType) {
		case SyncListService.TYPE_GAME:
			return SystemContext.getInstance().getGameContentSyncKey();
		case SyncListService.TYPE_GAME_PACKAGE:
			if (id != null) {
				GameVo gvo = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext()).getGameByGameId(id);
				if (gvo != null)
					return gvo.getGputime();
				else
					return 0;
			}
			return 0;
		case SyncListService.TYPE_GROUP_USER:
			if (id != null) {
				GroupVo grvo = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext()).findGroupByGrid(id);
				if (grvo != null)
					return grvo.getUreltime();
				else
					return 0;
			}
			return 0;
		case SyncListService.TYPE_MY_GAME:
			return SystemContext.getInstance().getGameRelSyncKey();
		case SyncListService.TYPE_MY_GROUP:
			return SystemContext.getInstance().getGroupSyncKey();
		case SyncListService.TYPE_MY_REL_USER:
			return SystemContext.getInstance().getUserRelSyncKey();
		default:
			return 0;
		}
	}

	/**
	 * 保存贴吧列表数据
	 * 
	 * @param result
	 * @param callBack
	 */
	private void saveGameList(final Msgs.RelationResult result, final int syncType, final SyncCallBack callBack) {
		if (result != null) {
			final List<Msgs.RelationResult.RelationEntry> list = result.getEntryList();
			if (list != null && list.size() > 0) {
				GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
				List<GameVo> games = new ArrayList<GameVo>();
				for (int i = 0; i < list.size(); i++) {
					long gid = list.get(i).getId();
					// // 存储贴吧
					GameVo vo = new GameVo();
					vo.setGameid(gid);
					gameDao.insertOrUpdateByGameid(vo);
					games.add(vo);
				}
				gameDao.bulkInsertOrUpdateByGameid(games);
				// 设置贴吧同步KEY
				SystemContext.getInstance().setGameContentSyncKey(result.getUpdatedtime());
			} else {
				callBack.onSuccess(syncType);
			}
		} else {
			callBack.onSuccess(syncType);
		}
	}
	private List<GameRoleServiceVo> saveMyRoleService(RelationResult result,
			SyncCallBack callBack) {
		if(result!=null){
			final List<Msgs.RelationResult.RelationEntry> list = result.getEntryList();
			if(list!=null&&list.size()>0){
				List<GameRoleServiceVo> gameSs = new ArrayList<GameRoleServiceVo>();
				for(int i =0;i<list.size();i++){
					Msgs.RelationResult.RelationEntry entity = list.get(i);
					GameRoleServiceVo vo = new GameRoleServiceVo();
					vo.setId(entity.getId());
					vo.setType(entity.getType());
					vo.setName(entity.getName());
					vo.setGameId(entity.getGameid());
					gameSs.add(vo);
				}
				return gameSs;
			}else{
				callBack.onSuccess(null);
				return null;
			}
		}else{
			callBack.onSuccess(null);
			return null;
		}
	}

	/**
	 * 保存贴吧列表数据
	 * 
	 * @param result
	 * @param callBack
	 */
	private void saveGamePackageList(final long id, final Msgs.RelationResult result, final SyncCallBack callBack) {
		if (result != null) {
			final List<Msgs.RelationResult.RelationEntry> list = result.getEntryList();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					final int count = i;
					long gpid = list.get(i).getId();
					ServiceFactory.getInstance().getSyncEntityService().syncEntity(gpid, SyncEntityService.TYPE_GAME_PACKAGE, new SyncCallBack() {

						@Override
						public void onSuccess(Object res) {
							if (count == list.size() - 1) {
								if (syncGamePackageSuccess) {
									// 设置贴吧包同步KEY
									GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
									GameVo gameVo = gameDao.getGameByGameId(id);
									gameVo.setGputime(result.getUpdatedtime());
									gameDao.insertOrUpdateByGameid(gameVo);
								}
								callBack.onSuccess(id);
							}
						}

						@Override
						public void onFailure(Integer res) {
							syncGamePackageSuccess = false;
							if (count == list.size() - 1) {
								callBack.onSuccess(id);
							}
						}
					});
				}
			} else {
				callBack.onSuccess(id);
			}
		} else {
			callBack.onSuccess(id);
		}
	}

	/**
	 * 保存公会用户列表数据
	 * 
	 * @param result
	 * @param callBack
	 */
	private void saveGroupUser(long id, Msgs.RelationResult result, final SyncCallBack callBack) {
		if (result != null) {
			List<Msgs.RelationResult.RelationEntry> list = result.getEntryList();
			GroupUserRelDao groupUserRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
			if (list != null && list.size() > 0) {
				List<GroupUserRelVo> gusers = new ArrayList<GroupUserRelVo>();
				for (int i = 0; i < list.size(); i++) {
					Msgs.RelationResult.RelationEntry entry = list.get(i);
					GroupUserRelVo guvo = new GroupUserRelVo();
					guvo.setGrid(id);
					guvo.setRel(entry.getType());
					guvo.setUid(entry.getId());
					guvo.setCpoint(entry.getPoint());
					guvo.setRemark(entry.getRemark());
					guvo.setAtime(entry.getLastlogintime());
					gusers.add(guvo);
				}
				groupUserRelDao.insertOrUpdate(gusers);
				// 设置公会成员同步KEY
				GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
				GroupVo gvo = groupDao.findGroupByGrid(id);
				if (gvo != null) {
					gvo.setUreltime(result.getUpdatedtime());
					groupDao.updateOrInsertById(gvo);
				}
			}
		}
		callBack.onSuccess(null);
	}

	/**
	 * 保存我的贴吧列表数据
	 * 
	 * @param result
	 * @param callBack
	 */
	private void saveMyGame(Msgs.RelationResult result, final SyncCallBack callBack) {
		if (result != null) {
			List<Msgs.RelationResult.RelationEntry> list = result.getEntryList();
			if (list != null && list.size() > 0) {
				RelationGameDao relGameDao = DaoFactory.getDaoFactory().getRelationGameDao(SystemContext.getInstance().getContext());
				List<RelationGameVo> relGames = new ArrayList<RelationGameVo>();
				for (int i = 0; i < list.size(); i++) {
					Msgs.RelationResult.RelationEntry entry = list.get(i);
					RelationGameVo vo = new RelationGameVo();
					vo.setGameid(entry.getId());
					vo.setRelation(entry.getType());
					vo.setIsbarmanager(entry.getBacktype());
					vo.setLastupdatetime(entry.getUpdatetime());
					relGames.add(vo);
				}
				relGameDao.bulkInsertOrUpdateRelationGames(relGames);
				// 设置我的贴吧同步KEY
				SystemContext.getInstance().setGameRelSyncKey(result.getUpdatedtime());
			}
		}
		callBack.onSuccess(null);
	}

	/**
	 * 保存我的公会列表数据
	 * 
	 * @param result
	 * @param callBack
	 */
	private void saveMyGroup(final Msgs.RelationResult result, final int syncType, final SyncCallBack callBack) {
		if (result != null) {
			final List<Msgs.RelationResult.RelationEntry> list = result.getEntryList();
			if (list != null && list.size() > 0) {
				GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
				GroupUserRelDao groupUserRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
				List<GroupVo>gvoList = new ArrayList<GroupVo>();
				List<Long> ids = new ArrayList<Long>();
				for (int i = 0; i < list.size(); i++) {
					// 计数器
					final int count = i;
					long grid = list.get(i).getId();
					int type = list.get(i).getType();
					long sid = list.get(i).getSid();
					if (type == 0) {
						groupDao.deleteByGrid(grid);
						groupUserRelDao.deleteRel(grid);
						// 删除消息
						ProxyFactory.getInstance().getMessageProxy()
						.delMessage(MsgsConstants.MC_MCHAT, grid, MsgsConstants.DOMAIN_GROUP, MsgsConstants.MCC_CHAT);
						if (count == list.size() - 1) {
							if (syncMyGroupSuccess) {
								// 设置我的公会同步KEY
								SystemContext.getInstance().setGroupSyncKey(result.getUpdatedtime());
							}
							callBack.onSuccess(syncType);
						}
					} else {
						GroupVo vo = new GroupVo();
						vo.setGrid(grid);
						vo.setRelWithGroup(type);
						vo.setMsgoffon(-1);
						vo.setSid(sid);
						gvoList.add(vo);
						ids.add(grid);
					}
				}
				groupDao.insertOrUpdate(gvoList);
				gvoList.clear();
				callBack.onSuccess(syncType);
			} else {
				callBack.onSuccess(syncType);
			}
		} else {
			callBack.onSuccess(syncType);
		}
	}

	/**
	 * 保存关系用户列表数据
	 * 
	 * @param result
	 * @param callBack
	 */
	private void saveMyRelUser(Msgs.RelationResult result, final SyncCallBack callBack) {
		if (result != null) {
			List<Msgs.RelationResult.RelationEntry> list = result.getEntryList();
			if (list != null && list.size() > 0) {
				UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
				List<UserVo> users = new ArrayList<UserVo>();
				List<Long> ids  = new ArrayList<Long>();
				for (int i = 0; i < list.size(); i++) {
					Msgs.RelationResult.RelationEntry entry = list.get(i);
					if (entry != null) {
						UserVo vo = new UserVo();
						vo.setUserid(entry.getId());
						vo.setRelPositive(entry.getType());
						vo.setRelInverse(entry.getBacktype());
						vo.setRemarksName(entry.getName());
						users.add(vo);
						ids.add(entry.getId());
					}
				}
				userDao.bulkInsertOrUpdateByUserid(users);
				ServiceFactory.getInstance().getSyncEntityService().syncEntity(ids, SyncEntityService.TYPE_USER, null,new SyncCallBack() {

					@Override
					public void onSuccess(Object result) {
						callBack.onSuccess(null);

					}

					@Override
					public void onFailure(Integer result) {
						callBack.onSuccess(null);

					}
				});
				// 设置我的关系用户同步KEY
				SystemContext.getInstance().setUserRelSyncKey(result.getUpdatedtime());
			} else {
				callBack.onSuccess(null);
			}
		} else {
			callBack.onSuccess(null);
		}
	}

}
