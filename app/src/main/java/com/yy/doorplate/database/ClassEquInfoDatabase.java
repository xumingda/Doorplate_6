package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.ClassEquInfoModel;

public class ClassEquInfoDatabase {

	private static final String TABLE = "ClassEquInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "ClassEquInfoDatabase";

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
				+ "classEquId verchar(20) UNIQUE," + "zcsblbdm verchar(50),"
				+ "zcsblbmc verchar(200)," + "zcsbbh verchar(50),"
				+ "zcsbmc verchar(200)," + "zcsbgys verchar(50),"
				+ "zcsbgysmc verchar(200)," + "zcsbpp verchar(100),"
				+ "ggxh verchar(200)," + "jldw verchar(50),"
				+ "sbxx verchar(255)," + "gzsj verchar(50),"
				+ "sbje verchar(20)," + "cfdddm verchar(50),"
				+ "cfddmc verchar(50)," + "glbmdm verchar(50),"
				+ "glbmmc verchar(50)," + "sbglfzr verchar(50),"
				+ "sbglfzrxm verchar(50)," + "sbsyr verchar(50),"
				+ "sbsyrxm verchar(50)," + "sbzk verchar(10),"
				+ "remark verchar(500)" + ")";
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

	public synchronized List<ClassEquInfoModel> query_all() {
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
		List<ClassEquInfoModel> list = new ArrayList<ClassEquInfoModel>();
		while (cursor.moveToNext()) {
			ClassEquInfoModel model = new ClassEquInfoModel();
			model.id = cursor.getString(1);
			model.zcsblbdm = cursor.getString(2);
			model.zcsblbmc = cursor.getString(3);
			model.zcsbbh = cursor.getString(4);
			model.zcsbmc = cursor.getString(5);
			model.zcsbgys = cursor.getString(6);
			model.zcsbgysmc = cursor.getString(7);
			model.zcsbpp = cursor.getString(8);
			model.ggxh = cursor.getString(9);
			model.jldw = cursor.getString(10);
			model.sbxx = cursor.getString(11);
			model.gzsj = cursor.getString(12);
			model.sbje = cursor.getString(13);
			model.cfdddm = cursor.getString(14);
			model.cfddmc = cursor.getString(15);
			model.glbmdm = cursor.getString(16);
			model.glbmmc = cursor.getString(17);
			model.sbglfzr = cursor.getString(18);
			model.sbglfzrxm = cursor.getString(19);
			model.sbsyr = cursor.getString(20);
			model.sbsyrxm = cursor.getString(21);
			model.sbzk = cursor.getString(22);
			model.remark = cursor.getString(23);
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

	public synchronized void insert(ClassEquInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("classEquId", model.id);
		values.put("zcsblbdm", model.zcsblbdm);
		values.put("zcsblbmc", model.zcsblbmc);
		values.put("zcsbbh", model.zcsbbh);
		values.put("zcsbmc", model.zcsbmc);
		values.put("zcsbgys", model.zcsbgys);
		values.put("zcsbgysmc", model.zcsbgysmc);
		values.put("zcsbpp", model.zcsbpp);
		values.put("ggxh", model.ggxh);
		values.put("jldw", model.jldw);
		values.put("sbxx", model.sbxx);
		values.put("gzsj", model.gzsj);
		values.put("sbje", model.sbje);
		values.put("cfdddm", model.cfdddm);
		values.put("cfddmc", model.cfddmc);
		values.put("glbmdm", model.glbmdm);
		values.put("glbmmc", model.glbmmc);
		values.put("sbglfzr", model.sbglfzr);
		values.put("sbglfzrxm", model.sbglfzrxm);
		values.put("sbsyr", model.sbsyr);
		values.put("sbsyrxm", model.sbsyrxm);
		values.put("sbzk", model.sbzk);
		values.put("remark", model.remark);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String id, ClassEquInfoModel model) {
		initDB();
		String whereClause = "classEquId = " + id;

		ContentValues values = new ContentValues();
		values.put("classEquId", model.id);
		values.put("zcsblbdm", model.zcsblbdm);
		values.put("zcsblbmc", model.zcsblbmc);
		values.put("zcsbbh", model.zcsbbh);
		values.put("zcsbmc", model.zcsbmc);
		values.put("zcsbgys", model.zcsbgys);
		values.put("zcsbgysmc", model.zcsbgysmc);
		values.put("zcsbpp", model.zcsbpp);
		values.put("ggxh", model.ggxh);
		values.put("jldw", model.jldw);
		values.put("sbxx", model.sbxx);
		values.put("gzsj", model.gzsj);
		values.put("sbje", model.sbje);
		values.put("cfdddm", model.cfdddm);
		values.put("cfddmc", model.cfddmc);
		values.put("glbmdm", model.glbmdm);
		values.put("glbmmc", model.glbmmc);
		values.put("sbglfzr", model.sbglfzr);
		values.put("sbglfzrxm", model.sbglfzrxm);
		values.put("sbsyr", model.sbsyr);
		values.put("sbsyrxm", model.sbsyrxm);
		values.put("sbzk", model.sbzk);
		values.put("remark", model.remark);
		try {
			sqLiteDatabase.update(TABLE, values, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete(String id) {
		initDB();
		String sql = "delete from " + TABLE + " where classEquId = '" + id
				+ "'";
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
