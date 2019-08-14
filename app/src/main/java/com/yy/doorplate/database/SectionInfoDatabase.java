package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.SectionInfoModel;

public class SectionInfoDatabase {

	private static final String TABLE = "SectionInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "SectionInfoDatabase";

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
				+ "sectionId verchar(20) UNIQUE," + "sjlb verchar(10),"
				+ "jcdm verchar(10)," + "jcmc verchar(20),"
				+ "jcskkssj verchar(50)," + "jcskjssj verchar(50)" + ")";
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

	public synchronized List<SectionInfoModel> query_all() {
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
		List<SectionInfoModel> list = new ArrayList<SectionInfoModel>();
		while (cursor.moveToNext()) {
			SectionInfoModel model = new SectionInfoModel();
			model.id = cursor.getString(1);
			model.sjlb = cursor.getString(2);
			model.jcdm = cursor.getString(3);
			model.jcmc = cursor.getString(4);
			model.jcskkssj = cursor.getString(5);
			model.jcskjssj = cursor.getString(6);
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

	public synchronized SectionInfoModel query_by_jcdm(String jcdm) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "jcdm = ?",
					new String[] { jcdm }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			SectionInfoModel model = new SectionInfoModel();
			model.id = cursor.getString(1);
			model.sjlb = cursor.getString(2);
			model.jcdm = cursor.getString(3);
			model.jcmc = cursor.getString(4);
			model.jcskkssj = cursor.getString(5);
			model.jcskjssj = cursor.getString(6);
			cursor.close();
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(SectionInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("sectionId", model.id);
		values.put("sjlb", model.sjlb);
		values.put("jcdm", model.jcdm);
		values.put("jcmc", model.jcmc);
		values.put("jcskkssj", model.jcskkssj);
		values.put("jcskjssj", model.jcskjssj);
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
