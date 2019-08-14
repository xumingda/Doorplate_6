package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.OrgModel;

public class OrgDatabase {

	private static final String TABLE = "Org";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "OrgDatabase";

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
				+ "orgId verchar(20) PRIMARY KEY," + "orgCode verchar(20),"
				+ "orgName verchar(20)," + "parentOrgId verchar(20)" + ")";
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

	public synchronized List<OrgModel> query(String selection, String[] selectionArgs) {
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
		List<OrgModel> list = new ArrayList<OrgModel>();
		while (cursor.moveToNext()) {
			OrgModel model = new OrgModel();
			model.orgId = cursor.getString(0);
			model.orgCode = cursor.getString(1);
			model.orgName = cursor.getString(2);
			model.parentOrgId = cursor.getString(3);
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

	public synchronized void insert(OrgModel org) {
		initDB();
		ContentValues cValue = new ContentValues();
		cValue.put("orgId", org.orgId);
		cValue.put("orgCode", org.orgCode);
		cValue.put("orgName", org.orgName);
		cValue.put("parentOrgId", org.parentOrgId);
		try {
			sqLiteDatabase.insert(TABLE, null, cValue);
		} catch (Exception e) {
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

	private void close() {
		try {
			sqLiteDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
