package com.yy.doorplate.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.advertisement.activity.DisplayActivity;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.AdAdapter;
import com.yy.doorplate.adapter.NoticeAdapter;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.SchoolInfoDatabase;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.model.SchoolInfoModel;
import com.yy.doorplate.view.MyPullUpListView;
import com.yy.doorplate.view.MyPullUpListView.IloadListener;

public class PjNoticeListActivity extends Activity implements OnClickListener,
		OnItemClickListener, IloadListener {

	private final String TAG = "PjNoticeListActivity";

	private MyApplication application;

	private LinearLayout ly_notice_top;
	private TextView txt_notice_top;
	private Button btn_noticelist_back, btn_notice, btn_notice_bj,
			btn_guanggao;
	private MyPullUpListView list_notice;

	private NoticeAdapter noticeAdapter = null;

	private List<NoticeInfoModel> list;
	private List<SchoolInfoModel> schoolInfoModels;
	private List<PlayTaskModel> playTaskModels;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	// 1学校通知 2宣传公告 3班级通知 4学校新闻 5学校信息
	private int type = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pj_activity_notice_list);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		type = getIntent().getIntExtra("type", 1);

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
		ly_notice_top = (LinearLayout) findViewById(R.id.ly_notice_top);
		txt_notice_top = (TextView) findViewById(R.id.txt_notice_top);
		btn_noticelist_back = (Button) findViewById(R.id.btn_noticelist_back);
		btn_notice = (Button) findViewById(R.id.btn_notice);
		btn_notice_bj = (Button) findViewById(R.id.btn_notice_bj);
		btn_guanggao = (Button) findViewById(R.id.btn_guanggao);
		list_notice = (MyPullUpListView) findViewById(R.id.list_notice);

		btn_noticelist_back.setOnClickListener(this);
		btn_notice.setOnClickListener(this);
		btn_notice_bj.setOnClickListener(this);
		btn_guanggao.setOnClickListener(this);

		list_notice.setOnItemClickListener(this);
		if (type == 1 || type == 3 || type == 4) {
			list_notice.setInterface(this);
		}

		if (type <= 3) {
			ly_notice_top.setVisibility(View.VISIBLE);
			txt_notice_top.setVisibility(View.INVISIBLE);
			if (type == 1) {
				btn_notice.setTextColor(getResources().getColor(R.color.white));
				btn_notice
						.setBackgroundResource(R.drawable.btn_classinfo_click);
				btn_notice_bj.setTextColor(getResources().getColor(
						R.color.black));
				btn_notice_bj.setBackgroundColor(getResources().getColor(
						R.color.white));
				btn_guanggao.setTextColor(getResources()
						.getColor(R.color.black));
				btn_guanggao.setBackgroundResource(R.drawable.btn_banjiinfo);
				NoticeInfoDatabase database = new NoticeInfoDatabase();
				list = database.query_by_lmdm("bpxxtz");
				if (list != null && list.size() > 0) {
					noticeAdapter = new NoticeAdapter(application, list);
					list_notice.setAdapter(noticeAdapter);
				}
			}
			if (type == 3) {
				btn_notice.setTextColor(getResources().getColor(R.color.black));
				btn_notice.setBackgroundResource(R.drawable.btn_classinfo);
				btn_notice_bj.setTextColor(getResources().getColor(
						R.color.white));
				btn_notice_bj.setBackgroundColor(getResources().getColor(
						R.color.YELLOW));
				btn_guanggao.setTextColor(getResources()
						.getColor(R.color.black));
				btn_guanggao.setBackgroundResource(R.drawable.btn_banjiinfo);
				NoticeInfoDatabase database = new NoticeInfoDatabase();
				list = database.query_by_lmdm("bpbjtz");
				if (list != null && list.size() > 0) {
					noticeAdapter = new NoticeAdapter(application, list);
					list_notice.setAdapter(noticeAdapter);
				}
			}
		} else {
			ly_notice_top.setVisibility(View.INVISIBLE);
			txt_notice_top.setVisibility(View.VISIBLE);
			if (type == 4) {
				txt_notice_top.setText("学校新闻");
				NoticeInfoDatabase database = new NoticeInfoDatabase();
				list = database.query_by_lmdm("bpxxxw");
				if (list != null && list.size() > 0) {
					noticeAdapter = new NoticeAdapter(application, list);
					list_notice.setAdapter(noticeAdapter);
				}
			}
			if (type == 5) {
				txt_notice_top.setText("学校信息");
				SchoolInfoDatabase database = new SchoolInfoDatabase();
				schoolInfoModels = database.query_all();
				if (schoolInfoModels != null && schoolInfoModels.size() > 0) {
					List<Map<String, String>> list = new ArrayList<Map<String, String>>();
					for (int i = 0; i < schoolInfoModels.size(); i++) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("title", schoolInfoModels.get(i).infoName);
						list.add(map);
					}
					list_notice.setAdapter(new SimpleAdapter(application, list,
							R.layout.item_notice, new String[] { "title" },
							new int[] { R.id.txt_item_notice_title }));
				}
			}
		}

		if (application.playTaskModels != null
				&& application.playTaskModels.size() > 0) {
			playTaskModels = new ArrayList<PlayTaskModel>();
			for (PlayTaskModel model : application.playTaskModels) {
				if (model.playType.equals("MEDIA")) {
					playTaskModels.add(model);
				}
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_noticelist_back:
			finish();
			break;
		case R.id.btn_notice: {
			type = 1;
			btn_notice.setTextColor(getResources().getColor(R.color.white));
			btn_notice.setBackgroundResource(R.drawable.btn_classinfo_click);
			btn_notice_bj.setTextColor(getResources().getColor(R.color.black));
			btn_notice_bj.setBackgroundColor(getResources().getColor(
					R.color.white));
			btn_guanggao.setTextColor(getResources().getColor(R.color.black));
			btn_guanggao.setBackgroundResource(R.drawable.btn_banjiinfo);
			NoticeInfoDatabase database = new NoticeInfoDatabase();
			list = database.query_by_lmdm("bpxxtz");
			if (list != null && list.size() > 0) {
				noticeAdapter = new NoticeAdapter(application, list);
				list_notice.setAdapter(noticeAdapter);
			} else {
				list_notice.setAdapter(null);
			}
			break;
		}
		case R.id.btn_notice_bj: {
			type = 3;
			btn_notice.setTextColor(getResources().getColor(R.color.black));
			btn_notice.setBackgroundResource(R.drawable.btn_classinfo);
			btn_notice_bj.setTextColor(getResources().getColor(R.color.white));
			btn_notice_bj.setBackgroundColor(getResources().getColor(
					R.color.YELLOW));
			btn_guanggao.setTextColor(getResources().getColor(R.color.black));
			btn_guanggao.setBackgroundResource(R.drawable.btn_banjiinfo);
			NoticeInfoDatabase database = new NoticeInfoDatabase();
			list = database.query_by_lmdm("bpbjtz");
			if (list != null && list.size() > 0) {
				noticeAdapter = new NoticeAdapter(application, list);
				list_notice.setAdapter(noticeAdapter);
			} else {
				list_notice.setAdapter(null);
			}
			break;
		}
		case R.id.btn_guanggao: {
			type = 2;
			btn_notice.setTextColor(getResources().getColor(R.color.black));
			btn_notice.setBackgroundResource(R.drawable.btn_classinfo);
			btn_notice_bj.setTextColor(getResources().getColor(R.color.black));
			btn_notice_bj.setBackgroundColor(getResources().getColor(
					R.color.white));
			btn_guanggao.setTextColor(getResources().getColor(R.color.white));
			btn_guanggao.setBackgroundResource(R.drawable.btn_banjiinfo_click);
			if (playTaskModels != null && playTaskModels.size() > 0) {
				AdAdapter adAdapter = new AdAdapter(application, playTaskModels);
				list_notice.setAdapter(adAdapter);
			} else {
				list_notice.setAdapter(null);
			}
			break;
		}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (type == 1) {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校通知");
			intent.putExtra("id", list.get(arg2).id);
			startActivity(intent);
		} else if (type == 2) {
			application.isGuanggao = true;
			if (playTaskModels != null
					&& !TextUtils.isEmpty(playTaskModels.get(arg2).srcPath)) {
				String[] s = playTaskModels.get(arg2).srcPath.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1];
				if (playTaskModels.get(arg2).srcType.equals("IMAGE")) {
					Intent intent = new Intent(this, PlayActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("type", "IMAGE");
					startActivity(intent);
				} else if (playTaskModels.get(arg2).srcType.equals("VIDEO")) {
					Intent intent = new Intent(this, PlayActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("type", "VIDEO_EXAMPLE");
					startActivity(intent);
				} else if (playTaskModels.get(arg2).srcType.equals("TEMPLATE")) {
					Intent intent = new Intent(getApplicationContext(),
							DisplayActivity.class);
					intent.putExtra("ThemePolicyPath", application
							.getFtpPath(playTaskModels.get(arg2).srcPath));
					startActivity(intent);
				}
			}
		} else if (type == 3) {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("top", "班级通知");
			intent.putExtra("id", list.get(arg2).id);
			startActivity(intent);
		} else if (type == 4) {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("top", "学校新闻");
			intent.putExtra("id", list.get(arg2).id);
			startActivity(intent);
		} else if (type == 5) {
			Intent intent = new Intent(this, SchoolDetailsActivity.class);
			intent.putExtra("infoCode", schoolInfoModels.get(arg2).infoCode);
			startActivity(intent);
		}
	}

	@Override
	public void onLoad() {
		if (type == 1) {
			if (list != null && list.size() > 0
					&& list.size() < application.notice_total_bpxxtz
					&& !application.isUpdataNoticeList) {
				list_notice.loadStart();
				application.isUpdataNoticeList = true;
				application.httpProcess
						.QryNotice(
								application.equInfoModel.jssysdm,
								list.size()
										/ Integer
												.parseInt(MyApplication.NOTICE_PAGE_COUNT)
										+ "", MyApplication.NOTICE_PAGE_COUNT,
								"bpxxtz", "", application.equInfoModel.bjdm,
								false, null);
			}
		} else if (type == 3) {
			if (list != null && list.size() > 0
					&& list.size() < application.notice_total_bpbjtz
					&& !application.isUpdataNoticeList) {
				list_notice.loadStart();
				application.isUpdataNoticeList = true;
				application.httpProcess
						.QryNotice(
								application.equInfoModel.jssysdm,
								list.size()
										/ Integer
												.parseInt(MyApplication.NOTICE_PAGE_COUNT)
										+ "", MyApplication.NOTICE_PAGE_COUNT,
								"bpbjtz", "", application.equInfoModel.bjdm,
								false, null);
			}
		} else if (type == 4) {
			if (list != null && list.size() > 0
					&& list.size() < application.notice_total_bpxxxw
					&& !application.isUpdataNoticeList) {
				list_notice.loadStart();
				application.isUpdataNoticeList = true;
				application.httpProcess
						.QryNotice(
								application.equInfoModel.jssysdm,
								list.size()
										/ Integer
												.parseInt(MyApplication.NOTICE_PAGE_COUNT)
										+ "", MyApplication.NOTICE_PAGE_COUNT,
								"bpxxxw", "", application.equInfoModel.bjdm,
								false, null);
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
			if (tag.equals(HttpProcess.QRY_NOTICE) && !application.isUpdateEQ
					&& application.isUpdataNoticeList) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					NoticeInfoDatabase database = new NoticeInfoDatabase();
					List<NoticeInfoModel> infoModels = null;
					if (type == 1) {
						infoModels = database.query_by_lmdm("bpxxtz", list
								.size(), Integer
								.parseInt(MyApplication.NOTICE_PAGE_COUNT));
					} else if (type == 3) {
						infoModels = database.query_by_lmdm("bpbjtz", list
								.size(), Integer
								.parseInt(MyApplication.NOTICE_PAGE_COUNT));
					} else if (type == 4) {
						infoModels = database.query_by_lmdm("bpxxxw", list
								.size(), Integer
								.parseInt(MyApplication.NOTICE_PAGE_COUNT));
					}
					if (infoModels != null && infoModels.size() > 0) {
						for (NoticeInfoModel model : infoModels) {
							list.add(model);
						}
					}
					noticeAdapter.notifyDataSetChanged();
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("更新通知失败，原因：" + msg);
				}
				application.isUpdataNoticeList = false;
				list_notice.loadComplete();
			} else if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}
}
