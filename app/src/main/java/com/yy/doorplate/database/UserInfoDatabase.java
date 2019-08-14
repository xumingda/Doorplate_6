package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.UserInfoModel;

public class UserInfoDatabase {

	private static final String TABLE = "UserInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "UserInfoDatabase";

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
				+ "userName verchar(50)," + "password verchar(50),"
				+ "realName verchar(50)," + "cardNo verchar(50)" + ")";
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

	public synchronized UserInfoModel query_by_userName(String userName,
			String password) {
		password = MyApplication.getMd5ByString(password);
		Log.d(TAG, "password = " + password);
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null,
					"userName = ? and password = ?", new String[] { userName,
							password }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			UserInfoModel model = new UserInfoModel();
			model.userName = cursor.getString(1);
			model.password = cursor.getString(2);
			model.realName = cursor.getString(3);
			model.cardNo = cursor.getString(4);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized UserInfoModel query_by_cardNo(String cardNo) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "cardNo = ?",
					new String[] { cardNo }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			UserInfoModel model = new UserInfoModel();
			model.userName = cursor.getString(1);
			model.password = cursor.getString(2);
			model.realName = cursor.getString(3);
			model.cardNo = cursor.getString(4);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(UserInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("userName", model.userName);
		values.put("password", model.password);
		values.put("realName", model.realName);
		values.put("cardNo", model.cardNo);
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
