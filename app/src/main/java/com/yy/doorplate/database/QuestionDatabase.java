package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.QuestionModel;

public class QuestionDatabase {

	private static final String TABLE = "Question";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "QuestionDatabase";

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
				+ "questionCode verchar(20) UNIQUE," + "question verchar(500),"
				+ "questionnaireCode verchar(20)," + "answerType verchar(20)"
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

	public synchronized List<QuestionModel> query_by_questionnaireCode(
			String questionnaireCode) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "questionnaireCode = ?",
					new String[] { questionnaireCode }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<QuestionModel> list = new ArrayList<QuestionModel>();
		while (cursor.moveToNext()) {
			QuestionModel model = new QuestionModel();
			model.questionCode = cursor.getString(1);
			model.question = cursor.getString(2);
			model.questionnaireCode = cursor.getString(3);
			model.answerType = cursor.getString(4);
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

	public synchronized QuestionModel query_by_code(String code) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "questionCode = ?",
					new String[] { code }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			QuestionModel model = new QuestionModel();
			model.questionCode = cursor.getString(1);
			model.question = cursor.getString(2);
			model.questionnaireCode = cursor.getString(3);
			model.answerType = cursor.getString(4);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(QuestionModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("questionCode", model.questionCode);
		values.put("question", model.question);
		values.put("questionnaireCode", model.questionnaireCode);
		values.put("answerType", model.answerType);
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
