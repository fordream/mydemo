/**      
* GroupDao.java Create on 2013-10-24     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.vo.local.GroupVo;

/** 
 * @ClassName: GroupDao 
 * @Description: 公会DAO 
 * @author 王卫
 * @date 2013-10-24 上午09:53:20 
 * @Version 1.0
 * 
 */
public interface GroupDao {
	/**
	 * 插入
	 * @param vo
	 * @return
	 */
	public long insert(GroupVo vo);
	/**
	 * 更新
	 * @param vo
	 * @return
	 */
	public int update(GroupVo vo);
	/**
	 * 更新或删除
	 * @param vo
	 * @return
	 */
	public int updateOrInsertById(GroupVo vo);
	/**
	 * 查找公会
	 * @param grid
	 * @return
	 */
	public GroupVo findGroupByGrid(long grid);
	/**
	 * 查找所有公会
	 * @return
	 */
	public List<GroupVo> findAllGroups();
	/**
	 * 删除公会
	 * @param grid
	 * @return
	 */
	public int deleteByGrid(long grid);
	
	/**
	 * 获取到我的所有公会
	 * @return
	 */
	public List<GroupVo> findAllMyGroups();
	
	/**
	 * 用事务的方式插入数据到数据库
	 * @param items
	 * @return
	 */
	public void insertOrUpdate(List<GroupVo>items);
	 
}
