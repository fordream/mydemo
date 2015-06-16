/**      
* PointTaskDaoImpl.java Create on 2014-8-21     
*      
* Copyright (c) 2014 by GreenShore Network
* Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

*/ 

package com.iwgame.msgs.localdb.dao.sqlite;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.PointTaskDao;
import com.iwgame.msgs.proto.Msgs.PostContent;
import com.iwgame.msgs.vo.local.GroupGradeVo;
import com.iwgame.msgs.vo.local.GroupVo;
import com.iwgame.msgs.vo.local.PointTaskVo;
import com.iwgame.utils.LogUtil;

/** 
 * @ClassName: PointTaskDaoImpl 
 * @Description: TODO(...) 
 * @author 王卫
 * @date 2014-8-21 下午5:53:17 
 * @Version 1.0
 * 
 */
public class SqlitePointTaskDaoImpl implements PointTaskDao {
	
	private static final String TAG = "PointTaskDaoImpl";
	private SQLiteDatabase db;
	private final String[] TB_COLUMNS = new String[] { "id", "taskid", "taskname", "exp","exptimes","detailsBytes","taskdesc", "point","type", "times", "status", "toid", "detail"};
	private final String TB_NAME = "pointtask";

	public SqlitePointTaskDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.PointTaskDao#getDb()
	 */
	@Override
	public SQLiteDatabase getDb() {
		return db;
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.PointTaskDao#insertOrUpdateAll(java.util.List)
	 */
	@Override
	public List<PointTaskVo> insertOrUpdateAll(List<PointTaskVo> items) {
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
	 * @see com.iwgame.msgs.localdb.dao.PointTaskDao#insertOrUpdate(com.iwgame.msgs.vo.local.PointTaskVo)
	 */
	@Override
	public PointTaskVo insertOrUpdate(PointTaskVo item) {
		PointTaskVo vo = queryByTaskId(item.getTaskid());
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

	/**
	 * 用事务的方式插入数据库
	 */
	@Override
	public void insertOrUpdatePointTask(List<PointTaskVo> items) {
		if(db == null) return;
		db.beginTransaction();
		try {
			int numValues = items.size();
			for (int i = 0; i < numValues; i++) {
				insertOrUpdate(items.get(i));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.PointTaskDao#queryAll()
	 */
	@Override
	public List<PointTaskVo> queryAll() {
		if (db == null)
			return null;
		String strwhere = " status <> 1 ";
		return cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, null, null, null, null, null));
	}

	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.PointTaskDao#queryByGrade(int)
	 */
	@Override
	public PointTaskVo queryByTaskId(int tid) {
		if (db == null)
			return null;
		PointTaskVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		String strorder = null;
		String strhaving = null;
		String strgroup = null;
		strwhere = "taskid=?";
		arrwhere = new String[1];
		arrwhere[0] = Integer.toString(tid);
		
		ArrayList<PointTaskVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
		if (list.size() > 0) {
			vo = list.get(0);
		} else {
			Log.w(TAG, "no query id =  " + tid);
		}
		return vo;
	}
	
	/* (non-Javadoc)
	 * @see com.iwgame.msgs.localdb.dao.PointTaskDao#insert(com.iwgame.msgs.vo.local.PointTaskVo)
	 */
	@Override
	public PointTaskVo insert(PointTaskVo vo) {
		if (db == null)
			return null;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("taskid", vo.getTaskid());
		contentvalues.put("taskname", vo.getTaskname());
		contentvalues.put("taskdesc", vo.getTaskdesc());
		contentvalues.put("point", vo.getPoint());
		contentvalues.put("type", vo.getType());
		contentvalues.put("times", vo.getTimes());
		contentvalues.put("status", vo.getStatus());
		contentvalues.put("toid", vo.getToid());
        contentvalues.put("detail", vo.getDetail());
        contentvalues.put("exp", vo.getExp());
        contentvalues.put("exptimes", vo.getExptimes());
        contentvalues.put("detailsBytes", vo.getDetailsBytes());
//        contentvalues.put("postcontent",vo.getPostContent().toByteArray());
//        contentvalues.put("gids", vo.getGids());
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
	 * @see com.iwgame.msgs.localdb.dao.PointTaskDao#update(com.iwgame.msgs.vo.local.PointTaskVo)
	 */
	@Override
	public int update(PointTaskVo vo) {
		if (db == null)
			return -1;
		ContentValues contentvalues = new ContentValues();
		if(vo.getTaskid() > 0)
			contentvalues.put("taskid", vo.getTaskid());
		if(vo.getTaskname() != null)
			contentvalues.put("taskname", vo.getTaskname());
		if(vo.getTaskdesc() != null)
			contentvalues.put("taskdesc", vo.getTaskdesc());
		if(vo.getPoint() > 0)
			contentvalues.put("point", vo.getPoint());
		if(vo.getType() > 0)
			contentvalues.put("type", vo.getType());
		if(vo.getTimes() > 0)
			contentvalues.put("times", vo.getTimes());
		if(vo.getStatus() != -1)
			contentvalues.put("status", vo.getStatus());
		if(vo.getToid() != -1)
			contentvalues.put("toid", vo.getToid());
        if(vo.getDetail() != null){
        	contentvalues.put("detail", vo.getDetail());
        }
        if(vo.getExp() >0){
        	contentvalues.put("exp", vo.getExp());
        }
        if(vo.getExptimes() >0){
        	contentvalues.put("exptimes", vo.getExptimes());
        }
//        if(vo.getPostContent()!=null){
//        	contentvalues.put("postcontent",vo.getPostContent().toByteArray());
//        }
//        if(vo.getDetailsBytes() != null){
//        	contentvalues.put("detailsBytes", vo.getDetailsBytes());
//        }
//        if(vo.getGids()!=null){
//        	 contentvalues.put("gids", vo.getGids());
//        }
        
		String[] arr = new String[1];
		arr[0] = Long.toString(vo.getId());

		return db.update(TB_NAME, contentvalues, "id =? ", arr);
	}
	
	private ArrayList<PointTaskVo> cursor2Objects(Cursor cursor) {
		ArrayList<PointTaskVo> list = new ArrayList<PointTaskVo>();
		try {
			while (cursor.moveToNext()) {
				PointTaskVo vo = new PointTaskVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int taskidIndex = cursor.getColumnIndexOrThrow("taskid");
				final int tasknameIndex = cursor.getColumnIndexOrThrow("taskname");
				final int taskdescIndex = cursor.getColumnIndexOrThrow("taskdesc");
				final int pointIndex = cursor.getColumnIndexOrThrow("point");
				final int typeIndex = cursor.getColumnIndexOrThrow("type");
				final int timesIndex = cursor.getColumnIndexOrThrow("times");
				final int statusIndex = cursor.getColumnIndexOrThrow("status");
				final int toidIndex = cursor.getColumnIndexOrThrow("toid");
                final int detailIndex = cursor.getColumnIndexOrThrow("detail");
                final int expIndex = cursor.getColumnIndexOrThrow("exp");
                final int exptimesIndex = cursor.getColumnIndexOrThrow("exptimes");
                final int detailsBytesIndex = cursor.getColumnIndexOrThrow("detailsBytes");
//                final int postContentIndex =cursor.getColumnIndexOrThrow("postcontent");
//                final int gidsIndex = cursor.getColumnIndexOrThrow("gids");
				vo.setId(cursor.getLong(idIndex));
				vo.setTaskid(cursor.getInt(taskidIndex));
				vo.setTaskname(cursor.getString(tasknameIndex));
				vo.setTaskdesc(cursor.getString(taskdescIndex));
				vo.setPoint(cursor.getInt(pointIndex));
				vo.setType(cursor.getInt(typeIndex));
				vo.setTimes(cursor.getInt(timesIndex));
				vo.setStatus(cursor.getInt(statusIndex));
				vo.setToid(cursor.getInt(toidIndex));
				vo.setDetail(cursor.getString(detailIndex));
				vo.setExp(cursor.getInt(expIndex));
				vo.setExptimes(cursor.getInt(exptimesIndex));
				vo.setDetailsBytes(cursor.getString(detailsBytesIndex));
//				vo.setPostContent(getPostContent(cursor.getBlob(postContentIndex)));
//				vo.setGids(cursor.getString(gidsIndex));
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
	public PostContent getPostContent(byte[] by){
		PostContent postContent = null;
	try {
		ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(by));
		postContent = (PostContent) obj.readObject();
	}  catch (Exception e) {
		//Log.e(TAG, "反序列化失败！"+e.printStackTrace());
	}
				      
	return postContent;
	}

}
