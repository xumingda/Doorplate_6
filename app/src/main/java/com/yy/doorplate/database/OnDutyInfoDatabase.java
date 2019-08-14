package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.OnDutyInfoModel;

public class OnDutyInfoDatabase {

	private static final String TABLE = "OnDutyInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "OnDutyInfoDatabase";

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
				+ "jssysdm verchar(20)," + "bjdm verchar(20),"
				+ "date verchar(20)," + "xm verchar(50)," + "xssh verchar(50)"
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

	public synchronized List<OnDutyInfoModel> query_by_date(String date) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "date = ?",
					new String[] { date }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<OnDutyInfoModel> list = new ArrayList<OnDutyInfoModel>();
		while (cursor.moveToNext()) {
			OnDutyInfoModel model = new OnDutyInfoModel();
			model.jssysdm = cursor.getString(1);
			model.bjdm = cursor.getString(2);
			model.date = cursor.getString(3);
			model.xm = cursor.getString(4);
			model.xssh = cursor.getString(5);
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

	public synchronized void insert(OnDutyInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("jssysdm", model.jssysdm);
		values.put("bjdm", model.bjdm);
		values.put("date", model.date);
		values.put("xm", model.xm);
		values.put("xssh", model.xssh);
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
