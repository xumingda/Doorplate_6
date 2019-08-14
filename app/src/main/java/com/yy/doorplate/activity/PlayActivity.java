package com.yy.doorplate.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.yy.doorplate.MyApplication;
import com.yy.doorplate.R;
import com.yy.doorplate.database.PlayTaskDatabase;
import com.yy.doorplate.model.PlayTaskModel;
import com.yy.doorplate.tool.ImageLoader;
import com.yy.doorplate.tool.ImageLoader.OnImageLoaderListener;

public class PlayActivity extends Activity implements OnClickListener {

	private final String TAG = "PlayActivity";

	private MyApplication application;

	private ImageSwitcher img_play;
	private SurfaceView mSurfaceView;
	private SurfaceHolder surfaceHolder;
	private Button btn_volume;
	private View control, mBottomView;
	private TextView total_time, cur_time;
	private ImageButton play;
	private SeekBar playback_seeker;

	private LocalBroadcastManager broadcastManager;
	private MyBroadcastReceiver myBroadcastReceiver;

	private String path, type;

	private ImageLoader imageLoader;

	private List<PlayTaskModel> list = null;
	private int list_i = 0;

	private boolean isStopTrackingTouch = true;

	private Timer timerSeekBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		application = (MyApplication) getApplicationContext();

		application.handler_touch.removeMessages(0);

		path = getIntent().getStringExtra("path");
		type = getIntent().getStringExtra("type");

		imageLoader = new ImageLoader(application);

		broadcastManager = LocalBroadcastManager.getInstance(this);
		myBroadcastReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(MyApplication.BROADCAST);
		broadcastManager.registerReceiver(myBroadcastReceiver, filter);

