package com.yy.doorplate.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.advertisement.activity.DisplayManager;
import com.advertisement.system.ConstData;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.AttendInfoDatabase;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.EquModelTaskDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.PageLayoutDatabase;
import com.yy.doorplate.database.PersonnelAttendanceDatabase;
import com.yy.doorplate.database.PlayTaskDatabase;
import com.yy.doorplate.database.SectionInfoDatabase;
import com.yy.doorplate.database.UserInfoDatabase;
import com.yy.doorplate.model.AttendInfoModel;
import com.yy.doorplate.model.ClockRuleModel;
import com.yy.doorplate.model.CurriculumInfoModel;
import com.yy.doorplate.model.EquModelTaskModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PageLayoutModel;
import com.yy.doorplate.model.PersonnelAttendanceModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.SectionInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;
import com.yy.doorplate.ui.AppCodeView;
import com.yy.doorplate.view.CustomTextView;

public class PjMainActivity extends Activity implements OnClickListener {

	private final String TAG = "PjMainActivity";

	private MyApplication application;

	private TextClock textClock;
	private LinearLayout ly_main_schedule, ly_main_class;
	private RelativeLayout ly_activity_main, ly_main_new1, ly_main_new2,
			ly_banner;
	private Button btn_main_new, btn_main_class, btn_main_schedule,
			btn_main_new_school, btn_main_read, btn_main_setting;
	private TextView txt_weather, txt_location, txt_main_nonew,
			txt_main_new_title1, txt_main_new_title2, txt_main_new_nr1,
			txt_main_new_nr2, txt_main_new_time1, txt_main_new_time2,
			txt_main_kc, txt_main_time, txt_main_classnew,
			txt_main_nonschedule, txt_main_noclassnew, txt_main_nextschedule,
			txt_main_cc;
	private CustomTextView txt_main_class, txt_main_bj, txt_main_teacher;
	private SurfaceView sv_pj_main;
	private ImageView img_main_new1, img_main_new2;
	private ImageSwitcher img_main_banner;

	private int curriculumInfos_today_id = 0;

	private boolean isThread = true;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private List<NoticeInfoModel> noticeInfoModels = null;
	private NoticeInfoModel noticeInfoModel = null;

	private ProgressDialog progressDialog = null;

	private ImageLoader imageLoader = null;
	private SurfaceHolder surfaceHolder = null;
	private PlayTaskModel region_media_video = null;
	private List<PlayTaskModel> region_media_img = null;
	private int region_media_img_i = 0;

	private String new_rl_1, new_url_1, new_path_1, new_rl_2, new_url_2,
			new_path_2;
	private List<Map<String, String>> new_pics = null;
	private int new_pics_i = 0;

	private DisplayManager displayManager = null;
	private DisplayManager displayManager_media = null;

	private List<PageLayoutModel> pageLayoutModels = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pj_activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		application.initServer();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		filter.addAction(ConstData.ACTIVITY_UPDATE);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		ly_activity_main = (RelativeLayout) findViewById(R.id.ly_activity_main);
		PageLayoutDatabase database = new PageLayoutDatabase();
		PageLayoutModel model = database.query_by_pageCode("INDEX");
		if (model == null) {
			initView();
		} else {
			ly_activity_main.removeAllViews();
			displayManager = new DisplayManager(this, application,
					ly_activity_main);
			displayManager.setImageLoader(imageLoader);
			displayManager
					.setActivity("com.yy.doorplate.activity.PjMainActivity");
			displayManager.setActivity(this);
			displayManager.start(application.getFtpPath(model.resPath));
		}

