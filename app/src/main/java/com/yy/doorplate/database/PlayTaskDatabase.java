package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.PlayTaskModel;

public class PlayTaskDatabase {

	private static final String TABLE = "PlayTask";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "PlayTaskDatabase";

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
				+ "taskId verchar(20) UNIQUE," + "taskName verchar(50),"
				+ "playStartDate verchar(20)," + "playEndDate verchar(20),"
				+ "playStartTime verchar(20)," + "playEndTime verchar(20),"
				+ "srcPath verchar(50)," + "srcType verchar(10),"
				+ "fileType verchar(10)," + "taskType verchar(10),"
				+ "playType verchar(10)," + "taskDesc verchar(200),"
				+ "position verchar(5)" + ")";
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

	public synchronized List<PlayTaskModel> query(String selection,
			String[] selectionArgs) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, selection,
					selectionArgs, null, null, "taskId desc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<PlayTaskModel> list = new ArrayList<PlayTaskModel>();
		while (cursor.moveToNext()) {
			PlayTaskModel model = new PlayTaskModel();
			model.taskId = cursor.getString(1);
			model.taskName = cursor.getString(2);
			model.playStartDate = cursor.getString(3);
			model.playEndDate = cursor.getString(4);
			model.playStartTime = cursor.getString(5);
			model.playEndTime = cursor.getString(6);
			model.srcPath = cursor.getString(7);
			model.srcType = cursor.getString(8);
			model.fileType = cursor.getString(9);
			model.taskType = cursor.getString(10);
			model.playType = cursor.getString(11);
			model.taskDesc = cursor.getString(12);
			model.position = cursor.getString(13);
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

	public synchronized List<PlayTaskModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					"taskId desc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<PlayTaskModel> list = new ArrayList<PlayTaskModel>();
		while (cursor.moveToNext()) {
			PlayTaskModel model = new PlayTaskModel();
			model.taskId = cursor.getString(1);
			model.taskName = cursor.getString(2);
			model.playStartDate = cursor.getString(3);
			model.playEndDate = cursor.getString(4);
			model.playStartTime = cursor.getString(5);
			model.playEndTime = cursor.getString(6);
			model.srcPath = cursor.getString(7);
			model.srcType = cursor.getString(8);
			model.fileType = cursor.getString(9);
			model.taskType = cursor.getString(10);
			model.playType = cursor.getString(11);
			model.taskDesc = cursor.getString(12);
			model.position = cursor.getString(13);
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

	public synchronized List<PlayTaskModel> query_by_playType(String playType) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "playType = ?",
					new String[] { playType }, null, null, "taskId desc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<PlayTaskModel> list = new ArrayList<PlayTaskModel>();
		while (cursor.moveToNext()) {
			PlayTaskModel model = new PlayTaskModel();
			model.taskId = cursor.getString(1);
			model.taskName = cursor.getString(2);
			model.playStartDate = cursor.getString(3);
			model.playEndDate = cursor.getString(4);
			model.playStartTime = cursor.getString(5);
			model.playEndTime = cursor.getString(6);
			model.srcPath = cursor.getString(7);
			model.srcType = cursor.getString(8);
			model.fileType = cursor.getString(9);
			model.taskType = cursor.getString(10);
			model.playType = cursor.getString(11);
			model.taskDesc = cursor.getString(12);
			model.position = cursor.getString(13);
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

	public synchronized void insert(PlayTaskModel model) {
		Log.d(TAG, model.toString());
		initDB();
		ContentValues values = new ContentValues();
		values.put("taskId", model.taskId);
		values.put("taskName", model.taskName);
		values.put("playStartDate", model.playStartDate);
		values.put("playEndDate", model.playEndDate);
		values.put("playStartTime", model.playStartTime);
		values.put("playEndTime", model.playEndTime);
		values.put("srcPath", model.srcPath);
		values.put("srcType", model.srcType);
		values.put("fileType", model.fileType);
		values.put("taskType", model.taskType);
		values.put("playType", model.playType);
		values.put("taskDesc", model.taskDesc);
		values.put("position", model.position);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete(String taskId) {
		initDB();
		String sql = "delete from " + TABLE + " where taskId = '" + taskId
				+ "'";
		try {
			sqLiteDatabase.execSQL(sql);
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
