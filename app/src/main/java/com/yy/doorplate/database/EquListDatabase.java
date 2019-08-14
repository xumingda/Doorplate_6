package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.EquInfoModel;

public class EquListDatabase {

	private static final String TABLE = "EquList";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "EquListDatabase";

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
				+ "equCode verchar(20) UNIQUE," + "equName verchar(50),"
				+ "jssysdm verchar(20)," + "jssysmc verchar(50),"
				+ "ip verchar(20)," + "gateway verchar(20),"
				+ "subMask verchar(20)," + "dns verchar(20),"
				+ "dns2 verchar(20)," + "mac verchar(20),"
				+ "status verchar(10)," + "equMode verchar(10),"
				+ "bjdm verchar(20)," + "accSysIp verchar(20),"
				+ "accSysPort verchar(10)," + "equType verchar(10)" + ")";
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

	public synchronized List<EquInfoModel> query_by_type(String type) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "equType = ?",
					new String[] { type }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<EquInfoModel> list = new ArrayList<EquInfoModel>();
		while (cursor.moveToNext()) {
			EquInfoModel model = new EquInfoModel();
			model.equCode = cursor.getString(1);
			model.equName = cursor.getString(2);
			model.jssysdm = cursor.getString(3);
			model.jssysmc = cursor.getString(4);
			model.ip = cursor.getString(5);
			model.gateway = cursor.getString(6);
			model.subMask = cursor.getString(7);
			model.dns = cursor.getString(8);
			model.dns2 = cursor.getString(9);
			model.mac = cursor.getString(10);
			model.status = cursor.getString(11);
			model.equMode = cursor.getString(12);
			model.bjdm = cursor.getString(13);
			model.accSysIp = cursor.getString(14);
			model.accSysPort = cursor.getString(15);
			model.equType = cursor.getString(16);
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

	public synchronized EquInfoModel query_by_ip(String ip) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "ip = ?",
					new String[] { ip }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			EquInfoModel model = new EquInfoModel();
			model.equCode = cursor.getString(1);
			model.equName = cursor.getString(2);
			model.jssysdm = cursor.getString(3);
			model.jssysmc = cursor.getString(4);
			model.ip = cursor.getString(5);
			model.gateway = cursor.getString(6);
			model.subMask = cursor.getString(7);
			model.dns = cursor.getString(8);
			model.dns2 = cursor.getString(9);
			model.mac = cursor.getString(10);
			model.status = cursor.getString(11);
			model.equMode = cursor.getString(12);
			model.bjdm = cursor.getString(13);
			model.accSysIp = cursor.getString(14);
			model.accSysPort = cursor.getString(15);
			model.equType = cursor.getString(16);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}
	
	public synchronized EquInfoModel query_by_id(String id) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "equCode = ?",
					new String[] { id }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			EquInfoModel model = new EquInfoModel();
			model.equCode = cursor.getString(1);
			model.equName = cursor.getString(2);
			model.jssysdm = cursor.getString(3);
			model.jssysmc = cursor.getString(4);
			model.ip = cursor.getString(5);
			model.gateway = cursor.getString(6);
			model.subMask = cursor.getString(7);
			model.dns = cursor.getString(8);
			model.dns2 = cursor.getString(9);
			model.mac = cursor.getString(10);
			model.status = cursor.getString(11);
			model.equMode = cursor.getString(12);
			model.bjdm = cursor.getString(13);
			model.accSysIp = cursor.getString(14);
			model.accSysPort = cursor.getString(15);
			model.equType = cursor.getString(16);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(EquInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("equCode", model.equCode);
		values.put("equName", model.equName);
		values.put("jssysdm", model.jssysdm);
		values.put("jssysmc", model.jssysmc);
		values.put("ip", model.ip);
		values.put("gateway", model.gateway);
		values.put("subMask", model.subMask);
		values.put("dns", model.dns);
		values.put("dns2", model.dns2);
		values.put("mac", model.mac);
		values.put("status", model.status);
		values.put("equMode", model.equMode);
		values.put("bjdm", model.bjdm);
		values.put("accSysIp", model.accSysIp);
		values.put("accSysPort", model.accSysPort);
		values.put("equType", model.equType);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String equNo, EquInfoModel model) {
		initDB();
		String whereClause = "equNo = " + equNo;

		ContentValues values = new ContentValues();
		values.put("equCode", model.equCode);
		values.put("equName", model.equName);
		values.put("jssysdm", model.jssysdm);
		values.put("jssysmc", model.jssysmc);
		values.put("ip", model.ip);
		values.put("gateway", model.gateway);
		values.put("subMask", model.subMask);
		values.put("dns", model.dns);
		values.put("dns2", model.dns2);
		values.put("mac", model.mac);
		values.put("status", model.status);
		values.put("equMode", model.equMode);
		values.put("bjdm", model.bjdm);
		values.put("accSysIp", model.accSysIp);
		values.put("accSysPort", model.accSysPort);
		values.put("equType", model.equType);
		try {
			sqLiteDatabase.update(TABLE, values, whereClause, null);
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
