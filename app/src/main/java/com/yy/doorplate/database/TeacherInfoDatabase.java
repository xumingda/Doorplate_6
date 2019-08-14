package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.TeacherInfoModel;

public class TeacherInfoDatabase {

	private static final String TABLE = "TeacherInfo";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "TeacherInfoDatabase";

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
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT," + "xm verchar(20),"
				+ "teacherId verchar(10)," + "xb verchar(10),"
				+ "date verchar(10)," + "zgxlmc verchar(10),"
				+ "zgxwmc verchar(10)," + "byxx verchar(20),"
				+ "byzy verchar(20)," + "jxqk verchar(200),"
				+ "kyqk verchar(200)," + "zp verchar(100),"
				+ "jgzc verchar(20)," + "jgjl verchar(5),"
				+ "rybh verchar(20) UNIQUE" + ")";
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

	public synchronized List<TeacherInfoModel> query_all() {
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
		List<TeacherInfoModel> list = new ArrayList<TeacherInfoModel>();
		while (cursor.moveToNext()) {
			TeacherInfoModel model = new TeacherInfoModel();
			model.id = cursor.getString(2);
			model.xm = cursor.getString(1);
			model.xb = cursor.getString(3);
			model.date = cursor.getString(4);
			model.zgxlmc = cursor.getString(5);
			model.zgxwmc = cursor.getString(6);
			model.byxx = cursor.getString(7);
			model.byzy = cursor.getString(8);
			model.jxqk = cursor.getString(9);
			model.kyqk = cursor.getString(10);
			model.zp = cursor.getString(11);
			try {
				model.jgzc = cursor.getString(12);
				model.jgjl = cursor.getString(13);
				model.rybh = cursor.getString(14);
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

	public synchronized TeacherInfoModel query_by_id(String id) {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, "rybh = ?",
					new String[] { id }, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		if (cursor.moveToFirst()) {
			TeacherInfoModel model = new TeacherInfoModel();
			model.id = cursor.getString(2);
			model.xm = cursor.getString(1);
			model.xb = cursor.getString(3);
			model.date = cursor.getString(4);
			model.zgxlmc = cursor.getString(5);
			model.zgxwmc = cursor.getString(6);
			model.byxx = cursor.getString(7);
			model.byzy = cursor.getString(8);
			model.jxqk = cursor.getString(9);
			model.kyqk = cursor.getString(10);
			model.zp = cursor.getString(11);
			try {
				model.jgzc = cursor.getString(12);
				model.jgjl = cursor.getString(13);
				model.rybh = cursor.getString(14);
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

	public synchronized void insert(TeacherInfoModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("teacherId", model.id);
		values.put("xm", model.xm);
		values.put("xb", model.xb);
		values.put("date", model.date);
		values.put("zgxlmc", model.zgxlmc);
		values.put("zgxwmc", model.zgxwmc);
		values.put("byxx", model.byxx);
		values.put("byzy", model.byzy);
		values.put("jxqk", model.jxqk);
		values.put("kyqk", model.kyqk);
		values.put("zp", model.zp);
		try {
			values.put("jgzc", model.jgzc);
			values.put("jgjl", model.jgjl);
			values.put("rybh", model.rybh);
			sqLiteDatabase.insert(TABLE, null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public synchronized void update(String id, TeacherInfoModel model) {
		initDB();
		String whereClause = "teacherId = " + id;

		ContentValues values = new ContentValues();
		values.put("teacherId", model.id);
		values.put("xm", model.xm);
		values.put("xb", model.xb);
		values.put("date", model.date);
		values.put("zgxlmc", model.zgxlmc);
		values.put("zgxwmc", model.zgxwmc);
		values.put("byxx", model.byxx);
		values.put("byzy", model.byzy);
		values.put("jxqk", model.jxqk);
		values.put("kyqk", model.kyqk);
		values.put("zp", model.zp);
		try {
			values.put("jgzc", model.jgzc);
			values.put("jgjl", model.jgjl);
			values.put("rybh", model.rybh);
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
