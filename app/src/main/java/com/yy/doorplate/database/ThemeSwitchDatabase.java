package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ThemeSwitch;

public class ThemeSwitchDatabase {

	private static final String TABLE = "ThemeSwitch";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ThemeSwitchDatabase";

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
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "layer INTEGER,"
				+ "themeSwitchUrl verchar(100)" + ")";
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

	public synchronized List<ThemeSwitch> query_by_layer(String layer) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "layer = ?",
					new String[] { layer }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<ThemeSwitch> list = new ArrayList<ThemeSwitch>();
		while (cursor.moveToNext()) {
			ThemeSwitch model = new ThemeSwitch();
			model.id = cursor.getInt(0);
			model.layer = cursor.getInt(1);
			model.themeSwitchUrl = cursor.getString(2);
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

	public synchronized ThemeSwitch query_by_id(int id) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "id = ?",
					new String[] { id + "" }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			ThemeSwitch model = new ThemeSwitch();
			model.id = cursor.getInt(0);
			model.layer = cursor.getInt(1);
			model.themeSwitchUrl = cursor.getString(2);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(ThemeSwitch model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("layer", model.layer);
		values.put("themeSwitchUrl", model.themeSwitchUrl);
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
