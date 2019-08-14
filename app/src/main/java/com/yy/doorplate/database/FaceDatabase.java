package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.FaceModel;

public class FaceDatabase {

	private static final String TABLE = "Face";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "FaceDatabase";

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
				+ "faceId verchar(10)," + "rybh verchar(20),"
				+ "xm verchar(20)," + "js verchar(5)," + "cardid verchar(20)"
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

	public synchronized FaceModel query_by_faceId(String faceId) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "faceId = ?",
					new String[] { faceId }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			FaceModel model = new FaceModel();
			model.id = cursor.getInt(0);
			model.faceId = cursor.getString(1);
			model.rybh = cursor.getString(2);
			model.xm = cursor.getString(3);
			model.js = cursor.getString(4);
			model.cardid = cursor.getString(5);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized FaceModel query_by_rybh(String rybh) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "rybh = ?",
					new String[] { rybh }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			FaceModel model = new FaceModel();
			model.id = cursor.getInt(0);
			model.faceId = cursor.getString(1);
			model.rybh = cursor.getString(2);
			model.xm = cursor.getString(3);
			model.js = cursor.getString(4);
			model.cardid = cursor.getString(5);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized FaceModel query_by_cardid(String cardid) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "cardid = ?",
					new String[] { cardid }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			FaceModel model = new FaceModel();
			model.id = cursor.getInt(0);
			model.faceId = cursor.getString(1);
			model.rybh = cursor.getString(2);
			model.xm = cursor.getString(3);
			model.js = cursor.getString(4);
			model.cardid = cursor.getString(5);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(FaceModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("faceId", model.faceId);
		values.put("rybh", model.rybh);
		values.put("xm", model.xm);
		values.put("js", model.js);
		values.put("cardid", model.cardid);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(FaceModel model) {
		initDB();
		String whereClause = "rybh = '" + model.rybh + "'";

		ContentValues values = new ContentValues();
		values.put("faceId", model.faceId);
		values.put("rybh", model.rybh);
		values.put("xm", model.xm);
		values.put("js", model.js);
		values.put("cardid", model.cardid);
		try {
			sqLiteDatabase.update(TABLE, values, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete() {
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
