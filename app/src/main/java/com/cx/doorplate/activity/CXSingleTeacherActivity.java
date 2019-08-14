package com.cx.doorplate.activity;

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

import com.cx.doorplate.fragment.TeacherAllFragment;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.MyViewPagerAdapter;
import com.yy.doorplate.database.TeacherInfoDatabase;
import com.yy.doorplate.model.TeacherInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.view.CustomViewpager;

import java.util.ArrayList;
import java.util.List;

public class CXSingleTeacherActivity extends FragmentActivity implements OnClickListener {

	private final String TAG = "CXTeacherActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private Button btn_cx_back, btn_teacher_left, btn_teacher_right;
	private TextView txt_weather;
	private TextClock textClock;
	private CustomViewpager vp_teacher;

	private ImageLoader imageLoader = null;

	private List<TeacherInfoModel> teacherInfoModels = null;
	private TeacherInfoModel teacherInfoModel;
	private List<Fragment> fragments;
	private int fragment_total, fragment_i;
	private String teacherId=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_teacher);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);
		Intent myIntent=getIntent();
		if (myIntent != null) {
			teacherId = myIntent.getStringExtra("id");
		}
		initView();
	}

	private void initView() {
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
		btn_teacher_left = (Button) findViewById(R.id.btn_teacher_left);
		btn_teacher_right = (Button) findViewById(R.id.btn_teacher_right);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		vp_teacher = (CustomViewpager) findViewById(R.id.vp_teacher);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyyƒÍMM‘¬dd»’\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   " + application.temperature.trim() + "\n"
				+ application.weather.trim());

		btn_cx_back.setOnClickListener(this);
		btn_teacher_left.setOnClickListener(this);
		btn_teacher_right.setOnClickListener(this);

		vp_teacher.setOnPageChangeListener(new OnPageChangeListener() {

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

		updata_teacher();
	}

	private void updata_teacher() {
		TeacherInfoDatabase teacherInfoDatabase = new TeacherInfoDatabase();
		if(teacherId==null) {
			teacherInfoModels = teacherInfoDatabase.query_all();
		}
		else
		{
			teacherInfoModel = teacherInfoDatabase.query_by_id(teacherId);
			if(teacherInfoModel!=null) {
				teacherInfoModels=new ArrayList<TeacherInfoModel>();
				teacherInfoModels.add(teacherInfoModel);
			}
		}
		fragment_i = 0;
		fragment_total = 0;
		fragments = new ArrayList<Fragment>();
		if (teacherInfoModels != null && teacherInfoModels.size() > 0) {
			if (teacherInfoModels.size() % 10 == 0) {
				fragment_total = teacherInfoModels.size() / 10;
			} else {
				fragment_total = teacherInfoModels.size() / 10 + 1;
			}
		}
		if (fragment_total > 0) {
			for (int i = 0; i < fragment_total; i++) {
				TeacherAllFragment fragment = new TeacherAllFragment();
				fragment.setImageLoader(imageLoader);
				if (i == fragment_total - 1) {
					fragment.setList(teacherInfoModels.subList(10 * i, teacherInfoModels.size()));
				} else {
					fragment.setList(teacherInfoModels.subList(10 * i, 10 * i + 10));
				}
				fragments.add(fragment);
			}
			MyViewPagerAdapter adapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragments);
			vp_teacher.setAdapter(adapter);
			btn_teacher_left.setVisibility(View.VISIBLE);
			btn_teacher_right.setVisibility(View.VISIBLE);
		} else {
			vp_teacher.setAdapter(null);
			btn_teacher_left.setVisibility(View.INVISIBLE);
			btn_teacher_right.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cx_back:
			finish();
			break;
		case R.id.btn_teacher_left:
			if (fragment_i > 0) {
				fragment_i--;
				vp_teacher.setCurrentItem(fragment_i);
			}
			break;
		case R.id.btn_teacher_right:
			if (fragment_i < fragment_total - 1) {
				fragment_i++;
				vp_teacher.setCurrentItem(fragment_i);
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
		application.handler_touch.sendEmptyMessageDelayed(0, application.screensaver_time * 1000);
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
			application.handler_touch.sendEmptyMessageDelayed(0, application.screensaver_time * 1000);
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
			application.handler_touch.sendEmptyMessageDelayed(0, application.screensaver_time * 1000);
			break;
		}
		try {
			return super.dispatchTouchEvent(ev);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
