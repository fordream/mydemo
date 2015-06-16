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
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.msgs.vo.local.UserGradeVo;

/**
 * @ClassName: UserGradeDao
 * @Description: 公会等级策略
 * @author 王卫
 * @date 2014-8-21 下午5:33:39
 * @Version 1.0
 * 
 */
public interface PointTaskDao {

	public SQLiteDatabase getDb();

	public List<PointTaskVo> insertOrUpdateAll(List<PointTaskVo> items);

	public PointTaskVo insertOrUpdate(PointTaskVo item);

	public List<PointTaskVo> queryAll();

	public PointTaskVo queryByTaskId(int tid);

	public PointTaskVo insert(PointTaskVo vo);

	public int update(PointTaskVo vo);

	void insertOrUpdatePointTask(List<PointTaskVo> items);

}
