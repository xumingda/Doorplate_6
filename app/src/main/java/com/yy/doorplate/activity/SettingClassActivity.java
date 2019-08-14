package com.yy.doorplate.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;

public class SettingClassActivity extends Activity implements OnClickListener {

	private final String TAG = "SettingClassActivity";

	private MyApplication application;

	private Button btn_setting_back, btn_setting_save, btn_setting_notice,
			btn_setting_curriculum;
	private EditText edt_setting_jsdm, edt_setting_bjdm, edt_setting_ip;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_class);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
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
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();
			if (isShouldHideSoftKeyBoard(view, ev)) {
				hideSoftKeyBoard(view.getWindowToken());
			}
		}
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
		btn_setting_back = (Button) findViewById(R.id.btn_setting_back);
		btn_setting_save = (Button) findViewById(R.id.btn_setting_save);
		btn_setting_notice = (Button) findViewById(R.id.btn_setting_notice);
		btn_setting_curriculum = (Button) findViewById(R.id.btn_setting_curriculum);
		edt_setting_jsdm = (EditText) findViewById(R.id.edt_setting_jsdm);
		edt_setting_bjdm = (EditText) findViewById(R.id.edt_setting_bjdm);
		edt_setting_ip = (EditText) findViewById(R.id.edt_setting_ip);
		edt_setting_jsdm.addTextChangedListener(application.textWatcher);
		edt_setting_bjdm.addTextChangedListener(application.textWatcher);
		edt_setting_ip.addTextChangedListener(application.textWatcher);
		btn_setting_back.setOnClickListener(this);
		btn_setting_save.setOnClickListener(this);
		btn_setting_notice.setOnClickListener(this);
		btn_setting_curriculum.setOnClickListener(this);

		if (application.equInfoModel != null) {
			edt_setting_jsdm.setText(application.equInfoModel.jssysdm);
			edt_setting_bjdm.setText(application.equInfoModel.bjdm);
		}
		edt_setting_ip.setText(application.server_ip);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_setting_back:
			finish();
			break;
		case R.id.btn_setting_save: {
			save();
			break;
		}
		case R.id.btn_setting_notice: {
			application.showToast("获取通知中...");
			if (!application.isUpdateNotice) {
				application.isUpdateNotice = true;
				application.httpProcess.QryNoticePJ(
						application.equInfoModel.jssysdm, "0",
						MyApplication.NOTICE_PAGE_COUNT,
						application.equInfoModel.bjdm, false, null);
			}
			break;
		}
		case R.id.btn_setting_curriculum: {
			application.showToast("获取课程中...");
			if (!application.isUpdateCurriculum) {
				application.isUpdateCurriculum = true;
				application.httpProcess.QryCurriculum_day(
						application.equInfoModel.jssysdm,
						MyApplication.getTime("yyyy-MM-dd") + " 00:00:00",
						MyApplication.getTime("yyyy-MM-dd") + " 23:59:59", "",
						null);
			}
			break;
		}
		}
	}

	private void save() {
		String jsdm = edt_setting_jsdm.getText().toString();
		String bjdm = edt_setting_bjdm.getText().toString();
		String ip = edt_setting_ip.getText().toString();
		if (!TextUtils.isEmpty(jsdm) && !TextUtils.isEmpty(ip)) {
			// if (jsdm.equals(application.equInfoModel.jssysdm) && isSame(bjdm)
			// && ip.equals(application.server_ip)) {
			// application.showToast("已保存");
			// } else if (jsdm.equals(application.equInfoModel.jssysdm)
			// && isSame(bjdm) && !ip.equals(application.server_ip)) {
			// application.setServer_ip(ip);
			// application.httpProcess.QryEqu(null);
			// } else if (!jsdm.equals(application.equInfoModel.jssysdm)
			// || !isSame(bjdm)) {
			application.isUpdateEQ = true;
			application.showDialog("正在更新设备信息...", 0);
			application.setServer_ip(ip);
			application.httpProcess.Register(application.equInfoModel.equName,
					jsdm, bjdm, application.equInfoModel.orgId);
			// }
		} else {
			application.showToast("请输入完整");
		}
	}

	private boolean isSame(String bjdm) {
		if (!TextUtils.isEmpty(bjdm)
				&& TextUtils.isEmpty(application.equInfoModel.bjdm)) {
			return false;
		} else if (TextUtils.isEmpty(bjdm)
				&& !TextUtils.isEmpty(application.equInfoModel.bjdm)) {
			return false;
		} else if (TextUtils.isEmpty(bjdm)
				&& TextUtils.isEmpty(application.equInfoModel.bjdm)) {
			return true;
		} else {
			if (bjdm.equals(application.equInfoModel.bjdm)) {
				return true;
			} else {
				return false;
			}
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.QRY_EQU) && !application.isUpdateEQ) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					application.tcpProcess.reStart(
							application.equInfoModel.accSysIp,
							application.equInfoModel.accSysPort);
					application.showToast("服务器地址保存成功");
				} else {
					Log.e(TAG, tag + " " + msg);
					application.setServer_ip(application.getServer_ip());
					application.showToast("服务器地址保存失败");
				}
			} else if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}
	

	private boolean isShouldHideSoftKeyBoard(View view, MotionEvent event) {
		if (view != null && (view instanceof EditText)) {
			int[] l = { 0, 0 };
			view.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + view.getHeight(), right = left + view.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	private void hideSoftKeyBoard(IBinder token) {
		if (token != null) {
			InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}
