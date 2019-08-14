package com.yy.doorplate.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.CurriculumAdapter;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.model.CurriculumInfoModel;

public class CurriculumActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private final String TAG = "CurriculumActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private Button btn_curriculum_back;
	private LinearLayout ly_monday, ly_tuesday, ly_wednesday, ly_thursday,
			ly_friday, ly_saturday, ly_sunday;
	private RelativeLayout ly_spinner;
	private TextView txt_curriculum_title, txt_monday, txt_monday_date,
			txt_tuesday, txt_tuesday_date, txt_wednesday, txt_wednesday_date,
			txt_thursday, txt_thursday_date, txt_friday, txt_friday_date,
			txt_saturday, txt_saturday_date, txt_sunday, txt_sunday_date,
			txt_spinner, txt_curriculum_no;
	private View view_curriculum;
	private ListView list_curriculum;

	private CurriculumAdapter curriculumAdapter;

	private SimpleDateFormat format;
	private Calendar calendar = Calendar.getInstance();

	private Date date_kxsj, date_jssj, date_click, date_1, date_2, date_3,
			date_4, date_5, date_6, date_7;
	private int week_current;

	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_curriculum);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();

		initData(System.currentTimeMillis());
		format = new SimpleDateFormat("yyyy-MM-dd");
		CurriculumInfoDatabase database = new CurriculumInfoDatabase();
		Log.e("date_click","date_click:"+date_click);
		List<CurriculumInfoModel> list = database.query_by_skrq(format
				.format(date_click));
		if (list != null && list.size() > 0) {
			view_curriculum.setVisibility(View.VISIBLE);
			curriculumAdapter = new CurriculumAdapter(application, list);
			list_curriculum.setAdapter(curriculumAdapter);
			txt_curriculum_no.setVisibility(View.INVISIBLE);
		} else {
			view_curriculum.setVisibility(View.INVISIBLE);
			list_curriculum.setAdapter(null);
			txt_curriculum_no.setVisibility(View.VISIBLE);
		}

		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this, null, "加载中...", false,
					false);
		}
		format = new SimpleDateFormat("yyyy-MM-dd");
		application.httpProcess.QryCurriculum_week(
				application.equInfoModel.jssysdm, format.format(date_1)
						+ " 00:00:00", format.format(date_7) + " 23:59:59", "");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case KeyEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case MotionEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	private void initView() {
		btn_curriculum_back = (Button) findViewById(R.id.btn_curriculum_back);
		ly_monday = (LinearLayout) findViewById(R.id.ly_monday);
		ly_tuesday = (LinearLayout) findViewById(R.id.ly_tuesday);
		ly_wednesday = (LinearLayout) findViewById(R.id.ly_wednesday);
		ly_thursday = (LinearLayout) findViewById(R.id.ly_thursday);
		ly_friday = (LinearLayout) findViewById(R.id.ly_friday);
		ly_saturday = (LinearLayout) findViewById(R.id.ly_saturday);
		ly_sunday = (LinearLayout) findViewById(R.id.ly_sunday);
		ly_spinner = (RelativeLayout) findViewById(R.id.ly_spinner);
		txt_curriculum_title = (TextView) findViewById(R.id.txt_curriculum_title);
		txt_monday = (TextView) findViewById(R.id.txt_monday);
		txt_monday_date = (TextView) findViewById(R.id.txt_monday_date);
		txt_tuesday = (TextView) findViewById(R.id.txt_tuesday);
		txt_tuesday_date = (TextView) findViewById(R.id.txt_tuesday_date);
		txt_wednesday = (TextView) findViewById(R.id.txt_wednesday);
		txt_wednesday_date = (TextView) findViewById(R.id.txt_wednesday_date);
		txt_thursday = (TextView) findViewById(R.id.txt_thursday);
		txt_thursday_date = (TextView) findViewById(R.id.txt_thursday_date);
		txt_friday = (TextView) findViewById(R.id.txt_friday);
		txt_friday_date = (TextView) findViewById(R.id.txt_friday_date);
		txt_saturday = (TextView) findViewById(R.id.txt_saturday);
		txt_saturday_date = (TextView) findViewById(R.id.txt_saturday_date);
		txt_sunday = (TextView) findViewById(R.id.txt_sunday);
		txt_sunday_date = (TextView) findViewById(R.id.txt_sunday_date);
		txt_spinner = (TextView) findViewById(R.id.txt_spinner);
		txt_curriculum_no = (TextView) findViewById(R.id.txt_curriculum_no);
		view_curriculum = findViewById(R.id.view_curriculum);
		list_curriculum = (ListView) findViewById(R.id.list_curriculum);

		btn_curriculum_back.setOnClickListener(this);
		ly_monday.setOnClickListener(this);
		ly_tuesday.setOnClickListener(this);
		ly_wednesday.setOnClickListener(this);
		ly_thursday.setOnClickListener(this);
		ly_friday.setOnClickListener(this);
		ly_saturday.setOnClickListener(this);
		ly_sunday.setOnClickListener(this);
		ly_spinner.setOnClickListener(this);
		list_curriculum.setOnItemClickListener(this);

		if (!TextUtils.isEmpty(application.dqxq)
				&& application.dqxq.equals("1")) {
			txt_curriculum_title.setText(/*
										 * application.equInfoModel.jssysmc +
										 */"课程表 ( " + application.dqxn
					+ " ) 第一学期");
		} else if (!TextUtils.isEmpty(application.dqxq)
				&& application.dqxq.equals("2")) {
			txt_curriculum_title.setText(/*
										 * application.equInfoModel.jssysmc +
										 */"课程表 ( " + application.dqxn
					+ " ) 第二学期");
		} else {
			txt_curriculum_title.setText(/*
										 * application.equInfoModel.jssysmc +
										 */"课程表 ( " + application.dqxn + " )");
		}
	}

	private void initData(long now) {
		view_curriculum.setVisibility(View.INVISIBLE);
		txt_curriculum_no.setVisibility(View.VISIBLE);
		list_curriculum.setAdapter(null);
		try {
			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			date_kxsj = format.parse(application.kxsj);
			date_jssj = format.parse(application.jssj);
			if (date_kxsj.getTime() <= now && now <= date_jssj.getTime()) {
				date_click = new Date(now);
				week_current = Math.abs(getGapCount(date_click, date_kxsj) / 7) + 1;
			} else {
				date_click = date_kxsj;
				week_current = 1;
			}
			calendar.setTime(date_click);
			if (calendar.get(Calendar.DAY_OF_WEEK) == 2) {
				date_1 = date_click;
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				date_2 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				date_3 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 3);
				date_4 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 4);
				date_5 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 5);
				date_6 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 6);
				date_7 = calendar.getTime();
				ui_monday();
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == 3) {
				date_2 = date_click;
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				date_1 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				date_3 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				date_4 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 3);
				date_5 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 4);
				date_6 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 5);
				date_7 = calendar.getTime();
				ui_tuesday();
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == 4) {
				date_3 = date_click;
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -2);
				date_1 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				date_2 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				date_4 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				date_5 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 3);
				date_6 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 4);
				date_7 = calendar.getTime();
				ui_wednesday();
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == 5) {
				date_4 = date_click;
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -3);
				date_1 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -2);
				date_2 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				date_3 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				date_5 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				date_6 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 3);
				date_7 = calendar.getTime();
				ui_thursday();
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == 6) {
				date_5 = date_click;
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -4);
				date_1 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -3);
				date_2 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -2);
				date_3 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				date_4 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				date_6 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 2);
				date_7 = calendar.getTime();
				ui_friday();
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
				date_6 = date_click;
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -5);
				date_1 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -4);
				date_2 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -3);
				date_3 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -2);
				date_4 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				date_5 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				date_7 = calendar.getTime();
				ui_saturday();
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
				date_7 = date_click;
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -6);
				date_1 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -5);
				date_2 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -4);
				date_3 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -3);
				date_4 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -2);
				date_5 = calendar.getTime();
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				date_6 = calendar.getTime();
				ui_sunday();
			}
			format = new SimpleDateFormat("MM月dd日");
			txt_monday_date.setText(format.format(date_1));
			txt_tuesday_date.setText(format.format(date_2));
			txt_wednesday_date.setText(format.format(date_3));
			txt_thursday_date.setText(format.format(date_4));
			txt_friday_date.setText(format.format(date_5));
			txt_saturday_date.setText(format.format(date_6));
			txt_sunday_date.setText(format.format(date_7));
			txt_spinner.setText("第 " + week_current + " 周");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_curriculum_back:
			finish();
			break;
		case R.id.ly_monday: {
			ui_monday();
			format = new SimpleDateFormat("yyyy-MM-dd");
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_by_skrq(format
					.format(date_1));
			if (list != null && list.size() > 0) {
				view_curriculum.setVisibility(View.VISIBLE);
				curriculumAdapter = new CurriculumAdapter(application, list);
				list_curriculum.setAdapter(curriculumAdapter);
				txt_curriculum_no.setVisibility(View.INVISIBLE);
			} else {
				view_curriculum.setVisibility(View.INVISIBLE);
				list_curriculum.setAdapter(null);
				txt_curriculum_no.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.ly_tuesday: {
			ui_tuesday();
			format = new SimpleDateFormat("yyyy-MM-dd");
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_by_skrq(format
					.format(date_2));
			if (list != null && list.size() > 0) {
				view_curriculum.setVisibility(View.VISIBLE);
				curriculumAdapter = new CurriculumAdapter(application, list);
				list_curriculum.setAdapter(curriculumAdapter);
				txt_curriculum_no.setVisibility(View.INVISIBLE);
			} else {
				view_curriculum.setVisibility(View.INVISIBLE);
				list_curriculum.setAdapter(null);
				txt_curriculum_no.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.ly_wednesday: {
			ui_wednesday();
			format = new SimpleDateFormat("yyyy-MM-dd");
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_by_skrq(format
					.format(date_3));
			if (list != null && list.size() > 0) {
				view_curriculum.setVisibility(View.VISIBLE);
				curriculumAdapter = new CurriculumAdapter(application, list);
				list_curriculum.setAdapter(curriculumAdapter);
				txt_curriculum_no.setVisibility(View.INVISIBLE);
			} else {
				view_curriculum.setVisibility(View.INVISIBLE);
				list_curriculum.setAdapter(null);
				txt_curriculum_no.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.ly_thursday: {
			ui_thursday();
			format = new SimpleDateFormat("yyyy-MM-dd");
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_by_skrq(format
					.format(date_4));
			if (list != null && list.size() > 0) {
				view_curriculum.setVisibility(View.VISIBLE);
				curriculumAdapter = new CurriculumAdapter(application, list);
				list_curriculum.setAdapter(curriculumAdapter);
				txt_curriculum_no.setVisibility(View.INVISIBLE);
			} else {
				view_curriculum.setVisibility(View.INVISIBLE);
				list_curriculum.setAdapter(null);
				txt_curriculum_no.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.ly_friday: {
			ui_friday();
			format = new SimpleDateFormat("yyyy-MM-dd");
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_by_skrq(format
					.format(date_5));
			if (list != null && list.size() > 0) {
				view_curriculum.setVisibility(View.VISIBLE);
				curriculumAdapter = new CurriculumAdapter(application, list);
				list_curriculum.setAdapter(curriculumAdapter);
				txt_curriculum_no.setVisibility(View.INVISIBLE);
			} else {
				view_curriculum.setVisibility(View.INVISIBLE);
				list_curriculum.setAdapter(null);
				txt_curriculum_no.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.ly_saturday: {
			ui_saturday();
			format = new SimpleDateFormat("yyyy-MM-dd");
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_by_skrq(format
					.format(date_6));
			if (list != null && list.size() > 0) {
				view_curriculum.setVisibility(View.VISIBLE);
				curriculumAdapter = new CurriculumAdapter(application, list);
				list_curriculum.setAdapter(curriculumAdapter);
				txt_curriculum_no.setVisibility(View.INVISIBLE);
			} else {
				view_curriculum.setVisibility(View.INVISIBLE);
				list_curriculum.setAdapter(null);
				txt_curriculum_no.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.ly_sunday: {
			ui_sunday();
			format = new SimpleDateFormat("yyyy-MM-dd");
			CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			List<CurriculumInfoModel> list = database.query_by_skrq(format
					.format(date_7));
			if (list != null && list.size() > 0) {
				view_curriculum.setVisibility(View.VISIBLE);
				curriculumAdapter = new CurriculumAdapter(application, list);
				list_curriculum.setAdapter(curriculumAdapter);
				txt_curriculum_no.setVisibility(View.INVISIBLE);
			} else {
				view_curriculum.setVisibility(View.INVISIBLE);
				list_curriculum.setAdapter(null);
				txt_curriculum_no.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.ly_spinner: {
			showDialogWeek();
			break;
		}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Log.d(TAG, "-----onItemClick-----");
		if (arg0.getId() == R.id.list_dialog_week) {
			txt_curriculum_no.setVisibility(View.INVISIBLE);
			txt_spinner.setText("第 " + (arg2 + 1) + " 周");
			application.dialog.dismiss();
			if (week_current != arg2 + 1) {
				calendar.setTime(date_click);
				calendar.add(Calendar.DAY_OF_MONTH,
						(1 + arg2 - week_current) * 7);
				date_click = calendar.getTime();
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(this, null, "加载中...",
							false, false);
				}
				if (date_click.getTime() > date_jssj.getTime()) {
					date_click = date_jssj;
				}
				initData(date_click.getTime());
				format = new SimpleDateFormat("yyyy-MM-dd");
				application.httpProcess.QryCurriculum_week(
						application.equInfoModel.jssysdm, format.format(date_1)
								+ " 00:00:00", format.format(date_7)
								+ " 23:59:59", "");
			}
		} else if (arg0.getId() == R.id.list_curriculum) {
			Intent intent = new Intent(this, AttendInfoActivity.class);
			intent.putExtra("skjhid", curriculumAdapter.getList().get(arg2).id);
			startActivity(intent);
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			// if (!application.getRunningActivityName().equals(
			// "com.yy.doorplate.CurriculumActivity")) {
			// return;
			// }
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.QRY_CURRICULUM + "week")
					&& !application.isGrxx) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				Log.d(TAG, tag + " " + msg);
				if (result) {
				} else {
					application.showToast(msg);
				}
				format = new SimpleDateFormat("yyyy-MM-dd");
				CurriculumInfoDatabase database = new CurriculumInfoDatabase();
				List<CurriculumInfoModel> list = database.query_by_skrq(format
						.format(date_click));
				if (list != null && list.size() > 0) {
					view_curriculum.setVisibility(View.VISIBLE);
					curriculumAdapter = new CurriculumAdapter(application, list);
					list_curriculum.setAdapter(curriculumAdapter);
					txt_curriculum_no.setVisibility(View.INVISIBLE);
				} else {
					view_curriculum.setVisibility(View.INVISIBLE);
					list_curriculum.setAdapter(null);
					txt_curriculum_no.setVisibility(View.VISIBLE);
				}
				progressDialog.dismiss();
				progressDialog = null;
			} else if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

	private void ui_monday() {
		ly_monday.setBackgroundResource(R.drawable.ly_curriculum_click);
		ly_tuesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_wednesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_thursday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_friday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_saturday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_sunday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		txt_monday.setTextColor(getResources().getColor(R.color.white));
		txt_monday_date.setTextColor(getResources().getColor(R.color.white));
		txt_tuesday.setTextColor(getResources().getColor(R.color.black));
		txt_tuesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_wednesday.setTextColor(getResources().getColor(R.color.black));
		txt_wednesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_thursday.setTextColor(getResources().getColor(R.color.black));
		txt_thursday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_friday.setTextColor(getResources().getColor(R.color.black));
		txt_friday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_saturday.setTextColor(getResources().getColor(R.color.black));
		txt_saturday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_sunday.setTextColor(getResources().getColor(R.color.black));
		txt_sunday_date.setTextColor(getResources().getColor(R.color.gray));
	}

	private void ui_tuesday() {
		ly_monday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_tuesday.setBackgroundResource(R.drawable.ly_curriculum_click);
		ly_wednesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_thursday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_friday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_saturday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_sunday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		txt_monday.setTextColor(getResources().getColor(R.color.black));
		txt_monday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_tuesday.setTextColor(getResources().getColor(R.color.white));
		txt_tuesday_date.setTextColor(getResources().getColor(R.color.white));
		txt_wednesday.setTextColor(getResources().getColor(R.color.black));
		txt_wednesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_thursday.setTextColor(getResources().getColor(R.color.black));
		txt_thursday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_friday.setTextColor(getResources().getColor(R.color.black));
		txt_friday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_saturday.setTextColor(getResources().getColor(R.color.black));
		txt_saturday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_sunday.setTextColor(getResources().getColor(R.color.black));
		txt_sunday_date.setTextColor(getResources().getColor(R.color.gray));
	}

	private void ui_wednesday() {
		ly_monday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_tuesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_wednesday.setBackgroundResource(R.drawable.ly_curriculum_click);
		ly_thursday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_friday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_saturday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_sunday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		txt_monday.setTextColor(getResources().getColor(R.color.black));
		txt_monday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_tuesday.setTextColor(getResources().getColor(R.color.black));
		txt_tuesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_wednesday.setTextColor(getResources().getColor(R.color.white));
		txt_wednesday_date.setTextColor(getResources().getColor(R.color.white));
		txt_thursday.setTextColor(getResources().getColor(R.color.black));
		txt_thursday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_friday.setTextColor(getResources().getColor(R.color.black));
		txt_friday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_saturday.setTextColor(getResources().getColor(R.color.black));
		txt_saturday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_sunday.setTextColor(getResources().getColor(R.color.black));
		txt_sunday_date.setTextColor(getResources().getColor(R.color.gray));
	}

	private void ui_thursday() {
		ly_monday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_tuesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_wednesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_thursday.setBackgroundResource(R.drawable.ly_curriculum_click);
		ly_friday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_saturday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_sunday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		txt_monday.setTextColor(getResources().getColor(R.color.black));
		txt_monday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_tuesday.setTextColor(getResources().getColor(R.color.black));
		txt_tuesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_wednesday.setTextColor(getResources().getColor(R.color.black));
		txt_wednesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_thursday.setTextColor(getResources().getColor(R.color.white));
		txt_thursday_date.setTextColor(getResources().getColor(R.color.white));
		txt_friday.setTextColor(getResources().getColor(R.color.black));
		txt_friday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_saturday.setTextColor(getResources().getColor(R.color.black));
		txt_saturday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_sunday.setTextColor(getResources().getColor(R.color.black));
		txt_sunday_date.setTextColor(getResources().getColor(R.color.gray));
	}

	private void ui_friday() {
		ly_monday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_tuesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_wednesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_thursday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_friday.setBackgroundResource(R.drawable.ly_curriculum_click);
		ly_saturday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_sunday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		txt_monday.setTextColor(getResources().getColor(R.color.black));
		txt_monday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_tuesday.setTextColor(getResources().getColor(R.color.black));
		txt_tuesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_wednesday.setTextColor(getResources().getColor(R.color.black));
		txt_wednesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_thursday.setTextColor(getResources().getColor(R.color.black));
		txt_thursday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_friday.setTextColor(getResources().getColor(R.color.white));
		txt_friday_date.setTextColor(getResources().getColor(R.color.white));
		txt_saturday.setTextColor(getResources().getColor(R.color.black));
		txt_saturday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_sunday.setTextColor(getResources().getColor(R.color.black));
		txt_sunday_date.setTextColor(getResources().getColor(R.color.gray));
	}

	private void ui_saturday() {
		ly_monday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_tuesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_wednesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_thursday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_friday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_saturday.setBackgroundResource(R.drawable.ly_curriculum_click);
		ly_sunday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		txt_monday.setTextColor(getResources().getColor(R.color.black));
		txt_monday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_tuesday.setTextColor(getResources().getColor(R.color.black));
		txt_tuesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_wednesday.setTextColor(getResources().getColor(R.color.black));
		txt_wednesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_thursday.setTextColor(getResources().getColor(R.color.black));
		txt_thursday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_friday.setTextColor(getResources().getColor(R.color.black));
		txt_friday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_saturday.setTextColor(getResources().getColor(R.color.white));
		txt_saturday_date.setTextColor(getResources().getColor(R.color.white));
		txt_sunday.setTextColor(getResources().getColor(R.color.black));
		txt_sunday_date.setTextColor(getResources().getColor(R.color.gray));
	}

	private void ui_sunday() {
		ly_monday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_tuesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_wednesday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_thursday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_friday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_saturday.setBackgroundColor(getResources().getColor(
				R.color.bottom_color));
		ly_sunday.setBackgroundResource(R.drawable.ly_curriculum_click);
		txt_monday.setTextColor(getResources().getColor(R.color.black));
		txt_monday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_tuesday.setTextColor(getResources().getColor(R.color.black));
		txt_tuesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_wednesday.setTextColor(getResources().getColor(R.color.black));
		txt_wednesday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_thursday.setTextColor(getResources().getColor(R.color.black));
		txt_thursday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_friday.setTextColor(getResources().getColor(R.color.black));
		txt_friday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_saturday.setTextColor(getResources().getColor(R.color.black));
		txt_saturday_date.setTextColor(getResources().getColor(R.color.gray));
		txt_sunday.setTextColor(getResources().getColor(R.color.white));
		txt_sunday_date.setTextColor(getResources().getColor(R.color.white));
	}

	private int getGapCount(Date startDate, Date endDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	private ListView list_dialog_week;

	private void showDialogWeek() {
		AlertDialog.Builder builder = new AlertDialog.Builder(application);
		application.dialog = builder.create();
		application.dialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		application.dialog.show();
		application.dialog.getWindow().setLayout(220, 700);
		application.dialog.getWindow().setContentView(R.layout.dialog_week);
		WindowManager.LayoutParams lp = application.dialog.getWindow()
				.getAttributes();
		lp.x = 755;
		lp.y = -90;
		application.dialog.getWindow().setAttributes(lp);
		list_dialog_week = (ListView) application.dialog
				.findViewById(R.id.list_dialog_week);

		int zs = Integer.parseInt(application.zs);
		List<Map<String, String>> weeks = new ArrayList<Map<String, String>>();
		for (int i = 1; i <= zs; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("week", "第 " + i + " 周");
			weeks.add(map);
		}

		list_dialog_week.setAdapter(new SimpleAdapter(application, weeks,
				R.layout.item_week, new String[] { "week" },
				new int[] { R.id.txt_item_week }));
		list_dialog_week.setSelection(week_current - 1);
		list_dialog_week.setOnItemClickListener(this);
	}

}
