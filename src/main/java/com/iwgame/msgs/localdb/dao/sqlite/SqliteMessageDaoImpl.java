/**      
 * SqliteMessageDaoImpl.java Create on 2013-11-13     
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

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.MessageDao;
import com.iwgame.msgs.vo.local.MessageVo;
import com.iwgame.utils.DateUtil;

/**
 * @ClassName: SqliteMessageDaoImpl
 * @Description: MessageDao实现类
 * @author chuanglong
 * @date 2013-11-13 上午9:53:46
 * @Version 1.0
 * 
 */
public class SqliteMessageDaoImpl implements MessageDao {

	private SQLiteDatabase db;
	private final String TAG_LOG = "SqliteMessageDaoImpl";
	private final String[] TB_COLUMNS = new String[] { "id", "source", "channeltype", "msgid", "fromid", "fromdomain", "toid", "todomain",
			"subjectid", "subjectdomain", "category", "summary", "contenttype", "content", "createtime", "position", "readstatus", "status",
			"forwardId", "forwardType", "msgindex", "ext", "estimatetype", "estimateop", "notnotify" };
	private final String TB_NAME = "message";

	public SqliteMessageDaoImpl(Context context) {
		MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
		if (helper != null)
			db = helper.getWritableDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.MessageDao#insert(com.iwgame.msgs.vo.local
	 * .MessageVo)
	 */
	@Override
	public MessageVo insert(MessageVo vo) {
		if (db == null)
			return null;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("source", vo.getSource());
		contentvalues.put("channeltype", vo.getChannelType());
		contentvalues.put("msgid", vo.getMsgId());
		contentvalues.put("fromid", vo.getFromId());
		contentvalues.put("fromdomain", vo.getFromDomain());
		contentvalues.put("toid", vo.getToId());
		contentvalues.put("todomain", vo.getToDomain());
		contentvalues.put("subjectid", vo.getSubjectId());
		contentvalues.put("subjectdomain", vo.getSubjectDomain());
		contentvalues.put("category", vo.getCategory());
		contentvalues.put("summary", vo.getSummary());
		contentvalues.put("contenttype", vo.getContentType());
		contentvalues.put("content", vo.getContent());
		contentvalues.put("createtime", vo.getCreateTime());
		contentvalues.put("position", vo.getPosition());
		contentvalues.put("readstatus", vo.getReadStatus());
		contentvalues.put("status", vo.getStatus());
		contentvalues.put("forwardId", vo.getForwardId());
		contentvalues.put("forwardType", vo.getForwardType());
		contentvalues.put("msgindex", vo.getMsgIndex());
		contentvalues.put("estimatetype", vo.getEstimatetype());
		contentvalues.put("estimateop", vo.getEstimateop());
		contentvalues.put("notnotify", vo.getNotNotify());
		contentvalues.put("ext", vo.getExt());

		int rowid = (int) db.insert(TB_NAME, null, contentvalues);
		if (rowid < 0) {
			Log.e(TAG_LOG, "insert is error: " + vo.toString());
			vo = null;
		} else {
			vo.setId(rowid);
		}
		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#updateById(long, long,
	 * java.lang.String, int)
	 */
	@Override
	public int updateById(int id, long msgid, long msgindex, long createTime, int contentType, String content, int status) {
		if (db == null)
			return -1;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("status", status);
		if (status == MessageVo.STATUS_SENDSUCC) {

			contentvalues.put("msgid", msgid);
			contentvalues.put("createTime", createTime);
			contentvalues.put("msgindex", msgindex);
			if (content != null && !content.trim().equals("")) {
				contentvalues.put("contenttype", contentType);
				contentvalues.put("content", content);
			}

		} else if (status == MessageVo.STATUS_SENDING) {
			if (content != null && !content.trim().equals("")) {
				contentvalues.put("contenttype", contentType);
				contentvalues.put("content", content);
			}
		} else if (status == MessageVo.STATUS_SENDERR || status == MessageVo.STATUS_DEL) {

		}
		return db.update(TB_NAME, contentvalues, " id = ? ", new String[] { Long.toString(id) });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#updateMessageContentById(int,
	 * int,String)
	 */
	@Override
	public int updateMessageContentById(int id, int contentType, String content) {
		if (db == null)
			return -1;
		ContentValues contentvalues = new ContentValues();
		if (content != null && !content.trim().equals("")) {
			contentvalues.put("contenttype", contentType);
			contentvalues.put("content", content);
		}
		return db.update(TB_NAME, contentvalues, " id = ? ", new String[] { Long.toString(id) });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#getMessageByMsgId(long)
	 */
	@Override
	public MessageVo getMessageByMsgId(long msgid) {
		if (db == null)
			return null;
		MessageVo vo = null;
		String strwhere = null;
		String[] arrwhere = null;
		strwhere = "msgid=?";
		arrwhere = new String[1];
		arrwhere[0] = Long.toString(msgid);
		List<MessageVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, null, null, null));
		if (list.size() > 0) {
			vo = list.get(0);
		}
		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#delMessage(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	@Override
	public int delMessage(String channelType, long subjectid, String subjectDomain, String category) {
		if (db == null)
			return -1;
		String strWhere = "";
		if (subjectid == -1) {
			strWhere = "channelType = ? and subjectid <> ? and subjectdomain =? and category = ?";
		} else {
			strWhere = "channelType = ? and subjectid = ? and subjectdomain =? and category = ?";
		}
		String[] arrwhere = new String[] { channelType, Long.toString(subjectid), subjectDomain, category };
		return db.delete(TB_NAME, strWhere, arrwhere);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#getMessage(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MessageVo> getMessage(String channelType, long subjectid, String subjectDomain, String category, long msgindex, int limit,
			long minIndex) {
		return getMessage(channelType, subjectid, subjectDomain, category, msgindex, limit, minIndex, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#getMessage(java.lang.String,
	 * long, java.lang.String, java.lang.String, long, int, long, int, int)
	 */
	@Override
	public List<MessageVo> getMessage(String channelType, long subjectid, String subjectDomain, String category, long msgindex, int limit,
			long minIndex, int estimateop) {
		if (db == null)
			return new ArrayList<MessageVo>();
		String sql = "";
		String selectionArgs[] = null;
		sql = "select * from " + TB_NAME;
		sql += " where channelType = ? ";
		sql += " and subjectid = ? ";
		sql += " and subjectdomain =? and category = ? ";
		if (estimateop != -1) {
			sql += " and estimateop = ? ";
		}
		sql += " and msgindex < ? ";
		// sql += " and msgindex > ? ";
		sql += " order by msgindex desc,id desc ";
		sql += " limit ?";
		if (estimateop != -1) {
			selectionArgs = new String[] { channelType, Long.toString(subjectid), subjectDomain, category, Integer.toString(estimateop),
					Long.toString(msgindex), Integer.toString(-limit) };
		} else {
			selectionArgs = new String[] { channelType, Long.toString(subjectid), subjectDomain, category, Long.toString(msgindex),
					Integer.toString(-limit) };
		}
		// selectionArgs = new String[] { channelType, Long.toString(subjectid),
		// subjectDomain, category,
		// Long.toString(msgindex),Long.toString(minIndex),Integer.toString(-limit)
		// };
		List<MessageVo> list = cursor2Objects(db.rawQuery(sql, selectionArgs));
		// 查找条数是否是需要的条数，如果是的话，就补充，有可能相同 的msgindex ，这是本地保存的发送失败的原因
		if (list.size() == -limit) {
			sql = "";
			sql = " select * from " + TB_NAME;
			sql += " where msgindex = " + list.get(list.size() - 1).getMsgIndex();
			sql += " and channelType = ? ";
			sql += " and subjectid = ? ";
			sql += " and subjectdomain =? and category = ? ";
			sql += " order by msgindex desc ,id desc";

			selectionArgs = new String[] { channelType, Long.toString(subjectid), subjectDomain, category };
			List<MessageVo> list2 = cursor2Objects(db.rawQuery(sql, null));
			if (list2.size() > 1) {
				int tmplist2noRepeatIndex = 1;
				for (int i = 0; i < list2.size(); i++) {
					int tmpid = list2.get(i).getId();
					int tmplistRepeatIndex = Integer.MAX_VALUE;
					for (int j = list.size() - 1; j >= 0; j--) {
						// 找到重复
						if (list.get(j).getId() == tmpid) {
							tmplistRepeatIndex = j;
							break;
						}
					}

					if (tmplistRepeatIndex != Integer.MAX_VALUE) {
						// 算出 tmplistRepeatIndex下面

						tmplist2noRepeatIndex = i + (list.size() - tmplistRepeatIndex);
						break;
					}
				}
				for (int i = tmplist2noRepeatIndex; i < list2.size(); i++) {
					list.add(list2.get(i));
				}

			}

		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#getMessage(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MessageVo> getFromLastMessage(String channelType, long subjectid, String subjectDomain, String category) {
		// TODO Auto-generated method stub
		if (db == null)
			return new ArrayList<MessageVo>();
		String sql = "";
		String selectionArgs[] = null;
		sql = "select * from " + TB_NAME;
		sql += " where channelType = ? ";
		sql += " and subjectid = ? ";
		sql += " and subjectdomain =? and category = ? ";
		sql += " and status != " + MessageVo.STATUS_DEL;
		sql += " group by fromid,fromdomain";
		sql += " order by msgindex desc ";
		selectionArgs = new String[] { channelType, Long.toString(subjectid), subjectDomain, category };
		List<MessageVo> list = cursor2Objects(db.rawQuery(sql, selectionArgs));
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#getSubjectLastMessage()
	 */
	@Override
	public List<MessageVo> getSubjectLastMessage() {
		if (db == null)
			return new ArrayList<MessageVo>();
		String sql = "";
		sql += " select *,max(msgindex) from message ";
		sql += " where  status != " + MessageVo.STATUS_DEL;
		sql += " group by channelType,subjectid,subjectdomain,category order by msgid desc";
		List<MessageVo> list = cursor2Objects(db.rawQuery(sql, null));
		return list;
	}

	/**
	 * @param cursor
	 * @return
	 */
	private List<MessageVo> cursor2Objects(Cursor cursor) {
		ArrayList<MessageVo> list = new ArrayList<MessageVo>();
		try {
			while (cursor.moveToNext()) {
				MessageVo vo = new MessageVo();
				final int idIndex = cursor.getColumnIndexOrThrow("id");
				final int sourceIndex = cursor.getColumnIndexOrThrow("source");
				final int channeltypeIndex = cursor.getColumnIndexOrThrow("channeltype");
				final int msgidIndex = cursor.getColumnIndexOrThrow("msgid");
				final int fromidIndex = cursor.getColumnIndexOrThrow("fromid");
				final int fromdomainIndex = cursor.getColumnIndexOrThrow("fromdomain");
				final int toidIndex = cursor.getColumnIndexOrThrow("toid");
				final int todomainIndex = cursor.getColumnIndexOrThrow("todomain");
				final int subjectidIndex = cursor.getColumnIndexOrThrow("subjectid");
				final int subjectdomainIndex = cursor.getColumnIndexOrThrow("subjectdomain");
				final int categoryIndex = cursor.getColumnIndexOrThrow("category");
				final int summaryIndex = cursor.getColumnIndexOrThrow("summary");
				final int contenttypeIndex = cursor.getColumnIndexOrThrow("contenttype");
				final int contentIndex = cursor.getColumnIndexOrThrow("content");
				final int createtimeIndex = cursor.getColumnIndexOrThrow("createtime");
				final int positionIndex = cursor.getColumnIndexOrThrow("position");
				final int readstatusIndex = cursor.getColumnIndexOrThrow("readstatus");
				final int statusIndex = cursor.getColumnIndexOrThrow("status");
				final int forwardIdIndex = cursor.getColumnIndexOrThrow("forwardId");
				final int forwardTypeIndex = cursor.getColumnIndexOrThrow("forwardType");
				final int msgindexIndex = cursor.getColumnIndexOrThrow("msgindex");
				final int extIndex = cursor.getColumnIndexOrThrow("ext");
				final int notnotify = cursor.getColumnIndexOrThrow("notnotify");
				final int estimatetype = cursor.getColumnIndexOrThrow("estimatetype");
				final int estimateop = cursor.getColumnIndexOrThrow("estimateop");

				vo.setId(cursor.getInt(idIndex));
				vo.setSource(cursor.getInt(sourceIndex));
				vo.setChannelType(cursor.getString(channeltypeIndex));
				vo.setMsgId(cursor.getLong(msgidIndex));
				vo.setFromId(cursor.getLong(fromidIndex));
				vo.setFromDomain(cursor.getString(fromdomainIndex));
				vo.setToId(cursor.getLong(toidIndex));
				vo.setToDomain(cursor.getString(todomainIndex));
				vo.setSubjectId(cursor.getLong(subjectidIndex));
				vo.setSubjectDomain(cursor.getString(subjectdomainIndex));
				vo.setCategory(cursor.getString(categoryIndex));
				vo.setSummary(cursor.getString(summaryIndex));
				vo.setContentType(cursor.getInt(contenttypeIndex));
				vo.setContent(cursor.getString(contentIndex));
				vo.setCreateTime(cursor.getLong(createtimeIndex));
				vo.setPosition(cursor.getString(positionIndex));
				vo.setReadStatus(cursor.getInt(readstatusIndex));
				vo.setStatus(cursor.getInt(statusIndex));
				vo.setForwardId(cursor.getLong(forwardIdIndex));
				vo.setForwardType(cursor.getString(forwardTypeIndex));
				vo.setMsgIndex(cursor.getLong(msgindexIndex));
				vo.setExt(cursor.getString(extIndex));
				vo.setEstimateop(cursor.getInt(estimateop));
				vo.setEstimatetype(cursor.getInt(estimatetype));
				vo.setNotNotify(cursor.getInt(notnotify));
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
	 * com.iwgame.msgs.localdb.dao.MessageDao#getLastMessage(java.lang.String,
	 * long, java.lang.String, java.lang.String)
	 */
	@Override
	public MessageVo getLastMessage(String channelType, long subjectid, String subjectDomain, String category) {
		// TODO Auto-generated method stub
		if (db == null)
			return null;
		String sql = "";
		String selectionArgs[] = null;
		sql = "select * from " + TB_NAME;
		sql += " where channelType = ? ";
		sql += " and subjectid = ? ";
		sql += " and subjectdomain =? and category = ? ";
		sql += " order by msgindex desc ";
		sql += " limit 1";
		selectionArgs = new String[] { channelType, Long.toString(subjectid), subjectDomain, category };
		List<MessageVo> list = cursor2Objects(db.rawQuery(sql, selectionArgs));
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.MessageDao#getMessageByChannelTypeAndCategory
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public List<MessageVo> getMessageByChannelTypeAndCategory(String channelType, String category) {
		if (db == null)
			return null;
		String sql = "";
		String selectionArgs[] = null;
		sql = "select * from " + TB_NAME;
		sql += " where channelType = ? ";
		sql += " and category = ? ";
		selectionArgs = new String[] { channelType, category };
		return cursor2Objects(db.rawQuery(sql, selectionArgs));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.MessageDao#setMessageDelByChannelTypeAndCategory
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public int delByChannelTypeAndCategory(String channelType, String category, String content) {
		if (db == null)
			return -1;
		String strWhere = "channelType = ? and category = ? and content = ?";
		String[] arrwhere = new String[] { channelType, category, content };
		return db.delete(TB_NAME, strWhere, arrwhere);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iwgame.msgs.localdb.dao.MessageDao#insertMessages(java.util.List)
	 */
	@Override
	public int insertMessages(List<MessageVo> messages) {
		if (db == null)
			return -1;

		db.beginTransaction();
		try {
			int numValues = messages.size();
			for (int i = 0; i < numValues; i++) {
				insert(messages.get(i));
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
	 * @see com.iwgame.msgs.localdb.dao.MessageDao#delMessage(java.lang.String,
	 * java.lang.String, long, java.lang.String)
	 */
	@Override
	public int delMessageByPolicy(String channelType, String category, long subjectid, String subjectDomain) {
		if (db == null)
			return -1;
		MessageVo mvo = getLastMessage(channelType, subjectid, subjectDomain, category);
		if (mvo != null) {
			long maxMsgindex = mvo.getMsgIndex();
			long lave = 1000 - 10 * DateUtil.getLaveDay(mvo.getCreateTime());
			if (lave < 100)
				lave = 100;
			String strWhere = "channelType = ? and subjectid = ? and subjectdomain =? and category = ? and msgindex < ?";
			String[] arrwhere = new String[] { channelType, Long.toString(subjectid), subjectDomain, category, Long.toString(maxMsgindex - lave) };
			return db.delete(TB_NAME, strWhere, arrwhere);
		} else {
			return 0;
		}
	}

}
