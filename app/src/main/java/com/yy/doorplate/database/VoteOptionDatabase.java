package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.VoteOptionModel;

public class VoteOptionDatabase {

	private static final String TABLE = "VoteOption";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "VoteOptionDatabase";

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
				+ "voteOptionId verchar(20)," + "voteThemeId verchar(20),"
				+ "optionPic verchar(100)," + "optionTitle verchar(100),"
				+ "optionValue verchar(500)," + "optionVoteCount verchar(5)"
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

	public synchronized VoteOptionModel query_by_id(String id) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "voteOptionId = ?",
					new String[] { id }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			VoteOptionModel model = new VoteOptionModel();
			model.voteOptionId = cursor.getString(1);
			// model.voteThemeId = cursor.getString(2);
			model.optionPic = cursor.getString(3);
			model.optionTitle = cursor.getString(4);
			model.optionValue = cursor.getString(5);
			model.optionVoteCount = cursor.getString(6);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized List<VoteOptionModel> query_by_voteThemeId(
			String voteThemeId) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "voteThemeId = ?",
					new String[] { voteThemeId }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<VoteOptionModel> list = new ArrayList<VoteOptionModel>();
		while (cursor.moveToNext()) {
			VoteOptionModel model = new VoteOptionModel();
			model.voteOptionId = cursor.getString(1);
			// model.voteThemeId = cursor.getString(2);
			model.optionPic = cursor.getString(3);
			model.optionTitle = cursor.getString(4);
			model.optionValue = cursor.getString(5);
			model.optionVoteCount = cursor.getString(6);
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

	public synchronized void insert(VoteOptionModel model, String voteThemeId) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("voteOptionId", model.voteOptionId);
		values.put("voteThemeId", voteThemeId);
		values.put("optionPic", model.optionPic);
		values.put("optionTitle", model.optionTitle);
		values.put("optionValue", model.optionValue);
		values.put("optionVoteCount", model.optionVoteCount);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(VoteOptionModel model, String voteThemeId) {
		initDB();
		String whereClause = "voteOptionId = " + model.voteOptionId;

		ContentValues values = new ContentValues();
		values.put("voteOptionId", model.voteOptionId);
		values.put("voteThemeId", voteThemeId);
		values.put("optionPic", model.optionPic);
		values.put("optionTitle", model.optionTitle);
		values.put("optionValue", model.optionValue);
		values.put("optionVoteCount", model.optionVoteCount);
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
