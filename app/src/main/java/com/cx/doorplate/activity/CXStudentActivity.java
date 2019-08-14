package com.cx.doorplate.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.cx.doorplate.fragment.StudentAllFragment;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.MyViewPagerAdapter;
import com.yy.doorplate.database.StudentInfoDatabase;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.view.CustomViewpager;

public class CXStudentActivity extends FragmentActivity implements
		OnClickListener {

	private final String TAG = "CXStudentActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private Button btn_cx_back, btn_student_left, btn_student_right;
	private TextView txt_weather;
	private TextClock textClock;
	private CustomViewpager vp_student;

	private ImageLoader imageLoader = null;

	private List<StudentInfoModel> studentInfoModels = null;
	private List<Fragment> fragments;
	private int fragment_total, fragment_i;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_student);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	private void initView() {
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
		btn_student_left = (Button) findViewById(R.id.btn_student_left);
		btn_student_right = (Button) findViewById(R.id.btn_student_right);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		vp_student = (CustomViewpager) findViewById(R.id.vp_student);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyyƒÍMM‘¬dd»’\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());

		btn_cx_back.setOnClickListener(this);
		btn_student_left.setOnClickListener(this);
		btn_student_right.setOnClickListener(this);

		vp_student.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				fragment_i = arg0;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		updata_student();
	}

	private void updata_student() {
		StudentInfoDatabase studentInfoDatabase = new StudentInfoDatabase();
		studentInfoModels = studentInfoDatabase
				.query_by_ssbj(application.equInfoModel.bjdm);
		fragment_i = 0;
		fragment_total = 0;
		fragments = new ArrayList<Fragment>();
		if (studentInfoModels != null && studentInfoModels.size() > 0) {
			if (studentInfoModels.size() % 10 == 0) {
				fragment_total = studentInfoModels.size() / 10;
			} else {
				fragment_total = studentInfoModels.size() / 10 + 1;
			}
		}
		if (fragment_total > 0) {
			for (int i = 0; i < fragment_total; i++) {
				StudentAllFragment fragment = new StudentAllFragment();
				fragment.setImageLoader(imageLoader);
				if (i == fragment_total - 1) {
					fragment.setList(studentInfoModels.subList(10 * i,
							studentInfoModels.size()));
				} else {
					fragment.setList(studentInfoModels.subList(10 * i,
							10 * i + 10));
				}
				fragments.add(fragment);
			}
			MyViewPagerAdapter adapter = new MyViewPagerAdapter(
					getSupportFragmentManager(), fragments);
			vp_student.setAdapter(adapter);
			btn_student_left.setVisibility(View.VISIBLE);
			btn_student_right.setVisibility(View.VISIBLE);
		} else {
			vp_student.setAdapter(null);
			btn_student_left.setVisibility(View.INVISIBLE);
			btn_student_right.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cx_back:
			finish();
			break;
		case R.id.btn_student_left:
			if (fragment_i > 0) {
				fragment_i--;
				vp_student.setCurrentItem(fragment_i);
			}
			break;
		case R.id.btn_student_right:
			if (fragment_i < fragment_total - 1) {
				fragment_i++;
				vp_student.setCurrentItem(fragment_i);
			}
			break;
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
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
}
