package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.StudentInfoModel;

public class StudentInfoDatabase {

	private static final String TABLE = "StudentInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "StudentInfoDatabase";

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
				+ "studentId verchar(20) UNIQUE," + "remark verchar(500),"
				+ "xsxh verchar(20)," + "xm verchar(20)," + "zwh verchar(5),"
				+ "rxny verchar(50)," + "ssxy verchar(20),"
				+ "ssxb verchar(20)," + "ssxbmc verchar(20),"
				+ "sszy verchar(20)," + "sszymc verchar(20),"
				+ "ssbj verchar(20)," + "ssbjmc verchar(20),"
				+ "xslb verchar(2)," + "xszt verchar(2)," + "xb verchar(2),"
				+ "zjlx verchar(2)," + "zjh verchar(20)," + "zp verchar(100),"
				+ "xszw verchar(20)," + "bzr verchar(20),"
				+ "dzxx verchar(100)," + "csny verchar(20),"
				+ "yddh verchar(20)," + "lxrdh verchar(20),"
				+ "jtdz verchar(500)," + "tc verchar(500),"
				+ "cardid verchar(20)," + "xsjzuid verchar(50),"
				+ "uid verchar(50)" + ")";
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

	public synchronized List<StudentInfoModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null,
					/* "zwh asc" */null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<StudentInfoModel> list = new ArrayList<StudentInfoModel>();
		while (cursor.moveToNext()) {
			StudentInfoModel model = new StudentInfoModel();
			model.id = cursor.getString(1);
			model.remark = cursor.getString(2);
			model.xsxh = cursor.getString(3);
			model.xm = cursor.getString(4);
			model.zwh = cursor.getString(5);
			model.rxny = cursor.getString(6);
			model.ssxy = cursor.getString(7);
			model.ssxb = cursor.getString(8);
			model.ssxbmc = cursor.getString(9);
			model.sszy = cursor.getString(10);
			model.sszymc = cursor.getString(11);
			model.ssbj = cursor.getString(12);
			model.ssbjmc = cursor.getString(13);
			model.xslb = cursor.getString(14);
			model.xszt = cursor.getString(15);
			model.xb = cursor.getString(16);
			model.zjlx = cursor.getString(17);
			model.zjh = cursor.getString(18);
			model.zp = cursor.getString(19);
			model.xszw = cursor.getString(20);
			try {
				model.bzr = cursor.getString(21);
				model.dzxx = cursor.getString(22);
				model.csny = cursor.getString(23);
				model.yddh = cursor.getString(24);
				model.lxrdh = cursor.getString(25);
				model.jtdz = cursor.getString(26);
				model.tc = cursor.getString(27);
				model.cardid = cursor.getString(28);
				model.xsjzuid = cursor.getString(29);
				model.uid = cursor.getString(30);
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

	public synchronized List<StudentInfoModel> query_by_ssbj(String ssbj) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "ssbj = ?",
					new String[] { ssbj }, null,
					/* "zwh asc" */null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<StudentInfoModel> list = new ArrayList<StudentInfoModel>();
		while (cursor.moveToNext()) {
			StudentInfoModel model = new StudentInfoModel();
			model.id = cursor.getString(1);
			model.remark = cursor.getString(2);
			model.xsxh = cursor.getString(3);
			model.xm = cursor.getString(4);
			model.zwh = cursor.getString(5);
			model.rxny = cursor.getString(6);
			model.ssxy = cursor.getString(7);
			model.ssxb = cursor.getString(8);
			model.ssxbmc = cursor.getString(9);
			model.sszy = cursor.getString(10);
			model.sszymc = cursor.getString(11);
			model.ssbj = cursor.getString(12);
			model.ssbjmc = cursor.getString(13);
			model.xslb = cursor.getString(14);
			model.xszt = cursor.getString(15);
			model.xb = cursor.getString(16);
			model.zjlx = cursor.getString(17);
			model.zjh = cursor.getString(18);
			model.zp = cursor.getString(19);
			model.xszw = cursor.getString(20);
			try {
				model.bzr = cursor.getString(21);
				model.dzxx = cursor.getString(22);
				model.csny = cursor.getString(23);
				model.yddh = cursor.getString(24);
				model.lxrdh = cursor.getString(25);
				model.jtdz = cursor.getString(26);
				model.tc = cursor.getString(27);
				model.cardid = cursor.getString(28);
				model.xsjzuid = cursor.getString(29);
				model.uid = cursor.getString(30);
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

	public synchronized StudentInfoModel query_by_cardid(String cardid) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "cardid = ?",
					new String[] { cardid }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			StudentInfoModel model = new StudentInfoModel();
			model.id = cursor.getString(1);
			model.remark = cursor.getString(2);
			model.xsxh = cursor.getString(3);
			model.xm = cursor.getString(4);
			model.zwh = cursor.getString(5);
			model.rxny = cursor.getString(6);
			model.ssxy = cursor.getString(7);
			model.ssxb = cursor.getString(8);
			model.ssxbmc = cursor.getString(9);
			model.sszy = cursor.getString(10);
			model.sszymc = cursor.getString(11);
			model.ssbj = cursor.getString(12);
			model.ssbjmc = cursor.getString(13);
			model.xslb = cursor.getString(14);
			model.xszt = cursor.getString(15);
			model.xb = cursor.getString(16);
			model.zjlx = cursor.getString(17);
			model.zjh = cursor.getString(18);
			model.zp = cursor.getString(19);
			model.xszw = cursor.getString(20);
			try {
				model.bzr = cursor.getString(21);
				model.dzxx = cursor.getString(22);
				model.csny = cursor.getString(23);
				model.yddh = cursor.getString(24);
				model.lxrdh = cursor.getString(25);
				model.jtdz = cursor.getString(26);
				model.tc = cursor.getString(27);
				model.cardid = cursor.getString(28);
				model.xsjzuid = cursor.getString(29);
				model.uid = cursor.getString(30);
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

	public synchronized StudentInfoModel query_by_xsxh(String xsxh) {
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
		if (cursor.moveToFirst()) {
			StudentInfoModel model = new StudentInfoModel();
			model.id = cursor.getString(1);
			model.remark = cursor.getString(2);
			model.xsxh = cursor.getString(3);
			model.xm = cursor.getString(4);
			model.zwh = cursor.getString(5);
			model.rxny = cursor.getString(6);
			model.ssxy = cursor.getString(7);
			model.ssxb = cursor.getString(8);
			model.ssxbmc = cursor.getString(9);
			model.sszy = cursor.getString(10);
			model.sszymc = cursor.getString(11);
			model.ssbj = cursor.getString(12);
			model.ssbjmc = cursor.getString(13);
			model.xslb = cursor.getString(14);
			model.xszt = cursor.getString(15);
			model.xb = cursor.getString(16);
			model.zjlx = cursor.getString(17);
			model.zjh = cursor.getString(18);
			model.zp = cursor.getString(19);
			model.xszw = cursor.getString(20);
			try {
				model.bzr = cursor.getString(21);
				model.dzxx = cursor.getString(22);
				model.csny = cursor.getString(23);
				model.yddh = cursor.getString(24);
				model.lxrdh = cursor.getString(25);
				model.jtdz = cursor.getString(26);
				model.tc = cursor.getString(27);
				model.cardid = cursor.getString(28);
				model.xsjzuid = cursor.getString(29);
				model.uid = cursor.getString(30);
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

	public synchronized void insert(StudentInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("studentId", model.id);
		values.put("remark", model.remark);
		values.put("xsxh", model.xsxh);
		values.put("xm", model.xm);
		values.put("zwh", model.zwh);
		values.put("rxny", model.rxny);
		values.put("ssxy", model.ssxy);
		values.put("ssxb", model.ssxb);
		values.put("ssxbmc", model.ssxbmc);
		values.put("sszy", model.sszy);
		values.put("sszymc", model.sszymc);
		values.put("ssbj", model.ssbj);
		values.put("ssbjmc", model.ssbjmc);
		values.put("xslb", model.xslb);
		values.put("xszt", model.xszt);
		values.put("xb", model.xb);
		values.put("zjlx", model.zjlx);
		values.put("zjh", model.zjh);
		values.put("zp", model.zp);
		values.put("xszw", model.xszw);
		try {
			values.put("bzr", model.bzr);
			values.put("dzxx", model.dzxx);
			values.put("csny", model.csny);
			values.put("yddh", model.yddh);
			values.put("lxrdh", model.lxrdh);
			values.put("jtdz", model.jtdz);
			values.put("tc", model.tc);
			values.put("cardid", model.cardid);
			values.put("xsjzuid", model.xsjzuid);
			values.put("uid", model.uid);
			sqLiteDatabase.replace(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String id, StudentInfoModel model) {
		initDB();
		String whereClause = "studentId = " + id;

		ContentValues values = new ContentValues();
		values.put("studentId", model.id);
		values.put("remark", model.remark);
		values.put("xsxh", model.xsxh);
		values.put("xm", model.xm);
		values.put("zwh", model.zwh);
		values.put("rxny", model.rxny);
		values.put("ssxy", model.ssxy);
		values.put("ssxb", model.ssxb);
		values.put("ssxbmc", model.ssxbmc);
		values.put("sszy", model.sszy);
		values.put("sszymc", model.sszymc);
		values.put("ssbj", model.ssbj);
		values.put("ssbjmc", model.ssbjmc);
		values.put("xslb", model.xslb);
		values.put("xszt", model.xszt);
		values.put("xb", model.xb);
		values.put("zjlx", model.zjlx);
		values.put("zjh", model.zjh);
		values.put("zp", model.zp);
		values.put("xszw", model.xszw);
		try {
			values.put("bzr", model.bzr);
			values.put("dzxx", model.dzxx);
			values.put("csny", model.csny);
			values.put("yddh", model.yddh);
			values.put("lxrdh", model.lxrdh);
			values.put("jtdz", model.jtdz);
			values.put("tc", model.tc);
			values.put("cardid", model.cardid);
			values.put("xsjzuid", model.xsjzuid);
			values.put("uid", model.uid);
			sqLiteDatabase.update(TABLE, values, whereClause, null);
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
