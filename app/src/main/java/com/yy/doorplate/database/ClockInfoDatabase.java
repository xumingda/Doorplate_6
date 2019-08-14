package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ClockInfoModel;

public class ClockInfoDatabase {

	private static final String TABLE = "ClockInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ClockInfoDatabase";

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
				+ "id verchar(20) UNIQUE," + "xm verchar(20),"
				+ "js verchar(5)," + "rybh verchar(20)," + "dkrq verchar(20),"
				+ "dksj verchar(20)," + "dklx verchar(5),"
				+ "isupload verchar(5)" + ")";
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

	public synchronized List<ClockInfoModel> query_by_isupload(String isupload) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "isupload = ?",
					new String[] { isupload }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<ClockInfoModel> list = new ArrayList<ClockInfoModel>();
		while (cursor.moveToNext()) {
			ClockInfoModel model = new ClockInfoModel();
			model.id = cursor.getString(0);
			model.xm = cursor.getString(1);
			model.js = cursor.getString(2);
			model.rybh = cursor.getString(3);
			model.dkrq = cursor.getString(4);
			model.dksj = cursor.getString(5);
			model.dklx = cursor.getString(6);
			model.isupload = cursor.getString(7);
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

	public synchronized void insert(ClockInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("xm", model.xm);
		values.put("js", model.js);
		values.put("rybh", model.rybh);
		values.put("dkrq", model.dkrq);
		values.put("dksj", model.dksj);
		values.put("dklx", model.dklx);
		values.put("isupload", model.isupload);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(ClockInfoModel model) {
		initDB();
		String whereClause = "id = " + model.id;

		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("xm", model.xm);
		values.put("js", model.js);
		values.put("rybh", model.rybh);
		values.put("dkrq", model.dkrq);
		values.put("dksj", model.dksj);
		values.put("dklx", model.dklx);
		values.put("isupload", model.isupload);
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
