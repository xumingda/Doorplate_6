package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ExaminationModel;

public class ExaminationDatabase {

	private static final String TABLE = "Examination";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ExaminationDatabase";

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
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "ksmc verchar(50),"
				+ "kcmc verchar(50)," + "ksrq verchar(20),"
				+ "ksjs verchar(20)," + "kskssj verchar(20),"
				+ "ksjssj verchar(20)," + "kskm verchar(50),"
				+ "jcls verchar(20)" + ")";
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

	public synchronized List<ExaminationModel> query_all() {
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
		List<ExaminationModel> list = new ArrayList<ExaminationModel>();
		while (cursor.moveToNext()) {
			ExaminationModel model = new ExaminationModel();
			model.ksmc = cursor.getString(1);
			model.kcmc = cursor.getString(2);
			model.ksrq = cursor.getString(3);
			model.ksjs = cursor.getString(4);
			model.kskssj = cursor.getString(5);
			model.ksjssj = cursor.getString(6);
			model.kskm = cursor.getString(7);
			model.jcls = cursor.getString(8);
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

	public synchronized List<ExaminationModel> query_by_ksrq(String ksrq) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "ksrq = ?",
					new String[] { ksrq }, null, null, "kskssj asc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<ExaminationModel> list = new ArrayList<ExaminationModel>();
		while (cursor.moveToNext()) {
			ExaminationModel model = new ExaminationModel();
			model.ksmc = cursor.getString(1);
			model.kcmc = cursor.getString(2);
			model.ksrq = cursor.getString(3);
			model.ksjs = cursor.getString(4);
			model.kskssj = cursor.getString(5);
			model.ksjssj = cursor.getString(6);
			model.kskm = cursor.getString(7);
			model.jcls = cursor.getString(8);
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

	public synchronized void insert(ExaminationModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("ksmc", model.ksmc);
		values.put("kcmc", model.kcmc);
		values.put("ksrq", model.ksrq);
		values.put("ksjs", model.ksjs);
		values.put("kskssj", model.kskssj);
		values.put("ksjssj", model.ksjssj);
		values.put("kskm", model.kskm);
		values.put("jcls", model.jcls);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
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

	private void close() {
		try {
			sqLiteDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
