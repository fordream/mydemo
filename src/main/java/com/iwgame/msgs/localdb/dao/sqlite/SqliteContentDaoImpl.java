package com.iwgame.msgs.localdb.dao.sqlite;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.msgs.localdb.dao.ContentDao;
import com.iwgame.msgs.vo.local.ContentVo;

public class SqliteContentDaoImpl implements ContentDao {
    private SQLiteDatabase db;
    private final String TAG_LOG = "SqliteContentDaoImpl";
    private final String[] TB_COLUMNS = new String[] { "id", "publishingid", "publishingtype", "contentid", "content", "type", "parentid", "parenttype", "parentpublishingid", "parentpublishingtype",
	    "ancestorid", "ancestortype", "ancestorpublishingid", "ancestorpublishingtype", "status", "createtime", "praisecount", "treadcount", "commentcount", "forwardcount", "allowpraise",
	    "allowtread", "allowcomment", "allowforward", "ispraise", "istread", "iscomment", "isforward" };

    private final String TB_NAME = "content";

    public SqliteContentDaoImpl(Context context) {
	MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(context);
	db = helper.getWritableDatabase();
    }

    @Override
    public ContentVo insert(ContentVo vo) {
	// TODO Auto-generated method stub
	ContentValues contentvalues = new ContentValues();
	contentvalues.put("publishingid", vo.getPublishingid());
	contentvalues.put("publishingtype", vo.getPublishingtype());
	contentvalues.put("contentid", vo.getContentid());
	contentvalues.put("content", vo.getContent());
	contentvalues.put("type", vo.getType());
	contentvalues.put("parentid", vo.getParentid());
	contentvalues.put("parenttype", vo.getParenttype());
	contentvalues.put("parentpublishingid", vo.getParentpublishingid());
	contentvalues.put("parentpublishingtype", vo.getParentpublishingtype());
	contentvalues.put("ancestorid", vo.getAncestorId());
	contentvalues.put("ancestortype", vo.getAncestorType());
	contentvalues.put("ancestorpublishingid", vo.getAncestorpublishingid());
	contentvalues.put("ancestorpublishingtype", vo.getAncestorpublishingtype());

	contentvalues.put("status", vo.getStatus());
	contentvalues.put("createtime", vo.getCreateTime());
	contentvalues.put("praisecount", vo.getPraiseCount());
	contentvalues.put("treadcount", vo.getTreadCount());
	contentvalues.put("commentcount", vo.getCommentCount());
	contentvalues.put("forwardcount", vo.getForwardCount());
	contentvalues.put("allowpraise", vo.getAllowPraise());
	contentvalues.put("allowtread", vo.getAllowTread());
	contentvalues.put("allowcomment", vo.getAllowComment());
	contentvalues.put("allowforward", vo.getAllowForward());
	contentvalues.put("ispraise", vo.getIsPraise());
	contentvalues.put("istread", vo.getIsTread());
	contentvalues.put("iscomment", vo.getIsComment());
	contentvalues.put("isforward", vo.getIsForward());

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
    public int updateById(ContentVo vo) {
	// TODO Auto-generated method stub
	ContentValues contentvalues = new ContentValues();
	contentvalues.put("publishingid", vo.getPublishingid());
	contentvalues.put("publishingtype", vo.getPublishingtype());
	contentvalues.put("contentid", vo.getContentid());
	contentvalues.put("content", vo.getContent());
	contentvalues.put("type", vo.getType());
	contentvalues.put("parentid", vo.getParentid());
	contentvalues.put("parenttype", vo.getParenttype());
	contentvalues.put("parentpublishingid", vo.getParentpublishingid());
	contentvalues.put("parentpublishingtype", vo.getParentpublishingtype());
	contentvalues.put("ancestorid", vo.getAncestorId());
	contentvalues.put("ancestortype", vo.getAncestorType());
	contentvalues.put("ancestorpublishingid", vo.getAncestorpublishingid());
	contentvalues.put("ancestorpublishingtype", vo.getAncestorpublishingtype());

	contentvalues.put("status", vo.getStatus());
	contentvalues.put("createtime", vo.getCreateTime());
	contentvalues.put("praisecount", vo.getPraiseCount());
	contentvalues.put("treadcount", vo.getTreadCount());
	contentvalues.put("commentcount", vo.getCommentCount());
	contentvalues.put("forwardcount", vo.getForwardCount());
	contentvalues.put("allowpraise", vo.getAllowPraise());
	contentvalues.put("allowtread", vo.getAllowTread());
	contentvalues.put("allowcomment", vo.getAllowComment());
	contentvalues.put("allowforward", vo.getAllowForward());
	contentvalues.put("ispraise", vo.getIsPraise());
	contentvalues.put("istread", vo.getIsTread());
	contentvalues.put("iscomment", vo.getIsComment());
	contentvalues.put("isforward", vo.getIsForward());

	String[] arr = new String[1];
	arr[0] = Long.toString(vo.getId());

	return db.update(TB_NAME, contentvalues, "id =? ", arr);
    }

    @Override
    public ContentVo getContent(int type, long contentid) {
	// TODO Auto-generated method stub
	ContentVo vo = null;
	String strwhere = null;
	String[] arrwhere = null;
	String strorder = null;
	String strhaving = null;
	String strgroup = null;
	strwhere = " type=?  and contentid = ?";
	arrwhere = new String[2];
	arrwhere[0] = Integer.toString(type);
	arrwhere[1] = Long.toString(contentid);

	ArrayList<ContentVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS, strwhere, arrwhere, strgroup, strhaving, strorder));
	if (list.size() > 0) {
	    vo = list.get(0);
	}
	return vo;
    }

    //
    // @Override
    // public ContentVo getContentById(long id) {
    // // TODO Auto-generated method stub
    // ContentVo vo = null;
    // String strwhere = null;
    // String[] arrwhere = null;
    // String strorder = null;
    // String strhaving = null;
    // String strgroup = null;
    // strwhere = " id = ?";
    // arrwhere = new String[1];
    // arrwhere[0] = Long.toString(id);
    //
    // ArrayList<ContentVo> list = cursor2Objects(db.query(TB_NAME, TB_COLUMNS,
    // strwhere, arrwhere, strgroup, strhaving, strorder));
    // if (list.size() > 0) {
    // vo = list.get(0);
    // }
    // return vo;
    // }

    private ArrayList<ContentVo> cursor2Objects(Cursor cursor) {

	ArrayList<ContentVo> list = new ArrayList<ContentVo>();
	try {
	    while (cursor.moveToNext()) {
		ContentVo vo = new ContentVo();
		final int idIndex = cursor.getColumnIndexOrThrow("id");
		final int publishingidIndex = cursor.getColumnIndexOrThrow("publishingid");
		final int publishingtypeIndex = cursor.getColumnIndexOrThrow("publishingtype");
		final int contentidIndex = cursor.getColumnIndexOrThrow("contentid");
		final int contentIndex = cursor.getColumnIndexOrThrow("content");
		final int typeIndex = cursor.getColumnIndexOrThrow("type");
		final int parentidIndex = cursor.getColumnIndexOrThrow("parentid");
		final int parenttypeIndex = cursor.getColumnIndexOrThrow("parenttype");
		final int parentpublishingidIndex = cursor.getColumnIndexOrThrow("parentpublishingid");
		final int parentpublishingtypeIndex = cursor.getColumnIndexOrThrow("parentpublishingtype");
		final int ancestoridIndex = cursor.getColumnIndexOrThrow("ancestorid");
		final int ancestortypeIndex = cursor.getColumnIndexOrThrow("ancestortype");
		final int ancestorpublishingidIndex = cursor.getColumnIndexOrThrow("ancestorpublishingid");
		final int ancestorpublishingtypeIndex = cursor.getColumnIndexOrThrow("ancestorpublishingtype");

		final int statusIndex = cursor.getColumnIndexOrThrow("status");
		final int createtimeIndex = cursor.getColumnIndexOrThrow("createtime");
		final int praisecountIndex = cursor.getColumnIndexOrThrow("praisecount");
		final int treadcountIndex = cursor.getColumnIndexOrThrow("treadcount");
		final int commentcountIndex = cursor.getColumnIndexOrThrow("commentcount");
		final int forwardcountIndex = cursor.getColumnIndexOrThrow("forwardcount");
		final int allowpraiseIndex = cursor.getColumnIndexOrThrow("allowpraise");
		final int allowtreadIndex = cursor.getColumnIndexOrThrow("allowtread");
		final int allowcommentIndex = cursor.getColumnIndexOrThrow("allowcomment");
		final int allowforwardIndex = cursor.getColumnIndexOrThrow("allowforward");
		final int ispraiseIndex = cursor.getColumnIndexOrThrow("ispraise");
		final int istreadIndex = cursor.getColumnIndexOrThrow("istread");
		final int iscommentIndex = cursor.getColumnIndexOrThrow("iscomment");
		final int isforwardIndex = cursor.getColumnIndexOrThrow("isforward");

		vo.setId(cursor.getLong(idIndex));
		vo.setPublishingid(cursor.getLong(publishingidIndex));
		vo.setPublishingtype(cursor.getString(publishingtypeIndex));
		vo.setContentid(cursor.getInt(contentidIndex));
		vo.setContent(cursor.getString(contentIndex));
		vo.setType(cursor.getInt(typeIndex));
		vo.setParentid(cursor.getLong(parentidIndex));
		vo.setParenttype(cursor.getInt(parenttypeIndex));
		vo.setParentpublishingid(cursor.getLong(parentpublishingidIndex));
		vo.setParentpublishingtype(cursor.getString(parentpublishingtypeIndex));
		vo.setAncestorId(cursor.getLong(ancestoridIndex));
		vo.setAncestorType(cursor.getInt(ancestortypeIndex));
		vo.setAncestorpublishingid(cursor.getLong(ancestorpublishingidIndex));
		vo.setAncestorpublishingtype(cursor.getString(ancestorpublishingtypeIndex));

		vo.setStatus(cursor.getInt(statusIndex));
		vo.setCreateTime(cursor.getLong(createtimeIndex));
		vo.setPraiseCount(cursor.getInt(praisecountIndex));
		vo.setTreadCount(cursor.getInt(treadcountIndex));
		vo.setCommentCount(cursor.getInt(commentcountIndex));
		vo.setForwardCount(cursor.getInt(forwardcountIndex));
		vo.setAllowPraise(cursor.getInt(allowpraiseIndex));
		vo.setAllowTread(cursor.getInt(allowtreadIndex));
		vo.setAllowComment(cursor.getInt(allowcommentIndex));
		vo.setAllowForward(cursor.getInt(allowforwardIndex));
		vo.setIsPraise(cursor.getInt(ispraiseIndex));
		vo.setIsTread(cursor.getInt(istreadIndex));
		vo.setIsComment(cursor.getInt(iscommentIndex));
		vo.setIsForward(cursor.getInt(isforwardIndex));

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
}
