package com.yy.doorplate.activity;

import java.util.List;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.ClassEquAdapter;
import com.yy.doorplate.adapter.StudentAdapter;
import com.yy.doorplate.database.ClassEquInfoDatabase;
import com.yy.doorplate.database.ClassInfoDatabase;
import com.yy.doorplate.database.StudentInfoDatabase;
import com.yy.doorplate.model.ClassEquInfoModel;
import com.yy.doorplate.model.ClassInfoModel;
import com.yy.doorplate.model.StudentInfoModel;
import com.yy.doorplate.view.CustomTextView;

public class ClassInfoActivity extends Activity implements OnClickListener {

	private final String TAG = "ClassInfoActivity";

	private MyApplication application;

	private LinearLayout ly_classinfo, ly_banjiinfo;
	private Button btn_classinfo_back, btn_banjiinfo, btn_classinfo;
	private TextView txt_classinfo_title, txt_classinfo_glylx,
			txt_classinfo_phone, txt_classinfo_jsnl, txt_classinfo_xszs,
			txt_class_remark;
	private CustomTextView txt_classinfo_gly;
	private ListView list_classinfo;

	private ClassEquAdapter classEquAdapter = null;
	private StudentAdapter studentAdapter = null;

	private List<ClassEquInfoModel> classEquInfoModels = null;
	private List<StudentInfoModel> studentInfoModels = null;
	private ClassInfoModel classInfoModel = null;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classinfo);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	private void initView() {
		ly_classinfo = (LinearLayout) findViewById(R.id.ly_classinfo);
		ly_banjiinfo = (LinearLayout) findViewById(R.id.ly_banjiinfo);
		btn_classinfo_back = (Button) findViewById(R.id.btn_classinfo_back);
		btn_banjiinfo = (Button) findViewById(R.id.btn_banjiinfo);
		btn_classinfo = (Button) findViewById(R.id.btn_classinfo);
		txt_classinfo_title = (TextView) findViewById(R.id.txt_classinfo_title);
		txt_classinfo_gly = (CustomTextView) findViewById(R.id.txt_classinfo_gly);
		txt_classinfo_glylx = (TextView) findViewById(R.id.txt_classinfo_glylx);
		txt_classinfo_phone = (TextView) findViewById(R.id.txt_classinfo_phone);
		txt_classinfo_jsnl = (TextView) findViewById(R.id.txt_classinfo_jsnl);
		txt_classinfo_xszs = (TextView) findViewById(R.id.txt_classinfo_xszs);
		txt_class_remark = (TextView) findViewById(R.id.txt_class_remark);
		list_classinfo = (ListView) findViewById(R.id.list_classinfo);

		btn_classinfo_back.setOnClickListener(this);
		btn_banjiinfo.setOnClickListener(this);
		btn_classinfo.setOnClickListener(this);

		if (application.classRoomInfoModel != null) {
			if (!TextUtils.isEmpty(application.classRoomInfoModel.jssysmc)) {
				txt_classinfo_title
						.setText(application.classRoomInfoModel.jssysmc);
			}
			if (!TextUtils.isEmpty(application.classRoomInfoModel.xsrl)
					&& !application.classRoomInfoModel.xsrl.equals("null")) {
				txt_classinfo_jsnl.setText(application.classRoomInfoModel.xsrl
						+ "人");
			} else {
				txt_classinfo_jsnl.setText("- -");
			}
			if (!TextUtils.isEmpty(application.classRoomInfoModel.remark)
					&& !application.classRoomInfoModel.remark.equals("null")) {
				txt_class_remark.setText(application.classRoomInfoModel.remark);
			}
			if (!TextUtils.isEmpty(application.classRoomInfoModel.cdglyxm)
					&& !application.classRoomInfoModel.cdglyxm.equals("null")) {
				txt_classinfo_gly
						.setText(application.classRoomInfoModel.cdglyxm);
				txt_classinfo_glylx.setVisibility(View.VISIBLE);
			} else {
				txt_classinfo_gly.setText("无");
				txt_classinfo_glylx.setVisibility(View.INVISIBLE);
			}
			if (!TextUtils.isEmpty(application.classRoomInfoModel.cdglydh)
					&& !application.classRoomInfoModel.cdglydh.equals("null")) {
				txt_classinfo_phone
						.setText(application.classRoomInfoModel.cdglydh);
			}
		}

		ClassEquInfoDatabase classEquInfoDatabase = new ClassEquInfoDatabase();
		classEquInfoModels = classEquInfoDatabase.query_all();
		if (classEquInfoModels != null && classEquInfoModels.size() > 0) {
			classEquAdapter = new ClassEquAdapter(application,
					classEquInfoModels);
			list_classinfo.setAdapter(classEquAdapter);
		}

		ClassInfoDatabase classInfoDatabase = new ClassInfoDatabase();
		classInfoModel = classInfoDatabase.query();
		StudentInfoDatabase studentInfoDatabase = new StudentInfoDatabase();
		studentInfoModels = studentInfoDatabase
				.query_by_ssbj(application.equInfoModel.bjdm);

		if (classInfoModel == null) {
			btn_banjiinfo.setVisibility(View.INVISIBLE);
			btn_classinfo.setVisibility(View.INVISIBLE);
		} else if (studentInfoModels != null && studentInfoModels.size() > 0) {
			studentAdapter = new StudentAdapter(application, list_classinfo,
					studentInfoModels);
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
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_classinfo_back:
			finish();
			break;
		case R.id.btn_classinfo: {
			btn_classinfo.setTextColor(getResources().getColor(R.color.white));
			btn_classinfo.setBackgroundResource(R.drawable.btn_classinfo_click);
			btn_banjiinfo.setTextColor(getResources().getColor(R.color.black));
			btn_banjiinfo.setBackgroundResource(R.drawable.btn_banjiinfo);
			txt_classinfo_glylx.setText(getResources().getString(R.string.gly));
			txt_classinfo_xszs.setText(getResources().getString(R.string.jsnl));

			if (!TextUtils.isEmpty(application.classRoomInfoModel.cdglyxm)
					&& !application.classRoomInfoModel.cdglyxm.equals("null")) {
				txt_classinfo_gly
						.setText(application.classRoomInfoModel.cdglyxm);
				txt_classinfo_glylx.setVisibility(View.VISIBLE);
			} else {
				txt_classinfo_gly.setText("无");
				txt_classinfo_glylx.setVisibility(View.INVISIBLE);
			}
			if (!TextUtils.isEmpty(application.classRoomInfoModel.cdglydh)
					&& !application.classRoomInfoModel.cdglydh.equals("null")) {
				txt_classinfo_phone
						.setText(application.classRoomInfoModel.cdglydh);
			} else {
				txt_classinfo_phone.setText("");
			}
			if (!TextUtils.isEmpty(application.classRoomInfoModel.xsrl)
					&& !application.classRoomInfoModel.xsrl.equals("null")) {
				txt_classinfo_jsnl.setText(application.classRoomInfoModel.xsrl
						+ "人");
			} else {
				txt_classinfo_jsnl.setText("- -");
			}

			ly_classinfo.setVisibility(View.VISIBLE);
			ly_banjiinfo.setVisibility(View.INVISIBLE);

			if (classEquInfoModels != null && classEquInfoModels.size() > 0) {
				classEquAdapter = new ClassEquAdapter(application,
						classEquInfoModels);
				list_classinfo.setAdapter(classEquAdapter);
			} else {
				list_classinfo.setAdapter(null);
			}
			break;
		}
		case R.id.btn_banjiinfo: {
			btn_classinfo.setTextColor(getResources().getColor(R.color.black));
			btn_classinfo.setBackgroundResource(R.drawable.btn_classinfo);
			btn_banjiinfo.setTextColor(getResources().getColor(R.color.white));
			btn_banjiinfo.setBackgroundResource(R.drawable.btn_banjiinfo_click);
			txt_classinfo_glylx.setText(getResources().getString(R.string.bzr));
			txt_classinfo_xszs.setText(getResources().getString(R.string.xszs));
			if (!TextUtils.isEmpty(classInfoModel.fdyxm)
					&& !classInfoModel.fdyxm.equals("null")) {
				txt_classinfo_gly.setText(classInfoModel.fdyxm);
				txt_classinfo_glylx.setVisibility(View.VISIBLE);
			} else {
				txt_classinfo_gly.setText("无");
				txt_classinfo_glylx.setVisibility(View.INVISIBLE);
			}
			if (!TextUtils.isEmpty(classInfoModel.fdydh)
					&& !classInfoModel.fdydh.equals("null")) {
				txt_classinfo_phone.setText(classInfoModel.fdydh);
			} else {
				txt_classinfo_phone.setText("");
			}
			ly_classinfo.setVisibility(View.INVISIBLE);
			ly_banjiinfo.setVisibility(View.VISIBLE);

			if (studentInfoModels != null && studentInfoModels.size() > 0) {
				txt_classinfo_jsnl.setText(studentInfoModels.size() + "人");
				list_classinfo.setAdapter(studentAdapter);
			} else {
				txt_classinfo_jsnl.setText("- -");
				list_classinfo.setAdapter(null);
			}
			break;
		}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (studentAdapter != null) {
			studentAdapter.clearCache();
		}
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
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
