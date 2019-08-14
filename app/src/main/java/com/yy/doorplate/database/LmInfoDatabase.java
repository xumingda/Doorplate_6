package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.LmInfoModel;

public class LmInfoDatabase {

	private static final String TABLE = "LmInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "LmInfoDatabase";

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
				+ "id verchar(20)," + "lmdm verchar(10)," + "lmmc verchar(20),"
				+ "xxztlb verchar(10)," + "xxztlbmc verchar(20),"
				+ "sjlx verchar(5)," + "lmlj verchar(100)" + ")";
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

	public synchronized List<LmInfoModel> query(String selection,
			String[] selectionArgs) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, selection,
					selectionArgs, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<LmInfoModel> list = new ArrayList<LmInfoModel>();
		while (cursor.moveToNext()) {
			LmInfoModel model = new LmInfoModel();
			model.id = cursor.getString(0);
			model.lmdm = cursor.getString(1);
			model.lmmc = cursor.getString(2);
			model.xxztlb = cursor.getString(3);
			model.xxztlbmc = cursor.getString(4);
			model.sjlx = cursor.getString(5);
			model.lmlj = cursor.getString(6);
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

	public synchronized void insert(LmInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("lmdm", model.lmdm);
		values.put("lmmc", model.lmmc);
		values.put("xxztlb", model.xxztlb);
		values.put("xxztlbmc", model.xxztlbmc);
		values.put("sjlx", model.sjlx);
		values.put("lmlj", model.lmlj);
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
