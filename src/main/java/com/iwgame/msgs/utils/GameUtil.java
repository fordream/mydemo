/**      
 * GameUtil.java Create on 2013-9-27     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.util.List;

import com.iwgame.msgs.common.ProxyCallBack;
import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.localdb.dao.GamePackageDao;
import com.iwgame.msgs.localdb.dao.RelationGameDao;
import com.iwgame.msgs.module.ProxyFactory;
import com.iwgame.msgs.vo.local.GamePackageVo;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: GameUtil
 * @Description: 贴吧或贴吧包及用户行为操作
 * @author 王卫
 * @date 2013-9-27 上午10:58:56
 * @Version 1.0
 * 
 */
public class GameUtil {

	private static final String TAG = "GameUtil";

	/**
	 * 检查包是否为贴吧包
	 * 
	 * @param pname
	 * @return
	 */
	public static GamePackageVo getGamePackage(String pname) {
		try {
			if (SystemContext.getInstance().getExtUserVo() != null) {
				GamePackageDao gamePackageDao = DaoFactory.getDaoFactory().getGamePackageDao(SystemContext.getInstance().getContext());
				return gamePackageDao.getGamePackageByPackageName(pname);
			} else {
				return null;
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "获取贴吧包数据库错误");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 采集上传用户操作（贴吧）行为
	 * 
	 * @param tid
	 * @param ttype
	 * @param op
	 */
	public static void userAction(long tid, int ttype, int op) {
		ProxyFactory.getInstance().getUserProxy().userAction(new ProxyCallBack<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				// TODO
			}

			@Override
			public void onFailure(Integer result,String resultMsg) {
				// TODO
			}
		}, null, tid, ttype, op, null, null,null);
	}
	
	/**
	 * 矫正游戏关系
	 */
	public static void redressGameRel(int mfcount){
		try {
			if (SystemContext.getInstance().getExtUserVo() != null) {
				final List<GameVo> gamelist = DaoFactory.getDaoFactory().getGameDao(SystemContext.getInstance().getContext()).getGameListByRelation(1, 0, 1, Integer.MAX_VALUE);
				if(gamelist != null && gamelist.size() < mfcount){
					//同步关注游戏关系
					ProxyFactory.getInstance().getGameProxy().getFollowGamesByUtime(new ProxyCallBack<List<GameVo>>() {
						
						@Override
						public void onSuccess(List<GameVo> result) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onFailure(Integer result, String resultMsg) {
							// TODO Auto-generated method stub
							
						}
					}, SystemContext.getInstance().getContext(), true, 0L);
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "获取贴吧包数据库错误");
			e.printStackTrace();
		}
	}

}
