/**      
 * UserActionDao.java Create on 2014-1-28     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.localdb.dao;

import com.iwgame.msgs.vo.local.UserActionVo;

/**
 * @ClassName: UserActionDao
 * @Description: 用户行为数据操作接口
 * @author 王卫
 * @date 2014-1-28 上午11:06:25
 * @Version 1.0
 * 
 */
public interface UserActionDao {
	// 公会实体类型
	public static final int ENTITY_TYPE_GROUP = 1;
	// 贴吧实体类型
	public static final int ENTITY_TYPE_POSTBAR = 2;
	// 公会申请操作类型
	public static final int OP_TYPE_GROUP_APPLEY = 1;
	// 贴吧申请操作类型
	public static final int OP_TYPE_POSTBAR_MNAGER_APPLEY = 2;

	/**
	 * 插入行为日志
	 * 
	 * @param vo
	 * @return
	 */
	public long insert(UserActionVo vo);

	/**
	 * 更新行为日志
	 * 
	 * @param vo
	 * @return
	 */
	public int updata(UserActionVo vo);

	/**
	 * 插入某个公会今日申请次数行为日志
	 * 
	 * @param grid
	 * @return
	 */
	public void insertUserAction(long grid, int entityType, int opType);

	/**
	 * 查找某个公会今日申请次数行为日志
	 * 
	 * @param grid
	 * @return
	 */
	public UserActionVo findUserAction(long grid, int entityType, int opType);

}
