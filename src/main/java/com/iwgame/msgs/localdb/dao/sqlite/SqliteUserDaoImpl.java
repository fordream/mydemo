package com.iwgame.msgs.localdb.dao.sqlite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.localdb.dao.UserDao;
import com.iwgame.msgs.module.group.object.UserItemObj;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.UserVo;
import com.iwgame.utils.LogUtil;

public class SqliteUserDaoImpl implements UserDao {
	private static final String TAG = "SqliteUserDaoImpl";
	private SQLiteDatabase db;
	private final String TAG_LOG = "SqliteUserDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "userid", "username", "serial", "avatar", "rel_positive", "rel_inverse", "grade", "sex",
			"city", "mood", "description", "updatetime", "age", "job", "gametime", "likegametype", "mobile", "weibo", "mobilename", "weiboname", "point", "remarkname", "atime"};
	private final String TB_NAME = "user";

	public SqliteUserDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	@Override
	public UserVo insert(UserVo vo) {
		if (db == null)
			return null;

		ContentValues contentvalues = new ContentValues();
		contentvalues.put("userid", vo.getUserid());
		contentvalues.put("username", vo.getUsername());
		contentvalues.put("serial", vo.getSerial());
		contentvalues.put("avatar", vo.getAvatar());
		if (vo.getRelPositive() != -1)
			contentvalues.put("rel_positive", vo.getRelPositive());
		else
			contentvalues.put("rel_positive", 0);
		if (vo.getRelInverse() != -1)
			contentvalues.put("rel_inverse", vo.getRelInverse());
		else
			contentvalues.put("rel_inverse", 0);
		contentvalues.put("grade", vo.getGrade());
		if (vo.getSex() != -1)
			contentvalues.put("sex", vo.getSex());
		else
			contentvalues.put("sex", 0);
		contentvalues.put("city", vo.getCity());
		contentvalues.put("mood", vo.getMood());
		contentvalues.put("description", vo.getDescription());
		contentvalues.put("updatetime", vo.getUpdatetime());
		if (vo.getAge() != -1)
			contentvalues.put("age", vo.getAge());
		else
			contentvalues.put("age", 0);
		contentvalues.put("job", vo.getJob());
		contentvalues.put("gametime", vo.getGameTime());
		contentvalues.put("likegametype", vo.getLikeGameType());
		contentvalues.put("mobile", vo.getMobile());
		contentvalues.put("weibo", vo.getWeibo());
		contentvalues.put("mobilename", vo.getMobileName());
		contentvalues.put("weiboname", vo.getWeiboName());
		contentvalues.put("point", vo.getPoint());
		contentvalues.put("atime", vo.getAtime());
		contentvalues.put("remarkname", vo.getRemarksName());
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
	public int updateById(UserVo vo) {
		if (db == null)
			return -1;

		ContentValues contentvalues = new ContentValues();
		if (vo.getUserid() > 0)
			contentvalues.put("userid", vo.getUserid());
		if (vo.getUsername() != null)
			contentvalues.put("username", vo.getUsername());
		if (vo.getSerial() > 0)
			contentvalues.put("serial", vo.getSerial());
		if (vo.getAvatar() != null)
			contentvalues.put("avatar", vo.getAvatar());
		if (vo.getGrade() > 0)
			contentvalues.put("grade", vo.getGrade());
		if (vo.getSex() > -1)
			contentvalues.put("sex", vo.getSex());
		if (vo.getCity() != null)
			contentvalues.put("city", vo.getCity());
		if (vo.getMood() != null)
			contentvalues.put("mood", vo.getMood());
		if (vo.getDescription() != null)
			contentvalues.put("description", vo.getDescription());
		if (vo.getUpdatetime() > 0)
			contentvalues.put("updatetime", vo.getUpdatetime());
		if (vo.getAge() != -1)
			contentvalues.put("age", vo.getAge());
		if (vo.getJob() != null)
			contentvalues.put("job", vo.getJob());
		if (vo.getGameTime() != null)
			contentvalues.put("gametime", vo.getGameTime());
		if (vo.getLikeGameType() != null)
			contentvalues.put("likegametype", vo.getLikeGameType());
		if (vo.getRelPositive() != -1)
			contentvalues.put("rel_positive", vo.getRelPositive());
		if (vo.getRelInverse() != -1)
			contentvalues.put("rel_inverse", vo.getRelInverse());
		if (vo.getMobile() != null)
			contentvalues.put("mobile", vo.getMobile());
		if (vo.getWeibo() != null)
			contentvalues.put("weibo", vo.getWeibo());
		if (vo.getMobileName() != null)
			contentvalues.put("mobilename", vo.getMobileName());
		if (vo.getWeiboName() != null)
			contentvalues.put("weiboname", vo.getWeiboName());
		if(vo.getPoint() > 0)
			contentvalues.put("point", vo.getPoint());
		if(vo.getAtime() > 0)
			contentvalues.put("atime", vo.getAtime());
		if(vo.getRemarksName() != null)
			contentvalues.put("remarkname", vo.getRemarksName());
		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());

		return db.update(TB_NAME, contentvalues, "id = ? ", arr);

	}

	@Override
	public int updateByUserRel(long userid, int positive, int inverse) {
		if (db == null)
			return -1;

		ContentValues contentvalues = new ContentValues();
		contentvalues.put("rel_positive", positive);
		contentvalues.put("rel_inverse", inverse);
		String[] arr = new String[] { String.valueOf(userid) };
		return db.update(TB_NAME, contentvalues, "userid = ? ", arr);
	}

	@Override
	public UserVo insertOrUpdateByUserid(UserVo vo) {
		// TODO Auto-generated method stub
		UserVo vo2 = getUserByUserId(vo.getUserid());
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
	public List<UserVo> bulkInsert(List<UserVo> items) {
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
	public List<UserVo> bulkInsertOrUpdateByUserid(List<UserVo> items) {
		if (db == null)
			return null;

		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				items.set(i, insertOrUpdateByUserid(items.get(i)));
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

	// @Override
	// public int deleteByUserid(long userid) {
	// // TODO Auto-generated method stub
	// String[] arr = new String[1];
	// arr[0] = Long.toString(userid);
	// return db.delete(TB_NAME, "userid =? ", arr);
	// }
	@Override
	public int deleteByRel(int relPositive, int relInverse) {
		if (db == null)
			return -1;

		String[] arr = new String[2];
		arr[0] = String.valueOf(relPositive);
		arr[1] = String.valueOf(relInverse);
		return db.delete(TB_NAME, "rel_positive =? and rel_inverse = ? ", arr);
	}

	@Override
	public UserVo getUserById(long id) {
		if (db == null)
			return null;

		UserVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "id=?";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(id);

		ArrayList<UserVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.e(TAG_LOG, "no query id =  " + id);
		}
		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.UserDao#getUserByUserId(long)
	 */
	@Override
	public UserVo getUserByUserId(long userid) {
		if (db == null)
			return null;
		String strwhere = "userid=?";
		String[] arrwhere = { String.valueOf(userid) };
		ArrayList<UserVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			Log.w(TAG_LOG, "no query userid =  " + userid);
		}
		return null;
	}

	@Override
	public List<UserVo> getUserListByKeyIsUseid(int relation, long keyid, int direction, int size) {
		if (db == null)
			return null;

		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		if (relation > 100) {
			strwhere = "rel_inverse=? ";
			relation = relation - 100;
		} else {
			strwhere = "rel_positive=? ";
		}
		if (direction == 1)// 向下，向大的数字
		{
			strwhere += " and userid>? ";
		} else if (direction == 2)// 向前，向小的数字
		{
			strwhere += " and userid<? ";
		}
		arrwhere = new String[2];
		arrwhere[0] = Long.toString(relation);
		arrwhere[1] = Long.toString(keyid);

		String strlimit = Long.toString(size);

		ArrayList<UserVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder, strlimit));
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.UserDao#getUsersByRelation(int, int)
	 */
	@Override
	public List<UserVo> getUsersByRelation(int type, int relation, int orderType) {
		if (db == null)
			return null;

		String strwhere = "";
		if (type == 1) {
			strwhere = "rel_positive=? ";
		} else if (type == 2) {
			strwhere = "rel_inverse=? ";
		}
		String[] arrwhere = { String.valueOf(relation) };
		ArrayList<UserVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null, null));
		return list;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.UserDao#getUsersByRelation(int, int)
	 */
	@Override
	public Map<Long, UserVo> getUsersByRel(int type, int relation, int orderType) {
		if (db == null)
			return null;

		String strwhere = "";
		if (type == 1) {
			strwhere = "rel_positive=? ";
		} else if (type == 2) {
			strwhere = "rel_inverse=? ";
		}
		String[] arrwhere = { String.valueOf(relation) };
		Map<Long, UserVo> list = getMap(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null, null));
		return list;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.UserDao#getUsersByRelation(int, int,
	 * String)
	 */
	@Override
	public List<UserVo> getUsersByRelation(int type, int relation, int orderType, String keyword) {
		if (db == null)
			return null;

		LogUtil.d(TAG, "--------获取通讯录好友数据DAO：type=" + type + ", relation=" + relation + ", keyword=" + keyword);
		String strwhere = "";
		if (type == 1) {
			strwhere = "rel_positive=? ";
		} else if (type == 2) {
			strwhere = "rel_inverse=? ";
		}
		strwhere += " and (username like ? or remarkname like ?)";
		String sql = "select * from user where " + strwhere;
		String[] selectionArgs = { String.valueOf(relation), "%" + keyword + "%", "%" + keyword + "%"};
		LogUtil.d(TAG, "------------sql=" + sql);
		LogUtil.d(TAG, "------------selectionArgs=" + Arrays.toString(selectionArgs));
		ArrayList<UserVo> list = cursor2Objects(db.rawQuery(sql, selectionArgs));
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.UserDao#findInviteUsers(long)
	 */
	@Override
	public List<UserVo> findInviteUsers(long grid) {
		if (db == null)
			return null;

		String strwhere = "grid=? ";
		String sql = "select * from user where rel_positive = 1 and userid not in (select uid from groupuserrel where " + strwhere + ")";
		String[] selectionArgs = new String[] { Long.toString(grid) };
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		ArrayList<UserVo> list = cursor2Objects(cursor);
		return list;
	}

	private ArrayList<UserVo> cursor2Objects(Cursor cursor) {
		ArrayList<UserVo> list = new ArrayList<UserVo>();
		try {
			while (cursor.moveToNext()) {
				UserVo vo = new UserVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int useridIndex = cursor.getColumnIndexOrThrow("userid");
				final int usernameIndex = cursor.getColumnIndexOrThrow("username");
				final int serialIndex = cursor.getColumnIndexOrThrow("serial");
				final int avatarIndex = cursor.getColumnIndexOrThrow("avatar");
				final int relPositiveIndex = cursor.getColumnIndexOrThrow("rel_positive");
				final int relInverseIndex = cursor.getColumnIndexOrThrow("rel_inverse");
				final int gradeIndex = cursor.getColumnIndexOrThrow("grade");
				final int sexIndex = cursor.getColumnIndexOrThrow("sex");
				final int cityIndex = cursor.getColumnIndexOrThrow("city");
				final int moodIndex = cursor.getColumnIndexOrThrow("mood");
				final int descriptionIndex = cursor.getColumnIndexOrThrow("description");
				final int updatetimeIndex = cursor.getColumnIndexOrThrow("updatetime");
				final int ageIndex = cursor.getColumnIndexOrThrow("age");
				final int jobIndex = cursor.getColumnIndexOrThrow("job");
				final int gametimeIndex = cursor.getColumnIndexOrThrow("gametime");
				final int likegametypeIndex = cursor.getColumnIndexOrThrow("likegametype");
				final int mobileIndex = cursor.getColumnIndexOrThrow("mobile");
				final int weiboIndex = cursor.getColumnIndexOrThrow("weibo");
				final int mobileNameIndex = cursor.getColumnIndexOrThrow("mobilename");
				final int weiboNameIndex = cursor.getColumnIndexOrThrow("weiboname");
				final int pointIndex = cursor.getColumnIndexOrThrow("point");
				final int atimeIndex = cursor.getColumnIndexOrThrow("atime");
				final int remarknameIndex = cursor.getColumnIndexOrThrow("remarkname");
				vo.setId(cursor.getInt(idIndex));
				vo.setUserid(cursor.getLong(useridIndex));
				vo.setUsername(cursor.getString(usernameIndex));
				vo.setSerial(cursor.getLong(serialIndex));
				vo.setAvatar(cursor.getString(avatarIndex));
				vo.setRelPositive(cursor.getInt(relPositiveIndex));
				vo.setRelInverse(cursor.getInt(relInverseIndex));
				vo.setGrade(cursor.getInt(gradeIndex));
				vo.setSex(cursor.getInt(sexIndex));
				vo.setCity(cursor.getString(cityIndex));
				vo.setMood(cursor.getString(moodIndex));
				vo.setDescription(cursor.getString(descriptionIndex));
				vo.setUpdatetime(cursor.getLong(updatetimeIndex));
				vo.setAge(cursor.getInt(ageIndex));
				vo.setJob(cursor.getString(jobIndex));
				vo.setGameTime(cursor.getString(gametimeIndex));
				vo.setLikeGameType(cursor.getString(likegametypeIndex));
				vo.setMobile(cursor.getString(mobileIndex));
				vo.setWeibo(cursor.getString(weiboIndex));
				vo.setMobileName(cursor.getString(mobileNameIndex));
				vo.setWeiboName(cursor.getString(weiboNameIndex));
				vo.setAtime(cursor.getLong(atimeIndex));
				vo.setPoint(cursor.getInt(pointIndex));
				vo.setRemarksName(cursor.getString(remarknameIndex));
				list.add(vo);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return list;
	}

	
	private Map<Long, UserVo> getMap(Cursor cursor) {
		Map<Long, UserVo> list = new HashMap<Long, UserVo>();
		try {
			while (cursor.moveToNext()) {
				UserVo vo = new UserVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int useridIndex = cursor.getColumnIndexOrThrow("userid");
				final int usernameIndex = cursor.getColumnIndexOrThrow("username");
				final int serialIndex = cursor.getColumnIndexOrThrow("serial");
				final int avatarIndex = cursor.getColumnIndexOrThrow("avatar");
				final int relPositiveIndex = cursor.getColumnIndexOrThrow("rel_positive");
				final int relInverseIndex = cursor.getColumnIndexOrThrow("rel_inverse");
				final int gradeIndex = cursor.getColumnIndexOrThrow("grade");
				final int sexIndex = cursor.getColumnIndexOrThrow("sex");
				final int cityIndex = cursor.getColumnIndexOrThrow("city");
				final int moodIndex = cursor.getColumnIndexOrThrow("mood");
				final int descriptionIndex = cursor.getColumnIndexOrThrow("description");
				final int updatetimeIndex = cursor.getColumnIndexOrThrow("updatetime");
				final int ageIndex = cursor.getColumnIndexOrThrow("age");
				final int jobIndex = cursor.getColumnIndexOrThrow("job");
				final int gametimeIndex = cursor.getColumnIndexOrThrow("gametime");
				final int likegametypeIndex = cursor.getColumnIndexOrThrow("likegametype");
				final int mobileIndex = cursor.getColumnIndexOrThrow("mobile");
				final int weiboIndex = cursor.getColumnIndexOrThrow("weibo");
				final int mobileNameIndex = cursor.getColumnIndexOrThrow("mobilename");
				final int weiboNameIndex = cursor.getColumnIndexOrThrow("weiboname");
				final int pointIndex = cursor.getColumnIndexOrThrow("point");
				final int atimeIndex = cursor.getColumnIndexOrThrow("atime");
				final int remarknameIndex = cursor.getColumnIndexOrThrow("remarkname");
				vo.setId(cursor.getInt(idIndex));
				vo.setUserid(cursor.getLong(useridIndex));
				vo.setUsername(cursor.getString(usernameIndex));
				vo.setSerial(cursor.getLong(serialIndex));
				vo.setAvatar(cursor.getString(avatarIndex));
				vo.setRelPositive(cursor.getInt(relPositiveIndex));
				vo.setRelInverse(cursor.getInt(relInverseIndex));
				vo.setGrade(cursor.getInt(gradeIndex));
				vo.setSex(cursor.getInt(sexIndex));
				vo.setCity(cursor.getString(cityIndex));
				vo.setMood(cursor.getString(moodIndex));
				vo.setDescription(cursor.getString(descriptionIndex));
				vo.setUpdatetime(cursor.getLong(updatetimeIndex));
				vo.setAge(cursor.getInt(ageIndex));
				vo.setJob(cursor.getString(jobIndex));
				vo.setGameTime(cursor.getString(gametimeIndex));
				vo.setLikeGameType(cursor.getString(likegametypeIndex));
				vo.setMobile(cursor.getString(mobileIndex));
				vo.setWeibo(cursor.getString(weiboIndex));
				vo.setMobileName(cursor.getString(mobileNameIndex));
				vo.setWeiboName(cursor.getString(weiboNameIndex));
				vo.setAtime(cursor.getLong(atimeIndex));
				vo.setPoint(cursor.getInt(pointIndex));
				vo.setRemarksName(cursor.getString(remarknameIndex));
				list.put(cursor.getLong(useridIndex), vo);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return list;
	}
	
	
	
	/**
	 * 通过关键字去查询
	 * 关注的用户
	 * 邀请成员
	 */
	@Override
	public List<UserItemObj> getFollowUserByRelation(int type, int relation,
			int orderType, String keyword, long grid) {
		if (db == null)
			return null;

		LogUtil.d(TAG, "--------获取通讯录好友数据DAO：type=" + type + ", relation=" + relation + ", keyword=" + keyword);
		String strwhere = "";
		if (type == 1) {
			strwhere = "rel_positive=? ";
		} else if (type == 2) {
			strwhere = "rel_inverse=? ";
		}
		strwhere += " and (username like ? or remarkname like ?)";
		String sql = "select * from user where " + strwhere;
		String[] selectionArgs = { String.valueOf(relation), "%" + keyword + "%", "%" + keyword + "%"};
		LogUtil.d(TAG, "------------sql=" + sql);
		LogUtil.d(TAG, "------------selectionArgs=" + Arrays.toString(selectionArgs));
		ArrayList<UserItemObj> list = cursor3Objects(grid,db.rawQuery(sql, selectionArgs));
		return list;
	}

	/**
	 * 获取到用户的
	 * 详细信息
	 * @param rawQuery
	 * @return
	 */
	private ArrayList<UserItemObj> cursor3Objects(long grid,Cursor cursor) {
		GroupUserRelDao userRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getContext());
		ArrayList<UserItemObj> list = new ArrayList<UserItemObj>();
		try {
			GroupUserRelVo userRelVo;
			while (cursor.moveToNext()) {
				UserItemObj obj = new UserItemObj();
				final int useridIndex = cursor.getColumnIndexOrThrow("userid");
				final int usernameIndex = cursor.getColumnIndexOrThrow("username");
				final int avatarIndex = cursor.getColumnIndexOrThrow("avatar");
				final int gradeIndex = cursor.getColumnIndexOrThrow("grade");
				final int sexIndex = cursor.getColumnIndexOrThrow("sex");
				final int moodIndex = cursor.getColumnIndexOrThrow("mood");
				final int ageIndex = cursor.getColumnIndexOrThrow("age");
				final int remarkIndex = cursor.getColumnIndexOrThrow("remarkname");
				userRelVo = userRelDao.findUsers(grid, cursor.getLong(useridIndex));
				if(userRelVo != null) continue;
				obj.setUid(cursor.getLong(useridIndex));
				obj.setAvatar(cursor.getString(avatarIndex));
				obj.setNickname(cursor.getString(usernameIndex));
				obj.setSex(cursor.getInt(sexIndex));
				obj.setAge(cursor.getInt(ageIndex));
				obj.setMood(cursor.getString(moodIndex));
				obj.setGrade(cursor.getInt(gradeIndex));
				obj.setRemark(cursor.getString(remarkIndex));
				list.add(obj);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return list;
	}
}
