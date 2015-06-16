package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.vo.local.RelationGameVo;

public interface RelationGameDao {
	public  RelationGameVo insert(RelationGameVo vo);
	public int updateById(RelationGameVo vo) ;
	public RelationGameVo insertOrUpdateRelGame(RelationGameVo vo);
	public List<RelationGameVo> bulkInsert(List<RelationGameVo> items);
	public List<RelationGameVo> bulkInsertOrUpdateRelationGames(List<RelationGameVo> items);
	public int deleteById(long id);
	public int deleteAll();
	public int deleteByGameid(long gameid);
	public RelationGameVo getRelationGameById(long id) ;
	public RelationGameVo getRelationGameByGameId(long gameid) ;
	

}
