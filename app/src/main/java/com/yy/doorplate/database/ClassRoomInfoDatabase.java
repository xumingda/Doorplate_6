package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ClassRoomInfoModel;

public class ClassRoomInfoDatabase {

	private static final String TABLE = "ClassRoomInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ClassRoomInfoDatabase";

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
				+ "classId verchar(20) UNIQUE," + "lx verchar(10),"
				+ "jssysdm verchar(20)," + "jssysmc verchar(50),"
				+ "xsrl verchar(5)," + "bjrl verchar(5),"
				+ "gxjgdm verchar(500)," + "gxjgmc verchar(500),"
				+ "remark verchar(500)," + "cdglyxm verchar(20),"
				+ "cdglygh verchar(20)," + "cdglydh verchar(20),"
				+ "cdglyzp verchar(100)," + "cdglykh verchar(20)" + ")";
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

	public synchronized ClassRoomInfoModel query() {
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
			ClassRoomInfoModel model = new ClassRoomInfoModel();
			model.id = cursor.getString(1);
			model.lx = cursor.getString(2);
			model.jssysdm = cursor.getString(3);
			model.jssysmc = cursor.getString(4);
			model.xsrl = cursor.getString(5);
			model.bjrl = cursor.getString(6);
			model.gxjgdm = cursor.getString(7);
			model.gxjgmc = cursor.getString(8);
			model.remark = cursor.getString(9);
			model.cdglyxm = cursor.getString(10);
			model.cdglygh = cursor.getString(11);
			model.cdglydh = cursor.getString(12);
			model.cdglyzp = cursor.getString(13);
			model.cdglykh = cursor.getString(14);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(ClassRoomInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("classId", model.id);
		values.put("lx", model.lx);
		values.put("jssysdm", model.jssysdm);
		values.put("jssysmc", model.jssysmc);
		values.put("xsrl", model.xsrl);
		values.put("bjrl", model.bjrl);
		values.put("gxjgdm", model.gxjgdm);
		values.put("gxjgmc", model.gxjgmc);
		values.put("remark", model.remark);
		values.put("cdglyxm", model.cdglyxm);
		values.put("cdglygh", model.cdglygh);
		values.put("cdglydh", model.cdglydh);
		values.put("cdglyzp", model.cdglyzp);
		values.put("cdglykh", model.cdglykh);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String id, ClassRoomInfoModel model) {
		initDB();
		String whereClause = "classId = " + id;

		ContentValues values = new ContentValues();
		values.put("classId", model.id);
		values.put("lx", model.lx);
		values.put("jssysdm", model.jssysdm);
		values.put("jssysmc", model.jssysmc);
		values.put("xsrl", model.xsrl);
		values.put("bjrl", model.bjrl);
		values.put("gxjgdm", model.gxjgdm);
		values.put("gxjgmc", model.gxjgmc);
		values.put("remark", model.remark);
		values.put("cdglyxm", model.cdglyxm);
		values.put("cdglygh", model.cdglygh);
		values.put("cdglydh", model.cdglydh);
		values.put("cdglyzp", model.cdglyzp);
		values.put("cdglykh", model.cdglykh);
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
