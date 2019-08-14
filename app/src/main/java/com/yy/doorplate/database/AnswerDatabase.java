package com.yy.doorplate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.AnswerModel;

public class AnswerDatabase {

	private static final String TABLE = "Answer";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "AnswerDatabase";

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
				+ "questionCode verchar(20)," + "answer verchar(500),"
				+ "replyPerson verchar(20)," + "questionnaireCode verchar(20),"
				+ "skjhid verchar(20)" + ")";
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

	public synchronized void insert(AnswerModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("questionCode", model.questionCode);
		values.put("answer", model.answer);
		values.put("replyPerson", model.replyPerson);
		try {
			values.put("questionnaireCode", model.questionnaireCode);
			values.put("skjhid", model.skjhid);
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String code, AnswerModel model) {
		initDB();
		String whereClause = "questionCode = " + code;

		ContentValues values = new ContentValues();
		values.put("questionCode", model.questionCode);
		values.put("answer", model.answer);
		values.put("replyPerson", model.replyPerson);
		try {
			values.put("questionnaireCode", model.questionnaireCode);
			values.put("skjhid", model.skjhid);
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
