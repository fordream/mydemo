/**      
 * SqliteTopicTagDaoImpl.java Create on 2015-2-15     
 *      
 * Copyright (c) 2015 by GreenShore Network
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
import com.iwgame.msgs.localdb.dao.TopicTagDao;
import com.iwgame.msgs.vo.local.TopicTagVo;

/**
 * @ClassName: SqliteTopicTagDaoImpl
 * @Description: 贴吧历史标签存储实现
 * @author 王卫
 * @date 2015-2-15 下午1:17:11
 * @Version 1.0
 * 
 */
public class SqliteTopicTagDaoImpl implements TopicTagDao {

	private static final String TAG = "SqliteTopicTagDaoImpl";
	private SQLiteDatabase db;
	private final String[] TB_COLUMNS = new String[] { "id", "tagname", "utime" };
	private final String TB_NAME = "topictag";

	public SqliteTopicTagDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.TopicTagDao#getTopicTagByTagName(java.lang
	 * .String)
	 */
	@Override
	public TopicTagVo getTopicTagByTagName(String tagName) {
		if (db == null)
			return null;

		TopicTagVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "tagname=?";
		arrwhere = new String[1];
		arrwhere[0] = tagName;

		ArrayList<TopicTagVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.e(TAG, "no query id =  " + tagName);
		}
		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.TopicTagDao#insertTopicTag(com.iwgame.msgs
	 * .proto.Msgs.TopicTag)
	 */
	@Override
	public int insertTopicTag(TopicTagVo vo) {
		if (db == null)
			return -1;

		ContentValues contentvalues = new ContentValues();
		contentvalues.put("tagname", vo.getName());
		contentvalues.put("utime", System.currentTimeMillis());
		return (int) db.insert(TB_NAME, null, contentvalues);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.TopicTagDao#updateTopicTag(com.iwgame.msgs
	 * .vo.local.TopicTagVo)
	 */
	@Override
	public int updateTopicTag(TopicTagVo vo) {
		if (db == null)
			return -1;

		ContentValues contentvalues = new ContentValues();
		contentvalues.put("tagname", vo.getName());
		contentvalues.put("utime", System.currentTimeMillis());

		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());

		return db.update(TB_NAME, contentvalues, "id = ? ", arr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.TopicTagDao#insertOrUpdate(com.iwgame.msgs
	 * .proto.Msgs.TopicTag)
	 */
	@Override
	public int insertOrUpdate(TopicTagVo tag) {
		TopicTagVo vo = getTopicTagByTagName(tag.getName());
		if (vo != null) {
			return updateTopicTag(tag);
		} else {
			return insertTopicTag(tag);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.TopicTagDao#getTopicTags(int)
	 */
	@Override
	public List<TopicTagVo> getTopicTags(int limit) {
		if (db == null)
			return null;
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, null, null, null, null, "utime desc", String.valueOf(limit)));
	}

	private ArrayList<TopicTagVo> cursor2Objects(Cursor cursor) {
		ArrayList<TopicTagVo> list = new ArrayList<TopicTagVo>();
		try {
			while (cursor.moveToNext()) {
				TopicTagVo vo = new TopicTagVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int tagname = cursor.getColumnIndexOrThrow("tagname");
				final int utime = cursor.getColumnIndexOrThrow("utime");
				vo.setId(cursor.getInt(idIndex));
				vo.setName(cursor.getString(tagname));
				vo.setUtime(cursor.getLong(utime));
				list.add(vo);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return list;
	}

}
