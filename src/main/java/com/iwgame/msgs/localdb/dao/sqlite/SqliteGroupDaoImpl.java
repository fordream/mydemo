/**      
 * SqliteGroupDaoImpl.java Create on 2013-10-24     
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

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.vo.local.GroupVo;

/**
 * @ClassName: SqliteGroupDaoImpl
 * @Description: 公会DAO实现类
 * @author 王卫
 * @date 2013-10-24 上午10:18:15
 * @Version 1.0
 * 
 */
public class SqliteGroupDaoImpl implements GroupDao {

	private SQLiteDatabase db;
	private final String TAG_LOG = "SqliteGroupDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "grid", "name", "avatar", "serial", "presidentId", "gid", "notice", "undesc",
			"creatTime", "utime", "ureltime", "cleanMaxUid", "total", "presidentName", "needValidate", "netoffon", "msgoffon", "grade", "point", "relwithgroup","maxcount", "sid"};
	private final String TB_NAME = "groups";

	/**
	 * 
	 */
	public SqliteGroupDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupDao#insert(com.iwgame.msgs.vo.local.
	 * GroupVo)
	 */
	@Override
	public long insert(GroupVo vo) {
		if (db == null)
			return -1l;
		ContentValues values = new ContentValues();
		values.put("grid", vo.getGrid());
		values.put("name", vo.getName());
		values.put("avatar", vo.getAvatar());
		values.put("serial", vo.getSerial());
		values.put("presidentId", vo.getPresidentId());
		values.put("gid", vo.getGid());
		values.put("notice", vo.getNotice());
		values.put("undesc", vo.getUndesc());
		values.put("creatTime", vo.getCreatTime());
		values.put("utime", vo.getUtime());
		values.put("ureltime", vo.getUreltime());
		values.put("cleanMaxUid", vo.getCleanMaxUid());
		values.put("total", vo.getTotal());
		values.put("presidentName", vo.getPresidentName());
		values.put("needValidate", vo.getNeedValidate());
		values.put("netoffon", vo.getNetoffon());
		values.put("msgoffon", vo.getMsgoffon() == -1 ? 1 : vo.getMsgoffon());
		values.put("grade", vo.getGrade());
		values.put("point", vo.getPoint());
		values.put("relwithgroup", vo.getRelWithGroup());
		values.put("maxcount", vo.getMaxcount());
		values.put("sid", vo.getSid());
		return db.insert(TB_NAME, null, values);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupDao#updateById(com.iwgame.msgs.vo.local
	 * .GroupVo)
	 */
	@Override
	public int update(GroupVo vo) {
		if (db == null)
			return -1;
		ContentValues values = new ContentValues();
		if (vo.getGrid() > 0)
			values.put("grid", vo.getGrid());
		if (vo.getName() != null)
			values.put("name", vo.getName());
		if (vo.getAvatar() != null)
			values.put("avatar", vo.getAvatar());
		if (vo.getSerial() > 0)
			values.put("serial", vo.getSerial());
		if (vo.getPresidentId() > 0)
			values.put("presidentId", vo.getPresidentId());
		if (vo.getGid() > 0)
			values.put("gid", vo.getGid());
		if (vo.getNotice() != null)
			values.put("notice", vo.getNotice());
		if (vo.getUndesc() != null)
			values.put("undesc", vo.getUndesc());
		if (vo.getCreatTime() > 0)
			values.put("creatTime", vo.getCreatTime());
		if (vo.getUtime() > 0)
			values.put("utime", vo.getUtime());
		if (vo.getUreltime() > 0)
			values.put("ureltime", vo.getUreltime());
		if (vo.getCleanMaxUid() > 0)
			values.put("cleanMaxUid", vo.getCleanMaxUid());
		if (vo.getTotal() > 0)
			values.put("total", vo.getTotal());
		if (vo.getPresidentName() != null)
			values.put("presidentName", vo.getPresidentName());
		if (vo.getNeedValidate() != -1)
			values.put("needValidate", vo.getNeedValidate());
		if (vo.getNetoffon() != -1)
			values.put("netoffon", vo.getNetoffon());
		if (vo.getMsgoffon() != -1)
			values.put("msgoffon", vo.getMsgoffon());
		if (vo.getPoint() > 0)
			values.put("point", vo.getPoint());
		if (vo.getGrade() > 0)
			values.put("grade", vo.getGrade());
		if(vo.getRelWithGroup() > 0)
			values.put("relwithgroup", vo.getRelWithGroup());
		if(vo.getMaxcount() > 0)
			values.put("maxcount", vo.getMaxcount());
		if(vo.getSid() > 0)
			values.put("sid", vo.getSid());
		String[] arr = new String[] { Long.toString(vo.getId()) };
		return db.update(TB_NAME, values, "id =? ", arr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.GroupDao#updateOrDeleteById(com.iwgame.msgs
	 * .vo.local.GroupVo)
	 */
	@Override
	public int updateOrInsertById(GroupVo vo) {
		// TODO Auto-generated method stub
		GroupVo gvo = findGroupByGrid(vo.getGrid());
		if (gvo != null) {
			// 更新
			vo.setId(gvo.getId());
			return update(vo);
		} else {
			// 插入
			insert(vo);
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupDao#findGroupByGrid(long)
	 */
	@Override
	public GroupVo findGroupByGrid(long grid) {
		if (db == null)
			return null;
		String selection = "grid=?";
		String[] arrwhere = new String[] { Long.toString(grid) };
		ArrayList<GroupVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, selection, arrwhere, null, null, null));
		if (list != null && list.size() == 1) {
			return list.get(0);
		} else {
			Log.w(TAG_LOG, "no query grid =  " + grid);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupDao#findAllGroups()
	 */
	@Override
	public List<GroupVo> findAllGroups() {
		if (db == null)
			return null;
		long uid = SystemContext.getInstance().getExtUserVo().getUserid();
		String sql = "select t1.* from groups t1 left join groupuserrel t2 on t1.grid = t2.grid where t2.uid = " + uid + " order by t2.rel desc";
		return cursor2Objects(db.rawQuery(sql, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupDao#findAllGroups()
	 */
	@Override
	public List<GroupVo> findAllMyGroups() {
		if (db == null)
			return null;
		long uid = SystemContext.getInstance().getExtUserVo().getUserid();
        String sql = "select * from groups where relwithgroup > 0 order by sid desc, relwithgroup desc";
		return cursor2Objects(db.rawQuery(sql, null));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.GroupDao#deleteByGrid(long)
	 */
	@Override
	public int deleteByGrid(long grid) {
		if (db == null)
			return -1;
		return db.delete(TB_NAME, "grid =? ", new String[] { Long.toString(grid) });
	}

	/**
	 * 
	 * @param cursor
	 * @return
	 */
	private ArrayList<GroupVo> cursor2Objects(Cursor cursor) {
		if (cursor != null) {
			ArrayList<GroupVo> list = new ArrayList<GroupVo>();
			try {
				while (cursor.moveToNext()) {
					GroupVo vo = new GroupVo();
					final int idIndex = cursor.getColumnIndexOrThrow("id");
					final int gridIndex = cursor.getColumnIndexOrThrow("grid");
					final int nameIndex = cursor.getColumnIndexOrThrow("name");
					final int avatarIndex = cursor.getColumnIndexOrThrow("avatar");
					final int serialIndex = cursor.getColumnIndexOrThrow("serial");
					final int presidentIdIndex = cursor.getColumnIndexOrThrow("presidentId");
					final int gidIndex = cursor.getColumnIndexOrThrow("gid");
					final int noticeIndex = cursor.getColumnIndexOrThrow("notice");
					final int undescIndex = cursor.getColumnIndexOrThrow("undesc");
					final int creatTimeIndex = cursor.getColumnIndexOrThrow("creatTime");
					final int utimeIndex = cursor.getColumnIndexOrThrow("utime");
					final int ureltimeIndex = cursor.getColumnIndexOrThrow("ureltime");
					final int cleanMaxUidIndex = cursor.getColumnIndexOrThrow("cleanMaxUid");
					final int totalIndex = cursor.getColumnIndexOrThrow("total");
					final int presidentNameIndex = cursor.getColumnIndexOrThrow("presidentName");
					final int needValidateIndex = cursor.getColumnIndexOrThrow("needValidate");
					final int netoffonIndex = cursor.getColumnIndexOrThrow("netoffon");
					final int msgoffonIndex = cursor.getColumnIndexOrThrow("msgoffon");
					final int gradeIndex = cursor.getColumnIndexOrThrow("grade");
					final int pointIndex = cursor.getColumnIndexOrThrow("point");
					final int relwithgroup = cursor.getColumnIndexOrThrow("relwithgroup");
					final int maxcount = cursor.getColumnIndexOrThrow("maxcount");
					final int sid = cursor.getColumnIndexOrThrow("sid");
					vo.setId(cursor.getLong(idIndex));
					vo.setGrid(cursor.getLong(gridIndex));
					vo.setName(cursor.getString(nameIndex));
					vo.setAvatar(cursor.getString(avatarIndex));
					vo.setSerial(cursor.getLong(serialIndex));
					vo.setPresidentId(cursor.getLong(presidentIdIndex));
					vo.setGid(cursor.getLong(gidIndex));
					vo.setNotice(cursor.getString(noticeIndex));
					vo.setUndesc(cursor.getString(undescIndex));
					vo.setCreatTime(cursor.getLong(creatTimeIndex));
					vo.setUtime(cursor.getLong(utimeIndex));
					vo.setUreltime(cursor.getLong(ureltimeIndex));
					vo.setCleanMaxUid(cursor.getLong(cleanMaxUidIndex));
					vo.setTotal(cursor.getInt(totalIndex));
					vo.setPresidentName(cursor.getString(presidentNameIndex));
					vo.setNeedValidate(cursor.getInt(needValidateIndex));
					vo.setNetoffon(cursor.getInt(netoffonIndex));
					vo.setMsgoffon(cursor.getInt(msgoffonIndex));
					vo.setGrade(cursor.getInt(gradeIndex));
					vo.setPoint(cursor.getInt(pointIndex));
					vo.setRelWithGroup(cursor.getInt(relwithgroup));
					vo.setMaxcount(cursor.getInt(maxcount));
					vo.setSid(cursor.getLong(sid));
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

	/**
	 * 用事务的方式插入数据库
	 */
	@Override
	public void insertOrUpdate(List<GroupVo> items) {
		if(db == null) return;
		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				updateOrInsertById(items.get(i));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

}
