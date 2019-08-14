package com.cx.doorplate.activity;

import java.text.SimpleDateFormat;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.database.LmInfoDatabase;
import com.yy.doorplate.database.NoticeInfoDatabase;
import com.yy.doorplate.database.SchoolInfoDatabase;
import com.yy.doorplate.database.XyxxDatabase;
import com.yy.doorplate.model.LmInfoModel;
import com.yy.doorplate.model.NoticeInfoModel;
import com.yy.doorplate.model.SchoolInfoModel;
import com.yy.doorplate.model.XyxxTypeModel;
import com.yy.doorplate.view.CustomTextView;
import com.yy.doorplate.view.MyPullUpListView;
import com.yy.doorplate.view.MyPullUpListView.IloadListener;

public class CXNoticeListActivity extends Activity implements OnClickListener,
		OnItemClickListener, IloadListener {

	private final String TAG = "CXNoticeListActivity";

	private MyApplication application;

	private RelativeLayout ly_notice_xxxw;
	private Button btn_noticelist_back;
//	private ImageView img_notice_title;
	private TextView txt_weather,txt_notice_title;
	private TextClock textClock;
	private MyPullUpListView list_notice, list_notice_lm, list_notice_xxxw;
	private CustomTextView txt_school_name;

	private CXNoticeAdapter noticeAdapter = null;
	private CXXxxwAdapter xxxwAdapter = null;
	private CXLmAdapter lmAdapter = null;

	private List<NoticeInfoModel> list;
	private List<SchoolInfoModel> schoolInfoModels;
	private List<LmInfoModel> lmInfoModels;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	// 1学校通知 2班级通知 3学校新闻 4班级新闻 5学校信息
	private int type = 1;
	private String type_xxxw = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_notice_list);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		type = getIntent().getIntExtra("type", 1);

		initView();
	}

	private void initView() {
		ly_notice_xxxw = (RelativeLayout) findViewById(R.id.ly_notice_xxxw);
		btn_noticelist_back = (Button) findViewById(R.id.btn_noticelist_back);
//		img_notice_title = (ImageView) findViewById(R.id.img_notice_title);
		txt_notice_title=(TextView) findViewById(R.id.txt_notice_title);
		txt_weather = (TextView) findViewById(R.id.txt_weather);
		txt_school_name = (CustomTextView) findViewById(R.id.txt_school_name);
		list_notice = (MyPullUpListView) findViewById(R.id.list_notice);
		list_notice_lm = (MyPullUpListView) findViewById(R.id.list_notice_lm);
		list_notice_xxxw = (MyPullUpListView) findViewById(R.id.list_notice_xxxw);

		textClock = (TextClock) findViewById(R.id.textClock);
		textClock.setFormat12Hour(null);
		textClock.setFormat24Hour("yyyy年MM月dd日\nEEEE      HH : mm");

		btn_noticelist_back.setOnClickListener(this);
		list_notice.setOnItemClickListener(this);
		list_notice_xxxw.setOnItemClickListener(this);
		list_notice_lm.setOnItemClickListener(this);
		if (type != 5) {
			list_notice.setInterface(this);
			list_notice_xxxw.setInterface(this);
		}

		txt_weather.setText(application.currentCity.trim() + "   "
				+ application.temperature.trim() + "\n"
				+ application.weather.trim());
		updata_new_notice();
	}

	private void updata_new_notice() {
		if (type == 5) {
			txt_notice_title.setText("校园文化");
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
						R.layout.cx_item_notice, new String[] { "title" },
						new int[] { R.id.txt_item_notice_title }));
			}
		} else if (type == 3) {
			XyxxDatabase xyxxDatabase = new XyxxDatabase();
			XyxxTypeModel xyxxTypeModel = xyxxDatabase.query();
			if (xyxxTypeModel != null) {
				txt_school_name.setText(xyxxTypeModel.xymc + " 校园新闻");
			}
			txt_school_name.setVisibility(View.VISIBLE);
			txt_notice_title.setVisibility(View.INVISIBLE);
			ly_notice_xxxw.setVisibility(View.VISIBLE);
			list_notice.setVisibility(View.INVISIBLE);

			LmInfoDatabase lmInfoDatabase = new LmInfoDatabase();
			lmInfoModels = lmInfoDatabase.query("lmdm = ?",
					new String[] { "bpxxxw" });
			if (lmInfoModels != null && lmInfoModels.size() > 0) {
				LmInfoModel lmInfoModel = new LmInfoModel();
				lmInfoModel.xxztlbmc = "全部";
				lmInfoModels.add(0, lmInfoModel);
			} else {
				lmInfoModels = new ArrayList<LmInfoModel>();
				LmInfoModel lmInfoModel = new LmInfoModel();
				lmInfoModel.xxztlbmc = "全部";
				lmInfoModels.add(lmInfoModel);
			}
			lmAdapter = new CXLmAdapter();
			list_notice_lm.setAdapter(lmAdapter);
			NoticeInfoDatabase database = new NoticeInfoDatabase();
			list = database.query_by_lmdm("bpxxxw");
			if (list != null && list.size() > 0) {
				xxxwAdapter = new CXXxxwAdapter();
				list_notice_xxxw.setAdapter(xxxwAdapter);
			}
		} else {
			NoticeInfoDatabase database = new NoticeInfoDatabase();
			if (type == 1) {
				list = database.query_by_lmdm("bpxxtz");
				txt_notice_title.setText("校园通知");
			} else if (type == 2) {
				list = database.query_by_lmdm("bpbjtz");
				txt_notice_title.setText("班级通知");
			} else if (type == 4) {
				list = database.query_by_lmdm("bpbjxw");
				txt_notice_title.setText("班级新闻");
			}
			if (list != null && list.size() > 0) {
				noticeAdapter = new CXNoticeAdapter();
				list_notice.setAdapter(noticeAdapter);
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_noticelist_back:
			finish();
			break;
		}
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.list_notice:
		case R.id.list_notice_xxxw: {
			Intent intent = new Intent(this, CXNoticeDetailsActivity.class);
			intent.putExtra("type", type);
			if (type != 5 && list != null) {
				intent.putExtra("id", list.get(arg2).id);
			} else if (type == 5 && schoolInfoModels != null) {
				intent.putExtra("infoCode", schoolInfoModels.get(arg2).infoCode);
			}
			startActivity(intent);
			break;
		}
		case R.id.list_notice_lm:
			NoticeInfoDatabase database = new NoticeInfoDatabase();
			if (arg2 == 0) {
				list = database.query_by_lmdm("bpxxxw");
			} else {
				type_xxxw = lmInfoModels.get(arg2).xxztlb;
				list = database.query_by_lmdm("bpxxxw", type_xxxw);
			}
			if (list != null && list.size() > 0) {
				xxxwAdapter = new CXXxxwAdapter();
				list_notice_xxxw.setAdapter(xxxwAdapter);
			} else {
				list_notice_xxxw.setAdapter(null);
			}
			lmAdapter.updataSelectedView(arg2);
			break;
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
								"bpxxtz", "", "", false, null);
			}
		} else if (type == 2) {
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
								"bpbjtz", "", "", false, null);
			}
		} else if (type == 3) {
			int total = 0;
			if (TextUtils.isEmpty(type_xxxw)) {
				total = application.notice_total_bpxxxw;
			} else {
				total = application.getNotice_total_bpxxxw(type_xxxw);
			}
			if (list != null && list.size() > 0 && list.size() < total
					&& !application.isUpdataNoticeList) {
				list_notice_xxxw.loadStart();
				application.isUpdataNoticeList = true;
				application.httpProcess
						.QryNotice(
								application.equInfoModel.jssysdm,
								list.size()
										/ Integer
												.parseInt(MyApplication.NOTICE_PAGE_COUNT)
										+ "", MyApplication.NOTICE_PAGE_COUNT,
								"bpxxxw", type_xxxw, "", false, null);
			}
		} else if (type == 4) {
			if (list != null && list.size() > 0
					&& list.size() < application.notice_total_bpbjxw
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
								"bpbjxw", "", application.equInfoModel.bjdm,
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
					} else if (type == 2) {
						infoModels = database.query_by_lmdm("bpbjtz", list
								.size(), Integer
								.parseInt(MyApplication.NOTICE_PAGE_COUNT));
					} else if (type == 3) {
						if (TextUtils.isEmpty(type_xxxw)) {
							infoModels = database.query_by_lmdm("bpxxxw", list
									.size(), Integer
									.parseInt(MyApplication.NOTICE_PAGE_COUNT));
						} else {
							infoModels = database
									.query_by_lmdm(
											"bpxxxw",
											type_xxxw,
											list.size(),
											Integer.parseInt(MyApplication.NOTICE_PAGE_COUNT));
						}
					} else if (type == 4) {
						infoModels = database.query_by_lmdm("bpbjxw", list
								.size(), Integer
								.parseInt(MyApplication.NOTICE_PAGE_COUNT));
					}
					if (infoModels != null && infoModels.size() > 0) {
						for (NoticeInfoModel model : infoModels) {
							list.add(model);
						}
					}
					if (type == 3) {
						xxxwAdapter.notifyDataSetChanged();
					} else {
						noticeAdapter.notifyDataSetChanged();
					}
				} else {
					Log.e(TAG, tag + " " + msg);
					application.showToast("更新通知失败，原因：" + msg);
				}
				application.isUpdataNoticeList = false;
				list_notice.loadComplete();
				list_notice_xxxw.loadComplete();
			} else if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

	public class CXNoticeAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			private TextView txt_item_notice_time;
			private CustomTextView txt_item_notice_title;
		}

		@Override
		public View getView(final int position, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(application).inflate(
						R.layout.cx_item_notice, null);
				viewHolder.txt_item_notice_title = (CustomTextView) view
						.findViewById(R.id.txt_item_notice_title);
				viewHolder.txt_item_notice_time = (TextView) view
						.findViewById(R.id.txt_item_notice_time);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.txt_item_notice_title.setText(list.get(position).xxzt);
			if (!TextUtils.isEmpty(list.get(position).fbsj)) {
				try {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					viewHolder.txt_item_notice_time.setText("发布时间 : "
							+ formatter.format(Long.parseLong(list
									.get(position).fbsj)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					viewHolder.txt_item_notice_time
							.setText(list.get(position).fbsj);
				}
			}
			return view;
		}
	}

	public class CXLmAdapter extends BaseAdapter {

		private int selectionItem = 0;

		@Override
		public int getCount() {
			return lmInfoModels.size();
		}

		@Override
		public Object getItem(int position) {
			return lmInfoModels.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			private RelativeLayout ly_item_lm;
			private TextView txt_item_lm;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(application).inflate(
						R.layout.cx_item_lm, null);
				viewHolder.ly_item_lm = (RelativeLayout) view
						.findViewById(R.id.ly_item_lm);
				viewHolder.txt_item_lm = (TextView) view
						.findViewById(R.id.txt_item_lm);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.txt_item_lm.setText(lmInfoModels.get(position).xxztlbmc);
			if (position == selectionItem) {
				viewHolder.txt_item_lm.setTextColor(getResources().getColor(
						R.color.white));
				viewHolder.ly_item_lm
						.setBackgroundResource(R.drawable.cx_item_lm_click);
			} else {
				viewHolder.txt_item_lm.setTextColor(getResources().getColor(
						R.color.black));
				viewHolder.ly_item_lm.setBackground(null);
			}
			ListView.LayoutParams params = new ListView.LayoutParams(
					ListView.LayoutParams.MATCH_PARENT, 108);
			view.setLayoutParams(params);
			return view;
		}

		public void updataSelectedView(int position) {
			selectionItem = position;
			if (lmInfoModels != null && lmInfoModels.size() > 0) {
				int startShownIndex = list_notice_lm.getFirstVisiblePosition();
				int endShownIndex = list_notice_lm.getLastVisiblePosition();
				for (int i = startShownIndex; i <= endShownIndex; i++) {
					View view = list_notice_lm.getChildAt(i - startShownIndex);
					if (view == null) {
						break;
					}
					ListView.LayoutParams params = new ListView.LayoutParams(
							ListView.LayoutParams.MATCH_PARENT, 108);
					view.setLayoutParams(params);
					ViewHolder viewHolder = (ViewHolder) view.getTag();
					if (viewHolder != null) {
						if (position != -1 && position == i) {
							viewHolder.txt_item_lm.setTextColor(getResources()
									.getColor(R.color.white));
							viewHolder.ly_item_lm
									.setBackgroundResource(R.drawable.cx_item_lm_click);
						} else {
							viewHolder.txt_item_lm.setTextColor(getResources()
									.getColor(R.color.black));
							viewHolder.ly_item_lm.setBackground(null);
						}
					}
				}
			}
		}
	}

	public class CXXxxwAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			private TextView txt_item_notice_time;
			private CustomTextView txt_item_notice_title;
		}

		@Override
		public View getView(final int position, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(application).inflate(
						R.layout.cx_item_xxxw, null);
				viewHolder.txt_item_notice_title = (CustomTextView) view
						.findViewById(R.id.txt_item_notice_title);
				viewHolder.txt_item_notice_time = (TextView) view
						.findViewById(R.id.txt_item_notice_time);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.txt_item_notice_title.setText(list.get(position).xxzt);
			if (!TextUtils.isEmpty(list.get(position).fbsj)) {
				try {
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					viewHolder.txt_item_notice_time.setText(formatter
							.format(Long.parseLong(list.get(position).fbsj)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					viewHolder.txt_item_notice_time
							.setText(list.get(position).fbsj);
				}
			}
			return view;
		}
	}
}
