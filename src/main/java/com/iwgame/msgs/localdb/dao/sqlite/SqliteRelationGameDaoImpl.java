package com.iwgame.msgs.localdb.dao.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.RelationGameDao;
import com.iwgame.msgs.vo.local.RelationGameVo;

public class SqliteRelationGameDaoImpl implements RelationGameDao {
	private SQLiteDatabase db;
	private final String TAG_LOG = "SqliteRelationGameDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "gameid", "relation", "lastupdatetime", "isbarmanager" };
	private final String TB_NAME = "relationgame";

	public SqliteRelationGameDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	@Override
	public RelationGameVo insert(RelationGameVo vo) {
		if (db == null)
			return null;

		ContentValues contentvalues = new ContentValues();

		contentvalues.put("gameid", vo.getGameid());
		contentvalues.put("relation", vo.getRelation());
		contentvalues.put("lastupdatetime", vo.getLastupdatetime());
		contentvalues.put("isbarmanager", vo.getIsbarmanager());

		int rowid = (int) db.insert(TB_NAME, null, contentvalues);
		if (rowid < 0) {
			Log.e(TAG_LOG, "insert is error: " + vo.toString());
			vo = null;
		} else {
			vo.setId(rowid);
		}
		return vo;
	}

	@Override
	public int updateById(RelationGameVo vo) {
		if (db == null)
			return -1;

		ContentValues contentvalues = new ContentValues();

		contentvalues.put("gameid", vo.getGameid());
		contentvalues.put("relation", vo.getRelation());
		contentvalues.put("lastupdatetime", vo.getLastupdatetime());
		contentvalues.put("isbarmanager", vo.getIsbarmanager());

		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());

		return db.update(TB_NAME, contentvalues, "id =? ", arr);
	}

	@Override
	public RelationGameVo insertOrUpdateRelGame(RelationGameVo vo) {
		if (db == null)
			return null;

		RelationGameVo vo2 = getRelationGameByGameId(vo.getGameid());
		if (vo2 != null) {
			// 更新
			vo.setId(vo2.getId());
			updateById(vo);
		} else {
			// 插入
			vo = insert(vo);
		}
		return vo;
	}

	@Override
	public List<RelationGameVo> bulkInsert(List<RelationGameVo> items) {
		if (db == null)
			return null;

		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				items.set(i, insert(items.get(i)));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		// db.close();
		return items;
	}

	@Override
	public List<RelationGameVo> bulkInsertOrUpdateRelationGames(List<RelationGameVo> items) {
		if (db == null)
			return null;

		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				items.set(i, insertOrUpdateRelGame(items.get(i)));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return items;
	}

	@Override
	public int deleteById(long id) {
		if (db == null)
			return -1;

		String[] arr = new String[1];
		arr[0] = Long.toString(id);
		return db.delete(TB_NAME, "id =? ", arr);
	}

	@Override
	public int deleteAll() {
		if (db == null)
			return -1;

		return db.delete(TB_NAME, null, null);
	}

	@Override
	public int deleteByGameid(long gameid) {
		if (db == null)
			return -1;

		String[] arr = new String[1];
		arr[0] = Long.toString(gameid);
		return db.delete(TB_NAME, "gameid =? ", arr);
	}

	@Override
	public RelationGameVo getRelationGameById(long id) {
		if (db == null)
			return null;

		RelationGameVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "id=?";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(id);

		ArrayList<RelationGameVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.w(TAG_LOG, "no query id =  " + id);
		}
		return vo;
	}

	@Override
	public RelationGameVo getRelationGameByGameId(long gameid) {
		if (db == null)
			return null;

		RelationGameVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = " gameid=? ";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(gameid);

		ArrayList<RelationGameVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.w(TAG_LOG, "no query gameid =  " + gameid);
		}
		return vo;
	}

	private ArrayList<RelationGameVo> cursor2Objects(Cursor cursor) {
		ArrayList<RelationGameVo> list = new ArrayList<RelationGameVo>();
		try {
			while (cursor.moveToNext()) {
				RelationGameVo vo = new RelationGameVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int gameidIndex = cursor.getColumnIndexOrThrow("gameid");
				final int relationIndex = cursor.getColumnIndexOrThrow("relation");
				final int lastupdatetimeIndex = cursor.getColumnIndexOrThrow("lastupdatetime");
				final int isbarmanagerIndex = cursor.getColumnIndexOrThrow("isbarmanager");

				vo.setId(cursor.getLong(idIndex));
				vo.setGameid(cursor.getLong(gameidIndex));
				vo.setRelation(cursor.getInt(relationIndex));
				vo.setLastupdatetime(cursor.getLong(lastupdatetimeIndex));
				vo.setIsbarmanager(cursor.getInt(isbarmanagerIndex));
				list.add(vo);
			}
		} finally {
			if (cursor != null)
				cursor.close();
			// if(db.isOpen()) db.close();
		}

		return list;
	}
}
