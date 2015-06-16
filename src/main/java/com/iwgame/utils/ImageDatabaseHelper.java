package com.iwgame.utils;

import java.io.File;

import com.iwgame.msgs.utils.ImageFile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ImageDatabaseHelper {

	public static final String TABLE_IMAGE = "image";
	public static final String COLUMN_IMAGE_URL = "url";
	public static final String COLUMN_IMAGE_DATA = "data";

	private static ImageDatabaseHelper instance;
	private static DBOpenHelper dbOpenHelper;

	public static synchronized ImageDatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new ImageDatabaseHelper(context);
		}
		return instance;
	}

	private ImageDatabaseHelper(Context context) {
		dbOpenHelper = DBOpenHelper.getInstance(context);
	}

	public String query(String url) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery("select " + COLUMN_IMAGE_DATA + " from "
				+ TABLE_IMAGE + " where " + COLUMN_IMAGE_URL + "=?",
				new String[] { url });

		String data = null;
		try {
			if (cursor.moveToFirst()) {
				data = cursor.getString(0);
			}
		} finally {
			cursor.close();
			// db.close();
		}
		return data;
	}

	public void delete(String url) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete(TABLE_IMAGE, COLUMN_IMAGE_URL + "=?", new String[] { url });

		// db.close();
	}

	public synchronized void save(String url, String data) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COLUMN_IMAGE_URL, url);
		values.put(COLUMN_IMAGE_DATA, data);

		try {
			db.insert(TABLE_IMAGE, COLUMN_IMAGE_URL, values);
		} finally {
			// db.close();
		}
	}

	public void deleteAll() {

		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.delete(TABLE_IMAGE, null, null);

		// db.close();

	}

	public static void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		dropTable(db);
		createTable(db);
	}

	private static void createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_IMAGE
				+ " (_id integer primary key autoincrement, "
				+ COLUMN_IMAGE_URL + " TEXT, " + COLUMN_IMAGE_DATA + " TEXT);");
	}

	private static void dropTable(SQLiteDatabase db) {
		delAllFile(ImageFile.DIR);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
	}

	public static void delAllFile(String file) {
		File dir = new File(file);
		if (!dir.exists()) {
			return;
		}

		if (dir.isFile()) {
			dir.delete();
			return;
		}

		String[] tempList = dir.list();
		File temp = null;
		for (String name : tempList) {
			temp = new File(dir, name);

			if (temp.isFile()) {
				temp.delete();
			} else if (temp.isDirectory()) {
				delAllFile(temp.getAbsolutePath());

				temp.delete();
			}
		}
	}
}
