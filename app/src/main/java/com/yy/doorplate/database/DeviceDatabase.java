package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.DeviceModel;

public class DeviceDatabase {

	private static final String TABLE = "Device";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "DeviceDatabase";

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
				+ "name verchar(20) UNIQUE," + "img INTEGER," + "state INTEGER"
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

	public synchronized List<DeviceModel> query_all() {
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
		List<DeviceModel> list = new ArrayList<DeviceModel>();
		while (cursor.moveToNext()) {
			DeviceModel model = new DeviceModel();
			model.id = cursor.getInt(0);
			model.name = cursor.getString(1);
			model.img = cursor.getInt(2);
			model.state = cursor.getInt(3);
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

	public synchronized List<DeviceModel> query_all_() {
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
		List<DeviceModel> list = new ArrayList<DeviceModel>();
		while (cursor.moveToNext()) {
			DeviceModel model = new DeviceModel();
			model.id = cursor.getInt(0);
			model.name = cursor.getString(1);
			model.img = cursor.getInt(2);
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

	public synchronized DeviceModel query_inspect() {
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
			DeviceModel model = new DeviceModel();
			model.id = cursor.getInt(0);
			model.name = cursor.getString(1);
			model.img = cursor.getInt(2);
			model.state = cursor.getInt(3);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized DeviceModel query_by_id(int id) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "id = ?",
					new String[] { id + "" }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			DeviceModel model = new DeviceModel();
			model.id = cursor.getInt(0);
			model.name = cursor.getString(1);
			model.img = cursor.getInt(2);
			model.state = cursor.getInt(3);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized DeviceModel query_by_name(String name) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "name = ?",
					new String[] { name }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			DeviceModel model = new DeviceModel();
			model.id = cursor.getInt(0);
			model.name = cursor.getString(1);
			model.img = cursor.getInt(2);
			model.state = cursor.getInt(3);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(DeviceModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("name", model.name);
		values.put("img", model.img);
		values.put("state", model.state);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(DeviceModel model) {
		initDB();
		String whereClause = "id = " + model.id;

		ContentValues values = new ContentValues();
		values.put("name", model.name);
		values.put("img", model.img);
		values.put("state", model.state);
		try {
			sqLiteDatabase.update(TABLE, values, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete(int id) {
		initDB();
		String sql = "delete from " + TABLE + " where id = " + id;
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
