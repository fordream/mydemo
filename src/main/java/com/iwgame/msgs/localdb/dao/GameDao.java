package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;

public interface GameDao {

	public GameVo insert(GameVo vo);

	public int updateById(GameVo vo);

	public GameVo insertOrUpdateByGameid(GameVo vo);

	public List<GameVo> bulkInsert(List<GameVo> items);

	public List<GameVo> bulkInsertOrUpdateByGameid(List<GameVo> items);

	public int deleteAll();

	public int deleteById(long id);

	public int deleteByGameid(long gameid);

	public GameVo getGameById(long id);

	public GameVo getGameByGameId(long gameid);

	public List<GameVo> getGameListByAll(long keyid, int direction, int size);

	public List<GameVo> getGameListByRelation(int relation, long keyid, int direction, int size);

	/**
	 * 根据关键字搜索贴吧
	 * 
	 * @param keyword
	 * @return
	 */
	public List<ExtGameVo> searchGameByKeyword(String keyword);

	/**
	 * 查找所有贴吧
	 * 
	 * @return
	 */
	public List<GameVo> searchAllGames();

	/**
	 * 获取贴吧个数
	 * 
	 * @return
	 */
	public int getGameCount();

	/**
	 * 获取贴吧最后更新时间
	 * 
	 * @return
	 */
	public long getGameMaxTime();
	
	/**
	 * 按游戏id查询多个游戏
	 * @param ids
	 * @return
	 */
	public List<GameVo> searchGamesByIds(String ids);
}
