package com.cx.doorplate.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.FaceDatabase;
import com.yy.doorplate.model.FaceModel;
import com.yy.doorplate.model.PersonInfoModel;
import com.yy.doorplate.tool.General;

public class CXFaceRegister extends Activity {

	private final String TAG = "CXFaceRegister";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private TextView txt_weather, txt_face_nop;
	private TextClock textClock;
	private Button btn_cx_back;

	private ProgressDialog progressDialog = null;

	private PersonInfoModel personInfoModel;
	private FaceModel faceModel;
	private FaceDatabase faceDatabase = new FaceDatabase();

	private boolean isFaceON = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_faceregister);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		application.cardType = "QRY_PERSON_FACE";

		initView();

		// 保存识别记录图片关闭、开启活体检测、开启实时识别
		application.faceProcess.face_rec_set(0, 1, 0, 1, 1, 0, 0, 0, 0, 1);
		// application.faceProcess.getVersion();
		// application.faceProcess.reboot();
		// application.faceProcess.setTime(MyApplication.getTime("yyyy/MM/dd HH:mm:ss"));
	}

	private void initView() {
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		txt_face_nop = (TextView) findViewById(R.id.txt_face_nop);
		btn_cx_back = (Button) findViewById(R.id.btn_cx_back);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");
		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());

		btn_cx_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
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
			} else if (tag.equals("QRY_PERSON_FACE")) {
				String mCardNum = intent.getStringExtra("mCardNum");
				showProgressDialog();
				application.httpProcess.QryPerson(
						application.equInfoModel.jssysdm, mCardNum);
			} else if (tag.equals(HttpProcess.QRY_PERSON)
					&& "QRY_PERSON_FACE".equals(application.cardType)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				closeProgressDialog();
				if (result) {
					personInfoModel = new PersonInfoModel();
					personInfoModel.xm = intent.getStringExtra("xm");
					personInfoModel.js = intent.getStringExtra("js");
					personInfoModel.rybh = intent.getStringExtra("rybh");
					personInfoModel.xsbj = intent.getStringExtra("xsbj");
					personInfoModel.jsbm = intent.getStringExtra("jsbm");
					personInfoModel.cardid = intent.getStringExtra("cardid");

					// 保存识别记录图片关闭、关闭活体检测、关闭实时识别
					application.faceProcess.face_rec_set(0, 1, 0, 1, 1, 0, 0,
							0, 0, 0);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("查询人员失败，原因 : " + msg);
				}
			} else if (tag.equals("FaceError")) {
				// txt_face_nop.setText("人脸模块启动失败");
			} else if (tag.equals("FaceBack")) {
				String cmd = intent.getStringExtra("FaceCMD");
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (cmd.equals("CMD_FACEREC_REALTIME")) {
					Log.d(TAG, "CMD_FACEREC_REALTIME" + msg);
					faceDatabase = new FaceDatabase();
					FaceModel model = faceDatabase.query_by_faceId(msg);
					if (model != null) {
						application.commotAttend(model.cardid);
					}
				} else if (cmd.equals("CMD_FACEREC_SET")) {
					isFaceON = !isFaceON;
					if (isFaceON) {
						txt_face_nop.setText("人脸模块启动成功，请对准摄像头考勤");
					} else {
						application.faceProcess.person_register();
					}
				} else if (cmd.equals("CMD_PERSON_REGISTER")) {
					txt_face_nop.setText("注册人脸中，请将人脸对准摄像头");
					faceModel = new FaceModel();
					faceModel.faceId = msg;
				} else if (cmd.equals("CMD_FACE_REGISTER_ING")) {
					// application.showToast("人脸采集中，请将人脸对准摄像头");
				} else if (cmd.equals("CMD_FACE_REGISTER_FINISH")) {
					if (personInfoModel != null) {
						faceModel.xm = personInfoModel.xm;
						faceModel.js = personInfoModel.js;
						faceModel.rybh = personInfoModel.rybh;
						faceModel.cardid = personInfoModel.cardid;
						if (faceDatabase.query_by_rybh(faceModel.rybh) == null) {
							faceDatabase.insert(faceModel);
						} else {
							application.faceProcess
									.face_clear(General.hexStr2Str(faceDatabase
											.query_by_rybh(faceModel.rybh).faceId));
							faceDatabase.update(faceModel);
						}
					}
					application.showToast("人脸采集完成");
					// 保存识别记录图片关闭、开启活体检测、开启实时识别
					application.faceProcess.face_rec_set(0, 1, 0, 1, 1, 0, 0,
							0, 0, 1);
				} else if (cmd.equals("CMD_FACE_REGISTER_TIMEOUT")) {
					// 保存识别记录图片关闭、开启活体检测、开启实时识别
					application.faceProcess.face_rec_set(0, 1, 0, 1, 1, 0, 0,
							0, 0, 1);
				}
			}
		}
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this, null, "加载中", false,
					false);
		}
	}

	private void closeProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		application.handler_touch.removeMessages(0);
		// application.handler_touch.sendEmptyMessageDelayed(0,
		// application.screensaver_time * 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
		application.cardType = null;
		// 保存识别记录图片关闭、关闭活体检测、关闭实时识别
		application.faceProcess.face_rec_set(0, 1, 0, 1, 1, 0, 0, 0, 0, 0);
	}

	// @Override
	// public boolean dispatchKeyEvent(KeyEvent event) {
	// switch (event.getAction()) {
	// case KeyEvent.ACTION_DOWN:
	// application.handler_touch.removeMessages(0);
	// break;
	// case KeyEvent.ACTION_UP:
	// application.handler_touch.sendEmptyMessageDelayed(0,
	// application.screensaver_time * 1000);
	// break;
	// }
	// return super.dispatchKeyEvent(event);
	// }
	//
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// switch (ev.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	// application.handler_touch.removeMessages(0);
	// break;
	// case MotionEvent.ACTION_UP:
	// application.handler_touch.sendEmptyMessageDelayed(0,
	// application.screensaver_time * 1000);
	// break;
	// }
	// return super.dispatchTouchEvent(ev);
	// }
}
