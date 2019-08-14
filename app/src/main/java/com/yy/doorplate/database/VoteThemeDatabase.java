package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.VoteThemeModel;

public class VoteThemeDatabase {

	private static final String TABLE = "VoteTheme";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "VoteThemeDatabase";

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
				+ "voteThemeId verchar(20) UNIQUE," + "themeName verchar(100),"
				+ "themeIcon verchar(100)," + "themeValue verchar(5000),"
				+ "voteKinds verchar(5)," + "allowNum verchar(5),"
				+ "participationCount verchar(5),"
				+ "activityKind verchar(10)," + "endTime verchar(20),"
				+ "startTime verchar(20)," + "themeIconTitle verchar(100),"
				+ "belongTo verchar(5)" + ")";
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

	public synchronized VoteThemeModel query_by_id(String id) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "voteThemeId = ?",
					new String[] { id }, null, null, "startTime desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			VoteThemeModel model = new VoteThemeModel();
			model.voteThemeId = cursor.getString(1);
			model.themeName = cursor.getString(2);
			model.themeIcon = cursor.getString(3);
			model.themeValue = cursor.getString(4);
			model.voteKinds = cursor.getString(5);
			model.allowNum = cursor.getString(6);
			model.participationCount = cursor.getString(7);
			model.activityKind = cursor.getString(8);
			model.endTime = cursor.getString(9);
			model.startTime = cursor.getString(10);
			model.themeIconTitle = cursor.getString(11);
			model.belongTo = cursor.getString(12);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized List<VoteThemeModel> query(String selection,
			String[] selectionArgs) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, selection,
					selectionArgs, null, null, "startTime desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<VoteThemeModel> list = new ArrayList<VoteThemeModel>();
		while (cursor.moveToNext()) {
			VoteThemeModel model = new VoteThemeModel();
			model.voteThemeId = cursor.getString(1);
			model.themeName = cursor.getString(2);
			model.themeIcon = cursor.getString(3);
			model.themeValue = cursor.getString(4);
			model.voteKinds = cursor.getString(5);
			model.allowNum = cursor.getString(6);
			model.participationCount = cursor.getString(7);
			model.activityKind = cursor.getString(8);
			model.endTime = cursor.getString(9);
			model.startTime = cursor.getString(10);
			model.themeIconTitle = cursor.getString(11);
			model.belongTo = cursor.getString(12);
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

	public synchronized void insert(VoteThemeModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("voteThemeId", model.voteThemeId);
		values.put("themeName", model.themeName);
		values.put("themeIcon", model.themeIcon);
		values.put("themeValue", model.themeValue);
		values.put("voteKinds", model.voteKinds);
		values.put("allowNum", model.allowNum);
		values.put("participationCount", model.participationCount);
		values.put("activityKind", model.activityKind);
		values.put("endTime", model.endTime);
		values.put("startTime", model.startTime);
		values.put("themeIconTitle", model.themeIconTitle);
		values.put("belongTo", model.belongTo);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(VoteThemeModel model) {
		initDB();
		String whereClause = "voteThemeId = " + model.voteThemeId;

		ContentValues values = new ContentValues();
		values.put("voteThemeId", model.voteThemeId);
		values.put("themeName", model.themeName);
		values.put("themeIcon", model.themeIcon);
		values.put("themeValue", model.themeValue);
		values.put("voteKinds", model.voteKinds);
		values.put("allowNum", model.allowNum);
		values.put("participationCount", model.participationCount);
		values.put("activityKind", model.activityKind);
		values.put("endTime", model.endTime);
		values.put("startTime", model.startTime);
		values.put("themeIconTitle", model.themeIconTitle);
		values.put("belongTo", model.belongTo);
		try {
			sqLiteDatabase.update(TABLE, values, whereClause, null);
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

	public synchronized void delete_by_activityKind(String activityKind) {
		initDB();
		String sql = "delete from " + TABLE + " where activityKind = '"
				+ activityKind + "'";
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
