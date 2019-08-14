package com.advertisement.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.advertisement.system.ConstData;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.ui.AppCodeView;

public class ActivityAppcodeo extends Activity {

	private final String TAG = "ActivityAppcodeo";

	private MyApplication application;

	private RelativeLayout ly_appcode;

	private DisplayManager displayManager;

	private ImageLoader imageLoader = null;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appcode);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		filter.addAction(ConstData.ACTIVITY_UPDATE);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		ly_appcode = (RelativeLayout) findViewById(R.id.ly_appcode);
		ly_appcode.removeAllViews();

		String path = getIntent().getStringExtra("ThemePolicyPath");
		displayManager = new DisplayManager(this, application, ly_appcode);
		displayManager.setImageLoader(imageLoader);
		displayManager
				.setActivity("com.advertisement.activity.ActivityAppcodeo");
		displayManager.setActivity(this);
		displayManager.start(path);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.OPERATE_AUTHORIZATION)) {
				if (TextUtils.isEmpty(application.operateType)) {
					return;
				}
				if (displayManager != null) {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_DIALOG_DIS);
				}
			} else if (tag.equals("commotAttend_ui")
					|| tag.equals("absenteeism_ui")) {
				if (displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_ATTEND);
				}
			} else if (tag.equals(HttpProcess.QRY_NOTICE)
					&& !application.isUpdateEQ
					&& !application.isUpdataNoticeList) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result && displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_NOTIC);
				}
			} else if (tag.startsWith(HttpProcess.QRY_CURRICULUM)
					&& !application.isGrxx) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result && displayManager != null) {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_CURRICULUM);
				}
			} else if (tag.equals("updataEQ")) {
				if (displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_EQU);
				}
			} else if (tag.equals("playTask")) {
				if (displayManager != null) {
					displayManager
							.updata(AppCodeView.APPCODE_UPDATA_REGION_MEDIA);
				}
			} else if (tag.equals(HttpProcess.QRY_WEATHER)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result && displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_WEATHER);
				}
			} else if (tag.equals(HttpProcess.QRY_PRIZE)
					&& !application.isUpdateEQ && !application.isGrxx) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					handler.sendEmptyMessageDelayed(1, 10 * 1000);
				}
			} else if (tag.equals(HttpProcess.QRY_SCHOOL)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result && displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_SCHOOL);
				}
			} else if (tag.equals(HttpProcess.QRY_QUEST)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result && displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_QUEST);
				}
			} else if (tag.equals(HttpProcess.QRY_BLACKBOARD)
					&& !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result && displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_ENGLISH);
				}
			} else if (tag.equals(HttpProcess.QRY_ONDUTY)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result && displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_ZHIRI);
				}
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (displayManager != null) {
					displayManager.updata(AppCodeView.APPCODE_UPDATA_HONOR);
				}
				break;
			}
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
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
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.clearCache();
		imageLoader.cancelTask();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		if (displayManager != null) {
			displayManager.stop();
			displayManager.destroy();
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
}
