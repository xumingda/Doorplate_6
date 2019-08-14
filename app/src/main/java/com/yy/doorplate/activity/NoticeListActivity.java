package com.yy.doorplate.activity;

import java.util.ArrayList;
import java.util.List;

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

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.adapter.AdAdapter;
import com.yy.doorplate.adapter.NoticeAdapter;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.view.MyPullUpListView;
import com.yy.doorplate.view.MyPullUpListView.IloadListener;

public class NoticeListActivity extends Activity implements
		OnItemClickListener, IloadListener {

	private final String TAG = "NoticeListActivity";

	private MyApplication application;

	private Button btn_noticelist_back, btn_notice, btn_guanggao;
	private MyPullUpListView list_notice;

	private NoticeAdapter noticeAdapter = null;

	private List<NoticeInfoModel> list;
	private List<PlayTaskModel> playTaskModels;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private int type = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice_list);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

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
		btn_noticelist_back = (Button) findViewById(R.id.btn_noticelist_back);
		btn_notice = (Button) findViewById(R.id.btn_notice);
		btn_guanggao = (Button) findViewById(R.id.btn_guanggao);
		list_notice = (MyPullUpListView) findViewById(R.id.list_notice);

		btn_noticelist_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		btn_notice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				type = 1;
				btn_notice.setTextColor(getResources().getColor(R.color.white));
				btn_notice
						.setBackgroundResource(R.drawable.btn_classinfo_click);
				btn_guanggao.setTextColor(getResources()
						.getColor(R.color.black));
				btn_guanggao.setBackgroundResource(R.drawable.btn_banjiinfo);
				if (list != null && list.size() > 0) {
					noticeAdapter = new NoticeAdapter(application, list);
					list_notice.setAdapter(noticeAdapter);
				} else {
					list_notice.setAdapter(null);
				}
			}
		});
		btn_guanggao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				type = 2;
				btn_notice.setTextColor(getResources().getColor(R.color.black));
				btn_notice.setBackgroundResource(R.drawable.btn_classinfo);
				btn_guanggao.setTextColor(getResources()
						.getColor(R.color.white));
				btn_guanggao
						.setBackgroundResource(R.drawable.btn_banjiinfo_click);
				if (playTaskModels != null && playTaskModels.size() > 0) {
					AdAdapter adAdapter = new AdAdapter(application,
							playTaskModels);
					list_notice.setAdapter(adAdapter);
				} else {
					list_notice.setAdapter(null);
				}
			}
		});
		list_notice.setOnItemClickListener(this);
		list_notice.setInterface(this);

		NoticeInfoDatabase database = new NoticeInfoDatabase();
		list = database.query_all();
		if (list != null && list.size() > 0) {
			noticeAdapter = new NoticeAdapter(application, list);
			list_notice.setAdapter(noticeAdapter);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (type == 1) {
			Intent intent = new Intent(this, NoticeDetailsActivity.class);
			intent.putExtra("id", list.get(arg2).id);
			startActivity(intent);
		} else if (type == 2) {
			application.isGuanggao = true;
			if (application.playTaskModels != null
					&& !TextUtils
							.isEmpty(application.playTaskModels.get(arg2).srcPath)) {
				String[] s = application.playTaskModels.get(arg2).srcPath
						.split("/");
				String path = MyApplication.PATH_ROOT
						+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1];
				if (application.playTaskModels.get(arg2).srcType
						.equals("IMAGE")) {
					Intent intent = new Intent(this, PlayActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("type", "IMAGE");
					startActivity(intent);
				} else if (application.playTaskModels.get(arg2).srcType
						.equals("VIDEO")) {
					Intent intent = new Intent(this, PlayActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("type", "VIDEO_EXAMPLE");
					startActivity(intent);
				}
			}
		}
	}

	@Override
	public void onLoad() {
		if (list != null && list.size() > 0
				&& list.size() < application.notice_total
				&& !application.isUpdataNoticeList) {
			list_notice.loadStart();
			application.isUpdataNoticeList = true;
			application.httpProcess.QryNotice(
					application.equInfoModel.jssysdm,
					list.size()
							/ Integer.parseInt(MyApplication.NOTICE_PAGE_COUNT)
							+ "", MyApplication.NOTICE_PAGE_COUNT, "", "", "",
					false, null);
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
					List<NoticeInfoModel> infoModels = database.query_limit(
							list.size(),
							Integer.parseInt(MyApplication.NOTICE_PAGE_COUNT));
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
