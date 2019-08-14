package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.XyxxTypeModel;

public class XyxxDatabase {

	private static final String TABLE = "Xyxx";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "XyxxDatabase";

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
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "bsdm verchar(20),"
				+ "xymc verchar(100)," + "xsdazdhzxx verchar(1000)" + ")";
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

	public synchronized XyxxTypeModel query() {
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
			XyxxTypeModel model = new XyxxTypeModel();
			model.bsdm = cursor.getString(1);
			model.xymc = cursor.getString(2);
			model.xsdazdhzxx = cursor.getString(3);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(XyxxTypeModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("bsdm", model.bsdm);
		values.put("xymc", model.xymc);
		values.put("xsdazdhzxx", model.xsdazdhzxx);
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
