package com.yy.doorplate.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.model.CarouselModel;

public class CarouselDatabase {

	private static final String TABLE = "Carousel";
	private SQLiteDatabase sqLiteDatabase;

	private static final String TAG = "CarouselDatabase";

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
				+ "dataSource verchar(20)," + "modelType verchar(20),"
				+ "publicDate verchar(20)," + "outDate verchar(20),"
				+ "photos verchar(20)" + ")";
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

	public synchronized List<CarouselModel> query(String selection,
			String[] selectionArgs) {
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
		List<CarouselModel> list = new ArrayList<CarouselModel>();
		while (cursor.moveToNext()) {
			CarouselModel model = new CarouselModel();
			model.dataSource = cursor.getString(1);
			model.modelType = cursor.getString(2);
			model.publicDate = cursor.getString(3);
			model.outDate = cursor.getString(4);
			try {
				model.photos = cursor.getString(5);
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

	public synchronized void insert(CarouselModel model) {
		initDB();
		ContentValues values = new ContentValues();
		values.put("dataSource", model.dataSource);
		values.put("modelType", model.modelType);
		values.put("publicDate", model.publicDate);
		values.put("outDate", model.outDate);
		try {
			values.put("photos", model.photos);
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
