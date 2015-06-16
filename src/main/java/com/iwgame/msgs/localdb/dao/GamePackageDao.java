package com.iwgame.msgs.localdb.dao;


import java.util.List;

import com.iwgame.msgs.vo.local.GamePackageVo;

public interface GamePackageDao {

	public  GamePackageVo insert(GamePackageVo vo);
	public int updateById(GamePackageVo vo) ;
	public GamePackageVo insertOrUpdateByPackageid(GamePackageVo vo);
	public List<GamePackageVo> bulkInsert(List<GamePackageVo> items);
	public List<GamePackageVo> bulkInsertOrUpdateByPackageid(List<GamePackageVo> items);
	public int deleteById(long id);
	public int deleteAll();
	public int deleteByPackageid(long packageid);
	public GamePackageVo getGamePackageById(long id) ;
	public GamePackageVo getGamePackageByPackageId(long packageid) ;
	public List<GamePackageVo> getGamePackageListByGameId(long gameid);
	
	/**
	 * 
	 * @param packageName
	 * @return
	 */
	public GamePackageVo getGamePackageByPackageName(String packageName);

}
