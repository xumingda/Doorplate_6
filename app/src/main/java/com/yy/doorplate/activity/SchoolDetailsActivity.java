package com.yy.doorplate.activity;

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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.SchoolInfoDatabase;
import com.yy.doorplate.model.SchoolInfoModel;

public class SchoolDetailsActivity extends Activity {

	private final String TAG = "SchoolDetailsActivity";

	private MyApplication application;

	private Button btn_noticedetails_back;

	private SchoolInfoModel schoolInfoModel = null;

	private TextView txt_noticedetails_top, txt_notice_details_title;
	private WebView web_notice;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_details);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		SchoolInfoDatabase database = new SchoolInfoDatabase();
		schoolInfoModel = database.query_by_infoCode(getIntent()
				.getStringExtra("infoCode"));

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
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		web_notice.removeAllViews();
		web_notice.destroy();
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
		btn_noticedetails_back = (Button) findViewById(R.id.btn_noticedetails_back);
		txt_noticedetails_top = (TextView) findViewById(R.id.txt_noticedetails_top);
		txt_notice_details_title = (TextView) findViewById(R.id.txt_notice_details_title);
		web_notice = (WebView) findViewById(R.id.web_notice);

		btn_noticedetails_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		txt_noticedetails_top.setText("学校信息");
		if (schoolInfoModel != null) {
			txt_notice_details_title.setText(schoolInfoModel.infoName);
			if (!TextUtils.isEmpty(schoolInfoModel.content)) {
				web_notice.loadDataWithBaseURL(null, schoolInfoModel.content,
						"text/html", "utf-8", null);
				WebSettings webSettings = web_notice.getSettings();
				webSettings.setDefaultFontSize(40);
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
}
