package com.cx.doorplate.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

public class CXWeatherActivity extends Activity implements OnClickListener {
	private static final String TAG = "CXWeatherActivity";

	private MyApplication application;
	private LocalBroadcastManager localBroadcastManager;
	private MyBroadcastReceiver broadcastReceiver;

	private Button btn_cx_back;
	private TextClock textClock, tc_weather_time;

	private ProgressDialog progressDialog = null;

	private TextView txt_weather_today_tem, txt_weather_fx, txt_weather_quality, txt_weather_aqipm, txt_weather_notice,
			txt_weather_gonggao,txt_weather_cityname;

	private LinearLayout ly_yubao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_weather);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();
		localBroadcastManager = LocalBroadcastManager.getInstance(application);

		broadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		localBroadcastManager.registerReceiver(broadcastReceiver, filter);

		initView();
		application.httpProcess.qryCityWeather();
		showProgressDialog();
	}

	private void initView() {
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);
		textClock = (TextClock) findViewById(R.id.textClock);

		tc_weather_time = (TextClock) findViewById(R.id.tc_weather_time);
		ly_yubao = (LinearLayout) findViewById(R.id.ly_yubao);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
		tc_weather_time.setFormat12Hour(null);
		tc_weather_time.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");

		txt_weather_today_tem = (TextView) findViewById(R.id.txt_weather_today_tem);
		txt_weather_fx = (TextView) findViewById(R.id.txt_weather_fx);
		txt_weather_quality = (TextView) findViewById(R.id.txt_weather_quality);
		txt_weather_aqipm = (TextView) findViewById(R.id.txt_weather_aqipm);
		txt_weather_notice = (TextView) findViewById(R.id.txt_weather_notice);
		txt_weather_gonggao = (TextView) findViewById(R.id.txt_weather_gonggao);
		txt_weather_cityname=(TextView) findViewById(R.id.txt_weather_cityname);
		btn_cx_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cx_back:
			finish();
			break;

		}
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this, null, "加载中", false, false);
		}
	}

	private void closeProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}

			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			boolean result = intent.getBooleanExtra(MyApplication.CMD_RESULT, false);
			String msg = intent.getStringExtra(MyApplication.CMD_MSG);
			if (tag.equals("permission_finish")) {
				closeProgressDialog();
				finish();
			} else if (tag.equals(HttpProcess.QRY_CITY_WEATHER)) {
				if (result) {
					String weatherInfoStr = intent.getStringExtra("weatherInfo");
					try {
						JSONObject weatherInfo = new JSONObject(weatherInfoStr);
						JSONObject cityInfo=weatherInfo.getJSONObject("cityInfo");
						String city = cityInfo.getString("city");
						JSONObject data=weatherInfo.getJSONObject("data");
						String wendu = data.getString("wendu");
						String shidu = data.getString("shidu");
						String quality = data.getString("quality");
						String pm25 = data.getString("pm25");
						String ganmao = data.getString("ganmao");
						JSONArray forecast = data.getJSONArray("forecast");
						if (forecast != null && forecast.length() > 0) {
							String fx = forecast.getJSONObject(0).getString("fx");
							String fl = forecast.getJSONObject(0).getString("fl");
							Integer aqi = forecast.getJSONObject(0).getInt("aqi");
							String type = forecast.getJSONObject(0).getString("type");
							String notice = forecast.getJSONObject(0).getString("notice");
							txt_weather_cityname.setText(city);
							txt_weather_today_tem.setText(wendu + "℃");
							txt_weather_fx.setText(type+"\n"+fx + fl + "\t" + shidu);
							txt_weather_quality.setText("空气质量：" + quality);
							txt_weather_aqipm.setText("AQI:" + aqi + "\tPM2.5:" + pm25);
							txt_weather_notice.setText(notice);
							txt_weather_gonggao.setText(ganmao);
						}
						for (int i = 1; i < forecast.length(); i++) {
							String date = forecast.getJSONObject(i).getString("date");
							String high = forecast.getJSONObject(i).getString("high");
							String low = forecast.getJSONObject(i).getString("low");
							String type = forecast.getJSONObject(i).getString("type");
							String fx = forecast.getJSONObject(i).getString("fx");
							String fl = forecast.getJSONObject(i).getString("fl");
							Integer aqi = forecast.getJSONObject(i).getInt("aqi");
							TextView textView = new TextView(CXWeatherActivity.this);
							textView.setWidth(10);
							textView.setHeight(15);
							textView.setTextSize(25);
							textView.setGravity(Gravity.CENTER);
							textView.setTextColor(Color.WHITE);
							textView.setPadding(5, 5, 5, 5);
							textView.setText(
									date + "\n" + type + "\n最" + high + "\n最" + low + "\n" + fx + fl + "\nAQI:" + aqi);
							textView.setBackgroundResource(R.drawable.cx_weather_day_bg);
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 250);
							params.setMargins(5, 0, 5, 0);
							textView.setLayoutParams(params);
							ly_yubao.addView(textView);
						}
						closeProgressDialog();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					application.showToast("查询失败," + msg);
					closeProgressDialog();
					finish();
				}

			}
		}
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
		return super.dispatchTouchEvent(ev);
	}
}
