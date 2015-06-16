/**      
* UserGradeDao.java Create on 2014-8-21     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.localdb.dao;

import java.util.List;

import com.iwgame.msgs.vo.local.UserGradeVo;
import com.iwgame.msgs.vo.local.UserVo;

import android.database.sqlite.SQLiteDatabase;

/** 
 * @ClassName: UserGradeDao 
 * @Description: 用户等级策略
 * @author 王卫
 * @date 2014-8-21 下午5:33:39 
 * @Version 1.0
 * 
 */
public interface UserGradeDao {
	
	public SQLiteDatabase getDb();
	
	public List<UserGradeVo> insertOrUpdateAll(List<UserGradeVo> items);
	
	public UserGradeVo insertOrUpdate(UserGradeVo item);
	
	public List<UserGradeVo> queryAll();
	
	public UserGradeVo queryByGrade(int grade);
	
	public UserGradeVo insert(UserGradeVo vo);

	public int update(UserGradeVo vo);

}
