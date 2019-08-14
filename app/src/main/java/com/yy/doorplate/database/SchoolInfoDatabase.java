package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.SchoolInfoModel;

public class SchoolInfoDatabase {

	private static final String TABLE = "SchoolInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "SchoolInfoDatabase";

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
				+ "infoName verchar(20)," + "infoCode verchar(50) UNIQUE,"
				+ "content verchar(5000)" + ")";
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

	public synchronized List<SchoolInfoModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<SchoolInfoModel> list = new ArrayList<SchoolInfoModel>();
		while (cursor.moveToNext()) {
			SchoolInfoModel model = new SchoolInfoModel();
			model.infoName = cursor.getString(1);
			model.infoCode = cursor.getString(2);
			model.content = cursor.getString(3);
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
	}

	public synchronized SchoolInfoModel query_by_infoCode(String infoCode) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "infoCode = ?",
					new String[] { infoCode }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			SchoolInfoModel model = new SchoolInfoModel();
			model.infoName = cursor.getString(1);
			model.infoCode = cursor.getString(2);
			model.content = cursor.getString(3);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(SchoolInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("infoName", model.infoName);
		values.put("infoCode", model.infoCode);
		values.put("content", model.content);
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
