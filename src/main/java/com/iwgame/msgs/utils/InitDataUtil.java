/**      
 * InitDataUtil.java Create on 2014-3-3     
 *      
 * Copyright (c) 2014 by GreenShore Network
 * Company: 上海绿岸网络科技有限公司(Shanghai GreenShore Network Technology Co.,Ltd.)

 */

package com.iwgame.msgs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.iwgame.msgs.context.SystemContext;
import com.iwgame.msgs.localdb.MySQLiteOpenHelper;
import com.iwgame.utils.LogUtil;

/**
 * @ClassName: InitDataUtil
 * @Description: 初始化数据
 * @author 王卫
 * @date 2014-3-3 下午6:12:33
 * @Version 1.0
 * 
 */
public class InitDataUtil {

	private static final String TAG = "InitDataUtil";

	private static final String INIT_SQL = "game_sqlite.sql";
	private static final String INIT_SQL_GAME_PACKAGE = "game_package_sqlite.sql";

	public static void initData() {
		InputStream in;
		try {
			in = SystemContext.getInstance().getContext().getAssets().open(INIT_SQL);
			MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(SystemContext.getInstance().getContext());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			String sqlUpdate = null;
			SQLiteDatabase database = helper.getWritableDatabase();
			database.beginTransaction();
			try {
				while ((sqlUpdate = bufferedReader.readLine()) != null) {
					if (!TextUtils.isEmpty(sqlUpdate)) {
						database.execSQL(sqlUpdate);
					}
				}
				database.setTransactionSuccessful();
			} catch (Exception e) {
				LogUtil.e(TAG, String.format("--->初始化数据失败IOException %s", e.getMessage()));
			} finally {
				database.endTransaction();
			}
			bufferedReader.close();
			in.close();
		} catch (IOException e) {
			LogUtil.e(TAG, String.format("--->初始化数据失败IOException %s", e.getMessage()));
		}
	}
	
	/**
	 * 
	 */
	public static void initGamePackageData() {
		InputStream in;
		try {
			in = SystemContext.getInstance().getContext().getAssets().open(INIT_SQL_GAME_PACKAGE);
			MySQLiteOpenHelper helper = MySQLiteOpenHelper.getInstance(SystemContext.getInstance().getContext());
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			String sqlUpdate = null;
			SQLiteDatabase database = helper.getWritableDatabase();
			database.beginTransaction();
			try {
				while ((sqlUpdate = bufferedReader.readLine()) != null) {
					if (!TextUtils.isEmpty(sqlUpdate)) {
						database.execSQL(sqlUpdate);
					}
				}
				database.setTransactionSuccessful();
			} catch (Exception e) {
				LogUtil.e(TAG, String.format("--->初始化数据失败IOException %s", e.getMessage()));
			} finally {
				database.endTransaction();
			}
			bufferedReader.close();
			in.close();
		} catch (IOException e) {
			LogUtil.e(TAG, String.format("--->初始化数据失败IOException %s", e.getMessage()));
		}
	}
}
