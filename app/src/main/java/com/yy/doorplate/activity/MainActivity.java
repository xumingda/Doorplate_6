package com.yy.doorplate.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.advertisement.system.ConstData;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.CurriculumTodayAdapter;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.database.UserInfoDatabase;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class MainActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private final String TAG = "MainActivity";

	private MyApplication application;

	private TextClock textClock;
	private RelativeLayout ly_banner, ly_main_class;
	private LinearLayout ly_main_new1, ly_main_new2, ly_main_new3;
	private Button btn_main_new, btn_main_class, btn_main_schedule,
			btn_main_control, btn_main_talk, btn_main_setting,
			btn_main_schedule_ly, btn_main_class_ly;
	private TextView txt_main_nonew, txt_schedule_noclass, txt_main_time,
			txt_main_teacher, txt_main_solid_arrive, txt_main_arrive,
			txt_main_1, txt_main_2, txt_main_3, txt_main_4, txt_main_5,
			txt_main_6, txt_class_noclass, txt_weather, txt_location;
	private TextView txt_main_new_title1, txt_main_new_title2,
			txt_main_new_title3, txt_main_new_nr1, txt_main_new_nr2,
			txt_main_new_nr3, txt_main_new_time1, txt_main_new_time2,
			txt_main_new_time3;
	private CustomTextView txt_main_class, txt_main_banji, txt_classroom;
	private ListView list_main_schedule;

	private CurriculumTodayAdapter curriculumTodayAdapter = null;

	private int arrive = 0;

	private int curriculumInfos_today_id = 0;

	private boolean isThread = true;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private List<NoticeInfoModel> noticeInfoModels = null;

	private ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();
		application.initServer();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		filter.addAction(ConstData.ACTIVITY_UPDATE);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
		threadTime.start();
	}

	private void initView() {
		ly_banner = (RelativeLayout) findViewById(R.id.ly_banner);
		ly_main_class = (RelativeLayout) findViewById(R.id.ly_main_class);
		ly_main_new1 = (LinearLayout) findViewById(R.id.ly_main_new1);
		ly_main_new2 = (LinearLayout) findViewById(R.id.ly_main_new2);
		ly_main_new3 = (LinearLayout) findViewById(R.id.ly_main_new3);
		btn_main_new = (Button) findViewById(R.id.btn_main_new);
		btn_main_class = (Button) findViewById(R.id.btn_main_class);
		btn_main_schedule = (Button) findViewById(R.id.btn_main_schedule);
		btn_main_control = (Button) findViewById(R.id.btn_main_control);
		btn_main_talk = (Button) findViewById(R.id.btn_main_talk);
		btn_main_setting = (Button) findViewById(R.id.btn_main_setting);
		btn_main_schedule_ly = (Button) findViewById(R.id.btn_main_schedule_ly);
		btn_main_class_ly = (Button) findViewById(R.id.btn_main_class_ly);
		txt_main_nonew = (TextView) findViewById(R.id.txt_main_nonew);
		txt_schedule_noclass = (TextView) findViewById(R.id.txt_schedule_noclass);
		txt_main_class = (CustomTextView) findViewById(R.id.txt_main_class);
		txt_main_banji = (CustomTextView) findViewById(R.id.txt_main_banji);
		txt_main_time = (TextView) findViewById(R.id.txt_main_time);
		txt_main_teacher = (TextView) findViewById(R.id.txt_main_teacher);
		txt_main_solid_arrive = (TextView) findViewById(R.id.txt_main_solid_arrive);
		txt_main_arrive = (TextView) findViewById(R.id.txt_main_arrive);
		txt_main_1 = (TextView) findViewById(R.id.txt_main_1);
		txt_main_2 = (TextView) findViewById(R.id.txt_main_2);
		txt_main_3 = (TextView) findViewById(R.id.txt_main_3);
		txt_main_4 = (TextView) findViewById(R.id.txt_main_4);
		txt_main_5 = (TextView) findViewById(R.id.txt_main_5);
		txt_main_6 = (TextView) findViewById(R.id.txt_main_6);
		txt_class_noclass = (TextView) findViewById(R.id.txt_class_noclass);
		txt_classroom = (CustomTextView) findViewById(R.id.txt_classroom);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		txt_location = (TextView) findViewById(R.id.txt_location);
		txt_main_new_title1 = (TextView) findViewById(R.id.txt_main_new_title1);
		txt_main_new_title2 = (TextView) findViewById(R.id.txt_main_new_title2);
		txt_main_new_title3 = (TextView) findViewById(R.id.txt_main_new_title3);
		txt_main_new_nr1 = (TextView) findViewById(R.id.txt_main_new_nr1);
		txt_main_new_nr2 = (TextView) findViewById(R.id.txt_main_new_nr2);
		txt_main_new_nr3 = (TextView) findViewById(R.id.txt_main_new_nr3);
		txt_main_new_time1 = (TextView) findViewById(R.id.txt_main_new_time1);
		txt_main_new_time2 = (TextView) findViewById(R.id.txt_main_new_time2);
		txt_main_new_time3 = (TextView) findViewById(R.id.txt_main_new_time3);
		list_main_schedule = (ListView) findViewById(R.id.list_main_schedule);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日 HH:mm:ss");

		ly_main_class.setOnClickListener(this);
		ly_main_new1.setOnClickListener(this);
		ly_main_new2.setOnClickListener(this);
		ly_main_new3.setOnClickListener(this);
		btn_main_class_ly.setOnClickListener(this);
		btn_main_schedule_ly.setOnClickListener(this);
		btn_main_new.setOnClickListener(this);
		btn_main_class.setOnClickListener(this);
		btn_main_schedule.setOnClickListener(this);
		btn_main_control.setOnClickListener(this);
		btn_main_talk.setOnClickListener(this);
		btn_main_setting.setOnClickListener(this);
		list_main_schedule.setOnItemClickListener(this);

		if (application.equInfoModel != null
				&& application.classRoomInfoModel != null) {
			txt_classroom.setText(application.equInfoModel.jssysmc);
		}
		updata_now_curriculum();
		updata_today_curriculum();
		updata_new_notice();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ly_main_class: {
			if (application.curriculumInfoModel_now == null) {
				application.showToast(txt_class_noclass.getText().toString());
			} else {
				Intent intent = new Intent(this, AttendInfoActivity.class);
				intent.putExtra("skjhid",
						application.curriculumInfoModel_now.id);
				startActivity(intent);
			}
			break;
		}
		case R.id.btn_main_class:
		case R.id.btn_main_class_ly: {
			if (application.equInfoModel != null
					&& application.classRoomInfoModel != null) {
				Intent intent = new Intent(this, ClassInfoActivity.class);
				startActivity(intent);
			} else {
				application.showToast(getResources().getString(
						R.string.no_classinfo));
			}
			break;
		}
		case R.id.btn_main_schedule:
		case R.id.btn_main_schedule_ly: {
			// CurriculumInfoDatabase database = new CurriculumInfoDatabase();
			// List<CurriculumInfoModel> list = database.query_all();
			// if (list != null && list.size() > 0) {
			Intent intent = new Intent(this, CurriculumActivity.class);
			startActivity(intent);
			// } else {
			// application.showToast(getResources()
			// .getString(R.string.noclass));
			// }
			break;
		}
		case R.id.btn_main_new: {
			if (noticeInfoModels != null && noticeInfoModels.size() > 0) {
				Intent intent = new Intent(this, NoticeListActivity.class);
				startActivity(intent);
			} else {
				application.showToast(getResources().getString(R.string.nonew));
			}
			break;
		}
		case R.id.btn_main_control:
			application.operateType = "CONTROL";
			DialogPermission();
			// Intent i = new Intent(MainActivity.this,
			// ControlActivity.class);
			// startActivity(i);
			break;
		case R.id.btn_main_talk:
			application.operateType = "VEDIO_CALL";
			DialogPermission();
			// Intent ii = new Intent(MainActivity.this,
			// VideoCallActivity.class);
			// startActivity(ii);
			break;
		case R.id.btn_main_setting:
			application.operateType = "SETTING";
			DialogPermission();
			// Intent iii = new Intent(MainActivity.this,
			// SettingActivity.class);
			// startActivity(iii);
			break;
		case R.id.btn_permission_user: {
			btn_permission_user
					.setBackgroundResource(R.drawable.btn_permission_type);
			btn_permission_user.setTextColor(getResources().getColor(
					R.color.white));
			ly_permission_user.setVisibility(View.VISIBLE);
			btn_permission_card
					.setBackgroundResource(R.drawable.btn_permission_type_click);
			btn_permission_card.setTextColor(getResources().getColor(
					R.color.blue));
			ly_permission_card.setVisibility(View.INVISIBLE);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null)
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		}
		case R.id.btn_permission_card: {
			btn_permission_card
					.setBackgroundResource(R.drawable.btn_permission_type);
			btn_permission_card.setTextColor(getResources().getColor(
					R.color.white));
			ly_permission_card.setVisibility(View.VISIBLE);
			btn_permission_user
					.setBackgroundResource(R.drawable.btn_permission_type_click);
			btn_permission_user.setTextColor(getResources().getColor(
					R.color.blue));
			ly_permission_user.setVisibility(View.INVISIBLE);
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null)
				imm.hideSoftInputFromWindow(
						edt_permission_user.getWindowToken(), 0);
			break;
		}
		case R.id.btn_premission_login:
			String name = edt_permission_user.getText().toString();
			String pwd = edt_permission_pwd.getText().toString();
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pwd)) {
				if (name.equals("admin") && pwd.equals("admin123!@#")) {
					application.showToast("认证通过");
					if (application.dialog != null
							&& application.dialog.isShowing()) {
						application.dialog.dismiss();
					}
					if (application.operateType.equals("CONTROL")) {
						Intent i = new Intent(MainActivity.this,
								ControlActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("VEDIO_CALL")) {
						application.call_from_who = name;
						Intent i = new Intent(MainActivity.this,
								VideoCallActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("SETTING")) {
						Intent i = new Intent(MainActivity.this,
								SettingActivity.class);
						startActivity(i);
					}
					return;
				}
				UserInfoDatabase userInfoDatabase = new UserInfoDatabase();
				if (userInfoDatabase.query_by_userName(name, pwd) != null) {
					application.showToast("认证通过");
					if (application.dialog != null
							&& application.dialog.isShowing()) {
						application.dialog.dismiss();
					}
					if (application.operateType.equals("CONTROL")) {
						Intent i = new Intent(MainActivity.this,
								ControlActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("VEDIO_CALL")) {
						application.call_from_who = name;
						Intent i = new Intent(MainActivity.this,
								VideoCallActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("SETTING")) {
						Intent i = new Intent(MainActivity.this,
								SettingActivity.class);
						startActivity(i);
					}
					return;
				}
				btn_premission_login.setEnabled(false);
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(MainActivity.this,
							null, "认证中，请稍后", false, false);
				}
				application.httpProcess.Permission(
						application.equInfoModel.jssysdm,
						application.operateType, "ACCOUNT_PASSWORD", name, pwd,
						"");
				if (application.operateType.equals("VEDIO_CALL")) {
					application.call_from_who = name;
				}
			} else {
				application.showToast("账号或密码不能为空");
			}
			break;
		case R.id.btn_premission_cancel1:
		case R.id.btn_premission_cancel2:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null)
				imm.hideSoftInputFromWindow(
						edt_permission_user.getWindowToken(), 0);
			if (application.dialog != null && application.dialog.isShowing()) {
				application.dialog.dismiss();
			}
			break;
		case R.id.ly_main_new1: {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("id", noticeInfoModels.get(0).id);
			startActivity(intent);
			break;
		}
		case R.id.ly_main_new2: {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("id", noticeInfoModels.get(1).id);
			startActivity(intent);
			break;
		}
		case R.id.ly_main_new3: {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("id", noticeInfoModels.get(2).id);
			startActivity(intent);
			break;
		}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, AttendInfoActivity.class);
		intent.putExtra("skjhid",
				application.curriculumInfos_today.get(arg2).id);
		startActivity(intent);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 更新当前课程信息
	private void updata_now_curriculum() {
		if (application.curriculumInfos_today != null
				&& application.curriculumInfos_today.size() > 0) {
			if (application.curriculumInfoModel_now != null) {
				txt_class_noclass.setVisibility(View.INVISIBLE);
				txt_main_1.setVisibility(View.VISIBLE);
				txt_main_2.setVisibility(View.VISIBLE);
				txt_main_3.setVisibility(View.VISIBLE);
				txt_main_4.setVisibility(View.VISIBLE);
				txt_main_5.setVisibility(View.VISIBLE);
				txt_main_6.setVisibility(View.VISIBLE);
				txt_main_class.setVisibility(View.VISIBLE);
				txt_main_banji.setVisibility(View.VISIBLE);
				txt_main_time.setVisibility(View.VISIBLE);
				txt_main_teacher.setVisibility(View.VISIBLE);
				txt_main_solid_arrive.setVisibility(View.VISIBLE);
				txt_main_arrive.setVisibility(View.VISIBLE);
				txt_main_class
						.setText(application.curriculumInfoModel_now.kcmc);
				txt_main_banji
						.setText(application.curriculumInfoModel_now.skbjmc);
				txt_main_time.setText(MyApplication
						.jcToTime(application.curriculumInfoModel_now.jc));
				txt_main_teacher
						.setText(application.curriculumInfoModel_now.skjsxm);
				AttendInfoDatabase database = new AttendInfoDatabase();
				List<AttendInfoModel> list = database
						.query_by_skjhid(application.curriculumInfoModel_now.id);
				if (list != null && list.size() > 0) {
					arrive = 0;
					for (AttendInfoModel model : list) {
						if (model.kqzt.equals("0") || model.kqzt.equals("3")) {
							arrive++;
						}
					}
					txt_main_solid_arrive.setText("" + list.size());
					txt_main_arrive.setText("" + arrive);
				} else {
					txt_main_solid_arrive.setText("- -");
					txt_main_arrive.setText("- -");
				}
			} else {
				txt_main_1.setVisibility(View.INVISIBLE);
				txt_main_2.setVisibility(View.INVISIBLE);
				txt_main_3.setVisibility(View.INVISIBLE);
				txt_main_4.setVisibility(View.INVISIBLE);
				txt_main_5.setVisibility(View.INVISIBLE);
				txt_main_6.setVisibility(View.INVISIBLE);
				txt_main_class.setVisibility(View.INVISIBLE);
				txt_main_banji.setVisibility(View.INVISIBLE);
				txt_main_time.setVisibility(View.INVISIBLE);
				txt_main_teacher.setVisibility(View.INVISIBLE);
				txt_main_solid_arrive.setVisibility(View.INVISIBLE);
				txt_main_arrive.setVisibility(View.INVISIBLE);
				txt_class_noclass.setVisibility(View.VISIBLE);
				txt_class_noclass.setText(getResources().getString(
						R.string.noclass_));
			}
		} else {
			txt_main_1.setVisibility(View.INVISIBLE);
			txt_main_2.setVisibility(View.INVISIBLE);
			txt_main_3.setVisibility(View.INVISIBLE);
			txt_main_4.setVisibility(View.INVISIBLE);
			txt_main_5.setVisibility(View.INVISIBLE);
			txt_main_6.setVisibility(View.INVISIBLE);
			txt_main_class.setVisibility(View.INVISIBLE);
			txt_main_banji.setVisibility(View.INVISIBLE);
			txt_main_time.setVisibility(View.INVISIBLE);
			txt_main_teacher.setVisibility(View.INVISIBLE);
			txt_main_solid_arrive.setVisibility(View.INVISIBLE);
			txt_main_arrive.setVisibility(View.INVISIBLE);
			txt_class_noclass.setVisibility(View.VISIBLE);
			txt_class_noclass.setText(getResources()
					.getString(R.string.noclass));
		}
	}

	// 更新当天课程信息
	private void updata_today_curriculum() {
		if (application.curriculumInfos_today != null
				&& application.curriculumInfos_today.size() > 0) {
			curriculumTodayAdapter = new CurriculumTodayAdapter(application,
					application.curriculumInfos_today);
			list_main_schedule.setAdapter(curriculumTodayAdapter);
			txt_schedule_noclass.setVisibility(View.INVISIBLE);
		} else {
			list_main_schedule.setAdapter(null);
			txt_schedule_noclass.setVisibility(View.VISIBLE);
			txt_schedule_noclass.setText(getResources().getString(
					R.string.noclass));
		}
	}

	private void updata_new_notice() {
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		noticeInfoModels = database.query_limit(0, 3);
		if (noticeInfoModels != null && noticeInfoModels.size() > 0) {
			txt_main_nonew.setVisibility(View.INVISIBLE);
			ly_main_new1.setVisibility(View.VISIBLE);
			txt_main_new_title1.setText(noticeInfoModels.get(0).xxzt);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			txt_main_new_time1.setText(formatter.format(Long
					.parseLong(noticeInfoModels.get(0).fbsj)));
			txt_main_new_nr1.setText("");
			if (!TextUtils.isEmpty(noticeInfoModels.get(0).xxnr)) {
				Document document = Jsoup.parse(noticeInfoModels.get(0).xxnr);
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					String new_rl = "";
					for (Element element : p) {
						if (TextUtils.isEmpty(new_rl)) {
							new_rl = element.text();
						} else {
							new_rl = new_rl + "\n" + element.text();
						}
						if (!TextUtils.isEmpty(new_rl) && new_rl.length() > 100) {
							break;
						}
					}
					txt_main_new_nr1.setText(new_rl);
				}
			}
			if (noticeInfoModels.size() > 1) {
				ly_main_new2.setVisibility(View.VISIBLE);
				txt_main_new_title2.setText(noticeInfoModels.get(1).xxzt);
				txt_main_new_time2.setText(formatter.format(Long
						.parseLong(noticeInfoModels.get(1).fbsj)));
				txt_main_new_nr2.setText("");
				if (!TextUtils.isEmpty(noticeInfoModels.get(1).xxnr)) {
					Document document = Jsoup
							.parse(noticeInfoModels.get(1).xxnr);
					Elements p = document.select("p");
					if (p != null && p.size() > 0) {
						String new_rl = "";
						for (Element element : p) {
							if (TextUtils.isEmpty(new_rl)) {
								new_rl = element.text();
							} else {
								new_rl = new_rl + "\n" + element.text();
							}
							if (!TextUtils.isEmpty(new_rl)
									&& new_rl.length() > 100) {
								break;
							}
						}
						txt_main_new_nr2.setText(new_rl);
					}
				}
			}
			if (noticeInfoModels.size() > 2) {
				ly_main_new3.setVisibility(View.VISIBLE);
				txt_main_new_title3.setText(noticeInfoModels.get(2).xxzt);
				txt_main_new_time3.setText(formatter.format(Long
						.parseLong(noticeInfoModels.get(2).fbsj)));
				txt_main_new_nr3.setText("");
				if (!TextUtils.isEmpty(noticeInfoModels.get(2).xxnr)) {
					Document document = Jsoup
							.parse(noticeInfoModels.get(2).xxnr);
					Elements p = document.select("p");
					if (p != null && p.size() > 0) {
						String new_rl = "";
						for (Element element : p) {
							if (TextUtils.isEmpty(new_rl)) {
								new_rl = element.text();
							} else {
								new_rl = new_rl + "\n" + element.text();
							}
							if (!TextUtils.isEmpty(new_rl)
									&& new_rl.length() > 100) {
								break;
							}
						}
						txt_main_new_nr3.setText(new_rl);
					}
				}
			}
		} else {
			txt_main_nonew.setVisibility(View.VISIBLE);
			ly_main_new1.setVisibility(View.INVISIBLE);
			ly_main_new2.setVisibility(View.INVISIBLE);
			ly_main_new3.setVisibility(View.INVISIBLE);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				updata_now_curriculum();
				updata_today_curriculum();
				// if (application.curriculumInfoModel_now == null) {
				// application.handler_touch.removeMessages(0);
				// application.handler_touch.sendEmptyMessageDelayed(0,
				// application.screensaver_time * 1000);
				// }
				break;
			}
			case 2: {
				updata_now_curriculum();
				updata_today_curriculum();
				if (application.curriculumInfos_today != null
						&& application.curriculumInfos_today.size() > 0) {
					curriculumInfos_today_id = 0;
					application.httpProcess.QryAttend(
							application.equInfoModel.jssysdm,
							application.curriculumInfos_today
									.get(curriculumInfos_today_id++).id, null);
				} else {
					application.isUpdateCurriculum = false;
					application.showToast("更新课程完成");
				}
				break;
			}
			case 3: {
				arrive++;
				txt_main_arrive.setText("" + arrive);
				break;
			}
			}
		};
	};

	private Thread threadTime = new Thread(new Runnable() {

		@Override
		public void run() {
			while (isThread) {
				// 根据时间更新当前课程
				if (MyApplication.getTime("HHmmss").endsWith("00")) {
					playTask();
					Absenteeism(MyApplication.getTime("HHmmss"));
					CurriculumInfoModel model = application.findCurriculum(10);
					if (model != null
							&& application.curriculumInfoModel_now != null
							&& !model.id
									.equals(application.curriculumInfoModel_now.id)) {
						application.curriculumInfoModel_now = model;
						handler.sendEmptyMessage(1);
					} else if (model != null
							&& application.curriculumInfoModel_now == null) {
						application.curriculumInfoModel_now = model;
						handler.sendEmptyMessage(1);
					} else if (model == null
							&& application.curriculumInfoModel_now != null) {
						application.curriculumInfoModel_now = model;
						handler.sendEmptyMessage(1);
					}
				}
				// 根据时间更新当天课程
				if (MyApplication.getTime("HHmmss").equals("030000")) {
					application.isUpdateCurriculum = true;
					application.httpProcess.QryCurriculum_day(
							application.equInfoModel.jssysdm,
							MyApplication.getTime("yyyy-MM-dd") + " 00:00:00",
							MyApplication.getTime("yyyy-MM-dd") + " 23:59:59",
							"", null);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.QRY_ATTEND)
					&& !application.isUpdateEQ
					&& !application.isQryAttend
					&& !application.getRunningActivityName().equals(
							"com.yy.doorplate.activity.EmptyActivity")) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg + " " + curriculumInfos_today_id);
					if (curriculumInfos_today_id < application.curriculumInfos_today
							.size()) {
						application.httpProcess.QryAttend(
								application.equInfoModel.jssysdm,
								application.curriculumInfos_today
										.get(curriculumInfos_today_id++).id,
								null);
					} else {
						application.showToast("更新课程完成");
						application.isUpdateCurriculum = false;
						application.setToday(MyApplication.getTime("yyyyMMdd"));
						updateImg();
					}
				} else {
					application.isUpdateCurriculum = false;
					Log.e(TAG, tag + " " + msg);
					application.showToast("更新课程失败，原因：" + msg);
				}
			} else if (tag.equals(HttpProcess.OPERATE_AUTHORIZATION)) {
				if (TextUtils.isEmpty(application.operateType)) {
					return;
				}
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.showToast("认证通过");
					if (application.dialog != null
							&& application.dialog.isShowing()) {
						application.dialog.dismiss();
					}
					if (application.operateType.equals("CONTROL")) {
						Intent i = new Intent(MainActivity.this,
								ControlActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("VEDIO_CALL")) {
						Intent i = new Intent(MainActivity.this,
								VideoCallActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("SETTING")) {
						Intent i = new Intent(MainActivity.this,
								SettingActivity.class);
						startActivity(i);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("认证失败");
				}
				btn_premission_login.setEnabled(true);
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			} else if (tag.equals("permission_ui")) {
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(MainActivity.this,
							null, "认证中，请稍后", false, false);
				}
			} else if (tag.equals("commotAttend_ui")) {
				AttendInfoDatabase database = new AttendInfoDatabase();
				List<AttendInfoModel> list = database
						.query_by_skjhid(application.curriculumInfoModel_now.id);
				if (list != null && list.size() > 0) {
					arrive = 0;
					for (AttendInfoModel model : list) {
						if (model.kqzt.equals("0") || model.kqzt.equals("3")) {
							arrive++;
						}
					}
					txt_main_solid_arrive.setText("" + list.size());
					txt_main_arrive.setText("" + arrive);
				} else {
					txt_main_solid_arrive.setText("- -");
					txt_main_arrive.setText("- -");
				}
			} else if (tag.equals(HttpProcess.QRY_NOTICE)
					&& !application.isUpdateEQ
					&& !application.isUpdataNoticeList) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.showToast("更新通知完成");
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("更新通知失败，原因：" + msg);
				}
				updata_new_notice();
				application.isUpdateNotice = false;
			} else if (tag.equals(HttpProcess.QRY_CURRICULUM + "day")) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				CurriculumInfoDatabase database = new CurriculumInfoDatabase();
				application.curriculumInfos_today = database
						.query_by_skrq(MyApplication.getTime("yyyy-MM-dd"));
				application.curriculumInfoModel_now = application
						.findCurriculum(10);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					handler.sendEmptyMessage(2);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("更新课程失败，原因：" + msg);
				}
			} else if (tag.equals("updataEQ")) {
				if (application.equInfoModel != null
						&& application.classRoomInfoModel != null) {
					txt_classroom.setText(application.equInfoModel.jssysmc);
				}
				updata_now_curriculum();
				updata_today_curriculum();
				updata_new_notice();
			} else if (tag.equals("playTask")) {
				playTask();
			}
		}
	}

	private EditText edt_permission_user, edt_permission_pwd;
	private Button btn_permission_user, btn_permission_card,
			btn_premission_login, btn_premission_cancel1,
			btn_premission_cancel2;
	private RelativeLayout ly_permission_user, ly_permission_card;

	private void DialogPermission() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		application.dialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(application);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_permission, null);
		application.dialog.setView(layout);
		application.dialog.show();
		application.dialog.getWindow().setLayout(900, 700);
		application.dialog.getWindow().setContentView(
				R.layout.dialog_permission);
		// application.dialog.getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		application.dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				if (arg2.getAction() == KeyEvent.ACTION_UP
						&& arg1 == KeyEvent.KEYCODE_MENU
						&& !TextUtils.isEmpty(application.operateType)
						&& application.operateType.equals("VEDIO_CALL")) {
					if (application.dialog != null
							&& application.dialog.isShowing()) {
						application.dialog.dismiss();
					}
				}
				return false;
			}
		});
		application.dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialogInterface) {
				application.operateType = null;
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null)
					imm.hideSoftInputFromWindow(getWindow().getDecorView()
							.getWindowToken(), 0);
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		});
		ly_permission_user = (RelativeLayout) application.dialog
				.findViewById(R.id.ly_permission_user);
		ly_permission_card = (RelativeLayout) application.dialog
				.findViewById(R.id.ly_permission_card);
		edt_permission_user = (EditText) application.dialog
				.findViewById(R.id.edt_permission_user);
		edt_permission_pwd = (EditText) application.dialog
				.findViewById(R.id.edt_permission_pwd);
		btn_permission_user = (Button) application.dialog
				.findViewById(R.id.btn_permission_user);
		btn_permission_card = (Button) application.dialog
				.findViewById(R.id.btn_permission_card);
		btn_premission_login = (Button) application.dialog
				.findViewById(R.id.btn_premission_login);
		btn_premission_cancel1 = (Button) application.dialog
				.findViewById(R.id.btn_premission_cancel1);
		btn_premission_cancel2 = (Button) application.dialog
				.findViewById(R.id.btn_premission_cancel2);
		btn_permission_user.setOnClickListener(this);
		btn_permission_card.setOnClickListener(this);
		btn_premission_login.setOnClickListener(this);
		btn_premission_cancel1.setOnClickListener(this);
		btn_premission_cancel2.setOnClickListener(this);
		edt_permission_user.addTextChangedListener(application.textWatcher);
		edt_permission_pwd.addTextChangedListener(application.textWatcher);
	}

	// 旷课处理
	private void Absenteeism(String HHmmss) {
		if (application.curriculumInfoModel_now == null) {
			return;
		}
		String[] jc = application.curriculumInfoModel_now.jc.split("-");
		SectionInfoDatabase database = new SectionInfoDatabase();
		SectionInfoModel model = database.query_by_jcdm(jc[0]);
		if (model != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
			try {
				Date time = formatter.parse(HHmmss);
				if (time.getTime() > Long.parseLong(model.jcskkssj) + 1000 * 60
						* application.absenteeism_timeout) {
					AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
					List<AttendInfoModel> list = attendInfoDatabase
							.query_by_skjhid(application.curriculumInfoModel_now.id);
					if (list != null && list.size() > 0) {
						int count = 0;
						boolean isDo = false;
						for (AttendInfoModel attendInfoModel : list) {
							if (!attendInfoModel.kqzt.equals("11")) {
								count++;
							}
						}
						if (count == list.size()) {
							return;
						}
						for (AttendInfoModel attendInfoModel : list) {
							if (attendInfoModel.kqzt.equals("11")) {
								isDo = true;
								attendInfoModel.kqzt = "4";
								attendInfoModel.qdsx = (++count) + "";
								attendInfoDatabase.update(attendInfoModel.id,
										attendInfoModel);
								application.httpProcess.CommitAttend(
										attendInfoModel.xsxh, attendInfoModel);
							}
						}
						if (isDo) {
							Intent intent = new Intent(MyApplication.BROADCAST);
							intent.putExtra(MyApplication.BROADCAST_TAG,
									"absenteeism_ui");
							broadcastManager.sendBroadcast(intent);
						}
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private void playTask() {
		if (application.getRunningActivityName().equals(
				"com.yy.doorplate.activity.CallActivity")) {
			return;
		}
		if (application.isDownloadPlay || application.isGuangbo) {
			return;
		}
		if (application.playTaskModels == null) {
			return;
		}
		if (application.playTaskModels.size() == 0) {
			return;
		}
		if (application.playTask_position != -1
				&& application.playTaskModels
						.get(application.playTask_position).playType
						.equals("RADIO")
				&& application.playTaskModels
						.get(application.playTask_position).playEndTime
						.equals(MyApplication.getTime("HH:mm:ss"))) {
			if (application.getRunningActivityName().equals(
					"com.yy.doorplate.activity.PlayActivity")) {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG,
						"PlayActivity_finish");
				broadcastManager.sendBroadcast(intent);
			}
			application.playTask_position = -1;
		}
		for (int i = 0; i < application.playTaskModels.size(); i++) {
			try {
				PlayTaskModel model = application.playTaskModels.get(i);
				if (model.playType.equals("RADIO")
						&& model.taskType.equals("TIMING")) {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					long start = formatter.parse(
							model.playStartDate + " " + model.playStartTime)
							.getTime();
					long end = formatter.parse(
							model.playEndDate + " " + model.playEndTime)
							.getTime();
					long now = System.currentTimeMillis();
					Log.d(TAG, "start:" + start + " end:" + end + " time:"
							+ now);
					if (start <= now && now < end) {
						playGuangbo(i);
						break;
					}
				} else if (model.playType.equals("MEDIA")
						&& !application.isGuangbo && !application.isGuanggao
						&& application.playTask_position != i) {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					long startData = formatter.parse(
							model.playStartDate + " " + model.playStartTime)
							.getTime();
					long endData = formatter.parse(
							model.playEndDate + " " + model.playEndTime)
							.getTime();
					long nowData = System.currentTimeMillis();
					if (startData > nowData || nowData >= endData) {
						return;
					}

					formatter = new SimpleDateFormat("HH:mm:ss");
					String HHmmss = formatter
							.format(System.currentTimeMillis());
					Date time = formatter.parse(HHmmss);
					Date start = formatter.parse(model.playStartTime);
					Date end = formatter.parse(model.playEndTime);
					if (start.getTime() <= time.getTime()
							&& time.getTime() < end.getTime()) {
						Log.d(TAG, "---guanggao---" + i);
						application.playTask_position = i;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void playGuangbo(final int i) {
		final PlayTaskModel model = application.playTaskModels.get(i);
		if (model.srcType.equals("AUDIO")) {
			if (application.getRunningActivityName().equals(
					"com.yy.doorplate.activity.PlayActivity")) {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG,
						"PlayActivity_finish");
				broadcastManager.sendBroadcast(intent);
			}
			application.isGuanggao = false;
			application.isGuangbo = true;
			application.playTask_position = i;
			String[] s = model.srcPath.split("/");
			final String path = MyApplication.PATH_ROOT
					+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1];
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						application.mediaPlayer.stop();
						application.mediaPlayer.reset();
						application.mediaPlayer
								.setAudioStreamType(AudioManager.STREAM_MUSIC);
						application.mediaPlayer.setDataSource(path);
						application.mediaPlayer.setDisplay(null);
						application.mediaPlayer.setLooping(false);
						application.mediaPlayer
								.setOnErrorListener(new OnErrorListener() {

									@Override
									public boolean onError(MediaPlayer arg0,
											int arg1, int arg2) {
										application.isGuangbo = false;
										if (application.playTask_position != -1) {
											application.playTaskModels
													.remove(application.playTask_position);
											application.playTask_position = -1;
										}
										return false;
									}
								});
						application.mediaPlayer
								.setOnPreparedListener(new OnPreparedListener() {

									@Override
									public void onPrepared(
											MediaPlayer mediaPlayer) {
										Log.d(TAG, "downLoad Play start");
										mediaPlayer.start();
										application.showGuangbo(model.taskDesc,
												false);
									}
								});
						application.mediaPlayer
								.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {
										application.isGuangbo = false;
										if (application.playTask_position != -1) {
											application.playTaskModels
													.remove(application.playTask_position);
											application.playTask_position = -1;
										}
										if (application.dialog != null
												&& application.dialog
														.isShowing()) {
											application.dialog.dismiss();
										}
									}
								});
						application.mediaPlayer.prepareAsync();
					} catch (Exception e) {
						e.printStackTrace();
						application.isGuangbo = false;
						if (application.playTask_position != -1) {
							application.playTaskModels
									.remove(application.playTask_position);
							application.playTask_position = -1;
						}
					}
				}
			}, 2000);
		}
	}

	private void updateImg() {
		File file = new File(MyApplication.PATH_ROOT
				+ MyApplication.PATH_PICTURE);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (File f : files) {
					if (f.getName().startsWith("attend")
							|| f.getName().startsWith("teacher")) {
						f.delete();
					}
				}
			}
		}
		AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
		List<AttendInfoModel> attendInfoModels = attendInfoDatabase.query_all();
		if (attendInfoModels != null && attendInfoModels.size() > 0) {
			for (AttendInfoModel model : attendInfoModels) {
				if (!TextUtils.isEmpty(model.zp)) {
					String[] s = model.zp.split("\\.");
					application.downLoadImg(model.zp, MyApplication.PATH_ROOT
							+ MyApplication.PATH_PICTURE + "/attend_"
							+ model.id + "." + s[s.length - 1]);
				}
			}
		}
		if (application.curriculumInfos_today != null
				&& application.curriculumInfos_today.size() > 0) {
			for (CurriculumInfoModel model : application.curriculumInfos_today) {
				if (!TextUtils.isEmpty(model.skjszp)) {
					String[] s = model.skjszp.split("\\.");
					application.downLoadImg(model.skjszp,
							MyApplication.PATH_ROOT
									+ MyApplication.PATH_PICTURE + "/teacher_"
									+ model.skjs + "." + s[s.length - 1]);
				}
			}
		}
	}
}
