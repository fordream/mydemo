package com.iwgame.msgs.localdb.dao.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.GameDao;
import com.iwgame.msgs.vo.local.GameVo;
import com.iwgame.msgs.vo.local.ext.ExtGameVo;

public class SqliteGameDaoImpl implements GameDao {
	private SQLiteDatabase db;
	private final String TAG_LOG = "SqliteGameDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "gameid", "gamename", "gamelogo", "gamepackageid", "type", "publisher", "like",
			"dislike", "mlike", "mdislike", "status", "utime", "desc", "gputime", "gtype" };
	private final String TB_NAME = "game";

	public SqliteGameDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	@Override
	public GameVo insert(GameVo vo) {
		if (db == null)
			return null;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("gameid", vo.getGameid());
		contentvalues.put("gamename", vo.getGamename());
		contentvalues.put("gamelogo", vo.getGamelogo());
		contentvalues.put("gamepackageid", vo.getGamepackageid());
		contentvalues.put("type", vo.getType());
		contentvalues.put("publisher", vo.getPublisher());
		contentvalues.put("like", vo.getLike());
		contentvalues.put("dislike", vo.getDislike());
		contentvalues.put("mlike", vo.getMlike());
		contentvalues.put("mdislike", vo.getMdislike());
		contentvalues.put("status", vo.getStatus());
		contentvalues.put("utime", vo.getUtime());
		contentvalues.put("desc", vo.getDesc());
		contentvalues.put("gputime", vo.getGputime());
		contentvalues.put("gtype", vo.getGtype());

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
	public int updateById(GameVo vo) {
		if (db == null)
			return -1;
		ContentValues contentvalues = new ContentValues();
		if (vo.getGameid() > 0)
			contentvalues.put("gameid", vo.getGameid());
		if (vo.getGamename() != null)
			contentvalues.put("gamename", vo.getGamename());
		if (vo.getGamelogo() != null)
			contentvalues.put("gamelogo", vo.getGamelogo());
		if (vo.getGamepackageid() > 0)
			contentvalues.put("gamepackageid", vo.getGamepackageid());
		if (vo.getType() != null)
			contentvalues.put("type", vo.getType());
		if (vo.getPublisher() != null)
			contentvalues.put("publisher", vo.getPublisher());
		if (vo.getLike() > 0)
			contentvalues.put("like", vo.getLike());
		if (vo.getDislike() > 0)
			contentvalues.put("dislike", vo.getDislike());
		if (vo.getMlike() > 0)
			contentvalues.put("mlike", vo.getMlike());
		if (vo.getMdislike() > 0)
			contentvalues.put("mdislike", vo.getMdislike());
		if (vo.getStatus() > 0)
			contentvalues.put("status", vo.getStatus());
		if (vo.getStatus() > 0)
			contentvalues.put("utime", vo.getUtime());
		if (vo.getDesc() != null)
			contentvalues.put("desc", vo.getDesc());
		if (vo.getGputime() > 0)
			contentvalues.put("gputime", vo.getGputime());
		if (vo.getGtype() > 0)
			contentvalues.put("gtype", vo.getGtype());

		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());

		return db.update(TB_NAME, contentvalues, "id =? ", arr);
	}

	@Override
	public GameVo insertOrUpdateByGameid(GameVo vo) {
		// TODO Auto-generated method stub
		GameVo vo2 = getGameByGameId(vo.getGameid());
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
	public List<GameVo> bulkInsert(List<GameVo> items) {
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
	public List<GameVo> bulkInsertOrUpdateByGameid(List<GameVo> items) {
		if (db == null)
			return null;
		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				items.set(i, insertOrUpdateByGameid(items.get(i)));
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
	public GameVo getGameById(long id) {
		if (db == null)
			return null;
		GameVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "id=?";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(id);

		ArrayList<GameVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.w(TAG_LOG, "no query id =  " + id);
		}
		return vo;
	}

	@Override
	public GameVo getGameByGameId(long gameid) {
		if (db == null)
			return null;
		GameVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = " gameid=? ";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(gameid);

		ArrayList<GameVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.w(TAG_LOG, "no query gameid =  " + gameid);
		}
		return vo;
	}

	private ArrayList<GameVo> cursor2Objects(Cursor cursor) {
		ArrayList<GameVo> list = new ArrayList<GameVo>();
		try {
			while (cursor.moveToNext()) {
				GameVo vo = new GameVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int gameidIndex = cursor.getColumnIndexOrThrow("gameid");
				final int gamenameIndex = cursor.getColumnIndexOrThrow("gamename");
				final int gamelogoIndex = cursor.getColumnIndexOrThrow("gamelogo");
				final int gamepackageidIndex = cursor.getColumnIndexOrThrow("gamepackageid");
				final int typeIndex = cursor.getColumnIndexOrThrow("type");
				final int publisher = cursor.getColumnIndexOrThrow("publisher");
				final int like = cursor.getColumnIndexOrThrow("like");
				final int dislike = cursor.getColumnIndexOrThrow("dislike");
				final int mlike = cursor.getColumnIndexOrThrow("mlike");
				final int mdislike = cursor.getColumnIndexOrThrow("mdislike");
				final int status = cursor.getColumnIndexOrThrow("status");
				final int utime = cursor.getColumnIndexOrThrow("utime");
				final int desc = cursor.getColumnIndexOrThrow("desc");
				final int gputime = cursor.getColumnIndexOrThrow("gputime");
				final int gtype = cursor.getColumnIndexOrThrow("gtype");

				vo.setId(cursor.getLong(idIndex));
				vo.setGameid(cursor.getLong(gameidIndex));
				vo.setGamename(cursor.getString(gamenameIndex));
				vo.setGamelogo(cursor.getString(gamelogoIndex));
				vo.setGamepackageid(cursor.getLong(gamepackageidIndex));
				vo.setType(cursor.getString(typeIndex));
				vo.setPublisher(cursor.getString(publisher));
				vo.setLike(cursor.getInt(like));
				vo.setDislike(cursor.getInt(dislike));
				vo.setMlike(cursor.getInt(mlike));
				vo.setMdislike(cursor.getInt(mdislike));
				vo.setStatus(cursor.getInt(status));
				vo.setUtime(cursor.getInt(utime));
				vo.setDesc(cursor.getString(desc));
				vo.setGputime(cursor.getInt(gputime));
				vo.setGtype(cursor.getInt(gtype));
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

	private ArrayList<GameVo> cursor2Objects2(Cursor cursor) {
		ArrayList<GameVo> list = new ArrayList<GameVo>();
		try {
			while (cursor.moveToNext()) {
				GameVo vo = new GameVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int gameidIndex = cursor.getColumnIndexOrThrow("gameid");
				final int gamenameIndex = cursor.getColumnIndexOrThrow("gamename");
				final int gamelogoIndex = cursor.getColumnIndexOrThrow("gamelogo");
				final int gamepackageidIndex = cursor.getColumnIndexOrThrow("gamepackageid");
				final int typeIndex = cursor.getColumnIndexOrThrow("type");
				final int publisher = cursor.getColumnIndexOrThrow("publisher");
				final int like = cursor.getColumnIndexOrThrow("like");
				final int dislike = cursor.getColumnIndexOrThrow("dislike");
				final int mlike = cursor.getColumnIndexOrThrow("mlike");
				final int mdislike = cursor.getColumnIndexOrThrow("mdislike");
				final int status = cursor.getColumnIndexOrThrow("status");
				final int utime = cursor.getColumnIndexOrThrow("utime");
				final int desc = cursor.getColumnIndexOrThrow("desc");
				final int gputime = cursor.getColumnIndexOrThrow("gputime");
				final int gtype = cursor.getColumnIndexOrThrow("gtype");
				final int lastupdatetime = cursor.getColumnIndexOrThrow("lastupdatetime");

				vo.setId(cursor.getLong(idIndex));
				vo.setGameid(cursor.getLong(gameidIndex));
				vo.setGamename(cursor.getString(gamenameIndex));
				vo.setGamelogo(cursor.getString(gamelogoIndex));
				vo.setGamepackageid(cursor.getLong(gamepackageidIndex));
				vo.setType(cursor.getString(typeIndex));
				vo.setPublisher(cursor.getString(publisher));
				vo.setLike(cursor.getInt(like));
				vo.setDislike(cursor.getInt(dislike));
				vo.setMlike(cursor.getInt(mlike));
				vo.setMdislike(cursor.getInt(mdislike));
				vo.setStatus(cursor.getInt(status));
				vo.setUtime(cursor.getLong(utime));
				vo.setDesc(cursor.getString(desc));
				vo.setGputime(cursor.getLong(gputime));
				vo.setGtype(cursor.getInt(gtype));
				vo.setFollowtime(cursor.getLong(lastupdatetime));
				list.add(vo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			// if(db.isOpen()) db.close();
		}

		return list;
	}

	/**
	 * 处理贴吧包和用户关系贴吧数据
	 * 
	 * @param cursor
	 * @return
	 */
	private ArrayList<ExtGameVo> searchResultCursor2Objects(Cursor cursor) {
		ArrayList<ExtGameVo> list = new ArrayList<ExtGameVo>();
		try {
			while (cursor.moveToNext()) {
				ExtGameVo vo = new ExtGameVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int gameidIndex = cursor.getColumnIndexOrThrow("gameid");
				final int gamenameIndex = cursor.getColumnIndexOrThrow("gamename");
				final int gamelogoIndex = cursor.getColumnIndexOrThrow("gamelogo");
				final int gamepackageidIndex = cursor.getColumnIndexOrThrow("gamepackageid");
				final int typeIndex = cursor.getColumnIndexOrThrow("type");
				final int publisher = cursor.getColumnIndexOrThrow("publisher");
				final int like = cursor.getColumnIndexOrThrow("like");
				final int dislike = cursor.getColumnIndexOrThrow("dislike");
				final int mlike = cursor.getColumnIndexOrThrow("mlike");
				final int mdislike = cursor.getColumnIndexOrThrow("mdislike");
				final int status = cursor.getColumnIndexOrThrow("status");
				final int relation = cursor.getColumnIndexOrThrow("relation");
				final int utime = cursor.getColumnIndexOrThrow("utime");
				final int desc = cursor.getColumnIndexOrThrow("desc");
				final int gputime = cursor.getColumnIndexOrThrow("gputime");
				final int gtype = cursor.getColumnIndexOrThrow("gtype");

				vo.setId(cursor.getLong(idIndex));
				vo.setGameid(cursor.getLong(gameidIndex));
				vo.setGamename(cursor.getString(gamenameIndex));
				vo.setGamelogo(cursor.getString(gamelogoIndex));
				vo.setGamepackageid(cursor.getLong(gamepackageidIndex));
				vo.setType(cursor.getString(typeIndex));
				vo.setPublisher(cursor.getString(publisher));
				vo.setLike(cursor.getInt(like));
				vo.setDislike(cursor.getInt(dislike));
				vo.setMlike(cursor.getInt(mlike));
				vo.setMdislike(cursor.getInt(mdislike));
				vo.setStatus(cursor.getInt(status));
				vo.setFollow(cursor.getInt(relation) == 0 ? false : true);
				vo.setUtime(cursor.getInt(utime));
				vo.setDesc(cursor.getString(desc));
				vo.setGputime(cursor.getInt(gputime));
				vo.setGtype(cursor.getInt(gtype));
				list.add(vo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
			// if(db.isOpen()) db.close();
		}

		return list;
	}

	@Override
	public ArrayList<GameVo> getGameListByAll(long keyid, int direction, int size) {
		if (db == null)
			return null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "";
		if (direction == 1)// 向下，向大的数字
		{
			strwhere += "  id>? ";
		} else if (direction == 2)// 向前，向小的数字
		{
			strwhere += "  id<? ";
		}
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(keyid);

		String strlimit = Long.toString(size);
		ArrayList<GameVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder, strlimit));
		return list;
	}

	@Override
	public ArrayList<GameVo> getGameListByRelation(int relation, long keyid, int direction, int size) {
		if (db == null)
			return null;
		GameVo vo = null;
		String strwhere = null;

		strwhere = "t2.relation=? ";

		String sql = "select t2.gameid, t1.id, t1.gamename, t1.gamelogo, t1.gamepackageid, t1.type, t1.publisher, t1.like, t1.dislike, t1.mlike, t1.mdislike, t1.status, t1.utime, t1.desc, t1.gputime, t1.gtype, t2.lastupdatetime from relationgame t2 left join game t1  on t1.gameid = t2.gameid where "
				+ strwhere + " order by t2.lastupdatetime desc";
		String[] selectionArgs = new String[1];

		selectionArgs[0] = relation + "";
		Cursor cursor = db.rawQuery(sql, selectionArgs);

		ArrayList<GameVo> list = cursor2Objects2(cursor);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GameDao#searchGameByKeyword(java.lang.String)
	 */
	@Override
	public List<ExtGameVo> searchGameByKeyword(String keyword) {
		if (db == null)
			return null;
		String strwhere = " t1.gamename like ?";
		String[] selectionArgs = { "%" + keyword + "%" };
		String sql = "select t1.*,t2.relation from game t1 left join relationgame t2 on t1.gameid=t2.gameid where " + strwhere;
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		ArrayList<ExtGameVo> list = searchResultCursor2Objects(cursor);
		return list;

		// String strwhere;
		// strwhere = " t1.gamename like ?";
		// String[] arrwhere = { "%" + keyword + "%" };
		// ArrayList<GameVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS,
		// strwhere, arrwhere, null, null, null, null));
		// return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GameDao#searchAllGames()
	 */
	@Override
	public List<GameVo> searchAllGames() {
		if (db == null)
			return null;
		String sql = "select * from game";
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<GameVo> list = cursor2Objects(cursor);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GameDao#getGameCount()
	 */
	@Override
	public int getGameCount() {
		if (db == null)
			return -1;
		String sql = "select * from game";
		Cursor cursor = db.rawQuery(sql, null);
		int count = 0;
		if (cursor != null) {
			count = cursor.getCount();
			cursor.close();
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GameDao#getGameMaxTime()
	 */
	@Override
	public long getGameMaxTime() {
		if (db == null)
			return -1l;
		String sql = "select max(utime) from game";
		SQLiteStatement stmt = db.compileStatement(sql);
		return stmt.simpleQueryForLong();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GameDao#searchGamesByIds(java.lang.String)
	 */
	@Override
	public List<GameVo> searchGamesByIds(String ids) {
		if (db == null)
			return null;
		String sql = "select * from game where gameid in (" + ids + ")";
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<GameVo> list = cursor2Objects(cursor);
		return list;
	}

}
