package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.CurriculumInfoModel;

public class CurriculumInfoDatabase {

	private static final String TABLE = "CurriculumInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "CurriculumInfoDatabase";

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
				+ "curriculumId verchar(20) UNIQUE," + "jxrwid verchar(20),"
				+ "zc verchar(2)," + "skrq verchar(50)," + "xqs verchar(2),"
				+ "jc verchar(20)," + "jcshow verchar(20),"
				+ "kcmc verchar(50)," + "sknl verchar(500),"
				+ "skfs verchar(2)," + "skfsmc verchar(20),"
				+ "zyts verchar(2)," + "zypgfs verchar(2),"
				+ "zypgfsmc verchar(20)," + "fzrszs verchar(50),"
				+ "syyqsbsl verchar(500)," + "skcdlx verchar(2),"
				+ "skcddm verchar(50)," + "skcdmc verchar(100),"
				+ "skjs verchar(10)," + "skjsxm verchar(20),"
				+ "skjszp verchar(100)," + "skbj verchar(20),"
				+ "skbjmc verchar(30)," + "syrs verchar(10),"
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

	public synchronized List<CurriculumInfoModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					"skrq asc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<CurriculumInfoModel> list = new ArrayList<CurriculumInfoModel>();
		while (cursor.moveToNext()) {
			CurriculumInfoModel model = new CurriculumInfoModel();
			model.id = cursor.getString(1);
			model.jxrwid = cursor.getString(2);
			model.zc = cursor.getString(3);
			model.skrq = cursor.getString(4);
			model.xqs = cursor.getString(5);
			model.jc = cursor.getString(6);
			model.jcshow = cursor.getString(7);
			model.kcmc = cursor.getString(8);
			model.sknl = cursor.getString(9);
			model.skfs = cursor.getString(10);
			model.skfsmc = cursor.getString(11);
			model.zyts = cursor.getString(12);
			model.zypgfs = cursor.getString(13);
			model.zypgfsmc = cursor.getString(14);
			model.fzrszs = cursor.getString(15);
			model.syyqsbsl = cursor.getString(16);
			model.skcdlx = cursor.getString(17);
			model.skcddm = cursor.getString(18);
			model.skcdmc = cursor.getString(19);
			model.skjs = cursor.getString(20);
			model.skjsxm = cursor.getString(21);
			model.skjszp = cursor.getString(22);
			model.skbj = cursor.getString(23);
			model.skbjmc = cursor.getString(24);
			model.syrs = cursor.getString(25);
			model.remark = cursor.getString(26);
			list.add(model);
		}
		close();
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		} else {
			cursor.close();
			return sort_by_skrq_jc(list);
		}
	}

	public synchronized List<CurriculumInfoModel> query_by_skrq(String skrq) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "skrq = ?",
					new String[] { skrq }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<CurriculumInfoModel> list = new ArrayList<CurriculumInfoModel>();
		while (cursor.moveToNext()) {
			CurriculumInfoModel model = new CurriculumInfoModel();
			model.id = cursor.getString(1);
			model.jxrwid = cursor.getString(2);
			model.zc = cursor.getString(3);
			model.skrq = cursor.getString(4);
			model.xqs = cursor.getString(5);
			model.jc = cursor.getString(6);
			model.jcshow = cursor.getString(7);
			model.kcmc = cursor.getString(8);
			model.sknl = cursor.getString(9);
			model.skfs = cursor.getString(10);
			model.skfsmc = cursor.getString(11);
			model.zyts = cursor.getString(12);
			model.zypgfs = cursor.getString(13);
			model.zypgfsmc = cursor.getString(14);
			model.fzrszs = cursor.getString(15);
			model.syyqsbsl = cursor.getString(16);
			model.skcdlx = cursor.getString(17);
			model.skcddm = cursor.getString(18);
			model.skcdmc = cursor.getString(19);
			model.skjs = cursor.getString(20);
			model.skjsxm = cursor.getString(21);
			model.skjszp = cursor.getString(22);
			model.skbj = cursor.getString(23);
			model.skbjmc = cursor.getString(24);
			model.syrs = cursor.getString(25);
			model.remark = cursor.getString(26);
			list.add(model);
		}
		close();
		if (cursor.getCount() == 0) {
			cursor.close();
			return null;
		} else {
			cursor.close();
			return sort_by_jc(list);
		}
	}

	public synchronized CurriculumInfoModel query_by_id(String id) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "curriculumId = ?",
					new String[] { id }, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			CurriculumInfoModel model = new CurriculumInfoModel();
			model.id = cursor.getString(1);
			model.jxrwid = cursor.getString(2);
			model.zc = cursor.getString(3);
			model.skrq = cursor.getString(4);
			model.xqs = cursor.getString(5);
			model.jc = cursor.getString(6);
			model.jcshow = cursor.getString(7);
			model.kcmc = cursor.getString(8);
			model.sknl = cursor.getString(9);
			model.skfs = cursor.getString(10);
			model.skfsmc = cursor.getString(11);
			model.zyts = cursor.getString(12);
			model.zypgfs = cursor.getString(13);
			model.zypgfsmc = cursor.getString(14);
			model.fzrszs = cursor.getString(15);
			model.syyqsbsl = cursor.getString(16);
			model.skcdlx = cursor.getString(17);
			model.skcddm = cursor.getString(18);
			model.skcdmc = cursor.getString(19);
			model.skjs = cursor.getString(20);
			model.skjsxm = cursor.getString(21);
			model.skjszp = cursor.getString(22);
			model.skbj = cursor.getString(23);
			model.skbjmc = cursor.getString(24);
			model.syrs = cursor.getString(25);
			model.remark = cursor.getString(26);
			close();
			return model;
		} else {
			close();
			return null;
		}
	}

	public synchronized void insert(CurriculumInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("curriculumId", model.id);
		values.put("jxrwid", model.jxrwid);
		values.put("zc", model.zc);
		values.put("skrq", model.skrq);
		values.put("xqs", model.xqs);
		values.put("jc", model.jc);
		values.put("jcshow", model.jcshow);
		values.put("kcmc", model.kcmc);
		values.put("sknl", model.sknl);
		values.put("skfs", model.skfs);
		values.put("skfsmc", model.skfsmc);
		values.put("zyts", model.zyts);
		values.put("zypgfs", model.zypgfs);
		values.put("zypgfsmc", model.zypgfsmc);
		values.put("fzrszs", model.fzrszs);
		values.put("syyqsbsl", model.syyqsbsl);
		values.put("skcdlx", model.skcdlx);
		values.put("skcddm", model.skcddm);
		values.put("skcdmc", model.skcdmc);
		values.put("skjszp", model.skjszp);
		values.put("skjs", model.skjs);
		values.put("skjsxm", model.skjsxm);
		values.put("skbj", model.skbj);
		values.put("skbjmc", model.skbjmc);
		values.put("syrs", model.syrs);
		values.put("remark", model.remark);
		try {
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String id, CurriculumInfoModel model) {
		initDB();
		String whereClause = "curriculumId = " + id;

		ContentValues values = new ContentValues();
		values.put("curriculumId", model.id);
		values.put("jxrwid", model.jxrwid);
		values.put("zc", model.zc);
		values.put("skrq", model.skrq);
		values.put("xqs", model.xqs);
		values.put("jc", model.jc);
		values.put("jcshow", model.jcshow);
		values.put("kcmc", model.kcmc);
		values.put("sknl", model.sknl);
		values.put("skfs", model.skfs);
		values.put("skfsmc", model.skfsmc);
		values.put("zyts", model.zyts);
		values.put("zypgfs", model.zypgfs);
		values.put("zypgfsmc", model.zypgfsmc);
		values.put("fzrszs", model.fzrszs);
		values.put("syyqsbsl", model.syyqsbsl);
		values.put("skcdlx", model.skcdlx);
		values.put("skcddm", model.skcddm);
		values.put("skcdmc", model.skcdmc);
		values.put("skjszp", model.skjszp);
		values.put("skjs", model.skjs);
		values.put("skjsxm", model.skjsxm);
		values.put("skbj", model.skbj);
		values.put("skbjmc", model.skbjmc);
		values.put("syrs", model.syrs);
		values.put("remark", model.remark);
		try {
			sqLiteDatabase.update(TABLE, values, whereClause, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void delete(String skrq) {
		initDB();
		String sql = "delete from " + TABLE + " where skrq = '" + skrq + "'";
		Log.d(TAG, sql);
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

	private List<CurriculumInfoModel> sort_by_skrq_jc(
			List<CurriculumInfoModel> list) {
		List<CurriculumInfoModel> result = new ArrayList<CurriculumInfoModel>();
		List<CurriculumInfoModel> list1 = null;
		String skrq = "";
		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				skrq = list.get(i).skrq;
				list1 = new ArrayList<CurriculumInfoModel>();
			}
			if (skrq.equals(list.get(i).skrq)) {
				list1.add(list.get(i));
			} else {
				result.addAll(sort_by_jc(list1));
				skrq = list.get(i).skrq;
				list1 = new ArrayList<CurriculumInfoModel>();
				list1.add(list.get(i));
			}
		}
		return result;
	}

	private List<CurriculumInfoModel> sort_by_jc(List<CurriculumInfoModel> list) {
		try {
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < list.size() - 1 - i; j++) {
					int a = Integer.parseInt(list.get(j).jc.split("-")[0]);
					int b = Integer.parseInt(list.get(j + 1).jc.split("-")[0]);
					if (a > b) {
						CurriculumInfoModel model = list.get(j);
						list.set(j, list.get(j + 1));
						list.set(j + 1, model);
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return list;
	}
}
