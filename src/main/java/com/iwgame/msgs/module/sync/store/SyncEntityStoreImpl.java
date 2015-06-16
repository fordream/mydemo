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
import com.iwgame.msgs.localdb.dao.GamePackageDao;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.sync.SyncCallBack;
import com.iwgame.msgs.module.sync.SyncEntityService;
import com.iwgame.msgs.proto.Msgs;
import com.iwgame.msgs.proto.Msgs.ContentDetailsResult;
import com.iwgame.msgs.proto.Msgs.ErrorCode;
import com.iwgame.msgs.proto.Msgs.GameInfoDetail;
import com.iwgame.msgs.proto.Msgs.GamePackageInfoDetail;
import com.iwgame.msgs.proto.Msgs.GroupDetail;
import com.iwgame.msgs.proto.Msgs.UserInfoDetail;
import com.iwgame.msgs.utils.BuildVoUtil;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.msgs.vo.local.ext.ExtUserVo;
import com.iwgame.utils.LogUtil;
import com.iwgame.xaction.proto.XAction.XActionResult;

/**
 * @ClassName: SyncStoreImpl
 * @Description: 同步实体存储实现
 * @author 王卫
 * @date 2013-12-25 下午5:28:40
 * @Version 1.0
 * 
 */
public class SyncEntityStoreImpl implements SyncStore {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.module.sync.store.SyncStore#process(com.iwgame.xaction
	 * .proto.XAction.XActionResult, com.iwgame.msgs.module.sync.SyncCallBack)
	 */
	@Override
	public void process(List<Long> ids, int syncType, XActionResult result, SyncCallBack callBack) {
		if (result != null) {
			if (result.hasExtension(Msgs.contentDetailsResult)) {
				Msgs.ContentDetailsResult contentResult = result.getExtension(Msgs.contentDetailsResult);
				switch (syncType) {
				case SyncEntityService.TYPE_GAME:
					saveGame(ids, contentResult, callBack);
					break;
				case SyncEntityService.TYPE_GAME_PACKAGE:
					saveGamePackage(ids, contentResult, callBack);
					break;
				case SyncEntityService.TYPE_GROUP:
					saveGroup(ids, contentResult, callBack);
					break;
				case SyncEntityService.TYPE_USER:
					saveUser(ids, contentResult, callBack);
					break;
				default:
					break;
				}
			} else {
				if (callBack != null) {
					callBack.onSuccess(null);
				}
			}
		} else {
			if (callBack != null) {
				callBack.onSuccess(null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.module.sync.store.SyncStore#getSyncKey(int, long)
	 */
	@Override
	public long getSyncKey(int syncType, Long id) {
		if(SystemContext.getInstance().getExtUserVo() == null)
			return 0;
		if (id != null) {
			switch (syncType) {
			case SyncEntityService.TYPE_GAME:
				try {
					GameVo gvo = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext()).getGameByGameId(id);
					if (gvo != null) {
						return gvo.getUtime();
					} else {
						return 0;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			case SyncEntityService.TYPE_GAME_PACKAGE:
				GamePackageVo gpvo = DaoFactory.getDaoFactory().getGamePackageDao(SystemContext.getInstance().getContext())
						.getGamePackageByPackageId(id);
				if (gpvo != null) {
					return gpvo.getUtime();
				} else {
					return 0;
				}
			case SyncEntityService.TYPE_GROUP:
				GroupVo grvo = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext()).findGroupByGrid(id);
				if (grvo != null) {
					return grvo.getUtime();
				} else {
					return 0;
				}
			case SyncEntityService.TYPE_USER:
				UserVo uvo = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext()).getUserByUserId(id);
				if (uvo != null) {
					return uvo.getUpdatetime();
				} else {
					return 0;
				}
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}

	/**
	 * @param result
	 * @param callBack
	 */
	private void saveGamePackage(List<Long> ids, ContentDetailsResult result, SyncCallBack callBack) {
		if (SystemContext.getInstance().getExtUserVo() == null)
			return;
		
		GamePackageDao gpackageDao = DaoFactory.getDaoFactory().getGamePackageDao(SystemContext.getInstance().getContext());
		if (result != null) {
			List<GamePackageInfoDetail> details = result.getGamePackageDetailList();
			if(details != null){
				List<GamePackageVo> items = new ArrayList<GamePackageVo>();
				for (int i = 0; i < details.size(); i++) {
					GamePackageInfoDetail detail = details.get(i);
					GamePackageVo vo = new GamePackageVo();
					vo.setPackageid(detail.getGpid());
					vo.setGamename(detail.getGameName());
					vo.setGameicon(detail.getGameIcon());
					vo.setPublisher(detail.getPublisher());
					vo.setFilesize(detail.getFileSize());
					vo.setVersion(detail.getGameVersion());
					vo.setScreenshot(detail.getGamePic());
					vo.setPlatform(detail.getPlatform());
					vo.setDownloadurl(detail.getDownloadUrl());
					vo.setPackagename(detail.getGamePackage());
					vo.setGameid(detail.getGid());
					vo.setDesc(detail.getDesc());
					vo.setType(detail.getCategory());
					vo.setStatus(detail.getStatus());
					vo.setUtime(detail.getUtime());
					items.add(vo);
				}
				gpackageDao.bulkInsertOrUpdateByPackageid(items);
			}
		}
		if (callBack != null) {
			callBack.onSuccess(ErrorCode.EC_OK_VALUE);
		}
	}

	/**
	 * @param result
	 * @param callBack
	 */
	private void saveGame(List<Long> ids, ContentDetailsResult result, SyncCallBack callBack) {
		if (SystemContext.getInstance().getExtUserVo() == null)
			return;
		
		GameDao gameDao = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext());
		if (result != null) {
			List<GameInfoDetail> details = result.getGameDetailList();
			if(details != null){
				List<GameVo> items = new ArrayList<GameVo>();
				for (int i = 0; i < details.size(); i++) {
					GameInfoDetail detail = details.get(i);
					GameVo vo = new GameVo();
					vo.setGameid(detail.getGid());
					vo.setGamename(detail.getGameName());
					vo.setType(detail.getCategory());
					vo.setGamelogo(detail.getLogo());
					vo.setGamepackageid(detail.getDefaultgp());
					vo.setStatus(detail.getStatus());
					vo.setUtime(detail.getUtime());
					vo.setDesc(detail.getDesc());
					vo.setGtype(detail.getGtype());
					vo.setPublisher(detail.getPublisher());
					items.add(vo);
				}
				gameDao.bulkInsertOrUpdateByGameid(items);
			}
		}
		if (callBack != null) {
			callBack.onSuccess(ErrorCode.EC_OK_VALUE);
		}
	}

	/**
	 * @param result
	 * @param callBack
	 */
	private void saveUser(List<Long> ids, ContentDetailsResult result, SyncCallBack callBack) {
		if (SystemContext.getInstance().getExtUserVo() == null)
			return;
		
		UserDao userDao = DaoFactory.getDaoFactory().getUserDao(SystemContext.getInstance().getContext());
		if (result != null) {
			List<UserInfoDetail> details = result.getUserInfoDetailList();
			if(details != null){
				List<UserVo> items = new ArrayList<UserVo>();
				for (int i = 0; i < details.size(); i++) {
					UserInfoDetail detail = details.get(i);
					ExtUserVo vo = BuildVoUtil.buildExtUserVo(detail);
					items.add(vo);
				}
				userDao.bulkInsertOrUpdateByUserid(items);
			}
		}
		if (callBack != null) {
			callBack.onSuccess(ErrorCode.EC_OK_VALUE);
		}
	}

	/**
	 * @param result
	 * @param callBack
	 */
	private void saveGroup(List<Long> ids, ContentDetailsResult result, SyncCallBack callBack) {
		if (SystemContext.getInstance().getExtUserVo() == null)
			return;
		
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getContext());
		if (result != null) {
			List<GroupDetail> details = result.getGroupDetailList();
			if(details != null){
				List<GroupVo> items = new ArrayList<GroupVo>();
				for (int i = 0; i < details.size(); i++) {
					GroupDetail detail = details.get(i);
					GroupVo vo = new GroupVo();
					vo.setAvatar(detail.getAvatar());
					vo.setCreatTime(detail.getCreatetime());
					vo.setGid(detail.getGid());
					vo.setGrid(detail.getGrid());
					vo.setName(detail.getName());
					vo.setNotice(detail.getNotice());
					vo.setPresidentId(detail.getPresidentId());
					vo.setSerial(detail.getSerial());
					vo.setUndesc(detail.getGroupDesc());
					vo.setUtime(detail.getUtime());
					vo.setPresidentName(detail.getPresidentName());
					vo.setTotal(detail.getTotal());
					vo.setNeedValidate(detail.getNeedValidate() ? 1 : 0);
					vo.setGrade(detail.getGrade());
					vo.setMaxcount(detail.getMaxcount());
					vo.setMsgoffon(-1);
					vo.setSid(detail.getSid());
					LogUtil.d("synentity", detail.getName()+detail.getPresidentName());
					items.add(vo);
				}
				groupDao.insertOrUpdate(items);
			}
		}
		if (callBack != null) {
			callBack.onSuccess(ErrorCode.EC_OK_VALUE);
		}
	}
}
