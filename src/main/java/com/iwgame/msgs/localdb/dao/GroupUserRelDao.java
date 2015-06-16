/**      
* UserGroupRelDao.java Create on 2013-10-24     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.vo.local.GroupUserRelVo;

/** 
 * @ClassName: UserGroupRelDao 
 * @Description: 用户公会关系
 * @author 王卫
 * @date 2013-10-24 上午10:06:46 
 * @Version 1.0
 * 
 */
public interface GroupUserRelDao {

	/**
	 * 插入
	 * @param vo
	 * @return
	 */
	public long insert(GroupUserRelVo vo);
	/**
	 * 更新
	 * @param vo
	 * @return
	 */
	public int updateById(GroupUserRelVo vo);
	/**
	 * 更新或插入
	 * @param vo
	 * @return
	 */
	public int insertOrUpdate(GroupUserRelVo vo);
	/**
	 * 更新或插入多条数据
	 * @param items
	 * @return
	 */
	public int insertOrUpdate(List<GroupUserRelVo> items);
	/**
	 * 删除公会成员
	 * @param uid
	 * @param grid
	 * @return
	 */
	public int deleteRel(long uid, long grid);
	/**
	 * 删除该公会的所有关系记录
	 * @param grid
	 * @return
	 */
	public int deleteRel(long grid);
	/**
	 * 查找公会用户
	 * @param grid
	 * @return
	 */
	public List<GroupUserRelVo> findUsersByGrid(long grid);
	/**
	 * 查找公会用户
	 * @param grid
	 * @return
	 */
	public List<GroupUserRelVo> findUsersByGrid(long grid,String s);
	/**
	 * 查找公会用户
	 * @param grid
	 * @param limit
	 * @return
	 */
	public List<GroupUserRelVo> findUsersByGrid(long grid, int limit);
	/**
	 * 查找某个公会的某个用户
	 * @param grid
	 * @param uid
	 * @return
	 */
	public GroupUserRelVo findUsers(long grid, long uid);
	
}
