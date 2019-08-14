package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.PicDreOrActivityModel;

public class PicDreOrActivityDatabase {

	private static final String TABLE = "PicDreOrActivity";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "PicDreOrActivityDatabase";

	private void openDB() {
		try {
			sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(MyApplication.DATABASE, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void create() {
		String sql = "create table if not exists " + TABLE + "(" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "objectId verchar(20) UNIQUE," + "parentId verchar(20)," + "objectType verchar(5),"
				+ "objectName verchar(100)," + "objectComment verchar(5000)," + "objectIcon verchar(500),"
				+ "objectEntityAddress verchar(500)," + "objectPoint verchar(5)," + "objectHasCount verchar(5),"
				+ "kind verchar(5)," + "createData verchar(20)" + ")";
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

	public synchronized String queryObjectIdbyObjectName(String photos, String kind) {
		initDB();
		String objectId = "";
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "objectName=? and kind = ? ", new String[] { photos, kind },
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return "";
		}
		if (cursor.moveToFirst()) {
			objectId = cursor.getString(1);
		}

		if (cursor != null) {
			cursor.close();
		}
		close();
		return objectId;
	}

	public synchronized List<PicDreOrActivityModel> query(String selection, String[] selectionArgs) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, selection, selectionArgs, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}

		List<PicDreOrActivityModel> list = new ArrayList<PicDreOrActivityModel>();
		while (cursor.moveToNext()) {
			PicDreOrActivityModel model = new PicDreOrActivityModel();
			model.objectId = cursor.getString(1);
			model.parentId = cursor.getString(2);
			model.objectType = cursor.getString(3);
			model.objectName = cursor.getString(4);
			model.objectComment = cursor.getString(5);
			model.objectIcon = cursor.getString(6);
			model.objectEntityAddress = cursor.getString(7);
			model.objectPoint = cursor.getString(8);
			model.objectHasCount = cursor.getString(9);
			model.kind = cursor.getString(10);
			model.createData = cursor.getString(11);
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

	public synchronized void insert(PicDreOrActivityModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("objectId", model.objectId);
		values.put("parentId", model.parentId);
		values.put("objectType", model.objectType);
		values.put("objectName", model.objectName);
		values.put("objectComment", model.objectComment);
		values.put("objectIcon", model.objectIcon);
		values.put("objectEntityAddress", model.objectEntityAddress);
		values.put("objectPoint", model.objectPoint);
		values.put("objectHasCount", model.objectHasCount);
		values.put("kind", model.kind);
		values.put("createData", model.createData);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(PicDreOrActivityModel model) {
		initDB();
		String whereClause = "objectId = '" + model.objectId + "'";

		ContentValues values = new ContentValues();
		values.put("objectId", model.objectId);
		values.put("parentId", model.parentId);
		values.put("objectType", model.objectType);
		values.put("objectName", model.objectName);
		values.put("objectComment", model.objectComment);
		values.put("objectIcon", model.objectIcon);
		values.put("objectEntityAddress", model.objectEntityAddress);
		values.put("objectPoint", model.objectPoint);
		values.put("objectHasCount", model.objectHasCount);
		values.put("kind", model.kind);
		values.put("createData", model.createData);
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

	private void close() {
		try {
			sqLiteDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
