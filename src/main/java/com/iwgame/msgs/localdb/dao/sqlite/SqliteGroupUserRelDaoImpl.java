/**      
 * SqliteGroupUserRelDaoImpl.java Create on 2013-10-24     
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

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.vo.local.GroupUserRelVo;

/**
 * @ClassName: SqliteGroupUserRelDaoImpl
 * @Description: 用户公会DAO实现类
 * @author 王卫
 * @date 2013-10-24 上午10:18:50
 * @Version 1.0
 * 
 */
public class SqliteGroupUserRelDaoImpl implements GroupUserRelDao {

	private SQLiteDatabase db;
	private final String TAG_LOG = "SqliteGroupUserRelDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "uid", "grid", "rel", "cpoint", "atime", "remark" };
	private final String TB_NAME = "groupuserrel";

	/**
	 * 
	 */
	public SqliteGroupUserRelDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupUserRelDao#insert(com.iwgame.msgs.vo
	 * .local.GroupUserRelVo)
	 */
	@Override
	public long insert(GroupUserRelVo vo) {
		if (db == null)
			return -1l;
		ContentValues values = new ContentValues();
		values.put("uid", vo.getUid());
		values.put("grid", vo.getGrid());
		values.put("rel", vo.getRel());
		values.put("cpoint", vo.getCpoint());
		values.put("atime", vo.getAtime());
		values.put("remark", vo.getRemark());
		return db.insert(TB_NAME, null, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupUserRelDao#updateById(com.iwgame.msgs
	 * .vo.local.GroupUserRelVo)
	 */
	@Override
	public int updateById(GroupUserRelVo vo) {
		if (db == null)
			return -1;
		ContentValues values = new ContentValues();
		if (vo.getGrid() > 0)
			values.put("grid", vo.getGrid());
		if (vo.getUid() > 0)
			values.put("uid", vo.getUid());
		values.put("rel", vo.getRel());
		if (vo.getCpoint() > 0)
			values.put("cpoint", vo.getCpoint());
		if (vo.getAtime() > 0)
			values.put("atime", vo.getAtime());
		values.put("remark", vo.getRemark());
		String[] arr = new String[] { Long.toString(vo.getId()) };
		return db.update(TB_NAME, values, "id =? ", arr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupUserRelDao#insertOrUpdate(com.iwgame
	 * .msgs.vo.local.GroupUserRelVo)
	 */
	@Override
	public int insertOrUpdate(GroupUserRelVo vo) {
		// TODO Auto-generated method stub
		GroupUserRelVo guvo = findUsers(vo.getGrid(), vo.getUid());
		if (guvo != null) {
			// 更新
			vo.setId(guvo.getId());
			return updateById(vo);
		} else {
			// 插入
			insert(vo);
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupUserRelDao#insertOrUpdate(java.util.
	 * List)
	 */
	@Override
	public int insertOrUpdate(List<GroupUserRelVo> items) {
		if (db == null)
			return -1;
		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				GroupUserRelVo vo = items.get(i);
				if (vo.getRel() == GroupUserRelVo.REL_OUT) {
					deleteRel(vo.getUid(), vo.getGrid());
				} else {
					insertOrUpdate(vo);
				}
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupUserRelDao#deleteByUid(long)
	 */
	@Override
	public int deleteRel(long uid, long grid) {
		if (db == null)
			return -1;
		return db.delete(TB_NAME, "uid = ? and grid = ? ", new String[] { Long.toString(uid), Long.toString(grid) });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupUserRelDao#deleteRel(long)
	 */
	@Override
	public int deleteRel(long grid) {
		if (db == null)
			return -1;
		return db.delete(TB_NAME, "grid = ? ", new String[] { Long.toString(grid) });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupUserRelDao#findUsersByGrid(long)
	 */
	@Override
	public List<GroupUserRelVo> findUsersByGrid(long grid) {
		if (db == null)
			return null;
		String selection = "grid=?";
		String[] arrwhere = new String[] { Long.toString(grid) };
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, selection, arrwhere, null, null, "rel desc"));
	}

	/**
	 * 查找公会用户 按照用户的uid来排序
	 */
	@Override
	public List<GroupUserRelVo> findUsersByGrid(long grid, String s) {
		if (db == null)
			return null;
		String selection = "grid=?";
		String[] arrwhere = new String[] { Long.toString(grid) };
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, selection, arrwhere, null, null, "cpoint desc,uid asc"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupUserRelDao#findUsersByGrid(long,
	 * int)
	 */
	@Override
	public List<GroupUserRelVo> findUsersByGrid(long grid, int limit) {
		if (db == null)
			return null;
		String selection = "grid=?";
		String[] arrwhere = new String[] { Long.toString(grid) };
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, selection, arrwhere, null, null, "cpoint desc", limit + ""));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupUserRelDao#findUsers(long, long)
	 */
	@Override
	public GroupUserRelVo findUsers(long grid, long uid) {
		if (db == null)
			return null;
		String selection = "grid=? and uid = ? ";
		String[] arrwhere = new String[] { Long.toString(grid), Long.toString(uid) };
		List<GroupUserRelVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, selection, arrwhere, null, null, null));
		if (list != null && list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param cursor
	 * @return
	 */
	private ArrayList<GroupUserRelVo> cursor2Objects(Cursor cursor) {
		if (cursor != null) {
			ArrayList<GroupUserRelVo> list = new ArrayList<GroupUserRelVo>();
			try {
				while (cursor.moveToNext()) {
					GroupUserRelVo vo = new GroupUserRelVo();
					final int idIndex = cursor.getColumnIndexOrThrow("id");
					final int gridIndex = cursor.getColumnIndexOrThrow("grid");
					final int uidIndex = cursor.getColumnIndexOrThrow("uid");
					final int relIndex = cursor.getColumnIndexOrThrow("rel");
					final int pointIndex = cursor.getColumnIndexOrThrow("cpoint");
					final int atimeIndex = cursor.getColumnIndexOrThrow("atime");
					final int remarkIndex = cursor.getColumnIndexOrThrow("remark");
					vo.setId(cursor.getLong(idIndex));
					vo.setGrid(cursor.getLong(gridIndex));
					vo.setUid(cursor.getLong(uidIndex));
					vo.setRel(cursor.getInt(relIndex));
					vo.setCpoint(cursor.getInt(pointIndex));
					vo.setAtime(cursor.getInt(atimeIndex));
					vo.setRemark(cursor.getString(remarkIndex));
					list.add(vo);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
			}
			return list;
		} else {
			return null;
		}
	}
}
