package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.DiyMenuModel;

public class DiyMenuDatabase {

	private static final String TABLE = "DiyMenu";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "DiyMenuDatabase";

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
				+ "id verchar(20)," + "itemCode verchar(20) UNIQUE,"
				+ "parmentCode verchar(20)," + "name verchar(100),"
				+ "iconAddress verchar(500)," + "valueAddress verchar(500)"
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

	public synchronized List<DiyMenuModel> query_all() {
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
		List<DiyMenuModel> list = new ArrayList<DiyMenuModel>();
		while (cursor.moveToNext()) {
			DiyMenuModel model = new DiyMenuModel();
			model.id = cursor.getString(0);
			model.itemCode = cursor.getString(1);
			model.parmentCode = cursor.getString(2);
			model.name = cursor.getString(3);
			model.iconAddress = cursor.getString(4);
			model.valueAddress = cursor.getString(5);
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

	public synchronized List<DiyMenuModel> query(String selection,
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
		List<DiyMenuModel> list = new ArrayList<DiyMenuModel>();
		while (cursor.moveToNext()) {
			DiyMenuModel model = new DiyMenuModel();
			model.id = cursor.getString(0);
			model.itemCode = cursor.getString(1);
			model.parmentCode = cursor.getString(2);
			model.name = cursor.getString(3);
			model.iconAddress = cursor.getString(4);
			model.valueAddress = cursor.getString(5);
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

	public synchronized void insert(DiyMenuModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("id", model.id);
		values.put("itemCode", model.itemCode);
		values.put("parmentCode", model.parmentCode);
		values.put("name", model.name);
		values.put("iconAddress", model.iconAddress);
		values.put("valueAddress", model.valueAddress);
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
