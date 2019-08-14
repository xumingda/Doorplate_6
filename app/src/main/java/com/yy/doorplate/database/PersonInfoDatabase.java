package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.PersonInfoModel;

public class PersonInfoDatabase {

	private static final String TABLE = "PersonInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "PersonInfoDatabase";

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
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "xm verchar(20),"
				+ "js verchar(5)," + "rybh verchar(20) UNIQUE,"
				+ "xsbj verchar(20)," + "jsbm verchar(20),"
				+ "cardid verchar(20)" + ")";
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

	public synchronized List<PersonInfoModel> query_by_js(String js) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "js = ?",
					new String[] { js }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<PersonInfoModel> list = new ArrayList<PersonInfoModel>();
		while (cursor.moveToNext()) {
			PersonInfoModel model = new PersonInfoModel();
			model.xm = cursor.getString(1);
			model.js = cursor.getString(2);
			model.rybh = cursor.getString(3);
			model.xsbj = cursor.getString(4);
			model.jsbm = cursor.getString(5);
			model.cardid = cursor.getString(6);
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

	public synchronized PersonInfoModel query_by_rybh(String rybh) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "rybh = ?",
					new String[] { rybh }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			PersonInfoModel model = new PersonInfoModel();
			model.xm = cursor.getString(1);
			model.js = cursor.getString(2);
			model.rybh = cursor.getString(3);
			model.xsbj = cursor.getString(4);
			model.jsbm = cursor.getString(5);
			model.cardid = cursor.getString(6);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized PersonInfoModel query_by_cardid(String cardid) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "cardid = ?",
					new String[] { cardid }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			PersonInfoModel model = new PersonInfoModel();
			model.xm = cursor.getString(1);
			model.js = cursor.getString(2);
			model.rybh = cursor.getString(3);
			model.xsbj = cursor.getString(4);
			model.jsbm = cursor.getString(5);
			model.cardid = cursor.getString(6);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(PersonInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("xm", model.xm);
		values.put("js", model.js);
		values.put("rybh", model.rybh);
		values.put("xsbj", model.xsbj);
		values.put("jsbm", model.jsbm);
		values.put("cardid", model.cardid);
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
