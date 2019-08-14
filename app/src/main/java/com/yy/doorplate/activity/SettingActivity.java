package com.yy.doorplate.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class SettingActivity extends Activity implements OnClickListener {

	private final String TAG = "SettingActivity";

	private MyApplication application;

	private Button btn_setting_back, btn_setting_net, btn_setting_class,
			btn_setting_update, btn_setting_more, btn_setting_reboot;

	private ImageLoader imageLoader;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
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

	private void initView() {
		btn_setting_back = (Button) findViewById(R.id.btn_setting_back);
		btn_setting_net = (Button) findViewById(R.id.btn_setting_net);
		btn_setting_class = (Button) findViewById(R.id.btn_setting_class);
		btn_setting_update = (Button) findViewById(R.id.btn_setting_update);
		btn_setting_more = (Button) findViewById(R.id.btn_setting_more);
		btn_setting_reboot = (Button) findViewById(R.id.btn_setting_reboot);
		btn_setting_back.setOnClickListener(this);
		btn_setting_net.setOnClickListener(this);
		btn_setting_class.setOnClickListener(this);
		btn_setting_update.setOnClickListener(this);
		btn_setting_more.setOnClickListener(this);
		btn_setting_reboot.setOnClickListener(this);

		imageLoader.getBitmapFormRes(R.drawable.btn_setting_net,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						btn_setting_net
								.setBackground(new BitmapDrawable(bitmap));
					}
				});
		imageLoader.getBitmapFormRes(R.drawable.btn_setting_class,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						btn_setting_class.setBackground(new BitmapDrawable(
								bitmap));
					}
				});
		imageLoader.getBitmapFormRes(R.drawable.btn_setting_update,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						btn_setting_update.setBackground(new BitmapDrawable(
								bitmap));
					}
				});
		imageLoader.getBitmapFormRes(R.drawable.btn_setting_more,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						btn_setting_more.setBackground(new BitmapDrawable(
								bitmap));
					}
				});
		imageLoader.getBitmapFormRes(R.drawable.btn_setting_reboot,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						btn_setting_reboot.setBackground(new BitmapDrawable(
								bitmap));
					}
				});

		btn_setting_more.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				DialogProPwd();
				return false;
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_setting_back:
			finish();
			break;
		case R.id.btn_setting_net: {
			Intent intent = new Intent(this, NetSetActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_setting_class: {
			if (application.isSynComplete) {
				Intent intent = new Intent(this, SettingClassActivity.class);
				startActivity(intent);
			}
			break;
		}
		case R.id.btn_setting_update: {
			if (!application.isDownloadAPP && application.isSynComplete) {
				application.httpProcess.GetVersion(null);
			}
			break;
		}
		case R.id.btn_setting_more: {
			Intent intent = new Intent(this, SettingMoreActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.btn_setting_reboot: {
			Intent intent = new Intent();
			intent.setAction("system_to_reboot");
			sendBroadcast(intent);
			break;
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
			if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

	private EditText edt_pro_pwd;
	private Button btn_pro_pwd_ok, btn_pro_pwd_cancel;

	private void DialogProPwd() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		application.dialog = builder.create();
		LayoutInflater inflater = LayoutInflater.from(application);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.dialog_pro_pwd, null);
		application.dialog.setView(layout);
		application.dialog.show();
		application.dialog.getWindow().setLayout(
				getResources().getDimensionPixelSize(R.dimen.dimen_320_dip),
				getResources().getDimensionPixelSize(R.dimen.dimen_220_dip));
		application.dialog.getWindow().setContentView(R.layout.dialog_pro_pwd);
		application.dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		edt_pro_pwd = (EditText) application.dialog
				.findViewById(R.id.edt_pro_pwd);
		edt_pro_pwd.addTextChangedListener(application.textWatcher);
		btn_pro_pwd_ok = (Button) application.dialog
				.findViewById(R.id.btn_pro_pwd_ok);
		btn_pro_pwd_cancel = (Button) application.dialog
				.findViewById(R.id.btn_pro_pwd_cancel);
		btn_pro_pwd_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				application.dialog.dismiss();
			}
		});
		btn_pro_pwd_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String str = edt_pro_pwd.getText().toString();
				if (!TextUtils.isEmpty(str) && str.equals("88003580")) {
					application.openApp(SettingActivity.this,
							"com.android.settings");
					application.dialog.dismiss();
				} else if (!TextUtils.isEmpty(str) && str.equals("83592600")) {
					if (application.mEYunSdk != null) {
						application.mEYunSdk.Finish();
					}
					if (application.dogProcess != null) {
						application.dogProcess.disenbleLight();
						application.dogProcess.stopDog();
						application.dogProcess.close();
						if (application.thread_dog != null) {
							application.is_thread_dog = false;
							application.thread_dog.interrupt();
							application.thread_dog = null;
						}
					}
					application.openApp(SettingActivity.this,
							"com.yy.doorplatetest");
					application.dialog.dismiss();
				} else {
					edt_pro_pwd.setText("");
					application.showToast("√‹¬Î¥ÌŒÛ");
				}
			}
		});
	}

}
