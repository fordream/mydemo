/**      
 * GroupGradeDaoImpl.java Create on 2014-8-21     
 *      
 * Copyright (c) 2014 by GreenShore Network
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
import com.iwgame.msgs.localdb.dao.GroupGradeDao;
import com.iwgame.msgs.vo.local.GroupGradeVo;

/**
 * @ClassName: GroupGradeDaoImpl
 * @Description: TODO(...)
 * @author 王卫
 * @date 2014-8-21 下午5:52:43
 * @Version 1.0
 * 
 */
public class SqliteGroupGradeDaoImpl implements GroupGradeDao {

	private static final String TAG = "GroupGradeDaoImpl";
	private SQLiteDatabase db;
	private final String[] TB_COLUMNS = new String[] { "id", "grade", "point", "members", "userlimit", "grouplimit", "status" };
	private final String TB_NAME = "groupgrade";

	public SqliteGroupGradeDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupGradeDao#getDb()
	 */
	@Override
	public SQLiteDatabase getDb() {
		return db;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupGradeDao#insertOrUpdate(com.iwgame.msgs
	 * .vo.local.GroupGradeVo)
	 */
	@Override
	public GroupGradeVo insertOrUpdate(GroupGradeVo item) {
		GroupGradeVo vo = queryByGrade(item.getGrade());
		if (vo != null) {
			// 更新
			item.setId(vo.getId());
			update(item);
		} else {
			// 插入
			item = insert(item);
		}
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupGradeDao#insertOrUpdateAll(java.util
	 * .List)
	 */
	@Override
	public List<GroupGradeVo> insertOrUpdateAll(List<GroupGradeVo> items) {
		if (db == null)
			return null;
		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				items.set(i, insertOrUpdate(items.get(i)));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupGradeDao#queryAll()
	 */
	@Override
	public List<GroupGradeVo> queryAll() {
		if (db == null)
			return null;
		String strwhere = " status <> 1 ";
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, null, null, null, null, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupGradeDao#queryByGrade(int)
	 */
	@Override
	public GroupGradeVo queryByGrade(int grade) {
		if (db == null)
			return null;
		GroupGradeVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "grade=?";
		arrwhere = new String[1];
		arrwhere[0] = Integer.toString(grade);

		ArrayList<GroupGradeVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.w(TAG, "no query id =  " + grade);
		}
		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupGradeDao#insert(com.iwgame.msgs.vo.local
	 * .GroupGradeVo)
	 */
	@Override
	public GroupGradeVo insert(GroupGradeVo vo) {
		if (db == null)
			return null;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("grade", vo.getGrade());
		contentvalues.put("point", vo.getPoint());
		contentvalues.put("members", vo.getMembers());
		contentvalues.put("userlimit", vo.getUserlimit());
		contentvalues.put("grouplimit", vo.getGrouplimit());
		contentvalues.put("status", vo.getStatus());

		int rowid = (int) db.insert(TB_NAME, null, contentvalues);
		if (rowid < 0) {
			Log.e(TAG, "insert is error: " + vo.toString());
			vo = null;
		} else {
			vo.setId(rowid);
		}
		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupGradeDao#update(com.iwgame.msgs.vo.local
	 * .GroupGradeVo)
	 */
	@Override
	public int update(GroupGradeVo vo) {
		if (db == null)
			return -1;
		ContentValues contentvalues = new ContentValues();
		if(vo.getGrade() > 0)
			contentvalues.put("grade", vo.getGrade());
		if(vo.getPoint() > 0)
			contentvalues.put("point", vo.getPoint());
		if(vo.getMembers() > 0)
			contentvalues.put("members", vo.getMembers());
		if(vo.getUserlimit() > 0)
			contentvalues.put("userlimit", vo.getUserlimit());
		if(vo.getGrouplimit() > 0)
			contentvalues.put("grouplimit", vo.getGrouplimit());
		if(vo.getStatus() != -1)
			contentvalues.put("status", vo.getStatus());

		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());

		return db.update(TB_NAME, contentvalues, "id =? ", arr);
	}
	
	private ArrayList<GroupGradeVo> cursor2Objects(Cursor cursor) {
		ArrayList<GroupGradeVo> list = new ArrayList<GroupGradeVo>();
		try {
			while (cursor.moveToNext()) {
				GroupGradeVo vo = new GroupGradeVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int gradeIndex = cursor.getColumnIndexOrThrow("grade");
				final int pointIndex = cursor.getColumnIndexOrThrow("point");
				final int membersIndex = cursor.getColumnIndexOrThrow("members");
				final int userlimitIndex = cursor.getColumnIndexOrThrow("userlimit");
				final int grouplimitIndex = cursor.getColumnIndexOrThrow("grouplimit");
				final int statusIndex = cursor.getColumnIndexOrThrow("status");

				vo.setId(cursor.getLong(idIndex));
				vo.setGrade(cursor.getInt(gradeIndex));
				vo.setPoint(cursor.getInt(pointIndex));
				vo.setMembers(cursor.getInt(membersIndex));
				vo.setUserlimit(cursor.getInt(userlimitIndex));
				vo.setGrouplimit(cursor.getInt(grouplimitIndex));
				vo.setStatus(cursor.getInt(statusIndex));
				list.add(vo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return list;
	}
}
