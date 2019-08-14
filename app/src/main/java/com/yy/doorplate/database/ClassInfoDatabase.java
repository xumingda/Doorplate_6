package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ClassInfoModel;

public class ClassInfoDatabase {

	private static final String TABLE = "ClassInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ClassInfoDatabase";

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
				+ "classInfoId verchar(20) UNIQUE," + "ssxy verchar(50),"
				+ "ssxq verchar(50)," + "ssxqmc verchar(50),"
				+ "ssxb verchar(20)," + "ssxbmc verchar(50),"
				+ "sszy verchar(20)," + "sszymc verchar(50),"
				+ "bjdm verchar(20)," + "bjmc verchar(50)," + "nj verchar(10),"
				+ "xz verchar(10)," + "xzshow verchar(10),"
				+ "bjzxrs verchar(10)," + "bjsjzxrs verchar(10),"
				+ "fdy verchar(20)," + "fdyxm verchar(20),"
				+ "fdydh verchar(20)," + "fdyzp verchar(100)" + ")";
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

	public synchronized ClassInfoModel query() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			ClassInfoModel model = new ClassInfoModel();
			model.id = cursor.getString(1);
			model.ssxy = cursor.getString(2);
			model.ssxq = cursor.getString(3);
			model.ssxqmc = cursor.getString(4);
			model.ssxb = cursor.getString(5);
			model.ssxbmc = cursor.getString(6);
			model.sszy = cursor.getString(7);
			model.sszymc = cursor.getString(8);
			model.bjdm = cursor.getString(9);
			model.bjmc = cursor.getString(10);
			model.nj = cursor.getString(11);
			model.xz = cursor.getString(12);
			model.xzshow = cursor.getString(13);
			model.bjzxrs = cursor.getString(14);
			model.bjsjzxrs = cursor.getString(15);
			model.fdy = cursor.getString(16);
			model.fdyxm = cursor.getString(17);
			model.fdydh = cursor.getString(18);
			model.fdyzp = cursor.getString(19);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(ClassInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("classInfoId", model.id);
		values.put("ssxy", model.ssxy);
		values.put("ssxq", model.ssxq);
		values.put("ssxqmc", model.ssxqmc);
		values.put("ssxb", model.ssxb);
		values.put("ssxbmc", model.ssxbmc);
		values.put("sszy", model.sszy);
		values.put("sszymc", model.sszymc);
		values.put("bjdm", model.bjdm);
		values.put("bjmc", model.bjmc);
		values.put("nj", model.nj);
		values.put("xz", model.xz);
		values.put("xzshow", model.xzshow);
		values.put("bjzxrs", model.bjzxrs);
		values.put("bjsjzxrs", model.bjsjzxrs);
		values.put("fdy", model.fdy);
		values.put("fdyxm", model.fdyxm);
		values.put("fdydh", model.fdydh);
		values.put("fdyzp", model.fdyzp);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String bjdm, ClassInfoModel model) {
		initDB();
		String whereClause = "bjdm = " + bjdm;

		ContentValues values = new ContentValues();
		values.put("classInfoId", model.id);
		values.put("ssxy", model.ssxy);
		values.put("ssxq", model.ssxq);
		values.put("ssxqmc", model.ssxqmc);
		values.put("ssxb", model.ssxb);
		values.put("ssxbmc", model.ssxbmc);
		values.put("sszy", model.sszy);
		values.put("sszymc", model.sszymc);
		values.put("bjdm", model.bjdm);
		values.put("bjmc", model.bjmc);
		values.put("nj", model.nj);
		values.put("xz", model.xz);
		values.put("xzshow", model.xzshow);
		values.put("bjzxrs", model.bjzxrs);
		values.put("bjsjzxrs", model.bjsjzxrs);
		values.put("fdy", model.fdy);
		values.put("fdyxm", model.fdyxm);
		values.put("fdydh", model.fdydh);
		values.put("fdyzp", model.fdyzp);
		try {
			sqLiteDatabase.update(TABLE, values, whereClause, null);
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

	private void close() {
		try {
			sqLiteDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
