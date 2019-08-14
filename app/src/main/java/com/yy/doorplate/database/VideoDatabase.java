package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.VideoInfoModel;

public class VideoDatabase {

	private static final String TABLE = "Video";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "VideoDatabase";

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
				+ "id verchar(10) UNIQUE," + "relaObjType verchar(10),"
				+ "relaObjValue verchar(50)," + "fileDesc verchar(100),"
				+ "fileName verchar(100)," + "resPath verchar(100),"
				+ "status verchar(5)," + "likeNum verchar(5)" + ")";
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

	public synchronized List<VideoInfoModel> query_all() {
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
		List<VideoInfoModel> list = new ArrayList<VideoInfoModel>();
		while (cursor.moveToNext()) {
			VideoInfoModel model = new VideoInfoModel();
			model.id = cursor.getString(0);
			model.relaObjType = cursor.getString(1);
			model.relaObjValue = cursor.getString(2);
			model.fileDesc = cursor.getString(3);
			model.fileName = cursor.getString(4);
			model.resPath = cursor.getString(5);
			model.status = cursor.getString(6);
			model.likeNum = cursor.getString(7);
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

	public synchronized List<VideoInfoModel> query(String selection,
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
		List<VideoInfoModel> list = new ArrayList<VideoInfoModel>();
		while (cursor.moveToNext()) {
			VideoInfoModel model = new VideoInfoModel();
			model.id = cursor.getString(0);
			model.relaObjType = cursor.getString(1);
			model.relaObjValue = cursor.getString(2);
			model.fileDesc = cursor.getString(3);
			model.fileName = cursor.getString(4);
			model.resPath = cursor.getString(5);
			model.status = cursor.getString(6);
			model.likeNum = cursor.getString(7);
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

	public synchronized void insert(VideoInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("relaObjType", model.relaObjType);
		values.put("relaObjValue", model.relaObjValue);
		values.put("fileDesc", model.fileDesc);
		values.put("fileName", model.fileName);
		values.put("resPath", model.resPath);
		values.put("status", model.status);
		values.put("likeNum", model.likeNum);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(VideoInfoModel model) {
		initDB();
		String whereClause = "id = '" + model.id + "'";

		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("relaObjType", model.relaObjType);
		values.put("relaObjValue", model.relaObjValue);
		values.put("fileDesc", model.fileDesc);
		values.put("fileName", model.fileName);
		values.put("resPath", model.resPath);
		values.put("status", model.status);
		values.put("likeNum", model.likeNum);
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

	public synchronized void delete_by_id(String id) {
		initDB();
		String sql = "delete from " + TABLE + " where id = '" + id + "'";
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
