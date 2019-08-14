package com.cx.doorplate.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.activity.AddSceneActivity;
import com.yy.doorplate.activity.SceneInfoActivity;
import com.yy.doorplate.database.DeviceDatabase;
import com.yy.doorplate.database.SAndDDatabase;
import com.yy.doorplate.database.SceneDatabase;
import com.yy.doorplate.model.DeviceModel;
import com.yy.doorplate.model.SceneModel;
import com.yy.doorplate.protocol.SmartProtocol;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class CXControlActivity extends Activity implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {
	private final String TAG = "CXControlActivity";

	private MyApplication application;

	private Button btn_control_back, btn_scene, btn_device, btn_add;
	private HorizontalScrollView sv_control_scene;
	private LinearLayout ly_contron;
	private RelativeLayout ly_contron_device;
	private GridView gv_contron_device;

	private int sceneOrDevice = 1;

	private List<Button> button_dels = new ArrayList<Button>();

	private SceneDatabase sceneDatabase = new SceneDatabase();
	private DeviceDatabase deviceDatabase = new DeviceDatabase();
	private SAndDDatabase sAndDDatabase = new SAndDDatabase();
	private List<SceneModel> sceneModels;
	private List<DeviceModel> deviceModels;

	private ImageLoader imageLoader;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private boolean is = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cx_activity_control);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		initData();
		application.handler_touch.removeMessages(0);
		application.handler_touch.sendEmptyMessageDelayed(0,
				application.screensaver_time * 1000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		imageLoader.cancelTask();
		imageLoader.clearCache();
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case KeyEvent.ACTION_DOWN:
			application.handler_touch.removeMessages(0);
			break;
		case KeyEvent.ACTION_UP:
			application.handler_touch.sendEmptyMessageDelayed(0,
					application.screensaver_time * 1000);
			if (is) {
				is = false;
				return super.dispatchTouchEvent(event);
			}
			if (button_dels != null && button_dels.size() > 0) {
				for (Button button : button_dels) {
					if (button != null
							&& button.getVisibility() == View.VISIBLE) {
						button.setVisibility(View.INVISIBLE);
					}
				}
			}
			if (deviceModels != null && deviceModels.size() > 0) {
				for (int i = 0; i < deviceModels.size(); i++) {
					ImageView imageView = (ImageView) gv_contron_device
							.findViewWithTag("ImageView" + i);
					if (imageView != null) {
						imageView.setVisibility(View.INVISIBLE);
					}
				}
			}
			break;
		}
		return super.dispatchTouchEvent(event);
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

	private void initView() {
		btn_control_back = (Button) findViewById(R.id.btn_control_back);
		btn_scene = (Button) findViewById(R.id.btn_scene);
		btn_device = (Button) findViewById(R.id.btn_device);
		btn_add = (Button) findViewById(R.id.btn_add);
		sv_control_scene = (HorizontalScrollView) findViewById(R.id.sv_control_scene);
		ly_contron = (LinearLayout) findViewById(R.id.ly_contron);
		ly_contron_device = (RelativeLayout) findViewById(R.id.ly_contron_device);
		gv_contron_device = (GridView) findViewById(R.id.gv_contron_device);
		btn_control_back.setOnClickListener(this);
		btn_scene.setOnClickListener(this);
		btn_device.setOnClickListener(this);
		btn_add.setOnClickListener(this);
		gv_contron_device.setOnItemClickListener(this);
		gv_contron_device.setOnItemLongClickListener(this);
	}

	private void initData() {
		sceneModels = sceneDatabase.query_all();
		deviceModels = deviceDatabase.query_all();
		ly_contron.removeAllViews();
		button_dels.clear();
		if (sceneModels != null && sceneModels.size() > 0) {
			for (int i = 0; i < sceneModels.size(); i++) {
				addScene(i);
			}
		} else {
			application.showToast("暂无场景，点击右上角添加场景");
		}
		if (deviceModels != null && deviceModels.size() > 0) {
			MyAdapter adapter = new MyAdapter(deviceModels);
			gv_contron_device.setAdapter(adapter);
		} else {
			application.showToast("暂无设备，点击右上角添加设备");
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (deviceModels.get(arg2).id != 1) {
			application.smartUtils.deviceSet((byte) deviceModels.get(arg2).id,
					SmartProtocol.EQU_ON);
			application.showToast("进入学习模式");
			startMediaPlayer();

			if (deviceModels != null && deviceModels.size() > 0) {
				for (int i = 0; i < deviceModels.size(); i++) {
					ImageView imageView = (ImageView) gv_contron_device
							.findViewWithTag("ImageView" + i);
					if (imageView != null) {
						if (i == arg2) {
							imageView.setVisibility(View.VISIBLE);
							is = true;
						} else
							imageView.setVisibility(View.INVISIBLE);
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (deviceModels.get(arg2).id == 1) {
			application.smartUtils.openDoor(CXControlActivity.this, 1500);
			startMediaPlayer();
		} else {
			if (deviceModels.get(arg2).state == 0) {
				application.smartUtils.deviceCtrl(
						(byte) deviceModels.get(arg2).id, SmartProtocol.EQU_ON);
				deviceModels.get(arg2).state = 1;
			} else {
				application.smartUtils
						.deviceCtrl((byte) deviceModels.get(arg2).id,
								SmartProtocol.EQU_OFF);
				deviceModels.get(arg2).state = 0;
			}
			deviceDatabase.update(deviceModels.get(arg2));
			TextView textView = (TextView) gv_contron_device
					.findViewWithTag("TextView" + arg2);
			ImageView imageView = (ImageView) gv_contron_device
					.findViewWithTag("ImageView" + arg2);
			if (imageView != null
					&& imageView.getVisibility() == View.INVISIBLE) {
				updataItemView(textView, deviceModels.get(arg2));
				startMediaPlayer();
			}
		}
	}

	private void updataItemView(TextView textView, DeviceModel model) {
		switch (model.img) {
		case 1:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_1_on);
			else
				textView.setBackgroundResource(R.drawable.device_1_off);
			break;
		case 2:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_2_on);
			else
				textView.setBackgroundResource(R.drawable.device_2_off);
			break;
		case 3:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_3_on);
			else
				textView.setBackgroundResource(R.drawable.device_3_off);
			break;
		case 4:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_4_on);
			else
				textView.setBackgroundResource(R.drawable.device_4_off);
			break;
		case 5:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_5_on);
			else
				textView.setBackgroundResource(R.drawable.device_5_off);
			break;
		case 6:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_6_on);
			else
				textView.setBackgroundResource(R.drawable.device_6_off);
			break;
		case 7:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_7_on);
			else
				textView.setBackgroundResource(R.drawable.device_7_off);
			break;
		case 8:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_8_on);
			else
				textView.setBackgroundResource(R.drawable.device_8_off);
			break;
		default:
			if (model.state == 0)
				textView.setBackgroundResource(R.drawable.device_9_on);
			else
				textView.setBackgroundResource(R.drawable.device_9_off);
			break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_control_back:
			finish();
			break;
		case R.id.btn_scene:
			sceneOrDevice = 1;
			sv_control_scene.setVisibility(View.VISIBLE);
			ly_contron_device.setVisibility(View.INVISIBLE);
			btn_scene.setTextColor(getResources().getColor(R.color.white));
			btn_scene.setBackgroundResource(R.drawable.btn_classinfo_click);
			btn_device.setTextColor(getResources().getColor(R.color.black));
			btn_device.setBackgroundResource(R.drawable.btn_banjiinfo);
			ly_contron.removeAllViews();
			button_dels.clear();
			if (sceneModels != null && sceneModels.size() > 0) {
				for (int i = 0; i < sceneModels.size(); i++) {
					addScene(i);
				}
			}
			// else {
			// application.showToast("暂无场景，点击右上角添加场景");
			// }
			break;
		case R.id.btn_device:
			sceneOrDevice = 2;
			sv_control_scene.setVisibility(View.INVISIBLE);
			ly_contron_device.setVisibility(View.VISIBLE);
			btn_scene.setTextColor(getResources().getColor(R.color.black));
			btn_scene.setBackgroundResource(R.drawable.btn_classinfo);
			btn_device.setTextColor(getResources().getColor(R.color.white));
			btn_device.setBackgroundResource(R.drawable.btn_banjiinfo_click);
			// if (deviceModels != null && deviceModels.size() > 0) {
			// MyAdapter adapter = new MyAdapter(deviceModels);
			// gv_contron_device.setAdapter(adapter);
			// } else {
			// application.showToast("暂无设备，点击右上角添加设备");
			// }
			break;
		case R.id.btn_add: {
			Intent intent = new Intent(this, AddSceneActivity.class);
			intent.putExtra("sceneOrDevice", sceneOrDevice);
			startActivity(intent);
			break;
		}
		}
	}

	private void addScene(final int i) {
		final SceneModel model = sceneModels.get(i);

		RelativeLayout relativeLayout = new RelativeLayout(this);

		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				338, 604);
		params2.topMargin = 20;
		final Button button = new Button(this);
		OnImageLoaderListener listener = new OnImageLoaderListener() {

			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				button.setBackground(new BitmapDrawable(bitmap));
			}
		};
		switch (model.img) {
		case 1:
			imageLoader.getBitmapFormRes(R.drawable.scene_1, listener);
			break;
		case 2:
			imageLoader.getBitmapFormRes(R.drawable.scene_2, listener);
			break;
		case 3:
			imageLoader.getBitmapFormRes(R.drawable.scene_3, listener);
			break;
		case 4:
			imageLoader.getBitmapFormRes(R.drawable.scene_4, listener);
			break;
		case 5:
			imageLoader.getBitmapFormRes(R.drawable.scene_5, listener);
			break;
		case 6:
			imageLoader.getBitmapFormRes(R.drawable.scene_6, listener);
			break;
		case 7:
			imageLoader.getBitmapFormRes(R.drawable.scene_7, listener);
			break;
		case 8:
			imageLoader.getBitmapFormRes(R.drawable.scene_8, listener);
			break;
		default:
			imageLoader.getBitmapFormRes(R.drawable.scene_9, listener);
			break;
		}
		button.setPadding(0, 0, 0, 90);
		button.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		button.setTextSize(90);
		button.setTextColor(getResources().getColor(R.color.white));
		button.setText(model.name);
		relativeLayout.addView(button, params2);

		params2 = new RelativeLayout.LayoutParams(60, 60);
		final Button button_del = new Button(this);
		button_del.setBackgroundResource(R.drawable.btn_del);
		button_del.setVisibility(View.INVISIBLE);
		params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		relativeLayout.addView(button_del, params2);
		button_dels.add(button_del);

		params2 = new RelativeLayout.LayoutParams(336, 77);
		params2.topMargin = 638;
		params2.leftMargin = 1;
		Button button_info = new Button(this);
		button_info.setBackgroundResource(R.drawable.btn_scene_info);
		relativeLayout.addView(button_info, params2);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				application.smartUtils.equCtrl((byte) model.id);
				startMediaPlayer();
			}
		});
		button.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				button_del.setVisibility(View.VISIBLE);
				application.smartUtils.equSet((byte) model.id);
				application.showToast("进入学习模式");
				startMediaPlayer();
				is = true;
				return true;
			}
		});
		button.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
				case KeyEvent.ACTION_DOWN:
					if (button_dels != null && button_dels.size() > 0) {
						for (Button button : button_dels) {
							if (button != null
									&& button.getVisibility() == View.VISIBLE) {
								button.setVisibility(View.INVISIBLE);
							}
						}
					}
					break;
				case KeyEvent.ACTION_UP:
					break;
				}
				return false;
			}
		});
		button_del.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
				case KeyEvent.ACTION_DOWN:
					sceneDatabase.delete(model.id);
					sAndDDatabase.delete_by_sceneId(model.id);
					button_dels.remove(i);
					initData();
					break;
				case KeyEvent.ACTION_UP:
					break;
				}
				return false;
			}
		});
		button_info.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(CXControlActivity.this,
						SceneInfoActivity.class);
				intent.putExtra("sceneId", model.id);
				startActivity(intent);
			}
		});

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(358,
				LayoutParams.WRAP_CONTENT);
		if (ly_contron.getChildCount() == 0) {
			ly_contron.addView(relativeLayout, params);
		} else {
			params.leftMargin = 15;
			ly_contron.addView(relativeLayout, params);
		}
	}

	private class MyAdapter extends BaseAdapter {

		private List<DeviceModel> list;

		public MyAdapter(List<DeviceModel> list) {
			this.list = list;
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
			private ImageView img_scene_info_del;
		}

		@Override
		public View getView(int position, View view, ViewGroup viewGroup) {
			ViewHolder viewHolder = null;
			if (viewHolder == null) {
				viewHolder = new ViewHolder();
				view = LayoutInflater.from(CXControlActivity.this).inflate(
						R.layout.item_sceneinfo, null);
				viewHolder.btn_scene_info = (TextView) view
						.findViewById(R.id.btn_scene_info);
				viewHolder.img_scene_info_del = (ImageView) view
						.findViewById(R.id.img_scene_info_del);
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
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_1_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_1_off,
							listener);
				break;
			case 2:
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_2_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_2_off,
							listener);
				break;
			case 3:
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_3_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_3_off,
							listener);
				break;
			case 4:
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_4_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_4_off,
							listener);
				break;
			case 5:
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_5_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_5_off,
							listener);
				break;
			case 6:
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_6_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_6_off,
							listener);
				break;
			case 7:
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_7_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_7_off,
							listener);
				break;
			case 8:
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_8_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_8_off,
							listener);
				break;
			default:
				if (list.get(position).state == 0)
					imageLoader.getBitmapFormRes(R.drawable.device_9_on,
							listener);
				else
					imageLoader.getBitmapFormRes(R.drawable.device_9_off,
							listener);
				break;
			}
			viewHolder.btn_scene_info.setTag("TextView" + position);
			viewHolder.img_scene_info_del.setTag("ImageView" + position);
			viewHolder.img_scene_info_del
					.setOnTouchListener(new OnTouchListener() {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							switch (event.getAction()) {
							case KeyEvent.ACTION_DOWN:
								Log.d(TAG, "button_del onTouch --- ACTION_DOWN");
								String tag = v.getTag().toString();
								tag = tag.replace("ImageView", "");
								int arg2 = Integer.parseInt(tag);
								deviceDatabase.delete(deviceModels.get(arg2).id);
								sAndDDatabase.delete_by_deviceId(deviceModels
										.get(arg2).id);
								handler.sendEmptyMessageDelayed(0, 100);
								break;
							}
							return false;
						}
					});
			return view;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				initData();
				break;
			}
		};
	};

	private void startMediaPlayer() {
		String path = "di.mp3";
		try {
			application.openVoice();
			application.mediaPlayer.stop();
			application.mediaPlayer.reset();
			AssetFileDescriptor descriptor = getAssets().openFd(path);
			application.mediaPlayer
					.setAudioStreamType(AudioManager.STREAM_MUSIC);
			application.mediaPlayer.setDataSource(
					descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			application.mediaPlayer.setDisplay(null);
			application.mediaPlayer.setLooping(false);
			application.mediaPlayer
					.setOnPreparedListener(new OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer mediaPlayer) {
							mediaPlayer.start();
						}
					});
			application.mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							application.closeVoice();
						}
					});
			application.mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
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
