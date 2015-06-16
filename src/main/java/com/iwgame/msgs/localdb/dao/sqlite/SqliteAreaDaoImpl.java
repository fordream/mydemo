/**      
 * SqliteAreaDao.java Create on 2013-9-16     
 *      
 * Copyright (c) 2013 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.localdb.dao.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.AreaDao;
import com.iwgame.msgs.vo.local.AreaVo;

/**
 * @ClassName: SqliteAreaDao
 * @Description: TODO(...)
 * @author 王卫
 * @date 2013-9-16 下午07:57:48
 * @Version 1.0
 * 
 */
public class SqliteAreaDaoImpl implements AreaDao {

	private SQLiteDatabase db;
	private final String TAG_LOG = "SqliteAreaDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "areaname", "parentid", "type" };
	private final String TB_NAME = "area";

	public SqliteAreaDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.AreaDao#findProvince(long)
	 */
	@Override
	public List<AreaVo> findAreaByType(String[] types) {
		if (db == null)
			return null;
		if (types != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < types.length; i++) {
				if (i != types.length - 1)
					sb.append(" type=? or");
				else
					sb.append(" type=?");
			}
			String strwhere = sb.toString();
			String[] arrwhere = types;
			return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null, null));
		} else {
			return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, "parentid = id", null, null, null, null, null));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.AreaDao#findCity(long)
	 */
	@Override
	public List<AreaVo> findAreaByParentid(int pid) {
		if (db == null)
			return null;
		String strwhere = "parentid = ? and id != ?";
		String[] arrwhere = { String.valueOf(pid), String.valueOf(pid) };
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null, null));
	}

	private List<AreaVo> cursor2Objects(Cursor cursor) {
		List<AreaVo> list = new ArrayList<AreaVo>();
		try {
			while (cursor.moveToNext()) {
				AreaVo vo = new AreaVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int areaname = cursor.getColumnIndexOrThrow("areaname");
				final int parentid = cursor.getColumnIndexOrThrow("parentid");
				final int type = cursor.getColumnIndexOrThrow("type");
				vo.setId(cursor.getInt(idIndex));
				vo.setAreaname(cursor.getString(areaname));
				vo.setParentid(cursor.getInt(parentid));
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
