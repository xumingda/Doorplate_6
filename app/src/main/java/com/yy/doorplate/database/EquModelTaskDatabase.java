package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.EquModelTaskModel;

public class EquModelTaskDatabase {

	private static final String TABLE = "EquModelTask";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "EquModelTaskDatabase";

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
				+ "startTime verchar(20)," + "stopTime verchar(20),"
				+ "equModel verchar(5)," + "screenModelCode verchar(20)" + ")";
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

	public synchronized List<EquModelTaskModel> query_all() {
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
		List<EquModelTaskModel> list = new ArrayList<EquModelTaskModel>();
		while (cursor.moveToNext()) {
			EquModelTaskModel model = new EquModelTaskModel();
			model.startTime = cursor.getString(1);
			model.stopTime = cursor.getString(2);
			model.equModel = cursor.getString(3);
			model.screenModelCode = cursor.getString(4);
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

	public synchronized void insert(EquModelTaskModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("startTime", model.startTime);
		values.put("stopTime", model.stopTime);
		values.put("equModel", model.equModel);
		values.put("screenModelCode", model.screenModelCode);
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

	private void close() {
		try {
			sqLiteDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
