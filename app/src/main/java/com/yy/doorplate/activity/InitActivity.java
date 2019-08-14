package com.yy.doorplate.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.advertisement.system.ConstData;
import com.common.AppLog;
import com.cx.doorplate.activity.CXMainActivity;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.FTPManager;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.CurriculumInfoDatabase;
import com.yy.doorplate.database.OrgDatabase;
import com.yy.doorplate.database.PageLayoutDatabase;
import com.yy.doorplate.database.VideoDatabase;
import com.yy.doorplate.model.OrgModel;
import com.yy.doorplate.model.PageLayoutModel;
import com.yy.doorplate.model.VideoInfoModel;

public class InitActivity extends Activity implements OnClickListener,
		OnLongClickListener, OnItemSelectedListener {

	private final String TAG = "InitActivity";

	private MyApplication application;

	private RelativeLayout ly_register_1, ly_register_2;
	private EditText edt_init_name, edt_init_jsdm, edt_init_bjdm, edt_init_ip;
	private Button btn_init_next, btn_init_ok, btn_init_set, btn_init_test;
	private Spinner sp_init;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private int curriculumInfos_today_id = 0;

	private String name, jsdm, bjdm;

	private List<PageLayoutModel> pageLayoutModels = null;

	private List<VideoInfoModel> vedioInfoModels = null;
	private int vedioInfoModels_i = 0;

	// 机构列表
	private List<OrgModel> orgs;
	// 选中的机构ID
	private String orgId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_init);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BROADCAST);
		filter.addAction(ConstData.ACTIVITY_UPDATE);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		ly_register_1 = (RelativeLayout) findViewById(R.id.ly_register_1);
		ly_register_2 = (RelativeLayout) findViewById(R.id.ly_register_2);
		edt_init_name = (EditText) findViewById(R.id.edt_init_name);
		edt_init_jsdm = (EditText) findViewById(R.id.edt_init_jsdm);
		edt_init_bjdm = (EditText) findViewById(R.id.edt_init_bjdm);
		edt_init_ip = (EditText) findViewById(R.id.edt_init_ip);
		sp_init = (Spinner) findViewById(R.id.sp_init);
		btn_init_next = (Button) findViewById(R.id.btn_init_next);
		btn_init_ok = (Button) findViewById(R.id.btn_init_ok);
		btn_init_set = (Button) findViewById(R.id.btn_init_set);
		btn_init_test = (Button) findViewById(R.id.btn_init_test);

		btn_init_next.setOnClickListener(this);
		btn_init_ok.setOnClickListener(this);
		btn_init_set.setOnClickListener(this);
		btn_init_set.setOnLongClickListener(this);
		btn_init_test.setOnLongClickListener(this);
		sp_init.setOnItemSelectedListener(this);

		edt_init_ip.setText(application.server_ip);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_init_next: {
			String ip = edt_init_ip.getText().toString().trim();
			if (TextUtils.isEmpty(ip)) {
				application.showToast("请输入服务器地址");
			} else {
				application.setServer_ip(ip);
				application.httpProcess.qryOrg();
				application.showDialog("获取机构列表", 0);
			}
			break;
		}
		case R.id.btn_init_ok: {
			name = edt_init_name.getText().toString();
			jsdm = edt_init_jsdm.getText().toString();
			bjdm = edt_init_bjdm.getText().toString();
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(jsdm)) {
				application.httpProcess.QryDataTime(null, orgId);
				application.showDialog("正在同步时间...", 0);
			}
			break;
		}
		case R.id.btn_init_set: {
			Intent intent = new Intent(InitActivity.this, SettingActivity.class);
			startActivity(intent);
			break;
		}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.btn_init_set: {
			application.openApp(InitActivity.this, "com.android.settings");
			break;
		}
		case R.id.btn_init_test: {
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
			application.openApp(InitActivity.this, "com.yy.doorplatetest");
			break;
		}
		}
		return false;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		orgId = orgs.get(arg2).orgId;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ConstData.ACTIVITY_UPDATE)) {
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
					Intent i = new Intent(InitActivity.this,
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
			if (HttpProcess.QRY_ORG.equals(tag)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				OrgDatabase orgDatabase = new OrgDatabase();
				orgs = orgDatabase.query(null, null);
				if (result && orgs != null) {
					application.stopDialog();
					ly_register_1.setVisibility(View.INVISIBLE);
					ly_register_2.setVisibility(View.VISIBLE);
					List<String> strings = new ArrayList<String>();
					for (OrgModel org : orgs) {
						strings.add(org.orgName);
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							InitActivity.this,
							android.R.layout.simple_list_item_1, strings);
					sp_init.setAdapter(adapter);
				} else {
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_DATATIME)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.httpProcess.Register(name, jsdm, bjdm, orgId);
					application.showDialog("正在注册...", 0);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.EQUIPMENT_REGISTER)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				String networkType = intent.getStringExtra("networkType");
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					if ("STATIC".equals(networkType)) {
						application.showDialog("设置静态IP...", 0);
						application.httpProcess.setIP(
								application.equInfoModel.ip,
								application.equInfoModel.gateway,
								application.equInfoModel.subMask,
								application.equInfoModel.dns,
								application.equInfoModel.dns2, null);
					} else {
						application.showDialog("查询教室信息...", 0);
						application.httpProcess.QryClassRoom(
								application.equInfoModel.jssysdm, null);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.SET_IP)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.showDialog("查询教室信息...", 0);
					application.httpProcess.QryClassRoom(
							application.equInfoModel.jssysdm, null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_CLASS_DEVICE)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					if (!TextUtils.isEmpty(application.equInfoModel.bjdm)) {
						application.showDialog("查询班级信息...", 0);
						application.httpProcess.QryStudent(
								application.equInfoModel.bjdm, "", null, true);


					} else {
						application.showDialog("查询课程信息...", 0);
						application.httpProcess.QryCurriculum(
								application.equInfoModel.jssysdm,
								application.kxsj, application.jssj, "", true,
								null);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
				application.httpProcess.QryLogo(null);
				application.httpProcess.QryScreensaver();
				application.httpProcess
						.getWeather(application.equInfoModel.jssysdm);
				application.httpProcess.qryUser(null);
				application.httpProcess.QryBook(
						application.equInfoModel.jssysdm, "0", "25", "", true,
						null);
				application.httpProcess.qryCarouse();
			} else if (tag.equals(HttpProcess.QRY_STUDENT)) {

				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.initImgStudent();
					application.showDialog("查询教师信息...", 0);
					application.httpProcess.QryTeacher(
							application.equInfoModel.jssysdm, "", null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}


//				boolean result = intent.getBooleanExtra(
//						MyApplication.CMD_RESULT, false);
//				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
//				if (result) {
//					AppLog.d(TAG, tag + " " + msg);
//					application.initImgStudent();
//					if (!TextUtils.isEmpty(application.equInfoModel.bjdm)) {
//						application.showDialog("查询班训信息...", 0);
//						application.httpProcess.qryClassMottoInfo(application.equInfoModel.bjdm, "");
//					}
//				} else {
//					Log.e(TAG, tag + " " + msg);
//					application.showDialog(msg, 3);
//				}
			} else if (tag.equals(HttpProcess.QRY_CLASS_MOTTO)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					//application.initImgStudent();
					application.showDialog("查询教师信息...", 0);
					application.httpProcess.QryTeacher(
							application.equInfoModel.jssysdm, "", null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			}else if (tag.equals(HttpProcess.QRY_TEACHER)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.initImgTeacher();
					application.showDialog("查询课程信息...", 0);
					application.httpProcess.QryCurriculum(
							application.equInfoModel.jssysdm, application.kxsj,
							application.jssj, "", true, null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_CURRICULUM)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					CurriculumInfoDatabase database = new CurriculumInfoDatabase();
					application.curriculumInfos_today = database
							.query_by_skrq(MyApplication.getTime("yyyy-MM-dd"));
					if (application.curriculumInfos_today != null
							&& application.curriculumInfos_today.size() > 0) {
						application.showDialog("查询考勤信息...", 0);
						curriculumInfos_today_id = 0;
						application.httpProcess.QryAttend(
								application.equInfoModel.jssysdm,
								application.curriculumInfos_today
										.get(curriculumInfos_today_id++).id,
								null);
					} else {
						application.showDialog("查询通知信息...", 0);
						application.httpProcess.QryNoticePJ(
								application.equInfoModel.jssysdm, "0",
								MyApplication.NOTICE_PAGE_COUNT,
								application.equInfoModel.bjdm, true, null);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_ATTEND)) {
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
						application.initImgAttend();
						application.showDialog("查询通知信息...", 0);
						application.httpProcess.QryNoticePJ(
								application.equInfoModel.jssysdm, "0",
								MyApplication.NOTICE_PAGE_COUNT,
								application.equInfoModel.bjdm, true, null);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_NOTICE)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.showDialog("查询其他设备列表...", 0);
					application.httpProcess.QryEquList(
							application.equInfoModel.jssysdm, null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_EQU_LIST)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.initImgTelMenu();
					application.showDialog("查询荣誉墙信息...", 0);
					application.httpProcess.QryPrize(
							application.equInfoModel.jssysdm, "", null, true);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_PRIZE)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.initImgPrize();
					application.showDialog("查询班级之星...", 0);
					application.httpProcess.QryStar(
							application.equInfoModel.jssysdm,
							application.equInfoModel.bjdm, "0", "1000", null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_STAR)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.initImgSatr();
					application.showDialog("查询小黑板信息...", 0);
					application.httpProcess.QryBlackboard(
							application.equInfoModel.jssysdm, null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_BLACKBOARD)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.showDialog("查询值日信息...", 0);
					application.httpProcess.QryOnDuty(
							application.equInfoModel.jssysdm, null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_ONDUTY)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.showDialog("查询学校信息...", 0);
					application.httpProcess.QrySchool(
							application.equInfoModel.jssysdm, null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_SCHOOL)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.showDialog("查询问卷调查信息...", 0);
					application.httpProcess.QryQuest(
							application.equInfoModel.jssysdm, "", null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_QUEST)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.initImgQuest();
					application.showDialog("查询投票评比信息...", 0);
					application.httpProcess.QryVote("0",
							MyApplication.NOTICE_PAGE_COUNT, "", "vote",
							"vote", null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_VOTE + "_vote")) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.showDialog("查询活动报名信息...", 0);
					application.httpProcess.QryVote("0",
							MyApplication.NOTICE_PAGE_COUNT, "", "activity",
							"activity", null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_VOTE + "_activity")) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.initImgVote();
					application.showDialog("查询考勤教师信息...", 0);
					application.httpProcess.QryTeacherList(true, null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_TEACHERLIST)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.showDialog("查询考勤规则信息...", 0);
					application.httpProcess.QryClockRule(null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_CLOCKRULE)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.showDialog("查询人员考勤信息...", 0);
					application.httpProcess.QryPersonAttend(null,
							MyApplication.getTime("yyyy-MM-dd"), "");
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_PERSONATTEND)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.setToday(MyApplication.getTime("yyyyMMdd"));
					application.showDialog("查询相册信息...", 0);
					application.httpProcess.QryPhoto(
							application.equInfoModel.jssysdm,
							application.equInfoModel.bjdm, null);
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_PHOTO)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					application.initImgPic();
					// if (application.classInfoModel != null) {
					// application.showDialog("获取小视频信息...", 0);
					// application.httpProcess.qryVideo(null,
					// application.equInfoModel.jssysdm, "bjdm",
					// application.equInfoModel.bjdm,
					// application.equInfoModel.bjdm);
					// } else {
					Finish();
					// }
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_VIDEO)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					AppLog.d(TAG, tag + " " + msg);
					VideoDatabase database = new VideoDatabase();
					vedioInfoModels = database.query(
							"relaObjType = ? and relaObjValue = ?",
							new String[] { "bjdm",
									application.equInfoModel.bjdm });
					vedioInfoModels_i = 0;
					if (vedioInfoModels != null && vedioInfoModels.size() > 0) {
						handler.sendEmptyMessage(0);
					} else {
						Finish();
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showDialog(msg, 3);
				}
			} else if (tag.equals(HttpProcess.QRY_PAGE)) {
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
						Intent i = new Intent(InitActivity.this,
								CXMainActivity.class);
						startActivity(i);
						finish();
					}
				} else {
					Intent i = new Intent(InitActivity.this,
							CXMainActivity.class);
					startActivity(i);

                    finish();
				}
			}
		}
	}

	private void Finish() {
		application.curriculumInfoModel_now = application.findCurriculum(10);
		application.httpProcess.qryPageLayout(null);
		application.httpProcess.QryDiyMenu(application.equInfoModel.jssysdm);
		application.setSynComplete(true);
		application.stopDialog();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				if (vedioInfoModels.size() > vedioInfoModels_i) {
					String[] s = vedioInfoModels.get(vedioInfoModels_i).resPath
							.split("/");
					String path = MyApplication.PATH_ROOT
							+ MyApplication.PATH_VIDEO + "/" + s[s.length - 1];
					downLoadVideo(
							vedioInfoModels.get(vedioInfoModels_i).resPath,
							path);
				} else {
					Finish();
				}
				break;
			}
			}
		};
	};

	private void downLoadVideo(final String urlStr, final String path) {
		if (TextUtils.isEmpty(urlStr)) {
			downLoadVideoError();
			return;
		}
		final File file = new File(path);
		if (file.exists()) {
			downLoadVideoFinish();
			return;
		}
		application.executoService.execute(new Runnable() {
			@Override
			public void run() {
				FTPManager ftpManager = new FTPManager();
				if (ftpManager.connect(urlStr, path)) {
					if (ftpManager.download()) {
						downLoadVideoFinish();
					} else {
						downLoadVideoError();
					}
					ftpManager.disConnect();
				} else {
					downLoadVideoError();
				}
			}
		});
	}

	private void downLoadVideoError() {
		VideoDatabase database = new VideoDatabase();
		database.delete_by_id(vedioInfoModels.get(vedioInfoModels_i).id);
		vedioInfoModels_i++;
		handler.sendEmptyMessage(0);
	}

	private void downLoadVideoFinish() {
		vedioInfoModels_i++;
		handler.sendEmptyMessage(0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();
			if (isShouldHideSoftKeyBoard(view, ev)) {
				hideSoftKeyBoard(view.getWindowToken());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	private boolean isShouldHideSoftKeyBoard(View view, MotionEvent event) {
		if (view != null && (view instanceof EditText)) {
			int[] l = { 0, 0 };
			view.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + view.getHeight(), right = left + view.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
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