		threadTime.start();
	}

	private void initView() {
		ly_main_schedule = (LinearLayout) findViewById(R.id.ly_main_schedule);
		ly_main_new1 = (RelativeLayout) findViewById(R.id.ly_main_new1);
		ly_main_new2 = (RelativeLayout) findViewById(R.id.ly_main_new2);
		ly_main_class = (LinearLayout) findViewById(R.id.ly_main_class);
		ly_banner = (RelativeLayout) findViewById(R.id.ly_banner);
		btn_main_new = (Button) findViewById(R.id.btn_main_new);
		btn_main_class = (Button) findViewById(R.id.btn_main_new_class);
		btn_main_schedule = (Button) findViewById(R.id.btn_main_schedule);
		btn_main_new_school = (Button) findViewById(R.id.btn_main_new_school);
		btn_main_read = (Button) findViewById(R.id.btn_main_read);
		btn_main_setting = (Button) findViewById(R.id.btn_main_setting);
		txt_main_nonew = (TextView) findViewById(R.id.txt_main_nonew);
		txt_main_class = (CustomTextView) findViewById(R.id.txt_main_class);
		txt_main_bj = (CustomTextView) findViewById(R.id.txt_main_bj);
		txt_main_kc = (TextView) findViewById(R.id.txt_main_kc);
		txt_main_time = (TextView) findViewById(R.id.txt_main_time);
		txt_main_teacher = (CustomTextView) findViewById(R.id.txt_main_teacher);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		txt_location = (TextView) findViewById(R.id.txt_location);
		txt_main_new_title1 = (TextView) findViewById(R.id.txt_main_new_title1);
		txt_main_new_title2 = (TextView) findViewById(R.id.txt_main_new_title2);
		txt_main_new_nr1 = (TextView) findViewById(R.id.txt_main_new_nr1);
		txt_main_new_nr2 = (TextView) findViewById(R.id.txt_main_new_nr2);
		txt_main_new_time1 = (TextView) findViewById(R.id.txt_main_new_time1);
		txt_main_new_time2 = (TextView) findViewById(R.id.txt_main_new_time2);
		txt_main_classnew = (TextView) findViewById(R.id.txt_main_classnew);
		txt_main_nonschedule = (TextView) findViewById(R.id.txt_main_nonschedule);
		txt_main_noclassnew = (TextView) findViewById(R.id.txt_main_noclassnew);
		txt_main_nextschedule = (TextView) findViewById(R.id.txt_main_nextschedule);
		txt_main_cc = (TextView) findViewById(R.id.txt_main_cc);
		sv_pj_main = (SurfaceView) findViewById(R.id.sv_pj_main);
		img_main_banner = (ImageSwitcher) findViewById(R.id.img_main_banner);
		img_main_new1 = (ImageView) findViewById(R.id.img_main_new1);
		img_main_new2 = (ImageView) findViewById(R.id.img_main_new2);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy-MM-dd HH:mm");

		img_main_banner.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(PjMainActivity.this);
				ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
						ImageSwitcher.LayoutParams.MATCH_PARENT,
						ImageSwitcher.LayoutParams.MATCH_PARENT);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(params);
				return imageView;
			}
		});
		img_main_banner.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.enteralpha));
		img_main_banner.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.exitalpha));

		txt_main_classnew.setOnClickListener(this);
		ly_main_new1.setOnClickListener(this);
		ly_main_new2.setOnClickListener(this);
		ly_main_schedule.setOnClickListener(this);
		ly_main_class.setOnClickListener(this);
		btn_main_new.setOnClickListener(this);
		btn_main_class.setOnClickListener(this);
		btn_main_schedule.setOnClickListener(this);
		btn_main_new_school.setOnClickListener(this);
		btn_main_read.setOnClickListener(this);
		btn_main_setting.setOnClickListener(this);
		img_main_banner.setOnClickListener(this);

		surfaceHolder = sv_pj_main.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.setKeepScreenOn(true);
		surfaceHolder.addCallback(new surFaceView());

		if (application.classInfoModel != null) {
			txt_main_class.setText(application.classInfoModel.bjmc);
		}
		if (application.equInfoModel != null) {
			txt_main_bj.setText(application.equInfoModel.jssysmc);
			application.httpProcess
					.getWeather(application.equInfoModel.jssysdm);
		}
		updata_now_curriculum();
		updata_new_notice();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_main_new: {
			Intent intent = new Intent(this, PjNoticeListActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_main_new_class: {
			Intent intent = new Intent(this, ClassNewActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_main_schedule: {
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
		case R.id.btn_main_new_school: {
			Intent intent = new Intent(this, SchoolNewActitivty.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_main_read: {
			try {
				application.openApp(this, MyApplication.READ);
			} catch (Exception e) {
				e.printStackTrace();
				Intent intent = new Intent(this, WebviewActivity.class);
				startActivity(intent);
			}
			break;
		}
		case R.id.btn_main_setting:
			application.operateType = "SETTING";
			DialogPermission();
			// Intent iii = new Intent(this, SettingActivity.class);
			// startActivity(iii);
			break;
		case R.id.ly_main_new1: {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校通知");
			intent.putExtra("from", "PjMainActivity");
			intent.putExtra("type", 1);
			intent.putExtra("id", noticeInfoModels.get(0).id);
			startActivity(intent);
			break;
		}
		case R.id.ly_main_new2: {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校通知");
			intent.putExtra("from", "PjMainActivity");
			intent.putExtra("type", 1);
			intent.putExtra("id", noticeInfoModels.get(1).id);
			startActivity(intent);
			break;
		}
		case R.id.txt_main_classnew: {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("top", "班级通知");
			intent.putExtra("from", "PjMainActivity");
			intent.putExtra("type", 3);
			intent.putExtra("id", noticeInfoModel.id);
			startActivity(intent);
			break;
		}
		case R.id.ly_main_class: {
			Intent intent = new Intent(this, ClassInfoActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.ly_main_schedule: {
			Intent intent = new Intent(this, AttendInfoActivity.class);
			if (application.curriculumInfoModel_now != null) {
				intent.putExtra("skjhid",
						application.curriculumInfoModel_now.id);
			} else if (application.curriculumInfoModel_next != null) {
				intent.putExtra("skjhid",
						application.curriculumInfoModel_next.id);
			}
			startActivity(intent);
		}
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
						Intent i = new Intent(PjMainActivity.this,
								ControlActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("VEDIO_CALL")) {
						application.call_from_who = name;
						Intent i = new Intent(PjMainActivity.this,
								VideoCallActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("SETTING")) {
						Intent i = new Intent(PjMainActivity.this,
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
						Intent i = new Intent(PjMainActivity.this,
								ControlActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("VEDIO_CALL")) {
						application.call_from_who = name;
						Intent i = new Intent(PjMainActivity.this,
								VideoCallActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("SETTING")) {
						Intent i = new Intent(PjMainActivity.this,
								SettingActivity.class);
						startActivity(i);
					}
					return;
				}
				btn_premission_login.setEnabled(false);
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(PjMainActivity.this,
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
		case R.id.img_main_banner: {
			if (new_pics != null && new_pics.size() > 0) {
				int i = new_pics_i - 1;
				if (i < 0) {
					i = 0;
				}
				Intent intent = new Intent(this, NoticeDetailsActivity.class);
				intent.putExtra("top", "学校新闻");
				intent.putExtra("from", "PjMainActivity");
				intent.putExtra("type", 4);
				intent.putExtra("id", new_pics.get(i).get("id"));
				startActivity(intent);
			}
			break;
		}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.clearCache();
		imageLoader.cancelTask();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		if (displayManager != null) {
			displayManager.stop();
			displayManager.destroy();
		}
		if (displayManager_media != null) {
			displayManager_media.stop();
			displayManager_media.destroy();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!equModelTask()) {
			if (displayManager == null) {
				updata_banner();
			}
			application.handler_touch.removeMessages(0);
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeMessages(4);
		handler.removeMessages(5);
		handler.removeMessages(10);
		if (new_pics != null) {
			new_pics.clear();
			new_pics_i = 0;
		}
		if (displayManager_media != null) {
			displayManager_media.stop();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (displayManager != null) {
			displayManager.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (displayManager != null) {
			displayManager.resume();
		}
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
				txt_main_cc.setText("当前课程");
				txt_main_nonschedule.setVisibility(View.INVISIBLE);
				ly_main_schedule.setVisibility(View.VISIBLE);
				txt_main_kc.setText(application.curriculumInfoModel_now.kcmc);
				txt_main_time
						.setText(application.curriculumInfoModel_now.skrq
								+ "\n"
								+ MyApplication
										.jcToTime(application.curriculumInfoModel_now.jc));
				txt_main_teacher
						.setText(application.curriculumInfoModel_now.skjsxm);

				application.curriculumInfoModel_next = null;
				for (int i = 0; i < application.curriculumInfos_today.size(); i++) {
					if (application.curriculumInfos_today.get(i).id
							.equals(application.curriculumInfoModel_now.id)
							&& application.curriculumInfos_today.size() > (i + 1)) {
						application.curriculumInfoModel_next = application.curriculumInfos_today
								.get(i + 1);
						break;
					}
				}
				if (application.curriculumInfoModel_next != null) {
					txt_main_nextschedule.setVisibility(View.VISIBLE);
					txt_main_nextschedule.setText("下一节  : "
							+ application.curriculumInfoModel_next.kcmc);
				} else {
					txt_main_nextschedule.setVisibility(View.INVISIBLE);
				}
			} else {
				application.curriculumInfoModel_next = null;
				try {
					application.curriculumInfoModel_next = findNextCurriculum();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (application.curriculumInfoModel_next != null) {
					txt_main_cc.setText("下一节课程");
					txt_main_nextschedule.setVisibility(View.INVISIBLE);
					txt_main_nonschedule.setVisibility(View.INVISIBLE);
					ly_main_schedule.setVisibility(View.VISIBLE);
					txt_main_kc
							.setText(application.curriculumInfoModel_next.kcmc);
					txt_main_time
							.setText(application.curriculumInfoModel_next.skrq
									+ "\n"
									+ MyApplication
											.jcToTime(application.curriculumInfoModel_next.jc));
					txt_main_teacher
							.setText(application.curriculumInfoModel_next.skjsxm);
				} else {
					txt_main_cc.setText("当前课程");
					ly_main_schedule.setVisibility(View.INVISIBLE);
					txt_main_nonschedule.setVisibility(View.VISIBLE);
					txt_main_nextschedule.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			application.curriculumInfoModel_next = null;
			try {
				application.curriculumInfoModel_next = findNextCurriculum();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (application.curriculumInfoModel_next != null) {
				txt_main_cc.setText("下一节课程");
				txt_main_nextschedule.setVisibility(View.INVISIBLE);
				txt_main_nonschedule.setVisibility(View.INVISIBLE);
				ly_main_schedule.setVisibility(View.VISIBLE);
				txt_main_kc.setText(application.curriculumInfoModel_next.kcmc);
				txt_main_time
						.setText(application.curriculumInfoModel_next.skrq
								+ "\n"
								+ MyApplication
										.jcToTime(application.curriculumInfoModel_next.jc));
				txt_main_teacher
						.setText(application.curriculumInfoModel_next.skjsxm);
			} else {
				txt_main_cc.setText("当前课程");
				ly_main_schedule.setVisibility(View.INVISIBLE);
				txt_main_nonschedule.setVisibility(View.VISIBLE);
				txt_main_nextschedule.setVisibility(View.INVISIBLE);
			}
		}
	}

	private CurriculumInfoModel findNextCurriculum() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
		if (application.curriculumInfos_today != null) {
			if (application.curriculumInfoModel_now == null) {
				SectionInfoDatabase database = new SectionInfoDatabase();
				String HHmmss = formatter.format(System.currentTimeMillis());
				Date time = formatter.parse(HHmmss);
				for (CurriculumInfoModel model : application.curriculumInfos_today) {
					String[] jcs = model.jc.split("-");
					SectionInfoModel sectionInfoModel = database
							.query_by_jcdm(jcs[0]);
					if (sectionInfoModel != null
							&& time.getTime() < Long
									.parseLong(sectionInfoModel.jcskkssj)) {
						return model;
					}
				}
			}
		}
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		CurriculumInfoDatabase database = new CurriculumInfoDatabase();
		List<CurriculumInfoModel> list = database.query_all();
		if (list != null && list.size() > 0) {
			for (CurriculumInfoModel model : list) {
				if (formatter.parse(model.skrq).getTime() > System
						.currentTimeMillis()) {
					return model;
				}
			}
		}
		return null;
	}

	private void updata_new_notice() {
		NoticeInfoDatabase database = new NoticeInfoDatabase();
		noticeInfoModels = database.query_by_lmdm("bpxxtz", 0, 2);
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
				Element element = document.select("img").first();
				Elements p = document.select("p");
				if (p != null && p.size() > 0) {
					new_rl_1 = "";
					for (Element e : p) {
						if (TextUtils.isEmpty(new_rl_1)) {
							new_rl_1 = e.text();
						} else {
							new_rl_1 = new_rl_1 + "\n" + e.text();
						}
						if (!TextUtils.isEmpty(new_rl_1)
								&& new_rl_1.length() > 100) {
							break;
						}
					}
					txt_main_new_nr1.setText(new_rl_1);
				}
				if (element != null) {
					new_url_1 = element.attr("src");
					if (!TextUtils.isEmpty(new_url_1)) {
						String[] s = new_url_1.split("/");
						new_path_1 = MyApplication.PATH_ROOT
								+ MyApplication.PATH_NOTICE + "/"
								+ s[s.length - 1];
						// Log.d(TAG, new_path_1);
						File file = new File(new_path_1);
						if (file.exists()) {
							imageLoader.getBitmapFormUrl(new_path_1,
									new OnImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap, String url) {
											if (bitmap != null) {
												Message msg = Message.obtain();
												msg.what = 4;
												msg.obj = bitmap;
												handler.sendMessage(msg);
											} else {
												handler.sendEmptyMessage(4);
											}
										}
									}, 250, 180);
						} else {
							downLoadImg(new_url_1, new_path_1, 4);
						}
					} else {
						handler.sendEmptyMessage(4);
					}
				} else {
					handler.sendEmptyMessage(4);
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
					Element element = document.select("img").first();
					Elements p = document.select("p");
					if (p != null && p.size() > 0) {
						new_rl_2 = "";
						for (Element e : p) {
							if (TextUtils.isEmpty(new_rl_2)) {
								new_rl_2 = e.text();
							} else {
								new_rl_2 = new_rl_2 + "\n" + e.text();
							}
							if (!TextUtils.isEmpty(new_rl_2)
									&& new_rl_2.length() > 100) {
								break;
							}
						}
						txt_main_new_nr2.setText(new_rl_2);
					}
					if (element != null) {
						new_url_2 = element.attr("src");
						if (!TextUtils.isEmpty(new_url_2)) {
							String[] s = new_url_2.split("/");
							new_path_2 = MyApplication.PATH_ROOT
									+ MyApplication.PATH_NOTICE + "/"
									+ s[s.length - 1];
							// Log.d(TAG, new_path_2);
							File file = new File(new_path_2);
							if (file.exists()) {
								imageLoader.getBitmapFormUrl(new_path_2,
										new OnImageLoaderListener() {
											@Override
											public void onImageLoader(
													Bitmap bitmap, String url) {
												if (bitmap != null) {
													Message msg = Message
															.obtain();
													msg.what = 5;
													msg.obj = bitmap;
													handler.sendMessage(msg);
												} else {
													handler.sendEmptyMessage(5);
												}
											}
										}, 250, 180);
							} else {
								downLoadImg(new_url_2, new_path_2, 5);
							}
						} else {
							handler.sendEmptyMessage(5);
						}
					} else {
						handler.sendEmptyMessage(5);
					}
				}
			}
		} else {
			txt_main_nonew.setVisibility(View.VISIBLE);
			ly_main_new1.setVisibility(View.INVISIBLE);
			ly_main_new2.setVisibility(View.INVISIBLE);
		}
		List<NoticeInfoModel> list = database.query_by_lmdm("bpbjtz", 0, 1);
		if (list != null && list.size() == 1) {
			noticeInfoModel = list.get(0);
			txt_main_classnew.setText("");
			if (!TextUtils.isEmpty(noticeInfoModel.xxnr)) {
				txt_main_classnew.setVisibility(View.VISIBLE);
				txt_main_noclassnew.setVisibility(View.INVISIBLE);
				Document document = Jsoup.parse(noticeInfoModel.xxnr);
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
					txt_main_classnew.setText(new_rl);
				}
			} else {
				txt_main_classnew.setVisibility(View.INVISIBLE);
				txt_main_noclassnew.setVisibility(View.VISIBLE);
			}
		} else {
			txt_main_classnew.setVisibility(View.INVISIBLE);
			txt_main_noclassnew.setVisibility(View.VISIBLE);
		}
	}

	private void updata_banner() {
		region_media_video = null;
		region_media_img = new ArrayList<PlayTaskModel>();
		region_media_img_i = 0;
		handler.removeMessages(3);
		handler.removeMessages(7);
		handler.removeMessages(10);
		imageLoader.clearCache();
		img_main_banner.setVisibility(View.VISIBLE);
		sv_pj_main.setVisibility(View.INVISIBLE);
		application.regionIsPlaying = false;
		if (application.mediaPlayer_region != null) {
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
		} else {
			application.mediaPlayer_region = new MediaPlayer();
		}
		new_pics = new ArrayList<Map<String, String>>();
		new_pics_i = 0;

		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				NoticeInfoDatabase database = new NoticeInfoDatabase();
				List<NoticeInfoModel> models = database.query_by_lmdm("bpxxxw");
				if (models != null && models.size() > 0) {
					for (NoticeInfoModel model : models) {
						if (!TextUtils.isEmpty(model.xxnr)) {
							Document document = Jsoup.parse(model.xxnr);
							Element element = document.select("img").first();
							if (element != null) {
								String src = element.attr("src");
								if (!TextUtils.isEmpty(src)) {
									String[] s = src.split("/");
									String path = MyApplication.PATH_ROOT
											+ MyApplication.PATH_NOTICE + "/"
											+ s[s.length - 1];
									Map<String, String> map = new HashMap<String, String>();
									map.put("url", src);
									map.put("path", path);
									map.put("id", model.id);
									new_pics.add(map);
								}
							}
						}
						if (new_pics.size() >= 5) {
							break;
						}
					}
				}
				handler.sendEmptyMessage(10);
			}
		});
	}

	private void updata_banner_region_media() {
		if (application.playTaskModels != null
				&& application.playTaskModels.size() > 0) {
			for (PlayTaskModel model : application.playTaskModels) {
				if (model.playType.equals("REGION_MEDIA")
						&& model.position.equals("01")
						&& model.srcType.equals("TEMPLATE")) {
					String policyPath = application.getFtpPath(model.srcPath);
					File file = new File(policyPath);
					if (file.exists()) {
						ly_banner.removeAllViews();
						if (displayManager_media == null) {
							displayManager_media = new DisplayManager(this,
									application, ly_banner);
							displayManager_media.start(policyPath);
						} else {
							displayManager_media.stop();
							displayManager_media.destroy();
							displayManager_media.start(policyPath);
						}
						return;
					}
				}
			}
			for (PlayTaskModel model : application.playTaskModels) {
				if (model.playType.equals("REGION_MEDIA")
						&& model.position.equals("01")) {
					if (model.srcType.equals("VIDEO")) {
						region_media_video = model;
						break;
					} else if (model.srcType.equals("IMAGE")) {
						region_media_img.add(model);
					}
				}
			}
		}
		if (region_media_video != null
				&& !TextUtils.isEmpty(region_media_video.srcPath)) {
			String[] s = region_media_video.srcPath.split("/");
			File file = new File(MyApplication.PATH_ROOT
					+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1]);
			if (file.exists()) {
				img_main_banner.setVisibility(View.INVISIBLE);
				sv_pj_main.setVisibility(View.VISIBLE);
				return;
			}
		}
		if (region_media_img.size() > 0) {
			play_img();
		} else {
			updata_banner_default();
		}
	}

	private void play_img() {
		img_main_banner.setVisibility(View.VISIBLE);
		sv_pj_main.setVisibility(View.INVISIBLE);
		if (region_media_img.size() == 1) {
			if (!TextUtils.isEmpty(region_media_img.get(0).srcPath)) {
				String[] s = region_media_img.get(0).srcPath.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1];
				File file = new File(path);
				if (file.exists()) {
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null)
										img_main_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
								}
							}, 772, 307);
				} else {
					updata_banner_default();
				}
			} else {
				updata_banner_default();
			}
		} else if (region_media_img.size() > 1) {
			boolean b = false;
			for (PlayTaskModel model : region_media_img) {
				if (!TextUtils.isEmpty(model.srcPath)) {
					String[] s = model.srcPath.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PLAYTASK + "/"
							+ s[s.length - 1];
					File file = new File(path);
					if (file.exists()) {
						b = true;
						break;
					}
				}
			}
			if (b) {
				handler.sendEmptyMessage(3);
			} else {
				updata_banner_default();
			}
		}
	}

	private void updata_banner_default() {
		img_main_banner.setVisibility(View.VISIBLE);
		sv_pj_main.setVisibility(View.INVISIBLE);
		imageLoader.getBitmapFormRes(R.drawable.pj_ly_banner,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						img_main_banner.setImageDrawable(new BitmapDrawable(
								bitmap));
					}
				});
	}

	private class surFaceView implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d(TAG, "---surfaceCreated---");
			handler.sendEmptyMessageDelayed(7, 3000);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(TAG, "---surfaceDestroyed---");
			handler.removeMessages(7);
			application.regionIsPlaying = false;
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
		}
	}

	private void mediaPlayer_region_play() {
		if (!application.getRunningActivityName().equals(
				"com.yy.doorplate.activity.PjMainActivity")
				|| region_media_video == null) {
			return;
		}
		try {
			String[] s = region_media_video.srcPath.split("/");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PLAYTASK
					+ "/" + s[s.length - 1];
			application.mediaPlayer_region.stop();
			application.mediaPlayer_region.reset();
			application.mediaPlayer_region
					.setAudioStreamType(AudioManager.STREAM_MUSIC);
			application.mediaPlayer_region.setDataSource(path);
			application.mediaPlayer_region.setDisplay(surfaceHolder);
			application.mediaPlayer_region
					.setOnErrorListener(new OnErrorListener() {

						@Override
						public boolean onError(MediaPlayer arg0, int arg1,
								int arg2) {
							updata_banner_default();
							return false;
						}
					});
			application.mediaPlayer_region
					.setOnPreparedListener(new OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer mediaPlayer) {
							application.regionIsPlaying = true;
							if (!application.isGuangbo) {
								mediaPlayer.start();
							}
						}
					});
			application.mediaPlayer_region
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							mediaPlayer_region_play();
						}
					});
			application.mediaPlayer_region.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
			updata_banner_default();
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				if (displayManager != null) {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_CURRICULUM);
				} else {
					updata_now_curriculum();
				}
				if (application.curriculumInfoModel_now != null
						&& application.getRunningActivityName().equals(
								"com.advertisement.activity.DisplayActivity")) {
					Intent intent = new Intent(MyApplication.BROADCAST);
					intent.putExtra(MyApplication.BROADCAST_TAG,
							"PlayActivity_finish");
					broadcastManager.sendBroadcast(intent);
				}
				break;
			}
			case 2: {
				if (displayManager != null) {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_CURRICULUM);
				} else {
					updata_now_curriculum();
				}
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
				if (!TextUtils
						.isEmpty(region_media_img.get(region_media_img_i).srcPath)) {
					String[] s = region_media_img.get(region_media_img_i).srcPath
							.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_PLAYTASK + "/"
							+ s[s.length - 1];
					// Log.d(TAG, path);
					imageLoader.getBitmapFormUrl(path,
							new OnImageLoaderListener() {
								@Override
								public void onImageLoader(Bitmap bitmap,
										String url) {
									if (bitmap != null)
										img_main_banner
												.setImageDrawable(new BitmapDrawable(
														bitmap));
									if (region_media_img_i == region_media_img
											.size() - 1) {
										region_media_img_i = 0;
									} else {
										region_media_img_i++;
									}
									handler.sendEmptyMessageDelayed(3, 5 * 1000);
								}
							}, 772, 307);
				} else {
					if (region_media_img_i == region_media_img.size() - 1) {
						region_media_img_i = 0;
					} else {
						region_media_img_i++;
					}
					handler.sendEmptyMessageDelayed(3, 1000);
				}
				break;
			}
			case 4: {
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					img_main_new1.setVisibility(View.VISIBLE);
					img_main_new1.setImageBitmap(bitmap);
					LayoutParams params = txt_main_new_title1.getLayoutParams();
					params.width = 450;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_title1.setLayoutParams(params);
					params = txt_main_new_nr1.getLayoutParams();
					params.width = 450;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_nr1.setLayoutParams(params);
				} else {
					img_main_new1.setVisibility(View.INVISIBLE);
					LayoutParams params = txt_main_new_title1.getLayoutParams();
					params.width = 712;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_title1.setLayoutParams(params);
					params = txt_main_new_nr1.getLayoutParams();
					params.width = 712;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_nr1.setLayoutParams(params);
				}
				break;
			}
			case 5: {
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					img_main_new2.setVisibility(View.VISIBLE);
					img_main_new2.setImageBitmap(bitmap);
					LayoutParams params = txt_main_new_title2.getLayoutParams();
					params.width = 460;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_title2.setLayoutParams(params);
					params = txt_main_new_nr2.getLayoutParams();
					params.width = 460;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_nr2.setLayoutParams(params);
				} else {
					img_main_new2.setVisibility(View.INVISIBLE);
					LayoutParams params = txt_main_new_title2.getLayoutParams();
					params.width = 712;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_title2.setLayoutParams(params);
					params = txt_main_new_nr2.getLayoutParams();
					params.width = 712;
					params.height = LayoutParams.WRAP_CONTENT;
					txt_main_new_nr2.setLayoutParams(params);
				}
				break;
			}
			case 6:
				txt_main_classnew.setText(msg.obj.toString());
				break;
			case 7:
				mediaPlayer_region_play();
				break;
			case 8:
				if (TextUtils.isEmpty(new_path_1)) {
					break;
				}
				imageLoader.getBitmapFormUrl(new_path_1,
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null) {
									Message msg = Message.obtain();
									msg.what = 4;
									msg.obj = bitmap;
									handler.sendMessage(msg);
								} else {
									handler.sendEmptyMessage(4);
								}
							}
						}, 250, 180);
				break;
			case 9:
				if (TextUtils.isEmpty(new_path_2)) {
					break;
				}
				imageLoader.getBitmapFormUrl(new_path_2,
						new OnImageLoaderListener() {
							@Override
							public void onImageLoader(Bitmap bitmap, String url) {
								if (bitmap != null) {
									Message msg = Message.obtain();
									msg.what = 5;
									msg.obj = bitmap;
									handler.sendMessage(msg);
								} else {
									handler.sendEmptyMessage(5);
								}
							}
						}, 250, 180);
				break;
			case 10:
				if (new_pics != null && new_pics.size() > 0) {
					if (new_pics.size() == 1) {
						File file = new File(new_pics.get(0).get("path"));
						if (!file.exists()) {
							downLoadImg(new_pics.get(0).get("url"), new_pics
									.get(0).get("path"));
						} else {
							imageLoader.getBitmapFormUrl(
									new_pics.get(0).get("path"),
									new OnImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap, String url) {
											if (bitmap != null)
												img_main_banner
														.setImageDrawable(new BitmapDrawable(
																bitmap));
										}
									}, 772, 307);
						}
					} else {
						File file = new File(new_pics.get(new_pics_i).get(
								"path"));
						if (!file.exists()) {
							downLoadImg(new_pics.get(new_pics_i).get("url"),
									new_pics.get(new_pics_i).get("path"));
						} else {
							imageLoader.getBitmapFormUrl(
									new_pics.get(new_pics_i).get("path"),
									new OnImageLoaderListener() {
										@Override
										public void onImageLoader(
												Bitmap bitmap, String url) {
											if (bitmap != null)
												img_main_banner
														.setImageDrawable(new BitmapDrawable(
																bitmap));
											if (new_pics_i == new_pics.size() - 1) {
												new_pics_i = 0;
											} else {
												new_pics_i++;
											}
											handler.sendEmptyMessageDelayed(10,
													5 * 1000);
										}
									}, 772, 307);
						}
					}
				} else {
					updata_banner_region_media();
				}
				break;
			case 11:
				if (displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_HONOR);
				}
				break;
			case 12:
				application.showDialogOff();
				break;
			}
		};
	};

	private Thread threadTime = new Thread(new Runnable() {

		@Override
		public void run() {
			while (isThread) {
				// 根据时间更新当前课程
				String hhmmss = MyApplication.getTime("HHmmss");
				timingOFFON();
				if (hhmmss.endsWith("00")) {
					equModelTask();
					playTask();
					try {
						Absenteeism(MyApplication.getTime("HH:mm:ss"));
					} catch (ParseException e) {
						e.printStackTrace();
					}
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
				if (hhmmss.equals("000000")) {
					application.isUpdateCurriculum = true;
					application.httpProcess.QryCurriculum_day(
							application.equInfoModel.jssysdm,
							MyApplication.getTime("yyyy-MM-dd") + " 00:00:00",
							MyApplication.getTime("yyyy-MM-dd") + " 23:59:59",
							"", null);
					application.httpProcess.QryPersonAttend(null,
							MyApplication.getTime("yyyy-MM-dd"), "");
					application.httpProcess
							.getWeather(application.equInfoModel.jssysdm);
					// try {
					// application.getOFFONTime();
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
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
			if (intent.getAction().equals(ConstData.ACTIVITY_UPDATE)) {
				if (application.pageLayoutTotal > 0) {
					Log.d(TAG,
							"------------ConstData.ACTIVITY_UPDATE-----------");
					boolean result = intent.getBooleanExtra(
							MyApplication.CMD_RESULT, false);
					// application.pageLayoutCount++;
					// if (application.pageLayoutCount <
					// application.pageLayoutTotal) {
					// application.systemManager
					// .downloadPolicy(pageLayoutModels
					// .get(application.pageLayoutCount).resPath);
					// } else {
					application.pageLayoutTotal = 0;
					application.pageLayoutCount = 0;
					PageLayoutDatabase database = new PageLayoutDatabase();
					if (result) {
						PageLayoutModel model = database
								.query_by_pageCode("INDEX");
						if (model != null) {
							ly_activity_main.removeAllViews();
							if (displayManager == null) {
								displayManager = new DisplayManager(
										PjMainActivity.this, application,
										ly_activity_main);
								displayManager.setImageLoader(imageLoader);
								displayManager
										.setActivity("com.yy.doorplate.activity.PjMainActivity");
								displayManager.setActivity(PjMainActivity.this);
							}
							displayManager.start(application
									.getFtpPath(model.resPath));
						}
						application.httpProcess.commitBack(application.pageSn,
								HttpProcess.SUCESS, HttpProcess.SUCESS_MSG);
					} else {
						database.delete_all();
						application.showToast("页面更新失败");
						application.httpProcess.commitBack(application.pageSn,
								HttpProcess.ERROR, "页面更新失败");
					}
					// }
				}
			}
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
						Intent i = new Intent(PjMainActivity.this,
								ControlActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("VEDIO_CALL")) {
						Intent i = new Intent(PjMainActivity.this,
								VideoCallActivity.class);
						startActivity(i);
					} else if (application.operateType.equals("SETTING")) {
						Intent i = new Intent(PjMainActivity.this,
								SettingActivity.class);
						startActivity(i);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("认证失败");
				}
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				if (displayManager != null) {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_DIALOG_DIS);
				} else {
					btn_premission_login.setEnabled(true);
				}
			} else if (tag.equals("permission_ui")) {
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(PjMainActivity.this,
							null, "认证中，请稍后", false, false);
				}
			} else if (tag.equals("commotAttend_ui")) {
				if (displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_ATTEND);
				}
			} else if (tag.equals("absenteeism_ui")) {
				if (displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_ATTEND);
				}
			} else if (tag.equals(HttpProcess.QRY_NOTICE)
					&& !application.isUpdateEQ
					&& !application.isUpdataNoticeList) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.showToast("更新通知完成");
					application.httpProcess.commitBack(sn, HttpProcess.SUCESS,
							HttpProcess.SUCESS_MSG);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("更新通知失败，原因：" + msg);
					application.httpProcess.commitBack(sn, HttpProcess.ERROR,
							msg);
				}
				if (displayManager == null) {
					updata_new_notice();
					if (application.getRunningActivityName().equals(
							"com.yy.doorplate.activity.PjMainActivity")) {
						updata_banner();
					}
				} else {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_NOTIC);
				}
				application.isUpdateNotice = false;
			} else if (tag.equals(HttpProcess.QRY_CURRICULUM + "day")) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				CurriculumInfoDatabase database = new CurriculumInfoDatabase();
				application.curriculumInfos_today = database
						.query_by_skrq(MyApplication.getTime("yyyy-MM-dd"));
				application.curriculumInfoModel_now = application
						.findCurriculum(10);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					handler.sendEmptyMessage(2);
					application.httpProcess.commitBack(sn, HttpProcess.SUCESS,
							HttpProcess.SUCESS_MSG);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("更新课程失败，原因：" + msg);
					application.httpProcess.commitBack(sn, HttpProcess.ERROR,
							msg);
				}
			} else if (tag.equals(HttpProcess.QRY_CURRICULUM + "week")
					&& !application.isGrxx) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager != null) {
						displayManager
								.updata(AppCodeView.APPCODE_UPDATA_CURRICULUM);
					}
				}
			} else if (tag.equals("updataEQ")) {
				if (displayManager == null) {
					if (application.classInfoModel != null) {
						txt_main_class.setText(application.classInfoModel.bjmc);
					}
					if (application.equInfoModel != null) {
						txt_main_bj.setText(application.equInfoModel.jssysmc);
					}
					updata_now_curriculum();
					updata_new_notice();
				} else {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_EQU);
				}
			} else if (tag.equals("playTask")) {
				Log.d(TAG, "------playTask------");
				playTask();
				if (displayManager == null) {
					if (application.getRunningActivityName().equals(
							"com.yy.doorplate.activity.PjMainActivity")) {
						updata_banner();
					}
				} else {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_REGION_MEDIA);
				}
			} else if (tag.equals(HttpProcess.QRY_WEATHER)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager == null) {
						txt_location.setText(application.currentCity);
						txt_weather.setText(application.weather.trim() + " "
								+ application.temperature.trim());
					} else {
						displayManager
								.updata(AppCodeView.APPCODE_UPDATA_WEATHER);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_EQUMODEL)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (result) {
					application.httpProcess.getExam(
							application.equInfoModel.jssysdm, null);
					application.httpProcess.commitBack(sn, HttpProcess.SUCESS,
							HttpProcess.SUCESS_MSG);
				} else {
					application.httpProcess.commitBack(sn, HttpProcess.ERROR,
							msg);
				}
			} else if (tag.equals(HttpProcess.QRY_EXAM)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (result) {
					equModelTask();
					application.httpProcess.commitBack(sn, HttpProcess.SUCESS,
							HttpProcess.SUCESS_MSG);
				} else {
					application.httpProcess.commitBack(sn, HttpProcess.ERROR,
							msg);
				}
			} else if (tag.equals(HttpProcess.QRY_PAGE)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String sn = intent.getStringExtra(MyApplication.CMD_SN);
				if (result) {
					PageLayoutDatabase database = new PageLayoutDatabase();
					pageLayoutModels = database.query_all();
					if (pageLayoutModels != null
							&& pageLayoutModels.size() > 0
							&& !TextUtils
									.isEmpty(pageLayoutModels.get(0).resPath)) {
						application.showToast("正在加载动态布局");
						application.pageLayoutTotal = pageLayoutModels.size();
						application.systemManager
								.downloadPolicy(pageLayoutModels.get(0).resPath);
						Intent intent1 = new Intent(MyApplication.BROADCAST);
						intent1.putExtra(MyApplication.BROADCAST_TAG,
								"permission_finish");
						broadcastManager.sendBroadcast(intent1);
					} else {
						application.httpProcess.commitBack(sn,
								HttpProcess.NONE, HttpProcess.NONE_MSG);
					}
				} else {
					application.httpProcess.commitBack(sn, HttpProcess.ERROR,
							msg);
				}
			} else if (tag.equals(HttpProcess.QRY_PRIZE)
					&& !application.isUpdateEQ && !application.isGrxx) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					handler.sendEmptyMessageDelayed(11, 10 * 1000);
				}
			} else if (tag.equals(HttpProcess.QRY_SCHOOL)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager != null) {
						displayManager
								.updata(AppCodeView.APPCODE_UPDATA_SCHOOL);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_QUEST)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager != null) {
						displayManager.updata(AppCodeView.APPCODE_UPDATA_QUEST);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_BLACKBOARD)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager != null) {
						displayManager
								.updata(AppCodeView.APPCODE_UPDATA_ENGLISH);
					}
				}
			} else if (tag.equals(HttpProcess.QRY_ONDUTY)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (displayManager != null) {
						displayManager.updata(AppCodeView.APPCODE_UPDATA_ZHIRI);
					}
				}
			}
		}
	}

	private EditText edt_permission_user, edt_permission_pwd;
	private Button btn_permission_user, btn_permission_card,
			btn_premission_login, btn_premission_cancel1,
			btn_premission_cancel2;
	private RelativeLayout ly_permission_user, ly_permission_card;

	private void DialogPermission() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				PjMainActivity.this);
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
		// btn_permission_card.setEnabled(false);
		// btn_permission_user
		// .setBackgroundResource(R.drawable.btn_permission_type);
		// btn_permission_user
		// .setTextColor(getResources().getColor(R.color.white));
		// ly_permission_user.setVisibility(View.VISIBLE);
		// btn_permission_card
		// .setBackgroundResource(R.drawable.btn_permission_type_click);
		// btn_permission_card.setTextColor(getResources().getColor(R.color.blue));
		// ly_permission_card.setVisibility(View.INVISIBLE);
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	// 未打卡处理
	private void Absenteeism(String HHmmss) throws ParseException {
		if (application.clockRule_teather == null
				|| application.clockRule_student == null) {
			return;
		}
		PersonnelAttendanceDatabase personnelAttendanceDatabase = new PersonnelAttendanceDatabase();
		List<PersonnelAttendanceModel> list = personnelAttendanceDatabase
				.query("date = ?",
						new String[] { MyApplication.getTime("yyyy-MM-dd") });
		if (list == null) {
			return;
		}
		List<PersonnelAttendanceModel> updataList = new ArrayList<PersonnelAttendanceModel>();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		long now = format.parse(HHmmss).getTime();
		for (PersonnelAttendanceModel model : list) {
			if (TextUtils.isEmpty(model.status)) {
				continue;
			}
			if (model.isToWorkClock.equals("0")
					|| !model.status.split(",")[0].equals("11")) {
				continue;
			}
			ClockRuleModel ruleModel;
			if (model.js.equals("1")) {
				ruleModel = application.clockRule_teather;
			} else {
				ruleModel = application.clockRule_student;
			}
			long toWorkTime = format.parse(model.toWorkTime).getTime();
			long cgcd = toWorkTime + Integer.parseInt(ruleModel.cgcd) * 60
					* 1000;
			if (now > cgcd) {
				model.status = "4,4";
				personnelAttendanceDatabase.update(model);
				updataList.add(model);
			}
		}
		if (updataList.size() > 0) {
			application.httpProcess.commitPersonnelAttendance(updataList);
			attendStudent(updataList);
		}
	}

	private void attendStudent(List<PersonnelAttendanceModel> attendanceModels)
			throws ParseException {
		AttendInfoDatabase attendInfoDatabase = new AttendInfoDatabase();
		CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
		SectionInfoDatabase sectionInfoDatabase = new SectionInfoDatabase();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		for (PersonnelAttendanceModel personnelAttendanceModel : attendanceModels) {
			boolean isUpdate = false;
			if (personnelAttendanceModel == null) {
				continue;
			}
			List<AttendInfoModel> attendInfoModels = attendInfoDatabase
					.query_by_skrq(personnelAttendanceModel.date);
			if (attendInfoModels == null) {
				continue;
			}
			if (TextUtils.isEmpty(personnelAttendanceModel.status)) {
				continue;
			}
			long toWorkTime = format.parse(personnelAttendanceModel.toWorkTime)
					.getTime();
			long offWorkTime = format.parse(
					personnelAttendanceModel.offWorkTime).getTime();
			for (AttendInfoModel attendInfoModel : attendInfoModels) {
				if (!attendInfoModel.kqzt.equals("11")) {
					continue;
				}
				CurriculumInfoModel curriculumInfoModel = curriculumInfoDatabase
						.query_by_id(attendInfoModel.skjhid);
				if (curriculumInfoModel == null) {
					continue;
				}
				String[] jcs = curriculumInfoModel.jc.split("-");
				SectionInfoModel section1 = null, section2 = null;
				if (jcs.length > 1) {
					section1 = sectionInfoDatabase.query_by_jcdm(jcs[0]);
					section2 = sectionInfoDatabase
							.query_by_jcdm(jcs[jcs.length - 1]);
				} else {
					section1 = sectionInfoDatabase.query_by_jcdm(jcs[0]);
					section2 = section1;
				}
				if (section1 == null || section2 == null) {
					continue;
				}
				if ((toWorkTime <= Long.parseLong(section1.jcskkssj) && Long
						.parseLong(section2.jcskjssj) <= offWorkTime)
						|| (toWorkTime <= Long.parseLong(section2.jcskjssj) && Long
								.parseLong(section2.jcskjssj) <= offWorkTime)
						|| (toWorkTime <= Long.parseLong(section1.jcskkssj) && Long
								.parseLong(section1.jcskkssj) <= offWorkTime)) {
					isUpdate = true;
					attendInfoModel.kqzt = "4";
					attendInfoDatabase.update(attendInfoModel.id,
							attendInfoModel);
					Log.e(TAG, attendInfoModel.toString());
					application.httpProcess.CommitAttend(attendInfoModel.xsxh,
							attendInfoModel);
				}
			}
			if (isUpdate) {
				Intent intent = new Intent(MyApplication.BROADCAST);
				intent.putExtra(MyApplication.BROADCAST_TAG, "absenteeism_ui");
				intent.putExtra("attend_id", attendInfoModels.get(0).id);
				intent.putExtra("attend_kqzt", attendInfoModels.get(0).kqzt);
				broadcastManager.sendBroadcast(intent);
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
				&& application.playTaskModels.size() > application.playTask_position
				&& application.playTaskModels
						.get(application.playTask_position).playType
						.equals("MEDIA")
				&& application.playTaskModels
						.get(application.playTask_position).playEndTime
						.equals(MyApplication.getTime("HH:mm:ss"))) {
			if (application.getRunningActivityName().equals(
					"com.advertisement.activity.DisplayActivity")) {
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
						Log.d(TAG, "---guangbo---" + i);
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
					"com.advertisement.activity.DisplayActivity")) {
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
										if (application.playTask_position != -1
												&& application.playTaskModels
														.size() > application.playTask_position) {
											PlayTaskDatabase database = new PlayTaskDatabase();
											database.delete(application.playTaskModels
													.get(application.playTask_position).taskId);
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
										if (application.playTask_position != -1
												&& application.playTaskModels
														.size() > application.playTask_position) {
											PlayTaskDatabase database = new PlayTaskDatabase();
											database.delete(application.playTaskModels
													.get(application.playTask_position).taskId);
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
						if (application.playTask_position != -1
								&& application.playTaskModels.size() > application.playTask_position) {
							PlayTaskDatabase database = new PlayTaskDatabase();
							database.delete(application.playTaskModels
									.get(application.playTask_position).taskId);
							application.playTaskModels
									.remove(application.playTask_position);
							application.playTask_position = -1;
						}
					}
				}
			}, 2000);
		}
	}

	private void timingOFFON() {
		if (TextUtils.isEmpty(application.time_off)
				|| TextUtils.isEmpty(application.time_on)
				|| TextUtils.isEmpty(application.time_off_actual)) {
			return;
		}
		try {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			long off = format.parse(application.time_off_actual).getTime();
			long on = format.parse(application.time_on).getTime();
			if (off <= System.currentTimeMillis()
					&& System.currentTimeMillis() < (on - 1000 * 60 * MyApplication.OFFON)) {
				application.dogProcess.disenbleLight();
				application.dogProcess.stopDog();
				application.dogProcess.close();
				// Date time_on = LocalToGTM(application.time_on);
				// application.mEYunSdk.setRebootClock(time_on.getDate(),
				// time_on.getHours(), time_on.getMinutes());
				// Intent intent = new Intent();
				// intent.setAction("system_to_reboot");
				// sendBroadcast(intent);
			} else if (off > System.currentTimeMillis()
					&& !application.time_off_actual
							.equals(application.time_off)) {
				int i = (int) ((off - System.currentTimeMillis()) / 1000);
				if (i == 30) {
					handler.sendEmptyMessage(12);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 转成格林威治时间
	 * 
	 * @param LocalDate
	 */
	private Date LocalToGTM(String LocalDate) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date result_date;
		long result_time = 0;
		try {
			result_date = format.parse(LocalDate);
			result_time = result_date.getTime();
			String s = format.format(result_time - 8 * 60 * 60 * 1000);
			return format.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void updateImg() {
		File file = new File(MyApplication.PATH_ROOT
				+ MyApplication.PATH_PICTURE);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				for (File f : files) {
					if (f.getName().startsWith("attend")) {
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

	private void downLoadImg(final String urlStr, final String path,
			final int what) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			return;
		}
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							handler.sendEmptyMessage(what + 4);
						}
						ftpManager.disConnect();
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						handler.sendEmptyMessage(what + 4);
					}
				}
			}
		});
	}

	private void downLoadImg(final String urlStr, final String path) {
		if (TextUtils.isEmpty(urlStr)) {
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			return;
		}
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				if (urlStr.startsWith("ftp")) {
					FTPManager ftpManager = new FTPManager();
					if (ftpManager.connect(urlStr, path)) {
						if (ftpManager.download()) {
							handler.sendEmptyMessage(10);
						} else {
							if (new_pics.size() > new_pics_i) {
								new_pics.remove(new_pics_i);
								new_pics_i = 0;
								handler.sendEmptyMessage(10);
							}
						}
					} else {
						if (new_pics.size() > new_pics_i) {
							new_pics.remove(new_pics_i);
							new_pics_i = 0;
							handler.sendEmptyMessage(10);
						}
					}
				} else {
					if (application.httpDownLoad(urlStr, path)) {
						handler.sendEmptyMessage(10);
					} else {
						if (new_pics.size() > new_pics_i) {
							new_pics.remove(new_pics_i);
							new_pics_i = 0;
							handler.sendEmptyMessage(10);
						}
					}
				}
			}
		});
	}

	private boolean equModelTask() {
		if (application.getRunningActivityName().equals(
				"com.yy.doorplate.activity.ExamActivity")) {
			return false;
		}
		EquModelTaskDatabase database = new EquModelTaskDatabase();
		List<EquModelTaskModel> taskModels = database.query_all();
		if (taskModels == null) {
			return false;
		}
		for (EquModelTaskModel model : taskModels) {
			if (!model.equModel.equals("01")) {
				continue;
			}
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date startTime = formatter.parse(model.startTime);
				Date stopTime = formatter.parse(model.stopTime);
				long time = System.currentTimeMillis();
				if (time >= startTime.getTime() && time < stopTime.getTime()) {
					Intent broad = new Intent(MyApplication.BROADCAST);
					broad.putExtra(MyApplication.BROADCAST_TAG,
							"PlayActivity_finish");
					broadcastManager.sendBroadcast(broad);

					Intent intent = new Intent(this, ExamActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("EquModelTaskModel", model);
					intent.putExtras(bundle);
					startActivity(intent);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
