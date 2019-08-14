package com.yy.doorplate.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.OnDutyInfoDatabase;
import com.yy.doorplate.model.OnDutyInfoModel;

public class ZhiriActivity extends Activity {

	private final String TAG = "ZhiriActivity";

	private MyApplication application;

	private Button btn_zhiri_back;
	private TextView txt_zhiri_yue, txt_weather;
	private GridView grid_zhiri;
	private TextClock textClock;

	private String[] numArray = { "一月", "二月", "三月", "四月", "五月", "六月", "七月",
			"八月", "九月", "十月", "十一月", "十二月" };

	private SimpleDateFormat format;
	private Calendar calendar = Calendar.getInstance();

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhiri);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	private void initView() {
		btn_zhiri_back = (Button) findViewById(R.id.btn_zhiri_back);
		txt_zhiri_yue = (TextView) findViewById(R.id.txt_zhiri_yue);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		grid_zhiri = (GridView) findViewById(R.id.grid_zhiri);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());

		txt_zhiri_yue.setText(numArray[calendar.get(Calendar.MONTH)]);
		btn_zhiri_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		updata();
	}

	private void updata() {
		Date date = getMonthStart();
		Date monthEnd = getMonthEnd();
		List<Map<String, String>> list = null;
		while (!date.after(monthEnd)) {
			calendar.setTime(date);
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
					|| calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				date = getNext(date);
				continue;
			} else {
				if (list == null) {
					list = new ArrayList<Map<String, String>>();
					switch (calendar.get(Calendar.DAY_OF_WEEK)) {
					case Calendar.MONDAY: {
						Map<String, String> map = new HashMap<String, String>();
						map.put("zhiri", findZhiri(date));
						list.add(map);
						break;
					}
					case Calendar.TUESDAY: {
						Map<String, String> map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", findZhiri(date));
						list.add(map);
						break;
					}
					case Calendar.WEDNESDAY: {
						Map<String, String> map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", findZhiri(date));
						list.add(map);
						break;
					}
					case Calendar.THURSDAY: {
						Map<String, String> map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", findZhiri(date));
						list.add(map);
						break;
					}
					case Calendar.FRIDAY: {
						Map<String, String> map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", "");
						list.add(map);
						map = new HashMap<String, String>();
						map.put("zhiri", findZhiri(date));
						list.add(map);
						break;
					}
					}
				} else {
					Map<String, String> map = new HashMap<String, String>();
					map.put("zhiri", findZhiri(date));
					list.add(map);
				}
			}
			date = getNext(date);
		}
		if (list != null && list.size() > 0 && list.size() <= 25) {
			grid_zhiri.setAdapter(new SimpleAdapter(application, list,
					R.layout.item_zhiri, new String[] { "zhiri" },
					new int[] { R.id.txt_item_zhiri }));
		}
	}

	private String findZhiri(Date date) {
		format = new SimpleDateFormat("yyyy-MM-dd");
		String s = "";
		OnDutyInfoDatabase database = new OnDutyInfoDatabase();
		List<OnDutyInfoModel> dutyInfoModels = database.query_by_date(format
				.format(date));
		if (dutyInfoModels != null) {
			for (int i = 0; i < dutyInfoModels.size(); i++) {
				if (!TextUtils.isEmpty(dutyInfoModels.get(i).xm)) {
					if (i == 0) {
						s = s + dutyInfoModels.get(i).xm;
					} else {
						s = s + "、" + dutyInfoModels.get(i).xm;
					}
				}
			}
		}
		return s;
	}

	private Date getMonthStart() {
		Date date = new Date(System.currentTimeMillis());
		calendar.setTime(date);
		int index = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.add(Calendar.DATE, (1 - index));
		return calendar.getTime();
	}

	private Date getMonthEnd() {
		Date date = new Date(System.currentTimeMillis());
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		int index = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.add(Calendar.DATE, (-index));
		return calendar.getTime();
	}

	private Date getNext(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		return calendar.getTime();
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
}
