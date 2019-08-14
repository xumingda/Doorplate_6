package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ClockRuleModel;

public class ClockRuleDatabase {

	private static final String TABLE = "ClockRule";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ClockRuleDatabase";

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
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "sbkyxtq verchar(5)," + "xbkyxtq verchar(5),"
				+ "sbkyxyh verchar(5)," + "xbkyxyh verchar(5),"
				+ "jbddk verchar(5)," + "jbdxxdk verchar(5),"
				+ "xbbdk verchar(5)," + "yxcd verchar(5)," + "yzcd verchar(5),"
				+ "cgcd verchar(5)," + "dkgz verchar(5)," + "js verchar(5)"
				+ ")";
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

	public synchronized ClockRuleModel query_by_js(String js) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "js = ?",
					new String[] { js }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			ClockRuleModel model = new ClockRuleModel();
			model.sbkyxtq = cursor.getString(1);
			model.xbkyxtq = cursor.getString(2);
			model.sbkyxyh = cursor.getString(3);
			model.xbkyxyh = cursor.getString(4);
			model.jbddk = cursor.getString(5);
			model.jbdxxdk = cursor.getString(6);
			model.xbbdk = cursor.getString(7);
			model.yxcd = cursor.getString(8);
			model.yzcd = cursor.getString(9);
			model.cgcd = cursor.getString(10);
			model.dkgz = cursor.getString(11);
			model.js = cursor.getString(12);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(ClockRuleModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("sbkyxtq", model.sbkyxtq);
		values.put("xbkyxtq", model.xbkyxtq);
		values.put("sbkyxyh", model.sbkyxyh);
		values.put("xbkyxyh", model.xbkyxyh);
		values.put("jbddk", model.jbddk);
		values.put("jbdxxdk", model.jbdxxdk);
		values.put("xbbdk", model.xbbdk);
		values.put("yxcd", model.yxcd);
		values.put("yzcd", model.yzcd);
		values.put("cgcd", model.cgcd);
		values.put("dkgz", model.dkgz);
		values.put("js", model.js);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete_all() {
		initDB();
		String sql = "delete from " + TABLE;
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
