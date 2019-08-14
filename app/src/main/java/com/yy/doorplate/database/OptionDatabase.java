package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.OptionModel;

public class OptionDatabase {

	private static final String TABLE = "Option";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "OptionDatabase";

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
				+ "questionCode verchar(20),"
				+ "optionCode verchar(20) UNIQUE," + "optionName verchar(500)"
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

	public synchronized OptionModel query_by_code(String code) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "optionCode = ?",
					new String[] { code }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			OptionModel model = new OptionModel();
			model.questionCode = cursor.getString(1);
			model.optionCode = cursor.getString(2);
			model.optionName = cursor.getString(3);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized List<OptionModel> query_by_questionCode(
			String questionCode) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "questionCode = ?",
					new String[] { questionCode }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<OptionModel> list = new ArrayList<OptionModel>();
		while (cursor.moveToNext()) {
			OptionModel model = new OptionModel();
			model.questionCode = cursor.getString(1);
			model.optionCode = cursor.getString(2);
			model.optionName = cursor.getString(3);
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

	public synchronized void insert(OptionModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("questionCode", model.questionCode);
		values.put("optionCode", model.optionCode);
		values.put("optionName", model.optionName);
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
