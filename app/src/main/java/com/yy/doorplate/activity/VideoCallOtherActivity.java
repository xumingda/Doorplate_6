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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.EquListDatabase;
import com.yy.doorplate.database.MenuAndEqDatabase;
import com.yy.doorplate.model.EquInfoModel;
import com.yy.doorplate.model.MenuAndEqModel;
import com.yy.doorplate.view.CustomTextView;

public class VideoCallOtherActivity extends Activity implements
		OnClickListener, OnItemClickListener {

	private final String TAG = "VideoCallOtherActivity";

	private MyApplication application;

	private Button btn_callother_back, btn_callother_search;
	private EditText edt_callother_search;
	private GridView gv_callother;

	private List<EquInfoModel> list = new ArrayList<EquInfoModel>();

	private MyAdapter adapter = null;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videocall_other);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

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
		btn_callother_back = (Button) findViewById(R.id.btn_callother_back);
		btn_callother_search = (Button) findViewById(R.id.btn_callother_search);
		edt_callother_search = (EditText) findViewById(R.id.edt_callother_search);
		gv_callother = (GridView) findViewById(R.id.gv_callother);
		edt_callother_search.addTextChangedListener(application.textWatcher);
		btn_callother_back.setOnClickListener(this);
		btn_callother_search.setOnClickListener(this);
		gv_callother.setOnItemClickListener(this);

		EquListDatabase equListDatabase = new EquListDatabase();
		MenuAndEqDatabase menuAndEqDatabase = new MenuAndEqDatabase();
		List<MenuAndEqModel> menuAndEqModels = menuAndEqDatabase
				.query_by_tel(getIntent().getStringExtra("menuId"));
		if (menuAndEqModels != null && menuAndEqModels.size() > 0) {
			for (MenuAndEqModel model : menuAndEqModels) {
				EquInfoModel equInfoModel = equListDatabase
						.query_by_id(model.equCode);
				if (equInfoModel != null) {
					list.add(equInfoModel);
				}
			}
		}
		if (list != null && list.size() > 0) {
			adapter = new MyAdapter();
			gv_callother.setAdapter(adapter);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_callother_back:
			finish();
			break;
		case R.id.btn_callother_search:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		if (!TextUtils.isEmpty(list.get(position).ip)) {
			application.call_eq = list.get(position);
			Intent intent = new Intent(this, CallActivity.class);
			intent.putExtra("isCall", true);
			startActivity(intent);
		} else {
			application.showToast("设备IP地址异常，无法呼叫");
		}
	}

	private class MyAdapter extends BaseAdapter {

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
			private TextView txt_call_other;
		}

		@Override
		public View getView(final int position, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(VideoCallOtherActivity.this)
						.inflate(R.layout.item_call_other, null);
				viewHolder.txt_call_other = (CustomTextView) view
						.findViewById(R.id.txt_call_other);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.txt_call_other.setText(list.get(position).jssysmc);
			return view;
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
