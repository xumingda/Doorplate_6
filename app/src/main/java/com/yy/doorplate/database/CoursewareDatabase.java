package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.CoursewareModel;

public class CoursewareDatabase {

	private static final String TABLE = "Courseware";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "CoursewareDatabase";

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
				+ "id verchar(20) UNIQUE," + "skjhid verchar(20),"
				+ "fileName verchar(100)," + "filePath verchar(100)" + ")";
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

	public synchronized List<CoursewareModel> query_by_skjhid(String skjhid) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "skjhid = ?",
					new String[] { skjhid }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<CoursewareModel> list = new ArrayList<CoursewareModel>();
		while (cursor.moveToNext()) {
			CoursewareModel model = new CoursewareModel();
			model.id = cursor.getString(0);
			model.skjhid = cursor.getString(1);
			model.fileName = cursor.getString(2);
			model.filePath = cursor.getString(3);
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

	public synchronized void insert(CoursewareModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("skjhid", model.skjhid);
		values.put("fileName", model.fileName);
		values.put("filePath", model.filePath);
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

	public synchronized void delete_by_skjhid(String skjhid) {
		initDB();
		String sql = "delete from " + TABLE + " where skjhid = '" + skjhid
				+ "'";
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
