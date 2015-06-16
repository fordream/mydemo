/**      
* UserGradeDaoImpl.java Create on 2014-8-21     
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
import com.iwgame.msgs.localdb.dao.UserGradeDao;
import com.iwgame.msgs.vo.local.UserGradeVo;

/** 
 * @ClassName: UserGradeDaoImpl 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-8-21 下午5:48:53 
 * @Version 1.0
 * 
 */
public class SqliteUserGradeDaoImpl implements UserGradeDao {

	private static final String TAG = "UserGradeDaoImpl";
	private SQLiteDatabase db;
	private final String[] TB_COLUMNS = new String[] { "id", "grade", "point", "joingroup", "creategroup", "followgame", "sendpost", "datelimit", "status" ,"options","multiple"};
	private final String TB_NAME = "usergrade";

	public SqliteUserGradeDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.UserGradeDao#getDb()
	 */
	@Override
	public SQLiteDatabase getDb() {
		return db;
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.UserGradeDao#insertOrUpdateAll(java.util.List)
	 */
	@Override
	public List<UserGradeVo> insertOrUpdateAll(List<UserGradeVo> items) {
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

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.UserGradeDao#insertOrUpdate(com.iwgame.msgs.vo.local.UserGradeVo)
	 */
	@Override
	public UserGradeVo insertOrUpdate(UserGradeVo item) {
		UserGradeVo vo = queryByGrade(item.getGrade());
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

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.UserGradeDao#queryAll()
	 */
	@Override
	public List<UserGradeVo> queryAll() {
		if (db == null)
			return null;
		String strwhere = " status <> 1 ";
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, null, null, null, null, null));
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.UserGradeDao#queryByGrade(int)
	 */
	@Override
	public UserGradeVo queryByGrade(int grade) {
		if (db == null)
			return null;
		UserGradeVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "grade=?";
		arrwhere = new String[1];
		arrwhere[0] = Integer.toString(grade);

		ArrayList<UserGradeVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.w(TAG, "no query id =  " + grade);
		}
		return vo;
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.UserGradeDao#insert(com.iwgame.msgs.vo.local.UserGradeVo)
	 */
	@Override
	public UserGradeVo insert(UserGradeVo vo) {
		if (db == null)
			return null;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("grade", vo.getGrade());
		contentvalues.put("point", vo.getPoint());
		contentvalues.put("joingroup", vo.getJoingroup());
		contentvalues.put("creategroup", vo.getCreategroup());
		contentvalues.put("followgame", vo.getFollowgame());
		contentvalues.put("sendpost", vo.getSendpost());
		contentvalues.put("datelimit", vo.getDatelimit());
		contentvalues.put("status", vo.getStatus());
        contentvalues.put("options", vo.getOptions());
        contentvalues.put("multiple", vo.getMultiple());
		int rowid = (int) db.insert(TB_NAME, null, contentvalues);
		if (rowid < 0) {
			Log.e(TAG, "insert is error: " + vo.toString());
			vo = null;
		} else {
			vo.setId(rowid);
		}
		return vo;
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.UserGradeDao#update(com.iwgame.msgs.vo.local.UserGradeVo)
	 */
	@Override
	public int update(UserGradeVo vo) {
		if (db == null)
			return -1;
		ContentValues contentvalues = new ContentValues();
		if(vo.getGrade() > 0)
			contentvalues.put("grade", vo.getGrade());
		if(vo.getPoint() > 0)
			contentvalues.put("point", vo.getPoint());
		if(vo.getJoingroup() > 0)
			contentvalues.put("joingroup", vo.getJoingroup());
		if(vo.getCreategroup() > 0)
			contentvalues.put("creategroup", vo.getCreategroup());
		if(vo.getFollowgame() > 0)
			contentvalues.put("followgame", vo.getFollowgame());
		if(vo.getSendpost() > 0)
			contentvalues.put("sendpost", vo.getSendpost());
		if(vo.getDatelimit() > 0)
			contentvalues.put("datelimit", vo.getDatelimit());
		if(vo.getStatus() != -1)
			contentvalues.put("status", vo.getStatus());
        contentvalues.put("options", vo.getOptions());
        contentvalues.put("multiple", vo.getMultiple());
		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());

		return db.update(TB_NAME, contentvalues, "id =? ", arr);
	}
	
	private ArrayList<UserGradeVo> cursor2Objects(Cursor cursor) {
		ArrayList<UserGradeVo> list = new ArrayList<UserGradeVo>();
		try {
			while (cursor.moveToNext()) {
				UserGradeVo vo = new UserGradeVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int gradeIndex = cursor.getColumnIndexOrThrow("grade");
				final int pointIndex = cursor.getColumnIndexOrThrow("point");
				final int joingroupIndex = cursor.getColumnIndexOrThrow("joingroup");
				final int creategroupIndex = cursor.getColumnIndexOrThrow("creategroup");
				final int followgameIndex = cursor.getColumnIndexOrThrow("followgame");
				final int sendpostIndex = cursor.getColumnIndexOrThrow("sendpost");
				final int datelimitIndex = cursor.getColumnIndexOrThrow("datelimit");
				final int statusIndex = cursor.getColumnIndexOrThrow("status");
                final int optionsIndex = cursor.getColumnIndex("options");
                final int multipleIndex = cursor.getColumnIndex("multiple");
				vo.setId(cursor.getLong(idIndex));
				vo.setGrade(cursor.getInt(gradeIndex));
				vo.setPoint(cursor.getInt(pointIndex));
				vo.setJoingroup(cursor.getInt(joingroupIndex));
				vo.setCreategroup(cursor.getInt(creategroupIndex));
				vo.setFollowgame(cursor.getInt(followgameIndex));
				vo.setSendpost(cursor.getInt(sendpostIndex));
				vo.setDatelimit(cursor.getInt(datelimitIndex));
				vo.setStatus(cursor.getInt(statusIndex));
				vo.setOptions(cursor.getString(optionsIndex));
				vo.setMultiple(cursor.getString(multipleIndex));
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
