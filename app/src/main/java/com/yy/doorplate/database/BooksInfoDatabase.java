package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.BooksInfoModel;

public class BooksInfoDatabase {

	private static final String TABLE = "Books";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "BooksInfoDatabase";

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
				+ "id verchar(20) UNIQUE," + "title verchar(100),"
				+ "bookNum verchar(50)," + "publish verchar(50),"
				+ "author verchar(50)," + "updateTime verchar(50),"
				+ "summary verchar(500)," + "cover verchar(100),"
				+ "sort verchar(5)," + "dataSources verchar(50),"
				+ "thirdbookId verchar(50)" + ")";
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

	public synchronized List<BooksInfoModel> query_all() {
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
		List<BooksInfoModel> list = new ArrayList<BooksInfoModel>();
		while (cursor.moveToNext()) {
			BooksInfoModel model = new BooksInfoModel();
			model.id = cursor.getString(0);
			model.title = cursor.getString(1);
			model.bookNum = cursor.getString(2);
			model.publish = cursor.getString(3);
			model.author = cursor.getString(4);
			model.updateTime = cursor.getString(5);
			model.summary = cursor.getString(6);
			model.cover = cursor.getString(7);
			model.sort = cursor.getString(8);
			model.dataSources = cursor.getString(9);
			model.thirdbookId = cursor.getString(10);
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

	public synchronized void insert(BooksInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("title", model.title);
		values.put("bookNum", model.bookNum);
		values.put("publish", model.publish);
		values.put("author", model.author);
		values.put("updateTime", model.updateTime);
		values.put("summary", model.summary);
		values.put("cover", model.cover);
		values.put("sort", model.sort);
		values.put("dataSources", model.dataSources);
		values.put("thirdbookId", model.thirdbookId);
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
