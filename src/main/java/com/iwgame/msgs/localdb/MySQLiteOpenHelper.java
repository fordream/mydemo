package com.iwgame.msgs.localdb;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.dao.DaoFactory;
import com.iwgame.msgs.localdb.dao.GroupDao;
import com.iwgame.msgs.localdb.dao.GroupUserRelDao;
import com.iwgame.msgs.vo.local.GroupUserRelVo;
import com.iwgame.msgs.vo.local.GroupVo;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String SYNC_KEY = "SYNC_KEY";
	private static final int DATABASE_VERSION = 19;

	private static MySQLiteOpenHelper instance;
	private SQLiteDatabase mDefaultWritableDatabase = null;

	public static MySQLiteOpenHelper getInstance(Context context) {
		synchronized (SYNC_KEY) {
			if (instance == null) {
				if (SystemContext.getInstance().getExtUserVo() != null) {
					String database_name = SystemContext.getInstance().getExtUserVo().getUserid() + "_msgs.db";
					instance = new MySQLiteOpenHelper(context, database_name);
				}
			}

		}
		return instance;
	}

	public static void clean() {
		if (instance != null)
			instance = null;
	}

	private MySQLiteOpenHelper(Context context, String database_name) {
		super(context, database_name, null, DATABASE_VERSION);
	}

	@Override
	public SQLiteDatabase getWritableDatabase() {
		final SQLiteDatabase db;
		if (mDefaultWritableDatabase != null) {
			db = mDefaultWritableDatabase;
		} else {
			db = super.getWritableDatabase();
		}
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.mDefaultWritableDatabase = db;
		db.execSQL(InitSql.V1_SQL_CREAT_TB_USER);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_GAME);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_GAMEPACKAGE);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_RELATIONGAME);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_CONTENT);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_AREA);
		String[] areaArray = InitSql.V1_SQL_CREAT_TB_AREA_INIT.split(";");
		for (int i = 0; i < areaArray.length; i++) {
			db.execSQL("insert into area values " + areaArray[i]);
		}
		db.execSQL(InitSql.V1_SQL_CREAT_TB_RESOURCE);

		db.execSQL(InitSql.V1_SQL_CREAT_TB_GROUP);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_GROPUREL);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_MESSAGE);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_ACTION);

		db.execSQL(InitSql.V2_SQL_CREAT_INDEX_MESSAGE);
		db.execSQL(InitSql.V3_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_FORWARDID);
		db.execSQL(InitSql.V3_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_FORWARDTYPE);

		db.execSQL(InitSql.V4_SQL_ALTER_TB_USER_ADD_COLUMN_MOBILE);
		db.execSQL(InitSql.V4_SQL_ALTER_TB_USER_ADD_COLUMN_MOBILENAME);
		db.execSQL(InitSql.V4_SQL_ALTER_TB_USER_ADD_COLUMN_WEIBO);
		db.execSQL(InitSql.V4_SQL_ALTER_TB_USER_ADD_COLUMN_WEIBONAME);

		db.execSQL(InitSql.V6_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_MSGINDEX);

		db.execSQL(InitSql.V8_SQL_ALTER_TB_USER_ADD_COLUMN_POINT);
		db.execSQL(InitSql.V8_SQL_ALTER_TB_GROUPS_ADD_COLUMN_GRADE);
		db.execSQL(InitSql.V8_SQL_ALTER_TB_GROUPS_ADD_COLUMN_POINT);
		db.execSQL(InitSql.V8_SQL_ALTER_TB_GROUPSUSERREL_ADD_COLUMN_CPOINT);

		db.execSQL(InitSql.V9_SQL_ALTER_TB_USER_ADD_COLUMN_REMARKNAME);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_USERGRADE);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_GROUPGRADE);
		db.execSQL(InitSql.V1_SQL_CREAT_TB_POINTTASK);

		db.execSQL(InitSql.V16_SQL_CREAT_TB_TOPICTAG);

		// db.execSQL(InitSql.V18_SQL_ALTER_TB_USER_ADD_COLUMN_EXP);
		// db.execSQL(InitSql.V18_SQL_ALTER_TB_USER_GRADE_ADD_COLUMN_EXP);
		// db.execSQL(InitSql.V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_EXP);

		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion > 1) {
			SystemContext.getInstance().setPointTaskSyncKey(0l);
			db.execSQL(InitSql.V18_SQL_ALTER_TB_DEL_POINTTASK_TABLE);
			db.execSQL(InitSql.V1_SQL_CREAT_TB_POINTTASK);
		}
		this.mDefaultWritableDatabase = db;
		switch (oldVersion) {
		case 1:
			db.execSQL(InitSql.V2_SQL_CREAT_INDEX_MESSAGE);
		case 2:
			db.execSQL(InitSql.V3_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_FORWARDID);
			db.execSQL(InitSql.V3_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_FORWARDTYPE);
		case 3:
			db.execSQL(InitSql.V4_SQL_ALTER_TB_USER_ADD_COLUMN_MOBILE);
			db.execSQL(InitSql.V4_SQL_ALTER_TB_USER_ADD_COLUMN_MOBILENAME);
			db.execSQL(InitSql.V4_SQL_ALTER_TB_USER_ADD_COLUMN_WEIBO);
			db.execSQL(InitSql.V4_SQL_ALTER_TB_USER_ADD_COLUMN_WEIBONAME);
		case 4:
			db.execSQL(InitSql.V5_SQL_UPDATE_TB_MESSAGE_UPDATE_DATA);
		case 5:
			db.execSQL(InitSql.V6_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_MSGINDEX);
		case 6:
			db.execSQL(InitSql.V7_SQL_DEL_ALL_MESSAGE);
		case 7:
			db.execSQL(InitSql.V8_SQL_ALTER_TB_USER_ADD_COLUMN_POINT);
			db.execSQL(InitSql.V8_SQL_ALTER_TB_GROUPS_ADD_COLUMN_GRADE);
			db.execSQL(InitSql.V8_SQL_ALTER_TB_GROUPS_ADD_COLUMN_POINT);
			db.execSQL(InitSql.V8_SQL_ALTER_TB_GROUPSUSERREL_ADD_COLUMN_CPOINT);
			db.execSQL(InitSql.V1_SQL_CREAT_TB_USERGRADE);
			db.execSQL(InitSql.V1_SQL_CREAT_TB_GROUPGRADE);
			db.execSQL(InitSql.V1_SQL_CREAT_TB_POINTTASK);
		case 8:
			db.execSQL(InitSql.V9_SQL_ALTER_TB_USER_ADD_COLUMN_REMARKNAME);
			db.execSQL(InitSql.V9_SQL_ALTER_TB_USER_ADD_COLUMN_ATIME);
		case 9:
			db.execSQL(InitSql.V10_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_DETAIL);
			db.execSQL(InitSql.V10_SQL_ALTER_TB_GROUPSUSERREL_ADD_COLUMN_ATIME);
		case 10:
			db.execSQL(InitSql.V11_SQL_alter_TB_USER_GRADE_ADD_COLUMN_MULTIPLE);
			db.execSQL(InitSql.V11_SQL_alter_TB_USER_GRADE_ADD_COLUMN_OPTIONS);
		case 11:
			db.execSQL(InitSql.V12_SQL_ALTER_TB_USER_GRADE_ADD_COLUMN_MULTIPLE);
		case 12:
			db.execSQL(InitSql.V13_SQL_ALTER_TB_GROUP_ADD_COLUMN_RELWITHGROUP);
			addColum();
		case 13:
			db.execSQL(InitSql.V14_SQL_ALTER_TB_GROUP_ADD_COLUMN_MAXCOUNT);
		case 14:
			db.execSQL(InitSql.V15_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_EXT);
		case 15:
			db.execSQL(InitSql.V16_SQL_CREAT_TB_TOPICTAG);
		case 16:
			db.execSQL(InitSql.V17_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_ESTIMATEOP);
			db.execSQL(InitSql.V17_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_ESTIMATETYPE);
			db.execSQL(InitSql.V17_SQL_ALTER_TB_MESSAGE_ADD_COLUMN_NOTNOTIFY);
		case 17:
			// db.execSQL(InitSql.V18_SQL_ALTER_TB_USER_ADD_COLUMN_EXP);
			// db.execSQL(InitSql.V18_SQL_ALTER_TB_USER_GRADE_ADD_COLUMN_EXP);
			// db.execSQL(InitSql.V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_EXP);
			db.execSQL(InitSql.V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_EXP);
			db.execSQL(InitSql.V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_EXPTIMES);
			db.execSQL(InitSql.V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_DETAILSBYTES);
			db.execSQL(InitSql.V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_GIDS);
			db.execSQL(InitSql.V18_SQL_ALTER_TB_POINTTASK_ADD_COLUMN_POSTCONTENT);
		case 18:
			db.execSQL(InitSql.V19_SQL_ALTER_TB_GROUP_ADD_COLUMN_SID);
			
		default:
			break;
		}
	}

	/**
	 * 获取到数据库里面 的所有公会列表 然后对公会添加一个属性
	 */
	private void addColum() {
		GroupDao groupDao = DaoFactory.getDaoFactory().getGroupDao(SystemContext.getInstance().getInstance().getContext());
		GroupUserRelDao groupUserRelDao = DaoFactory.getDaoFactory().getGroupUserRelDao(SystemContext.getInstance().getInstance().getContext());
		List<GroupVo> list = groupDao.findAllGroups();
		if (list == null || list.size() <= 0)
			return;
		GroupUserRelVo groupUserRelVo;
		GroupVo groupVo;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			groupVo = list.get(i);
			groupUserRelVo = groupUserRelDao.findUsers(groupVo.getGrid(), SystemContext.getInstance().getExtUserVo().getUserid());
			if (groupUserRelVo != null) {
				groupVo.setRelWithGroup(groupUserRelVo.getRel());
			}
		}
		groupDao.insertOrUpdate(list);
	}
}
