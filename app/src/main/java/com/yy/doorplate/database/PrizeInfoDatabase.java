package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.PrizeInfoModel;

public class PrizeInfoDatabase {

	private static final String TABLE = "PrizeInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "PrizeInfoDatabase";

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
				+ "prizeId verchar(20) UNIQUE," + "prizeCode verchar(20),"
				+ "prizeName verchar(100)," + "iconUrl verchar(100),"
				+ "prizeDate verchar(20)," + "prizeType verchar(10),"
				+ "relaCode verchar(20)," + "prizePerson verchar(100),"
				+ "prizeUnit verchar(100)," + "ranking verchar(20),"
				+ "awardsUnit verchar(100)," + "xsxh verchar(20)" + ")";
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

	public synchronized List<PrizeInfoModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					"prizeDate desc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<PrizeInfoModel> list = new ArrayList<PrizeInfoModel>();
		while (cursor.moveToNext()) {
			PrizeInfoModel model = new PrizeInfoModel();
			model.prizeId = cursor.getString(1);
			model.prizeCode = cursor.getString(2);
			model.prizeName = cursor.getString(3);
			model.iconUrl = cursor.getString(4);
			model.prizeDate = cursor.getString(5);
			model.prizeType = cursor.getString(6);
			model.relaCode = cursor.getString(7);
			model.prizePerson = cursor.getString(8);
			model.prizeUnit = cursor.getString(9);
			model.ranking = cursor.getString(10);
			model.awardsUnit = cursor.getString(11);
			try {
				model.xsxh = cursor.getString(12);
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

	public synchronized List<PrizeInfoModel> query_by_prizeType(String prizeType) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "prizeType = ?",
					new String[] { prizeType }, null, null, "prizeDate desc",
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<PrizeInfoModel> list = new ArrayList<PrizeInfoModel>();
		while (cursor.moveToNext()) {
			PrizeInfoModel model = new PrizeInfoModel();
			model.prizeId = cursor.getString(1);
			model.prizeCode = cursor.getString(2);
			model.prizeName = cursor.getString(3);
			model.iconUrl = cursor.getString(4);
			model.prizeDate = cursor.getString(5);
			model.prizeType = cursor.getString(6);
			model.relaCode = cursor.getString(7);
			model.prizePerson = cursor.getString(8);
			model.prizeUnit = cursor.getString(9);
			model.ranking = cursor.getString(10);
			model.awardsUnit = cursor.getString(11);
			try {
				model.xsxh = cursor.getString(12);
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

	public synchronized PrizeInfoModel query_by_code(String code) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "prizeCode = ?",
					new String[] { code }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			PrizeInfoModel model = new PrizeInfoModel();
			model.prizeId = cursor.getString(1);
			model.prizeCode = cursor.getString(2);
			model.prizeName = cursor.getString(3);
			model.iconUrl = cursor.getString(4);
			model.prizeDate = cursor.getString(5);
			model.prizeType = cursor.getString(6);
			model.relaCode = cursor.getString(7);
			model.prizePerson = cursor.getString(8);
			model.prizeUnit = cursor.getString(9);
			model.ranking = cursor.getString(10);
			model.awardsUnit = cursor.getString(11);
			try {
				model.xsxh = cursor.getString(12);
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

	public synchronized List<PrizeInfoModel> query_by_xsxh(String xsxh) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "xsxh = ?",
					new String[] { xsxh }, null, null, "prizeDate desc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<PrizeInfoModel> list = new ArrayList<PrizeInfoModel>();
		while (cursor.moveToNext()) {
			PrizeInfoModel model = new PrizeInfoModel();
			model.prizeId = cursor.getString(1);
			model.prizeCode = cursor.getString(2);
			model.prizeName = cursor.getString(3);
			model.iconUrl = cursor.getString(4);
			model.prizeDate = cursor.getString(5);
			model.prizeType = cursor.getString(6);
			model.relaCode = cursor.getString(7);
			model.prizePerson = cursor.getString(8);
			model.prizeUnit = cursor.getString(9);
			model.ranking = cursor.getString(10);
			model.awardsUnit = cursor.getString(11);
			try {
				model.xsxh = cursor.getString(12);
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

	public synchronized void insert(PrizeInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("prizeId", model.prizeId);
		values.put("prizeCode", model.prizeCode);
		values.put("prizeName", model.prizeName);
		values.put("iconUrl", model.iconUrl);
		values.put("prizeDate", model.prizeDate);
		values.put("prizeType", model.prizeType);
		values.put("relaCode", model.relaCode);
		values.put("prizePerson", model.prizePerson);
		values.put("prizeUnit", model.prizeUnit);
		values.put("ranking", model.ranking);
		values.put("awardsUnit", model.awardsUnit);
		try {
			values.put("xsxh", model.xsxh);
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
