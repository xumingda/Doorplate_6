package com.yy.doorplate.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.tool.EthernetUtils;
import com.yy.doorplate.tool.WifiUtils;

public class SettingMoreActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener {

	private final String TAG = "SettingMoreActivity";

	private MyApplication application;

	private RelativeLayout ly_setlight;
	private TextView txt_set_info;
	private Button btn_set_back, btn_set_info, btn_set_light;
	private SeekBar seekbar_light;
	private CheckBox cb_isautobrightness;

	private int type = 1;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_more);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	private void initView() {
		ly_setlight = (RelativeLayout) findViewById(R.id.ly_setlight);
		txt_set_info = (TextView) findViewById(R.id.txt_set_info);
		btn_set_back = (Button) findViewById(R.id.btn_set_back);
		btn_set_info = (Button) findViewById(R.id.btn_set_info);
		btn_set_light = (Button) findViewById(R.id.btn_set_light);
		seekbar_light = (SeekBar) findViewById(R.id.seekbar_light);
		cb_isautobrightness = (CheckBox) findViewById(R.id.cb_isautobrightness);

		btn_set_back.setOnClickListener(this);
		btn_set_info.setOnClickListener(this);
		btn_set_light.setOnClickListener(this);

		if (type == 1) {
			btn_set_info
					.setBackgroundResource(R.drawable.btn_moresetting_label);
			btn_set_light.setBackground(null);
			txt_set_info.setVisibility(View.VISIBLE);
			ly_setlight.setVisibility(View.INVISIBLE);
		} else if (type == 2) {
			btn_set_info.setBackground(null);
			btn_set_light
					.setBackgroundResource(R.drawable.btn_moresetting_label);
			txt_set_info.setVisibility(View.INVISIBLE);
			ly_setlight.setVisibility(View.VISIBLE);
		}
		initInfo();

		seekbar_light.setOnSeekBarChangeListener(this);
		seekbar_light.setMax(250);
		seekbar_light.setProgress(getScreenBrightness());
		cb_isautobrightness.setChecked(application.isAutoBrightness);
		cb_isautobrightness
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						application.setAutoBrightness(isChecked);
					}
				});
	}

	private void initInfo() {
		String ip = null, mac = null;
		NetworkInfo networkInfo = (NetworkInfo) ((ConnectivityManager) application
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		mac = MyApplication.getMacAddress();
		if (networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			WifiUtils wifiUtils = new WifiUtils(application);
			ip = wifiUtils.getWifiInfoFromDhcpIp();
		} else if (networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
			EthernetUtils ethernetUtils = new EthernetUtils(application);
			if (ethernetUtils.isUsingStaticIp()) {
				ip = ethernetUtils.getEthInfoFromStaticIP();
			} else {
				ip = EthernetUtils.getEthInfoFromDhcpIP();
			}
			mac = MyApplication.getMacAddress();
		}

		if (TextUtils.isEmpty(ip)) {
			ip = "无网络";
		}
		if (TextUtils.isEmpty(mac)) {
			mac = "无网络";
		}
		txt_set_info.setText("本机编号 : " + " " + MyApplication.getSN() + "\n"
				+ "网络地址 : " + " " + ip + "\n" + "MAC地址 : " + " " + mac + "\n"
				+ "软件版本 : " + " " + application.getVersionName() + "\n"
				+ "硬件版本 : " + " " + "润金3288主板" + "\n" + "系统版本 : " + " "
				+ android.os.Build.DISPLAY + "\n" + "监控板版本 : "
				+ application.dogVersion);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_set_back:
			finish();
			break;
		case R.id.btn_set_info:
			btn_set_info
					.setBackgroundResource(R.drawable.btn_moresetting_label);
			btn_set_light.setBackground(null);
			txt_set_info.setVisibility(View.VISIBLE);
			ly_setlight.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_set_light:
			btn_set_info.setBackground(null);
			btn_set_light
					.setBackgroundResource(R.drawable.btn_moresetting_label);
			txt_set_info.setVisibility(View.INVISIBLE);
			ly_setlight.setVisibility(View.VISIBLE);
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		saveScreenBrightness(progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
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

	private int getScreenBrightness() {
		int screenBrightness = -1;
		try {
			screenBrightness = Settings.System.getInt(
					this.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception localException) {

		}
		return screenBrightness;
	}

	private void saveScreenBrightness(int light) {
		Intent intent = new Intent();
		intent.setAction("set_screen_brightness");
		intent.putExtra("screen_brightness", light);
		sendBroadcast(intent);
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