		initView();
	}

	private void initView() {
		img_play = (ImageSwitcher) findViewById(R.id.img_play);
		mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
		btn_volume = (Button) findViewById(R.id.btn_volume);
		control = findViewById(R.id.control);
		mBottomView = findViewById(R.id.bottomView);
		total_time = (TextView) findViewById(R.id.cur_time);
		cur_time = (TextView) findViewById(R.id.total_time);
		play = (ImageButton) findViewById(R.id.play);
		playback_seeker = (SeekBar) findViewById(R.id.playback_seeker);

		img_play.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				ImageView imageView = new ImageView(PlayActivity.this);
				ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
						ImageSwitcher.LayoutParams.MATCH_PARENT,
						ImageSwitcher.LayoutParams.MATCH_PARENT);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setLayoutParams(params);
				return imageView;
			}
		});
		img_play.setInAnimation(AnimationUtils.loadAnimation(this,
				R.anim.enteralpha));
		img_play.setOutAnimation(AnimationUtils.loadAnimation(this,
				R.anim.exitalpha));

		img_play.setOnClickListener(this);
		mSurfaceView.setOnClickListener(this);
		btn_volume.setOnClickListener(this);
		play.setOnClickListener(this);
		mBottomView.setOnClickListener(this);
		playback_seeker
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekBar seekbar) {
						isStopTrackingTouch = true;
						application.mediaPlayer.seekTo(seekbar.getProgress());
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						isStopTrackingTouch = false;
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

					}
				});

		mSurfaceView.getHolder().setType(
				SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceView.getHolder().setKeepScreenOn(true);
		mSurfaceView.getHolder().addCallback(new surFaceView());
		surfaceHolder = mSurfaceView.getHolder();
		if (application.isGuanggao && type.equals("IMAGE")) {
			btn_volume.setVisibility(View.INVISIBLE);
			img_play.setVisibility(View.VISIBLE);
			imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if (bitmap != null) {
						img_play.setImageDrawable(new BitmapDrawable(bitmap));
					} else {
						finish();
					}
				}
			}, 1920, 1080);
		} else if (application.isGuanggao
				&& (type.equals("VIDEO") || type.equals("VIDEO_EXAMPLE"))) {
			btn_volume.setVisibility(View.VISIBLE);
			mSurfaceView.setVisibility(View.VISIBLE);
			control.setVisibility(View.VISIBLE);
		} else if (!application.isGuanggao) {
			btn_volume.setVisibility(View.INVISIBLE);
			img_play.setVisibility(View.VISIBLE);
			PlayTaskDatabase database = new PlayTaskDatabase();
			list = database.query_by_playType("SCREEN_SAVER");
			Log.d(TAG, "SCREEN_SAVERSCREEN_SAVERSCREEN_SAVERSCREEN_SAVER");
			if (list != null && list.size() > 0) {
				list_i = 0;
				handler.sendEmptyMessage(0);
			} else {
				finish();
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String[] s = list.get(list_i).srcPath.split("/");
			final String path = MyApplication.PATH_ROOT
					+ MyApplication.PATH_PLAYTASK + "/" + s[s.length - 1];
			Log.d(TAG, path);
			imageLoader.getBitmapFormUrl(path, new OnImageLoaderListener() {

				@Override
				public void onImageLoader(Bitmap bitmap, String url) {
					if (bitmap != null) {
						img_play.setImageDrawable(new BitmapDrawable(bitmap));
					}
					if (list.size() > 1) {
						if (list_i == list.size() - 1) {
							list_i = 0;
						} else {
							list_i++;
						}
						handler.sendEmptyMessageDelayed(0, 10 * 1000);
					}
				}
			}, 1920, 1080);
		};
	};

	private class surFaceView implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			play();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!intent.getAction().equals(MyApplication.BROADCAST)) {
				return;
			}
			String tag = intent.getStringExtra(MyApplication.BROADCAST_TAG);
			if (tag.equals("PlayActivity_finish")) {
				finish();
			}
		}
	}

	private void play() {
		try {
			application.mediaPlayer.stop();
			application.mediaPlayer.reset();
			application.mediaPlayer.release();
			application.mediaPlayer = new MediaPlayer();
			application.mediaPlayer
					.setAudioStreamType(AudioManager.STREAM_MUSIC);
			application.mediaPlayer.setDataSource(path);
			application.mediaPlayer.setDisplay(surfaceHolder);
			application.mediaPlayer.setLooping(false);
			application.mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					finish();
					return false;
				}
			});
			application.mediaPlayer
					.setOnPreparedListener(new OnPreparedListener() {

						@Override
						public void onPrepared(MediaPlayer mediaPlayer) {
							Log.d(TAG, "downLoad Play start");
							application.openVoice();
							mHandler.removeMessages(0);
							mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
							playback_seeker.setMax(mediaPlayer.getDuration());
							play.setBackgroundResource(R.drawable.btn_pause_n);
							timerSeekBar = new Timer();
							timerSeekBar.schedule(new TimerTask() {
								@Override
								public void run() {
									mHandler.sendEmptyMessage(1);
								}
							}, 10, 1000);
							mediaPlayer.start();
						}
					});
			application.mediaPlayer
					.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mediaPlayer) {
							if (type.equals("VIDEO_EXAMPLE")) {
								finish();
							} else if (type.equals("VIDEO")) {
								play();
							}
						}
					});
			application.mediaPlayer
					.setOnSeekCompleteListener(new OnSeekCompleteListener() {

						@Override
						public void onSeekComplete(MediaPlayer mediaPlayer) {
							if (mediaPlayer != null && mediaPlayer.isPlaying()) {
								int value = mediaPlayer.getCurrentPosition();
								playback_seeker.setProgress(value);
								int a = value / 1000 / 60;
								int b = value / 1000 % 60;
								if (a < 10 && b < 10)
									cur_time.setText("0" + a + ":" + "0" + b);
								else if (a < 10 && b >= 10)
									cur_time.setText("0" + a + ":" + b);
								else if (a >= 10 && b < 10)
									cur_time.setText(a + ":" + "0" + b);
								else
									cur_time.setText(a + ":" + b);
							}
						}
					});
			application.mediaPlayer.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeMessages(0);
		if (application.mediaPlayer != null
				&& application.mediaPlayer.isPlaying()) {
			application.mediaPlayer.stop();
			application.mediaPlayer.reset();
		}
		if (timerSeekBar != null) {
			timerSeekBar.cancel();
			timerSeekBar = null;
		}
		application.closeVoice();
		imageLoader.cancelTask();
		imageLoader.clearCache();
		application.isGuanggao = false;
		broadcastManager.unregisterReceiver(myBroadcastReceiver);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.img_play:
			finish();
			break;
		case R.id.mSurfaceView:
			if (mBottomView.getVisibility() == View.VISIBLE) {
				finish();
			} else {
				control.setVisibility(View.VISIBLE);
				mBottomView.setVisibility(View.VISIBLE);
				mHandler.removeMessages(0);
				mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
			}
			break;
		case R.id.btn_volume: {
			if (application.isSilent) {
				btn_volume.setBackgroundResource(R.drawable.img_volume);
				application.isSilent = false;
				// application.mAudioManager.setStreamMute(
				// AudioManager.STREAM_MUSIC, true);
				application.mAudioManager.setStreamVolume(
						AudioManager.STREAM_MUSIC, application.volume, 0);
			} else {
				btn_volume.setBackgroundResource(R.drawable.img_volume_no);
				application.isSilent = true;
				// application.mAudioManager.setStreamMute(
				// AudioManager.STREAM_MUSIC, true);
				application.mAudioManager.setStreamVolume(
						AudioManager.STREAM_MUSIC, 0, 0);
			}
			break;
		}
		case R.id.play: {
			if (application.mediaPlayer != null
					&& application.mediaPlayer.isPlaying()) {
				application.mediaPlayer.pause();
				play.setBackgroundResource(R.drawable.btn_play_n);
				control.setVisibility(View.VISIBLE);
				mBottomView.setVisibility(View.VISIBLE);
				mHandler.removeMessages(0);
				mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
			} else if (application.mediaPlayer != null
					&& !application.mediaPlayer.isPlaying()) {
				application.mediaPlayer.start();
				play.setBackgroundResource(R.drawable.btn_pause_n);
				control.setVisibility(View.VISIBLE);
				mBottomView.setVisibility(View.VISIBLE);
				mHandler.removeMessages(0);
				mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
			}
			break;
		}
		case R.id.bottomView:
			mHandler.removeMessages(0);
			mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				TranslateAnimation mHideDownTransformation = new TranslateAnimation(
						0.0f, 0.0f, 0.0f, 200.0f);
				mHideDownTransformation.setDuration(1000);
				mBottomView.startAnimation(mHideDownTransformation);
				mBottomView.setVisibility(View.GONE);
			} else if (msg.what == 1) {
				if (application.mediaPlayer != null
						&& application.mediaPlayer.isPlaying()
						&& isStopTrackingTouch) {
					int position = application.mediaPlayer.getCurrentPosition();
					int mMax = application.mediaPlayer.getDuration();
					if (mMax <= 0) {
						return;
					}
					playback_seeker.setProgress(position);
					int a = position / 1000 / 60;
					int b = position / 1000 % 60;
					int c = mMax / 1000 / 60;
					int d = mMax / 1000 % 60;
					if (a < 10 && b < 10)
						cur_time.setText("0" + a + ":" + "0" + b);
					else if (a < 10 && b >= 10)
						cur_time.setText("0" + a + ":" + b);
					else if (a >= 10 && b < 10)
						cur_time.setText(a + ":" + "0" + b);
					else
						cur_time.setText(a + ":" + b);
					if (c < 10 && d < 10)
						total_time.setText("0" + c + ":" + "0" + d);
					else if (c < 10 && d >= 10)
						total_time.setText("0" + c + ":" + d);
					else if (c >= 10 && d < 10)
						total_time.setText(c + ":" + "0" + d);
					else
						total_time.setText(c + ":" + d);
				}
			}
		}
	};
}
