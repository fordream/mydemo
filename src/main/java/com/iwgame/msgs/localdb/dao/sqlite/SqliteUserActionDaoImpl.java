/**      
 * UserActionDaoImpl.java Create on 2014-1-28     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.localdb.dao.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.UserActionDao;
import com.iwgame.msgs.vo.local.UserActionVo;
import com.iwgame.utils.DateUtil;

/**
 * @ClassName: UserActionDaoImpl
 * @Description: TODO(...)
 * @author 王卫
 * @date 2014-1-28 上午11:15:58
 * @Version 1.0
 * 
 */
public class SqliteUserActionDaoImpl implements UserActionDao {
	private SQLiteDatabase db;
	private final String TAG_LOG = "UserActionDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "entitytype", "entityid", "count", "optype", "content", "creattime" };
	private final String TB_NAME = "useraction";

	public SqliteUserActionDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.UserActionDao#insert(com.iwgame.msgs.vo.local
	 * .UserActionVo)
	 */
	@Override
	public long insert(UserActionVo vo) {
		if (db == null)
			return -1l;

		ContentValues contentvalues = new ContentValues();
		contentvalues.put("entitytype", vo.getEntitytype());
		contentvalues.put("entityid", vo.getEntityid());
		contentvalues.put("count", vo.getCount());
		contentvalues.put("optype", vo.getOpttype());
		contentvalues.put("content", vo.getContent());
		contentvalues.put("creattime", vo.getCreattime());
		return db.insert(TB_NAME, null, contentvalues);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.UserActionDao#updata(com.iwgame.msgs.vo.local
	 * .UserActionVo)
	 */
	@Override
	public int updata(UserActionVo vo) {
		if (db == null)
			return -1;

		ContentValues contentvalues = new ContentValues();
		if (vo.getEntitytype() > 0)
			contentvalues.put("entitytype", vo.getEntitytype());
		if (vo.getEntityid() > 0)
			contentvalues.put("entityid", vo.getEntityid());
		if (vo.getCount() > 0)
			contentvalues.put("count", vo.getCount());
		if (vo.getOpttype() > 0)
			contentvalues.put("optype", vo.getOpttype());
		if (vo.getContent() != null)
			contentvalues.put("content", vo.getContent());
		if (vo.getCreattime() > 0)
			contentvalues.put("creattime", vo.getCreattime());
		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());
		return db.update(TB_NAME, contentvalues, "id = ? ", arr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.UserActionDao#insertGroupApply(long)
	 */
	@Override
	public void insertUserAction(long grid, int entityType, int opType) {
		UserActionVo vo = findUserAction(grid, entityType, opType);
		if (vo != null) {
			boolean istoday = DateUtil.isToday(vo.getCreattime());
			if (istoday) {
				vo.setCount(vo.getCount() + 1);
			} else {
				vo.setCount(1);
			}
			vo.setCreattime(System.currentTimeMillis());
			updata(vo);
		} else {
			vo = new UserActionVo();
			vo.setCount(1);
			vo.setCreattime(System.currentTimeMillis());
			vo.setEntityid(grid);
			vo.setEntitytype(entityType);
			vo.setOpttype(opType);
			insert(vo);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.UserActionDao#findGroupApply(long)
	 */
	@Override
	public UserActionVo findUserAction(long entityId, int entityType, int opType) {
		if (db == null)
			return null;

		String strwhere = " entitytype = ? and entityid = ? and optype = ? ";
		String[] arrwhere = { String.valueOf(entityType), String.valueOf(entityId), String.valueOf(opType) };
		ArrayList<UserActionVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null, null));
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	private ArrayList<UserActionVo> cursor2Objects(Cursor cursor) {
		ArrayList<UserActionVo> list = new ArrayList<UserActionVo>();
		try {
			while (cursor.moveToNext()) {
				UserActionVo vo = new UserActionVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int entitytype = cursor.getColumnIndexOrThrow("entitytype");
				final int entityid = cursor.getColumnIndexOrThrow("entityid");
				final int count = cursor.getColumnIndexOrThrow("count");
				final int optype = cursor.getColumnIndexOrThrow("optype");
				final int content = cursor.getColumnIndexOrThrow("content");
				final int creattime = cursor.getColumnIndexOrThrow("creattime");

				vo.setId(cursor.getInt(idIndex));
				vo.setEntitytype(cursor.getInt(entitytype));
				vo.setEntityid(cursor.getLong(entityid));
				vo.setCount(cursor.getInt(count));
				vo.setOpttype(cursor.getInt(optype));
				vo.setContent(cursor.getString(content));
				vo.setCreattime(cursor.getLong(creattime));
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
