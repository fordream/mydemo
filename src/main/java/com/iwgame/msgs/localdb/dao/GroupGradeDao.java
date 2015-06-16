/**      
* UserGradeDao.java Create on 2014-8-21     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.localdb.dao;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.UserGradeVo;

/** 
 * @ClassName: UserGradeDao 
 * @Description: 公会等级策略
 * @author 王卫
 * @date 2014-8-21 下午5:33:39 
 * @Version 1.0
 * 
 */
public interface GroupGradeDao {
	
	public SQLiteDatabase getDb();
	
	public List<GroupGradeVo> insertOrUpdateAll(List<GroupGradeVo> items);
	
	public GroupGradeVo insertOrUpdate(GroupGradeVo item);
	
	public List<GroupGradeVo> queryAll();
	
	public GroupGradeVo queryByGrade(int grade);
	
	public GroupGradeVo insert(GroupGradeVo vo);

	public int update(GroupGradeVo vo);

}
