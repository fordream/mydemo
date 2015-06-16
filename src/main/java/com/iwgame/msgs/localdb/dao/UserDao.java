package com.iwgame.msgs.localdb.dao;

import java.util.List;
import java.util.Map;

import android.database.sqlite.SQLiteDatabase;

import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.vo.local.UserVo;

public interface UserDao {

	public SQLiteDatabase getDb();
	
	/**
	 * 插入一个用户
	 * @param vo
	 * @return
	 */
	public  UserVo insert(UserVo vo);
	/**
	 * 通过id更新用户
	 * @param vo
	 * @return
	 */
	public int updateById(UserVo vo) ;
	
	/**
	 * 通过用户id 和关系，以及操作方式 来更新用户的关系
	 * @param userid
	 * @param positive
	 * @param inverse
	 * @return
	 */
	public int updateByUserRel(long userid, int positive, int inverse);
	
	/**
	 * 通过userid插入或更新用户
	 * @param vo
	 * @return
	 */
	public UserVo insertOrUpdateByUserid(UserVo vo);
	/**
	 * 批量插入用户
	 * @param items
	 * @return
	 */
	public List<UserVo> bulkInsert(List<UserVo> items);
	/**
	 * 通过userid批量插入或更新用户
	 * @param items
	 * @return
	 */
	public List<UserVo> bulkInsertOrUpdateByUserid(List<UserVo> items);
    /**
     * 通过id 删除用户
     * @param id
     * @return
     */
	public int deleteById(long id);
	/**
	 * 通过userid删除用户
	 * @param userid
	 * @return
	 */
	//public int deleteByUserid(long userid);
	
	/**
	 * 通过与当前用户的关系， 删除用户
	 * @param relPositive 该用户与当前用户的关系（正向关系）
	 * @param relInverse 当前用户与该用户的关系（反向关系）
	 * @return
	 */
	public int deleteByRel(int relPositive,int relInverse);
	
	/**
	 * 通过userid获得用户
	 * @param userid
	 * @return
	 */
	public UserVo getUserByUserId(long userid) ;
	/**
	 * 获得用户列表
	 * @param relation 关系[1关注2黑名单,101粉丝]
	 * @param keyid 
	 * @param direction 方向[1:向下，向大的数字;2:向前，向小的数字]
	 * @param size
	 * @return
	 */
	public List<UserVo> getUserListByKeyIsUseid(int relation, long keyid ,int direction,int size);
	/**
	 * 获取所有用户
	 * @param type 我关注
	 * @param relation 关系[0无关系1关注,2黑名单]
	 * @param orderType 排序类型[1字母]
	 * @return
	 */
	public List<UserVo> getUsersByRelation(int type, int relation, int orderType);
	/**
	 * 获取所有用户
	 * @param type 我关注
	 * @param relation 关系[0无关系1关注,2黑名单]
	 * @param orderType
	 * @param keyword 模糊的用户名
	 * @return
	 */
	public List<UserVo> getUsersByRelation(int type, int relation, int orderType, String keyword);

	/**
	 * 获取所有用户
	 *  在邀请成员页面，
	 *  通过关键字去查询用户的详细信息
	 * @param type 我关注
	 * @param relation 关系[0无关系1关注,2黑名单]
	 * @param orderType
	 * @param keyword 模糊的用户名
	 * @return
	 */
	public List<UserItemObj> getFollowUserByRelation(int type, int relation, int orderType, String keyword, long grid);
	
	
	/**
	 * 根据用户ID获取用户
	 * @param id
	 * @return
	 */
	public UserVo getUserById(long id) ;
	
	/**
	 * 查询可要求的用户
	 * @param grid
	 * @return
	 */
	public List<UserVo> findInviteUsers(long grid);

	/**
	 * 获取到map结构
	 * @param type
	 * @param relation
	 * @param orderType
	 * @return
	 */
	public Map<Long, UserVo> getUsersByRel(int type, int relation, int orderType);

}
