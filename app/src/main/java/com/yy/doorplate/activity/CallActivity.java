package com.yy.doorplate.activity;

import java.util.Timer;
import java.util.TimerTask;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.network.NetworkProtocol;
import com.network.NetworkState;
import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.communication.HttpProcess;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class CallActivity extends Activity {

	private final String TAG = "CallActivity";

	private MyApplication application;

	private RelativeLayout ly_call_local, ly_call_remote;
	private LinearLayout ly_call_btn;
	private TextView txt_call_from, txt_call_time, txt_call_title,
			txt_call_wait, txt_call_local, txt_call_remote;
	private Button btn_call, btn_handup;

	private boolean isCall;

	private Timer timer_time = null;
	private int count_time = 0;

	private ImageLoader imageLoader;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplication();

		application.handler_touch.removeMessages(0);

		imageLoader = new ImageLoader(application);

		if (application.call_eq == null) {
			finish();
			return;
		}

		application.openVoice();

		application.setHandler_call(handler_call);

		isCall = getIntent().getBooleanExtra("isCall", false);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();

		application.mNetworkManager.setVideoTxRx(true, true);
		application.mNetworkManager
				.setLayoutView(ly_call_local, ly_call_remote);
		if (application.call_eq.equType.equals("CONTROL_CENTER")) {
			application.mNetworkManager.enableRecord(application.call_eq.ip);
		} else {
			application.mNetworkManager.disableRecord();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		application.setHandler_call(null);
		application.call_eq = null;

		application.closeVoice();

		imageLoader.cancelTask();
		imageLoader.clearCache();

		if (broadcastManager != null)
			broadcastManager.unregisterReceiver(myBroadcastReceiver);

		// yy add
		// handler_call.removeMessages(10);
		application.mNetworkManager.closeCamera();
	}

	private void initView() {
		ly_call_local = (RelativeLayout) findViewById(R.id.ly_call_local);
		ly_call_remote = (RelativeLayout) findViewById(R.id.ly_call_remote);
		ly_call_btn = (LinearLayout) findViewById(R.id.ly_call_btn);
		txt_call_from = (TextView) findViewById(R.id.txt_call_from);
		txt_call_time = (TextView) findViewById(R.id.txt_call_time);
		txt_call_title = (TextView) findViewById(R.id.txt_call_title);
		txt_call_wait = (TextView) findViewById(R.id.txt_call_wait);
		txt_call_local = (TextView) findViewById(R.id.txt_call_local);
		txt_call_remote = (TextView) findViewById(R.id.txt_call_remote);

		imageLoader.getBitmapFormRes(R.drawable.ly_call_wait,
				new OnImageLoaderListener() {
					@Override
					public void onImageLoader(Bitmap bitmap, String url) {
						txt_call_wait.setBackground(new BitmapDrawable(bitmap));
					}
				});

		btn_call = new Button(this);
		btn_call.setBackgroundResource(R.drawable.btn_call);
		btn_call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				application.mediaPlayer.stop();
				application.mNetworkManager.startCall(null, 0, (byte) 0, 0);
			}
		});
		btn_handup = new Button(this);
		btn_handup.setBackgroundResource(R.drawable.btn_handup);
		btn_handup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				application.mediaPlayer.stop();
				application.setHandler_call(null);
				application.mNetworkManager.stopCall();
				if (timer_time != null) {
					count_time = 0;
					timer_time.cancel();
					timer_time = null;
				}
				finish();
			}
		});

		txt_call_title.setText("连接" + application.call_eq.jssysmc + "对讲中");
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
				140, 140);
		if (isCall) {
			application.mNetworkManager.startCall(application.call_eq.ip, 0,
					NetworkProtocol.DATA_CALL_NORMAL, 0);
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					application.mNetworkManager
							.sendCard(application.call_from_who);
				}
			}, 1000);
			ly_call_btn.addView(btn_handup, linearParams);
			txt_call_wait.setText("正在呼叫中，请耐心等待...");

			// yy add
			ly_call_local.setVisibility(View.VISIBLE);
			ly_call_remote.setVisibility(View.VISIBLE);
			txt_call_local.setVisibility(View.VISIBLE);
			txt_call_remote.setVisibility(View.VISIBLE);
			txt_call_wait.setVisibility(View.INVISIBLE);
			txt_call_local.setText(application.equInfoModel.jssysmc);
			txt_call_remote.setText(application.call_eq.jssysmc);
			linearParams = new LinearLayout.LayoutParams(140, 140);
			ly_call_btn.removeAllViews();
			ly_call_btn.addView(btn_handup, linearParams);
			application.mNetworkManager.startPreview(ly_call_local);
			// handler_call.sendEmptyMessageDelayed(10, 30 * 1000);
			// startMediaPlayer("onhook.mp3");
		} else {
			ly_call_btn.addView(btn_call, linearParams);
			linearParams.leftMargin = 25;
			ly_call_btn.addView(btn_handup, linearParams);
			txt_call_wait.setText("等待接听...");
			startMediaPlayer("ring1.wav");
		}
	}

	private Handler handler_call = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -99: {
				finish();
				break;
			}
			case 100: {
				txt_call_time.setText("通话时长:" + msg.obj.toString());
				break;
			}
			case 99: {
				application.httpProcess.QryPerson(
						application.equInfoModel.jssysdm, msg.obj.toString());
				break;
			}
			case NetworkState.STATE_NETWORK_DISABLED: {
				Log.i(TAG, "网络不可用");
				endThisActivity("网络异常");
				break;
			}
			case NetworkState.STATE_NETWORK_ENABLED: {
				Log.i(TAG, "网络可用");
				endThisActivity("由于长时间未接听，通话结束");
				break;
			}
			case NetworkState.STATE_CALL_ED: {
				Log.i(TAG, "被叫");
				break;
			}
			case NetworkState.STATE_CALL_IN: {
				Log.i(TAG, "通话中");
				answer(application.call_eq.jssysmc + " 通话中");
				break;
			}
			case NetworkState.STATE_CALL_END: {
				Log.i(TAG, "通话结束");
				endThisActivity("通话结束");
				break;
			}
			case NetworkState.STATE_CALL_WAIT: {
				Log.i(TAG, "主动呼叫，等待对方应答");
				startMediaPlayer("onhook.mp3");
				break;
			}
			case NetworkState.STATE_CALL_BUSY: {
				Log.i(TAG, "对方返回应答，线路繁忙");
				endThisActivity("线路繁忙");
				break;
			}
			case NetworkState.STATE_CALL_REFUSE: {
				Log.i(TAG, "对方返回应答，拒绝接听");
				endThisActivity("对方拒绝接听");
				break;
			}
			case NetworkState.STATE_CALL_CANNOT: {
				Log.i(TAG, "对方返回应答，无法接听");
				endThisActivity("对方无法接听");
				break;
			}
			case NetworkState.STATE_CALL_ERROR: {
				Log.i(TAG, "呼叫异常");
				// yy add
				endThisActivity("呼叫异常");
				break;
			}
			case NetworkState.STATE_TIMEOUT: {
				Log.i(TAG, "通讯超时");
				endThisActivity("通讯超时");
				break;
			}
			case NetworkState.STATE_CALL_IN_MONITOR: {
				Log.i(TAG, "监控通话中");
				break;
			}
			}
		}
	};

	private void answer(String msg) {
		ly_call_local.setVisibility(View.VISIBLE);
		ly_call_remote.setVisibility(View.VISIBLE);
		txt_call_local.setVisibility(View.VISIBLE);
		txt_call_remote.setVisibility(View.VISIBLE);
		txt_call_wait.setVisibility(View.INVISIBLE);
		txt_call_local.setText(application.equInfoModel.jssysmc);
		txt_call_remote.setText(application.call_eq.jssysmc);
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
				140, 140);
		ly_call_btn.removeAllViews();
		ly_call_btn.addView(btn_handup, linearParams);

		application.mediaPlayer.stop();

		if (timer_time == null) {
			timer_time = new Timer();
			timer_time.schedule(new TimerTask() {
				@Override
				public void run() {
					int minute = count_time / 60;
					int second = count_time % 60;
					Message msg = Message.obtain();
					msg.what = 100;
					if (minute < 10 && second < 10) {
						msg.obj = "0" + minute + ":" + "0" + second;
					} else if (minute >= 10 && second >= 10) {
						msg.obj = minute + ":" + second;
					} else if (minute >= 10 && second < 10) {
						msg.obj = minute + ":" + "0" + second;
					} else if (minute < 10 && second >= 10) {
						msg.obj = "0" + minute + ":" + second;
					}
					handler_call.sendMessage(msg);
					count_time++;
				}
			}, 0, 1000);
		}
	}

	private void endThisActivity(String msg) {
		application.mediaPlayer.stop();
		ly_call_local.setVisibility(View.INVISIBLE);
		ly_call_remote.setVisibility(View.INVISIBLE);
		txt_call_local.setVisibility(View.INVISIBLE);
		txt_call_remote.setVisibility(View.INVISIBLE);
		txt_call_wait.setVisibility(View.VISIBLE);
		txt_call_wait.setText(msg);
		txt_call_time.setText("");
		ly_call_btn.removeAllViews();
		handler_call.sendEmptyMessageDelayed(-99, 1000);
		if (timer_time != null) {
			count_time = 0;
			timer_time.cancel();
			timer_time = null;
		}
	}

	private void startMediaPlayer(String path) {
		try {
			application.mediaPlayer.stop();
			application.mediaPlayer.reset();
			application.mediaPlayer
					.setAudioStreamType(AudioManager.STREAM_MUSIC);
			AssetFileDescriptor descriptor = getAssets().openFd(path);
			application.mediaPlayer.setDataSource(
					descriptor.getFileDescriptor(),
					descriptor.getStartOffset(), descriptor.getLength());
			application.mediaPlayer.setDisplay(null);
			application.mediaPlayer.setLooping(true);
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
			if (tag.equals(HttpProcess.QRY_PERSON)) {
				boolean result = intent.getBooleanExtra(
						MyApplication.CMD_RESULT, false);
				String msg = intent.getStringExtra(MyApplication.CMD_MSG);
				if (result) {
					Log.d(TAG, tag + " " + msg);
					String xm = intent.getStringExtra("xm");
					String js = intent.getStringExtra("js");
					String rybh = intent.getStringExtra("rybh");
					String xsbj = intent.getStringExtra("xsbj");
					String jsbm = intent.getStringExtra("jsbm");
					if (js.equals("1")) {
						txt_call_from.setText("操作人员:教师 " + jsbm + xm);
					} else if (js.equals("2")) {
						txt_call_from.setText("操作人员:学生 " + xsbj + xm);
					} else {
						txt_call_from.setText("操作人员:" + xm);
					}
				} else {
					Log.e(TAG, tag + " " + msg);
				}
			}
		}
	}
}
