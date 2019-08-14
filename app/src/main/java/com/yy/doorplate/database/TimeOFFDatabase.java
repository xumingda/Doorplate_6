package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.TimerTypeModel;

public class TimeOFFDatabase {

	private static final String TABLE = "TimeOFF";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "TimeOFFDatabase";

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
				+ "timerType verchar(10)," + "time verchar(10),"
				+ "repeatWeek verchar(5)," + "date verchar(10)" + ")";
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

	public synchronized List<TimerTypeModel> query_all() {
		initDB();
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.query(TABLE, null, null, null, null, null,
					"date,repeatWeek,time asc", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor == null) {
			return null;
		}
		List<TimerTypeModel> list = new ArrayList<TimerTypeModel>();
		while (cursor.moveToNext()) {
			TimerTypeModel model = new TimerTypeModel();
			model.timerType = cursor.getString(1);
			model.time = cursor.getString(2);
			model.repeatWeek = cursor.getString(3);
			model.date = cursor.getString(4);
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

	public synchronized void insert(TimerTypeModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("timerType", model.timerType);
		values.put("time", model.time);
		values.put("date", model.date);
		values.put("repeatWeek", model.repeatWeek);
		if (!TextUtils.isEmpty(model.repeatWeek)) {
			model.repeatWeek = model.repeatWeek.replace("Mo", "1");
			model.repeatWeek = model.repeatWeek.replace("Tu", "2");
			model.repeatWeek = model.repeatWeek.replace("We", "3");
			model.repeatWeek = model.repeatWeek.replace("Th", "4");
			model.repeatWeek = model.repeatWeek.replace("Fr", "5");
			model.repeatWeek = model.repeatWeek.replace("Sa", "6");
			model.repeatWeek = model.repeatWeek.replace("Su", "7");
			String[] repeatWeek = model.repeatWeek.split(",");
			if (repeatWeek.length > 0) {
				for (String s : repeatWeek) {
					if (!TextUtils.isEmpty(s)) {
						values.put("repeatWeek", s);
						try {
							sqLiteDatabase.insert(TABLE, null, values);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				try {
					sqLiteDatabase.insert(TABLE, null, values);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				sqLiteDatabase.insert(TABLE, null, values);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
