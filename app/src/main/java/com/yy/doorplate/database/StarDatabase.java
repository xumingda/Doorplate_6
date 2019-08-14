package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.StarModel;

public class StarDatabase {

	private static final String TABLE = "Star";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "StarDatabase";

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
				+ "id verchar(20) UNIQUE," + "starId verchar(20),"
				+ "starName verchar(100)," + "starType verchar(5),"
				+ "starIsCode verchar(20)," + "getStarPersonCode verchar(20),"
				+ "getStarPersonName verchar(20),"
				+ "awardsPerson verchar(20)," + "awardsUnit verchar(50),"
				+ "awardsData verchar(20)," + "iconUrl verchar(100),"
				+ "Info verchar(100)" + ")";
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

	public synchronized List<StarModel> query(String selection,
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
		List<StarModel> list = new ArrayList<StarModel>();
		while (cursor.moveToNext()) {
			StarModel model = new StarModel();
			model.id = cursor.getString(0);
			model.starId = cursor.getString(1);
			model.starName = cursor.getString(2);
			model.starType = cursor.getString(3);
			model.starIsCode = cursor.getString(4);
			model.getStarPersonCode = cursor.getString(5);
			try {
				model.getStarPersonName = cursor.getString(6);
			} catch (Exception e) {
				e.printStackTrace();
			}
			model.awardsPerson = cursor.getString(7);
			model.awardsUnit = cursor.getString(8);
			model.awardsData = cursor.getString(9);
			model.iconUrl = cursor.getString(10);
			model.Info = cursor.getString(11);
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

	public synchronized void insert(StarModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("starId", model.starId);
		values.put("starName", model.starName);
		values.put("starType", model.starType);
		values.put("starIsCode", model.starIsCode);
		values.put("getStarPersonCode", model.getStarPersonCode);
		values.put("awardsPerson", model.awardsPerson);
		values.put("awardsUnit", model.awardsUnit);
		values.put("awardsData", model.awardsData);
		values.put("iconUrl", model.iconUrl);
		values.put("Info", model.Info);
		try {
			values.put("getStarPersonName", model.getStarPersonName);
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
