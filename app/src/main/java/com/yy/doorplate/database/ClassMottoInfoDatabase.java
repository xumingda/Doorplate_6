package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ClassMottoInfoModel;

public class ClassMottoInfoDatabase {

	private static final String TABLE = "ClassMottoInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ClassMottoInfoDatabase";

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
				+ "classId verchar(20) UNIQUE," + "bjdm verchar(20),"
				+ "bjmc verchar(50)," + "fdy verchar(50),"
				+ "fdyxm verchar(50)," + "classMotto verchar(50),"
				+ "Tagger verchar(500)," + "aphorism verchar(500),"
				+ "lifePhoto verchar(50)"+ ")";
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

	public synchronized ClassMottoInfoModel query() {
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
			ClassMottoInfoModel model = new ClassMottoInfoModel();
			model.id = cursor.getString(1);
			model.bjdm = cursor.getString(2);
			model.bjmc = cursor.getString(3);
			model.fdy = cursor.getString(4);
			model.fdyxm = cursor.getString(5);
			model.classMotto = cursor.getString(6);
			model.Tagger = cursor.getString(7);
			model.aphorism = cursor.getString(8);
			model.lifePhoto = cursor.getString(9);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(ClassMottoInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("classId", model.id);
		values.put("bjdm", model.bjdm);
		values.put("bjmc", model.bjmc);
		values.put("fdy", model.fdy);
		values.put("fdyxm", model.fdyxm);
		values.put("classMotto", model.classMotto);
		values.put("Tagger", model.Tagger);
		values.put("aphorism", model.aphorism);
		values.put("lifePhoto", model.lifePhoto);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String id, ClassMottoInfoModel model) {
		initDB();
		String whereClause = "classId = " + id;

		ContentValues values = new ContentValues();
		values.put("classId", model.id);
		values.put("bjdm", model.bjdm);
		values.put("bjmc", model.bjmc);
		values.put("fdy", model.fdy);
		values.put("fdyxm", model.fdyxm);
		values.put("classMotto", model.classMotto);
		values.put("Tagger", model.Tagger);
		values.put("aphorism", model.aphorism);
		values.put("lifePhoto", model.lifePhoto);
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
