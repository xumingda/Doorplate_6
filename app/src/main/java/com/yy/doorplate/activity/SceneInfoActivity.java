package com.yy.doorplate.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.DeviceDatabase;
import com.yy.doorplate.database.SAndDDatabase;
import com.yy.doorplate.database.SceneDatabase;
import com.yy.doorplate.model.DeviceModel;
import com.yy.doorplate.model.SceneAndDeviceModel;
import com.yy.doorplate.model.SceneModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class SceneInfoActivity extends Activity implements OnClickListener,
		OnItemClickListener {

	private final String TAG = "SceneInfoActivity";

	private MyApplication application;

	private Button btn_sceneinfo_back, btn_sceneinfo_edit;
	private TextView txt_sceneinfo_title;
	private GridView gv_sceneinfo;

	private SceneDatabase sceneDatabase = new SceneDatabase();
	private DeviceDatabase deviceDatabase = new DeviceDatabase();
	private SAndDDatabase sAndDDatabase = new SAndDDatabase();

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private SceneModel sceneModel;
	private List<SceneAndDeviceModel> sceneAndDeviceModels;
	private List<DeviceModel> deviceModels_haved, deviceModels_all;

	private MyAdapter adapter;

	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sceneinfo);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initData();
		initView();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
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

	private void initData() {
		sceneModel = sceneDatabase.query_by_id(getIntent().getIntExtra(
				"sceneId", 0));
		deviceModels_all = deviceDatabase.query_all();
		sceneAndDeviceModels = new ArrayList<SceneAndDeviceModel>();
		if (sceneModel != null) {
			sceneAndDeviceModels = sAndDDatabase
					.query_by_sceneId(sceneModel.id);
		}
		deviceModels_haved = new ArrayList<DeviceModel>();
		if (sceneAndDeviceModels != null && sceneAndDeviceModels.size() > 0) {
			for (SceneAndDeviceModel model : sceneAndDeviceModels) {
				DeviceModel deviceModel = deviceDatabase
						.query_by_id(model.deviceId);
				if (deviceModel != null) {
					deviceModels_haved.add(deviceModel);
				}
			}
		}
	}

	private void initView() {
		btn_sceneinfo_back = (Button) findViewById(R.id.btn_sceneinfo_back);
		btn_sceneinfo_edit = (Button) findViewById(R.id.btn_sceneinfo_edit);
		txt_sceneinfo_title = (TextView) findViewById(R.id.txt_sceneinfo_title);
		gv_sceneinfo = (GridView) findViewById(R.id.gv_sceneinfo);

		btn_sceneinfo_back.setOnClickListener(this);
		btn_sceneinfo_edit.setOnClickListener(this);
		if (sceneModel != null && !TextUtils.isEmpty(sceneModel.name)) {
			txt_sceneinfo_title.setText(sceneModel.name + "场景信息关联设备列表");
		}
		gv_sceneinfo.setOnItemClickListener(this);

		if (deviceModels_haved != null && deviceModels_haved.size() > 0) {
			adapter = new MyAdapter(deviceModels_haved, 1);
			gv_sceneinfo.setAdapter(adapter);
		} else {
			application.showToast("暂无设备，点击右上角编辑添加设备");
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_sceneinfo_back:
			finish();
			break;
		case R.id.btn_sceneinfo_edit: {
			if (btn_sceneinfo_edit.getText().toString().equals("编辑")) {
				btn_sceneinfo_edit.setText("保存");
				if (deviceModels_all != null && deviceModels_all.size() > 0) {
					adapter = new MyAdapter(deviceModels_all, 2);
					gv_sceneinfo.setAdapter(adapter);
				} else {
					gv_sceneinfo.setAdapter(null);
					application.showToast("暂无设备，请先添加设备");
				}
			} else if (btn_sceneinfo_edit.getText().toString().equals("保存")) {
				// sAndDDatabase.delete_by_sceneId(sceneModel.id);
				// if (deviceModels_all != null && deviceModels_all.size() > 0
				// && booleans != null && booleans.length > 0
				// && deviceModels_all.size() == booleans.length) {
				// for (int i = 0; i < booleans.length; i++) {
				// if (booleans[i]) {
				// SceneAndDeviceModel model = new SceneAndDeviceModel();
				// model.sceneId = sceneModel.id;
				// model.deviceId = deviceModels_all.get(i).id;
				// sAndDDatabase.insert(model);
				// }
				// }
				// }
				btn_sceneinfo_edit.setText("编辑");
				initData();
				if (deviceModels_haved != null && deviceModels_haved.size() > 0) {
					adapter = new MyAdapter(deviceModels_haved, 1);
					gv_sceneinfo.setAdapter(adapter);
				} else {
					gv_sceneinfo.setAdapter(null);
				}
				if (deviceModels_all != null && deviceModels_all.size() > 0) {
					application.showToast("保存成功");
				}
			}
			break;
		}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		adapter.updataImg(position);
	}

	private class MyAdapter extends BaseAdapter {

		private List<DeviceModel> list;
		private int model = 1;

		public MyAdapter(List<DeviceModel> list, int model) {
			this.list = list;
			this.model = model;
		}

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
			private TextView btn_scene_info;
			private ImageView img_scene_info;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(SceneInfoActivity.this).inflate(
						R.layout.item_sceneinfo, null);
				viewHolder.btn_scene_info = (TextView) view
						.findViewById(R.id.btn_scene_info);
				viewHolder.img_scene_info = (ImageView) view
						.findViewById(R.id.img_scene_info);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			viewHolder.btn_scene_info.setText(list.get(position).name);
			final TextView textView = viewHolder.btn_scene_info;
			OnImageLoaderListener listener = new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					textView.setBackground(new BitmapDrawable(bitmap));
				}
			};
			switch (list.get(position).img) {
			case 1:
				imageLoader.getBitmapFormRes(R.drawable.device_1_on, listener);
				break;
			case 2:
				imageLoader.getBitmapFormRes(R.drawable.device_2_on, listener);
				break;
			case 3:
				imageLoader.getBitmapFormRes(R.drawable.device_3_on, listener);
				break;
			case 4:
				imageLoader.getBitmapFormRes(R.drawable.device_4_on, listener);
				break;
			case 5:
				imageLoader.getBitmapFormRes(R.drawable.device_5_on, listener);
				break;
			case 6:
				imageLoader.getBitmapFormRes(R.drawable.device_6_on, listener);
				break;
			case 7:
				imageLoader.getBitmapFormRes(R.drawable.device_7_on, listener);
				break;
			case 8:
				imageLoader.getBitmapFormRes(R.drawable.device_8_on, listener);
				break;
			default:
				imageLoader.getBitmapFormRes(R.drawable.device_9_on, listener);
				break;
			}
			viewHolder.img_scene_info.setTag(position);
			if (model == 2
					&& sAndDDatabase.query_by_sd(sceneModel.id,
							list.get(position).id) != null) {
				viewHolder.img_scene_info.setVisibility(View.VISIBLE);
			}
			return view;
		}

		public void updataImg(int position) {
			if (model == 2) {
				ImageView imageView = (ImageView) gv_sceneinfo
						.findViewWithTag(position);
				if (imageView != null) {
					if (imageView.getVisibility() == View.VISIBLE) {
						imageView.setVisibility(View.INVISIBLE);
						sAndDDatabase.delete(sceneModel.id,
								deviceModels_all.get(position).id);
					} else if (imageView.getVisibility() == View.INVISIBLE) {
						imageView.setVisibility(View.VISIBLE);
						SceneAndDeviceModel model = new SceneAndDeviceModel();
						model.sceneId = sceneModel.id;
						model.deviceId = deviceModels_all.get(position).id;
						sAndDDatabase.insert(model);
					}
				}
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
			if (tag.equals("permission_finish")) {
				finish();
			}
		}
	}

}
