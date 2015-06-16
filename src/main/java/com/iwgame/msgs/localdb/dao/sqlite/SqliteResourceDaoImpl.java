/**      
* SqliteResourceDao.java Create on 2013-9-30     
*      
* Copyright (c) 2013 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.localdb.dao.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.ResourceDao;
import com.iwgame.msgs.vo.local.ResourceVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.LogUtil;

/** 
 * @ClassName: SqliteResourceDao 
 * @Description: 数据操作类
 * @author 王卫
 * @date 2013-9-30 下午12:01:52 
 * @Version 1.0
 * 
 */
public class SqliteResourceDaoImpl implements ResourceDao {

	private static final String TAG = "SqliteResourceDaoImpl";
	private SQLiteDatabase db;
	private final String[] TB_COLUMNS = new String[] { "id", "userid", "resourceId", "createtime", "type"};
	private final String TB_NAME = "resource";

	public SqliteResourceDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if(helper != null)
			db = helper.getWritableDatabase();
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.ResourceDao#insert(com.iwgame.msgs.vo.local.ResourceVo)
	 */
	@Override
	public long insert(ResourceVo vo) {
		if (db == null)
			return -1l;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("userid", vo.getUserid());
		contentvalues.put("resourceId", vo.getResourceId());
		contentvalues.put("createtime", vo.getCreattime());
		contentvalues.put("type", vo.getType());
		return db.insert(TB_NAME, null, contentvalues);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.ResourceDao#update(com.iwgame.msgs.vo.local.ResourceVo)
	 */
	@Override
	public long update(ResourceVo vo) {
		if (db == null)
			return -1l;
		ContentValues contentvalues = new ContentValues();
		if(vo.getUserid() > 0)
			contentvalues.put("userid", vo.getUserid());
		if(vo.getResourceId() != null)
			contentvalues.put("resourceId", vo.getResourceId());
		if(vo.getCreattime() > 0)
			contentvalues.put("createtime", vo.getCreattime());
		if(vo.getType() > -1)
			contentvalues.put("type", vo.getType());

		String[] arr = new String[1];
		arr[0] = vo.getResourceId();

		return db.update(TB_NAME, contentvalues, "resourceId = ? ", arr);
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.ResourceDao#insertOrUpdate(com.iwgame.msgs.vo.local.ResourceVo)
	 */
	@Override
	public long insertOrUpdate(ResourceVo vo) {
		ResourceVo vo2 = findResourceByResid(vo.getResourceId());
		if (vo2 != null) {
			// 更新
			vo.setId(vo2.getId());
			return update(vo);
		} else {
			// 插入
			return insert(vo);
		}
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.ResourceDao#deleteByResId()
	 */
	@Override
	public long deleteByResId(String resid) {
		if (db == null)
			return -1l;
		String[] arr = new String[1];
		arr[0] = resid;
		return db.delete(TB_NAME, "resourceId =? ", arr);
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.ResourceDao#findResourceByUserid(long)
	 */
	@Override
	public List<ResourceVo> findResourceByUserid(long uid) {
		if (db == null)
			return null;
		String strwhere = "userid=?";
		String[] arrwhere = { String.valueOf(uid) };
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null));
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.ResourceDao#findResourceByResid(java.lang.String)
	 */
	@Override
	public ResourceVo findResourceByResid(String resid) {
		if (db == null)
			return null;
		String strwhere = "resourceId=?";
		String[] arrwhere = { String.valueOf(resid) };
		ArrayList<ResourceVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			LogUtil.i(TAG, "no query resid =  " + resid);
			return null;
		}
	}
	
	private ArrayList<ResourceVo> cursor2Objects(Cursor cursor) {
		ArrayList<ResourceVo> list = new ArrayList<ResourceVo>();
		try {
			while (cursor.moveToNext()) {
				ResourceVo vo = new ResourceVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int useridIndex = cursor.getColumnIndexOrThrow("userid");
				final int resourceId = cursor.getColumnIndexOrThrow("resourceId");
				final int createtime = cursor.getColumnIndexOrThrow("createtime");
				final int type = cursor.getColumnIndexOrThrow("type");
				vo.setId(cursor.getInt(idIndex));
				vo.setUserid(cursor.getLong(useridIndex));
				vo.setResourceId(cursor.getString(resourceId));
				vo.setCreattime(cursor.getLong(createtime));
				vo.setType(cursor.getInt(type));
				list.add(vo);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return list;
	}

}
