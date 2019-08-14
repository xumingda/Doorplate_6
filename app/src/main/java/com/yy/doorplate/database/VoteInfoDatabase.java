package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.VoteInfoModel;

public class VoteInfoDatabase {

	private static final String TABLE = "VoteInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "VoteInfoDatabase";

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
				+ "voteThemeId verchar(20)," + "voteOptionId verchar(20),"
				+ "rybh verchar(20)" + ")";
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

	public synchronized void insert(VoteInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("voteThemeId", model.voteThemeId);
		values.put("voteOptionId", model.voteOptionId);
		values.put("rybh", model.rybh);
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

	public synchronized void delete_by_voteThemeId(String voteThemeId) {
		initDB();
		String sql = "delete from " + TABLE + " where voteThemeId = '"
				+ voteThemeId + "'";
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
