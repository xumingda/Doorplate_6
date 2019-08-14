package com.yy.doorplate.activity;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.EquListDatabase;
import com.yy.doorplate.database.MenuAndEqDatabase;
import com.yy.doorplate.database.TelMenuDatabase;
import com.yy.doorplate.model.EquInfoModel;
import com.yy.doorplate.model.MenuAndEqModel;
import com.yy.doorplate.model.TelMenuInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class VideoCallActivity extends Activity implements OnClickListener {

	private final String TAG = "VideoCallActivity";

	private MyApplication application;

	private LinearLayout ly_videocall;
	private Button btn_videocall_back;

	private TelMenuDatabase telMenuDatabase = new TelMenuDatabase();
	private MenuAndEqDatabase menuAndEqDatabase = new MenuAndEqDatabase();
	private EquListDatabase equListDatabase = new EquListDatabase();
	private List<TelMenuInfoModel> telMenuInfoModels;
	private List<MenuAndEqModel> menuAndEqModels;

	private ImageLoader imageLoader = null;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_call);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	private void initView() {
		ly_videocall = (LinearLayout) findViewById(R.id.ly_videocall);
		btn_videocall_back = (Button) findViewById(R.id.btn_videocall_back);
		btn_videocall_back.setOnClickListener(this);

		telMenuInfoModels = telMenuDatabase.query_all();
		if (telMenuInfoModels != null && telMenuInfoModels.size() > 0) {
			for (TelMenuInfoModel model : telMenuInfoModels) {
				addTelMenu(model);
			}
		}
	}

	private void addTelMenu(final TelMenuInfoModel model) {

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(375,
				657);
		final Button button = new Button(this);
		OnImageLoaderListener listener = new OnImageLoaderListener() {

			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				if (bitmap != null) {
					button.setBackground(new BitmapDrawable(bitmap));
				} else {
					button.setBackgroundResource(R.drawable.btn_videocall_4);
				}
			}
		};
		if (!TextUtils.isEmpty(model.menuIcon)) {
			String[] s = model.menuIcon.split("\\.");
			String path = MyApplication.PATH_ROOT + MyApplication.PATH_PICTURE
					+ "/talkIcon_" + model.menuId + "." + s[s.length - 1];
			if (imageLoader.getBitmapFromMemCache(path) != null) {
				button.setBackground(new BitmapDrawable(imageLoader
						.getBitmapFromMemCache(path)));
			} else {
				imageLoader.getBitmapFormUrl(path, listener, 375, 657);
			}
		} else {
			imageLoader.getBitmapFormRes(R.drawable.btn_videocall_4, listener);
		}
		button.setPadding(0, 0, 0, 100);
		button.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		button.setTextSize(60);
		button.setTextColor(getResources().getColor(R.color.white));
		button.setText(model.menuName);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				menuAndEqModels = menuAndEqDatabase.query_by_tel(model.menuId);
				if (menuAndEqModels != null && menuAndEqModels.size() > 0) {
					if (menuAndEqModels.size() == 1) {
						EquInfoModel equInfoModel = equListDatabase
								.query_by_id(menuAndEqModels.get(0).equCode);
						if (equInfoModel != null
								&& !TextUtils.isEmpty(equInfoModel.ip)) {
							application.call_eq = equInfoModel;
							Intent intent = new Intent(VideoCallActivity.this,
									CallActivity.class);
							intent.putExtra("isCall", true);
							startActivity(intent);
						} else {
							application.showToast("设备IP地址异常，无法呼叫");
						}
					} else {
						Intent intent = new Intent(VideoCallActivity.this,
								VideoCallOtherActivity.class);
						intent.putExtra("menuId", model.menuId);
						startActivity(intent);
					}
				} else {
					application.showToast("暂无" + model.menuName + "信息");
				}
			}
		});

		if (ly_videocall.getChildCount() == 0) {
			ly_videocall.addView(button, params);
		} else {
			params.leftMargin = 50;
			ly_videocall.addView(button, params);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_videocall_back:
			finish();
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
}
