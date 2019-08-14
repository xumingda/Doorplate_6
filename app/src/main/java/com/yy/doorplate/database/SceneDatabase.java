package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.SceneModel;

public class SceneDatabase {

	private static final String TABLE = "Scene";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "SceneDatabase";

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
				+ "name verchar(20) UNIQUE," + "img INTEGER" + ")";
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

	public synchronized List<SceneModel> query_all() {
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
		List<SceneModel> list = new ArrayList<SceneModel>();
		while (cursor.moveToNext()) {
			SceneModel model = new SceneModel();
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

	public synchronized SceneModel query_by_name(String name) {
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
			SceneModel model = new SceneModel();
			model.id = cursor.getInt(0);
			model.name = cursor.getString(1);
			model.img = cursor.getInt(2);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized SceneModel query_by_id(int id) {
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
			SceneModel model = new SceneModel();
			model.id = cursor.getInt(0);
			model.name = cursor.getString(1);
			model.img = cursor.getInt(2);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(SceneModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("name", model.name);
		values.put("img", model.img);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
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

	private void close() {
		try {
			sqLiteDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
