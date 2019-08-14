package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.PageLayoutModel;

public class PageLayoutDatabase {

	private static final String TABLE = "PageLayout";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "PageLayoutDatabase";

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
				+ "pageCode verchar(10)," + "resPath verchar(50),"
				+ "pageName verchar(20)" + ")";
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

	public synchronized List<PageLayoutModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					"id desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<PageLayoutModel> list = new ArrayList<PageLayoutModel>();
		while (cursor.moveToNext()) {
			PageLayoutModel model = new PageLayoutModel();
			model.pageCode = cursor.getString(1);
			model.resPath = cursor.getString(2);
			model.pageName = cursor.getString(3);
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

	public synchronized PageLayoutModel query_by_pageCode(String pageCode) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "pageCode = ?",
					new String[] { pageCode }, null, null, "id desc");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			PageLayoutModel model = new PageLayoutModel();
			model.pageCode = cursor.getString(1);
			model.resPath = cursor.getString(2);
			model.pageName = cursor.getString(3);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(PageLayoutModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("pageCode", model.pageCode);
		values.put("resPath", model.resPath);
		values.put("pageName", model.pageName);
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
