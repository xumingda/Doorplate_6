package com.yy.doorplate.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import android.view.WindowManager;

import com.advertisement.system.ConstData;
import com.common.AppLog;
import com.cx.doorplate.activity.CXMainActivity;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.PageLayoutDatabase;
import com.yy.doorplate.model.PageLayoutModel;

public class EmptyActivity extends Activity {

	private final String TAG = "EmptyActivity";

	private MyApplication application;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private int curriculumInfos_today_id = 0;

	private Timer timer = null;

	private List<PageLayoutModel> pageLayoutModels = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		AppLog.d(TAG, "-------EmptyActivity onCreate------");


//		Intent i = new Intent(EmptyActivity.this,
//				CXMainActivity.class);
//		startActivity(i);



		if (application.timer_init == null) {
		    AppLog.d(TAG, "---application.timer_init == null---");

			application.timer_init = new Timer();
			application.timer_init.schedule(new TimerTask() {

				@Override
				public void run() {
				    //lph20190402
//					application.UsbOtgCtrl(2);
//					application.openDog();
//					application.openUSBListener();
					if (application.isSynComplete) {
						broadcastManager = LocalBroadcastManager
								.getInstance(EmptyActivity.this);
						myBroadcastReceiver = new MyBroadcastReceiver();
						IntentFilter filter = new IntentFilter();
						filter.addAction(MyApplication.BROADCAST);
						filter.addAction(ConstData.ACTIVITY_UPDATE);
						broadcastManager.registerReceiver(myBroadcastReceiver,
								filter);
						application.httpProcess.QryDataTime(null,
								application.equInfoModel.orgId);
					} else {
						Intent intent = new Intent(EmptyActivity.this,
								InitActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}, 10 * 1000);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (application.timer_init != null) {
			application.timer_init.cancel();
			application.timer_init = null;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (myBroadcastReceiver != null) {
			broadcastManager.unregisterReceiver(myBroadcastReceiver);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConstData.ACTIVITY_UPDATE)) {
				AppLog.d(TAG, "------------ConstData.ACTIVITY_UPDATE-----------");
				if (application.pageLayoutTotal > 0) {
					// application.pageLayoutCount++;
					// if (application.pageLayoutCount <
					// application.pageLayoutTotal) {
					// application.systemManager
					// .downloadPolicy(pageLayoutModels
					// .get(application.pageLayoutCount).resPath);
					// } else {
					boolean result = intent.getBooleanExtra(
							MyApplication.CMD_RESULT, false);
					if (!result) {
						PageLayoutDatabase database = new PageLayoutDatabase();
						database.delete_all();
					}
					AppLog.d(TAG, "------------CXMainActivity-----------");
					application.pageLayoutTotal = 0;
					application.pageLayoutCount = 0;
					Intent i = new Intent(EmptyActivity.this,
							CXMainActivity.class);
					startActivity(i);
					finish();
					// }
				}
			}
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals(HttpProcess.QRY_DATATIME)
					&& application.isSynComplete) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					application.showToast("同步时间成功");
				} else {
					application.showToast("同步时间失败，原因：" + msg);
				}
				if (timer == null) {
					timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							CurriculumInfoDatabase curriculumInfoDatabase = new CurriculumInfoDatabase();
							application.curriculumInfos_today = curriculumInfoDatabase
									.query_by_skrq(MyApplication
											.getTime("yyyy-MM-dd"));
							application.curriculumInfoModel_now = application
									.findCurriculum(10);
							if (MyApplication.getTime("yyyyMMdd").equals(
									application.today)) {
								Finish();
							} else {
								if (application.curriculumInfos_today != null) {
									curriculumInfos_today_id = 0;
									application.httpProcess
											.QryAttend(
													application.equInfoModel.jssysdm,
													application.curriculumInfos_today
															.get(curriculumInfos_today_id++).id,
													null);
								} else {
									application.httpProcess
											.QryPersonAttend(
													null,
													MyApplication
															.getTime("yyyy-MM-dd"),
													"");
								}
							}
						}
					}, 3 * 1000);
				}
			} else if (tag.equals(HttpProcess.QRY_ATTEND)
					&& application.getRunningActivityName().equals(
							"com.yy.doorplate.activity.EmptyActivity")) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg + " " + curriculumInfos_today_id);
					if (curriculumInfos_today_id < application.curriculumInfos_today
							.size()) {
						application.httpProcess.QryAttend(
								application.equInfoModel.jssysdm,
								application.curriculumInfos_today
										.get(curriculumInfos_today_id++).id,
								null);
					} else {
						// broadcastManager
						// .unregisterReceiver(myBroadcastReceiver);
						application.httpProcess.QryPersonAttend(null,
								MyApplication.getTime("yyyy-MM-dd"), "");
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.httpProcess.QryPersonAttend(null,
							MyApplication.getTime("yyyy-MM-dd"), "");
				}
			} else if (tag.equals(HttpProcess.QRY_PERSONATTEND)
					&& application.getRunningActivityName().equals(
							"com.yy.doorplate.activity.EmptyActivity")) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					application.setToday(MyApplication.getTime("yyyyMMdd"));
				}
				Finish();
			} else if (tag.equals(HttpProcess.QRY_PAGE)) {
				AppLog.d(TAG, "------------QRY_PAGE-----------");
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				if (result) {
					PageLayoutDatabase database = new PageLayoutDatabase();
					pageLayoutModels = database.query_all();
					if (pageLayoutModels != null
							&& pageLayoutModels.size() > 0
							&& !TextUtils
									.isEmpty(pageLayoutModels.get(0).resPath)) {
						application.showToast("正在加载动态布局");
						application.pageLayoutTotal = pageLayoutModels.size();
						application.systemManager
								.downloadPolicy(pageLayoutModels.get(0).resPath);
					} else {
						Intent i = new Intent(EmptyActivity.this,
								CXMainActivity.class);
						startActivity(i);
						finish();
					}
				} else {
					Intent i = new Intent(EmptyActivity.this,
							CXMainActivity.class);
					startActivity(i);
					finish();
				}
			}
		}
	}

	private void Finish() {
		// Intent i = new Intent(
		// EmptyActivity.this,
		// CXMainActivity.class);
		// startActivity(i);
		// finish();
		application.httpProcess.qryPageLayout(null);
		application.httpProcess.QryDiyMenu(application.equInfoModel.jssysdm);
	}
}
