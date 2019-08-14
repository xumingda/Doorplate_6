package com.yy.doorplate.activity;

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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.DeviceDatabase;
import com.yy.doorplate.database.SAndDDatabase;
import com.yy.doorplate.database.SceneDatabase;
import com.yy.doorplate.model.DeviceModel;
import com.yy.doorplate.model.SceneModel;
import com.yy.doorplate.protocol.SmartProtocol;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class ControlActivity extends Activity implements OnClickListener {

	private final String TAG = "ControlActivity";

	private MyApplication application;

	private Button btn_control_back, btn_scene, btn_device, btn_add;
	private HorizontalScrollView sv_control_scene;
	private LinearLayout ly_contron;

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
		setContentView(R.layout.activity_control);
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
		btn_control_back.setOnClickListener(this);
		btn_scene.setOnClickListener(this);
		btn_device.setOnClickListener(this);
		btn_add.setOnClickListener(this);
	}

	private void initData() {
		sceneModels = sceneDatabase.query_all();
		deviceModels = deviceDatabase.query_all();
		ly_contron.removeAllViews();
		button_dels.clear();
		if (sceneOrDevice == 1) {
			sv_control_scene.setPadding(35, 80, 35, 0);
			if (sceneModels != null && sceneModels.size() > 0) {
				for (int i = 0; i < sceneModels.size(); i++) {
					addScene(i);
				}
			} else {
				application.showToast("暂无场景，点击右上角添加场景");
			}
		} else if (sceneOrDevice == 2) {
			sv_control_scene.setPadding(100, 130, 100, 0);
			if (deviceModels != null && deviceModels.size() > 0) {
				for (int i = 0; i < deviceModels.size(); i++) {
					addDevice(i);
				}
			} else {
				application.showToast("暂无设备，点击右上角添加设备");
			}
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
			btn_scene.setTextColor(getResources().getColor(R.color.white));
			btn_scene.setBackgroundResource(R.drawable.btn_classinfo_click);
			btn_device.setTextColor(getResources().getColor(R.color.black));
			btn_device.setBackgroundResource(R.drawable.btn_banjiinfo);
			sv_control_scene.setPadding(35, 80, 35, 0);
			ly_contron.removeAllViews();
			button_dels.clear();
			sv_control_scene.scrollTo(0, 0);
			if (sceneModels != null && sceneModels.size() > 0) {
				for (int i = 0; i < sceneModels.size(); i++) {
					addScene(i);
				}
			} else {
				application.showToast("暂无场景，点击右上角添加场景");
			}
			break;
		case R.id.btn_device:
			sceneOrDevice = 2;
			btn_scene.setTextColor(getResources().getColor(R.color.black));
			btn_scene.setBackgroundResource(R.drawable.btn_classinfo);
			btn_device.setTextColor(getResources().getColor(R.color.white));
			btn_device.setBackgroundResource(R.drawable.btn_banjiinfo_click);
			sv_control_scene.setPadding(100, 130, 100, 0);
			ly_contron.removeAllViews();
			button_dels.clear();
			sv_control_scene.scrollTo(0, 0);
			if (deviceModels != null && deviceModels.size() > 0) {
				for (int i = 0; i < deviceModels.size(); i++) {
					addDevice(i);
				}
			} else {
				application.showToast("暂无设备，点击右上角添加设备");
			}
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
		// relativeLayout.addView(button_info, params2);

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
				Intent intent = new Intent(ControlActivity.this,
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

	private void addDevice(final int i) {
		final DeviceModel model = deviceModels.get(i);

		RelativeLayout relativeLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				250, 250);
		params2.topMargin = 20;
		final Button button1 = new Button(this);
		OnImageLoaderListener listener = new OnImageLoaderListener() {

			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				button1.setBackground(new BitmapDrawable(bitmap));
			}
		};
		button1.setPadding(0, 0, 0, 50);
		switch (model.img) {
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
		button1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		button1.setTextSize(40);
		button1.setTextColor(getResources().getColor(R.color.white));
		button1.setText(model.name);
		relativeLayout.addView(button1, params2);

		params2 = new RelativeLayout.LayoutParams(250, 250);
		params2.topMargin = 310;
		final Button button2 = new Button(this);
		listener = new OnImageLoaderListener() {

			@Override
			public void onImageLoader(Bitmap bitmap, String url) {
				button2.setBackground(new BitmapDrawable(bitmap));
			}
		};
		button2.setPadding(0, 0, 0, 50);
		switch (model.img) {
		case 1:
			imageLoader.getBitmapFormRes(R.drawable.device_1_off, listener);
			break;
		case 2:
			imageLoader.getBitmapFormRes(R.drawable.device_2_off, listener);
			break;
		case 3:
			imageLoader.getBitmapFormRes(R.drawable.device_3_off, listener);
			break;
		case 4:
			imageLoader.getBitmapFormRes(R.drawable.device_4_off, listener);
			break;
		case 5:
			imageLoader.getBitmapFormRes(R.drawable.device_5_off, listener);
			break;
		case 6:
			imageLoader.getBitmapFormRes(R.drawable.device_6_off, listener);
			break;
		case 7:
			imageLoader.getBitmapFormRes(R.drawable.device_7_off, listener);
			break;
		case 8:
			imageLoader.getBitmapFormRes(R.drawable.device_8_off, listener);
			break;
		default:
			imageLoader.getBitmapFormRes(R.drawable.device_9_off, listener);
			break;
		}
		button2.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		button2.setTextSize(40);
		button2.setTextColor(getResources().getColor(R.color.white));
		button2.setText(model.name);
		relativeLayout.addView(button2, params2);

		params2 = new RelativeLayout.LayoutParams(60, 60);
		final Button button_del = new Button(this);
		button_del.setBackgroundResource(R.drawable.btn_del);
		button_del.setVisibility(View.INVISIBLE);
		params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		relativeLayout.addView(button_del, params2);
		button_dels.add(button_del);

		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (model.id != 1) {
					application.smartUtils.deviceCtrl((byte) model.id,
							SmartProtocol.EQU_ON);
					startMediaPlayer();
				} else {
					application.smartUtils.openDoor(ControlActivity.this, 1500);
					startMediaPlayer();
				}
			}
		});
		button1.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				if (model.id != 1) {
					button_del.setVisibility(View.VISIBLE);
					application.smartUtils.deviceSet((byte) model.id,
							SmartProtocol.EQU_ON);
					application.showToast("进入学习模式");
					startMediaPlayer();
					is = true;
				}
				return true;
			}
		});
		button1.setOnTouchListener(new OnTouchListener() {

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
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (model.id != 1) {
					application.smartUtils.deviceCtrl((byte) model.id,
							SmartProtocol.EQU_OFF);
					startMediaPlayer();
				} else {
					application.smartUtils.closeDoor(ControlActivity.this);
					startMediaPlayer();
				}
			}
		});
		button2.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				if (model.id != 1) {
					button_del.setVisibility(View.VISIBLE);
					application.smartUtils.deviceSet((byte) model.id,
							SmartProtocol.EQU_OFF);
					application.showToast("进入学习模式");
					startMediaPlayer();
					is = true;
				}
				return true;
			}
		});
		button2.setOnTouchListener(new OnTouchListener() {

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
					Log.d(TAG, "button_del onTouch --- ACTION_DOWN");
					deviceDatabase.delete(model.id);
					sAndDDatabase.delete_by_deviceId(model.id);
					button_dels.remove(i);
					initData();
					break;
				case KeyEvent.ACTION_UP:
					Log.d(TAG, "button_del onTouch --- ACTION_UP");
					break;
				}
				return false;
			}
		});
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(270,
				LayoutParams.WRAP_CONTENT);
		if (ly_contron.getChildCount() == 0) {
			ly_contron.addView(relativeLayout, params);
		} else {
			params.leftMargin = 20;
			ly_contron.addView(relativeLayout, params);
		}
	}

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
