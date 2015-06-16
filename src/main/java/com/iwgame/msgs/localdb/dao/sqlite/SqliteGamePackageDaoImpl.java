package com.iwgame.msgs.localdb.dao.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.GamePackageDao;
import com.iwgame.msgs.vo.local.GamePackageVo;

public class SqliteGamePackageDaoImpl implements GamePackageDao {
	private SQLiteDatabase db;
	private final String TAG_LOG = "SqliteGamePackageDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "packageid", "gameid", "packagename", "downloadurl", "type", "dev", "version",
			"filesize", "desc", "screenshot", "gamename", "status", "utime", "gameicon", "publisher", "platform", "createtime" };
	private final String TB_NAME = "gamepackage";

	public SqliteGamePackageDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	@Override
	public GamePackageVo insert(GamePackageVo vo) {
		if (db == null)
			return null;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("packageid", vo.getPackageid());
		contentvalues.put("gameid", vo.getGameid());
		contentvalues.put("packagename", vo.getPackagename());
		contentvalues.put("downloadurl", vo.getDownloadurl());
		contentvalues.put("type", vo.getType());
		contentvalues.put("dev", vo.getDev());
		contentvalues.put("version", vo.getVersion());
		contentvalues.put("filesize", vo.getFilesize());
		contentvalues.put("desc", vo.getDesc());
		contentvalues.put("screenshot", vo.getScreenshot());
		contentvalues.put("gamename", vo.getGamename());
		contentvalues.put("status", vo.getStatus());
		contentvalues.put("utime", vo.getUtime());
		contentvalues.put("gameicon", vo.getGameicon());
		contentvalues.put("publisher", vo.getPublisher());
		contentvalues.put("platform", vo.getPlatform());
		contentvalues.put("createtime", vo.getCreatetime());

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
	public int updateById(GamePackageVo vo) {
		if (db == null)
			return -1;
		ContentValues contentvalues = new ContentValues();
		if (vo.getPackageid() > 0)
			contentvalues.put("packageid", vo.getPackageid());
		if (vo.getGameid() > 0)
			contentvalues.put("gameid", vo.getGameid());
		if (vo.getPackagename() != null)
			contentvalues.put("packagename", vo.getPackagename());
		if (vo.getDownloadurl() != null)
			contentvalues.put("downloadurl", vo.getDownloadurl());
		if (vo.getType() != null)
			contentvalues.put("type", vo.getType());
		if (vo.getDev() != null)
			contentvalues.put("dev", vo.getDev());
		if (vo.getVersion() != null)
			contentvalues.put("version", vo.getVersion());
		if (vo.getFilesize() > 0)
			contentvalues.put("filesize", vo.getFilesize());
		if (vo.getDesc() != null)
			contentvalues.put("desc", vo.getDesc());
		if (vo.getScreenshot() != null)
			contentvalues.put("screenshot", vo.getScreenshot());
		if (vo.getGamename() != null)
			contentvalues.put("gamename", vo.getGamename());
		if (vo.getStatus() > 0)
			contentvalues.put("status", vo.getStatus());
		if (vo.getUtime() > 0)
			contentvalues.put("utime", vo.getUtime());
		if (vo.getGameicon() != null)
			contentvalues.put("gameicon", vo.getGameicon());
		if (vo.getPublisher() != null)
			contentvalues.put("publisher", vo.getPublisher());
		if (vo.getPlatform() > 0)
			contentvalues.put("platform", vo.getPlatform());
		if (vo.getCreatetime() > 0)
			contentvalues.put("createtime", vo.getCreatetime());

		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());

		return db.update(TB_NAME, contentvalues, "id =? ", arr);

	}

	@Override
	public GamePackageVo insertOrUpdateByPackageid(GamePackageVo vo) {
		// TODO Auto-generated method stub
		GamePackageVo vo2 = getGamePackageByPackageId(vo.getPackageid());
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
	public List<GamePackageVo> bulkInsert(List<GamePackageVo> items) {
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
	public List<GamePackageVo> bulkInsertOrUpdateByPackageid(List<GamePackageVo> items) {
		if (db == null)
			return null;
		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				items.set(i, insertOrUpdateByPackageid(items.get(i)));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return items;
	}

	@Override
	public int deleteById(long id) {
		// TODO Auto-generated method stub
		String[] arr = new String[1];
		arr[0] = Long.toString(id);
		return db.delete(TB_NAME, "id =? ", arr);
	}

	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub

		return db.delete(TB_NAME, null, null);
	}

	@Override
	public int deleteByPackageid(long packageid) {
		// TODO Auto-generated method stub
		String[] arr = new String[1];
		arr[0] = Long.toString(packageid);
		return db.delete(TB_NAME, "packageid =? ", arr);
	}

	@Override
	public GamePackageVo getGamePackageById(long id) {
		if (db == null)
			return null;
		GamePackageVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "id=?";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(id);

		ArrayList<GamePackageVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			// Log.w(TAG_LOG, "no query id =  " + id);
		}
		return vo;
	}

	@Override
	public GamePackageVo getGamePackageByPackageId(long packageid) {
		if (db == null)
			return null;
		GamePackageVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = " packageid = ? ";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(packageid);

		ArrayList<GamePackageVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.w(TAG_LOG, "no query packageid =  " + packageid);
		}
		return vo;
	}

	@Override
	public ArrayList<GamePackageVo> getGamePackageListByGameId(long gameid) {
		if (db == null)
			return null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = " gameid=? ";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(gameid);

		ArrayList<GamePackageVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));

		return list;
	}

	private ArrayList<GamePackageVo> cursor2Objects(Cursor cursor) {
		ArrayList<GamePackageVo> list = new ArrayList<GamePackageVo>();
		try {
			while (cursor.moveToNext()) {
				GamePackageVo vo = new GamePackageVo();

				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int packageidIndex = cursor.getColumnIndexOrThrow("packageid");
				final int gameidIndex = cursor.getColumnIndexOrThrow("gameid");
				final int packagenameIndex = cursor.getColumnIndexOrThrow("packagename");
				final int downloadurlIndex = cursor.getColumnIndexOrThrow("downloadurl");
				final int typeIndex = cursor.getColumnIndexOrThrow("type");
				final int devIndex = cursor.getColumnIndexOrThrow("dev");
				final int versionIndex = cursor.getColumnIndexOrThrow("version");
				final int filesizeIndex = cursor.getColumnIndexOrThrow("filesize");
				final int descIndex = cursor.getColumnIndexOrThrow("desc");
				final int screenshotIndex = cursor.getColumnIndexOrThrow("screenshot");
				final int gamename = cursor.getColumnIndexOrThrow("gamename");
				final int status = cursor.getColumnIndexOrThrow("status");
				final int utime = cursor.getColumnIndexOrThrow("utime");
				final int gameicon = cursor.getColumnIndexOrThrow("gameicon");
				final int publisher = cursor.getColumnIndexOrThrow("publisher");
				final int platform = cursor.getColumnIndexOrThrow("platform");
				final int createtime = cursor.getColumnIndexOrThrow("createtime");

				vo.setId(cursor.getLong(idIndex));
				vo.setPackageid(cursor.getLong(packageidIndex));
				vo.setGameid(cursor.getLong(gameidIndex));
				vo.setPackagename(cursor.getString(packagenameIndex));
				vo.setDownloadurl(cursor.getString(downloadurlIndex));
				vo.setType(cursor.getString(typeIndex));
				vo.setDev(cursor.getString(devIndex));
				vo.setVersion(cursor.getString(versionIndex));
				vo.setFilesize(cursor.getLong(filesizeIndex));
				vo.setDesc(cursor.getString(descIndex));
				vo.setScreenshot(cursor.getString(screenshotIndex));
				vo.setGamename(cursor.getString(gamename));
				vo.setStatus(cursor.getInt(status));
				vo.setUtime(cursor.getLong(status));
				vo.setGameicon(cursor.getString(gameicon));
				vo.setPublisher(cursor.getString(publisher));
				vo.setPlatform(cursor.getInt(platform));
				vo.setCreatetime(cursor.getLong(createtime));

				list.add(vo);
			}
		} finally {
			if (cursor != null)
				cursor.close();
			// if(db.isOpen()) db.close();
		}

		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GamePackageDao#getGamePackageByPackageName
	 * (java.lang.String)
	 */
	@Override
	public GamePackageVo getGamePackageByPackageName(String packageName) {
		if (db == null)
			return null;
		GamePackageVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = " packagename=? ";
		arrwhere = new String[1];
		arrwhere[0] = packageName;

		ArrayList<GamePackageVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			// Log.w(TAG_LOG, "no query packageName =  " + packageName);
		}
		return vo;
	}

}
