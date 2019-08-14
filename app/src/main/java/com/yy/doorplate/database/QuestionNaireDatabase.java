package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.QuestionNaireModel;

public class QuestionNaireDatabase {

	private static final String TABLE = "QuestionNaire";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "QuestionNaireDatabase";

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
				+ "title verchar(100),"
				+ "questionnaireCode verchar(20) UNIQUE,"
				+ "questionnaireDesc verchar(100),"
				+ "questionnaireBelongKind verchar(5),"
				+ "questionnaireIconAddress verchar(1000),"
				+ "createTime verchar(20)" + ")";
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

	public synchronized List<QuestionNaireModel> query(String selection,
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
		List<QuestionNaireModel> list = new ArrayList<QuestionNaireModel>();
		while (cursor.moveToNext()) {
			QuestionNaireModel model = new QuestionNaireModel();
			model.title = cursor.getString(1);
			model.questionnaireCode = cursor.getString(2);
			model.questionnaireDesc = cursor.getString(3);
			try {
				model.questionnaireBelongKind = cursor.getString(4);
				model.questionnaireIconAddress = cursor.getString(5);
				model.createTime = cursor.getString(6);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	public synchronized QuestionNaireModel query_first() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			QuestionNaireModel model = new QuestionNaireModel();
			model.title = cursor.getString(1);
			model.questionnaireCode = cursor.getString(2);
			model.questionnaireDesc = cursor.getString(3);
			try {
				model.questionnaireBelongKind = cursor.getString(4);
				model.questionnaireIconAddress = cursor.getString(5);
				model.createTime = cursor.getString(6);
			} catch (Exception e) {
				e.printStackTrace();
			}
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized QuestionNaireModel query_by_code(String code) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "questionnaireCode = ?",
					new String[] { code }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			QuestionNaireModel model = new QuestionNaireModel();
			model.title = cursor.getString(1);
			model.questionnaireCode = cursor.getString(2);
			model.questionnaireDesc = cursor.getString(3);
			try {
				model.questionnaireBelongKind = cursor.getString(4);
				model.questionnaireIconAddress = cursor.getString(5);
				model.createTime = cursor.getString(6);
			} catch (Exception e) {
				e.printStackTrace();
			}
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized List<QuestionNaireModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<QuestionNaireModel> list = new ArrayList<QuestionNaireModel>();
		while (cursor.moveToNext()) {
			QuestionNaireModel model = new QuestionNaireModel();
			model.title = cursor.getString(1);
			model.questionnaireCode = cursor.getString(2);
			model.questionnaireDesc = cursor.getString(3);
			try {
				model.questionnaireBelongKind = cursor.getString(4);
				model.questionnaireIconAddress = cursor.getString(5);
				model.createTime = cursor.getString(6);
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	public synchronized void insert(QuestionNaireModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("title", model.title);
		values.put("questionnaireCode", model.questionnaireCode);
		values.put("questionnaireDesc", model.questionnaireDesc);
		try {
			values.put("questionnaireBelongKind", model.questionnaireBelongKind);
			values.put("questionnaireIconAddress",
					model.questionnaireIconAddress);
			values.put("createTime", model.createTime);
			sqLiteDatabase.insert(TABLE, null, values);
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
