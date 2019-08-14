package com.yy.doorplate.activity;

import java.io.File;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.TeacherInfoDatabase;
import com.yy.doorplate.model.TeacherInfoModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.view.CustomTextView;

public class TeacherActivity extends Activity implements OnClickListener {

	private final String TAG = "TeacherActivity";

	private MyApplication application;

	private Button btn_teacher_back, btn_teacher_kyqk, btn_teacher_jxqk;
	private TextView txt_teacher_sex, txt_teacher_byxx, txt_teacher_byzy,
			txt_teacher_xlxw, txt_teacher_jxqk;
	private CustomTextView txt_teacher_name;
	private ImageView img_teacher;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private String skjs;

	private TeacherInfoDatabase database = new TeacherInfoDatabase();
	private TeacherInfoModel model;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
		initData();
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
		btn_teacher_back = (Button) findViewById(R.id.btn_teacher_back);
		btn_teacher_kyqk = (Button) findViewById(R.id.btn_teacher_kyqk);
		btn_teacher_jxqk = (Button) findViewById(R.id.btn_teacher_jxqk);
		txt_teacher_name = (CustomTextView) findViewById(R.id.txt_teacher_name);
		txt_teacher_sex = (TextView) findViewById(R.id.txt_teacher_sex);
		txt_teacher_byxx = (TextView) findViewById(R.id.txt_teacher_byxx);
		txt_teacher_byzy = (TextView) findViewById(R.id.txt_teacher_byzy);
		txt_teacher_xlxw = (TextView) findViewById(R.id.txt_teacher_xlxw);
		txt_teacher_jxqk = (TextView) findViewById(R.id.txt_teacher_jxqk);
		img_teacher = (ImageView) findViewById(R.id.img_teacher);
		btn_teacher_back.setOnClickListener(this);
		btn_teacher_kyqk.setOnClickListener(this);
		btn_teacher_jxqk.setOnClickListener(this);
	}

	private void initData() {
		skjs = getIntent().getStringExtra("skjs");
		model = database.query_by_id(skjs);
		// if (model != null) {
		// showInfo();
		// } else {
		application.httpProcess.QryTeacher(application.equInfoModel.jssysdm,
				skjs, null);
		// }
	}

	private void showInfo() {
		if (model == null) {
			return;
		}
		if (!TextUtils.isEmpty(model.xm)) {
			txt_teacher_name.setText(model.xm);
		}
		if (!TextUtils.isEmpty(model.xb)) {
			if (model.xb.equals("1")) {
				txt_teacher_sex.setText("男");
			} else if (model.xb.equals("2")) {
				txt_teacher_sex.setText("女");
			}
		}
		if (!TextUtils.isEmpty(model.byxx) && !model.byxx.equals("null")) {
			txt_teacher_byxx.setText(model.byxx);
		}
		if (!TextUtils.isEmpty(model.byzy) && !model.byzy.equals("null")) {
			txt_teacher_byzy.setText(model.byzy);
		}
		if ((!TextUtils.isEmpty(model.zgxlmc) && !model.zgxlmc.equals("null"))
				|| (!TextUtils.isEmpty(model.zgxwmc) && !model.zgxwmc
						.equals("null"))) {
			txt_teacher_xlxw.setText(model.zgxlmc + model.zgxwmc);
		}
		if (!TextUtils.isEmpty(model.kyqk) && !model.kyqk.equals("null")) {
			txt_teacher_jxqk.setText(model.kyqk);
		}
		if (!TextUtils.isEmpty(model.zp)) {
			String[] s = model.zp.split("\\.");
			File file = new File(MyApplication.PATH_ROOT
					+ MyApplication.PATH_PICTURE + "/teacher_" + model.rybh
					+ "." + s[s.length - 1]);
			if (file.exists()) {
				ImageLoader imageLoader = new ImageLoader();
				img_teacher
						.setImageBitmap(imageLoader
								.decodeSampledBitmapFromPath(
										MyApplication.PATH_ROOT
												+ MyApplication.PATH_PICTURE
												+ "/teacher_" + model.rybh
												+ "." + s[s.length - 1], 188,
										188));
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_teacher_back:
			finish();
			break;
		case R.id.btn_teacher_kyqk:
			btn_teacher_kyqk.setTextColor(getResources()
					.getColor(R.color.white));
			btn_teacher_kyqk.setBackgroundResource(R.color.YELLOW);
			btn_teacher_jxqk.setTextColor(getResources()
					.getColor(R.color.black));
			btn_teacher_jxqk.setBackgroundResource(R.color.bottom_color);
			if (model != null && !TextUtils.isEmpty(model.kyqk)
					&& !model.kyqk.equals("null")) {
				txt_teacher_jxqk.setText(model.kyqk);
			} else {
				txt_teacher_jxqk.setText("");
			}
			break;
		case R.id.btn_teacher_jxqk:
			btn_teacher_jxqk.setTextColor(getResources()
					.getColor(R.color.white));
			btn_teacher_jxqk.setBackgroundResource(R.color.YELLOW);
			btn_teacher_kyqk.setTextColor(getResources()
					.getColor(R.color.black));
			btn_teacher_kyqk.setBackgroundResource(R.color.bottom_color);
			if (model != null && !TextUtils.isEmpty(model.jxqk)
					&& !model.jxqk.equals("null")) {
				txt_teacher_jxqk.setText(model.jxqk);
			} else {
				txt_teacher_jxqk.setText("");
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
			if (tag.equals(HttpProcess.QRY_TEACHER)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					model = database.query_by_id(skjs);
					showInfo();
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("教师信息查询失败，原因：" + msg);
				}
			} else if (tag.equals("permission_finish")) {
				finish();
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}
}
