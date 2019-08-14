package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.SceneAndDeviceModel;

public class SAndDDatabase {

	private static final String TABLE = "SAndD";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "SAndDDatabase";

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
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "sceneId INTEGER,"
				+ "deviceId INTEGER" + ")";
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

	public synchronized List<SceneAndDeviceModel> query_by_sceneId(int sceneId) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "sceneId = ?",
					new String[] { sceneId + "" }, null, null, "deviceId asc",
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<SceneAndDeviceModel> list = new ArrayList<SceneAndDeviceModel>();
		while (cursor.moveToNext()) {
			SceneAndDeviceModel model = new SceneAndDeviceModel();
			model.id = cursor.getInt(0);
			model.sceneId = cursor.getInt(1);
			model.deviceId = cursor.getInt(2);
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

	public synchronized SceneAndDeviceModel query_by_sd(int sceneId,
			int deviceId) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null,
					"sceneId = ? and deviceId = ?", new String[] {
							sceneId + "", deviceId + "" }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			SceneAndDeviceModel model = new SceneAndDeviceModel();
			model.id = cursor.getInt(0);
			model.sceneId = cursor.getInt(1);
			model.deviceId = cursor.getInt(2);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(SceneAndDeviceModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("sceneId", model.sceneId);
		values.put("deviceId", model.deviceId);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete_by_sceneId(int sceneId) {
		initDB();
		String sql = "delete from " + TABLE + " where sceneId = " + sceneId;
		try {
			sqLiteDatabase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete_by_deviceId(int deviceId) {
		initDB();
		String sql = "delete from " + TABLE + " where deviceId = " + deviceId;
		try {
			sqLiteDatabase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete(int sceneId, int deviceId) {
		initDB();
		String sql = "delete from " + TABLE + " where sceneId = " + sceneId
				+ " and deviceId = " + deviceId;
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
