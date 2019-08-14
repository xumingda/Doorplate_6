package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.TelMenuInfoModel;

public class TelMenuDatabase {

	private static final String TABLE = "TelMenu";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "TelMenuDatabase";

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
				+ "menuId verchar(20) UNIQUE," + "menuName verchar(20),"
				+ "menuSort verchar(5)," + "menuCode verchar(20),"
				+ "menuIcon verchar(100)," + "menuType verchar(20)" + ")";
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

	public synchronized List<TelMenuInfoModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					"menuSort asc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<TelMenuInfoModel> list = new ArrayList<TelMenuInfoModel>();
		while (cursor.moveToNext()) {
			TelMenuInfoModel model = new TelMenuInfoModel();
			model.menuId = cursor.getString(1);
			model.menuName = cursor.getString(2);
			model.menuSort = cursor.getString(3);
			model.menuCode = cursor.getString(4);
			model.menuIcon = cursor.getString(5);
			model.menuType = cursor.getString(6);
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

	public synchronized TelMenuInfoModel query_by_menuType(String menuType) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "menuType = ?",
					new String[] { menuType + "" }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			TelMenuInfoModel model = new TelMenuInfoModel();
			model.menuId = cursor.getString(1);
			model.menuName = cursor.getString(2);
			model.menuSort = cursor.getString(3);
			model.menuCode = cursor.getString(4);
			model.menuIcon = cursor.getString(5);
			model.menuType = cursor.getString(6);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(TelMenuInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("menuId", model.menuId);
		values.put("menuName", model.menuName);
		values.put("menuSort", model.menuSort);
		values.put("menuCode", model.menuCode);
		values.put("menuIcon", model.menuIcon);
		values.put("menuType", model.menuType);
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
