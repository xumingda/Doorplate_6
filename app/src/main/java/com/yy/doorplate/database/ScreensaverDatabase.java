package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ScreensaverModel;

public class ScreensaverDatabase {

	private static final String TABLE = "Screensaver";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ScreensaverDatabase";

	private void openDB() {
		try {
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(
					MyApplication.DATABASE, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void create() {
		String sql = "create table if not exists " + TABLE + "("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "type verchar(10),"
				+ "url verchar(100)" + ")";
		try {
			sqLiteDatabase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initDB() {
		openDB();
		create();
	}

	public synchronized List<ScreensaverModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					null, null);
			if (cursor == null) {
				return null;
			}
			List<ScreensaverModel> list = new ArrayList<ScreensaverModel>();
			while (cursor.moveToNext()) {
				ScreensaverModel model = new ScreensaverModel();
				model.type = cursor.getString(1);
				model.url = cursor.getString(2);
				list.add(model);
			}
			close();
			if (cursor.getCount() == 0) {
				cursor.close();
				return null;
			} else {
				cursor.close();
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void insert(ScreensaverModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("type", model.type);
		values.put("url", model.url);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete() {
		initDB();
		String sql = "delete from " + TABLE;
		try {
			sqLiteDatabase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void drop() {
		initDB();
		String sql = "DROP TABLE " + TABLE;
		try {
			sqLiteDatabase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	private void close() {
		try {
			sqLiteDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
