package com.yy.doorplate.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.EquModelTaskDatabase;
import com.yy.doorplate.database.ExaminationDatabase;
import com.yy.doorplate.model.EquModelTaskModel;
import com.yy.doorplate.model.ExaminationModel;

public class ExamActivity extends Activity implements OnClickListener {

	private final String TAG = "ExamActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private TextClock clock_exam_1, clock_exam_2;
	private TextView txt_exam_ksmc, txt_exam_kcmc, txt_exam_kskm,
			txt_exam_time, txt_exam_weather;
	private Button btn_exam_1, btn_exam_2, btn_exam_3;

	private EquModelTaskModel equModelTaskModel;
	private List<ExaminationModel> examinationModels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exam);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();
		application.handler_touch.removeMessages(0);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		Intent intent = new Intent(MyApplication.BROADCAST);
		intent.putExtra(MyApplication.BROADCAST_TAG, "permission_finish");
		broadcastManager.sendBroadcast(intent);

		equModelTaskModel = (EquModelTaskModel) getIntent()
				.getSerializableExtra("EquModelTaskModel");

		initView();
		threadTime.start();
	}

	private void initView() {
		clock_exam_1 = (TextClock) findViewById(R.id.clock_exam_1);
		clock_exam_1.setFormat12Hour(null);
		clock_exam_1.setFormat24Hour("yyyyÄêMMÔÂddÈÕ	EEEE");
		clock_exam_2 = (TextClock) findViewById(R.id.clock_exam_2);
		clock_exam_2.setFormat12Hour(null);
		clock_exam_2.setFormat24Hour("HH:mm:ss");
		txt_exam_ksmc = (TextView) findViewById(R.id.txt_exam_ksmc);
		txt_exam_kcmc = (TextView) findViewById(R.id.txt_exam_kcmc);
		txt_exam_kskm = (TextView) findViewById(R.id.txt_exam_kskm);
		txt_exam_time = (TextView) findViewById(R.id.txt_exam_time);
		txt_exam_weather = (TextView) findViewById(R.id.txt_exam_weather);
		btn_exam_1 = (Button) findViewById(R.id.btn_exam_1);
		btn_exam_2 = (Button) findViewById(R.id.btn_exam_2);
		btn_exam_3 = (Button) findViewById(R.id.btn_exam_3);
		btn_exam_1.setOnClickListener(this);
		btn_exam_2.setOnClickListener(this);
		btn_exam_3.setOnClickListener(this);

		examination();
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.QRY_EQUMODEL)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					if (!equModelTaskList()) {
						finish();
					}
				}
			} else if (tag.equals(HttpProcess.QRY_EXAM)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					examination();
				}
			} else if (tag.equals(HttpProcess.QRY_WEATHER)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					txt_exam_weather.setText(application.currentCity + " "
							+ application.weather + " "
							+ application.temperature);
				}
			}
		}
	}

	@Override
	public void onClick(View view) {

	}

	private void examination() {
		ExaminationDatabase database = new ExaminationDatabase();
		examinationModels = database.query_by_ksrq(MyApplication
				.getTime("yyyy-MM-dd"));
		if (examinationModels != null && examinationModels.size() > 0) {
			Message msg = Message.obtain();
			msg.what = 1;
			msg.obj = examinationModels.get(0);
			handler.sendMessage(msg);
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				String HHmmss = formatter.format(System.currentTimeMillis());
				Date time = formatter.parse(HHmmss);
				for (ExaminationModel model : examinationModels) {
					Date start = formatter.parse(model.kskssj);
					Date end = formatter.parse(model.ksjssj);
					if (time.getTime() <= end.getTime()
							&& (start.getTime() < time.getTime() || start
									.getTime() - time.getTime() < 60 * 60 * 1000)) {
						msg = Message.obtain();
						msg.what = 2;
						msg.obj = model;
						handler.sendMessage(msg);
						break;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1: {
				ExaminationModel model = (ExaminationModel) msg.obj;
				txt_exam_ksmc.setText(model.ksmc);
				txt_exam_kcmc.setText(model.kcmc);
				break;
			}
			case 2: {
				ExaminationModel model = (ExaminationModel) msg.obj;
				// txt_exam_ksmc.setText(model.ksmc);
				// txt_exam_kcmc.setText(model.kcmc);
				txt_exam_kskm.setText(model.kskm);
				txt_exam_time.setText("( " + model.kskssj.substring(0, 5)
						+ " ~ " + model.ksjssj.substring(0, 5) + " )");
				break;
			}
			}
		};
	};

	private boolean isThread = true;
	private Thread threadTime = new Thread(new Runnable() {

		@Override
		public void run() {
			while (isThread) {
				String hhmmss = MyApplication.getTime("HHmmss");
				if (hhmmss.endsWith("00")) {
					if (!equModelTask()) {
						finish();
						break;
					}
					examination();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	});

	private boolean equModelTask() {
		if (equModelTaskModel == null) {
			return false;
		}
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date startTime = formatter.parse(equModelTaskModel.startTime);
			Date stopTime = formatter.parse(equModelTaskModel.stopTime);
			long time = System.currentTimeMillis();
			if (time >= startTime.getTime() && time < stopTime.getTime()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean equModelTaskList() {
		EquModelTaskDatabase database = new EquModelTaskDatabase();
		List<EquModelTaskModel> taskModels = database.query_all();
		if (taskModels == null) {
			return false;
		}
		for (EquModelTaskModel model : taskModels) {
			if (!model.equModel.equals("01")) {
				continue;
			}
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date startTime = formatter.parse(model.startTime);
				Date stopTime = formatter.parse(model.stopTime);
				long time = System.currentTimeMillis();
				if (time >= startTime.getTime() && time < stopTime.getTime()) {
					equModelTaskModel = model;
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		isThread = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
