package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.AttendInfoModel;

public class AttendInfoDatabase {

	private static final String TABLE = "AttendInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "AttendInfoDatabase";

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
				+ "attendId verchar(20) UNIQUE," + "xn verchar(10),"
				+ "xq verchar(10)," + "jxrwid verchar(20),"
				+ "kcmc verchar(30)," + "zc verchar(2)," + "skrq verchar(50),"
				+ "jcshow verchar(20)," + "skjhid verchar(20),"
				+ "ks verchar(2)," + "ssxy verchar(2)," + "ssxb verchar(4),"
				+ "ssxbmc verchar(30)," + "ssbj verchar(20),"
				+ "ssbjmc verchar(50)," + "xsxh verchar(15),"
				+ "zwh verchar(3)," + "xsxm verchar(30)," + "kqzt verchar(1),"
				+ "kqtjry verchar(15)," + "kqtjryxm verchar(20),"
				+ "kqsj verchar(50)," + "glqj verchar(20),"
				+ "remark verchar(500)," + "zp verchar(200),"
				+ "xb verchar(10)," + "xskh verchar(50)," + "qdsx verchar(5)"
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

	public synchronized List<AttendInfoModel> query_all() {
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
		List<AttendInfoModel> list = new ArrayList<AttendInfoModel>();
		while (cursor.moveToNext()) {
			AttendInfoModel model = new AttendInfoModel();
			model.id = cursor.getString(1);
			model.xn = cursor.getString(2);
			model.xq = cursor.getString(3);
			model.jxrwid = cursor.getString(4);
			model.kcmc = cursor.getString(5);
			model.zc = cursor.getString(6);
			model.skrq = cursor.getString(7);
			model.jcshow = cursor.getString(8);
			model.skjhid = cursor.getString(9);
			model.ks = cursor.getString(10);
			model.ssxy = cursor.getString(11);
			model.ssxb = cursor.getString(12);
			model.ssxbmc = cursor.getString(13);
			model.ssbj = cursor.getString(14);
			model.ssbjmc = cursor.getString(15);
			model.xsxh = cursor.getString(16);
			model.zwh = cursor.getString(17);
			model.xsxm = cursor.getString(18);
			model.kqzt = cursor.getString(19);
			model.kqtjry = cursor.getString(20);
			model.kqtjryxm = cursor.getString(21);
			model.kqsj = cursor.getString(22);
			model.glqj = cursor.getString(23);
			model.remark = cursor.getString(24);
			model.zp = cursor.getString(25);
			model.xb = cursor.getString(26);
			model.xskh = cursor.getString(27);
			model.qdsx = cursor.getString(28);
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

	public synchronized List<AttendInfoModel> query_by_skjhid(String skjhid) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "skjhid = ?",
					new String[] { skjhid }, null, null, "zwh asc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<AttendInfoModel> list = new ArrayList<AttendInfoModel>();
		while (cursor.moveToNext()) {
			AttendInfoModel model = new AttendInfoModel();
			model.id = cursor.getString(1);
			model.xn = cursor.getString(2);
			model.xq = cursor.getString(3);
			model.jxrwid = cursor.getString(4);
			model.kcmc = cursor.getString(5);
			model.zc = cursor.getString(6);
			model.skrq = cursor.getString(7);
			model.jcshow = cursor.getString(8);
			model.skjhid = cursor.getString(9);
			model.ks = cursor.getString(10);
			model.ssxy = cursor.getString(11);
			model.ssxb = cursor.getString(12);
			model.ssxbmc = cursor.getString(13);
			model.ssbj = cursor.getString(14);
			model.ssbjmc = cursor.getString(15);
			model.xsxh = cursor.getString(16);
			model.zwh = cursor.getString(17);
			model.xsxm = cursor.getString(18);
			model.kqzt = cursor.getString(19);
			model.kqtjry = cursor.getString(20);
			model.kqtjryxm = cursor.getString(21);
			model.kqsj = cursor.getString(22);
			model.glqj = cursor.getString(23);
			model.remark = cursor.getString(24);
			model.zp = cursor.getString(25);
			model.xb = cursor.getString(26);
			model.xskh = cursor.getString(27);
			model.qdsx = cursor.getString(28);
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

	public synchronized List<AttendInfoModel> query_by_skrq(String skrq) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "skrq = ?",
					new String[] { skrq }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<AttendInfoModel> list = new ArrayList<AttendInfoModel>();
		while (cursor.moveToNext()) {
			AttendInfoModel model = new AttendInfoModel();
			model.id = cursor.getString(1);
			model.xn = cursor.getString(2);
			model.xq = cursor.getString(3);
			model.jxrwid = cursor.getString(4);
			model.kcmc = cursor.getString(5);
			model.zc = cursor.getString(6);
			model.skrq = cursor.getString(7);
			model.jcshow = cursor.getString(8);
			model.skjhid = cursor.getString(9);
			model.ks = cursor.getString(10);
			model.ssxy = cursor.getString(11);
			model.ssxb = cursor.getString(12);
			model.ssxbmc = cursor.getString(13);
			model.ssbj = cursor.getString(14);
			model.ssbjmc = cursor.getString(15);
			model.xsxh = cursor.getString(16);
			model.zwh = cursor.getString(17);
			model.xsxm = cursor.getString(18);
			model.kqzt = cursor.getString(19);
			model.kqtjry = cursor.getString(20);
			model.kqtjryxm = cursor.getString(21);
			model.kqsj = cursor.getString(22);
			model.glqj = cursor.getString(23);
			model.remark = cursor.getString(24);
			model.zp = cursor.getString(25);
			model.xb = cursor.getString(26);
			model.xskh = cursor.getString(27);
			model.qdsx = cursor.getString(28);
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

	public synchronized List<AttendInfoModel> query_by_skrqAndxskh(String skrq,
			String xskh) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "skrq = ? and xskh = ?",
					new String[] { skrq, xskh }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<AttendInfoModel> list = new ArrayList<AttendInfoModel>();
		while (cursor.moveToNext()) {
			AttendInfoModel model = new AttendInfoModel();
			model.id = cursor.getString(1);
			model.xn = cursor.getString(2);
			model.xq = cursor.getString(3);
			model.jxrwid = cursor.getString(4);
			model.kcmc = cursor.getString(5);
			model.zc = cursor.getString(6);
			model.skrq = cursor.getString(7);
			model.jcshow = cursor.getString(8);
			model.skjhid = cursor.getString(9);
			model.ks = cursor.getString(10);
			model.ssxy = cursor.getString(11);
			model.ssxb = cursor.getString(12);
			model.ssxbmc = cursor.getString(13);
			model.ssbj = cursor.getString(14);
			model.ssbjmc = cursor.getString(15);
			model.xsxh = cursor.getString(16);
			model.zwh = cursor.getString(17);
			model.xsxm = cursor.getString(18);
			model.kqzt = cursor.getString(19);
			model.kqtjry = cursor.getString(20);
			model.kqtjryxm = cursor.getString(21);
			model.kqsj = cursor.getString(22);
			model.glqj = cursor.getString(23);
			model.remark = cursor.getString(24);
			model.zp = cursor.getString(25);
			model.xb = cursor.getString(26);
			model.xskh = cursor.getString(27);
			model.qdsx = cursor.getString(28);
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

	public synchronized List<AttendInfoModel> query_by_xskh(String xskh) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "xskh = ?",
					new String[] { xskh }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<AttendInfoModel> list = new ArrayList<AttendInfoModel>();
		while (cursor.moveToNext()) {
			AttendInfoModel model = new AttendInfoModel();
			model.id = cursor.getString(1);
			model.xn = cursor.getString(2);
			model.xq = cursor.getString(3);
			model.jxrwid = cursor.getString(4);
			model.kcmc = cursor.getString(5);
			model.zc = cursor.getString(6);
			model.skrq = cursor.getString(7);
			model.jcshow = cursor.getString(8);
			model.skjhid = cursor.getString(9);
			model.ks = cursor.getString(10);
			model.ssxy = cursor.getString(11);
			model.ssxb = cursor.getString(12);
			model.ssxbmc = cursor.getString(13);
			model.ssbj = cursor.getString(14);
			model.ssbjmc = cursor.getString(15);
			model.xsxh = cursor.getString(16);
			model.zwh = cursor.getString(17);
			model.xsxm = cursor.getString(18);
			model.kqzt = cursor.getString(19);
			model.kqtjry = cursor.getString(20);
			model.kqtjryxm = cursor.getString(21);
			model.kqsj = cursor.getString(22);
			model.glqj = cursor.getString(23);
			model.remark = cursor.getString(24);
			model.zp = cursor.getString(25);
			model.xb = cursor.getString(26);
			model.xskh = cursor.getString(27);
			model.qdsx = cursor.getString(28);
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

	public synchronized List<AttendInfoModel> query_by_xsxh(String xsxh) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "xsxh = ?",
					new String[] { xsxh }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<AttendInfoModel> list = new ArrayList<AttendInfoModel>();
		while (cursor.moveToNext()) {
			AttendInfoModel model = new AttendInfoModel();
			model.id = cursor.getString(1);
			model.xn = cursor.getString(2);
			model.xq = cursor.getString(3);
			model.jxrwid = cursor.getString(4);
			model.kcmc = cursor.getString(5);
			model.zc = cursor.getString(6);
			model.skrq = cursor.getString(7);
			model.jcshow = cursor.getString(8);
			model.skjhid = cursor.getString(9);
			model.ks = cursor.getString(10);
			model.ssxy = cursor.getString(11);
			model.ssxb = cursor.getString(12);
			model.ssxbmc = cursor.getString(13);
			model.ssbj = cursor.getString(14);
			model.ssbjmc = cursor.getString(15);
			model.xsxh = cursor.getString(16);
			model.zwh = cursor.getString(17);
			model.xsxm = cursor.getString(18);
			model.kqzt = cursor.getString(19);
			model.kqtjry = cursor.getString(20);
			model.kqtjryxm = cursor.getString(21);
			model.kqsj = cursor.getString(22);
			model.glqj = cursor.getString(23);
			model.remark = cursor.getString(24);
			model.zp = cursor.getString(25);
			model.xb = cursor.getString(26);
			model.xskh = cursor.getString(27);
			model.qdsx = cursor.getString(28);
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

	public synchronized void insert(AttendInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("attendId", model.id);
		values.put("xn", model.xn);
		values.put("xq", model.xq);
		values.put("jxrwid", model.jxrwid);
		values.put("kcmc", model.kcmc);
		values.put("zc", model.zc);
		values.put("skrq", model.skrq);
		values.put("jcshow", model.jcshow);
		values.put("skjhid", model.skjhid);
		values.put("ks", model.ks);
		values.put("ssxy", model.ssxy);
		values.put("ssxb", model.ssxb);
		values.put("ssxbmc", model.ssxbmc);
		values.put("ssbj", model.ssbj);
		values.put("ssbjmc", model.ssbjmc);
		values.put("xsxh", model.xsxh);
		values.put("zwh", model.zwh);
		values.put("xsxm", model.xsxm);
		values.put("kqzt", model.kqzt);
		values.put("kqtjry", model.kqtjry);
		values.put("kqtjryxm", model.kqtjryxm);
		values.put("kqsj", model.kqsj);
		values.put("glqj", model.glqj);
		values.put("remark", model.remark);
		values.put("zp", model.zp);
		values.put("xb", model.xb);
		values.put("xskh", model.xskh);
		values.put("qdsx", model.qdsx);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String id, AttendInfoModel model) {
		initDB();
		String whereClause = "attendId = " + id;

		ContentValues values = new ContentValues();
		values.put("attendId", model.id);
		values.put("xn", model.xn);
		values.put("xq", model.xq);
		values.put("jxrwid", model.jxrwid);
		values.put("kcmc", model.kcmc);
		values.put("zc", model.zc);
		values.put("skrq", model.skrq);
		values.put("jcshow", model.jcshow);
		values.put("skjhid", model.skjhid);
		values.put("ks", model.ks);
		values.put("ssxy", model.ssxy);
		values.put("ssxb", model.ssxb);
		values.put("ssxbmc", model.ssxbmc);
		values.put("ssbj", model.ssbj);
		values.put("ssbjmc", model.ssbjmc);
		values.put("xsxh", model.xsxh);
		values.put("zwh", model.zwh);
		values.put("xsxm", model.xsxm);
		values.put("kqzt", model.kqzt);
		values.put("kqtjry", model.kqtjry);
		values.put("kqtjryxm", model.kqtjryxm);
		values.put("kqsj", model.kqsj);
		values.put("glqj", model.glqj);
		values.put("remark", model.remark);
		values.put("zp", model.zp);
		values.put("xb", model.xb);
		values.put("xskh", model.xskh);
		values.put("qdsx", model.qdsx);
		try {
			sqLiteDatabase.update(TABLE, values, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete(String id) {
		initDB();
		String sql = "delete from " + TABLE + " where attendId = '" + id + "'";
		try {
			sqLiteDatabase.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void deleteByskjhid(String skjhid) {
		initDB();
		String sql = "delete from " + TABLE + " where skjhid = '" + skjhid
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
