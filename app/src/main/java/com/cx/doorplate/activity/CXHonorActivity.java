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
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.cx.doorplate.fragment.HonorFragment;
import com.cx.doorplate.fragment.StarFragment;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.MyViewPagerAdapter;
import com.yy.doorplate.database.PrizeInfoDatabase;
import com.yy.doorplate.database.StarDatabase;
import com.yy.doorplate.model.PrizeInfoModel;
import com.yy.doorplate.model.StarModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.view.CustomViewpager;

public class CXHonorActivity extends FragmentActivity implements
		OnClickListener {

	private final String TAG = "CXHonorActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private Button btn_cx_back, btn_honor_left, btn_honor_right;
	private TextView txt_weather,txt_honor_title;
	private TextClock textClock;
//	private ImageView img_honor_title;
	private CustomViewpager vp_honor;

	private ImageLoader imageLoader = null;

	private List<Fragment> fragments;
	private int fragment_total, fragment_i;

	// 1学校荣誉；2班级荣誉；3班级之星
	private int type = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_honor);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		type = getIntent().getIntExtra("type", 1);

		initView();
	}

	private void initView() {
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
		btn_honor_left = (Button) findViewById(R.id.btn_honor_left);
		btn_honor_right = (Button) findViewById(R.id.btn_honor_right);
		txt_honor_title = (TextView) findViewById(R.id.txt_honor_title);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		vp_honor = (CustomViewpager) findViewById(R.id.vp_honor);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());

		btn_cx_back.setOnClickListener(this);
		btn_honor_left.setOnClickListener(this);
		btn_honor_right.setOnClickListener(this);

		vp_honor.setOnPageChangeListener(new OnPageChangeListener() {

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

		if (type == 1) {
			txt_honor_title.setText("学校荣誉");
		} else if (type == 2) {
//			img_honor_title.setImageResource(R.drawable.cx_img_honor_title2);
			txt_honor_title.setText("班级荣誉");
		} else if (type == 3) {
//			img_honor_title.setImageResource(R.drawable.cx_img_honor_title3);
			txt_honor_title.setText("班级之星");
		}

		updata();
	}

	private void updata() {
		fragment_i = 0;
		fragment_total = 0;
		fragments = new ArrayList<Fragment>();
		if (type == 1 || type == 2) {
			PrizeInfoDatabase database = new PrizeInfoDatabase();
			List<PrizeInfoModel> list = null;
			if (type == 1) {
				list = database.query_by_prizeType("SCHOOL");
			} else {
				list = database.query_by_prizeType("CLASS");
			}
			if (list != null && list.size() > 0) {
				if (list.size() % 4 == 0) {
					fragment_total = list.size() / 4;
				} else {
					fragment_total = list.size() / 4 + 1;
				}
			}
			if (fragment_total > 0) {
				for (int i = 0; i < fragment_total; i++) {
					HonorFragment fragment = new HonorFragment();
					fragment.setImageLoader(imageLoader);
					if (i == fragment_total - 1) {
						fragment.setPrizeInfoModels(list.subList(4 * i,
								list.size()));
					} else {
						fragment.setPrizeInfoModels(list.subList(4 * i,
								4 * i + 4));
					}
					fragments.add(fragment);
				}
				MyViewPagerAdapter adapter = new MyViewPagerAdapter(
						getSupportFragmentManager(), fragments);
				vp_honor.setAdapter(adapter);
				btn_honor_left.setVisibility(View.VISIBLE);
				btn_honor_right.setVisibility(View.VISIBLE);
			} else {
				vp_honor.setAdapter(null);
				btn_honor_left.setVisibility(View.INVISIBLE);
				btn_honor_right.setVisibility(View.INVISIBLE);
			}
		} else if (type == 3) {
			StarDatabase starDatabase = new StarDatabase();
			List<StarModel> starModels = starDatabase.query("starType = ?",
					new String[] { "2" });
			if (starModels != null && starModels.size() > 0) {
				if (starModels.size() % 4 == 0) {
					fragment_total = starModels.size() / 4;
				} else {
					fragment_total = starModels.size() / 4 + 1;
				}
			}
			if (fragment_total > 0) {
				for (int i = 0; i < fragment_total; i++) {
					StarFragment fragment = new StarFragment();
					fragment.setImageLoader(imageLoader);
					if (i == fragment_total - 1) {
						fragment.setStarModels(starModels.subList(4 * i,
								starModels.size()));
					} else {
						fragment.setStarModels(starModels.subList(4 * i,
								4 * i + 4));
					}
					fragments.add(fragment);
				}
				MyViewPagerAdapter adapter = new MyViewPagerAdapter(
						getSupportFragmentManager(), fragments);
				vp_honor.setAdapter(adapter);
				btn_honor_left.setVisibility(View.VISIBLE);
				btn_honor_right.setVisibility(View.VISIBLE);
			} else {
				vp_honor.setAdapter(null);
				btn_honor_left.setVisibility(View.INVISIBLE);
				btn_honor_right.setVisibility(View.INVISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cx_back:
			finish();
			break;
		case R.id.btn_honor_left:
			if (fragment_i > 0) {
				fragment_i--;
				vp_honor.setCurrentItem(fragment_i);
			}
			break;
		case R.id.btn_honor_right:
			if (fragment_i < fragment_total - 1) {
				fragment_i++;
				vp_honor.setCurrentItem(fragment_i);
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
