package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.NoticeInfoModel;

public class NoticeInfoDatabase {

	private static final String TABLE = "NoticeInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "NoticeInfoDatabase";

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
				+ "noticeInfoId verchar(20) UNIQUE," + "lmdm verchar(50),"
				+ "lmmc verchar(100)," + "xxzt verchar(500),"
				+ "xxnr verchar(10000)," + "fbr verchar(20),"
				+ "fbrxm verchar(50)," + "fbrbm verchar(50),"
				+ "fbrbmmc verchar(100)," + "fbsj verchar(50),"
				+ "hhs verchar(10)," + "dzs verchar(10),"
				+ "zttp verchar(500)," + "tplb verchar(5000),"
				+ "wjlb verchar(5000)," + "remark verchar(500),"
				+ "create_time verchar(50)," + "update_time verchar(50),"
				+ "deltag verchar(10)," + "oprybh verchar(50),"
				+ "xxztcode verchar(50)," + "xxztlb verchar(50),"
				+ "xxztzt verchar(10)," + "nrlb verchar(5),"
				+ "gldm verchar(20)," + "endTime verchar(20)" + ")";
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

	public synchronized List<NoticeInfoModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					"fbsj desc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<NoticeInfoModel> list = new ArrayList<NoticeInfoModel>();
		while (cursor.moveToNext()) {
			NoticeInfoModel model = new NoticeInfoModel();
			model.id = cursor.getString(1);
			model.lmdm = cursor.getString(2);
			model.lmmc = cursor.getString(3);
			model.xxzt = cursor.getString(4);
			model.xxnr = cursor.getString(5);
			model.fbr = cursor.getString(6);
			model.fbrxm = cursor.getString(7);
			model.fbrbm = cursor.getString(8);
			model.fbrbmmc = cursor.getString(9);
			model.fbsj = cursor.getString(10);
			model.hhs = cursor.getString(11);
			model.dzs = cursor.getString(12);
			model.zttp = cursor.getString(13);
			model.tplb = cursor.getString(14);
			model.wjlb = cursor.getString(15);
			model.remark = cursor.getString(16);
			model.create_time = cursor.getString(17);
			model.update_time = cursor.getString(18);
			model.deltag = cursor.getString(19);
			model.oprybh = cursor.getString(20);
			model.xxztcode = cursor.getString(21);
			model.xxztlb = cursor.getString(22);
			model.xxztzt = cursor.getString(23);
			try {
				model.nrlb = cursor.getString(24);
				model.gldm = cursor.getString(25);
				model.endTime = cursor.getString(26);
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

	public synchronized List<NoticeInfoModel> query_limit(int count, int skip) {
		initDB();
		Cursor cursor = null;
		try {
			String sql = "select * from " + TABLE
					+ " order by fbsj desc LIMIT " + count + "," + skip;
			cursor = sqLiteDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<NoticeInfoModel> list = new ArrayList<NoticeInfoModel>();
		while (cursor.moveToNext()) {
			NoticeInfoModel model = new NoticeInfoModel();
			model.id = cursor.getString(1);
			model.lmdm = cursor.getString(2);
			model.lmmc = cursor.getString(3);
			model.xxzt = cursor.getString(4);
			model.xxnr = cursor.getString(5);
			model.fbr = cursor.getString(6);
			model.fbrxm = cursor.getString(7);
			model.fbrbm = cursor.getString(8);
			model.fbrbmmc = cursor.getString(9);
			model.fbsj = cursor.getString(10);
			model.hhs = cursor.getString(11);
			model.dzs = cursor.getString(12);
			model.zttp = cursor.getString(13);
			model.tplb = cursor.getString(14);
			model.wjlb = cursor.getString(15);
			model.remark = cursor.getString(16);
			model.create_time = cursor.getString(17);
			model.update_time = cursor.getString(18);
			model.deltag = cursor.getString(19);
			model.oprybh = cursor.getString(20);
			model.xxztcode = cursor.getString(21);
			model.xxztlb = cursor.getString(22);
			model.xxztzt = cursor.getString(23);
			try {
				model.nrlb = cursor.getString(24);
				model.gldm = cursor.getString(25);
				model.endTime = cursor.getString(26);
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

	public synchronized NoticeInfoModel query_by_id(String id) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "noticeInfoId = ?",
					new String[] { id }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			NoticeInfoModel model = new NoticeInfoModel();
			model.id = cursor.getString(1);
			model.lmdm = cursor.getString(2);
			model.lmmc = cursor.getString(3);
			model.xxzt = cursor.getString(4);
			model.xxnr = cursor.getString(5);
			model.fbr = cursor.getString(6);
			model.fbrxm = cursor.getString(7);
			model.fbrbm = cursor.getString(8);
			model.fbrbmmc = cursor.getString(9);
			model.fbsj = cursor.getString(10);
			model.hhs = cursor.getString(11);
			model.dzs = cursor.getString(12);
			model.zttp = cursor.getString(13);
			model.tplb = cursor.getString(14);
			model.wjlb = cursor.getString(15);
			model.remark = cursor.getString(16);
			model.create_time = cursor.getString(17);
			model.update_time = cursor.getString(18);
			model.deltag = cursor.getString(19);
			model.oprybh = cursor.getString(20);
			model.xxztcode = cursor.getString(21);
			model.xxztlb = cursor.getString(22);
			model.xxztzt = cursor.getString(23);
			try {
				model.nrlb = cursor.getString(24);
				model.gldm = cursor.getString(25);
				model.endTime = cursor.getString(26);
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

	public synchronized List<NoticeInfoModel> query_by_lmdm(String lmdm) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "lmdm = ?",
					new String[] { lmdm }, null, null, "fbsj desc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<NoticeInfoModel> list = new ArrayList<NoticeInfoModel>();
		while (cursor.moveToNext()) {
			NoticeInfoModel model = new NoticeInfoModel();
			model.id = cursor.getString(1);
			model.lmdm = cursor.getString(2);
			model.lmmc = cursor.getString(3);
			model.xxzt = cursor.getString(4);
			model.xxnr = cursor.getString(5);
			model.fbr = cursor.getString(6);
			model.fbrxm = cursor.getString(7);
			model.fbrbm = cursor.getString(8);
			model.fbrbmmc = cursor.getString(9);
			model.fbsj = cursor.getString(10);
			model.hhs = cursor.getString(11);
			model.dzs = cursor.getString(12);
			model.zttp = cursor.getString(13);
			model.tplb = cursor.getString(14);
			model.wjlb = cursor.getString(15);
			model.remark = cursor.getString(16);
			model.create_time = cursor.getString(17);
			model.update_time = cursor.getString(18);
			model.deltag = cursor.getString(19);
			model.oprybh = cursor.getString(20);
			model.xxztcode = cursor.getString(21);
			model.xxztlb = cursor.getString(22);
			model.xxztzt = cursor.getString(23);
			try {
				model.nrlb = cursor.getString(24);
				model.gldm = cursor.getString(25);
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

	public synchronized List<NoticeInfoModel> query_by_lmdm(String lmdm,
															String xxztlb) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null,
					"lmdm = ? and xxztlb = ?", new String[] { lmdm, xxztlb },
					null, null, "fbsj desc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<NoticeInfoModel> list = new ArrayList<NoticeInfoModel>();
		while (cursor.moveToNext()) {
			NoticeInfoModel model = new NoticeInfoModel();
			model.id = cursor.getString(1);
			model.lmdm = cursor.getString(2);
			model.lmmc = cursor.getString(3);
			model.xxzt = cursor.getString(4);
			model.xxnr = cursor.getString(5);
			model.fbr = cursor.getString(6);
			model.fbrxm = cursor.getString(7);
			model.fbrbm = cursor.getString(8);
			model.fbrbmmc = cursor.getString(9);
			model.fbsj = cursor.getString(10);
			model.hhs = cursor.getString(11);
			model.dzs = cursor.getString(12);
			model.zttp = cursor.getString(13);
			model.tplb = cursor.getString(14);
			model.wjlb = cursor.getString(15);
			model.remark = cursor.getString(16);
			model.create_time = cursor.getString(17);
			model.update_time = cursor.getString(18);
			model.deltag = cursor.getString(19);
			model.oprybh = cursor.getString(20);
			model.xxztcode = cursor.getString(21);
			model.xxztlb = cursor.getString(22);
			model.xxztzt = cursor.getString(23);
			try {
				model.nrlb = cursor.getString(24);
				model.gldm = cursor.getString(25);
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

	public synchronized List<NoticeInfoModel> query_by_lmdm(String lmdm,
															int count, int skip) {
		initDB();
		Cursor cursor = null;
		try {
			String sql = "select * from " + TABLE + " where lmdm = '" + lmdm
					+ "' order by fbsj desc LIMIT " + count + "," + skip;
			cursor = sqLiteDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<NoticeInfoModel> list = new ArrayList<NoticeInfoModel>();
		while (cursor.moveToNext()) {
			NoticeInfoModel model = new NoticeInfoModel();
			model.id = cursor.getString(1);
			model.lmdm = cursor.getString(2);
			model.lmmc = cursor.getString(3);
			model.xxzt = cursor.getString(4);
			model.xxnr = cursor.getString(5);
			model.fbr = cursor.getString(6);
			model.fbrxm = cursor.getString(7);
			model.fbrbm = cursor.getString(8);
			model.fbrbmmc = cursor.getString(9);
			model.fbsj = cursor.getString(10);
			model.hhs = cursor.getString(11);
			model.dzs = cursor.getString(12);
			model.zttp = cursor.getString(13);
			model.tplb = cursor.getString(14);
			model.wjlb = cursor.getString(15);
			model.remark = cursor.getString(16);
			model.create_time = cursor.getString(17);
			model.update_time = cursor.getString(18);
			model.deltag = cursor.getString(19);
			model.oprybh = cursor.getString(20);
			model.xxztcode = cursor.getString(21);
			model.xxztlb = cursor.getString(22);
			model.xxztzt = cursor.getString(23);
			try {
				model.nrlb = cursor.getString(24);
				model.gldm = cursor.getString(25);
				model.endTime = cursor.getString(26);
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

	public synchronized List<NoticeInfoModel> query_by_lmdm(String lmdm,
															String xxztlb, int count, int skip) {
		initDB();
		Cursor cursor = null;
		try {
			String sql = "select * from " + TABLE + " where lmdm = '" + lmdm
					+ "' and xxztlb = '" + xxztlb
					+ "' order by fbsj desc LIMIT " + count + "," + skip;
			cursor = sqLiteDatabase.rawQuery(sql, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<NoticeInfoModel> list = new ArrayList<NoticeInfoModel>();
		while (cursor.moveToNext()) {
			NoticeInfoModel model = new NoticeInfoModel();
			model.id = cursor.getString(1);
			model.lmdm = cursor.getString(2);
			model.lmmc = cursor.getString(3);
			model.xxzt = cursor.getString(4);
			model.xxnr = cursor.getString(5);
			model.fbr = cursor.getString(6);
			model.fbrxm = cursor.getString(7);
			model.fbrbm = cursor.getString(8);
			model.fbrbmmc = cursor.getString(9);
			model.fbsj = cursor.getString(10);
			model.hhs = cursor.getString(11);
			model.dzs = cursor.getString(12);
			model.zttp = cursor.getString(13);
			model.tplb = cursor.getString(14);
			model.wjlb = cursor.getString(15);
			model.remark = cursor.getString(16);
			model.create_time = cursor.getString(17);
			model.update_time = cursor.getString(18);
			model.deltag = cursor.getString(19);
			model.oprybh = cursor.getString(20);
			model.xxztcode = cursor.getString(21);
			model.xxztlb = cursor.getString(22);
			model.xxztzt = cursor.getString(23);
			try {
				model.nrlb = cursor.getString(24);
				model.gldm = cursor.getString(25);
				model.endTime = cursor.getString(26);
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

	public synchronized void insert(NoticeInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("noticeInfoId", model.id);
		values.put("lmdm", model.lmdm);
		values.put("lmmc", model.lmmc);
		values.put("xxzt", model.xxzt);
		values.put("xxnr", model.xxnr);
		values.put("fbr", model.fbr);
		values.put("fbrxm", model.fbrxm);
		values.put("fbrbm", model.fbrbm);
		values.put("fbrbmmc", model.fbrbmmc);
		values.put("fbsj", model.fbsj);
		values.put("hhs", model.hhs);
		values.put("dzs", model.dzs);
		values.put("zttp", model.zttp);
		values.put("tplb", model.tplb);
		values.put("wjlb", model.wjlb);
		values.put("remark", model.remark);
		values.put("create_time", model.create_time);
		values.put("update_time", model.update_time);
		values.put("deltag", model.deltag);
		values.put("oprybh", model.oprybh);
		values.put("xxztcode", model.xxztcode);
		values.put("xxztlb", model.xxztlb);
		values.put("xxztzt", model.xxztzt);
		try {
			values.put("nrlb", model.nrlb);
			values.put("gldm", model.gldm);
			values.put("endTime", model.endTime);
			sqLiteDatabase.replace(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String id, NoticeInfoModel model) {
		initDB();
		String whereClause = "noticeInfoId = " + id;

		ContentValues values = new ContentValues();
		values.put("noticeInfoId", model.id);
		values.put("lmdm", model.lmdm);
		values.put("lmmc", model.lmmc);
		values.put("xxzt", model.xxzt);
		values.put("xxnr", model.xxnr);
		values.put("fbr", model.fbr);
		values.put("fbrxm", model.fbrxm);
		values.put("fbrbm", model.fbrbm);
		values.put("fbrbmmc", model.fbrbmmc);
		values.put("fbsj", model.fbsj);
		values.put("hhs", model.hhs);
		values.put("dzs", model.dzs);
		values.put("zttp", model.zttp);
		values.put("tplb", model.tplb);
		values.put("wjlb", model.wjlb);
		values.put("remark", model.remark);
		values.put("create_time", model.create_time);
		values.put("update_time", model.update_time);
		values.put("deltag", model.deltag);
		values.put("oprybh", model.oprybh);
		values.put("xxztcode", model.xxztcode);
		values.put("xxztlb", model.xxztlb);
		values.put("xxztzt", model.xxztzt);
		try {
			values.put("nrlb", model.nrlb);
			values.put("gldm", model.gldm);
			values.put("endTime", model.endTime);
			sqLiteDatabase.update(TABLE, values, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete(String id) {
		initDB();
		String sql = "delete from " + TABLE + " where noticeInfoId = '" + id
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
